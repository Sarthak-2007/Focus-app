package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CapturedNotificationEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #45 (C5): the DAO half of the notification-listener cures,
 * exercised against the real Room database (the same singleton the
 * listener writes through):
 * - the retention deletes (age prune + newest-N trim),
 * - the stored-row identity match (package + postTime, since the
 *   issue #138 CUT removed stored content) behind dedupe-replace
 *   and the removal delete (a recalled message must not resurrect),
 * - getSessionCoveringInstant, the post-time session decision.
 *
 * Session instants use a fixed 2023-era base so rows other suites
 * leave behind (started at real "now", far later) can never cover
 * them and pollute an assertion.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// PIN-SCOPE: PLUMBING — DAO retention/identity/covering-instant over real Room (working-rules adoption, Oct 31 18:06 IST).
class NotificationRetentionDaoTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dao get() = MonolithDatabase.getInstance(context).monolithDao()

    @Before
    fun clearCaptured() = runBlocking {
        dao.clearAllCapturedNotifications()
        // FIX-COMPILE (#478 revival, Oct 31): the covering-instant
        // tests assert about the whole focus_sessions table in the
        // shared singleton DB, and other suites leave session rows
        // behind — including ACTIVE rows with startWallTimeMillis
        // = 0, which the covering predicate (isActive = 1) reads
        // as covering every instant since the epoch. That pollution
        // was invisible while this class was dead (the DAO sweep's
        // initializationError); revived, it broke both assertNull
        // covering checks deterministically (run 41165139902: a
        // leftover "Deep Study" row, id=33, startWallTimeMillis=0,
        // was returned for instants the test's own row cannot
        // cover). Clear sessions in @Before as well, so every test
        // here starts from the empty world it asserts about. Each
        // covering test inserts its own row inside the test.
        dao.getSessionHistorySnapshot().forEach { dao.deleteSessionById(it.id) }
    }

    // Issue #138 CUT: a capture's identity is package + timestamp
    // (there is no stored title/body to distinguish rows by), so
    // the fixtures below tell rows apart by their timestamps.
    private fun captured(ts: Long, pkg: String = "com.example.chat") =
        CapturedNotificationEntity(
            timestampMillis = ts,
            packageName = pkg,
            appName = "Chat"
        )

    @Test
    fun pruneDeletesOnlyRowsOlderThanTheCutoff() = runBlocking {
        val day = 24L * 60 * 60 * 1000
        dao.insertCapturedNotification(captured(1_000L))
        dao.insertCapturedNotification(captured(8 * day))
        dao.insertCapturedNotification(captured(9 * day))

        dao.pruneCapturedNotificationsOlderThan(8 * day)

        val stamps = dao.getCapturedNotificationsSnapshot().map { it.timestampMillis }.toSet()
        assertEquals(setOf(8 * day, 9 * day), stamps)
    }

    @Test
    fun trimKeepsExactlyTheNewestRows() = runBlocking {
        val base = 1_700_000_000_000L
        repeat(205) { i ->
            dao.insertCapturedNotification(captured(base + i * 1_000L))
        }
        dao.trimCapturedNotificationsToNewest(200)

        val rows = dao.getCapturedNotificationsSnapshot()
        assertEquals(200, rows.size)
        val stamps = rows.map { it.timestampMillis }.toSet()
        assertTrue(
            "the five oldest must be trimmed",
            stamps.none { it in (0..4).map { i -> base + i * 1_000L }.toSet() }
        )
        assertTrue(stamps.contains(base + 5 * 1_000L))
        assertTrue(stamps.contains(base + 204 * 1_000L))
    }

    @Test
    fun matchingDeleteRemovesOnlyTheMatchingRow() = runBlocking {
        val ts = 1_700_000_100_000L
        dao.insertCapturedNotification(captured(ts))
        dao.insertCapturedNotification(captured(ts + 1_000L))
        dao.insertCapturedNotification(captured(ts + 2_000L))

        val found = dao.findCapturedNotificationMatching("com.example.chat", ts)
        assertNotNull(found)

        val deleted = dao.deleteCapturedNotificationMatching("com.example.chat", ts)
        assertEquals(1, deleted)

        val rows = dao.getCapturedNotificationsSnapshot()
        assertEquals(2, rows.size)
        assertNull(dao.findCapturedNotificationMatching("com.example.chat", ts))
        // A delete with no matching row is a no-op, not an error.
        assertEquals(0, dao.deleteCapturedNotificationMatching("com.example.chat", ts))
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
