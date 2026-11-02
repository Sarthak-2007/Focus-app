package com.example

import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.ui.foundation.CeremonyScores
import com.example.ui.foundation.DoorBody
import com.example.ui.foundation.WithdrawalBodies
import com.example.ui.lifting.BRACKET_SENTENCE
import com.example.ui.lifting.LiftingCeremony
import com.example.ui.lifting.LiftingTexture
import com.example.ui.lifting.LiftCeremonyPhase
import com.example.ui.lifting.PlateLanding
import com.example.ui.lifting.PlatePileView
import com.example.ui.lifting.StruckPlateView
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomInks
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-40 — THE LIFTING + THE PLATE STRIKE, pinned where
 * they live: the ceremony clock (C-17), the bolts' withdrawal
 * testimony (275/221/183 ms filed slides, reverse load order),
 * the door's weight-driven opening and its strike, the struck
 * plate's graded forms (TX-SHOW-10/13/14), the pile that is
 * never totalled (C-07), and the brass grain (LiftingTexture).
 * All motion figures UNPROVEN-ON-DEVICE, as filed.
 */
class LiftingMechanismsTest {

    // ── The ceremony clock ─────────────────────────────────────

    @Test
    fun `ceremony standsStillUntilTheLockTrips`() {
        val pose = LiftingCeremony.poseAt(0)
        assertEquals(LiftCeremonyPhase.STILLNESS, pose.phase)
        assertEquals(0, pose.boltsWithdrawn)
        assertEquals(0.0, pose.doorAngleDeg, 0.001)
        assertFalse(pose.struck)
        // The score's own filed points, read off the foundation.
        assertEquals(600, LiftingCeremony.TripMs)
        assertEquals(2680, LiftingCeremony.SwingOpenMs)
        assertEquals(6300, LiftingCeremony.ScoreEndMs)
        // The score files the strike at 5,574; the pose reads it
        // off the door BODY (the strike is fired by the travel,
        // never by a timer), and the foundation's own testimony
        // tolerance for the integrated strike is ±3 ms
        // (StrongroomMotionTest) — the two are held together
        // inside exactly that tolerance.
        val filedStrike = CeremonyScores.Lifting.first { it.name == "the-strike" }.atMs
        assertEquals(5574, filedStrike)
        assertTrue(
            kotlin.math.abs(LiftingCeremony.StrikeMs - filedStrike) <= 3
        )
    }

    @Test
    fun boltsWithdrawOneByOneInReverseLoadOrder() {
        // Filed slides (foundation WithdrawalBodies): 275/221/183.
        assertEquals(listOf(275, 221, 183), WithdrawalBodies.FILED_SLIDE_MS)
        // Nothing withdrawn before the first snap's strain ends.
        assertEquals(0, LiftingCeremony.poseAt(1439).boltsWithdrawn)
        // Snap-1 (heaviest) completes first: 1440 + 275.
        assertEquals(1, LiftingCeremony.poseAt(1440 + 275).boltsWithdrawn)
        assertEquals(2, LiftingCeremony.poseAt(1940 + 221).boltsWithdrawn)
        // All three clear before the swing-open begins (2680).
        val beforeSwing = LiftingCeremony.poseAt(2679)
        assertEquals(3, beforeSwing.boltsWithdrawn)
        assertEquals(LiftCeremonyPhase.WITHDRAWING, beforeSwing.phase)
        // Fractions run heaviest-first: mid-slide, bolt 1 is
        // travelling while bolt 3 has not strained yet.
        val mid = LiftingCeremony.poseAt(1600)
        assertTrue(mid.boltFractions[0] > 0f && mid.boltFractions[0] < 1f)
        assertTrue(mid.boltFractions[2] == 0f)
        // The strain phase never exceeds its filed 8% travel.
        val strain = LiftingCeremony.poseAt(1440 - 45)
        assertTrue(strain.boltFractions[0] in 0f..0.08f)
    }

