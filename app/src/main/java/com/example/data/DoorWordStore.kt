package com.example.data

import android.content.Context

/**
 * THE DOOR-WORD'S STORE (BUILD-SR-37) — the fourth printing's word,
 * persisted under the coordinator's ruling on GAP-COORD-01
 * (SUCCESSOR-22, at PR #873, 2026-11-03):
 *
 *  - The door-word is a SINGLE WORD OF HIS CHOOSING, set at the
 *    signing table alongside his combination, printed on the
 *    sealed face, changeable ONLY at a signing.
 *  - The app never supplies, suggests, or defaults it: no word
 *    exists anywhere in this file, and an unset word is a complete
 *    state — the slot stands as a kept place on the sealed face,
 *    on the same law as his sentence's kept place.
 *
 * Stored OUTSIDE Room on purpose, like ShieldIntentionStore and
 * DayWordStore: a one-word fact with no schema business (the
 * database is frozen). It is a standing word, not a per-day fact:
 * it is set once at a signing and stands across days until he
 * changes it at another signing — the sealed face reads it as it
 * stood at the closing, so a later change can never re-print a
 * face that already stands (the print-once law is kept by the
 * face's snapshot derivation, not by freezing this store).
 *
 * Writes COMMIT (the G-02 standard): what the signing table shows
 * as set is set.
 */
class DoorWordStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** His door-word, or null when none stands (the kept place). */
    fun doorWord(): String? = DoorWordLaw.lawfulWord(prefs.getString(KEY_DOOR_WORD, null))

    /**
     * Set the word as written at the signing. Returns true when the
     * word stands as written. A blank writing CLEARS the word (the
     * kept place stands again — a complete state, not an error).
     * A writing that is not a single word is refused whole and
     * nothing changes: the ruling's form is one word, and the
     * mechanism — not a message — is what refuses.
     */
    fun put(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            prefs.edit().remove(KEY_DOOR_WORD).commit()
            return true
        }
        val word = DoorWordLaw.lawfulWord(trimmed) ?: return false
        prefs.edit().putString(KEY_DOOR_WORD, word).commit()
        return true
    }

    companion object {
        private const val PREFS_NAME = "monolith_door_word"
        private const val KEY_DOOR_WORD = "door_word"
    }
}

/**
 * The door-word's form law, pure so the sealed face's derivation
 * and the bare JVM tests read the same fact as the store: a lawful
 * door-word is exactly ONE word — non-blank, carrying no
 * whitespace inside it. Anything else is not a door-word (null):
 * the derivation renders the kept place for it, and the store
 * refuses to persist it. The law judges FORM only — the word's
 * content is his, exempt from any bench, exactly like his sentence.
 */
object DoorWordLaw {

    fun lawfulWord(raw: String?): String? {
        val word = raw?.trim().orEmpty()
        if (word.isEmpty()) return null
        if (word.any { it.isWhitespace() }) return null
        return word
    }
}
