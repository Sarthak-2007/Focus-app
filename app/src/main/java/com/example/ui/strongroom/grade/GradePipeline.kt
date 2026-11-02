package com.example.ui.strongroom.grade

import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * STAGE 10 PRE-BUILD — THE GRADE RENDERING PIPELINE (BUILD-SR-10, T24).
 *
 * The spatial half of RELIGHT, in production code. P0's `Relight.kt`
 * (BUILD-SR-1, PR #842) is the settled mechanism's colour half: partner
 * inks, 1/8 quantisation, per-state descents — with no spatial field.
 * DESIGNER-SR-1 named the missing half exactly: a heightfield built
 * from the surface itself, its luminance gradient projected onto the
 * state's light direction, blends struck per pixel. This file is that
 * half, as pure JVM-testable law shaped value-for-value to P0's filed
 * parameter table, so at Stage 10 composition the surfaces consume
 * ONE mechanism — this spatial field driving P0's colour law — and
 * the faces lane's divergent local table (toward white, unquantised,
 * no descent, DARK lit) is never ported (LEAD-SLICE-4's collision;
 * V-3, the RELIGHT light kill, stands).
 *
 * No colour outside the seven inks is ever produced: every derived
 * tone is a named ink blended toward another named ink at a whole
 * number of eighths. No green. Device behaviour UNPROVEN-ON-DEVICE
 * (G-7); every physical figure below UNPROVEN (G-4).
 */

/** The seven inks as data (palette ONLY — F-1). ARGB, alpha FF. */
enum class GradeInk(val argb: Long) {
    MAGENTA(0xFFF04FCB),
    VIOLET(0xFF493EA4),
    GOLD(0xFFE2BC39),
    ROSE(0xFFC84481),
    ORANGE(0xFFE47E35),
    BLUE(0xFF377ED0),
    GRAPHITE(0xFF34323B),
    ;

    val r: Int get() = ((argb shr 16) and 0xFF).toInt()
    val g: Int get() = ((argb shr 8) and 0xFF).toInt()
    val b: Int get() = (argb and 0xFF).toInt()
}

/** A colour in flight: three channels 0..255, always the blend of two
 *  named inks at a printed fraction — never a new token. */
data class GradeColour(val r: Int, val g: Int, val b: Int) {
    fun toArgb(): Long =
        (0xFFL shl 24) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()

    companion object {
        fun of(ink: GradeInk): GradeColour = GradeColour(ink.r, ink.g, ink.b)
    }
}

enum class GradeState { SIGNING, SEALED, DARK, DAY }
enum class GradeSurface { FACE, PLATE }

/** Per-state parameters, mirroring P0 `Relight.paramsFor` value for
 *  value (read firsthand from `build-wt-foundation`): light direction,
 *  descents in eighths, warm partner's existence, lit cap, DARK's
 *  subtraction-only law. Stage 10 tunes THESE numbers in the running
 *  app; the mechanism does not change. */
data class GradeStateParams(
    val lightDirectionX: Float,
    val lightDirectionY: Float,
    val faceDescentEighths: Int,
    val plateDescentEighths: Int,
    val warmLitPartner: Boolean,
    val maxLitEighths: Int,
    val subtractionOnly: Boolean,
)

object GradePipeline {

    const val ENAMEL_LIT_CAP_EIGHTHS: Int = 2

    fun paramsFor(state: GradeState): GradeStateParams = when (state) {
        GradeState.SIGNING -> GradeStateParams(-0.83f, -0.55f, 0, 0, true, 8, false)
        GradeState.SEALED -> GradeStateParams(-0.83f, -0.55f, 3, 0, true, 1, false)
        GradeState.DARK -> GradeStateParams(0f, 0f, 5, 1, false, 0, true)
        GradeState.DAY -> GradeStateParams(0f, -1f, 0, 0, false, 0, false)
    }

    enum class InkClass { ENAMEL, GOLD, GRAPHITE, OTHER }

    private const val CLASSIFY_MAX_DISTANCE: Double = 0.38

    fun classify(colour: GradeColour): InkClass {
        fun dist(ink: GradeInk): Double {
            val dr = colour.r / 255.0 - ink.r / 255.0
            val dg = colour.g / 255.0 - ink.g / 255.0
            val db = colour.b / 255.0 - ink.b / 255.0
            return sqrt(dr * dr + dg * dg + db * db)
        }
        val nearest = listOf(
            InkClass.ENAMEL to dist(GradeInk.VIOLET),
            InkClass.GOLD to dist(GradeInk.GOLD),
            InkClass.GRAPHITE to dist(GradeInk.GRAPHITE),
        ).minByOrNull { it.second }!!
        return if (nearest.second <= CLASSIFY_MAX_DISTANCE) nearest.first else InkClass.OTHER
    }

