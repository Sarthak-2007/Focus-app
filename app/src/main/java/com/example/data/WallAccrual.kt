package com.example.data

/**
 * Wall-clock accrual for the active task's stored seconds.
 *
 * The shipped increment added +1 per CALL of the session-progress
 * updater — and two tickers call it (the ViewModel's 1s loop and
 * the Shield service's monitor), so a task was credited per call,
 * not per second: ≈90 credited per 60 wall seconds with both
 * tickers alive (Hale, LR2-S2-24). A stored "actual" that is a call
 * count is a lie in the permanent record.
 *
 * This accrual observes monotonic deltas instead. Call [observe]
 * on every progress tick with the task that should accrue (null
 * when none should — e.g. during a Safe Break, when the anchor
 * advances without crediting). Any number of callers, at any
 * cadence, sum to the wall time that actually passed: the second
 * ticker's call simply finds the delta already consumed. Task or
 * session switches re-anchor and credit nothing for the switch
 * instant itself.
 */
/**
 * The Safe Break countdown, wall-derived (Bus's catch, filed
 * 09:30 IST Oct 29: the shipped code decremented the break's
 * remaining seconds by 1 PER CALL of the progress updater — the
 * same tick-count disease as task accrual, draining a 600-second
 * break in ~400 wall seconds under both tickers).
 *
 * The derivation needs no new state: break entry stamps the
 * session's lastTickElapsedRealtimeMillis with the entry instant,
 * and every break write since keeps the (remaining, lastTick)
 * pair persisted. Remaining now = stored remaining minus the
 * whole wall seconds since the last stored tick. Two tickers
 * reading the same stored pair compute the same answer instead
 * of double-draining; a process-death gap counts, because the
 * break really did elapse (the session's own elapsed clock is
 * boot-monotonic wall time too). A reboot tick (nowMono behind
 * the stored tick) credits nothing and re-anchors.
 */
internal fun breakRemainingAfter(
    storedRemaining: Int,
    lastTickMonoMillis: Long,
    nowMonoMillis: Long
): Int {
    // No "unstamped" sentinel: break entry always stamps the tick,
    // and 0 is a legitimate anchor value (first millisecond after
    // boot) — a guard here froze the countdown's first second when
    // this was unit-tested from a zero anchor.
    val elapsedSeconds = ((nowMonoMillis - lastTickMonoMillis) / 1000L).coerceAtLeast(0L)
    return (storedRemaining - elapsedSeconds).coerceAtLeast(0L).toInt()
}

/**
 * The call-exclusion ledger, wall-derived (W3 #134,
 * phone-call-mid-session) — the mirror image of
 * [breakRemainingAfter]'s countdown. While the call bypass
 * signal (AudioManager in a call mode) is active, the progress
 * machine adds the whole wall seconds since the session's last
 * stored tick to the block's persisted callExcludedSeconds, and
 * books elapsed as verified-minus-excluded: call time never
 * accrues as focus time. The derivation needs no new state —
 * every progress write already persists the (excluded, lastTick)
 * pair, so two tickers reading the same stored pair agree, a
 * process-death gap counts (the shield-gap latch judges that
 * absence separately), and a reboot tick (nowMono behind the
 * stored tick) excludes nothing and re-anchors. The first
 * call-active tick can over-exclude by at most one tick cadence
 * (the interval since the previous tick may partly predate the
 * ring) — bounded, and it errs toward LESS claimed focus, never
 * more.
 */
internal fun callExcludedAfter(
    storedExcludedSeconds: Int,
    lastTickMonoMillis: Long,
    nowMonoMillis: Long
): Int {
    val deltaSeconds = ((nowMonoMillis - lastTickMonoMillis) / 1000L).coerceAtLeast(0L)
    return storedExcludedSeconds + deltaSeconds.toInt()
}

class WallAccrual {

    private var sessionId: Int = -1
    private var taskId: Int = -1
    private var lastMonoMillis: Long = 0L
    private var anchored: Boolean = false

    /**
     * Returns the whole seconds to credit to [accruingTaskId] for
     * the interval since the previous observation.
     */
    @Synchronized
    fun observe(sessionId: Int, accruingTaskId: Int?, nowMonoMillis: Long): Int {
        if (accruingTaskId == null) {
            // Not accruing (Safe Break, no task): advance the anchor
            // so the idle interval is never credited later.
            this.sessionId = sessionId
            this.taskId = -1
            lastMonoMillis = nowMonoMillis
            anchored = true
            return 0
        }
        if (!anchored || sessionId != this.sessionId || accruingTaskId != this.taskId) {
            // Anchored-ness is an explicit flag, never a sentinel
            // value: a monotonic clock legitimately reads 0 at
            // boot, and a sentinel check re-anchors (and silently
            // drops) the first real interval.
            this.sessionId = sessionId
            this.taskId = accruingTaskId
            lastMonoMillis = nowMonoMillis
            anchored = true
            return 0
        }
        val deltaMs = nowMonoMillis - lastMonoMillis
        if (deltaMs <= 0L) return 0
        val seconds = (deltaMs / 1000L).toInt()
        if (seconds > 0) {
            // Keep the sub-second remainder in the anchor so slow
            // cadences do not leak fractions of every interval.
            lastMonoMillis += seconds * 1000L
        }
        return seconds
    }
}
