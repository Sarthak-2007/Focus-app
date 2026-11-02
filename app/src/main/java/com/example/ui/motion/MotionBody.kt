package com.example.ui.motion

import androidx.compose.animation.core.SpringSpec
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * THE MOTION ENGINE — physics primitives (MOTION-SR-5, T24).
 *
 * The successor to MonolithCurves/MonolithMotion (succession agreed with
 * BUILD-SR-1 by letter, 2026-11-02): motion here is PHYSICS, not easing
 * presets. A moving thing is a body with MASS, STIFFNESS and RESISTANCE,
 * and SETTLE is a first-class part of its definition — the explicit
 * distance/velocity band inside which the body is at rest. There is no
 * duration on a spring body: how long a travel takes is a consequence of
 * its physics, never a number typed beside it.
 *
 * Numbers enter as data: MOTION-SR-1..4's specs parameterise these types;
 * the seeded set in [MonolithBodies] is P0's landed character, preserved
 * exactly (see the factory note there).
 */

/** The definition of "at rest" for a body: both bands must hold at once. */
data class SettleSpec(
    /** |value − target| at or below this counts as arrived. */
    val distanceEpsilon: Float = 0.001f,
    /** |velocity| at or below this (value-units per second) counts as still. */
    val velocityEpsilon: Float = 0.01f
) {
    init {
        require(distanceEpsilon > 0f) { "settle distance epsilon must be positive" }
        require(velocityEpsilon > 0f) { "settle velocity epsilon must be positive" }
    }
}

/**
 * A spring body: mass, stiffness, resistance — and its settle band.
 * Damping ratio and natural frequency are DERIVED views, never stored:
 * a spec files the physics; the character follows.
 */
data class PhysicsBody(
    val mass: Float,
    /** Spring constant k (force per unit displacement). */
    val stiffness: Float,
    /** Damping coefficient c (force per unit velocity) — the resistance. */
    val resistance: Float,
    val settle: SettleSpec = SettleSpec()
) {
    init {
        require(mass > 0f) { "mass must be positive" }
        require(stiffness > 0f) { "stiffness must be positive" }
        require(resistance >= 0f) { "resistance must not be negative" }
    }

    /** ζ = c / (2√(k·m)) — below 1 the body overshoots, at 1 it is dead, above it crawls. */
    val dampingRatio: Float get() = resistance / (2f * sqrt(stiffness * mass))

    /** The stiffness Compose's unit-mass spring would need: k/m. */
    val normalisedStiffness: Float get() = stiffness / mass

    /** Undamped natural frequency, radians per second. */
    val naturalFrequency: Float get() = sqrt(stiffness / mass)

    /** The Compose bridge: the same character in SpringSpec terms. */
    fun toSpringSpec(): SpringSpec<Float> =
        SpringSpec(dampingRatio = dampingRatio, stiffness = normalisedStiffness)

    companion object {
        /**
         * Build a body from character: [dampingRatio] ζ and the P0-style
         * unit-mass [normalisedStiffness], at a real [mass]. The actual
         * k and c scale with mass so the MOTION is identical whatever
         * mass the spec assigns — mass then stays free to mean what it
         * should (how the body couples to detents and collisions),
         * without silently retuning the travel.
         */
        fun fromCharacter(
            mass: Float,
            dampingRatio: Float,
            normalisedStiffness: Float,
            settle: SettleSpec = SettleSpec()
        ): PhysicsBody {
            val k = normalisedStiffness * mass
            val c = 2f * dampingRatio * sqrt(k * mass)
            return PhysicsBody(mass = mass, stiffness = k, resistance = c, settle = settle)
        }
    }
}

/** One instant of a body's travel: where it is, how fast it is moving. */
data class SpringState(val value: Float, val velocity: Float)

/**
 * The deterministic spring integrator: semi-implicit Euler on fixed
 * substeps (max 1/240 s). It can be substepped, seeded and aborted —
 * the properties an interpolator lacks — so a choreography score can
 * ask "where is this body at t?" as a pure function and get the same
 * answer on every run, every device, in every test.
 */
object SpringIntegrator {

    private const val MAX_SUBSTEP_SECONDS = 1f / 240f

    /** Advance one step of [dtSeconds] (substepped internally) toward [target]. */
    fun step(state: SpringState, target: Float, body: PhysicsBody, dtSeconds: Float): SpringState {
        require(dtSeconds >= 0f) { "dt must not be negative" }
        if (dtSeconds == 0f) return state
        val substeps = maxOf(1, (dtSeconds / MAX_SUBSTEP_SECONDS).toInt() + 1)
        val h = dtSeconds / substeps
        var s = state
        repeat(substeps) {
            // a = (−k(x − target) − c·v) / m
            val acceleration = (-body.stiffness * (s.value - target) - body.resistance * s.velocity) / body.mass
            val velocity = s.velocity + acceleration * h
            s = SpringState(value = s.value + velocity * h, velocity = velocity)
        }
        return s
    }

    /** Rest is the body's own settle band — both halves, at once. */
    fun isSettled(state: SpringState, target: Float, body: PhysicsBody): Boolean =
        kotlin.math.abs(state.value - target) <= body.settle.distanceEpsilon &&
            kotlin.math.abs(state.velocity) <= body.settle.velocityEpsilon

