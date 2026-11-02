package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.strongroom.SealedFaceScreen
import com.example.ui.strongroom.SealedFaceState
import com.example.ui.theme.MyApplicationTheme
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P3 — THE SEALED FACE, rendered (ui/strongroom/SealedFaceScreen.kt):
 * the kept ground carries the four printings' places — the ring,
 * the door-word slot's kept place, his sentence or its kept
 * place, the held-mark's kept place — and the grille plate; a
 * reach on the door fires the knock callback and nothing else;
 * the grille is a separate touch of its own.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomSealedFaceRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun face_carriesTheFourPrintings_andTheGrille() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SealedFaceScreen(
                    state = SealedFaceState("06:30", "Hold the morning."),
                    onKnockReach = {},
                    onAntechamberRequest = {}
                )
            }
        }
        composeRule.onNodeWithTag("sealed_face").assertIsDisplayed()
        composeRule.onNodeWithTag("sealed_face_ring").assertIsDisplayed()
        composeRule.onNodeWithTag("door_word_kept_place").assertExists()
        composeRule.onNodeWithTag("face_sentence").assertExists()
        composeRule.onNodeWithTag("sentence_kept_place").assertDoesNotExist()
        composeRule.onNodeWithTag("held_mark_kept_place").assertExists()
        composeRule.onNodeWithTag("antechamber_grille").assertExists()
    }

    @Test
    fun face_withoutASentence_standsTheKeptPlace() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SealedFaceScreen(
                    state = SealedFaceState("21:45", null),
                    onKnockReach = {},
                    onAntechamberRequest = {}
                )
            }
        }
        composeRule.onNodeWithTag("sentence_kept_place").assertExists()
        composeRule.onNodeWithTag("face_sentence").assertDoesNotExist()
    }

    @Test
    fun reach_firesTheKnock_andTheGrilleIsItsOwnTouch() {
        val knocks = AtomicInteger(0)
        val grilles = AtomicInteger(0)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SealedFaceScreen(
                    state = SealedFaceState("06:30", null),
                    onKnockReach = { knocks.incrementAndGet() },
                    onAntechamberRequest = { grilles.incrementAndGet() }
                )
            }
        }
        composeRule.onNodeWithTag("sealed_face").performClick()
        composeRule.waitForIdle()
        assert(knocks.get() == 1) { "one reach, one knock — got ${knocks.get()}" }
        composeRule.onNodeWithTag("antechamber_grille").performClick()
        composeRule.waitForIdle()
        assert(grilles.get() == 1) { "the grille is a second, separate touch" }
        assert(knocks.get() == 1) { "the grille touch must not also knock" }
    }
}
