package com.example

import com.example.service.InterceptorState
import com.example.service.PermPrint
import com.example.service.WatchItemId
import com.example.service.WatchStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
        WatchStore.runSweep { item -> item != WatchItemId.DEVICE_ADMIN }
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
        WatchStore.runSweep { true }
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
            true
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
        WatchStore.runSweep { true }
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
}
