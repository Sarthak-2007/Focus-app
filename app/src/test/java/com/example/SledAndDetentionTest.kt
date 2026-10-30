package com.example

import com.example.data.Detention
import com.example.ui.SledReceiptMark
import com.example.ui.SledReturnGate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Slice E — Schlitten §3.3's gate and Nocke §5's detention,
 * table-proven as pure arithmetic.
 *
 * The gate: ARMED at publish; the host's tryStart fires AT MOST
 * once per session (the loser of every race observes false);
 * navigation VOIDs an armed gate; expiry voids only an
 * UNWITNESSED gate — a receipt nobody composed a Deck for must
 * not fire its return hours later on a stale ARMED, and a
 * witnessed one must survive to its firing.
 *
 * The factory rule (§2.3): seated while a session runs, seated
 * while a return is pending (receipt + ARMED), home otherwise.
 */
class SledReturnGateTest {

    @Test
    fun tryStartFiresExactlyOnce() {
        val gate = SledReturnGate()
        gate.arm(7, deadlineMono = 10_000L)
        assertTrue(gate.tryStart(7))
        assertFalse("a second host must observe false", gate.tryStart(7))
        assertFalse(gate.tryStart(7))
        assertEquals(SledReturnGate.Stage.FIRED, gate.stage)
    }

    @Test
    fun foreignSessionCannotFireOrVoid() {
        val gate = SledReturnGate()
        gate.arm(7, deadlineMono = 10_000L)
        assertFalse(gate.tryStart(8))
        gate.void(8)
        gate.expire(8)
        assertEquals(SledReturnGate.Stage.ARMED, gate.stage)
        assertTrue(gate.tryStart(7))
    }

    @Test
    fun voidIsTerminalForThatSession() {
        val gate = SledReturnGate()
        gate.arm(7, deadlineMono = 10_000L)
        gate.void(7)
        assertEquals(SledReturnGate.Stage.VOID, gate.stage)
        assertFalse(gate.tryStart(7))
    }

    @Test
    fun expiryVoidsOnlyTheUnwitnessed() {
        val unwitnessed = SledReturnGate()
        unwitnessed.arm(7, deadlineMono = 10_000L)
        unwitnessed.expire(7)
        assertEquals(SledReturnGate.Stage.VOID, unwitnessed.stage)
        assertFalse(unwitnessed.tryStart(7))

        val witnessed = SledReturnGate()
        witnessed.arm(7, deadlineMono = 10_000L)
        witnessed.witness(7)
        witnessed.expire(7)
        assertEquals(
            "a witnessed gate survives expiry to its firing",
            SledReturnGate.Stage.ARMED, witnessed.stage
        )
        assertTrue(witnessed.tryStart(7))
    }

    @Test
    fun initialFractionTruthTable() {
        val gate = SledReturnGate()
        // No session, no receipt: home.
        assertEquals(0f, gate.initialFraction(false, null, 0L), 0f)
        // A session runs: seated, whatever the gate says.
        assertEquals(1f, gate.initialFraction(true, null, 0L), 0f)
        // Receipt standing + gate ARMED for it: seated (return pending).
        gate.arm(7, deadlineMono = 10_000L)
        assertEquals(1f, gate.initialFraction(false, SledReceiptMark(7), 0L), 0f)
        // Receipt for a DIFFERENT session: home.
        assertEquals(0f, gate.initialFraction(false, SledReceiptMark(8), 0L), 0f)
        // Gate FIRED (the return already ran): home.
        gate.tryStart(7)
        assertEquals(0f, gate.initialFraction(false, SledReceiptMark(7), 0L), 0f)
        // Gate VOID (he navigated away): home.
        val voided = SledReturnGate()
        voided.arm(9, deadlineMono = 10_000L)
        voided.void(9)
        assertEquals(0f, voided.initialFraction(false, SledReceiptMark(9), 0L), 0f)
    }

    @Test
    fun nextArmReplacesTheLast() {
        val gate = SledReturnGate()
        gate.arm(7, deadlineMono = 10_000L)
        assertTrue(gate.tryStart(7))
        gate.arm(8, deadlineMono = 20_000L)
        assertEquals(8, gate.sessionId)
        assertEquals(SledReturnGate.Stage.ARMED, gate.stage)
        assertTrue(gate.tryStart(8))
    }
}

/**
 * Detention's table (build-ready §7.1): the held frame runs a
 * fixed 1200 from E0; the rack starts at max(1200, P); the
 * receipt's t=0 is min(max(1480, R0 + 280), 1800); the finish
 * books FAULTED with no P by E1520 — and then issues at E1800
 * exactly, in the one lawful form.
 */
class DetentionTableTest {

    @Test
    fun receiptAtFollowsTheFormula() {
        // No pawl at all: the ceiling.
        assertEquals(1800L, Detention.receiptAtMs(null))
        // Pawl inside the held frame: R0 is the frame's own end.
        assertEquals(1480L, Detention.receiptAtMs(900L))
        assertEquals(1480L, Detention.receiptAtMs(1200L))
        // Pawl past the frame: the 280 rack delay rules.
        assertEquals(1580L, Detention.receiptAtMs(1300L))
        assertEquals(1780L, Detention.receiptAtMs(1500L))
        // A late pawl is capped at the ceiling, never past it.
        assertEquals(1800L, Detention.receiptAtMs(1600L))
        assertEquals(1800L, Detention.receiptAtMs(5_000L))
    }

    @Test
    fun rackStartWithNoPawlIsTheCeiling_notTheSentinel() {
        // Audit R1 #40: the ViewModel's detention tail delays by
        // rackStartMs(pawl) + RACK_AFTER_PAWL_MS. With no pawl the
        // old sentinel (Long.MAX_VALUE/4) parked that delay at
        // ~2^61 ms — the faulted finish never retired. The rack
        // now deems itself started at the ceiling: bounded tail.
        assertEquals(1800L, Detention.rackStartMs(null))
        assertTrue(
            Detention.rackStartMs(null) + Detention.RACK_AFTER_PAWL_MS <= 2_200L
        )
        // A real pawl is untouched by the cure.
        assertEquals(1200L, Detention.rackStartMs(900L))
        assertEquals(1500L, Detention.rackStartMs(1500L))
    }

    @Test
    fun faultDeadlineIs1520AndHaltIsImmediate() {
        assertFalse(Detention.isFaulted(null, halted = false, nowOffsetMs = 1519L))
        assertTrue(Detention.isFaulted(null, halted = false, nowOffsetMs = 1520L))
        assertTrue(Detention.isFaulted(null, halted = true, nowOffsetMs = 0L))
        assertFalse(
            "a lifted pawl never faults by deadline",
            Detention.isFaulted(1400L, halted = false, nowOffsetMs = 9_999L)
        )
    }

    @Test
    fun issueOffsetCombinesBothBranches() {
        // Healthy: the formula's answer.
        assertEquals(1580L, Detention.issueOffsetMs(1300L, halted = false, nowOffsetMs = 1400L))
        // Unfaulted and unlifted: nothing issues yet.
        assertEquals(null, Detention.issueOffsetMs(null, halted = false, nowOffsetMs = 1000L))
        // Faulted: E1800 exactly, whatever the clock says.
        assertEquals(1800L, Detention.issueOffsetMs(null, halted = false, nowOffsetMs = 1520L))
        assertEquals(1800L, Detention.issueOffsetMs(null, halted = true, nowOffsetMs = 300L))
    }
}
