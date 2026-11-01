package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.LoadoutRules
import com.example.data.SmartLoadout
import com.example.data.categorizePackage
import com.example.ui.appMatchesQuery
import com.example.ui.screens.PEEK_TERMS_LINE
import com.example.ui.screens.ShieldMatrixScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.concurrent.atomic.AtomicReference

/**
 * Issue #107 (Audit R3, journey shield-setup-lived) — the evidence.
 * Pure halves: the category guess, the loadout rule + preview, the
 * search match. Rendered half (Robolectric): the Shield screen
 * prints the PEEK terms and the slip hint, a cap tap STAGES a
 * loadout (subtitle + side-effects printed, nothing applied) and
 * only APPLY commits; the no-match line never claims absence.
 */
class ShieldSetupTruthPureTest {

    private fun app(
        pkg: String,
        name: String,
        category: AppCategory = AppCategory.SHOPPING,
        tier: AppBlockTier = AppBlockTier.OPEN
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = category.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    // ---- #3: the category guess ---------------------------------

    @Test
    fun categorize_playStoreIsNotGames() {
        // The old bare "play" token filed the Play Store as GAMES.
        assertEquals(
            AppCategory.SHOPPING,
            categorizePackage("com.android.vending", "Play Store")
        )
    }

    @Test
    fun categorize_displayInLabelIsNotGames() {
        assertEquals(
            AppCategory.SHOPPING,
            categorizePackage("com.example.displaynotes", "Display Notes")
        )
    }

    @Test
    fun categorize_realAppsStillLand() {
        assertEquals(
            AppCategory.SHORT_VIDEO,
            categorizePackage("com.zhiliaoapp.musically", "TikTok")
        )
        assertEquals(
            AppCategory.GAMES,
            categorizePackage("com.supercell.clashofclans", "Clash of Clans")
        )
        assertEquals(
            AppCategory.MESSAGING,
            categorizePackage("com.whatsapp", "WhatsApp")
        )
        assertEquals(
            AppCategory.BROWSERS,
            categorizePackage("com.android.chrome", "Chrome")
        )
    }

    // ---- #1: the loadout rule + preview --------------------------

    @Test
    fun loadout_studyTiers() {
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.instagram.android", "Instagram",
                AppCategory.SHORT_VIDEO.name
            )
        )
        assertEquals(
            AppBlockTier.PEEK_2M,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.whatsapp", "WhatsApp",
                AppCategory.MESSAGING.name
            )
        )
        // Ren ruling 00:34 IST Oct 31 (#7) SUPERSEDES the old line
        // here: an unruled category (SHOPPING) with no tier of his
        // own used to default OPEN under STUDY — that default was
        // the loadout leak that undid the inversion. The default
        // is now SHUT; the full ruling matrix is pinned in
        // ShieldRulingsOct31Test.
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.STUDY, "com.google.android.apps.docs", "Docs",
                AppCategory.SHOPPING.name
            )
        )
    }

    @Test
    fun loadout_sleepKeepsPhoneOpen() {
        assertEquals(
            AppBlockTier.OPEN,
            LoadoutRules.tierFor(
                SmartLoadout.SLEEP, "com.android.dialer", "Phone",
                AppCategory.SHOPPING.name
            )
        )
        assertEquals(
            AppBlockTier.LOCKED,
            LoadoutRules.tierFor(
                SmartLoadout.SLEEP, "com.instagram.android", "Instagram",
                AppCategory.SHORT_VIDEO.name
            )
        )
    }

    @Test
    fun loadout_previewCountsChangesAndLanding() {
        val apps = listOf(
            app("com.instagram.android", "Instagram", AppCategory.SHORT_VIDEO, AppBlockTier.OPEN),
            app("com.whatsapp", "WhatsApp", AppCategory.MESSAGING, AppBlockTier.PEEK_2M),
            app("com.docs", "Docs", AppCategory.SHOPPING, AppBlockTier.LOCKED)
        )
        val preview = LoadoutRules.preview(SmartLoadout.STUDY, apps)
        // Instagram OPEN->LOCKED changes; WhatsApp stays PEEK.
        // Docs was LOCKED->OPEN before Issue #129; a loadout no
        // longer loosens LOCKED->OPEN on a category guess, so Docs
        // stays LOCKED and uncounted as a change.
        assertEquals(1, preview.changed)
        assertEquals(2, preview.shut)
        assertEquals(1, preview.peek)
        assertEquals(0, preview.open)
    }

    @Test
    fun loadout_subtitlesExistForThePreview() {
        SmartLoadout.entries.forEach { assertTrue(it.subtitle.isNotBlank()) }
        assertTrue(SmartLoadout.STUDY.subtitle.contains("Shorts & Web shields"))
    }

    // ---- #2: the printed PEEK terms are the enforced terms -------

    @Test
    fun peekTermsLinePrintsTheContract() {
        assertTrue(PEEK_TERMS_LINE.startsWith("PEEK = 2 MINUTES, ONCE"))
        assertTrue(PEEK_TERMS_LINE.contains("20 MINUTES SHUT"))
    }

    // ---- #5: the search match ------------------------------------

    @Test
    fun search_matchesNamePackageAndCategory() {
        val tiktok = app("com.zhiliaoapp.musically", "TikTok", AppCategory.SHORT_VIDEO)
        assertTrue(appMatchesQuery(tiktok, "tiktok"))
        assertTrue(appMatchesQuery(tiktok, "musically"))
        assertTrue(appMatchesQuery(tiktok, "zhiliaoapp"))
        assertTrue(appMatchesQuery(tiktok, "short video"))
        assertFalse(appMatchesQuery(tiktok, "banking"))
        assertTrue(appMatchesQuery(tiktok, ""))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ShieldSetupTruthRenderTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun app(
        pkg: String,
        name: String,
        category: AppCategory = AppCategory.SHOPPING,
        tier: AppBlockTier = AppBlockTier.OPEN
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = category.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    private fun setShield(
        apps: List<BlockedAppEntity>,
        filtered: List<BlockedAppEntity> = apps,
        query: String = "",
        applied: AtomicReference<SmartLoadout?> = AtomicReference(null)
    ) {
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ShieldMatrixScreen(
                    allApps = apps,
                    filteredApps = filtered,
                    searchQuery = query,
                    selectedCategory = null,
                    shortsReelsSniperEnabled = false,
                    browserUrlBlockerEnabled = false,
                    onSearchQueryChanged = {},
                    onSelectCategory = {},
                    onSetAppTier = { _, _ -> },
                    onToggleShortsReelsSniper = {},
                    onToggleBrowserUrlBlocker = {}
                )
            }
        }
    }

    @Test
    fun shield_printsPeekTermsAndSlipHint() {
        setShield(listOf(app("com.whatsapp", "WhatsApp", AppCategory.MESSAGING, AppBlockTier.PEEK_2M)))
        composeRule.onNodeWithTag("shield_peek_terms").assertIsDisplayed()
        composeRule.onNodeWithText("PEEK = 2 MINUTES", substring = true).assertExists()
        composeRule.onNodeWithTag("slip_hint").assertIsDisplayed()
        composeRule.onNodeWithText("TAP A LINE TO FLIP IT", substring = true).assertExists()
    }

    // (File renamed ShieldSetupTruthTest.kt ->
    // ShieldSetupTruthRenderTest.kt in the same PR: the targeted
    // gate maps a changed test FILE to a class filter by file
    // name, and the old name matched neither class this file
    // holds, so its targeted job could only ever report "no tests
    // found". The rename targets the Render class — the class
    // pruned below; the Pure class's identical blind spot is
    // filed on #166 for the workflow parcel.)
    //
    // PRUNED (floor lane, census #6, row 20 — Sarthak's Oct 31
    // pruning process: useless tests only, by name, with reason):
    // shield_capTapStagesLoadout_applyCommits stood here. It
    // guarded the Shield screen's cap-staging flow (tap a cap stop
    // -> loadout_preview -> loadout_apply commits) — a surface
    // Shield Rethink 2 Step 2 DELETED from production (commit
    // 3011b409: "profiles + smart-loadout DELETED from the Shield
    // surface"; the LoadoutPreviewPanel followed in 1b5a06da).
    // Verified firsthand at the prune: ShieldCapSelector( has no
    // invocation site anywhere in production, and the
    // loadout_preview / loadout_apply tags exist nowhere in
    // app/src/main. The test could only ever fail on a missing
    // node. ShieldCap's pure logic keeps its own coverage in
    // ShieldCapGuardLineWave3Test. Reason + evidence on #166 and
    // #245.

    @Test
    fun shield_noMatchLineNeverClaimsAbsence() {
        setShield(
            apps = listOf(app("com.zhiliaoapp.musically", "TikTok", AppCategory.SHORT_VIDEO)),
            filtered = emptyList(),
            query = "banking"
        )
        composeRule.onNodeWithTag("shield_no_match_line").assertIsDisplayed()
        composeRule.onNodeWithText("NO MATCH FOR THAT SEARCH", substring = true).assertExists()
        composeRule.onNodeWithText("NO APP BY THAT NAME ON THIS PHONE", substring = true)
            .assertDoesNotExist()
    }
}
