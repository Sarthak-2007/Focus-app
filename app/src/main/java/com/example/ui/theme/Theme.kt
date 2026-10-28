package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    error = TokenNothingRed,
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
    error = NightNothingRed,
    onError = NightColorPrimary,
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
        shapes = MonolithShapes,
        content = content
    )
}
