package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * 1. Sora — Display & Section Headers (Sarthak's standing pick for display type):
 *    Used for the Rotary Dial numerals, Bento metrics, Desk Clock, and tactile headers.
 */
// Real weights only (Round 0b fix): the old registration pointed all four
// weights at the single Regular file, so every "Bold" was synthesized smear.
// sora_medium/semibold/bold are true instances of the Sora variable font.
val SoraFamily = FontFamily(
    Font(R.font.sora, FontWeight.Normal),
    Font(R.font.sora_medium, FontWeight.Medium),
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold, FontWeight.Bold)
)

/**
 * 2. Atkinson Hyperlegible — Interface & Body (Sarthak's standing pick for UI text):
 *    Built for legibility; used for app rows, 3-stop switches, and explanatory copy.
 */
// Atkinson ships only Regular + Bold; Medium/SemiBold requests resolve
// to the nearest real weight instead of a fake one (Round 0b fix).
val AtkinsonHyperlegibleFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)

/**
 * 3. Anonymous Pro — Telemetry & Numbers (Sarthak's standing pick for mono):
 *    Used for timers, serial numbers, peek tags, and quota badges.
 */
// Anonymous Pro ships only Regular + Bold (Round 0b fix, as above).
val AnonymousProFamily = FontFamily(
    Font(R.font.anonymous_pro, FontWeight.Normal),
    Font(R.font.anonymous_pro_bold, FontWeight.Bold)
)


val TightNumericPlatformStyle = PlatformTextStyle(includeFontPadding = false)
val CenteredNumericLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

// Monolith type ladder (TYPE-RAMP-TABLE, Set's audit, Oct 29 2026):
// six sizes only - 104 / 72 / 50 / 22 / 15 / 10 - no two tiers within 1.4x.
// The old lying font aliases (SpaceGrotesk / PlusJakartaSans / JetBrainsMono
// / DmSans) are dead; call sites name the real families. BentoMetric (36sp,
// off-ladder, unused) is dead. The Atkinson "Medium" is dead - the family
// ships 400/700 only, so body text is honest 400.

/** H2 - the one big number in hand (running time, Deck total). */
val DialClockTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    lineHeight = 72.sp,
    letterSpacing = (-2.0).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

/** H1 - across-the-room time. AOD only. */
val DeskClockTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 104.sp,
    lineHeight = 100.sp,
    letterSpacing = (-3.0).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

/** L1 - the engraved plate: every micro-label, and nothing else. */
val PillBadgeTextStyle = TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 2.0.sp,
    platformStyle = TightNumericPlatformStyle
)

/** T1 - section titles. */
val DrawerHeaderTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.3).sp,
    platformStyle = TightNumericPlatformStyle
)

/** Button words - Sora 700, tracked +0.5. */
val PrimaryButtonTextStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp,
    platformStyle = TightNumericPlatformStyle
)

/** App names / row titles at B1 - Atkinson 700. */
val AppTitleTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp
)

/** B1 body - Atkinson 400 (the only honest body weight the family ships). */
val BodyTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp
)

/** The reception sheet's second voice (Fein §B/§C, FINISH-type-print):
 *  the door's job-adjacent lines — chit line, STALE gloss, prepare
 *  escort — in BodyTextStyle's values, worn in onSurfaceVariant at
 *  the call sites. Wave-2 fix (Pforte): ReceptionSheet used this
 *  name at four sites with no definition and no import anywhere in
 *  the tree — a compile-blocker caught by the Wave-2 finisher
 *  cluster, not by Wave-1's "print CLOSED" record. */
val DoorJobTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp
)

/** T1 in Atkinson - the ACTIVE task title. */
val TaskActiveTitleTextStyle = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 27.sp,
    platformStyle = TightNumericPlatformStyle
)

val Typography = Typography(
    displayLarge = DeskClockTextStyle,
    displayMedium = DialClockTextStyle,
    headlineLarge = DrawerHeaderTextStyle,
    headlineMedium = DrawerHeaderTextStyle,
    titleLarge = TaskActiveTitleTextStyle,
    titleMedium = PrimaryButtonTextStyle,
    bodyLarge = AppTitleTextStyle,
    bodyMedium = BodyTextStyle,
    bodySmall = BodyTextStyle,
    labelLarge = PillBadgeTextStyle,
    labelMedium = PillBadgeTextStyle,
    labelSmall = PillBadgeTextStyle
)
