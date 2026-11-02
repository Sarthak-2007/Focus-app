package com.example

import com.example.data.FinishKind
import com.example.data.FocusSessionEntity
import com.example.data.finishKindOf
import com.example.data.finishPulses
import com.example.session.DeskClockSettleMachine
import com.example.session.DeskClockVerdict
import com.example.session.NightReturnPhase
import com.example.session.NightReturnState
import com.example.session.chargerDisconnectStamp
import com.example.session.deskClockSurrenderDeadlineMono
import com.example.session.deskClockTremorVerdict
import com.example.session.nightReturnEntryIsSnap
import com.example.session.nightReturnExpireIfStale
import com.example.session.nightReturnOnBloom
import com.example.session.nightReturnOnHandDismissal
import com.example.session.nightReturnOnStandDown
import com.example.session.nightReturnOnUnveil
import com.example.session.nightReturnScrimAlpha
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * FLOW §2 + issue #13 (Session Room merge) — the desk clock's
 * auto-raise and print law, proven against the pure machinery:
 * the DeskClockSettle file's DeskClockSettleMachine (settle /
 * cold-open / dismissal, V1 as
 * amended: three-state tremor verdict, HELD suppression, duty
 * cycle, wake re-arm) and the overlay's print derivations in
 * AirlockAndDisarmOverlays.kt (A-1 block line, A-2 descent
 * floors + door invariant, A-3 dawn ramp, A-6 seats, A-7 tear
 * clamp, A-12's C-1/C-2 answers, A-10's stand-down silence).
 * Ported from the flow-clock lane's DeskClockFlowTest and
 * adapted where the audit amended the lane's prints; the
 * state × figure × expected-string table is audit §7 debt 10
 * (Nox's fixture) discharged in code.
 *
 * Written under the no-compile lane order — DECLARED, unrun;
 * Ren compiles and runs on the merged tree.
 */
class DeskClockFlowTest {

    // ---------------------------------------------------------------
    // The settle machine (lane cases, verbatim law)
    // ---------------------------------------------------------------

    private val birthWall = 1_000_000L

    private fun tick(
        machine: DeskClockSettleMachine,
        sessionId: Int?,
        sessionStartWall: Long,
        visible: Boolean = false,
        nowMono: Long,
        lastInteractionMono: Long,
        sensorVerdict: DeskClockVerdict = DeskClockVerdict.UNKNOWN
    ): Boolean = machine.tick(
        sessionId = sessionId,
        sessionStartWallMillis = sessionStartWall,
        processBirthWallMillis = birthWall,
        visible = visible,
        nowMono = nowMono,
        lastInteractionMono = lastInteractionMono,
        sensorVerdict = sensorVerdict
    )

    @Test
    fun settleRaisesAfterTwentySecondsUntouched_andNotBefore() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 10_000L, lastInteractionMono = m0))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 19_999L, lastInteractionMono = m0))
        assertTrue(tick(machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = m0))
    }

    @Test
    fun settleCountsFromTheLastInteraction_whenLaterThanTheSeal() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0))
        val handAt = m0 + 15_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = handAt))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 34_999L, lastInteractionMono = handAt))
        assertTrue(tick(machine, 7, sealedStartWall, nowMono = m0 + 35_000L, lastInteractionMono = handAt))
    }

    @Test
    fun dismissedSessionNeverRaises() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0))
        machine.noteHandDecision(7)
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = m0))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 3_600_000L, lastInteractionMono = m0))
    }

    @Test
    fun raiseFiresExactlyOnce() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0))
        assertTrue(tick(machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = m0))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 21_000L, lastInteractionMono = m0))
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 60_000L, lastInteractionMono = m0))
    }

    @Test
    fun decidedSessionStaysDecided_whenTheFinishHidesTheClock() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0))
        assertTrue(tick(machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = m0))
        assertFalse(
            tick(machine, 7, sealedStartWall, visible = false, nowMono = m0 + 21_000L, lastInteractionMono = m0)
        )
        assertFalse(
            tick(machine, 7, sealedStartWall, visible = false, nowMono = m0 + 120_000L, lastInteractionMono = m0)
        )
    }

    @Test
    fun coldOpenIntoALiveSessionRaisesAtOnce() {
        val machine = DeskClockSettleMachine()
        val bornLongBefore = birthWall - 3_600_000L
        val m0 = 500_000L
        assertTrue(tick(machine, 9, bornLongBefore, nowMono = m0, lastInteractionMono = m0))
        assertTrue(machine.lastRaiseWasColdOpen)
        assertFalse(tick(machine, 9, bornLongBefore, nowMono = m0 + 1_000L, lastInteractionMono = m0))
    }

    @Test
    fun handRaisedClockNeedsNoMachineSecond() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(machine, 7, sealedStartWall, visible = true, nowMono = m0 + 5_000L, lastInteractionMono = m0)
        )
        machine.noteHandDecision(7)
        assertFalse(tick(machine, 7, sealedStartWall, nowMono = m0 + 60_000L, lastInteractionMono = m0))
    }

    @Test
    fun noSessionNoRaise_andTheNextSessionArmsFromItsOwnSeal() {
        val machine = DeskClockSettleMachine()
        val m0 = 100_000L
        assertFalse(tick(machine, null, 0L, nowMono = m0, lastInteractionMono = m0))
        assertFalse(tick(machine, null, 0L, nowMono = m0 + 3_600_000L, lastInteractionMono = m0))
        val start7 = birthWall + 5_000L
        assertFalse(tick(machine, 7, start7, nowMono = m0, lastInteractionMono = m0))
        machine.noteHandDecision(7)
        assertFalse(tick(machine, null, 0L, nowMono = m0 + 30_000L, lastInteractionMono = m0))
        val start8 = birthWall + 7_200_000L
        val m8 = m0 + 7_200_000L
        assertFalse(tick(machine, 8, start8, nowMono = m8, lastInteractionMono = m8))
        assertFalse(tick(machine, 8, start8, nowMono = m8 + 19_999L, lastInteractionMono = m8))
        assertTrue(tick(machine, 8, start8, nowMono = m8 + 20_000L, lastInteractionMono = m8))
    }

    // ---------------------------------------------------------------
    // V1 as amended (E-2): the three-state discriminator
    // ---------------------------------------------------------------

    @Test
    fun tremorVerdict_threeStates() {
        // No usable window (sensor absent) → UNKNOWN.
        assertEquals(DeskClockVerdict.UNKNOWN, deskClockTremorVerdict(null, 1.0))
        // Band at the floor → REST (furniture).
        assertEquals(DeskClockVerdict.REST, deskClockTremorVerdict(1.0, 1.0))
        assertEquals(DeskClockVerdict.REST, deskClockTremorVerdict(1.4, 1.0))
        // Band above floor → HELD (a living hand).
        assertEquals(DeskClockVerdict.HELD, deskClockTremorVerdict(3.0, 1.0))
    }

    @Test
    fun restRaisesOnTheSecondConsecutiveBeat_notTheFirst() {
        // Wave-2 (Samt F-1): REST is a dwell, not a sample —
        // one REST beat can be a braced reader's breath trough;
        // two consecutive beats are furniture.
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 1_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
    }

    @Test
    fun aHeldBeatBetweenRestBeats_restartsTheDwell() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 1_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 2_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
    }

    @Test
    fun heldSuppressesBothPaths_whileTheSignaturePersists() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        // A braced reader: still orientation, tremor band alive.
        // Neither the sensor path nor the 20 s backstop may seize.
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 20_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_600_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        // The phone is put down: REST raises on its second
        // consecutive beat (the Wave-2 dwell).
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_601_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_602_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.REST
            )
        )
    }

    @Test
    fun heldDutyCyclesTheSampler_sixSecondsInThirty() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        // Continuous while HELD is fresh.
        assertTrue(machine.sensorSamplingActive(7, m0 + 10_000L))
        // After 30 s held: 6 s in every 30 s.
        assertTrue(machine.sensorSamplingActive(7, m0 + 61_000L))
        assertFalse(machine.sensorSamplingActive(7, m0 + 70_000L))
        assertTrue(machine.sensorSamplingActive(7, m0 + 91_000L))
        // A decided session never samples.
        machine.noteHandDecision(7)
        assertFalse(machine.sensorSamplingActive(7, m0 + 92_000L))
    }

    @Test
    fun wakeReRaise_reArmsTheSampler() {
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 1_000L, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(machine.isDecided(7))
        // Battery yield, then a wake tap: the sampler re-arms and
        // the machine may raise the session again on sustained
        // REST (the Wave-2 dwell applies here too).
        machine.noteWakeReRaise(7)
        assertFalse(machine.isDecided(7))
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 600_000L,
                lastInteractionMono = m0 + 600_000L, sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 601_000L,
                lastInteractionMono = m0 + 600_000L, sensorVerdict = DeskClockVerdict.REST
            )
        )
    }

    @Test
    fun surrenderDeadline_isMaxPlusTenMinutesStill() {
        assertEquals(
            1_000L + 600_000L,
            deskClockSurrenderDeadlineMono(
                settleStampMono = 1_000L, lastInteractionMono = 500L,
                lastChargerDisconnectMono = 0L
            )
        )
        // An unplug at 03:00 moves the deadline; a stamp of 0
        // (never happened) never wins the max.
        assertEquals(
            900_000L + 600_000L,
            deskClockSurrenderDeadlineMono(
                settleStampMono = 1_000L, lastInteractionMono = 500L,
                lastChargerDisconnectMono = 900_000L
            )
        )
    }

    @Test
    fun chargerDisconnectStamp_theC16Scenario() {
        // Salz C-16's scenario, the one the merged tests could
        // not see: the clock stands from T (22:00) on charge;
        // the unplug comes at T+5 h (03:00) WHILE THE FACE
        // STANDS. The stamp must be the unplug beat itself, so
        // the deadline is unplug + 10 min — no yield before it,
        // and the 520 ms fade window sits just ahead of it.
        val t = 1_000_000L
        val unplug = t + 5L * 3_600_000L
        var stamp = 0L
        // Plugged beats all evening: the stamp is never taken
        // and never needed — charging holds the face.
        stamp = chargerDisconnectStamp(true, true, stamp, t)
        assertEquals(0L, stamp)
        // The edge, sampled on a beat while the clock stands.
        stamp = chargerDisconnectStamp(true, false, stamp, unplug)
        assertEquals(unplug, stamp)
        // Still unplugged beats keep the edge, not the latest.
        stamp = chargerDisconnectStamp(false, false, stamp, unplug + 60_000L)
        assertEquals(unplug, stamp)
        val deadline = deskClockSurrenderDeadlineMono(
            settleStampMono = t, lastInteractionMono = t,
            lastChargerDisconnectMono = stamp
        )
        assertEquals(unplug + 600_000L, deadline)
        // A later plug-in does NOT erase the disconnect fact…
        stamp = chargerDisconnectStamp(false, true, stamp, unplug + 120_000L)
        assertEquals(unplug, stamp)
        // …and a process born on battery stamps its first beat.
        assertEquals(
            t, chargerDisconnectStamp(null, false, 0L, t)
        )
    }

    // ---------------------------------------------------------------
    // The print derivations
    // ---------------------------------------------------------------

    private fun wallAt(hour: Int, minute: Int, second: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, second)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun session(
        isActive: Boolean = true,
        isStopwatchMode: Boolean = false,
        targetDurationSeconds: Int = 3600,
        elapsedSeconds: Int = 0,
        isInSafeBreak: Boolean = false,
        safeBreakSecondsRemaining: Int = 0,
        intention: String = "",
        profileName: String = "STUDY",
        totalStudyBlocks: Int = 1,
        currentBlockIndex: Int = 1,
        routineId: Int? = null,
        windowEndMillis: Long = 0L
    ): FocusSessionEntity = FocusSessionEntity(
        id = 7,
        intention = intention,
        profileName = profileName,
        targetDurationSeconds = targetDurationSeconds,
        elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = birthWall + 5_000L,
        startElapsedRealtimeMillis = 100_000L,
        isStopwatchMode = isStopwatchMode,
        isInSafeBreak = isInSafeBreak,
        safeBreakSecondsRemaining = safeBreakSecondsRemaining,
        isActive = isActive,
        completedSuccessfully = false,
        totalStudyBlocks = totalStudyBlocks,
        currentBlockIndex = currentBlockIndex,
        routineId = routineId,
        windowEndMillis = windowEndMillis
    )

    @Test
    fun standDownIsSilent_andClassified() {
        // A-10(i): finishPulses is false for the stand-down kind;
        // the VM gates the whole ceremony's calls on the kind.
        val now = wallAt(3, 30)
        val row = session(isActive = false, routineId = 5, windowEndMillis = now - 1000L)
        val kind = finishKindOf(row, isSleepLoadout = true, nowWallMillis = now)
        assertEquals(FinishKind.ROUTINE_SLEEP_STANDDOWN, kind)
        assertFalse(finishPulses(kind!!, row))
    }


    // ---------------------------------------------------------------
    // V1 residual (wave-3, Samt F-2 / Funke P-3 reduced latch)
    // ---------------------------------------------------------------

    @Test
    fun heldLatchesAcrossUnknownBeats_untilPositiveRest() {
        // Wave-3: HELD clears only on a positive REST verdict.
        // UNKNOWN beats while held — the sampler's off-window
        // re-registration reads UNKNOWN until its poles charge —
        // must neither release the latch nor restart the 20 s
        // backstop: ignorance is not evidence the hand left.
        val machine = DeskClockSettleMachine()
        val sealedStartWall = birthWall + 5_000L
        val m0 = 100_000L
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0, lastInteractionMono = m0,
                sensorVerdict = DeskClockVerdict.HELD
            )
        )
        // Past the backstop, still latched: no raise.
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 30_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.UNKNOWN
            )
        )
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_600_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.UNKNOWN
            )
        )
        // A positive REST releases the latch and re-arms; the
        // raise still owes the sustained-REST dwell (2 beats).
        assertFalse(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_601_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.REST
            )
        )
        assertTrue(
            tick(
                machine, 7, sealedStartWall, nowMono = m0 + 3_602_000L,
                lastInteractionMono = m0, sensorVerdict = DeskClockVerdict.REST
            )
        )
    }

    // ---------------------------------------------------------------
    // E-1 as one scrim (wave-3): the night-return phase machine
    // (Ruhe §7(b): the grammar had ZERO test coverage pre-wave-3)
    // ---------------------------------------------------------------

    @Test
    fun nightReturn_scrimWeights() {
        assertEquals(0.80f, nightReturnScrimAlpha(NightReturnPhase.VEILED), 0.001f)
        assertEquals(0.45f, nightReturnScrimAlpha(NightReturnPhase.LANDING), 0.001f)
        assertEquals(0f, nightReturnScrimAlpha(NightReturnPhase.OPEN), 0.001f)
    }

    @Test
    fun nightReturn_veilGate_witnessedOrFreshCold_neverStale() {
        // Hauch H-3: witnessed veils; a cold discovery veils
        // only within 30 min of the window's end; at 2 h cold
        // the Deck wears no veil (the receipt still files).
        val end = wallAt(3, 30)
        val open = NightReturnState()
        assertEquals(
            NightReturnPhase.VEILED,
            nightReturnOnStandDown(open, witnessed = true, end, end).phase
        )
        assertEquals(
            NightReturnPhase.VEILED,
            nightReturnOnStandDown(open, witnessed = false, end, end + 20L * 60_000L).phase
        )
        assertEquals(
            NightReturnPhase.VEILED,
            nightReturnOnStandDown(open, witnessed = false, end, end + 30L * 60_000L).phase
        )
        assertEquals(
            NightReturnPhase.OPEN,
            nightReturnOnStandDown(open, witnessed = false, end, end + 2L * 3_600_000L).phase
        )
    }

    @Test
    fun nightReturn_unveilThenBloom_andContactIsNotAVerb() {
        // The two-touch grammar: touch 1 unveils (VEILED →
        // LANDING), the first true verb blooms (LANDING →
        // OPEN, episode closed). Bloom calls off-phase are
        // no-ops — a verb under the veil cannot skip the
        // Landing, and there is no contact path in this machine
        // at all (noteUserInteraction holds no transition).
        val veiled = nightReturnOnStandDown(
            NightReturnState(), witnessed = true, wallAt(3, 30), wallAt(3, 30)
        )
        assertEquals(NightReturnPhase.VEILED, nightReturnOnBloom(veiled).phase)
        val landed = nightReturnOnUnveil(veiled)
        assertEquals(NightReturnPhase.LANDING, landed.phase)
        assertTrue(landed.episodeOpen)
        val bloomed = nightReturnOnBloom(landed)
        assertEquals(NightReturnPhase.OPEN, bloomed.phase)
        assertFalse(bloomed.episodeOpen)
        // The unveil is idempotent off-phase.
        assertEquals(NightReturnPhase.OPEN, nightReturnOnUnveil(bloomed).phase)
    }

    @Test
    fun nightReturn_handDismissalLandsOnlyAtNight() {
        // Ruhe R-1 / Daemmer DAE-5: a night hand dismissal of
        // the standing room enters at LANDING; a day dismissal
        // (A-N3) changes nothing.
        val open = NightReturnState()
        val landed = nightReturnOnHandDismissal(open, isNight = true, nowWallMillis = wallAt(2, 0))
        assertEquals(NightReturnPhase.LANDING, landed.phase)
        assertTrue(landed.episodeOpen)
        assertEquals(
            NightReturnPhase.OPEN,
            nightReturnOnHandDismissal(open, isNight = false, nowWallMillis = wallAt(14, 0)).phase
        )
    }

    @Test
    fun nightReturn_expiry_staleAndDaylightOnly() {
        // Ruhe R-5: armed > 30 min with no night condition
        // standing clears; inside the grace, or while a night
        // condition stands, the return survives.
        val armedAt = wallAt(3, 35)
        val landed = NightReturnState(NightReturnPhase.LANDING, armedAt, episodeOpen = true)
        assertEquals(
            NightReturnPhase.OPEN,
            nightReturnExpireIfStale(landed, isNight = false, nowWallMillis = armedAt + 31L * 60_000L).phase
        )
        assertEquals(
            NightReturnPhase.LANDING,
            nightReturnExpireIfStale(landed, isNight = false, nowWallMillis = armedAt + 10L * 60_000L).phase
        )
        assertEquals(
            NightReturnPhase.LANDING,
            nightReturnExpireIfStale(landed, isNight = true, nowWallMillis = armedAt + 3L * 3_600_000L).phase
        )
    }

    @Test
    fun nightReturn_entrySnaps_onlyWhereRuled() {
        // Ruhe §1.3: the veil's entry snaps (covered), a hand
        // dismissal's Landing entry snaps (under the Throw),
        // an expiry clear snaps; the unveil and the bloom are
        // the two animated legs.
        assertTrue(
            nightReturnEntryIsSnap(NightReturnPhase.OPEN, NightReturnPhase.VEILED, expired = false)
        )
        assertTrue(
            nightReturnEntryIsSnap(NightReturnPhase.OPEN, NightReturnPhase.LANDING, expired = false)
        )
        assertFalse(
            nightReturnEntryIsSnap(NightReturnPhase.VEILED, NightReturnPhase.LANDING, expired = false)
        )
        assertFalse(
            nightReturnEntryIsSnap(NightReturnPhase.LANDING, NightReturnPhase.OPEN, expired = false)
        )
        assertTrue(
            nightReturnEntryIsSnap(NightReturnPhase.LANDING, NightReturnPhase.OPEN, expired = true)
        )
    }
}
