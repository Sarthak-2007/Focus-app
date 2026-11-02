package com.example.ui.`break`

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE BREAK'S TEXTURES — grain derived from the surface itself
 * (research intake §1: texture is a material property, not a
 * filter; a surface property, executed in-build).
 *
 * The break's materials: ENAMEL for the eased leaf (the door's
 * own deep-violet enamelled steel, descended in the sealed
 * light), BRASS for the escapement's window rim, its teeth and
 * the within-reach tokens (struck and turned brass), STEEL for
 * the bolts and the case window's inner ring (machined, coarse),
 * and the LAMPLIGHT SEAM — the crack of the room's warm light at
 * the eased leaf's edge: orange exists here AS LIGHT ONLY (Grade
 * Book §1.5, D-03 — never as pigment, never as a flat fill),
 * spent through RELIGHT's lit partner exactly as the foundation
 * spends it.
 *
 * The law is the room's, restated because it binds here too:
 * grain is a deterministic function of position (the same point
 * of brass carries the same grain on every frame — a wheel that
 * re-grains between frames is a screen, not a clock); a grain
 * value never becomes a colour of its own — it becomes a RELIGHT
 * gradient, so every tone is the surface ink's own lit or shadow
 * partner, in whole eighths. No eighth ink, no alpha wash, no
 * image of any kind. The hash is the room's own (one grain law
 * for the room's furniture, never forked per slice).
 */
object BreakTexture {

    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        /** The door's enamel: fine grain, shallow relief. */
        ENAMEL(cellPx = 7, reliefEighthsCap = 2),

        /** Struck/turned brass: fine, warm, shallow. */
        BRASS(cellPx = 6, reliefEighthsCap = 2),

        /** Machined steel: coarser, deeper — bolts and case. */
        STEEL(cellPx = 11, reliefEighthsCap = 3)
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
