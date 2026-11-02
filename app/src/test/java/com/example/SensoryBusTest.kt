package com.example

import com.example.service.haptics.EffectiveHaptic
import com.example.service.haptics.HapticEvent
import com.example.service.haptics.MotionCue
import com.example.service.haptics.MotorClass
import com.example.service.haptics.MotorProfile
import com.example.service.haptics.SensoryBus
import com.example.service.haptics.SensoryRegistry
import com.example.service.haptics.SoundCue
import com.example.service.haptics.WaveformPlayer
import com.example.service.haptics.MotionPlayer
import com.example.service.haptics.SoundPlayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE BUS RULE (service/haptics; FEEL-SR-5): firing an event
 * through [SensoryBus] dispatches all three senses from the
 * event's ONE [SensoryRegistry] row, in the order haptic →
 * sound → motion; the haptics fact gates the felt leg only;
 * the motor profile is honoured at fire time; and a
 * silent-by-law event is a real firing whose dispatch is
 * three silences.
 */
class SensoryBusTest {

    private class Recording {
        val order = mutableListOf<String>()
        val haptics = mutableListOf<EffectiveHaptic>()
        val sounds = mutableListOf<SoundCue>()
        val motions = mutableListOf<MotionCue>()

        val waveformPlayer = object : WaveformPlayer {
            override fun play(effective: EffectiveHaptic, event: HapticEvent) {
                order += "haptic"
                haptics += effective
            }
        }
        val soundPlayer = object : SoundPlayer {
            override fun play(cue: SoundCue, event: HapticEvent) {
                order += "sound"
                sounds += cue
            }
        }
        val motionPlayer = object : MotionPlayer {
            override fun play(cue: MotionCue, event: HapticEvent) {
                order += "motion"
                motions += cue
            }
        }
    }

    private fun bus(
        rec: Recording,
        profile: MotorProfile = MotorProfile.FULL,
        hapticsEnabled: Boolean = true
    ) = SensoryBus(
        waveformPlayer = rec.waveformPlayer,
        soundPlayer = rec.soundPlayer,
        motionPlayer = rec.motionPlayer,
        profile = { profile },
        hapticsEnabled = { hapticsEnabled }
    )

    @Test
    fun fire_dispatchesAllThreeSensesFromTheOneRow_inOrder() {
        val rec = Recording()
        val dispatch = bus(rec).fire(HapticEvent.DRUM_LAND)
        val row = SensoryRegistry.row(HapticEvent.DRUM_LAND)
        assertEquals(listOf("haptic", "sound", "motion"), rec.order)
        assertEquals(row.sound, rec.sounds.single())
        assertEquals(row.motion, rec.motions.single())
        assertEquals(HapticEvent.DRUM_LAND, dispatch.event)
        assertTrue(dispatch.soundPlayed)
        assertTrue(dispatch.motionPlayed)
    }

    @Test
    fun fire_knock_soundsAndFeels_butMovesNothing() {
        val rec = Recording()
        val dispatch = bus(rec).fire(HapticEvent.KNOCK)
        assertTrue(rec.haptics.single() is EffectiveHaptic.PlayComposition)
        assertEquals(1, rec.sounds.size)
        assertTrue("the face does not move", rec.motions.isEmpty())
        assertFalse(dispatch.motionPlayed)
    }

    @Test
    fun fire_silentByLawEvent_isAFiringOfThreeSilences() {
        val rec = Recording()
        val dispatch = bus(rec).fire(HapticEvent.HELD_ARRIVAL)
        assertTrue(rec.haptics.single() is EffectiveHaptic.Silent)
        assertTrue(rec.sounds.isEmpty())
        assertTrue(rec.motions.isEmpty())
        assertFalse(dispatch.soundPlayed)
        assertFalse(dispatch.motionPlayed)
    }

    @Test
    fun hapticsFactOff_silencesTheFeltLeg_only() {
        val rec = Recording()
        val dispatch = bus(rec, hapticsEnabled = false).fire(HapticEvent.KNOCK)
        assertTrue(dispatch.haptic is EffectiveHaptic.Silent)
        assertTrue("the event stands: its sound still fires", dispatch.soundPlayed)
        assertEquals(1, rec.sounds.size)
    }

    @Test
    fun motorProfile_isHonouredAtFireTime() {
        val rec = Recording()
        bus(rec, profile = MotorProfile(MotorClass.LEGACY_ONESHOT))
            .fire(HapticEvent.KNOCK)
        assertEquals(EffectiveHaptic.PlayOneShot(42L), rec.haptics.single())

        val rec2 = Recording()
        bus(rec2, profile = MotorProfile(MotorClass.FIXED_AMPLITUDE))
            .fire(HapticEvent.LATCH_ENGAGE)
        val fixed = rec2.haptics.single() as EffectiveHaptic.PlayWaveform
        // The latch's gap survives the fixed-amplitude encoding.
        assertEquals(26L, fixed.timingsMs[2])
        assertEquals(0, fixed.amplitudes[2])
    }

    @Test
    fun everyEvent_firesCleanly_andReportsItself() {
        val rec = Recording()
        val b = bus(rec)
        HapticEvent.entries.forEach { event ->
            val dispatch = b.fire(event)
            assertEquals(event, dispatch.event)
            assertEquals(SensoryRegistry.row(event).sound, dispatch.sound)
            assertEquals(SensoryRegistry.row(event).motion, dispatch.motion)
        }
        assertEquals(HapticEvent.entries.size, rec.haptics.size)
    }
}
