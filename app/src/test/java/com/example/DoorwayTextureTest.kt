package com.example

import androidx.compose.ui.graphics.Color
import com.example.ui.doorway.DoorwaySwing
import com.example.ui.doorway.DoorwayTexture
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-35 covering tests: the Doorway's texture law and its
 * entry swing, read off the foundation's own primitives.
 *
 * The texture law under test (intake §1): grain is a property of
 * the surface itself — deterministic at a position, and every
 * tone it produces is the surface ink's own RELIGHT partner moved
 * by whole eighths within the species' cap. No eighth ink can
 * appear: a tone produced here must always sit exactly on an
 * eighths blend between the ink and one of its two partners.
 */
class DoorwayTextureTest {

    @Test
    fun grain_isDeterministicAtAPosition() {
        repeat(50) {
            val a = DoorwayTexture.grainAt(3, 7, seed = 11)
            val b = DoorwayTexture.grainAt(3, 7, seed = 11)
            assertEquals(a, b)
        }
    }

    @Test
    fun grain_variesAcrossTheSurface() {
        val values = (0..24).map { DoorwayTexture.grainAt(it, it * 2, seed = 11) }.toSet()
        assertTrue("grain must not be a flat field", values.size > 8)
    }

    @Test
    fun grain_staysInUnitRange() {
        for (x in 0..40) for (y in 0..40) {
            val g = DoorwayTexture.grainAt(x, y, seed = 23)
            assertTrue(g >= 0f && g <= 1f)
        }
    }

    private fun isEighthsBlendBetween(tone: Color, from: Color, to: Color): Boolean =
        (0..8).any { StrongroomLight.blendEighth(from, to, it) == tone }

    @Test
    fun enamelTones_areOnlyTheInksOwnEighthsPartners() {
        val ink = StrongroomInks.Violet
        val state = StrongroomLightState.SIGNING
        val lit = StrongroomLight.litPartner(ink, StrongroomInks.Graphite, state)
        val shadow = StrongroomLight.shadowPartner(ink)
        for (x in 0..30) for (y in 0..30) {
            val tone = DoorwayTexture.cellTone(
                ink, StrongroomInks.Graphite,
                DoorwayTexture.Species.ENAMEL, x, y, seed = 11, state = state
            )
            assertTrue(
                "tone $tone at $x,$y is not an eighths blend of the enamel toward its partners",
                isEighthsBlendBetween(tone, ink, lit) || isEighthsBlendBetween(tone, ink, shadow)
            )
        }
    }

    @Test
    fun brassTones_neverLeaveTheSevenInksDerivations() {
        val ink = StrongroomInks.Gold
        val state = StrongroomLightState.SIGNING
        val lit = StrongroomLight.litPartner(ink, StrongroomInks.Graphite, state)
        val shadow = StrongroomLight.shadowPartner(ink)
        for (x in 0..30) for (y in 0..30) {
            val tone = DoorwayTexture.cellTone(
                ink, StrongroomInks.Graphite,
                DoorwayTexture.Species.BRASS, x, y, seed = 23, state = state
            )
            assertTrue(
                isEighthsBlendBetween(tone, ink, lit) || isEighthsBlendBetween(tone, ink, shadow)
            )
            assertTrue(StrongroomInkLawNeverGreen(tone))
        }
    }

    private fun StrongroomInkLawNeverGreen(c: Color): Boolean =
        !(c.green > c.red && c.green > c.blue)

    @Test
    fun swing_startsAtTheEndStopEngagement_andSettlesAtTheFiledRest() {
        assertTrue(DoorwaySwing.DurationMs > 0)
        val start = DoorwaySwing.angleAt(0)
        assertTrue("entry starts at the end-stop band, was $start", start >= 73.0)
        val rest = DoorwaySwing.angleAt(DoorwaySwing.DurationMs)
        assertEquals(82.09, rest, 0.35)
    }

    @Test
    fun swing_isMonotonic_weightDoesNotLeapBack() {
        var prev = DoorwaySwing.angleAt(0)
        var ms = 10
        while (ms <= DoorwaySwing.DurationMs) {
            val a = DoorwaySwing.angleAt(ms)
            assertTrue("angle fell back at ${ms}ms: $prev -> $a", a >= prev - 0.001)
            prev = a
            ms += 10
        }
    }
}
