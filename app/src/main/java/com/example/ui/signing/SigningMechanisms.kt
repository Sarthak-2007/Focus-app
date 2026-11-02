package com.example.ui.signing

import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.ui.foundation.StrongroomMaximum
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * THE SIGNING'S MECHANISMS — the pure machinery of the evening act
 * (census C-02 THE SIGNING, a HERO), authored by BUILD-SR-36.
 *
 * Every figure here is read off the foundation's filed maximum
 * toolkit ([StrongroomMaximum]) or the kept domain law
 * ([StrongroomDay] · [StrongroomComposition] · [StrongroomGuard]) —
 * no figure is invented in this file, and none is imported from a
 * foreign spec (intake §3: invented figures as spec are NOT
 * APPLIED). All felt figures remain UNPROVEN-ON-DEVICE, as the
 * foundation's head stamps them.
 *
 * The combination is NEVER a value in any state this file exposes:
 * the spin mechanism counts seats and passes and reports only
 * completion. His combination is spun by his hand alone and is
 * written nowhere — not in a field, not in a log, not in the slip
 * (ledger A-2). That absence is structural, not a comment's promise:
 * there is no property anywhere in this file that could hold it.
 */

// ────────────────────────────────────────────────────────────────
// THE SIGNING DIAL — the lifting hour, designed fresh in-app
// (ledger E-11: the ring + hour as drawn were rejected twice; the
// render species is not ported). The vernier aperture at the index
// is signing-craft INPUT from the Gemini intake (intake §3, routed
// at gemini-apply-routing.md): a magnified readout under the
// hairline so the thumb never blocks the number — judged in-build
// here as mechanism, not adopted as spec (its 2.2x figure is NOT
// applied; the aperture's magnification is a drawing act in the
// surface file, and no number from that file stands in this one).
// ────────────────────────────────────────────────────────────────

object SigningDial {

    /** Minutes in the dial's full circle: the 12-hour face. */
    const val CIRCLE_MINUTES: Int = 12 * 60

    /** The hour train's own rail (foundation): quarters at
     *  amplitude 55, whole hours at 85, silence between. The dial
     *  is caught by exactly these detents and no others. */
    val Train = StrongroomMaximum.hourTrain()

    /** Angle for a minute-of-day, on the 12-hour face. */
    fun angleFor(minuteOfDay: Int): Double {
        val m = ((minuteOfDay % CIRCLE_MINUTES) + CIRCLE_MINUTES) % CIRCLE_MINUTES
        return m.toDouble() / CIRCLE_MINUTES * 360.0
    }

    /** Minute-of-day for a dial angle (the hand's raw reading,
     *  before the rail catches it). */
    fun minuteFor(angleDeg: Double): Int {
        val a = ((angleDeg % 360.0) + 360.0) % 360.0
        return (a / 360.0 * CIRCLE_MINUTES).roundToInt() % CIRCLE_MINUTES
    }

    /**
     * The rail's catch, in the foundation's own law: on release
     * within ±[StrongroomMaximum.HOUR_SNAP_WINDOW_MINUTES] of a
     * quarter, the setter snaps to the quarter and the detent
     * fires; a release elsewhere places the hour with no event.
     * Returns the placed minute-of-day and whether a detent fired.
     */
    fun settle(rawMinuteOfDay: Int, pm: Boolean): Pair<Int, Boolean> {
        val day = ((rawMinuteOfDay % CIRCLE_MINUTES) + CIRCLE_MINUTES) % CIRCLE_MINUTES +
            if (pm) CIRCLE_MINUTES else 0
        val nearest = Train.rail.nearest(day.toDouble())
        return if (abs(day - nearest) <= StrongroomMaximum.HOUR_SNAP_WINDOW_MINUTES) {
            nearest.toInt() to true
        } else {
            day to false
        }
    }

