package com.example

import com.example.data.WallAccrual
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Hale LR2-S2-24, the tick-count poison: stored task seconds were
 * credited per CALL of the progress updater, and two tickers call
 * it (VM loop + Shield service) — ≈90 credited per 60 wall seconds.
 * WallAccrual must credit WALL time no matter who calls, or how
 * often.
 */
class WallAccrualTest {

    @Test
    fun twoInterleavedTickers_sumToWallTime_notCallCount() {
        val accrual = WallAccrual()
        var credited = 0
        // Both tickers observe the same task for 60 wall seconds,
        // each calling every 500ms (240 calls in total).
        credited += accrual.observe(sessionId = 1, accruingTaskId = 7, nowMonoMillis = 0L)
        for (tick in 1..120) {
            val now = tick * 500L
            credited += accrual.observe(1, 7, now) // VM ticker
            credited += accrual.observe(1, 7, now) // Shield ticker, same instant
        }
        assertEquals(60, credited)
    }

    @Test
    fun singleTicker_oneCallPerSecond_creditsSixty() {
        val accrual = WallAccrual()
        var credited = accrual.observe(1, 7, 0L)
        for (tick in 1..60) {
            credited += accrual.observe(1, 7, tick * 1000L)
        }
        assertEquals(60, credited)
    }

    @Test
    fun safeBreakGap_isNeverCredited() {
        val accrual = WallAccrual()
        var credited = accrual.observe(1, 7, 0L)
        credited += accrual.observe(1, 7, 30_000L) // 30s of work
        // Safe Break: ten minutes of observations with no accruing task.
        for (tick in 1..10) {
            credited += accrual.observe(1, null, 30_000L + tick * 60_000L)
        }
        credited += accrual.observe(1, 7, 630_000L) // first post-break tick re-anchors
        credited += accrual.observe(1, 7, 660_000L) // 30 more seconds of work
        assertEquals(60, credited)
    }

    @Test
    fun taskSwitch_reanchors_andCreditsNothingForTheSwitch() {
        val accrual = WallAccrual()
        var creditedA = accrual.observe(1, 7, 0L)
        creditedA += accrual.observe(1, 7, 45_000L)
        assertEquals(45, creditedA)
        // Task B becomes active: its first observation anchors only.
        assertEquals(0, accrual.observe(1, 9, 45_000L))
        assertEquals(15, accrual.observe(1, 9, 60_000L))
        // A new session also re-anchors.
        assertEquals(0, accrual.observe(2, 9, 60_000L))
    }

    // Bus's catch: the Safe Break countdown decremented per CALL
    // too — 600s drained in ~400 wall seconds under both tickers.
    @Test
    fun breakCountdown_twoTickers_drainsInWallTime() {
        var remaining = 600
        var lastTick = 0L
        var now = 0L
        // Simulate 600 wall seconds: VM ticks every 1s, Shield every 2s.
        // Each call re-derives from the stored (remaining, lastTick) pair
        // and stores its answer back — the second ticker to write in the
        // same second computes from the first's pair and agrees.
        while (now < 600_000L) {
            now += 1000L
            remaining = com.example.data.breakRemainingAfter(remaining, lastTick, now)
            lastTick = now
            if (now % 2000L == 0L) {
                // Shield's call at the same instant: zero delta, no drain.
                remaining = com.example.data.breakRemainingAfter(remaining, lastTick, now)
            }
        }
        assertEquals(0, remaining)
        assertEquals(600_000L, now)
    }

    @Test
    fun breakCountdown_processDeathGap_countsAsElapsed() {
        // App dead for five minutes mid-break: the break really elapsed.
        assertEquals(300, com.example.data.breakRemainingAfter(600, 10_000L, 310_000L))
    }

    @Test
    fun breakCountdown_rebootTick_creditsNothing() {
        // Monotonic clock reset by a reboot: no negative drain.
        assertEquals(600, com.example.data.breakRemainingAfter(600, 900_000L, 5_000L))
    }

    @Test
    fun sharedInstance_twoTickersStillSumToWallTime() {
        // Audit R1 #41: repositories no longer mint private
        // accruals — there is ONE shared instance, so the second
        // ticker (here: the "other repository's" handle on the
        // same object) finds every delta already consumed.
        val tickerA = WallAccrual.shared
        val tickerB = WallAccrual.shared
        assertSame(tickerA, tickerB)
        val session = 990_041
        var credited = tickerA.observe(session, 7, 1_000_000L)
        for (tick in 1..60) {
            credited += tickerA.observe(session, 7, 1_000_000L + tick * 1000L)
            credited += tickerB.observe(session, 7, 1_000_000L + tick * 1000L)
        }
        assertEquals(60, credited)
    }

    @Test
    fun seed_resumesTheAnchorAfterProcessDeath() {
        // Audit R1 #41: a fresh instance seeded from the
        // persisted anchor credits the death gap once, through
        // the ordinary observe path.
        val accrual = WallAccrual()
        accrual.seed(sessionId = 5, taskId = 9, lastMonoMillis = 10_000L)
        assertEquals(60, accrual.observe(5, 9, 70_000L))
        // Seeding an already-anchored instance is a no-op: the
        // live anchor is fresher than any stored one.
        accrual.seed(sessionId = 6, taskId = 6, lastMonoMillis = 0L)
        assertEquals(1, accrual.observe(5, 9, 71_000L))
        // The snapshot round-trips the anchor for persistence.
        assertEquals(Triple(5, 9, 71_000L), accrual.anchorSnapshot())
    }

    @Test
    fun breakTickPair_conservesTheSubSecondRemainder() {
        // Audit R1 #41: at a 1,050 ms cadence the old pair
        // (truncated remaining, FULL instant as anchor) leaked
        // 50 ms per tick — a 600 s break needed ~630 s of wall
        // time. The pair function ends the break on wall time.
        var remaining = 600
        var anchor = 0L
        var now = 0L
        var ticks = 0
        while (remaining > 0 && ticks < 1_000) {
            now += 1_050L
            val (nextRemaining, nextAnchor) =
                com.example.data.breakTickAfter(remaining, anchor, now)
            remaining = nextRemaining
            anchor = nextAnchor
            ticks++
        }
        assertEquals(0, remaining)
        assertTrue("break ended at ${now}ms wall", now in 600_000L..601_050L)
        assertTrue("anchor never runs ahead of now", anchor <= now)
    }

    @Test
    fun subSecondRemainders_areNotLeaked() {
        val accrual = WallAccrual()
        var credited = accrual.observe(1, 7, 0L)
        // 1500ms cadence for 60s of wall time: 40 calls.
        for (tick in 1..40) {
            credited += accrual.observe(1, 7, tick * 1500L)
        }
        assertEquals(60, credited)
    }
}
