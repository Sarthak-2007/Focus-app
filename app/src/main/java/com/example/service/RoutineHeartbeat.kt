package com.example.service

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.RoutineDerive
import com.example.data.SmartLoadout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * The single reconcile funnel (routines rebuild §2). Every trigger —
 * chain alarm, boot, package replaced, clock change, app resume,
 * routine CRUD, session edges, the shield loop's cadence, and
 * throttled opportunistic wakes from the accessibility service and
 * notification listener — enters through here and runs the SAME pass:
 *
 *   read schedule + sessions → derive() → apply diffs → rearm chain.
 *
 * derive() is the only brain; this object is the only pair of hands.
 * Coalescing: one pass at a time; a trigger arriving mid-pass sets a
 * flag and the pass loops once more, so nothing is ever lost and
 * nothing ever runs twice for the same state.
 *
 * Purity (Otis N1–N6): this file never reads system-guard status,
 * never advances session progress, never takes the generic disarm
 * path, and touches no UI or notification surface. A tick cannot
 * disturb a live manual session beyond finalising what derive()
 * declares due.
 *
 * Failure recovery (issue #47 F3): the chain alarm is ONE-SHOT —
 * consumed when it fires — and reconcileOnce rearms it only at
 * its step 5. A pass that throws mid-way therefore used to leave
 * no chain armed, no liveness stamp, and a swallowed exception:
 * the heartbeat died silently. Now a failed pass is logged, the
 * pendingAgain flag still earns its loop, and the chain is
 * re-armed at [recoveryRearmMillis] — the next real boundary, or
 * a recovery tick ~60 s out, whichever comes first — so the
 * funnel always has a next wake even when the database hiccups.
 */
object RoutineHeartbeat {

    enum class Source {
        CHAIN_ALARM, BOOT, PACKAGE_REPLACED, TIME_CHANGED,
        RESUME, CRUD, SESSION_EDGE, SHIELD_LOOP, OPPORTUNISTIC
    }

    private const val TAG = "RoutineHeartbeat"
    private const val OPPORTUNISTIC_THROTTLE_MILLIS = 30_000L
    private const val DERIVE_LOOKBACK_MILLIS = 48L * 60L * 60L * 1000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gate = Mutex()
    private val pendingAgain = AtomicBoolean(false)
    private val lastOpportunisticAt = AtomicLong(0L)

    @Volatile
    private var lastArmedBoundaryMillis: Long? = null

    /** The suspending entry — callers with a coroutine use this. */
    suspend fun tick(context: Context, source: Source, force: Boolean = false) {
        val appContext = context.applicationContext
        if (!gate.tryLock()) {
            pendingAgain.set(true)
            return
        }
        try {
            do {
                pendingAgain.set(false)
                try {
                    reconcileOnce(appContext)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Issue #47 F3: one exception must not kill the
                    // one-shot chain. Log it (never swallow), keep
                    // the pendingAgain loop honest, and re-arm a
                    // recovery wake BEFORE looping/unlocking.
                    Log.e(TAG, "reconcile pass failed (source=$source); rearming recovery chain", e)
                    recoverChainAfterFailure(appContext)
                }
            } while (pendingAgain.get())
        } finally {
            gate.unlock()
        }
    }

    /** F3 recovery: rearm the chain after a failed pass. The next
     *  real boundary wins if it is sooner; otherwise a recovery
     *  tick ~60 s out retries the funnel. Best-effort by design —
     *  if even this read fails, the shield loop's 30 s cadence and
     *  every opportunistic trigger remain as the outer net, and
     *  the failure above is already in the log. */
    private suspend fun recoverChainAfterFailure(appContext: Context) {
        try {
            val dao = MonolithDatabase.getInstance(appContext).monolithDao()
            val now = System.currentTimeMillis()
            val boundary = RoutineDerive.nextBoundaryMillis(dao.getAllRoutinesSnapshot(), now)
            RoutineScheduler.rearmChain(appContext, recoveryRearmMillis(boundary, now))
            // The failed pass never reached its step 5/6: force the
            // next successful pass to re-derive the chain state
            // instead of trusting the pre-failure boundary memory.
            lastArmedBoundaryMillis = null
        } catch (_: Exception) {
            // Logged at the call site; nothing further can be armed
            // from here without the database.
        }
    }

    /** Fire-and-forget entry for receivers and service callbacks. */
    fun tickAsync(context: Context, source: Source, force: Boolean = false) {
        val appContext = context.applicationContext
        scope.launch { tick(appContext, source, force) }
    }

    /** Throttled entry for high-frequency service callbacks: at most
     *  one opportunistic pass per 30 s of monotonic time. */
    fun tickOpportunistic(context: Context) {
        val now = SystemClock.elapsedRealtime()
        val last = lastOpportunisticAt.get()
        if (now - last < OPPORTUNISTIC_THROTTLE_MILLIS) return
        if (!lastOpportunisticAt.compareAndSet(last, now)) return
        tickAsync(context, Source.OPPORTUNISTIC)
    }

    private suspend fun reconcileOnce(appContext: Context) {
        val database = MonolithDatabase.getInstance(appContext)
        val dao = database.monolithDao()
        val repository = MonolithRepository(appContext, dao)

        val now = System.currentTimeMillis()
        val routines = dao.getAllRoutinesSnapshot()
        val routinesById = routines.associateBy { it.id }
        val active = dao.getActiveSessionSnapshot()
        val since = now - DERIVE_LOOKBACK_MILLIS
        // Issue #47 F5: cheap 'nothing due' probe — when no session
        // is active and the COUNT over the exact union of the two
        // derive() reads is zero, both reads PROVABLY return empty,
        // so derive over an empty list (semantics identical) and
        // skip materialising the 48 h session lists entirely.
        val sessions = if (active == null && dao.countDeriveSessionInputs(since) == 0) {
            emptyList()
        } else {
            (dao.getRoutineSessionsForDerive(since) + dao.getSessionsStartedSince(since))
                .distinctBy { it.id }
        }
        val sessionsById = sessions.associateBy { it.id }
        val settings = dao.getGuardSettingsSnapshot()

        val result = RoutineDerive.derive(
            routines = routines,
            nowMillis = now,
            activeSession = active,
            sessions = sessions,
            settings = settings ?: GuardSettingsEntity()
        )

        // 1. Finalise everything derive() declares due — known routines
        //    first, then orphans (routine row deleted; sealed by the
        //    stored window snapshot alone, R5).
        val finaliseIds = result.derivations.flatMap { it.finaliseDue } + result.orphanFinaliseDue
        var activeFinalised = false
        for (id in finaliseIds.distinct()) {
            val session = sessionsById[id] ?: continue
            repository.finaliseRoutineSession(session, now)
            if (active != null && session.id == active.id) activeFinalised = true
        }

        // 2. SLEEP stand-downs: an active SLEEP session past its stored
        //    window end is stood down unstamped (J2) — a plain
        //    deactivation, never a judgement.
        if (active != null && active.routineId != null && active.windowEndMillis in 1..now) {
            if (routinesById[active.routineId]?.loadoutName == SmartLoadout.SLEEP.name) {
                repository.deactivateRoutineSession(active)
                activeFinalised = true
            }
        }

        // 3. Materialise at most one desired session. derive() only
        //    emits one for an unclaimed containing window, so this is
        //    safe even in the same pass that finalised the previous
        //    window's session (back-to-back windows: the old session
        //    seals and the new one seals in — one pass, no dead gap).
        //    The repository's start path deactivates any predecessor.
        val desired = result.derivations.firstNotNullOfOrNull { it.desiredSession }
        var materialised = false
        if (desired != null) {
            val transition = SessionSealTransitions.get(appContext)
            // Slice E (F-S1(i)): an annulled proving tombstoned its
            // window — the machine refused that seal late, and the
            // window stays refused for this process's life. The
            // tombstone is checked BEFORE materialising, never after.
            if (transition.isTombstoned(desired.routineId, desired.windowStartMillis)) {
                materialised = false
            } else {
                val session = repository.materialiseRoutineSession(desired)
                materialised = true
                // 3b. THE SEAL'S FOURTH ACT (F-S1(i) full
                // convergence): the materialised session proves
                // and seals through the same transition the Deck's
                // arm uses — the SLEEP 20:00 seal sounds, bit-
                // identically. Launched off-pass: the proving
                // window (≤1500ms) must not hold the reconcile
                // mutex, and its verdict needs no witness here —
                // the tombstone and the row carry it.
                val sealSessionId = session.id
                scope.launch {
                    transition.proveAndSeal(sealSessionId, null)
                }
            }
        }

        // 4. Shield bookkeeping: if a finalise ended the active session
        //    and nothing replaced it, the shield stands down. A live
        //    session (manual or routine) is never stopped here.
        if (activeFinalised && !materialised) {
            MonolithShieldService.stopShield(appContext)
        }

        // 5. Rearm the chain when the boundary moved or the chain is
        //    missing entirely.
        val boundary = result.nextBoundaryMillis
        if (boundary != lastArmedBoundaryMillis || !RoutineScheduler.isChainArmed(appContext)) {
            if (boundary == null) {
                RoutineScheduler.cancelChain(appContext)
            } else {
                RoutineScheduler.rearmChain(appContext, boundary)
            }
            lastArmedBoundaryMillis = boundary
        }

        // 6. Liveness stamp — proof on disk that the funnel ran.
        dao.stampLastRoutineTick(now)
    }
}

/** How long after a failed pass the recovery tick fires when no
 *  real boundary is sooner (issue #47 F3). */
internal const val RECOVERY_RETRY_DELAY_MS = 60_000L

/** Floor under a recovery rearm: an overdue boundary must not
 *  re-fire instantly in a tight failure loop. */
internal const val RECOVERY_MIN_DELAY_MS = 5_000L

/** The recovery chain instant after a failed heartbeat pass
 *  (issue #47 F3): the next real schedule boundary when it is the
 *  sooner wake, otherwise a recovery tick [RECOVERY_RETRY_DELAY_MS]
 *  out — and never in the past, so a persistent failure retries on
 *  a human cadence instead of spinning. Pure. */
internal fun recoveryRearmMillis(nextBoundaryMillis: Long?, nowMillis: Long): Long {
    val target = minOf(nextBoundaryMillis ?: Long.MAX_VALUE, nowMillis + RECOVERY_RETRY_DELAY_MS)
    return if (target <= nowMillis) nowMillis + RECOVERY_MIN_DELAY_MS else target
}
