package com.example.service

/**
 * THE SOUND REGISTRY — every designed event, named once (SOUND-SR-5).
 *
 * Until this file, Monolith's voices lived as hand-written playX()
 * methods on [TactileSoundEngine], their figures hard-coded at each
 * method, called from dozens of scattered sites — and the
 * Strongroom's designed events (the sealing clunks, the drum, the
 * crank, the plate strike) did not exist as named things at all.
 * This registry is the single naming place:
 *
 *  - EVERY designed event is a row here: the Strongroom day, the
 *    designed silences, and every legacy engine voice, so no voice
 *    exists outside the registry.
 *  - NO ASSET IS HARD-CODED TWICE. A Strongroom event's figures
 *    (pitch / duration / gain / chord / haptic waveform) live ONLY
 *    in its row. A legacy event's figures stay ONLY in the engine,
 *    and its row points at the engine's named performer instead —
 *    a row carries figures XOR a legacy performer, never both
 *    (test-enforced, SoundEventRegistryTest).
 *  - NO EVENT CLOSES UNDECIDED (T46's staircase law): every row
 *    carries its decision — SOUNDS, FELT_ONLY, or NAMED_SILENCE.
 *    Silence in this app is designed, never leftover: the held
 *    call's arrival, the ajar day, the desk clock's forming are
 *    NAMED_SILENCE rows, dispatched lawfully to nothing.
 *  - THE FELT-TWIN LAW: every SOUNDS row carries its haptic twin,
 *    so when sound is off the day stays legible by feel alone
 *    (the Slice 7 debt, collected by design here). Sole named
 *    exception: the soundscape — a continuous ambient lane has no
 *    twin by nature.
 *
 * Sound seats' specs slot in HERE and nowhere else: when a
 * SOUND-SR-1..4 spec lands, it replaces a row's figures (rows
 * whose figures no sound seat has specced yet say PROVISIONAL in
 * their source, citing the governing filing). No call site ever
 * changes shape.
 *
 * Pure Kotlin — no Android types — so the registry and the gate
 * law below are pinned by plain unit tests off-device.
 *
 * R8-4 DEMOTION (Stage 13 integration, BUILD-SR-19): this
 * registry is the sound performer's VOICE TABLE, not the app's
 * event table. The single public door is FEEL-SR-5's
 * `SensoryBus` / `SensoryRegistry` (service/haptics/); production
 * call sites fire `HapticEvent`s there, and the bus's sound
 * adapter (`StrongroomSoundAdapter`) resolves each row's cue
 * into rows of THIS table behind the bus. No production call
 * site dispatches a [SoundEvent] directly.
 */

/** The volume-curve family a voice belongs to. The family is what
 *  the gate scales (see [SoundGateLaw]): a private knock in the
 *  hand and a ceremony in the room obey different curves. */
enum class SoundFamily {
    /** In the hand, for the man holding the phone only: the knock,
     *  detents, the grille touch. Low, dull, private (CRITIC SO-3:
     *  a knock that announces him to the room is shame engineered
     *  as feedback). Night-scaled hard; DND-silenced. */
    PRIVATE_HAND,

    /** The room's mechanisms: the sealing, the lifting, the plate.
     *  Ceremony happens in his quiet hours and is tuned for them.
     *  Softened under night/DND, never deleted while attended —
     *  the seal voice's standing law (NightSignal). */
    ROOM_CEREMONY,

    /** Machinery-fired voices nobody is touching the phone to
     *  hear: break boundaries, the summons class. The ringer's
     *  word and DND bind them fully (issue #150). */
    MACHINERY_UNATTENDED,

    /** The one ruled continuous voice (the session soundscape).
     *  Its own lane, its own master gain; registered here so the
     *  registry is complete, dispatched through its own start/stop
     *  law, never through the transient path. */
    AMBIENT
}

/** T46's decision column: what an event does, decided by name. */
enum class SoundDecision {
    /** A voice + its felt twin. */
    SOUNDS,

    /** No voice, by design — the event is carried as touch alone
     *  (hard stops, the peek warning). */
    FELT_ONLY,

    /** Neither voice nor felt event, by design — the designed
     *  silences. Dispatching one is lawful and does nothing. */
    NAMED_SILENCE
}

/** How fast an event must land, as a budget on the caller-thread
 *  segment the code controls (dispatch → haptic issued + audio
 *  queued). The device segment (HAL → speaker / motor) is
 *  UNPROVEN-ON-DEVICE (Audit A G-7) and is never claimed here. */
enum class LatencyClass(val budgetMs: Long) {
    /** With the touch: the knock, detents, refusals. A knock that
     *  lands late reads as a toy (CRITIC W-5 / Slice 6 pass bar). */
    TOUCH_COINCIDENT(50),

    /** Ceremony steps: each clunk/bolt lands on its sequence beat. */
    CEREMONY_STEP(120),

    /** Machinery voices: timely, not coincident. */
    UNATTENDED_TIMELY(500),

    /** No budget: the event makes no sound to be late with. */
    NONE(0)
}

/** One voice's figures — the asset, as data. Rendered by the
 *  engine's standing synth (4 partials, dual decays); a voice is
 *  these four numbers and nothing else, so two rows can never
 *  secretly share an asset: sharing is a named, visible act. */
