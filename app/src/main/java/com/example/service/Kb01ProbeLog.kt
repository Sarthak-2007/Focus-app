package com.example.service

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import com.example.data.FocusSessionEntity
import java.io.File

/**
 * KB01 SPIKE INSTRUMENTATION ONLY — Stage 4P, Kill Book Row 1
 * (time-lock integrity vs clock changes). Pit spike, never product.
 *
 * This logger changes NOTHING about the as-built firing chain
 * (PREP-04: the spike prices the chain that exists, not the chain
 * that should). It only records, per event, the signed triple the
 * claim is judged on — wall start · monotonic start · boot count —
 * plus the reading at the event instant, so the firing instant can
 * be computed in elapsed terms from the raw log alone:
 *
 *   firing elapsed offset = eventMono - row.startElapsedRealtimeMillis
 *   (same boot) — cross-boot arms use accumulatedBeforeRebootSeconds
 *   + mono since boot, exactly as computeVerifiedSessionElapsed does.
 *
 * Output: logcat tag KB01PROBE + append to filesDir/kb01-probe.log
 * (the file survives process kill and reboot; it is pulled as the
 * run's raw log under the Run ID).
 */
object Kb01ProbeLog {

    private const val TAG = "KB01PROBE"
    private const val FILE_NAME = "kb01-probe.log"

    @Volatile private var lastActiveSessionId: Int = -1
    @Volatile private var lastWasActive: Boolean = false

    private fun bootCountNow(context: Context): Int = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (e: Exception) {
        -1
    }

    @Synchronized
    fun log(context: Context, event: String, detail: String) {
        val line = "KB01 $event wall=${System.currentTimeMillis()} " +
            "mono=${SystemClock.elapsedRealtime()} boot=${bootCountNow(context)} $detail"
        Log.i(TAG, line)
        try {
            File(context.filesDir, FILE_NAME).appendText(line + "\n")
        } catch (e: Exception) {
            Log.e(TAG, "KB01 probe file append failed", e)
        }
    }

    private fun rowTriple(s: FocusSessionEntity): String =
        "session=${s.id} startWall=${s.startWallTimeMillis} " +
            "startMono=${s.startElapsedRealtimeMillis} rowBoot=${s.bootCount} " +
            "accumBeforeRebootSec=${s.accumulatedBeforeRebootSeconds} " +
            "targetSec=${s.targetDurationSeconds} penaltySec=${s.penaltySecondsAdded} " +
            "elapsedSec=${s.elapsedSeconds} active=${s.isActive} " +
            "completed=${s.completedSuccessfully}"

    /** Called from the shield loop with the row the tick just persisted. */
    fun onTickRow(context: Context, s: FocusSessionEntity) {
        if (s.isActive) {
            if (lastActiveSessionId != s.id) {
                log(context, "SEAL_OBSERVED", rowTriple(s))
                lastActiveSessionId = s.id
            }
            val remaining = s.targetDurationSeconds + s.penaltySecondsAdded - s.elapsedSeconds
            if (remaining in 0..20) {
                log(context, "TICK_NEAR_END remainingSec=$remaining", rowTriple(s))
            }
            lastWasActive = true
        } else if (lastWasActive && lastActiveSessionId == s.id) {
            log(context, "END_OBSERVED", rowTriple(s))
            lastWasActive = false
            lastActiveSessionId = -1
        }
    }
}
