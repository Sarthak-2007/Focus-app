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

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == applicationContext.packageName || pkg.startsWith("com.android.systemui")) return

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
                        messageBody = text.ifEmpty { "Suppressed notification during study session" }
                    )
                )
                try {
                    cancelNotification(sbn.key)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
