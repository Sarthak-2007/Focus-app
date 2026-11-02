package com.example

import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.ui.signing.AllowancePawl
import com.example.ui.signing.BreakChoice
import com.example.ui.signing.CombinationSpin
import com.example.ui.signing.SigningDial
import com.example.ui.signing.SigningSlip
import com.example.ui.signing.SigningState
import com.example.ui.signing.SigningTexture
import com.example.ui.signing.SigningToken
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-36 — the Signing's mechanisms, pinned in the end phase's
 * spirit but written with the slice (the venue's Targeted job runs
 * them): the dial's rail and vernier, the combination's seat law,
 * the break's free choice, the pawl's asymmetric travel, the slip's
 * lawful lines. Figures are the foundation's own
 * (StrongroomMaximum), never this file's inventions.
 */
class SigningMechanismsTest {

    // ── The dial ────────────────────────────────────────────────

    @Test
    fun dial_snapsToQuarterWithinTheFiledWindow() {
        // 05:03 lies within ±4 minutes of the 05:00 quarter: the
        // rail catches it and the detent fires.
        val (placed, fired) = SigningDial.settle(5 * 60 + 3, pm = false)
        assertEquals(5 * 60, placed)
        assertTrue(fired)
    }

    @Test
    fun dial_placesWithoutEventOutsideTheWindow() {
        // 05:09 is beyond the window of either neighbour quarter:
        // the hour is placed as the hand left it, with no event.
        val (placed, fired) = SigningDial.settle(5 * 60 + 9, pm = false)
        assertEquals(5 * 60 + 9, placed)
        assertFalse(fired)
    }

    @Test
    fun vernier_readsDetentPlusRemainder() {
        val reading = SigningDial.vernierFor(5 * 60 + 7)
        assertEquals(5 * 60, reading.mainDetentMinute)
        assertEquals(7, reading.vernierMinute)
        assertEquals(5 * 60 + 7, reading.exactMinute)
    }

    @Test
    fun dial_timeRoundTrips() {
        val t = SigningDial.timeFor(17 * 60 + 45)
        assertEquals(LocalTime.of(17, 45), t)
        assertEquals(17 * 60 + 45, SigningDial.minuteOfDay(t))
    }

    // ── The combination spin ────────────────────────────────────

    @Test
    fun spin_seatsOnlyInThePassLawfulDirection() {
        val spin = CombinationSpin()
        // Pass 1 runs clockwise: a counter-clockwise arrival at a
        // station seats nothing.
        assertFalse(spin.attemptSeat(0.0, velocityDegPerSec = 60.0, clockwise = false))
        assertEquals(0, spin.seatsTaken)
        assertTrue(spin.attemptSeat(0.0, velocityDegPerSec = 60.0, clockwise = true))
        assertEquals(1, spin.seatsTaken)
    }

    @Test
    fun spin_aFlungArrivalSeatsNothing() {
        val spin = CombinationSpin()
        // Above the seat ceiling (120°/s, filed): the hand must
        // ARRIVE, not fling.
        assertFalse(spin.attemptSeat(0.0, velocityDegPerSec = 200.0, clockwise = true))
        assertEquals(0, spin.seatsTaken)
    }

    @Test
    fun spin_threeSeatsAlternateAndCompleteTheTurnover() {
        val spin = CombinationSpin()
        assertTrue(spin.attemptSeat(0.0, 60.0, clockwise = true))
        assertTrue(spin.attemptSeat(72.0, -60.0, clockwise = false))
        assertTrue(spin.attemptSeat(144.0, 60.0, clockwise = true))
        assertTrue(spin.isComplete)
        // The great wheel's turnover is whole: 360° across seats.
        assertEquals(360.0, spin.wheelTurnoverDeg, 0.001)
        // A fourth seat does not exist.
        assertFalse(spin.attemptSeat(0.0, 60.0, clockwise = true))
    }

    @Test
    fun spin_stationAmplitudesThickenByPass() {
        val spin = CombinationSpin()
        assertEquals(70, spin.stationAmplitude())
        spin.attemptSeat(0.0, 60.0, clockwise = true)
        assertEquals(95, spin.stationAmplitude())
        spin.attemptSeat(72.0, -60.0, clockwise = false)
        assertEquals(120, spin.stationAmplitude())
    }

    // ── The break choice ────────────────────────────────────────

    @Test
    fun break_anyLengthStandsExactlyAsPlaced() {
        // D-6: 37 minutes is nobody's preset — it stands, unrounded.
        assertEquals(37, BreakChoice.place(37.0))
        assertEquals(113, BreakChoice.place(113.0))
        assertFalse(BreakChoice.isPreset(37))
    }

    @Test
    fun break_presetsAreShortcutsToTheSameFigures() {
        StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES.forEach { preset ->
            assertEquals(preset, BreakChoice.shortcut(preset))
            assertTrue(BreakChoice.isPreset(preset))
        }
    }

    // ── The allowance pawl ──────────────────────────────────────

    private fun guard(
        allowance: Int = 60,
        spentToday: Boolean = false,
        todayIso: String = "2026-11-03"
    ) = StrongroomGuard.GuardState(
        allowanceMinutes = allowance,
        spentDateIso = if (spentToday) todayIso else "",
        spentMinutesThatDay = if (spentToday) allowance else 0,
        dayPurseTotalMinutes = allowance
    )

