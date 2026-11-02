package com.example.domain

import java.time.ZoneId

/**
 * P6 — THE LIFTING, THE DAY PLATE, THE PILE, THE DRAWER
 * (BUILD-SR-4, build-first-usable, Slice 1).
 *
 * Pure Kotlin, no Android imports — the same law [FocusDomain] and
 * BUILD-SR-3's StrongroomDay stand under. This file is the truth
 * of the day's END, built against P1's filed model contract
 * (StrongroomDay.DayPlate / PunchKind / DrawerEntry, read firsthand
 * from BUILD-SR-3's lane) — the shapes below mirror that contract
 * field for field so the wiring layer (P2/P9) maps between them
 * without translation loss; where P1's types land on main, these
 * wrappers re-point at them in one move (the seam is named in this
 * lane's opening line).
 *
 * The substance, by ledger row:
 *
 *  - A-13 / the brief: THE LIFTING IS AN EVENT. Bolts withdraw one
 *    by one; the door opens on its own weight. There is no fade
 *    state anywhere in this file — the ceremony's phases are iron
 *    and weight, and a phase machine that cannot express a screen
 *    fade cannot be wired into one.
 *  - B-13: THE HANDING BACK IS FIRST. At the lift itself, the
 *    people who waited are handed back into his phone's own list,
 *    BEFORE the plate is read, before everything else. The phase
 *    machine below makes the plate unreachable until the handing
 *    back is delivered — not by convention, by construction.
 *  - A-10 / B-14 / E-1: THE PLATE IS STRUCK ONCE, NEVER RE-STRUCK.
 *    Hours held · each reach AND each held call a punched hole at
 *    graded sizes · the seal whole or broken · the crank's snapped
 *    seal kept. A second strike returns the standing plate
 *    untouched ([PlateStrikeGuard]).
 *  - B-14: hours the hold did NOT cover strike NOTHING. A day no
 *    seal stood over produces no plate at all — not an empty
 *    plate, no plate ([plateTruthFor] returns null). Punches
 *    outside the held span are not struck: they belong to hours
 *    the hold never covered.
 *  - A-12 / B-11 / E-6: FAILURE IS THE AJAR FACE + THE BRACKET
 *    STRIKE. A fallen day's plate carries the bracket gap in
 *    bracket hours — "standing at the last check, fallen by the
 *    first check that found it" (A-12's settled words) — and the
 *    ceremony for such a day has no bolts and no opening: the
 *    door already stands ajar in a dark room.
 *  - B-14 / B-15: LATE STANDINGS ARE CONFESSED, NEVER BACKDATED.
 *    A confession is appended BESIDE the plate, carrying its own
 *    moment and the moment it speaks of, separately, forever.
 *    The struck fields never move. The plate drops whether or
 *    not it is read; reading is a mark beside it, not a change
 *    to it.
 *  - A-11 / B-16 / E-2: PLATES STACK IN THE ROOM — never among
 *    the signing's materials — as the progress display. The pile
 *    type carries NO member that sums, totals, scores, grades,
 *    or streaks: there is nothing here to add up.
 *  - D-8: THE DRAWER STORES FINISHED TASKS QUIETLY. Stored,
 *    unscored, untotalled — the entry type carries a title and
 *    when it was finished, and nothing that could judge it.
 *
 * Device behaviour is not claimed: UNPROVEN-ON-DEVICE (Audit A
 * G-7). Punch diameters are design figures (Audit A §2.6),
 * stamped UNPROVEN in the build record (U-4) — the graded ORDER
 * (reach large · held medium · floor small) is settled substance;
 * the figures are renderings.
 */
object LiftingPlate {

    // ------------------------------------------------------------------
    // Punches — graded holes a finger could learn (P1 PunchKind)
    // ------------------------------------------------------------------

    /** Punch grades, largest to smallest. The grade is the
     *  substance; a surface renders it at its design figure. */
    enum class PunchGrade { LARGE, MEDIUM, SMALL }

