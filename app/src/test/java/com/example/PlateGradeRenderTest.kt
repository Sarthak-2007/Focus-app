package com.example

import com.example.ui.strongroom.grade.GradeColour
import com.example.ui.strongroom.grade.GradeInk
import com.example.ui.strongroom.grade.PlateGradeRender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** STAGE 10 PRE-BUILD (BUILD-SR-10): DESIGNER-SR-4's plate figures as
 *  executable law. */
class PlateGradeRenderTest {

    // The figures × Stage 5's conversion: 17.64 / 12.6 / 9.45 dp.
    @Test
    fun punchDiametersEqualTheFigures() {
        assertEquals(17.64f, PlateGradeRender.punchDiameterDp(PlateGradeRender.HoleKind.REACH), 0.01f)
        assertEquals(12.6f, PlateGradeRender.punchDiameterDp(PlateGradeRender.HoleKind.HELD), 0.01f)
        assertEquals(9.45f, PlateGradeRender.punchDiameterDp(PlateGradeRender.HoleKind.FLOOR), 0.01f)
    }

    // The grading IS the ratios: steps 1.40 and 1.33 (±0.01).
    @Test
    fun ratioStepsAreTheSettledSteps() {
        val reach = PlateGradeRender.ratioToFloor(PlateGradeRender.HoleKind.REACH)
        val held = PlateGradeRender.ratioToFloor(PlateGradeRender.HoleKind.HELD)
        assertEquals(1.40f, reach / held, 0.01f)
        assertEquals(1.33f, held / 1.0f, 0.01f)
    }

    // BR-06: every brass tone derives from gold toward graphite only —
    // each channel sits between the two inks, monotonically by lerp.
    @Test
    fun brassLadderDerivesFromGoldTowardLineOnly() {
        val gold = GradeColour.of(GradeInk.GOLD)
        val line = GradeColour.of(GradeInk.GRAPHITE)
        val ladder = listOf(
            PlateGradeRender.BRASS_RAISED, PlateGradeRender.BRASS_FIELD,
            PlateGradeRender.BRASS_FAILURE_FIELD, PlateGradeRender.BRASS_RECESS,
            PlateGradeRender.BRASS_DEEP,
        )
        ladder.forEach { tone ->
            assertTrue(tone.r in line.r..gold.r)
            assertTrue(tone.g in line.g..gold.g)
            assertTrue(tone.b in gold.b..line.b || tone.b in line.b..gold.b)
        }
        // Descent is monotonic: raised is nearest gold, deep nearest line.
        assertTrue(PlateGradeRender.BRASS_RAISED.r > PlateGradeRender.BRASS_FIELD.r)
        assertTrue(PlateGradeRender.BRASS_FIELD.r > PlateGradeRender.BRASS_RECESS.r)
        assertTrue(PlateGradeRender.BRASS_RECESS.r > PlateGradeRender.BRASS_DEEP.r)
    }

    // The failure descent is capped at the filed 0.34.
    @Test
    fun failureFieldIsCapped() {
        assertEquals(0.34f, PlateGradeRender.FAILURE_FIELD_LERP, 0.001f)
        assertTrue(PlateGradeRender.BRASS_FAILURE_FIELD.r < PlateGradeRender.BRASS_FIELD.r)
        assertTrue(PlateGradeRender.BRASS_FAILURE_FIELD.r > PlateGradeRender.BRASS_DEEP.r)
    }

    // B-11/B-14: failure days have no strike moment; the seal's place
    // stands empty — never a crack.
    @Test
    fun failureDayHasNoStrikeAndAnEmptySealPlace() {
        assertFalse(PlateGradeRender.strikeMomentExists(failureDay = true))
        assertTrue(PlateGradeRender.strikeMomentExists(failureDay = false))
        assertEquals(
            PlateGradeRender.SealPlace.EMPTY_PLACE,
            PlateGradeRender.sealPlace(failureDay = true),
        )
        assertEquals(
            PlateGradeRender.SealPlace.STRUCK_RING,
            PlateGradeRender.sealPlace(failureDay = false),
        )
        assertEquals(3f, PlateGradeRender.STRIKE_SETTLE_DP, 0.001f)
        assertEquals(90, PlateGradeRender.STRIKE_SETTLE_MS)
    }

    // Stack offsets are a pure function of the filing date, in range.
    @Test
    fun stackOffsetsAreDeterministicAndInRange() {
        for (day in 19000L..19030L) {
            assertEquals(PlateGradeRender.stackOffsetX(day), PlateGradeRender.stackOffsetX(day))
            assertTrue(PlateGradeRender.stackOffsetX(day) in -3..3)
            assertTrue(PlateGradeRender.stackOffsetY(day) in -1..1)
            assertTrue(PlateGradeRender.stackRotationTenths(day) in -4..4)
        }
        assertEquals(24, PlateGradeRender.drawnEdges(100))
        assertEquals(3, PlateGradeRender.drawnEdges(3))
    }

    // Depth is not graded: the anatomy fractions are kind-independent
    // constants (one alphabet for the hand).
    @Test
    fun holeAnatomyFractionsAreConstant() {
        assertEquals(0.86f, PlateGradeRender.WALL_FRACTION, 0.001f)
        assertEquals(0.62f, PlateGradeRender.THROUGH_DARK_FRACTION, 0.001f)
        assertEquals(0.10f, PlateGradeRender.LIP_OFFSET_FRACTION, 0.001f)
    }
}
