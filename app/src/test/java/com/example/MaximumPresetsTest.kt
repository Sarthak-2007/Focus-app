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

    // ---------- Census additions (LEAD-STAGE-11, by number) ----------

    @Test
    fun `E-04 the clunk pairing hook names the engine's own sync points, in order`() {
        val sealingClunks = com.example.ui.motion.MonolithScores.Sealing.syncPoints
            .filter { it.name.startsWith("bolt-clunk") }.map { it.name }
        assertEquals(sealingClunks, MaximumPresets.BoltClunkPairing.map { it.second })
        assertEquals(listOf("bolt-1", "bolt-2", "bolt-3"), MaximumPresets.BoltClunkPairing.map { it.first })
    }

    @Test
    fun `E-06 the grille latch seats within its constraint and its curves are distinct by value`() {
        assertTrue(MaximumPresets.GrilleLatchSpec.amplitude <= 150)
        assertTrue(MaximumPresets.GrilleLatchSpec.durationMs <= 40)
        // M-4: distinct BY VALUE at a probe point, not just by name.
        val latch = MaximumPresets.GrilleLatchCurve.resistanceAt(1f, 1f)
        val swing = MaximumPresets.GrilleSwingCurve.resistanceAt(1f, 1f)
        assertEquals(2.5f, latch, 0.001f)
        assertEquals(1.35f, swing, 0.001f)
        assertTrue(latch != swing)
    }

    @Test
    fun `E-10 spent stops strain over the ruled 4 degrees and then refuse`() {
        assertEquals(4.0f, MaximumPresets.SPENT_STOP_TRAVEL_DEG, 0f)
        val stop = MaximumPresets.spentPurseStop(travelLimit = 90f)
        assertEquals(4.0f, stop.spec.approachZone, 0f)
        val refused = stop.push(currentValue = 89f, proposedValue = 95f, velocity = 1f)
        assertTrue(refused.refused)
        assertEquals(90f, refused.value, 0f)
    }

    @Test
    fun `E-14 the desk clock's extent is linear in held time - an eased extent would fail here`() {
        assertEquals(0f, MaximumPresets.deskClockExtent(0f), 0f)
        assertEquals(0.25f, MaximumPresets.deskClockExtent(0.25f), 0f)
        assertEquals(0.5f, MaximumPresets.deskClockExtent(0.5f), 0f)
        assertEquals(1f, MaximumPresets.deskClockExtent(1f), 0f)
        assertEquals(1f, MaximumPresets.deskClockExtent(2f), 0f) // clamped, never beyond the day
    }

    @Test
    fun `the dynamic range ships as data, heaviest to lightest, uncompressed`() {
        // H-6 / FEEL-SR-3 section 2: 240 down to 30, strictly descending.
        val peaks = MaximumPresets.DynamicRangePeaks
        assertEquals(240, peaks.first())
        assertEquals(30, peaks.last())
        assertTrue(peaks.zipWithNext().all { (a, b) -> a > b })
    }

    @Test
    fun `the ruled exclusions stand named - ring-hour out by law, the drum's end is its return`() {
        assertEquals(listOf("ring-hour", "drum-minute-end"), MaximumPresets.ExcludedByLaw.map { it.name })
        assertTrue(MaximumPresets.CensusOrder.none { it.name == "ring-hour" })
    }
}
