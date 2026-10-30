package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

/**
 * Issue #339 (Second-pass F3): the break-end alarm's delivery.
 * Posts the dedicated 'Break's over' notification exactly once —
 * the [BreakEndScheduler] ledger is checked before posting and
 * stamped after, so a re-delivered alarm (process death, reboot
 * re-arm) can never double-fire the return call.
 */
class BreakEndReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val breakEndsWallMillis =
            intent?.getLongExtra(BreakEndScheduler.EXTRA_BREAK_END_MILLIS, 0L) ?: 0L
        if (!BreakEndScheduler.shouldNotifyBreakEnd(
                BreakEndScheduler.lastNotified(context),
                breakEndsWallMillis
            )
        ) {
            return
        }
        BreakEndScheduler.markNotified(context, breakEndsWallMillis)

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Break ended",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "The call back to the desk when a Safe Break ends"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Break's over")
            .setContentText("Back to your session.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "BreakEnded"
        const val NOTIFICATION_ID = 820000
    }
}
