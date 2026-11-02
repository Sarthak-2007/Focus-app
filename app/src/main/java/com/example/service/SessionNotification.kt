package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.R

/**
 * P8b — THE ONE NOTIFICATION (BUILD-SR-6, build-first-usable).
 *
 * The session's ONLY lock-screen presence (ledger E-4): exactly one
 * notification for the sealed session — one plain line, a statement
 * of state, in Atkinson at the OS presentation size (Stage 5 HLD-1:
 * form law only — size and tracking are the OS's, and this file sets
 * neither; the trial 14sp presentation is a form diagram, not a
 * step). KB10 NO VERDICT stands over the whole context.
 *
 * THE WORDING IS RULED, NOT CHOSEN HERE. Stage 5's type system,
 * Amendment A-6.2, rules it: "Locked until the hour" (FIT-S5-2,
 * concurred; P0 carries the same words as `MonolithType.Hld1Wording`
 * on agent/build-sr-foundation). That ruling supersedes Audit A
 * G-6's earlier "unfixed, build-chosen" framing on the record
 * (AL-0056 — the record governs; BUILD-SR-6's opening line flags the
 * supersession by name). The line carries no numbers at all — the
 * word "hour" is not a figure (FIG-8 claims no figure for this
 * context), and nothing beyond the state is said.
 *
 * ONE TEXT FIELD ONLY (TYPE-SR-4 Spec 1 §4, agreed by letter): the
 * line stands in the content text; the content title is never set —
 * never both fields, never a duplicated line, no BigText/expanded
 * style, the OS's standard template only (no RemoteViews).
 *
 * IT NEVER PROMOTES ITSELF. The spec below is the whole behaviour:
 * a fixed ID (one card, updated in place, never a second), a
 * low-importance silent channel, no sound, no vibration, no badge
 * number, no chronometer, no actions, no progress — it stands
 * while the seal stands and is cancelled at the lifting. Its
 * public (lock-screen) version is the SAME one line in the same
 * one field: a statement of state is safe to show whole, so
 * nothing is hidden and nothing extra is revealed. Held/gap
 * arrivals post nothing and update nothing (MOTION-SR-3).
 *
 * NO RE-POST AFTER DISMISSAL (Spec 1 §5.6): if he dismisses the
 * line mid-session, the session does not re-post it. The card's
 * delete intent records the dismissal for the standing session;
 * [SessionNotification.show] honours it until the lifting clears
 * it. Exactly one seal stands at a time, so the dismissal flag is
 * one bit, keyed to the standing session by that fact alone.
 *
 * WIRING, LANDED (BUILD-SR-14, the follow-on BUILD-SR-13's
 * composition record §Cures.6 left standing): P2's engine bridge
 * ([StrongroomEngineBridge]) is this poster's production caller —
 * [SessionNotification.beginSession] at the swing in
 * `activateSeal`, [SessionNotification.cancel] at the lifting in
 * `onFiringWake` and at seal death in `onBoot` (AJAR and
 * BROKEN_TAMPER), and [SessionNotification.show] on an INTACT
 * boot (the standing session's card re-posted after a reboot,
 * its dismissal bit honoured — beginSession is never used there,
 * as it would clear a dismissal he already made). The shield
 * service's legacy standing card stands retired as a lock-screen
 * presence (composition cure 6, VISIBILITY_SECRET, no public
 * version): this card is the session's only lock-screen presence
 * (E-4). Device behaviour — how his OnePlus renders this card on
 * its lock screen — stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */
object SessionNotificationSpec {

    /** One card, one ID, for the whole programme: re-posting updates
     *  this card in place; a second card cannot exist. */
    const val NOTIFICATION_ID: Int = 2711

    const val CHANNEL_ID: String = "monolith_session_state"

    /** The ruled line — Stage 5 Amendment A-6.2. Must read exactly
     *  as P0's `MonolithType.Hld1Wording` when the lanes compose;
     *  the P8 test pins the literal so a drift fails the build. */
    const val WORDING: String = "Locked until the hour"

    /** One text field only (Spec 1 §4): the line is the content
     *  text; a content title is NEVER set. */
    const val HAS_CONTENT_TITLE: Boolean = false

