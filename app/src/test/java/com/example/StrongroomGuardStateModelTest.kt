package com.example

import com.example.domain.StrongroomGuard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

/**
 * TEST-SR-1 (T40) — THE LOOSENING ROADS + THE GUARD, for P1.
 *
 * Obligations: ledger D-1 (frozen allowance) against B-6 (the
 * two-signature law for loosenings, FX-29) — with their collision
 * G-a (ledger §G, OBJ-T7-4) carried EXACTLY as the ledger carries
 * it: UNRESOLVED, decider Ren. This suite asserts D-1 as
 * filed-and-applied (the build's standing behaviour) and parks the
 * FX-29 road in a disabled, named expectation below. NOTHING in
 * this file settles G-a, and no seat may cite it as settling G-a.
 *
 * Also D-2 (slow off) and D-3 (a stretch has no off) at the points
 * the lane's own tests do not already pin. Authored under
 * METHODS-SR-4's method (top-level, own @Test, package com.example).
 */
class StrongroomGuardStateModelTest {

    private val today = "2026-11-03"
    private val nextDayStart = 1_000_000L
    private val base = StrongroomGuard.GuardState(allowanceMinutes = 30)

    private fun fieldNames(type: Class<*>): Set<String> =
        type.declaredFields.map { it.name }.filter { !it.startsWith("$") }.toSet()

    // ------------------------------------------------------------------
    // D-1 as applied — the raise lands at the next day-start
    // ------------------------------------------------------------------

    @Test
    fun d1_raise_landsExactlyAtTheDayStart_andApplyingTwiceChangesNothing() {
        val (state, result) = StrongroomGuard.requestRaise(base, 45, today, nextDayStart)
        assertEquals(StrongroomGuard.RaiseRequest.Scheduled(45, nextDayStart), result)
        // One millisecond before the day-start: still today's word.
        assertEquals(30, StrongroomGuard.applyPending(state, nextDayStart - 1).allowanceMinutes)
        // At the day-start: the raise stands. Applying again is a
        // no-op — a landed raise cannot land twice or drift.
        val landed = StrongroomGuard.applyPending(state, nextDayStart)
        assertEquals(45, landed.allowanceMinutes)
        assertEquals(landed, StrongroomGuard.applyPending(landed, nextDayStart + 86_400_000L))
    }

    @Test
    fun d1_raise_onceSpent_isNotEvenScheduled_andNothingLandsTomorrow() {
        // Beyond the hard stop means UNREACHABLE, not delayed: no
        // pending raise exists, so tomorrow's applyPending has
        // nothing to land.
        val spent = base.copy(spentDateIso = today, spentMinutesThatDay = 30)
        val (state, result) = StrongroomGuard.requestRaise(spent, 45, today, nextDayStart)
        assertEquals(StrongroomGuard.RaiseRequest.BeyondHardStop, result)
        assertEquals(-1, state.pendingAllowanceMinutes)
        assertEquals(30, StrongroomGuard.applyPending(state, nextDayStart).allowanceMinutes)
    }

    @Test
    fun d1_tighten_isInstant_evenOnASpentDay_tighteningIsNeverBeyondTheStop() {
        // The hard stop fences LOOSENING only. On a spent day the
        // strong direction still moves instantly.
        val spent = base.copy(spentDateIso = today, spentMinutesThatDay = 30)
        val tightened = StrongroomGuard.tighten(spent, 5)
        assertEquals(5, tightened.allowanceMinutes)
    }

    // ------------------------------------------------------------------
    // The loosening roads, swept together (D-1 vs D-2)
    // ------------------------------------------------------------------

    @Test
    fun loosenings_everyRoadLandsAtADayStart_orIsRefused_theOnlyInstantChangeLowers() {
        // Road 1 (D-1): a raise schedules at the day-start.
        val (_, raise) = StrongroomGuard.requestRaise(base, 45, today, nextDayStart)
        assertEquals(nextDayStart, (raise as StrongroomGuard.RaiseRequest.Scheduled).effectiveAtMillis)
        // Road 2 (D-2): an off schedules at the day-start.
        val (_, off) = StrongroomGuard.requestOff(
            base, nowMillis = 10L, sealedBlockActive = false, nextDayStartMillis = nextDayStart
        )
        assertEquals(nextDayStart, (off as StrongroomGuard.OffRequest.Scheduled).effectiveAtMillis)
        // The single instant transition in the machine only ever
        // LOWERS: a "tighten" that raises is refused by the type.
        val raised = runCatching { StrongroomGuard.tighten(base, 45) }
        assertTrue(raised.isFailure)
    }

    @Test
    fun applyPending_raiseAndOffDueTogether_bothLand() {
        val (withRaise, _) = StrongroomGuard.requestRaise(base, 45, today, nextDayStart)
        val (withBoth, _) = StrongroomGuard.requestOff(
            withRaise, nowMillis = 10L, sealedBlockActive = false, nextDayStartMillis = nextDayStart
        )
        val landed = StrongroomGuard.applyPending(withBoth, nextDayStart)
        assertEquals(45, landed.allowanceMinutes)
        assertFalse(landed.guardOn)
    }