    /** One kind of day's mark on the plate. REACH: a reach for a
     *  locked app (the knock's testimony). HELD: a held call (an
     *  unknown number that waited). FLOOR: the floor event — the
     *  smallest mark, the day's least fact. */
    enum class PunchKind(val grade: PunchGrade) {
        REACH(PunchGrade.LARGE),
        HELD(PunchGrade.MEDIUM),
        FLOOR(PunchGrade.SMALL)
    }

    /** One punched hole: struck at its elapsed offset into the
     *  sealed span, exactly as P1's Punch carries it. */
    data class Punch(
        val kind: PunchKind,
        val atOffsetMillis: Long,
        val packageName: String = ""
    )

    // ------------------------------------------------------------------
    // The plate's truth
    // ------------------------------------------------------------------

    /** The seal's end-state as the plate tells it. LIFTED days read
     *  WHOLE; a crank-spent day still reads WHOLE here — the crank's
     *  snapped seal is its own kept fact on the plate, not the day
     *  seal's failure (P1's law, carried). */
    enum class SealTruth { WHOLE, BROKEN, AJAR }

    /**
     * What a strike would say, before it is struck. Built only via
     * [plateTruthFor], which enforces the covering law.
     *
     * [bracketStandingHour] / [bracketFallenHour] are the bracket
     * hours of a failed day (HH:MM wall figures as the checks found
     * them): the last check that found the seal standing, and the
     * first check that found it fallen. Both are required exactly
     * when [bracketGapMillis] > 0, and forbidden otherwise — a
     * whole day has no bracket to strike.
     */
    data class PlateTruth(
        val filingDateIso: String,
        val hoursHeldMillis: Long,
        val punches: List<Punch>,
        val sealTruth: SealTruth,
        val crankSealBroken: Boolean,
        val bracketGapMillis: Long = 0L,
        val bracketStandingHour: String? = null,
        val bracketFallenHour: String? = null
    ) {
        init {
            require(hoursHeldMillis >= 0L) { "held time cannot be negative" }
            require(bracketGapMillis >= 0L) { "the bracket gap cannot be negative" }
            if (bracketGapMillis > 0L) {
                require(sealTruth != SealTruth.WHOLE) { "a whole day carries no bracket gap" }
                require(!bracketStandingHour.isNullOrBlank() && !bracketFallenHour.isNullOrBlank()) {
                    "a bracket strike names both bracket hours: standing at the last check, fallen by the first check that found it"
                }
            } else {
                require(bracketStandingHour == null && bracketFallenHour == null) {
                    "bracket hours exist only on a bracket strike"
                }
            }
        }

        val isFailure: Boolean get() = sealTruth != SealTruth.WHOLE

        /** The day's holes in striking order. */
        val punchesInOrder: List<Punch> get() = punches.sortedBy { it.atOffsetMillis }

        fun punchesOf(kind: PunchKind): List<Punch> =
            punches.filter { it.kind == kind }.sortedBy { it.atOffsetMillis }
    }

    /**
     * Build the truth for a day — or NO PLATE AT ALL.
     *
     * [sealedSpanMillis] is the span the seal actually stood over
     * the day. When no seal stood (span <= 0 — the day was never
     * signed into the room, or its hours were his open hours), the
     * hold covered nothing and NOTHING IS STRUCK: this returns
     * null, and no empty plate exists anywhere downstream (B-14).
     *
     * Punches are covered only inside the held span: a punch whose
     * offset falls after the held hours is dropped here, by name —
     * it belongs to hours the hold did not cover, and the plate
     * does not chase them.
     */
    fun plateTruthFor(
        filingDateIso: String,
        sealedSpanMillis: Long,
        hoursHeldMillis: Long,
        punches: List<Punch>,
        sealTruth: SealTruth,
        crankSealBroken: Boolean,
        bracketGapMillis: Long = 0L,
        bracketStandingHour: String? = null,
        bracketFallenHour: String? = null
    ): PlateTruth? {
        if (sealedSpanMillis <= 0L) return null
        val held = hoursHeldMillis.coerceIn(0L, sealedSpanMillis)
        val covered = punches.filter { it.atOffsetMillis in 0..held }
        return PlateTruth(
            filingDateIso = filingDateIso,
            hoursHeldMillis = held,
            punches = covered,
            sealTruth = sealTruth,
            crankSealBroken = crankSealBroken,
            bracketGapMillis = bracketGapMillis,
            bracketStandingHour = bracketStandingHour,
            bracketFallenHour = bracketFallenHour
        )
    }

