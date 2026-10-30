package com.example

import com.example.data.AppBlockTier
import com.example.data.ForegroundDoor
import com.example.data.LoadoutRules
import com.example.data.SafetyFloor
import com.example.data.SmartLoadout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #332 (Audit R5 supplement — Flipd, second-pass):
 * F5 — the door safety floor (dialer / telecom / IME / emergency
 * are never intercepted; writer-side the dialler floor is
 * EssentialApps #119, and the SLEEP loadout lands the dialler
 * OPEN through it); F1 — the shield fallback door's foreground
 * decision over the UsageEvents stream (latest foreground event
 * wins; a settled blocked app stays visible; an allowed app in
 * front clears an earlier blocked one); F2 — floor/call surfaces
 * are exempt per-package, never a whole-door holiday.
 */
class SafetyFloorForegroundDoorTest {

    @Test
    fun floorCoversDialerTelecomAndKeyboards() {
        assertTrue(SafetyFloor.isFloorPackage("com.google.android.dialer"))
        assertTrue(SafetyFloor.isFloorPackage("com.android.incallui"))
        assertTrue(SafetyFloor.isFloorPackage("com.android.server.telecom"))
        assertTrue(SafetyFloor.isFloorPackage("com.google.android.inputmethod.latin"))
        assertTrue(SafetyFloor.isFloorPackage("com.samsung.android.honeyboard"))
        assertTrue(SafetyFloor.isFloorPackage("com.example.custom.dialer"))
    }

    @Test
    fun floorNeverExemptsContentOrCommerce() {
        // The Flipd contrast (#329 F4): Spotify, ride-hailing and
        // payments stay the user's to lock — floor is safety only.
        assertFalse(SafetyFloor.isFloorPackage("com.spotify.music"))
        assertFalse(SafetyFloor.isFloorPackage("com.ubercab"))
        assertFalse(SafetyFloor.isFloorPackage("com.google.android.apps.walletnfcrel"))
        assertFalse(SafetyFloor.isFloorPackage("com.instagram.android"))
    }

    @Test
    fun sleepLoadoutCanNoLongerLockTheDialer() {
        // Writer-side floor (#119 EssentialApps, consumed by
        // LoadoutRules): the dialler is an OPEN essential in SLEEP.
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutRules.tierFor(
                SmartLoadout.SLEEP, "com.google.android.dialer", "Phone", "TOOLS"
            )
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.SLEEP, "com.instagram.android", "Instagram", "SOCIAL"
            )
        )
    }

    private fun fg(pkg: String, at: Long) =
        ForegroundDoor.FgEvent(pkg, ForegroundDoor.TYPE_FOREGROUND, at)

    @Test
    fun settledBlockedAppStaysVisible() {
        // Opened 19 s ago and never left: the old lastTimeUsed door
        // went blind after 8 s; the event door still sees it.
        val events = listOf(fg("com.instagram.android", 1_000L))
        assertEquals(
            "com.instagram.android",
            ForegroundDoor.foregroundBlocked(events, setOf("com.instagram.android"))
        )
    }

    @Test
    fun allowedAppInFrontClearsEarlierBlocked() {
        val events = listOf(
            fg("com.instagram.android", 1_000L),
            fg("org.khanacademy", 5_000L)
        )
        assertNull(
            ForegroundDoor.foregroundBlocked(events, setOf("com.instagram.android"))
        )
    }

    @Test
    fun backgroundEventsDoNotCountAsForeground() {
        val events = listOf(
            ForegroundDoor.FgEvent("com.instagram.android", 2, 9_000L),
            fg("org.khanacademy", 1_000L)
        )
        assertNull(
            ForegroundDoor.foregroundBlocked(events, setOf("com.instagram.android"))
        )
    }

    @Test
    fun callSurfaceInFrontIsExemptButLockStaysUpForOthers() {
        val duringCall = listOf(fg("com.google.android.dialer", 2_000L))
        assertNull(
            ForegroundDoor.foregroundBlocked(
                duringCall, setOf("com.google.android.dialer", "com.instagram.android")
            )
        )
        // Same window, user swipes to a blocked app mid-call: bounced.
        val swipedMidCall = duringCall + fg("com.instagram.android", 4_000L)
        assertEquals(
            "com.instagram.android",
            ForegroundDoor.foregroundBlocked(
                swipedMidCall, setOf("com.google.android.dialer", "com.instagram.android")
            )
        )
    }
}
