package com.example

import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * Audit R5 #306 F2 — the forced-on rule for live routines.
 *
 * StayFree's scheduled modes cannot be manually disabled while the
 * schedule runs; Monolith's toggleRoutine was a bare unconditional
 * flip, so the person being blocked could kill the scheduled
 * protection in one tap at the moment it started to bite. The cure
 * gates the flip on [RoutineSchedule.isEnforcingAt]: enabled, not
 * paused, and a window contains the instant. These tests state the
 * predicate's world; the ViewModel guards are direct calls into it.
 *
 * Fixtures use ALL_DAYS masks and same-day Calendar instants, so
 * they hold in any local zone and on any weekday.
 */
class RoutineForcedLiveTest {

    private fun at(hour: Int, minute: Int = 0): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun routine(
        startMinutes: Int,
        endMinutes: Int,
        daysMask: Int = RoutineSchedule.ALL_DAYS,
        enabled: Boolean = true,
        pausedUntilMillis: Long = 0L
    ) = RoutineEntity(
        name = "STUDY",
        startMinutes = startMinutes,
        endMinutes = endMinutes,
        daysMask = daysMask,
        loadoutName = "STUDY",
        enabled = enabled,
        pausedUntilMillis = pausedUntilMillis
    )

    @Test
    fun insideWindow_enforcing() {
        // 09:00–13:00, now 10:00 — the window is live, the off-switch
        // must be refused.
        val r = routine(startMinutes = 540, endMinutes = 780)
        assertTrue(RoutineSchedule.isEnforcingAt(r, at(10)))
    }

    @Test
    fun outsideWindow_notEnforcing() {
        val r = routine(startMinutes = 540, endMinutes = 780)
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(14)))
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(8)))
    }

    @Test
    fun overnightWindow_enforcingAfterMidnight() {
        // 22:30–07:00 — at 02:00 the window that started yesterday
        // is still running, so the routine is still forced-on.
        val r = routine(startMinutes = 1350, endMinutes = 420)
        assertTrue(RoutineSchedule.isEnforcingAt(r, at(2)))
        assertTrue(RoutineSchedule.isEnforcingAt(r, at(23)))
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(12)))
    }

    @Test
    fun disabledRoutine_neverEnforcing() {
        val r = routine(startMinutes = 540, endMinutes = 780, enabled = false)
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(10)))
    }

    @Test
    fun pausedRoutine_notEnforcing_staysEditable() {
        // A pause already in force means protection is not biting;
        // the owner keeps the off-switch.
        val now = at(10)
        val r = routine(
            startMinutes = 540, endMinutes = 780,
            pausedUntilMillis = now + 3_600_000L
        )
        assertFalse(RoutineSchedule.isEnforcingAt(r, now))
    }

    @Test
    fun windowBoundary_startInclusive_endExclusive() {
        val r = routine(startMinutes = 540, endMinutes = 780)
        assertTrue(RoutineSchedule.isEnforcingAt(r, at(9)))
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(13)))
    }

    @Test
    fun dayNotInMask_notEnforcing() {
        // Mask excludes today (and an all-days-minus-today mask still
        // contains yesterday, whose 09:00–13:00 window ended yesterday).
        val todayBit = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val mask = RoutineSchedule.ALL_DAYS xor (1 shl todayBit)
        val r = routine(startMinutes = 540, endMinutes = 780, daysMask = mask)
        assertFalse(RoutineSchedule.isEnforcingAt(r, at(10)))
    }
}
