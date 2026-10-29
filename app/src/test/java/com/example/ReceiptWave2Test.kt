package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.data.FinishKind
import com.example.data.ReceiptState
import com.example.ui.components.ReceiptSheet
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Issue #10 WAVE-2 (receipt audit Group A) — declared, uncompiled.
 *
 * Pins the wave-2 print cures at the sheet surface:
 *  1. A2 — a legacy SLEPT THROUGH stamps STOOD DOWN; the old word
 *     never re-prints. Plain ENDED adds no segment (no tautology);
 *     the shield-gap compound never double-prints the gap.
 *  2. Item 10 — BREAK BANKED never prints, whatever the state
 *     carries as record.
 *  3. A3/W-43 — the footer carries HOURS, never RECORD.
 *  4. A11 — a cross-midnight span puts a date on the ARMED line.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceiptWave2Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun receipt(
        outcomeWord: String,
        armWallMs: Long,
        endedAtWallMs: Long,
        bankedBreakSeconds: Int = 0,
        shieldWasOff: Boolean = false
    ) = ReceiptState(
        sessionId = 42,
        number = 48,
        kind = FinishKind.ROUTINE_SLEEP_STANDDOWN,
        outcomeWord = outcomeWord,
        title = "Night revision",
        armWallMs = armWallMs,
        endedAtWallMs = endedAtWallMs,
        plannedSeconds = 3000,
        sealedElapsedSeconds = 3000,
        heldAppsAtSeal = 0,
        heldSitesAtSeal = 0,
        routineLabelWithin = null,
        bankedBreakSeconds = bankedBreakSeconds,
        shieldWasOff = shieldWasOff,
        faulted = false,
        deflections = emptyList(),
        restored = true,
        receiptPublishedMonoMs = 0L
    )

    private fun show(state: ReceiptState) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ReceiptSheet(receipt = state, onDismiss = {})
                }
            }
        }
    }

    private fun wall(hour: Int, minute: Int, dayOffset: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun legacySleptThrough_normalisesToStoodDown() {
        show(receipt("SLEPT THROUGH", wall(22, 0), wall(22, 50)))
        composeRule.onNodeWithText("STOOD DOWN", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("SLEPT THROUGH", substring = true).assertDoesNotExist()
    }

    @Test
    fun breakBanked_neverPrints_andFooterCarriesHours() {
        show(receipt("ENDED", wall(9, 0), wall(9, 50), bankedBreakSeconds = 3600))
        composeRule.onNodeWithText("BREAK BANKED", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("ENDED · ENDED", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("YOUR STUDY HOURS LIVE IN YPT", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("YOUR STUDY RECORD LIVES IN YPT", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun shieldGap_gapPrintsOnce_inTheShieldLineOnly() {
        show(
            receipt(
                "ENDED · SHIELD GAP", wall(9, 0), wall(9, 50),
                shieldWasOff = true
            )
        )
        composeRule.onNodeWithText("ENDED · SHIELD GAP", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText(
            "THE SHIELD WAS OFF FOR PART OF THAT SESSION", substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun crossMidnight_armedLineCarriesItsDate() {
        val arm = wall(23, 50, dayOffset = -1)
        show(receipt("ENDED", arm, wall(0, 10)))
        val day = SimpleDateFormat("EEE d MMM", Locale.US)
            .format(Date(arm)).uppercase(Locale.US)
        composeRule.onNodeWithText("ARMED $day 23:50", substring = true).assertIsDisplayed()
    }
}
