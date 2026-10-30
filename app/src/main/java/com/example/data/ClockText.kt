package com.example.data

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * #88 (Audit R2, l10n): the app's clocks speak the DEVICE's language —
 * the system 12/24-hour setting and the device locale — never a pinned
 * Locale.US "HH:mm". Before this object existed, every clock in the
 * app printed raw 24-hour times to 12-hour users, with no AM/PM.
 *
 * DISPLAY formatting only. Machine formats (ISO dates, stored stamps
 * that other code parses back) stay pinned Locale.US at their own
 * sites, on purpose — they are not clocks, they are data.
 */
object ClockText {

    /** Time of day per the system setting, e.g. "17:30" or "5:30 PM". */
    fun timeOfDay(context: Context, wallMillis: Long): String =
        DateFormat.getTimeFormat(context).format(Date(wallMillis))

    /**
     * The pure core of [timeOfDay]: the caller supplies the device's
     * 12/24-hour choice (read via [is24Hour] at any Android edge).
     * Pure functions — the seal's print helpers — take this boolean
     * instead of a Context, so their JVM tests can pin BOTH branches.
     */
    fun timeOfDay(is24Hour: Boolean, wallMillis: Long): String {
        val pattern = if (is24Hour) "HH:mm" else "h:mm a"
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(wallMillis))
    }

    /** True when the device is set to 24-hour time. */
    fun is24Hour(context: Context): Boolean = DateFormat.is24HourFormat(context)

    /** "d MMM · <time>" for a past day, the time per the system setting. */
    fun dayAndTime(context: Context, wallMillis: Long): String =
        dayAndTime(is24Hour(context), wallMillis)

    /** Pure core of [dayAndTime] — see [timeOfDay]'s boolean overload. */
    fun dayAndTime(is24Hour: Boolean, wallMillis: Long): String {
        val day = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(wallMillis))
        return "$day · ${timeOfDay(is24Hour, wallMillis)}"
    }
}
