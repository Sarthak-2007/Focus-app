package com.example

import com.example.ui.strongroom.HourFigures
import com.example.ui.strongroom.RingGeometry
import kotlin.math.ceil
import kotlin.math.hypot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * D-16 GATE CURE (BUILD-SR-15, final phase) — the covering test
 * for `ui/strongroom/HourFigures.kt`, TYPE-SR-2's drawn figures.
 *
 * This is a stem-cover test with substance, not an existence
 * check (METHODS-SR-4 AP-1/AP-5): it re-measures, from the
 * production path data itself, the separations TYPE-SR-2 filed
 * in `work/type-sr-2/verification.md` —
 *
 *  - T1: the rotated 6 against the 9 (and the rotated 9 against
 *    the 6) must differ by at least 15% of union ink — the FIG-1
 *    consult's floor. The filed measurement is 46.7% / 47.0%
 *    (IoU 0.533 / 0.530) against the master face's failing 5–7%.
 *    The raster here is this test's own (stroke centrelines
 *    stamped at the graver's width on a 7-unit grid), so the
 *    asserted bound is the consult's floor, not the filed point
 *    value — a drawing that degraded toward the font's pair
 *    (5–7%) fails here by name.
 *  - T3 spot pairs: the filed worst upright pair is 3/8 at IoU
 *    0.655; 3/8, 0/8 and 5/8 must each clear the same 15% floor.
 *  - T5: the colon's punches are 150 units square — wider than
 *    the one graver cut (132) — and the colon carries no stroke.
 *
 * Plus the consumption contract the ring relies on: the figure
 * set is exactly 0–9 + colon, the cell advance agrees with
 * RingGeometry's ADVANCE_EM, the pivot is the cell centre, and
 * every drawn point stands inside the cell (placement sanity —
 * a figure that escaped its cell would strike through its
 * neighbour on the ring).
 */
class HourFiguresTest {

    // ---- the test's own raster ----

    private data class Pt(val x: Double, val y: Double)

    private fun flatten(cmds: List<HourFigures.Cmd>): List<Pt> {
        val out = mutableListOf<Pt>()
        var cur = Pt(0.0, 0.0)
        var start = Pt(0.0, 0.0)
        fun cubic(p0: Pt, p1: Pt, p2: Pt, p3: Pt) {
            for (i in 1..24) {
                val t = i / 24.0
                val u = 1 - t
                out.add(
                    Pt(
                        u * u * u * p0.x + 3 * u * u * t * p1.x +
                            3 * u * t * t * p2.x + t * t * t * p3.x,
                        u * u * u * p0.y + 3 * u * u * t * p1.y +
                            3 * u * t * t * p2.y + t * t * t * p3.y
                    )
                )
            }
        }
        for (c in cmds) {
            when (c.op) {
                HourFigures.Op.M -> {
                    cur = Pt(c.a[0].toDouble(), c.a[1].toDouble())
                    start = cur
                    out.add(cur)
                }
                HourFigures.Op.L -> {
                    val next = Pt(c.a[0].toDouble(), c.a[1].toDouble())
                    for (i in 1..8) {
                        val t = i / 8.0
                        out.add(Pt(cur.x + (next.x - cur.x) * t, cur.y + (next.y - cur.y) * t))
                    }
                    cur = next
                }
                HourFigures.Op.C -> {
                    val p1 = Pt(c.a[0].toDouble(), c.a[1].toDouble())
                    val p2 = Pt(c.a[2].toDouble(), c.a[3].toDouble())
                    val p3 = Pt(c.a[4].toDouble(), c.a[5].toDouble())
                    cubic(cur, p1, p2, p3)
                    cur = p3
                }
                HourFigures.Op.Z -> {
                    for (i in 1..8) {
                        val t = i / 8.0
                        out.add(Pt(cur.x + (start.x - cur.x) * t, cur.y + (start.y - cur.y) * t))
                    }
                    cur = start
                }
            }
        }
        return out
    }

    private val cell = 7.0
    private val cols = ceil(HourFigures.ADVANCE_UNITS / cell).toInt() // 78
    private val rows = ceil(700.0 / cell).toInt() // 100

    /** Ink = grid cells within half the graver's cut of a stroke
     *  centreline sample, plus the punches' filled rects. */
    private fun ink(
        glyph: HourFigures.Glyph,
        rotate180: Boolean = false
    ): Set<Int> {
        val inked = mutableSetOf<Int>()
        val r = HourFigures.STROKE_UNITS / 2.0
        fun stamp(x: Double, y: Double) {
            val cx = x / cell
            val cy = y / cell
            val rad = r / cell
            val x0 = maxOf(0, (cx - rad).toInt())
            val x1 = minOf(cols - 1, (cx + rad).toInt())
            val y0 = maxOf(0, (cy - rad).toInt())
            val y1 = minOf(rows - 1, (cy + rad).toInt())
            for (gx in x0..x1) {
                for (gy in y0..y1) {
                    val dx = (gx + 0.5) * cell - x
                    val dy = (gy + 0.5) * cell - y
                    if (hypot(dx, dy) <= r) inked.add(gy * cols + gx)
                }
            }
        }
        fun place(p: Pt): Pt =
            if (rotate180) Pt(HourFigures.ADVANCE_UNITS - p.x, 700.0 - p.y) else p
        for (stroke in glyph.strokes) {
            for (p in flatten(stroke)) {
                val q = place(p)
                stamp(q.x, q.y)
            }
        }
        for (punch in glyph.punches) {
            val corners = listOf(
                Pt(punch.x.toDouble(), punch.y.toDouble()),
                Pt((punch.x + punch.w).toDouble(), (punch.y + punch.h).toDouble())
            ).map { place(it) }
            val x0 = minOf(corners[0].x, corners[1].x)
            val x1 = maxOf(corners[0].x, corners[1].x)
            val y0 = minOf(corners[0].y, corners[1].y)
            val y1 = maxOf(corners[0].y, corners[1].y)
            for (gx in 0 until cols) {
                for (gy in 0 until rows) {
                    val px = (gx + 0.5) * cell
                    val py = (gy + 0.5) * cell
                    if (px in x0..x1 && py in y0..y1) inked.add(gy * cols + gx)
                }
            }
        }
        return inked
    }

