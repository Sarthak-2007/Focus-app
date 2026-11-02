package com.example.ui.maximum

import com.example.ui.motion.DetentRail
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.MonolithBodies

/**
 * MAXIMUM PRESETS — the census set as data (BUILD-SR-11, T24).
 *
 * Stage 11's per-element pass is ASSEMBLY: each significant
 * element's maximum treatment is chosen from the toolkit by
 * naming its preset here, never by inventing machinery at the
 * surface. Every number below traces to a filing read firsthand:
 * the bodies are MOTION-SR-5's seeded `MonolithBodies` (P0's
 * landed character); the detent amplitudes, latencies and laws
 * are FEEL-SR-3's `01–04` by section, cited per preset.
 *
 * ORDER (the assembly order Stage 11 works in): the brief's head
 * — the combination spin — then plan §2's maximum law (wheel/
 * bolts · knock · drum crank · governor's crank · door weight),
 * then FEEL-SR-3's remaining setters (hour · allowance · break ·
 * stretch). LEAD-STAGE-11's census had not filed at this act; a
 * re-ordering changes this list's order, a new element adds a
 * row — both are data changes, named in the lane's return.
 */

/** One census element's assembly entry: its name, its body in words, its preset home. */
data class CensusPreset(val name: String, val home: String)

object MaximumPresets {

    /** The assembly order, by name — the list Stage 11 works down. */
    val CensusOrder: List<CensusPreset> = listOf(
        CensusPreset("combination-spin", "CombinationSpin + SpinTrain"),
        CensusPreset("wheel-bolts", "MonolithBodies.Bolt via Choreography scores (MOTION-SR-5)"),
        CensusPreset("knock", "MonolithBodies.Knock (critically damped, never overshoots)"),
        CensusPreset("drum-crank", "DrumDrag"),
        CensusPreset("governors-crank", "MonolithBodies.Crank + HardStop (spent purse)"),
        CensusPreset("door-weight", "MonolithBodies.DoorWeight via Choreography scores (MOTION-SR-5)"),
        CensusPreset("lifting-hour", "HourTrain + HourDrag"),
        CensusPreset("allowance", "TightenTrain / HollowTrain"),
        CensusPreset("break", "BreakTrain + BreakDrag"),
        CensusPreset("stretch", "StretchSteps + StretchDrag (no inertia)")
    )

    // ---------- THE SPIN (FEEL-SR-3 C1) ----------

    /** Station spacing on the spin's dial: ten numbers around the circle. */
    const val SPIN_STATION_SPACING_DEG: Float = 36f

    /** The spin's governed velocity ceiling — a dial that cannot be flicked through a number. */
    const val SPIN_MAX_VELOCITY_DEG_PER_SEC: Float = 240f

    /** Released mid-orbit, the spin settles within ~30° (FEEL-SR-3 C1, UX-SR-1 §5). */
    const val SPIN_MAX_COAST_DEG: Float = 30f

    /** Station amplitudes by pass: the thickening made felt (C1.1: 70 → 95 → 120). */
    fun spinStationAmplitude(pass: Int): Int = when {
        pass <= 1 -> 70
        pass == 2 -> 95
        else -> 120
    }

    /** The spin's station train for one [pass]: single ticks at the pass's amplitude. */
    fun spinTrain(pass: Int): DetentTrain = DetentTrain(
        positions = (0 until 10).map { it * SPIN_STATION_SPACING_DEG },
        specFor = {
            DetentEventSpec(
                amplitude = spinStationAmplitude(pass),
                durationMs = if (pass >= 3) 14 else 12
            )
        }
    )

    /** The gate (C1.2): a 35 ms void first, then one 28 ms pulse at 165. Never a station's shape. */
    val GateSpec: DetentEventSpec = DetentEventSpec(amplitude = 165, durationMs = 28, preSilenceMs = 35)

    /** The seat's approach ramp (C1.3a): rising pressure over the last ~15° before the seat. */
    const val SEAT_APPROACH_DEG: Float = 15f

    /** The seat fires only at or below this velocity — the hand must ARRIVE, not fling (C1.3). */
    const val SEAT_MAX_VELOCITY_DEG_PER_SEC: Float = 120f

    /** The seat thud (C1.3b): 235 decaying, then — after silence — the 75 settle. */
    val SeatThudSpec: DetentEventSpec = DetentEventSpec(amplitude = 235, durationMs = 65)
    val SeatSettleSpec: DetentEventSpec = DetentEventSpec(amplitude = 75, durationMs = 28, preSilenceMs = 30)

