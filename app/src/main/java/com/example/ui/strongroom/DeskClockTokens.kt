package com.example.ui.strongroom

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * DESK CLOCK TOKENS — BUILD-SR-6's lane-local seam file for P8,
 * filed under the seam named in this lane's opening line
 * (ascent/build-first-usable/work/build-sr-6/opening-line.md,
 * difference 3): P0 FOUNDATION (BUILD-SR-1, PR #842) is not on main
 * yet, so every colour/type/light value below is derived firsthand
 * from the filed law — Audit A §4 (the seven inks, kept ground &
 * finish) and Stage 5 §1.3 (FRM-1) — and shaped to P0's filed
 * contract (its `Relight.kt` and `Type.kt`, read firsthand on
 * `agent/build-sr-foundation`). When P0 lands, this file is
 * re-pointed in one act — surfaces consume [ClockInks],
 * [ClockFrm1] and [relight] here, never raw values — and any
 * difference is named upward before merging.
 *
 * PALETTE LAW: the seven inks are the whole palette —
 * #F04FCB · #493EA4 · #E2BC39 · #C84481 · #E47E35 · #377ED0 ·
 * #34323B — never green. Every other tone the clock shows is one
 * of these inks moved along its own luminance by the RELIGHT rule
 * below — a derivation, never a new hex.
 */
object ClockInks {
    val Magenta = Color(0xFFF04FCB)
    val Violet = Color(0xFF493EA4)
    val Gold = Color(0xFFE2BC39)
    val Rose = Color(0xFFC84481)
    val Ember = Color(0xFFE47E35)
    val Blue = Color(0xFF377ED0)
    val Graphite = Color(0xFF34323B)

    /** The palette, whole — the census the P8 tests read. */
    val All: List<Color> = listOf(Magenta, Violet, Gold, Rose, Ember, Blue, Graphite)

    /** The regulator's ground: the room's deep-violet enamel. */
    val Ground = Violet

    /** The rim and the forming record: gold leaf. */
    val Leaf = Gold

    /** Hands, ticks, punch marks: the dark line-work ink. */
    val LineWork = Graphite

    /** A lived shaped gap on the rim: the warm ember ink. */
    val GapWarmth = Ember
}

/**
 * FRM-1 — the forming record's step (Stage 5 §1.3): Atkinson
 * Hyperlegible 400 at the record's body step (SCR-2 class — a size
 * equivalence only, per Amendment A-6.7; no screen law reaches the
 * forming kind through it). The desk clock's surface draws its
 * record and carries NO text today; this style stands as the filed
 * step any future FRM-1 unit would be set at, so no later surface
 * invents a size. It is spent by nothing else in this lane.
 */
val ClockFrm1 = TextStyle(
    fontFamily = AtkinsonHyperlegibleFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

// ---------------------------------------------------------------------
// RELIGHT — P0's theme-level rendering law, ported faithfully at the
// seam (source: `Relight.kt` on agent/build-sr-foundation, PR #842;
// the mechanism is Audit A §4/§5 G-5's KEPT light: relief DERIVED
// FROM A SURFACE'S OWN LUMINANCE, distinct per state). Blends are
// quantised to whole eighths toward a named lawful ink — a derived
// colour is always an ink blended toward another ink at a printed
// fraction, never a new token. At P0's landing this port is deleted
// and the surfaces call P0's `Relight` directly.
// ---------------------------------------------------------------------

enum class ClockRelightState { SIGNING, SEALED, DARK, DAY }

object ClockRelight {

    const val ENAMEL_LIT_CAP_EIGHTHS = 2

    private data class Params(
        val faceDescentEighths: Int,
        val warmLitPartner: Boolean,
        val maxLitEighths: Int,
        val subtractionOnly: Boolean
    )

    private fun paramsFor(state: ClockRelightState): Params = when (state) {
        ClockRelightState.SIGNING -> Params(0, warmLitPartner = true, maxLitEighths = 8, subtractionOnly = false)
        ClockRelightState.SEALED -> Params(3, warmLitPartner = true, maxLitEighths = 1, subtractionOnly = false)
        ClockRelightState.DARK -> Params(5, warmLitPartner = false, maxLitEighths = 0, subtractionOnly = true)
        ClockRelightState.DAY -> Params(0, warmLitPartner = false, maxLitEighths = 0, subtractionOnly = false)
    }

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
            InkClass.ENAMEL to dist2(ClockInks.Violet),
            InkClass.GOLD to dist2(ClockInks.Gold),
            InkClass.GRAPHITE to dist2(ClockInks.Graphite)
        ).minByOrNull { it.second }!!
        return if (sqrt(nearest.second) <= CLASSIFY_MAX_DISTANCE) nearest.first else InkClass.OTHER
    }

    /** [from] blended toward [to] at a whole number of eighths. */
    fun blendEighth(from: Color, to: Color, eighths: Int): Color {
        val steps = eighths.coerceIn(0, 8)
        if (steps == 0) return from
        if (steps == 8) return to
        return lerp(from, to, steps / 8f)
    }

    fun descend(color: Color, eighths: Int): Color = blendEighth(color, ClockInks.Graphite, eighths)

    /** A whole surface's mass under a state, before per-mark relief:
     *  SEALED descends the face 3/8 toward graphite; DARK descends
     *  per class (enamel 5/8, gold 1/2); OTHER never moves. */
    fun stateSurface(base: Color, state: ClockRelightState): Color {
        if (classify(base) == InkClass.OTHER) return base
        if (state == ClockRelightState.DARK) {
            return when (classify(base)) {
                InkClass.ENAMEL -> descend(base, 5)
                InkClass.GOLD -> descend(base, 4)
                else -> base
            }
        }
        return descend(base, paramsFor(state).faceDescentEighths)
    }

    /**
     * One mark, lit. [gradient] is the mark's own luminance gradient
     * projected onto the light direction, -1..1 (positive = the
     * light-facing lip, from the upper left; negative = shadow side).
     * Magnitude is quantised to whole eighths and capped by the
     * state's law, enamel's ≤1/4 cap included; DARK brightens nothing.
     */
    fun relief(mark: Color, field: Color, gradient: Float, state: ClockRelightState): Color {
        val params = paramsFor(state)
        val g = gradient.coerceIn(-1f, 1f)
        return when {
            g > 0f -> {
                if (params.subtractionOnly || params.maxLitEighths == 0 || !params.warmLitPartner) {
                    mark
                } else {
                    var cap = params.maxLitEighths
                    if (classify(mark) == InkClass.ENAMEL) cap = minOf(cap, ENAMEL_LIT_CAP_EIGHTHS)
                    val steps = (g * cap).roundToInt().coerceIn(0, cap)
                    val partner = when (classify(mark)) {
                        InkClass.ENAMEL, InkClass.GOLD -> ClockInks.Ember
                        InkClass.GRAPHITE -> field
                        InkClass.OTHER -> mark
                    }
                    blendEighth(mark, partner, steps)
                }
            }
            g < 0f -> {
                val partner = when (classify(mark)) {
                    InkClass.GRAPHITE, InkClass.OTHER -> mark
                    else -> ClockInks.Graphite
                }
                val steps = (-g * 8f).roundToInt().coerceIn(0, 8)
                blendEighth(mark, partner, steps)
            }
            else -> mark
        }
    }
}
