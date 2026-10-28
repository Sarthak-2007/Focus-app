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
val TokenColorOnSurfaceVariant = Color(0xFF787170)    // 13. Secondary text/glyphs (R:120, G:113, B:112)
val TokenColorOutline = Color(0xFF615B5A)             // 14. Strong hairlines, borders (R:97, G:91, B:90)
val TokenColorOutlineVariant = Color(0x75615B5A)      // 15. Hairlines (base colour at 0x75 alpha)
val TokenBlockBarSurface = Color(0xFF2E2A26)          // 16. The session/block bar — darkest element (R:46, G:42, B:38)
val TokenTextColorPrimary = Color(0xFF2C2A29)         // 17. Body text (R:44, G:42, B:41)
val TokenTextColorSecondary = Color(0xFF615B5A)       // 18. Secondary text (R:97, G:91, B:90)
val TokenNothingRed = Color(0xFFE30613)               // 19. Brand/dot-matrix accents only (R:227, G:6, B:19)

// Night / Armed Lockdown Mode 19 Tokens (DEVELOPER-GUIDE.pdf §1 Night table)
val NightColorBackground = Color(0xFF201D1A)          // 1. Night ground (R:32, G:29, B:26)
val NightColorSurface = Color(0xFF201D1A)             // 2. Night default surface
val NightColorSurfaceSheet = Color(0xFF2E2A26)        // 3. Night bottom sheets (R:46, G:42, B:38)
val NightColorSurfaceVariant = Color(0xFF272320)      // 4. Night recessed/inset areas (R:39, G:35, B:32)
val NightColorSurfaceBright = Color(0xFF332A27)       // 5. Night raised card (R:51, G:42, B:39)
val NightColorPrimary = Color(0xFFFFE4CF)             // 6. Flips to cream — ink inverts, doesn't grey (R:255, G:228, B:207)
val NightColorOnPrimary = Color(0xFF1B110D)           // 7. Content on cream primary (R:27, G:17, B:13)
val NightColorPrimaryContainer = Color(0xFFF47442)    // 8. Accent warms up one step for dark ground (R:244, G:116, B:66)
val NightColorOnPrimaryContainer = Color(0xFF471602)  // 9. Content on night accent (R:71, G:22, B:2)
val NightColorSecondaryContainer = Color(0xFFFFB38F)  // 10. Selected pill in night bottom nav (R:255, G:179, B:143)
val NightColorOnSecondaryContainer = Color(0xFF40352E)// 11. Content on night pill (R:64, G:53, B:46)
val NightColorOnSurface = Color(0xFFFBECE1)           // 12. Night primary icon/glyph tone (R:251, G:236, B:225)
val NightColorOnSurfaceVariant = Color(0xFFFAE4D3)    // 13. Night secondary text/glyphs (R:250, G:228, B:211)
val NightColorOutline = Color(0xFF807A7A)             // 14. Night strong hairlines (R:128, G:122, B:122)
val NightColorOutlineVariant = Color(0x758F8888)      // 15. Night hairlines at 0x75 alpha (R:143, G:136, B:136)
val NightBlockBarSurface = Color(0xFF161311)          // 16. Night block bar surface (R:22, G:19, B:17)
val NightTextColorPrimary = Color(0xFFF8F5F5)         // 17. Night body text (R:248, G:245, B:245)
val NightTextColorSecondary = Color(0xFFF1E8E6)       // 18. Night secondary text (R:241, G:232, B:230)
val NightNothingRed = Color(0xFFFF2A3C)               // 19. Night dot-matrix red (R:255, G:42, B:60)

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
    TokenNothingRed
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
    NightNothingRed
)

// =========================================================================
// SEMANTIC ALIASES (Strictly mapped to the 19 Tokens)
// =========================================================================

val PorcelainCanvas = TokenColorBackground
val SunlitPorcelain = TokenColorSurfaceBright
val RecessedPorcelain = TokenColorSurfaceVariant
val PorcelainKeycapSkirt = TokenColorSurfaceVariant
val PorcelainInnerBorder = TokenColorOutline

val ObsidianHeader = TokenBlockBarSurface
val ObsidianAnodizedTop = NightColorSurfaceSheet
val ObsidianElevated = NightColorSurfaceBright
val ObsidianKeycapSkirt = NightBlockBarSurface
val EspressoInk = TokenColorPrimary
val MutedWarmGray = TokenColorOnSurfaceVariant
val SilkWhite = TokenColorOnPrimary

