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
 *   issue #34) the table equals the faked installed set exactly
 *   for UNRULED rows — and a pruned row's stored state is
 *   remembered and restored if the app returns (STOSS-34 surge
 *   wording, adopted in the merge). Since issue #142 F2 (the
 *   union law) a row carrying a RULING is catalogue membership
 *   by right and is never pruned for absence — discovery cannot
 *   distinguish a truly uninstalled app from an installed but
 *   launcher-invisible one, and the ruled row is the man's only
 *   findable record of his ruling; that half is pinned by
 *   sync_ruledRowForLauncherInvisibleApp_survivesEverySync;
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
        // The rot, staged: an UNRULED row for an app that is not
        // on the phone, and his own SHUT on an app that is. (The
        // ghost carried a ruling when this test was first written;
        // since issue #142 F2's union law a ruled row never prunes,
        // so the prune mechanics are staged on the unruled row —
        // the ruled half is pinned by the dedicated union test
        // below.)
        dao.insertApps(
            listOf(
                storedRow("com.ghost.app", "Ghost", AppBlockTier.OPEN),
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
        // The prune recorded itself — one app, and NO ruling went
        // with it (the ghost was unruled; under the union law a
        // ruling can no longer leave this way at all).
        assertEquals(PruneNote(appsRemoved = 1, rulesRemoved = false), repository.pendingPruneNote())

        // The ghost returns on a later genuine look: it is reborn
        // with its REMEMBERED state (OPEN, as filed when it was
        // pruned), never re-priced by the category guess
        // (segment adopted from the STOSS-34 surge in the merge,
        // made window-aware like the rest of this test).
        fakeInstalled("com.ghost.app" to "Ghost")
        nextGenuineLook()
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            AppBlockTier.OPEN,
            dao.getAllAppsSnapshot().first { it.packageName == "com.ghost.app" }.resolvedTier
        )
    }

    @Test
    fun sync_ruledRowForLauncherInvisibleApp_survivesEverySync() = runBlocking {
        // Issue #142 F2 — THE UNION LAW, end to end. The staged
        // phone holds a SHUT ruling for an app the launcher query
        // cannot see (installed but launcher-invisible: the exact
        // state discovery cannot tell apart from uninstalled).
        // Sync after genuine sync, the row must stay in the
        // catalogue with its tier intact — findable, its ruling
        // visible — and no strike or prune note may ever be filed
        // against it.
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                storedRow("com.hidden.app", "Hidden", AppBlockTier.LOCKED),
                storedRow("com.keeper.app", "Keeper", AppBlockTier.LOCKED)
            )
        )
        fakeInstalled("com.keeper.app" to "Keeper")

        repeat(3) {
            repository.ensureSeededAndSyncPackages()
            val table = dao.getAllAppsSnapshot()
            assertEquals(
                setOf("com.hidden.app", "com.keeper.app"),
                table.map { it.packageName }.toSet()
            )
            assertEquals(
                AppBlockTier.LOCKED,
                table.first { it.packageName == "com.hidden.app" }.resolvedTier
            )
            assertTrue(strikes().isEmpty())
            assertNull(repository.pendingPruneNote())
            nextGenuineLook()
        }
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
        // A post-catalog effect as the second run's pulse. The
        // original pulse deleted the seeded tasks and expected
        // the run to re-seed them — an instrument the seed law
        // itself repealed: tasks are seeded once-ever
        // (SEED_TASKS_KEY; Audit R2 #96 — a store that carried
        // content is never resurrected, an emptied store is the
        // user's own emptying, honoured), so no later run can
        // re-seed them and that pulse read dead on every run.
        // The pulse now rides the function's LAST step instead:
        // the day-word drain (F-SCH-1) runs at the tail of the
        // same pass under no ever-flag. Stage a lawful word; if
        // the second run dies anywhere before its end, the word
        // never lands in day_notes.
        val wordPrefs = context.getSharedPreferences(
            MonolithRepository.DAY_WORDS_PREFS_NAME, Context.MODE_PRIVATE
        )
        wordPrefs.edit().putString("2099-01-01", "REST").commit()
        assertTrue(dao.getAllDayNotesSnapshot().none { it.dateIso == "2099-01-01" })

        repository.ensureSeededAndSyncPackages()

        // The run demonstrably ran to completion (the staged word
        // drained at the function's tail, past the catalog block)…
        assertTrue(
            "the empty-query run must execute past the catalog block",
            dao.getAllDayNotesSnapshot().any { it.dateIso == "2099-01-01" && it.word == "REST" }
        )
        // …its input was verifiably empty (asserted above), and
        // still nothing was pruned: only a working empty-query
        // guard explains all three facts together. Remove the
        // guard and both rows are pruned; kill the run and the
        // staged word never drains.
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
        // The ghost is UNRULED: since issue #142 F2's union law
        // only unruled rows can prune at all.
        dao.insertApps(listOf(storedRow("com.ghost.app", "Ghost", AppBlockTier.OPEN)))
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
            PruneNote(appsRemoved = 1, rulesRemoved = false),
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
        // THE INVERSION (issue #129, Shield Rethink 2): an app no
        // remembered ruling, factory preset or category policy
        // claims is born LOCKED — shut until he lists it
        // otherwise in the catalogue; a blocker that defaults
        // unknown apps to open fails its one job. "Three" was
        // never ruled, so the synced table carries it LOCKED by
        // birth, and the grouping — which reads stored tiers
        // only — seats it in shut beside his own LOCKED ruling
        // on "Two" (ledger order is by display name, so Three
        // precedes Two). This test's pre-inversion form expected
        // Three in neither group, under the born-OPEN law the
        // inversion repealed; the census read [Three, Two]
        // because production obeys the current law.
        assertEquals(listOf("Three", "Two"), rules.shut.map { it.appName })
        // Audit R1 tests-1 #5 (sibling site): the peekCount /
        // shutCount assertions are replaced, not merely dropped —
        // the counts are derived getters (`list.size`) over the
        // lists just asserted, so they checked nothing. Content
        // carries the claim instead: packages, tiers, and the
        // untouched third app's exclusion from both groups.
        assertEquals(listOf("com.one.app"), rules.peek.map { it.packageName })
        assertEquals(listOf("com.three.app", "com.two.app"), rules.shut.map { it.packageName })
        assertTrue(rules.peek.all { it.resolvedTier == AppBlockTier.PEEK_2M })
        assertTrue(rules.shut.all { it.resolvedTier == AppBlockTier.LOCKED })
        assertTrue(rules.peek.none { it.packageName == "com.three.app" })
        // Three's shut seat is a BIRTH, not a ruling: its stored
        // tier stands LOCKED with no setAppBlockTier call ever
        // made on it, while Two's LOCKED is the ruling above —
        // the grouping reads both off the same synced table,
        // which is the claim this test exists to guard. And a
        // born-LOCKED app is no OPEN app under any reading.
        assertEquals(
            AppBlockTier.LOCKED,
            dao.getAllAppsSnapshot().first { it.packageName == "com.three.app" }.resolvedTier
        )
        assertTrue(rules.open.none { it.packageName == "com.three.app" })
    }
}
