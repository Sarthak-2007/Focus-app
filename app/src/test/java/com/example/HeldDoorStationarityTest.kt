package com.example

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
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
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration
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
            // An expired wall is stamped at the monotonic epoch
            // (1L): always positive, always long past — a relative
            // "now minus a minute" can go negative under
            // Robolectric's young clock, and the overlay lawfully
            // discards a non-positive stamp and restarts the wall.
            interceptedAtMonoMillis = if (wallRunning) now else 1L,
            peekCooldownEndsAtMonoMillis =
                if (cooldownSecs > 0) now + cooldownSecs * 1000L else 0L,
            peekCooldownRemainingSeconds = cooldownSecs,
            peekWithheld = withheld,
            priorInterceptsThisSession = priorIntercepts
        )
    }

    // One composition per test (the Android compose rule forbids
    // a second setContent); visits swap the held state instead —
    // which is also the more honest frame change: same
    // composition, new door state.
    private var heldState by mutableStateOf<AirlockInterceptState?>(null)
    private var mounted = false

    private fun render(state: AirlockInterceptState) {
        heldState = state
        if (mounted) return
        mounted = true
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, isFocusSessionActive = true) {
                heldState?.let {
                    AirlockInterceptOverlay(
                        state = it,
                        onSelectReason = {},
                        onTransmuteParticleTap = {},
                        onReturnToFocus = {},
                        onTake2MinutePeek = {},
                        onEndSessionInstead = {}
                    )
                }
            }
        }
    }

    // The overlay's wall reads the real monotonic clock, which
    // Robolectric freezes; travelling past the wall means moving
    // BOTH the shadow clock (what the overlay reads) and the
    // compose test clock (what its ticker delays run on) — the
    // same pairing the repo's other clock tests use.
    private fun travelPastWall() {
        ShadowSystemClock.advanceBy(Duration.ofSeconds(11))
        composeRule.waitForIdle()
    }

    // waitUntil pumps the compose test clock while polling, which
    // is what runs the overlay ticker's delays until its
    // recomputed countdown (now reading the advanced shadow
    // clock) reaches zero and the offer chip composes.
    private fun awaitPeekChip() {
        composeRule.waitUntil(timeoutMillis = 10_000L) {
            composeRule.onAllNodesWithTag("airlock_peek_button")
                .fetchSemanticsNodes().isNotEmpty()
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
        travelPastWall()
        awaitPeekChip()
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
        travelPastWall()
        awaitPeekChip()
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
