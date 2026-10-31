package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.MonolithDatabase
import com.example.data.RoutineSchedule
import com.example.service.MonolithShieldService
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
                        id, buildViews(appContext, status)
                    )
                }
            } finally {
                pendingResult.finish()
            }
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
    private suspend fun readStatusLine(context: Context): String {
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
                // Issue #155 (IQ-1 unified latch, WING-F): the
                // accessibility input is the shield loop's OWN
                // published standing — measured at the flip site
                // every iteration (bound AND armed for this
                // session). The widget never reads the
                // accessibility service itself; that fork would
                // let a bound-but-unarmed door print SEALED.
                val sealedProven = WidgetSealTruth.sealedProven(
                    isActive = session.isActive,
                    protecting = MonolithShieldService.protecting,
                    protectingSessionId = MonolithShieldService.protectingSessionId,
                    sessionId = session.id,
                    beatWallMillis = session.shieldLastBeatWallMillis,
                    nowWallMillis = now,
                    accessibilityBound = MonolithShieldService.protectingAccessibilityStanding
                )
                if (!sealedProven) {
                    // Issue #155 (IQ-2 VC-2 + IQ-5 F-4): no
                    // row-asserted "SESSION LIVE", short enough to
                    // read whole at the smallest (3x1) placement.
                    "SHIELD UNPROVEN \u2022 OPEN THE APP TO CHECK"
                } else when {
                    session.isStopwatchMode ->
                        sealedLine(session.intention, "STOPWATCH, NO SET END")
                    session.windowEndMillis > now ->
                        sealedLine(session.intention, "until ${hhmm(session.windowEndMillis)}")
                    else -> {
                        // Manual timed session: the end is now plus the
                        // whole-span remainder the model itself derives.
                        val end = now + session.spanRemainingSeconds * 1000L
                        sealedLine(session.intention, "until ${hhmm(end)}")
                    }
                }
            } else {
                val next = dao.getAllRoutinesSnapshot()
                    .filter { RoutineSchedule.isLive(it) }
                    .mapNotNull { routine ->
                        RoutineSchedule.effectiveNextStartMillis(routine, now)
                            ?.let { start -> routine to start }
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
                    if (next.first.name.isBlank()) {
                        "OPEN \u2022 NEXT ROUTINE ${dayLabel(next.second, now)} ${hhmm(next.second)}"
                    } else {
                        "OPEN \u2022 NEXT ROUTINE ${next.first.name} ${dayLabel(next.second, now)} ${hhmm(next.second)}"
                    }
                } else {
                    "OPEN"
                }
            }
        } catch (_: Exception) {
            // Never a false claim: if the state cannot be read, say so.
            "OPEN THE APP TO CHECK"
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
