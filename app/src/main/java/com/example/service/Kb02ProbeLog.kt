package com.example.service

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import com.example.data.FocusSessionEntity
import java.io.File
import java.security.MessageDigest

/**
 * KB02 SPIKE INSTRUMENTATION ONLY — Stage 4P, Kill Book Row 2
 * (sealed persistence across reboot/kill, MS-SPINE-03 /
 * SPINE-HOURS-01, Arms A/B). Pit spike, never product.
 *
 * This logger changes NOTHING about the as-built persistence and
 * restoration chain (the spike prices the chain that exists, not
 * the chain that should). It only records, per event, the sealed
 * state captured whole — the fields the claim is judged on:
 * locked set (profile + held counts at seal), lifting hour
 * (derived: startWall + target + penalty), remaining allowance
 * (target + penalty - elapsed at capture), and the session row's
 * own anchors — plus process/boot markers so the interval between
 * an event (kill / reboot) and the restored face can be measured
 * from the raw log alone, never recalled.
 *
 * The face itself (its four printings, re-shown vs recomposed) is
 * an operator capture: the frozen script photographs the face at
 * every capture point and the photographs' hashes are entered in
 * the run log header; this logger's row snapshot is the state
 * half of that comparison, never a substitute for the photograph.
 *
 * Output: logcat tag KB02PROBE + append to filesDir/kb02-probe.log
 * (the file survives process kill and reboot; it is pulled as the
 * run's raw log under the Run ID).
 */
object Kb02ProbeLog {

    private const val TAG = "KB02PROBE"
    private const val FILE_NAME = "kb02-probe.log"

    @Volatile private var lastSnapshot: String = ""
    @Volatile private var firstTickLogged: Boolean = false

    private fun bootCountNow(context: Context): Int = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (e: Exception) {
        -1
    }

    @Synchronized
    fun log(context: Context, event: String, detail: String) {
        val line = "KB02 $event wall=${System.currentTimeMillis()} " +
            "mono=${SystemClock.elapsedRealtime()} boot=${bootCountNow(context)} $detail"
        Log.i(TAG, line)
        try {
            File(context.filesDir, FILE_NAME).appendText(line + "\n")
        } catch (e: Exception) {
            Log.e(TAG, "KB02 probe file append failed", e)
        }
    }

    private fun hmacFingerprint(s: FocusSessionEntity): String = try {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(s.hmacSignature.toByteArray(Charsets.UTF_8))
        digest.joinToString("") { "%02x".format(it) }.take(16)
    } catch (e: Exception) {
        "unavailable"
    }

    /** The sealed state, captured whole — every field the claim names. */
    fun snapshot(s: FocusSessionEntity): String =
        "session=${s.id} profile=${s.profileName} " +
            "targetSec=${s.targetDurationSeconds} penaltySec=${s.penaltySecondsAdded} " +
            "elapsedSec=${s.elapsedSeconds} " +
            "remainingSec=${s.targetDurationSeconds + s.penaltySecondsAdded - s.elapsedSeconds} " +
            "startWall=${s.startWallTimeMillis} startMono=${s.startElapsedRealtimeMillis} " +
            "rowBoot=${s.bootCount} accumBeforeRebootSec=${s.accumulatedBeforeRebootSeconds} " +
            "heldAppsAtSeal=${s.heldAppsAtSeal} heldShutAtSeal=${s.heldShutAtSeal} " +
            "heldPeekAtSeal=${s.heldPeekAtSeal} heldSitesAtSeal=${s.heldSitesAtSeal} " +
            "breakTakenSec=${s.breakTakenSeconds} callExcludedSec=${s.callExcludedSeconds} " +
            "phantomReaches=${s.phantomReachesBlocked} tamper=${s.tamperDetected} " +
            "active=${s.isActive} completed=${s.completedSuccessfully} " +
            "shieldLastBeatWall=${s.shieldLastBeatWallMillis} shieldGapSeen=${s.shieldGapSeen} " +
            "rebootGapSec=${s.rebootGapSeconds} hmacFp=${hmacFingerprint(s)}"

    /** Called from the shield loop with the row the tick just persisted. */
    fun onTickRow(context: Context, s: FocusSessionEntity) {
        if (!firstTickLogged) {
            firstTickLogged = true
            // mono at this instant is time-since-boot: on the reboot
            // arm it is the boot-to-first-tick measure; on the kill
            // arm it bounds the kill-to-restart span together with
            // the operator's wall-clock attempt log.
            log(context, "PROCESS_FIRST_TICK", snapshot(s))
        }
        val snap = snapshot(s)
        if (snap != lastSnapshot) {
            log(context, "STATE_SNAPSHOT", snap)
            lastSnapshot = snap
        }
    }
}
