package com.example

import com.example.service.NightSignal
import com.example.ui.components.DESK_CLOCK_BAR_ALPHA
import com.example.ui.components.DESK_CLOCK_SCREEN_BRIGHTNESS
import com.example.ui.components.DESK_CLOCK_TIME_ALPHA
import com.example.ui.evaluateTheme
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R3b · night-half-asleep (issue #116): the one night
 * signal and everything that reads it.
 */
class NightSignalTest {

    private fun at(hour: Int, minute: Int = 0): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun clockNight_windowBoundaries() {
        assertTrue(NightSignal.isClockNightHour(20))
        assertTrue(NightSignal.isClockNightHour(23))
        assertTrue(NightSignal.isClockNightHour(0))
        // The audit's case: 04:00, SLEEP window (20:00–03:30) over.
        assertTrue(NightSignal.isClockNightHour(4))
        assertTrue(NightSignal.isClockNightHour(5))
        assertFalse(NightSignal.isClockNightHour(6))
        assertFalse(NightSignal.isClockNightHour(12))
        assertFalse(NightSignal.isClockNightHour(19))
    }

    @Test
    fun clockNight_isNightAtUsesLocalHour() {
        val utc = TimeZone.getTimeZone("UTC")
        assertTrue(NightSignal.isNightAt(at(2, 0), utc))
        assertFalse(NightSignal.isNightAt(at(12, 0), utc))
    }

    @Test
    fun theme_sessionOrSleepEndingAtNight_holdsNight() {
        // A session ending at 01:00 and a SLEEP window ending at
        // 03:30 both land on clock night, never on the light
        // scheme: hold-till-morning (finding 2). Asserted through
        // the shared signal with an explicit zone (zone-proof);
        // the evaluation's clock step reads this same signal.
        assertTrue(NightSignal.isNightAt(at(1, 0), TimeZone.getTimeZone("UTC")))
        assertTrue(NightSignal.isNightAt(at(3, 30), TimeZone.getTimeZone("UTC")))
        assertFalse(NightSignal.isNightAt(at(12, 0), TimeZone.getTimeZone("UTC")))
        // The evaluation itself: with the system witness on, any
        // hour is night — the system step precedes the clock step.
        val systemOwned = evaluateTheme(false, emptyList(), at(12, 0), systemNight = true)
        assertTrue(systemOwned.isNight)
        assertEquals("AUTO · NIGHT — SYSTEM DARK MODE", systemOwned.readout)
    }

    @Test
    fun gainScale_matrix() {
        // Day: everything full gain, exactly as shipped.
        assertEquals(1f, NightSignal.gainScale(night = false, doNotDisturb = false, sealVoice = false), 0.001f)
        assertEquals(1f, NightSignal.gainScale(night = false, doNotDisturb = false, sealVoice = true), 0.001f)
        // Night: ordinary sounds lowered, seal softened not deleted.
        assertEquals(0.40f, NightSignal.gainScale(night = true, doNotDisturb = false, sealVoice = false), 0.001f)
        assertEquals(0.65f, NightSignal.gainScale(night = true, doNotDisturb = false, sealVoice = true), 0.001f)
        // DND (any hour): ordinary sounds silenced, seal still speaks.
        assertEquals(0f, NightSignal.gainScale(night = false, doNotDisturb = true, sealVoice = false), 0.001f)
        assertEquals(0.35f, NightSignal.gainScale(night = false, doNotDisturb = true, sealVoice = true), 0.001f)
        assertEquals(0f, NightSignal.gainScale(night = true, doNotDisturb = true, sealVoice = false), 0.001f)
        assertEquals(0.35f, NightSignal.gainScale(night = true, doNotDisturb = true, sealVoice = true), 0.001f)
    }

    @Test
    fun deskClock_nightLuminanceConstants() {
        // The kept nightstand clock, dimmed: 5% window brightness,
        // time and bar below full alpha (finding 3).
        assertEquals(0.05f, DESK_CLOCK_SCREEN_BRIGHTNESS, 0.001f)
        assertTrue(DESK_CLOCK_TIME_ALPHA < 1f)
        assertTrue(DESK_CLOCK_BAR_ALPHA < DESK_CLOCK_TIME_ALPHA)
    }
}
