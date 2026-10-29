package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.MainActivity
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
 * Issue #46 (routine-scheduler audit): routine boundaries are
 * user-asked times and must fire EXACTLY. The chain is therefore armed
 * as an alarm clock ([AlarmManager.setAlarmClock]) — exempt from Doze
 * and App Standby, with the user-visible alarm affordance pointing at
 * MainActivity. The inexact [AlarmManager.setAndAllowWhileIdle] path
 * survives ONLY as a visibly DEGRADED fallback for when the platform
 * denies exact alarms (no SCHEDULE_EXACT_ALARM grant on API 31+, or a
 * SecurityException at arm time); it is never silent and never
 * reported as exact. Arming NEVER fails silently: every path returns a
 * [ChainArmResult] and logs.
 *
 * The legacy per-routine alarms (request codes id*2 / id*2+1, extras
 * routine_id/boundary) exist only so [cancelLegacyRoutineAlarms] can
 * reproduce their PendingIntents exactly and cancel them at reseed.
 */
object RoutineScheduler {

    private const val TAG = "RoutineScheduler"

    const val CHAIN_REQUEST_CODE = 0x5201
    const val CHAIN_SHOW_REQUEST_CODE = 0x5202

    const val DEGRADED_REASON_NO_EXACT_PERMISSION =
        "DEGRADED: exact alarms not permitted (canScheduleExactAlarms=false); " +
            "armed inexactly with setAndAllowWhileIdle — boundary may fire late"

    // Legacy shipped-alarm identity — used ONLY to cancel them.
    const val EXTRA_ROUTINE_ID = "routine_id"
    const val EXTRA_BOUNDARY = "boundary"
    const val BOUNDARY_START = "start"
    const val BOUNDARY_END = "end"

    /** How the chain alarm will be armed. Pure decision output. */
    enum class ArmMode {
        /** setAlarmClock — exact, Doze/App-Standby-exempt. */
        ALARM_CLOCK_EXACT,

        /** setAndAllowWhileIdle — inexact DEGRADED fallback only. */
        ALLOW_WHILE_IDLE_INEXACT
    }

    /** The honest outcome of an arm attempt — never a silent return. */
    sealed interface ChainArmResult {
        /** Armed exactly via setAlarmClock at [boundaryMillis]. */
        data class ArmedExact(val boundaryMillis: Long) : ChainArmResult

        /**
         * Armed, but DEGRADED: inexact fallback because the platform
         * denied exact alarms. [reason] says why; the boundary may
         * fire late and callers must not record this as an exact arm.
         */
        data class ArmedInexact(val boundaryMillis: Long, val reason: String) : ChainArmResult

        /** NOT armed. [reason] says why; the chain is down until a
         *  later trigger retries. */
        data class Failed(val reason: String, val boundaryMillis: Long? = null) : ChainArmResult

        /** No next boundary exists — the chain was cancelled. */
        data object NoBoundary : ChainArmResult
    }

    /**
     * Pure arm-mode decision (issue #46): exact whenever the platform
     * allows it. Before API 31 (S) exact alarms needed no permission;
     * from API 31 on, [canScheduleExactAlarms] is the gate. Denied
     * exact capability degrades — visibly — to the inexact mode.
     */
    fun armMode(sdkInt: Int, canScheduleExactAlarms: Boolean): ArmMode =
        if (sdkInt < Build.VERSION_CODES.S || canScheduleExactAlarms) {
            ArmMode.ALARM_CLOCK_EXACT
        } else {
            ArmMode.ALLOW_WHILE_IDLE_INEXACT
        }

    /** Pure result mapping for a successful arm in [mode]. */
    fun chainArmResult(
        mode: ArmMode,
        boundaryMillis: Long,
        inexactReason: String = DEGRADED_REASON_NO_EXACT_PERMISSION
    ): ChainArmResult = when (mode) {
        ArmMode.ALARM_CLOCK_EXACT -> ChainArmResult.ArmedExact(boundaryMillis)
        ArmMode.ALLOW_WHILE_IDLE_INEXACT -> ChainArmResult.ArmedInexact(boundaryMillis, inexactReason)
    }

