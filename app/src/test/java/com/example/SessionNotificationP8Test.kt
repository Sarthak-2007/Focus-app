package com.example

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.service.SessionNotificationSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P8b — THE ONE NOTIFICATION (service/SessionNotification.kt):
 *
 *  - ONE card: a single fixed ID, updated in place.
 *  - THE RULED LINE: the wording is Stage 5 Amendment A-6.2's —
 *    the exact literal P0 carries as Hld1Wording — with no digit
 *    anywhere in it (the notification carries no numbers).
 *  - IT NEVER PROMOTES ITSELF: low importance, low priority,
 *    silent, ongoing while the seal stands, no chronometer, no
 *    badge, no actions.
 */
class SessionNotificationP8Test {

    @Test
    fun wording_isTheRuledLine_exactly() {
        assertEquals("Locked until the hour", SessionNotificationSpec.WORDING)
        assertEquals("Monolith", SessionNotificationSpec.TITLE)
    }

    @Test
    fun wording_carriesNoNumber() {
        assertFalse(
            "the one plain line carries no digit — FIG-8 claims no figure for this context",
            SessionNotificationSpec.WORDING.any { it.isDigit() }
        )
        assertFalse(SessionNotificationSpec.WORDING.contains("%"))
    }

    @Test
    fun oneCard_oneFixedId() {
        assertTrue(SessionNotificationSpec.NOTIFICATION_ID > 0)
        // The ID is a compile-time constant: one value exists, so a
        // second card cannot be posted beside the first.
        assertEquals(2711, SessionNotificationSpec.NOTIFICATION_ID)
    }

    @Test
    fun itNeverPromotesItself() {
        assertEquals(NotificationManager.IMPORTANCE_LOW, SessionNotificationSpec.IMPORTANCE)
        assertEquals(NotificationCompat.PRIORITY_LOW, SessionNotificationSpec.PRIORITY)
        assertTrue(SessionNotificationSpec.ONGOING)
        assertTrue(SessionNotificationSpec.SILENT)
        assertTrue(SessionNotificationSpec.ONLY_ALERT_ONCE)
        assertFalse(SessionNotificationSpec.USES_CHRONOMETER)
        assertFalse(SessionNotificationSpec.HAS_BADGE_NUMBER)
        assertFalse(SessionNotificationSpec.HAS_ACTIONS)
    }
}
