package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LiftingPlate
import com.example.ui.strongroom.grade.asComposeColor
import com.example.ui.strongroom.grade.asHoleKind

/**
 * PLATE TOKENS — BUILD-SR-4's lane-local token file for the plate
 * lane (P6), under the same seam BUILD-SR-2 filed for the faces
 * lane: P0 FOUNDATION was not on main when this lane started, so
 * these tokens are derived firsthand from the filed law — Audit A
 * §4 (the seven inks, the kept violet/gold chord, the dark
 * line-work) and Stage 5 (the PLT scale for the plate's print) —
 * and shaped to P0's filed contract. When P0 (and BUILD-SR-2's
 * StrongroomTokens) land, this file is re-pointed at theirs in
 * one act and any difference is named upward; no surface in this
 * lane hard-codes a colour, a size, or a weight outside this file.
 *
 * PALETTE LAW: the seven inks are the whole palette —
 * #F04FCB · #493EA4 · #E2BC39 · #C84481 · #E47E35 · #377ED0 ·
 * #34323B — never green. Every other tone a plate surface shows
 * is one of these inks lerped toward another of them — a
 * derivation along the palette, never a new hex.
 */

// ---------------------------------------------------------------------
// THE SEVEN INKS (Audit A §4 — the closed palette; same values as
// BUILD-SR-2's StrongroomTokens, by the one palette law)
// ---------------------------------------------------------------------

val PlateInkPink = Color(0xFFF04FCB)
val PlateInkViolet = Color(0xFF493EA4)
val PlateInkGold = Color(0xFFE2BC39)
val PlateInkRose = Color(0xFFC84481)
val PlateInkEmber = Color(0xFFE47E35)
val PlateInkBlue = Color(0xFF377ED0)
val PlateInkLine = Color(0xFF34323B)

val PlateInks: List<Color> = listOf(
    PlateInkPink, PlateInkViolet, PlateInkGold, PlateInkRose,
    PlateInkEmber, PlateInkBlue, PlateInkLine
)

/** The plate lane's token contract as one named object — the
 *  census a sweep reads: the palette whole, and nothing else. */
object PlateTokens {
    val inks: List<Color> = PlateInks
    fun punchDiameter(grade: LiftingPlate.PunchGrade): Dp =
        com.example.ui.theme.punchDiameter(grade)
}

// ---------------------------------------------------------------------
// THE BRASS PLATE (E-9: the brass day plate KEPT by his eye)
// ---------------------------------------------------------------------

/** The plate's brass field — DESIGNER-SR-4's ladder (BR-06),
 *  consumed from BUILD-SR-10's grade pipeline at Stage 10
 *  composition (BUILD-SR-16): gold toward the line ink at 0.18,
 *  struck as data in [com.example.ui.strongroom.grade.PlateGradeRender]
 *  and bridged here. Brass renders in the gold ink ALONE — every
 *  tone is a descent toward the line ink, never a second hue. */
val PlateBrass =
    com.example.ui.strongroom.grade.PlateGradeRender.BRASS_FIELD
        .asComposeColor()

/** The brass descended — a fallen day's plate. DESIGNER-SR-4 §4's
 *  cap stands: the field descends to 0.34 and no further (§MB-BRASS
 *  L-DARK: the plate stays the frame's lightest object, its bracket
 *  pair at full contrast). Slice 1's flat 0.52 descent is RETIRED —
 *  it buried the bracket pair it had to be read against. */
val PlateBrassDark =
    com.example.ui.strongroom.grade.PlateGradeRender.BRASS_FAILURE_FIELD
        .asComposeColor()

/** The plate's engraved line-work: the dark ink, as kept (E-8). */
val PlateLineWork = PlateInkLine

/** A punched hole's depth: the line ink at full depth. */
val PlateHoleDepth = PlateInkLine

/** The door the plate hangs on: the kept violet ground. */
val PlateDoorGround = PlateInkViolet

/** The dark room of a fallen day: the line ink, whole. */
val PlateDarkRoom = PlateInkLine

// ---------------------------------------------------------------------
// GRADED HOLE FIGURES — DESIGNER-SR-4's corrected ladder, consumed
// from the grade pipeline at Stage 10 composition (BUILD-SR-16).
// Figures (§MB-NUMBERS N-4, DESIGN values, UNPROVEN G-4): REACH
// Ø 2.8 mm · HELD Ø 2.0 mm · FLOOR Ø 1.5 mm at Stage 5's conversion
// 1 mm ≈ 6.3 dp → 17.6 / 12.6 / 9.5 dp. The ratios 1.87 : 1.33 : 1
// ARE the grading (the consistency law: no kind re-scales alone).
// Slice 1's 20/13/8 dp are RETIRED — their wider steps (1.54 · 1.63,
// floor at 0.40 of reach against the figure's 0.54) taught a
// different grading than the sittings settled.
// ---------------------------------------------------------------------

fun punchDiameter(grade: LiftingPlate.PunchGrade): Dp =
    com.example.ui.strongroom.grade.PlateGradeRender
        .punchDiameterDp(grade.asHoleKind()).dp

// ---------------------------------------------------------------------
// THE PLATE'S PRINT — Stage 5's PLT scale, carried to screen at
// 1pt = 1sp as the faces lane carries the face's steps
// ---------------------------------------------------------------------

/** PLT-1 — the plate's heading print: Anonymous Pro 700 (the
 *  telemetry hand — struck figures are the plate's speech). */
val PlateHeadingStyle = TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.4.sp
)

/** PLT-2 — the plate's body print: Anonymous Pro 400. */
val PlateBodyStyle = TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.3.sp
)

/** The room-side lists (pile, drawer): Atkinson Hyperlegible 400 —
 *  the interface hand, for surfaces he reads rather than feels. */
val RoomListStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 22.sp
)

/** Room-side headings: Sora 600, the display hand at rest. */
val RoomHeadingStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp
)
