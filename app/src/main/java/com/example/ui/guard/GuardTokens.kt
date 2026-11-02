package com.example.ui.guard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.SoraFamily

/**
 * GUARD TOKENS — BUILD-SR-5's lane-local token file for P7, under
 * the seam named in this lane's opening line: P0 FOUNDATION
 * (BUILD-SR-1) was not on main when this lane started, so these
 * tokens are derived firsthand from the filed law — Audit A §4
 * (the seven inks, the kept violet/gold chord) and Stage 5 (the
 * type scales) — and deliberately placed in THIS package under
 * Guard-prefixed names, not at the faces lane's
 * `ui/theme/StrongroomTokens.kt` path, so the composition merge
 * carries no file collision. Re-pointing at P0's tokens at
 * composition is a one-file act; no Guard surface hard-codes a
 * colour, size, or weight outside this file.
 *
 * PALETTE LAW: the seven inks are the whole palette —
 * #F04FCB · #493EA4 · #E2BC39 · #C84481 · #E47E35 · #377ED0 ·
 * #34323B — never green. Every other tone shown is one of these
 * inks at an alpha, a derivation, never a new hex.
 */

val GuardInkPink = Color(0xFFF04FCB)
val GuardInkViolet = Color(0xFF493EA4)
val GuardInkGold = Color(0xFFE2BC39)
val GuardInkRose = Color(0xFFC84481)
val GuardInkEmber = Color(0xFFE47E35)
val GuardInkBlue = Color(0xFF377ED0)
val GuardInkLine = Color(0xFF34323B)

/** The palette, whole — the census the Guard token test reads. */
val GuardInks: List<Color> = listOf(
    GuardInkPink, GuardInkViolet, GuardInkGold, GuardInkRose,
    GuardInkEmber, GuardInkBlue, GuardInkLine
)

/** The door's ground: deep-violet enamelled steel. */
val GuardGround = GuardInkViolet

/** Gold leaf: headings, the pinstripe, the wheel's brass. */
val GuardLeaf = GuardInkGold

/** The printings' ink: dark line-work — spent on gold plates,
 *  where it reads as engraving, never as body text on violet. */
val GuardLineWork = GuardInkLine

/** The surface's title — Sora 700 at the SCR-5 class. */
val GuardTitleStyle = TextStyle(
    fontFamily = SoraFamily, fontWeight = FontWeight.Bold,
    fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.24).sp
)

/** Section heads — Sora 600 at the SCR-4 class. */
val GuardSectionStyle = TextStyle(
    fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.20).sp
)

/** Body law lines — Atkinson 400 at the SCR-2 class. */
val GuardBodyStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
    fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp
)

/** Secondary lines — Atkinson 400 at the SCR-1 class. */
val GuardSecondaryStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily, fontWeight = FontWeight.Normal,
    fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.28.sp
)

/** The standing word + figures — Anonymous Pro: the allowance
 *  figure at the ANON-3 class, the standing word at ANON-2. */
val GuardFigureStyle = TextStyle(
    fontFamily = AnonymousProFamily, fontWeight = FontWeight.Normal,
    fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp
)

val GuardStandingStyle = TextStyle(
    fontFamily = AnonymousProFamily, fontWeight = FontWeight.Bold,
    fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.34.sp
)

/** Plate labels (on / off / begin) — Sora 600, screen class. */
val GuardPlateLabelStyle = TextStyle(
    fontFamily = SoraFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.16.sp
)
