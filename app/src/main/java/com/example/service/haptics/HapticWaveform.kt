package com.example.service.haptics

/**
 * THE FELT LAYER'S UNIT — one waveform, as data (Slice 7 / Stage 13
 * machinery, FEEL-SR-5). UNPROVEN-ON-DEVICE: every figure in this
 * package is a design figure until judged in a hand in the running
 * app (Audit A G-7).
 *
 * A waveform is the [android.os.VibrationEffect.createWaveform]
 * shape, carried pure (no Android imports) so the registry, the
 * primitives and the degradation law are all JVM-provable:
 * [timingsMs] and [amplitudes] pair 1:1, segment 0 is the lead-in
 * (timing 0, amplitude 0), an amplitude of 0 inside the body is a
 * true silence gap, and amplitudes never rise where a spec says
 * they fall — the tests hold those shapes, not adjectives.
 *
 * [fallbackMs] is the pre-API-26 one-shot the feel seats chose for
 * this exact voice — never a duration invented by the player.
 * [kind] tags the shapes the degradation law treats specially:
 * a [WaveformKind.RESISTANCE] stop must degrade to silence or a
 * short seat, NEVER to a continuous error buzz (CRITIC-SR-1 H-3).
 */
data class HapticWaveform(
    val timingsMs: List<Long>,
    val amplitudes: List<Int>,
    val fallbackMs: Long,
    val kind: WaveformKind = WaveformKind.NEUTRAL,
    val composition: List<PrimitiveSegment> = emptyList()
) {
    init {
        require(timingsMs.size == amplitudes.size) {
            "timings/amplitudes must pair 1:1 (${timingsMs.size} vs ${amplitudes.size})"
        }
        require(timingsMs.isNotEmpty() && timingsMs[0] == 0L && amplitudes[0] == 0) {
            "segment 0 is the lead-in: timing 0, amplitude 0"
        }
        require(timingsMs.all { it >= 0L }) { "timings are never negative" }
        require(amplitudes.all { it in 0..255 }) { "amplitudes live in 0..255" }
        require(fallbackMs >= 0L) { "fallback is never negative" }
    }

    /** Total felt length, lead-in excluded by its own zero. */
    val totalDurationMs: Long get() = timingsMs.sum()

    /** The loudest the hand is ever asked to feel. */
    val peakAmplitude: Int get() = amplitudes.maxOrNull() ?: 0

    /** Amplitude × time — the felt energy the dynamic-range law
     *  (H-6) is read against: door > bolts > crank > drum grille. */
    val energy: Long get() =
        timingsMs.zip(amplitudes).sumOf { (t, a) -> t * a }

    val isSilent: Boolean get() = peakAmplitude == 0 || totalDurationMs == 0L

    companion object {
        /** The designed silence: an event whose law is NO felt
         *  event at all (held dot, desk-clock forming, gaps,
         *  hand-back — CRITIC-SR-1 H-4, SOUND-SR-4's silence map).
         *  Silence is a registry row, never an absent row, so a
         *  test can prove the silent events silent. */
        val SILENCE = HapticWaveform(
            timingsMs = listOf(0L),
            amplitudes = listOf(0),
            fallbackMs = 0L,
            kind = WaveformKind.SILENCE
        )
    }
}

/** The shapes the machinery itself must know, by name. */
enum class WaveformKind {
    /** An ordinary voice; degrades by the general law. */
    NEUTRAL,

    /** A mechanism that will not move (spent drum, spent crank).
     *  Degradation may shorten it, soften it, or silence it —
     *  it may never stretch it into a buzz (H-3). */
    RESISTANCE,

    /** A boundary ceremony (the seal, the lifting). Its fallback
     *  is a single heavy approximation, chosen by its spec. */
    CEREMONY,

    /** No felt event exists for this row, by ruling. */
    SILENCE
}

/** One composition primitive (API 30+ hardware vocabulary), as
 *  data: [kind] at [scale] (0..1), after [delayMs] of silence.
 *  The Android player maps [kind] onto
 *  `VibrationEffect.Composition` constants; classes below
 *  COMPOSITION drop the list whole and play the waveform. */
data class PrimitiveSegment(
    val kind: PrimitiveKind,
    val scale: Float = 1.0f,
    val delayMs: Int = 0
) {
    init {
        require(scale in 0f..1f) { "primitive scale lives in 0..1" }
        require(delayMs >= 0) { "primitive delay is never negative" }
    }
}

enum class PrimitiveKind {
    CLICK, THUD, SPIN, TICK, LOW_TICK, QUICK_RISE, QUICK_FALL, SLOW_RISE
}
