package com.example.ui.drawer

import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.DetentRail
import com.example.ui.foundation.PhysicsBody
import com.example.ui.foundation.SettleSpec

/**
 * THE DRAWER'S MECHANISMS — the pure half of BUILD-SR-41's slice
 * (census C-22 · C-07 · C-19 · C-11, INVENTORY-SR-1, derived
 * firsthand; the brief's merge of the rows is the coordinator's:
 * THE DRAWER (C-22) holds THE PLATE STACK (C-07) as its contents
 * in THE TRAY (C-19), and THE HANDING BACK (C-11) is its
 * threshold act). No Android, no Compose: every law the drawer
 * obeys is stated here where the venue can pin it, and the
 * surface in Drawer.kt only renders what this file derives.
 *
 * The laws, by census row:
 *
 *  - C-22 THE DRAWER: a quiet drawer in the room's furniture
 *    where finished tasks are PUT AWAY AND KEPT — stored, not
 *    displayed. An unsealed-hours surface only, opened rarely,
 *    BY HIS HAND, away from the signing place; never reachable
 *    while sealed (ledger D-7). Nothing in it is scored, graded,
 *    totalled, or surfaced back at him; it offers nothing and
 *    prompts nothing. Its only lawful words are the stored
 *    tasks' own words — HIS words, as written at their signings
 *    (TX-SAY-07). The drawer adds no title-word, count,
 *    date-grade, or summary of its own. A drawer that reads as
 *    a history LIST is the condemned form: these are objects in
 *    a drawer, lifted and looked at one by one — so this model
 *    carries NO list operation, no index, no search, no sort
 *    key other than the stack's own order.
 *  - C-07 THE PLATE STACK, as the drawer's contents: brass
 *    plates stacking in their days' order, all season — the
 *    growing pile IS the progress display (ledger E-2). The
 *    stack type offers NO operation that sums, totals, scores,
 *    grades, or streaks (TX-SHOW-19; every aggregate form is
 *    DIE, TX-DIE-645). Two views, one model: a plate here is
 *    the store's struck row for its day — the same truth the
 *    strike struck and the doorway strip renders — re-expressed
 *    field for field, never a second record.
 *  - C-19 THE TRAY: the drawer's receptacle. It receives and
 *    keeps: the plates, and the governor's snapped lead seals,
 *    kept visible — drawn in the drawer's back compartment from
 *    the plates' own crank fact ([DrawerContents.snappedSealDays],
 *    the pile's own derivation, as P6 filed it).
 *  - C-11 THE HANDING BACK, the drawer's threshold act: before
 *    the plate is read, the day is handed back. At the drawer's
 *    threshold the people who waited are handed back into his
 *    phone's own list FIRST — the plates cannot be lifted until
 *    the handing back stands delivered ([HandingBackGate]).
 *    Preparation is [LiftingPlate.HandingBack.prepare] itself
 *    (P6's type, one model); delivery is the wiring layer's act,
 *    confirmed to this model as a flag — exactly the ceremony's
 *    own shape at the lifting. It prints nothing new, announces
 *    nothing, counts nobody (C-11: no announcement, summary, or
 *    count of who waited is lawful).
 *  - SCREENSHOTS ALLOWED (Sarthak's order, Nov 3, 2026): the
 *    old R-6 FLAG_SECURE predicate that lived here is deleted
 *    whole. The drawer — like every surface — is screenshottable
 *    and thumbnails in Recents normally.
 *  - THE SLIDE, in physics vocabulary: the drawer runs on a
 *    heavy body ([DrawerSlide.Body] — mass 2.4, ζ 0.92,
 *    unit-stiffness 260: under-damped by a breath, so the
 *    drawer arrives with weight and settles once, never
 *    bouncing like a toy and never creeping like a default
 *    animation), caught at both ends of its travel by a detent
 *    rail ([DrawerSlide.Rail]: shut · open). A plate lifted
 *    from the pile rides its own lighter body
 *    ([DrawerSlide.PlateBody]) and settles back into the stack
 *    when his hand leaves it. Every pose is a pure function of
 *    the body and the elapsed clock — no easing curve exists
 *    in this slice. All figures UNPROVEN-ON-DEVICE, as filed.
 */

/** One punched hole on a kept plate, at its struck grade. The
 *  grade is P6's (reach large · held medium · floor small —
 *  a finger could learn them, ledger E-9); SHOWING punches
 *  strike at the HELD grade (the doorway strip's own mapping,
 *  carried — the hole drawn is unchanged). */
data class DrawerPunch(val grade: LiftingPlate.PunchGrade)

/** The seal line's lawful states on a kept plate — exactly two
 *  (TX-SHOW-13). A crank day reads WHOLE here: the crank's
 *  snapped seal is its own kept fact ([DrawerPlate.crankSealKept]),
 *  not the day seal's failure (P1's law, carried — GAP-COORD-05's
 *  question is the plate's telling at the strike, not this
 *  drawer's to re-open). */
enum class DrawerSealLine { WHOLE, BROKEN }

