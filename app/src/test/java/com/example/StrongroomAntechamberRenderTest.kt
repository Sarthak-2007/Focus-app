package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.strongroom.AntechamberState
import com.example.ui.strongroom.AntechamberScreen
import com.example.ui.strongroom.CrankOutcome
import com.example.ui.strongroom.HeldCall
import com.example.ui.strongroom.HeldCode
import com.example.ui.theme.MyApplicationTheme
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P5 — THE ANTECHAMBER, rendered (ui/strongroom/AntechamberScreen.kt):
 * the list law on the surface (B-9) — calling out first, the
 * allowed rows as signed, the pile behind its own step; the
 * drum turning and reporting its outcome; the pile's second
 * step, where a code's showing stands digits-alone; and on a
 * spent purse the allowed rows are GONE from the room, not
 * dimmed — they do not exist in the tree.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomAntechamberRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun state(purse: Int = 2) = AntechamberState(
        callingOut = listOf("Asha"),
        allowedAsSigned = listOf("Notes", "Maps"),
        purseMinutes = purse,
        heldCalls = listOf(HeldCall("c1", "+91 98110 24680", "09:12")),
        heldCodes = listOf(HeldCode("k1", "482 913", "09:14"))
    )

    @Test
    fun room_carriesTheListLaw_andTheDrumTurns() {
        val crank = AtomicReference<CrankOutcome?>(null)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(),
                    readOnlyLines = listOf("Lifting hour 06:30"),
                    onCrankOutcome = { crank.set(it) },
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        composeRule.onNodeWithTag("antechamber").assertIsDisplayed()
        composeRule.onNodeWithTag("antechamber_calling_out").assertIsDisplayed()
        composeRule.onNodeWithText("The drum holds 2 minutes").assertExists()
        composeRule.onNodeWithTag("antechamber_allowed_Notes").assertExists()
        composeRule.onNodeWithTag("antechamber_allowed_Maps").assertExists()
        composeRule.onNodeWithTag("antechamber_pile_entry").assertExists()
        composeRule.onNodeWithTag("antechamber_readonly").assertExists()

        composeRule.onNodeWithTag("antechamber_crank_Notes").performClick()
        composeRule.waitForIdle()
        assertEquals(CrankOutcome.TURNED, crank.get())
        composeRule.onNodeWithText("OPEN — Notes").assertExists()
    }

    @Test
    fun pile_isASecondStep_andTheShowingStandsDigitsAlone() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(),
                    readOnlyLines = emptyList(),
                    onCrankOutcome = {},
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        // The pile is not in the room's list — it is behind its step.
        composeRule.onNodeWithTag("pile_call_c1").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_pile_entry").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pile_call_c1").assertIsDisplayed()
        composeRule.onNodeWithText("+91 98110 24680").assertExists()
        composeRule.onNodeWithTag("pile_take_c1").assertExists()
        composeRule.onNodeWithTag("pile_leave_c1").assertExists()
        // Unshown, no digits stand anywhere. Shown, they stand alone.
        composeRule.onNodeWithTag("pile_digits_k1").assertDoesNotExist()
        composeRule.onNodeWithTag("pile_show_k1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pile_digits_k1").assertIsDisplayed()
        composeRule.onNodeWithText("482 913").assertExists()
    }

    @Test
    fun spentPurse_theAllowedThingsAreGone_notDimmed() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(purse = 0),
                    readOnlyLines = emptyList(),
                    onCrankOutcome = {},
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        composeRule.onNodeWithTag("antechamber_purse_spent").assertIsDisplayed()
        composeRule.onNodeWithTag("antechamber_allowed_Notes").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_allowed_Maps").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_crank_Notes").assertDoesNotExist()
    }
}
