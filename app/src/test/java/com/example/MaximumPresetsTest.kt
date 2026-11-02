package com.example

import com.example.ui.maximum.MaximumPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** BUILD-SR-11 — the presets: FEEL-SR-3's numbers, as data, in the census's assembly order. */
class MaximumPresetsTest {

    @Test
    fun `the census order stands as filed, the spin at its head`() {
        assertEquals(
            listOf(
                "combination-spin", "wheel-bolts", "knock", "drum-crank", "governors-crank",
                "door-weight", "lifting-hour", "allowance", "break", "stretch"
            ),
            MaximumPresets.CensusOrder.map { it.name }
        )
    }

    @Test
    fun `the spin's station ladder thickens by pass - 70, 95, 120`() {
        assertEquals(70, MaximumPresets.spinStationAmplitude(1))
        assertEquals(95, MaximumPresets.spinStationAmplitude(2))
        assertEquals(120, MaximumPresets.spinStationAmplitude(3))
        assertEquals(120, MaximumPresets.spinStationAmplitude(7))
        // The gate is never a station's shape: void first, heavier, longer.
        assertEquals(35, MaximumPresets.GateSpec.preSilenceMs)
        assertEquals(165, MaximumPresets.GateSpec.amplitude)
    }

    @Test
    fun `the hour train is sparse firm stones - quarters 55, whole hours 85`() {
        val train = MaximumPresets.hourTrain()
        assertEquals(85, train.specFor(0).amplitude) // minute 0
        assertEquals(55, train.specFor(1).amplitude) // minute 15
        assertEquals(85, train.specFor(4).amplitude) // minute 60
    }

    @Test
    fun `tighten rises as it tightens and raise is hollow - no axis shared`() {
        val tighten = MaximumPresets.tightenTrain(steps = 4)
        assertEquals(140, tighten.specFor(0).amplitude)
        assertEquals(100, tighten.specFor(0).secondaryAmplitude)
        assertEquals(175, tighten.specFor(4).amplitude)
        assertEquals(130, tighten.specFor(4).secondaryAmplitude)
        assertEquals(210, MaximumPresets.TightenLockSpec.amplitude)

        val hollow = MaximumPresets.hollowTrain(steps = 4)
        val hollowSpec = hollow.specFor(2)
        assertEquals(60, hollowSpec.amplitude)
        assertEquals(50, hollowSpec.latencyMs) // the deferral itself, declared
        assertTrue(hollowSpec.ramped)
        // A hollow detent is not a quiet tighten: single, late, ramped, edgeless.
        assertEquals(null, hollowSpec.secondaryAmplitude)
    }

    @Test
    fun `break presets are haptic-only notches - they cost no travel`() {
        // FEEL-SR-3 C5.2 law 1: the notch exists solely in the waveform;
        // the drag's physics are bit-identical at preset and other values.
        val train = MaximumPresets.breakTrain(rangeMinutes = 30, presetMinutes = listOf(10))
        assertEquals(1f, train.resistanceAt(10f, 3f), 0f)
        assertEquals(1f, train.resistanceAt(11f, 3f), 0f)
        assertEquals(75, train.specFor(10).amplitude)
        assertTrue(train.specFor(10).ramped)
        assertEquals(30, train.specFor(1).amplitude) // the minute grain
        assertEquals(50, train.specFor(5).amplitude) // the five-minute grain
    }

    @Test
    fun `stretch steps are the heaviest recurring detent, rising with distance`() {
        val train = MaximumPresets.stretchTrain(days = 30)
        assertEquals(150, train.specFor(0).amplitude)
        assertEquals(195, train.specFor(30).amplitude)
        assertEquals(90L, MaximumPresets.STRETCH_MIN_STEP_INTERVAL_MS)
    }
}
