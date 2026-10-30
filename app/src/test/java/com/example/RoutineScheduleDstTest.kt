package com.example

import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

/**
 * Audit R5 #259 (R5-0266): routine window ends are WALL-CLOCK times.
 * An overnight window stored as 22:00–06:00 must end at 06:00 local
 * on DST transition nights too — the old start + elapsed-duration
 * arithmetic ended it at 07:00 on spring-forward night (an hour
 * late) and 05:00 on fall-back night (an hour early).
 *
 * Fixtures pin the default zone to America/New_York; production
 * reads the same system-default zone in both Calendar and java.time.
 */
class RoutineScheduleDstTest {

    private lateinit var originalZone: TimeZone
    private val zone: ZoneId = ZoneId.of("America/New_York")

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

    private fun overnight() = RoutineEntity(
        id = 1, name = "NIGHT", startMinutes = 22 * 60, endMinutes = 6 * 60,
        daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name
    )

    @Test
    fun springForwardNight_windowEndsAtSixWallClock() {
        // DST begins Sun 2026-03-08 02:00 -> 03:00 in New York.
        val routine = overnight()
        val during = instant(LocalDateTime.of(2026, 3, 8, 5, 30))
        val window = RoutineSchedule.windowContaining(routine, during)
        assertNotNull(window)
        assertEquals(
            instant(LocalDateTime.of(2026, 3, 8, 6, 0)),
            window!!.endMillis
        )
        // 06:30 local is outside — the old arithmetic held it until 07:00.
        assertNull(
            RoutineSchedule.windowContaining(
                routine, instant(LocalDateTime.of(2026, 3, 8, 6, 30))
            )
        )
    }

    @Test
    fun fallBackNight_windowEndsAtSixWallClock() {
        // DST ends Sun 2026-11-01 02:00 -> 01:00 in New York.
        val routine = overnight()
        val during = instant(LocalDateTime.of(2026, 11, 1, 5, 30))
        val window = RoutineSchedule.windowContaining(routine, during)
        assertNotNull(window)
        assertEquals(
            instant(LocalDateTime.of(2026, 11, 1, 6, 0)),
            window!!.endMillis
        )
        assertNull(
            RoutineSchedule.windowContaining(
                routine, instant(LocalDateTime.of(2026, 11, 1, 6, 30))
            )
        )
    }

    @Test
    fun ordinaryNight_endMatchesElapsedArithmetic() {
        // No transition on 2026-03-14 -> 15 (EDT throughout): the
        // wall-clock end equals start + 8 elapsed hours exactly.
        val routine = overnight()
        val start = instant(LocalDateTime.of(2026, 3, 14, 22, 0))
        val during = instant(LocalDateTime.of(2026, 3, 15, 3, 0))
        val window = RoutineSchedule.windowContaining(routine, during)
        assertNotNull(window)
        assertEquals(start + 8 * 3_600_000L, window!!.endMillis)
    }

    @Test
    fun springForwardNight_nextWindowEndsAtSixWallClock() {
        val routine = overnight()
        val before = instant(LocalDateTime.of(2026, 3, 7, 12, 0))
        val next = RoutineSchedule.nextWindow(routine, before)
        assertNotNull(next)
        assertEquals(
            instant(LocalDateTime.of(2026, 3, 8, 6, 0)),
            next!!.endMillis
        )
    }
}
