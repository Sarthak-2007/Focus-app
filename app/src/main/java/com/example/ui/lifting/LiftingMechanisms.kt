package com.example.ui.lifting

import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.CeremonyScores
import com.example.ui.foundation.DoorBody
import com.example.ui.foundation.DrivenDoor
import com.example.ui.foundation.PhysicsBody
import com.example.ui.foundation.SettleSpec
import com.example.ui.foundation.StrongroomReducedMotion
import com.example.ui.foundation.WithdrawalBodies
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * THE LIFTING'S MECHANISMS — the pure machinery of the lifting
 * (census C-17), the plate strike (C-06, a HERO) and the plate
 * pile (C-07), authored by BUILD-SR-40.
 *
 * Every figure here is read off the foundation's filed bodies
 * and scores — [CeremonyScores.Lifting] (the ceremony's single
 * clock) · [WithdrawalBodies] (the bolts freed in reverse load
 * order: strain 90 ms, then slides 275 / 221 / 183 ms) ·
 * [DrivenDoor] / [DoorBody] (the door opens BY WEIGHT: imbalance
 * torque, closer band, end-stop; the strike is fired by the
 * door's travel at θ = 81.2°, never by a timer of its own) —
 * never invented in this file, never imported from a foreign
 * spec. All felt/motion figures remain UNPROVEN-ON-DEVICE, as
 * the foundation's head stamps them.
 *
 * THE LIFTING LAW AS CODED (census C-17 · C-11 · C-06):
 *  - At the engraved hour the time-lock fires BY ITSELF
 *    (score: held-stillness ends and the lock trips at 600 ms).
 *    No hand performs it, no tap dismisses it, and this machine
 *    has no invocable entry point: a pose is a pure function of
 *    the ceremony clock, and the clock's zero is the firing.
 *  - THE HANDING BACK IS FIRST (C-11): it is an execution
 *    precondition of the lift, executed by the engine's wiring
 *    as the lift fires — never a presented phase, never a list
 *    this surface shows. The domain ceremony
 *    ([LiftingPlate.advance]) gates the plate behind it; this
 *    surface presents no plate before the strike, which stands
 *    after the precondition in the day's own order.
 *  - Bolts withdraw ONE BY ONE in reverse load order — the
 *    heaviest bolt's body first (snap weights 1.0 · 0.82 · 0.64,
 *    the mirror of the sealing's ascending clunks).
 *  - The door then opens on its own weight on [DrivenDoor];
 *    when its travel meets the stop at [DoorBody.STRIKE_DEG]
 *    THE PLATE IS STRUCK — one strike, once, never re-struck.
 *  - A fallen day has NO ceremony: its pose never exists. The
 *    failure face (C-18) is a standing form, not a phase here.
 */

// ────────────────────────────────────────────────────────────────
// The ceremony clock — poses read off the filed score + bodies
// ────────────────────────────────────────────────────────────────

/** The lifting's phases, in their one lawful order. */
enum class LiftCeremonyPhase {
    /** The held stillness before the lock trips (score 0–600). */
    STILLNESS,

    /** The time-lock has tripped; the press still seats (600–1120). */
    TRIPPED,

    /** The press unseats (1120–1440). */
    UNSEATING,

    /** Bolts withdraw one by one (1440–2680). */
    WITHDRAWING,

    /** The door opens on its own weight (2680–the strike). */
    OPENING,

    /** The door's travel has struck the plate (strike–settle). */
    STRUCK,

    /** The door rests against its stop; the day stands open. */
    SETTLED
}

/** One instant of the ceremony: everything a surface may draw,
 *  and nothing it may decide. */
data class LiftCeremonyPose(
    val elapsedMs: Int,
    val phase: LiftCeremonyPhase,
    /** Per-bolt withdrawn fraction 0..1, in withdrawal order
     *  (heaviest first). A bolt's strain phase shows as ≤ 0.08 —
     *  the drive loads against a static hold before break-free
     *  ([WithdrawalBodies.STRAIN_MS]). */
    val boltFractions: List<Float>,
    /** Bolts whose slide has completed — withdrawn is a fact,
     *  counted at completion, never at start. */
    val boltsWithdrawn: Int,
    /** The door's angle in degrees, off [DrivenDoor] (0 = shut). */
    val doorAngleDeg: Double,
    /** The plate has been struck (the door's travel met its stop). */
    val struck: Boolean,
    /** The door rests in its settle band against the stop. */
    val settled: Boolean,
    /** The struck plate's landing into the tray, 0..1 — a spring
     *  pose, integrated, never eased (see [PlateLandingBody]). */
    val plateDropFraction: Double
)

object LiftingCeremony {

    /** Score points, by name — read from [CeremonyScores.Lifting],
     *  the ceremony's single filed clock. */
    private fun scoreMs(name: String): Int =
        CeremonyScores.Lifting.first { it.name == name }.atMs

    val TripMs: Int = scoreMs("time-lock-trips")
    val UnseatMs: Int = scoreMs("unseat-begins")
    val SwingOpenMs: Int = scoreMs("swing-open-begins")
    val ScoreEndMs: Int = scoreMs("score-end")

    /** The three snaps, in withdrawal order (heaviest first). */
    val SnapMs: List<Int> = listOf(scoreMs("snap-1"), scoreMs("snap-2"), scoreMs("snap-3"))

    /** The door's own body, integrated once — poses are read off
     *  it, never re-simulated per frame. */
    private val door: DrivenDoor by lazy { DrivenDoor() }

    /** The strike moment on the ceremony clock: the swing-open
     *  start + the door body's own strike millisecond, exactly
     *  as the score files it (5,574). */
    val StrikeMs: Int = SwingOpenMs + (door.strikeMs ?: DoorBody.FiledTestimony.STRIKE_MS)

    /** The door's settle on the ceremony clock (5,926 as filed). */
    val SettledMs: Int = SwingOpenMs + (door.settledMs ?: DoorBody.FiledTestimony.SETTLED_MS)

    /** One bolt's withdrawn fraction at ceremony [elapsedMs]:
     *  strain (≤ 8% travel) for [WithdrawalBodies.STRAIN_MS]
     *  before its snap, then its filed slide. */
    fun boltFraction(boltIndex: Int, elapsedMs: Int): Float {
        val snap = SnapMs[boltIndex]
        val slide = WithdrawalBodies.FILED_SLIDE_MS[boltIndex]
        val strainStart = snap - WithdrawalBodies.STRAIN_MS
        return when {
            elapsedMs < strainStart -> 0f
            elapsedMs < snap ->
                0.08f * ((elapsedMs - strainStart).toFloat() / WithdrawalBodies.STRAIN_MS)
            elapsedMs >= snap + slide -> 1f
            else -> 0.08f + 0.92f * ((elapsedMs - snap).toFloat() / slide)
        }
    }

    /** The door's angle at ceremony [elapsedMs] — weight-driven
     *  on [DrivenDoor]; before the swing-open the door stands
     *  shut, however the bolts stand. */
    fun doorAngleAt(elapsedMs: Int, reducedMotion: Boolean = false): Double {
        if (elapsedMs < SwingOpenMs) return 0.0
        if (reducedMotion) {
            // The reduced-motion profile (foundation §5): held
            // poses, cuts between — the strike fires on the final
            // pose's cut. Who acts and whether it completed are
            // preserved; the weight is surrendered, named.
            val step = (elapsedMs - SwingOpenMs) / StrongroomReducedMotion.SWING_OPEN_HOLD_MS
            val poses = StrongroomReducedMotion.SWING_OPEN_POSES_DEG
            return poses[step.coerceIn(0, poses.lastIndex)]
        }
        return door.angleAt(elapsedMs - SwingOpenMs)
    }

    /** The strike moment under the reduced-motion profile: the
     *  final pose's cut. */
    fun strikeMs(reducedMotion: Boolean = false): Int =
        if (reducedMotion) {
            SwingOpenMs + StrongroomReducedMotion.SWING_OPEN_HOLD_MS *
                (StrongroomReducedMotion.SWING_OPEN_POSES_DEG.size - 1)
        } else StrikeMs

    /** The ceremony's pose at [elapsedMs] — the one derivation
     *  every lifting surface reads. */
    fun poseAt(elapsedMs: Int, reducedMotion: Boolean = false): LiftCeremonyPose {
        val t = elapsedMs.coerceAtLeast(0)
        val fractions = SnapMs.indices.map { boltFraction(it, t) }
        val strike = strikeMs(reducedMotion)
        val struck = t >= strike
        val settled = if (reducedMotion) struck else t >= SettledMs
        val phase = when {
            settled && struck -> LiftCeremonyPhase.SETTLED
            struck -> LiftCeremonyPhase.STRUCK
            t >= SwingOpenMs -> LiftCeremonyPhase.OPENING
            t >= SnapMs.first() -> LiftCeremonyPhase.WITHDRAWING
            t >= UnseatMs -> LiftCeremonyPhase.UNSEATING
            t >= TripMs -> LiftCeremonyPhase.TRIPPED
            else -> LiftCeremonyPhase.STILLNESS
        }
        return LiftCeremonyPose(
            elapsedMs = t,
            phase = phase,
            boltFractions = fractions,
            boltsWithdrawn = fractions.count { it >= 1f },
            doorAngleDeg = doorAngleAt(t, reducedMotion),
            struck = struck,
            settled = settled,
            plateDropFraction = if (!struck) 0.0 else PlateLanding.fractionAt(t - strike)
        )
    }
}

/**
 * THE PLATE'S LANDING — a struck plate lands with weight into
 * the pile (per the research routing: piles ARRIVE). Its body:
 * a heavy brass mass on a near-dead spring — it drops, meets
 * the pile, and settles with the smallest overshoot the mass
 * allows; it never bounces like a card. Poses are integrated
 * by the foundation's [BodyIntegrator] at its 1 ms substep.
 */
object PlateLanding {
    val Body: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 2.4, dampingRatio = 0.92, normalisedStiffness = 380.0,
        settle = SettleSpec(distanceEpsilon = 0.002, velocityEpsilon = 0.02)
    )

    /** Landing fraction 0..1 at [msAfterStrike] — 0 = the plate
     *  still at the strike's height, 1 = resting in the pile. */
    fun fractionAt(msAfterStrike: Int): Double {
        if (msAfterStrike <= 0) return 0.0
        return BodyIntegrator.valueAt(0.0, 1.0, Body, msAfterStrike / 1000.0)
            .coerceIn(0.0, 1.05)
    }
}

