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
 */
object MonolithCurves {
    // Duration vocabulary (ms)
    const val DURATION_180 = 180
    const val DURATION_260 = 260
    const val DURATION_280 = 280
    const val DURATION_420 = 420
    const val DURATION_520 = 520

    // Android View ValueAnimator Interpolators
    val CurveA: PathInterpolator by lazy { PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f) }
    val CurveB: PathInterpolator by lazy { PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f) }
    val CurveC: PathInterpolator by lazy { PathInterpolator(0.3f, 0.0f, 0.8f, 0.15f) }
    val MechanicalOvershoot: OvershootInterpolator by lazy { OvershootInterpolator(1.25f) }
    val MechanicalAnticipateOvershoot: AnticipateOvershootInterpolator by lazy {
        AnticipateOvershootInterpolator(1.1f)
    }

    // Compose Easing equivalents of the exact same control points
    val EasingA: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EasingB: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EasingC: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    fun <T> specA(durationMs: Int = DURATION_260): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingA)

    fun <T> specB(durationMs: Int = DURATION_420): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingB)

    fun <T> specC(durationMs: Int = DURATION_280): TweenSpec<T> =
        tween(durationMillis = durationMs, easing = EasingC)
}
