package com.example.data

/**
 * ROUTINES REBUILD — window state as a pure function (Sten §1,
 * Derive-Don't-Fire). One object; no other file in the app may
 * compute "is this window open / what should exist".
 *
 * PURITY, ABSOLUTE: no clock reads, no DAO, no Context, no
 * randomness inside derive(). Every input arrives as a parameter;
 * the same inputs return an equal result, always. This is what
 * makes a stale or hallucinated caller harmless: a wrong *trigger*
 * can only ever produce a correct *evaluation of now*.
 *
 * There is NO test-date suppression anywhere in this file (Ren,
 * 0915 — struck in its widest form): the app does not know that a
 * date is a test day. Routines run on their plain day-mask;
 * Saturday is a day like any other. No SUPPRESSED state exists.
 */

data class WindowInstance(
    val startMillis: Long,       // SCHEDULED start (frozen truth, not delivery time)
    val endMillis: Long,         // SCHEDULED end = start + duration
    val remainderMinutes: Int,   // (endMillis - now) / 60_000, floor, >= 0
    val driftMinutes: Int        // (now - start) / 60_000 when now > start, else 0
)

data class DesiredSession(
    val routineId: Int,
    val windowStartMillis: Long,
    val windowEndMillis: Long,
    val durationMinutes: Int,    // = remainderMinutes, NEVER the full window
    val loadoutName: String,     // routine.loadoutName, applied at materialise
    val intention: String,       // = routine.name
    val profileName: String      // = "Routine: <name>" — legacy rendering only;
                                 // identity is routineId, never this string
)

enum class WindowState {
    DISABLED,        // enabled == false
    PAUSED,          // a pause-until covers the candidate window (pause in force)
    OUTSIDE,         // enabled, no window contains now, nothing owed
    OPEN_IDLE,       // window contains now, no session has ever claimed it
    OPEN_RUNNING,    // window contains now, active session carries this routineId + window
    OPEN_HELD,       // window contains now, a different/no-routine session occupies it
    CLOSED_DUE       // work is owed: an unfinalised routine session exists, or the
                     // latest closed window judges provably absent (MISSED/NOT_DELIVERED)
}

/** The query form of the plan §5.4 outcomes; stamps on session rows
 *  use the same names as strings in `routineOutcome`. UNKNOWN is a
 *  first-class result, not an error: it prints nothing, books
 *  nothing, and breaks a streak walk. */
enum class WindowJudgement { ATTENDED, PARTIAL, MISSED, HELD, LATE_FIRE, NOT_DELIVERED, UNKNOWN }

data class RoutineDerivation(
    val routineId: Int,
    val state: WindowState,
    val window: WindowInstance?,         // the window containing now, if any
    val desiredSession: DesiredSession?, // non-null ONLY in state OPEN_IDLE with remainder >= 5
    val finaliseDue: List<Int>,        // session ids the reconciler must finalise now
    val judgement: WindowJudgement?    // for the latest CLOSED window, if provable (§1.6)
)

data class DeriveResult(
    val derivations: List<RoutineDerivation>, // one per input routine, input order
    val nextBoundaryMillis: Long?,            // the chain alarm's instant
    /** Unfinalised routine sessions whose routine row no longer
     *  exists (deleted mid-window): finalise by stored window (R5). */
    val orphanFinaliseDue: List<Int>
)

object RoutineDerive {

    /** Below this remainder a window never seals (Sten §1.5):
     *  sealing him because Doze napped is the machine punishing
     *  him for its own lateness. */
    const val MIN_REMAINDER_MINUTES = 5

    fun derive(
        routines: List<RoutineEntity>,
        nowMillis: Long,
        activeSession: FocusSessionEntity?,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity
    ): DeriveResult {
        val derivations = routines.map { routine ->
            deriveOne(routine, nowMillis, activeSession, sessions, settings)
        }
        val knownIds = routines.map { it.id }.toSet()
        val orphans = sessions.filter { s ->
            s.routineId != null && s.routineId !in knownIds && s.routineOutcome == null &&
                (!s.isActive || s.windowEndMillis <= nowMillis)
        }.map { it.id }
        return DeriveResult(
            derivations = derivations,
            nextBoundaryMillis = nextBoundary(routines, nowMillis),
            orphanFinaliseDue = orphans
        )
    }

