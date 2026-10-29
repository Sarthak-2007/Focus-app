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
 */
class RoutineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RoutineHeartbeat.tick(appContext, RoutineHeartbeat.Source.CHAIN_ALARM, force = true)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
