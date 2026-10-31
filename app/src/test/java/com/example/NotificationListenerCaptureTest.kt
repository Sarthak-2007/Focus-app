package com.example

import android.app.Notification
import android.content.Context
import android.os.Process
import android.os.SystemClock
import android.service.notification.NotificationListenerService
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
 * Issue #45 (C5), end-to-end through the real listener and the
 * real Room database:
 * - an unchanged update of a held notification inserts nothing,
 * - a changed update replaces the held row (exactly one copy),
 * - a removal for our own listener cancel keeps the held copy,
 * - a real removal (the sender recalled it) deletes the held row
 *   so the message cannot resurrect in the digest.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationListenerCaptureTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dao get() = MonolithDatabase.getInstance(context).monolithDao()

    private fun sbn(title: String, text: String, postTime: Long): StatusBarNotification {
        val notification = Notification.Builder(context, "c5")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        return StatusBarNotification(
            "com.example.chat", "com.example.chat", 7, null,
            0, 0, 0, notification, Process.myUserHandle(), postTime
        )
    }

    private suspend fun awaitRows(expected: (List<com.example.data.CapturedNotificationEntity>) -> Boolean) =
        run {
            var rows = dao.getCapturedNotificationsSnapshot()
            var waitedMs = 0L
            while (!expected(rows) && waitedMs < 5_000L) {
                delay(100L)
                waitedMs += 100L
                rows = dao.getCapturedNotificationsSnapshot()
            }
            rows
        }

    /** Active session + a LOCKED chat app, then a fresh listener. */
    private suspend fun armedListener(): MonolithNotificationListener {
        dao.clearAllCapturedNotifications()
        dao.insertSession(
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
        return Robolectric.buildService(MonolithNotificationListener::class.java).create().get()
    }

    @Test
    fun unchangedUpdateInsertsNothingAndChangedUpdateReplaces() = runBlocking {
        val listener = armedListener()
        val postTime = System.currentTimeMillis()

        listener.onNotificationPosted(sbn("Mum", "hi", postTime))
        var rows = awaitRows { it.isNotEmpty() }
        assertTrue("the capture never landed", rows.isNotEmpty())
        assertEquals(1, rows.size)
        val firstRowId = rows.single().id
        // Let the dedupe tracker settle (it records just after the
        // insert lands) before replaying the update.
        delay(300L)

        // Identical update: no new row.
        listener.onNotificationPosted(sbn("Mum", "hi", postTime))
        delay(700L)
        rows = dao.getCapturedNotificationsSnapshot()
        assertEquals("an unchanged update must not duplicate the held copy", 1, rows.size)

        // Changed content under the same key: the held row is
        // replaced — still exactly one row (a fresh row id, since
        // the superseded row is deleted and re-inserted), with the
        // original postTime as its timestamp. (Issue #138 CUT: the
        // row carries no body, so replacement is read off the id,
        // not off stored content.)
        listener.onNotificationPosted(sbn("Mum", "edited", postTime))
        rows = awaitRows { it.size == 1 && it.single().id != firstRowId }
        assertEquals(1, rows.size)
        assertEquals(postTime, rows.single().timestampMillis)
    }

    /**
     * Issue #155 WING-F (SMALL-2): the badge/Deck held count is this
     * table's SIZE, so a duplicate row is a false glance claim. A
     * fresh listener instance (empty in-memory dedupe — the state
     * after a process restart, when the rebind sweep re-walks a
     * still-live held notification) re-posting the identical
     * capture must insert NOTHING: the table's own stored
     * identity (package + postTime) is the durable dedupe.
     */
    @Test
    fun recaptureByFreshListenerInstanceDoesNotDuplicateHeldRow() = runBlocking {
        val listener = armedListener()
        val postTime = System.currentTimeMillis()

        listener.onNotificationPosted(sbn("Mum", "hi", postTime))
        val rows = awaitRows { it.isNotEmpty() }
        assertTrue("the capture never landed", rows.isNotEmpty())
        assertEquals(1, rows.size)
        delay(300L)

        // A NEW listener instance: its dedupe tracker starts empty,
        // exactly as after a process death + rebind sweep.
        val freshListener = Robolectric.buildService(
            MonolithNotificationListener::class.java
        ).create().get()
        freshListener.onNotificationPosted(sbn("Mum", "hi", postTime))
        delay(900L)
        assertEquals(
            "a re-capture of an already-held notification must not " +
                "inflate the held count the badge prints",
            1, dao.getCapturedNotificationsSnapshot().size
        )
    }

    /** Issue #138 CUT, pinned (supersedes Audit R6 #340 F68's
     *  consent-gated body storage): a capture still lands, but the
     *  held row is METADATA ONLY — package, app name, capture
     *  time. No consent state changes this: the entity carries no
     *  sender/body field at all, which the reflection assertion
     *  below pins so a future field cannot quietly restore
     *  content storage. */
    @Test
    fun captureStoresMetadataOnlyUnderTheCut() = runBlocking {
        val listener = armedListener()
        val postTime = System.currentTimeMillis()

        listener.onNotificationPosted(sbn("Mum", "hi", postTime))
        val rows = awaitRows { it.isNotEmpty() }
        assertTrue("the capture never landed", rows.isNotEmpty())
        assertEquals(1, rows.size)
        assertEquals("com.example.chat", rows.single().packageName)
        assertEquals("Chat", rows.single().appName)
        assertEquals(postTime, rows.single().timestampMillis)

        val fieldNames = com.example.data.CapturedNotificationEntity::class.java
            .declaredFields.map { it.name }.toSet()
        assertTrue(
            "content storage must stay cut: no senderTitle/messageBody field",
            fieldNames.none { it == "senderTitle" || it == "messageBody" }
        )
    }

    @Test
    fun removalDeletesHeldCopyButOurOwnCancelDoesNot() = runBlocking {
        val listener = armedListener()
        val postTime = System.currentTimeMillis()
        val posted = sbn("Mum", "hi", postTime)

        listener.onNotificationPosted(posted)
        val rows = awaitRows { it.isNotEmpty() }
        assertTrue("the capture never landed", rows.isNotEmpty())
        delay(300L)

        // The listener's own suppression cancel echoes back as a
        // removal — the held copy must survive it.
        listener.onNotificationRemoved(
            posted, null, NotificationListenerService.REASON_LISTENER_CANCEL
        )
        delay(700L)
        assertEquals(
            "our own cancel must not delete the held copy",
            1, dao.getCapturedNotificationsSnapshot().size
        )

        // A real removal (sender/app cancelled it): the held copy
        // is deleted, so the message cannot resurrect in the digest.
        listener.onNotificationRemoved(
            posted, null, NotificationListenerService.REASON_CANCEL
        )
        val after = awaitRows { it.isEmpty() }
        assertTrue("a recalled message must leave the digest", after.isEmpty())
    }
}
