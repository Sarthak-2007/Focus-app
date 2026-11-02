package com.example

import com.example.ui.maximum.CombinationSpin
import com.example.ui.maximum.SpinEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-11 — the proof piece: FEEL-SR-3's C1 grammar as executable
 * law. Station ladder, gates, THE SEAT, the dead dial — and the two
 * refusals: no error event, and nothing of the combination stored.
 */
class CombinationSpinTest {

    private fun spin(seatAtDeg: Float, seatDirection: Int = 1): Pair<CombinationSpin, MutableList<SpinEvent>> {
        val events = mutableListOf<SpinEvent>()
        return CombinationSpin(seatAtDeg, seatDirection, onEvent = { events += it }) to events
    }

    @Test
    fun `stations tick per number at the first pass's amplitude`() {
        val (spin, events) = spin(seatAtDeg = 3600f)
        spin.rotate(36f, 0.5f)
        val stations = events.filterIsInstance<SpinEvent.Station>()
        assertEquals(1, stations.size)
        assertEquals(70, stations.single().spec.amplitude)
        assertEquals(1, stations.single().pass)
    }

    @Test
    fun `a reversal after real travel fires the gate and thickens the next pass`() {
        val (spin, events) = spin(seatAtDeg = 3600f)
        spin.rotate(72f, 1.0f) // two stations, pass 1
        spin.rotate(-36f, 0.5f) // reverse: the gate, then pass 2 begins
        assertEquals(1, events.filterIsInstance<SpinEvent.Gate>().size)
        assertEquals(165, events.filterIsInstance<SpinEvent.Gate>().single().spec.amplitude)
        assertEquals(35, events.filterIsInstance<SpinEvent.Gate>().single().spec.preSilenceMs)
        assertEquals(2, spin.pass)
        spin.rotate(-36f, 0.5f) // the next station is pass 2's, at its thicker amplitude
        val passTwo = events.filterIsInstance<SpinEvent.Station>().last()
        assertEquals(95, passTwo.spec.amplitude)
        assertEquals(2, passTwo.pass)
    }

    @Test
    fun `the seat - ramp on approach, thud and settle on arrival, dial dead after`() {
        val (spin, events) = spin(seatAtDeg = 72f)
        spin.rotate(60f, 1.0f) // approach, governed speed: the ramp fires in the last 15 degrees
        assertEquals(1, events.filterIsInstance<SpinEvent.SeatRamp>().size)
        // The approach resistance is real: inside the zone the same
        // gesture turns fewer degrees (24 in the hand, 16 on the dial
        // at the zone's entry resistance) — and still arrives.
        spin.rotate(24f, 0.5f) // arrival at governed velocity, correct direction
        assertEquals(1, events.filterIsInstance<SpinEvent.SeatThud>().size)
        assertEquals(235, events.filterIsInstance<SpinEvent.SeatThud>().single().spec.amplitude)
        assertEquals(1, events.filterIsInstance<SpinEvent.SeatSettle>().size)
        assertTrue(spin.seated)
        assertEquals(72f, spin.angleDeg, 0f) // the dial rests ON the seat, exactly
    }

    @Test
    fun `a flung crossing of the seat's station does not seat - the missing seat is the message`() {
        val (spin, events) = spin(seatAtDeg = 72f)
        spin.rotate(72f, 0.2f) // far above the seat's governed velocity
        assertFalse(spin.seated)
        // And nothing but ordinary station ticks was said: no thud, no
        // settle, and — by the event type's construction — no error.
        assertTrue(events.all { it is SpinEvent.Station })
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun `a crossing from the wrong direction does not seat`() {
        val (spin, _) = spin(seatAtDeg = 72f, seatDirection = -1)
        spin.rotate(100f, 1.0f)
        assertFalse(spin.seated)
    }

    @Test
    fun `after the seat, a reverse attempt meets the end-stop once per attempt`() {
        val (spin, events) = spin(seatAtDeg = 72f)
        spin.rotate(60f, 1.0f)
        spin.rotate(24f, 0.5f)
        assertTrue(spin.seated)
        events.clear()
        spin.beginGesture()
        spin.rotate(-36f, 0.5f)
        spin.rotate(-36f, 0.5f) // still the same attempt: not answered again
        assertEquals(1, events.filterIsInstance<SpinEvent.EndStop>().size)
        assertEquals(195, events.filterIsInstance<SpinEvent.EndStop>().single().spec.amplitude)
        assertEquals(0f, spin.rotate(-10f, 0.5f), 0f) // the dead dial does not turn
        spin.beginGesture() // a new attempt is a new answer — identical, never escalated
        spin.rotate(-36f, 0.5f)
        assertEquals(2, events.filterIsInstance<SpinEvent.EndStop>().size)
    }

    @Test
    fun `a release can never seat - seating happens only under the hand`() {
        val (spin, _) = spin(seatAtDeg = 36f)
        spin.beginGesture()
        spin.rotate(10f, 0.5f)
        spin.endGesture() // the dial settles onto a station — possibly the seat's own
        assertFalse(spin.seated)
    }

    @Test
    fun `the combination is never written anywhere - the model's fields carry none`() {
        // Ledger A-2, asserted the way P4's census asserts SigningResult:
        // no declared field of the spin model may be a place a
        // combination, digit, secret or code could hide.
        val banned = listOf("combination", "digit", "secret", "code", "pin")
        val fields = CombinationSpin::class.java.declaredFields.map { it.name.lowercase() }
        for (name in fields) {
            for (word in banned) {
                assertFalse("field '$name' could hide a combination", name.contains(word))
            }
        }
    }
}
