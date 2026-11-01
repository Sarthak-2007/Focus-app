package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.SessionEventKind
import com.example.data.SessionEventLogEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F05: the append-only session-event log and
 * the planned-end backup on the session row.
 *
 * The repository writes the events (start / supersede / end /
 * user-switch gap); these cases pin the storage contract the
 * restore path depends on: events append in order, never mutate,
 * and the planned end stamped at birth survives later row writes.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildTimerEventLogTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun sessionLifecycle_writesAppendOnlyTransitionEvents() = runBlocking {
        val dao = db.monolithDao()
        dao.insertSessionEvent(
            SessionEventLogEntity(sessionId = 7, kind = SessionEventKind.STARTED, atWallMillis = 100L, detail = "5000")
        )
        dao.insertSessionEvent(
            SessionEventLogEntity(sessionId = 7, kind = SessionEventKind.ENDED, atWallMillis = 200L, detail = "COMPLETED")
        )
        dao.insertSessionEvent(
            SessionEventLogEntity(sessionId = 8, kind = SessionEventKind.STARTED, atWallMillis = 300L)
        )
        val events = dao.getSessionEventsForSession(7)
        assertEquals(2, events.size)
        assertEquals(SessionEventKind.STARTED, events[0].kind)
        assertEquals(SessionEventKind.ENDED, events[1].kind)
        // Append-only: the first row is unchanged after the later
        // appends (same detail, earlier id).
        assertEquals("5000", events[0].detail)
        assertTrue(events[0].id < events[1].id)
        // The whole-log snapshot carries every session's events.
        assertEquals(3, dao.getAllSessionEventsSnapshot().size)
    }

    @Test
    fun endTimeBackup_survivesMutableRowOverwrite() = runBlocking {
        val dao = db.monolithDao()
        val sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "study",
                profileName = "Default",
                targetDurationSeconds = 1500,
                elapsedSeconds = 0,
                startWallTimeMillis = 1_000L,
                startElapsedRealtimeMillis = 500L,
                isActive = true,
                completedSuccessfully = false,
                plannedEndWallMillis = 9_999L
            )
        ).toInt()
        // A later progress write touches the row; the planned end
        // stamped at birth is a stored column, not a derivation the
        // write can move.
        val row = dao.getSessionById(sessionId)!!
        dao.updateSession(row.copy(elapsedSeconds = 42))
        assertEquals(9_999L, dao.getSessionById(sessionId)!!.plannedEndWallMillis)
    }

    @Test
    fun eventLog_vocabularyCoversLifecycleAndGap() {
        // The four kinds the restore + truth paths rely on.
        assertEquals("STARTED", SessionEventKind.STARTED)
        assertEquals("SUPERSEDED", SessionEventKind.SUPERSEDED)
        assertEquals("ENDED", SessionEventKind.ENDED)
        assertEquals("USER_GAP", SessionEventKind.USER_GAP)
    }
}
