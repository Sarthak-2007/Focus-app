package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #7, Ren ruling 00:34 IST Oct 31 (Ruling 2), the repository
 * half: the one-time LEGACY OPEN reset at the end of the first
 * seed-sync after the inversion lands (LegacyOpenReset is the pure
 * selection, pinned in ShieldRulingsOct31Test). Filed by BUILDER-7
 * with the rulings landing (PR #740): the sync-path test classes
 * re-pinned to the settled world in that landing, and the reset
 * itself gained this pin so the re-pin rests on a guarded subject.
 *
 * PIN SCOPE: PLUMBING — the real repository over the real
 * (in-memory) database and the real prefs file: the reset's
 * writes, its persisted done-flag and its announcement count are
 * the contract.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LegacyOpenResetRepositoryTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        // A genuinely legacy world: no settled-world stamp — the
        // reset has NOT run on this fixture.
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun row(pkg: String, name: String, tier: AppBlockTier) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SHOPPING.name,
        isBlocked = false,
        blockTier = tier.name
    )

    private suspend fun tierOf(pkg: String): AppBlockTier =
        db.monolithDao().getAllAppsSnapshot().first { it.packageName == pkg }.resolvedTier

    private fun resetDone(): Boolean =
        context.getSharedPreferences(MonolithRepository.APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(MonolithRepository.LEGACY_OPEN_RESET_DONE_KEY, false)

    @Test
    fun reset_closesLegacyOpenRows_once_andAnnouncesTheCount() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                row("com.example.legacyopen", "Legacy Open", AppBlockTier.OPEN),
                row("com.google.android.dialer", "Phone", AppBlockTier.OPEN),
                row("com.example.peekrow", "Peek Row", AppBlockTier.PEEK_2M),
                row("com.example.shutrow", "Shut Row", AppBlockTier.LOCKED)
            )
        )

        repository.ensureSeededAndSyncPackages()

        // The ruling closes legacy OPEN rows once — his hand can
        // re-open any of them afterwards.
        assertEquals(AppBlockTier.LOCKED, tierOf("com.example.legacyopen"))
        // Never selected: the dialler floor's packages (the reset's
        // own exclusions) and rows that were never OPEN.
        assertEquals(AppBlockTier.OPEN, tierOf("com.google.android.dialer"))
        assertEquals(AppBlockTier.PEEK_2M, tierOf("com.example.peekrow"))
        assertEquals(AppBlockTier.LOCKED, tierOf("com.example.shutrow"))
        // The Sheet announcement is held, and one-time is a
        // persisted fact.
        assertTrue(repository.pendingLegacyOpenResetCount() >= 1)
        assertTrue(resetDone())

        // Never refires: dismiss the note, sync again — nothing is
        // re-closed and no new announcement is held.
        repository.clearLegacyOpenResetNote()
        repository.ensureSeededAndSyncPackages()
        assertEquals(0, repository.pendingLegacyOpenResetCount())
        assertEquals(AppBlockTier.LOCKED, tierOf("com.example.legacyopen"))
        assertTrue(resetDone())
    }

    @Test
    fun freshInstall_stampsDoneSilently_noAnnouncement() = runBlocking {
        // No staged catalogue: the first sync births the catalogue
        // itself, so nothing on this "phone" is legacy.
        repository.ensureSeededAndSyncPackages()
        assertTrue(resetDone())
        assertEquals(0, repository.pendingLegacyOpenResetCount())
    }
}
