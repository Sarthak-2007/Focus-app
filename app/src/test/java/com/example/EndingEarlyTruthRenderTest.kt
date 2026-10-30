package com.example

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.StudyModePreset
import com.example.service.TactileSoundEngine
import com.example.ui.DisarmChallengeState
import com.example.ui.components.DisarmChallengeOverlay
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Issue #103 — the Deck/overlay truth labels, rendered.
 *
 * As in the cascade render suite, a chip's label lives inside its
 * clickable's merged semantics where the text finder cannot see
 * it, so each truth branch carries its own testTag as its designed
 * handle:
 *  - F1: a judged routine session inside the grace window renders
 *    `routine_judged_end_button`, never `grace_abort_button`.
 *  - F3: a stopwatch session past the 20-minute floor renders
 *    `stopwatch_finish_button`, never the valve's
 *    `request_disarm_button`; below the floor the valve stands.
 *  - F5: a refused hold prints `valve_refusal_message` in the
 *    disarm overlay, on the valve face and on the SPENT face.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class EndingEarlyTruthRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun fakeSession(elapsedSeconds: Int, stopwatch: Boolean = false) = FocusSessionEntity(
        id = 7,
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 3000,
        elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = System.currentTimeMillis(),
        startElapsedRealtimeMillis = 1_000L,
        isActive = true,
        completedSuccessfully = false,
        isStopwatchMode = stopwatch
    )

    private fun setDeck(session: FocusSessionEntity, routineJudged: Boolean = false) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                TactileDeckScreen(
                    activeSession = session,
                    dialMinutes = 50,
                    studyBlocksCount = 2,
                    activeTask = null,
                    queuedTasks = emptyList(),
                    capturedNotificationsCount = 0,
                    soundEngine = TactileSoundEngine(context),
                    isDarkTheme = false,
                    onDialMinutesChanged = {},
                    onSelectStudyPreset = {},
                    onSetStudyBlocksCount = {},
                    onCompleteActiveTask = {},
                    onToggleTaskSubStep = { _, _ -> },
                    onOpenTaskSequencer = {},
                    onArmSession = {},
                    onRequestDisarm = {},
                    onCancelWithinGraceWindow = {},
                    onOpenAlwaysOnDeskClock = {},
                    onOpenNotificationDigest = {},
                    isActiveRoutineJudged = routineJudged
                )
            }
        }
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // The slide's integrator can keep the rule non-idle.
        }
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun manualSessionInsideGrace_rendersGraceAbort() {
        setDeck(fakeSession(elapsedSeconds = 5))
        composeRule.onNodeWithTag("grace_abort_button").assertExists()
        composeRule.onNodeWithTag("routine_judged_end_button").assertDoesNotExist()
    }

    @Test
    fun judgedRoutineInsideGrace_neverPromisesGraceAbort() {
        setDeck(fakeSession(elapsedSeconds = 5), routineJudged = true)
        composeRule.onNodeWithTag("routine_judged_end_button").assertExists()
        composeRule.onNodeWithTag("grace_abort_button").assertDoesNotExist()
    }

    @Test
    fun stopwatchPastFloor_rendersFreeFinishNotTheValve() {
        setDeck(fakeSession(elapsedSeconds = 1300, stopwatch = true))
        composeRule.onNodeWithTag("stopwatch_finish_button").assertExists()
        composeRule.onNodeWithTag("request_disarm_button").assertDoesNotExist()
    }

    @Test
    fun stopwatchBelowFloor_keepsTheValveChip() {
        setDeck(fakeSession(elapsedSeconds = 600, stopwatch = true))
        composeRule.onNodeWithTag("request_disarm_button").assertExists()
        composeRule.onNodeWithTag("stopwatch_finish_button").assertDoesNotExist()
    }

    @Test
    fun refusedHold_printsItsRefusalOnTheValveFace() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                DisarmChallengeOverlay(
                    state = DisarmChallengeState(
                        isVisible = true,
                        refusalMessage = DisarmChallengeState.REFUSAL_NO_TOKEN
                    ),
                    tokensRemaining = 1,
                    onDismissKeepFocusing = {},
                    onCompleteDisarm = {}
                )
            }
        }
        ShadowLooper.idleMainLooper()
        // The overlay's modal tap-guards (clickable(enabled = false)
        // on the Box and the Column) merge descendant semantics, so
        // the refusal's tag/text are only addressable in the
        // unmerged tree — same merged-semantics fact the Deck chips
        // document at the top of this file.
        composeRule.onNodeWithTag("valve_refusal_message", useUnmergedTree = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            DisarmChallengeState.REFUSAL_NO_TOKEN,
            useUnmergedTree = true
        ).assertIsDisplayed()
    }

    @Test
    fun refusedHold_printsItsRefusalOnTheSpentFaceToo() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                DisarmChallengeOverlay(
                    state = DisarmChallengeState(
                        isVisible = true,
                        refusalMessage = DisarmChallengeState.REFUSAL_NO_SESSION
                    ),
                    tokensRemaining = 0,
                    onDismissKeepFocusing = {},
                    onCompleteDisarm = {}
                )
            }
        }
        ShadowLooper.idleMainLooper()
        composeRule.onNodeWithTag("valve_refusal_message", useUnmergedTree = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            DisarmChallengeState.REFUSAL_NO_SESSION,
            useUnmergedTree = true
        ).assertIsDisplayed()
    }
}
