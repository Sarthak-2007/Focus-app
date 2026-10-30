package com.example

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.service.TactileSoundEngine
import com.example.ui.components.ParkDrawer
import com.example.ui.theme.MyApplicationTheme
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Truth Car 3 — UNA-01, the rendered half (Pruefer's standard,
 * test 3: ShortsToggleRenderedAndTrue — the print half).
 *
 * PARK's Shorts + Reels sled must PRINT the stored fact. The
 * sheet is composed with a null Watch snapshot on purpose: the
 * board then prints its nine names and no words, the Sound sled
 * prints ON, and the plate carries no OFF — so the single "OFF"
 * text node on the sheet, when it exists, can only be the
 * sniper sled's margin word. Stored OFF → exactly one OFF on the
 * sheet and the sled present under its tag; stored ON → no OFF
 * anywhere and the sled still present.
 *
 * The social-sites row, which joined the sheet after this header
 * was written, contributes no "OFF" text node either: with a null
 * snapshot its board word is null, and its sled is a canvas view
 * in chrome mode that draws no text — the count still isolates
 * the sniper sled's margin word alone.
 *
 * The drive half of the standard (throwing the sled changes the
 * row, not a flow) is covered where the wiring lives:
 * ShortsSniperTruthTest.viewModel_printIsTheStore_toggleWritesTheStore
 * — MainActivity binds this sled to that same toggle, and
 * SledSwitchView fires onToggleRequested on any ACTION_UP.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ShortsToggleRenderTest {

    // ===== D9-REVEAL-BEGIN (instrument block — print-only, remove in one cut) =====
    // W2 FLOOR reveal step (D9 design, #166): a chained, print-only
    // uncaught-exception recorder. It prints the full throwable chain
    // (Throwable.printStackTrace semantics, suppressed included) to
    // System.err between D9-REVEAL-BEGIN / D9-REVEAL-END markers so the
    // CI job log carries the cause that UncaughtExceptionsBeforeTest
    // suppresses, then DELEGATES to the previously installed handler —
    // it never swallows, alters, or re-orders anything.
    companion object {
        @JvmStatic
        @BeforeClass
        fun installD9RevealUncaughtHandler() {
            val previous = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                try {
                    System.err.println("D9-REVEAL-BEGIN class=ShortsToggleRenderTest")
                    System.err.println(
                        "D9-REVEAL thread=${thread.name} type=${throwable.javaClass.name} msg=${throwable.message}"
                    )
                    throwable.printStackTrace(System.err)
                    System.err.println("D9-REVEAL-END")
                    System.err.flush()
                } finally {
                    if (previous != null) {
                        previous.uncaughtException(thread, throwable)
                    }
                }
            }
        }
    }
    // ===== D9-REVEAL-END (instrument block) =====

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun setPark(storedShorts: Boolean) {
        // D9-REVEAL-BEGIN setPark guard (print-only, rethrows unchanged)
        try {
        val soundEngine = TactileSoundEngine(context)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ParkDrawer(
                    visible = true,
                    snapshot = null,
                    isSealed = false,
                    isNight = false,
                    soundEnabled = true,
                    shortsReelsSniperEnabled = storedShorts,
                    soundEngine = soundEngine,
                    themeReadout = "AUTO · DAY — NO ROUTINE OWNS IT",
                    parkReturnTick = 0,
                    onDismiss = {},
                    onFixClick = {},
                    onToggleSound = {},
                    onToggleShortsReelsSniper = {},
                    // Wave-2 premise: social pair is scaffolding here —
                    // `true` is the persisted default; the toggle is not under test in this file.
                    socialSitesEnabled = true,
                    onToggleSocialSites = {}
                )
            }
        }
        // Let the drawer rise and the margin words settle (the
        // harness's discipline: idle the looper, give it room).
        repeat(12) {
            ShadowLooper.idleMainLooper()
            Thread.sleep(50)
        }
        composeRule.waitForIdle()
        } catch (t: Throwable) {
            System.err.println("D9-REVEAL-BEGIN class=ShortsToggleRenderTest where=setPark")
            System.err.println(
                "D9-REVEAL thread=${Thread.currentThread().name} type=${t.javaClass.name} msg=${t.message}"
            )
            t.printStackTrace(System.err)
            System.err.println("D9-REVEAL-END")
            System.err.flush()
            throw t
        }
        // D9-REVEAL-END setPark guard
    }

    @Test
    fun ShortsToggleRenderedAndTrue_storedOff_printsOff() {
        setPark(storedShorts = false)
        composeRule.onNodeWithTag("park_shorts_sled").assertExists()
        composeRule.onAllNodesWithText("OFF").assertCountEquals(1)
    }

    @Test
    fun ShortsToggleRenderedAndTrue_storedOn_printsNoOff() {
        setPark(storedShorts = true)
        composeRule.onNodeWithTag("park_shorts_sled").assertExists()
        composeRule.onAllNodesWithText("OFF").assertCountEquals(0)
    }
}
