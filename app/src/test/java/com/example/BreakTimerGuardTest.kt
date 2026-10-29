package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Truth Car 3 (ST-02, Tamm) — Pruefer's guard: cover, not proof.
 *
 * Written against trunk fields ONLY, so it compiles and passes
 * on the pristine tree as well as in this lane: the Safe Break
 * still ends by its timer, exactly as it did before the verb
 * existed. The verb's arrival must not move the exit it shares.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreakTimerGuardTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun breakStillEndsByTimer() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val nowMono = SystemClock.elapsedRealtime()
        val row = FocusSessionEntity(
            intention = "BREAK GUARD",
            profileName = "Manual",
            targetDurationSeconds = 1500,
            elapsedSeconds = 1500,
            startWallTimeMillis = System.currentTimeMillis() - 25 * 60_000L,
            startElapsedRealtimeMillis = nowMono - 25 * 60_000L,
            lastTickElapsedRealtimeMillis = nowMono - 601_000L,
            totalStudyBlocks = 2,
            currentBlockIndex = 1,
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            isActive = true,
            completedSuccessfully = false
        )
        val id = dao.insertSession(row).toInt()
        try {
            val flipped = repository.updateSessionProgress(
                dao.getSessionById(id)!!
            )
            assertFalse(flipped.isInSafeBreak)
            assertEquals(2, flipped.currentBlockIndex)
            assertEquals(0, flipped.safeBreakSecondsRemaining)
        } finally {
            dao.deleteSessionById(id)
        }
    }
}
