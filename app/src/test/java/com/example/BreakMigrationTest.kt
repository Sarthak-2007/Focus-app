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
 * Truth Car 3 — breaks lane (11→12, Tamm): the migration that
 * carries breakTakenSeconds.
 *
 *  A: a v11-shaped file takes 11→12 alone: the column lands, the
 *     pre-existing row reads the truthful default (0 — every
 *     break a pre-v12 row ever took ran to its timer, because no
 *     early end existed; nothing is owed and nothing is invented),
 *     the row survives, and the struck testDateEpochDay exists
 *     nowhere.
 *  B: Room itself at v12 — an in-memory build from the entities
 *     round-trips the new field, so the entity and the migrated
 *     schema are one shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BreakMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "break_migration_test.db"

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
    fun migration11to12_addsTakenColumn_withTruthfulDefault() {
        // Shape a v11 file: focus_sessions as it stood, one row in.
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(11) {
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

        // Open it at 12 through the lane's migration alone.
        val openConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
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
        val db = factory.create(openConfig).writableDatabase
        try {
            val cols = columnsOf(db, "focus_sessions")
            assertTrue(cols.contains("breakTakenSeconds"))
            assertFalse(cols.contains("testDateEpochDay"))
            db.query("SELECT breakTakenSeconds, intention FROM focus_sessions WHERE id = 1")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals(0, cursor.getInt(0))
                    assertEquals("shaped row", cursor.getString(1))
                }
        } finally {
            db.close()
        }
    }

    @Test
    fun roomAtV12_roundTripsBreakTakenSeconds() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .addMigrations(
                MonolithDatabase.MIGRATION_7_8,
                MonolithDatabase.MIGRATION_8_9,
                MonolithDatabase.MIGRATION_9_10,
                MonolithDatabase.MIGRATION_10_11,
                MonolithDatabase.MIGRATION_11_12
            )
            .build()
        try {
            val dao = db.monolithDao()
            val id = dao.insertSession(
                FocusSessionEntity(
                    intention = "ROUND TRIP",
                    profileName = "Manual",
                    targetDurationSeconds = 1500,
                    elapsedSeconds = 0,
                    startWallTimeMillis = 0L,
                    startElapsedRealtimeMillis = 0L,
                    breakTakenSeconds = 240,
                    isActive = true,
                    completedSuccessfully = false
                )
            ).toInt()
            assertEquals(240, dao.getSessionById(id)!!.breakTakenSeconds)
        } finally {
            db.close()
        }
    }
}