    /** Relative luminance — the surface's own value the relief field
     *  is derived from (same sRGB linearisation as P0). */
    fun luminance(colour: GradeColour): Float {
        fun lin(ch: Int): Float {
            val v = ch / 255f
            return if (v <= 0.04045f) v / 12.92f
            else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        }
        return 0.2126f * lin(colour.r) + 0.7152f * lin(colour.g) + 0.0722f * lin(colour.b)
    }

    /** The only derivation the law allows: whole eighths, 0..8. */
    fun blendEighth(from: GradeColour, to: GradeColour, eighths: Int): GradeColour {
        val steps = eighths.coerceIn(0, 8)
        if (steps == 0) return from
        if (steps == 8) return to
        val f = steps / 8f
        fun ch(a: Int, b: Int): Int = (a + (b - a) * f).roundToInt().coerceIn(0, 255)
        return GradeColour(ch(from.r, to.r), ch(from.g, to.g), ch(from.b, to.b))
    }

    fun descend(colour: GradeColour, eighths: Int): GradeColour =
        blendEighth(colour, GradeColour.of(GradeInk.GRAPHITE), eighths)

    fun litPartner(mark: GradeColour, field: GradeColour, state: GradeState): GradeColour {
        if (!paramsFor(state).warmLitPartner) return mark
        return when (classify(mark)) {
            InkClass.ENAMEL, InkClass.GOLD -> GradeColour.of(GradeInk.ORANGE)
            InkClass.GRAPHITE -> field
            InkClass.OTHER -> mark
        }
    }

    fun shadowPartner(mark: GradeColour): GradeColour = when (classify(mark)) {
        InkClass.GRAPHITE, InkClass.OTHER -> mark
        else -> GradeColour.of(GradeInk.GRAPHITE)
    }

