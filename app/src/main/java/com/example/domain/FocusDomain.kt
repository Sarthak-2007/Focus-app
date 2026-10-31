package com.example.domain

/**
 * ISSUE-67 (Audit R1 · architecture) — THE DOMAIN SLICE, FIRST CUT.
 *
 * A pure-Kotlin domain layer: no Android imports, no data-layer
 * imports, no service-layer imports. Every function here takes
 * plain values and returns plain values, so the derivations can be
 * unit-tested on a bare JVM and reused from any layer without
 * dragging a Context, a DAO, or a living service behind them.
 *
 * This file is the proof the layer can exist (the audit's finding 4
 * recorded that it did not): [com.example.data.MonolithRepository]
 * delegates to these functions for the derivations that were always
 * pure but lived trapped inside the 2,932-line god class. The
 * repository keeps the Android work (clock reads, DAO reads); the
 * arithmetic lives here, once.
 *
 * Tier identities cross this boundary as NAMES (the enum entry
 * names of the data layer's AppBlockTier), so this file never
 * imports the data layer.
 */
object FocusDomain {

    // NOTE: package categorisation was extracted here in this
    // branch's first cut, but main's issue-#132 extraction
    // (AppCategorizer, a superset with the MUSIC token category)
    // landed first — one classifier, never two; it is not
    // duplicated here.

    // ------------------------------------------------------------------
    // Peek lease arithmetic (was MonolithRepository's cooldown body)
    // ------------------------------------------------------------------

    /**
     * Seconds of peek cooldown remaining. The anchor is the LATER of
     * the lease's stored end and the last peek's end; the remaining
     * span is capped at the full cooldown so a future anchor can
     * never print more than the cooldown itself. 0 when no peek has
     * ever anchored, or the cooldown has fully run.
     */
    fun peekCooldownRemainingSeconds(
        lastPeekEndMono: Long,
        activePeekUntilMono: Long,
        nowMono: Long,
        cooldownMillis: Long
    ): Int {
        val anchorEndMono = maxOf(lastPeekEndMono, activePeekUntilMono)
        if (anchorEndMono <= 0L) return 0
        val remainingMs = (anchorEndMono + cooldownMillis - nowMono).coerceAtMost(cooldownMillis)
        return if (remainingMs > 0L) (remainingMs / 1000L).toInt() else 0
    }

    // ------------------------------------------------------------------
    // Held-apps count (was MonolithRepository.countHeldApps)
    // ------------------------------------------------------------------

    /**
     * How many apps the shield actually holds: every row whose tier
     * name is not the OPEN tier's name.
     */
    fun countHeldApps(tierNames: List<String>): Int =
        tierNames.count { it != "OPEN" }

    // ------------------------------------------------------------------
    // History derivation (was inline in compute7DayHistory)
    // ------------------------------------------------------------------

    /**
     * The 25-minute-block count for a day's total: whole 25s, with
     * the shipped floor — a day of 15+ minutes counts at least one
     * block, a shorter day counts none beyond its whole blocks.
     */
    fun blocks25For(totalMinutes: Int): Int =
        (totalMinutes / 25).coerceAtLeast(if (totalMinutes >= 15) 1 else 0)

    // ------------------------------------------------------------------
    // Completed-task summary line (was completedTaskSummaryEntry)
    // ------------------------------------------------------------------

    /**
     * The one completed-task summary format, shared by the booking
     * site and the reopen repair (which strips by exact string
     * match — one formatter, so the pair can never drift).
     *
     * Issue #110's truthful actual segment (landed on main while
     * this slice was in flight; adopted verbatim — the old
     * coerceAtLeast(1) inflated sub-minute completions to a minute
     * that never happened): zero accrued seconds says "no session
     * time", a real sub-minute accrual says "<1m actual", whole
     * minutes say so. Estimate segment only when estimated.
     */
    fun completedTaskSummaryEntry(
        title: String,
        estimatedMinutes: Int,
        actualSecondsSpent: Int
    ): String {
        val actualSegment = when {
            actualSecondsSpent <= 0 -> "no session time"
            actualSecondsSpent < 60 -> "<1m actual"
            else -> "${actualSecondsSpent / 60}m actual"
        }
        return if (estimatedMinutes > 0) {
            "$title (${estimatedMinutes}m est / $actualSegment)"
        } else {
            "$title ($actualSegment)"
        }
    }
}
