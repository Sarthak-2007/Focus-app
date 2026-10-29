package com.example

import com.example.ui.PeekWithheld
import com.example.ui.components.airlockCountdownRemaining
import com.example.ui.components.cooldownRemainingSeconds
import com.example.ui.components.peekCooldownPrint
import com.example.ui.components.peekUnlocked
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #55 — airlock-overlays audit cures. Every test discriminates:
 * it fails on the pre-cure behaviour described in AUDIT-R1 slice
 * "airlock-overlays" and passes on the cure.
 */
class AirlockOverlayIssue55Test {

    // Finding 3 (HIGH): the cooldown trap — "AGAIN IN 0 MIN" and a
    // peek that never unlocks in a standing airlock.
    @Test
    fun cooldownPrintNeverDegradesToZeroMinutes() {
        assertEquals("PEEK COOLDOWN — OVER", peekCooldownPrint(0))
        assertEquals("PEEK COOLDOWN — OVER", peekCooldownPrint(-30))
        // The pre-existing ceiling contract for live cooldowns is intact.
        assertEquals("PEEK COOLDOWN — AGAIN IN 1 MIN", peekCooldownPrint(1))
        assertEquals("PEEK COOLDOWN — AGAIN IN 1 MIN", peekCooldownPrint(60))
        assertEquals("PEEK COOLDOWN — AGAIN IN 2 MIN", peekCooldownPrint(61))
        assertEquals("PEEK COOLDOWN — AGAIN IN 15 MIN", peekCooldownPrint(900))
    }

    @Test
    fun peekUnlocksWhenCooldownExpiresInStandingAirlock() {
        // Frozen snapshot said canUse=false purely because of cooldown;
        // the moment the deadline-derived remainder hits 0 it unlocks.
        assertTrue(peekUnlocked(false, PeekWithheld.COOLDOWN, 0))
        assertFalse(peekUnlocked(false, PeekWithheld.COOLDOWN, 1))
        assertFalse(peekUnlocked(false, PeekWithheld.COOLDOWN, 900))
    }

    @Test
    fun peekNeverUnlocksLocallyForOtherWithheldReasons() {
        assertFalse(peekUnlocked(false, PeekWithheld.ALREADY_ACTIVE, 0))
        assertFalse(peekUnlocked(false, PeekWithheld.PEEK_OFF, 0))
        assertFalse(peekUnlocked(false, null, 0))
        assertTrue(peekUnlocked(true, null, 0))
        assertTrue(peekUnlocked(true, PeekWithheld.COOLDOWN, 42))
    }

    @Test
    fun cooldownRemainingIsDeadlineDerived() {
        val deadline = 1_000_000L
        assertEquals(0, cooldownRemainingSeconds(deadline, deadline))
        assertEquals(0, cooldownRemainingSeconds(deadline, deadline + 5_000L))
        assertEquals(1, cooldownRemainingSeconds(deadline, deadline - 1L))
        assertEquals(1, cooldownRemainingSeconds(deadline, deadline - 1_000L))
        assertEquals(2, cooldownRemainingSeconds(deadline, deadline - 1_001L))
        assertEquals(900, cooldownRemainingSeconds(deadline, deadline - 900_000L))
    }

    // Finding 5 (MEDIUM): the 5s wall is per-intercept and
    // deadline-based — a fresh start instant always yields the full
    // wall, and drift cannot stretch it.
    @Test
    fun countdownIsDeadlineDerivedFromInterceptStart() {
        val start = 500_000L
        assertEquals(5, airlockCountdownRemaining(start, start))
        assertEquals(5, airlockCountdownRemaining(start, start + 999L))
        assertEquals(4, airlockCountdownRemaining(start, start + 1_000L))
        assertEquals(1, airlockCountdownRemaining(start, start + 4_999L))
        assertEquals(0, airlockCountdownRemaining(start, start + 5_000L))
        assertEquals(0, airlockCountdownRemaining(start, start + 3_600_000L))
        // Clock skew backwards never inflates the wall beyond 5.
        assertEquals(5, airlockCountdownRemaining(start, start - 2_000L))
        // A NEW intercept (new start instant) restarts the full wall —
        // the same-package re-intercept case that used to inherit 0.
        val reIntercept = start + 60_000L
        assertEquals(5, airlockCountdownRemaining(reIntercept, reIntercept))
    }
}
