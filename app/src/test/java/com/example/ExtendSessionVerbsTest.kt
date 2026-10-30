package com.example

import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.CryptoIntegrityManager
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
 * WELLE-7 compile repair pin: TWO session-extend verbs, two
 * features, one former name.
 *
 *  - extendActiveSession(extraMinutes) — Issue #287 F7, merged
 *    FIRST (PR #460, 2026-10-30T09:51Z). Takes minutes, returns
 *    the updated row (null when no session is active; the row
 *    unchanged when extraMinutes <= 0 or stopwatch), uncapped,
 *    re-signs in the same write. It keeps the name.
 *  - extendActiveSessionCapped(addSeconds = 900) — Issue #283,
 *    merged LATER (PR #413, 2026-10-30T10:01Z). Takes seconds,
 *    answers Boolean, REFUSES while an hour or more remains and
 *    refuses stopwatch sessions, re-signs in the same write.
 *    Renamed in the repair; behaviour byte-for-byte the old
 *    second overload's.
 *
 * Before the repair the pair was a conflicting-overloads compile
 * break on main. These tests pin that BOTH verbs exist under
 * distinct names and keep their distinct semantics — a future
 * merge that re-collides or silently swaps them fails here.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExtendSessionVerbsTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun clearForeignActiveSessions(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL(
            "UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1"
        )
    }

    /** A session that has just started: both anchors at now, so
     *  the verified elapsed the capped verb prices is ~0 and the
     *  remaining time is the target itself. */
    private fun activeRow(
        target: Int = 1500,
        stopwatch: Boolean = false
    ): FocusSessionEntity {
        val nowMono = SystemClock.elapsedRealtime()
        return FocusSessionEntity(
            intention = "EXTEND VERBS",
            profileName = "Manual",
            targetDurationSeconds = target,
            elapsedSeconds = 0,
            startWallTimeMillis = System.currentTimeMillis(),
            startElapsedRealtimeMillis = nowMono,
            lastTickElapsedRealtimeMillis = nowMono,
            isStopwatchMode = stopwatch,
            isActive = true,
            completedSuccessfully = false
        )
    }

    // ---- the kept verb: extendActiveSession(extraMinutes), #287 --

    @Test
    fun keptVerb_extendsByMinutes_returnsRow_andResigns() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(activeRow(target = 1500)).toInt()
        try {
            val updated = repository.extendActiveSession(10)
            assertNotNull(updated)
            assertEquals(1500 + 600, updated!!.targetDurationSeconds)
            val persisted = dao.getSessionById(id)!!
            assertEquals(2100, persisted.targetDurationSeconds)
            assertTrue(persisted.hmacSignature.isNotBlank())
            assertTrue(CryptoIntegrityManager.verifySessionState(persisted))
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun keptVerb_nonPositiveMinutes_returnsRowUnchanged() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(activeRow(target = 1500)).toInt()
        try {
            val out = repository.extendActiveSession(0)
            assertNotNull(out)
            assertEquals(1500, out!!.targetDurationSeconds)
            assertEquals(1500, dao.getSessionById(id)!!.targetDurationSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun keptVerb_uncapped_extendsEvenWithHoursRemaining() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        // 2 hours standing: the #287 verb has no 60-minute cap —
        // that refusal belongs ONLY to the capped #283 verb.
        val id = dao.insertSession(activeRow(target = 7200)).toInt()
        try {
            val updated = repository.extendActiveSession(5)
            assertEquals(7200 + 300, updated!!.targetDurationSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    // ---- the renamed verb: extendActiveSessionCapped, #283 -------

    @Test
    fun cappedVerb_defaultExtendsBy900Seconds_andResigns() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(activeRow(target = 1500)).toInt()
        try {
            assertTrue(repository.extendActiveSessionCapped())
            val persisted = dao.getSessionById(id)!!
            assertEquals(1500 + 900, persisted.targetDurationSeconds)
            assertTrue(persisted.hmacSignature.isNotBlank())
            assertTrue(CryptoIntegrityManager.verifySessionState(persisted))
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun cappedVerb_refusesWhileAnHourOrMoreRemains() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(activeRow(target = 3600)).toInt()
        try {
            assertFalse(repository.extendActiveSessionCapped())
            assertEquals(3600, dao.getSessionById(id)!!.targetDurationSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun cappedVerb_refusesStopwatch() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        val id = dao.insertSession(activeRow(target = 1500, stopwatch = true)).toInt()
        try {
            assertFalse(repository.extendActiveSessionCapped())
            assertEquals(1500, dao.getSessionById(id)!!.targetDurationSeconds)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    // ---- both verbs with no active session ------------------------

    @Test
    fun noActiveSession_keptReturnsNull_cappedReturnsFalse() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)
        assertNull(repository.extendActiveSession(10))
        assertFalse(repository.extendActiveSessionCapped())
    }
}