    /** Rearm the chain at the next boundary implied by [routines]. */
    fun rearmChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long): ChainArmResult {
        val boundary = RoutineDerive.nextBoundaryMillis(routines, nowMillis)
        return if (boundary == null) {
            cancelChain(context)
            ChainArmResult.NoBoundary
        } else {
            rearmChain(context, boundary)
        }
    }

    /** Cancel-then-set: exactly one chain alarm exists at any time. */
    fun rearmChain(context: Context, boundaryMillis: Long): ChainArmResult {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            ?: run {
                Log.e(TAG, "rearmChain FAILED at $boundaryMillis: AlarmManager unavailable — chain NOT armed")
                return ChainArmResult.Failed("AlarmManager unavailable", boundaryMillis)
            }
        val intent = chainPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)
            ?: run {
                Log.e(TAG, "rearmChain FAILED at $boundaryMillis: chain PendingIntent unavailable — chain NOT armed")
                return ChainArmResult.Failed("chain PendingIntent unavailable", boundaryMillis)
            }
        alarmManager.cancel(intent)

        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                alarmManager.canScheduleExactAlarms()
            } catch (e: SecurityException) {
                Log.w(TAG, "canScheduleExactAlarms threw — treating exact as denied (DEGRADED)", e)
                false
            }
        } else {
            true
        }

        return when (val mode = armMode(Build.VERSION.SDK_INT, canExact)) {
            ArmMode.ALARM_CLOCK_EXACT -> {
                val showIntent = chainShowPendingIntent(context)
                if (showIntent == null) {
                    Log.e(TAG, "rearmChain FAILED at $boundaryMillis: alarm-clock show PendingIntent unavailable — chain NOT armed")
                    ChainArmResult.Failed("alarm-clock show PendingIntent unavailable", boundaryMillis)
                } else {
                    try {
                        alarmManager.setAlarmClock(
                            AlarmManager.AlarmClockInfo(boundaryMillis, showIntent),
                            intent
                        )
                        ChainArmResult.ArmedExact(boundaryMillis)
                    } catch (e: SecurityException) {
                        // The platform denied exact at arm time despite
                        // the capability check — degrade VISIBLY, never
                        // silently, and never pretend the arm is exact.
                        Log.w(
                            TAG,
                            "setAlarmClock denied at $boundaryMillis — falling back DEGRADED to inexact",
                            e
                        )
                        armInexact(alarmManager, boundaryMillis, intent, "DEGRADED: setAlarmClock denied (${e.message}); armed inexactly — boundary may fire late")
                    }
                }
            }
            ArmMode.ALLOW_WHILE_IDLE_INEXACT ->
                armInexact(alarmManager, boundaryMillis, intent, DEGRADED_REASON_NO_EXACT_PERMISSION)
        }
    }

    /** The DEGRADED fallback arm. Logs at warning level on success so
     *  a degraded chain is always visible in the log; a failure here
     *  is a [ChainArmResult.Failed], never a silent return. */
    private fun armInexact(
        alarmManager: AlarmManager,
        boundaryMillis: Long,
        intent: PendingIntent,
        reason: String
    ): ChainArmResult {
        return try {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, boundaryMillis, intent)
            Log.w(TAG, "chain armed INEXACTLY at $boundaryMillis — $reason")
            ChainArmResult.ArmedInexact(boundaryMillis, reason)
        } catch (e: SecurityException) {
            Log.e(TAG, "rearmChain FAILED at $boundaryMillis: inexact fallback denied — chain NOT armed", e)
            ChainArmResult.Failed("inexact fallback denied: ${e.message}", boundaryMillis)
        }
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

    /** The alarm clock's user-visible affordance: tapping the system
     *  alarm indicator opens Monolith. */
    private fun chainShowPendingIntent(context: Context): PendingIntent? =
        PendingIntent.getActivity(
            context, CHAIN_SHOW_REQUEST_CODE,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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
