package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Issue #125 F4 (Sarthak's ruling, Oct 31 19:14 IST, sharpened at
 * 6544202995) — the in-grace edit, enforced in the repository
 * write: add time / change duration, change blocks, change
 * loadout — in the start window only (the 30s grace window since
 * F1), never after it, and never on a routine-born row.
 *
 * PIN-scope: PLUMBING — repository-write assertions on
 * [MonolithRepository.editActiveSessionInGrace]: window gate on
 * the verified law elapsed, the row facts it writes, the HMAC
 * re-sign on a duration change, and the refusal shapes. The
 * inserted rows are SIGNED over their targets, so a duration
 * edit that forgot to re-sign would fail
 * [CryptoIntegrityManager.verifySessionState] here.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InGraceEditRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private suspend fun insertActiveSessionBackdated(
        dao: MonolithDao,
        secondsAgo: Long,
        targetDurationSeconds: Int = 50 * 60,
        totalStudyBlocks: Int = 1,
        profileName: String = "Manual Focus",
        isStopwatchMode: Boolean = false,
        routineId: Int? = null
    ): Int {
        // Same anchor cure as GraceDeadlineRepositoryTest's
        // helper: advance the shadow monotonic clock past the
        // backdate first, so the anchor pair is real and the
        // verified elapsed equals the backdate.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(secondsAgo + 60L))
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val startWall = nowWall - secondsAgo * 1000L
        val startMono = (nowMono - secondsAgo * 1000L).coerceAtLeast(1L)
        dao.deactivateAllSessions()
        // Issue #36 (rulings 2+3): the scalar signSessionState
        // overload is deleted; the row is signed in the entity
        // form over exactly the row that persists — the same
        // writer idiom the repository itself uses.
        val unsignedRow = FocusSessionEntity(
            intention = "In-grace edit test session",
            profileName = profileName,
            targetDurationSeconds = targetDurationSeconds,
            elapsedSeconds = secondsAgo.toInt(),
            startWallTimeMillis = startWall,
            startElapsedRealtimeMillis = startMono,
            hmacSignature = "",
            isStopwatchMode = isStopwatchMode,
            totalStudyBlocks = totalStudyBlocks,
            isActive = true,
            completedSuccessfully = false,
            routineId = routineId
        )
        val id = dao.insertSession(
            unsignedRow.copy(
                hmacSignature = CryptoIntegrityManager.signSessionState(unsignedRow)
            )
        )
        return id.toInt()
    }

    @Test
    fun editInsideTheWindow_appliesDurationBlocksAndLoadout_andReSigns() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 5L)
        val before = dao.getSessionById(sessionId)!!
        assertTrue(CryptoIntegrityManager.verifySessionState(before))

        val applied = repo.editActiveSessionInGrace(
            durationMinutes = 90,
            studyBlocksCount = 3,
            loadout = SmartLoadout.STUDY
        )

        assertTrue(applied)
        val row = dao.getSessionById(sessionId)!!
        assertEquals(90 * 60, row.targetDurationSeconds)
        assertEquals(3, row.totalStudyBlocks)
        assertEquals(SmartLoadout.STUDY.title, row.profileName)
        // The duration change re-signed the row: the signature
        // moved AND still verifies against the new target.
        assertNotEquals(before.hmacSignature, row.hmacSignature)
        assertTrue(CryptoIntegrityManager.verifySessionState(row))
        val (_, tamper) = repo.computeVerifiedSessionElapsed(row)
        assertFalse(tamper)
        // The seal's birth facts are NOT the edit's to rewrite.
        assertEquals(before.heldAppsAtSeal, row.heldAppsAtSeal)
        assertEquals(before.startWallTimeMillis, row.startWallTimeMillis)
    }

    @Test
    fun editAtSecondTwenty_applies_underTheStretchedWindow() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // Second 20 refused under the old 10s law; F1's ruling
        // stretched the window to 30, and F4's gate rides it.
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 20L)

        val applied = repo.editActiveSessionInGrace(
            durationMinutes = null,
            studyBlocksCount = 4,
            loadout = null
        )

        assertTrue(applied)
        assertEquals(4, dao.getSessionById(sessionId)!!.totalStudyBlocks)
    }

    @Test
    fun editAfterTheWindow_refusesAndChangesNothing() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        val sessionId = insertActiveSessionBackdated(dao, secondsAgo = 45L)
        val before = dao.getSessionById(sessionId)!!

        val applied = repo.editActiveSessionInGrace(
            durationMinutes = 90,
            studyBlocksCount = 3,
            loadout = SmartLoadout.SLEEP
        )

        assertFalse(applied)
        val row = dao.getSessionById(sessionId)!!
        assertEquals(before.targetDurationSeconds, row.targetDurationSeconds)
        assertEquals(before.totalStudyBlocks, row.totalStudyBlocks)
        assertEquals(before.profileName, row.profileName)
        assertEquals(before.hmacSignature, row.hmacSignature)
    }

    @Test
    fun editWithNothingToCorrect_refuses() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        insertActiveSessionBackdated(dao, secondsAgo = 5L)

        assertFalse(
            repo.editActiveSessionInGrace(
                durationMinutes = null,
                studyBlocksCount = null,
                loadout = null
            )
        )
    }

    @Test
    fun routineBornSession_refusesEvenInsideTheWindow() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        val sessionId = insertActiveSessionBackdated(
            dao, secondsAgo = 5L, routineId = 7
        )

        val applied = repo.editActiveSessionInGrace(
            durationMinutes = 90,
            studyBlocksCount = 2,
            loadout = SmartLoadout.STUDY
        )

        assertFalse(applied)
        assertEquals(50 * 60, dao.getSessionById(sessionId)!!.targetDurationSeconds)
    }

    @Test
    fun stopwatchRow_takesBlocksAndLoadout_butNotDuration() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        val repo = MonolithRepository(context, dao)
        // A FLOW arm: its target is the 20-minute floor fact, not
        // a set duration — there is no wrong duration to correct.
        val sessionId = insertActiveSessionBackdated(
            dao,
            secondsAgo = 5L,
            targetDurationSeconds = MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS,
            isStopwatchMode = true
        )

        val applied = repo.editActiveSessionInGrace(
            durationMinutes = 90,
            studyBlocksCount = 2,
            loadout = SmartLoadout.SLEEP
        )

        assertTrue(applied)
        val row = dao.getSessionById(sessionId)!!
        assertEquals(
            MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS,
            row.targetDurationSeconds
        )
        assertEquals(2, row.totalStudyBlocks)
        assertEquals(SmartLoadout.SLEEP.title, row.profileName)
        assertTrue(CryptoIntegrityManager.verifySessionState(row))
    }
}
