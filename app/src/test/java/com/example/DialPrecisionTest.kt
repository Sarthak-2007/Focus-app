package com.example

import com.example.ui.views.DialPrecision
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #123 (dial-precision): the wheel's precision law, proven on
 * the pure math the view consults.
 *
 * 1. Every whole minute is a stop — 47 exists.
 * 5. A value the app itself seeded (47) reads back as itself at its
 *    own angle — touching the wheel cannot round it away.
 * 3. Boundary hysteresis: trembling on a midpoint holds the value.
 * 4. One pitch ladder: monotonic, and the endpoints are the filed
 *    340Hz..880Hz.
 */
class DialPrecisionTest {

    @Test
    fun everyWholeMinuteIsAStop() {
        for (m in 10..180) {
            assertEquals("minute $m must read back as itself", m, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(m)))
        }
    }

    @Test
    fun fortySevenSpecifically() {
        assertEquals(47, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(47)))
        assertEquals(47, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(47), current = 47))
    }

    @Test
    fun flowAndShores() {
        assertEquals(5, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(5)))
        assertEquals(5, DialPrecision.valueForAngle(110f))
        assertEquals(10, DialPrecision.valueForAngle(126f))
        assertEquals(180, DialPrecision.valueForAngle(432f))
        // Beyond the ends clamps, never wraps.
        assertEquals(180, DialPrecision.valueForAngle(500f))
    }

    @Test
    fun hysteresisHoldsValueAgainstTremble() {
        // The 45/46 midpoint sits at raw 45.5 minutes. Trembling
        // +/-0.15 minutes around it must NOT toggle a held 45 or 46.
        val midAngle = DialPrecision.angleForMinutes(45) + 0.5f * DialPrecision.DEG_PER_MIN
        val tremble = listOf(-0.27f, -0.1f, 0f, 0.1f, 0.27f) // degrees
        for (d in tremble) {
            assertEquals("held 45 survives tremble $d", 45, DialPrecision.valueForAngle(midAngle + d, current = 45))
            assertEquals("held 46 survives tremble $d", 46, DialPrecision.valueForAngle(midAngle + d, current = 46))
        }
    }

    @Test
    fun hysteresisYieldsToADeliberateCrossing() {
        // A full minute past the midpoint is a decision, not tremble.
        val past46 = DialPrecision.angleForMinutes(46) + 0.4f
        assertEquals(46, DialPrecision.valueForAngle(past46, current = 45))
        val below45 = DialPrecision.angleForMinutes(45) - 0.4f
        assertEquals(45, DialPrecision.valueForAngle(below45, current = 46))
        // Multi-minute drags still travel the full distance.
        assertEquals(90, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(90), current = 45))
        assertEquals(20, DialPrecision.valueForAngle(DialPrecision.angleForMinutes(20), current = 90))
    }

    @Test
    fun onePitchLadderMonotonicWithFiledEndpoints() {
        assertEquals(340f, DialPrecision.detentPitchHz(5), 0.01f)
        assertEquals(880f, DialPrecision.detentPitchHz(180), 0.01f)
        var prev = Float.NEGATIVE_INFINITY
        for (m in 5..180) {
            val p = DialPrecision.detentPitchHz(m)
            assertTrue("pitch must rise with value ($m)", p > prev)
            prev = p
        }
    }
}
