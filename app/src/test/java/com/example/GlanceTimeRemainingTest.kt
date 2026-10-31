package com.example

import com.example.data.FocusSessionEntity
import com.example.data.formatSessionDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * #127 (glance-time-remaining): a two-second glance must never meet an
 * unlabelled or mis-formatted time. These pin the scope captions and
 * the duration print every glance surface shares.
 *
 * Recomposition note (DURCH-C verdict on #127, re-pointed by D-K5-2):
 * the original branch tests for `glanceHeroSeconds` and
 * `deskClockLabel` are GONE on purpose — main cured the break hero by
 * a different mechanism (#148: the Deck hero reads
 * safeBreakSecondsRemaining during a break) and the desk-clock labels
 * by #111 (`deskClockModeLabel`). What remains pinned here is what
 * THIS recomposition lands: the caption, the shared duration print,
 * the shared session-end derivation, and #111's label strings the
 * format port must not regress.
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
        stopwatch: Boolean = false
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
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun durationPrint_neverPrintsMinutesPast99() {
        assertEquals("00:00", formatSessionDuration(0))
        assertEquals("50:00", formatSessionDuration(3000))
        // A 3x50 session's span is 170 minutes: H:MM:SS, never "170:00".
        assertEquals("2:50:00", formatSessionDuration(10200))
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
    fun caption_duringBreak_labelsBreakAndSession() {
        // Repository state at a block boundary: elapsed == target, so
        // remainingSeconds == 0 for the whole break — the caption must
        // never let a glance read that as FINISHED. Span remaining on
        // main's formula: 3*3000 + 2*600 - (1*3000 + 0 + (600-450))
        // = 7050 = 1:57:30.
        val inBreak = row(inBreak = true, breakRemaining = 450, elapsed = 3000)
        assertEquals(0, inBreak.remainingSeconds)
        assertEquals(
            "BREAK 07:30 LEFT · SESSION 1:57:30 LEFT",
            inBreak.glanceCaption
        )
    }

    @Test
    fun deskClockModeLabel_namesTheDirection() {
        // Re-pointed at main's #111 label function (the branch's
        // deskClockLabel is superseded and must not return).
        assertEquals(
            "ELAPSED",
            com.example.ui.components.deskClockModeLabel(row(stopwatch = true))
        )
        assertEquals(
            "REMAINING",
            com.example.ui.components.deskClockModeLabel(row())
        )
        assertEquals(
            "SET TIME",
            com.example.ui.components.deskClockModeLabel(null)
        )
    }

    @Test
    fun sessionEnd_isOneFreshDerivation() {
        // Finding 3: end = the caller's fresh now + span remaining,
        // exactly — no minute-boundary rounding anywhere in the math.
        assertEquals(
            1_700_000_000_000L + 10_200_000L,
            com.example.ui.components.sessionEndsAtMillis(1_700_000_000_000L, 10200)
        )
        assertEquals(
            1_700_000_000_000L,
            com.example.ui.components.sessionEndsAtMillis(1_700_000_000_000L, -5)
        )
    }
}
