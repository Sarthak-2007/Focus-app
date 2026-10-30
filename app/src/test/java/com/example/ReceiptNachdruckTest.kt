package com.example

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.data.FinishKind
import com.example.data.ReceiptDeflection
import com.example.data.ReceiptState
import com.example.ui.components.ReceiptSheet
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Calendar

/**
 * Issue #10 NACHDRUCK (improver) — declared, uncompiled.
 *
 * Pins the improver rounds at the sheet surface:
 *  1. Round 3 (Finisher 3 §3) — a ≥20-minute silence between two
 *     printed deflections earns a `{longSpan} QUIET` span rule;
 *     a 19-minute gap earns none; no rule opens the list.
 *  2. Round 4 (Finisher 3 §4) — a site deflection prints its
 *     domain plus `· SITE`; an app deflection prints no tag.
 *  3. Round 2 (Finisher 3 §2.2) — entries print in the acquitted
 *     bare form (`· <name>`) under their own time column.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceiptNachdruckTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun receipt(deflections: List<ReceiptDeflection>) = ReceiptState(
        sessionId = 42,
        number = 48,
        kind = FinishKind.MANUAL_FINISH,
        outcomeWord = "",
        title = "Morning revision",
        armWallMs = wall(9, 0),
        endedAtWallMs = wall(10, 30),
        plannedSeconds = 5400,
        sealedElapsedSeconds = 5400,
        // WELLE-6 landing adaptation: on current main the HELD
        // line prints whenever both figures are captured (>= 0)
        // and its "SITES" would false-positive the no-SITE-tag
        // assertion below. -1 is main's uncaptured stamp — the
        // sheet omits the HELD line whole — so the fixture keeps
        // every assertion about deflection tags, as written.
        heldAppsAtSeal = -1,
        heldSitesAtSeal = -1,
        routineLabelWithin = null,
        bankedBreakSeconds = 0,
        shieldWasOff = false,
        faulted = false,
        deflections = deflections,
        restored = true,
        receiptPublishedMonoMs = 0L
    )

    private fun show(state: ReceiptState) {
        composeRule.setContent {
            // WELLE-6 landing fix: main's theme (issue #63) has
            // deliberately NO dynamicColor parameter — the
            // recovered file's call predates that removal.
            MyApplicationTheme(darkTheme = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ReceiptSheet(receipt = state, onDismiss = {})
                }
            }
        }
    }

    private fun wall(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun deflection(name: String, pkg: String, h: Int, m: Int, isSite: Boolean = false) =
        ReceiptDeflection(name, pkg, wall(h, m), isSite = isSite)

    @Test
    fun silenceOf28Minutes_printsQuietRule() {
        show(
            receipt(
                listOf(
                    deflection("YouTube", "com.google.android.youtube", 9, 5),
                    deflection("Instagram", "com.instagram.android", 9, 33)
                )
            )
        )
        composeRule.onNodeWithText("28 MIN QUIET", substring = true).assertIsDisplayed()
    }

    @Test
    fun silenceOfExactly20Minutes_printsQuietRule() {
        show(
            receipt(
                listOf(
                    deflection("YouTube", "com.google.android.youtube", 9, 5),
                    deflection("Instagram", "com.instagram.android", 9, 25)
                )
            )
        )
        composeRule.onNodeWithText("20 MIN QUIET", substring = true).assertIsDisplayed()
    }

    @Test
    fun silenceOf19Minutes_printsNoQuietRule() {
        show(
            receipt(
                listOf(
                    deflection("YouTube", "com.google.android.youtube", 9, 5),
                    deflection("Instagram", "com.instagram.android", 9, 24)
                )
            )
        )
        composeRule.onNodeWithText("QUIET", substring = true).assertDoesNotExist()
    }

    @Test
    fun siteDeflection_printsDomainPlusSiteTag() {
        show(
            receipt(
                listOf(
                    deflection("youtube.com", "com.android.chrome", 9, 2, isSite = true),
                    deflection("YouTube", "com.google.android.youtube", 9, 5)
                )
            )
        )
        composeRule.onNodeWithText("youtube.com · SITE", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("· YouTube", substring = true).assertIsDisplayed()
    }

    @Test
    fun appOnlyDeflections_printNoSiteTag() {
        show(
            receipt(
                listOf(
                    deflection("YouTube", "com.google.android.youtube", 9, 5)
                )
            )
        )
        composeRule.onNodeWithText("SITE", substring = true).assertDoesNotExist()
    }
}
