package com.example

import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * TEST-SR-3 (T40) — EXITS REACH ONLY THE ANTECHAMBER (ledger A-14).
 *
 * The door has exactly TWO openings: the lifting hour and the
 * governor's crank. The #683 exit set (pre-set PIN · temporary
 * unlock · charger · cooldown) reaches the ANTECHAMBER ONLY; the
 * session stays sealed through each. This suite is exhaustive
 * over the exit enum: if a fifth exit kind is ever added, the
 * sweep below forces its author to state its landing here.
 *
 * Also pinned: the crank is an OPENING, not an exit (its spend
 * breaks the crank's own lead seal, never the day's seal), and
 * the break is a FIRING, not an opening (it runs inside the seal).
 */
class StrongroomExitsAntechamberTestSr3Test {

    private val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)

    @Test
    fun exits_exhaustive_everyKindLandsInTheAntechamberOnly() {
        // The #683 set, by name — an added kind breaks this count
        // and must be landed deliberately, never by default.
        assertEquals(4, StrongroomRouting.ExitKind.values().size)
        assertEquals(
            setOf(
                StrongroomRouting.ExitKind.PIN,
                StrongroomRouting.ExitKind.TEMP_UNLOCK,
                StrongroomRouting.ExitKind.CHARGER,
                StrongroomRouting.ExitKind.COOLDOWN
            ),
            StrongroomRouting.ExitKind.values().toSet()
        )
        for (kind in StrongroomRouting.ExitKind.values()) {
            val landing = StrongroomRouting.routeExit(kind)
            assertEquals("exit $kind must land in the antechamber",
                StrongroomRouting.Surface.ANTECHAMBER, landing.surface)
            assertFalse("exit $kind must not open the door", landing.doorOpened)
            assertTrue("the session must stay sealed through exit $kind", landing.sessionSealed)
        }
    }

    @Test
    fun exits_noLandingIsASurfaceBeyondTheGrille() {
        // Every exit landing is inside the sealed surface set —
        // an exit can never land on the normal app, the Deck,
        // settings, or records (that would be a third opening).
        for (kind in StrongroomRouting.ExitKind.values()) {
            val landing = StrongroomRouting.routeExit(kind)
            assertTrue(StrongroomRouting.surfaceLawfulWhileSealed(landing.surface))
            assertFalse(landing.surface == StrongroomRouting.Surface.NORMAL_APP)
            assertFalse(landing.surface == StrongroomRouting.Surface.DECK)
            assertFalse(landing.surface == StrongroomRouting.Surface.SETTINGS)
            assertFalse(landing.surface == StrongroomRouting.Surface.RECORDS)
        }
    }

    @Test
    fun crank_isAnOpeningNotAnExit_itsSpendBreaksItsOwnSealNeverTheDays() {
        val week = StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1_000L)
        val spent = week.spend(atWallMillis = 2_000L)!!
        assertTrue(spent.sealBroken)
        // There is no second minute and no stockpile (D-4).
        assertNull(spent.spend(atWallMillis = 3_000L))
        // The DAY's seal after a crank spend: still sealed on the
        // span, and the plate carries the crank's broken seal as
        // its own fact — the day does NOT read broken.
        val signing = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-03",
            lockedPackages = setOf("com.video.app"),
            liftingTime = LocalTime.of(6, 0),
            sentence = null,
            breakLengthMinutes = 0,
            breakFiresAtOffsetMillis = -1L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )
        val midBlock = seal.sealedAtElapsedMillis + 60_000L
        val plate = StrongroomDay.strikePlate(
            signing, seal, emptyList(), spent,
            nowElapsedMillis = midBlock, nowBootCount = 3, sealFailed = false
        )
        // The plate struck over a crank-spent day reads the DAY's
        // seal as it stands — SEALED — while carrying the crank's
        // broken seal as its own separate fact. A crank spend that
        // read the day BROKEN would be a laundered verdict.
        assertEquals(StrongroomDay.SealState.SEALED, plate.sealState)
        assertTrue(plate.crankSealBroken)
        assertEquals(
            StrongroomDay.SealState.SEALED,
            seal.stateAt(midBlock, 3, sealFailed = false)
        )
    }

    @Test
    fun break_isAFiringNotAnOpening_itRunsInsideTheSeal() {
        val firing = StrongroomDay.BreakFiring(
            firesAtElapsedMillis = seal.sealedAtElapsedMillis + 3_600_000L,
            lengthMinutes = 20
        )
        val inside = firing.firesAtElapsedMillis + 1L
        assertTrue(firing.isRunning(inside))
        // While the break runs, the seal still stands and the
        // lifting has not moved by one millisecond.
        assertEquals(
            StrongroomDay.SealState.SEALED,
            seal.stateAt(inside, 3, sealFailed = false)
        )
        assertEquals(
            seal.sealedAtElapsedMillis + seal.spanDurationMillis,
            seal.liftingAtElapsedMillis
        )
        // A freely chosen length is priced as chosen (D-6): a 47
        // minute break — no preset — fires and ends on its own
        // arithmetic.
        val odd = StrongroomDay.BreakFiring(firing.firesAtElapsedMillis, 47)
        assertEquals(firing.firesAtElapsedMillis + 47L * 60_000L, odd.endsAtElapsedMillis)
        assertFalse(odd.isRunning(odd.endsAtElapsedMillis))
    }

    @Test
    fun purseHardStop_isNotAnExit_spentMeansTheCrankWillNotTurn() {
        val purse = StrongroomDay.AllowancePurse(totalMinutes = 30, spentMinutes = 30)
        assertTrue(purse.isHardStopped)
        assertNull(purse.spend(1))
        // And an overspend against a live purse is refused WHOLE —
        // never partially granted, never converted into an opening.
        val live = StrongroomDay.AllowancePurse(totalMinutes = 30, spentMinutes = 25)
        assertNull(live.spend(6))
        assertEquals(5, live.spend(5)!!.remainingMinutes)
    }
}