// ────────────────────────────────────────────────────────────────
// The struck plate — its rendering truth (C-06)
// ────────────────────────────────────────────────────────────────

/** The bracket pair's settled sentence (TX-SHOW-14): the words
 *  the gapped plate is struck with, whole, never paraphrased. */
const val BRACKET_SENTENCE: String =
    "standing at the last check, fallen by the first check that found it"

/** One punched hole as the plate renders it: its grade (a finger
 *  could learn them — ledger E-9) and its place along the plate,
 *  which is its place in the day: the offset it was struck at,
 *  against the hours the hold covered. */
data class PlatePunchView(
    val grade: LiftingPlate.PunchGrade,
    /** 0..1 along the day the hold covered. */
    val dayFraction: Float
)

/** The bracket pair (TX-SHOW-14), in its struck form: the
 *  settled sentence + the two bracket hours as figures. */
data class BracketView(
    val sentence: String,
    /** The last check that found the seal standing, HH:mm. */
    val standingHourText: String,
    /** The first check that found it fallen, HH:mm. */
    val fallenHourText: String
)

/**
 * THE STRUCK PLATE'S VIEW — everything the brass carries, and
 * nothing else (census C-06):
 *  - [hoursHeldText] — the hours the hold covered, struck in
 *    brass (TX-SHOW-10). Hours the hold did NOT cover strike
 *    nothing: they are not in this type, not chased, not graded.
 *  - [punches] — graded holes: reach large · held-call medium ·
 *    showing medium (its digits are never struck, TX-SHOW-12) ·
 *    floor small. A taking from the drum strikes NO hole.
 *  - [sealLineText] — exactly two lawful states, no third
 *    (TX-SHOW-13): "seal whole" / "seal broken".
 *  - [bracket] — a fallen day's pair (TX-SHOW-14), or null. The
 *    killed citation form ("Sealed 5:02 · fallen by 11:40",
 *    TX-DIE-637) exists nowhere in this type.
 *  - The crank's snapped seal is NOT a plate field: it is kept
 *    in the tray ([crankSealBroken] is carried only so the tray
 *    can render the kept seal from the pile's facts).
 *
 * This type has no score, no total, no grade, no streak —
 * by construction (C-06 / C-07).
 */
