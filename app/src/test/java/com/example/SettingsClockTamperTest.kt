package com.example

import com.example.service.MonolithAccessibilityService
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #296 (Audit R5 supplement — ScreenZen, second-pass F2):
 * the Date & time settings page is a clock-tamper surface during a
 * session. ScreenZen guards it explicitly (info_lock_time_change);
 * Monolith's Settings guard previously required the page to mention
 * Monolith, which the clock page never does, so the guard was blind
 * to it. The cure keys on the page's own controls (the markers in
 * [MonolithAccessibilityService.isDateTimeSettingsSurface]).
 *
 * The window texts below are lowercased full-window dumps in the
 * shape collectWindowTextLowercase produces (every node's text and
 * content description, space-joined).
 */
class SettingsClockTamperTest {

    @Test
    fun `pixel date and time page is a tamper surface`() {
        val dump = "date & time use network-provided time " +
            "use network-provided time zone select time zone " +
            "use locale default use 24-hour format"
        assertTrue(MonolithAccessibilityService.isDateTimeSettingsSurface(dump))
    }

    @Test
    fun `aosp date and time page is a tamper surface`() {
        val dump = "date & time automatic date & time " +
            "automatic time zone set date set time select time zone"
        assertTrue(MonolithAccessibilityService.isDateTimeSettingsSurface(dump))
    }

    @Test
    fun `settings index only lists the row and stays open`() {
        // The Settings home screen names "Date & time" as one row
        // among many but carries none of the page's own controls —
        // opening Settings itself must NOT intercept.
        val dump = "settings network & internet connected devices " +
            "apps notifications battery storage sound & vibration " +
            "display security & privacy location safety & emergency " +
            "passwords & accounts digital wellbeing date & time " +
            "system about phone"
        assertFalse(MonolithAccessibilityService.isDateTimeSettingsSurface(dump))
    }

    @Test
    fun `monolith app info page is not a clock surface`() {
        val dump = "app info monolith notifications permissions " +
            "force stop uninstall clear data clear cache device admin"
        assertFalse(MonolithAccessibilityService.isDateTimeSettingsSurface(dump))
    }

    @Test
    fun `empty window text is not a clock surface`() {
        assertFalse(MonolithAccessibilityService.isDateTimeSettingsSurface(""))
    }
}
