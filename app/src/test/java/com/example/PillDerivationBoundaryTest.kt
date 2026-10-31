package com.example

import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.service.PillDerivation
import com.example.service.PillFace
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The completing boundary pack for the pill derivation — T02
 * seat 3's TEST-COVERAGE CENSUS on #16 (comment 6536820385),
 * sections D1–D10. The 15 tests in [PillDerivationTest] cover the
 * derivation's primary paths one gate at a time; this file covers
 * the CROSS-PRODUCTS and the edges they do not reach: suppression
 * gates against live break/peek states, a live lease under a dead
 * session, the null-foreground fall-through, reboot signatures,
 * clamps, and the exact ceiling boundaries of the peek fuse.
 *
 * Every expected value below is a hand-computed literal, in the
 * F1/F2/F5 oracle style the existing suite settled on — no
 * expected value is produced by calling the code under test.
 *
 * Written against the pill API as it exists on PR #231's train
 * (`batch-10`); parked on `agent/issue-16-test-matrix` for
 * post-train landing. DECLARED, uncompiled — no local compiling.
 *
 * — T24 Builder seat 4, #16 wave
 */
class PillDerivationBoundaryTest {

    private val own = "com.example"
    private val t0 = 1_000_000L

    private fun session(
        isActive: Boolean = true,
        isStopwatch: Boolean = false,
        isInSafeBreak: Boolean = false,
        elapsedSeconds: Int = 0,
        targetDurationSeconds: Int = 1500,
        penaltySecondsAdded: Int = 0,
        safeBreakSecondsRemaining: Int = 0,
        lastTickElapsedRealtimeMillis: Long = 0L,
        currentBlockIndex: Int = 1,
        totalStudyBlocks: Int = 1
    ) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = targetDurationSeconds,
        elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = 1_000_000L,
        startElapsedRealtimeMillis = 0L,
        isActive = isActive,
        completedSuccessfully = false,
        isStopwatchMode = isStopwatch,
        isInSafeBreak = isInSafeBreak,
        safeBreakSecondsRemaining = safeBreakSecondsRemaining,
        lastTickElapsedRealtimeMillis = lastTickElapsedRealtimeMillis,
        penaltySecondsAdded = penaltySecondsAdded,
        currentBlockIndex = currentBlockIndex,
        totalStudyBlocks = totalStudyBlocks
    )

    private fun app(pkg: String, name: String, peekUntil: Long = 0L) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = "MESSAGING",
        isBlocked = true,
        activePeekUntilElapsedRealtime = peekUntil
    )

    private fun derive(
        session: FocusSessionEntity?,
        rows: List<BlockedAppEntity> = emptyList(),
        now: Long = t0,
        foreground: String? = "com.notes.app",
        keyguard: Boolean = false
    ) = PillDerivation.derive(session, rows, now, foreground, own, keyguard)

    private fun breakSession() = session(
        isInSafeBreak = true,
        safeBreakSecondsRemaining = 600,
        lastTickElapsedRealtimeMillis = t0
    )

    private fun leaseRows() = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))

    // ------------------------------------------------------------------
    // D1 — the keyguard gate outranks BREAK and PEEK, not just SESSION.
    // The existing keyguard test uses a plain session only; the code
    // checks the keyguard BEFORE the break and lease branches.
    // ------------------------------------------------------------------

    @Test
    fun keyguardSuppressesABreakFace() {
        assertEquals(
            PillFace.Absent,
            derive(breakSession(), now = t0 + 30_000L, keyguard = true)
        )
    }

    @Test
    fun keyguardSuppressesAPeekFace() {
        assertEquals(
            PillFace.Absent,
            derive(session(), leaseRows(), now = t0, foreground = "com.peeked", keyguard = true)
        )
    }

    // ------------------------------------------------------------------
    // D2 — own-package and telecom foregrounds outrank BREAK (and the
    // telecom set outranks PEEK). The existing own/telecom tests use
    // a plain session only.
    // ------------------------------------------------------------------

    @Test
    fun ownPackageSuppressesABreakFace() {
        assertEquals(
            PillFace.Absent,
            derive(breakSession(), now = t0 + 30_000L, foreground = own)
        )
    }

    @Test
    fun telecomForegroundSuppressesABreakFace() {
        assertEquals(
            PillFace.Absent,
            derive(breakSession(), now = t0 + 30_000L, foreground = "com.android.incallui")
        )
    }

    @Test
    fun telecomForegroundSuppressesAPeekFace() {
        assertEquals(
            PillFace.Absent,
            derive(
                session(), leaseRows(), now = t0,
                foreground = "com.google.android.dialer"
            )
        )
    }

    @Test
    fun ownPackageSuppressesAPeekFace() {
        assertEquals(
            PillFace.Absent,
            derive(session(), leaseRows(), now = t0, foreground = own)
        )
    }

    // ------------------------------------------------------------------
    // D3 — a live lease must not resurrect a dead session. The
    // session gate is checked before any row is consulted.
    // ------------------------------------------------------------------

    @Test
    fun inactiveSessionWithALiveLeaseIsAbsent() {
        assertEquals(
            PillFace.Absent,
            derive(session(isActive = false), leaseRows(), now = t0, foreground = "com.peeked")
        )
    }

    @Test
    fun nullSessionWithALiveLeaseIsAbsent() {
        assertEquals(
            PillFace.Absent,
            derive(null, leaseRows(), now = t0, foreground = "com.peeked")
        )
    }

    // ------------------------------------------------------------------
    // D4 — a null foreground is "no read", NOT own-package: the
    // derivation falls through to whichever face the rows demand.
    // (The controller resolves its own unreadable case before
    // calling; this pins the derivation's contract as written.)
    // ------------------------------------------------------------------

    @Test
    fun nullForegroundStillShowsThePeekFace() {
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 120),
            derive(session(), leaseRows(), now = t0, foreground = null)
        )
    }

    @Test
    fun nullForegroundStillShowsTheSessionFace() {
        val face = derive(session(elapsedSeconds = 300), now = t0, foreground = null)
        assertEquals(
            PillFace.Session("Deep Study", false, 1200, 300, 1, 1),
            face
        )
    }

    @Test
    fun nullForegroundStillShowsTheBreakFace() {
        assertEquals(
            PillFace.Break(570),
            derive(breakSession(), now = t0 + 30_000L, foreground = null)
        )
    }

    // ------------------------------------------------------------------
    // D5 — the reboot signature on the SESSION branch: nowMono
    // behind the stored tick credits nothing (the derivation's own
    // comment promises this; the break side is pinned in
    // WallAccrualTest, the session side was untested).
    // ------------------------------------------------------------------

    @Test
    fun rebootSignatureCreditsNothingToATimedSession() {
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            lastTickElapsedRealtimeMillis = t0 + 900_000L
        )
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(300, face.elapsedSeconds)
        assertEquals(1200, face.remainingSeconds)
    }

    @Test
    fun rebootSignatureCreditsNothingToAStopwatchSession() {
        val s = session(
            isStopwatch = true,
            elapsedSeconds = 100,
            lastTickElapsedRealtimeMillis = t0 + 900_000L
        )
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(100, face.elapsedSeconds)
    }

    // ------------------------------------------------------------------
    // D6 — timed remaining clamps at 0: at the target exactly, and
    // past target + penalty. The face never prints a negative fuse.
    // ------------------------------------------------------------------

    @Test
    fun timedRemainingIsZeroExactlyAtTheTarget() {
        val s = session(elapsedSeconds = 1500, targetDurationSeconds = 1500)
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(0, face.remainingSeconds)
    }

    @Test
    fun timedRemainingNeverGoesNegativePastTargetPlusPenalty() {
        val s = session(
            elapsedSeconds = 3000,
            targetDurationSeconds = 1500,
            penaltySecondsAdded = 900
        )
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(0, face.remainingSeconds)
        assertEquals(3000, face.elapsedSeconds)
    }

    // ------------------------------------------------------------------
    // D7 — full data-class equality on the primary paths (the F5
    // revert-pin style): the timed test in the existing suite never
    // asserts intention/elapsedSeconds; the stopwatch test never
    // asserts remainingSeconds == 0 or the block fields.
    // ------------------------------------------------------------------

    @Test
    fun timedSessionFaceIsPinnedWhole() {
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            lastTickElapsedRealtimeMillis = t0,
            currentBlockIndex = 2,
            totalStudyBlocks = 4
        )
        assertEquals(
            PillFace.Session(
                intention = "Deep Study",
                isStopwatch = false,
                remainingSeconds = 1190,
                elapsedSeconds = 310,
                blockIndex = 2,
                blockCount = 4
            ),
            derive(s, now = t0 + 10_000L)
        )
    }

    @Test
    fun stopwatchSessionFaceIsPinnedWhole() {
        val s = session(
            isStopwatch = true,
            elapsedSeconds = 100,
            lastTickElapsedRealtimeMillis = t0,
            currentBlockIndex = 3,
            totalStudyBlocks = 5
        )
        assertEquals(
            PillFace.Session(
                intention = "Deep Study",
                isStopwatch = true,
                remainingSeconds = 0,
                elapsedSeconds = 105,
                blockIndex = 3,
                blockCount = 5
            ),
            derive(s, now = t0 + 5_000L)
        )
    }

    // ------------------------------------------------------------------
    // D8 — the break-tick asymmetry, at the derivation level. The
    // session's elapsedAt treats lastTick <= 0 as unstamped (credits
    // nothing — pinned by unstampedTickCreditsNothing… in the
    // existing suite); breakRemainingAfter treats 0 as a LEGITIMATE
    // anchor, so a break row stamped at boot-millisecond 0 drains
    // by the full clock. And a break reboot signature keeps the
    // stored remaining whole.
    // ------------------------------------------------------------------

    @Test
    fun breakStampedAtTickZeroDrainsByTheFullClock() {
        val s = session(
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            lastTickElapsedRealtimeMillis = 0L
        )
        // 600 stored, anchor 0, now = t0 (1_000_000 ms = 1000 s):
        // 600 − 1000 clamps to 0. A guard that treated 0 as
        // "unstamped" would wrongly print 600 here.
        assertEquals(PillFace.Break(0), derive(s, now = t0))
    }

    @Test
    fun breakRebootSignatureKeepsTheStoredRemaining() {
        val s = session(
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            lastTickElapsedRealtimeMillis = t0 + 900_000L
        )
        assertEquals(PillFace.Break(600), derive(s, now = t0))
    }

    // ------------------------------------------------------------------
    // D9 — the peek ceiling at its exact boundaries. The existing
    // suite covers a 999 ms offset and the last millisecond; these
    // pin the whole-second steps themselves.
    // ------------------------------------------------------------------

    @Test
    fun peekCeilingAtExactWholeSecondBoundaries() {
        val s = session()
        val rows = leaseRows()
        // Exactly 1 000 ms of fuse left reads 1, not 0 and not 2.
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 1),
            derive(s, rows, now = t0 + 119_000L, foreground = "com.peeked")
        )
        // 1 001 ms left ceilings to 2.
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 2),
            derive(s, rows, now = t0 + 118_999L, foreground = "com.peeked")
        )
        // A single millisecond of fuse left still reads 1.
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 1),
            derive(s, rows, now = t0 + 119_999L, foreground = "com.peeked")
        )
    }

    // ------------------------------------------------------------------
    // T16 caveat on the existing suite's D11/D9: the stamped-tick ×
    // penalty cell of the matrix was empty — the tick test carries
    // no penalty, the penalty test carries no tick. Both together:
    // elapsed 300 stored + 10 s of tick = 310; effective target
    // 1500 + 900 = 2400; remaining = 2400 − 310 = 2090.
    // ------------------------------------------------------------------

    @Test
    fun stampedTickAndPenaltyComposeInTheRemaining() {
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            penaltySecondsAdded = 900,
            lastTickElapsedRealtimeMillis = t0
        )
        val face = derive(s, now = t0 + 10_000L) as PillFace.Session
        assertEquals(310, face.elapsedSeconds)
        assertEquals(2090, face.remainingSeconds)
    }

    // ------------------------------------------------------------------
    // T16 caveat on the existing suite's break samples: every
    // offset there is a whole second, so the break's sub-second
    // truncation was never sampled at the derivation level (the
    // session side was). The break face holds its second until
    // the whole second has passed, exactly like the house
    // function pinned in PillTimeArithmeticTest.
    // ------------------------------------------------------------------

    @Test
    fun breakFaceDrainsNothingInsideTheFirstWholeSecond() {
        val s = breakSession()
        assertEquals(PillFace.Break(600), derive(s, now = t0))
        assertEquals(PillFace.Break(600), derive(s, now = t0 + 999L))
        assertEquals(PillFace.Break(599), derive(s, now = t0 + 1_000L))
        assertEquals(PillFace.Break(541), derive(s, now = t0 + 59_999L))
        assertEquals(PillFace.Break(540), derive(s, now = t0 + 60_000L))
    }

    // ------------------------------------------------------------------
    // D10 — a long-expired lease falls through to the session face,
    // pinned whole: the row's stale non-zero end is ignored, not
    // merely outranked at the end instant (which the existing
    // suite already covers).
    // ------------------------------------------------------------------

    @Test
    fun longExpiredLeaseFallsThroughToTheWholeSessionFace() {
        assertEquals(
            PillFace.Session(
                intention = "Deep Study",
                isStopwatch = false,
                remainingSeconds = 1500,
                elapsedSeconds = 0,
                blockIndex = 1,
                blockCount = 1
            ),
            derive(session(), leaseRows(), now = t0 + 180_000L, foreground = "com.peeked")
        )
    }
}
