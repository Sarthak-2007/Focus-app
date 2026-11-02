package com.example.ui.strongroom

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * THE RING — DESIGNED FRESH IN-APP (Audit A G-3).
 *
 * The ring + hour as drawn was rejected twice by Sarthak's eye
 * (sitting walk records 2 and 3: L3 SENT BACK, then SENT BACK
 * again). That render species is NOT ported, consulted, or
 * reskinned anywhere in this file: what stands here is a new
 * drawing, built as pure geometry in Compose-canvas terms —
 * engraved hour figures (Anonymous Pro 700, set as outlines on
 * the curve) on a ring whose proportions carry R 26.71 mm as a
 * relative DRAWING figure. Its verdict seat is Slice 4 (VISUAL
 * DESIGN, TO GRADE), judged by his eye in the running app — so
 * this geometry is built at full craft now, not as a placeholder:
 * every number below is derived from the filed law, and the
 * derivations are stated so the Slice 4 judgement can re-derive
 * them.
 *
 * THE LAW CARRIED (Stage 5 type-system, as composed):
 *  - FIG-1: the lifting hour is HH:MM — 5 glyphs, tabular lining,
 *    fixed cells, invariant width for every hour that can be set.
 *  - EL-1: fixed cells, no kerning — tracking is the cell advance,
 *    set once per surface.
 *  - EL-2: on the curve, tracking opens one step over the flat,
 *    measured as arc length at baseline radius.
 *  - EL-4/EL-5 + C-1: engraved settings sit one weight grade above
 *    master (700 is the face's master for figures and the grade
 *    the ring is drawn at — the font weight is the renderer's
 *    act; the geometry's act is the cell discipline), hairlines
 *    prohibited, counters enlarged — drawing consequences carried
 *    by the renderer, not by these coordinates.
 *  - A-3.4 (the hour's ring-radius duty): the 5-glyph line at
 *    ENG-1 measures 46.2 mm of arc at the true 0.5459em advance;
 *    the cut proceeds at ENG-1 only if the filed radius is
 *    ≥ 24 mm (arc ≤ 110.3°, end tilt ≤ 55.2°). R 26.71 mm filed
 *    here satisfies the duty, with the EL-2 tracking opening
 *    included in this file's arc — checked by test, not asserted.
 *  - NO FILL is permanent (G-3): the ring is a band of rules and
 *    figures on the ground — nothing in this geometry describes a
 *    filled disc, and the renderer draws strokes only.
 *
 * Units: millimetres of DRAWING space. The renderer scales the
 * whole drawing uniformly to its canvas; proportions — never
 * physical claims — are what the millimetres carry (G-3, G-7:
 * UNPROVEN-ON-DEVICE for anything a physical reading would add).
 */
object RingGeometry {

    /** The ring's baseline radius — the filed drawing figure. */
    const val RADIUS_MM = 26.71

    /** ENG-1's figure size: PRT-5, 48pt = 48/72 in = 16.9333 mm. */
    const val FONT_SIZE_MM = 48.0 / 72.0 * 25.4

    /** Anonymous Pro's one true advance: 1118 / 2048 unitsPerEm
     *  (type-system Amendment A-2, verified from the font file —
     *  never the 0.60em that belongs to another face). */
    const val ADVANCE_EM = 1118.0 / 2048.0

    /** The flat tracking for hour figures (Stage 5 §5.3): +0.02em. */
    const val FLAT_TRACKING_EM = 0.02

    /** EL-2: on the curve the tracking opens ONE STEP over the
     *  flat. The printed tracking ladder (§5.3) steps
     *  0.02 → 0.04 → 0.05 → 0.06em; one step over 0.02 is 0.04em. */
    const val CURVE_TRACKING_EM = 0.04

    /** Glyphs in the one hour-form: H H : M M. */
    const val GLYPH_COUNT = 5

    /** One cell's arc length at baseline radius: the fixed advance
     *  plus the curve-opened tracking, both as arc (EL-1/EL-2). */
    val CELL_ARC_MM: Double = FONT_SIZE_MM * (ADVANCE_EM + CURVE_TRACKING_EM)

    /** The whole hour's arc, tracking included. A-3.4's 46.2 mm is
     *  this figure before the EL-2 opening (5 × advance alone). */
    val TOTAL_ARC_MM: Double = CELL_ARC_MM * GLYPH_COUNT

    /** The arc with no tracking at all — A-3.4's filed measure. */
    val BARE_ARC_MM: Double = FONT_SIZE_MM * ADVANCE_EM * GLYPH_COUNT

    /** Total sweep of the hour about the ring's centre, degrees. */
    val SWEEP_DEG: Double = TOTAL_ARC_MM / RADIUS_MM * 180.0 / PI

    /** The end glyphs' tilt from vertical — A-3.4's ≤ 55.2° bound
     *  is this figure's ceiling. */
    val END_TILT_DEG: Double = SWEEP_DEG / 2.0

    /** The band's radial thickness: the figure size plus air above
     *  and below the baseline — the glyphs sit ON the ring, their
     *  bodies inside the band, never astride its rules. Inner and
     *  outer rule radii are derived from this, symmetrically. */
    val BAND_MM: Double = FONT_SIZE_MM * 1.42

    val OUTER_RADIUS_MM: Double = RADIUS_MM + BAND_MM / 2.0
    val INNER_RADIUS_MM: Double = RADIUS_MM - BAND_MM / 2.0

    /**
     * One engraved glyph's placement. [offsetDeg] is measured from
     * the ring's top, clockwise positive (the reading direction).
     * [unitX]/[unitY] are the glyph centre in unit space — the
     * ring's centre is (0, 0), the baseline radius is 1.0, y grows
     * DOWNWARD as a canvas's does — so a renderer multiplies by
     * its pixel radius and adds its centre. [rotationDeg] is the
     * glyph's own rotation about its centre: engraved on the
     * curve, each glyph's baseline follows the arc, so the glyph
     * turns with its offset.
     */
    data class GlyphPlacement(
        val char: Char,
        val offsetDeg: Double,
        val unitX: Double,
        val unitY: Double,
        val rotationDeg: Double
    )

    /** The lawful hour-form: HH:MM, 00–23 : 00–59, always 5 glyphs. */
    fun isLawfulHour(hour: String): Boolean {
        if (hour.length != GLYPH_COUNT || hour[2] != ':') return false
        val h = hour.substring(0, 2).toIntOrNull() ?: return false
        val m = hour.substring(3, 5).toIntOrNull() ?: return false
        return h in 0..23 && m in 0..59
    }

    /**
     * The five placements for [hour], centred on the ring's top:
     * the middle glyph (the colon) stands at offset 0, the pairs
     * step out by whole fixed cells (EL-1 — equal angular steps,
     * because equal arcs on one radius are equal angles).
     */
    fun placements(hour: String): List<GlyphPlacement> {
        require(isLawfulHour(hour)) { "Not a lawful HH:MM hour: $hour" }
        val cellDeg = CELL_ARC_MM / RADIUS_MM * 180.0 / PI
        return hour.mapIndexed { i, c ->
            val offset = (i - (GLYPH_COUNT - 1) / 2.0) * cellDeg
            val rad = offset * PI / 180.0
            GlyphPlacement(
                char = c,
                offsetDeg = offset,
                unitX = sin(rad),
                unitY = -cos(rad),
                rotationDeg = offset
            )
        }
    }

    /** The gold chord's span: the arc the figures stand on,
     *  returned as (startOffsetDeg, sweepDeg) in the same
     *  from-the-top measure as the placements — the renderer
     *  strikes the chord exactly under the hour, no wider. */
    fun chordSpan(): Pair<Double, Double> = (-SWEEP_DEG / 2.0) to SWEEP_DEG
}