data class VoiceSpec(
    val pitchHz: Float,
    val durationMs: Int,
    val gain: Float,
    val chordRatio: Float
)

/** The felt twin: a waveform in the engine's standing vocabulary
 *  (segment timings pair 1:1 with amplitudes after the lead-in 0;
 *  [fallbackMs] is the pre-API-26 one-shot). */
data class HapticSpec(
    val timingsMs: List<Long>,
    val amplitudes: List<Int>,
    val fallbackMs: Long
)

/** One registry row. [voice] XOR [legacyPerformer] for SOUNDS rows
 *  (see the file doc); [haptic] is null exactly for NAMED_SILENCE
 *  rows, legacy-performed rows (their waveform lives in the
 *  engine), and the soundscape exception. */
data class SoundEventSpec(
    val event: SoundEvent,
    val family: SoundFamily,
    val decision: SoundDecision,
    /** True when a person is touching the phone to cause this
     *  event; machinery-fired events are unattended and DND binds
     *  them (issue #150). A registry fact, not a call-site flag. */
    val attended: Boolean,
    val voice: VoiceSpec?,
    val haptic: HapticSpec?,
    /** The engine's existing named performer for legacy voices
     *  (e.g. "playSealThrow"). The single dispatch table in
     *  [TactileSoundPerformer] is the only place this name is
     *  resolved to a call. */
    val legacyPerformer: String?,
    /** The tear's standing exemption (Sarthak's Oct 30 ruling):
     *  full gain at all hours — the night/DND *gain* gate never
     *  scales it. Hard gates (sound fact, ringer) still bind. */
    val exemptFromNightGain: Boolean,
    val latencyClass: LatencyClass,
    /** Where the figures come from, by name. PROVISIONAL = no
     *  sound seat has specced this event yet; the figures are
     *  this lane's, cited to the governing filing, and a seat's
     *  spec replaces them in this row only. */
    val source: String
)

/** Every designed event, named. The order is the day's order. */
enum class SoundEvent {
    // -- The signing & the sealing (the Seal's act, first sitting) --
    COMBINATION_DETENT,
    COMBINATION_SETTLE,
    COMBINATION_GATE_CLACK,
    DOOR_SWING_CLOSE,
    SEAL_CLUNK_1,
    SEAL_CLUNK_2,
    SEAL_CLUNK_3,

    // -- The sealed hours --
    KNOCK,
    GRILLE_DOOR_TOUCH,
    DESK_CLOCK_FORMING,
    SESSION_NOTIFICATION,
    CONTACT_CALL_THROUGH,
    HELD_CALL_ARRIVAL,
    CODE_SHOWING_TICK,

    // -- The antechamber: the drum & the governor's crank --
    DRUM_DETENT,
    DRUM_MINUTE_END,
    DRUM_LAND_STRIKE,
    DRUM_LAND_SEAT,
    DRUM_SPENT_REFUSAL,
    GOVERNOR_CRANK_TURN,
    LEAD_SEAL_SNAP,
    CRANK_SPENT_REFUSAL,
    CRANK_REDRIVE,

    // -- The guard & the stretch (the pawl, the dawn rock, the
    //    carve) and the drawer — the voice-debt set, specced by
    //    the SOUND seat 2026-11-03 --
    TIGHTEN_TICK_1,
    TIGHTEN_TICK_2,
    TIGHTEN_SEAT_CLICK,
    RAISE_ROCK_1,
    RAISE_ROCK_2,
    RAISE_ROCK_3,
    CARVE_SCRAPE,
    CARVE_BLOW,
    CARVE_CHIP,
    DRAWER_SLIDE,

    // -- The break (a firing of the time-lock, invocable by no one) --
    BREAK_FIRING,
    BREAK_END_RETURN,

    // -- The lifting & the plate --
    LIFT_BOLT_1,
    LIFT_BOLT_2,
    LIFT_BOLT_3,
    DOOR_OPEN_WEIGHT,
    PLATE_STRIKE,
    PLATE_STRIKE_FAILURE,
    AJAR_DAY,

    // -- The legacy voices (pre-Strongroom surfaces; figures live
    //    in the engine, rows point at their performers) --
    LEGACY_ROTARY_DETENT,
    LEGACY_FIRM_CLICK,
    LEGACY_SEAT_KNOCK,
    LEGACY_LATCH,
    LEGACY_BREAK_END_CUE,
    LEGACY_DOOR_ARRIVAL,
    LEGACY_DOOR_ARM,
    LEGACY_DOOR_RETURN,
    LEGACY_DOOR_PEEK,
    LEGACY_DOOR_EXIT,
    LEGACY_BREAK_SUMMONS,
    LEGACY_WARNING_KNOCK,
    LEGACY_HARMONIC_SEAL,
    LEGACY_SEAL_THROW,
    LEGACY_TEAR_PULSE,
    LEGACY_COMPLETION_PULSE,
    PEEK_WARNING_TICK,

    // -- The one continuous voice --
    SOUNDSCAPE
}

/**
 * THE REGISTRY. One row per event; [ALL] is complete by
 * construction and the tests hold it so (every enum entry named
 * exactly once, performer keys unique, twins present).
 */
