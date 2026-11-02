package com.example

import androidx.compose.ui.graphics.Color
import com.example.service.StrongroomKnock
import com.example.ui.theme.InkViolet
import com.example.ui.theme.Relight
import com.example.ui.theme.RelightState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE RELIGHT RULE and THE KNOCK — two contracts in one file.
 *
 * RELIGHT — RE-AIMED AT COMPOSITION (BUILD-SR-13, Ruling R2-6):
 * this file originally asserted the faces lane's divergent tuning
 * table (ReliefLight.kt: FaceLightState strengths, SEALED
 * strongest at >= 0.8). That table is DELETED at composition —
 * P0's Relight.kt is the canonical mechanism — and the relief
 * tests below now assert P0's law instead: the lit side of a mark
 * never darkens it and the shadow side never lightens it, in every
 * state; DARK is subtraction-only (its lit side is the mark
 * itself, unchanged); SEALED's warm lit blend is capped at 1/8 —
 * the descended, poorest-lit state, the exact opposite of the
 * deleted table's claim. The re-aim is named in the composition
 * record (work/build-sr-13/composition-record.md).
 *
 * THE KNOCK (service/StrongroomKnock.kt; Audit A §2.3): one dull
 * heavy thud, felt more than heard — its pitch sits below every
 * other engine voice (240–360 Hz family), its body inside the
 * 160 ms transient law, its gain under the seal's 0.62; its
 * waveform is ONE mass strike: a silent lead-in, a full-amplitude
 * blow, then amplitudes that only fall.
 */
class StrongroomReliefAndKnockTest {

    private val violet: Color = InkViolet

    @Test
    fun relief_litSideNeverDarkens_shadowSideNeverLightens_inEveryState() {
        val base = Relight.luminance(violet)
        RelightState.values().forEach { state ->
            val lit = Relight.relief(violet, violet, 1f, state)
            val shadow = Relight.relief(violet, violet, -1f, state)
            assertTrue(
                "$state lit side must not fall below the mark",
                Relight.luminance(lit) >= base - 0.0001f
            )
            assertTrue(
                "$state shadow side must not rise above the mark",
                Relight.luminance(shadow) <= base + 0.0001f
            )
        }
    }

    @Test
    fun relief_darkIsSubtractionOnly_andSealedLitCapIsOneEighth() {
        // DARK: no warm partner, subtraction only — the lit side
        // is the mark itself, unchanged.
        assertEquals(violet, Relight.relief(violet, violet, 1f, RelightState.DARK))
        assertEquals(violet, Relight.relief(violet, violet, 1f, RelightState.DAY))
        // SEALED is the descended state: its lit cap is 1/8, the
        // smallest non-zero cap in the table (P0 Relight.kt).
        val caps = RelightState.values().map { Relight.paramsFor(it).maxLitEighths }
        assertEquals(1, Relight.paramsFor(RelightState.SEALED).maxLitEighths)
        assertTrue(caps.all { it == 0 || it >= 1 })
        assertEquals(8, Relight.paramsFor(RelightState.SIGNING).maxLitEighths)
    }

    @Test
    fun relief_neverLeavesTheCube_atTheExtremes() {
        listOf(Color.Black, Color.White, violet).forEach { base ->
            RelightState.values().forEach { state ->
                listOf(
                    Relight.relief(base, base, 1f, state),
                    Relight.relief(base, base, -1f, state)
                ).forEach { c ->
                    assertTrue(c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f)
                }
            }
        }
    }

    @Test
    fun knock_isTheLowestVoice_andUnderTheSeal() {
        assertTrue(StrongroomKnock.PITCH_HZ < 240f)
        assertTrue(StrongroomKnock.PITCH_HZ < 360f)
        assertTrue(StrongroomKnock.GAIN < 0.62f)
        assertTrue(StrongroomKnock.GAIN > 0f)
        assertTrue(StrongroomKnock.DURATION_MS in 1..160)
    }

    @Test
    fun knock_waveformIsOneMassStrike_thatOnlySettles() {
        val timings = StrongroomKnock.HAPTIC_TIMINGS_MS
        val amps = StrongroomKnock.HAPTIC_AMPLITUDES
        assertEquals(timings.size, amps.size)
        // A silent lead-in, then the blow at full amplitude.
        assertEquals(0L, timings[0])
        assertEquals(0, amps[0])
        assertEquals(255, amps[1])
        // After the blow the hand only settles: strictly falling.
        for (i in 2 until amps.size) {
            assertTrue(
                "amplitudes must only fall after the blow",
                amps[i] < amps[i - 1]
            )
        }
        // The whole felt event is shorter than the heard body —
        // felt first, and finished first.
        assertTrue(timings.sum() < StrongroomKnock.DURATION_MS)
        assertEquals(1.0f, StrongroomKnock.THUD_PRIMITIVE_SCALE, 0f)
    }
}
