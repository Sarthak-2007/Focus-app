package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ArmZoneStore
import com.example.data.DayWordStore
import com.example.data.FinishKind
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.TimeZone

/**
 * Issue #152 residue (reopened) — the cross-zone refusal itself,
 * at the repository, which had ZERO coverage: the landed F1 cure
 * was proven only at the store's witness, never at the guard
 * that reads it. Three refusal shapes are pinned here:
 *
 *  1. CHANGE — a different word for a witnessed past day,
 *     declared from another zone, is refused.
 *  2. CLEAR — deleting a witnessed past day's word from another
 *     zone is refused (the landed guard fired only when the new
 *     word was non-null, so erasure crossed zones freely).
 *  3. SAME-WORD RE-DECLARE — lawful (it rewrites nothing), but
 *     it must NOT move the witness: after it, shape 1 must still
 *     be refused. (The store overwrote the witness
 *     unconditionally, so a same-word tap after landing laundered
 *     the date into the arrival zone and re-opened shape 1.)
 *
 * Plus the F2 ENDED half: the end zone is captured at the
 * session-ending write and threads through publish + re-issue,
 * so a slip read in a third zone still prints the witnessed end.
 *
 * Zone travel is simulated with TimeZone.setDefault — the same
 * device-default the production code reads via
 * ZoneId.systemDefault() — and the original default is restored
 * after every test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DayWordCrossZoneRefusalTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val dbName = "day_word_cross_zone_test.db"
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private lateinit var originalZone: TimeZone

    private val dayWordPrefs
        get() = context.getSharedPreferences(
            MonolithRepository.DAY_WORDS_PREFS_NAME, Context.MODE_PRIVATE
        )
    private val armZonePrefs
        get() = context.getSharedPreferences("monolith_arm_zone", Context.MODE_PRIVATE)

    @Before
    fun setup() {
        originalZone = TimeZone.getDefault()
        context.deleteDatabase(dbName)
        dayWordPrefs.edit().clear().commit()
        armZonePrefs.edit().clear().commit()
        db = Room.databaseBuilder(context, MonolithDatabase::class.java, dbName)
            .addMigrations(MonolithDatabase.MIGRATION_7_8, MonolithDatabase.MIGRATION_8_9)
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun teardown() {
        TimeZone.setDefault(originalZone)
        db.close()
        context.deleteDatabase(dbName)
        dayWordPrefs.edit().clear().commit()
        armZonePrefs.edit().clear().commit()
    }

    private fun travelTo(zoneId: String) {
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
    }

    private val date = "2026-10-26"

    @Test
    fun crossZone_changeToDifferentWord_isRefused() = runBlocking {
        travelTo("Asia/Kolkata")
        assertTrue(repository.declareDayWord(date, "REST"))

        travelTo("America/New_York")
        assertFalse(
            "a different word for a witnessed past day must be refused cross-zone",
            repository.declareDayWord(date, "ILL")
        )
        assertEquals("REST", repository.allDayWords()[date])
        assertEquals("Asia/Kolkata", DayWordStore(context).declaredZone(date))
    }

    @Test
    fun crossZone_clear_isRefused_sameZoneClearStaysLawful() = runBlocking {
        travelTo("Asia/Kolkata")
        assertTrue(repository.declareDayWord(date, "REST"))

        travelTo("America/New_York")
        assertFalse(
            "clearing a witnessed past day across a landing must be refused",
            repository.declareDayWord(date, null)
        )
        assertEquals("REST", repository.allDayWords()[date])
        assertEquals("Asia/Kolkata", DayWordStore(context).declaredZone(date))

        // Control: back in the declaration zone the clear is the
        // owner's own act and lands — the refusal is the zone,
        // not a stuck store.
        travelTo("Asia/Kolkata")
        assertTrue(repository.declareDayWord(date, null))
        assertTrue(repository.allDayWords().isEmpty())
        assertNull(DayWordStore(context).declaredZone(date))
    }

    @Test
    fun crossZone_sameWordRedeclare_keepsWitness_andChangeStillRefused() = runBlocking {
        travelTo("Asia/Kolkata")
        assertTrue(repository.declareDayWord(date, "REST"))

        travelTo("America/New_York")
        assertTrue(
            "re-declaring the same word rewrites nothing and stays lawful",
            repository.declareDayWord(date, "REST")
        )
        assertEquals(
            "the same-word re-declare must not move the witness",
            "Asia/Kolkata", DayWordStore(context).declaredZone(date)
        )
        assertFalse(
            "the overwrite door must stay shut after the same-word re-declare",
            repository.declareDayWord(date, "ILL")
        )
        assertEquals("REST", repository.allDayWords()[date])
    }

    @Test
    fun sameZone_changeAndClear_stayFree() = runBlocking {
        travelTo("Asia/Kolkata")
        assertTrue(repository.declareDayWord(date, "REST"))
        assertTrue(repository.declareDayWord(date, "ILL"))
        assertEquals("ILL", repository.allDayWords()[date])
        assertTrue(repository.declareDayWord(date, null))
        assertTrue(repository.allDayWords().isEmpty())
    }

    @Test
    fun endZone_capturedAtEndingWrite_threadsPublishAndReissue() = runBlocking {
        // A FLOW session armed and ended in Kolkata.
        travelTo("Asia/Kolkata")
        val dao = db.monolithDao()
        val id = dao.insertSession(
            FocusSessionEntity(
                intention = "Revision",
                profileName = "Default",
                targetDurationSeconds = 1200,
                elapsedSeconds = 1800,
                startWallTimeMillis = System.currentTimeMillis() - 1_800_000L,
                startElapsedRealtimeMillis = 0L,
                isActive = true,
                completedSuccessfully = false,
                isStopwatchMode = true,
                outcome = ""
            )
        ).toInt()

        repository.disarmActiveSession(completed = true, outcomeReason = "Finished")

        val ended = dao.getSessionById(id)
        assertNotNull(ended)
        assertTrue("the ending write must stamp the end", ended!!.endWallMillis > 0L)
        assertEquals(
            "the end zone is witnessed at the ending write",
            "Asia/Kolkata", ArmZoneStore(context).endZoneFor(ended.endWallMillis)
        )

        // The slip is assembled and read after landing in a
        // third zone — ENDED must still carry the witnessed end
        // zone, on the fresh publish and on the re-issue alike.
        travelTo("America/New_York")
        val pending = repository.assemblePendingReceipt(
            id, FinishKind.FLOW_FINISH, ended.endWallMillis
        )
        assertNotNull(pending)
        val published = repository.publishReceipt(pending!!, faulted = false)
        assertNotNull(published)
        assertEquals("Asia/Kolkata", published!!.endZoneId)

        val reissued = repository.receiptForSession(id)
        assertNotNull(reissued)
        assertEquals("Asia/Kolkata", reissued!!.endZoneId)
    }
}