/**
 * One struck brass plate, kept in the drawer. Re-expressed from
 * the store's struck row ([StrongroomDay.DayPlate]) field for
 * field — the same truth, the drawer's view of it.
 *
 * [taskLine] is the day's finished task in HIS OWN WORDS (B-3,
 * TX-SAY-07) — the drawer's one lawful text (C-22). It is
 * carried verbatim, never edited, never summarised.
 * [hoursHeldMillis] is carried truthfully (TX-SHOW-10's struck
 * figure) and rendered NOWHERE in this slice: no format for it
 * was ever decided, and the doorway slice's ruling stands —
 * the figure is the strike's struck form, not a number a
 * browsing surface mints a costume for.
 * [bracketStandingHour] / [bracketFallenHour] are the fallen
 * day's bracket pair in its settled words' shape (TX-SHOW-14:
 * "standing at the last check, fallen by the first check that
 * found it") — carried only when the day row's strike carried
 * them; the store's day row persists the gap's length but not
 * its hours, so at the host the pair stands absent rather than
 * invented (the doorway composition's named resolution, carried).
 */
data class DrawerPlate(
    val filingDateIso: String,
    val sealLine: DrawerSealLine,
    val punches: List<DrawerPunch>,
    val taskLine: String?,
    val crankSealKept: Boolean,
    val hoursHeldMillis: Long,
    val bracketStandingHour: String? = null,
    val bracketFallenHour: String? = null
) {
    val isFailure: Boolean get() = sealLine == DrawerSealLine.BROKEN

    companion object {
        /**
         * The one mapping from the store's struck row. A plate
         * exists only once struck: a still-SEALED day has no
         * plate to keep (B-14 — hours the hold did not cover
         * strike nothing), and this mapping refuses it whole
         * rather than inventing a seal state for the drawer.
         */
        fun fromStruckRow(row: StrongroomDay.DayPlate): DrawerPlate {
            require(row.sealState != StrongroomDay.SealState.SEALED) {
                "a sealed day's plate is not struck yet"
            }
            return DrawerPlate(
                filingDateIso = row.filingDateIso,
                sealLine = when (row.sealState) {
                    StrongroomDay.SealState.LIFTED -> DrawerSealLine.WHOLE
                    StrongroomDay.SealState.BROKEN,
                    StrongroomDay.SealState.AJAR -> DrawerSealLine.BROKEN
                    StrongroomDay.SealState.SEALED -> error("unreachable: guarded above")
                },
                punches = row.punches.map { punch ->
                    DrawerPunch(
                        grade = when (punch.kind) {
                            StrongroomDay.PunchKind.REACH -> LiftingPlate.PunchGrade.LARGE
                            StrongroomDay.PunchKind.HELD,
                            StrongroomDay.PunchKind.SHOWING -> LiftingPlate.PunchGrade.MEDIUM
                            StrongroomDay.PunchKind.FLOOR -> LiftingPlate.PunchGrade.SMALL
                        }
                    )
                },
                taskLine = row.taskLine,
                crankSealKept = row.crankSealBroken,
                hoursHeldMillis = row.hoursHeldMillis
            )
        }
    }
}

/** One stored finished task from the drawer's older keeping —
 *  the legacy COMPLETED rows the store's drawerPage re-expresses
 *  ([StrongroomDay.DrawerEntry]). A title in his words and when
 *  it was finished — the type carries nothing that could judge
 *  it (D-8), and this slice adds no field to it. */
data class DrawerTaskSlip(
    val taskId: Int,
    val title: String,
    val completedAtMillis: Long
) {
    companion object {
        fun fromEntry(entry: StrongroomDay.DrawerEntry): DrawerTaskSlip =
            DrawerTaskSlip(entry.taskId, entry.title, entry.completedAtMillis)
    }
}

/**
 * The drawer's contents, derived whole from the standing facts
 * in one act: the struck plates (the store's rows, struck states
 * only — the host filters; this derivation trusts the filter
 * and [DrawerPlate.fromStruckRow] enforces it) and the stored
 * task slips. The same facts derive the same drawer on every
 * opening — a reboot cannot empty it, and nothing here is
 * re-struck, re-scored, or re-counted at read time.
 */
data class DrawerContents(
    /** The pile: newest struck on top — the order a hand lifts
     *  them (P6's PlatePile order, carried). */
    val platesNewestFirst: List<DrawerPlate>,
    /** Stored task slips, most recently finished first. */
    val tasksNewestFirst: List<DrawerTaskSlip>
) {
    /** True when the drawer stands empty — the furniture as it
     *  is, no placeholder, no "nothing here yet" (GAP-R03). */
    val isEmpty: Boolean get() = platesNewestFirst.isEmpty() && tasksNewestFirst.isEmpty()

    /** The days whose snapped crank seals are kept in the tray,
     *  in stack order (newest first) — P6's pile derivation,
     *  carried whole. The tray draws one kept seal per day here. */
    fun snappedSealDays(): List<String> =
        platesNewestFirst.filter { it.crankSealKept }.map { it.filingDateIso }

    fun plateFor(filingDateIso: String): DrawerPlate? =
        platesNewestFirst.firstOrNull { it.filingDateIso == filingDateIso }

    companion object {
        fun derive(
            struckRows: List<StrongroomDay.DayPlate>,
            storedEntries: List<StrongroomDay.DrawerEntry>
        ): DrawerContents = DrawerContents(
            platesNewestFirst = struckRows
                .map { DrawerPlate.fromStruckRow(it) }
                .sortedByDescending { it.filingDateIso },
            tasksNewestFirst = storedEntries
                .map { DrawerTaskSlip.fromEntry(it) }
                .sortedByDescending { it.completedAtMillis }
        )

        val Empty = DrawerContents(emptyList(), emptyList())
    }
}

