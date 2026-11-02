package com.example

import com.example.service.haptics.HapticPrimitives
import com.example.service.haptics.HapticWaveform
import com.example.service.haptics.WaveformKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * THE COMPOSITION PRIMITIVES' SHAPES (service/haptics;
 * FEEL-SR-5): a detent settles, a thud only falls, the clunks'
 * mass ascends while their gaps widen, the withdrawals run
 * heaviest-first, a resistance stop is short by construction,
 * the hollow seat's seat never lands, and the carve ends at
 * its cut.
 */
class HapticsPrimitivesTest {

    @Test
    fun tick_defaultsAreTheEnginesRotaryShape_andItSettles() {
        val tick = HapticPrimitives.tick()
        assertEquals(listOf(0L, 7L, 5L), tick.timingsMs)
        assertEquals(listOf(0, 180, 70), tick.amplitudes)
        assertTrue(tick.amplitudes[1] > tick.amplitudes[2])
    }

    @Test
    fun thud_onlyFalls() {
        val thud = HapticPrimitives.thud()
        val body = thud.amplitudes.drop(1)
        assertEquals(body.sortedDescending(), body)
        assertEquals(listOf(0L, 42L, 30L, 16L), thud.timingsMs)
    }

    @Test
    fun detentTrain_interpolatesAndGapsBetweenDetents() {
        val train = HapticPrimitives.detentTrain(
            count = 4, startAmplitude = 100, endAmplitude = 220,
            strikeMs = 8L, gapMs = 18L
        )
        val strikes = train.amplitudes.filterIndexed { i, _ -> i % 2 == 1 }
        val gaps = train.amplitudes.filterIndexed { i, _ -> i % 2 == 0 && i > 0 }
        assertEquals(4, strikes.size)
        assertEquals(100, strikes.first())
        assertEquals(220, strikes.last())
        assertEquals(strikes.sorted(), strikes)
        assertTrue(gaps.all { it == 0 })
        assertEquals(3, gaps.size)
    }

    @Test
    fun descendingClunks_massAscends_bodiesLengthen_gapsWiden() {
        val clunks = HapticPrimitives.descendingClunks()
        assertEquals(WaveformKind.CEREMONY, clunks.kind)
        val strikes = mutableListOf<Int>()
        val bodies = mutableListOf<Long>()
        val gaps = mutableListOf<Long>()
        var i = 1
        while (i < clunks.timingsMs.size) {
            bodies += clunks.timingsMs[i]
            strikes += clunks.amplitudes[i]
            if (i + 1 < clunks.timingsMs.size) gaps += clunks.timingsMs[i + 1]
            i += 2
        }
        assertEquals(listOf(235, 245, 255), strikes)
        assertEquals(bodies.sorted(), bodies)
        assertEquals(gaps.sorted(), gaps)
    }

    @Test
    fun withdrawals_runHeaviestFirst_withStrainSnapSlidePerBolt() {
        val w = HapticPrimitives.withdrawals(count = 3)
        // Per bolt: 3 strain steps + snap + slide = 5 segments,
        // plus a gap between bolts.
        val peaks = mutableListOf<Int>()
        var i = 1
        repeat(3) {
            val bolt = w.amplitudes.subList(i, i + 5)
            // strain rises within the bolt
            assertTrue(bolt[0] < bolt[1] && bolt[1] < bolt[2])
            // snap is the bolt's peak; slide falls away
            assertEquals(bolt[3], bolt.max())
            assertTrue(bolt[4] < bolt[3])
            peaks += bolt[3]
            i += 6 // 5 segments + 1 gap (harmless overshoot on the last bolt)
        }
        assertEquals(peaks.sortedDescending(), peaks)
    }

    @Test
    fun resistanceStop_isShort_andRefusesToStretchIntoABuzz() {
        val stop = HapticPrimitives.resistanceStop()
        assertEquals(WaveformKind.RESISTANCE, stop.kind)
        assertTrue(stop.totalDurationMs <= HapticPrimitives.MAX_RESISTANCE_TOTAL_MS)
        // The load rises, the brake is the peak, and then it ends.
        val body = stop.amplitudes.drop(1)
        assertEquals(body.sorted(), body)
        assertEquals(stop.peakAmplitude, body.last())
        try {
            HapticPrimitives.resistanceStop(stepMs = 500L)
            fail("an over-long resistance stop must be refused at construction")
        } catch (expected: IllegalArgumentException) {
            // H-3, enforced by the builder itself.
        }
    }

    @Test
    fun hollowSeat_theSeatNeverLands() {
        val hollow = HapticPrimitives.hollowSeat()
        val peakIndex = hollow.amplitudes.indexOf(hollow.peakAmplitude)
        // Immediately after the swell's peak: a true gap.
        assertEquals(0, hollow.amplitudes[peakIndex + 1])
        // And nothing after the gap reaches even half the swell.
        val after = hollow.amplitudes.drop(peakIndex + 1)
        assertTrue(after.all { it <= hollow.peakAmplitude / 2 })
    }

    @Test
    fun carve_endsAtItsCut_withNothingAfter() {
        val carve = HapticPrimitives.carve()
        assertEquals(205, carve.amplitudes.last())
        assertEquals(carve.peakAmplitude, carve.amplitudes.last())
        // The strokes before the cut thin out.
        val strokes = carve.amplitudes.filterIndexed { i, a -> i % 2 == 1 && a > 0 }.dropLast(1)
        assertEquals(strokes.sortedDescending(), strokes)
    }

    @Test
    fun waveform_invariantsAreEnforced() {
        try {
            HapticWaveform(listOf(0L, 10L), listOf(0), fallbackMs = 1L)
            fail("mismatched timings/amplitudes must be refused")
        } catch (expected: IllegalArgumentException) {
        }
        try {
            HapticWaveform(listOf(5L, 10L), listOf(0, 100), fallbackMs = 1L)
            fail("a non-zero lead-in must be refused")
        } catch (expected: IllegalArgumentException) {
        }
    }
}
