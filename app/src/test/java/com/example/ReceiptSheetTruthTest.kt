package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import com.example.data.FinishKind
import com.example.data.ReceiptDeflection
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
        bankedBreakSeconds: Int = 0,
        heldAppsAtSeal: Int = -1,
        heldSitesAtSeal: Int = -1,
        heldShutAtSeal: Int = -1,
        heldPeekAtSeal: Int = -1,
        deflections: List<ReceiptDeflection> = emptyList(),
        armZoneId: String? = null,
        endZoneId: String? = null
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
        heldAppsAtSeal = heldAppsAtSeal,
        heldSitesAtSeal = heldSitesAtSeal,
        routineLabelWithin = null,
        bankedBreakSeconds = bankedBreakSeconds,
        shieldWasOff = false,
        faulted = false,
        deflections = deflections,
        restored = true,
        receiptPublishedMonoMs = 0L,
        heldShutAtSeal = heldShutAtSeal,
        heldPeekAtSeal = heldPeekAtSeal,
        armZoneId = armZoneId,
        endZoneId = endZoneId
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

    // ---- issue #10: footer, HELD split, deflection marks ------

    @Test
    fun footer_plainWords_noAcronym() {
        show(receipt(wall(9, 0), wall(10, 0)))
        composeRule.onNodeWithText(
            "THIS APP KEEPS ITS OWN LOG · STUDY TIME IS IN THE STUDY TIMER",
            substring = true
        ).assertIsDisplayed()
        // W-43: the acronym appears NOWHERE on the rendered slip.
        composeRule.onAllNodesWithText("YPT", substring = true)
            .assertCountEquals(0)
    }

    @Test
    fun heldLine_splitByTier_whenSplitRecorded() {
        show(
            receipt(
                wall(9, 0), wall(10, 0),
                heldAppsAtSeal = 9, heldSitesAtSeal = 12,
                heldShutAtSeal = 6, heldPeekAtSeal = 3
            )
        )
        composeRule.onNodeWithText(
            "HELD 6 SHUT · 3 PEEK (2 MIN) · 12 WEBSITES",
            substring = true
        ).assertIsDisplayed()
        // The merged figure never prints alongside the split.
        composeRule.onNodeWithText("HELD 9 APPS", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun heldLine_preSplitRow_printsStoredTotal_neverInventsSplit() {
        show(
            receipt(
                wall(9, 0), wall(10, 0),
                heldAppsAtSeal = 9, heldSitesAtSeal = 12
            )
        )
        composeRule.onNodeWithText(
            "HELD 9 APPS · 12 SITES ON THIS PHONE",
            substring = true
        ).assertIsDisplayed()
        composeRule.onNodeWithText("SHUT", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun deflections_printNamesOnly_noPeekMarkerAnywhere() {
        // R-04, as the law now stands (A4): assembly admits ONLY
        // proven blocks into this list — a granted peek never
        // reaches the slip at all, so nothing here may carry a
        // peek marker or read a let-through as a turn-away.
        show(
            receipt(
                wall(9, 0), wall(10, 0),
                deflections = listOf(
                    ReceiptDeflection("KnockApp", "com.knock", wall(9, 30)),
                    ReceiptDeflection("ShutApp", "com.shut", wall(9, 45))
                )
            )
        )
        composeRule.onNodeWithText("· KnockApp", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("· ShutApp", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("PEEKED", substring = true)
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
    fun ended_printsInItsWitnessedZone_whenReadInAThirdZone() {
        // Issue #152 residue: armed in Kolkata, ended in London,
        // the slip is re-issued in New York. ENDED must print the
        // London wall time it was lived in, with London's offset —
        // before the end-zone witness existed it silently
        // re-rendered in the render zone (00:20, New York).
        val original = java.util.TimeZone.getDefault()
        try {
            java.util.TimeZone.setDefault(
                java.util.TimeZone.getTimeZone("America/New_York")
            )
            val arm = java.time.Instant.parse("2026-10-26T03:30:00Z").toEpochMilli()
            val end = java.time.Instant.parse("2026-10-26T04:20:00Z").toEpochMilli()
            show(
                receipt(
                    arm, end,
                    armZoneId = "Asia/Kolkata", endZoneId = "Europe/London"
                )
            )
            val armDay = dayLabel(arm, java.time.ZoneId.of("Asia/Kolkata"))
            val endDay = dayLabel(end, java.time.ZoneId.of("Europe/London"))
            composeRule.onNodeWithText("ARMED $armDay 09:00 (+05:30)", substring = true)
                .assertIsDisplayed()
            composeRule.onNodeWithText("ENDED $endDay 04:20 (+00:00)", substring = true)
                .assertIsDisplayed()
            // The render-zone re-render of the end must not exist.
            composeRule.onNodeWithText("ENDED ${dayLabel(end)} 00:20", substring = true)
                .assertDoesNotExist()
        } finally {
            java.util.TimeZone.setDefault(original)
        }
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
