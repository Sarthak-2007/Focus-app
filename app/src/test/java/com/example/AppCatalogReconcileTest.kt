package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
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

        // Issue #34: a prune needs TWO consecutive absences — the
        // ghosts were already absent at the previous sync.
        val plan = AppCatalogReconcile.reconcile(
            existing, installed, emptyMap(), categorize,
            previouslyAbsent = setOf("com.ghost.video", "com.gone.chat")
        )

        // Both twice-absent packages leave; the keeper stays.
        assertEquals(setOf("com.ghost.video", "com.gone.chat"), plan.toPrune.toSet())
        assertTrue(plan.pendingPrune.isEmpty())
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
        // The keeper's usage moves to the real figure; the quiet
        // row is unmeasured and keeps its figure. Both rows' stored
        // category (SOCIAL) is refreshed to the phone's truth
        // (SHOPPING per the stub) — issue #34 — while neither tier
        // is ever re-priced by a sync.
        val byPackage = plan.usageUpdates.associateBy { it.packageName }
        assertEquals("com.keeper", byPackage.getValue("com.keeper").packageName)
        assertEquals(17, byPackage.getValue("com.keeper").todayUsageMinutes)
        assertEquals(AppBlockTier.OPEN, byPackage.getValue("com.keeper").resolvedTier)
        assertEquals(9, byPackage.getValue("com.quiet").todayUsageMinutes)
        assertEquals(AppBlockTier.LOCKED, byPackage.getValue("com.quiet").resolvedTier)
        assertTrue(plan.usageUpdates.all { it.category == AppCategory.SHOPPING.name })
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

    // ------------------------------------------------------------------
    // Issue #34 — the honest-catalog repairs.
    // ------------------------------------------------------------------

    @Test
    fun reconcile_emptyInstalledListPrunesNothing() {
        val existing = listOf(
            row("com.a", "A", AppBlockTier.LOCKED),
            row("com.b", "B", AppBlockTier.PEEK_2M)
        )
        // Even with every package carrying a strike, an empty query
        // is a failed query: no prune, no new notes.
        val plan = AppCatalogReconcile.reconcile(
            existing, emptyList(), emptyMap(), categorize,
            previouslyAbsent = setOf("com.a", "com.b")
        )
        assertTrue(plan.toPrune.isEmpty())
        assertTrue(plan.pendingPrune.isEmpty())
        assertTrue(plan.toInsert.isEmpty())
    }

    @Test
    fun reconcile_firstAbsenceOnlyNotes_secondConsecutiveAbsencePrunes() {
        val existing = listOf(
            row("com.keeper", "Keeper", AppBlockTier.LOCKED),
            row("com.flaky", "Flaky", AppBlockTier.LOCKED)
        )
        val installed = listOf(InstalledPackage("com.keeper", "Keeper"))

        val first = AppCatalogReconcile.reconcile(existing, installed, emptyMap(), categorize)
        assertTrue(first.toPrune.isEmpty())
        assertEquals(listOf("com.flaky"), first.pendingPrune)

        val second = AppCatalogReconcile.reconcile(
            existing, installed, emptyMap(), categorize,
            previouslyAbsent = first.pendingPrune.toSet()
        )
        assertEquals(listOf("com.flaky"), second.toPrune)
        assertTrue(second.pendingPrune.isEmpty())
    }

    @Test
    fun reconcile_partialListCannotWipeCatalogOnFirstSighting() {
        // A truncated launcher result (1 of 4 visible) deletes
        // nothing on the sync that first produces it.
        val existing = (1..4).map { row("com.app.$it", "App $it", AppBlockTier.LOCKED) }
        val installed = listOf(InstalledPackage("com.app.1", "App 1"))
        val plan = AppCatalogReconcile.reconcile(existing, installed, emptyMap(), categorize)
        assertTrue(plan.toPrune.isEmpty())
        assertEquals(3, plan.pendingPrune.size)
    }

    @Test
    fun reconcile_rememberedRuleRestoresTierAndLimitOnReturn() {
        // "com.returned" categorizes as SHOPPING (would be born
        // OPEN/30 by default) — the remembered LOCKED/45 must win.
        val plan = AppCatalogReconcile.reconcile(
            emptyList(),
            listOf(InstalledPackage("com.returned", "Returned")),
            emptyMap(), categorize,
            rememberedRules = mapOf(
                "com.returned" to com.example.data.AppRulePreset(AppBlockTier.LOCKED, 45)
            )
        )
        val born = plan.toInsert.single()
        assertEquals(AppBlockTier.LOCKED, born.resolvedTier)
        assertEquals(45, born.dailyLimitMinutes)
        assertTrue(born.isBlocked)
    }

    @Test
    fun reconcile_realZeroUsageIsWritten_zeroIsNotImmortal() {
        val existing = listOf(row("com.keeper", "Keeper", AppBlockTier.OPEN, usage = 9))
        val installed = listOf(InstalledPackage("com.keeper", "Keeper"))
        val plan = AppCatalogReconcile.reconcile(
            existing, installed, mapOf("com.keeper" to 0), categorize
        )
        assertEquals(1, plan.usageUpdates.size)
        assertEquals(0, plan.usageUpdates.single().todayUsageMinutes)
    }

    @Test
    fun reconcile_unmeasuredPackageKeepsItsFigure() {
        // Absent from the usage map = unmeasured (e.g. no usage
        // permission), NOT a proven zero: the figure stands.
        val existing = listOf(row("com.keeper", "Keeper", AppBlockTier.OPEN, usage = 9))
        val installed = listOf(InstalledPackage("com.keeper", "Keeper"))
        val plan = AppCatalogReconcile.reconcile(existing, installed, emptyMap(), categorize)
        assertTrue(plan.usageUpdates.isEmpty())
    }

    @Test
    fun reconcile_refreshesStaleLabelAndCategory_keepsRuling() {
        val existing = listOf(row("com.keeper", "Old Name", AppBlockTier.LOCKED, usage = 3))
        // The stored category is SOCIAL (the row() helper); the
        // phone now calls it "Keeper Chat" -> MESSAGING.
        val installed = listOf(InstalledPackage("com.keeper", "Keeper Chat"))
        val plan = AppCatalogReconcile.reconcile(existing, installed, emptyMap(), categorize)
        assertEquals(1, plan.usageUpdates.size)
        val refreshed = plan.usageUpdates.single()
        assertEquals("Keeper Chat", refreshed.appName)
        assertEquals(AppCategory.MESSAGING.name, refreshed.category)
        assertEquals(AppBlockTier.LOCKED, refreshed.resolvedTier)
        assertEquals(30, refreshed.dailyLimitMinutes)
        assertEquals(3, refreshed.todayUsageMinutes)
    }

    @Test
    fun reconcile_dedupe_mainActivityLabelWins_regardlessOfOrder() {
        val main = InstalledPackage("com.duo", "Duo", isMainActivity = true)
        val shortcut = InstalledPackage("com.duo", "Duo Shortcut")
        for (installed in listOf(listOf(main, shortcut), listOf(shortcut, main))) {
            val plan = AppCatalogReconcile.reconcile(
                emptyList(), installed, emptyMap(), categorize
            )
            assertEquals(1, plan.toInsert.size)
            assertEquals("Duo", plan.toInsert.single().appName)
        }
    }

    @Test
    fun reconcile_dedupe_noMainFlag_deterministicWinner_regardlessOfOrder() {
        val a = InstalledPackage("com.duo", "Beta Label")
        val b = InstalledPackage("com.duo", "Alpha Label")
        for (installed in listOf(listOf(a, b), listOf(b, a))) {
            val plan = AppCatalogReconcile.reconcile(
                emptyList(), installed, emptyMap(), categorize
            )
            assertEquals("Alpha Label", plan.toInsert.single().appName)
        }
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
        assertEquals(2, rules.peekCount)
        assertEquals(2, rules.shutCount)
    }

    @Test
    fun grouping_noDecisionsIsEmpty() {
        val rules = listOf(row("com.open", "Open One", AppBlockTier.OPEN)).groupedByRule()
        assertTrue(rules.isEmpty)
        assertEquals(0, rules.peekCount)
        assertEquals(0, rules.shutCount)
    }
}
