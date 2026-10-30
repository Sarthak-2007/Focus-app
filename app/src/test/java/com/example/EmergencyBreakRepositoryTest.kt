package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BreakResult
import com.example.data.EmergencyBreakSpendEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.MonolithWeek
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.TimeZone

/**
 * TOKEN-SPEC §8 tests 3 + 4 + the restart property of 7, at the
 * repository — the enforcement layer. Runs against the app's real
 * (device-protected) database file in the Robolectric sandbox, because
 * completeEmergencyBreak() takes its transaction from the singleton,
 * exactly as it does on the phone.
 *
 * The old build failed test 7 by construction: the count lived in an
 * in-memory flow, so process death refilled it. Here remaining is
 * re-derived from the ledger rows by a FRESH repository instance —
 * there is no memory left to lose.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EmergencyBreakRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** Delegating DAO that forces a failure at exactly one write. */
    private class FailingDao(
        private val delegate: MonolithDao,
        private val failOnInsert: Boolean = false,
        private val failOnUpdate: Boolean = false
    ) : MonolithDao by delegate {
        override suspend fun insertEmergencyBreakSpend(spend: EmergencyBreakSpendEntity) {
            if (failOnInsert) throw IllegalStateException("forced insert failure")
            delegate.insertEmergencyBreakSpend(spend)
        }

        override suspend fun updateSession(session: FocusSessionEntity) {
            if (failOnUpdate) throw IllegalStateException("forced disarm failure")
            delegate.updateSession(session)
        }
    }

    private suspend fun insertActiveSession(dao: MonolithDao, profileName: String): Int {
        val id = dao.insertSession(
            FocusSessionEntity(
                intention = "Token test session",
                profileName = profileName,
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        )
        return id.toInt()
    }

    @Test
    fun noActiveSession_returnsNoActiveSession_andWritesNothing() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        assertNull(dao.getActiveSessionSnapshot())
        val spendsBefore = dao.getAllSpendsSnapshot().size
        assertEquals(BreakResult.NoActiveSession, repo.completeEmergencyBreak())
        assertEquals(spendsBefore, dao.getAllSpendsSnapshot().size)
    }

    @Test
    fun spendIsAtomicDerivedAndRefusedAtZero() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // Boundary-proof anchor (#91): production stamps the spend
        // with the anchor of the COMPLETION instant, re-reading the
        // clock inside completeEmergencyBreak(). An anchor captured
        // once here goes stale if Monday 00:00 passes mid-test, so
        // this var is re-derived from the spend row's own instant
        // right after the real completion (see below) and every
        // later assertion reads the week the spend actually booked.
        var anchorNow = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        var nextAnchorNow = MonolithWeek.nextWeekAnchorMillis(System.currentTimeMillis())

        // Fresh week: the full break is available.
        assertEquals(0, dao.getSpendsForWeek(anchorNow, nextAnchorNow).size)
        assertEquals(1, repo.emergencyBreaksRemaining())

        // Test 4a — forced failure at the spend insert: the session
        // stays sealed and the ledger stays empty.
        val sessionId = insertActiveSession(dao, "Manual Focus")
        val insertFailRepo = MonolithRepository(context, FailingDao(dao, failOnInsert = true))
        try {
            insertFailRepo.completeEmergencyBreak()
            fail("expected the forced insert failure to propagate")
        } catch (expected: IllegalStateException) {
            assertEquals("forced insert failure", expected.message)
        }
        assertEquals(0, dao.getSpendsForWeek(anchorNow, nextAnchorNow).size)
        assertNotNull(dao.getActiveSessionSnapshot())

        // Test 4b — forced failure inside the disarm step: the spend
        // insert rolls back with the transaction; no orphan spend row,
        // session still sealed.
        val disarmFailRepo = MonolithRepository(context, FailingDao(dao, failOnUpdate = true))
        try {
            disarmFailRepo.completeEmergencyBreak()
            fail("expected the forced disarm failure to propagate")
        } catch (expected: IllegalStateException) {
            assertEquals("forced disarm failure", expected.message)
        }
        assertEquals(0, dao.getSpendsForWeek(anchorNow, nextAnchorNow).size)
        assertEquals(sessionId, dao.getActiveSessionSnapshot()?.id)

        // The real completion: spend + disarm commit together.
        val beforeSpend = System.currentTimeMillis()
        assertEquals(BreakResult.Completed, repo.completeEmergencyBreak())
        // Re-anchor from the spend's own instant — the single
        // instant production itself stamped — so the assertions
        // below read the week of the spend even when the test
        // straddled Monday 00:00 since anchorNow was captured.
        val spendRow = dao.getAllSpendsSnapshot().single { it.sessionId == sessionId }
        anchorNow = MonolithWeek.weekAnchorMillis(spendRow.spentAtWallMillis)
        nextAnchorNow = MonolithWeek.nextWeekAnchorMillis(spendRow.spentAtWallMillis)
        val spends = dao.getSpendsForWeek(anchorNow, nextAnchorNow)
        assertEquals(1, spends.size)
        val spend = spends.single()
        assertEquals(sessionId, spend.sessionId)
        assertEquals(60, spend.heldSeconds)
        // The stored anchor is the anchor OF THE SPEND INSTANT (§4.2) —
        // stored at spend time, never recomputed.
        assertEquals(MonolithWeek.weekAnchorMillis(spend.spentAtWallMillis), spend.weekAnchorMillis)
        assertTrue(spend.spentAtWallMillis >= beforeSpend)
        // Session ended, booked as a break — not a completion — under
        // the exact outcome-reason constant, in its intercept record.
        assertNull(dao.getActiveSessionSnapshot())
        val broken = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertEquals(false, broken.completedSuccessfully)
        val breakLog = dao.getInterceptLogsFlow().first()
            .firstOrNull { it.outcome == MonolithRepository.EMERGENCY_BREAK_REASON }
        assertNotNull("the break's record renders from the spend path, reason constant exact", breakLog)

        // Test 7's property — a fresh repository (the "relaunch") still
        // derives 0: the truth is the row, not any process memory.
        val relaunchedRepo = MonolithRepository(context, dao)
        assertEquals(0, relaunchedRepo.emergencyBreaksRemaining())
        assertEquals(0, relaunchedRepo.observeEmergencyBreaksRemaining().first())

        // Test 3 — at zero, a second completion is refused: the new
        // session stays sealed and no second row is written.
        val secondId = insertActiveSession(dao, "Manual Focus")
        assertEquals(BreakResult.RefusedNoToken, relaunchedRepo.completeEmergencyBreak())
        assertEquals(secondId, dao.getActiveSessionSnapshot()?.id)
        assertEquals(1, dao.getSpendsForWeek(anchorNow, nextAnchorNow).size)

        // §4.4 — the grace cancel never passes through the ledger: it
        // ends its session booking zero and writes no spend.
        relaunchedRepo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )
        assertNull(dao.getActiveSessionSnapshot())
        assertEquals(1, dao.getSpendsForWeek(anchorNow, nextAnchorNow).size)
        val grace = dao.getSessionHistorySnapshot().first { it.id == secondId }
        assertEquals(0, grace.elapsedSeconds)
    }

    // -----------------------------------------------------------------
    // Issue #39 F1 — the timezone hole, end to end. A break spent
    // under one default zone must STILL count after the device zone
    // changes: remaining stays 0 and a second completion is refused.
    // Under the old anchor-equality matching every one of these read
    // remaining == 1 after the switch and minted a second break.
    // -----------------------------------------------------------------

    @Test
    fun timezoneChange_istToNewYork_capStaysSpent() = runBlocking {
        assertCapSurvivesZoneChange("Asia/Kolkata", "America/New_York")
    }

    @Test
    fun timezoneChange_newYorkToIst_capStaysSpent() = runBlocking {
        assertCapSurvivesZoneChange("America/New_York", "Asia/Kolkata")
    }

    @Test
    fun timezoneChange_plus14ToMinus12_capStaysSpent() = runBlocking {
        // The widest real pair: +14 (Kiritimati) to −12 (Etc/GMT+12) —
        // the same Monday sits the ruled-maximum 26h apart.
        assertCapSurvivesZoneChange("Pacific/Kiritimati", "Etc/GMT+12")
    }

    private suspend fun assertCapSurvivesZoneChange(fromZone: String, toZone: String) {
        val originalZone = TimeZone.getDefault()
        val database = MonolithDatabase.getInstance(context)
        val dao = database.monolithDao()
        val repo = MonolithRepository(context, dao)
        val createdSessionIds = mutableListOf<Int>()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(fromZone))
            // Deterministic reset (the issue #66 pattern): no row
            // that would count against THIS week may pre-exist —
            // precisely the rows the new matching would count.
            val now = System.currentTimeMillis()
            val anchor = MonolithWeek.weekAnchorMillis(now)
            val nextAnchor = MonolithWeek.nextWeekAnchorMillis(now)
            val tol = MonolithWeek.WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS
            database.openHelper.writableDatabase.execSQL(
                "DELETE FROM emergency_break_spends WHERE " +
                    "(weekAnchorMillis BETWEEN ${anchor - tol} AND ${anchor + tol}) " +
                    "OR (spentAtWallMillis >= $anchor AND spentAtWallMillis < $nextAnchor)"
            )
            assertEquals(1, repo.emergencyBreaksRemaining())

            // Spend the week's one break under the FROM zone.
            val firstId = insertActiveSession(dao, "Manual Focus")
            createdSessionIds += firstId
            assertEquals(BreakResult.Completed, repo.completeEmergencyBreak())
            assertEquals(0, repo.emergencyBreaksRemaining())

            // The phone lands in the TO zone: the stored spend was
            // minted under a foreign Monday, yet the cap must hold —
            // on the one-shot read, on the live flow, and at the
            // enforcement point itself.
            TimeZone.setDefault(TimeZone.getTimeZone(toZone))
            assertEquals(
                "weekly cap must survive the zone change $fromZone -> $toZone",
                0, repo.emergencyBreaksRemaining()
            )
            assertEquals(0, repo.observeEmergencyBreaksRemaining().first())
            val secondId = insertActiveSession(dao, "Manual Focus")
            createdSessionIds += secondId
            assertEquals(BreakResult.RefusedNoToken, repo.completeEmergencyBreak())
            assertEquals(secondId, dao.getActiveSessionSnapshot()?.id)
            // Exactly one spend row was ever written by this test.
            assertEquals(1, dao.getAllSpendsSnapshot().count { it.sessionId in createdSessionIds })
        } finally {
            TimeZone.setDefault(originalZone)
            // Leave the shared ledger and session state exactly as
            // found: this test's spend rows are deleted by session
            // id and its refused session is grace-cancelled, so no
            // foreign-zone row can leak into another test's week.
            if (createdSessionIds.isNotEmpty()) {
                val ids = createdSessionIds.joinToString(",")
                database.openHelper.writableDatabase.execSQL(
                    "DELETE FROM emergency_break_spends WHERE sessionId IN ($ids)"
                )
            }
            val activeId = dao.getActiveSessionSnapshot()?.id
            if (activeId != null && activeId in createdSessionIds) {
                repo.disarmActiveSession(
                    completed = false,
                    outcomeReason = "Accidental Start Grace Cancel"
                )
            }
        }
    }
}
