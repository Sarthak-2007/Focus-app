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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Truth Car 3 (ST-02, Tamm) at the repository — the break exits.
 *
 * The register's charge: a Safe Break ended only when its timer
 * expired; no end/skip/resume verb existed anywhere in the tree.
 * These tests pin the verb and its honesty condition:
 *
 *  1. the verb performs the expiry transition NOW (next block,
 *     fresh anchors), not at the timer;
 *  2. it books the break's ACTUALLY taken seconds — and the span
 *     derivation is CONTINUOUS across the flip: the lived figure
 *     the desk clock and the receipt read does not jump by the
 *     untaken remainder (under the old cycle arithmetic it would
 *     have jumped by exactly that: target + 600 in place of
 *     target + 240 below);
 *  3. the expiry flip books the full length, and taken seconds
 *     accumulate across a session's breaks;
 *  4. the verb refuses quietly where there is no break to end.
 *
 * In-break rows are written the way a live tick leaves them: the
 * stored remaining is the already-derived figure and lastTick is
 * the last tick's instant (the progress machine rewrites the pair
 * every tick — MonolithRepository's countdown branch).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreakVerbsRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val target = 1500 // one 25-minute block

    /** The verb reads THE active session from a database file the
     *  whole suite shares (the token suite's hygiene precedent):
     *  a foreign class's leftover active row would be the row the
     *  verb meets. The verb tests take the active slot clean, and
     *  every row they insert is deleted on the way out. */
    private fun clearForeignActiveSessions(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL(
            "UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1"
        )
    }

    private fun breakRow(
        blocks: Int = 2,
        blockIndex: Int = 1,
        storedRemaining: Int = 360,
        taken: Int = 0,
        lastTickMono: Long = SystemClock.elapsedRealtime()
    ) = FocusSessionEntity(
        intention = "BREAK VERBS",
        profileName = "Manual",
        targetDurationSeconds = target,
        elapsedSeconds = target,
        startWallTimeMillis = System.currentTimeMillis() - 25 * 60_000L,
        startElapsedRealtimeMillis = lastTickMono - 25 * 60_000L,
        lastTickElapsedRealtimeMillis = lastTickMono,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = true,
        safeBreakSecondsRemaining = storedRemaining,
        breakTakenSeconds = taken,
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun endEarly_flipsNow_andBooksOnlyTheTakenPart() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        // A break 240 of its 600 seconds in: the last tick stored
        // remaining = 360.
        val id = dao.insertSession(breakRow()).toInt()
        try {
            val stored = dao.getSessionById(id)!!
            // The lived span mid-break: block + the 240 taken.
            assertEquals(target + 240, stored.spanElapsedSeconds)

            val flipped = repository.endSafeBreakEarly(id)
            assertNotNull(flipped)
            flipped!!
            assertFalse(flipped.isInSafeBreak)
            assertEquals(0, flipped.safeBreakSecondsRemaining)
            assertEquals(2, flipped.currentBlockIndex)
            assertEquals(0, flipped.elapsedSeconds)
            assertTrue(flipped.isActive)
            // The taken ledger books the 240 that ran — never the
            // 600 the timer would have run.
            assertEquals(240, flipped.breakTakenSeconds)
            // Continuity: the span does not move at the flip.
            assertEquals(target + 240, flipped.spanElapsedSeconds)
            // And the write is the stored fact, not a returned echo.
            val persisted = dao.getSessionById(id)!!
            assertFalse(persisted.isInSafeBreak)
            assertEquals(240, persisted.breakTakenSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun endEarly_atTheFirstSecond_booksZero() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(breakRow(storedRemaining = 600)).toInt()
        try {
            val flipped = repository.endSafeBreakEarly(id)!!
            assertEquals(0, flipped.breakTakenSeconds)
            assertEquals(2, flipped.currentBlockIndex)
            assertEquals(target, flipped.spanElapsedSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun endEarly_refusesWhenNotInBreak() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val studying = breakRow().copy(
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            elapsedSeconds = 100
        )
        val id = dao.insertSession(studying).toInt()
        try {
            val out = repository.endSafeBreakEarly(id)!!
            assertFalse(out.isInSafeBreak)
            assertEquals(1, out.currentBlockIndex)
            assertEquals(0, out.breakTakenSeconds)
            assertEquals(100, dao.getSessionById(id)!!.elapsedSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun endEarly_wrongId_leavesTheBreakStanding() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(breakRow()).toInt()
        try {
            repository.endSafeBreakEarly(id + 999)
            val stored = dao.getSessionById(id)!!
            assertTrue(stored.isInSafeBreak)
            assertEquals(0, stored.breakTakenSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun expiryFlip_booksTheFullLength() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // The stored pair as the last tick left it just past the
        // end: remaining 600 stamped 601 seconds ago derives to 0.
        val nowMono = SystemClock.elapsedRealtime()
        val row = breakRow(
            storedRemaining = 600,
            lastTickMono = nowMono - 601_000L
        )
        val id = dao.insertSession(row).toInt()
        try {
            val flipped = repository.updateSessionProgress(
                dao.getSessionById(id)!!
            )
            assertFalse(flipped.isInSafeBreak)
            assertEquals(2, flipped.currentBlockIndex)
            assertEquals(600, flipped.breakTakenSeconds)
            assertEquals(target + 600, flipped.spanElapsedSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun takenAccumulatesAcrossBreaks() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // Second break of three, 240 already booked from an early
        // end; this one expires in full: 240 + 600.
        val nowMono = SystemClock.elapsedRealtime()
        val row = breakRow(
            blocks = 3,
            blockIndex = 2,
            storedRemaining = 600,
            taken = 240,
            lastTickMono = nowMono - 601_000L
        )
        val id = dao.insertSession(row).toInt()
        try {
            val flipped = repository.updateSessionProgress(
                dao.getSessionById(id)!!
            )
            assertEquals(3, flipped.currentBlockIndex)
            assertEquals(840, flipped.breakTakenSeconds)
            assertEquals(2 * target + 840, flipped.spanElapsedSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    // ---- ST-03: the boundaries speak at the flips -----------------
    // Proven at the CALL level (Pruefer's standard): the
    // repository's player seam is replaced by a counter, and each
    // flip must emit exactly one knock request — false (release)
    // at the entry, true (engage) at the exit, from BOTH exits.
    // On trunk the seam does not exist and no knock fires at all.

    @Test
    fun breakEntry_firesOneReleaseKnock_andCountdownStaysSilent() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val knocks = mutableListOf<Boolean>()
        repository.breakBoundaryKnock = { engaged -> knocks.add(engaged) }
        clearForeignActiveSessions(db)
        // A block already past its target by every derivation
        // (stored elapsed and both anchors agree), one block of
        // two: the next progress pass must enter the break.
        val nowMono = SystemClock.elapsedRealtime()
        val studying = FocusSessionEntity(
            intention = "BREAK VERBS",
            profileName = "Manual",
            targetDurationSeconds = target,
            elapsedSeconds = target + 2000,
            startWallTimeMillis = System.currentTimeMillis() - (target + 2000) * 1000L,
            startElapsedRealtimeMillis = nowMono - (target + 2000) * 1000L,
            lastTickElapsedRealtimeMillis = nowMono,
            totalStudyBlocks = 2,
            currentBlockIndex = 1,
            isInSafeBreak = false,
            isActive = true,
            completedSuccessfully = false
        )
        val id = dao.insertSession(studying).toInt()
        try {
            val entered = repository.updateSessionProgress(dao.getSessionById(id)!!)
            assertTrue(entered.isInSafeBreak)
            assertEquals(listOf(false), knocks)
            // A countdown tick crosses no boundary: still one.
            val ticked = repository.updateSessionProgress(entered)
            assertTrue(ticked.isInSafeBreak)
            assertEquals(listOf(false), knocks)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun breakExpiry_firesOneEngageKnock() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val knocks = mutableListOf<Boolean>()
        repository.breakBoundaryKnock = { engaged -> knocks.add(engaged) }
        val nowMono = SystemClock.elapsedRealtime()
        val id = dao.insertSession(
            breakRow(storedRemaining = 600, lastTickMono = nowMono - 601_000L)
        ).toInt()
        try {
            val flipped = repository.updateSessionProgress(dao.getSessionById(id)!!)
            assertFalse(flipped.isInSafeBreak)
            assertEquals(listOf(true), knocks)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun breakVerb_firesTheSameEngageKnock() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val knocks = mutableListOf<Boolean>()
        repository.breakBoundaryKnock = { engaged -> knocks.add(engaged) }
        clearForeignActiveSessions(db)
        val id = dao.insertSession(breakRow()).toInt()
        try {
            repository.endSafeBreakEarly(id)
            // The hand's exit and the timer's exit are the same
            // boundary: the same single engage knock.
            assertEquals(listOf(true), knocks)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun countdownBranch_doesNotBookTaken() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        // Mid-break, seconds still standing: progress rewrites the
        // derived pair and books NOTHING — a break is booked when
        // it exits, never while it runs.
        val id = dao.insertSession(breakRow(storedRemaining = 360)).toInt()
        try {
            val out = repository.updateSessionProgress(dao.getSessionById(id)!!)
            assertTrue(out.isInSafeBreak)
            assertEquals(0, out.breakTakenSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }
}
