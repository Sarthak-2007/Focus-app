package com.example.service.haptics

import kotlin.math.roundToInt

/**
 * AMPLITUDE HONESTY — what the motor in the hand can actually
 * render, and what a waveform becomes on it (FEEL-SR-5).
 *
 * The substrate asserted amplitudes at hardware it never checked:
 * `createWaveform` amplitudes on a motor without amplitude control
 * are silently flattened by the HAL, so two voices specced apart
 * arrive identical and nobody can say when that happened. This
 * file makes the capability a fact ([MotorProfile]) and the
 * rendering a pure, testable function ([degrade]).
 *
 * THE DEGRADATION LAW (FEEL-SR-1, adopted whole):
 *  1. The TEMPORAL SKELETON survives every class — segment count,
 *     order, gaps, interval ratios. Timing is the one channel
 *     every motor renders honestly.
 *  2. Amplitude never INVENTS itself: a class without amplitude
 *     control encodes amplitude as DURATION (FEEL-SR-1's
 *     DUTY-SWELL at the small end: stronger = longer, weaker =
 *     shorter, silence stays silence), it never flattens two
 *     voices into one shout.
 *  3. A RESISTANCE stop degrades toward silence, never toward a
 *     buzz: shortened on waveform classes, a short seat on legacy,
 *     silence where nothing honest remains (H-3).
 *  4. A designed SILENCE renders as silence on every class.
 *  5. Nothing degrades into an ERROR shape. This file contains
 *     no code path that can produce a long continuous high-
 *     amplitude vibration from a non-ceremony waveform.
 *
 * UNPROVEN-ON-DEVICE: the class boundaries are the Android
 * platform's documented capabilities; which class Sarthak's
 * OnePlus reports, and how each rendering feels, is unproven
 * until the build is judged in hand (G-7).
 */
enum class MotorClass {
    /** API 30+ primitives supported: compositions play as specced. */
    COMPOSITION,

    /** API 26+ waveform with per-segment amplitude control. */
    AMPLITUDE_WAVEFORM,

    /** Waveform timing, but the motor flattens amplitudes —
     *  amplitude is encoded as duration (law 2). */
    FIXED_AMPLITUDE,

    /** Pre-API-26: one-shots only, at the spec's own fallback. */
    LEGACY_ONESHOT,

    /** No vibrator, or haptics unavailable: the felt leg is
     *  silence; sound and motion still fire (the event is never
     *  lost, only unspoken in this sense). */
    NO_MOTOR
}

/**
 * The motor fact. [amplitudeCeiling] is the strongest amplitude
 * this motor renders honestly (255 unless a HAL quantises below
 * it); amplitudes are only ever scaled DOWN toward it.
 */
data class MotorProfile(
    val motorClass: MotorClass,
    val amplitudeCeiling: Int = 255
) {
    init {
        require(amplitudeCeiling in 1..255)
    }

    companion object {
        /** The strongest common case, for tests and previews. */
        val FULL = MotorProfile(MotorClass.COMPOSITION)

        val NONE = MotorProfile(MotorClass.NO_MOTOR)
    }
}

/** What the player is actually asked to do, after degradation. */
sealed interface EffectiveHaptic {
    /** Play the composition primitives as specced. */
    data class PlayComposition(val segments: List<PrimitiveSegment>) : EffectiveHaptic

    /** Play a waveform. Amplitudes of -1 mean the platform's
     *  DEFAULT_AMPLITUDE (the fixed-amplitude encoding: shape is
     *  carried by timing alone). */
    data class PlayWaveform(val timingsMs: List<Long>, val amplitudes: List<Int>) : EffectiveHaptic

    /** Play one one-shot of [durationMs] at default amplitude. */
    data class PlayOneShot(val durationMs: Long) : EffectiveHaptic

    /** Play nothing — by law, by capability, or by the fact. */
    data class Silent(val reason: String) : EffectiveHaptic
}

