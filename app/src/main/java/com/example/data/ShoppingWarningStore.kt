package com.example.data

import android.content.Context

/**
 * Issue #129 D-2 (Audit R3b, unlisted-distraction): the one-time
 * ReviewSheet warning for the SHOPPING fail-closed default.
 *
 * The panel verdict (on #129, carried 64–4): SHOPPING stands shut
 * in a session by DEFAULT, not by his ruling — the default
 * profiles list SHOPPING in their blocked categories (so a
 * profile lands every SHOPPING row LOCKED), and the classifier
 * files every UNRECOGNISED app as SHOPPING, where the D-1
 * fail-closed birth holds it LOCKED until he opens it himself.
 * He never ruled shopping shut; a silent, unruled block is the
 * app legislating in his name, and under TELL-THE-TRUTH the
 * silence is a lie. So the Shield review surface says it ONCE,
 * pre-seal, before the first mid-session slam can read as a bug:
 * one persisted one-time flag + one sheet row, display-only —
 * this file changes no enforcement anywhere.
 */
object ShoppingFailClosedWarning {

    /**
     * The warning is owed exactly when it has never been
     * dismissed AND the catalogue holds at least one SHOPPING
     * row. The category law hangs over every SHOPPING row
     * whatever its current tier — a profile tap lands the whole
     * category LOCKED (MonolithRepository.applyProfile), and a
     * guess-filed row is born LOCKED — so any SHOPPING row is
     * exposure to the unruled default. With no SHOPPING row on
     * the phone there is nothing to warn about, and printing
     * the warning there would claim a block that does not exist.
     */
    fun owed(apps: List<BlockedAppEntity>, dismissed: Boolean): Boolean =
        !dismissed && apps.any { it.category == AppCategory.SHOPPING.name }
}

/**
 * The one persisted one-time flag behind the D-2 warning,
 * stored OUTSIDE Room on the InstallNoteStore pattern: readable
 * at first composition without touching the Room schema, and
 * written with COMMIT — a shown-once fact is exactly the class
 * of fact this app exists not to lose. Once dismissed, the
 * warning never prints again, even if more SHOPPING apps are
 * installed later: one-time means one time.
 */
class ShoppingWarningStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun dismissed(): Boolean = prefs.getBoolean(KEY_DISMISSED, false)

    fun dismiss() {
        prefs.edit().putBoolean(KEY_DISMISSED, true).commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_shopping_warning"
        private const val KEY_DISMISSED = "fail_closed_warning_dismissed"
    }
}
