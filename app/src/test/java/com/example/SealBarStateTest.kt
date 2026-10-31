package com.example

import androidx.compose.ui.graphics.Color
import com.example.service.WarrantState
import com.example.ui.components.BoltInputs
import com.example.ui.components.BoltWindow
import com.example.ui.components.SealBarParams
import com.example.ui.components.SealBarState
import com.example.ui.components.boltStateFor
import com.example.ui.components.endsAtNumeralsFor
import com.example.ui.components.endingSoonThresholdFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #4 — the Bolt fold's truth table (THE BOLT §2, landed by
 * the closer pass after KRALL K-1 / VEX §A proved the v0 string
 * re-encoding was not the design's one derivation). One test per
 * precedence row, in precedence order. Every input derives from
 * the host's ONE warrant read; the fold is pure and reads no
 * store, no seam helper, no warrant object.
 */
class SealBarStateTest {

    private fun inputs(
        running: Boolean = false,
        detention: Boolean = false,
        hasRead: Boolean = true,
        raw: WarrantState? = WarrantState.LIVE,
        stale: Boolean = false,
        shieldStale: Boolean = false,
        print: WarrantState? = WarrantState.LIVE,
        frozen: WarrantState? = WarrantState.LIVE,
        stopwatch: Boolean = false,
        remaining: Int? = null,
        span: Int? = null
    ) = BoltInputs(
        sessionRunning = running,
        detentionHeld = detention,
        hasRead = hasRead,
        liveRaw = raw,
        readStale = stale,
        shieldStale = shieldStale,
        printState = print,
        frozenState = frozen,
        isStopwatch = stopwatch,
        remainingSeconds = remaining,
        spanSeconds = span
    )

    // Row 1 — detention outranks every other face: the release
    // was refused; the seal stays shut and lit (KANTE exception).
    @Test
    fun detentionHeldIsDetentionFaultWhateverTheWireSays() {
        val face = boltStateFor(
            inputs(running = false, detention = true, raw = WarrantState.OFF,
                print = WarrantState.OFF, frozen = null)
        )
        assertEquals(SealBarState.DETENTION_FAULT, face.state)
        assertEquals(BoltWindow.BLANK, face.window)
    }

