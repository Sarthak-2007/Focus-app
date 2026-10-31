package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// MONOLITH 19 SEMANTIC COLOR TOKENS — DEVELOPER-GUIDE.pdf §1 & §1.1
// Strictly Zero-Green: Every single color satisfies green < maxOf(red, blue)
// =========================================================================

// Light Mode 19 Tokens (Warm Stone Ground #E4DFDA + Pure Crisp Raised #FAF6F0 + Single Accent #F55314)
val TokenColorBackground = Color(0xFFE4DFDA)          // 1. Window/screen ground (R:228, G:223, B:218)
val TokenColorSurface = Color(0xFFE4DFDA)             // 2. Default surface — same as ground, flat by default
val TokenColorSurfaceSheet = Color(0xFFE4DFDA)        // 3. Bottom sheets
val TokenColorSurfaceVariant = Color(0xFFD8D1CB)      // 4. Recessed/inset areas, preset tiles (R:216, G:209, B:203)
val TokenColorSurfaceBright = Color(0xFFFAF6F0)       // 5. The ONE raised card (timer card, nav capsule) (R:250, G:246, B:240)
val TokenColorPrimary = Color(0xFF252323)             // 6. Ink. Primary buttons & controls are near-black (R:37, G:35, B:35)
val TokenColorOnPrimary = Color(0xFFFBF1EC)           // 7. Content on ink (R:251, G:241, B:236)
val TokenColorPrimaryContainer = Color(0xFFF55314)    // 8. THE single accent — hot orange, spent rarely (R:245, G:83, B:20)
val TokenColorOnPrimaryContainer = Color(0xFF471602)  // 9. Content on the accent (R:71, G:22, B:2)
val TokenColorSecondaryContainer = Color(0xFF615B5A)  // 10. Selected pill in the bottom nav (R:97, G:91, B:90)
val TokenColorOnSecondaryContainer = Color(0xFFF4EFEE)// 11. Content on the pill (R:244, G:239, B:238)
val TokenColorOnSurface = Color(0xFF4E4948)           // 12. Primary icon/glyph tone on surfaces (R:78, G:73, B:72)
val TokenColorOnSurfaceVariant = Color(0xFF5F5856)    // 13. Secondary text/glyphs (R:95, G:88, B:86) — corrected Oct 30 (issue #63): the old comment read 120/113/112, which is #787170, not this value
val TokenColorOutline = Color(0xFF615B5A)             // 14. Strong hairlines, borders (R:97, G:91, B:90)
val TokenColorOutlineVariant = Color(0x75615B5A)      // 15. Hairlines (base colour at 0x75 alpha)
val TokenBlockBarSurface = Color(0xFF2E2A26)          // 16. The session/block bar — darkest element (R:46, G:42, B:38)
val TokenTextColorPrimary = Color(0xFF2C2A29)         // 17. Body text (R:44, G:42, B:41)
val TokenTextColorSecondary = Color(0xFF615B5A)       // 18. Secondary text (R:97, G:91, B:90)
val TokenAlert = Color(0xFFD63E06)                    // 19. Alert tone — deepest step of the ONE orange hue (R:214, G:62, B:6). Was Nothing Red; killed in Ren's revamp.

// Night / Armed Lockdown Mode 19 Tokens (DEVELOPER-GUIDE.pdf §1 Night table)
val NightColorBackground = Color(0xFF171412)          // 1. Night ground — Round 1a staircase base (R:23, G:20, B:18)
val NightColorSurface = Color(0xFF171412)             // 2. Night default surface — same as ground, flat by default
val NightColorSurfaceSheet = Color(0xFF4A423C)        // 3. Night sheets — the control-fill cap tone (1.86:1 over ground); topmost hardware
val NightColorSurfaceVariant = Color(0xFF100E0C)      // 4. Night recessed/inset — BELOW ground (was #272320, lighter than ground: recesses reversed)
val NightColorSurfaceBright = Color(0xFF362C27)       // 5. Night raised card — staircase step 1, 1.35:1 over ground
val NightColorPrimary = Color(0xFFFFE4CF)             // 6. Flips to cream — ink inverts, doesn't grey (R:255, G:228, B:207)
val NightColorOnPrimary = Color(0xFF1B110D)           // 7. Content on cream primary (R:27, G:17, B:13)
val NightColorPrimaryContainer = Color(0xFFF47442)    // 8. Accent warms up one step for dark ground (R:244, G:116, B:66)
val NightColorOnPrimaryContainer = Color(0xFF471602)  // 9. Content on night accent (R:71, G:22, B:2)
val NightColorSecondaryContainer = Color(0xFF4A423C)  // 10. Selected pill fill — the cap (was peach #FFB38F, a second floodlight at night)
val NightColorOnSecondaryContainer = Color(0xFFFFE4CF)// 11. Content on the cap — cream text, 8.1:1
val NightColorOnSurface = Color(0xFFEDE3D8)           // 12. Night primary glyph tone — 14.5:1 over ground
val NightColorOnSurfaceVariant = Color(0xFF9C8E84)    // 13. Night muted tier — 5.8:1 (Issue #70 F5: was #776A60 at 3.5:1, below WCAG AA 4.5:1; now the palette's own proven secondary tier — same warm grey family, no new hue)
val NightColorOutline = Color(0xFF807A7A)             // 14. Night strong hairlines (R:128, G:122, B:122)
val NightColorOutlineVariant = Color(0x758F8888)      // 15. Night hairlines at 0x75 alpha (R:143, G:136, B:136)
val NightBlockBarSurface = Color(0xFF161311)          // 16. Night block bar surface (R:22, G:19, B:17)
val NightTextColorPrimary = Color(0xFFD2C6BB)         // 17. Night body text — 11:1 tier (was ~16.8:1, everything shouted)
val NightTextColorSecondary = Color(0xFF9C8E84)       // 18. Night secondary text — 5.8:1 tier
val NightAlert = Color(0xFFFF8A5C)                    // 19. Night alert tone — the same orange hue, warmed for dark ground (R:255, G:138, B:92)

