package com.example

import com.example.data.DrawerDay
import com.example.data.FocusSessionEntity
import com.example.data.GuardSettingsEntity
import com.example.data.RoutineDerive
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.data.WindowJudgement
import com.example.data.WindowState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * derive() as a pure function (routines rebuild §1) — plain JUnit, no
 * Android. Covers Sten's acceptance set: stale/duplicate hints are
 * irrelevant (derive never sees alarms), rename/delete safety,
 * Saturday as a plain day (Ren 0915 — the suppression clause is
 * struck), the eligibility horizon, pause semantics, idempotence,
 * boundary arithmetic, orphan detection.
 *
 * Week of the fixtures: Mon 2026-10-26 … Sun 2026-11-01, local zone —
 * the same Calendar math production uses.
 */
class RoutineDeriveTest {

    private fun at(dayOffsetFromMonday: Int, hour: Int, minute: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 26, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, dayOffsetFromMonday)
        return cal.timeInMillis
    }

    private fun routine(
        id: Int = 1,
        name: String = "DAY BLOCKS",
        start: Int = 540,
        end: Int = 1170,
        mask: Int = RoutineSchedule.ALL_DAYS,
        loadout: String = SmartLoadout.STUDY.name,
        enabled: Boolean = true,
        pausedUntil: Long = 0L,
        editedAt: Long = 0L
    ) = RoutineEntity(
        id = id, name = name, startMinutes = start, endMinutes = end,
        daysMask = mask, loadoutName = loadout, enabled = enabled,
        pausedUntilMillis = pausedUntil, scheduleEditedAtMillis = editedAt
    )

    private fun session(
        id: Int,
        routineId: Int?,
        windowStart: Long,
        windowEnd: Long,
        startWall: Long = windowStart,
        elapsed: Int = 0,
        active: Boolean = false,
        outcome: String? = null
    ) = FocusSessionEntity(
        id = id, intention = "s$id", profileName = "Routine: X",
        targetDurationSeconds = 3600, elapsedSeconds = elapsed,
        startWallTimeMillis = startWall, startElapsedRealtimeMillis = 0L,
        isActive = active, completedSuccessfully = false,
        routineId = routineId, windowStartMillis = windowStart,
        windowEndMillis = windowEnd, routineOutcome = outcome
    )

    private val settings = GuardSettingsEntity()

    private fun derive(
        routines: List<RoutineEntity>,
        now: Long,
        active: FocusSessionEntity? = null,
        sessions: List<FocusSessionEntity> = emptyList(),
        settings: GuardSettingsEntity = this.settings,
        excused: Set<String> = emptySet()
    ) = RoutineDerive.derive(routines, now, active, sessions, settings, excused)

    /** The filing-date iso of an instant, as the Drawer files it. */
    private fun filingIso(millis: Long): String =
        DrawerDay.dateIso(DrawerDay.filingDate(millis))

    // --- Window states -------------------------------------------------

    @Test
    fun openWindow_noSession_desiresRemainderSession() {
        val now = at(2, 10, 0) // Wed 10:00, DAY BLOCKS 09:00–19:30
        val result = derive(listOf(routine()), now)
        val d = result.derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
        assertEquals(at(2, 9, 0), d.desiredSession!!.windowStartMillis)
        assertEquals(at(2, 19, 30), d.desiredSession!!.windowEndMillis)
        assertEquals(570, d.desiredSession!!.durationMinutes) // remainder, NOT 630
        assertEquals(at(2, 19, 30), result.nextBoundaryMillis)
    }

    @Test
    fun openWindow_withOwnActiveSession_isRunning() {
        val now = at(2, 10, 0)
        val active = session(7, 1, at(2, 9, 0), at(2, 19, 30), active = true)
        val d = derive(listOf(routine()), now, active, listOf(active)).derivations.single()
        assertEquals(WindowState.OPEN_RUNNING, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun openWindow_heldByManualSession_neverSteamrolled() {
        val now = at(2, 10, 0)
        val manual = session(8, null, 0L, 0L, startWall = at(2, 9, 30), active = true)
        val d = derive(listOf(routine()), now, manual, listOf(manual)).derivations.single()
        assertEquals(WindowState.OPEN_HELD, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun openWindow_claimedByFinishedUnstampedSession_isClosedDue_neverReminted() {
        val now = at(2, 12, 0)
        val claim = session(9, 1, at(2, 9, 0), at(2, 19, 30), elapsed = 600)
        val d = derive(listOf(routine()), now, null, listOf(claim)).derivations.single()
        assertEquals(WindowState.CLOSED_DUE, d.state)
        assertNull(d.desiredSession)
        assertTrue(d.finaliseDue.contains(9))
    }

    @Test
    fun disabledRoutine_isDisabled_butFinaliseStillOwed() {
        val now = at(2, 12, 0)
        val stale = session(9, 1, at(1, 9, 0), at(1, 19, 30), elapsed = 100)
        val d = derive(listOf(routine(enabled = false)), now, null, listOf(stale)).derivations.single()
        assertEquals(WindowState.DISABLED, d.state)
        assertTrue(d.finaliseDue.contains(9))
    }

    // --- Saturday is a plain day (Ren 0915) -----------------------------

    @Test
    fun saturday_allDaysRoutine_derivesNormally() {
        val now = at(5, 10, 0) // Saturday 10:00
        val d = derive(listOf(routine()), now).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
    }

    @Test
    fun saturday_monFriRoutine_simplyHasNoWindow() {
        val now = at(5, 10, 0) // Saturday
        val d = derive(listOf(routine(mask = RoutineSchedule.MON_FRI)), now).derivations.single()
        // No Saturday window exists and nothing is desired — Saturday
        // is simply not one of its days. The state is CLOSED_DUE
        // because FRIDAY's window (provably scheduled, never claimed)
        // judges MISSED: the state machine reports the owed work,
        // which is the day-mask speaking, not any date predicate.
        assertNull(d.window)
        assertNull(d.desiredSession)
        assertEquals(WindowJudgement.MISSED, d.judgement)
        assertEquals(WindowState.CLOSED_DUE, d.state)
    }

    // --- Judgements ------------------------------------------------------

    @Test
    fun closedWindow_stampIsTheJudgement() {
        val now = at(3, 8, 0) // Thu 08:00; latest closed = Wed window
        val stamped = session(9, 1, at(2, 9, 0), at(2, 19, 30), outcome = "PARTIAL")
        val d = derive(listOf(routine()), now, null, listOf(stamped)).derivations.single()
        assertEquals(WindowJudgement.PARTIAL, d.judgement)
    }

    @Test
    fun closedWindow_noSession_bootInsideWindow_isNotDelivered() {
        val now = at(3, 8, 0)
        val booted = GuardSettingsEntity(lastBootAtMillis = at(2, 15, 0))
        val d = derive(listOf(routine()), now, settings = booted).derivations.single()
        assertEquals(WindowJudgement.NOT_DELIVERED, d.judgement)
    }

    @Test
    fun closedWindow_noSession_afterEditHorizon_isMissed() {
        val now = at(3, 8, 0)
        val r = routine(editedAt = at(0, 0, 0)) // edited Monday; Wed window postdates it
        val d = derive(listOf(r), now).derivations.single()
        assertEquals(WindowJudgement.MISSED, d.judgement)
    }

    @Test
    fun closedWindow_beforeEditHorizon_isUnknown_neverInventedMiss() {
        val now = at(3, 8, 0)
        val r = routine(editedAt = at(3, 0, 0)) // edited Thu 00:00; Wed window predates it
        val d = derive(listOf(r), now).derivations.single()
        assertEquals(WindowJudgement.UNKNOWN, d.judgement)
    }

    // --- Excused dates (issue #137, F1) --------------------------------

    @Test
    fun excusedClosedWindow_judgesNothing_neverMissed() {
        val now = at(3, 8, 0) // Thu 08:00; latest closed = Wed window
        val excused = setOf(filingIso(at(2, 9, 0))) // Wed's filing date
        val d = derive(listOf(routine()), now, excused = excused).derivations.single()
        assertNull(d.judgement)
        assertEquals(WindowState.OUTSIDE, d.state)
    }

    @Test
    fun excusedOpenWindow_desiresNoSession() {
        val now = at(2, 10, 0) // Wed 10:00, inside DAY BLOCKS
        val excused = setOf(filingIso(at(2, 9, 0)))
        val d = derive(listOf(routine()), now, excused = excused).derivations.single()
        assertNull(d.desiredSession)
        assertEquals(WindowState.PAUSED, d.state)
    }

    @Test
    fun unexcusedClosedWindow_stillMissed() {
        // Control: a different date's excuse does not leak.
        val now = at(3, 8, 0)
        val excused = setOf(filingIso(at(0, 9, 0))) // Monday only
        val d = derive(listOf(routine()), now, excused = excused).derivations.single()
        assertEquals(WindowJudgement.MISSED, d.judgement)
    }

    // --- Pause semantics ---------------------------------------------------

    @Test
    fun fullyPausedClosedWindow_judgesNothing() {
        val now = at(3, 8, 0)
        val r = routine(pausedUntil = at(3, 0, 0)) // pause covers Wed window end-to-end
        val d = derive(listOf(r), now).derivations.single()
        assertNull(d.judgement)
    }

    @Test
    fun pauseInForce_overOpenWindow_isPaused() {
        val now = at(2, 10, 0)
        val r = routine(pausedUntil = at(2, 18, 0))
        val d = derive(listOf(r), now).derivations.single()
        assertEquals(WindowState.PAUSED, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun pauseExpiredMidWindow_windowMaterialisesAtRemainder() {
        // Otis T8: pause ended 11:00, now 12:00 — the window lives.
        val now = at(2, 12, 0)
        val r = routine(pausedUntil = at(2, 11, 0))
        val d = derive(listOf(r), now).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
    }

    // --- Orphans + idempotence --------------------------------------------

    @Test
    fun sessionWhoseRoutineWasDeleted_isOrphanFinaliseDue() {
        val now = at(3, 8, 0)
        val orphan = session(11, 99, at(2, 9, 0), at(2, 19, 30), elapsed = 300)
        val result = derive(listOf(routine()), now, null, listOf(orphan))
        assertEquals(listOf(11), result.orphanFinaliseDue)
    }

    @Test
    fun derive_isIdempotent_sameInputsSameOutputs() {
        val now = at(2, 12, 0)
        val claim = session(9, 1, at(2, 9, 0), at(2, 19, 30), elapsed = 600)
        val routines = listOf(routine(), routine(id = 2, name = "SLEEP", start = 1200, end = 210, loadout = SmartLoadout.SLEEP.name))
        val a = derive(routines, now, null, listOf(claim))
        val b = derive(routines, now, null, listOf(claim))
        assertEquals(a, b)
    }

    @Test
    fun remainderBelowFiveMinutes_neverSeals() {
        val now = at(2, 19, 27) // 3 minutes left in DAY BLOCKS
        val d = derive(listOf(routine()), now).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun sleepRoutine_openWindow_stillMaterialises_butNeverFinalises() {
        val now = at(2, 21, 0) // Wed 21:00, SLEEP 20:00–03:30
        val sleep = routine(id = 2, name = "SLEEP", start = 1200, end = 210, loadout = SmartLoadout.SLEEP.name)
        val stale = session(12, 2, at(1, 20, 0), at(2, 3, 30), elapsed = 400)
        val d = derive(listOf(sleep), now, null, listOf(stale)).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
        assertTrue(d.finaliseDue.isEmpty()) // J2: SLEEP is never finalised
        assertNull(d.judgement)
    }

    @Test
    fun nextBoundary_isEarliestStartEndOrPauseExpiry() {
        val now = at(2, 8, 0) // Wed 08:00 — next is DAY BLOCKS start 09:00
        val result = derive(listOf(routine()), now)
        assertEquals(at(2, 9, 0), result.nextBoundaryMillis)
    }
}
