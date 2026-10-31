package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.DayNoteEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * PIN scope: PLUMBING — pins the full migration chain's schema
 * validation, no behaviour.
 *
 * Routines rebuild — Migration chain 7→8→9.
 *
 * 8a: a v7-shaped file runs BOTH migrations: the token ledger appears
 * (empty), all fifteen routines columns land with truthful defaults,
 * pre-existing rows survive, and the STRUCK test-date predicate
 * column exists nowhere (Ren's 0915 ruling — no testDateEpochDay).
 * 8b: a v8 file holding a real spend keeps it through 8→9.
 * 8c: Room v9 round-trips the new routine/session fields.
 * 8d: a file built from the FULL v7 entity schema (every table,
 *     every column, exactly as Room created them on a fresh v7
 *     install — the v9 generated CREATE set minus the 8→9 deltas
 *     and the spend ledger) runs the whole chain and is then
 *     opened through Room itself: Room migrates AND validates the
 *     final schema against the v9 entities. day_notes (F-SCH-1)
 *     lands, empty, with the specced two columns; sentinels
 *     survive; the struck testDateEpochDay exists on NO table.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// PIN-SCOPE: PLUMBING — Room migration chain 7→21, schema validation (working-rules adoption, Oct 31 18:06 IST).
class RoutinesMigrationChainTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "routines_migration_chain_test.db"

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
        context.deleteDatabase("routines_v9_room_test.db")
    }

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
        val cols = mutableSetOf<String>()
        db.query("PRAGMA table_info($table)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) cols.add(cursor.getString(nameIdx))
        }
        return cols
    }

    private fun createShapedFile(version: Int, withSpend: Boolean) {
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
                        "CREATE TABLE IF NOT EXISTS `routines` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO routines (id, name) VALUES (1, 'Morning Deep Work')")
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`title` TEXT NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `guard_settings` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO guard_settings (id) VALUES (1)")
                    if (withSpend) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `emergency_break_spends` (" +
                                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "`sessionId` INTEGER NOT NULL, " +
                                "`spentAtWallMillis` INTEGER NOT NULL, " +
                                "`weekAnchorMillis` INTEGER NOT NULL, " +
                                "`heldSeconds` INTEGER NOT NULL)"
                        )
                        db.execSQL(
                            "INSERT INTO emergency_break_spends (sessionId, spentAtWallMillis, weekAnchorMillis, heldSeconds) " +
                                "VALUES (7, 1000, 2000, 60)"
                        )
                    }
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config).writableDatabase.close()
    }

    @Test
    fun v7File_runsBothMigrations_columnsLand_rowsIntact_noTestDateColumn() {
        createShapedFile(version = 7, withSpend = false)
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config9 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(9) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(7, oldVersion)
                    assertEquals(9, newVersion)
                    MonolithDatabase.MIGRATION_7_8.migrate(db)
                    MonolithDatabase.MIGRATION_8_9.migrate(db)
                }
            })
            .build()
        val db = factory.create(config9).writableDatabase

        // Token ledger from 7→8: present and empty.
        db.query("SELECT COUNT(*) FROM emergency_break_spends").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }

        // The fifteen columns, table by table.
        val routineCols = columnsOf(db, "routines")
        assertTrue(routineCols.containsAll(setOf("pausedUntilMillis", "scheduleEditedAtMillis")))
        val sessionCols = columnsOf(db, "focus_sessions")
        assertTrue(
            sessionCols.containsAll(
                setOf(
                    "routineId", "windowStartMillis", "windowEndMillis", "routineOutcome",
                    "evidenceMask", "firstEvidenceMillis", "lastEvidenceMillis", "taskId"
                )
            )
        )
        val settingsCols = columnsOf(db, "guard_settings")
        assertTrue(
            settingsCols.containsAll(
                setOf(
                    "routineReseedV1Done", "routineReseedNoteDismissed",
                    "lastBootAtMillis", "lastRoutineTickMillis"
                )
            )
        )
        assertTrue(columnsOf(db, "focus_tasks").contains("startedAtSessionElapsedSec"))

        // The struck predicate exists NOWHERE (Ren 0915).
        for (table in listOf("routines", "focus_sessions", "focus_tasks", "guard_settings")) {
            assertFalse(
                "testDateEpochDay must not exist on $table",
                columnsOf(db, table).contains("testDateEpochDay")
            )
        }
        // No attendance table is ever created.
        db.query(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'routine_attendance'"
        ).use { cursor -> assertFalse(cursor.moveToFirst()) }

        // Defaults are truthful on pre-existing rows.
        db.query("SELECT pausedUntilMillis, scheduleEditedAtMillis FROM routines WHERE id = 1")
            .use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0L, cursor.getLong(0))
                assertEquals(0L, cursor.getLong(1))
            }
        db.query(
            "SELECT routineId, routineOutcome, windowStartMillis FROM focus_sessions WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.isNull(0))
            assertTrue(cursor.isNull(1))
            assertEquals(0L, cursor.getLong(2))
        }
        // Pre-existing rows intact.
        db.query("SELECT intention FROM focus_sessions WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("shaped row", cursor.getString(0))
        }
        db.query("SELECT name FROM routines WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Morning Deep Work", cursor.getString(0))
        }
        db.close()
    }

    @Test
    fun v8File_withSpend_keepsSpendThroughMigration89() {
        createShapedFile(version = 8, withSpend = true)
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config9 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(9) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(8, oldVersion)
                    assertEquals(9, newVersion)
                    MonolithDatabase.MIGRATION_8_9.migrate(db)
                }
            })
            .build()
        val db = factory.create(config9).writableDatabase
        db.query("SELECT sessionId, heldSeconds FROM emergency_break_spends").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(7, cursor.getInt(0))
            assertEquals(60, cursor.getInt(1))
        }
        db.close()
    }

    @Test
    fun roomV9_roundTripsRoutineAndSessionFields() = runBlocking {
        val db = Room.databaseBuilder(context, MonolithDatabase::class.java, "routines_v9_room_test.db")
            .addMigrations(MonolithDatabase.MIGRATION_7_8, MonolithDatabase.MIGRATION_8_9, MonolithDatabase.MIGRATION_9_10)
            .build()
        val dao = db.monolithDao()
        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "FASTED BLOCK", startMinutes = 300, endMinutes = 480,
                    daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name,
                    enabled = true, pausedUntilMillis = 123L, scheduleEditedAtMillis = 456L
                )
            )
        )
        val routineId = dao.getAllRoutinesSnapshot().single { it.name == "FASTED BLOCK" }.id
        val sessionId = dao.insertSession(
            FocusSessionEntity(
                intention = "FASTED BLOCK", profileName = "Routine: FASTED BLOCK",
                targetDurationSeconds = 3600, elapsedSeconds = 0,
                startWallTimeMillis = 1_000L, startElapsedRealtimeMillis = 0L,
                isActive = true, completedSuccessfully = false,
                routineId = routineId, windowStartMillis = 900L, windowEndMillis = 4_500L,
                evidenceMask = 1, firstEvidenceMillis = 0L, lastEvidenceMillis = 0L
            )
        ).toInt()
        val routine = dao.getRoutineById(routineId)
        assertEquals(123L, routine?.pausedUntilMillis)
        assertEquals(456L, routine?.scheduleEditedAtMillis)
        val session = dao.getSessionHistorySnapshot().single { it.id == sessionId }
        assertEquals(routineId, session.routineId)
        assertEquals(900L, session.windowStartMillis)
        assertNull(session.routineOutcome)
        db.close()
    }

    /**
     * Creates a database file whose schema is the v7 ENTITY schema:
     * every table Room created on a fresh v7 install, with its full
     * column set (derived from the v9 generated CREATE statements,
     * minus the fifteen 8→9 columns and the 7→8 spend ledger).
     */
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
                    // Sentinels: one row each in the tables 8→9 alters.
                    db.execSQL(
                        "INSERT INTO routines (name, startMinutes, endMinutes, daysMask, loadoutName, enabled) " +
                            "VALUES ('Morning Deep Work', 540, 780, 62, 'STUDY', 0)"
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
    fun v7EntitySchema_fullChain_roomValidates_dayNotesLandsEmpty_noTestDateAnywhere() {
        createV7EntityFile()
        val room = Room.databaseBuilder(context, MonolithDatabase::class.java, dbName)
            .addMigrations(
                MonolithDatabase.MIGRATION_7_8, MonolithDatabase.MIGRATION_8_9,
                MonolithDatabase.MIGRATION_9_10, MonolithDatabase.MIGRATION_10_11,
                MonolithDatabase.MIGRATION_11_12, MonolithDatabase.MIGRATION_12_13,
                MonolithDatabase.MIGRATION_13_14,
                MonolithDatabase.MIGRATION_14_15,
                MonolithDatabase.MIGRATION_15_16,
                MonolithDatabase.MIGRATION_16_17,
                MonolithDatabase.MIGRATION_17_18,
                MonolithDatabase.MIGRATION_18_19,
                MonolithDatabase.MIGRATION_19_20,
                MonolithDatabase.MIGRATION_20_21
            )
            .build()

        runBlocking {
            val dao = room.monolithDao()
            // This read forces the open: Room has now migrated
            // 7→8→9→10→11→12→13→14→15→16→17→18→19→20→21 AND
            // validated the resulting schema against every v21
            // entity, day_notes included.
            assertTrue(dao.getAllDayNotesSnapshot().isEmpty())
            dao.insertDayNotes(listOf(DayNoteEntity("2026-10-29", "REST")))
            assertEquals(
                listOf(DayNoteEntity("2026-10-29", "REST")),
                dao.getAllDayNotesSnapshot()
            )
            // Sentinels intact, 8→9 defaults truthful.
            val routine = dao.getAllRoutinesSnapshot().single { it.name == "Morning Deep Work" }
            assertEquals(0L, routine.pausedUntilMillis)
            assertEquals(0L, routine.scheduleEditedAtMillis)
            val session = dao.getSessionHistorySnapshot().single()
            assertEquals("shaped session", session.intention)
            assertNull(session.routineId)
            assertNull(session.taskId)
        }

        val raw = room.openHelper.writableDatabase
        val tables = mutableSetOf<String>()
        raw.query("SELECT name FROM sqlite_master WHERE type = 'table'").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) tables.add(cursor.getString(nameIdx))
        }
        assertTrue(
            tables.containsAll(
                setOf(
                    "blocked_apps", "focus_profiles", "focus_sessions", "intercept_logs",
                    "captured_notifications", "focus_tasks", "blocked_sites", "guard_settings",
                    "routines", "emergency_break_spends", "day_notes"
                )
            )
        )
        assertFalse(tables.contains("routine_attendance"))

        // The struck predicate exists NOWHERE — every table scanned.
        for (table in tables) {
            if (table == "room_master_table" || table.startsWith("sqlite_")) continue
            assertFalse(
                "testDateEpochDay must not exist on $table",
                columnsOf(raw, table).contains("testDateEpochDay")
            )
        }

        // day_notes is exactly the specced two columns, date as PK.
        assertEquals(setOf("dateIso", "word"), columnsOf(raw, "day_notes"))

        // Issue #138 CUT (19→20): captured_notifications survives
        // as metadata only — the content columns are GONE at v20.
        assertEquals(
            setOf("id", "timestampMillis", "packageName", "appName", "isRead"),
            columnsOf(raw, "captured_notifications")
        )
        raw.query("PRAGMA table_info(day_notes)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            val pkIdx = cursor.getColumnIndex("pk")
            val pkByName = mutableMapOf<String, Int>()
            while (cursor.moveToNext()) pkByName[cursor.getString(nameIdx)] = cursor.getInt(pkIdx)
            assertEquals(1, pkByName["dateIso"])
            assertEquals(0, pkByName["word"])
        }
        room.close()
    }
}
