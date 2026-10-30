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
 * from the singleton. Isolation follows the issue-#66 pattern with
 * the #91 correction: the week's ledger is reset deterministically
 * at each test's start — keyed to the PRODUCTION clock
 * (honestNowMillis, the instant completeEmergencyBreak itself
 * anchors on), never to a raw wall-clock anchor captured in the
 * test, because the suite's shared clock-honesty anchor can make
 * the two name different weeks — and each test deletes exactly
 * what it created on the way out. Preconditions are asserted by
 * name so a hostile suite state fails HERE, at its own line, not
 * downstream disguised as a cure failure.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ValveFloorRecheckTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** The week the production code will anchor on RIGHT NOW. */
    private fun productionAnchor(repo: MonolithRepository): Long =
        MonolithWeek.weekAnchorMillis(repo.honestNowMillis())

    private suspend fun resetWeekLedger(db: MonolithDatabase, anchor: Long) {
        // Cycle-3 hardening (finisher): the reset is TOTAL, not
        // week-scoped. Cycles 1–2 failed in-suite with the honest
        // week and the wall week naming different ledgers (the #91
        // hazard in both directions): a week-scoped delete leaves
        // other weeks' rows — booked by earlier classes in this
        // shared singleton — standing where a later read can see
        // them. This suite owns the ledger while it runs: every
        // spend row goes, and both candidate weeks must read empty
        // before a seed is planted.
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends"
        )
        assertEquals(
            "precondition: production week ledger empty after reset",
            0, db.monolithDao().getSpendsForWeek(anchor).size
        )
        assertEquals(
            "precondition: wall week ledger empty after reset",
            0,
            db.monolithDao()
                .getSpendsForWeek(MonolithWeek.weekAnchorMillis(System.currentTimeMillis()))
                .size
        )
        assertEquals(
            "precondition: spend ledger fully empty after reset",
            0, db.monolithDao().getAllSpendsSnapshot().size
        )
    }

    private fun cleanup(db: MonolithDatabase, sessionId: Int) {
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends WHERE sessionId = $sessionId"
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
     * VERIFIED elapsed is [verifiedSeconds]: the verified figure is
     * carried in accumulatedBeforeRebootSeconds, the segment the
     * verified-elapsed computation credits before the monotonic
     * anchor's own span, so lawElapsed = max(verified, banked) is
     * exactly [verifiedSeconds] (plus a sub-second tick), as it is
     * on the phone when the hold itself carries the session across.
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

    /** The seeded row must be the active session production will
     *  judge, with the verified figure the test claims it carries. */
    private suspend fun assertSeeded(
        db: MonolithDatabase,
        repo: MonolithRepository,
        sessionId: Int,
        verifiedSeconds: Int
    ) {
        val active = db.monolithDao().getActiveSessionSnapshot()
        assertEquals(
            "precondition: seeded session is the active one",
            sessionId, active?.id
        )
        assertTrue("precondition: seeded session is stopwatch", active!!.isStopwatchMode)
        val (verified, _) = repo.computeVerifiedSessionElapsed(active)
        assertTrue(
            "precondition: verified elapsed $verified should be within a tick of $verifiedSeconds",
            verified >= verifiedSeconds && verified <= verifiedSeconds + 5
        )
    }

    @Test
    fun valveCompletionAfterFloorCrossing_endsCleanly_andSpendsNothing() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, dao)
        resetWeekLedger(db, productionAnchor(repo))

        // Banked 19:30 (below floor at overlay open), verified 21:00
        // (floor crossed by completion): the hold must NOT charge.
        val sessionId = insertStopwatchSession(db, verifiedSeconds = 21 * 60)
        try {
            assertSeeded(db, repo, sessionId, verifiedSeconds = 21 * 60)

            val result = repo.completeEmergencyBreak()

            assertEquals(
                "valve completion past the floor must end cleanly, not break",
                BreakResult.CompletedCleanlyAtFloor, result
            )
            // No spend row anywhere names this session; token whole.
            assertEquals(
                "no spend may be written for a floor-met stop",
                0, dao.getAllSpendsSnapshot().count { it.sessionId == sessionId }
            )
            assertEquals(
                "the week's token must still be whole (read in the honest week production books in)",
                1, repo.emergencyBreaksRemaining(repo.honestNowMillis())
            )
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
            cleanup(db, sessionId)
        }
    }

    @Test
    fun valveCompletionBeforeFloor_spendsTheToken_asBefore() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repo = MonolithRepository(context, dao)
        resetWeekLedger(db, productionAnchor(repo))

        // Banked 19:30 and verified ~0: lawElapsed is the banked
        // 19:30, still under the floor — the break is a real break
        // and the token law is untouched.
        val sessionId = insertStopwatchSession(db, verifiedSeconds = 0)
        try {
            assertSeeded(db, repo, sessionId, verifiedSeconds = 0)

            val result = repo.completeEmergencyBreak()

            assertEquals(
                "valve completion below the floor must remain a paid break",
                BreakResult.Completed, result
            )
            val mySpends = dao.getAllSpendsSnapshot().filter { it.sessionId == sessionId }
            assertEquals("exactly one spend, naming this session", 1, mySpends.size)
            assertEquals(
                "the week's token must be spent (read in the honest week production books in)",
                0, repo.emergencyBreaksRemaining(repo.honestNowMillis())
            )
            assertNull(dao.getActiveSessionSnapshot())
            val broken = dao.getSessionHistorySnapshot().first { it.id == sessionId }
            assertFalse(broken.isActive)
            assertFalse(broken.completedSuccessfully)
            assertEquals(SessionOutcome.VALVE_BROKE, broken.outcome)
        } finally {
            cleanup(db, sessionId)
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
