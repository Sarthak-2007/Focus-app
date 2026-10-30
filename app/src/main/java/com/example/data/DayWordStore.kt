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

    /** All declared words, dateIso (yyyy-MM-dd, filing date) → word.
     *  Read-side validation (Audit R1 #37): an entry is served only
     *  if its key parses as a real ISO calendar date AND its value
     *  is in [WORDS] — a stray, legacy or corrupted entry in the
     *  file is not a declared word, and the header's "closed by
     *  construction" claim is now enforced on the way out as well
     *  as on the way in. */
    fun all(): Map<String, String> =
        prefs.all.entries
            .mapNotNull { (key, value) ->
                (value as? String)
                    ?.takeIf { it in WORDS && isDateIso(key) }
                    ?.let { key to it }
            }
            .toMap()

    /** Declare [word] for [dateIso], or clear it with null. Words
     *  outside [WORDS] are refused — the set is the law — as are
     *  date strings that do not parse as a real ISO calendar date.
     *
     *  The write COMMITS (Truth Car 2, G-02): apply()'s async flush
     *  left a window where a hard process death silently lost a
     *  word the Drawer had already shown as declared — and this
     *  store's one record is his authored declaration for a sick
     *  day, exactly the class of fact this app exists not to lose.
     *  The seal marker commits synchronously for the same reason
     *  (SessionSealTransition); the word now meets that standard.
     *
     *  Returns commit()'s verdict (Audit R1 #37): false means the
     *  write did NOT reach disk — the caller must not present the
     *  word as declared. A refused word/date also returns false.
     *  The G-02 guarantee ("cannot be silently lost") is only true
     *  if the failure is reportable; discarding this Boolean made
     *  it unverifiable. */
    fun put(dateIso: String, word: String?): Boolean {
        if (!isDateIso(dateIso)) return false
        val editor = prefs.edit()
        when {
            word == null -> editor.remove(dateIso).remove(zoneKey(dateIso))
            word in WORDS -> editor.putString(dateIso, word)
                // Issue #152: witness the zone this declaration is
                // made in, under a side key the readers never serve
                // as a word (its key is not a date and its value is
                // not in WORDS, so all() filters it twice over).
                // The seed-sync drain likewise skips it, so the
                // witness survives the word's move into day_notes.
                .putString(zoneKey(dateIso), java.time.ZoneId.systemDefault().id)
            else -> return false
        }
        return editor.commit()
    }

    /** Issue #152: the zone the standing declaration for
     *  [dateIso] was made in, or null when no witness exists
     *  (a word declared before this witness, or none declared). */
    fun declaredZone(dateIso: String): String? =
        prefs.getString(zoneKey(dateIso), null)

    private fun zoneKey(dateIso: String): String = "$dateIso#zone"

    /** Strict yyyy-MM-dd: parses as a real calendar date and
     *  round-trips to the same string (no "2026-13-40", no
     *  non-padded or signed variants). */
    private fun isDateIso(value: String): Boolean =
        runCatching {
            java.time.LocalDate.parse(value).toString() == value
        }.getOrDefault(false)

    companion object {
        private const val PREFS_NAME = "monolith_day_words"

        /** The complete, closed word list. Never TEST. */
        val WORDS: List<String> = listOf("REST", "ILL", "FAMILY")

        /** The excuse words (issue #137, F1): a filing date
         *  carrying one of these is excused from routine
         *  judgement — RoutineDerive grants it the paused-window
         *  amnesty (never MISSED), the heartbeat does not
         *  auto-start its routines, and the finalise funnel
         *  stamps UNKNOWN, never MISSED, over it. Today the
         *  excuse set is the whole closed list above. */
        val EXCUSE_WORDS: Set<String> = setOf("ILL", "REST", "FAMILY")

        /** True when [word] is an excuse word. */
        fun isExcuse(word: String?): Boolean = word in EXCUSE_WORDS
    }
}
