package com.example

import com.example.data.FocusSessionEntity
import com.example.data.SessionOutcome
import com.example.data.classifyElapsedOutcome
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #90 (complexity F5): characterisation tests for the
 * outcome classifier extracted out of updateSessionProgress
 * (Slice E, Nocke §2.3). These pin the classification exactly as
 * the inline code computed it on the tick hot path: a session
 * that has not completed keeps its standing outcome, a routine
 * row keeps its standing outcome, and a completed manual session
 * is ELAPSED_CLEAN only while the shield's beat is fresh — the
 * latch ever set, or a beat older than the 10s law measured from
 * the later of the beat and the session's start, books the gap.
 */
class SessionOutcomeClassifierTest {

    private fun session(
        startWall: Long = 1_000_000L,
        beatWall: Long = 0L,
        gapSeen: Boolean = false,
        outcome: String = ""
    ) = FocusSessionEntity(
        intention = "CLASSIFY",
        profileName = "Manual",
        targetDurationSeconds = 1500,
        elapsedSeconds = 1500,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = 0L,
        isActive = true,
        completedSuccessfully = false,
        shieldLastBeatWallMillis = beatWall,
        shieldGapSeen = gapSeen,
        outcome = outcome
    )

    @Test
    fun notCompleted_keepsStandingOutcome() {
        val s = session(outcome = "STANDING")
        assertEquals(
            "STANDING",
            classifyElapsedOutcome(s, allBlocksCompleted = false,
                isRoutineSession = false, nowWallMillis = 9_999_999L)
        )
    }

    @Test
    fun routineSession_keepsStandingOutcome() {
        // Even with a stale beat and the latch set: a routine
        // row's judgement is routineOutcome, never this stamp.
        val s = session(beatWall = 0L, gapSeen = true, outcome = "STANDING")
        assertEquals(
            "STANDING",
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = true, nowWallMillis = 9_999_999L)
        )
    }

    @Test
    fun completed_freshBeat_isClean() {
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 5_000L)
        assertEquals(
            SessionOutcome.ELAPSED_CLEAN,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 14_999L)
        )
    }

    @Test
    fun completed_beatExactlyAtLaw_isClean() {
        // The law is strictly greater-than: a beat exactly 10s
        // old has not crossed it.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start)
        assertEquals(
            SessionOutcome.ELAPSED_CLEAN,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 10_000L)
        )
    }

    @Test
    fun completed_beatPastLaw_isGap() {
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start)
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 10_001L)
        )
    }

    @Test
    fun completed_latchSet_isGapEvenWithFreshBeat() {
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 9_000L, gapSeen = true)
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 9_500L)
        )
    }

    @Test
    fun completed_gapMeasuredFromSessionStartWhenNoBeat() {
        // No beat ever landed (beatWall 0): the reference is the
        // session's own start, never the epoch.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = 0L)
        assertEquals(
            SessionOutcome.ELAPSED_CLEAN,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 5_000L)
        )
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classifyElapsedOutcome(s, allBlocksCompleted = true,
                isRoutineSession = false, nowWallMillis = start + 10_001L)
        )
    }
}
