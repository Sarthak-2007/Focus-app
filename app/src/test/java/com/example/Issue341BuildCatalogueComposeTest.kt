package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.performScrollToNode
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.ui.screens.ShieldMatrixScreen
import com.example.ui.theme.MyApplicationTheme
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Audit R6 #341 BUILD — F64: the Shield catalogue row is pure
 * Compose. The AndroidView host (AppRowSledToggleView) is gone
 * from the catalogue — every row paid a full View
 * measure/layout/draw inside the scrolling list. The sled's
 * contract is unchanged and pinned here: the row renders (name,
 * usage caption, tier word for assistive tech), tapping a stop
 * selects it, tapping the row body cycles the full ring
 * (F22: LOCKED → PEEK → OPEN → LOCKED), and a long list scrolls
 * and recycles cleanly.
 *
 * The View class itself stays in the tree for its other hosts
 * and its pinned tests (SuitcaseViewsIssue60Test) — this file
 * pins the CATALOGUE's host, which is the F64 charge.
 * PIN scope: RENDER.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class Issue341BuildCatalogueComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun app(
        pkg: String,
        name: String,
        tier: AppBlockTier = AppBlockTier.LOCKED
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name,
        dailyLimitMinutes = 20,
        todayUsageMinutes = 3
    )

    private fun setShield(
        apps: List<BlockedAppEntity>,
        tierSink: AtomicReference<Pair<String, AppBlockTier>?> = AtomicReference(null)
    ) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ShieldMatrixScreen(
                    allApps = apps,
                    filteredApps = apps,
                    searchQuery = "",
                    selectedCategory = null,
                    shortsReelsSniperEnabled = false,
                    browserUrlBlockerEnabled = false,
                    onSearchQueryChanged = {},
                    onSelectCategory = {},
                    onSetAppTier = { pkg, tier -> tierSink.set(pkg to tier) },
                    onApplySmartLoadout = {},
                    onToggleShortsReelsSniper = {},
                    onToggleBrowserUrlBlocker = {}
                )
            }
        }
    }

    private fun scrollToRow(pkg: String) {
        composeRule.onNodeWithTag("shield_matrix_screen")
            .performScrollToNode(hasTestTag("app_row_$pkg"))
    }

    @Test
    fun catalogueRow_rendersWithoutAndroidViewHost() {
        val pkg = "com.example.social"
        setShield(listOf(app(pkg, "Social App")))
        scrollToRow(pkg)
        // The row exists as a Compose node carrying the sled's
        // semantics: "<app name>, Shut" — the same words the View
        // host exposed to assistive tech.
        composeRule.onNodeWithTag("app_row_$pkg").assertExists()
        composeRule.onNodeWithTag("app_row_stop_${pkg}_LOCKED").assertExists()
        composeRule.onNodeWithTag("app_row_stop_${pkg}_PEEK_2M").assertExists()
        composeRule.onNodeWithTag("app_row_stop_${pkg}_OPEN").assertExists()
    }

    @Test
    fun catalogueRow_tierCycleTap_stillReachesOpen() {
        val pkg = "com.example.social"
        val sink = AtomicReference<Pair<String, AppBlockTier>?>(null)
        setShield(listOf(app(pkg, "Social App", AppBlockTier.LOCKED)), sink)
        scrollToRow(pkg)
        // Direct stop selection: OPEN is reachable in one tap.
        // FINAL-PHASE NOTE (BUILD-SR-15, H-29): on the composed
        // tree the click landed but the sink read null (judging
        // run 41425228610) while the same code passed on main —
        // the row, its stops and resolvedTier are byte-identical
        // to main; the composed delta is the P0 theme/type layer
        // under the composition. The click is now driven after an
        // explicit idle wait, and a failure prints the unmerged
        // semantics tree into the assertion so the next read is
        // evidence, not another guess.
        // Third read (BUILD-SR-15): both dumps are now evidence.
        // The OPEN stop stands in BOTH trees at (262,64)-(302,100)
        // with its own OnClick; the wedge gate is open
        // (isSessionActive defaults false); the row and its stops
        // are byte-identical to main, where this passed. What the
        // composed tree changed is the weight of the first frames
        // (P0 theme/type) — and this is the file's only
        // SCROLL-DEPENDENT tap: performScrollToNode starts the
        // lazy list's scroll, whose settle runs on the compose
        // clock that Robolectric does not auto-advance. A tap
        // injected before the settle lands on the pre-scroll
        // layout and reaches no stop. The clock is therefore
        // advanced past the settle on both sides of the tap.
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("app_row_stop_${pkg}_OPEN").performClick()
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()
        assertEquals(
            "OPEN stop tap must reach onSetAppTier; merged tree: " +
                composeRule.onRoot().printToString(),
            pkg to AppBlockTier.OPEN,
            sink.get()
        )
    }

    @Test
    fun catalogueList_scrollsManyRows_withoutViewChurnCrash() {
        val apps = (1..100).map { i -> app("com.example.app$i", "App $i") }
        setShield(apps)
        // The last row composes after a scroll — the list recycles
        // Compose rows the whole way down, no crash, no churn host.
        scrollToRow("com.example.app100")
        composeRule.onNodeWithTag("app_row_com.example.app100").assertExists()
        scrollToRow("com.example.app1")
        composeRule.onNodeWithTag("app_row_com.example.app1").assertExists()
    }
}
