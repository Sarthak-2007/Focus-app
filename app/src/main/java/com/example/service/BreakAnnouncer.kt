package com.example.service

/**
 * The Safe Break's voice (Audit R3, safe-break-lived #3 + #4).
 *
 * As lived, the break was announced at neither end: entry fired
 * only the repository's mechanical-latch knock (a sound-gated
 * click a user in another app or with the screen off never
 * registers), and expiry was a hard cut — the PEEK app re-entered
 * the blocked set inside one ~2 s shield poll and the intercept
 * landed mid-use, the Deck's own clock rounding the whole final
 * minute up to "1 MIN".
 *
 * This is the pure transition machine the shield loop feeds on
 * every iteration (the shield is the one process guaranteed alive
 * for the whole session, in and out of every app): it turns the
 * session row's break state into at most one alert per boundary —
 *
 *  - [STARTED]      the first tick that sees the break in force;
 *  - [FINAL_MINUTE] the first tick inside the last 60 seconds;
 *  - [ENDED]        the first tick that sees the break gone after
 *                   having seen it (expiry AND end-early alike —
 *                   both exits cross the same row flip).
 *
 * A service (re)start mid-break reports STARTED with the true
 * remaining time — late truth, never silence. A new session id
 * resets the machine, so one session's break can never leak its
 * alerts into the next. Announcements are information only: no
 * alert here opens any door (HOLD-absolutely).
 */
enum class BreakAlert { STARTED, FINAL_MINUTE, ENDED }

class BreakAnnouncer {

    private var sessionId: Int = -1
    private var inBreak: Boolean = false
    private var finalMinuteWarned: Boolean = false

    fun onTick(
        sessionId: Int,
        isInSafeBreak: Boolean,
        safeBreakSecondsRemaining: Int
    ): BreakAlert? {
        if (sessionId != this.sessionId) {
            this.sessionId = sessionId
            inBreak = false
            finalMinuteWarned = false
        }
        if (isInSafeBreak) {
            if (!inBreak) {
                inBreak = true
                // Born inside the final minute: the STARTED alert
                // already carries the true (short) clock, so the
                // final-minute warning would be a duplicate.
                finalMinuteWarned = safeBreakSecondsRemaining <= FINAL_MINUTE_SECONDS
                return BreakAlert.STARTED
            }
            if (!finalMinuteWarned &&
                safeBreakSecondsRemaining in 1..FINAL_MINUTE_SECONDS
            ) {
                finalMinuteWarned = true
                return BreakAlert.FINAL_MINUTE
            }
            return null
        }
        if (inBreak) {
            inBreak = false
            finalMinuteWarned = false
            return BreakAlert.ENDED
        }
        return null
    }

    companion object {
        const val FINAL_MINUTE_SECONDS = 60
    }
}
