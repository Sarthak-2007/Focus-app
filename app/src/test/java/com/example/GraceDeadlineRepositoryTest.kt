package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.DisarmResult
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
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
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * PIN scope: PLUMBING.
 *
 * Issue #103 F2 — the grace deadline (30 seconds since issue
 * #125 F1 stretched the window, Sarthak's ruling Oct 31 19:14
 * IST) is enforced in the ending write, not only rendered by
 * the Deck.
 *
 * Before the fix, [MonolithRepository.disarmActiveSession] derived
 * `isGraceCancel` purely from the outcome-reason STRING, so any
 * late "Grace Cancel" zeroed the session's real elapsed history
 * and stamped GRACE_CANCEL. Now the write checks the VERIFIED
 * elapsed against [MonolithRepository.GRACE_WINDOW_SECONDS]:
 * inside the window the cancel is free and books zero; past it,
 * the same call books the real elapsed as an ordinary early end.
 *
 * Issue #523 (6-3) completes the law's other half: booking the late
 * grace cancel honestly still DISARMED it — an un-tokened exit.
 * Now a grace-cancel reason past the window is REFUSED:
 * [DisarmResult.RefusedLateGraceCancel], nothing is written, and
 * the session stays sealed. Ordinary early ends (any other reason)
 * keep the #149 booking exactly as before.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GraceDeadlineRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private suspend fun insertActiveSessionBackdated(
        dao: MonolithDao,
        secondsAgo: Long
    ): Int {
        // AUDIT-CLOCK census seat (a) cure (#166): Robolectric's
        // shadow monotonic clock starts near zero, so the raw
        // backdate below clamped the mono anchor to 1L — the
        // verified clock then read (0s, tamper = true), and every
        // assertion in this file rode the presaved stored counter
        // alone; the verified leg these tests advertise did not
        // exist. Give the phone an uptime past the backdate first
        // (the ForgeLot idiom), inside the helper so both call
        // sites are cured: the anchor pair is now real, the
        // verified read equals the backdate, and a break in the
        // verified-clock path moves the booked outcome.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(secondsAgo + 60L))
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val id = dao.insertSession(
            FocusSessionEntity(
                intention = "Grace deadline test session",
                profileName = "Manual Focus",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = secondsAgo.toInt(),
                startWallTimeMillis = nowWall - secondsAgo * 1000L,
                startElapsedRealtimeMillis = (nowMono - secondsAgo * 1000L).coerceAtLeast(1L),
                isActive = true,
                completedSuccessfully = false
            )
        )
        return id.toInt()
    }

    @Test
    fun graceCancelInsideTheWindow_booksZeroAndGraceCancel() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 5L)

        val result = repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )

        assertEquals(DisarmResult.Disarmed, result)
        assertNull(dao.getActiveSessionSnapshot())
        val row = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertEquals(0, row.elapsedSeconds)
        assertEquals(SessionOutcome.GRACE_CANCEL, row.outcome)
        // The verified leg is real now (seat (a) cure): the two
        // anchors agree, so the ending write stamps no tamper.
        // Before the cure this row silently carried tamper = true.
        assertFalse(row.tamperDetected)
    }

    @Test
    fun graceCancelAtSecondTwenty_booksZero_underTheStretchedWindow() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // Issue #125 F1: second 20 was past the old 10-second
        // cliff (it would have booked real elapsed as an early
        // end); under the ruled 30-second window the same cancel
        // is free and books zero.
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 20L)

        repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )

        assertNull(dao.getActiveSessionSnapshot())
        val row = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertEquals(0, row.elapsedSeconds)
        assertEquals(SessionOutcome.GRACE_CANCEL, row.outcome)
        assertFalse(row.tamperDetected)
    }

    @Test
    fun graceCancelAfterTheWindow_isRefused_sessionStaysSealedAndUnbooked() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // Five real minutes in — far past the printed window.
        // The stored counter and the verified clock agree (the
        // helper advances the shadow clock so both anchors are
        // real); only the string claims "Grace Cancel".
        // Issue #523 (6-3): the call
        // is refused — the #149-era expectation (booked ENDED_EARLY)
        // is superseded for grace reasons; the exit itself is what
        // was never priced, and the refusal is the cure.
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 300L)

        val result = repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )

        assertEquals(DisarmResult.RefusedLateGraceCancel, result)
        val stillActive = dao.getActiveSessionSnapshot()
        assertNotNull("a refused disarm leaves the session sealed", stillActive)
        assertEquals(sessionId, stillActive!!.id)
        // Nothing was booked: the session's own row is still the
        // ACTIVE row (getSessionHistorySnapshot lists every row, so
        // the refusal's proof is the row's state, not its absence).
        val unbooked = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertTrue("a refused disarm books no ending", unbooked.isActive)

        // The sealed session remains endable through the ordinary
        // door — and cleanup restores this class's shared-DB
        // invariant (no active rows left behind for sibling tests).
        val cleanup = repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Ended Early"
        )
        assertEquals(DisarmResult.Disarmed, cleanup)
        assertNull(dao.getActiveSessionSnapshot())
        val booked = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertEquals(SessionOutcome.ENDED_EARLY, booked.outcome)
        // The verified leg is real now (seat (a) cure): the two
        // anchors agree, so the ending write stamps no tamper.
        assertFalse(booked.tamperDetected)
    }

    @Test
    fun ordinaryEarlyEndAfterTheWindow_stillBooksRealElapsed() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // The refusal is scoped to the grace string: an ordinary
        // early end at the same elapsed keeps the #149 booking —
        // real elapsed, ENDED_EARLY, partial stamp.
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 300L)

        val result = repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Ended Early"
        )

        assertEquals(DisarmResult.Disarmed, result)
        assertNull(dao.getActiveSessionSnapshot())
        val row = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertTrue(
            "an ordinary early end must keep real history (was ${row.elapsedSeconds})",
            row.elapsedSeconds >= 60
        )
        assertEquals(SessionOutcome.ENDED_EARLY, row.outcome)
        assertEquals("partial_stamp", row.forgedArtifactId)
        // The verified leg is real now (seat (a) cure): the two
        // anchors agree, so the ending write stamps no tamper.
        // Before the cure the verified read was (0s, tamper =
        // true) and this booking rode the stored counter alone.
        assertFalse(row.tamperDetected)
    }
}
