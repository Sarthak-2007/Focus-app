package com.example.ui.maximum

import com.example.ui.motion.DetentRail
import com.example.ui.motion.InertiaBody
import com.example.ui.motion.PhysicsBody
import com.example.ui.motion.SpringIntegrator
import com.example.ui.motion.SpringState
import kotlin.math.abs

/**
 * INERTIA-CARRYING DRAGS WITH HONEST SETTLE (BUILD-SR-11, T24).
 *
 * The hour setter, the break setter, the allowance rim are linear
 * drags; a maximum drag keeps the hand's momentum when the hand
 * leaves and then SETTLES HONESTLY:
 *
 *  - **Capture is decided from where the body is heading**, not
 *    where the finger let go (MOTION-SR-5's
 *    [DetentRail.captureTarget], reused, not forked).
 *  - **Settle is the body's own band**, never a timeout: the drag
 *    is at rest only when [SpringIntegrator.isSettled] says so
 *    against the body's [com.example.ui.motion.SettleSpec], and a
 *    settled drag rests EXACTLY on its decided value.
 *  - **No-inertia controls are expressible, not hacked**: the
 *    stretch stepper (FEEL-SR-3 C6.1 — "released at any velocity,
 *    travel stops within the current day") is this same drag with
 *    an inertia body whose decay stills it within a step; the
 *    machinery does not change, the parameter does. Its preset
 *    lives in [MaximumPresets] and its law is asserted by test.
 */
data class InertiaDragSpec(
    /** The spring body of the settle onto the captured detent. */
    val body: PhysicsBody,
    /** The coast body after the hand leaves. */
    val inertia: InertiaBody,
    /** The detents this drag may rest on. */
    val rail: DetentRail,
    /** Within this distance of a detent, a slow body is held by it. */
    val captureRadius: Float = 0.5f,
    /** At or below this speed (units/s), a body inside the radius is captured. */
    val captureVelocity: Float = 0.5f,
    /** How far ahead the capture decision looks, seconds. */
    val captureLookaheadSeconds: Float = 0.12f
) {
    init {
        require(captureRadius > 0f) { "capture radius must be positive" }
        require(captureVelocity > 0f) { "capture velocity must be positive" }
        require(captureLookaheadSeconds >= 0f) { "capture lookahead must not be negative" }
    }
}

/** The outcome of a drag's release: the coast, the catch, the rest. */
data class DragRelease(
    /** The detent the drag rests on, exactly. */
    val target: Float,
    /** The value the drag actually came to rest at — equal to [target], by the honest settle. */
    val finalValue: Float,
    /** Where the pure coast alone would have come to rest (the inertia's own answer). */
    val coastRestValue: Float,
    /** Total simulated seconds from release to rest on the target. */
    val settleSeconds: Float
)

class InertiaDrag(val spec: InertiaDragSpec) {

    /**
     * Release a travelling drag. The coast runs first (substepped,
     * deterministic); the moment the body is slow and inside a
     * detent's radius it is captured, and the spring — carrying the
     * coast's remaining velocity — settles it onto the target the
     * capture decision named at release. Returns the rest, exact.
     */
    fun release(from: SpringState): DragRelease {
        val target = spec.rail.captureTarget(from.value, from.velocity, spec.captureLookaheadSeconds)
        val coastRest = spec.inertia.restValue(from)
        var state = from
        var elapsed = 0f
        val h = 1f / 240f
        while (elapsed < 10f) {
            if (spec.rail.shouldCapture(state.value, state.velocity, spec.captureRadius, spec.captureVelocity)) break
            if (abs(state.velocity) <= spec.captureVelocity) break
            state = spec.inertia.step(state, h)
            elapsed += h
        }
        // Spring settle carrying current velocity, integrated by hand
        // (simulateUntilSettled starts from rest; a carried drag does not).
        var settleElapsed = 0f
        while (settleElapsed < 10f) {
            if (SpringIntegrator.isSettled(state, target, spec.body)) {
                state = SpringState(target, 0f)
                break
            }
            state = SpringIntegrator.step(state, target, spec.body, h)
            settleElapsed += h
        }
        if (SpringIntegrator.isSettled(state, target, spec.body)) state = SpringState(target, 0f)
        return DragRelease(
            target = target, finalValue = state.value,
            coastRestValue = coastRest, settleSeconds = elapsed + settleElapsed
        )
    }

    /** Where a drag released at [from] would rest — the pure query the tests and previews use. */
    fun restTarget(from: SpringState): Float =
        spec.rail.captureTarget(from.value, from.velocity, spec.captureLookaheadSeconds)
}
