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
 * - after a sync the table equals the faked installed set exactly,
 *   including the stored-rule-for-an-uninstalled-app case;
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
    }

    @Test
    fun sync_emptyQueryResult_prunesNothing() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(listOf(storedRow("com.only.app", "Only", AppBlockTier.PEEK_2M)))
        // Nothing registered: the launcher query can only fail or
        // come back empty, and an empty answer must never be read
        // as "he uninstalled everything".
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            listOf("com.only.app"),
            dao.getAllAppsSnapshot().map { it.packageName }
        )
        assertNull(repository.pendingPruneNote())
    }

    @Test
    fun sync_secondRun_idempotent_noNewPruneRecord() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(listOf(storedRow("com.ghost.app", "Ghost", AppBlockTier.LOCKED)))
        fakeInstalled("com.keeper.app" to "Keeper")

        repository.ensureSeededAndSyncPackages()
        repository.clearPruneNote()
        val afterFirst = dao.getAllAppsSnapshot().map { it.packageName }.toSet()

        repository.ensureSeededAndSyncPackages()
        assertEquals(afterFirst, dao.getAllAppsSnapshot().map { it.packageName }.toSet())
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
        assertEquals(1, rules.peekCount)
        assertEquals(1, rules.shutCount)
    }
}
