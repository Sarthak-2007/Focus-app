package com.example

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCatalogReconcile
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.PruneNote
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
 * THE COLLAPSE WINDOW, proven at the repository (issue #34,
 * fresh-eyes L3): the two-strike prune was defeated by its own
 * callers — the ViewModel init syncs, its guard refresh syncs
 * AGAIN seconds later, PARK's open syncs too — so one
 * transiently-filtered cold-start query spent both strikes in a
 * single launch. These tests pin the cure:
 *
 * - (a) repeat syncs inside the window are ONE look: a
 *   transiently-partial query prunes NOTHING, however many
 *   same-launch calls repeat it;
 * - (b) two syncs separated beyond the window are two genuine
 *   looks: a truly-gone app IS pruned, its ruling is filed as a
 *   tombstone, and the tombstone restores the ruling on return.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CatalogSyncWindowTest {

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
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
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

    /** Age the last-sync stamp past the collapse window, so the
     *  next sync is a genuinely separate look, not a repeat. */
    private fun backdateLastSyncBeyondWindow() {
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(
                MonolithRepository.LAST_CATALOG_SYNC_KEY,
                System.currentTimeMillis() -
                    AppCatalogReconcile.SYNC_COLLAPSE_WINDOW_MILLIS - 60_000L
            )
            .commit()
    }

    private fun tombstones(): Set<String> =
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(MonolithRepository.REMEMBERED_RULES_KEY, emptySet())
            ?: emptySet()

    @Test
    fun twoSyncsInSameLaunch_transientPartialQuery_prunesNothing() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                storedRow("com.ghost.app", "Ghost", AppBlockTier.LOCKED),
                storedRow("com.keeper.app", "Keeper", AppBlockTier.LOCKED)
            )
        )
        // The transient look: this query momentarily cannot see
        // the ghost (filtered / other profile / warming up).
        fakeInstalled("com.keeper.app" to "Keeper")

        // The same-launch burst, as the app actually fires it:
        // ViewModel init, its guard refresh, a PARK open.
        repository.ensureSeededAndSyncPackages()
        repository.ensureSeededAndSyncPackages()
        repository.ensureSeededAndSyncPackages()

        // One look, however many calls: the ghost was noted at
        // most — never pruned — and no prune was recorded.
        assertEquals(
            setOf("com.ghost.app", "com.keeper.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertNull(repository.pendingPruneNote())
        assertTrue(tombstones().isEmpty())
    }

    @Test
    fun twoSyncsBeyondWindow_pruneTrulyGoneApp_tombstoneFiledAndRestores() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                storedRow("com.ghost.app", "Ghost", AppBlockTier.LOCKED),
                storedRow("com.keeper.app", "Keeper", AppBlockTier.LOCKED)
            )
        )
        fakeInstalled("com.keeper.app" to "Keeper")

        // First genuine look: the ghost is only noted.
        repository.ensureSeededAndSyncPackages()
        assertTrue(
            dao.getAllAppsSnapshot().any { it.packageName == "com.ghost.app" }
        )
        assertNull(repository.pendingPruneNote())

        // Second genuine look, beyond the window: the ghost is
        // truly gone, so it is pruned — with its ruling filed.
        backdateLastSyncBeyondWindow()
        repository.ensureSeededAndSyncPackages()
        assertEquals(
            setOf("com.keeper.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertEquals(
            PruneNote(appsRemoved = 1, rulesRemoved = true),
            repository.pendingPruneNote()
        )
        assertTrue(tombstones().contains("com.ghost.app|LOCKED|30"))

        // The ghost returns on a later look: the tombstone, not a
        // factory/category default, decides its rebirth.
        fakeInstalled("com.ghost.app" to "Ghost")
        backdateLastSyncBeyondWindow()
        repository.ensureSeededAndSyncPackages()
        val reborn = dao.getAllAppsSnapshot()
            .first { it.packageName == "com.ghost.app" }
        assertEquals(AppBlockTier.LOCKED, reborn.resolvedTier)
        assertEquals(30, reborn.dailyLimitMinutes)
    }
}
