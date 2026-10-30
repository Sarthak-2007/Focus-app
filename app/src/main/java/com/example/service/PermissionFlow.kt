package com.example.service

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ============================================================================
 * THE PERMISSION FLOW (issue #324, finding 4 — BUILD half): the grant
 * sequence as a sequenced, persisted flow instead of a manual serial
 * loop over the PARK board's FIX keys.
 *
 * What stands on main without this file: the Watch Store measures the
 * six grants and PARK prints them cheapest-first
 * ([WatchItemId.PERMISSIONS], issue #309 F5); each FIX key fires one
 * Settings trip and [PermissionReturnService] walks the user back
 * from that one trip. But no state anywhere records WHERE in the
 * sequence the user is: leave mid-sequence — Settings, Home, process
 * death — and the trip back starts from memory, not from a record.
 * Zario's funnel is resumable because its step state is persisted
 * (AUDIT_ROUND5.md, Zario second pass, finding 4); Monolith's primer
 * was dead code and its grant sequence had no resume state at all.
 *
 * This file is the build half of the cure. The DESIGN half — the
 * flow's final presentation, per-permission explainers after value is
 * shown — is owned by the guard/warrant programme on issue #58; what
 * lives here is the machinery that half will stand on:
 *
 *  - [PermissionFlow]: the pure sequencing core. The current step is
 *    DERIVED from the Watch snapshot's own prints — the first
 *    measured lack in board order — never stored, so it can never
 *    disagree with the measured truth the board prints.
 *  - [PermissionFlowRecord]: the persisted step state — when the
 *    flow started, which steps have been PRESENTED (their FIX trip
 *    fired), the step the user stopped at, and when the full set
 *    first stood granted. Only facts are durable; measurements stay
 *    in the Watch Store, which deliberately does not persist them
 *    (its header states why: a restored measurement would print old
 *    proof as fresh).
 *  - [PermissionFlowStore]: the record's durable home —
 *    SharedPreferences, written with commit() the instant a fact
 *    lands (the Watch Store's LAST PROVED standard, issue #48), and
 *    restored by [PermissionFlowStore.bind] in the next process, so
 *    an interrupted sequence resumes at the step it stopped at.
 *
 * The primer this state serves is ShieldPermissionPrimerModal,
 * rebound in MainActivity (issue #324): rebuilt WITHOUT the deleted
 * primer's COMMIT & ARM ANYWAY path (register LR2-S1-18 — a session
 * armed with no interception behind it was a timer wearing a lock's
 * name). The primer presents the current step, explains it in the
 * board's own rationale words ([WatchStore.boardRationale], issue
 * #280), and fires that step's FIX trip. Arming stays where the law
 * put it: the slide reads the warrant itself and refuses in the open.
 * ============================================================================
 */

/** The flow's position against one Watch snapshot, derived fresh at
 *  every read by [PermissionFlow.progressAt] — never stored.
 *
 *  [grantedCount] is null while any of the six is UNMEASURED: a
 *  count computed off a partial set is a fabrication (the Guard
 *  line's law, [WatchStore.countedWarranted]), so callers print
 *  their not-checked form, never a number.
 *
 *  [currentStep] is the first ACTIONABLE lack in board order — a
 *  grant measured MISSING (or MISSING_STALE, an old denial, which
 *  PARK's FIX key already treats as missing). A grant that is merely
 *  STALE or UNPROVED is not a step: there is no FIX trip that cures
 *  an unmeasured or unprovable read, so the flow waits for the sweep
 *  instead of inventing an ask.
 *
 *  [complete] is the strong claim: all six print GRANTED, fresh. */
data class PermissionFlowProgress(
    val totalSteps: Int,
    val grantedCount: Int?,
    val currentStep: WatchItemId?,
    val complete: Boolean
)

/** The persisted step state: four facts, nothing measured.
 *  [presentedSteps] records presentation, not success — a step is
 *  presented when its FIX trip fires; whether the grant landed is
 *  the Watch Store's fact to re-measure, never this record's to
 *  claim. [completedWallMs] is forward-only in the same sense as
 *  LAST PROVED: the first instant the complete set stood is a
 *  historical fact that stays true; a later revocation reopens the
 *  flow (the derivation sees the lack) without rewriting history. */
data class PermissionFlowRecord(
    val startedWallMs: Long? = null,
    val presentedSteps: Set<WatchItemId> = emptySet(),
    val lastStep: WatchItemId? = null,
    val completedWallMs: Long? = null
) {
    /** A step's FIX trip fired: the flow has started (if it had
     *  not), the step joins the presented set, and it becomes the
     *  step the user stopped at. Pure — the store persists the
     *  result. */
    fun withStepPresented(item: WatchItemId, wallMs: Long): PermissionFlowRecord =
        copy(
            startedWallMs = startedWallMs ?: wallMs,
            presentedSteps = presentedSteps + item,
            lastStep = item
        )

    /** The complete set stood at [wallMs]: recorded once, never
     *  regressed — a second completion keeps the first instant. */
    fun withCompletion(wallMs: Long): PermissionFlowRecord =
        if (completedWallMs != null) this else copy(completedWallMs = wallMs)

    /** The primer stood: the flow's start is the first presentation
     *  of the primer itself, so a resume after process death can
     *  tell an in-progress flow from a never-begun one. */
    fun withFlowStarted(wallMs: Long): PermissionFlowRecord =
        if (startedWallMs != null) this else copy(startedWallMs = wallMs)

    /** True while a begun flow has neither completed nor been
     *  overtaken by a measured completion: the resume case. */
    fun inProgress(progress: PermissionFlowProgress): Boolean =
        startedWallMs != null && completedWallMs == null && !progress.complete

    companion object {
        val EMPTY = PermissionFlowRecord()
    }
}

/** The pure sequencing core: every derivation the primer and the
 *  resume path read, over the Watch Store's own prints. */
object PermissionFlow {

    /** The flow's position against [snapshot] at [nowMono]. */
    fun progressAt(snapshot: WatchSnapshot, nowMono: Long): PermissionFlowProgress {
        val prints = WatchItemId.PERMISSIONS.map {
            it to WatchStore.permPrintAt(snapshot, it, nowMono)
        }
        val step = prints.firstOrNull {
            it.second == PermPrint.MISSING || it.second == PermPrint.MISSING_STALE
        }?.first
        return PermissionFlowProgress(
            totalSteps = WatchItemId.PERMISSIONS.size,
            grantedCount = WatchStore.countedWarranted(snapshot, nowMono),
            currentStep = step,
            complete = prints.all { it.second == PermPrint.GRANTED }
        )
    }

    /** Where an interrupted flow resumes: the step the user actually
     *  stopped at, while that step still stands missing — the
     *  funnel's promise is the SAME step, not the board's first lack.
     *  If that step has since been granted (or was never recorded),
     *  the resume falls to the flow's current derived step. Null
     *  when the set stands complete. */
    fun resumeStep(
        record: PermissionFlowRecord,
        snapshot: WatchSnapshot,
        nowMono: Long
    ): WatchItemId? {
        val progress = progressAt(snapshot, nowMono)
        if (progress.complete) return null
        val last = record.lastStep
        if (last != null) {
            val print = WatchStore.permPrintAt(snapshot, last, nowMono)
            if (print == PermPrint.MISSING || print == PermPrint.MISSING_STALE) {
                return last
            }
        }
        return progress.currentStep
    }

    /** A step's 1-based position in board order; 0 for an item the
     *  flow does not sequence (an interceptor, or none). */
    fun stepIndex(item: WatchItemId?): Int {
        if (item == null) return 0
        val idx = WatchItemId.PERMISSIONS.indexOf(item)
        return if (idx >= 0) idx + 1 else 0
    }

    /** The primer's step line, e.g. "STEP 2 OF 6 — OVERLAY".
     *  Null when there is no step to present. */
    fun primerStepLine(step: WatchItemId?, progress: PermissionFlowProgress): String? {
        if (step == null) return null
        return "STEP ${stepIndex(step)} OF ${progress.totalSteps} — ${step.boardName}"
    }

    /** The primer's FIX key for a step, e.g. "FIX OVERLAY" — the
     *  same verb PARK's board uses. Null when there is no step. */
    fun primerFixLabel(step: WatchItemId?): String? =
        step?.let { "FIX ${it.boardName}" }

    /** The primer's granted count, e.g. "3 OF 6 GRANTED" — null
     *  while the count does not exist (an unmeasured grant), so the
     *  surface prints nothing instead of a fabricated number. */
    fun primerCountLine(progress: PermissionFlowProgress): String? =
        progress.grantedCount?.let { "$it OF ${progress.totalSteps} GRANTED" }

    /** The step's explainer, in the board's own words
     *  ([WatchStore.boardRationale], issue #280): what the grant
     *  does, and what it never does, BEFORE the Settings trip
     *  fires. */
    fun primerExplainer(step: WatchItemId?): String? =
        step?.let { WatchStore.boardRationale(it) }
}

/**
 * The step state's durable home. Writers (and only these): the
 * primer's host in MainActivity — presentation (the primer stood,
 * a step's FIX trip fired) and the completion landing (a snapshot
 * whose progress reads complete). Every write lands on disk with
 * commit() before the call returns: a step state that dies with the
 * process it was meant to survive is not a resume state.
 */
object PermissionFlowStore {

    private const val PREFS_NAME = "monolith_permission_flow"
    private const val KEY_STARTED_WALL = "flow_started_wall_ms"
    private const val KEY_PRESENTED_STEPS = "presented_steps"
    private const val KEY_LAST_STEP = "last_step"
    private const val KEY_COMPLETED_WALL = "flow_completed_wall_ms"

    @Volatile private var boundContext: Context? = null

    private val _records = MutableStateFlow(PermissionFlowRecord.EMPTY)
    val records: StateFlow<PermissionFlowRecord> = _records.asStateFlow()

    /** The current record (the same value [records] publishes). */
    fun record(): PermissionFlowRecord = _records.value

    /**
     * Bind the store to this process (idempotent): restores the
     * persisted record, so a fresh process opens with the flow
     * exactly where the last one stopped. Called from MainActivity's
     * onCreate beside [WatchStore.bind].
     */
    fun bind(context: Context) {
        val app = context.applicationContext
        if (boundContext === app) return
        boundContext = app
        val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val started = prefs.getLong(KEY_STARTED_WALL, 0L)
        val completed = prefs.getLong(KEY_COMPLETED_WALL, 0L)
        val presented = prefs.getStringSet(KEY_PRESENTED_STEPS, emptySet())
            ?.mapNotNull { name -> runCatching { WatchItemId.valueOf(name) }.getOrNull() }
            ?.toSet() ?: emptySet()
        val last = prefs.getString(KEY_LAST_STEP, null)
            ?.let { name -> runCatching { WatchItemId.valueOf(name) }.getOrNull() }
        _records.value = PermissionFlowRecord(
            startedWallMs = if (started > 0L) started else null,
            presentedSteps = presented,
            lastStep = last,
            completedWallMs = if (completed > 0L) completed else null
        )
    }

    /** The primer stood: stamp the flow's start (once). */
    fun notePrimerShown(wallMs: Long = System.currentTimeMillis()) {
        mutate { it.withFlowStarted(wallMs) }
    }

    /** A step's FIX trip is firing: record the presentation BEFORE
     *  the Settings pane opens, so a process death inside Settings
     *  still resumes at this step. */
    fun recordStepPresented(item: WatchItemId, wallMs: Long = System.currentTimeMillis()) {
        mutate { it.withStepPresented(item, wallMs) }
    }

    /** A fresh progress read: when it first reads complete, the
     *  completion instant lands (forward-only, via the record). */
    fun noteProgress(progress: PermissionFlowProgress, wallMs: Long = System.currentTimeMillis()) {
        if (!progress.complete) return
        mutate { it.withCompletion(wallMs) }
    }

    private fun mutate(transform: (PermissionFlowRecord) -> PermissionFlowRecord) {
        val next = transform(_records.value)
        if (next == _records.value) return
        _records.value = next
        persist(next)
    }

    private fun persist(record: PermissionFlowRecord) {
        val app = boundContext ?: return
        // commit(), not apply() (the Watch Store's LAST PROVED
        // standard, issue #48): the fact must be on disk when this
        // returns — the whole point of the record is surviving the
        // process that wrote it. Writes are rare (one per primer
        // presentation / FIX trip / completion), so the synchronous
        // write is cheap.
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_STARTED_WALL, record.startedWallMs ?: 0L)
            .putStringSet(KEY_PRESENTED_STEPS, record.presentedSteps.map { it.name }.toSet())
            .putString(KEY_LAST_STEP, record.lastStep?.name)
            .putLong(KEY_COMPLETED_WALL, record.completedWallMs ?: 0L)
            .commit()
    }

    /** Test seam (the Watch Store's pattern): in-memory state and
     *  the binding reset; the persisted facts are NOT cleared —
     *  a following [bind] restores them, which is exactly what the
     *  restart test exercises. */
    fun resetForTest() {
        _records.value = PermissionFlowRecord.EMPTY
        boundContext = null
    }
}
