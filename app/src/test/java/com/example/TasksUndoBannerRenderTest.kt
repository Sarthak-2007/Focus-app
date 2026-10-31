package com.example

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import com.example.data.FocusTaskEntity
import com.example.data.TaskQueueStatus
import com.example.ui.screens.FocusSequencerScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #106, finding 3: delete a task low in the queue and the
 * UNDO banner must be BORN VISIBLE. The banner is an item near
 * the top of the Tasks list; this test scrolls the list to its
 * foot first (where a low delete is tapped), then lands the
 * delete — the screen must bring the banner into view itself,
 * inside its 5-second life, without the user scrolling back up.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TasksUndoBannerRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun undoBanner_scrollsIntoView_whenDeleteLandsWhileScrolledDown() {
        val tasks = (1..12).map { n ->
            FocusTaskEntity(
                id = n,
                title = "Task number $n",
                estimatedMinutes = 0,
                categoryTag = "Study",
                queueOrder = n - 1,
                status = if (n == 1) TaskQueueStatus.ACTIVE.name else TaskQueueStatus.QUEUED.name
            )
        }
        val deleted = mutableStateOf<FocusTaskEntity?>(null)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                FocusSequencerScreen(
                    activeTask = tasks.first(),
                    queuedTasks = tasks.drop(1),
                    completedTasks = emptyList(),
                    deletedTaskForUndo = deleted.value,
                    isSessionActive = false,
                    soundEngine = null,
                    isDarkTheme = false,
                    onAddTask = { _, _, _, _, _ -> },
                    onCueTaskToFocus = {},
                    onCompleteTask = {},
                    onDeleteTask = {},
                    onUndoDeleteTask = {},
                    onDismissUndoBanner = {},
                    onReopenTask = {},
                    onToggleSubStep = { _, _ -> },
                    // Composed at the train merge (K8): the train's
                    // sequencer screen requires the sub-step callback
                    // (the identical test predates it on both trees);
                    // stubbed like every other callback here — the
                    // undo banner under test never touches it.
                    onAddSubStep = { _, _ -> },
                    onAutoSplitTask = {},
                    onNavigateToDeck = {}
                )
            }
        }
        composeRule.waitForIdle()
        // The user is at the foot of a real day's queue.
        composeRule.onNodeWithTag("focus_sequencer_screen").performScrollToIndex(13)
        composeRule.waitForIdle()
        // The low delete lands; its banner is born at the top.
        composeRule.runOnUiThread { deleted.value = tasks.last() }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("undo_delete_task_button").assertIsDisplayed()
    }
}
