package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.BreakResult
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.MonolithWeek
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * TOKEN-SPEC §8 item 9 (B3) at the routines seam: a completed valve
 * hold on a ROUTINE session spends the week's one break token AND
 * finalises the session through the §5.4 funnel, in ONE transaction.
 * The spend row names the session; the session's booking is the
 * funnel's evidence-derived verdict — never its tick-accrued elapsed,
 * never the generic disarm's wall-clock max, and no Partial Stamp is
 * written for a routine session.
 *
 * Runs against the singleton database file, exactly as the token
 * suite does, because completeEmergencyBreak takes its transaction
 * from the singleton. This class completes exactly ONE successful
 * break; what it creates, it deletes again on the way out.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TokenBreakRoutineSeamTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun routineSession_breakSpendsToken_andFinalisesThroughTheFunnel() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        // The singleton file is shared across test classes in a run,
        // and the token suite leaves its own spend behind: this class
        // takes the week ledger clean, and leaves it clean.
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends WHERE weekAnchorMillis = $anchor"
        )
        assertEquals(0, dao.getSpendsForWeek(anchor).size)
        // Same hygiene for the Partial Stamp count below: the
        // generic disarm path — exercised by the token suite on
        // this same shared file — writes 'com.monolith.system'
        // intercept logs, and class order inside a run is not a
        // law (the amended run's order exposed exactly this: a
        // leftover stamp from EmergencyBreakRepositoryTest read
        // as this test's own). Take the stamp ledger clean too,
        // so the closing count measures only this test's break.
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM intercept_logs WHERE packageName = 'com.monolith.system'"
        )

        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
                    daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name,
                    enabled = true
                )
            )
        )
        val routineId = dao.getAllRoutinesSnapshot().single { it.name == "DAY BLOCKS" }.id

        val now = System.currentTimeMillis()
        val hour = 3_600_000L
        val sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "DAY BLOCKS",
                profileName = "Routine: DAY BLOCKS",
                targetDurationSeconds = 3600,
                // Tick-accrued junk the funnel must never inherit.
                elapsedSeconds = 5_000,
                startWallTimeMillis = now - 2 * hour,
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false,
                routineId = routineId,
                windowStartMillis = now - 2 * hour,
                windowEndMillis = now - hour
            )
        ).toInt()

        val result = repository.completeEmergencyBreak()
        assertEquals(BreakResult.Completed, result)

        // The spend committed, naming this session.
        val spends = dao.getSpendsForWeek(anchor)
        assertEquals(1, spends.size)
        assertEquals(sessionId, spends.single().sessionId)

        // The session sealed through the funnel: no evidence inside
        // the window → MISSED at 0 booked seconds, no success, and no
        // "partial_stamp" artifact (the generic path never ran).
        val sealed = dao.getSessionHistorySnapshot().single { it.id == sessionId }
        assertFalse(sealed.isActive)
        assertEquals("MISSED", sealed.routineOutcome)
        assertEquals(0, sealed.elapsedSeconds)
        assertFalse(sealed.completedSuccessfully)
        assertEquals(null, sealed.forgedArtifactId)

        // No Partial Stamp intercept log was written for the routine
        // session (the generic disarm writes one; the funnel doesn't).
        db.openHelper.writableDatabase.query(
            "SELECT COUNT(*) FROM intercept_logs WHERE packageName = 'com.monolith.system'"
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(0, cursor.getInt(0))
        }

        // Hygiene: remove exactly what this test created.
        db.openHelper.writableDatabase.execSQL(
            "DELETE FROM emergency_break_spends WHERE sessionId = $sessionId"
        )
        db.openHelper.writableDatabase.execSQL("DELETE FROM focus_sessions WHERE id = $sessionId")
        db.openHelper.writableDatabase.execSQL("DELETE FROM routines WHERE id = $routineId")
        assertEquals(0, dao.getSpendsForWeek(anchor).size)
    }

    @Test
    fun legacyPrefixSession_breakKeepsInterimBooking_neverWallClockMax() = runBlocking {
        // A3 legacy row: routineId null, recognised only by the prefix.
        // The break must book its tick-accrued elapsed ONLY (the
        // interim body) — the spend still commits atomically with it.
        // NOTE: this test is Refused-safe — if the class's one spend
        // already happened, the assertion set degrades to the refusal
        // contract instead of double-spending.
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        val spendsBefore = dao.getSpendsForWeek(anchor).size

        val now = System.currentTimeMillis()
        val sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "Old Routine",
                profileName = "Routine: Old",
                targetDurationSeconds = 3600,
                elapsedSeconds = 700,
                startWallTimeMillis = now - 3_600_000L,
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        ).toInt()

        val result = repository.completeEmergencyBreak()
        if (spendsBefore == 0) {
            assertEquals(BreakResult.Completed, result)
            val sealed = dao.getSessionHistorySnapshot().single { it.id == sessionId }
            assertFalse(sealed.isActive)
            assertEquals(700, sealed.elapsedSeconds) // tick-accrued only
            assertEquals(null, sealed.routineOutcome) // legacy rows are never stamped (A3)
            db.openHelper.writableDatabase.execSQL(
                "DELETE FROM emergency_break_spends WHERE sessionId = $sessionId"
            )
        } else {
            assertEquals(BreakResult.RefusedNoToken, result)
            val sealed = dao.getSessionHistorySnapshot().single { it.id == sessionId }
            assertEquals(true, sealed.isActive) // refusal leaves it sealed shut
        }
        db.openHelper.writableDatabase.execSQL("DELETE FROM focus_sessions WHERE id = $sessionId")
    }
}
