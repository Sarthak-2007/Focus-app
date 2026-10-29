package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FinishKind
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import com.example.data.finishKindOf
import com.example.data.finishPulses
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Slice E — the № claim and the publish gate, at the repository
 * (the enforcement layer), against the app's real database file
 * in the Robolectric sandbox — the claim takes its transaction
 * from the singleton, exactly as it does on the phone.
 *
 * Three properties:
 *  1. The claim mints in sequence, once per session, and a
 *     re-claim returns the standing № (Zettel Z1's latch).
 *  2. A settings write between claims does NOT clobber the
 *     sequence — §7.5's REPLACE trap, fixed in this commit with
 *     targeted UPDATEs, proven here by interleaving one.
 *  3. publishReceipt's gate re-checks the terminal predicate on
 *     the fresh row: an already-published session publishes
 *     nothing twice, and an active row assembles nothing.
 * Plus the discriminator's matrix as pure function tests.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReceiptClaimRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun repository(): MonolithRepository {
        val db = MonolithDatabase.getInstance(context)
        return MonolithRepository(context, db.monolithDao())
    }

    private fun endedRow(
        outcome: String,
        stopwatch: Boolean = false,
        completed: Boolean = true
    ) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 3000,
        elapsedSeconds = 3000,
        startWallTimeMillis = 1_000_000L,
        startElapsedRealtimeMillis = 0L,
        isActive = false,
        completedSuccessfully = completed,
        isStopwatchMode = stopwatch,
        outcome = outcome,
        heldAppsAtSeal = 3,
        heldSitesAtSeal = 2
    )

    /**
     * One sequential story — the sandbox database is shared by
     * the whole class, so the claim sequence is asserted
     * RELATIVE to wherever it stands when the story starts.
     */
    @Test
    fun claimAndPublishStory() = runBlocking {
        val repo = repository()
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val base = repo.standingReceipt()?.first ?: 0

        // The claim mints in sequence, once per session.
        assertEquals(base + 1, repo.claimReceiptNumber(101, "ENDED"))
        assertEquals(
            "a re-claim returns the standing №, no increment",
            base + 1, repo.claimReceiptNumber(101, "ENDED")
        )
        // §7.5's trap, interleaved on purpose: a settings write
        // between claims must leave the sequence standing.
        repo.setBrowserBlockerEnabled(false)
        assertEquals(base + 2, repo.claimReceiptNumber(102, "ENDED"))
        repo.setBrowserBlockerEnabled(true)
        assertEquals(base + 3, repo.claimReceiptNumber(103, "ATTENDED"))
        assertEquals(103, repo.standingReceipt()!!.second)

        // The publish gate: a real finish publishes exactly once.
        val id = dao.insertSession(endedRow(SessionOutcome.ELAPSED_CLEAN)).toInt()
        val pending = repo.assemblePendingReceipt(id, FinishKind.MANUAL_FINISH, 4_600_000L)
        assertNotNull(pending)
        val receipt = repo.publishReceipt(pending!!, faulted = false)
        assertNotNull(receipt)
        assertEquals(base + 4, receipt!!.number)
        assertEquals(
            "№ " + (base + 4).toString().padStart(3, '0'),
            receipt.numberPrint
        )
        assertEquals(3, receipt.heldAppsAtSeal)
        assertEquals(2, receipt.heldSitesAtSeal)
        assertFalse(receipt.faulted)

        // A second publish of the same finish — even freshly
        // assembled — mints nothing and returns null.
        val again = repo.assemblePendingReceipt(id, FinishKind.MANUAL_FINISH, 4_600_000L)
        assertNotNull(again)
        assertNull(repo.publishReceipt(again!!, faulted = false))
        assertEquals(base + 4, repo.standingReceipt()!!.first)

        // An active row is not a finish: assembly refuses it.
        val activeId = dao.insertSession(
            endedRow(SessionOutcome.ELAPSED_CLEAN).copy(isActive = true)
        ).toInt()
        assertNull(repo.assemblePendingReceipt(activeId, FinishKind.MANUAL_FINISH, 4_600_000L))

        // Cleanup is part of the contract: the sandbox database
        // file is shared across test classes in the fork, and a
        // leftover ACTIVE row from this story broke
        // EmergencyBreakRepositoryTest's no-active-session
        // assertions in the proof-8 run. Both inserted rows go.
        dao.deleteSessionById(id)
        dao.deleteSessionById(activeId)
    }

    /**
     * Truth Car 2 — RC-04, RC-01, RC-02 at the repository.
     * RC-04: the claim stamps the № ON THE SESSION ROW inside the
     * claim transaction; a second session's claim must not move
     * the first's number, and a re-claim of the first returns its
     * stamped № and mints nothing.
     * RC-02: a FLOW finish assembles with NO planned seconds (the
     * slip omits PLANNED); a manual finish keeps its plan.
     * RC-01: the row's persisted end stamp wins over any endedAt
     * derived at assembly time.
     */
    @Test
    fun truthCar2_rowStampAndAssembly() = runBlocking {
        val repo = repository()
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()

        val a = dao.insertSession(endedRow(SessionOutcome.ELAPSED_CLEAN)).toInt()
        val b = dao.insertSession(endedRow(SessionOutcome.ELAPSED_CLEAN)).toInt()
        val na = repo.claimReceiptNumber(a, "ENDED")
        val nb = repo.claimReceiptNumber(b, "ENDED")
        assertEquals(na + 1, nb)
        assertEquals("the claim stamps the row (RC-04)", na, dao.getSessionById(a)!!.receiptNumber)
        assertEquals(nb, dao.getSessionById(b)!!.receiptNumber)
        assertEquals(
            "a re-claim of A returns A's stamped №, minting nothing",
            na, repo.claimReceiptNumber(a, "ENDED")
        )
        assertEquals(na, dao.getSessionById(a)!!.receiptNumber)

        // RC-02.
        val flowId = dao.insertSession(
            endedRow(SessionOutcome.STOPWATCH_ENDED, stopwatch = true)
        ).toInt()
        val flowPending = repo.assemblePendingReceipt(flowId, FinishKind.FLOW_FINISH, 4_600_000L)
        assertNotNull(flowPending)
        assertNull("a FLOW finish carries no plan (RC-02)", flowPending!!.plannedSeconds)
        val manualPending = repo.assemblePendingReceipt(a, FinishKind.MANUAL_FINISH, 4_600_000L)
        assertNotNull(manualPending)
        assertEquals(3000, manualPending!!.plannedSeconds)

        // RC-01.
        val stamped = dao.insertSession(
            endedRow(SessionOutcome.ELAPSED_CLEAN).copy(endWallMillis = 4_444_000L)
        ).toInt()
        val stampedPending =
            repo.assemblePendingReceipt(stamped, FinishKind.MANUAL_FINISH, 9_999_000L)
        assertNotNull(stampedPending)
        assertEquals(
            "the row's persisted stamp wins over the caller's derivation (RC-01)",
            4_444_000L, stampedPending!!.endedAtWallMs
        )

        // Sandbox hygiene, same contract as the story above.
        dao.deleteSessionById(a)
        dao.deleteSessionById(b)
        dao.deleteSessionById(flowId)
        dao.deleteSessionById(stamped)
    }

    @Test
    fun discriminatorMatrix() {
        val now = 10_000_000L
        fun row(
            outcome: String = "",
            routineId: Int? = null,
            routineOutcome: String? = null,
            stopwatch: Boolean = false,
            completed: Boolean = true,
            active: Boolean = false,
            windowEnd: Long = 0L
        ) = FocusSessionEntity(
            intention = "X",
            profileName = "Default",
            targetDurationSeconds = 3000,
            elapsedSeconds = 3000,
            startWallTimeMillis = 1_000L,
            startElapsedRealtimeMillis = 0L,
            isActive = active,
            completedSuccessfully = completed,
            isStopwatchMode = stopwatch,
            outcome = outcome,
            routineId = routineId,
            routineOutcome = routineOutcome,
            windowEndMillis = windowEnd
        )

        // Manual finishes.
        val clean = row(outcome = SessionOutcome.ELAPSED_CLEAN)
        assertEquals(FinishKind.MANUAL_FINISH, finishKindOf(clean, false, now))
        assertTrue(finishPulses(FinishKind.MANUAL_FINISH, clean))
        val gap = row(outcome = SessionOutcome.ELAPSED_SHIELD_GAP)
        assertEquals(FinishKind.MANUAL_FINISH, finishKindOf(gap, false, now))
        assertFalse(
            "death-booked finishes never pulse (B-12)",
            finishPulses(FinishKind.MANUAL_FINISH, gap)
        )
        // FLOW.
        val flow = row(outcome = SessionOutcome.STOPWATCH_ENDED, stopwatch = true)
        assertEquals(FinishKind.FLOW_FINISH, finishKindOf(flow, false, now))
        assertNull(
            finishKindOf(
                row(outcome = SessionOutcome.STOPWATCH_ENDED, stopwatch = true, completed = false),
                false, now
            )
        )
        // Endings that publish NOTHING.
        assertNull(finishKindOf(row(outcome = SessionOutcome.VALVE_BROKE), false, now))
        assertNull(finishKindOf(row(outcome = SessionOutcome.GRACE_CANCEL), false, now))
        assertNull(finishKindOf(row(outcome = SessionOutcome.ENDED_EARLY), false, now))
        assertNull(finishKindOf(row(outcome = SessionOutcome.ELAPSED_CLEAN, active = true), false, now))
        // Routine finishes.
        assertEquals(
            FinishKind.ROUTINE_ATTENDED,
            finishKindOf(row(routineId = 5, routineOutcome = "ATTENDED"), false, now)
        )
        val partial = row(routineId = 5, routineOutcome = "PARTIAL")
        assertEquals(FinishKind.ROUTINE_PARTIAL, finishKindOf(partial, false, now))
        assertFalse(finishPulses(FinishKind.ROUTINE_PARTIAL, partial))
        assertEquals(
            FinishKind.ROUTINE_SLEEP_STANDDOWN,
            finishKindOf(
                row(routineId = 5, routineOutcome = null, windowEnd = 5_000_000L),
                isSleepLoadout = true, nowWallMillis = now
            )
        )
        assertNull(
            "a missed routine window is never an E0",
            finishKindOf(
                row(routineId = 5, routineOutcome = "MISSED", windowEnd = 5_000_000L),
                isSleepLoadout = true, nowWallMillis = now
            )
        )
    }
}
