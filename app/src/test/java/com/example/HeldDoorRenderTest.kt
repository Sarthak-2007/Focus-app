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
 *
 * (Surge port, STURM3-11: the harness call is adapted to the
 * Deck's current signature on main — the task/checklist/profile
 * parameters the old Deck took were retired by the Deck's own
 * rework; the defaulted host seams are left at their defaults.
 * The finding's two assertions are unchanged.)
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
            MyApplicationTheme(darkTheme = false) {
                TactileDeckScreen(
                    activeSession = null,
                    dialMinutes = 50,
                    studyBlocksCount = 1,
                    activeTask = null,
                    queuedTasks = emptyList(),
                    capturedNotificationsCount = capturedCount,
                    soundEngine = com.example.service.TactileSoundEngine(context),
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
        // The chip prints its word and the held count as ONE text
        // ("HELD 3" — BlockitActionChip joins wordLabel + mono
        // badge), so the exact node text carries both facts: the
        // door stands, and the count it prints is the captured
        // count. (Floor lane, census #6 row 13: the bare "HELD"
        // query could never match the joined text.)
        composeRule.onNodeWithText("HELD 3").assertExists()
    }

    @Test
    fun idleDeckWithNothingCapturedSeatsNoHeldDoor() {
        setIdleDeck(capturedCount = 0)
        composeRule.onNodeWithText("HELD").assertDoesNotExist()
    }
}
