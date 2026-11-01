package com.example

import com.example.data.DrawerDay
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

/**
 * Issue #155 (T9, re-close bench) — pins for the refusal-aware
 * next-start derivation behind the widget's NEXT ROUTINE line.
 *
 * Before this cure the widget derived the line from isLive +
 * effectiveNextStartMillis alone (pause-aware, refusal-blind): on
 * an excused date (ILL / REST / FAMILY day-word) or over a
 * tombstoned window it kept naming a next seal the routine
 * heartbeat was bound to refuse. nextStartMillisRefusalAware
 * consumes the heartbeat's own refusal set — excused filing dates
 * and tombstones — and falls silent (null) where no unrefused
 * start can be proven inside the walk horizon (WING-D C1).
 *
 * Zone pinned to Asia/Kolkata (no DST) so the wall-clock fixtures
 * are exact; the filing-date isos are computed through DrawerDay
 * itself, the same construction the heartbeat uses.
 */
class Issue155NextStartRefusalTest {

    private lateinit var originalZone: TimeZone
    private val zone: ZoneId = ZoneId.of("Asia/Kolkata")

    @Before
    fun pinZone() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone(zone))
    }

    @After
    fun restoreZone() {
        TimeZone.setDefault(originalZone)
    }

    private fun instant(local: LocalDateTime): Long =
        local.atZone(zone).toInstant().toEpochMilli()

    private fun isoOf(millis: Long): String =
        DrawerDay.dateIso(DrawerDay.filingDate(millis))

    /** Daily 19:00-20:00 routine; "now" in the tests is Monday
     *  2026-11-02, 10:00 local — today's window is ahead. */
    private fun daily(pausedUntilMillis: Long = 0L) = RoutineEntity(
        id = 7, name = "EVENING", startMinutes = 19 * 60, endMinutes = 20 * 60,
        daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name,
        pausedUntilMillis = pausedUntilMillis
    )

    private val now = instant(LocalDateTime.of(2026, 11, 2, 10, 0))
    private val todayStart = instant(LocalDateTime.of(2026, 11, 2, 19, 0))
    private val tomorrowStart = instant(LocalDateTime.of(2026, 11, 3, 19, 0))
    private val wednesdayStart = instant(LocalDateTime.of(2026, 11, 4, 19, 0))

    private fun next(
        routine: RoutineEntity,
        excused: Set<String> = emptySet(),
        tombstoned: Set<Long> = emptySet()
    ): Long? = RoutineSchedule.nextStartMillisRefusalAware(
        routine, now, excused
    ) { _, start -> start in tombstoned }

    @Test
    fun unrefusedCandidatePassesThrough() {
        assertEquals(todayStart, next(daily()))
    }

    @Test
    fun excusedTodayStepsToTomorrow() {
        assertEquals(tomorrowStart, next(daily(), excused = setOf(isoOf(todayStart))))
    }

    @Test
    fun tombstonedTodayStepsToTomorrow() {
        assertEquals(tomorrowStart, next(daily(), tombstoned = setOf(todayStart)))
    }

    @Test
    fun excuseAndTombstoneChainStepsPastBoth() {
        assertEquals(
            wednesdayStart,
            next(
                daily(),
                excused = setOf(isoOf(todayStart)),
                tombstoned = setOf(tomorrowStart)
            )
        )
    }

    @Test
    fun tombstoneOnALaterWindowDoesNotMoveTodaysAnswer() {
        assertEquals(todayStart, next(daily(), tombstoned = setOf(tomorrowStart)))
    }

    @Test
    fun everyWindowRefusedFallsSilent() {
        val excused = (0..20).map { offset ->
            isoOf(instant(LocalDateTime.of(2026, 11, 2, 19, 0).plusDays(offset.toLong())))
        }.toSet()
        assertNull(next(daily(), excused = excused))
    }

    @Test
    fun pauseCompositionIsPreservedUnderTheWalk() {
        // Paused until tomorrow noon: the effective (pause-aware)
        // start is already tomorrow; an excuse on today must not
        // resurrect or shift it, and the walk still applies after.
        val paused = daily(pausedUntilMillis = instant(LocalDateTime.of(2026, 11, 3, 12, 0)))
        assertEquals(tomorrowStart, next(paused, excused = setOf(isoOf(todayStart))))
        assertEquals(
            wednesdayStart,
            next(paused, tombstoned = setOf(tomorrowStart))
        )
    }
}
