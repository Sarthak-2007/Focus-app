package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
 * P5 — THE ANTECHAMBER, rendered, after the C-3 rethink
 * (ui/strongroom/AntechamberScreen.kt): calling out first; the
 * signed set standing and opening directly at every purse
 * state; the drum as its own band whose stock is a depleting
 * strip of marks, never a number; on a spent purse the signed
 * rows still stand while the drum's stock rows do not exist;
 * the pile behind its second step, its price stated on the
 * unshown code row, and the showing standing digits-alone.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomAntechamberRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun state(purse: Int = 2) = AntechamberState(
        callingOut = listOf("Asha"),
        allowedAsSigned = listOf("Notes", "Maps"),
        drumCandidates = listOf("Social", "Games"),
        purseMinutes = purse,
        heldCalls = listOf(HeldCall("c1", "+91 98110 24680", "09:12")),
        heldCodes = listOf(HeldCode("k1", "482 913", "09:14"))
    )

    @Test
    fun room_carriesBothSets_andTheDrumTurns() {
        val crank = AtomicReference<CrankOutcome?>(null)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(),
                    readOnlyLines = listOf("Lifting hour 06:30"),
                    onCrankOutcome = { crank.set(it) },
                    onSignedOpen = {},
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        composeRule.onNodeWithTag("antechamber").assertIsDisplayed()
        composeRule.onNodeWithTag("antechamber_calling_out").assertIsDisplayed()
        composeRule.onNodeWithTag("antechamber_allowed_Notes").assertExists()
        composeRule.onNodeWithTag("antechamber_allowed_Maps").assertExists()
        composeRule.onNodeWithTag("antechamber_drum_stock").assertExists()
        composeRule.onNodeWithTag("antechamber_drum_Social").assertExists()
        composeRule.onNodeWithTag("antechamber_pile_entry").assertExists()
        composeRule.onNodeWithTag("antechamber_readonly").assertExists()

        composeRule.onNodeWithTag("antechamber_crank_Social").performClick()
        composeRule.waitForIdle()
        assertEquals(CrankOutcome.TURNED, crank.get())
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // §6.1 struck the room's words over an opening — the
        // open app stands under its carried name alone, with
        // its TURN BACK handle standing beside it (the handle
        // exists only while an opening stands). The struck
        // caption stands nowhere.
        composeRule.onNodeWithTag("antechamber_turn_back").assertExists()
        composeRule.onNodeWithText("OPEN BY THE DRUM — Social").assertDoesNotExist()
    }

    @Test
    fun signedSet_opensDirectly_andStandsWhenThePurseIsSpent() {
        val crank = AtomicReference<CrankOutcome?>(null)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(purse = 0),
                    readOnlyLines = emptyList(),
                    onCrankOutcome = { crank.set(it) },
                    onSignedOpen = {},
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        // Set 1 stands, spent purse or not — and opens purse-free.
        composeRule.onNodeWithTag("antechamber_allowed_Notes").assertExists()
        composeRule.onNodeWithTag("antechamber_open_Notes").performClick()
        composeRule.waitForIdle()
        assertEquals(CrankOutcome.OPENED_FROM_THE_SIGNED_SET, crank.get())
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the opening stands under its carried name alone
        // (§6.1 — "OPEN — Notes" is struck copy), witnessed by
        // the TURN BACK handle that stands only while it is open.
        composeRule.onNodeWithTag("antechamber_turn_back").assertExists()
        composeRule.onNodeWithText("OPEN — Notes").assertDoesNotExist()
        // The drum's stock is gone — and at the hard stop the
        // deck (§6.4) stands ∅ words: no spent line, no node.
        composeRule.onNodeWithTag("antechamber_purse_spent").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_drum_stock").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_drum_Social").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_crank_Social").assertDoesNotExist()
    }

    @Test
    fun pile_isASecondStep_priceStated_andTheShowingStandsDigitsAlone() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AntechamberScreen(
                    initialState = state(),
                    readOnlyLines = emptyList(),
                    onCrankOutcome = {},
                    onSignedOpen = {},
                    onShowingOutcome = {},
                    onTakeCall = {},
                    onReturned = {},
                    onLeave = {}
                )
            }
        }
        composeRule.onNodeWithTag("pile_call_c1").assertDoesNotExist()
        composeRule.onNodeWithTag("antechamber_pile_entry").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pile_call_c1").assertIsDisplayed()
        // RE-AIMED at the end phase (BUILD-SR-24) to the composed
        // chamber: in HeldPileLayout the number stands TWICE by
        // design — on the WAITING shelf (what waits, shown whole)
        // and again on the TAKING shelf beside its handles — so
        // the single-node text finder is the wrong instrument;
        // the count is the pin.
        composeRule.onAllNodesWithText("+91 98110 24680").assertCountEquals(2)
        composeRule.onNodeWithTag("pile_take_c1").assertExists()
        composeRule.onNodeWithTag("pile_leave_c1").assertExists()
        // The price stands on the unshown row, before the taking.
        composeRule.onNodeWithTag("pile_price_k1").assertExists()
        composeRule.onNodeWithTag("pile_digits_k1").assertDoesNotExist()
        composeRule.onNodeWithTag("pile_show_k1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pile_digits_k1").assertIsDisplayed()
        composeRule.onNodeWithText("482 913").assertExists()
    }
}
