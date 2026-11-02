package com.example

import com.example.ui.motion.BoltBodies
import com.example.ui.motion.DoorBody
import com.example.ui.motion.DoorCeremonyState
import com.example.ui.motion.DoorMotionPlayer
import com.example.ui.motion.DoorReducedMotion
import com.example.ui.motion.DoorScores
import com.example.ui.motion.PressBody
import com.example.ui.motion.WithdrawalBodies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * DOOR MOTION — MOTION-SR-1's spec, pinned to its own simulation
 * (BUILD-SR-18). The expected figures are door-motion-spec.md's
 * sampled table and generate.py's assertions, read firsthand:
 * strike 2,894 ms swing-relative · settle 3,246 ms · peak ω 50.2°/s ·
 * θ(500 ms) = 2.24° · bolt travels 167/196/239 ms · withdrawal
 * slides 275/221/183 ms · press settle 431 ms, overshoot 0.
 * All figures UNPROVEN-ON-DEVICE (G-7) — these tests pin the
 * arithmetic, never the feel. Tests are end-phase evidence (R6);
 * the lane's standing proof is the compile.
 */
class DoorMotionTest {

    private val sim = DoorBody.simulateOpen()

    @Test
    fun `door opens by weight - slow commitment, strike at 81_2, settle, no rebound`() {
        // Slow commitment: in the first half-second the door has barely admitted it will move.
        assertEquals(2.24f, sim.thetaAt(500), 0.35f)
        // The strike: θ = 81.2° on the rising edge at 2,894 ms swing-relative (spec §3).
        assertTrue(abs((sim.strikeMs ?: -1L) - 2894L) <= 30L)
        // Ceremony-absolute: swing starts at 2,680 → strike at 5,574 (spec §0).
        assertTrue(abs(DoorScores.Lifting.SWING_START + (sim.strikeMs ?: -1L) - 5574L) <= 30L)
        assertEquals(5574L, DoorScores.Lifting.SYNC_STRIKE)
        // Settle inside the spec's band at 3,246 ms sim.
        assertTrue(abs((sim.settleMs ?: -1L) - 3246L) <= 60L)
        // Peak ω past the travel's midpoint: fastest when already committed.
        assertEquals(50.2f, sim.peakOmegaDegS, 2.5f)
        assertTrue(sim.peakOmegaAtMs in 1600L..2100L)
        // No rebound: ω never goes meaningfully negative after the strike; θ monotonic at 100 ms samples.
        assertTrue(sim.minOmegaAfterStrikeDegS >= -0.5f)
        var prev = -1f
        for (t in 0L..3200L step 100L) {
            val theta = sim.thetaAt(t)
            assertTrue("θ must be monotonic (no bounce) at ${t}ms", theta >= prev - 0.001f)
            prev = theta
        }
        // Final rest against the stop: 82.09° sim — compression, not overshoot-and-return.
        assertEquals(82.09f, sim.finalThetaDeg, 0.2f)
    }

    @Test
    fun `bolts - one drive, three masses, the travel gradient is caused by mass`() {
        val travels = (0..2).map { BoltBodies.travel(it).travelMs }
        listOf(167L, 196L, 239L).zip(travels).forEach { (want, got) ->
            assertTrue("bolt travel $got vs spec $want", abs(got - want) <= 12L)
        }
        assertTrue(travels[0] < travels[1] && travels[1] < travels[2])
        // Arrival velocity FALLS as mass rises; momentum (m·v) RISES — the heaviest lands hardest.
        val arrivals = (0..2).map { BoltBodies.travel(it).arrivalVelocity }
        assertTrue(arrivals[0] > arrivals[1] && arrivals[1] > arrivals[2])
        val momenta = BoltBodies.MASSES.zip(arrivals).map { (m, v) -> m * v }
        assertTrue(momenta[0] < momenta[1] && momenta[1] < momenta[2])
        // The seat is a clamp: fraction is exactly 1 at travel end, never beyond.
        for (i in 0..2) {
            assertEquals(1f, BoltBodies.fractionAt(i, travels[i]), 0f)
            assertEquals(1f, BoltBodies.fractionAt(i, travels[i] + 500L), 0f)
            assertTrue(BoltBodies.fractionAt(i, travels[i] - 5L) < 1f)
        }
    }

