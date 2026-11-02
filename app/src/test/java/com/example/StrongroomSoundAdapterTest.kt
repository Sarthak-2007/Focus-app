package com.example

import com.example.service.SoundDecision
import com.example.service.SoundEvent
import com.example.service.SoundRegistry
import com.example.service.haptics.HapticEvent
import com.example.service.haptics.SensoryRegistry
import com.example.service.haptics.SoundCue
import com.example.service.haptics.StrongroomSoundAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BUILD-SR-19 — the sound adapter's contract, pinned off-device:
 * the R8-4 split (bus = events, voice table = voices behind the
 * adapter), silence by construction, and the SOUND-SR-1 figures
 * as they now stand in the voice table. Compile is the slice's
 * proof (R6); these tests travel to the end-phase list by name.
 */
class StrongroomSoundAdapterTest {

    private fun recordingAdapter(): Pair<StrongroomSoundAdapter, MutableList<SoundEvent>> {
        val dispatched = mutableListOf<SoundEvent>()
        return StrongroomSoundAdapter { voices -> dispatched += voices } to dispatched
    }

    @Test
    fun knockSpeaksTheKnockRowAt118Hz() {
        val (adapter, dispatched) = recordingAdapter()
        val row = SensoryRegistry.row(HapticEvent.KNOCK)
        adapter.play(row.sound, HapticEvent.KNOCK)
        assertEquals(listOf(SoundEvent.KNOCK), dispatched)
        assertEquals(118f, SoundRegistry.spec(SoundEvent.KNOCK).voice!!.pitchHz, 0.01f)
    }

    @Test
    fun silenceCueDispatchesNothing() {
        val (adapter, dispatched) = recordingAdapter()
        adapter.play(SoundCue.SILENCE, HapticEvent.KNOCK)
        adapter.play(SoundCue.SILENCE, HapticEvent.DOOR_SEAL)
        assertTrue(dispatched.isEmpty())
    }

    @Test
    fun doorSealSpeaksSwingThenThreeClunksInOrder() {
        val (adapter, dispatched) = recordingAdapter()
        val row = SensoryRegistry.row(HapticEvent.DOOR_SEAL)
        adapter.play(row.sound, HapticEvent.DOOR_SEAL)
        assertEquals(
            listOf(
                SoundEvent.DOOR_SWING_CLOSE,
                SoundEvent.SEAL_CLUNK_1,
                SoundEvent.SEAL_CLUNK_2,
                SoundEvent.SEAL_CLUNK_3
            ),
            dispatched
        )
    }

    @Test
    fun clunkFiguresAreSoundSr1DescendingPitchAscendingWeight() {
        val clunks = listOf(
            SoundEvent.SEAL_CLUNK_1,
            SoundEvent.SEAL_CLUNK_2,
            SoundEvent.SEAL_CLUNK_3
        ).map { SoundRegistry.spec(it).voice!! }
        assertEquals(listOf(191f, 143f, 97f), clunks.map { it.pitchHz })
        assertEquals(listOf(0.62f, 0.70f, 0.80f), clunks.map { it.gain })
        // SOUND-SR-1's timelines: the seal's gaps widen.
        assertEquals(
            listOf(0, 500, 1180),
            SoundRegistry.SEAL_SEQUENCE.map { it.second }
        )
    }

    @Test
    fun liftingFiguresAreTheMirrorSet() {
        val bolts = listOf(
            SoundEvent.LIFT_BOLT_1,
            SoundEvent.LIFT_BOLT_2,
            SoundEvent.LIFT_BOLT_3
        ).map { SoundRegistry.spec(it).voice!! }
        assertEquals(listOf(121f, 158f, 207f), bolts.map { it.pitchHz })
        assertEquals(listOf(0.50f, 0.46f, 0.42f), bolts.map { it.gain })
        assertEquals(
            listOf(0, 500, 1000),
            SoundRegistry.LIFT_SEQUENCE.map { it.second }
        )
        val (adapter, dispatched) = recordingAdapter()
        val row = SensoryRegistry.row(HapticEvent.DOOR_LIFT)
        adapter.play(row.sound, HapticEvent.DOOR_LIFT)
        assertEquals(
            listOf(
                SoundEvent.LIFT_BOLT_1,
                SoundEvent.LIFT_BOLT_2,
                SoundEvent.LIFT_BOLT_3,
                SoundEvent.DOOR_OPEN_WEIGHT
            ),
            dispatched
        )
    }

