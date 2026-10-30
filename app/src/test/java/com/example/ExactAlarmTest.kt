package com.example

import android.os.Build
import com.example.service.ExactAlarm
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #319 (Audit R5 — YPT platform execution, F2): the
 * exact-alarm decision matrix. Before the cure the app held no
 * exact-alarm permission and every scheduled wake was inexact by
 * construction; now exactness is one pure decision:
 *
 *  - pre-API 31 there is no grant to probe, so exact is always
 *    lawful (setExactAndAllowWhileIdle needs no permission there);
 *  - API 31+ exact is lawful only while canScheduleExactAlarms()
 *    is true — the SCHEDULE_EXACT_ALARM grant is user-controlled
 *    and denied by default on Android 13+;
 *  - without the grant the schedulers must fall back to inexact,
 *    never throw and never skip the alarm: alarms are hints and
 *    the heartbeat reconciles, so a missing grant may cost
 *    punctuality but never correctness.
 */
class ExactAlarmTest {

    @Test
    fun `pre-S devices always use exact - no grant exists to probe`() {
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.N_MR1, canScheduleExactAlarms = false))
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.R, canScheduleExactAlarms = false))
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.R, canScheduleExactAlarms = true))
    }

    @Test
    fun `S and above use exact only while the grant is held`() {
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.S, canScheduleExactAlarms = true))
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.TIRAMISU, canScheduleExactAlarms = true))
        assertTrue(ExactAlarm.useExact(Build.VERSION_CODES.UPSIDE_DOWN_CAKE, canScheduleExactAlarms = true))
        assertFalse(ExactAlarm.useExact(Build.VERSION_CODES.S, canScheduleExactAlarms = false))
        assertFalse(ExactAlarm.useExact(Build.VERSION_CODES.TIRAMISU, canScheduleExactAlarms = false))
        assertFalse(ExactAlarm.useExact(Build.VERSION_CODES.UPSIDE_DOWN_CAKE, canScheduleExactAlarms = false))
    }
}
