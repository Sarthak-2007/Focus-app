package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #103 F2 — the 10-second grace deadline is enforced in the
 * ending write, not only rendered by the Deck.
 *
 * Before the fix, [MonolithRepository.disarmActiveSession] derived
 * `isGraceCancel` purely from the outcome-reason STRING, so any
 * late "Grace Cancel" zeroed the session's real elapsed history
 * and stamped GRACE_CANCEL. Now the write checks the VERIFIED
 * elapsed against [MonolithRepository.GRACE_WINDOW_SECONDS]:
 * inside the window the cancel is free and books zero; past it,
 * the same call books the real elapsed as an ordinary early end.
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

        repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )

        assertNull(dao.getActiveSessionSnapshot())
        val row = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertEquals(0, row.elapsedSeconds)
        assertEquals(SessionOutcome.GRACE_CANCEL, row.outcome)
    }

    @Test
    fun graceCancelAfterTheWindow_booksRealElapsedAsEarlyEnd() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // Five real minutes in — far past the printed 10 seconds.
        // The stored counter and the verified clock agree; only the
        // string claims "Grace Cancel".
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 300L)

        repo.disarmActiveSession(
            completed = false,
            outcomeReason = "Accidental Start Grace Cancel"
        )

        assertNull(dao.getActiveSessionSnapshot())
        val row = dao.getSessionHistorySnapshot().first { it.id == sessionId }
        assertTrue(
            "a late grace cancel must not zero real history (was ${row.elapsedSeconds})",
            row.elapsedSeconds >= 60
        )
        assertEquals(SessionOutcome.ENDED_EARLY, row.outcome)
        assertEquals("partial_stamp", row.forgedArtifactId)
    }
}
