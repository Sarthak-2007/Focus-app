package com.example

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P8a — THE DESK CLOCK SURFACE (ui/strongroom/DeskClockScreen.kt).
 *
 * The surface itself is a Canvas drawing with no JVM-renderable
 * state; what this test pins is its filed shape, so a later edit
 * that renames or drops a half of the surface fails here:
 *
 *  - `DeskClockFace` — the stateless regulator drawing (record +
 *    hands in, canvas out), and
 *  - `DeskClockScreen` — the hosted, minute-ticking in-session
 *    surface that feeds it —
 * both stand on the file's facade class, by name.
 */
class DeskClockScreenP8Test {

    @Test
    fun theSurfaceStands_withBothHalves_byName() {
        val facade = Class.forName("com.example.ui.strongroom.DeskClockScreenKt")
        val methods = facade.declaredMethods.map { it.name }
        assertTrue("DeskClockFace missing from the surface", methods.contains("DeskClockFace"))
        assertTrue("DeskClockScreen missing from the surface", methods.contains("DeskClockScreen"))
    }
}