    @Test
    fun doorOpensByWeightAndTheStrikeIsItsTravelMeetingTheStop() {
        // Shut until the swing-open; then the driven body's own
        // angles (filed testimony: strike at 2,894 swing-relative).
        assertEquals(0.0, LiftingCeremony.poseAt(2679).doorAngleDeg, 0.001)
        assertEquals(
            DoorBody.FiledTestimony.ANGLE_AT_500_MS,
            LiftingCeremony.doorAngleAt(2680 + 500), 0.05
        )
        assertFalse(LiftingCeremony.poseAt(LiftingCeremony.StrikeMs - 1).struck)
        val atStrike = LiftingCeremony.poseAt(LiftingCeremony.StrikeMs)
        assertTrue(atStrike.struck)
        assertTrue(atStrike.doorAngleDeg >= DoorBody.STRIKE_DEG)
        assertEquals(LiftCeremonyPhase.STRUCK, atStrike.phase)
        val settled = LiftingCeremony.poseAt(LiftingCeremony.SettledMs)
        assertTrue(settled.settled)
        assertEquals(LiftCeremonyPhase.SETTLED, settled.phase)
        // At its settle the door stands at its filed rest, against
        // the stop (final 82.09°, strike compression, no rebound).
        assertTrue(settled.doorAngleDeg >= DoorBody.STRIKE_DEG)
        assertEquals(DoorBody.FiledTestimony.FINAL_DEG, settled.doorAngleDeg, 1.2)
        val rest = LiftingCeremony.poseAt(LiftingCeremony.ScoreEndMs)
        assertEquals(DoorBody.FiledTestimony.FINAL_DEG, rest.doorAngleDeg, 0.05)
    }

    @Test
    fun plateLandsWithWeightAfterTheStrikeOnly() {
        assertEquals(0.0, PlateLanding.fractionAt(-1), 0.0001)
        assertEquals(0.0, PlateLanding.fractionAt(0), 0.0001)
        val mid = PlateLanding.fractionAt(80)
        assertTrue("landing is a travel, not a cut: $mid", mid in 0.05..0.99)
        val landed = PlateLanding.fractionAt(4000)
        assertEquals(1.0, landed, 0.06)
        // The pose carries no drop before the strike, ever.
        assertEquals(
            0.0,
            LiftingCeremony.poseAt(LiftingCeremony.StrikeMs - 1).plateDropFraction,
            0.0001
        )
        assertTrue(
            LiftingCeremony.poseAt(LiftingCeremony.StrikeMs + 900).plateDropFraction > 0.5
        )
    }

    @Test
    fun reducedMotionKeepsTheStrikeOnTheFinalPosesCut() {
        val strike = LiftingCeremony.strikeMs(reducedMotion = true)
        assertFalse(LiftingCeremony.poseAt(strike - 1, reducedMotion = true).struck)
        val pose = LiftingCeremony.poseAt(strike, reducedMotion = true)
        assertTrue(pose.struck)
        assertEquals(82.0, pose.doorAngleDeg, 0.001)
    }

    // ── The struck plate's forms ───────────────────────────────

