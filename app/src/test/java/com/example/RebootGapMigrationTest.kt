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
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * PIN scope: PLUMBING — pins the 20→21 schema migration and the
 * entity's column mapping, no behaviour.
 *
 * Issue #525 — Migration(20, 21). A v20-shaped database opens
 * under v21: the focus_sessions row survives, and the new
 * rebootGapSeconds column stands at its truthful default —
 * 0, because no reboot gap was ever booked before this
 * migration existed (the old derivation credited the uptime
 * instead; inventing a figure now would fabricate the record).
 * (Authored as 18→19; renumbered twice in flight — #324's step
 * took 18→19, #138's took 19→20 — so the next free link
 * carries it.)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RebootGapMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "reboot_gap_migration_test.db"

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
    }

    @Test
    fun v20DatabaseOpensUnderV21_rowIntact_gapColumnDefaultsToZero() {
        val factory = FrameworkSQLiteOpenHelperFactory()

        // A v20-shaped database: a focus_sessions table without
        // the new column, one live row.
        val config20 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(20) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`intention` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_sessions (id, intention) VALUES (1, 'v20 row')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config20).writableDatabase.close()

        // Open the same file as v21, running the registered migration.
        val config21 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(21) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(20, oldVersion)
                    assertEquals(21, newVersion)
                    MonolithDatabase.MIGRATION_20_21.migrate(db)
                }
            })
            .build()
        val db = factory.create(config21).writableDatabase

        // The column stands…
        db.query("PRAGMA table_info(focus_sessions)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            var found = false
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIdx) == "rebootGapSeconds") found = true
            }
            assertTrue("rebootGapSeconds must exist after 20→21", found)
        }
        // …the v20 row survived, at the truthful default…
        db.query("SELECT intention, rebootGapSeconds FROM focus_sessions WHERE id = 1")
            .use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("v20 row", cursor.getString(0))
                assertEquals(0, cursor.getInt(1))
            }
        // …and the column takes a booked figure.
        db.execSQL("UPDATE focus_sessions SET rebootGapSeconds = 6900 WHERE id = 1")
        db.query("SELECT rebootGapSeconds FROM focus_sessions WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(6900, cursor.getInt(0))
        }
        db.close()
    }

    /** The v21 entity round-trips the booked gap through Room. */
    @Test
    fun roomV21SessionRoundTripsTheRebootGap() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            val id = dao.insertSession(
                FocusSessionEntity(
                    intention = "Deep Study",
                    profileName = "Default",
                    targetDurationSeconds = 3_600,
                    elapsedSeconds = 0,
                    startWallTimeMillis = 0L,
                    startElapsedRealtimeMillis = 0L,
                    rebootGapSeconds = 6_900,
                    isActive = true,
                    completedSuccessfully = false
                )
            ).toInt()
            assertEquals(6_900, dao.getSessionById(id)?.rebootGapSeconds)
        } finally {
            db.close()
        }
    }
}
