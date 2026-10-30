package com.example.data

import kotlin.math.abs

/**
 * Clock honesty (Audit R2 · clock-attacks, issue #82).
 *
 * Every verdict that reads the raw wall clock is forgeable: the user
 * owns that clock. This object is the dual-clock discipline the rest
 * of the machinery already uses for session elapsed time, generalised
 * to "what time is it, honestly":
 *
 * - A persisted anchor pairs a wall instant with the monotonic
 *   `SystemClock.elapsedRealtime()` reading and the boot count at
 *   which the pair was taken.
 * - While both clocks agree (drift within [TRAVEL_TOLERANCE_MILLIS]),
 *   the wall clock is believed and the anchor is refreshed.
 * - When they disagree beyond tolerance, the wall clock has travelled
 *   (or is being travelled): the honest now is the anchor's wall
 *   instant advanced by monotonic time only. A jump into next week
 *   does not move the honest now; a rollback does not rewind it.
 *
 * Known limit, stated plainly: the anchor re-bases on the first
 * read of each boot (elapsedRealtime resets on reboot and nothing
 * monotonic survives one) — but the honest now never rewinds below
 * the last anchored wall instant, so a rollback performed while the
 * device is off, before the new boot's first tick, is floored at
 * the anchor and cannot rewind the emergency-token week. Forward
 * travel performed while the device is off remains the stated
 * fundamental limit: it is indistinguishable from real off-time,
 * so it is anchored as truth. Session-level verdicts are
 * separately protected by the
 * session rows' own monotonic anchors, which re-anchor the same way
 * computeVerifiedSessionElapsed already accepts.
 */
object ClockHonesty {

    /** Wall/monotonic disagreement tolerated as ordinary clock noise
     *  (NTP corrections, manual minute-level fixes). Attack jumps in
     *  the audit are hours to weeks — far outside this band. */
    const val TRAVEL_TOLERANCE_MILLIS = 120_000L

    /** [honestNowMillis] is the only "now" a verdict may use;
     *  [reanchor] tells the caller to persist (wall, mono, boot) as
     *  the new anchor pair. */
    data class Decision(val honestNowMillis: Long, val reanchor: Boolean)

    fun decide(
        anchorWallMillis: Long,
        anchorElapsedRealtimeMillis: Long,
        anchorBootCount: Int,
        nowWallMillis: Long,
        nowElapsedRealtimeMillis: Long,
        nowBootCount: Int
    ): Decision {
        if (anchorWallMillis <= 0L || anchorElapsedRealtimeMillis <= 0L ||
            anchorBootCount != nowBootCount ||
            nowElapsedRealtimeMillis < anchorElapsedRealtimeMillis
        ) {
            // No usable anchor this boot: anchor to the wall clock,
            // but never below the last anchored wall instant — a
            // rollback performed while powered off (before this
            // boot's first read) must not rewind the honest now.
            // With no anchor ever (anchorWallMillis <= 0) the max is
            // the wall clock itself, unchanged; forward travel while
            // off stays the documented limit above.
            return Decision(maxOf(nowWallMillis, anchorWallMillis), reanchor = true)
        }
        val implied = anchorWallMillis + (nowElapsedRealtimeMillis - anchorElapsedRealtimeMillis)
        val drift = nowWallMillis - implied
        return if (abs(drift) <= TRAVEL_TOLERANCE_MILLIS) {
            // The clocks agree: believe the wall clock, refresh anchor.
            Decision(nowWallMillis, reanchor = true)
        } else {
            // The wall clock travelled: monotonic time is the truth.
            Decision(implied, reanchor = false)
        }
    }

    /**
     * The wall-clock instant a session's monotonic anchors honestly
     * imply as "now" — the shield-gap verdict's clock (issue #82
     * finding 1). Mirrors computeVerifiedSessionElapsed's two shapes:
     * same-boot (accumulated + monotonic since the session anchor)
     * and post-reboot (prior elapsed + monotonic since boot).
     * A wall-clock jump moves neither term; a rollback cannot make
     * a stale beat look fresh.
     */
    fun impliedNowMillis(
        startWallMillis: Long,
        accumulatedBeforeRebootSeconds: Int,
        startElapsedRealtimeMillis: Long,
        lastTickElapsedRealtimeMillis: Long,
        elapsedSeconds: Int,
        nowElapsedRealtimeMillis: Long
    ): Long {
        val honestElapsedMillis = if (
            startElapsedRealtimeMillis > 0L &&
            nowElapsedRealtimeMillis >= startElapsedRealtimeMillis &&
            (lastTickElapsedRealtimeMillis <= 0L ||
                lastTickElapsedRealtimeMillis <= nowElapsedRealtimeMillis)
        ) {
            accumulatedBeforeRebootSeconds * 1000L +
                (nowElapsedRealtimeMillis - startElapsedRealtimeMillis)
        } else {
            maxOf(accumulatedBeforeRebootSeconds, elapsedSeconds) * 1000L +
                nowElapsedRealtimeMillis
        }
        return startWallMillis + honestElapsedMillis
    }
}
