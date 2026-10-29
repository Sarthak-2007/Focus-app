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
