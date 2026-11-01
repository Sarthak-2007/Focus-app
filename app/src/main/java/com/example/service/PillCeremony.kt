package com.example.service

import android.view.View

/**
 * Issue #102 (retained DAYLIGHT break 2): the pill's ceremony —
 * ONE spec for how a floating pill arrives and leaves.
 *
 * The W9 filing's charge was that the pill "snaps in like a
 * system toast": attached with a bare `WindowManager.addView`,
 * removed the same way, no entrance, no exit. The numbers below
 * are the whole ceremony as pure data (unit-tested): a pill
 * fades in while settling [ENTER_SLIDE_DP] down into its seat,
 * and fades out while lifting [EXIT_SLIDE_DP] — brief, in the
 * family of the app's own overlay fades, never a bounce or a
 * flourish: this surface is read mid-temptation, so the ceremony
 * is a breath, not a performance.
 */
object PillCeremony {
    const val ENTER_DURATION_MS: Long = 220L
    const val EXIT_DURATION_MS: Long = 160L
    const val ENTER_SLIDE_DP: Float = 12f
    const val EXIT_SLIDE_DP: Float = 8f
    const val START_ALPHA: Float = 0f
    const val REST_ALPHA: Float = 1f

    /** Where an entering pill starts, as (alpha, translationY in
     *  px for the caller's density): invisible, [ENTER_SLIDE_DP]
     *  above its seat; it settles DOWN into place. */
    fun enterStart(density: Float): Pair<Float, Float> =
        START_ALPHA to -ENTER_SLIDE_DP * density

    /** Where an exiting pill ends: invisible, lifted
     *  [EXIT_SLIDE_DP] above its seat. */
    fun exitEnd(density: Float): Pair<Float, Float> =
        START_ALPHA to -EXIT_SLIDE_DP * density
}

/** Begin the entrance on a freshly attached pill window. */
fun animatePillIn(view: View) {
    val (alpha, translation) = PillCeremony.enterStart(view.resources.displayMetrics.density)
    view.alpha = alpha
    view.translationY = translation
    view.animate()
        .alpha(PillCeremony.REST_ALPHA)
        .translationY(0f)
        .setDuration(PillCeremony.ENTER_DURATION_MS)
        .start()
}

/**
 * Begin the exit on a pill window being retired; [onDone] runs
 * when the fade completes (the caller removes the window there —
 * a window removed mid-fade would snap, which is the exact
 * behaviour this ceremony replaces). If the view is already
 * detached, [onDone] runs immediately.
 */
fun animatePillOut(view: View, onDone: () -> Unit) {
    if (!view.isAttachedToWindow) {
        onDone()
        return
    }
    val (alpha, translation) = PillCeremony.exitEnd(view.resources.displayMetrics.density)
    view.animate()
        .alpha(alpha)
        .translationY(translation)
        .setDuration(PillCeremony.EXIT_DURATION_MS)
        .withEndAction { onDone() }
        .start()
}