    @Test
    fun `withdrawals - strain then snap then slide, reverse load order, never a reversed seating`() {
        val slides = (0..2).map { WithdrawalBodies.slide(it).travelMs }
        listOf(275L, 221L, 183L).zip(slides).forEach { (want, got) ->
            assertTrue("withdrawal slide $got vs spec $want", abs(got - want) <= 12L)
        }
        // Heaviest first: withdrawal order is the bolts' mass order reversed.
        assertEquals(BoltBodies.MASSES.reversed(), WithdrawalBodies.MASSES)
        // Strain: no travel while the bolt loads; the slide runs after.
        for (i in 0..2) {
            assertEquals(0f, WithdrawalBodies.fractionAt(i, WithdrawalBodies.STRAIN_MS), 0f)
            assertEquals(1f, WithdrawalBodies.fractionAt(i, WithdrawalBodies.totalMs(i)), 0f)
        }
        // Not a reversed seating: drive and resistance both differ from the bolts'.
        assertTrue(WithdrawalBodies.DRIVE.p0 != BoltBodies.DRIVE.p0)
        assertTrue(WithdrawalBodies.RESISTANCE != BoltBodies.RESISTANCE)
    }

    @Test
    fun `press - overdamped, settle 431 ms, zero overshoot, never a fourth clunk`() {
        val press = PressBody.simulate()
        assertTrue(abs((press.settleMs ?: -1L) - 431L) <= 45L)
        assertEquals(0f, press.overshoot, 0.001f)
        assertTrue(PressBody.BODY.dampingRatio > 1.3f)
    }

    @Test
    fun `interruption law - death resolves to a decided state, never a partial one`() {
        val player = DoorMotionPlayer()
        assertEquals(DoorCeremonyState.OPEN_UNSEALED, player.resolveSealOnDeath(DoorScores.Seal.COMMIT - 1))
        assertEquals(DoorCeremonyState.SEALED, player.resolveSealOnDeath(DoorScores.Seal.COMMIT))
        assertEquals(DoorCeremonyState.SEALED, player.resolveLiftingOnDeath(DoorScores.Lifting.SYNC_TRIP - 1))
        assertEquals(DoorCeremonyState.LIFTED, player.resolveLiftingOnDeath(DoorScores.Lifting.SYNC_TRIP))
    }

    @Test
    fun `player poses follow the ceremony clocks`() {
        val player = DoorMotionPlayer()
        // Before the swing the door has not moved; at strike time it stands at the strike angle.
        assertEquals(0f, player.liftingDoorAngleAt(DoorScores.Lifting.SWING_START), 0f)
        assertTrue(abs(player.liftingDoorAngleAt(DoorScores.Lifting.SYNC_STRIKE) - DoorBody.STRIKE_DEG) < 1.5f)
        assertTrue(player.liftingStrikeFiredAt(DoorScores.Lifting.SYNC_STRIKE + 40L))
        assertTrue(player.liftingDoorSettledAt(DoorScores.Lifting.DOOR_SETTLED))
        // Withdrawal 1 (heaviest) is housed by its filed housing time.
        assertEquals(1f, player.liftingWithdrawalFractionAt(0, DoorScores.Lifting.HOUSED[0] + 5L), 0f)
        // Seal bolt 1 seats at its clunk onset.
        assertEquals(1f, player.sealBoltFractionAt(0, DoorScores.Seal.SYNC_CLUNKS[0] + 5L), 0f)
    }

    @Test
    fun `reduced motion - poses and cuts, onsets unscaled, stillness unchanged`() {
        assertEquals(640L, DoorReducedMotion.SWING_OPEN_TOTAL_MS)
        assertEquals(listOf(0f, 27f, 55f, 82f), DoorReducedMotion.SWING_OPEN_POSES_DEG)
        assertEquals(82f, DoorReducedMotion.swingOpenPoseAt(DoorReducedMotion.SWING_OPEN_TOTAL_MS), 0f)
        // Seat/snap onsets are the full profile's — the reduced profile changes smoothness, never rhythm.
        assertEquals(listOf(1160L, 1660L, 2340L), DoorScores.Seal.SYNC_CLUNKS)
        assertEquals(listOf(1440L, 1940L, 2440L), DoorScores.Lifting.SYNC_SNAPS)
    }

    @Test
    fun `tracker is critically damped - no overshoot past his hand, ever`() {
        assertEquals(1.0f, DoorBody.TRACKER.dampingRatio, 0.01f)
    }
}
