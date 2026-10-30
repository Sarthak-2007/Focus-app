package com.example

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Audit R3 #113, finding 1 — the HELD door outlives the session.
 *
 * The digest's only door used to be a chip composed only while a
 * session runs, so a session that ended unwatched stranded every
 * notification it had captured. On the IDLE Deck the chip now
 * stands whenever anything stands captured — and, just as
 * binding, an empty capture seats no door at all.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w400dp-h800dp")
class HeldDoorRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun setIdleDeck(capturedCount: Int) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                TactileDeckScreen(
                    activeSession = null,
                    dialMinutes = 50,
                    studyBlocksCount = 1,
                    studyPreset = com.example.data.StudyModePreset.POMODORO_25_5,
                    checklist = emptyList(),
                    activeTask = null,
                    queuedTasks = emptyList(),
                    completedTasksCount = 0,
                    totalQueueMinutes = 0,
                    selectedProfileName = "Default",
                    intentionText = "Deep Study",
                    totalFocusedMinutes = 0,
                    totalInterceptions = 0,
                    profiles = emptyList(),
                    apps = emptyList(),
                    shortsReelsSniperEnabled = true,
                    browserUrlBlockerEnabled = true,
                    capturedNotificationsCount = capturedCount,
                    soundEngine = com.example.service.TactileSoundEngine(context),
                    isDarkTheme = false,
                    onDialMinutesChanged = {},
                    onSelectStudyPreset = {},
                    onSetStudyBlocksCount = {},
                    onToggleChecklistItem = {},
                    onAddChecklistItem = {},
                    onCompleteActiveTask = {},
                    onDeferActiveTask = {},
                    onExtendActiveTaskOvertime = {},
                    onAddQuickTaskToQueue = { _, _ -> },
                    onToggleTaskSubStep = { _, _ -> },
                    onOpenTaskSequencer = {},
                    onOpenCurioVault = {},
                    onIntentionChanged = {},
                    onProfileSelected = {},
                    onArmSession = {},
                    onRequestDisarm = {},
                    onCancelWithinGraceWindow = {},
                    onOpenAlwaysOnDeskClock = {},
                    onOpenNotificationDigest = {}
                )
            }
        }
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // The idle Deck's views can keep the rule non-idle;
            // the semantics tree is what this test reads.
        }
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun idleDeckWithCapturedNotificationsSeatsTheHeldDoor() {
        setIdleDeck(capturedCount = 3)
        // Existence in the tree is the finding's claim — the door
        // is composed on the idle Deck (it sits in the scroll
        // column, so displayed-ness is the scroller's business).
        composeRule.onNodeWithText("HELD").assertExists()
    }

    @Test
    fun idleDeckWithNothingCapturedSeatsNoHeldDoor() {
        setIdleDeck(capturedCount = 0)
        composeRule.onNodeWithText("HELD").assertDoesNotExist()
    }
}
