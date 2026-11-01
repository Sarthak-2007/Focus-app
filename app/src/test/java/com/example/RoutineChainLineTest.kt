package com.example

import com.example.ui.components.ROUTINE_CHAIN_STALE_MS
import com.example.ui.components.routineChainLineFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * PIN-SCOPE: RENDER — pins the printed text of the pure
 * routineChainLineFor derivation (GuardLine.kt); no Android,
 * no posting, no source reads.
 *
 * Issue #94 F4 (Audit R2, oem-battery-reality): the routine-chain
 * liveness line — the first reader of the heartbeat's
 * lastRoutineTickMillis stamp. Before this, the stamp was written
 * every tick and read nowhere, so a dead chain was invisible
 * in-app. These pins hold the line's four honest states:
 * no window → no claim; window + fresh stamp → LIVE; window +
 * stamp older than two tick intervals → DEAD with the age;
 * window + no stamp ever → DEAD, never ticked.
 */
class RoutineChainLineTest {

    private val now = 1_700_000_000_000L

    @Test
    fun noWindowEnforcing_printsNothing() {
        // Outside a window the chain legitimately sleeps until the
        // next boundary — a stale stamp there is not a death, and
        // the line must claim nothing.
        assertNull(routineChainLineFor(0L, windowActive = false, nowMillis = now))
        assertNull(
            routineChainLineFor(
                now - ROUTINE_CHAIN_STALE_MS * 10, windowActive = false, nowMillis = now
            )
        )
    }

    @Test
    fun windowActive_freshStamp_printsLive() {
        val print = routineChainLineFor(now - 5_000L, windowActive = true, nowMillis = now)
        assertEquals("ROUTINE — LIVE", print?.text)
    }

    @Test
    fun windowActive_stampAtTheStaleLine_printsLive() {
        // Exactly two tick intervals old is not yet DEAD — the
        // stale line is strictly past it.
        val print = routineChainLineFor(
            now - ROUTINE_CHAIN_STALE_MS, windowActive = true, nowMillis = now
        )
        assertEquals("ROUTINE — LIVE", print?.text)
    }

    @Test
    fun windowActive_staleStamp_printsDeadWithAge() {
        val print = routineChainLineFor(now - 184_000L, windowActive = true, nowMillis = now)
        assertEquals("ROUTINE — DEAD · LAST TICK 184S AGO", print?.text)
    }

    @Test
    fun windowActive_neverTicked_printsDeadNeverTicked() {
        val print = routineChainLineFor(0L, windowActive = true, nowMillis = now)
        assertEquals("ROUTINE — DEAD · NEVER TICKED", print?.text)
    }
}
