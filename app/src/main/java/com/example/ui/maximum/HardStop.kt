package com.example.ui.maximum

import kotlin.math.abs

/**
 * HARD STOPS — resistance-then-refusal as a reusable behaviour
 * (BUILD-SR-11, T24, Stage 11).
 *
 * Three of the day's mechanisms end the same way: the drum's crank
 * against a spent purse (ledger A-7 — "the crank will not turn"),
 * the spin's dial against its seat (FEEL-SR-3 C1.3 — the END-STOP),
 * the break setter at its zero end (C5 — "a position with a floor
 * under it"). Their shared shape is the mechanism this file builds
 * once:
 *
 *  1. **Resistance first.** Inside the approach zone the hand
 *     feels the stop coming: resistance ramps (quadratically, so
 *     the last stretch costs most) from base to maximum. A stop
 *     with no approach is a wall the hand learns nothing from.
 *  2. **Then refusal, once.** A push past the limit is refused
 *     whole: the value clamps AT the limit, velocity dies, and
 *     exactly ONE refusal event fires per attempt — identical
 *     every time, no escalation, no softening (the W-5d law
 *     FEEL-SR-3 carries). A hand that keeps pressing does not
 *     keep being answered.
 *  3. **Re-arming is leaving.** The refusal re-arms only when the
 *     value retreats out of the approach zone — a new approach is
 *     a new attempt; jiggling at the limit is not.
 *
 * A stop is directional: [HardStopSpec.sign] +1 guards an upper
 * limit, −1 a lower one; the maths is written once, in signed
 * distance past the limit.
 */
data class HardStopSpec(
    /** The limit value itself — the control may rest here, never pass. */
    val limit: Float,
    /** +1: nothing above the limit. −1: nothing below it. */
    val sign: Int = 1,
    /** Width of the approach zone before the limit, in value units. */
    val approachZone: Float,
    /** Resistance multiplier AT the limit. */
    val maxResistance: Float,
    /** Resistance multiplier at the approach zone's outer edge. */
    val baseResistance: Float = 1f,
    /** The refusal's felt shape, as data (FEEL-SR-3: flat, no tick, no ramp). */
    val refusalSpec: DetentEventSpec = DetentEventSpec(amplitude = 195, durationMs = 45)
) {
    init {
        require(sign == 1 || sign == -1) { "a hard stop's sign must be +1 or -1" }
        require(approachZone > 0f) { "the approach zone must be positive" }
        require(maxResistance >= baseResistance && baseResistance >= 0f) {
            "resistance must ramp upward toward the limit"
        }
    }

    /** Signed distance of [value] past the limit: positive = beyond, negative = short of it. */
    fun pastLimit(value: Float): Float = sign * (value - limit)

    /** Signed distance short of the limit (positive while approaching). */
    fun shortOfLimit(value: Float): Float = -pastLimit(value)
}

/** The outcome of one push against a hard-stopped control. */
data class PushResult(
    /** The value the control actually takes (clamped at the limit on refusal). */
    val value: Float,
    /** The velocity the control keeps (zero on refusal). */
    val velocity: Float,
    /** True when this push was refused at the limit. */
    val refused: Boolean,
    /** True only on the attempt's first refusal — the event the caller fires. */
    val refusalEvent: Boolean
)

class HardStop(val spec: HardStopSpec) : ResistanceCurve {

    private var armed = true

    /** Refusal events fired so far — one per attempt, by construction. */
    var attempts: Int = 0
        private set

    /** The stop's own resistance curve: the ramp inside the approach zone. */
    override fun resistanceAt(value: Float, velocity: Float): Float {
        val short = spec.shortOfLimit(value)
        if (short <= 0f) return spec.maxResistance
        if (short >= spec.approachZone) return spec.baseResistance
        val t = 1f - short / spec.approachZone // 0 at the zone's edge, 1 at the limit
        return spec.baseResistance + (spec.maxResistance - spec.baseResistance) * t * t
    }

    /**
     * Push the control from [currentValue] toward [proposedValue]
     * at [velocity]. Inside the limit the push lands whole (the
     * caller applies [resistanceAt] to the gesture itself); past
     * it, the push is refused per the file's law 2. Leaving the
     * approach zone re-arms the refusal (law 3).
     */
    fun push(currentValue: Float, proposedValue: Float, velocity: Float): PushResult {
        if (spec.shortOfLimit(currentValue) > spec.approachZone) armed = true
        return if (spec.pastLimit(proposedValue) > 0f) {
            val event = armed
            if (event) {
                attempts++
                armed = false
            }
            PushResult(value = spec.limit, velocity = 0f, refused = true, refusalEvent = event)
        } else {
            PushResult(value = proposedValue, velocity = velocity, refused = false, refusalEvent = false)
        }
    }

    /** True while [value] sits inside the approach zone — the hand can feel the stop coming. */
    fun inApproachZone(value: Float): Boolean =
        spec.shortOfLimit(value) in 0f..spec.approachZone

    /** Travelled distance must be real: a stop mis-set against its own maths is refused. */
    fun checkTravel(from: Float, to: Float): Boolean = abs(to - from).isFinite()
}