object SoundRegistry {

    // Shared waveform shapes, named once — a shape reused by
    // several rows is a visible, single definition, not a copy.
    private val MASS_STRIKE = HapticSpec(
        timingsMs = listOf(0, 42, 30, 16),
        amplitudes = listOf(0, 255, 96, 48),
        fallbackMs = 42
    )
    private val LATCH_ENGAGE = HapticSpec(
        timingsMs = listOf(0, 11, 26, 9),
        amplitudes = listOf(0, 215, 0, 150),
        fallbackMs = 4
    )
    private val LATCH_RELEASE = HapticSpec(
        timingsMs = listOf(0, 9, 22, 10),
        amplitudes = listOf(0, 165, 0, 195),
        fallbackMs = 4
    )
    private val DETENT_TICK = HapticSpec(
        timingsMs = listOf(0, 7, 5),
        amplitudes = listOf(0, 180, 70),
        fallbackMs = 1
    )
    private val HARD_STOP = HapticSpec(
        timingsMs = listOf(0, 26, 18),
        amplitudes = listOf(0, 210, 90),
        fallbackMs = 26
    )
    private fun row(
        event: SoundEvent,
        family: SoundFamily,
        decision: SoundDecision,
        attended: Boolean,
        voice: VoiceSpec? = null,
        haptic: HapticSpec? = null,
        legacyPerformer: String? = null,
        exemptFromNightGain: Boolean = false,
        latencyClass: LatencyClass = LatencyClass.NONE,
        source: String
    ) = SoundEventSpec(
        event, family, decision, attended, voice, haptic,
        legacyPerformer, exemptFromNightGain, latencyClass, source
    )

