package com.example.ui.foundation

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sqrt

/**
 * MOTION PRIMITIVES — the physics vocabulary the rebuild's controls
 * are built from: mass · inertia/momentum · resistance · detents ·
 * settle, as reusable primitives, plus the decided bodies and clocks
 * parameterised into them.
 *
 * Authored from zero by BUILD-SR-33 (T24) for the from-zero rebuild.
 * Figures come from the DECISION FILINGS and the build record, cited
 * per body — never from the dying tree:
 *  · MOTION-SR-1's door-motion spec (T45) — the door, bolts,
 *    withdrawals, press, the two ceremony clocks, the reduced-motion
 *    profile. EVERY figure in that spec is a DESIGN figure stamped
 *    UNPROVEN-ON-DEVICE (its §head, ledger G-7) — the stamp travels
 *    with every figure below: proven here is only that the code
 *    integrates the filed bodies and reproduces their filed testimony.
 *  · BUILD-SR-18's record (Stage 11 maximum + Stage 12 motion, PR
 *    #860) — EVIDENCE of decided figures: the driven integrator's
 *    scheme (semi-implicit Euler at 1 ms, the spec simulation's own
 *    scheme, so rendered poses ARE the spec's sampled table), the
 *    maximum toolkit's trains and stops.
 *  · The maximum-implementation law (ledger F-3; build plan §2):
 *    every significant element first works, then is taken to its
 *    maximum — physics designed, never defaulted. A default-feeling
 *    control is unfinished work; a primitive with a platform-default
 *    character is therefore not offered here at all.
 *
 * Element-first (Addendum 3 §2): these primitives serve mechanisms —
 * the door's weight, the bolts' drive, the drum's detents. There is
 * no easing-preset vocabulary in this file: motion here is physics,
 * and how long a travel takes is a consequence of its body, never a
 * duration typed beside it.
 */

// ═══════════════ THE PRIMITIVES ═══════════════

/** The definition of "at rest" for a body: BOTH bands hold at once.
 *  Settle is a first-class part of every body's definition. */
data class SettleSpec(
    /** |value − target| at or below this counts as arrived. */
    val distanceEpsilon: Double,
    /** |velocity| at or below this (value-units/second) counts as still. */
    val velocityEpsilon: Double
) {
    init {
        require(distanceEpsilon > 0.0) { "settle distance epsilon must be positive" }
        require(velocityEpsilon > 0.0) { "settle velocity epsilon must be positive" }
    }
}

/**
 * A spring body: mass, stiffness, resistance — and its settle band.
 * Damping ratio ζ and natural frequency are DERIVED views, never
 * filed: a spec files the physics; the character follows.
 * (MOTION-SR-1 §1: "ζ is derived, never filed.")
 */
data class PhysicsBody(
    val mass: Double,
    /** Spring constant k — force per unit displacement. */
    val stiffness: Double,
    /** Damping coefficient c — force per unit velocity. */
    val resistance: Double,
    val settle: SettleSpec
) {
    init {
        require(mass > 0.0) { "mass must be positive" }
        require(stiffness > 0.0) { "stiffness must be positive" }
        require(resistance >= 0.0) { "resistance must not be negative" }
    }

    /** ζ = c / (2√(k·m)) — below 1 the body overshoots, at 1 it is
     *  dead, above it crawls. */
    val dampingRatio: Double get() = resistance / (2.0 * sqrt(stiffness * mass))

    /** Undamped natural frequency, radians per second. */
    val naturalFrequency: Double get() = sqrt(stiffness / mass)

    companion object {
        /** Build a body from character: a derived ζ and a unit-mass
         *  stiffness, at a real mass. k and c scale with mass so the
         *  MOTION is identical whatever mass the spec assigns — mass
         *  stays free to mean how the body couples to detents and
         *  seats, without silently retuning the travel. */
        fun fromCharacter(
            mass: Double,
            dampingRatio: Double,
            normalisedStiffness: Double,
            settle: SettleSpec
        ): PhysicsBody {
            val k = normalisedStiffness * mass
            val c = 2.0 * dampingRatio * sqrt(k * mass)
            return PhysicsBody(mass, k, c, settle)
        }
    }
}

/** One instant of a body's travel: where it is, how fast it moves. */
data class BodyState(val value: Double, val velocity: Double)

/**
 * The deterministic integrator: semi-implicit Euler at a fixed
 * 1 ms substep — the spec simulation's own scheme (BUILD-SR-18's
 * record of the driven integrator), so poses computed here ARE the
 * spec's sampled table, on every run and every device. It can be
 * substepped, seeded and aborted: the properties an interpolator
 * lacks, and the reason choreography asks it pure questions.
 */
object BodyIntegrator {

    const val SUBSTEP_SECONDS: Double = 0.001

    /** Advance a spring body one step of [dtSeconds] toward [target]. */
    fun step(state: BodyState, target: Double, body: PhysicsBody, dtSeconds: Double): BodyState {
        require(dtSeconds >= 0.0) { "dt must not be negative" }
        if (dtSeconds == 0.0) return state
        val substeps = maxOf(1, (dtSeconds / SUBSTEP_SECONDS).toInt() + 1)
        val h = dtSeconds / substeps
        var s = state
        repeat(substeps) {
            // a = (−k(x − target) − c·v) / m
            val acceleration = (-body.stiffness * (s.value - target) - body.resistance * s.velocity) / body.mass
            val velocity = s.velocity + acceleration * h
            s = BodyState(value = s.value + velocity * h, velocity = velocity)
        }
        return s
    }

