package com.example

import com.example.data.FocusSessionEntity
import com.example.data.GuardSettingsEntity
import com.example.data.RoutineDerive
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.data.WindowState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/** #153 change-of-plans-day: the day the plan collapses must be
 *  survivable. F1: OFF mid-window frees the day. F2/F3: skip-today
 *  horizon math — the pause forgives exactly one window. */
class RoutineChangeOfPlansTest {

    private fun at(dayOffsetFromMonday: Int, hour: Int, minute: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 26, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, dayOffsetFromMonday)
        return cal.timeInMillis
    }

    private fun routine(
        enabled: Boolean = true,
        pausedUntil: Long = 0L
    ) = RoutineEntity(
        id = 1, name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
        daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name,
        enabled = enabled, pausedUntilMillis = pausedUntil, scheduleEditedAtMillis = 0L
    )

    private fun session(active: Boolean, windowStart: Long, windowEnd: Long) = FocusSessionEntity(
        id = 7, intention = "s7", profileName = "Routine: X",
        targetDurationSeconds = 3600, elapsedSeconds = 600,
        startWallTimeMillis = windowStart, startElapsedRealtimeMillis = 0L,
        isActive = active, completedSuccessfully = false,
        routineId = 1, windowStartMillis = windowStart,
        windowEndMillis = windowEnd, routineOutcome = null
    )

    @Test
    fun f1_disabledMidWindow_activeSessionIsFinaliseDue() {
        val now = at(0, 12, 0) // Monday 12:00, inside 09:00–19:30
        val active = session(true, at(0, 9, 0), at(0, 19, 30))
        val result = RoutineDerive.derive(
            listOf(routine(enabled = false)), now, active, listOf(active), GuardSettingsEntity()
        )
        val d = result.derivations.single()
        assertEquals(WindowState.DISABLED, d.state)
        assertTrue("disabled routine must free its running session", d.finaliseDue.contains(7))
    }

    @Test
    fun f1_enabledMidWindow_activeSessionIsNotFinaliseDue() {
        val now = at(0, 12, 0)
        val active = session(true, at(0, 9, 0), at(0, 19, 30))
        val result = RoutineDerive.derive(
            listOf(routine(enabled = true)), now, active, listOf(active), GuardSettingsEntity()
        )
        assertTrue(result.derivations.single().finaliseDue.isEmpty())
    }

    @Test
    fun f3_skipTodayHorizon_isEndOfContainingWindow() {
        val now = at(0, 12, 0)
        assertEquals(at(0, 19, 30), RoutineSchedule.skipWindowEndMillis(routine(), now))
    }

    @Test
    fun f3_skipTodayHorizon_beforeWindow_isEndOfNextWindow() {
        val now = at(0, 7, 0) // before today's 09:00 window
        assertEquals(at(0, 19, 30), RoutineSchedule.skipWindowEndMillis(routine(), now))
    }

    @Test
    fun f2_pauseCoveringWindowStart_derivesPaused() {
        val now = at(0, 12, 0)
        val paused = routine(pausedUntil = at(0, 19, 30))
        val result = RoutineDerive.derive(
            listOf(paused), now, null, emptyList(), GuardSettingsEntity()
        )
        assertEquals(WindowState.PAUSED, result.derivations.single().state)
    }
}
