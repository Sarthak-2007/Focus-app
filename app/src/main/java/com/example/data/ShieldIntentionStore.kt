package com.example.data

import android.content.Context

/**
 * The Shield intention line's memory (Shield Rethink 2, Step 2).
 *
 * The intention is a line HE writes — on the Shield sheet, in his
 * own words — that the arm screen and the session plate read back
 * verbatim. It is not a session field: Sarthak's ruling is that the
 * intention often spans days ("this week is for the AIATS"), so it
 * must survive sessions, restarts and the whole week until he
 * changes it. A session column would kill it at session end; the
 * sidecar is its honest home.
 *
 * Stored OUTSIDE Room on purpose, like DayWordStore and
 * LoadoutStore: a one-string fact with no schema business (the
 * database is frozen). Blank is a complete state — no placeholder,
 * no prompt, no absence marked anywhere the line is read. Writes
 * COMMIT (the G-02 standard): what the sheet shows as written is
 * written.
 */
class ShieldIntentionStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** His standing intention line, or "" when he has written none. */
    fun intention(): String = prefs.getString(KEY_INTENTION, null) ?: ""

    /** Record the line as written ("" clears it). Commits synchronously. */
    fun put(text: String) {
        prefs.edit().putString(KEY_INTENTION, text).commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_shield_intention"
        private const val KEY_INTENTION = "intention_line"
    }
}
