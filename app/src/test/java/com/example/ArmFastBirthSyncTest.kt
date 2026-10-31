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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * ARM FAST, first half (issue #515 F1): the catalogue resync
 * used to run INSIDE startSession, ahead of the insert — a
 * phone-wide PackageManager + LauncherApps scan sitting in the
 * proving budget (DWELL 210 / DEADLINE 500 / ANNUL 1500), able
 * to jam L1 and annul a correctly performed arm. The cure keeps
 * the #129 F2 birth coverage but moves the resync off the
 * seal's path: the birth inserts from the standing catalogue,
 * and the resync fires async the moment the insert lands.
 *
 * Proven here with the birth-sync scope held on a test
 * dispatcher, so "not before the insert" is deterministic,
 * not a timing hope:
 * - at the moment startSession returns, the newly installed
 *   app has NO row yet and the seal's held count is the
 *   standing catalogue's alone;
 * - once the fired launch is allowed to run, the resync lands
 *   the new app's row — the #129 coverage is preserved, only
 *   its place moved.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ArmFastBirthSyncTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var db: MonolithDatabase
    private val birthDispatcher = StandardTestDispatcher()
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(
            context,
            db.monolithDao(),
            birthSyncScope = CoroutineScope(birthDispatcher)
        )
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

    @Test
    fun startSession_birthsFromStandingCatalogue_birthSyncRunsAfterNotBefore() = runBlocking {
        val dao = db.monolithDao()
        // The standing catalogue: one ruled app, already rowed.
        dao.insertApps(listOf(storedRow("com.standing.app", "Standing", AppBlockTier.LOCKED)))
        // The phone also carries a second app, installed since
        // the last sync: only a resync can give it a row.
        fakeInstalled("com.standing.app" to "Standing", "com.new.app" to "New")

        val session = repository.startSession("Intention", "Study", 25)

        // The birth read the standing catalogue alone: the new
        // app has no row yet, and the seal's held count is the
        // standing LOCKED row's — the resync was NOT awaited on
        // the seal's path (under the old shape both assertions
        // fail: the inline sync rows the new app before insert).
        assertEquals(
            setOf("com.standing.app"),
            dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        )
        assertEquals(1, session.heldAppsAtSeal)

        // The fired resync still runs — right after, not never.
        // Releasing the held dispatcher starts the launch; the
        // sync itself hops to Dispatchers.IO inside, so its
        // landing is polled with a generous bound.
        birthDispatcher.scheduler.advanceUntilIdle()
        val deadline = System.currentTimeMillis() + 15_000
        var packages = dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        while ("com.new.app" !in packages && System.currentTimeMillis() < deadline) {
            Thread.sleep(50)
            packages = dao.getAllAppsSnapshot().map { it.packageName }.toSet()
        }
        assertTrue(
            "the birth resync must land the new app's row after the insert",
            "com.new.app" in packages
        )
    }
}
