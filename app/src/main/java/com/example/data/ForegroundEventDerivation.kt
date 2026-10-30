package com.example.data

/**
 * Foreground derivation from the UsageEvents stream (issue #295,
 * finding F2).
 *
 * Pure — no Android types — so the derivation law is unit-testable
 * over synthetic event sequences. The repository maps the
 * platform's UsageEvents (ACTIVITY_RESUMED / ACTIVITY_PAUSED /
 * ACTIVITY_STOPPED, and their pre-API-29 MOVE_TO_FOREGROUND /
 * MOVE_TO_BACKGROUND names) onto this vocabulary; everything after
 * that mapping is decided here.
 *
 * The law:
 *
 *  - Replaying the transitions in time order, the foreground is
 *    the package of the latest RESUMED that no PAUSED or STOPPED
 *    for the same package has since closed. A PAUSED / STOPPED for
 *    any other package never clears it (the system pauses the old
 *    activity AFTER resuming the new one).
 *  - When the stream names a foreground, that answer IS the
 *    foreground — blocked or not. The lastTimeUsed recency guess
 *    is NOT consulted: a guess must never override a known
 *    foreground, or the shield intercepts an app the user has
 *    already left (the exact false intercept F2 was filed for).
 *  - Only when the stream derives NO foreground (an empty window,
 *    or every resumed package since paused away) is the
 *    lastTimeUsed fallback invoked, and its answer is still
 *    filtered through the blocked set.
 */
enum class ForegroundTransition { RESUMED, PAUSED, STOPPED }

/** One mapped UsageEvents transition, in wall-clock time. */
data class ForegroundEventSample(
    val packageName: String,
    val transition: ForegroundTransition,
    val timeMillis: Long
)

object ForegroundEventDerivation {

    /**
     * How far back the repository queries the event stream. The
     * window must comfortably contain the current foreground's
     * RESUMED even for a stay-put user, or the derivation goes
     * silent and the recency guess takes over again; five minutes
     * of transitions is a few hundred small records, cheap for the
     * shield's 2-second poll to replay.
     */
    const val EVENT_WINDOW_MS: Long = 300_000L

    /** The event-derived foreground package, or null when the
     *  stream names none. */
    fun deriveForegroundPackage(events: List<ForegroundEventSample>): String? {
        var current: String? = null
        for (event in events.sortedBy { it.timeMillis }) {
            when (event.transition) {
                ForegroundTransition.RESUMED -> current = event.packageName
                ForegroundTransition.PAUSED,
                ForegroundTransition.STOPPED ->
                    if (current == event.packageName) current = null
            }
        }
        return current
    }

    /**
     * The blocked foreground for one shield poll: the derived
     * foreground when the stream names one (returned only if it is
     * blocked), otherwise [lastTimeUsedFallback]'s answer, itself
     * filtered through [blockedPackages].
     */
    fun selectBlockedForeground(
        events: List<ForegroundEventSample>,
        blockedPackages: Set<String>,
        lastTimeUsedFallback: () -> String?
    ): String? {
        val derived = deriveForegroundPackage(events)
        if (derived != null) {
            return derived.takeIf { it in blockedPackages }
        }
        return lastTimeUsedFallback()?.takeIf { it in blockedPackages }
    }
}
