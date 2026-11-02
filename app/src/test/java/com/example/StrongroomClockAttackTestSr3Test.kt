package com.example

import com.example.data.ClockHonesty
import com.example.domain.StrongroomDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneId

/**
 * TEST-SR-3 (T40) — CLOCK MANIPULATION (ledger B-7).
 *
 * The span is ELAPSED time from the sealing. The wall clock is
 * consulted exactly once — at sealing, to price the span — and
 * never again: [StrongroomDay.SealSpan] carries no wall field, so
 * after the swing there is nothing left for a wall-clock game to
 * move. These tests attack from every wall direction: forward
 * jump, rollback, timezone travel, and the DST repeated hour.
 *
 * PL-003 RIDES OPEN: the 115.548-second question is the ascent
 * record's (Stage 1 t0 vs its pair's filing completion; decider
 * Ren; problem-ledger PL-003). Nothing here settles it. Where a
 * test measures a wall-vs-elapsed discrepancy, the measurement is
 * PRINTED as data (see the DST test) — measured, reported, never
 * silently settled, exactly as charged.
 *
 * Venue-environment proof only. On-device clock behaviour stands
 * UNPROVEN-ON-DEVICE (Audit A G-7).
 */
class StrongroomClockAttackTestSr3Test {

    private val utc: ZoneId = ZoneId.of("UTC")

    // Seal struck at elapsed 100_000 in boot 3, span 3h.
    private val seal = StrongroomDay.SealSpan(100_000L, 3, 10_800_000L)

    @Test
    fun wallJumpForward_cannotShortenTheSeal_liftFiresOnElapsedOnly() {
        // The wall leaps +6h the minute after sealing. The span has
        // no wall field to consult: one elapsed millisecond before
        // the lifting, the seal stands; at the boundary, it lifts.
        val justBefore = seal.liftingAtElapsedMillis - 1L
        assertFalse(seal.isLifted(justBefore, nowBootCount = 3))
        assertEquals(StrongroomDay.SealState.SEALED, seal.stateAt(justBefore, 3, sealFailed = false))
        assertTrue(seal.isLifted(seal.liftingAtElapsedMillis, nowBootCount = 3))
        assertEquals(1L, seal.remainingMillis(justBefore))
    }

    @Test
    fun wallRollback_cannotExtendOrRewindTheSeal_heldTimeIsElapsedOnly() {
        // The wall rolls back 12h. Held time is elapsed arithmetic
        // and nothing else: at +1h elapsed exactly 1h is held,
        // whatever any wall anywhere says.
        assertEquals(3_600_000L, seal.heldMillis(100_000L + 3_600_000L))
        // A (forged) elapsed reading before the sealing floors at 0
        // — held time never goes negative, a seal is never "unheld".
        assertEquals(0L, seal.heldMillis(0L))
        assertEquals(0L, seal.heldMillis(99_999L))
    }

    @Test
    fun spanPricing_theWallIsConsultedOnceAtSealing_andTheRepeatedHourIsElapsedTruth() {
        // DST fall-back, Europe/London 2026-10-25: at 02:00 BST the
        // clocks go back to 01:00 GMT — the 01:00 hour happens
        // twice. Seal at wall 00:30 BST for a 06:00 lifting.
        val london = ZoneId.of("Europe/London")
        val sealWall = java.time.LocalDateTime.of(2026, 10, 25, 0, 30)
            .atZone(london).toInstant().toEpochMilli()
        val span = StrongroomDay.spanDurationAtSealing(sealWall, LocalTime.of(6, 0), london)
        // MEASURED AND REPORTED (PL-003 discipline): naive wall
        // subtraction says 5.5h; the instant truth — the only truth
        // an elapsed span may price — is 6.5h, because the repeated
        // hour is real elapsed time he must live through sealed.
        val naiveWallMillis = 5L * 3_600_000L + 1_800_000L
        println(
            "TEST-SR-3 MEASUREMENT dst-fallback: spanMillis=$span " +
                "naiveWallMillis=$naiveWallMillis discrepancyMillis=${span - naiveWallMillis}"
        )
        assertEquals(6L * 3_600_000L + 1_800_000L, span)
        // The spring-forward twin: 2027-03-28, seal 00:30 GMT for
        // 06:00 BST — the skipped hour is NOT elapsed time.
        val springWall = java.time.LocalDateTime.of(2027, 3, 28, 0, 30)
            .atZone(london).toInstant().toEpochMilli()
        val springSpan = StrongroomDay.spanDurationAtSealing(springWall, LocalTime.of(6, 0), london)
        println(
            "TEST-SR-3 MEASUREMENT dst-spring: spanMillis=$springSpan " +
                "naiveWallMillis=$naiveWallMillis discrepancyMillis=${springSpan - naiveWallMillis}"
        )
        assertEquals(4L * 3_600_000L + 1_800_000L, springSpan)
    }

