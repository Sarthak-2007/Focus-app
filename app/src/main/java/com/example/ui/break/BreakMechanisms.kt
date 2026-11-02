package com.example.ui.`break`

import com.example.domain.StrongroomDay
import com.example.ui.antechamber.RevolvingDrum
import com.example.ui.foundation.BoltBodies
import com.example.ui.foundation.BodyIntegrator
import com.example.ui.foundation.PhysicsBody
import com.example.ui.foundation.SettleSpec
import com.example.ui.foundation.SyncPoint
import kotlin.math.floor

/**
 * THE BREAK'S MECHANISMS — the pure half of BUILD-SR-39's slice
 * (census C-16 THE BREAK, INVENTORY-SR-1, derived firsthand; the
 * Stage 4 State Rows' X1 block, T2-X1, read whole).
 *
 * THE LAW THIS FILE EXECUTES, by source:
 *
 *  - C-16 / ledger B-12: the Break is A FIRING OF THE TIME-LOCK
 *    SET AT SIGNING, invocable by NO ONE. It cannot be summoned,
 *    hastened, lengthened, shortened or taken early from inside
 *    the day. At its composed hour the door eases on clockwork,
 *    not on his hand; when its span is spent the bolts re-drive
 *    themselves and the seal stands whole again exactly as
 *    struck. It is NOT a third opening of the Door (the Door has
 *    exactly two: the lifting hour and the governor's crank,
 *    ledger A-14). Its length was freely chosen at the signing
 *    (ledger D-6); the seal prices the actual chosen length.
 *  - T2-X1-FACE (Stage 4): what the break shows him is the
 *    antechamber and the ordinary relief of a composed pause —
 *    NOT the open room, NOT the normal app whole; a break shows
 *    only what its composition put within reach. This is the
 *    slice's GAP-COORD-08 ruling, made inline as charged, in the
 *    narrowest buildable form: THE BREAK'S INTERIOR IS THE
 *    ANTECHAMBER'S HELD STATE — the allowed things as signed
 *    standing within reach, the held pile still held behind its
 *    step, contacts still ringing through natively (the room
 *    stages nothing of a call), the locked things still locked.
 *    The ruling and its ground are flagged on the slice's PR.
 *  - T2-X1-INP: invocation by hand REFUSED; shortening or
 *    lengthening mid-span REFUSED; the seal's other terms HELD
 *    untouched around the opening the setting made. In this file
 *    that law is structural: [BreakClock] is a READ-ONLY
 *    derivation over the engine's standing facts — it has no
 *    verb, no setter, no extension — and the surface's action
 *    type (Break.kt) carries no start / stop / extend act.
 *  - T2-X1-OUT: the setting re-seals BY ITSELF — no act of his,
 *    no re-swing, no ceremony of his. The re-drive below is the
 *    machine's own score, played by the clock, not by a hand.
 *
 * THE MECHANISM THAT CARRIES THE SPAN — THE ESCAPEMENT (this
 * slice's invention inside the record, as charged): the
 * time-lock's own escape wheel, standing in a brass-rimmed case
 * window in the eased leaf. The wheel makes ONE revolution per
 * break span. Its teeth ahead of the pallet — the span not yet
 * run — stand in gold; the teeth that have passed stand in
 * graphite. The remaining break is therefore an ARC, readable
 * at a glance, at any distance, in any light: no digit, no
 * countdown, no timer text of any kind exists in this slice
 * (census C-16: no label, offer, countdown or summons word for
 * the Break exists in the record, and none is minted here).
 *
 * THE ESCAPEMENT IS NOT THE CRANK, AND NOT THE DRUM. The
 * governor's crank (C-04) is a hand-wound handle on the
 * antechamber's wall under a lead crimp seal — his hand, the
 * week's emergency minute, a door OPENING. The revolving drum
 * (C-09) is the antechamber's purse-metered stock turner. The
 * escapement is neither: no hand touches it, it opens nothing,
 * it spends nothing — it is the clock's own wheel, turning
 * because the setting fired, and the break surface contains no
 * crank and no drum in any form. Any merger of the escapement
 * with the crank is a named collision with decided law (both
 * foreign documents made this error; it is barred).
 *
 * THE BOLTS' STATE, AS DECIDED: the break fires INSIDE the
 * seal. The door EASES on its cam — at most [DoorEase.MAX_EASE]
 * of its travel, a crack of lamplight, never a swing — and the
 * bolts ease off their seats by at most
 * [BoltEase.MAX_EASE_FRACTION] of their travel. They never
 * withdraw: the seal stands through the whole span, and at its
 * end the bolts RE-DRIVE the eased fraction home on this
 * slice's own score ([BreakRedriveScore]) — three descending
 * clunks in the Seal's own weights, the press settling last —
 * and the seal stands whole again exactly as struck, with
 * nothing renegotiated.
 *
 * All felt/motion figures in this file are this slice's filed
 * figures, UNPROVEN-ON-DEVICE, as the foundation stamps its own.
 */

