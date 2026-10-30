package com.example

import com.example.data.AppBlockTier
import com.example.service.BreakAlert
import com.example.service.BreakAnnouncer
import com.example.service.notificationHeldDuringSession
import com.example.ui.components.deskClockBreakLine
import com.example.ui.screens.breakEyebrowText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R3 · safe-break-lived (issue #104): the break as LIVED —
 * what the board prints, what the listener holds, what announces
 * the boundaries, and what the clocks show — pinned to the truth
 * of the enforcement code.
 */
class SafeBreakTruthTest {

    // ---- #2: held notifications release for break-unlocked apps ----

    @Test
    fun notifications_releaseForPeekAppsDuringBreak_only() {
        // The break unlocks PEEK apps; their notifications flow.
        assertFalse(notificationHeldDuringSession(isInSafeBreak = true, tier = AppBlockTier.PEEK_2M))
        // LOCKED apps stay locked in a break — and stay held.
        assertTrue(notificationHeldDuringSession(isInSafeBreak = true, tier = AppBlockTier.LOCKED))
        // Outside a break, PEEK and LOCKED are both held.
        assertTrue(notificationHeldDuringSession(isInSafeBreak = false, tier = AppBlockTier.PEEK_2M))
        assertTrue(notificationHeldDuringSession(isInSafeBreak = false, tier = AppBlockTier.LOCKED))
        // OPEN is never held, break or not.
        assertFalse(notificationHeldDuringSession(isInSafeBreak = true, tier = AppBlockTier.OPEN))
        assertFalse(notificationHeldDuringSession(isInSafeBreak = false, tier = AppBlockTier.OPEN))
    }

    // ---- #3/#4: the announcer fires once per boundary, in order ----

    @Test
    fun announcer_fullBreakLifecycle() {
        val a = BreakAnnouncer()
        // Work block: silence.
        assertNull(a.onTick(sessionId = 7, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
        // Entry: STARTED once, then silence while the clock runs.
        assertEquals(BreakAlert.STARTED, a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 600))
        assertNull(a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 599))
        assertNull(a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 61))
        // Final minute: warned exactly once.
        assertEquals(BreakAlert.FINAL_MINUTE, a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 60))
        assertNull(a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 30))
        assertNull(a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 1))
        // Expiry: ENDED once, then silence in the next block.
        assertEquals(BreakAlert.ENDED, a.onTick(7, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
        assertNull(a.onTick(7, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
        // The NEXT break of the same session announces afresh.
        assertEquals(BreakAlert.STARTED, a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 600))
        assertEquals(BreakAlert.FINAL_MINUTE, a.onTick(7, isInSafeBreak = true, safeBreakSecondsRemaining = 45))
        assertEquals(BreakAlert.ENDED, a.onTick(7, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
    }

    @Test
    fun announcer_endEarlyStillAnnouncesEnd() {
        val a = BreakAnnouncer()
        assertEquals(BreakAlert.STARTED, a.onTick(3, isInSafeBreak = true, safeBreakSecondsRemaining = 600))
        assertNull(a.onTick(3, isInSafeBreak = true, safeBreakSecondsRemaining = 240))
        // Hand-ended at minute six: ENDED, no final-minute warning owed.
        assertEquals(BreakAlert.ENDED, a.onTick(3, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
    }

    @Test
    fun announcer_midBreakStartAnnouncesWithNoDuplicateFinalWarning() {
        val a = BreakAnnouncer()
        // Shield (re)started with 40 s of break left: STARTED speaks
        // (with the true clock), FINAL_MINUTE does not pile on.
        assertEquals(BreakAlert.STARTED, a.onTick(9, isInSafeBreak = true, safeBreakSecondsRemaining = 40))
        assertNull(a.onTick(9, isInSafeBreak = true, safeBreakSecondsRemaining = 20))
        assertEquals(BreakAlert.ENDED, a.onTick(9, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
    }

    @Test
    fun announcer_newSessionResets() {
        val a = BreakAnnouncer()
        assertEquals(BreakAlert.STARTED, a.onTick(1, isInSafeBreak = true, safeBreakSecondsRemaining = 600))
        // A different session's break is a new break, not a continuation.
        assertEquals(BreakAlert.STARTED, a.onTick(2, isInSafeBreak = true, safeBreakSecondsRemaining = 600))
        assertEquals(BreakAlert.ENDED, a.onTick(2, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
        // Session 1 returning cannot resurrect an ENDED for a break
        // the machine no longer tracks as its own.
        assertNull(a.onTick(1, isInSafeBreak = false, safeBreakSecondsRemaining = 0))
    }

    // ---- #3: the Deck eyebrow counts exact seconds ----

    @Test
    fun deckBreakEyebrow_isSecondPreciseNeverRoundedUp() {
        assertEquals("SAFE BREAK · 10:00 LEFT", breakEyebrowText(600))
        assertEquals("SAFE BREAK · 1:01 LEFT", breakEyebrowText(61))
        assertEquals("SAFE BREAK · 1:00 LEFT", breakEyebrowText(60))
        assertEquals("SAFE BREAK · 0:59 LEFT", breakEyebrowText(59))
        assertEquals("SAFE BREAK · 0:09 LEFT", breakEyebrowText(9))
        assertEquals("SAFE BREAK · 0:00 LEFT", breakEyebrowText(0))
        assertEquals("SAFE BREAK · 0:00 LEFT", breakEyebrowText(-5))
    }

    // ---- #5: the desk clock names the break ----

    @Test
    fun deskClockBreakLine_onlyDuringBreak_andNamesNextBlock() {
        assertNull(deskClockBreakLine(isInSafeBreak = false, nextBlockIndex = 2, totalBlocks = 3))
        assertEquals(
            "SAFE BREAK · BLOCK 2 RESUMES AT 00:00",
            deskClockBreakLine(isInSafeBreak = true, nextBlockIndex = 2, totalBlocks = 3)
        )
        // Never promises a block the session does not have.
        assertEquals(
            "SAFE BREAK · BLOCK 3 RESUMES AT 00:00",
            deskClockBreakLine(isInSafeBreak = true, nextBlockIndex = 99, totalBlocks = 3)
        )
    }
}
