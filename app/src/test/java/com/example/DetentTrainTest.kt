package com.example

import com.example.ui.maximum.DetentEventSpec
import com.example.ui.maximum.DetentTrain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** BUILD-SR-11 — detent trains: crossings are a pure function of position, never of time. */
class DetentTrainTest {

    private val spec = DetentEventSpec(amplitude = 50, durationMs = 10)
    private val train = DetentTrain(listOf(0f, 10f, 20f, 30f), specFor = { spec })

    @Test
    fun `crossings arrive in travel order, both directions`() {
        assertEquals(listOf(10f, 20f), train.crossings(5f, 25f).map { it.position })
        assertEquals(listOf(20f, 10f), train.crossings(25f, 5f).map { it.position })
        assertTrue(train.crossings(5f, 25f).all { it.direction == 1 })
        assertTrue(train.crossings(25f, 5f).all { it.direction == -1 })
    }

    @Test
    fun `a detent landed on counts and a detent started on does not recount`() {
        assertEquals(listOf(10f), train.crossings(5f, 10f).map { it.position })
        assertEquals(listOf(20f), train.crossings(10f, 20f).map { it.position })
        assertTrue(train.crossings(10f, 10f).isEmpty())
        assertTrue(train.crossings(12f, 18f).isEmpty())
    }

    @Test
    fun `the same travel always produces the identical sequence - position, not time`() {
        // There is no clock in the API: two calls with the same positions
        // are the whole proof a slow pass and a fast pass agree.
        assertEquals(train.crossings(5f, 25f), train.crossings(5f, 25f))
    }

    @Test
    fun `every crossing carries its waveform spec as data`() {
        val crossing = train.crossings(5f, 15f).single()
        assertEquals(50, crossing.spec.amplitude)
        assertEquals(10, crossing.spec.durationMs)
    }

    @Test
    fun `a waveform spec outside the motor range is refused`() {
        var refused = false
        try { DetentEventSpec(amplitude = 300, durationMs = 10) } catch (e: IllegalArgumentException) { refused = true }
        assertTrue(refused)
    }
}
