package com.example

import com.example.ui.foundation.BoltBodies
import com.example.ui.foundation.CeremonyScores
import com.example.ui.foundation.DoorBody
import com.example.ui.foundation.DrivenDoor
import com.example.ui.foundation.HandTracker
import com.example.ui.foundation.PressBody
import com.example.ui.foundation.StrongroomMaximum
import com.example.ui.foundation.WithdrawalBodies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-33 — covering tests for StrongroomMotion.kt: MOTION-SR-1's
 * filed testimony re-derived by integrating the filed bodies in
 * StrongroomMotion (the numbers are the bodies' testimony, not
 * constants believed on sight; AL-0056). All figures UNPROVEN-ON-DEVICE.
 */
class StrongroomMotionTest {

    // ── Motion primitives ──────────────────────────────────────────

    @Test
    fun doorBodyReproducesItsFiledTestimony() {
        // MOTION-SR-1 §1.1/§3, integrated live. Tolerances carry the
        // documented 1 ms loop-indexing difference (BUILD-SR-18's
        // record) and the strike-compression reading.
        val door = DrivenDoor()
        assertEquals(DoorBody.FiledTestimony.ANGLE_AT_500_MS, door.angleAt(500), 0.35)
        val strike = door.strikeMs!!
        assertTrue("strike $strike", kotlin.math.abs(strike - DoorBody.FiledTestimony.STRIKE_MS) <= 3)
        val settled = door.settledMs!!
        assertTrue("settled $settled", kotlin.math.abs(settled - DoorBody.FiledTestimony.SETTLED_MS) <= 5)
        assertEquals(DoorBody.FiledTestimony.FINAL_DEG, door.finalDeg, 0.15)
        // No rebound: the angle is monotonic across the whole swing.
        var prev = -1.0
        for (ms in 0..3200 step 100) {
            val a = door.angleAt(ms)
            assertTrue("monotonic at $ms", a >= prev - 1e-9)
            prev = a
        }
    }

    @Test
    fun boltsTravelByMassAlone() {
        // MOTION-SR-1 §1.2: one drive, three masses — travels
        // 167/196/239, arrival velocity falling as mass rises.
        val travels = BoltBodies.MASSES.map { BoltBodies.travelMs(it) }
        BoltBodies.FILED_TRAVEL_MS.forEachIndexed { i, filed ->
            assertTrue("bolt $i travel ${travels[i]} vs $filed",
                kotlin.math.abs(travels[i] - filed) <= 2)
        }
        val arrivals = BoltBodies.MASSES.map { BoltBodies.arrivalVelocity(it) }
        assertTrue(arrivals[0] > arrivals[1] && arrivals[1] > arrivals[2])
        // Momentum rises: the heaviest lands hardest.
        val momenta = BoltBodies.MASSES.mapIndexed { i, m -> m * arrivals[i] }
        assertTrue(momenta[0] < momenta[1] && momenta[1] < momenta[2])
    }

    @Test
    fun withdrawalsAreNotReversedBolts() {
        // MOTION-SR-1 §1.3: reverse load order, different drive and
        // resistance, strain first; slides 275/221/183.
        assertEquals(listOf(2.60, 1.50, 0.90), WithdrawalBodies.MASSES)
        val slides = WithdrawalBodies.MASSES.map { WithdrawalBodies.slideMs(it) }
        WithdrawalBodies.FILED_SLIDE_MS.forEachIndexed { i, filed ->
            assertTrue("withdrawal $i slide ${slides[i]} vs $filed",
                kotlin.math.abs(slides[i] - filed) <= 2)
        }
        assertTrue(WithdrawalBodies.DRIVE != BoltBodies.DRIVE)
        assertTrue(WithdrawalBodies.RESISTANCE != BoltBodies.RESISTANCE)
    }

    @Test
    fun pressIsOverdampedAndSettlesAsFiled() {
        // MOTION-SR-1 §1.4: ζ derived = 1.361, settle 431 ms.
        assertEquals(1.361, PressBody.Body.dampingRatio, 0.002)
        assertTrue(PressBody.Body.dampingRatio > 1.0)
    }

    @Test
    fun handTrackerIsCriticallyDamped() {
        // MOTION-SR-1 §1.1: ζ derived = 1.000.
        assertEquals(1.0, HandTracker.Body.dampingRatio, 0.001)
    }

    @Test
    fun ceremonyClocksAreTheFiledOnes() {
        // MOTION-SR-1 §0 + the ascending-weight law (0.64/0.82/1.0).
        val clunks = CeremonyScores.Seal.filter { it.name.startsWith("clunk") }
        assertEquals(listOf(1160, 1660, 2340), clunks.map { it.atMs })
        assertEquals(listOf(0.64, 0.82, 1.0), clunks.map { it.weight })
        val snaps = CeremonyScores.Lifting.filter { it.name.startsWith("snap") }
        assertEquals(listOf(1440, 1940, 2440), snaps.map { it.atMs })
        assertEquals(
            5574,
            CeremonyScores.Lifting.first { it.name == "the-strike" }.atMs
        )
    }

    @Test
    fun deskClockExtentIsLinear() {
        // Census E-14: extent(2t) = 2·extent(t) — an eased extent
        // cannot pass.
        for (t in listOf(0.0, 0.13, 0.25, 0.4, 0.5)) {
            assertEquals(
                2.0 * StrongroomMaximum.deskClockExtent(t),
                StrongroomMaximum.deskClockExtent(2 * t),
                1e-9
            )
        }
    }

    @Test
    fun spentStopTravelIsTheRuledFourDegrees() {
        // Census E-10: strain travel 4.0° on every spent stop.
        assertEquals(4.0, StrongroomMaximum.SPENT_STOP_TRAVEL_DEG, 0.0)
        assertEquals(
            StrongroomMaximum.SPENT_STOP_TRAVEL_DEG,
            StrongroomMaximum.spentPurseStop(120.0).approachZone,
            0.0
        )
    }
}
