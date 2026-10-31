package com.example.service

import android.content.Context

/**
 * Issue #274 (Audit R5 — Platform execution): the session mirror.
 *
 * The shield loop is the only writer of live session truth, and it
 * lives in a foreground service. Platform surfaces that must answer
 * WITHOUT opening the app — the Quick Settings tile, the standing
 * notification's chronometer — cannot reach into that loop. This
 * mirror is the one small durable fact they read: is a session live,
 * and (for countdown sessions) the wall-clock instant it ends.
 *
 * Written by MonolithShieldService on every loop iteration that
 * formats the countdown line, cleared when the service is destroyed.
 * It carries NO intention text and NO urge counts — the tile and the
 * lock screen are glance surfaces, and #274's redaction fix
 * (VISIBILITY_PRIVATE) set the rule: private text stays in the app.
 */
object SessionMirror {
    private const val PREFS = "session_mirror"
    private const val KEY_ACTIVE = "active"
    private const val KEY_END_WALL_MS = "end_wall_ms"

    data class Snapshot(val active: Boolean, val endWallMs: Long)

    fun publish(context: Context, endWallMs: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ACTIVE, true)
            .putLong(KEY_END_WALL_MS, endWallMs)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ACTIVE, false)
            .putLong(KEY_END_WALL_MS, 0L)
            .apply()
    }

    fun read(context: Context): Snapshot {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return Snapshot(
            active = prefs.getBoolean(KEY_ACTIVE, false),
            endWallMs = prefs.getLong(KEY_END_WALL_MS, 0L)
        )
    }
}

/** Pure remaining-time label for the tile subtitle — unit-tested. */
object SessionMirrorFormat {
    /** "42:17 left" for a live countdown; null when there is nothing
     *  truthful to say (no session, stopwatch, or an expired mirror). */
    fun remainingLabel(endWallMs: Long, nowMs: Long): String? {
        if (endWallMs <= 0L) return null
        val remainingMs = endWallMs - nowMs
        if (remainingMs <= 0L) return null
        val totalSec = (remainingMs + 999L) / 1000L
        val mins = totalSec / 60L
        val secs = totalSec % 60L
        return String.format("%d:%02d left", mins, secs)
    }
}