    // ------------------------------------------------------------------
    // The struck plate — once, and never again
    // ------------------------------------------------------------------

    /**
     * A confession appended beside a struck plate (B-14): a late
     * standing, told in the app's name at the moment it is learned.
     * [aboutMomentMillis] is the moment the standing speaks of;
     * [confessedAtMillis] is the moment of the confession itself.
     * They are different fields forever — a confession is never
     * backdated into the day it describes, and it never touches
     * the plate's struck fields.
     */
    data class PlateConfession(
        val standing: String,
        val aboutMomentMillis: Long,
        val confessedAtMillis: Long
    ) {
        init {
            require(standing.isNotBlank()) { "a confession says what it confesses" }
            require(confessedAtMillis >= aboutMomentMillis) {
                "a confession is made when it is learned, never backdated before the moment it speaks of"
            }
        }
    }

    /**
     * THE STRUCK PLATE. Immutable in its struck fields by
     * construction: the only operations are [confess] and
     * [markRead], both of which return a plate whose [truth] is
     * the identical object that was struck. There is no re-strike
     * operation on this type, and no copy() to sneak one through.
     */
    class StruckPlate internal constructor(
        val truth: PlateTruth,
        val struckAtElapsedMillis: Long,
        val confessions: List<PlateConfession> = emptyList(),
        val readAtMillis: Long? = null
    ) {
        /** Append a confession beside this plate. The plate's
         *  struck truth is returned untouched — the same instance. */
        fun confess(confession: PlateConfession): StruckPlate =
            StruckPlate(truth, struckAtElapsedMillis, confessions + confession, readAtMillis)

        /** Mark the plate read (B-15: offered nightly, owed never).
         *  Reading changes nothing struck. */
        fun markRead(atMillis: Long): StruckPlate =
            if (readAtMillis != null) this
            else StruckPlate(truth, struckAtElapsedMillis, confessions, atMillis)

        val isRead: Boolean get() = readAtMillis != null
    }

    /** The outcome of asking for a strike. */
    sealed interface StrikeOutcome {
        /** The strike landed; this plate now stands. */
        data class Struck(val plate: StruckPlate) : StrikeOutcome

        /** A plate already stands for this day. It is returned
         *  untouched — the new truth was NOT struck over it, and
         *  whatever it carries belongs in a confession, not a
         *  re-strike. */
        data class AlreadyStruck(val plate: StruckPlate) : StrikeOutcome
    }

    /**
     * THE STRUCK-ONCE GUARD (B-14). P1's store persists a strike
     * when told to; this guard is the law above the store that
     * decides whether it may be told. (Named upward in this lane's
     * opening line: if BUILD-SR-3 prefers the guard inside the
     * store, this object lifts out in one move — the law is the
     * same either way.)
     */
    object PlateStrikeGuard {
        fun strike(
            existing: StruckPlate?,
            truth: PlateTruth,
            struckAtElapsedMillis: Long
        ): StrikeOutcome {
            if (existing != null && existing.truth.filingDateIso == truth.filingDateIso) {
                return StrikeOutcome.AlreadyStruck(existing)
            }
            return StrikeOutcome.Struck(StruckPlate(truth, struckAtElapsedMillis))
        }
    }

    // ------------------------------------------------------------------
    // The handing back — first at the lift (B-13)
    // ------------------------------------------------------------------