    val ALL: List<SoundEventSpec> = listOf(
        // -- Signing & sealing ------------------------------------
        row(
            SoundEvent.COMBINATION_DETENT, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(680f, 6, 0.38f, 1.0f),
            haptic = DETENT_TICK,
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — engine's standing detent voice " +
                "(playRotaryDetent figures), named for the signing wheel; " +
                "SOUND seat spec replaces in this row"
        ),
        row(
            SoundEvent.COMBINATION_SETTLE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(620f, 12, 0.50f, 1.1f),
            haptic = HapticSpec(listOf(0, 13, 20, 8), listOf(0, 225, 0, 135), 8),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — engine's firm-click class, for the " +
                "combination's settle at the signature"
        ),
        row(
            SoundEvent.COMBINATION_GATE_CLACK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(340f, 30, 0.55f, 2.76f),
            haptic = HapticSpec(listOf(0, 35, 28), listOf(0, 0, 165), 28),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (voice-debt clearance, 2026-11-03): " +
                "FEEL-SR-3 C1.2's gate given its voice — one heavy steel " +
                "clack where the felt void ends; 340 Hz sits under the " +
                "station tick (680), chord 2.76 is the steel-bar partial"
        ),
        row(
            SoundEvent.DOOR_SWING_CLOSE, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(58f, 136, 0.40f, 2.76f),
            haptic = HapticSpec(listOf(0, 90, 60), listOf(0, 120, 56), 90),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 (Stage 13 integration, BUILD-SR-19): the " +
                "swing-shut CONTACT — f0 58 Hz, peak 0.40 (below clunk 1's " +
                "0.62 by law), dominant partial 2.76; duration is the " +
                "synth's 136 ms ceiling against the filed 1,150 ms render " +
                "(approach rumble + hinge mass are long-form layers the " +
                "transient voice model cannot carry — named, not faked)"
        ),
        // The three clunks (Audit A §2.1, ledger A-3), figures now
        // SOUND-SR-1's filed set (BUILD-SR-19): pitch DESCENDS
        // 191 → 143 → 97 Hz while weight ASCENDS — stem peaks
        // 0.62 → 0.70 → 0.80 (R7-4: the last clunk is the heaviest
        // fact). Durations are SOUND-SR-1's measured −20 dB body
        // lengths (97/116/171 ms), the third clamped at the synth's
        // 136 ms ceiling. Chord 2.76 = the dominant inharmonic
        // partial of SOUND-SR-1's steel-bar set [1, 2.76, 5.40,
        // 8.93]; the transient synth renders fundamental + one
        // chord partial, so the full set lives in SOUND-SR-1's
        // MechanismSounds — named limitation, not a silent cut.
        row(
            SoundEvent.SEAL_CLUNK_1, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(191f, 97, 0.62f, 2.76f),
            haptic = HapticSpec(listOf(0, 22, 30, 10), listOf(0, 205, 0, 120), 22),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 CLUNK_1: f0 191 Hz · body τ 46 ms · " +
                "peak 0.62 · burst lowpass 1,500 Hz; haptic twin is " +
                "SOUND-SR-1's filed proposal to FEEL-SR-1"
        ),
        row(
            SoundEvent.SEAL_CLUNK_2, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(143f, 116, 0.70f, 2.76f),
            haptic = HapticSpec(listOf(0, 26, 36, 13), listOf(0, 230, 0, 150), 26),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 CLUNK_2: f0 143 Hz · body τ 62 ms · " +
                "peak 0.70 · burst lowpass 1,150 Hz"
        ),
        row(
            SoundEvent.SEAL_CLUNK_3, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(97f, 136, 0.80f, 2.76f),
            haptic = HapticSpec(listOf(0, 32, 44, 18), listOf(0, 255, 0, 185), 32),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 CLUNK_3: f0 97 Hz · body τ 92 ms · " +
                "peak 0.80 (the set's loudest sample) · burst lowpass " +
                "820 Hz; −20 dB body 171 ms clamped at the synth's " +
                "136 ms ceiling, the tail carried by the haptic twin"
        ),

        // -- Sealed hours ------------------------------------------
        row(
            SoundEvent.KNOCK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(118f, 110, 0.52f, 0.9f),
            haptic = MASS_STRIKE,
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "BUILD-SR-2's StrongroomKnock spec (faces branch): " +
                "118 Hz / 110 ms / 0.52 / chord 0.9, mass-strike waveform " +
                "[0,42,30,16]@[0,255,96,48] — canonical here at composition"
        ),
        row(
            SoundEvent.GRILLE_DOOR_TOUCH, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(520f, 16, 0.54f, 1.2f),
            haptic = LATCH_ENGAGE,
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — the antechamber's deliberate second " +
                "touch (Audit A §2.3b), latch-engage class"
        ),
        row(
            SoundEvent.DESK_CLOCK_FORMING, SoundFamily.PRIVATE_HAND,
            SoundDecision.NAMED_SILENCE, attended = true,
            source = "Ledger E-3/A-9: the forming record forms in " +
                "stillness — no numbers, no voice, no felt event (CRITIC W-3b)"
        ),
        row(
            SoundEvent.SESSION_NOTIFICATION, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.NAMED_SILENCE, attended = false,
            source = "Ledger E-4 / Audit A G-6: the session's only " +
                "lock-screen presence is one plain line under the OS's " +
                "own presentation — the app authors no sound for it"
        ),
        row(
            SoundEvent.CONTACT_CALL_THROUGH, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.NAMED_SILENCE, attended = false,
            source = "His ruling (Audit A §2.3c): contacts ring through " +
                "natively — the app adds no voice and no felt event (CRITIC W-6a)"
        ),
        row(
            SoundEvent.HELD_CALL_ARRIVAL, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.NAMED_SILENCE, attended = false,
            source = "His ruling + CRITIC W-6b / SO-5: an unknown call " +
                "lands held with a quiet dot — no sound, no buzz, no popup"
        ),
        row(
            SoundEvent.CODE_SHOWING_TICK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(680f, 6, 0.25f, 1.0f),
            haptic = HapticSpec(listOf(0, 5, 4), listOf(0, 120, 50), 1),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — ledger B-18: a code showing is a " +
                "taking; the quietest detent in the registry"
        ),

        // -- Antechamber mechanisms --------------------------------
        row(
            SoundEvent.DRUM_DETENT, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(560f, 10, 0.44f, 1.0f),
            haptic = HapticSpec(listOf(0, 10, 6), listOf(0, 200, 80), 4),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — the revolving drum's detent landing " +
                "(Audit A §2.3d); governor-heavy, never a toy spin (CRITIC W-7d)"
        ),
        row(
            SoundEvent.DRUM_MINUTE_END, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(520f, 16, 0.40f, 0.9f),
            haptic = LATCH_RELEASE,
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "PROVISIONAL — the minute ends as designed: the app " +
                "recedes; a soft release, no negotiation voice (CRITIC W-7)"
        ),
        row(
            SoundEvent.DRUM_LAND_STRIKE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(185f, 34, 0.58f, 1.2f),
            haptic = HapticSpec(listOf(0, 14), listOf(0, 200), 14),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (voice-debt clearance, 2026-11-03): " +
                "ledger A-7's land, first half — the strike; brass- " +
                "weighted (chord 1.2), the heaviest voice the drum has"
        ),
        row(
            SoundEvent.DRUM_LAND_SEAT, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(148f, 42, 0.46f, 1.2f),
            haptic = HapticSpec(listOf(0, 16, 10), listOf(0, 0, 130), 10),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): the land's seat — lower " +
                "and softer after the strike, across the felt row's own " +
                "16 ms gap; strike + seat together are the drum landing"
        ),
        row(
            SoundEvent.DRUM_SPENT_REFUSAL, SoundFamily.PRIVATE_HAND,
            SoundDecision.FELT_ONLY, attended = true,
            haptic = HARD_STOP,
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Audit A §2.3d + CRITIC H-3: the crank will not turn — " +
                "a mechanical refusal felt in the hand, never an error buzz"
        ),
        row(
            SoundEvent.GOVERNOR_CRANK_TURN, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(430f, 14, 0.50f, 0.9f),
            haptic = HapticSpec(listOf(0, 14, 10), listOf(0, 220, 95), 8),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "PROVISIONAL — the governor's crank turning against " +
                "its lead seal (Audit A §2.4); heavier than the drum's detent"
        ),
        row(
            SoundEvent.LEAD_SEAL_SNAP, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(760f, 18, 0.58f, 1.2f),
            haptic = HapticSpec(listOf(0, 16, 26, 10), listOf(0, 240, 0, 160), 8),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "PROVISIONAL — the lead seal snapping is an event " +
                "(CRITIC SO-5: event without its designed sound = a mute " +
                "mechanism); sharp, once, irreversible for the week"
        ),
        row(
            SoundEvent.CRANK_SPENT_REFUSAL, SoundFamily.PRIVATE_HAND,
            SoundDecision.FELT_ONLY, attended = true,
            // FEEL-SR-4's C-01 arbitration: the governor's refusal
            // carries its OWN figures, distinct from the drum's —
            // a longer, heavier stop for the bigger mechanism.
            haptic = HapticSpec(listOf(0, 34, 24), listOf(0, 230, 110), 34),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "CRITIC W-9: the spent crank says so by not turning — " +
                "no voice; its figures are its own (FEEL-SR-4 C-01), " +
                "heavier and longer than the drum's HARD_STOP"
        ),
        row(
            SoundEvent.CRANK_REDRIVE, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(205f, 130, 0.56f, 1.2f),
            haptic = HapticSpec(listOf(0, 24, 38, 16), listOf(0, 225, 0, 160), 8),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "Coordinator Ruling R5-2 (2026-11-02): the governor's " +
                "minute ends by re-driving the SAME three bolts and seating " +
                "the door (FEEL-SR-2's 3 rising bolts + door seat governs " +
                "the ceremony's shape); one representative registry voice " +
                "for the re-drive family, PROVISIONAL until the Stage 13 " +
                "spec set lands"
        ),