    /** One mark, lit — P0's `relief` law, restated on data so the
     *  shader and the reference renderer strike identical colours. */
    fun relief(mark: GradeColour, field: GradeColour, gradient: Float, state: GradeState): GradeColour {
        val params = paramsFor(state)
        val g = gradient.coerceIn(-1f, 1f)
        return when {
            g > 0f -> {
                if (params.subtractionOnly || params.maxLitEighths == 0) mark
                else {
                    var cap = params.maxLitEighths
                    if (classify(mark) == InkClass.ENAMEL) {
                        cap = minOf(cap, ENAMEL_LIT_CAP_EIGHTHS)
                    }
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

    /** A whole surface's mass under a state, before per-mark relief —
     *  P0's `stateSurface`, including DARK's per-class descents. */
    fun stateSurface(base: GradeColour, state: GradeState, surface: GradeSurface): GradeColour {
        if (classify(base) == InkClass.OTHER) return base
        if (state == GradeState.DARK && surface == GradeSurface.FACE) {
            return when (classify(base)) {
                InkClass.ENAMEL -> descend(base, 5)
                InkClass.GOLD -> descend(base, 4)
                else -> base
            }
        }
        val params = paramsFor(state)
        val eighths = if (surface == GradeSurface.FACE) {
            params.faceDescentEighths
        } else {
            params.plateDescentEighths
        }
        return descend(base, eighths)
    }

    // ---- L-SEALED's two promoted reads, and no third ----
    fun sealedHourMark(): GradeColour = GradeColour.of(GradeInk.GRAPHITE)
    fun sealedPinstripe(): GradeColour = GradeColour.of(GradeInk.GOLD)
    fun sealedPinstripeLitLip(): GradeColour =
        blendEighth(GradeColour.of(GradeInk.GOLD), GradeColour.of(GradeInk.ORANGE), 1)
}

/** Finish depth — the grade's physical figures as data (Audit A G-4:
 *  rendered at best-judgment values, every one stamped UNPROVEN, each
 *  a design figure in mm-equivalent visual weight, never a claim
 *  about any real surface). One depth per face (SL-3) is enforced by
 *  [depthFor]: depth varies by KIND of cut, never by the day's facts
 *  and never by a hole's grade. */
object FinishDepth {
    /** Groove depth (Audit A G-4's best-judgment value). UNPROVEN. */
    const val GROOVE_DEPTH: Float = 0.30f

    /** The pinstripe's fineness at the test condition. UNPROVEN. */
    const val PINSTRIPE_WIDTH: Float = 0.18f

    /** Struck figures' one depth, all figures on a face. UNPROVEN. */
    const val STRIKE_DEPTH: Float = 0.24f

    /** Punched holes' one depth — identical for REACH/HELD/FLOOR. */
    const val PUNCH_DEPTH: Float = 0.30f

    enum class CutKind { GROOVE, PINSTRIPE, STRIKE, PUNCH }

    fun depthFor(kind: CutKind): Float = when (kind) {
        CutKind.GROOVE -> GROOVE_DEPTH
        CutKind.PINSTRIPE -> PINSTRIPE_WIDTH
        CutKind.STRIKE -> STRIKE_DEPTH
        CutKind.PUNCH -> PUNCH_DEPTH
    }
}

/**
 * The surface's own heightfield, built once per face from its cuts —
 * DESIGNER-SR-1's construction: grooves, the pinstripe, a punch are
 * depressions of [FinishDepth] depth in an otherwise flat slab, and
 * relief is read from the LUMINANCE of the rendered base, not from
 * the geometry directly, so the light belongs to the surface as drawn.
 * Coordinates are field fractions 0..1; the field is deterministic —
 * the same face builds the same field on every visit.
 */
class FaceHeightfield(
    val grooveRadii: List<Float> = listOf(0.36f),
    val pinstripeY: Float = 0.08f,
    val punches: List<Punch> = emptyList(),
) {
    data class Punch(val cx: Float, val cy: Float, val radiusFraction: Float)

    /** Height at a point: 0 = slab face, negative = cut depth. */
    fun heightAt(x: Float, y: Float): Float {
        var h = 0f
        val cx = x - 0.5f
        val cy = y - 0.5f
        val radius = sqrt(cx * cx + cy * cy)
        for (groove in grooveRadii) {
            val d = kotlin.math.abs(radius - groove)
            if (d < 0.012f) h = minOf(h, -FinishDepth.GROOVE_DEPTH * (1f - d / 0.012f))
        }
        val stripeD = kotlin.math.abs(y - pinstripeY)
        if (stripeD < 0.006f) {
            h = minOf(h, -FinishDepth.PINSTRIPE_WIDTH * (1f - stripeD / 0.006f))
        }
        for (punch in punches) {
            val dx = x - punch.cx
            val dy = y - punch.cy
            val d = sqrt(dx * dx + dy * dy)
            if (d < punch.radiusFraction) h = minOf(h, -FinishDepth.PUNCH_DEPTH)
        }
        return h
    }
}

/** The reference renderer: the pipeline's executable form and the
 *  AGSL shader's test oracle. Given a base field (each pixel's unlit
 *  ink, as data), it strikes the graded field: state descent first,
 *  then per-pixel relief from the base's own luminance gradient
 *  projected onto the state's light direction — plus the heightfield's
 *  contribution where a cut crosses the pixel. */
object GradeFieldRenderer {

    fun gradientAt(
        base: Array<Array<GradeColour>>,
        x: Int,
        y: Int,
        heightfield: FaceHeightfield?,
        state: GradeState,
    ): Float {
        val w = base[0].size
        val h = base.size
        fun lum(px: Int, py: Int): Float =
            GradePipeline.luminance(base[py.coerceIn(0, h - 1)][px.coerceIn(0, w - 1)])
        val gx = lum(x + 1, y) - lum(x - 1, y)
        val gy = lum(x, y + 1) - lum(x, y - 1)
        val params = GradePipeline.paramsFor(state)
        var projected = gx * params.lightDirectionX + gy * params.lightDirectionY
        if (heightfield != null && w > 1 && h > 1) {
            val fx = x.toFloat() / (w - 1)
            val fy = y.toFloat() / (h - 1)
            val step = 1f / (w - 1)
            val hx = heightfield.heightAt(fx + step, fy) - heightfield.heightAt(fx - step, fy)
            val hy = heightfield.heightAt(fx, fy + step) - heightfield.heightAt(fx, fy - step)
            projected += (hx * params.lightDirectionX + hy * params.lightDirectionY) * 2f
        }
        return projected.coerceIn(-1f, 1f)
    }

    fun render(
        base: Array<Array<GradeColour>>,
        state: GradeState,
        surface: GradeSurface,
        heightfield: FaceHeightfield? = null,
    ): Array<Array<GradeColour>> {
        val h = base.size
        val w = base[0].size
        return Array(h) { y ->
            Array(w) { x ->
                val descended = GradePipeline.stateSurface(base[y][x], state, surface)
                val g = gradientAt(base, x, y, heightfield, state)
                GradePipeline.relief(descended, descended, g, state)
            }
        }
    }
}
