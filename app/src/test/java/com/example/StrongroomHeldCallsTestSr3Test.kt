package com.example

import com.example.domain.StrongroomRouting
import com.example.service.StrongroomEngineBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TEST-SR-3 (T40) — HELD CALLS (ledger D-5): CONTACTS RING
 * THROUGH, UNKNOWN NUMBERS ARE HELD QUIETLY.
 *
 * Levels proven here, named honestly:
 *  - the ROUTING verdict (pure): contact → RING_THROUGH,
 *    unknown → HELD_QUIETLY; the caller classification is total
 *    (two classes, no third bucket a number could fall into);
 *  - the BRIDGE entry the listener is meant to call:
 *    [StrongroomEngineBridge.callDecision] maps the listener's
 *    isContact fact to the same verdicts;
 *  - the PER-CALL resolution: taking a held call connects THAT
 *    call only — it is not an unlock, not a lease, not a door.
 *
 * LEVEL NOT PROVEN — the gap, by name: as of this filing,
 * MonolithNotificationListener.kt contains NO reference to the
 * bridge or the routing (grep-verified firsthand, 2026-11-02).
 * D-5 at the LISTENER's code level is therefore UNWIRED, and is
 * carried as gap G-T3-1 in the obligations map + the letter to
 * BUILD-SR-3. These tests pin the contract the listener must
 * consult; they do not claim it does. On-device ring/held
 * behaviour stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */
class StrongroomHeldCallsTestSr3Test {

    @Test
    fun contactsRingThrough_unknownsHeldQuietly_classificationIsTotal() {
        assertEquals(2, StrongroomRouting.CallerClass.values().size)
        assertEquals(
            StrongroomRouting.CallDecision.RING_THROUGH,
            StrongroomRouting.routeCall(StrongroomRouting.CallerClass.CONTACT)
        )
        assertEquals(
            StrongroomRouting.CallDecision.HELD_QUIETLY,
            StrongroomRouting.routeCall(StrongroomRouting.CallerClass.UNKNOWN)
        )
        // Total sweep: no caller class falls through unrouted.
        for (caller in StrongroomRouting.CallerClass.values()) {
            val decision = StrongroomRouting.routeCall(caller)
            assertTrue(decision == StrongroomRouting.CallDecision.RING_THROUGH ||
                decision == StrongroomRouting.CallDecision.HELD_QUIETLY)
        }
    }

    @Test
    fun bridgeCallDecision_theListenersEntryPoint_mapsTheContactFact() {
        assertEquals(
            StrongroomRouting.CallDecision.RING_THROUGH,
            StrongroomEngineBridge.callDecision(isContact = true)
        )
        assertEquals(
            StrongroomRouting.CallDecision.HELD_QUIETLY,
            StrongroomEngineBridge.callDecision(isContact = false)
        )
    }

    @Test
    fun takingAHeldCall_connectsThatCallOnly_grantsNothingElse() {
        val take = StrongroomRouting.resolveHeldCall(StrongroomRouting.HeldCallChoice.TAKE)
        assertTrue(take.connectsThisCallOnly)
        assertFalse("a taken held call must not open the door", take.doorOpened)
        assertTrue("the session stays sealed through a taken call", take.sessionSealed)

        val leave = StrongroomRouting.resolveHeldCall(StrongroomRouting.HeldCallChoice.LEAVE)
        assertFalse(leave.connectsThisCallOnly)
        assertFalse(leave.doorOpened)
        assertTrue(leave.sessionSealed)

        // The choice set is closed: take it or leave it — there
        // is no third choice shaped like an unlock.
        assertEquals(2, StrongroomRouting.HeldCallChoice.values().size)
    }
}
