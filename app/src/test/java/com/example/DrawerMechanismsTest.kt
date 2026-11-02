package com.example

import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.ui.drawer.DrawerContents
import com.example.ui.drawer.DrawerPlate
import com.example.ui.drawer.DrawerSealLine
import com.example.ui.drawer.DrawerSlide
import com.example.ui.drawer.DrawerTexture
import com.example.ui.drawer.HandingBackGate
import com.example.ui.drawer.PileGeometry
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLightState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * BUILD-SR-41 covering tests: THE DRAWER's mechanisms — the
 * struck-row mapping (one model, two views), the pile's order
 * and geometry, the handing-back gate (C-11), the slide bodies,
 * and the texture law. (The R-6 FLAG_SECURE predicate test was
 * removed with the restriction itself, on Sarthak's order,
 * Nov 3, 2026: screenshots are allowed on every surface.)
 *
 * The census laws under test (INVENTORY-SR-1, firsthand):
 *  - C-22: the drawer stores finished tasks in his own words —
 *    no score, grade, total, or count exists in its types.
 *  - C-07: the pile is newest-first stack order; its types offer
 *    no summing operation (asserted here by behaviour: the only
 *    derivations are order, lookup, and the kept-seal days).
 *  - C-11: before the plate is read, the day is handed back —
 *    the gate stands shut while anyone waits undelivered.
 *  - B-14: a still-sealed day strikes nothing — the mapping
 *    refuses it whole rather than inventing a seal state.
 */
class DrawerMechanismsTest {

    private fun struckRow(
        iso: String,
        sealState: StrongroomDay.SealState,
        punches: List<StrongroomDay.Punch> = emptyList(),
        crank: Boolean = false,
        task: String? = null,
        heldMillis: Long = 5L * 3_600_000L
    ) = StrongroomDay.DayPlate(
        filingDateIso = iso,
        hoursHeldMillis = heldMillis,
        punches = punches,
        sealState = sealState,
        crankSealBroken = crank,
        taskLine = task
    )

    // ── The struck-row mapping ──────────────────────────────────

    @Test
    fun mapping_refusesASealedDayWhole() {
        try {
            DrawerPlate.fromStruckRow(
                struckRow("2026-11-01", StrongroomDay.SealState.SEALED)
            )
            fail("a sealed day's plate is not struck yet — B-14")
        } catch (expected: IllegalArgumentException) {
            // refused, not translated
        }
    }

    @Test
    fun mapping_sealLineHasExactlyItsTwoLawfulStates() {
        assertEquals(
            DrawerSealLine.WHOLE,
            DrawerPlate.fromStruckRow(
                struckRow("2026-11-01", StrongroomDay.SealState.LIFTED)
            ).sealLine
        )
        assertEquals(
            DrawerSealLine.BROKEN,
            DrawerPlate.fromStruckRow(
                struckRow("2026-11-01", StrongroomDay.SealState.BROKEN)
            ).sealLine
        )
        assertEquals(
            DrawerSealLine.BROKEN,
            DrawerPlate.fromStruckRow(
                struckRow("2026-11-01", StrongroomDay.SealState.AJAR)
            ).sealLine
        )
    }

