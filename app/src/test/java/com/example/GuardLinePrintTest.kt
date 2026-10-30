package com.example

import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.ui.components.guardLineFor
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #9 (Hof P-6) — the header Guard line's states.
 * UNMEASURED used to print NOTHING, and blank read as fine on a
 * cold open; the line now prints `GUARD — NOT CHECKED` until the
 * set is measured. Ported onto main's six-item PERMISSIONS board
 * (issue #109 F4) by W6-BAU-9: denominators are 6, fixtures grant
 * all six, and guardLineFor's nullable return is read via ?.text.
 */
class GuardLinePrintTest {

    private fun snapshot(
        prints: Map<WatchItemId, PermState>,
        mono: Long = 10_000L,
        monoFor: (WatchItemId) -> Long = { mono }
    ): WatchSnapshot {
        val items = prints.mapValues { (item, perm) ->
            WatchMeasurement(
                item = item,
                perm = perm,
                measuredMono = monoFor(item),
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
        WatchItemId.PERMISSIONS.associateWith { PermState.GRANTED }

    @Test
    fun nullSnapshot_printsNotChecked() {
        assertEquals("GUARD — NOT CHECKED", guardLineFor(null, 10_000L)?.text)
    }

    @Test
    fun unmeasuredCountedItem_printsNotChecked() {
        // Five of the six doors measured, one never measured:
        // no count may print — the set is not measured.
        val snap = snapshot(wholeSet() - WatchItemId.NOTIFICATION_LISTENER)
        assertEquals("GUARD — NOT CHECKED", guardLineFor(snap, 10_000L)?.text)
    }

    @Test
    fun wholeSet_printsSixOfSix() {
        val snap = snapshot(wholeSet())
        assertEquals("GUARD 6/6", guardLineFor(snap, 10_000L)?.text)
    }

    @Test
    fun missingDoor_printsCountAndBareName() {
        val snap = snapshot(wholeSet() + (WatchItemId.USAGE_ACCESS to PermState.MISSING))
        assertEquals("GUARD 5/6 — USAGE ACCESS", guardLineFor(snap, 10_000L)?.text)
    }

    @Test
    fun staleDoor_printsCountAndStaleName() {
        // ACCESSIBILITY measured at mono 10_000, the other five
        // re-measured at 50_000; read at 56_000 — only the
        // accessibility grant is past the 45s stale threshold, so
        // it alone no longer counts. (Finisher fix: the original
        // fixture measured all doors at 10_000, which stales the
        // whole set — the scenario never matched its own
        // expectation until the fixture staggered.)
        val snap = snapshot(
            wholeSet(),
            monoFor = { item ->
                if (item == WatchItemId.ACCESSIBILITY) 10_000L else 50_000L
            }
        )
        assertEquals(
            "GUARD 5/6 — ACCESSIBILITY STALE",
            guardLineFor(snap, 56_000L)?.text
        )
    }
}
