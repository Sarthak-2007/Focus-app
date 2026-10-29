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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Issue #11, innovator P-1c — THE STATIONARITY PROOF. The audit's
 * worst defect (F-2) was geometric: at countdown 0 the old card
 * grew ≈110dp and RETURN jumped ≈55dp under a travelling thumb.
 * The Skeleton Law (designer §1) says RETURN's bounds are
 * identical at wall-max, wall-1, wall-0/offer, and held. This
 * test is that law as a tripwire: it renders the door across its
 * state matrix and asserts the RETURN slab's bounds in root do
 * not move by a pixel within the peek-owing visit class, that
 * the price stands from frame one while counting, that the
 * withheld line stands from frame one while held, and that the
 * honest exit exists even on a door that owes no peek. Since
 * Tor's round 8 the card is bottom-anchored, so the last test
 * also asserts RETURN's address is identical ACROSS the two
 * visit classes — screen-space invariance, not just within a
 * visit.
 *
 * The frames are evidence for the overlay's rendering only —
 * the same honesty rule as the tree's other render tests.
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
        wallSecs: Long? = 10L,
        cooldownSecs: Long? = null,
        nonce: Long = 1L,
        priorIntercepts: Int = 0,
        justExpiredLease: Boolean = false,
        nextBreakAtMillis: Long? = null
    ): AirlockInterceptState {
        val now = SystemClock.elapsedRealtime()
        return AirlockInterceptState(
            isVisible = true,
            interceptedPackageName = "com.video.app",
            interceptedAppName = "Video App",
            interceptReasonBadge = "Blocked",
            sessionIntention = "Finish the problem set",
            canUse2MinutePeek = canPeek,
            wallEndsAtMono = wallSecs?.let { now + it * 1000L } ?: 0L,
            wallTotalSeconds = wallSecs?.toInt() ?: 0,
            peekCooldownEndsAtMono = cooldownSecs?.let { now + it * 1000L } ?: 0L,
            interceptNonce = nonce,
            peekWithheld = withheld,
            priorInterceptsThisSession = priorIntercepts,
            justExpiredLease = justExpiredLease,
            nextBreakAtMillis = nextBreakAtMillis
        )
    }

    private fun render(state: AirlockInterceptState) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false, isFocusSessionActive = true) {
                AirlockInterceptOverlay(
                    state = state,
                    onReturnToFocus = {},
                    onTake2MinutePeek = {},
                    onEndSessionInstead = {},
                    onDoorStateRefresh = {}
                )
            }
        }
    }

    private fun returnBounds(): Rect =
        composeRule.onNodeWithTag("airlock_return_button")
            .fetchSemanticsNode().boundsInRoot

    @Test
    fun returnBoundsAreIdenticalAcrossTheWholeVisit() {
        // COUNTING at the taper's cap (wall 10).
        render(state(wallSecs = 10L, nonce = 1L))
        val counting10 = returnBounds()
        // The gate line and the price both stand from frame one;
        // the chip does not exist yet.
        composeRule.onNodeWithText("PEEK OPENS IN 0:10").assertIsDisplayed()
        composeRule.onNodeWithText(
            "WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP"
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("airlock_peek_button").assertDoesNotExist()
        composeRule.onNodeWithText("EMERGENCY PEEK").assertDoesNotExist()

        // COUNTING at wall 1: the gate's figures changed; nothing
        // moved.
        render(state(wallSecs = 1L, nonce = 2L))
        assertEquals(counting10, returnBounds())

        // OFFER (wall expired): the chip stands in Z1; RETURN's
        // bounds are the counting bounds, pixel for pixel.
        render(state(wallSecs = -1L, nonce = 3L))
        assertEquals(counting10, returnBounds())
        composeRule.onNodeWithTag("airlock_peek_button").assertExists()
        // Wave-2 (Deep-thinker): the dead name is asserted HERE,
        // in the one frame where the chip actually stands — the
        // counting-frame assertion alone would stay green if the
        // offer chip were renamed back to it. The chip's badge is
        // likewise asserted at the surface: PEEK_DURATION_SECONDS
        // is 120, so the honest m+s badge prints "2m".
        composeRule.onNodeWithText("EMERGENCY PEEK").assertDoesNotExist()
        composeRule.onNodeWithText("2m", substring = true).assertExists()
        // The honest exit stands on peek-owing doors too, not
        // only on the never-owed door (Wave-2, Deep-thinker).
        composeRule.onNodeWithText("END THE SESSION INSTEAD").assertIsDisplayed()

        // HELD (cooldown): the withheld line stands from frame
        // one, the zone is the same 110dp, RETURN unmoved.
        render(
            state(
                canPeek = false,
                withheld = PeekWithheld.COOLDOWN,
                wallSecs = null,
                cooldownSecs = 900L,
                nonce = 4L
            )
        )
        assertEquals(counting10, returnBounds())
        composeRule.onNodeWithText("PEEK COOLDOWN — AGAIN IN 15 MIN").assertIsDisplayed()
    }

    @Test
    fun neverOwedDoorHasNoPeekZoneButKeepsItsExit() {
        render(state(canPeek = false, withheld = null, wallSecs = null, nonce = 5L))
        // Baseline law 4: no peek line of any kind.
        composeRule.onNodeWithTag("airlock_peek_button").assertDoesNotExist()
        composeRule.onNodeWithText(
            "WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP"
        ).assertDoesNotExist()
        // RETURN and the honest exit stand on every door.
        composeRule.onNodeWithTag("airlock_return_button").assertIsDisplayed()
        composeRule.onNodeWithText("END THE SESSION INSTEAD").assertIsDisplayed()
        // The footer carries the badge word alone.
        composeRule.onNodeWithText("BLOCKED").assertIsDisplayed()
        // Tor round 8 (FOLGE gap pass, F7): the card is
        // BOTTOM-anchored, so RETURN's screen-space address is
        // branch-invariant — the never-owed visit class and the
        // peek-owing class put RETURN at the SAME address (the
        // card grows upward; the stack below RETURN never
        // changes height). The pre-round-8 top-anchored build
        // asserted these DIFFER; that assertion died with the
        // anchor it described.
        val neverOwed = returnBounds()
        render(state(wallSecs = 10L, nonce = 6L))
        assertEquals(neverOwed, returnBounds())
    }

    @Test
    fun returnBoundsHoldWithMemoryErrandAndHomecomingContent() {
        // Wave-2 (Designer-29y): the wave-1 frames never composed
        // the three variable-content risks — the memory line, the
        // errand sub-line, the homecoming greeting. All three
        // stand in fixed slots; RETURN must not move for any.
        render(state(wallSecs = 10L, nonce = 8L))
        val plain = returnBounds()

        // MEMORY + ERRAND composed: third visit this session,
        // a break appointment on the sub-line.
        render(
            state(
                wallSecs = 10L,
                nonce = 9L,
                priorIntercepts = 2,
                nextBreakAtMillis = 1_800_000_000_000L
            )
        )
        composeRule.onNodeWithText("THIRD TIME THIS SESSION").assertIsDisplayed()
        assertEquals(plain, returnBounds())

        // HOMECOMING composed: the lease's own expiry echo,
        // greeted in the withheld tenant, cooldown still running.
        render(
            state(
                canPeek = false,
                withheld = PeekWithheld.COOLDOWN,
                wallSecs = null,
                cooldownSecs = 900L,
                nonce = 10L,
                justExpiredLease = true
            )
        )
        composeRule.onNodeWithText("THE PEEK ENDED").assertIsDisplayed()
        assertEquals(plain, returnBounds())
    }

    @Test
    fun honestExitRoutesOutOfTheDoor() {
        // Wave-2 (Deep-thinker): the exit is asserted as a ROUTE,
        // not just a print — its tap must reach the caller's
        // end-session-instead handler (which owns the P-2 guards).
        var exited = false
        composeRule.setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false, isFocusSessionActive = true) {
                AirlockInterceptOverlay(
                    state = state(wallSecs = -1L, nonce = 11L),
                    onReturnToFocus = {},
                    onTake2MinutePeek = {},
                    onEndSessionInstead = { exited = true },
                    onDoorStateRefresh = {}
                )
            }
        }
        composeRule.onNodeWithText("END THE SESSION INSTEAD").performClick()
        org.junit.Assert.assertTrue(exited)
    }

    @Test
    fun peekOwingFooterPrintsPeekAloneNeverTheDurationLiteral() {
        render(state(wallSecs = 10L, nonce = 7L))
        // Tor's ratification: on peek-tier doors the footer is
        // PEEK alone — the tier badge's "2M PEEK" literal never
        // prints on this face.
        composeRule.onNodeWithText("2M PEEK").assertDoesNotExist()
        // The footer's own node is exactly "PEEK" (the gate line's
        // longer string is a different node).
        composeRule.onNodeWithText("PEEK").assertExists()
    }
}
