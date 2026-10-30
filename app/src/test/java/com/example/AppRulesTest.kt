package com.example

import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.AppRules
import com.example.data.BlockedAppEntity
import com.example.data.groupedByRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Issue #35: AppRules' guarantees, proven as pure logic —
 * total classification (nothing drops silently), a real total
 * order, corrupt tiers never laundered into decisions,
 * de-duplication with bucket disjointness, and counts that are
 * the bucket list sizes themselves.
 */
class AppRulesTest {

    private fun row(
        pkg: String,
        name: String,
        tier: AppBlockTier,
        isBlocked: Boolean = tier != AppBlockTier.OPEN
    ) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = isBlocked,
        blockTier = tier.name
    )

    private fun corruptRow(pkg: String, name: String, isBlocked: Boolean) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = isBlocked,
        blockTier = "PEEK_2M_TYPO"
    )

    @Test
    fun emptyInput_allBucketsEmpty_countsZero() {
        val rules = emptyList<BlockedAppEntity>().groupedByRule()
        assertTrue(rules.isEmpty)
        assertEquals(0, rules.accountedCount)
        assertEquals(0, rules.peekCount + rules.shutCount + rules.openCount + rules.corruptCount)
    }

    @Test
    fun allShut_everyAppInShut_noneElsewhere() {
        val apps = listOf(
            row("com.b", "Beta", AppBlockTier.LOCKED),
            row("com.a", "Alpha", AppBlockTier.LOCKED)
        )
        val rules = apps.groupedByRule()
        assertEquals(listOf("Alpha", "Beta"), rules.shut.map { it.appName })
        assertEquals(2, rules.shutCount)
        assertEquals(2, rules.accountedCount)
        assertTrue(rules.peek.isEmpty() && rules.open.isEmpty() && rules.corrupt.isEmpty())
        assertFalse(rules.isEmpty)
    }

    @Test
    fun allOpen_countedAsOpen_neverVanished_noDecisions() {
        val apps = listOf(
            row("com.a", "Alpha", AppBlockTier.OPEN),
            row("com.b", "Beta", AppBlockTier.OPEN)
        )
        val rules = apps.groupedByRule()
        assertEquals(2, rules.openCount)
        assertEquals(2, rules.accountedCount)
        assertEquals(0, rules.peekCount + rules.shutCount)
        assertTrue(rules.isEmpty)
    }

    @Test
    fun duplicates_sameTier_countedOnce() {
        val app = row("com.dup", "Dupe", AppBlockTier.PEEK_2M)
        val rules = listOf(app, app.copy(), app.copy()).groupedByRule()
        assertEquals(1, rules.peekCount)
        assertEquals(1, rules.accountedCount)
    }

    @Test
    fun duplicates_contradictoryTiers_strictestWins_exactlyOneBucket() {
        val apps = listOf(
            row("com.dup", "Dupe", AppBlockTier.OPEN),
            row("com.dup", "Dupe", AppBlockTier.PEEK_2M),
            row("com.dup", "Dupe", AppBlockTier.LOCKED)
        )
        val rules = apps.groupedByRule()
        assertEquals(1, rules.accountedCount)
        assertEquals(listOf("com.dup"), rules.shut.map { it.packageName })
        assertTrue(rules.peek.isEmpty() && rules.open.isEmpty())
    }

    @Test
    fun corruptTier_blockedRow_isNotAFakeShutDecision() {
        // resolvedTier would launder this into LOCKED via isBlocked;
        // AppRules must file it as corrupt instead.
        val bad = corruptRow("com.bad", "Bad", isBlocked = true)
        assertEquals(AppBlockTier.LOCKED, bad.resolvedTier)
        val rules = listOf(bad).groupedByRule()
        assertEquals(1, rules.corruptCount)
        assertEquals(0, rules.shutCount)
        assertEquals(1, rules.accountedCount)
        assertFalse(rules.isEmpty)
    }

    @Test
    fun corruptTier_unblockedRow_isNotAFakeOpenDecision() {
        val bad = corruptRow("com.bad", "Bad", isBlocked = false)
        assertEquals(AppBlockTier.OPEN, bad.resolvedTier)
        val rules = listOf(bad).groupedByRule()
        assertEquals(1, rules.corruptCount)
        assertEquals(0, rules.openCount)
    }

    @Test
    fun corruptCopy_losesToValidCopyOfSamePackage() {
        val apps = listOf(
            corruptRow("com.dup", "Dupe", isBlocked = true),
            row("com.dup", "Dupe", AppBlockTier.PEEK_2M)
        )
        val rules = apps.groupedByRule()
        assertEquals(1, rules.peekCount)
        assertEquals(0, rules.corruptCount)
        assertEquals(1, rules.accountedCount)
    }

    @Test
    fun totalOrder_sameDisplayName_tieBreaksByPackage_regardlessOfInputOrder() {
        val a = row("com.clone.a", "Clone", AppBlockTier.LOCKED)
        val b = row("com.clone.b", "clone", AppBlockTier.LOCKED)
        val forward = listOf(b, a).groupedByRule().shut.map { it.packageName }
        val reverse = listOf(a, b).groupedByRule().shut.map { it.packageName }
        assertEquals(listOf("com.clone.a", "com.clone.b"), forward)
        assertEquals(forward, reverse)
    }

    @Test
    fun totalOrder_caseInsensitiveAlphabetical() {
        val apps = listOf(
            row("com.z", "zebra", AppBlockTier.PEEK_2M),
            row("com.a", "Apple", AppBlockTier.PEEK_2M),
            row("com.m", "mango", AppBlockTier.PEEK_2M)
        )
        assertEquals(
            listOf("Apple", "mango", "zebra"),
            apps.groupedByRule().peek.map { it.appName }
        )
    }

    @Test
    fun accounting_mixedInput_everyDistinctPackageCountedExactlyOnce() {
        val apps = listOf(
            row("com.p", "Peek", AppBlockTier.PEEK_2M),
            row("com.s", "Shut", AppBlockTier.LOCKED),
            row("com.o", "Open", AppBlockTier.OPEN),
            corruptRow("com.c", "Corrupt", isBlocked = true),
            row("com.p", "Peek", AppBlockTier.PEEK_2M)
        )
        val rules = apps.groupedByRule()
        assertEquals(4, rules.accountedCount)
        assertEquals(rules.peek.size, rules.peekCount)
        assertEquals(rules.shut.size, rules.shutCount)
        assertEquals(rules.open.size, rules.openCount)
        assertEquals(rules.corrupt.size, rules.corruptCount)
        val allPkgs = (rules.peek + rules.shut + rules.open + rules.corrupt).map { it.packageName }
        assertEquals(allPkgs.size, allPkgs.toSet().size)
    }

    @Test
    fun constructor_rejectsOverlappingBuckets() {
        val app = row("com.x", "X", AppBlockTier.LOCKED)
        try {
            AppRules(peek = listOf(app), shut = listOf(app))
            fail("overlapping buckets must be rejected")
        } catch (expected: IllegalArgumentException) {
            // disjointness enforced by construction
        }
    }
}