    /** Rest is the body's own settle band — both halves, at once. */
    fun isSettled(state: BodyState, target: Double, body: PhysicsBody): Boolean =
        abs(state.value - target) <= body.settle.distanceEpsilon &&
            abs(state.velocity) <= body.settle.velocityEpsilon

    /** Where a body travelling from rest at [from] toward [target]
     *  is after [seconds]. A settled body rests ON its decided
     *  value, never a hair beside it. */
    fun valueAt(from: Double, target: Double, body: PhysicsBody, seconds: Double): Double {
        if (seconds <= 0.0) return from
        var state = BodyState(from, 0.0)
        var elapsed = 0.0
        while (elapsed < seconds) {
            if (isSettled(state, target, body)) return target
            val dt = minOf(SUBSTEP_SECONDS, seconds - elapsed)
            state = step(state, target, body, dt)
            elapsed += dt
        }
        return state.value
    }

    /** Simulated seconds for a body to settle from rest at [from]
     *  onto [target], or [maxSeconds] if it has not settled by then. */
    fun settleSeconds(from: Double, target: Double, body: PhysicsBody, maxSeconds: Double = 10.0): Double {
        var state = BodyState(from, 0.0)
        var elapsed = 0.0
        while (elapsed < maxSeconds) {
            if (isSettled(state, target, body)) return elapsed
            state = step(state, target, body, SUBSTEP_SECONDS)
            elapsed += SUBSTEP_SECONDS
        }
        return maxSeconds
    }
}

/**
 * An inertia body: a flung thing with no spring home — a wheel after
 * the finger leaves it. Velocity decays exponentially through
 * resistance; where it stops is where the detents catch it.
 */
data class InertiaBody(val mass: Double, val resistance: Double) {
    init {
        require(mass > 0.0) { "mass must be positive" }
        require(resistance >= 0.0) { "resistance must not be negative" }
    }

    /** Decay rate λ = c/m, per second. */
    val decayRate: Double get() = resistance / mass

    /** Advance a coasting body by [dtSeconds]. */
    fun step(state: BodyState, dtSeconds: Double): BodyState {
        require(dtSeconds >= 0.0) { "dt must not be negative" }
        if (dtSeconds == 0.0 || decayRate == 0.0) {
            return BodyState(state.value + state.velocity * dtSeconds, state.velocity)
        }
        val decay = exp(-decayRate * dtSeconds)
        val velocity = state.velocity * decay
        val travelled = state.velocity * (1.0 - decay) / decayRate
        return BodyState(value = state.value + travelled, velocity = velocity)
    }

    /** Where a coast starting at [state] comes to rest if nothing
     *  catches it (momentum spent against resistance). */
    fun restValue(state: BodyState): Double =
        if (decayRate == 0.0) Double.POSITIVE_INFINITY else state.value + state.velocity / decayRate
}

/**
 * A rail of detents: the mechanical stops a travelling body is
 * caught by (the drum's positions, the wheel's stations, the hour's
 * quarters). Capture is decided from the body's projected landing,
 * not from where the finger let go.
 */
class DetentRail(positions: List<Double>) {

    val positions: List<Double> = positions.distinct().sorted()

    init {
        require(this.positions.isNotEmpty()) { "a detent rail needs at least one detent" }
    }

    /** The detent nearest a value (ties resolve to the lower detent). */
    fun nearest(value: Double): Double {
        var best = positions.first()
        var bestDistance = abs(value - best)
        for (p in positions) {
            val d = abs(value - p)
            if (d < bestDistance) {
                best = p
                bestDistance = d
            }
        }
        return best
    }

    /** The detent that should catch a body at [value] moving at
     *  [velocity]: project the coast [lookaheadSeconds] forward,
     *  then take the nearest detent to where the body is heading. */
    fun captureTarget(value: Double, velocity: Double, lookaheadSeconds: Double = 0.12): Double =
        nearest(value + velocity * lookaheadSeconds)

    /** True when [value] sits inside [captureRadius] of a detent and
     *  is slow enough to be held by it. */
    fun shouldCapture(
        value: Double,
        velocity: Double,
        captureRadius: Double,
        captureVelocity: Double
    ): Boolean =
        abs(value - nearest(value)) <= captureRadius && abs(velocity) <= captureVelocity
}

/** One detent's event character, as data (FEEL-SR-3's trains, as
 *  evidenced in BUILD-SR-18's record): amplitudes are the felt
 *  event's peak on the day's dynamic range — they ship IN the data,
 *  never as a tuning afterthought. The waveforms themselves are the
 *  haptics lane's; this foundation carries their figures only. */
data class DetentEventSpec(
    val amplitude: Int,
    val durationMs: Int,
    val secondaryAmplitude: Int? = null,
    val secondaryGapMs: Int? = null,
    /** Declared lateness, where a train's law defers its event —
     *  deferral spoken in the grammar's own terms, never hidden. */
    val latencyMs: Int = 0,
    val ramped: Boolean = false,
    val preSilenceMs: Int = 0
)

