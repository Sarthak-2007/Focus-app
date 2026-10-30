package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build

/**
 * Issue #319 (Audit R5 supplement — YPT platform execution, F2) —
 * the one exact-alarm law for every wake-up Monolith schedules.
 *
 * Before this cure the app held no exact-alarm permission at all:
 * the routine chain (RoutineScheduler) and the release-retry
 * cadence (ReleaseRetryScheduler) both fired through
 * setAndAllowWhileIdle, which Android may defer by minutes — so a
 * routine boundary could land late even with the battery exemption
 * held. YPT holds the exact-alarm permission and its locks fire
 * on the minute; this closes that gap.
 *
 * The law, kept deliberately small:
 *  - The manifest declares SCHEDULE_EXACT_ALARM. On Android 13+
 *    it is denied by default until the user grants "Alarms &
 *    reminders" — so exactness is always probed, never assumed:
 *    [useExact] is the whole decision, pure and unit-tested.
 *  - Below API 31 there is no grant to probe: setExactAndAllowWhileIdle
 *    (API 23+, and minSdk is 24) needs no permission there.
 *  - Alarms stay HINTS, never the mechanism (the routines
 *    machinery's standing law): when exact is unavailable the
 *    inexact call still fires, and the heartbeat reconciles from
 *    stored state on every other trigger. A missing grant degrades
 *    punctuality, never correctness.
 */
object ExactAlarm {

    /** The whole decision: exact only where the platform either
     *  needs no grant (pre-API 31) or the grant is actually held. */
    fun useExact(sdkInt: Int, canScheduleExactAlarms: Boolean): Boolean =
        sdkInt < Build.VERSION_CODES.S || canScheduleExactAlarms

    /** Set a wake-up alarm at [atMillis] — exact when [useExact]
     *  allows, inexact otherwise. Cancel-then-set stays the
     *  caller's job, exactly as before. */
    fun setWakeup(
        alarmManager: AlarmManager,
        atMillis: Long,
        intent: PendingIntent,
        sdkInt: Int = Build.VERSION.SDK_INT
    ) {
        val canExact = if (sdkInt >= Build.VERSION_CODES.S) {
            runCatching { alarmManager.canScheduleExactAlarms() }.getOrDefault(false)
        } else {
            false
        }
        if (useExact(sdkInt, canExact)) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        }
    }
}
