package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.LoadoutRules
import com.example.data.SmartLoadout
import com.example.service.unlistedAppMustBeShut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #129 (Audit R3b, unlisted-distraction) — the regression
 * suite for the fail-closed cures:
 *  - F2: a launchable app with NO catalogue row is shut at the
 *    accessibility door; system plumbing is never trapped.
 *  - F4: a SmartLoadout never loosens LOCKED -> OPEN on a
 *    category guess, so the one-tap fix cannot re-open the
 *    unlisted app the fail-closed birth shut.
 *  - F5: the OPEN tally is framed as exposure, not bookkeeping.
 * (F1's birth tiers are proven in AppCatalogReconcileTest.)
 */
class UnlistedDistraction129Test {

    private val own = "com.example"
    private val homes = setOf("com.google.android.apps.nexuslauncher")

    @Test
    fun unlisted_launchableApp_isShut() {
        assertTrue(
            unlistedAppMustBeShut(
                "com.unknown.distraction", own, hasLaunchIntent = true,
                homePackages = homes, dialerPackage = "com.android.dialer",
                imePackage = "com.google.android.inputmethod.latin"
            )
        )
    }

    @Test
    fun unlisted_systemSurfaces_areNeverShut() {
        val ime = "com.google.android.inputmethod.latin"
        val dialer = "com.android.dialer"
        // Monolith itself.
        assertFalse(unlistedAppMustBeShut(own, own, true, homes, dialer, ime))
        // Hard system surfaces.
        assertFalse(unlistedAppMustBeShut("android", own, true, homes, dialer, ime))
        assertFalse(unlistedAppMustBeShut("com.android.systemui", own, true, homes, dialer, ime))
        assertFalse(unlistedAppMustBeShut("com.google.android.permissioncontroller", own, true, homes, dialer, ime))
        // The launcher is the floor, not a door.
        assertFalse(unlistedAppMustBeShut(homes.single(), own, true, homes, dialer, ime))
        // Dialer and keyboard keep their surfaces.
        assertFalse(unlistedAppMustBeShut(dialer, own, true, homes, dialer, ime))
        assertFalse(unlistedAppMustBeShut(ime, own, true, homes, dialer, ime))
        // No launch intent = service plumbing, not an opened app.
        assertFalse(unlistedAppMustBeShut("com.google.android.gms", own, false, homes, dialer, ime))
    }

    @Test
    fun loadout_neverLoosensLockedToOpen_129() {
        // The audit's lived case: a distraction mis-filed as
        // SHOPPING, born LOCKED under the fail-closed birth.
        // STUDY's guess says OPEN; the law keeps it LOCKED.
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.fresh.shop", "Fresh Shop",
                AppCategory.SHOPPING.name, currentTier = AppBlockTier.LOCKED
            )
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.WORK_SPRINT, "com.fresh.shop", "Fresh Shop",
                AppCategory.SHOPPING.name, currentTier = AppBlockTier.LOCKED
            )
        )
        // An app HE holds OPEN stays OPEN under the same guess —
        // the law blocks loosening a shut app, never his ruling.
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.google.android.apps.docs", "Docs",
                AppCategory.SHOPPING.name, currentTier = AppBlockTier.OPEN
            )
        )
        // Tightening still works exactly as before.
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.instagram.android", "Instagram",
                AppCategory.SHORT_VIDEO.name, currentTier = AppBlockTier.OPEN
            )
        )
    }

    @Test
    fun loadout_secondBrowserIsPeek_notOpen_129() {
        // Ported from the earlier lane's LoadoutTiersTest (that
        // duplicate derivation was retired at the merge; the rule
        // lives in LoadoutRules now): every BROWSERS-category app
        // lands on PEEK_2M under STUDY and WORK_SPRINT, not just
        // Chrome by name.
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.opera.browser", "Opera",
                AppCategory.BROWSERS.name, currentTier = AppBlockTier.OPEN
            )
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutRules.tierFor(
                SmartLoadout.WORK_SPRINT, "com.duckduckgo.mobile.android", "DuckDuckGo",
                AppCategory.BROWSERS.name, currentTier = AppBlockTier.OPEN
            )
        )
        // And the promised categories still lock / peek as before.
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.reddit.frontpage", "Reddit",
                AppCategory.SOCIAL.name, currentTier = AppBlockTier.OPEN
            )
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.whatsapp", "WhatsApp",
                AppCategory.MESSAGING.name, currentTier = AppBlockTier.LOCKED
            )
        )
    }
}
