package com.example

import com.example.ui.maximum.ResistanceCurves
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** BUILD-SR-11 — resistance curves as executable law: ramps ramp, wells peak at the lip. */
class ResistanceCurvesTest {

    @Test
    fun `a ramp climbs linearly between its points and clamps outside`() {
        val ramp = ResistanceCurves.ramp(0f, 10f, 1f, 3f)
        assertEquals(1f, ramp.resistanceAt(0f, 0f), 0.001f)
        assertEquals(2f, ramp.resistanceAt(5f, 0f), 0.001f)
        assertEquals(3f, ramp.resistanceAt(10f, 0f), 0.001f)
        assertEquals(1f, ramp.resistanceAt(-5f, 0f), 0.001f)
        assertEquals(3f, ramp.resistanceAt(20f, 0f), 0.001f)
    }

    @Test
    fun `progressive resistance grows with displacement from its centre`() {
        val curve = ResistanceCurves.progressive(centre = 0f, scale = 10f, base = 1f, gain = 2f)
        assertEquals(1f, curve.resistanceAt(0f, 0f), 0.001f)
        assertEquals(3f, curve.resistanceAt(10f, 0f), 0.001f)
        assertEquals(3f, curve.resistanceAt(-10f, 0f), 0.001f)
        assertTrue(curve.resistanceAt(20f, 0f) > curve.resistanceAt(10f, 0f))
    }

    @Test
    fun `detent wells rest at the centre and peak at the lip`() {
        val wells = ResistanceCurves.detentWells(listOf(0f, 36f), lipDistance = 18f, depth = 0.6f)
        assertEquals(1f, wells.resistanceAt(0f, 0f), 0.001f)
        assertEquals(1.6f, wells.resistanceAt(18f, 0f), 0.001f)
        assertEquals(1f, wells.resistanceAt(36f, 0f), 0.001f)
    }

    @Test
    fun `viscous resistance follows the hand's speed`() {
        val viscous = ResistanceCurves.viscous(gain = 0.1f)
        assertEquals(1f, viscous.resistanceAt(0f, 0f), 0.001f)
        assertEquals(2f, viscous.resistanceAt(0f, 10f), 0.001f)
        assertEquals(2f, viscous.resistanceAt(0f, -10f), 0.001f)
    }

    @Test
    fun `invalid curves are refused at construction`() {
        var refused = 0
        try { ResistanceCurves.constant(-1f) } catch (e: IllegalArgumentException) { refused++ }
        try { ResistanceCurves.ramp(5f, 5f, 1f, 2f) } catch (e: IllegalArgumentException) { refused++ }
        try { ResistanceCurves.detentWells(emptyList(), 1f, 1f) } catch (e: IllegalArgumentException) { refused++ }
        assertEquals(3, refused)
    }
}
