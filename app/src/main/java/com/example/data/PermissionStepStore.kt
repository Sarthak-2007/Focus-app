package com.example.data

import android.content.Context
import com.example.service.WatchItemId

/**
 * Issue #324, finding 4 (build half, as relabelled on the record:
 * "primer re-creation + binding + persisted step state"): the
 * permission flow's memory.
 *
 * Before this store, the flow had NO persisted state anywhere:
 * the primer modal was deleted, and PermissionReturnService
 * (#308) — the return trip from a Settings pane — keeps its one
 * watched item in a @Volatile field with a 180-second deadline.
 * A process death mid-sequence (Android kills background
 * processes routinely, and a Settings trip is exactly a
 * background stay) forgot which permission he was in the middle
 * of granting, and the sequence restarted from zero.
 *
 * What rests here is the minimum the resume needs, on the
 * PreArmStore pattern — a small SharedPreferences store OUTSIDE
 * Room, because these are scalars with no schema business (the
 * migration-train ruling on #324: this half takes no Room
 * migration):
 *
 *  - the IN-FLIGHT step: the permission a FIX trip was opened
 *    for and has not yet landed. Written when a trip starts
 *    (the primer's FIX key or Park's own FIX keys, both through
 *    MainActivity.openFixPane), cleared when the sweep reads
 *    that grant as landed.
 *  - the ATTEMPTED set: every permission a trip was ever opened
 *    for, so the flow can tell "never started" from "started,
 *    interrupted" without inventing progress.
 *
 * What is deliberately NOT stored: completion. Completion is
 * derived from the live grants themselves (the Watch sweep's
 * own measurements) every time it is asked — a stored "done"
 * bit would go stale the moment a grant is revoked in Settings,
 * and this store never outranks the sweep's facts.
 *
 * Writes COMMIT (the PreArmStore standard): a deferred write
 * must not lose a step the flow has already shown as resting.
 */
class PermissionStepStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The permission whose FIX trip is in flight, or null when
     *  no trip is open. A stored name that no longer names a
     *  permission reads as null, never as a crash. */
    fun inFlightItem(): WatchItemId? =
        prefs.getString(KEY_IN_FLIGHT, null)?.let { name ->
            runCatching { WatchItemId.valueOf(name) }.getOrNull()
        }

    /** A FIX trip for [item] starts now: it becomes the in-flight
     *  step and joins the attempted set. */
    fun beginTrip(item: WatchItemId) {
        val attempted = attemptedItems() + item
        prefs.edit()
            .putString(KEY_IN_FLIGHT, item.name)
            .putStringSet(KEY_ATTEMPTED, attempted.map { it.name }.toSet())
            .putLong(KEY_IN_FLIGHT_SINCE, System.currentTimeMillis())
            .commit()
    }

    /** The in-flight step's grant has landed (or the flow is
     *  being reset): no trip is open. The attempted set stays —
     *  it is history, not progress. */
    fun clearInFlight() {
        prefs.edit().remove(KEY_IN_FLIGHT).remove(KEY_IN_FLIGHT_SINCE).commit()
    }

    /** Every permission a FIX trip was ever opened for. */
    fun attemptedItems(): Set<WatchItemId> =
        (prefs.getStringSet(KEY_ATTEMPTED, emptySet()) ?: emptySet())
            .mapNotNull { name -> runCatching { WatchItemId.valueOf(name) }.getOrNull() }
            .toSet()

    /** True once any FIX trip has ever started on this install. */
    fun hasBegun(): Boolean = prefs.contains(KEY_ATTEMPTED)

    companion object {
        private const val PREFS_NAME = "monolith_permission_steps"
        private const val KEY_IN_FLIGHT = "in_flight_item"
        private const val KEY_IN_FLIGHT_SINCE = "in_flight_since_millis"
        private const val KEY_ATTEMPTED = "attempted_items"
    }
}

/**
 * The permission sequence's pure logic (issue #324, finding 4):
 * which step stands now, given the LIVE grants and the persisted
 * in-flight step. Grants are the truth — this object never reads
 * the store; callers hand it both facts.
 *
 * The sequence order is the board's own cheapest-first order,
 * [WatchItemId.PERMISSIONS] (issue #309 F5): the read-only usage
 * ask leads, the invasive grants close. A step is never skipped
 * while ungranted, and a granted step never reopens unless its
 * grant is revoked — the revoked step, and only it, stands again.
 */
object PermissionStepLogic {

    /** The step the flow stands on: the in-flight step while its
     *  grant is still missing (an interrupted trip resumes exactly
     *  where it stopped); otherwise the first ungranted step in
     *  board order; null when every step is granted. */
    fun currentStep(
        grants: Map<WatchItemId, Boolean>,
        inFlight: WatchItemId?
    ): WatchItemId? {
        if (inFlight != null && inFlight in WatchItemId.PERMISSIONS &&
            grants[inFlight] != true
        ) {
            return inFlight
        }
        return WatchItemId.PERMISSIONS.firstOrNull { grants[it] != true }
    }

    /** True when every permission step reads granted. */
    fun isComplete(grants: Map<WatchItemId, Boolean>): Boolean =
        WatchItemId.PERMISSIONS.all { grants[it] == true }

    /** 1-based position of [step] in the sequence, for the
     *  primer's step print; 0 for a non-step item. */
    fun stepNumber(step: WatchItemId): Int =
        WatchItemId.PERMISSIONS.indexOf(step) + 1

    val stepCount: Int get() = WatchItemId.PERMISSIONS.size
}

/**
 * Issue #324, finding 4: each permission step's own explainer —
 * what the grant actually does in THIS app, stated before the
 * ask (the #58 design half: explained per-permission, after the
 * value is shown). Every line is checked against the code that
 * consumes the grant; in particular the device-admin line claims
 * no uninstall protection, because issue #44 established the
 * admin declares no policies and prevents nothing.
 */
fun permissionExplainer(item: WatchItemId): String = when (item) {
    WatchItemId.USAGE_ACCESS ->
        "Lets Monolith see which apps you open and for how long, so the shield knows a blocked app when it appears. It reads usage, never content."
    WatchItemId.OVERLAY ->
        "Lets Monolith draw over other apps — the floating timer pill that rides above a peek or a break and counts it down."
    WatchItemId.NOTIFICATION_LISTENER ->
        "Lets Monolith hold your notifications while a block runs and hand them back afterwards, instead of letting them pull you out of the block."
    WatchItemId.ACCESSIBILITY ->
        "The shield itself. Monolith's accessibility service is what notices a blocked app opening and steps in front of it. Without this, nothing can be blocked."
    WatchItemId.DEVICE_ADMIN ->
        "Registers Monolith as a device admin. On its own it blocks nothing and does not prevent uninstalling the app — it is one of the grants the guard watch keeps an eye on."
    WatchItemId.BATTERY ->
        "Exempts Monolith from battery optimisation, so Android does not quietly kill the shield in the background mid-block."
    else ->
        "One of the grants the guard watch measures."
}
