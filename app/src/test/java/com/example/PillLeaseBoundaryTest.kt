package com.example

import android.content.Context
import android.graphics.Typeface
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.PillTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * The peek lease's boundary pack — census R1–R8 plus the
 * cooldown arithmetic and the theme's typeface pin, on #16
 * (comment 6536820385; cooldown gaps cross-checked against the
 * existing suites this run).
 *
 * Already covered elsewhere, deliberately NOT duplicated:
 *  - the default 120 s grant + pre-armed anchor, grant-zeroes-
 *    others, hand-back re-anchoring ([PillLeaseRepositoryTest]);
 *  - the 0-minute and unlisted-package REFUSALS writing nothing,
 *    the provider's clamp/default table, the happy-path control
 *    ([St08PeekRefusalTest]);
 *  - the cooldown's happy path — ~1 200 s immediately after a
 *    grant and after an early end ([ExampleRobolectricTest]).
 *
 * What this file adds is everything around those points: the
 * non-default grant priced end-to-end (§4.4 case 3), an edit's
 * mid-flight immovability (§4.4 case 4), hand-back's unspecified
 * edges, re-grant sequences, the expired-lease sweep, bystander
 * anchor preservation, the concurrent-grant invariant (C26),
 * and the cooldown function's own boundaries (no anchor, cap,
 * ceiling, expiry, max-anchor law).
 *
 * Hygiene follows the lease/ST-08 precedent: the sandbox
 * database is shared by the whole suite, so packages are unique
 * per test, this class's rows are deleted on the way out, and
 * the guard-settings singleton is restored EXACTLY after every
 * test that touches it.
 *
 * Written against the pill API as it exists on PR #231's train
 * (`batch-10`); parked on `agent/issue-16-test-matrix` for
 * post-train landing. DECLARED, uncompiled — no local compiling.
 *
 * — T24 Builder seat 4, #16 wave
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PillLeaseBoundaryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun database(): MonolithDatabase = MonolithDatabase.getInstance(context)

    private fun repository(dao: MonolithDao): MonolithRepository =
        MonolithRepository(context, dao)

    private fun app(
        pkg: String,
        peekUntil: Long = 0L,
        peekEnd: Long = 0L
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = pkg.substringAfterLast('.').uppercase(),
        category = "MESSAGING",
        isBlocked = true,
        blockTier = "PEEK_2M",
        activePeekUntilElapsedRealtime = peekUntil,
        lastPeekEndElapsedRealtime = peekEnd
    )

    /** Restore the settings singleton to exactly what the test
     *  found: the prior row verbatim, or no row at all (the
     *  ST-08 suite's precedent; the DAO offers no settings
     *  delete). */
    private suspend fun restoreSettings(
        db: MonolithDatabase,
        dao: MonolithDao,
        original: GuardSettingsEntity?
    ) {
        if (original == null) {
            db.openHelper.writableDatabase.execSQL(
                "DELETE FROM guard_settings WHERE id = 1"
            )
        } else {
            dao.insertGuardSettings(original)
        }
    }

    private suspend fun rowsWithPrefix(
        dao: MonolithDao,
        prefix: String
    ): List<BlockedAppEntity> =
        dao.getAllAppsSnapshot().filter { it.packageName.startsWith(prefix) }

    private suspend fun row(dao: MonolithDao, pkg: String): BlockedAppEntity =
        dao.getAllAppsSnapshot().first { it.packageName == pkg }

    // ------------------------------------------------------------------
    // R1 — §4.4 case 3, the non-default grant, end-to-end: with
    // peekMinutes = 5 the stored end is priced at 300 000 ms from
    // the grant instant, and the cooldown anchor is that end.
    // ------------------------------------------------------------------

    @Test
    fun nonDefaultGrantIsPricedFromTheSettingsRow() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val repo = repository(dao)
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 5, breakMinutes = 10)
            )
            dao.insertApp(app("com.pillb.five"))

            val before = SystemClock.elapsedRealtime()
            val granted = repo.startPeekLease("com.pillb.five")
            val after = SystemClock.elapsedRealtime()

            assertTrue("a listed package at peekMinutes = 5 must grant", granted)
            val grantedRow = row(dao, "com.pillb.five")
            assertTrue(
                "stored end is the grant instant + 300 000 ms " +
                    "(end=${grantedRow.activePeekUntilElapsedRealtime}, " +
                    "window=${before + 300_000L}..${after + 300_000L})",
                grantedRow.activePeekUntilElapsedRealtime in
                    (before + 300_000L)..(after + 300_000L)
            )
            assertEquals(
                "cooldown anchor pre-armed to the non-default end",
                grantedRow.activePeekUntilElapsedRealtime,
                grantedRow.lastPeekEndElapsedRealtime
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.five"))
            restoreSettings(db, dao, originalSettings)
        }
    }

    // ------------------------------------------------------------------
    // R2 — §4.4 case 4, mid-flight immovability: an edit after the
    // grant must not move the live lease by a single millisecond;
    // the same edit prices the NEXT grant in full. The mid-flight
    // law (§3.4) is structural — this is its first test.
    // ------------------------------------------------------------------

    @Test
    fun settingsEditNeverRepricesALiveLeaseButPricesTheNextGrant() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val repo = repository(dao)
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 2, breakMinutes = 10)
            )
            dao.insertApp(app("com.pillb.flight"))

            assertTrue(repo.startPeekLease("com.pillb.flight"))
            val grantedRow = row(dao, "com.pillb.flight")

            // The edit lands while the lease is live.
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 30, breakMinutes = 10)
            )

            val afterEdit = row(dao, "com.pillb.flight")
            assertEquals(
                "a live lease's end is bit-identical after the edit",
                grantedRow.activePeekUntilElapsedRealtime,
                afterEdit.activePeekUntilElapsedRealtime
            )
            assertEquals(
                "…and its cooldown anchor is bit-identical too",
                grantedRow.lastPeekEndElapsedRealtime,
                afterEdit.lastPeekEndElapsedRealtime
            )

            // Hand back, re-grant: the new lease is priced at the
            // edited 30 minutes — the edit prices the future only.
            repo.recordPeekEnded("com.pillb.flight")
            val before = SystemClock.elapsedRealtime()
            assertTrue(repo.startPeekLease("com.pillb.flight"))
            val after = SystemClock.elapsedRealtime()
            val regranted = row(dao, "com.pillb.flight")
            assertTrue(
                "the next grant is priced at the edited 30 min " +
                    "(end=${regranted.activePeekUntilElapsedRealtime}, " +
                    "window=${before + 1_800_000L}..${after + 1_800_000L})",
                regranted.activePeekUntilElapsedRealtime in
                    (before + 1_800_000L)..(after + 1_800_000L)
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.flight"))
            restoreSettings(db, dao, originalSettings)
        }
    }

    // ------------------------------------------------------------------
    // R3 — hand-back with NO live lease. Today recordPeekEnded
    // unconditionally re-anchors the cooldown to the call instant
    // even on a never-granted row; the census names this
    // unspecified. This test PINS the current behaviour — if a
    // lane rules it wrong, this is the priced place to change it.
    // ------------------------------------------------------------------

    @Test
    fun handBackWithNoLiveLeaseReanchorsTheCooldownToNow() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        try {
            dao.insertApp(app("com.pillb.never"))

            val before = SystemClock.elapsedRealtime()
            repo.recordPeekEnded("com.pillb.never")
            val after = SystemClock.elapsedRealtime()

            val ended = row(dao, "com.pillb.never")
            assertEquals("the lease stays zeroed", 0L, ended.activePeekUntilElapsedRealtime)
            assertTrue(
                "the cooldown anchor moved to the hand-back instant " +
                    "(anchor=${ended.lastPeekEndElapsedRealtime}, window=$before..$after)",
                ended.lastPeekEndElapsedRealtime in before..after
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.never"))
        }
    }

    // ------------------------------------------------------------------
    // R3 (pair) — a SECOND hand-back moves the anchor again
    // (T16's L3 caveat): recordPeekEnded's anchor write is
    // unconditional, so an already-ended peek handed back twice
    // books two cooldown anchors, the later one standing. Pinned
    // as current behaviour, same status as R3.
    // ------------------------------------------------------------------

    @Test
    fun secondHandBackMovesTheCooldownAnchorAgain() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        try {
            dao.insertApp(app("com.pillb.twice"))

            assertTrue(repo.startPeekLease("com.pillb.twice"))
            repo.recordPeekEnded("com.pillb.twice")
            val firstAnchor = row(dao, "com.pillb.twice").lastPeekEndElapsedRealtime

            ShadowSystemClock.advanceBy(Duration.ofSeconds(5))
            val before = SystemClock.elapsedRealtime()
            repo.recordPeekEnded("com.pillb.twice")
            val after = SystemClock.elapsedRealtime()

            val second = row(dao, "com.pillb.twice")
            assertEquals("the lease stays zeroed", 0L, second.activePeekUntilElapsedRealtime)
            assertTrue(
                "the second hand-back re-anchored to its own instant " +
                    "(first=$firstAnchor, second=${second.lastPeekEndElapsedRealtime}, " +
                    "window=$before..$after)",
                second.lastPeekEndElapsedRealtime in before..after &&
                    second.lastPeekEndElapsedRealtime > firstAnchor
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.twice"))
        }
    }

    // ------------------------------------------------------------------
    // R4 — hand-back on an UNLISTED package: a no-op, not a crash,
    // and no other row is touched. (The grant half of the
    // unlisted case is ST-08's; this is the recordPeekEnded half.)
    // ------------------------------------------------------------------

    @Test
    fun handBackOnAnUnlistedPackageTouchesNothing() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        val bystander = app("com.pillb.bystander", peekUntil = 0L, peekEnd = 424_242L)
        try {
            dao.insertApp(bystander)

            repo.recordPeekEnded("com.pillb.absent")

            assertEquals(
                "no row was minted and the bystander is whole-row untouched",
                listOf(bystander),
                rowsWithPrefix(dao, "com.pillb.")
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.bystander"))
        }
    }

    // ------------------------------------------------------------------
    // R5 — re-grant sequences.
    // (a) grant A → hand-back A → grant B: exactly one live lease
    //     (B's), and A's anchor stands at its hand-back instant.
    // (b) grant A → grant A again: the end extends from the
    //     SECOND grant's instant; still exactly one live lease.
    // ------------------------------------------------------------------

    @Test
    fun grantHandBackGrantLeavesOneLiveLeaseAndTheHandBackAnchor() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        try {
            dao.insertApps(listOf(app("com.pillb.seqone"), app("com.pillb.seqtwo")))

            assertTrue(repo.startPeekLease("com.pillb.seqone"))
            repo.recordPeekEnded("com.pillb.seqone")
            val handedBack = row(dao, "com.pillb.seqone")
            assertEquals(0L, handedBack.activePeekUntilElapsedRealtime)

            assertTrue(repo.startPeekLease("com.pillb.seqtwo"))

            val rows = rowsWithPrefix(dao, "com.pillb.seq")
                .associateBy { it.packageName }
            assertEquals(
                "A's anchor still stands at its hand-back instant after B's grant",
                handedBack.lastPeekEndElapsedRealtime,
                rows.getValue("com.pillb.seqone").lastPeekEndElapsedRealtime
            )
            assertEquals(
                "exactly one live lease in storage, and it is B's",
                listOf("com.pillb.seqtwo"),
                rows.values
                    .filter { it.activePeekUntilElapsedRealtime > 0L }
                    .map { it.packageName }
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.seqone", "com.pillb.seqtwo"))
        }
    }

    @Test
    fun regrantingTheSameAppExtendsFromTheSecondGrant() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        try {
            dao.insertApp(app("com.pillb.again"))

            assertTrue(repo.startPeekLease("com.pillb.again"))
            val firstEnd = row(dao, "com.pillb.again").activePeekUntilElapsedRealtime

            val before = SystemClock.elapsedRealtime()
            assertTrue(repo.startPeekLease("com.pillb.again"))
            val after = SystemClock.elapsedRealtime()

            val second = row(dao, "com.pillb.again")
            assertTrue(
                "the second grant re-prices from its own instant " +
                    "(end=${second.activePeekUntilElapsedRealtime}, " +
                    "window=${before + 120_000L}..${after + 120_000L})",
                second.activePeekUntilElapsedRealtime in
                    (before + 120_000L)..(after + 120_000L)
            )
            assertTrue(
                "the re-grant never shortens the lease it replaces",
                second.activePeekUntilElapsedRealtime >= firstEnd
            )
            assertEquals(
                "anchor follows the new end",
                second.activePeekUntilElapsedRealtime,
                second.lastPeekEndElapsedRealtime
            )
            val live = rowsWithPrefix(dao, "com.pillb.again")
                .count { it.activePeekUntilElapsedRealtime > 0L }
            assertEquals("still exactly one live lease", 1, live)
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.again"))
        }
    }

    // ------------------------------------------------------------------
    // R6 — a grant sweeps an EXPIRED-but-nonzero lease: the stale
    // end is zeroed IN STORAGE (not merely ignored by the
    // derivation), and the swept row's earned anchor stands.
    // ------------------------------------------------------------------

    @Test
    fun grantSweepsAnExpiredNonzeroLeaseInStorage() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        ShadowSystemClock.advanceBy(Duration.ofMillis(300_000L))
        val now = SystemClock.elapsedRealtime()
        val stale = app(
            "com.pillb.stale",
            peekUntil = (now - 60_000L).coerceAtLeast(1L),
            peekEnd = (now - 60_000L).coerceAtLeast(1L)
        )
        try {
            dao.insertApps(listOf(stale, app("com.pillb.fresh")))

            assertTrue(repo.startPeekLease("com.pillb.fresh"))

            val swept = row(dao, "com.pillb.stale")
            assertEquals(
                "the expired lease is zeroed in storage by the grant",
                0L,
                swept.activePeekUntilElapsedRealtime
            )
            assertEquals(
                "…while its earned cooldown anchor stands",
                stale.lastPeekEndElapsedRealtime,
                swept.lastPeekEndElapsedRealtime
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.stale", "com.pillb.fresh"))
        }
    }

    // ------------------------------------------------------------------
    // R7 — bystander anchor preservation: the existing zeroing
    // test asserts the bystander's LEASE only; a bystander with a
    // pre-existing cooldown anchor keeps it bit-identical across
    // another row's grant.
    // ------------------------------------------------------------------

    @Test
    fun grantPreservesABystandersCooldownAnchorBitIdentically() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        val bystander = app("com.pillb.watcher", peekUntil = 0L, peekEnd = 777_777L)
        try {
            dao.insertApps(listOf(bystander, app("com.pillb.grantee")))

            assertTrue(repo.startPeekLease("com.pillb.grantee"))

            assertEquals(
                "the bystander row is whole-row untouched by the grant",
                bystander,
                row(dao, "com.pillb.watcher")
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.pillb.watcher", "com.pillb.grantee"))
        }
    }

    // ------------------------------------------------------------------
    // R8 — concurrent grants (critic C26, named unguarded in the
    // existing suite's own comment): two racing grants on two
    // rows, repeated, must never leave two live leases in
    // storage. The grant round is clear-then-stamp with no
    // transaction, so this test is EVIDENCE either way: if a
    // round ever fails, the failing round number is in the
    // assertion message and the priced cure (a @Transaction on
    // the grant round) is a post-train BUILD proposal — this
    // test is where that proposal gets its proof.
    // ------------------------------------------------------------------

    @Test
    fun concurrentGrantsLeaveAtMostOneLiveLease() = runBlocking {
        val dao = database().monolithDao()
        val repo = repository(dao)
        val packages = listOf("com.pillb.raceone", "com.pillb.racetwo")
        try {
            dao.insertApps(packages.map { app(it) })
            repeat(20) { round ->
                val results = packages
                    .map { pkg -> async { repo.startPeekLease(pkg) } }
                    .awaitAll()
                assertTrue(
                    "round $round: both grants report success",
                    results.all { it }
                )
                val live = dao.getAllAppsSnapshot()
                    .filter { it.packageName in packages }
                    .count { it.activePeekUntilElapsedRealtime > 0L }
                assertTrue(
                    "round $round: at most one live lease in storage (saw $live)",
                    live <= 1
                )
            }
        } finally {
            dao.deleteAppsByPackage(packages)
        }
    }

    // ------------------------------------------------------------------
    // Cooldown arithmetic — the boundaries around
    // ExampleRobolectricTest's happy path. The function reads
    // SystemClock at call time, so anchors are built relative to
    // a fresh read; every window below is wide enough for the
    // milliseconds the call itself consumes, and tight enough to
    // catch a floor-for-ceiling or cap regression.
    // ------------------------------------------------------------------

    @Test
    fun cooldownIsZeroAndEndsAtIsNullWhenNoPeekEverAnchored() {
        val repo = repository(database().monolithDao())
        val fresh = app("com.pillb.cold", peekUntil = 0L, peekEnd = 0L)
        assertEquals(0, repo.getPeekCooldownRemainingSeconds(fresh))
        assertEquals(null, repo.peekCooldownEndsAtElapsedRealtime(fresh))
    }

    @Test
    fun cooldownExpiresToZeroTwentyMinutesAfterTheAnchor() {
        val repo = repository(database().monolithDao())
        // The Robolectric monotonic clock may start near boot 0;
        // advance it (the ForgeLotTest pattern) so an anchor
        // 20 minutes in the past is a real, positive instant.
        ShadowSystemClock.advanceBy(Duration.ofMillis(1_300_000L))
        val now = SystemClock.elapsedRealtime()
        // Anchored 20 minutes and a second ago: fully expired.
        val expired = app(
            "com.pillb.expired",
            peekUntil = 0L,
            peekEnd = (now - 1_201_000L).coerceAtLeast(1L)
        )
        assertEquals(0, repo.getPeekCooldownRemainingSeconds(expired))
        assertEquals(
            "the end instant is the anchor + 20 min, even in the past",
            expired.lastPeekEndElapsedRealtime + 1_200_000L,
            repo.peekCooldownEndsAtElapsedRealtime(expired)
        )
    }

    @Test
    fun cooldownIsCappedAtTwentyMinutesEvenForALiveLeaseAnchor() {
        val repo = repository(database().monolithDao())
        val now = SystemClock.elapsedRealtime()
        // A live lease's end is in the future; the anchor law
        // takes max(lastPeekEnd, activePeekUntil), so without the
        // cap the remaining would read 1 200 + the lease's whole
        // remaining fuse. It must read exactly the 20-minute cap.
        val live = app(
            "com.pillb.live",
            peekUntil = now + 120_000L,
            peekEnd = now + 120_000L
        )
        assertEquals(1_200, repo.getPeekCooldownRemainingSeconds(live))
    }

    @Test
    fun cooldownCeilingsAPartialSecondUpNeverDown() {
        val repo = repository(database().monolithDao())
        val now = SystemClock.elapsedRealtime()
        // Anchor 1 500 ms in the future (a just-granted lease's
        // pre-armed anchor shape, shortened): raw remaining is
        // 1 200 000 + ~1 500 ms, capped to 1 200 000 ms, then the
        // sub-second part the call consumes must still ceiling
        // the print to the full 1 200 — a floor would read 1 199.
        val justGranted = app(
            "com.pillb.partial",
            peekUntil = 0L,
            peekEnd = now + 1_500L
        )
        assertEquals(1_200, repo.getPeekCooldownRemainingSeconds(justGranted))

        // Mid-cooldown: ended 10 minutes ago, so ~600 s remain;
        // the ceiling keeps the print at 600 for the first
        // partial second rather than dropping to 599 at once.
        // (Clock advanced first, as in the expiry test above, so
        // "10 minutes ago" is a real positive anchor.)
        ShadowSystemClock.advanceBy(Duration.ofMillis(1_300_000L))
        val midNow = SystemClock.elapsedRealtime()
        val mid = app(
            "com.pillb.mid",
            peekUntil = 0L,
            peekEnd = (midNow - 600_000L).coerceAtLeast(1L)
        )
        assertEquals(600, repo.getPeekCooldownRemainingSeconds(mid))
    }

    @Test
    fun cooldownEndFollowsTheLaterOfTheTwoAnchors() {
        val repo = repository(database().monolithDao())
        val now = SystemClock.elapsedRealtime()
        // lastPeekEnd is the older anchor; the live lease's end
        // is later and must win both reads.
        val row = app(
            "com.pillb.maxanchor",
            peekUntil = now + 5_000L,
            peekEnd = (now - 1_000L).coerceAtLeast(1L)
        )
        assertEquals(
            now + 5_000L + 1_200_000L,
            repo.peekCooldownEndsAtElapsedRealtime(row)
        )
        // And the mirror: a hand-back anchor later than a stale
        // lease end wins instead.
        val mirrored = app(
            "com.pillb.maxmirror",
            peekUntil = (now - 5_000L).coerceAtLeast(1L),
            peekEnd = now + 1_000L
        )
        assertEquals(
            now + 1_000L + 1_200_000L,
            repo.peekCooldownEndsAtElapsedRealtime(mirrored)
        )
    }

    @Test
    fun cooldownForAnUnknownPackageIsZero() = runBlocking {
        val repo = repository(database().monolithDao())
        assertEquals(0, repo.getPeekCooldownRemainingSeconds("com.pillb.unknown"))
    }

    // ------------------------------------------------------------------
    // T3 (theme, the Context half) — figures() caches: the second
    // call returns the identical Typeface instance, whether the
    // bundled Anonymous Pro loaded or the monospace fallback
    // carried it; the figures must never wait on a font, and the
    // renderer must not re-pay the load every repaint.
    // ------------------------------------------------------------------

    @Test
    fun figuresTypefaceIsCachedAcrossCalls() {
        val first: Typeface = PillTheme.figures(context)
        val second: Typeface = PillTheme.figures(context)
        assertSame("figures() must return its cached instance", first, second)
    }
}
