package com.example.service

import android.content.Context
import android.os.SystemClock
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

/**
 * SLICE E — THE SESSION SEAL TRANSITION (F-S1(i): FULL
 * CONVERGENCE, Ren's ruling §12). The one place the seal happens:
 * every seal in the app — the Deck's arm through the proving
 * cascade, a routine's materialisation through this file's own
 * witness loop, and the F-K1 recovery — executes through
 * [executeSeal], and `playSealThrow()` occurs exactly once in
 * production code, inside it (grep gate T-R2). The Throw is the
 * transition's LAST statement: claim → seal the row → publish →
 * Throw. A seal that cannot complete does not sound.
 *
 * Process-scoped single instance ([SessionSealTransitions]): the
 * claim set is a per-process fact (a claim is one process's
 * attempt). The annulment tombstones are NOT (issue #48): they
 * persist in the seal prefs beside the marker, because annulment
 * deletes the row — memory was the only record, and a refused
 * routine window resurrected on process death.
 */
interface SessionSealTransition {

    /** Prime the engine's seal lane (at latch / at materialise). */
    fun primeForSeal()

    /** Release a prime that will never fire (annulment paths). */
    fun cancelSealPrime()

    /**
     * The seal act, exactly once per session: claim the session,
     * run [sealRow] (the repository's re-anchor + re-sign; false
     * refuses — no Throw, ever, for this attempt), commit the
     * seal marker, [publish] the sealed state, then sound the
     * Throw as the last statement. Returns true iff the seal
     * landed and the Throw fired.
     */
    suspend fun executeSeal(
        sessionId: Int,
        t0Mono: Long,
        sealRow: suspend () -> Boolean,
        publish: () -> Unit
    ): Boolean

    /**
     * The routine door's proving: a standalone witness loop over
     * the same three links the cascade proves (row / shield /
     * a11y), on the cascade's own clocks — jam at latch+500 with
     * one shield re-dispatch, annulment at latch+1500 by row
     * deletion plus a tombstone the heartbeat checks before it
     * ever materialises the same window again.
     */
    suspend fun proveAndSeal(sessionId: Int, latchMonoMs: Long?): SealVerdict

    /**
     * F-K1 recovery, run at relaunch over a born-active row no
     * live attempt owns. Marker present ⇒ the seal landed and
     * the Throw already sounded: adopt silently. Marker absent
     * and the row young (< 10 min) ⇒ the proving was interrupted
     * by death: a fresh cascade — links confirm ⇒ the Throw
     * FIRES, once, late, truthfully; witnesses dead ⇒ annul.
     * Marker absent and the row old ⇒ it sealed under an older
     * build (which had no marker): adopt, never Throw.
     */
    suspend fun recover(sessionId: Int, rowStartWallMs: Long, nowWallMs: Long): SealVerdict

    /** True when this session's seal has landed (in this process,
     *  or by the marker's testimony). ESR-02's verbs read it. */
    fun isSealed(sessionId: Int): Boolean

    /** True when an annulled proving tombstoned this routine
     *  window: the heartbeat must not materialise it again. */
    fun isTombstoned(routineId: Int?, windowStartMillis: Long): Boolean
}

enum class SealVerdict { SEALED, JAMMED, ANNULLED }

