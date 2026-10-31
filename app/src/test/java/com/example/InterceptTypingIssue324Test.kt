package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.InterceptLogEntity
import com.example.data.InterceptOutcomeKind
import com.example.data.InterceptTriggerKind
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
 * Issue #324, finding 3 (build half): the intercept log's typed
 * event.
 *
 *  A: every word a writer in the tree produces maps to its kind,
 *     case-insensitively; an unproduced word is UNKNOWN, never
 *     guessed; the block set is exactly the receipt allowlist's.
 *  B: the trigger derivation — shortTags map back to their
 *     reasons; the reserved system packages are SYSTEM whatever
 *     their reason column carries.
 *  C: migration 18→19 on a v18-shaped intercept_logs: both kind
 *     columns land and the backfill derives each row's kind from
 *     the facts the row already carries (word, shortTag, package).
 *  D: Room at v19 round-trips the kinds: a row built with kinds
 *     reads them back, and a row built WITHOUT kinds (the
 *     pre-typing construction every older test uses) derives its
 *     typed values from its legacy words on read.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class InterceptTypingIssue324Test {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "intercept_typing_324_test.db"

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
    }

    @Test
    fun outcomeKinds_coverEveryWriterWord() {
        val cases = mapOf(
            "Blocked" to InterceptOutcomeKind.BLOCKED,
            "BLOCKED" to InterceptOutcomeKind.BLOCKED,
            "Stayed Focused" to InterceptOutcomeKind.STAYED_FOCUSED,
            "Split Into 10m Micro-Step" to InterceptOutcomeKind.SPLIT_INTO_MICRO_STEP,
            "Used 2m Peek" to InterceptOutcomeKind.USED_PEEK,
            "Intercepted" to InterceptOutcomeKind.INTERCEPTED,
            "Left Airlock Without Choosing" to InterceptOutcomeKind.LEFT_WITHOUT_CHOOSING,
            "Opened Launcher" to InterceptOutcomeKind.OPENED_LAUNCHER,
            "Uninstall Attempt — Returned" to InterceptOutcomeKind.UNINSTALL_ATTEMPT_RETURNED,
            "Open exposure" to InterceptOutcomeKind.OPEN_EXPOSURE
        )
        for ((word, kind) in cases) {
            assertEquals(word, kind, InterceptOutcomeKind.fromStoredWord(word))
            // The kind's own stored word round-trips to itself.
            assertEquals(kind, InterceptOutcomeKind.fromStoredWord(kind.storedWord!!))
        }
        assertEquals(
            InterceptOutcomeKind.UNKNOWN,
            InterceptOutcomeKind.fromStoredWord("Some Word No Writer Produces")
        )
        assertEquals(InterceptOutcomeKind.UNKNOWN, InterceptOutcomeKind.fromStoredWord(""))
    }

    @Test
    fun outcomeKind_blockSetMatchesReceiptAllowlist() {
        assertTrue(InterceptOutcomeKind.BLOCKED.isBlock)
        assertTrue(InterceptOutcomeKind.STAYED_FOCUSED.isBlock)
        assertTrue(InterceptOutcomeKind.SPLIT_INTO_MICRO_STEP.isBlock)
        assertFalse(InterceptOutcomeKind.USED_PEEK.isBlock)
        assertFalse(InterceptOutcomeKind.INTERCEPTED.isBlock)
        assertFalse(InterceptOutcomeKind.LEFT_WITHOUT_CHOOSING.isBlock)
        assertFalse(InterceptOutcomeKind.OPEN_EXPOSURE.isBlock)
        assertFalse(InterceptOutcomeKind.SESSION_END.isBlock)
        assertFalse(InterceptOutcomeKind.UNKNOWN.isBlock)
    }

    @Test
    fun triggerKind_derivesFromShortTagAndReservedPackages() {
        assertEquals(InterceptTriggerKind.HABIT, InterceptTriggerKind.derive("Habit", "com.example.app"))
        assertEquals(InterceptTriggerKind.STUCK, InterceptTriggerKind.derive("Stuck", "com.example.app"))
        assertEquals(InterceptTriggerKind.TIRED, InterceptTriggerKind.derive("Tired", "com.example.app"))
        assertEquals(InterceptTriggerKind.MESSAGE, InterceptTriggerKind.derive("Message", "com.example.app"))
        assertEquals(InterceptTriggerKind.UNKNOWN, InterceptTriggerKind.derive("com.other.app", "com.example.app"))
        // Exposure rows carry the exposed package as their reason —
        // the reserved package, not the reason, makes them SYSTEM.
        assertEquals(
            InterceptTriggerKind.SYSTEM,
            InterceptTriggerKind.derive("com.other.app", "com.monolith.exposure")
        )
        assertEquals(
            InterceptTriggerKind.SYSTEM,
            InterceptTriggerKind.derive("Habit", "com.monolith.system")
        )
    }

    @Test
    fun migration18to19_backfillsKindsFromRowFacts() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(18) {
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
                    db.execSQL(
                        "INSERT INTO intercept_logs (id, timestampMillis, packageName, appName, sessionIntention, outcome, triggerReason) VALUES " +
                            "(1, 1, 'com.video.app', 'Video', 'Study', 'Stayed Focused', 'Stuck')," +
                            "(2, 2, 'com.video.app', 'Video', 'Study', 'Used 2m Peek', 'Message')," +
                            "(3, 3, 'com.monolith.exposure', 'Video', 'Study', 'Open exposure', 'com.video.app')," +
                            "(4, 4, 'com.monolith.system', 'Unlatched Early', 'Study', 'Ended Early', 'Habit')," +
                            "(5, 5, 'com.video.app', 'Video', 'Study', 'A Word Nobody Wrote', 'Habit')"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        factory.create(createConfig).writableDatabase.close()

        val openConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(19) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    assertEquals(18, oldVersion)
                    assertEquals(19, newVersion)
                    MonolithDatabase.MIGRATION_18_19.migrate(db)
                }
            })
            .build()
        val db = factory.create(openConfig).writableDatabase
        try {
            val rows = mutableMapOf<Int, Pair<String, String>>()
            db.query("SELECT id, outcomeKind, triggerKind FROM intercept_logs ORDER BY id").use { cursor ->
                while (cursor.moveToNext()) {
                    rows[cursor.getInt(0)] = cursor.getString(1) to cursor.getString(2)
                }
            }
            assertEquals("STAYED_FOCUSED" to "STUCK", rows[1])
            assertEquals("USED_PEEK" to "MESSAGE", rows[2])
            assertEquals("OPEN_EXPOSURE" to "SYSTEM", rows[3])
            assertEquals("SESSION_END" to "SYSTEM", rows[4])
            assertEquals("UNKNOWN" to "HABIT", rows[5])
        } finally {
            db.close()
        }
    }

    @Test
    fun roomAtV19_roundTripsKinds_andDerivesForUntypedRows() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java).build()
        try {
            val dao = db.monolithDao()
            dao.insertInterceptLog(
                InterceptLogEntity(
                    packageName = "com.video.app",
                    appName = "Video",
                    sessionIntention = "Study",
                    outcome = "Stayed Focused",
                    triggerReason = "Stuck",
                    outcomeKind = InterceptOutcomeKind.STAYED_FOCUSED.name,
                    triggerKind = InterceptTriggerKind.STUCK.name
                )
            )
            dao.insertInterceptLog(
                InterceptLogEntity(
                    packageName = "com.video.app",
                    appName = "Video",
                    sessionIntention = "Study",
                    outcome = "Used 2m Peek",
                    triggerReason = "Message"
                )
            )
            val rows = dao.getInterceptLogsBetween(0L, Long.MAX_VALUE).sortedBy { it.id }
            assertEquals(2, rows.size)
            assertEquals(InterceptOutcomeKind.STAYED_FOCUSED, rows[0].outcomeKindTyped)
            assertEquals(InterceptTriggerKind.STUCK, rows[0].triggerKindTyped)
            // Untyped construction: persisted columns say UNKNOWN,
            // the typed reads derive from the legacy words.
            assertEquals("UNKNOWN", rows[1].outcomeKind)
            assertEquals(InterceptOutcomeKind.USED_PEEK, rows[1].outcomeKindTyped)
            assertEquals(InterceptTriggerKind.MESSAGE, rows[1].triggerKindTyped)
        } finally {
            db.close()
        }
    }
}
