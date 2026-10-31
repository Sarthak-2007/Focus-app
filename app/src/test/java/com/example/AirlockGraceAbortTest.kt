package com.example

import com.example.ui.components.airlockGraceBadgeSeconds
import com.example.ui.components.airlockGraceChipLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #125 (Audit R3b, armed-wrong F5 remainder): the grace
 * abort on the Airlock door. The badge is derived from the
 * session's verified elapsed at the intercept instant, aged on
 * the monotonic clock — the same window the repository's grace
 * law enforces (GraceDeadlineRepositoryTest pins that law; this
 * pins the door's print of it). The window itself is
 * [com.example.data.MonolithRepository.GRACE_WINDOW_SECONDS] —
 * 30s since issue #125 F1 (Sarthak's ruling, Oct 31 19:14 IST);
 * the expectations below are stated against the constant, and
 * one test pins the formula against an explicit window so the
 * arithmetic is proved independent of the law's current length.
 * PIN-scope: SOURCE — pure function assertions on
 * airlockGraceBadgeSeconds / airlockGraceChipLabel; no
 * plumbing, post or render claim rests on this file. The Deck
 * chip's formula (window + 1 − elapsed, coerced 1..window) is
 * the print: the whole window on the first usable frame, 1 on
 * the last, nothing past it.
 */
class AirlockGraceAbortTest {

    private val window = com.example.data.MonolithRepository.GRACE_WINDOW_SECONDS

    @Test
    fun noSessionSnapshotStandsNoAbort() {
        // -1 = not carried (no session, previews, unminted states).
        assertNull(airlockGraceBadgeSeconds(-1, 1_000L, 1_000L))
        assertNull(airlockGraceBadgeSeconds(-100, 1_000L, 1_000L))
    }

    @Test
    fun firstUsableFrameBadgesTheWholeWindow() {
        // Intercepted in the session's first second, read at once.
        assertEquals(window, airlockGraceBadgeSeconds(0, 5_000L, 5_000L))
        // Sub-second ageing does not spend a badge second.
        assertEquals(window, airlockGraceBadgeSeconds(0, 5_000L, 5_999L))
    }

    @Test
    fun secondElevenStandsInsideTheWindowNow() {
        // Issue #125 F1: the old second-11 cliff is floor inside
        // the stretched window — elapsed 11 badges window − 10,
        // and the new edge is the window itself, not 10.
        assertEquals(window - 10, airlockGraceBadgeSeconds(11, 2_000L, 2_000L))
        assertEquals(window - 19, airlockGraceBadgeSeconds(20, 2_000L, 2_000L))
    }

    @Test
    fun badgeCountsDownWithTheMonotonicAge() {
        // Armed 3s before the intercept, read 2s after it: the
        // live elapsed is 5, so window − 4 usable seconds remain.
        assertEquals(window - 4, airlockGraceBadgeSeconds(3, 10_000L, 12_000L))
        assertEquals(window - 4, airlockGraceBadgeSeconds(3, 10_000L, 12_500L))
    }

    @Test
    fun lastUsableSecondBadgesOne() {
        // Live elapsed exactly at the window edge is still inside
        // the repository's law (verified elapsed ≤ window).
        assertEquals(1, airlockGraceBadgeSeconds(window, 2_000L, 2_000L))
        assertEquals(1, airlockGraceBadgeSeconds(window - 1, 2_000L, 3_000L))
    }

    @Test
    fun pastTheWindowNoAbortStands() {
        assertNull(airlockGraceBadgeSeconds(window, 2_000L, 3_000L))
        assertNull(airlockGraceBadgeSeconds(window + 1, 2_000L, 2_000L))
        assertNull(airlockGraceBadgeSeconds(0, 2_000L, 2_000L + (window + 1) * 1_000L))
    }

    @Test
    fun clockSkewBackwardsNeverWidensTheWindow() {
        // A now before the intercept instant ages nothing.
        assertEquals(window - 2, airlockGraceBadgeSeconds(3, 10_000L, 4_000L))
    }

    @Test
    fun badgeFormulaIsWindowAgnostic() {
        // The arithmetic itself, pinned against an explicit
        // 10-second window (the pre-ruling law's length): first
        // frame whole, edge at 1, past it nothing.
        assertEquals(10, airlockGraceBadgeSeconds(0, 5_000L, 5_000L, windowSeconds = 10))
        assertEquals(1, airlockGraceBadgeSeconds(10, 2_000L, 2_000L, windowSeconds = 10))
        assertNull(airlockGraceBadgeSeconds(10, 2_000L, 3_000L, windowSeconds = 10))
    }

    @Test
    fun routineSessionsWearTheJudgedLabel() {
        assertEquals("END ROUTINE · WILL BE JUDGED", airlockGraceChipLabel(true))
        assertEquals("GRACE WINDOW ABORT", airlockGraceChipLabel(false))
    }
}
