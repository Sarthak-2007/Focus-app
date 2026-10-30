package com.example

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.AirlockInterceptState
import com.example.ui.PeekWithheld
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Issue #11 — the Held Door's render proof on the MERGED face.
 *
 * B-3 DISPOSITION (recorded on issue #11 at the merge): the
 * pre-merge version of this test claimed a "stationarity proof"
 * — RETURN's bounds identical across one live composition
 * travelling wall→0. It never proved that: every frame was a
 * fresh setContent with a new nonce, so it proved cross-visit
 * bounds equality only, and it proved it against the branch's
 * fixed-skeleton face, which the merge superseded with main's
 * Issue-#55 face (per-instance deadline wall, hero numeral,
 * launcher counts). The pixel-stationarity claim is WITHDRAWN,
 * not re-asserted here.
 *
 * What this test does prove, on the face that actually ships:
 *  - RETURN's bounds are equal across separate renders of the
 *    counting, offer and held frames of the same visit class
 *    (cross-visit equality — the honest residue of the claim).
 *  - The offer frame prints PEEK (never "EMERGENCY PEEK"), the
 *    honest m+s badge, and the re-timed price sentence.
 *  - The honest exit stands on the door and routes its tap.
 *  - The memory line prints on repeat visits (FIX-8 wiring).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HeldDoorStationarityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun state(
        canPeek: Boolean = true,
        withheld: PeekWithheld? = null,
        wallRunning: Boolean = true,
        cooldownSecs: Int = 0,
        instance: Long = 1L,
        priorIntercepts: Int = 0
    ): AirlockInterceptState {
        val now = SystemClock.elapsedRealtime()
        return AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = "com.video.app",
            interceptedAppName = "Video App",
            sessionIntention = "Finish the problem set",
            canUse2MinutePeek = canPeek,
            interceptInstanceId = instance,
            interceptedAtMonoMillis = if (wallRunning) now else now - 60_000L,
            peekCooldownEndsAtMonoMillis =
                if (cooldownSecs > 0) now + cooldownSecs * 1000L else 0L,
            peekCooldownRemainingSeconds = cooldownSecs,
            peekWithheld = withheld,
            priorInterceptsThisSession = priorIntercepts
        )
    }

    private fun render(state: AirlockInterceptState) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, isFocusSessionActive = true) {
                AirlockInterceptOverlay(
                    state = state,
                    onSelectReason = {},
                    onTransmuteParticleTap = {},
                    onReturnToFocus = {},
                    onTake2MinutePeek = {},
                    onEndSessionInstead = {}
                )
            }
        }
    }

    private fun returnBounds(): Rect =
        composeRule.onNodeWithTag("airlock_return_button")
            .fetchSemanticsNode().boundsInRoot

    @Test
    fun returnBoundsAreEqualAcrossCountingOfferAndHeldRenders() {
        // COUNTING: wall running (5s taper floor from frame one).
        render(state(wallRunning = true, instance = 1L))
        val counting = returnBounds()
        composeRule.onNodeWithTag("airlock_peek_button").assertDoesNotExist()

        // OFFER: wall expired, the chip stands — RETURN unmoved.
        render(state(wallRunning = false, instance = 2L))
        assertEquals(counting, returnBounds())
        composeRule.onNodeWithTag("airlock_peek_button").assertExists()

        // HELD: cooldown standing, withheld line printed — RETURN
        // unmoved.
        render(
            state(
                canPeek = false,
                withheld = PeekWithheld.COOLDOWN,
                wallRunning = false,
                cooldownSecs = 900,
                instance = 3L
            )
        )
        assertEquals(counting, returnBounds())
        composeRule.onNodeWithText("PEEK COOLDOWN — AGAIN IN 15 MIN").assertIsDisplayed()
    }

    @Test
    fun offerFramePrintsPeekNeverEmergencyPeek() {
        render(state(wallRunning = false, instance = 4L))
        // The dead name exists in no frame of the merged face.
        composeRule.onNodeWithText("EMERGENCY PEEK").assertDoesNotExist()
        composeRule.onNodeWithTag("airlock_peek_button").assertExists()
        // PEEK_DURATION_SECONDS is 120: the honest badge is "2m".
        composeRule.onNodeWithText("2m", substring = true).assertExists()
        // The price stands on the offer, re-timed to the lease's
        // end (main's Issue-#118 wording of the FIX-8 cure).
        composeRule.onNodeWithText(
            "WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN RUNS ON THIS APP"
        ).assertIsDisplayed()
    }

    @Test
    fun honestExitStandsAndRoutes() {
        var exited = false
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, isFocusSessionActive = true) {
                AirlockInterceptOverlay(
                    state = state(wallRunning = false, instance = 5L),
                    onSelectReason = {},
                    onTransmuteParticleTap = {},
                    onReturnToFocus = {},
                    onTake2MinutePeek = {},
                    onEndSessionInstead = { exited = true }
                )
            }
        }
        composeRule.onNodeWithText("END THE SESSION INSTEAD").assertIsDisplayed()
        composeRule.onNodeWithText("END THE SESSION INSTEAD").performClick()
        assertTrue(exited)
    }

    @Test
    fun memoryLinePrintsOnRepeatVisitsOnly() {
        render(state(wallRunning = true, instance = 6L, priorIntercepts = 2))
        composeRule.onNodeWithText("THIRD TIME THIS SESSION").assertIsDisplayed()
        render(state(wallRunning = true, instance = 7L, priorIntercepts = 0))
        composeRule.onNodeWithText("THIRD TIME THIS SESSION").assertDoesNotExist()
    }
}
