package com.example.ui.lifting

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE LIFTING'S TEXTURE — grain derived from the surface itself.
 *
 * The same law the Doorway's texture stands under (intake §1,
 * reconciled by SUCCESSOR-22): texture is a surface property —
 * the brass plate's grain, the enamel's skin, the steel's
 * machining are drawn as grain derived from the surface's own
 * ink through RELIGHT's partners and whole eighths. Never flat
 * fills on a material surface, never a post filter, never an
 * eighth ink: a grain value becomes a RELIGHT gradient (−1..1)
 * and nothing else.
 *
 * The grain is a deterministic function of position (a splitmix-
 * style cell hash — the same point of brass always carries the
 * same grain, on every frame and every device). A plate that
 * lands in the pile keeps its grain: the pile's plates are the
 * same metal, struck once, resting at honest angles.
 *
 * Pure arithmetic over plain values: unit-testable on a bare JVM.
 */
object LiftingTexture {

    /** Grain species, by material. [cellPx] is the grain cell's
     *  size at drawing scale; [reliefEighthsCap] caps how far the
     *  grain may push a tone from its ink, in RELIGHT eighths —
     *  brass is struck smooth and shallow, enamel is a shallow
     *  skin over steel, steel's machining cuts deeper. */
    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        BRASS(cellPx = 6, reliefEighthsCap = 2),
        ENAMEL(cellPx = 7, reliefEighthsCap = 2),
        STEEL(cellPx = 11, reliefEighthsCap = 3)
    }

    /**
     * The grain at one point of a surface, in 0..1. A splitmix-style
     * integer hash of the cell coordinates and the surface's seed:
     * deterministic, position-locked, and uncorrelated between
     * neighbouring cells (grain, not gradient).
     */
    fun grainAt(cellX: Int, cellY: Int, seed: Int): Float {
        var h = (cellX * 0x1E35A7BD) xor (cellY * 0x27D4EB2F) xor (seed * 0x165667B1)
        h = h xor (h ushr 15)
        h *= 0x85EBCA6B.toInt()
        h = h xor (h ushr 13)
        h *= 0xC2B2AE35.toInt()
        h = h xor (h ushr 16)
        return (h and 0xFFFFFF) / 0xFFFFFF.toFloat()
    }

    /** The grain as a RELIGHT gradient in −1..1: the surface's own
     *  luminance variation, ready for [StrongroomLight.relief]. */
    fun gradientAt(cellX: Int, cellY: Int, seed: Int): Float =
        grainAt(cellX, cellY, seed) * 2f - 1f

    /**
     * One grain cell's tone: [ink] moved toward its own RELIGHT
     * partner — the lit partner for a light-facing grain cell, the
     * shadow partner for a recessed one — by a whole number of
     * eighths, capped by the species. Where the state's law gives
     * a mark no lit partner (DARK's subtraction-only, DAY's
     * unwarmed light), the lit side of the grain simply does not
     * move — the state's law binds the texture, never the reverse.
     */
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
