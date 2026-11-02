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
 *  - THE RULED LINE: the wording is Stage 5 Amendment A-6.2's,
 *    carried in the single copy source (copy_10_2_notification)
 *    since the mirrors retired — with no digit anywhere in it
 *    (the notification carries no numbers).
 *  - IT NEVER PROMOTES ITSELF: low importance, low priority,
 *    silent, ongoing while the seal stands, no chronometer, no
 *    badge, no actions.
 */
class SessionNotificationP8Test {

    // RE-POINTED (BUILD-SR-23, step 7): the WORDING mirror is
    // retired; the ruled line lives only in the single copy
    // source, which the poster reads. These tests pin THE SOURCE.
    private fun deckString(key: String): String {
        val xml = java.io.File("src/main/res/values/strongroom_strings.xml").readText()
        val match = Regex(
            "<string name=\"" + key + "\">(.*?)</string>",
            RegexOption.DOT_MATCHES_ALL
        ).find(xml) ?: error("deck key missing from the single source: $key")
        return match.groupValues[1]
    }

    @Test
    fun wording_isTheRuledLine_exactly() {
        assertEquals("Locked until the hour", deckString("copy_10_2_notification"))
    }

    @Test
    fun oneTextFieldOnly_noContentTitle() {
        // TYPE-SR-4 Spec 1 §4: the line stands in the content text;
        // the content title is never set — never both, never doubled.
        assertFalse(SessionNotificationSpec.HAS_CONTENT_TITLE)
    }

    @Test
    fun aDismissedLine_isNotRepostedForTheSession() {
        // Spec 1 §5.6: a dismissal is honoured until the lifting.
        assertTrue(SessionNotificationSpec.shouldPost(dismissedThisSession = false))
        assertFalse(SessionNotificationSpec.shouldPost(dismissedThisSession = true))
    }

    @Test
    fun wording_carriesNoNumber() {
        assertFalse(
            "the one plain line carries no digit — FIG-8 claims no figure for this context",
            deckString("copy_10_2_notification").any { it.isDigit() }
        )
        assertFalse(deckString("copy_10_2_notification").contains("%"))
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