    /**
     * One person who waited while the door stood sealed: a held
     * caller, returned at the lift to his phone's OWN list.
     * [ownListRef] is that list's own reference for the person
     * (the wiring layer's contact / call-log reference — opaque
     * here, owned by the phone's list, not by Monolith: the
     * handing back returns people to HIS list; Monolith keeps no
     * list of its own).
     */
    data class WaitingPerson(
        val displayName: String,
        val ownListRef: String
    )

    /**
     * The handing back, prepared: the waiting people in the order
     * they first waited, each person once (a caller who called
     * three times is one person handed back once). Preparation is
     * pure; DELIVERY is the wiring layer's act (P2/P9 returns them
     * to the phone's own list), and the ceremony below cannot pass
     * this point until delivery is confirmed.
     */
    data class HandingBack(val waiting: List<WaitingPerson>) {
        val isEmpty: Boolean get() = waiting.isEmpty()

        companion object {
            fun prepare(waiting: List<WaitingPerson>): HandingBack =
                HandingBack(waiting.distinctBy { it.ownListRef })
        }
    }

    // ------------------------------------------------------------------
    // The lifting ceremony — a phase machine of iron and weight
    // ------------------------------------------------------------------

    /**
     * The ceremony's phases, in their one lawful order. A whole
     * day lifts through every phase; a fallen day has no ceremony
     * at all — it stands at AJAR from the first moment (A-12).
     * There is deliberately no phase for a fade, a slide, or a
     * screen change: the door's movement is bolts, then weight.
     */
    enum class LiftPhase {
        /** The seal stands; the hour has not fired. */
        SEALED,

        /** Bolts withdraw one by one (see [LiftingScore]). */
        BOLTS_WITHDRAWING,

        /** The door opens on its own weight, on the door-weight
         *  spring — slow commitment, long settle. */
        DOOR_OPENING,

        /** The handing back: the waiting people are returned to
         *  his phone's own list. The plate does not exist for him
         *  yet — this phase completes before any plate is read. */
        HANDING_BACK,

        /** The plate, struck once at the lifting, is read. */
        PLATE,

        /** The day is open; the room stands empty behind him. */
        OPEN,

        /** A fallen day: the door ajar in a dark room. No bolts,
         *  no opening, no ceremony — the truth, and its plate. */
        AJAR
    }

    /** Events the wiring layer reports into the ceremony. */
    enum class LiftEvent {
        LIFT_FIRED, BOLT_WITHDRAWN, DOOR_SETTLED, HANDED_BACK, PLATE_READ, FAILURE_FOUND
    }

    /**
     * The ceremony's standing state. [boltsWithdrawn] counts iron,
     * 0..[LiftingScore.BOLT_COUNT]. [handingBackDelivered] is the
     * gate the plate waits behind: PLATE is reachable only when it
     * stands true.
     */
    data class CeremonyState(
        val phase: LiftPhase,
        val boltsWithdrawn: Int = 0,
        val handingBackDelivered: Boolean = false,
        val handingBack: HandingBack = HandingBack(emptyList())
    ) {
        init {
            require(boltsWithdrawn in 0..LiftingScore.BOLT_COUNT) { "the door has exactly its bolts" }
        }

        /** True only in the PLATE phase — the plate may be read
         *  nowhere else, and never before the handing back. */
        val plateReadable: Boolean get() = phase == LiftPhase.PLATE || phase == LiftPhase.AJAR

        companion object {
            /** Begin a whole day's lifting. */
            fun begin(handingBack: HandingBack): CeremonyState =
                CeremonyState(LiftPhase.SEALED, handingBack = handingBack)

            /** Begin a fallen day: no ceremony, the ajar truth. The
             *  failure plate is readable at once — it is not a
             *  reading he is offered, it is what he finds. */
            fun beginAjar(handingBack: HandingBack): CeremonyState =
                CeremonyState(
                    LiftPhase.AJAR,
                    boltsWithdrawn = 0,
                    handingBackDelivered = false,
                    handingBack = handingBack
                )
        }
    }