data class StruckPlateView(
    val filingDateIso: String,
    val hoursHeldText: String,
    val punches: List<PlatePunchView>,
    val sealLineText: String,
    val bracket: BracketView?,
    val crankSealBroken: Boolean,
    val isFailure: Boolean
) {
    companion object {
        private val HOUR_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm")

        /** The hours-held figure as struck (TX-SHOW-10):
         *  whole hours and minutes, in plain struck words —
         *  "7 h 20 min" · "7 h" · "42 min". Never a decimal,
         *  never a percentage, never a fraction of a target. */
        fun hoursHeldText(hoursHeldMillis: Long): String {
            val totalMinutes = (hoursHeldMillis / 60_000L).coerceAtLeast(0L)
            val h = totalMinutes / 60
            val m = totalMinutes % 60
            return when {
                h > 0 && m > 0 -> "$h h $m min"
                h > 0 -> "$h h"
                else -> "$m min"
            }
        }

        /**
         * The struck form of a stored plate — the mapping the
         * doorway's model deliberately does NOT make (BUILD-SR-35
         * carried the bracket pair + the hours-held figure
         * unrendered; their struck forms are minted here).
         *
         * The bracket hours are derived from the store's own
         * struck facts and nothing else: the seal stood from
         * [sealedAtWallMillis]; it was still standing at
         * + [StrongroomDay.DayPlate.hoursHeldMillis] (the last
         * check's hour) and fallen by + held + bracket gap
         * (the first check that found it). No hour is invented:
         * both are the stored lengths read against the stored
         * sealing instant, in [zone].
         */
        fun fromStruckPlate(
            plate: StrongroomDay.DayPlate,
            sealedAtWallMillis: Long,
            zone: ZoneId = ZoneId.systemDefault()
        ): StruckPlateView {
            val truth = LiftingPlate.PlateTruth(
                filingDateIso = plate.filingDateIso,
                hoursHeldMillis = plate.hoursHeldMillis,
                punches = plate.punches.map { punch ->
                    LiftingPlate.Punch(
                        kind = when (punch.kind) {
                            StrongroomDay.PunchKind.REACH -> LiftingPlate.PunchKind.REACH
                            StrongroomDay.PunchKind.HELD,
                            StrongroomDay.PunchKind.SHOWING -> LiftingPlate.PunchKind.HELD
                            StrongroomDay.PunchKind.FLOOR -> LiftingPlate.PunchKind.FLOOR
                        },
                        atOffsetMillis = punch.atOffsetMillis,
                        packageName = punch.packageName
                    )
                },
                sealTruth = when (plate.sealState) {
                    StrongroomDay.SealState.LIFTED -> LiftingPlate.SealTruth.WHOLE
                    StrongroomDay.SealState.BROKEN -> LiftingPlate.SealTruth.BROKEN
                    StrongroomDay.SealState.AJAR -> LiftingPlate.SealTruth.AJAR
                    StrongroomDay.SealState.SEALED ->
                        error("a sealed day's plate is not struck yet")
                },
                crankSealBroken = plate.crankSealBroken,
                bracketGapMillis = plate.bracketGapMillis,
                bracketStandingHour = if (plate.bracketGapMillis > 0L) {
                    HOUR_FORMAT.format(
                        Instant.ofEpochMilli(sealedAtWallMillis + plate.hoursHeldMillis)
                            .atZone(zone)
                    )
                } else null,
                bracketFallenHour = if (plate.bracketGapMillis > 0L) {
                    HOUR_FORMAT.format(
                        Instant.ofEpochMilli(
                            sealedAtWallMillis + plate.hoursHeldMillis +
                                plate.bracketGapMillis
                        ).atZone(zone)
                    )
                } else null
            )
            return fromPlateTruth(truth)
        }

        /** The view from the domain plate truth — one mapping,
         *  shared by the host (store plates) and every test. */
        fun fromPlateTruth(truth: LiftingPlate.PlateTruth): StruckPlateView {
            val held = truth.hoursHeldMillis
            return StruckPlateView(
                filingDateIso = truth.filingDateIso,
                hoursHeldText = hoursHeldText(held),
                punches = truth.punchesInOrder.map { punch ->
                    PlatePunchView(
                        grade = punch.kind.grade,
                        dayFraction = if (held > 0L) {
                            (punch.atOffsetMillis.toFloat() / held.toFloat())
                                .coerceIn(0f, 1f)
                        } else 0f
                    )
                },
                sealLineText = if (truth.sealTruth == LiftingPlate.SealTruth.WHOLE) {
                    "seal whole"
                } else {
                    "seal broken"
                },
                bracket = if (truth.bracketGapMillis > 0L &&
                    truth.bracketStandingHour != null && truth.bracketFallenHour != null
                ) {
                    BracketView(
                        sentence = BRACKET_SENTENCE,
                        standingHourText = truth.bracketStandingHour,
                        fallenHourText = truth.bracketFallenHour
                    )
                } else null,
                crankSealBroken = truth.crankSealBroken,
                isFailure = truth.isFailure
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────
// The pile — plates stacking in the tray (C-07 · C-19)
// ────────────────────────────────────────────────────────────────

/**
 * THE PILE'S VIEW — the same pile the Drawer (BUILD-SR-41)
 * keeps: its facts are the store's struck rows, rendered here
 * as brass in the tray. Two views, one truth — this type is a
 * rendering of [LiftingPlate.PlatePile]'s stack (newest struck
 * on top), and carries NO operation that sums, totals, scores,
 * grades or streaks anything: the pile is proof, not a score.
 */
data class PlatePileView(
    /** The plate struck at this lifting — the pile's top, the
     *  one that ARRIVES (lands with weight, [PlateLanding]). */
    val landing: StruckPlateView?,
    /** The plates already resting in the tray, newest first. */
    val resting: List<StruckPlateView>,
    /** Filing dates whose crank seal stands snapped, in stack
     *  order — the tray keeps those seals (C-19), rendered from
     *  this list, exactly as the domain pile states it. */
    val snappedSealDates: List<String>
) {
    companion object {
        /** The pile from struck plates, newest first (the
         *  stack's order, as the store's rows are handed over). */
        fun fromPlates(platesNewestFirst: List<StruckPlateView>): PlatePileView =
            PlatePileView(
                landing = platesNewestFirst.firstOrNull(),
                resting = platesNewestFirst.drop(1),
                snappedSealDates = platesNewestFirst
                    .filter { it.crankSealBroken }
                    .map { it.filingDateIso }
            )

        /**
         * A resting plate's honest angle in the pile, in degrees:
         * plates land by hand and weight, never squared away —
         * a small deterministic tilt per stack depth, so the
         * pile reads as metal resting, not as a list rendering.
         * Depths cycle through five tilts; deeper plates are
         * progressively stilled by the weight above them.
         */
        fun tiltDegFor(depth: Int): Float {
            val tilts = listOf(-1.6f, 1.1f, -0.7f, 1.9f, -1.2f)
            val base = tilts[((depth % tilts.size) + tilts.size) % tilts.size]
            val stilling = 1f / (1f + depth * 0.35f)
            return base * stilling
        }

        /** How many resting plates the tray draws: the pile
         *  grows all season, but a drawing shows the top of the
         *  stack — deeper plates are present as the stack's
         *  rising edge, not as individual cards. */
        const val DRAWN_RESTING_MAX: Int = 5
    }
}
