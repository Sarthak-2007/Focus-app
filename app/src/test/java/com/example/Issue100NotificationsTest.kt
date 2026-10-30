package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.service.NotificationHoldCache
import com.example.service.SNOOZE_CHUNK_MILLIS
import com.example.service.sessionEndWallMillis
import com.example.service.snoozeDurationMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #100 (notifications-lived) — STURM3-08: with notification
 * content storage CUT (Sarthak's ruling), holding is a snooze of
 * the ORIGINAL notification. These tests pin the snooze maths and
 * the fast-path cache: a timed session snoozes to its own end (so
 * the original resurfaces, actions intact), a stopwatch or ended
 * session snoozes in chunks, and the cache only ever judges
 * inside the window it was fed — null outside it, never a guess.
 */
class Issue100NotificationsTest {

    private fun session(
        start: Long = 1_000_000L,
        blocks: Int = 2,
        targetSeconds: Int = 3000,
        penalty: Int = 0,
        stopwatch: Boolean = false,
        endWall: Long = 0L
    ) = FocusSessionEntity(
        intention = "Study", profileName = "P",
        targetDurationSeconds = targetSeconds, elapsedSeconds = 0,
        startWallTimeMillis = start, startElapsedRealtimeMillis = 0L,
        isStopwatchMode = stopwatch, totalStudyBlocks = blocks,
        penaltySecondsAdded = penalty,
        isActive = true, completedSuccessfully = false,
        endWallMillis = endWall
    )

    @Test fun sessionEndMirrorsTheDaoSpanFormula() {
        // 2 blocks x 3000s + one 600s gap = 6600s after start.
        assertEquals(1_000_000L + 6_600_000L, sessionEndWallMillis(session()))
        // A recorded end wins over the formula.
        assertEquals(42L, sessionEndWallMillis(session(endWall = 42L)))
        // Penalty extends the span per block.
        assertEquals(
            1_000_000L + (2 * 3100 + 600) * 1000L,
            sessionEndWallMillis(session(penalty = 100))
        )
    }

    @Test fun timedSessionSnoozesToItsOwnEnd() {
        val s = session()
        val now = 1_000_000L + 600_000L // 10 minutes in
        assertEquals(6_000_000L, snoozeDurationMillis(s, now))
    }

    @Test fun stopwatchAndEndedSessionsSnoozeInChunks() {
        assertEquals(SNOOZE_CHUNK_MILLIS, snoozeDurationMillis(session(stopwatch = true), 1_000_000L))
        assertEquals(SNOOZE_CHUNK_MILLIS, snoozeDurationMillis(session(), 99_000_000L))
    }

    @Test fun cacheJudgesOnlyInsideItsWindow() {
        val s = session()
        val apps = listOf(
            BlockedAppEntity(
                packageName = "com.chat", appName = "Chat", category = "MESSAGING",
                isBlocked = true, blockTier = "LOCKED"
            ),
            BlockedAppEntity(
                packageName = "com.open", appName = "Open", category = "PRODUCTIVITY",
                isBlocked = false, blockTier = "OPEN"
            )
        )
        NotificationHoldCache.update(s, apps)
        assertEquals(AppBlockTier.LOCKED, NotificationHoldCache.ruleCovering("com.chat", 2_000_000L)?.tier)
        assertEquals(AppBlockTier.OPEN, NotificationHoldCache.ruleCovering("com.open", 2_000_000L)?.tier)
        assertNull(NotificationHoldCache.ruleCovering("com.chat", 500L))
        assertNull(NotificationHoldCache.ruleCovering("com.chat", 99_000_000L))
        // The cache's own snooze maths agrees with the free function.
        assertEquals(6_000_000L, NotificationHoldCache.snoozeDuration(1_600_000L))
        NotificationHoldCache.clear()
        assertNull(NotificationHoldCache.ruleCovering("com.chat", 2_000_000L))
    }
}
