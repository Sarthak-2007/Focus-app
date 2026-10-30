package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

/**
 * The one accessor for prefs files that the direct-boot path reads
 * (issue #83, storage-hygiene F1 — completion by STURM3-04).
 *
 * The Room DB lives in device-protected storage so LOCKED_BOOT_COMPLETED
 * recovery works pre-unlock, but the prefs-backed facts the same
 * recovery depends on (the seal marker above all) were written and read
 * through the plain credential-protected context: pre-unlock they read
 * as absent, so a genuinely sealed session looked unsealed. The parked
 * #83 lane moved the seal WRITER (SessionSealTransition) to
 * device-protected storage but left two holes: the legacy
 * credential-protected file was never migrated (its marker would be
 * abandoned at upgrade), and MonolithRepository's marker readers still
 * read the credential-protected file — writer and readers split across
 * two different files forever.
 *
 * This accessor closes both: every access goes to the
 * device-protected file, and the first access per process migrates the
 * legacy credential-protected file across
 * ([Context.moveSharedPreferencesFrom], best-effort and one-shot) so a
 * marker landed by an older build still testifies after the move.
 */
object DeviceProtectedPrefs {

    private val migratedNames = mutableSetOf<String>()

    fun get(context: Context, name: String): SharedPreferences {
        val appContext = context.applicationContext
        val deviceContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appContext.createDeviceProtectedStorageContext() ?: appContext
        } else {
            appContext
        }
        if (deviceContext !== appContext) {
            synchronized(migratedNames) {
                if (migratedNames.add(name)) {
                    // Best-effort: on a fresh install there is nothing
                    // to move; if the credential file was already
                    // opened in this process the move can refuse, and
                    // the device-protected file is still the fact from
                    // here on.
                    runCatching {
                        deviceContext.moveSharedPreferencesFrom(appContext, name)
                    }
                }
            }
        }
        return deviceContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}