        // -- The guard, the stretch, the drawer (SOUND seat,
        //    voice-debt clearance, 2026-11-03) ---------------------
        row(
            SoundEvent.TIGHTEN_TICK_1, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(700f, 26, 0.42f, 1.0f),
            haptic = HapticSpec(listOf(0, 16), listOf(0, 140), 16),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): FEEL-SR-3 C3.1's DOUBLE " +
                "click, first click — its 26 ms body carries the click " +
                "plus the felt row's 10 ms gap, so the pair dispatched " +
                "back-to-back reads as one pawl act; loosest-step gain " +
                "(the climb with tightness is the felt row's, caller's)"
        ),
        row(
            SoundEvent.TIGHTEN_TICK_2, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(610f, 12, 0.34f, 1.0f),
            haptic = HapticSpec(listOf(0, 10), listOf(0, 100), 10),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): the double click's second " +
                "click — lower and softer, the pawl falling home"
        ),
        row(
            SoundEvent.TIGHTEN_SEAT_CLICK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(840f, 14, 0.58f, 1.2f),
            haptic = HapticSpec(listOf(0, 35), listOf(0, 210), 35),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): FEEL-SR-3 C3.2's " +
                "CLICK-LOCK — one hard snap on finger-lift, no tail, " +
                "no settle; the brightest guard voice, spent once " +
                "per tightening"
        ),
        row(
            SoundEvent.RAISE_ROCK_1, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(165f, 40, 0.34f, 0.9f),
            haptic = HapticSpec(listOf(0, 40), listOf(0, 85), 40),
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "SOUND seat (2026-11-03): FEEL-SR-3 C4.2's Unseated " +
                "Rock, first pulse — the disc rocks at the dawn the " +
                "raise dies. The filing's grit is long-form noise the " +
                "transient model cannot carry (cf. DOOR_SWING_CLOSE) — " +
                "three dying knocks stand for it, named, not faked"
        ),
        row(
            SoundEvent.RAISE_ROCK_2, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(150f, 34, 0.26f, 0.9f),
            haptic = HapticSpec(listOf(0, 30), listOf(0, 55), 30),
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "SOUND seat (2026-11-03): the rock's second pulse, dying"
        ),
        row(
            SoundEvent.RAISE_ROCK_3, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(138f, 28, 0.18f, 0.9f),
            haptic = HapticSpec(listOf(0, 18), listOf(0, 30), 18),
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "SOUND seat (2026-11-03): the rock's last pulse — " +
                "and nothing after it; a terminal accent would be a " +
                "lie (FEEL-SR-3 C4.2: NO terminal event)"
        ),
        row(
            SoundEvent.CARVE_SCRAPE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(240f, 136, 0.30f, 5.40f),
            haptic = HapticSpec(
                listOf(
                    0L, 100L, 100L, 100L, 100L, 100L, 100L,
                    100L, 100L, 100L, 100L, 100L, 100L
                ),
                listOf(0, 60, 65, 69, 74, 78, 83, 87, 92, 96, 101, 105, 110),
                40
            ),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): FEEL-SR-3 C6.2's scrape — " +
                "the filed 1.2 s rising texture is long-form noise the " +
                "transient voice model cannot carry (cf. DOOR_SWING_CLOSE); " +
                "this voice is the scrape's opening at the synth's 136 ms " +
                "ceiling, chord 5.40 (the steel set's harsh partial) for " +
                "grit, gain low under the hold — the full render is " +
                "MechanismSounds work, named, not faked"
        ),
        row(
            SoundEvent.CARVE_BLOW, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(175f, 80, 0.62f, 2.76f),
            haptic = HapticSpec(listOf(0, 55), listOf(0, 240), 55),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND seat (2026-11-03): FEEL-SR-3 C6.2's canonical " +
                "cut, the blow — its 80 ms body carries blow + hush " +
                "before the chip lands"
        ),
        row(
            SoundEvent.CARVE_CHIP, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(1350f, 14, 0.40f, 1.5f),
            haptic = HapticSpec(listOf(0, 30), listOf(0, 120), 30),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND seat (2026-11-03): the cut's chip — bright, " +
                "brief, spent exactly once in a stretch's life"
        ),
        row(
            SoundEvent.DRAWER_SLIDE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            voice = VoiceSpec(132f, 136, 0.30f, 0.9f),
            haptic = HapticSpec(listOf(0, 34, 20), listOf(0, 110, 70), 10),
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "SOUND seat (2026-11-03): DESIGNER-SR-5 §10's drawer " +
                "slide — low, soft, unhurried, at the synth's ceiling; " +
                "the wood's friction is long-form texture the transient " +
                "model cannot carry (cf. DOOR_SWING_CLOSE) — this voice " +
                "is the slide's low body, the full render named, not faked"
        ),

        // -- The break ----------------------------------------------
        row(
            SoundEvent.BREAK_FIRING, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.NAMED_SILENCE, attended = false,
            source = "Ledger B-12: the break is a firing of the time-lock " +
                "set at signing, invocable by no one — it begins in " +
                "stillness; no voice announces what nobody summoned"
        ),
        row(
            SoundEvent.BREAK_END_RETURN, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(430f, 80, 0.60f, 0.9f),
            haptic = HapticSpec(listOf(0, 16, 70, 16), listOf(0, 235, 0, 205), 4),
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "PROVISIONAL — the break-end voice's class (engine's " +
                "playBreakEndCue figures): a low double strike, its own " +
                "voice, machinery-fired so DND + ringer bind it"
        ),

        // -- The lifting & the plate --------------------------------
        // The lifting's mirror set (SOUND-SR-1, applied by
        // BUILD-SR-19): pitch ASCENDS 121 → 158 → 207 Hz, body
        // SHORTENS, brightness rises, loudness FALLS 0.50 → 0.42 —
        // performed, never a reversed clunk. These figures
        // supersede BUILD-SR-4's StrongroomLift bolt figures
        // (196/220/247 Hz, gains ascending) IN THIS REGISTRY;
        // StrongroomLift.kt itself is P6's file and still carries
        // its own figures — the divergence is named in
        // work/build-sr-19/record.md, not smoothed.
        row(
            SoundEvent.LIFT_BOLT_1, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(121f, 56, 0.50f, 2.76f),
            haptic = HapticSpec(listOf(0, 14), listOf(0, 150), 14),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 WITHDRAWAL_1: f0 121 Hz · body τ 30 ms · " +
                "peak 0.50 · burst lowpass 1,900 Hz; duration = measured " +
                "−20 dB body 56 ms; haptic = SOUND-SR-1's filed single pulse"
        ),
        row(
            SoundEvent.LIFT_BOLT_2, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(158f, 49, 0.46f, 2.76f),
            haptic = HapticSpec(listOf(0, 12), listOf(0, 135), 12),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 WITHDRAWAL_2: f0 158 Hz · body τ 25 ms · " +
                "peak 0.46 · burst lowpass 2,300 Hz"
        ),
        row(
            SoundEvent.LIFT_BOLT_3, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(207f, 37, 0.42f, 2.76f),
            haptic = HapticSpec(listOf(0, 10), listOf(0, 120), 10),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND-SR-1 WITHDRAWAL_3: f0 207 Hz · body τ 20 ms · " +
                "peak 0.42 · burst lowpass 2,800 Hz"
        ),
        row(
            SoundEvent.DOOR_OPEN_WEIGHT, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(132f, 136, 0.40f, 0.9f),
            haptic = HapticSpec(listOf(0, 90, 60), listOf(0, 120, 56), 90),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "BUILD-SR-4's StrongroomLift spec: the door's own " +
                "weight, 132 Hz / 0.40, long low settle; its 150 ms body " +
                "is capped at the synth's 136 ms ceiling (see SEAL_CLUNK_3). " +
                "SOUND-SR-1's swing_open (3.4 s decaying rumble, hinge " +
                "41 Hz) is a long-form render the transient voice model " +
                "cannot carry — named, not faked (BUILD-SR-19)"
        ),
        row(
            SoundEvent.PLATE_STRIKE, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(1180f, 120, 0.66f, 1.5f),
            haptic = HapticSpec(listOf(0, 18, 30, 12), listOf(0, 230, 0, 150), 8),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "PROVISIONAL — the plate strike is the brightest " +
                "sound in the app and is spent once a day (T46 Stage 13 " +
                "composition law); struck brass, high and brief"
        ),
        row(
            SoundEvent.PLATE_STRIKE_FAILURE, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = false,
            voice = VoiceSpec(690f, 88, 0.50f, 1.5f),
            haptic = HapticSpec(
                listOf(0, 24, 36, 50, 18), listOf(0, 190, 120, 0, 70), 24
            ),
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "SOUND seat (voice-debt clearance, 2026-11-03): " +
                "SOUND-SR-4's FAILURE-STRIKE given its voice — the " +
                "strike's brass (chord 1.5) taken lower (690 against " +
                "1180 Hz), damped (88 against 120 ms) and quieter " +
                "(0.50 against 0.66); the filed dead gap cut into the " +
                "resonance is a long-form detail the transient model " +
                "cannot cut — named, not faked"
        ),
        row(
            SoundEvent.AJAR_DAY, SoundFamily.ROOM_CEREMONY,
            SoundDecision.NAMED_SILENCE, attended = false,
            source = "BUILD-SR-4's StrongroomLift law: the ajar day has " +
                "NO voice — a ceremony sound over a failure would be " +
                "theatre; silence is that day's sound, by design"
        ),

        // -- Legacy voices ------------------------------------------
        row(
            SoundEvent.LEGACY_ROTARY_DETENT, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playRotaryDetent",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer, figures in TactileSoundEngine"
        ),
        row(
            SoundEvent.LEGACY_FIRM_CLICK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playFirmClick",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_SEAT_KNOCK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playSeatKnock",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer (parameterised at its " +
                "call sites; superseded for the sealed face by KNOCK)"
        ),
        row(
            SoundEvent.LEGACY_LATCH, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playMechanicalLatch",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer; the housekeeping voice " +
                "(reservation law: never the seal clunk)"
        ),
        row(
            SoundEvent.LEGACY_BREAK_END_CUE, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.SOUNDS, attended = false,
            legacyPerformer = "playBreakEndCue",
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "Legacy — engine performer (issue #150 voice)"
        ),
        row(
            SoundEvent.LEGACY_DOOR_ARRIVAL, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playDoorArrival",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_DOOR_ARM, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playDoorArm",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_DOOR_RETURN, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playDoorReturn",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_DOOR_PEEK, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playDoorPeek",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_DOOR_EXIT, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playDoorExit",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_BREAK_SUMMONS, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.SOUNDS, attended = false,
            legacyPerformer = "playBreakEndSummons",
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_WARNING_KNOCK, SoundFamily.MACHINERY_UNATTENDED,
            SoundDecision.SOUNDS, attended = false,
            legacyPerformer = "playBreakWarningKnock",
            latencyClass = LatencyClass.UNATTENDED_TIMELY,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.LEGACY_HARMONIC_SEAL, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playHarmonicSeal",
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "Legacy — engine performer; DISARM family only " +
                "(reservation law, issue #112)"
        ),
        row(
            SoundEvent.LEGACY_SEAL_THROW, SoundFamily.ROOM_CEREMONY,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playSealThrow",
            latencyClass = LatencyClass.CEREMONY_STEP,
            source = "Legacy — the Throw keeps its own dedicated lane " +
                "in the engine (B-5: one voice, bit-identical); this " +
                "row names it, the lane performs it"
        ),
        row(
            SoundEvent.LEGACY_TEAR_PULSE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playTearPulse",
            exemptFromNightGain = true,
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer; full at all hours " +
                "(Sarthak's Oct 30 ruling, #14)"
        ),
        row(
            SoundEvent.LEGACY_COMPLETION_PULSE, SoundFamily.PRIVATE_HAND,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "playCompletionPulse",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Legacy — engine performer"
        ),
        row(
            SoundEvent.PEEK_WARNING_TICK, SoundFamily.PRIVATE_HAND,
            SoundDecision.FELT_ONLY, attended = true,
            legacyPerformer = "playPeekWarningTick",
            latencyClass = LatencyClass.TOUCH_COINCIDENT,
            source = "Engine law (issue #112 finding 5): haptic ONLY — " +
                "a sound from an invisible app is a noise from nowhere"
        ),

        // -- The continuous voice ------------------------------------
        row(
            SoundEvent.SOUNDSCAPE, SoundFamily.AMBIENT,
            SoundDecision.SOUNDS, attended = true,
            legacyPerformer = "startSoundscape",
            latencyClass = LatencyClass.NONE,
            source = "#253 line 1: the one ruled ambient exception — " +
                "generated loop on its own lane, fact-gated, default OFF; " +
                "the felt-twin law's sole exception (a wash has no twin)"
        )
    )

    private val BY_EVENT: Map<SoundEvent, SoundEventSpec> =
        ALL.associateBy { it.event }

    /** The row for an event. Complete by construction: every enum
     *  entry is registered (held by SoundEventRegistryTest). */
    fun spec(event: SoundEvent): SoundEventSpec = BY_EVENT.getValue(event)

    /** The ceremony sequences, as (event, atMs) steps on their
     *  shared rhythm — the caller's motion schedules the beats;
     *  each step dispatches individually so an interrupted
     *  ceremony half-states nothing (CRITIC M-2). */
    // SOUND-SR-1's timelines (BUILD-SR-19): the seal's gaps
    // WIDEN (500 → 680 ms — relative onsets 0/500/1180 from
    // clunk 1); the lifting's gaps are EVEN (500 → 500 ms).
    // These are the acoustic score's onsets; P0's motion rhythm
    // (MonolithCurves.SealClunks 0/190/420) is the motion lane's
    // table and is not rewritten here — the divergence is named
    // in work/build-sr-19/record.md.
    val SEAL_SEQUENCE: List<Pair<SoundEvent, Int>> = listOf(
        SoundEvent.SEAL_CLUNK_1 to 0,
        SoundEvent.SEAL_CLUNK_2 to 500,
        SoundEvent.SEAL_CLUNK_3 to 1180
    )
    val LIFT_SEQUENCE: List<Pair<SoundEvent, Int>> = listOf(
        SoundEvent.LIFT_BOLT_1 to 0,
        SoundEvent.LIFT_BOLT_2 to 500,
        SoundEvent.LIFT_BOLT_3 to 1000
    )
}

