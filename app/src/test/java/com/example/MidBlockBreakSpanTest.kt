package com.example

import com.example.data.FocusSessionEntity
import com.example.data.SessionWallClock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #261 — the span and wall-clock derivations inside a
 * MID-BLOCK Safe Break, where the current block has NOT finished.
 *
 * The flip-born formulas price the in-break block whole (it HAS
 * finished at a flip). A mid-block break must price the frozen
 * part only — and the span TARGET must carry the extra break
 * slot the entry booked, or every surface would call the
 * session finished while its resumed block still had the
 * break's length left to run. The flip-born figures are pinned
 * unchanged by BreakSpanTest; these pin the mid-block branch.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MidBlockBreakSpanTest {

    private val target = 1500

    private fun row(
        blocks: Int = 2,
        blockIndex: Int = 1,
        remaining: Int = 0,
        taken: Int = 0,
        elapsed: Int = 0,
        extra: Int = 0,
        startWall: Long = 0L
    ) = FocusSessionEntity(
        intention = "MID-BLOCK SPAN",
        profileName = "Manual",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = 0L,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = true,
        midBlockSafeBreak = true,
        safeBreakSecondsRemaining = remaining,
        breakTakenSeconds = taken,
        extraSafeBreakSeconds = extra,
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun span_insideMidBlockBreak_countsTheFrozenPartOnly() {
        // 600 into block 1, 240 into the break (360 standing):
        // the span is 600 study + 240 break — never the whole
        // 1500 block the flip-born formula would price.
        val r = row(elapsed = 600, remaining = 360, extra = 600)
        assertEquals(600 + 240, r.spanElapsedSeconds)
    }

    @Test
    fun spanTarget_pricesTheExtraBreakSlot() {
        // 2 blocks: 2 targets + 1 scheduled break + the 1 extra
        // mid-block slot booked at entry.
        val r = row(elapsed = 600, remaining = 360, extra = 600)
        assertEquals(2 * target + 600 + 600, r.spanTargetSeconds)
        // Remaining = the block's 900 study + the break's
        // standing 360 + the scheduled break 600 + block 2's 1500.
        assertEquals(900 + 360 + 600 + target, r.spanRemainingSeconds)
    }

    @Test
    fun span_secondBlockMidBreak_keepsTheFinishedBlockWhole() {
        // Block 2 of 3, one full scheduled break behind, 100 into
        // block 2 and 60 into its mid-block break.
        val r = row(
            blocks = 3, blockIndex = 2, elapsed = 100,
            remaining = 540, taken = 600, extra = 600
        )
        assertEquals(target + 600 + 100 + 60, r.spanElapsedSeconds)
        assertEquals(3 * target + 2 * 600 + 600, r.spanTargetSeconds)
    }

    @Test
    fun wallClockAnchor_midBlockBreakStandsOnTheFrozenElapsed() {
        // The last-known-alive anchor is the block's start wall
        // plus the FROZEN elapsed plus the break time run —
        // pricing the whole target here would move the anchor
        // 900 s into a future that has not happened.
        val r = row(elapsed = 600, remaining = 360, extra = 600, startWall = 1_000_000L)
        assertEquals(1_000_000L + (600 + 240) * 1000L, SessionWallClock.anchorWallMillis(r))
        // And the planned end is that anchor plus the true
        // remaining (900 study + 360 break standing + the 600
        // scheduled break + block 2's 1500).
        assertEquals(
            1_000_000L + (600 + 240 + 900 + 360 + 600 + target) * 1000L,
            SessionWallClock.plannedEndWallMillis(r)
        )
    }
}
