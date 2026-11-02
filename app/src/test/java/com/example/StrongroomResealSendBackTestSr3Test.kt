package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId

/**
 * TEST-SR-3 (T40) — SEND-BACK EVIDENCE CLASS.
 *
 * THESE TWO TESTS ARE EXPECTED TO FAIL against the P1/P2 code as
 * snapshotted on 2026-11-02. They assert the LAWFUL behaviour;
 * the store's seal() as written does not hold it:
 *
 *  - `StrongroomStore.seal()` reads any standing row and
 *    re-strikes the span, with NO state guard. The store's own
 *    amendment door (amendDay) refuses sealed and struck days —
 *    the swing's door must refuse at least as hard:
 *      1. a SECOND swing on a sealed day re-bases the span (the
 *         lifting moves; punches struck against the first basis
 *         are silently rebased);
 *      2. sealing an AJAR-struck day overwrites its state to
 *         SEALED — a failed day resurrected over its own plate,
 *         against B-11/B-14 (the failure face is the truth; the
 *         plate is struck once and never un-struck).
 *
 * The cure belongs to BUILD-SR-3's lane (a state guard in seal():
 * refuse when the row is SEALED or in STRUCK_STATES, returning
 * the standing span / null respectively). When the cure lands,
 * this class goes green and TEST-SR-3 strikes the send-back by
 * name. Until then its red IS the finding — it is never to be
 * "fixed" by weakening these assertions.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomResealSendBackTestSr3Test {

    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private var db: MonolithDatabase? = null

    @After
    fun teardown() {
        db?.close()
        db = null
    }

    private fun newStore(): StrongroomStore {
        if (db == null) {
            db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
        return StrongroomStore(db!!.monolithDao())
    }

    private val utc: ZoneId = ZoneId.of("UTC")
    private val dateIso = "2026-11-03"
    private val sealWall =
        java.time.LocalDateTime.of(2026, 11, 2, 20, 0).atZone(utc).toInstant().toEpochMilli()

    private fun standingOrder(): StrongroomComposition.WeekStandingOrder {
        val template = StrongroomComposition.DayTemplate(
            lockedPackages = setOf("com.video.app"),
            liftingTime = LocalTime.of(6, 0),
            breakLengthMinutes = 20,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )
        return StrongroomComposition.WeekStandingOrder(
            shapeByWeekday = DayOfWeek.values().associateWith { StrongroomComposition.DayShape.ORDINARY },
            templateByShape = mapOf(StrongroomComposition.DayShape.ORDINARY to template)
        )
    }

    @Test
    fun secondSwing_mustNotMoveTheLifting() = runBlocking {
        val store = newStore()
        store.composeDay(standingOrder(), dateIso)
        val first = store.seal(dateIso, sealWall, 100_000L, 3, utc)
        assertNotNull(first)
        // A second swing an hour later (a re-entered sitting, a
        // duplicated boot group, a retried activation): the span
        // struck first is the day's truth.
        val second = store.seal(dateIso, sealWall + 3_600_000L, 100_000L + 3_600_000L, 3, utc)
        assertEquals(
            "a second swing must not re-strike the span",
            first!!.liftingAtElapsedMillis,
            second!!.liftingAtElapsedMillis
        )
        assertEquals(first.sealedAtElapsedMillis, second.sealedAtElapsedMillis)
        assertEquals(first.spanDurationMillis, second.spanDurationMillis)
    }

    @Test
    fun resealAfterAjarStrike_mustBeRefused_aFailedDayIsNotResurrected() = runBlocking {
        val store = newStore()
        store.composeDay(standingOrder(), dateIso)
        val span = store.seal(dateIso, sealWall, 100_000L, 3, utc)!!
        // Boot death: the ajar plate is struck (the bridge's act).
        val plate = store.strikePlate(dateIso, 150_000L, 4, sealFailed = false, zone = utc)!!
        assertEquals(StrongroomDay.SealState.AJAR, plate.sealState)
        // Any later attempt to seal the same date must be refused:
        // the day has told its truth.
        val resurrected = store.seal(dateIso, sealWall + 7_200_000L, 200_000L, 4, utc)
        assertNull("sealing an ajar-struck day must be refused", resurrected)
        assertEquals("AJAR", store.day(dateIso)!!.state)
        assertEquals(span.sealedAtElapsedMillis, store.day(dateIso)!!.sealedAtElapsedMillis)
    }
}
