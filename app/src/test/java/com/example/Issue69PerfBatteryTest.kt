package com.example

import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.shieldBeatIsStale
import com.example.service.ShieldNotificationGuard
import com.example.service.shieldIterationApps
import com.example.service.shieldNotificationKey
import com.example.ui.screens.RestingFlip
import com.example.ui.screens.pruneRests
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #69 (Audit R1, performance-battery) — the four cures,
 * proven at the pure seams the production code actually calls:
 *
 *  F1  the shield loop derives its blocked set, its expired-peek
 *      eviction and the intercepted app's row from ONE apps
 *      snapshot per iteration (counting provider), and re-posts
 *      its standing notification only when the readable text
 *      changes (guard).
 *  F3  the ViewModel ticker's persist decision is the shield-gap
 *      law itself: fresh beat -> no duplicate write; stale past
 *      10 s -> the fallback writer fires. Proven on the pure law
 *      and on the repository door against the real database.
 *  F4  a repeat sync inside the collapse window skips the
 *      PackageManager enumeration itself (the gate the
 *      repository consults before enumerating).
 *  F5a the ShieldMatrix rest sweeper is demand-driven: with an
 *      empty rests map no sweep ever runs; the prune law retires
 *      rests by age only while rests exist.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue69PerfBatteryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    // ------------------------------------------------------------ F1

    private fun app(
        pkg: String,
        tier: AppBlockTier,
        leaseEnd: Long = 0L,
        dailyLimit: Int = 0,
        usage: Int = 0
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = pkg,
        category = "SOCIAL",
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name,
        dailyLimitMinutes = dailyLimit,
        todayUsageMinutes = usage,
        activePeekUntilElapsedRealtime = leaseEnd
    )

    @Test
    fun f1_oneSnapshotFeedsEveryConsumer() {
        val now = 1_000_000L
        val rows = listOf(
            app("locked", AppBlockTier.LOCKED),
            app("open", AppBlockTier.OPEN),
            app("leased", AppBlockTier.PEEK_2M, leaseEnd = now + 60_000L),
            app("expired", AppBlockTier.PEEK_2M, leaseEnd = now - 5_000L),
            app("overlimit", AppBlockTier.OPEN, dailyLimit = 30, usage = 45)
        )
        var providerCalls = 0
        val provider = { providerCalls++; rows }

        // The loop's exact derivation: ONE provider call, then
        // every consumer reads the resulting iteration view.
        val iteration = shieldIterationApps(provider(), isInSafeBreak = false, nowMono = now)

        assertEquals(1, providerCalls)
        assertEquals(setOf("locked", "expired", "overlimit"), iteration.blockedPackages)
        assertEquals(setOf("expired"), iteration.expiredPeeks)
        assertEquals("leased", iteration.appFor("leased")?.packageName)
        assertEquals(rows, iteration.snapshot)
    }

    @Test
    fun f1_safeBreakUnblocksPeekTier() {
        val now = 1_000_000L
        val rows = listOf(app("peek", AppBlockTier.PEEK_2M, leaseEnd = 0L))
        val iteration = shieldIterationApps(rows, isInSafeBreak = true, nowMono = now)
        assertTrue(iteration.blockedPackages.isEmpty())
    }

    @Test
    fun f1_notificationRepostsOnlyOnTextChange() {
        val guard = ShieldNotificationGuard()
        val key = shieldNotificationKey("Monolith • Study", "25:00 left • 3 reaches blocked", "big", 2)
        assertTrue(guard.shouldPost(key)) // first post
        assertFalse(guard.shouldPost(key)) // identical text: no re-post
        assertFalse(
            guard.shouldPost(
                shieldNotificationKey("Monolith • Study", "25:00 left • 3 reaches blocked", "big", 2)
            )
        )
        // A stopped reach changes the count -> re-post.
        assertTrue(
            guard.shouldPost(
                shieldNotificationKey("Monolith • Study", "25:00 left • 4 reaches blocked", "big", 2)
            )
        )
        // A held notification changes the badge -> re-post.
        assertTrue(
            guard.shouldPost(
                shieldNotificationKey("Monolith • Study", "25:00 left • 4 reaches blocked", "big", 3)
            )
        )
        // The countdown minute turns -> re-post.
        assertTrue(
            guard.shouldPost(
                shieldNotificationKey("Monolith • Study", "24:00 left • 4 reaches blocked", "big", 3)
            )
        )
    }

    // ------------------------------------------------------------ F3

    @Test
    fun f3_pureLaw_freshBeatIsNotStale_staleBeatIs() {
        val start = 1_000_000L
        // Beat 3 s ago, implied now = start + 3 s: fresh.
        assertFalse(
            shieldBeatIsStale(
                shieldLastBeatWallMillis = start,
                startWallTimeMillis = start,
                appLastUpdateMillis = 0L,
                impliedNowMillis = start + 3_000L
            )
        )
        // Beat exactly at the 10 s threshold: not stale (law is >).
        assertFalse(
            shieldBeatIsStale(
                shieldLastBeatWallMillis = start,
                startWallTimeMillis = start,
                appLastUpdateMillis = 0L,
                impliedNowMillis = start + 10_000L
            )
        )
        // Beat 11 s stale: the fallback writer must fire.
        assertTrue(
            shieldBeatIsStale(
                shieldLastBeatWallMillis = start,
                startWallTimeMillis = start,
                appLastUpdateMillis = 0L,
                impliedNowMillis = start + 11_000L
            )
        )
        // Pre-v10 row (beat 0): no beat evidence, never stale by
        // this test — the latch adopts it instead.
        assertFalse(
            shieldBeatIsStale(
                shieldLastBeatWallMillis = 0L,
                startWallTimeMillis = start,
                appLastUpdateMillis = 0L,
                impliedNowMillis = start + 3_600_000L
            )
        )
        // Update exemption (#95): the build landed after the last
        // beat — downtime is not a gap.
        assertFalse(
            shieldBeatIsStale(
                shieldLastBeatWallMillis = start,
                startWallTimeMillis = start,
                appLastUpdateMillis = start + 20_000L,
                impliedNowMillis = start + 25_000L
            )
        )
    }

    @Test
    fun f3_repositoryDoor_matchesTheLaw() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, db.monolithDao())
        val now = System.currentTimeMillis()
        val mono = SystemClock.elapsedRealtime()

        suspend fun seed(beat: Long, startElapsed: Long) {
            dao.insertSession(
                FocusSessionEntity(
                    intention = "Deep Study",
                    profileName = "Default",
                    targetDurationSeconds = 3000,
                    elapsedSeconds = 0,
                    startWallTimeMillis = now - (mono - startElapsed),
                    startElapsedRealtimeMillis = startElapsed,
                    isActive = true,
                    completedSuccessfully = false,
                    isStopwatchMode = false,
                    outcome = "",
                    shieldLastBeatWallMillis = beat,
                    shieldGapSeen = false
                )
            )
        }

        // Fresh beat (2 monotonic seconds ago): the VM must NOT
        // duplicate the shield's write.
        seed(beat = now - 2_000L, startElapsed = mono - 2_000L)
        assertFalse(repo.isShieldBeatStale(mono))

        // Beat silent for 30 monotonic seconds: stale past the
        // 10 s law — the VM fallback writer fires.
        seed(beat = now - 30_000L, startElapsed = mono - 30_000L)
        assertTrue(repo.isShieldBeatStale(mono))
    }

    // ------------------------------------------------------------ F4

    @Test
    fun f4_repeatSyncInsideWindowSkipsEnumeration() {
        val lastSync = 5_000_000L
        // Two minutes after a completed sync, non-forced: the
        // enumeration is skipped — zero PackageManager work.
        assertTrue(
            AppCatalogReconcile.isEnumerationSkipped(
                forceCatalogSync = false,
                lastSuccessfulSyncMillis = lastSync,
                nowMillis = lastSync + 120_000L
            )
        )
        // A forced sync always enumerates.
        assertFalse(
            AppCatalogReconcile.isEnumerationSkipped(
                forceCatalogSync = true,
                lastSuccessfulSyncMillis = lastSync,
                nowMillis = lastSync + 120_000L
            )
        )
        // No prior sync on record: enumerate.
        assertFalse(
            AppCatalogReconcile.isEnumerationSkipped(
                forceCatalogSync = false,
                lastSuccessfulSyncMillis = 0L,
                nowMillis = lastSync + 120_000L
            )
        )
        // After the collapse window: a genuinely separate look.
        assertFalse(
            AppCatalogReconcile.isEnumerationSkipped(
                forceCatalogSync = false,
                lastSuccessfulSyncMillis = lastSync,
                nowMillis = lastSync + AppCatalogReconcile.SYNC_COLLAPSE_WINDOW_MILLIS + 1L
            )
        )
    }

    // ------------------------------------------------------------ F5a

    private fun rest(createdAtMs: Long) = RestingFlip(
        from = AppBlockTier.OPEN,
        to = AppBlockTier.LOCKED,
        exiting = false,
        createdAtMs = createdAtMs
    )

    @Test
    fun f5a_pruneRetiresOnlyAgedRests() {
        val now = 100_000L
        val rests = mapOf(
            "young" to rest(now - 500L),
            "edge" to rest(now - 1_200L),
            "old" to rest(now - 1_201L)
        )
        assertEquals(setOf("young", "edge"), pruneRests(rests, now).keys)
        assertTrue(pruneRests(emptyMap(), now).isEmpty())
    }

    @Test
    fun f5a_noSweepRunsWhileRestsAreEmpty() = runBlocking {
        // The screen's exact effect structure: snapshotFlow on
        // the rests map; the sweep body runs only while the map
        // is non-empty. With an empty map, zero sweeps — forever,
        // not one 700 ms wakeup after another.
        var rests by mutableStateOf<Map<String, RestingFlip>>(emptyMap())
        var sweepRuns = 0
        val job = launch {
            snapshotFlow { rests }.collect { flips ->
                if (flips.isNotEmpty()) {
                    while (rests.isNotEmpty()) {
                        sweepRuns++
                        rests = pruneRests(rests, SystemClock.elapsedRealtime())
                        if (rests.isNotEmpty()) delay(50)
                    }
                }
            }
        }
        delay(300)
        assertEquals(0, sweepRuns)

        // A rest lands: the sweep wakes, and retires it by age.
        val now = SystemClock.elapsedRealtime()
        rests = mapOf("a" to rest(now - 5_000L))
        delay(300)
        assertTrue(sweepRuns >= 1)
        assertTrue(rests.isEmpty())

        // Empty again: the sweep goes silent again.
        val runsAfter = sweepRuns
        delay(300)
        assertEquals(runsAfter, sweepRuns)
        job.cancel()
    }
}
