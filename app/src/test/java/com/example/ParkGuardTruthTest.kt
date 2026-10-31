package com.example

import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.components.guardLineFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #9 — Park's guard truth (Zahn Z-2, P-6).
 *
 * A printed guard state must derive from live witness reads or
 * say it doesn't know. Before this cure, `countedWarranted`
 * returned a raw count off any snapshot — on a cold open's EMPTY
 * snapshot that count is 0, and a surface printing it reads
 * "all doors failed" when the truth is "nothing checked yet".
 * The count now does not exist until every one of the six is
 * measured, and the header line prints the confession
 * `GUARD — NOT CHECKED` in exactly those states.
 */
class ParkGuardTruthTest {

    private fun perm(item: WatchItemId, state: PermState, mono: Long = 10_000L) =
        WatchMeasurement(
            item = item, perm = state,
            measuredMono = mono, measuredWall = 1_700_000_000_000L, sweepGen = 1
        )

    private fun snapOf(vararg ms: WatchMeasurement): WatchSnapshot =
        WatchSnapshot(
            items = ms.associateBy { it.item },
            engineHeartbeatMono = 0L, engineHeartbeatWall = 0L,
            engineProtecting = false, lastProvedWallMillis = null
        )

    private fun fullSnap(missing: WatchItemId? = null): WatchSnapshot =
        snapOf(*WatchItemId.PERMISSIONS.map { item ->
            perm(item, if (item == missing) PermState.MISSING else PermState.GRANTED)
        }.toTypedArray())

    @Test
    fun countedWarranted_emptySnapshotHasNoCount() {
        assertNull(WatchStore.countedWarranted(WatchSnapshot.EMPTY, 20_000L))
    }

    @Test
    fun countedWarranted_partialSetHasNoCount() {
        // Five measured GRANTED, DEVICE_ADMIN never measured:
        // the arithmetic count would be 5 — a fabrication off a
        // partial set. The count must not exist.
        val partial = fullSnap().copy(
            items = fullSnap().items - WatchItemId.DEVICE_ADMIN
        )
        assertNull(WatchStore.countedWarranted(partial, 20_000L))
    }

    @Test
    fun countedWarranted_fullSetCountsGranted() {
        assertEquals(6, WatchStore.countedWarranted(fullSnap(), 20_000L))
        assertEquals(
            5,
            WatchStore.countedWarranted(fullSnap(missing = WatchItemId.OVERLAY), 20_000L)
        )
    }

    @Test
    fun coldOpenPrintsNotCheckedNeverACount() {
        // The 04:55 cold open, word for word: EMPTY snapshot in,
        // the confession out — never `GUARD 0/6`, never blank.
        assertEquals(
            "GUARD — NOT CHECKED",
            guardLineFor(WatchSnapshot.EMPTY, nowMono = 20_000L).text
        )
        assertEquals(
            "GUARD — NOT CHECKED",
            guardLineFor(null, nowMono = 20_000L).text
        )
    }
}
