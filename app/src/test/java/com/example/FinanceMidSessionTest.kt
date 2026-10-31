package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
import com.example.data.AppCategorizer
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.InstalledPackage
import com.example.service.InterceptPath
import com.example.service.NotificationCapturePolicy
import com.example.ui.PeekWithheld
import com.example.ui.peekOfferFor
import com.example.ui.peekWithheldFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #120 — Audit R3b, angle "otp-banking-mid-session" (persona:
 * a student whose bank sends a 30-second OTP mid-session). Each
 * test fails on the pre-cure behaviour named in the audit:
 *
 *  F5 — no finance category existed: banks/UPI/authenticators were
 *       filed SHOPPING and bulk-locked by the Sleep profile.
 *  F3 — a LOCKED banking app had no peek at all; the only door was
 *       the once-a-week 60-second emergency valve.
 *  F4 — the 20-minute per-app peek cooldown killed the second OTP.
 *  F1 — the listener cancelled the original notification (and its
 *       Approve action); the cure's testable core is the pure
 *       one-time-code predicate that now spares the original.
 *  (F2, the frozen digest, was already cured by issue #118's
 *   remint-on-arrival and is struck on the issue with evidence.)
 */
class FinanceMidSessionTest {

    // -------------------------------------------------------------
    // F5 — the classifier gives finance a deterministic home, and
    // what it files there is born peekable, never locked.
    // -------------------------------------------------------------

    @Test
    fun financePackages_fileAsFinance() {
        val cases = listOf(
            "com.google.android.apps.nbu.paisa.user" to "Google Pay",
            "com.phonepe.app" to "PhonePe",
            "net.one97.paytm" to "Paytm",
            "in.org.npci.upiapp" to "BHIM UPI",
            "com.google.android.apps.authenticator2" to "Authenticator",
            "com.microsoft.authenticator" to "Microsoft Authenticator",
            "com.example.hdfcbank" to "HDFC Bank",
            "com.example.upiapp" to "UPI Payments"
        )
        for ((pkg, label) in cases) {
            assertEquals("$pkg must file FINANCE", AppCategory.FINANCE,
                AppCategorizer.categorize(pkg, label))
        }
    }

    @Test
    fun financeFiling_doesNotStealOtherCategories() {
        assertEquals(AppCategory.MESSAGING,
            AppCategorizer.categorize("com.whatsapp", "WhatsApp"))
        assertEquals(AppCategory.GAMES,
            AppCategorizer.categorize("com.supercell.clashroyale", "Clash Royale"))
        assertEquals(AppCategory.MUSIC,
            AppCategorizer.categorize("com.spotify.music", "Spotify"))
        assertEquals(AppCategory.SHOPPING,
            AppCategorizer.categorize("com.example.notes", "Notes"))
    }

    private fun discovered(pkg: String, label: String): BlockedAppEntity {
        val plan = AppCatalogReconcile.reconcile(
            existing = emptyList(),
            installed = listOf(InstalledPackage(pkg, label)),
            usageMinutes = emptyMap(),
            categorize = AppCategorizer::categorize
        )
        assertEquals(1, plan.toInsert.size)
        return plan.toInsert.single()
    }

    @Test
    fun discoveredFinance_isBornPeek_neverLocked() {
        for ((pkg, label) in listOf(
            "com.phonepe.app" to "PhonePe",
            "com.example.hdfcbank" to "HDFC Bank"
        )) {
            val row = discovered(pkg, label)
            assertEquals(AppCategory.FINANCE.name, row.category)
            assertEquals("$pkg must be born PEEK_2M", AppBlockTier.PEEK_2M, row.resolvedTier)
        }
    }

    // -------------------------------------------------------------
    // F3 + F4 — the essential peek: a LOCKED finance app still
    // offers the 2-minute peek, and the cooldown never rations it.
    // Non-finance semantics are untouched.
    // -------------------------------------------------------------

