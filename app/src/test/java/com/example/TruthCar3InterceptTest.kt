package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.service.InterceptPath
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.service.WatchStore
import com.example.ui.components.airlockSessionLine
import com.example.ui.components.deskClockBannerText
import com.example.ui.components.endsAtText
import com.example.ui.components.peekBadgeText
import com.example.ui.components.peekCooldownPrint
import com.example.ui.components.peekPriceText
import com.example.ui.components.refusalSentenceFor
import com.example.ui.components.seamMarksFor
import com.example.ui.peekOfferFor
import com.example.ui.peekSecondsRemaining
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Truth Car 3 — Suki's Deck/intercept slice. Every charge in Una's
 * UNA2 audit gets a discriminating test here: the test fails on the
 * pre-cure code's behaviour and passes on the cure.
 *
 *  UNA2-01 — the peek offer is per-path (peekOfferFor's matrix).
 *  UNA2-02 — the split tenant (asserted in SliceDGestureAndStripTest).
 *  UNA2-03 — the warrant's read instant: age, staleness, print state,
 *            the stale seam mark, the withheld sentence.
 *  Appendix — the desk clock's banner (no fabricated name) and the
 *            withheld peek's cooldown print.
 */
class TruthCar3InterceptTest {

    private fun app(tier: AppBlockTier) = BlockedAppEntity(
        packageName = "com.example.app",
        appName = "App",
        category = "Social",
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    // -----------------------------------------------------------------
    // UNA2-01 — the offer exists only where a lease can be honoured.
    // -----------------------------------------------------------------

    @Test
    fun peekOfferHoldsOnlyOnHonouringDoors() {
        val peekApp = app(AppBlockTier.PEEK_2M)
        // The two doors whose blockers read the lease.
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, peekApp, 0, false))
        assertTrue(peekOfferFor(InterceptPath.SHIELD_APP, peekApp, 0, false))
        // The sniper, the site blocker, the uninstall guard and an
        // unidentified door never sell a peek — on the guard's door
        // the old code defaulted the offer ON for an unlisted app.
        assertFalse(peekOfferFor(InterceptPath.SNIPER, peekApp, 0, false))
        assertFalse(peekOfferFor(InterceptPath.SITE, peekApp, 0, false))
        assertFalse(peekOfferFor(InterceptPath.UNINSTALL_GUARD, peekApp, 0, false))
        assertFalse(peekOfferFor(InterceptPath.UNKNOWN, peekApp, 0, false))
    }

    @Test
    fun peekOfferNeverDefaultsOnForAnUnlistedPackage() {
        // The uninstall guard's whole caseload: no row, no offer —
        // on ANY door, including an honouring one.
        for (path in InterceptPath.values()) {
            assertFalse(peekOfferFor(path, null, 0, false))
        }
    }

    @Test
    fun peekOfferNeedsThePeekTierAClearCooldownAndNoRunningPill() {
        val peekApp = app(AppBlockTier.PEEK_2M)
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, app(AppBlockTier.LOCKED), 0, false))
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, app(AppBlockTier.OPEN), 0, false))
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, peekApp, 1, false))
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, peekApp, 900, false))
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, peekApp, 0, true))
        assertFalse(peekOfferFor(InterceptPath.SHIELD_APP, peekApp, 0, true))
    }

    // -----------------------------------------------------------------
    // UNA2-03 — the warrant carries its read instant.
    // -----------------------------------------------------------------

    private fun warrant(
        state: WarrantState,
        readMono: Long,
        bound: Boolean = true,
        stamped: Boolean = true,
        usage: UsageAccess = UsageAccess.ALLOWED
    ) = Warrant(
        state = state,
        a11yBound = bound,
        a11yStampedThisResidency = stamped,
        shieldProtecting = false,
        usage = usage,
        readMono = readMono
    )

    @Test
    fun warrantAgeIsMeasuredFromTheReadInstant() {
        val w = warrant(WarrantState.LIVE, readMono = 100_000L)
        assertEquals(10_000L, WatchStore.warrantAgeMs(w, 110_000L))
        assertEquals(46_000L, WatchStore.warrantAgeMs(w, 146_000L))
        // A warrant built by hand carries no instant: age unknown,
        // never age zero.
        assertNull(WatchStore.warrantAgeMs(warrant(WarrantState.LIVE, readMono = 0L), 110_000L))
        assertNull(WatchStore.warrantAgeMs(w, 0L))
    }

    @Test
    fun stalenessTurnsAtTheBoardsOwnThreshold() {
        val w = warrant(WarrantState.LIVE, readMono = 100_000L)
        assertFalse(WatchStore.warrantStaleForPrint(w, 110_000L))
        assertFalse(WatchStore.warrantStaleForPrint(w, 145_000L))
        assertTrue(WatchStore.warrantStaleForPrint(w, 145_001L))
        assertTrue(WatchStore.warrantStaleForPrint(w, 10_000_000L))
        // Age-unknown never reads stale — it degrades nothing.
        assertFalse(
            WatchStore.warrantStaleForPrint(warrant(WarrantState.LIVE, readMono = 0L), 10_000_000L)
        )
    }

    @Test
    fun printStateDemotesOnlyAStaleLive() {
        val fresh = warrant(WarrantState.LIVE, readMono = 100_000L)
        val stale = warrant(WarrantState.LIVE, readMono = 100_000L)
        assertEquals(WarrantState.LIVE, WatchStore.printState(fresh, 110_000L))
        assertEquals(WarrantState.UNPROVEN, WatchStore.printState(stale, 200_000L))
        // OFF and UNPROVEN claim nothing a stale read could inflate.
        assertEquals(
            WarrantState.OFF,
            WatchStore.printState(warrant(WarrantState.OFF, readMono = 100_000L, bound = false), 200_000L)
        )
        assertEquals(
            WarrantState.UNPROVEN,
            WatchStore.printState(warrant(WarrantState.UNPROVEN, readMono = 100_000L), 200_000L)
        )
        assertNull(WatchStore.printState(null, 200_000L))
    }

    @Test
    fun staleSeamAdmitsItsAgeInsteadOfClaimingLiveProof() {
        val staleLive = warrant(WarrantState.LIVE, readMono = 100_000L)
        assertEquals(
            "BIND HELD · THIS VISIT" to "PROOF: STALE",
            seamMarksFor(staleLive, 200_000L)
        )
        // Fresh reads delegate to the base marks untouched.
        assertEquals(
            "BIND HELD · THIS VISIT" to "PROOF: LIVE BIND",
            seamMarksFor(staleLive, 110_000L)
        )
        // The residency facts on the left do not age.
        assertEquals(
            "BIND NOT HELD" to "PROOF: STALE",
            seamMarksFor(warrant(WarrantState.OFF, readMono = 100_000L, bound = false), 200_000L)
        )
        assertEquals(
            "BIND HELD · UNSEEN" to "PROOF: STALE",
            seamMarksFor(warrant(WarrantState.UNPROVEN, readMono = 100_000L, stamped = false), 200_000L)
        )
        assertNull(seamMarksFor(null, 200_000L))
    }

    @Test
    fun staleWarrantSpeaksNoRefusalSentence() {
        // A fix may only be demanded off a current read.
        val dead = warrant(WarrantState.OFF, readMono = 100_000L, bound = false)
        assertEquals(
            "BLOCKING IS OFF — TAP TO FIX",
            refusalSentenceFor(dead, 110_000L)?.text
        )
        assertNull(refusalSentenceFor(dead, 200_000L))
        assertNull(refusalSentenceFor(null, 200_000L))
    }

    @Test
    fun deriveWarrantStampsTheReadInstantItWasGiven() {
        val w = WatchStore.deriveWarrant(
            a11yBound = true, stamped = true,
            usage = UsageAccess.ALLOWED, shieldProtecting = false,
            readMono = 424_242L
        )
        assertEquals(424_242L, w.readMono)
        assertEquals(WarrantState.LIVE, w.state)
    }

    // -----------------------------------------------------------------
    // Appendix — the desk clock's banner and the cooldown print.
    // -----------------------------------------------------------------

    @Test
    fun deskClockBannerNeverFabricatesAName() {
        assertEquals("Physics", deskClockBannerText("Physics", "Revise"))
        assertEquals("Revise", deskClockBannerText(null, "Revise"))
        // The fabricated default is gone: nothing named, no banner.
        assertNull(deskClockBannerText(null, null))
        // A blank intention is not a name either (the old code
        // printed an empty banner box for intention "").
        assertNull(deskClockBannerText(null, ""))
        assertNull(deskClockBannerText("", "  "))
        assertEquals("Revise", deskClockBannerText("", "Revise"))
    }

    @Test
    fun peekCooldownPrintCeilsItsMinutes() {
        // SIL-3 (Hale W-30's words): reason + remaining, ceiling.
        assertEquals("PEEK COOLDOWN — AGAIN IN 15 MIN", peekCooldownPrint(900))
        assertEquals("PEEK COOLDOWN — AGAIN IN 2 MIN", peekCooldownPrint(61))
        assertEquals("PEEK COOLDOWN — AGAIN IN 1 MIN", peekCooldownPrint(60))
        assertEquals("PEEK COOLDOWN — AGAIN IN 1 MIN", peekCooldownPrint(1))
    }

    @Test
    fun peekPriceAndBadgePrintFromTheSetting() {
        // SIL-2 (Pruefer's form): set the peek to 5 — the badge
        // says 5; the price names the 20-minute cooldown the arm
        // will impose. No surface hardcodes either number.
        assertEquals("5m", peekBadgeText(5 * 60))
        assertEquals("2m", peekBadgeText(120))
        assertEquals(
            "TAKING THE PEEK STARTS A 20 MINUTES COOLDOWN ON THIS APP",
            peekPriceText(20 * 60_000L)
        )
        assertEquals(
            "TAKING THE PEEK STARTS A 1 MINUTE COOLDOWN ON THIS APP",
            peekPriceText(60_000L)
        )
    }

    @Test
    fun airlockSessionLineStatesOnlyARealEnd() {
        // SIL-1 (Hale W-28): timed → the span end; stopwatch → no
        // set end; neither → no line, never an invented end.
        val end = 1_700_000_000_000L
        assertEquals("SESSION " + endsAtText(true, end), airlockSessionLine(true, end, false))
        assertEquals("SESSION RUNNING · NO SET END", airlockSessionLine(true, null, true))
        assertNull(airlockSessionLine(true, null, false))
    }

    @Test
    fun peekRemainingDerivesFromTheStoredEnd() {
        // UNA2-06 (in-app half): the printed remaining is a pure
        // function of the lease's stored end — no second clock.
        val now = 500_000L
        assertEquals(120, peekSecondsRemaining(now + 120_000L, now))
        assertEquals(62, peekSecondsRemaining(now + 61_500L, now))
        assertEquals(1, peekSecondsRemaining(now + 1L, now))
        // End reached, end absent (0), end in the past: over.
        assertNull(peekSecondsRemaining(now, now))
        assertNull(peekSecondsRemaining(0L, now))
        assertNull(peekSecondsRemaining(now - 1L, now))
    }
}
