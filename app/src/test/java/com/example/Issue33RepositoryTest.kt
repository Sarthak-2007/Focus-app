package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #33 (audit R1, repository slice) — the regression pins:
 *
 *  1. LOST UPDATES (F2): a session tick computed from a caller's
 *     STALE snapshot must not clobber fields another writer landed
 *     after the snapshot was taken. The intercept path bumps
 *     phantomReachesBlocked on the live row; a tick carrying the
 *     pre-intercept snapshot used to write the whole stale row back
 *     and silently zero the count.
 *  2. HIDDEN SINGLETON (F1): the transactional paths run on the
 *     INJECTED database provider, not on a `getInstance` the
 *     function went and fetched behind the DAO's back.
 *  3. BOOT COUNT (F4): the nullable read and the legacy Int surface
 *     agree whenever the system value exists — the fabricated
 *     fallback may never contradict a real read on decision paths.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue33RepositoryTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun staleSnapshotTickDoesNotClobberInterceptCount() = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        val nowMono = SystemClock.elapsedRealtime()
        val row = FocusSessionEntity(
            intention = "ISSUE-33 TICK",
            profileName = "Manual",
            targetDurationSeconds = 3600,
            elapsedSeconds = 0,
            startWallTimeMillis = System.currentTimeMillis(),
            startElapsedRealtimeMillis = nowMono,
            lastTickElapsedRealtimeMillis = nowMono,
            totalStudyBlocks = 1,
            currentBlockIndex = 1,
            isActive = true,
            completedSuccessfully = false
        )
        dao.deactivateAllSessions()
        val id = dao.insertSession(row).toInt()
        try {
            // The ViewModel ticker's snapshot, taken BEFORE the intercept.
            val stale = dao.getSessionById(id)!!
            assertEquals(0, stale.phantomReachesBlocked)

            // The Shield's intercept lands on the live row.
            repository.recordIntercept(
                packageName = "com.example.distractor",
                appName = "Distractor",
                sessionIntention = "ISSUE-33 TICK",
                outcome = "Blocked"
            )
            assertEquals(1, dao.getSessionById(id)!!.phantomReachesBlocked)

            // The stale tick fires — the count must survive it.
            repository.updateSessionProgress(stale)
            assertEquals(1, dao.getSessionById(id)!!.phantomReachesBlocked)
        } finally {
            dao.deleteSessionById(id)
        }
    }

    @Test
    fun transactionalPathsUseTheInjectedDatabaseProvider() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            var providerCalls = 0
            val repository = MonolithRepository(context, db.monolithDao()) {
                providerCalls++
                db
            }
            val session = repository.startSession(
                intention = "ISSUE-33 PROVIDER",
                profileName = "Manual",
                durationMinutes = 30
            )
            assertTrue("startSession must transact on the injected database", providerCalls > 0)
            assertNotNull(db.monolithDao().getSessionById(session.id))
        } finally {
            db.close()
        }
    }

    @Test
    fun bootCountSurfacesAgreeWhenTheSystemValueExists() {
        val repository = MonolithRepository(
            context,
            MonolithDatabase.getInstance(context).monolithDao()
        )
        val nullable = repository.querySystemBootCountOrNull()
        if (nullable != null) {
            assertEquals(nullable, repository.querySystemBootCount())
        }
    }
}
