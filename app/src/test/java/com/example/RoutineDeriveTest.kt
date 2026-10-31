package com.example

import com.example.data.DrawerDay
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.GuardSettingsEntity
import com.example.data.RoutineDerive
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.data.TaskQueueStatus
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
        outcome: String? = null,
        endWall: Long = 0L
    ) = FocusSessionEntity(
        id = id, intention = "s$id", profileName = "Routine: X",
        targetDurationSeconds = 3600, elapsedSeconds = elapsed,
        startWallTimeMillis = startWall, startElapsedRealtimeMillis = 0L,
        isActive = active, completedSuccessfully = false,
        routineId = routineId, windowStartMillis = windowStart,
        windowEndMillis = windowEnd, routineOutcome = outcome,
        endWallMillis = endWall
    )

    private val settings = GuardSettingsEntity()

    private fun derive(
        routines: List<RoutineEntity>,
        now: Long,
        active: FocusSessionEntity? = null,
        sessions: List<FocusSessionEntity> = emptyList(),
        settings: GuardSettingsEntity = this.settings,
        excused: Set<String> = emptySet(),
        // Defaulting to null keeps every pre-F-1 case above
        // meaning exactly what it meant before F-1. (Merged-tree
        // note: M-A1 G1 gave the production signature the same
        // default, so main's existing call sites keep compiling;
        // under R1 a null head seals "", never a routine name.)
        headTaskTitle: String? = null
    ) = RoutineDerive.derive(
        routines, now, active, sessions, settings,
        headTaskTitle = headTaskTitle, excusedDateIsos = excused
    )

    // --- F-1 fixtures: his real seeded morning --------------------------
    // FASTED BLOCK 05:00–08:00 (routineTargets) and the seeded queue
    // exactly as ensureSeededAndSyncPackages writes it (MonolithRepository):
    // one ACTIVE head, then QUEUED in queueOrder. Titles verbatim.

    private fun fastedBlock(id: Int = 3) = routine(
        id = id, name = "FASTED BLOCK", start = 300, end = 480
    )

    private fun task(
        id: Int,
        title: String,
        status: TaskQueueStatus,
        queueOrder: Int
    ) = FocusTaskEntity(
        id = id, title = title, queueOrder = queueOrder, status = status.name
    )

    private fun seededMorningQueue() = listOf(
        task(1, "Finish Chapter 4 Problem Set (#1–6)", TaskQueueStatus.ACTIVE, 0),
        task(2, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.QUEUED, 1),
        task(3, "Debug Dynamic Programming Memo Table", TaskQueueStatus.QUEUED, 2)
    )
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
        // Edited Monday, so Friday's window provably postdates the
        // edit horizon (Issue #39 F3: a 0 stamp would judge UNKNOWN).
        val d = derive(listOf(routine(mask = RoutineSchedule.MON_FRI, editedAt = at(0, 0, 0))), now).derivations.single()
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
        // Edited Monday, so the Wed window provably postdates the
        // edit horizon — unexcused it would judge MISSED (issue #39
        // F3); the null below is the J6b amnesty speaking, not the
        // horizon's UNKNOWN.
        val d = derive(listOf(routine(editedAt = at(0, 0, 0))), now, excused = excused).derivations.single()
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
        // Edited Monday (issue #39 F3 horizon), so MISSED is the
        // judgement the excuse would have to suppress to leak.
        val d = derive(listOf(routine(editedAt = at(0, 0, 0))), now, excused = excused).derivations.single()
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

    // --- Issue #39: false-record elimination ----------------------------

    @Test
    fun closedWindow_zeroEditStamp_isUnknown_neverInventedMiss() {
        // F3: editedAt defaults to 0 = no edit recorded under the new
        // engine (migration default). The Wed window cannot be proved
        // scheduled, so no MISSED may be invented for it.
        val now = at(3, 8, 0) // Thu 08:00; latest closed = Wed window
        val d = derive(listOf(routine()), now).derivations.single()
        assertEquals(WindowJudgement.UNKNOWN, d.judgement)
        assertEquals(WindowState.OUTSIDE, d.state)
    }

    @Test
    fun closedWindow_duplicateClaims_unstampedWins_regardlessOfOrder() {
        // F4 (judge site): a stamped duplicate listed FIRST must not
        // mask the unstamped claim — unstamped = pending, no judgement.
        val now = at(3, 8, 0)
        val stamped = session(20, 1, at(2, 9, 0), at(2, 19, 30), outcome = "PARTIAL")
        val unstamped = session(21, 1, at(2, 9, 0), at(2, 19, 30))
        val d = derive(listOf(routine()), now, null, listOf(stamped, unstamped)).derivations.single()
        assertNull(d.judgement)
    }

    @Test
    fun closedWindow_duplicateStampedClaims_lowestIdWins() {
        // F4 fallback: both stamped — the earliest written (lowest
        // id) is the judgement of record, not the first listed.
        val now = at(3, 8, 0)
        val later = session(31, 1, at(2, 9, 0), at(2, 19, 30), outcome = "ATTENDED")
        val earlier = session(24, 1, at(2, 9, 0), at(2, 19, 30), outcome = "PARTIAL")
        val d = derive(listOf(routine()), now, null, listOf(later, earlier)).derivations.single()
        assertEquals(WindowJudgement.PARTIAL, d.judgement)
    }

    @Test
    fun openWindow_duplicateClaims_unstampedWins_regardlessOfOrder() {
        // F4 (open-window site): the stamped duplicate listed first
        // would conclude the window (OUTSIDE); the unstamped claim
        // is the live truth — work is owed (CLOSED_DUE).
        val now = at(2, 12, 0)
        val stamped = session(20, 1, at(2, 9, 0), at(2, 19, 30), outcome = "ATTENDED")
        val unstamped = session(21, 1, at(2, 9, 0), at(2, 19, 30), elapsed = 600)
        val d = derive(listOf(routine()), now, null, listOf(stamped, unstamped)).derivations.single()
        assertEquals(WindowState.CLOSED_DUE, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun overlaps_pausedSession_endWallStampCoversWhereElapsedEstimateMisses() {
        // F5: paused session — 60 elapsed seconds but a wall span of
        // 09:00–12:00. The elapsed estimate ends it at 09:01 and
        // misses the 10:00–11:00 interval; the endWall stamp covers it.
        val paused = session(
            30, null, 0L, 0L, startWall = at(2, 9, 0), elapsed = 60, endWall = at(2, 12, 0)
        )
        assertTrue(RoutineDerive.overlapsAny(listOf(paused), at(2, 10, 0), at(2, 11, 0), exceptRoutineId = 1))
    }

    @Test
    fun overlaps_legacyRowWithoutEndWallStamp_fallsBackToElapsedEstimate() {
        // F5 legacy fallback: endWallMillis == 0 keeps the old
        // start + elapsed span, in both directions.
        val legacy = session(31, null, 0L, 0L, startWall = at(2, 9, 0), elapsed = 3600)
        assertTrue(RoutineDerive.overlapsAny(listOf(legacy), at(2, 9, 30), at(2, 10, 30), exceptRoutineId = 1))
        assertEquals(
            false,
            RoutineDerive.overlapsAny(listOf(legacy), at(2, 10, 30), at(2, 11, 0), exceptRoutineId = 1)
        )
    }

    @Test
    fun openWindow_heldByPausedSession_whoseElapsedEstimateHasEnded() {
        // F5 end-to-end: a manual session that started 08:00, ran 60
        // elapsed seconds across pauses, and wall-ended 10:30 still
        // occupies the 09:00 window opening — the window is HELD,
        // not idle, at 10:00.
        val now = at(2, 10, 0)
        val manual = session(
            32, null, 0L, 0L, startWall = at(2, 8, 0), elapsed = 60, endWall = at(2, 10, 30)
        )
        val d = derive(listOf(routine()), now, null, listOf(manual)).derivations.single()
        assertEquals(WindowState.OPEN_HELD, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun nextBoundary_isEarliestStartEndOrPauseExpiry() {
        val now = at(2, 8, 0) // Wed 08:00 — next is DAY BLOCKS start 09:00
        val result = derive(listOf(routine()), now)
        assertEquals(at(2, 9, 0), result.nextBoundaryMillis)
    }

    // --- F-1: the head resolver (pure; the caller's seam) -----------------

    @Test
    fun resolveHeadTitle_seededMorning_returnsTheActiveHead() {
        assertEquals(
            "Finish Chapter 4 Problem Set (#1–6)",
            RoutineDerive.resolveHeadTitle(seededMorningQueue())
        )
    }

    @Test
    fun resolveHeadTitle_followsDaoOrder_activeAheadOfQueuedRegardlessOfQueueOrder() {
        // The DAO order (MonolithDao getAllTasksSnapshot) sorts by
        // status CASE before queueOrder, so an ACTIVE row precedes a
        // QUEUED row even when the queued row's queueOrder is lower.
        // The resolver honours the list as ordered; it never re-sorts.
        val daoOrdered = listOf(
            task(3, "Debug Dynamic Programming Memo Table", TaskQueueStatus.ACTIVE, 2),
            task(2, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.QUEUED, 0),
            task(1, "Finish Chapter 4 Problem Set (#1–6)", TaskQueueStatus.QUEUED, 1)
        )
        assertEquals(
            "Debug Dynamic Programming Memo Table",
            RoutineDerive.resolveHeadTitle(daoOrdered)
        )
    }

    @Test
    fun resolveHeadTitle_deferredOnly_returnsNull() {
        val deferredOnly = listOf(
            task(4, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.DEFERRED, 0),
            task(5, "Debug Dynamic Programming Memo Table", TaskQueueStatus.DEFERRED, 1)
        )
        assertNull(RoutineDerive.resolveHeadTitle(deferredOnly))
    }

    @Test
    fun resolveHeadTitle_completedRowsNeverHeadline() {
        val completedAndDeferred = listOf(
            task(4, "Finish Chapter 4 Problem Set (#1–6)", TaskQueueStatus.COMPLETED, 0),
            task(5, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.DEFERRED, 1)
        )
        assertNull(RoutineDerive.resolveHeadTitle(completedAndDeferred))
    }

    @Test
    fun resolveHeadTitle_blankHead_skipsToNextEligibleRow() {
        val blankHead = listOf(
            task(1, "   ", TaskQueueStatus.ACTIVE, 0),
            task(2, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.QUEUED, 1)
        )
        assertEquals(
            "Synthesize Lecture 9 Architecture Notes",
            RoutineDerive.resolveHeadTitle(blankHead)
        )
    }

    @Test
    fun resolveHeadTitle_allBlank_returnsNull() {
        val allBlank = listOf(
            task(1, "", TaskQueueStatus.ACTIVE, 0),
            task(2, "  ", TaskQueueStatus.QUEUED, 1)
        )
        assertNull(RoutineDerive.resolveHeadTitle(allBlank))
    }

    @Test
    fun resolveHeadTitle_emptyQueue_returnsNull() {
        assertNull(RoutineDerive.resolveHeadTitle(emptyList()))
    }

    // --- F-1: derive-level intention (the plan's edge-case table) ---------

    @Test
    fun fastedBlock_openWindow_headTitleTakenAsIntention() {
        val now = at(0, 6, 0) // Mon 06:00, inside FASTED BLOCK 05:00–08:00
        val head = RoutineDerive.resolveHeadTitle(seededMorningQueue())
        val d = derive(listOf(fastedBlock()), now, headTaskTitle = head).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
        assertEquals("Finish Chapter 4 Problem Set (#1–6)", d.desiredSession!!.intention)
        // The routine still owns the session's identity and window;
        // only the intention comes from the queue.
        assertEquals("Routine: FASTED BLOCK", d.desiredSession!!.profileName)
        assertEquals(120, d.desiredSession!!.durationMinutes) // remainder to 08:00
    }

    @Test
    fun fastedBlock_openWindow_emptyQueue_sealsEmptyIntention() {
        val now = at(0, 6, 0)
        val head = RoutineDerive.resolveHeadTitle(emptyList())
        val d = derive(listOf(fastedBlock()), now, headTaskTitle = head).derivations.single()
        assertNotNull(d.desiredSession)
        assertEquals("", d.desiredSession!!.intention) // R1: no routine-name fallback — absent head seals "" (#124)
    }

    @Test
    fun fastedBlock_blankHeadPassedDirectly_sealsEmptyIntention() {
        // Wave-2 (Seat F1-Verify, H2): derive() guards itself —
        // a blank head handed straight to derive (bypassing the
        // resolver) must never seal.
        val now = at(0, 6, 0)
        val d = derive(listOf(fastedBlock()), now, headTaskTitle = "   ").derivations.single()
        assertNotNull(d.desiredSession)
        assertEquals("", d.desiredSession!!.intention) // R1: no routine-name fallback — absent head seals "" (#124)
    }

    @Test
    fun fastedBlock_openWindow_deferredOnlyQueue_sealsEmptyIntention() {
        val now = at(0, 6, 0)
        val deferredOnly = listOf(
            task(4, "Synthesize Lecture 9 Architecture Notes", TaskQueueStatus.DEFERRED, 0)
        )
        val head = RoutineDerive.resolveHeadTitle(deferredOnly)
        val d = derive(listOf(fastedBlock()), now, headTaskTitle = head).derivations.single()
        assertNotNull(d.desiredSession)
        assertEquals("", d.desiredSession!!.intention) // R1: no routine-name fallback — absent head seals "" (#124)
    }

    @Test
    fun intentionIsASnapshot_sameInputsSameIntention_laterHeadDoesNotRewriteEarlier() {
        val now = at(0, 6, 0)
        val morning = listOf(fastedBlock())
        val first = derive(morning, now, headTaskTitle = "Finish Chapter 4 Problem Set (#1–6)")
        val again = derive(morning, now, headTaskTitle = "Finish Chapter 4 Problem Set (#1–6)")
        assertEquals(first, again) // derive is pure: same inputs, same intention
        // The queue moved on before the next seal; the earlier
        // derivation's frozen string does not move with it.
        val later = derive(morning, now, headTaskTitle = "Synthesize Lecture 9 Architecture Notes")
        assertEquals(
            "Finish Chapter 4 Problem Set (#1–6)",
            first.derivations.single().desiredSession!!.intention
        )
        assertEquals(
            "Synthesize Lecture 9 Architecture Notes",
            later.derivations.single().desiredSession!!.intention
        )
    }

    @Test
    fun twoRoutinesOneMorning_eachReadsTheHeadAtItsOwnSeal_andNothingIsConsumed() {
        val head = RoutineDerive.resolveHeadTitle(seededMorningQueue())
        val routines = listOf(fastedBlock(), routine()) // + DAY BLOCKS 09:00–19:30
        val atSix = derive(routines, at(0, 6, 0), headTaskTitle = head)
        assertEquals(
            "Finish Chapter 4 Problem Set (#1–6)",
            atSix.derivations.first { it.routineId == 3 }.desiredSession!!.intention
        )
        val atTen = derive(routines, at(0, 10, 0), headTaskTitle = head)
        assertEquals(
            "Finish Chapter 4 Problem Set (#1–6)",
            atTen.derivations.first { it.routineId == 1 }.desiredSession!!.intention
        )
        // Read-only derivation: after both seals the queue head is
        // still the head — sealing completed, promoted or dequeued
        // nothing (the resolver over the same list still answers it).
        assertEquals(
            "Finish Chapter 4 Problem Set (#1–6)",
            RoutineDerive.resolveHeadTitle(seededMorningQueue())
        )
    }

    @Test
    fun sleepRoutine_withHeadAvailable_takesTheHeadTitle_R1() {
        // R1 (lead ruling at the main-merge sitting): the split has
        // no SLEEP carve-out — a present head names the session,
        // SLEEP included; what R1 struck is the routine-NAME
        // fallback when no head exists. (Train's old SLEEP exception
        // sealed the name "SLEEP" here; that was the name fallback
        // in its strongest form, and R1 strikes it everywhere.)
        // Non-interference stands: SLEEP is still never finalised
        // and judges nothing (J2).
        val now = at(2, 21, 0) // Wed 21:00, SLEEP 20:00–03:30
        val sleep = routine(id = 2, name = "SLEEP", start = 1200, end = 210, loadout = SmartLoadout.SLEEP.name)
        val head = RoutineDerive.resolveHeadTitle(seededMorningQueue())
        val d = derive(listOf(sleep), now, headTaskTitle = head).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNotNull(d.desiredSession)
        assertEquals("Finish Chapter 4 Problem Set (#1–6)", d.desiredSession!!.intention)
        assertTrue(d.finaliseDue.isEmpty())
        assertNull(d.judgement)
    }

    @Test
    fun headAvailable_subFiveRemainder_stillNeverSeals() {
        val now = at(0, 7, 57) // 3 minutes left in FASTED BLOCK
        val head = RoutineDerive.resolveHeadTitle(seededMorningQueue())
        val d = derive(listOf(fastedBlock()), now, headTaskTitle = head).derivations.single()
        assertEquals(WindowState.OPEN_IDLE, d.state)
        assertNull(d.desiredSession)
    }

    @Test
    fun headAvailable_pauseInForce_stillPaused_neverSeals() {
        val now = at(0, 6, 0)
        val paused = routine(
            id = 3, name = "FASTED BLOCK", start = 300, end = 480,
            pausedUntil = at(0, 7, 0)
        )
        val head = RoutineDerive.resolveHeadTitle(seededMorningQueue())
        val d = derive(listOf(paused), now, headTaskTitle = head).derivations.single()
        assertEquals(WindowState.PAUSED, d.state)
        assertNull(d.desiredSession)
    }
}