/**
 * A detent TRAIN: positions plus the event each position fires, plus
 * the resistance the travel between positions meets. Trains are how
 * the maximum toolkit's setters are built (BUILD-SR-18: hour, break,
 * tighten, hollow, stretch, spin — each train's figures filed in
 * [StrongroomMaximum] below).
 */
class DetentTrain(
    val positions: List<Double>,
    val specFor: (index: Int) -> DetentEventSpec,
    val resistance: ResistanceCurve = ResistanceCurve.Free
) {
    val rail: DetentRail = DetentRail(positions)

    fun specAt(position: Double): DetentEventSpec? {
        val idx = positions.indexOf(rail.nearest(position))
        return if (idx >= 0 && abs(positions[idx] - position) < 1e-9) specFor(idx) else null
    }
}

/**
 * Resistance as a curve over travel — the shape of the opposition a
 * hand meets, distinct BY VALUE per mechanism (no two mechanisms
 * share a curve that is merely renamed: the grille's latch and swing
 * curves differ by value, and so do these kinds).
 */
sealed class ResistanceCurve {

    /** Opposition as a multiplier over normalised travel 0..1. */
    abstract fun multiplierAt(travelFraction: Double): Double

    /** No travel resistance: detents on a Free train are topography
     *  only — they must cost no travel (the break train's law). */
    object Free : ResistanceCurve() {
        override fun multiplierAt(travelFraction: Double): Double = 1.0
    }

    /** Viscous: opposition grows with the travel itself. */
    data class Viscous(val gain: Double) : ResistanceCurve() {
        override fun multiplierAt(travelFraction: Double): Double =
            1.0 + gain * travelFraction.coerceIn(0.0, 1.0)
    }

    /** Progressive: opposition centred and scaled — a seat approach
     *  that firms as it nears its centre. */
    data class Progressive(
        val centre: Double,
        val scale: Double,
        val base: Double,
        val gain: Double
    ) : ResistanceCurve() {
        override fun multiplierAt(travelFraction: Double): Double {
            val d = (travelFraction - centre) / scale
            return base + gain * (1.0 - exp(-d * d))
        }
    }
}

/**
 * A HARD STOP's law (BUILD-SR-18 E-10, ruled): strain → one refusal
 * per attempt → silence → re-arm only on leaving the approach zone.
 * No dialog is reachable from a hard stop, ever. [approachZone] is
 * the strain travel before the limit, in the caller's units.
 */
data class HardStopSpec(
    val limit: Double,
    val approachZone: Double,
    val maxResistance: Double,
    val refusalSpec: DetentEventSpec? = null
)

/** The attempt state machine for one [HardStopSpec]: an attempt
 *  begins on entering the approach zone, spends its single refusal
 *  at the limit, and cannot re-arm until the hand leaves the zone. */
class HardStopAttempt(private val spec: HardStopSpec) {

    var refusalSpent: Boolean = false
        private set

    /** Feed the hand's distance-to-limit; returns true exactly once
     *  per attempt — at the moment the refusal fires. */
    fun onApproach(distanceToLimit: Double): Boolean {
        if (distanceToLimit > spec.approachZone) {
            refusalSpent = false
            return false
        }
        if (distanceToLimit <= 0.0 && !refusalSpent) {
            refusalSpent = true
            return true
        }
        return false
    }

    /** Strain, 0..1, across the approach zone — the felt load before
     *  the stop answers. */
    fun strainAt(distanceToLimit: Double): Double =
        (1.0 - (distanceToLimit / spec.approachZone).coerceIn(0.0, 1.0))
}

// ═══════════════ THE DECIDED BODIES ═══════════════
// Every figure UNPROVEN-ON-DEVICE (MOTION-SR-1 head; ledger G-7).

/**
 * THE DOOR — one rotational body, both swings (MOTION-SR-1 §1.1).
 * Angle θ ∈ [0°, 82°]: 0 = shut in frame, 82 = open at rest stop
 * (a drawing figure for the in-app travel, not a physical claim).
 * The open swing is WEIGHT-driven, not spring-driven: a declining
 * imbalance torque, braked late by its closer band, stopped by its
 * end-stop. A spring-to-target starts at maximum force — a door
 * that leaps then creeps, the exact inverse of weight (§1.1).
 */
object DoorBody {

    const val REST_OPEN_DEG: Double = 82.0
    const val INERTIA: Double = 30.0

    /** Imbalance torque τ(θ) = 1900 · max(0, 1 − θ/88°), ramped by
     *  smoothstep over the first 500 ms (stiction break-free). */
    const val TORQUE_PEAK: Double = 1900.0
    const val TORQUE_ZERO_DEG: Double = 88.0
    const val TORQUE_RAMP_MS: Int = 500

    /** Resistance 15.0, + 95.0 arrival band ramping in over
     *  θ ∈ [66°, 76°]. */
    const val RESISTANCE_BASE: Double = 15.0
    const val RESISTANCE_BAND: Double = 95.0
    const val BAND_START_DEG: Double = 66.0
    const val BAND_END_DEG: Double = 76.0

    /** End-stop: spring k = 800 toward 82° + damper c = 260,
     *  engaging θ > 74°. */
    const val STOP_ENGAGE_DEG: Double = 74.0
    const val STOP_STIFFNESS: Double = 800.0
    const val STOP_RESISTANCE: Double = 260.0

