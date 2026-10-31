package com.example

import com.example.ui.shouldRederiveAirlock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #87 (process-death F4, re-derive half): the restored
 * Airlock stands only when EVERY stored fact still holds. Each
 * test below breaks exactly one fact of the all-true case — the
 * re-derive must refuse on any single break, because a door
 * re-presented on a dead fact is a fabricated state: the seal's
 * survival across process death is the product's core promise,
 * and a restoration that can invent doors weakens it.
 */
class AirlockRederiveTest {

    private fun decide(
        newestOutcome: String? = "Intercepted",
        airlockAlreadyVisible: Boolean = false,
        sessionActive: Boolean = true,
        interceptDuringSession: Boolean = true,
        intentionMatches: Boolean = true,
        packageStillBlocked: Boolean = true,
        peekLeaseLiveForPackage: Boolean = false
    ): Boolean = shouldRederiveAirlock(
        newestOutcome = newestOutcome,
        airlockAlreadyVisible = airlockAlreadyVisible,
        sessionActive = sessionActive,
        interceptDuringSession = interceptDuringSession,
        intentionMatches = intentionMatches,
        packageStillBlocked = packageStillBlocked,
        peekLeaseLiveForPackage = peekLeaseLiveForPackage
    )

    @Test
    fun allFactsHold_restores() {
        assertTrue(decide())
    }

    @Test
    fun noInterceptRow_restoresNothing() {
        assertFalse(decide(newestOutcome = null))
    }

    @Test
    fun verbOutcomeOnTop_restoresNothing() {
        // Every verb books a newer row than the arrival row, so a
        // verb outcome as the newest row means the user chose.
        assertFalse(decide(newestOutcome = "Used 2m Peek"))
        assertFalse(decide(newestOutcome = "Stayed Focused"))
        assertFalse(decide(newestOutcome = "Split Into 10m Micro-Step"))
        assertFalse(decide(newestOutcome = "Left Airlock Without Choosing"))
    }

    @Test
    fun airlockAlreadyStanding_restoresNothing() {
        assertFalse(decide(airlockAlreadyVisible = true))
    }

    @Test
    fun sessionOver_restoresNothing() {
        assertFalse(decide(sessionActive = false))
    }

    @Test
    fun interceptPredatesSession_restoresNothing() {
        // The row belongs to an earlier session that died with the
        // process; the live session never saw this reach.
        assertFalse(decide(interceptDuringSession = false))
    }

    @Test
    fun intentionMismatch_restoresNothing() {
        assertFalse(decide(intentionMatches = false))
    }

    @Test
    fun packageSinceOpened_restoresNothing() {
        assertFalse(decide(packageStillBlocked = false))
    }

    @Test
    fun livePeekLease_restoresNothing() {
        // The peek verb WAS chosen; the F2 pill rehydration owns
        // that restoration, never an Airlock over a live lease.
        assertFalse(decide(peekLeaseLiveForPackage = true))
    }
}
