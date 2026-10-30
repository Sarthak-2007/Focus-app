package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BreakResult
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.MonolithWeek
import com.example.data.SessionOutcome
import com.example.ui.stopwatchRemainingFloorSeconds
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #147 REOPENED (Findings 2/3 seam): the valve's booking must
 * re-judge the stopwatch floor at COMPLETION time, not trust the
 * snapshot the overlay opened on.
 *
 * The failure case these tests pin: the overlay opens at ~19:30
 * banked elapsed — below the 20-minute floor, token armed, overlay
 * text promising "N more minutes" — and the floor crosses while the
 * overlay is open (the 60-second hold itself, or a wait inside the
 * overlay). Before the cure, completing the hold spent the week's
 * only token and booked VALVE_BROKE for a stop the app itself
 * prints as free. After it, the completion is re-judged against
 * lawElapsed = max(verified, banked) — the same figure the disarm
 * door books by — inside the spend transaction.
 *
 * Runs against the singleton database file, exactly as the token
 * suite does, because completeEmergencyBreak takes its transaction
 * from the singleton. Isolation follows the issue-#66 pattern: the
 * week's spend ledger is reset deterministically at each test's
 * start and asserted empty, and each test deletes exactly what it
 * created on the way out.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ValveFloorRecheckTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private suspend fun resetWeekLedger(db: MonolithDatabase, anchor: Long) {
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends WHERE weekAnchorMillis = $anchor"
        )
        assertEquals(0, db.monolithDao().getSpendsForWeek(anchor).size)
    }

    private fun cleanup(db: MonolithDatabase, anchor: Long, sessionId: Int) {
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends WHERE weekAnchorMillis = $anchor"
        )
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM focus_sessions WHERE id = $sessionId"
        )
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM intercept_logs WHERE packageName = 'com.monolith.system'"
        )
    }

    /**
     * A stopwatch session whose BANKED elapsed (the overlay's
     * snapshot figure) sits at 19:30 — below the floor — while its
     * VERIFIED elapsed has already crossed it: [verifiedSeconds] is
     * carried in accumulatedBeforeRebootSeconds, the segment the
     * verified-elapsed computation credits before the monotonic
     * anchor's own span, so lawElapsed = max(verified, banked)
     * lands past the floor exactly as it does on the phone when the
     * hold itself carries the session across.
     */
    private suspend fun insertStopwatchSession(
        db: MonolithDatabase,
        verifiedSeconds: Int
    ): Int {
        val now = System.currentTimeMillis()
        return db.monolithDao().insertSession(
            FocusSessionEntity(
                intention = "Valve floor test session",
                profileName = "FLOW",
                targetDurationSeconds = 0,
                elapsedSeconds = 19 * 60 + 30,
                accumulatedBeforeRebootSeconds = verifiedSeconds,
                startWallTimeMillis = now,
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false,
                isStopwatchMode = true
            )
        ).toInt()
    }

    @Test
    fun valveCompletionAfterFloorCrossing_endsCleanly_andSpendsNothing() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, dao)
        val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        resetWeekLedger(db, anchor)

        // Banked 19:30 (below floor at overlay open), verified 21:00
        // (floor crossed by completion): the hold must NOT charge.
        val sessionId = insertStopwatchSession(db, verifiedSeconds = 21 * 60)
        try {
            val result = repo.completeEmergencyBreak()

            assertEquals(BreakResult.CompletedCleanlyAtFloor, result)
            // No spend row, token still whole.
            assertEquals(0, dao.getSpendsForWeek(anchor).size)
            assertEquals(1, repo.emergencyBreaksRemaining())
            // Session ended CLEANLY through the stopwatch door:
            // completed, STOPWATCH_ENDED, booked at the law figure —
            // never VALVE_BROKE.
            assertNull(dao.getActiveSessionSnapshot())
            val ended = dao.getSessionHistorySnapshot().first { it.id == sessionId }
            assertFalse(ended.isActive)
            assertTrue(ended.completedSuccessfully)
            assertEquals(SessionOutcome.STOPWATCH_ENDED, ended.outcome)
            assertTrue(ended.elapsedSeconds >= MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS)
        } finally {
            cleanup(db, anchor, sessionId)
        }
    }

    @Test
    fun valveCompletionBeforeFloor_spendsTheToken_asBefore() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, dao)
        val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        resetWeekLedger(db, anchor)

        // Banked 19:30 and verified ~0: lawElapsed is the banked
        // 19:30, still under the floor — the break is a real break
        // and the token law is untouched.
        val sessionId = insertStopwatchSession(db, verifiedSeconds = 0)
        try {
            val result = repo.completeEmergencyBreak()

            assertEquals(BreakResult.Completed, result)
            val spends = dao.getSpendsForWeek(anchor)
            assertEquals(1, spends.size)
            assertEquals(sessionId, spends.single().sessionId)
            assertEquals(0, repo.emergencyBreaksRemaining())
            assertNull(dao.getActiveSessionSnapshot())
            val broken = dao.getSessionHistorySnapshot().first { it.id == sessionId }
            assertFalse(broken.isActive)
            assertFalse(broken.completedSuccessfully)
            assertEquals(SessionOutcome.VALVE_BROKE, broken.outcome)
        } finally {
            cleanup(db, anchor, sessionId)
        }
    }
}

/**
 * The overlay print's half of the seam: the remaining-floor figure
 * every stopwatch print derives from. At the floor the figure is
 * zero — and the overlay renders its wait-to-free line only above
 * zero — so a floor crossed while the overlay sits open retires the
 * line instead of leaving "N more minutes" standing over a free
 * stop. Past the floor it stays zero, never negative.
 */
class StopwatchRemainingFloorSecondsTest {

    @Test
    fun remainingFloor_countsDownToTheFloor_andStopsAtZero() {
        val floor = MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS
        assertEquals(floor, stopwatchRemainingFloorSeconds(0))
        assertEquals(30, stopwatchRemainingFloorSeconds(19 * 60 + 30))
        assertEquals(1, stopwatchRemainingFloorSeconds(floor - 1))
        assertEquals(0, stopwatchRemainingFloorSeconds(floor))
        assertEquals(0, stopwatchRemainingFloorSeconds(floor + 300))
    }
}
