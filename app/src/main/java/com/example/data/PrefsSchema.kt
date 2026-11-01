package com.example.data

import android.content.Context

/**
 * Audit R6 #341 F56: the SharedPreferences schema version.
 *
 * Room is not the only store an update can strand: at the audit
 * this app carried 23 distinct SharedPreferences files and NONE
 * of them carried a version stamp, so no future update could
 * evolve any of their formats without silently stranding or
 * misreading user state. Every file now carries
 * [VERSION_KEY] = [CURRENT_VERSION] after its first write under
 * this regime, and upgrades run through the migrator chain:
 *
 * - a file with no key is a v0 (pre-versioning) file: the v0→v1
 *   migrator is a pure stamp (v1 IS the current format of every
 *   file — nothing is reinterpreted, nothing is moved);
 * - a file stamped with a FUTURE version (a downgrade, a restored
 *   backup from a newer build) is left ENTIRELY untouched — its
 *   values are never read through a migrator that cannot know
 *   them, and its stamp is never lowered;
 * - the day-word drain residue DURCH-E named (unrecognised values
 *   left in monolith_day_words by the drain) is quarantined by
 *   that drain's own rule already: it stays in the file, unserved
 *   and undeleted (DayWordStore's read-side validation) — this
 *   regime changes nothing about it.
 *
 * The enumeration is the 23 files on current main (wave-3 read),
 * by name — a new prefs file that is not added here is caught by
 * Issue341BuildPrefsVersionTest, which greps the tree for
 * getSharedPreferences names and diffs against this list.
 */
object PrefsSchema {

    const val VERSION_KEY = "prefs_schema_version"
    const val CURRENT_VERSION = 1

    /** Every SharedPreferences file in the app, by name. */
    val ALL_PREFS_FILES: List<String> = listOf(
        "airlock_breath",
        "break_end_ledger",
        "intercept_nonce",
        "monolith_active_task_days",
        "monolith_app_list",
        "monolith_arm_zone",
        "monolith_chrome",
        "monolith_clock_anchor",
        "monolith_day_words",
        "monolith_install_notes",
        "monolith_intercept_consumption",
        "monolith_loadout",
        "monolith_prearm",
        "monolith_provenance",
        "monolith_receipt_dismissal",
        "monolith_seed",
        "monolith_seal",
        "monolith_sound_effects",
        "monolith_undo_delete",
        "monolith_wall_accrual",
        "monolith_watch",
        "surface_rules",
        "web_gate"
    )

    /**
     * What one file's stamp becomes after migration: pure, so the
     * chain is unit-testable without Android. Returns the version
     * the file should carry, or null when the file must be left
     * untouched (a future version we cannot know).
     */
    fun migratedVersion(storedVersion: Int?): Int? = when {
        storedVersion == null -> CURRENT_VERSION // v0 → v1: stamp only
        storedVersion < 0 -> CURRENT_VERSION // a corrupt stamp is not a future version
        storedVersion <= CURRENT_VERSION -> CURRENT_VERSION
        else -> null // future version: hands off
    }

    /**
     * Stamp every known prefs file with the migrated version.
     * Files that do not exist yet are created empty-but-stamped —
     * the stamp is metadata about the file's format, and a store
     * that opens the file later finds its regime already declared.
     * Future-version files are skipped untouched. Called once per
     * process start from MonolithApp; cheap (23 tiny files).
     */
    fun stampAll(context: Context) {
        val appCtx = context.applicationContext
        for (name in ALL_PREFS_FILES) {
            val prefs = appCtx.getSharedPreferences(name, Context.MODE_PRIVATE)
            val stored = if (prefs.contains(VERSION_KEY)) prefs.getInt(VERSION_KEY, 0) else null
            val target = migratedVersion(stored) ?: continue
            if (stored != target) {
                prefs.edit().putInt(VERSION_KEY, target).apply()
            }
        }
    }
}
