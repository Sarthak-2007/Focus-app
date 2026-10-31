package com.example

import com.example.ui.components.airlockStakeLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #325 (Audit R5, Zario visual): the stake line on the
 * Airlock door. The balance prints at the moment of decision;
 * at zero the spent state is stated, never hidden; an uncarried
 * balance (negative) prints nothing rather than inventing one.
 */
class AirlockStakeLineTest {

    @Test
    fun uncarriedBalancePrintsNothing() {
        assertNull(airlockStakeLine(-1))
        assertNull(airlockStakeLine(-100))
    }

    @Test
    fun zeroStatesTheSpentValve() {
        val line = airlockStakeLine(0)
        assertTrue(line!!.contains("NONE LEFT THIS WEEK"))
        assertTrue(line.contains("SPENT"))
    }

    @Test
    fun oneUsesTheSingularPrint() {
        assertEquals("EMERGENCY BREAK: 1 LEFT THIS WEEK", airlockStakeLine(1))
    }

    @Test
    fun manyPrintTheirCount() {
        assertEquals("EMERGENCY BREAK: 3 LEFT THIS WEEK", airlockStakeLine(3))
    }
}