    // The never-promotes-itself properties, as named constants so
    // the tests pin the behaviour, not a builder's mood.
    const val IMPORTANCE: Int = NotificationManager.IMPORTANCE_LOW
    const val PRIORITY: Int = NotificationCompat.PRIORITY_LOW
    const val ONGOING: Boolean = true
    const val SILENT: Boolean = true
    const val ONLY_ALERT_ONCE: Boolean = true
    const val USES_CHRONOMETER: Boolean = false
    const val HAS_BADGE_NUMBER: Boolean = false
    const val HAS_ACTIONS: Boolean = false

    /** The dismissal decision, pure: a dismissed line is not
     *  re-posted for the standing session. */
    fun shouldPost(dismissedThisSession: Boolean): Boolean = !dismissedThisSession
}

/** The dismissal bit — one standing session, one bit. */
internal object SessionNotificationStore {
    private const val PREFS = "monolith_session_notification"
    private const val KEY_DISMISSED = "dismissed"

    fun isDismissed(context: Context): Boolean =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_DISMISSED, false)

    fun markDismissed(context: Context) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DISMISSED, true).apply()
    }

    fun clear(context: Context) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DISMISSED, false).apply()
    }
}

/** The card's delete intent target: a dismissal is a decision the
 *  session honours (Spec 1 §5.6). */
class SessionNotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        SessionNotificationStore.markDismissed(context)
    }
}

object SessionNotification {

    /** A seal takes: a new session begins — the dismissal bit of
     *  any earlier session is cleared and the one card is posted. */
    fun beginSession(context: Context) {
        SessionNotificationStore.clear(context)
        show(context)
    }

    /** Post (or update in place) the session's one card — unless
     *  he has dismissed it for this session, in which case the
     *  session stays silent (Spec 1 §5.6). */
    fun show(context: Context) {
        val appContext = context.applicationContext
        if (!SessionNotificationSpec.shouldPost(SessionNotificationStore.isDismissed(appContext))) return
        ensureChannel(appContext)
        val deleteIntent = PendingIntent.getBroadcast(
            appContext,
            SessionNotificationSpec.NOTIFICATION_ID,
            Intent().setComponent(ComponentName(appContext, SessionNotificationDismissReceiver::class.java)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        fun oneLineBuilder() = NotificationCompat.Builder(appContext, SessionNotificationSpec.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentText(appContext.getString(R.string.copy_10_2_notification))
        val publicVersion = oneLineBuilder()
            .setOngoing(SessionNotificationSpec.ONGOING)
            .build()
        val notification = oneLineBuilder()
            .setPriority(SessionNotificationSpec.PRIORITY)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOngoing(SessionNotificationSpec.ONGOING)
            .setSilent(SessionNotificationSpec.SILENT)
            .setOnlyAlertOnce(SessionNotificationSpec.ONLY_ALERT_ONCE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setDeleteIntent(deleteIntent)
            .build()
        // COMPOSITION CURE (BUILD-SR-13): the composed head's lint
        // run (41424101165) named one NEW error over the standing
        // 11 — MissingPermission at this notify call. On API 33+
        // the post permission can be absent; the one line then
        // simply does not post (the seal itself is not the card).
        // The areNotificationsEnabled check is the lint-honest
        // guard; behaviour otherwise unchanged.
        val manager = NotificationManagerCompat.from(appContext)
        if (manager.areNotificationsEnabled()) {
            manager.notify(SessionNotificationSpec.NOTIFICATION_ID, notification)
        }
    }

    /** The lifting takes the card down and clears the dismissal
     *  bit: no session, no presence, no residue. */
    fun cancel(context: Context) {
        NotificationManagerCompat.from(context.applicationContext)
            .cancel(SessionNotificationSpec.NOTIFICATION_ID)
        SessionNotificationStore.clear(context)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (manager.getNotificationChannel(SessionNotificationSpec.CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            SessionNotificationSpec.CHANNEL_ID,
            "Session",
            SessionNotificationSpec.IMPORTANCE
        ).apply {
            description = "The session's one plain line, while a seal stands."
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
        }
        manager.createNotificationChannel(channel)
    }
}
