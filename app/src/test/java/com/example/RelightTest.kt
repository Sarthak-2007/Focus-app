package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkGraphite
import com.example.ui.theme.InkOrange
import com.example.ui.theme.InkViolet
import com.example.ui.theme.Relight
import com.example.ui.theme.RelightState
import com.example.ui.theme.RelightSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * RELIGHT as executable law (P0 — BUILD-SR-1): relief derived from a
 * surface's own luminance, quantised to 1/8 steps, parameterised per
 * state. Source: RELIGHT-S67-1's mechanism filing (§2 mechanism, §3 the
 * four states) + Audit A §5 G-5. Device rendering stays OWED-TO-DEVICE;
 * these tests prove the MECHANISM's arithmetic, never the display.
 */
class RelightTest {

    @Test
    fun `every derivation is a whole number of eighths between two lawful inks`() {
        assertEquals(InkViolet, Relight.blendEighth(InkViolet, InkGraphite, 0))
        assertEquals(InkGraphite, Relight.blendEighth(InkViolet, InkGraphite, 8))
        assertEquals(lerp(InkViolet, InkGraphite, 0.5f), Relight.blendEighth(InkViolet, InkGraphite, 4))
        assertEquals(lerp(InkGold, InkOrange, 0.125f), Relight.blendEighth(InkGold, InkOrange, 1))
        // Out-of-range steps clamp to the quantised range, never extrapolate.
        assertEquals(InkGraphite, Relight.blendEighth(InkViolet, InkGraphite, 99))
        assertEquals(InkViolet, Relight.blendEighth(InkViolet, InkGraphite, -3))
    }

    @Test
    fun `luminance is the surface's own value, ordered as the inks stand`() {
        assertTrue(Relight.luminance(InkGold) > Relight.luminance(InkViolet))
        assertTrue(Relight.luminance(InkViolet) > Relight.luminance(InkGraphite))
        assertEquals(0f, Relight.luminance(Color.Black), 0.0f)
        assertEquals(1f, Relight.luminance(Color.White), 0.001f)
    }

    @Test
    fun `classification names the filed classes and refuses distant colours`() {
        assertEquals(Relight.InkClass.ENAMEL, Relight.classify(InkViolet))
        assertEquals(Relight.InkClass.GOLD, Relight.classify(InkGold))
        assertEquals(Relight.InkClass.GRAPHITE, Relight.classify(InkGraphite))
        assertEquals(Relight.InkClass.OTHER, Relight.classify(Color.White))
    }

    @Test
    fun `signing - enamel warmth is capped at a quarter, gold warms fully, cuts lip in field ink`() {
        // Enamel: lit blend toward orange caps at 2/8 (RELIGHT §2: ≤1/4, L-SIGN only).
        assertEquals(
            Relight.blendEighth(InkViolet, InkOrange, Relight.ENAMEL_LIT_CAP_EIGHTHS),
            Relight.relief(InkViolet, InkViolet, 1f, RelightState.SIGNING)
        )
        // Gold: the lit lip can travel the whole way to its shellac partner.
        assertEquals(InkOrange, Relight.relief(InkGold, InkViolet, 1f, RelightState.SIGNING))
        // A graphite cut's lip is the field ink it is cut into, catching light.
        assertEquals(InkViolet, Relight.relief(InkGraphite, InkViolet, 1f, RelightState.SIGNING))
        // The shadow side descends toward graphite.
        assertEquals(InkGraphite, Relight.relief(InkGold, InkViolet, -1f, RelightState.SIGNING))
    }

