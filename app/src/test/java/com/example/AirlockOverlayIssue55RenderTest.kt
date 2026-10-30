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
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
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
        val now = SystemClock.elapsedRealtime()
        setAirlock(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = false,
                peekCooldownRemainingSeconds = 0,
                peekWithheld = PeekWithheld.COOLDOWN,
                interceptInstanceId = 7L,
                interceptedAtMonoMillis = now - 10_000L,
                peekCooldownEndsAtMonoMillis = now - 1L
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
        val now = SystemClock.elapsedRealtime()
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
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
                            interceptedAtMonoMillis = now - 10_000L
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
        val now = SystemClock.elapsedRealtime()
        // First intercept: wall long expired, peek on offer.
        setAirlock(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = true,
                interceptInstanceId = 3L,
                interceptedAtMonoMillis = now - 10_000L
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
        composeRule.onNodeWithTag("airlock_peek_button").assertDoesNotExist()
        composeRule.onNodeWithText("05").assertIsDisplayed()
    }
}
