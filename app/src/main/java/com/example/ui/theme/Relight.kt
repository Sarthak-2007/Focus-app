package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.roundToInt
import kotlin.math.sqrt

// =========================================================================
// RELIGHT — THE THEME-LEVEL RENDERING LAW (P0 FOUNDATION · BUILD-SR-1)
// Source of law: RELIGHT-S67-1's mechanism filing (Stage 6/7 rethink of
// L5) + Audit A §4/§5 G-5. The kept light is a MECHANISM, not a render
// trick on one sample: relief light DERIVED FROM A SURFACE'S OWN
// LUMINANCE, distinct per state (signing / sealed / dark / day).
//
// The mechanism, as filed: a pixel on the light-facing lip of a cut
// blends toward its class's LIT partner ink; a pixel on the shadow side
// blends toward its SHADOW partner ink. Every blend is quantised to
// 1/8 steps — a derived colour is always a named lawful ink blended
// toward another named lawful ink at a printed fraction, never a new
// token, never a flat field. Blends act on a colour's own value, so
// texture and anti-alias survive as themselves, lit.
//
// This file is the Compose-side execution other lanes consume: pure
// colour functions + the per-state parameter table. Slice 4 (visual, to
// grade) tunes the PARAMETERS in the running app; the mechanism is
// written once, here. Device reads stay OWED-TO-DEVICE (KB10 NO
// VERDICT): nothing here claims how the OnePlus renders these blends.
// =========================================================================

/** The four states the face lives in. */
enum class RelightState { SIGNING, SEALED, DARK, DAY }

/** Which object is being lit — the plate is not on the sealed face and
 *  does not share the face's descent (RELIGHT §3, L-SEALED/L-DARK). */
enum class RelightSurface { FACE, PLATE }

/** Per-state light behaviour, as data (Slice 4 tunes numbers, not code).
 *  Descents are mass movements toward the graphite floor, in eighths. */
data class RelightStateParams(
    /** Light direction (x, y), screen space, y down. */
    val lightDirectionX: Float,
    val lightDirectionY: Float,
    /** Descent of a FACE enamel field toward graphite, in eighths. */
    val faceDescentEighths: Int,
    /** Descent of a PLATE field toward graphite, in eighths. */
    val plateDescentEighths: Int,
    /** Whether a warm lit partner exists at all in this state. In DARK
     *  the lamp is out and NO warm read survives; in DAY no warm partner
     *  exists in the model — relief is by recess shadow only. */
    val warmLitPartner: Boolean,
    /** Cap on lit-side blending, in eighths (enamel's own cap is lower:
     *  see Relight.ENAMEL_LIT_CAP_EIGHTHS). */
    val maxLitEighths: Int,
    /** DARK's law: subtraction only — nothing is ever brightened. */
    val subtractionOnly: Boolean
)

object Relight {

    /** Enamel's lit blend cap: the lamp's warmth reaches the enamel at
     *  ≤1/4 (2/8), L-SIGN only (RELIGHT §2, enamel row). */
    const val ENAMEL_LIT_CAP_EIGHTHS = 2

    fun paramsFor(state: RelightState): RelightStateParams = when (state) {
        // L-SIGN — lamp full and warm, raking from the upper left at a
        // low angle (-0.83, -0.55). Relief at full strength, no descent.
        RelightState.SIGNING -> RelightStateParams(
            lightDirectionX = -0.83f, lightDirectionY = -0.55f,
            faceDescentEighths = 0, plateDescentEighths = 0,
            warmLitPartner = true, maxLitEighths = 8, subtractionOnly = false
        )
        // L-SEALED — poverty on purpose: the whole face mass descends
        // 3/8 toward graphite; the warm family reaches the door only as
        // spill (lit cap 1/8). The plate is carried exact and unlit.
        RelightState.SEALED -> RelightStateParams(
            lightDirectionX = -0.83f, lightDirectionY = -0.55f,
            faceDescentEighths = 3, plateDescentEighths = 0,
            warmLitPartner = true, maxLitEighths = 1, subtractionOnly = false
        )
        // L-DARK — lamp out; subtraction only. Face descent is per class
        // (enamel 5/8, gold 1/2 — see stateSurface); plate descends 1/8.
        RelightState.DARK -> RelightStateParams(
            lightDirectionX = 0f, lightDirectionY = 0f,
            faceDescentEighths = 5, plateDescentEighths = 1,
            warmLitPartner = false, maxLitEighths = 0, subtractionOnly = true
        )
        // L-DAY — plain, unwarmed, overhead light (0, -1). Relief by
        // recess shadow only: no warm partner, no promotion, no theatre.
        RelightState.DAY -> RelightStateParams(
            lightDirectionX = 0f, lightDirectionY = -1f,
            faceDescentEighths = 0, plateDescentEighths = 0,
            warmLitPartner = false, maxLitEighths = 0, subtractionOnly = false
        )
    }

    /** The material classes the filed partner table names. A colour too
     *  far from every classed ink is OTHER — and OTHER's law is lead's
     *  law: no change in any state; it reads by shape. */
    enum class InkClass { ENAMEL, GOLD, GRAPHITE, OTHER }

    private const val CLASSIFY_MAX_DISTANCE = 0.38f

