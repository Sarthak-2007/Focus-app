package com.example

import android.content.Context
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CurioCatalog
import com.example.data.DrawerDay
import com.example.data.FocusSessionEntity
import com.example.data.FocusTaskEntity
import com.example.data.ForgeStat
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.PARTIAL_STAMP_ARTIFACT
import com.example.data.PARTIAL_STAMP_ID
import com.example.data.StoredMonth
import com.example.data.TaskQueueStatus
import com.example.ui.screens.DrawerScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Issue #110 (Audit R3, journey vault-history-lived) — the truth
 * fixes and their evidence:
 *
 *  - Finding 3: the receipt entry for a completed task prints its
 *    REAL accrued time. Zero accrued seconds (ticked done before
 *    any session time reached it) prints "no session time",
 *    never the old coerceAtLeast(1) invention of "1m actual"; a
 *    real sub-minute accrual prints "<1m actual". The storing
 *    itself stands (BY-DESIGN: he finished the task; the Drawer
 *    is the finished-task drawer) — proven by the no-session
 *    completion test below.
 *  - Finding 2 (interim relabel): the Drawer's SPENT prints say
 *    TOTAL / SPENT IN TOTAL — the lifetime accrual filed under
 *    the finish day, never that day's own time. (The per-day
 *    split itself is STANDS-BLOCKED on segment storage the
 *    schema train owns; see the DrawerScreen header.)
 *  - Finding 5: forgeStats() derives count + most-recent forge
 *    from stored session rows, and 'partial_stamp' resolves to a
 *    real, honestly-labelled catalog entry the cabinet carries.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w450dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class VaultHistoryTruthTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repo: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(hour, minute).atZone(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli()

    private suspend fun activeSession(): Int =
        db.monolithDao().insertSession(
            FocusSessionEntity(
                intention = "Vault test session",
                profileName = "Manual Focus",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        ).toInt()

    private suspend fun queuedTask(title: String, estimatedMinutes: Int, spentSeconds: Int): Int =
        db.monolithDao().insertTask(
            FocusTaskEntity(
                title = title,
                estimatedMinutes = estimatedMinutes,
                actualSecondsSpent = spentSeconds,
                categoryTag = "Study",
                queueOrder = 0,
                status = TaskQueueStatus.QUEUED.name
            )
        ).toInt()

    // ---- Finding 3: the receipt entry tells the truth ----

    @Test
    fun receiptEntry_zeroAccruedTask_printsNoSessionTime_neverOneMinute() = runBlocking {
        val sessionId = activeSession()
        val taskId = queuedTask("Desk Tidy", estimatedMinutes = 25, spentSeconds = 0)

        repo.completeTaskAndAdvance(taskId)

        val session = db.monolithDao().getActiveSessionSnapshot()!!
        assertEquals(sessionId, session.id)
        assertEquals("Desk Tidy (25m est / no session time)", session.completedTasksSummary)
        assertFalse(session.completedTasksSummary.contains("1m actual"))
        // The stored row keeps the truth too: completed in this
        // session, zero seconds accrued — not a rounded-up minute.
        val stored = db.monolithDao().getTaskById(taskId)!!
        assertEquals(TaskQueueStatus.COMPLETED, stored.resolvedStatus)
        assertEquals(sessionId, stored.completedInSessionId)
        assertEquals(0, stored.actualSecondsSpent)
    }

    @Test
    fun receiptEntry_subMinuteAccrual_printsLessThanOneMinute() = runBlocking {
        activeSession()
        val taskId = queuedTask("Quick Check", estimatedMinutes = 0, spentSeconds = 30)

        repo.completeTaskAndAdvance(taskId)

        val session = db.monolithDao().getActiveSessionSnapshot()!!
        assertEquals("Quick Check (<1m actual)", session.completedTasksSummary)
    }

    @Test
    fun receiptEntry_fullMinutes_printsAsBefore() = runBlocking {
        activeSession()
        val taskId = queuedTask("Deep Work", estimatedMinutes = 25, spentSeconds = 1440)

        repo.completeTaskAndAdvance(taskId)

        val session = db.monolithDao().getActiveSessionSnapshot()!!
        assertEquals("Deep Work (25m est / 24m actual)", session.completedTasksSummary)
    }

    // Finding 3, BY-DESIGN half: a task ticked done with no session
    // ever run is still STORED — he did finish the task, and the
    // Drawer is the finished-task drawer. What changed is only
    // that nothing inflates its zero seconds into a fake minute.
    @Test
    fun completionWithoutSession_isStillStored_withZeroSeconds() = runBlocking {
        val taskId = queuedTask("Offline Finish", estimatedMinutes = 25, spentSeconds = 0)

        repo.completeTaskAndAdvance(taskId)

        val stored = db.monolithDao().getTaskById(taskId)!!
        assertEquals(TaskQueueStatus.COMPLETED, stored.resolvedStatus)
        assertEquals(0, stored.completedInSessionId)
        assertEquals(0, stored.actualSecondsSpent)
        val filing = DrawerDay.filingDate(stored.completedAtMillis)
        val page = repo.storedPage(filing.year, filing.monthValue, 0)
        assertTrue(page.rows.any { it.id == taskId })
    }

    // ---- Finding 5: the cabinet remembers ----

    private suspend fun endedSession(forgedArtifactId: String?, start: Long, end: Long): Int =
        db.monolithDao().insertSession(
            FocusSessionEntity(
                intention = "Forge test",
                profileName = "Manual Focus",
                targetDurationSeconds = 25 * 60,
                elapsedSeconds = 1500,
                startWallTimeMillis = start,
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = false,
                completedSuccessfully = true,
                forgedArtifactId = forgedArtifactId,
                endWallMillis = end
            )
        ).toInt()

    @Test
    fun forgeStats_countsForges_andKeepsMostRecentInstant() = runBlocking {
        val d1 = millis(LocalDate.of(2026, 10, 20), 10, 0)
        val d2 = millis(LocalDate.of(2026, 10, 27), 18, 30)
        endedSession("artifact_gyro", d1 - 3_600_000, d1)
        endedSession("artifact_gyro", d2 - 3_600_000, d2)
        // A pre-v11 row: no end stamp — the start is the fallback.
        endedSession(PARTIAL_STAMP_ID, d1, 0L)
        endedSession(null, d2 - 3_600_000, d2)

        val stats = repo.forgeStats()
        assertEquals(2, stats["artifact_gyro"]?.count)
        assertEquals(d2, stats["artifact_gyro"]?.lastForgedAtMillis)
        assertEquals(1, stats[PARTIAL_STAMP_ID]?.count)
        assertEquals(d1, stats[PARTIAL_STAMP_ID]?.lastForgedAtMillis)
        assertFalse(stats.containsKey("artifact_loom"))
        // The DISTINCT forged set still resolves the stamp id —
        // and now a catalog entry answers for it.
        assertTrue(repo.forgedArtifactIds().contains(PARTIAL_STAMP_ID))
    }

    @Test
    fun partialStamp_hasAnHonestCatalogEntry_andIsNotACurioOnOffer() {
        assertEquals(PARTIAL_STAMP_ID, PARTIAL_STAMP_ARTIFACT.id)
        assertEquals("Partial Stamp", PARTIAL_STAMP_ARTIFACT.name)
        // The six curios on offer stay six — the stamp is a record
        // that joins the cabinet only when a real session earns it.
        assertEquals(6, CurioCatalog.artifacts.size)
        assertTrue(CurioCatalog.artifacts.none { it.id == PARTIAL_STAMP_ID })
    }

    @Test
    fun disarmEarlyEnd_stampsTheConstant_andForgeStatsCountIt() = runBlocking {
        val session = repo.startSession(
            intention = "Early end test",
            profileName = "Study Loadout",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        val fiveMinsAgoMono = (SystemClock.elapsedRealtime() - 300_000L).coerceAtLeast(1L)
        db.monolithDao().updateSession(
            session.copy(
                startWallTimeMillis = System.currentTimeMillis() - 300_000L,
                startElapsedRealtimeMillis = fiveMinsAgoMono,
                elapsedSeconds = 300
            )
        )

        repo.disarmActiveSession(completed = false, outcomeReason = "Unlatched Early")

        val row = db.monolithDao().getSessionById(session.id)!!
        assertEquals(PARTIAL_STAMP_ID, row.forgedArtifactId)
        assertEquals(1, repo.forgeStats()[PARTIAL_STAMP_ID]?.count)
    }

    // ---- Finding 2: the Drawer's SPENT prints say TOTAL ----

    private fun settle() {
        ShadowLooper.idleMainLooper()
        Thread.sleep(500)
        ShadowLooper.idleMainLooper()
    }

    private fun headingWithYear(date: LocalDate): String =
        DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.US)
            .format(date).uppercase(Locale.US)

    @Test
    fun drawerRowAndDetail_printSpentAsLifetimeTotal() {
        val now = System.currentTimeMillis()
        val task = FocusTaskEntity(
            id = 7,
            title = "Electrostatics Problem Set",
            estimatedMinutes = 45,
            actualSecondsSpent = 3600,
            categoryTag = "PHYSICS",
            queueOrder = 0,
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = now
        )
        val filing = DrawerDay.filingYearMonth(now)
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(filing.first, filing.second)),
                    drawerMonth = filing,
                    drawerRows = listOf(task),
                    drawerHasEarlier = false,
                    drawerQuery = "",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = emptyMap(),
                    isSealed = false,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareTodayWord = {}
                )
            }
        }
        settle()

        // The shelf row: the lifetime total, labelled TOTAL —
        // never a bare duration the finish day could be read
        // as having contained.
        composeRule.onNodeWithText("TOTAL 1H 00M").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_row_7").performClick()
        settle()
        composeRule.onNodeWithText("SPENT IN TOTAL 1H 00M").assertIsDisplayed()
    }

    // Floor lane (#166 row 29): this test stood red from census
    // #3 to #6 on the stamp assertions below, and the failure was
    // production's, not the rig's — DrawerScreen resolved the open
    // cabinet detail against the curios-only list while the
    // Partial Stamp joined only a second, list-local copy, so the
    // stamp's row rendered and took the tap but its detail could
    // never open. The cure (PR #705) computes the joined list once
    // and resolves against it; the assertions below stand exactly
    // as written.
    @Test
    fun cabinetDetail_printsForgeCountAndLastDate_stampIsHonest() {
        val gyroLast = millis(LocalDate.of(2026, 10, 27), 18, 30)
        val stampLast = millis(LocalDate.of(2026, 10, 20), 10, 0)
        val filing = DrawerDay.filingYearMonth(System.currentTimeMillis())
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                DrawerScreen(
                    storedMonths = listOf(StoredMonth(filing.first, filing.second)),
                    drawerMonth = filing,
                    drawerRows = emptyList(),
                    drawerHasEarlier = false,
                    drawerQuery = "",
                    drawerResults = emptyList(),
                    drawerSearchHasEarlier = false,
                    dayWords = emptyMap(),
                    isSealed = false,
                    onSelectMonth = { _, _ -> },
                    onLoadEarlier = {},
                    onQueryChange = {},
                    onLoadEarlierSearch = {},
                    onDeclareTodayWord = {},
                    forgedArtifactIds = setOf("artifact_gyro", PARTIAL_STAMP_ID),
                    forgeStats = mapOf(
                        "artifact_gyro" to ForgeStat(count = 3, lastForgedAtMillis = gyroLast),
                        PARTIAL_STAMP_ID to ForgeStat(count = 2, lastForgedAtMillis = stampLast)
                    )
                )
            }
        }
        settle()

        composeRule.onNodeWithTag("drawer_curios_row").performClick()
        settle()
        // Repeat forges no longer collapse to a binary stamp.
        composeRule.onNodeWithText("FORGED ×3").assertIsDisplayed()
        composeRule.onNodeWithTag("cabinet_row_artifact_gyro").performClick()
        settle()
        composeRule.onNodeWithText("FORGED — IT STANDS IN THE CABINET").assertIsDisplayed()
        composeRule.onNodeWithText(
            "FORGED ×3 · LAST ${headingWithYear(LocalDate.of(2026, 10, 27))}"
        ).assertIsDisplayed()

        composeRule.onNodeWithTag("cabinet_back").performClick()
        settle()
        // The Partial Stamp stands as its own honest row and its
        // detail never calls it forged.
        composeRule.onNodeWithText("STAMPED").assertIsDisplayed()
        composeRule.onNodeWithTag("cabinet_row_partial_stamp").performClick()
        settle()
        composeRule.onNodeWithText("Partial Stamp").assertIsDisplayed()
        composeRule.onNodeWithText("STAMPED — IT STANDS IN THE CABINET").assertIsDisplayed()
        composeRule.onNodeWithText(
            "STAMPED ×2 · LAST ${headingWithYear(LocalDate.of(2026, 10, 20))}"
        ).assertIsDisplayed()
    }
}
