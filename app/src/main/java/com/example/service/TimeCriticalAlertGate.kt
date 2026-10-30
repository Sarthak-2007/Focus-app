package com.example.service

import android.app.Notification
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import com.example.data.TimeCriticalApps

/**
 * TIME-CRITICAL ALERT GATE (issue #121, Audit R3b
 * "alarm-fires-mid-session"): the check BOTH screen doors
 * (MonolithAccessibilityService, MonolithShieldService via the
 * repository) run before covering a foreground app, so a ringing
 * alarm — or a calendar reminder the user just tapped — is never
 * buried under the Airlock.
 *
 * The old doors could not see an alarm at all: their only bypass
 * was the call check (AudioManager MODE_RINGTONE / MODE_IN_CALL /
 * MODE_IN_COMMUNICATION), and a ringing alarm sets none of those
 * modes — it plays on the ALARM stream. The exemption lived only
 * in the notification listener, the one door that does not control
 * the screen.
 *
 * A package passes this gate only while its alert is LIVE, through
 * either of two independent proofs:
 *  1. NOTIFICATION PROOF — the bound notification listener holds an
 *     active notification from this package in a time-critical
 *     category (ALARM for a firing alarm; REMINDER / EVENT for a
 *     calendar reminder). See
 *     [MonolithNotificationListener.activeAlertCategoriesFor].
 *  2. PLAYBACK PROOF (alarm class only) — an audio playback with
 *     USAGE_ALARM is active right now AND this package is
 *     structurally an alarm app ([TimeCriticalApps]): an alarm is
 *     sounding and the foreground app is of the class that sounds
 *     alarms. This proof needs no listener binding.
 *
 * The gate is deliberately narrow: it names a package, not a class
 * of traffic — a locked alarm app is exempt only while its alarm
 * rings (notification live or alarm-stream playback active), a
 * locked calendar only while its reminder/event notification is
 * live. The moment the alert ends, the ordinary tier rules apply
 * again in full. Nothing else about blocking changes.
 */
object TimeCriticalAlertGate {

    /** Notification categories that mark a live time-critical
     *  alert from a package. */
    val ALERT_CATEGORIES: Set<String> = setOf(
        Notification.CATEGORY_ALARM,
        Notification.CATEGORY_REMINDER,
        Notification.CATEGORY_EVENT
    )

    /**
     * Pure decision, unit-tested: does [packageName] hold a live
     * time-critical alert?
     * - [activeCategories] are the categories of the package's
     *   currently active notifications (empty when the listener is
     *   unbound or holds none for it);
     * - [alarmPlaybackActive] is true while any USAGE_ALARM
     *   playback is sounding;
     * - [isAlarmClass] is the structural alarm classification.
     */
    fun isAlertLiveFor(
        activeCategories: Set<String>,
        alarmPlaybackActive: Boolean,
        isAlarmClass: Boolean
    ): Boolean {
        if (activeCategories.any { it in ALERT_CATEGORIES }) return true
        return alarmPlaybackActive && isAlarmClass
    }

    /** True while any audio playback on the alarm usage is active
     *  (API 26+; false below, where the notification proof is the
     *  available route). */
    fun isAlarmPlaybackActive(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return false
            am.activePlaybackConfigurations.any { config ->
                config.audioAttributes?.usage == AudioAttributes.USAGE_ALARM
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * The doors' one call: is [packageName]'s time-critical alert
     * live right now? Combines the listener's notification proof
     * with the alarm-playback proof, classifying the package
     * structurally when the playback route is needed.
     */
    fun isAlertLiveFor(context: Context, packageName: String): Boolean {
        val categories = MonolithNotificationListener
            .activeAlertCategoriesFor(packageName)
        val playback = isAlarmPlaybackActive(context)
        val isAlarmClass = if (playback) {
            val (alarmHandlers, _) = TimeCriticalApps.resolveHandlerSets(context)
            TimeCriticalApps.isAlarmPackage(packageName, alarmHandlers)
        } else {
            false
        }
        return isAlertLiveFor(
            activeCategories = categories,
            alarmPlaybackActive = playback,
            isAlarmClass = isAlarmClass
        )
    }
}
