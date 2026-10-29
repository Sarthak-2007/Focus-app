package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CapturedNotificationEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
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
 * THE FROZEN DIGEST's sealed gate (Quaere's angered thumb): while a
 * session is active, the held world can be read but never acted
 * on. The repository — not the UI — is the binding gate: both
 * delete paths refuse, return false, and touch nothing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FrozenDigestGateTest {

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

    private suspend fun insertCapture(body: String): Int {
        val dao = db.monolithDao()
        dao.insertCapturedNotification(
            CapturedNotificationEntity(
                timestampMillis = System.currentTimeMillis(),
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                senderTitle = "Sender",
                messageBody = body
            )
        )
        return dao.getCapturedNotificationsSnapshot().first { it.messageBody == body }.id
    }

    private suspend fun insertActiveSession(): Int {
        val dao = db.monolithDao()
        return dao.insertSession(
            FocusSessionEntity(
                intention = "Gate test session",
                profileName = "Manual Focus",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        ).toInt()
    }

    @Test
    fun sealed_clearAndDelete_refuseAndTouchNothing() = runBlocking {
        val dao = db.monolithDao()
        val id = insertCapture("held one")
        insertCapture("held two")
        insertActiveSession()

        assertFalse(repo.clearAllCapturedNotifications())
        assertFalse(repo.deleteCapturedNotification(id))
        assertEquals(2, dao.getCapturedNotificationsSnapshot().size)
    }

    @Test
    fun unsealed_clearAndDelete_runAndReturnTrue() = runBlocking {
        val dao = db.monolithDao()
        val id = insertCapture("held one")
        insertCapture("held two")

        assertTrue(repo.deleteCapturedNotification(id))
        assertEquals(1, dao.getCapturedNotificationsSnapshot().size)
        assertTrue(repo.clearAllCapturedNotifications())
        assertEquals(0, dao.getCapturedNotificationsSnapshot().size)
    }

    @Test
    fun sessionEnded_gateOpensAgain() = runBlocking {
        val dao = db.monolithDao()
        insertCapture("held one")
        val sessionId = insertActiveSession()
        assertFalse(repo.clearAllCapturedNotifications())

        val session = dao.getActiveSessionSnapshot()!!
        assertEquals(sessionId, session.id)
        dao.updateSession(session.copy(isActive = false))

        assertTrue(repo.clearAllCapturedNotifications())
        assertEquals(0, dao.getCapturedNotificationsSnapshot().size)
    }

    // The frozen snapshot is the sealed truth surface: it must NOT
    // inherit the live flow's LIMIT 100 — a held item past the cap
    // is still held, and a snapshot that dropped it would lie.
    @Test
    fun snapshot_isNotSilentlyCapped() = runBlocking {
        val dao = db.monolithDao()
        repeat(105) { i ->
            dao.insertCapturedNotification(
                CapturedNotificationEntity(
                    timestampMillis = System.currentTimeMillis() + i,
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    senderTitle = "Sender",
                    messageBody = "capture $i"
                )
            )
        }
        assertEquals(105, repo.capturedNotificationsSnapshot().size)
    }
}
