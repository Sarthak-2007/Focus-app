package com.example.service

import java.util.Calendar
import java.util.TimeZone

/**
 * THE ONE NIGHT SIGNAL (Audit R3b, night-half-asleep, issue #116).
 *
 * Until this file, "night" in Monolith meant only "a session or a
 * SLEEP routine owns this moment" — so a half-asleep user at 04:00
 * with SLEEP paused got the full light theme, full-brightness
 * nightstand clock, a vertigo seal shot and full-gain sounds. Every
 * night-sensitive surface now reads THIS one derivation, so the
 * theme, the desk clock, the seal shot and the sound engine can
 * never disagree about whether it is night.
 *
 * The window is the user's own sleep window: he sleeps at 20:00
 * and wakes at 03:30, and the seeded SLEEP routine is 20:00–03:30.
 * Clock night runs 20:00–06:00 so the hours after waking (still
 * dark, still groggy) stay night too — a session or SLEEP window
 * ending at 01:00 or 03:30 lands on clock night, never on the
 * light scheme. Night therefore only ever ENDS at 06:00, in the
 * morning, at the moment the user is long awake: hold-till-morning
 * by construction.
 */
object NightSignal {

    const val NIGHT_START_HOUR = 20
    const val NIGHT_END_HOUR = 6

    /** Pure: is this local hour inside clock night? */
    fun isClockNightHour(hourOfDay: Int): Boolean =
        hourOfDay >= NIGHT_START_HOUR || hourOfDay < NIGHT_END_HOUR

    /** Clock night at an instant, in the device's local zone. */
    fun isNightAt(nowMillis: Long, zone: TimeZone = TimeZone.getDefault()): Boolean {
        val cal = Calendar.getInstance(zone)
        cal.timeInMillis = nowMillis
        return isClockNightHour(cal.get(Calendar.HOUR_OF_DAY))
    }

    /**
     * The night gain law for the sound engine (finding 5):
     *  - Do Not Disturb (interruption filter is not ALL): ordinary
     *    clicks/knocks/detents are silenced outright; the seal
     *    voice is only ever SOFTENED, never deleted — it is a
     *    proven, loved mechanic, so it still speaks, at 0.35.
     *  - Clock night without DND: ordinary sounds at 0.40 of their
     *    daytime gain, the seal at 0.65.
     *  - Day: full gain, exactly as shipped.
     * Pure, so the matrix is unit-tested cell by cell.
     */
    fun gainScale(night: Boolean, doNotDisturb: Boolean, sealVoice: Boolean): Float = when {
        doNotDisturb -> if (sealVoice) 0.35f else 0f
        night -> if (sealVoice) 0.65f else 0.40f
        else -> 1f
    }
}
