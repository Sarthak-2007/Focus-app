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
 * Issue #261 (15→16): the migration that carries the mid-block
 * Safe Break door's two columns.
 *
 *  A: a v15-shaped file takes 15→16 alone: both columns land,
 *     the pre-existing row reads the truthful defaults (0 /
 *     false — before this door no break could start mid-block,
 *     so no past row entered one), the row survives, and the
 *     struck testDateEpochDay exists nowhere.
 *  B: Room itself at v16 — an in-memory build from the entities
 *     round-trips the new fields, so the entity and the migrated
 *     schema are one shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MidBlockBreakMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "mid_block_break_migration_test.db"

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
    fun migration15to16_addsMidBlockColumns_withTruthfulDefaults() {
        // Shape a v15 file: focus_sessions as it stood, one row in.
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(15) {
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

        // Open it at 16 through the lane's migration alone.
        val openConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(16) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(15, oldVersion)
                    assertEquals(16, newVersion)
                    MonolithDatabase.MIGRATION_15_16.migrate(db)
                }
            })
            .build()
        val db = factory.create(openConfig).writableDatabase
        try {
            val cols = columnsOf(db, "focus_sessions")
            assertTrue(cols.contains("midBlockSafeBreak"))
            assertTrue(cols.contains("extraSafeBreakSeconds"))
            assertFalse(cols.contains("testDateEpochDay"))
            db.query("SELECT midBlockSafeBreak, extraSafeBreakSeconds, intention FROM focus_sessions WHERE id = 1")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals(0, cursor.getInt(0))
                    assertEquals(0, cursor.getInt(1))
                    assertEquals("shaped row", cursor.getString(2))
                }
        } finally {
            db.close()
        }
    }

    @Test
    fun roomAtV16_roundTripsMidBlockFields() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .addMigrations(
                MonolithDatabase.MIGRATION_7_8,
                MonolithDatabase.MIGRATION_8_9,
                MonolithDatabase.MIGRATION_9_10,
                MonolithDatabase.MIGRATION_10_11,
                MonolithDatabase.MIGRATION_11_12,
                MonolithDatabase.MIGRATION_12_13,
                MonolithDatabase.MIGRATION_13_14,
                MonolithDatabase.MIGRATION_14_15,
                MonolithDatabase.MIGRATION_15_16
            )
            .build()
        try {
            val dao = db.monolithDao()
            val id = dao.insertSession(
                FocusSessionEntity(
                    intention = "ROUND TRIP",
                    profileName = "Manual",
                    targetDurationSeconds = 1500,
                    elapsedSeconds = 600,
                    startWallTimeMillis = 0L,
                    startElapsedRealtimeMillis = 0L,
                    isInSafeBreak = true,
                    midBlockSafeBreak = true,
                    extraSafeBreakSeconds = 600,
                    isActive = true,
                    completedSuccessfully = false
                )
            ).toInt()
            val stored = dao.getSessionById(id)!!
            assertTrue(stored.midBlockSafeBreak)
            assertEquals(600, stored.extraSafeBreakSeconds)
        } finally {
            db.close()
        }
    }
}
