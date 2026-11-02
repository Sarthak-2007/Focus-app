package com.example

import com.example.service.haptics.AndroidWaveformPlayer
import com.example.service.haptics.EffectiveHaptic
import com.example.service.haptics.HapticEvent
import com.example.service.haptics.MotorClass
import com.example.service.haptics.MotorProfile
import com.example.service.haptics.PrimitiveKind
import com.example.service.haptics.SensoryRegistry
import com.example.service.haptics.degrade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AMPLITUDE HONESTY (service/haptics; FEEL-SR-5): the
 * [MotorProfile] classification table ([AndroidWaveformPlayer]
 * .classify) and the pure [degrade] law — the temporal
 * skeleton survives every motor class, amplitude is only ever
 * scaled down or encoded as duration, a designed silence is
 * silent everywhere, and a resistance stop degrades toward
 * silence, never toward a buzz, on any class.
 */
class HapticsDegradationTest {

    private fun waveformOf(event: HapticEvent) = SensoryRegistry.row(event).haptic

    @Test
    fun classify_readsTheBoundaryTableHonestly() {
        assertEquals(
            MotorClass.COMPOSITION,
            AndroidWaveformPlayer.classify(35, true, true, primitivesSupported = true).motorClass
        )
        assertEquals(
            MotorClass.COMPOSITION,
            AndroidWaveformPlayer.classify(30, true, false, primitivesSupported = true).motorClass
        )
        assertEquals(
            MotorClass.AMPLITUDE_WAVEFORM,
            AndroidWaveformPlayer.classify(29, true, true, primitivesSupported = false).motorClass
        )
        assertEquals(
            MotorClass.FIXED_AMPLITUDE,
            AndroidWaveformPlayer.classify(29, true, false, primitivesSupported = false).motorClass
        )
        assertEquals(
            MotorClass.LEGACY_ONESHOT,
            AndroidWaveformPlayer.classify(25, true, false, primitivesSupported = false).motorClass
        )
        assertEquals(
            MotorClass.NO_MOTOR,
            AndroidWaveformPlayer.classify(35, false, true, primitivesSupported = true).motorClass
        )
    }

    @Test
    fun designedSilence_isSilentOnEveryMotorClass() {
        val silence = waveformOf(HapticEvent.HELD_ARRIVAL)
        MotorClass.entries.forEach { motorClass ->
            val effective = degrade(silence, MotorProfile(motorClass))
            assertTrue("$motorClass", effective is EffectiveHaptic.Silent)
        }
    }

    @Test
    fun noMotor_silencesTheFeltLeg() {
        val effective = degrade(waveformOf(HapticEvent.KNOCK), MotorProfile.NONE)
        assertTrue(effective is EffectiveHaptic.Silent)
    }

    @Test
    fun compositionClass_playsTheKnocksThudPrimitive() {
        val effective = degrade(waveformOf(HapticEvent.KNOCK), MotorProfile.FULL)
        assertTrue(effective is EffectiveHaptic.PlayComposition)
        val segments = (effective as EffectiveHaptic.PlayComposition).segments
        assertEquals(1, segments.size)
        assertEquals(PrimitiveKind.THUD, segments[0].kind)
        assertEquals(1.0f, segments[0].scale)
    }

    @Test
    fun compositionClass_withoutPrimitives_playsTheWaveformVerbatim() {
        val seal = waveformOf(HapticEvent.DOOR_SEAL)
        val effective = degrade(seal, MotorProfile.FULL)
        assertTrue(effective is EffectiveHaptic.PlayWaveform)
        effective as EffectiveHaptic.PlayWaveform
        assertEquals(seal.timingsMs, effective.timingsMs)
        assertEquals(seal.amplitudes, effective.amplitudes)
    }

    @Test
    fun amplitudeCeiling_scalesDownOnly_andKeepsSilenceSilent() {
        val knock = waveformOf(HapticEvent.KNOCK)
        val profile = MotorProfile(MotorClass.AMPLITUDE_WAVEFORM, amplitudeCeiling = 128)
        val effective = degrade(knock, profile) as EffectiveHaptic.PlayWaveform
        assertEquals(knock.timingsMs, effective.timingsMs)
        assertTrue(effective.amplitudes.all { it in 0..128 })
        assertEquals(128, effective.amplitudes.max())
        assertEquals(0, effective.amplitudes[0])
        // Ratios survive the scaling (the knock still falls).
        assertTrue(effective.amplitudes[1] > effective.amplitudes[2])
        assertTrue(effective.amplitudes[2] > effective.amplitudes[3])
    }

    @Test
    fun fixedAmplitude_encodesAmplitudeAsDuration_andPreservesTheSkeleton() {
        SensoryRegistry.all().forEach { row ->
            if (row.haptic.isSilent) return@forEach
            val effective = degrade(
                row.haptic, MotorProfile(MotorClass.FIXED_AMPLITUDE)
            ) as EffectiveHaptic.PlayWaveform
            // Same skeleton: same segment count, gaps exactly
            // where the spec put them.
            assertEquals("${row.event}", row.haptic.timingsMs.size, effective.timingsMs.size)
            row.haptic.amplitudes.forEachIndexed { i, amp ->
                if (amp == 0) {
                    assertEquals("${row.event} gap @$i moved", row.haptic.timingsMs[i], effective.timingsMs[i])
                    assertEquals(0, effective.amplitudes[i])
                } else {
                    assertEquals("${row.event} segment @$i", -1, effective.amplitudes[i])
                }
            }
        }
        // And on the knock, stronger still means longer.
        val knock = degrade(
            waveformOf(HapticEvent.KNOCK), MotorProfile(MotorClass.FIXED_AMPLITUDE)
        ) as EffectiveHaptic.PlayWaveform
        assertTrue(knock.timingsMs[1] > knock.timingsMs[2])
        assertTrue(knock.timingsMs[2] > knock.timingsMs[3])
    }

    @Test
    fun legacy_spendsEachVoicesOwnFallback() {
        val legacy = MotorProfile(MotorClass.LEGACY_ONESHOT)
        assertEquals(
            EffectiveHaptic.PlayOneShot(42L),
            degrade(waveformOf(HapticEvent.KNOCK), legacy)
        )
        assertEquals(
            EffectiveHaptic.PlayOneShot(90L),
            degrade(waveformOf(HapticEvent.DOOR_SEAL), legacy)
        )
        assertEquals(
            EffectiveHaptic.PlayOneShot(1L),
            degrade(waveformOf(HapticEvent.ROTARY_DETENT), legacy)
        )
    }

    @Test
    fun resistance_degradesTowardSilence_neverTowardABuzz_onEveryClass() {
        listOf(HapticEvent.DRUM_HARD_STOP, HapticEvent.GOVERNOR_HARD_STOP).forEach { event ->
            val waveform = waveformOf(event)
            MotorClass.entries.forEach { motorClass ->
                when (val effective = degrade(waveform, MotorProfile(motorClass))) {
                    is EffectiveHaptic.Silent -> Unit // honest
                    is EffectiveHaptic.PlayOneShot ->
                        assertTrue(
                            "$event on $motorClass buzzes",
                            effective.durationMs <= 40L
                        )
                    is EffectiveHaptic.PlayWaveform ->
                        assertTrue(
                            "$event on $motorClass stretched",
                            effective.timingsMs.sum() <= 160L
                        )
                    is EffectiveHaptic.PlayComposition ->
                        assertTrue(
                            "$event on $motorClass must not compose a buzz",
                            effective.segments.isEmpty() || waveform.composition.isNotEmpty()
                        )
                }
            }
        }
    }
}
