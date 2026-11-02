package com.example

import com.example.domain.StrongroomGuard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P1 (BUILD-SR-3) — the Guard state machine, pure JVM.
 *
 * His Oct 31 rulings by name (Audit A §3): (1) frozen allowance —
 * tighten instant, raise from next day-start only, loosening beyond
 * the hard stop once spent; (2) slow off — off at next day-start,
 * on instant, no off while sealed; (3) a stretch has NO off — its
 * end date is the exit.
 */
class StrongroomGuardP1Test {

    private val today = "2026-11-03"
    private val nextDayStart = 1_000_000L
    private val base = StrongroomGuard.GuardState(allowanceMinutes = 30)

    // ---------- rule 1: frozen allowance ----------

    @Test
    fun tighten_isInstant_anytime_evenInAStretch() {
        val inStretch = base.copy(stretchEndMillis = 5_000_000L)
        val tightened = StrongroomGuard.tighten(inStretch, 10)
        assertEquals(10, tightened.allowanceMinutes)
        assertEquals(-1, tightened.pendingAllowanceMinutes)
    }

    @Test
    fun tighten_supersedesAPendingRaise() {
        val (raised, _) = StrongroomGuard.requestRaise(base, 45, today, nextDayStart)
        val tightened = StrongroomGuard.tighten(raised, 20)
        assertEquals(20, tightened.allowanceMinutes)
        assertEquals(-1, tightened.pendingAllowanceMinutes)
    }

    @Test
    fun raise_takesEffectFromNextDayStart_neverSameDay() {
        val (state, result) = StrongroomGuard.requestRaise(base, 45, today, nextDayStart)
        assertEquals(
            StrongroomGuard.RaiseRequest.Scheduled(45, nextDayStart), result
        )
        assertEquals(30, state.allowanceMinutes) // still today's word
        val before = StrongroomGuard.applyPending(state, nextDayStart - 1)
        assertEquals(30, before.allowanceMinutes)
        val after = StrongroomGuard.applyPending(state, nextDayStart)
        assertEquals(45, after.allowanceMinutes)
        assertEquals(-1, after.pendingAllowanceMinutes)
    }

    @Test
    fun raise_onceSpent_sitsBeyondTheHardStop() {
        val spent = base.copy(spentDateIso = today, spentMinutesThatDay = 30)
        val (state, result) = StrongroomGuard.requestRaise(spent, 45, today, nextDayStart)
        assertEquals(StrongroomGuard.RaiseRequest.BeyondHardStop, result)
        assertEquals(-1, state.pendingAllowanceMinutes) // not even scheduled
        // A spend on ANOTHER day never blocks: the key is the filing date.
        val otherDay = base.copy(spentDateIso = "2026-11-02", spentMinutesThatDay = 30)
        val (_, result2) = StrongroomGuard.requestRaise(otherDay, 45, today, nextDayStart)
        assertTrue(result2 is StrongroomGuard.RaiseRequest.Scheduled)
    }

    @Test
    fun hardStop_readsTheDaysOwnPurse_whenItDivergesFromTheGuardAllowance() {
        // DF-8: the day's purse (10) is smaller than the Guard
        // allowance (30). Ten spent minutes stop the DRUM — and the
        // hard stop must read the day's purse, or a raise slips
        // through the seam while the drum is honestly stopped.
        val divergent = base.copy(
            spentDateIso = today, spentMinutesThatDay = 10, dayPurseTotalMinutes = 10
        )
        val (_, result) = StrongroomGuard.requestRaise(divergent, 45, today, nextDayStart)
        assertEquals(StrongroomGuard.RaiseRequest.BeyondHardStop, result)
        // With no day standing (-1), the allowance in force governs.
        val noDay = base.copy(spentDateIso = today, spentMinutesThatDay = 10)
        val (_, result2) = StrongroomGuard.requestRaise(noDay, 45, today, nextDayStart)
        assertTrue(result2 is StrongroomGuard.RaiseRequest.Scheduled)
    }

    // ---------- rule 2: slow off ----------

    @Test
    fun off_takesEffectAtNextDayStart_onIsInstant() {
        val (state, result) = StrongroomGuard.requestOff(base, nowMillis = 10L, sealedBlockActive = false, nextDayStartMillis = nextDayStart)
        assertEquals(StrongroomGuard.OffRequest.Scheduled(nextDayStart), result)
        assertTrue(state.guardOn) // stands until it lands
        assertFalse(StrongroomGuard.applyPending(state, nextDayStart).guardOn)
        // On is instant and cancels a pending off.
        val backOn = StrongroomGuard.turnOn(state)
        assertTrue(backOn.guardOn)
        assertEquals(0L, backOn.pendingOffFromMillis)
        assertTrue(StrongroomGuard.applyPending(backOn, nextDayStart).guardOn)
    }

    @Test
    fun off_whileSealed_doesNotExist_refusedNotQueued() {
        val (state, result) = StrongroomGuard.requestOff(base, nowMillis = 10L, sealedBlockActive = true, nextDayStartMillis = nextDayStart)
        assertEquals(
            StrongroomGuard.OffRequest.Refused(StrongroomGuard.OffRefusal.SEALED_ACTIVE), result
        )
        assertEquals(0L, state.pendingOffFromMillis) // nothing queued behind the seal
    }

    // ---------- rule 3: a stretch has no off ----------

    @Test
    fun stretch_hasNoOff_itsEndIsTheOnlyExit() {
        val stretch = StrongroomGuard.beginStretch(base, endMillis = 9_000_000L, nowMillis = 10L)
        val (_, result) = StrongroomGuard.requestOff(stretch, nowMillis = 20L, sealedBlockActive = false, nextDayStartMillis = nextDayStart)
        assertEquals(
            StrongroomGuard.OffRequest.Refused(StrongroomGuard.OffRefusal.STRETCH_ACTIVE), result
        )
        // Before the end: still standing. At the end: ended, by time alone.
        assertTrue(StrongroomGuard.applyPending(stretch, 8_999_999L).stretchActive(8_999_999L))
        val ended = StrongroomGuard.applyPending(stretch, 9_000_000L)
        assertEquals(0L, ended.stretchEndMillis)
        assertFalse(ended.stretchActive(9_000_000L))
    }

    @Test
    fun stretch_beginningCancelsAPendingSlowOff() {
        val (offPending, _) = StrongroomGuard.requestOff(base, nowMillis = 10L, sealedBlockActive = false, nextDayStartMillis = nextDayStart)
        assertTrue(offPending.pendingOffFromMillis > 0L)
        val stretch = StrongroomGuard.beginStretch(offPending, endMillis = 9_000_000L, nowMillis = 20L)
        assertEquals(0L, stretch.pendingOffFromMillis)
        assertTrue(stretch.guardOn)
    }
}
