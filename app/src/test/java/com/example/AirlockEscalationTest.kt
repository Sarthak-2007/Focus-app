package com.example

import com.example.service.InterceptClaim
import com.example.ui.components.escalatedAirlockWallSeconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R6 #340: F17's escalating wall ladder and F63's cross-door
 * episode claim, pinned as pure behaviour.
 */
class AirlockEscalationTest {

    @Test
    fun `wall ladder doubles to the 120 cap`() {
        assertEquals(5, escalatedAirlockWallSeconds(0))
        assertEquals(10, escalatedAirlockWallSeconds(1))
        assertEquals(20, escalatedAirlockWallSeconds(2))
        assertEquals(40, escalatedAirlockWallSeconds(3))
        assertEquals(80, escalatedAirlockWallSeconds(4))
        assertEquals(120, escalatedAirlockWallSeconds(5))
        assertEquals(120, escalatedAirlockWallSeconds(40))
    }

    @Test
    fun `wall ladder never shrinks on bad input`() {
        assertEquals(5, escalatedAirlockWallSeconds(-3))
    }

    @Test
    fun `episode claim - first door wins, second stands down`() {
        InterceptClaim.resetForTest()
        assertTrue(InterceptClaim.tryClaim("com.example.a"))
        assertFalse(InterceptClaim.tryClaim("com.example.a"))
        // A different package is never silenced by the claim.
        assertTrue(InterceptClaim.tryClaim("com.example.b"))
    }

    @Test
    fun `episode claim - release and foreground change void it`() {
        InterceptClaim.resetForTest()
        assertTrue(InterceptClaim.tryClaim("com.example.a"))
        InterceptClaim.release("com.example.a")
        assertTrue(InterceptClaim.tryClaim("com.example.a"))
        InterceptClaim.releaseExcept("com.example.b")
        assertTrue(
            "Foreground moving elsewhere ends the episode",
            InterceptClaim.tryClaim("com.example.a")
        )
        InterceptClaim.releaseAll()
        assertFalse(InterceptClaim.isClaimed("com.example.a"))
    }
}
