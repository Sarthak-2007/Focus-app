package com.example

import com.example.ui.strongroom.RingGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlin.math.sqrt

/**
 * THE FRESH RING's geometry contract (ui/strongroom/RingGeometry.kt;
 * Audit A G-3 — designed fresh in-app, the twice-rejected render
 * species neither ported nor consulted):
 *
 *  - FIG-1/EL-1: the hour is HH:MM, five glyphs in fixed cells —
 *    equal angular steps, invariant across every lawful hour.
 *  - A-3.4's ring-radius duty: R ≥ 24 mm, the bare arc is the
 *    filed 46.2 mm, sweep ≤ 110.3°, end tilt ≤ 55.2° — WITH the
 *    EL-2 curve-opened tracking included in this drawing's arc.
 *  - EL-2: the curve tracking (0.04em) opens exactly one printed
 *    ladder step over the flat (0.02em).
 *  - Placements sit on the unit circle, symmetric about the top,
 *    each glyph rotated by its own offset.
 */
class StrongroomRingGeometryTest {

    @Test
    fun filedRadius_satisfiesTheRingRadiusDuty() {
        assertEquals(26.71, RingGeometry.RADIUS_MM, 0.0)
        assertTrue(RingGeometry.RADIUS_MM >= 24.0)
    }

    @Test
    fun bareArc_isTheFiled46Point2Millimetres() {
        // A-3.4: 5 glyphs × 48pt × the true 0.5459em advance.
        assertEquals(46.2, RingGeometry.BARE_ARC_MM, 0.15)
        assertEquals(1118.0 / 2048.0, RingGeometry.ADVANCE_EM, 1e-12)
    }

    @Test
    fun sweepAndEndTilt_stayInsideTheDutyBounds_withTrackingIncluded() {
        assertTrue(
            "sweep ${RingGeometry.SWEEP_DEG} must be ≤ 110.3",
            RingGeometry.SWEEP_DEG <= 110.3
        )
        assertTrue(
            "end tilt ${RingGeometry.END_TILT_DEG} must be ≤ 55.2",
            RingGeometry.END_TILT_DEG <= 55.2
        )
        // The EL-2 opening is real: the tracked arc is the wider one.
        assertTrue(RingGeometry.TOTAL_ARC_MM > RingGeometry.BARE_ARC_MM)
    }

    @Test
    fun curveTracking_opensOneLadderStepOverTheFlat() {
        assertEquals(0.02, RingGeometry.FLAT_TRACKING_EM, 0.0)
        assertEquals(0.04, RingGeometry.CURVE_TRACKING_EM, 0.0)
    }

    @Test
    fun placements_fiveGlyphs_centredOnTop_symmetricOnTheCircle() {
        val p = RingGeometry.placements("07:30")
        assertEquals(5, p.size)
        assertEquals("07:30", p.map { it.char }.joinToString(""))
        // The colon stands at the top, dead centre.
        assertEquals(0.0, p[2].offsetDeg, 1e-9)
        assertEquals(0.0, p[2].unitX, 1e-9)
        assertEquals(-1.0, p[2].unitY, 1e-9)
        // Symmetry: the pairs mirror about the top.
        assertEquals(-p[4].unitX, p[0].unitX, 1e-9)
        assertEquals(-p[3].unitX, p[1].unitX, 1e-9)
        assertEquals(p[4].unitY, p[0].unitY, 1e-9)
        // Every glyph centre lies on the baseline circle, and each
        // glyph is rotated by exactly its own offset (EL-2's curve).
        p.forEach { g ->
            assertEquals(1.0, sqrt(g.unitX * g.unitX + g.unitY * g.unitY), 1e-9)
            assertEquals(g.offsetDeg, g.rotationDeg, 0.0)
        }
    }

    @Test
    fun placements_fixedCells_equalSteps_invariantAcrossHours() {
        val a = RingGeometry.placements("00:00")
        val b = RingGeometry.placements("23:59")
        // FIG-1: invariant width — the cells never move with the hour.
        a.zip(b).forEach { (x, y) -> assertEquals(x.offsetDeg, y.offsetDeg, 0.0) }
        // EL-1: equal steps between neighbours.
        val steps = a.zipWithNext { x, y -> y.offsetDeg - x.offsetDeg }
        steps.forEach { assertEquals(steps.first(), it, 1e-9) }
    }

    @Test
    fun lawfulHourForm_acceptsTheDayAndRefusesTheRest() {
        assertTrue(RingGeometry.isLawfulHour("00:00"))
        assertTrue(RingGeometry.isLawfulHour("23:59"))
        assertFalse(RingGeometry.isLawfulHour("24:00"))
        assertFalse(RingGeometry.isLawfulHour("07:60"))
        assertFalse(RingGeometry.isLawfulHour("7:30"))
        assertFalse(RingGeometry.isLawfulHour("0730"))
        assertFalse(RingGeometry.isLawfulHour(""))
        try {
            RingGeometry.placements("24:00")
            fail("an unlawful hour must not be placeable on the ring")
        } catch (expected: IllegalArgumentException) {
            // the ring refuses what the face cannot say
        }
    }

    @Test
    fun chordSpan_coversExactlyTheHourArc() {
        val (start, sweep) = RingGeometry.chordSpan()
        assertEquals(-RingGeometry.SWEEP_DEG / 2.0, start, 0.0)
        assertEquals(RingGeometry.SWEEP_DEG, sweep, 0.0)
        // And it ends where the last glyph's cell ends.
        val p = RingGeometry.placements("12:00")
        assertTrue(start <= p.first().offsetDeg)
        assertTrue(start + sweep >= p.last().offsetDeg)
    }
}
