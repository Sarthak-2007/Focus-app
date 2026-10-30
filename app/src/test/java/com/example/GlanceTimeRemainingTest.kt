package com.example

import com.example.data.FocusSessionEntity
import com.example.data.formatSessionDuration
import com.example.ui.components.deskClockModeLabel
import com.example.ui.components.sessionEndsAtMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * #127 (glance-time-remaining), recomposed onto current main: a
 * two-second glance must never meet an unlabelled or mis-formatted
 * time. These pin the scope captions and the duration print every
 * glance surface shares.
 *
 * Recompose notes (verification vs main, comment 6523688620):
 *  - F2's hero seconds are NOT pinned here: main cured the Safe
 *    Break hero in #148 (Deck reads safeBreakSecondsRemaining; the
 *    PR's glanceHeroSeconds was duplicative and was dropped). Only
 *    the scope caption is this issue's.
 *  - F4's labels are NOT the PR's deskClockLabel: main cured them
 *    in #111 as deskClockModeLabel (ELAPSED/REMAINING/SET TIME) —
 *    the assertions below are re-pointed at those strings. Only
 *    formatSessionDuration (the digits) is this issue's.
 *  - F5 is not pinned: #111 superseded the EXIT tap with a
 *    deliberate long-press anywhere; nothing here restores it.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GlanceTimeRemainingTest {

    private fun row(
        blocks: Int = 3,
        blockIndex: Int = 1,
        inBreak: Boolean = false,
        breakRemaining: Int = 0,
        taken: Int = 0,
        elapsed: Int = 0,
        stopwatch: Boolean = false,
        active: Boolean = true
    ) = FocusSessionEntity(
        intention = "GLANCE",
        profileName = "Manual",
        targetDurationSeconds = 3000,
        elapsedSeconds = elapsed,
        startWallTimeMillis = 0L,
        startElapsedRealtimeMillis = 0L,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = inBreak,
        safeBreakSecondsRemaining = breakRemaining,
        breakTakenSeconds = taken,
        isStopwatchMode = stopwatch,
        isActive = active,
        completedSuccessfully = false
    )

    @Test
    fun durationPrint_neverPrintsMinutesPast99() {
        assertEquals("00:00", formatSessionDuration(0))
        assertEquals("00:00", formatSessionDuration(-5))
        assertEquals("50:00", formatSessionDuration(3000))
        // A 3x50 session's span is 170 minutes: H:MM:SS, never "170:00".
        assertEquals("2:50:00", formatSessionDuration(10200))
        assertEquals("1:00:00", formatSessionDuration(3600))
    }

    @Test
    fun caption_multiBlock_labelsBothScopes() {
        // At the start of block 1 of 3x50: block truth 50:00, session
        // truth 2:50:00 — the caption must carry BOTH, labelled.
        val caption = row().glanceCaption
        assertTrue(caption.contains("BLOCK 1/3"))
        assertTrue(caption.contains("50:00 LEFT IN BLOCK"))
        assertTrue(caption.contains("SESSION 2:50:00 LEFT"))
    }

    @Test
    fun caption_singleBlock_isSessionOnly() {
        assertEquals("SESSION 50:00 LEFT", row(blocks = 1).glanceCaption)
    }

    @Test
    fun caption_stopwatch_saysElapsed() {
        assertEquals("ELAPSED · NO SET END", row(stopwatch = true).glanceCaption)
    }

    @Test
    fun caption_duringBreak_labelsBreakAndSession_neverABareZero() {
        // Repository state at a block boundary: elapsed == target, so
        // remainingSeconds == 0 for the whole break (#148 cured the
        // hero's own reading on main). The caption labels both the
        // break and the session:
        // span remaining = 3*3000 + 2*600 - (1*3000 + 0 + (600-450))
        //                = 10200 - 3150 = 7050 = 1:57:30.
        val inBreak = row(inBreak = true, breakRemaining = 450, elapsed = 3000)
        assertEquals(0, inBreak.remainingSeconds)
        assertEquals(
            "BREAK 07:30 LEFT · SESSION 1:57:30 LEFT",
            inBreak.glanceCaption
        )
    }

    @Test
    fun deskClockModeLabel_namesTheDirection_mainStrings() {
        // #111's labels, already on main — re-pointed from the PR's
        // superseded deskClockLabel strings.
        assertEquals("REMAINING", deskClockModeLabel(row()))
        assertEquals("ELAPSED", deskClockModeLabel(row(stopwatch = true)))
        assertEquals("SET TIME", deskClockModeLabel(null))
        assertEquals("SET TIME", deskClockModeLabel(row(active = false)))
    }

    @Test
    fun sessionEnd_isOneFreshDerivation() {
        // Finding 3: end = the caller's fresh now + span remaining,
        // exactly — no minute-boundary rounding anywhere in the math.
        assertEquals(
            1_700_000_000_000L + 10_200_000L,
            sessionEndsAtMillis(1_700_000_000_000L, 10200)
        )
        assertEquals(
            1_700_000_000_000L,
            sessionEndsAtMillis(1_700_000_000_000L, -5)
        )
        // Freshness: two reads 30s apart move ENDS by exactly 30s —
        // a minute-stale frame clock would quantise this to 0 or 60s.
        val delta = sessionEndsAtMillis(1_700_000_030_000L, 600) -
            sessionEndsAtMillis(1_700_000_000_000L, 600)
        assertEquals(30_000L, delta)
    }
}
