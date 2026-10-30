package com.example

import com.example.data.AirlockBreathStore
import com.example.data.AirlockEscalation
import com.example.ui.components.airlockReachLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #265 (B-265-19 + B-265-20): the escalating Airlock wall
 * and the reach-count line. Pure arithmetic + pure wording — the
 * wall a craving cascade meets must climb exactly as priced
 * (5s → 10s → 20s at the default base) and the door must state
 * the attempt number truthfully, including printing NOTHING on
 * the first reach (no invented history) and nothing when the
 * count was not carried (previews/tests, count 0).
 */
class AirlockEscalationTest {

    @Test
    fun multiplierLadderIsOneTwoFour() {
        assertEquals(1, AirlockEscalation.multiplierForReach(0))
        assertEquals(1, AirlockEscalation.multiplierForReach(1))
        assertEquals(2, AirlockEscalation.multiplierForReach(2))
        assertEquals(4, AirlockEscalation.multiplierForReach(3))
        assertEquals(4, AirlockEscalation.multiplierForReach(10))
    }

    @Test
    fun defaultBaseLadderIsFiveTenTwenty() {
        assertEquals(5, AirlockEscalation.effectiveBreathSeconds(5, 1))
        assertEquals(10, AirlockEscalation.effectiveBreathSeconds(5, 2))
        assertEquals(20, AirlockEscalation.effectiveBreathSeconds(5, 3))
        assertEquals(20, AirlockEscalation.effectiveBreathSeconds(5, 9))
    }

    @Test
    fun escalatedWallNeverExceedsCeiling() {
        // A user-configured 30s base quadruples to 120 raw — the
        // wall must stop at the 60s ceiling, never absurd.
        assertEquals(60, AirlockEscalation.effectiveBreathSeconds(30, 3))
        assertEquals(60, AirlockEscalation.effectiveBreathSeconds(30, 2).coerceAtMost(60))
    }

    @Test
    fun baseIsClampedThroughTheStoreFloorFirst() {
        // A stray base of 0 is Decision #16's floor (3s) before
        // any multiplier is applied.
        assertEquals(
            AirlockBreathStore.MIN_SECONDS,
            AirlockEscalation.effectiveBreathSeconds(0, 1)
        )
    }

    @Test
    fun reachLinePrintsOnlyFromTheSecondReach() {
        assertNull(airlockReachLine(0))
        assertNull(airlockReachLine(1))
        assertEquals("REACH 2 THIS SESSION — THE WALL STANDS LONGER", airlockReachLine(2))
        assertEquals("REACH 7 THIS SESSION — THE WALL STANDS LONGER", airlockReachLine(7))
    }
}
