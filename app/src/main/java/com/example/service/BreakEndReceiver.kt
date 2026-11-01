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
 * the session gone or replaced suppresses the card. Deliveries
 * carrying no session id (alarms armed before the gate existed)
 * skip the read entirely: the gate allows id 0 against every
 * store state (pinned in BreakEndSessionGateTest), so those post
 * on the spot, exactly as the ungated receiver did.
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
        if (scheduledSessionId == 0) {
            // No session to gate on: sessionGateAllows(0, ·) is
            // unconditionally true, so the read could only ever
            // allow — post inline, the pre-gate behaviour.
            postBreakOver(context, breakEndsWallMillis)
            return
        }
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
                    postBreakOver(appContext, breakEndsWallMillis)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postBreakOver(context: Context, breakEndsWallMillis: Long) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
        // Issue #155 WING-F (SMALL-2): the dedupe ledger is stamped
        // only once the post is actually possible. Before this, the
        // mark landed BEFORE the manager was even obtained — a null
        // manager (or any failure above the post) consumed the
        // break-end instant forever: marked notified, never
        // notified, and the dedupe rule then suppressed every
        // legitimate re-delivery.
        BreakEndScheduler.markNotified(context, breakEndsWallMillis)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Issue #94 (routed stray ex #155, FEEL-2): this channel
            // used to be created with NO sound/vibration specified,
            // so a break-end stacked the channel's default system
            // sound + vibration on top of the engine's break-end
            // PCM cue and haptic summons — two voices, one event.
            // Wing law: per event the engine OR the channel speaks,
            // never both; break-end is engine-owned, so the channel
            // is EXPLICITLY silent and carries only the visual card.
            // Channels are immutable once created, so the specified
            // behaviour lives under a v2 id and the legacy id is
            // deleted — the #155 F4 migration pattern.
            manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Break ended",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "The call back to the desk when a Safe Break ends"
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
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
            .setSmallIcon(R.drawable.ic_stat_shield)
            .setContentTitle("Break's over")
            .setContentText("Back to your session.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            // Issue #155 mechanics census (SMALL-3, WING-F): every
            // sibling moment-card in the shield estate self-expires —
            // the F3 completion card and (PR #540) the SESSION
            // COMPLETE summons both carry the 45-minute timeout.
            // This card carried NONE and nothing in the codebase
            // cancels id 820000, so an untapped "Break's over" could
            // linger in the shade indefinitely. Same 45 minutes: its
            // news value is the return to the desk, and that decays
            // on the same clock as the completion receipt's.
            .setTimeoutAfter(BREAK_END_TIMEOUT_MS)
            .setContentIntent(contentIntent)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "BreakEnded_v2"

        /** Issue #94 (routed stray ex #155): the pre-specification
         *  break-end channel — unspecified sound/vibration defaults,
         *  immutable on-device; deleted wherever the v2 channel is
         *  created so its defaults die with it. */
        const val LEGACY_CHANNEL_ID = "BreakEnded"
        const val NOTIFICATION_ID = 820000

        /** Issue #155: self-expiry for the break-end card, matching
         *  the shield estate's completion timeout
         *  (MonolithShieldService.COMPLETION_TIMEOUT_MS). */
        const val BREAK_END_TIMEOUT_MS = 45 * 60 * 1000L
    }
}
