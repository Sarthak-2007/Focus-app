package com.example.ui.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * DECIDED MOTION — interruption semantics, enforced in the API (M-2).
 *
 * Law: an interrupted animation resolves to a DECIDED state only. This
 * file makes the law structural rather than conventional:
 *
 *  - An animation is created with the full set of states it may rest
 *    in ([DecidedStates]). There is deliberately NO API that animates
 *    to a bare float, and none that stops "wherever it is": the only
 *    targets this API accepts are decided states.
 *  - Interruption means RETARGETING: a new decided target is declared
 *    and the body travels there carrying its current velocity. The
 *    state it was resting in remains the resting state until the new
 *    target is actually reached.
 *  - Persistence can only ever hold a decided state: the saveable
 *    state mid-flight is the in-flight TARGET (where the motion had
 *    already decided to end), never the mid-pose. Process death
 *    therefore restores a decided state by construction. A legacy or
 *    corrupt fractional pose is restored by [DecidedStates.nearest] —
 *    snapped to a decided state, never rendered as a rest pose.
 */

/** The declared resting states of one animation, each with its pose value. */
class DecidedStates<S : Any>(states: List<S>, val valueOf: (S) -> Float) {

    val states: List<S> = states.distinct()

    init {
        require(this.states.isNotEmpty()) { "an animation must declare at least one decided state" }
        val values = this.states.map(valueOf)
        require(values.distinct().size == values.size) {
            "decided states must have distinct pose values — two rests at one pose is one state"
        }
    }

    fun value(state: S): Float = valueOf(state)

    fun contains(state: S): Boolean = state in states

    /** The decided state nearest a fractional pose — the ONLY lawful way to restore one. */
    fun nearest(poseValue: Float): S {
        var best = states.first()
        var bestDistance = kotlin.math.abs(valueOf(best) - poseValue)
        for (s in states) {
            val d = kotlin.math.abs(valueOf(s) - poseValue)
            if (d < bestDistance) {
                best = s
                bestDistance = d
            }
        }
        return best
    }
}

/**
 * The pure state machine under every decided animation. The Compose
 * wrapper below delegates to it; the machine itself is testable
 * without a frame clock.
 */
class DecidedMotionMachine<S : Any>(
    val decided: DecidedStates<S>,
    initial: S
) {
    init {
        require(decided.contains(initial)) { "initial state must be a decided state" }
    }

    /** The state the animation is resting in. Changes ONLY on arrival. */
    var restingState: S = initial
        private set

    /** The decided state the animation is travelling to (== restingState when at rest). */
    var targetState: S = initial
        private set

    val isAtRest: Boolean get() = targetState == restingState

    /** Begin travel to a decided state. The only way motion starts. */
    fun begin(to: S) {
        require(decided.contains(to)) { "target must be a declared decided state" }
        targetState = to
    }

    /**
     * Interrupt: declare a new decided destination mid-flight. The
     * resting state is untouched; the body simply owes a different
     * arrival now. Interrupting toward the current resting state is a
     * lawful recall — the body comes back and rests where it started.
     */
    fun interrupt(to: S) = begin(to)

    /** Arrival: the target becomes the resting state. Called by the driver on settle. */
    fun arrive() {
        restingState = targetState
    }

    /**
     * The state persistence may record — ALWAYS decided. Mid-flight it
     * is the target (the motion's declared destination); at rest it is
     * the resting state. A mid-pose is never returned here.
     */
    fun saveableState(): S = targetState

    companion object {
        /** Restore after process death: the machine stands at rest in the saved decided state. */
        fun <S : Any> restore(decided: DecidedStates<S>, saved: S): DecidedMotionMachine<S> =
            DecidedMotionMachine(decided, saved)

        /**
         * Restore from a fractional pose (a legacy save, a corrupt
         * record): the pose is snapped to its nearest decided state
         * and the machine stands THERE. The pose itself never rests.
         */
        fun <S : Any> restoreFromPose(decided: DecidedStates<S>, poseValue: Float): DecidedMotionMachine<S> =
            DecidedMotionMachine(decided, decided.nearest(poseValue))
    }
}

/**
 * The Compose driver: an Animatable bound to a [DecidedMotionMachine].
 * Its whole public surface is decided-state shaped — animate to a
 * state, snap to a state, read the machine. Velocity carries across
 * an interruption automatically: Animatable keeps its velocity when a
 * new animateTo supersedes the old, and the spring body does the rest.
 */
class DecidedAnimatable<S : Any>(
    val machine: DecidedMotionMachine<S>,
    initialPose: Float = machine.decided.value(machine.restingState)
) {
    val animatable: Animatable<Float, androidx.compose.animation.core.AnimationVector1D> =
        Animatable(initialPose)

    /** The current fractional pose — for RENDERING only. Never a rest, never saveable. */
    val pose: Float get() = animatable.value

    val restingState: S get() = machine.restingState
    val targetState: S get() = machine.targetState

    /**
     * Travel to a decided state on [body]'s physics. Calling this
     * while a previous travel is running IS the interruption: the
     * machine retargets and this call supersedes the old animation,
     * carrying current velocity into the new spring.
     */
    suspend fun animateTo(state: S, body: PhysicsBody) {
        machine.begin(state)
        animatable.animateTo(
            targetValue = machine.decided.value(state),
            animationSpec = body.toSpringSpec()
        )
        if (animatable.value == machine.decided.value(state)) machine.arrive()
    }

    /** Teleport to a decided state (state restoration, never a visual effect). */
    suspend fun snapToDecided(state: S) {
        machine.begin(state)
        animatable.snapTo(machine.decided.value(state))
        machine.arrive()
    }
}

/** Remember a decided animation standing at rest in [initial]. */
@Composable
fun <S : Any> rememberDecidedAnimatable(
    decided: DecidedStates<S>,
    initial: S
): DecidedAnimatable<S> = remember(decided, initial) {
    DecidedAnimatable(DecidedMotionMachine(decided, initial))
}
