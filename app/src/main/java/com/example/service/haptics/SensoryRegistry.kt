package com.example.service.haptics

/**
 * THE REGISTRY — one row per felt event, binding the three
 * senses of that event together (FEEL-SR-5, for Slice 7).
 *
 * THE HARD RULE, STRUCTURAL: a surface never fires a haptic, a
 * sound, or a motion. It fires an [HapticEvent] through
 * [SensoryBus], and the bus reads THIS row — so the felt, heard
 * and seen halves of an event are one datum and cannot drift
 * apart, be spent separately, or be re-tuned in one sense while
 * the others keep an old shape. The substrate's failure this
 * replaces: ~17 engine methods each hand-pairing a waveform
 * with a transient, and call sites (overlays, components,
 * `SealHaptics`) firing platform haptics with no pairing at all.
 *
 * Waveform sources are cited per row. Legacy rows carry the
 * shipped engine's waveforms VERBATIM (TactileSoundEngine on
 * main) — the registry subsumes them, it does not re-tune them.
 * Strongroom rows carry the feel seats' filed tables verbatim
 * where they have landed: FEEL-SR-1's canonical door arrays,
 * FEEL-SR-3's control waveforms (04's consolidated table),
 * BUILD-SR-2's StrongroomKnock, and FEEL-SR-4's census as the
 * index that arbitrates collisions between them (its C-01 /
 * C-04 / C-05 positions are cited on the rows they govern,
 * proposals marked unruled where they are proposals).
 *
 * Sound numbers are the engine's call-site figures where they
 * exist; rows whose sound spec is still owed by its seat carry
 * a named cue with null figures — a named debt, never an
 * invented number. UNPROVEN-ON-DEVICE throughout (G-7).
 */
data class SoundCue(
    val name: String,
    val pitchHz: Float? = null,
    val durationMs: Int? = null,
    val gain: Float? = null,
    val chordRatio: Float? = null
) {
    val isSilence: Boolean get() = name == SILENCE_NAME

    companion object {
        const val SILENCE_NAME = "silence"

        /** Designed silence: no sound exists for this event,
         *  by ruling (SOUND-SR-4's silence map). */
        val SILENCE = SoundCue(SILENCE_NAME)
    }
}

/** The motion half of a row: a named cue into the motion
 *  grammar, or [NONE] where the event's law is stillness —
 *  the knock moves nothing; the face answers with the thud
 *  alone. */
data class MotionCue(val name: String?) {
    val isNone: Boolean get() = name == null

    companion object {
        val NONE = MotionCue(null)
    }
}

/** One event, three senses, one source citation. */
data class SensoryRow(
    val event: HapticEvent,
    val haptic: HapticWaveform,
    val sound: SoundCue,
    val motion: MotionCue,
    val source: String
)

object SensoryRegistry {

    private val rowsByEvent: Map<HapticEvent, SensoryRow> = buildRows()

    init {
        // The census and the registry are one fact, or the
        // registry does not exist: an event with no row is a
        // construction failure, never a runtime surprise.
        val missing = HapticEvent.entries.toSet() - rowsByEvent.keys
        require(missing.isEmpty()) {
            "SensoryRegistry lacks rows for: $missing — the census is the registry's index"
        }
        // Silent-by-law events carry silence in ALL three
        // senses or the row is a lie (H-4).
        rowsByEvent.values.forEach { row ->
            if (row.event.isSilentByLaw) {
                require(row.haptic.isSilent && row.sound.isSilence && row.motion.isNone) {
                    "${row.event} is silent by law but its row is not silent in every sense"
                }
            }
        }
    }

    fun row(event: HapticEvent): SensoryRow = rowsByEvent.getValue(event)

    fun all(): List<SensoryRow> = HapticEvent.entries.map { rowsByEvent.getValue(it) }

    val size: Int get() = rowsByEvent.size

    // ── Row construction ───────────────────────────────────────

