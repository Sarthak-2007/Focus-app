package com.example

import com.example.ui.components.GUARD_NOT_CHECKED_TEXT
import com.example.ui.components.GuardLinePrint
import com.example.ui.components.resolveGuardLinePrint
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Audit R3 #113, finding 2 — the header band's tense law,
 * reconciled with issue #9 (P-6 / Zahn Z-4).
 *
 * The seal freeze used to be the band's print for the session's
 * whole life: revoke a permission mid-session and the header
 * kept printing the frozen whole print over the corpse. The
 * resolution: a MEASURED live print outranks the captured one,
 * in both directions; the freeze survives only as the fallback
 * when the live print is at the NOT CHECKED floor — and a freeze
 * captured AT that floor holds nothing at all, so a restored
 * session can never be frozen at NOT CHECKED for its whole life.
 * Since issue #9 the live print is never null: unmeasured prints
 * `GUARD — NOT CHECKED` itself, never a blank.
 */
class GuardLineTruthTest {

    private val whole = GuardLinePrint("GUARD 6/6")
    private val lack = GuardLinePrint("GUARD 5/6 — OVERLAY")
    private val notChecked = GuardLinePrint(GUARD_NOT_CHECKED_TEXT)

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
    fun unmeasuredLivePrintLeavesTheMeasuredFreezeStanding() {
        assertEquals(
            whole,
            resolveGuardLinePrint(sessionActive = true, frozen = whole, live = notChecked)
        )
    }

    @Test
    fun notCheckedFreezeHoldsNothing() {
        // Zahn Z-4: a freeze captured at the floor is ignorance,
        // not a seal fact — the live print stands, floor or not.
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = true, frozen = notChecked, live = lack)
        )
        assertEquals(
            notChecked,
            resolveGuardLinePrint(
                sessionActive = true, frozen = notChecked, live = notChecked
            )
        )
    }

    @Test
    fun noFreezeYetLivePrintStands() {
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = true, frozen = null, live = lack)
        )
        assertEquals(
            notChecked,
            resolveGuardLinePrint(sessionActive = true, frozen = null, live = notChecked)
        )
    }

    @Test
    fun idleBandIsTheLivePrint() {
        assertEquals(
            lack,
            resolveGuardLinePrint(sessionActive = false, frozen = whole, live = lack)
        )
        assertEquals(
            notChecked,
            resolveGuardLinePrint(sessionActive = false, frozen = whole, live = notChecked)
        )
    }
}
