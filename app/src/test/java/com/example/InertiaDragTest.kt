package com.example

import com.example.ui.maximum.InertiaDrag
import com.example.ui.maximum.InertiaDragSpec
import com.example.ui.maximum.MaximumPresets
import com.example.ui.motion.DetentRail
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.MonolithBodies
import com.example.ui.motion.SpringState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** BUILD-SR-11 — inertia drags: capture by heading, settle honestly, rest exactly on target. */
class InertiaDragTest {

    private fun drag(): InertiaDrag = InertiaDrag(
        InertiaDragSpec(
            body = MonolithBodies.Drum,
            inertia = InertiaBody(mass = 1.6f, resistance = 1.1f),
            rail = DetentRail((0..10).map { it.toFloat() }),
            captureRadius = 0.5f,
            captureVelocity = 0.5f
        )
    )

    @Test
    fun `capture is decided from where the drag is heading, not where it was released`() {
        // Released beside 2, travelling fast toward 3: the catch is 3.
        val outcome = drag().release(SpringState(value = 2.2f, velocity = 3f))
        assertEquals(3f, outcome.target, 0f)
        // Nearly still beside 2: the catch is 2.
        val still = drag().release(SpringState(value = 2.2f, velocity = 0.1f))
        assertEquals(2f, still.target, 0f)
    }

    @Test
    fun `the settle is honest - the drag rests exactly on its target`() {
        val outcome = drag().release(SpringState(value = 2.2f, velocity = 3f))
        assertEquals(outcome.target, outcome.finalValue, 0f)
        assertTrue(outcome.settleSeconds in 0.01f..10f)
    }

    @Test
    fun `the stretch drag has no inertia - a fling stops within the current day`() {
        // FEEL-SR-3 C6.1 law 1: released at any velocity, travel stops
        // within the current day-step. Same machinery, parameterised.
        val stretch = MaximumPresets.stretchDrag(days = 30)
        val outcome = stretch.release(SpringState(value = 5f, velocity = 20f))
        assertEquals(5f, outcome.target, 0f)
        assertEquals(5f, outcome.finalValue, 0f)
        assertTrue(abs(outcome.coastRestValue - 5f) < 0.1f)
    }
}
