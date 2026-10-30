package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.EmergencyBreakSpendEntity
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
 * TOKEN-SPEC §8 test 6 — Migration(7, 8). A v7-shaped database opens
 * under v8: existing rows intact, the spends table present and empty.
 * (The builder's missing fallback line is a source-level fact, verified
 * in MonolithDatabase.kt — with it gone, an un-migrated future bump is
 * a loud failure, never a silent wipe.)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TokenMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "token_migration_test.db"

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
        context.deleteDatabase("token_v8_room_test.db")
    }

    @Test
    fun v7DatabaseOpensUnderV8_rowsIntact_spendsTablePresentAndEmpty() {
        val factory = FrameworkSQLiteOpenHelperFactory()

        // A v7-shaped database: one pre-existing table with a row, and
        // no spends table.
        val config7 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(7) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`intention` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_sessions (id, intention) VALUES (1, 'v7 row')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(config7).writableDatabase.close()

        // Open the same file as v8, running the registered migration.
        val config8 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(8) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(7, oldVersion)
                    assertEquals(8, newVersion)
                    MonolithDatabase.MIGRATION_7_8.migrate(db)
                }
            })
            .build()
        val db = factory.create(config8).writableDatabase

        // The spends table exists…
        db.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'emergency_break_spends'")
            .use { cursor -> assertTrue(cursor.moveToFirst()) }
        // …and starts empty (no backfill — §6.3: nothing truthful exists
        // to backfill, so v8 starts with an empty ledger, full token).
        db.query("SELECT COUNT(*) FROM emergency_break_spends").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
        // The v7 row survived untouched.
        db.query("SELECT intention FROM focus_sessions WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("v7 row", cursor.getString(0))
        }
        // The DDL accepts exactly the entity's columns.
        db.execSQL(
            "INSERT INTO emergency_break_spends (sessionId, spentAtWallMillis, weekAnchorMillis, heldSeconds) " +
                "VALUES (1, 1000, 2000, 60)"
        )
        db.query("SELECT COUNT(*) FROM emergency_break_spends").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        db.close()
    }

    @Test
    fun roomV8DatabaseRoundTripsASpend() = runBlocking {
        val db = Room.databaseBuilder(context, MonolithDatabase::class.java, "token_v8_room_test.db")
            .addMigrations(MonolithDatabase.MIGRATION_7_8)
            .build()
        val dao = db.monolithDao()
        dao.insertEmergencyBreakSpend(
            EmergencyBreakSpendEntity(
                sessionId = 42,
                spentAtWallMillis = 5_000L,
                weekAnchorMillis = 4_000L,
                heldSeconds = 60
            )
        )
        val rows = dao.getSpendsForWeek(4_000L, 4_000L + 7 * 24 * 3_600_000L)
        assertEquals(1, rows.size)
        assertEquals(42, rows.single().sessionId)
        db.close()
    }
}
