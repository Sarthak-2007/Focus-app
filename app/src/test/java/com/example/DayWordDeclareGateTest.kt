package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import com.example.data.DrawerDay
import com.example.data.FocusTaskEntity
import com.example.data.StoredMonth
import com.example.data.TaskQueueStatus
import com.example.ui.screens.DrawerScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicReference

/**
 * Issue #137, F2: the day-word declaration must not be locked out
 * precisely when he is sick. Before this cure, every DayHeading
 * call site computed canDeclare = !isSealed && (today, or
 * yesterday while it still carried no word) — so an active
 * (routine) session sealed the declaration away, and a day older
 * than yesterday, or a yesterday that already carried a word,
 * could never be declared or corrected after the fact.
 *
 * This test renders the Drawer SEALED, with yesterday already
 * carrying REST, and proves the picker still answers: tapping
 * FAMILY on yesterday's heading fires the declaration for
 * yesterday's date.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DayWordDeclareGateTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sealedDrawer_pastDayWithWord_pickerAnswers_andDeclaresForThatDate() {
        val filingToday = DrawerDay.filingDate(System.currentTimeMillis())
        val yesterday = filingToday.minusDays(1)
        val declared = AtomicReference<Pair<LocalDate, String?>?>(null)
        val declaredToday = AtomicReference<String?>(null)

        val completedYesterday = FocusTaskEntity(
            title = "Physics Mock — Full Syllabus",
            notesOrSubsteps = "",
            estimatedMinutes = 45,
            actualSecondsSpent = 10_800,
            categoryTag = "PHYSICS",
            queueOrder = 0,
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = yesterday.atTime(21, 14)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                DrawerScreen(
                    storedMonths = listOf(
                        StoredMonth(filingToday.year, filingToday.monthValue)
                    ),
                    drawerMonth = filingToday.year to filingToday.monthValue,
                    drawerRows = listOf(completedYesterday),
                    drawerHasEarlier = false,
                    drawerQuery = "",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = mapOf(DrawerDay.dateIso(yesterday) to "REST"),
                    isSealed = true,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareTodayWord = { word -> declaredToday.set(word) },
                    onDeclareDayWord = { date, word -> declared.set(date to word) }
                )
            }
        }
        composeRule.waitForIdle()

        // The landing lists the current month newest-first: today's
        // heading, then yesterday's. Yesterday already carries
        // REST — under the old gate its heading printed the word
        // and answered nothing, and the seal removed the picker
        // from every heading (today's group was not even built).
        // Now both pickers are composed, sealed or not.
        val familyChips = composeRule.onAllNodesWithTag("declare_word_family")
        familyChips.assertCountEquals(2)

        // Yesterday's chip declares for yesterday's date — a past
        // day corrected after the fact, while sealed.
        familyChips[1].performClick()
        composeRule.waitForIdle()
        assertEquals(yesterday to "FAMILY", declared.get())

        // Today's chip declares through the today door, also while
        // sealed.
        familyChips[0].performClick()
        composeRule.waitForIdle()
        assertEquals("FAMILY", declaredToday.get())
    }
}
