package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Slice E — Migration 9→10 (Saum's unified manifest, F-N1 +
 * F-SCH-2 as ruled) and the full clean-upgrade chain.
 *
 * E-a: a v9-shaped file takes 9→10 alone: the twelve columns land
 *      (8 on focus_sessions, 4 on guard_settings), defaults are
 *      truthful — held counts at −1 (uncaptured; NO backfill),
 *      outcome '', the claim at rest — rows intact, and the
 *      struck testDateEpochDay exists nowhere.
 * E-b: the same shaped file at v7 runs 7→8→9→10 end to end.
 * E-c: a file built from the FULL v7 entity schema opens through
 *      Room itself at v10: Room migrates the whole chain AND
 *      validates the final schema against the v10 entities; the
 *      new session fields round-trip with their defaults.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// PIN-SCOPE: PLUMBING — Room migration chain to v19, schema validation (working-rules adoption, Oct 31 18:06 IST).
class SliceEMigrationChainTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "slice_e_migration_chain_test.db"

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

    private fun createShapedFile(version: Int) {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`intention` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_sessions (id, intention) VALUES (1, 'shaped row')")
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `guard_settings` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO guard_settings (id) VALUES (1)")
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `routines` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`title` TEXT NOT NULL)"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config).writableDatabase.close()
    }

    private fun openWithMigrations(fromVersion: Int, migrate: (SupportSQLiteDatabase) -> Unit): SupportSQLiteDatabase {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(11) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(fromVersion, oldVersion)
                    assertEquals(11, newVersion)
                    migrate(db)
                }
            })
            .build()
        return factory.create(config).writableDatabase
    }

    private fun assertSliceEColumns(db: SupportSQLiteDatabase) {
        val sessionCols = columnsOf(db, "focus_sessions")
        assertTrue(
            sessionCols.containsAll(
                setOf(
                    "heldAppsAtSeal", "heldSitesAtSeal", "releaseFaultShield",
                    "releaseRetriesDone", "releaseFaultedAtWallMillis", "outcome",
                    "shieldLastBeatWallMillis", "shieldGapSeen"
                )
            )
        )
        val settingsCols = columnsOf(db, "guard_settings")
        assertTrue(
            settingsCols.containsAll(
                setOf(
                    "lastCompositeSessionId", "lastReceiptOutcome",
                    "receiptSequence", "lastReceiptSessionId"
                )
            )
        )
        // Truth Car 2 (10→11): the receipt's two row facts land on
        // focus_sessions, defaulting to their truthful zeros —
        // 0 = "no stamp was taken", never a fabricated time or №.
        assertTrue(sessionCols.containsAll(setOf("endWallMillis", "receiptNumber")))
        db.query(
            "SELECT endWallMillis, receiptNumber FROM focus_sessions WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0L, cursor.getLong(0))
            assertEquals(0, cursor.getInt(1))
        }
        for (table in listOf("focus_sessions", "guard_settings", "routines", "focus_tasks")) {
            assertFalse(
                "testDateEpochDay must not exist on $table",
                columnsOf(db, table).contains("testDateEpochDay")
            )
        }
        // Defaults are truthful on the pre-existing row: the held
        // counts read UNCAPTURED (−1), never a fabricated zero.
        db.query(
            "SELECT heldAppsAtSeal, heldSitesAtSeal, outcome, shieldGapSeen FROM focus_sessions WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(-1, cursor.getInt(0))
            assertEquals(-1, cursor.getInt(1))
            assertEquals("", cursor.getString(2))
            assertEquals(0, cursor.getInt(3))
        }
        db.query(
            "SELECT receiptSequence, lastCompositeSessionId, lastReceiptSessionId FROM guard_settings WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
            assertEquals(-1, cursor.getInt(1))
            assertEquals(0, cursor.getInt(2))
        }
        db.query("SELECT intention FROM focus_sessions WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("shaped row", cursor.getString(0))
        }
    }

    @Test
    fun v9File_takesChainToV11() {
        createShapedFile(version = 9)
        val db = openWithMigrations(9) {
            MonolithDatabase.MIGRATION_9_10.migrate(it)
            // Truth Car 2: 10→11 lands the receipt's two row
            // facts (RC-01's end stamp, RC-04's receipt №).
            MonolithDatabase.MIGRATION_10_11.migrate(it)
        }
        assertSliceEColumns(db)
        db.close()
    }

    @Test
    fun v7File_runsTheWholeChain() {
        createShapedFile(version = 7)
        val db = openWithMigrations(7) {
            MonolithDatabase.MIGRATION_7_8.migrate(it)
            MonolithDatabase.MIGRATION_8_9.migrate(it)
            MonolithDatabase.MIGRATION_9_10.migrate(it)
            MonolithDatabase.MIGRATION_10_11.migrate(it)
        }
        assertSliceEColumns(db)
        db.close()
    }

    /** The full v7 entity schema (the routines chain test's DDL,
     *  verbatim) — Room opens it at the current version and
     *  validates everything. */
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
                    db.execSQL(
                        "INSERT INTO focus_sessions (intention, profileName, targetDurationSeconds, elapsedSeconds, " +
                            "startWallTimeMillis, startElapsedRealtimeMillis, bootCount, accumulatedBeforeRebootSeconds, " +
                            "lastTickElapsedRealtimeMillis, hmacSignature, isStopwatchMode, totalStudyBlocks, " +
                            "currentBlockIndex, isInSafeBreak, safeBreakSecondsRemaining, penaltySecondsAdded, " +
                            "isActive, completedSuccessfully, phantomReachesBlocked, tamperDetected, " +
                            "completedTasksSummary, tasksCompletedCount) VALUES ('shaped session', 'Study Loadout', " +
                            "3000, 0, 1000, 1000, 1, 0, 0, '', 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, '', 0)"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config).writableDatabase.close()
    }

    @Test
    fun v7EntitySchema_fullChain_roomValidatesAtV16() {
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
                MonolithDatabase.MIGRATION_19_20
            )
            .build()
        runBlocking {
            val dao = room.monolithDao()
            // The read forces the open: Room has migrated
            // 7→8→9→10→11→12→13→14→15→16→17→18→19 AND validated
            // the result against every v19 entity.
            val session = dao.getSessionHistorySnapshot().single()
            assertEquals("shaped session", session.intention)
            assertEquals(0L, session.endWallMillis)
            assertEquals(0, session.receiptNumber)
            // W3 #134 (14→15): a pre-v15 row never had a call
            // second excluded — 0 is the fact.
            assertEquals(0, session.callExcludedSeconds)
            // Issue #261 (15→16): a pre-v16 row never entered a
            // mid-block Safe Break — false / 0 are the facts.
            assertFalse(session.midBlockSafeBreak)
            assertEquals(0, session.extraSafeBreakSeconds)
            // Truth Car 3 (breaks lane, 11→12): a pre-v12 row took
            // no early-ended break — 0 taken seconds is the fact.
            assertEquals(0, session.breakTakenSeconds)
            assertEquals(-1, session.heldAppsAtSeal)
            assertEquals(-1, session.heldSitesAtSeal)
            // Issue #10 (13→14): a pre-v14 row's tier split was
            // never recorded — -1 (uncaptured) is the truth, and
            // the receipt will print its stored total, not a split.
            assertEquals(-1, session.heldShutAtSeal)
            assertEquals(-1, session.heldPeekAtSeal)
            // Audit R6 #340 (16→17): a pre-v17 row was never
            // finish-sealed — '' is the truth, no retroactive seal.
            assertEquals("", session.finishSignature)
            assertEquals("", session.prevFinishSignature)
            assertEquals("", session.outcome)
            assertEquals(0L, session.shieldLastBeatWallMillis)
            assertFalse(session.shieldGapSeen)
            val settings = dao.getGuardSettingsSnapshot()
            assertEquals(0, settings?.receiptSequence)
            assertEquals(-1, settings?.lastCompositeSessionId)
        }
        val raw = room.openHelper.writableDatabase
        val tables = mutableSetOf<String>()
        raw.query("SELECT name FROM sqlite_master WHERE type = 'table'").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) tables.add(cursor.getString(nameIdx))
        }
        for (table in tables) {
            if (table == "room_master_table" || table.startsWith("sqlite_")) continue
            assertFalse(
                "testDateEpochDay must not exist on $table",
                columnsOf(raw, table).contains("testDateEpochDay")
            )
        }
        room.close()
    }
}
