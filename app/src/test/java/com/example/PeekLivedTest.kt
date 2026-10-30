package com.example

import com.example.ui.components.peekTermsText
import com.example.ui.components.peekWithheldReasonVisible
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #102 (peek-lived) — the two residuals ported onto main's
 * #114/#99 peek mechanism (PR #351's own pill/enforcer build was
 * superseded by PR #444; its PeekLivedTest cases for the rival
 * enforcer and pill clock are NOT ported — main's equivalents are
 * covered by Issue99PeekJourneyTest):
 *  - finding 5: the offer's terms separate the peek from the
 *    weekly EMERGENCY BREAK in plain words (ported verbatim from
 *    PR #351's PeekLivedTest).
 *  - finding 4: the withheld peek's reason is visible whenever
 *    the peek is not ready — the breath countdown plays no part,
 *    so the reason shows from the first second.
 */
class PeekLivedTest {

    @Test
    fun peekTermsSayTheBreakIsNotSpentAndTheSessionSurvives() {
        val terms = peekTermsText()
        assertTrue(terms.contains("DOES NOT SPEND YOUR EMERGENCY BREAK"))
        assertTrue(terms.contains("DOES NOT END THE SESSION"))
    }

    @Test
    fun withheldReasonShowsWheneverThePeekIsNotReady() {
        assertTrue(peekWithheldReasonVisible(peekReady = false))
        assertFalse(peekWithheldReasonVisible(peekReady = true))
    }
}
