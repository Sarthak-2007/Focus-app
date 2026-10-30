package com.example

import com.example.data.ForegroundEventStream
import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #290 (Audit R5 supplement — Forest, second pass): F4's
 * event-stream fold and F1's stopwatch terminal law constants.
 * Every test states the world after the cure: before it, the
 * shield door had no event-stream path at all and FLOW had no cap.
 */
class ForegroundEventStreamTest {

    private fun ev(pkg: String, type: Int, at: Long) =
        ForegroundEventStream.Event(pkg, type, at)

    private val blocked = setOf("com.social.app", "com.video.app")

    @Test
    fun resumedCandidateIsForeground() {
        val events = listOf(ev("com.social.app", ForegroundEventStream.ACTIVITY_RESUMED, 100L))
        assertEquals("com.social.app", ForegroundEventStream.foregroundPackage(events, blocked))
    }

    @Test
    fun pausedCandidateIsNotForeground() {
        val events = listOf(
            ev("com.social.app", ForegroundEventStream.ACTIVITY_RESUMED, 100L),
            ev("com.social.app", ForegroundEventStream.ACTIVITY_PAUSED, 200L)
        )
        assertNull(ForegroundEventStream.foregroundPackage(events, blocked))
    }

    @Test
    fun platformTransitionOrderKeepsTheNewApp() {
        // A pauses, B resumes, A stops — B is foreground, and A's
        // late STOPPED must not evict it.
        val events = listOf(
            ev("com.social.app", ForegroundEventStream.ACTIVITY_PAUSED, 100L),
            ev("com.video.app", ForegroundEventStream.ACTIVITY_RESUMED, 110L),
            ev("com.social.app", ForegroundEventStream.ACTIVITY_STOPPED, 120L)
        )
        assertEquals("com.video.app", ForegroundEventStream.foregroundPackage(events, blocked))
    }

    @Test
    fun nonCandidateEventsNeverMoveTheAnswer() {
        // System UI / shade / launcher churn is detection noise.
        val events = listOf(
            ev("com.android.systemui", ForegroundEventStream.ACTIVITY_RESUMED, 100L),
            ev("com.social.app", ForegroundEventStream.ACTIVITY_RESUMED, 110L),
            ev("com.android.systemui", ForegroundEventStream.ACTIVITY_RESUMED, 120L)
        )
        assertEquals("com.social.app", ForegroundEventStream.foregroundPackage(events, blocked))
    }

    @Test
    fun unorderedInputFoldsChronologically() {
        val events = listOf(
            ev("com.social.app", ForegroundEventStream.ACTIVITY_PAUSED, 300L),
            ev("com.social.app", ForegroundEventStream.ACTIVITY_RESUMED, 100L)
        )
        assertNull(ForegroundEventStream.foregroundPackage(events, blocked))
    }

    @Test
    fun emptyStreamAndEmptyCandidatesYieldNull() {
        assertNull(ForegroundEventStream.foregroundPackage(emptyList(), blocked))
        assertNull(
            ForegroundEventStream.foregroundPackage(
                listOf(ev("com.social.app", ForegroundEventStream.ACTIVITY_RESUMED, 1L)),
                emptySet()
            )
        )
    }

    @Test
    fun stopwatchTerminalLawConstantsExistAndAreOrdered() {
        assertTrue(MonolithRepository.STOPWATCH_MAX_CAP_SECONDS > MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS)
        assertEquals(4 * 60 * 60, MonolithRepository.STOPWATCH_MAX_CAP_SECONDS)
        assertEquals("STOPWATCH_WITHERED", SessionOutcome.STOPWATCH_WITHERED)
    }
}
