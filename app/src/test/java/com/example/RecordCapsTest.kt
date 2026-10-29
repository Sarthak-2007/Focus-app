package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.InterceptLogEntity
import com.example.data.MonolithDao
import com.example.data.MonolithDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * WORK-QUEUE 16 — THE RECORD CAPS DIE. Tief's audit found the
 * sessions-history reads capped at LIMIT 60 and the intercept-log
 * read capped at LIMIT 80: the stored record silently truncated in
 * the lists. These tests seed PAST both caps and read every row
 * back — full history, newest first, order intact.
 *
 * The captured-notifications live flow keeps its LIMIT 100 (a live
 * flow, not history — the split is ruled sound, Tief audit) and is
 * not touched by this change; no test here asserts against it.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RecordCapsTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var dao: MonolithDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.monolithDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun session(startMillis: Long): FocusSessionEntity =
        FocusSessionEntity(
            intention = "caps-proof",
            profileName = "STUDY",
            targetDurationSeconds = 3000,
            elapsedSeconds = 3000,
            startWallTimeMillis = startMillis,
            startElapsedRealtimeMillis = startMillis,
            isActive = false,
            completedSuccessfully = true
        )

    private fun intercept(atMillis: Long): InterceptLogEntity =
        InterceptLogEntity(
            timestampMillis = atMillis,
            packageName = "com.example.blocked",
            appName = "Blocked",
            sessionIntention = "caps-proof",
            outcome = "BLOCKED"
        )

    @Test
    fun sessionHistorySnapshotReturnsAll75PastTheOldCapOf60() = runBlocking {
        val base = 1_700_000_000_000L
        repeat(75) { i -> dao.insertSession(session(base + i * 60_000L)) }

        val history = dao.getSessionHistorySnapshot()

        assertEquals(75, history.size)
        // Newest first, and the OLDEST row — the one the cap silently
        // dropped — is present at the tail.
        assertEquals(base + 74 * 60_000L, history.first().startWallTimeMillis)
        assertEquals(base, history.last().startWallTimeMillis)
    }

    @Test
    fun sessionHistoryFlowReturnsAll75PastTheOldCapOf60() = runBlocking {
        val base = 1_700_100_000_000L
        repeat(75) { i -> dao.insertSession(session(base + i * 60_000L)) }

        val history = dao.getSessionHistoryFlow().first()

        assertEquals(75, history.size)
        assertEquals(base + 74 * 60_000L, history.first().startWallTimeMillis)
        assertEquals(base, history.last().startWallTimeMillis)
    }

    @Test
    fun interceptLogFlowReturnsAll100PastTheOldCapOf80() = runBlocking {
        val base = 1_700_200_000_000L
        repeat(100) { i -> dao.insertInterceptLog(intercept(base + i * 1_000L)) }

        val logs = dao.getInterceptLogsFlow().first()

        assertEquals(100, logs.size)
        assertEquals(base + 99 * 1_000L, logs.first().timestampMillis)
        assertEquals(base, logs.last().timestampMillis)
    }
}
