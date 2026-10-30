package com.example.data

import android.content.Context
import java.util.Locale

/**
 * Declared day words — his authored note for a date, printed beside
 * that date's heading in the Drawer. It is a word, never a number,
 * and it is never folded into any total (the Drawer prints no totals
 * for it to be folded into).
 *
 * Stored OUTSIDE Room on purpose: capture ships with the Drawer
 * without touching the Room schema. A deferred capture would lose
 * every word declared in the meantime, unbackfillably — the same
 * no-backfill law TOKEN-SPEC §6 states for spends. The `day_notes`
 * Room table waits for the 8→9 window, which will drain this store.
 *
 * The vocabulary is OPEN (issue #137, F4): [WORDS] is the suggested
 * list the Drawer offers as chips, not a closed law — [put] accepts
 * any single uppercase word (see [normalize]). A lost day he names
 * himself is still his word.
 *
 * Three of the suggestions are EXCUSE words ([EXCUSE_WORDS]):
 * a date carrying one is excused — RoutineDerive grants it the
 * paused-window amnesty (never MISSED), the heartbeat does not
 * auto-start its routines, and the finalise funnel never stamps
 * MISSED over it (issue #137, F1/F5).
 */
class DayWordStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** All declared words, dateIso (yyyy-MM-dd, filing date) → word. */
    fun all(): Map<String, String> =
        prefs.all.entries
            .mapNotNull { (key, value) -> (value as? String)?.let { key to it } }
            .toMap()

    /** Declare [word] for [dateIso], or clear it with null. The
     *  word is normalised by [normalize]; a word that cannot
     *  normalise (blank, more than one token, over-long, or
     *  carrying non-letters) is refused silently.
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
            else -> {
                val clean = normalize(word) ?: return
                editor.putString(dateIso, clean)
            }
        }
        editor.commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_day_words"

        /** Longest declarable word (issue #137, F4). */
        const val MAX_WORD_LENGTH = 12

        /** The suggested words the Drawer offers as chips — a
         *  suggestion list, NOT a closed vocabulary: any word
         *  [normalize] accepts may be declared. */
        val WORDS: List<String> = listOf("REST", "ILL", "FAMILY", "TRAVEL", "WORK")

        /** The excuse words (issue #137, F1): a filing date
         *  carrying one of these is excused from routine judgement
         *  and materialisation. */
        val EXCUSE_WORDS: Set<String> = setOf("ILL", "REST", "FAMILY")

        /** True when [word] is an excuse word. */
        fun isExcuse(word: String?): Boolean = word in EXCUSE_WORDS

        /** The stored form of a declared word, or null when it
         *  cannot be one: trimmed, uppercased, exactly one token
         *  of letters A–Z, at most [MAX_WORD_LENGTH] chars. */
        fun normalize(word: String): String? =
            word.trim().uppercase(Locale.US)
                .takeIf { it.isNotEmpty() && it.length <= MAX_WORD_LENGTH && it.all { c -> c in 'A'..'Z' } }
    }
}
