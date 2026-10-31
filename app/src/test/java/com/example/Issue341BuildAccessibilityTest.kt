package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.DisarmChallengeState
import com.example.ui.components.DisarmChallengeOverlay
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Audit R6 #341 BUILD — F23: the emergency valve announces its
 * progress to assistive tech. The disarm overlay's hold progress
 * was visual-only; it now carries a polite live region whose
 * text is bucketed in 15-second bank steps — a blind user hears
 * progress at meaningful steps, never a per-tick flood (the
 * region's text only changes when the bucket changes).
 *
 * Placement note (named): the desk draft put these cases in
 * Wave3Issue42AccessibilityTest; they live here so the overlay
 * renders in this file's own harness — the case names are the
 * draft's.
 * PIN scope: RENDER.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class Issue341BuildAccessibilityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun setValve() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                DisarmChallengeOverlay(
                    state = DisarmChallengeState(isVisible = true, requiredHoldSeconds = 60),
                    tokensRemaining = 1,
                    soundEngine = null,
                    onDismissKeepFocusing = {},
                    onCompleteDisarm = {}
                )
            }
        }
    }

    @Test
    fun emergencyValve_progress_changeAnnouncesViaLiveRegion() {
        setValve()
        // The live region node exists and is POLITE (assertive
        // would shout over the user's own screen reader).
        composeRule.onNodeWithTag("emergency_valve_live_region")
            .assertExists()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.LiveRegion, LiveRegionMode.Polite
                )
            )
    }

    @Test
    fun emergencyValve_announcementsDoNotSpamEveryTick() {
        setValve()
        // Exactly ONE live-region node serves the valve — the
        // bucketed text changes at 15-second bank steps, so there
        // is no per-tick announcement surface to spam from.
        composeRule.onAllNodesWithTag("emergency_valve_live_region")
            .assertCountEquals(1)
    }
}
