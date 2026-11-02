package com.example

import com.example.ui.strongroom.FaceCraft
import com.example.ui.strongroom.FaceCraftFallback
import com.example.ui.strongroom.faceCraftUniforms
import com.example.ui.theme.InkGraphite
import com.example.ui.theme.Relight
import com.example.ui.theme.RelightState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DESIGNER-SR-1's FaceCraft, adopted into the tree at Stage 10
 * composition (BUILD-SR-16) — its pure law pinned by name: the
 * per-glyph lip (the whole point of the adoption — a uniform
 * offset is the failed species), the promoted reads, the depth
 * table's signs. Tests postponed to end-phase (R6).
 */
class FaceCraftTest {

    // Two glyphs at opposite ends of the arc catch light
    // differently: the lip offset rotates with the glyph's own
    // arc position. A single uniform offset for every glyph —
    // Slice 1's form — fails this by construction.
    @Test
    fun glyphLipOffset_rotatesWithTheGlyphsArcPosition() {
        val near = FaceCraftFallback.glyphLipOffsetPx(20.0, RelightState.SEALED, 10f)
        val far = FaceCraftFallback.glyphLipOffsetPx(160.0, RelightState.SEALED, 10f)
        assertNotEquals(near, far)
    }

    // In DARK the light direction is (0,0) — subtraction-only,
    // no lip is lit: the offset collapses to zero.
    @Test
    fun glyphLipOffset_inDarkIsZero() {
        val offset = FaceCraftFallback.glyphLipOffsetPx(45.0, RelightState.DARK, 10f)
        assertEquals(0f, offset.first, 0.001f)
        assertEquals(0f, offset.second, 0.001f)
    }

    // Promoted read (i): in SEALED the groove floor is exact
    // graphite, held — P0's sealedHourMark, by name.
    @Test
    fun grooveFloor_sealedIsThePromotedGraphite() {
        assertEquals(Relight.sealedHourMark(), FaceCraftFallback.grooveFloor(RelightState.SEALED))
        assertEquals(InkGraphite, FaceCraftFallback.grooveFloor(RelightState.SEALED))
    }

    // Promoted read (ii): in SEALED the pinstripe pair is P0's —
    // exact gold held, its lip warmed exactly one eighth.
    @Test
    fun pinstripe_sealedIsThePromotedPair() {
        val (base, lip) = FaceCraftFallback.pinstripe(RelightState.SEALED)
        assertEquals(Relight.sealedPinstripe(), base)
        assertEquals(Relight.sealedPinstripeLitLip(), lip)
    }

    // The heightfield's depth table: grooves and the punch are
    // depressions, the pinstripe and ring rules are ridges — the
    // signs are the construction.
    @Test
    fun depthTable_groovesDepressAndRidgesRise() {
        assertTrue(FaceCraft.DEPTH_GLYPH_GROOVE < 0f)
        assertTrue(FaceCraft.DEPTH_HELD_PUNCH < 0f)
        assertTrue(FaceCraft.DEPTH_PINSTRIPE_RIDGE > 0f)
        assertTrue(FaceCraft.DEPTH_RING_RULE > 0f)
    }

    // The shader's uniforms derive ONLY from P0's table.
    @Test
    fun uniforms_deriveFromP0sTable() {
        val params = Relight.paramsFor(RelightState.SEALED)
        val uniforms = faceCraftUniforms(RelightState.SEALED)
        @Suppress("UNCHECKED_CAST")
        val lightDir = uniforms["lightDir"] as FloatArray
        assertEquals(params.lightDirectionX, lightDir[0], 0.001f)
        assertEquals(params.lightDirectionY, lightDir[1], 0.001f)
        assertEquals(
            params.maxLitEighths.toFloat(),
            uniforms["maxLitEighths"] as Float,
            0.001f
        )
    }
}
