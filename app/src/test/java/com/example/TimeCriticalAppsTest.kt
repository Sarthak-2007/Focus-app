package com.example

import android.app.Notification
import com.example.data.TimeCriticalApps
import com.example.service.TimeCriticalAlertGate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #121 (Audit R3b, "alarm-fires-mid-session"): the
 * time-critical class, proven as pure logic.
 *
 * Persona: a user whose alarm must ring during a sealed session —
 * including the overnight SLEEP routines — and whose calendar
 * reminder must not be silently cancelled. The old code protected
 * that user only when the alarm app's branding happened to contain
 * 'clock' or 'alarm', only at the notification door, and never for
 * calendar at all.
 */
class TimeCriticalAppsTest {

    // ---------- Finding 2: SLEEP exemption is structural ----------

    @Test
    fun sleepExempt_alarmAppBrandedWithoutClockOrAlarm_butAnswersAlarmIntents() {
        // A sleep-cycle-style alarm: neither package nor label
        // carries 'clock', 'alarm', 'phone', 'dialer' or 'maps'.
        val exempt = TimeCriticalApps.isSleepLoadoutExempt(
            packageName = "com.example.dawnrise",
            appName = "Dawnrise",
            alarmHandlers = setOf("com.example.dawnrise"),
            calendarHandlers = emptySet()
        )
        assertTrue(exempt)
    }

    @Test
    fun sleepExempt_knownAlarmPackage_byExactIdentity() {
        assertTrue(
            TimeCriticalApps.isSleepLoadoutExempt(
                packageName = "com.northpark.sleepcycle",
                appName = "Sleep Cycle",
                alarmHandlers = emptySet(),
                calendarHandlers = emptySet()
            )
        )
        assertTrue(
            TimeCriticalApps.isAlarmPackage(
                "com.google.android.deskclock", emptySet()
            )
        )
    }

    @Test
    fun sleepExempt_calendarApp_byIntentAndByIdentity() {
        assertTrue(
            TimeCriticalApps.isSleepLoadoutExempt(
                packageName = "com.example.planner",
                appName = "Planner",
                alarmHandlers = emptySet(),
                calendarHandlers = setOf("com.example.planner")
            )
        )
        assertTrue(
            TimeCriticalApps.isCalendarPackage(
                "com.google.android.calendar", emptySet()
            )
        )
    }

    @Test
    fun sleepExempt_legacyUtilitySubstrings_stillPass() {
        assertTrue(
            TimeCriticalApps.isSleepLoadoutExempt(
                packageName = "com.google.android.dialer",
                appName = "Phone",
                alarmHandlers = emptySet(),
                calendarHandlers = emptySet()
            )
        )
        assertTrue(
            TimeCriticalApps.isSleepLoadoutExempt(
                packageName = "com.google.android.apps.maps",
                appName = "Maps",
                alarmHandlers = emptySet(),
                calendarHandlers = emptySet()
            )
        )
    }

    @Test
    fun sleepLocked_ordinaryApp_noStructuralSignal_noSubstring() {
        assertFalse(
            TimeCriticalApps.isSleepLoadoutExempt(
                packageName = "com.example.socialfeed",
                appName = "Social Feed",
                alarmHandlers = setOf("com.example.dawnrise"),
                calendarHandlers = setOf("com.example.planner")
            )
        )
    }

    @Test
    fun classification_nearMissPackages_areNotMembers() {
        // Exact identity is identity: packages that merely resemble
        // a known package do not join the class, and one class's
        // membership does not leak into the other.
        assertFalse(
            TimeCriticalApps.isAlarmPackage(
                "com.google.android.deskclock.fake", emptySet()
            )
        )
        assertFalse(
            TimeCriticalApps.isCalendarPackage(
                "com.google.android.deskclock",
                calendarHandlers = emptySet()
            )
        )
        assertFalse(
            TimeCriticalApps.isAlarmPackage(
                "com.google.android.calendar",
                alarmHandlers = emptySet()
            )
        )
    }

    // ---------- Finding 1: the doors' live-alert gate ----------