    /**
     * THE VERNIER APERTURE's reading: under the hairline, the main
     * scale reads to its quarter detent and the vernier scale reads
     * the remainder — the exact minute is the sum, read where the
     * thumb is not. [mainDetentMinute] is the quarter the hairline
     * has passed; [vernierMinute] is the offset from it, 0..14.
     */
    data class VernierReading(val mainDetentMinute: Int, val vernierMinute: Int) {
        val exactMinute: Int get() = mainDetentMinute + vernierMinute
    }

    fun vernierFor(minuteOfDay: Int): VernierReading {
        val m = ((minuteOfDay % (24 * 60)) + 24 * 60) % (24 * 60)
        val detent = Train.rail.nearest(
            // The vernier reads the detent AT OR BELOW the hairline:
            // project a half-window back so `nearest` lands on the
            // passed detent, never the approaching one.
            m.toDouble() - StrongroomMaximum.HOUR_SNAP_WINDOW_MINUTES / 2.0 - 0.5
        ).toInt()
        val base = if (detent > m) detent - 15 else detent
        return VernierReading(mainDetentMinute = base, vernierMinute = m - base)
    }

    fun timeFor(minuteOfDay: Int): LocalTime =
        LocalTime.of((minuteOfDay / 60) % 24, minuteOfDay % 60)

    fun minuteOfDay(time: LocalTime): Int = time.hour * 60 + time.minute
}

// ────────────────────────────────────────────────────────────────
// THE COMBINATION SPIN + THE GREAT WHEEL'S TURNOVER
// (census C-02's signature · ledger A-16: the signing's spin is
// worked through the great wheel). Foundation figures: ten
// stations at 36° (SPIN_STATION_SPACING_DEG), governed ceiling
// 240°/s, seat only at or below 120°/s over the last 15° of
// approach, station amplitudes thickening by pass 70 → 95 → 120.
// A classic combination's discipline: three seats, directions
// alternating, the hand must ARRIVE — a flung pass seats nothing.
// ────────────────────────────────────────────────────────────────

class CombinationSpin {

    /** Seats taken so far, 0..3. The digits themselves are not
     *  kept: a seat records THAT the hand arrived at a station in
     *  the pass's lawful direction — never which station. */
    var seatsTaken: Int = 0
        private set

    /** The current pass, 1..3 (0 before the first movement). */
    val pass: Int get() = (seatsTaken + 1).coerceAtMost(3)

    /** The lawful direction of the current pass: seats alternate
     *  clockwise, counter-clockwise, clockwise. */
    val passClockwise: Boolean get() = pass % 2 == 1

    val isComplete: Boolean get() = seatsTaken >= 3

    /** The great wheel's turnover, geared to the spin: one full
     *  turnover (360°) across the three seats — the wheel turns
     *  120° per seat taken, and stands turned-over only when the
     *  signature is whole (ledger A-16). */
    val wheelTurnoverDeg: Double get() = seatsTaken * 120.0

    /** The station under the hand, 0..9, from rotation alone. */
    fun stationAt(rotationDeg: Double): Int {
        val a = ((rotationDeg % 360.0) + 360.0) % 360.0
        return (a / StrongroomMaximum.SPIN_STATION_SPACING_DEG).roundToInt() % 10
    }

    /**
     * Attempt a seat: the hand, travelling in the pass's lawful
     * direction at or below the seat ceiling, arrives within the
     * approach arc of a station. Returns true exactly when the
     * seat is taken. A wrong-direction or over-speed arrival seats
     * nothing and changes nothing — the mechanism refuses silently,
     * the way a real lock's gates refuse a flung dial.
     */
    fun attemptSeat(
        rotationDeg: Double,
        velocityDegPerSec: Double,
        clockwise: Boolean
    ): Boolean {
        if (isComplete) return false
        if (clockwise != passClockwise) return false
        if (abs(velocityDegPerSec) > StrongroomMaximum.SEAT_MAX_VELOCITY_DEG_PER_SEC) return false
        val a = ((rotationDeg % 360.0) + 360.0) % 360.0
        val stationAngle = stationAt(rotationDeg) *
            StrongroomMaximum.SPIN_STATION_SPACING_DEG
        val gap = abs(a - stationAngle).coerceAtMost(360.0 - abs(a - stationAngle))
        if (gap > StrongroomMaximum.SEAT_APPROACH_DEG) return false
        seatsTaken += 1
        return true
    }

