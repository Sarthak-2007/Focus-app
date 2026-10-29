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

    /**
     * One retry attempt, from any trigger (alarm, boot, Deck
     * open): find the faulted row, and if its next cadence
     * instant has arrived, try the stop again. Returns true when
     * the shield stands down (healed — filed to the record).
     */
    suspend fun runDueRetry(context: Context): Boolean = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val dao = MonolithDatabase.getInstance(appContext).monolithDao()
        val faulted = dao.getSessionHistorySnapshot()
            .firstOrNull { it.releaseFaultShield && it.releaseFaultedAtWallMillis > 0L }
            ?: return@withContext false
        val now = System.currentTimeMillis()
        if (MonolithShieldService.instance == null) {
            // Healed before this trigger even fired (the shield
            // stopped on its own poll): the cadence ends here.
            cancel(appContext)
            return@withContext true
        }
        val due = nextRetryWallMillis(faulted.releaseFaultedAtWallMillis, faulted.releaseRetriesDone)
        if (due == null) {
            cancel(appContext)
            return@withContext false
        }
        if (now < due) {
            scheduleNext(appContext, due)
            return@withContext false
        }
        MonolithShieldService.stopShield(appContext)
        val done = faulted.releaseRetriesDone + 1
        dao.stampReleaseRetries(faulted.id, done)
        val next = nextRetryWallMillis(faulted.releaseFaultedAtWallMillis, done)
        if (next != null) {
            scheduleNext(appContext, next)
        } else {
            cancel(appContext)
        }
        // The stop was commanded; whether it landed is the next
        // trigger's read (instance == null above), never assumed.
        false
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