val BurntVermilion = TokenColorPrimaryContainer
val VermilionSkirt = TokenColorOnPrimaryContainer
val VermilionHighlight = NightColorPrimaryContainer
val SolarAmber = TokenColorPrimaryContainer
val AmberSkirt = TokenColorOnPrimaryContainer
val ElectricIris = TokenColorSecondaryContainer
val IrisSkirt = TokenBlockBarSurface
val SoftPeachWash = TokenColorSurfaceVariant
val SoftPeriwinkle = TokenColorSurfaceVariant

// Compatibility aliases for unit/Robolectric tests
val StoneChassisCanvas = TokenColorBackground
val PureWhiteDisplay = TokenColorSurfaceBright
val RecessedBentoTile = TokenColorSurfaceVariant
val MediumStoneTile = TokenColorSurfaceVariant
val SoftStoneDivider = TokenColorOutline

val ActiveCharcoalTile = NightColorSurfaceBright
val BlockMatrixTaupe = TokenColorPrimary
val MutedTaupeCaption = TokenColorOnSurfaceVariant
val GhostMatrixSegment = TokenColorSurfaceVariant

val InternationalOrange = TokenColorPrimaryContainer
val ApricotPeachPill = TokenColorPrimaryContainer
val CobaltIndigoAccent = TokenColorSecondaryContainer

val SunlitAlabaster = TokenColorBackground
val WarmIvoryPaper = TokenColorSurfaceBright
val SandblastedStone = TokenColorSurfaceVariant
val PressedPuttyWell = TokenColorSurfaceVariant
val WarmHairlineBorder = TokenColorOutline

val WarmCharcoal = NightColorSurfaceBright
val MutedClay = TokenColorOnSurfaceVariant

val SoftTerracotta = TokenColorPrimaryContainer
val DeepCobalt = TokenColorSecondaryContainer
val WarmOchre = TokenColorPrimaryContainer

val PaperCream = TokenColorBackground
val CardStockWhite = TokenColorSurfaceBright
val WarmSandWell = TokenColorSurfaceVariant
val VellumTagGray = TokenColorSurfaceVariant
val CharcoalInk = TokenColorPrimary
val GraphiteSubInk = NightColorSurfaceBright
val MutedPencil = TokenColorOnSurfaceVariant
val BauhausRed = TokenColorPrimaryContainer
val BauhausBlue = TokenColorSecondaryContainer
val BauhausYellow = TokenColorPrimaryContainer
val VioletAccent = TokenColorSecondaryContainer

val Obsidian = TokenBlockBarSurface
val BasaltSurface = TokenColorSurfaceBright
val ElevatedBasalt = TokenColorSurfaceVariant
val BrushedSteel = TokenColorSurfaceVariant
val HairlineBorder = TokenColorOutline
val StarkWhite = TokenColorPrimary
val TitaniumGray = NightColorSurfaceBright
val MutedSlate = TokenColorOnSurfaceVariant
val SolarOrange = TokenColorPrimaryContainer
val WarmAmber = TokenColorPrimaryContainer
val ElectricViolet = TokenColorSecondaryContainer
val DeepIndigo = TokenBlockBarSurface
val CrimsonAlert = TokenNothingRed

val Basalt = TokenColorSurfaceBright
val BasaltBottomShadow = TokenColorOutline
val BasaltTopHighlight = TokenColorOnPrimary
val BlazeAmber = TokenColorPrimaryContainer
val DimSlate = TokenColorOnSurfaceVariant
val ElectricCrimson = TokenColorPrimaryContainer
val RecessedWell = TokenColorSurfaceVariant
val SolarYellow = TokenColorPrimaryContainer

val SunlitPaper = TokenColorBackground
val RecessedLinen = TokenColorSurfaceVariant
val RaisedCreamKey = TokenColorSurfaceBright
val PressedCreamKey = TokenColorSurfaceVariant
val PaperShadow = TokenColorOutline
val PaperDivider = TokenColorOutline
val WarmIndigoDusk = NightColorSurfaceBright
val RecessedIndigo = TokenBlockBarSurface
val RaisedIndigoKey = NightColorSurfaceBright
val PressedIndigoKey = TokenBlockBarSurface
val IndigoBorder = TokenColorOnSurfaceVariant
val BauhausCrimson = TokenColorPrimaryContainer
val BauhausUltramarine = TokenColorSecondaryContainer
val BauhausCanary = TokenColorPrimaryContainer
val BauhausTerracotta = TokenColorPrimaryContainer
val SoftCoralWash = TokenColorSurfaceVariant
val WarmAmberWash = TokenColorSurfaceVariant
val UltramarineWash = TokenColorSurfaceVariant
val SunlitApricot = TokenColorPrimaryContainer
val SandstoneInset = TokenColorSurfaceVariant
val ObsidianSlate = TokenBlockBarSurface
val CarbonShadow = NightColorSurfaceBright
val IrisVioletWash = TokenColorSurfaceVariant
