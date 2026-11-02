package com.example

import com.example.domain.DeskClockRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * P8a — THE FORMING PLATE'S RECORD (domain/DeskClockRecord.kt;
 * DESIGNER-SR-6's §6 seam):
 *
 *  - PREFIX: formed columns are the span's elapsed fraction of the
 *    field, floored at nothing, capped at the whole field.
 *  - MONOTONIC: later readings never form less.
 *  - COLUMN-KIND TRUTH: a column is SMOOTH iff its centre falls in
 *    a gap span, SMOOTH_FIRING iff in the break's firing span.
 *  - PUNCH-PLACE TRUTH: an inscribed punch's place (x · lane ·
 *    diameter class) matches its fact exactly; a punch inscribes
 *    only once its moment has passed.
 *  - CF-1: the completed record equals the static completed field.
 *  - NO NUMBERS: the record's types carry no string; the seal
 *    place is EMPTY by constant.
 */
class DeskClockRecordP8Test {

    private val span = 12L * 60L * 60L * 1000L

    @Test
    fun prefix_formsByExtent_andCapsAtTheField() {
        assertEquals(0, DeskClockRecord.record(span, 0L, emptyList(), emptyList()).formedColumns)
        assertEquals(29, DeskClockRecord.record(span, span / 4, emptyList(), emptyList()).formedColumns)
        assertEquals(58, DeskClockRecord.record(span, span / 2, emptyList(), emptyList()).formedColumns)
        val done = DeskClockRecord.record(span, span, emptyList(), emptyList())
        assertEquals(DeskClockRecord.COLUMNS, done.formedColumns)
        assertTrue(done.completed)
        val past = DeskClockRecord.record(span, span * 2, emptyList(), emptyList())
        assertEquals(DeskClockRecord.COLUMNS, past.formedColumns)
        assertTrue(past.completed)
        assertEquals(0, DeskClockRecord.record(span, -5_000L, emptyList(), emptyList()).formedColumns)
        assertFalse(DeskClockRecord.record(span, span / 2, emptyList(), emptyList()).completed)
    }

    @Test
    fun formation_isMonotonic_acrossTheWholeSpan() {
        var last = -1
        var elapsed = 0L
        while (elapsed <= span) {
            val formed = DeskClockRecord.record(span, elapsed, emptyList(), emptyList()).formedColumns
            assertTrue("formed count reversed at $elapsed", formed >= last)
            last = formed
            elapsed += span / 97 // an unaligned step, on purpose
        }
        assertEquals(
            DeskClockRecord.COLUMNS,
            DeskClockRecord.record(span, span, emptyList(), emptyList()).formedColumns
        )
    }

    @Test
    fun columnKindTruth_centreInGapIsSmooth_centreInFiringIsSmoothFiring() {
        val gap = DeskClockRecord.GapSpan(
            startOffsetMillis = (span * 0.30).toLong(),
            durationMillis = (span * 0.08).toLong()
        )
        val firing = DeskClockRecord.GapSpan(
            startOffsetMillis = (span * 0.52).toLong(),
            durationMillis = (span * 0.05).toLong(),
            isBreakFiring = true
        )
        val plate = DeskClockRecord.record(span, span / 2, emptyList(), listOf(gap, firing))
        assertEquals(DeskClockRecord.COLUMNS, plate.columnKinds.size)
        for (column in 0 until DeskClockRecord.COLUMNS) {
            val centre = (column + 0.5) / DeskClockRecord.COLUMNS
            val expected = when {
                centre >= 0.52 && centre < 0.57 -> DeskClockRecord.ColumnKind.SMOOTH_FIRING
                centre >= 0.30 && centre < 0.38 -> DeskClockRecord.ColumnKind.SMOOTH
                else -> DeskClockRecord.ColumnKind.GRAIN
            }
            assertEquals("column $column", expected, plate.columnKinds[column])
        }
    }

    @Test
    fun punchPlaceTruth_placeLaneAndClassMatchTheFact() {
        val facts = listOf(
            DeskClockRecord.PunchFact(DeskClockRecord.PunchKind.REACH, span / 4),
            DeskClockRecord.PunchFact(DeskClockRecord.PunchKind.HELD, span / 2),
            DeskClockRecord.PunchFact(DeskClockRecord.PunchKind.SHOWING, span * 3 / 4)
        )
        val plate = DeskClockRecord.record(span, span, facts, emptyList())
        assertEquals(3, plate.punches.size)
        assertEquals(0.25f, plate.punches[0].xFraction, 0.001f)
        assertEquals(DeskClockRecord.Lane.UPPER, plate.punches[0].lane)
        assertEquals(DeskClockRecord.DiameterClass.LARGE, plate.punches[0].diameterClass)
        assertEquals(0.5f, plate.punches[1].xFraction, 0.001f)
        assertEquals(DeskClockRecord.Lane.MIDDLE, plate.punches[1].lane)
        assertEquals(DeskClockRecord.DiameterClass.MEDIUM, plate.punches[1].diameterClass)
        assertEquals(0.75f, plate.punches[2].xFraction, 0.001f)
        assertEquals(DeskClockRecord.Lane.LOWER, plate.punches[2].lane)
        assertEquals(DeskClockRecord.DiameterClass.SMALL, plate.punches[2].diameterClass)
        // A punch whose moment has not come is not inscribed.
        val early = DeskClockRecord.record(span, span / 3, facts, emptyList())
        assertEquals(listOf(facts[0].kind), early.punches.map { it.kind })
    }

    @Test
    fun cf1_theCompletedRecordEqualsTheStaticCompletedField() {
        val facts = listOf(DeskClockRecord.PunchFact(DeskClockRecord.PunchKind.REACH, span / 5))
        val gaps = listOf(DeskClockRecord.GapSpan(span / 4, span / 8))
        assertEquals(
            DeskClockRecord.completedField(span, facts, gaps),
            DeskClockRecord.record(span, span, facts, gaps)
        )
    }

    @Test
    fun theSealPlace_isEmpty_byConstant_atEveryReading() {
        for (elapsed in listOf(0L, span / 2, span)) {
            assertEquals(
                DeskClockRecord.SealPlace.EMPTY,
                DeskClockRecord.record(span, elapsed, emptyList(), emptyList()).sealPlace
            )
        }
    }

    @Test
    fun theRecord_carriesNoString() {
        for (type in listOf(
            DeskClockRecord.FormingPlate::class.java,
            DeskClockRecord.Punch::class.java,
            DeskClockRecord.PunchFact::class.java,
            DeskClockRecord.GapSpan::class.java
        )) {
            for (field in type.declaredFields) {
                assertFalse(
                    "${type.simpleName}.${field.name} is a string — the record carries no words or figures",
                    field.type == String::class.java
                )
            }
        }
    }

    @Test
    fun noSpan_noRecord() {
        try {
            DeskClockRecord.record(0L, 0L, emptyList(), emptyList())
            fail("a non-positive span must refuse: the record exists only inside a standing seal")
        } catch (expected: IllegalArgumentException) {
            // refused, as filed
        }
    }
}
