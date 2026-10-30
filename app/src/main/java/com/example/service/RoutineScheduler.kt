package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.RoutineDerive
import com.example.data.RoutineEntity

/**
 * The single self-rearming chain alarm (routines rebuild §2.1).
 *
 * Alarms are HINTS, never the mechanism: one chain alarm points at the
 * next schedule boundary; when it fires, the heartbeat reconciles
 * everything from stored state and rearms. A lost, late, or duplicated
 * alarm costs nothing — every other trigger (resume, boot, shield
 * loop, session edges) runs the same reconcile.
 *
 * The legacy per-routine alarms (request codes id*2 / id*2+1, extras
 * routine_id/boundary) exist only so [cancelLegacyRoutineAlarms] can
 * reproduce their PendingIntents exactly and cancel them at reseed.
 */
object RoutineScheduler {

    const val CHAIN_REQUEST_CODE = 0x5201

    // Legacy shipped-alarm identity — used ONLY to cancel them.
    const val EXTRA_ROUTINE_ID = "routine_id"
    const val EXTRA_BOUNDARY = "boundary"
    const val BOUNDARY_START = "start"
    const val BOUNDARY_END = "end"

    /** Rearm the chain at the next boundary implied by [routines]. */
    fun rearmChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long) {
        val boundary = RoutineDerive.nextBoundaryMillis(routines, nowMillis)
        if (boundary == null) cancelChain(context) else rearmChain(context, boundary)
    }

    /** Cancel-then-set: exactly one chain alarm exists at any time. */
    fun rearmChain(context: Context, boundaryMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = chainPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        alarmManager.cancel(intent)
        // Issue #319 F2: exact when the exact-alarm grant is held
        // (ExactAlarm's one law), inexact otherwise — the chain
        // stays a hint either way; the heartbeat reconciles.
        ExactAlarm.setWakeup(alarmManager, boundaryMillis, intent)
    }

    fun cancelChain(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        chainPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)?.let { alarmManager.cancel(it) }
    }

    /** True while a chain alarm is armed (FLAG_NO_CREATE probe — the
     *  heartbeat only rearms on boundary change or a missing chain). */
    fun isChainArmed(context: Context): Boolean =
        chainPendingIntent(context, PendingIntent.FLAG_NO_CREATE) != null

    /** Cancels the shipped per-routine alarms by reproducing their
     *  PendingIntents exactly (same component, extras, request codes). */
    fun cancelLegacyRoutineAlarms(context: Context, routineIds: List<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (id in routineIds) {
            for (boundary in listOf(BOUNDARY_START, BOUNDARY_END)) {
                val intent = Intent(context, RoutineAlarmReceiver::class.java).apply {
                    putExtra(EXTRA_ROUTINE_ID, id)
                    putExtra(EXTRA_BOUNDARY, boundary)
                }
                val requestCode = id * 2 + if (boundary == BOUNDARY_END) 1 else 0
                val pending = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pending)
                pending.cancel()
            }
        }
    }

    private fun chainIntent(context: Context): Intent =
        Intent(context, RoutineAlarmReceiver::class.java)

    // Nullable by contract: with FLAG_NO_CREATE, getBroadcast returns
    // null when no chain alarm exists — that null IS the answer
    // isChainArmed exists to receive. (A non-null declaration here
    // crashed the first-ever tick with an intrinsic null-check NPE;
    // the screenshot harness caught it.)
    private fun chainPendingIntent(context: Context, mode: Int): PendingIntent? =
        PendingIntent.getBroadcast(
            context, CHAIN_REQUEST_CODE, chainIntent(context),
            mode or PendingIntent.FLAG_IMMUTABLE
        )
}
