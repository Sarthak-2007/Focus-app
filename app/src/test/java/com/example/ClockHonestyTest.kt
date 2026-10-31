package com.example

import com.example.data.ClockHonesty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #82 (Audit R2 · clock-attacks): the dual-clock discipline.
 * Pure arithmetic, no Android — the anchor/implied-now decisions
 * every clock verdict now runs on.
 */
class ClockHonestyTest {

    private val anchorWall = 1_000_000_000L
    private val anchorMono = 500_000L
    private val boot = 7

    // ---------- decide(): the honest now ----------

    @Test
    fun noAnchorTakesWallAtFaceValueAndAnchors() {
        val d = ClockHonesty.decide(0L, 0L, -1, anchorWall, anchorMono, boot)
        assertEquals(anchorWall, d.honestNowMillis)
        assertTrue(d.reanchor)
    }

    @Test
    fun agreeingClocksBelieveTheWallAndRefresh() {
        // 60 s of real time passes on both clocks.
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall + 60_000L,
            nowElapsedRealtimeMillis = anchorMono + 60_000L,
            nowBootCount = boot
        )
        assertEquals(anchorWall + 60_000L, d.honestNowMillis)
        assertTrue(d.reanchor)
    }

    @Test
    fun forwardJumpOfAWeekDoesNotMoveTheHonestNow() {
        // The F3 attack: date travelled +7 days, 60 s really passed.
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall + 7 * 24 * 3600_000L,
            nowElapsedRealtimeMillis = anchorMono + 60_000L,
            nowBootCount = boot
        )
        assertEquals(anchorWall + 60_000L, d.honestNowMillis)
        assertFalse(d.reanchor)
    }

    @Test
    fun rollbackOfAWeekDoesNotRewindTheHonestNow() {
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall - 7 * 24 * 3600_000L,
            nowElapsedRealtimeMillis = anchorMono + 60_000L,
            nowBootCount = boot
        )
        assertEquals(anchorWall + 60_000L, d.honestNowMillis)
        assertFalse(d.reanchor)
    }

    @Test
    fun smallCorrectionWithinToleranceIsBelieved() {
        // A 60 s manual/NTP correction: inside the 120 s band.
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall + 60_000L + 60_000L,
            nowElapsedRealtimeMillis = anchorMono + 60_000L,
            nowBootCount = boot
        )
        assertEquals(anchorWall + 120_000L, d.honestNowMillis)
        assertTrue(d.reanchor)
    }

    @Test
    fun rollbackAcrossRebootDoesNotRewindTheHonestNow() {
        // Issue #82 tightening (STURM3-16): the phone is powered OFF,
        // the wall clock is rolled back a week, then it boots. The
        // persisted anchor survives the reboot even though its
        // monotonic pair does not — the first read of the new boot
        // must floor at the anchored wall instant, never rewind to
        // the travelled wall (which would rewind the token week).
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall - 7 * 24 * 3600_000L,
            nowElapsedRealtimeMillis = 30_000L, // new boot: mono restarted
            nowBootCount = boot + 1
        )
        assertEquals(anchorWall, d.honestNowMillis)
        assertTrue(d.reanchor)
    }

    @Test
    fun newBootReanchorsAtFaceValue() {
        val d = ClockHonesty.decide(
            anchorWall, anchorMono, boot,
            nowWallMillis = anchorWall + 3600_000L,
            nowElapsedRealtimeMillis = 30_000L,
            nowBootCount = boot + 1
        )
        assertEquals(anchorWall + 3600_000L, d.honestNowMillis)
        assertTrue(d.reanchor)
    }

    // ---------- impliedNowMillis(): the shield-gap clock ----------

    @Test
    fun impliedNowIgnoresWallJumpSoNoGapIsForged() {
        // F1 attack shape: the wall clock jumps +11 s (or +11 days);
        // the implied now is built from monotonic time only, so a
        // beat 2 s old in monotonic terms stays 2 s old.
        val startWall = 2_000_000_000L
        val startMono = 800_000L
        val implied = ClockHonesty.impliedNowMillis(
            startWallMillis = startWall,
            accumulatedBeforeRebootSeconds = 0,
            startElapsedRealtimeMillis = startMono,
            lastTickElapsedRealtimeMillis = startMono + 30_000L,
            elapsedSeconds = 30,
            nowElapsedRealtimeMillis = startMono + 30_000L
        )
        assertEquals(startWall + 30_000L, implied)
        val beat = startWall + 28_000L
        assertTrue(implied - beat <= 10_000L) // no gap — the healthy truth
    }

    @Test
    fun impliedNowKeepsARealGapVisibleAfterRollback() {
        // The shield died at session start; 61 s of monotonic time
        // pass. Rolling the wall clock back cannot launder this.
        val startWall = 2_000_000_000L
        val startMono = 800_000L
        val implied = ClockHonesty.impliedNowMillis(
            startWallMillis = startWall,
            accumulatedBeforeRebootSeconds = 0,
            startElapsedRealtimeMillis = startMono,
            lastTickElapsedRealtimeMillis = startMono + 61_000L,
            elapsedSeconds = 61,
            nowElapsedRealtimeMillis = startMono + 61_000L
        )
        val beatRef = startWall // beat never advanced past the start
        assertTrue(implied - beatRef > 10_000L) // gap stands
    }

    @Test
    fun impliedNowPostRebootShapeUsesPriorElapsedPlusMonoSinceBoot() {
        val startWall = 2_000_000_000L
        val implied = ClockHonesty.impliedNowMillis(
            startWallMillis = startWall,
            accumulatedBeforeRebootSeconds = 120,
            startElapsedRealtimeMillis = 800_000L,
            lastTickElapsedRealtimeMillis = 900_000L,
            elapsedSeconds = 100,
            nowElapsedRealtimeMillis = 50_000L // new boot: mono restarted
        )
        // max(120, 100) s of prior elapsed + 50 s since boot.
        assertEquals(startWall + 170_000L, implied)
    }
}
