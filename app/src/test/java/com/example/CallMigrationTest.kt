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
 * W3 #134 (14→15; renumbered twice at composition — main took
 *  12→13 for issue #287 and then 13→14 for issue #10's held-split
 *  while this lane was in flight):
 *  the migration that carries callExcludedSeconds.
 *
 *  A: a v14-shaped file takes 14→15 alone: the column lands, the
 *     pre-existing row reads the truthful default (0 — before
 *     this column the progress machine never looked at call
 *     state, so no past row can claim an excluded second), the
 *     row survives, and the struck testDateEpochDay exists
 *     nowhere.
 *  B: Room itself at v15 — an in-memory build from the entities
 *     round-trips the new field, so the entity and the migrated
 *     schema are one shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CallMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "call_migration_test.db"

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
    fun migration14to15_addsCallExcludedColumn_withTruthfulDefault() {
        // Shape a v14 file: focus_sessions as it stood, one row in.
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(14) {
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

        // Open it at 15 through the lane's migration alone.
        val openConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(15) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(14, oldVersion)
                    assertEquals(15, newVersion)
                    MonolithDatabase.MIGRATION_14_15.migrate(db)
                }
            })
            .build()
        val db = factory.create(openConfig).writableDatabase
        try {
            val cols = columnsOf(db, "focus_sessions")
            assertTrue(cols.contains("callExcludedSeconds"))
            assertFalse(cols.contains("testDateEpochDay"))
            db.query("SELECT callExcludedSeconds, intention FROM focus_sessions WHERE id = 1")
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
    fun roomAtV15_roundTripsCallExcludedSeconds() = runBlocking {
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
                    elapsedSeconds = 0,
                    startWallTimeMillis = 0L,
                    startElapsedRealtimeMillis = 0L,
                    callExcludedSeconds = 300,
                    isActive = true,
                    completedSuccessfully = false
                )
            ).toInt()
            assertEquals(300, dao.getSessionById(id)!!.callExcludedSeconds)
        } finally {
            db.close()
        }
    }
}
