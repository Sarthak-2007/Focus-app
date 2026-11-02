package com.example.service.haptics

import android.content.Context
import com.example.service.SoundEvent
import com.example.service.StrongroomSoundEngine
import com.example.service.TactileSoundEngine

/**
 * THE SOUND ADAPTER (BUILD-SR-19, Stage 13) — the bus's heard
 * leg, and the ONLY bridge between the two registries.
 *
 * R8-4's split, in code:
 *  - [SensoryBus] / [SensoryRegistry] (FEEL-SR-5, BUILD-SR-20's
 *    lane) are the single event table and the only public door:
 *    a surface fires a [HapticEvent]; the bus fires its row's
 *    felt waveform itself, then hands the row's [SoundCue] here.
 *  - The sound registry ([SoundEvent], SOUND-SR-5) is demoted
 *    to the performer's VOICE TABLE behind this adapter: it owns
 *    voices, not events. [VOICES] is the whole mapping — one bus
 *    event to the ordered voice rows that speak it (ceremonies
 *    are sequences). An event absent from [VOICES] has no voice:
 *    either its cue is silence by law, or its voice is a named
 *    debt (see the record, work/build-sr-19/record.md) — this
 *    adapter never invents a voice for it.
 *
 * THE TWIN LAW ON THIS PATH: the bus has already fired the
 * event's felt waveform from its own row, so a dispatch here
 * must never fire the voice table's haptic twin a second time.
 * [dispatchVoices] therefore runs the voice-table engine with
 * the phone's facts copied with `hapticsEnabled = false` — the
 * gate's AUDIO law (sound fact · ringer · DND · night family
 * curves, [com.example.service.SoundGateLaw]) still decides
 * exactly once, and the felt leg stays the bus's alone.
 *
 * SILENCE BY CONSTRUCTION: a [SoundCue.SILENCE] returns before
 * any dispatch; a mapped NAMED_SILENCE voice row (the break's
 * firing) dispatches lawfully and plays nothing, leaving its
 * silence in the dispatch ledger as testimony.
 *
 * Install (the production wiring point, used by
 * StrongroomEngineBridge):
 *   AndroidWaveformPlayer.bus(context,
 *       soundPlayer = StrongroomSoundAdapter.production(context))
 *
 * All figures UNPROVEN-ON-DEVICE (Audit A G-7).
 */