    /**
     * Advance the ceremony. Unlawful transitions return the state
     * unchanged — the machine has no error state to debug at the
     * lifting hour; an event that arrives out of order simply is
     * not the ceremony, and the door does not move for it.
     */
    fun advance(state: CeremonyState, event: LiftEvent): CeremonyState = when (state.phase) {
        LiftPhase.SEALED -> when (event) {
            LiftEvent.LIFT_FIRED -> state.copy(phase = LiftPhase.BOLTS_WITHDRAWING)
            LiftEvent.FAILURE_FOUND -> CeremonyState.beginAjar(state.handingBack)
            else -> state
        }
        LiftPhase.BOLTS_WITHDRAWING -> when (event) {
            LiftEvent.BOLT_WITHDRAWN -> {
                val n = state.boltsWithdrawn + 1
                if (n >= LiftingScore.BOLT_COUNT) state.copy(boltsWithdrawn = n, phase = LiftPhase.DOOR_OPENING)
                else state.copy(boltsWithdrawn = n)
            }
            else -> state
        }
        LiftPhase.DOOR_OPENING -> when (event) {
            LiftEvent.DOOR_SETTLED ->
                // The door stands open. Before anything else —
                // before the plate — the handing back. A day nobody
                // waited through delivers it empty, in the same act.
                if (state.handingBack.isEmpty) {
                    state.copy(phase = LiftPhase.PLATE, handingBackDelivered = true)
                } else {
                    state.copy(phase = LiftPhase.HANDING_BACK)
                }
            else -> state
        }
        LiftPhase.HANDING_BACK -> when (event) {
            LiftEvent.HANDED_BACK -> state.copy(phase = LiftPhase.PLATE, handingBackDelivered = true)
            else -> state
        }
        LiftPhase.PLATE -> when (event) {
            LiftEvent.PLATE_READ -> state.copy(phase = LiftPhase.OPEN)
            else -> state
        }
        LiftPhase.AJAR -> when (event) {
            // Even at the ajar door, the people who waited are
            // handed back — the failure is the seal's, not theirs —
            // and only then is the room his again.
            LiftEvent.HANDED_BACK -> state.copy(handingBackDelivered = true)
            LiftEvent.PLATE_READ ->
                if (state.handingBackDelivered || state.handingBack.isEmpty) {
                    state.copy(phase = LiftPhase.OPEN, handingBackDelivered = true)
                } else state
            else -> state
        }
        LiftPhase.OPEN -> state
    }

    // ------------------------------------------------------------------
    // The score of the lifting (motion figures, by citation)
    // ------------------------------------------------------------------

    /**
     * The lifting's motion score. The bolt figures are P0's
     * `MonolithMotion.LiftClunks`, read firsthand from BUILD-SR-1's
     * lane and mirrored here because P0 is not yet on main: bolts
     * withdraw one by one on the sealing's widening rhythm,
     * ASCENDING in weight (0.64 · 0.82 · 1.0) — the door gives its
     * iron back in the order it took it. The door's opening rides
     * P0's DoorWeightSpring (damping 0.86, stiffness 55: "soft
     * spring, heavy mass — slow commitment, long settle"). When P0
     * lands, this mirror is deleted and the surfaces read P0's
     * primitives directly; the figures must not drift apart — the
     * token test in this lane pins them to the cited values.
     */
    object LiftingScore {
        data class BoltWithdrawal(val atMs: Int, val durationMs: Int, val weight: Float)

        val BOLTS: List<BoltWithdrawal> = listOf(
            BoltWithdrawal(atMs = 0, durationMs = 120, weight = 0.64f),
            BoltWithdrawal(atMs = 190, durationMs = 150, weight = 0.82f),
            BoltWithdrawal(atMs = 420, durationMs = 190, weight = 1.0f)
        )

        val BOLT_COUNT: Int get() = BOLTS.size

        /** P0 DoorWeightSpring, by citation. */
        const val DOOR_SPRING_DAMPING: Float = 0.86f
        const val DOOR_SPRING_STIFFNESS: Float = 55f

