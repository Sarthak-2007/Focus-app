package com.example

import com.example.data.callExcludedAfter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * W3 #134 (phone-call-mid-session): the exclusion ledger's pure
 * derivation. While the call bypass signal is active, the
 * progress machine adds the whole wall seconds since the
 * session's last stored tick to the block's persisted
 * callExcludedSeconds and books elapsed as verified-minus-
 * excluded — the mirror image of the Safe Break countdown's
 * derivation (breakRemainingAfter), with the same two-ticker
 * and reboot properties.
 */
class CallExclusionTest {

    @Test
    fun tenMinuteCall_excludesSixHundredSeconds() {
        // One tick at the call's start (anchor 0), one at hang-up.
        assertEquals(600, callExcludedAfter(0, 0L, 600_000L))
    }

    @Test
    fun exclusionAccumulatesAcrossTicks() {
        var excluded = 0
        var lastTick = 0L
        // 2s shield cadence for a 50-minute call.
        for (tick in 1..1500) {
            val now = tick * 2000L
            excluded = callExcludedAfter(excluded, lastTick, now)
            lastTick = now
        }
        assertEquals(3000, excluded)
    }

    @Test
    fun twoInterleavedTickers_agreeNotDoubleCount() {
        // Both tickers derive from the SAME stored (excluded,
        // lastTick) pair and store their answer back — the second
        // writer in the same second finds the delta consumed.
        var excluded = 0
        var lastTick = 0L
        var now = 0L
        while (now < 600_000L) {
            now += 1000L
            excluded = callExcludedAfter(excluded, lastTick, now) // VM ticker
            lastTick = now
            if (now % 2000L == 0L) {
                excluded = callExcludedAfter(excluded, lastTick, now) // Shield, same instant
                lastTick = now
            }
        }
        assertEquals(600, excluded)
    }

    @Test
    fun rebootTick_excludesNothing() {
        // Monotonic clock reset by a reboot: no negative exclusion.
        assertEquals(120, callExcludedAfter(120, 900_000L, 5_000L))
    }

    @Test
    fun subSecondCadence_excludesWholeSecondsOnly() {
        // A 1500ms gap contributes 1 whole second; the derivation
        // is re-run from the stored pair every tick, exactly like
        // the break countdown it mirrors.
        assertEquals(1, callExcludedAfter(0, 0L, 1500L))
        assertEquals(0, callExcludedAfter(0, 0L, 999L))
    }
}
