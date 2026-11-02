package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.ui.strongroom.CompositionDraft
import com.example.ui.strongroom.SigningAppOption
import com.example.ui.strongroom.SigningResult
import com.example.ui.strongroom.SigningScreen
import com.example.ui.strongroom.StrongroomCopy
import com.example.ui.theme.MyApplicationTheme
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P4 — THE SIGNING, rendered (ui/strongroom/SigningScreen.kt):
 * the walk end to end — the purpose stands at the composition
 * (B-1), the telling stands at the foot (B-19), a spent day is
 * told by this surface itself (B-4), the clause stands only at
 * the signature (B-5), leaving is possible until the spinning
 * begins and impossible after (the uninterruptible sequence,
 * made visible), and signing hands a SigningResult onward with
 * the day SET — and no swing anywhere on the surface.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomSigningRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun draft(spentDay: Boolean = false) = CompositionDraft(
        dayShapeName = "ordinary",
        appOptions = listOf(
            SigningAppOption("com.social", "Social"),
            SigningAppOption("com.video", "Video")
        ),
        composedLocked = setOf("com.social", "com.video"),
        composedHour = "06:30",
        composedSentence = null,
        spentDay = spentDay
    )

    @Test
    fun signing_walksTheSequence_andHandsTheSetDayOnward() {
        val signed = AtomicReference<SigningResult?>(null)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SigningScreen(
                    draft = draft(),
                    onDetent = {},
                    onLatch = {},
                    onLeave = {},
                    onSigned = { signed.set(it) }
                )
            }
        }
        // The composition: purpose + telling stand; no spent-day
        // telling on an unspent day; leaving is still possible.
        composeRule.onNodeWithTag("signing_purpose").assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.activity.getString(StrongroomCopy.Purpose)).assertExists()
        composeRule.onNodeWithTag("signing_telling").assertExists()
        composeRule.onNodeWithTag("signing_codes_wait").assertDoesNotExist()
        composeRule.onNodeWithTag("signing_leave").assertExists()

        // Amend: unlock Video. Then walk forward, one step each.
        composeRule.onNodeWithTag("signing_app_com.video").performClick()
        composeRule.onNodeWithTag("signing_continue").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("signing_hour_value").assertIsDisplayed()
        composeRule.onNodeWithText("06:30").assertExists()
        composeRule.onNodeWithTag("signing_hour_up").performClick()
        composeRule.onNodeWithText("07:30").assertExists()

        composeRule.onNodeWithTag("signing_continue").performClick()
        composeRule.waitForIdle()
        // No sentence written: the kept place stands.
        composeRule.onNodeWithText("THE KEPT PLACE STANDS").assertExists()

        composeRule.onNodeWithTag("signing_continue").performClick()
        composeRule.waitForIdle()
        // The clause stands here, at the signature — and the
        // moment the spinning begins, leaving is gone.
        composeRule.onNodeWithTag("signing_clause").assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.activity.getString(StrongroomCopy.Clause)).assertExists()
        composeRule.onNodeWithTag("signing_leave").assertExists()
        // A-16 (BUILD-SR-22): the combination is worked through
        // the great wheel — a drag around its centre turns it;
        // the first detent crossed begins the signature.
        composeRule.onNodeWithTag("signing_wheel").performTouchInput {
            val c = center
            val r = width / 4f
            down(Offset(c.x + r, c.y))
            for (step in 1..8) {
                val a = step * (Math.PI / 8.0)
                moveBy(
                    Offset(
                        c.x + (r * kotlin.math.cos(a)).toFloat(),
                        c.y + (r * kotlin.math.sin(a)).toFloat()
                    )
                )
            }
            up()
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("signing_leave").assertDoesNotExist()

        composeRule.onNodeWithTag("signing_sign").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("signing_ready").assertIsDisplayed()
        composeRule.onNodeWithText("The door stands ready. It swings at the first sitting.")
            .assertExists()

        composeRule.onNodeWithTag("signing_done").performClick()
        composeRule.waitForIdle()
        val result = signed.get()
        assertEquals("07:30", result?.liftingHour)
        assertEquals(setOf("com.social"), result?.lockedPackages)
        assertEquals(null, result?.sentence)
    }

    @Test
    fun signing_onASpentDay_saysCodesWaitTonight() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SigningScreen(
                    draft = draft(spentDay = true),
                    onDetent = {},
                    onLatch = {},
                    onLeave = {},
                    onSigned = {}
                )
            }
        }
        composeRule.onNodeWithTag("signing_codes_wait").assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.activity.getString(StrongroomCopy.CodesWaitTonight)).assertExists()
    }
}