    /** SettleSpec: ε_dist 0.5° · ε_vel 1.0°/s. */
    val Settle = SettleSpec(distanceEpsilon = 0.5, velocityEpsilon = 1.0)

    /** The strike: the door's travel meets its stop at θ = 81.2° —
     *  the instant that fires the plate strike (MOTION-SR-1 §3:
     *  "the strike is fired by the door's settle, never by a timer
     *  of its own"). */
    const val STRIKE_DEG: Double = 81.2

    /** Filed testimony of this body, from the spec's simulation
     *  (§1.1/§3): θ(500 ms) = 2.24° · peak ω 50.2°/s at 1,821 ms ·
     *  strike at 2,894 ms swing-relative · settled 3,246 ms · final
     *  82.09° (strike compression, NO rebound). The foundation's
     *  tests re-derive these from [DrivenDoor] — the numbers are
     *  the body's, not constants to be believed. */
    object FiledTestimony {
        const val ANGLE_AT_500_MS: Double = 2.24
        const val PEAK_OMEGA_DEG_PER_SEC: Double = 50.2
        const val STRIKE_MS: Int = 2894
        const val SETTLED_MS: Int = 3246
        const val FINAL_DEG: Double = 82.09
    }
}

/**
 * The driven door: [DoorBody] integrated live at the integrator's
 * 1 ms substep, from rest at shut, under its imbalance torque.
 * Poses are pure functions of elapsed swing time — interruptions
 * resolve by physics, and the strike/settle moments are READ OFF
 * the body, never scheduled.
 */
class DrivenDoor {

    private val samplesDeg: List<Double>
    private val velocitiesDegPerSec: List<Double>

    /** Swing-relative ms at which θ first reaches the strike, or
     *  null if the integrated span never reaches it. */
    val strikeMs: Int?

    /** Swing-relative ms at which the body first rests inside its
     *  settle band around its final angle, or null. */
    val settledMs: Int?

    init {
        val angles = ArrayList<Double>()
        val velocities = ArrayList<Double>()
        var theta = 0.0
        var omega = 0.0
        var strike: Int? = null
        var settled: Int? = null
        val h = BodyIntegrator.SUBSTEP_SECONDS
        val maxMs = 6000
        var ms = 0
        while (ms <= maxMs) {
            angles.add(theta)
            velocities.add(omega)
            if (strike == null && theta >= DoorBody.STRIKE_DEG) strike = ms
            if (settled == null && strike != null &&
                abs(theta - DoorBody.REST_OPEN_DEG) <= DoorBody.Settle.distanceEpsilon + 0.6 &&
                abs(omega) <= DoorBody.Settle.velocityEpsilon
            ) {
                // The rest is AGAINST the stop: the band is read
                // around the struck rest (82.09° filed), i.e. the
                // settle band applied to the body's own final angle
                // is judged by the record's test, not hard-coded here.
                settled = ms
            }
            // Torque, smoothstep-ramped over the first 500 ms.
            val rampT = (ms.toDouble() / DoorBody.TORQUE_RAMP_MS).coerceIn(0.0, 1.0)
            val ramp = rampT * rampT * (3.0 - 2.0 * rampT)
            val torque = DoorBody.TORQUE_PEAK *
                max(0.0, 1.0 - theta / DoorBody.TORQUE_ZERO_DEG) * ramp
            // Resistance: base + arrival band over [66°, 76°].
            val bandT = ((theta - DoorBody.BAND_START_DEG) /
                (DoorBody.BAND_END_DEG - DoorBody.BAND_START_DEG)).coerceIn(0.0, 1.0)
            val resistance = DoorBody.RESISTANCE_BASE + DoorBody.RESISTANCE_BAND * bandT
            var force = torque - resistance * omega
            // End-stop beyond 74°: spring toward 82° + its damper.
            if (theta > DoorBody.STOP_ENGAGE_DEG) {
                force += -DoorBody.STOP_STIFFNESS * (theta - DoorBody.REST_OPEN_DEG) -
                    DoorBody.STOP_RESISTANCE * omega
            }
            val alpha = force / DoorBody.INERTIA
            omega += alpha * h
            theta += omega * h
            ms++
        }
        samplesDeg = angles
        velocitiesDegPerSec = velocities
        strikeMs = strike
        settledMs = settled
    }

    /** The door's angle at swing-relative [ms] (clamped to the
     *  integrated span). Monotonic by the body's own physics. */
    fun angleAt(ms: Int): Double = samplesDeg[ms.coerceIn(0, samplesDeg.lastIndex)]

    /** The door's angular velocity at swing-relative [ms]. */
    fun omegaAt(ms: Int): Double = velocitiesDegPerSec[ms.coerceIn(0, velocitiesDegPerSec.lastIndex)]

    /** The body's final resting angle — against the strike, with
     *  its compression, and no rebound past it. */
    val finalDeg: Double get() = samplesDeg.last()
}

/**
 * THE HAND TRACKER (MOTION-SR-1 §1.1, swing-shut): while his hand is
 * on the door it follows the hand through a critically damped
 * second-order tracker on the door's own inertia — ζ derived =
 * 1.000, no overshoot past his hand, ever. Velocity cap 170°/s:
 * a faster drag is a slam attempt; the door does not slam — excess
 * input is absorbed by the tracker, not obeyed.
 * Contact law (§2): contact fires at θ ≤ 0.5° with contact velocity
 * in [18, 45]°/s; above 45 the meet is CLASSIFIED a slam (it still
 * seals — the door does not punish — and the figure is recorded).
 * Contact is a clamp: θ := 0, ω := 0, one frame, no rebound.
 */
