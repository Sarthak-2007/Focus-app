package com.example

import com.example.data.BatteryRisk
import com.example.data.BatterySnapshot
import com.example.data.FocusSessionEntity
import com.example.data.SessionWallClock
import com.example.data.batteryBoardIsLack
import com.example.data.batteryBoardWord
import com.example.data.batteryRisk
import com.example.data.batteryShieldWarning
import com.example.data.deskClockBatteryNotice
import com.example.data.deskClockKeepsScreenOn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #122 (Audit R3b, low-battery-mid-session): the battery
 * derivations and the wall-clock death deadline. Pure — the
 * platform read is the only untested seam, and it degrades to
 * unknown rather than inventing a level.
 */
class BatteryTruthTest {

    private fun snap(
        level: Int? = 85,
        saver: Boolean = false,
        charging: Boolean? = false,
        exempt: Boolean = true
    ) = BatterySnapshot(level, saver, charging, exempt)

    // ---- Finding 1: risk + the shield's warning ----

    @Test
    fun persona_at8PercentSaverOn_isCritical_andWarns() {
        val s = snap(level = 8, saver = true)
        assertEquals(BatteryRisk.CRITICAL, batteryRisk(s))
        assertEquals(
            "BATTERY 8% — PLUG IN NOW: PROTECTION ENDS IF THIS PHONE DIES",
            batteryShieldWarning(s)
        )
    }

    @Test
    fun low_at20_warns_withoutNow() {
        val s = snap(level = 20)
        assertEquals(BatteryRisk.LOW, batteryRisk(s))
        assertEquals(
            "BATTERY 20% — PLUG IN: PROTECTION ENDS IF THIS PHONE DIES",
            batteryShieldWarning(s)
        )
    }

    @Test
    fun saverOnly_atHighLevel_stillNamesSaver() {
        val s = snap(level = 90, saver = true)
        assertEquals(BatteryRisk.SAVER_ON, batteryRisk(s))
        assertTrue(batteryShieldWarning(s)!!.contains("SAVER ON"))
    }

    @Test
    fun healthyBattery_noWarning() {
        assertEquals(BatteryRisk.NONE, batteryRisk(snap(level = 85)))
        assertNull(batteryShieldWarning(snap(level = 85)))
    }

    @Test
    fun chargingSuppressesRisk_aPluggedInPhoneIsRecovering() {
        val s = snap(level = 8, saver = true, charging = true)
        assertEquals(BatteryRisk.NONE, batteryRisk(s))
        assertNull(batteryShieldWarning(s))
    }

    @Test
    fun unknownLevel_neverInventsAPercentage() {
        val s = snap(level = null, charging = null)
        assertEquals(BatteryRisk.NONE, batteryRisk(s))
        assertNull(batteryShieldWarning(s))
        // Saver with an unknown level still warns, without a %.
        val saverUnknown = snap(level = null, saver = true, charging = null)
        assertEquals(BatteryRisk.SAVER_ON, batteryRisk(saverUnknown))
        assertFalse(batteryShieldWarning(saverUnknown)!!.contains("%"))
    }

    // ---- Finding 2: PARK's board word ----

    @Test
    fun boardWord_at8SaverOn_isNotSatisfied() {
        val s = snap(level = 8, saver = true)
        assertEquals("8% · SAVER ON", batteryBoardWord(s))
        assertTrue(batteryBoardIsLack(s))
    }

    @Test
    fun boardWord_namesMissingExemption_evenWithALevel() {
        // The first draft hid this: a level existed, so NOT EXEMPT
        // vanished and the FIX door's one lack looked healthy.
        val s = snap(level = 85, exempt = false)
        assertEquals("85% · NOT EXEMPT", batteryBoardWord(s))
        assertTrue(batteryBoardIsLack(s))
    }

    @Test
    fun boardWord_chargingAndHealthy() {
        val s = snap(level = 42, charging = true)
        assertEquals("42% · CHARGING", batteryBoardWord(s))
        assertFalse(batteryBoardIsLack(s))
    }

    @Test
    fun boardWord_noLevel_fallsBackToTheExemptionFact() {
        assertEquals("EXEMPT", batteryBoardWord(snap(level = null, charging = null)))
        assertEquals(
            "NOT EXEMPT",
            batteryBoardWord(snap(level = null, charging = null, exempt = false))
        )
    }

