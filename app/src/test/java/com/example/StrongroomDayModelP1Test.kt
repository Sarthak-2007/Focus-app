package com.example

import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneId

/**
 * P1 (BUILD-SR-3) — the Strongroom day model, pure JVM.
 *
 * The laws under test are Audit A §2 / Stage 2's, by name: the seal
 * is an elapsed-time span (wall-clock-proof), shaped gaps live
 * inside it and are never openings, the purse's spent = hard stop,
 * the break is a firing priced at signing at ANY freely chosen
 * length, the crank is one minute per week spent whole, the plate
 * tells the truth (ajar + bracket gap on failure), the day-start
 * is 03:30.
 */
class StrongroomDayModelP1Test {

    private val utc: ZoneId = ZoneId.of("UTC")
    private val signing = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-03",
        lockedPackages = setOf("com.video.app", "com.social.app"),
        liftingTime = LocalTime.of(6, 0),
        sentence = null,
        breakLengthMinutes = 47,
        breakFiresAtOffsetMillis = 3_600_000L,
        allowanceMinutes = 30,
        gaps = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 7_200_000L, 1_800_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.SADHANA, 9_000_000L, 1_800_000L)
        )
    )
    private val seal = StrongroomDay.SealSpan(
        sealedAtElapsedMillis = 100_000L,
        sealedBootCount = 3,
        spanDurationMillis = 10_800_000L // 3h
    )

    // ---------- the seal is elapsed time ----------

    @Test
    fun seal_liftsOnElapsedSpan_notBefore() {
        assertFalse(seal.isLifted(seal.liftingAtElapsedMillis - 1, 3))
        assertTrue(seal.isLifted(seal.liftingAtElapsedMillis, 3))
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(seal.liftingAtElapsedMillis - 1, 3, false))
        assertEquals(StrongroomDay.SealState.LIFTED, seal.stateAt(seal.liftingAtElapsedMillis, 3, false))
    }

    @Test
    fun spanDurationAtSealing_nextOccurrenceOfLiftingHour() {
        // 2026-11-02T20:00Z sealing for a 06:00 lifting -> next morning, 10h.
        val wall = java.time.LocalDateTime.of(2026, 11, 2, 20, 0).atZone(utc).toInstant().toEpochMilli()
        assertEquals(36_000_000L, StrongroomDay.spanDurationAtSealing(wall, LocalTime.of(6, 0), utc))
        // 05:00 sealing for 06:00 -> 1h the same morning.
        val wall2 = java.time.LocalDateTime.of(2026, 11, 3, 5, 0).atZone(utc).toInstant().toEpochMilli()
        assertEquals(3_600_000L, StrongroomDay.spanDurationAtSealing(wall2, LocalTime.of(6, 0), utc))
    }

    @Test
    fun bootDeathUnderSeal_readsAjar_neverLifts_bracketGapStruck() {
        // The phone died mid-span: a new boot's elapsed clock can
        // never fire the lifting, and the plate carries the gap.
        val afterRebootElapsed = 50_000L
        assertFalse(seal.isLifted(afterRebootElapsed, 4))
        assertFalse(seal.isLifted(Long.MAX_VALUE / 2, 4))
        assertEquals(StrongroomDay.SealState.AJAR, seal.stateAt(afterRebootElapsed, 4, false))
        val plate = StrongroomDay.strikePlate(
            signing, seal, emptyList(),
            StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L),
            nowElapsedMillis = afterRebootElapsed, nowBootCount = 4, sealFailed = false
        )
        assertEquals(StrongroomDay.SealState.AJAR, plate.sealState)
        assertTrue(plate.bracketGapMillis > 0L)
        assertEquals(seal.spanDurationMillis, plate.hoursHeldMillis + plate.bracketGapMillis)
    }

    @Test
    fun failedSeal_readsBroken_withBracketGap() {
        val plate = StrongroomDay.strikePlate(
            signing, seal, emptyList(),
            StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L),
            nowElapsedMillis = seal.sealedAtElapsedMillis + 3_600_000L,
            nowBootCount = 3, sealFailed = true
        )
        assertEquals(StrongroomDay.SealState.BROKEN, plate.sealState)
        assertEquals(3_600_000L, plate.hoursHeldMillis)
        assertEquals(seal.spanDurationMillis - 3_600_000L, plate.bracketGapMillis)
    }

    // ---------- shaped gaps ----------

    @Test
    fun gaps_liveInsideTheSeal_andAreNeverOpenings() {
        // Inside the gym gap the seal still stands: stateAt knows
        // nothing of gaps, and the gap answers only "which shape".
        val inGym = seal.sealedAtElapsedMillis + 7_200_000L + 1L
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(inGym, 3, false))
        assertEquals(
            StrongroomDay.GapKind.GYM,
            StrongroomDay.gapAt(signing.gaps, 7_200_000L + 1L)?.kind
        )
        assertNull(StrongroomDay.gapAt(signing.gaps, 5_000_000L))
        assertTrue(StrongroomDay.gapsAreLawful(signing.gaps, seal.spanDurationMillis))
    }

    @Test
    fun gaps_outsideSpanOrOverlapping_areUnlawful() {
        val outside = listOf(StrongroomDay.ShapedGap(StrongroomDay.GapKind.MEAL, 10_000_000L, 3_600_000L))
        assertFalse(StrongroomDay.gapsAreLawful(outside, seal.spanDurationMillis))
        val overlapping = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 0L, 3_600_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.SADHANA, 1_800_000L, 3_600_000L)
        )
        assertFalse(StrongroomDay.gapsAreLawful(overlapping, seal.spanDurationMillis))
    }

    @Test
    fun gapKinds_fiveShapes_threeMechanics_r2_2() {
        // R2-2: all five names stand, with differing mechanics.
        assertEquals(
            StrongroomDay.GapPlacement.PRE_SWING,
            StrongroomDay.GapKind.MEDITATION.placement
        )
        assertEquals(
            StrongroomDay.GapPlacement.TERMINAL,
            StrongroomDay.GapKind.SLEEP.placement
        )
        for (kind in listOf(
            StrongroomDay.GapKind.GYM, StrongroomDay.GapKind.SADHANA, StrongroomDay.GapKind.MEAL
        )) {
            assertEquals(StrongroomDay.GapPlacement.IN_SPAN, kind.placement)
        }
        // Meditation is never inside the span: gapAt cannot answer
        // it, and the span-fit law does not judge it.
        val withMorning = signing.gaps + StrongroomDay.ShapedGap(
            StrongroomDay.GapKind.MEDITATION, 0L, 3_600_000L
        )
        assertNull(StrongroomDay.gapAt(withMorning, 60_000L))
        assertTrue(StrongroomDay.gapsAreLawful(withMorning, seal.spanDurationMillis))
        // Sleep is terminal: it, too, is outside the span's law.
        val withSleep = signing.gaps + StrongroomDay.ShapedGap(
            StrongroomDay.GapKind.SLEEP, 0L, 28_800_000L
        )
        assertTrue(StrongroomDay.gapsAreLawful(withSleep, seal.spanDurationMillis))
    }

    @Test
    fun seal_lateSwingSealsWhatIsLeft_gapFitNeverRefusesTheSwing() {
        // The swing's law (store-level in StrongroomMigrationP1Test
        // composition test): gaps are validated for shape at
        // signing via the 24h bound — an in-span gap past any
        // possible span is refused in the calm hour.
        val absurd = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 0L, 25L * 3_600_000L)
        )
        assertFalse(StrongroomDay.gapsFitAnySpan(absurd))
        assertTrue(StrongroomDay.gapsFitAnySpan(signing.gaps))
    }

    // ---------- the purse ----------

    @Test
    fun purse_spentIsHardStop_overspendRefusedWhole() {
        var purse = StrongroomDay.AllowancePurse(totalMinutes = 30)
        purse = purse.spend(20)!!
        assertEquals(10, purse.remainingMinutes)
        assertNull(purse.spend(11)) // refused whole, never partially granted
        purse = purse.spend(10)!!
        assertTrue(purse.isHardStopped)
        assertNull(purse.spend(1))
    }

    // ---------- the break firing ----------

    @Test
    fun break_isAFiring_atTheSignedOffset_forAnyFreelyChosenLength() {
        // 47 minutes: not a preset — presets are shortcuts only and
        // no validation in the model consults them.
        assertFalse(StrongroomDay.BREAK_PRESET_SHORTCUTS_MINUTES.contains(47))
        val firing = StrongroomDay.breakFiring(signing, seal)!!
        assertEquals(seal.sealedAtElapsedMillis + 3_600_000L, firing.firesAtElapsedMillis)
        assertFalse(firing.isDue(firing.firesAtElapsedMillis - 1))
        assertTrue(firing.isDue(firing.firesAtElapsedMillis))
        assertTrue(firing.isRunning(firing.firesAtElapsedMillis + 60_000L))
        assertFalse(firing.isRunning(firing.endsAtElapsedMillis))
        assertEquals(47L * 60_000L, firing.endsAtElapsedMillis - firing.firesAtElapsedMillis)
    }

    @Test
    fun break_noBreakPriced_noFiring() {
        assertNull(StrongroomDay.breakFiring(signing.copy(breakFiresAtOffsetMillis = -1L), seal))
        assertNull(StrongroomDay.breakFiring(signing.copy(breakLengthMinutes = 0), seal))
    }

    // ---------- the crank ----------

    @Test
    fun crank_oneMinutePerWeek_spentWhole_sealKeptBroken() {
        val week = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 99L)
        assertTrue(week.minuteAvailable)
        val spent = week.spend(atWallMillis = 123_456L)!!
        assertEquals(StrongroomDay.CRANK_SECONDS, spent.spentSeconds)
        assertTrue(spent.sealBroken)
        assertFalse(spent.minuteAvailable)
        assertNull(spent.spend(atWallMillis = 999L)) // no second minute, no stockpile
    }

    // ---------- the plate ----------

    @Test
    fun plate_carriesPunchesByGrade_crankSealKept_hoursHeld() {
        val crank = StrongroomDay.GovernorCrankWeek(99L).spend(1L)!!
        val punches = listOf(
            StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 5_000L, "com.dialer"),
            StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 1_000L, "com.video.app"),
            StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 9_000L)
        )
        val plate = StrongroomDay.strikePlate(
            signing, seal, punches, crank,
            nowElapsedMillis = seal.liftingAtElapsedMillis, nowBootCount = 3, sealFailed = false
        )
        assertEquals(StrongroomDay.SealState.LIFTED, plate.sealState)
        assertEquals(seal.spanDurationMillis, plate.hoursHeldMillis)
        assertEquals(0L, plate.bracketGapMillis)
        assertTrue(plate.crankSealBroken)
        assertEquals(listOf(1_000L, 5_000L, 9_000L), plate.punches.map { it.atOffsetMillis })
        assertEquals(StrongroomDay.PunchSize.LARGE, StrongroomDay.PunchKind.REACH.size)
        assertEquals(StrongroomDay.PunchSize.MEDIUM, StrongroomDay.PunchKind.HELD.size)
        assertEquals(StrongroomDay.PunchSize.SMALL, StrongroomDay.PunchKind.FLOOR.size)
    }

    // ---------- the 03:30 seam ----------

    @Test
    fun dayStart_is0330_filingDateRespectsTheSeam() {
        // 02:00 on Nov 3 files under Nov 2; 04:00 files under Nov 3.
        val at0200 = java.time.LocalDateTime.of(2026, 11, 3, 2, 0).atZone(utc).toInstant().toEpochMilli()
        val at0400 = java.time.LocalDateTime.of(2026, 11, 3, 4, 0).atZone(utc).toInstant().toEpochMilli()
        assertEquals("2026-11-02", StrongroomDay.filingDateIso(at0200, utc))
        assertEquals("2026-11-03", StrongroomDay.filingDateIso(at0400, utc))
        val expectedStart = java.time.LocalDateTime.of(2026, 11, 3, 3, 30).atZone(utc).toInstant().toEpochMilli()
        assertEquals(expectedStart + java.time.Duration.ofDays(1).toMillis(), StrongroomDay.nextDayStartMillis(at0400, utc))
        assertEquals(expectedStart, StrongroomDay.dayStartMillis(at0400, utc))
    }

    @Test
    fun sentence_keptPlaceStandsWhenUnchosenOrBlank() {
        assertNull(signing.sentenceOrKeptPlace)
        assertNull(signing.copy(sentence = "  ").sentenceOrKeptPlace)
        assertEquals("tomorrow we hold", signing.copy(sentence = "tomorrow we hold").sentenceOrKeptPlace)
    }
}