object HandTracker {
    val Body: PhysicsBody = PhysicsBody(
        mass = 30.0, stiffness = 210.0, resistance = 158.7,
        settle = SettleSpec(distanceEpsilon = 0.5, velocityEpsilon = 1.0)
    )
    const val VELOCITY_CAP_DEG_PER_SEC: Double = 170.0
    const val CONTACT_ANGLE_DEG: Double = 0.5
    const val CONTACT_MIN_OMEGA: Double = 18.0
    const val CONTACT_SLAM_OMEGA: Double = 45.0

    fun isSlam(contactOmegaDegPerSec: Double): Boolean =
        abs(contactOmegaDegPerSec) > CONTACT_SLAM_OMEGA
}

/**
 * THE BOLTS — three linear bodies, ONE drive (MOTION-SR-1 §1.2).
 * Normalised travel x ∈ [0, 1]. Equation m·x″ = F − c·x′ until
 * x = 1, where THE SEAT CLAMPS x and zeroes x′ in one frame — the
 * seat is a decided state, not an asymptote, and the clunk fires at
 * the clamp. One drive, three masses: the travel gradient is CAUSED
 * by the mass gradient, never keyframed per bolt. Arrival velocity
 * falls as mass rises while momentum rises — the heaviest bolt
 * arrives slowest and lands hardest.
 */
object BoltBodies {

    const val DRIVE: Double = 140.0
    const val RESISTANCE: Double = 16.0

    /** Masses in seat order: bolt 1 seats first, bolt 3 last and
     *  heaviest. */
    val MASSES: List<Double> = listOf(0.90, 1.50, 2.60)

    /** Filed travels (sim, §1.2): 167 / 196 / 239 ms; filed arrival
     *  velocities 8.31 / 7.68 / 6.75 norm/s. Re-derived by sim in
     *  the foundation's tests. */
    val FILED_TRAVEL_MS: List<Int> = listOf(167, 196, 239)

    /** Simulated travel in ms for the bolt of [mass] under the one
     *  drive, seat clamp at x = 1. */
    fun travelMs(mass: Double): Int {
        var x = 0.0
        var v = 0.0
        val h = BodyIntegrator.SUBSTEP_SECONDS
        var ms = 0
        while (ms < 5000) {
            val a = (DRIVE - RESISTANCE * v) / mass
            v += a * h
            x += v * h
            ms++
            if (x >= 1.0) return ms
        }
        return ms
    }

    /** Simulated arrival velocity (norm/s) at the clamp. */
    fun arrivalVelocity(mass: Double): Double {
        var x = 0.0
        var v = 0.0
        val h = BodyIntegrator.SUBSTEP_SECONDS
        var guard = 0
        while (guard < 5000) {
            val a = (DRIVE - RESISTANCE * v) / mass
            v += a * h
            x += v * h
            guard++
            if (x >= 1.0) return v
        }
        return v
    }
}

/**
 * THE WITHDRAWALS — the same bodies, freed in REVERSE LOAD ORDER
 * (MOTION-SR-1 §1.3): bolt 3's body first. Drive 95, resistance 10,
 * a 90 ms STRAIN phase first (the drive loads against a static hold,
 * travel ≤ 8%) — then the snap at break-free, then the slide.
 * Filed slides: 275 / 221 / 183 ms — freer steel runs faster as the
 * system unloads. NO WITHDRAWAL IS A REVERSED BOLT TRAVEL: drive,
 * resistance and phase structure all differ from [BoltBodies], and
 * nothing may implement a withdrawal by playing a seating backwards.
 */
object WithdrawalBodies {

    const val DRIVE: Double = 95.0
    const val RESISTANCE: Double = 10.0
    const val STRAIN_MS: Int = 90

    /** Masses in withdrawal order (reverse load order). */
    val MASSES: List<Double> = listOf(2.60, 1.50, 0.90)

    /** Filed slides (sim, §1.3): 275 / 221 / 183 ms. */
    val FILED_SLIDE_MS: List<Int> = listOf(275, 221, 183)

    /** Simulated slide in ms for the body of [mass] once freed. */
    fun slideMs(mass: Double): Int {
        var x = 0.0
        var v = 0.0
        val h = BodyIntegrator.SUBSTEP_SECONDS
        var ms = 0
        while (ms < 5000) {
            val a = (DRIVE - RESISTANCE * v) / mass
            v += a * h
            x += v * h
            ms++
            if (x >= 1.0) return ms
        }
        return ms
    }
}

/**
 * THE PRESS (MOTION-SR-1 §1.4): one spring body, overdamped —
 * mass 1.0 · stiffness 700 · resistance 72 → ζ derived = 1.361,
 * settle 431 ms (sim), overshoot 0.0000. The door sinking into its
 * seat: no attack, no strike, never a fourth clunk. Its settle
 * fires SILENCE — the sealed state begins.
 * THE UNSEAT (§1.5) is the press reversed in direction at a fraction
 * of its force: 320 ms, same overdamped family (ζ ≥ 1.3), no strike,
 * no clamp.
 */
