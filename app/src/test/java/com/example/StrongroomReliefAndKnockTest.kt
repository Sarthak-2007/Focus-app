package com.example

import com.example.service.StrongroomKnock
import com.example.ui.theme.FaceLightState
import com.example.ui.theme.ReliefLight
import com.example.ui.theme.Srgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE RELIGHT RULE and THE KNOCK — two contracts in one file,
 * each stated against its filing:
 *
 * RELIGHT (ui/theme/ReliefLight.kt; Audit A §4 KEPT mechanism,
 * G-5 tuning): relief is derived from the surface's OWN luminance
 * — for the kept violet ground the highlight's luminance stands
 * above the ground's and the shade's below it, in EVERY state;
 * the four states carry four distinct strengths; the sealed home
 * state visibly carries the relief (strength ≥ 0.8); and the
 * arithmetic never leaves the 0..1 cube, at black or white either.
 *
 * THE KNOCK (service/StrongroomKnock.kt; Audit A §2.3): one dull
 * heavy thud, felt more than heard — its pitch sits below every
 * other engine voice (240–360 Hz family), its body inside the
 * 160 ms transient law, its gain under the seal's 0.62; its
 * waveform is ONE mass strike: a silent lead-in, a full-amplitude
 * blow, then amplitudes that only fall.
 */
class StrongroomReliefAndKnockTest {

    private val violet = Srgb(0x49 / 255f, 0x3E / 255f, 0xA4 / 255f)

    @Test
    fun relief_bracketsTheGroundsOwnLuminance_inEveryState() {
        val base = violet.luminance()
        FaceLightState.values().forEach { state ->
            val (highlight, shade) = ReliefLight.reliefPair(violet, state)
            assertTrue(
                "$state highlight must rise above the ground",
                highlight.luminance() > base
            )
            assertTrue(
                "$state shade must fall below the ground",
                shade.luminance() < base
            )
        }
    }

    @Test
    fun relief_statesAreDistinct_andTheSealedStateCarriesIt() {
        val strengths = FaceLightState.values().map { it.strength }
        assertEquals(strengths.size, strengths.toSet().size)
        assertTrue(
            "G-5: the sealed home state must visibly carry the relief",
            FaceLightState.SEALED.strength >= 0.8f
        )
        // The dark room's relief is the faintest — present, never performed.
        assertEquals(
            FaceLightState.DARK,
            FaceLightState.values().minByOrNull { it.strength }
        )
    }

    @Test
    fun relief_neverLeavesTheCube_atTheExtremes() {
        listOf(Srgb(0f, 0f, 0f), Srgb(1f, 1f, 1f), violet).forEach { base ->
            FaceLightState.values().forEach { state ->
                val (h, s) = ReliefLight.reliefPair(base, state)
                listOf(h, s).forEach { c ->
                    assertTrue(c.r in 0f..1f && c.g in 0f..1f && c.b in 0f..1f)
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
