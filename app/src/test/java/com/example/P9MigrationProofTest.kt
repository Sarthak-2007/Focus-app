package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.LegacyCarry
import com.example.data.MonolithDatabase
import com.example.data.MonolithWeek
import com.example.data.StrongroomStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime

/**
 * P9 (BUILD-SR-8) — THE MIGRATION PROOF, at production shape.
 *
 * The claim under proof (plan §1 item 10, build record U-9): the
 * first usable build installs OVER his current app and his data
 * carries. P1's own `StrongroomMigrationP1Test` proves the mechanism
 * on minimal-form rows; this harness proves it on the production-
 * shaped fixture set ([P9ProductionFixtures]) — every dataset the
 * plan names, asserted BY NAME:
 *
 *  A · a v25-shaped file seeded with the full fixture set takes
 *      MIGRATION_25_26: the four Strongroom tables land with their
 *      full columns, and EVERY seeded row reads back value-for-value
 *      — loadout (apps by tier, the site, the profile), rules (the
 *      guard row), history (tasks, sessions, the week's spend).
 *      Row counts are unchanged on every seeded table.
 *  B · the migration is re-entrant: running it a second time over
 *      the same file changes nothing (CREATE-only, IF NOT EXISTS).
 *  C · at Room v26 the MEANING carries, not just the bytes:
 *      LegacyCarry derives tomorrow's signing from the fixture
 *      loadout (only the LOCKED app locks; his break length and the
 *      active session's own sentence carry verbatim), the drawer
 *      serves the fixture's finished task by title inside its
 *      window and not outside it, and the crank is born spent from
 *      the fixture's legacy spend, flagged as carried.
 *
 * Scope honesty (VERIFY-SR-1's proof map): this is venue proof of
 * the migration mechanism and the carry derivations. It does not
 * prove install-over on his OnePlus — that stays UNPROVEN-ON-DEVICE
 * (Audit A G-7) regardless of this file's colour.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class P9MigrationProofTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "p9_migration_proof_test.db"
    private val fx = P9ProductionFixtures

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
    }

    // ------------------------------------------------------------------
    // Raw-file helpers
    // ------------------------------------------------------------------

    private fun openV25File(): SupportSQLiteDatabase {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(25) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    fx.V25_DDL.forEach { db.execSQL(it) }
                    fx.seedV25Sql().forEach { db.execSQL(it) }
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        return factory.create(config).writableDatabase
    }

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
        val cols = mutableSetOf<String>()
        db.query("PRAGMA table_info($table)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) cols.add(cursor.getString(nameIdx))
        }
        return cols
    }

    private fun countRows(db: SupportSQLiteDatabase, table: String): Int {
        db.query("SELECT COUNT(*) FROM $table").use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else -1
        }
    }

    private fun queryOne(
        db: SupportSQLiteDatabase, sql: String
    ): Map<String, Any?>? {
        db.query(sql).use { cursor ->
            if (!cursor.moveToFirst()) return null
            val row = mutableMapOf<String, Any?>()
            for (i in 0 until cursor.columnCount) {
                val name = cursor.getColumnName(i)
                row[name] = when (cursor.getType(i)) {
                    android.database.Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i)
                    android.database.Cursor.FIELD_TYPE_STRING -> cursor.getString(i)
                    android.database.Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i)
                    android.database.Cursor.FIELD_TYPE_NULL -> null
                    else -> cursor.getString(i)
                }
            }
            return row
        }
    }

    // ------------------------------------------------------------------
    // A · the file carries, by name
    // ------------------------------------------------------------------

    @Test
    fun migration25to26_productionShapedFile_everyNamedRowCarries() {
        val db = openV25File()
        try {
            // Pre-migration sanity: the fixture set stands as seeded.
            assertEquals(3, countRows(db, "blocked_apps"))
            assertEquals(3, countRows(db, "focus_tasks"))
            assertEquals(2, countRows(db, "focus_sessions"))

            MonolithDatabase.MIGRATION_25_26.migrate(db)

            // The four Strongroom tables land, with their full columns.
            assertTrue(
                columnsOf(db, "strongroom_days").containsAll(
                    setOf(
                        "filingDateIso", "lockedPackagesCsv", "liftingHour", "liftingMinute",
                        "sentence", "breakLengthMinutes", "breakFiresAtOffsetMillis",
                        "allowanceTotalMinutes", "allowanceSpentMinutes", "gapsEncoded",
                        "sealedAtWallMillis", "sealedAtElapsedMillis", "sealedBootCount",
                        "spanDurationMillis", "state", "struckHeldMillis",
                        "struckBracketGapMillis", "crankSealBroken", "carriedFromSessionId"
                    )
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_punches").containsAll(
                    setOf("id", "filingDateIso", "kind", "atOffsetMillis", "packageName")
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_guard").containsAll(
                    setOf(
                        "id", "guardOn", "allowanceMinutes", "pendingAllowanceMinutes",
                        "pendingAllowanceFromMillis", "pendingOffFromMillis", "stretchEndMillis"
                    )
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_crank_weeks").containsAll(
                    setOf(
                        "weekAnchorMillis", "spentSeconds", "sealBroken",
                        "spentAtWallMillis", "carriedFromLegacySpend"
                    )
                )
            )

            // Row counts unchanged on every seeded legacy table.
            assertEquals(3, countRows(db, "blocked_apps"))
            assertEquals(1, countRows(db, "blocked_sites"))
            assertEquals(1, countRows(db, "focus_profiles"))
            assertEquals(1, countRows(db, "guard_settings"))
            assertEquals(3, countRows(db, "focus_tasks"))
            assertEquals(2, countRows(db, "focus_sessions"))
            assertEquals(1, countRows(db, "emergency_break_spends"))

            // LOADOUT, by name: the locked app keeps its tier, its
            // limit and its intercept count; peek and open likewise.
            val locked = queryOne(
                db, "SELECT * FROM blocked_apps WHERE packageName = '${fx.LOCKED_PACKAGE}'"
            )!!
            assertEquals("Video", locked["appName"])
            assertEquals("LOCKED", locked["blockTier"])
            assertEquals(1L, locked["isBlocked"])
            assertEquals(30L, locked["dailyLimitMinutes"])
            assertEquals(7L, locked["interceptCount"])
            val peek = queryOne(
                db, "SELECT * FROM blocked_apps WHERE packageName = '${fx.PEEK_PACKAGE}'"
            )!!
            assertEquals("PEEK_2M", peek["blockTier"])
            assertEquals(3L, peek["interceptCount"])
            val open = queryOne(
                db, "SELECT * FROM blocked_apps WHERE packageName = '${fx.OPEN_PACKAGE}'"
            )!!
            assertEquals("OPEN", open["blockTier"])
            assertEquals(0L, open["isBlocked"])
            assertEquals(12L, open["todayUsageMinutes"])

            val site = queryOne(
                db, "SELECT * FROM blocked_sites WHERE domain = '${fx.SITE_DOMAIN}'"
            )!!
            assertEquals(fx.SITE_DOMAIN, site["domain"])

            val profile = queryOne(
                db, "SELECT * FROM focus_profiles WHERE id = 3"
            )!!
            assertEquals(fx.PROFILE_NAME, profile["name"])
            assertEquals("DEEP", profile["codeName"])
            assertEquals(300L, profile["defaultMinutes"])
            assertEquals("VIDEO,SOCIAL", profile["blockedCategoriesCsv"])
            assertEquals("${fx.LOCKED_PACKAGE}:LOCKED", profile["appTiersCsv"])
            assertEquals(fx.SITE_DOMAIN, profile["sitesCsv"])
            assertEquals(14L, profile["endHour"])

            // RULES, by name: his break length and peek minutes are
            // his, not the shipped defaults the migration might
            // have substituted.
            val guard = queryOne(db, "SELECT * FROM guard_settings WHERE id = 1")!!
            assertEquals(fx.GUARD_BREAK_MINUTES.toLong(), guard["breakMinutes"])
            assertEquals(fx.GUARD_PEEK_MINUTES.toLong(), guard["peekMinutes"])
            assertEquals("HELD", guard["lastReceiptOutcome"])
            assertEquals(12L, guard["receiptSequence"])
            assertEquals(1_762_000_000_000L, guard["lastBootAtMillis"])

            // HISTORY, by name: tasks keep title/status/completion,
            // sessions keep their intentions verbatim.
            val done = queryOne(db, "SELECT * FROM focus_tasks WHERE id = 11")!!
            assertEquals(fx.DONE_TASK_TITLE, done["title"])
            assertEquals("COMPLETED", done["status"])
            assertEquals(5400L, done["actualSecondsSpent"])
            assertEquals(fx.NOW_MILLIS - 2 * 3_600_000L, done["completedAtMillis"])
            val queued = queryOne(db, "SELECT * FROM focus_tasks WHERE id = 13")!!
            assertEquals(fx.QUEUED_TASK_TITLE, queued["title"])
            assertEquals("QUEUED", queued["status"])

            val finished = queryOne(
                db, "SELECT * FROM focus_sessions WHERE id = ${fx.FINISHED_SESSION_ID}"
            )!!
            assertEquals(fx.FINISHED_SESSION_INTENTION, finished["intention"])
            assertEquals(10_740L, finished["elapsedSeconds"])
            assertEquals(1L, finished["completedSuccessfully"])
            val active = queryOne(
                db, "SELECT * FROM focus_sessions WHERE id = ${fx.ACTIVE_SESSION_ID}"
            )!!
            assertEquals(fx.ACTIVE_SESSION_INTENTION, active["intention"])
            assertEquals(1L, active["isActive"])

            val spend = queryOne(db, "SELECT * FROM emergency_break_spends WHERE id = 5")!!
            assertEquals(60L, spend["heldSeconds"])
            assertEquals(
                MonolithWeek.weekAnchorMillis(fx.NOW_MILLIS, fx.UTC),
                spend["weekAnchorMillis"]
            )
        } finally {
            db.close()
        }
    }

    // ------------------------------------------------------------------
    // B · re-entry changes nothing
    // ------------------------------------------------------------------

    @Test
    fun migration25to26_runTwice_legacyRowsStillIntact() {
        val db = openV25File()
        try {
            MonolithDatabase.MIGRATION_25_26.migrate(db)
            MonolithDatabase.MIGRATION_25_26.migrate(db)
            assertEquals(3, countRows(db, "blocked_apps"))
            assertEquals(3, countRows(db, "focus_tasks"))
            val guard = queryOne(db, "SELECT * FROM guard_settings WHERE id = 1")!!
            assertEquals(fx.GUARD_BREAK_MINUTES.toLong(), guard["breakMinutes"])
            assertEquals(0, countRows(db, "strongroom_days"))
        } finally {
            db.close()
        }
    }

    // ------------------------------------------------------------------
    // C · at v26 the meaning carries
    // ------------------------------------------------------------------

    @Test
    fun carryAtV26_signingDrawerAndCrank_fromTheFixtureSet() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            dao.insertApps(fx.blockedApps)
            dao.insertSite(fx.blockedSite)
            dao.insertProfile(fx.profile)
            dao.insertGuardSettings(fx.guardSettings)
            dao.insertTasks(fx.tasks())
            fx.sessions().forEach { dao.insertSession(it) }
            dao.insertEmergencyBreakSpend(fx.spendThisWeek())
            val store = StrongroomStore(dao)

            // COMPOSE: tomorrow's signing derives from HIS loadout —
            // only the LOCKED tier locks (peek/open never enter the
            // set), his break length carries, the active session's
            // own words carry as the sentence, verbatim.
            val activeSession = fx.sessions().first { it.id == fx.ACTIVE_SESSION_ID }
            val signing = LegacyCarry.deriveSigning(
                filingDateIso = "2026-11-05",
                blockedApps = fx.blockedApps,
                guardSettings = fx.guardSettings,
                activeSession = activeSession,
                guardAllowanceMinutes = 30,
                activeSessionWindowEnd = LocalTime.of(14, 0)
            )
            assertEquals(setOf(fx.LOCKED_PACKAGE), signing.lockedPackages)
            assertEquals(fx.GUARD_BREAK_MINUTES, signing.breakLengthMinutes)
            assertEquals(fx.ACTIVE_SESSION_INTENTION, signing.sentence)
            assertEquals(LocalTime.of(14, 0), signing.liftingTime)
            assertEquals(30, signing.allowanceMinutes)

            // DRAWER: the finished task is stored and served by
            // title inside its window; the older completion is not
            // dragged into the window, the queued task never stands
            // as finished.
            val page = store.drawerPage(
                fromMillis = fx.NOW_MILLIS - 24 * 3_600_000L,
                toMillis = fx.NOW_MILLIS + 3_600_000L,
                limit = 10, offset = 0
            )
            assertEquals(listOf(fx.DONE_TASK_TITLE), page.map { it.title })

            // CRANK: the fixture's legacy spend makes this week's
            // crank born spent, flagged as carried — his spent
            // minute is never refunded by the redesign.
            val week = store.crankWeek(fx.NOW_MILLIS, fx.UTC)
            assertFalse(week.minuteAvailable)
            assertTrue(week.sealBroken)
            val anchor = MonolithWeek.weekAnchorMillis(fx.NOW_MILLIS, fx.UTC)
            assertTrue(dao.getStrongroomCrankWeek(anchor)!!.carriedFromLegacySpend)
        } finally {
            db.close()
        }
    }
}
