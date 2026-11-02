package com.example.ui.maximum

import com.example.ui.motion.InertiaBody
import com.example.ui.motion.MonolithBodies
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/**
 * THE COMBINATION SPIN — the maximum toolkit's PROOF PIECE
 * (BUILD-SR-11, T24, Stage 11; the census's head).
 *
 * Built end-to-end on the toolkit, nothing bespoke: the dial is a
 * [WeightedRotary] (governed velocity, governed coast), its
 * stations are [MaximumPresets.spinTrain], its approach is a
 * one-sided resistance ramp over signed distance-to-seat, its
 * post-seat deadness is the
 * [HardStop] shape. Stage 11's other elements assemble the same
 * way; this piece proves the assembly on the hardest control.
 *
 * The felt grammar is FEEL-SR-3's C1, whole:
 *
 *  - **Stations** tick per number, keyed to travel position, with
 *    the pass ladder 70 → 95 → 120 — the thickening the hand
 *    counts passes by, without watching.
 *  - **Gates** fire at direction reversals (after real travel):
 *    a void, then a heavy pulse — never mistakable for a station.
 *  - **THE SEAT** — the final number, entered at governed
 *    velocity from the correct direction: an approach ramp over
 *    the last ~15°, a thud, a settle. After it the dial is DEAD:
 *    a reverse attempt meets the end-stop, once per attempt.
 *  - **No wrong-combination event exists.** The event type below
 *    has no error case by construction: a crossing of the seat's
 *    station too fast, or from the wrong direction, produces an
 *    ordinary station tick and travel continues. The missing
 *    seat is the message (FEEL-SR-3 C1, send-back #6).
 *  - **A release can never seat.** Seating happens only under
 *    the hand, in [rotate]; [endGesture]'s release settles the
 *    dial onto a station and nothing more.
 *
 * THE COMBINATION IS NEVER WRITTEN ANYWHERE (ledger A-2): this
 * model carries travel, passes and events only. There is no
 * field a combination could hide in — the census test asserts
 * the declared fields by name. What the seat IS in this model is
 * a travel target ([seatAtDeg], a cumulative angle on the dial's
 * own scale) handed in by the surface for the act's duration —
 * the model neither knows nor asks what number it represents.
 */

/** Every felt event the spin can emit. There is deliberately no error event. */
sealed interface SpinEvent {
    val spec: DetentEventSpec

    /** A station tick, at the pass's ladder amplitude. */
    data class Station(override val spec: DetentEventSpec, val pass: Int) : SpinEvent

    /** A gate, at a direction reversal: void first, then the pulse. */
    data class Gate(override val spec: DetentEventSpec) : SpinEvent

    /** The seat's approach ramp: rising pressure over the last degrees. */
    data class SeatRamp(override val spec: DetentEventSpec) : SpinEvent

    /** The seat itself: the thud. */
    data class SeatThud(override val spec: DetentEventSpec) : SpinEvent

    /** The mechanism coming to rest after the thud. */
    data class SeatSettle(override val spec: DetentEventSpec) : SpinEvent

    /** After the seat, a reverse attempt against the dead dial. Once per attempt. */
    data class EndStop(override val spec: DetentEventSpec) : SpinEvent
}