    @Test
    fun gate_alarmNotificationLive_passes() {
        assertTrue(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = setOf(Notification.CATEGORY_ALARM),
                alarmPlaybackActive = false,
                isAlarmClass = false
            )
        )
    }

    @Test
    fun gate_alarmPlaybackActive_andAlarmClass_passes() {
        // The listener-independent route: an alarm is sounding and
        // the foreground package is structurally an alarm app.
        assertTrue(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = emptySet(),
                alarmPlaybackActive = true,
                isAlarmClass = true
            )
        )
    }

    @Test
    fun gate_alarmPlaybackActive_butNotAlarmClass_doesNotPass() {
        // An alarm sounding somewhere must not unlock a random
        // locked app the user wandered into.
        assertFalse(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = emptySet(),
                alarmPlaybackActive = true,
                isAlarmClass = false
            )
        )
    }

    @Test
    fun gate_nothingLive_doesNotPass() {
        assertFalse(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = emptySet(),
                alarmPlaybackActive = false,
                isAlarmClass = true
            )
        )
        assertFalse(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = setOf(Notification.CATEGORY_SOCIAL),
                alarmPlaybackActive = false,
                isAlarmClass = true
            )
        )
    }

    // ---------- Finding 3: calendar reminders join the class ----------

    @Test
    fun gate_reminderAndEventNotificationsLive_pass() {
        assertTrue(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = setOf(Notification.CATEGORY_REMINDER),
                alarmPlaybackActive = false,
                isAlarmClass = false
            )
        )
        assertTrue(
            TimeCriticalAlertGate.isAlertLiveFor(
                activeCategories = setOf(Notification.CATEGORY_EVENT),
                alarmPlaybackActive = false,
                isAlarmClass = false
            )
        )
    }

    @Test
    fun gate_alertCategorySet_isExactlyTheTimeCriticalClass() {
        assertEquals(
            setOf(
                Notification.CATEGORY_ALARM,
                Notification.CATEGORY_REMINDER,
                Notification.CATEGORY_EVENT
            ),
            TimeCriticalAlertGate.ALERT_CATEGORIES
        )
    }

    // ---------- Rebase composition on current main ----------
    // The cure was re-applied by hand onto a main that had since
    // gained #119's EssentialApps and #107's LoadoutRules: these
    // tests pin the composed surfaces, not the old inline code.

    @Test
    fun capturePolicy_reminderAndEvent_areSparedLikeAlarm() {
        // Finding 3 at the listener's one capture decision: a
        // reminder / event notification is never captured (and so
        // never stored or cancelled), exactly as an alarm is.
        assertFalse(
            com.example.service.NotificationCapturePolicy
                .shouldCaptureNotification(0, Notification.CATEGORY_REMINDER)
        )
        assertFalse(
            com.example.service.NotificationCapturePolicy
                .shouldCaptureNotification(0, Notification.CATEGORY_EVENT)
        )
        assertFalse(
            com.example.service.NotificationCapturePolicy
                .shouldCaptureNotification(0, Notification.CATEGORY_ALARM)
        )
        // An ordinary message still captures — the exemption is
        // the time-critical class only.
        assertTrue(
            com.example.service.NotificationCapturePolicy
                .shouldCaptureNotification(0, Notification.CATEGORY_MESSAGE)
        )
    }

    @Test
    fun loadoutRules_sleep_timeCriticalPackageStaysOpen() {
        // Finding 2 at the loadout's one tier rule: a package in
        // the structural time-critical set lands OPEN under SLEEP
        // even with branding free of every legacy substring, while
        // an ordinary app still LOCKs and the #119 essentials keep
        // their package-identity OPEN.
        assertEquals(
            com.example.data.AppBlockTier.OPEN,
            com.example.data.LoadoutRules.tierFor(
                com.example.data.SmartLoadout.SLEEP,
                "com.northpark.sleepcycle", "Sleep Cycle",
                "HEALTH",
                timeCriticalPackages = setOf("com.northpark.sleepcycle")
            )
        )
        assertEquals(
            com.example.data.AppBlockTier.LOCKED,
            com.example.data.LoadoutRules.tierFor(
                com.example.data.SmartLoadout.SLEEP,
                "com.northpark.sleepcycle", "Sleep Cycle", "HEALTH"
            )
        )
        assertEquals(
            com.example.data.AppBlockTier.OPEN,
            com.example.data.LoadoutRules.tierFor(
                com.example.data.SmartLoadout.SLEEP,
                "com.android.camera2", "Camera", "OTHER"
            )
        )
    }
}
