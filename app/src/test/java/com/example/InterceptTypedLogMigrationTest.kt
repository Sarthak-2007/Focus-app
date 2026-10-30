package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.DistractionTriggerReason
import com.example.data.InterceptLogEntity
import com.example.data.InterceptOutcomeType
import com.example.data.InterceptStopReason
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
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
 * Issue #324, Finding 3 — BUILD half (15→16): the typed
 * intercept log.
 *
 *  A: a v15-shaped intercept_logs takes 15→16 alone: the three
 *     typed columns land and the backfill stamps ONLY what each
 *     legacy row proves — outcome classified from its own string,
 *     stop reason only for the reserved packages and the
 *     uninstall outcome, trigger type only from a stated tag on
 *     an ordinary row. The legacy columns are untouched, and the
 *     struck testDateEpochDay exists nowhere.
 *  B: Room at v16 round-trips the typed fields through the DAO,
 *     and the entity's typed accessors read them back.
 *  C: the repository's write path stamps the typed layer —
 *     derived from the legacy strings when the caller passes
 *     nothing typed, verbatim when it does.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InterceptTypedLogMigrationTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "intercept_typed_migration_test.db"

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

    private fun createV15File() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(15) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `intercept_logs` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`timestampMillis` INTEGER NOT NULL, " +
                            "`packageName` TEXT NOT NULL, " +
                            "`appName` TEXT NOT NULL, " +
                            "`sessionIntention` TEXT NOT NULL, " +
                            "`outcome` TEXT NOT NULL, " +
                            "`triggerReason` TEXT NOT NULL)"
                    )
                    fun row(id: Int, pkg: String, outcome: String, trigger: String) {
                        db.execSQL(
                            "INSERT INTO intercept_logs (id, timestampMillis, packageName, appName, sessionIntention, outcome, triggerReason) " +
                                "VALUES ($id, ${1000L + id}, '$pkg', 'App $id', 'Deep Study', '$outcome', '$trigger')"
                        )
                    }
                    row(1, "com.video.app", "Stayed Focused", "Stuck")
                    row(2, "com.chat.app", "Used 2m Peek", "Message")
                    row(3, "com.android.settings", "Uninstall Attempt — Returned", "Habit")
                    row(4, "com.monolith.exposure", "Open exposure", "com.video.app")
                    row(5, "com.monolith.system", "Ended Early", "Habit")
                    row(6, "com.game.app", "Something Else Entirely", "???")
                    row(7, "com.video.app", "Intercepted", "Habit")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(createConfig).writableDatabase.close()
    }

    @Test
    fun migration15to16_backfillsOnlyWhatEachRowProves() {
        createV15File()
        val factory = FrameworkSQLiteOpenHelperFactory()
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
            val cols = columnsOf(db, "intercept_logs")
            assertTrue(cols.containsAll(setOf("outcomeType", "stopReason", "triggerReasonType")))
            assertFalse(cols.contains("testDateEpochDay"))

            // id → (outcomeType, stopReason, triggerReasonType)
            val expected = mapOf(
                1 to Triple("STAYED_FOCUSED", "UNKNOWN", "STUCK_ON_PROBLEM"),
                2 to Triple("USED_PEEK", "UNKNOWN", "NEED_INFO"),
                3 to Triple("UNINSTALL_ATTEMPT_RETURNED", "UNINSTALL_GUARD", "MUSCLE_MEMORY"),
                4 to Triple("OPEN_EXPOSURE", "OPEN_EXPOSURE", "UNKNOWN"),
                5 to Triple("ENDED_BY_DISARM", "SESSION_DISARM", "UNKNOWN"),
                6 to Triple("UNKNOWN", "UNKNOWN", "UNKNOWN"),
                7 to Triple("INTERCEPTED", "UNKNOWN", "MUSCLE_MEMORY")
            )
            db.query("SELECT id, outcomeType, stopReason, triggerReasonType, outcome FROM intercept_logs ORDER BY id")
                .use { cursor ->
                    while (cursor.moveToNext()) {
                        val id = cursor.getInt(0)
                        val want = expected.getValue(id)
                        assertEquals("row $id outcomeType", want.first, cursor.getString(1))
                        assertEquals("row $id stopReason", want.second, cursor.getString(2))
                        assertEquals("row $id triggerReasonType", want.third, cursor.getString(3))
                    }
                }
            // The legacy word is untouched — the slip still prints it.
            db.query("SELECT outcome FROM intercept_logs WHERE id = 3").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Uninstall Attempt — Returned", cursor.getString(0))
            }
        } finally {
            db.close()
        }
    }

    @Test
    fun roomAtV16_roundTripsTypedFields() = runBlocking {
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
            dao.insertInterceptLog(
                InterceptLogEntity(
                    timestampMillis = 5000L,
                    packageName = "com.video.app",
                    appName = "Video App",
                    sessionIntention = "Deep Study",
                    outcome = "Used 2m Peek",
                    triggerReason = "Message",
                    outcomeType = "USED_PEEK",
                    stopReason = "ROUTINE_WINDOW",
                    triggerReasonType = "NEED_INFO"
                )
            )
            val stored = dao.getInterceptLogsBetween(0L, 10_000L).single()
            assertEquals("USED_PEEK", stored.outcomeType)
            assertEquals("ROUTINE_WINDOW", stored.stopReason)
            assertEquals("NEED_INFO", stored.triggerReasonType)
            assertEquals(InterceptOutcomeType.USED_PEEK, stored.outcomeKind)
            assertEquals(InterceptStopReason.ROUTINE_WINDOW, stored.stopReasonKind)
            assertEquals(DistractionTriggerReason.NEED_INFO, stored.triggerReasonKind)
        } finally {
            db.close()
        }
    }

    @Test
    fun recordIntercept_stampsTypedLayer_derivedAndExplicit() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .build()
        try {
            val repository = MonolithRepository(context, db.monolithDao(), database = db)

            // Legacy-only call: the types derive from the strings.
            repository.recordIntercept(
                packageName = "com.video.app",
                appName = "Video App",
                sessionIntention = "Deep Study",
                outcome = "Stayed Focused",
                triggerReason = "Stuck"
            )
            // Explicit call: the caller's types stamp verbatim.
            repository.recordIntercept(
                packageName = "com.chat.app",
                appName = "Chat App",
                sessionIntention = "Deep Study",
                outcome = "Used 2m Peek",
                triggerReason = "Message",
                outcomeType = InterceptOutcomeType.USED_PEEK,
                stopReason = InterceptStopReason.SESSION_LOCK,
                triggerReasonType = DistractionTriggerReason.NEED_INFO
            )

            val rows = db.monolithDao().getInterceptLogsBetween(0L, Long.MAX_VALUE)
            assertEquals(2, rows.size)
            val derived = rows.single { it.packageName == "com.video.app" }
            assertEquals("STAYED_FOCUSED", derived.outcomeType)
            assertEquals("UNKNOWN", derived.stopReason)
            assertEquals("STUCK_ON_PROBLEM", derived.triggerReasonType)
            val explicit = rows.single { it.packageName == "com.chat.app" }
            assertEquals("USED_PEEK", explicit.outcomeType)
            assertEquals("SESSION_LOCK", explicit.stopReason)
            assertEquals("NEED_INFO", explicit.triggerReasonType)
        } finally {
            db.close()
        }
    }
}
