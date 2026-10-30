package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
import com.example.data.AppCategorizer
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.InstalledPackage
import com.example.data.groupedByRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The catalog reconcile and the rules grouping, proven as pure
 * derivations (Ordner lane): the table ends a sync equal to the
 * installed set, and the slip's groups are the stored tiers.
 */
class AppCatalogReconcileTest {

    private val categorize: (String, String) -> AppCategory = { _, label ->
        when {
            label.contains("Video") -> AppCategory.SHORT_VIDEO
            label.contains("Chat") -> AppCategory.MESSAGING
            label.contains("Game") -> AppCategory.GAMES
            else -> AppCategory.SHOPPING
        }
    }

    private fun row(
        pkg: String,
        name: String,
        tier: AppBlockTier,
        usage: Int = 0
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name,
        dailyLimitMinutes = 30,
        todayUsageMinutes = usage,
        interceptCount = 0
    )

    @Test
    fun reconcile_prunesUninstalled_discoversInstalled_presetBeatsCategory() {
        val existing = listOf(
            row("com.ghost.video", "Ghost Video", AppBlockTier.LOCKED),
            row("com.keeper", "Keeper", AppBlockTier.LOCKED),
            row("com.gone.chat", "Gone Chat", AppBlockTier.PEEK_2M)
        )
        val installed = listOf(
            InstalledPackage("com.keeper", "Keeper"),
            InstalledPackage("com.google.android.youtube", "YouTube Video"),
            InstalledPackage("com.fresh.chat", "Fresh Chat")
        )

        val plan = AppCatalogReconcile.reconcile(existing, installed, emptyMap(), categorize)

        // Both absent packages leave; the keeper's row is untouched.
        assertEquals(setOf("com.ghost.video", "com.gone.chat"), plan.toPrune.toSet())
        val inserted = plan.toInsert.associateBy { it.packageName }
        assertEquals(setOf("com.google.android.youtube", "com.fresh.chat"), inserted.keys)
        // YouTube's FACTORY PRESET (PEEK, 30m) wins over its category
        // default (SHORT_VIDEO would be LOCKED): presets are birth
        // rules, categories are the fallback.
        assertEquals(AppBlockTier.PEEK_2M, inserted.getValue("com.google.android.youtube").resolvedTier)
        assertEquals(30, inserted.getValue("com.google.android.youtube").dailyLimitMinutes)
        // No preset: the category default rules (MESSAGING -> PEEK).
        assertEquals(AppBlockTier.PEEK_2M, inserted.getValue("com.fresh.chat").resolvedTier)
        // Discovered rows carry no invented history.
        assertTrue(plan.toInsert.all { it.interceptCount == 0 })
    }

    @Test
    fun reconcile_guessBornOpenAppsAreNamedUnreviewed_presetBornAreNot() {
        // #129 F1, as closed by the STURM-4 fix-forward: the
        // earlier lane named guess-born OPEN apps "unreviewed";
        // the settled policy goes further — the guess no longer
        // births OPEN at all (fail-closed: unlisted = shut), so
        // the unreviewed-open list is empty by construction and
        // the mystery app is born LOCKED. A preset-born OPEN app
        // (Chrome) keeps its deliberate preset birth.
        val installed = listOf(
            InstalledPackage("com.mystery", "Mystery"),
            InstalledPackage("com.android.chrome", "Chrome"),
            InstalledPackage("com.fresh.chat", "Fresh Chat"),
            InstalledPackage("com.new.game", "New Game")
        )
        val plan = AppCatalogReconcile.reconcile(emptyList(), installed, emptyMap(), categorize)
        assertEquals(emptyList<String>(), plan.unreviewedOpenPackages)
        val inserted = plan.toInsert.associateBy { it.packageName }
        assertEquals(AppBlockTier.LOCKED, inserted.getValue("com.mystery").resolvedTier)
        assertEquals(AppBlockTier.OPEN, inserted.getValue("com.android.chrome").resolvedTier)
    }

    @Test
    fun reconcile_categoryDefaults_shortVideoLocked_fallbackOpen_noPreset() {
        // Audit R1 tests-1 #2: the only SHORT_VIDEO app in the
        // suite used to be YouTube — whose FACTORY PRESET masked
        // the category default, so the LOCKED and OPEN
        // category-default branches were never asserted. These
        // packages have NO preset (checked against
        // FACTORY_PRESETS), so the category default is the only
        // rule that can price them.
        val installed = listOf(
            InstalledPackage("com.fresh.video", "Fresh Video"),
            InstalledPackage("com.fresh.game", "Fresh Game"),
            InstalledPackage("com.fresh.shop", "Fresh Shop")
        )
        val plan = AppCatalogReconcile.reconcile(emptyList(), installed, emptyMap(), categorize)
        val inserted = plan.toInsert.associateBy { it.packageName }
        assertEquals(3, inserted.size)
        // SHORT_VIDEO -> LOCKED (the branch YouTube's preset hid).
        val video = inserted.getValue("com.fresh.video")
        assertEquals(AppCategory.SHORT_VIDEO.name, video.category)
        assertEquals(AppBlockTier.LOCKED, video.resolvedTier)
        assertTrue(video.isBlocked)
        assertEquals(30, video.dailyLimitMinutes)
        // GAMES -> LOCKED (the same branch, its other category).
        assertEquals(AppBlockTier.LOCKED, inserted.getValue("com.fresh.game").resolvedTier)
        // SHOPPING hits the else branch -> LOCKED (Issue #129:
        // fail-closed birth; this asserted OPEN before the cure —
        // the born-open hole itself, encoded as a test).
        val shop = inserted.getValue("com.fresh.shop")
        assertEquals(AppCategory.SHOPPING.name, shop.category)
        assertEquals(AppBlockTier.LOCKED, shop.resolvedTier)
        assertTrue(shop.isBlocked)
    }

