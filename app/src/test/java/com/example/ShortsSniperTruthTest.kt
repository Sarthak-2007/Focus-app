package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.MonolithAccessibilityService
import com.example.service.TactileSoundEngine
import com.example.ui.MonolithViewModel
import java.time.Duration
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock

/**
 * PIN scope: PLUMBING — its migration-chain half pins the
 * schema validation end-to-end; the toggle-truth assertions
 * are SOURCE.
 *
 * Truth Car 3 — UNA-01: SHORTS + REELS WAS A PHANTOM.
 *
 * The charge, verified in trunk before the cure: the toggle lived
 * only in the ViewModel's memory (`_shortsReelsSniperEnabled`,
 * reborn `true` at every process start), it rendered nowhere (two
 * screens took the value as a parameter and never drew it), the
 * accessibility sniper read no setting at all (it fired
 * unconditionally during sessions), and PARK's board printed the
 * interceptor's state from that same memory. Four seams, one lie.
 *
 * The cure makes the switch a persisted Guard fact, end to end,
 * mirroring the browser-blocker pair. Each test below guards one
 * seam and would fail against the old construction:
 *
 *  1. The 11→12 migration lands the column on a real v11 row with
 *     the truthful default (1 — the sniper was always on), the row
 *     otherwise intact.
 *  2. The full chain (v7 entity file → Room at v14) validates and
 *     the switch round-trips through the DAO.
 *  3. The repository read is the store, not memory: a fresh
 *     repository instance — process death's honest simulation —
 *     reads what another instance wrote.
 *  4. The service's gate reads the stored switch in both
 *     directions (and a missing row as the shipped default).
 *  5. The ViewModel's printed value is seeded FROM the store and
 *     its toggle writes TO the store: stored OFF survives the
 *     ViewModel's birth, and a flip lands in the row.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// PIN-SCOPE: PLUMBING — migration chain to v21 + settings persistence (working-rules adoption, Oct 31 18:06 IST).
class ShortsSniperTruthTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "shorts_sniper_truth_test.db"

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
    }

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
        val cols = mutableSetOf<String>()
        db.query("PRAGMA table_info($table)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) cols.add(cursor.getString(nameIdx))
        }
        return cols
    }

    /** guard_settings alone at its exact v11 shape — the only
     *  table the 11→12 migration touches — carrying a row whose
     *  values the migration must not disturb. */
    private fun createV11GuardFile() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(11) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `guard_settings` (" +
                            "`id` INTEGER NOT NULL, " +
                            "`browserBlockerEnabled` INTEGER NOT NULL, " +
                            "`routineReseedV1Done` INTEGER NOT NULL, " +
                            "`routineReseedNoteDismissed` INTEGER NOT NULL, " +
                            "`lastBootAtMillis` INTEGER NOT NULL, " +
                            "`lastRoutineTickMillis` INTEGER NOT NULL, " +
                            "`lastCompositeSessionId` INTEGER NOT NULL, " +
                            "`lastReceiptOutcome` TEXT NOT NULL, " +
                            "`receiptSequence` INTEGER NOT NULL, " +
                            "`lastReceiptSessionId` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                    )
                    db.execSQL(
                        "INSERT INTO guard_settings (id, browserBlockerEnabled, " +
                            "routineReseedV1Done, routineReseedNoteDismissed, lastBootAtMillis, " +
                            "lastRoutineTickMillis, lastCompositeSessionId, lastReceiptOutcome, " +
                            "receiptSequence, lastReceiptSessionId) " +
                            "VALUES (1, 1, 1, 0, 123, 456, 7, 'KEPT', 41, 9)"
                    )
                    // Test-floor cure: MIGRATION_11_12 also alters
                    // focus_sessions; a v11 file without that table made
                    // the migration die with SQLiteException at open.
                    // A real v11 file always carries focus_sessions —
                    // the fixture now does too (shape mirrored from
                    // BreakMigrationTest's v11).
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`intention` TEXT NOT NULL)"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config).writableDatabase.close()
    }

    @Test
    fun v11File_migration1112_shortsColumnLands_defaultTrue_rowIntact() {
        createV11GuardFile()
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(12) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(11, oldVersion)
                    assertEquals(12, newVersion)
                    MonolithDatabase.MIGRATION_11_12.migrate(db)
                }
            })
            .build()
        val db = factory.create(config).writableDatabase
        val cols = columnsOf(db, "guard_settings")
        assertTrue(cols.contains("shortsReelsSniperEnabled"))
        assertFalse("the struck test-date predicate exists nowhere", cols.contains("testDateEpochDay"))
        db.query(
            "SELECT shortsReelsSniperEnabled, receiptSequence, lastReceiptOutcome " +
                "FROM guard_settings WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            // The default IS the pre-v12 truth: the sniper was on.
            assertEquals(1, cursor.getInt(0))
            // The row's other facts are untouched by the landing.
            assertEquals(41, cursor.getInt(1))
            assertEquals("KEPT", cursor.getString(2))
        }
        db.close()
    }

    /** The full v7 entity schema (the Slice E chain test's DDL,
     *  verbatim) — Room opens it at v14 and validates everything. */
    private fun createV7EntityFile() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(7) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `blocked_apps` (`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `category` TEXT NOT NULL, `isBlocked` INTEGER NOT NULL, `blockTier` TEXT NOT NULL, `dailyLimitMinutes` INTEGER NOT NULL, `todayUsageMinutes` INTEGER NOT NULL, `interceptCount` INTEGER NOT NULL, `activePeekUntilElapsedRealtime` INTEGER NOT NULL, `lastPeekEndElapsedRealtime` INTEGER NOT NULL, `isSystemDetected` INTEGER NOT NULL, PRIMARY KEY(`packageName`))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `codeName` TEXT NOT NULL, `description` TEXT NOT NULL, `defaultMinutes` INTEGER NOT NULL, `blockedCategoriesCsv` TEXT NOT NULL, `scheduleLabel` TEXT NOT NULL, `isScheduleEnabled` INTEGER NOT NULL, `startHour` INTEGER NOT NULL, `startMinute` INTEGER NOT NULL, `endHour` INTEGER NOT NULL, `endMinute` INTEGER NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `intention` TEXT NOT NULL, `profileName` TEXT NOT NULL, `targetDurationSeconds` INTEGER NOT NULL, `elapsedSeconds` INTEGER NOT NULL, `startWallTimeMillis` INTEGER NOT NULL, `startElapsedRealtimeMillis` INTEGER NOT NULL, `bootCount` INTEGER NOT NULL, `accumulatedBeforeRebootSeconds` INTEGER NOT NULL, `lastTickElapsedRealtimeMillis` INTEGER NOT NULL, `hmacSignature` TEXT NOT NULL, `isStopwatchMode` INTEGER NOT NULL, `totalStudyBlocks` INTEGER NOT NULL, `currentBlockIndex` INTEGER NOT NULL, `isInSafeBreak` INTEGER NOT NULL, `safeBreakSecondsRemaining` INTEGER NOT NULL, `penaltySecondsAdded` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, `completedSuccessfully` INTEGER NOT NULL, `phantomReachesBlocked` INTEGER NOT NULL, `tamperDetected` INTEGER NOT NULL, `forgedArtifactId` TEXT, `completedTasksSummary` TEXT NOT NULL, `tasksCompletedCount` INTEGER NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `intercept_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampMillis` INTEGER NOT NULL, `packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `sessionIntention` TEXT NOT NULL, `outcome` TEXT NOT NULL, `triggerReason` TEXT NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `captured_notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampMillis` INTEGER NOT NULL, `packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `senderTitle` TEXT NOT NULL, `messageBody` TEXT NOT NULL, `isRead` INTEGER NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `notesOrSubsteps` TEXT NOT NULL, `estimatedMinutes` INTEGER NOT NULL, `actualSecondsSpent` INTEGER NOT NULL, `categoryTag` TEXT NOT NULL, `queueOrder` INTEGER NOT NULL, `status` TEXT NOT NULL, `completedAtMillis` INTEGER NOT NULL, `completedInSessionId` INTEGER NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `blocked_sites` (`domain` TEXT NOT NULL, PRIMARY KEY(`domain`))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `guard_settings` (`id` INTEGER NOT NULL, `browserBlockerEnabled` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `routines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `startMinutes` INTEGER NOT NULL, `endMinutes` INTEGER NOT NULL, `daysMask` INTEGER NOT NULL, `loadoutName` TEXT NOT NULL, `enabled` INTEGER NOT NULL)"
                    )
                    db.execSQL("INSERT INTO guard_settings (id, browserBlockerEnabled) VALUES (1, 1)")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config).writableDatabase.close()
    }

    @Test
    fun v7EntitySchema_fullChain_roomValidatesAtV17_shortsDefaultsTrue_thenRoundTrips() {
        createV7EntityFile()
        val room = Room.databaseBuilder(context, MonolithDatabase::class.java, dbName)
            .addMigrations(
                MonolithDatabase.MIGRATION_7_8,
                MonolithDatabase.MIGRATION_8_9,
                MonolithDatabase.MIGRATION_9_10,
                MonolithDatabase.MIGRATION_10_11,
                MonolithDatabase.MIGRATION_11_12,
                MonolithDatabase.MIGRATION_12_13,
                MonolithDatabase.MIGRATION_13_14,
                MonolithDatabase.MIGRATION_14_15,
                MonolithDatabase.MIGRATION_15_16,
                MonolithDatabase.MIGRATION_16_17,
                MonolithDatabase.MIGRATION_17_18,
                MonolithDatabase.MIGRATION_18_19,
                MonolithDatabase.MIGRATION_19_20,
                MonolithDatabase.MIGRATION_20_21,
                MonolithDatabase.MIGRATION_21_22,
                MonolithDatabase.MIGRATION_22_23
            )
            .build()
        runBlocking {
            val dao = room.monolithDao()
            // The read forces the open: Room has migrated
            // 7→8→9→10→11→12→13→14→15→16→17→18→19→20→21 AND
            // validated the result against every v21 entity.
            val settings = dao.getGuardSettingsSnapshot()
            assertNotNull(settings)
            assertTrue(settings!!.shortsReelsSniperEnabled)
            dao.updateShortsReelsSniperEnabled(false)
            assertFalse(dao.getGuardSettingsSnapshot()!!.shortsReelsSniperEnabled)
            dao.updateShortsReelsSniperEnabled(true)
            assertTrue(dao.getGuardSettingsSnapshot()!!.shortsReelsSniperEnabled)
        }
        room.close()
    }

    @Test
    fun ShortsSettingPersistsAcrossProcessDeath() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            val writer = MonolithRepository(context, dao)
            // Never written: the read is the shipped default.
            assertTrue(writer.isShortsReelsSniperEnabled())
            writer.setShortsReelsSniperEnabled(false)
            // Process death, simulated honestly: a brand-new
            // repository holds no memory of the write — whatever
            // it reads now is the store, or nothing. (The old
            // construction fails this by design: there was no
            // store to read.)
            val reader = MonolithRepository(context, dao)
            assertFalse(reader.isShortsReelsSniperEnabled())
            reader.setShortsReelsSniperEnabled(true)
            assertTrue(writer.isShortsReelsSniperEnabled())
        } finally {
            db.close()
        }
    }

    @Test
    fun SniperHonoursShortsSetting() {
        val off = GuardSettingsEntity(shortsReelsSniperEnabled = false)
        val on = GuardSettingsEntity(shortsReelsSniperEnabled = true)
        // Stored OFF: the sniper leaves both feeds alone.
        assertFalse(MonolithAccessibilityService.shortsSniperArmed(off, "com.google.android.youtube"))
        assertFalse(MonolithAccessibilityService.shortsSniperArmed(off, "com.instagram.android"))
        // Stored ON: the sniper is armed for both feeds.
        assertTrue(MonolithAccessibilityService.shortsSniperArmed(on, "com.google.android.youtube"))
        assertTrue(MonolithAccessibilityService.shortsSniperArmed(on, "com.instagram.android"))
        // A missing row reads as the shipped default (ON) — the
        // store's absence must never silently disarm it.
        assertTrue(MonolithAccessibilityService.shortsSniperArmed(null, "com.google.android.youtube"))
        // And the switch never arms the sniper for other packages.
        assertFalse(MonolithAccessibilityService.shortsSniperArmed(on, "com.whatsapp.android"))
    }

    /** Pump the shadow clock in 25ms steps until [cond] holds
     *  (the screenshot harness's discipline, copied). */
    private fun pumpUntil(timeoutMs: Long = 60_000, cond: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            if (cond()) return true
            ShadowSystemClock.advanceBy(Duration.ofMillis(25))
            Thread.sleep(20)
        }
        ShadowLooper.idleMainLooper()
        return cond()
    }

    @Test
    fun viewModel_printIsTheStore_toggleWritesTheStore() {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao())
            runBlocking { repository.setShortsReelsSniperEnabled(false) }
            val viewModel = MonolithViewModel(
                context,
                repository,
                TactileSoundEngine(context)
            )
            // The flow is BORN `true` in code; only the init seed
            // can pull it down to the stored OFF. If the seed ever
            // stops reading the store, the user's OFF resurrects
            // as ON at every cold start — the phantom's exact lie.
            assertTrue(
                "the ViewModel's print must be seeded from the stored OFF",
                pumpUntil { !viewModel.shortsReelsSniperEnabled.value }
            )
            viewModel.toggleShortsReelsSniper()
            assertTrue(viewModel.shortsReelsSniperEnabled.value)
            assertTrue(
                "the toggle's flip must land in the persisted row",
                pumpUntil { runBlocking { repository.isShortsReelsSniperEnabled() } }
            )
        } finally {
            db.close()
        }
    }
}