    /** After the seat, the dial is dead: the end-stop (C1.3c), flat 195, once per attempt. */
    val SpinEndStop: HardStopSpec = HardStopSpec(
        limit = 0f, approachZone = SEAT_APPROACH_DEG, maxResistance = 8f,
        refusalSpec = DetentEventSpec(amplitude = 195, durationMs = 45)
    )

    fun spinRotary(): WeightedRotary = WeightedRotary(
        spec = WeightedRotarySpec(
            body = MonolithBodies.Wheel,
            inertia = InertiaBody(mass = MonolithBodies.Wheel.mass, resistance = 0.9f),
            maxVelocityDegPerSec = SPIN_MAX_VELOCITY_DEG_PER_SEC,
            maxCoastDegrees = SPIN_MAX_COAST_DEG
        ),
        stationPositions = (0 until 10).map { it * SPIN_STATION_SPACING_DEG }
    )

    // ---------- THE LIFTING HOUR (FEEL-SR-3 C2) ----------

    /**
     * The hour train over [rangeMinutes]: sparse firm stones —
     * quarters at 55, whole hours at 85, silence between (C2.1).
     * Its resistance is the well curve: the setter is placed, and
     * only the snap (±4 min of a quarter, C2.2) completes him.
     */
    fun hourTrain(rangeMinutes: Int = 24 * 60): DetentTrain = DetentTrain(
        positions = (0..rangeMinutes step 15).map { it.toFloat() },
        specFor = { index ->
            val minute = index * 15
            if (minute % 60 == 0) DetentEventSpec(amplitude = 85, durationMs = 14)
            else DetentEventSpec(amplitude = 55, durationMs = 8)
        }
    )

    /** Release snap window (C2.2): within ±4 minutes of a quarter, the setter pulls in. */
    const val HOUR_SNAP_WINDOW_MINUTES: Float = 4f

    /**
     * The hour drag (C2) — ADDED BY BUILD-SR-18 at Stage 11/12
     * assembly: this preset's census row names "HourTrain +
     * HourDrag" but no factory stood in the toolkit as landed.
     * The rail is the quarter train; capture radius is the ±4 min
     * snap window itself, so the drag's own capture decision IS
     * the C2.2 rule (a release outside the window rests where the
     * coast ends — the caller places it, no snap).
     */
    fun hourDrag(): InertiaDrag = InertiaDrag(
        InertiaDragSpec(
            body = MonolithBodies.Wheel,
            inertia = InertiaBody(mass = MonolithBodies.Wheel.mass, resistance = 0.9f),
            rail = DetentRail((0..24 * 60 step 15).map { it.toFloat() }),
            captureRadius = HOUR_SNAP_WINDOW_MINUTES,
            captureVelocity = 30f
        )
    )

    /**
     * The break drag (C5) — likewise added by BUILD-SR-18: the
     * census row names "BreakTrain + BreakDrag"; the train stood,
     * the drag did not. The rail is whole minutes (the break is
     * priced to the minute); its settle is the honest kind — it
     * rests exactly on the minute it is captured by.
     */
    fun breakDrag(): InertiaDrag = InertiaDrag(
        InertiaDragSpec(
            body = MonolithBodies.Wheel,
            inertia = InertiaBody(mass = MonolithBodies.Wheel.mass, resistance = 1.2f),
            rail = DetentRail((0..720).map { it.toFloat() }),
            captureRadius = 0.5f,
            captureVelocity = 20f
        )
    )

    // ---------- THE ALLOWANCE (FEEL-SR-3 C3/C4) ----------

    /**
     * Tighten (C3.1): the pawl's double pulse per 5-minute detent,
     * amplitude RISING as the figure tightens — 140/100 at the
     * loosest step to 175/130 at the tightest. [tightness] is the
     * caller's 0..1 progress toward tight; the train is built per
     * position so the rise is in the data, not the rendering.
     */
    fun tightenTrain(steps: Int): DetentTrain = DetentTrain(
        positions = (0..steps).map { it * 5f },
        specFor = { index ->
            val t = if (steps == 0) 1f else index.toFloat() / steps
            DetentEventSpec(
                amplitude = (140 + 35 * t).toInt(),
                durationMs = 16,
                secondaryAmplitude = (100 + 30 * t).toInt(),
                secondaryGapMs = 10
            )
        }
    )

    /** The click-lock on finger-lift (C3.2): one hard 210 snap, instant, no tail. */
    val TightenLockSpec: DetentEventSpec = DetentEventSpec(amplitude = 210, durationMs = 35)