class CombinationSpin(
    /** The cumulative angle (degrees, dial scale) at which the seat waits. */
    val seatAtDeg: Float,
    /** The direction the seat must be entered from: +1 upward travel, −1 downward. */
    val seatDirection: Int,
    val onEvent: (SpinEvent) -> Unit = {}
) {
    init {
        require(seatDirection == 1 || seatDirection == -1) { "the seat's direction must be +1 or -1" }
    }

    private val rotary: WeightedRotary = WeightedRotary(
        spec = WeightedRotarySpec(
            body = MonolithBodies.Wheel,
            inertia = InertiaBody(mass = MonolithBodies.Wheel.mass, resistance = 0.9f),
            maxVelocityDegPerSec = MaximumPresets.SPIN_MAX_VELOCITY_DEG_PER_SEC,
            maxCoastDegrees = MaximumPresets.SPIN_MAX_COAST_DEG,
            // The approach (C1.3a): a ONE-SIDED ramp over the signed
            // distance short of the seat — free everywhere except the
            // seat's last degrees, in the seat's own direction, where
            // resistance climbs to 3.5× at the seat itself. (A plain
            // two-point ramp would wrongly charge maximum resistance
            // on the seat's far side; the signed distance is the
            // correct axis, so the curve is written on it directly.)
            resistance = ResistanceCurve { value, _ ->
                val shortOfSeat = seatDirection * (seatAtDeg - value)
                if (shortOfSeat in 0f..MaximumPresets.SEAT_APPROACH_DEG) {
                    1f + 2.5f * (1f - shortOfSeat / MaximumPresets.SEAT_APPROACH_DEG)
                } else {
                    1f
                }
            }
        ),
        stationPositions = (0 until 10).map { it * MaximumPresets.SPIN_STATION_SPACING_DEG }
    )

    /** Cumulative travel, degrees — the dial's whole history this act, never a combination. */
    val angleDeg: Float get() = rotary.angleDeg

    val velocityDegPerSec: Float get() = rotary.velocityDegPerSec

    /** The current pass (1-based): gates passed + 1. The hand feels it in the ladder. */
    var pass: Int = 1
        private set

    /** True once the seat has fired. From here the dial is dead. */
    var seated: Boolean = false
        private set

    private var lastDirection: Int = 0
    private var lastGateAngle: Float = 0f
    private var seatRampFired: Boolean = false
    private var endStopArmed: Boolean = true

    /** A new gesture begins: a post-seat end-stop may fire once within it. */
    fun beginGesture() {
        endStopArmed = true
    }

    /** The gesture ends: unseated, the dial releases onto its station (and never seats). */
    fun endGesture(): RotaryRelease? {
        if (seated) return null
        return rotary.release()
    }

    /**
     * Turn the dial by the hand's [deltaDeg] over [dtSeconds].
     * Returns the degrees actually turned (0 once seated).
     * All events fire on position crossings inside this call —
     * FEEL-SR-3's latency law is the caller's motor binding, the
     * crossings themselves are position-true here.
     */
    fun rotate(deltaDeg: Float, dtSeconds: Float): Float {
        if (seated) {
            if (deltaDeg != 0f && endStopArmed) {
                endStopArmed = false
                onEvent(SpinEvent.EndStop(MaximumPresets.SpinEndStop.refusalSpec))
            }
            return 0f
        }
        val from = rotary.angleDeg
        val applied = rotary.dragBy(deltaDeg, dtSeconds)
        val to = rotary.angleDeg
        if (applied == 0f) return 0f
        val direction = sign(applied).toInt()

        emitStationCrossings(from, to)
        emitGateIfReversed(direction, to)
        emitSeatRampIfApproaching(direction, to)
        trySeat(from, to, direction)
        if (direction != 0) lastDirection = direction
        return applied
    }

    /** Station ticks for every station index crossed, at the current pass's ladder. */
    private fun emitStationCrossings(from: Float, to: Float) {
        val spacing = MaximumPresets.SPIN_STATION_SPACING_DEG
        val fromIndex = floor(from / spacing).toInt()
        val toIndex = floor(to / spacing).toInt()
        if (fromIndex == toIndex) return
        val train = MaximumPresets.spinTrain(pass)
        val step = if (toIndex > fromIndex) 1 else -1
        var index = fromIndex
        while (index != toIndex) {
            index += step
            val spec = train.specFor(((index % 10) + 10) % 10)
            onEvent(SpinEvent.Station(spec = spec, pass = pass))
        }
    }

    /** A gate: direction reversed after travelling at least one station since the last gate. */
    private fun emitGateIfReversed(direction: Int, to: Float) {
        if (lastDirection == 0 || direction == lastDirection) return
        if (abs(to - lastGateAngle) < MaximumPresets.SPIN_STATION_SPACING_DEG) return
        onEvent(SpinEvent.Gate(MaximumPresets.GateSpec))
        pass++
        lastGateAngle = to
    }

    /** The approach ramp, once per approach, over the seat's last ~15° in its direction. */
    private fun emitSeatRampIfApproaching(direction: Int, to: Float) {
        val shortOfSeat = seatDirection * (seatAtDeg - to)
        val inZone = shortOfSeat in 0f..MaximumPresets.SEAT_APPROACH_DEG
        if (!inZone) {
            seatRampFired = false
            return
        }
        if (direction == seatDirection && !seatRampFired) {
            seatRampFired = true
            onEvent(
                SpinEvent.SeatRamp(
                    DetentEventSpec(amplitude = 215, durationMs = 90, ramped = true)
                )
            )
        }
    }

    /**
     * The seat: crossed this call, in its direction, at governed
     * velocity. Any other crossing is an ordinary pass over a
     * station — the ticks already said everything there is to say.
     */
    private fun trySeat(from: Float, to: Float, direction: Int) {
        val wasShort = seatDirection * (seatAtDeg - from) > 0f
        val nowPast = seatDirection * (seatAtDeg - to) <= 0f
        if (!wasShort || !nowPast) return
        if (direction != seatDirection) return
        if (abs(rotary.velocityDegPerSec) > MaximumPresets.SEAT_MAX_VELOCITY_DEG_PER_SEC) return
        onEvent(SpinEvent.SeatThud(MaximumPresets.SeatThudSpec))
        onEvent(SpinEvent.SeatSettle(MaximumPresets.SeatSettleSpec))
        seated = true
        rotary.snapTo(seatAtDeg)
    }
}
