package com.example

import com.example.ui.views.SlideGestureMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * R5-0723 (issue #277): the velocity-handoff cure, asserted at the
 * pure-math level — SlideGestureMath.springStep is the one step
 * every return spring now takes, seeded with the motion it
 * interrupts. The view's modes keep their own constants; what is
 * proved here is the step's behaviour: a seed is conserved into
 * the first frame, substepping makes the trajectory frame-rate
 * independent (the raw 50ms step was not), and a seeded spring
 * travels with the hand before it comes home.
 */
class SlideHandoffMathTest {

    /** Integrate (x, v) for [seconds] in frames of [frameSec],
     *  substepping each frame at ≤5ms — stepMachine's discipline. */
    private fun integrateSubstepped(
        x0: Float, v0: Float, stiffness: Float, damping: Float,
        seconds: Float, frameSec: Float
    ): Pair<Float, Float> {
        var x = x0
        var v = v0
        var t = 0f
        while (t < seconds - 1e-6f) {
            var remaining = frameSec
            while (remaining > 1e-6f) {
                val h = minOf(remaining, 0.005f)
                val (nx, nv) = SlideGestureMath.springStep(x, v, 0f, stiffness, damping, h)
                x = nx
                v = nv
                remaining -= h
            }
            t += frameSec
        }
        return x to v
    }

    /** The pre-cure refusal integration: one raw step per frame. */
    private fun integrateRaw(
        x0: Float, v0: Float, stiffness: Float, damping: Float,
        seconds: Float, frameSec: Float
    ): Pair<Float, Float> {
        var x = x0
        var v = v0
        var t = 0f
        while (t < seconds - 1e-6f) {
            val (nx, nv) = SlideGestureMath.springStep(x, v, 0f, stiffness, damping, frameSec)
            x = nx
            v = nv
            t += frameSec
        }
        return x to v
    }

    @Test
    fun springStepConservesTheSeedIntoTheFirstFrame() {
        // A hand moving at +2 frac/s hands the spring its motion:
        // the first substep travels WITH the seed before the spring
        // pulls home. Seeded at velocity zero (the pre-cure state
        // on every exit) this step could only ever move backward.
        val (x, _) = SlideGestureMath.springStep(0.5f, 2f, 0f, 225f, 18f, 0.005f)
        assertTrue("seeded step must move with the hand first (x=$x)", x > 0.5f)
    }

    @Test
    fun springStepFromRestMovesTowardTheTarget() {
        val (x, v) = SlideGestureMath.springStep(0.5f, 0f, 0f, 260f, 25.2f, 0.005f)
        assertTrue(x < 0.5f)
        assertTrue(v < 0f)
    }

    @Test
    fun substeppedRefusalTrajectoryIsFrameRateIndependent() {
        // Fen's refusal spring (260 / 25.2), thrown at +2 frac/s.
        val at50 = integrateSubstepped(0.65f, 2f, 260f, 25.2f, 0.30f, 0.050f)
        val at33 = integrateSubstepped(0.65f, 2f, 260f, 25.2f, 0.30f, 0.033f)
        val at16 = integrateSubstepped(0.65f, 2f, 260f, 25.2f, 0.30f, 0.016f)
        assertEquals(at50.first, at33.first, 0.01f)
        assertEquals(at50.first, at16.first, 0.01f)
    }

    @Test
    fun rawFrameStepDisagreesWithTheSubsteppedSpring() {
        // The defect, measured: one raw 50ms step per frame (the
        // pre-cure REFUSAL_RETURN) integrates a different spring
        // than the same constants substepped.
        val raw = integrateRaw(0.65f, 2f, 260f, 25.2f, 0.30f, 0.050f)
        val sub = integrateSubstepped(0.65f, 2f, 260f, 25.2f, 0.30f, 0.050f)
        assertTrue(
            "raw and substepped must measurably differ (raw=${raw.first}, sub=${sub.first})",
            abs(raw.first - sub.first) > 0.01f
        )
    }

    @Test
    fun seededSpringBackTravelsWithTheHandThenSettlesHome() {
        // The spring-back's constants (225 / 18), released at 0.6
        // with the hand still travelling at +3 frac/s: the cap
        // first goes the hand's way, then the spring brings it
        // home and it settles inside two seconds.
        var x = 0.6f
        var v = 3f
        var maxX = x
        var settled = false
        var t = 0f
        while (t < 2f) {
            var remaining = 0.016f
            while (remaining > 1e-6f) {
                val h = minOf(remaining, 0.005f)
                val (nx, nv) = SlideGestureMath.springStep(x, v, 0f, 225f, 18f, h)
                x = nx
                v = nv
                remaining -= h
            }
            if (x > maxX) maxX = x
            t += 0.016f
            if (SlideGestureMath.springSettled(x, v)) {
                settled = true
                break
            }
        }
        assertTrue("cap must first travel with the hand (maxX=$maxX)", maxX > 0.6f)
        assertTrue("seeded spring-back must settle home", settled)
    }

    @Test
    fun springSettledThresholdsAreTheShippedOnes() {
        assertTrue(SlideGestureMath.springSettled(0.0005f, 0.04f))
        assertTrue(SlideGestureMath.springSettled(-0.01f, 0f))
        assertFalse(SlideGestureMath.springSettled(0.002f, 0f))
        assertFalse(SlideGestureMath.springSettled(0.0005f, 0.06f))
    }
}