/** The phone's sound facts, read at dispatch time. The Android
 *  reads live in [StrongroomSoundEngine]'s facts provider; this
 *  pure shape is what the gate law judges. */
data class SoundFacts(
    /** The persisted sound-effects fact (SoundEffectsSetting). */
    val soundEnabled: Boolean,
    /** The persisted haptics fact (SoundEffectsSetting). */
    val hapticsEnabled: Boolean,
    /** Ringer mode is SILENT or VIBRATE. */
    val ringerSilences: Boolean,
    /** An interruption filter other than ALL is in force. */
    val doNotDisturb: Boolean,
    /** Clock night (NightSignal's window). */
    val night: Boolean
)

/** The gate's verdict for one dispatch: the audio gain scale to
 *  apply to the row's voice (0 = no audio), whether the felt twin
 *  fires, and the law that decided it — named, so a silent event
 *  can always say why. */
data class SoundGateDecision(
    val audioGainScale: Float,
    val hapticFires: Boolean,
    val law: String
)

/**
 * THE SILENT-MODE / DND LAW, composed in one place (SOUND-SR-5).
 *
 * Three standing sources, one table:
 *  1. Issue #150 (the engine's two words): the ringer's word binds
 *     EVERY voice — SILENT/VIBRATE means no audio at all, the
 *     haptic twin still fires; DND binds unattended voices.
 *  2. NightSignal's gain law: DND silences ordinary voices and
 *     softens the seal/ceremony voice (0.35); clock night scales
 *     ordinary 0.40, ceremony 0.65.
 *  3. T46's degradation rule: sound degrades to its felt twin,
 *     never to nothing; no sound ever pierces silent mode or DND.
 *
 * The family curves ARE the volume law the brief names: a private
 * knock and a room ceremony take different scales from the same
 * facts because they are different kinds of event — private
 * voices are the man's own act in his hand (full by day, 0.40 at
 * night, gone under DND's ordinary-silence), ceremony voices are
 * the house's weight (0.65 at night, 0.35 under DND — softened,
 * never deleted, while attended), machinery voices nobody asked
 * to hear in this moment are bound hardest (gone under DND or
 * ringer, 0.40 at night). The tear's exemption stands over the
 * gain scales only; the hard gates bind it like everything else.
 *
 * This object decides; [StrongroomSoundEngine] applies the
 * decision exactly once (the registry path never also takes the
 * engine's internal night scaling — see playRegistryTransientExact).
 */
