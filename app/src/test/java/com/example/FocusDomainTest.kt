package com.example

import com.example.domain.FocusDomain
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * ISSUE-67: the extracted domain slice, tested pure — no Robolectric,
 * no Android, no DAO. These pin the exact shipped behaviour the
 * repository's inline versions had, so the delegation is a move,
 * not a change.
 */
class FocusDomainTest {

    @Test
    fun peekCooldown_noAnchorIsZero() {
        assertEquals(0, FocusDomain.peekCooldownRemainingSeconds(0L, 0L, 50_000L, 1_200_000L))
    }

    @Test
    fun peekCooldown_countsDownFromLaterAnchorAndCapsAtFullCooldown() {
        // Anchor at 100s (the later of the two), now at 100s: the
        // full 20-minute cooldown remains — and never more, even
        // when the clock reads before the anchor.
        assertEquals(1200, FocusDomain.peekCooldownRemainingSeconds(90_000L, 100_000L, 100_000L, 1_200_000L))
        assertEquals(1200, FocusDomain.peekCooldownRemainingSeconds(90_000L, 100_000L, 0L, 1_200_000L))
        // Half-way through: 600 s remain.
        assertEquals(600, FocusDomain.peekCooldownRemainingSeconds(0L, 100_000L, 700_000L, 1_200_000L))
        // Fully run: zero, never negative.
        assertEquals(0, FocusDomain.peekCooldownRemainingSeconds(0L, 100_000L, 1_300_000L, 1_200_000L))
        assertEquals(0, FocusDomain.peekCooldownRemainingSeconds(0L, 100_000L, 9_999_999L, 1_200_000L))
    }

    @Test
    fun countHeldApps_countsEverythingNotOpen() {
        assertEquals(2, FocusDomain.countHeldApps(listOf("LOCKED", "PEEK_2M", "OPEN")))
        assertEquals(0, FocusDomain.countHeldApps(listOf("OPEN", "OPEN")))
        assertEquals(0, FocusDomain.countHeldApps(emptyList()))
    }

    @Test
    fun blocks25_wholeBlocksWithFifteenMinuteFloor() {
        assertEquals(0, FocusDomain.blocks25For(0))
        assertEquals(0, FocusDomain.blocks25For(14))
        assertEquals(1, FocusDomain.blocks25For(15))
        assertEquals(1, FocusDomain.blocks25For(24))
        assertEquals(1, FocusDomain.blocks25For(25))
        assertEquals(2, FocusDomain.blocks25For(50))
        assertEquals(4, FocusDomain.blocks25For(119))
    }

    @Test
    fun completedTaskSummary_estimateSegmentOnlyWhenEstimated() {
        assertEquals(
            "Read ch.4 (50m est / 47m actual)",
            FocusDomain.completedTaskSummaryEntry("Read ch.4", 50, 47 * 60)
        )
        assertEquals(
            "Read ch.4 (47m actual)",
            FocusDomain.completedTaskSummaryEntry("Read ch.4", 0, 47 * 60)
        )
        // Issue #110 semantics (adopted on rebase): a sub-minute
        // accrual says "<1m", zero accrual says "no session time"
        // — never a whole minute that did not happen.
        assertEquals(
            "Quick (<1m actual)",
            FocusDomain.completedTaskSummaryEntry("Quick", 0, 20)
        )
        assertEquals(
            "Quick (no session time)",
            FocusDomain.completedTaskSummaryEntry("Quick", 0, 0)
        )
    }
}
