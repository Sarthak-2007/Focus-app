package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #261 (the transferred W9 DAYLIGHT DS3 finding) at the
 * repository — the Safe Break's missing entry door.
 *
 * The charge, verbatim in substance: a Safe Break could only
 * start when the machine's block ended — never mid-block, when
 * he needs it. The only entry point was the block-flip tick in
 * updateSessionProgress; no UI verb, ViewModel method, or Deck
 * chip started a break mid-block, while ending one early by hand
 * was fully wired — the asymmetry was the break.
 *
 * These tests pin the cure:
 *  1. the entry verb freezes the block (elapsed booked at the
 *     tap, index untouched), marks the break's mid-block origin,
 *     stamps its planned length into the span price, and arms it
 *     with the full length in force;
 *  2. BOTH exits — the hand's early end and the countdown's
 *     expiry flip — resume the SAME block from the frozen figure
 *     (index stands, elapsed stands, the verified clock counts
 *     on from the bank) instead of advancing past it;
 *  3. the taken ledger books what actually ran, and the span is
 *     CONTINUOUS across entry and exit;
 *  4. the verb refuses quietly where there is nothing to enter:
 *     already in a break, a finished block (the flip's own
 *     territory), a stopwatch session, a forged row, a wrong id;
 *  5. the entry and the resume speak the same single boundary
 *     knocks the flip-born entry/exit speak (ST-03).
 *
 * Rows are signed the way the seal signs them (the reboot
 * suite's precedent): the entry verb reads the verified clock,
 * and an unsigned row is a forged row to it.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MidBlockSafeBreakRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var dao: com.example.data.MonolithDao
    private lateinit var repository: MonolithRepository

    private val target = 1500 // one 25-minute block
    private val breakLength = 600 // the length in force

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.monolithDao()
        repository = MonolithRepository(context, dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** A signed, studying row [elapsed] seconds into block
     *  [blockIndex] of [blocks], anchors agreeing to the second. */
    private fun studyingRow(
        elapsed: Int = 600,
        blocks: Int = 2,
        blockIndex: Int = 1,
        stopwatch: Boolean = false,
        forged: Boolean = false
    ): FocusSessionEntity {
        val nowMono = SystemClock.elapsedRealtime()
        val startMono = nowMono - elapsed * 1000L
        val startWall = System.currentTimeMillis() - elapsed * 1000L
        val hmac = if (forged) "forged" else CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = startWall,
            startElapsedRealtimeMillis = startMono,
            bootCount = 0,
            targetDurationSeconds = target,
            isStopwatch = stopwatch
        )
        return FocusSessionEntity(
            intention = "MID-BLOCK BREAK",
            profileName = "Manual",
            targetDurationSeconds = target,
            elapsedSeconds = elapsed,
            startWallTimeMillis = startWall,
            startElapsedRealtimeMillis = startMono,
            bootCount = 0,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = hmac,
            totalStudyBlocks = blocks,
            currentBlockIndex = blockIndex,
            isInSafeBreak = false,
            isStopwatchMode = stopwatch,
            isActive = true,
            completedSuccessfully = false
        )
    }

    @Test
    fun entry_freezesTheBlock_andMarksTheOrigin() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        val entered = repository.startSafeBreakMidBlock(id)!!
        assertTrue(entered.isInSafeBreak)
        assertTrue(entered.midBlockSafeBreak)
        assertEquals(breakLength, entered.safeBreakSecondsRemaining)
        // The block froze — it did not finish, and nothing advanced.
        assertEquals(600, entered.elapsedSeconds)
        assertEquals(1, entered.currentBlockIndex)
        assertTrue(entered.isActive)
        // The planned span prices the extra break slot:
        // 2 blocks + 1 scheduled break + 1 mid-block break.
        assertEquals(breakLength, entered.extraSafeBreakSeconds)
        assertEquals(2 * target + 2 * breakLength, entered.spanTargetSeconds)
        // The lived span at the door: the frozen 600, nothing more.
        assertEquals(600, entered.spanElapsedSeconds)
        // And the write is the stored fact, not a returned echo.
        val persisted = dao.getSessionById(id)!!
        assertTrue(persisted.isInSafeBreak)
        assertTrue(persisted.midBlockSafeBreak)
    }

    @Test
    fun countdownTick_holdsTheFrozenBlock() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        val entered = repository.startSafeBreakMidBlock(id)!!
        val ticked = repository.updateSessionProgress(entered)
        assertTrue(ticked.isInSafeBreak)
        assertTrue(ticked.midBlockSafeBreak)
        assertEquals(600, ticked.elapsedSeconds)
        assertEquals(1, ticked.currentBlockIndex)
        assertEquals(0, ticked.breakTakenSeconds)
    }

    @Test
    fun endEarly_resumesTheSameBlock_andBooksOnlyTheTakenPart() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        val entered = repository.startSafeBreakMidBlock(id)!!
        // 240 of the break's 600 seconds run (the stored pair as a
        // tick would leave it), then the hand ends it.
        dao.updateSession(entered.copy(safeBreakSecondsRemaining = 360))
        val resumed = repository.endSafeBreakEarly(id)!!
        assertFalse(resumed.isInSafeBreak)
        assertFalse(resumed.midBlockSafeBreak)
        // The SAME block resumes: index and frozen elapsed stand.
        assertEquals(1, resumed.currentBlockIndex)
        assertEquals(600, resumed.elapsedSeconds)
        assertTrue(resumed.isActive)
        // The ledger books the 240 that ran — never the 600.
        assertEquals(240, resumed.breakTakenSeconds)
        // Continuity: 600 frozen study + 240 taken break, and the
        // span target still carries the planned extra slot.
        assertEquals(600 + 240, resumed.spanElapsedSeconds)
        assertEquals(2 * target + 2 * breakLength, resumed.spanTargetSeconds)
        // The resumed block's verified clock counts FROM the bank:
        // a progress pass right after the resume still reads the
        // frozen figure, not the break's seconds as focus.
        val ticked = repository.updateSessionProgress(resumed)
        assertFalse(ticked.isInSafeBreak)
        assertEquals(600, ticked.elapsedSeconds)
        assertEquals(1, ticked.currentBlockIndex)
    }

    @Test
    fun expiry_resumesTheSameBlock_andBooksTheFullLength() = runBlocking {
        // An in-flight mid-block break as the last tick left it
        // just past its end: remaining 600 stamped 601 s ago.
        val nowMono = SystemClock.elapsedRealtime()
        val entered = repository.startSafeBreakMidBlock(
            dao.insertSession(studyingRow(elapsed = 600)).toInt()
        )!!
        val inFlight = entered.copy(
            safeBreakSecondsRemaining = breakLength,
            lastTickElapsedRealtimeMillis = nowMono - (breakLength + 1) * 1000L
        )
        dao.updateSession(inFlight)
        val resumed = repository.updateSessionProgress(inFlight)
        assertFalse(resumed.isInSafeBreak)
        assertFalse(resumed.midBlockSafeBreak)
        assertEquals(1, resumed.currentBlockIndex)
        assertEquals(600, resumed.elapsedSeconds)
        assertEquals(breakLength, resumed.breakTakenSeconds)
        assertEquals(600 + breakLength, resumed.spanElapsedSeconds)
    }

    @Test
    fun entry_refusesWhenAlreadyInABreak() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        repository.startSafeBreakMidBlock(id)
        val again = repository.startSafeBreakMidBlock(id)!!
        assertTrue(again.isInSafeBreak)
        // No second entry: the planned extra is stamped once.
        assertEquals(breakLength, again.extraSafeBreakSeconds)
        assertEquals(breakLength, dao.getSessionById(id)!!.extraSafeBreakSeconds)
    }

    @Test
    fun entry_refusesWhenTheBlockAlreadyFinished() = runBlocking {
        // Anchors past the target by 10 s: the crossing belongs to
        // the progress machine's flip/finish, not this door.
        val id = dao.insertSession(studyingRow(elapsed = target + 10)).toInt()
        val out = repository.startSafeBreakMidBlock(id)!!
        assertFalse(out.isInSafeBreak)
        assertEquals(0, out.extraSafeBreakSeconds)
    }

    @Test
    fun entry_refusesAStopwatchSession() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600, stopwatch = true)).toInt()
        val out = repository.startSafeBreakMidBlock(id)!!
        assertFalse(out.isInSafeBreak)
        assertEquals(0, out.extraSafeBreakSeconds)
    }

    @Test
    fun entry_refusesAForgedRow() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600, forged = true)).toInt()
        val out = repository.startSafeBreakMidBlock(id)!!
        assertFalse(out.isInSafeBreak)
        assertEquals(0, out.extraSafeBreakSeconds)
    }

    @Test
    fun entry_wrongId_leavesTheSessionStudying() = runBlocking {
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        repository.startSafeBreakMidBlock(id + 999)
        val stored = dao.getSessionById(id)!!
        assertFalse(stored.isInSafeBreak)
        assertEquals(0, stored.extraSafeBreakSeconds)
    }

    @Test
    fun entryAndResume_speakTheBoundaryKnocksOnceEach() = runBlocking {
        val knocks = mutableListOf<Boolean>()
        repository.breakBoundaryKnock = { engaged -> knocks.add(engaged) }
        val id = dao.insertSession(studyingRow(elapsed = 600)).toInt()
        repository.startSafeBreakMidBlock(id)
        // The hand's entry speaks the release knock, exactly once —
        // the same voice the flip's entry speaks (ST-03).
        assertEquals(listOf(false), knocks)
        repository.endSafeBreakEarly(id)
        assertEquals(listOf(false, true), knocks)
    }

    @Test
    fun singleBlockSession_canTakeAMidBlockBreak() = runBlocking {
        // The filing's own frame: one long block (his 3-hour span
        // is a single block here) — the door must not require a
        // next block to exist.
        val id = dao.insertSession(studyingRow(elapsed = 900, blocks = 1)).toInt()
        val entered = repository.startSafeBreakMidBlock(id)!!
        assertTrue(entered.isInSafeBreak)
        assertEquals(target + breakLength, entered.spanTargetSeconds)
        val resumed = repository.endSafeBreakEarly(id)!!
        assertEquals(1, resumed.currentBlockIndex)
        assertEquals(900, resumed.elapsedSeconds)
        assertTrue(resumed.isActive)
    }
}
