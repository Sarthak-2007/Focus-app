package com.example

import com.example.ui.guard.GuardInks
import com.example.ui.guard.GuardWheel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P7 (BUILD-SR-5) — THE GUARD WHEEL'S CRAFT + TOKENS, pure JVM.
 *
 * The wheel's feel as figures: five-minute detents, an hour a
 * turn, a fling that decays and dies inside its bounds, a settle
 * spring that is firm and never bouncy. The figures themselves
 * are UNPROVEN as feel (judged in the hand at Slice 7/8); what
 * these tests prove is their CHARACTER — the shape the maximum
 * pass tunes, never re-founds. Palette: the seven inks only,
 * never green, read from the lane token census itself.
 */
class GuardCraftP7Test {

    @Test
    fun detents_areFiveMinutes_anHourToTheTurn() {
        assertEquals(5, GuardWheel.STEP_MINUTES)
        assertEquals(30.0, GuardWheel.DEGREES_PER_DETENT, 0.0)
        assertEquals(60, GuardWheel.minutesForDetent(12))
        assertEquals(360.0, GuardWheel.rotationForDetent(12), 0.0)
        assertEquals(6, GuardWheel.detentIndexFor(30))
        // An off-grid standing figure starts from its nearest stop.
        assertEquals(6, GuardWheel.detentIndexFor(32))
        assertEquals(7, GuardWheel.detentIndexFor(33))
    }

    @Test
    fun fling_travelsDecaysAndDiesInsideItsBounds() {
        // A firm flick: ~3 detents of travel, then rest.
        val rest = GuardWheel.flingRestDetent(
            fromIndex = 6, velocityDetentsPerSecond = 9.6, minIndex = 0, maxIndex = 12
        )
        assertEquals(9, rest)
        // A violent flick still dies at the wheel's edge.
        val edged = GuardWheel.flingRestDetent(
            fromIndex = 6, velocityDetentsPerSecond = 200.0, minIndex = 0, maxIndex = 12
        )
        assertEquals(12, edged)
        // Backwards past zero stops at nothing.
        val floored = GuardWheel.flingRestDetent(
            fromIndex = 2, velocityDetentsPerSecond = -50.0, minIndex = 0, maxIndex = 12
        )
        assertEquals(0, floored)
        // Below the fling floor there is no travel at all.
        val still = GuardWheel.flingRestDetent(
            fromIndex = 6, velocityDetentsPerSecond = 0.3, minIndex = 0, maxIndex = 12
        )
        assertEquals(6, still)
    }

    @Test
    fun settleEnvelope_decaysToRest() {
        assertEquals(1.0, GuardWheel.settleEnvelope(0.0), 0.001)
        assertTrue(GuardWheel.settleEnvelope(1.0) < 0.05)
        assertTrue(GuardWheel.settleEnvelope(2.0) < GuardWheel.settleEnvelope(1.0))
        // The spring figures: firm, damped below the bounce line.
        assertTrue(GuardWheel.SETTLE_STIFFNESS >= 200f)
        assertTrue(GuardWheel.SETTLE_DAMPING_RATIO in 0.7f..1.0f)
    }

    @Test
    fun palette_isExactlyTheSevenInks_neverGreen() {
        assertEquals(7, GuardInks.size)
        assertEquals(7, GuardInks.distinct().size)
        GuardInks.forEach { ink ->
            assertTrue(
                "an ink whose green dominates is not in this palette",
                ink.green < maxOf(ink.red, ink.blue)
            )
        }
    }
}
