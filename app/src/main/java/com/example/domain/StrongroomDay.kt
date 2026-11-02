package com.example.domain

import java.time.LocalTime
import java.time.ZoneId

/**
 * P1 — THE STRONGROOM DAY MODEL (BUILD-SR-3, build-first-usable).
 *
 * Pure Kotlin, no Android imports, no data-layer imports — the same
 * law [FocusDomain] stands under. Every function takes plain values
 * (instants are passed in, never read here) so the whole day is
 * unit-testable on a bare JVM.
 *
 * The substance is Audit A §2 + Stage 2's signed state material,
 * carried verbatim:
 *
 *  - SIGNATURE ≠ SWING (refit Floor 1, Beat 1/2 + the Stage 2
 *    day-map, governing over Audit A §2's compression): the
 *    SIGNATURE is the evening's act (choose · set · spin the
 *    combination). The SWING — bolts driving home — is the SEAL's
 *    act at the FIRST STUDY SITTING, washed and settled. Between
 *    them the morning stands inside NO seal: rising (03:30), bath,
 *    and the meditation hour stand before the beats and this model
 *    has NO state for them — the app's absence there is their whole
 *    specification (day-map M-02/M-04/M-05). Sleep likewise stands
 *    after the beats, unsealed.
 *  - The SEAL spans the composed day from the swing and is an
 *    ELAPSED-TIME span from the sealing: the span's length is fixed
 *    ONCE at sealing and only elapsed (monotonic) time advances it —
 *    the wall clock cannot move the lifting hour after the seal. A
 *    hold made late seals what is left of the day.
 *  - Gym / sadhana (the prayer) / the first meal are SHAPED GAPS
 *    INSIDE the seal — planned at the composition, never openings,
 *    never in the locked set: they were never put into the vault,
 *    so the door never opens for them. A gap carries no package
 *    name; by type it cannot be a locked app.
 *  - The door has exactly TWO openings: the lifting hour, and the
 *    governor's crank (the week's 60-second minute). The Break is a
 *    FIRING of the time-lock set at signing — invocable by no one,
 *    never a third opening. Lawful exits reach the antechamber only
 *    and are modelled in the wiring layer, never here: this file
 *    knows no exit that opens the door.
 *  - The day-start is 03:30 (the filing seam the substrate already
 *    keeps — DrawerDay / MonolithWeek; this file states the hour,
 *    the data layer binds it to those definitions, never a second
 *    seam).
 *  - Failure truth: if the seal's boot dies (power death / reboot),
 *    the elapsed span cannot be trusted across the boot — the day
 *    reads AJAR with a bracket gap, never whole. The door tells the
 *    truth about itself; nothing is added.
 *
 * Device behaviour is not claimed here or anywhere in P1:
 * containment on a real device stands UNPROVEN-ON-DEVICE (Audit A
 * G-7). This model is the truth the engine is wired to, in code.
 */
object StrongroomDay {

    /** The day-start hour: 03:30 local, his seam (Audit A §3, Stage 2). */
    const val DAY_START_HOUR: Int = 3
    const val DAY_START_MINUTE: Int = 30

    /** The governor's crank: one 60-second minute per week. */
    const val CRANK_SECONDS: Int = 60

    /**
     * The purpose sentence, verbatim (ledger B-1 — the record of
     * every surface it governs; twenty-three words, nothing minted
     * beyond it). Carried on every signing record from Slice 1.
     */
    const val PURPOSE_SENTENCE: String =
        "What I settle while I am strong, this holds while I am not. " +
            "It reopens nothing. At night it tells me the truth."

    /**
     * One-tap break shortcuts (Audit A §2.4). Shortcuts ONLY — the
     * lawful break length is any positive number of minutes, chosen
     * freely at signing; this list is never a ceiling and no
     * validation in this file consults it.
     */
    val BREAK_PRESET_SHORTCUTS_MINUTES: List<Int> = listOf(5, 10, 15, 30)

    // ------------------------------------------------------------------
    // The Signing
    // ------------------------------------------------------------------

