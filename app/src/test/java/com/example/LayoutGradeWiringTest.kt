package com.example

import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.strongroom.grade.GradeState
import com.example.ui.strongroom.grade.PlateGradeRender
import com.example.ui.strongroom.grade.asGradeState
import com.example.ui.strongroom.grade.asHoleKind
import com.example.ui.strongroom.layout.SigningSlot
import com.example.ui.strongroom.layout.TrayDrawerCompositionLaw
import com.example.ui.theme.RelightState
import com.example.ui.theme.punchDiameter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * STAGE 8 + 10 COMPOSITION WIRING (BUILD-SR-16): the seams this
 * composition struck, pinned by name — the plate's corrected
 * ladder consumed through PlateTokens, the state mapping the
 * sealed face's grade wire runs on, the tray's drawn-edge cap as
 * the composition law (not the layout file's twin constant), and
 * the spent-day slot's conditionality the signing surface gates
 * on. Tests postponed to end-phase (R6); compile is the proof.
 */
class LayoutGradeWiringTest {

    // DESIGNER-SR-4's ladder, consumed: 17.6 / 12.6 / 9.5 dp —
    // Slice 1's 20/13/8 dp are dead at this seam.
    @Test
    fun plateLadder_isDesignerSr4sFigures_notSlice1s() {
        assertEquals(17.64.dp, punchDiameter(LiftingPlate.PunchGrade.LARGE))
        assertEquals(12.6.dp, punchDiameter(LiftingPlate.PunchGrade.MEDIUM))
        assertEquals(9.45.dp, punchDiameter(LiftingPlate.PunchGrade.SMALL))
    }

    @Test
    fun punchGradeMapping_isReachHeldFloor() {
        assertEquals(
            PlateGradeRender.HoleKind.REACH,
            LiftingPlate.PunchGrade.LARGE.asHoleKind()
        )
        assertEquals(
            PlateGradeRender.HoleKind.HELD,
            LiftingPlate.PunchGrade.MEDIUM.asHoleKind()
        )
        assertEquals(
            PlateGradeRender.HoleKind.FLOOR,
            LiftingPlate.PunchGrade.SMALL.asHoleKind()
        )
    }

    // The grade wire's state mapping is 1:1 — P0's table and the
    // pipeline's mirror carry the same four states.
    @Test
    fun relightState_mapsOneToOneOntoGradeState() {
        assertEquals(GradeState.SIGNING, RelightState.SIGNING.asGradeState())
        assertEquals(GradeState.SEALED, RelightState.SEALED.asGradeState())
        assertEquals(GradeState.DARK, RelightState.DARK.asGradeState())
        assertEquals(GradeState.DAY, RelightState.DAY.asGradeState())
    }

    // The tray stack's cap, as the pile screen consumes it: a
    // picture cap at 24 — drawn height stays, never a count.
    @Test
    fun trayDrawnEdges_capAtTwentyFour() {
        assertEquals(3, TrayDrawerCompositionLaw.drawnEdges(3))
        assertEquals(24, TrayDrawerCompositionLaw.drawnEdges(24))
        assertEquals(24, TrayDrawerCompositionLaw.drawnEdges(90))
        assertEquals(
            TrayDrawerCompositionLaw.MAX_DRAWN_STACK_EDGES,
            PlateGradeRender.MAX_DRAWN_EDGES
        )
    }

    // The signing surface's spent-day gate: the slot stands only
    // on a spent day; every other slot stands on any day.
    @Test
    fun spentDayTellingSlot_standsOnlyOnASpentDay() {
        assertTrue(SigningSlot.stands(SigningSlot.SPENT_DAY_TELLING, spentDay = true))
        assertFalse(SigningSlot.stands(SigningSlot.SPENT_DAY_TELLING, spentDay = false))
        assertTrue(SigningSlot.stands(SigningSlot.COMPOSITION_AS_RECEIVED, spentDay = false))
        assertTrue(SigningSlot.stands(SigningSlot.DOOR_TELLING, spentDay = false))
    }
}
