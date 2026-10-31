package com.example

import com.example.service.PillCeremony
import com.example.service.breakPillLabelText
import com.example.ui.components.peekOfferPreviewText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #102 — retained W9 DAYLIGHT break 2 (the pill is half
 * his idea), plus one print rider on retained break 1.
 *
 * Break 1's cure (the peek-length writer) landed on main via
 * issue #517 / PR #714 while this lane was building — its suite
 * is PeekMinutesSetterTest, and this file deliberately does NOT
 * duplicate it. What remains here is what #517 never touched:
 *
 *  - the break pill's label law (BreakPillOverlay — the lived
 *    break face this PR adds; the overlay windows themselves
 *    are plain-view WindowManager faces driven by the
 *    ViewModel's activeSession collector),
 *  - the shared pill ceremony spec (PillCeremony — the fade +
 *    settle both overlays and the in-app pill now perform,
 *    where before every face snapped in like a system toast),
 *  - the offer preview's singular at the SET length of one
 *    minute — a print #517's writer made reachable ("1 MINUTES"
 *    was the standing text at peekMinutes = 1); at the default
 *    120s the line is byte-identical, as TemptationJourneyTest
 *    pins.
 *
 * Retained break 3 (the Shield book) was cured on main by
 * PR #388 — struck with evidence on the issue, no code here.
 *
 * PIN scope: SOURCE — pure-function truth over
 * breakPillLabelText, PillCeremony.enterStart / exitEnd and the
 * ceremony constants, and peekOfferPreviewText; no Android
 * runtime, no render, no post, no plumbing.
 */
class Issue102RetainedTest {

    @Test
    fun breakPillLabelWarnsOnlyInTheLastLiveSeconds() {
        assertEquals("BREAK", breakPillLabelText(600))
        assertEquals("BREAK", breakPillLabelText(31))
        assertEquals("BREAK · ENDING SOON", breakPillLabelText(30))
        assertEquals("BREAK · ENDING SOON", breakPillLabelText(1))
        // Zero is not a live second — the same law as the peek
        // pill's warning (peekPillWarning): a dead break wears
        // no warning.
        assertEquals("BREAK", breakPillLabelText(0))
    }

    @Test
    fun ceremonySpecIsABreathNotAPerformance() {
        assertTrue(PillCeremony.ENTER_DURATION_MS in 1L..400L)
        assertTrue(PillCeremony.EXIT_DURATION_MS in 1L..PillCeremony.ENTER_DURATION_MS)
        val (enterAlpha, enterOffset) = PillCeremony.enterStart(2f)
        assertEquals(0f, enterAlpha, 0.001f)
        assertEquals(-24f, enterOffset, 0.001f)
        val (exitAlpha, exitOffset) = PillCeremony.exitEnd(2f)
        assertEquals(0f, exitAlpha, 0.001f)
        assertEquals(-16f, exitOffset, 0.001f)
        assertEquals(1f, PillCeremony.REST_ALPHA, 0.001f)
    }

    @Test
    fun offerPreviewNamesOneMinuteInTheSingular() {
        assertEquals(
            "AFTER THE BREATH: ONE TAP OPENS THIS APP FOR 1 MINUTE",
            peekOfferPreviewText(60)
        )
        assertEquals(
            "AFTER THE BREATH: ONE TAP OPENS THIS APP FOR 2 MINUTES",
            peekOfferPreviewText(120)
        )
        assertEquals(
            "AFTER THE BREATH: ONE TAP OPENS THIS APP FOR 5 MINUTES",
            peekOfferPreviewText(300)
        )
    }
}
