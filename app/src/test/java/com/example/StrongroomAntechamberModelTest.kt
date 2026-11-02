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
 * THE ANTECHAMBER MODEL's contract (ui/strongroom/AntechamberModel.kt):
 * the list law (ledger B-9), the drum (A-7), and the digits fork
 * (B-18), tested at the model where the law lives:
 *
 *  - visibleAllowed is the signed list, in the signed order,
 *    unchanged — until the purse is spent, when it is GONE
 *    (empty). There is no state in which it is shorter, dimmed,
 *    or re-ranked.
 *  - The crank refuses what was not signed (B-9.5), hard-stops
 *    on a spent purse without changing anything, turns one app
 *    at a time at one minute per turning, and the open app
 *    returns by the same act that opened it.
 *  - A showing is priced once per taking: first look charges
 *    one minute, re-looks inside the taking are free, closing
 *    the pile ends the taking (a re-opened showing pays again),
 *    and a spent purse cannot show — the codes wait.
 *  - Held calls leave the pile by taking or by leaving, his
 *    choice per call; the pile's count is calls + codes.
 */
class StrongroomAntechamberModelTest {

    private fun room(
        purse: Int = 5,
        allowed: List<String> = listOf("Notes", "Maps", "Reader")
    ) = AntechamberState(
        callingOut = emptyList(),
        allowedAsSigned = allowed,
        purseMinutes = purse,
        heldCalls = listOf(HeldCall("c1", "+91 98110 24680", "09:12")),
        heldCodes = listOf(HeldCode("k1", "482 913", "09:14"))
    )

    @Test
    fun visibleAllowed_isTheSignedList_inSignedOrder_unchanged() {
        val r = room()
        assertEquals(listOf("Notes", "Maps", "Reader"), r.visibleAllowed)
        assertEquals(r.allowedAsSigned, r.visibleAllowed)
    }

    @Test
    fun visibleAllowed_isGone_whenThePurseIsSpent() {
        val spent = room(purse = 0)
        assertTrue(spent.purseSpent)
        assertEquals(emptyList<String>(), spent.visibleAllowed)
        // Gone means gone from the getter every surface draws —
        // and the signed list itself still stands in the model,
        // because tomorrow's purse is a new day, not a dimming.
        assertEquals(3, spent.allowedAsSigned.size)
    }

    @Test
    fun crank_turnsOneApp_forOneMinute() {
        val (next, outcome) = room().crankTo("Maps")
        assertEquals(CrankOutcome.TURNED, outcome)
        assertEquals("Maps", next.openApp)
        assertEquals(4, next.purseMinutes)
        // The last minute still turns — and spends the purse.
        val (last, lastOutcome) = room(purse = 1).crankTo("Notes")
        assertEquals(CrankOutcome.TURNED, lastOutcome)
        assertEquals(0, last.purseMinutes)
        assertEquals("Notes", last.openApp)
        assertTrue(last.visibleAllowed.isEmpty())
    }

    @Test
    fun crank_hardStopsOnASpentPurse_andChangesNothing() {
        val spent = room(purse = 0)
        val (next, outcome) = spent.crankTo("Notes")
        assertEquals(CrankOutcome.HARD_STOP_SPENT, outcome)
        assertEquals(spent, next)
    }

    @Test
    fun crank_refusesWhatWasNotSigned() {
        val (next, outcome) = room().crankTo("Social")
        assertEquals(CrankOutcome.NOT_BEHIND_THE_DOOR, outcome)
        assertNull(next.openApp)
        assertEquals(5, next.purseMinutes)
    }

    @Test
    fun crank_turnsOneAtATime_andReturnReopensTheDrum() {
        val (open, _) = room().crankTo("Notes")
        val (still, outcome) = open.crankTo("Maps")
        assertEquals(CrankOutcome.ONE_ALREADY_OPEN, outcome)
        assertEquals("Notes", still.openApp)
        val returned = still.returnOpen()
        assertNull(returned.openApp)
        val (again, againOutcome) = returned.crankTo("Maps")
        assertEquals(CrankOutcome.TURNED, againOutcome)
        assertEquals("Maps", again.openApp)
    }

    @Test
    fun showing_isPricedOncePerTaking() {
        val start = room()
        val (shown, first) = start.showCode("k1")
        assertEquals(ShowingOutcome.SHOWN, first)
        assertEquals(4, shown.purseMinutes)
        assertEquals("k1", shown.openShowingId)
        assertEquals(1, shown.showingsTaken)
        // Re-looks inside the same taking spend nothing.
        val (relook, second) = shown.showCode("k1")
        assertEquals(ShowingOutcome.RELOOK_FREE, second)
        assertEquals(4, relook.purseMinutes)
        assertEquals(1, relook.showingsTaken)
        // Close the pile: the taking ends. Re-opening pays again.
        val closed = relook.closePile()
        assertNull(closed.openShowingId)
        val (reshown, third) = closed.showCode("k1")
        assertEquals(ShowingOutcome.SHOWN, third)
        assertEquals(3, reshown.purseMinutes)
        assertEquals(2, reshown.showingsTaken)
    }


    @Test
    fun showing_cannotSpendASpentPurse() {
        val (next, outcome) = room(purse = 0).showCode("k1")
        assertEquals(ShowingOutcome.CANNOT_SPEND, outcome)
        assertNull(next.openShowingId)
        assertEquals(0, next.showingsTaken)
    }

    @Test
    fun showing_unknownCodeIsNoSuchCode() {
        val (_, outcome) = room().showCode("nope")
        assertEquals(ShowingOutcome.NO_SUCH_CODE, outcome)
    }

    @Test
    fun heldCalls_leaveByTakingOrByLeaving() {
        val r = room()
        assertEquals(2, r.heldCount)
        val taken = r.takeCall("c1")
        assertTrue(taken.heldCalls.isEmpty())
        assertEquals(1, taken.heldCount)
        val left = r.leaveCall("c1")
        assertTrue(left.heldCalls.isEmpty())
        // A call that is not in the pile changes nothing.
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