// ────────────────────────────────────────────────────────────────
// THE BREAK CLOCK — a read-only derivation over the firing.
// ────────────────────────────────────────────────────────────────

/**
 * The break's span as the engine stands it, anchored to the
 * monotonic clock. [totalMillis] is the priced length (the
 * length in force — freely chosen at signing, D-6);
 * [remainingAtAnchorMillis] is the engine's remaining figure at
 * the anchor instant; [anchorElapsedMillis] is that instant on
 * the elapsed-realtime basis — the same basis the seal's span
 * runs on (the wall clock moves nothing in this room).
 *
 * The type is a pure value with NO verb: nothing in this slice
 * can start, stop, extend or shorten a break, because no
 * function here mutates one. The host re-derives a fresh clock
 * from the engine's facts on every engine tick; the surface
 * only interpolates between anchors.
 */
data class BreakClock(
    val totalMillis: Long,
    val remainingAtAnchorMillis: Long,
    val anchorElapsedMillis: Long
) {
    init {
        require(totalMillis > 0L) { "a break spans a positive length" }
        require(remainingAtAnchorMillis in 0..totalMillis) {
            "remaining is a part of the span, never more than it"
        }
    }

    /** The remaining span at a monotonic reading, floored at 0:
     *  the clock's own arithmetic — elapsed time is the only
     *  thing that spends a break. */
    fun remainingAt(nowElapsedMillis: Long): Long =
        (remainingAtAnchorMillis - (nowElapsedMillis - anchorElapsedMillis))
            .coerceIn(0L, totalMillis)

    /** The elapsed part of the span at a reading. */
    fun elapsedAt(nowElapsedMillis: Long): Long =
        totalMillis - remainingAt(nowElapsedMillis)

    /** The span's remaining fraction at a reading, 1 → 0. */
    fun fractionRemainingAt(nowElapsedMillis: Long): Double =
        remainingAt(nowElapsedMillis).toDouble() / totalMillis.toDouble()

    /** The span's elapsed fraction at a reading, 0 → 1. */
    fun fractionElapsedAt(nowElapsedMillis: Long): Double =
        1.0 - fractionRemainingAt(nowElapsedMillis)

    /** True while the span runs at a reading. At 0 remaining the
     *  break is spent and the seal stands whole — the surface's
     *  work is done; the shell's own derivation moves the place. */
    fun isRunningAt(nowElapsedMillis: Long): Boolean =
        remainingAt(nowElapsedMillis) > 0L

    companion object {

        /**
         * From the Strongroom day's own firing (domain
         * [StrongroomDay.BreakFiring] — the signing's offset +
         * priced length against the seal's elapsed span): the
         * break clock when the time-lock's setting is the
         * standing fact. Null when the firing is not running at
         * the reading (not yet due, or spent) — an unfired or
         * spent firing derives NO clock and NO surface.
         */
        fun fromFiring(
            firing: StrongroomDay.BreakFiring,
            nowElapsedMillis: Long
        ): BreakClock? {
            if (!firing.isRunning(nowElapsedMillis)) return null
            return BreakClock(
                totalMillis = firing.lengthMinutes * 60_000L,
                remainingAtAnchorMillis = firing.endsAtElapsedMillis - nowElapsedMillis,
                anchorElapsedMillis = nowElapsedMillis
            )
        }

        /**
         * From the kept engine session's standing break facts
         * (the session the bridge struck: its length in force +
         * its remaining seconds, as the repository's tick stands
         * them). This is the same firing in the engine's own
         * keeping — the shell's BREAK place is derived from it —
         * read here, never written.
         */
        fun fromSession(
            totalSeconds: Int,
            remainingSeconds: Int,
            nowElapsedMillis: Long
        ): BreakClock? {
            if (totalSeconds <= 0 || remainingSeconds <= 0) return null
            return BreakClock(
                totalMillis = totalSeconds * 1000L,
                remainingAtAnchorMillis = remainingSeconds * 1000L,
                anchorElapsedMillis = nowElapsedMillis
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────
// THE PHASES — the firing's own shape, in the clock's terms.
// ────────────────────────────────────────────────────────────────

/** The break's phases. There is no phase a hand can enter or
 *  leave early: the phase is a function of the clock alone. */
enum class BreakPhase {
    /** The setting fires: the door eases on its cam, the bolts
     *  ease off their seats, the escapement takes up its first
     *  tooth. The approach is the mechanism's, legible in it. */
    EASING,

    /** The span at rest: the room held, the wheel turning tooth
     *  by tooth, the seal standing around the easing. */
    RUNNING,

    /** The span's last movement: the ease comes off, the bolts
     *  re-drive home on the score, the press settles — the seal
     *  stands whole again exactly as struck. */
    REDRIVING,

    /** The span is spent. The break no longer stands; what
     *  stands is the sealed face, by the shell's derivation. */
    SPENT
}

object BreakPhases {

    /** The ease's filed span: the door takes this long to come
     *  onto its cam at the firing (and its mirror closes the
     *  re-drive's front). */
    const val EASE_MS: Long = 1_800L

    /** The re-drive's filed span: the score's whole length
     *  ([BreakRedriveScore.EndMs]) plus its settle tail. */
    val REDRIVE_MS: Long get() = BreakRedriveScore.EndMs.toLong() + 500L

    /**
     * The phase windows for a span of [totalMillis]: each window
     * is its filed length, compressed to a quarter of the span
     * when the span is short — a freely chosen 1-minute break
     * (D-6: any length is lawful) still eases and still re-drives,
     * in proportion, never skipped and never overlapping.
     * Returns (easeWindow, redriveWindow).
     */
    fun windows(totalMillis: Long): Pair<Long, Long> {
        val quarter = totalMillis / 4L
        return minOf(EASE_MS, quarter) to minOf(REDRIVE_MS, quarter)
    }

    /** The phase at a reading, from the clock alone. */
    fun phaseAt(clock: BreakClock, nowElapsedMillis: Long): BreakPhase {
        val remaining = clock.remainingAt(nowElapsedMillis)
        if (remaining <= 0L) return BreakPhase.SPENT
        val (easeWindow, redriveWindow) = windows(clock.totalMillis)
        return when {
            remaining <= redriveWindow -> BreakPhase.REDRIVING
            clock.elapsedAt(nowElapsedMillis) < easeWindow -> BreakPhase.EASING
            else -> BreakPhase.RUNNING
        }
    }

    /** Milliseconds into the re-drive at a reading (0 outside
     *  it): the score's own clock. */
    fun redriveElapsedMs(clock: BreakClock, nowElapsedMillis: Long): Long {
        val (_, redriveWindow) = windows(clock.totalMillis)
        val remaining = clock.remainingAt(nowElapsedMillis)
        if (remaining <= 0L) return redriveWindow
        if (remaining > redriveWindow) return 0L
        return redriveWindow - remaining
    }
}

// ────────────────────────────────────────────────────────────────
// THE ESCAPEMENT — the time-lock's wheel, carrying the span.
// ────────────────────────────────────────────────────────────────

object Escapement {

    /** The wheel's teeth: one revolution per break span, one
     *  tooth per equal part of it. 48 teeth — a clockmaker's
     *  count, fine enough that the gold arc reads as a span,
     *  coarse enough that each tooth's passing is a felt event. */
    const val TEETH: Int = 48

    /** The recoil's overshoot, in tooth-units: as a tooth is
     *  released the wheel runs ahead by this much, then recoils
     *  back onto the pallet — the escapement's signature motion,
     *  advance-and-recoil, never a smooth sweep. A timer that
     *  sweeps is a screen; a wheel that recoils is a clock. */
    const val RECOIL_OVERSHOOT: Double = 0.14

    /** Where in a tooth's interval the release lands (fraction):
     *  the advance is quick, the recoil settles it, the rest of
     *  the interval the wheel stands locked on the pallet. */
    private const val RELEASE_END: Double = 0.22
    private const val RECOIL_END: Double = 0.55

    /**
     * The wheel's turned angle (degrees, 0..360) at an elapsed
     * fraction of the span. Motion is in physics vocabulary
     * only: the angle is a STEP function of whole teeth with a
     * recoil curve inside each step — between releases the wheel
     * does not move at all (it is locked on the pallet), so the
     * arc's edge advances in distinct, countable events. The
     * angle never runs backwards past a settled tooth: the
     * recoil returns TO the tooth, never behind it.
     */
    fun wheelAngleDeg(fractionElapsed: Double): Double {
        val p = fractionElapsed.coerceIn(0.0, 1.0)
        if (p >= 1.0) return 360.0
        val toothFloat = p * TEETH
        val whole = floor(toothFloat)
        val within = toothFloat - whole
        val step = when {
            within < RELEASE_END -> {
                // The release: the wheel runs ahead, overshooting
                // the tooth it is landing on.
                val t = within / RELEASE_END
                t * (1.0 + RECOIL_OVERSHOOT)
            }
            within < RECOIL_END -> {
                // The recoil: the overshoot is given back and the
                // wheel settles onto the pallet at the whole tooth.
                val t = (within - RELEASE_END) / (RECOIL_END - RELEASE_END)
                (1.0 + RECOIL_OVERSHOOT) - t * RECOIL_OVERSHOOT
            }
            else -> 1.0 // Locked on the pallet until the next release.
        }
        return (whole + step) / TEETH * 360.0
    }

    /** The teeth that have wholly passed at an elapsed fraction —
     *  the graphite count; the gold count is TEETH − this. */
    fun teethPassed(fractionElapsed: Double): Int =
        floor(fractionElapsed.coerceIn(0.0, 1.0) * TEETH).toInt().coerceIn(0, TEETH)

    /** The remaining span as an arc, in degrees: the gold arc
     *  ahead of the pallet. At a glance this IS the remaining
     *  break — no figure anywhere in the slice carries it. */
    fun remainingArcDeg(fractionRemaining: Double): Double =
        fractionRemaining.coerceIn(0.0, 1.0) * 360.0

    /** The pallet fork's angle (degrees, ±[PALLET_SWING_DEG]):
     *  the fork rocks as each tooth passes — one rock per tooth,
     *  snapped at the release, eased through the recoil, still
     *  while the wheel is locked. The fork is the wheel's
     *  witness: it moves only when a tooth moves. */
    const val PALLET_SWING_DEG: Double = 9.0

    fun palletAngleDeg(fractionElapsed: Double): Double {
        val p = fractionElapsed.coerceIn(0.0, 1.0)
        val toothFloat = p * TEETH
        val whole = floor(toothFloat).toInt()
        val within = toothFloat - whole
        val side = if (whole % 2 == 0) 1.0 else -1.0
        val t = when {
            within < RELEASE_END -> within / RELEASE_END
            within < RECOIL_END -> 1.0
            else -> 1.0
        }
        // The fork throws to the incoming tooth's side across the
        // release and holds it through the lock.
        return side * PALLET_SWING_DEG * (2.0 * t - 1.0) * -1.0
    }
}

// ────────────────────────────────────────────────────────────────
// THE DOOR'S EASE — the firing's opening movement, on its cam.
// ────────────────────────────────────────────────────────────────

object DoorEase {

    /** The ease's whole travel, in degrees of leaf: the door
     *  EASES — a crack, never a swing. (The swing is 82°; the
     *  lifting opens on the door's own weight; the break's ease
     *  is neither — it is the setting letting the leaf breathe
     *  against its frame while the seal stands.) */
    const val MAX_EASE_DEG: Double = 7.0

    /**
     * The ease body: the leaf on its cam, critically damped —
     * mass with no overshoot, because a door that bounced on its
     * ease would be a door playing. Filed figures: mass 2.6 ·
     * ζ 1.0 · normalised stiffness 900 (read against the
     * foundation's body vocabulary; this slice's body, its own
     * file). UNPROVEN-ON-DEVICE.
     */
    val Body: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 2.6, dampingRatio = 1.0, normalisedStiffness = 900.0,
        settle = SettleSpec(distanceEpsilon = 0.001, velocityEpsilon = 0.01)
    )

    /**
     * The ease fraction (0..1 of [MAX_EASE_DEG]) at a reading:
     * rising across the EASING window on the ease body, standing
     * at 1 through the span, and coming off across the
     * re-drive's front on the same body reversed — at SPENT the
     * ease is 0 and the leaf stands exactly as struck.
     */
    fun easeFraction(clock: BreakClock, nowElapsedMillis: Long): Double {
        val (easeWindow, redriveWindow) = BreakPhases.windows(clock.totalMillis)
        val remaining = clock.remainingAt(nowElapsedMillis)
        if (remaining <= 0L) return 0.0
        val elapsed = clock.elapsedAt(nowElapsedMillis)
        return when {
            elapsed < easeWindow -> BodyIntegrator.valueAt(
                0.0, 1.0, Body, elapsed / 1000.0
            ).coerceIn(0.0, 1.0)
            remaining <= redriveWindow -> {
                val intoRedrive = redriveWindow - remaining
                1.0 - BodyIntegrator.valueAt(
                    0.0, 1.0, Body, intoRedrive / 1000.0
                ).coerceIn(0.0, 1.0)
            }
            else -> 1.0
        }
    }

    /** The ease in degrees at a reading. */
    fun easeDeg(clock: BreakClock, nowElapsedMillis: Long): Double =
        easeFraction(clock, nowElapsedMillis) * MAX_EASE_DEG
}

// ────────────────────────────────────────────────────────────────
// THE BOLTS' EASE + THE RE-DRIVE — the seal stands; at the end
// it stands whole again, by the machine's own hand.
// ────────────────────────────────────────────────────────────────

/**
 * THE RE-DRIVE SCORE — this slice's ceremony clock for the
 * break's end (the foundation's [SyncPoint] form: sound, haptic
 * and motion sync on one monotonic clock; the Stage 13/14
 * passes ride these points later). The clunks carry the Seal
 * score's own weights in its own order (0.64 / 0.82 / 1.0 — the
 * last bolt the heaviest fact): the re-drive is the swing's
 * closing act played by the clock instead of the hand, and it
 * weighs the same.
 */
object BreakRedriveScore {

    val Points: List<SyncPoint> = listOf(
        SyncPoint("redrive-begins", 0, 0.0),
        SyncPoint("clunk-1", 300, 0.64),
        SyncPoint("clunk-2", 900, 0.82),
        SyncPoint("clunk-3", 1700, 1.0),
        SyncPoint("press-settled-silence", 2340, 0.0),
        SyncPoint("score-end", 2600, 0.0)
    )

    val EndMs: Int = Points.last().atMs

    private fun scoreMs(name: String): Int = Points.first { it.name == name }.atMs

    /** Each bolt's drive start (its clunk minus its own travel,
     *  read off [BoltBodies] — the travel gradient is caused by
     *  the mass gradient, exactly as at the swing). */
    val BoltDriveStartMs: List<Int> = listOf("clunk-1", "clunk-2", "clunk-3")
        .mapIndexed { i, name -> scoreMs(name) - BoltBodies.travelMs(BoltBodies.MASSES[i]) }

    val ClunkWeights: List<Double> =
        listOf("clunk-1", "clunk-2", "clunk-3").map { name ->
            Points.first { it.name == name }.weight
        }
}

object BoltEase {

    /** The bolts' ease, as a fraction of their full travel: the
     *  setting's cam lets each bolt breathe this far off its seat
     *  and NO further. The bolts never withdraw during a break —
     *  a withdrawn bolt is an opening, and the break is not one.
     *  0.22 of travel: seated enough to hold, eased enough to
     *  read as the firing's act. */
    const val MAX_EASE_FRACTION: Double = 0.22

    /** One bolt's eased fraction at a reading, outside the
     *  re-drive: the door's ease, scaled to the bolt's share. */
    fun easedFraction(clock: BreakClock, nowElapsedMillis: Long): Double =
        DoorEase.easeFraction(clock, nowElapsedMillis) * MAX_EASE_FRACTION

    /**
     * One bolt's HOME fraction at a re-drive reading (0 = fully
     * eased off its seat by the break's share, 1 = driven home):
     * during REDRIVING each bolt re-drives its eased share on
     * its score point, travelling on its own body (the
     * foundation's DRIVE / RESISTANCE / its mass — the same
     * integration the swing's bolts ride). Before its drive
     * starts it stands at its eased fraction; at its clunk it
     * stands home. Outside the re-drive this returns the
     * standing ease state expressed as home-ness
     * (1 − easedFraction).
     */
    fun homeFraction(
        boltIndex: Int,
        clock: BreakClock,
        nowElapsedMillis: Long
    ): Double {
        if (BreakPhases.phaseAt(clock, nowElapsedMillis) != BreakPhase.REDRIVING) {
            return 1.0 - easedFraction(clock, nowElapsedMillis)
        }
        val into = BreakPhases.redriveElapsedMs(clock, nowElapsedMillis).toInt()
        val start = BreakRedriveScore.BoltDriveStartMs[boltIndex]
        val elapsed = into - start
        if (elapsed <= 0) return 1.0 - MAX_EASE_FRACTION
        val mass = BoltBodies.MASSES[boltIndex]
        var x = 0.0
        var v = 0.0
        val h = BodyIntegrator.SUBSTEP_SECONDS
        var ms = 0
        while (ms < elapsed) {
            val a = (BoltBodies.DRIVE - BoltBodies.RESISTANCE * v) / mass
            v += a * h
            x += v * h
            ms++
            if (x >= 1.0) { x = 1.0; break }
        }
        // x is the re-drive's travel toward home, over the eased
        // share: home-ness rises from (1 − MAX) to 1 across it.
        return (1.0 - MAX_EASE_FRACTION) + x.coerceIn(0.0, 1.0) * MAX_EASE_FRACTION
    }

    /** True when bolt [boltIndex] has seated in the re-drive
     *  (its clunk's moment has passed). */
    fun boltSeatedInRedrive(boltIndex: Int, redriveElapsedMs: Int): Boolean =
        redriveElapsedMs >= BreakRedriveScore.Points
            .first { it.name == "clunk-${boltIndex + 1}" }.atMs
}

// ────────────────────────────────────────────────────────────────
// THE INTERIOR — the antechamber's held state, as ruled.
// ────────────────────────────────────────────────────────────────

object BreakInterior {

    /**
     * The things within reach during the break: the signing's
     * allowed set, in signed order, restricted exactly as the
     * drum's stock is restricted (route-book Law R-1 — a name
     * signed into both sets resolves toward the seal). During
     * the break they are not METERED — the break is a composed
     * pause, priced whole at signing, not a purse act: the
     * drum's minute belongs to the sealed hours' takings, and
     * no spend exists in this slice. What the composition did
     * not put within reach is not here (B-9(5), carried into
     * the break by T2-X1-FACE).
     */
    fun withinReach(signing: StrongroomDay.SigningRecord): List<String> =
        RevolvingDrum.stock(signing)

    /** The break's sections, in the antechamber's own order and
     *  no other (B-9 carried into the held state): calling out
     *  stands first in the room's law — natively, staged by
     *  nothing here — then the things within reach, then the
     *  held pile behind its step. The surface renders exactly
     *  these, in this order. */
    enum class Section { WITHIN_REACH, HELD_PILE }

    val SECTION_ORDER: List<Section> = listOf(Section.WITHIN_REACH, Section.HELD_PILE)
}
