package com.example

import com.example.service.haptics.HapticEvent
import com.example.ui.signing.SigningFeel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * FEEL-SEAT — the signing wheel's feel translator, pinned:
 * one notch per number on the filed 36° rail, a gate only
 * after a full station of committed travel is abandoned,
 * the final seat's own event, the seated dial's flat wall.
 * Every figure is the foundation's (StrongroomMaximum /
 * FEEL-SR-3 C1), never this file's invention.
 */
class SigningFeelTest {

    @Test
    fun travelWithinOneStationFiresNothing() {
        val feel = SigningFeel()
        feel.onTravel(10.0) // prime the stream
        assertTrue(feel.onTravel(20.0).isEmpty())
        assertTrue(feel.onTravel(35.9).isEmpty())
    }

    @Test
    fun crossingOneStationFiresOneNotch() {
        val feel = SigningFeel()
        feel.onTravel(10.0)
        assertEquals(
            listOf(HapticEvent.COMBINATION_STATION),
            feel.onTravel(40.0)
        )
    }

    @Test
    fun crossingThreeStationsFiresThreeNotchesInOrder() {
        val feel = SigningFeel()
        feel.onTravel(0.0)
        assertEquals(
            listOf(
                HapticEvent.COMBINATION_STATION,
                HapticEvent.COMBINATION_STATION,
                HapticEvent.COMBINATION_STATION
            ),
            feel.onTravel(110.0)
        )
    }

    @Test
    fun travelBackAcrossTheSameToothReClicksIt() {
        // A pawl re-clicks its tooth both ways: crossing the
        // boundary backwards is a crossing, and — the travel
        // abandoned being under one station — NOT a gate.
        val feel = SigningFeel()
        feel.onTravel(30.0)
        feel.onTravel(40.0) // crosses 36° forward: one notch
        assertEquals(
            listOf(HapticEvent.COMBINATION_STATION),
            feel.onTravel(30.0)
        )
    }

    @Test
    fun reversalAfterAFullStationCommittedFiresTheGate() {
        val feel = SigningFeel()
        feel.onTravel(0.0)
        feel.onTravel(50.0) // 50° committed clockwise (one notch at 36°)
        val events = feel.onTravel(44.0) // the hand reverses
        assertEquals(listOf(HapticEvent.COMBINATION_GATE), events)
    }

    @Test
    fun reversalOnJitterFiresNoGate() {
        val feel = SigningFeel()
        feel.onTravel(100.0)
        feel.onTravel(110.0) // 10° committed — under one station
        assertTrue(feel.onTravel(104.0).isEmpty())
    }

    @Test
    fun aGateNeedsFreshCommitmentBeforeItCanFireAgain() {
        val feel = SigningFeel()
        feel.onTravel(0.0)
        feel.onTravel(50.0)
        feel.onTravel(44.0) // gate one
        // A small counter-move and back must not re-fire it.
        assertTrue(feel.onTravel(49.0).isEmpty())
        assertTrue(feel.onTravel(44.0).isEmpty())
    }

    @Test
    fun negativeTravelCrossesStationsSymmetrically() {
        val feel = SigningFeel()
        feel.onTravel(0.0)
        assertEquals(
            listOf(HapticEvent.COMBINATION_STATION),
            feel.onTravel(-40.0)
        )
    }

    @Test
    fun onlyTheFinalSeatCarriesAnEvent() {
        val feel = SigningFeel()
        assertTrue(feel.onSeatTaken(finalSeat = false).isEmpty())
        assertEquals(
            listOf(HapticEvent.COMBINATION_FINAL_SEAT),
            feel.onSeatTaken(finalSeat = true)
        )
    }

    @Test
    fun attemptAgainstTheSeatedDialFiresTheEndStop() {
        val feel = SigningFeel()
        assertEquals(
            listOf(HapticEvent.COMBINATION_END_STOP),
            feel.onAttemptAgainstSeatedDial()
        )
    }

    @Test
    fun departingASeatedDetentDoesNotRecountIt() {
        // Prime exactly ON detent 0, then travel a few degrees
        // either way: no tooth is arrived at, so no notch —
        // in the negative direction above all, where a naive
        // bucket count would spend one at once.
        val feel = SigningFeel()
        feel.onTravel(0.0)
        assertTrue(feel.onTravel(-5.0).isEmpty())
        assertTrue(feel.onTravel(5.0).isEmpty())
    }

    @Test
    fun stillnessFiresNothing() {
        val feel = SigningFeel()
        feel.onTravel(72.0)
        assertTrue(feel.onTravel(72.0).isEmpty())
    }
}
