package com.example

import com.example.data.TokenWeekGuard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * PIN scope: SOURCE.
 *
 * Issue #523, finding 6-2 — the token week guard. Pure state
 * machine: a week that SKIPS past the last corroborated instant
 * while a spend stands against that instant's week is refused
 * (evaluation floored at the marker) until the claimed span has
 * been honestly lived in the same boot, or the clock heals.
 */
class TokenWeekGuardTest {

    private val ist: ZoneId = ZoneId.of("Asia/Kolkata")

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0): Long =
        ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ist).toInstant().toEpochMilli()

    // Wednesday of week 1 — the corroborated instant (a spend's, or
    // the marker's resting place) and Wednesday of week 2, the
    // skipped-to claim, exactly seven days later.
    private val marker = at(2026, 10, 28, 12)
    private val claim = at(2026, 11, 4, 12)
    private val weekMillis = 7 * 24 * 3_600_000L

    private fun evaluate(
        state: TokenWeekGuard.State,
        now: Long,
        boot: Int = 7,
        mono: Long = 1_000_000L,
        markerWeekHasSpend: Boolean = true,
        latestSpend: Long = 0L
    ): TokenWeekGuard.Result = TokenWeekGuard.evaluate(
        state = state,
        nowMillis = now,
        nowBootCount = boot,
        nowElapsedRealtimeMillis = mono,
        markerWeekHasSpend = markerWeekHasSpend,
        latestSpendWallMillis = latestSpend,
        zone = ist
    )

    @Test
    fun unseededMarkerSeedsAtLatestSpend_andThatWeeksSkipIsRefused() {
        val result = evaluate(
            state = TokenWeekGuard.State(),
            now = claim,
            latestSpend = marker
        )
        assertTrue(result.skipRefused)
        assertEquals(marker, result.effectiveNowMillis)
        assertEquals(marker, result.state.markerWallMillis)
        assertEquals(claim, result.state.pendingClaimWallMillis)
    }

    @Test
    fun unseededNoSpends_markerStartsAtNow_noRefusal() {
        val result = evaluate(
            state = TokenWeekGuard.State(),
            now = claim,
            markerWeekHasSpend = false,
            latestSpend = 0L
        )
        assertFalse(result.skipRefused)
        assertEquals(claim, result.effectiveNowMillis)
        assertEquals(claim, result.state.markerWallMillis)
        assertEquals(0L, result.state.pendingClaimWallMillis)
    }

    @Test
    fun skipWithoutSpend_markerFollowsNow() {
        val result = evaluate(
            state = TokenWeekGuard.State(markerWallMillis = marker),
            now = claim,
            markerWeekHasSpend = false
        )
        assertFalse(result.skipRefused)
        assertEquals(claim, result.effectiveNowMillis)
        assertEquals(claim, result.state.markerWallMillis)
    }

    @Test
    fun sameWeekRead_markerFollowsForward_andNeverRewinds() {
        val later = marker + 3_600_000L
        val forward = evaluate(
            state = TokenWeekGuard.State(markerWallMillis = marker),
            now = later
        )
        assertFalse(forward.skipRefused)
        assertEquals(later, forward.effectiveNowMillis)
        assertEquals(later, forward.state.markerWallMillis)

        val rewound = evaluate(
            state = TokenWeekGuard.State(markerWallMillis = later),
            now = marker
        )
        assertEquals(later, rewound.state.markerWallMillis)
    }

    @Test
    fun pendingSkip_sameBootBeforeSpanLived_stillRefused() {
        val pending = TokenWeekGuard.State(
            markerWallMillis = marker,
            pendingClaimWallMillis = claim,
            pendingBootCount = 7,
            pendingGateElapsedRealtimeMillis = 1_000_000L
        )
        val result = evaluate(
            state = pending,
            now = claim + 3_600_000L,
            boot = 7,
            mono = 1_000_000L + 3_600_000L // one hour lived of a seven-day claim
        )
        assertTrue(result.skipRefused)
        assertEquals(marker, result.effectiveNowMillis)
        assertEquals(claim, result.state.pendingClaimWallMillis)
    }

    @Test
    fun pendingSkip_sameBootAfterSpanLived_corroborated() {
        val pending = TokenWeekGuard.State(
            markerWallMillis = marker,
            pendingClaimWallMillis = claim,
            pendingBootCount = 7,
            pendingGateElapsedRealtimeMillis = 1_000_000L
        )
        val now = claim + 60_000L
        val result = evaluate(
            state = pending,
            now = now,
            boot = 7,
            mono = 1_000_000L + weekMillis + 60_000L
        )
        assertFalse(result.skipRefused)
        assertEquals(now, result.effectiveNowMillis)
        assertEquals(now, result.state.markerWallMillis)
        assertEquals(0L, result.state.pendingClaimWallMillis)
    }

    @Test
    fun pendingSkip_corroborationRequiresTheClaimedWeek() {
        val pending = TokenWeekGuard.State(
            markerWallMillis = marker,
            pendingClaimWallMillis = claim,
            pendingBootCount = 7,
            pendingGateElapsedRealtimeMillis = 1_000_000L
        )
        // The span was lived, but the wall clock was moved AGAIN —
        // the week on the clock is not the week that was claimed.
        val result = evaluate(
            state = pending,
            now = claim + weekMillis,
            boot = 7,
            mono = 1_000_000L + weekMillis + 60_000L
        )
        assertTrue(result.skipRefused)
        assertEquals(marker, result.effectiveNowMillis)
    }

    @Test
    fun pendingSkip_rebootInsideSpan_rearmsAndStillRefuses() {
        val pending = TokenWeekGuard.State(
            markerWallMillis = marker,
            pendingClaimWallMillis = claim,
            pendingBootCount = 7,
            pendingGateElapsedRealtimeMillis = 1_000_000L
        )
        val result = evaluate(
            state = pending,
            now = claim,
            boot = 8,
            mono = 5_000L // a young boot — the off-time is unaccountable
        )
        assertTrue(result.skipRefused)
        assertEquals(marker, result.effectiveNowMillis)
        assertEquals(8, result.state.pendingBootCount)
        assertEquals(claim, result.state.pendingClaimWallMillis)
    }

    @Test
    fun pendingSkip_clockHealed_skipDissolves() {
        val pending = TokenWeekGuard.State(
            markerWallMillis = marker,
            pendingClaimWallMillis = claim,
            pendingBootCount = 7,
            pendingGateElapsedRealtimeMillis = 1_000_000L
        )
        val healedNow = at(2026, 10, 29, 12)
        val result = evaluate(
            state = pending,
            now = healedNow,
            boot = 8,
            mono = 5_000L
        )
        assertFalse(result.skipRefused)
        assertEquals(healedNow, result.effectiveNowMillis)
        assertEquals(healedNow, result.state.markerWallMillis)
        assertEquals(0L, result.state.pendingClaimWallMillis)
    }
}
