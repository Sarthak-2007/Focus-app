package com.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #31 — Sarthak's ruling of 2026-10-31 (issue comment
 * 6544207912) on the two database findings that stood blocked on
 * his decision. Both tests drive the PRODUCTION open path —
 * [MonolithDatabase.getInstance], the exact builder his phone
 * runs — against a pre-seeded database file, never a test-local
 * builder, so the configuration under test is the shipped one.
 *
 *  F2 [PLUMBING]: a pre-v7 file (seeded at user_version 6, with
 *     a sentinel table standing in for pre-v7 data) opens
 *     healthy at the current version: the sentinel is GONE
 *     (wiped, per the ruling — no pre-v7 schema exists anywhere
 *     to migrate from) and the current schema stands in its
 *     place, usable through the real DAO.
 *  F5 [PLUMBING]: a NEWER file (seeded one version above the
 *     current — data written by a future build, opened by this
 *     one)
 *     opens healthy at the current version the same way:
 *     wiped, never a crash loop, and no downgrade migration
 *     was hand-written to get there.
 *
 * There is deliberately no in-chain control here: versions
 * inside the supported chain (7 up) migrate for real, and the
 * chain tests (RoutinesMigrationChainTest and siblings) own
 * that claim. These two tests pin only the ruled exceptions;
 * §6.2's loud failure still governs every other missing path.
 *
 * The current version is pinned as a literal on purpose (repo
 * practice — cf. BreakMigrationTest's Callback(11)/(12)): it
 * is the @Database(version = …) value on MonolithDatabase in
 * the same tree this test ships with.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DestructiveFallbackRulingTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    /** Mirrors the storage-context choice in getInstance. */
    private val storageContext: Context
        get() {
            val appCtx = context.applicationContext
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appCtx.createDeviceProtectedStorageContext() ?: appCtx
            } else {
                appCtx
            }
        }

    @Before
    fun coldStart() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase(DB_NAME)
        context.deleteDatabase(DB_NAME)
    }

    @After
    fun teardown() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase(DB_NAME)
        context.deleteDatabase(DB_NAME)
    }

    /** Writes a bare database file at [version] holding one sentinel table with one row. */
    private fun seedFileAt(version: Int, sentinelTable: String) {
        val file = storageContext.getDatabasePath(DB_NAME)
        file.parentFile?.mkdirs()
        val raw = SQLiteDatabase.openOrCreateDatabase(file, null)
        try {
            raw.version = version
            raw.execSQL("CREATE TABLE IF NOT EXISTS `$sentinelTable` (id INTEGER PRIMARY KEY, note TEXT)")
            raw.execSQL("INSERT INTO `$sentinelTable` (id, note) VALUES (1, 'seeded data')")
        } finally {
            raw.close()
        }
    }

    private fun tableExists(db: SupportSQLiteDatabase, table: String): Boolean {
        db.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '$table'")
            .use { cursor -> return cursor.moveToFirst() }
    }

    /**
     * The ruling's shared shape, asserted for both exceptions:
     * the production database opens (no exception, no crash
     * loop), lands on the current version, the seeded sentinel
     * is wiped, the current schema exists, and the fresh
     * database is genuinely usable through the real DAO.
     */
    private fun assertOpensWipedAndHealthy(sentinelTable: String) = runBlocking {
        val db = MonolithDatabase.getInstance(context)
        val sql = db.openHelper.writableDatabase
        assertEquals(CURRENT_VERSION, sql.version)
        assertFalse("$sentinelTable must be wiped", tableExists(sql, sentinelTable))
        assertTrue(tableExists(sql, "focus_sessions"))
        assertTrue(tableExists(sql, "blocked_apps"))

        db.monolithDao().insertApp(
            BlockedAppEntity(
                packageName = "com.example.fresh",
                appName = "Fresh",
                category = "TEST",
                isBlocked = true
            )
        )
        assertEquals("Fresh", db.monolithDao().getAppByPackage("com.example.fresh")!!.appName)
    }

    /** [PLUMBING] F2 — pre-v7 database wipes and starts fresh (ruling 6544207912). */
    @Test
    fun preV7Database_wipesAndStartsFresh() {
        seedFileAt(version = 6, sentinelTable = "sentinel_pre_v7")
        assertOpensWipedAndHealthy("sentinel_pre_v7")
    }

    /** [PLUMBING] F5 — downgrade wipes and starts fresh (ruling 6544207912). */
    @Test
    fun newerDatabase_downgradeWipesAndStartsFresh() {
        seedFileAt(version = CURRENT_VERSION + 1, sentinelTable = "sentinel_from_newer_build")
        assertOpensWipedAndHealthy("sentinel_from_newer_build")
    }

    private companion object {
        /** The production database name in MonolithDatabase.getInstance. */
        const val DB_NAME = "monolith_tactile_db"

        /** @Database(version = …) on MonolithDatabase in this tree. */
        const val CURRENT_VERSION = 25
    }
}
