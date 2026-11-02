package com.example

import com.example.domain.DeskClockRecord
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.ui.strongroom.SigningAppOption
import com.example.ui.strongroom.StrongroomFlowModel
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * STAGE 15 — THE INTEGRATED PROTOTYPE'S FLOW LAW
 * (ui/strongroom/StrongroomFlowModel.kt), proven on the bare JVM:
 *
 *  - The opening place is derived, never chosen: an unpresented
 *    strike opens at the lifting; a standing seal opens on its
 *    face; an unstamped run opens at the first run; the rest at
 *    the doorway.
 *  - While sealed, only P2's lawful places exist.
 *  - The break's arrival stands on the desk clock; the
 *    antechamber outranks it while he stands in it.
 *  - Every composed surface's intake is built from P1's facts:
 *    the signing draft drops a locked package that left the
 *    catalogue (never crashes, never invents); the desk clock
 *    carries REACH/HELD/SHOWING and no FLOOR (the superseded
 *    lane); the plate adapter maps the seal truth by name,
 *    carries the bracket only with both hours, and refuses a
 *    still-sealed day; the Guard snapshot mirrors P1's state.
 */
class StrongroomFlowP15Test {

    private fun signing(
        locked: Set<String> = setOf("com.locked"),
        allowed: List<String> = listOf("com.allowed"),
        gaps: List<StrongroomDay.ShapedGap> = emptyList(),
        breakOffset: Long = -1L,
        breakLength: Int = 0
    ) = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-02",
        lockedPackages = locked,
        allowedPackages = allowed,
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = breakLength,
        breakFiresAtOffsetMillis = breakOffset,
        allowanceMinutes = 30,
        gaps = gaps
    )

    @Test
    fun openingPlace_strikeFirst_thenSeal_thenFirstRun_thenDoorway() {
        assertEquals(
            StrongroomFlowModel.Place.LIFTING,
            StrongroomFlowModel.openingPlace(
                setupCompleted = false, sealedStanding = true, strikeUnpresented = true
            )
        )
        assertEquals(
            StrongroomFlowModel.Place.SEALED_FACE,
            StrongroomFlowModel.openingPlace(
                setupCompleted = false, sealedStanding = true, strikeUnpresented = false
            )
        )
        assertEquals(
            StrongroomFlowModel.Place.FIRST_RUN,
            StrongroomFlowModel.openingPlace(
                setupCompleted = false, sealedStanding = false, strikeUnpresented = false
            )
        )
        assertEquals(
            StrongroomFlowModel.Place.DOORWAY,
            StrongroomFlowModel.openingPlace(
                setupCompleted = true, sealedStanding = false, strikeUnpresented = false
            )
        )
    }

    @Test
    fun sealedLawfulness_matchesP2SurfaceSet() {
        assertTrue(StrongroomFlowModel.placeLawfulWhileSealed(StrongroomFlowModel.Place.SEALED_FACE))
        assertTrue(StrongroomFlowModel.placeLawfulWhileSealed(StrongroomFlowModel.Place.ANTECHAMBER))
        assertTrue(StrongroomFlowModel.placeLawfulWhileSealed(StrongroomFlowModel.Place.DESK_CLOCK))
        for (place in listOf(
            StrongroomFlowModel.Place.FIRST_RUN, StrongroomFlowModel.Place.DOORWAY,
            StrongroomFlowModel.Place.SIGNING, StrongroomFlowModel.Place.LIFTING,
            StrongroomFlowModel.Place.ROOM_PILE, StrongroomFlowModel.Place.ROOM_DRAWER,
            StrongroomFlowModel.Place.GUARD
        )) {
            assertFalse("place $place must not exist while sealed", StrongroomFlowModel.placeLawfulWhileSealed(place))
        }
    }

    @Test
    fun sealedPlace_breakStandsOnTheClock_antechamberOutranks() {
        assertEquals(
            StrongroomFlowModel.Place.DESK_CLOCK,
            StrongroomFlowModel.sealedPlace(breakRunning = true, inAntechamber = false)
        )
        assertEquals(
            StrongroomFlowModel.Place.ANTECHAMBER,
            StrongroomFlowModel.sealedPlace(breakRunning = true, inAntechamber = true)
        )
        assertEquals(
            StrongroomFlowModel.Place.SEALED_FACE,
            StrongroomFlowModel.sealedPlace(breakRunning = false, inAntechamber = false)
        )
    }

    @Test
    fun signingDraft_dropsUncataloguedLocked_andFormatsTheHour() {
        val draft = StrongroomFlowModel.signingDraft(
            dayShapeName = "ORDINARY",
            appOptions = listOf(
                SigningAppOption("com.locked", "Locked"),
                SigningAppOption("com.allowed", "Allowed")
            ),
            composedLocked = setOf("com.locked", "com.gone"),
            liftingTime = LocalTime.of(20, 5),
            sentence = null,
            breakLengthMinutes = 15,
            spentDay = false
        )
        assertEquals(setOf("com.locked"), draft.composedLocked)
        assertEquals("20:05", draft.composedHour)
        assertEquals(15, draft.composedBreakMinutes)
    }

    @Test
    fun antechamber_twoSetsExactlyAsSigned_drumIsTheRest() {
        val state = StrongroomFlowModel.antechamberState(
            signing = signing(),
            purseRemainingMinutes = 12,
            nameOf = { pkg -> "name:$pkg" },
            cataloguePackages = listOf("com.locked", "com.allowed", "com.other")
        )
        assertEquals(listOf("name:com.allowed"), state.visibleAllowed)
        assertEquals(listOf("name:com.other"), state.visibleDrumStock)
        assertEquals(12, state.purseMinutes)
        assertTrue(state.heldCalls.isEmpty() && state.heldCodes.isEmpty())
    }

    @Test
    fun deskClockPunches_floorHasNoSpeciesOnTheClock() {
        val facts = StrongroomFlowModel.deskClockPunches(
            listOf(
                StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 10L),
                StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 20L),
                StrongroomDay.Punch(StrongroomDay.PunchKind.SHOWING, 30L),
                StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 40L)
            )
        )
        assertEquals(
            listOf(
                DeskClockRecord.PunchKind.REACH,
                DeskClockRecord.PunchKind.HELD,
                DeskClockRecord.PunchKind.SHOWING
            ),
            facts.map { it.kind }
        )
    }

    @Test
    fun deskClockGaps_inSpanOnly_plusTheFiringSpan() {
        val gaps = listOf(
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.MEDITATION, 0L, 60_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.GYM, 100_000L, 60_000L),
            StrongroomDay.ShapedGap(StrongroomDay.GapKind.SLEEP, 200_000L, 60_000L)
        )
        val seal = StrongroomDay.SealSpan(1_000_000L, 3, 10L * 3_600_000L)
        val spans = StrongroomFlowModel.deskClockGaps(
            signing(gaps = gaps, breakOffset = 3_600_000L, breakLength = 20), seal
        )
        assertEquals(2, spans.size)
        assertFalse(spans[0].isBreakFiring)
        assertEquals(100_000L, spans[0].startOffsetMillis)
        assertTrue(spans[1].isBreakFiring)
        assertEquals(3_600_000L, spans[1].startOffsetMillis)
        assertEquals(20L * 60_000L, spans[1].durationMillis)
    }

    @Test
    fun struckPlate_wholeDay_mapsTruthAndPunches() {
        val dayPlate = StrongroomDay.DayPlate(
            filingDateIso = "2026-11-02",
            hoursHeldMillis = 3_600_000L,
            punches = listOf(
                StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 100L, "com.locked"),
                StrongroomDay.Punch(StrongroomDay.PunchKind.SHOWING, 200L)
            ),
            sealState = StrongroomDay.SealState.LIFTED,
            crankSealBroken = false
        )
        val plate = StrongroomFlowModel.struckPlate(
            dayPlate, spanDurationMillis = 3_600_000L,
            standingHour = null, fallenHour = null
        )
        assertNotNull(plate)
        assertEquals(LiftingPlate.SealTruth.WHOLE, plate!!.truth.sealTruth)
        assertEquals(
            listOf(LiftingPlate.PunchKind.REACH, LiftingPlate.PunchKind.HELD),
            plate.truth.punchesInOrder.map { it.kind }
        )
    }

    @Test
    fun struckPlate_fallenDay_carriesTheBracketWithBothHours() {
        val dayPlate = StrongroomDay.DayPlate(
            filingDateIso = "2026-11-02",
            hoursHeldMillis = 1_800_000L,
            punches = emptyList(),
            sealState = StrongroomDay.SealState.AJAR,
            crankSealBroken = true,
            bracketGapMillis = 1_800_000L
        )
        val plate = StrongroomFlowModel.struckPlate(
            dayPlate, spanDurationMillis = 3_600_000L,
            standingHour = "21:40", fallenHour = "03:12"
        )
        assertNotNull(plate)
        assertEquals(LiftingPlate.SealTruth.AJAR, plate!!.truth.sealTruth)
        assertTrue(plate.truth.isFailure)
        assertEquals("21:40", plate.truth.bracketStandingHour)
        assertEquals("03:12", plate.truth.bracketFallenHour)
    }

    @Test
    fun struckPlate_stillSealedDay_strikesNothing() {
        val dayPlate = StrongroomDay.DayPlate(
            filingDateIso = "2026-11-02",
            hoursHeldMillis = 0L,
            punches = emptyList(),
            sealState = StrongroomDay.SealState.SEALED,
            crankSealBroken = false
        )
        assertNull(
            StrongroomFlowModel.struckPlate(
                dayPlate, spanDurationMillis = 3_600_000L,
                standingHour = null, fallenHour = null
            )
        )
    }

    @Test
    fun guardSnapshot_mirrorsP1State_andNextDayStartStandsAhead() {
        val now = 1_799_999_000_000L
        val snapshot = StrongroomFlowModel.guardSnapshot(
            guard = StrongroomGuard.GuardState(
                guardOn = true,
                allowanceMinutes = 45,
                stretchEndMillis = now + 86_400_000L
            ),
            nowMillis = now,
            sealedBlockActive = true,
            purseSpentToday = false,
            crankMinuteSpent = true
        )
        assertEquals(45, snapshot.allowanceMinutes)
        assertTrue(snapshot.sealedBlockActive)
        assertTrue(snapshot.crankMinuteSpent)
        assertTrue(snapshot.stretchActive)
        assertTrue(snapshot.nextDayStartMillis > now)
        assertTrue(snapshot.advanceChosenExits.isEmpty() && snapshot.routines.isEmpty())
    }

    @Test
    fun sealedFace_isTheEngravedSnapshotOnly() {
        val face = StrongroomFlowModel.sealedFace(signing())
        assertEquals("20:00", face.liftingHour)
        assertFalse(face.hasSentence)
    }
}
