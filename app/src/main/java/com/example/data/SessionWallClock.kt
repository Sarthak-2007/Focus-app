package com.example.data

/**
 * Issue #122 (Audit R3b, finding 4): battery death was not an
 * ending. computeVerifiedSessionElapsed's reboot branch counts
 * accumulated seconds plus time since this boot only, discarding
 * the wall-clock hours the phone sat dead — so a session whose
 * planned end passed while the phone was dead resurrected on
 * recharge and re-locked the user for time that was long over.
 *
 * This object derives the one wall-clock fact that ends it: the
 * session's planned end, from the row's own anchors.
 *
 * The anti-tamper law is untouched: WITHIN a boot the monotonic
 * clock stays authoritative and a moved wall clock is tamper.
 * The wall deadline is consulted ONLY on the reboot branch
 * (boot count changed / monotonic reset) — the battery-death
 * signature — where monotonic time provably cannot account for
 * the downtime. Routine sessions are excluded: the heartbeat's
 * window finalise owns their endings. Stopwatch (FLOW) sessions
 * have no planned end and never expire here.
 *
 * Anchor note (verified against the progress machine): the
 * row's startWallTimeMillis is re-anchored at every block
 * boundary (advancePastBreak), so it is always the CURRENT
 * block's start wall — the anchor below therefore prices only
 * the current block (plus the current break's run time), and
 * [FocusSessionEntity.spanRemainingSeconds] prices the rest.
 * The caller (updateSessionProgress's reboot branch) books the
 * ending as ELAPSED_SHIELD_GAP — the span ran out while the
 * shield was gone — never as a clean success, and stamps the
 * planned end, not the recharge/discovery instant, as the end.
 */
object SessionWallClock {

    /**
     * The last wall instant the session is known to have been
     * alive: the current block's start wall plus the span time
     * provably elapsed at the last tick — the current block's
     * elapsed seconds, or (mid-break) the finished block plus
     * the break time actually run.
     */
    fun anchorWallMillis(session: FocusSessionEntity): Long {
        val inBlockSeconds = if (session.isInSafeBreak) {
            val breakElapsed = (session.effectiveBreakSeconds -
                session.safeBreakSecondsRemaining)
                .coerceIn(0, session.effectiveBreakSeconds)
            session.effectiveTargetSeconds + breakElapsed
        } else {
            session.elapsedSeconds
        }
        return session.startWallTimeMillis + inBlockSeconds * 1000L
    }

    /** The wall instant this session was always going to end:
     *  its last-known-alive anchor plus the span remaining at
     *  that anchor. Null for stopwatch sessions (no set end). */
    fun plannedEndWallMillis(session: FocusSessionEntity): Long? {
        if (session.isStopwatchMode) return null
        return anchorWallMillis(session) + session.spanRemainingSeconds * 1000L
    }

    /** True when [nowWallMillis] is at or past the planned end.
     *  Callers gate this on a detected reboot — see the file doc. */
    fun wallDeadlinePassed(session: FocusSessionEntity, nowWallMillis: Long): Boolean {
        val end = plannedEndWallMillis(session) ?: return false
        return nowWallMillis >= end
    }
}
