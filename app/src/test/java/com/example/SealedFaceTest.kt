package com.example

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.sealed.SealedTexture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-37 covering tests: the sealed face's texture law
 * (intake §1 — texture is a surface property, not a filter),
 * under RELIGHT's sealed state.
 */
class SealedFaceTest {

    @Test
    fun grain_isDeterministicAtAPosition() {
        repeat(50) {
            assertEquals(
                SealedTexture.grainAt(3, 7, seed = 37),
                SealedTexture.grainAt(3, 7, seed = 37)
            )
        }
    }

    @Test
    fun grain_variesAcrossTheSurface() {
        val values = (0..24).map { SealedTexture.grainAt(it, it * 2, seed = 37) }.toSet()
        assertTrue("grain must not be a flat field", values.size > 8)
    }

    private fun isEighthsBlendBetween(tone: Color, from: Color, to: Color): Boolean =
        (0..8).any { StrongroomLight.blendEighth(from, to, it) == tone }

    @Test
    fun enamelTones_areOnlyTheInksOwnEighthsPartners_inSealedLight() {
        val state = StrongroomLightState.SEALED
        val enamel = StrongroomLight.stateSurface(
            StrongroomInks.Violet, state,
            com.example.ui.foundation.StrongroomLitSurface.FACE
        )
        for (x in 0..20) for (y in 0..20) {
            val tone = SealedTexture.cellTone(
                enamel, StrongroomInks.Graphite,
                SealedTexture.Species.ENAMEL, x, y, seed = 37, state = state
            )
            val lit = StrongroomLight.litPartner(enamel, StrongroomInks.Graphite, state)
            val shadow = StrongroomLight.shadowPartner(enamel)
            assertTrue(
                "tone must be an eighths blend toward a RELIGHT partner",
                isEighthsBlendBetween(tone, enamel, lit) ||
                    isEighthsBlendBetween(tone, enamel, shadow)
            )
        }
    }

    @Test
    fun speciesDifferInGrainScaleAndDepth() {
        assertTrue(
            SealedTexture.Species.STEEL.cellPx > SealedTexture.Species.ENAMEL.cellPx
        )
        assertTrue(
            SealedTexture.Species.STEEL.reliefEighthsCap >
                SealedTexture.Species.ENAMEL.reliefEighthsCap
        )
    }
}
