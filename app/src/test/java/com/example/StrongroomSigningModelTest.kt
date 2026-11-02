package com.example

import com.example.ui.strongroom.CompositionDraft
import com.example.ui.strongroom.SigningAppOption
import com.example.ui.strongroom.SigningResult
import com.example.ui.strongroom.SigningState
import com.example.ui.strongroom.SigningStep
import com.example.ui.strongroom.SigningWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * THE SIGNING SEQUENCE's conformance contract (ui/strongroom/
 * SigningModel.kt; ledger C-4 — insertion figure 0 — plus the
 * corrected staging: signature ≠ swing):
 *
 *  - The sequence is the five filed steps in their one order;
 *    advance moves exactly one step, skips none, and READY is
 *    terminal. COMBINATION never advances — it completes.
 *  - Amendments live only at their own step (B-2's amend-act,
 *    step-bound); an amendment elsewhere stands still.
 *  - Uninterruptible from the signature: canLeave is true until
 *    beginSignature, false forever after — including at READY.
 *  - The result carries NO combination and NO seal: its fields
 *    are exactly the locked set, the hour, the sentence, the
 *    task line (A-2 — the combination is written nowhere; the
 *    swing is the seal act's, and no field here stands for it).
 *  - Unamended, the result IS the composed day (B-2: the signing
 *    amends, never creates).
 *  - The standing words are verbatim as filed (B-1/B-4/B-5/B-19).
 */
class StrongroomSigningModelTest {

    private fun draft(spentDay: Boolean = false) = CompositionDraft(
        dayShapeName = "ordinary",
        appOptions = listOf(
            SigningAppOption("com.social", "Social"),
            SigningAppOption("com.video", "Video"),
            SigningAppOption("com.notes", "Notes")
        ),
        composedLocked = setOf("com.social", "com.video"),
        composedHour = "06:30",
        composedSentence = null,
        spentDay = spentDay
    )

    private fun atCombination(): SigningState =
        SigningState(draft()).advance().advance().advance()

    @Test
    fun sequence_isTheFiveFiledSteps_inOrder() {
        assertEquals(
            listOf(
                SigningStep.COMPOSITION,
                SigningStep.HOUR,
                SigningStep.SENTENCE,
                SigningStep.COMBINATION,
                SigningStep.READY
            ),
            SigningStep.SEQUENCE
        )
    }

    @Test
    fun advance_movesOneStep_neverSkips_andReadyIsTerminal() {
        var s = SigningState(draft())
        val visited = mutableListOf(s.step)
        repeat(6) {
            s = s.advance()
            visited += s.step
        }
        assertEquals(
            listOf(
                SigningStep.COMPOSITION,
                SigningStep.HOUR,
                SigningStep.SENTENCE,
                SigningStep.COMBINATION,
                SigningStep.COMBINATION, // combination completes, never advances
                SigningStep.COMBINATION,
                SigningStep.COMBINATION
            ),
            visited
        )
        val ready = atCombination().beginSignature().completeSignature()
        assertEquals(SigningStep.READY, ready.step)
        assertEquals(SigningStep.READY, ready.advance().step)
    }

    @Test
    fun amendments_liveOnlyAtTheirOwnStep() {
        val start = SigningState(draft())
        // The hour cannot be amended at the composition step.
        assertEquals("06:30", start.setHour("07:00").liftingHour)
        // Apps cannot be amended at the hour step.
        val atHour = start.advance()
        assertEquals(
            start.lockedPackages,
            atHour.toggleApp("com.notes").lockedPackages
        )
        // The sentence cannot be amended at the composition step.
        assertNull(start.setSentence("a sentence").sentence)
        // At their own steps, each amendment lands.
        assertEquals("07:00", atHour.setHour("07:00").liftingHour)
        assertTrue("com.notes" in start.toggleApp("com.notes").lockedPackages)
        assertEquals(
            "a sentence",
            start.advance().advance().setSentence("a sentence").sentence
        )
        // An app that is not among the day's options is never added.
        assertEquals(
            start.lockedPackages,
            start.toggleApp("com.unknown").lockedPackages
        )
    }

    @Test
    fun uninterruptible_fromTheSignatureBegun() {
        var s = SigningState(draft())
        assertTrue(s.canLeave)
        s = atCombination()
        assertTrue(s.canLeave)
        s = s.beginSignature()
        assertFalse(s.canLeave)
        s = s.completeSignature()
        assertFalse(s.canLeave)
    }

    @Test
    fun signature_completesOnlyFromItsOwnBegunAct() {
        try {
            atCombination().completeSignature()
            fail("completion without a begun signature must refuse")
        } catch (expected: IllegalStateException) {
            // the sequence holds
        }
        try {
            SigningState(draft()).beginSignature().completeSignature()
            fail("completion away from the combination step must refuse")
        } catch (expected: IllegalStateException) {
            // beginSignature away from its step stands still, so
            // completion still has no begun act to complete
        }
    }

    @Test
    fun result_carriesNoCombinationAndNoSeal() {
        val fields = SigningResult::class.java.declaredFields
            .filter { !it.isSynthetic }
            .map { it.name }
            .sorted()
        assertEquals(
            listOf("liftingHour", "lockedPackages", "sentence", "taskLine"),
            fields
        )
    }

    @Test
    fun unamended_theResultIsTheComposedDay() {
        val d = draft()
        val result = atCombination().beginSignature().completeSignature().result
        assertEquals(d.composedLocked, result?.lockedPackages)
        assertEquals(d.composedHour, result?.liftingHour)
        assertNull(result?.sentence)
        assertTrue(SigningState(d).sentenceKeptPlace)
    }

    @Test
    fun amended_theResultCarriesTheAmendments() {
        var s = SigningState(draft())
        s = s.toggleApp("com.video") // unlock one composed lock
        s = s.advance()
        s = s.setHour("07:15")
        s = s.advance()
        s = s.setSentence("Hold the morning.")
        s = s.setTaskLine("Physics, in his words")
        s = s.advance().beginSignature().completeSignature()
        assertEquals(setOf("com.social"), s.result?.lockedPackages)
        assertEquals("07:15", s.result?.liftingHour)
        assertEquals("Hold the morning.", s.result?.sentence)
        assertEquals("Physics, in his words", s.result?.taskLine)
    }

    @Test
    fun standingWords_areVerbatimAsFiled() {
        assertEquals(
            "You can close this room yourself, on any calm evening, " +
                "in any month, and it will read as a kept account.",
            SigningWords.TELLING_LINE
        )
        assertEquals("codes wait tonight", SigningWords.CODES_WAIT_LINE)
        assertEquals("do not listen to me after this", SigningWords.SIGNING_CLAUSE)
        // B-1: the purpose sentence, in its fixed 23 words.
        assertEquals(23, SigningWords.PURPOSE_LINE.split(" ").size)
        assertTrue(
            SigningWords.PURPOSE_LINE.startsWith("What I settle while I am strong")
        )
    }

    @Test
    fun draft_refusesAnUnlawfulComposedDay() {
        try {
            draft().copy(composedHour = "25:00")
            fail("an unlawful composed hour must refuse the draft")
        } catch (expected: IllegalArgumentException) {
            // refused
        }
        try {
            draft().copy(composedLocked = setOf("com.notanoption"))
            fail("a composed lock outside the options must refuse the draft")
        } catch (expected: IllegalArgumentException) {
            // refused
        }
    }
}
