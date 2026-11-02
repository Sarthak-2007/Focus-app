package com.example.ui.antechamber

import androidx.compose.ui.graphics.Color
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState

/**
 * THE ANTECHAMBER'S TEXTURES — grain derived from the surface
 * itself (research intake §1: texture is a material property, not
 * a filter; executed at the material grade, in-build).
 *
 * The room's materials: STEEL for the grille (the door in the
 * room's flank — machined bars over the room's depth), BRASS for
 * the drum, the tokens, the calling horn and the shutter (struck
 * and turned brass — the drum's brass is the shutter's brass, one
 * metal, per the Gemini routing's single mechanism), and LEAD for
 * the governor's seal (dullness inside the graphite depths — lead
 * has no hue of its own, Grade Book §SW P-7; its grain is the
 * deepest and the least relieved, a crimped softness read by
 * shadow, not by light).
 *
 * The law is the doorway's and the signing table's, restated
 * because it binds here too: grain is a deterministic function of
 * position (the same point of brass carries the same grain on
 * every frame — a pile that re-grains between looks is a screen,
 * not a room); a grain value never becomes a colour of its own —
 * it becomes a RELIGHT gradient fed to [StrongroomLight], so every
 * tone is the surface ink's own lit or shadow partner, in whole
 * eighths. No eighth ink, no alpha wash, no image of any kind.
 * The hash is the signing table's own (one grain law for the
 * room's furniture, never forked per slice).
 */
object AntechamberTexture {

    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        /** Struck/turned brass: fine, warm, shallow. */
        BRASS(cellPx = 6, reliefEighthsCap = 2),

        /** Machined steel: coarser, deeper — the grille's bars. */
        STEEL(cellPx = 11, reliefEighthsCap = 3),

        /** Crimped lead: the governor's seal — coarse cells, but
         *  almost no relief: lead dents, it does not shine. */
        LEAD(cellPx = 9, reliefEighthsCap = 1)
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

    /** A grained fill as a draw helper's colour source is the
     *  caller's Canvas work; this file carries only the tone law,
     *  exactly as its sibling slices do. */
}
