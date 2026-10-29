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

/**
 * Silent Notification Catcher & Persistent Digest:
 * - Intercepts and suppresses notifications from blocked apps during active sessions.
 * - Explicitly excludes incoming phone/VoIP call notifications (Notification.CATEGORY_CALL)
 *   so urgent calls are never suppressed.
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
