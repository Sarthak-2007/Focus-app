package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.data.SmartLoadout
import com.example.data.groupedForCatalogue
import com.example.service.UsageAccess
import com.example.ui.ACCESS_PROOF_FRESH_MILLIS
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
 * PIN SCOPE: SOURCE — pure derivation truth table over
 * computeShieldBlockingStatus / routineCapWord /
 * groupedForCatalogue; no Android runtime.
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

    /** The monotonic instant every fixture reads "now" at, unless
     *  a case ages a stamp against it. */
    private val monoNow: Long = 1_000_000L

    private fun status(
        s: FocusSessionEntity?,
        nowWall: Long,
        shieldRunning: Boolean = false,
        shieldProtecting: Boolean = false,
        shieldSessionId: Int = -1,
        a11yBound: Boolean = false,
        a11yArmedSessionId: Int = -1,
        usage: UsageAccess = UsageAccess.ALLOWED,
        a11yRootMono: Long = monoNow,
        nowMono: Long = monoNow
    ) = computeShieldBlockingStatus(
        session = s,
        shieldServiceRunning = shieldRunning,
        shieldProtecting = shieldProtecting,
        shieldProtectingSessionId = shieldSessionId,
        accessibilityBound = a11yBound,
        accessibilityArmedSessionId = a11yArmedSessionId,
        usageAccess = usage,
        accessibilityLastRootMono = a11yRootMono,
        nowWallMillis = nowWall,
        nowMonoMillis = nowMono
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
        assertEquals("NOT BLOCKING — THE SHIELD'S OWN CHECKS HAVE STOPPED", st.headline)
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
    fun safeBreak_liveHeadlineLeadsWithWhatIsOpen() {
        val start = at(0, 10, 0)
        // Windowed, like every other ends-asserting case: the
        // windowless span fallback reads maintained break
        // bookkeeping this fixture does not carry, and this test's
        // claim is the lead wording (N-5), not the span math
        // (PRUEF-3's full-log read, run 41021933859).
        val s = session(
            startWall = start, beatWall = start, safeBreak = true,
            windowStart = at(0, 10, 0), windowEnd = at(0, 11, 0)
        )
        val st = proved(s, start)
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
        // N-5: in a break the lead names what is OPEN; BLOCKING NOW
        // as the lead is false for exactly the apps it counts.
        assertEquals(
            "ON A BREAK — PEEK APPS OPEN, THE REST STILL BLOCKED · ENDS 11:00",
            st.headline
        )
        assertFalse(st.headline.startsWith("BLOCKING NOW"))
    }

    // ------------------------------------------------------------------
    // The third witness: usage access (critic Nagel N-1)
    // ------------------------------------------------------------------

    @Test
    fun usageDenied_blindDoor_neverBlockingNow_evenWithBothWitnesses() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("USAGE ACCESS", st.missingProof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: THE APP CAN'T SEE WHICH APP IS OPEN",
            st.headline
        )
        assertFalse(st.isBlocking)
    }

    @Test
    fun usageUnset_blindDoor_sameVerdictAsDenied() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.UNSET
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("USAGE ACCESS", st.missingProof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun usageAndShieldMissing_headlineNamesUsage_missingProofNamesBoth() {
        val s = session()
        val st = status(s, at(0, 10, 0), usage = UsageAccess.DENIED)
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD + ACCESSIBILITY + USAGE ACCESS", st.missingProof)
        // Ranking: the grant only the user can give leads the line.
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: THE APP CAN'T SEE WHICH APP IS OPEN",
            st.headline
        )
    }

    // ------------------------------------------------------------------
    // The ACCESS leg's own staleness law (critic Nagel N-2)
    // ------------------------------------------------------------------

    @Test
    fun accessibilityRootStampStale_pipelineUnproven_notBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS - 1L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN",
            st.headline
        )
        assertFalse(st.isBlocking)
    }

    @Test
    fun accessibilityRootStampExactlyAtFreshLimit_isStillLive() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS
        )
        assertEquals(ShieldBlockingProof.LIVE, st.proof)
        assertTrue(st.isBlocking)
    }

    @Test
    fun accessibilityNeverAcquiredRoot_noProofEver_isStaleNotLive() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = 0L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun accessibilityAndShieldMissing_headlineNamesAccessibility() {
        val s = session()
        val st = status(s, at(0, 10, 0))
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD + ACCESSIBILITY", st.missingProof)
        // Ranking: accessibility (a grant) leads over the shield
        // (the app's own machinery).
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN",
            st.headline
        )
    }

    @Test
    fun shieldNeverFlipped_checksHaventStarted() {
        val s = session()
        val st = status(s, at(0, 10, 0), a11yBound = true, a11yArmedSessionId = s.id)
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
        assertEquals("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED", st.headline)
    }

    // ------------------------------------------------------------------
    // Closing tests (BAU-1 audit): isolation, precedence, boundaries
    // ------------------------------------------------------------------

    @Test
    fun shieldIdMismatchAlone_neverBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = 99,
            a11yBound = true, a11yArmedSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
        assertEquals("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED", st.headline)
        assertFalse(st.isBlocking)
    }

    @Test
    fun accessibilityIdMismatchAlone_neverBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = 99
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("ACCESSIBILITY", st.missingProof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN",
            st.headline
        )
        assertFalse(st.isBlocking)
    }

    @Test
    fun shieldRunningButNeverFlipped_checksHaventStarted() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = false, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
        assertEquals("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED", st.headline)
    }

    @Test
    fun shieldProtectingButServiceGone_neverBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = false, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun accessibilityArmedButUnbound_neverBlocking() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = false, a11yArmedSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("ACCESSIBILITY", st.missingProof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun usageDenied_rootStale_unprovenUsageNotAccessStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS - 1L
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("USAGE ACCESS", st.missingProof)
    }

    @Test
    fun usageDenied_beatStale_unprovenUsageNotStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start + 60_000L,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: THE APP CAN'T SEE WHICH APP IS OPEN",
            st.headline
        )
    }

    @Test
    fun accessMissing_rootStale_unprovenNotAccessStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS - 1L
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("ACCESSIBILITY", st.missingProof)
    }

    @Test
    fun accessMissing_beatStale_unprovenNotStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start + 60_000L,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: NO FRESH SIGN THE APP IS WATCHING YOUR SCREEN",
            st.headline
        )
    }

    @Test
    fun rootStale_beatStale_accessStaleOutranksStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start + 60_000L,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS - 1L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun shieldMissing_beatStale_unprovenShieldNotStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start + 60_000L,
            a11yBound = true, a11yArmedSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("SHIELD", st.missingProof)
        assertEquals("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED", st.headline)
    }

    @Test
    fun rootStale_shieldAlsoMissing_accessStale_diagnosticNamesBoth() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = monoNow - ACCESS_PROOF_FRESH_MILLIS - 1L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertEquals("SHIELD + ACCESSIBILITY", st.missingProof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun wallBackwards_beatInFuture_isStaleNotLive() {
        // The ruled behaviour (BAU-1 G2): a beat stamp ahead of the
        // wall clock is unmeasurable — stale, never fresh. A dead
        // loop must not read BLOCKING NOW for the whole set-back.
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = proved(s, start - 60_000L)
        assertEquals(ShieldBlockingProof.STALE, st.proof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun accessibilityRootStampInFuture_isStaleNotLive() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = monoNow + 60_000L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun beatExactlyOneMillisecondPastGap_isStale() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = proved(s, start + 10_001L)
        assertEquals(ShieldBlockingProof.STALE, st.proof)
        assertEquals("", st.missingProof)
        assertFalse(st.isBlocking)
    }

    @Test
    fun neverBeatenRow_agesFromSessionStart() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = 0L)
        assertEquals(ShieldBlockingProof.LIVE, proved(s, start + 10_000L).proof)
        assertEquals(ShieldBlockingProof.STALE, proved(s, start + 10_001L).proof)
    }

    @Test
    fun beatBeforeStart_startIsTheReference() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start - 60_000L)
        assertEquals(ShieldBlockingProof.LIVE, proved(s, start + 5_000L).proof)
    }

    // Safe break × every failure state: the failure always outranks
    // the break line (BAU-1 G4). A break inside an unproven wall
    // must never print "THE REST STILL BLOCKED".
    private fun breakSession() =
        session(startWall = at(0, 10, 0), beatWall = at(0, 10, 0), safeBreak = true)

    @Test
    fun safeBreak_usageDenied_failureOutranksBreak() {
        val s = breakSession()
        val st = status(
            s, at(0, 10, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertFalse(st.headline.contains("ON A BREAK"))
        assertFalse(st.isBlocking)
    }

    @Test
    fun safeBreak_accessMissing_failureOutranksBreak() {
        val s = breakSession()
        val st = status(
            s, at(0, 10, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id
        )
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertFalse(st.headline.contains("ON A BREAK"))
    }

    @Test
    fun safeBreak_rootStale_failureOutranksBreak() {
        val s = breakSession()
        val st = status(
            s, at(0, 10, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = 0L
        )
        assertEquals(ShieldBlockingProof.ACCESS_STALE, st.proof)
        assertFalse(st.headline.contains("ON A BREAK"))
    }

    @Test
    fun safeBreak_shieldMissing_failureOutranksBreak() {
        val s = breakSession()
        val st = status(s, at(0, 10, 0), a11yBound = true, a11yArmedSessionId = s.id)
        assertEquals(ShieldBlockingProof.UNPROVEN, st.proof)
        assertEquals("NOT BLOCKING — THE APP'S CHECKS HAVEN'T STARTED", st.headline)
    }

    @Test
    fun safeBreak_beatStale_failureOutranksBreak() {
        val s = breakSession()
        val st = proved(s, at(0, 10, 0) + 60_000L)
        assertEquals(ShieldBlockingProof.STALE, st.proof)
        assertEquals("NOT BLOCKING — THE SHIELD'S OWN CHECKS HAVE STOPPED", st.headline)
    }

    @Test
    fun usageDenied_accessMissingOnly_missingNamesBoth() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals("ACCESSIBILITY + USAGE ACCESS", st.missingProof)
        assertEquals(
            "NOT BLOCKING — NOT PROVEN: THE APP CAN'T SEE WHICH APP IS OPEN",
            st.headline
        )
    }

    @Test
    fun usageDenied_shieldMissingOnly_missingNamesBoth() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start)
        val st = status(
            s, start,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals("SHIELD + USAGE ACCESS", st.missingProof)
    }

    @Test
    fun liveRoutine_missingProofIsEmpty_failureBranchesCarryNoRoutineId() {
        val s = session(
            profileName = "Routine: SLEEP", routineId = 3,
            windowStart = at(0, 20, 0), windowEnd = at(1, 3, 30),
            startWall = at(0, 20, 0), beatWall = at(0, 21, 0)
        )
        assertEquals("", proved(s, at(0, 21, 0)).missingProof)
        val denied = status(
            s, at(0, 21, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        assertEquals(null, denied.activeRoutineId)
        val stale = proved(s, at(0, 21, 0) + 60_000L)
        assertEquals(ShieldBlockingProof.STALE, stale.proof)
        assertEquals(null, stale.activeRoutineId)
    }

    @Test
    fun runningNowMarker_neverPrintsOnAFailureBranch() {
        // DIAGNOSE-1R census (Oct 30): the routine row printed
        // RUNNING NOW under NOT BLOCKING headlines because the
        // failure branches carried the routine id into the screen.
        // The derivation now carries it only on LIVE, so the marker
        // has nothing to print from on any failure branch — proven
        // here end-to-end through routineRunningNowSuffix itself.
        val s = session(
            profileName = "Routine: SLEEP", routineId = 3,
            windowStart = at(0, 20, 0), windowEnd = at(1, 3, 30),
            startWall = at(0, 20, 0), beatWall = at(0, 21, 0)
        )
        val routine3 = RoutineEntity(
            id = 3, name = "SLEEP", startMinutes = 1200, endMinutes = 210,
            daysMask = RoutineSchedule.MON_FRI,
            loadoutName = SmartLoadout.STUDY.name, enabled = true
        )
        val denied = status(
            s, at(0, 21, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            usage = UsageAccess.DENIED
        )
        val accessStale = status(
            s, at(0, 21, 0),
            shieldRunning = true, shieldProtecting = true, shieldSessionId = s.id,
            a11yBound = true, a11yArmedSessionId = s.id,
            a11yRootMono = 1_000L, nowMono = 91_001L
        )
        val beatStale = proved(s, at(0, 21, 0) + 60_000L)
        assertEquals(ShieldBlockingProof.UNPROVEN, denied.proof)
        assertEquals(ShieldBlockingProof.ACCESS_STALE, accessStale.proof)
        assertEquals(ShieldBlockingProof.STALE, beatStale.proof)
        for (st in listOf(denied, accessStale, beatStale)) {
            assertEquals(null, st.activeRoutineId)
            assertEquals("", routineRunningNowSuffix(routine3, st.activeRoutineId))
        }
        // The marker is gated, not dead: LIVE still names it.
        assertEquals(
            " · RUNNING NOW",
            routineRunningNowSuffix(routine3, proved(s, at(0, 21, 0)).activeRoutineId)
        )
    }

    @Test
    fun routineWithBlankName_fallsBackToRoutineSession() {
        val s = session(
            profileName = "Routine: ", routineId = 3,
            windowStart = at(0, 10, 0), windowEnd = at(0, 11, 0),
            startWall = at(0, 10, 0), beatWall = at(0, 10, 0)
        )
        assertEquals(
            "BLOCKING NOW — ROUTINE SESSION · ENDS 11:00",
            proved(s, at(0, 10, 0)).headline
        )
    }

    @Test
    fun namelessProfile_printsPlainSession() {
        val start = at(0, 10, 0)
        val s = session(profileName = "", startWall = start, beatWall = start)
        assertTrue(proved(s, start).headline.startsWith("BLOCKING NOW — SESSION"))
    }

    @Test
    fun windowEndNotAfterStart_fallsBackToSpanEnd() {
        val start = at(0, 10, 0)
        val s = session(
            windowStart = start, windowEnd = start,
            startWall = start, beatWall = start,
            targetSeconds = 3600, elapsedSeconds = 0
        )
        assertTrue(proved(s, start).headline.endsWith("· ENDS 11:00"))
    }

    @Test
    fun safeBreak_stopwatch_breakHeadlineHasNoEnds() {
        val start = at(0, 10, 0)
        val s = session(startWall = start, beatWall = start, safeBreak = true, stopwatch = true)
        assertEquals(
            "ON A BREAK — PEEK APPS OPEN, THE REST STILL BLOCKED",
            proved(s, start).headline
        )
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