    /**
     * Raise (C4.1): THE HOLLOW SEAT's detents — peak 60, ramped,
     * ~50 ms late. The lateness is declared in the spec, per
     * [DetentEventSpec.latencyMs]'s law: deferral spoken in the
     * grammar's own terms.
     */
    fun hollowTrain(steps: Int): DetentTrain = DetentTrain(
        positions = (0..steps).map { it * 5f },
        specFor = {
            DetentEventSpec(amplitude = 60, durationMs = 22, latencyMs = 50, ramped = true)
        },
        resistance = ResistanceCurves.Free
    )

    /** The Unseated Rock on lift (C4.2): 85 → 55 → 30, dying out — NO terminal event follows it. */
    val UnseatedRockSpec: DetentEventSpec = DetentEventSpec(amplitude = 85, durationMs = 40)

    // ---------- THE BREAK (FEEL-SR-3 C5) ----------

    /**
     * The break train: continuous fine grain (30 per minute, 50 per
     * five minutes) with preset NOTCHES (75, ramped) laid over it —
     * and, by C5.2 law 1, resistance is [ResistanceCurves.Free]:
     * a notch is haptic-only topography; it must cost no travel.
     * [presetMinutes] names the shortcut values; the model owns
     * the list, this preset owns their feel.
     */
    fun breakTrain(rangeMinutes: Int = 120, presetMinutes: List<Int> = emptyList()): DetentTrain {
        val positions = (0..rangeMinutes).map { it.toFloat() }
        return DetentTrain(
            positions = positions,
            specFor = { index ->
                val minute = index
                when {
                    minute in presetMinutes -> DetentEventSpec(amplitude = 75, durationMs = 18, ramped = true)
                    minute % 5 == 0 -> DetentEventSpec(amplitude = 50, durationMs = 8)
                    else -> DetentEventSpec(amplitude = 30, durationMs = 6)
                }
            },
            resistance = ResistanceCurves.Free
        )
    }

    /** The break's zero end (C5): a flat floor at 130 — a position, not a toggle. */
    val BreakZeroStop: HardStopSpec = HardStopSpec(
        limit = 0f, sign = -1, approachZone = 3f, maxResistance = 6f,
        refusalSpec = DetentEventSpec(amplitude = 130, durationMs = 40)
    )

    // ---------- THE STRETCH (FEEL-SR-3 C6) ----------

    /**
     * Day steps (C6.1): the heaviest recurring detent in the app,
     * rising 150 → 195 with distance. Steps cannot fire faster
     * than one per 90 ms — carried here as data for the surface.
     */
    const val STRETCH_MIN_STEP_INTERVAL_MS: Long = 90L

    fun stretchTrain(days: Int): DetentTrain = DetentTrain(
        positions = (0..days).map { it.toFloat() },
        specFor = { index ->
            val t = if (days == 0) 1f else index.toFloat() / days
            DetentEventSpec(amplitude = (150 + 45 * t).toInt(), durationMs = 22)
        }
    )

    /**
     * The stretch drag (C6.1 law 1): NO INERTIA. The inertia body
     * is set so a release stills within a fraction of one day-step
     * at any lawful velocity — the same [InertiaDrag] machinery as
     * every other drag, parameterised to refuse a coast.
     */
    fun stretchDrag(days: Int): InertiaDrag = InertiaDrag(
        InertiaDragSpec(
            body = MonolithBodies.Crank,
            inertia = InertiaBody(mass = 1f, resistance = 400f),
            rail = DetentRail((0..days).map { it.toFloat() }),
            captureRadius = 0.49f,
            captureVelocity = 5f,
            // No lookahead: the capture is the day the hand is ON,
            // never a projection of its fling (C6.1 law 1).
            captureLookaheadSeconds = 0f
        )
    )

    // ---------- THE DRUM (ledger A-7, FEEL-SR-3 §04 reference rows) ----------

    /**
     * The revolving drum: one app per position, caught by its
     * detent (MOTION-SR-5's Drum body). A spent purse is the
     * [HardStop] at the travel's end — the crank will not turn.
     * E-08 (census, by ruling): the drum minute's END is this
     * drag's self-return — no separate end event exists, here or
     * anywhere in the toolkit.
     */
    fun drumDrag(positions: List<Float>): InertiaDrag = InertiaDrag(
        InertiaDragSpec(
            body = MonolithBodies.Drum,
            inertia = InertiaBody(mass = MonolithBodies.Drum.mass, resistance = 1.1f),
            rail = DetentRail(positions),
            captureRadius = 0.25f,
            captureVelocity = 0.4f
        )
    )

