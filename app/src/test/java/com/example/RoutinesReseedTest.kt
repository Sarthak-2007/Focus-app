package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The version-blind reseed (Sten §2) against a file database, calling
 * reseedRoutinesV1 directly (it is internal; the test module is a
 * friend of main). Proven: exact shipped seeds migrate in place with
 * ids preserved, user-edited rows are never touched, name collisions
 * are hands-off, missing targets insert, the flag lands, and a second
 * run is a no-op.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoutinesReseedTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "routines_reseed_test.db"
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context.deleteDatabase(dbName)
        db = Room.databaseBuilder(context, MonolithDatabase::class.java, dbName)
            .addMigrations(MonolithDatabase.MIGRATION_7_8, MonolithDatabase.MIGRATION_8_9)
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun teardown() {
        db.close()
        context.deleteDatabase(dbName)
    }

    private suspend fun seedGuard() {
        db.monolithDao().insertGuardSettings(GuardSettingsEntity())
    }

    private suspend fun rows(): List<RoutineEntity> = db.monolithDao().getAllRoutinesSnapshot()

    @Test
    fun shippedSeeds_migrateInPlace_targetsComplete_flagSet() = runBlocking {
        seedGuard()
        val dao = db.monolithDao()
        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "Morning Deep Work", startMinutes = 540, endMinutes = 780,
                    daysMask = RoutineSchedule.MON_FRI, loadoutName = SmartLoadout.STUDY.name, enabled = false
                ),
                RoutineEntity(
                    name = "Night Wind-Down", startMinutes = 1350, endMinutes = 420,
                    daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.SLEEP.name, enabled = true
                )
            )
        )
        val before = rows().associateBy { it.name }

        repository.reseedRoutinesV1()

        val after = rows()
        assertEquals(3, after.size)
        val dayBlocks = after.single { it.name == "DAY BLOCKS" }
        assertEquals(before.getValue("Morning Deep Work").id, dayBlocks.id) // id preserved
        assertEquals(540, dayBlocks.startMinutes)
        assertEquals(1170, dayBlocks.endMinutes)
        assertEquals(RoutineSchedule.ALL_DAYS, dayBlocks.daysMask)
        assertEquals(false, dayBlocks.enabled) // enabled carried, not forced on
        val sleep = after.single { it.name == "SLEEP" }
        assertEquals(before.getValue("Night Wind-Down").id, sleep.id)
        assertEquals(1200, sleep.startMinutes)
        assertEquals(210, sleep.endMinutes)
        assertEquals(SmartLoadout.SLEEP.name, sleep.loadoutName)
        val fasted = after.single { it.name == "FASTED BLOCK" }
        assertEquals(300, fasted.startMinutes)
        assertEquals(480, fasted.endMinutes)
        assertTrue(after.all { it.scheduleEditedAtMillis > 0L })
        assertTrue(dao.getGuardSettingsSnapshot()!!.routineReseedV1Done)
    }

    @Test
    fun userEditedSeedRow_isNeverTouched() = runBlocking {
        seedGuard()
        val dao = db.monolithDao()
        // The shipped seed's NAME but hand-edited times: not an exact
        // match, so the transform must leave it exactly as it lies.
        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "Morning Deep Work", startMinutes = 500, endMinutes = 800,
                    daysMask = RoutineSchedule.MON_FRI, loadoutName = SmartLoadout.STUDY.name, enabled = true
                )
            )
        )
        val editedBefore = rows().single()

        repository.reseedRoutinesV1()

        val editedAfter = rows().single { it.id == editedBefore.id }
        assertEquals(editedBefore, editedAfter)
        // Targets still land around it (no name collision with them).
        assertEquals(
            setOf("Morning Deep Work", "FASTED BLOCK", "DAY BLOCKS", "SLEEP"),
            rows().map { it.name }.toSet()
        )
        assertTrue(dao.getGuardSettingsSnapshot()!!.routineReseedV1Done)
    }

    @Test
    fun targetNameCollision_isHandsOff_noDuplicateInserted() = runBlocking {
        seedGuard()
        val dao = db.monolithDao()
        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "SLEEP", startMinutes = 1300, endMinutes = 300,
                    daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.SLEEP.name, enabled = true
                )
            )
        )
        repository.reseedRoutinesV1()
        val sleeps = rows().filter { it.name == "SLEEP" }
        assertEquals(1, sleeps.size)
        assertEquals(1300, sleeps.single().startMinutes) // his own times stand
        assertEquals(0L, sleeps.single().scheduleEditedAtMillis) // untouched
    }

    @Test
    fun secondRun_isANoOp() = runBlocking {
        seedGuard()
        db.monolithDao().insertRoutines(
            listOf(
                RoutineEntity(
                    name = "Morning Deep Work", startMinutes = 540, endMinutes = 780,
                    daysMask = RoutineSchedule.MON_FRI, loadoutName = SmartLoadout.STUDY.name, enabled = true
                )
            )
        )
        repository.reseedRoutinesV1()
        val once = rows()
        repository.reseedRoutinesV1()
        assertEquals(once, rows())
    }
}
