package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * STRONGROOM TOKENS — BUILD-SR-2's lane-local token file for the
 * faces lane (P3/P4/P5), filed under the seam named in this lane's
 * opening line (ascent/build-first-usable/work/build-sr-2/
 * opening-line.md, difference 2): P0 FOUNDATION (BUILD-SR-1,
 * branch agent/build-sr-foundation) was not up when this lane
 * started, so these tokens are derived firsthand from the filed
 * law — Audit A §4 (the seven inks, the kept ground & finish, the
 * four printings) and Stage 5 §1/§3 (the scale, the hierarchy) —
 * and shaped to P0's filed contract. When P0 lands, this file is
 * re-pointed at its tokens in one act and any difference is named
 * upward; no surface in this lane hard-codes a colour, a size, or
 * a weight outside this file.
 *
 * PALETTE LAW: the seven inks are the whole palette —
 * #F04FCB · #493EA4 · #E2BC39 · #C84481 · #E47E35 · #377ED0 ·
 * #34323B — never green. Every other tone a Strongroom surface
 * shows is one of these inks moved along its own luminance by
 * the RELIGHT rule ([ReliefLight]) — a derivation, never a new
 * hex.
 */

// ---------------------------------------------------------------------
// THE SEVEN INKS (Audit A §4 — the closed palette)
// ---------------------------------------------------------------------

val InkPink = Color(0xFFF04FCB)
val InkViolet = Color(0xFF493EA4)
val InkGold = Color(0xFFE2BC39)
val InkRose = Color(0xFFC84481)
val InkEmber = Color(0xFFE47E35)
val InkBlue = Color(0xFF377ED0)
val InkLine = Color(0xFF34323B)

/** The palette, whole — the census every Strongroom token test
 *  and later sweep reads. */
val StrongroomInks: List<Color> = listOf(
    InkPink, InkViolet, InkGold, InkRose, InkEmber, InkBlue, InkLine
)

// ---------------------------------------------------------------------
// THE KEPT GROUND & FINISH (Audit A §4; sitting walk record 2:
// L1 ground & finish KEPT · L2 dark line-work KEPT)
// ---------------------------------------------------------------------

/** The door's ground: deep-violet enamelled steel. */
val StrongroomGround = InkViolet

/** Gold leaf + hand-laid gold pinstripe. */
val StrongroomGold = InkGold

/** The four printings' ink: dark line-work. */
val StrongroomLineWork = InkLine

/** The antechamber lamp's warm note (the concept's own orange,
 *  in-palette as the ember ink) — P5's ground note, tokenised
 *  here so no later file reaches for a raw hex. */
val StrongroomLamp = InkEmber

// ---------------------------------------------------------------------
// RELIGHT BRIDGE — a Compose Color through the pure rule
// ---------------------------------------------------------------------

private fun Color.toSrgb(): Srgb = Srgb(red, green, blue)

private fun Srgb.toColor(): Color = Color(
    red = r.coerceIn(0f, 1f),
    green = g.coerceIn(0f, 1f),
    blue = b.coerceIn(0f, 1f)
)

/** This colour's raised edge under [state] (RELIGHT rule). */
fun Color.reliefHighlight(state: FaceLightState): Color =
    ReliefLight.highlight(toSrgb(), state).toColor()

/** This colour's cut edge under [state] (RELIGHT rule). */
fun Color.reliefShade(state: FaceLightState): Color =
    ReliefLight.shade(toSrgb(), state).toColor()

// ---------------------------------------------------------------------
// THE FACE'S TYPE — Stage 5 §1.2 printed steps, carried to screen
// at 1pt = 1sp (the face is composed once, at its printed scale;
// the fitting's true field width is Slice 4's in-app judgement).
// Tracking is set after-spread, never negative (§5.3): each
// letterSpacing below is its step's size × its filed em value.
// ---------------------------------------------------------------------

/** Rank 1 — the lifting hour's figures (PRT-5 / ENG-1 class):
 *  Anonymous Pro 700, 48pt, +0.02em. The face's first fact. */
val FaceHourStyle = TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 50.sp,
    letterSpacing = 0.96.sp // 48 × 0.02em
)

/** Rank 2 — the door-word slot (PRT-3): Sora 700, 24pt, +0.05em.
 *  The style stands ready for a word that does not exist yet
 *  (Audit A G-1); in Slice 1 the slot renders as its kept place
 *  and this style is spent by nothing but the slot's measure. */
val FaceDoorWordStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 1.2.sp // 24 × 0.05em
)

/** Rank 3 — his sentence, or its kept place (PRT-4): Sora 600,
 *  32pt, +0.04em, up to 3 lines — a longer sentence grows lines,
 *  never drops step. */
val FaceSentenceStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 1.28.sp // 32 × 0.04em
)

/** Rank 4 — the held-mark slot (PRT-2, the printed floor):
 *  Sora 600, 18pt, +0.06em. Testimony, never announcement. */
val FaceHeldMarkStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 25.sp,
    letterSpacing = 1.08.sp // 18 × 0.06em
)
