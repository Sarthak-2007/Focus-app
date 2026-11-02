package com.example.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.dp

val WarmTactileStudioColorScheme = lightColorScheme(
    primary = TokenColorPrimary,
    onPrimary = TokenColorOnPrimary,
    primaryContainer = TokenColorPrimaryContainer,
    onPrimaryContainer = TokenColorOnPrimaryContainer,
    secondary = TokenColorSecondaryContainer,
    onSecondary = TokenColorOnSecondaryContainer,
    secondaryContainer = TokenColorSecondaryContainer,
    onSecondaryContainer = TokenColorOnSecondaryContainer,
    // Tertiary names the token it IS (issue #63): the old alias
    // "ElectricIris" was this same grey wearing a violet name, and the
    // dark scheme smuggled it in as a light token in night clothing.
    tertiary = TokenColorSecondaryContainer,
    onTertiary = TokenColorOnSecondaryContainer,
    tertiaryContainer = TokenColorSurfaceVariant,
    onTertiaryContainer = TokenColorPrimary,
    background = TokenColorBackground,
    onBackground = TokenTextColorPrimary,
    surface = TokenColorSurface,
    onSurface = TokenColorOnSurface,
    surfaceVariant = TokenColorSurfaceVariant,
    onSurfaceVariant = TokenColorOnSurfaceVariant,
    error = TokenAlert,
    onError = TokenColorOnPrimary,
    errorContainer = TokenColorSurfaceVariant,
    onErrorContainer = TokenAlert,
    outline = TokenColorOutline,
    outlineVariant = TokenColorOutlineVariant,
    // Issue #63: EVERY slot the app consumes is wired — surfaceBright is
    // rendered by Airlock/Receipt/Deck, so the raised surfaces now take
    // the tokens that exist for that role (#FAF6F0 day / #362C27 night)
    // instead of Material3 library defaults. Container staircase steps
    // across the existing tokens only; inverse pair is the block bar
    // with cream on it (12.81:1, computed); scrim is true black.
    surfaceBright = TokenColorSurfaceBright,
    surfaceDim = TokenColorSurfaceVariant,
    surfaceContainerLowest = TokenColorSurfaceBright,
    surfaceContainerLow = TokenColorSurface,
    surfaceContainer = TokenColorSurfaceSheet,
    surfaceContainerHigh = TokenColorSurfaceVariant,
    surfaceContainerHighest = TokenColorSurfaceVariant,
    inverseSurface = TokenBlockBarSurface,
    inverseOnSurface = TokenColorOnPrimary,
    inversePrimary = TokenColorPrimaryContainer,
    scrim = TokenTrueBlack,
    surfaceTint = TokenColorPrimary
)

val SealedVaultNightColorScheme = darkColorScheme(
    primary = NightColorPrimary,
    onPrimary = NightColorOnPrimary,
    primaryContainer = NightColorPrimaryContainer,
    onPrimaryContainer = NightColorOnPrimaryContainer,
    secondary = NightColorSecondaryContainer,
    onSecondary = NightColorOnSecondaryContainer,
    secondaryContainer = NightColorSecondaryContainer,
    onSecondaryContainer = NightColorOnSecondaryContainer,
    // Tertiary is a NIGHT token now (issue #63) — was ElectricIris, a
    // light-mode alias (#615B5A day grey). Pairs computed: onTertiary
    // cream on #4A423C = 8.08:1; onTertiaryContainer #EDE3D8 on
    // #362C27 = 10.72:1.
    tertiary = NightColorSecondaryContainer,
    onTertiary = NightColorOnSecondaryContainer,
    tertiaryContainer = NightColorSurfaceBright,
    onTertiaryContainer = NightColorOnSurface,
    background = NightColorBackground,
    onBackground = NightTextColorPrimary,
    surface = NightColorSurface,
    onSurface = NightColorOnSurface,
    surfaceVariant = NightColorSurfaceVariant,
    onSurfaceVariant = NightColorOnSurfaceVariant,
    // Error is NightAlert again (issue #63): the token was dead because
    // the slot had been re-voiced to the accent, which made "error"
    // indistinguishable from "primary action". NightAlert #FF8A5C on
    // the night ground computes 7.90:1, and its onError ink
    // NightColorOnPrimary on it computes 7.98:1 — both pass. (The old
    // 1.95:1 figure was CREAM on NightAlert; the ink is dark, not cream.)
    error = NightAlert,
    onError = NightColorOnPrimary,
    errorContainer = NightColorSurfaceBright,
    onErrorContainer = NightAlert,
    outline = NightColorOutline,
    outlineVariant = NightColorOutlineVariant,
    // Same full wiring as day (issue #63): staircase across night tokens
    // only — recessed variant below ground, raised bright, sheet cap.
    // Inverse pair: cream surface, dark ink on it (15.23:1, computed).
    surfaceBright = NightColorSurfaceBright,
    surfaceDim = NightColorSurfaceVariant,
    surfaceContainerLowest = NightColorSurfaceVariant,
    surfaceContainerLow = NightColorBackground,
    surfaceContainer = NightColorSurfaceBright,
    surfaceContainerHigh = NightColorSurfaceSheet,
    surfaceContainerHighest = NightColorSurfaceSheet,
    inverseSurface = NightColorPrimary,
    inverseOnSurface = NightColorOnPrimary,
    inversePrimary = NightColorPrimaryContainer,
    scrim = TokenTrueBlack,
    surfaceTint = NightColorPrimary
)

