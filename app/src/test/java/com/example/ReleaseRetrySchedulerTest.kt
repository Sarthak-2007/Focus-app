package com.example

import com.example.service.ReleaseRetryScheduler
import com.example.service.ReleaseRetryScheduler.RetryAction
import com.example.service.recoveryRearmMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #47 (heartbeat-retry) — the retry cadence's decision matrix
 * and the heartbeat's recovery rearm, as pure functions.
 *
 * The five audit findings map onto these properties:
 *  F1 — a healed/superseded fault must never command a stop against
 *       a later live session's shield (SUPERSEDED, never STOP_*).
 *  F2 — the exhausted cadence's next trigger is the real third door
 *       (STOP_FINAL), not a silent return.
 *  F3 — a failed heartbeat pass always has a next wake
 *       (recoveryRearmMillis is always in the future).
 *  F4 — a late trigger never reschedules into the past; the next
 *       retry keeps its cadence spacing after the late attempt.
 * (F5's targeted DAO read + heartbeat probe are proven in
 * ReleaseRetryDaoTest against the real database.)
 */
class ReleaseRetrySchedulerTest {

    private val fault = 1_000_000L

    // ---- F1: heal + supersede ------------------------------------

    @Test
    fun noShieldStandingMeansHealedWhateverElseIsTrue() {
        assertEquals(
            RetryAction.HEALED_NO_SHIELD,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 10_000L, faultWallMillis = fault, retriesDone = 0,
                shieldPresent = false, liveSessionActive = false
            )
        )
        assertEquals(
            RetryAction.HEALED_NO_SHIELD,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 10_000L, faultWallMillis = fault, retriesDone = 0,
                shieldPresent = false, liveSessionActive = true
            )
        )
    }

    @Test
    fun liveSessionIsNeverAStopTargetEvenWhenRetryIsDue() {
        // The stale-fault kill from the audit: fault long past due,
        // retries unspent — but a later session is live, so the only
        // lawful verdict is SUPERSEDED. Never STOP_TIMED/STOP_FINAL.
        assertEquals(
            RetryAction.SUPERSEDED_BY_LIVE_SESSION,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 86_400_000L, faultWallMillis = fault, retriesDone = 0,
                shieldPresent = true, liveSessionActive = true
            )
        )
        assertEquals(
            RetryAction.SUPERSEDED_BY_LIVE_SESSION,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 86_400_000L, faultWallMillis = fault, retriesDone = 2,
                shieldPresent = true, liveSessionActive = true
            )
        )
    }

    // ---- Timed cadence -------------------------------------------

    @Test
    fun timedRetriesWaitThenFireOnCadence() {
        assertEquals(
            RetryAction.WAIT_UNTIL_DUE,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 29_999L, faultWallMillis = fault, retriesDone = 0,
                shieldPresent = true, liveSessionActive = false
            )
        )
        assertEquals(
            RetryAction.STOP_TIMED,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 30_000L, faultWallMillis = fault, retriesDone = 0,
                shieldPresent = true, liveSessionActive = false
            )
        )
        assertEquals(
            RetryAction.WAIT_UNTIL_DUE,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 299_999L, faultWallMillis = fault, retriesDone = 1,
                shieldPresent = true, liveSessionActive = false
            )
        )
        assertEquals(
            RetryAction.STOP_TIMED,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 300_000L, faultWallMillis = fault, retriesDone = 1,
                shieldPresent = true, liveSessionActive = false
            )
        )
    }

    // ---- F2: the third door is real -------------------------------

    @Test
    fun exhaustedTimedCadenceYieldsOneFinalAttemptThenCloses() {
        assertEquals(
            RetryAction.STOP_FINAL,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 86_400_000L, faultWallMillis = fault, retriesDone = 2,
                shieldPresent = true, liveSessionActive = false
            )
        )
        assertEquals(
            RetryAction.EXHAUSTED,
            ReleaseRetryScheduler.retryAction(
                nowMillis = fault + 86_400_000L, faultWallMillis = fault, retriesDone = 3,
                shieldPresent = true, liveSessionActive = false
            )
        )
    }

    // ---- F4: late triggers keep spacing ---------------------------

    @Test
    fun nextRetryAtKeepsNominalInstantWhenOnTime() {
        assertEquals(
            fault + 300_000L,
            ReleaseRetryScheduler.nextRetryAtMillis(fault, retriesDone = 1, nowMillis = fault + 30_000L)
        )
        assertNull(ReleaseRetryScheduler.nextRetryAtMillis(fault, retriesDone = 2, nowMillis = fault))
    }

    @Test
    fun nextRetryAtNeverSchedulesInThePast() {
        // First attempt fires a day late; the second retry must land
        // its full 270 s gap (300 s − 30 s) after THAT attempt, not
        // at fault+300 s — an instant a day in the past.
        val lateNow = fault + 86_400_000L
        assertEquals(
            lateNow + 270_000L,
            ReleaseRetryScheduler.nextRetryAtMillis(fault, retriesDone = 1, nowMillis = lateNow)
        )
        // A first retry scheduled after a late zeroth attempt keeps
        // the full 30 s first gap.
        assertEquals(
            lateNow + 30_000L,
            ReleaseRetryScheduler.nextRetryAtMillis(fault, retriesDone = 0, nowMillis = lateNow)
        )
    }

    // ---- F3: recovery rearm is always a future wake ---------------

    @Test
    fun recoveryRearmPicksSoonerBoundaryOrRecoveryTick() {
        val now = 5_000_000L
        assertEquals(now + 30_000L, recoveryRearmMillis(nextBoundaryMillis = now + 30_000L, nowMillis = now))
        assertEquals(now + 60_000L, recoveryRearmMillis(nextBoundaryMillis = now + 3_600_000L, nowMillis = now))
        assertEquals(now + 60_000L, recoveryRearmMillis(nextBoundaryMillis = null, nowMillis = now))
    }

    @Test
    fun recoveryRearmNeverFiresInstantlyOnOverdueBoundary() {
        val now = 5_000_000L
        // Boundary already past: a small floor, not an immediate
        // re-fire that would spin a persistent failure.
        assertEquals(now + 5_000L, recoveryRearmMillis(nextBoundaryMillis = now - 1L, nowMillis = now))
    }
}
