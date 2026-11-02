package com.example.ui.signing

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE SIGNING TABLE'S TEXTURES — grain derived from the surface
 * itself (research intake §1: texture is a surface property, not a
 * filter). The signing's materials are brass (the tray, the pawl,
 * the preset discs), steel (the dial, the wheel) and PAPER (the
 * slip) — the doorway slice's enamel/steel/brass grain law
 * (ui.doorway.DoorwayTexture) is the sibling implementation of the
 * same intake law; this file carries the species the signing table
 * adds: paper, whose grain is the shallowest of all — a slip is
 * smooth to the thumb, its grain read only in raking light.
 *
 * The law is the doorway's, restated because it binds here too:
 * grain is a deterministic function of position (the same point of
 * paper carries the same grain on every frame); a grain value
 * never becomes a colour of its own — it becomes a RELIGHT
 * gradient fed to [StrongroomLight], so every tone is the surface
 * ink's own lit or shadow partner, in whole eighths. No eighth
 * ink, no alpha wash, no image of any kind.
 */
object SigningTexture {

    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        /** Struck brass: fine, warm, shallow. */
        BRASS(cellPx = 6, reliefEighthsCap = 2),

        /** Machined steel: coarser, deeper. */
        STEEL(cellPx = 11, reliefEighthsCap = 3),

        /** The slip's paper: the finest grain on the table, one
         *  eighth at most — paper does not relieve, it fibres. */
        PAPER(cellPx = 5, reliefEighthsCap = 1)
    }

    fun grainAt(cellX: Int, cellY: Int, seed: Int): Float {
        var h = (cellX * 0x1E35A7BD) xor (cellY * 0x27D4EB2F) xor (seed * 0x165667B1)
        h = h xor (h ushr 15)
        h *= 0x85EBCA6B.toInt()
        h = h xor (h ushr 13)
        h *= 0xC2B2AE35.toInt()
        h = h xor (h ushr 16)
        return (h and 0xFFFFFF) / 0xFFFFFF.toFloat()
    }

    fun gradientAt(cellX: Int, cellY: Int, seed: Int): Float =
        grainAt(cellX, cellY, seed) * 2f - 1f

    fun cellTone(
        ink: Color,
        field: Color,
        species: Species,
        cellX: Int,
        cellY: Int,
        seed: Int,
        state: StrongroomLightState
    ): Color {
        val g = gradientAt(cellX, cellY, seed)
        val steps = (kotlin.math.abs(g) * species.reliefEighthsCap)
            .toInt().coerceIn(0, species.reliefEighthsCap)
        if (steps == 0) return ink
        return if (g > 0f) {
            StrongroomLight.blendEighth(
                ink, StrongroomLight.litPartner(ink, field, state), steps
            )
        } else {
            StrongroomLight.blendEighth(
                ink, StrongroomLight.shadowPartner(ink), steps
            )
        }
    }
}
