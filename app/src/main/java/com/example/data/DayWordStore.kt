package com.example.data

import android.content.Context

/**
 * Declared day words (REST / ILL / FAMILY) — his authored note for a
 * date, printed beside that date's heading in the Drawer. It is a
 * word, never a number, and it is never folded into any total (the
 * Drawer prints no totals for it to be folded into).
 *
 * Stored OUTSIDE Room on purpose: capture ships with the Drawer
 * without touching the Room schema. A deferred capture would lose
 * every word declared in the meantime, unbackfillably — the same
 * no-backfill law TOKEN-SPEC §6 states for spends. The `day_notes`
 * Room table waits for the 8→9 window, which will drain this store.
 *
 * The word list is closed by construction: REST, ILL, FAMILY —
 * never TEST. This app does not know test days.
 */
class DayWordStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** All declared words, dateIso (yyyy-MM-dd, filing date) → word. */
    fun all(): Map<String, String> =
        prefs.all.entries
            .mapNotNull { (key, value) -> (value as? String)?.let { key to it } }
            .toMap()

    /** Declare [word] for [dateIso], or clear it with null. Words
     *  outside [WORDS] are refused silently — the set is the law.
     *
     *  The write COMMITS (Truth Car 2, G-02): apply()'s async flush
     *  left a window where a hard process death silently lost a
     *  word the Drawer had already shown as declared — and this
     *  store's one record is his authored declaration for a sick
     *  day, exactly the class of fact this app exists not to lose.
     *  The seal marker commits synchronously for the same reason
     *  (SessionSealTransition); the word now meets that standard. */
    fun put(dateIso: String, word: String?) {
        val editor = prefs.edit()
        when {
            word == null -> editor.remove(dateIso)
            word in WORDS -> editor.putString(dateIso, word)
            else -> return
        }
        editor.commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_day_words"

        /** The complete, closed word list. Never TEST. */
        val WORDS: List<String> = listOf("REST", "ILL", "FAMILY")
    }
}
