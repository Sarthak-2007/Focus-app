package com.example.data

/**
 * Issue #143 (night-flip-mid-session), findings F1/F5/F6: the night
 * side of a session had no DARKENING GRAMMAR at all — grep found
 * zero dim/brightness/fade code in the app, zero grayscale anywhere,
 * and the one dark overlay drawn during a Safe Break appeared and
 * vanished as 0ms cuts.
 *
 * This object is the progression the findings ask for, as pure
 * math (unit-tested; the shield service and the Compose surfaces
 * both read it, so the descent the engine publishes and the
 * descent the eye sees can never disagree):
 *
 *  - INTO the break (F6): over the first [RAMP_SECONDS] of a Safe
 *    Break the screen progressively dims — scrim alpha rises,
 *    saturation falls toward monochrome, window brightness steps
 *    down — a descent the eye can follow, not a cliff.
 *  - OUT of the break (F1): when the shield drops at break end,
 *    the dim FADES back over [FADE_OUT_MILLIS] instead of snapping
 *    to full brightness in one frame.
 *  - F5's monochrome rides the same curve: saturation is a
 *    function of the dim progression, applied only to the
 *    night/sealed surfaces.
 *
 * Nothing here stores anything: the progression is derived from
 * the break's own countdown, which the shield loop already owns.
 */
object NightDimMath {

    /** Seconds over which the break descent runs its full course. */
    const val RAMP_SECONDS: Int = 60

    /** The deepest the break scrim ever gets: a heavy dim, never a
     *  blackout — the Deck's END BREAK verb and the break countdown
     *  stay readable underneath it the whole way down. */
    const val MAX_DIM_ALPHA: Float = 0.55f

    /** How long the return fade takes when the shield drops at
     *  break end. */
    const val FADE_OUT_MILLIS: Long = 4_000L

    /** Window brightness at full descent (WindowManager float). */
    const val MIN_WINDOW_BRIGHTNESS: Float = 0.30f

    /** Saturation at full descent: true monochrome (F5). */
    const val MIN_SATURATION: Float = 0.0f

    /** 0 at the break's first instant, 1 once the ramp has run. */
    fun rampFraction(elapsedInBreakSeconds: Int): Float {
        if (elapsedInBreakSeconds <= 0) return 0f
        return (elapsedInBreakSeconds.toFloat() / RAMP_SECONDS.toFloat()).coerceIn(0f, 1f)
    }

    /** Scrim alpha INTO the break: rises with the ramp, then holds
     *  at [MAX_DIM_ALPHA] for the rest of the break. */
    fun dimAlphaIntoBreak(elapsedInBreakSeconds: Int): Float =
        MAX_DIM_ALPHA * rampFraction(elapsedInBreakSeconds)

    /** Scrim alpha OUT of the break: starts at [fromAlpha] (the
     *  depth the descent had reached when the break ended — a
     *  break ended early fades from its own shallower depth) and
     *  decays linearly to 0 across [FADE_OUT_MILLIS]. At or before
     *  the end instant it reports [fromAlpha]; after the window, 0. */
    fun dimAlphaOutOfBreak(millisSinceBreakEnd: Long, fromAlpha: Float): Float {
        val start = fromAlpha.coerceIn(0f, MAX_DIM_ALPHA)
        if (millisSinceBreakEnd <= 0L) return start
        if (millisSinceBreakEnd >= FADE_OUT_MILLIS) return 0f
        val left = 1f - (millisSinceBreakEnd.toFloat() / FADE_OUT_MILLIS.toFloat())
        return start * left
    }

    /** Saturation for a given dim alpha: full colour at no dim,
     *  monochrome at full descent — the F5 curve, tied to the same
     *  progression so colour drains exactly as the light does. */
    fun saturationForDim(dimAlpha: Float): Float {
        val f = (dimAlpha.coerceIn(0f, MAX_DIM_ALPHA) / MAX_DIM_ALPHA)
        return 1f - (1f - MIN_SATURATION) * f
    }

    /** Window brightness for a given dim alpha: full (no override)
     *  at no dim, stepping down to [MIN_WINDOW_BRIGHTNESS]. */
    fun windowBrightnessForDim(dimAlpha: Float): Float {
        val f = (dimAlpha.coerceIn(0f, MAX_DIM_ALPHA) / MAX_DIM_ALPHA)
        return 1f - (1f - MIN_WINDOW_BRIGHTNESS) * f
    }

    /** The desk clock's saturation (F2/F5): an idle clock keeps its
     *  colour; a SEALED clock is already drained to near-monochrome,
     *  and a sealed clock inside a break follows the break's own
     *  curve the rest of the way to true monochrome. */
    fun deskClockSaturation(sealed: Boolean, dimAlpha: Float): Float {
        if (!sealed) return 1f
        return minOf(0.25f, saturationForDim(dimAlpha))
    }
}
