package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * W3 #134 (phone-call-mid-session, Audit R3b) at the repository —
 * the persona: his mother / coaching institute calls mid-session.
 *
 * The call signal is held open through the repository's probe
 * seam (the production default reads AudioManager, exactly the
 * signal the blocking doors already trust). Rows are written the
 * way a live tick leaves them: anchors in the past, lastTick at
 * the last tick's instant.
 *
 *  1. Call minutes are not focus minutes: a 60s-focus + 600s-call
 *     span books 60 elapsed, with the 600 in the exclusion
 *     ledger — and stays booked that way after hang-up.
 *  2. A session whose target was met just before the ring does
 *     NOT finish underneath the call: it stays active, unstamped,
 *     until the first post-hang-up tick finishes it and stamps
 *     the end then.
 *  3. A call straddling the Safe Break does not spend it: the
 *     countdown holds at its full remainder for the whole call,
 *     then drains and flips normally after hang-up — and the
 *     per-block call ledger resets at the break exit.
 *  (Finding 4's dialer exemption is deliberately absent —
 *  struck under Sarthak's NEVER THROUGH verdict; see #134.)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CallMidSessionRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun sessionRow(
        targetSeconds: Int,
        focusSecondsBeforeCall: Long,
        callSeconds: Long,
        isInSafeBreak: Boolean = false,
        safeBreakSecondsRemaining: Int = 0,
        totalStudyBlocks: Int = 1,
        currentBlockIndex: Int = 1,
        callExcludedSeconds: Int = 0
    ): FocusSessionEntity {
        // Fixture correction (the requirement assertions below are
        // unchanged): Robolectric's monotonic clock starts near
        // zero, so building the row straight off it put the
        // session's start anchor BEFORE boot (nowMono − span <= 0)
        // — a row no real phone can hold, since a session is
        // always started after boot, with uptime already past
        // every span it will ever contain. The progress machine
        // rightly reads a non-positive monotonic start anchor as
        // a post-reboot row and re-bases verified elapsed on the
        // stored figure, which silently ate the call exclusion
        // (verified 60 − excluded 600 -> 0). Give the phone a
        // realistic uptime first — six hours, comfortably past
        // the largest span these scenarios construct (2,101s) —
        // so the anchors model the phone the persona is on.
        ShadowSystemClock.advanceBy(Duration.ofMillis(6 * 60 * 60 * 1000L))
        val nowMono = SystemClock.elapsedRealtime()
        val nowWall = System.currentTimeMillis()
        val spanMillis = (focusSecondsBeforeCall + callSeconds) * 1000L
        return FocusSessionEntity(
            intention = "CALL MID-SESSION",
            profileName = "Manual",
            targetDurationSeconds = targetSeconds,
            elapsedSeconds = focusSecondsBeforeCall.toInt(),
            startWallTimeMillis = nowWall - spanMillis,
            startElapsedRealtimeMillis = nowMono - spanMillis,
            lastTickElapsedRealtimeMillis = nowMono - callSeconds * 1000L,
            totalStudyBlocks = totalStudyBlocks,
            currentBlockIndex = currentBlockIndex,
            isInSafeBreak = isInSafeBreak,
            safeBreakSecondsRemaining = safeBreakSecondsRemaining,
            callExcludedSeconds = callExcludedSeconds,
            shieldLastBeatWallMillis = nowWall,
            isActive = true,
            completedSuccessfully = false
        )
    }

    @Test
    fun callMinutes_areExcludedFromBookedElapsed() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // 60 seconds of focus, then a 10-minute call ending now.
        val id = dao.insertSession(
            sessionRow(targetSeconds = 3600, focusSecondsBeforeCall = 60, callSeconds = 600)
        ).toInt()
        try {
            repository.callActiveProbe = { true }
            val duringCall = repository.updateSessionProgress(dao.getSessionById(id)!!)
            assertEquals(60, duringCall.elapsedSeconds)
            assertEquals(600, duringCall.callExcludedSeconds)
            assertTrue(duringCall.isActive)

            // Hang up: the booking does not move — the call
            // minutes never re-enter the elapsed figure.
            repository.callActiveProbe = { false }
            val afterCall = repository.updateSessionProgress(duringCall)
            assertEquals(60, afterCall.elapsedSeconds)
            assertEquals(600, afterCall.callExcludedSeconds)
            assertTrue(afterCall.isActive)
        } finally {
            repository.callActiveProbe = { repository.isPhoneOrVoipCallActive() }
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun finish_isHeldUntilTheCallEnds() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // Target met in the cadence before the ring: 60s of focus
        // against a 60s target, then 60s on the call.
        val id = dao.insertSession(
            sessionRow(targetSeconds = 60, focusSecondsBeforeCall = 60, callSeconds = 60)
        ).toInt()
        try {
            repository.callActiveProbe = { true }
            val held = repository.updateSessionProgress(dao.getSessionById(id)!!)
            // No finish underneath the conversation: still
            // active, no outcome, no end stamp, not completed.
            assertTrue(held.isActive)
            assertFalse(held.completedSuccessfully)
            assertEquals("", held.outcome)
            assertEquals(0L, held.endWallMillis)
            assertEquals(60, held.elapsedSeconds)

            // First tick after hang-up: the finish fires now,
            // witnessed now.
            repository.callActiveProbe = { false }
            val finished = repository.updateSessionProgress(held)
            assertFalse(finished.isActive)
            assertTrue(finished.completedSuccessfully)
            assertEquals(SessionOutcome.ELAPSED_CLEAN, finished.outcome)
            assertTrue(finished.endWallMillis > 0L)
        } finally {
            repository.callActiveProbe = { repository.isPhoneOrVoipCallActive() }
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun safeBreak_doesNotDrainDuringACall() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // In the Safe Break with its full 600s remaining; the
        // last tick was 601s ago and a call covers that span.
        // The row carries a previous block's 45 excluded call
        // seconds, to prove the break exit resets the ledger.
        val nowMono = SystemClock.elapsedRealtime()
        val row = sessionRow(
            targetSeconds = 1500,
            focusSecondsBeforeCall = 1500,
            callSeconds = 601,
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            totalStudyBlocks = 2,
            currentBlockIndex = 1,
            callExcludedSeconds = 45
        ).copy(lastTickElapsedRealtimeMillis = nowMono - 601_000L)
        val id = dao.insertSession(row).toInt()
        try {
            repository.callActiveProbe = { true }
            val held = repository.updateSessionProgress(dao.getSessionById(id)!!)
            assertTrue(held.isInSafeBreak)
            assertEquals(600, held.safeBreakSecondsRemaining)
            assertEquals(1, held.currentBlockIndex)
            assertEquals(45, held.callExcludedSeconds)

            // After hang-up the break drains from the held
            // remainder: 601 further seconds end it, the next
            // block starts, and the call ledger restarts at 0.
            // Fixture correction for the composition onto current
            // main (requirement assertions unchanged, same class
            // as the uptime correction above): since issue #33 the
            // progress machine computes from the row AS IT STANDS
            // in the database — the caller's snapshot only names
            // the session — so ageing a snapshot copy no longer
            // moves time. Time travels on the clock instead: the
            // monotonic clock advances 601s, the stored row's tick
            // anchor stands 601s in the past, and the drain the
            // assertions judge is the machine's real one.
            repository.callActiveProbe = { false }
            ShadowSystemClock.advanceBy(Duration.ofMillis(601_000L))
            val flipped = repository.updateSessionProgress(held)
            assertFalse(flipped.isInSafeBreak)
            assertEquals(2, flipped.currentBlockIndex)
            assertEquals(0, flipped.safeBreakSecondsRemaining)
            assertEquals(600, flipped.breakTakenSeconds)
            assertEquals(0, flipped.callExcludedSeconds)
        } finally {
            repository.callActiveProbe = { repository.isPhoneOrVoipCallActive() }
            dao.deleteSessionById(id)
        }
    }

}
