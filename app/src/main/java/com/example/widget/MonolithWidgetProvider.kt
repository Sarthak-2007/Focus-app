package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.AppContainer
import com.example.R
import com.example.data.RoutineSchedule
import com.example.service.InterceptIntents
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
        // Via the #67 InterceptIntents seam (class-name addressed,
        // SINGLE_TOP) — the widget keeps no compile-time reference
        // to the UI root either.
        val launch = InterceptIntents.notificationIntent(context)
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
            val dao = AppContainer.dao(context)
            val now = System.currentTimeMillis()
            val session = dao.getActiveSessionSnapshot()?.takeIf { it.isActive }
            if (session != null) {
                when {
                    session.isStopwatchMode ->
                        "SEALED \u2022 ${session.intention} \u2022 STOPWATCH, NO SET END"
                    session.windowEndMillis > now ->
                        "SEALED \u2022 ${session.intention} \u2022 until ${hhmm(session.windowEndMillis)}"
                    else -> {
                        // Manual timed session: the end is now plus the
                        // whole-span remainder the model itself derives.
                        val end = now + session.spanRemainingSeconds * 1000L
                        "SEALED \u2022 ${session.intention} \u2022 until ${hhmm(end)}"
                    }
                }
            } else {
                val next = dao.getAllRoutinesSnapshot()
                    .filter { RoutineSchedule.isLive(it) }
                    .mapNotNull { routine ->
                        // Pause-aware next start (the helper the
                        // branch era read was folded away on main):
                        // origin is max(now, pausedUntilMillis), so a
                        // paused routine's next seal is computed from
                        // the pause's end — the scheduler's own rule.
                        RoutineSchedule.nextStartMillis(
                            routine, maxOf(now, routine.pausedUntilMillis)
                        )
                            ?.let { start -> routine to start }
                    }
                    .minByOrNull { it.second }
                if (next != null) {
                    "OPEN \u2022 NEXT SEAL ${next.first.name} ${hhmm(next.second)}"
                } else {
                    "OPEN"
                }
            }
        } catch (_: Exception) {
            // Never a false claim: if the state cannot be read, say so.
            "OPEN THE APP TO CHECK"
        }
    }

    private fun hhmm(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))

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
