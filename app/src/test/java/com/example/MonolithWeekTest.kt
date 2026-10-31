package com.example

import com.example.data.ClockHonesty
import com.example.data.MonolithWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * PIN scope: SOURCE.
 *
 * TOKEN-SPEC §8 test 1 — the week anchor. Pure java.time, no Android:
 * the anchor is the Monday 03:30 local instant of the containing
 * FILING week (issue #523, finding C-6 — the week turns on his 03:30
 * day seam, not at midnight), in whatever zone is asked, DST rules
 * included.
 */
class MonolithWeekTest {

    private fun anchorOf(zdt: ZonedDateTime): Long =
        MonolithWeek.weekAnchorMillis(zdt.toInstant().toEpochMilli(), zdt.zone)

    private fun expectedAnchor(year: Int, month: Int, day: Int, zone: ZoneId): Long =
        ZonedDateTime.of(year, month, day, 3, 30, 0, 0, zone).toInstant().toEpochMilli()

    @Test
    fun ordinaryWednesdayAnchorsToThatMonday() {
        val ist = ZoneId.of("Asia/Kolkata")
        val wed = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, ist)
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(wed))
    }

    @Test
    fun sundayBelongsToTheWeekThatIsEnding_monday0330StartsANewOne() {
        val ist = ZoneId.of("Asia/Kolkata")
        val sunday2359 = ZonedDateTime.of(2026, 11, 1, 23, 59, 0, 0, ist)
        // Issue #523 (C-6): Monday 00:00 is still Sunday by his filing
        // clock — the new week opens at Monday 03:30, not midnight.
        val monday0000 = ZonedDateTime.of(2026, 11, 2, 0, 0, 0, 0, ist)
        val monday0329 = ZonedDateTime.of(2026, 11, 2, 3, 29, 0, 0, ist)
        val monday0330 = ZonedDateTime.of(2026, 11, 2, 3, 30, 0, 0, ist)
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(sunday2359))
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(monday0000))
        assertEquals(expectedAnchor(2026, 10, 26, ist), anchorOf(monday0329))
        assertEquals(expectedAnchor(2026, 11, 2, ist), anchorOf(monday0330))
        assertEquals(monday0330.toInstant().toEpochMilli(), anchorOf(monday0330))
    }

    @Test
    fun dstSpringForwardWeekStillAnchorsToMonday0330() {
        val ny = ZoneId.of("America/New_York")
        // US spring forward was Sunday 2024-03-10, the last day of the
        // week that began Monday 03-04: the jump-day Sunday still
        // anchors to 03-04, and the Wednesday after it belongs to the
        // new week (03-11). DST must not shift any anchor off Monday
        // 03:30 local.
        val wedAfterJump = ZonedDateTime.of(2024, 3, 13, 12, 0, 0, 0, ny)
        val jumpDayLate = ZonedDateTime.of(2024, 3, 10, 23, 59, 0, 0, ny)
        val mondayAfter = ZonedDateTime.of(2024, 3, 11, 3, 30, 0, 0, ny)
        assertEquals(expectedAnchor(2024, 3, 11, ny), anchorOf(wedAfterJump))
        assertEquals(expectedAnchor(2024, 3, 4, ny), anchorOf(jumpDayLate))
        assertEquals(expectedAnchor(2024, 3, 11, ny), anchorOf(mondayAfter))

        val anchorZdt = Instant.ofEpochMilli(anchorOf(wedAfterJump)).atZone(ny)
        assertEquals(DayOfWeek.MONDAY, anchorZdt.dayOfWeek)
        assertEquals(LocalTime.of(3, 30), anchorZdt.toLocalTime())
    }

    // -----------------------------------------------------------------
    // Issue #39 F1 — spendCountsAgainstWeek, the timezone cure. A spend
    // minted under one zone must still count after the phone's zone
    // changes; a neighbouring week's spend must never count.
    // -----------------------------------------------------------------

    private fun counts(
        spendAnchor: Long,
        spentAt: Long,
        currentAnchor: Long,
        nextAnchor: Long
    ): Boolean = MonolithWeek.spendCountsAgainstWeek(
        spendAnchorMillis = spendAnchor,
        spentAtWallMillis = spentAt,
        currentAnchorMillis = currentAnchor,
        nextWeekAnchorMillis = nextAnchor
    )

    @Test
    fun foreignZoneAnchorCounts_anchorArm_westwardTravel() {
        val ist = ZoneId.of("Asia/Kolkata")
        val ny = ZoneId.of("America/New_York")
        // Spent Mon 04:00 IST — minutes after the IST week opened
        // (Monday 03:30, his day seam), booked against the IST
        // Monday. The phone then travels WEST: evaluated Wednesday in
        // New York, the same Monday's NY anchor sits 9.5h later as an
        // instant, so the spend instant falls BEFORE the NY week
        // window — the instant arm cannot see it. Only the anchor arm
        // (same Monday, ≤26h away) counts it.
        val spentAt = ZonedDateTime.of(2026, 10, 26, 4, 0, 0, 0, ist).toInstant().toEpochMilli()
        val spendAnchor = MonolithWeek.weekAnchorMillis(spentAt, ist)
        val evalInstant = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val currentAnchor = MonolithWeek.weekAnchorMillis(evalInstant, ny)
        val nextAnchor = MonolithWeek.nextWeekAnchorMillis(evalInstant, ny)
        assertEquals(34_200_000L, currentAnchor - spendAnchor)
        assertTrue(spentAt < currentAnchor) // instant arm blind here — by construction
        assertTrue(counts(spendAnchor, spentAt, currentAnchor, nextAnchor))
    }

    @Test
    fun foreignZoneSpendCounts_instantArm_eastwardTravel() {
        val ist = ZoneId.of("Asia/Kolkata")
        val ny = ZoneId.of("America/New_York")
        // Spent Sun 23:00 New York — the last hour of the NY week.
        // The phone travels EAST: in IST that instant is already
        // Monday 09:30 of the NEXT IST week, whose anchor sits ~6.6
        // days after the stored NY anchor — the anchor arm cannot see
        // it. Only the instant arm (the spend moment lies inside the
        // current IST week) counts it.
        val spentAt = ZonedDateTime.of(2026, 11, 1, 23, 0, 0, 0, ny).toInstant().toEpochMilli()
        val spendAnchor = MonolithWeek.weekAnchorMillis(spentAt, ny)
        val currentAnchor = MonolithWeek.weekAnchorMillis(spentAt, ist)
        val nextAnchor = MonolithWeek.nextWeekAnchorMillis(spentAt, ist)
        assertTrue(currentAnchor - spendAnchor > MonolithWeek.WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS)
        assertTrue(spentAt >= currentAnchor && spentAt < nextAnchor)
        assertTrue(counts(spendAnchor, spentAt, currentAnchor, nextAnchor))
    }

    @Test
    fun lastWeeksForeignAnchorDoesNotCount() {
        val ist = ZoneId.of("Asia/Kolkata")
        val ny = ZoneId.of("America/New_York")
        // Last week's NY spend, evaluated in this week's IST: the
        // stored anchor is ~a week away on the anchor arm and the
        // spend instant is outside this week's window — both arms
        // fail closed, the token is intact.
        val spentAt = ZonedDateTime.of(2026, 10, 21, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val spendAnchor = MonolithWeek.weekAnchorMillis(spentAt, ny)
        val evalInstant = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, ist).toInstant().toEpochMilli()
        val currentAnchor = MonolithWeek.weekAnchorMillis(evalInstant, ist)
        val nextAnchor = MonolithWeek.nextWeekAnchorMillis(evalInstant, ist)
        assertFalse(counts(spendAnchor, spentAt, currentAnchor, nextAnchor))
        // Next week's spend is equally invisible (fail closed both ways).
        val futureSpentAt = ZonedDateTime.of(2026, 11, 4, 12, 0, 0, 0, ist).toInstant().toEpochMilli()
        val futureAnchor = MonolithWeek.weekAnchorMillis(futureSpentAt, ist)
        assertFalse(counts(futureAnchor, futureSpentAt, currentAnchor, nextAnchor))
    }

    @Test
    fun anchorToleranceBoundary_isExactly26Hours_inclusive() {
        // The widest real zone pair (+14 / −12) moves the same Monday
        // by exactly 26h: at the boundary the spend still counts;
        // one millisecond beyond it, it does not.
        val tol = MonolithWeek.WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS
        assertEquals(93_600_000L, tol)
        val currentAnchor = expectedAnchor(2026, 10, 26, ZoneId.of("UTC"))
        val nextAnchor = currentAnchor + 7 * 24 * 3_600_000L
        val spentOutsideWindow = currentAnchor - 1L // instant arm blind in both probes
        assertTrue(counts(currentAnchor + tol, spentOutsideWindow, currentAnchor, nextAnchor))
        assertTrue(counts(currentAnchor - tol, spentOutsideWindow, currentAnchor, nextAnchor))
        assertFalse(counts(currentAnchor + tol + 1, spentOutsideWindow, currentAnchor, nextAnchor))
        assertFalse(counts(currentAnchor - tol - 1, spentOutsideWindow, currentAnchor, nextAnchor))
    }

    @Test
    fun dstWeeks_anchorStaysMonday0330_priorWeekSpendNeverCounts() {
        val ny = ZoneId.of("America/New_York")
        // SPRING (jump Sun 2024-03-10 02:00): the week that opens
        // Mon 03-11 still anchors to Monday 03:30 local, and a spend
        // from the jump week (anchor Mon 03-04) never counts in it.
        val springEval = ZonedDateTime.of(2024, 3, 13, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val springAnchor = MonolithWeek.weekAnchorMillis(springEval, ny)
        assertEquals(expectedAnchor(2024, 3, 11, ny), springAnchor)
        val springNext = MonolithWeek.nextWeekAnchorMillis(springEval, ny)
        val jumpWeekSpentAt = ZonedDateTime.of(2024, 3, 6, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val jumpWeekAnchor = MonolithWeek.weekAnchorMillis(jumpWeekSpentAt, ny)
        assertFalse(counts(jumpWeekAnchor, jumpWeekSpentAt, springAnchor, springNext))
        // …while a spend inside the jump week itself still counts
        // when evaluated later in that same week (Sun 23:59, post-jump).
        val jumpWeekEval = ZonedDateTime.of(2024, 3, 10, 23, 59, 0, 0, ny).toInstant().toEpochMilli()
        assertTrue(
            counts(
                jumpWeekAnchor, jumpWeekSpentAt,
                MonolithWeek.weekAnchorMillis(jumpWeekEval, ny),
                MonolithWeek.nextWeekAnchorMillis(jumpWeekEval, ny)
            )
        )
        // FALL (fall-back Sun 2024-11-03 02:00): same law — the week
        // opening Mon 11-04 anchors to Monday 03:30, and the prior
        // week's spend (anchor Mon 10-28) never counts in it.
        val fallEval = ZonedDateTime.of(2024, 11, 6, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val fallAnchor = MonolithWeek.weekAnchorMillis(fallEval, ny)
        assertEquals(expectedAnchor(2024, 11, 4, ny), fallAnchor)
        val fallNext = MonolithWeek.nextWeekAnchorMillis(fallEval, ny)
        val priorSpentAt = ZonedDateTime.of(2024, 10, 30, 12, 0, 0, 0, ny).toInstant().toEpochMilli()
        val priorAnchor = MonolithWeek.weekAnchorMillis(priorSpentAt, ny)
        assertFalse(counts(priorAnchor, priorSpentAt, fallAnchor, fallNext))
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
        // Both are genuinely Monday 03:30 in their own zone.
        assertEquals(DayOfWeek.MONDAY, Instant.ofEpochMilli(istAnchor).atZone(ist).dayOfWeek)
        assertEquals(LocalTime.of(3, 30), Instant.ofEpochMilli(istAnchor).atZone(ist).toLocalTime())
        assertEquals(DayOfWeek.MONDAY, Instant.ofEpochMilli(nyAnchor).atZone(ny).dayOfWeek)
        assertEquals(LocalTime.of(3, 30), Instant.ofEpochMilli(nyAnchor).atZone(ny).toLocalTime())
    }

    // -----------------------------------------------------------------
    // Issue #82 tightening (STURM3-16) — composition: the honest now
    // feeding the week anchor. Under in-boot travel of ±7 days the
    // honest now does not move (60 s of monotonic time only), so a
    // spend's week anchor is IDENTICAL under both attacks. UTC zone
    // pinned, per the patch spec.
    // -----------------------------------------------------------------

    private fun honestNowUnderTravel(travelMillis: Long): Long {
        val utc = ZoneId.of("UTC")
        val anchorWall = ZonedDateTime.of(2026, 10, 28, 12, 0, 0, 0, utc)
            .toInstant().toEpochMilli() // Wednesday mid-week
        val decision = ClockHonesty.decide(
            anchorWallMillis = anchorWall,
            anchorElapsedRealtimeMillis = 500_000L,
            anchorBootCount = 7,
            nowWallMillis = anchorWall + travelMillis,
            nowElapsedRealtimeMillis = 500_000L + 60_000L, // 60 s really passed
            nowBootCount = 7
        )
        assertEquals(anchorWall + 60_000L, decision.honestNowMillis)
        return decision.honestNowMillis
    }

    @Test
    fun spendWeekAnchorIdenticalUnderPlusSevenDayTravel() {
        val utc = ZoneId.of("UTC")
        val honest = honestNowUnderTravel(7 * 24 * 3600_000L)
        assertEquals(expectedAnchor(2026, 10, 26, utc), MonolithWeek.weekAnchorMillis(honest, utc))
    }

    @Test
    fun spendWeekAnchorIdenticalUnderMinusSevenDayTravel() {
        val utc = ZoneId.of("UTC")
        val honest = honestNowUnderTravel(-7 * 24 * 3600_000L)
        assertEquals(expectedAnchor(2026, 10, 26, utc), MonolithWeek.weekAnchorMillis(honest, utc))
    }
}
