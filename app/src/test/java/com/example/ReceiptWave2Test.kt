package com.example

import android.content.Context
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.data.FinishKind
import com.example.data.ReceiptState
import com.example.ui.components.ReceiptSheet
import com.example.ui.components.dayLabel
import com.example.ui.theme.MyApplicationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Calendar

/**
 * Issue #10 WAVE-2 (receipt audit Group A) — declared, uncompiled.
 *
 * Pins the wave-2 print cures at the sheet surface:
 *  1. A2 — a legacy SLEPT THROUGH stamps STOOD DOWN; the old word
 *     never re-prints. Plain ENDED adds no segment (no tautology);
 *     the shield-gap compound never double-prints the gap.
 *  2. Item 10 — BREAK BANKED never prints, whatever the state
 *     carries as record.
 *  3. A3/W-43, as superseded by BELEG (2eb57dfd) — the footer
 *     carries the standing plain-words sentence; the RECORD
 *     sentence never prints. (Item 13's YPT sentence was the
 *     law when this class was written; BELEG replaced it in
 *     writing and nobody swept this class — corrected in the
 *     queue-place-7 PR, convergence-s12.)
 *  4. A11 — a cross-midnight span puts a date on the ARMED line.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceiptWave2Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stageA24HourDevice() {
        // Rig contract (#166, floor lane): since #88 the app's
        // clocks speak the DEVICE's language — ClockText reads
        // the system 12/24-hour setting, never a pinned format
        // (ClockText.kt: "the system 12/24-hour setting and the
        // device locale — never a pinned Locale.US HH:mm"). This
        // class's ARMED/ENDED assertions are written in the
        // 24-hour forms, so the rig states its device setting the
        // way a 24-hour phone presents it. Unstaged, the rig's
        // unset setting resolves by default locale to 12-hour and
        // the slip prints "ARMED 9:00 AM" — a device this class
        // never claimed to test — while every format-free line
        // (RAN / PLANNED / HELD, all longSpan) kept passing.
        // Production is untouched: on a 12-hour phone the slip
        // still prints 12-hour, exactly as #88 ruled.
        val context = ApplicationProvider.getApplicationContext<Context>()
        Settings.System.putString(
            context.contentResolver, Settings.System.TIME_12_24, "24"
        )
    }

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
            MyApplicationTheme(darkTheme = false) {
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
        // Footer limb, corrected to the standing law: BELEG's
        // W-43 sentence (strings.xml receipt_footer_log) is what
        // the slip prints — the YPT sentence this assertion once
        // hunted was superseded in writing by 2eb57dfd and lives
        // on only as the Reception boundary line, a different
        // surface. The RECORD half of item 13 was never
        // superseded and stands as filed.
        composeRule.onNodeWithText(
            "THIS APP KEEPS ITS OWN LOG · STUDY TIME IS IN THE STUDY TIMER",
            substring = true
        ).assertIsDisplayed()
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
        composeRule.onNodeWithText("ARMED ${dayLabel(arm)} 23:50", substring = true)
            .assertIsDisplayed()
    }
}
