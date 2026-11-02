package com.example.ui.maximum

import com.example.ui.motion.DetentRail
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.PhysicsBody
import com.example.ui.motion.SpringIntegrator
import com.example.ui.motion.SpringState
import kotlin.math.abs

/**
 * THE WEIGHTED ROTARY — a spun mass as reusable machinery
 * (BUILD-SR-11, T24, Stage 11).
 *
 * The combination wheel, the drum, the crank are one mechanism at
 * different parameters: a mass the hand turns, detents it passes,
 * inertia it keeps when the hand leaves, and a settle that lands
 * it exactly on a station. This file is that mechanism, built on
 * MOTION-SR-5's bodies ([PhysicsBody] for the settle, [InertiaBody]
 * for the coast, [DetentRail] for the stations) — it re-implements
 * none of them.
 *
 * THE GOVERNOR is the rotary's character (FEEL-SR-3 C1, UX-SR-1 §5):
 * a combination dial must not be flicked through a number. Two
 * limits, both structural, neither a rendering choice:
 *
 *  - **Velocity is governed** — however hard the hand flings, the
 *    dial's velocity never exceeds [WeightedRotarySpec.maxVelocityDegPerSec].
 *  - **The coast is governed** — released mid-orbit, the dial
 *    travels at most [WeightedRotarySpec.maxCoastDegrees] on its
 *    inertia before the governor brake stills it, then the spring
 *    settle carries it onto its station. A release therefore can
 *    never seat the dial, never skip a station silently, and never
 *    come to rest between stations: every rest is a station,
 *    exactly.
 *
 * Angles are cumulative degrees (passes and revolutions live in
 * the accumulation, never normalised away); stations are the
 * rail's positions on that same cumulative scale's revolution —
 * see [stationUnder] for the mapping the spin uses.
 */
data class WeightedRotarySpec(
    /** The spring body of the settle onto a station. */
    val body: PhysicsBody,
    /** The coast body after the hand leaves. */
    val inertia: InertiaBody,
    /** The governor's velocity ceiling, degrees per second. */
    val maxVelocityDegPerSec: Float,
    /** The governor's coast ceiling, degrees, from the release point. */
    val maxCoastDegrees: Float,
    /** Resistance over travel — detent wells by default for a railed rotary. */
    val resistance: ResistanceCurve = ResistanceCurves.Free
) {
    init {
        require(maxVelocityDegPerSec > 0f) { "the governor's velocity ceiling must be positive" }
        require(maxCoastDegrees > 0f) { "the governor's coast ceiling must be positive" }
    }
}

/** The outcome of a release: where the coast ended, what caught it, where it rests. */
data class RotaryRelease(
    /** The station the dial rests on, exactly. */
    val station: Float,
    /** Degrees travelled on inertia before the brake/spring took over. */
    val coastDegrees: Float,
    /** Total simulated seconds from release to rest on the station. */
    val settleSeconds: Float,
    /** True when the governor's coast ceiling, not the inertia, ended the coast. */
    val governorBraked: Boolean
)

class WeightedRotary(
    val spec: WeightedRotarySpec,
    /** Stations within one revolution, degrees in [0, 360). */
    stationPositions: List<Float>
) {
    private val rail = DetentRail(stationPositions.map { it.mod(360f) })

    /** Current cumulative angle, degrees. */
    var angleDeg: Float = 0f
        private set

    /** Current velocity, degrees per second (governed at all times). */
    var velocityDegPerSec: Float = 0f
        private set

    /**
     * Turn the dial by the hand's [deltaDeg] over [dtSeconds].
     * The resistance curve divides the input; the resulting
     * velocity is clamped to the governor's ceiling. Returns the
     * degrees the dial actually turned.
     */
    fun dragBy(deltaDeg: Float, dtSeconds: Float): Float {
        require(dtSeconds > 0f) { "a drag needs a positive duration" }
        val resisted = deltaDeg / spec.resistance.resistanceAt(angleDeg, velocityDegPerSec)
        val rawVelocity = resisted / dtSeconds
        val governed = rawVelocity.coerceIn(-spec.maxVelocityDegPerSec, spec.maxVelocityDegPerSec)
        val applied = governed * dtSeconds
        angleDeg += applied
        velocityDegPerSec = governed
        return applied
    }

    /** The hand holds the dial still: velocity is the hand's to zero. */
    fun hold() {
        velocityDegPerSec = 0f
    }

    /**
     * Teleport to a decided angle (a seat snapping home, a state
     * restoration) — never a visual effect, never a travel.
     */
    fun snapTo(angle: Float) {
        angleDeg = angle
        velocityDegPerSec = 0f
    }

    /** The station (within the revolution) nearest the dial's current heading. */
    fun stationUnder(valueDeg: Float = angleDeg): Float = rail.nearest(valueDeg.mod(360f))

    /**
     * Release the dial: coast on inertia under the governor, then
     * settle by spring onto the station the dial is heading for.
     * Deterministic — the same release state always rests at the
     * same station (MOTION-SR-5's integrator, substepped).
     */
    fun release(): RotaryRelease {
        val start = angleDeg
        var state = SpringState(angleDeg, velocityDegPerSec)
        var elapsed = 0f
        var braked = false
        val h = 1f / 240f
        // Coast: inertia steps until the body stills, the governor's
        // ceiling is spent, or the velocity falls to the spring's
        // working range — then the settle takes it the rest of the way.
        while (abs(state.velocity) > 1f && elapsed < 10f) {
            if (abs(state.value - start) >= spec.maxCoastDegrees) {
                braked = true
                state = SpringState(state.value, 0f)
                break
            }
            state = spec.inertia.step(state, h)
            elapsed += h
        }
        val coast = abs(state.value - start)
        // The catch is decided from where the dial is heading at the
        // end of its coast, on the revolution's rail, then expressed
        // back on the cumulative scale nearest the coast's end.
        val stationInRevolution = rail.captureTarget(state.value.mod(360f), state.velocity, lookaheadSeconds = 0.12f)
        val target = cumulativeNear(state.value, stationInRevolution)
        val (settled, settleSeconds) = SpringIntegrator.simulateUntilSettled(state.value, target, spec.body)
        angleDeg = settled.value
        velocityDegPerSec = 0f
        return RotaryRelease(
            station = stationInRevolution,
            coastDegrees = coast,
            settleSeconds = elapsed + settleSeconds,
            governorBraked = braked
        )
    }

    /** Express a within-revolution station on the cumulative scale, nearest [near]. */
    private fun cumulativeNear(near: Float, stationInRevolution: Float): Float {
        val revolution = kotlin.math.floor(near / 360f)
        var best = revolution * 360f + stationInRevolution
        for (candidate in listOf(best - 360f, best, best + 360f)) {
            if (abs(candidate - near) < abs(best - near)) best = candidate
        }
        return best
    }
}
