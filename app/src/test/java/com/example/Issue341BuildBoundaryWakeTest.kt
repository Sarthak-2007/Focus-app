package com.example

import com.example.service.BoundaryWakeScheduler
import com.example.service.RoutineHeartbeat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R6 #341 BUILD — F17: the redundant boundary wake. The
 * alarm chain (RoutineScheduler) stays the primary path; the
 * WorkManager path (SessionBoundaryWorker) must target the SAME
 * boundary instant, so a boundary reached by either path — or
 * both — is the same boundary (RoutineHeartbeat.tick is
 * idempotent, so double delivery never double-fires a session
 * transition).
 *
 * The worker-fires-half needs a device/emulator WorkManager
 * harness and is named as such in the PR; these cases pin the
 * shared math both paths are built from.
 * PIN scope: PLUMBING.
 */
class Issue341BuildBoundaryWakeTest {

    @Test
    fun boundaryWake_alarmChainAndWorkerPath_targetTheSameBoundaryInstant() {
        val now = 1_700_000_000_000L
        val boundary = now + 1_800_000L // 30 minutes out
        // now + delay == boundary, exactly: both paths wake AT the
        // boundary, not near it.
        assertEquals(boundary, now + BoundaryWakeScheduler.delayMillis(boundary, now))
    }

    @Test
    fun boundaryWake_pastBoundary_clampsToImmediate_neverNegative() {
        val now = 1_700_000_000_000L
        assertEquals(0L, BoundaryWakeScheduler.delayMillis(now - 5_000L, now))
        assertEquals(0L, BoundaryWakeScheduler.delayMillis(now, now))
    }

    @Test
    fun boundaryWake_workerIsAHeartbeatSource() {
        // The worker path feeds the SAME heartbeat funnel as the
        // alarm paths (Source.WORKER exists in the vocabulary) —
        // one funnel is what makes double delivery harmless.
        assertTrue(
            RoutineHeartbeat.Source.entries.any { it.name == "WORKER" }
        )
    }
}
