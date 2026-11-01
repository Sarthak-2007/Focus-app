package com.example

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.DrawerDay
import com.example.data.FocusTaskEntity
import com.example.data.StoredMonth
import com.example.ui.screens.DrawerScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import java.time.LocalDate
import java.time.ZoneId

/**
 * Issue #158, F1 (Option A, ratified): a past day's word was
 * fossilised — after the 03:30 rollover a missing or wrong
 * REST/ILL/FAMILY could never be declared, corrected or cleared,
 * because the Drawer offered the declaration only for today (and,
 * in the filing zone, tomorrow) while it carried no word. The
 * write path was always date-generic (`declareDayWordFor`); the
 * missing piece was a surface on the past day itself.
 *
 * These tests pin that surface at the composition level:
 *  1. A wordless past day: open its heading's DECLARE DAY WORD
 *     toggle, tap ILL — the declaration fires for THAT past date.
 *  2. A past day already carrying REST: tapping the standing word
 *     clears it; tapping FAMILY corrects it — both for that date.
 *  3. Under seal the toggle is not composed at all: filing is an
 *     edit, and a sealed heading is print only.
 * Today never gains the past-day toggle (the filing zone owns
 * today and tomorrow).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PastDayWordDeclareTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val today: LocalDate = DrawerDay.filingDate(System.currentTimeMillis())
    private val past: LocalDate = today.minusDays(2)
    private val pastIso: String = DrawerDay.dateIso(past)

    private fun pastTask() = FocusTaskEntity(
        id = 41,
        title = "Physics Mock — Full Syllabus",
        notesOrSubsteps = "",
        estimatedMinutes = 45,
        actualSecondsSpent = 10_800,
        categoryTag = "PHYSICS",
        status = "COMPLETED",
        completedAtMillis = past.atTime(21, 14)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    private fun toggleTag(date: LocalDate) =
        "declare_past_word_toggle_${DrawerDay.dateIso(date)}"

    private fun headingTag(date: LocalDate) =
        "drawer_day_heading_${DrawerDay.dateIso(date)}"

    /**
     * Poll-with-looper-idle until the node with [tag] exists (the
     * suite's own awaitTrue idiom, SoundAndSocialTruthTest: looper
     * idle + waitForIdle + 25 ms, bounded at the class-standard
     * 30 s). Single-shot idles are not evidence a LazyColumn item
     * has composed — DIAG-S12-NEWFLAKE §1.3, Defect 2.
     *
     * The poll reads the COLLECTION form on purpose
     * (READER-S12-N01 §1.2, on the stamped artifact's stack): in
     * this suite's Compose version a single-node
     * fetchSemanticsNode() routes through fetchOneOrThrow and
     * THROWS assertExists on absence — that throw at the old
     * toggle probe (renderDrawer:116) is the stamped N-01 failure
     * itself. A poll built on it would die at its first fetch
     * instead of polling. onAllNodesWithTag(...).
     * fetchSemanticsNodes() returns an empty list on absence —
     * the same form SoundAndSocialTruthTest's awaits use.
     */
    private fun awaitNodeExists(tag: String, what: String) {
        val deadline = System.currentTimeMillis() + 30_000L
        while (System.currentTimeMillis() < deadline) {
            if (composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()) return
            ShadowLooper.idleMainLooper()
            try {
                composeRule.waitForIdle()
            } catch (t: Throwable) {
                // A composition that never idles must not stall
                // the poll; the looper idle above still drove it.
            }
            Thread.sleep(25L)
        }
        throw AssertionError("Timed out waiting for: $what")
    }

    private fun chipTag(date: LocalDate, word: String) =
        "declare_past_word_${DrawerDay.dateIso(date)}_${word.lowercase()}"

    /**
     * Renders the Drawer as production drives it: the viewed month
     * is state, and selecting a month on the shelf moves it. When
     * the past day sits in an earlier month than today, walks the
     * shelf into that month first — the same taps the man makes.
     *
     * DIAG-S12-NEWFLAKE §1.6 cure (queue place 11, N-01): the walk
     * decision is month arithmetic ALONE — never the old toggle
     * probe, which is definitionally absent under seal (DayHeading
     * composes the toggle only when !isSealed), so its null result
     * carried no information about the heading and spuriously
     * forced the walk on every month-boundary filing date. The
     * walk is awaited in both directions with the poll idiom, and
     * what is probed is the past day's HEADING tag.
     */
    private fun renderDrawer(
        dayWords: Map<String, String>,
        isSealed: Boolean,
        declared: MutableList<Pair<LocalDate, String?>>
    ) {
        var drawerMonth by mutableStateOf(today.year to today.monthValue)
        val months = listOf(
            StoredMonth(today.year, today.monthValue),
            StoredMonth(past.year, past.monthValue)
        ).distinct()
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                DrawerScreen(
                    storedMonths = months,
                    drawerMonth = drawerMonth,
                    drawerRows = listOf(pastTask()),
                    drawerHasEarlier = false,
                    drawerQuery = "",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = dayWords,
                    isSealed = isSealed,
                    onSelectMonth = { y, m -> drawerMonth = y to m },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareTodayWord = {},
                    onDeclareDayWord = { date, word -> declared += date to word }
                )
            }
        }
        val pastIsEarlierMonth = past.year < today.year ||
            (past.year == today.year && past.monthValue < today.monthValue)
        if (pastIsEarlierMonth) {
            awaitNodeExists(
                "drawer_month_${past.year}_${past.monthValue}",
                "the past month's shelf row"
            )
            composeRule.onNodeWithTag("drawer_month_${past.year}_${past.monthValue}")
                .performClick()
            composeRule.waitForIdle()
            awaitNodeExists(
                headingTag(past),
                "the past day heading on the MONTH view"
            )
        } else {
            awaitNodeExists(
                headingTag(past),
                "the past day heading on the landing"
            )
        }
    }

    @Test
    fun pastDayWithoutWord_declareFilesForThatPastDate() {
        val declared = mutableListOf<Pair<LocalDate, String?>>()
        renderDrawer(dayWords = emptyMap(), isSealed = false, declared = declared)

        // Today is never offered the past-day toggle.
        composeRule.onNodeWithTag(toggleTag(today)).assertDoesNotExist()

        composeRule.onNodeWithTag(toggleTag(past)).assertIsDisplayed()
        composeRule.onNodeWithTag(toggleTag(past)).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(chipTag(past, "ILL")).performClick()
        composeRule.waitForIdle()

        assertEquals(listOf(past to "ILL"), declared)
    }

    @Test
    fun pastDayWithWord_tappingStandingWordClears_tappingAnotherCorrects() {
        val declared = mutableListOf<Pair<LocalDate, String?>>()
        renderDrawer(
            dayWords = mapOf(pastIso to "REST"),
            isSealed = false,
            declared = declared
        )

        composeRule.onNodeWithTag(toggleTag(past)).performClick()
        composeRule.waitForIdle()
        // The standing word, tapped, clears the day.
        composeRule.onNodeWithTag(chipTag(past, "REST")).performClick()
        composeRule.waitForIdle()
        // Another word corrects it (the picker stays open; the
        // stored word in this harness is static, as the VM's
        // refreshed map is what closes the loop in production).
        composeRule.onNodeWithTag(chipTag(past, "FAMILY")).performClick()
        composeRule.waitForIdle()

        assertEquals(listOf(past to null, past to "FAMILY"), declared)
    }

    @Test
    fun sealedDrawer_pastDayHeadingIsPrintOnly() {
        val declared = mutableListOf<Pair<LocalDate, String?>>()
        renderDrawer(
            dayWords = mapOf(pastIso to "REST"),
            isSealed = true,
            declared = declared
        )

        // The heading IS composed (print) — asserted, not assumed:
        // as written before the §1.6 cure this case never checked
        // the heading exists at all, so a sealed drawer that
        // printed nothing would have passed it.
        composeRule.onNodeWithTag(headingTag(past)).assertExists()
        composeRule.onNodeWithTag(toggleTag(past)).assertDoesNotExist()
        assertEquals(emptyList<Pair<LocalDate, String?>>(), declared)
    }
}
