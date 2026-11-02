package com.example.ui.motion

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * THE DOOR'S WEIGHT — MOTION-SR-1's complete door-motion spec, driven
 * (BUILD-SR-18, T24, Stage 12 composition).
 *
 * MOTION-SR-5's engine ([MotionBody] / [DecidedMotion] / [Choreography])
 * expresses springs. A spring cannot express WEIGHT: a spring-to-target
 * starts at maximum force — a door that leaps, then creeps — the exact
 * inverse of a heavy door, which starts with almost nothing (stiction,
 * then a declining imbalance torque) and is braked late. MOTION-SR-1
 * filed the extension this file implements — `DriveSpec(kind, p0, p1)`,
 * a drive term on the track — in its parameter letter of 2026-11-02;
 * the extension did NOT stand in the engine code when this lane read
 * it firsthand, so the implementation here is BUILD-SR-18's own,
 * built to MOTION-SR-1's figures exactly (door-motion-spec.md §§0–6,
 * simulated by its generate.py at 1 kHz semi-implicit Euler; this
 * file integrates the same bodies the same way, and DoorMotionTest
 * pins the outputs to the spec's sampled table).
 *
 * What this supersedes, BY NAME (MOTION-SR-1 §6):
 *  - P0 `MonolithMotion.DoorWeightSpring` (ζ 0.86 / k 55) — SUPERSEDED:
 *    ζ < 1 overshoots at the stop; a bounce. Replaced by the driven
 *    door body below (torque drive + arrival band + stop damper;
 *    simulated compression 0.09°, no rebound).
 *  - P0 `MonolithMotion.BoltSpring` (ζ 0.92 / k 900) — SUPERSEDED:
 *    a spring into a seat is a bounce class; seats are CLAMPS.
 *    Replaced by one constant drive through three masses.
 *  - P0 `SealClunks` / `LiftClunks` rhythms as TIMING sources —
 *    RETIRED for the ceremonies; the clocks below are MOTION-SR-1 §0
 *    (SOUND-SR-1's landed onsets, adopted there).
 *
 * Every figure is a DESIGN figure, UNPROVEN-ON-DEVICE (Audit A G-7).
 */

/** A drive term a spring cannot express (MOTION-SR-1's filed shape). */
data class DriveSpec(
    /** "decliningTorque" (the door) · "constantForce" (bolts, withdrawals). */
    val kind: String,
    /** decliningTorque: τ0, zero-at degrees in p1 · constantForce: the force. */
    val p0: Float,
    val p1: Float = 0f
)

/** One instant of a driven body's travel. */
data class DrivenState(val value: Float, val velocity: Float)

/**
 * The driven integrator: semi-implicit Euler at a fixed 1 ms step —
 * the same scheme and step as MOTION-SR-1's simulation, so the poses
 * this engine renders ARE the spec's sampled table, not a rendering
 * of it. Deterministic: same elapsed, same pose, every run.
 */
object DrivenIntegrator {
    const val STEP_SECONDS: Float = 0.001f
}

/** The door body (MOTION-SR-1 §1.1): one body, both swings. */
object DoorBody {
    const val INERTIA: Float = 30f
    val OPEN_DRIVE = DriveSpec("decliningTorque", p0 = 1900f, p1 = 88f)
    const val OPEN_RAMP_MS: Long = 500L
    const val RESISTANCE: Float = 15f
    const val BAND_FROM_DEG: Float = 66f
    const val BAND_RESISTANCE: Float = 95f
    const val STOP_FROM_DEG: Float = 74f
    const val STOP_STIFFNESS: Float = 800f
    const val STOP_RESISTANCE: Float = 260f
    const val REST_DEG: Float = 82f
    /** The strike fires at θ = 81.2° on the rising edge (spec §3). */
    const val STRIKE_DEG: Float = 81.2f
    val SETTLE = SettleSpec(distanceEpsilon = 0.5f, velocityEpsilon = 1.0f)

    /** The swing-shut hand tracker: critically damped (ζ derived = 1.000). */
    val TRACKER = PhysicsBody(mass = 30f, stiffness = 210f, resistance = 158.7f)
    const val MAX_OMEGA_DEG_S: Float = 170f
    /** Contact window: below 18°/s the door has not met its frame; above 45 it is a slam (journaled, still seals). */
    val CONTACT_OMEGA_WINDOW: ClosedFloatingPointRange<Float> = 18f..45f

    private fun smoothstep(x: Float): Float = x * x * (3f - 2f * x)

    /** Acceleration of the opening door at one instant of its travel. */
    fun openAcceleration(thetaDeg: Float, omegaDegS: Float, elapsedMs: Long): Float {
        var torque = 0f
        if (thetaDeg > STOP_FROM_DEG) {
            torque += -STOP_STIFFNESS * (thetaDeg - REST_DEG) - STOP_RESISTANCE * omegaDegS
        }
        val ramp = smoothstep(min(1f, elapsedMs.toFloat() / OPEN_RAMP_MS.toFloat()))
        val drive = OPEN_DRIVE.p0 * max(0f, 1f - thetaDeg / OPEN_DRIVE.p1) * ramp
        val bandK = min(1f, max(0f, (thetaDeg - BAND_FROM_DEG) / 10f))
        val resistance = RESISTANCE + BAND_RESISTANCE * bandK
        return (drive - resistance * omegaDegS + torque) / INERTIA
    }

    /** Simulate the whole opening swing once; the table is the test fixture and the player's source. */
    fun simulateOpen(maxMs: Long = 3800L): DoorOpenSimulation {
        var theta = 0f
        var omega = 0f
        var strikeMs: Long? = null
        var settleMs: Long? = null
        var peakOmega = 0f
        var peakAtMs = 0L
        var minOmegaAfterStrike = 0f
        val samples = ArrayList<DrivenState>((maxMs + 1).toInt())
        samples.add(DrivenState(theta, omega))
        var t = 0L
        while (t < maxMs) {
            if (strikeMs == null && theta >= STRIKE_DEG) strikeMs = t
            val a = openAcceleration(theta, omega, t)
            omega += a * DrivenIntegrator.STEP_SECONDS
            theta += omega * DrivenIntegrator.STEP_SECONDS
            t += 1L
            if (omega > peakOmega) { peakOmega = omega; peakAtMs = t }
            if (strikeMs != null && omega < minOmegaAfterStrike) minOmegaAfterStrike = omega
            if (settleMs == null && abs(theta - REST_DEG) <= SETTLE.distanceEpsilon &&
                abs(omega) <= SETTLE.velocityEpsilon
            ) settleMs = t
            samples.add(DrivenState(theta, omega))
        }
        if (strikeMs == null && theta >= STRIKE_DEG) strikeMs = t
        return DoorOpenSimulation(
            samplesPerMs = samples,
            strikeMs = strikeMs,
            settleMs = settleMs,
            peakOmegaDegS = peakOmega,
            peakOmegaAtMs = peakAtMs,
            finalThetaDeg = theta,
            minOmegaAfterStrikeDegS = minOmegaAfterStrike
        )
    }
}

/** The simulated opening swing: pose per millisecond, plus its named events. */
data class DoorOpenSimulation(
    val samplesPerMs: List<DrivenState>,
    /** Swing-relative ms at which θ first reaches the strike angle, or null if it never does. */
    val strikeMs: Long?,
    /** Swing-relative ms at which the body first stands inside its settle band. */
    val settleMs: Long?,
    val peakOmegaDegS: Float,
    val peakOmegaAtMs: Long,
    val finalThetaDeg: Float,
    /** Most negative ω after the strike — the rebound witness (spec: never beyond −0.5°/s). */
    val minOmegaAfterStrikeDegS: Float
) {
    /** θ at [elapsedMs] into the swing (clamped to the simulated span). */
    fun thetaAt(elapsedMs: Long): Float =
        samplesPerMs[elapsedMs.coerceIn(0L, (samplesPerMs.size - 1).toLong()).toInt()].value

    /** ω at [elapsedMs] into the swing. */
    fun omegaAt(elapsedMs: Long): Float =
        samplesPerMs[elapsedMs.coerceIn(0L, (samplesPerMs.size - 1).toLong()).toInt()].velocity
}

/**
 * A driven linear body with a CLAMP seat (MOTION-SR-1 §1.2/§1.3):
 * `m·x″ = F − c·x′` until x = 1, where the seat clamps x and zeroes
 * x′ in one frame — the seat is a decided state, not an asymptote,
 * and its SyncPoint fires at the clamp.
 */
object DrivenClampBody {

    data class Travel(val travelMs: Long, val arrivalVelocity: Float)

    /** Time for the body to reach x = 1 from rest under a constant drive. */
    fun travel(mass: Float, drive: DriveSpec, resistance: Float): Travel {
        require(drive.kind == "constantForce") { "a clamp body takes a constant force" }
        var x = 0f
        var v = 0f
        var t = 0L
        while (t < 2000L) {
            val a = (drive.p0 - resistance * v) / mass
            v += a * DrivenIntegrator.STEP_SECONDS
            x += v * DrivenIntegrator.STEP_SECONDS
            t += 1L
            if (x >= 1f) return Travel(travelMs = t, arrivalVelocity = v)
        }
        return Travel(travelMs = t, arrivalVelocity = v)
    }

    /** Travel fraction at [elapsedMs] — 0 before/at rest, exactly 1 at and after the clamp. */
    fun fractionAt(mass: Float, drive: DriveSpec, resistance: Float, elapsedMs: Long): Float {
        if (elapsedMs <= 0L) return 0f
        var x = 0f
        var v = 0f
        var t = 0L
        while (t < elapsedMs && t < 2000L) {
            val a = (drive.p0 - resistance * v) / mass
            v += a * DrivenIntegrator.STEP_SECONDS
            x += v * DrivenIntegrator.STEP_SECONDS
            t += 1L
            if (x >= 1f) return 1f
        }
        return min(1f, x)
    }
}

/** The bolts (MOTION-SR-1 §1.2): three bodies, ONE drive — the mass gradient causes the travel gradient. */
object BoltBodies {
    val DRIVE = DriveSpec("constantForce", p0 = 140f)
    const val RESISTANCE: Float = 16f
    /** In seating order: bolt 1 seats first, bolt 3 last and heaviest. */
    val MASSES: List<Float> = listOf(0.90f, 1.50f, 2.60f)
    val SETTLE = SettleSpec(distanceEpsilon = 0f + 0.0001f, velocityEpsilon = 0.0001f)

    fun travel(index: Int): DrivenClampBody.Travel = DrivenClampBody.travel(MASSES[index], DRIVE, RESISTANCE)
    fun fractionAt(index: Int, elapsedMs: Long): Float =
        DrivenClampBody.fractionAt(MASSES[index], DRIVE, RESISTANCE, elapsedMs)
}

/**
 * The withdrawals (MOTION-SR-1 §1.3): the same bodies freed in REVERSE
 * load order — heaviest first. Strain (drive ramping against a static
 * hold, travel ≤ 8%) → snap (break-free, the SyncPoint) → slide.
 * No withdrawal is a reversed bolt travel: drive, resistance and
 * phase structure all differ from the seating.
 */
object WithdrawalBodies {
    val DRIVE = DriveSpec("constantForce", p0 = 95f)
    const val RESISTANCE: Float = 10f
    const val STRAIN_MS: Long = 90L
    /** In withdrawal order: bolt 3's body first, bolt 1's last. */
    val MASSES: List<Float> = listOf(2.60f, 1.50f, 0.90f)

    fun slide(index: Int): DrivenClampBody.Travel = DrivenClampBody.travel(MASSES[index], DRIVE, RESISTANCE)

    /** Total span of one withdrawal from its strain start: strain + slide. */
    fun totalMs(index: Int): Long = STRAIN_MS + slide(index).travelMs

    /**
     * Travel fraction at [elapsedMs] from this withdrawal's strain
     * start: 0 through the strain (the bolt loads, ≤8% by spec — the
     * render holds at 0; the strain is a felt fact, carried by the
     * snap's sync point), then the slide's driven fraction.
     */
    fun fractionAt(index: Int, elapsedMs: Long): Float {
        if (elapsedMs <= STRAIN_MS) return 0f
        return DrivenClampBody.fractionAt(MASSES[index], DRIVE, RESISTANCE, elapsedMs - STRAIN_MS)
    }
}

/** The press (MOTION-SR-1 §1.4): one overdamped spring body (ζ derived 1.361, overshoot 0). */
object PressBody {
    val BODY = PhysicsBody(mass = 1f, stiffness = 700f, resistance = 72f)
    val SETTLE = SettleSpec(distanceEpsilon = 0.05f, velocityEpsilon = 0.10f)
    const val TRAVEL_DP: Float = 2.5f

    /** Simulate the press at 1 ms (the spec's scheme) → its settle moment and overshoot. */
    fun simulate(maxMs: Long = 600L): PressSimulation {
        var x = 0f
        var v = 0f
        var overshoot = 0f
        var settleMs: Long? = null
        var t = 0L
        while (t < maxMs) {
            val a = (-BODY.stiffness * (x - 1f) - BODY.resistance * v) / BODY.mass
            v += a * DrivenIntegrator.STEP_SECONDS
            x += v * DrivenIntegrator.STEP_SECONDS
            t += 1L
            if (x - 1f > overshoot) overshoot = x - 1f
            if (settleMs == null && abs(x - 1f) <= SETTLE.distanceEpsilon && abs(v) <= SETTLE.velocityEpsilon) {
                settleMs = t
            }
        }
        return PressSimulation(settleMs = settleMs, overshoot = overshoot, finalValue = x)
    }
}

data class PressSimulation(val settleMs: Long?, val overshoot: Float, val finalValue: Float)

/** The two ceremony clocks (MOTION-SR-1 §0) — monotonic elapsed ms, never wall-clock. */
object DoorScores {
    /** THE SEAL (t=0 = the swing's first frame, at the first study sitting). */
    object Seal {
        const val SWING_START: Long = 0L
        const val CONTACT: Long = 780L
        const val WHEEL_START: Long = 900L
        /** Bolt 1's break-free: past this, the Seal is a fact completing itself. */
        const val COMMIT: Long = 993L
        val BOLT_BREAK_FREE: List<Long> = listOf(993L, 1464L, 2101L)
        /** S-1..S-3: the seats (clamps), where the clunks fire. */
        val SYNC_CLUNKS: List<Long> = listOf(1160L, 1660L, 2340L)
        const val PRESS_START: Long = 2580L
        const val PRESS_SETTLED: Long = 3011L
        const val END: Long = 3100L
        val SYNC_POINTS: List<SyncPoint> = SYNC_CLUNKS.mapIndexed { i, at ->
            SyncPoint(atMs = at, kind = SyncKind.SOUND_AND_HAPTIC, name = "bolt-clunk-${i + 1}",
                weight = listOf(0.64f, 0.82f, 1.0f)[i])
        }
    }

    /** THE LIFTING (t=0 = the lifting hour's arrival on the elapsed span). */
    object Lifting {
        const val STILLNESS_END: Long = 600L
        /** L-1: the time-lock trips — journaled; once tripped it cannot un-trip. */
        const val SYNC_TRIP: Long = 600L
        const val UNSEAT_START: Long = 1120L
        val STRAIN_START: List<Long> = listOf(1350L, 1850L, 2350L)
        /** L-2..L-4: the snaps (break-free frames). */
        val SYNC_SNAPS: List<Long> = listOf(1440L, 1940L, 2440L)
        val HOUSED: List<Long> = listOf(1715L, 2161L, 2623L)
        const val SWING_START: Long = 2680L
        /** L-5: fired by the door's travel meeting its stop — θ = 81.2°, t = 5,574 ceremony-absolute. */
        const val SYNC_STRIKE: Long = 5574L
        const val DOOR_SETTLED: Long = 5926L
        const val END: Long = 6300L
        val SYNC_POINTS: List<SyncPoint> =
            listOf(SyncPoint(atMs = SYNC_TRIP, kind = SyncKind.SOUND_AND_HAPTIC, name = "time-lock-trip", weight = 0.5f)) +
                SYNC_SNAPS.mapIndexed { i, at ->
                    SyncPoint(atMs = at, kind = SyncKind.SOUND_AND_HAPTIC, name = "withdrawal-snap-${i + 1}",
                        weight = listOf(1.0f, 0.82f, 0.64f)[i])
                } +
                SyncPoint(atMs = SYNC_STRIKE, kind = SyncKind.SOUND_AND_HAPTIC, name = "plate-strike", weight = 1f)
    }
}

/** The decided states a ceremony can rest in (MOTION-SR-1's interruption table, §1). */
enum class DoorCeremonyState { OPEN_UNSEALED, SEALED, LIFTED, PLATE_STRUCK, AJAR_FAILED }

/**
 * The pure door player: poses as functions of the ceremony clock,
 * states as facts from the journal law. A surface renders what this
 * player says; it invents nothing (interruption table §0.2).
 */
class DoorMotionPlayer {

    private val openSimulation: DoorOpenSimulation by lazy { DoorBody.simulateOpen() }

    /** The opening door's simulated swing (strike/settle read from the bodies, not declared). */
    fun openSwing(): DoorOpenSimulation = openSimulation

    /** Door angle at [elapsedMs] into the LIFTING ceremony (0 before the swing starts). */
    fun liftingDoorAngleAt(elapsedMs: Long): Float {
        val local = elapsedMs - DoorScores.Lifting.SWING_START
        if (local <= 0L) return 0f
        return openSimulation.thetaAt(local)
    }

    /** True once the lifting's door has met its stop (the strike has fired by travel, not by timer). */
    fun liftingStrikeFiredAt(elapsedMs: Long): Boolean {
        val strike = openSimulation.strikeMs ?: return false
        return elapsedMs - DoorScores.Lifting.SWING_START >= strike
    }

    /** True once the lifting's door stands inside its settle band. */
    fun liftingDoorSettledAt(elapsedMs: Long): Boolean {
        val settle = openSimulation.settleMs ?: return false
        return elapsedMs - DoorScores.Lifting.SWING_START >= settle
    }

    /**
     * Withdrawal travel fraction for withdrawal [index] (0 = heaviest
     * first) at [elapsedMs] into the LIFTING ceremony.
     */
    fun liftingWithdrawalFractionAt(index: Int, elapsedMs: Long): Float =
        WithdrawalBodies.fractionAt(index, elapsedMs - DoorScores.Lifting.STRAIN_START[index])

    /** Bolt travel fraction for bolt [index] (seating order) at [elapsedMs] into the SEAL ceremony. */
    fun sealBoltFractionAt(index: Int, elapsedMs: Long): Float =
        BoltBodies.fractionAt(index, elapsedMs - DoorScores.Seal.BOLT_BREAK_FREE[index])

    /**
     * The interruption law (table §1, DEATH column): a ceremony that
     * dies resolves to a DECIDED state, never a partial one.
     * Seal: before COMMIT → OPEN_UNSEALED; at/after → SEALED
     * (completed unwitnessed; never replayed). Lifting: once the
     * time-lock has tripped (SYNC_TRIP) → LIFTED.
     */
    fun resolveSealOnDeath(elapsedMsAtDeath: Long): DoorCeremonyState =
        if (elapsedMsAtDeath < DoorScores.Seal.COMMIT) DoorCeremonyState.OPEN_UNSEALED
        else DoorCeremonyState.SEALED

    fun resolveLiftingOnDeath(elapsedMsAtDeath: Long): DoorCeremonyState =
        if (elapsedMsAtDeath < DoorScores.Lifting.SYNC_TRIP) DoorCeremonyState.SEALED
        else DoorCeremonyState.LIFTED
}

/**
 * Reduced motion (MOTION-SR-1 §5): sequence preserved, weight
 * surrendered — poses + cuts, seat/snap onsets UNSCALED, stillness
 * unchanged. The platform's raw duration scale is never applied to
 * the ceremony scores.
 */
object DoorReducedMotion {
    val SWING_SHUT_POSES_DEG: List<Float> = listOf(82f, 41f, 0f)
    const val SHUT_POSE_HOLD_MS: Long = 140L
    val SWING_OPEN_POSES_DEG: List<Float> = listOf(0f, 27f, 55f, 82f)
    const val OPEN_POSE_HOLD_MS: Long = 160L
    /** Each bolt renders in 2 steps (half-travel, seated) at its unscaled seat onset. */
    const val BOLT_STEPS: Int = 2

    /** The reduced swing-open's total span: 4 poses × 160 ms holds. */
    const val SWING_OPEN_TOTAL_MS: Long = 640L

    /** The reduced swing-open pose at [elapsedMs] (cuts between holds; the strike fires on the final cut). */
    fun swingOpenPoseAt(elapsedMs: Long): Float {
        if (elapsedMs < 0L) return SWING_OPEN_POSES_DEG.first()
        val index = (elapsedMs / OPEN_POSE_HOLD_MS).toInt().coerceIn(0, SWING_OPEN_POSES_DEG.lastIndex)
        return SWING_OPEN_POSES_DEG[index]
    }
}
