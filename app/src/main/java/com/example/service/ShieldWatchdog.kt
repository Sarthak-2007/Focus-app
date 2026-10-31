package com.example.service

import android.content.Context
import com.example.data.MonolithDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Issue #323 (Audit R5 — YPT head-to-head, F3): the shield watchdog.
 *
 * The finding, verified: YPT re-arms its blockers with a dedicated
 * alarm-receiver watchdog that checks for an active block before
 * restarting anything; Monolith's shield restart was START_STICKY
 * plus the boot receiver — if an OEM battery killer or an OOM kill
 * took the shield service down mid-session and the system did not
 * honour START_STICKY, nothing noticed until the user next opened
 * the app, while the session row still claimed protection.
 *
 * The cure rides the alarm that already exists: the routine chain
 * alarm fires the heartbeat on every schedule boundary, and its
 * receiver now also runs this watchdog. One question, asked on
 * every chain-alarm fire (and only there — the heartbeat's purity
 * law is untouched, and the boot receiver already covers reboots):
 *
 *   a session is active AND no shield instance stands
 *     → the shield died underneath a live block: restart it.
 *
 * Every other combination is a no-op by construction:
 *  - no active session: a stopped shield is the correct state
 *    (disarm ends the session and the shield together);
 *  - shield standing: nothing to heal.
 *
 * The decision is pure ([shouldRestartShield]) and unit-tested;
 * [runOnce] is the thin Android shell around it.
 */
object ShieldWatchdog {

    /** The whole law: restart only a shield that died under a
     *  live session. */
    fun shouldRestartShield(hasActiveSession: Boolean, shieldPresent: Boolean): Boolean =
        hasActiveSession && !shieldPresent

    /** One watchdog pass. Returns true when it restarted the
     *  shield. Safe from any trigger: a stale or duplicated pass
     *  reads current state and heals only what is actually dead. */
    suspend fun runOnce(context: Context): Boolean = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val dao = MonolithDatabase.getInstance(appContext).monolithDao()
        val active = dao.getActiveSessionSnapshot()
        if (!shouldRestartShield(active != null, MonolithShieldService.instance != null)) {
            return@withContext false
        }
        MonolithShieldService.startShield(appContext)
        true
    }
}
