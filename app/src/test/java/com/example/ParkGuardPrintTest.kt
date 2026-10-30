package com.example

import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.components.GuardLinePrint
import com.example.ui.components.frozenGuardLineOrLive
import com.example.ui.components.guardLineFor
import com.example.ui.components.parkGuardCountSegment
import com.example.ui.components.parkGuardHeadline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #9, closer pass — the fabricated-count charges, pinned:
 *
 *  - Z-2: PARK's headline and foot signature may print a count
 *    ONLY off a fully measured counted set. Cold open (EMPTY),
 *    null, and every partial set print NOT CHECKED. The day
 *    simulator's 04:55 cold-open derivation runs through these
 *    exact functions: EMPTY in, `GUARD — NOT CHECKED` out.
 *  - Z-4: a seal freeze captured at the NOT CHECKED floor falls
 *    through to the live derivation; a MEASURED freeze holds.
 *  - Z-7: the usage door's UNSET is named by the Guard line,
 *    never dressed as a measured denial.
 */
class ParkGuardPrintTest {

    private fun snapshot(
        prints: Map<WatchItemId, PermState>,
        mono: Long = 10_000L
    ): WatchSnapshot {
        val items = prints.mapValues { (item, perm) ->
            WatchMeasurement(
                item = item,
                perm = perm,
                measuredMono = mono,
                measuredWall = 1_700_000_000_000L
            )
        }
        return WatchSnapshot(
            items = items,
            engineHeartbeatMono = mono,
            engineHeartbeatWall = 1_700_000_000_000L,
            engineProtecting = false,
            lastProvedWallMillis = null
        )
    }

    private fun wholeSet(): Map<WatchItemId, PermState> =
        WatchItemId.COUNTED.associateWith { PermState.GRANTED }

    // ---------------------------------------------------------------
    // Z-2 — the headline
    // ---------------------------------------------------------------

    @Test
    fun coldOpen_emptySnapshot_headlinePrintsNotChecked() {
        // THE day-simulator case: a fresh process, WatchSnapshot
        // .EMPTY, nothing measured. The old headline printed the
        // fabricated GUARD 0/4 here.
        assertEquals(
            "GUARD — NOT CHECKED",
            parkGuardHeadline(WatchSnapshot.EMPTY, 20_000L)
        )
        assertEquals(
            "NOT CHECKED",
            parkGuardCountSegment(WatchSnapshot.EMPTY, 20_000L)
        )
        assertNull(WatchStore.countedWarranted(WatchSnapshot.EMPTY, 20_000L))
    }

    @Test
    fun nullSnapshot_headlinePrintsNotChecked() {
        assertEquals("GUARD — NOT CHECKED", parkGuardHeadline(null, 20_000L))
        assertEquals("NOT CHECKED", parkGuardCountSegment(null, 20_000L))
    }

    @Test
    fun partialSet_headlinePrintsNotCheckedNeverACount() {
        // Three granted, one never measured: the header prints
        // NOT CHECKED for this snapshot, and now the headline
        // and the foot agree with it.
        val snap = snapshot(wholeSet() - WatchItemId.DEVICE_ADMIN)
        assertEquals("GUARD — NOT CHECKED", parkGuardHeadline(snap, 20_000L))
        assertEquals("NOT CHECKED", parkGuardCountSegment(snap, 20_000L))
        assertNull(WatchStore.countedWarranted(snap, 20_000L))
    }

    @Test
    fun fullyMeasuredSet_headlinePrintsTheDerivedCount() {
        val snap = snapshot(
            wholeSet() + (WatchItemId.USAGE_ACCESS to PermState.MISSING)
        )
        assertEquals("GUARD 3/4", parkGuardHeadline(snap, 20_000L))
        assertEquals("3/4", parkGuardCountSegment(snap, 20_000L))
        assertEquals(3, WatchStore.countedWarranted(snap, 20_000L))
    }

    @Test
    fun wholeSet_headlinePrintsFourOfFour() {
        val snap = snapshot(wholeSet())
        assertEquals("GUARD 4/4", parkGuardHeadline(snap, 20_000L))
        assertEquals("4/4", parkGuardCountSegment(snap, 20_000L))
    }

    // ---------------------------------------------------------------
    // Z-4 — the freeze fall-through
    // ---------------------------------------------------------------

    @Test
    fun frozenNotChecked_fallsThroughToLiveDerivation() {
        // A restored session on a fully measured machine: the
        // frozen floor must not print for the session's life.
        val snap = snapshot(wholeSet())
        val print = frozenGuardLineOrLive(
            GuardLinePrint("GUARD — NOT CHECKED"),
            sessionActive = true,
            snapshot = snap,
            nowMono = 20_000L
        )
        assertEquals("GUARD 4/4", print.text)
    }

    @Test
    fun frozenMeasured_holdsAgainstAnUnmeasuredStore() {
        // The freeze law itself is untouched: a measured capture
        // holds even when the live store has since reset.
        val print = frozenGuardLineOrLive(
            GuardLinePrint("GUARD 4/4"),
            sessionActive = true,
            snapshot = WatchSnapshot.EMPTY,
            nowMono = 20_000L
        )
        assertEquals("GUARD 4/4", print.text)
    }

    @Test
    fun noActiveSession_liveDerivationAnswers() {
        val snap = snapshot(wholeSet())
        val print = frozenGuardLineOrLive(
            GuardLinePrint("GUARD — NOT CHECKED"),
            sessionActive = false,
            snapshot = snap,
            nowMono = 20_000L
        )
        assertEquals("GUARD 4/4", print.text)
    }

    // ---------------------------------------------------------------
    // Z-7 — UNSET is its own word
    // ---------------------------------------------------------------

    @Test
    fun usageUnset_guardLineNamesUnset() {
        val snap = snapshot(
            wholeSet() + (WatchItemId.USAGE_ACCESS to PermState.UNSET)
        )
        assertEquals(
            "GUARD 3/4 — USAGE ACCESS UNSET",
            guardLineFor(snap, 20_000L).text
        )
        // UNSET is a measured answer: the count exists (3 of the
        // four warranted) — it is not gated like UNMEASURED.
        assertEquals("GUARD 3/4", parkGuardHeadline(snap, 20_000L))
    }
}
