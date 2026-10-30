package com.example

import com.example.data.MonolithRepository
import com.example.data.SessionOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #290 (Audit R5 supplement — Forest, second pass, F1):
 * FLOW's terminal law. Before the cure, a count-up session ran
 * forever — updateSessionProgress's stopwatch branch merely
 * copied elapsed into the row and returned, and a sub-floor
 * manual stop booked the generic ENDED_EARLY. After the cure:
 * the 4-hour cap ends FLOW in the tick itself, and the
 * sub-floor stop books its own STOPWATCH_WITHERED outcome.
 *
 * (The F4 event-stream fold that originally shared this file
 * was withdrawn at merge time: the same cure landed on main
 * first via issue #332's ForegroundDoor, and a second competing
 * fold does not ship.)
 */
class StopwatchTerminalLawTest {

    @Test
    fun capExistsAboveTheFloor() {
        assertTrue(
            MonolithRepository.STOPWATCH_MAX_CAP_SECONDS >
                MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS
        )
        assertEquals(4 * 60 * 60, MonolithRepository.STOPWATCH_MAX_CAP_SECONDS)
        assertEquals(20 * 60, MonolithRepository.STOPWATCH_MIN_FLOOR_SECONDS)
    }

    @Test
    fun witheredOutcomeIsDistinctFromEveryOtherEnding() {
        assertEquals("STOPWATCH_WITHERED", SessionOutcome.STOPWATCH_WITHERED)
        assertTrue(SessionOutcome.STOPWATCH_WITHERED != SessionOutcome.STOPWATCH_ENDED)
        assertTrue(SessionOutcome.STOPWATCH_WITHERED != SessionOutcome.ENDED_EARLY)
        assertTrue(SessionOutcome.STOPWATCH_WITHERED != SessionOutcome.GRACE_CANCEL)
        assertTrue(SessionOutcome.STOPWATCH_WITHERED != SessionOutcome.VALVE_BROKE)
    }
}
