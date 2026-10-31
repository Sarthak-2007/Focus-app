package com.example

import com.example.data.BreakDimState
import com.example.data.NightDimMath
import com.example.service.DndGate
import com.example.ui.breakDimAlphaAt
import com.example.ui.components.deskClockEyebrow
import com.example.ui.components.deskClockSealedLines
import com.example.ui.components.sealedYieldLine
import android.app.NotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #143 (night-flip-mid-session) — the six findings' pure
 * machinery. Every test discriminates: it fails on the pre-cure
 * behaviour (no dim progression anywhere, no fade at break end,
 * one desk-clock face for sealed and idle, a Yield sheet with no
 * sealed-state answer, arming that never consulted DND, no
 * monochrome anywhere) and passes on the cure.
 */
class NightDimIssue143Test {

    // ---------- F6: dimming progression INTO the break ----------

    @Test
    fun dimStartsAtZeroAndRampsOverSixtySeconds() {
        assertEquals(0f, NightDimMath.dimAlphaIntoBreak(0), 0.001f)
        assertEquals(0f, NightDimMath.dimAlphaIntoBreak(-5), 0.001f)
        // Half-way down the ramp: half the depth.
        assertEquals(
            NightDimMath.MAX_DIM_ALPHA * 0.5f,
            NightDimMath.dimAlphaIntoBreak(30), 0.001f
        )
        // Ramp complete at 60s, then it HOLDS — never deeper.
        assertEquals(NightDimMath.MAX_DIM_ALPHA, NightDimMath.dimAlphaIntoBreak(60), 0.001f)
        assertEquals(NightDimMath.MAX_DIM_ALPHA, NightDimMath.dimAlphaIntoBreak(599), 0.001f)
    }

    @Test
    fun dimIsMonotonicAcrossTheWholeBreak() {
        var previous = -1f
        for (elapsed in 0..600 step 7) {
            val alpha = NightDimMath.dimAlphaIntoBreak(elapsed)
            assertTrue("alpha regressed at ${elapsed}s", alpha >= previous)
            previous = alpha
        }
    }

    // ---------- F1: fade OUT at break end ----------

    @Test
    fun fadeOutDecaysFromTheReachedDepthToZeroOverFourSeconds() {
        val from = NightDimMath.MAX_DIM_ALPHA
        assertEquals(from, NightDimMath.dimAlphaOutOfBreak(0L, from), 0.001f)
        assertEquals(from, NightDimMath.dimAlphaOutOfBreak(-100L, from), 0.001f)
        assertEquals(from * 0.5f, NightDimMath.dimAlphaOutOfBreak(2_000L, from), 0.001f)
        assertEquals(0f, NightDimMath.dimAlphaOutOfBreak(4_000L, from), 0.001f)
        assertEquals(0f, NightDimMath.dimAlphaOutOfBreak(60_000L, from), 0.001f)
    }

    @Test
    fun fadeOutFromAnEarlyEndedBreakStartsShallower() {
        // A break ended at 15s had only reached a quarter depth;
        // its fade starts THERE, not at the maximum.
        val shallow = NightDimMath.dimAlphaIntoBreak(15)
        assertTrue(shallow < NightDimMath.MAX_DIM_ALPHA)
        assertEquals(shallow, NightDimMath.dimAlphaOutOfBreak(0L, shallow), 0.001f)
        assertEquals(0f, NightDimMath.dimAlphaOutOfBreak(4_000L, shallow), 0.001f)
    }

    @Test
    fun breakDimAlphaAtReadsThePublishedState() {
        val inBreak = BreakDimState(inBreak = true, breakElapsedSeconds = 60)
        assertEquals(NightDimMath.MAX_DIM_ALPHA, breakDimAlphaAt(inBreak, 10_000L), 0.001f)
        val fading = BreakDimState(
            inBreak = false, breakEndedMono = 10_000L,
            fadeFromAlpha = NightDimMath.MAX_DIM_ALPHA
        )
        assertEquals(
            NightDimMath.MAX_DIM_ALPHA * 0.5f,
            breakDimAlphaAt(fading, 12_000L), 0.001f
        )
        assertEquals(0f, breakDimAlphaAt(BreakDimState(), 12_000L), 0.001f)
    }

    // ---------- F5: monochrome rides the same curve ----------

    @Test
    fun saturationDrainsExactlyWithTheDim() {
        assertEquals(1f, NightDimMath.saturationForDim(0f), 0.001f)
        assertEquals(0.5f, NightDimMath.saturationForDim(NightDimMath.MAX_DIM_ALPHA * 0.5f), 0.001f)
        assertEquals(0f, NightDimMath.saturationForDim(NightDimMath.MAX_DIM_ALPHA), 0.001f)
        // Out-of-range inputs clamp instead of going negative.
        assertEquals(0f, NightDimMath.saturationForDim(2f), 0.001f)
        assertEquals(1f, NightDimMath.saturationForDim(-1f), 0.001f)
    }

    @Test
    fun windowBrightnessStepsDownWithTheDim() {
        assertEquals(1f, NightDimMath.windowBrightnessForDim(0f), 0.001f)
        assertEquals(
            NightDimMath.MIN_WINDOW_BRIGHTNESS,
            NightDimMath.windowBrightnessForDim(NightDimMath.MAX_DIM_ALPHA), 0.001f
        )
    }

