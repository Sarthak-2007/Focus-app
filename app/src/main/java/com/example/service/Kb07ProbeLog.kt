package com.example.service

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import com.example.data.FocusSessionEntity
import java.io.File
import java.security.MessageDigest

/**
 * KB07 SPIKE INSTRUMENTATION ONLY — Stage 4P, Kill Book Row 7
 * (record persistence — REC-1 THE STRIKING · REC-2 THE PLATE STACK).
 * Pit spike, never product.
 *
 * Branch fact this spike prices (read firsthand at the Floor head
 * 03c830fb…, GAP-15): there is NO struck-plate artefact entity in
 * this build. The "plate" is the completed focus_sessions row —
 * a mutable Room row (@Update paths and a DELETE-by-id exist in
 * MonolithDao) carrying receiptNumber + a finishSignature /
 * prevFinishSignature chain — re-derived at read time from its
 * ingredient fields. This logger changes NOTHING about striking,
 * storage, or reading. It only records, at the active→inactive
 * transition (the strike as built), the row's full ingredient
 * canon plus a SHA-256 over that canon, so every later re-read in
 * the frozen script can be diffed field-by-field and hash-by-hash
 * against the strike instant:
 *
 *   any field diff, any hash diff, any second strike line for the
 *   same session id, or a missing strike line = the run's finding.
 *
 * Vocabulary law (REFILE-2, binding on this row): the canon carries
 * reaches (phantomReachesBlocked as built) — a peek/taking is drum
 * spending and counts NO hole; held-call holes are counted from the
 * day's held-call record by the operator's event log, never from a
 * merged "peek + held call" count.
 *
 * Output: logcat tag KB07PROBE + append to filesDir/kb07-probe.log
 * (survives process kill and reboot; pulled as the run's raw log).
 */
object Kb07ProbeLog {

    private const val TAG = "KB07PROBE"
    private const val FILE_NAME = "kb07-probe.log"

    @Volatile private var lastActiveSessionId: Int = -1
    @Volatile private var lastWasActive: Boolean = false

    private fun bootCountNow(context: Context): Int = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (e: Exception) {
        -1
    }

    /** The plate's ingredient canon, in fixed field order. */
    fun canon(s: FocusSessionEntity): String =
        "session=${s.id} intention=${s.intention} profile=${s.profileName} " +
            "targetSec=${s.targetDurationSeconds} elapsedSec=${s.elapsedSeconds} " +
            "startWall=${s.startWallTimeMillis} penaltySec=${s.penaltySecondsAdded} " +
            "tamper=${s.tamperDetected} reachesBlocked=${s.phantomReachesBlocked} " +
            "completed=${s.completedSuccessfully} receiptNumber=${s.receiptNumber} " +
            "finishSig=${s.finishSignature} prevFinishSig=${s.prevFinishSignature} " +
            "tasksSummary=${s.completedTasksSummary} tasksCount=${s.tasksCompletedCount} " +
            "routineOutcome=${s.routineOutcome} breakTakenSec=${s.breakTakenSeconds} " +
            "callExcludedSec=${s.callExcludedSeconds} " +
            "heldAppsAtSeal=${s.heldAppsAtSeal} heldSitesAtSeal=${s.heldSitesAtSeal}"

    fun canonHash(s: FocusSessionEntity): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(canon(s).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    @Synchronized
    fun log(context: Context, event: String, detail: String) {
        val line = "KB07 $event wall=${System.currentTimeMillis()} " +
            "mono=${SystemClock.elapsedRealtime()} boot=${bootCountNow(context)} $detail"
        Log.i(TAG, line)
        try {
            File(context.filesDir, FILE_NAME).appendText(line + "\n")
        } catch (e: Exception) {
            Log.e(TAG, "KB07 probe file append failed", e)
        }
    }

    /** Called from the shield loop with the row the tick just persisted. */
    fun onTickRow(context: Context, s: FocusSessionEntity) {
        if (s.isActive) {
            if (lastActiveSessionId != s.id) {
                log(context, "SEAL_OBSERVED", canon(s) + " hash=" + canonHash(s))
                lastActiveSessionId = s.id
            }
            lastWasActive = true
        } else if (lastWasActive && lastActiveSessionId == s.id) {
            // The strike as built: the session row stands completed.
            log(context, "STRIKE_OBSERVED", canon(s) + " hash=" + canonHash(s))
            lastWasActive = false
            lastActiveSessionId = -1
        }
    }
}
