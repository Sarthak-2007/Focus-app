package com.example

import com.example.service.peekExpiryShouldEvict
import com.example.ui.components.peekCountdownText
import com.example.ui.components.peekTermsText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #102 (peek-lived) — the journey's pure decisions:
 *  - finding 2: at lease expiry, eviction fires ONLY when the
 *    foreground package is the peeked package itself.
 *  - finding 5: the offer's terms separate the peek from the
 *    weekly EMERGENCY BREAK in plain words.
 *  - finding 1: the pill face's clock format.
 */
class PeekLivedTest {

    @Test
    fun expiryEvictsOnlyAUserStillInsideThePeekedApp() {
        assertTrue(peekExpiryShouldEvict("com.example.chat", "com.example.chat"))
        // Already left (launcher or another app in front): no eviction.
        assertFalse(peekExpiryShouldEvict("com.android.launcher", "com.example.chat"))
        assertFalse(peekExpiryShouldEvict("com.example.other", "com.example.chat"))
        // Foreground unknown: never evict on a guess.
        assertFalse(peekExpiryShouldEvict(null, "com.example.chat"))
        // No peeked package: nothing to evict from.
        assertFalse(peekExpiryShouldEvict("com.example.chat", ""))
    }

    @Test
    fun peekTermsSayTheBreakIsNotSpentAndTheSessionSurvives() {
        val terms = peekTermsText()
        assertTrue(terms.contains("DOES NOT SPEND YOUR EMERGENCY BREAK"))
        assertTrue(terms.contains("DOES NOT END THE SESSION"))
    }

    @Test
    fun peekCountdownPrintsMinutesAndPaddedSeconds() {
        assertEquals("2:00", peekCountdownText(120))
        assertEquals("1:59", peekCountdownText(119))
        assertEquals("0:07", peekCountdownText(7))
        assertEquals("0:00", peekCountdownText(0))
        assertEquals("0:00", peekCountdownText(-5))
    }
}
