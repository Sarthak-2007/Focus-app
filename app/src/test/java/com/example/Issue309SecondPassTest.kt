package com.example

import com.example.service.WatchItemId
import com.example.service.exactAlarmPermitted
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #309 (Audit R5, AppBlock second pass): the two pure
 * decisions this issue's cures rest on —
 * F2: exact alarms are permitted pre-API-31 unconditionally and
 *     afterwards exactly when the system says the grant is held.
 * F5: the permission board leads with its cheapest ask (read-only
 *     USAGE ACCESS) and closes with its scariest (DEVICE ADMIN
 *     among the last), never leading with ACCESSIBILITY again.
 */
class Issue309SecondPassTest {

    @Test
    fun exactAlarmDecision() {
        assertTrue(exactAlarmPermitted(sdkInt = 28, canScheduleExactAlarms = false))
        assertTrue(exactAlarmPermitted(sdkInt = 30, canScheduleExactAlarms = false))
        assertTrue(exactAlarmPermitted(sdkInt = 31, canScheduleExactAlarms = true))
        assertFalse(exactAlarmPermitted(sdkInt = 31, canScheduleExactAlarms = false))
        assertFalse(exactAlarmPermitted(sdkInt = 35, canScheduleExactAlarms = false))
        assertTrue(exactAlarmPermitted(sdkInt = 35, canScheduleExactAlarms = true))
    }

    @Test
    fun boardLeadsCheapestFirst() {
        val board = WatchItemId.PERMISSIONS
        assertEquals(WatchItemId.USAGE_ACCESS, board.first())
        assertTrue(
            board.indexOf(WatchItemId.USAGE_ACCESS) <
                board.indexOf(WatchItemId.ACCESSIBILITY)
        )
        assertTrue(
            board.indexOf(WatchItemId.ACCESSIBILITY) >
                board.indexOf(WatchItemId.NOTIFICATION_LISTENER)
        )
        // The counted four keep the board's relative order.
        val countedOnBoard = board.filter { it in WatchItemId.COUNTED }
        assertEquals(countedOnBoard, WatchItemId.COUNTED)
    }
}
