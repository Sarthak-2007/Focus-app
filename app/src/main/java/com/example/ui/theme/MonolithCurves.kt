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