    private fun entity(category: AppCategory, tier: AppBlockTier) = BlockedAppEntity(
        packageName = "com.example.app",
        appName = "Example",
        category = category.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    @Test
    fun lockedFinance_stillOffersThePeek() {
        val bank = entity(AppCategory.FINANCE, AppBlockTier.LOCKED)
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, bank, 0, false))
        assertTrue(peekOfferFor(InterceptPath.SHIELD_APP, bank, 0, false))
    }

    @Test
    fun financePeek_isNeverRationedByTheCooldown() {
        val bank = entity(AppCategory.FINANCE, AppBlockTier.LOCKED)
        // 19 of the 20 cooldown minutes still standing: a second
        // OTP (login, then payment) still gets its door.
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, bank, 19 * 60, false))
        val upi = entity(AppCategory.FINANCE, AppBlockTier.PEEK_2M)
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, upi, 19 * 60, false))
    }

    @Test
    fun financePeek_stillBindsToTheRunningPill() {
        val bank = entity(AppCategory.FINANCE, AppBlockTier.LOCKED)
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, bank, 0, true))
        assertEquals(PeekWithheld.ALREADY_ACTIVE,
            peekWithheldFor(false, InterceptPath.APP_TIER, bank, 0, true))
    }

    @Test
    fun nonFinancePeekSemantics_unchanged() {
        val shop = entity(AppCategory.SHOPPING, AppBlockTier.LOCKED)
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, shop, 0, false))
        // Floor lane (census #6, row 12): this line used to expect
        // silence (null). Issue #154 composed its state line on top
        // of #120 — a LOCKED tier on an honouring door now NAMES
        // itself (PeekWithheld.TIER_LOCKED, "a state line, not a
        // refusal"), with finance rows expressly carved out so
        // their peek keeps its own withheld reasons below. The
        // non-finance semantics #120 froze are the offer and the
        // ration (asserted here, unchanged); the withheld answer
        // for a locked non-finance row is #154's named state.
        assertEquals(PeekWithheld.TIER_LOCKED,
            peekWithheldFor(false, InterceptPath.APP_TIER, shop, 0, false))
        val chat = entity(AppCategory.MESSAGING, AppBlockTier.PEEK_2M)
        assertTrue(peekOfferFor(InterceptPath.APP_TIER, chat, 0, false))
        // The 20-minute ration still binds non-finance peeks.
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, chat, 60, false))
    }

    // -------------------------------------------------------------
    // F1 — the one-time-code predicate that spares the original
    // notification (copied to HELD, never cancelled).
    // -------------------------------------------------------------

    @Test
    fun oneTimeCodes_areRecognised() {
        assertTrue(NotificationCapturePolicy.looksLikeOneTimeCode(
            "SBI", "Your OTP is 482913. Do not share it with anyone."))
        assertTrue(NotificationCapturePolicy.looksLikeOneTimeCode(
            "VM-HDFCBK", "Use 839201 as your verification code for login."))
        assertTrue(NotificationCapturePolicy.looksLikeOneTimeCode(
            "WhatsApp", "Your one-time passcode is ready"))
        assertTrue(NotificationCapturePolicy.looksLikeOneTimeCode(
            "Bank", "Your passcode was changed just now"))
    }

    @Test
    fun ordinaryMessages_areNotOneTimeCodes() {
        // Digits alone never qualify — a chat full of numbers is
        // still a chat, and it is still held like any other.
        assertFalse(NotificationCapturePolicy.looksLikeOneTimeCode(
            "Asha", "I took 482913 photos on the trip, sending the best 12"))
        assertFalse(NotificationCapturePolicy.looksLikeOneTimeCode(
            "Asha", "the code for the gym locker is on the fridge"))
        assertFalse(NotificationCapturePolicy.looksLikeOneTimeCode(
            "News", "Sensex closes 840 points higher"))
    }
}
