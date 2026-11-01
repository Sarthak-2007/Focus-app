package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.LegacyOpenReset
import com.example.data.LoadoutRules
import com.example.data.SmartLoadout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Ren rulings 00:34 IST Oct 31 (#7, from KLAR-BAU's final
 * supplement via Review Ren; filed on #7 by K1-W7b, comment
 * 6531765331; carried here as the companion lane beside PR #388):
 *
 *  Ruling 1 — the loadout-default leak flips to SHUT. STUDY /
 *  WORK_SPRINT used to answer OPEN for every category no rule
 *  names, so a routine firing a loadout silently undid the
 *  inversion. The default is now: his own tier stands, and an
 *  app with no tier defaults LOCKED.
 *
 *  Ruling 2 — legacy OPEN rows reset once, announced with the
 *  count. LegacyOpenReset is the selection half of that reset.
 *
 * PIN SCOPE: SOURCE — pure derivation/selection truth table over
 * LoadoutRules.tierFor and LegacyOpenReset; no Android runtime.
 */
class ShieldRulingsOct31Test {

    private fun tier(
        loadout: SmartLoadout,
        pkg: String,
        category: AppCategory,
        current: AppBlockTier? = null
    ) = LoadoutRules.tierFor(loadout, pkg, "App", category.name, current)

    // ---- Ruling 1: the flipped default ---------------------------

    @Test
    fun unruled_tierlessApp_defaultsShut_bothLoadouts() {
        // SHOPPING is named by no STUDY / WORK_SPRINT rule — the
        // old else -> OPEN leak, now LOCKED when nothing else
        // speaks for the app.
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.shop.example", AppCategory.SHOPPING)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.WORK_SPRINT, "com.shop.example", AppCategory.SHOPPING)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.WORK_SPRINT, "com.bank.example", AppCategory.FINANCE)
        )
    }

    @Test
    fun unruled_peekIsNoLongerLoosenedToOpen() {
        // The lived leak: an unruled app standing at PEEK (his
        // peek, or the dialler floor's) was loosened to OPEN by a
        // routine firing either loadout. It now stays PEEK.
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.STUDY, "com.shop.example", AppCategory.SHOPPING, AppBlockTier.PEEK_2M)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.WORK_SPRINT, "com.shop.example", AppCategory.SHOPPING, AppBlockTier.PEEK_2M)
        )
    }

    @Test
    fun unruled_hisOwnTierStands() {
        // #129's law is untouched by the flip: a loadout never
        // overrides HIS ruling in either direction. His OPEN
        // stays OPEN, his LOCKED stays LOCKED — the Sheet is the
        // only law-writing place; the flip removes the loadout's
        // own OPEN default, not his word.
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "com.google.android.apps.docs", AppCategory.SHOPPING, AppBlockTier.OPEN)
        )
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.WORK_SPRINT, "com.google.android.apps.docs", AppCategory.SHOPPING, AppBlockTier.OPEN)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.shop.example", AppCategory.SHOPPING, AppBlockTier.LOCKED)
        )
    }

    @Test
    fun unruled_diallerFloorStillBindsTheDefault() {
        // The flipped default is LOCKED, but the dialler floor
        // (#119) still raises a dialler out of LOCKED to PEEK.
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.STUDY, "com.android.dialer", AppCategory.SHOPPING)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.WORK_SPRINT, "com.android.dialer", AppCategory.SHOPPING)
        )
    }

    @Test
    fun namedRules_areUnchangedByTheFlip() {
        // Reasoned category policy is not the leak: study
        // surfaces and EDUCATION stay OPEN under STUDY, messaging
        // peeks, browsers peek under WORK_SPRINT, the named
        // distraction categories still lock.
        assertEquals(
            AppBlockTier.OPEN,
            tier(SmartLoadout.STUDY, "com.ichi2.anki", AppCategory.EDUCATION)
        )
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.google.android.youtube", "YouTube",
                AppCategory.SHORT_VIDEO.name
            )
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.STUDY, "com.whatsapp", AppCategory.MESSAGING)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            tier(SmartLoadout.WORK_SPRINT, "com.opera.browser", AppCategory.BROWSERS)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.WORK_SPRINT, "com.example.game", AppCategory.GAMES)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            tier(SmartLoadout.STUDY, "com.instagram.android", AppCategory.SOCIAL)
        )
    }

    // ---- Ruling 2: the reset selection ---------------------------

    private fun app(
        pkg: String,
        tier: AppBlockTier,
        category: AppCategory = AppCategory.SHOPPING
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = pkg,
        category = category.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    @Test
    fun reset_selectsExactlyTheOpenRemainder() {
        val apps = listOf(
            app("com.shop.example", AppBlockTier.OPEN),
            app("com.social.example", AppBlockTier.OPEN, AppCategory.SOCIAL),
            app("com.peek.example", AppBlockTier.PEEK_2M),
            app("com.shut.example", AppBlockTier.LOCKED)
        )
        assertEquals(
            listOf("com.shop.example", "com.social.example"),
            LegacyOpenReset.packagesToReset(apps, ownPackage = "com.example")
        )
    }

    @Test
    fun reset_neverSelectsFloorOrSelf() {
        val apps = listOf(
            app("com.android.dialer", AppBlockTier.OPEN),
            app("com.google.android.inputmethod.latin", AppBlockTier.OPEN),
            app("com.android.emergency", AppBlockTier.OPEN),
            app("com.example", AppBlockTier.OPEN),
            app("com.shop.example", AppBlockTier.OPEN)
        )
        assertEquals(
            listOf("com.shop.example"),
            LegacyOpenReset.packagesToReset(apps, ownPackage = "com.example")
        )
    }

    @Test
    fun reset_emptyWhenNothingStandsOpen() {
        val apps = listOf(
            app("com.peek.example", AppBlockTier.PEEK_2M),
            app("com.shut.example", AppBlockTier.LOCKED)
        )
        assertTrue(
            LegacyOpenReset.packagesToReset(apps, ownPackage = "com.example").isEmpty()
        )
        assertTrue(
            LegacyOpenReset.packagesToReset(emptyList(), ownPackage = "com.example").isEmpty()
        )
    }

    @Test
    fun announcement_carriesTheLiveCount() {
        // The ruling's sentence, in the Sheet strip's grammar.
        assertEquals(
            "7 APPS WERE RESET TO SHUT — REOPEN ANY OF THEM HERE",
            LegacyOpenReset.announcementLine(7)
        )
        assertEquals(
            "1 APP WAS RESET TO SHUT — REOPEN IT HERE",
            LegacyOpenReset.announcementLine(1)
        )
    }
}
