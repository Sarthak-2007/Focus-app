package com.example.service

/**
 * THE LIFT — its felt voice, as pure constants (P6 · BUILD-SR-4).
 *
 * The lifting is an EVENT (A-13): bolts withdraw one by one and
 * the door opens on its own weight. Its voice is designed here in
 * the same shape as BUILD-SR-2's knock spec — pure figures, one
 * performer. The performer wiring (the engine spending these
 * figures at the ceremony's beats) is P2/P9's seam: this file
 * invents no engine call, and the ceremony surfaces report their
 * beats outward ([com.example.ui.strongroom.LiftingScreen]'s
 * callbacks) for that wiring to spend.
 *
 * The design, against the engine's standing voices (read firsthand
 * from BUILD-SR-2's StrongroomKnock: the knock at 118 Hz / gain
 * 0.52, the seal family at 240–360 Hz / 0.62):
 *
 *  - BOLT WITHDRAWALS: three clunks on P0's LiftClunks rhythm
 *    (0 · 190 · 420 ms), ASCENDING — pitch 196 → 220 → 247 Hz,
 *    gain 0.44 → 0.52 → 0.62. The sealing descended into the
 *    door; the lifting climbs out of it, the last bolt arriving
 *    at the seal family's own weight. Each withdrawal is one
 *    mass movement in the hand: a short draw, a catch, a seat —
 *    amplitudes falling, never rising, never a buzz.
 *  - THE DOOR'S WEIGHT: when the last bolt clears, the door
 *    itself speaks once — a low 132 Hz body, 150 ms, gain 0.40,
 *    felt as the mass beginning to move, with a long low haptic
 *    settle under it. The door does not chime its freedom; it
 *    groans its weight, once.
 *  - THE AJAR DAY HAS NO VOICE. A fallen day fires no clunks and
 *    no door body: the door already stands open in the dark, and
 *    a ceremony sound over it would be theatre over a failure.
 *    Silence is that day's sound, by design.
 *
 * Slice note (plan §6): Slice 1 builds this working-and-good —
 * designed figures, tuned on paper against the engine's voices.
 * Their in-hand maximum is Slices 6/7's work, judged in the
 * running app. UNPROVEN-ON-DEVICE (Audit A G-7).
 */
object StrongroomLift {

    /** One bolt withdrawal's voice. Timings mirror P0's LiftClunks
     *  by citation (see LiftingPlate.LiftingScore). */
    data class BoltVoice(val atMs: Int, val pitchHz: Float, val gain: Float)

    val BOLT_VOICES: List<BoltVoice> = listOf(
        BoltVoice(atMs = 0, pitchHz = 196f, gain = 0.44f),
        BoltVoice(atMs = 190, pitchHz = 220f, gain = 0.52f),
        BoltVoice(atMs = 420, pitchHz = 247f, gain = 0.62f)
    )

    /** Each withdrawal's body length, ms (inside the engine's
     *  160 ms transient law, as the knock's body is). */
    const val BOLT_DURATION_MS: Int = 130

    /** The dark chord ratio the door family speaks (the knock's
     *  own figure, carried — one family, one interval). */
    const val CHORD_RATIO: Float = 0.9f

    /** Haptic segments per withdrawal (ms): lead-in 0, the draw,
     *  the catch, the seat. Amplitudes pair 1:1 after the lead-in
     *  and fall by design — iron leaving its hole, not striking. */
    val BOLT_HAPTIC_TIMINGS_MS = longArrayOf(0, 30, 22, 14)
    val BOLT_HAPTIC_AMPLITUDES = intArrayOf(0, 170, 110, 64)

    /** Pre-API-26 fallback: the draw alone, as a one-shot. */
    const val BOLT_HAPTIC_FALLBACK_MS: Long = 30L

    // -- The door's weight -------------------------------------------

    /** The door body's pitch: just above the knock (the door is
     *  heavier than a fist on it), far below the seal family. */
    const val DOOR_PITCH_HZ: Float = 132f

    /** The door body's length, ms. */
    const val DOOR_DURATION_MS: Int = 150

    /** Under the last bolt: the mass moves, it does not announce. */
    const val DOOR_GAIN: Float = 0.40f

    /** Haptic settle under the door's movement (ms): one long low
     *  commitment, amplitude held then falling — weight, not buzz. */
    val DOOR_HAPTIC_TIMINGS_MS = longArrayOf(0, 90, 60)
    val DOOR_HAPTIC_AMPLITUDES = intArrayOf(0, 120, 56)

    const val DOOR_HAPTIC_FALLBACK_MS: Long = 90L
}
