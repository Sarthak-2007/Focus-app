package com.example.service

import android.app.NotificationManager
import android.content.Context

/**
 * Issue #143 (night-flip-mid-session), finding F4: arming never
 * checked Do-Not-Disturb — zero DND/NotificationManager-policy hits
 * in the whole app. A man seals a session in a quiet room and the
 * phone still rings at full voice, because Monolith blocked the
 * apps and never spoke to the system's own silence.
 *
 * The cure is a CHECK with a truthful outcome, in the sealed
 * state's own accounting:
 *  - DND already on → Monolith says so and touches nothing; the
 *    user's own silence is never "restored" away at finish.
 *  - DND off + policy access granted → Monolith ENGAGES DND at the
 *    seal (alarms still ring — a study block is not a reason to
 *    sleep through an alarm) and RESTORES the exact filter it
 *    found when the session ends.
 *  - DND off + NO policy access → Monolith says plainly that it
 *    cannot silence the phone and names the grant that would let
 *    it. It never pretends the phone is quiet.
 *
 * His ruling stands: notification CONTENT is never read or stored
 * here — the only thing persisted is the previous interruption
 * FILTER (one integer: the system's own state), kept solely so the
 * restore is exact.
 */
object DndGate {

    const val PREFS = "monolith_dnd_gate"
    const val KEY_ENGAGED_BY_MONOLITH = "engaged_by_monolith"
    const val KEY_PREVIOUS_FILTER = "previous_filter"

    enum class Decision {
        /** The phone is already silenced by the user's own hand. */
        ALREADY_QUIET,
        /** Monolith may engage DND at the seal and restore after. */
        ENGAGE_ON_SEAL,
        /** DND is off and Monolith has no grant to change it. */
        CANNOT_ENGAGE_NO_ACCESS,
        /** The filter could not be read (no service / unknown). */
        UNKNOWN
    }

    /** The pure decision (unit-tested): filter + grant → outcome. */
    fun decide(currentFilter: Int, policyAccessGranted: Boolean): Decision = when {
        currentFilter == NotificationManager.INTERRUPTION_FILTER_NONE ||
            currentFilter == NotificationManager.INTERRUPTION_FILTER_ALARMS ||
            currentFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY ->
            Decision.ALREADY_QUIET
        currentFilter == NotificationManager.INTERRUPTION_FILTER_ALL ->
            if (policyAccessGranted) Decision.ENGAGE_ON_SEAL else Decision.CANNOT_ENGAGE_NO_ACCESS
        else -> Decision.UNKNOWN
    }

    /** The line the arm flow prints (pure, unit-tested). Null for
     *  UNKNOWN — an unreadable state prints nothing rather than a
     *  guess. */
    fun armLine(decision: Decision): String? = when (decision) {
        Decision.ALREADY_QUIET -> "DO NOT DISTURB: ALREADY ON — THE PHONE IS QUIET BY YOUR OWN HAND"
        Decision.ENGAGE_ON_SEAL -> "DO NOT DISTURB: MONOLITH WILL SILENCE THE PHONE AT THE SEAL AND RESTORE IT AT THE FINISH"
        Decision.CANNOT_ENGAGE_NO_ACCESS -> "DO NOT DISTURB: OFF — MONOLITH HAS NO PERMISSION TO SILENCE THE PHONE (GRANT IT IN PARK'S PERMISSIONS)"
        Decision.UNKNOWN -> null
    }

    /** The line after the seal, once the gate has acted. */
    fun sealedLine(engagedByMonolith: Boolean, currentFilter: Int): String? = when {
        engagedByMonolith -> "DO NOT DISTURB: ON — SET BY MONOLITH FOR THIS BLOCK, RESTORED AT THE FINISH"
        currentFilter == NotificationManager.INTERRUPTION_FILTER_ALL ->
            "DO NOT DISTURB: OFF — THE PHONE CAN STILL RING THROUGH THIS BLOCK"
        currentFilter == NotificationManager.INTERRUPTION_FILTER_UNKNOWN -> null
        else -> "DO NOT DISTURB: ON"
    }

    private fun manager(context: Context): NotificationManager? =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    fun currentDecision(context: Context): Decision {
        val nm = manager(context) ?: return Decision.UNKNOWN
        return decide(
            currentFilter = nm.currentInterruptionFilter,
            policyAccessGranted = nm.isNotificationPolicyAccessGranted
        )
    }

    /**
     * Called when a session seals. Engages DND only on the
     * ENGAGE_ON_SEAL decision, recording the prior filter for the
     * restore. Returns true when Monolith itself holds the gate.
     */
    fun engageAtSeal(context: Context): Boolean {
        val nm = manager(context) ?: return false
        if (currentDecision(context) != Decision.ENGAGE_ON_SEAL) return false
        val previous = nm.currentInterruptionFilter
        return try {
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_ENGAGED_BY_MONOLITH, true)
                .putInt(KEY_PREVIOUS_FILTER, previous)
                .apply()
            true
        } catch (_: SecurityException) {
            false
        }
    }

    /**
     * Called when the session ends (any ending — finish, disarm,
     * valve, annul). Restores ONLY a gate Monolith itself engaged;
     * a user's own DND is never touched. Safe to call repeatedly:
     * the flag clears on the first restore.
     */
    fun restoreAfterSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ENGAGED_BY_MONOLITH, false)) return
        val previous = prefs.getInt(
            KEY_PREVIOUS_FILTER, NotificationManager.INTERRUPTION_FILTER_ALL
        )
        // Clear first: a failed restore must not loop forever, and
        // the record's job is done either way.
        prefs.edit()
            .putBoolean(KEY_ENGAGED_BY_MONOLITH, false)
            .remove(KEY_PREVIOUS_FILTER)
            .apply()
        val nm = manager(context) ?: return
        if (!nm.isNotificationPolicyAccessGranted) return
        try {
            nm.setInterruptionFilter(previous)
        } catch (_: SecurityException) {
            // The grant was revoked mid-session; nothing truthful
            // left to do — the sealed line already said the state.
        }
    }

    fun engagedByMonolith(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENGAGED_BY_MONOLITH, false)

    fun currentFilter(context: Context): Int =
        manager(context)?.currentInterruptionFilter
            ?: NotificationManager.INTERRUPTION_FILTER_UNKNOWN
}
