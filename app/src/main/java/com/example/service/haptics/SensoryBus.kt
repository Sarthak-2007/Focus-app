package com.example.service.haptics

/**
 * THE BUS — the only public door to the felt layer (FEEL-SR-5).
 *
 * THE HARD RULE, ENFORCED IN CODE: a caller fires an
 * [HapticEvent]; the bus reads that event's ONE registry row
 * and dispatches all three senses from it, in one call, in a
 * fixed order (haptic → sound → motion). There is no public
 * API on this bus — or anywhere in this package — for firing a
 * waveform, a cue, or a motion separately, and the waveform
 * player receives only [EffectiveHaptic]s the bus itself
 * degraded from registry rows. The three senses cannot drift
 * apart because they were never separate calls.
 *
 * Gates, read at fire time (never cached at construction):
 *  - the persisted haptics FACT ([SoundEffectsSetting] on
 *    Android; injected here as [hapticsEnabled]) — OFF silences
 *    the felt leg only, exactly as the engine has always held:
 *    muting loses a sense, never the event;
 *  - the motor fact ([profile]) — read per firing, so a profile
 *    learned late corrects every later event.
 *
 * Firing is synchronous and total: [fire] returns the [Dispatch]
 * it made — what was asked of each sense — so callers and tests
 * read the same record. A silent-by-law event returns a
 * dispatch of three silences; that is a firing, not a no-op.
 *
 * PRE-EMPTION (FEEL-SR-1 L-5): if the system takes the motor
 * mid-waveform (a contact call rings through by ruling), the
 * ceremony is truncated and is NEVER replayed or resumed —
 * the waveform is testimony, not truth, and a witness does
 * not re-testify. This bus contains no retry, by design.
 */
class SensoryBus(
    private val waveformPlayer: WaveformPlayer,
    private val soundPlayer: SoundPlayer,
    private val motionPlayer: MotionPlayer,
    private val profile: () -> MotorProfile,
    private val hapticsEnabled: () -> Boolean
) {

    /** Fire [event]'s whole row. The only way in. */
    fun fire(event: HapticEvent): Dispatch {
        val row = SensoryRegistry.row(event)

        val effective: EffectiveHaptic = if (hapticsEnabled()) {
            degrade(row.haptic, profile())
        } else {
            EffectiveHaptic.Silent("haptics fact is OFF — the felt leg rests, the event stands")
        }
        waveformPlayer.play(effective, event)

        val soundPlayed = !row.sound.isSilence
        if (soundPlayed) soundPlayer.play(row.sound, event)

        val motionPlayed = !row.motion.isNone
        if (motionPlayed) motionPlayer.play(row.motion, event)

        return Dispatch(
            event = event,
            haptic = effective,
            sound = row.sound,
            soundPlayed = soundPlayed,
            motion = row.motion,
            motionPlayed = motionPlayed
        )
    }

    /** The record of one firing: what each sense was asked. */
    data class Dispatch(
        val event: HapticEvent,
        val haptic: EffectiveHaptic,
        val sound: SoundCue,
        val soundPlayed: Boolean,
        val motion: MotionCue,
        val motionPlayed: Boolean
    )
}

/** The felt leg's hands. Implementations receive ONLY bus-
 *  degraded [EffectiveHaptic]s — never a raw registry waveform —
 *  so no call site can bypass amplitude honesty. */
interface WaveformPlayer {
    fun play(effective: EffectiveHaptic, event: HapticEvent)
}

/** The heard leg's hands (a named cue from the event's row). */
interface SoundPlayer {
    fun play(cue: SoundCue, event: HapticEvent)
}

/** The seen leg's hands (a named cue from the event's row). */
interface MotionPlayer {
    fun play(cue: MotionCue, event: HapticEvent)
}

/** A player that plays nothing — for previews, tests, and
 *  surfaces whose sense is not yet wired. Silence by choice,
 *  recorded as such wherever it is installed. */
object NoOpWaveformPlayer : WaveformPlayer {
    override fun play(effective: EffectiveHaptic, event: HapticEvent) = Unit
}

object NoOpSoundPlayer : SoundPlayer {
    override fun play(cue: SoundCue, event: HapticEvent) = Unit
}

object NoOpMotionPlayer : MotionPlayer {
    override fun play(cue: MotionCue, event: HapticEvent) = Unit
}
