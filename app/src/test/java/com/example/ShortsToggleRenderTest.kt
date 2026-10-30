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

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    companion object {
        // D9-REVEAL (#166, W2 FLOOR): chained, print-only
        // uncaught-exception recorder. Installed once per class;
        // prints the FULL stack of any background death to
        // System.err -- every Suppressed: and Caused by: line via
        // printStackTrace -- then DELEGATES to the previously
        // installed handler. It never swallows and never replaces
        // the verdict. The captured chain is the discriminator:
        // frames naming MonolithViewModel / repository flows point
        // at D7's closed-DB cross-class leak; frames confined to
        // this class's own composition point intra-class.
        @BeforeClass
        @JvmStatic
        fun installD9RevealHandler() {
            val previous = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                System.err.println("D9-REVEAL-BEGIN class=ShortsToggleRenderTest")
                System.err.println(
                    "D9-REVEAL thread=" + thread.name +
                        " type=" + throwable.javaClass.name +
                        " msg=" + throwable.message
                )
                throwable.printStackTrace()
                System.err.println("D9-REVEAL-END")
                previous?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun setPark(storedShorts: Boolean) {
        // D9-REVEAL (#166): print-only wrap -- a synchronous death
        // inside the harness setup names itself in the job log,
        // then the SAME throwable continues (rethrow). No
        // assertion, timing, or behaviour change.
        try {
            setParkBody(storedShorts)
        } catch (t: Throwable) {
            System.err.println("D9-REVEAL-BEGIN class=ShortsToggleRenderTest where=setPark")
            System.err.println(
                "D9-REVEAL thread=" + Thread.currentThread().name +
                    " type=" + t.javaClass.name +
                    " msg=" + t.message
            )
            t.printStackTrace()
            System.err.println("D9-REVEAL-END")
            throw t
        }
    }

    private fun setParkBody(storedShorts: Boolean) {
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
