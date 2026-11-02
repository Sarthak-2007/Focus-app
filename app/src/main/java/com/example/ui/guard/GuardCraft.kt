package com.example.ui.guard

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

/**
 * P7 — THE ALLOWANCE WHEEL'S CRAFT (BUILD-SR-5).
 *
 * The Guard surface is where his weak hour will push, so its
 * control cannot feel like a settings slider. The wheel is a
 * combination dial: it turns in FIVE-MINUTE DETENTS, carries
 * mass (a fling keeps travelling and decays), and settles into
 * its detent on a spring with weight — never a default-feeling
 * control (F-3: a default-feeling control is unfinished work).
 *
 * This file is the physics as PURE FIGURES + pure functions, so
 * the feel is unit-tested on a bare JVM and Slices 5/7/8 (motion
 * / haptics / maximum) tune NUMBERS here, never structure. The
 * figures are build-chosen at software scope and travel stamped
 * UNPROVEN as feel — they are judged in the hand, in the running
 * app, not asserted here.
 */
object GuardWheel {

    /** One detent = five minutes of allowance. */
    const val STEP_MINUTES: Int = 5

    /** Dial travel per detent, in degrees: a full turn of the
     *  raise dial is twelve detents — an hour a turn, like a
     *  combination dial's honest pace. */
    const val DEGREES_PER_DETENT: Double = 30.0

    /** Mass: a released fling decays exponentially at this rate
     *  (per second). Heavy enough that a flick travels a few
     *  detents and dies; light dials feel like toys. */
    const val FLING_DECAY_PER_SECOND: Double = 3.2

    /** A fling slower than this (detents/second) is no fling —
     *  the wheel simply settles where it was left. */
    const val FLING_MIN_DETENTS_PER_SECOND: Double = 0.6

    /** The settle spring the Compose wheel consumes (stiffness /
     *  damping ratio): firm, one overshoot at most, no bounce —
     *  brass coming to rest against a stop. */
    const val SETTLE_STIFFNESS: Float = 320f
    const val SETTLE_DAMPING_RATIO: Float = 0.82f

    /** Detent index of a minutes value: the nearest stop. The
     *  standing allowance may sit off-grid (a legacy figure);
     *  the wheel starts from its nearest detent and the model's
     *  detent list keeps the exact figure reachable. */
    fun detentIndexFor(minutes: Int): Int =
        (minutes.toDouble() / STEP_MINUTES).roundToInt().coerceAtLeast(0)

    fun minutesForDetent(index: Int): Int =
        index.coerceAtLeast(0) * STEP_MINUTES

    fun rotationForDetent(index: Int): Double = index * DEGREES_PER_DETENT

    /** The detent a raw rotation is nearest to. */
    fun detentForRotation(rotationDegrees: Double): Int =
        (rotationDegrees / DEGREES_PER_DETENT).roundToInt().coerceAtLeast(0)

    /**
     * Where a fling comes to rest: exponential decay carries the
     * wheel [velocityDetentsPerSecond] / decay detents further
     * (the integral of v·e^(−kt)), signed by direction, clamped
     * to [minIndex, maxIndex]. Below the fling floor, it rests
     * where it was released.
     */
    fun flingRestDetent(
        fromIndex: Int,
        velocityDetentsPerSecond: Double,
        minIndex: Int,
        maxIndex: Int
    ): Int {
        if (abs(velocityDetentsPerSecond) < FLING_MIN_DETENTS_PER_SECOND) {
            return fromIndex.coerceIn(minIndex, maxIndex)
        }
        val travel = velocityDetentsPerSecond / FLING_DECAY_PER_SECOND
        return (fromIndex + travel.roundToInt()).coerceIn(minIndex, maxIndex)
    }

    /** The wheel's rotation a moment into its settle: a decaying
     *  approach used by the tests to prove the settle is finite
     *  and monotone-ish in envelope — the Compose spring renders
     *  the same character with [SETTLE_STIFFNESS] figures. */
    fun settleEnvelope(elapsedSeconds: Double): Double =
        exp(-FLING_DECAY_PER_SECOND * elapsedSeconds.coerceAtLeast(0.0))
}
