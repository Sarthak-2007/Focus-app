package com.example.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
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
    tertiary = ElectricIris,
    onTertiary = TokenColorOnPrimary,
    tertiaryContainer = SoftPeriwinkle,
    onTertiaryContainer = TokenColorPrimary,
    background = TokenColorBackground,
    onBackground = TokenTextColorPrimary,
    surface = TokenColorSurface,
    onSurface = TokenColorOnSurface,
    surfaceVariant = TokenColorSurfaceVariant,
    onSurfaceVariant = TokenColorOnSurfaceVariant,
    error = TokenAlert,
    onError = TokenColorOnPrimary,
    outline = TokenColorOutline,
    outlineVariant = TokenColorOutlineVariant
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
    tertiary = ElectricIris,
    onTertiary = NightColorPrimary,
    tertiaryContainer = NightColorSurfaceBright,
    onTertiaryContainer = NightColorPrimary,
    background = NightColorBackground,
    onBackground = NightTextColorPrimary,
    surface = NightColorSurface,
    onSurface = NightColorOnSurface,
    surfaceVariant = NightColorSurfaceVariant,
    onSurfaceVariant = NightColorOnSurfaceVariant,
    error = NightColorPrimaryContainer,
    // Chrome C7 (the accent law): the night error slot is re-voiced
    // to the locked night accent #F47442 — #FF8A5C (NightAlert) was
    // a fourth orange living in the scheme, outside the accent set;
    // it leaves the screen set here. (NightAlert stays in the
    // palette file as a token definition; nothing renders it.)
    // Measured (Round 0b): cream #FFE4CF on NightAlert #FF8A5C is 1.95:1 —
    // illegible. The dark on-primary ink gives 8.1:1. (onTertiary and
    // onTertiaryContainer were audited in the same pass: cream on their
    // containers measures 5.6:1 and 11.9:1, so they stay.)
    onError = NightColorOnPrimary,
    outline = NightColorOutline,
    outlineVariant = NightColorOutlineVariant
)

val ObsidianSolarColorScheme = WarmTactileStudioColorScheme
val SunlitPaperLightColorScheme = WarmTactileStudioColorScheme
val WarmIndigoDuskDarkColorScheme = SealedVaultNightColorScheme

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
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    isFocusSessionActive: Boolean = false,
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
        CompositionLocalProvider(LocalIndication provides NoRippleIndication) {
            content()
        }
    }
}
