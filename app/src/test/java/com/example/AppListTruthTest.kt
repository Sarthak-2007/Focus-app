package com.example

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.PruneNote
import com.example.data.groupedByRule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * THE APP LIST'S TRUTH, proven end to end at the repository
 * (Ordner lane — Sarthak's complaint, Oct 29 22:40 IST: the list
 * held apps he never installed):
 *
 * - after two genuinely separate syncs (the two-strike law,
 *   issue #34) the table equals the faked installed set exactly,
 *   including the stored-rule-for-an-uninstalled-app case — and
 *   a pruned ruling is remembered and restored if the app
 *   returns (STOSS-34 surge wording, adopted in the merge);
 * - the man's own tier on a surviving app is never re-priced;
 * - an empty package query (the query failed) prunes NOTHING;
 * - the prune records itself for the one-time strip (Mikro M5);
 * - a tier tap persists.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppListTruthTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** The faked package source: these packages, and only these,
     *  answer the launcher query on this "phone". */
    private fun fakeInstalled(vararg apps: Pair<String, String>) {
        val shadow = Shadows.shadowOf(context.packageManager)
        val query = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        for ((pkg, label) in apps) {
            val info = ResolveInfo().apply {
                activityInfo = ActivityInfo().apply {
                    packageName = pkg
                    name = "$pkg.MainActivity"
                    nonLocalizedLabel = label
                }
                nonLocalizedLabel = label
            }
            shadow.addResolveInfoForIntent(query, info)
        }
    }

    private fun storedRow(pkg: String, name: String, tier: AppBlockTier) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SOCIAL.name,
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name,
        dailyLimitMinutes = 30,
        todayUsageMinutes = 0,
        interceptCount = 0
    )

    /** The persisted strike set (issue #34 two-strike law). */
    private fun strikes(): Set<String> =
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(MonolithRepository.ABSENT_ONCE_KEY, emptySet())
            ?: emptySet()

    /** Make the next sync a genuinely separate look (L3): clear
     *  the last-sync stamp so the collapse window cannot fold it
     *  into the previous look. */
    private fun nextGenuineLook() {
        context.getSharedPreferences(
            MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE
        ).edit().putLong(MonolithRepository.LAST_CATALOG_SYNC_KEY, 0L).commit()
    }

    @Test
    fun sync_tableEqualsInstalledSet_ghostPruned_userTierSurvives_pruneRecorded() = runBlocking {
        val dao = db.monolithDao()
        // The rot, staged: a rule for an app that is not on the
        // phone, and his own SHUT on an app that is.
        dao.insertApps(
            listOf(
                storedRow("com.ghost.app", "Ghost", AppBlockTier.LOCKED),
                storedRow("com.keeper.app", "Keeper", AppBlockTier.LOCKED)
            )
        )
        fakeInstalled(
            "com.keeper.app" to "Keeper",
            "com.fresh.chat" to "Fresh Chat"
        )
        assertNull(repository.pendingPruneNote())

        // REPAIR 2 (verifier) — the two-strike law, stated in the
        // test instead of the old single-sync law it used to
        // encode. RUN 1: the ghost earns a strike and is STILL
        // LISTED; nothing is pruned, nothing is noted.
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            setOf("com.ghost.app", "com.keeper.app", "com.fresh.chat"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertEquals(setOf("com.ghost.app"), strikes())
        assertNull(repository.pendingPruneNote())

        // RUN 2, a genuinely separate look (L3 window cleared):
        // second consecutive absence — the ghost is pruned and
        // the prune records itself.
        nextGenuineLook()
        repository.ensureSeededAndSyncPackages()

        val table = dao.getAllAppsSnapshot()
        assertEquals(
            setOf("com.keeper.app", "com.fresh.chat"),
            table.map { it.packageName }.toSet()
        )
        // His SHUT on the keeper survived the sync untouched.
        assertEquals(
            AppBlockTier.LOCKED,
            table.first { it.packageName == "com.keeper.app" }.resolvedTier
        )
        assertEquals(
            "Fresh Chat",
            table.first { it.packageName == "com.fresh.chat" }.appName
        )
        // The prune recorded itself — one app, and a ruling went
        // with it (the ghost was SHUT).
        assertEquals(PruneNote(appsRemoved = 1, rulesRemoved = true), repository.pendingPruneNote())

        // The ghost returns on a later genuine look: it is reborn
        // with its REMEMBERED ruling (SHUT), never a silent OPEN
        // (segment adopted from the STOSS-34 surge in the merge,
        // made window-aware like the rest of this test).
        fakeInstalled("com.ghost.app" to "Ghost")
        nextGenuineLook()
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            AppBlockTier.LOCKED,
            dao.getAllAppsSnapshot().first { it.packageName == "com.ghost.app" }.resolvedTier
        )
    }

    @Test
    fun sync_emptyQueryResult_prunesNothing() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(listOf(storedRow("com.only.app", "Only", AppBlockTier.PEEK_2M)))

        // DISTINGUISHING CONTROL (audit R1 tests-1 #3): the old
        // version of this test could not tell "the empty-query
        // guard worked" from "the entire sync silently failed" —
        // both leave the table untouched. So first, in this same
        // harness, a sync whose query DOES answer must visibly
        // work: it discovers the control app, keeps the stored
        // one, and records no prune. The pipeline (launcher query
        // → reconcile → DAO) is proven alive before it is trusted.
        fakeInstalled(
            "com.only.app" to "Only",
            "com.control.app" to "Control"
        )
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            "control sync must discover the control app and keep the stored one",
            setOf("com.only.app", "com.control.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertNull(repository.pendingPruneNote())

        // Now make the launcher query TRULY empty and verify that
        // as a direct fact (not an assumption): both faked answers
        // are removed from the shadow package manager, and the
        // exact query the repository issues returns nothing.
        val shadow = Shadows.shadowOf(context.packageManager)
        val query = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        shadow.removeResolveInfosForIntent(query, "com.only.app")
        shadow.removeResolveInfosForIntent(query, "com.control.app")
        // Verifiably empty AS THE REPOSITORY SEES IT: the raw
        // query may still answer the app's own launcher activity
        // (and com.android.settings), which the sync filters out
        // before reconciling — what must be empty is the
        // post-filter installed set the reconcile would consume.
        val postFilterInstalled = context.packageManager.queryIntentActivities(query, 0)
            .mapNotNull { it.activityInfo?.packageName }
            .filter { it != context.packageName && !it.startsWith("com.android.settings") }
        assertTrue(
            "the second run's installed set must be verifiably empty, was $postFilterInstalled",
            postFilterInstalled.isEmpty()
        )
        // A post-catalog effect as the second run's pulse: the
        // sync seeds default tasks AFTER the catalog block, so
        // delete every seeded task — if the second run silently
        // dies anywhere before its end, they stay deleted.
        dao.getAllTasksSnapshot().forEach { dao.deleteTaskById(it.id) }
        assertTrue(dao.getAllTasksSnapshot().isEmpty())

        repository.ensureSeededAndSyncPackages()

        // The run demonstrably ran to completion (tasks re-seeded
        // past the catalog block)…
        assertTrue(
            "the empty-query run must execute past the catalog block",
            dao.getAllTasksSnapshot().isNotEmpty()
        )
        // …its input was verifiably empty (asserted above), and
        // still nothing was pruned: only a working empty-query
        // guard explains all three facts together. Remove the
        // guard and both rows are pruned; kill the run and the
        // tasks never come back.
        assertEquals(
            "empty-query run must prune nothing",
            setOf("com.only.app", "com.control.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertNull(repository.pendingPruneNote())
    }

    @Test
    fun sync_secondRun_idempotent_noNewPruneRecord() = runBlocking {
        // REPAIR 2 (verifier): this test's name states its truth
        // intent — a settled catalog does not change or re-note
        // on repeat syncs. Under the old single-sync law that
        // settled state existed after run 1; under the two-strike
        // law it exists after run 2 (run 1 only strikes the
        // ghost, run 2 prunes it). The idempotence claim is
        // therefore re-aimed at RUN 3, with runs 1–2 asserting
        // the new law on the way — no assertion deleted, the
        // intermediate law now pinned instead of assumed away.
        val dao = db.monolithDao()
        dao.insertApps(listOf(storedRow("com.ghost.app", "Ghost", AppBlockTier.LOCKED)))
        fakeInstalled("com.keeper.app" to "Keeper")

        // Run 1: ghost still listed, strike recorded, no prune.
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            setOf("com.ghost.app", "com.keeper.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertEquals(setOf("com.ghost.app"), strikes())
        assertNull(repository.pendingPruneNote())

        // Run 2 (genuine second look): ghost pruned, note filed.
        nextGenuineLook()
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            setOf("com.keeper.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertEquals(
            PruneNote(appsRemoved = 1, rulesRemoved = true),
            repository.pendingPruneNote()
        )

        // Run 3 (another genuine look): the settled catalog is
        // idempotent — same table, no new prune record.
        repository.clearPruneNote()
        val settled = dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        nextGenuineLook()
        repository.ensureSeededAndSyncPackages()
        assertEquals(settled, dao.getAllAppsSnapshot().map { it.packageName }.toSet())
        assertNull(repository.pendingPruneNote())
    }

    @Test
    fun tierTap_persists() = runBlocking {
        val dao = db.monolithDao()
        fakeInstalled("com.keeper.app" to "Keeper")
        repository.ensureSeededAndSyncPackages()
        val born = dao.getAllAppsSnapshot().first { it.packageName == "com.keeper.app" }

        // The sled's commit path, at the repository: set, then read back.
        repository.setAppBlockTier(born.packageName, AppBlockTier.LOCKED)
        val shut = dao.getAllAppsSnapshot().first { it.packageName == born.packageName }
        assertEquals(AppBlockTier.LOCKED, shut.resolvedTier)
        assertTrue(shut.isBlocked)

        repository.setAppBlockTier(born.packageName, AppBlockTier.OPEN)
        val opened = dao.getAllAppsSnapshot().first { it.packageName == born.packageName }
        assertEquals(AppBlockTier.OPEN, opened.resolvedTier)
        assertTrue(!opened.isBlocked)

        // And the cycle path the Deck uses lands where it should.
        val cycled = repository.cycleAppBlockTier(opened)
        assertEquals(AppBlockTier.LOCKED, cycled)
        assertEquals(
            AppBlockTier.LOCKED,
            dao.getAllAppsSnapshot().first { it.packageName == born.packageName }.resolvedTier
        )
    }

    @Test
    fun rulesGrouping_readsTheSyncedTable() = runBlocking {
        val dao = db.monolithDao()
        fakeInstalled(
            "com.one.app" to "One",
            "com.two.app" to "Two",
            "com.three.app" to "Three"
        )
        repository.ensureSeededAndSyncPackages()
        repository.setAppBlockTier("com.one.app", AppBlockTier.PEEK_2M)
        repository.setAppBlockTier("com.two.app", AppBlockTier.LOCKED)

        val rules = dao.getAllAppsSnapshot().groupedByRule()
        assertEquals(listOf("One"), rules.peek.map { it.appName })
        assertEquals(listOf("Two"), rules.shut.map { it.appName })
        // Audit R1 tests-1 #5 (sibling site): the peekCount /
        // shutCount assertions are replaced, not merely dropped —
        // the counts are derived getters (`list.size`) over the
        // lists just asserted, so they checked nothing. Content
        // carries the claim instead: packages, tiers, and the
        // untouched third app's exclusion from both groups.
        assertEquals(listOf("com.one.app"), rules.peek.map { it.packageName })
        assertEquals(listOf("com.two.app"), rules.shut.map { it.packageName })
        assertTrue(rules.peek.all { it.resolvedTier == AppBlockTier.PEEK_2M })
        assertTrue(rules.shut.all { it.resolvedTier == AppBlockTier.LOCKED })
        assertTrue(rules.peek.none { it.packageName == "com.three.app" })
        assertTrue(rules.shut.none { it.packageName == "com.three.app" })
    }
}
