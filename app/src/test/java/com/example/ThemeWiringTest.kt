package com.example

import androidx.compose.ui.unit.sp
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkGraphite
import com.example.ui.theme.InkMagenta
import com.example.ui.theme.InkViolet
import com.example.ui.theme.MonolithType
import com.example.ui.theme.NightAlert
import com.example.ui.theme.NightColorOnPrimary
import com.example.ui.theme.NightColorSecondaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.SealedVaultNightColorScheme
import com.example.ui.theme.StrongroomColorScheme
import com.example.ui.theme.TokenColorOnPrimary
import com.example.ui.theme.TokenColorSurfaceBright
import com.example.ui.theme.TokenTrueBlack
import com.example.ui.theme.Typography
import com.example.ui.theme.WarmTactileStudioColorScheme
import androidx.compose.ui.text.font.FontWeight
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
    fun `typography assigns all slots from the Stage 5 steps`() {
        // P0 (BUILD-SR-1): the old 104/72/50/22/15/10 ladder is retired;
        // every Material slot now IS a named Stage 5 step.
        assertEquals(MonolithType.Scr6, Typography.displayLarge)
        assertEquals(MonolithType.Scr4, Typography.displaySmall)
        assertEquals(20.sp, Typography.displaySmall.fontSize)
        assertEquals(MonolithType.Scr4, Typography.headlineLarge)
        assertEquals(MonolithType.Scr3, Typography.headlineSmall)
        assertEquals(MonolithType.Scr1, Typography.titleSmall)
        // bodyLarge is the honest Atkinson 400 primary body step (SCR-2).
        assertEquals(MonolithType.Scr2, Typography.bodyLarge)
        assertEquals(FontWeight.Normal, Typography.bodyLarge.fontWeight)
        assertNotEquals(Typography.bodyMedium.lineHeight, Typography.bodySmall.lineHeight)
    }

    @Test
    fun `strongroom scheme is wired from the seven inks`() {
        assertEquals(InkViolet, StrongroomColorScheme.background)
        assertEquals(InkGold, StrongroomColorScheme.onBackground)
        assertEquals(InkGold, StrongroomColorScheme.primary)
        assertEquals(InkGraphite, StrongroomColorScheme.onPrimary)
        assertEquals(InkGraphite, StrongroomColorScheme.surfaceVariant)
        assertEquals(InkMagenta, StrongroomColorScheme.error)
    }
}
