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
 * can only ever produce a correct *evaluation of now*. F-1's
 * queue-head title honours the same law: the caller resolves it
 * (resolveHeadTitle over its own DAO snapshot) and passes the
 * resulting String? in — derive() never reads the task queue.
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
    val intention: String,       // F-1 (issue #12): the queue head's title as
                                 // resolved by the caller (see resolveHeadTitle);
                                 // the routine's own name when no head exists,
                                 // and ALWAYS the routine's own name for SLEEP
                                 // routines (lead ruling: the head names work,
                                 // never sleep)
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

    /**
     * F-1 (issue #12): resolves the queue head's title from the
     * caller's ordered task snapshot. PURE — a list in, a title
     * (or null) out; no DAO, no Context, no clock — so the rule
     * is unit-tested directly and derive() purity is untouched:
     * derive() only ever receives the String? this returns.
     *
     * Input contract: [tasks] in the DAO's queue order (MonolithDao
     * getAllTasksSnapshot: status CASE ACTIVE, QUEUED, DEFERRED,
     * else; queueOrder ASC; id ASC). Candidates are ACTIVE or
     * QUEUED rows only — a DEFERRED task is "not today" and NEVER
     * headlines a session (plan ruling 7: no next-up helper with a
     * DEFERRED fallback may serve this seam), and COMPLETED rows
     * are the Drawer's record, not the queue's future. A blank or
     * whitespace title is skipped to the next eligible row. Null
     * when nothing is eligible (empty queue, DEFERRED-only,
     * all-blank): the caller passes null on and deriveOne falls
     * back to the routine name — the pre-F-1 behaviour.
     */
    fun resolveHeadTitle(tasks: List<FocusTaskEntity>): String? =
        tasks.firstOrNull { task ->
            (task.resolvedStatus == TaskQueueStatus.ACTIVE ||
                task.resolvedStatus == TaskQueueStatus.QUEUED) &&
                task.title.isNotBlank()
        }?.title

    fun derive(
        routines: List<RoutineEntity>,
        nowMillis: Long,
        activeSession: FocusSessionEntity?,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity,
        headTaskTitle: String? = null,
        excusedDateIsos: Set<String> = emptySet()
    ): DeriveResult {
        val derivations = routines.map { routine ->
            deriveOne(
                routine, nowMillis, activeSession, sessions, settings,
                headTaskTitle = headTaskTitle, excusedDateIsos = excusedDateIsos
            )
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
        settings: GuardSettingsEntity,
        headTaskTitle: String? = null,
        excusedDateIsos: Set<String> = emptySet()
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
            val base = mine.filter { s ->
                s.routineOutcome == null && (!s.isActive || s.windowEndMillis <= nowMillis)
            }.map { it.id }
            // #153 F1 (CRITICAL): a routine switched OFF mid-window
            // frees the day — its still-active session is finalise-
            // due NOW, not at its stored window end. The heartbeat
            // finalises it and stands the shield down in the same
            // pass. (SLEEP rows keep the J2 stand-down path, widened
            // in the heartbeat to disabled rows.)
            if (!routine.enabled) {
                (base + mine.filter { s ->
                    s.isActive && s.routineOutcome == null
                }.map { it.id }).distinct()
            } else {
                base
            }
        }

        val judgement = if (isSleep) null else judgeLatestClosed(routine, nowMillis, sessions, settings, excusedDateIsos)

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
            // Excused date (issue #137, F1b): the window's filing
            // date carries an excuse word (ILL / REST / FAMILY), so
            // no session materialises for it. The state reuses
            // PAUSED — the existing "this window does not run"
            // state; no new vocabulary is invented for it.
            if (isExcused(window.startMillis, excusedDateIsos)) {
                return RoutineDerivation(routine.id, WindowState.PAUSED, window, null, finaliseDue, judgement)
            }
            val active = activeSession
            if (active != null && active.routineId == routine.id &&
                active.windowStartMillis == window.startMillis
            ) {
                return RoutineDerivation(routine.id, WindowState.OPEN_RUNNING, window, null, finaliseDue, judgement)
            }
            val claim = pickClaim(mine.filter { it.windowStartMillis == window.startMillis })
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
                    // R1 (lead ruling at the main-merge sitting,
                    // on M-A1 OPEN-2 / M-A3 §7 H3): the head names
                    // the session when the queue has one — his own
                    // queued words, frozen at seal; later queue edits
                    // cannot rewrite it, and sealing consumes nothing.
                    // When there is no head, the intention is ""
                    // (issue #124): a routine arms the block, not the
                    // words — the routine’s NAME is never filed as
                    // his declaration, SLEEP included, no exceptions.
                    intention = headTaskTitle?.takeIf { it.isNotBlank() } ?: "",
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
    /** True when [windowStartMillis]'s filing date (DrawerDay's
     *  03:30 rule — the same date the Drawer files the word under)
     *  is one of the excused date isos the caller read from the
     *  day-word stores. */
    private fun isExcused(windowStartMillis: Long, excusedDateIsos: Set<String>): Boolean =
        DrawerDay.dateIso(DrawerDay.filingDate(windowStartMillis)) in excusedDateIsos

    private fun judgeLatestClosed(
        routine: RoutineEntity,
        nowMillis: Long,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity,
        excusedDateIsos: Set<String> = emptySet()
    ): WindowJudgement? {
        val closed = RoutineSchedule.latestClosedWindow(routine, nowMillis) ?: return null
        // J6: a window the pause covered end-to-end never existed.
        if (routine.pausedUntilMillis >= closed.endMillis) return null
        // J6b (issue #137, F1a): a window whose filing date carries
        // an excuse word gets the same amnesty as J6 — it is never
        // judged MISSED (or NOT_DELIVERED) for a day he declared.
        if (isExcused(closed.startMillis, excusedDateIsos)) return null
        val claim = pickClaim(sessions.filter {
            it.routineId == routine.id && it.windowStartMillis == closed.startMillis
        })
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
        // Issue #39 F3: a 0 stamp means NO edit was ever recorded
        // under the new engine (migration default, MonolithDatabase
        // 8→9) — it is not an edit at time zero. Without a recorded
        // edit the row cannot prove the window was ever scheduled,
        // so the judgement is UNKNOWN, never an invented MISSED
        // (the law printed on RoutineEntity.scheduleEditedAtMillis).
        if (routine.scheduleEditedAtMillis == 0L) {
            return WindowJudgement.UNKNOWN
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

    /** Issue #39 F4: the claim of record for a window, chosen
     *  deterministically — never by list order. Duplicate rows for
     *  one window exist (races, restores); an unstamped row is the
     *  live truth (work still owed / judgement still pending), so
     *  it wins over any stamped duplicate that would mask it. Among
     *  rows of the same stamped-ness, the lowest id (the earliest
     *  written) wins. */
    private fun pickClaim(candidates: List<FocusSessionEntity>): FocusSessionEntity? =
        candidates.minWithOrNull(
            compareBy<FocusSessionEntity> { it.routineOutcome != null }.thenBy { it.id }
        )

    /** Does any session other than [exceptRoutineId]'s own rows
     *  overlap [fromMillis, toMillis]? A finished session's span is
     *  [startWallTimeMillis, endWallMillis] when its v11 ending
     *  stamp exists (Issue #39 F5 — the elapsed-seconds estimate
     *  undercounts paused sessions, whose wall span runs long past
     *  start + elapsed; the MonolithDao covering predicate reads
     *  the same stamp the same way); legacy rows with no stamp
     *  (endWallMillis == 0) fall back to the elapsed estimate.
     *  Live rows span to Long.MAX_VALUE. Overlap needs positive
     *  length. */
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
                val spanEnd = when {
                    s.isActive -> Long.MAX_VALUE // still occupying; bounded by the caller's interval
                    s.endWallMillis > 0L -> s.endWallMillis
                    else -> s.startWallTimeMillis + s.elapsedSeconds * 1000L
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
            // "n ARMED" count prints (R-6 wording).
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
