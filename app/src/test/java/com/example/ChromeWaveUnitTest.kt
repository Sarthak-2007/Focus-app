package com.example

import com.example.data.RoutineEntity
import com.example.service.PermState
import com.example.service.UsageAccess
import com.example.service.WarrantState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.service.WatchStore
import com.example.ui.components.guardLineFor
import com.example.ui.evaluateTheme
import com.example.ui.views.RailGeometry
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Chrome wave — the pure derivations, tested as pure functions:
 * the warrant table (equivalent to the deleted liveWarrant logic),
 * the staleness derivative, the Guard line's three states, the rail
 * geometry at the reference width, and the theme evaluation's
 * precedence.
 */
class ChromeWaveUnitTest {

    // ------------------------------------------------------------------
    // The warrant: WatchStore.deriveWarrant must reproduce the old
    // liveWarrant() state table EXACTLY (spec §4.5 equivalence). The
    // reference below is the deleted function's logic, transcribed.
    // ------------------------------------------------------------------
    private fun referenceWarrantState(
        a11yBound: Boolean,
        stamped: Boolean,
        usage: UsageAccess
    ): WarrantState = when {
        !a11yBound -> WarrantState.OFF
        usage == UsageAccess.DENIED -> WarrantState.OFF
        !stamped -> WarrantState.UNPROVEN
        usage != UsageAccess.ALLOWED -> WarrantState.UNPROVEN
        else -> WarrantState.LIVE
    }