    @Test
    fun mapping_punchGradesCarryAndShowingRidesAtHeld() {
        val plate = DrawerPlate.fromStruckRow(
            struckRow(
                "2026-11-01", StrongroomDay.SealState.LIFTED,
                punches = listOf(
                    StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 10L),
                    StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 20L),
                    StrongroomDay.Punch(StrongroomDay.PunchKind.SHOWING, 30L),
                    StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 40L)
                )
            )
        )
        assertEquals(
            listOf(
                LiftingPlate.PunchGrade.LARGE,
                LiftingPlate.PunchGrade.MEDIUM,
                LiftingPlate.PunchGrade.MEDIUM,
                LiftingPlate.PunchGrade.SMALL
            ),
            plate.punches.map { it.grade }
        )
    }

    @Test
    fun mapping_taskLineIsHisWordsVerbatimAndCrankStaysItsOwnFact() {
        val plate = DrawerPlate.fromStruckRow(
            struckRow(
                "2026-11-01", StrongroomDay.SealState.LIFTED,
                crank = true, task = "finish the optics one-shot"
            )
        )
        assertEquals("finish the optics one-shot", plate.taskLine)
        assertTrue(plate.crankSealKept)
        // A crank day still reads WHOLE: the snapped seal is its
        // own kept fact, not the day seal's failure.
        assertEquals(DrawerSealLine.WHOLE, plate.sealLine)
        assertFalse(plate.isFailure)
    }

    // ── The contents derivation ─────────────────────────────────

    @Test
    fun derive_pileIsNewestFirstAndTasksNewestFirst() {
        val contents = DrawerContents.derive(
            struckRows = listOf(
                struckRow("2026-10-30", StrongroomDay.SealState.LIFTED),
                struckRow("2026-11-02", StrongroomDay.SealState.LIFTED),
                struckRow("2026-11-01", StrongroomDay.SealState.BROKEN)
            ),
            storedEntries = listOf(
                StrongroomDay.DrawerEntry(1, "older task", 1_000L),
                StrongroomDay.DrawerEntry(2, "newer task", 2_000L)
            )
        )
        assertEquals(
            listOf("2026-11-02", "2026-11-01", "2026-10-30"),
            contents.platesNewestFirst.map { it.filingDateIso }
        )
        assertEquals(
            listOf("newer task", "older task"),
            contents.tasksNewestFirst.map { it.title }
        )
        assertFalse(contents.isEmpty)
        assertEquals(
            "2026-11-01",
            contents.plateFor("2026-11-01")?.filingDateIso
        )
    }

    @Test
    fun derive_snappedSealDaysRideInStackOrder() {
        val contents = DrawerContents.derive(
            struckRows = listOf(
                struckRow("2026-10-30", StrongroomDay.SealState.LIFTED, crank = true),
                struckRow("2026-11-02", StrongroomDay.SealState.LIFTED, crank = false),
                struckRow("2026-11-01", StrongroomDay.SealState.LIFTED, crank = true)
            ),
            storedEntries = emptyList()
        )
        assertEquals(listOf("2026-11-01", "2026-10-30"), contents.snappedSealDays())
    }

    @Test
    fun derive_emptyIsTheHonestEmpty() {
        assertTrue(DrawerContents.derive(emptyList(), emptyList()).isEmpty)
        assertTrue(DrawerContents.Empty.isEmpty)
    }

    // ── The handing-back gate (C-11) ────────────────────────────

    @Test
    fun gate_nobodyWaitingStandsOpenByTheSameAct() {
        assertTrue(
            HandingBackGate.platesRevealed(
                LiftingPlate.HandingBack(emptyList()), delivered = false
            )
        )
    }

    @Test
    fun gate_waitingUndeliveredKeepsThePlatesUnliftable() {
        val waiting = LiftingPlate.HandingBack.prepare(
            listOf(
                LiftingPlate.WaitingPerson("+91 00000 00000", "held:1"),
                LiftingPlate.WaitingPerson("+91 00000 00000", "held:1")
            )
        )
        // Preparation dedupes: one person, handed back once.
        assertEquals(1, waiting.waiting.size)
        assertFalse(HandingBackGate.platesRevealed(waiting, delivered = false))
        assertTrue(HandingBackGate.platesRevealed(waiting, delivered = true))
    }

    // ── The slide bodies ────────────────────────────────────────

    @Test
    fun slide_startsShutAndSettlesDrawn() {
        assertEquals(0.0, DrawerSlide.travelAt(0.0, 1.0, 0), 0.0)
        val mid = DrawerSlide.travelAt(0.0, 1.0, 300)
        assertTrue("the drawer travels", mid > 0.05 && mid < 1.05)
        val settled = DrawerSlide.travelAt(0.0, 1.0, 1600)
        assertEquals(1.0, settled, 0.01)
    }

    @Test
    fun slide_detentsCatchAtBothEndsOfTheRun() {
        assertEquals(1.0, DrawerSlide.settleTarget(0.9, 0.5), 0.0)
        assertEquals(0.0, DrawerSlide.settleTarget(0.1, -0.5), 0.0)
        assertEquals(0.0, DrawerSlide.settleTarget(0.0, 0.0), 0.0)
        assertEquals(1.0, DrawerSlide.settleTarget(1.0, 0.0), 0.0)
    }

    @Test
    fun plateRise_settlesIntoTheHandsView() {
        assertEquals(0.0, DrawerSlide.plateRiseAt(0.0, 1.0, 0), 0.0)
        assertEquals(1.0, DrawerSlide.plateRiseAt(0.0, 1.0, 1200), 0.01)
    }

    // ── The pile's geometry ─────────────────────────────────────

    @Test
    fun pile_growsOneBandPerBuriedPlate() {
        assertEquals(0f, PileGeometry.pileHeightDp(0), 0f)
        assertEquals(PileGeometry.PLATE_HEIGHT_DP, PileGeometry.pileHeightDp(1), 0f)
        assertEquals(
            PileGeometry.PLATE_HEIGHT_DP + 4 * PileGeometry.BAND_DP,
            PileGeometry.pileHeightDp(5), 0f
        )
        // The growth IS the display: more season, taller pile.
        assertTrue(PileGeometry.pileHeightDp(9) > PileGeometry.pileHeightDp(3))
    }

    // ── The texture law ─────────────────────────────────────────

    @Test
    fun texture_grainIsDeterministicAndWoodStreaksRunLong() {
        val a = DrawerTexture.grainAt(DrawerTexture.Species.WOOD, 7, 3, seed = 41)
        val b = DrawerTexture.grainAt(DrawerTexture.Species.WOOD, 7, 3, seed = 41)
        assertEquals(a, b)
        // Along the run, cells inside one stretched grain step
        // share their figure — the streak lies long (6:1).
        val streak = DrawerTexture.grainAt(DrawerTexture.Species.WOOD, 12, 5, seed = 41)
        for (x in 13..17) {
            assertEquals(
                streak,
                DrawerTexture.grainAt(DrawerTexture.Species.WOOD, x, 5, seed = 41)
            )
        }
        // Across the run it changes: down one cell is new wood.
        val varied = (0..12).map {
            DrawerTexture.grainAt(DrawerTexture.Species.WOOD, 12, it, seed = 41)
        }.toSet()
        assertTrue(varied.size > 4)
    }

    @Test
    fun texture_everyToneStaysInsideTheSevenInksNeverGreen() {
        val inks = listOf(
            StrongroomInks.Gold, StrongroomInks.Graphite, StrongroomInks.Violet
        )
        for (species in DrawerTexture.Species.entries) {
            for (ink in inks) {
                for (x in 0..12) for (y in 0..12) {
                    val tone = DrawerTexture.cellTone(
                        ink, StrongroomInks.Violet, species, x, y,
                        seed = 43, state = StrongroomLightState.SIGNING
                    )
                    assertTrue(
                        "no eighth colour, never green",
                        tone.green < maxOf(tone.red, tone.blue)
                    )
                }
            }
        }
    }
}
