package com.example

import com.example.ui.components.airlockAttemptLine
import com.example.ui.components.attemptOrdinal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #254 F12 (Audit R5): the attempt mirror on the Airlock
 * door. The count is the intercept ledger's own number for today;
 * an uncarried count (0 or negative) prints nothing rather than
 * inventing one, and the ordinal follows English teen rules.
 */
class AirlockAttemptLineTest {

    @Test
    fun uncarriedCountPrintsNothing() {
        assertNull(airlockAttemptLine(0))
        assertNull(airlockAttemptLine(-3))
    }

    @Test
    fun firstReachStatesItself() {
        assertEquals("1ST REACH TODAY", airlockAttemptLine(1))
    }

    @Test
    fun ordinalsFollowEnglishRules() {
        assertEquals("2ND", attemptOrdinal(2))
        assertEquals("3RD", attemptOrdinal(3))
        assertEquals("4TH", attemptOrdinal(4))
        assertEquals("11TH", attemptOrdinal(11))
        assertEquals("12TH", attemptOrdinal(12))
        assertEquals("13TH", attemptOrdinal(13))
        assertEquals("21ST", attemptOrdinal(21))
        assertEquals("22ND", attemptOrdinal(22))
        assertEquals("23RD", attemptOrdinal(23))
        assertEquals("40TH REACH TODAY", airlockAttemptLine(40))
    }
}
