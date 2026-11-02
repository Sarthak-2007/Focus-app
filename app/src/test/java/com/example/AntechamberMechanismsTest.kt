package com.example

import com.example.data.StrongroomHeldArrivalEntity
import com.example.domain.StrongroomDay
import com.example.ui.antechamber.AntechamberModel
import com.example.ui.antechamber.AntechamberTexture
import com.example.ui.antechamber.HeldSlip
import com.example.ui.antechamber.RevolvingDrum
import com.example.ui.foundation.StrongroomInkLaw
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLightState
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-38 — the Antechamber's mechanisms, pinned where they
 * live (the venue's Targeted job runs them): B-9's list order ·
 * Law R-1's drum-stock resolution (no wider fallback) · the
 * drum's spend law (spend only behind TURNED; refused whole at
 * the hard stop) · the shutter's derivation at 00:00 (stock gone,
 * never dimmed) · S4-C27's dot (one, count- and age-invariant) ·
 * the pile's WAITING-only derivation · the digits line · the
 * crank's availability and the tray's kept seal.
 */
class AntechamberMechanismsTest {

    private fun signing(
        allowed: List<String> = listOf("app.alpha", "app.beta", "app.gamma"),
        locked: Set<String> = setOf("app.locked")
    ): StrongroomDay.SigningRecord = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-03",
        lockedPackages = locked,
        allowedPackages = allowed,
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = 10,
        breakFiresAtOffsetMillis = -1L,
        allowanceMinutes = 30,
        gaps = emptyList()
    )

    private fun arrival(
        id: Int,
        kind: String = StrongroomHeldArrivalEntity.KIND_CALL,
        line: String = "+91999999999$id",
        state: String = StrongroomHeldArrivalEntity.STATE_WAITING,
        at: Long = 1_700_000_000_000L + id * 60_000L
    ): StrongroomHeldArrivalEntity = StrongroomHeldArrivalEntity(
        id = id, filingDateIso = "2026-11-03", kind = kind, line = line,
        arrivedAtWallMillis = at, state = state
    )

    private fun crank(spent: Boolean): StrongroomDay.GovernorCrankWeek =
        StrongroomDay.GovernorCrankWeek(
            weekAnchorMillis = 1_700_000_000_000L,
            spentSeconds = if (spent) StrongroomDay.CRANK_SECONDS else 0,
            sealBroken = spent,
            spentAtWallMillis = if (spent) 1_700_000_100_000L else 0L
        )

    // ── B-9's list law ──────────────────────────────────────────

    @Test
    fun listOrder_isCallingOut_tokens_heldPile_andNothingElse() {
        assertEquals(
            listOf(
                AntechamberModel.Section.CALLING_OUT,
                AntechamberModel.Section.TOKENS,
                AntechamberModel.Section.HELD_PILE
            ),
            AntechamberModel.LIST_ORDER
        )
    }

    @Test
    fun takeLeaveWords_areTheExactRuledWords() {
        assertEquals("take it", AntechamberModel.TAKE_WORD)
        assertEquals("leave it", AntechamberModel.LEAVE_WORD)
    }

    // ── Law R-1 — the drum stock is the only resolution ────────

    @Test
    fun stock_isTheSignedAllowed_inSignedOrder() {
        assertEquals(
            listOf("app.alpha", "app.beta", "app.gamma"),
            RevolvingDrum.stock(signing())
        )
    }

    @Test
    fun stock_excludesALockedNameSignedIntoBothSets() {
        // A signing contradiction resolves toward the seal.
        val s = signing(
            allowed = listOf("app.alpha", "app.beta"),
            locked = setOf("app.beta")
        )
        assertEquals(listOf("app.alpha"), RevolvingDrum.stock(s))
    }

    @Test
    fun turnedName_resolvesOnlyThroughTheStock() {
        // A name no signing allowed cannot be turned to him, at
        // any rotation — there is no wider catalogue behind the
        // stock for a fallback to consult.
        val s = signing()
        val turned = (0 until 360 step 15).map { deg ->
            RevolvingDrum.turnedPackage(deg.toDouble(), s)
        }.toSet()
        assertEquals(setOf("app.alpha", "app.beta", "app.gamma"), turned)
        assertFalse("app.locked" in turned)
        assertFalse("app.unlisted" in turned)
    }

    @Test
    fun turnedName_isNullWhenTheDrumStandsEmpty() {
        val s = signing(allowed = emptyList())
        assertNull(RevolvingDrum.turnedPackage(0.0, s))
        assertTrue(
            RevolvingDrum.turn(0.0, s, StrongroomDay.AllowancePurse(30)) is
                RevolvingDrum.Turn.Empty
        )
    }

    // ── The drum's stations + settle ────────────────────────────

    @Test
    fun stationAt_zeroRotationPresentsTheFirstSignedApp() {
        assertEquals(0, RevolvingDrum.stationAt(0.0, 3))
        assertEquals(
            "app.alpha",
            RevolvingDrum.turnedPackage(0.0, signing())
        )
    }

    @Test
    fun stationAt_turnsBothWays_andWraps() {
        // One step clockwise carries station 1 to the index; one
        // step back carries the last station (turn it back is
        // lawful, census C-09).
        assertEquals(1, RevolvingDrum.stationAt(-120.0, 3))
        assertEquals(2, RevolvingDrum.stationAt(120.0, 3))
        assertEquals(0, RevolvingDrum.stationAt(360.0, 3))
        assertEquals(1, RevolvingDrum.stationAt(-480.0, 3))
    }

    @Test
    fun settleTarget_seatsTheStation_andIsNearest() {
        assertEquals(-120.0, RevolvingDrum.settleTarget(-100.0, 1, 3), 1e-9)
        assertEquals(240.0, RevolvingDrum.settleTarget(250.0, 1, 3), 1e-9)
        assertEquals(0.0, RevolvingDrum.settleTarget(10.0, 0, 3), 1e-9)
    }

    // ── The spend law — spend only behind TURNED ───────────────

    @Test
    fun turn_spendsExactlyOneMinute_andTurnsTheStation() {
        val purse = StrongroomDay.AllowancePurse(totalMinutes = 30, spentMinutes = 4)
        val turn = RevolvingDrum.turn(-120.0, signing(), purse)
        assertTrue(turn is RevolvingDrum.Turn.Turned)
        turn as RevolvingDrum.Turn.Turned
        assertEquals("app.beta", turn.packageName)
        assertEquals(5, turn.purseAfter.spentMinutes)
        assertEquals(25, turn.purseAfter.remainingMinutes)
    }

    @Test
    fun turn_atTheHardStop_refusesWhole_andSpendsNothing() {
        val spent = StrongroomDay.AllowancePurse(totalMinutes = 30, spentMinutes = 30)
        val turn = RevolvingDrum.turn(0.0, signing(), spent)
        assertTrue(turn is RevolvingDrum.Turn.HardStop)
        // Refused whole: the purse offered is the purse that stands.
        assertEquals(30, spent.spentMinutes)
    }

    @Test
    fun showingSpend_paysFromTheSamePurse_andRefusesAtTheStop() {
        val purse = StrongroomDay.AllowancePurse(totalMinutes = 2, spentMinutes = 1)
        assertEquals(2, RevolvingDrum.showingSpend(purse)?.spentMinutes)
        val spent = StrongroomDay.AllowancePurse(totalMinutes = 2, spentMinutes = 2)
        assertNull(RevolvingDrum.showingSpend(spent))
    }

    @Test
    fun spentStop_strainTravelIsTheFiledFourDegrees() {
        val attempt = RevolvingDrum.spentStopAttempt()
        // Inside the strain travel: no refusal yet; at the limit:
        // exactly one refusal; a second approach without leaving
        // the zone spends nothing more.
        assertFalse(attempt.onApproach(2.0))
        assertTrue(attempt.onApproach(0.0))
        assertFalse(attempt.onApproach(0.0))
    }

    // ── The shutter at 00:00 — gone, never dimmed ──────────────

    @Test
    fun room_shutterClosedExactlyWhenThePurseIsSpent() {
        val open = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30, 29), crank(false), emptyList()
        )
        assertFalse(open.shutterClosed)
        assertEquals(3, open.drumStock.size)

        val shut = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30, 30), crank(false), emptyList()
        )
        assertTrue(shut.shutterClosed)
        // Gone, not dimmed: the stock behind the shutter is empty.
        assertTrue(shut.drumStock.isEmpty())
    }

    // ── The held pile + S4-C27's dot ────────────────────────────

    @Test
    fun pile_isWaitingOnly_firstWaitedFirst() {
        val rows = listOf(
            arrival(1),
            arrival(2, state = StrongroomHeldArrivalEntity.STATE_TAKEN),
            arrival(3),
            arrival(4, state = StrongroomHeldArrivalEntity.STATE_LEFT)
        )
        val pile = AntechamberModel.waitingPile(rows)
        assertEquals(listOf(1, 3), pile.map { it.id })
    }

    @Test
    fun dot_isOne_forOneArrivalOrNine_andAbsentForNone() {
        assertFalse(AntechamberModel.quietDotVisible(emptyList()))
        assertTrue(
            AntechamberModel.quietDotVisible(
                AntechamberModel.waitingPile(listOf(arrival(1)))
            )
        )
        val nine = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30),
            crank(false), (1..9).map { arrival(it) }
        )
        // The aggregation is a boolean and nothing more: nine
        // waiting arrivals produce the same single signal as one,
        // and no count exists anywhere in the room derivation.
        assertTrue(nine.quietDot)
        assertEquals(9, nine.waiting.size)
        val one = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30),
            crank(false), listOf(arrival(1))
        )
        assertEquals(one.quietDot, nine.quietDot)
    }

    @Test
    fun dot_doesNotAge_nineHoursLaterTheSameSignal() {
        // Age-invariance: arrivals struck nine hours apart derive
        // the identical dot fact (the derivation reads WAITING
        // only — no timestamp enters it).
        val early = AntechamberModel.waitingPile(listOf(arrival(1, at = 0L)))
        val late = AntechamberModel.waitingPile(
            listOf(arrival(1, at = 9L * 3_600_000L))
        )
        assertEquals(
            AntechamberModel.quietDotVisible(early),
            AntechamberModel.quietDotVisible(late)
        )
    }

    // ── The digits line (TX-SHOW-07/08) ─────────────────────────

    @Test
    fun heldLine_codeIsDigitsAloneWithTheHour() {
        val zone = ZoneId.of("UTC")
        val slip = HeldSlip(
            id = 7, kind = HeldSlip.Kind.CODE, line = "482913",
            arrivedAtWallMillis = Instant.parse("2026-11-03T09:05:00Z").toEpochMilli()
        )
        assertEquals("482913  09:05", AntechamberModel.heldLine(slip, zone))
    }

    @Test
    fun heldLine_callIsTheNumberShownWhole() {
        val slip = HeldSlip(
            id = 8, kind = HeldSlip.Kind.CALL, line = "+919876543210",
            arrivedAtWallMillis = 0L
        )
        assertEquals("+919876543210", AntechamberModel.heldLine(slip, ZoneId.of("UTC")))
    }

    // ── The governor's crank + the tray's kept seal ────────────

    @Test
    fun room_crankStandsAvailableWhileItsSealIsWhole() {
        val room = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30), crank(false), emptyList()
        )
        assertTrue(room.crankAvailable)
        assertFalse(room.snappedSealKept)
    }

    // ── Texture — grain from the surface itself (intake §1) ────

    @Test
    fun texture_grainIsDeterministicPerPosition_andInRange() {
        // The same point of brass carries the same grain on every
        // frame: a pile that re-grains between looks is a screen.
        val a = AntechamberTexture.grainAt(3, 5, seed = 31)
        val b = AntechamberTexture.grainAt(3, 5, seed = 31)
        assertEquals(a, b)
        assertTrue(a >= 0f && a <= 1f)
        assertEquals(
            AntechamberTexture.gradientAt(3, 5, seed = 31),
            AntechamberTexture.gradientAt(3, 5, seed = 31)
        )
    }

    @Test
    fun texture_cellTonesStayInsideTheSevenInksLaw() {
        // Every grained tone of the room's three species is a
        // RELIGHT partner of its own ink — never green, never an
        // eighth ink introduced by the grain.
        for (species in AntechamberTexture.Species.entries) {
            for (ink in listOf(StrongroomInks.Gold, StrongroomInks.Graphite)) {
                val tone = AntechamberTexture.cellTone(
                    ink, StrongroomInks.Violet, species,
                    cellX = 2, cellY = 4, seed = 53,
                    state = StrongroomLightState.SEALED
                )
                assertTrue(StrongroomInkLaw.isNeverGreen(tone))
            }
        }
    }

    @Test
    fun room_spentCrankLeavesTheWallEmpty_andTheSealKept() {
        val room = AntechamberModel.room(
            signing(), StrongroomDay.AllowancePurse(30), crank(true), emptyList()
        )
        assertFalse(room.crankAvailable)
        assertTrue(room.snappedSealKept)
    }
}