    @Test
    fun timezoneTravel_afterSealing_movesNothing_theSpanHasNoZone() {
        // Pricing the same instant from two zones at SEALING is the
        // one lawful wall consultation and it prices by instant:
        // 20:00Z == 01:30 next-day IST; a 06:00 lifting in the
        // sealing zone is what was signed.
        val sealWall = java.time.LocalDateTime.of(2026, 11, 2, 20, 0).atZone(utc).toInstant().toEpochMilli()
        val spanUtc = StrongroomDay.spanDurationAtSealing(sealWall, LocalTime.of(6, 0), utc)
        assertEquals(10L * 3_600_000L, spanUtc)
        // After sealing, travel is inexpressible against the span:
        // stateAt/held/remaining take elapsed + boot only. This
        // test's existence over those signatures IS the assertion —
        // there is no zone or wall parameter left to attack.
        val struck = StrongroomDay.SealSpan(100_000L, 3, spanUtc)
        assertEquals(spanUtc - 1L, struck.remainingMillis(struck.sealedAtElapsedMillis + 1L))
    }

    @Test
    fun clockHonesty_forwardJumpAndRollback_neitherMovesTheHonestNow() {
        // The substrate's dual-clock discipline (issue #82), which
        // the week's verdicts read: anchor wall 1_000_000 at
        // elapsed 500_000, boot 3.
        // Wall jumps +6h while 10 elapsed minutes pass: the honest
        // now advances by the elapsed 10 minutes only.
        val jumped = ClockHonesty.decide(
            anchorWallMillis = 1_000_000L, anchorElapsedRealtimeMillis = 500_000L, anchorBootCount = 3,
            nowWallMillis = 1_000_000L + 6L * 3_600_000L + 600_000L,
            nowElapsedRealtimeMillis = 500_000L + 600_000L, nowBootCount = 3
        )
        assertEquals(1_000_000L + 600_000L, jumped.honestNowMillis)
        assertFalse(jumped.reanchor)
        // Wall rolls back 12h: same honest now — a rollback cannot
        // rewind a week, a purse, or a crank.
        val rolled = ClockHonesty.decide(
            anchorWallMillis = 1_000_000L, anchorElapsedRealtimeMillis = 500_000L, anchorBootCount = 3,
            nowWallMillis = 1_000_000L - 12L * 3_600_000L,
            nowElapsedRealtimeMillis = 500_000L + 600_000L, nowBootCount = 3
        )
        assertEquals(1_000_000L + 600_000L, rolled.honestNowMillis)
        // Ordinary NTP-sized drift (inside tolerance) is believed.
        val drift = ClockHonesty.decide(
            anchorWallMillis = 1_000_000L, anchorElapsedRealtimeMillis = 500_000L, anchorBootCount = 3,
            nowWallMillis = 1_000_000L + 600_000L + 30_000L,
            nowElapsedRealtimeMillis = 500_000L + 600_000L, nowBootCount = 3
        )
        assertEquals(1_000_000L + 630_000L, drift.honestNowMillis)
        assertTrue(drift.reanchor)
    }

    @Test
    fun prematureWake_theSpanRefusesAndTheNextFiringIsUnmoved() {
        // A moved wall fires the alarm hint early. The wiring
        // re-derives from the span (bridge) — at routing level the
        // truth it re-derives is: not lifted, and the firing
        // schedule is exactly where it was.
        val signing = StrongroomDay.SigningRecord(
            filingDateIso = "2026-11-03",
            lockedPackages = setOf("com.video.app"),
            liftingTime = LocalTime.of(6, 0),
            sentence = null,
            breakLengthMinutes = 20,
            breakFiresAtOffsetMillis = 3_600_000L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )
        val earlyWake = seal.sealedAtElapsedMillis + 60_000L // 1 minute in
        assertFalse(seal.isLifted(earlyWake, nowBootCount = 3))
        val next = com.example.domain.StrongroomRouting.nextFiring(signing, seal, earlyWake)
        assertEquals(com.example.domain.StrongroomRouting.FiringKind.BREAK_START, next!!.kind)
        assertEquals(seal.sealedAtElapsedMillis + 3_600_000L, next.atElapsedMillis)
    }
}
