package com.example

import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.os.SystemClock
import android.service.notification.StatusBarNotification
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.service.HeldSuppressionCounter
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
 * Issue #100 (notification-content storage CUT), end-to-end through
 * the real listener and the real Room database:
 * - a blocked app's notification during a session is SUPPRESSED —
 *   the only trace is the in-memory, session-scoped suppression
 *   counter; no content is stored anywhere (there is no captured
 *   table left to assert against — that absence is the cure),
 * - a notification whose body lives OUTSIDE EXTRA_TEXT (big-text)
 *   is suppressed too (finding 4),
 * - a one-time code is never suppressed (issue #120),
 * - the counter is session-scoped: another session id reads 0.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationListenerCaptureTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dao get() = MonolithDatabase.getInstance(context).monolithDao()

    private fun sbn(
        title: String,
        text: String?,
        postTime: Long,
        bigText: String? = null,
        pkg: String = "com.example.chat"
    ): StatusBarNotification {
        val builder = Notification.Builder(context, "c5")
            .setContentTitle(title)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
        if (text != null) builder.setContentText(text)
        val notification = builder.build()
        if (bigText != null) {
            val extras = Bundle()
            extras.putCharSequence(Notification.EXTRA_TITLE, title)
            extras.putCharSequence(Notification.EXTRA_BIG_TEXT, bigText)
            notification.extras.putAll(extras)
        }
        return StatusBarNotification(
            pkg, pkg, 7, null,
            0, 0, 0, notification, Process.myUserHandle(), postTime
        )
    }

    private suspend fun awaitCount(sessionId: Int, expected: Int): Int {
        var count = HeldSuppressionCounter.countFor(sessionId)
        var waitedMs = 0L
        while (count < expected && waitedMs < 5_000L) {
            delay(100L)
            waitedMs += 100L
            count = HeldSuppressionCounter.countFor(sessionId)
        }
        return count
    }

    /** Active session + a LOCKED chat app, then a fresh listener.
     *  Returns the listener and the session id. */
    private suspend fun armedListener(): Pair<MonolithNotificationListener, Int> {
        val sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "C5 listener test",
                profileName = "C5",
                targetDurationSeconds = 50 * 60,
                elapsedSeconds = 0,
                startWallTimeMillis = System.currentTimeMillis(),
                startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
                isActive = true,
                completedSuccessfully = false
            )
        ).toInt()
        dao.insertApp(
            BlockedAppEntity(
                packageName = "com.example.chat",
                appName = "Chat",
                category = "Messaging",
                isBlocked = true,
                blockTier = AppBlockTier.LOCKED.name
            )
        )
        val listener = Robolectric.buildService(MonolithNotificationListener::class.java).create().get()
        return listener to sessionId
    }

    @Test
    fun blockedNotificationIsSuppressedAndCounted_notStored() = runBlocking {
        val (listener, sessionId) = armedListener()
        val postTime = System.currentTimeMillis()

        listener.onNotificationPosted(sbn("Mum", "hi", postTime))
        assertEquals(
            "a held notification must be counted as suppressed",
            1, awaitCount(sessionId, 1)
        )
        // Session scope (finding 3's replacement): any other
        // session reads zero — the count is not a global pile.
        assertEquals(0, HeldSuppressionCounter.countFor(sessionId + 999))
    }

    @Test
    fun bodyOutsideExtraTextIsSuppressedToo() = runBlocking {
        val (listener, sessionId) = armedListener()
        val postTime = System.currentTimeMillis()

        // No EXTRA_TEXT at all — the body lives in EXTRA_BIG_TEXT.
        listener.onNotificationPosted(sbn("Mum", null, postTime, bigText = "long message body"))
        assertEquals(
            "a big-text-only notification must not walk through",
            1, awaitCount(sessionId, 1)
        )
    }

    @Test
    fun oneTimeCodeIsNeverSuppressed() = runBlocking {
        val (listener, sessionId) = armedListener()
        val postTime = System.currentTimeMillis()

        listener.onNotificationPosted(sbn("Bank", "Your OTP is 123456", postTime))
        delay(900L)
        assertEquals(
            "a one-time code keeps its shade entry — never suppressed",
            0, HeldSuppressionCounter.countFor(sessionId)
        )
        assertTrue(true)
    }
}