/**
 * THE STRONGROOM SCHEME (P0 FOUNDATION — BUILD-SR-1): the redesigned
 * app's Material scheme, wired from the seven inks ONLY (Color.kt's law
 * layer). The ground is the kept violet enamel; gold leaf carries
 * primary emphasis and text on the ground (the violet/gold chord, his
 * eye, Audit A §4); graphite is the recessed/line-work tier. Every slot
 * is an ink — no derived colour stands in a slot; relief and descent
 * are RELIGHT's derivations at render time, never scheme values.
 * The surface lanes (P3+) adopt this scheme surface by surface; the
 * legacy schemes above remain the outgoing generation's, untouched.
 */
val StrongroomColorScheme = darkColorScheme(
    primary = InkGold,
    onPrimary = InkGraphite,
    primaryContainer = InkViolet,
    onPrimaryContainer = InkGold,
    secondary = InkRose,
    onSecondary = InkGraphite,
    secondaryContainer = InkGraphite,
    onSecondaryContainer = InkGold,
    tertiary = InkBlue,
    onTertiary = InkGraphite,
    tertiaryContainer = InkViolet,
    onTertiaryContainer = InkGold,
    background = InkViolet,
    onBackground = InkGold,
    surface = InkViolet,
    onSurface = InkGold,
    surfaceVariant = InkGraphite,
    onSurfaceVariant = InkGold,
    surfaceBright = InkViolet,
    surfaceDim = InkGraphite,
    surfaceContainerLowest = InkGraphite,
    surfaceContainerLow = InkViolet,
    surfaceContainer = InkViolet,
    surfaceContainerHigh = InkGraphite,
    surfaceContainerHighest = InkGraphite,
    inverseSurface = InkGold,
    inverseOnSurface = InkGraphite,
    inversePrimary = InkViolet,
    error = InkMagenta,
    onError = InkGraphite,
    errorContainer = InkRose,
    onErrorContainer = InkGraphite,
    outline = InkGold,
    outlineVariant = Color(0x75E2BC39),
    scrim = InkGraphite,
    surfaceTint = InkGold
)


/**
 * The app's Material typography (P0): every slot IS a named Stage 5
 * step from MonolithType (Type.kt) — a component reaching for a slot
 * inherits the scale, never a stock Material size. This val lives in
 * Theme.kt, not Type.kt, to keep class initialisation acyclic: Type.kt's
 * families are read by MonolithType's styles, whose values this val
 * reads; Theme.kt reads back into neither. (Venue-caught: with the val
 * in Type.kt, a MonolithType-first touch built Typography from
 * still-null styles.)
 */
// Material slots draw ONLY from the Stage 5 steps (P0): a component that
// reaches for a slot inherits the scale, never a stock Material size.
val Typography = Typography(
    displayLarge = MonolithType.Scr6,
    displayMedium = MonolithType.Scr5,
    displaySmall = MonolithType.Scr4,
    headlineLarge = MonolithType.Scr4,
    headlineMedium = MonolithType.Scr3,
    headlineSmall = MonolithType.Scr3,
    titleLarge = MonolithType.Scr3,
    titleMedium = MonolithType.Scr2,
    titleSmall = MonolithType.Scr1,
    bodyLarge = MonolithType.Scr2,
    bodyMedium = MonolithType.Scr1,
    bodySmall = MonolithType.Scr0,
    labelLarge = MonolithType.Scr3,
    labelMedium = MonolithType.Scr1,
    labelSmall = MonolithType.Scr0
)

// (Issue #63: the three scheme aliases that duplicated these two
// schemes under other names — ObsidianSolar / SunlitPaperLight /
// WarmIndigoDuskDark — had zero call sites and are deleted.)

// Strictly two corner radii across the app: 12.dp (small) and 24.dp (large) per DEVELOPER-GUIDE.pdf §3
val MonolithShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * A no-op indication (Round 1c): draws nothing for any interaction.
 * The public NoIndication object only exists in newer foundation
 * versions than this project's BOM 2024.09, so the factory is local.
 */
private object NoRippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        object : Modifier.Node() {}
    override fun hashCode(): Int = -1
    override fun equals(other: Any?): Boolean = other === this
}

@Composable
fun MyApplicationTheme(
    // No default (issue #63): the caller owns the night decision — the
    // ViewModel's theme evaluation (routines + session, chrome C4), NOT
    // the OS flag; a default `false` here lied that day was the norm.
    // There is deliberately no dynamicColor parameter: it was accepted
    // and never read (MainActivity hard-coded it false). Wallpaper
    // dynamic colour can NEVER take this app over — the brand palette
    // is law, and a wallpaper hue could literally introduce green.
    darkTheme: Boolean,
    isFocusSessionActive: Boolean = false,
    // P0: the session's RELIGHT state (Relight.kt). Default is the
    // signing light; the sealed-hours surfaces provide SEALED/DARK/DAY
    // as the day's state demands. The theme provides the law's state —
    // surfaces consume it, never re-derive it.
    relightState: RelightState = RelightState.SIGNING,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme || isFocusSessionActive) {
        SealedVaultNightColorScheme
    } else {
        WarmTactileStudioColorScheme
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = MonolithShapes
    ) {
        // Ripple is purged app-wide (Round 1c, state grammar): controls in
        // this app answer with position, fill and sound — never a wash.
        CompositionLocalProvider(
            LocalIndication provides NoRippleIndication,
            LocalRelightState provides relightState
        ) {
            content()
        }
    }
}
