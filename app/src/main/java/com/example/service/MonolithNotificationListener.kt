package com.example.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.AppBlockTier
import com.example.data.CapturedNotificationEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Notification.CATEGORY_TRANSPORT's value (media playback). */
internal const val MEDIA_TRANSPORT_CATEGORY = "transport"

/** Notification.EXTRA_TEMPLATE value for a media-style notification. */
internal const val MEDIA_STYLE_TEMPLATE = "android.app.Notification\$MediaStyle"

/**
 * Issue #132 (music-during-focus): a media PLAYBACK notification
 * is not a held message — it is the player's transport controls
 * (pause / skip), and its "text" is a song title, not a message.
 * Identified only by markers the posting app itself supplies:
 * the transport category, a media-session token in the extras,
 * or the MediaStyle template. Pure, so the exemption is
 * unit-testable without a Notification instance. A player's
 * OTHER notifications (a new-episode promo with none of these
 * markers) are not exempt and are still held as before.
 */
internal fun isMediaPlaybackNotification(
    category: String?,
    hasMediaSession: Boolean,
    template: String?
): Boolean =
    category == MEDIA_TRANSPORT_CATEGORY || hasMediaSession || template == MEDIA_STYLE_TEMPLATE

/**
 * Silent Notification Catcher & Persistent Digest:
 * - Intercepts and suppresses notifications from blocked apps during active sessions.
 * - Explicitly excludes incoming phone/VoIP call notifications (Notification.CATEGORY_CALL)
 *   so urgent calls are never suppressed.
 * - Explicitly excludes media playback notifications (see
 *   [isMediaPlaybackNotification]): a song is not a message, and
 *   cancelling the notification would strip the player's
 *   pause/skip controls from the shade and lock screen.
 * - Saves captured notifications to Room persistent disk storage so they survive background
 *   restarts and can be read, cleared, or tapped to open after studying.
 */
class MonolithNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        // Watch Store BIND registry (chrome C3): the listener proves
        // its binding by EXISTING, stamped by its own lifecycle.
        @Volatile
        var instance: MonolithNotificationListener? = null
            internal set
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        WatchStore.onNotificationListenerBound(true)
    }

    override fun onListenerDisconnected() {
        if (instance === this) {
            instance = null
            WatchStore.onNotificationListenerBound(false)
        }
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == applicationContext.packageName || pkg.startsWith("com.android.systemui")) return

        // Opportunistic routine reconcile (throttled to 30 s inside
        // the heartbeat) — same reasoning as the accessibility feed.
        RoutineHeartbeat.tickOpportunistic(applicationContext)

        // Never suppress incoming cellular or VoIP call notifications
        if (notification.category == Notification.CATEGORY_CALL ||
            notification.category == Notification.CATEGORY_ALARM
        ) {
            return
        }

        // Never suppress media playback (Issue #132): the
        // notification IS the player's pause/skip controls, not a
        // message to hold for the digest — at any tier.
        if (isMediaPlaybackNotification(
                category = notification.category,
                hasMediaSession = notification.extras
                    ?.containsKey(Notification.EXTRA_MEDIA_SESSION) == true,
                template = notification.extras
                    ?.getString(Notification.EXTRA_TEMPLATE)
            )
        ) {
            return
        }

        val extras = notification.extras
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (title.isEmpty() && text.isEmpty()) return

        scope.launch {
            val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
            val activeSession = dao.getActiveSessionSnapshot() ?: return@launch
            if (!activeSession.isActive) return@launch

            val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkg }
            if (appRule != null && appRule.resolvedTier != AppBlockTier.OPEN) {
                dao.insertCapturedNotification(
                    CapturedNotificationEntity(
                        packageName = pkg,
                        appName = appRule.appName,
                        senderTitle = title.ifEmpty { appRule.appName },
                        // The captured body is the notification's
                        // own text or nothing (Truth Car 3,
                        // appendix): a title-only notification
                        // stores an EMPTY body — the digest prints
                        // the title alone. The old fallback
                        // sentence was invented copy wearing the
                        // sender's name.
                        messageBody = text
                    )
                )
                try {
                    cancelNotification(sbn.key)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onDestroy() {
        // Chrome wave: the Watch Store's listener stamp dies with
        // the binding (merged into this, the class's one onDestroy).
        if (instance === this) {
            instance = null
            WatchStore.onNotificationListenerBound(false)
        }
        scope.cancel()
        super.onDestroy()
    }
}
