package com.example

import com.example.service.haptics.EffectiveHaptic
import com.example.service.haptics.HapticEvent
import com.example.service.haptics.HapticPrimitives
import com.example.service.haptics.MotorClass
import com.example.service.haptics.MotorProfile
import com.example.service.haptics.SensoryRegistry
import com.example.service.haptics.degrade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PROFILE C · ERM-LONG (FEEL-SR-1 §5.4; added as a degrade class
 * by BUILD-SR-20, closing FEEL-SR-5's named gap 1): a slow-attack
 * ERM motor renders voices STRETCHED — the door rows carry
 * FEEL-SR-1's verbatim stretched arrays, other voices derive the
 * ×1.4 stretch with ratios preserved and the 1.5 s cap held, a
 * designed silence stays silent, and a resistance stop degrades
 * toward silence (a short seat), never toward a stretched buzz.
 * All figures UNPROVEN-ON-DEVICE.
 */
class HapticsErmLongTest {

    private val erm = MotorProfile(MotorClass.ERM_LONG)

    private fun waveformOf(event: HapticEvent) = SensoryRegistry.row(event).haptic

    @Test
    fun ermLong_doorSeal_isFeelsSr1sVerbatim1426() {
        val out = degrade(waveformOf(HapticEvent.DOOR_SEAL), erm)
        assertTrue(out is EffectiveHaptic.PlayWaveform)
        out as EffectiveHaptic.PlayWaveform
        assertEquals(1426L, out.timingsMs.sum())
        assertEquals(
            listOf(0L, 80L, 80L, 80L, 80L, 80L, 80L, 80L, 80L, 80L, 100L, 24L, 44L, 150L, 28L, 56L, 160L, 36L, 72L, 36L),
            out.timingsMs
        )
        // Mass still ascends across the three clunks on ERM.
        assertEquals(235, out.amplitudes[11])
        assertEquals(250, out.amplitudes[14])
        assertEquals(255, out.amplitudes[17])
    }

    @Test
    fun ermLong_doorLift_isFeelsSr1sVerbatim1486() {
        val out = degrade(waveformOf(HapticEvent.DOOR_LIFT), erm)
        assertTrue(out is EffectiveHaptic.PlayWaveform)
        out as EffectiveHaptic.PlayWaveform
        assertEquals(1486L, out.timingsMs.sum())
        assertEquals(110L, out.timingsMs.last())
    }

    @Test
    fun ermLong_genericVoice_stretchesByPointFour_ratiosPreserved() {
        val tick = HapticPrimitives.tick(amplitude = 180, strikeMs = 10L, tailMs = 20L)
        val out = degrade(tick, erm)
        assertTrue(out is EffectiveHaptic.PlayWaveform)
        out as EffectiveHaptic.PlayWaveform
        assertEquals(listOf(0L, 14L, 28L), out.timingsMs)
        assertEquals(tick.amplitudes, out.amplitudes)
    }

    @Test
    fun ermLong_longVoice_totalNeverExceeds1500() {
        val out = degrade(waveformOf(HapticEvent.STRETCH_SCRAPE), erm)
        if (out is EffectiveHaptic.PlayWaveform) {
            assertTrue(out.timingsMs.sum() <= 1500L)
        }
    }

    @Test
    fun ermLong_resistanceStop_isShortSeat_neverBuzz() {
        val stop = HapticPrimitives.resistanceStop()
        val out = degrade(stop, erm)
        assertTrue(out is EffectiveHaptic.PlayOneShot)
        out as EffectiveHaptic.PlayOneShot
        assertTrue(out.durationMs <= 40L)
    }

    @Test
    fun ermLong_silentByLaw_staysSilent() {
        val out = degrade(waveformOf(HapticEvent.HELD_ARRIVAL), erm)
        assertTrue(out is EffectiveHaptic.Silent)
    }
}
