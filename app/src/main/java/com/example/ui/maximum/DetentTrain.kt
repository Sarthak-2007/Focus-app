package com.example.ui.maximum

/**
 * DETENT TRAINS — a run of mechanical stops as one parameterised
 * object (BUILD-SR-11, T24, Stage 11).
 *
 * Two laws from FEEL-SR-3 govern everything here:
 *
 *  1. **Detents are keyed to POSITION, never to time.** A slow pass
 *     and a fast pass over the same travel produce the identical
 *     crossing sequence — [DetentTrain.crossings] is a pure function
 *     of the two positions, and no clock exists in this file.
 *  2. **A detent is a waveform, not a word.** Every detent carries
 *     its [DetentEventSpec] — timings and amplitudes as data — so a
 *     lane that renders "a click" without numbers has nowhere to
 *     hide. Binding the spec to a motor is the haptics slice's act
 *     (held, plan §8); the spec travels whole until then.
 *
 * The train also owns its resistance: by default the detent-well
 * curve over its own positions ([ResistanceCurves.detentWells]),
 * replaceable per element — the break setter's presets, for
 * example, are haptic-only and MUST override it with
 * [ResistanceCurves.Free] (FEEL-SR-3 C5.2 law 1, asserted in
 * MaximumPresetsTest).
 */

/**
 * One detent's felt shape, as data (FEEL-SR-3's `W[...]` notation,
 * decomposed). Amplitudes are 0–255 motor targets on one motor;
 * ratios govern (FEEL-SR-3 §04.5 — absolutes are UNPROVEN-ON-DEVICE
 * tuning targets until judged in hand).
 */
data class DetentEventSpec(
    /** Peak amplitude of the main pulse, 0–255. */
    val amplitude: Int,
    /** Main pulse length, ms. */
    val durationMs: Int,
    /** Silence BEFORE the pulse, ms — the gate's void (C1.2) is this field. */
    val preSilenceMs: Int = 0,
    /**
     * Delay after the position crossing before the pulse peaks, ms.
     * Every honest detent in the app is ≤10 (FEEL-SR-3's latency law);
     * the Hollow Seat's ~50 is its substance, carried here explicitly —
     * a spec may be late only by declaring it.
     */
    val latencyMs: Int = 0,
    /** Second pulse's amplitude (the pawl's catch, C3.1), or null for a single pulse. */
    val secondaryAmplitude: Int? = null,
    /** Gap between the main pulse and the second, ms. */
    val secondaryGapMs: Int = 0,
    /** True when the attack ramps instead of striking (hollow detents, preset notches). */
    val ramped: Boolean = false
) {
    init {
        require(amplitude in 0..255) { "detent amplitude must be within 0..255" }
        require(durationMs > 0) { "detent duration must be positive" }
        require(preSilenceMs >= 0 && latencyMs >= 0 && secondaryGapMs >= 0) { "detent timings must not be negative" }
        require(secondaryAmplitude == null || secondaryAmplitude in 0..255) {
            "secondary amplitude must be within 0..255"
        }
    }
}

/** Half the smallest gap between detents — the default lip distance for a train's well curve. */
private fun defaultLip(positions: List<Float>): Float {
    val sorted = positions.distinct().sorted()
    if (sorted.size < 2) return 1f
    var minGap = Float.MAX_VALUE
    for (i in 1 until sorted.size) minGap = minOf(minGap, sorted[i] - sorted[i - 1])
    return minGap / 2f
}

/** One crossing of one detent, in travel order. */
data class DetentCrossing(
    val position: Float,
    /** +1 travelling upward through the position, −1 downward. */
    val direction: Int,
    val spec: DetentEventSpec
)

/**
 * A train of detents at [positions], each with its spec from
 * [specFor] (by sorted index), resisted by [resistance].
 */
class DetentTrain(
    positions: List<Float>,
    val specFor: (index: Int) -> DetentEventSpec,
    val resistance: ResistanceCurve = ResistanceCurves.detentWells(
        positions, lipDistance = defaultLip(positions), depth = 0.6f
    )
) {
    val positions: List<Float> = positions.distinct().sorted()

    init {
        require(this.positions.isNotEmpty()) { "a detent train needs at least one detent" }
    }

    /**
     * Every detent crossed travelling [from] → [to], in the order
     * the hand meets them. A detent landed on exactly counts; a
     * detent started on does not recount; standing still crosses
     * nothing. Pure in the two positions — see the file's law 1.
     */
    fun crossings(from: Float, to: Float): List<DetentCrossing> {
        if (from == to) return emptyList()
        val direction = if (to > from) 1 else -1
        val hit = positions.withIndex().filter { (_, p) ->
            if (direction > 0) p > from && p <= to else p < from && p >= to
        }
        val ordered = if (direction > 0) hit else hit.reversed()
        return ordered.map { (index, p) -> DetentCrossing(p, direction, specFor(index)) }
    }

    /** The detent nearest a value (ties resolve to the lower detent). */
    fun nearest(value: Float): Float {
        var best = positions.first()
        var bestDistance = kotlin.math.abs(value - best)
        for (p in positions) {
            val d = kotlin.math.abs(value - p)
            if (d < bestDistance) {
                best = p
                bestDistance = d
            }
        }
        return best
    }

    fun resistanceAt(value: Float, velocity: Float): Float = resistance.resistanceAt(value, velocity)

    companion object {
        /** A uniform train: [count] detents from [start], [step] apart, one spec for all. */
        fun uniform(start: Float, step: Float, count: Int, spec: DetentEventSpec): DetentTrain {
            require(step != 0f) { "a uniform train needs a non-zero step" }
            require(count > 0) { "a uniform train needs at least one detent" }
            return DetentTrain((0 until count).map { start + it * step }, specFor = { spec })
        }
    }
}
