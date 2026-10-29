package com.example

import androidx.compose.ui.unit.sp
import com.example.ui.theme.DisplayUnitTextStyle
import com.example.ui.theme.NightAlert
import com.example.ui.theme.NightColorOnPrimary
import com.example.ui.theme.NightColorSecondaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.SealedVaultNightColorScheme
import com.example.ui.theme.TokenColorOnPrimary
import com.example.ui.theme.TokenColorSurfaceBright
import com.example.ui.theme.TokenTrueBlack
import com.example.ui.theme.Typography
import com.example.ui.theme.WarmTactileStudioColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Issue #63 — the theme is fully wired, not half: every slot the app
 * consumes resolves to a Monolith token, the dark scheme carries no
 * light-mode token, and all 15 Typography slots are on the ladder.
 */
class ThemeWiringTest {

    @Test
    fun `surfaceBright is the token in both schemes, not a library default`() {
        assertEquals(TokenColorSurfaceBright, WarmTactileStudioColorScheme.surfaceBright)
        assertEquals(NightColorSurfaceBright, SealedVaultNightColorScheme.surfaceBright)
    }

    @Test
    fun `container staircase and inverse slots are wired in both schemes`() {
        assertEquals(TokenTrueBlack, WarmTactileStudioColorScheme.scrim)
        assertEquals(TokenTrueBlack, SealedVaultNightColorScheme.scrim)
        assertNotEquals(
            WarmTactileStudioColorScheme.surfaceContainerLowest,
            WarmTactileStudioColorScheme.surfaceContainerHighest
        )
        assertEquals(TokenColorOnPrimary, WarmTactileStudioColorScheme.inverseOnSurface)
        assertEquals(NightColorOnPrimary, SealedVaultNightColorScheme.inverseOnSurface)
    }

    @Test
    fun `dark tertiary and error are night tokens`() {
        assertEquals(NightColorSecondaryContainer, SealedVaultNightColorScheme.tertiary)
        assertEquals(NightAlert, SealedVaultNightColorScheme.error)
        assertEquals(NightColorOnPrimary, SealedVaultNightColorScheme.onError)
    }

    @Test
    fun `typography assigns all slots and the 50 tier is real`() {
        assertEquals(DisplayUnitTextStyle, Typography.displaySmall)
        assertEquals(50.sp, Typography.displaySmall.fontSize)
        assertEquals(Typography.headlineLarge, Typography.headlineSmall)
        assertEquals(Typography.titleMedium.fontFamily, Typography.titleSmall.fontFamily)
        // bodyLarge is the honest 400 body tier, not the bold title style.
        assertNotEquals(Typography.titleSmall.fontWeight, Typography.bodyLarge.fontWeight)
        assertNotEquals(Typography.bodyMedium.lineHeight, Typography.bodySmall.lineHeight)
    }
}
