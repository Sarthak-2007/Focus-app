package com.example

import com.example.service.HapticSpec
import com.example.service.SoundEvent
import com.example.service.SoundFacts
import com.example.service.SoundPerformer
import com.example.service.StrongroomSoundEngine
import com.example.service.VoiceSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SOUND-SR-5: the playback layer's behaviour, driven through a
 * fake performer with a stepped clock — dispatch order (felt
 * first, on the caller's thread), the gate applied exactly once,
 * designed silences dispatching to nothing, legacy rows resolving
 * through the one table, and the latency ledger recording the
 * caller-thread path against the event's budget.
 */
class StrongroomSoundEngineTest {

    private class FakePerformer : SoundPerformer {
        val order = mutableListOf<String>()
        val transients = mutableListOf<Triple<VoiceSpec, Float, Boolean>>()
        val haptics = mutableListOf<HapticSpec>()
        val legacies = mutableListOf<Pair<String, Boolean>>()
        var warmUps = 0
        var throwInTransient = false

        override fun transient(voice: VoiceSpec, gainScale: Float, unattended: Boolean) {
            if (throwInTransient) throw RuntimeException("dead track")
            order += "audio"
            transients += Triple(voice, gainScale, unattended)
        }

        override fun haptic(spec: HapticSpec) {
            order += "haptic"
            haptics += spec
        }

        override fun legacy(performer: String, unattended: Boolean) {
            order += "legacy"
            legacies += performer to unattended
        }

        override fun warmUp() {
            warmUps++
        }
    }

    private fun facts(
        sound: Boolean = true,
        haptics: Boolean = true,
        ringer: Boolean = false,
        dnd: Boolean = false,
        night: Boolean = false
    ) = SoundFacts(sound, haptics, ringer, dnd, night)

    /** A clock that advances 2 ms per read — the dispatch path's
     *  segments come out at deterministic, budget-sized values. */
    private fun steppedClock(): () -> Long {
        var t = 0L
        return { t += 2_000_000L; t }
    }

    private fun engine(
        performer: FakePerformer,
        f: SoundFacts
    ) = StrongroomSoundEngine(performer, { f }, steppedClock())

    @Test
    fun knockFiresFeltFirstThenAudioAtFullDayGain() {
        val fake = FakePerformer()
        val record = engine(fake, facts()).dispatch(SoundEvent.KNOCK)
        assertEquals(listOf("haptic", "audio"), fake.order)
        assertEquals(1, fake.transients.size)
        // The performer receives the registry voice (0.52) and the
        // gate's scale (1.0 by day) as separate facts; the engine
        // hook multiplies them — one scaling authority.
        assertEquals(0.52f, fake.transients[0].first.gain, 0.001f)
        assertEquals(1f, fake.transients[0].second, 0.001f)
        assertFalse(fake.transients[0].third) // attended
        assertTrue(record.hapticFired)
        assertEquals("day: full", record.law)
        assertTrue(
            "caller-thread path ${record.totalMicros}us over the 50ms budget",
            record.totalMicros <= 50_000
        )
    }

    @Test
    fun knockUnderRingerSilenceIsFeltOnly() {
        val fake = FakePerformer()
        val record = engine(fake, facts(ringer = true)).dispatch(SoundEvent.KNOCK)
        assertTrue(fake.transients.isEmpty())
        assertEquals(1, fake.haptics.size)
        assertTrue(record.hapticFired)
        assertEquals(0f, record.audioGainScale, 0.001f)
    }

    @Test
    fun ceremonyScalesOnceUnderDnd() {
        val fake = FakePerformer()
        engine(fake, facts(dnd = true)).dispatch(SoundEvent.SEAL_CLUNK_1)
        assertEquals(1, fake.transients.size)
        // Registry gain 0.62, gate scale 0.35 (ceremony under DND) —
        // applied once, at the performer's hook.
        assertEquals(0.62f, fake.transients[0].first.gain, 0.001f)
        assertEquals(0.35f, fake.transients[0].second, 0.001f)
    }

    @Test
    fun unattendedLiftingUnderDndIsFeltOnly() {
        val fake = FakePerformer()
        val record = engine(fake, facts(dnd = true)).dispatch(SoundEvent.LIFT_BOLT_1)
        assertTrue(fake.transients.isEmpty())
        assertEquals(1, fake.haptics.size)
        assertTrue(record.hapticFired)
    }

    @Test
    fun designedSilenceDispatchesToNothing() {
        val fake = FakePerformer()
        val e = engine(fake, facts())
        val record = e.dispatch(SoundEvent.HELD_CALL_ARRIVAL)
        assertTrue(fake.order.isEmpty())
        assertFalse(record.hapticFired)
        assertEquals("named-silence", record.law)
        // …and the dispatch is still recorded — silence is an act.
        assertEquals(1L, e.ledger.dispatchCount)
        assertEquals(record, e.ledger.lastFor(SoundEvent.HELD_CALL_ARRIVAL))
    }

    @Test
    fun hardStopIsFeltOnly() {
        val fake = FakePerformer()
        engine(fake, facts()).dispatch(SoundEvent.DRUM_SPENT_REFUSAL)
        assertEquals(listOf("haptic"), fake.order)
    }

    @Test
    fun legacyRowsResolveThroughTheTable() {
        val fake = FakePerformer()
        val e = engine(fake, facts())
        e.dispatch(SoundEvent.LEGACY_LATCH)
        e.dispatch(SoundEvent.PEEK_WARNING_TICK)
        e.dispatch(SoundEvent.SOUNDSCAPE)
        assertEquals(
            listOf(
                "playMechanicalLatch" to false,
                "playPeekWarningTick" to false,
                "startSoundscape" to false
            ),
            fake.legacies
        )
        assertTrue(fake.transients.isEmpty())
        assertTrue(fake.haptics.isEmpty()) // legacy waveforms live in the performers
    }

    @Test
    fun ledgerRecordsPerEventAndCounts() {
        val fake = FakePerformer()
        val e = engine(fake, facts())
        e.dispatch(SoundEvent.KNOCK)
        e.dispatch(SoundEvent.KNOCK)
        e.dispatch(SoundEvent.PLATE_STRIKE)
        assertEquals(3L, e.ledger.dispatchCount)
        assertEquals(
            setOf(SoundEvent.KNOCK, SoundEvent.PLATE_STRIKE),
            e.ledger.eventsSeen()
        )
        assertEquals(
            SoundEvent.PLATE_STRIKE,
            e.ledger.lastFor(SoundEvent.PLATE_STRIKE)?.event
        )
    }

    @Test
    fun performerFailureNeverTakesTheEventDown() {
        val fake = FakePerformer()
        fake.throwInTransient = true
        val record = engine(fake, facts()).dispatch(SoundEvent.KNOCK)
        assertTrue("the twin fired before the audio failure", record.hapticFired)
        assertEquals(1, fake.haptics.size)
    }

    @Test
    fun warmUpDelegatesToThePerformer() {
        val fake = FakePerformer()
        engine(fake, facts()).warmUp()
        assertEquals(1, fake.warmUps)
    }
}
