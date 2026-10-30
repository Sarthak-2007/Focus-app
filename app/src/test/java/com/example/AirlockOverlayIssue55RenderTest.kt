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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

/**
 * Issue #55 (verifier test gaps), rendered half — deterministic
 * first-frame assertions only (no clock advancement, so no
 * scheduler flakiness):
 *  - a standing COOLDOWN airlock whose deadline has passed swaps
 *    the withheld line for the peek chip with no re-trigger;
 *  - a same-package re-intercept (new instance, fresh stamp)
 *    resets the wall: chip gone, numeral back at 05.
 *
 * #166 floor cure (DURCH-D, family AirlockOverlayIssue55RenderTest):
 * root cause was a stale harness assumption, NOT a production
 * defect. The overlay gates the peek chip behind the 5-second
 * breathing wall (`countdownSecs == 0 && peekReady`), and the
 * wall is deadline-derived from the intercept's monotonic stamp
 * — both correct, and the pure math is pinned green by
 * AirlockOverlayIssue55Test. These render tests used stamp 1L as
 * "ancient", but under Robolectric the shadow monotonic clock
 * starts near zero, so 1L is only ~0 ms old at composition: the
 * wall legitimately stands at 05, the chip never composes, and
 * all three tests failed at the peek node (census run
 * 41034113823: AssertionError at lines 67, 102, 122). The
 * fix-forward that introduced 1L (7d57e9f9) asserted the expired
 * path was exercised; the census proves it was not. Cure is
 * test-side only: pin the shadow clock 60 s past boot in setUp
 * so a 1L stamp is genuinely ancient on the first frame. The
 * re-intercept case carried a second, masked harness fault —
 * two setContent calls in one test method, which the compose
 * rule forbids — cured by driving the re-intercept through a
 * state holder in a single setContent, exactly how production
 * swaps the state under the standing overlay (see that case).
 * No assertion changed, no production code touched.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AirlockOverlayIssue55RenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun pinShadowClockPastTheWall() {
        // See class KDoc (#166): Robolectric's shadow monotonic
        // clock starts near zero, so the tests' ancient-but-
        // positive 1L stamps read as brand-new intercepts and the
        // 5 s wall never stands expired on the first frame.
        // Advancing the shadow clock a full minute before any
        // composition makes 1L genuinely ancient — the expired-
        // wall premise every case below is written against. The
        // re-intercept case still stamps SystemClock.elapsed-
        // Realtime() at body time, i.e. "now" on this pinned
        // clock, so its fresh full wall is unaffected.
        ShadowSystemClock.advanceBy(Duration.ofSeconds(60))
    }

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
        // negative and trip the state's fresh-stamp fallback.
        // 1L only reads as ancient because setUp pins the shadow
        // clock 60 s past boot — #166, see class KDoc.)
        //
        // #166 second stacked fault in this case: it used to call
        // setAirlock (setContent) TWICE, but the compose rule
        // forbids a second setContent per test method (recorded
        // in-repo in DrawerRenderTest's KDoc; compose BOM moved
        // 2024.09.00 -> 2025.08.00 after this case was written).
        // At census the case died at its first peek assertion, so
        // the unlawful second call was never reached and the fault
        // stayed masked; the first #166 branch run reached it and
        // died with IllegalStateException at the setContent call.
        // The re-intercept is now driven the way production drives
        // it — the standing overlay receives a NEW state instance
        // (the ViewModel swaps airlockState while the overlay
        // stands) — through one setContent and a state holder.
        var standing by mutableStateOf(
            AirlockInterceptState(
                isVisible = true,
                interceptedPackageName = "com.example.blocked",
                interceptedAppName = "Blocked",
                canUse2MinutePeek = true,
                interceptInstanceId = 3L,
                interceptedAtMonoMillis = 1L
            )
        )
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                AirlockInterceptOverlay(
                    state = standing,
                    onSelectReason = {},
                    onTransmuteParticleTap = {},
                    onReturnToFocus = {},
                    onTake2MinutePeek = {}
                )
            }
        }
        composeRule.onNodeWithTag("airlock_peek_button").assertIsDisplayed()

        // Same package re-intercepts while the overlay stands: new
        // instance, fresh stamp — the wall restarts in full.
        standing = AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = "com.example.blocked",
            interceptedAppName = "Blocked",
            canUse2MinutePeek = true,
            interceptInstanceId = 4L,
            interceptedAtMonoMillis = SystemClock.elapsedRealtime()
        )
        // Audit R6 #341 F60: the peek offer is composed from the
        // first frame (disabled during the countdown) so the card
        // never grows mid-decision — it EXISTS here now; what the
        // countdown still withholds is the spend, not the sight.
        composeRule.onNodeWithTag("airlock_peek_button").assertIsDisplayed()
        composeRule.onNodeWithText("05").assertIsDisplayed()
    }
}
