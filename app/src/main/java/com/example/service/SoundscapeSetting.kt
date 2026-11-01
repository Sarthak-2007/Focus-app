package com.example.service

import android.content.Context
import android.content.SharedPreferences

/**
 * #253 LINE 1 — THE SESSION SOUNDSCAPE as a persisted FACT.
 *
 * Modelled on [SoundEffectsSetting] (Truth Car 3, UNA-03) and
 * under the same laws, for the same mechanical reason: the fact
 * must be readable synchronously at engine-start time, on any
 * thread, so it lives in SharedPreferences — in DEVICE-PROTECTED
 * storage, with the same one-shot credential→device-protected
 * migration (issue #83), because a process born through direct
 * boot must read the same fact as the UI process.
 *
 *  1. DEFAULT OFF — a soundscape is content the user chooses,
 *     never a surprise: a fresh install is silent until the
 *     PARK sled says otherwise. (Unlike sound effects, whose
 *     default ON preserves pre-setting shipped behaviour, the
 *     soundscape has no shipped behaviour to preserve.)
 *  2. WRITES COMMIT — [setEnabled] uses `commit()`, never
 *     `apply()`, and returns the commit's verdict, so no surface
 *     can print a setting the disk never took (issue #49).
 *
 * There is deliberately no live-engine push here: the engine
 * reads the fact at every start call, and the ViewModel drives
 * start/stop on the toggle and on the session's edges, so there
 * is no second copy of the fact to disagree.
 */
object SoundscapeSetting {

    internal const val PREFS_NAME = "monolith_soundscape"
    internal const val KEY_ENABLED = "soundscape_enabled"

    /** One-shot guard for the credential→device-protected
     *  migration in [prefs] (issue #83 pattern). */
    @Volatile
    private var hasMigratedSoundscapeSetting = false

    private fun prefs(context: Context): SharedPreferences {
        val appContext = context.applicationContext
        val deviceContext = appContext.createDeviceProtectedStorageContext() ?: appContext
        if (!hasMigratedSoundscapeSetting) {
            synchronized(this) {
                if (!hasMigratedSoundscapeSetting) {
                    runCatching {
                        deviceContext.moveSharedPreferencesFrom(appContext, PREFS_NAME)
                    }
                    hasMigratedSoundscapeSetting = true
                }
            }
        }
        return deviceContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** The fact. Default OFF — see law 1 above. */
    fun isEnabled(context: Context): Boolean =
        prefs(context)
            .getBoolean(KEY_ENABLED, false)

    /** Write the fact. Returns true only when the commit landed
     *  (law 2) — the caller prints the disk's answer, never the
     *  attempt. */
    fun setEnabled(context: Context, enabled: Boolean): Boolean =
        prefs(context)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .commit()
}
