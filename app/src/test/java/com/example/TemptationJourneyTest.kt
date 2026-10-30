package com.example

import com.example.service.peekPillClockText
import com.example.service.peekPillWarning
import com.example.ui.components.peekCooldownScopeText
import com.example.ui.components.peekOfferPreviewText
import com.example.ui.components.peekPriceText
import com.example.ui.screens.emergencyBreakPriceText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #114 (Audit R3, moment-of-temptation) — the pure print
 * laws behind the fixes. The composable faces themselves are
 * rendering; every WORD they print derives from these functions,
 * so the words are what get pinned here.
 *
 *  Finding 1 — the pill's clock and warning (PeekPillOverlay +
 *              PeekCountdownPill both print from these).
 *  Finding 3 — the breath's exchange and the cooldown's per-app
 *              scope, printed during the countdown.
 *  Finding 5 — the emergency break's price line under its Deck
 *              chip, for both ledger states.
 */
class TemptationJourneyTest {

    // ---- Finding 1: the pill's clock ----

    @Test
    fun pillClockPrintsMinutesAndZeroPaddedSeconds() {
        assertEquals("2:00", peekPillClockText(120))
        assertEquals("1:05", peekPillClockText(65))
        assertEquals("0:09", peekPillClockText(9))
        assertEquals("0:00", peekPillClockText(0))
    }

    @Test
    fun pillClockNeverPrintsNegativeTime() {
        assertEquals("0:00", peekPillClockText(-42))
    }

    @Test
    fun pillWarnsOnlyInsideTheLastThirtySeconds() {
        assertTrue(peekPillWarning(30))
        assertTrue(peekPillWarning(1))
        assertFalse(peekPillWarning(31))
        assertFalse(peekPillWarning(120))
    }

    // ---- Finding 3: the exchange, stated during the breath ----

    @Test
    fun breathPreviewNamesTheLeaseThatWaitsAtZero() {
        assertEquals(
            "AFTER THE BREATH: ONE TAP OPENS THIS APP FOR 2 MINUTES",
            peekOfferPreviewText(120)
        )
    }

    @Test
    fun cooldownScopeIsPrintedAsPerApp() {
        // The scope line must never imply a global cooldown the
        // machinery does not deliver.
        assertTrue(peekCooldownScopeText().contains("PER APP"))
        assertTrue(peekPriceText(20 * 60_000L).contains("ON THIS APP"))
    }

    // ---- Finding 5: the break's price, both ledger states ----

    @Test
    fun breakPriceNamesTheSessionAndTheWeeksOnlyBreak() {
        val line = emergencyBreakPriceText(1)
        assertTrue(line.contains("ENDS THIS SESSION"))
        assertTrue(line.contains("ONLY EMERGENCY BREAK"))
    }

    @Test
    fun breakPriceSaysSpentBeforeTheTapWhenSpent() {
        assertTrue(emergencyBreakPriceText(0).contains("ALREADY SPENT"))
    }
}
