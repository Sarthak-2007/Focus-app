package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.TaskQueueStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #124 (typing-intention) at the repository — the freeze.
 *
 * The audit's charge: the app put words in the user's mouth. The
 * session's intention was a factory default, a stock phrase
 * substituted for a blank, or whatever task title the queue last
 * copied in — and mid-session promotion rewrote the stored row,
 * so the receipt and later airlock lines no longer showed what
 * the session was armed with.
 *
 * These tests pin the cure at the snapshot point:
 *  1. a blank declaration is stored blank — no substitution;
 *  2. a declared intention is stored trimmed and capped;
 *  3. neither completion-driven nor manual task promotion ever
 *     rewrites the running session's stored intention.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class IntentionFreezeRepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** The suite shares one database file: take the active slot
     *  clean, the token suite's hygiene precedent. */
    private fun clearForeignActiveSessions(db: MonolithDatabase) {
        db.openHelper.writableDatabase.execSQL(
            "UPDATE focus_sessions SET isActive = 0 WHERE isActive = 1"
        )
    }

    @Test
    fun blankIntention_isStoredBlank_noStockPhrase() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val blank = repository.startSession(
            intention = "",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        assertEquals("", blank.intention)

        val spaces = repository.startSession(
            intention = "   ",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        assertEquals("", spaces.intention)
    }

    @Test
    fun declaredIntention_isTrimmedAndCappedAtTheSnapshot() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val trimmed = repository.startSession(
            intention = "  Finish the problem set  ",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        assertEquals("Finish the problem set", trimmed.intention)

        val capped = repository.startSession(
            intention = "x".repeat(200),
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        assertEquals(MonolithRepository.MAX_INTENTION_CHARS, capped.intention.length)
    }

    @Test
    fun completionPromotion_neverRewritesTheRunningSessionIntention() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        val first = repository.addTaskToQueue(
            title = "First Task Title",
            estimatedMinutes = 25,
            categoryTag = "Study",
            notesOrSubsteps = "",
            promoteToActive = true
        )
        repository.addTaskToQueue(
            title = "Second Task Title",
            estimatedMinutes = 25,
            categoryTag = "Study",
            notesOrSubsteps = "",
            promoteToActive = false
        )
        assertEquals(TaskQueueStatus.ACTIVE, first.resolvedStatus)

        repository.startSession(
            intention = "My Declared Words",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )

        val next = repository.completeTaskAndAdvance(first.id)
        assertNotNull(next)
        assertEquals("Second Task Title", next!!.title)

        val session = dao.getActiveSessionSnapshot()
        assertNotNull(session)
        assertEquals("My Declared Words", session!!.intention)
    }

    @Test
    fun manualPromotion_neverRewritesTheRunningSessionIntention() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        clearForeignActiveSessions(db)

        repository.addTaskToQueue(
            title = "Active Task Title",
            estimatedMinutes = 25,
            categoryTag = "Study",
            notesOrSubsteps = "",
            promoteToActive = true
        )
        val queued = repository.addTaskToQueue(
            title = "Promoted Task Title",
            estimatedMinutes = 25,
            categoryTag = "Study",
            notesOrSubsteps = "",
            promoteToActive = false
        )

        repository.startSession(
            intention = "Armed With These Words",
            profileName = "Manual",
            durationMinutes = 25,
            studyBlocksCount = 1
        )

        val promoted = repository.promoteTaskToActive(queued.id)
        assertNotNull(promoted)
        assertEquals(TaskQueueStatus.ACTIVE, promoted!!.resolvedStatus)

        val session = dao.getActiveSessionSnapshot()
        assertNotNull(session)
        assertEquals("Armed With These Words", session!!.intention)
    }
}
