package com.example.data

import android.content.Context

/**
 * Issue #318 (Audit R5 supplement, one sec second-pass F4): the
 * Airlock breathing wall was a composable-local integer — every
 * intercept breathed for exactly 5 seconds because the literal lived
 * in the overlay, unpersisted and unconfigurable, while the rival
 * stores its breath as a per-configuration object.
 *
 * The breath is now a stored configuration value, outside Room on
 * purpose (same reasoning as [DayWordStore]: a value this small
 * ships without a schema migration). The default stays 5 seconds —
 * Decision #16's wall — until the user changes it; the stored value
 * is clamped on the way out as well as on the way in, so a stray or
 * corrupted entry can never produce a zero-length or absurd wall.
 */
class AirlockBreathStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The configured breathing-wall length in seconds, validated. */
    fun breathSeconds(): Int = clampSeconds(prefs.getInt(KEY_BREATH_SECONDS, DEFAULT_SECONDS))

    /** Persist a new breathing-wall length, clamped to the lawful range. */
    fun setBreathSeconds(seconds: Int) {
        prefs.edit().putInt(KEY_BREATH_SECONDS, clampSeconds(seconds)).apply()
    }

    companion object {
        const val PREFS_NAME = "airlock_breath"
        const val KEY_BREATH_SECONDS = "breath_seconds"
        const val DEFAULT_SECONDS = 5
        const val MIN_SECONDS = 3
        const val MAX_SECONDS = 30

        /** Pure, unit-tested: any stored or offered value is served
         *  only inside [MIN_SECONDS]..[MAX_SECONDS]. */
        fun clampSeconds(raw: Int): Int = raw.coerceIn(MIN_SECONDS, MAX_SECONDS)
    }
}