object SoundGateLaw {

    fun decide(spec: SoundEventSpec, facts: SoundFacts): SoundGateDecision {
        if (spec.decision == SoundDecision.NAMED_SILENCE) {
            return SoundGateDecision(0f, false, "named-silence")
        }
        // A felt event carries whenever the row HAS one (its own
        // waveform, or a legacy performer that fires its own) and
        // the haptics fact allows it. The soundscape is the named
        // exception: a continuous wash has no felt twin.
        val haptic = facts.hapticsEnabled &&
            spec.event != SoundEvent.SOUNDSCAPE &&
            (spec.haptic != null || spec.legacyPerformer != null)
        if (spec.decision == SoundDecision.FELT_ONLY) {
            return SoundGateDecision(0f, haptic, "felt-only")
        }
        // SOUNDS from here.
        if (!facts.soundEnabled) {
            return SoundGateDecision(0f, haptic, "sound-fact-off: felt twin carries it")
        }
        if (facts.ringerSilences) {
            return SoundGateDecision(0f, haptic, "ringer: felt twin carries it")
        }
        if (!spec.attended && facts.doNotDisturb) {
            return SoundGateDecision(0f, haptic, "dnd-unattended: felt twin carries it")
        }
        if (spec.exemptFromNightGain) {
            return SoundGateDecision(1f, haptic, "full-gain-exempt (tear ruling)")
        }
        val scale = when (spec.family) {
            SoundFamily.ROOM_CEREMONY -> when {
                facts.doNotDisturb -> 0.35f
                facts.night -> 0.65f
                else -> 1f
            }
            SoundFamily.PRIVATE_HAND, SoundFamily.MACHINERY_UNATTENDED -> when {
                facts.doNotDisturb -> 0f
                facts.night -> 0.40f
                else -> 1f
            }
            SoundFamily.AMBIENT -> when {
                facts.doNotDisturb -> 0f
                facts.night -> 0.40f
                else -> 1f
            }
        }
        val law = when {
            scale <= 0f -> "dnd: ordinary voice silenced, felt twin carries it"
            facts.doNotDisturb -> "dnd: ceremony softened 0.35"
            facts.night -> "night: family curve"
            else -> "day: full"
        }
        return SoundGateDecision(scale, haptic, law)
    }
}