    /**
     * The signing record: what the strong hour settled for a filing
     * day. [sentence] null = the kept place stands (no sentence
     * chosen; the place is kept, sized, empty — Audit A G-1's shape
     * applied to the sentence slot by the concept's own "or its kept
     * place" arm). [breakFiresAtOffsetMillis] is the break firing's
     * offset into the sealed span (-1 = no break priced); the length
     * is freely chosen, priced HERE at signing, never re-priced by
     * the weak hour.
     */
    data class SigningRecord(
        val filingDateIso: String,
        val lockedPackages: Set<String>,
        val liftingTime: LocalTime,
        val sentence: String?,
        val breakLengthMinutes: Int,
        val breakFiresAtOffsetMillis: Long,
        val allowanceMinutes: Int,
        val gaps: List<ShapedGap>,
        /** The task line (ledger B-3): the reason for the block, in
         *  HIS OWN WORDS, attached to the block's sealing and spent
         *  when that sealing lifts. His words are exempt from any
         *  word-bench, wholly — this model validates nothing about
         *  them: no length, no form, no vocabulary. */
        val taskLine: String? = null,
        /** The purpose sentence as carried (ledger B-1). */
        val purposeSentence: String = PURPOSE_SENTENCE
    ) {
        init {
            require(breakLengthMinutes >= 0) { "break length cannot be negative" }
            require(allowanceMinutes >= 0) { "allowance cannot be negative" }
            require(breakFiresAtOffsetMillis >= -1L) { "break offset is -1 (none) or a span offset" }
        }

        /** The sentence as the face carries it: the words, or the
         *  kept place (null) untouched. */
        val sentenceOrKeptPlace: String? get() = sentence?.takeIf { it.isNotBlank() }
    }

    // ------------------------------------------------------------------
    // Shaped gaps — inside the seal, never openings
    // ------------------------------------------------------------------

    /**
     * The shaped gaps INSIDE a standing seal, per the governing
     * filings (refit Beat 2; day-map sealed span): gym, sadhana
     * (the prayer), the first meal. MEDITATION IS NOT HERE: the
     * morning meditation hour stands before the seal, inside no
     * seal, and no gap value may be invented for it — a meditation
     * inside the day after the swing is sadhana's shape, not this
     * enum's to mint.
     */
    enum class GapKind { GYM, SADHANA, MEAL }

    /**
     * A shaped gap inside the seal: a named shape of his day
     * (meditation, gym, sadhana, a meal) that the seal SPANS — the
     * phone stays sealed through it; it is not an opening, carries
     * no package, and can never enter the locked set. Offsets are
     * elapsed offsets into the span, like everything in the seal.
     */
    data class ShapedGap(
        val kind: GapKind,
        val startOffsetMillis: Long,
        val durationMillis: Long
    ) {
        init {
            require(startOffsetMillis >= 0L) { "gap cannot start before the seal" }
            require(durationMillis > 0L) { "a gap has a shape: positive duration" }
        }
        val endOffsetMillis: Long get() = startOffsetMillis + durationMillis
    }

    /** True when [offsetMillis] falls inside a shaped gap. Inside a
     *  gap the seal still stands — this answers "which shape of the
     *  day is this", never "is the door open". */
    fun gapAt(gaps: List<ShapedGap>, offsetMillis: Long): ShapedGap? =
        gaps.firstOrNull { offsetMillis >= it.startOffsetMillis && offsetMillis < it.endOffsetMillis }

    /** Every gap must lie inside the span and gaps never overlap:
     *  a shape outside the seal, or two shapes on one hour, is a
     *  signing error, refused at signing. */
    fun gapsAreLawful(gaps: List<ShapedGap>, spanDurationMillis: Long): Boolean {
        if (gaps.any { it.endOffsetMillis > spanDurationMillis }) return false
        val sorted = gaps.sortedBy { it.startOffsetMillis }
        return sorted.zipWithNext().all { (a, b) -> a.endOffsetMillis <= b.startOffsetMillis }
    }

    // ------------------------------------------------------------------
    // The Seal — an elapsed-time span
    // ------------------------------------------------------------------

    enum class SealState { SEALED, LIFTED, BROKEN, AJAR }