    /** The felt amplitude of the current pass's stations — the
     *  thickening, in the foundation's figures (70 → 95 → 120). */
    fun stationAmplitude(): Int = StrongroomMaximum.spinStationAmplitude(pass)

    fun reset() {
        seatsTaken = 0
    }
}

// ────────────────────────────────────────────────────────────────
// THE BREAK CHOICE — freely chosen (ledger D-6). The foundation's
// break train: continuous fine grain, preset notches laid over it
// as topography on a FREE resistance curve — a notch costs no
// travel and stops nothing. Presets are one-tap shortcuts only,
// never the ceiling: any minute the hand places, stands.
// ────────────────────────────────────────────────────────────────

object BreakChoice {

    /** The train's own span, in minutes (foundation positions). */
    const val MAX_MINUTES: Int = 120

    val Train = StrongroomMaximum.breakTrain(StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES)

    /** Place a length: the hand's figure stands exactly as placed
     *  — no snapping to a preset, no rounding to a notch. */
    fun place(minutes: Double): Int = minutes.roundToInt().coerceIn(0, MAX_MINUTES)

    /** A preset shortcut: one tap places its exact figure. It is a
     *  shortcut to a lawful figure, not a tier of figures. */
    fun shortcut(presetMinutes: Int): Int = place(presetMinutes.toDouble())

    fun isPreset(minutes: Int): Boolean =
        minutes in StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES
}

// ────────────────────────────────────────────────────────────────
// THE ALLOWANCE PAWL — the one-way brass ratchet (intake §3 APPLY,
// routed to census C-23): tightening clicks off today instantly;
// a raise winds a visible NEXT DAY-START pending ring against the
// pawl's stop; once the day's purse is spent the loosening side
// sits beyond the hard stop. The law is the domain's own
// (StrongroomGuard, ledger D-1 as DECIDED at ledger §G-a: a raise
// travels D-1's road — next day-start — never FX-29's).
// ────────────────────────────────────────────────────────────────

object AllowancePawl {

    sealed interface Move {
        /** Tightened (or held): effective the instant it is set. */
        data class Tightened(val state: StrongroomGuard.GuardState) : Move

        /** A raise, wound against the pawl: it lands at the next
         *  day-start and at no earlier moment. */
        data class RaiseWound(
            val state: StrongroomGuard.GuardState,
            val effectiveAtMillis: Long
        ) : Move

        /** The purse is spent: the loosening side is beyond the
         *  hard stop and the move is refused by the mechanism. */
        data object BeyondHardStop : Move
    }

    /** The tighten train's figures (foundation): the pawl's double
     *  pulse per 5-minute detent, rising with tightness. */
    fun tightenTrain(currentMinutes: Int) =
        StrongroomMaximum.tightenTrain((currentMinutes / 5).coerceAtLeast(0))

    /** The raise's hollow seat (foundation): a different waveform
     *  family from tightening, by law — the opposition is felt. */
    fun hollowTrain(currentMinutes: Int) =
        StrongroomMaximum.hollowTrain((currentMinutes / 5).coerceAtLeast(0))

    fun move(
        state: StrongroomGuard.GuardState,
        targetMinutes: Int,
        todayIso: String,
        nextDayStartMillis: Long
    ): Move {
        val target = targetMinutes.coerceAtLeast(0)
        return if (target <= state.allowanceMinutes) {
            Move.Tightened(StrongroomGuard.tighten(state, target))
        } else {
            val (next, request) = StrongroomGuard.requestRaise(
                state, target, todayIso, nextDayStartMillis
            )
            when (request) {
                is StrongroomGuard.RaiseRequest.Scheduled ->
                    Move.RaiseWound(next, request.effectiveAtMillis)
                is StrongroomGuard.RaiseRequest.BeyondHardStop -> Move.BeyondHardStop
            }
        }
    }

