package com.example

import com.example.data.AirlockBreathStore
import com.example.ui.components.airlockCountdownRemaining
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #318 F4: the breathing wall is a stored, per-configuration
 * value — the clamp that guards the store and the deadline math that
 * consumes a non-default wall are both pure and pinned here.
 */
class AirlockBreathStoreIssue318Test {

    @Test
    fun defaultIsDecision16Wall() {
        assertEquals(5, AirlockBreathStore.DEFAULT_SECONDS)
    }

    @Test
    fun clampKeepsLawfulValues() {
        assertEquals(3, AirlockBreathStore.clampSeconds(3))
        assertEquals(5, AirlockBreathStore.clampSeconds(5))
        assertEquals(12, AirlockBreathStore.clampSeconds(12))
        assertEquals(30, AirlockBreathStore.clampSeconds(30))
    }

    @Test
    fun clampRejectsZeroAndAbsurdWalls() {
        assertEquals(AirlockBreathStore.MIN_SECONDS, AirlockBreathStore.clampSeconds(0))
        assertEquals(AirlockBreathStore.MIN_SECONDS, AirlockBreathStore.clampSeconds(-7))
        assertEquals(AirlockBreathStore.MAX_SECONDS, AirlockBreathStore.clampSeconds(600))
    }

    @Test
    fun countdownHonoursConfiguredWall() {
        val start = 100_000L
        assertEquals(12, airlockCountdownRemaining(start, start, wallSeconds = 12))
        assertEquals(7, airlockCountdownRemaining(start, start + 5_000L, wallSeconds = 12))
        assertEquals(0, airlockCountdownRemaining(start, start + 12_000L, wallSeconds = 12))
        assertEquals(0, airlockCountdownRemaining(start, start + 99_000L, wallSeconds = 12))
    }
}