    private fun separation(a: Set<Int>, b: Set<Int>): Double {
        val union = a union b
        if (union.isEmpty()) return 0.0
        return 1.0 - (a intersect b).size.toDouble() / union.size.toDouble()
    }

    // ---- the filed substance ----

    @Test
    fun rotatedSixAndNine_differBeyondTheConsultFloor() {
        val six = HourFigures.glyphFor('6')
        val nine = HourFigures.glyphFor('9')
        val sep69 = separation(ink(six, rotate180 = true), ink(nine))
        val sep96 = separation(ink(nine, rotate180 = true), ink(six))
        assertTrue(
            "rotated 6 vs 9 must differ by >= 15% of union ink (consult floor; " +
                "filed 46.7%), measured $sep69",
            sep69 >= 0.15
        )
        assertTrue(
            "rotated 9 vs 6 must differ by >= 15% of union ink (filed 47.0%), " +
                "measured $sep96",
            sep96 >= 0.15
        )
        // And the pair is not one drawing: upright they separate too.
        assertTrue(separation(ink(six), ink(nine)) >= 0.15)
    }

    @Test
    fun uprightWorstPairs_stillSeparateByForm() {
        // TYPE-SR-2 T3: worst filed pair 3/8 at IoU 0.655.
        for ((a, b) in listOf('3' to '8', '0' to '8', '5' to '8', '6' to '8')) {
            val sep = separation(ink(HourFigures.glyphFor(a)), ink(HourFigures.glyphFor(b)))
            assertTrue("pair $a/$b must differ by >= 15% of union ink, measured $sep", sep >= 0.15)
        }
    }

    @Test
    fun colon_isTwoPunches_widerThanTheGraversCut() {
        val colon = HourFigures.glyphFor(':')
        assertTrue("the colon is punches, not strokes", colon.strokes.isEmpty())
        assertEquals(2, colon.punches.size)
        for (punch in colon.punches) {
            assertEquals(150f, punch.w, 0.0f)
            assertEquals(150f, punch.h, 0.0f)
            assertTrue(
                "a punch is wider than the one cut width (T5)",
                punch.w > HourFigures.STROKE_UNITS
            )
        }
    }

    @Test
    fun theFigureSet_isExactlyTheDigitsAndTheColon() {
        assertEquals(
            ('0'..'9').toSet() + ':',
            HourFigures.GLYPHS.keys
        )
        for (digit in '0'..'9') {
            assertTrue(
                "digit $digit must carry at least one stroke",
                HourFigures.glyphFor(digit).strokes.isNotEmpty()
            )
        }
        try {
            HourFigures.glyphFor('A')
            fail("glyphFor must refuse a char the ring can never place")
        } catch (expected: NoSuchElementException) {
            // getValue's refusal is the contract.
        }
    }

    @Test
    fun zero_carriesItsSlash() {
        // FIG-1's engraved translation: the slashed zero. Its
        // second stroke is the single diagonal (178,128)->(368,572).
        val zero = HourFigures.glyphFor('0')
        assertEquals(2, zero.strokes.size)
        val slash = zero.strokes[1]
        assertEquals(HourFigures.Op.M, slash.first().op)
        assertEquals(HourFigures.Op.L, slash.last().op)
        assertTrue(slash.last().a[0] > slash.first().a[0])
        assertTrue(slash.last().a[1] > slash.first().a[1])
    }

    @Test
    fun cellContract_agreesWithTheRing_andEveryPointStandsInsideTheCell() {
        assertEquals(132f, HourFigures.STROKE_UNITS, 0.0f)
        assertEquals(546f, HourFigures.ADVANCE_UNITS, 0.0f)
        assertEquals(273f, HourFigures.CENTRE_X, 0.0f)
        assertEquals(350f, HourFigures.CENTRE_Y, 0.0f)
        // The ring's cell is the same advance (EL-1: no kerning).
        val ringAdvanceUnits = RingGeometry.ADVANCE_EM * HourFigures.EM_UNITS
        assertTrue(
            "figure advance ${HourFigures.ADVANCE_UNITS} must agree with the ring's " +
                "$ringAdvanceUnits",
            kotlin.math.abs(HourFigures.ADVANCE_UNITS - ringAdvanceUnits) < 1.0
        )
        // Placement sanity: no drawn point escapes the cell the
        // ring will place it in.
        for ((c, glyph) in HourFigures.GLYPHS) {
            for (stroke in glyph.strokes) {
                for (cmd in stroke) {
                    val xs = cmd.a.filterIndexed { i, _ -> i % 2 == 0 }
                    val ys = cmd.a.filterIndexed { i, _ -> i % 2 == 1 }
                    for (x in xs) {
                        assertTrue("glyph $c x=$x outside the cell", x in 0f..HourFigures.ADVANCE_UNITS)
                    }
                    for (y in ys) {
                        assertTrue("glyph $c y=$y outside cap height", y in 0f..700f)
                    }
                }
            }
            for (punch in glyph.punches) {
                assertTrue(punch.x >= 0f && punch.x + punch.w <= HourFigures.ADVANCE_UNITS)
                assertTrue(punch.y >= 0f && punch.y + punch.h <= 700f)
            }
        }
    }
}
