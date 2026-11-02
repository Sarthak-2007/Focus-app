package com.example

import com.example.service.haptics.HapticEvent
import com.example.service.haptics.HapticWaveform
import com.example.service.haptics.SensoryRegistry
import com.example.service.haptics.WaveformKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * THE REGISTRY'S CONTRACTS (service/haptics; FEEL-SR-5):
 * the census ([HapticEvent]) and the registry are one fact —
 * every event has exactly one row; the silent-by-law events
 * are silent in all three senses; the hard stops are
 * resistance shapes with silent sound; no row anywhere is an
 * error buzz; and the dynamic range (H-6) holds across the
 * named chain door > knock > drum detent > grille.
 */
class HapticsRegistryTest {

    @Test
    fun registry_coversEveryHapticEvent_exactlyOnce() {
        assertEquals(HapticEvent.entries.size, SensoryRegistry.size)
        val events = SensoryRegistry.all().map { it.event }
        assertEquals(events.size, events.toSet().size)
        assertEquals(HapticEvent.entries.toSet(), events.toSet())
    }

    @Test
    fun everyRow_carriesItsSource() {
        SensoryRegistry.all().forEach { row ->
            assertTrue("${row.event} must cite its source", row.source.isNotBlank())
        }
    }

    @Test
    fun silentByLawEvents_areSilentInAllThreeSenses() {
        val silent = HapticEvent.entries.filter { it.isSilentByLaw }
        assertTrue("the census must name its silent events", silent.isNotEmpty())
        silent.forEach { event ->
            val row = SensoryRegistry.row(event)
            assertTrue("$event haptic must be silence", row.haptic.isSilent)
            assertEquals(HapticWaveform.SILENCE, row.haptic)
            assertTrue("$event sound must be silence", row.sound.isSilence)
            assertTrue("$event motion must be none", row.motion.isNone)
        }
    }

    @Test
    fun spokenEvents_haveAFeltVoice() {
        HapticEvent.entries.filter { !it.isSilentByLaw }.forEach { event ->
            assertFalse(
                "$event is a spoken event but its waveform is silent",
                SensoryRegistry.row(event).haptic.isSilent
            )
        }
    }

    @Test
    fun knockRow_isTheStrongroomKnockSpec_verbatim() {
        val row = SensoryRegistry.row(HapticEvent.KNOCK)
        assertEquals(listOf(0L, 42L, 30L, 16L), row.haptic.timingsMs)
        assertEquals(listOf(0, 255, 96, 48), row.haptic.amplitudes)
        assertEquals(42L, row.haptic.fallbackMs)
        assertEquals(118f, row.sound.pitchHz)
        assertEquals(110, row.sound.durationMs)
        assertEquals(0.52f, row.sound.gain)
        assertTrue("the knock moves nothing", row.motion.isNone)
        assertEquals(1, row.haptic.composition.size)
    }

    @Test
    fun doorSeal_isFeelsSr1sCanonical1204_withMassAscendingClunks() {
        val seal = SensoryRegistry.row(HapticEvent.DOOR_SEAL).haptic
        assertEquals(1204L, seal.totalDurationMs)
        assertEquals(WaveformKind.CEREMONY, seal.kind)
        // The three bolt strikes (the swing's own peak, 200,
        // stays below them by FEEL-SR-1's L-3).
        val strikes = seal.amplitudes.filter { it >= 235 }
        assertEquals(listOf(235, 250, 255), strikes)
        // Strikes land at the filed sync onsets 670 / 860 / 1074.
        val onsets = mutableListOf<Long>()
        var t = 0L
        seal.timingsMs.zip(seal.amplitudes).forEach { (timing, amp) ->
            if (amp >= 235) onsets += t
            t += timing
        }
        assertEquals(listOf(670L, 860L, 1074L), onsets)
    }

    @Test
    fun doorLift_isFeelsSr1sCanonical1197_andEndsWithoutTerminalImpact() {
        val lift = SensoryRegistry.row(HapticEvent.DOOR_LIFT).haptic
        assertEquals(1197L, lift.totalDurationMs)
        assertEquals(WaveformKind.CEREMONY, lift.kind)
        // Opening ends in stillness: the last felt segment is the
        // swell's lowest step, far under the waveform's peak.
        assertTrue(lift.amplitudes.last() < lift.peakAmplitude)
        assertEquals(30, lift.amplitudes.last())
    }

