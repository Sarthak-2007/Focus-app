package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SLICE E — DRAHT'S RELEASE RETRY. When a finish's shield would
 * not stop (the ENDED-FAULT branch), the machine does not give
 * up at the receipt: it retries the stop on a persisted cadence
 * — +30s, +5min, and on Deck-open — with its state in the ended
 * row's own release* columns (9→10), so a dead process loses
 * nothing. Alarms are HINTS, never the mechanism (the routines
 * machinery's law, cloned): the cadence is re-derived from
 * releaseFaultedAtWallMillis + releaseRetriesDone on every
 * trigger, so a lost or duplicated alarm costs nothing.
 *
 * A healed fault files to the record — the retry stops, the row
 * keeps its fault stamps as history — and NEVER amends the
 * issued receipt: the slip printed the truth at its instant, and
 * a receipt that rewrites itself afterwards is a lie with a
 * timestamp.
 *
 * Issue #47 (heartbeat-retry) cures, now part of the law here:
 *  - A heal CLEARS the row's fault flag ([MonolithDao.clearReleaseFault]):
 *    a fault that healed — the shield stopped on its own, or a
 *    later session's shield replaced it — must never be picked
 *    up again, or a stale row would command a stop against a
 *    later, unrelated live session's shield.
 *  - A live session is NEVER a retry target: while another
 *    session is active, the standing shield (if any) protects
 *    THAT session, so the fault is superseded, cleared, and no
 *    stop is ever commanded.
 *  - The Deck-open third door is REAL: once both timed retries
 *    are spent, the next trigger makes one final stop attempt
 *    before the cadence closes for good.
 *  - Spacing survives late triggers: a retry rescheduled after
 *    a late attempt is placed at least its cadence gap after
 *    THAT attempt, never at a past instant that fires instantly.
 */
object ReleaseRetryScheduler {

    private const val REQUEST_CODE = 0x5207

    /** The cadence: retry 1 at +30s, retry 2 at +5min, then the
     *  Deck-open trigger is the only door left. */
    val CADENCE_OFFSETS_MS = longArrayOf(30_000L, 300_000L)

    fun scheduleNext(context: Context, atWallMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = pendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        alarmManager.cancel(intent)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atWallMillis, intent)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        pendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)?.let { alarmManager.cancel(it) }
    }

    private fun pendingIntent(context: Context, flags: Int): PendingIntent? {
        val intent = Intent(context, ReleaseRetryReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** The next cadence instant still owed for a fault stamped at
     *  [faultWallMillis] with [retriesDone] spent, or null when
     *  the cadence is exhausted. Pure — the cadence's whole law. */
    fun nextRetryWallMillis(faultWallMillis: Long, retriesDone: Int): Long? =
        CADENCE_OFFSETS_MS.getOrNull(retriesDone)?.let { faultWallMillis + it }

    /** The instant the NEXT retry may be scheduled for, after an
     *  attempt made at [nowMillis]. Normally the nominal cadence
     *  instant; when a trigger ran so late that the nominal
     *  instant is already past (issue #47 F4 — a past alarm fires
     *  instantly and collapses the cadence), the retry keeps its
     *  intended spacing after THIS attempt instead. Pure. */
    fun nextRetryAtMillis(faultWallMillis: Long, retriesDone: Int, nowMillis: Long): Long? {
        val nominal = nextRetryWallMillis(faultWallMillis, retriesDone) ?: return null
        if (nominal > nowMillis) return nominal
        val previousOffset = if (retriesDone > 0) CADENCE_OFFSETS_MS[retriesDone - 1] else 0L
        return nowMillis + (CADENCE_OFFSETS_MS[retriesDone] - previousOffset)
    }

    /** What one trigger must do about a faulted row. Pure — every
     *  branch of [runDueRetry] is decided here so the whole matrix
     *  is unit-testable without Android. */
    enum class RetryAction {
        /** No shield stands at all: the fault healed on its own. */
        HEALED_NO_SHIELD,

        /** Another session is live: the standing shield protects
         *  IT, never the faulted row — the fault is superseded and
         *  no stop may be commanded. */
        SUPERSEDED_BY_LIVE_SESSION,

        /** The next timed cadence instant is still ahead. */
        WAIT_UNTIL_DUE,

        /** A timed cadence instant has arrived: try the stop. */
        STOP_TIMED,

        /** Both timed retries are spent: this trigger IS the
         *  advertised third door (Deck-open) — one final attempt. */
        STOP_FINAL,

        /** The final attempt is spent too: the cadence is closed. */
        EXHAUSTED
    }

    fun retryAction(
        nowMillis: Long,
        faultWallMillis: Long,
        retriesDone: Int,
        shieldPresent: Boolean,
        liveSessionActive: Boolean
    ): RetryAction = when {
        !shieldPresent -> RetryAction.HEALED_NO_SHIELD
        liveSessionActive -> RetryAction.SUPERSEDED_BY_LIVE_SESSION
        retriesDone < CADENCE_OFFSETS_MS.size -> {
            val due = faultWallMillis + CADENCE_OFFSETS_MS[retriesDone]
            if (nowMillis < due) RetryAction.WAIT_UNTIL_DUE else RetryAction.STOP_TIMED
        }
        retriesDone == CADENCE_OFFSETS_MS.size -> RetryAction.STOP_FINAL
        else -> RetryAction.EXHAUSTED
    }

    /**
     * One retry attempt, from any trigger (alarm, boot, Deck
     * open): find the faulted row, and if its next cadence
     * instant has arrived, try the stop again. Returns true when
     * the shield stands down (healed — filed to the record).
     */
    suspend fun runDueRetry(context: Context): Boolean = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val dao = MonolithDatabase.getInstance(appContext).monolithDao()
        // Targeted read (issue #47 F5): newest faulted row only,
        // never a full-history scan per trigger.
        val faulted = dao.getFaultedReleaseSession() ?: return@withContext false
        val now = System.currentTimeMillis()
        val active = dao.getActiveSessionSnapshot()
        when (
            retryAction(
                nowMillis = now,
                faultWallMillis = faulted.releaseFaultedAtWallMillis,
                retriesDone = faulted.releaseRetriesDone,
                shieldPresent = MonolithShieldService.instance != null,
                liveSessionActive = active != null && active.id != faulted.id
            )
        ) {
            RetryAction.HEALED_NO_SHIELD,
            RetryAction.SUPERSEDED_BY_LIVE_SESSION -> {
                // The fault is over — healed on its own poll, or
                // superseded by a later session's shield. CLEAR it
                // (issue #47 F1): an uncleared stale fault was
                // picked up by later triggers and commanded a stop
                // against a live session's shield. The fault stamps
                // stay on the row as history; only the live flag goes.
                dao.clearReleaseFault(faulted.id)
                cancel(appContext)
                true
            }
            RetryAction.WAIT_UNTIL_DUE -> {
                scheduleNext(
                    appContext,
                    nextRetryWallMillis(faulted.releaseFaultedAtWallMillis, faulted.releaseRetriesDone)!!
                )
                false
            }
            RetryAction.STOP_TIMED -> {
                MonolithShieldService.stopShield(appContext)
                // Issue #30 finding 4: atomic increment in SQLite,
                // then a fresh read for the cadence arithmetic —
                // a caller-computed absolute write could lose a
                // retry when two triggers raced on the same row.
                dao.incrementReleaseRetries(faulted.id)
                val done = dao.getSessionById(faulted.id)?.releaseRetriesDone
                    ?: (faulted.releaseRetriesDone + 1)
                // Spacing-preserving reschedule (issue #47 F4):
                // never an instant in the past.
                val next = nextRetryAtMillis(faulted.releaseFaultedAtWallMillis, done, now)
                if (next != null) {
                    scheduleNext(appContext, next)
                } else {
                    cancel(appContext)
                }
                // The stop was commanded; whether it landed is the
                // next trigger's read (instance == null), never assumed.
                false
            }
            RetryAction.STOP_FINAL -> {
                // The third door (issue #47 F2): both timed retries
                // were spent without the shield standing down, so
                // THIS trigger makes the final attempt the header
                // always promised — then the cadence closes.
                MonolithShieldService.stopShield(appContext)
                dao.stampReleaseRetries(faulted.id, CADENCE_OFFSETS_MS.size + 1)
                cancel(appContext)
                false
            }
            RetryAction.EXHAUSTED -> {
                cancel(appContext)
                false
            }
        }
    }
}

/** The retry alarm's receiver — a hint shell, like the routine
 *  chain's: a fire means only "a cadence instant may have
 *  passed — derive and act". */
class ReleaseRetryReceiver : android.content.BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReleaseRetryScheduler.runDueRetry(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