    @Test
    fun breakArrivalDispatchesTheNamedSilenceRowAndNothingSounds() {
        val (adapter, dispatched) = recordingAdapter()
        val row = SensoryRegistry.row(HapticEvent.BREAK_ARRIVAL)
        adapter.play(row.sound, HapticEvent.BREAK_ARRIVAL)
        assertEquals(listOf(SoundEvent.BREAK_FIRING), dispatched)
        assertEquals(
            SoundDecision.NAMED_SILENCE,
            SoundRegistry.spec(SoundEvent.BREAK_FIRING).decision
        )
    }

    @Test
    fun everyMappedVoiceExistsInTheVoiceTable() {
        // Construction already guarantees registry completeness;
        // this pins the adapter's map against the voice table so
        // a renamed voice fails here by name.
        StrongroomSoundAdapter.VOICES.values.flatten().forEach { voice ->
            SoundRegistry.spec(voice)
        }
        assertTrue(StrongroomSoundAdapter.VOICES.isNotEmpty())
    }

    @Test
    fun silentByLawBusEventsHaveNoVoiceMapping() {
        HapticEvent.entries.filter { it.isSilentByLaw }.forEach { event ->
            assertTrue(
                "$event is silent by law but the adapter maps a voice for it",
                !StrongroomSoundAdapter.VOICES.containsKey(event)
            )
        }
    }

    @Test
    fun plateStrikeFailureSpeaksItsFiledVoice() {
        // REPINNED by the SOUND seat's voice-debt clearance
        // (2026-11-03): this test formerly pinned the failure
        // strike's voicelessness ("the adapter must not fake
        // one"). The debt is now cleared with a real voice —
        // SOUND-SR-4's FAILURE-STRIKE, specced in the registry —
        // so the pin inverts: the failure speaks, lower and
        // damper than the strike it failed to be.
        val (adapter, dispatched) = recordingAdapter()
        val row = SensoryRegistry.row(HapticEvent.PLATE_STRIKE_FAILURE)
        adapter.play(row.sound, HapticEvent.PLATE_STRIKE_FAILURE)
        assertEquals(listOf(SoundEvent.PLATE_STRIKE_FAILURE), dispatched)
        val failure = SoundRegistry.spec(SoundEvent.PLATE_STRIKE_FAILURE).voice!!
        val strike = SoundRegistry.spec(SoundEvent.PLATE_STRIKE).voice!!
        assertEquals(690f, failure.pitchHz, 0.01f)
        assertTrue(failure.pitchHz < strike.pitchHz)
        assertTrue(failure.gain < strike.gain)
        assertTrue(failure.durationMs < strike.durationMs)
    }

    @Test
    fun theNineVoiceDebtsAreCleared() {
        // The ledger's nine voice debts (SOUND seat, 2026-11-03):
        // each named cue now resolves to real voices through the
        // adapter, and each cue's filed figures are its lead
        // voice's figures — the two registries agree by name.
        val debts = listOf(
            HapticEvent.COMBINATION_GATE,
            HapticEvent.DRUM_LAND,
            HapticEvent.GUARD_TIGHTEN_DETENT,
            HapticEvent.GUARD_TIGHTEN_SEAT,
            HapticEvent.GUARD_RAISE_ROCK,
            HapticEvent.STRETCH_SCRAPE,
            HapticEvent.STRETCH_CARVE,
            HapticEvent.DRAWER_OPEN,
            HapticEvent.PLATE_STRIKE_FAILURE
        )
        val (adapter, dispatched) = recordingAdapter()
        debts.forEach { event ->
            dispatched.clear()
            val row = SensoryRegistry.row(event)
            adapter.play(row.sound, event)
            assertTrue("$event still voiceless", dispatched.isNotEmpty())
            val lead = SoundRegistry.spec(dispatched.first()).voice!!
            assertEquals("$event cue/voice pitch", row.sound.pitchHz, lead.pitchHz)
            assertEquals("$event cue/voice duration", row.sound.durationMs, lead.durationMs)
            assertEquals("$event cue/voice gain", row.sound.gain, lead.gain)
            assertEquals("$event cue/voice chord", row.sound.chordRatio, lead.chordRatio)
        }
    }
}
