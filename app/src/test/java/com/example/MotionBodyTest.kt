package com.example

import com.example.ui.motion.DetentRail
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.MonolithBodies
import com.example.ui.motion.PhysicsBody
import com.example.ui.motion.SpringIntegrator
import com.example.ui.motion.SpringState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MOTION-SR-5 — the physics primitives as executable law: mass,
 * resistance and settle are first-class; P0's landed character is
 * preserved exactly; the integrator settles bodies ON their targets.
 */
class MotionBodyTest {

    // ---------- P0 character, preserved ----------

    @Test
    fun `seeded bodies carry P0 MonolithMotion character exactly`() {
        // (body, P0 dampingRatio, P0 stiffness) from BUILD-SR-1's MonolithMotion, PR #842.
        val cases = listOf(
            Triple(MonolithBodies.Bolt, 0.92f, 900f),
            Triple(MonolithBodies.Knock, 1.0f, 2600f),
            Triple(MonolithBodies.Drum, 0.78f, 420f),
            Triple(MonolithBodies.DoorWeight, 0.86f, 55f),
            Triple(MonolithBodies.Wheel, 0.65f, 180f),
            Triple(MonolithBodies.Crank, 0.72f, 300f)
        )
        for ((body, zeta, stiffness) in cases) {
            assertEquals(zeta, body.dampingRatio, 0.001f)
            assertEquals(stiffness, body.normalisedStiffness, 0.5f)
            val spec = body.toSpringSpec()
            assertEquals(zeta, spec.dampingRatio, 0.001f)
            assertEquals(stiffness, spec.stiffness, 0.5f)
        }
    }

    @Test
    fun `character is mass-invariant - the same motion at any mass`() {
        val light = PhysicsBody.fromCharacter(mass = 1f, dampingRatio = 0.8f, normalisedStiffness = 300f)
        val heavy = PhysicsBody.fromCharacter(mass = 5f, dampingRatio = 0.8f, normalisedStiffness = 300f)
        assertEquals(
            SpringIntegrator.valueAt(0f, 1f, light, 0.3f),
            SpringIntegrator.valueAt(0f, 1f, heavy, 0.3f),
            0.0001f
        )
    }

    // ---------- The integrator ----------

    @Test
    fun `every seeded body settles exactly on its target`() {
        val bodies = listOf(
            MonolithBodies.Bolt, MonolithBodies.Knock, MonolithBodies.Drum,
            MonolithBodies.DoorWeight, MonolithBodies.Wheel, MonolithBodies.Crank
        )
        for (body in bodies) {
            val (final, seconds) = SpringIntegrator.simulateUntilSettled(0f, 1f, body)
            assertEquals(1f, final.value, 0f)
            assertEquals(0f, final.velocity, 0f)
            assertTrue("body must settle in finite time", seconds in 0.05f..10f)
        }
    }

    @Test
    fun `the knock never overshoots and the drum does`() {
        // Critically damped (ζ=1): monotone approach, no value beyond target.
        var knockMax = 0f
        for (i in 0..40) {
            knockMax = maxOf(knockMax, SpringIntegrator.valueAt(0f, 1f, MonolithBodies.Knock, i * 0.02f))
        }
        assertTrue("knock overshot: $knockMax", knockMax <= 1.001f)
        // Under-damped (ζ=0.78): the drum passes its detent before being caught.
        var drumMax = 0f
        for (i in 0..60) {
            drumMax = maxOf(drumMax, SpringIntegrator.valueAt(0f, 1f, MonolithBodies.Drum, i * 0.02f))
        }
        assertTrue("drum should overshoot its target once, got max $drumMax", drumMax > 1.0f)
    }

    @Test
    fun `settle is a band, not a timeout - a body outside its band is not settled`() {
        val body = MonolithBodies.Bolt
        assertTrue(SpringIntegrator.isSettled(SpringState(1f, 0f), 1f, body))
        assertTrue(!SpringIntegrator.isSettled(SpringState(0.5f, 0f), 1f, body))
        assertTrue(!SpringIntegrator.isSettled(SpringState(1f, 5f), 1f, body))
    }

    // ---------- Inertia ----------

    @Test
    fun `inertia decays exponentially and rests where the math says`() {
        val wheel = InertiaBody(mass = 1.2f, resistance = 0.6f) // λ = 0.5 /s
        val start = SpringState(value = 0f, velocity = 4f)
        val later = wheel.step(start, 1f)
        assertEquals(4f * kotlin.math.exp(-0.5f), later.velocity, 0.001f)
        assertEquals(wheel.restValue(start), 8f, 0.001f)
        // A long coast lands on the rest value.
        val coasted = wheel.step(start, 60f)
        assertEquals(8f, coasted.value, 0.01f)
        assertEquals(0f, coasted.velocity, 0.001f)
    }

    // ---------- Detents ----------

    @Test
    fun `detents capture where the body is heading, not where it was released`() {
        val rail = DetentRail(listOf(0f, 1f, 2f, 3f))
        assertEquals(1f, rail.nearest(1.4f), 0f)
        // Moving fast toward 2, released beside 1: the catch is 2.
        assertEquals(2f, rail.captureTarget(value = 1.3f, velocity = 6f, lookaheadSeconds = 0.12f), 0f)
        // Nearly still beside 1: the catch is 1.
        assertEquals(1f, rail.captureTarget(value = 1.3f, velocity = 0.1f, lookaheadSeconds = 0.12f), 0f)
        assertTrue(rail.shouldCapture(value = 2.01f, velocity = 0.05f, captureRadius = 0.05f, captureVelocity = 0.2f))
        assertTrue(!rail.shouldCapture(value = 2.01f, velocity = 3f, captureRadius = 0.05f, captureVelocity = 0.2f))
    }

    @Test
    fun `invalid physics is refused at construction`() {
        var refused = 0
        try { PhysicsBody(mass = 0f, stiffness = 100f, resistance = 10f) } catch (e: IllegalArgumentException) { refused++ }
        try { PhysicsBody(mass = 1f, stiffness = -1f, resistance = 10f) } catch (e: IllegalArgumentException) { refused++ }
        try { DetentRail(emptyList()) } catch (e: IllegalArgumentException) { refused++ }
        assertEquals(3, refused)
    }
}
