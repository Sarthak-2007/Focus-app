package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.InterceptStopReason
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.countByStopReason
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #785 (the #324 follow-on, sequenced after #341 PR-B):
 * MIGRATION_24_25 — the typed stop-reason column.
 *
 * Two halves, both pinned here:
 *  - The step itself, driven directly over a v24-shaped
 *    intercept_logs fixture (the Issue341BuildMigrationTest
 *    direct-step pattern): the column lands with its default,
 *    and the backfill stamps ONLY what a pre-v25 row proves —
 *    the two reserved packages and the uninstall outcome. An
 *    exposure-worded outcome on an ordinary package stays
 *    UNKNOWN: the package is the evidence, never the prose.
 *  - The write half at v25 (in-memory Room): the repository
 *    stamps the caller's door verdict, derives from the row's
 *    own facts when the caller passes none, and the arrival
 *    stamp survives the row's later resolution untouched —
 *    the aggregation then counts the written rows by type.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StopReasonMigrationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
        val cols = mutableSetOf<String>()
        db.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIdx = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) cols += cursor.getString(nameIdx)
        }
        return cols
    }

    @Test
    fun migration24to25_landsColumn_backfillsOnlyProvenRows() {
        val dbName = "stopreason-migration-test.db"
        context.deleteDatabase(dbName)
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(24) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // The v24 shape of intercept_logs (every
                    // column the v25 entity knows except the new
                    // one), plus the timestamp index.
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `intercept_logs` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`timestampMillis` INTEGER NOT NULL, " +
                            "`packageName` TEXT NOT NULL, " +
                            "`appName` TEXT NOT NULL, " +
                            "`sessionIntention` TEXT NOT NULL, " +
                            "`outcome` TEXT NOT NULL, " +
                            "`triggerReason` TEXT NOT NULL, " +
                            "`outcomeKind` TEXT NOT NULL, " +
                            "`triggerKind` TEXT NOT NULL)"
                    )
                    db.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_intercept_logs_timestampMillis` " +
                            "ON `intercept_logs` (`timestampMillis`)"
                    )
                    fun seed(packageName: String, outcome: String) {
                        db.execSQL(
                            "INSERT INTO `intercept_logs` " +
                                "(`timestampMillis`, `packageName`, `appName`, " +
                                "`sessionIntention`, `outcome`, `triggerReason`, " +
                                "`outcomeKind`, `triggerKind`) VALUES " +
                                "(1, '$packageName', 'App', 'Deep Study', " +
                                "'$outcome', 'UNSTATED', 'UNKNOWN', 'UNKNOWN')"
                        )
                    }
                    seed("com.monolith.system", "Ended Early")
                    seed("com.monolith.exposure", "Open exposure")
                    seed("com.android.settings", "Uninstall Attempt — Returned")
                    seed("com.video.app", "Stayed Focused")
                    seed("com.video.app", "Open exposure")
                    seed("com.video.app", "AT DOOR")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, old: Int, new: Int) = Unit
            })
            .build()
        val helper = factory.create(config)
        val db = helper.writableDatabase
        try {
            MonolithDatabase.MIGRATION_24_25.migrate(db)

            assertTrue(columnsOf(db, "intercept_logs").contains("stopReason"))
            val stamped = mutableListOf<Pair<String, String>>()
            db.query(
                "SELECT `packageName`, `stopReason` FROM `intercept_logs` ORDER BY `id`"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    stamped += cursor.getString(0) to cursor.getString(1)
                }
            }
            assertEquals(
                listOf(
                    "com.monolith.system" to "SESSION_DISARM",
                    "com.monolith.exposure" to "OPEN_EXPOSURE",
                    "com.android.settings" to "UNINSTALL_GUARD",
                    "com.video.app" to "UNKNOWN",
                    "com.video.app" to "UNKNOWN",
                    "com.video.app" to "UNKNOWN"
                ),
                stamped
            )
        } finally {
            helper.close()
            context.deleteDatabase(dbName)
        }
    }

    @Test
    fun repositoryStamps_writeThrough_andAggregationCounts() = runBlocking {
        val room = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = MonolithRepository(context, room.monolithDao())

            // A caller holding the door stamps its verdict.
            repository.recordIntercept(
                packageName = "com.video.app",
                appName = "Video App",
                sessionIntention = "Deep Study",
                outcome = "Stayed Focused",
                triggerReason = "Habit",
                stopReason = InterceptStopReason.SESSION_LOCK
            )
            // A caller passing nothing stamps what the row's own
            // facts prove — the system package derives DISARM at
            // the write, an ordinary package stays UNKNOWN.
            repository.recordIntercept(
                packageName = "com.monolith.system",
                appName = "System",
                sessionIntention = "Deep Study",
                outcome = "Ended Early",
                triggerReason = "UNSTATED"
            )
            repository.recordIntercept(
                packageName = "com.video.app",
                appName = "Video App",
                sessionIntention = "Deep Study",
                outcome = "Stayed Focused",
                triggerReason = "Habit"
            )

            // The arrival stamp rides the row through its later
            // resolution: resolveIntercept rewrites the outcome
            // only, never the reason.
            val arrivalId = repository.bookInterceptArrival(
                packageName = "com.tiktok.app",
                appName = "TikTok",
                sessionIntention = "Deep Study",
                arrivedAtWallMillis = 1_000L,
                stopReason = InterceptStopReason.SNIPER
            )
            assertTrue(arrivalId > 0)
            repository.resolveIntercept(
                packageName = "com.tiktok.app",
                outcome = "Stayed Focused",
                resolvedAtWallMillis = 2_000L
            )

            val rows = room.monolithDao().getAllInterceptLogs().first()
            assertEquals(4, rows.size)
            // Two video rows share package+outcome; the stamped
            // one is identified by its column.
            assertEquals(
                InterceptStopReason.SESSION_LOCK,
                rows.first {
                    it.packageName == "com.video.app" && it.stopReason == "SESSION_LOCK"
                }.stopReasonTyped
            )
            assertEquals(
                InterceptStopReason.SESSION_DISARM,
                rows.first { it.packageName == "com.monolith.system" }.stopReasonTyped
            )
            assertEquals(
                InterceptStopReason.SNIPER,
                rows.first { it.packageName == "com.tiktok.app" }.stopReasonTyped
            )
            assertEquals(
                "Stayed Focused",
                rows.first { it.packageName == "com.tiktok.app" }.outcome
            )
            assertEquals(
                mapOf(
                    InterceptStopReason.SESSION_LOCK to 1,
                    InterceptStopReason.SESSION_DISARM to 1,
                    InterceptStopReason.SNIPER to 1,
                    InterceptStopReason.UNKNOWN to 1
                ),
                rows.countByStopReason()
            )
        } finally {
            room.close()
        }
    }
}