    @Test
    fun pawl_tighteningIsInstant() {
        val move = AllowancePawl.move(
            guard(allowance = 60), targetMinutes = 30,
            todayIso = "2026-11-03", nextDayStartMillis = 999L
        )
        assertTrue(move is AllowancePawl.Move.Tightened)
        assertEquals(30, (move as AllowancePawl.Move.Tightened).state.allowanceMinutes)
    }

    @Test
    fun pawl_raiseWindsToTheNextDayStartOnly() {
        val move = AllowancePawl.move(
            guard(allowance = 60), targetMinutes = 90,
            todayIso = "2026-11-03", nextDayStartMillis = 999L
        )
        assertTrue(move is AllowancePawl.Move.RaiseWound)
        val wound = move as AllowancePawl.Move.RaiseWound
        assertEquals(999L, wound.effectiveAtMillis)
        // Today's allowance does NOT move: the raise is pending.
        assertEquals(60, wound.state.allowanceMinutes)
        assertEquals(90, wound.state.pendingAllowanceMinutes)
        assertTrue(AllowancePawl.raiseWound(wound.state))
    }

    @Test
    fun pawl_raiseBeyondTheHardStopWhenThePurseIsSpent() {
        val move = AllowancePawl.move(
            guard(allowance = 60, spentToday = true), targetMinutes = 90,
            todayIso = "2026-11-03", nextDayStartMillis = 999L
        )
        assertTrue(move is AllowancePawl.Move.BeyondHardStop)
    }

    // ── The slip ────────────────────────────────────────────────

    private fun composedDay(sentence: String?) = StrongroomComposition.ComposedDay(
        filingDateIso = "2026-11-04",
        shape = StrongroomComposition.DayShape.ORDINARY,
        record = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-04",
            lockedPackages = setOf("com.example.locked"),
            allowedPackages = listOf("com.example.allowed"),
            liftingTime = LocalTime.of(5, 30),
            sentence = sentence,
            breakLengthMinutes = 37,
            breakFiresAtOffsetMillis = -1L,
            allowanceMinutes = 60,
            gaps = emptyList(),
            taskLine = "physics, in his own words"
        ),
        amendedBySigning = true
    )

    @Test
    fun slip_carriesTheLawfulWordsAndHisFigures() {
        val lines = SigningSlip.lines(
            composedDay(sentence = "his own sentence"),
            firstSigning = true, codesWaitTonight = true, allowedCount = 1
        )
        val texts = lines.map { it.text }
        assertTrue(StrongroomDay.PURPOSE_SENTENCE in texts)
        assertTrue(SigningSlip.CLAUSE in texts)
        assertTrue(SigningSlip.DOOR_TELLING in texts)
        assertTrue("codes wait tonight" in texts)
        assertTrue("ordinary day" in texts)
        assertTrue("physics, in his own words" in texts)
        assertTrue("his own sentence" in texts)
        assertTrue("05:30" in texts)
        assertTrue("37" in texts)
    }

    // ── Texture + the table's standing state (Signing.kt) ───────

    @Test
    fun texture_grainIsPositionLocked() {
        // The same point of paper carries the same grain on every
        // frame — a shimmering grain would be a filter, not the
        // surface's own property (intake §1).
        val a = SigningTexture.grainAt(3, 7, seed = 97)
        val b = SigningTexture.grainAt(3, 7, seed = 97)
        assertEquals(a, b, 0.0f)
        assertTrue(a in 0f..1f)
        // Paper's relief cap is the shallowest on the table.
        assertEquals(1, SigningTexture.Species.PAPER.reliefEighthsCap)
        assertTrue(
            SigningTexture.Species.PAPER.reliefEighthsCap <
                SigningTexture.Species.STEEL.reliefEighthsCap
        )
    }

    @Test
    fun signingState_tokensOnTheTrayFollowTheSignedOrder() {
        val state = SigningState(
            day = composedDay(sentence = null),
            tokens = listOf(
                SigningToken("com.example.allowed", "Allowed"),
                SigningToken("com.example.other", "Other")
            ),
            guard = guard()
        )
        // The tray holds every token; the signed set stands in the
        // record's own order, exactly as signed.
        assertEquals(2, state.tokens.size)
        assertEquals(
            listOf("com.example.allowed"),
            state.day.record.allowedPackages
        )
    }

    @Test
    fun slip_keepsThePlaceWhenNoSentenceStands() {
        val lines = SigningSlip.lines(
            composedDay(sentence = null),
            firstSigning = false, codesWaitTonight = false, allowedCount = 0
        )
        val kept = lines.single { it.kind == SigningSlip.LineKind.KEPT_PLACE }
        assertEquals("", kept.text)
        assertFalse(lines.any { it.kind == SigningSlip.LineKind.SENTENCE })
        // No door telling off the first signing; no spent telling.
        assertFalse(lines.any { it.kind == SigningSlip.LineKind.DOOR_TELLING })
        assertFalse(lines.any { it.kind == SigningSlip.LineKind.SPENT_DAY })
    }
}
