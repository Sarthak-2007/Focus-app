package com.example

import android.app.Notification
import com.example.service.CaptureDedupeTracker
import com.example.service.NotificationCapturePolicy
import com.example.service.SeenCapture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #45 (C5): the notification-kind filter's truth table and
 * the dedupe tracker's behaviour, as pure JVM tests. The policy
 * reads only the inlined Notification flag/category constants, so
 * no Android runtime is needed here.
 */
// PIN-SCOPE: SOURCE — pure capture policy + in-memory dedupe tracker (working-rules adoption, Oct 31 18:06 IST).
class NotificationCapturePolicyTest {

    @Test
    fun plainMessageIsCaptured() {
        assertTrue(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_MESSAGE))
        assertTrue(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_EMAIL))
        assertTrue(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_SOCIAL))
        assertTrue(NotificationCapturePolicy.shouldCaptureNotification(0, null))
    }

    @Test
    fun groupSummaryIsSkipped() {
        assertFalse(
            NotificationCapturePolicy.shouldCaptureNotification(
                Notification.FLAG_GROUP_SUMMARY, Notification.CATEGORY_MESSAGE
            )
        )
    }

    @Test
    fun ongoingIsSkipped() {
        assertFalse(
            NotificationCapturePolicy.shouldCaptureNotification(
                Notification.FLAG_ONGOING_EVENT, Notification.CATEGORY_MESSAGE
            )
        )
    }

    @Test
    fun foregroundServiceIsSkipped() {
        assertFalse(
            NotificationCapturePolicy.shouldCaptureNotification(
                Notification.FLAG_FOREGROUND_SERVICE, Notification.CATEGORY_SERVICE
            )
        )
        // The flag alone is enough, whatever the category claims.
        assertFalse(
            NotificationCapturePolicy.shouldCaptureNotification(
                Notification.FLAG_FOREGROUND_SERVICE, Notification.CATEGORY_MESSAGE
            )
        )
    }

    @Test
    fun statusCategoriesAreSkipped() {
        assertFalse(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_PROGRESS))
        assertFalse(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_SERVICE))
        assertFalse(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_TRANSPORT))
    }

    @Test
    fun callAndAlarmKeepTheNeverSuppressRule() {
        assertFalse(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_CALL))
        assertFalse(NotificationCapturePolicy.shouldCaptureNotification(0, Notification.CATEGORY_ALARM))
    }

    @Test
    fun unrelatedFlagsDoNotBlockCapture() {
        val flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONLY_ALERT_ONCE
        assertTrue(NotificationCapturePolicy.shouldCaptureNotification(flags, Notification.CATEGORY_MESSAGE))
    }

    @Test
    fun fingerprintDistinguishesTitleAndBody() {
        assertEquals(
            NotificationCapturePolicy.contentFingerprint("Mum", "hi"),
            NotificationCapturePolicy.contentFingerprint("Mum", "hi")
        )
        assertNotEquals(
            NotificationCapturePolicy.contentFingerprint("Mum", "hi"),
            NotificationCapturePolicy.contentFingerprint("Mum", "changed")
        )
        assertNotEquals(
            NotificationCapturePolicy.contentFingerprint("Mum", "hi"),
            NotificationCapturePolicy.contentFingerprint("Dad", "hi")
        )
        // The separator keeps ("ab","c") and ("a","bc") distinct.
        assertNotEquals(
            NotificationCapturePolicy.contentFingerprint("ab", "c"),
            NotificationCapturePolicy.contentFingerprint("a", "bc")
        )
    }

    private fun seen(title: String, body: String, postTime: Long = 1_000L) = SeenCapture(
        fingerprint = NotificationCapturePolicy.contentFingerprint(title, body),
        packageName = "com.example.chat",
        postTimeMillis = postTime
    )

    @Test
    fun trackerReportsUnchangedOnlyForSameKeyAndContent() {
        val tracker = CaptureDedupeTracker()
        val fp = NotificationCapturePolicy.contentFingerprint("Mum", "hi")
        assertFalse(tracker.isUnchanged("k1", fp))
        tracker.record("k1", seen("Mum", "hi"))
        assertTrue(tracker.isUnchanged("k1", fp))
        assertFalse(
            tracker.isUnchanged("k1", NotificationCapturePolicy.contentFingerprint("Mum", "edited"))
        )
        // Same content under a different key is a different notification.
        assertFalse(tracker.isUnchanged("k2", fp))
    }

    @Test
    fun trackerForgetDropsTheEntryAndReturnsIt() {
        val tracker = CaptureDedupeTracker()
        tracker.record("k1", seen("Mum", "hi"))
        val dropped = tracker.forget("k1")
        assertEquals("com.example.chat", dropped?.packageName)
        assertNull(tracker.seen("k1"))
        assertFalse(
            tracker.isUnchanged("k1", NotificationCapturePolicy.contentFingerprint("Mum", "hi"))
        )
        assertNull(tracker.forget("k1"))
    }

    @Test
    fun trackerRecordReplacesThePreviousCapture() {
        val tracker = CaptureDedupeTracker()
        tracker.record("k1", seen("Mum", "hi", postTime = 1_000L))
        tracker.record("k1", seen("Mum", "edited", postTime = 2_000L))
        assertEquals(2_000L, tracker.seen("k1")?.postTimeMillis)
        assertTrue(
            tracker.isUnchanged("k1", NotificationCapturePolicy.contentFingerprint("Mum", "edited"))
        )
    }
}