    // Row 2 — UNA2-04: running + live OFF opens the seal at once;
    // the frozen LIVE verdict is never consulted.
    @Test
    fun runningLiveOffOpensTheSealWhateverTheFreezeSaid() {
        val face = boltStateFor(
            inputs(running = true, raw = WarrantState.OFF,
                print = WarrantState.OFF, frozen = WarrantState.LIVE,
                remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.OFF_WHILE_RUNNING, face.state)
        assertEquals(BoltWindow.BLANK, face.window)
    }

    // Row 3 — STALE (KRALL K-2): running, raw LIVE, read aged
    // past the 45 s print law (printState has already degraded
    // to UNPROVEN — readStale is what tells this row from row 4).
    // Thrown pose, dark lamp, shuttered window: never RUNNING.
    @Test
    fun runningStaleLiveIsStaleNeverRunning() {
        val face = boltStateFor(
            inputs(running = true, raw = WarrantState.LIVE, stale = true,
                print = WarrantState.UNPROVEN, frozen = WarrantState.LIVE,
                remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.STALE, face.state)
        assertEquals(BoltWindow.SHUTTERED, face.window)
    }

    // Row 3b — B-C1 (DURCH-A4 port pass): running with a FRESH
    // warrant print but a dead shield beat folds to STALE, never
    // RUNNING — the warrant proves the bind, not the shield's
    // beat, and the two stale laws are not merged: this row
    // fires with readStale = false and print LIVE.
    @Test
    fun runningShieldBeatDeadIsStaleEvenWithAFreshWarrant() {
        val face = boltStateFor(
            inputs(running = true, raw = WarrantState.LIVE, stale = false,
                shieldStale = true, print = WarrantState.LIVE,
                frozen = WarrantState.LIVE, remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.STALE, face.state)
        assertEquals(BoltWindow.SHUTTERED, face.window)
        // Stopwatch sessions shutter the same way — no FLOW lamp
        // may burn over a dead shield beat.
        val flow = boltStateFor(
            inputs(running = true, stopwatch = true, shieldStale = true)
        )
        assertEquals(SealBarState.STALE, flow.state)
        // And the OFF row still outranks it: a live OFF wire is
        // the open face, not the stale one.
        val off = boltStateFor(
            inputs(running = true, raw = WarrantState.OFF,
                shieldStale = true, print = WarrantState.OFF,
                frozen = WarrantState.LIVE, remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.OFF_WHILE_RUNNING, off.state)
        // At idle the shield row does not exist: no session pose
        // is held, so a dead beat cannot shutter an idle window.
        val idle = boltStateFor(
            inputs(running = false, shieldStale = true, frozen = null)
        )
        assertEquals(SealBarState.BLOCKING, idle.state)
    }

    // Row 4a — running + live UNPROVEN wire (issue #6 C5(b)'s
    // degrade, as a fold row): shut plate, dark lamp, live end.
    @Test
    fun runningLiveUnprovenKeepsThePlateShutAndTheLampDark() {
        val face = boltStateFor(
            inputs(running = true, raw = WarrantState.UNPROVEN,
                print = WarrantState.UNPROVEN, frozen = WarrantState.LIVE,
                remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.RUNNING_UNPROVEN, face.state)
        assertEquals(BoltWindow.LIVE_END, face.window)
        // An unproven bind never earns ENDING_SOON's lit alert.
        val late = boltStateFor(
            inputs(running = true, raw = WarrantState.UNPROVEN,
                print = WarrantState.UNPROVEN, frozen = WarrantState.LIVE,
                remaining = 10, span = 3600)
        )
        assertEquals(SealBarState.RUNNING_UNPROVEN, late.state)
    }

    // Row 4b — the series law (HOLLIS H-6): the frozen verdict
    // alone can never light the running lamp. Frozen LIVE with
    // no read at all is still unproven.
    @Test
    fun runningFrozenAloneNeverLightsTheLamp() {
        val noRead = boltStateFor(
            inputs(running = true, hasRead = false, raw = null,
                print = null, frozen = WarrantState.LIVE,
                remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.RUNNING_UNPROVEN, noRead.state)
        val frozenDead = boltStateFor(
            inputs(running = true, raw = WarrantState.LIVE,
                print = WarrantState.LIVE, frozen = WarrantState.UNPROVEN,
                remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.RUNNING_UNPROVEN, frozenDead.state)
    }

    // Row 5 — proven stopwatch: FLOW (magenta family, no end).
    @Test
    fun runningProvenStopwatchIsFlowWithNoEnd() {
        val face = boltStateFor(
            inputs(running = true, stopwatch = true, remaining = null)
        )
        assertEquals(SealBarState.FLOW, face.state)
        assertEquals(BoltWindow.BLANK, face.window)
        val unproven = boltStateFor(
            inputs(running = true, stopwatch = true,
                raw = WarrantState.UNPROVEN, print = WarrantState.UNPROVEN)
        )
        assertEquals(SealBarState.FLOW_UNPROVEN, unproven.state)
    }

    // Row 6 — ENDING_SOON at the span-floored threshold (H-3).
    @Test
    fun endingSoonStartsExactlyAtTheThresholdForLongSpans() {
        val t = SealBarParams.endingSoonThresholdSeconds
        assertEquals(
            SealBarState.ENDING_SOON,
            boltStateFor(
                inputs(running = true, remaining = t, span = 3600)
            ).state
        )
        assertEquals(
            SealBarState.RUNNING,
            boltStateFor(
                inputs(running = true, remaining = t + 1, span = 3600)
            ).state
        )
        assertEquals(
            SealBarState.ENDING_SOON,
            boltStateFor(
                inputs(running = true, remaining = 0, span = 3600)
            ).state
        )
    }

    // Row 6, the degenerate case (HOLLIS H-3): the shortest
    // legal session (300 s span) is NOT born ending soon —
    // its threshold is half its span.
    @Test
    fun shortestLegalSessionIsNotBornEndingSoon() {
        assertEquals(150, endingSoonThresholdFor(300))
        assertEquals(
            SealBarState.RUNNING,
            boltStateFor(
                inputs(running = true, remaining = 300, span = 300)
            ).state
        )
        assertEquals(
            SealBarState.ENDING_SOON,
            boltStateFor(
                inputs(running = true, remaining = 150, span = 300)
            ).state
        )
        // Long spans keep the absolute cap; unknown span keeps it too.
        assertEquals(300, endingSoonThresholdFor(3600))
        assertEquals(300, endingSoonThresholdFor(null))
    }

    // Row 7 — proven timed running, plain.
    @Test
    fun runningProvenTimedIsRunning() {
        val face = boltStateFor(
            inputs(running = true, remaining = 3600, span = 3600)
        )
        assertEquals(SealBarState.RUNNING, face.state)
        assertEquals(BoltWindow.LIVE_END, face.window)
        // Null remaining (no span priced) never falls into
        // ENDING_SOON over a null.
        assertEquals(
            SealBarState.RUNNING,
            boltStateFor(inputs(running = true, remaining = null)).state
        )
    }

    // Idle rows — OFF / NO_READ / BLOCKING / UNPROVEN, each with
    // the labelled planned-end window (HOLLIS H-1's cure: the
    // datum is present at idle, in plan grammar).
    @Test
    fun idleFacesCarryTheLabelledPlannedEnd() {
        val off = boltStateFor(
            inputs(raw = WarrantState.OFF, print = WarrantState.OFF,
                frozen = null)
        )
        assertEquals(SealBarState.OFF, off.state)
        assertEquals(BoltWindow.PLANNED_END, off.window)

        val noRead = boltStateFor(
            inputs(hasRead = false, raw = null, print = null, frozen = null)
        )
        assertEquals(SealBarState.NO_READ, noRead.state)
        assertEquals(BoltWindow.PLANNED_END, noRead.window)

        val blocking = boltStateFor(inputs(frozen = null))
        assertEquals(SealBarState.BLOCKING, blocking.state)
        assertEquals(BoltWindow.PLANNED_END, blocking.window)

        val unproven = boltStateFor(
            inputs(raw = WarrantState.UNPROVEN,
                print = WarrantState.UNPROVEN, frozen = null)
        )
        assertEquals(SealBarState.UNPROVEN, unproven.state)
    }

    // Idle stale LIVE degrades through the print law to the
    // caught pose — no STALE face at idle, because no session
    // pose exists to hold (the fold's documented boundary).
    @Test
    fun idleStaleLiveIsCaughtNotStale() {
        val face = boltStateFor(
            inputs(raw = WarrantState.LIVE, stale = true,
                print = WarrantState.UNPROVEN, frozen = null)
        )
        assertEquals(SealBarState.UNPROVEN, face.state)
    }

    @Test
    fun caughtIsOneThirdNeverHalf() {
        // THE BOLT §3: ½ reads as a resting state; ⅓ is the caught pose.
        assertEquals(1f / 3f, SealBarParams.plateUnprovenFraction, 0.0001f)
    }

    // THE BOLT §6 glance gate, recomputed over the elements that
    // actually render (KRALL K-5 / HOLLIS H-4): OFF's exposed bed
    // (90×34 = 3,060dp², not the phantom 644) plus its painted
    // socket, against RUNNING's real face — magenta disc + halo
    // ring, per mode. Model arithmetic from the params; the
    // render-side pixel measurement is BoltRenderGateTest's.
    @Test
    fun glanceGateArithmeticOverRenderedElements() {
        assertEquals(3060f, SealBarParams.bedExposedAreaDp2(), 0.001f)
        for (night in listOf(false, true)) {
            val running = SealBarParams.jewelLuminousTotal(
                SealBarState.RUNNING, night
            )
            assertTrue(
                "RUNNING jewel total $running over budget (night=$night)",
                running <= SealBarParams.jewelLuminousBudget
            )
            val off = SealBarParams.offLuminousTotal()
            assertTrue(
                "OFF total $off must be ≥3× RUNNING $running (night=$night)",
                off >= 3f * running
            )
        }
        assertTrue(SealBarParams.lampDiameter.value <= 9f)
    }

    @Test
    fun warningOrangeIsTheOneModeInvariantHue() {
        // THE BOLT §5: #D63E06 in both modes; #FF8A5C never returns.
        assertEquals(Color(0xFFD63E06), SealBarParams.warningOrange)
    }

    @Test
    fun numeralsComeOnlyFromAnEndsPrint() {
        assertEquals("14:30", endsAtNumeralsFor("ENDS 14:30"))
        assertNull(endsAtNumeralsFor("NO END"))
        assertNull(endsAtNumeralsFor("INVALID"))
        assertNull(endsAtNumeralsFor(""))
    }
    @Test
    fun endingSoonStartsExactlyAtTheThreshold() {
        val t = SealBarParams.endingSoonThresholdSeconds
        assertEquals(
            SealBarState.ENDING_SOON,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = t)
        )
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = t + 1)
        )
        // The last second of the span is still ending soon, never
        // already off — the session owns its end.
        assertEquals(
            SealBarState.ENDING_SOON,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = 0)
        )
    }
    @Test
    fun idleOnIsBlockingIdleOffIsOpenIdleUnknownIsHalfSet() {
        assertEquals(
            SealBarState.BLOCKING,
            sealBarStateFor(isSessionRunning = false, blockWord = "ON", remainingSeconds = null)
        )
        assertEquals(
            SealBarState.OFF,
            sealBarStateFor(isSessionRunning = false, blockWord = "OFF", remainingSeconds = null)
        )
        assertEquals(
            SealBarState.UNPROVEN,
            sealBarStateFor(isSessionRunning = false, blockWord = "UNKNOWN", remainingSeconds = null)
        )
    }
    @Test
    fun jewelStaysInsideTheGlanceBudgetCap() {
        // THE BOLT §6: jewel ≤9dp is the enforceable proxy for the
        // ≤41.9 luminous budget; the budget ships as a named constant.
        assertTrue(SealBarParams.lampDiameter.value <= 9f)
        assertEquals(41.9f, SealBarParams.jewelLuminousBudget, 0.0001f)
    }
    @Test
    fun runningOnIsRunningWithNumeralsState() {
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = 3600)
        )
        // Stopwatch: no remaining span exists, and the state must
        // not fall into ENDING SOON over a null.
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = null)
        )
    }
    @Test
    fun runningUnknownKeepsThePlateShutAndTheLampDark() {
        assertEquals(
            SealBarState.RUNNING_UNPROVEN,
            sealBarStateFor(isSessionRunning = true, blockWord = "UNKNOWN", remainingSeconds = 3600)
        )
        // An unproven bind never earns ENDING SOON's lit alert —
        // the threshold applies to the proven branch only.
        assertEquals(
            SealBarState.RUNNING_UNPROVEN,
            sealBarStateFor(isSessionRunning = true, blockWord = "UNKNOWN", remainingSeconds = 10)
        )
    }
}
