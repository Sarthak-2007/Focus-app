package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.EmergencyBreakSpendEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithWeek
import com.example.data.emergencyBreaksRemainingFromSpendCount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * TOKEN-SPEC §8 test 2 — the derivation. remaining = max(0, 1 − spends
 * booked against the CURRENT anchor): no spends → 1, one spend this
 * anchor → 0, one spend against last anchor → 1, two spends on one
 * anchor (corruption drill) → 0, never negative. Spends are attributed
 * by their STORED anchor, so historical weeks are hermetically
 * separate test fixtures.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EmergencyBreakLedgerTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun anchorOf(year: Int, month: Int, day: Int): Long {
        val utc = ZoneId.of("UTC")
        val zdt = ZonedDateTime.of(year, month, day, 12, 0, 0, 0, utc)
        return MonolithWeek.weekAnchorMillis(zdt.toInstant().toEpochMilli(), utc)
    }

    private fun spend(anchor: Long, spentAt: Long, sessionId: Int = 1) =
        EmergencyBreakSpendEntity(
            sessionId = sessionId,
            spentAtWallMillis = spentAt,
            weekAnchorMillis = anchor,
            heldSeconds = 60
        )

    @Test
    fun formula_noSpendsOne_oneSpendZero_neverNegative() {
        assertEquals(1, emergencyBreaksRemainingFromSpendCount(0))
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(1))
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(2))
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(7))
    }

    @Test
    fun spendsAreAttributedByStoredAnchorOnly() = runBlocking {
        val dao = db.monolithDao()
        val weekA = anchorOf(2024, 1, 3)   // week of Mon 2024-01-01
        val weekB = anchorOf(2024, 1, 10)  // week of Mon 2024-01-08
        val weekC = anchorOf(2024, 1, 17)  // week of Mon 2024-01-15
        val weekD = anchorOf(2024, 1, 24)  // week of Mon 2024-01-22
        assertTrue(weekA < weekB && weekB < weekC && weekC < weekD)

        // Empty ledger: every week reads full.
        assertEquals(1, emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(weekA, weekB).size))

        // One spend in week B; one (later) spend in week A.
        dao.insertEmergencyBreakSpend(spend(weekB, spentAt = 2_000L, sessionId = 11))
        dao.insertEmergencyBreakSpend(spend(weekA, spentAt = 3_000L, sessionId = 12))

        // A spend against LAST anchor does not touch the current one:
        // evaluated against week C, history is invisible → remaining 1.
        assertEquals(0, dao.getSpendsForWeek(weekC, weekD).size)
        assertEquals(1, emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(weekC, weekD).size))
        // Its own week reads spent.
        assertEquals(1, dao.getSpendsForWeek(weekB, weekC).size)
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(weekB, weekC).size))

        // Corruption drill: a second row on the same anchor still
        // derives 0 — the formula floors, it never goes negative.
        dao.insertEmergencyBreakSpend(spend(weekB, spentAt = 4_000L, sessionId = 13))
        assertEquals(2, dao.getSpendsForWeek(weekB, weekC).size)
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(weekB, weekC).size))

        // The observe flow sees the same rows for the same anchor.
        assertEquals(2, dao.observeSpendsForWeek(weekB, weekC).first().size)
        assertEquals(0, dao.observeSpendsForWeek(weekC, weekD).first().size)

        // Snapshot: every spend, newest spend first.
        val all = dao.getAllSpendsSnapshot()
        assertEquals(3, all.size)
        assertEquals(listOf(4_000L, 3_000L, 2_000L), all.map { it.spentAtWallMillis })
        assertTrue(all.all { it.heldSeconds == 60 })
    }

    /**
     * Issue #39 F1 — the two SQL arms, isolated. A spend minted under
     * another timezone carries that zone's Monday as its stored
     * anchor: within 26h of this week's anchor it counts by the
     * ANCHOR arm even when its instant falls outside this local week;
     * a spend whose stored anchor is a full week away still counts by
     * the INSTANT arm when the moment it was spent lies inside this
     * local week. A neighbouring week's spend — far on both arms —
     * never counts.
     */
    @Test
    fun foreignZoneSpendsCountByEitherArm_neighbouringWeekNever() = runBlocking {
        val dao = db.monolithDao()
        val weekB = anchorOf(2024, 1, 10)  // week of Mon 2024-01-08 (UTC)
        val weekC = anchorOf(2024, 1, 17)  // week of Mon 2024-01-15 (UTC)
        val hour = 3_600_000L

        // Anchor arm only: stored anchor = this Monday seen from a
        // zone 9.5h west (instant 9.5h later), spent instant 1h BEFORE
        // this local week opens — outside the instant window.
        dao.insertEmergencyBreakSpend(
            spend(anchor = weekB + 9 * hour + hour / 2, spentAt = weekB - hour, sessionId = 21)
        )
        assertEquals(1, dao.getSpendsForWeek(weekB, weekC).size)

        // Instant arm only: stored anchor = LAST week's Monday (7d
        // away, far beyond the 26h tolerance), spent instant inside
        // this local week.
        val weekA = anchorOf(2024, 1, 3)
        dao.insertEmergencyBreakSpend(
            spend(anchor = weekA, spentAt = weekB + hour, sessionId = 22)
        )
        assertEquals(2, dao.getSpendsForWeek(weekB, weekC).size)
        assertEquals(2, dao.observeSpendsForWeek(weekB, weekC).first().size)

        // Fail closed: next week's spend (anchor AND instant a week
        // ahead) is invisible to this week on both arms.
        dao.insertEmergencyBreakSpend(
            spend(anchor = weekC, spentAt = weekC + hour, sessionId = 23)
        )
        assertEquals(2, dao.getSpendsForWeek(weekB, weekC).size)
        assertEquals(0, emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(weekB, weekC).size))
    }
}