    /**
     * The sealed span. [sealedAtElapsedMillis] is a monotonic
     * (elapsedRealtime-basis) reading; [spanDurationMillis] was
     * fixed once at sealing from the honest wall at that moment;
     * [sealedBootCount] is the boot the seal was struck in. After
     * sealing, ONLY elapsed time is consulted: moving the wall clock
     * moves nothing here (the span is wall-clock-proof by
     * construction — there is no wall field left to consult).
     */
    data class SealSpan(
        val sealedAtElapsedMillis: Long,
        val sealedBootCount: Int,
        val spanDurationMillis: Long
    ) {
        init {
            require(spanDurationMillis > 0L) { "a seal spans a positive span" }
        }

        /** The elapsed reading at which the time-lock fires the bolts. */
        val liftingAtElapsedMillis: Long get() = sealedAtElapsedMillis + spanDurationMillis

        /** Elapsed time held so far, floored at 0, capped at the span. */
        fun heldMillis(nowElapsedMillis: Long): Long =
            (nowElapsedMillis - sealedAtElapsedMillis).coerceIn(0L, spanDurationMillis)

        fun remainingMillis(nowElapsedMillis: Long): Long =
            (liftingAtElapsedMillis - nowElapsedMillis).coerceAtLeast(0L)

        /** The lifting fires iff the same boot still stands and the
         *  elapsed span has run. A different boot count means the
         *  monotonic clock reset under the seal: the span cannot be
         *  trusted, the seal reads AJAR (see [stateAt]), and the
         *  lifting NEVER fires off a reset clock. */
        fun isLifted(nowElapsedMillis: Long, nowBootCount: Int): Boolean =
            nowBootCount == sealedBootCount && nowElapsedMillis >= liftingAtElapsedMillis

        /**
         * The seal's state at a reading. [crankSpent] is the week's
         * crank fact (the second opening, lawfully used, breaks the
         * crank's own seal — the DAY seal reads BROKEN only when the
         * record says the seal itself failed; the crank's broken
         * seal is carried on the plate separately, see DayPlate).
         * A boot change under a standing seal = AJAR: power death
         * in the night, the door ajar in a dark room.
         */
        fun stateAt(nowElapsedMillis: Long, nowBootCount: Int, sealFailed: Boolean): SealState = when {
            sealFailed -> SealState.BROKEN
            nowBootCount != sealedBootCount -> SealState.AJAR
            nowElapsedMillis >= liftingAtElapsedMillis -> SealState.LIFTED
            else -> SealState.SEALED
        }

        /** The unheld gap for a failed/ajar day: the part of the
         *  span that was NOT held when the seal died — struck on the
         *  plate in bracket hours. 0 while the seal stands or lifts. */
        fun bracketGapMillis(nowElapsedMillis: Long, nowBootCount: Int, sealFailed: Boolean): Long =
            if (stateAt(nowElapsedMillis, nowBootCount, sealFailed) == SealState.SEALED ||
                stateAt(nowElapsedMillis, nowBootCount, sealFailed) == SealState.LIFTED
            ) 0L else spanDurationMillis - heldMillis(nowElapsedMillis)
    }

    /**
     * Strike a span at sealing: the duration from the sealing
     * instant to the NEXT occurrence of the signing's lifting hour
     * on the wall clock AS IT STANDS AT SEALING. This is the one
     * moment the wall is consulted; the returned duration is then
     * frozen into the span and the wall is never consulted again.
     */
    fun spanDurationAtSealing(
        sealedAtWallMillis: Long,
        liftingTime: LocalTime,
        zone: ZoneId = ZoneId.systemDefault()
    ): Long {
        val sealedDateTime = java.time.Instant.ofEpochMilli(sealedAtWallMillis).atZone(zone).toLocalDateTime()
        var lift = sealedDateTime.toLocalDate().atTime(liftingTime)
        if (!lift.isAfter(sealedDateTime)) lift = lift.plusDays(1)
        val liftMillis = lift.atZone(zone).toInstant().toEpochMilli()
        return (liftMillis - sealedAtWallMillis).coerceAtLeast(1L)
    }

    // ------------------------------------------------------------------
    // The allowance purse — the revolving drum
    // ------------------------------------------------------------------