val DaylightZeroGreenTokens: List<Color> = listOf(
    TokenColorBackground,
    TokenColorSurface,
    TokenColorSurfaceSheet,
    TokenColorSurfaceVariant,
    TokenColorSurfaceBright,
    TokenColorPrimary,
    TokenColorOnPrimary,
    TokenColorPrimaryContainer,
    TokenColorOnPrimaryContainer,
    TokenColorSecondaryContainer,
    TokenColorOnSecondaryContainer,
    TokenColorOnSurface,
    TokenColorOnSurfaceVariant,
    TokenColorOutline,
    TokenColorOutlineVariant,
    TokenBlockBarSurface,
    TokenTextColorPrimary,
    TokenTextColorSecondary,
    TokenAlert
)

val NightZeroGreenTokens: List<Color> = listOf(
    NightColorBackground,
    NightColorSurface,
    NightColorSurfaceSheet,
    NightColorSurfaceVariant,
    NightColorSurfaceBright,
    NightColorPrimary,
    NightColorOnPrimary,
    NightColorPrimaryContainer,
    NightColorOnPrimaryContainer,
    NightColorSecondaryContainer,
    NightColorOnSecondaryContainer,
    NightColorOnSurface,
    NightColorOnSurfaceVariant,
    NightColorOutline,
    NightColorOutlineVariant,
    NightBlockBarSurface,
    NightTextColorPrimary,
    NightTextColorSecondary,
    NightAlert
)

// =========================================================================
// COMPONENT TONES (issue #63): colours a single component genuinely needs,
// tokenised here — the ONLY home for hex outside the 19+19 — instead of
// raw literals scattered across screens. Names say what the colour IS.
// =========================================================================

/** True black: OLED-off AOD ground + the scrim base both schemes share.
 *  Achromatic zero (0,0,0) — kept out of the 19+19 census lists, whose
 *  strict zero-green rule is about chromatic tokens. */
val TokenTrueBlack = Color(0xFF000000)

/** Sequencer pad-edge staircase, night (steps away from the night ground). */
val SequencerEdgeNight1 = Color(0xFF4E4847)
val SequencerEdgeNight2 = Color(0xFF3B3735)
val SequencerEdgeNight3 = Color(0xFF2D2927)

/** Sequencer pad-edge staircase, day (steps down the warm greys). */
val SequencerEdgeDay1 = Color(0xFFA8A39F)
val SequencerEdgeDay2 = Color(0xFFBDB7B4)
val SequencerEdgeDay3 = Color(0xFFCCC7C3)

/** Sequencer hairline base at night (the NightColorOutlineVariant base hue). */
val SequencerHairlineNight = Color(0xFF8F8888)

// NOTE (issue #63): the 97-name alias block that used to live here is
// DELETED. 72 of its names had zero call sites; the rest were exercised
// only by one Robolectric census test. Worse, names lied about colour
// (StarkWhite was near-black #252323, BauhausYellow/SolarYellow/
// BauhausCanary were orange #F55314, ElectricViolet/BauhausBlue were
// grey #615B5A, GraphiteSubInk was a NIGHT token aliased for general
// use). Call sites now name the real token they mean. The zero-green
// census test iterates DaylightZeroGreenTokens + NightZeroGreenTokens.
