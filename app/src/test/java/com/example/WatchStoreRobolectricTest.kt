package com.example

import com.example.service.InterceptorState
import com.example.service.PermPrint
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

    /** Issue #9, Zahn Z-1: completion is a GENERATION test, not a
     *  wall-clock era test. A sweep whose DEVICE_ADMIN query threw
     *  stamps five of six; a gen-0 lifecycle stamp for DEVICE_ADMIN
     *  then lands with a fresh wall time — inside the old era
     *  test's window, so the era test would have "completed" the
     *  set and advanced LAST PROVED on a stamp that never served
     *  the sweep. The generation test must refuse: the set the
     *  sweep dispatched is not the set that landed. */
    @Test
    fun sweep_gen0StampsCannotCompleteAPendingSweep() {
        WatchStore.runSweep { item ->
            if (item == WatchItemId.DEVICE_ADMIN) {
                throw IllegalStateException("query died")
            }
            true
        }
        // Five of the sweep's own stamps land (generation > 0).
        awaitSnapshot { s ->
            WatchItemId.PERMISSIONS.count { id ->
                (s.items[id]?.sweepGen ?: 0) > 0
            } == 5
        }
        assertNull(WatchStore.snapshots.value.lastProvedWallMillis)
        // The gen-0 lifecycle stamp: fresh wall, wrong generation.
        WatchStore.onDeviceAdminChanged(true)
        val snap = WatchStore.snapshots.value
        assertEquals(
            PermPrint.GRANTED,
            WatchStore.permPrintAt(
                snap, WatchItemId.DEVICE_ADMIN,
                android.os.SystemClock.elapsedRealtime()
            )
        )
        // Measured — but it completes nothing.
        assertNull(snap.lastProvedWallMillis)
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

        // Parents granted, settings on, no suspension: the blocking
        // pair RUNNING. NOTIFICATIONS stays OFF — issue #109, F1:
        // the listener is bound but NO session is active, and the
        // listener captures nothing without one. Binding alone
        // printing RUNNING was the false print.
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
        assertEquals(InterceptorState.OFF, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)

        // A session goes active: now the capture is real and the
        // line may print RUNNING.
        WatchStore.updateInterceptorInputs(sessionActive = true)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.RUNNING, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)

        // His suspension (the Safe Break): the blocking pair PAUSES;
        // the listener's capture has no suspension and keeps running.
        WatchStore.updateInterceptorInputs(safeBreak = true)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.PAUSED, snap.measurement(WatchItemId.SHORTS_REELS)?.interceptor)
        assertEquals(InterceptorState.PAUSED, snap.measurement(WatchItemId.SOCIAL_SITES)?.interceptor)
        assertEquals(InterceptorState.RUNNING, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)

        // The session ends: the line falls back to OFF on the same
        // binding — the capture stopped with the session.
        WatchStore.updateInterceptorInputs(sessionActive = false, safeBreak = false)
        snap = WatchStore.snapshots.value
        assertEquals(InterceptorState.OFF, snap.measurement(WatchItemId.NOTIFICATIONS)?.interceptor)
        WatchStore.updateInterceptorInputs(sessionActive = true)

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
            WatchStore.runSweep { true }
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
