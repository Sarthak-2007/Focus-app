package com.example

import com.example.data.FocusSessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Truth Car 3 (ST-02, Tamm) — the span derivation itself.
 *
 * The honesty condition, at the figure every surface reads: a
 * completed break contributes the seconds ACTUALLY taken
 * (breakTakenSeconds), never the assumed full length. With every
 * break taken in full the new derivation agrees with the old
 * cycle arithmetic to the second — the regression guard — and
 * the moment a break is cut, only the taken part is priced.
 * The desk clock, the finish display and the receipt's sealed
 * elapsed all read this one derivation, so pinning it here pins
 * them all.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreakSpanTest {

    private val target = 1500

    private fun row(
        blocks: Int = 3,
        blockIndex: Int = 1,
        inBreak: Boolean = false,
        remaining: Int = 0,
        taken: Int = 0,
        elapsed: Int = 0
    ) = FocusSessionEntity(
        intention = "SPAN",
        profileName = "Manual",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = 0L,
        startElapsedRealtimeMillis = 0L,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = inBreak,
        safeBreakSecondsRemaining = remaining,
        breakTakenSeconds = taken,
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun span_fullBreaks_agreesWithTheOldArithmetic() {
        // Block 2 of 3, one full break behind, 100 into the block:
        // (2-1) * (1500 + 600) + 100 under the old formula.
        val r = row(blockIndex = 2, taken = 600, elapsed = 100)
        assertEquals(2200, r.spanElapsedSeconds)
        // Inside break 2, 450 of its 600 left (150 in):
        // (2-1) * 2100 + 1500 + 150 under the old formula.
        val inBreak = row(
            blockIndex = 2, inBreak = true, remaining = 450, taken = 600
        )
        assertEquals(3750, inBreak.spanElapsedSeconds)
    }

    @Test
    fun span_cutBreak_pricesOnlyWhatRan() {
        // Break 1 ended at 240 of 600; now 100 into block 2.
        val r = row(blockIndex = 2, taken = 240, elapsed = 100)
        assertEquals(target + 240 + 100, r.spanElapsedSeconds)
    }

    @Test
    fun span_insideBreak_countsTheRunningPartOnly() {
        // 240 into break 1 (360 left standing), nothing booked yet.
        val r = row(blockIndex = 1, inBreak = true, remaining = 360)
        assertEquals(target + 240, r.spanElapsedSeconds)
    }
}
