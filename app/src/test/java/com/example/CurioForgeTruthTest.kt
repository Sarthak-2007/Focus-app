package com.example

import com.example.data.FocusSessionEntity
import com.example.data.SessionOutcome
import com.example.ui.screens.CABINET_FORGE_RULE
import com.example.ui.screens.curioForgeGapMark
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R3b #145 — REOPENED truth defects A + B (fixer re-seat,
 * DURCH-D). The #402 closure claimed both lines cured; independent
 * verification found them still standing on main, and this suite
 * pins the cures so the claims cannot silently regress again.
 *
 * Defect A: the cabinet's printed forge rule overclaimed twice —
 * a routine-born span you finish yourself forges nothing (the
 * finalise gate is `allBlocksCompleted && !isRoutineSession`),
 * and a timed span hand-ended before all its blocks complete
 * forges nothing (at most a Partial Stamp). The print is now one
 * named sentence, pinned verbatim below.
 *
 * Defect B: the cabinet provenance marked a shield-gap forge only
 * when `record.outcome == ELAPSED_SHIELD_GAP`, but a FLOW forge's
 * outcome is stamped STOPWATCH_ENDED by the disarm when-chain
 * (`completed && isStopwatchMode` wins regardless of shield
 * state) — the mark was unreachable for exactly the FLOW forges
 * F4 added. The mark now derives from the shield-gap fact (the
 * row's shieldGapSeen latch) as well as the outcome stamp.
 */
class CurioForgeTruthTest {

    private fun forgeRecord(
        outcome: String,
        gapSeen: Boolean,
        stopwatch: Boolean = false
    ) = FocusSessionEntity(
        intention = "FORGE TRUTH",
        profileName = "Manual",
        targetDurationSeconds = 1500,
        elapsedSeconds = 1500,
        startWallTimeMillis = 1_000_000L,
        startElapsedRealtimeMillis = 0L,
        isActive = false,
        completedSuccessfully = true,
        isStopwatchMode = stopwatch,
        forgedArtifactId = "curio-1",
        outcome = outcome,
        shieldGapSeen = gapSeen
    )

    // ---- Defect A: the printed rule ----------------------------

    @Test
    fun forgeRule_isTheExactScopedSentence() {
        assertEquals(
            "A MANUAL SPAN YOU FINISH IN FULL FORGES ONE — THE FORGE CHOOSES WHICH.",
            CABINET_FORGE_RULE
        )
    }

    @Test
    fun forgeRule_carriesTheRoutineException() {
        // MANUAL is the app's own word for a non-routine span
        // (routineId == null); the old print carried no exception
        // while routine-born finishes forge nothing.
        assertTrue(CABINET_FORGE_RULE.startsWith("A MANUAL SPAN"))
    }

    @Test
    fun forgeRule_carriesTheInFullCondition() {
        // A timed span hand-ended early forges nothing; the old
        // print promised a forge to any self-finish.
        assertTrue(CABINET_FORGE_RULE.contains("FINISH IN FULL FORGES ONE"))
    }

    @Test
    fun forgeRule_isNotTheOldUnscopedClaim() {
        assertFalse(
            CABINET_FORGE_RULE ==
                "A SPAN YOU FINISH YOURSELF FORGES ONE — THE FORGE CHOOSES WHICH."
        )
    }

    // ---- Defect B: the shield-gap mark -------------------------

    @Test
    fun flowForge_withShieldGap_carriesTheGapMark() {
        // The row exactly as disarmActiveSession stamps a
        // hand-completed FLOW forge that crossed a shield gap:
        // outcome STOPWATCH_ENDED (the when-chain's precedence),
        // the gap carried only by the shieldGapSeen latch.
        val record = forgeRecord(
            outcome = SessionOutcome.STOPWATCH_ENDED,
            gapSeen = true,
            stopwatch = true
        )
        assertEquals(" · SHIELD GAP", curioForgeGapMark(record))
    }

    @Test
    fun flowForge_clean_carriesNoGapMark() {
        val record = forgeRecord(
            outcome = SessionOutcome.STOPWATCH_ENDED,
            gapSeen = false,
            stopwatch = true
        )
        assertEquals("", curioForgeGapMark(record))
    }

    @Test
    fun timedGapForge_outcomeStamp_stillCarriesTheGapMark() {
        // The pre-existing witness: a timed forge whose finish
        // classified ELAPSED_SHIELD_GAP keeps its mark even when
        // the latch itself was never set (the classifier can book
        // the gap from beat staleness at the ending write).
        val record = forgeRecord(
            outcome = SessionOutcome.ELAPSED_SHIELD_GAP,
            gapSeen = false
        )
        assertEquals(" · SHIELD GAP", curioForgeGapMark(record))
    }

    @Test
    fun timedCleanForge_carriesNoGapMark() {
        val record = forgeRecord(
            outcome = SessionOutcome.ELAPSED_CLEAN,
            gapSeen = false
        )
        assertEquals("", curioForgeGapMark(record))
    }
}