    // ------------------------------------------------------------------
    // D-2 / D-3 — precedence and the stretch's exits
    // ------------------------------------------------------------------

    @Test
    fun d2d3_off_insideAStretch_isRefusedAsAStretch_evenWhileSealed() {
        // Both refusals apply; the stretch's reason is the one
        // returned (a stretch has NO off — the stronger law), and
        // nothing is queued behind either.
        val stretch = StrongroomGuard.beginStretch(base, endMillis = 9_000_000L, nowMillis = 10L)
        val (state, result) = StrongroomGuard.requestOff(
            stretch, nowMillis = 20L, sealedBlockActive = true, nextDayStartMillis = nextDayStart
        )
        assertEquals(
            StrongroomGuard.OffRequest.Refused(StrongroomGuard.OffRefusal.STRETCH_ACTIVE), result
        )
        assertEquals(0L, state.pendingOffFromMillis)
    }

    @Test
    fun d3_stretch_carriesNoOff_andNoWitnessMachinery_inStateOrSurface() {
        // D-3's second half, by construction: no witness/urge-log
        // field exists on the state, and the machine's surface has
        // no cancel/end-stretch or witness operation at all — the
        // end date chosen at setup is the only exit.
        val fields = fieldNames(StrongroomGuard.GuardState::class.java)
        assertEquals(
            // ADOPTION RE-AIM (BUILD-SR-15, C-16): the composed
            // GuardState carries one more field than the author's
            // census — dayPurseTotalMinutes, the standing day
            // purse the allowance is drawn against (default -1 =
            // unprovisioned). It is allowance machinery, not an
            // off and not a witness; D-3's prohibitions below
            // stand untouched.
            setOf(
                "guardOn", "allowanceMinutes", "pendingAllowanceMinutes",
                "pendingAllowanceFromMillis", "pendingOffFromMillis",
                "stretchEndMillis", "spentDateIso", "spentMinutesThatDay",
                "dayPurseTotalMinutes"
            ),
            fields
        )
        assertTrue(fields.none {
            it.contains("witness", ignoreCase = true) || it.contains("urge", ignoreCase = true)
        })
        val methods = StrongroomGuard::class.java.declaredMethods.map { it.name }
        assertTrue(methods.none {
            it.contains("cancel", ignoreCase = true) ||
                it.contains("endStretch", ignoreCase = true) ||
                it.contains("witness", ignoreCase = true) ||
                it.contains("urge", ignoreCase = true)
        })
        // On is instant even inside a stretch — and it does NOT
        // end the stretch: turning on is not an exit either.
        val stretch = StrongroomGuard.beginStretch(base, endMillis = 9_000_000L, nowMillis = 10L)
        val on = StrongroomGuard.turnOn(stretch)
        assertTrue(on.guardOn)
        assertTrue(on.stretchActive(20L))
    }

    // ------------------------------------------------------------------
    // G-a — UNRESOLVED, carried, never settled by this suite
    // ------------------------------------------------------------------

    @Test
    fun ga_asTheMachineStands_noSignatureMachineryExistsOnTheGuardState() {
        // A fact about TODAY's machine, asserted as a fact only:
        // the Guard state carries no signature fields, so the
        // FX-29 two-signature road is not implemented anywhere in
        // it. This neither rules G-a nor pretends the road was
        // considered and rejected — the decider is Ren (ledger §G).
        val fields = fieldNames(StrongroomGuard.GuardState::class.java)
        assertTrue(fields.none { it.contains("signature", ignoreCase = true) })
        val methods = StrongroomGuard::class.java.declaredMethods.map { it.name }
        assertTrue(methods.none { it.contains("sign", ignoreCase = true) })
    }

    /**
     * G-a / OBJ-T7-4 — THE UNRESOLVED EXPECTATION.
     *
     * IF Ren rules that a frozen-allowance raise travels the FX-29
     * two-signature road (ledger B-6), THEN the expectation is: a
     * raise lands at the FIRST DAY-START AFTER THE SECOND
     * SIGNATURE — one signature alone schedules nothing, and the
     * D-1-only behaviour asserted above is superseded.
     *
     * This test is DISABLED until that ruling exists. It is an
     * expectation on the record, never a settled assertion, and a
     * green run of this suite says nothing about it (it does not
     * execute). When the ruling lands, this seat un-ignores it,
     * writes it against the ruled API, and the D-1 tests above are
     * re-read in the same act.
     */
    @Ignore("G-a UNRESOLVED (ledger section G, OBJ-T7-4) — decider Ren; not a settled assertion")
    @Test
    fun ga_RIVAL_twoSignatureRoad_raiseLandsAtFirstDayStartAfterTheSecondSignature() {
        // No two-signature API exists in StrongroomGuard today, so
        // there is nothing honest to assert against. The body is a
        // marker, not a proof.
        assertTrue(
            "G-a unruled: enable only when Ren rules the FX-29 road and the API exists",
            false
        )
    }
}
