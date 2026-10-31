package com.example

import com.example.widget.WidgetSealTruth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #155, verifier residue (2) — the widget's SEALED truth
 * rule. Before the cure the widget printed SEALED from the session
 * row alone (`isActive`): F2's lie reborn — a dead or blind shield
 * beside a live row still read SEALED. Every test below states a
 * world and the only honest print it allows: SEALED is proven
 * solely by the shield's protecting witness standing for THIS
 * session AND a fresh persisted shield beat.
 */
class WidgetSealTruthTest {

    private val now = 1_000_000L
    private val sessionId = 7

    private fun proven(
        isActive: Boolean = true,
        protecting: Boolean = true,
        protectingSessionId: Int = sessionId,
        beatWallMillis: Long = now - 1_000L,
        nowWallMillis: Long = now,
        accessibilityBound: Boolean = true
    ): Boolean = WidgetSealTruth.sealedProven(
        isActive = isActive,
        protecting = protecting,
        protectingSessionId = protectingSessionId,
        sessionId = sessionId,
        beatWallMillis = beatWallMillis,
        nowWallMillis = nowWallMillis,
        accessibilityBound = accessibilityBound
    )

    @Test
    fun proven_whenWitnessStandsForThisSessionAndBeatIsFresh() {
        assertTrue(proven())
    }

    @Test
    fun unproven_whenShieldNeverProvedProtection() {
        // The dead/blind shield: usage access revoked or the loop
        // gone — the row is live, the witness never flipped.
        assertFalse(proven(protecting = false))
    }

    @Test
    fun unproven_whenWitnessStandsForAnotherSession() {
        assertFalse(proven(protectingSessionId = sessionId + 1))
    }

    @Test
    fun unproven_whenNoBeatWasEverStamped() {
        assertFalse(proven(beatWallMillis = 0L))
    }

    @Test
    fun unproven_whenBeatIsStale() {
        // The loop stamps ~every second; a beat older than the
        // freshness window means the shield is not beating now.
        assertFalse(
            proven(beatWallMillis = now - WidgetSealTruth.BEAT_FRESH_MILLIS - 1L)
        )
    }

    @Test
    fun proven_atTheFreshnessBoundary() {
        assertTrue(
            proven(beatWallMillis = now - WidgetSealTruth.BEAT_FRESH_MILLIS)
        )
    }

    @Test
    fun unproven_whenBeatIsFromTheFuture() {
        // Clock moved backwards — a beat ahead of now proves
        // nothing about the present.
        assertFalse(proven(beatWallMillis = now + 1_000L))
    }

    @Test
    fun unproven_whenAccessibilityDoorIsDead() {
        // IQ-5 F-3(ii): the usage witness can stand and the beat
        // be fresh while the accessibility door — the door that
        // actually blocks — is dead. Blocking is OFF; SEALED
        // must not print.
        assertFalse(proven(accessibilityBound = false))
    }

    @Test
    fun unproven_whenSessionIsNotActive() {
        assertFalse(proven(isActive = false))
    }

    @Test
    fun freshnessWindowIsThirtySeconds() {
        assertEquals(30_000L, WidgetSealTruth.BEAT_FRESH_MILLIS)
    }
}
