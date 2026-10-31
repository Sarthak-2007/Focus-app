package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Issue #339 (Audit R5 supplement — Freedom, Second-pass F3):
 * Freedom's break is a native sub-session whose end is a scheduled
 * event — an exact alarm plus a deduped 'break over' notification.
 * Monolith's Safe Break was two countdown fields on the session
 * row, counted down inside progress ticks: if the process died or
 * stalled, the return point was silently swallowed.
 *
 * This scheduler makes the break's end a scheduled event, the
 * shape the audit's Fix names:
 *  - armed at break entry with `setExactAndAllowWhileIdle` (the
 *    SCHEDULE_EXACT_ALARM permission is already declared and the
 *    RoutineScheduler already uses this exact call), falling back
 *    to `setAndAllowWhileIdle` if the exact grant is ever denied;
 *  - delivered to [BreakEndReceiver], which posts the dedicated
 *    HIGH-importance 'Break's over' notification;
 *  - deduped by a persisted ledger (last notified break-end
 *    instant), so the notification fires exactly once even if the
 *    process was dead at the boundary and the alarm is re-delivered.
 */
object BreakEndScheduler {

    const val EXTRA_BREAK_END_MILLIS = "com.example.extra.BREAK_END_MILLIS"
    const val EXTRA_SESSION_ID = "com.example.extra.BREAK_END_SESSION_ID"

    private const val REQUEST_CODE = 902913
    private const val PREFS_NAME = "break_end_ledger"
    private const val KEY_LAST_NOTIFIED = "last_notified_break_end_millis"

    private fun pendingIntent(context: Context, sessionId: Int, breakEndsWallMillis: Long): PendingIntent {
        val intent = Intent(context, BreakEndReceiver::class.java).apply {
            putExtra(EXTRA_BREAK_END_MILLIS, breakEndsWallMillis)
            putExtra(EXTRA_SESSION_ID, sessionId)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /** Arm the break-end alarm for [breakEndsWallMillis]. */
    fun schedule(context: Context, sessionId: Int, breakEndsWallMillis: Long) {
        val app = context.applicationContext
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pi = pendingIntent(app, sessionId, breakEndsWallMillis)
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, breakEndsWallMillis, pi)
        } catch (_: SecurityException) {
            // Exact-alarm grant denied/revoked: an inexact
            // while-idle alarm still lands the return call —
            // degraded, never silently absent.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, breakEndsWallMillis, pi)
        }
    }

    /** Disarm (break ended early, session finalised, superseded). */
    fun cancel(context: Context) {
        val app = context.applicationContext
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        // The PendingIntent identity must match schedule()'s; the
        // extras do not participate in identity, so a probe intent
        // with no extras cancels the armed one.
        val probe = PendingIntent.getBroadcast(
            app,
            REQUEST_CODE,
            Intent(app, BreakEndReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        ) ?: return
        alarmManager.cancel(probe)
        probe.cancel()
    }

    /** The dedup rule, pure: notify exactly when this break-end
     *  instant has not already been notified. */
    fun shouldNotifyBreakEnd(lastNotifiedMillis: Long, scheduledMillis: Long): Boolean =
        scheduledMillis > 0L && lastNotifiedMillis != scheduledMillis

    /**
     * Issue #155 (WING-F lifecycle edges): the session gate, pure.
     *
     * The alarm is disarmed at every break EXIT
     * (advancePastBreak / resumeBlockFromMidBreak cancel it), but
     * a session that ENDS mid-break — user stop, emergency valve,
     * routine finalise, supersede by a new session — never
     * cancelled it, and the receiver posted "Break's over / Back
     * to your session" for a session that no longer existed.
     * [EXTRA_SESSION_ID] was written at schedule time and read
     * nowhere; this gate is its reader.
     *
     * The gate deliberately does NOT require the row to still be
     * in a break: at a legitimate expiry the shield tick may
     * already have flipped the break off before the alarm's
     * delivery runs, and that delivery must still land. It
     * requires only that the session the break belonged to is
     * still THE live session. A scheduled id of 0 (an alarm armed
     * before the extra existed) is allowed — legacy deliveries
     * keep the pre-gate behaviour rather than being swallowed.
     */
    fun sessionGateAllows(scheduledSessionId: Int, activeSessionId: Int?): Boolean =
        scheduledSessionId <= 0 || activeSessionId == scheduledSessionId


    fun lastNotified(context: Context): Long =
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_NOTIFIED, 0L)

    fun markNotified(context: Context, breakEndsWallMillis: Long) {
        // Issue #155 WING-F (SMALL-2): commit(), not apply(). This
        // ledger IS the exactly-once claim for the break-end call;
        // apply()'s in-memory update is instant but its disk flush
        // is asynchronous, so a process death in the flush window
        // resurrected the pre-mark value and the re-delivered alarm
        // posted "Break's over" a second time. The app's other
        // claim-stamps already hold this standard (WatchStore's
        // LAST PROVED and the seal marker both commit() for exactly
        // this reason), and the write is one long, once per break.
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_NOTIFIED, breakEndsWallMillis)
            .commit()
    }

    /** SDK gate kept beside the scheduler for the receiver's
     *  channel work (channels exist from O). */
    fun channelsSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}
