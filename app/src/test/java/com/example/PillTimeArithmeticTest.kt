package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The break countdown's arithmetic, as a literal table — the
 * parts of `breakRemainingAfter` (data/WallAccrual.kt) that
 * [WallAccrualTest] does not already pin.
 *
 * Already covered there, deliberately NOT duplicated here: the
 * two-ticker wall-time drain, the process-death gap counting as
 * elapsed, and the reboot signature (now behind the tick credits
 * nothing). What was missing is the boundary table itself:
 * sub-second samples drain nothing, the drain steps exactly on
 * whole seconds, an exhausted break stays exhausted, and the
 * tick-0 anchor is legitimate (census D8's house-function half —
 * the derivation-level half lives in
 * [PillDerivationBoundaryTest]).
 *
 * Every expected value is a hand-computed literal. Plain JUnit.
 * Written against PR #231's train tree; parked on
 * `agent/issue-16-test-matrix` for post-train landing.
 * DECLARED, uncompiled.
 *
 * — T24 Builder seat 4, #16 wave
 */
class PillTimeArithmeticTest {

    @Test
    fun breakRemainingDrainsNothingInsideTheFirstWholeSecond() {
        // Stored 600 at tick 10_000: at +0 ms, +1 ms and +999 ms
        // the break has not yet lost a second.
        assertEquals(600, com.example.data.breakRemainingAfter(600, 10_000L, 10_000L))
        assertEquals(600, com.example.data.breakRemainingAfter(600, 10_000L, 10_001L))
        assertEquals(600, com.example.data.breakRemainingAfter(600, 10_000L, 10_999L))
    }

    @Test
    fun breakRemainingStepsExactlyOnWholeSeconds() {
        assertEquals(599, com.example.data.breakRemainingAfter(600, 10_000L, 11_000L))
        assertEquals(599, com.example.data.breakRemainingAfter(600, 10_000L, 11_999L))
        assertEquals(598, com.example.data.breakRemainingAfter(600, 10_000L, 12_000L))
        assertEquals(541, com.example.data.breakRemainingAfter(600, 10_000L, 69_000L))
        assertEquals(540, com.example.data.breakRemainingAfter(600, 10_000L, 70_000L))
    }

    @Test
    fun breakRemainingReachesZeroAtItsEndAndNeverBelow() {
        assertEquals(1, com.example.data.breakRemainingAfter(600, 10_000L, 609_000L))
        assertEquals(0, com.example.data.breakRemainingAfter(600, 10_000L, 610_000L))
        // An hour past the end the answer is still 0, never a
        // negative "remaining" a surface could print.
        assertEquals(0, com.example.data.breakRemainingAfter(600, 10_000L, 4_210_000L))
    }

    @Test
    fun breakRemainingOfAnExhaustedBreakStaysZero() {
        assertEquals(0, com.example.data.breakRemainingAfter(0, 10_000L, 10_000L))
        assertEquals(0, com.example.data.breakRemainingAfter(0, 10_000L, 610_000L))
    }

    @Test
    fun breakRemainingTreatsTickZeroAsALegitimateAnchor() {
        // Census D8: unlike the session's elapsedAt, this function
        // has NO "unstamped" sentinel — break entry always stamps
        // the tick, and 0 is the legitimate first millisecond
        // after boot. A 600 s break anchored at 0 has drained
        // 1 s at 1_000 ms and is fully drained at 1_000_000 ms.
        assertEquals(600, com.example.data.breakRemainingAfter(600, 0L, 0L))
        assertEquals(599, com.example.data.breakRemainingAfter(600, 0L, 1_000L))
        assertEquals(0, com.example.data.breakRemainingAfter(600, 0L, 1_000_000L))
    }

    @Test
    fun breakRemainingAtTheSameInstantIsTheStoredValue() {
        // The identity sample every ticker pair relies on: two
        // readers of the same stored pair at the same instant
        // compute the same answer.
        assertEquals(437, com.example.data.breakRemainingAfter(437, 123_456L, 123_456L))
    }
}