    /** True while a raise stands wound against the pawl — the
     *  pending ring's standing fact, read off the Guard state. */
    fun raiseWound(state: StrongroomGuard.GuardState): Boolean =
        state.pendingAllowanceMinutes >= 0
}

// ────────────────────────────────────────────────────────────────
// THE SLIP — the signing's record, struck as the act's record
// (brief C-09; the complete slip's pull lives on as the struck day
// plate at the lifting, ledger E-1 — this slip is the signing
// table's own paper: what was settled, in the order it was
// settled, struck once the signature binds). Its lines carry only
// the signing's lawful words (census C-02): the purpose sentence
// and the clause spoken whole at the signing, the door's telling
// at the first signing, "codes wait tonight" where the purse
// stands spent, his task in his own words, his sentence or its
// kept place. The combination appears on no line, ever.
// ────────────────────────────────────────────────────────────────

object SigningSlip {

    const val CLAUSE: String = "do not listen to me after this"
    const val DOOR_TELLING: String =
        "You can close this room yourself, on any calm evening, in any month, " +
            "and it will read as a kept account."
    const val SPENT_DAY_TELLING: String = "codes wait tonight"

    enum class LineKind {
        PURPOSE, CLAUSE, DOOR_TELLING, SPENT_DAY, DAY_SHAPE, TASK,
        SENTENCE, KEPT_PLACE, LIFTING, BREAK, ALLOWANCE, ALLOWED
    }

    data class Line(val kind: LineKind, val text: String)

    fun dayShapeWord(shape: StrongroomComposition.DayShape): String = when (shape) {
        StrongroomComposition.DayShape.ORDINARY -> "ordinary day"
        StrongroomComposition.DayShape.TEST -> "test day"
        StrongroomComposition.DayShape.SMALL -> "the small day"
        StrongroomComposition.DayShape.REST -> "the rest day"
    }

    /**
     * The slip's lines for a standing record, in striking order.
     * [firstSigning] carries the door's telling (ledger B-19: at
     * the first signing, and standing quiet at every signing —
     * carried here only where the host names the first).
     * [codesWaitTonight] carries the spent-day telling (B-4).
     * [allowedCount] is struck as a count of TOKENS on the tray —
     * objects, never a names list (the names are the antechamber's
     * own showing, exactly as signed, on its own surface).
     */
    fun lines(
        day: StrongroomComposition.ComposedDay,
        firstSigning: Boolean,
        codesWaitTonight: Boolean,
        allowedCount: Int
    ): List<Line> {
        val r = day.record
        val out = mutableListOf<Line>()
        out += Line(LineKind.PURPOSE, StrongroomDay.PURPOSE_SENTENCE)
        out += Line(LineKind.CLAUSE, CLAUSE)
        if (firstSigning) out += Line(LineKind.DOOR_TELLING, DOOR_TELLING)
        if (codesWaitTonight) out += Line(LineKind.SPENT_DAY, SPENT_DAY_TELLING)
        out += Line(LineKind.DAY_SHAPE, dayShapeWord(day.shape))
        r.taskLine?.takeIf { it.isNotBlank() }?.let { out += Line(LineKind.TASK, it) }
        val sentence = r.sentenceOrKeptPlace
        if (sentence != null) {
            out += Line(LineKind.SENTENCE, sentence)
        } else {
            // The kept place: a line struck as a place, not a word —
            // the slip holds the space his sentence would occupy.
            out += Line(LineKind.KEPT_PLACE, "")
        }
        out += Line(
            LineKind.LIFTING,
            "%02d:%02d".format(r.liftingTime.hour, r.liftingTime.minute)
        )
        out += Line(LineKind.BREAK, "${r.breakLengthMinutes}")
        out += Line(LineKind.ALLOWANCE, "${r.allowanceMinutes}")
        out += Line(LineKind.ALLOWED, "$allowedCount")
        return out
    }
}