    private fun buildRows(): Map<HapticEvent, SensoryRow> = listOf(

        // ── The door ──
        SensoryRow(
            HapticEvent.DOOR_SEAL,
            HapticWaveform(
                listOf(
                    0L,
                    65L, 65L, 65L, 65L, 65L, 65L, 65L, 65L, 65L,
                    85L,
                    16L, 30L, 14L,
                    130L,
                    20L, 38L, 16L,
                    140L,
                    26L, 52L, 22L, 30L
                ),
                listOf(
                    0,
                    55, 85, 120, 155, 185, 200, 185, 145, 90,
                    0,
                    235, 105, 45,
                    0,
                    250, 125, 55,
                    0,
                    255, 150, 70, 35
                ),
                fallbackMs = 90L, kind = WaveformKind.CEREMONY
            ),
            SoundCue("three-descending-clunks"),
            MotionCue("door-swing-and-bolts"),
            "Ledger A-3/B-7 · FEEL-SR-1's canonical DOOR-SEAL table, verbatim (1,204 ms; " +
                "strikes at 670/860/1074 ms; mass ascends 235→250→255 while pitch descends)"
        ),
        SensoryRow(
            HapticEvent.DOOR_LIFT,
            HapticWaveform(
                listOf(
                    0L,
                    22L, 22L, 12L, 18L,
                    120L,
                    18L, 18L, 10L, 14L,
                    120L,
                    14L, 14L, 8L, 12L,
                    100L,
                    65L, 65L, 65L, 65L, 65L, 65L, 65L, 65L, 65L, 90L
                ),
                listOf(
                    0,
                    80, 140, 230, 60,
                    0,
                    75, 125, 215, 50,
                    0,
                    65, 110, 195, 40,
                    0,
                    90, 145, 185, 200, 185, 155, 120, 85, 55, 30
                ),
                fallbackMs = 80L, kind = WaveformKind.CEREMONY
            ),
            SoundCue("lifting-bolts-and-door-weight"),
            MotionCue("bolts-withdraw-and-door-opens"),
            "Ledger A-13 · FEEL-SR-1's canonical DOOR-LIFT table, verbatim (1,197 ms; " +
                "release snaps at 44/230/402 ms, opening swing at 522 ms, no terminal impact)"
        ),
        SensoryRow(
            HapticEvent.SEAL_THROW,
            HapticWaveform(
                listOf(0L, 24L, 38L, 16L), listOf(0, 255, 0, 175),
                fallbackMs = 8L, kind = WaveformKind.CEREMONY
            ),
            SoundCue("seal-throw-render", pitchHz = 360f, durationMs = 1000, gain = 0.62f),
            MotionCue.NONE,
            "TactileSoundEngine SEAL_THROW_TIMINGS/AMPLITUDES + renderSealBuffer, verbatim"
        ),
        SensoryRow(
            HapticEvent.SEAL_CLUNK_DISARM,
            HapticWaveform(
                listOf(0L, 18L, 30L, 12L), listOf(0, 200, 90, 140), fallbackMs = 8L
            ),
            SoundCue("harmonic-seal-disarm", pitchHz = 480f, durationMs = 48, gain = 0.62f, chordRatio = 1.5f),
            MotionCue.NONE,
            "TactileSoundEngine.playHarmonicSeal(arming = false), verbatim; reservation law in its KDoc"
        ),

        // ── The signing ──
        SensoryRow(
            HapticEvent.ROTARY_DETENT,
            HapticPrimitives.tick(),
            SoundCue("rotary-detent", pitchHz = 680f, durationMs = 6, gain = 0.38f),
            MotionCue.NONE,
            "TactileSoundEngine.playRotaryDetent, verbatim at its defaults"
        ),
        SensoryRow(
            HapticEvent.COMBINATION_STATION,
            HapticWaveform(listOf(0L, 12L), listOf(0, 70), fallbackMs = 1L),
            SoundCue("spin-station-tick"),
            MotionCue.NONE,
            "FEEL-SR-3 C1.1 at pass 1 (ladder 70→95→120 by pass, the caller's count); " +
                "a wrong combination completed fires THIS and nothing else — no error event exists"
        ),
        SensoryRow(
            HapticEvent.COMBINATION_GATE,
            HapticWaveform(listOf(0L, 35L, 28L), listOf(0, 0, 165), fallbackMs = 28L),
            SoundCue("spin-gate-clack"),
            MotionCue.NONE,
            "FEEL-SR-3 C1.2: a 35 ms felt void, then one heavy tick — a gate is never a station"
        ),
        SensoryRow(
            HapticEvent.COMBINATION_FINAL_SEAT,
            HapticWaveform(
                listOf(
                    0L,
                    10L, 10L, 10L, 10L, 10L, 10L, 10L, 10L, 10L,
                    22L, 22L, 21L,
                    30L,
                    28L
                ),
                listOf(
                    0,
                    120, 132, 144, 156, 168, 180, 192, 204, 215,
                    235, 160, 90,
                    0,
                    75
                ),
                fallbackMs = 65L,
                composition = listOf(PrimitiveSegment(PrimitiveKind.THUD, scale = 1.0f))
            ),
            SoundCue("combination-seat"),
            MotionCue.NONE,
            "FEEL-SR-3 C1.3: approach ramp 120→215, the seat thud (65 ms decay rendered in " +
                "three steps — a waveform segment cannot ramp), 30 ms hush, one settle pulse"
        ),
        SensoryRow(
            HapticEvent.COMBINATION_END_STOP,
            HapticWaveform(listOf(0L, 45L), listOf(0, 195), fallbackMs = 45L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C1.3: the seated dial's end-stop — flat wall, no tick, felt only"
        ),
        SensoryRow(
            HapticEvent.HOUR_DETENT_QUARTER,
            HapticWaveform(listOf(0L, 8L), listOf(0, 55), fallbackMs = 1L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C2.1: the quarter stone — sparse, firm, felt only; between quarters, silence"
        ),
        SensoryRow(
            HapticEvent.HOUR_DETENT_WHOLE,
            HapticWaveform(listOf(0L, 14L), listOf(0, 85), fallbackMs = 1L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C2.1: the whole-hour counting tick, one size up from the quarter"
        ),
        silentRow(HapticEvent.HOUR_RELEASED, "FEEL-SR-3 C2.2: release away from a quarter places, never locks"),
        SensoryRow(
            HapticEvent.BREAK_GRAIN_MINUTE,
            HapticWaveform(listOf(0L, 6L), listOf(0, 30), fallbackMs = 1L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C5.1: the break setter's per-minute brush — grain, never stones"
        ),
        SensoryRow(
            HapticEvent.BREAK_GRAIN_FIVE,
            HapticWaveform(listOf(0L, 8L), listOf(0, 50), fallbackMs = 1L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C5.1: the grain at a 5-minute mark"
        ),
        SensoryRow(
            HapticEvent.BREAK_PRESET_NOTCH,
            HapticWaveform(listOf(0L, 18L), listOf(0, 75), fallbackMs = 18L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C5.2: the preset notch — a soft swell, haptic only, zero added resistance"
        ),
        SensoryRow(
            HapticEvent.BREAK_PRESET_REST,
            HapticWaveform(listOf(0L, 22L), listOf(0, 65), fallbackMs = 22L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C5.2: the resting pulse — a resting hand is answered, a travelling one is not detained"
        ),
        SensoryRow(
            HapticEvent.BREAK_ZERO_STOP,
            HapticWaveform(listOf(0L, 40L), listOf(0, 130), fallbackMs = 40L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C5.2: the zero end-stop — a flat floor, no ramp, no settle, no scrape before it"
        ),
        silentRow(HapticEvent.BREAK_RELEASED, "FEEL-SR-3 C5.2: a non-zero break release is priced at the spin, not here"),
        SensoryRow(
            HapticEvent.SIGNATURE_LATCH,
            HapticWaveform(
                listOf(0L, 13L, 20L, 8L), listOf(0, 225, 0, 135), fallbackMs = 8L
            ),
            SoundCue("firm-click", pitchHz = 620f, durationMs = 12, gain = 0.5f, chordRatio = 1.1f),
            MotionCue.NONE,
            "TactileSoundEngine.playFirmClick, verbatim"
        ),

        // ── The sealed face ──
        SensoryRow(
            HapticEvent.KNOCK,
            HapticWaveform(
                listOf(0L, 42L, 30L, 16L), listOf(0, 255, 96, 48),
                fallbackMs = 42L,
                composition = listOf(PrimitiveSegment(PrimitiveKind.THUD, scale = 1.0f))
            ),
            SoundCue("knock", pitchHz = 118f, durationMs = 110, gain = 0.52f, chordRatio = 0.9f),
            MotionCue.NONE,
            "Ledger A-5 · StrongroomKnock (BUILD-SR-2), verbatim incl. THUD primitive; " +
                "the face does not move — the thud is the whole reply"
        ),
        silentRow(HapticEvent.HELD_ARRIVAL, "Ledger D-5 · SOUND-SR-4 S-5/S-6 · CRITIC-SR-1 H-4"),
        silentRow(HapticEvent.DESK_CLOCK_FORMING, "Ledger E-3 · CRITIC-SR-1 H-4"),
        silentRow(
            HapticEvent.GAP_ARRIVAL,
            "Ruling C-2 · SOUND-SR-4 GAP-SILENCE · R4-1: in-span gaps (gym·sadhana·meals) " +
                "arrive silently inside the seal; meditation (pre-swing) and sleep (post-lift) " +
                "stand outside it entirely, where the app is absent and nothing could fire"
        ),
        silentRow(HapticEvent.HAND_BACK, "Ledger B-13 · SOUND-SR-4 HAND-BACK-SILENCE"),
        silentRow(HapticEvent.CARVED_STRIP_TOUCH, "FEEL-SR-3 position 4: after the carve, silence is the finality"),

        // ── The antechamber ──
        SensoryRow(
            HapticEvent.GRILLE_TOUCH,
            HapticPrimitives.seat(
                strikeAmplitude = 140, strikeMs = 9L, gapMs = 14L,
                seatAmplitude = 90, seatMs = 6L, fallbackMs = 4L
            ),
            SoundCue("grille-touch"),
            MotionCue("grille-door"),
            "SOUND-SR-4 S-7: one low mechanical touch-sound, matched to its motion"
        ),
        SensoryRow(
            HapticEvent.DRUM_DETENT,
            HapticPrimitives.tick(amplitude = 190, strikeMs = 11L, tailMs = 8L, tailFraction = 0.58f, fallbackMs = 8L),
            SoundCue("drum-detent"),
            MotionCue("drum-crank-turn"),
            "Ledger A-7 · SOUND-SR-2 drum set (detent-for-detent sync point)"
        ),
        SensoryRow(
            HapticEvent.DRUM_LAND,
            HapticPrimitives.seat(
                strikeAmplitude = 200, strikeMs = 14L, gapMs = 16L,
                seatAmplitude = 130, seatMs = 10L, fallbackMs = 8L
            ),
            SoundCue("drum-land"),
            MotionCue("drum-rolls-app-to-him"),
            "Ledger A-7: the crank turns one app to him and it LANDS"
        ),
        SensoryRow(
            HapticEvent.DRUM_RETURN,
            HapticPrimitives.tick(amplitude = 110, strikeMs = 12L, tailMs = 8L, tailFraction = 0.5f, fallbackMs = 6L),
            SoundCue("drum-return"),
            MotionCue("drum-app-recedes"),
            "SOUND-SR-4 S-10: one soft self-return, lower and shorter than the turn"
        ),
        SensoryRow(
            HapticEvent.DRUM_HARD_STOP,
            HapticPrimitives.resistanceStop(),
            SoundCue.SILENCE,
            MotionCue("crank-does-not-turn"),
            "Ledger A-7 · CRITIC-SR-1 H-3 · SOUND-SR-4 HARD-STOP-SILENCE: resistance speaks, sound does not"
        ),
        SensoryRow(
            HapticEvent.GOVERNOR_CRANK_TURN,
            HapticPrimitives.detentTrain(
                count = 3, startAmplitude = 170, endAmplitude = 210,
                strikeMs = 10L, gapMs = 22L, fallbackMs = 8L
            ),
            SoundCue("governor-crank-turn"),
            MotionCue("governor-crank-turn"),
            "Ledger A-8: the emergency crank's turn carries its cost in weight"
        ),
        SensoryRow(
            HapticEvent.GOVERNOR_SEAL_SNAP,
            HapticWaveform(
                listOf(0L, 6L, 26L, 12L), listOf(0, 255, 0, 150), fallbackMs = 6L
            ),
            SoundCue("lead-seal-snap"),
            MotionCue("lead-seal-snaps"),
            "Ledger A-8 · SOUND-SR-2: the snap is the one bright, irreversible event of the crank"
        ),
        SensoryRow(
            HapticEvent.GOVERNOR_HARD_STOP,
            HapticPrimitives.resistanceStop(
                peakAmplitude = 235, steps = 2, stepMs = 6L, brakeMs = 12L, fallbackMs = 24L
            ),
            SoundCue.SILENCE,
            MotionCue("crank-does-not-turn"),
            "Ledger A-8 · CRITIC-SR-1 H-3 · SOUND-SR-4 S-14 — differentiated from the drum's " +
                "stop per FEEL-SR-4's C-01 arbitration PROPOSAL (unruled): near-zero travel " +
                "against bolt-weight deadness — the hand meets the door itself, not a mechanism"
        ),
        SensoryRow(
            HapticEvent.GOVERNOR_BOLTS_REDRIVE,
            HapticPrimitives.descendingClunks(
                count = 3, firstAmplitude = 210, lastAmplitude = 230,
                firstBodyMs = 40L, lastBodyMs = 60L,
                firstGapMs = 900L, gapGrowthMs = 0L, fallbackMs = 60L
            ),
            SoundCue("governor-bolts-redrive"),
            MotionCue("bolts-redrive"),
            "FEEL-SR-4 F-13 + its C-05 PROPOSAL (unruled, provisional): after the crank the " +
                "seal is whole again — re-driven at swing spacing with drive-home weight"
        ),
        SensoryRow(
            HapticEvent.CODE_CRANK_ARM,
            HapticWaveform(
                listOf(0L, 20L, 20L, 20L, 20L), listOf(0, 90, 130, 170, 205),
                fallbackMs = 20L
            ),
            SoundCue.SILENCE,
            MotionCue("code-crank-arming"),
            "Ledger B-18 · FEEL-SR-4 F-10 (UX-SR-3 §4.3 Stage A): rising resistance, no click — felt only"
        ),
        SensoryRow(
            HapticEvent.CODE_DETENT_SPEND,
            HapticWaveform(listOf(0L, 12L), listOf(0, 220), fallbackMs = 12L),
            SoundCue("code-detent-spend"),
            MotionCue.NONE,
            "FEEL-SR-4 F-11: one hard detent click, felt and heard — the purse spends at this instant"
        ),
        SensoryRow(
            HapticEvent.CODE_HARD_STOP,
            HapticPrimitives.resistanceStop(
                peakAmplitude = 205, steps = 2, stepMs = 8L, brakeMs = 10L, fallbackMs = 24L
            ),
            SoundCue.SILENCE,
            MotionCue("code-crank-does-not-turn"),
            "FEEL-SR-4 F-17: zero travel from the first pressure — the shortest stop in the registry"
        ),

        // ── The Guard ──
        SensoryRow(
            HapticEvent.GUARD_TIGHTEN_DETENT,
            HapticWaveform(
                listOf(0L, 16L, 10L, 10L), listOf(0, 140, 0, 100), fallbackMs = 10L
            ),
            SoundCue("tightening-ratchet-tick"),
            MotionCue.NONE,
            "FEEL-SR-3 C3.1: the pawl's DOUBLE click, at the loosest step — amplitude climbs " +
                "140/100 → 175/130 as the figure tightens (the caller's step); supersedes " +
                "DESIGNER-SR-5 §10's single-click target in feel, by FEEL-SR-3's named delta"
        ),
        SensoryRow(
            HapticEvent.GUARD_TIGHTEN_SEAT,
            HapticWaveform(listOf(0L, 35L), listOf(0, 210), fallbackMs = 35L),
            SoundCue("tightening-seat-click"),
            MotionCue.NONE,
            "FEEL-SR-3 C3.2: the CLICK-LOCK on finger-lift — one hard snap, no tail, no settle (D-1)"
        ),
        SensoryRow(
            HapticEvent.GUARD_RAISE_DETENT,
            HapticWaveform(
                listOf(0L, 30L, 22L), listOf(0, 45, 60), fallbackMs = 22L
            ),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C4.1: the hollow detent — a 30 ms ramp to a soft 60 peak, arriving " +
                "~50 ms late by design; lateness is the deferral, felt only"
        ),
        SensoryRow(
            HapticEvent.GUARD_RAISE_ROCK,
            HapticWaveform(
                listOf(0L, 40L, 70L, 30L, 90L, 18L), listOf(0, 85, 0, 55, 0, 30),
                fallbackMs = 40L
            ),
            SoundCue("dawn-seat-filing"),
            MotionCue("dawn-seat-disc-rests-unseated"),
            "FEEL-SR-3 C4.2: the Unseated Rock — three dying pulses, NO terminal event; " +
                "DESIGNER-SR-5's disc, in feel"
        ),
        SensoryRow(
            HapticEvent.GUARD_RAISE_CANCEL,
            HapticWaveform(listOf(0L, 25L), listOf(0, 60), fallbackMs = 25L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C4.2: lifting the disc out — one soft release, un-filing a paper"
        ),
        silentRow(
            HapticEvent.DAY_START_BOUNDARY,
            "R-C1 · FEEL-SR-4 N-05: 03:30 is a state boundary, not an alarm — nothing fires"
        ),
        SensoryRow(
            HapticEvent.STRETCH_DAY_STEP,
            HapticWaveform(listOf(0L, 22L), listOf(0, 170), fallbackMs = 22L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "FEEL-SR-3 C6.1 at mid-distance (ladder 150→195 with distance is the caller's " +
                "step count) — the heaviest recurring detent, felt only"
        ),
        SensoryRow(
            HapticEvent.STRETCH_SCRAPE,
            HapticWaveform(
                listOf(
                    0L,
                    100L, 100L, 100L, 100L, 100L, 100L,
                    100L, 100L, 100L, 100L, 100L, 100L
                ),
                listOf(0, 60, 65, 69, 74, 78, 83, 87, 92, 96, 101, 105, 110),
                fallbackMs = 40L
            ),
            SoundCue("carve-scrape"),
            MotionCue("carve-scrape"),
            "FEEL-SR-3 C6.2: the scrape — 1.2 s of texture ramping 60→110 under the hold, " +
                "rendered in 100 ms steps; a released hold fires nothing else, ever"
        ),
        SensoryRow(
            HapticEvent.STRETCH_CARVE,
            HapticWaveform(
                listOf(0L, 55L, 30L, 30L), listOf(0, 240, 0, 120), fallbackMs = 55L
            ),
            SoundCue("carve-cut"),
            MotionCue("carved-end"),
            "FEEL-SR-3 C6.2's canonical cut: blow, hush, chip — fires exactly once in a stretch's life"
        ),

        // ── The break ──
        SensoryRow(
            HapticEvent.BREAK_ARRIVAL,
            HapticWaveform(
                listOf(0L, 300L, 300L, 300L, 300L, 300L, 300L, 200L),
                listOf(0, 90, 120, 140, 130, 100, 70, 45),
                fallbackMs = 60L
            ),
            SoundCue("break-clockwork-ease"),
            MotionCue.NONE,
            "Ledger B-12 · FEEL-SR-4 F-14 + its C-04 arbitration: the easing is a ~2 s DRAWN " +
                "event — in pocket form the same duration at muted weight; duration, never " +
                "amplitude alone, is the wall between this and the knock"
        ),
        SensoryRow(
            HapticEvent.BOLTS_REDRIVE,
            HapticWaveform(
                listOf(0L, 20L, 40L, 2440L, 18L, 36L, 2446L, 16L, 34L, 24L),
                listOf(0, 200, 120, 0, 180, 100, 0, 155, 85, 55),
                fallbackMs = 60L
            ),
            SoundCue("bolt-redrive-seating"),
            MotionCue("bolts-redrive"),
            "FEEL-SR-4 F-15 (UX-SR-4 R-3): three seatings ~2.5 s apart as ONE atomic row, " +
                "each a step below the swing's clunks, the final settle the SOFTEST — the " +
                "mirror of the seal, where the last bolt is the heaviest"
        ),
        SensoryRow(
            HapticEvent.BREAK_END_CUE,
            HapticWaveform(
                listOf(0L, 16L, 70L, 16L), listOf(0, 235, 0, 205), fallbackMs = 4L
            ),
            SoundCue("break-end-cue", pitchHz = 430f, durationMs = 80, gain = 0.60f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playBreakEndCue, verbatim (its transient fires twice by its own spec)"
        ),
        SensoryRow(
            HapticEvent.BREAK_END_SUMMONS,
            HapticWaveform(
                listOf(0L, 32L, 80L, 32L, 80L, 32L), listOf(0, 255, 0, 255, 0, 230),
                fallbackMs = 8L
            ),
            SoundCue("break-end-summons", pitchHz = 320f, durationMs = 64, gain = 0.7f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playBreakEndSummons, verbatim"
        ),
        SensoryRow(
            HapticEvent.BREAK_WARNING,
            HapticWaveform(
                listOf(0L, 14L, 44L, 14L), listOf(0, 200, 0, 170), fallbackMs = 4L
            ),
            SoundCue("break-warning-knock", pitchHz = 520f, durationMs = 16, gain = 0.4f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playBreakWarningKnock, verbatim"
        ),

        // ── The plate & the drawer ──
        SensoryRow(
            HapticEvent.PLATE_STRIKE,
            HapticPrimitives.thud(
                strikeAmplitude = 240, strikeMs = 28L,
                settleAmplitude = 110, settleMs = 60L,
                seatAmplitude = 60, seatMs = 20L, fallbackMs = 28L
            ),
            SoundCue("plate-strike"),
            MotionCue("plate-is-struck"),
            "Ledger A-10 · DESIGNER-SR-4 §3: one strike at the lifting, the plate's whole telling"
        ),
        SensoryRow(
            HapticEvent.PLATE_STRIKE_FAILURE,
            HapticWaveform(
                listOf(0L, 24L, 36L, 50L, 18L), listOf(0, 190, 120, 0, 70),
                fallbackMs = 24L
            ),
            SoundCue("plate-strike-failure"),
            MotionCue("plate-is-struck"),
            "Ledger A-12 · SOUND-SR-4 FAILURE-STRIKE: lower, damped, a dead gap cut into the resonance"
        ),
        SensoryRow(
            HapticEvent.DRAWER_OPEN,
            HapticWaveform(
                listOf(0L, 34L, 20L), listOf(0, 110, 70), fallbackMs = 10L
            ),
            SoundCue("drawer-slide"),
            MotionCue("drawer-opens"),
            "DESIGNER-SR-5 §10: the drawer's slide — low, soft, unhurried"
        ),
        SensoryRow(
            HapticEvent.TASK_STORED,
            HapticPrimitives.tick(amplitude = 120, strikeMs = 9L, tailMs = 6L, tailFraction = 0.5f, fallbackMs = 4L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "Ledger D-8: a task is STORED, not celebrated — a felt seating, no sound (E-5)"
        ),

        // ── Legacy engine voices, verbatim ──
        SensoryRow(
            HapticEvent.LATCH_ENGAGE,
            HapticWaveform(listOf(0L, 11L, 26L, 9L), listOf(0, 215, 0, 150), fallbackMs = 4L),
            SoundCue("latch-engage", pitchHz = 760f, durationMs = 16, gain = 0.54f, chordRatio = 1.2f),
            MotionCue.NONE,
            "TactileSoundEngine.playMechanicalLatch(engaged = true), verbatim"
        ),
        SensoryRow(
            HapticEvent.LATCH_RELEASE,
            HapticWaveform(listOf(0L, 9L, 22L, 10L), listOf(0, 165, 0, 195), fallbackMs = 4L),
            SoundCue("latch-release", pitchHz = 520f, durationMs = 16, gain = 0.54f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playMechanicalLatch(engaged = false), verbatim"
        ),
        SensoryRow(
            HapticEvent.SEAT_KNOCK,
            HapticWaveform(listOf(0L, 16L, 8L), listOf(0, 178, 62), fallbackMs = 16L),
            SoundCue("seat-knock", pitchHz = 300f, durationMs = 22, gain = 0.43f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playSeatKnock at its mid-weight form (16 ms @ 0.7); " +
                "the engine's parameterised spends stay parameterised until re-pointed"
        ),
        SensoryRow(
            HapticEvent.DOOR_ARRIVAL,
            HapticWaveform(listOf(0L, 11L, 26L, 9L), listOf(0, 215, 0, 150), fallbackMs = 4L),
            SoundCue("door-arrival", pitchHz = 300f, durationMs = 44, gain = 0.60f, chordRatio = 1.5f),
            MotionCue.NONE,
            "TactileSoundEngine.playDoorArrival, verbatim"
        ),
        SensoryRow(
            HapticEvent.DOOR_ARM,
            HapticWaveform(listOf(0L, 7L, 5L), listOf(0, 150, 60), fallbackMs = 1L),
            SoundCue("door-arm", pitchHz = 300f, durationMs = 44, gain = 0.30f, chordRatio = 1.5f),
            MotionCue.NONE,
            "TactileSoundEngine.playDoorArm, verbatim"
        ),
        SensoryRow(
            HapticEvent.DOOR_RETURN,
            HapticWaveform(listOf(0L, 9L, 22L, 10L), listOf(0, 165, 0, 195), fallbackMs = 4L),
            SoundCue("door-return", pitchHz = 300f, durationMs = 28, gain = 0.48f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playDoorReturn, verbatim"
        ),
        SensoryRow(
            HapticEvent.DOOR_PEEK,
            HapticWaveform(listOf(0L, 11L, 26L, 9L), listOf(0, 215, 0, 150), fallbackMs = 4L),
            SoundCue("door-peek", pitchHz = 300f, durationMs = 32, gain = 0.50f, chordRatio = 1.5f),
            MotionCue.NONE,
            "TactileSoundEngine.playDoorPeek, verbatim"
        ),
        SensoryRow(
            HapticEvent.DOOR_EXIT,
            HapticWaveform(listOf(0L, 9L, 22L, 10L), listOf(0, 165, 0, 195), fallbackMs = 4L),
            SoundCue("door-exit", pitchHz = 240f, durationMs = 40, gain = 0.42f, chordRatio = 0.9f),
            MotionCue.NONE,
            "TactileSoundEngine.playDoorExit, verbatim"
        ),
        SensoryRow(
            HapticEvent.PEEK_WARNING_TICK,
            HapticWaveform(listOf(0L, 7L, 5L), listOf(0, 180, 70), fallbackMs = 1L),
            SoundCue.SILENCE,
            MotionCue.NONE,
            "TactileSoundEngine.playPeekWarningTick, verbatim — HAPTIC ONLY by its own design"
        ),
        SensoryRow(
            HapticEvent.TEAR_PULSE,
            HapticWaveform(listOf(0L, 8L, 14L, 8L), listOf(0, 160, 0, 110), fallbackMs = 1L),
            SoundCue("tear-pulse", pitchHz = 240f, durationMs = 40, gain = 0.68f, chordRatio = 1.21f),
            MotionCue.NONE,
            "TactileSoundEngine.playTearPulse, verbatim"
        ),
        SensoryRow(
            HapticEvent.COMPLETION_PULSE,
            HapticWaveform(
                listOf(0L, 10L, 24L, 10L, 24L, 14L), listOf(0, 175, 0, 125, 0, 80),
                fallbackMs = 10L
            ),
            SoundCue("completion-pulse", pitchHz = 185f, durationMs = 64, gain = 0.68f, chordRatio = 1.33f),
            MotionCue.NONE,
            "TactileSoundEngine.playCompletionPulse, verbatim"
        )
    ).associateBy { it.event }

    private fun silentRow(event: HapticEvent, source: String) = SensoryRow(
        event, HapticWaveform.SILENCE, SoundCue.SILENCE, MotionCue.NONE, source
    )
}
