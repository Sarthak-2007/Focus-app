package com.example

import com.example.domain.StrongroomDay
import com.example.ui.foundation.BoltBodies
import com.example.ui.foundation.CeremonyScores
import com.example.ui.foundation.DoorBody
import com.example.ui.foundation.HandTracker
import com.example.ui.sealed.GrilleSecondTouch
import com.example.ui.sealed.HandClosedDoor
import com.example.ui.sealed.KnockAnswer
import com.example.ui.sealed.Printing
import com.example.ui.sealed.SealedFaceState
import com.example.ui.sealed.SwingCeremony
import com.example.ui.sealed.deriveSealedPrintings
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-37 covering tests: the seal's mechanisms — the four
 * printings' derivation and hierarchy, the swing's choreography
 * read off the foundation's bodies, the knock's state law, and
 * the grille's second touch.
 */
class SealedFaceMechanismsTest {

    private fun record(
        sentence: String? = "hold the line",
        lifting: LocalTime = LocalTime.of(17, 30)
    ) = StrongroomDay.SigningRecord(
        filingDateIso = "2026-11-03",
        lockedPackages = setOf("com.video.app"),
        allowedPackages = listOf("com.notes.app"),
        liftingTime = lifting,
        sentence = sentence,
        breakLengthMinutes = 10,
        breakFiresAtOffsetMillis = -1L,
        allowanceMinutes = 30,
        gaps = emptyList(),
        taskLine = null
    )

    // ── The four printings ──────────────────────────────────────

    @Test
    fun printings_standInTheDecidedHierarchy() {
        val p = deriveSealedPrintings(record(), doorWord = "anchor")
        assertEquals(
            listOf(
                Printing.LiftingHour("17:30"),
                Printing.DoorWord("anchor"),
                Printing.Sentence("hold the line"),
                Printing.HeldMark
            ),
            p.inHierarchyOrder
        )
        assertEquals(4, p.inHierarchyOrder.size)
    }

    @Test
    fun printings_sentenceUnset_keptPlaceStands() {
        val p = deriveSealedPrintings(record(sentence = null), doorWord = "anchor")
        assertEquals(Printing.Sentence(null), p.sentence)
        // A blank sentence is also the kept place (domain law).
        val blank = deriveSealedPrintings(record(sentence = "  "), doorWord = "anchor")
        assertEquals(Printing.Sentence(null), blank.sentence)
    }

    @Test
    fun printings_doorWordUnset_keptPlaceStands() {
        assertEquals(
            Printing.DoorWord(null),
            deriveSealedPrintings(record(), doorWord = null).doorWord
        )
        assertEquals(
            Printing.DoorWord(null),
            deriveSealedPrintings(record(), doorWord = "").doorWord
        )
    }

    @Test
    fun printings_doorWordMustBeOneWord() {
        // Two words are not a door-word: the kept place stands
        // rather than a phrase being printed (the ruling's form).
        assertEquals(
            Printing.DoorWord(null),
            deriveSealedPrintings(record(), doorWord = "two words").doorWord
        )
        assertEquals(
            Printing.DoorWord("one"),
            deriveSealedPrintings(record(), doorWord = "  one  ").doorWord
        )
    }

    @Test
    fun printings_areDeterministic_thePaintBarsGround() {
        // The same standing facts derive the same face, every time:
        // the face is a struck record, not a live assembly.
        repeat(20) {
            assertEquals(
                deriveSealedPrintings(record(), "anchor"),
                deriveSealedPrintings(record(), "anchor")
            )
        }
    }

    @Test
    fun face_standsOnlyFromAStandingSeal_unsignedDayStandsOpen() {
        // GAP-COORD-04 as ruled: no standing seal → no face.
        assertNull(SealedFaceState.standing(null, "anchor"))
        assertTrue(SealedFaceState.standing(record(), "anchor") != null)
    }

    // ── The swing ───────────────────────────────────────────────

    @Test
    fun swing_boltSeatsLandOnTheScoresClunks() {
        SwingCeremony.BoltSeatOffsetsMs.forEachIndexed { i, seat ->
            assertFalse(SwingCeremony.boltSeated(i, seat - 1))
            assertTrue(SwingCeremony.boltSeated(i, seat))
        }
        // Offsets are the score's own clunks, relative to contact.
        val contact = CeremonyScores.Seal.first { it.name == "frame-contact" }.atMs
        val clunks = listOf("clunk-1", "clunk-2", "clunk-3")
            .map { name -> CeremonyScores.Seal.first { it.name == name }.atMs - contact }
        assertEquals(clunks, SwingCeremony.BoltSeatOffsetsMs)
    }

    @Test
    fun swing_boltTravelsAreTheBodiesOwnTravels() {
        assertEquals(
            BoltBodies.MASSES.map { BoltBodies.travelMs(it) },
            SwingCeremony.BoltTravelMs
        )
        // Bolt 1's drive start is the ceremony's commit point.
        assertEquals(
            CeremonyScores.SEAL_COMMIT_MS - SwingCeremony.FrameContactMs,
            SwingCeremony.BoltDriveStartOffsetsMs[0]
        )
    }

