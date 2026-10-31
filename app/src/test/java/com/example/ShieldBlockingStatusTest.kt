package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.data.groupedForCatalogue
import com.example.ui.ShieldBlockingProof
import com.example.ui.computeShieldBlockingStatus
import com.example.ui.routineCapWord
import com.example.ui.routineRunningNowSuffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * Shield's status sentence + routine print words + catalogue
 * grouping (Shield audit R-1/R-5/R-6; issue #7) — plain JUnit, no
 * Android: the derivations are pure and their truth tables are
 * the contract. Written by MONTEUR (BUILDER, Vogt's issue #7
 * cell); NOT run in this cell (nobody compiles — declared).
 *
 * Week of the fixtures: Mon 2026-10-26 … Sun 2026-11-01, local
 * zone — the same Calendar math production uses.
 */
class ShieldBlockingStatusTest {

    private fun at(dayOffsetFromMonday: Int, hour: Int, minute: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 26, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, dayOffsetFromMonday)
        return cal.timeInMillis
    }

    private fun session(
        id: Int = 7,
        profileName: String = "Study Loadout",
        active: Boolean = true,
        routineId: Int? = null,
        windowStart: Long = 0L,
        windowEnd: Long = 0L,
        startWall: Long = at(0, 10, 0),
        beatWall: Long = 0L,
        stopwatch: Boolean = false,
        safeBreak: Boolean = false,
        targetSeconds: Int = 3600,
        elapsedSeconds: Int = 0
    ) = FocusSessionEntity(
        id = id, intention = "Deep work", profileName = profileName,
        targetDurationSeconds = targetSeconds, elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = startWall, startElapsedRealtimeMillis = 0L,
        isStopwatchMode = stopwatch, isInSafeBreak = safeBreak,
        isActive = active, completedSuccessfully = false,
        routineId = routineId, windowStartMillis = windowStart,
        windowEndMillis = windowEnd, shieldLastBeatWallMillis = beatWall
    )

    private fun status(
        s: FocusSessionEntity?,
        nowWall: Long,
        shieldRunning: Boolean = false,
        shieldProtecting: Boolean = false,
        shieldSessionId: Int = -1,
        a11yBound: Boolean = false,
        a11yArmedSessionId: Int = -1
    ) = computeShieldBlockingStatus(
        session = s,
        shieldServiceRunning = shieldRunning,
        shieldProtecting = shieldProtecting,
        shieldProtectingSessionId = shieldSessionId,
        accessibilityBound = a11yBound,
        accessibilityArmedSessionId = a11yArmedSessionId,
        nowWallMillis = nowWall
    )

    private fun proved(s: FocusSessionEntity, nowWall: Long) = status(
        s, nowWall,
        shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
        a11yBound = true, a11yArmedSessionId = s.id
    )

    // ------------------------------------------------------------------
    // The status sentence
    // ------------------------------------------------------------------

    @Test
    fun noSession_printsNotBlocking() {
        val st = status(null, at(0, 10, 0))
        assertEquals(ShieldBlockingProof.NO_SESSION, st.proof)
        assertEquals("NOT BLOCKING — NO SESSION RUNNING", st.headline)
        assertFalse(st.isBlocking)
    }

    @Test
    fun inactiveSession_printsNotBlocking() {
        val st = proved(session(active = false), at(0, 10, 0))
        assertEquals(ShieldBlockingProof.NO_SESSION, st.proof)
        assertEquals("NOT BLOCKING — NO SESSION RUNNING", st.headline)
    }

    @Test
    fun activeSessionWithoutWitnesses_isUnproven_neverBlockingNow() {
        val st = status(session(), at(0, 10, 0))
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD + ACCESSIBILITY", st.missingProof)
        assertTrue(st.headline.startsWith("NOT BLOCKING"))
        assertFalse(st.headline.startsWith("BLOCKING NOW"))
        assertFalse(st.isBlocking)
    }

    @Test
    fun onlyShieldWitness_missingAccessibility() {
        val s = session()
        val st = status(
            s, at(0, 10, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("ACCESSIBILITY", st.missingProof)
    }

    @Test
    fun onlyAccessibilityWitness_missingShield() {
        val s = session()
        val st = status(s, at(0, 10, 0), a11yBound = true, a11yArmedSessionId = s.id)
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
    }

    @Test
    fun witnessesForAnotherSession_doNotProveThisOne() {
        val s = session(id = 7)
        val st = status(
            s, at(0, 10, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = 99,
            a11yBound = true, a11yArmedSessionId = 99
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun bothWitnesses_freshBeat_blockingNow_routineLabelAndWindowEnd() {
        val s = session(
            profileName = "Routine: SLEEP", routineId = 3,
            windowStart = at(0, 20, 0), windowEnd = at(1, 3, 30),
            startWall = at(0, 20, 0), beatWall = at(0, 21, 0)
        )
        val st = proved(s, at(0, 21, 0))
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
        assertTrue(st.isBlocking)
        assertEquals("BLOCKING NOW — SLEEP SESSION · ENDS 03:30", st.headline)
    }

    @Test
    fun bothWitnesses_staleBeat_printsStaleNotBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = proved(s, start + 60_000L)
        assertEquals(ShieldBlockingProof.STALE, st.proof)
        assertEquals("NOT BLOCKING — SHIELD PROOF STALE", st.headline)
        assertFalse(st.isBlocking)
    }

    @Test
    fun beatExactlyAtGapThreshold_isStillLive() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = proved(s, start + 10_000L)
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
    }

    @Test
    fun handSession_labelIsProfile_endsDerivedFromSpan() {
        val start = at(0, 10, 0)
        val s = session(
            startWall = start, beatWall = start,
            targetSeconds = 3600, elapsedSeconds = 600
        )
        val st = proved(s, start)
        // Span remaining is 50 minutes from a 10:00 read.
        assertEquals("BLOCKING NOW — STUDY LOADOUT · ENDS 10:50", st.headline)
    }

    @Test
    fun stopwatchSession_liveHeadlineStatesNoEnd() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start, stopwatch = true)
        val st = proved(s, start)
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
        assertTrue(st.headline.startsWith("BLOCKING NOW"))
        assertFalse(st.headline.contains("ENDS"))
    }

    @Test
    fun safeBreak_liveHeadlineNamesPeekOpen() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start, safeBreak = true)
        val st = proved(s, start)
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
        assertTrue(st.headline.contains("SAFE BREAK — PEEK APPS OPEN"))
    }

    // ------------------------------------------------------------------
    // Routine print words (R-6 / R-5)
    // ------------------------------------------------------------------

    @Test
    fun capWord_truthTable() {
        assertEquals("OFF", routineCapWord(enabled = false, daysMask = 127))
        assertEquals("OFF", routineCapWord(enabled = false, daysMask = 0))
        assertEquals("WON'T FIRE", routineCapWord(enabled = true, daysMask = 0))
        assertEquals("ON", routineCapWord(enabled = true, daysMask = 127))
        assertEquals("ON", routineCapWord(enabled = true, daysMask = 31))
    }

    private fun routine(
        mask: Int = RoutineSchedule.MON_FRI,
        enabled: Boolean = true,
        start: Int = 540,
        end: Int = 1170
    ) = RoutineEntity(
        id = 1, name = "DAY BLOCKS", startMinutes = start, endMinutes = end,
        daysMask = mask, loadoutName = SmartLoadout.STUDY.name, enabled = enabled
    )

    @Test
    fun runningNowSuffix_onlyWhenTheActiveSessionNamesThisRoutine() {
        // The marker's source is the session row's routineId
        // (critic Salz C-5), never window arithmetic: inside the
        // window is not enough, and a paused / held / claimed
        // window whose session is not this routine prints nothing.
        assertEquals(" · RUNNING NOW", routineRunningNowSuffix(routine(), activeRoutineId = 1))
        assertEquals("", routineRunningNowSuffix(routine(), activeRoutineId = 2))
        assertEquals("", routineRunningNowSuffix(routine(), activeRoutineId = null))
        // A WON'T FIRE row is never named by a session.
        assertEquals("", routineRunningNowSuffix(routine(mask = 0), activeRoutineId = null))
    }

    @Test
    fun status_carriesTheActiveSessionsRoutineId() {
        val s = session(
            profileName = "Routine: SLEEP", routineId = 3,
            windowStart = at(0, 20, 0), windowEnd = at(1, 3, 30),
            startWall = at(0, 20, 0), beatWall = at(0, 21, 0)
        )
        assertEquals(3, proved(s, at(0, 21, 0)).activeRoutineId)
        assertEquals(null, status(null, at(0, 21, 0)).activeRoutineId)
        // A hand session names no routine.
        val hand = session(startWall = at(0, 10, 0), beatWall = at(0, 10, 0))
        assertEquals(null, proved(hand, at(0, 10, 0)).activeRoutineId)
    }

    // ------------------------------------------------------------------
    // The catalogue's one enumeration (R-1)
    // ------------------------------------------------------------------

    private fun app(name: String, tier: AppBlockTier) = BlockedAppEntity(
        packageName = "com.example.${name.lowercase()}",
        appName = name,
        category = "SOCIAL",
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    @Test
    fun groupedForCatalogue_peekShutOpen_alphaInside_eachAppOnce() {
        val apps = listOf(
            app("Zulu", AppBlockTier.LOCKED),
            app("Alpha", AppBlockTier.OPEN),
            app("Mike", AppBlockTier.PEEK_2M),
            app("Bravo", AppBlockTier.LOCKED),
            app("Anna", AppBlockTier.PEEK_2M)
        )
        val groups = apps.groupedForCatalogue()
        assertEquals(
            listOf(AppBlockTier.PEEK_2M, AppBlockTier.LOCKED, AppBlockTier.OPEN),
            groups.map { it.first }
        )
        assertEquals(listOf("Anna", "Mike"), groups[0].second.map { it.appName })
        assertEquals(listOf("Bravo", "Zulu"), groups[1].second.map { it.appName })
        // De-duplication: every app appears exactly once.
        val flat = groups.flatMap { it.second }
        assertEquals(apps.size, flat.size)
        assertEquals(apps.map { it.packageName }.toSet(), flat.map { it.packageName }.toSet())
    }

    @Test
    fun groupedForCatalogue_absentRulingsContributeNoGroup() {
        val groups = listOf(app("Solo", AppBlockTier.LOCKED)).groupedForCatalogue()
        assertEquals(1, groups.size)
        assertEquals(AppBlockTier.LOCKED, groups[0].first)
        assertTrue(emptyList<BlockedAppEntity>().groupedForCatalogue().isEmpty())
    }
}
