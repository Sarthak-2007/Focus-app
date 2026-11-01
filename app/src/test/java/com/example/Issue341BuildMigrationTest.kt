package com.example

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD (PR-B) — the schema step carrying F05 / F20 /
 * F58 / F67, run directly against a pre-step fixture: the two
 * new log tables appear, the three columns land, and existing
 * rows keep their facts (a day word survives with a truthful
 * 0 declaration instant — never a fabricated time).
 *
 * Train note: this case references the migration object BY NAME
 * (MonolithDatabase.MIGRATION_23_24). The step's number is ruled
 * by Ren (clone), #341 comment 6544758887: PR-A landed at DB v23,
 * this #341 PR-B step is 23→24, sole owner this desk, and the
 * typed stop-reason column is sequenced after it as its own
 * step. The fixture below is the v23 shape of the touched
 * tables.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
        val cols = mutableSetOf<String>()
        db.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) cols += cursor.getString(nameIdx)
        }
        return cols
    }

    private fun tableExists(db: SupportSQLiteDatabase, table: String): Boolean {
        db.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(table)
        ).use { return it.moveToFirst() }
    }

    @Test
    fun migrationChain_dayNotesGainsDeclarationTime_existingRowsKeepWord() {
        val dbName = "issue341-migration-test.db"
        context.deleteDatabase(dbName)
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(23) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Pre-step shapes of the touched tables
                    // (only the columns the fixture needs — the
                    // migration's ALTERs do not read the rest).
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `day_notes` (" +
                            "`dateIso` TEXT NOT NULL PRIMARY KEY, " +
                            "`word` TEXT NOT NULL)"
                    )
                    db.execSQL(
                        "INSERT INTO `day_notes` (`dateIso`, `word`) " +
                            "VALUES ('2026-10-30', 'REST')"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_sessions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`title` TEXT NOT NULL, " +
                            "`notesOrSubsteps` TEXT NOT NULL)"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, old: Int, new: Int) = Unit
            })
            .build()
        val helper = factory.create(config)
        val db = helper.writableDatabase
        try {
            MonolithDatabase.MIGRATION_23_24.migrate(db)

            // F58: the declaration-time column lands; the standing
            // word survives; the instant is 0 (unrecorded), never
            // a fabricated declaration time.
            assertTrue(columnsOf(db, "day_notes").contains("declaredAtMillis"))
            db.query("SELECT `word`, `declaredAtMillis` FROM `day_notes`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("REST", cursor.getString(0))
                assertEquals(0L, cursor.getLong(1))
            }
            // F67: the note column lands beside the substeps.
            assertTrue(columnsOf(db, "focus_tasks").contains("noteText"))
            // F05: the planned-end backup column + the event log.
            assertTrue(columnsOf(db, "focus_sessions").contains("plannedEndWallMillis"))
            assertTrue(tableExists(db, "session_event_logs"))
            // F20: the rule-change log.
            assertTrue(tableExists(db, "rule_change_log"))
        } finally {
            helper.close()
            context.deleteDatabase(dbName)
        }
    }
}
