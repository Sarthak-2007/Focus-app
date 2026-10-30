package com.example

import com.example.data.FocusSessionEntity
import com.example.data.SessionOutcome
import com.example.data.classifyElapsedOutcome
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #90 (complexity F5): characterisation tests for the
 * outcome classifier extracted out of updateSessionProgress.
 * Re-pinned at the #90 merge onto the composed #82/#95 logic the
 * extraction now carries: a session that has not completed keeps
 * its standing outcome, a routine row keeps its standing outcome,
 * and a completed manual session is ELAPSED_CLEAN only while the
 * shield's beat is fresh against the monotonic-implied now (the
 * session start advanced by the VERIFIED elapsed — never the raw
 * wall clock, so clock travel can neither forge nor launder a
 * gap). The reference never sits before the current build landed
 * (update downtime is not a gap), and a beat of 0 is a pre-v10
 * row: the beat test alone cannot convict it; only the latch can.
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

    private fun classify(
        s: FocusSessionEntity,
        completed: Boolean = true,
        routine: Boolean = false,
        verifiedElapsedSeconds: Long = 150L,
        appLastUpdateMillis: Long = 0L
    ) = classifyElapsedOutcome(
        session = s,
        allBlocksCompleted = completed,
        isRoutineSession = routine,
        verifiedElapsedSeconds = verifiedElapsedSeconds,
        appLastUpdateMillis = appLastUpdateMillis
    )

    @Test
    fun notCompleted_keepsStandingOutcome() {
        val s = session(outcome = "STANDING")
        assertEquals("STANDING", classify(s, completed = false))
    }

    @Test
    fun routineSession_keepsStandingOutcome() {
        // Even with a stale beat and the latch set: a routine
        // row's judgement is routineOutcome, never this stamp.
        val s = session(beatWall = 0L, gapSeen = true, outcome = "STANDING")
        assertEquals("STANDING", classify(s, routine = true))
    }

    @Test
    fun completed_freshBeat_isClean() {
        val start = 1_000_000L
        // Implied now = start + 150s; the beat landed 5s before it.
        val s = session(startWall = start, beatWall = start + 145_000L)
        assertEquals(SessionOutcome.ELAPSED_CLEAN, classify(s))
    }

    @Test
    fun completed_beatExactlyAtLaw_isClean() {
        // The law is strictly greater-than: a beat exactly 10s
        // behind the implied now has not crossed it.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 140_000L)
        assertEquals(SessionOutcome.ELAPSED_CLEAN, classify(s))
    }

    @Test
    fun completed_beatPastLaw_isGap() {
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 139_999L)
        assertEquals(SessionOutcome.ELAPSED_SHIELD_GAP, classify(s))
    }

    @Test
    fun completed_latchSet_isGapEvenWithFreshBeat() {
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 149_000L, gapSeen = true)
        assertEquals(SessionOutcome.ELAPSED_SHIELD_GAP, classify(s))
    }

    @Test
    fun completed_noBeatEverLanded_cannotConvictOnBeatAlone() {
        // #95: a beat of 0 is a pre-v10 row — no beat evidence
        // exists, so even a long verified elapsed stays CLEAN
        // unless the latch was set.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = 0L)
        assertEquals(
            SessionOutcome.ELAPSED_CLEAN,
            classify(s, verifiedElapsedSeconds = 1500L)
        )
        val latched = session(startWall = start, beatWall = 0L, gapSeen = true)
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classify(latched, verifiedElapsedSeconds = 1500L)
        )
    }

    @Test
    fun completed_updateDowntime_isNotAGap() {
        // #95: the reference includes the current build's install
        // time — a beat that predates the update is measured from
        // the update, not from the beat itself.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 20_000L)
        assertEquals(
            SessionOutcome.ELAPSED_CLEAN,
            classify(s, appLastUpdateMillis = start + 140_000L)
        )
        // Same facts without the update term: the beat is 130s
        // stale against the implied now and the gap books.
        assertEquals(SessionOutcome.ELAPSED_SHIELD_GAP, classify(s))
    }
}
