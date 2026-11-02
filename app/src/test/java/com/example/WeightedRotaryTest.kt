package com.example

import com.example.ui.maximum.WeightedRotary
import com.example.ui.maximum.WeightedRotarySpec
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.MonolithBodies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** BUILD-SR-11 — the weighted rotary: governed velocity, governed coast, rest only on stations. */
class WeightedRotaryTest {

    private fun rotary(inertiaResistance: Float = 0.9f): WeightedRotary = WeightedRotary(
        spec = WeightedRotarySpec(
            body = MonolithBodies.Wheel,
            inertia = InertiaBody(mass = MonolithBodies.Wheel.mass, resistance = inertiaResistance),
            maxVelocityDegPerSec = 240f,
            maxCoastDegrees = 30f
        ),
        stationPositions = (0 until 10).map { it * 36f }
    )

    @Test
    fun `the governor caps velocity however hard the hand flings`() {
        val dial = rotary()
        val applied = dial.dragBy(720f, 0.016f)
        assertEquals(240f * 0.016f, applied, 0.001f)
        assertEquals(240f, dial.velocityDegPerSec, 0.001f)
    }

    @Test
    fun `a release rests exactly on a station, never between stations`() {
        val dial = rotary()
        dial.dragBy(50f, 0.5f)
        val release = dial.release()
        val rest = dial.angleDeg % 360f
        assertEquals(release.station, rest, 0.001f)
        assertTrue("rest must be a station multiple of 36: $rest", abs(rest % 36f) < 0.001f || abs(rest % 36f - 36f) < 0.001f)
        assertEquals(0f, dial.velocityDegPerSec, 0f)
    }

    @Test
    fun `the governor brakes a coast that would run past its ceiling`() {
        // A nearly frictionless inertia at full governed velocity would
        // coast hundreds of degrees ungoverned; the ceiling ends it.
        val dial = rotary(inertiaResistance = 0.05f)
        dial.dragBy(240f, 1.0f) // one second at the ceiling: velocity 240
        val start = dial.angleDeg
        val release = dial.release()
        assertTrue(release.governorBraked)
        assertTrue("coast ${release.coastDegrees} must stay at the ceiling", release.coastDegrees <= 31f)
        // And the whole release — coast + settle-to-station — can never
        // run away: at most the ceiling plus half a station spacing.
        assertTrue(abs(dial.angleDeg - start) <= 30f + 18f + 1f)
    }

    @Test
    fun `the same release always rests at the same station`() {
        val first = rotary()
        val second = rotary()
        first.dragBy(73f, 0.7f)
        second.dragBy(73f, 0.7f)
        first.release()
        second.release()
        assertEquals(first.angleDeg, second.angleDeg, 0f)
    }
}
