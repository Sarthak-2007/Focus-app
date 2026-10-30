package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
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
 * Issue #287 (12→13, Forest head-to-head F5/F8): the migration
 * that carries the failure-cause pair and the reflection line.
 *
 *  A: a v12-shaped file takes 12→13 alone: the three columns
 *     land, the pre-existing row reads the truthful defaults
 *     ('' — no culprit was ever recorded, no reflection was
 *     ever stored; nothing is invented), and the struck
 *     testDateEpochDay exists nowhere.
 *  B: Room itself at v13 — an in-memory build from the entities
 *     round-trips the new fields, so the entity and the migrated
 *     schema are one shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ForestSessionRecordMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "forest_session_record_migration_test.db"

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

    @Test
    fun migration12to13_addsRecordColumns_withTruthfulDefaults() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(12) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`intention` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_sessions (id, intention) VALUES (1, 'shaped row')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(createConfig).writableDatabase.close()

        val openConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(13) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(12, oldVersion)
                    assertEquals(13, newVersion)
                    MonolithDatabase.MIGRATION_12_13.migrate(db)
                }
            })
            .build()
        val db = factory.create(openConfig).writableDatabase
        val cols = columnsOf(db, "focus_sessions")
        assertTrue(cols.contains("failureCausePackage"))
        assertTrue(cols.contains("failureCauseKind"))
        assertTrue(cols.contains("reflectionNote"))
        assertFalse(cols.contains("testDateEpochDay"))
        db.query("SELECT failureCausePackage, failureCauseKind, reflectionNote FROM focus_sessions WHERE id = 1")
            .use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("", cursor.getString(0))
                assertEquals("", cursor.getString(1))
                assertEquals("", cursor.getString(2))
            }
        db.close()
    }

    @Test
    fun roomV13_roundTripsTheRecordFields() = runBlocking {
        val roomDb = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = roomDb.monolithDao()
            val row = FocusSessionEntity(
                intention = "Physics",
                profileName = "Deep Study",
                targetDurationSeconds = 3000,
                elapsedSeconds = 1200,
                startWallTimeMillis = 1_000L,
                startElapsedRealtimeMillis = 500L,
                isActive = false,
                completedSuccessfully = false,
                outcome = "VALVE_BROKE",
                failureCausePackage = "com.social.app",
                failureCauseKind = "EMERGENCY_VALVE",
                reflectionNote = "Reached for it at the hard problem."
            )
            val id = dao.insertSession(row).toInt()
            val read = dao.getSessionById(id)
            assertEquals("com.social.app", read?.failureCausePackage)
            assertEquals("EMERGENCY_VALVE", read?.failureCauseKind)
            assertEquals("Reached for it at the hard problem.", read?.reflectionNote)
        } finally {
            roomDb.close()
        }
    }
}
