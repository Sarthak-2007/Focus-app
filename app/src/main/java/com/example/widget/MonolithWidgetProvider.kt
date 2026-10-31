package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.AppContainer
import com.example.MainActivity
import com.example.R
import com.example.data.DayWordStore
import com.example.data.MonolithDatabase
import com.example.data.RoutineSchedule
import com.example.service.MonolithShieldService
import com.example.service.SessionSealTransitions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Issue #155 (F1): the home-screen glance surface.
 *
 * Line 1 is always "MONOLITH". Line 2 prints only what the stored
 * state can prove: a live session's seal and end, the next live
 * routine's start, or OPEN. On ANY read failure it prints the
 * open-the-app line instead of guessing — a widget that cannot
 * prove its claim must not make one.
 *
 * RemoteViews supports no custom fonts, so the styling is plain
 * system text on purpose — no image is faked to look like brand type.
 */
class MonolithWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val status = readStatusLine(appContext)
                for (id in appWidgetIds) {
                    appWidgetManager.updateAppWidget(
                        id, buildViews(appContext, status.line)
                    )
                }
                // Issue #155 (IQ-4 edge 1/2): a SEALED render arms
                // its own expiry. The rendered RemoteViews are a
                // snapshot with no lifetime of their own — without
                // this wakeup a dead shield's last SEALED print
                // survives until the 30-minute period update.
                if (status.sealedBeatWallMillis > 0L) {
                    armSealedExpiry(appContext, appWidgetIds, status.sealedBeatWallMillis)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    /** One rendered glance line plus, when the line claims SEALED,
     *  the beat that proved it (0 otherwise). */
    private data class WidgetStatus(
        val line: String,
        val sealedBeatWallMillis: Long = 0L
    )

    /** Issue #155 (IQ-4 edge 1): one-shot wakeup at the rendered
     *  beat's freshness boundary, on the shield restart alarm's own
     *  pattern (setAndAllowWhileIdle). It re-invokes this provider's
     *  update; the re-render re-reads the truth and, if the shield
     *  is still beating, re-arms from the fresher beat. */
    private fun armSealedExpiry(context: Context, appWidgetIds: IntArray, beatWallMillis: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                ?: return
            val intent = Intent(context, MonolithWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            val pending = PendingIntent.getBroadcast(
                context, SEALED_EXPIRY_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                WidgetSealTruth.expiryWakeMillis(beatWallMillis),
                pending
            )
        } catch (_: Exception) {
            // No alarm service (or a revoked exact-alarm grant):
            // the period update remains the backstop, as before.
        }
    }

    private fun buildViews(context: Context, statusLine: String): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_monolith)
        views.setTextViewText(R.id.widget_title, "MONOLITH")
        views.setTextViewText(R.id.widget_status, statusLine)
        val launch = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        views.setOnClickPendingIntent(
            R.id.widget_root,
            PendingIntent.getActivity(
                context, 0, launch,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        return views
    }

    /** The one truthful status line, read defensively from Room. */
    private suspend fun readStatusLine(context: Context): WidgetStatus {
        return try {
            val dao = MonolithDatabase.getInstance(context).monolithDao()
            val now = System.currentTimeMillis()
            val session = dao.getActiveSessionSnapshot()?.takeIf { it.isActive }
            if (session != null) {
                // Issue #155, verifier residue (2): SEALED is a
                // claim about the SHIELD, not about the row. It
                // prints only when the shield's own truth proves
                // it — protecting witness standing for this session
                // AND a fresh persisted beat (WidgetSealTruth).
                // A live row beside a dead/blind shield prints the
                // honest unproven line, never SEALED.
                // Issue #155 (IQ-1 unified latch, WING-F): no
                // accessibility input is read here at all. The
                // shield's flip requires both doors and its demote
                // fires on either door closing, so the protecting
                // witness implies the accessibility door — the
                // widget never derives it a second time (the fork
                // IQ-1 rejected is removed, not re-homed).
                val sealedProven = WidgetSealTruth.sealedProven(
                    isActive = session.isActive,
                    protecting = MonolithShieldService.protecting,
                    protectingSessionId = MonolithShieldService.protectingSessionId,
                    sessionId = session.id,
                    beatWallMillis = session.shieldLastBeatWallMillis,
                    nowWallMillis = now,
                    shieldGapSeen = session.shieldGapSeen
                )
                if (!sealedProven) {
                    // Issue #155 (IQ-2 VC-2 + IQ-5 F-4): no
                    // row-asserted "SESSION LIVE", short enough to
                    // read whole at the smallest (3x1) placement.
                    WidgetStatus("SHIELD UNPROVEN \u2022 OPEN THE APP TO CHECK")
                } else when {
                    // Issue #155 (IQ-4 edge 8a): a Safe Break is a
                    // PARTIAL seal — PEEK apps are lawfully open
                    // while locked apps stay held. An unqualified
                    // SEALED here is a whole claim for a partial
                    // state; the break gets its own line, naming
                    // what is open and when the full seal resumes.
                    session.isInSafeBreak -> {
                        val resumes = now + session.safeBreakSecondsRemaining * 1000L
                        WidgetStatus(
                            "ON BREAK \u2022 ${session.intention} \u2022 PEEK APPS OPEN \u2022 SEAL RESUMES ${hhmm(resumes)}",
                            sealedBeatWallMillis = session.shieldLastBeatWallMillis
                        )
                    }
                    session.isStopwatchMode ->
                        WidgetStatus(
                            sealedLine(session.intention, "STOPWATCH, NO SET END"),
                            sealedBeatWallMillis = session.shieldLastBeatWallMillis
                        )
                    session.windowEndMillis > now ->
                        WidgetStatus(
                            sealedLine(session.intention, "until ${hhmm(session.windowEndMillis)}"),
                            sealedBeatWallMillis = session.shieldLastBeatWallMillis
                        )
                    else -> {
                        // Manual timed session: the end is now plus the
                        // whole-span remainder the model itself derives.
                        val end = now + session.spanRemainingSeconds * 1000L
                        WidgetStatus(
                            sealedLine(session.intention, "until ${hhmm(end)}"),
                            sealedBeatWallMillis = session.shieldLastBeatWallMillis
                        )
                    }
                }
            } else {
                // Issue #155 (T9, re-close bench): the NEXT ROUTINE
                // derivation is REFUSAL-AWARE. It used to read only
                // isLive + effectiveNextStartMillis (pause-aware),
                // so on an excused date (ILL / REST / FAMILY) or
                // over a tombstoned window the widget kept naming
                // a next seal the heartbeat was bound to refuse —
                // a future claim the machine knew was false. The
                // derivation now consumes the heartbeat's own
                // refusal set (excused filing dates + tombstones,
                // read exactly as RoutineHeartbeat reads them),
                // and where no unrefused start can be proven the
                // line FALLS SILENT — plain OPEN (WING-D C1).
                val repository = AppContainer.repository(context)
                val excusedDateIsos = repository.allDayWords().entries
                    .filter { DayWordStore.isExcuse(it.value) }
                    .map { it.key }
                    .toSet()
                val transitions = SessionSealTransitions.get(context)
                val next = dao.getAllRoutinesSnapshot()
                    .filter { RoutineSchedule.isLive(it) }
                    .mapNotNull { routine ->
                        RoutineSchedule.nextStartMillisRefusalAware(
                            routine, now, excusedDateIsos
                        ) { routineId, windowStart ->
                            transitions.isTombstoned(routineId, windowStart)
                        }?.let { start -> routine to start }
                    }
                    .minByOrNull { it.second }
                if (next != null) {
                    // Issue #155 (IQ-2 VC-3): routine-marked and
                    // day-qualified — "NEXT SEAL" stated the app's
                    // future act (Monolith is hand-armed; a routine
                    // is the user's, the app does not seal itself),
                    // and a bare HH:mm let tomorrow read as today.
                    // Issue #155 (WING-F): a blank routine name
                    // drops its segment: no doubled spaces and
                    // no dangling gap where the name would sit.
                    WidgetStatus(
                        if (next.first.name.isBlank()) {
                            "OPEN \u2022 NEXT ROUTINE ${dayLabel(next.second, now)} ${hhmm(next.second)}"
                        } else {
                            "OPEN \u2022 NEXT ROUTINE ${next.first.name} ${dayLabel(next.second, now)} ${hhmm(next.second)}"
                        }
                    )
                } else {
                    WidgetStatus("OPEN")
                }
            }
        } catch (_: Exception) {
            // Never a false claim: if the state cannot be read, say so.
            WidgetStatus("OPEN THE APP TO CHECK")
        }
    }

    /** Issue #155 (WING-F): the SEALED line's segments — a blank
     *  intention drops out instead of leaving a doubled separator
     *  ("SEALED •  • until …") on the home screen. */
    private fun sealedLine(intention: String, tail: String): String =
        listOfNotNull("SEALED", intention.takeIf { it.isNotBlank() }, tail)
            .joinToString(" \u2022 ")

    private fun hhmm(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))

    /** Issue #155 (IQ-2 VC-3): TODAY / TOMORROW / day name for a
     *  future start, so the widget's clock time never reads as
     *  sooner than it is. */
    private fun dayLabel(startMillis: Long, nowMillis: Long): String {
        val start = java.util.Calendar.getInstance().apply { timeInMillis = startMillis }
        val nowCal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        fun sameDay(a: java.util.Calendar, b: java.util.Calendar): Boolean =
            a.get(java.util.Calendar.YEAR) == b.get(java.util.Calendar.YEAR) &&
                a.get(java.util.Calendar.DAY_OF_YEAR) == b.get(java.util.Calendar.DAY_OF_YEAR)
        if (sameDay(start, nowCal)) return "TODAY"
        nowCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        if (sameDay(start, nowCal)) return "TOMORROW"
        return SimpleDateFormat("EEE", Locale.US).format(Date(startMillis)).uppercase(Locale.US)
    }

    companion object {
        /** Request code for the SEALED-expiry one-shot (IQ-4 edge 1). */
        private const val SEALED_EXPIRY_REQUEST_CODE = 0x155E

        /**
         * Refresh every placed widget NOW — the shield service calls
         * this on session start (first protecting iteration), session
         * completion, and shield stop, so the glance is fresh at the
         * moments that matter instead of waiting for the platform's
         * 30-minute update period.
         */
        fun requestUpdate(context: Context) {
            try {
                val manager = AppWidgetManager.getInstance(context)
                val component = ComponentName(context, MonolithWidgetProvider::class.java)
                val ids = manager.getAppWidgetIds(component)
                if (ids.isNotEmpty()) {
                    context.sendBroadcast(
                        Intent(context, MonolithWidgetProvider::class.java).apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        }
                    )
                }
            } catch (_: Exception) {
                // No widget placed (or host gone) — nothing to refresh.
            }
        }
    }
}
