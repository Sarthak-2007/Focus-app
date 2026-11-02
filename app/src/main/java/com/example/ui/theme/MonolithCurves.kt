package com.example.ui.theme

import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.PathInterpolator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween

/**
 * MONOLITH MOTION SYSTEM (DEVELOPER-GUIDE.pdf §5)
 * Three shared cubic-bezier curves + mechanical spring-back interpolators,
 * with durations strictly chosen from { 180, 260, 280, 420, 520 } ms.
 * Slice D amendment (Cato, D4): the slide's driven mesh adds 140 / 110 /
 * 250 to the vocabulary — the mesh bezier itself lives analytically in
 * SlideToStartView's integrator (an interpolator cannot be substepped,
 * seeded, or aborted), these constants name its durations only.
 */
object MonolithCurves {
    // Duration vocabulary (ms)
    const val DURATION_110 = 110
    const val DURATION_140 = 140
    const val DURATION_180 = 180
    const val DURATION_250 = 250
    const val DURATION_260 = 260
    const val DURATION_280 = 280
    const val DURATION_420 = 420
    const val DURATION_520 = 520
    /** Slice E (Zug-II): the seal shot's total span — the vertigo's
     *  clock and the input lock's outer edge share this one name. */
    const val DURATION_1050 = 1050

    // Android View ValueAnimator Interpolators
    val CurveA: PathInterpolator by lazy { PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f) }
    val CurveB: PathInterpolator by lazy { PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f) }
    val CurveC: PathInterpolator by lazy { PathInterpolator(0.3f, 0.0f, 0.8f, 0.15f) }
    /** Slice E, F-S3's scoped names (the registry's PRESS / SEAT,
     *  named for their one joint so no second surface spends them):
     *  the hub's press dip (45ms) and its seat return (160ms). */
    val HubPressDip: PathInterpolator by lazy { PathInterpolator(0.20f, 0.00f, 0.30f, 1.00f) }
    val HubSeatReturn: PathInterpolator by lazy { PathInterpolator(0.22f, 0.90f, 0.28f, 1.00f) }

    /** Slice E (Schlitten/Spule L-4): the sled return's curve —
     *  the ONLY new curve this slice adds; its 650ms duration is a
     *  literal at the animator, never tokenised, no Compose twin. */
    val ReturnHome: PathInterpolator by lazy { PathInterpolator(0.3f, 0.0f, 0.5f, 1.0f) }

    val MechanicalOvershoot: OvershootInterpolator by lazy { OvershootInterpolator(1.25f) }
    val MechanicalAnticipateOvershoot: AnticipateOvershootInterpolator by lazy {
        AnticipateOvershootInterpolator(1.1f)
    }

    // Compose Easing equivalents of the exact same control points
    val EasingA: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /** Slice E (Zug-II §5.2): the seal shot's transform easing —
     *  fast commitment, long settle; the blur re-eases on its own
     *  gentler curve (a blur that tracks the transform reads as a
     *  smear, not a recession). */
    val VertigoTransformEasing: Easing = CubicBezierEasing(0.5f, 0.0f, 0.85f, 0.35f)
    val VertigoBlurEasing: Easing = CubicBezierEasing(0.32f, 0.0f, 0.18f, 1.0f)
    val EasingB: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EasingC: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    fun <T> specA(durationMs: Int = DURATION_260): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingA)

    fun <T> specB(durationMs: Int = DURATION_420): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingB)

    fun <T> specC(durationMs: Int = DURATION_280): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingC)
}

/**
 * CHROME KINEMATICS (chrome wave, spec §§5–6) — the named motion
 * vocabulary of the rail and the PARK drawer. SEAT is Curve A read
 * as a landing (fast, exact, zero overshoot); THROW is a dismissal
 * (accelerating away, never a landing). No chrome motion exists
 * outside these names.
 */
object MonolithKin {
    /** KIN-NAV — the rail cap's travel: 180ms + 60 per stop crossed. */
    const val NAV_BASE_MS = 180
    const val NAV_PER_STOP_MS = 60
    fun navDurationMs(stopsCrossed: Int): Int = NAV_BASE_MS + NAV_PER_STOP_MS * stopsCrossed

    /** KIN-DRAWER — PARK rises 280 SEAT, leaves 160 THROW. */
    const val DRAWER_OPEN_MS = 280
    const val DRAWER_CLOSE_MS = 160

    /** KIN-TAB-IN — at the viewport host only: the outgoing screen
     *  fades 120 linear; the incoming translates 14dp in stop order
     *  and fades in over 240. */
    const val TAB_FADE_OUT_MS = 120
    const val TAB_IN_MS = 240
    const val TAB_TRANSLATE_DP = 14