class SessionSealTransitionImpl(
    private val appContext: Context,
    private val engine: TactileSoundEngine,
    private val repository: MonolithRepository,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() }
) : SessionSealTransition {

    private val claimed = mutableSetOf<Int>()
    private val sealed = mutableSetOf<Int>()

    /** Annulment tombstones, loaded from the seal prefs on first
     *  touch (issue #48): the refusal outlives the process, like
     *  the row deletion it accompanies. */
    private val tombstones: MutableSet<Pair<Int, Long>> by lazy { loadPersistedTombstones() }
    private val proving = AtomicBoolean(false)

    override fun primeForSeal() = engine.primeForSeal()

    override fun cancelSealPrime() = engine.cancelSealPrime()

    /** Truth Car 3 (UNA-03): a bind re-reads the persisted sound
     *  fact into this transition's engine. Construction already
     *  births the engine from the fact; the re-read is the second
     *  lock — no path through [SessionSealTransitions.get] can
     *  hand out a transition whose engine carries anything but
     *  the fact as it stands at bind time. */
    fun resyncSoundFromPersistedFact() {
        engine.soundEnabled = SoundEffectsSetting.isEnabled(appContext)
    }

    /** Test seam: this transition's engine's sound gate, as bound. */
    fun engineSoundEnabledForTest(): Boolean = engine.soundEnabled

    private fun claim(sessionId: Int): Boolean = synchronized(claimed) { claimed.add(sessionId) }

    /** The seal marker (F-K1): committed synchronously BEFORE
     *  the Throw sounds, so a process dying mid-seal leaves
     *  proof on disk that the seal landed — recovery then adopts
     *  instead of re-sounding a Throw for a seal already heard.
     *  Issue #48: the in-memory add is synchronized (isSealed and
     *  recover already read/add under the same lock; this write
     *  raced them), and the marker records EVERY sealed session
     *  in a persisted id set — the legacy single int alone let a
     *  second seal erase the first session's only on-disk proof.
     *  The int still lands, as the latest-seal witness ESR-02 and
     *  older readers consult. */
    private fun writeSealMarker(sessionId: Int) {
        synchronized(sealed) { sealed += sessionId }
        appContext.getSharedPreferences(MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(MonolithRepository.SEAL_MARKER_SESSION_KEY, sessionId)
            .putLong(MonolithRepository.SEAL_MARKER_WALL_KEY, System.currentTimeMillis())
            .putStringSet(
                MonolithRepository.SEAL_MARKER_SESSIONS_KEY,
                persistedSealedIds() + sessionId.toString()
            )
            .commit()
    }

    private fun persistedSealedIds(): Set<String> =
        appContext.getSharedPreferences(MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(MonolithRepository.SEAL_MARKER_SESSIONS_KEY, emptySet())
            .orEmpty()

    /** Rollback for a seal that did not complete (issue #48): when
     *  [executeSeal]'s publish throws after the marker committed,
     *  the marker must not keep testifying to a seal that never
     *  published — strike this session from the in-memory set and,
     *  where the persisted slots name it, from disk (the legacy
     *  int falls back to another still-sealed session, or clears).
     *  Recovery then reads the truth — marker absent, row young —
     *  and re-proves, instead of adopting a seal that never
     *  sounded as if it had been heard. */
    private fun clearSealMarker(sessionId: Int) {
        synchronized(sealed) { sealed -= sessionId }
        val prefs = appContext.getSharedPreferences(
            MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE
        )
        val ids = persistedSealedIds()
        val namesThis = prefs.getInt(MonolithRepository.SEAL_MARKER_SESSION_KEY, -1) == sessionId
        if (!namesThis && sessionId.toString() !in ids) return
        val editor = prefs.edit()
        val remaining = ids - sessionId.toString()
        if (sessionId.toString() in ids) {
            editor.putStringSet(MonolithRepository.SEAL_MARKER_SESSIONS_KEY, remaining)
        }
        if (namesThis) {
            val fallback = remaining.firstNotNullOfOrNull { it.toIntOrNull() }
            if (fallback != null) {
                editor.putInt(MonolithRepository.SEAL_MARKER_SESSION_KEY, fallback)
            } else {
                editor.remove(MonolithRepository.SEAL_MARKER_SESSION_KEY)
            }
        }
        editor.commit()
    }

    private fun loadPersistedTombstones(): MutableSet<Pair<Int, Long>> {
        val raw = appContext.getSharedPreferences(
            MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE
        ).getStringSet(TOMBSTONES_KEY, emptySet()).orEmpty()
        return raw.mapNotNull { entry ->
            val parts = entry.split(':')
            val id = parts.getOrNull(0)?.toIntOrNull()
            val start = parts.getOrNull(1)?.toLongOrNull()
            if (id != null && start != null) id to start else null
        }.toMutableSet()
    }

    /** Persist the tombstone set (issue #48). Entries whose window
     *  start is older than [TOMBSTONE_KEEP_MS] can never be
     *  consulted again — the heartbeat derives over a 48h lookback
     *  — and are pruned here, so the set stays the size of the
     *  refusal horizon, not of the install's life. */
    private fun persistTombstones() {
        val now = System.currentTimeMillis()
        val live = synchronized(tombstones) {
            tombstones.removeAll { (_, start) -> now - start > TOMBSTONE_KEEP_MS }
            tombstones.map { (id, start) -> "$id:$start" }.toSet()
        }
        appContext.getSharedPreferences(MonolithRepository.SEAL_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(TOMBSTONES_KEY, live)
            .commit()
    }

    override suspend fun executeSeal(
        sessionId: Int,
        t0Mono: Long,
        sealRow: suspend () -> Boolean,
        publish: () -> Unit
    ): Boolean {
        if (!claim(sessionId)) return false
        val ok = runCatching { sealRow() }.getOrDefault(false)
        if (!ok) {
            // The claim stays spent (Klinke G5): a dead attempt
            // cannot seal late, and no Throw ever sounds over an
            // un-re-anchored row.
            return false
        }
        writeSealMarker(sessionId)
        // Issue #48: publish is guarded. A throwing publish used
        // to leave the committed marker testifying "sealed and
        // heard" over a seal that never published and never
        // sounded — and recovery would adopt it silently, forever.
        // Now the marker is struck and the act reports failure;
        // the claim stays spent (above), so nothing re-seals in
        // this process, and recovery re-proves from the true
        // marker-absent state.
        val published = runCatching { publish() }.isSuccess
        if (!published) {
            clearSealMarker(sessionId)
            return false
        }
        engine.playSealThrow()
        return true
    }

    override fun isSealed(sessionId: Int): Boolean =
        synchronized(sealed) { sessionId in sealed } ||
            sessionId in repository.sealedSessionIdsFromMarker()

    override fun isTombstoned(routineId: Int?, windowStartMillis: Long): Boolean =
        routineId != null && synchronized(tombstones) {
            (routineId to windowStartMillis) in tombstones
        }

    /** The three witnesses, read directly off their services —
     *  the same expressions the cascade's LiveArmWitnesses uses
     *  (two derivations of one fact would disagree; these are
     *  reads of the same stored stamps, not derivations). */
    private fun shieldMono(sessionId: Int): Long? =
        if (MonolithShieldService.instance != null &&
            MonolithShieldService.protecting &&
            MonolithShieldService.protectingSessionId == sessionId
        ) {
            MonolithShieldService.protectingMono.takeIf { it > 0L }
        } else {
            null
        }

    private fun a11yMono(sessionId: Int): Long? =
        if (MonolithAccessibilityService.instance != null &&
            MonolithAccessibilityService.armedSessionId == sessionId
        ) {
            MonolithAccessibilityService.armedMono.takeIf { it > 0L }
        } else {
            null
        }

    override suspend fun proveAndSeal(sessionId: Int, latchMonoMs: Long?): SealVerdict =
        prove(sessionId, latchMonoMs ?: clock(), annulOnFailure = true)

    override suspend fun recover(
        sessionId: Int,
        rowStartWallMs: Long,
        nowWallMs: Long
    ): SealVerdict {
        if (sessionId in repository.sealedSessionIdsFromMarker()) {
            synchronized(sealed) { sealed += sessionId }
            return SealVerdict.SEALED
        }
        if (nowWallMs - rowStartWallMs >= RECOVERY_YOUNG_WINDOW_MS) {
            // An old, marker-absent row sealed under an older
            // build: adopt it as the running session it is. A
            // Throw now would be a haunting, not a proof.
            synchronized(sealed) { sealed += sessionId }
            return SealVerdict.SEALED
        }
        return prove(sessionId, clock(), annulOnFailure = true)
    }

    private suspend fun prove(
        sessionId: Int,
        latchMono: Long,
        annulOnFailure: Boolean
    ): SealVerdict {
        if (!proving.compareAndSet(false, true)) return SealVerdict.JAMMED
        try {
            primeForSeal()
            var l1: Long? = null
            var l2: Long? = null
            var l3: Long? = null
            var jamDispatched = false
            while (true) {
                val now = clock()
                // Witnesses are re-read live every sample; a
                // confirmed link whose witness dies pre-t=0
                // re-pends (the cascade's law, mirrored).
                val row = runCatching { repository.activeSessionSnapshot() }.getOrNull()
                l1 = if (row != null && row.id == sessionId && row.isActive) l1 ?: now else null
                val sNow = shieldMono(sessionId)
                l2 = if (sNow == null) null else (l2 ?: sNow)
                val aNow = a11yMono(sessionId)
                l3 = if (aNow == null) null else (l3 ?: aNow)

                if (l1 != null && l2 != null && l3 != null) {
                    val lastConfirm = maxOf(l1, l2, l3)
                    val t0 = maxOf(lastConfirm, latchMono + DWELL_MS)
                    if (now >= t0) {
                        val sealedOk = executeSeal(
                            sessionId = sessionId,
                            t0Mono = t0,
                            sealRow = { repository.sealSession(sessionId, t0) },
                            publish = {}
                        )
                        return if (sealedOk) SealVerdict.SEALED else SealVerdict.ANNULLED
                    }
                }

                if (!jamDispatched && now >= latchMono + JAM_MS) {
                    jamDispatched = true
                    // Machine-only retry: the shield re-dispatches;
                    // L1 and L3 have no dispatch of their own.
                    if (l2 == null) MonolithShieldService.startShield(appContext)
                }

                if (now >= latchMono + ANNUL_MS) {
                    cancelSealPrime()
                    if (!annulOnFailure) return SealVerdict.JAMMED
                    val dead = runCatching { repository.sessionById(sessionId) }.getOrNull()
                    runCatching { repository.annulProvingSession(sessionId) }
                    if (dead?.routineId != null) {
                        synchronized(tombstones) {
                            tombstones += (dead.routineId to dead.windowStartMillis)
                        }
                        // Issue #48: the refusal is persisted in
                        // the same breath — the row is being
                        // deleted, so this write is the only record
                        // that survives process death.
                        persistTombstones()
                    }
                    // The shield stops only if it is still ours.
                    if (MonolithShieldService.protectingSessionId == sessionId ||
                        MonolithShieldService.instance == null
                    ) {
                        MonolithShieldService.stopShield(appContext)
                    }
                    return SealVerdict.ANNULLED
                }

                delay(SAMPLE_MS)
            }
        } finally {
            proving.set(false)
        }
    }

    companion object {
        /** The cascade's dwell floor, mirrored: t=0 never fires
         *  before latch + 210 ms. */
        const val DWELL_MS = 210L

        /** Jam at latch + 500 (400 per-link + 100 slack). */
        const val JAM_MS = 500L

        /** Annulment at latch + 1500 — one retry window. */
        const val ANNUL_MS = 1500L

        const val SAMPLE_MS = 25L

        /** A born-active row younger than this at relaunch is a
         *  proving death interrupted; older is an older build's
         *  seal (F-K1's fork premise, as ruled). */
        const val RECOVERY_YOUNG_WINDOW_MS = 600_000L

        /** The annulment tombstones' prefs key (issue #48), in the
         *  seal marker's own prefs file. */
        const val TOMBSTONES_KEY = "annulled_windows"

        /** How long a tombstone is kept: the heartbeat's derive
         *  lookback is 48h; past this horizon (48h + a day's
         *  margin) no derivation can name the window again. */
        const val TOMBSTONE_KEEP_MS = 72L * 60L * 60L * 1000L
    }
}

/** The process-scoped provider: the VM's controller and the
 *  routine heartbeat share ONE transition, so the claim set and
 *  the tombstones are single facts. First construction wins its
 *  dependencies; later callers receive the standing instance. */
object SessionSealTransitions {

    @Volatile
    private var instance: SessionSealTransitionImpl? = null

    fun get(
        context: Context,
        engine: TactileSoundEngine? = null,
        repository: MonolithRepository? = null
    ): SessionSealTransition {
        // Truth Car 3 (UNA-03): whichever path binds — first
        // construction or the standing instance — the returned
        // transition's engine is re-read against the persisted
        // sound fact before the caller can seal through it. The
        // heartbeat's engine-less bind (the UNA-03 failure shape:
        // a routine-born process binding a default-ON engine) is
        // covered twice over: the fresh engine is born from the
        // fact, and the bind re-reads it.
        instance?.let { it.resyncSoundFromPersistedFact(); return it }
        synchronized(this) {
            instance?.let { it.resyncSoundFromPersistedFact(); return it }
            val appContext = context.applicationContext
            val repo = repository ?: MonolithRepository(
                appContext, MonolithDatabase.getInstance(appContext).monolithDao()
            )
            val eng = engine ?: TactileSoundEngine(appContext)
            val built = SessionSealTransitionImpl(appContext, eng, repo)
            built.resyncSoundFromPersistedFact()
            instance = built
            return built
        }
    }

    fun peek(): SessionSealTransition? = instance

    /** Test seam: the standing instance's engine sound gate, or
     *  null before the first bind. */
    fun boundEngineSoundEnabledForTest(): Boolean? =
        instance?.engineSoundEnabledForTest()

    /** Test seam: the provider is a process singleton; unit tests
     *  reset it between cases (the WatchStore precedent).
     *  Production code never calls this. */
    fun resetForTest() {
        synchronized(this) { instance = null }
    }
}
