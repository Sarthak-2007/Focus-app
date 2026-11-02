package com.example.ui.drawer

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE DRAWER'S TEXTURE — grain derived from each surface itself.
 *
 * Texture is a surface property (intake §1), decided in-build:
 * the drawer's furniture is WOOD-DARK and BRASS and STEEL inside
 * the seven inks — no eighth colour exists here. The wood is
 * graphite descended toward its floor with violet in its lit
 * partner (dark-stained timber read through the room's own
 * grade: graphite mass, violet relief — never a brown, which
 * would be an eighth ink invented for furniture). The brass is
 * the plate's gold. The steel is graphite. Every tone below is
 * the surface ink's own RELIGHT partner moved by whole eighths
 * ([StrongroomLight]) — grain chooses the step count, never a
 * colour.
 *
 * Species differ the way the materials differ:
 *  - WOOD: long thin streaks — the grain runs ALONG the drawer,
 *    so neighbouring cells across the run share their figure far
 *    more than cells down it (the hash is stretched 30:1 along
 *    x). Fine cells, shallow relief: stained wood drinks
 *    light. (The earlier coarse 13dp / 6:1 form read on a
 *    real phone as violet bars — see the species note.)
 *  - BRASS: fine cells, shallow relief — struck smooth (the
 *    doorway's brass law, carried).
 *  - STEEL: coarser cells, deeper relief — machined marks cut
 *    (the doorway's steel law, carried).
 *
 * The grain is a deterministic function of position: the same
 * point of wood always carries the same streak, on every frame
 * and every device. A shimmering texture would be a filter laid
 * OVER the surface; this is the surface's own grain.
 *
 * Pure arithmetic over plain values: unit-testable on a bare JVM.
 */
object DrawerTexture {

    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        // WOOD cured on device evidence (DEVICE-TRUTH, 2026-11-03):
        // at 13dp cells stretched 6:1, the grain stood on Sarthak's
        // phone (OnePlus 13, 1440x3168) as a mosaic of hard-edged
        // violet bars filling the empty drawer — each hash group
        // was a 78x13dp billboard flipping between graphite and
        // its violet lit partner. Timber grain is fine and thin:
        // 3dp cells on a 30:1 run — long faint lines at the same
        // 2-eighth cap, never blocks. (The cap is not the lever:
        // at a 1-eighth cap the quantiser's truncation would
        // flat-line the grain to nothing.)
        WOOD(cellPx = 3, reliefEighthsCap = 2),
        BRASS(cellPx = 6, reliefEighthsCap = 2),
        STEEL(cellPx = 11, reliefEighthsCap = 3)
    }

    /** The grain at one point of a surface, in 0..1. Splitmix-style
     *  integer hash of the cell coordinates and the surface's seed
     *  (the doorway texture's hash, carried — one grain family for
     *  the room's furniture). WOOD stretches its coordinates along
     *  the run before hashing, so its streaks lie long and thin. */
    fun grainAt(species: Species, cellX: Int, cellY: Int, seed: Int): Float {
        val hx = if (species == Species.WOOD) cellX / 30 else cellX
        val hy = if (species == Species.WOOD) cellY else cellY
        var h = (hx * 0x1E35A7BD) xor (hy * 0x27D4EB2F) xor (seed * 0x165667B1)
        h = h xor (h ushr 15)
        h *= 0x85EBCA6B.toInt()
        h = h xor (h ushr 13)
        h *= 0xC2B2AE35.toInt()
        h = h xor (h ushr 16)
        return (h and 0xFFFFFF) / 0xFFFFFF.toFloat()
    }

    /** The grain as a RELIGHT gradient in −1..1. */
    fun gradientAt(species: Species, cellX: Int, cellY: Int, seed: Int): Float =
        grainAt(species, cellX, cellY, seed) * 2f - 1f

    /**
     * One grain cell's tone: [ink] moved toward its own RELIGHT
     * partner by whole eighths, capped by the species — the
     * doorway texture's cell law, carried whole. Where the
     * state's law gives a mark no lit partner, the lit side of
     * the grain simply does not move: the state's law binds the
     * texture, never the reverse.
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
        val g = gradientAt(species, cellX, cellY, seed)
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

    /** The drawer's wood ink: graphite, the room's mass — dark
     *  timber inside the seven inks, its warmth carried by
     *  RELIGHT's lit partner (violet relief, orange spent only
     *  as light), never by a furniture hue of its own. */
    val WoodInk: Color = com.example.ui.foundation.StrongroomInks.Graphite
}
