package com.example

import com.example.data.FocusSessionEntity
import com.example.data.HistoryCsv
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PIN: PLUMBING — the HistoryCsv document contract (issue #253
 * line 39): the pinned column set, RFC 4180 escaping, ISO-8601
 * UTC rendering beside the raw millis, null routine fields as
 * empty columns, and the caller's row order preserved exactly.
 * The formatter is pure, so every assertion here is on the exact
 * bytes of the exported document.
 */
class HistoryCsvTest {

    private fun session(
        id: Int = 7,
        intention: String = "Physics",
        profileName: String = "Default",
        startWallTimeMillis: Long = 0L,
        routineId: Int? = null,
        routineOutcome: String? = null,
        completedTasksSummary: String = ""
    ) = FocusSessionEntity(
        id = id,
        intention = intention,
        profileName = profileName,
        targetDurationSeconds = 3600,
        elapsedSeconds = 3500,
        startWallTimeMillis = startWallTimeMillis,
        startElapsedRealtimeMillis = 0L,
        isActive = false,
        completedSuccessfully = true,
        routineId = routineId,
        routineOutcome = routineOutcome,
        completedTasksSummary = completedTasksSummary
    )

    @Test
    fun headerIsThePinnedColumnSet() {
        assertEquals(
            "id,start_iso_utc,start_wall_time_millis,intention,profile_name," +
                "routine_id,routine_outcome,target_seconds,elapsed_seconds," +
                "break_taken_seconds,call_excluded_seconds,penalty_seconds_added," +
                "stopwatch,total_study_blocks,tasks_completed,tasks_summary," +
                "completed,is_active,phantom_reaches_blocked,tamper_detected," +
                "held_apps_at_seal,held_sites_at_seal,held_shut_at_seal,held_peek_at_seal",
            HistoryCsv.HEADER
        )
    }

    @Test
    fun emptyHistoryIsTheHeaderAlone() {
        assertEquals(HistoryCsv.HEADER + "\r\n", HistoryCsv.format(emptyList()))
    }

    @Test
    fun oneRowRendersEveryColumnInOrder() {
        val expected = HistoryCsv.HEADER + "\r\n" +
            "7,1970-01-01T00:00:00Z,0,Physics,Default,,,3600,3500,0,0,0," +
            "false,1,0,,true,false,0,false,-1,-1,-1,-1" + "\r\n"
        assertEquals(expected, HistoryCsv.format(listOf(session())))
    }

    @Test
    fun startIsoIsTheSameInstantAsTheRawMillis() {
        // 1_700_000_000_000 ms == 2023-11-14T22:13:20Z.
        val doc = HistoryCsv.format(
            listOf(session(startWallTimeMillis = 1_700_000_000_000L))
        )
        assertTrue(doc.contains(",2023-11-14T22:13:20Z,1700000000000,"))
    }

    @Test
    fun fieldsWithCommaQuoteOrNewlineAreQuotedAndDoubled() {
        val doc = HistoryCsv.format(
            listOf(
                session(
                    intention = "Read, \"deeply\"\nnow",
                    completedTasksSummary = "ch 1, ch 2"
                )
            )
        )
        assertTrue(doc.contains(",\"Read, \"\"deeply\"\"\nnow\","))
        assertTrue(doc.contains(",\"ch 1, ch 2\","))
    }

    @Test
    fun routineFieldsRenderWhenPresent() {
        val doc = HistoryCsv.format(
            listOf(session(routineId = 3, routineOutcome = "ATTENDED"))
        )
        assertTrue(doc.contains(",Default,3,ATTENDED,3600,"))
    }

    @Test
    fun rowOrderIsPreservedAsGiven() {
        val doc = HistoryCsv.format(
            listOf(session(id = 9), session(id = 4), session(id = 12))
        )
        val rows = doc.trimEnd('\r', '\n').split("\r\n")
        assertEquals(4, rows.size)
        assertTrue(rows[1].startsWith("9,"))
        assertTrue(rows[2].startsWith("4,"))
        assertTrue(rows[3].startsWith("12,"))
    }
}
