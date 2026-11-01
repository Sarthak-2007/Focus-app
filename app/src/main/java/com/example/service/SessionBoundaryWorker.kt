package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.AppContainer
import com.example.data.RoutineDerive
import java.util.concurrent.TimeUnit

/**
 * Audit R6 #341 F17 — the REDUNDANT wake path for session
 * boundaries. Freedom wakes a dead process for a scheduled
 * boundary with a forced WorkManager event; Monolith's only path
 * was the alarm chain — exact alarms and the heartbeat improved
 * it, but a chain whose registration died with its process had no
 * second road in.
 *
 * This worker is that second road, and it is deliberately a HINT
 * SHELL like RoutineAlarmReceiver: a fire means only "a boundary
 * may have passed — reconcile", and it calls the same heartbeat
 * tick the chain alarm calls. The tick is idempotent by design
 * (derive() judges from stored state; a duplicated fire from both
 * paths changes nothing that isn't owed), so redundancy here can
 * never double-start or double-end a session. After ticking, the
 * worker re-arms its own next one-time job at the next boundary —
 * the worker path perpetuates itself exactly as the chain does.
 *
 * WorkManager's jobs are persisted by the platform across process
 * death and reboot, which is the entire point: this path survives
 * the deaths the alarm chain does not.
 */
class SessionBoundaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        RoutineHeartbeat.tick(appContext, RoutineHeartbeat.Source.WORKER, force = true)
        ShieldWatchdog.runOnce(appContext)
        // Self-perpetuate: arm this path's next boundary.
        val routines = AppContainer.repository(appContext).allRoutinesSnapshot()
        BoundaryWakeScheduler.armNext(appContext, routines, System.currentTimeMillis())
        return Result.success()
    }
}

/**
 * The worker path's arming law (F17). One unique one-time job at
 * the next routine boundary; re-arming replaces the pending job,
 * cancelling removes it — mirroring the alarm chain's
 * cancel-then-set discipline so the two paths never disagree
 * about which boundary is next (both derive it from the same
 * RoutineDerive.nextBoundaryMillis).
 */
object BoundaryWakeScheduler {

    const val UNIQUE_WORK_NAME = "monolith_session_boundary_wake"

    /** The delay a boundary [boundaryMillis] seen from
     *  [nowMillis] arms with — never negative; a boundary at or
     *  behind now arms an immediate job. Pure, for tests. */
    fun delayMillis(boundaryMillis: Long, nowMillis: Long): Long =
        (boundaryMillis - nowMillis).coerceAtLeast(0L)

    fun armNext(context: Context, routines: List<com.example.data.RoutineEntity>, nowMillis: Long) {
        val boundary = RoutineDerive.nextBoundaryMillis(routines, nowMillis)
        if (boundary == null) {
            cancel(context)
            return
        }
        armAt(context, boundary, nowMillis)
    }

    fun armAt(context: Context, boundaryMillis: Long, nowMillis: Long) {
        val request = OneTimeWorkRequestBuilder<SessionBoundaryWorker>()
            .setInitialDelay(delayMillis(boundaryMillis, nowMillis), TimeUnit.MILLISECONDS)
            .addTag(UNIQUE_WORK_NAME)
            .build()
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}