    /**
     * The day's allowance purse: [totalMinutes] of drum time priced
     * at signing (under the Guard's frozen allowance). A spend the
     * purse cannot cover is refused whole — the crank will not turn
     * past the purse; spent = hard stop ([isHardStopped]).
     */
    data class AllowancePurse(val totalMinutes: Int, val spentMinutes: Int = 0) {
        init {
            require(totalMinutes >= 0 && spentMinutes >= 0 && spentMinutes <= totalMinutes) {
                "purse facts are non-negative and spent never exceeds total"
            }
        }
        val remainingMinutes: Int get() = totalMinutes - spentMinutes
        val isHardStopped: Boolean get() = remainingMinutes <= 0

        /** The purse after a spend, or null when the spend exceeds
         *  what remains (refused whole, never partially granted). */
        fun spend(minutes: Int): AllowancePurse? {
            if (minutes <= 0) return this
            if (minutes > remainingMinutes) return null
            return copy(spentMinutes = spentMinutes + minutes)
        }
    }

    // ------------------------------------------------------------------
    // The Break — a firing priced at signing
    // ------------------------------------------------------------------

    /**
     * The break as the time-lock's firing: set at signing, invocable
     * by no one. [firesAtElapsedMillis] is an absolute elapsed
     * reading (seal start + the signing's offset); the firing is DUE
     * when elapsed time reaches it — due is a fact of the span, not
     * a button anyone presses. It is not an opening: when it fires,
     * the break runs its priced length INSIDE the seal and the seal
     * resumes; the door never opened.
     */
    data class BreakFiring(
        val firesAtElapsedMillis: Long,
        val lengthMinutes: Int
    ) {
        val endsAtElapsedMillis: Long get() = firesAtElapsedMillis + lengthMinutes * 60_000L
        fun isDue(nowElapsedMillis: Long): Boolean = nowElapsedMillis >= firesAtElapsedMillis
        fun isRunning(nowElapsedMillis: Long): Boolean =
            nowElapsedMillis >= firesAtElapsedMillis && nowElapsedMillis < endsAtElapsedMillis
    }

    /** Build the day's firing from a signing + its seal, or null
     *  when the signing priced no break. */
    fun breakFiring(signing: SigningRecord, seal: SealSpan): BreakFiring? {
        if (signing.breakFiresAtOffsetMillis < 0L || signing.breakLengthMinutes <= 0) return null
        return BreakFiring(
            firesAtElapsedMillis = seal.sealedAtElapsedMillis + signing.breakFiresAtOffsetMillis,
            lengthMinutes = signing.breakLengthMinutes
        )
    }

    // ------------------------------------------------------------------
    // The governor's crank — the week's minute
    // ------------------------------------------------------------------

    /**
     * The crank's week state: one 60-second minute per week
     * (the week is MonolithWeek's Monday-03:30 week — [weekAnchorMillis]
     * is that anchor, minted by the data layer, never recomputed
     * here). [spentSeconds] is 0 or [CRANK_SECONDS]: the minute is
     * spent whole or not at all. Spending snaps the crank's lead
     * seal: [sealBroken] is kept, visible all week, and carried onto
     * the day plate. A fresh week anchor = a fresh minute.
     */
    data class GovernorCrankWeek(
        val weekAnchorMillis: Long,
        val spentSeconds: Int = 0,
        val sealBroken: Boolean = false,
        val spentAtWallMillis: Long = 0L
    ) {
        init {
            require(spentSeconds == 0 || spentSeconds == CRANK_SECONDS) {
                "the crank minute is spent whole or not at all"
            }
            require(sealBroken == (spentSeconds == CRANK_SECONDS)) {
                "the crank's seal is broken exactly when its minute is spent"
            }
        }
        val minuteAvailable: Boolean get() = spentSeconds == 0

        /** Spend the week's minute, or null when it is already spent
         *  (there is no second minute; a stockpile of exits does not
         *  exist). */
        fun spend(atWallMillis: Long): GovernorCrankWeek? =
            if (!minuteAvailable) null
            else copy(spentSeconds = CRANK_SECONDS, sealBroken = true, spentAtWallMillis = atWallMillis)
    }

    // ------------------------------------------------------------------
    // The day plate + the drawer
    // ------------------------------------------------------------------