    fun deriveOne(
        routine: RoutineEntity,
        nowMillis: Long,
        activeSession: FocusSessionEntity?,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity
    ): RoutineDerivation {
        val mine = sessions.filter { it.routineId == routine.id }
        val isSleep = routine.loadoutName == SmartLoadout.SLEEP.name

        // Finalise is owed for my rows that are unstamped and either
        // inactive (progress-completed, disarm-routed, orphaned by a
        // dead process) or standing past their stored window end.
        // SLEEP rows are never finalised (J2): they stand down by
        // stored window end through the generic path, stamp nothing.
        val finaliseDue = if (isSleep) {
            emptyList()
        } else {
            mine.filter { s ->
                s.routineOutcome == null && (!s.isActive || s.windowEndMillis <= nowMillis)
            }.map { it.id }
        }

        val judgement = if (isSleep) null else judgeLatestClosed(routine, nowMillis, sessions, settings)

        if (!routine.enabled) {
            return RoutineDerivation(routine.id, WindowState.DISABLED, null, null, finaliseDue, judgement)
        }

        val window = RoutineSchedule.windowContaining(routine, nowMillis)
        if (window != null) {
            // Pause in force over this window's start: the window is
            // paused while now < pausedUntilMillis. Once the pause
            // expires mid-window the window evaluates normally and
            // materialises at remainder (Otis §2.4 amendment / T8) —
            // the pause killed the start, not the whole window.
            if (nowMillis < routine.pausedUntilMillis && window.startMillis < routine.pausedUntilMillis) {
                return RoutineDerivation(routine.id, WindowState.PAUSED, window, null, finaliseDue, judgement)
            }
            val active = activeSession
            if (active != null && active.routineId == routine.id &&
                active.windowStartMillis == window.startMillis
            ) {
                return RoutineDerivation(routine.id, WindowState.OPEN_RUNNING, window, null, finaliseDue, judgement)
            }
            val claim = mine.firstOrNull { it.windowStartMillis == window.startMillis }
            if (claim != null) {
                // The window was claimed and its session already
                // ended: no second mint, ever (§1.5). If the claim
                // is still unstamped, work is owed (CLOSED_DUE);
                // if stamped, the window's business is concluded
                // and its stamp is the judgement of record.
                val state = if (claim.routineOutcome == null) WindowState.CLOSED_DUE else WindowState.OUTSIDE
                return RoutineDerivation(routine.id, state, window, null, finaliseDue, judgement)
            }
            if (active != null) {
                return RoutineDerivation(routine.id, WindowState.OPEN_HELD, window, null, finaliseDue, judgement)
            }
            if (overlapsAny(sessions, window.startMillis, minOf(nowMillis, window.endMillis), exceptRoutineId = routine.id)) {
                return RoutineDerivation(routine.id, WindowState.OPEN_HELD, window, null, finaliseDue, judgement)
            }
            val desired = if (window.remainderMinutes >= MIN_REMAINDER_MINUTES) {
                DesiredSession(
                    routineId = routine.id,
                    windowStartMillis = window.startMillis,
                    windowEndMillis = window.endMillis,
                    durationMinutes = window.remainderMinutes,
                    loadoutName = routine.loadoutName,
                    intention = routine.name,
                    profileName = "Routine: ${routine.name}"
                )
            } else {
                null
            }
            return RoutineDerivation(routine.id, WindowState.OPEN_IDLE, window, desired, finaliseDue, judgement)
        }

        // No containing window.
        if (finaliseDue.isNotEmpty()) {
            return RoutineDerivation(routine.id, WindowState.CLOSED_DUE, null, null, finaliseDue, judgement)
        }
        if (judgement == WindowJudgement.MISSED || judgement == WindowJudgement.NOT_DELIVERED) {
            return RoutineDerivation(routine.id, WindowState.CLOSED_DUE, null, null, finaliseDue, judgement)
        }
        val next = RoutineSchedule.nextWindow(routine, nowMillis)
        if (next != null && nowMillis < routine.pausedUntilMillis && next.startMillis < routine.pausedUntilMillis) {
            return RoutineDerivation(routine.id, WindowState.PAUSED, null, null, finaliseDue, judgement)
        }
        return RoutineDerivation(routine.id, WindowState.OUTSIDE, null, null, finaliseDue, judgement)
    }

