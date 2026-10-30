package com.example

import com.example.data.FinishKind
import com.example.data.FocusSessionEntity
import com.example.data.finishKindOf
import com.example.data.finishPulses
import com.example.ui.DeskClockSettleMachine
import com.example.ui.DeskClockVerdict
import com.example.ui.NightReturnPhase
import com.example.ui.NightReturnState
import com.example.ui.chargerDisconnectStamp
import com.example.ui.deskClockSurrenderDeadlineMono
import com.example.ui.deskClockTremorVerdict
import com.example.ui.nightReturnEntryIsSnap
import com.example.ui.nightReturnExpireIfStale
import com.example.ui.nightReturnOnBloom
import com.example.ui.nightReturnOnHandDismissal
import com.example.ui.nightReturnOnStandDown
import com.example.ui.nightReturnOnUnveil
import com.example.ui.nightReturnScrimAlpha
import com.example.ui.components.deskClockBannerText
import com.example.ui.components.deskClockBlockLine
import com.example.ui.components.deskClockBreakLine
import com.example.ui.components.deskClockEndsLine
import com.example.ui.components.deskClockHeroLabel
import com.example.ui.components.deskClockHeroText
import com.example.ui.components.deskClockNowLine
import com.example.ui.components.deskClockRemainingSeconds
import com.example.ui.components.deskClockPrintAlphas
import com.example.ui.components.deskClockSubLine
import com.example.ui.components.tearFraction
import com.example.ui.components.TEAR_HAIR_FRACTION
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * FLOW §2 + issue #13 (Session Room merge) — the desk clock's
 * auto-raise and print law, proven against the pure machinery:
 * DeskClockSettleMachine (settle / cold-open / dismissal, V1 as
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
    fun idleFacePrintsTheCurrentTime_notTheDialSetting() {
        val now = wallAt(3, 30)
        assertEquals("03:30", deskClockHeroText(null, now))
        assertEquals("03:30", deskClockHeroText(session(isActive = false), now))
    }

    @Test
    fun runningHeroReadsTheSpan_andTheStopwatchItsElapsed() {
        val now = wallAt(21, 0)
        assertEquals("58:59", deskClockHeroText(session(elapsedSeconds = 61), now))
        // Wave-2 (Salz C-8): L1 grammar — ≥ 60 min is H:MM,
        // never total-minutes ("125:49" is dead).
        assertEquals(
            "2:05",
            deskClockHeroText(session(isStopwatchMode = true, elapsedSeconds = 7549), now)
        )
    }

    @Test
    fun routineHeroCountsToTheWindowEnd_notTheFlooredSpan() {
        // Wave-2 (Daemmer DAE-1): a routine row whose span
        // arithmetic says 30 s remain (target built from floored
        // minutes) while its frozen window ends 75 s from now —
        // the hero must read the window's end (01:15, MM:SS), the same
        // instant ENDS prints, never the span's early 00:30.
        val now = wallAt(3, 28, 45)
        val s = session(
            targetDurationSeconds = 3600, elapsedSeconds = 3570,
            routineId = 3, windowEndMillis = now + 75_000L
        )
        assertEquals("01:15", deskClockHeroText(s, now))
        assertEquals("ENDS 03:30", deskClockEndsLine(s, now))
    }

    @Test
    fun heroLabel_isInseparableInEveryState() {
        // Politur P-1: LEFT (timed) / ELAPSED (stopwatch) /
        // THE TIME NOW (idle, incl. an inactive row).
        assertEquals("THE TIME NOW", deskClockHeroLabel(null))
        assertEquals("THE TIME NOW", deskClockHeroLabel(session(isActive = false)))
        assertEquals("LEFT", deskClockHeroLabel(session(elapsedSeconds = 0)))
        assertEquals(
            "ELAPSED",
            deskClockHeroLabel(session(isStopwatchMode = true, elapsedSeconds = 5))
        )
    }

    @Test
    fun heroBoundary_oneFrameChangeAtTheHour() {
        // §7 debt 1, hero share: 3601 / 3600 / 3599 s remaining
        // cross H:MM -> MM:SS with no intermediate glyph and no
        // total-minutes figure at any point.
        val now = wallAt(21, 0)
        assertEquals(
            "1:00",
            deskClockHeroText(session(targetDurationSeconds = 3601, elapsedSeconds = 0), now)
        )
        assertEquals(
            "1:00",
            deskClockHeroText(session(targetDurationSeconds = 3600, elapsedSeconds = 0), now)
        )
        assertEquals(
            "59:59",
            deskClockHeroText(session(targetDurationSeconds = 3600, elapsedSeconds = 1), now)
        )
    }

    @Test
    fun subLineLastMinute_keysToTheWindowForRoutineRows() {
        // Politur P-2: the routine row of DAE-1's test — span
        // arithmetic says 30 s remain, the window says 75 s.
        // The hero reads 01:15, so NOW must NOT drop yet; and
        // inside the window's true last minute it MUST drop,
        // even though the span already reads 0.
        val now = wallAt(3, 28, 45)
        val s = session(
            targetDurationSeconds = 3600, elapsedSeconds = 3570,
            routineId = 3, windowEndMillis = now + 75_000L
        )
        assertEquals(75, deskClockRemainingSeconds(s, now))
        assertEquals("NOW 03:28 · ENDS 03:30", deskClockSubLine(s, now))
        // 30 s later the window has 45 s left — the promotion
        // fires on the window's minute, not the span's.
        val inLastMinute = now + 30_000L
        assertEquals(45, deskClockRemainingSeconds(s, inLastMinute))
        assertEquals("ENDS 03:30", deskClockSubLine(s, inLastMinute))
    }

    @Test
    fun nowLineWearsItsLabel() {
        assertEquals("NOW 21:47", deskClockNowLine(wallAt(21, 47)))
    }

    @Test
    fun bannerChain_taskThenIntentionThenLoadoutFloor() {
        // A-12 / C-2: L7's chain gains the row's own loadout
        // (profileName) as its floor — anonymity only when even
        // that is blank, which no arming path produces.
        assertNull(deskClockBannerText(null, null))
        assertNull(deskClockBannerText(null, "", "  "))
        assertEquals("Integration — Ex 4.2", deskClockBannerText("Integration — Ex 4.2", "ignored"))
        assertEquals("Revise optics", deskClockBannerText(null, "Revise optics"))
        assertEquals("STUDY", deskClockBannerText(null, null, "STUDY"))
        assertEquals("SLEEP", deskClockBannerText(null, null, "Routine: SLEEP"))
    }

    @Test
    fun breakLinePrintsTheCuredSentence_andOnlyInABreak() {
        // §C-3 as cured by Wave-2 (Salz C-18): PEEK APPS OPEN,
        // UNTIL in wall time — the end derived at the reading
        // instant (now + remaining), never a countdown.
        val now = wallAt(21, 25)
        assertEquals("BREAK — PEEK APPS OPEN · UNTIL 21:27", deskClockBreakLine(true, 61, now))
        assertEquals("BREAK — PEEK APPS OPEN · UNTIL 21:35", deskClockBreakLine(true, 600, now))
        assertEquals("BREAK — PEEK APPS OPEN · UNTIL 21:26", deskClockBreakLine(true, 1, now))
        assertNull(deskClockBreakLine(false, 300, now))
    }

    @Test
    fun subLine_seats() {
        // A-6: timed reads NOW · ENDS; the last minute drops
        // NOW; a stopwatch reads NOW alone.
        val now = wallAt(21, 47)
        assertEquals(
            "NOW 21:47 · ENDS 22:47",
            deskClockSubLine(session(elapsedSeconds = 0), now)
        )
        assertEquals(
            "ENDS 21:48",
            deskClockSubLine(session(elapsedSeconds = 3570), wallAt(21, 47, 30))
        )
        assertEquals(
            "NOW 21:47",
            deskClockSubLine(session(isStopwatchMode = true, elapsedSeconds = 7549), now)
        )
    }

    @Test
    fun endsForRoutineRows_derivesFromWindowEndMillis() {
        // A-12 / C-1: the face promises the end the stand-down
        // will keep — the routine row's frozen window end, not
        // the span arithmetic (which can differ from the window).
        val now = wallAt(3, 30)
        val row = session(
            targetDurationSeconds = 3600, elapsedSeconds = 0,
            routineId = 5, windowEndMillis = wallAt(4, 0)
        )
        assertEquals("ENDS 04:00", deskClockEndsLine(row, now))
    }

    @Test
    fun blockLine_stateOnly_withUntil_finalBlockFigureless() {
        // A-1: k < n prints the block's end in wall time; the
        // final block prints no figure; a break suppresses the
        // line whole; a single-block span has no block line.
        val now = wallAt(21, 0)
        val multi = session(
            targetDurationSeconds = 1500, elapsedSeconds = 600,
            totalStudyBlocks = 3, currentBlockIndex = 1
        )
        assertEquals("BLOCK 1 OF 3 · UNTIL 21:15", deskClockBlockLine(multi, now))
        assertEquals(
            "BLOCK 3 OF 3",
            deskClockBlockLine(multi.copy(currentBlockIndex = 3, elapsedSeconds = 600), now)
        )
        assertNull(
            deskClockBlockLine(
                multi.copy(isInSafeBreak = true, safeBreakSecondsRemaining = 300), now
            )
        )
        assertNull(deskClockBlockLine(session(elapsedSeconds = 0), now))
        assertNull(
            deskClockBlockLine(
                session(isStopwatchMode = true, totalStudyBlocks = 3), now
            )
        )
    }

    @Test
    fun alphas_descentFloors_doorInvariant_dawnRamp() {
        // A-2: at span start, standing alphas.
        val start = deskClockPrintAlphas(0f, descentSuspended = false, 3600, false)
        assertEquals(0.55f, start.hero, 0.001f)
        // At span end: hero at its 0.36 floor, number lines at
        // their 0.50 floor, exit never below 0.40 and always
        // ≥ 1.10 × hero (the door invariant, ratio form).
        val end = deskClockPrintAlphas(1f, descentSuspended = false, 3600, false)
        assertEquals(0.36f, end.hero, 0.001f)
        assertEquals(0.50f, end.numberLine, 0.001f)
        assertTrue(end.exit >= 0.40f)
        assertTrue(end.exit >= end.hero * 1.10f - 0.001f)
        // A-2(iii): a fresh raise suspends the descent.
        val raised = deskClockPrintAlphas(1f, descentSuspended = true, 3600, false)
        assertEquals(0.55f, raised.hero, 0.001f)
        // A-3: the dawn ramp — halfway through the final minute
        // the descended hero is halfway back to standing.
        val dawn = deskClockPrintAlphas(1f, descentSuspended = false, 30, false)
        assertEquals(0.455f, dawn.hero, 0.001f)
        // At T-0 the ramp completes.
        val t0 = deskClockPrintAlphas(1f, descentSuspended = false, 1, false)
        assertTrue(t0.hero > 0.54f)
    }

    @Test
    fun tearFraction_isBoundedByHairAndSpan() {
        // A-7(i): coerceIn(hair, 1f) — a delayed finish can run
        // elapsed past target; the rule never overprints.
        assertEquals(TEAR_HAIR_FRACTION, tearFraction(-1f), 0.0001f)
        assertEquals(TEAR_HAIR_FRACTION, tearFraction(0f), 0.0001f)
        assertEquals(0.5f, tearFraction(0.5f), 0.0001f)
        assertEquals(1f, tearFraction(1.5f), 0.0001f)
    }

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
    // §7 debt 10: state × figure × expected-string table
    // ---------------------------------------------------------------

    private data class FaceCase(
        val state: String,
        val session: FocusSessionEntity?,
        val now: Long,
        val hero: String,
        // Wave-3 (Politur P-4): the label column — P-1's defect
        // lived in the table's blind spot (no label field), so
        // the table asserts the label in every state now.
        val label: String,
        val subLine: String?,
        val blockLine: String?,
        val banner: String?
    )

    @Test
    fun faceTable_stateByFigureByExpectedString() {
        val t2147 = wallAt(21, 47)
        val routineLastMinuteNow = wallAt(3, 29, 15)
        val cases = listOf(
            FaceCase(
                state = "IDLE",
                session = null, now = wallAt(3, 30),
                hero = "03:30", label = "THE TIME NOW",
                subLine = null, blockLine = null, banner = null
            ),
            FaceCase(
                state = "RUNNING TIMED",
                session = session(elapsedSeconds = 0), now = t2147,
                hero = "1:00", label = "LEFT", subLine = "NOW 21:47 · ENDS 22:47",
                blockLine = null, banner = "STUDY"
            ),
            FaceCase(
                state = "RUNNING MULTI-BLOCK",
                session = session(
                    targetDurationSeconds = 1500, elapsedSeconds = 600,
                    totalStudyBlocks = 3, currentBlockIndex = 1
                ),
                now = wallAt(21, 0),
                hero = "1:25", label = "LEFT", subLine = "NOW 21:00 · ENDS 22:25",
                blockLine = "BLOCK 1 OF 3 · UNTIL 21:15", banner = "STUDY"
            ),
            FaceCase(
                state = "BREAK (block line suppressed, break line owns the slot)",
                session = session(
                    targetDurationSeconds = 1500, elapsedSeconds = 1500,
                    totalStudyBlocks = 3, currentBlockIndex = 1,
                    isInSafeBreak = true, safeBreakSecondsRemaining = 300
                ),
                now = wallAt(21, 25),
                hero = "1:05", label = "LEFT", subLine = "NOW 21:25 · ENDS 22:30",
                blockLine = null, banner = "BREAK — PEEK APPS OPEN · UNTIL 21:30"
            ),
            FaceCase(
                state = "LAST MINUTE",
                session = session(elapsedSeconds = 3570), now = wallAt(21, 47, 30),
                hero = "00:30", label = "LEFT", subLine = "ENDS 21:48",
                blockLine = null, banner = "STUDY"
            ),
            FaceCase(
                // Wave-3 (Politur P-4): the routine LAST MINUTE
                // row the table could not see — span arithmetic
                // says 30 s remain, the window says 45 s; hero,
                // promotion and dawn all key to the window.
                state = "ROUTINE LAST MINUTE (window-keyed)",
                session = session(
                    targetDurationSeconds = 3600, elapsedSeconds = 3570,
                    routineId = 3, windowEndMillis = routineLastMinuteNow + 45_000L,
                    profileName = "Routine: SLEEP"
                ),
                now = routineLastMinuteNow,
                hero = "00:45", label = "LEFT", subLine = "ENDS 03:30",
                blockLine = null, banner = "SLEEP"
            ),
            FaceCase(
                state = "STOPWATCH",
                session = session(isStopwatchMode = true, elapsedSeconds = 7549),
                now = t2147,
                hero = "2:05", label = "ELAPSED", subLine = "NOW 21:47",
                blockLine = null, banner = "STUDY"
            )
        )
        for (case in cases) {
            assertEquals("${case.state} hero", case.hero, deskClockHeroText(case.session, case.now))
            assertEquals(
                "${case.state} label", case.label, deskClockHeroLabel(case.session)
            )
            assertEquals(
                "${case.state} sub-line", case.subLine,
                case.session?.takeIf { it.isActive }?.let { deskClockSubLine(it, case.now) }
            )
            assertEquals(
                "${case.state} block line", case.blockLine,
                case.session?.let { deskClockBlockLine(it, case.now) }
            )
            assertEquals(
                "${case.state} banner", case.banner,
                case.session?.takeIf { it.isActive }?.let { s ->
                    deskClockBreakLine(s.isInSafeBreak, s.safeBreakSecondsRemaining, case.now)
                        ?: deskClockBannerText(null, s.intention, s.profileName)
                }
            )
        }
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
