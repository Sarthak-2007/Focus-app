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

    // Audit R1 #37 (HIGH): the months index used to be computed by
    // a SQL twin subtracting 12,600 ELAPSED seconds — a different
    // function from this wall-clock rule on DST days, including at
    // real month boundaries. The twin is dead (the DAO now returns
    // raw instants and this object files them); these are the
    // audit's own verified divergence cases, pinned so the filing
    // rule can never quietly fork again.
    @Test
    fun dstFallBack_monthBoundary_filesUnderOctober() {
        val newYork = ZoneId.of("America/New_York")
        // 2026-11-01 02:30 EST (the repeated hour, UTC-5): before
        // the day's 03:30 start, so it belongs to Oct 31's day —
        // and October's month. The dead SQL twin said November.
        val instant = java.time.OffsetDateTime.of(2026, 11, 1, 2, 30, 0, 0, java.time.ZoneOffset.ofHours(-5))
            .toInstant().toEpochMilli()
        assertEquals(LocalDate.of(2026, 10, 31), DrawerDay.filingDate(instant, newYork))
        assertEquals(2026 to 10, DrawerDay.filingYearMonth(instant, newYork))
        // And October's month bounds contain it — index and page agree.
        val (from, to) = DrawerDay.monthBoundsMillis(2026, 10, newYork)
        assertTrue(instant >= from && instant < to)
    }

    @Test
    fun dstSpringForward_filesUnderSameDay() {
        val newYork = ZoneId.of("America/New_York")
        // 2026-03-08 04:00 EDT (UTC-4, just after the 02:00 gap):
        // past 03:30, so it files under Mar 8 itself. The dead SQL
        // twin filed it a day later.
        val instant = java.time.OffsetDateTime.of(2026, 3, 8, 4, 0, 0, 0, java.time.ZoneOffset.ofHours(-4))
            .toInstant().toEpochMilli()
        assertEquals(LocalDate.of(2026, 3, 8), DrawerDay.filingDate(instant, newYork))
        assertEquals(2026 to 3, DrawerDay.filingYearMonth(instant, newYork))
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
