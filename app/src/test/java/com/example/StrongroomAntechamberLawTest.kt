package com.example

import com.example.ui.strongroom.AntechamberState
import com.example.ui.strongroom.CrankOutcome
import com.example.ui.strongroom.HeldCall
import com.example.ui.strongroom.HeldCode
import com.example.ui.strongroom.ShowingOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE ANTECHAMBER MODEL's contract, after the C-3 rethink
 * (ui/strongroom/AntechamberModel.kt; ruling C-3 + R2-4 + B-9 + B-18):
 *
 *  - SET 1 stands at EVERY purse state: the signed list is the
 *    visible list, in signed order, and opening a signed app
 *    spends no purse — the purse never pays for the day's own
 *    shape.
 *  - SET 2 (the drum) serves only beyond-set names: a signed
 *    name is refused BY NAME (IN_THE_SIGNED_SET), an unknown
 *    name is not in the room, one app stands open at a time
 *    across both sets, and a spent purse hard-stops the crank
 *    and empties the drum's visible stock (gone, never dimmed).
 *  - A showing is priced once per taking from the same purse:
 *    first look charges one minute, re-looks inside the taking
 *    are free, closing the pile by his act ends the taking (a
 *    re-opened showing pays again), a spent purse cannot show.
 *  - Held calls leave the pile by taking or by leaving; the
 *    held code's type carries digits, hour, id — no name field.
 */
class StrongroomAntechamberLawTest {

    private fun room(purse: Int = 5) = AntechamberState(
        callingOut = emptyList(),
        allowedAsSigned = listOf("Notes", "Maps"),
        drumCandidates = listOf("Social", "Games"),
        purseMinutes = purse,
        heldCalls = listOf(HeldCall("c1", "+91 98110 24680", "09:12")),
        heldCodes = listOf(HeldCode("k1", "482 913", "09:14"))
    )

    @Test
    fun set1_standsAtEveryPurseState_andOpensPurseFree() {
        assertEquals(listOf("Notes", "Maps"), room(purse = 0).visibleAllowed)
        assertEquals(listOf("Notes", "Maps"), room(purse = 5).visibleAllowed)
        val (next, outcome) = room(purse = 0).openSigned("Notes")
        assertEquals(CrankOutcome.OPENED_FROM_THE_SIGNED_SET, outcome)
        assertEquals("Notes", next.openApp)
        assertEquals(0, next.purseMinutes)
    }

    @Test
    fun drum_refusesASignedNameByName() {
        val (next, outcome) = room().crankTo("Notes")
        assertEquals(CrankOutcome.IN_THE_SIGNED_SET, outcome)
        assertNull(next.openApp)
        assertEquals(5, next.purseMinutes)
    }

    @Test
    fun drum_refusesWhatIsOnNeitherList() {
        val (_, outcome) = room().crankTo("Unknown")
        assertEquals(CrankOutcome.NOT_IN_THE_ROOM, outcome)
        val (_, outcome2) = room().openSigned("Social")
        assertEquals(CrankOutcome.NOT_IN_THE_ROOM, outcome2)
    }

    @Test
    fun drum_turnsOneBeyondSetApp_forOneMinute() {
        val (next, outcome) = room().crankTo("Social")
        assertEquals(CrankOutcome.TURNED, outcome)
        assertEquals("Social", next.openApp)
        assertTrue(next.openViaDrum)
        assertEquals(4, next.purseMinutes)
        val (last, lastOutcome) = room(purse = 1).crankTo("Games")
        assertEquals(CrankOutcome.TURNED, lastOutcome)
        assertEquals(0, last.purseMinutes)
        assertTrue(last.visibleDrumStock.isEmpty())
    }

    @Test
    fun drum_hardStopsOnASpentPurse_andChangesNothing() {
        val spent = room(purse = 0)
        val (next, outcome) = spent.crankTo("Social")
        assertEquals(CrankOutcome.HARD_STOP_SPENT, outcome)
        assertEquals(spent, next)
        assertTrue(spent.visibleDrumStock.isEmpty())
        assertEquals(listOf("Social", "Games"), room(purse = 3).visibleDrumStock)
    }

    @Test
    fun oneThingOpenAtATime_acrossBothSets_andReturnReopens() {
        val (open, _) = room().openSigned("Notes")
        val (still, outcome) = open.crankTo("Social")
        assertEquals(CrankOutcome.ONE_ALREADY_OPEN, outcome)
        assertEquals("Notes", still.openApp)
        val (again, againOutcome) = still.returnOpen().crankTo("Social")
        assertEquals(CrankOutcome.TURNED, againOutcome)
        assertEquals("Social", again.openApp)
    }

    @Test
    fun showing_isPricedOncePerTaking_fromTheOnePurse() {
        val (shown, first) = room().showCode("k1")
        assertEquals(ShowingOutcome.SHOWN, first)
        assertEquals(4, shown.purseMinutes)
        assertEquals(1, shown.showingsTaken)
        val (relook, second) = shown.showCode("k1")
        assertEquals(ShowingOutcome.RELOOK_FREE, second)
        assertEquals(4, relook.purseMinutes)
        assertEquals(1, relook.showingsTaken)
        val (reshown, third) = relook.closePile().showCode("k1")
        assertEquals(ShowingOutcome.SHOWN, third)
        assertEquals(3, reshown.purseMinutes)
        assertEquals(2, reshown.showingsTaken)
    }


    @Test
    fun showing_cannotSpendASpentPurse_andUnknownCodeIsNoSuch() {
        val (next, outcome) = room(purse = 0).showCode("k1")
        assertEquals(ShowingOutcome.CANNOT_SPEND, outcome)
        assertNull(next.openShowingId)
        val (_, outcome2) = room().showCode("nope")
        assertEquals(ShowingOutcome.NO_SUCH_CODE, outcome2)
    }

    @Test
    fun heldCalls_leaveByTakingOrByLeaving() {
        val r = room()
        assertEquals(2, r.heldCount)
        assertTrue(r.takeCall("c1").heldCalls.isEmpty())
        assertTrue(r.leaveCall("c1").heldCalls.isEmpty())
        assertEquals(r, r.takeCall("unknown"))
    }

    @Test
    fun heldCode_carriesDigitsAndHour_andNoNameField() {
        val fields = HeldCode::class.java.declaredFields
            .filter {
                !it.isSynthetic &&
                    !java.lang.reflect.Modifier.isStatic(it.modifiers) &&
                    !it.name.startsWith("$")
            }
            .map { it.name }
            .sorted()
        assertEquals(listOf("digits", "hour", "id"), fields)
    }
}
