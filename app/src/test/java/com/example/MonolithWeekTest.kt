package com.example

import com.example.data.MonolithWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * TOKEN-SPEC §8 test 1 — the week anchor. Pure java.time, no Android:
 * the anchor is the Monday 00:00 local instant of the containing week,
 * in whatever zone is asked, DST rules included.
 */
class MonolithWeekTest {

    private fun anchorOf(zdt: ZonedDateTime): Long =
        MonolithWeek.weekAnchorMillis(zdt.toInstant().toEpochMilli(), zdt.zone)

    private fun expectedAnchor(year: Int, month: Int, day: Int, zone: ZoneId): Long =
        ZonedDateTime.of(year, month, day, 0, 0, 0, 0, zone).toInstant().toEpochMilli()

    @Test
    fun ordinaryWednesdayAnchorsToThatMonday() {
        val ist = ZoneId.of("Asia/Kolkata")
        val wed = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, ist)
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(wed))
    }

    @Test
    fun sundayBelongsToTheWeekThatIsEnding_mondayStartsANewOne() {
        val ist = ZoneId.of("Asia/Kolkata")
        val sunday2359 = ZonedDateTime.of(2026, 11, 1, 23, 59, 0, 0, ist)
        val monday0000 = ZonedDateTime.of(2026, 11, 2, 0, 0, 0, 0, ist)
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(sunday2359))
        assertEquals(expectedAnchor(2026, 11, 2, ist), anchorOf(monday0000))
        assertEquals(monday0000.toInstant().toEpochMilli(), anchorOf(monday0000))
    }

    @Test
    fun dstSpringForwardWeekStillAnchorsToMondayMidnight() {
        val ny = ZoneId.of("America/New_York")
        // US spring forward was Sunday 2024-03-10, the last day of the
        // week that began Monday 03-04: the jump-day Sunday still
        // anchors to 03-04 (a normal midnight — the jump is at 02:00),
        // and the Wednesday after it belongs to the new week (03-11).
        // DST must not shift any anchor off Monday 00:00 local.
        val wedAfterJump = ZonedDateTime.of(2024, 3, 13, 12, 0, 0, 0, ny)
        val jumpDayLate = ZonedDateTime.of(2024, 3, 10, 23, 59, 0, 0, ny)
        val mondayAfter = ZonedDateTime.of(2024, 3, 11, 0, 0, 0, 0, ny)
        assertEquals(expectedAnchor(2024, 3, 11, ny), anchorOf(wedAfterJump))
        assertEquals(expectedAnchor(2024, 3, 4, ny), anchorOf(jumpDayLate))
        assertEquals(expectedAnchor(2024, 3, 11, ny), anchorOf(mondayAfter))

        val anchorZdt = Instant.ofEpochMilli(anchorOf(wedAfterJump)).atZone(ny)
        assertEquals(DayOfWeek.MONDAY, anchorZdt.dayOfWeek)
        assertEquals(LocalTime.MIDNIGHT, anchorZdt.toLocalTime())
    }

    @Test
    fun sameInstantTwoZones_anchorsDifferByTheZoneDifferenceOnly() {
        val ist = ZoneId.of("Asia/Kolkata")
        val ny = ZoneId.of("America/New_York")
        // Mid-week instant (same containing week in both zones).
        val instant = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant()
        val istAnchor = MonolithWeek.weekAnchorMillis(instant.toEpochMilli(), ist)
        val nyAnchor = MonolithWeek.weekAnchorMillis(instant.toEpochMilli(), ny)
        // IST is UTC+5:30, New York EDT is UTC-4: the local Mondays are
        // 9.5 hours apart as instants — the ruled travel bound (§3).
        assertEquals(34_200_000L, nyAnchor - istAnchor)
        assertTrue(nyAnchor - istAnchor <= 26 * 3_600_000L)
        // Both are genuinely Monday 00:00 in their own zone.
        assertEquals(DayOfWeek.MONDAY, Instant.ofEpochMilli(istAnchor).atZone(ist).dayOfWeek)
        assertEquals(LocalTime.MIDNIGHT, Instant.ofEpochMilli(istAnchor).atZone(ist).toLocalTime())
        assertEquals(DayOfWeek.MONDAY, Instant.ofEpochMilli(nyAnchor).atZone(ny).dayOfWeek)
        assertEquals(LocalTime.MIDNIGHT, Instant.ofEpochMilli(nyAnchor).atZone(ny).toLocalTime())
    }
}
