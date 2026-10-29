package com.example

import com.example.data.LoadoutStore
import com.example.data.SmartLoadout
import com.example.service.PermPrint
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.components.ShieldCap
import com.example.ui.components.guardLineFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #58 (shield-cap-guardline) — the wave-3 cures, tested as
 * pure functions:
 *  - F1: a stale denial prints STALE on the Guard line, never
 *    bare-and-current forever; a fresh denial still prints bare.
 *  - F5: the Guard line's fraction is built from COUNTED.size.
 *  - F6: the cap index is clamped, and the index↔loadout mapping
 *    round-trips through its ONE source (LoadoutStore.CAP_ORDER).
 *  - F4: the re-tap guard fires only on a real move.
 * (F2 semantics and F3 hit-area live in the composable; their pure
 * inputs — clamped index, selected index, stop words — are the
 * values asserted here.)
 */
class ShieldCapGuardLineWave3Test {

    private fun perm(item: WatchItemId, state: PermState, mono: Long) =
        WatchMeasurement(item, perm = state, measuredMono = mono, measuredWall = 1_000L)

    private fun snapOf(vararg ms: WatchMeasurement): WatchSnapshot =
        WatchSnapshot(
            items = ms.associateBy { it.item },
            engineHeartbeatMono = 0L,
            engineHeartbeatWall = 0L,
            engineProtecting = false,
            lastProvedWallMillis = null
        )

    // --------------------------------------------------------------
    // F1 — stale guard semantics
    // --------------------------------------------------------------

    @Test
    fun guardLine_freshMissingPrintsBare() {
        val snap = snapOf(
            perm(WatchItemId.ACCESSIBILITY, PermState.MISSING, mono = 10_000L),
            perm(WatchItemId.USAGE_ACCESS, PermState.GRANTED, mono = 10_000L),
            perm(WatchItemId.DEVICE_ADMIN, PermState.GRANTED, mono = 10_000L),
            perm(WatchItemId.NOTIFICATION_LISTENER, PermState.GRANTED, mono = 10_000L)
        )
        assertEquals(
            "GUARD 3/4 — ACCESSIBILITY",
            guardLineFor(snap, nowMono = 20_000L)?.text
        )
    }

    @Test
    fun guardLine_staleMissingPrintsStaleSuffix() {
        // The denial was measured at 10s; the grants were re-measured
        // at 100s. Read at 110s: the denial is 100s old — ancient —
        // while the grants are fresh. The line must name the denial
        // STALE, not present it as current fact.
        val snap = snapOf(
            perm(WatchItemId.ACCESSIBILITY, PermState.MISSING, mono = 10_000L),
            perm(WatchItemId.USAGE_ACCESS, PermState.GRANTED, mono = 100_000L),
            perm(WatchItemId.DEVICE_ADMIN, PermState.GRANTED, mono = 100_000L),
            perm(WatchItemId.NOTIFICATION_LISTENER, PermState.GRANTED, mono = 100_000L)
        )
        assertEquals(
            "GUARD 3/4 — ACCESSIBILITY STALE",
            guardLineFor(snap, nowMono = 110_000L)?.text
        )
    }

