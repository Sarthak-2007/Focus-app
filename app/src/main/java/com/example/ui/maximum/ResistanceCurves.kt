package com.example.ui.maximum

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow

/**
 * RESISTANCE CURVES — the maximum toolkit's first primitive
 * (BUILD-SR-11, T24, Stage 11).
 *
 * A default-feeling control has, at best, one constant resistance.
 * A maximum control's resistance is a CURVE over its travel: it
 * ramps as a tightening bites, peaks at a detent's lip, grows
 * viscous as the hand speeds up. This file makes that curve a
 * first-class, parameterised value every other toolkit piece
 * consumes — Stage 11's per-element pass then chooses curves as
 * data, it does not invent machinery per element.
 *
 * A curve answers one question: at [value], moving at [velocity]
 * (value-units per second), by what MULTIPLIER is the hand's
 * input resisted? 1 = free travel; 2 = the same gesture moves
 * the control half as far; higher = harder. A multiplier is
 * never negative and never NaN — a curve that could push the
 * hand backwards or vanish is refused at construction.
 *
 * Everything here is pure: no frame clock, no Compose, no motor.
 * MOTION-SR-5's engine (`com.example.ui.motion`) owns bodies and
 * integration; this package extends it and forks nothing.
 */
fun interface ResistanceCurve {
    fun resistanceAt(value: Float, velocity: Float): Float
}

/** Named curve factories — the vocabulary Stage 11 assembles from. */
object ResistanceCurves {

    /** Free travel: the hand's input lands whole. */
    val Free: ResistanceCurve = ResistanceCurve { _, _ -> 1f }

    /** One constant resistance everywhere — the floor a curve must beat to earn its name. */
    fun constant(multiplier: Float): ResistanceCurve {
        require(multiplier >= 0f && multiplier.isFinite()) { "resistance multiplier must be finite and >= 0" }
        return ResistanceCurve { _, _ -> multiplier }
    }

    /**
     * A linear ramp between two travel points: [fromMultiplier] at
     * [startValue], [toMultiplier] at [endValue], clamped outside.
     * The tightening dial's rising bite is this curve (FEEL-SR-3 C3.1:
     * the last five minutes must feel harder than the first).
     */
    fun ramp(startValue: Float, endValue: Float, fromMultiplier: Float, toMultiplier: Float): ResistanceCurve {
        require(endValue != startValue) { "a ramp needs two distinct travel points" }
        require(fromMultiplier >= 0f && toMultiplier >= 0f) { "ramp multipliers must be >= 0" }
        return ResistanceCurve { value, _ ->
            val t = ((value - startValue) / (endValue - startValue)).coerceIn(0f, 1f)
            fromMultiplier + (toMultiplier - fromMultiplier) * t
        }
    }

    /**
     * Progressive resistance: grows with displacement from [centre]
     * as gain · |displacement / scale|^exponent, on top of [base].
     * The stretch stepper's rising weight with distance (FEEL-SR-3
     * C6.1) and the seat's approach ramp (C1.3) are this curve.
     */
    fun progressive(centre: Float, scale: Float, base: Float = 1f, gain: Float, exponent: Float = 2f): ResistanceCurve {
        require(scale > 0f) { "progressive scale must be positive" }
        require(base >= 0f && gain >= 0f) { "progressive base/gain must be >= 0" }
        require(exponent > 0f) { "progressive exponent must be positive" }
        return ResistanceCurve { value, _ ->
            base + gain * (abs(value - centre) / scale).pow(exponent)
        }
    }

    /**
     * Detent wells: resistance is LOWEST at each detent's centre
     * (the control rests there willingly) and PEAKS at [lipDistance]
     * from it — the lip the hand must climb to leave. The bump is
     * x²·e^(1−x²) over x = distance/lipDistance: 0 at the centre,
     * 1 at the lip, decaying beyond. The NEAREST well governs: a
     * centre is a true rest (exactly base resistance), and the
     * midpoint between two detents is the lip between them —
     * neighbouring tails never leak into a rest.
     */
    fun detentWells(positions: List<Float>, lipDistance: Float, depth: Float): ResistanceCurve {
        require(positions.isNotEmpty()) { "detent wells need at least one position" }
        require(lipDistance > 0f) { "lip distance must be positive" }
        require(depth >= 0f) { "well depth must be >= 0" }
        val sorted = positions.distinct().sorted()
        return ResistanceCurve { value, _ ->
            var nearest = Float.MAX_VALUE
            for (p in sorted) nearest = minOf(nearest, abs(value - p))
            val x = nearest / lipDistance
            val bump = x * x * exp(1f - x * x)
            1f + depth * bump
        }
    }

    /** Viscous resistance: the faster the hand moves, the harder it pushes back. */
    fun viscous(gain: Float, base: Float = 1f): ResistanceCurve {
        require(gain >= 0f && base >= 0f) { "viscous gain/base must be >= 0" }
        return ResistanceCurve { _, velocity -> base + gain * abs(velocity) }
    }

    /** The stronger of two curves at every point — composition without stacking. */
    fun maxOf(first: ResistanceCurve, second: ResistanceCurve): ResistanceCurve =
        ResistanceCurve { value, velocity ->
            maxOf(first.resistanceAt(value, velocity), second.resistanceAt(value, velocity))
        }

    /** Two curves acting together: their multipliers multiply. */
    fun product(first: ResistanceCurve, second: ResistanceCurve): ResistanceCurve =
        ResistanceCurve { value, velocity ->
            first.resistanceAt(value, velocity) * second.resistanceAt(value, velocity)
        }
}
