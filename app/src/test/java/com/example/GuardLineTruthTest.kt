package com.example

import com.example.ui.components.GuardLinePrint
import com.example.ui.components.resolveGuardLinePrint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Audit R3 #113, finding 2 — the header band's tense law.
 *
 * The seal freeze used to be the band's print for the session's
 * whole life: revoke a permission mid-session and the header
 * kept printing `GUARD 4/4` over the corpse. The resolution:
 * a MEASURED live print outranks the captured one, in both
 * directions; the freeze survives only as the unmeasured
 * fallback, which is the one job a past measurement can honestly
 * do for a present-tense line.
 */
class GuardLineTruthTest {

    private val whole = GuardLinePrint("GUARD 4/4")
    private val lack = GuardLinePrint("GUARD 3/4 — USAGE ACCESS")

    @Test
    fun revokedMidSessionDropsTheFrozenWholePrint() {
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = true, frozen = whole, live = lack)
        )
    }

    @Test
    fun restoredMidSessionLiftsAFrozenLackPrint() {
        assertEquals(
            whole,
            resolveGuardLinePrint(sessionActive = true, frozen = lack, live = whole)
        )
    }

    @Test
    fun unmeasuredLivePrintLeavesTheFreezeStanding() {
        assertEquals(
            whole,
            resolveGuardLinePrint(sessionActive = true, frozen = whole, live = null)
        )
    }

    @Test
    fun noFreezeYetLivePrintStands() {
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = true, frozen = null, live = lack)
        )
    }

    @Test
    fun idleBandIsTheLivePrint() {
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = false, frozen = whole, live = lack)
        )
        assertNull(
            resolveGuardLinePrint(sessionActive = false, frozen = whole, live = null)
        )
    }
}
