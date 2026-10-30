package com.example

import com.example.ui.components.deskClockWallTimeText
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

/**
 * WELLE-5 / #253-10: the desk clock tells the time of day.
 * The helper formats in the device zone (SimpleDateFormat's
 * default), so the test pins the zone to UTC and restores it.
 */
class DeskClockWallTimeTest {

    private var originalZone: TimeZone? = null

    @Before
    fun pinUtc() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreZone() {
        originalZone?.let { TimeZone.setDefault(it) }
    }

    @Test
    fun epochMidnightPrintsZeroZero() {
        assertEquals("00:00", deskClockWallTimeText(0L))
    }

    @Test
    fun afternoonPrints24Hour() {
        val millis = (13L * 3600L + 5L * 60L) * 1000L
        assertEquals("13:05", deskClockWallTimeText(millis))
    }

    @Test
    fun minutesAreZeroPadded() {
        val millis = (3L * 3600L + 7L * 60L) * 1000L
        assertEquals("03:07", deskClockWallTimeText(millis))
    }
}
