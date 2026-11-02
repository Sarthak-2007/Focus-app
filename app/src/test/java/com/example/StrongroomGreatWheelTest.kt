package com.example

import com.example.ui.strongroom.WheelPhysics
import com.example.ui.strongroom.WheelTurn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A-16 — THE GREAT WHEEL's mechanism (BUILD-SR-22): the pure
 * turn, judged without a device. Every felt figure is
 * UNPROVEN-ON-DEVICE; what these tests prove is the mechanism's
 * LAW: position-keyed detents, the tumbler's carry, governed
 * inertia (no free spin at any flick), and the seat — a released
 * or interrupted wheel always rests exactly on a detent.
 * Tests named here are registered in
 * ascent/build-first-usable/end-phase-test-list.md the same act
 * (Rulings Round 6: tests never block; compile is the proof).
 */
class StrongroomGreatWheelTest {

    @Test
    fun wheel_detentSequence_identicalAtTwoGestureVelocities() {
        // The same arc, dragged in few large moves vs many small
        // ones, crosses the identical detent sequence.
        fun turnIn(chunks: Int): List<Long> {
            val wheel = WheelTurn()
            val crossed = mutableListOf<Long>()
            val total = WheelPhysics.DETENT_DEGREES * 5 + 4f
            repeat(chunks) { crossed += wheel.dragBy(total / chunks) }
            return crossed
        }
        assertEquals(turnIn(2), turnIn(23))
        assertEquals(listOf(1L, 2L, 3L, 4L), turnIn(2))
    }

    @Test
    fun wheel_tumbler_carriesOnesTensHundreds() {
        // Drive by repeated detent arcs; the carry arithmetic is
        // read off the resulting turn:
        val exact = WheelTurn()
        var crossed = 0
        while (crossed < 123) {
            crossed += exact.dragBy(WheelPhysics.DETENT_DEGREES * 3).size
        }
        // The tumbler reads the turn exactly: dials are the
        // decimal digits of the detent count, carries included.
        val n = exact.totalDetents.toInt()
        assertTrue(n >= 123)
        assertEquals(listOf(n / 100, (n / 10) % 10, n % 10), exact.dials())
        // Negative turns wrap lawfully: −1 detent reads 999.
        val back = WheelTurn()
        back.dragBy(-WheelPhysics.DETENT_DEGREES * 3)
        assertEquals(listOf(9, 9, 9), back.dials())
    }

    @Test
    fun wheel_flickVelocity_cannotFreeSpin() {
        val gentle = WheelTurn()
        gentle.release(120f)
        val violent = WheelTurn()
        val crossed = violent.release(100_000f)
        // Travel is capped by construction: even an absurd flick
        // crosses no more detents than the governed maximum.
        val maxDetents = (WheelPhysics.MAX_RELEASE_TRAVEL_DEG /
            WheelPhysics.DETENT_DEGREES).toInt() + 1
        assertTrue(crossed.size <= maxDetents)
        assertTrue(violent.angleDegrees <= WheelPhysics.MAX_RELEASE_TRAVEL_DEG +
            WheelPhysics.DETENT_DEGREES)
    }

    @Test
    fun wheel_releaseAndInterrupt_alwaysRestOnADetent() {
        val wheel = WheelTurn()
        wheel.dragBy(WheelPhysics.DETENT_DEGREES * 0.4f)
        wheel.interrupt()
        assertEquals(0f, wheel.pendingFraction(), 0.0001f)
        assertEquals(
            0f,
            wheel.angleDegrees % WheelPhysics.DETENT_DEGREES,
            0.0001f
        )
        val carried = WheelTurn()
        carried.release(400f)
        assertEquals(0f, carried.pendingFraction(), 0.0001f)
        assertEquals(
            0f,
            carried.angleDegrees % WheelPhysics.DETENT_DEGREES,
            0.0001f
        )
    }

    @Test
    fun wheel_reset_forgetsTheCombinationWhole() {
        val wheel = WheelTurn()
        repeat(7) { wheel.dragBy(WheelPhysics.DETENT_DEGREES * 2) }
        wheel.reset()
        assertEquals(0L, wheel.totalDetents)
        assertEquals(listOf(0, 0, 0), wheel.dials())
    }
}
