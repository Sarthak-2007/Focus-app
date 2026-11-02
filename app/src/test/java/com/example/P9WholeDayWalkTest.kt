package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusTaskEntity
import com.example.data.LegacyCarry
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.data.TaskQueueStatus
import com.example.data.toSealSpan
import com.example.data.toSigningRecord
import com.example.domain.StrongroomDay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * P9 (BUILD-SR-8) — THE WHOLE-DAY WALK, as automated tests.
 *
 * Implements VERIFY-SR-1's P9 proof map (`p9-day-walk-proof-map.md`)
 * at the categories the map fixes: every assertion below is one the
 * map marks AUTOMATED (record/model arithmetic, purse + hard stop,
 * break pricing/scheduling logic, crank week state machine, lifting
 * time logic, plate construction). The map's CODE-READ wirings
 * (shield/accessibility/boot, grille entry, scheduler receivers)
 * and every UNPROVEN-ON-DEVICE claim (containment itself, the
 * knock's felt character, exact-alarm firing on his OnePlus) are
 * NOT asserted here and are not upgraded by these tests' colour.
 *
 * The beat order is the brief's ten: sign → compose → swing →
 * knock → drum → break → crank → lift → hand-back → plate. Compose
 * (ledger B-2: tomorrow stands composed from the standing order;
 * the signing AMENDS, never creates) runs through P1's
 * [LegacyCarry]; the walk's signing is that composition amended
 * with the day's gaps and its priced break. Hand-back (ledger
 * B-13) has no dedicated P1 API — its full form is P6's — so the
 * walk asserts it through the only P1 surfaces that carry it: the
 * struck plate's HELD punches, enumerable by name at the lift,
 * checked BEFORE the plate's other facts are read, as B-13 orders
 * (the waited are handed back before the plate is read).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class P9WholeDayWalkTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val fx = P9ProductionFixtures
    private val utc = fx.UTC

    /** The walk's day: filing date 2026-11-05; sealed 04:00 UTC for
     *  a 14:00 lifting — a ten-hour elapsed span. */
    private val filingDate = "2026-11-05"
    private val sealWallMillis: Long = LocalDateTime.of(2026, 11, 5, 4, 0)
        .atZone(utc).toInstant().toEpochMilli()
    private val sealedAtElapsed = 500_000L
    private val bootCount = 7
    private val spanMillis = 36_000_000L // 10 h, 04:00 → 14:00

    private fun composedSigning(): StrongroomDay.SigningRecord {
        // COMPOSE (B-2): the day stands composed from his standing
        // order — here, the fixture loadout via LegacyCarry.
        // COMPOSITION CURE (BUILD-SR-13): the harness was written
        // against a draft P1 signature (activeSession = the session
        // entity, non-null return). The landed P1 API takes the
        // session's intention string and returns null when no
        // lifting hour stands; the fixture's window end stands, so
        // the record is unwrapped. Named in the composition record.
        val composed = LegacyCarry.deriveSigning(
            filingDateIso = filingDate,
            blockedApps = fx.blockedApps,
            guardSettings = fx.guardSettings,
            activeSessionIntention = fx.sessions()
                .first { it.id == fx.ACTIVE_SESSION_ID }.intention,
            guardAllowanceMinutes = 30,
            activeSessionWindowEnd = LocalTime.of(14, 0)
        )!!
        // The evening signing AMENDS the composition: it prices the
        // day's break — 37 minutes, freely chosen, deliberately NOT
        // one of the preset shortcuts — and shapes the gym gap.
        return composed.copy(
            breakLengthMinutes = 37,
            breakFiresAtOffsetMillis = 10_800_000L, // 3 h into the span
            gaps = listOf(
                StrongroomDay.ShapedGap(
                    StrongroomDay.GapKind.GYM, 7_200_000L, 3_600_000L
                )
            )
        )
    }

    // ------------------------------------------------------------------
    // The walk, store-level: every beat in order, one day
    // ------------------------------------------------------------------

    @Test
    fun wholeDayWalk_signComposeSwingKnockDrumBreakCrankLiftHandBackPlate() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.monolithDao()
            val store = StrongroomStore(dao)
            // A task finished during the walk's day, for hand-back's
            // drawer half.
            dao.insertTask(
                FocusTaskEntity(
                    title = "Walk day: finished task",
                    status = TaskQueueStatus.COMPLETED.name,
                    completedAtMillis = sealWallMillis + 3_600_000L,
                    completedInSessionId = 99
                )
            )

            // 1 · SIGN + COMPOSE: the amended composition persists
            // whole — locked set, lifting hour, his sentence
            // verbatim, the priced break, the shaped gap.
            val signing = composedSigning()
            assertEquals(setOf(fx.LOCKED_PACKAGE), signing.lockedPackages)
            assertEquals(fx.ACTIVE_SESSION_INTENTION, signing.sentence)
            store.sign(signing)
            val signedRow = store.day(filingDate)!!
            assertEquals("SIGNED", signedRow.state)
            assertEquals(signing, signedRow.toSigningRecord())

            // 2 · SWING: the seal strikes its elapsed span at the
            // first sitting. The span is ten hours by the wall AT
            // SEALING — and from here the wall is never consulted
            // again: the span object carries no wall field, so no
            // later wall move can move the lifting.
            val span = store.seal(
                filingDate, sealWallMillis,
                sealedAtElapsedMillis = sealedAtElapsed, bootCount = bootCount, zone = utc
            )!!
            assertEquals(spanMillis, span.spanDurationMillis)
            assertEquals("SEALED", store.day(filingDate)!!.state)
            assertFalse(span.isLifted(sealedAtElapsed + spanMillis - 1, bootCount))
            assertTrue(span.isLifted(sealedAtElapsed + spanMillis, bootCount))
            // Inside the gym gap the seal still stands — a shaped
            // gap is a shape of the day, never an opening.
            val gapElapsed = sealedAtElapsed + 7_200_000L + 60_000L
            assertEquals(
                StrongroomDay.GapKind.GYM,
                StrongroomDay.gapAt(signing.gaps, 7_200_000L + 60_000L)?.kind
            )
            assertEquals(
                StrongroomDay.SealState.SEALED,
                span.stateAt(gapElapsed, bootCount, sealFailed = false)
            )

            // 3 · KNOCK: a reach for the locked app punches the
            // plate's REACH hole; a held call punches HELD; a floor
            // event punches FLOOR. Nothing opens — in model terms,
            // the day's state does not move.
            store.punch(
                filingDate,
                StrongroomDay.Punch(StrongroomDay.PunchKind.REACH, 600_000L, fx.LOCKED_PACKAGE)
            )
            store.punch(
                filingDate,
                StrongroomDay.Punch(StrongroomDay.PunchKind.HELD, 900_000L, "com.android.dialer")
            )
            store.punch(
                filingDate,
                StrongroomDay.Punch(StrongroomDay.PunchKind.FLOOR, 1_200_000L, "")
            )
            assertEquals("SEALED", store.day(filingDate)!!.state)
            assertEquals(3, store.punches(filingDate).size)

            // 4 · DRUM: the purse spends minute by minute; the last
            // minute lands exactly on the hard stop; one more is
            // refused whole — the crank will not turn.
            val afterFive = store.spendAllowance(filingDate, 5)!!
            assertEquals(25, afterFive.remainingMinutes)
            assertFalse(afterFive.isHardStopped)
            val spent = store.spendAllowance(filingDate, 25)!!
            assertEquals(0, spent.remainingMinutes)
            assertTrue(spent.isHardStopped)
            assertNull(store.spendAllowance(filingDate, 1))

            // 5 · BREAK: the firing priced at signing — due at its
            // offset, running for its freely chosen 37 minutes,
            // spent after. A firing, not an opening: while it runs
            // the seal's state is still SEALED.
            val firing = StrongroomDay.breakFiring(signing, span)!!
            assertEquals(37, firing.lengthMinutes)
            assertFalse(firing.isDue(sealedAtElapsed + 10_800_000L - 1))
            assertTrue(firing.isDue(sealedAtElapsed + 10_800_000L))
            assertTrue(firing.isRunning(sealedAtElapsed + 10_800_000L))
            assertFalse(firing.isRunning(firing.endsAtElapsedMillis))
            assertEquals(
                StrongroomDay.SealState.SEALED,
                span.stateAt(sealedAtElapsed + 10_800_000L, bootCount, sealFailed = false)
            )

            // 6 · CRANK: the week's single minute, spent whole; the
            // seal snaps and stays snapped; there is no second
            // minute this week — and a fresh week stands fresh (the
            // proof map's named week-boundary edge).
            val week = store.crankWeek(sealWallMillis, utc)
            assertTrue(week.minuteAvailable)
            val crankSpent = store.spendCrank(sealWallMillis + 3_600_000L, utc)!!
            assertEquals(StrongroomDay.CRANK_SECONDS, crankSpent.spentSeconds)
            assertTrue(crankSpent.sealBroken)
            assertNull(store.spendCrank(sealWallMillis + 7_200_000L, utc))
            val nextWeek = store.crankWeek(sealWallMillis + 7L * 24 * 3_600_000L, utc)
            assertTrue(nextWeek.minuteAvailable)
            assertFalse(nextWeek.sealBroken)

            // 7 · LIFT: at the elapsed span's end, same boot, the
            // time-lock fires.
            val liftElapsed = sealedAtElapsed + spanMillis
            assertTrue(span.isLifted(liftElapsed, bootCount))
            val plate = store.strikePlate(
                filingDate,
                nowElapsedMillis = liftElapsed,
                nowBootCount = bootCount, sealFailed = false, zone = utc
            )!!

            // 8 · HAND-BACK (B-13), before the plate is read: the
            // people who waited are handed back first — the day's
            // HELD punch is enumerable by name from the struck
            // plate, and the drawer's finished tasks are served.
            val handedBack = plate.punchesOf(StrongroomDay.PunchKind.HELD)
            assertEquals(1, handedBack.size)
            assertEquals("com.android.dialer", handedBack.single().packageName)
            assertEquals(900_000L, handedBack.single().atOffsetMillis)
            val drawer = store.drawerPage(
                fromMillis = sealWallMillis,
                toMillis = sealWallMillis + 24 * 3_600_000L,
                limit = 10, offset = 0
            )
            assertEquals(listOf("Walk day: finished task"), drawer.map { it.title })

            // 9 · PLATE: the day's truth struck once — the full
            // span held, punches graded LARGE/MEDIUM/SMALL by kind,
            // the crank's snapped seal kept on the plate, no
            // bracket gap on a held day; persisted as LIFTED.
            assertEquals(StrongroomDay.SealState.LIFTED, plate.sealState)
            assertEquals(spanMillis, plate.hoursHeldMillis)
            assertEquals(0L, plate.bracketGapMillis)
            assertTrue(plate.crankSealBroken)
            assertEquals(
                StrongroomDay.PunchSize.LARGE,
                plate.punchesOf(StrongroomDay.PunchKind.REACH).single().kind.size
            )
            assertEquals(
                StrongroomDay.PunchSize.MEDIUM,
                plate.punchesOf(StrongroomDay.PunchKind.HELD).single().kind.size
            )
            assertEquals(
                StrongroomDay.PunchSize.SMALL,
                plate.punchesOf(StrongroomDay.PunchKind.FLOOR).single().kind.size
            )
            val struckRow = store.day(filingDate)!!
            assertEquals("LIFTED", struckRow.state)
            assertEquals(spanMillis, struckRow.struckHeldMillis)
            assertTrue(struckRow.crankSealBroken)
        } finally {
            db.close()
        }
    }

    // ------------------------------------------------------------------
    // The failure walk: power death under the seal
    // ------------------------------------------------------------------

    @Test
    fun wholeDayWalk_bootDeathUnderSeal_strikesAnAjarPlate_neverALiftedOne() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val store = StrongroomStore(db.monolithDao())
            val date = "2026-11-06"
            val wall = LocalDateTime.of(2026, 11, 6, 4, 0)
                .atZone(utc).toInstant().toEpochMilli()
            store.sign(
                StrongroomDay.SigningRecord(
                    filingDateIso = date,
                    lockedPackages = setOf(fx.LOCKED_PACKAGE),
                    liftingTime = LocalTime.of(14, 0),
                    sentence = null, // the kept place stands
                    breakLengthMinutes = 0,
                    breakFiresAtOffsetMillis = -1L,
                    allowanceMinutes = 30,
                    gaps = emptyList()
                )
            )
            val span = store.seal(
                date, wall, sealedAtElapsedMillis = 100_000L, bootCount = 3, zone = utc
            )!!
            assertEquals(spanMillis, span.spanDurationMillis)

            // The phone dies five hours in; the boot count that
            // comes back is a different boot. The elapsed span can
            // no longer be trusted: the lifting NEVER fires off the
            // reset clock, at any elapsed reading.
            assertFalse(span.isLifted(100_000L + spanMillis + 999_999L, nowBootCount = 4))
            val plate = store.strikePlate(
                date,
                nowElapsedMillis = 100_000L + 5 * 3_600_000L,
                nowBootCount = 4, sealFailed = false, zone = utc
            )!!
            assertEquals(StrongroomDay.SealState.AJAR, plate.sealState)
            assertEquals(5 * 3_600_000L, plate.hoursHeldMillis)
            assertEquals(spanMillis - 5 * 3_600_000L, plate.bracketGapMillis)
            assertTrue(plate.bracketGapMillis > 0L)
            assertEquals("AJAR", store.day(date)!!.state)
            assertNotNull(store.day(date)!!.toSealSpan())
        } finally {
            db.close()
        }
    }

    // ------------------------------------------------------------------
    // The walk, pure-domain: the same beats with no store beneath
    // ------------------------------------------------------------------

    @Test
    fun wholeDayWalk_pureDomain_theBeatsHoldWithoutTheStore() {
        val signing = composedSigning()
        // SWING: strike the span from the wall at sealing.
        val duration = StrongroomDay.spanDurationAtSealing(
            sealWallMillis, signing.liftingTime, utc
        )
        assertEquals(spanMillis, duration)
        val span = StrongroomDay.SealSpan(sealedAtElapsed, bootCount, duration)

        // KNOCK → DRUM → BREAK → CRANK as pure facts.
        val reach = StrongroomDay.Punch(
            StrongroomDay.PunchKind.REACH, 600_000L, fx.LOCKED_PACKAGE
        )
        var purse = StrongroomDay.AllowancePurse(signing.allowanceMinutes)
        purse = purse.spend(30)!!
        assertTrue(purse.isHardStopped)
        assertNull(purse.spend(1))
        val firing = StrongroomDay.breakFiring(signing, span)!!
        assertTrue(firing.isDue(sealedAtElapsed + 10_800_000L))
        var crank = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1L)
        crank = crank.spend(sealWallMillis)!!
        assertTrue(crank.sealBroken)
        assertNull(crank.spend(sealWallMillis + 1_000L))

        // LIFT → PLATE, struck from the pure facts alone.
        val plate = StrongroomDay.strikePlate(
            signing = signing, seal = span, punches = listOf(reach),
            crank = crank,
            nowElapsedMillis = sealedAtElapsed + spanMillis,
            nowBootCount = bootCount, sealFailed = false
        )
        assertEquals(StrongroomDay.SealState.LIFTED, plate.sealState)
        assertEquals(spanMillis, plate.hoursHeldMillis)
        assertTrue(plate.crankSealBroken)
        assertEquals(listOf(reach), plate.punchesOf(StrongroomDay.PunchKind.REACH))
    }
}