class StrongroomSoundAdapter(
    private val dispatchVoices: (List<SoundEvent>) -> Unit
) : SoundPlayer {

    override fun play(cue: SoundCue, event: HapticEvent) {
        if (cue.isSilence) return
        val voices = VOICES[event] ?: return
        dispatchVoices(voices)
    }

    companion object {

        /**
         * THE MAP — bus event → voice-table rows, in speaking
         * order. Every [HapticEvent] not listed here is disposed
         * by name in BUILD-SR-19's record (silent-by-construction
         * or adapter-ready/voice-owed); the adapter test holds
         * this map against both registries so a renamed event or
         * a dropped voice fails loudly, never silently.
         */
        val VOICES: Map<HapticEvent, List<SoundEvent>> = mapOf(
            // The ceremonies: sequences, spoken in order.
            HapticEvent.DOOR_SEAL to listOf(
                SoundEvent.DOOR_SWING_CLOSE,
                SoundEvent.SEAL_CLUNK_1,
                SoundEvent.SEAL_CLUNK_2,
                SoundEvent.SEAL_CLUNK_3
            ),
            HapticEvent.DOOR_LIFT to listOf(
                SoundEvent.LIFT_BOLT_1,
                SoundEvent.LIFT_BOLT_2,
                SoundEvent.LIFT_BOLT_3,
                SoundEvent.DOOR_OPEN_WEIGHT
            ),
            // The governor's minute, spent and re-driven.
            HapticEvent.GOVERNOR_BOLTS_REDRIVE to listOf(SoundEvent.CRANK_REDRIVE),
            HapticEvent.BOLTS_REDRIVE to listOf(SoundEvent.CRANK_REDRIVE),
            // The signing.
            HapticEvent.SEAL_THROW to listOf(SoundEvent.LEGACY_SEAL_THROW),
            HapticEvent.SEAL_CLUNK_DISARM to listOf(SoundEvent.LEGACY_HARMONIC_SEAL),
            HapticEvent.ROTARY_DETENT to listOf(SoundEvent.LEGACY_ROTARY_DETENT),
            HapticEvent.COMBINATION_STATION to listOf(SoundEvent.COMBINATION_DETENT),
            HapticEvent.COMBINATION_GATE to listOf(SoundEvent.COMBINATION_GATE_CLACK),
            HapticEvent.COMBINATION_FINAL_SEAT to listOf(SoundEvent.COMBINATION_SETTLE),
            HapticEvent.SIGNATURE_LATCH to listOf(SoundEvent.LEGACY_FIRM_CLICK),
            // The sealed face's one event.
            HapticEvent.KNOCK to listOf(SoundEvent.KNOCK),
            // The antechamber.
            HapticEvent.GRILLE_TOUCH to listOf(SoundEvent.GRILLE_DOOR_TOUCH),
            HapticEvent.DRUM_DETENT to listOf(SoundEvent.DRUM_DETENT),
            HapticEvent.DRUM_LAND to listOf(
                SoundEvent.DRUM_LAND_STRIKE,
                SoundEvent.DRUM_LAND_SEAT
            ),
            HapticEvent.DRUM_RETURN to listOf(SoundEvent.DRUM_MINUTE_END),
            HapticEvent.GOVERNOR_CRANK_TURN to listOf(SoundEvent.GOVERNOR_CRANK_TURN),
            HapticEvent.GOVERNOR_SEAL_SNAP to listOf(SoundEvent.LEAD_SEAL_SNAP),
            HapticEvent.CODE_DETENT_SPEND to listOf(SoundEvent.CODE_SHOWING_TICK),
            // The guard & the stretch (SOUND seat, voice-debt
            // clearance, 2026-11-03): the pawl's double click is
            // two voices dispatched back-to-back across the felt
            // row's own gap; the dawn rock is three dying pulses
            // with no terminal voice, as filed.
            HapticEvent.GUARD_TIGHTEN_DETENT to listOf(
                SoundEvent.TIGHTEN_TICK_1,
                SoundEvent.TIGHTEN_TICK_2
            ),
            HapticEvent.GUARD_TIGHTEN_SEAT to listOf(SoundEvent.TIGHTEN_SEAT_CLICK),
            HapticEvent.GUARD_RAISE_ROCK to listOf(
                SoundEvent.RAISE_ROCK_1,
                SoundEvent.RAISE_ROCK_2,
                SoundEvent.RAISE_ROCK_3
            ),
            HapticEvent.STRETCH_SCRAPE to listOf(SoundEvent.CARVE_SCRAPE),
            HapticEvent.STRETCH_CARVE to listOf(
                SoundEvent.CARVE_BLOW,
                SoundEvent.CARVE_CHIP
            ),
            // The break: its firing is a NAMED_SILENCE in the
            // voice table (dispatched, recorded, unplayed); its
            // end has its own voice.
            HapticEvent.BREAK_ARRIVAL to listOf(SoundEvent.BREAK_FIRING),
            HapticEvent.BREAK_END_CUE to listOf(SoundEvent.BREAK_END_RETURN),
            HapticEvent.BREAK_END_SUMMONS to listOf(SoundEvent.LEGACY_BREAK_SUMMONS),
            HapticEvent.BREAK_WARNING to listOf(SoundEvent.LEGACY_WARNING_KNOCK),
            // The plate.
            HapticEvent.PLATE_STRIKE to listOf(SoundEvent.PLATE_STRIKE),
            HapticEvent.PLATE_STRIKE_FAILURE to listOf(SoundEvent.PLATE_STRIKE_FAILURE),
            // The drawer.
            HapticEvent.DRAWER_OPEN to listOf(SoundEvent.DRAWER_SLIDE),
            // The legacy voices, subsumed by the bus census.
            // LATCH_RELEASE resolves to the same named performer:
            // the performer's direction parameterisation stays
            // with its standing direct call sites (named in the
            // record) — the registry row names the voice, not a
            // parameterisation, exactly as SOUND-SR-5 filed it.
            HapticEvent.LATCH_ENGAGE to listOf(SoundEvent.LEGACY_LATCH),
            HapticEvent.LATCH_RELEASE to listOf(SoundEvent.LEGACY_LATCH),
            HapticEvent.SEAT_KNOCK to listOf(SoundEvent.LEGACY_SEAT_KNOCK),
            HapticEvent.DOOR_ARRIVAL to listOf(SoundEvent.LEGACY_DOOR_ARRIVAL),
            HapticEvent.DOOR_ARM to listOf(SoundEvent.LEGACY_DOOR_ARM),
            HapticEvent.DOOR_RETURN to listOf(SoundEvent.LEGACY_DOOR_RETURN),
            HapticEvent.DOOR_PEEK to listOf(SoundEvent.LEGACY_DOOR_PEEK),
            HapticEvent.DOOR_EXIT to listOf(SoundEvent.LEGACY_DOOR_EXIT),
            HapticEvent.TEAR_PULSE to listOf(SoundEvent.LEGACY_TEAR_PULSE),
            HapticEvent.COMPLETION_PULSE to listOf(SoundEvent.LEGACY_COMPLETION_PULSE)
        )

        /** The production adapter: a [StrongroomSoundEngine]
         *  over the standing [TactileSoundEngine], its facts
         *  read live with the felt leg suppressed (see the twin
         *  law above). A dead engine degrades to silence — a
         *  sound failure never takes the event down. */
        fun production(context: Context): StrongroomSoundAdapter {
            val appContext = context.applicationContext
            val engine = StrongroomSoundEngine(
                performer = com.example.service.TactileSoundPerformer(
                    TactileSoundEngine(appContext)
                ),
                factsProvider = {
                    StrongroomSoundEngine.readFacts(appContext)
                        .copy(hapticsEnabled = false)
                }
            )
            return StrongroomSoundAdapter { voices ->
                voices.forEach { voice ->
                    try {
                        engine.dispatch(voice)
                    } catch (_: Exception) {
                        // Degrade to silence, never to a crash.
                    }
                }
            }
        }
    }
}
