package com.example.service

/**
 * THE KNOCK — its design, as pure constants (the engine's
 * [TactileSoundEngine.playKnock] is this spec's only performer).
 *
 * The sealed face's only event (Audit A §2.3): reaching for a
 * locked app is ONE dull, heavy thud, FELT MORE THAN HEARD, and
 * nothing moves, nothing opens, nothing apologises. This spec is
 * the thud designed as a voice of its own — never a platform
 * default, never another voice reused:
 *
 *  - PITCH: 118 Hz — below every other voice in the engine (the
 *    door family speaks at 240–360 Hz; the seal at 360 Hz). A
 *    knock on a vault door is body, not edge: the ear takes the
 *    tail, the hand takes the blow.
 *  - BODY: 110 ms of transient (inside the engine's 160 ms law)
 *    with the dark chord (0.9 — the release-interval family), at
 *    gain 0.52: present in the room, deliberately UNDER the
 *    seal's 0.62. Heard second; felt first.
 *  - HAND: one mass strike, no bounce — a 42 ms full-amplitude
 *    blow (255), a 30 ms settle at body weight (96), a 16 ms
 *    final seat (48). The decay is the door absorbing the blow:
 *    amplitudes fall by more than half each step and never rise.
 *    On hardware that speaks composition primitives (API 30+)
 *    the blow is PRIMITIVE_THUD at full scale — the platform's
 *    own heaviest primitive, spent whole, once.
 *
 * Slice note (plan §6): Slice 1 builds this working-and-good —
 * a designed voice, tuned on paper against the engine's existing
 * voices. Its in-hand maximum is Slice 7's HAPTICS pass, judged
 * in the running app; nothing here pretends that judgement has
 * happened. UNPROVEN-ON-DEVICE (Audit A G-7).
 */
object StrongroomKnock {

    /** The blow's pitch — the engine's lowest voice. */
    const val PITCH_HZ = 118f

    /** The body's length, ms (the engine renders ≤160 ms). */
    const val DURATION_MS = 110

    /** Under the seal's 0.62 by design: felt first, heard second. */
    const val GAIN = 0.52f

    /** The dark chord — the release interval, as the seat knock
     *  and the door's exit already speak it. */
    const val CHORD_RATIO = 0.9f

    /** Waveform segments (ms): lead-in 0, the blow, the settle,
     *  the seat. Amplitudes pair 1:1 after the lead-in. */
    val HAPTIC_TIMINGS_MS = longArrayOf(0, 42, 30, 16)
    val HAPTIC_AMPLITUDES = intArrayOf(0, 255, 96, 48)

    /** Pre-API-26 fallback: the blow alone, as a one-shot. */
    const val HAPTIC_FALLBACK_MS = 42L

    /** The thud primitive's scale, where the hardware speaks it:
     *  full — a knock is not a half-measure. */
    const val THUD_PRIMITIVE_SCALE = 1.0f
}
