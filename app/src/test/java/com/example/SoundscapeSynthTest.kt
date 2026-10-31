package com.example

import com.example.service.SoundscapeSynth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * #253 line 1: the session soundscape's render — the one ruled
 * exception to the engine's transient-only law. These pins hold
 * the properties the lane's design promises: deterministic,
 * seamless at the wrap, normalised to its ceiling, and never
 * silent.
 */
class SoundscapeSynthTest {

    @Test
    fun renderHasTheLoopLength() {
        assertEquals(SoundscapeSynth.LOOP_SAMPLES, SoundscapeSynth.renderLoop().size)
    }

    @Test
    fun renderIsDeterministic() {
        val a = SoundscapeSynth.renderLoop()
        val b = SoundscapeSynth.renderLoop()
        assertTrue(a.contentEquals(b))
    }

    @Test
    fun renderIsNormalisedToItsCeiling() {
        val loop = SoundscapeSynth.renderLoop()
        var peak = 0f
        for (s in loop) peak = maxOf(peak, abs(s))
        // The ceiling is a ceiling: nothing exceeds it, and the
        // normalisation actually reaches it (within float slack).
        assertTrue("peak $peak exceeds ceiling", peak <= SoundscapeSynth.PEAK + 0.001f)
        assertTrue("peak $peak never reaches ceiling", peak > SoundscapeSynth.PEAK - 0.01f)
    }

    @Test
    fun renderIsNotSilent() {
        val loop = SoundscapeSynth.renderLoop()
        var sumSq = 0.0
        for (s in loop) sumSq += (s * s).toDouble()
        val rms = sqrt(sumSq / loop.size)
        assertTrue("RMS $rms is silence in practice", rms > 0.03)
    }

    @Test
    fun wrapIsContinuous() {
        val loop = SoundscapeSynth.renderLoop()
        // By construction out[0] is the raw render's continuation
        // past out[LOOP - 1], so the wrap step is one raw step —
        // brown noise at this leak moves in millisteps, never a
        // click. Pin the bound, not the construction.
        val wrapStep = abs(loop[0] - loop[loop.size - 1])
        assertTrue("wrap step $wrapStep is a click", wrapStep < 0.02f)
        // And no interior step is a click either.
        var maxStep = 0f
        for (i in 1 until loop.size) {
            maxStep = maxOf(maxStep, abs(loop[i] - loop[i - 1]))
        }
        assertTrue("max interior step $maxStep is a click", maxStep < 0.02f)
    }

    @Test
    fun fadeGainFollowsTheRampLaw() {
        assertEquals(0f, SoundscapeSynth.fadeGain(0), 0.0001f)
        assertEquals(1f, SoundscapeSynth.fadeGain(SoundscapeSynth.FADE_SAMPLES), 0.0001f)
        assertEquals(
            0.5f,
            SoundscapeSynth.fadeGain(SoundscapeSynth.FADE_SAMPLES / 2),
            0.001f
        )
        // Past the end of the fade the gain rests at full.
        assertEquals(1f, SoundscapeSynth.fadeGain(SoundscapeSynth.FADE_SAMPLES * 2), 0.0001f)
        // Before its start it rests at zero.
        assertEquals(0f, SoundscapeSynth.fadeGain(-100), 0.0001f)
    }
}
