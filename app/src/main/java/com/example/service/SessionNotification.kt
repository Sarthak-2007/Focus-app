package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
 * neither). KB10 NO VERDICT stands over the whole context.
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
 * IT NEVER PROMOTES ITSELF. The spec below is the whole behaviour:
 * a fixed ID (one card, updated in place, never a second), a
 * low-importance silent channel, no sound, no vibration, no badge
 * number, no chronometer, no actions, no big text, no auto-cancel
 * games — it stands while the seal stands and is cancelled at the
 * lifting. Its public (lock-screen) version is the SAME one line:
 * a statement of state is safe to show whole, so nothing is hidden
 * and nothing extra is revealed.
 *
 * SEAM, NAMED: this is a poster, not a wiring. P2's engine wiring
 * (BUILD-SR-3's second wave) calls [SessionNotification.show] when
 * a seal takes and [SessionNotification.cancel] when it lifts; the
 * shield service's legacy standing card is composition work, not
 * silently rewritten here. Device behaviour — how his OnePlus
 * renders this card on its lock screen — stands
 * UNPROVEN-ON-DEVICE (Audit A G-7).
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

    /** The card's title is the app's own name and nothing more —
     *  the plain line itself is the content text. */
    const val TITLE: String = "Monolith"

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
}

object SessionNotification {

    /** Post (or update in place) the session's one card. */
    fun show(context: Context) {
        val appContext = context.applicationContext
        ensureChannel(appContext)
        val publicVersion = NotificationCompat.Builder(appContext, SessionNotificationSpec.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(SessionNotificationSpec.TITLE)
            .setContentText(SessionNotificationSpec.WORDING)
            .setOngoing(SessionNotificationSpec.ONGOING)
            .build()
        val notification = NotificationCompat.Builder(appContext, SessionNotificationSpec.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle(SessionNotificationSpec.TITLE)
            .setContentText(SessionNotificationSpec.WORDING)
            .setPriority(SessionNotificationSpec.PRIORITY)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOngoing(SessionNotificationSpec.ONGOING)
            .setSilent(SessionNotificationSpec.SILENT)
            .setOnlyAlertOnce(SessionNotificationSpec.ONLY_ALERT_ONCE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .build()
        NotificationManagerCompat.from(appContext)
            .notify(SessionNotificationSpec.NOTIFICATION_ID, notification)
    }

    /** The lifting takes the card down: no session, no presence. */
    fun cancel(context: Context) {
        NotificationManagerCompat.from(context.applicationContext)
            .cancel(SessionNotificationSpec.NOTIFICATION_ID)
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
