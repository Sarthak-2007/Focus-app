package com.example.service

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

/**
 * THE PLAYBACK LAYER (SOUND-SR-5) — the machinery the sound
 * seats' assets run on. One door: [dispatch] takes a named event
 * from the registry ([SoundEvent]), and this class does the rest:
 *
 *  1. GATE — [SoundGateLaw] judges the phone's facts ONCE. The
 *     registry path never takes a second scaling downstream (the
 *     performer's exact transient bypasses the engine's internal
 *     night gate), so a gain is scaled by exactly one authority.
 *  2. FELT FIRST — the haptic twin fires on the CALLER's thread,
 *     synchronously, before any audio work. The knock lands WITH
 *     the touch because its felt path never queues: vibrate() is
 *     the whole cost. Audio follows through the engine's synth
 *     queue (offer wakes its poll immediately).
 *  3. ONE DISPATCH TABLE — [TactileSoundPerformer] is the only
 *     place a registry row resolves to an engine call. Legacy
 *     rows name their performer; registry rows play their figures
 *     through the generic exact transient. No other file maps an
 *     event to a voice.
 *
 * LATENCY, HONEST (the budget lives on [LatencyClass]):
 * the caller-thread segment — gate + vibrate() + queue offer —
 * is what code controls, and [DispatchLedger] measures exactly
 * that, per event, in nanoseconds. The path's device-side costs
 * are named, not hidden: audio focus and AudioTrack construction
 * are pre-paid by [TactileSoundEngine.warmUpSoundPath] at
 * seal/prime time (a reach never pays them first); synthesis of
 * a ≤160 ms transient is ≤7,056 samples of standing math, and
 * repeat voices are served from the engine's render cache. The
 * HAL → speaker/motor segment is UNPROVEN-ON-DEVICE (Audit A
 * G-7): no number in this file claims it.
 *
 * A torn-down or absent performer degrades to the felt twin
 * where one exists and to silence where none does — a sound
 * failure must never take the event, or the surface, down.
 */

/** The seam the dispatch plays through. Production binds
 *  [TactileSoundPerformer]; tests bind a fake and read the calls. */
interface SoundPerformer {
    /** Play a registry voice at an already gate-scaled gain. */
    fun transient(voice: VoiceSpec, gainScale: Float, unattended: Boolean)

    /** Fire a registry waveform on this thread, now. */
    fun haptic(spec: HapticSpec)

    /** Resolve a legacy row's named performer. Legacy performers
     *  carry the engine's own standing gate (haptic before audio
     *  gate, night/DND scaling inside), which agrees with the
     *  registry gate's verdict — dispatch always calls, and the
     *  outcome is the decided one on both paths. */
    fun legacy(performer: String, unattended: Boolean)

    /** Pre-pay the audio path's fixed costs (focus, track). */
    fun warmUp()
}

/** Per-event caller-thread path record. [hapticMicros] is the
 *  felt path's cost (gate → vibrate() issued); [totalMicros] the
 *  whole dispatch. Read against the event's [LatencyClass]
 *  budget by tests and, on device, by whoever holds the proof. */
data class DispatchRecord(
    val event: SoundEvent,
    val law: String,
    val audioGainScale: Float,
    val hapticFired: Boolean,
    val hapticMicros: Long,
    val totalMicros: Long
)

/** The ledger: last record per event + a total count. Bounded by
 *  the registry's size by construction (one slot per event). */
class DispatchLedger {
    private val last = mutableMapOf<SoundEvent, DispatchRecord>()

    @Volatile
    var dispatchCount: Long = 0
        private set

    @Synchronized
    fun record(entry: DispatchRecord) {
        last[entry.event] = entry
        dispatchCount++
    }

    @Synchronized
    fun lastFor(event: SoundEvent): DispatchRecord? = last[event]

    @Synchronized
    fun eventsSeen(): Set<SoundEvent> = last.keys.toSet()
}

