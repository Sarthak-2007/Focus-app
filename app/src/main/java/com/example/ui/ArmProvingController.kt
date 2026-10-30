package com.example.ui
import com.example.R

import android.util.Log
import com.example.MLog
import android.content.Context
import android.os.SystemClock
import android.view.Choreographer
import com.example.data.MonolithRepository
import com.example.service.MonolithAccessibilityService
import com.example.service.MonolithShieldService
import com.example.service.RoutineHeartbeat
import com.example.ui.components.RefusalSentence
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SLICE D STEP 4 — THE CASCADE (Opal's §F2, reconciled at N=3).
 *
 * The slide's latch frame is the point of no return for the
 * GESTURE; it is not yet a seal. What the latch creates — a
 * session row, a shield start, an accessibility gate opening on
 * that row — is proven here, link by link, against witnesses in
 * code, and only when every link has answered does t=0 exist.
 * Before t=0 no surface may claim SEALED; the session readout
 * starts at t=0, the row is re-anchored to t=0, and the seal
 * sound fires at t=0 — its only arm-path call.
 *
 * PROVING lives here and nowhere else: a controller state in the
 * ViewModel layer. Not a DB state, not a session column, not a
 * migration. The row is born active at insert (the shield's
 * monitor loop stands down on an inactive row, so a born-
 * inactive design would kill L2's witness at birth), and the
 * ≤500ms of enforcement before t=0 is lawful because no surface
 * claims the seal before t=0 — and §3.2's re-anchor means those
 * milliseconds are never counted as session time.
 */

/** The three links. Fixed, witnessable, never per-app: apps are
 *  intercepted on the attempt, never shut in advance, so there
 *  is nothing per-app to confirm (Opal §1 — the per-app link and
 *  the rack's N=loadout died together). */
enum class ArmLink(val displayName: String, val plainName: String) {
    /** The session row exists and is the active row: the insert
     *  returned an id AND a snapshot read returns that id with
     *  isActive == true. */
    SESSION_ROW("SESSION", "THE SESSION"),

    /** The shield is a live instance, its foreground start
     *  succeeded, AND its monitor loop completed a first
     *  iteration that read the latched row as active. */
    SHIELD_PROTECTING("SHIELD", "THE SHIELD"),

    /** The accessibility service is bound AND has observed this
     *  session as active — its own session gate, which early-
     *  returns when no active session exists, is open for this
     *  row. */
    A11Y_SESSION_ARMED("ACCESS", "ACCESSIBILITY")
}

sealed interface LinkMark {
    data object Pending : LinkMark
    data class Confirmed(val mono: Long) : LinkMark
}

sealed interface ArmPhase {
    data object Idle : ArmPhase

    /** A latch has dispatched the batch; links are answering.
     *  JAMMED is a substate of this attempt, not a new attempt:
     *  same attemptId, and marks confirmed in round 1 are never
     *  revoked by the retry (only by their witness dying). */
    data class Proving(
        val attemptId: Long,
        val latchMono: Long,
        val sessionId: Int?,
        val links: Map<ArmLink, LinkMark>,
        val retryCount: Int = 0
    ) : ArmPhase

    data class Jammed(
        val attemptId: Long,
        val latchMono: Long,
        val sessionId: Int?,
        val links: Map<ArmLink, LinkMark>,
        val jamSet: Set<ArmLink>,
        val retryCount: Int
    ) : ArmPhase

    /** t=0 has fired. The session's life from here is the row's
     *  business; the phase relaxes to Idle when the row goes. */
    data class SealedRunning(
        val sessionId: Int,
        val t0Mono: Long
    ) : ArmPhase
}

/** provenCount is DERIVED, never stored separately — the
 *  controller, and any surface that ever renders the count, can
 *  then never disagree about it, because there is one field. */
fun ArmPhase.provenCount(): Int = when (this) {
    is ArmPhase.Proving -> links.values.count { it is LinkMark.Confirmed }
    is ArmPhase.Jammed -> links.values.count { it is LinkMark.Confirmed }
    is ArmPhase.SealedRunning -> ArmLink.entries.size
    ArmPhase.Idle -> 0
}

private fun ArmPhase.attemptIdOrNull(): Long? = when (this) {
    is ArmPhase.Proving -> attemptId
    is ArmPhase.Jammed -> attemptId
    else -> null
}

private fun ArmPhase.latchMonoOrNull(): Long? = when (this) {
    is ArmPhase.Proving -> latchMono
    is ArmPhase.Jammed -> latchMono
    else -> null
}

private fun ArmPhase.sessionIdOrNull(): Int? = when (this) {
    is ArmPhase.Proving -> sessionId
    is ArmPhase.Jammed -> sessionId
    is ArmPhase.SealedRunning -> sessionId
    ArmPhase.Idle -> null
}

private fun ArmPhase.linksOrNull(): Map<ArmLink, LinkMark>? = when (this) {
    is ArmPhase.Proving -> links
    is ArmPhase.Jammed -> links
    else -> null
}

private fun ArmPhase.pendingLinks(): Set<ArmLink> =
    linksOrNull()?.filterValues { it is LinkMark.Pending }?.keys ?: emptySet()

private fun ArmPhase.allConfirmed(): Boolean =
    linksOrNull()?.values?.all { it is LinkMark.Confirmed } == true

/** Everything the cascade needs from the data layer and the
 *  platform, behind one seam so the controller's clocks and
 *  ordering are unit-testable without Android services. The
 *  production implementation is [RepositoryArmSessionPort]. */
interface ArmSessionPort {
    suspend fun startSession(
        intention: String,
        profileName: String,
        durationMinutes: Int,
        studyBlocksCount: Int
    ): Int

    /** The active row's id on a fresh snapshot read, or null. */
    suspend fun activeSnapshotId(): Int?

    /** The t=0 a sealed row actually carries — its re-anchored
     *  startElapsedRealtimeMillis — or null while the row is
     *  unsealed or gone. Read for the other-door publication:
     *  when the seal landed through the routine door, the row's
     *  own anchors are the truth, never this attempt's computed
     *  candidate (issue #29 finding 4). */
    suspend fun sealedRowT0Mono(sessionId: Int): Long?

    /** Re-anchor + re-sign at t=0. Boolean since Slice E
     *  (Klinke §4.3.2): false = the row is gone or no longer the
     *  active one — the transition must not sound the Throw. */
    suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean

    suspend fun annulProvingSession(sessionId: Int)

    fun startShield()

    fun stopShield()

    /** The routines SESSION_EDGE reconcile, fired once L1 lands. */
    fun onSessionStartedEdge()
}

class RepositoryArmSessionPort(
    private val repository: MonolithRepository,
    private val appContext: Context
) : ArmSessionPort {
    override suspend fun startSession(
        intention: String,
        profileName: String,
        durationMinutes: Int,
        studyBlocksCount: Int
    ): Int = repository.startSession(
        intention = intention,
        profileName = profileName,
        durationMinutes = durationMinutes,
        studyBlocksCount = studyBlocksCount,
        // Issue #264 B-264-18: the hand arm's break choice is the
        // preset the user last picked — the same stored fact the
        // Deck's preset badge ("5m Break" / "10m Break") prints.
        // Reading it here, at the one hand-arm door, stamps the
        // choice on the row so the mechanism delivers the length
        // the badge promised.
        breakMinutes = com.example.data.PreArmStore(appContext).studyPreset().breakMinutes
    ).id

    override suspend fun activeSnapshotId(): Int? =
        repository.activeSessionSnapshot()?.takeIf { it.isActive }?.id

    override suspend fun sealedRowT0Mono(sessionId: Int): Long? =
        repository.sessionById(sessionId)?.takeIf { it.isActive }
            ?.startElapsedRealtimeMillis

    override suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean =
        repository.sealSession(sessionId, t0Mono)

    override suspend fun annulProvingSession(sessionId: Int) =
        repository.annulProvingSession(sessionId)

    override fun startShield() = MonolithShieldService.startShield(appContext)

    override fun stopShield() = MonolithShieldService.stopShield(appContext)

    override fun onSessionStartedEdge() {
        RoutineHeartbeat.tickAsync(appContext, RoutineHeartbeat.Source.SESSION_EDGE)
    }
}

/** The two post-latch witnesses, read synchronously. Each
 *  returns the witness's own monotonic CONFIRMED stamp for the
 *  latched session, or null while the link is unproven (or its
 *  witness has died — the sampler re-pends a confirmed link
 *  whose witness clears before t=0). */
interface ArmWitnesses {
    fun shieldProtectingMono(sessionId: Int): Long?
    fun a11yArmedMono(sessionId: Int): Long?
}

object LiveArmWitnesses : ArmWitnesses {
    override fun shieldProtectingMono(sessionId: Int): Long? =
        if (MonolithShieldService.instance != null &&
            MonolithShieldService.protecting &&
            MonolithShieldService.protectingSessionId == sessionId
        ) {
            MonolithShieldService.protectingMono.takeIf { it > 0L }
        } else {
            null
        }

    override fun a11yArmedMono(sessionId: Int): Long? =
        if (MonolithAccessibilityService.instance != null &&
            MonolithAccessibilityService.armedSessionId == sessionId
        ) {
            MonolithAccessibilityService.armedMono.takeIf { it > 0L }
        } else {
            null
        }
}

class ArmProvingController(
    private val scope: CoroutineScope,
    private val port: ArmSessionPort,
    private val witnesses: ArmWitnesses,
    /** Slice E (F-S1(i)): the seal itself — claim, row, marker,
     *  publish, Throw — lives in the transition now. This
     *  controller owns the proving; the transition owns the seal
     *  act, here and at the routine door alike. */
    private val transition: com.example.service.SessionSealTransition,
    private val playJamHaptics: () -> Unit,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() },
    private val framePoster: ((() -> Unit) -> Unit) = { callback ->
        Choreographer.getInstance().postFrameCallback { callback() }
    },
    private val dwellMs: Long = DWELL_MS,
    private val deadlineMs: Long = DEADLINE_MS,
    private val annulMs: Long = ANNUL_MS,
    private val sampleMs: Long = SAMPLE_MS,
    private val sealWatchdogMs: Long = SEAL_WATCHDOG_MS
) {
    private val _phase = MutableStateFlow<ArmPhase>(ArmPhase.Idle)
    val phase: StateFlow<ArmPhase> = _phase.asStateFlow()

    /** The annulment's sentence, held for the slot after the
     *  phase has returned to Idle — the one refusal this machine
     *  prints in the past tense. Cleared at the next latch. */
    private val _armSentence = MutableStateFlow<RefusalSentence?>(null)
    val armSentence: StateFlow<RefusalSentence?> = _armSentence.asStateFlow()

    /** The Deck's hand-back key: incremented on EVERY machine
     *  hand-back of a latched attempt — under the settled
     *  dispatch law (Joss's §3(c)/§6.5; Ren's Charge-2 ruling)
     *  that is the annulment at the final deadline, the one
     *  exit a dispatched attempt can take without sealing.
     *  The Deck consumes it purely as a reset key: the slide
     *  view returns to REST and the press latch retires. A
     *  latched attempt is never left seated without one of
     *  these increments. */
    private val _annulCount = MutableStateFlow(0)
    val annulCount: StateFlow<Int> = _annulCount.asStateFlow()

    private var attemptSeq = 0L
    private var samplerJob: Job? = null
    private var sentenceClearJob: Job? = null
    private var sealWatchdogJob: Job? = null

    /** Confirmations sampled before L1 resolved: timestamped by
     *  their witnesses, uncounted until L1 lands (a shield
     *  protecting no row has proved nothing about a seal). */
    private val heldMarks = mutableMapOf<ArmLink, Long>()

    /** Attempts annulled before their insert returned: a late
     *  row from a dead attempt is annulled on arrival, never
     *  adopted. */
    private val deadAttempts = mutableSetOf<Long>()

    /**
     * The latch entry. Called synchronously inside the slide's
     * latch frame (the view's onSlideCommitted chain runs on the
     * main thread in that frame), so the L read here IS the
     * latch frame's mono for any practical purpose — the
     * cascade's only clock, read once, stored in the attempt.
     * Every stamp in this slice is elapsedRealtime(); wall clock
     * never enters proving.
     */
    fun onLatch(
        intention: String,
        profileName: String,
        durationMinutes: Int,
        studyBlocksCount: Int
    ) {
        val current = _phase.value
        if (current is ArmPhase.Proving || current is ArmPhase.Jammed) return
        if (current is ArmPhase.SealedRunning) return
        // DISPATCH LAW, settled (Joss's §3(c)/§6.5 governs;
        // Ren's Charge-2 ruling, Oct 29): this entry is called
        // only on a latch frame that read LIVE — the slide's
        // latch frame IS the verdict, and it is the only one.
        // An earlier §F2 draft re-read the warrant here as a
        // dispatch precondition; that second read is dropped.
        // Two derivations of one fact will disagree, and a
        // death between the frame's read and this dispatch
        // belongs to the cascade: PROVING judges it, the jam
        // names it, the annulment hands the view back. So
        // entry always dispatches — every seated cap has an
        // attempt, every attempt resolves by seal or by
        // annulment, and no latched view can be stranded
        // without a machine owning its fate (the strand harm
        // Charge 2 closed, closed by construction here).
        val latchMono = clock()
        sentenceClearJob?.cancel()
        _armSentence.value = null
        val attemptId = ++attemptSeq
        heldMarks.clear()
        _phase.value = ArmPhase.Proving(
            attemptId = attemptId,
            latchMono = latchMono,
            sessionId = null,
            links = ArmLink.entries.associateWith { LinkMark.Pending },
            retryCount = 0
        )
        // THE BATCH (≤16ms spread, same vsync window): (i) the
        // insert launches — its return resolves L1's first half;
        // (ii) the shield starts; (iii) L3 arms itself off the
        // Room Flow when L1 lands. Nothing is awaited serially.
        // Slice E: the seal lane primes in the same batch — the
        // latch knock has already fired in the view, so its seq
        // stands below the prime's barrier (Riegel §1.2).
        transition.primeForSeal()
        scope.launch {
            try {
                val id = port.startSession(
                    intention = intention,
                    profileName = profileName,
                    durationMinutes = durationMinutes,
                    studyBlocksCount = studyBlocksCount
                )
                onSessionRowInserted(attemptId, id)
            } catch (e: Exception) {
                // Issue #75 F1 wiring: the failed insert now speaks.
                MLog.e("startSession insert failed; L1 stays pending", e)
                // The insert never returned: L1 stays pending and
                // the clocks judge it. Annulment with no row is
                // stopShield + attempt discard only.
            }
        }
        port.startShield()
        samplerJob?.cancel()
        samplerJob = scope.launch { runAttempt(attemptId) }
    }

    private suspend fun onSessionRowInserted(attemptId: Long, sessionId: Int) {
        if (attemptId in deadAttempts) {
            // Truth (issue #76): this annul used to be discarded
            // silently — a failure leaked the proving row forever.
            runCatching { port.annulProvingSession(sessionId) }
                .onFailure { Log.e("ArmProving", "dead-attempt annul failed for session $sessionId", it) }
            return
        }
        val current = _phase.value
        if (current.attemptIdOrNull() != attemptId) return
        setSessionId(attemptId, sessionId)
        // L1's second half: a snapshot read returns the latched
        // id as the active row. The stamp is read on the
        // snapshot's return, per the link's definition.
        val snapshotId = runCatching { port.activeSnapshotId() }
            .onFailure { MLog.w("activeSnapshotId read failed after session insert", it) }
            .getOrNull()
        if (snapshotId == sessionId) {
            // The snapshot read suspended: the attempt may have
            // been annulled or superseded while it was in flight.
            // Re-check before anything is published for it — the
            // SESSION_EDGE reconcile fires ONLY inside this
            // proved block, for a live attempt (issue #29
            // finding 3: it previously fired unconditionally,
            // even when the snapshot read failed and even for an
            // attempt that died during the read).
            if (attemptId in deadAttempts) {
                runCatching { port.annulProvingSession(sessionId) }
                    .onFailure { MLog.e("annul of superseded attempt's session $sessionId failed", it) }
                return
            }
            if (_phase.value.attemptIdOrNull() != attemptId) return
            applyMark(attemptId, ArmLink.SESSION_ROW, clock())
            flushHeldMarks(attemptId)
            port.onSessionStartedEdge()
        }
    }

    /**
     * Issue #126 F1 — RECOVERY GATE. A born-active row found at
     * relaunch is re-proven by the seal transition's recover()
     * (SessionSealTransition), but that machine is not this
     * phase: without a gate the Deck reads ArmPhase.Idle for the
     * whole recovery and renders the unproven row as fully
     * RUNNING — a surface claiming the seal before its verdict,
     * against this file's own header law. The ViewModel calls
     * this before recover() for exactly the rows recover() will
     * actually re-prove (marker absent, row young — recover's
     * own forks, mirrored so a lawful marker/old-row adoption
     * never flashes PROVING), and [resolveRecovery] with the
     * verdict when it lands. While the gate stands the Deck's
     * existing gate (provingThisSession) renders the row's idle
     * form and navigation locks, exactly as for a live attempt.
     */
    fun beginRecovery(sessionId: Int) {
        if (_phase.value != ArmPhase.Idle) return
        _phase.value = ArmPhase.Proving(
            attemptId = ++attemptSeq,
            latchMono = clock(),
            sessionId = sessionId,
            links = ArmLink.entries.associateWith { LinkMark.Pending },
            retryCount = 0
        )
    }

    /** The recovery verdict lands. SEALED relaxes to Idle (the
     *  row was re-proven and re-anchored by the transition, or
     *  adopted on its marker — the Deck then runs it settled,
     *  no shot replayed). ANNULLED means the transition deleted
     *  the row itself: this only hands the view back and speaks
     *  the past-tense sentence, mirroring [annul] without
     *  touching the port. Any other/null verdict (recover threw,
     *  or its proving slot was busy) is inconclusive: relax
     *  silently rather than print a verdict nobody reached. */
    fun resolveRecovery(sessionId: Int, verdict: com.example.service.SealVerdict?) {
        val current = _phase.value
        if (current !is ArmPhase.Proving || current.sessionId != sessionId) return
        _phase.value = ArmPhase.Idle
        if (verdict == com.example.service.SealVerdict.ANNULLED) {
            _annulCount.value += 1
            val sentence = RefusalSentence(
                textRes = R.string.sentence_no_proof_blocking_off,
                args = listOf(ArmLink.SESSION_ROW.displayName),
                action = null
            )
            _armSentence.value = sentence
            sentenceClearJob?.cancel()
            sentenceClearJob = scope.launch {
                delay(SENTENCE_HOLD_MS)
                if (_armSentence.value === sentence) _armSentence.value = null
            }
        }
    }

    /** The sealed session's row has gone (completion, valve,
     *  grace abort — the lawful post-t0 doors): the phase
     *  relaxes. The session's life was the row's business from
     *  t=0 on; this only tidies the controller. */
    fun onActiveSessionGone() {
        if (_phase.value is ArmPhase.SealedRunning) {
            _phase.value = ArmPhase.Idle
        }
    }

    private fun setSessionId(attemptId: Long, sessionId: Int) {
        val current = _phase.value
        _phase.value = when (current) {
            is ArmPhase.Proving ->
                if (current.attemptId == attemptId) current.copy(sessionId = sessionId) else current
            is ArmPhase.Jammed ->
                if (current.attemptId == attemptId) current.copy(sessionId = sessionId) else current
            else -> current
        }
    }

    private fun applyMark(attemptId: Long, link: ArmLink, mono: Long) {
        val current = _phase.value
        if (current.attemptIdOrNull() != attemptId) return
        val links = current.linksOrNull() ?: return
        if (links[link] is LinkMark.Confirmed) return
        val next = links + (link to LinkMark.Confirmed(mono))
        _phase.value = when (current) {
            is ArmPhase.Proving -> current.copy(links = next)
            is ArmPhase.Jammed -> current.copy(links = next)
            else -> current
        }
    }

    private fun flushHeldMarks(attemptId: Long) {
        if (heldMarks.isEmpty()) return
        val pending = heldMarks.toMap()
        heldMarks.clear()
        for ((link, mono) in pending) applyMark(attemptId, link, mono)
    }

    /** A sampled confirmation: counted at once if L1 has
     *  resolved; held — timestamped, uncounted — until it does. */
    private fun holdOrConfirm(attemptId: Long, link: ArmLink, mono: Long) {
        val links = _phase.value.linksOrNull() ?: return
        if (links[ArmLink.SESSION_ROW] is LinkMark.Confirmed) {
            applyMark(attemptId, link, mono)
        } else {
            val existing = heldMarks[link]
            if (existing == null || mono < existing) heldMarks[link] = mono
        }
    }

    /** A confirmed link whose witness has died before t=0 goes
     *  back to pending: the fact it attested stopped being true
     *  before the seal, and a seal printed on it would be a lie.
     *  (The RETRY never revokes a mark; a dead witness does.) */
    private fun rependIfWitnessDead(attemptId: Long, link: ArmLink) {
        val current = _phase.value
        if (current.attemptIdOrNull() != attemptId) return
        val links = current.linksOrNull() ?: return
        if (links[link] !is LinkMark.Confirmed) return
        val next = links + (link to LinkMark.Pending)
        _phase.value = when (current) {
            is ArmPhase.Proving -> current.copy(links = next)
            is ArmPhase.Jammed -> current.copy(links = next)
            else -> current
        }
    }

    private suspend fun runAttempt(attemptId: Long) {
        while (true) {
            val current = _phase.value
            if (current.attemptIdOrNull() != attemptId) return
            val latchMono = current.latchMonoOrNull() ?: return

            // Sample the two dispatched witnesses against the
            // latched row (impossible before its id exists —
            // both witnesses are keyed by it).
            val sessionId = current.sessionIdOrNull()
            if (sessionId != null) {
                // L1 self-heals: if the insert's own snapshot read
                // raced (a null read in the commit's shadow), the
                // sampler re-reads while SESSION-ROW is pending —
                // the link's definition is a state of the world,
                // not of one read. Held marks flush on its landing.
                if (current.linksOrNull()?.get(ArmLink.SESSION_ROW) is LinkMark.Pending) {
                    val snap = runCatching { port.activeSnapshotId() }
                        .onFailure { MLog.d("sampler activeSnapshotId read failed: ${it.message}") }
                        .getOrNull()
                    if (snap == sessionId) {
                        applyMark(attemptId, ArmLink.SESSION_ROW, clock())
                        flushHeldMarks(attemptId)
                        // The port's contract is "fired once L1
                        // lands" — a heal landing IS a landing
                        // (issue #29 finding 3: the edge belongs
                        // to the proof, whichever read completes
                        // it; this block runs only on the
                        // Pending→Confirmed flip, so it fires
                        // exactly once).
                        port.onSessionStartedEdge()
                    }
                }
                val shieldMono = witnesses.shieldProtectingMono(sessionId)
                if (shieldMono != null) {
                    holdOrConfirm(attemptId, ArmLink.SHIELD_PROTECTING, shieldMono)
                } else {
                    rependIfWitnessDead(attemptId, ArmLink.SHIELD_PROTECTING)
                }
                val a11yMono = witnesses.a11yArmedMono(sessionId)
                if (a11yMono != null) {
                    holdOrConfirm(attemptId, ArmLink.A11Y_SESSION_ARMED, a11yMono)
                } else {
                    rependIfWitnessDead(attemptId, ArmLink.A11Y_SESSION_ARMED)
                }
            }

            val sampled = _phase.value
            if (sampled.attemptIdOrNull() != attemptId) return
            if (sampled.allConfirmed()) {
                declareT0(attemptId)
                return
            }

            val now = clock()
            var jammedThisIteration = false
            if (sampled is ArmPhase.Proving && now >= latchMono + deadlineMs) {
                // DEADLINE (per-link 400ms timeouts, concurrent
                // from L, + 100ms scheduling slack): the pending
                // links are the jam set. No t=0, no seal sound,
                // no readout start. The retry is the machine's,
                // never his — no verb is offered. Jam haptics as
                // filed: the seat-knock voice at 20ms @ 1.0, ×3
                // at 90ms spacing (the VM wires the voice).
                val jamSet = sampled.pendingLinks()
                _phase.value = ArmPhase.Jammed(
                    attemptId = attemptId,
                    latchMono = latchMono,
                    sessionId = sampled.sessionId,
                    links = sampled.links,
                    jamSet = jamSet,
                    retryCount = 1
                )
                jammedThisIteration = true
                playJamHaptics()
                // Retry dispatch, jam set only: the shield is
                // re-dispatched (its start re-proves the witness
                // from a fresh loop). L1 and L3 have no dispatch
                // of their own — L1's insert is still in flight
                // or already landed, L3 arms off L1's Flow — so
                // their retry is the extended window itself.
                if (ArmLink.SHIELD_PROTECTING in jamSet) {
                    port.startShield()
                }
            }

            // The annulment judges the Jammed phase only on a
            // LATER iteration than the one that declared it
            // (issue #29 finding 2): a single clock leap across
            // both deadlines previously annulled in the same
            // iteration that set Jammed, so the jam was never
            // rendered and the retry window collapsed to zero.
            // The Jammed phase must stand for at least one
            // sample cycle — its own sentence, its retry — before
            // the final deadline can be read against it.
            val afterJam = _phase.value
            if (!jammedThisIteration &&
                afterJam is ArmPhase.Jammed &&
                now >= latchMono + annulMs &&
                afterJam.pendingLinks().isNotEmpty()
            ) {
                annul(attemptId)
                return
            }

            delay(sampleMs)
        }
    }

    /** t=0, declared on a frame: t0 = max(last CONFIRMED stamp,
     *  latch + 210ms dwell, the pawl frame). The pawl term is
     *  discharged by the frame in which the seal is struck —
     *  the declaration frame itself, whose mono is read inside
     *  the callback. (The rack that would have carried a
     *  literal pawl ships only through its §F7 device gate,
     *  which has not run; the imprint's Strike is this build's
     *  seated pawl, and the Deck fires it from this t0.) */
    private fun declareT0(attemptId: Long) {
        val current = _phase.value
        val links = current.linksOrNull() ?: return
        val latchMono = current.latchMonoOrNull() ?: return
        val lastConfirm = links.values.filterIsInstance<LinkMark.Confirmed>().maxOf { it.mono }
        val candidate = maxOf(lastConfirm, latchMono + dwellMs)
        postT0Frame(attemptId, candidate)
        // THE WATCHDOG (issue #29 finding 1): declareT0 retires
        // the sampler, and everything after it rides on one
        // posted frame and one launched seal act — a frame that
        // never arrives (detached view), a seal act that hangs,
        // or a launch that dies could previously strand the
        // attempt in Proving forever, clocks running, nothing
        // judging. The watchdog re-opens the case: if the seal
        // landed through the other door, publish the row's own
        // anchors; otherwise hand the attempt back to the
        // sampler, whose clocks (still from L) judge it to jam
        // and annulment like any unproven attempt. No attempt
        // strands.
        sealWatchdogJob?.cancel()
        sealWatchdogJob = scope.launch {
            delay(sealWatchdogMs)
            val current = _phase.value
            if (current.attemptIdOrNull() != attemptId) return@launch
            val sessionId = current.sessionIdOrNull()
            if (sessionId != null && transition.isSealed(sessionId)) {
                val rowT0 = runCatching { port.sealedRowT0Mono(sessionId) }
                .onFailure { MLog.d("sealedRowT0Mono($sessionId) read failed: ${it.message}") }
                .getOrNull()
                _phase.value = ArmPhase.SealedRunning(
                    sessionId = sessionId,
                    t0Mono = rowT0 ?: candidate
                )
            } else if (samplerJob?.isActive != true) {
                samplerJob = scope.launch { runAttempt(attemptId) }
            }
        }
    }

    private fun postT0Frame(attemptId: Long, candidate: Long) {
        framePoster {
            val frameMono = clock()
            val current = _phase.value
            if (current.attemptIdOrNull() != attemptId) return@framePoster
            val sessionIdNow = current.sessionIdOrNull()
            // The frame re-reads the witnesses themselves, not
            // just the marks: a witness that died in the gap
            // between the sampler's all-confirmed read and this
            // frame leaves the marks stale, and the seal never
            // fires on a stale proof. Sampling resumes; the
            // clocks (unchanged, still from L) judge it.
            val witnessesLive = sessionIdNow != null &&
                witnesses.shieldProtectingMono(sessionIdNow) != null &&
                witnesses.a11yArmedMono(sessionIdNow) != null
            if (!current.allConfirmed() || !witnessesLive) {
                if (samplerJob?.isActive != true) {
                    samplerJob = scope.launch { runAttempt(attemptId) }
                }
                return@framePoster
            }
            if (frameMono < candidate) {
                postT0Frame(attemptId, candidate)
                return@framePoster
            }
            val sessionId = current.sessionIdOrNull() ?: return@framePoster
            val t0 = maxOf(candidate, frameMono)
            // THE SEAL ACT (Slice E, Klinke §3.1's claim block,
            // executed by the transition): claim → sealSession
            // (Boolean-gated) → publish SEALED_RUNNING → the
            // Throw, as the act's last statement, inside
            // executeSeal. Publication no longer precedes the
            // row's re-anchor: no surface can render "running"
            // from un-re-anchored anchors. Exactly once is the
            // transition's claim, not this frame's hope.
            scope.launch {
                // A seal act that THROWS is a refused seal, not
                // a stranded attempt (issue #29 finding 1): the
                // exception is caught and judged by the same
                // gate a false return faces. Cancellation is
                // never swallowed.
                val sealedOk = try {
                    transition.executeSeal(
                        sessionId = sessionId,
                        t0Mono = t0,
                        sealRow = { port.sealSession(sessionId, t0) },
                        publish = {
                            _phase.value = ArmPhase.SealedRunning(sessionId = sessionId, t0Mono = t0)
                        }
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Issue #75 F1 wiring: a thrown seal attempt read
                    // as a plain false — it speaks now.
                    MLog.e("executeSeal threw for session $sessionId; treating as not sealed", e)
                    false
                }
                if (!sealedOk) {
                    if (transition.isSealed(sessionId)) {
                        // The seal landed through the other door
                        // (the claim was already spent on a real
                        // seal): the fact stands; publish it —
                        // with the ROW's real seal anchors, not
                        // this attempt's computed candidate
                        // (issue #29 finding 4: publishing the
                        // invented t0 misstated when the session
                        // actually began whenever the other door
                        // sealed at a different instant).
                        val rowT0 = runCatching { port.sealedRowT0Mono(sessionId) }
                .onFailure { MLog.d("sealedRowT0Mono($sessionId) read failed: ${it.message}") }
                .getOrNull()
                        _phase.value = ArmPhase.SealedRunning(
                            sessionId = sessionId,
                            t0Mono = rowT0 ?: t0
                        )
                    } else {
                        failSeal(attemptId)
                    }
                }
            }
        }
    }

    /** The seal refused at its own gate (Klinke G5/G6): the row
     *  died under the attempt between proof and the seal act.
     *  No Throw sounded and none ever will for this attempt; the
     *  machine hands the slide back with the annulment's
     *  past-tense sentence. The row is NOT deleted here — the
     *  refusal means it is already gone or no longer ours. */
    private fun failSeal(attemptId: Long) {
        val current = _phase.value
        if (current.attemptIdOrNull() != attemptId) return
        transition.cancelSealPrime()
        _phase.value = ArmPhase.Idle
        _annulCount.value += 1
        val sentence = RefusalSentence(
            textRes = R.string.sentence_not_sealed_session,
            action = null
        )
        _armSentence.value = sentence
        sentenceClearJob?.cancel()
        sentenceClearJob = scope.launch {
            delay(SENTENCE_HOLD_MS)
            if (_armSentence.value === sentence) _armSentence.value = null
        }
    }

    /** ANNULMENT (final deadline, L+1500 — one retry window;
     *  fork F-OPAL-2 as recommended): the attempt un-exists.
     *  Row deleted (not deactivated), shield stopped, no
     *  tombstone, no Ledger line, no history — a session whose
     *  t0 never fired is not a session that failed; it is an arm
     *  that was refused late. Machine-initiated, pre-t0 only;
     *  after t=0 the only exits are the session's lawful doors. */
    private suspend fun annul(attemptId: Long) {
        val current = _phase.value
        if (current.attemptIdOrNull() != attemptId) return
        val sessionId = current.sessionIdOrNull()
        val unanswering = current.pendingLinks()
        deadAttempts += attemptId
        // Slice E (Riegel §1.2): a primed seal that never fires
        // must not leave its barrier standing over the queue.
        transition.cancelSealPrime()
        // Truth (issue #76): a FAILED snapshot read is not "no other
        // session", and a FAILED annul is not an annulment. The old
        // code flattened both through runCatching + getOrNull and
        // discarded results, so a DB hiccup could stop a stranger's
        // shield — and the refusal sentence below then published
        // "NOTHING WAS SEALED" over a row that may still stand.
        var annulFailed = false
        if (sessionId != null) {
            val activeResult = runCatching { port.activeSnapshotId() }
            val annulResult = runCatching { port.annulProvingSession(sessionId) }
            if (annulResult.isFailure) {
                annulFailed = true
                Log.e("ArmProving", "annulProvingSession($sessionId) failed", annulResult.exceptionOrNull())
            }
            // The shield is ours to stop only when a read that
            // SUCCEEDED says the active row is nobody's or ours.
            // A failed read proves nothing and stops nothing.
            val activeNow = activeResult.getOrNull()
            if (activeResult.isSuccess && (activeNow == null || activeNow == sessionId)) {
                port.stopShield()
            }
            // A different active row means another session (a
            // routine materialisation) took the seat mid-attempt:
            // our row is still annulled, but its shield is not
            // ours to stop.
        } else {
            port.stopShield()
        }
        _phase.value = ArmPhase.Idle
        _annulCount.value += 1
        val first = unanswering.minByOrNull { it.ordinal } ?: ArmLink.SESSION_ROW
        // Issue #126 F5: the batch started the shield in the
        // latch frame, so blocking may genuinely have intercepted
        // the user during the wait — enforcement the old sentence
        // ("NOTHING WAS SEALED") left him to discover alone,
        // against a row that no longer exists. The sentence now
        // closes that loop truthfully in every annul path: any
        // blocking the wait ran is off (annul stops the shield
        // above). Wording is conditional ("ANY") because in the
        // SESSION_ROW-jammed path no row ever armed the gate.
        val sentence = RefusalSentence(
            textRes = if (annulFailed) {
                R.string.sentence_annul_failed
            } else {
                // R3b #164 F5: the failed link is named in plain
                // words and the sentence says what happened, in
                // the order it happened — no studio code, and no
                // "NOTHING WAS SEALED" standing against the
                // screen's own ARM SESSION NOW.
                // (Merge note, #126 F5: this wording superseded
                // the branch's "ANY BLOCKING FROM THE WAIT IS OFF"
                // form when #164 re-ruled the annul sentence on
                // main; the F5 substance stands — annul stops the
                // shield above, so any blocking the wait ran is
                // off, and the recovery path's sentence in
                // resolveRecovery still says so in words.)
                R.string.sentence_not_sealed_named
            },
            args = if (annulFailed) emptyList() else listOf(first.plainName),
            action = null
        )
        _armSentence.value = sentence
        sentenceClearJob?.cancel()
        sentenceClearJob = scope.launch {
            delay(SENTENCE_HOLD_MS)
            if (_armSentence.value === sentence) _armSentence.value = null
        }
    }

    companion object {
        /** The seal's dwell floor: t=0 never fires before the
         *  latch + 210ms, however fast the links answer. On a
         *  healthy morning this is the binding term. */
        const val DWELL_MS = 210L

        /** Per-link timeout, concurrent from L (Opal §F2). Its
         *  expiry has no separate observable event: the pending
         *  set it leaves behind IS the jam set, declared at the
         *  deadline below. */
        const val LINK_TIMEOUT_MS = 400L

        /** The proving deadline: L + 500ms (400 + 100 slack). */
        const val DEADLINE_MS = 500L

        /** The annulment deadline: L + 1500ms — one retry window
         *  (F-OPAL-2, as recommended over two retries). */
        const val ANNUL_MS = 1500L

        const val SAMPLE_MS = 25L

        /** The seal watchdog's patience after t=0 is declared:
         *  the frame + seal act get this long to land before
         *  the attempt is handed back to the sampler's clocks.
         *  Generous beside the 210ms dwell and the frame's own
         *  cadence; a healthy seal lands in milliseconds. */
        const val SEAL_WATCHDOG_MS = 1_000L

        /** How long the annulment sentence stands in the slot. */
        const val SENTENCE_HOLD_MS = 10_000L
    }
}