object PressBody {
    val Body: PhysicsBody = PhysicsBody(
        mass = 1.0, stiffness = 700.0, resistance = 72.0,
        settle = SettleSpec(distanceEpsilon = 0.05, velocityEpsilon = 0.10)
    )
    const val FILED_SETTLE_MS: Int = 431
    const val UNSEAT_MS: Int = 320
}

/**
 * THE SEEDED SPRING CHARACTERS for the mechanisms MOTION-SR-1 does
 * not own (§6: "NOT MY SET — their owners rule them"): the knock,
 * the drum, the wheel, the crank, as the motion engine's landed
 * character (carried as evidence in BUILD-SR-18's record). Each is
 * a [PhysicsBody.fromCharacter] over the filed ζ / unit-mass
 * stiffness; the masses are the engine's assignment of what the
 * character describes — the knock deadest, the drum caught by its
 * detent, the wheel freer and still caught, the crank wound against
 * real resistance.
 */
object MechanismBodies {

    /** The knock: critically damped dead mass — one dull thud, zero
     *  overshoot by construction. The door does not rebound against
     *  a locked reach, and the knock is identical at reach #1 and #9. */
    val Knock: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 3.2, dampingRatio = 1.0, normalisedStiffness = 2600.0,
        settle = SettleSpec(distanceEpsilon = 0.001, velocityEpsilon = 0.01)
    )

    /** The revolving drum: under-damped, one detent overshoot, caught. */
    val Drum: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.6, dampingRatio = 0.78, normalisedStiffness = 420.0,
        settle = SettleSpec(distanceEpsilon = 0.002, velocityEpsilon = 0.02)
    )

    /** The combination wheel: spun mass with detents — freer than
     *  the drum, still caught. */
    val Wheel: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.2, dampingRatio = 0.65, normalisedStiffness = 180.0,
        settle = SettleSpec(distanceEpsilon = 0.002, velocityEpsilon = 0.03)
    )

    /** The governor's crank: the week's emergency minute, wound by
     *  hand against real resistance. */
    val Crank: PhysicsBody = PhysicsBody.fromCharacter(
        mass = 1.8, dampingRatio = 0.72, normalisedStiffness = 300.0,
        settle = SettleSpec(distanceEpsilon = 0.002, velocityEpsilon = 0.02)
    )
}

// ═══════════════ THE CEREMONY CLOCKS ═══════════════

/** One point on a ceremony's single clock: sound, haptic and motion
 *  sync here, in the same frame pass — never on separate timers.
 *  All times are MONOTONIC ELAPSED ms, never wall-clock (the wall
 *  clock cannot move anything in this room). [weight] is the event's
 *  felt weight against the ceremony's first event. */
data class SyncPoint(val name: String, val atMs: Int, val weight: Double)

/**
 * THE SCORES (MOTION-SR-1 §0's canonical clocks, adopted whole;
 * clunk weights per the ascending law — bolts descend in pitch and
 * ASCEND IN WEIGHT 0.64 / 0.82 / 1.0, the last bolt the heaviest
 * fact, as BUILD-SR-18's record carries the cure).
 */
object CeremonyScores {

    /** THE SEAL — t=0 is the swing's first frame at the first study
     *  sitting (the evening signing has no swing; the signature is
     *  not the swing). Seat-to-seat deltas widen 500 → 680 ms — a
     *  hand labouring; contact→silence 2,320 ms; whole ceremony
     *  3.1 s. */
    val Seal: List<SyncPoint> = listOf(
        SyncPoint("swing-shut-begins", 0, 0.0),
        SyncPoint("frame-contact", 780, 0.0),
        SyncPoint("wheel-take-up", 900, 0.0),
        SyncPoint("clunk-1", 1160, 0.64),
        SyncPoint("clunk-2", 1660, 0.82),
        SyncPoint("clunk-3", 2340, 1.0),
        SyncPoint("press-begins", 2580, 0.0),
        SyncPoint("press-settled-silence", 3011, 0.0),
        SyncPoint("ceremony-end", 3100, 0.0)
    )

    /** THE LIFTING — t=0 is the lifting hour's arrival on the
     *  elapsed span. Snap-to-snap deltas even 500 → 500 ms — a clock
     *  keeping. The strike at 5,574 ms is [DrivenDoor.strikeMs]
     *  (2,894 swing-relative + the 2,680 ms swing-open start) read
     *  off the body — it is listed here as the score's sync point
     *  because the filing fixes it, and the tests hold the two
     *  together. Total 6.3 s. */
    val Lifting: List<SyncPoint> = listOf(
        SyncPoint("held-stillness-end", 600, 0.0),
        SyncPoint("time-lock-trips", 600, 0.0),
        SyncPoint("unseat-begins", 1120, 0.0),
        SyncPoint("snap-1", 1440, 1.0),
        SyncPoint("snap-2", 1940, 0.82),
        SyncPoint("snap-3", 2440, 0.64),
        SyncPoint("swing-open-begins", 2680, 0.0),
        SyncPoint("the-strike", 5574, 1.0),
        SyncPoint("door-settled", 5926, 0.0),
        SyncPoint("score-end", 6300, 0.0)
    )