/**
 * Render [waveform] for [profile]. Pure; the whole law above is
 * exercised by `HapticsDegradationTest` on the JVM.
 */
fun degrade(waveform: HapticWaveform, profile: MotorProfile): EffectiveHaptic {
    if (waveform.isSilent || waveform.kind == WaveformKind.SILENCE) {
        return EffectiveHaptic.Silent("designed silence — the event's law, on every motor")
    }
    return when (profile.motorClass) {
        MotorClass.NO_MOTOR ->
            EffectiveHaptic.Silent("no motor — the event is carried by sound and motion")

        MotorClass.COMPOSITION ->
            if (waveform.composition.isNotEmpty()) {
                EffectiveHaptic.PlayComposition(waveform.composition)
            } else {
                waveformForAmplitudeMotor(waveform, profile)
            }

        MotorClass.AMPLITUDE_WAVEFORM -> waveformForAmplitudeMotor(waveform, profile)

        MotorClass.FIXED_AMPLITUDE -> waveformForFixedMotor(waveform)

        MotorClass.LEGACY_ONESHOT -> when (waveform.kind) {
            // Law 3: a stop on a one-shot motor is a short seat —
            // its own chosen fallback, capped short so it can
            // never stretch into a scolding buzz.
            WaveformKind.RESISTANCE ->
                EffectiveHaptic.PlayOneShot(
                    waveform.fallbackMs.coerceAtMost(MAX_LEGACY_RESISTANCE_MS)
                )
            else -> EffectiveHaptic.PlayOneShot(waveform.fallbackMs)
        }
    }
}

/** Waveform classes with honest amplitude: amplitudes pass,
 *  scaled DOWN only, toward the motor's ceiling. */
private fun waveformForAmplitudeMotor(
    waveform: HapticWaveform,
    profile: MotorProfile
): EffectiveHaptic.PlayWaveform {
    val ceiling = profile.amplitudeCeiling
    val amplitudes = if (ceiling >= waveform.peakAmplitude) {
        waveform.amplitudes
    } else {
        waveform.amplitudes.map { amp ->
            if (amp == 0) 0 else (amp * ceiling / 255f).roundToInt().coerceIn(1, ceiling)
        }
    }
    return EffectiveHaptic.PlayWaveform(waveform.timingsMs, amplitudes)
}

/**
 * The fixed-amplitude encoding (law 2): amplitude becomes
 * duration. Each sounding segment's length scales by
 * 0.5 + 0.5 × (amplitude / peak) — so the strongest segment
 * keeps its full written length, weaker ones shorten toward
 * half, silence gaps keep their exact written length, and two
 * voices specced apart stay apart. Amplitudes are reported as
 * [DEFAULT_AMPLITUDE] (-1): the motor's one strength, shaped
 * by time alone.
 */
private fun waveformForFixedMotor(waveform: HapticWaveform): EffectiveHaptic.PlayWaveform {
    val peak = waveform.peakAmplitude.coerceAtLeast(1)
    val timings = waveform.timingsMs.mapIndexed { i, t ->
        val amp = waveform.amplitudes[i]
        when {
            i == 0 -> 0L
            amp == 0 -> t // a gap is a gap, exactly as written
            else -> (t * (0.5f + 0.5f * amp / peak)).roundToInt().toLong().coerceAtLeast(2L)
        }
    }
    val amplitudes = waveform.amplitudes.map { if (it == 0) 0 else DEFAULT_AMPLITUDE }
    return EffectiveHaptic.PlayWaveform(timings, amplitudes)
}

/** Mirrors `VibrationEffect.DEFAULT_AMPLITUDE`; carried as a
 *  literal so this file stays free of Android imports. */
const val DEFAULT_AMPLITUDE = -1

/** A resistance stop's longest honest legacy rendering (H-3). */
const val MAX_LEGACY_RESISTANCE_MS = 40L
