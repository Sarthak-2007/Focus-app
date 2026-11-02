package com.example.ui.strongroom

import com.example.domain.DeskClockRecord
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.domain.StrongroomRouting
import com.example.ui.guard.GuardSurfaceSnapshot
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * STAGE 15 — THE INTEGRATED PROTOTYPE'S FLOW LAW (BUILD-SR-21).
 *
 * Pure Kotlin, no Android imports — the same law the other
 * Strongroom models stand under. This file is the composition
 * act the lanes named and left for P9/Stage 15: every composed
 * surface's intake shape is built HERE from P1/P2's standing
 * state, and the day's opening place is derived HERE from the
 * same facts, so the host composable renders a decision it does
 * not invent.
 *
 * THE DAY, AS ONE FLOW (the host walks exactly this):
 *  FIRST_RUN (≤10 minutes, one confirmation — UX-SR-5 §1) →
 *  DOORWAY (the open door: today's standing + the room's doors) →
 *  SIGNING (the evening's amendment of tomorrow's composition) →
 *  the SWING (the doorway's seal act; P1/P2 strike the span) →
 *  SEALED_FACE (the session's whole world; the grille is the one
 *  way onward) → ANTECHAMBER (grille · drum · held pile) →
 *  DESK_CLOCK (the break's arrival stands here: the forming
 *  plate's firing span — R-C2: no readout, texture only) →
 *  LIFTING (hand-back first, then the plate) → the ROOM (pile +
 *  drawer, B-16) and the GUARD, in their ruled unsealed places.
 *
 * While a seal stands, [StrongroomRouting.SEALED_SURFACES] is
 * the whole lawful set and this model offers no other place:
 * [placeWhileSealed] answers only face / antechamber / clock.
 */
object StrongroomFlowModel {

    /** Every place the integrated day can stand in. */
    enum class Place {
        FIRST_RUN, DOORWAY, SIGNING, SEALED_FACE, ANTECHAMBER,
        DESK_CLOCK, LIFTING, ROOM_PILE, ROOM_DRAWER, GUARD
    }

    /**
     * The day's opening place, from standing facts alone:
     *  - an unpresented strike (lifted or fallen) opens at the
     *    LIFTING — the plate is offered before anything else,
     *    owed never (B-15), and a fallen day's testimony is
     *    never gated behind the room;
     *  - a standing seal opens on its FACE — the sealed world
     *    is the whole world while it stands;
     *  - an unstamped first run opens at the FIRST_RUN;
     *  - everything else opens at the DOORWAY.
     */
    fun openingPlace(
        setupCompleted: Boolean,
        sealedStanding: Boolean,
        strikeUnpresented: Boolean
    ): Place = when {
        strikeUnpresented -> Place.LIFTING
        sealedStanding -> Place.SEALED_FACE
        !setupCompleted -> Place.FIRST_RUN
        else -> Place.DOORWAY
    }

    /** The lawful places while a seal stands (P2 containment,
     *  restated for the host: nothing else may render). */
    fun placeLawfulWhileSealed(place: Place): Boolean = when (place) {
        Place.SEALED_FACE -> StrongroomRouting.surfaceLawfulWhileSealed(
            StrongroomRouting.Surface.SEALED_FACE
        )
        Place.ANTECHAMBER -> StrongroomRouting.surfaceLawfulWhileSealed(
            StrongroomRouting.Surface.ANTECHAMBER
        )
        Place.DESK_CLOCK -> StrongroomRouting.surfaceLawfulWhileSealed(
            StrongroomRouting.Surface.DESK_CLOCK
        )
        else -> false
    }

    /** The break's arrival: while the priced firing runs, the
     *  sealed world's standing surface is the desk clock — its
     *  forming plate carries the firing as texture (SMOOTH_FIRING),
     *  the gap-arrival state change R-C2 names. Before and after
     *  the firing, the face stands. */
    fun sealedPlace(breakRunning: Boolean, inAntechamber: Boolean): Place = when {
        inAntechamber -> Place.ANTECHAMBER
        breakRunning -> Place.DESK_CLOCK
        else -> Place.SEALED_FACE
    }

    // ------------------------------------------------------------------
    // The signing's draft — from the standing composition (B-2)
    // ------------------------------------------------------------------

    /**
     * Build P4's intake draft from a composed day (P1's
     * composition is its source, exactly as [CompositionDraft]
     * requires). [appOptions] are the day's catalogue in the
     * catalogue's own order; the composed locked set must stand
     * among them (the draft's own init enforces it — a locked
     * package that left the catalogue is dropped here, by name
     * in the host's record, rather than crashing the signing).
     */
    fun signingDraft(
        dayShapeName: String,
        appOptions: List<SigningAppOption>,
        composedLocked: Set<String>,
        liftingTime: LocalTime,
        sentence: String?,
        breakLengthMinutes: Int,
        spentDay: Boolean,
        teachingLine: String? = null
    ): CompositionDraft = CompositionDraft(
        dayShapeName = dayShapeName,
        appOptions = appOptions,
        composedLocked = composedLocked.intersect(appOptions.map { it.packageName }.toSet()),
        composedHour = hourText(liftingTime),
        composedSentence = sentence,
        spentDay = spentDay,
        composedBreakMinutes = breakLengthMinutes,
        teachingLine = teachingLine
    )

    /** The lawful HH:MM figure (FIG-1) for a wall time. */
    fun hourText(time: LocalTime): String =
        time.format(DateTimeFormatter.ofPattern("HH:mm"))

    /** The lawful HH:MM figure of a wall instant, in [zone]. */
    fun hourText(wallMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        Instant.ofEpochMilli(wallMillis).atZone(zone).toLocalTime()
            .format(DateTimeFormatter.ofPattern("HH:mm"))

    // ------------------------------------------------------------------
    // The sealed face — the sealing-time snapshot (P3's intake)
    // ------------------------------------------------------------------

    /** The face as engraved at sealing: the day's lifting hour
     *  and his sentence (or its kept place), nothing else — the
     *  model's own law forbids any live field. */
    fun sealedFace(signing: StrongroomDay.SigningRecord): SealedFaceState =
        SealedFaceState(
            liftingHour = hourText(signing.liftingTime),
            sentence = signing.sentenceOrKeptPlace
        )

    // ------------------------------------------------------------------
    // The antechamber — two sets + the pile (P5's intake)
    // ------------------------------------------------------------------

    /**
     * Build the room from the sealed day's signed record. The
     * signed set renders as plain words exactly as signed (B-9):
     * [nameOf] resolves a package to the name his phone gives it;
     * a package with no name in the catalogue renders as its
     * package name — the signed fact is never dropped for a
     * missing label. The drum's stock is every OTHER catalogued
     * app (not locked, not signed) by name. The purse is the
     * day's own remaining allowance (P1's purse arithmetic is
     * its source). Held calls/codes arrive empty until a
     * production held-pile store exists — named in the record,
     * never fabricated here.
     */
    fun antechamberState(
        signing: StrongroomDay.SigningRecord,
        purseRemainingMinutes: Int,
        nameOf: (String) -> String,
        cataloguePackages: List<String>
    ): AntechamberState {
        val signedNames = signing.allowedPackages.map(nameOf)
        val drumNames = cataloguePackages
            .filter { it !in signing.lockedPackages && it !in signing.allowedPackages }
            .map(nameOf)
        return AntechamberState(
            allowedAsSigned = signedNames,
            drumCandidates = drumNames,
            purseMinutes = purseRemainingMinutes
        )
    }

    /** The antechamber's read-only day panel (B-9's day-as-set
     *  face): the day's standing facts as plain lines — the
     *  lifting hour, the priced break, his task line when one
     *  stands. No control exists among them, by construction. */
    fun antechamberReadOnlyLines(signing: StrongroomDay.SigningRecord): List<String> =
        buildList {
            add("Sealed until ${hourText(signing.liftingTime)}")
            if (signing.breakLengthMinutes > 0 && signing.breakFiresAtOffsetMillis >= 0L) {
                add("The break stands priced at ${signing.breakLengthMinutes} minutes")
            }
            signing.taskLine?.takeIf { it.isNotBlank() }?.let { add(it) }
        }

    // ------------------------------------------------------------------
    // The desk clock — P1 facts onto P8's plain facts (the seam
    // BUILD-SR-6 named for composition, discharged here)
    // ------------------------------------------------------------------

    /** P1 punches onto the clock's three species. P1's FLOOR
     *  punch has no desk-clock species — DESIGNER-SR-6's spec
     *  superseded the floor lane (BUILD-SR-6 addendum item 2) —
     *  so floor punches are not carried onto the forming plate;
     *  they stand on the day plate, where P6 renders them. */
    fun deskClockPunches(
        punches: List<StrongroomDay.Punch>
    ): List<DeskClockRecord.PunchFact> = punches.mapNotNull { punch ->
        val kind = when (punch.kind) {
            StrongroomDay.PunchKind.REACH -> DeskClockRecord.PunchKind.REACH
            StrongroomDay.PunchKind.HELD -> DeskClockRecord.PunchKind.HELD
            StrongroomDay.PunchKind.SHOWING -> DeskClockRecord.PunchKind.SHOWING
            StrongroomDay.PunchKind.FLOOR -> null
        } ?: return@mapNotNull null
        DeskClockRecord.PunchFact(kind = kind, atOffsetMillis = punch.atOffsetMillis)
    }

    /** The day's texture spans: the IN_SPAN shaped gaps (gym ·
     *  sadhana · meal — meditation is pre-swing and sleep is
     *  terminal; neither stands inside the span) plus the
     *  break's firing span, marked as its own species. */
    fun deskClockGaps(
        signing: StrongroomDay.SigningRecord,
        seal: StrongroomDay.SealSpan
    ): List<DeskClockRecord.GapSpan> = buildList {
        StrongroomDay.inSpanGaps(signing.gaps).forEach { gap ->
            add(
                DeskClockRecord.GapSpan(
                    startOffsetMillis = gap.startOffsetMillis,
                    durationMillis = gap.durationMillis,
                    isBreakFiring = false
                )
            )
        }
        StrongroomDay.breakFiring(signing, seal)?.let { firing ->
            add(
                DeskClockRecord.GapSpan(
                    startOffsetMillis = firing.firesAtElapsedMillis - seal.sealedAtElapsedMillis,
                    durationMillis = firing.lengthMinutes * 60_000L,
                    isBreakFiring = true
                )
            )
        }
    }

    // ------------------------------------------------------------------
    // The plate — P1's struck day onto P6's struck plate (the
    // failureFace supply BUILD-SR-14 named as the composition
    // act, discharged here for the lifting surface)
    // ------------------------------------------------------------------

    /**
     * Adapt P1's [StrongroomDay.DayPlate] to P6's
     * [LiftingPlate.StruckPlate], or null when no seal stood
     * (B-14: no empty plate exists). The seal truth maps whole /
     * broken / ajar by name. THE BRACKET: a fallen day's truth
     * requires both bracket hours (P6's own init). The hours
     * supplied are the two real recorded instants the composed
     * build holds: [standingHour] — the seal's striking wall
     * hour, the last moment the seal is recorded standing; and
     * [fallenHour] — the hour of the check that found it fallen
     * (the boot/strike discovery, as the host reads it). No
     * other hours exist in the composed tree to supply; the
     * derivation is named in the walk record, never hidden.
     */
    fun struckPlate(
        dayPlate: StrongroomDay.DayPlate,
        spanDurationMillis: Long,
        standingHour: String?,
        fallenHour: String?
    ): LiftingPlate.StruckPlate? {
        val truth = LiftingPlate.plateTruthFor(
            filingDateIso = dayPlate.filingDateIso,
            sealedSpanMillis = spanDurationMillis,
            hoursHeldMillis = dayPlate.hoursHeldMillis,
            punches = dayPlate.punches.map { punch ->
                LiftingPlate.Punch(
                    // P6's plate carries three species; P1's SHOWING
                    // (R7-2) is a held-family taking struck at the
                    // HELD grade in P1's own law, so it stands in
                    // the plate's HELD row — the grade is preserved,
                    // the species merge is named in the walk record.
                    kind = when (punch.kind) {
                        StrongroomDay.PunchKind.REACH -> LiftingPlate.PunchKind.REACH
                        StrongroomDay.PunchKind.HELD -> LiftingPlate.PunchKind.HELD
                        StrongroomDay.PunchKind.SHOWING -> LiftingPlate.PunchKind.HELD
                        StrongroomDay.PunchKind.FLOOR -> LiftingPlate.PunchKind.FLOOR
                    },
                    atOffsetMillis = punch.atOffsetMillis,
                    packageName = punch.packageName
                )
            },
            sealTruth = when (dayPlate.sealState) {
                StrongroomDay.SealState.LIFTED -> LiftingPlate.SealTruth.WHOLE
                StrongroomDay.SealState.BROKEN -> LiftingPlate.SealTruth.BROKEN
                StrongroomDay.SealState.AJAR -> LiftingPlate.SealTruth.AJAR
                StrongroomDay.SealState.SEALED -> return null
            },
            crankSealBroken = dayPlate.crankSealBroken,
            bracketGapMillis = dayPlate.bracketGapMillis,
            bracketStandingHour = standingHour,
            bracketFallenHour = fallenHour
        ) ?: return null
        return LiftingPlate.StruckPlate(
            truth = truth,
            struckAtElapsedMillis = dayPlate.hoursHeldMillis
        )
    }

    /** The pile, newest first — the room's progress display
     *  (A-11/E-2): plates stacking, no operation that could
     *  produce a total exists on the type. */
    fun platePile(plates: List<LiftingPlate.StruckPlate>): LiftingPlate.PlatePile =
        LiftingPlate.PlatePile(plates)

    /** The drawer from the store's stored tasks (D-8): titles
     *  and finishing instants only — the mapping drops every
     *  other field the legacy row carries, as P1's store does. */
    fun taskDrawer(entries: List<StrongroomDay.DrawerEntry>): LiftingPlate.TaskDrawer =
        LiftingPlate.TaskDrawer(
            entries.map { entry ->
                LiftingPlate.DrawerTask(
                    taskId = entry.taskId,
                    title = entry.title,
                    completedAtMillis = entry.completedAtMillis
                )
            }
        )

    // ------------------------------------------------------------------
    // The Guard — P1's state onto P7's snapshot (the seam
    // GuardSurfaceModel's header names, discharged here)
    // ------------------------------------------------------------------

    /**
     * Build the Guard surface's snapshot from the store's Guard
     * state plus the engine facts only the wiring can supply
     * (sealedBlockActive from P2's standing seal; the crank's
     * week fact). Advance-chosen exits and routine rows carry
     * no production source in the composed tree — they arrive
     * empty rather than invented, and the absence is named in
     * the walk record.
     */
    fun guardSnapshot(
        guard: StrongroomGuard.GuardState,
        nowMillis: Long,
        sealedBlockActive: Boolean,
        purseSpentToday: Boolean,
        crankMinuteSpent: Boolean
    ): GuardSurfaceSnapshot = GuardSurfaceSnapshot(
        guardOn = guard.guardOn,
        allowanceMinutes = guard.allowanceMinutes,
        pendingAllowanceMinutes = guard.pendingAllowanceMinutes,
        pendingAllowanceFromMillis = guard.pendingAllowanceFromMillis,
        pendingOffFromMillis = guard.pendingOffFromMillis,
        stretchEndMillis = guard.stretchEndMillis,
        nowMillis = nowMillis,
        nextDayStartMillis = StrongroomDay.nextDayStartMillis(nowMillis),
        sealedBlockActive = sealedBlockActive,
        purseSpentToday = purseSpentToday,
        crankMinuteSpent = crankMinuteSpent,
        advanceChosenExits = emptyList(),
        routines = emptyList()
    )
}
