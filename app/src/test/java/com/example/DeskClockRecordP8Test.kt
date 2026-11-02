package com.example

import com.example.domain.DeskClockRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * P8a — THE DESK CLOCK'S FORMING RECORD (domain/DeskClockRecord.kt):
 *
 *  - PREFIX: formed extent is the span's elapsed fraction, floored
 *    at nothing and capped at the whole rim; marks and gaps appear
 *    only inside the formed extent, never ahead of it.
 *  - MONOTONIC: later readings never form less.
 *  - NO NUMBERS: the record's types carry no string and no display
 *    figure — extent is the only quantity.
 *  - GRADED MARKS: P1's grades ride the kinds (reach large · held
 *    medium · floor small).
 *  - HANDS: positions for the time of day (C-2) — twelve is up,
 *    the hour hand travels with the minutes.
 */
class DeskClockRecordP8Test {

    private val span = 12L * 60L * 60L * 1000L // a twelve-hour span, in elapsed millis

    @Test
    fun prefix_formsByExtent_andCapsAtTheSpan() {
        assertEquals(0, DeskClockRecord.record(span, 0L, emptyList(), emptyList()).formedGrains)
        assertEquals(12, DeskClockRecord.record(span, span / 4, emptyList(), emptyList()).formedGrains)
        assertEquals(24, DeskClockRecord.record(span, span / 2, emptyList(), emptyList()).formedGrains)
        val done = DeskClockRecord.record(span, span, emptyList(), emptyList())
        assertEquals(DeskClockRecord.GRAINS, done.formedGrains)
        assertTrue(done.isComplete)
        // A reading past the lifting stands complete and still.
        val past = DeskClockRecord.record(span, span * 2, emptyList(), emptyList())
        assertEquals(DeskClockRecord.GRAINS, past.formedGrains)
        assertTrue(past.isComplete)
        // A reading before the seal floors at nothing formed.
        assertEquals(0, DeskClockRecord.record(span, -5_000L, emptyList(), emptyList()).formedGrains)
        assertFalse(DeskClockRecord.record(span, span / 2, emptyList(), emptyList()).isComplete)
    }

    @Test
    fun formation_isMonotonic_acrossTheWholeSpan() {
        var last = -1
        var elapsed = 0L
        while (elapsed <= span) {
            val formed = DeskClockRecord.record(span, elapsed, emptyList(), emptyList()).formedGrains
            assertTrue("formed extent reversed at $elapsed", formed >= last)
            last = formed
            elapsed += span / 97 // an unaligned step, on purpose
        }
        assertEquals(DeskClockRecord.GRAINS, last)
    }

    @Test
    fun marks_inscribeOnlyOnceTheirMomentHasPassed_inTimeOrder() {
        val late = DeskClockRecord.Mark(DeskClockRecord.MarkKind.REACH, span / 2)
        val early = DeskClockRecord.Mark(DeskClockRecord.MarkKind.HELD, span / 4)
        val before = DeskClockRecord.record(span, span / 3, listOf(late, early), emptyList())
        assertEquals(listOf(early), before.marks)
        val after = DeskClockRecord.record(span, span, listOf(late, early), emptyList())
        assertEquals(listOf(early, late), after.marks)
    }

    @Test
    fun gaps_drawOnlyTheirLivedPart() {
        val gap = DeskClockRecord.GapSpan(startOffsetMillis = span / 4, durationMillis = span / 4)
        val future = DeskClockRecord.GapSpan(startOffsetMillis = span / 2, durationMillis = span / 4)
        val during = DeskClockRecord.record(span, span / 4 + span / 8, emptyList(), listOf(gap, future))
        assertEquals(1, during.gaps.size)
        assertEquals(span / 4, during.gaps[0].startOffsetMillis)
        assertEquals(span / 8, during.gaps[0].durationMillis)
        val after = DeskClockRecord.record(span, span, emptyList(), listOf(gap, future))
        assertEquals(listOf(gap, future), after.gaps)
    }

    @Test
    fun grades_rideTheKinds_asRuled() {
        assertEquals(DeskClockRecord.MarkSize.LARGE, DeskClockRecord.MarkKind.REACH.size)
        assertEquals(DeskClockRecord.MarkSize.MEDIUM, DeskClockRecord.MarkKind.HELD.size)
        assertEquals(DeskClockRecord.MarkSize.SMALL, DeskClockRecord.MarkKind.FLOOR.size)
    }

    @Test
    fun theRecord_carriesNoString_andNoDisplayFigure() {
        // The no-numbers law at the type level: nothing in the
        // record's data can be rendered as text or a figure.
        for (type in listOf(
            DeskClockRecord.FormingRecord::class.java,
            DeskClockRecord.Mark::class.java,
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

    @Test
    fun hands_standWhereTheTimeStands() {
        val twelve = DeskClockRecord.hands(12, 0)
        assertEquals(0f, twelve.hourAngleDegrees, 0.001f)
        assertEquals(0f, twelve.minuteAngleDegrees, 0.001f)
        val halfPastThree = DeskClockRecord.hands(3, 30)
        assertEquals(105f, halfPastThree.hourAngleDegrees, 0.001f)
        assertEquals(180f, halfPastThree.minuteAngleDegrees, 0.001f)
        val gym = DeskClockRecord.hands(8, 0)
        assertEquals(240f, gym.hourAngleDegrees, 0.001f)
        // Midnight and noon are the same place on a regulator's dial.
        assertEquals(
            DeskClockRecord.hands(0, 0).hourAngleDegrees,
            DeskClockRecord.hands(12, 0).hourAngleDegrees,
            0.001f
        )
    }

    @Test
    fun fractionAt_isAPlace_notAFigure() {
        assertEquals(0f, DeskClockRecord.fractionAt(span, 0L), 0.001f)
        assertEquals(0.5f, DeskClockRecord.fractionAt(span, span / 2), 0.001f)
        assertEquals(1f, DeskClockRecord.fractionAt(span, span * 3), 0.001f)
    }
}