    /** The governor's crank against a spent purse: resistance, then refusal, once per attempt. */
    fun spentPurseStop(travelLimit: Float): HardStop = HardStop(
        HardStopSpec(limit = travelLimit, approachZone = SPENT_STOP_TRAVEL_DEG, maxResistance = 10f)
    )

    // ---------- CENSUS ADDITIONS (LEAD-STAGE-11, filed mid-leg, by number) ----------

    /**
     * Elements the census rules OUT of the toolkit, by name — their
     * absence is ruled, not missed (letter of 2026-11-02):
     * E-13 ring + hour (stillness at Stage 10's grade: no motion,
     * no haptic, no hook — kept out entirely).
     * E-08 the drum minute's end (no separate end event exists:
     * the end IS [drumDrag]'s self-return, per Rulings Round 3 on
     * FEEL-SR-4 GAP-1 — named here so the tracker can cite it).
     */
    val ExcludedByLaw: List<CensusPreset> = listOf(
        CensusPreset("ring-hour", "E-13 EXCLUDED BY LAW: stillness — no preset, no hook"),
        CensusPreset("drum-minute-end", "E-08 NO SEPARATE EVENT: the drum's self-return is the end")
    )

    /**
     * E-04 · THE THREE CLUNKS AS ONE CEREMONY — the pairing hook
     * only: each bolt event ↔ its clunk ID (MOTION-SR-5's sync
     * point names, verbatim). The samples are SOUND-SR-1's
     * synthesised set (Stage 13 preparation); this toolkit pairs,
     * it does not sound.
     */
    val BoltClunkPairing: List<Pair<String, String>> = listOf(
        "bolt-1" to "bolt-clunk-1",
        "bolt-2" to "bolt-clunk-2",
        "bolt-3" to "bolt-clunk-3"
    )

    /**
     * E-06 · THE GRILLE DOOR'S SECOND TOUCH — two phases, one
     * preset: the LATCH seats first (peak ≤150, ≤40 ms —
     * FEEL-SR-2's constraint, MOTION-SR-2 §2's numbers), then the
     * WEIGHTED SWING on the door's own body. The two phases'
     * curves are distinct BY VALUE (the M-4 guard): the latch's
     * progressive seat-approach is not the swing's viscous weight,
     * and neither is any other preset's curve.
     */
    val GrilleLatchSpec: DetentEventSpec = DetentEventSpec(amplitude = 150, durationMs = 40)
    val GrilleLatchCurve: ResistanceCurve =
        ResistanceCurves.progressive(centre = 0f, scale = 1f, base = 1f, gain = 1.5f)
    val GrilleSwingCurve: ResistanceCurve = ResistanceCurves.viscous(gain = 0.35f)

    /** The grille swing's body: the door's weight, at grille scale in the caller's units. */
    val GrilleSwingBody get() = MonolithBodies.DoorWeight

    /**
     * E-10 · THE HARD STOPS (drum spent / crank spent) — distinct
     * from the cranks themselves: strain → STOP → silence → one
     * unload, no dialog reachable. The strain travel is RULED
     * 4.0° (Rulings Round 3, MOTION-SR-2 S-2) — it is the
     * [HardStopSpec.approachZone] of every spent stop this
     * toolkit builds; the refusal event is the one unload, and
     * the silence after it is [HardStop]'s once-per-attempt law.
     */
    const val SPENT_STOP_TRAVEL_DEG: Float = 4.0f

    /**
     * E-14 · THE DESK CLOCK'S FORMING — curves-as-data is lawful
     * (MOTION-SR-3): the extent is LINEAR in held time. An eased
     * extent is a named send-back in that filing, so this curve
     * is the identity, asserted linear by test. No felt or sound
     * hook exists for the clock, and none is offered here.
     */
    fun deskClockExtent(heldFraction: Float): Float = heldFraction.coerceIn(0f, 1f)

    /**
     * THE DYNAMIC RANGE, AS DATA (H-6 / FEEL-SR-3 §2): the day's
     * peak amplitudes, heaviest → lightest, exactly as filed —
     * carve cut 240 · seat thud 235 · click-lock 210 · end-stop
     * 195 · … · break grain 30. A preset system that compresses
     * this range toward one middle buzz fails even with every
     * waveform present; the range therefore ships in the data,
     * not as a tuning afterthought.
     */
    val DynamicRangePeaks: List<Int> = listOf(
        240, 235, 210, 195, 175, 165, 150, 130, 120, 110, 85, 75, 60, 55, 50, 30
    )
}
