package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.ui.components.AlwaysOnDeskClockOverlay
import com.example.ui.components.deskClockBreakLine
import com.example.ui.components.deskClockBurnInOffsetDp
import com.example.ui.components.deskClockGlanceLine
import com.example.ui.components.deskClockModeLabel
import com.example.ui.components.deskClockProgress
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Issue #111 (Audit R3, desk-clock-lived) — the desk clock as lived:
 * lock-screen presence is window-flag code (pinned by the overlay's
 * DisposableEffect, not unit-testable here); everything else on the
 * face is a pure derivation and is pinned here, plus one render test
 * for the break announcement and the hold-to-exit gesture.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DeskClockLivedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun row(
        stopwatch: Boolean = false,
        inBreak: Boolean = false,
        breakRemaining: Int = 0,
        elapsed: Int = 0,
        blocks: Int = 2,
        active: Boolean = true
    ) = FocusSessionEntity(
        intention = "Revise",
        profileName = "Manual",
        targetDurationSeconds = 1500,
        elapsedSeconds = elapsed,
        startWallTimeMillis = 1_700_000_000_000L,
        startElapsedRealtimeMillis = 0L,
        isStopwatchMode = stopwatch,
        totalStudyBlocks = blocks,
        currentBlockIndex = 1,
        isInSafeBreak = inBreak,
        safeBreakSecondsRemaining = breakRemaining,
        isActive = active,
        completedSuccessfully = false
    )

    // ---- Finding 2: burn-in is a real figure-8, in dp -------------

    @Test
    fun burnIn_wanderReachesTensOfDp_notSixPixels() {
        val points = (0 until 60).map { deskClockBurnInOffsetDp(it) }
        val maxDx = points.maxOf { abs(it.first) }
        val maxDy = points.maxOf { abs(it.second) }
        assertTrue("dx amplitude $maxDx must exceed 20dp", maxDx > 20f)
        assertTrue("dy amplitude $maxDy must exceed 10dp", maxDy > 10f)
        // Every minute the face actually moves (no parked raster cell).
        for (i in points.indices) {
            val a = points[i]
            val b = points[(i + 1) % points.size]
            assertTrue("step $i must move", hypot(a.first - b.first, a.second - b.second) > 1f)
        }
        // Figure-8, not a circle: y crosses zero while x is off-centre
        // (the lemniscate's waist) — some mid-step has |y| small and
        // |x| large.
        assertTrue(points.any { abs(it.second) < 2f && abs(it.first) > 15f })
        // The path is a closed 60-step cycle.
        assertEquals(deskClockBurnInOffsetDp(0), deskClockBurnInOffsetDp(60))
    }

    // ---- Finding 3: the break is announced on the clock -----------

    @Test
    fun breakLine_onlyDuringABreak_andCountsTheBreakItself() {
        assertEquals(
            "SAFE BREAK • 07:32 LEFT — STEP AWAY",
            deskClockBreakLine(row(inBreak = true, breakRemaining = 452))
        )
        assertNull(deskClockBreakLine(row(inBreak = false)))
        assertNull(deskClockBreakLine(row(inBreak = true, breakRemaining = 452, active = false)))
        assertNull(deskClockBreakLine(null))
    }

    // ---- Finding 4: the glance names the number -------------------

    @Test
    fun modeLabel_namesTheDirection() {
        assertEquals("REMAINING", deskClockModeLabel(row()))
        assertEquals("ELAPSED", deskClockModeLabel(row(stopwatch = true)))
        assertEquals("SET TIME", deskClockModeLabel(null))
        assertEquals("SET TIME", deskClockModeLabel(row(active = false)))
    }

    @Test
    fun glanceLine_statesDayTimeAndTheSpansOwnEnd() {
        val s = row() // 2 blocks x 1500 + 1 break x 600 = 3600s span
        val line = deskClockGlanceLine(true, 1_700_000_000_000L, s)
        val expectedEnd = "ENDS " + SimpleDateFormat("HH:mm", Locale.US)
            .format(Date(1_700_000_000_000L + 3_600_000L))
        assertTrue(line, line.endsWith(expectedEnd))
        // #88: day names in the device locale (the test JVM's is en,
        // matching Locale.US here) + the system-setting time.
        val expectedDay = SimpleDateFormat("EEE d MMM", Locale.US)
            .format(Date(1_700_000_000_000L)).uppercase(Locale.US) +
            " • " + SimpleDateFormat("HH:mm", Locale.US)
            .format(Date(1_700_000_000_000L))
        assertTrue(line, line.startsWith(expectedDay))
        // The 12-hour branch prints the same day with a 12h clock.
        val expectedEnd12 = "ENDS " + SimpleDateFormat("h:mm a", Locale.US)
            .format(Date(1_700_000_000_000L + 3_600_000L))
        assertTrue(deskClockGlanceLine(false, 1_700_000_000_000L, s)
            .endsWith(expectedEnd12))
        // Stopwatch: no end is invented.
        assertTrue(deskClockGlanceLine(true, 1_700_000_000_000L, row(stopwatch = true))
            .endsWith("NO SET END"))
        // No session: day/time only, no fabricated end.
        assertEquals(expectedDay, deskClockGlanceLine(true, 1_700_000_000_000L, null))
    }

    @Test
    fun stopwatchBar_cyclesPerLap_neverPinsAtFull() {
        assertEquals(0f, deskClockProgress(row(stopwatch = true, elapsed = 1200)), 0.001f)
        assertEquals(0.5f, deskClockProgress(row(stopwatch = true, elapsed = 1800)), 0.001f)
        assertTrue(deskClockProgress(row(stopwatch = true, elapsed = 1199)) < 1f)
        assertTrue(deskClockProgress(row(stopwatch = true, elapsed = 7200)) < 1f)
        // Timed keeps the span fraction, floored mid-session.
        assertTrue(deskClockProgress(row(elapsed = 0)) >= 0.04f)
        // Idle is honestly empty.
        assertEquals(0f, deskClockProgress(null), 0.001f)
    }

    // ---- Findings 3+5 as rendered: break face, hold-to-exit -------

    @Test
    fun renderedClock_breakFaceAnnouncesAndTapDoesNotDismiss_holdDoes() {
        var dismissed = false
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AlwaysOnDeskClockOverlay(
                    visible = true,
                    activeSession = row(inBreak = true, breakRemaining = 452),
                    activeTask = FocusTaskEntity(title = "Physics"),
                    // Composed at the train merge (K8): dialMinutes
                    // was deleted from the face's contract by the
                    // train's Wave-3 (Glaette G-6) — the print had
                    // already stopped reading it on main (the idle
                    // lie's dead input). Every assertion below stands
                    // as written: the break face derives from the
                    // session row, never from that wire.
                    burnInStep = 0,
                    onDismiss = { dismissed = true }
                )
            }
        }
        repeat(6) { ShadowLooper.idleMainLooper(); Thread.sleep(50) }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("desk_clock_break_line").assertExists()
        composeRule.onNodeWithText("SAFE BREAK • 07:32 LEFT — STEP AWAY").assertExists()
        composeRule.onNodeWithText("REMAINING").assertExists()
        // The task banner is suppressed through the break.
        composeRule.onNodeWithText("Physics").assertDoesNotExist()
        // Issue #143 F2: a sealed session's clock no longer wears
        // the idle face — the eyebrow names the sealed break state
        // (the exit instruction survives verbatim).
        composeRule.onNodeWithText("SEALED — SAFE BREAK • HOLD ANYWHERE TO EXIT").assertExists()

        // A stray tap dismisses nothing.
        composeRule.onNodeWithTag("always_on_desk_clock_overlay").performClick()
        composeRule.waitForIdle()
        assertTrue("tap must not dismiss the desk clock", !dismissed)

        // A deliberate long-press exits. This Compose version
        // (BOM 2024.09) has neither longPress nor longClick in its
        // ui-test API, so the hold is injected by hand: down, hold
        // past the long-press timeout in event time, up.
        composeRule.onNodeWithTag("always_on_desk_clock_overlay")
            .performTouchInput {
                down(center)
                advanceEventTime(2_000L)
                up()
            }
        repeat(6) { ShadowLooper.idleMainLooper(); Thread.sleep(50) }
        composeRule.waitForIdle()
        assertTrue("long-press must dismiss the desk clock", dismissed)
    }
}
