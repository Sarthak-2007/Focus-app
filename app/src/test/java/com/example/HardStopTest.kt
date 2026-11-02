package com.example

import com.example.ui.maximum.HardStop
import com.example.ui.maximum.HardStopSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** BUILD-SR-11 — hard stops: resistance first, then one refusal per attempt. */
class HardStopTest {

    private fun stop() = HardStop(
        HardStopSpec(limit = 100f, approachZone = 10f, maxResistance = 6f, baseResistance = 1f)
    )

    @Test
    fun `resistance ramps through the approach zone toward the limit`() {
        val stop = stop()
        assertEquals(1f, stop.resistanceAt(50f, 0f), 0.001f) // outside the zone
        assertEquals(2.25f, stop.resistanceAt(95f, 0f), 0.001f) // halfway in: quadratic ramp
        assertEquals(6f, stop.resistanceAt(100f, 0f), 0.001f) // at the limit
        assertTrue(stop.inApproachZone(95f))
        assertFalse(stop.inApproachZone(50f))
    }

    @Test
    fun `a push inside the limit lands whole`() {
        val result = stop().push(currentValue = 90f, proposedValue = 95f, velocity = 1f)
        assertFalse(result.refused)
        assertEquals(95f, result.value, 0f)
        assertEquals(1f, result.velocity, 0f)
    }

    @Test
    fun `a push past the limit is refused whole - clamped, velocity dead, one event`() {
        val stop = stop()
        val result = stop.push(currentValue = 95f, proposedValue = 105f, velocity = 1f)
        assertTrue(result.refused)
        assertTrue(result.refusalEvent)
        assertEquals(100f, result.value, 0f)
        assertEquals(0f, result.velocity, 0f)
        assertEquals(1, stop.attempts)
    }

    @Test
    fun `a hand that keeps pressing is not answered again`() {
        val stop = stop()
        stop.push(95f, 105f, 1f)
        val again = stop.push(100f, 105f, 1f)
        assertTrue(again.refused)
        assertFalse(again.refusalEvent) // identical refusal, no second event, no escalation
        assertEquals(1, stop.attempts)
    }

    @Test
    fun `leaving the approach zone re-arms the refusal`() {
        val stop = stop()
        stop.push(95f, 105f, 1f)
        stop.push(100f, 80f, -1f) // the hand retreats out of the zone
        val second = stop.push(80f, 105f, 1f)
        assertTrue(second.refusalEvent)
        assertEquals(2, stop.attempts)
    }

    @Test
    fun `a lower stop refuses downward travel past its limit`() {
        val stop = HardStop(HardStopSpec(limit = 0f, sign = -1, approachZone = 3f, maxResistance = 6f))
        val result = stop.push(currentValue = 2f, proposedValue = -3f, velocity = -1f)
        assertTrue(result.refused)
        assertEquals(0f, result.value, 0f)
    }
}