    @Test
    fun permPrint_missingStalesAtTheSameBoundaryAsGranted() {
        val snap = snapOf(
            perm(WatchItemId.ACCESSIBILITY, PermState.MISSING, mono = 10_000L)
        )
        val boundary = 10_000L + WatchStore.STALE_AFTER_MS
        assertEquals(PermPrint.MISSING, WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, boundary))
        assertEquals(
            PermPrint.MISSING_STALE,
            WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, boundary + 1L)
        )
    }

    // --------------------------------------------------------------
    // F5 — the denominator is COUNTED.size, by construction
    // --------------------------------------------------------------

    @Test
    fun guardLine_fractionDenominatorIsCountedSize() {
        val total = WatchItemId.COUNTED.size
        val allGranted = snapOf(
            *WatchItemId.COUNTED.map { perm(it, PermState.GRANTED, mono = 10_000L) }.toTypedArray()
        )
        assertEquals("GUARD $total/$total", guardLineFor(allGranted, nowMono = 20_000L)?.text)

        val oneMissing = snapOf(
            perm(WatchItemId.COUNTED.first(), PermState.MISSING, mono = 10_000L),
            *WatchItemId.COUNTED.drop(1).map { perm(it, PermState.GRANTED, mono = 10_000L) }.toTypedArray()
        )
        assertEquals(
            "GUARD ${total - 1}/$total — ${WatchItemId.COUNTED.first().boardName}",
            guardLineFor(oneMissing, nowMono = 20_000L)?.text
        )
    }

    // --------------------------------------------------------------
    // F6 — clamping + the one mapping, round-tripped
    // --------------------------------------------------------------

    @Test
    fun shieldCap_clampIndexBoundsAnyHandedIndex() {
        assertEquals(0, ShieldCap.clampIndex(-5))
        assertEquals(0, ShieldCap.clampIndex(Int.MIN_VALUE))
        assertEquals(0, ShieldCap.clampIndex(0))
        assertEquals(1, ShieldCap.clampIndex(1))
        assertEquals(ShieldCap.STOP_COUNT - 1, ShieldCap.clampIndex(ShieldCap.STOP_COUNT - 1))
        assertEquals(ShieldCap.STOP_COUNT - 1, ShieldCap.clampIndex(ShieldCap.STOP_COUNT))
        assertEquals(ShieldCap.STOP_COUNT - 1, ShieldCap.clampIndex(Int.MAX_VALUE))
    }

    @Test
    fun capMapping_roundTripsThroughItsOneSource() {
        // Every seated index resolves to a loadout, and that loadout
        // maps back to the same index — in both directions, for
        // every stop, from the single CAP_ORDER source.
        for (i in LoadoutStore.CAP_ORDER.indices) {
            val loadout = LoadoutStore.loadoutAtCapIndex(i)
            assertEquals("index $i must seat a loadout", LoadoutStore.CAP_ORDER[i], loadout)
            assertEquals(i, LoadoutStore.capIndexOf(loadout!!))
        }
        for (loadout in SmartLoadout.entries) {
            assertEquals(loadout, LoadoutStore.loadoutAtCapIndex(LoadoutStore.capIndexOf(loadout)))
        }
        // The selector's stops are exactly the mapping's stops:
        // same count, so no stop renders without a loadout and no
        // loadout exists without a stop.
        assertEquals(LoadoutStore.CAP_ORDER.size, ShieldCap.STOP_COUNT)
        assertEquals(LoadoutStore.CAP_ORDER.size, ShieldCap.STOP_WORDS.size)
        assertEquals(
            listOf(SmartLoadout.STUDY, SmartLoadout.WORK_SPRINT, SmartLoadout.SLEEP),
            LoadoutStore.CAP_ORDER
        )
    }

    @Test
    fun capMapping_outOfRangeIndexSeatsNothing() {
        // Never SLEEP-by-else again: garbage resolves to null and
        // the caller applies nothing.
        assertNull(LoadoutStore.loadoutAtCapIndex(-1))
        assertNull(LoadoutStore.loadoutAtCapIndex(LoadoutStore.CAP_ORDER.size))
        assertNull(LoadoutStore.loadoutAtCapIndex(Int.MAX_VALUE))
    }

    // --------------------------------------------------------------
    // F4 — the re-tap guard
    // --------------------------------------------------------------

    @Test
    fun shieldCap_retapOfRestingStopDoesNotFire() {
        for (i in 0 until ShieldCap.STOP_COUNT) {
            assertFalse("re-tap of stop $i must not fire", ShieldCap.shouldHandleTap(i, i))
        }
        assertTrue(ShieldCap.shouldHandleTap(0, 1))
        assertTrue(ShieldCap.shouldHandleTap(0, 2))
        assertTrue(ShieldCap.shouldHandleTap(2, 0))
    }
}
