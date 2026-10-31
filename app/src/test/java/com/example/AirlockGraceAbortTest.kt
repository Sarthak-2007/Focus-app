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
 * the monotonic clock — the same ≤10s window the repository's
 * grace law enforces (GraceDeadlineRepositoryTest pins that law;
 * this pins the door's print of it). PIN-scope: SOURCE — pure
 * function assertions on airlockGraceBadgeSeconds /
 * airlockGraceChipLabel; no plumbing, post or render claim rests
 * on this file. The Deck chip's formula
 * (window + 1 − elapsed, coerced 1..window) is the print: 10 on
 * the first usable frame, 1 on the last, nothing past it.
 */
class AirlockGraceAbortTest {

    @Test
    fun noSessionSnapshotStandsNoAbort() {
        // -1 = not carried (no session, previews, unminted states).
        assertNull(airlockGraceBadgeSeconds(-1, 1_000L, 1_000L))
        assertNull(airlockGraceBadgeSeconds(-100, 1_000L, 1_000L))
    }

    @Test
    fun firstUsableFrameBadgesTen() {
        // Intercepted in the session's first second, read at once.
        assertEquals(10, airlockGraceBadgeSeconds(0, 5_000L, 5_000L))
        // Sub-second ageing does not spend a badge second.
        assertEquals(10, airlockGraceBadgeSeconds(0, 5_000L, 5_999L))
    }

    @Test
    fun badgeCountsDownWithTheMonotonicAge() {
        // Armed 3s before the intercept, read 2s after it: the
        // live elapsed is 5, so 6 usable seconds remain.
        assertEquals(6, airlockGraceBadgeSeconds(3, 10_000L, 12_000L))
        assertEquals(6, airlockGraceBadgeSeconds(3, 10_000L, 12_500L))
    }

    @Test
    fun lastUsableSecondBadgesOne() {
        // Live elapsed exactly at the window edge is still inside
        // the repository's law (verified elapsed ≤ 10).
        assertEquals(1, airlockGraceBadgeSeconds(10, 2_000L, 2_000L))
        assertEquals(1, airlockGraceBadgeSeconds(9, 2_000L, 3_000L))
    }

    @Test
    fun pastTheWindowNoAbortStands() {
        assertNull(airlockGraceBadgeSeconds(10, 2_000L, 3_000L))
        assertNull(airlockGraceBadgeSeconds(11, 2_000L, 2_000L))
        assertNull(airlockGraceBadgeSeconds(0, 2_000L, 13_000L))
    }

    @Test
    fun clockSkewBackwardsNeverWidensTheWindow() {
        // A now before the intercept instant ages nothing.
        assertEquals(8, airlockGraceBadgeSeconds(3, 10_000L, 4_000L))
    }

    @Test
    fun routineSessionsWearTheJudgedLabel() {
        assertEquals("END ROUTINE · WILL BE JUDGED", airlockGraceChipLabel(true))
        assertEquals("GRACE WINDOW ABORT", airlockGraceChipLabel(false))
    }
}