    fun classify(color: Color): InkClass {
        fun dist2(c: Color): Float {
            val dr = color.red - c.red
            val dg = color.green - c.green
            val db = color.blue - c.blue
            return dr * dr + dg * dg + db * db
        }
        val nearest = listOf(
            InkClass.ENAMEL to dist2(InkViolet),
            InkClass.GOLD to dist2(InkGold),
            InkClass.GRAPHITE to dist2(InkGraphite)
        ).minByOrNull { it.second }!!
        return if (sqrt(nearest.second) <= CLASSIFY_MAX_DISTANCE) nearest.first else InkClass.OTHER
    }

    /** Relative luminance (sRGB-linearised) — the surface's own value
     *  the relief field is derived from. */
    fun luminance(color: Color): Float {
        fun lin(ch: Float): Float =
            if (ch <= 0.04045f) ch / 12.92f else Math.pow(((ch + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        return 0.2126f * lin(color.red) + 0.7152f * lin(color.green) + 0.0722f * lin(color.blue)
    }

    /** The only derivation the law allows: [from] blended toward [to] at
     *  a whole number of eighths (0..8). Quantised by construction. */
    fun blendEighth(from: Color, to: Color, eighths: Int): Color {
        val steps = eighths.coerceIn(0, 8)
        if (steps == 0) return from
        if (steps == 8) return to
        return lerp(from, to, steps / 8f)
    }

    /** Descent toward the graphite floor at a printed fraction. */
    fun descend(color: Color, eighths: Int): Color = blendEighth(color, InkGraphite, eighths)

    /** The lit partner of a mark, per the filed table:
     *  enamel/gold warm toward shellac orange; a graphite cut's lip is
     *  the FIELD ink it is cut into, catching light; OTHER never moves.
     *  Where the state has no warm partner (DARK, DAY), the lit partner
     *  is the mark itself — relief comes from the shadow side alone. */
    fun litPartner(mark: Color, field: Color, state: RelightState): Color {
        if (!paramsFor(state).warmLitPartner) return mark
        return when (classify(mark)) {
            InkClass.ENAMEL -> InkOrange
            InkClass.GOLD -> InkOrange
            InkClass.GRAPHITE -> field
            InkClass.OTHER -> mark
        }
    }

    /** The shadow partner: the graphite floor — except graphite itself,
     *  whose floor does not darken past graphite, and OTHER, unmoved. */
    fun shadowPartner(mark: Color): Color = when (classify(mark)) {
        InkClass.GRAPHITE -> mark
        InkClass.OTHER -> mark
        else -> InkGraphite
    }

    /** One mark, lit: [gradient] is the mark's own luminance gradient
     *  projected onto the state's light direction, in -1..1 (positive =
     *  light-facing lip, negative = shadow side). The blend magnitude is
     *  quantised to whole eighths and capped by the state's law —
     *  including enamel's ≤1/4 cap and DARK's subtraction-only rule. */
    fun relief(mark: Color, field: Color, gradient: Float, state: RelightState): Color {
        val params = paramsFor(state)
        val g = gradient.coerceIn(-1f, 1f)
        return when {
            g > 0f -> {
                if (params.subtractionOnly || params.maxLitEighths == 0) {
                    mark
                } else {
                    var cap = params.maxLitEighths
                    if (classify(mark) == InkClass.ENAMEL) cap = minOf(cap, ENAMEL_LIT_CAP_EIGHTHS)
                    val steps = (g * cap).roundToInt().coerceIn(0, cap)
                    blendEighth(mark, litPartner(mark, field, state), steps)
                }
            }
            g < 0f -> {
                val steps = (-g * 8f).roundToInt().coerceIn(0, 8)
                blendEighth(mark, shadowPartner(mark), steps)
            }
            else -> mark
        }
    }

    /** A whole surface's mass under a state, before per-mark relief:
     *  the filed descents. SEALED descends the face 3/8 and carries the
     *  plate exact; DARK descends per class (below); DAY and SIGNING
     *  descend nothing. OTHER-class colours never move (lead's law). */
    fun stateSurface(base: Color, state: RelightState, surface: RelightSurface): Color {
        val params = paramsFor(state)
        if (classify(base) == InkClass.OTHER) return base
        if (state == RelightState.DARK && surface == RelightSurface.FACE) {
            // Per-class L-DARK descents (RELIGHT §3): enamel 5/8, door
            // gold 1/2, graphite already at the floor.
            return when (classify(base)) {
                InkClass.ENAMEL -> descend(base, 5)
                InkClass.GOLD -> descend(base, 4)
                else -> base
            }
        }
        val eighths = if (surface == RelightSurface.FACE) {
            params.faceDescentEighths
        } else {
            params.plateDescentEighths
        }
        return descend(base, eighths)
    }

    // ---- L-SEALED's two promoted reads, and no third (RELIGHT §3) ----

    /** (i) The engraved lifting hour at resting brightness: exact
     *  graphite, holding while the field around it descends. */
    fun sealedHourMark(): Color = InkGraphite

    /** (ii) The pinstripe glint: exact gold, held. */
    fun sealedPinstripe(): Color = InkGold

    /** The pinstripe's light-facing lip: warmed ONE 1/8 step toward
     *  orange — spill, not lamp. */
    fun sealedPinstripeLitLip(): Color = blendEighth(InkGold, InkOrange, 1)
}

/** The session's relight state, provided by the theme. Surfaces read
 *  the state and ask [Relight] for their colours; a surface that bakes
 *  a per-state colour of its own has forked the law. */
val LocalRelightState = compositionLocalOf { RelightState.SIGNING }
