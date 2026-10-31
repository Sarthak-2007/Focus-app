package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.components.GuardLinePrint
import com.example.ui.components.MonolithPullDownHeader
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Issue #97 (recomposed, landed via PR #224) — the covering test for
 * MonolithCustomComponents.kt's one behavioural change in that
 * recomposition: the Guard line's tap target in
 * [MonolithPullDownHeader] carries the spoken label
 * "Park — setup, permissions and settings", so the way back into
 * setup is named for accessibility, not just printed for sight.
 *
 * This file exists because the targeted-tests gate (issue #166)
 * found MonolithCustomComponents.kt unmapped on PR #224's first
 * rebased head (run 41201589459): no test file carried the stem and
 * none referenced it. The file name carries the stem by design.
 *
 * PIN scope: RENDER — both tests render the real composable and
 * assert on its semantics tree / tap delivery.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MonolithCustomComponentsHeaderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun guardLineNode_carriesParkSpokenLabel() {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                MonolithPullDownHeader(
                    guardLine = GuardLinePrint("GUARD 6/6"),
                    isSealed = false,
                    onGuardLineTap = {}
                )
            }
        }
        val node = composeRule.onNodeWithTag("header_guard_line").fetchSemanticsNode()
        assertEquals(
            listOf("Park — setup, permissions and settings"),
            node.config[SemanticsProperties.ContentDescription]
        )
    }

    @Test
    fun guardLineTap_stillFiresHostCallback() {
        val tapped = AtomicBoolean(false)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                MonolithPullDownHeader(
                    guardLine = GuardLinePrint("GUARD 6/6"),
                    isSealed = false,
                    onGuardLineTap = { tapped.set(true) }
                )
            }
        }
        composeRule.onNodeWithTag("header_guard_line").performClick()
        assertTrue(tapped.get())
    }
}
