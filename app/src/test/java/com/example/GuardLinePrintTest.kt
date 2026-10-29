package com.example

import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.ui.components.guardLineFor
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #9 (Hof P-6) — the header Guard line's four states.
 * UNMEASURED used to print NOTHING, and blank read as fine on a
 * cold open; the line now prints `GUARD — NOT CHECKED` until the
 * counted set is measured. These tests pin all four prints.
 * DECLARED — written in the issue-9 cell, uncompiled (nobody
 * compiles in a cell); the suite's verdict belongs to Ren's build.
 */
class GuardLinePrintTest {

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

    @Test
    fun nullSnapshot_printsNotChecked() {
        assertEquals("GUARD — NOT CHECKED", guardLineFor(null, 10_000L).text)
    }

    @Test
    fun unmeasuredCountedItem_printsNotChecked() {
        // Three counted doors measured, the fourth never measured:
        // no count may print — the set is not measured.
        val snap = snapshot(wholeSet() - WatchItemId.NOTIFICATION_LISTENER)
        assertEquals("GUARD — NOT CHECKED", guardLineFor(snap, 10_000L).text)
    }

    @Test
    fun wholeSet_printsFourOfFour() {
        val snap = snapshot(wholeSet())
        assertEquals("GUARD 4/4", guardLineFor(snap, 10_000L).text)
    }

    @Test
    fun missingDoor_printsCountAndBareName() {
        val snap = snapshot(wholeSet() + (WatchItemId.USAGE_ACCESS to PermState.MISSING))
        assertEquals("GUARD 3/4 — USAGE ACCESS", guardLineFor(snap, 10_000L).text)
    }

    @Test
    fun staleDoor_printsCountAndStaleName() {
        // Measured at mono 10_000; read at 10_000 + 46_000 — past
        // the 45s stale threshold, so the grant no longer counts.
        val snap = snapshot(wholeSet())
        assertEquals(
            "GUARD 3/4 — ACCESSIBILITY STALE",
            guardLineFor(snap, 56_000L).text
        )
    }
}