    /**
     * Run a body from [from] (at rest) toward [target] until it settles
     * or [maxSeconds] elapses. Returns the final state and the simulated
     * seconds consumed. On settle the state is snapped exactly onto the
     * target with zero velocity: a settled body rests ON its decided
     * value, never a hair beside it.
     */
    fun simulateUntilSettled(
        from: Float,
        target: Float,
        body: PhysicsBody,
        maxSeconds: Float = 10f
    ): Pair<SpringState, Float> {
        var state = SpringState(from, 0f)
        var elapsed = 0f
        val h = MAX_SUBSTEP_SECONDS
        while (elapsed < maxSeconds) {
            if (isSettled(state, target, body)) return SpringState(target, 0f) to elapsed
            state = step(state, target, body, h)
            elapsed += h
        }
        return state to elapsed
    }

    /** Where a body travelling from rest at [from] toward [target] is after [seconds]. */
    fun valueAt(from: Float, target: Float, body: PhysicsBody, seconds: Float): Float {
        if (seconds <= 0f) return from
        var state = SpringState(from, 0f)
        var elapsed = 0f
        val h = MAX_SUBSTEP_SECONDS
        while (elapsed < seconds) {
            if (isSettled(state, target, body)) return target
            val dt = minOf(h, seconds - elapsed)
            state = step(state, target, body, dt)
            elapsed += dt
        }
        return state.value
    }
}

/**
 * An inertia body: a flung thing with no spring home — a wheel after the
 * finger leaves it. Velocity decays exponentially through resistance;
 * where it stops is where the detents (see [DetentRail]) catch it.
 */
data class InertiaBody(val mass: Float, val resistance: Float) {
    init {
        require(mass > 0f) { "mass must be positive" }
        require(resistance >= 0f) { "resistance must not be negative" }
    }

    /** Decay rate λ = c/m, per second. */
    val decayRate: Float get() = resistance / mass

    /** Advance a coasting body: velocity decays, position integrates it. */
    fun step(state: SpringState, dtSeconds: Float): SpringState {
        require(dtSeconds >= 0f) { "dt must not be negative" }
        if (dtSeconds == 0f || decayRate == 0f) {
            return SpringState(state.value + state.velocity * dtSeconds, state.velocity)
        }
        val decay = exp(-decayRate * dtSeconds)
        val velocity = state.velocity * decay
        val travelled = state.velocity * (1f - decay) / decayRate
        return SpringState(value = state.value + travelled, velocity = velocity)
    }

    /** Where a coast starting at [state] comes to rest if nothing catches it. */
    fun restValue(state: SpringState): Float =
        if (decayRate == 0f) Float.POSITIVE_INFINITY else state.value + state.velocity / decayRate
}

/**
 * A rail of detents: the mechanical stops a travelling body is caught
 * by (the drum's positions, the wheel's numbers). Capture is decided
 * from the body's projected landing, not from where the finger let go.
 */
class DetentRail(positions: List<Float>) {

    val positions: List<Float> = positions.distinct().sorted()

    init {
        require(this.positions.isNotEmpty()) { "a detent rail needs at least one detent" }
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

    /**
     * The detent that should catch a body at [value] moving at
     * [velocity]: project the coast [lookaheadSeconds] forward, then
     * take the nearest detent to where the body is heading.
     */
    fun captureTarget(value: Float, velocity: Float, lookaheadSeconds: Float = 0.12f): Float =
        nearest(value + velocity * lookaheadSeconds)

    /** True when [value] sits inside [captureRadius] of a detent and is slow enough to be held. */
    fun shouldCapture(
        value: Float,
        velocity: Float,
        captureRadius: Float,
        captureVelocity: Float
    ): Boolean =
        kotlin.math.abs(value - nearest(value)) <= captureRadius &&
            kotlin.math.abs(velocity) <= captureVelocity
}

/**
 * The seeded bodies — P0's landed `MonolithMotion` character (BUILD-SR-1,
 * PR #842), re-expressed as physics. Each body's ζ and normalised
 * stiffness equal P0's SpringSpec numbers exactly (asserted in
 * MotionBodyTest); the masses are this engine's assignment of what P0
 * described in words — bolt iron heavy, the knock deadest, the door
 * heaviest and softest. MOTION-SR-1..4's numbers replace this table as
 * data when they land; no other file changes.
 */
object MonolithBodies {

    /** The bolt drive: near-critically damped, very stiff — drives home and stops. */
    val Bolt: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 2.4f, dampingRatio = 0.92f, normalisedStiffness = 900f,
        settle = SettleSpec(distanceEpsilon = 0.002f, velocityEpsilon = 0.02f)
    )

    /** The knock: critically damped dead mass — one thud, zero overshoot. */
    val Knock: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 3.2f, dampingRatio = 1.0f, normalisedStiffness = 2600f,
        settle = SettleSpec(distanceEpsilon = 0.001f, velocityEpsilon = 0.01f)
    )

    /** The revolving drum: under-damped, caught by its detent. */
    val Drum: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.6f, dampingRatio = 0.78f, normalisedStiffness = 420f,
        settle = SettleSpec(distanceEpsilon = 0.002f, velocityEpsilon = 0.02f)
    )

    /** The door's opening weight: the heaviest body, the softest spring, the long settle. */
    val DoorWeight: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 4.5f, dampingRatio = 0.86f, normalisedStiffness = 55f,
        settle = SettleSpec(distanceEpsilon = 0.003f, velocityEpsilon = 0.01f)
    )

    /** The combination wheel: spun mass, freer, still caught. */
    val Wheel: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.2f, dampingRatio = 0.65f, normalisedStiffness = 180f,
        settle = SettleSpec(distanceEpsilon = 0.002f, velocityEpsilon = 0.03f)
    )

    /** The governor's crank: wound by hand against real resistance. */
    val Crank: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.8f, dampingRatio = 0.72f, normalisedStiffness = 300f,
        settle = SettleSpec(distanceEpsilon = 0.002f, velocityEpsilon = 0.02f)
    )
}