    /** Punch sizes a finger could learn (Audit A §2.6, design
     *  figures): reach Ø-large, held Ø-medium, floor Ø-small. */
    enum class PunchKind(val size: PunchSize) {
        REACH(PunchSize.LARGE),
        HELD(PunchSize.MEDIUM),
        FLOOR(PunchSize.SMALL)
    }

    enum class PunchSize { LARGE, MEDIUM, SMALL }

    /** One punched hole on the day plate: a reach for a locked app,
     *  a held call, a floor event — struck at its elapsed offset. */
    data class Punch(
        val kind: PunchKind,
        val atOffsetMillis: Long,
        val packageName: String = ""
    )

    /**
     * The day plate: the day's truth, struck at lifting (or at
     * failure). Hours held, the punches by graded size, the seal
     * whole or broken, the crank's snapped seal kept visible. A
     * failed day carries the bracket gap. The plate has NO score,
     * NO total, NO grade — those fields do not exist in this type,
     * by construction (Monolith is a blocker, not a tracker).
     */
    data class DayPlate(
        val filingDateIso: String,
        val hoursHeldMillis: Long,
        val punches: List<Punch>,
        val sealState: SealState,
        val crankSealBroken: Boolean,
        val bracketGapMillis: Long = 0L,
        /** The day's task line, spent at this striking (B-3): his
         *  words carried onto the plate verbatim, never edited. */
        val taskLine: String? = null
    ) {
        /** Punches in striking order, grouped by kind for the face
         *  that renders graded holes. Order within a kind is time. */
        fun punchesOf(kind: PunchKind): List<Punch> =
            punches.filter { it.kind == kind }.sortedBy { it.atOffsetMillis }
    }

    /** Strike the plate from a finished (lifted / broken / ajar) day. */
    fun strikePlate(
        signing: SigningRecord,
        seal: SealSpan,
        punches: List<Punch>,
        crank: GovernorCrankWeek,
        nowElapsedMillis: Long,
        nowBootCount: Int,
        sealFailed: Boolean
    ): DayPlate {
        val state = seal.stateAt(nowElapsedMillis, nowBootCount, sealFailed)
        return DayPlate(
            filingDateIso = signing.filingDateIso,
            hoursHeldMillis = seal.heldMillis(nowElapsedMillis),
            punches = punches.sortedBy { it.atOffsetMillis },
            sealState = state,
            crankSealBroken = crank.sealBroken,
            bracketGapMillis = seal.bracketGapMillis(nowElapsedMillis, nowBootCount, sealFailed),
            taskLine = signing.taskLine
        )
    }

    /**
     * A stored finished task, for the drawer. Stored, unscored,
     * untotalled: the type carries a title and when it was finished
     * — nothing that could be summed, graded, or streaked exists
     * here.
     */
    data class DrawerEntry(
        val taskId: Int,
        val title: String,
        val completedAtMillis: Long
    )

    /** The filing date (his 03:30 seam) of an instant, stated once
     *  here for the model's callers; the data layer's DrawerDay is
     *  the same seam and the store binds to it. */
    fun filingDateIso(instantMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        java.time.Instant.ofEpochMilli(instantMillis).atZone(zone).toLocalDateTime()
            .minusMinutes(DAY_START_HOUR * 60L + DAY_START_MINUTE).toLocalDate().toString()

    /** The next day-start (03:30 local) strictly after [fromMillis]. */
    fun nextDayStartMillis(fromMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
        val dateTime = java.time.Instant.ofEpochMilli(fromMillis).atZone(zone).toLocalDateTime()
        var start = dateTime.toLocalDate().atTime(DAY_START_HOUR, DAY_START_MINUTE)
        if (!start.isAfter(dateTime)) start = start.plusDays(1)
        return start.atZone(zone).toInstant().toEpochMilli()
    }

    /** The day-start (03:30 local) of the filing day containing
     *  [instantMillis] — the instant a pending Guard change priced
     *  "from the next day-start" is measured against. */
    fun dayStartMillis(instantMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
        val filing = java.time.Instant.ofEpochMilli(instantMillis).atZone(zone).toLocalDateTime()
            .minusMinutes(DAY_START_HOUR * 60L + DAY_START_MINUTE).toLocalDate()
        return filing.atTime(DAY_START_HOUR, DAY_START_MINUTE).atZone(zone).toInstant().toEpochMilli()
    }
}