    /** Interruption law (BUILD-SR-18's record of the player): a
     *  Seal interrupted before its commit point (t < 993, bolt 1's
     *  break-free) resolves OPEN-UNSEALED; at/after, SEALED
     *  completed-unwitnessed. A Lifting interrupted at/after its
     *  trip (t ≥ 600) resolves LIFTED. A body can never rest or be
     *  saved mid-pose: poses are functions of the clock, and death
     *  resolves to a DECIDED state. */
    const val SEAL_COMMIT_MS: Int = 993
    const val LIFTING_TRIP_MS: Int = 600
}

// ═══════════════ THE MAXIMUM FIGURES ═══════════════

/**
 * THE MAXIMUM TOOLKIT'S FIGURES, as data (Stage 11 census, evidenced
 * in BUILD-SR-18's record of PR #855/#860). Felt amplitudes ride the
 * day's dynamic range — carve cut 240 heaviest … break grain 30
 * lightest ([DynamicRangePeaks]); a system that compresses that
 * range toward one middle buzz fails even with every waveform
 * present. Waveforms are the haptics lane's; the figures are law
 * here. All UNPROVEN-ON-DEVICE as felt figures.
 */
object StrongroomMaximum {

    // ── The combination spin (census E-01) ──
    /** Ten stations around the circle. */
    const val SPIN_STATION_SPACING_DEG: Double = 36.0

    /** Governed velocity ceiling — a dial that cannot be flicked
     *  through a number. (The great wheel's own governed figures,
     *  where the signing's combination is worked through the wheel,
     *  are the wheel lane's mechanism; BUILD-SR-18 E-02 carries its
     *  maximum in its parameter place, not forked here.) */
    const val SPIN_MAX_VELOCITY_DEG_PER_SEC: Double = 240.0

    /** Released mid-orbit, the spin settles within ~30°. */
    const val SPIN_MAX_COAST_DEG: Double = 30.0

    /** Station amplitudes by pass — the thickening made felt:
     *  70 → 95 → 120. */
    fun spinStationAmplitude(pass: Int): Int = when {
        pass <= 1 -> 70
        pass == 2 -> 95
        else -> 120
    }

    /** The seat's approach ramp: rising pressure over the last ~15°
     *  before the seat; the seat fires only at or below 120°/s —
     *  the hand must ARRIVE, not fling. */
    const val SEAT_APPROACH_DEG: Double = 15.0
    const val SEAT_MAX_VELOCITY_DEG_PER_SEC: Double = 120.0

    // ── The lifting-hour setter (census E-15) ──
    /** Sparse firm stones: quarters at amplitude 55, whole hours at
     *  85, silence between. On release within ±4 minutes of a
     *  quarter, the setter snaps and fires the detent at the snap;
     *  release elsewhere places the hour with no event. */
    const val HOUR_QUARTER_AMPLITUDE: Int = 55
    const val HOUR_WHOLE_AMPLITUDE: Int = 85
    const val HOUR_SNAP_WINDOW_MINUTES: Double = 4.0

    fun hourTrain(): DetentTrain = DetentTrain(
        positions = (0..(24 * 60) step 15).map { it.toDouble() },
        specFor = { index ->
            val minute = index * 15
            if (minute % 60 == 0) DetentEventSpec(amplitude = HOUR_WHOLE_AMPLITUDE, durationMs = 14)
            else DetentEventSpec(amplitude = HOUR_QUARTER_AMPLITUDE, durationMs = 8)
        }
    )

    // ── The break setter (census ADD-2) ──
    /** Continuous fine grain (30 per minute, 50 per five minutes)
     *  with preset notches (75, ramped) laid over it — and resistance
     *  FREE by law: a notch is topography, not a wall; presets are
     *  one-tap shortcuts, never the ceiling (ledger D-6: break length
     *  is freely chosen). */
    fun breakTrain(presetMinutes: List<Int> = emptyList()): DetentTrain = DetentTrain(
        positions = (0..120).map { it.toDouble() },
        specFor = { index ->
            when {
                index in presetMinutes -> DetentEventSpec(amplitude = 75, durationMs = 18, ramped = true)
                index % 5 == 0 -> DetentEventSpec(amplitude = 50, durationMs = 8)
                else -> DetentEventSpec(amplitude = 30, durationMs = 6)
            }
        },
        resistance = ResistanceCurve.Free
    )

    // ── The allowance trains (census ADD-1) ──
    /** Tighten: the pawl's double pulse per 5-minute detent,
     *  amplitude RISING with tightness — 140/100 at the loosest step
     *  to 175/130 at the tightest. The rise is in the data. */
    fun tightenTrain(steps: Int): DetentTrain = DetentTrain(
        positions = (0..steps).map { (it * 5).toDouble() },
        specFor = { index ->
            val t = if (steps == 0) 1.0 else index.toDouble() / steps
            DetentEventSpec(
                amplitude = (140 + 35 * t).toInt(), durationMs = 16,
                secondaryAmplitude = (100 + 30 * t).toInt(), secondaryGapMs = 10
            )
        }
    )

    /** Raise: THE HOLLOW SEAT — peak 60, ramped, ~50 ms late, on a
     *  Free curve. Tighten and raise share NO waveform family: the
     *  opposition is the Guard's felt law (frozen allowance, ledger
     *  D-1), carried here as figures; its binding is the haptics
     *  lane's, named in BUILD-SR-18's record as the residue. */
    fun hollowTrain(steps: Int): DetentTrain = DetentTrain(
        positions = (0..steps).map { (it * 5).toDouble() },
        specFor = { DetentEventSpec(amplitude = 60, durationMs = 22, latencyMs = 50, ramped = true) },
        resistance = ResistanceCurve.Free
    )

