package com.example

import com.example.data.DrawerDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * The Drawer's 03:30 filing rule (Kaeufer's midnight walk): a
 * completion at 00:02 belongs to the day that began the previous
 * 03:30 — and to that day's MONTH — while its printed time stays
 * 00:02. Pure java.time; explicit zone so the test means the same
 * thing on any machine.
 */
class DrawerDayTest {

    private val zone: ZoneId = ZoneId.of("Asia/Kolkata")

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(hour, minute).atZone(zone).toInstant().toEpochMilli()

    @Test
    fun justAfterMidnight_filesUnderPreviousDay() {
        val instant = millis(LocalDate.of(2026, 10, 29), 0, 2)
        assertEquals(LocalDate.of(2026, 10, 28), DrawerDay.filingDate(instant, zone))
    }

    @Test
    fun boundary_0329previous_0330new() {
        val date = LocalDate.of(2026, 10, 29)
        assertEquals(date.minusDays(1), DrawerDay.filingDate(millis(date, 3, 29), zone))
        assertEquals(date, DrawerDay.filingDate(millis(date, 3, 30), zone))
        assertEquals(date, DrawerDay.filingDate(millis(date, 23, 59), zone))
    }

    // DRW-05's ticker arithmetic (Truth Car 2): the next boundary
    // is always the coming 03:30 — from midday it is tomorrow's,
    // from the small hours it is today's, and AT 03:30 it is the
    // NEXT day's (the ticker must never spin on a zero wait).
    @Test
    fun nextBoundary_isAlwaysTheComing0330() {
        val date = LocalDate.of(2026, 10, 29)
        assertEquals(
            millis(date.plusDays(1), 3, 30),
            DrawerDay.nextBoundaryMillis(millis(date, 12, 0), zone)
        )
        assertEquals(
            millis(date, 3, 30),
            DrawerDay.nextBoundaryMillis(millis(date, 0, 2), zone)
        )
        assertEquals(
            millis(date.plusDays(1), 3, 30),
            DrawerDay.nextBoundaryMillis(millis(date, 3, 30), zone)
        )
    }

    @Test
    fun firstOfMonth_0002_filesUnderPreviousMonth() {
        val instant = millis(LocalDate.of(2026, 11, 1), 0, 2)
        assertEquals(2026 to 10, DrawerDay.filingYearMonth(instant, zone))
    }

    @Test
    fun monthBounds_containExactlyTheFilingMonth() {
        val (from, to) = DrawerDay.monthBoundsMillis(2026, 10, zone)
        // 00:02 on Nov 1 files to October: inside October's bounds.
        val nov1Early = millis(LocalDate.of(2026, 11, 1), 0, 2)
        assertTrue(nov1Early >= from && nov1Early < to)
        // 00:02 on Oct 1 files to September: outside October's bounds.
        val oct1Early = millis(LocalDate.of(2026, 10, 1), 0, 2)
        assertTrue(oct1Early < from)
        // Bounds themselves sit at 03:30.
        assertEquals(millis(LocalDate.of(2026, 10, 1), 3, 30), from)
        assertEquals(millis(LocalDate.of(2026, 11, 1), 3, 30), to)
    }
}