    @Test
    fun feelSr3ControlTables_areCarriedVerbatim() {
        // The tighten pawl is the app's only double-pulse detent.
        val pawl = SensoryRegistry.row(HapticEvent.GUARD_TIGHTEN_DETENT).haptic
        assertEquals(listOf(0L, 16L, 10L, 10L), pawl.timingsMs)
        assertEquals(listOf(0, 140, 0, 100), pawl.amplitudes)
        // The click-lock: one hard snap, no tail.
        val lock = SensoryRegistry.row(HapticEvent.GUARD_TIGHTEN_SEAT).haptic
        assertEquals(listOf(0L, 35L), lock.timingsMs)
        assertEquals(listOf(0, 210), lock.amplitudes)
        // The Unseated Rock dies out — no terminal event.
        val rock = SensoryRegistry.row(HapticEvent.GUARD_RAISE_ROCK).haptic
        assertEquals(listOf(0, 85, 0, 55, 0, 30), rock.amplitudes)
        // The carve's cut is the last thing the stretch ever says.
        val carve = SensoryRegistry.row(HapticEvent.STRETCH_CARVE).haptic
        assertEquals(listOf(0L, 55L, 30L, 30L), carve.timingsMs)
        assertEquals(listOf(0, 240, 0, 120), carve.amplitudes)
    }

    @Test
    fun breakRedrive_isThreeSeatingsDescendingToTheSoftest() {
        val redrive = SensoryRegistry.row(HapticEvent.BOLTS_REDRIVE).haptic
        assertTrue(redrive.totalDurationMs > 4_900L)
        val strikes = redrive.amplitudes.filter { it >= 150 }
        assertEquals(listOf(200, 180, 155), strikes)
    }

    @Test
    fun breakArrival_isADrawnTwoSeconds_neverAPulse() {
        // FEEL-SR-4 C-04: duration, not amplitude, is the wall
        // between the break's easing and the knock.
        val arrival = SensoryRegistry.row(HapticEvent.BREAK_ARRIVAL).haptic
        assertTrue(arrival.totalDurationMs in 1_900L..2_100L)
        val knock = SensoryRegistry.row(HapticEvent.KNOCK).haptic
        assertTrue(arrival.totalDurationMs > knock.totalDurationMs * 10)
        assertTrue(arrival.peakAmplitude < knock.peakAmplitude)
    }

    @Test
    fun codeCrankFamily_isCensused_withItsSpendAndItsZeroTravelStop() {
        val arm = SensoryRegistry.row(HapticEvent.CODE_CRANK_ARM).haptic
        assertEquals(arm.amplitudes.sorted(), arm.amplitudes)
        val spend = SensoryRegistry.row(HapticEvent.CODE_DETENT_SPEND).haptic
        assertEquals(listOf(0L, 12L), spend.timingsMs)
        assertEquals(220, spend.peakAmplitude)
        val stop = SensoryRegistry.row(HapticEvent.CODE_HARD_STOP).haptic
        assertEquals(WaveformKind.RESISTANCE, stop.kind)
        assertTrue(stop.totalDurationMs <= 30L)
    }

    @Test
    fun hardStops_areResistanceShapes_withSilentSound() {
        listOf(HapticEvent.DRUM_HARD_STOP, HapticEvent.GOVERNOR_HARD_STOP).forEach { event ->
            val row = SensoryRegistry.row(event)
            assertEquals("$event", WaveformKind.RESISTANCE, row.haptic.kind)
            assertTrue("$event must not sound a refusal", row.sound.isSilence)
            assertFalse("$event must still be FELT as resistance", row.haptic.isSilent)
        }
    }

    @Test
    fun noRow_anywhere_isAnErrorBuzz() {
        // An error buzz's shape: one long continuous loud segment.
        SensoryRegistry.all().forEach { row ->
            row.haptic.timingsMs.zip(row.haptic.amplitudes).forEach { (timing, amp) ->
                assertFalse(
                    "${row.event} carries a buzz-shaped segment (${timing}ms @ $amp)",
                    timing >= 150L && amp >= 200
                )
            }
        }
    }

    @Test
    fun dynamicRange_holdsAcrossTheNamedChain() {
        fun energy(event: HapticEvent) = SensoryRegistry.row(event).haptic.energy
        assertTrue(energy(HapticEvent.DOOR_SEAL) > energy(HapticEvent.KNOCK))
        assertTrue(energy(HapticEvent.KNOCK) > energy(HapticEvent.DRUM_DETENT))
        assertTrue(energy(HapticEvent.DRUM_DETENT) > energy(HapticEvent.GRILLE_TOUCH))
        // And the ranks the census declares agree with that order.
        assertTrue(
            HapticEvent.DOOR_SEAL.weightRank > HapticEvent.KNOCK.weightRank &&
                HapticEvent.KNOCK.weightRank > HapticEvent.DRUM_DETENT.weightRank &&
                HapticEvent.DRUM_DETENT.weightRank > HapticEvent.GRILLE_TOUCH.weightRank
        )
    }

    @Test
    fun hapticOnlyRows_existAndAreLawful() {
        // Felt with NO sound, by design (never a missing sound).
        listOf(HapticEvent.PEEK_WARNING_TICK, HapticEvent.TASK_STORED).forEach { event ->
            val row = SensoryRegistry.row(event)
            assertFalse("$event", row.haptic.isSilent)
            assertTrue("$event", row.sound.isSilence)
        }
    }
}