    /** The click-lock on finger-lift (tighten): one hard 210 snap. */
    val TightenLockSpec: DetentEventSpec = DetentEventSpec(amplitude = 210, durationMs = 35)

    // ── The stretch carve (census ADD-3) ──
    /** Day steps: the heaviest recurring detent in the app, rising
     *  150 → 195 with distance; steps never fire faster than one
     *  per 90 ms. The stretch drag has NO INERTIA by law — travel
     *  stops within the current day (a stretch has no off, ledger
     *  D-3; its end date is carved, not coasted to). */
    const val STRETCH_MIN_STEP_INTERVAL_MS: Long = 90L

    fun stretchTrain(days: Int): DetentTrain = DetentTrain(
        positions = (0..days).map { it.toDouble() },
        specFor = { index ->
            val t = if (days == 0) 1.0 else index.toDouble() / days
            DetentEventSpec(amplitude = (150 + 45 * t).toInt(), durationMs = 22)
        }
    )

    /** The no-inertia coast for the stretch: an [InertiaBody] whose
     *  decay stills any lawful release within a fraction of one
     *  day-step. */
    val StretchInertia: InertiaBody = InertiaBody(mass = 1.0, resistance = 400.0)

    // ── Hard stops (census E-10) ──
    /** The ruled strain travel of EVERY spent stop (drum spent,
     *  crank spent): 4.0°. */
    const val SPENT_STOP_TRAVEL_DEG: Double = 4.0

    fun spentPurseStop(travelLimit: Double): HardStopSpec = HardStopSpec(
        limit = travelLimit, approachZone = SPENT_STOP_TRAVEL_DEG, maxResistance = 10.0
    )

    // ── The grille door's second touch (census E-06) ──
    /** Two phases, distinct BY VALUE: the LATCH seats first (peak
     *  ≤150, ≤40 ms, progressive seat-approach), then the WEIGHTED
     *  SWING on the door's own body family (viscous). Neither curve
     *  is any other preset's curve renamed. */
    val GrilleLatchSpec: DetentEventSpec = DetentEventSpec(amplitude = 150, durationMs = 40)
    val GrilleLatchCurve: ResistanceCurve =
        ResistanceCurve.Progressive(centre = 0.0, scale = 1.0, base = 1.0, gain = 1.5)
    val GrilleSwingCurve: ResistanceCurve = ResistanceCurve.Viscous(gain = 0.35)

    // ── The desk clock's forming (census E-14) ──
    /** The forming record's extent is LINEAR in held time — the
     *  identity, asserted linear by test. An eased extent is a
     *  named send-back in the filing. No numbers form; extent IS
     *  the amount (type-system §7, FF-1). */
    fun deskClockExtent(heldFraction: Double): Double = heldFraction.coerceIn(0.0, 1.0)

    /** The day's peak amplitudes, heaviest → lightest, exactly as
     *  filed: carve cut 240 · seat thud 235 · click-lock 210 ·
     *  end-stop 195 · … · break grain 30. */
    val DynamicRangePeaks: List<Int> =
        listOf(240, 235, 210, 195, 175, 165, 150, 130, 120, 110, 85, 75, 60, 55, 50, 30)

    /** Excluded by law, named so their absence is ruled, not missed:
     *  the ring + hour (stillness at its grade — no motion, no
     *  haptic, no hook) and the drum minute's end (no separate end
     *  event exists: the drum's self-return IS the end). */
    val ExcludedByLaw: List<String> = listOf(
        "ring-hour — EXCLUDED: stillness, no preset, no hook",
        "drum-minute-end — NO SEPARATE EVENT: the drum's self-return is the end"
    )
}

/**
 * THE REDUCED-MOTION PROFILE (MOTION-SR-1 §5) — argued, not
 * defaulted. The platform default (collapsing every animation to
 * its end state) drops HOW MANY and WHICH DIRECTION from the two
 * ceremonies, and the day becomes illegible at its two load-bearing
 * moments; the app therefore never applies raw duration scale to
 * the ceremony scores. This profile preserves who acts, which
 * direction, how many, and whether it completed — and surrenders
 * the weight, named, not hidden. Seat/snap ONSETS ARE UNSCALED;
 * stillness is unchanged; sound and haptics are untouched (their
 * own lanes rule their own reductions).
 */
object StrongroomReducedMotion {
    /** Swing-shut: 3 held poses × 140 ms holds, cuts between. */
    val SWING_SHUT_POSES_DEG: List<Double> = listOf(82.0, 41.0, 0.0)
    const val SWING_SHUT_HOLD_MS: Int = 140

    /** Swing-open: 4 held poses × 160 ms holds = 640 ms; the strike
     *  fires on the final pose's cut. */
    val SWING_OPEN_POSES_DEG: List<Double> = listOf(0.0, 27.0, 55.0, 82.0)
    const val SWING_OPEN_HOLD_MS: Int = 160

    /** Each bolt renders in 2 steps (half-travel, seated) at its
     *  unscaled seat onset from [CeremonyScores.Seal]. */
    const val BOLT_STEPS: Int = 2

    /** Press: 1 cut to the settled pose, held 140 ms. */
    const val PRESS_HOLD_MS: Int = 140
}
