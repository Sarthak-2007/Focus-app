package com.example.data

import android.content.Context

/**
 * One-time install-day disclosures (Audit R3b #161, update-day).
 *
 * Two moments the app used to meet with silence:
 *  - a FRESH START: this install was seeded empty — a first install
 *    OR a wiped phone OR a new phone are indistinguishable from
 *    inside, and the honest sentence covers all three: no past came
 *    with this install, because Monolith keeps everything on the
 *    phone it was made on.
 *  - an UPDATE: the version on this phone rose since the last open.
 *    The platform carried the data through; the app now says an
 *    update happened instead of leaving no trace of it.
 *
 * Stored OUTSIDE Room (the DayWordStore pattern): these flags must
 * be writable from the seed path and readable at first composition
 * without touching the Room schema. Writes COMMIT — a disclosure
 * flag is exactly the class of fact this app exists not to lose.
 */
class InstallNoteStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The seed path found an empty install and seeded it: a
     *  fresh-start disclosure is owed at first open. */
    fun markFreshStart() {
        prefs.edit().putBoolean(KEY_FRESH_PENDING, true).commit()
    }

    fun freshStartPending(): Boolean = prefs.getBoolean(KEY_FRESH_PENDING, false)

    fun dismissFreshStart() {
        prefs.edit().putBoolean(KEY_FRESH_PENDING, false).commit()
    }

    /**
     * Records [currentVersion] as the version seen at this open.
     * Returns true exactly when a version was recorded before AND
     * the new one is higher — an update landed between opens — and
     * raises the update-note flag in the same write. The first-ever
     * record (a fresh install) returns false: the fresh-start note
     * owns that moment, and claiming an "update" there would be a
     * lie of the opposite sign.
     */
    fun registerOpen(currentVersion: Long): Boolean {
        val seen = prefs.getLong(KEY_SEEN_VERSION, 0L)
        if (seen == currentVersion) return false
        val updated = seen != 0L && currentVersion > seen
        val editor = prefs.edit().putLong(KEY_SEEN_VERSION, currentVersion)
        if (updated) editor.putBoolean(KEY_UPDATE_PENDING, true)
        editor.commit()
        return updated
    }

    fun updatePending(): Boolean = prefs.getBoolean(KEY_UPDATE_PENDING, false)

    fun dismissUpdate() {
        prefs.edit().putBoolean(KEY_UPDATE_PENDING, false).commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_install_notes"
        private const val KEY_FRESH_PENDING = "fresh_start_pending"
        private const val KEY_UPDATE_PENDING = "update_pending"
        private const val KEY_SEEN_VERSION = "seen_version_code"
    }
}
