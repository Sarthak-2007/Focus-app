package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.EditableTimesProvider
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * ST-08: the composed peek machinery refuses safely.
 *
 * [MonolithRepository.startPeekLease] is a composition of two
 * gates, in this order (batch-10, pill Flag B + Ed2 B6):
 *
 *  1. the duration gate — the length comes through
 *     [EditableTimesProvider] off the guard settings row
 *     (peekMinutes, clamped 0–30); a duration of 0 (peekMinutes
 *     = 0, peek OFF) refuses BEFORE any DAO write: no lease is
 *     stamped and no other app's live lease is cleared;
 *  2. the listing gate — a package with no row in blocked_apps
 *     refuses the same way: no lease, no cooldown, no row minted.
 *
 * And the provider itself is pinned as a table: 0 stays 0,
 * over-range values clamp to §3.5's bounds (peek 30, break 60),
 * and a MISSING settings row falls back to the shipped defaults
 * (peek 2 min, break 10 min).
 *
 * A happy-path control closes the file: with a listed package
 * and peekMinutes = 2 the same function grants, stamps the end
 * ≈ now + 120_000, and clears a pre-existing lease on another
 * app — so the refusals above are the gates working, not a
 * function that simply never grants.
 *
 * Hygiene (the PillLease / BreakVerbs precedent): the database
 * file is shared by the whole suite, so packages are unique per
 * test, this class's app rows are deleted on the way out, and
 * the guard settings singleton — which other classes read at
 * its defaults — is restored EXACTLY after every test that
 * touches it: the prior row re-inserted verbatim, or, if there
 * was no row, the row deleted again via the open helper (the
 * RoutinesFinalise / TokenBreak DELETE precedent; the DAO
 * itself offers no settings delete).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class St08PeekRefusalTest {

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

    /** Restore the settings singleton to exactly what this test
     *  found: the prior row verbatim, or no row at all. */
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

    // ------------------------------------------------------------------
    // 1. peekMinutes = 0 refuses, and writes NOTHING at all.
    // ------------------------------------------------------------------

    @Test
    fun peekMinutesZeroRefusesAndWritesNothing() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val repo = repository(dao)
        val originalSettings = dao.getGuardSettingsSnapshot()

        // The target is LISTED (the listing gate would pass) and
        // another app holds a live lease with its cooldown anchor
        // standing: if the refusal wrote anything — a lease stamp
        // on the target, or clearOtherPeekLeases on the other
        // row — one of the whole-row equalities below breaks.
        val otherLeaseEnd = SystemClock.elapsedRealtime() + 600_000L
        val target = app("com.st08zero.target")
        val other = app(
            "com.st08zero.other",
            peekUntil = otherLeaseEnd,
            peekEnd = otherLeaseEnd
        )
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 0, breakMinutes = 10)
            )
            dao.insertApps(listOf(target, other))

            // Precondition: the zero really comes from the row —
            // the provider reads this settings row as 0 ms, so the
            // refusal below is the duration gate's, no other.
            assertEquals(0L, EditableTimesProvider(dao).peekDurationMillis())

            val granted = repo.startPeekLease("com.st08zero.target")

            assertFalse("peekMinutes = 0 must refuse the grant", granted)
            val rows = rowsWithPrefix(dao, "com.st08zero.")
                .associateBy { it.packageName }
            assertEquals(
                "no peek lease row exists afterwards for the target",
                0L,
                rows.getValue("com.st08zero.target").activePeekUntilElapsedRealtime
            )
            assertEquals(
                "the target row is untouched, whole-row",
                target,
                rows.getValue("com.st08zero.target")
            )
            assertEquals(
                "no other app's lease was cleared — nothing written at all",
                other,
                rows.getValue("com.st08zero.other")
            )
        } finally {
            dao.deleteAppsByPackage(
                listOf("com.st08zero.target", "com.st08zero.other")
            )
            restoreSettings(db, dao, originalSettings)
        }
    }

    // ------------------------------------------------------------------
    // 2. An unlisted package refuses, and writes NOTHING.
    // ------------------------------------------------------------------

    @Test
    fun unlistedPackageRefusesAndWritesNothing() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val repo = repository(dao)
        val originalSettings = dao.getGuardSettingsSnapshot()

        // Duration is POSITIVE here (peekMinutes = 2), so the
        // duration gate passes and the refusal is the listing
        // gate's alone. The bystander holds a live lease: the
        // refusal must not clear it on its way out.
        val bystanderLeaseEnd = SystemClock.elapsedRealtime() + 600_000L
        val bystander = app(
            "com.st08unlisted.bystander",
            peekUntil = bystanderLeaseEnd,
            peekEnd = bystanderLeaseEnd
        )
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 2, breakMinutes = 10)
            )
            dao.insertApp(bystander)
            assertEquals(
                "precondition: the duration gate would pass",
                120_000L,
                EditableTimesProvider(dao).peekDurationMillis()
            )

            val granted = repo.startPeekLease("com.st08unlisted.absent")

            assertFalse("an unlisted package must be refused", granted)
            assertEquals(
                "no row was minted for the unlisted package and the " +
                    "bystander's lease was not cleared — nothing written",
                listOf(bystander),
                rowsWithPrefix(dao, "com.st08unlisted.")
            )
        } finally {
            dao.deleteAppsByPackage(listOf("com.st08unlisted.bystander"))
            restoreSettings(db, dao, originalSettings)
        }
    }

    // ------------------------------------------------------------------
    // 3. The provider's table: zero, clamps, and the missing row.
    // ------------------------------------------------------------------

    @Test
    fun providerZeroMinutesIsZeroDuration() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 0, breakMinutes = 0)
            )
            val provider = EditableTimesProvider(dao)
            assertEquals(0L, provider.peekDurationMillis())
            assertEquals(0, provider.breakDurationSeconds())
        } finally {
            restoreSettings(db, dao, originalSettings)
        }
    }

    @Test
    fun providerClampsAboveTheBounds() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 45, breakMinutes = 90)
            )
            val provider = EditableTimesProvider(dao)
            assertEquals(
                "peek 45 min clamps to the 30 min bound",
                30 * 60_000L,
                provider.peekDurationMillis()
            )
            assertEquals(
                "break 90 min clamps to the 60 min bound, in seconds",
                60 * 60,
                provider.breakDurationSeconds()
            )
        } finally {
            restoreSettings(db, dao, originalSettings)
        }
    }

    @Test
    fun providerClampsBelowZeroToZero() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = -5, breakMinutes = -5)
            )
            val provider = EditableTimesProvider(dao)
            assertEquals(0L, provider.peekDurationMillis())
            assertEquals(0, provider.breakDurationSeconds())
        } finally {
            restoreSettings(db, dao, originalSettings)
        }
    }

    @Test
    fun providerMissingSettingsRowFallsBackToDefaults() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val originalSettings = dao.getGuardSettingsSnapshot()
        try {
            // The DAO offers no settings delete, so the missing-row
            // state is made with the suite's DELETE precedent —
            // and PROVEN missing before the provider is read, so
            // this test can never pass against a leftover row.
            db.openHelper.writableDatabase.execSQL(
                "DELETE FROM guard_settings WHERE id = 1"
            )
            assertNull(
                "precondition: the settings row really is missing",
                dao.getGuardSettingsSnapshot()
            )

            val provider = EditableTimesProvider(dao)
            assertEquals(
                "missing row: peek defaults to 2 min",
                2 * 60_000L,
                provider.peekDurationMillis()
            )
            assertEquals(
                "missing row: break defaults to 10 min, in seconds",
                10 * 60,
                provider.breakDurationSeconds()
            )
        } finally {
            restoreSettings(db, dao, originalSettings)
        }
    }

    // ------------------------------------------------------------------
    // 4. Happy-path control: the same function grants when it should.
    // ------------------------------------------------------------------

    @Test
    fun listedPackageWithPeekMinutesTwoGrantsAndClearsOtherLease() = runBlocking {
        val db = database()
        val dao = db.monolithDao()
        val repo = repository(dao)
        val originalSettings = dao.getGuardSettingsSnapshot()

        val otherLeaseEnd = SystemClock.elapsedRealtime() + 600_000L
        val target = app("com.st08happy.target")
        val other = app(
            "com.st08happy.other",
            peekUntil = otherLeaseEnd,
            peekEnd = otherLeaseEnd
        )
        try {
            dao.insertGuardSettings(
                GuardSettingsEntity(peekMinutes = 2, breakMinutes = 10)
            )
            dao.insertApps(listOf(target, other))

            val before = SystemClock.elapsedRealtime()
            val granted = repo.startPeekLease("com.st08happy.target")
            val after = SystemClock.elapsedRealtime()

            assertTrue("a listed package at peekMinutes = 2 must grant", granted)
            val rows = rowsWithPrefix(dao, "com.st08happy.")
                .associateBy { it.packageName }
            val leaseEnd = rows.getValue("com.st08happy.target")
                .activePeekUntilElapsedRealtime
            assertTrue(
                "lease end ≈ now + 120_000 (end was ${leaseEnd - before}ms " +
                    "after the pre-call clock, ${leaseEnd - after}ms after " +
                    "the post-call clock)",
                leaseEnd - before >= 120_000L && leaseEnd - after <= 120_000L
            )
            assertEquals(
                "the pre-existing lease on the OTHER app is cleared",
                0L,
                rows.getValue("com.st08happy.other").activePeekUntilElapsedRealtime
            )
            assertEquals(
                "…while the other app's cooldown anchor stands",
                otherLeaseEnd,
                rows.getValue("com.st08happy.other").lastPeekEndElapsedRealtime
            )
        } finally {
            dao.deleteAppsByPackage(
                listOf("com.st08happy.target", "com.st08happy.other")
            )
            restoreSettings(db, dao, originalSettings)
        }
    }
}
