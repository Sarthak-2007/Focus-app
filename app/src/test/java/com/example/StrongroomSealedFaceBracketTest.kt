package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.strongroom.BracketPair
import com.example.ui.strongroom.SealedFaceScreen
import com.example.ui.strongroom.SealedFaceState
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * B-11's AMENDMENT (Ruling R7-1, landed by BUILD-SR-14) — the
 * seal-gone form of P3's model and face:
 *
 *  - A standing face carries no bracket: [SealedFaceState.sealGone]
 *    is false and [SealedFaceState.sealGoneBracket] is null.
 *  - The seal-gone form carries the bracket pair — standing at
 *    the last check, fallen by the first check that found it —
 *    both hours lawful HH:MM, in that order, with the day's
 *    lifting hour and sentence carried unchanged (nothing added,
 *    nothing smoothed: the pair is two check hours, never one
 *    "held for" figure and never a fall instant).
 *  - An unlawful hour cannot enter the pair, at either end.
 *  - Rendered: the bracket stands at the hour's rank
 *    (`sealed_face_bracket`), the ring does not, and every kept
 *    place stands exactly as on the standing face.
 */
class StrongroomSealedFaceBracketModelTest {

    @Test
    fun standingFace_carriesNoBracket() {
        val face = SealedFaceState("06:30", "Hold the morning.")
        assertFalse(face.sealGone)
        assertNull(face.sealGoneBracket)
    }

    @Test
    fun sealGoneForm_carriesTheBracketPair_andNothingElseChanges() {
        val face = SealedFaceState.sealGone(
            liftingHour = "06:30",
            sentence = "Hold the morning.",
            standingHour = "23:10",
            fallenHour = "01:40"
        )
        assertTrue(face.sealGone)
        assertEquals(BracketPair("23:10", "01:40"), face.sealGoneBracket)
        assertEquals("23:10", face.sealGoneBracket?.standingHour)
        assertEquals("01:40", face.sealGoneBracket?.fallenHour)
        // The day as engraved, unchanged by the fall.
        assertEquals("06:30", face.liftingHour)
        assertEquals("Hold the morning.", face.sentence)
        assertTrue(face.hasSentence)
    }

    @Test
    fun bracketPair_refusesUnlawfulHours_atEitherEnd() {
        try {
            BracketPair("25:10", "01:40")
            fail("an unlawful standing hour cannot enter the bracket")
        } catch (expected: IllegalArgumentException) {
            // refused at the model's door
        }
        try {
            BracketPair("23:10", "1:40")
            fail("an unlawful fallen hour cannot enter the bracket")
        } catch (expected: IllegalArgumentException) {
            // refused at the model's door
        }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomSealedFaceBracketRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sealGoneFace_standsTheBracket_andTheKeptPlaces_andNoRing() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SealedFaceScreen(
                    state = SealedFaceState.sealGone(
                        liftingHour = "06:30",
                        sentence = null,
                        standingHour = "23:10",
                        fallenHour = "01:40"
                    ),
                    onKnockReach = {},
                    onAntechamberRequest = {}
                )
            }
        }
        composeRule.onNodeWithTag("sealed_face").assertIsDisplayed()
        composeRule.onNodeWithTag("sealed_face_bracket").assertExists()
        composeRule.onNodeWithTag("sealed_face_ring").assertDoesNotExist()
        composeRule.onNodeWithTag("door_word_kept_place").assertExists()
        composeRule.onNodeWithTag("sentence_kept_place").assertExists()
        composeRule.onNodeWithTag("held_mark_kept_place").assertExists()
    }
}
