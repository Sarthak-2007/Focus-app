package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DesiredSession
import com.example.data.FocusSessionEntity
import com.example.data.InterceptLogEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The §5.4 finalise funnel (routines rebuild) at the repository, on a
 * real file database. The laws proven here:
 * - booked seconds come from EVIDENCE only (Freya T4/T5): a ghost
 *   window books 0; stored tick-accrued elapsed is never inherited.
 * - booked ≤ window length in every arm, elapsedSeconds := attended
 *   in every arm (A2), success and artifacts only on ATTENDED.
 * - identity is routineId: rename and even deletion of the routine
 *   row cannot orphan a session's judgement (T9).
 * - history excludes unfinalised routine rows and A3 legacy rows.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoutinesFinaliseTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "routines_finalise_test.db"
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

    private suspend fun insertRoutine(loadout: String = SmartLoadout.STUDY.name): Int {
        val dao = db.monolithDao()
        dao.insertRoutines(
            listOf(
                RoutineEntity(
                    name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
                    daysMask = RoutineSchedule.ALL_DAYS, loadoutName = loadout, enabled = true
                )
            )
        )
        return dao.getAllRoutinesSnapshot().maxOf { it.id }
    }

    private suspend fun insertSession(
        routineId: Int?,
        windowStart: Long,
        windowEnd: Long,
        startWall: Long = windowStart,
        elapsed: Int = 0,
        active: Boolean = false,
        profileName: String = "Routine: DAY BLOCKS",
        outcome: String? = null
    ): Int = db.monolithDao().insertSession(
        FocusSessionEntity(
            intention = "DAY BLOCKS", profileName = profileName,
            targetDurationSeconds = 3600, elapsedSeconds = elapsed,
            startWallTimeMillis = startWall, startElapsedRealtimeMillis = 0L,
            isActive = active, completedSuccessfully = false,
            routineId = routineId, windowStartMillis = windowStart,
            windowEndMillis = windowEnd, routineOutcome = outcome
        )
    ).toInt()

    private suspend fun intercept(atMillis: Long) {
        db.monolithDao().insertInterceptLog(
            InterceptLogEntity(
                timestampMillis = atMillis, packageName = "com.example.distraction",
                appName = "Distraction", sessionIntention = "DAY BLOCKS", outcome = "Blocked"
            )
        )
    }

    private suspend fun row(id: Int): FocusSessionEntity =
        db.monolithDao().getSessionHistorySnapshot().single { it.id == id }

    private val hour = 3_600_000L
    private val minute = 60_000L

    @Test
    fun ghostWindow_zeroEvidence_booksZero_missed() = runBlocking {
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val id = insertSession(routineId, now - 3 * hour, now - 30 * minute)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("MISSED", outcome)
        val sealed = row(id)
        assertEquals(0, sealed.elapsedSeconds)
        assertFalse(sealed.completedSuccessfully)
        assertNull(sealed.forgedArtifactId)
        assertFalse(sealed.isActive)
    }

    @Test
    fun storedTickElapsed_isNeverInherited_bookedIsEvidenceOnly() = runBlocking {
        // Freya T5: the shield died mid-window; the row carries a fat
        // tick-accrued elapsed. Finalise must ignore it entirely.
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val id = insertSession(routineId, now - 3 * hour, now - 30 * minute, elapsed = 99_999)
        repository.finaliseRoutineSession(row(id), now)
        assertEquals(0, row(id).elapsedSeconds)
    }

    @Test
    fun denseIntercepts_attended_booksCoverage_andSetsE3() = runBlocking {
        val now = System.currentTimeMillis()
        val start = now - 2 * hour
        val end = start + hour // 60-minute window
        val routineId = insertRoutine()
        val id = insertSession(routineId, start, end)
        // An intercept every five minutes across the whole window:
        // union of 5-minute arcs covers the full hour.
        for (i in 0..11) intercept(start + i * 5 * minute)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("ATTENDED", outcome)
        val sealed = row(id)
        assertEquals(3600, sealed.elapsedSeconds)
        assertTrue(sealed.completedSuccessfully)
        assertTrue(sealed.evidenceMask and MonolithRepository.EVIDENCE_E3_INTERCEPT != 0)
    }

    @Test
    fun partialCoverage_booksPartialAtAttendedSeconds() = runBlocking {
        val now = System.currentTimeMillis()
        val start = now - 2 * hour
        val end = start + hour
        val routineId = insertRoutine()
        val id = insertSession(routineId, start, end)
        // Four intercepts 5 min apart from the window start: 20 minutes
        // of continuous coverage = 1200 s (≥ 900 floor, < 3600 − 900).
        for (i in 0..3) intercept(start + i * 5 * minute)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("PARTIAL", outcome)
        val sealed = row(id)
        assertEquals(1200, sealed.elapsedSeconds)
        assertFalse(sealed.completedSuccessfully)
        assertNull(sealed.forgedArtifactId)
    }

    @Test
    fun lateFire_judgedAgainstTheRemainder() = runBlocking {
        val now = System.currentTimeMillis()
        val start = now - 2 * hour
        val end = start + hour
        val routineId = insertRoutine()
        // Session materialised 30 minutes late into a 60-minute window.
        val id = insertSession(routineId, start, end, startWall = start + 30 * minute)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("LATE_FIRE", outcome)
        assertEquals(0, row(id).elapsedSeconds)
    }

    @Test
    fun lateFire_withStrongRemainderAttendance_attends() = runBlocking {
        val now = System.currentTimeMillis()
        val start = now - 2 * hour
        val end = start + hour
        val routineId = insertRoutine()
        val id = insertSession(routineId, start, end, startWall = start + 30 * minute)
        // Cover the whole 30-minute remainder (6 intercepts).
        for (i in 0..5) intercept(start + 30 * minute + i * 5 * minute)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("ATTENDED", outcome)
        assertEquals(1800, row(id).elapsedSeconds)
    }

    @Test
    fun renameAndDeleteRoutine_finaliseStillSealsBySnapshot() = runBlocking {
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val id = insertSession(routineId, now - 3 * hour, now - 30 * minute)
        // Rename the routine after the session exists (T9) …
        val routine = db.monolithDao().getRoutineById(routineId)!!
        db.monolithDao().updateRoutine(routine.copy(name = "RENAMED"))
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertEquals("MISSED", outcome)

        // … then delete the routine row entirely; a second session
        // still seals against its stored window alone.
        val id2 = insertSession(routineId, now - 5 * hour, now - 4 * hour)
        db.openHelper.writableDatabase.execSQL("DELETE FROM routines WHERE id = $routineId")
        assertNull(db.monolithDao().getRoutineById(routineId))
        assertEquals("MISSED", repository.finaliseRoutineSession(row(id2), now))
    }

    @Test
    fun finalise_isIdempotent_secondCallChangesNothing() = runBlocking {
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val id = insertSession(routineId, now - 3 * hour, now - 30 * minute)
        repository.finaliseRoutineSession(row(id), now)
        val first = row(id)
        // New evidence arriving later must NOT rewrite a sealed row.
        intercept(now - 2 * hour)
        val again = repository.finaliseRoutineSession(first, now)
        assertEquals("MISSED", again)
        assertEquals(first, row(id))
    }

    @Test
    fun history_excludesUnfinalisedRoutineRowsAndLegacyRows() = runBlocking {
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        // Finalised routine session: 1200 s PARTIAL, started today.
        val fin = insertSession(routineId, now - hour, now - 30 * minute)
        for (i in 0..3) intercept(now - hour + i * 5 * minute)
        repository.finaliseRoutineSession(row(fin), now)
        // Unfinalised routine session carrying junk elapsed: worth 0.
        insertSession(routineId, now - 2 * hour, now - hour, elapsed = 9_999)
        // Legacy A3 row: routineId null, prefix profile, window 0.
        insertSession(null, 0L, 0L, startWall = now - 30 * minute, elapsed = 8_888, profileName = "Routine: Old")
        // Manual session: counts in full.
        insertSession(null, 0L, 0L, startWall = now - 20 * minute, elapsed = 600, profileName = "Manual")
        val today = repository.compute7DayHistory().last()
        assertEquals((1200 + 600) / 60, today.totalMinutes)
    }

    @Test
    fun auditInvariant_everyFinalisedRow_booksAtMostItsWindow() = runBlocking {
        // Freya T10, as an invariant over sealed rows: booked ≤ window,
        // success ⇔ ATTENDED, artifact only on ATTENDED.
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val a = insertSession(routineId, now - 2 * hour, now - hour) // ghost → MISSED
        val b = insertSession(routineId, now - 4 * hour, now - 3 * hour)
        for (i in 0..11) intercept(now - 4 * hour + i * 5 * minute) // full → ATTENDED
        repository.finaliseRoutineSession(row(a), now)
        repository.finaliseRoutineSession(row(b), now)
        for (sealed in db.monolithDao().getSessionHistorySnapshot().filter { it.routineOutcome != null }) {
            val windowSec = (sealed.windowEndMillis - sealed.windowStartMillis) / 1000
            assertTrue(sealed.elapsedSeconds <= windowSec)
            assertEquals(sealed.routineOutcome == "ATTENDED", sealed.completedSuccessfully)
            if (sealed.routineOutcome != "ATTENDED") assertNull(sealed.forgedArtifactId)
        }
    }

    @Test
    fun sleepRoutineSession_isStoodDown_neverStamped() = runBlocking {
        val now = System.currentTimeMillis()
        val sleepId = insertRoutine(loadout = SmartLoadout.SLEEP.name)
        val id = insertSession(sleepId, now - 8 * hour, now - hour, active = true)
        val outcome = repository.finaliseRoutineSession(row(id), now)
        assertNull(outcome)
        val sealed = row(id)
        assertNull(sealed.routineOutcome)
        assertFalse(sealed.isActive)
    }

    @Test
    fun materialise_snapshotsWindowAndSetsE1() = runBlocking {
        val now = System.currentTimeMillis()
        val routineId = insertRoutine()
        val session = repository.materialiseRoutineSession(
            DesiredSession(
                routineId = routineId,
                windowStartMillis = now - 15 * minute,
                windowEndMillis = now + 45 * minute,
                durationMinutes = 45,
                loadoutName = SmartLoadout.STUDY.name,
                intention = "DAY BLOCKS",
                profileName = "Routine: DAY BLOCKS"
            )
        )
        assertEquals(routineId, session.routineId)
        assertEquals(now - 15 * minute, session.windowStartMillis)
        assertEquals(now + 45 * minute, session.windowEndMillis)
        assertEquals(45 * 60, session.targetDurationSeconds)
        assertEquals(MonolithRepository.EVIDENCE_E1_START, session.evidenceMask and MonolithRepository.EVIDENCE_E1_START)
        assertEquals(0L, session.firstEvidenceMillis)
    }
}
