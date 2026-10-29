package com.example.data

import android.content.Context

/**
 * The hand loadout cap's memory (UNA-07, Truth Car 3).
 *
 * The Shield LOADOUT cap used to rest on `remember { 0 }` — STUDY by
 * composition memory: leave Shield and return, or restart the
 * process, and the cap sat on STUDY no matter what he had actually
 * picked, while the app tiers underneath stayed wherever his last
 * pick had put them. The selector lied about the state it selects.
 *
 * The cap now rests on a stored fact: the last loadout HE applied by
 * hand (the ViewModel's applySmartLoadout path — the hand path only;
 * a routine firing its own loadout at 20:00 does not move his hand
 * selector). Stored OUTSIDE Room on purpose, like DayWordStore: a
 * one-string fact with no schema business. Writes COMMIT (the G-02
 * standard): a deferred capture must not lose the pick the cap has
 * already shown as resting.
 */
class LoadoutStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The last hand-applied loadout, or null if he has never picked. */
    fun lastHandLoadout(): SmartLoadout? =
        prefs.getString(KEY_LAST_HAND_LOADOUT, null)?.let { name ->
            runCatching { SmartLoadout.valueOf(name) }.getOrNull()
        }

    /** Record a hand pick. Commits synchronously (see class doc). */
    fun put(loadout: SmartLoadout) {
        prefs.edit().putString(KEY_LAST_HAND_LOADOUT, loadout.name).commit()
    }

    /** The cap position the stored fact implies; STUDY (0) before
     *  any pick — the same position the old remember default showed,
     *  but now a default of record, not of amnesia. */
    fun capIndex(): Int = capIndexOf(lastHandLoadout() ?: SmartLoadout.STUDY)

    companion object {
        private const val PREFS_NAME = "monolith_loadout"
        private const val KEY_LAST_HAND_LOADOUT = "last_hand_loadout"

        /** Cap positions, ShieldCapSelector's order: the ONE mapping
         *  between a loadout and its seat on the cap. */
        fun capIndexOf(loadout: SmartLoadout): Int = when (loadout) {
            SmartLoadout.STUDY -> 0
            SmartLoadout.WORK_SPRINT -> 1
            SmartLoadout.SLEEP -> 2
        }
    }
}