class StrongroomSoundEngine(
    private val performer: SoundPerformer,
    private val factsProvider: () -> SoundFacts,
    private val nanoClock: () -> Long = { System.nanoTime() }
) {
    val ledger = DispatchLedger()

    /**
     * Dispatch one named event. Returns the gate's decision as
     * recorded — a NAMED_SILENCE dispatch returns its silence
     * lawfully, with nothing fired. Never throws for a sound
     * reason: performer failures degrade to the felt twin /
     * silence and the record still lands.
     */
    fun dispatch(event: SoundEvent): DispatchRecord {
        val started = nanoClock()
        val spec = SoundRegistry.spec(event)
        val decision = SoundGateLaw.decide(spec, factsProvider())

        var hapticFired = false
        var hapticDoneAt = started
        // Felt first, on this thread — but only when the row
        // carries its OWN waveform. Legacy rows' performers fire
        // their own waveforms as part of the legacy call below.
        if (decision.hapticFires && spec.haptic != null) {
            try {
                performer.haptic(spec.haptic)
                hapticFired = true
            } catch (_: Exception) {
                // A dead vibrator loses the twin, never the event.
            }
            hapticDoneAt = nanoClock()
        }

        if (spec.legacyPerformer != null) {
            // Legacy rows always reach their performer: the
            // engine's own standing gate inside it produces the
            // same outcome the registry gate decided (its haptic
            // fires before its audio gate, so the felt twin
            // survives a silenced voice — the twin law holds on
            // this path too). The record reports the gate's
            // verdict for the row.
            hapticFired = decision.hapticFires
            try {
                performer.legacy(spec.legacyPerformer, !spec.attended)
            } catch (_: Exception) {}
            hapticDoneAt = nanoClock()
        } else if (spec.voice != null && decision.audioGainScale > 0f) {
            try {
                performer.transient(spec.voice, decision.audioGainScale, !spec.attended)
            } catch (_: Exception) {}
        }

        val finished = nanoClock()
        val entry = DispatchRecord(
            event = event,
            law = decision.law,
            audioGainScale = decision.audioGainScale,
            hapticFired = hapticFired,
            hapticMicros = (hapticDoneAt - started) / 1_000,
            totalMicros = (finished - started) / 1_000
        )
        ledger.record(entry)
        return entry
    }

    /** Pre-pay the audio path before the events that cannot wait
     *  (call at seal/prime time and when the sealed face shows). */
    fun warmUp() {
        try {
            performer.warmUp()
        } catch (_: Exception) {}
    }

    companion object {
        /** The production binding: a [TactileSoundEngine] behind
         *  the dispatch table, facts read live from the phone. */
        fun production(context: Context, engine: TactileSoundEngine): StrongroomSoundEngine {
            val appContext = context.applicationContext
            return StrongroomSoundEngine(
                performer = TactileSoundPerformer(engine),
                factsProvider = { readFacts(appContext) }
            )
        }

        /** The facts read: the persisted sound/haptics facts, the
         *  ringer's word, DND's word, clock night. Every read is
         *  guarded — an unreadable fact reads as its permissive
         *  value (a broken query must never invent a silence). */
        fun readFacts(context: Context): SoundFacts {
            val ringer = try {
                val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                am != null && am.ringerMode != AudioManager.RINGER_MODE_NORMAL
            } catch (_: Exception) {
                false
            }
            val dnd = try {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as? NotificationManager
                nm != null &&
                    nm.currentInterruptionFilter !=
                    NotificationManager.INTERRUPTION_FILTER_ALL
            } catch (_: Exception) {
                false
            }
            return SoundFacts(
                soundEnabled = SoundEffectsSetting.isEnabled(context),
                hapticsEnabled = SoundEffectsSetting.isHapticsEnabled(context),
                ringerSilences = ringer,
                doNotDisturb = dnd,
                night = NightSignal.isNightAt(System.currentTimeMillis())
            )
        }
    }
}

/**
 * THE SINGLE DISPATCH TABLE onto the shipped engine. Every
 * legacy name resolves here and nowhere else; registry voices
 * play through the engine's exact-transient hook at the gate's
 * scale (one scaling authority). Parameterised legacy voices
 * (the seat knock's mass arguments, the latch's direction, the
 * seal's arming) resolve to their standing default spend — the
 * registry row names the voice, not a parameterisation; a caller
 * that needs the parameterised form keeps its existing direct
 * call, and that call is the legacy surface's own business.
 */
/** Every legacy performer name the dispatch table resolves.
 *  SoundEventRegistryTest holds the registry's legacy names
 *  equal to this set, so a row naming a performer the table
 *  cannot resolve — or a table entry no row names — fails the
 *  build instead of degrading to silence in production. */
val TACTILE_PERFORMER_NAMES: Set<String> = setOf(
    "playRotaryDetent", "playFirmClick", "playSeatKnock",
    "playMechanicalLatch", "playBreakEndCue", "playDoorArrival",
    "playDoorArm", "playDoorReturn", "playDoorPeek", "playDoorExit",
    "playBreakEndSummons", "playBreakWarningKnock",
    "playHarmonicSeal", "playSealThrow", "playTearPulse",
    "playCompletionPulse", "playPeekWarningTick", "startSoundscape"
)

class TactileSoundPerformer(
    private val engine: TactileSoundEngine
) : SoundPerformer {

    override fun transient(voice: VoiceSpec, gainScale: Float, unattended: Boolean) {
        engine.playRegistryTransientExact(
            fundamentalHz = voice.pitchHz,
            durationMs = voice.durationMs,
            gain = voice.gain * gainScale,
            chordRatio = voice.chordRatio,
            unattended = unattended
        )
    }

    override fun haptic(spec: HapticSpec) {
        engine.playRegistryHaptic(
            timingsMs = spec.timingsMs.toLongArray(),
            amplitudes = spec.amplitudes.toIntArray(),
            fallbackMs = spec.fallbackMs
        )
    }

    override fun legacy(performer: String, unattended: Boolean) {
        if (performer !in TACTILE_PERFORMER_NAMES) return
        when (performer) {
            "playRotaryDetent" -> engine.playRotaryDetent()
            "playFirmClick" -> engine.playFirmClick()
            "playSeatKnock" -> engine.playSeatKnock(20, 1.0f, 320f, unattended)
            "playMechanicalLatch" -> engine.playMechanicalLatch(true, unattended)
            "playBreakEndCue" -> engine.playBreakEndCue()
            "playDoorArrival" -> engine.playDoorArrival()
            "playDoorArm" -> engine.playDoorArm()
            "playDoorReturn" -> engine.playDoorReturn()
            "playDoorPeek" -> engine.playDoorPeek()
            "playDoorExit" -> engine.playDoorExit()
            "playBreakEndSummons" -> engine.playBreakEndSummons()
            "playBreakWarningKnock" -> engine.playBreakWarningKnock()
            "playHarmonicSeal" -> engine.playHarmonicSeal(arming = false)
            "playSealThrow" -> engine.playSealThrow(unattended)
            "playTearPulse" -> engine.playTearPulse()
            "playCompletionPulse" -> engine.playCompletionPulse(unattended)
            "playPeekWarningTick" -> engine.playPeekWarningTick()
            "startSoundscape" -> engine.startSoundscape()
            // An unresolvable name is a registry bug, caught by
            // SoundEventRegistryTest's performer-table test; in
            // production it degrades to silence, never a crash.
            else -> Unit
        }
    }

    override fun warmUp() {
        engine.warmUpSoundPath()
    }
}