    // ---------- F2: the sealed desk-clock face differs ----------

    @Test
    fun deskClockEyebrowNamesTheStateTruthfully() {
        assertEquals("NIGHTSTAND DESK CLOCK • HOLD ANYWHERE TO EXIT", deskClockEyebrow(sealed = false, inBreak = false))
        assertEquals("SEALED — NIGHT FACE • HOLD ANYWHERE TO EXIT", deskClockEyebrow(sealed = true, inBreak = false))
        assertEquals("SEALED — SAFE BREAK • HOLD ANYWHERE TO EXIT", deskClockEyebrow(sealed = true, inBreak = true))
    }

    @Test
    fun deskClockSealedLinesCarryElapsedAndRemaining() {
        val lines = deskClockSealedLines(
            sealed = true,
            elapsedSeconds = 754, remainingSeconds = 2246
        )
        assertEquals(listOf("ELAPSED 12:34", "REMAINING 37:26"), lines)
        // Idle: no session numbers are fabricated — one honest line.
        assertEquals(
            listOf("NO SESSION SEALED — THIS IS THE DIAL, NOT A SESSION"),
            deskClockSealedLines(false, 0, 0)
        )
    }

    @Test
    fun deskClockSaturationIsDrainedOnlyWhenSealed() {
        assertEquals(1f, NightDimMath.deskClockSaturation(sealed = false, dimAlpha = 0.5f), 0.001f)
        assertEquals(0.25f, NightDimMath.deskClockSaturation(sealed = true, dimAlpha = 0f), 0.001f)
        assertEquals(0f, NightDimMath.deskClockSaturation(sealed = true, dimAlpha = NightDimMath.MAX_DIM_ALPHA), 0.001f)
    }

    // ---------- F3: the sealed Yield sheet answers as sealed ----------

    @Test
    fun sealedYieldLineSpeaksInTheSealedStatesOwnWords() {
        assertEquals(
            "SEALED — BLOCK 2 OF 4 · CALCULATOR STAYS HELD UNTIL THIS BLOCK ENDS",
            sealedYieldLine("Calculator", inBreak = false, breakRemainingSeconds = 0, blockIndex = 2, blockCount = 4)
        )
        assertEquals(
            "SEALED — SAFE BREAK RUNNING · 07:11 OF QUIET LEFT · CALCULATOR STAYS HELD UNTIL THE NEXT BLOCK SEALS",
            sealedYieldLine("Calculator", inBreak = true, breakRemainingSeconds = 431, blockIndex = 2, blockCount = 4)
        )
        // A stopwatch session has no block count to name; the line
        // falls back to the session, never an invented block.
        assertEquals(
            "SEALED — CALCULATOR STAYS HELD UNTIL THE SESSION ENDS",
            sealedYieldLine("Calculator", inBreak = false, breakRemainingSeconds = 0, blockIndex = 0, blockCount = 0)
        )
    }

    // ---------- F4: arming consults Do-Not-Disturb ----------

    @Test
    fun dndDecisionMatrixIsTruthful() {
        // Already quiet by the user's own hand — any DND flavour.
        assertEquals(
            DndGate.Decision.ALREADY_QUIET,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_NONE, policyAccessGranted = false)
        )
        assertEquals(
            DndGate.Decision.ALREADY_QUIET,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_ALARMS, policyAccessGranted = false)
        )
        assertEquals(
            DndGate.Decision.ALREADY_QUIET,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_PRIORITY, policyAccessGranted = true)
        )
        // Ringing phone: engage only with the grant.
        assertEquals(
            DndGate.Decision.ENGAGE_ON_SEAL,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_ALL, policyAccessGranted = true)
        )
        assertEquals(
            DndGate.Decision.CANNOT_ENGAGE_NO_ACCESS,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_ALL, policyAccessGranted = false)
        )
        // Unreadable state is UNKNOWN, never a guess.
        assertEquals(
            DndGate.Decision.UNKNOWN,
            DndGate.decide(NotificationManager.INTERRUPTION_FILTER_UNKNOWN, policyAccessGranted = true)
        )
    }

    @Test
    fun dndLinesSayWhatWillHappenAndAdmitWhatCannot() {
        assertTrue(DndGate.armLine(DndGate.Decision.ALREADY_QUIET)!!.contains("ALREADY ON"))
        assertTrue(DndGate.armLine(DndGate.Decision.ENGAGE_ON_SEAL)!!.contains("RESTORE IT AT THE FINISH"))
        val noAccess = DndGate.armLine(DndGate.Decision.CANNOT_ENGAGE_NO_ACCESS)!!
        assertTrue(noAccess.contains("OFF"))
        assertTrue(noAccess.contains("NO PERMISSION"))
        assertNull(DndGate.armLine(DndGate.Decision.UNKNOWN))
        assertEquals(
            "DO NOT DISTURB: OFF — THE PHONE CAN STILL RING THROUGH THIS BLOCK",
            DndGate.sealedLine(engagedByMonolith = false, currentFilter = NotificationManager.INTERRUPTION_FILTER_ALL)
        )
        assertEquals(
            "DO NOT DISTURB: ON — SET BY MONOLITH FOR THIS BLOCK, RESTORED AT THE FINISH",
            DndGate.sealedLine(engagedByMonolith = true, currentFilter = NotificationManager.INTERRUPTION_FILTER_ALARMS)
        )
    }
}
