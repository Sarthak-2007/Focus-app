package com.example

import com.example.service.InterceptorState
import com.example.service.PermPrint
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchStore
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Chrome wave — the Watch Store's writers, exercised live: the
 * sweep's parallel landings complete a set (LAST PROVED advances
 * only then), lifecycle stamps land as measurements, and the
 * interceptor family derives from settings + parents + suspension.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WatchStoreRobolectricTest {

    @Before
    fun reset() {
        WatchStore.resetForTest()
    }

    private fun awaitSnapshot(timeoutMs: Long = 30_000, until: (com.example.service.WatchSnapshot) -> Boolean) {
        // The sweep lands on Dispatchers.IO; under a loaded bench
        // (parallel lanes compiling) the sandbox's threads starve,
        // so the poll idles the main looper to help it along and
        // waits generously — the timeout is a bound, not a sleep.
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (until(WatchStore.snapshots.value)) return
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            Thread.sleep(25)
        }
        throw AssertionError("snapshot condition not met within ${timeoutMs}ms: ${WatchStore.snapshots.value}")
    }

    @Test
    fun sweep_completesTheSetAndAdvancesLastProved() {
        assertNull(WatchStore.snapshots.value.lastProvedWallMillis)
        WatchStore.runSweep { item ->
            if (item != WatchItemId.DEVICE_ADMIN) PermState.GRANTED else PermState.MISSING
        }
        awaitSnapshot { it.lastProvedWallMillis != null }
        val snap = WatchStore.snapshots.value
        assertNotNull(snap.lastProvedWallMillis)
        // The sweep's verdicts landed per item.
        val now = android.os.SystemClock.elapsedRealtime()
        assertEquals(
            PermPrint.MISSING,
            WatchStore.permPrintAt(snap, WatchItemId.DEVICE_ADMIN, now)
        )
        assertEquals(
            PermPrint.GRANTED,
            WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, now)
        )
    }

    @Test
    fun sweep_completionAdvancesTheCompletedGeneration() {
        // Issue #9 (Critic C-1): PARK's CHECK key is answered only
        // by the sweep it dispatched — the snapshot records WHICH
        // generation completed, in parity with LAST PROVED.
        assertEquals(0, WatchStore.snapshots.value.lastCompletedSweepGen)
        WatchStore.runSweep { PermState.GRANTED }
        val gen = WatchStore.currentSweepGen()
        awaitSnapshot { it.lastCompletedSweepGen == gen }
        assertEquals(gen, WatchStore.snapshots.value.lastCompletedSweepGen)
        assertNotNull(WatchStore.snapshots.value.lastProvedWallMillis)
    }

    @Test
    fun sweep_thrownQueryStampsNothingAndBlocksCompletion() {
        // Issue #9 (Critic C-9): a query that throws is a crashed
        // question, not a measured "no" — it stamps nothing, the
        // item keeps its (absent) word, and the set cannot
        // complete on it.
        WatchStore.runSweep { item ->
            if (item == WatchItemId.DEVICE_ADMIN) {
                throw RuntimeException("binder died")
            }
            PermState.GRANTED
        }
        // The other five land…
        awaitSnapshot { snap ->
            WatchItemId.PERMISSIONS
                .filter { it != WatchItemId.DEVICE_ADMIN }
                .all { snap.measurement(it) != null }
        }
        // …and after a settle, the crashed door is still
        // unmeasured and nothing has completed.
        Thread.sleep(250)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        val snap = WatchStore.snapshots.value
        assertNull(snap.measurement(WatchItemId.DEVICE_ADMIN))
        assertNull(snap.lastProvedWallMillis)
        assertEquals(0, snap.lastCompletedSweepGen)
    }

    @Test
    fun sweep_gen0StampsCannotCompleteAPendingSweep() {
        // Issue #9 (Zahn Z-1): completion tests the stamp's
        // sweepGen, not the wall-clock era. The sweep's DEVICE_ADMIN
        // query crashes (stamps nothing); a gen-0 lifecycle stamp
        // for that same door lands INSIDE the sweep's era — under
        // the old era test that stamp completed the set and the
        // store badged it with the sweep's generation. Now it must
        // not: presence is not the sweep's answer.
        WatchStore.runSweep { item ->
            if (item == WatchItemId.DEVICE_ADMIN) {
                throw RuntimeException("binder died")
            }
            PermState.GRANTED
        }
        val gen = WatchStore.currentSweepGen()
        awaitSnapshot { snap ->
            WatchItemId.PERMISSIONS
                .filter { it != WatchItemId.DEVICE_ADMIN }
                .all { snap.measurement(it)?.sweepGen == gen }
        }
        // The gen-0 rescue attempt: lifecycle + shield-loop roads.
        WatchStore.onDeviceAdminChanged(true)
        WatchStore.onShieldIteration(protecting = true)
        Thread.sleep(250)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        val snap = WatchStore.snapshots.value
        assertEquals(
            0,
            snap.measurement(WatchItemId.DEVICE_ADMIN)?.sweepGen
        )
        assertNull(snap.lastProvedWallMillis)
        assertEquals(0, snap.lastCompletedSweepGen)
    }

    @Test
    fun sweep_abandonedSweepRecordsItsFailedGeneration() {
        // Issue #9 (Zahn Z-6): a sweep that can never complete is
        // abandoned on the record once its coexistence window
        // closes — lastFailedSweepGen names it, so PARK's CHECK
        // key can print DID NOT ANSWER instead of CHECKING forever.
        WatchStore.runSweep { item ->
            if (item == WatchItemId.DEVICE_ADMIN) {
                throw RuntimeException("binder died")
            }
            PermState.GRANTED
        }
        val gen = WatchStore.currentSweepGen()
        awaitSnapshot { snap ->
            WatchItemId.PERMISSIONS
                .filter { it != WatchItemId.DEVICE_ADMIN }
                .all { snap.measurement(it) != null }
        }
        // Not yet stale: no failure recorded.
        assertEquals(0, WatchStore.snapshots.value.lastFailedSweepGen)
        assertTrue(
            WatchStore.expirePendingSweepIfStale(
                System.currentTimeMillis() + WatchStore.STALE_AFTER_MS + 1_000L
            )
        )
        val snap = WatchStore.snapshots.value
        assertEquals(gen, snap.lastFailedSweepGen)
        assertEquals(0, snap.lastCompletedSweepGen)
        assertNull(snap.lastProvedWallMillis)
        assertEquals(
            WatchItemId.DEVICE_ADMIN,
            WatchStore.firstUnansweredForGen(snap, gen)
        )
        // Idempotent: a second expiry changes nothing.
        assertTrue(
            !WatchStore.expirePendingSweepIfStale(
                System.currentTimeMillis() + WatchStore.STALE_AFTER_MS + 2_000L
            )
        )
    }

    @Test
    fun sweep_usageUnsetStampsUnsetAndPrintsUnset() {
        // Issue #9 (Zahn Z-7): the usage door's UNSET is a measured
        // third state — it stamps as UNSET and prints as UNSET,
        // never as a fresh MISSING. The set still completes: UNSET
        // is an answer, not a silence.
        WatchStore.runSweep { item ->
            if (item == WatchItemId.USAGE_ACCESS) PermState.UNSET
            else PermState.GRANTED
        }
        awaitSnapshot { it.lastProvedWallMillis != null }
        val snap = WatchStore.snapshots.value
        val now = android.os.SystemClock.elapsedRealtime()
        assertEquals(
            PermPrint.UNSET,
            WatchStore.permPrintAt(snap, WatchItemId.USAGE_ACCESS, now)
        )
        // UNSET never stales into another word.
        assertEquals(
            PermPrint.UNSET,
            WatchStore.permPrintAt(
                snap, WatchItemId.USAGE_ACCESS, now + WatchStore.STALE_AFTER_MS + 1_000L
            )
        )
    }

    @Test
    fun bindWriters_stampTheirItems() {
        WatchStore.onAccessibilityBound(true)
        WatchStore.onNotificationListenerBound(false)
        WatchStore.onDeviceAdminChanged(true)
        val snap = WatchStore.snapshots.value
        val now = android.os.SystemClock.elapsedRealtime()
        assertEquals(PermPrint.GRANTED, WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, now))
        assertEquals(PermPrint.MISSING, WatchStore.permPrintAt(snap, WatchItemId.NOTIFICATION_LISTENER, now))
        assertEquals(PermPrint.GRANTED, WatchStore.permPrintAt(snap, WatchItemId.DEVICE_ADMIN, now))
        // No sweep ran: no complete set, no LAST PROVED.
        assertNull(snap.lastProvedWallMillis)
    }

    @Test
    fun interceptors_deriveFromSettingsParentsAndSuspension() {
        // Parents unmeasured: the lines are unmeasured (name only).
        WatchStore.updateInterceptorInputs(shortsOn = true, sitesOn = true, safeBreak = false)
        var snap = WatchStore.snapshots.value
        assertNull(snap.measurement(WatchItemId.SHORTS_REELS))

        // Parents granted, settings on, no suspension: RUNNING.
        // Await the COMPLETE set (LAST PROVED), not the first
        // derived line: the six queries land in parallel, and a
        // sibling item's parent may still be in flight when the
        // first interceptor derives.
        WatchStore.onAccessibilityBound(true)
        WatchStore.runSweep { PermState.GRANTED }
        awaitSnapshot { s -> s.lastProvedWallMillis != null }
        awaitSnapshot { s -> s.measurement(WatchItemId.SHORTS_REELS)?.interceptor == InterceptorState.RUNNING }
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.RUNNING, snap.measurement(WatchItemId.SOCIAL_SITES)?.interceptor)
        assertEquals(InterceptorState.RUNNING, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)

        // His suspension (the Safe Break): the blocking pair PAUSES;
        // the listener's capture has no suspension and keeps running.
        WatchStore.updateInterceptorInputs(safeBreak = true)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.PAUSED, snap.measurement(WatchItemId.SHORTS_REELS)?.interceptor)
        assertEquals(InterceptorState.PAUSED, snap.measurement(WatchItemId.SOCIAL_SITES)?.interceptor)
        assertEquals(InterceptorState.RUNNING, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)

        // Setting off: OFF regardless of parents.
        WatchStore.updateInterceptorInputs(shortsOn = false, safeBreak = false)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.OFF, snap.measurement(WatchItemId.SHORTS_REELS)?.interceptor)

        // Parent lost: OFF (never a stale RUNNING).
        WatchStore.updateInterceptorInputs(shortsOn = true)
        WatchStore.onAccessibilityBound(false)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.OFF, snap.measurement(WatchItemId.SHORTS_REELS)?.interceptor)
        assertEquals(InterceptorState.OFF, snap.measurement(WatchItemId.SOCIAL_SITES)?.interceptor)
    }

    /** Issue #48: poke() must actually emit. StateFlow suppresses
     *  equal values, and the old poke assigned a bare copy() —
     *  equal by construction — so no collector was ever notified.
     *  The generation bump makes consecutive snapshots unequal. */
    @Test
    fun poke_actuallyEmits() {
        val before = WatchStore.snapshots.value
        WatchStore.poke()
        val after = WatchStore.snapshots.value
        assertEquals(before.pokeGen + 1, after.pokeGen)
        assertTrue("a poked snapshot must differ from its predecessor", before != after)
        WatchStore.poke()
        assertEquals(after.pokeGen + 1, WatchStore.snapshots.value.pokeGen)
    }

    /** Issue #48: LAST PROVED is the store's one durable fact.
     *  A sweep advances it in this process; process death (the
     *  in-memory reset) must not wipe it back to "never proved" —
     *  the next process's bind restores it from prefs. */
    @Test
    fun lastProved_survivesProcessDeath_viaBind() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("monolith_watch", Context.MODE_PRIVATE)
            .edit().clear().commit()
        try {
            WatchStore.bind(context)
            WatchStore.runSweep { PermState.GRANTED }
            awaitSnapshot { it.lastProvedWallMillis != null }
            val proved = WatchStore.snapshots.value.lastProvedWallMillis

            // Process death: the in-memory store resets to EMPTY…
            WatchStore.resetForTest()
            assertNull(WatchStore.snapshots.value.lastProvedWallMillis)

            // …and the next process's bind restores the fact.
            WatchStore.bind(context)
            assertEquals(proved, WatchStore.snapshots.value.lastProvedWallMillis)
        } finally {
            WatchStore.resetForTest()
            context.getSharedPreferences("monolith_watch", Context.MODE_PRIVATE)
                .edit().clear().commit()
        }
    }
}