    /** PRESS — the FIX key answers on DOWN: dip within 90ms. */
    const val PRESS_MS = 90
    /** PRESS-BACK — the key's return when nothing was fixed: ≤150ms. */
    const val PRESS_BACK_MS = 150

    /** SEAT easing (Curve A): travel lands, never overshoots. */
    val EasingSeat: Easing = MonolithCurves.EasingA
    /** THROW easing: a dismissal accelerates away. */
    val EasingThrow: Easing = CubicBezierEasing(0.5f, 0.0f, 1.0f, 0.4f)
}

/**
 * MONOLITH MOTION — THE MAXIMUM-IMPLEMENTATION PRIMITIVES (P0, BUILD-SR-1)
 *
 * The foundation the sealing clunks, the knock, the drum and the door's
 * weight are built on (programme §2 maximum pass: every significant
 * control first works, then is taken to its maximum — mass, inertia,
 * detents, settle — before its piece closes; a default-feeling control
 * is unfinished work). These are NAMED VALUES, not defaults: slices 5
 * (motion) and 8 (maximum) tune the numbers here, in one place, and no
 * lane hard-codes a private spring. Character, per primitive:
 *  - BOLT: near-critically damped, very stiff — a heavy bolt drives
 *    home and stops; the settle is a decision, not a bounce.
 *  - KNOCK: critically damped dead mass — one dull thud, felt more than
 *    heard; zero overshoot by construction (the door does not rebound
 *    against a locked reach).
 *  - DRUM: under-damped with one detent overshoot — the revolving drum
 *    turns, passes its detent, and is caught by it.
 *  - DOOR WEIGHT: soft spring, heavy mass — the door opens by its own
 *    weight at the lifting: slow commitment, long settle.
 *  - WHEEL/CRANK: spun mass with detents — freer than the drum, still
 *    caught; the combination wheel and the governor's crank share the
 *    family, at different stiffness.
 */
object MonolithMotion {

    /** The bolt drive (the sealing's moving iron). */
    val BoltSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 0.92f, stiffness = 900f)

    /** The knock's dead stop. */
    val KnockSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 1.0f, stiffness = 2600f)

    /** The revolving drum's detent turn (the daily-allowance crank). */
    val DrumSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 0.78f, stiffness = 420f)

    /** The door's opening weight at the lifting. */
    val DoorWeightSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 0.86f, stiffness = 55f)

    /** The combination wheel's spin (the signature at the signing). */
    val WheelSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 0.65f, stiffness = 180f)

    /** The governor's crank: the week's emergency minute, wound by hand. */
    val CrankSpring: androidx.compose.animation.core.SpringSpec<Float> =
        androidx.compose.animation.core.SpringSpec(dampingRatio = 0.72f, stiffness = 300f)

    /** One clunk of the sealing: when it lands, how long its body rings
     *  in the felt/sound design, and its weight against the first. */
    data class Clunk(val atMs: Int, val durationMs: Int, val weight: Float)

    /** THE SEALING — three descending clunks (Audit A §2.1: bolts drive
     *  home one by one, three descending clunks, felt). Descending in
     *  weight, widening in interval, lengthening in body: the first
     *  bolt is the loudest fact; the third is the door finishing its
     *  sentence. Sound/haptic tuning is Slice 6/7's; the sequence's
     *  shape is foundation law. */
    val SealClunks: List<Clunk> = listOf(
        Clunk(atMs = 0, durationMs = 120, weight = 1.0f),
        Clunk(atMs = 190, durationMs = 150, weight = 0.82f),
        Clunk(atMs = 420, durationMs = 190, weight = 0.64f)
    )

    /** The lifting's mirror: bolts withdraw one by one (Audit A §2.5)
     *  on the same widening rhythm, ascending in weight — the door
     *  gives its iron back in the order it took it. */
    val LiftClunks: List<Clunk> = listOf(
        Clunk(atMs = 0, durationMs = 120, weight = 0.64f),
        Clunk(atMs = 190, durationMs = 150, weight = 0.82f),
        Clunk(atMs = 420, durationMs = 190, weight = 1.0f)
    )

    /** Weight landing: fast commitment, no bounce, a settle you feel
     *  in the last fifth. For driven iron (bolts, the plate's drop). */
    val WeightEasing: Easing = CubicBezierEasing(0.22f, 0.9f, 0.26f, 1.0f)

    /** Detent travel: even pull, caught at the end. For the drum and
     *  the crank, whose ends are mechanical stops, not targets. */
    val DetentEasing: Easing = CubicBezierEasing(0.3f, 0.0f, 0.2f, 1.0f)

    /** The wind before a release: a crank or key gathering its price
     *  before it moves. Anticipation is the tell that mass is real. */
    val WindEasing: Easing = CubicBezierEasing(0.36f, -0.28f, 0.24f, 1.0f)
}
