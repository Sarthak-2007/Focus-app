package com.example.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager

/**
 * Issue #122 (Audit R3b, low-battery-mid-session): the app was
 * battery-blind — it never sensed level, saver state or charging,
 * and its one BATTERY verdict measured only the setup-time Doze
 * exemption. This file is the one battery read every surface
 * shares: the shield's notification, PARK's board, the desk
 * clock's keep-screen-on cutoff and the ViewModel's sweep all
 * derive from this snapshot and these pure derivations, so the
 * app can only ever tell one battery story.
 *
 * TELL-THE-TRUTH: an unknown level stays unknown (null) — no
 * derivation below invents a percentage, and a surface that has
 * no reading prints no battery claim.
 */

/** Level at or below which a session is at risk (percent). */
const val LOW_BATTERY_PERCENT = 20

/** Level at or below which death is imminent (percent). */
const val CRITICAL_BATTERY_PERCENT = 10

/** One battery reading: level, saver, charging, and the setup
 *  exemption the old BATTERY line mistook for the whole story.
 *  [levelPercent] and [isCharging] are null when the platform
 *  does not say — unknown, never guessed. */
data class BatterySnapshot(
    val levelPercent: Int?,
    val isPowerSaveMode: Boolean,
    val isCharging: Boolean?,
    val exemptionIgnored: Boolean
)

/** How much danger this battery is in for a running session.
 *  Charging suppresses every risk: a plugged-in phone at 8% is
 *  recovering, not dying — warning there would be a false alarm
 *  that trains him to ignore the real one. */
enum class BatteryRisk { NONE, SAVER_ON, LOW, CRITICAL }

fun batteryRisk(snapshot: BatterySnapshot): BatteryRisk {
    if (snapshot.isCharging == true) return BatteryRisk.NONE
    val level = snapshot.levelPercent
    return when {
        level != null && level <= CRITICAL_BATTERY_PERCENT -> BatteryRisk.CRITICAL
        level != null && level <= LOW_BATTERY_PERCENT -> BatteryRisk.LOW
        snapshot.isPowerSaveMode -> BatteryRisk.SAVER_ON
        else -> BatteryRisk.NONE
    }
}

/**
 * The shield notification's warning segment, or null when the
 * battery can keep the promise the shield is making. The words
 * state the mechanism plainly: protection ends with the phone.
 */
fun batteryShieldWarning(snapshot: BatterySnapshot): String? {
    val level = snapshot.levelPercent
    return when (batteryRisk(snapshot)) {
        BatteryRisk.NONE -> null
        BatteryRisk.CRITICAL ->
            "BATTERY ${level}% — PLUG IN NOW: PROTECTION ENDS IF THIS PHONE DIES"
        BatteryRisk.LOW ->
            "BATTERY ${level}% — PLUG IN: PROTECTION ENDS IF THIS PHONE DIES"
        BatteryRisk.SAVER_ON ->
            (if (level != null) "BATTERY $level% · SAVER ON" else "BATTERY SAVER ON") +
                " — PROTECTION ENDS IF THIS PHONE DIES"
    }
}

/**
 * PARK's BATTERY board word: the battery itself, not the setup
 * exemption alone. The level leads; saver and charging are
 * named; and a missing exemption is named too — the first draft
 * of this function dropped NOT EXEMPT whenever a level existed,
 * which would have hidden the one lack the FIX door can repair
 * behind a healthy-looking "85%". Every fact the snapshot holds
 * is printed; the exemption is never again the whole story, and
 * it is never silently dropped from it either.
 */
fun batteryBoardWord(snapshot: BatterySnapshot): String {
    val parts = mutableListOf<String>()
    snapshot.levelPercent?.let { parts.add("$it%") }
    when {
        snapshot.isCharging == true -> parts.add("CHARGING")
        snapshot.isPowerSaveMode -> parts.add("SAVER ON")
    }
    if (!snapshot.exemptionIgnored) parts.add("NOT EXEMPT")
    return if (parts.isEmpty()) "EXEMPT" else parts.joinToString(" · ")
}

/** Whether the board word is a lack (prints in the lack ink):
 *  any live battery risk, or the setup exemption missing. */
fun batteryBoardIsLack(snapshot: BatterySnapshot): Boolean =
    batteryRisk(snapshot) != BatteryRisk.NONE || !snapshot.exemptionIgnored

/**
 * The desk clock's keep-screen-on law: the always-on display may
 * hold the screen only while the battery is in no risk. At 8%
 * (or with saver on) holding the screen on is the fastest route
 * to the death the shield never survives — so the flag is never
 * added, the system timeout governs, and the clock prints why.
 */
fun deskClockKeepsScreenOn(snapshot: BatterySnapshot): Boolean =
    batteryRisk(snapshot) == BatteryRisk.NONE

/** The desk clock's printed reason when it refuses to hold the
 *  screen on, or null when it may. The clock never goes dark
 *  silently: the refusal and its cause print on the clock face. */
fun deskClockBatteryNotice(snapshot: BatterySnapshot): String? {
    val level = snapshot.levelPercent
    return when (batteryRisk(snapshot)) {
        BatteryRisk.NONE -> null
        BatteryRisk.CRITICAL ->
            "BATTERY ${level}% — THIS CLOCK WILL NOT HOLD THE SCREEN ON · PLUG IN NOW"
        BatteryRisk.LOW ->
            "BATTERY ${level}% — THIS CLOCK WILL NOT HOLD THE SCREEN ON · PLUG IN"
        BatteryRisk.SAVER_ON ->
            (if (level != null) "BATTERY $level% · SAVER ON" else "BATTERY SAVER ON") +
                " — THIS CLOCK WILL NOT HOLD THE SCREEN ON"
    }
}

/**
 * The platform read. Level comes from BatteryManager's capacity
 * property, falling back to the sticky BATTERY_CHANGED intent's
 * level/scale; charging from the same intent's status; saver
 * and the exemption from PowerManager. Every failure degrades
 * to unknown/false — a failed read never fabricates a level.
 */
fun readBatterySnapshot(context: Context): BatterySnapshot {
    val appContext = context.applicationContext
    var level: Int? = null
    var charging: Boolean? = null
    try {
        val bm = appContext.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val capacity = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (capacity != null && capacity in 0..100) level = capacity
        val sticky = appContext.registerReceiver(
            null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        if (sticky != null) {
            if (level == null) {
                val raw = sticky.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = sticky.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (raw >= 0 && scale > 0) level = raw * 100 / scale
            }
            val status = sticky.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            if (status != -1) {
                charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            }
        }
    } catch (_: Exception) {
        // Unknown stays unknown.
    }
    var saver = false
    var exempt = false
    try {
        val pm = appContext.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (pm != null) {
            saver = pm.isPowerSaveMode
            exempt = pm.isIgnoringBatteryOptimizations(appContext.packageName)
        }
    } catch (_: Exception) {
        // As above.
    }
    return BatterySnapshot(
        levelPercent = level,
        isPowerSaveMode = saver,
        isCharging = charging,
        exemptionIgnored = exempt
    )
}
