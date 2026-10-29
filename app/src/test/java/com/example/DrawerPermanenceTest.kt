package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusTaskEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.TaskQueueStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * THE DRAWER's permanence guards + storage reads. The promise is
 * that a finished task is kept — forever, findable, unpruned. Each
 * test names the shipped hole it closes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DrawerPermanenceTest {

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

    private fun completedTask(title: String, completedAt: Long, notes: String = ""): FocusTaskEntity =
        FocusTaskEntity(
            title = title,
            notesOrSubsteps = notes,
            estimatedMinutes = 25,
            actualSecondsSpent = 600,
            categoryTag = "Study",
            queueOrder = 0,
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = completedAt
        )

    private fun millis(date: LocalDate, hour: Int, minute: Int): Long =
        date.atTime(hour, minute).atZone(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli()

    // The seed planted a finished task he never did; the Drawer's
    // first rendering broke on it and NOTHING STORED YET was
    // unreachable. Fresh installs must seed NO completed task.
    @Test
    fun freshSeed_containsNoCompletedTask() = runBlocking {
        repo.ensureSeededAndSyncPackages()
        val tasks = db.monolithDao().getAllTasksSnapshot()
        assertTrue(tasks.isNotEmpty())
        assertTrue(
            "seeded tasks must never include a completion",
            tasks.none { it.resolvedStatus == TaskQueueStatus.COMPLETED }
        )
    }

    // LR2-S1-2: the shipped seed also wrote ~150 minutes of
    // fabricated SESSION history (three demo sessions he never
    // ran). A fresh install's history must be empty — his record
    // holds only sessions he actually ran.
    @Test
    fun freshSeed_containsNoFabricatedSessions() = runBlocking {
        repo.ensureSeededAndSyncPackages()
        assertTrue(db.monolithDao().getSessionHistorySnapshot().isEmpty())
    }

    // Tief's hole: the launch dedup deleted later rows sharing a
    // title|status key — repeat-titled completions are the normal
    // shape of a year. Both must survive a relaunch (a second
    // ensureSeededAndSyncPackages call).
    @Test
    fun seedDedup_neverDeletesCompletedRows() = runBlocking {
        val dao = db.monolithDao()
        dao.insertTask(completedTask("Physics Mock", millis(LocalDate.of(2026, 10, 5), 10, 0)))
        dao.insertTask(completedTask("Physics Mock", millis(LocalDate.of(2026, 10, 12), 10, 0)))
        repo.ensureSeededAndSyncPackages()
        val stored = dao.getAllTasksSnapshot()
            .filter { it.resolvedStatus == TaskQueueStatus.COMPLETED }
        assertEquals(2, stored.size)
    }

    // The queue's delete-with-undo path must refuse a stored row.
    @Test
    fun deleteTaskById_refusesCompletedRow() = runBlocking {
        val dao = db.monolithDao()
        val id = dao.insertTask(
            completedTask("Kept Forever", System.currentTimeMillis())
        ).toInt()
        val (deleted, _) = repo.deleteTaskById(id)
        assertNull(deleted)
        assertNotNull(dao.getTaskById(id))
    }

    // 51 completions inside one filing month, several sharing the
    // exact same millisecond: EARLIER pages 50 + 1, no row skipped
    // or doubled at the seam (the id DESC tiebreak is load-bearing).
    @Test
    fun storedPage_pages51_withoutSkipOrDouble() = runBlocking {
        val dao = db.monolithDao()
        val base = millis(LocalDate.of(2026, 10, 15), 12, 0)
        repeat(51) { i ->
            // Every third row shares the previous row's timestamp.
            val ts = if (i % 3 == 0 && i > 0) base - (i - 1) * 60_000L else base - i * 60_000L
            dao.insertTask(completedTask("Task $i", ts))
        }
        val page0 = repo.storedPage(2026, 10, 0)
        val page1 = repo.storedPage(2026, 10, 50)
        assertEquals(50, page0.rows.size)
        assertEquals(1, page1.rows.size)
        // DRW-04: hasEarlier is the one-extra-row signal, never a
        // page-size inference — 51 stored means page 0 HAS an
        // earlier page and the 1-row page 1 does not.
        assertTrue(page0.hasEarlier)
        assertFalse(page1.hasEarlier)
        val ids = (page0.rows + page1.rows).map { it.id }
        assertEquals(51, ids.distinct().size)
        // Newest first: the first row carries the largest stored
        // timestamp (the base instant — insertion order is NOT
        // completion order, so this compares timestamps, not ids).
        assertEquals(base, page0.rows.first().completedAtMillis)
    }

    // DRW-04's edge: exactly 50 stored rows is a COMPLETE page —
    // hasEarlier must be false (the old size == 50 inference
    // printed an EARLIER row that led nowhere).
    @Test
    fun storedPage_exactly50_hasNoEarlier() = runBlocking {
        val dao = db.monolithDao()
        val base = millis(LocalDate.of(2026, 10, 15), 12, 0)
        repeat(50) { i ->
            dao.insertTask(completedTask("Exact $i", base - i * 60_000L))
        }
        val page = repo.storedPage(2026, 10, 0)
        assertEquals(50, page.rows.size)
        assertFalse(page.hasEarlier)
    }

    // Kaeufer: a completion at 00:02 on Nov 1 files under OCTOBER —
    // in the months index and in the paged reads alike.
    @Test
    fun completionAt0002_filesUnderPreviousMonth() = runBlocking {
        val dao = db.monolithDao()
        dao.insertTask(
            completedTask("Test-Eve Finish", millis(LocalDate.of(2026, 11, 1), 0, 2))
        )
        val months = repo.storedMonths()
        assertEquals(listOf(2026 to 10), months.map { it.year to it.month })
        assertEquals(1, repo.storedPage(2026, 10, 0).rows.size)
        assertEquals(0, repo.storedPage(2026, 11, 0).rows.size)
    }

    // Search reads the stored notes text across all years, and
    // pages past 50 matches — the oldest match is the point.
    @Test
    fun search_findsNotesWordAcrossYears_andPagesPast50() = runBlocking {
        val dao = db.monolithDao()
        dao.insertTask(
            completedTask(
                "Old Chapter",
                millis(LocalDate.of(2024, 3, 3), 9, 0),
                notes = "quixotic vector drill"
            )
        )
        repeat(55) { i ->
            dao.insertTask(
                completedTask(
                    "Filler $i",
                    millis(LocalDate.of(2026, 10, 10), 10, 0) - i * 60_000L,
                    notes = "commonword entry"
                )
            )
        }
        val found = repo.searchStoredTasks("quixotic", 0)
        assertEquals(1, found.rows.size)
        assertEquals("Old Chapter", found.rows.first().title)
        assertFalse(found.hasEarlier)

        val firstPage = repo.searchStoredTasks("commonword", 0)
        val secondPage = repo.searchStoredTasks("commonword", 50)
        assertEquals(50, firstPage.rows.size)
        assertEquals(5, secondPage.rows.size)
        assertTrue(firstPage.hasEarlier)
        assertFalse(secondPage.hasEarlier)
        assertEquals(55, (firstPage.rows + secondPage.rows).map { it.id }.distinct().size)
    }

    // REOPEN is its own verb (Register 3: completeTaskAndAdvance
    // is completion-only in the trunk). Reopening the last finish
    // restores the task, clears its stamp, and strips the exact
    // receipt entry it wrote from the still-active session —
    // count decremented, other entries untouched (Wieder).
    @Test
    fun reopen_restoresTaskAndStripsItsReceiptEntry() = runBlocking {
        val dao = db.monolithDao()
        val sessionId = dao.insertSession(
            com.example.data.FocusSessionEntity(
                intention = "Reopen test session",
                profileName = "Manual Focus",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = android.os.SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false,
                completedTasksSummary = "Other Task (10m est / 9m actual) • Target Task (25m est / 24m actual)",
                tasksCompletedCount = 2
            )
        ).toInt()
        val taskId = dao.insertTask(
            completedTask("Target Task", System.currentTimeMillis()).copy(
                estimatedMinutes = 25,
                actualSecondsSpent = 1440,
                completedInSessionId = sessionId
            )
        ).toInt()

        repo.reopenStoredTask(taskId)

        val restored = dao.getTaskById(taskId)!!
        assertTrue(restored.resolvedStatus != TaskQueueStatus.COMPLETED)
        assertEquals(0L, restored.completedAtMillis)
        val session = dao.getActiveSessionSnapshot()!!
        assertEquals("Other Task (10m est / 9m actual)", session.completedTasksSummary)
        assertEquals(1, session.tasksCompletedCount)
    }

    // The verb refuses to touch a task that is not completed.
    @Test
    fun reopen_nonCompletedTask_writesNothing() = runBlocking {
        val dao = db.monolithDao()
        val taskId = dao.insertTask(
            FocusTaskEntity(
                title = "Still Queued",
                estimatedMinutes = 25,
                categoryTag = "Study",
                queueOrder = 1,
                status = TaskQueueStatus.QUEUED.name
            )
        ).toInt()
        repo.reopenStoredTask(taskId)
        val task = dao.getTaskById(taskId)!!
        assertEquals(TaskQueueStatus.QUEUED, task.resolvedStatus)
        assertEquals(0L, task.completedAtMillis)
    }

    // A declared word with no tasks still puts its month in the
    // index — otherwise the word is filed nowhere (Selbst).
    @Test
    fun wordOnlyMonth_appearsInIndex() = runBlocking {
        repo.declareDayWord("2026-08-14", "ILL")
        assertEquals("ILL", repo.dayWords()["2026-08-14"])
        val months = repo.storedMonths().map { it.year to it.month }
        assertTrue(months.contains(2026 to 8))
        // The closed word list refuses anything else, TEST included.
        repo.declareDayWord("2026-08-15", "TEST")
        assertNull(repo.dayWords()["2026-08-15"])
    }
}
