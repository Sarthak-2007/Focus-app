package com.example.data

/**
 * Issue #290 (Audit R5 supplement — Forest, second pass, F4): the
 * shield door's foreground truth, derived from the real UsageEvents
 * event stream instead of a lastTimeUsed recency guess.
 *
 * Forest layers accessibility + a UsageEvents stream + task-stack;
 * ScreenZen's detector likewise aggregates UsageEvents. Monolith's
 * shield door previously ran queryUsageStats(INTERVAL_DAILY) and
 * named the blocked package with the newest lastTimeUsed inside an
 * 8-second window "foreground" — a guess that can certify an app
 * that was merely used recently and can miss a fast app-switch
 * entirely.
 *
 * This object is the pure core of the event-stream path: it folds
 * an ordered ACTIVITY_RESUMED / ACTIVITY_PAUSED / ACTIVITY_STOPPED
 * sequence (the event-type ints are android.app.usage.UsageEvents
 * .Event's own constants, mirrored here so the fold is unit-testable
 * on the JVM without an Android runtime) and answers which candidate
 * package is foreground at the end of the stream. Events for
 * packages outside [candidates] (system UI, notification shade,
 * launchers) never move the answer — the same detection-hygiene
 * property Forest's notification listener exists to protect.
 *
 * The repository's lastTimeUsed path survives as the fallback for
 * devices whose event stream comes back empty.
 */
object ForegroundEventStream {

    // Mirrors of UsageEvents.Event constants (stable platform ints).
    const val ACTIVITY_RESUMED = 1
    const val ACTIVITY_PAUSED = 2
    const val ACTIVITY_STOPPED = 23

    data class Event(
        val packageName: String,
        val type: Int,
        val timestampMillis: Long
    )

    /**
     * The candidate package foreground at the end of [events], or
     * null when no candidate is resumed at the end (user on the
     * launcher, screen off, every candidate paused/stopped). The
     * fold is chronological regardless of input order; a PAUSED or
     * STOPPED only clears the package it names, so the platform's
     * transition order (old app pauses, new app resumes, old app
     * stops) never evicts the app that just took the foreground.
     */
    fun foregroundPackage(events: List<Event>, candidates: Set<String>): String? {
        if (candidates.isEmpty()) return null
        var current: String? = null
        for (event in events.sortedBy { it.timestampMillis }) {
            if (event.packageName !in candidates) continue
            when (event.type) {
                ACTIVITY_RESUMED -> current = event.packageName
                ACTIVITY_PAUSED, ACTIVITY_STOPPED ->
                    if (current == event.packageName) current = null
            }
        }
        return current
    }
}
