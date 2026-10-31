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
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Issue #339 (Second-pass F3): the break-end alarm's delivery.
 * Posts the dedicated 'Break's over' notification exactly once —
 * the [BreakEndScheduler] ledger is checked before posting and
 * stamped after, so a re-delivered alarm (process death, reboot
 * re-arm) can never double-fire the return call.
 *
 * Issue #155 (WING-F lifecycle edges): the delivery is also gated
 * on the session the break belonged to still being the live
 * session ([BreakEndScheduler.sessionGateAllows]). A session that
 * ended mid-break (stop / emergency valve / finalise / supersede)
 * never cancelled this alarm, and the ungated receiver announced
 * a return to a session that no longer existed. The gate read is
 * async (goAsync, the boot/package receivers' posture); a FAILED
 * read fails open — an unprovable gate must not swallow the
 * return call #339 exists to guarantee — while a read that proves
 * the session gone or replaced suppresses the card.
 */
class BreakEndReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
        val scheduledSessionId =
            intent?.getIntExtra(BreakEndScheduler.EXTRA_SESSION_ID, 0) ?: 0
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        scope.launch {
            try {
                val gateAllows = try {
                    val active = MonolithDatabase.getInstance(appContext)
                        .monolithDao().getActiveSessionSnapshot()
                        ?.takeIf { it.isActive }
                    BreakEndScheduler.sessionGateAllows(scheduledSessionId, active?.id)
                } catch (_: Exception) {
                    // Fail open on an unreadable store (see KDoc):
                    // suppression requires proof the session is gone.
                    true
                }
                if (gateAllows) {
                    BreakEndScheduler.markNotified(appContext, breakEndsWallMillis)
                    postBreakOver(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postBreakOver(context: Context) {
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
            // Issue #155 (WING-F lifecycle edges): this is news
            // about one instant, not a standing status — like the
            // F3 completion card it expires instead of posing as
            // current forever in the shade.
            .setTimeoutAfter(BREAK_OVER_TIMEOUT_MS)
            .setContentIntent(contentIntent)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "BreakEnded"
        const val NOTIFICATION_ID = 820000

        /** Issue #155: the break-over card self-expires on the
         *  same 45-minute horizon as the F3 completion card. */
        const val BREAK_OVER_TIMEOUT_MS = 45 * 60 * 1000L
    }
}
