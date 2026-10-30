package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The chain alarm's receiver — a hint shell (routines rebuild §2.1).
 *
 * It carries NO routine id, NO boundary, and NO logic: a fire means
 * only "a boundary may have passed — reconcile." All judgement lives
 * in derive(); all hands are the heartbeat's. A stale, duplicated, or
 * mis-delivered fire reconciles against stored state and changes
 * nothing that isn't owed.
 *
 * Issue #323 F3: after the reconcile, the same fire runs the
 * ShieldWatchdog — if a session is live but the shield service
 * died underneath it (OEM kill, OOM, an unhonoured START_STICKY),
 * the watchdog restarts it. The watchdog carries its own one-line
 * law; this receiver only gives it the alarm's cadence.
 */
class RoutineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RoutineHeartbeat.tick(appContext, RoutineHeartbeat.Source.CHAIN_ALARM, force = true)
                ShieldWatchdog.runOnce(appContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