    @Test
    fun warrantDerivation_matchesTheDeletedLiveWarrantTable() {
        for (bound in listOf(false, true)) {
            for (stamped in listOf(false, true)) {
                for (usage in UsageAccess.entries) {
                    for (protecting in listOf(false, true)) {
                        val w = WatchStore.deriveWarrant(bound, stamped, usage, protecting)
                        assertEquals(
                            "bound=$bound stamped=$stamped usage=$usage",
                            referenceWarrantState(bound, stamped, usage),
                            w.state
                        )
                        assertEquals(protecting, w.shieldProtecting)
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Staleness: derived at render, never stored. A PASS older than
    // 45s of monotonic time is STALE; MISSING never stales.
    // ------------------------------------------------------------------
    private fun snapshotOf(vararg ms: WatchMeasurement): WatchSnapshot =
        WatchSnapshot(
            items = ms.associateBy { it.item },
            engineHeartbeatMono = 0L,
            engineHeartbeatWall = 0L,
            engineProtecting = false,
            lastProvedWallMillis = null
        )

    private fun perm(item: WatchItemId, state: PermState, mono: Long) =
        WatchMeasurement(item, perm = state, measuredMono = mono, measuredWall = 1_000L)

    @Test
    fun permPrint_staleOnlyForPassMeasurementsPast45s() {
        val snap = snapshotOf(
            perm(WatchItemId.ACCESSIBILITY, PermState.GRANTED, mono = 10_000L),
            perm(WatchItemId.USAGE_ACCESS, PermState.MISSING, mono = 10_000L)
        )
        assertEquals(
            com.example.service.PermPrint.GRANTED,
            WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, nowMono = 55_000L)
        )
        assertEquals(
            com.example.service.PermPrint.STALE,
            WatchStore.permPrintAt(snap, WatchItemId.ACCESSIBILITY, nowMono = 55_001L)
        )
        assertEquals(
            com.example.service.PermPrint.MISSING,
            WatchStore.permPrintAt(snap, WatchItemId.USAGE_ACCESS, nowMono = 9_999_999L)
        )
        assertEquals(
            com.example.service.PermPrint.UNMEASURED,
            WatchStore.permPrintAt(snap, WatchItemId.BATTERY, nowMono = 0L)
        )
    }

    // ------------------------------------------------------------------
    // The Guard line: whole / lack (bare + stale) / unmeasured.
    // ------------------------------------------------------------------
    private fun countedSnap(
        a11y: PermState, usage: PermState, admin: PermState, listener: PermState,
        mono: Long = 10_000L
    ): WatchSnapshot = snapshotOf(
        perm(WatchItemId.ACCESSIBILITY, a11y, mono),
        perm(WatchItemId.USAGE_ACCESS, usage, mono),
        perm(WatchItemId.DEVICE_ADMIN, admin, mono),
        perm(WatchItemId.NOTIFICATION_LISTENER, listener, mono)
    )

    @Test
    fun guardLine_wholePrints4of4() {
        val snap = countedSnap(
            PermState.GRANTED, PermState.GRANTED, PermState.GRANTED, PermState.GRANTED
        )
        assertEquals("GUARD 4/4", guardLineFor(snap, nowMono = 20_000L)?.text)
    }

    @Test
    fun guardLine_lackNamesTheFirstInBoardOrder() {
        val snap = countedSnap(
            PermState.GRANTED, PermState.MISSING, PermState.MISSING, PermState.GRANTED
        )
        assertEquals("GUARD 2/4 — USAGE ACCESS", guardLineFor(snap, nowMono = 20_000L)?.text)
    }

    @Test
    fun guardLine_staleNamesWithTheStaleSuffix() {
        val snap = countedSnap(
            PermState.GRANTED, PermState.GRANTED, PermState.GRANTED, PermState.GRANTED
        )
        // All four measured at 10_000; read at 60_000 -> all stale,
        // the first in board order is named with the suffix.
        assertEquals(
            "GUARD 0/4 — ACCESSIBILITY STALE",
            guardLineFor(snap, nowMono = 60_000L)?.text
        )
    }

    @Test
    fun guardLine_unmeasuredPrintsNothing() {
        val snap = countedSnap(
            PermState.GRANTED, PermState.GRANTED, PermState.GRANTED, PermState.GRANTED
        )
        val partial = snap.copy(items = snap.items - WatchItemId.DEVICE_ADMIN)
        assertNull(guardLineFor(partial, nowMono = 20_000L))
        assertNull(guardLineFor(null, nowMono = 20_000L))
        assertNull(guardLineFor(WatchSnapshot.EMPTY, nowMono = 20_000L))
    }

    // ------------------------------------------------------------------
    // Rail geometry at the 390dp reference width (spec §6.1).
    // ------------------------------------------------------------------
    @Test
    fun railGeometry_referenceWidth() {
        val recess = RailGeometry.recessWidthDp(390f)
        assertEquals(350f, recess, 0.001f)
        assertEquals(73.5f, RailGeometry.segmentDp(recess), 0.001f)
        assertEquals(65.5f, RailGeometry.capWidthDp(recess), 0.001f)
        assertEquals(298f, RailGeometry.parkStartDp(recess), 0.001f)
        assertEquals(8f, RailGeometry.capLeftDp(0f, recess), 0.001f)
        assertEquals(228.5f, RailGeometry.capLeftDp(3f, recess), 0.001f)
        // Travel DECK -> LEDGER = 220.5dp.
        assertEquals(
            220.5f,
            RailGeometry.capLeftDp(3f, recess) - RailGeometry.capLeftDp(0f, recess),
            0.001f
        )
        // The cap seats against the PARK zone's end wall.
        assertEquals(
            346f - 65.5f,
            RailGeometry.capLeftDp(4f, recess),
            0.001f
        )
        assertEquals(3, RailGeometry.stopForX(297f, recess))
        assertEquals(4, RailGeometry.stopForX(298f, recess))
        assertEquals(0, RailGeometry.stopForX(4f, recess))
        assertEquals(3, RailGeometry.nearestStop(2.6f))
        assertEquals(4, RailGeometry.nearestStop(3.6f))
    }

    // ------------------------------------------------------------------
    // Rail hit-testing + geometry floors (issue #62 findings 4 & 5):
    // the margins fabricate no stops, and narrow widths degrade to
    // zero-size segments instead of negative geometry / NaN travel.
    // ------------------------------------------------------------------
    @Test
    fun railGeometry_hitTestingBoundsAndNarrowFloors() {
        val recess = RailGeometry.recessWidthDp(390f) // 350, track 4..346
        // Left margin / inset strip (relX < INSET) selects nothing.
        assertEquals(RailGeometry.NO_STOP, RailGeometry.stopForX(-20f, recess))
        assertEquals(RailGeometry.NO_STOP, RailGeometry.stopForX(0f, recess))
        assertEquals(RailGeometry.NO_STOP, RailGeometry.stopForX(3.9f, recess))
        // Right of the track's end wall selects nothing.
        assertEquals(RailGeometry.NO_STOP, RailGeometry.stopForX(346.1f, recess))
        assertEquals(RailGeometry.NO_STOP, RailGeometry.stopForX(370f, recess))
        // The end wall itself is still PARK.
        assertEquals(RailGeometry.PARK_INDEX, RailGeometry.stopForX(346f, recess))

        // Narrow host: recess 20dp — segment and cap floor at zero,
        // never negative, and no position math produces NaN.
        val narrow = RailGeometry.recessWidthDp(60f)
        assertEquals(20f, narrow, 0.001f)
        assertEquals(0f, RailGeometry.segmentDp(narrow), 0.001f)
        assertEquals(0f, RailGeometry.capWidthDp(narrow), 0.001f)
        assertTrue(RailGeometry.capLeftDp(2f, narrow).isFinite())
        assertTrue(RailGeometry.stopForX(10f, narrow) in -1..4)
        // Zero-width host: recess floors at zero too.
        assertEquals(0f, RailGeometry.recessWidthDp(30f), 0.001f)
    }

    @Test
    fun railGeometry_resolveMeasureHonoursModes() {
        val exactly = android.view.View.MeasureSpec.EXACTLY
        val atMost = android.view.View.MeasureSpec.AT_MOST
        val unspecified = android.view.View.MeasureSpec.UNSPECIFIED
        // EXACTLY: the spec size wins, both ways.
        assertEquals(500, RailGeometry.resolveMeasure(exactly, 500, 390))
        assertEquals(200, RailGeometry.resolveMeasure(exactly, 200, 390))
        // AT_MOST: desired size, capped by the offer.
        assertEquals(390, RailGeometry.resolveMeasure(atMost, 500, 390))
        assertEquals(300, RailGeometry.resolveMeasure(atMost, 300, 390))
        // UNSPECIFIED: the desired size.
        assertEquals(390, RailGeometry.resolveMeasure(unspecified, 0, 390))
    }

    // ------------------------------------------------------------------
    // Theme evaluation: session > SLEEP window > nothing (§8.10).
    // ------------------------------------------------------------------
    private fun sleepRoutine(start: Int, end: Int, enabled: Boolean = true, pausedUntil: Long = 0L) =
        RoutineEntity(
            id = 7,
            name = "SLEEP",
            startMinutes = start,
            endMinutes = end,
            daysMask = 127,
            loadoutName = "SLEEP",
            enabled = enabled,
            pausedUntilMillis = pausedUntil
        )

    private fun tonightAt(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun themeEvaluation_precedence() {
        val sleep = sleepRoutine(start = 20 * 60, end = 3 * 60 + 30)
        val at2130 = tonightAt(21, 30)
        val at1200 = tonightAt(12, 0)

        val session = evaluateTheme(sessionActive = true, routines = listOf(sleep), nowMillis = at1200)
        assertTrue(session.isNight)
        assertEquals("AUTO · NIGHT — SESSION ACTIVE", session.readout)

        val owned = evaluateTheme(sessionActive = false, routines = listOf(sleep), nowMillis = at2130)
        assertTrue(owned.isNight)
        assertEquals("AUTO · NIGHT — SLEEP 20:00", owned.readout)

        val day = evaluateTheme(sessionActive = false, routines = listOf(sleep), nowMillis = at1200)
        assertTrue(!day.isNight)
        assertEquals("AUTO · DAY — NO ROUTINE OWNS IT", day.readout)

        // Audit R3b #116 (finding 1): a disabled or paused SLEEP
        // routine no longer costs the dark UI — clock night owns
        // the theme at 21:30 even when no routine does. (Before
        // the fix these two asserted DAY: darkness was hostage to
        // accepting SLEEP's blocking.)
        val disabled = evaluateTheme(
            sessionActive = false,
            routines = listOf(sleepRoutine(20 * 60, 3 * 60 + 30, enabled = false)),
            nowMillis = at2130
        )
        assertTrue(disabled.isNight)
        assertEquals("AUTO · NIGHT — CLOCK 20:00–06:00", disabled.readout)

        val paused = evaluateTheme(
            sessionActive = false,
            routines = listOf(
                sleepRoutine(20 * 60, 3 * 60 + 30, pausedUntil = at2130 + 60_000L)
            ),
            nowMillis = at2130
        )
        assertTrue(paused.isNight)
        assertEquals("AUTO · NIGHT — CLOCK 20:00–06:00", paused.readout)

        // Midday with SLEEP disabled stays DAY: the clock step is
        // a night signal, not a permanent dark mode.
        val disabledDay = evaluateTheme(
            sessionActive = false,
            routines = listOf(sleepRoutine(20 * 60, 3 * 60 + 30, enabled = false)),
            nowMillis = at1200
        )
        assertTrue(!disabledDay.isNight)
    }
}
