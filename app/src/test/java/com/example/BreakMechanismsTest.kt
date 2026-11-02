package com.example

import com.example.domain.StrongroomDay
import com.example.ui.`break`.BoltEase
import com.example.ui.`break`.BreakClock
import com.example.ui.`break`.BreakInterior
import com.example.ui.`break`.BreakPhase
import com.example.ui.`break`.BreakPhases
import com.example.ui.`break`.BreakRedriveScore
import com.example.ui.`break`.BreakTexture
import com.example.ui.`break`.DoorEase
import com.example.ui.`break`.Escapement
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

/**
 * BUILD-SR-39 covering tests: THE BREAK's mechanisms (census
 * C-16) — the firing's clock, its phases, the escapement's
 * stepped motion, the door's ease, the bolts' ease and re-drive,
 * the interior's reach, and the texture law. The slice's claims
 * are pinned here; the venue's Targeted job runs them.
 */
class BreakMechanismsTest {

    private fun clock(totalMs: Long = 1_800_000L, remainingMs: Long = 1_800_000L) =
        BreakClock(totalMs, remainingMs, anchorElapsedMillis = 1_000_000L)

    private fun signing(
        allowed: List<String> = listOf("app.a", "app.b", "app.locked"),
        locked: Set<String> = setOf("app.locked")
    ) = StrongroomDay.SigningRecord(
        filingDateIso = "2026-08-31",
        lockedPackages = locked,
        allowedPackages = allowed,
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = 30,
        breakFiresAtOffsetMillis = 3_600_000L,
        allowanceMinutes = 30,
        gaps = emptyList()
    )

    // ── The clock: a read-only derivation over the firing ──────

    @Test
    fun clock_spendsOnlyByElapsedTime() {
        val c = clock()
        assertEquals(1_800_000L, c.remainingAt(1_000_000L))
        assertEquals(1_200_000L, c.remainingAt(1_600_000L))
        assertEquals(0L, c.remainingAt(2_800_000L))
        assertEquals(0L, c.remainingAt(9_999_999L))
    }

    @Test
    fun clock_fromFiring_onlyWhileRunning() {
        val seal = StrongroomDay.SealSpan(
            sealedAtElapsedMillis = 0L, sealedBootCount = 1,
            spanDurationMillis = 10L * 3_600_000L
        )
        val firing = StrongroomDay.breakFiring(signing(), seal)!!
        assertNull(BreakClock.fromFiring(firing, 3_599_999L))
        val running = BreakClock.fromFiring(firing, 3_600_000L)
        assertTrue(running != null)
        assertEquals(1_800_000L, running!!.totalMillis)
        assertNull(BreakClock.fromFiring(firing, firing.endsAtElapsedMillis))
    }

    @Test
    fun clock_fromSession_requiresARunningBreak() {
        assertNull(BreakClock.fromSession(600, 0, 0L))
        assertNull(BreakClock.fromSession(0, 10, 0L))
        val c = BreakClock.fromSession(600, 300, 5_000L)!!
        assertEquals(600_000L, c.totalMillis)
        assertEquals(300_000L, c.remainingAt(5_000L))
    }

    @Test
    fun clock_noBreakPriced_noClock() {
        val seal = StrongroomDay.SealSpan(0L, 1, 10L * 3_600_000L)
        val noBreak = signing().copy(breakFiresAtOffsetMillis = -1L)
        assertNull(StrongroomDay.breakFiring(noBreak, seal))
    }

    // ── Phases ──────────────────────────────────────────────────

    @Test
    fun phases_runInOrder_andSpentIsTerminal() {
        val c = clock()
        assertEquals(BreakPhase.EASING, BreakPhases.phaseAt(c, 1_000_000L))
        assertEquals(BreakPhase.RUNNING, BreakPhases.phaseAt(c, 1_000_000L + 60_000L))
        assertEquals(
            BreakPhase.REDRIVING,
            BreakPhases.phaseAt(c, 1_000_000L + 1_800_000L - 1_000L)
        )
        assertEquals(BreakPhase.SPENT, BreakPhases.phaseAt(c, 1_000_000L + 1_800_000L))
    }

