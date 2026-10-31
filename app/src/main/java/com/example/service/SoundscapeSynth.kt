package com.example.service

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * #253 LINE 1 — THE SESSION SOUNDSCAPE'S VOICE (pure render).
 *
 * Until this file the engine was architecturally forbidden from
 * making any sound longer than 160 ms: every voice is a transient
 * (§7 of the developer guide — "Zero ambient sound mixer loops"),
 * so a session could never carry a continuous sound of its own.
 * This render is the one ruled exception, and it is built to the
 * house's own terms:
 *
 *  - ZERO audio files — the loop is generated, like every other
 *    voice in the engine (brown noise through a leaky integrator,
 *    driven by the same xorshift32 family the seal bolt uses, so
 *    the grain is deterministic and identical on every device).
 *  - ONE soundscape — a low brown-noise wash (the focus-ambient
 *    the #255 quick-win named: brown noise / rain class), not a
 *    library. Choosing more is the sound owner's settled-position
 *    work on #51, not this build's.
 *  - SEAMLESS BY CONSTRUCTION — the render is LOOP + CROSSFADE
 *    samples long; the tail is folded into the head under an
 *    equal-power (sin/cos) crossfade, so the loop point carries
 *    no click and no period thump.
 *  - NORMALISED — the loop's peak is scaled to [PEAK], so the
 *    lane's gain staging (fade rules + night gain) starts from a
 *    known ceiling on every device.
 *
 * Pure math only — no Android types — so the render is pinned
 * by plain unit tests (SoundscapeSynthTest).
 */
object SoundscapeSynth {

    const val SAMPLE_RATE = 44100

    /** The loop's length: 6 s at 44 100 Hz. Long enough that the
     *  brown wash has no audible period, short enough to retain. */
    const val LOOP_SAMPLES = 264_600

    /** The seamless fold: the last 1.5 s of the raw render is
     *  mixed into the first 1.5 s of the loop. */
    const val CROSSFADE_SAMPLES = 66_150

    /** xorshift32 seed for the noise drive (Marsaglia 13/17/5,
     *  the seal bolt's family). Golden-ratio constant, non-zero —
     *  the zero state is absorbing. */
    const val NOISE_SEED: Int = 0x2545F491.toInt()

    /** Leaky-integrator coefficient for the brown wash: each
     *  white sample contributes 2% and the state keeps 98% — the
     *  classic 1/f² slope, low and even. */
    const val BROWN_LEAK = 0.98f
    const val BROWN_DRIVE = 0.02f

    /** The retained loop's peak ceiling after normalisation. */
    const val PEAK = 0.5f

    /** Fade rules (the priced scope's "fade rules"): the lane
     *  ramps gain in and out over 2.5 s — a soundscape never
     *  starts or stops with an edge. */
    const val FADE_SAMPLES = 110_250

    internal fun xorshift32(state: Int): Int {
        var x = state
        x = x xor (x shl 13)
        x = x xor (x ushr 17)
        x = x xor (x shl 5)
        return x
    }

    /** The fade envelope's shape: linear 0→1 across the fade.
     *  Pure so the lane's ramp law is testable off-device. */
    fun fadeGain(samplesIntoFade: Int, fadeSamples: Int = FADE_SAMPLES): Float {
        if (fadeSamples <= 0) return 1f
        return (samplesIntoFade.coerceIn(0, fadeSamples).toFloat() / fadeSamples.toFloat())
    }

    /**
     * Render the seamless loop. Deterministic: two calls return
     * bit-identical arrays (golden-vector tested).
     */
    fun renderLoop(): FloatArray {
        val total = LOOP_SAMPLES + CROSSFADE_SAMPLES
        val raw = FloatArray(total)
        var state = NOISE_SEED
        var brown = 0f
        var peak = 0f
        for (i in 0 until total) {
            state = xorshift32(state)
            // White drive in [-1, 1) from the top 24 bits.
            val white = ((state ushr 8) / 8388608f) - 1f
            brown = (brown + BROWN_DRIVE * white) * BROWN_LEAK
            raw[i] = brown
            val a = if (brown < 0f) -brown else brown
            if (a > peak) peak = a
        }
        val scale = if (peak > 0f) PEAK / peak else 0f
        val out = FloatArray(LOOP_SAMPLES)
        // Body: samples [0, LOOP) straight from the render.
        for (i in 0 until LOOP_SAMPLES) {
            out[i] = raw[i] * scale
        }
        // The fold: at the wrap, playback continues from
        // out[LOOP - 1] = raw[LOOP - 1] into out[0] — so out[0]
        // must be the render's natural continuation, raw[LOOP],
        // and the blend must hand over to plain raw[j] by the
        // window's end. Head (raw[j]) therefore takes the sin
        // weight (0 → 1 across the window) and the tail
        // (raw[LOOP + j]) the cos weight (1 → 0): equal power,
        // continuous in value at both joins.
        for (j in 0 until CROSSFADE_SAMPLES) {
            val t = j.toFloat() / CROSSFADE_SAMPLES.toFloat()
            val angle = t * (PI.toFloat() / 2f)
            val headGain = sin(angle)
            val tailGain = cos(angle)
            out[j] = out[j] * headGain + raw[LOOP_SAMPLES + j] * scale * tailGain
        }
        return out
    }
}
