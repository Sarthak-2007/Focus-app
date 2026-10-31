package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.BlockedSiteEntity
import com.example.data.FocusProfileEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.SoundEffectsSetting
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #253 lines 8 + 9: the full-context profile, applied.
 *
 *  - line 8: a per-app tier saved in the profile BEATS the
 *    category stencil (an OPEN entry is the per-app exception;
 *    a LOCKED entry rules an app whose category is not listed),
 *    per-app limits land, and captureCurrentAsProfile records
 *    the standing state so re-applying it reproduces it.
 *  - line 9: the profile's site set replaces the standing set,
 *    and the facts it rules (sound / sniper / browser blocker)
 *    land through their stores; a profile that rules nothing
 *    (every legacy row) leaves sites and facts untouched.
 *  - the undo snapshot restores the bundle half too.
 *
 * PIN: SOURCE (repository apply/capture/undo semantics over a
 * real in-memory Room).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileBundleApplyTest {

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

    private fun app(pkg: String, category: AppCategory, tier: AppBlockTier, limit: Int = 30) =
        BlockedAppEntity(
            packageName = pkg,
            appName = pkg,
            category = category.name,
            isBlocked = tier != AppBlockTier.OPEN,
            blockTier = tier.name,
            dailyLimitMinutes = limit
        )

    private fun profile(
        categories: String = "",
        tiers: String = "",
        limits: String = "",
        sites: String = "",
        sound: Boolean? = null,
        sniper: Boolean? = null,
        browser: Boolean? = null
    ) = FocusProfileEntity(
        name = "Bundle",
        codeName = "BUNDLE",
        description = "test",
        defaultMinutes = 50,
        blockedCategoriesCsv = categories,
        scheduleLabel = "",
        isScheduleEnabled = false,
        startHour = 0,
        startMinute = 0,
        endHour = 0,
        endMinute = 0,
        appTiersCsv = tiers,
        appLimitsCsv = limits,
        sitesCsv = sites,
        soundEnabled = sound,
        sniperEnabled = sniper,
        browserBlockEnabled = browser
    )

    private suspend fun tierOf(pkg: String): AppBlockTier =
        db.monolithDao().getAllAppsSnapshot().first { it.packageName == pkg }.resolvedTier

    private suspend fun limitOf(pkg: String): Int =
        db.monolithDao().getAllAppsSnapshot().first { it.packageName == pkg }.dailyLimitMinutes

    @Test
    fun perAppTierBeatsStencil_andLimitLands() = runTest {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                // SOCIAL is stencil-listed (would LOCK), but the
                // profile's saved ruling says OPEN — the exception.
                app("com.social.app", AppCategory.SOCIAL, AppBlockTier.OPEN),
                // EDUCATION is not listed (stencil OPEN), but the
                // saved ruling says LOCKED.
                app("com.edu.app", AppCategory.EDUCATION, AppBlockTier.OPEN),
                // Untouched by the bundle: pure stencil behaviour.
                app("com.game.app", AppCategory.GAMES, AppBlockTier.OPEN)
            )
        )
        repository.applyProfile(
            profile(
                categories = "SOCIAL,GAMES",
                tiers = "com.social.app=OPEN,com.edu.app=LOCKED",
                limits = "com.game.app=15"
            )
        )
        assertEquals(AppBlockTier.OPEN, tierOf("com.social.app"))
        assertEquals(AppBlockTier.LOCKED, tierOf("com.edu.app"))
        assertEquals(AppBlockTier.LOCKED, tierOf("com.game.app"))
        assertEquals(15, limitOf("com.game.app"))
    }

    @Test
    fun sitesAndFactsLand_legacyProfileLeavesThem() = runTest {
        val dao = db.monolithDao()
        dao.insertApps(listOf(app("com.a", AppCategory.SOCIAL, AppBlockTier.OPEN)))
        dao.insertSites(listOf(BlockedSiteEntity("old.com"), BlockedSiteEntity("keep.com")))
        SoundEffectsSetting.setEnabled(context, true)
        repository.setShortsReelsSniperEnabled(false)
        repository.setBrowserBlockerEnabled(true)

        // Legacy profile: no bundle — sites and facts untouched.
        repository.applyProfile(profile(categories = "SOCIAL"))
        assertEquals(
            setOf("old.com", "keep.com"),
            dao.getAllSitesSnapshot().map { it.domain }.toSet()
        )
        assertTrue(SoundEffectsSetting.isEnabled(context))
        assertFalse(repository.isShortsReelsSniperEnabled())
        assertTrue(repository.isBrowserBlockerEnabled())

        // Bundled profile: site set replaced, facts written.
        repository.applyProfile(
            profile(
                categories = "SOCIAL",
                sites = "keep.com,new.com",
                sound = false,
                sniper = true,
                browser = false
            )
        )
        assertEquals(
            setOf("keep.com", "new.com"),
            dao.getAllSitesSnapshot().map { it.domain }.toSet()
        )
        assertFalse(SoundEffectsSetting.isEnabled(context))
        assertTrue(repository.isShortsReelsSniperEnabled())
        assertFalse(repository.isBrowserBlockerEnabled())
    }

    @Test
    fun captureThenApply_reproducesTheState_andUndoRestoresTheBundle() = runTest {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                app("com.social.app", AppCategory.SOCIAL, AppBlockTier.LOCKED, limit = 20),
                app("com.edu.app", AppCategory.EDUCATION, AppBlockTier.PEEK_2M, limit = 45)
            )
        )
        dao.insertSites(listOf(BlockedSiteEntity("a.com")))
        SoundEffectsSetting.setEnabled(context, false)
        repository.setShortsReelsSniperEnabled(true)

        val saved = repository.captureCurrentAsProfile("Snapshot test")
        assertTrue(saved.id > 0)
        assertTrue(saved.appTiersCsv.contains("com.social.app=LOCKED"))

        // Scramble the state, then apply the snapshot: the
        // captured context returns.
        dao.insertApps(
            listOf(
                app("com.social.app", AppCategory.SOCIAL, AppBlockTier.OPEN, limit = 99),
                app("com.edu.app", AppCategory.EDUCATION, AppBlockTier.OPEN, limit = 99)
            )
        )
        dao.insertSites(listOf(BlockedSiteEntity("b.com")))
        SoundEffectsSetting.setEnabled(context, true)
        repository.setShortsReelsSniperEnabled(false)

        repository.applyProfile(saved)
        assertEquals(AppBlockTier.LOCKED, tierOf("com.social.app"))
        assertEquals(20, limitOf("com.social.app"))
        assertEquals(AppBlockTier.PEEK_2M, tierOf("com.edu.app"))
        assertEquals(45, limitOf("com.edu.app"))
        assertEquals(setOf("a.com"), dao.getAllSitesSnapshot().map { it.domain }.toSet())
        assertFalse(SoundEffectsSetting.isEnabled(context))
        assertTrue(repository.isShortsReelsSniperEnabled())

        // Undo restores the scrambled (pre-apply) bundle state.
        assertTrue(repository.restoreProfileUndoSnapshot())
        assertEquals(AppBlockTier.OPEN, tierOf("com.social.app"))
        assertEquals(99, limitOf("com.social.app"))
        assertEquals(setOf("a.com", "b.com"), dao.getAllSitesSnapshot().map { it.domain }.toSet())
        assertTrue(SoundEffectsSetting.isEnabled(context))
        assertFalse(repository.isShortsReelsSniperEnabled())
    }
}
