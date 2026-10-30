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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #106 (Audit R3, journey "tasks-lived") — regression tests
 * for findings 1, 2, 4 and 5. Finding 3 (undo banner born
 * off-screen) is covered by TasksUndoBannerRenderTest.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TasksLivedFixesTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // The seal marker is install-global prefs; never let one
        // test's seal leak into another test's session.
        context.getSharedPreferences("monolith_seal", Context.MODE_PRIVATE)
            .edit().clear().commit()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun markSealed(sessionId: Int) {
        context.getSharedPreferences("monolith_seal", Context.MODE_PRIVATE)
            .edit().putInt("sealed_session_id", sessionId).commit()
    }

    // ---- Finding 1: seed-sync must never delete a user task on
    // a title collision ------------------------------------------

    @Test
    fun seedSync_keepsTwoDifferentTasksThatShareATitle() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        dao.insertTask(
            FocusTaskEntity(
                title = "Revision", notesOrSubsteps = "Physics thermodynamics",
                estimatedMinutes = 0, categoryTag = "Study", queueOrder = 10,
                status = TaskQueueStatus.QUEUED.name
            )
        )
        dao.insertTask(
            FocusTaskEntity(
                title = "Revision", notesOrSubsteps = "Chemistry GOC",
                estimatedMinutes = 0, categoryTag = "Study", queueOrder = 11,
                status = TaskQueueStatus.QUEUED.name
            )
        )
        repository.ensureSeededAndSyncPackages()
        repository.ensureSeededAndSyncPackages()
        val revisions = dao.getAllTasksSnapshot().filter { it.title == "Revision" }
        assertEquals("Both same-titled user tasks must survive seed-sync", 2, revisions.size)
        assertEquals(
            setOf("Physics thermodynamics", "Chemistry GOC"),
            revisions.map { it.notesOrSubsteps }.toSet()
        )
    }

    @Test
    fun seedSync_removesOnlyAnExactDoubleInsert() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        val dupe = FocusTaskEntity(
            title = "Read Chapter 4", notesOrSubsteps = "",
            estimatedMinutes = 0, categoryTag = "Study", queueOrder = 20,
            status = TaskQueueStatus.QUEUED.name
        )
        dao.insertTask(dupe)
        dao.insertTask(dupe)
        repository.ensureSeededAndSyncPackages()
        val rows = dao.getAllTasksSnapshot().filter { it.title == "Read Chapter 4" }
        assertEquals("One exact double-insert collapses to one row", 1, rows.size)
    }

    // ---- Finding 2: Deck advance must not rewrite a sealed
    // session's intention ----------------------------------------

    @Test
    fun sealedAdvance_doesNotRewriteSessionIntention() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        val active = dao.getAllTasksSnapshot().first { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        val session = repository.startSession(
            intention = active.title, profileName = "Study Loadout",
            durationMinutes = 25, studyBlocksCount = 1
        )
        markSealed(session.id)
        val next = repository.completeTaskAndAdvance(active.id)
        assertEquals("Synthesize Lecture 9 Architecture Notes", next?.title)
        val after = dao.getActiveSessionSnapshot()
        assertEquals(
            "A sealed session's intention (its receipt title) must not move",
            active.title, after?.intention
        )
    }

    @Test
    fun unsealedAdvance_alsoDoesNotRewriteSessionIntention() = runBlocking {
        // Closure rebase note (DURCH-C, #106): the Wave-3B cure for
        // finding 2 was a sealed-only refusal, and this test originally
        // asserted an UNSEALED advance still moves the intention. Main
        // has since adopted the stronger law (issue #124,
        // typing-intention): a session's intention is frozen at arm
        // time for its whole life, sealed or not. The PR's sealed-only
        // hunk was therefore dropped on rebase — main already cures
        // finding 2 in the stronger form — and this test now pins the
        // standing law so the weaker behaviour can never return.
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        val active = dao.getAllTasksSnapshot().first { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        repository.startSession(
            intention = active.title, profileName = "Study Loadout",
            durationMinutes = 25, studyBlocksCount = 1
        )
        val next = repository.completeTaskAndAdvance(active.id)
        assertEquals("Synthesize Lecture 9 Architecture Notes", next?.title)
        val after = dao.getActiveSessionSnapshot()
        assertEquals(
            "An unsealed session's intention is frozen at arm time too (issue #124)",
            active.title, after?.intention
        )
    }

    // ---- Finding 4: a mid-session jot-down must not hijack NOW -

    @Test
    fun midSessionJot_staysQueued_andTickDoesNotCrownIt() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        dao.getAllTasksSnapshot().forEach { dao.deleteTaskById(it.id) }
        val session = repository.startSession(
            intention = "Maths", profileName = "Study Loadout",
            durationMinutes = 25, studyBlocksCount = 1
        )
        val jot = repository.addTaskToQueue(
            title = "Jot for later", estimatedMinutes = 0,
            categoryTag = "Study", notesOrSubsteps = "", promoteToActive = false
        )
        assertEquals(TaskQueueStatus.QUEUED, jot.resolvedStatus)
        repository.updateSessionProgress(session)
        val afterTick = dao.getTaskById(jot.id)
        assertEquals(TaskQueueStatus.QUEUED, afterTick?.resolvedStatus)
        assertEquals(0, afterTick?.actualSecondsSpent)
    }

    @Test
    fun firstTaskOutsideSession_becomesActive() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        dao.getAllTasksSnapshot().forEach { dao.deleteTaskById(it.id) }
        val first = repository.addTaskToQueue(
            title = "First task of the day", estimatedMinutes = 0,
            categoryTag = "Study", notesOrSubsteps = "", promoteToActive = false
        )
        assertEquals(TaskQueueStatus.ACTIVE, first.resolvedStatus)
    }

    // ---- Finding 5: SPLIT derives steps from the task's own words

    @Test
    fun split_historyDates_neverGetsProblemSetSteps() {
        val steps = FocusTaskEntity.defaultSubStepsForCategory("Revise History dates", "Study")
        assertEquals(3, steps.size)
        assertTrue(steps.none { "problem set" in it.title.lowercase() })
        assertTrue(steps.none { "units" in it.title.lowercase() })
        assertTrue(steps.none { "diagram" in it.title.lowercase() })
    }

    @Test
    fun split_frenchVerbs_neverGetsProblemSetSteps() {
        val steps = FocusTaskEntity.defaultSubStepsForCategory("Learn French verbs", "Study")
        assertEquals(3, steps.size)
        assertTrue(steps.none { "problem set" in it.title.lowercase() })
        assertFalse(steps == FocusTaskEntity.defaultSubStepsForCategory("Revise History dates", "Study"))
    }

    @Test
    fun split_problemTask_keepsTheProblemSetTemplate() {
        val steps = FocusTaskEntity.defaultSubStepsForCategory("Finish Chapter 4 Problem Set (#1-6)", "Study")
        assertTrue(steps.any { "problem set" in it.title.lowercase() })
    }

    @Test
    fun split_titleThatListsItsParts_splitsOnItsOwnParts() {
        val steps = FocusTaskEntity.defaultSubStepsForCategory(
            "Read Ch. 4; make flashcards; self-test", "Study"
        )
        assertEquals(listOf("Read Ch. 4", "make flashcards", "self-test"), steps.map { it.title })
    }

    @Test
    fun split_unmatchedTask_stepsNameTheTaskItself() {
        val steps = FocusTaskEntity.defaultSubStepsForCategory("Organise the desk drawer", "Study")
        assertTrue(steps.all { "Organise the desk drawer" in it.title })
    }

    @Test
    fun autoSplit_writingTask_stillYieldsThreeSteps() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        val added = repository.addTaskToQueue(
            title = "Write Systems Lab Report", estimatedMinutes = 45,
            categoryTag = "Writing", notesOrSubsteps = "", promoteToActive = false
        )
        repository.autoSplitTaskIntoMicroSteps(added.id)
        val split = dao.getTaskById(added.id)
        assertEquals(3, split?.subSteps?.size)
    }
}
