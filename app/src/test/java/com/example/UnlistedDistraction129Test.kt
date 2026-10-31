package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.LoadoutRules
import com.example.data.ShoppingFailClosedWarning
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
 *  - D-2: the one-time SHOPPING fail-closed warning is owed
 *    exactly while it has never been dismissed and a SHOPPING
 *    row stands in the catalogue (panel verdict, 64–4).
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

    // ---- D-2: the one-time SHOPPING fail-closed warning --------

    private fun row(pkg: String, category: AppCategory, tier: AppBlockTier) =
        BlockedAppEntity(
            packageName = pkg, appName = pkg, category = category.name,
            isBlocked = tier != AppBlockTier.OPEN, blockTier = tier.name
        )

    @Test
    fun shoppingWarning_owedWhileShoppingRowStands_undismissed_129() {
        // The lived case: an unrecognised app filed SHOPPING and
        // born LOCKED under the D-1 fail-closed birth — he has
        // never been told the category stands shut by default.
        assertTrue(
            ShoppingFailClosedWarning.owed(
                listOf(row("com.fresh.shop", AppCategory.SHOPPING, AppBlockTier.LOCKED)),
                dismissed = false
            )
        )
        // The category law hangs over the row whatever its tier:
        // a profile tap lands the whole SHOPPING category LOCKED,
        // so a row he holds OPEN (or PEEK) is still exposure to
        // the unruled default and the warning is still owed.
        assertTrue(
            ShoppingFailClosedWarning.owed(
                listOf(row("com.big.store", AppCategory.SHOPPING, AppBlockTier.OPEN)),
                dismissed = false
            )
        )
        assertTrue(
            ShoppingFailClosedWarning.owed(
                listOf(row("com.big.store", AppCategory.SHOPPING, AppBlockTier.PEEK_2M)),
                dismissed = false
            )
        )
    }

    @Test
    fun shoppingWarning_oneTime_onceDismissedNeverOwed_129() {
        // One-time means one time: after his tap, the warning is
        // never owed again — not even when a new SHOPPING app is
        // installed later.
        val apps = listOf(
            row("com.fresh.shop", AppCategory.SHOPPING, AppBlockTier.LOCKED),
            row("com.new.store", AppCategory.SHOPPING, AppBlockTier.LOCKED)
        )
        assertFalse(ShoppingFailClosedWarning.owed(apps, dismissed = true))
    }

    @Test
    fun shoppingWarning_notOwedWithoutShoppingRow_129() {
        // No SHOPPING row on the phone = no fail-closed block to
        // warn about; other categories standing shut (his ruled
        // distractions) never trigger the shopping warning.
        assertFalse(ShoppingFailClosedWarning.owed(emptyList(), dismissed = false))
        assertFalse(
            ShoppingFailClosedWarning.owed(
                listOf(
                    row("com.instagram.android", AppCategory.SOCIAL, AppBlockTier.LOCKED),
                    row("com.whatsapp", AppCategory.MESSAGING, AppBlockTier.PEEK_2M)
                ),
                dismissed = false
            )
        )
    }
}