    @Test
    fun phases_shortSpan_windowsCompress_neverOverlap() {
        // A freely chosen 1-minute break (D-6): windows are a
        // quarter of the span each, phases still all stand.
        val c = clock(totalMs = 60_000L, remainingMs = 60_000L)
        val (ease, redrive) = BreakPhases.windows(60_000L)
        assertEquals(15_000L, ease)
        assertEquals(15_000L, redrive)
        assertEquals(BreakPhase.EASING, BreakPhases.phaseAt(c, 1_000_000L))
        assertEquals(BreakPhase.RUNNING, BreakPhases.phaseAt(c, 1_000_000L + 30_000L))
        assertEquals(BreakPhase.REDRIVING, BreakPhases.phaseAt(c, 1_000_000L + 50_000L))
    }

    // ── The escapement ──────────────────────────────────────────

    @Test
    fun escapement_angleNeverRecedesPastTheRecoil() {
        // The wheel's only backward motion is the recoil itself:
        // never more than the filed overshoot, and the span's
        // ends are exact.
        val recoilDeg = Escapement.RECOIL_OVERSHOOT / Escapement.TEETH * 360.0
        var last = 0.0
        var p = 0.0
        while (p <= 1.0) {
            val a = Escapement.wheelAngleDeg(p)
            assertTrue(
                "angle receded past the recoil at p=$p",
                a >= last - recoilDeg - 1e-9
            )
            last = a
            p += 0.001
        }
        assertEquals(360.0, Escapement.wheelAngleDeg(1.0), 1e-9)
        assertEquals(0.0, Escapement.wheelAngleDeg(0.0), 1e-9)
    }

    @Test
    fun escapement_locksBetweenReleases() {
        // Inside one tooth's locked stretch the wheel does not
        // move at all: two readings in the lock read identically.
        val tooth = 5
        val base = tooth.toDouble() / Escapement.TEETH
        val a = Escapement.wheelAngleDeg(base + 0.70 / Escapement.TEETH)
        val b = Escapement.wheelAngleDeg(base + 0.95 / Escapement.TEETH)
        assertEquals(a, b, 1e-9)
    }

    @Test
    fun escapement_recoilNeverPassesTheSettledTooth() {
        // The recoil returns TO the tooth being landed on, never
        // behind the previous one: at every point of tooth k's
        // interval the angle is ≥ k teeth of travel.
        for (k in 0 until Escapement.TEETH) {
            var within = 0.0
            while (within < 1.0) {
                val p = (k + within) / Escapement.TEETH
                val a = Escapement.wheelAngleDeg(p)
                assertTrue(
                    "recoil behind settled tooth $k at within=$within",
                    a >= k * (360.0 / Escapement.TEETH) - 1e-9
                )
                within += 0.05
            }
        }
    }

    @Test
    fun escapement_remainingArcIsTheSpan() {
        assertEquals(360.0, Escapement.remainingArcDeg(1.0), 1e-9)
        assertEquals(180.0, Escapement.remainingArcDeg(0.5), 1e-9)
        assertEquals(0.0, Escapement.remainingArcDeg(0.0), 1e-9)
        assertEquals(24, Escapement.teethPassed(0.5))
    }

    @Test
    fun escapement_palletRocksWithinItsSwing() {
        var p = 0.0
        while (p <= 1.0) {
            val a = Escapement.palletAngleDeg(p)
            assertTrue(
                kotlin.math.abs(a) <= Escapement.PALLET_SWING_DEG + 1e-9
            )
            p += 0.002
        }
    }

    // ── The door's ease + the bolts ─────────────────────────────