        /** P0 BoltSpring, by citation (the withdrawal's iron). */
        const val BOLT_SPRING_DAMPING: Float = 0.92f
        const val BOLT_SPRING_STIFFNESS: Float = 900f

        /** When the last bolt's body has finished: the door may
         *  begin to move only on iron fully withdrawn. */
        val boltsCompleteAtMs: Int get() = BOLTS.maxOf { it.atMs + it.durationMs }

        /** How many bolts stand withdrawn at [elapsedMs] into the
         *  withdrawal — a bolt counts when its stroke completes,
         *  not when it starts: withdrawn is a fact, not a hope. */
        fun boltsWithdrawnAt(elapsedMs: Int): Int =
            BOLTS.count { elapsedMs >= it.atMs + it.durationMs }
    }

    // ------------------------------------------------------------------
    // The pile — plates stacking in the room (A-11 / B-16 / E-2)
    // ------------------------------------------------------------------

    /** Where the pile stands. The type carries exactly one lawful
     *  value: the room's tray side. The signing place is not a
     *  value this type can hold — the record never argues at the
     *  composing hour (B-16). */
    enum class PilePlace { ROOM_TRAY }

    /**
     * The season's plates, stacked. Browsable, in the room, away
     * from the signing. This type deliberately offers NO operation
     * that sums hours, counts a streak, averages, grades, or
     * totals anything: the pile is proof, not a score (A-11). Its
     * order is the stack's order — newest struck on top.
     */
    class PlatePile(plates: List<StruckPlate>) {
        val place: PilePlace = PilePlace.ROOM_TRAY

        /** The stack, newest first — the order a hand lifts them. */
        val platesNewestFirst: List<StruckPlate> =
            plates.sortedByDescending { it.truth.filingDateIso }

        fun plateFor(filingDateIso: String): StruckPlate? =
            platesNewestFirst.firstOrNull { it.truth.filingDateIso == filingDateIso }

        /** The pile with one more plate struck onto it. A plate
         *  for a day already in the pile is refused whole — the
         *  standing plate is kept and the newcomer dropped (the
         *  struck-once law, at pile scale). */
        fun stacked(plate: StruckPlate): PlatePile =
            if (plateFor(plate.truth.filingDateIso) != null) this
            else PlatePile(platesNewestFirst + plate)
    }

    // ------------------------------------------------------------------
    // The drawer — stored, unscored, untotalled (D-8)
    // ------------------------------------------------------------------

    /**
     * A stored finished task. Mirrors P1's DrawerEntry field for
     * field: a title and when it was finished — nothing that could
     * be summed, graded, or streaked exists in this type.
     */
    data class DrawerTask(
        val taskId: Int,
        val title: String,
        val completedAtMillis: Long
    )

    /**
     * The quiet drawer. Tasks are stored and looked back through
     * rarely; the drawer groups them by their filing day (his
     * 03:30 seam, the same seam P1's filingDateIso states) for
     * browsing and offers no total, no count-as-verdict, no
     * ordering by anything but time.
     */
    class TaskDrawer(tasks: List<DrawerTask>) {
        /** Stored tasks, most recently finished first. */
        val tasksNewestFirst: List<DrawerTask> =
            tasks.sortedByDescending { it.completedAtMillis }

        /** Browse by filing day: date → that day's stored tasks,
         *  days newest first. A browsing shape only — the map's
         *  values are never summed anywhere in this type. */
        fun byFilingDay(zone: ZoneId = ZoneId.systemDefault()): Map<String, List<DrawerTask>> =
            tasksNewestFirst
                .groupBy { filingDateIso(it.completedAtMillis, zone) }
                .toSortedMap(compareByDescending { it })

        companion object {
            /** His 03:30 filing seam, stated as P1 states it. */
            fun filingDateIso(instantMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
                java.time.Instant.ofEpochMilli(instantMillis).atZone(zone).toLocalDateTime()
                    .minusMinutes(3L * 60L + 30L).toLocalDate().toString()
        }
    }
}
