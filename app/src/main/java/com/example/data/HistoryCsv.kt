package com.example.data

import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Issue #253, line 39: the session history as a portable CSV
 * document — the first external report the app has ever made.
 *
 * PURE: a formatter over stored [FocusSessionEntity] rows — no
 * Android, no I/O, no clock. The same rows in, the same document
 * out, byte for byte. The caller (MonolithRepository's export
 * verb) supplies the rows in the DAO's canonical history order
 * (newest first); the formatter preserves that order and never
 * re-sorts, filters or drops a row — an export that quietly
 * edited the history would be a lie with a .csv extension.
 *
 * Columns are the history facts a reader outside the app can act
 * on: when the session ran, what it declared, what it booked.
 * Deliberately NOT columns: the reboot/monotonic anchors and the
 * HMAC signature (integrity machinery, meaningless off-device),
 * the routine window snapshot and evidence mask (the attendance
 * verdict rides as routine_outcome), the live break state
 * (transient by definition) and the release-fault stamps (a
 * machinery post-mortem, not history). A -1 seal count prints as
 * stored: the row says uncaptured, the export does not invent.
 *
 * Format: RFC 4180 — CRLF record separators; a field is quoted
 * when it carries a comma, a quote or a line break, and quotes
 * double inside quoted fields. start_iso_utc is the same instant
 * as start_wall_time_millis rendered ISO-8601 in UTC; the raw
 * millis ride beside it so nothing depends on a parser's clock.
 */
object HistoryCsv {

    /** The document picker's suggested file name. */
    const val FILE_NAME = "monolith-history.csv"

    val HEADER: String = listOf(
        "id",
        "start_iso_utc",
        "start_wall_time_millis",
        "intention",
        "profile_name",
        "routine_id",
        "routine_outcome",
        "target_seconds",
        "elapsed_seconds",
        "break_taken_seconds",
        "call_excluded_seconds",
        "penalty_seconds_added",
        "stopwatch",
        "total_study_blocks",
        "tasks_completed",
        "tasks_summary",
        "completed",
        "is_active",
        "phantom_reaches_blocked",
        "tamper_detected",
        "held_apps_at_seal",
        "held_sites_at_seal",
        "held_shut_at_seal",
        "held_peek_at_seal"
    ).joinToString(",")

    fun format(sessions: List<FocusSessionEntity>): String {
        val out = StringBuilder()
        out.append(HEADER).append("\r\n")
        for (session in sessions) {
            out.append(row(session)).append("\r\n")
        }
        return out.toString()
    }

    private fun row(s: FocusSessionEntity): String = listOf(
        s.id.toString(),
        DateTimeFormatter.ISO_INSTANT.format(
            Instant.ofEpochMilli(s.startWallTimeMillis)
        ),
        s.startWallTimeMillis.toString(),
        s.intention,
        s.profileName,
        s.routineId?.toString() ?: "",
        s.routineOutcome ?: "",
        s.targetDurationSeconds.toString(),
        s.elapsedSeconds.toString(),
        s.breakTakenSeconds.toString(),
        s.callExcludedSeconds.toString(),
        s.penaltySecondsAdded.toString(),
        s.isStopwatchMode.toString(),
        s.totalStudyBlocks.toString(),
        s.tasksCompletedCount.toString(),
        s.completedTasksSummary,
        s.completedSuccessfully.toString(),
        s.isActive.toString(),
        s.phantomReachesBlocked.toString(),
        s.tamperDetected.toString(),
        s.heldAppsAtSeal.toString(),
        s.heldSitesAtSeal.toString(),
        s.heldShutAtSeal.toString(),
        s.heldPeekAtSeal.toString()
    ).joinToString(",") { escape(it) }

    private fun escape(field: String): String =
        if (field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
}
