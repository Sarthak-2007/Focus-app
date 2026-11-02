package com.example

import com.example.service.SoundDecision
import com.example.service.SoundEvent
import com.example.service.SoundFacts
import com.example.service.SoundFamily
import com.example.service.SoundGateLaw
import com.example.service.SoundRegistry
import com.example.service.TACTILE_PERFORMER_NAMES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SOUND-SR-5: the registry's laws, pinned. Completeness (every
 * designed event named exactly once), the no-asset-twice XOR,
 * the felt-twin law, the designed silences, the ceremony
 * sequences' shapes, and the silent-mode/DND gate's truth table.
 */
class SoundEventRegistryTest {

    private fun dayFacts() = SoundFacts(
        soundEnabled = true, hapticsEnabled = true,
        ringerSilences = false, doNotDisturb = false, night = false
    )

    @Test
    fun everyEventIsRegisteredExactlyOnce() {
        val registered = SoundRegistry.ALL.map { it.event }
        assertEquals(SoundEvent.values().size, registered.size)
        assertEquals(SoundEvent.values().toSet(), registered.toSet())
    }

    @Test
    fun noAssetIsHardCodedTwice() {
        for (spec in SoundRegistry.ALL) {
            when (spec.decision) {
                SoundDecision.SOUNDS -> {
                    // Figures XOR legacy performer, never both,
                    // never neither.
                    assertTrue(
                        "${spec.event}: SOUNDS row needs figures or a performer",
                        (spec.voice == null) != (spec.legacyPerformer == null)
                    )
                }
                SoundDecision.FELT_ONLY -> {
                    assertNull("${spec.event}: FELT_ONLY carries no voice", spec.voice)
                    assertTrue(
                        "${spec.event}: FELT_ONLY needs a waveform or a performer",
                        spec.haptic != null || spec.legacyPerformer != null
                    )
                }
                SoundDecision.NAMED_SILENCE -> {
                    assertNull(spec.voice)
                    assertNull(spec.haptic)
                    assertNull(spec.legacyPerformer)
                }
            }
        }
        // Registry-owned voices are all distinct assets: no two
        // rows secretly share one voice's figures.
        val voices = SoundRegistry.ALL.mapNotNull { it.voice }
        assertEquals(voices.size, voices.toSet().size)
        // Legacy performer names are unique per row.
        val names = SoundRegistry.ALL.mapNotNull { it.legacyPerformer }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun everySoundingEventCarriesItsFeltTwin() {
        for (spec in SoundRegistry.ALL) {
            if (spec.decision != SoundDecision.SOUNDS) continue
            if (spec.event == SoundEvent.SOUNDSCAPE) continue // named exception
            assertTrue(
                "${spec.event}: no felt twin — the day would go illegible " +
                    "by feel when sound is off",
                spec.haptic != null || spec.legacyPerformer != null
            )
        }
        // The exception is exactly one event.
        val twinless = SoundRegistry.ALL.filter {
            it.decision == SoundDecision.SOUNDS && it.haptic == null &&
                it.legacyPerformer == null
        }
        assertTrue(twinless.isEmpty())
    }

    @Test
    fun dispatchTableResolvesEveryLegacyName() {
        val registryNames = SoundRegistry.ALL.mapNotNull { it.legacyPerformer }.toSet()
        assertEquals(TACTILE_PERFORMER_NAMES, registryNames)
    }

    @Test
    fun designedSilencesAreNamed() {
        val silences = SoundRegistry.ALL
            .filter { it.decision == SoundDecision.NAMED_SILENCE }
            .map { it.event }
            .toSet()
        assertEquals(
            setOf(
                SoundEvent.DESK_CLOCK_FORMING,
                SoundEvent.SESSION_NOTIFICATION,
                SoundEvent.CONTACT_CALL_THROUGH,
                SoundEvent.HELD_CALL_ARRIVAL,
                SoundEvent.BREAK_FIRING,
                SoundEvent.AJAR_DAY
            ),
            silences
        )
    }

    @Test
    fun hardStopsAreFeltOnly() {
        assertEquals(
            SoundDecision.FELT_ONLY,
            SoundRegistry.spec(SoundEvent.DRUM_SPENT_REFUSAL).decision
        )
        assertEquals(
            SoundDecision.FELT_ONLY,
            SoundRegistry.spec(SoundEvent.CRANK_SPENT_REFUSAL).decision
        )
        assertEquals(
            SoundDecision.FELT_ONLY,
            SoundRegistry.spec(SoundEvent.PEEK_WARNING_TICK).decision
        )
    }

    @Test
    fun sealingDescendsAndLiftingAscends() {
        // RE-AIMED at the end phase (BUILD-SR-24) to the filed
        // ruling + spec — the seam's departing side was THIS
        // TEST, never the registry: it pinned P0's superseded
        // table ([0, 190, 420] onsets, seal gains DESCENDING).
        // R7-4 rules the clunks descend in PITCH and ascend in
        // WEIGHT; SOUND-SR-1's filed figures govern both acts:
        // seal f0 191→143→97 Hz, stem peaks 0.62→0.70→0.80,
        // onsets Δ500/Δ680 (widening) = [0, 500, 1180] relative;
        // lifting f0 121→158→207 Hz, stem peaks 0.50→0.46→0.42
        // (the withdrawals lighten as they free), onsets Δ500/
        // Δ500 (even) = [0, 500, 1000] relative. The registry
        // carries those figures verbatim; they are pinned here
        // exactly so any drift fails by name.
        val seal = SoundRegistry.SEAL_SEQUENCE.map { SoundRegistry.spec(it.first) }
        assertEquals(listOf(0, 500, 1180), SoundRegistry.SEAL_SEQUENCE.map { it.second })
        assertEquals(listOf(191f, 143f, 97f), seal.map { it.voice!!.pitchHz })
        assertEquals(listOf(0.62f, 0.70f, 0.80f), seal.map { it.voice!!.gain })
        assertTrue(seal[0].voice!!.pitchHz > seal[1].voice!!.pitchHz)
        assertTrue(seal[1].voice!!.pitchHz > seal[2].voice!!.pitchHz)
        assertTrue(seal[0].voice!!.gain < seal[1].voice!!.gain)
        assertTrue(seal[1].voice!!.gain < seal[2].voice!!.gain)

        val lift = SoundRegistry.LIFT_SEQUENCE.map { SoundRegistry.spec(it.first) }
        assertEquals(listOf(0, 500, 1000), SoundRegistry.LIFT_SEQUENCE.map { it.second })
        assertEquals(listOf(121f, 158f, 207f), lift.map { it.voice!!.pitchHz })
        assertEquals(listOf(0.50f, 0.46f, 0.42f), lift.map { it.voice!!.gain })
        assertTrue(lift[0].voice!!.pitchHz < lift[1].voice!!.pitchHz)
        assertTrue(lift[1].voice!!.pitchHz < lift[2].voice!!.pitchHz)
        assertTrue(lift[0].voice!!.gain > lift[1].voice!!.gain)
        assertTrue(lift[1].voice!!.gain > lift[2].voice!!.gain)
    }

    @Test
    fun knockIsTheLowestPrivateVoiceAndTouchCoincident() {
        val knock = SoundRegistry.spec(SoundEvent.KNOCK)
        assertEquals(118f, knock.voice!!.pitchHz, 0.01f)
        assertEquals(50L, knock.latencyClass.budgetMs)
        val privateVoices = SoundRegistry.ALL.filter {
            it.family == SoundFamily.PRIVATE_HAND && it.voice != null
        }
        assertTrue(privateVoices.all { it.voice!!.pitchHz >= knock.voice!!.pitchHz })
    }

    // -- The gate law's truth table --------------------------------

    @Test
    fun gateDayFullRingerSilencesAudioNotTheEvent() {
        val knock = SoundRegistry.spec(SoundEvent.KNOCK)
        val day = SoundGateLaw.decide(knock, dayFacts())
        assertEquals(1f, day.audioGainScale, 0.001f)
        assertTrue(day.hapticFires)

        val silenced = SoundGateLaw.decide(knock, dayFacts().copy(ringerSilences = true))
        assertEquals(0f, silenced.audioGainScale, 0.001f)
        assertTrue("the felt twin carries a silenced knock", silenced.hapticFires)

        val factOff = SoundGateLaw.decide(knock, dayFacts().copy(soundEnabled = false))
        assertEquals(0f, factOff.audioGainScale, 0.001f)
        assertTrue(factOff.hapticFires)
    }

    @Test
    fun gateCeremonySoftensUnderDndAndNightNeverDeletes() {
        val clunk = SoundRegistry.spec(SoundEvent.SEAL_CLUNK_1)
        assertEquals(
            0.35f,
            SoundGateLaw.decide(clunk, dayFacts().copy(doNotDisturb = true))
                .audioGainScale, 0.001f
        )
        assertEquals(
            0.65f,
            SoundGateLaw.decide(clunk, dayFacts().copy(night = true))
                .audioGainScale, 0.001f
        )
    }

    @Test
    fun gateUnattendedVoicesAreFullyDndBound() {
        val bolt = SoundRegistry.spec(SoundEvent.LIFT_BOLT_1)
        val dnd = SoundGateLaw.decide(bolt, dayFacts().copy(doNotDisturb = true))
        assertEquals(0f, dnd.audioGainScale, 0.001f)
        assertTrue("the lifting is still felt under DND", dnd.hapticFires)

        val breakEnd = SoundRegistry.spec(SoundEvent.BREAK_END_RETURN)
        assertEquals(
            0.40f,
            SoundGateLaw.decide(breakEnd, dayFacts().copy(night = true))
                .audioGainScale, 0.001f
        )
        assertEquals(
            0f,
            SoundGateLaw.decide(breakEnd, dayFacts().copy(ringerSilences = true))
                .audioGainScale, 0.001f
        )
    }

    @Test
    fun gatePrivateVoicesSilenceUnderAttendedDnd() {
        // NightSignal's law: DND silences ordinary voices even
        // when attended; the twin carries them.
        val detent = SoundRegistry.spec(SoundEvent.COMBINATION_DETENT)
        val dnd = SoundGateLaw.decide(detent, dayFacts().copy(doNotDisturb = true))
        assertEquals(0f, dnd.audioGainScale, 0.001f)
        assertTrue(dnd.hapticFires)
    }

    @Test
    fun gateTearExemptionStandsOverGainScalesOnly() {
        val tear = SoundRegistry.spec(SoundEvent.LEGACY_TEAR_PULSE)
        val nightDnd = SoundGateLaw.decide(
            tear, dayFacts().copy(night = true, doNotDisturb = true)
        )
        assertEquals(1f, nightDnd.audioGainScale, 0.001f)
        // The hard gates still bind the exempt voice.
        assertEquals(
            0f,
            SoundGateLaw.decide(tear, dayFacts().copy(ringerSilences = true))
                .audioGainScale, 0.001f
        )
    }

    @Test
    fun gateNamedSilenceAndHapticsFact() {
        val held = SoundRegistry.spec(SoundEvent.HELD_CALL_ARRIVAL)
        val d = SoundGateLaw.decide(held, dayFacts())
        assertEquals(0f, d.audioGainScale, 0.001f)
        assertFalse(d.hapticFires)

        val knock = SoundRegistry.spec(SoundEvent.KNOCK)
        val noHaptics = SoundGateLaw.decide(knock, dayFacts().copy(hapticsEnabled = false))
        assertEquals(1f, noHaptics.audioGainScale, 0.001f)
        assertFalse(noHaptics.hapticFires)
    }

    @Test
    fun soundscapeIsTheSoleTwinlessSound() {
        val scape = SoundRegistry.spec(SoundEvent.SOUNDSCAPE)
        val d = SoundGateLaw.decide(scape, dayFacts())
        assertEquals(1f, d.audioGainScale, 0.001f)
        assertFalse(d.hapticFires)
    }
}
