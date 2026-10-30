package com.example.data

/**
 * Issue #265 (Audit R5 — Behaviour science, lines 19/20/22/36;
 * STANDS-BLOCKED specs B-265-19 + B-265-20, priced in CELL-E §6):
 * the Airlock wall never escalated and never showed an attempt
 * count — the 10th reach in a craving cascade met the identical
 * fixed wall as the 1st, and the self-monitoring half of the
 * intervention (seeing your own attempt number at the moment of
 * decision) was missing even though every intercept is logged.
 *
 * The escalation is a pure function of the CONFIGURED base
 * ([AirlockBreathStore], Decision #16's wall as the user set it)
 * and the per-session reach count: reach 1 breathes the base,
 * reach 2 doubles it, reach 3 and beyond quadruple it — with the
 * default base of 5s that is the audited 5s → 10s → 20s ladder.
 * The result is clamped to [MAX_EFFECTIVE_SECONDS] so a large
 * configured base can never grow an absurd wall, and never below
 * the store's own minimum.
 *
 * The count is per SESSION: a new session is a new cascade and
 * starts again at the base. Nothing here is a streak, a score or
 * a total — the Drawer law ("no totals, no streaks, no
 * percentages, no counts" for the archive) governs stored
 * history; this is a live fact about the current session printed
 * on the door at the moment of temptation, the same class as the
 * stake line (Issue #325) and the session end the door states.
 */
object AirlockEscalation {

    /** Hard ceiling for an escalated wall, whatever the base. */
    const val MAX_EFFECTIVE_SECONDS = 60

    /** Pure, unit-tested: the ladder — 1st reach ×1, 2nd ×2, 3rd+ ×4. */
    fun multiplierForReach(reachCount: Int): Int = when {
        reachCount <= 1 -> 1
        reachCount == 2 -> 2
        else -> 4
    }

    /**
     * Pure, unit-tested: the wall length for this reach. The base
     * is validated through the store's own clamp first, so a stray
     * caller value can never undercut Decision #16's floor.
     */
    fun effectiveBreathSeconds(baseSeconds: Int, reachCount: Int): Int {
        val base = AirlockBreathStore.clampSeconds(baseSeconds)
        return (base * multiplierForReach(reachCount))
            .coerceIn(AirlockBreathStore.MIN_SECONDS, MAX_EFFECTIVE_SECONDS)
    }
}