/**
 * C-11 at the drawer's threshold, as a gate: the plates may be
 * lifted only when the handing back stands delivered — or when
 * nobody waited, in which case the gate stands open by the same
 * act that delivers it empty (the lifting ceremony's own rule,
 * carried from [LiftingPlate.advance]).
 */
object HandingBackGate {
    fun platesRevealed(handingBack: LiftingPlate.HandingBack, delivered: Boolean): Boolean =
        delivered || handingBack.isEmpty
}

/**
 * THE SLIDE — the drawer's travel as a body, not an animation.
 * Travel is a fraction 0 (shut) .. 1 (drawn fully open). The
 * body is heavy and a breath under-damped; the rail catches it
 * at both ends of its run. A plate lifted from the pile rides
 * [PlateBody] — brass in the hand: lighter, quicker, caught by
 * the stack when it settles back.
 */
object DrawerSlide {

    /** The drawer's body: mass 2.4 · ζ 0.92 · unit-stiffness 260
     *  (k = 624, c ≈ 56.3 by the foundation's character law). */
    val Body: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 2.4, dampingRatio = 0.92, normalisedStiffness = 260.0,
        settle = SettleSpec(distanceEpsilon = 0.004, velocityEpsilon = 0.02)
    )

    /** A lifted plate's body: mass 0.9 · ζ 0.85 · unit-stiffness
     *  340 — brass leaves the pile with a small overshoot and is
     *  caught, honestly, by the stack beneath it. */
    val PlateBody: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 0.9, dampingRatio = 0.85, normalisedStiffness = 340.0,
        settle = SettleSpec(distanceEpsilon = 0.004, velocityEpsilon = 0.02)
    )

    /** The run's two detents: shut and drawn. There is no lawful
     *  resting place between them — a drawer left ajar is a hand
     *  still on it, and the rail's capture decides where it
     *  settles when the hand leaves. */
    val Rail: DetentRail = DetentRail(listOf(0.0, 1.0))

    /** The drawer's travel fraction at [elapsedMs] into a slide
     *  from [from] toward [to] — a pure function of the body. */
    fun travelAt(from: Double, to: Double, elapsedMs: Int): Double {
        if (elapsedMs <= 0) return from
        return BodyIntegrator.valueAt(from, to, Body, elapsedMs / 1000.0)
            .coerceIn(0.0, 1.0)
    }

    /** A lifted plate's rise fraction at [elapsedMs] (0 = lying
     *  in the pile, 1 = fully lifted into the hand's view). */
    fun plateRiseAt(from: Double, to: Double, elapsedMs: Int): Double {
        if (elapsedMs <= 0) return from
        return BodyIntegrator.valueAt(from, to, PlateBody, elapsedMs / 1000.0)
            .coerceIn(0.0, 1.0)
    }

    /** Where a released drawer settles: the detent its projected
     *  landing is heading for (the rail's capture law). */
    fun settleTarget(travel: Double, velocity: Double): Double =
        Rail.captureTarget(travel, velocity)
}

/**
 * THE PILE'S GEOMETRY — how the stack lies in the drawer, as
 * figures the surface spends and the tests pin. Plates lie
 * overlapped: each plate shows a struck band of the one beneath
 * it, the way brass lies in a tray — the pile's HEIGHT grows
 * with the season, and that growth is the progress display
 * (E-2). No figure here counts anything aloud: these are
 * drawing figures, never rendered as numbers.
 */
object PileGeometry {

    /** One plate's full height, dp. */
    const val PLATE_HEIGHT_DP: Float = 64f

    /** The band each buried plate shows above the one below it,
     *  dp — its struck edge: holes and seal line live on the
     *  top plate whole; a buried plate shows its edge only. */
    const val BAND_DP: Float = 15f

    /** The drawer's visible depth, dp: past this the pile runs
     *  under the drawer's back and is reached by his hand
     *  (scroll), never by a "show more" — there is none. */
    const val VIEWPORT_DP: Float = 300f

    /** The pile's drawn height for [plateCount] plates: the top
     *  plate whole, every plate beneath it one band taller. */
    fun pileHeightDp(plateCount: Int): Float =
        if (plateCount <= 0) 0f else PLATE_HEIGHT_DP + (plateCount - 1) * BAND_DP

    /** How far a lifted plate rises clear of the pile, dp — into
     *  the hand's view, above the stack it was lifted from. */
    const val LIFT_CLEARANCE_DP: Float = 10f
}
