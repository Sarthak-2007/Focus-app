package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DayNoteEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.flow.first
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
 * Two pre-merge fixes, proven at the repository:
 *
 * 1. SEED EXCISION (Lie Register 3, F-3). The fabricated demo
 *    history — three invented sessions, two invented intercept
 *    logs, two invented captured notifications — is struck from
 *    this lane's seed path exactly as trunk struck it. A fresh
 *    database seeds ONLY the lawful stores (sites, guard
 *    settings, routines, the app catalog, profiles, tasks,
 *    package sync) and the three history stores stay EMPTY.
 *
 * 2. F-SCH-1 DRAIN. Declared day words captured in the prefs
 *    file `monolith_day_words` (the Drawer lane's DayWordStore
 *    contract: dateIso → word) drain into the `day_notes` Room
 *    table at seed-sync. Only the closed word list drains —
 *    REST / ILL / FAMILY. A TEST word is refused and left
 *    sitting in prefs: never stored, never silently dropped.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SeedAndDayWordDrainTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "seed_and_drain_test.db"
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    private val prefs
        get() = context.getSharedPreferences(
            MonolithRepository.DAY_WORDS_PREFS_NAME, Context.MODE_PRIVATE
        )

    @Before
    fun setup() {
        context.deleteDatabase(dbName)
        prefs.edit().clear().commit()
        db = Room.databaseBuilder(context, MonolithDatabase::class.java, dbName)
            .addMigrations(MonolithDatabase.MIGRATION_7_8, MonolithDatabase.MIGRATION_8_9)
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        // The catalog is installed-only now (Ordner lane): one app
        // exists on this "phone", so the lawful app sync has a row
        // to discover — the seed assertions below read it.
        val shadow = org.robolectric.Shadows.shadowOf(context.packageManager)
        val query = android.content.Intent(android.content.Intent.ACTION_MAIN, null)
            .addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        val info = android.content.pm.ResolveInfo().apply {
            activityInfo = android.content.pm.ActivityInfo().apply {
                packageName = "com.keep.notes"
                name = "com.keep.notes.MainActivity"
                nonLocalizedLabel = "Keep Notes"
            }
            nonLocalizedLabel = "Keep Notes"
        }
        shadow.addResolveInfoForIntent(query, info)
    }

    @After
    fun teardown() {
        db.close()
        context.deleteDatabase(dbName)
        prefs.edit().clear().commit()
    }

    @Test
    fun freshSeed_historyStoresStayEmpty_lawfulStoresSeed() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()

        // The struck fabrication: zero rows, by name.
        assertTrue(
            "no session may be seeded — history starts empty and true",
            dao.getSessionHistorySnapshot().isEmpty()
        )
        assertTrue(
            "no intercept log may be seeded",
            dao.getInterceptLogsFlow().first().isEmpty()
        )
        assertTrue(
            "no captured notification may be seeded",
            dao.getCapturedNotificationsFlow().first().isEmpty()
        )

        // The lawful seed set still lands, whole.
        assertTrue(dao.getAllSitesSnapshot().isNotEmpty())
        assertTrue(dao.getGuardSettingsSnapshot() != null)
        assertEquals(
            setOf("FASTED BLOCK", "DAY BLOCKS", "SLEEP"),
            dao.getAllRoutinesSnapshot().map { it.name }.toSet()
        )
        // Issue #96 F1: seeded routines are born disabled — a fresh
        // install auto-seals nothing until he enables them himself.
        assertTrue(
            "seeded routines must be born disabled (#96 F1)",
            dao.getAllRoutinesSnapshot().none { it.enabled }
        )
        assertTrue(dao.getAllAppsSnapshot().isNotEmpty())
        assertTrue(dao.getAllProfilesSnapshot().isNotEmpty())
        assertTrue(dao.getAllTasksSnapshot().isNotEmpty())
    }

    @Test
    fun seedSync_drainsDeclaredWords_refusesTestAndNonStrings_idempotent() = runBlocking {
        prefs.edit()
            .putString("2026-10-26", "REST")
            .putString("2026-10-27", "ILL")
            .putString("2026-10-28", "TEST")
            .putInt("2026-10-29", 42)
            .commit()

        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()

        // Only the closed list drained, in date order.
        assertEquals(
            listOf(
                DayNoteEntity("2026-10-26", "REST"),
                DayNoteEntity("2026-10-27", "ILL")
            ),
            dao.getAllDayNotesSnapshot()
        )
        // The refused entries stay in prefs, untouched and visible:
        // TEST was never stored and never silently dropped.
        assertEquals("TEST", prefs.getString("2026-10-28", null))
        assertEquals(42, prefs.getInt("2026-10-29", -1))
        assertEquals(2, prefs.all.size)

        // Second seed-sync: a strict no-op for the table and prefs.
        repository.ensureSeededAndSyncPackages()
        assertEquals(2, dao.getAllDayNotesSnapshot().size)
        assertEquals(2, prefs.all.size)
    }

    // Audit R1 #37 (HIGH): clearing a word that had drained into
    // Room used to resurrect on the next union read — the clear
    // removed only the (already drained, hence absent) prefs key
    // and no DELETE for day_notes existed. The clear must now
    // reach the table, and the word must stay gone.
    @Test
    fun clear_afterDrain_removesWordFromUnionForever() = runBlocking {
        prefs.edit().putString("2026-10-26", "REST").commit()
        repository.ensureSeededAndSyncPackages()
        val dao = db.monolithDao()
        assertEquals(
            listOf(DayNoteEntity("2026-10-26", "REST")),
            dao.getAllDayNotesSnapshot()
        )
        assertEquals("REST", repository.allDayWords()["2026-10-26"])

        val cleared = repository.declareDayWord("2026-10-26", null)

        assertTrue("the prefs removal must report its commit verdict", cleared)
        assertTrue(dao.getAllDayNotesSnapshot().isEmpty())
        assertTrue(repository.allDayWords().isEmpty())

        // And the date is declarable again afterwards.
        assertTrue(repository.declareDayWord("2026-10-26", "ILL"))
        assertEquals("ILL", repository.allDayWords()["2026-10-26"])
    }

    @Test
    fun drain_prefValueWinsOverExistingRow_captureIsTheRecord() = runBlocking {
        val dao = db.monolithDao()
        dao.insertDayNotes(listOf(DayNoteEntity("2026-10-26", "FAMILY")))
        prefs.edit().putString("2026-10-26", "REST").commit()

        repository.drainDayWordsFromPrefs()

        assertEquals(
            listOf(DayNoteEntity("2026-10-26", "REST")),
            dao.getAllDayNotesSnapshot()
        )
        assertTrue(prefs.all.isEmpty())
    }
}
