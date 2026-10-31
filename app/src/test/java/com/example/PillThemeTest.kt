package com.example

import com.example.service.PillPalette
import com.example.service.PillPalettes
import com.example.service.PillTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The pill's skin, pinned as data — census T1/T2 on #16
 * (comment 6536820385). [PillTheme] had zero tests: the default
 * palette is the face every look-world will redress, and until
 * it is pinned literally, a silent colour drift (or a green
 * slipping into the default, against the studio law) would pass
 * every suite in the repo.
 *
 * Plain JUnit — the palette is pure data. The figures-typeface
 * caching pin (census T3) needs a Context and lives with the
 * Robolectric pack in [PillLeaseBoundaryTest].
 *
 * Written against PR #231's train tree; parked on
 * `agent/issue-16-test-matrix` for post-train landing.
 * DECLARED, uncompiled.
 *
 * — T24 Builder seat 4, #16 wave
 */
class PillThemeTest {

    // T1 — the default palette, pinned literally, all five ARGB
    // values including the background's E6 alpha and the locked
    // accent.
    @Test
    fun defaultPaletteIsPinnedLiterally() {
        val p = PillPalettes.Default
        assertEquals(0xE61F1A16.toInt(), p.backgroundArgb)
        assertEquals(0xFF4A4038.toInt(), p.borderArgb)
        assertEquals(0xFFFFE4CF.toInt(), p.textArgb)
        assertEquals(0xFFB9ACA0.toInt(), p.mutedTextArgb)
        assertEquals(0xFFF47442.toInt(), p.accentArgb)
    }

    // T1 (continued) — the standing holder starts at the default:
    // a look lane's redress is a deliberate write, never a drift
    // in the initial value.
    @Test
    fun themeHolderStartsAtTheDefaultPalette() {
        assertEquals(PillPalettes.Default, PillTheme.palette)
    }

    // T1 (continued) — never green, machine-checked: in no
    // default colour may the green channel dominate BOTH the red
    // and the blue (a green-dominant colour is what the studio
    // law bans from anything built for Sarthak).
    @Test
    fun defaultPaletteHasNoGreenDominantColour() {
        val p = PillPalettes.Default
        val colours = listOf(
            "background" to p.backgroundArgb,
            "border" to p.borderArgb,
            "text" to p.textArgb,
            "mutedText" to p.mutedTextArgb,
            "accent" to p.accentArgb
        )
        for ((name, argb) in colours) {
            val r = (argb shr 16) and 0xFF
            val g = (argb shr 8) and 0xFF
            val b = argb and 0xFF
            assertTrue(
                "$name must not be green-dominant (r=$r g=$g b=$b)",
                !(g > r && g > b)
            )
        }
    }

    // T2 — a palette swap is honoured by the holder the renderer
    // reads: write a custom palette, read the same instance back.
    // (The render-level half — the next render actually painting
    // the new colours — needs the controller harness, Builder
    // seat 3's scaffold; this pins the seam the render reads.)
    // The default is restored in `finally`: the holder is a
    // process-wide singleton other suites share.
    @Test
    fun paletteSwapIsReadBackByTheThemeHolder() {
        val custom = PillPalette(
            backgroundArgb = 0xFF101010.toInt(),
            borderArgb = 0xFF202020.toInt(),
            textArgb = 0xFF303030.toInt(),
            mutedTextArgb = 0xFF404040.toInt(),
            accentArgb = 0xFF505050.toInt()
        )
        try {
            PillTheme.palette = custom
            assertEquals(custom, PillTheme.palette)
        } finally {
            PillTheme.palette = PillPalettes.Default
        }
        assertEquals(PillPalettes.Default, PillTheme.palette)
    }
}
