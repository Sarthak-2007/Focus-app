package com.example.service

/**
 * Foreground selection for the shield monitor (issue #43, finding 2).
 *
 * Pure — no Android types — so the preference law is unit-testable:
 *
 *  - When an accessibility foreground witness exists (a package
 *    stamped from a real TYPE_WINDOW_STATE_CHANGED event), that
 *    package IS the foreground. It is returned only when it is in
 *    [blockedPackages]; otherwise the answer is null. The
 *    UsageStats guess is NOT consulted in this case — falling back
 *    to a lastTimeUsed guess when the real foreground is known
 *    would re-introduce exactly the false intercepts the witness
 *    exists to remove (an app the user already left, picked by
 *    recency).
 *  - Only when NO witness exists is [usageStatsFallback] invoked,
 *    and its answer is still filtered through [blockedPackages].
 */
object ForegroundSelection {

    fun selectBlockedForeground(
        accessibilityForeground: String?,
        blockedPackages: Set<String>,
        usageStatsFallback: () -> String?
    ): String? {
        if (accessibilityForeground != null) {
            return accessibilityForeground.takeIf { it in blockedPackages }
        }
        return usageStatsFallback()?.takeIf { it in blockedPackages }
    }
}

/**
 * Restart policy for the shield service (issue #43, finding 4).
 * Pure decision + delay, so the law is unit-testable: a restart
 * alarm is scheduled on task removal ONLY when the shield was
 * protecting — a stopped shield must never resurrect itself.
 * Force-stop is out of scope by platform design (no code runs
 * after a force-stop); this covers swipe-away / task removal.
 */
object ShieldRestartPolicy {

    /** Near-immediate restart attempt after task removal. */
    const val RESTART_DELAY_MS: Long = 1_000L

    fun shouldScheduleRestart(wasProtecting: Boolean): Boolean = wasProtecting
}
