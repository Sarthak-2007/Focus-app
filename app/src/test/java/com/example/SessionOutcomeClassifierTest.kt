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
 * gap). Issue #524 struck #95's install-time term out of the
 * reference: update downtime past the 10 s threshold books the
 * gap; under the threshold the threshold itself is the exemption.
 * A beat of 0 is a pre-v10 row: the beat test alone cannot
 * convict it; only the latch can.
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
        usageStatsPermissionHeld: Boolean = true,
        openExposureSeen: Boolean = false
    ) = classifyElapsedOutcome(
        session = s,
        allBlocksCompleted = completed,
        isRoutineSession = routine,
        verifiedElapsedSeconds = verifiedElapsedSeconds,
        usageStatsPermissionHeld = usageStatsPermissionHeld,
        openExposureSeen = openExposureSeen
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
    fun completed_updateDowntimePastThreshold_isAGap() {
        // #524 struck #95's blanket exemption (this test's old
        // form pinned it): the reference no longer includes the
        // build's install time, so a beat that predates an update
        // is measured from the beat itself — here 130 s stale
        // against the implied now (start + 150 s) — and minutes
        // of install downtime book the gap.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 20_000L)
        assertEquals(SessionOutcome.ELAPSED_SHIELD_GAP, classify(s))
        // What survives of #95's intent: downtime UNDER the 10 s
        // threshold stays exempt — by the threshold itself, not
        // by an install-time term. A beat 5 s behind the implied
        // now (the shape a brief install leaves) stays clean.
        val brief = session(startWall = start, beatWall = start + 145_000L)
        assertEquals(SessionOutcome.ELAPSED_CLEAN, classify(brief))
    }

    @Test
    fun completed_noUsagePermission_isGapEvenWithFreshBeat() {
        // #81 F4: a finish may certify CLEAN only if the shield
        // can still see at the ending write.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 145_000L)
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classify(s, usageStatsPermissionHeld = false)
        )
    }

    @Test
    fun completed_openExposure_booksExposureBelowGap() {
        // #129 F3: an unguarded door used inside the window is
        // neither clean nor a shield gap — it books EXPOSURE,
        // and only when the gap terms do not fire first.
        val start = 1_000_000L
        val s = session(startWall = start, beatWall = start + 145_000L)
        assertEquals(
            SessionOutcome.ELAPSED_OPEN_EXPOSURE,
            classify(s, openExposureSeen = true)
        )
        val latched = session(startWall = start, beatWall = start + 145_000L, gapSeen = true)
        assertEquals(
            SessionOutcome.ELAPSED_SHIELD_GAP,
            classify(latched, openExposureSeen = true)
        )
    }
}
