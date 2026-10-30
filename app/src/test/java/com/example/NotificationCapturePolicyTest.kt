package com.example

import android.app.Notification
import com.example.service.NotificationCapturePolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #45 (C5): the notification-kind filter's truth table, as
 * pure JVM tests. The policy reads only the inlined Notification
 * flag/category constants, so no Android runtime is needed here.
 * Issue #100: the policy survives the CUT as the SNOOZE-HOLD
 * eligibility gate (plus the OTP exemption via
 * looksLikeOneTimeCode); the dedupe tracker and content
 * fingerprint it once fed are deleted with the store.
 */
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

}
