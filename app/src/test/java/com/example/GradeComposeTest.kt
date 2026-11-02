package com.example

import com.example.domain.LiftingPlate
import com.example.ui.strongroom.grade.GradeColour
import com.example.ui.strongroom.grade.GradeInk
import com.example.ui.strongroom.grade.GradeState
import com.example.ui.strongroom.grade.PlateGradeRender
import com.example.ui.strongroom.grade.asComposeColor
import com.example.ui.strongroom.grade.asGradeState
import com.example.ui.strongroom.grade.asHoleKind
import com.example.ui.theme.RelightState
import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The grade pipeline's Compose seam (GradeCompose.kt, BUILD-SR-16)
 * pinned by name: the bridges strike no tone of their own — a
 * pipeline colour crosses as the identical ARGB, the four states
 * map 1:1, and P6's punch grades land on DESIGNER-SR-4's hole
 * kinds. Tests postponed to end-phase (R6).
 */
class GradeComposeTest {

    @Test
    fun gradeColour_crossesAsTheIdenticalArgb() {
        for (ink in GradeInk.entries) {
            val colour = GradeColour.of(ink)
            assertEquals(
                colour.toArgb().toInt(),
                colour.asComposeColor().toArgb()
            )
        }
    }

    @Test
    fun relightState_mapsOneToOneOntoGradeState() {
        assertEquals(GradeState.SIGNING, RelightState.SIGNING.asGradeState())
        assertEquals(GradeState.SEALED, RelightState.SEALED.asGradeState())
        assertEquals(GradeState.DARK, RelightState.DARK.asGradeState())
        assertEquals(GradeState.DAY, RelightState.DAY.asGradeState())
    }

    @Test
    fun punchGrade_mapsOntoTheFiguredHoleKinds() {
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
}
