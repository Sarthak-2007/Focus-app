package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * getSessionCoveringInstant, the post-time session decision the
 * notification listener's snooze hold is built on, exercised
 * against the real Room database.
 *
 * This file is what survives of Issue #45's NotificationRetention-
 * DaoTest: the retention/prune/matching-delete tests pinned the
 * captured_notifications store, which Issue #100's CUT deleted
 * (table dropped by MIGRATION_18_19); the covering-instant tests
 * pin a query that still exists and now carries the hold's
 * session-end maths, so they stay.
 *
 * Session instants use a fixed 2023-era base so rows other suites
 * leave behind (started at real "now", far later) can never cover
 * them and pollute an assertion.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SessionCoveringDaoTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dao get() = MonolithDatabase.getInstance(context).monolithDao()

    @Before
    fun clearSessions() = runBlocking {
        // Carried from main's #478 revival of these tests (Oct 31):
        // the covering-instant tests assert about the whole
        // focus_sessions table in the shared singleton DB, and other
        // suites leave session rows behind — including ACTIVE rows
        // with startWallTimeMillis = 0, which the covering predicate
        // (isActive = 1) reads as covering every instant since the
        // epoch. Clear sessions first, so every test here starts
        // from the empty world it asserts about.
        dao.getSessionHistorySnapshot().forEach { dao.deleteSessionById(it.id) }
    }

    private suspend fun insertSession(
        start: Long,
        targetSeconds: Int,
        elapsedSeconds: Int,
        isActive: Boolean,
        endWallMillis: Long
    ): Long = dao.insertSession(
        FocusSessionEntity(
            intention = "Covering instant test",
            profileName = "Covering",
            targetDurationSeconds = targetSeconds,
            elapsedSeconds = elapsedSeconds,
            startWallTimeMillis = start,
            startElapsedRealtimeMillis = 0L,
            isActive = isActive,
            completedSuccessfully = !isActive && endWallMillis > 0L,
            endWallMillis = endWallMillis
        )
    )

    @Test
    fun endedSessionCoversOnlyUpToItsStampedEnd() = runBlocking {
        val start = 1_700_000_000_000L
        val id = insertSession(
            start = start,
            targetSeconds = 50 * 60,
            elapsedSeconds = 10 * 60,
            isActive = false,
            endWallMillis = start + 10 * 60_000L
        )
        val inside = dao.getSessionCoveringInstant(start + 5 * 60_000L)
        assertEquals(id, inside?.id?.toLong())
        // After the true end the same row must not cover, even
        // though the bare target (50 min) has not yet elapsed —
        // this is the exactness the endWallMillis stamp buys.
        assertNull(dao.getSessionCoveringInstant(start + 20 * 60_000L))
        assertNull(dao.getSessionCoveringInstant(start - 1_000L))
    }

    @Test
    fun legacyEndedSessionCoversUpToItsSpanBound() = runBlocking {
        // Spaced far from the stamped-end test's window so the two
        // rows can never cover each other's instants.
        val start = 1_700_100_000_000L
        val id = insertSession(
            start = start,
            targetSeconds = 60 * 60,
            elapsedSeconds = 30 * 60,
            isActive = false,
            endWallMillis = 0L
        )
        // Span bound for one block = start + 3600 s.
        val inside = dao.getSessionCoveringInstant(start + 50 * 60_000L)
        assertEquals(id, inside?.id?.toLong())
        assertNull(dao.getSessionCoveringInstant(start + 70 * 60_000L))
    }
}
