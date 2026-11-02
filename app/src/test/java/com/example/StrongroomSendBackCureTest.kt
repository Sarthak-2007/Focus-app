package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomDayEntity
import com.example.data.StrongroomHeldArrivalEntity
import com.example.data.StrongroomStore
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.service.StrongroomEngineBridge
import com.example.ui.strongroom.SigningAppOption
import com.example.ui.strongroom.StrongroomFlowModel
import java.time.DayOfWeek
import java.time.LocalTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BUILD-SR-28 — THE SEND-BACK CURES, pinned by name (CRITIC-SR-8's
 * Slice-1 send-back, Gates 2–3):
 *
 *  K1 · R2-8 — the host's draft (StrongroomFlowModel.signingDraft,
 *       the exact function SigningPlace calls) carries the swing's
 *       teaching at the FIRST signing and at no other; the
 *       first-signing fact is P1's store's own (no signing record
 *       stands before it — composed days alone do not spend it).
 *  K2 · W-7 — the drum's spend lands in the store at the taking
 *       (spendAllowance, the caller the shipped tree lacked), so
 *       the purse the antechamber re-derives on re-entry is the
 *       day's own spent purse, never a fresh full one; and the
 *       turned name resolves to exactly the drum-stock package.
 *  K3 · W-6 — a held arrival recorded in P1's new held record
 *       appears in the pile the antechamber builds, and resolving
 *       it (TAKE) discharges that line only.
 *  + MIGRATION_26_27 — CREATE-only: the held record lands, his
 *       existing rows read back untouched.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomSendBackCureTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private var db: MonolithDatabase? = null

    @After
    fun teardown() {
        db?.close()
        db = null
        context.deleteDatabase(migrationDbName)
    }

    private fun openDb(): MonolithDatabase {
        val built = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db = built
        return built
    }

    private fun signingRecord(
        dateIso: String = "2026-11-03",
        allowance: Int = 30
    ) = StrongroomDay.SigningRecord(
        filingDateIso = dateIso,
        lockedPackages = setOf("com.locked"),
        allowedPackages = listOf("com.allowed"),
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = 20,
        breakFiresAtOffsetMillis = -1L,
        allowanceMinutes = allowance,
        gaps = emptyList()
    )

    private fun order() = StrongroomComposition.WeekStandingOrder(
        shapeByWeekday = DayOfWeek.values().associateWith {
            StrongroomComposition.DayShape.ORDINARY
        },
        templateByShape = mapOf(
            StrongroomComposition.DayShape.ORDINARY to StrongroomComposition.DayTemplate(
                lockedPackages = setOf("com.locked"),
                allowedPackages = listOf("com.allowed"),
                liftingTime = LocalTime.of(20, 0),
                breakLengthMinutes = 20,
                breakFiresAtOffsetMillis = -1L,
                allowanceMinutes = 30,
                gaps = emptyList()
            )
        )
    )

    private fun draft(firstSigning: Boolean) = StrongroomFlowModel.signingDraft(
        dayShapeName = "ORDINARY",
        appOptions = listOf(
            SigningAppOption("com.locked", "Locked"),
            SigningAppOption("com.allowed", "Allowed")
        ),
        composedLocked = setOf("com.locked"),
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = 20,
        spentDay = false,
        firstSigning = firstSigning
    )

    // ------------------------------------------------------------------
    // K1 — the teaching fires once, and never twice
    // ------------------------------------------------------------------

    @Test
    fun k1_teaching_firesAtFirstSigning_andNeverAtAnOrdinaryOne() {
        val first = draft(firstSigning = true)
        assertNotNull("the first signing must carry the swing's teaching", first.teachingLine)
        assertEquals(StrongroomFlowModel.SWING_TEACHING_LINE, first.teachingLine)
        assertTrue(
            "the teaching must name the swing's own gesture (the hold)",
            first.teachingLine!!.contains("hold the door")
        )
        val ordinary = draft(firstSigning = false)
        assertNull("an ordinary signing carries no teaching", ordinary.teachingLine)
    }

    @Test
    fun k1_firstSigningFact_composedDaysDoNotSpendIt_aSigningDoes() = runBlocking {
        val store = StrongroomStore(openDb().monolithDao())
        // Nothing stands: the first signing is still owed.
        assertTrue(store.isFirstSigning())
        // A composed day (the standing order's work, not a
        // signing) does not spend the first signing.
        store.composeDay(order(), "2026-11-03")
        assertTrue(store.isFirstSigning())
        // His signing lands: never again.
        store.sign(signingRecord("2026-11-03"))
        assertTrue(!store.isFirstSigning())
        // The pure form over rows reads the same facts.
        assertTrue(
            StrongroomStore(openDb().monolithDao()).isFirstSigning(
                listOf(
                    StrongroomDayEntity(
                        filingDateIso = "2026-11-03",
                        liftingHour = 20,
                        liftingMinute = 0,
                        state = StrongroomDayEntity.STATE_COMPOSED,
                        amendedBySigning = false
                    )
                )
            )
        )
    }

    // ------------------------------------------------------------------
    // K2 — the drum's spend persists across re-entry
    // ------------------------------------------------------------------

    @Test
    fun k2_drumSpend_persistsInStore_antechamberReentryReadsSpentPurse() = runBlocking {
        val dao = openDb().monolithDao()
        val store = StrongroomStore(dao)
        store.sign(signingRecord())
        // The taking, exactly as the host now performs it.
        val afterSpend = store.spendAllowance("2026-11-03", 1)
        assertNotNull(afterSpend)
        assertEquals(29, afterSpend!!.remainingMinutes)
        // Re-entry: the host re-reads the day row and rebuilds the
        // room from the store — the purse is 29, never a fresh 30.
        val row = store.day("2026-11-03")!!
        assertEquals(1, row.allowanceSpentMinutes)
        val room = StrongroomFlowModel.antechamberState(
            signing = signingRecord(),
            purseRemainingMinutes = row.allowanceTotalMinutes - row.allowanceSpentMinutes,
            nameOf = { it },
            cataloguePackages = listOf("com.locked", "com.allowed", "com.drum")
        )
        assertEquals(29, room.purseMinutes)
        // And the drum's own arithmetic on that re-derived state
        // spends the NEXT minute, not the same one again.
        val (afterSecond, outcome) = room.crankTo("com.drum")
        assertEquals(com.example.ui.strongroom.CrankOutcome.TURNED, outcome)
        assertEquals(28, afterSecond.purseMinutes)
    }

    @Test
    fun k2_drumPackageFor_resolvesOnlyDrumStock() {
        val nameOf = { pkg: String ->
            mapOf(
                "com.locked" to "Locked",
                "com.allowed" to "Allowed",
                "com.drum" to "Drum App"
            )[pkg] ?: pkg
        }
        val catalogue = listOf("com.locked", "com.allowed", "com.drum")
        assertEquals(
            "com.drum",
            StrongroomFlowModel.drumPackageFor(signingRecord(), catalogue, nameOf, "Drum App")
        )
        // A signed name is not drum stock; a locked name neither;
        // an unknown name resolves to nothing — no launch, no spend.
        assertNull(StrongroomFlowModel.drumPackageFor(signingRecord(), catalogue, nameOf, "Allowed"))
        assertNull(StrongroomFlowModel.drumPackageFor(signingRecord(), catalogue, nameOf, "Locked"))
        assertNull(StrongroomFlowModel.drumPackageFor(signingRecord(), catalogue, nameOf, "Nope"))
    }

    // ------------------------------------------------------------------
    // K3 — a recorded held arrival appears in the pile
    // ------------------------------------------------------------------

    @Test
    fun k3_heldArrivals_recorded_appearInPile_takeDischargesThatLineOnly() = runBlocking {
        val dao = openDb().monolithDao()
        val store = StrongroomStore(dao)
        val callId = store.recordHeldArrival(
            "2026-11-03", StrongroomHeldArrivalEntity.KIND_CALL,
            "+919876543210", 1_700_000_000_000L
        )
        store.recordHeldArrival(
            "2026-11-03", StrongroomHeldArrivalEntity.KIND_CALL,
            "+919123456780", 1_700_000_100_000L
        )
        store.recordHeldArrival(
            "2026-11-03", StrongroomHeldArrivalEntity.KIND_CODE,
            "482916", 1_700_000_200_000L
        )
        fun pile() = StrongroomFlowModel.antechamberState(
            signing = signingRecord(),
            purseRemainingMinutes = 30,
            nameOf = { it },
            cataloguePackages = listOf("com.locked", "com.allowed"),
            heldArrivals = runBlocking { store.heldArrivals("2026-11-03") }
        )
        val before = pile()
        assertEquals(2, before.heldCalls.size)
        assertEquals("+919876543210", before.heldCalls[0].number)
        assertEquals(callId.toString(), before.heldCalls[0].id)
        assertEquals(1, before.heldCodes.size)
        assertEquals("482916", before.heldCodes[0].digits)
        // TAKE discharges that line only: the other call and the
        // code still wait.
        store.resolveHeldArrival(callId.toInt(), taken = true)
        val after = pile()
        assertEquals(1, after.heldCalls.size)
        assertEquals("+919123456780", after.heldCalls[0].number)
        assertEquals(1, after.heldCodes.size)
        // LEAVE likewise leaves the pile, persisted.
        val leftId = after.heldCalls[0].id.toInt()
        store.resolveHeldArrival(leftId, taken = false)
        assertTrue(pile().heldCalls.isEmpty())
        assertEquals(1, pile().heldCodes.size)
    }

    @Test
    fun k3_bridgeExtraction_numberAndDigits_fromNotificationWords() {
        assertEquals(
            "+919876543210",
            StrongroomEngineBridge.extractCallerNumber("Incoming call +91 98765 43210")
        )
        assertNull(
            "a contact's name carries no number to extract",
            StrongroomEngineBridge.extractCallerNumber("Incoming call — Mama")
        )
        assertEquals(
            "482916",
            StrongroomEngineBridge.extractHeldCodeDigits("Your verification code is 482916. Do not share.")
        )
        assertNull(StrongroomEngineBridge.extractHeldCodeDigits("See you at 7"))
    }

    // ------------------------------------------------------------------
    // The migration — 26→27 CREATE-only
    // ------------------------------------------------------------------

    private val migrationDbName = "strongroom_sendback_cure_migration.db"

    @Test
    fun migration26to27_createsHeldArrivals_andCarriesExistingRowsUntouched() {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(migrationDbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(26) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `focus_tasks` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`title` TEXT NOT NULL)"
                    )
                    db.execSQL("INSERT INTO focus_tasks (id, title) VALUES (7, 'his task, carried')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()
        val db = factory.create(config).writableDatabase
        try {
            MonolithDatabase.MIGRATION_26_27.migrate(db)
            val cols = mutableSetOf<String>()
            db.query("PRAGMA table_info(strongroom_held_arrivals)").use { cursor ->
                val nameIdx = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) cols.add(cursor.getString(nameIdx))
            }
            assertEquals(
                setOf("id", "filingDateIso", "kind", "line", "arrivedAtWallMillis", "state"),
                cols
            )
            db.query("SELECT title FROM focus_tasks WHERE id = 7").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("his task, carried", cursor.getString(0))
            }
        } finally {
            db.close()
        }
    }
}
