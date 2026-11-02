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
 * Strongroom rows carry the feel seats' filed shapes
 * (FEEL-SR-1/-3 headline numbers, BUILD-SR-2's StrongroomKnock
 * verbatim); where a seat's full spec has not landed, the row
 * says so and carries the opening line's numbers as starting
 * truth. FEEL-SR-4's census extends this table as data.
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
            HapticEvent.DOOR_SEAL, doorSealWaveform(),
            SoundCue("three-descending-clunks"),
            MotionCue("door-swing-and-bolts"),
            "Ledger A-3/B-7 · FEEL-SR-1 DOOR-SEAL (1,204 ms: swing swell + latch silence + " +
                "mass-ascending clunks; full table pending its spec file)"
        ),
        SensoryRow(
            HapticEvent.DOOR_LIFT, doorLiftWaveform(),
            SoundCue("lifting-bolts-and-door-weight"),
            MotionCue("bolts-withdraw-and-door-opens"),
            "Ledger A-13 · FEEL-SR-1 DOOR-LIFT (1,197 ms: withdrawals heaviest-first, " +
                "unlatched stillness, opening swing, no terminal impact)"
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
            HapticEvent.COMBINATION_FINAL_SEAT,
            HapticPrimitives.seat(
                strikeAmplitude = 240, strikeMs = 13L, gapMs = 30L,
                seatAmplitude = 175, seatMs = 12L, fallbackMs = 8L
            ),
            SoundCue("combination-final-seat"),
            MotionCue.NONE,
            "FEEL-SR-3: the spin's final detent is a SEAT (finality shape 1 of 3)"
        ),
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
        silentRow(HapticEvent.GAP_ARRIVAL, "Ruling C-2 · SOUND-SR-4 GAP-SILENCE"),
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
            HapticPrimitives.resistanceStop(peakAmplitude = 225, stepMs = 30L),
            SoundCue.SILENCE,
            MotionCue("crank-does-not-turn"),
            "Ledger A-8 · CRITIC-SR-1 H-3 · SOUND-SR-4 S-14: a spent week refuses identically, in silence"
        ),

        // ── The Guard ──
        SensoryRow(
            HapticEvent.GUARD_TIGHTEN_DETENT,
            HapticPrimitives.tick(amplitude = 170, strikeMs = 6L, tailMs = 5L, tailFraction = 0.47f, fallbackMs = 1L),
            SoundCue("tightening-ratchet-tick"),
            MotionCue.NONE,
            "DESIGNER-SR-5 §10: low brass tick per tightening detent"
        ),
        SensoryRow(
            HapticEvent.GUARD_TIGHTEN_SEAT,
            HapticPrimitives.seat(
                strikeAmplitude = 225, strikeMs = 10L, gapMs = 18L,
                seatAmplitude = 170, seatMs = 8L, fallbackMs = 4L
            ),
            SoundCue("tightening-seat-click"),
            MotionCue.NONE,
            "FEEL-SR-3: tightening's finality is a CLICK-LOCK — pawl clicks, one hard seat, instant (D-1)"
        ),
        SensoryRow(
            HapticEvent.GUARD_RAISE_FILED,
            HapticPrimitives.hollowSeat(),
            SoundCue("dawn-seat-filing"),
            MotionCue("dawn-seat-disc-rests-unseated"),
            "Ledger D-1 · FEEL-SR-3's HOLLOW SEAT · DESIGNER-SR-5 §10 (soft roll, one muted knock)"
        ),
        SensoryRow(
            HapticEvent.STRETCH_CARVE,
            HapticPrimitives.carve(),
            SoundCue("carve"),
            MotionCue("carved-end"),
            "Ledger D-3 · FEEL-SR-3: the carve — scrape, one cut, then permanent silence"
        ),

        // ── The break ──
        SensoryRow(
            HapticEvent.BREAK_ARRIVAL,
            HapticWaveform(
                listOf(0L, 18L, 40L, 14L), listOf(0, 150, 0, 100), fallbackMs = 8L
            ),
            SoundCue("break-clockwork-ease"),
            MotionCue.NONE,
            "Ledger B-12 · SOUND-SR-4 S-15: the clockwork eases — felt more than heard, no approach shape"
        ),
        SensoryRow(
            HapticEvent.BOLTS_REDRIVE,
            HapticPrimitives.seat(
                strikeAmplitude = 195, strikeMs = 12L, gapMs = 20L,
                seatAmplitude = 140, seatMs = 10L, fallbackMs = 8L
            ),
            SoundCue("bolt-redrive-seating"),
            MotionCue("bolt-redrives"),
            "SOUND-SR-4 S-13: ONE seating per firing — the bus fires this row once per bolt, " +
                "three times, spacing is the caller's clock, a step below the swing's clunks"
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

    /** FEEL-SR-1's DOOR-SEAL, composed: a 585 ms stepped swing
     *  swell (movement, peak BELOW strike amplitude), 85 ms of
     *  latch silence, then the mass-ascending clunks — 1,204 ms
     *  in total, as its opening line files it. */
    private fun doorSealWaveform(): HapticWaveform {
        val swingTimings = listOf(0L, 117L, 117L, 117L, 117L, 117L, 85L)
        val swingAmplitudes = listOf(0, 60, 88, 110, 132, 150, 0)
        val clunks = HapticPrimitives.descendingClunks(
            count = 3, firstAmplitude = 235, lastAmplitude = 255,
            firstBodyMs = 46L, lastBodyMs = 78L,
            firstGapMs = 160L, gapGrowthMs = 28L, fallbackMs = 90L
        )
        return HapticWaveform(
            timingsMs = swingTimings + clunks.timingsMs.drop(1),
            amplitudes = swingAmplitudes + clunks.amplitudes.drop(1),
            fallbackMs = 90L,
            kind = WaveformKind.CEREMONY
        )
    }

    /** FEEL-SR-1's DOOR-LIFT, composed: the withdrawals
     *  (heaviest first), 100 ms of unlatched stillness, then the
     *  opening swing as the seal swing's envelope mirrored —
     *  ending on the swell's lowest step, in NO terminal impact.
     *  1,197 ms in total. */
    private fun doorLiftWaveform(): HapticWaveform {
        val withdrawals = HapticPrimitives.withdrawals(
            count = 3, firstPeakAmplitude = 235, lastPeakAmplitude = 195,
            fallbackMs = 80L
        )
        val stillnessTimings = listOf(100L)
        val stillnessAmplitudes = listOf(0)
        val openingTimings = listOf(113L, 113L, 113L, 113L, 113L)
        val openingAmplitudes = listOf(150, 128, 105, 83, 60)
        return HapticWaveform(
            timingsMs = withdrawals.timingsMs + stillnessTimings + openingTimings,
            amplitudes = withdrawals.amplitudes + stillnessAmplitudes + openingAmplitudes,
            fallbackMs = 80L,
            kind = WaveformKind.CEREMONY
        )
    }
}