    @Test
    fun reconcile_unlistedIsBornLocked_129() {
        // Issue #129 F1 with the REAL classifier: SOCIAL and
        // BROWSERS — both born OPEN under the old policy — are
        // born LOCKED, as is every unrecognised app (SHOPPING).
        val installed = listOf(
            InstalledPackage("com.fresh.social", "Fresh Twitter Club"),
            InstalledPackage("com.fresh.browser", "Fresh Browser"),
            InstalledPackage("com.unknown.thing", "Mystery App")
        )
        val plan = AppCatalogReconcile.reconcile(
            emptyList(), installed, emptyMap(), AppCategorizer::categorize
        )
        val inserted = plan.toInsert.associateBy { it.packageName }
        assertEquals(AppCategory.SOCIAL.name, inserted.getValue("com.fresh.social").category)
        assertEquals(AppBlockTier.LOCKED, inserted.getValue("com.fresh.social").resolvedTier)
        assertEquals(AppCategory.BROWSERS.name, inserted.getValue("com.fresh.browser").category)
        assertEquals(AppBlockTier.LOCKED, inserted.getValue("com.fresh.browser").resolvedTier)
        assertEquals(AppCategory.SHOPPING.name, inserted.getValue("com.unknown.thing").category)
        assertEquals(AppBlockTier.LOCKED, inserted.getValue("com.unknown.thing").resolvedTier)
    }

    @Test
    fun reconcile_discoveryHasNoCap() {
        val installed = (1..25).map { InstalledPackage("com.app.$it", "App $it") }
        val plan = AppCatalogReconcile.reconcile(emptyList(), installed, emptyMap(), categorize)
        assertEquals(25, plan.toInsert.size)
        assertTrue(plan.toPrune.isEmpty())
    }

    @Test
    fun reconcile_existingRowKeepsItsTier_usageFollowsTheRealFigure() {
        val existing = listOf(
            row("com.keeper", "Keeper", AppBlockTier.OPEN, usage = 3),
            row("com.quiet", "Quiet", AppBlockTier.LOCKED, usage = 9)
        )
        val installed = listOf(
            InstalledPackage("com.keeper", "Keeper"),
            InstalledPackage("com.quiet", "Quiet")
        )
        val plan = AppCatalogReconcile.reconcile(
            existing, installed, mapOf("com.keeper" to 17), categorize
        )
        assertTrue(plan.toInsert.isEmpty())
        assertTrue(plan.toPrune.isEmpty())
        // Only the keeper's usage moves, to the real figure; its
        // OPEN tier — the man's own setting — is never re-priced.
        assertEquals(1, plan.usageUpdates.size)
        assertEquals("com.keeper", plan.usageUpdates.single().packageName)
        assertEquals(17, plan.usageUpdates.single().todayUsageMinutes)
        assertEquals(AppBlockTier.OPEN, plan.usageUpdates.single().resolvedTier)
    }

    @Test
    fun reconcile_multiActivityPackageIsOneApp() {
        val installed = listOf(
            InstalledPackage("com.duo", "Duo"),
            InstalledPackage("com.duo", "Duo")
        )
        val plan = AppCatalogReconcile.reconcile(emptyList(), installed, emptyMap(), categorize)
        assertEquals(1, plan.toInsert.size)
    }

    @Test
    fun grouping_splitsByRuling_countsAreTheLists_alphabetical() {
        val apps = listOf(
            row("com.zeta", "Zeta", AppBlockTier.PEEK_2M),
            row("com.alpha", "Alpha", AppBlockTier.LOCKED),
            row("com.open", "Open One", AppBlockTier.OPEN),
            row("com.beta", "Beta", AppBlockTier.PEEK_2M),
            row("com.gamma", "Gamma", AppBlockTier.LOCKED)
        )
        val rules = apps.groupedByRule()
        assertEquals(listOf("Beta", "Zeta"), rules.peek.map { it.appName })
        assertEquals(listOf("Alpha", "Gamma"), rules.shut.map { it.appName })
        // Audit R1 tests-1 #5: peekCount/shutCount assertions were
        // deleted here — NOT as a downgrade, but because they
        // asserted nothing: AppRules' counts are `get() = list.size`
        // of the very lists asserted above, so they could only ever
        // agree with themselves. They are REPLACED by assertions on
        // actual content the counts never touched: the packages and
        // tiers inside each group, and the OPEN app's exclusion.
        assertEquals(listOf("com.beta", "com.zeta"), rules.peek.map { it.packageName })
        assertEquals(listOf("com.alpha", "com.gamma"), rules.shut.map { it.packageName })
        assertTrue(rules.peek.all { it.resolvedTier == AppBlockTier.PEEK_2M })
        assertTrue(rules.shut.all { it.resolvedTier == AppBlockTier.LOCKED })
        assertTrue(rules.peek.none { it.packageName == "com.open" })
        assertTrue(rules.shut.none { it.packageName == "com.open" })
    }

    @Test
    fun grouping_noDecisionsIsEmpty() {
        val rules = listOf(row("com.open", "Open One", AppBlockTier.OPEN)).groupedByRule()
        assertTrue(rules.isEmpty)
        // Count assertions removed for the same reason as above
        // (derived getters, tautological); the content claim of
        // this case is that BOTH groups are actually empty.
        assertTrue(rules.peek.isEmpty())
        assertTrue(rules.shut.isEmpty())
    }
}
