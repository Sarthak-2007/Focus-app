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
 * 1. Space Grotesk — Engraved Horological Display & Section Headers:
 *    Used for the Rotary Sun Dial numerals, Bento metrics, Nightstand Clock, and tactile headers.
 */
val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Normal),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.SemiBold),
    Font(R.font.space_grotesk, FontWeight.Bold)
)

/**
 * 2. Plus Jakarta Sans — Humanist Studio UI & App Switchboard Copy:
 *    Warm, friendly, high-legibility proportions for app rows, 3-stop switches, and studio notes.
 */
val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

/**
 * 3. JetBrains Mono — Tabular Technical Stamps, Serial Numbers & Timers:
 *    Used for the Arrival Time Loupe, stamp denominations, 01:59 Peek Tag, and quota badges.
 */
val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Medium),
    Font(R.font.jetbrains_mono, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono, FontWeight.Bold)
)

// Compatibility aliases for existing references
val AnonymousProFamily = JetBrainsMonoFamily
val SoraFamily = SpaceGroteskFamily
val AtkinsonHyperlegibleFamily = PlusJakartaSansFamily
val DmSansFamily = PlusJakartaSansFamily

val TightNumericPlatformStyle = PlatformTextStyle(includeFontPadding = false)
val CenteredNumericLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

// Explicit Sundial Studio TextStyles
val DialClockTextStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 58.sp,
    lineHeight = 56.sp,
    letterSpacing = (-2.0).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

val BentoMetricTextStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 38.sp,
    letterSpacing = (-1.2).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

val DeskClockTextStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 104.sp,
    lineHeight = 100.sp,
    letterSpacing = (-3.0).sp,
    platformStyle = TightNumericPlatformStyle,
    lineHeightStyle = CenteredNumericLineHeightStyle
)

val PillBadgeTextStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 11.5.sp,
    lineHeight = 15.sp,
    letterSpacing = 0.3.sp,
    platformStyle = TightNumericPlatformStyle
)

val DrawerHeaderTextStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 21.sp,
    lineHeight = 25.sp,
    letterSpacing = (-0.5).sp,
    platformStyle = TightNumericPlatformStyle
)

val PrimaryButtonTextStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.2).sp,
    platformStyle = TightNumericPlatformStyle
)

val AppTitleTextStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp
)

val SubtitleExplanationTextStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp
)

val Typography = Typography(
    displayLarge = DialClockTextStyle,
    displayMedium = DeskClockTextStyle,
    headlineLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.4).sp,
        platformStyle = TightNumericPlatformStyle
    ),
    headlineMedium = DrawerHeaderTextStyle,
    titleLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.3).sp,
        platformStyle = TightNumericPlatformStyle
    ),
    titleMedium = PrimaryButtonTextStyle,
    bodyLarge = AppTitleTextStyle,
    bodyMedium = SubtitleExplanationTextStyle,
    bodySmall = SubtitleExplanationTextStyle,
    labelLarge = PillBadgeTextStyle,
    labelMedium = PillBadgeTextStyle,
    labelSmall = PillBadgeTextStyle
)
