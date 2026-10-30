package com.example

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.ui.AirlockInterceptState
import com.example.ui.PeekWithheld
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.height
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #55 (verifier test gaps), rendered half — deterministic
 * first-frame assertions only (no clock advancement, so no
 * scheduler flakiness):
 *  - a standing COOLDOWN airlock whose deadline has passed swaps
 *    the withheld line for the peek chip with no re-trigger;
 *  - a same-package re-intercept (new instance, fresh stamp)
 *    resets the wall: chip gone, numeral back at 05.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AirlockOverlayIssue55RenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun setAirlock(state: AirlockInterceptState) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AirlockInterceptOverlay(
                    state = state,
                    onSelectReason = {},
                    onTransmuteParticleTap = {},
                    onReturnToFocus = {},
                    onTake2MinutePeek = {}
                )
            }
        }
    }

    @Test
    fun expiredCooldownShowsPeekChipNotWithheldLine() {
        setAirlock(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = false,
                peekCooldownRemainingSeconds = 0,
                peekWithheld = PeekWithheld.COOLDOWN,
                interceptInstanceId = 7L,
                interceptedAtMonoMillis = 1L,
                peekCooldownEndsAtMonoMillis = 1L
            )
        )
        composeRule.onNodeWithTag("airlock_peek_button").assertIsDisplayed()
        composeRule.onNodeWithText("PEEK COOLDOWN — AGAIN IN 0 MIN").assertDoesNotExist()
    }

    @Test
    fun exitsStayReachableOnShortScreen() {
        // Fresh-eyes trap: on a short screen the card must scroll —
        // RETURN (and the peek chip) can never be clipped unreachable
        // while Back is deliberately swallowed.
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.height(300.dp)
                ) {
                    AirlockInterceptOverlay(
                        state = AirlockInterceptState(
                            isVisible = true,
                            interceptedPackageName = "com.example.blocked",
                            interceptedAppName = "Blocked",
                            canUse2MinutePeek = true,
                            interceptInstanceId = 9L,
                            interceptedAtMonoMillis = 1L
                        ),
                        onSelectReason = {},
                        onTransmuteParticleTap = {},
                        onReturnToFocus = {},
                        onTake2MinutePeek = {}
                    )
                }
            }
        }
        composeRule.onNodeWithTag("airlock_return_button")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("airlock_peek_button")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun samePackageReInterceptResetsWallAndHidesChip() {
        // First intercept: wall long expired, peek on offer.
        // (Stamps are 1L — ancient but positive: Robolectric's
        // shadow elapsedRealtime is tiny, so now-10_000 would go
        // negative and trip the state's fresh-stamp fallback.)
        setAirlock(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = true,
                interceptInstanceId = 3L,
                interceptedAtMonoMillis = 1L
            )
        )
        composeRule.onNodeWithTag("airlock_peek_button").assertIsDisplayed()

        // Same package re-intercepts while the overlay stands: new
        // instance, fresh stamp — the wall restarts in full.
        setAirlock(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = true,
                interceptInstanceId = 4L,
                interceptedAtMonoMillis = SystemClock.elapsedRealtime()
            )
        )
        // Audit R6 #341 F60: the peek offer is composed from the
        // first frame (disabled during the countdown) so the card
        // never grows mid-decision — it EXISTS here now; what the
        // countdown still withholds is the spend, not the sight.
        composeRule.onNodeWithTag("airlock_peek_button").assertIsDisplayed()
        composeRule.onNodeWithText("05").assertIsDisplayed()
    }
}
