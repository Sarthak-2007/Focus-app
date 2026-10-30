package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.AlarmClock
import android.provider.CalendarContract

/**
 * TIME-CRITICAL APPS (issue #121, Audit R3b "alarm-fires-mid-session"):
 * the alarm-app and calendar-app classes whose alerts must survive a
 * sealed session — an alarm that cannot be dismissed and a reminder
 * that is silently cancelled are failures of the blocker, not focus.
 *
 * The old protection was a NAME SUBSTRING: the SLEEP loadout opened
 * an app only if package+label contained 'clock'/'alarm' (etc.), so
 * an alarm app branded without those words (a sleep-cycle alarm, a
 * dawn simulator) was auto-LOCKED all night by the seeded routines,
 * and calendar — matching no substring — was LOCKED with it.
 *
 * Classification here is STRUCTURAL, never branding:
 *  1. INTENT IDENTITY — a package that answers the platform's alarm
 *     intents (AlarmClock SET_ALARM / SHOW_ALARMS / SET_TIMER /
 *     SHOW_TIMERS) IS an alarm app, whatever it calls itself; a
 *     package that declares the launcher's CATEGORY_APP_CALENDAR or
 *     answers calendar event VIEW/INSERT intents IS a calendar app.
 *     These sets are resolved from the PackageManager at use time
 *     (the manifest's <queries> block declares these intents so the
 *     resolution is visible on Android 11+).
 *  2. EXACT PACKAGE IDENTITY — the stock deskclock / calendar
 *     packages of the major OEMs and established third-party alarm
 *     apps, matched by full package name. An exact package name is
 *     an identity, not a substring: renaming the label cannot move
 *     an app in or out of the class.
 *
 * The legacy name substrings survive ONLY as an extra fallback in
 * the SLEEP decision below (an app the structural routes somehow
 * miss but which calls itself a clock keeps the protection it
 * already had) — they are no longer the only safeguard, and no
 * structural member of the class depends on them.
 *
 * This file is categorisation / lock-rule / exemption logic only.
 * It knows nothing about routines or scheduling.
 */
object TimeCriticalApps {

    /** Stock deskclocks (exact package identity) across OEMs, plus
     *  established third-party alarm apps whose branding carries
     *  neither 'clock' nor 'alarm'. */
    val KNOWN_ALARM_PACKAGES: Set<String> = setOf(
        "com.google.android.deskclock",
        "com.android.deskclock",
        "com.sec.android.app.clockpackage",
        "com.oneplus.deskclock",
        "com.oppo.deskclock",
        "com.coloros.alarmclock",
        "com.miui.clock",
        "com.xiaomi.clock",
        "com.huawei.deskclock",
        "com.asus.deskclock",
        "com.motorola.deskclock",
        "com.northpark.sleepcycle",
        "droom.sleepIfUCan"
    )

    /** Stock calendars (exact package identity) across OEMs. */
    val KNOWN_CALENDAR_PACKAGES: Set<String> = setOf(
        "com.google.android.calendar",
        "com.android.calendar",
        "com.sec.android.app.calendar",
        "com.samsung.android.calendar",
        "com.miui.calendar",
        "com.xiaomi.calendar",
        "com.huawei.calendar",
        "com.oneplus.calendar",
        "com.oppo.calendar",
        "com.coloros.calendar"
    )

    fun isAlarmPackage(packageName: String, alarmHandlers: Set<String>): Boolean =
        packageName in KNOWN_ALARM_PACKAGES || packageName in alarmHandlers

    fun isCalendarPackage(packageName: String, calendarHandlers: Set<String>): Boolean =
        packageName in KNOWN_CALENDAR_PACKAGES || packageName in calendarHandlers

    fun isTimeCriticalPackage(
        packageName: String,
        alarmHandlers: Set<String>,
        calendarHandlers: Set<String>
    ): Boolean =
        isAlarmPackage(packageName, alarmHandlers) ||
            isCalendarPackage(packageName, calendarHandlers)

    /**
     * The SLEEP loadout's exemption decision (pure): an app stays
     * OPEN under SLEEP when it is structurally time-critical
     * (alarm/calendar class), or when it matches the loadout's
     * long-standing utility substrings (dialer / phone / clock /
     * alarm / maps). Everything else is LOCKED — the exemption is
     * exactly this class and those utilities, nothing wider.
     */
    fun isSleepLoadoutExempt(
        packageName: String,
        appName: String,
        alarmHandlers: Set<String>,
        calendarHandlers: Set<String>
    ): Boolean {
        if (isTimeCriticalPackage(packageName, alarmHandlers, calendarHandlers)) return true
        val lower = "$packageName $appName".lowercase()
        return lower.contains("dialer") || lower.contains("phone") ||
            lower.contains("clock") || lower.contains("alarm") ||
            lower.contains("maps")
    }

    /** Packages answering the platform alarm intents — alarm apps
     *  by capability, independent of branding. */
    fun resolveAlarmHandlerPackages(pm: PackageManager): Set<String> {
        val actions = listOf(
            AlarmClock.ACTION_SET_ALARM,
            AlarmClock.ACTION_SHOW_ALARMS,
            AlarmClock.ACTION_SET_TIMER,
            AlarmClock.ACTION_SHOW_TIMERS
        )
        val out = mutableSetOf<String>()
        for (action in actions) {
            try {
                pm.queryIntentActivities(Intent(action), 0)
                    .mapNotNullTo(out) { it.activityInfo?.packageName }
            } catch (_: Exception) {
                // A failed query narrows nothing else: the exact
                // identity sets and the other actions still stand.
            }
        }
        return out
    }

    /** Packages that ARE calendar apps by declaration: the
     *  launcher's calendar category, or handling calendar event
     *  VIEW / INSERT intents. */
    fun resolveCalendarHandlerPackages(pm: PackageManager): Set<String> {
        val out = mutableSetOf<String>()
        val intents = listOf(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR),
            Intent(Intent.ACTION_VIEW, CalendarContract.Events.CONTENT_URI),
            Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
        )
        for (intent in intents) {
            try {
                pm.queryIntentActivities(intent, 0)
                    .mapNotNullTo(out) { it.activityInfo?.packageName }
            } catch (_: Exception) {
                // As above: one failed query must not widen or
                // narrow the rest of the classification.
            }
        }
        return out
    }

    /** Both structural handler sets for [context], resolved once. */
    fun resolveHandlerSets(context: Context): Pair<Set<String>, Set<String>> {
        val pm = context.packageManager
        return resolveAlarmHandlerPackages(pm) to resolveCalendarHandlerPackages(pm)
    }

    /**
     * The subset of [candidates] that is structurally time-critical,
     * with the handler sets resolved once for the whole call. This
     * is the one set every SLEEP consumer shares — the repository's
     * commit path (MonolithRepository.applySmartLoadout), the Shield
     * preview and the ratchet warning (LoadoutRules, issue #107's
     * preview-cannot-diverge law) — so all three judge the same apps
     * exempt.
     */
    fun resolveTimeCriticalPackages(
        context: Context,
        candidates: Collection<String>
    ): Set<String> {
        val (alarmHandlers, calendarHandlers) = resolveHandlerSets(context)
        return candidates.filterTo(mutableSetOf()) {
            isTimeCriticalPackage(it, alarmHandlers, calendarHandlers)
        }
    }
}