    @Test
    fun swing_boltFractionRunsFromRestToSeatedInSeatOrder() {
        for (i in 0..2) {
            assertEquals(0.0, SwingCeremony.boltTravelFraction(i, 0), 1e-9)
            val seat = SwingCeremony.BoltSeatOffsetsMs[i]
            assertEquals(
                1.0, SwingCeremony.boltTravelFraction(i, seat + 5), 1e-9
            )
            // Monotonic travel under the one drive.
            var prev = 0.0
            for (ms in 0..seat step 20) {
                val f = SwingCeremony.boltTravelFraction(i, ms)
                assertTrue(f >= prev - 1e-9)
                prev = f
            }
        }
        // Seat order is the mass order: bolt 1 seats first, bolt 3 last.
        assertTrue(
            SwingCeremony.BoltSeatOffsetsMs[0] < SwingCeremony.BoltSeatOffsetsMs[1]
        )
        assertTrue(
            SwingCeremony.BoltSeatOffsetsMs[1] < SwingCeremony.BoltSeatOffsetsMs[2]
        )
        // Clunk weights ascend: the last bolt is the heaviest fact.
        assertEquals(listOf(0.64, 0.82, 1.0), SwingCeremony.ClunkWeights)
    }

    @Test
    fun swing_pressSettlesWithoutOvershoot_neverAFourthClunk() {
        assertEquals(0.0, SwingCeremony.pressFraction(0), 1e-9)
        var prev = 0.0
        for (ms in 0..SwingCeremony.CeremonyEndOffsetMs step 25) {
            val f = SwingCeremony.pressFraction(ms)
            assertTrue("press never overshoots", f <= 1.0)
            assertTrue("press never rebounds", f >= prev - 1e-9)
            prev = f
        }
        assertTrue(SwingCeremony.pressFraction(SwingCeremony.PressSettledOffsetMs) > 0.9)
        assertFalse(SwingCeremony.isCeremonyComplete(SwingCeremony.CeremonyEndOffsetMs - 1))
        assertTrue(SwingCeremony.isCeremonyComplete(SwingCeremony.CeremonyEndOffsetMs))
    }

    @Test
    fun handDoor_followsTheHandThroughTheTracker_noOvershootPastTheHand() {
        val door = HandClosedDoor()
        assertEquals(DoorBody.REST_OPEN_DEG, door.angleDeg, 1e-9)
        door.moveHandTo(0.0)
        var prev = door.angleDeg
        repeat(400) {
            door.step(0.016)
            assertTrue("tracker never overshoots past the hand", door.angleDeg >= 0.0)
            assertTrue("closing is monotonic under a held hand", door.angleDeg <= prev + 1e-9)
            prev = door.angleDeg
        }
        assertTrue(door.atFrameContact())
        door.clampAtContact()
        assertEquals(0.0, door.angleDeg, 1e-9)
        assertEquals(0.0, door.velocityDegPerSec, 1e-9)
    }

    @Test
    fun handDoor_slamClassificationIsTheFoundations() {
        assertTrue(HandTracker.isSlam(HandTracker.CONTACT_SLAM_OMEGA + 1.0))
        assertFalse(HandTracker.isSlam(HandTracker.CONTACT_SLAM_OMEGA))
        // The hand's target is clamped to the door's travel.
        val door = HandClosedDoor()
        door.moveHandTo(500.0)
        assertEquals(DoorBody.REST_OPEN_DEG, door.handTargetDeg, 1e-9)
        door.moveHandTo(-50.0)
        assertEquals(0.0, door.handTargetDeg, 1e-9)
    }

    // ── The knock ───────────────────────────────────────────────

    @Test
    fun knock_changesNothing_noMarkNoCountNoWear() {
        val face = SealedFaceState.standing(record(), "anchor")!!
        var after = face
        repeat(9) { after = KnockAnswer.answer(after) }
        assertEquals(face, after)
        assertEquals(face.printings, after.printings)
    }

    @Test
    fun knock_displacementDecaysDead_noRebound_identicalEveryReach() {
        var prev = Double.MAX_VALUE
        for (ms in 0..KnockAnswer.SettleMs step 10) {
            val d = KnockAnswer.displacementAt(ms)
            assertTrue("no overshoot", d <= 1.0)
            assertTrue("monotonic absorption, no rebound", d <= prev + 1e-9)
            prev = d
        }
        assertTrue(KnockAnswer.displacementAt(KnockAnswer.SettleMs + 500) < 0.02)
        // No memory: the answer at any reach is the same function.
        assertEquals(
            KnockAnswer.displacementAt(40),
            KnockAnswer.displacementAt(40),
            0.0
        )
    }

    // ── The grille's second touch ───────────────────────────────

    @Test
    fun grille_firstTouchSeatsLatchOnly_secondTouchSwings() {
        val grille = GrilleSecondTouch()
        assertEquals(GrilleSecondTouch.Result.LATCH_SEATED, grille.touch(1_000L))
        assertTrue(grille.latchSeated)
        assertEquals(GrilleSecondTouch.Result.OPENED, grille.touch(2_000L))
        assertFalse(grille.latchSeated)
    }

    @Test
    fun grille_secondTouchTooSoonIsTheSameAct() {
        val grille = GrilleSecondTouch()
        grille.touch(1_000L)
        assertEquals(GrilleSecondTouch.Result.SAME_ACT, grille.touch(1_050L))
        assertTrue(grille.latchSeated)
        // A later, separate act still swings it.
        assertEquals(GrilleSecondTouch.Result.OPENED, grille.touch(1_500L))
    }

    @Test
    fun grille_resetUnseatsTheLatch() {
        val grille = GrilleSecondTouch()
        grille.touch(0L)
        grille.reset()
        assertFalse(grille.latchSeated)
        assertEquals(GrilleSecondTouch.Result.LATCH_SEATED, grille.touch(1_000L))
    }
}