    private fun wholePlate() = StrongroomDay.DayPlate(
        filingDateIso = "2026-11-02",
        hoursHeldMillis = 7L * 3_600_000L + 20L * 60_000L,
        punches = listOf(
            StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 600_000L),
            StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 3_600_000L),
            StrongroomDay.Punch(StrongroomDay.PunchKind.SHOWING, 7_200_000L),
            StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 9_000_000L)
        ),
        sealState = StrongroomDay.SealState.LIFTED,
        crankSealBroken = false
    )

    @Test
    fun struckPlateCarriesHoursHeldGradedHolesAndTheSealLine() {
        val view = StruckPlateView.fromStruckPlate(
            wholePlate(), sealedAtWallMillis = 0L, zone = ZoneId.of("UTC")
        )
        // TX-SHOW-10: the hours held, struck in plain figures.
        assertEquals("7 h 20 min", view.hoursHeldText)
        // TX-SHOW-13: exactly its lawful state.
        assertEquals("seal whole", view.sealLineText)
        assertFalse(view.isFailure)
        assertNull(view.bracket)
        // Graded holes (E-9): reach large · held medium ·
        // showing medium (digits never struck) · floor small,
        // in striking order, each at its place in the day.
        assertEquals(
            listOf(
                LiftingPlate.PunchGrade.LARGE,
                LiftingPlate.PunchGrade.MEDIUM,
                LiftingPlate.PunchGrade.MEDIUM,
                LiftingPlate.PunchGrade.SMALL
            ),
            view.punches.map { it.grade }
        )
        assertTrue(view.punches.all { it.dayFraction in 0f..1f })
        assertTrue(
            view.punches.zipWithNext().all { (a, b) -> a.dayFraction <= b.dayFraction }
        )
    }

    @Test
    fun hoursHeldFigureIsPlainAtEveryShape() {
        assertEquals("42 min", StruckPlateView.hoursHeldText(42L * 60_000L))
        assertEquals("7 h", StruckPlateView.hoursHeldText(7L * 3_600_000L))
        assertEquals("0 min", StruckPlateView.hoursHeldText(0L))
    }

    @Test
    fun fallenDayStrikesTheBracketPairInItsSettledForm() {
        // Sealed at 03:30 UTC, held 1h32 (standing at 05:02),
        // fallen by 11:40 — the gap is 6h38 of unheld span.
        val sealedAt = java.time.LocalDateTime.of(2026, 11, 2, 3, 30)
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val held = 92L * 60_000L
        val gap = (6L * 60 + 38) * 60_000L
        val plate = StrongroomDay.DayPlate(
            filingDateIso = "2026-11-02",
            hoursHeldMillis = held,
            punches = emptyList(),
            sealState = StrongroomDay.SealState.AJAR,
            crankSealBroken = false,
            bracketGapMillis = gap
        )
        val view = StruckPlateView.fromStruckPlate(plate, sealedAt, ZoneId.of("UTC"))
        assertTrue(view.isFailure)
        assertEquals("seal broken", view.sealLineText)
        val bracket = view.bracket!!
        // TX-SHOW-14: the settled pair — the sentence whole, and
        // the two hours as figures. The killed citation form
        // (TX-DIE-637) is not the sentence and appears nowhere.
        assertEquals(BRACKET_SENTENCE, bracket.sentence)
        assertEquals(
            "standing at the last check, fallen by the first check that found it",
            bracket.sentence
        )
        assertEquals("05:02", bracket.standingHourText)
        assertEquals("11:40", bracket.fallenHourText)
    }

    @Test
    fun wholeDayHasNoBracketAndNoFailure() {
        val truth = LiftingPlate.PlateTruth(
            filingDateIso = "2026-11-02",
            hoursHeldMillis = 3_600_000L,
            punches = emptyList(),
            sealTruth = LiftingPlate.SealTruth.WHOLE,
            crankSealBroken = true
        )
        val view = StruckPlateView.fromPlateTruth(truth)
        assertNull(view.bracket)
        assertFalse(view.isFailure)
        assertTrue(view.crankSealBroken)
    }

    // ── The pile ───────────────────────────────────────────────

    @Test
    fun pileStacksNewestOnTopAndIsNeverTotalled() {
        fun view(date: String, crank: Boolean) = StruckPlateView(
            filingDateIso = date, hoursHeldText = "1 h", punches = emptyList(),
            sealLineText = "seal whole", bracket = null,
            crankSealBroken = crank, isFailure = false
        )
        val pile = PlatePileView.fromPlates(
            listOf(view("2026-11-02", true), view("2026-11-01", false), view("2026-10-31", true))
        )
        assertEquals("2026-11-02", pile.landing?.filingDateIso)
        assertEquals(
            listOf("2026-11-01", "2026-10-31"),
            pile.resting.map { it.filingDateIso }
        )
        // The tray's kept seals, from the pile's own facts.
        assertEquals(listOf("2026-11-02", "2026-10-31"), pile.snappedSealDates)
        // Honest angles: small, deterministic, stilled by depth.
        assertTrue(PlatePileView.tiltDegFor(2) == PlatePileView.tiltDegFor(2))
        assertTrue(kotlin.math.abs(PlatePileView.tiltDegFor(0)) <= 2.0f)
        assertTrue(
            kotlin.math.abs(PlatePileView.tiltDegFor(4)) <
                kotlin.math.abs(PlatePileView.tiltDegFor(0)) + 0.001f
        )
        val empty = PlatePileView.fromPlates(emptyList())
        assertNull(empty.landing)
        assertTrue(empty.resting.isEmpty())
    }

    // ── Texture ────────────────────────────────────────────────

    @Test
    fun brassGrainIsDeterministicPositionLockedAndWithinItsCap() {
        assertTrue(LiftingTexture.grainAt(3, 4, 7) == LiftingTexture.grainAt(3, 4, 7))
        assertTrue(LiftingTexture.grainAt(3, 4, 7) in 0f..1f)
        // Neighbouring cells differ (grain, not gradient).
        assertTrue(LiftingTexture.grainAt(3, 4, 7) != LiftingTexture.grainAt(4, 4, 7))
        // A cell tone is the ink or its RELIGHT partner moved by
        // whole eighths within the species cap — never a new hue:
        // every channel stays between the ink's and its partners'.
        val tone = LiftingTexture.cellTone(
            StrongroomInks.Gold, StrongroomInks.Graphite,
            LiftingTexture.Species.BRASS, 5, 5, seed = 99,
            state = StrongroomLightState.DAY
        )
        assertTrue(tone.red in 0f..1f && tone.green < maxOf(tone.red, tone.blue))
        // Species figures: brass fine + shallow, steel coarse + deep.
        assertEquals(6, LiftingTexture.Species.BRASS.cellPx)
        assertEquals(2, LiftingTexture.Species.BRASS.reliefEighthsCap)
        assertEquals(11, LiftingTexture.Species.STEEL.cellPx)
        assertEquals(3, LiftingTexture.Species.STEEL.reliefEighthsCap)
    }
}
