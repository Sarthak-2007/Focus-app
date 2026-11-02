package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.domain.LiftingPlate
import com.example.ui.strongroom.LiftingScreen
import com.example.ui.strongroom.SealedFaceState
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * P6 CONSUMES P3's SEAL-GONE FORM (Ruling R7-1, landed by
 * BUILD-SR-14): at a fallen day's lifting, [LiftingScreen]'s
 * failure branch presents the testimony through P3's renderer —
 * the bracket at the hour's rank, in the dark room — and P6's
 * own ajar placeholder does not stand when the form is supplied.
 * The plate beneath is the same struck plate as ever; this test
 * carries no plate, so only the face is read.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w420dp-h920dp")
class StrongroomLiftingFailureFaceP6Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun fallenLifting_presentsP3sSealGoneForm_notP6sOwnFace() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                LiftingScreen(
                    plate = null,
                    handingBack = LiftingPlate.HandingBack(emptyList()),
                    failure = true,
                    failureFace = SealedFaceState.sealGone(
                        liftingHour = "06:30",
                        sentence = null,
                        standingHour = "23:10",
                        fallenHour = "01:40"
                    )
                )
            }
        }
        composeRule.onNodeWithTag("lifting_screen").assertExists()
        composeRule.onNodeWithTag("sealed_face_bracket", useUnmergedNode = true).assertExists()
        composeRule.onNodeWithTag("sealed_face_ring").assertDoesNotExist()
        composeRule.onNodeWithTag("ajar_face").assertDoesNotExist()
    }
}