    @Test
    fun `sealed - the face descends three eighths, the plate is carried exact, two promoted reads hold`() {
        assertEquals(
            Relight.blendEighth(InkViolet, InkGraphite, 3),
            Relight.stateSurface(InkViolet, RelightState.SEALED, RelightSurface.FACE)
        )
        assertEquals(InkGold, Relight.stateSurface(InkGold, RelightState.SEALED, RelightSurface.PLATE))
        // The two promoted reads, and the glint's one-eighth warm lip.
        assertEquals(InkGraphite, Relight.sealedHourMark())
        assertEquals(InkGold, Relight.sealedPinstripe())
        assertEquals(lerp(InkGold, InkOrange, 0.125f), Relight.sealedPinstripeLitLip())
        // Spill, not lamp: even a fully lit gold mark warms by at most 1/8.
        assertEquals(
            Relight.blendEighth(InkGold, InkOrange, 1),
            Relight.relief(InkGold, InkViolet, 1f, RelightState.SEALED)
        )
    }

    @Test
    fun `dark - subtraction only, per-class descents, no warm read survives`() {
        // Nothing brightens in the dark: a light-facing gradient changes nothing.
        assertEquals(InkGold, Relight.relief(InkGold, InkViolet, 1f, RelightState.DARK))
        assertEquals(InkViolet, Relight.relief(InkViolet, InkViolet, 1f, RelightState.DARK))
        // Per-class face descents: enamel 5/8, gold 1/2, graphite already at the floor.
        assertEquals(
            Relight.blendEighth(InkViolet, InkGraphite, 5),
            Relight.stateSurface(InkViolet, RelightState.DARK, RelightSurface.FACE)
        )
        assertEquals(
            Relight.blendEighth(InkGold, InkGraphite, 4),
            Relight.stateSurface(InkGold, RelightState.DARK, RelightSurface.FACE)
        )
        assertEquals(InkGraphite, Relight.stateSurface(InkGraphite, RelightState.DARK, RelightSurface.FACE))
        // The plate descends only 1/8 — the frame's lightest object by arithmetic.
        assertEquals(
            Relight.blendEighth(InkGold, InkGraphite, 1),
            Relight.stateSurface(InkGold, RelightState.DARK, RelightSurface.PLATE)
        )
    }

    @Test
    fun `day - no warm partner exists, relief is by recess shadow only`() {
        assertEquals(InkGold, Relight.relief(InkGold, InkViolet, 1f, RelightState.DAY))
        assertEquals(InkGraphite, Relight.relief(InkGold, InkViolet, -1f, RelightState.DAY))
        assertEquals(InkViolet, Relight.stateSurface(InkViolet, RelightState.DAY, RelightSurface.FACE))
    }

    @Test
    fun `other-class colours never move in any state - lead's law`() {
        for (state in RelightState.values()) {
            assertEquals(Color.White, Relight.stateSurface(Color.White, state, RelightSurface.FACE))
            assertEquals(Color.White, Relight.relief(Color.White, InkViolet, 1f, state))
            assertEquals(Color.White, Relight.relief(Color.White, InkViolet, -1f, state))
        }
    }

    @Test
    fun `dark face descents are exposed as data per class`() {
        assertEquals(5, Relight.darkFaceDescentEighths(Relight.InkClass.ENAMEL))
        assertEquals(4, Relight.darkFaceDescentEighths(Relight.InkClass.GOLD))
        assertEquals(0, Relight.darkFaceDescentEighths(Relight.InkClass.GRAPHITE))
        assertEquals(0, Relight.darkFaceDescentEighths(Relight.InkClass.OTHER))
    }

    @Test
    fun `state parameters carry the filed directions and descent fractions`() {
        val signing = Relight.paramsFor(RelightState.SIGNING)
        assertEquals(-0.83f, signing.lightDirectionX, 0.0f)
        assertEquals(-0.55f, signing.lightDirectionY, 0.0f)
        assertEquals(3, Relight.paramsFor(RelightState.SEALED).faceDescentEighths)
        assertEquals(0, Relight.paramsFor(RelightState.SEALED).plateDescentEighths)
        assertTrue(Relight.paramsFor(RelightState.DARK).subtractionOnly)
        assertEquals(-1f, Relight.paramsFor(RelightState.DAY).lightDirectionY, 0.0f)
    }
}
