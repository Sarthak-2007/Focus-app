package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The chain alarm's receiver — a hint shell (routines rebuild §2.1).
 *
 * It carries NO routine id, NO boundary, and NO logic: a fire means
 * only "a boundary may have passed — reconcile." All judgement lives
 * in derive(); all hands are the heartbeat's. A stale, duplicated, or
 * mis-delivered fire reconciles against stored state and changes
 * nothing that isn't owed.
 *
 * Issue #46: a background broadcast must never crash the app or kill
 * the chain. The tick runs on a SupervisorJob scope with a
 * CoroutineExceptionHandler and an inner try/catch (CancellationException
 * is rethrown, never swallowed); the heartbeat rearms the chain FIRST
 * inside the tick, so even a failing reconcile leaves the next
 * boundary armed. pendingResult.finish() runs exactly once, in the
 * finally, on every path.
 */
class RoutineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        val finished = AtomicBoolean(false)
        fun finishOnce() {
            if (finished.compareAndSet(false, true)) {
                pendingResult.finish()
            }
        }
        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "chain-alarm tick failed — chain rearm runs first in the tick, so the next boundary stays armed", throwable)
            finishOnce()
        }
        CoroutineScope(SupervisorJob() + Dispatchers.IO + handler).launch {
            try {
                RoutineHeartbeat.tick(appContext, RoutineHeartbeat.Source.CHAIN_ALARM, force = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "chain-alarm tick failed — chain rearm runs first in the tick, so the next boundary stays armed", e)
            } finally {
                finishOnce()
            }
        }
    }

    private companion object {
        const val TAG = "RoutineAlarmReceiver"
    }
}
