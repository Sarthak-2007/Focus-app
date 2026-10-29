package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.data.DrawerDay
import com.example.data.FocusTaskEntity
import com.example.data.StoredMonth
import com.example.ui.screens.DrawerScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

/**
 * Issue #8 WAVE-2 (AUDIT-drawer §2/§4) — declared, uncompiled.
 *
 * Pins the wave-2 Drawer cures at the composition surface:
 *  1. The filing zone files TOMORROW (the audit's worst finding):
 *     tapping ILL on tomorrow's line calls back with tomorrow's
 *     filing date — and a declared line retires to pure print.
 *  2. Search is LIVE under seal (a query mutates nothing stored)
 *     and its empty line is re-scoped: NO STORED TASK UNDER X.
 *  3. A substep-only search hit prints the matched step as its
 *     reason line.
 *  4. Search BACK clears the query instead of trapping the man
 *     in a mode he leaves only by erasing his typing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DrawerWave2Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val today: LocalDate = DrawerDay.filingDate(System.currentTimeMillis())
    private val tomorrow: LocalDate = today.plusDays(1)

    private fun task(
        id: Int,
        title: String,
        steps: String = ""
    ) = FocusTaskEntity(
        id = id,
        title = title,
        notesOrSubsteps = steps,
        estimatedMinutes = 0,
        actualSecondsSpent = 1500,
        categoryTag = "Physics",
        status = "COMPLETED",
        completedAtMillis = System.currentTimeMillis() - 3_600_000L
    )

    @Test
    fun filingZone_filesTomorrow_andDeclaredLineRetiresToPrint() {
        val declared = mutableListOf<Pair<LocalDate, String?>>()
        composeRule.setContent {
            MyApplicationTheme {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(today.year, today.monthValue)),
                    drawerMonth = today.year to today.monthValue,
                    drawerRows = listOf(task(1, "Rotational mechanics")),
                    drawerHasEarlier = false,
                    drawerQuery = "",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = mapOf(DrawerDay.dateIso(today) to "ILL"),
                    isSealed = false,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareDayWord = { date, word -> declared += date to word }
                )
            }
        }
        // Today is already declared: its line is print, no tappable word.
        composeRule.onNodeWithTag("filing_line_${DrawerDay.dateIso(today)}")
            .assertIsDisplayed()
        composeRule.onNodeWithTag("filing_word_${DrawerDay.dateIso(today)}_ill")
            .assertDoesNotExist()
        // Tomorrow is fileable: tap ILL, the callback carries tomorrow.
        composeRule.onNodeWithTag("filing_word_${DrawerDay.dateIso(tomorrow)}_ill")
            .performClick()
        assertEquals(listOf(tomorrow to "ILL"), declared)
    }

    @Test
    fun search_liveUnderSeal_rescopedEmptyLine_andBackClears() {
        var query = ""
        composeRule.setContent {
            MyApplicationTheme {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(today.year, today.monthValue)),
                    drawerMonth = today.year to today.monthValue,
                    drawerRows = emptyList(),
                    drawerHasEarlier = false,
                    drawerQuery = query,
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = emptyMap(),
                    isSealed = true,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = { query = it },
                    onLoadEarlierSearch = {},
                    onDeclareDayWord = { _, _ -> }
                )
            }
        }
        // Sealed, yet the field accepts a query (it mutates nothing stored).
        composeRule.onNodeWithTag("drawer_search_input")
            .performTextInput("optics")
        assertEquals("optics", query)
        // Recompose with the query standing: the empty line is re-scoped.
        composeRule.setContent {
            MyApplicationTheme {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(today.year, today.monthValue)),
                    drawerMonth = today.year to today.monthValue,
                    drawerRows = emptyList(),
                    drawerHasEarlier = false,
                    drawerQuery = "optics",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = emptyMap(),
                    isSealed = true,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = { query = it },
                    onLoadEarlierSearch = {},
                    onDeclareDayWord = { _, _ -> }
                )
            }
        }
        composeRule.onNodeWithText("NO STORED TASK UNDER “optics”").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_search_back").performClick()
        assertTrue(query.isEmpty())
    }

    @Test
    fun search_substepHit_printsTheMatchedStepAsItsReason() {
        val steps = FocusTaskEntity.serializeSubSteps(
            listOf(
                com.example.data.TaskSubStep(0, "Revise ray diagrams", false),
                com.example.data.TaskSubStep(1, "Solve lens maker sums", true)
            )
        )
        composeRule.setContent {
            MyApplicationTheme {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(today.year, today.monthValue)),
                    drawerMonth = today.year to today.monthValue,
                    drawerRows = emptyList(),
                    drawerHasEarlier = false,
                    drawerQuery = "lens maker",
                    drawerResults = listOf(task(7, "Optics practice", steps)),
                    drawerSearchHasEarlier = false,
                    dayWords = emptyMap(),
                    isSealed = false,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareDayWord = { _, _ -> }
                )
            }
        }
        composeRule.onNodeWithText("Solve lens maker sums").assertIsDisplayed()
    }
}
