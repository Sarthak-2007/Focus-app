package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.LoadoutTiers
import com.example.data.SmartLoadout
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #129 F4: the loadout derivation, proven pure — browsers
 * get a knock under STUDY/WORK_SPRINT (not just Chrome by name),
 * the promised categories lock, and the SHOPPING unknown stays
 * OPEN only as the priced, decision-needed remainder.
 */
class LoadoutTiersTest {

    @Test
    fun study_secondBrowserIsPeek_notOpen() {
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutTiers.tierFor(
                SmartLoadout.STUDY, "com.opera.browser", "Opera", AppCategory.BROWSERS.name
            )
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutTiers.tierFor(
                SmartLoadout.WORK_SPRINT, "org.mozilla.firefox", "Firefox", AppCategory.BROWSERS.name
            )
        )
    }

    @Test
    fun study_locksPromisedCategories_messagingPeeks() {
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutTiers.tierFor(SmartLoadout.STUDY, "com.x", "X", AppCategory.SOCIAL.name)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutTiers.tierFor(SmartLoadout.STUDY, "com.g", "G", AppCategory.GAMES.name)
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutTiers.tierFor(SmartLoadout.STUDY, "com.wa", "WA", AppCategory.MESSAGING.name)
        )
    }

    @Test
    fun workSprint_socialAndBrowsersPeek_gamesLock() {
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutTiers.tierFor(SmartLoadout.WORK_SPRINT, "com.r", "R", AppCategory.SOCIAL.name)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutTiers.tierFor(SmartLoadout.WORK_SPRINT, "com.g", "G", AppCategory.GAMES.name)
        )
    }

    @Test
    fun sleep_locksEverythingExceptPhoneClockMaps() {
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutTiers.tierFor(SmartLoadout.SLEEP, "com.dialer", "Phone", AppCategory.SHOPPING.name)
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutTiers.tierFor(SmartLoadout.SLEEP, "com.news", "News", AppCategory.BROWSERS.name)
        )
    }

    @Test
    fun shoppingUnknown_staysOpen_underStudyAndSprint_theDecisionNeededRemainder() {
        // Documented boundary, not an oversight: failing the
        // whole "Other Apps" bucket closed would lock Docs and
        // calculators too. It stays OPEN here and stays in the
        // unreviewed record (F1) until Sarthak rules on the flip.
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutTiers.tierFor(SmartLoadout.STUDY, "com.unknown", "Unknown", AppCategory.SHOPPING.name)
        )
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutTiers.tierFor(SmartLoadout.WORK_SPRINT, "com.unknown", "Unknown", AppCategory.SHOPPING.name)
        )
    }
}