    // ---- Finding 3: the desk clock's keep-screen-on law ----

    @Test
    fun deskClock_releasesTheScreenAtRisk_andSaysWhy() {
        val s = snap(level = 8, saver = true)
        assertFalse(deskClockKeepsScreenOn(s))
        assertTrue(deskClockBatteryNotice(s)!!.contains("WILL NOT HOLD THE SCREEN ON"))
        assertTrue(deskClockKeepsScreenOn(snap(level = 85)))
        assertNull(deskClockBatteryNotice(snap(level = 85)))
        // Charging at 8%: the hold is lawful again.
        assertTrue(deskClockKeepsScreenOn(snap(level = 8, charging = true)))
    }

    // ---- Finding 4: the wall-clock death deadline ----

    private fun session(
        target: Int = 1500,
        elapsed: Int = 1200,
        startWall: Long = 1_000_000L,
        blocks: Int = 1,
        blockIndex: Int = 1,
        inBreak: Boolean = false,
        breakRemaining: Int = 0,
        breakTaken: Int = 0,
        stopwatch: Boolean = false
    ) = FocusSessionEntity(
        intention = "DEATH",
        profileName = "Manual",
        targetDurationSeconds = target,
        elapsedSeconds = elapsed,
        startWallTimeMillis = startWall,
        startElapsedRealtimeMillis = 0L,
        totalStudyBlocks = blocks,
        currentBlockIndex = blockIndex,
        isInSafeBreak = inBreak,
        safeBreakSecondsRemaining = breakRemaining,
        breakTakenSeconds = breakTaken,
        isStopwatchMode = stopwatch,
        isActive = true,
        completedSuccessfully = false
    )

    @Test
    fun wallDeadline_singleBlock_endIsStartPlusTarget() {
        val s = session()
        // Anchor = start + 1200 elapsed; remaining = 300.
        assertEquals(1_000_000L + 1500_000L, SessionWallClock.plannedEndWallMillis(s))
        assertFalse(SessionWallClock.wallDeadlinePassed(s, 1_000_000L + 1499_000L))
        assertTrue(SessionWallClock.wallDeadlinePassed(s, 1_000_000L + 1500_000L))
    }

    @Test
    fun wallDeadline_phoneDeadForHours_pastTheEnd() {
        // Persona: dies at minute 20 of 50, charged 3 hours later.
        val s = session(target = 3000, elapsed = 1200)
        val threeHoursAfterDeath = 1_000_000L + 1200_000L + 3 * 3600_000L
        assertTrue(SessionWallClock.wallDeadlinePassed(s, threeHoursAfterDeath))
    }

    @Test
    fun wallDeadline_multiBlock_countsBlocksAndBreaks() {
        // Block 2 of 2 (1500 each + one 600 break), 100s into
        // block 2, block 2 re-anchored at its own start wall.
        val s = session(
            elapsed = 100, startWall = 5_000_000L,
            blocks = 2, blockIndex = 2, breakTaken = 600
        )
        // Remaining = (2*1500 + 600) - (1500 + 600 + 100) = 1400.
        assertEquals(
            5_000_000L + 100_000L + 1400_000L,
            SessionWallClock.plannedEndWallMillis(s)
        )
    }

    @Test
    fun wallDeadline_midBreak_anchorCountsTheBreakRun() {
        // In the break after block 1 of 2, 150s of break run
        // (450 left), block 1 started at 2_000_000.
        val s = session(
            elapsed = 1500, startWall = 2_000_000L,
            blocks = 2, blockIndex = 1, inBreak = true, breakRemaining = 450
        )
        // Anchor = start + 1500 + 150; span remaining =
        // 3600 - (1500 + 0 + 150) = 1950. End = anchor + 1950s
        // = start + 3600s exactly: the session's true span end.
        assertEquals(2_000_000L + 3600_000L, SessionWallClock.plannedEndWallMillis(s))
    }

    @Test
    fun wallDeadline_stopwatch_neverExpires() {
        val s = session(stopwatch = true)
        assertNull(SessionWallClock.plannedEndWallMillis(s))
        assertFalse(SessionWallClock.wallDeadlinePassed(s, Long.MAX_VALUE / 2))
    }
}
