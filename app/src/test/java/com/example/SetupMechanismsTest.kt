package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ReturnChit
import com.example.data.ReturnChitState
import com.example.data.SetupStore
import com.example.data.chitVerdictFor
import com.example.service.PermPrint
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.components.chitLineFor
import com.example.ui.components.chitPrintsAgainst
import com.example.ui.components.doorsMaskAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * THE SETUP MECHANISMS (issue #14 merge — the Return Chit +
 * the Setup Ledger, audit §3 Proposals 1–2). Proof set written
 * unrun under the compile-once law, like the reception's own
 * thirteen: the merged compile is the first run.
 *
 * The chit's law in one breath: a verdict may be written only
 * from a measurement taken AFTER the departure, and the mono
 * clock going backwards (a reboot in Settings) darkens the
 * chit rather than letting two boot clocks be compared.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
class SetupMechanismsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    private val departureMono = 1_000_000L

    private fun chit(
        state: ReturnChitState = ReturnChitState.DEPARTED,
        nextDoor: WatchItemId? = null
    ) = ReturnChit(
        door = WatchItemId.ACCESSIBILITY,
        departurePrint = PermPrint.MISSING,
        departureMono = departureMono,
        departureWall = 5_000L,
        state = state,
        nextDoor = nextDoor
    )

    private fun measurement(perm: PermState?, measuredMono: Long) =
        WatchMeasurement(
            WatchItemId.ACCESSIBILITY,
            perm = perm,
            measuredMono = measuredMono,
            measuredWall = 6_000L
        )

    // ------------------------------------------------------------
    // chitVerdictFor — the table, row by row (spec §1.4)
    // ------------------------------------------------------------

    @Test
    fun verdictRow1MonoBackwardsIsDark() {
        assertEquals(
            ReturnChitState.RETURNED_DARK,
            chitVerdictFor(chit(), measurement(PermState.GRANTED, departureMono + 1_000), departureMono - 1)
        )
    }

    @Test
    fun verdictRow2NothingAfterDepartureJudgesNothing() {
        // No measurement at all.
        assertNull(chitVerdictFor(chit(), null, departureMono + 10_000))
        // A measurement AT the departure instant is not after it.
        assertNull(
            chitVerdictFor(
                chit(),
                measurement(PermState.GRANTED, departureMono),
                departureMono + 10_000
            )
        )
        // A pre-departure stamp, however fresh it looks.
        assertNull(
            chitVerdictFor(
                chit(),
                measurement(PermState.GRANTED, departureMono - 500),
                departureMono + 10_000
            )
        )
    }

    @Test
    fun verdictRow3MeasurementWithoutPermIsUnread() {
        assertEquals(
            ReturnChitState.RETURNED_UNREAD,
            chitVerdictFor(chit(), measurement(null, departureMono + 1_000), departureMono + 10_000)
        )
    }

    @Test
    fun verdictRow4FreshGrantIsRepaired() {
        assertEquals(
            ReturnChitState.RETURNED_REPAIRED,
            chitVerdictFor(chit(), measurement(PermState.GRANTED, departureMono + 1_000), departureMono + 10_000)
        )
    }

    @Test
    fun verdictRow5StillMissingIsUnchanged() {
        assertEquals(
            ReturnChitState.RETURNED_UNCHANGED,
            chitVerdictFor(chit(), measurement(PermState.MISSING, departureMono + 1_000), departureMono + 10_000)
        )
    }

    @Test
    fun verdictRow6GrantedButAgedIsUnread() {
        val measured = departureMono + 1_000
        assertEquals(
            ReturnChitState.RETURNED_UNREAD,
            chitVerdictFor(
                chit(),
                measurement(PermState.GRANTED, measured),
                measured + WatchStore.STALE_AFTER_MS + 1
            )
        )
    }

    // ------------------------------------------------------------
    // chitLineFor — the printed lines (spec §1.6)
    // ------------------------------------------------------------

    @Test
    fun chitLinesAtMissingDeparture() {
        assertEquals(
            "WENT OUT NOT SET · CAME BACK SET. · NEXT — USAGE ACCESS.",
            chitLineFor(chit(ReturnChitState.RETURNED_REPAIRED, WatchItemId.USAGE_ACCESS))
        )
        assertEquals(
            "WENT OUT NOT SET · CAME BACK SET. · ALL FOUR SET.",
            chitLineFor(chit(ReturnChitState.RETURNED_REPAIRED))
        )
        assertEquals(
            "WENT OUT NOT SET · CAME BACK NOT SET. · NEXT — DEVICE ADMIN.",
            chitLineFor(chit(ReturnChitState.RETURNED_UNCHANGED, WatchItemId.DEVICE_ADMIN))
        )
        assertEquals(
            "WENT OUT NOT SET · CAME BACK NOT CHECKED.",
            chitLineFor(chit(ReturnChitState.RETURNED_UNREAD, WatchItemId.DEVICE_ADMIN))
        )
        assertEquals(
            "WENT OUT NOT SET · RETURN NOT READ.",
            chitLineFor(chit(ReturnChitState.RETURNED_DARK))
        )
        assertNull(chitLineFor(chit(ReturnChitState.DEPARTED)))
        assertNull(chitLineFor(chit(ReturnChitState.SPENT)))
    }

    @Test
    fun chitPrintsOnlyWhileTheCurrentPrintAgrees() {
        // C-15: a REPAIRED verdict dies the moment the door's
        // proof ages or the grant goes — it never stands under
        // UNPROVED or NOT SET.
        val repaired = chit(ReturnChitState.RETURNED_REPAIRED)
        assertTrue(chitPrintsAgainst(repaired, PermPrint.GRANTED))
        assertTrue(!chitPrintsAgainst(repaired, PermPrint.STALE))
        assertTrue(!chitPrintsAgainst(repaired, PermPrint.MISSING))
        val unchanged = chit(ReturnChitState.RETURNED_UNCHANGED)
        assertTrue(chitPrintsAgainst(unchanged, PermPrint.MISSING))
        assertTrue(!chitPrintsAgainst(unchanged, PermPrint.GRANTED))
        // UNREAD / DARK claim nothing about the door: any print.
        assertTrue(
            chitPrintsAgainst(chit(ReturnChitState.RETURNED_UNREAD), PermPrint.STALE)
        )
        assertTrue(
            chitPrintsAgainst(chit(ReturnChitState.RETURNED_DARK), PermPrint.MISSING)
        )
        assertTrue(!chitPrintsAgainst(chit(ReturnChitState.DEPARTED), PermPrint.GRANTED))
    }

    // ------------------------------------------------------------
    // doorsMaskAt — the ledger's derivation
    // ------------------------------------------------------------

    private fun snapshotWith(
        states: Map<WatchItemId, PermState>,
        monoFor: (WatchItemId) -> Long
    ): WatchSnapshot {
        val wall = System.currentTimeMillis()
        return WatchSnapshot(
            items = states.mapValues { (item, perm) ->
                WatchMeasurement(item, perm = perm, measuredMono = monoFor(item), measuredWall = wall)
            },
            engineHeartbeatMono = states.keys.maxOfOrNull { monoFor(it) } ?: 0L,
            engineHeartbeatWall = wall,
            engineProtecting = false,
            lastProvedWallMillis = wall
        )
    }

    @Test
    fun doorsMaskSetsFreshGrantsOnly() {
        val now = 2_000_000L
        val fresh = now - 1_000
        val aged = now - WatchStore.STALE_AFTER_MS - 1
        val snap = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.MISSING
            )
        ) { item -> if (item == WatchItemId.DEVICE_ADMIN) aged else fresh }
        // Bits: ACC(0) + USAGE(1) set; ADMIN(2) granted but STALE
        // — the ledger never upgrades aged proof; LISTENER(3)
        // missing. Mask = 0b0011.
        assertEquals(0b0011, doorsMaskAt(snap, now))
        assertEquals(0, doorsMaskAt(null, now))
    }

    // ------------------------------------------------------------
    // The store — ledger write-once + the chit round-trip
    // ------------------------------------------------------------

    @Test
    fun stampSetupIsWriteOnceForTheLedgerFields() {
        val store = SetupStore(context)
        store.stampSetup(0b0101)
        assertTrue(store.isCompleted())
        assertEquals(0b0101, store.doorsAtStampMask())
        assertTrue(store.stampedWall() != null)
        val wall = store.stampedWall()
        store.stampSetup(0b1111)
        assertEquals(
            "a later DONE never rewrites day one's mask",
            0b0101, store.doorsAtStampMask()
        )
        assertEquals(wall, store.stampedWall())
    }

    @Test
    fun chitRoundTripResolveSpendAndTheRefusals() {
        val store = SetupStore(context)
        store.writeDepartureChit(WatchItemId.DEVICE_ADMIN, PermPrint.MISSING)
        val written = store.chit()
        assertEquals(WatchItemId.DEVICE_ADMIN, written?.door)
        assertEquals(ReturnChitState.DEPARTED, written?.state)
        // Spend refuses from DEPARTED — an unjudged excursion is
        // never silently spent.
        store.spendChit()
        assertEquals(ReturnChitState.DEPARTED, store.chit()?.state)
        store.resolveChit(ReturnChitState.RETURNED_REPAIRED, WatchItemId.USAGE_ACCESS)
        assertEquals(ReturnChitState.RETURNED_REPAIRED, store.chit()?.state)
        assertEquals(WatchItemId.USAGE_ACCESS, store.chit()?.nextDoor)
        // A late second resolver rewrites nothing.
        store.resolveChit(ReturnChitState.RETURNED_UNCHANGED, null)
        assertEquals(ReturnChitState.RETURNED_REPAIRED, store.chit()?.state)
        store.spendChit()
        assertEquals(ReturnChitState.SPENT, store.chit()?.state)
        store.clearChit()
        assertNull(store.chit())
    }
}
