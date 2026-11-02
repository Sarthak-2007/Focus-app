package com.example

import com.example.ui.motion.DecidedMotionMachine
import com.example.ui.motion.DecidedStates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MOTION-SR-5 — M-2 as executable law: an animation rests ONLY in a
 * declared decided state; interruption retargets; persistence holds
 * decided states only; a fractional pose is never restored as a rest.
 */
class DecidedMotionTest {

    private enum class Door { OPEN, SEALED }

    private val doorStates = DecidedStates(listOf(Door.OPEN, Door.SEALED)) {
        if (it == Door.SEALED) 1f else 0f
    }

    @Test
    fun `resting state changes only on arrival`() {
        val machine = DecidedMotionMachine(doorStates, Door.OPEN)
        machine.begin(Door.SEALED)
        // Mid-flight: still resting OPEN; the machine merely owes SEALED.
        assertEquals(Door.OPEN, machine.restingState)
        assertEquals(Door.SEALED, machine.targetState)
        assertTrue(!machine.isAtRest)
        machine.arrive()
        assertEquals(Door.SEALED, machine.restingState)
        assertTrue(machine.isAtRest)
    }

    @Test
    fun `interruption retargets to another decided state - the recall is lawful`() {
        val machine = DecidedMotionMachine(doorStates, Door.OPEN)
        machine.begin(Door.SEALED)
        machine.interrupt(Door.OPEN) // called back mid-flight
        assertEquals(Door.OPEN, machine.targetState)
        machine.arrive()
        assertEquals(Door.OPEN, machine.restingState)
    }

    @Test
    fun `the saveable state is always decided - mid-flight it is the target, never the pose`() {
        val machine = DecidedMotionMachine(doorStates, Door.OPEN)
        assertEquals(Door.OPEN, machine.saveableState())
        machine.begin(Door.SEALED)
        // The pose right now is somewhere in (0,1); what may be saved is SEALED.
        assertEquals(Door.SEALED, machine.saveableState())
        assertTrue(doorStates.contains(machine.saveableState()))
    }

    @Test
    fun `process death restores a decided state, and a fractional pose is snapped, never rested`() {
        val restored = DecidedMotionMachine.restore(doorStates, Door.SEALED)
        assertEquals(Door.SEALED, restored.restingState)
        assertTrue(restored.isAtRest)

        // A legacy/corrupt fractional save: 0.8 of the way to sealed.
        val fromPose = DecidedMotionMachine.restoreFromPose(doorStates, 0.8f)
        assertEquals(Door.SEALED, fromPose.restingState)
        val fromLowPose = DecidedMotionMachine.restoreFromPose(doorStates, 0.2f)
        assertEquals(Door.OPEN, fromLowPose.restingState)
    }

    @Test
    fun `undeclared targets and duplicate poses are refused`() {
        var refused = 0
        val machine = DecidedMotionMachine(doorStates, Door.OPEN)
        try {
            // A state outside the declared set cannot be targeted.
            val other = DecidedStates(listOf(Door.OPEN)) { 0f }
            DecidedMotionMachine(other, Door.SEALED)
        } catch (e: IllegalArgumentException) { refused++ }
        try {
            // Two decided states at one pose value are one state — refused.
            DecidedStates(listOf(Door.OPEN, Door.SEALED)) { 0f }
        } catch (e: IllegalArgumentException) { refused++ }
        assertEquals(Door.OPEN, machine.restingState)
        assertEquals(2, refused)
    }
}
