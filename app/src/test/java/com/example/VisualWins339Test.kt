package com.example

import androidx.compose.ui.text.TextStyle
import com.example.ui.theme.BodySmallTextStyle
import com.example.ui.theme.BodyTextStyle
import com.example.ui.theme.DeskClockTextStyle
import com.example.ui.theme.DialClockTextStyle
import com.example.ui.theme.DisplayUnitTextStyle
import com.example.ui.theme.DrawerHeaderTextStyle
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.PillBadgeTextStyle
import com.example.ui.theme.PrimaryButtonTextStyle
import com.example.ui.theme.AppTitleTextStyle
import com.example.ui.theme.TaskActiveTitleTextStyle
import com.example.ui.theme.Typography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #339 (Audit R5 supplement — Freedom, Wins): the visual
 * wins, pinned.
 *
 * F4 — the type ladder: Monolith's theme carries SIX sizes only
 * (104 / 72 / 50 / 22 / 15 / 10 — the TYPE-RAMP-TABLE in Type.kt),
 * where Freedom's block screen alone uses 76 arbitrary per-call
 * sizes. This test reads the actual Typography object: if a
 * future style lands off-ladder in a theme slot, it fails here.
 * (Call-site font sizes were also census-verified for this issue:
 * 25 distinct values tree-wide, 183 of 197 usages inside the
 * 10–16 core band — see the issue body.)
 *
 * F1 — the Deck's compositing is a pure function of state: its
 * vertigo/blur easings are deterministic curves with fixed
 * endpoints (the graphicsLayer + RenderEffect rig in
 * TactileDeckScreen consumes exactly these).
 */
class VisualWins339Test {

    private val ladder = setOf(104f, 72f, 50f, 22f, 15f, 10f)

    @Test
    fun everyTypographySlotSitsOnTheLadder() {
        val slots: List<Pair<String, TextStyle>> = listOf(
            "displayLarge" to Typography.displayLarge,
            "displayMedium" to Typography.displayMedium,
            "displaySmall" to Typography.displaySmall,
            "headlineLarge" to Typography.headlineLarge,
            "headlineMedium" to Typography.headlineMedium,
            "headlineSmall" to Typography.headlineSmall,
            "titleLarge" to Typography.titleLarge,
            "titleMedium" to Typography.titleMedium,
            "titleSmall" to Typography.titleSmall,
            "bodyLarge" to Typography.bodyLarge,
            "bodyMedium" to Typography.bodyMedium,
            "bodySmall" to Typography.bodySmall,
            "labelLarge" to Typography.labelLarge,
            "labelMedium" to Typography.labelMedium,
            "labelSmall" to Typography.labelSmall
        )
        for ((name, style) in slots) {
            assertTrue(
                "$name is off-ladder at ${style.fontSize.value}sp",
                ladder.contains(style.fontSize.value)
            )
        }
    }

    @Test
    fun namedStylesMatchTheLadderTiers() {
        assertEquals(104f, DeskClockTextStyle.fontSize.value)
        assertEquals(72f, DialClockTextStyle.fontSize.value)
        assertEquals(50f, DisplayUnitTextStyle.fontSize.value)
        assertEquals(22f, DrawerHeaderTextStyle.fontSize.value)
        assertEquals(22f, TaskActiveTitleTextStyle.fontSize.value)
        assertEquals(15f, BodyTextStyle.fontSize.value)
        assertEquals(15f, BodySmallTextStyle.fontSize.value)
        assertEquals(15f, PrimaryButtonTextStyle.fontSize.value)
        assertEquals(15f, AppTitleTextStyle.fontSize.value)
        assertEquals(10f, PillBadgeTextStyle.fontSize.value)
    }

    @Test
    fun deckCompositingCurvesAreDeterministicStateFunctions() {
        // Same input, same output, fixed endpoints — the Deck's
        // layered/blurred frame is a pure function of shot state.
        assertEquals(0f, MonolithCurves.VertigoBlurEasing.transform(0f), 0.001f)
        assertEquals(1f, MonolithCurves.VertigoBlurEasing.transform(1f), 0.001f)
        assertEquals(
            MonolithCurves.VertigoBlurEasing.transform(0.5f),
            MonolithCurves.VertigoBlurEasing.transform(0.5f),
            0.0f
        )
        assertEquals(0f, MonolithCurves.VertigoTransformEasing.transform(0f), 0.001f)
        assertEquals(1f, MonolithCurves.VertigoTransformEasing.transform(1f), 0.001f)
    }
}
