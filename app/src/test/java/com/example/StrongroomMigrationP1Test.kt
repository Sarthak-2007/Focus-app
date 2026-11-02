package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.EmergencyBreakSpendEntity
import com.example.data.GuardSettingsEntity
import com.example.data.LegacyCarry
import com.example.data.MonolithDatabase
import com.example.data.MonolithWeek
import com.example.data.StrongroomStore
import com.example.data.toSigningRecord
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime
import java.time.ZoneId

/**
 * P1 (BUILD-SR-3) — MIGRATION PROOF, its own named tests.
 *
 *  A: a v25-shaped file takes 25→26 alone: the four Strongroom
 *     tables land with their full columns, and the legacy rows
 *     seeded before the migration (a task, a guard row, a blocked
 *     app, a session) read back byte-for-byte — his loadouts,
 *     rules and history are CARRIED, never discarded.
 *  B: Room itself at v26 — an in-memory build from the entities
 *     round-trips the Strongroom rows through the real DAO, so the
 *     entity schemas and the migrated schema are one shape; the
 *     store seals a day, spends the purse to its hard stop, and
 *     strikes its plate.
 *  C: the carry of MEANING — a legacy emergency-break spend booked
 *     against this week makes the crank born spent (never
 *     refunded), and LegacyCarry derives a signing from his actual
 *     loadout rows.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomMigrationP1Test {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "strongroom_migration_p1_test.db"

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

    private fun countRows(db: SupportSQLiteDatabase, table: String): Int {
        db.query("SELECT COUNT(*) FROM $table").use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else -1
        }
    }

    @Test
    fun migration25to26_createsStrongroomTables_andCarriesLegacyRowsUntouched() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val createConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(25) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // A v25 file's shape, the tables this proof reads
                    // back: minimal lawful forms (the migration under
                    // test touches none of them).
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`title` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_tasks (id, title) VALUES (7, 'his task, carried')")
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `guard_settings` (" +
                            "`id` INTEGER PRIMARY KEY NOT NULL, " +
                            "`breakMinutes` INTEGER NOT NULL)"
                    )
                    db.execSQL("INSERT INTO guard_settings (id, breakMinutes) VALUES (1, 25)")
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `blocked_apps` (" +
                            "`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, " +
                            "PRIMARY KEY(`packageName`))"
                    )
                    db.execSQL(
                        "INSERT INTO blocked_apps (packageName, appName) " +
                            "VALUES ('com.video.app', 'Video')"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        val helper = factory.create(createConfig)
        val db = helper.writableDatabase
        try {
            assertEquals(1, countRows(db, "focus_tasks"))

            MonolithDatabase.MIGRATION_25_26.migrate(db)

            // The new tables stand, whole.
            assertTrue(
                columnsOf(db, "strongroom_days").containsAll(
                    listOf(
                        "filingDateIso", "lockedPackagesCsv", "liftingHour", "liftingMinute",
                        "sentence", "breakLengthMinutes", "breakFiresAtOffsetMillis",
                        "allowanceTotalMinutes", "allowanceSpentMinutes", "gapsEncoded",
                        "sealedAtWallMillis", "sealedAtElapsedMillis", "sealedBootCount",
                        "spanDurationMillis", "state", "dayShape", "amendedBySigning", "taskLine",
                        "struckHeldMillis",
                        "struckBracketGapMillis", "crankSealBroken", "carriedFromSessionId"
                    )
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_punches").containsAll(
                    listOf("id", "filingDateIso", "kind", "atOffsetMillis", "packageName")
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_guard").containsAll(
                    listOf(
                        "id", "guardOn", "allowanceMinutes", "pendingAllowanceMinutes",
                        "pendingAllowanceFromMillis", "pendingOffFromMillis", "stretchEndMillis"
                    )
                )
            )
            assertTrue(
                columnsOf(db, "strongroom_crank_weeks").containsAll(
                    listOf("weekAnchorMillis", "spentSeconds", "sealBroken", "spentAtWallMillis", "carriedFromLegacySpend")
                )
            )
            assertEquals(0, countRows(db, "strongroom_days"))

            // The legacy rows are carried, untouched.
            db.query("SELECT title FROM focus_tasks WHERE id = 7").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("his task, carried", cursor.getString(0))
            }
            db.query("SELECT breakMinutes FROM guard_settings WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(25, cursor.getInt(0))
            }
            assertEquals(1, countRows(db, "blocked_apps"))
        } finally {
            helper.close()
        }
    }

    @Test
    fun roomV26_roundTripsStrongroomRows_andTheStoreRunsADay() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val store = StrongroomStore(db.monolithDao())
            val utc = ZoneId.of("UTC")
            val signing = com.example.domain.StrongroomDay.SigningRecord(
                filingDateIso = "2026-11-03",
                lockedPackages = setOf("com.video.app"),
                liftingTime = LocalTime.of(6, 0),
                sentence = null,
                breakLengthMinutes = 20,
                breakFiresAtOffsetMillis = 3_600_000L,
                allowanceMinutes = 30,
                gaps = listOf(
                    com.example.domain.StrongroomDay.ShapedGap(
                        com.example.domain.StrongroomDay.GapKind.GYM, 7_200_000L, 3_600_000L
                    )
                )
            )
            store.sign(signing)
            val readBack = store.day("2026-11-03")!!.toSigningRecord()
            assertEquals(signing.lockedPackages, readBack.lockedPackages)
            assertEquals(signing.gaps, readBack.gaps)
            assertNull(readBack.sentenceOrKeptPlace)

            // Seal at 2026-11-02T20:00Z for the 06:00 lifting: 10h span.
            val sealWall = java.time.LocalDateTime.of(2026, 11, 2, 20, 0)
                .atZone(utc).toInstant().toEpochMilli()
            val span = store.seal("2026-11-03", sealWall, sealedAtElapsedMillis = 100_000L, bootCount = 3, zone = utc)
            assertNotNull(span)
            assertEquals(36_000_000L, span!!.spanDurationMillis)

            // Purse: spend to the hard stop, the next minute refused.
            assertNotNull(store.spendAllowance("2026-11-03", 30))
            assertNull(store.spendAllowance("2026-11-03", 1))

            // A knock punch, then the plate at lifting.
            store.punch(
                "2026-11-03",
                com.example.domain.StrongroomDay.Punch(
                    com.example.domain.StrongroomDay.PunchKind.REACH, 60_000L, "com.video.app"
                )
            )
            val plate = store.strikePlate(
                "2026-11-03",
                nowElapsedMillis = 100_000L + 36_000_000L,
                nowBootCount = 3, sealFailed = false, zone = utc
            )!!
            assertEquals(com.example.domain.StrongroomDay.SealState.LIFTED, plate.sealState)
            assertEquals(1, plate.punches.size)
            assertEquals("LIFTED", store.day("2026-11-03")!!.state)

            // The Guard is born standing, at the named default.
            val guard = store.guardState(sealWall)
            assertTrue(guard.guardOn)
            assertEquals(StrongroomStore.DEFAULT_ALLOWANCE_MINUTES, guard.allowanceMinutes)
        } finally {
            db.close()
        }
    }

    @Test
    fun composition_unsignedDaySeals_andPlateStrikesOnce() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val store = StrongroomStore(db.monolithDao())
            val utc = ZoneId.of("UTC")
            val template = com.example.domain.StrongroomComposition.DayTemplate(
                lockedPackages = setOf("com.video.app"),
                liftingTime = LocalTime.of(6, 0),
                breakLengthMinutes = 20,
                breakFiresAtOffsetMillis = -1L,
                allowanceMinutes = 30,
                gaps = emptyList()
            )
            val order = com.example.domain.StrongroomComposition.WeekStandingOrder(
                shapeByWeekday = java.time.DayOfWeek.values()
                    .associateWith { com.example.domain.StrongroomComposition.DayShape.ORDINARY },
                templateByShape = mapOf(
                    com.example.domain.StrongroomComposition.DayShape.ORDINARY to template
                )
            )
            // No evening happened. The day still stands composed…
            val composed = store.composeDay(order, "2026-11-03")
            assertFalse(composed.amendedBySigning)
            assertEquals(
                com.example.data.StrongroomDayEntity.STATE_COMPOSED,
                store.day("2026-11-03")!!.state
            )
            // …and the swing seals it as it stands (B-2: the app
            // never checks whether the evening happened).
            val sealWall = java.time.LocalDateTime.of(2026, 11, 3, 5, 0)
                .atZone(utc).toInstant().toEpochMilli()
            val span = store.seal("2026-11-03", sealWall, 100_000L, bootCount = 3, zone = utc)
            assertNotNull(span)
            // The plate strikes once; a second strike returns the
            // plate as struck and writes nothing new (B-14).
            val first = store.strikePlate(
                "2026-11-03", nowElapsedMillis = 100_000L + span!!.spanDurationMillis,
                nowBootCount = 3, sealFailed = false, zone = utc
            )!!
            val second = store.strikePlate(
                "2026-11-03", nowElapsedMillis = 100_000L,
                nowBootCount = 9, sealFailed = true, zone = utc
            )!!
            assertEquals(first.sealState, second.sealState)
            assertEquals(first.hoursHeldMillis, second.hoursHeldMillis)
            assertEquals("LIFTED", store.day("2026-11-03")!!.state)
            // Amendment after the strike is refused.
            assertNull(
                store.amendDay(
                    order, "2026-11-03",
                    com.example.domain.StrongroomComposition.SigningAmendment(sentence = "too late")
                )
            )
        } finally {
            db.close()
        }
    }

    @Test
    fun carry_legacyWeekSpend_makesTheCrankBornSpent() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            val utc = ZoneId.of("UTC")
            val now = java.time.LocalDateTime.of(2026, 11, 4, 12, 0)
                .atZone(utc).toInstant().toEpochMilli() // a Wednesday
            val anchor = MonolithWeek.weekAnchorMillis(now, utc)
            dao.insertEmergencyBreakSpend(
                EmergencyBreakSpendEntity(
                    sessionId = 1,
                    spentAtWallMillis = now - 3_600_000L,
                    weekAnchorMillis = anchor,
                    heldSeconds = 60
                )
            )
            val store = StrongroomStore(dao)
            val week = store.crankWeek(now, utc)
            assertFalse(week.minuteAvailable) // his spent minute is never refunded
            assertTrue(week.sealBroken)
            assertNull(store.spendCrank(now, utc))
            // A week with no legacy spend stands fresh.
            val nextWeek = store.crankWeek(now + 7L * 24 * 3_600_000L, utc)
            assertTrue(nextWeek.minuteAvailable)
        } finally {
            db.close()
        }
    }

    @Test
    fun carry_legacyCarry_derivesSigningFromHisActualLoadout() {
        val apps = listOf(
            BlockedAppEntity(
                packageName = "com.video.app", appName = "Video", category = "VIDEO",
                isBlocked = true, blockTier = AppBlockTier.LOCKED.name
            ),
            BlockedAppEntity(
                packageName = "com.notes.app", appName = "Notes", category = "TOOLS",
                isBlocked = false, blockTier = AppBlockTier.OPEN.name
            ),
            BlockedAppEntity(
                packageName = "com.peek.app", appName = "Peek", category = "SOCIAL",
                isBlocked = true, blockTier = AppBlockTier.PEEK_2M.name
            )
        )
        val signing = LegacyCarry.deriveSigning(
            filingDateIso = "2026-11-03",
            blockedApps = apps,
            guardSettings = GuardSettingsEntity(breakMinutes = 25),
            activeSession = null,
            guardAllowanceMinutes = 30,
            activeSessionWindowEnd = null
        )
        // Only the LOCKED tier enters the locked set — peek and open
        // apps are not locked, and gaps never enter it at all.
        assertEquals(setOf("com.video.app"), signing.lockedPackages)
        assertEquals(25, signing.breakLengthMinutes) // his current break length, carried
        assertNull(signing.sentence) // no session stands: the kept place stands
        assertEquals(LegacyCarry.DEFAULT_LIFTING_TIME, signing.liftingTime)
        assertEquals(30, signing.allowanceMinutes)
    }
}
