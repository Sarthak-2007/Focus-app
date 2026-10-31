package com.example

import android.app.Notification
import android.content.Context
import android.os.Process
import android.os.SystemClock
import android.service.notification.StatusBarNotification
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.service.MonolithNotificationListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Truth Car 3, appendix APP-2 — as completed by the issue #138
 * CUT: the original law was that a stored capture body is the
 * notification's own text or NOTHING (the old code stored an
 * invented sentence, "Suppressed notification during study
 * session", as if the sender had written it). Sarthak's #138
 * ruling cut content storage entirely, so the law now reads
 * stronger: a notification from a blocked app lands in the digest
 * as METADATA ONLY (app + capture time) — no sender, no body,
 * invented or otherwise. This test keeps its original scenario
 * (a title-only notification) and pins the metadata landing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TruthCar3CaptureBodyTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun captureBodyIsNeverInvented() = runBlocking {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        dao.clearAllCapturedNotifications()
        // This test runs against the PRODUCTION singleton DB (the
        // listener resolves it internally), so every row written
        // here must leave with the test: a leaked ACTIVE session
        // falsifies other classes' no-active-session assertions
        // (ReceiptClaimRepositoryTest documents exactly that
        // damage) and a leaked LOCKED app row pollutes the catalog.
        // The finally below covers every exit path — assertion
        // failure included — deleting by the inserted row's own
        // id / package via the DAO.
        var sessionId: Long? = null
        try {
        sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "Capture body test",
                profileName = "CaptureBody",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        )
        dao.insertApp(
            BlockedAppEntity(
                packageName = "com.example.chat",
                appName = "Chat",
                category = "Messaging",
                isBlocked = true,
                blockTier = AppBlockTier.LOCKED.name
            )
        )

        // A title-only notification: a sender, no body text.
        val notification = Notification.Builder(context, "truth3")
            .setContentTitle("Mum")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        // SDK 36's only public constructor is the @Deprecated
        // 10-argument form — (pkg, opPkg, id, tag, uid, initialPid,
        // score, notification, user, postTime). The 9-argument form
        // this call was written against no longer exists, hence the
        // third 0 (uid, initialPid, score — none of them read by
        // the listener under test).
        val sbn = StatusBarNotification(
            "com.example.chat", "com.example.chat", 7, null,
            0, 0, 0, notification, Process.myUserHandle(),
            System.currentTimeMillis()
        )

        val listener = Robolectric.buildService(MonolithNotificationListener::class.java)
            .create().get()
        listener.onNotificationPosted(sbn)

        // The capture lands on the listener's IO scope: poll the
        // store rather than sleeping a fixed guess.
        var rows = dao.getCapturedNotificationsSnapshot()
        var waitedMs = 0L
        while (rows.isEmpty() && waitedMs < 5_000L) {
            delay(100L)
            waitedMs += 100L
            rows = dao.getCapturedNotificationsSnapshot()
        }
        assertTrue("the capture never landed", rows.isNotEmpty())
        val row = rows.first { it.packageName == "com.example.chat" }
        // Metadata only (issue #138 CUT): the app that was held,
        // and nothing of the message — the entity has no sender or
        // body field to assert a value on (that absence is pinned
        // in NotificationListenerCaptureTest).
        assertEquals("Chat", row.appName)
        assertTrue(row.timestampMillis > 0L)
        } finally {
            sessionId?.let { dao.deleteSessionById(it.toInt()) }
            dao.deleteAppsByPackage(listOf("com.example.chat"))
            dao.clearAllCapturedNotifications()
        }
    }
}
