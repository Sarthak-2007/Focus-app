package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R2 #95 — the shield-gap law's truth guards, at the
 * repository against the real database. One sequential story (the
 * sandbox database is shared by the class, and the active-session
 * read takes the newest active row):
 *
 *  1. A pre-v10 row (beat = 0: the beat column did not exist when
 *     the session started) is ADOPTED on first judgement — the
 *     beat is stamped, no gap latched — instead of being convicted
 *     from its session start for a gap nobody observed.
 *  2. The law still bites: once adopted, a beat gone stale past
 *     the 10s threshold latches the gap.
 *  3. A normally-beating row with a stale beat latches; a row
 *     with a fresh beat does not.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ShieldGapLawTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun repository(): MonolithRepository {
        val db = MonolithDatabase.getInstance(context)
        return MonolithRepository(context, db.monolithDao())
    }

    private fun activeRow(startWall: Long, beat: Long) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 3000,
        elapsedSeconds = 0,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = 0L,
        isActive = true,
        completedSuccessfully = false,
        isStopwatchMode = false,
        outcome = "",
        shieldLastBeatWallMillis = beat,
        shieldGapSeen = false
    )

    @Test
    fun gapLawStory() = runBlocking {
        val repo = repository()
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val now = System.currentTimeMillis()

        // 1. Pre-v10 row: started an hour ago, no beat ever recorded.
        val aId = dao.insertSession(activeRow(startWall = now - 3_600_000L, beat = 0L)).toInt()
        assertFalse(repo.latchShieldGapIfStale(now))
        val a = dao.getSessionById(aId)!!
        assertEquals(now, a.shieldLastBeatWallMillis)
        assertFalse(a.shieldGapSeen)

        // 2. Twenty seconds on with no further beat: the law bites.
        assertTrue(repo.latchShieldGapIfStale(now + 20_000L))
        assertTrue(dao.getSessionById(aId)!!.shieldGapSeen)
        dao.updateSession(dao.getSessionById(aId)!!.copy(isActive = false))

        // 3a. A beating row whose beat went stale a minute ago latches.
        val bId = dao.insertSession(
            activeRow(startWall = now - 3_600_000L, beat = now - 60_000L)
        ).toInt()
        assertTrue(repo.latchShieldGapIfStale(now))
        assertTrue(dao.getSessionById(bId)!!.shieldGapSeen)
        dao.updateSession(dao.getSessionById(bId)!!.copy(isActive = false))

        // 3b. A row with a fresh beat does not latch.
        val cId = dao.insertSession(activeRow(startWall = now - 60_000L, beat = now)).toInt()
        assertFalse(repo.latchShieldGapIfStale(now + 5_000L))
        assertFalse(dao.getSessionById(cId)!!.shieldGapSeen)
    }
}
