package com.example.ui.strongroom.grade

import androidx.compose.ui.graphics.Color
import com.example.domain.LiftingPlate
import com.example.ui.theme.RelightState

/**
 * STAGE 10 COMPOSITION — THE GRADE PIPELINE'S COMPOSE SEAM
 * (BUILD-SR-16, 2026-11-02).
 *
 * The bridges by which the composed surfaces consume BUILD-SR-10's
 * grade law ([GradePipeline] / [PlateGradeRender]) without striking
 * a tone of their own: data colours become Compose colours here and
 * nowhere else, the faces lane's [RelightState] maps 1:1 onto
 * [GradeState] (the same four states, P0's table mirrored
 * value-for-value in the pipeline), and P6's [LiftingPlate.PunchGrade]
 * maps onto the grade's hole kinds — LARGE→REACH · MEDIUM→HELD ·
 * SMALL→FLOOR, the mapping DESIGNER-SR-4's ladder files (figures
 * 2.8 / 2.0 / 1.5 mm at 6.3 dp/mm = 17.6 / 12.6 / 9.5 dp; Slice 1's
 * 20/13/8 dp are retired by this seam).
 */

/** A pipeline colour as a Compose colour — the pipeline's ARGB,
 *  unchanged. */
fun GradeColour.asComposeColor(): Color = Color(toArgb())

/** The four states are one table in two spellings (P0's Relight and
 *  the pipeline's mirror of it); this is the whole mapping. */
fun RelightState.asGradeState(): GradeState = when (this) {
    RelightState.SIGNING -> GradeState.SIGNING
    RelightState.SEALED -> GradeState.SEALED
    RelightState.DARK -> GradeState.DARK
    RelightState.DAY -> GradeState.DAY
}

/** P6's punch grades at DESIGNER-SR-4's figures. */
fun LiftingPlate.PunchGrade.asHoleKind(): PlateGradeRender.HoleKind = when (this) {
    LiftingPlate.PunchGrade.LARGE -> PlateGradeRender.HoleKind.REACH
    LiftingPlate.PunchGrade.MEDIUM -> PlateGradeRender.HoleKind.HELD
    LiftingPlate.PunchGrade.SMALL -> PlateGradeRender.HoleKind.FLOOR
}

/** A surface's ground tone struck through the pipeline: the state's
 *  mass descent first (P0's stateSurface law), as data in, Compose
 *  colour out. */
fun gradeGround(base: GradeInk, state: RelightState, surface: GradeSurface): Color =
    GradePipeline.stateSurface(GradeColour.of(base), state.asGradeState(), surface)
        .asComposeColor()
