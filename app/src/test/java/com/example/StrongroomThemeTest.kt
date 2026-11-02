package com.example

import com.example.ui.foundation.APP_NAME
import com.example.ui.foundation.StrongroomColorScheme
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomType
import com.example.ui.foundation.StrongroomTypography
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * BUILD-SR-33 — covering tests for StrongroomTheme.kt: the scheme is
 * built ONLY from the seven inks (every slot is an ink of record),
 * the typography maps slot-for-slot onto the Stage 5 scales, and the
 * user-facing name is ULYSSES (ledger E-12).
 */
class StrongroomThemeTest {

    @Test
    fun schemeSlotsAreOnlyTheSevenInks() {
        val slots = listOf(
            StrongroomColorScheme.primary,
            StrongroomColorScheme.onPrimary,
            StrongroomColorScheme.secondary,
            StrongroomColorScheme.onSecondary,
            StrongroomColorScheme.tertiary,
            StrongroomColorScheme.onTertiary,
            StrongroomColorScheme.background,
            StrongroomColorScheme.onBackground,
            StrongroomColorScheme.surface,
            StrongroomColorScheme.onSurface,
            StrongroomColorScheme.surfaceVariant,
            StrongroomColorScheme.onSurfaceVariant,
            StrongroomColorScheme.outline,
            StrongroomColorScheme.error,
            StrongroomColorScheme.onError
        )
        slots.forEach { slot ->
            assertTrue_ink(slot)
        }
        // The grade, in slots: gold primary, violet surface, graphite field.
        assertEquals(StrongroomInks.Gold, StrongroomColorScheme.primary)
        assertEquals(StrongroomInks.Violet, StrongroomColorScheme.surface)
        assertEquals(StrongroomInks.Graphite, StrongroomColorScheme.background)
    }

    private fun assertTrue_ink(slot: androidx.compose.ui.graphics.Color) {
        org.junit.Assert.assertTrue(
            "scheme slot $slot is not one of the seven inks",
            StrongroomInks.All.contains(slot)
        )
    }

    @Test
    fun typographyMapsOntoTheScales() {
        assertEquals(StrongroomType.Scr6, StrongroomTypography.displayLarge)
        assertEquals(StrongroomType.Scr5, StrongroomTypography.headlineLarge)
        assertEquals(StrongroomType.Scr4, StrongroomTypography.headlineMedium)
        assertEquals(StrongroomType.Scr3, StrongroomTypography.titleLarge)
        assertEquals(StrongroomType.Scr2, StrongroomTypography.bodyLarge)
        assertEquals(StrongroomType.Scr1, StrongroomTypography.bodyMedium)
        assertEquals(StrongroomType.Scr0, StrongroomTypography.labelSmall)
    }

    @Test
    fun nameIsUlysses() {
        assertEquals("ULYSSES", APP_NAME)
    }
}
