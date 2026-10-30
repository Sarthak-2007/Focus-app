package com.example

import com.example.data.WebGateVerdict
import com.example.data.normaliseAllowedHost
import com.example.data.webGateVerdict
import com.example.service.EjectionAction
import com.example.service.EjectionTracker
import com.example.service.BreakEndScheduler
import com.example.service.PermPrint
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.ui.components.BlockingAvailability
import com.example.ui.components.availabilityLineFor
import com.example.ui.components.blockingAvailabilityFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Issue #339 (Freedom, Second-pass findings F1–F4). */
class SecondPass339Test {

    // ---- F1: the Web Gate ---------------------------------------

    @Test
    fun listedDomainBlocksInBothModes() {
        assertEquals(
            WebGateVerdict.BLOCK_LISTED,
            webGateVerdict("https://www.youtube.com/watch", false, emptyList(), listOf("youtube.com"))
        )
        assertEquals(
            WebGateVerdict.BLOCK_LISTED,
            webGateVerdict("https://www.youtube.com/watch", true, listOf("youtube.com"), listOf("youtube.com"))
        )
    }

    @Test
    fun blockAllAllowsOnlyTheAllowlist() {
        val allowed = listOf("khanacademy.org", "wikipedia.org")
        assertEquals(
            WebGateVerdict.ALLOW,
            webGateVerdict("https://www.khanacademy.org/science", true, allowed, emptyList())
        )
        assertEquals(
            WebGateVerdict.BLOCK_ALL,
            webGateVerdict("https://www.reddit.com/r/all", true, allowed, emptyList())
        )
        // Parent-domain rule: a subdomain of an allowed host rides.
        assertEquals(
            WebGateVerdict.ALLOW,
            webGateVerdict("https://en.wikipedia.org/wiki/Cell", true, allowed, emptyList())
        )
        // A lookalike host does NOT ride.
        assertEquals(
            WebGateVerdict.BLOCK_ALL,
            webGateVerdict("https://notwikipedia.org/wiki/Cell", true, allowed, emptyList())
        )
    }

    @Test
    fun gateOffAllowsEverythingUnlisted() {
        assertEquals(
            WebGateVerdict.ALLOW,
            webGateVerdict("https://example.com/", false, emptyList(), emptyList())
        )
    }

    @Test
    fun allowedHostNormalisation() {
        assertEquals("khanacademy.org", normaliseAllowedHost(" https://www.KhanAcademy.org/path "))
        assertEquals("example.com", normaliseAllowedHost("*.example.com"))
        assertNull(normaliseAllowedHost("not a host"))
        assertNull(normaliseAllowedHost("localhost"))
    }

    // ---- F2: ejection escalation --------------------------------

    @Test
    fun firstOffenceBacksRepeatEscalatesHome() {
        EjectionTracker.reset()
        assertEquals(EjectionAction.BACK, EjectionTracker.recordAndDecide("com.a", 1_000))
        assertEquals(EjectionAction.HOME, EjectionTracker.recordAndDecide("com.a", 2_000))
        // A different package starts the ladder over.
        assertEquals(EjectionAction.BACK, EjectionTracker.recordAndDecide("com.b", 3_000))
        EjectionTracker.reset()
    }

    @Test
    fun repeatAfterTheWindowStartsOver() {
        EjectionTracker.reset()
        assertEquals(EjectionAction.BACK, EjectionTracker.recordAndDecide("com.a", 1_000))
        assertEquals(
            EjectionAction.BACK,
            EjectionTracker.recordAndDecide("com.a", 1_000 + EjectionTracker.REPEAT_WINDOW_MS + 1)
        )
        EjectionTracker.reset()
    }

    // ---- F3: break-end dedup ------------------------------------

    @Test
    fun breakEndNotifiesExactlyOncePerInstant() {
        assertTrue(BreakEndScheduler.shouldNotifyBreakEnd(0L, 5_000L))
        assertFalse(BreakEndScheduler.shouldNotifyBreakEnd(5_000L, 5_000L))
        assertTrue(BreakEndScheduler.shouldNotifyBreakEnd(5_000L, 9_000L))
        assertFalse(BreakEndScheduler.shouldNotifyBreakEnd(0L, 0L))
    }

    // ---- F4: the criticality taxonomy ---------------------------

    private fun snapshotWith(states: Map<WatchItemId, PermState>, nowMono: Long): WatchSnapshot =
        WatchSnapshot(
            items = states.mapValues { (item, state) ->
                WatchMeasurement(item = item, perm = state, measuredMono = nowMono, measuredWall = 0L)
            },
            engineHeartbeatMono = nowMono,
            engineHeartbeatWall = 0L,
            engineProtecting = false,
            lastProvedWallMillis = null
        )

    private val allGranted = WatchItemId.PERMISSIONS.associateWith { PermState.GRANTED }

    @Test
    fun allGrantedIsActive() {
        val now = 100_000L
        assertEquals(
            BlockingAvailability.ACTIVE,
            blockingAvailabilityFor(snapshotWith(allGranted, now), now)
        )
        assertEquals("BLOCKING — ACTIVE", availabilityLineFor(snapshotWith(allGranted, now), now)?.text)
    }

    @Test
    fun accessibilityMissingIsDisabledWhateverElseStands() {
        val now = 100_000L
        val states = allGranted + (WatchItemId.ACCESSIBILITY to PermState.MISSING)
        assertEquals(
            BlockingAvailability.DISABLED,
            blockingAvailabilityFor(snapshotWith(states, now), now)
        )
    }

    @Test
    fun cosmeticMissingIsRestrictedNotDisabled() {
        val now = 100_000L
        val states = allGranted + (WatchItemId.DEVICE_ADMIN to PermState.MISSING)
        assertEquals(
            BlockingAvailability.RESTRICTED,
            blockingAvailabilityFor(snapshotWith(states, now), now)
        )
        // Usage access missing degrades the detector — restricted,
        // not disabled: Freedom's taxonomy, exactly.
        val usageStates = allGranted + (WatchItemId.USAGE_ACCESS to PermState.MISSING)
        assertEquals(
            BlockingAvailability.RESTRICTED,
            blockingAvailabilityFor(snapshotWith(usageStates, now), now)
        )
    }

    @Test
    fun unmeasuredAccessibilityMakesNoClaim() {
        val now = 100_000L
        val states = allGranted - WatchItemId.ACCESSIBILITY
        assertNull(blockingAvailabilityFor(snapshotWith(states, now), now))
        assertNull(availabilityLineFor(snapshotWith(states, now), now))
    }
}
