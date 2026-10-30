package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import com.example.data.FinishKind
import com.example.data.ReceiptState
import com.example.ui.components.ReceiptSheet
import com.example.ui.components.dayLabel
import com.example.ui.components.longSpan
import com.example.ui.components.sameLocalDay
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Issue #57 (audit R1, slice receipt-sheet) — the five findings
 * pinned at the sheet surface and at the pure formatters:
 *  1. The sealed elapsed prints as the RAN total.
 *  2. BREAK BANKED never prints a false/truncated figure.
 *  3. PLANNED is exact — no silent ceiling to the next minute.
 *  4. Cross-midnight spans date BOTH the ARMED and ENDED lines;
 *     same-day slips keep the bare HH:mm print.
 *  5. Dismissal fires on the press DOWN, not on release.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceiptSheetTruthTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    // ---- pure formatters -------------------------------------

    @Test
    fun longSpan_wholeMinutes_printUnchanged() {
        assertEquals("0 SEC", longSpan(0))
        assertEquals("1 MIN", longSpan(60))
        assertEquals("50 MIN", longSpan(3000))
        assertEquals("1H 00M", longSpan(3600))
        assertEquals("2H 00M", longSpan(7200))
    }

    @Test
    fun longSpan_subMinuteRemainders_areSpokenNeverRounded() {
        assertEquals("45 SEC", longSpan(45))
        assertEquals("59 SEC", longSpan(59))
        assertEquals("1 MIN 01 SEC", longSpan(61))
        assertEquals("1 MIN 59 SEC", longSpan(119))
        assertEquals("1H 00M 01 SEC", longSpan(3601))
        assertEquals("1H 01M 01 SEC", longSpan(3661))
    }

    @Test
    fun sameLocalDay_andDayLabel() {
        val lateNight = wall(23, 50, dayOffset = -1)
        val afterMidnight = wall(0, 10)
        assertFalse(sameLocalDay(lateNight, afterMidnight))
        assertTrue(sameLocalDay(wall(9, 0), wall(9, 50)))
        assertTrue(dayLabel(lateNight).isNotBlank())
        assertFalse(dayLabel(lateNight).contains(":"))
    }

    // ---- sheet surface ---------------------------------------

    private fun receipt(
        armWallMs: Long,
        endedAtWallMs: Long,
        plannedSeconds: Int? = 3000,
        sealedElapsedSeconds: Int = 3000,
        bankedBreakSeconds: Int = 0
    ) = ReceiptState(
        sessionId = 57,
        number = 57,
        kind = FinishKind.MANUAL_FINISH,
        outcomeWord = "ENDED",
        title = "Revision",
        armWallMs = armWallMs,
        endedAtWallMs = endedAtWallMs,
        plannedSeconds = plannedSeconds,
        sealedElapsedSeconds = sealedElapsedSeconds,
        heldAppsAtSeal = -1,
        heldSitesAtSeal = -1,
        routineLabelWithin = null,
        bankedBreakSeconds = bankedBreakSeconds,
        shieldWasOff = false,
        faulted = false,
        deflections = emptyList(),
        restored = true,
        receiptPublishedMonoMs = 0L
    )

    private fun show(state: ReceiptState, onDismiss: () -> Unit = {}) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ReceiptSheet(receipt = state, onDismiss = onDismiss)
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
    fun ranLine_printsTheSealedElapsed_exactly() {
        show(receipt(wall(9, 0), wall(10, 1), sealedElapsedSeconds = 3661))
        composeRule.onNodeWithText("RAN 1H 01M 01 SEC", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun ranLine_zeroElapsed_printsNothing() {
        show(receipt(wall(9, 0), wall(9, 0), sealedElapsedSeconds = 0))
        composeRule.onNodeWithText("RAN ", substring = true).assertDoesNotExist()
    }

    @Test
    fun planned_isExact_noCeiling() {
        show(receipt(wall(9, 0), wall(9, 2), plannedSeconds = 61))
        composeRule.onNodeWithText("PLANNED 1 MIN 01 SEC", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("PLANNED 2 MIN", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun breakBanked_neverPrintsFalseZeroOrTruncation() {
        show(receipt(wall(9, 0), wall(9, 50), bankedBreakSeconds = 45))
        composeRule.onNodeWithText("BREAK BANKED 45 SEC", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("BREAK BANKED 0 MIN", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun breakBanked_119seconds_printsInFull() {
        show(receipt(wall(9, 0), wall(9, 50), bankedBreakSeconds = 119))
        composeRule.onNodeWithText("BREAK BANKED 1 MIN 59 SEC", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun crossMidnight_bothLinesCarryTheirDay() {
        val arm = wall(23, 50, dayOffset = -1)
        val end = wall(0, 10)
        show(receipt(arm, end))
        composeRule.onNodeWithText("ARMED ${dayLabel(arm)} 23:50", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("ENDED ${dayLabel(end)} 00:10", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun sameDay_linesKeepBareTimes() {
        show(receipt(wall(9, 0), wall(9, 50)))
        composeRule.onNodeWithText("ARMED 09:00", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("ENDED 09:50", substring = true)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("ARMED ${dayLabel(wall(9, 0))}", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun dismissal_firesOnPressDown_notOnRelease() {
        val dismissed = AtomicBoolean(false)
        show(receipt(wall(9, 0), wall(9, 50))) { dismissed.set(true) }
        // A press that is never released: the old clickable only
        // fired on the UP, so this gesture dismissed nothing.
        composeRule.onNodeWithTag("receiptSheetScrim")
            .performTouchInput { down(center) }
        composeRule.waitForIdle()
        assertTrue("DOWN alone must dismiss", dismissed.get())
    }
}