    @Test
    fun ease_risesStandsAndComesOff_andIsZeroWhenSpent() {
        val c = clock()
        assertEquals(0.0, DoorEase.easeFraction(c, 1_000_000L), 1e-9)
        val mid = DoorEase.easeFraction(c, 1_000_000L + 60_000L)
        assertEquals(1.0, mid, 0.05)
        val off = DoorEase.easeFraction(c, 1_000_000L + 1_800_000L - 100L)
        assertTrue("ease must be coming off in the re-drive", off < 1.0)
        assertEquals(0.0, DoorEase.easeFraction(c, 1_000_000L + 1_800_000L), 1e-9)
        assertTrue(DoorEase.MAX_EASE_DEG < 82.0) // an ease, never a swing
    }

    @Test
    fun bolts_neverWithdrawDuringTheBreak() {
        val c = clock()
        var now = 1_000_000L
        while (now <= 1_000_000L + 1_800_000L) {
            val eased = BoltEase.easedFraction(c, now)
            assertTrue(
                "bolt ease $eased exceeds its filed share",
                eased <= BoltEase.MAX_EASE_FRACTION + 1e-9
            )
            assertTrue(eased >= 0.0)
            now += 10_000L
        }
    }

    @Test
    fun bolts_redriveHomeInSeatOrder_onTheScore() {
        // In the re-drive each bolt reaches home at its clunk,
        // in seat order, and all stand home by the score's end.
        val total = 1_800_000L
        val c = clock(totalMs = total, remainingMs = total)
        val redriveStart = 1_000_000L + total - BreakPhases.windows(total).second
        for (i in 0 until 3) {
            val clunk = BreakRedriveScore.Points.first { it.name == "clunk-${i + 1}" }.atMs
            assertTrue(BoltEase.boltSeatedInRedrive(i, clunk))
            val home = BoltEase.homeFraction(
                i, c, redriveStart + clunk + BoltBodiesTravelMarginMs
            )
            assertEquals("bolt $i home at its clunk", 1.0, home, 0.02)
        }
        assertTrue(
            BreakRedriveScore.BoltDriveStartMs.zipWithNext().all { (a, b) -> a < b }
        )
        assertEquals(
            listOf(0.64, 0.82, 1.0), BreakRedriveScore.ClunkWeights
        )
    }

    // ── The interior ────────────────────────────────────────────

    @Test
    fun interior_withinReach_isTheSignedStock_inSignedOrder() {
        val reach = BreakInterior.withinReach(signing())
        assertEquals(listOf("app.a", "app.b"), reach)
    }

    @Test
    fun interior_sectionsStandInTheRoomsOrder() {
        assertEquals(
            listOf(
                BreakInterior.Section.WITHIN_REACH,
                BreakInterior.Section.HELD_PILE
            ),
            BreakInterior.SECTION_ORDER
        )
    }

    // ── Texture law ─────────────────────────────────────────────

    @Test
    fun texture_isDeterministicAtAPosition() {
        repeat(50) {
            assertEquals(
                BreakTexture.grainAt(3, 7, seed = 59),
                BreakTexture.grainAt(3, 7, seed = 59)
            )
        }
        val values = (0..24).map { BreakTexture.grainAt(it, it * 2, seed = 59) }.toSet()
        assertTrue("grain must not be a flat field", values.size > 8)
    }

    @Test
    fun texture_tonesAreOnlyEighthsBlendsTowardRelightPartners() {
        val state = StrongroomLightState.SEALED
        for (x in 0..12) for (y in 0..12) {
            val tone = BreakTexture.cellTone(
                StrongroomInks.Gold, StrongroomInks.Graphite,
                BreakTexture.Species.BRASS, x, y, seed = 59, state = state
            )
            val lit = StrongroomLight.litPartner(StrongroomInks.Gold, StrongroomInks.Graphite, state)
            val shadow = StrongroomLight.shadowPartner(StrongroomInks.Gold)
            val lawful = (0..8).any {
                StrongroomLight.blendEighth(StrongroomInks.Gold, lit, it) == tone ||
                    StrongroomLight.blendEighth(StrongroomInks.Gold, shadow, it) == tone
            }
            assertTrue("tone at $x,$y must be an eighths blend toward a partner", lawful)
        }
    }

    private companion object {
        /** A breath past a bolt's clunk for its travel home. */
        const val BoltBodiesTravelMarginMs: Long = 300L
    }
}
