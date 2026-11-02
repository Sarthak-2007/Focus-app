package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
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

    /** The standing hour on the signing's hour step, read off
     *  the value node itself. */
    private fun hourText(): String =
        composeRule.onNodeWithTag("signing_hour_value")
            .fetchSemanticsNode()!!
            .config[SemanticsProperties.Text][0].text

    /** E-15, driven as a hand drives it — by eye (BUILD-SR-24
     *  re-aim): the hour is set by the drag strip, never a
     *  stepper. The strip travels 2 minutes per dp and its
     *  release snaps to a quarter inside ±4 minutes, so each
     *  drag aims 2 dp PAST the target (the snap window absorbs
     *  whatever fraction of the gesture touch slop spends),
     *  then the value is read back and corrected until it
     *  stands. Three passes bound the walk; the assertion
     *  after the loop is the verdict. */
    private fun dragHourTo(target: String) {
        fun minutesOf(hhmm: String) =
            hhmm.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
        repeat(3) {
            val current = hourText()
            if (current == target) return
            var delta = minutesOf(target) - minutesOf(current)
            if (delta > 720) delta -= 1440
            if (delta < -720) delta += 1440
            val travelDp = delta / 2f + if (delta >= 0) 2f else -2f
            composeRule.onNodeWithTag("signing_hour_drag").performTouchInput {
                val startX = if (travelDp >= 0) center.x - 120f else center.x + 120f
                val travelPx = travelDp * density
                // Down / stepped moves / up — this Compose
                // version's touch scope has no swipe member;
                // the strip accumulates per-event deltas, so
                // the steps sum to the same travel.
                down(Offset(startX, center.y))
                repeat(10) {
                    moveBy(Offset(travelPx / 10f, 0f))
                }
                up()
            }
            composeRule.waitForIdle()
        }
        assertEquals(target, hourText())
    }

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
        // RE-AIMED at the end phase (BUILD-SR-24): the retired
        // stepper tag signing_hour_up stands nowhere — E-15's
        // governed drag strip sets the hour in its place.
        dragHourTo("07:30")
        composeRule.onNodeWithText("07:30").assertExists()

        composeRule.onNodeWithTag("signing_continue").performClick()
        composeRule.waitForIdle()
        // No sentence written: the kept place stands.
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // the deck struck the sentence step's headings and
        // captions — no kept-place caption stands anywhere;
        // the step is its fields alone, and the kept place is
        // proven by the RESULT at the walk's end (the handed-on
        // sentence is null), not by furniture copy.
        composeRule.onNodeWithTag("signing_sentence_field").assertIsDisplayed()
        composeRule.onNodeWithText("THE KEPT PLACE STANDS").assertDoesNotExist()

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
        // RE-AIMED at the end phase (BUILD-SR-24) to the deck:
        // §1 struck every post-spin confirmation line — the
        // sealing itself is the confirmation, and no
        // signing_ready sheet stands; the READY step is its
        // DONE handle alone, which hands the result onward.
        composeRule.onNodeWithTag("signing_done").assertIsDisplayed()
        composeRule.onNodeWithText("The door stands ready. It swings at the first sitting.")
            .assertDoesNotExist()

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