    /** §1.6 judgement for the latest closed window (J1–J6, minus the
     *  struck suppression clause of J6: paused/disabled windows do
     *  not exist for judgement — a day he paused in advance cannot
     *  be a miss). */
    private fun judgeLatestClosed(
        routine: RoutineEntity,
        nowMillis: Long,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity
    ): WindowJudgement? {
        val closed = RoutineSchedule.latestClosedWindow(routine, nowMillis) ?: return null
        // J6: a window the pause covered end-to-end never existed.
        if (routine.pausedUntilMillis >= closed.endMillis) return null
        val claim = sessions.firstOrNull {
            it.routineId == routine.id && it.windowStartMillis == closed.startMillis
        }
        if (claim != null) {
            // J1: a stamp IS the judgement; unstamped = pending, not judged here.
            return claim.routineOutcome?.let { stamp ->
                runCatching { WindowJudgement.valueOf(stamp) }.getOrNull()
            }
        }
        // J3: occupied by a session of another kind.
        if (overlapsAny(sessions, closed.startMillis, closed.endMillis, exceptRoutineId = routine.id)) {
            return WindowJudgement.HELD
        }
        // J4: NOT_DELIVERED only if provable — a boot inside the
        // window (this is the most recent closed window by
        // construction of latestClosedWindow).
        if (settings.lastBootAtMillis in closed.startMillis..closed.endMillis) {
            return WindowJudgement.NOT_DELIVERED
        }
        // J4: MISSED if the window provably postdates the last edit,
        // so the current row proves it was scheduled.
        if (closed.startMillis > routine.scheduleEditedAtMillis) {
            return WindowJudgement.MISSED
        }
        // Before the edit horizon the row cannot prove the window
        // was ever scheduled (it may have been paused, disabled, or
        // timed differently) — the record does not invent misses.
        return WindowJudgement.UNKNOWN
    }

    /** Does any session other than [exceptRoutineId]'s own rows
     *  overlap [fromMillis, toMillis]? A session's span is estimated
     *  as [startWallTimeMillis, start + elapsed] (live rows: to
     *  now). Overlap needs positive length. */
    internal fun overlapsAny(
        sessions: List<FocusSessionEntity>,
        fromMillis: Long,
        toMillis: Long,
        exceptRoutineId: Int?
    ): Boolean {
        if (toMillis <= fromMillis) return false
        return sessions.any { s ->
            if (exceptRoutineId != null && s.routineId == exceptRoutineId) {
                false
            } else {
                val spanEnd = if (s.isActive) {
                    Long.MAX_VALUE // still occupying; bounded by the caller's interval
                } else {
                    s.startWallTimeMillis + s.elapsedSeconds * 1000L
                }
                s.startWallTimeMillis < toMillis && spanEnd > fromMillis
            }
        }
    }

    /** Public form of the chain-boundary computation, for callers
     *  (reseed) that hold only the routine snapshot. */
    fun nextBoundaryMillis(routines: List<RoutineEntity>, nowMillis: Long): Long? =
        nextBoundary(routines, nowMillis)

    /** The chain alarm's instant (§2.1 + A5): the minimum over all
     *  enabled, day-selected routines of { next start strictly after
     *  now, end of the window containing now, pause expiry falling
     *  inside an otherwise open window }. */
    private fun nextBoundary(routines: List<RoutineEntity>, nowMillis: Long): Long? {
        var best: Long? = null
        fun offer(candidate: Long?) {
            if (candidate != null && candidate > nowMillis && (best == null || candidate < best!!)) {
                best = candidate
            }
        }
        for (routine in routines) {
            // UNA-09: the chain reads the ONE live definition
            // (RoutineSchedule.isLive) — the same predicate Shield's
            // "n ACTIVE" count prints.
            if (!RoutineSchedule.isLive(routine)) continue
            offer(RoutineSchedule.windowContaining(routine, nowMillis)?.endMillis)
            offer(RoutineSchedule.nextStartMillis(routine, nowMillis))
            val pause = routine.pausedUntilMillis
            if (pause > nowMillis && RoutineSchedule.windowContaining(routine, pause) != null) {
                offer(pause)
            }
        }
        return best
    }
}
