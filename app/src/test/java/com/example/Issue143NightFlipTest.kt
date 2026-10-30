package com.example

import com.example.ui.evaluateTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #143 (night-flip-mid-session): the theme evaluation's
 * finish hold. Session-night itself is gated on the shot's
 * GROUND step by the caller (MonolithViewModel's combine passes
 * sessionActive only once ShotGround.GROUNDED) — these tests pin
 * the evaluation-level contract the combine relies on:
 *  - a held finish keeps NIGHT after the session ends;
 *  - an unheld end returns to the ordinary precedence (no blast
 *    in either direction for a day finish);
 *  - an active session outranks the hold in the readout.
 */
class Issue143NightFlipTest {

    // Midday in the device zone. (Composition note, #116 × #143:
    // with the clock-night step composed into evaluateTheme, the
    // raw epoch — 00:00 UTC — is clock night, not "a plain
    // moment"; a DAY fixture must be a day hour in the zone the
    // evaluation reads. The held/session fixtures keep their
    // epoch stamp: those steps precede the clock step, so their
    // verdicts are zone-independent.)
    private fun middayMillis(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 12)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun finishHeld_keepsNightAfterSessionEnds() {
        val held = evaluateTheme(
            sessionActive = false,
            routines = emptyList(),
            nowMillis = 1_000L,
            finishHeld = true
        )
        assertTrue(held.isNight)
        assertEquals("AUTO · NIGHT — FINISH HELD", held.readout)
    }

    @Test
    fun finishNotHeld_dayStaysDay() {
        val day = evaluateTheme(
            sessionActive = false,
            routines = emptyList(),
            nowMillis = middayMillis(),
            finishHeld = false
        )
        assertFalse(day.isNight)
        assertEquals("AUTO · DAY — NO ROUTINE OWNS IT", day.readout)
    }

    @Test
    fun sessionActive_outranksFinishHoldInReadout() {
        val session = evaluateTheme(
            sessionActive = true,
            routines = emptyList(),
            nowMillis = 1_000L,
            finishHeld = true
        )
        assertTrue(session.isNight)
        assertEquals("AUTO · NIGHT — SESSION ACTIVE", session.readout)
    }
}
