package com.example.ui.doorway

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE DOORWAY'S TEXTURE — grain derived from the surface itself.
 *
 * Research intake (governing file: `research/intake-and-apply-list.md`
 * §1, reconciling SUCCESSOR-22's routing): TEXTURE is a surface
 * property decided in-build — the door's enamel/steel/brass carry
 * real grain derived from the surface itself, inside the seven-ink
 * law; never flat fills, never a post filter.
 *
 * How this file obeys that, exactly:
 *  - The grain is a deterministic function of position on the
 *    surface (a cell hash — the same point of enamel always carries
 *    the same grain, on every frame and every device; a shimmering
 *    or random-per-frame texture would be a filter laid OVER the
 *    surface, not the surface's own grain).
 *  - A grain value never becomes a colour of its own. It becomes a
 *    RELIGHT gradient (−1..1) fed to [StrongroomLight.relief], so
 *    every tone the grain produces is the surface ink's own lit or
 *    shadow partner, quantised to whole eighths by the foundation's
 *    law. No eighth ink, no alpha wash, no new hue can enter here.
 *  - Enamel grain is fine (small cells, shallow relief); steel
 *    grain is coarser and deeper; brass grain is fine and warm.
 *    The three species differ in cell size and relief depth, the
 *    way the real materials differ — studied from real enamelled
 *    vault doors, machined steel, and struck brass (real-reference
 *    discipline, intake §1), not from a generated look.
 *
 * Pure arithmetic over plain values: unit-testable on a bare JVM.
 */
object DoorwayTexture {

    /** Grain species, by material. [cellPx] is the grain cell's
     *  size at drawing scale; [reliefEighthsCap] caps how far the
     *  grain may push a tone from its ink, in RELIGHT eighths —
     *  enamel is a shallow skin over steel, steel's machining
     *  marks cut deeper, brass is struck smooth and shallow. */
    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        ENAMEL(cellPx = 7, reliefEighthsCap = 2),
        STEEL(cellPx = 11, reliefEighthsCap = 3),
        BRASS(cellPx = 6, reliefEighthsCap = 2)
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
     * eighths, capped by the species. The partners and the eighths
     * quantisation are [StrongroomLight]'s own; this file chooses
     * only the step count, from the grain. Where the state's law
     * gives a mark no lit partner (DARK's subtraction-only, DAY's
     * unwarmed light), [StrongroomLight.litPartner] returns the
     * mark itself and the lit side of the grain simply does not
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
