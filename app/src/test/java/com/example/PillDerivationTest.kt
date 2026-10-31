package com.example

import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.service.PillDerivation
import com.example.service.PillFace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The pill derivation, as a table (PILL-AND-TIMES §4.4 case 5,
 * extended): §2.1's pure function over its cases. The load-bearing
 * property is the last one — at every sampled instant the face
 * equals the stored ends at that instant, because the face is
 * recomputed from the rows and the caller's clock, never counted.
 * No ticks exist in this file's world to disagree.
 */
class PillDerivationTest {

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

    @Test
    fun noSessionIsAbsent() {
        assertEquals(PillFace.Absent, derive(null))
    }

    @Test
    fun inactiveSessionIsAbsent() {
        assertEquals(PillFace.Absent, derive(session(isActive = false)))
    }

    @Test
    fun ownPackageIsAbsent() {
        // §2.4 Q1 — a hard rule: inside Monolith, Monolith speaks.
        assertEquals(PillFace.Absent, derive(session(), foreground = own))
    }

    @Test
    fun telecomForegroundIsAbsent() {
        // §2.4 Q4 — a human voice outranks a fuse.
        // Wave-2 settling fix (F4): iterate the production set itself
        // AND pin its exact contents, so the test and the list cannot
        // drift apart in either direction — a typo in any of the five
        // packages, or a silent addition/removal, now fails here.
        assertEquals(
            setOf(
                "com.android.incallui",
                "com.google.android.dialer",
                "com.samsung.android.incallui",
                "com.samsung.android.dialer",
                "com.android.dialer"
            ),
            PillDerivation.TELECOM_FOREGROUND_PACKAGES
        )
        for (pkg in PillDerivation.TELECOM_FOREGROUND_PACKAGES) {
            assertEquals("telecom $pkg", PillFace.Absent, derive(session(), foreground = pkg))
        }
    }

    @Test
    fun keyguardIsAbsent() {
        // §2.4 Q2 — the lock screen is the system's face.
        assertEquals(PillFace.Absent, derive(session(), keyguard = true))
    }

    @Test
    fun breakOutranksPeek() {
        // During a break the doors stand open lease-free; a fuse on
        // an open door would be a false urgency.
        val s = session(
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            lastTickElapsedRealtimeMillis = t0
        )
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(s, rows, now = t0 + 30_000L, foreground = "com.peeked")
        assertEquals(PillFace.Break(570), face)
    }

    @Test
    fun breakRemainingEqualsTheHouseDerivationAtEverySample() {
        val s = session(
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            lastTickElapsedRealtimeMillis = t0
        )
        // Wave-2 settling fix (F1): literal expected values, computed
        // by hand from the spec (600 s stored, whole seconds elapsed,
        // clamped at 0). The previous oracle called the very function
        // under test (breakRemainingAfter) on both sides, so any bug
        // inside it passed twice. Literals: 600-0, 600-1, 600-59,
        // 600-60, 600-599, 600-600→0, past-end→0.
        val samples = listOf(
            0L to 600, 1_000L to 599, 59_000L to 541, 60_000L to 540,
            599_000L to 1, 600_000L to 0, 900_000L to 0
        )
        for ((offsetMs, expected) in samples) {
            val face = derive(s, now = t0 + offsetMs)
            assertEquals("at +${offsetMs}ms", PillFace.Break(expected), face)
        }
    }

    @Test
    fun stopwatchShowsElapsedFromTheStoredPair() {
        val s = session(
            isStopwatch = true,
            elapsedSeconds = 100,
            lastTickElapsedRealtimeMillis = t0
        )
        val at5 = derive(s, now = t0 + 5_000L) as PillFace.Session
        assertTrue(at5.isStopwatch)
        assertEquals(105, at5.elapsedSeconds)
        // Sub-second deltas credit nothing — whole seconds only.
        val at59 = derive(s, now = t0 + 5_999L) as PillFace.Session
        assertEquals(105, at59.elapsedSeconds)
        val at6 = derive(s, now = t0 + 6_000L) as PillFace.Session
        assertEquals(106, at6.elapsedSeconds)
    }

    @Test
    fun timedShowsNowFrameRemainingAndBlock() {
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            lastTickElapsedRealtimeMillis = t0,
            currentBlockIndex = 2,
            totalStudyBlocks = 4
        )
        val face = derive(s, now = t0 + 10_000L) as PillFace.Session
        assertEquals(1190, face.remainingSeconds)
        assertEquals(2, face.blockIndex)
        assertEquals(4, face.blockCount)
    }

    @Test
    fun unstampedTickCreditsNothingBeyondTheStoredFact() {
        val s = session(elapsedSeconds = 300, targetDurationSeconds = 1500)
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(1200, face.remainingSeconds)
    }

    @Test
    fun remainingIncludesThePenalty() {
        // The Now Frame's law: effective target = target + penalty.
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            penaltySecondsAdded = 900
        )
        val face = derive(s, now = t0) as PillFace.Session
        assertEquals(2100, face.remainingSeconds)
    }

    @Test
    fun peekFaceNamesTheRowAndDiesExactlyAtItsEnd() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val s = session()
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 120),
            derive(s, rows, now = t0, foreground = "com.peeked")
        )
        // Ceiling: the last partial second still reads 1…
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 1),
            derive(s, rows, now = t0 + 119_999L, foreground = "com.peeked")
        )
        // …and at the end instant the lease is dead — the face is
        // the session's again, not a zero fuse.
        // Wave-2 settling fix (F5): pin the reverted Session's values,
        // not just its type. The default session here carries
        // intention "Deep Study", elapsed 0, target 1500, an unstamped
        // tick (credits nothing — see unstampedTick…), block 1 of 1.
        val atEnd = derive(s, rows, now = t0 + 120_000L, foreground = "com.peeked")
        assertEquals(
            PillFace.Session(
                intention = "Deep Study",
                isStopwatch = false,
                remainingSeconds = 1500,
                elapsedSeconds = 0,
                blockIndex = 1,
                blockCount = 1
            ),
            atEnd
        )
    }

    @Test
    fun peekFaceFollowsHimWhereverHeWanders() {
        // §2.4 Q5: the countdown follows him to any admitted
        // surface; the face always names the peeked app.
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(session(), rows, now = t0, foreground = "com.notes.app")
        assertEquals(PillFace.Peek("com.peeked", "Peeked", 120), face)
    }

    @Test
    fun twoLiveLeasesResolveToTheLaterEnd() {
        // Storage can never hold two after a grant (the grant
        // zeroes the rest) — the max stays as the belt.
        // Wave-2 classification (F3): BELT, NOT INVARIANT. This
        // proves only the derivation's maxByOrNull arbitration if
        // storage were ever corrupted into two live leases. It
        // proves nothing about the one-live-lease invariant —
        // that is grantZeroesEveryOtherRowsLease's job, sequential
        // only; concurrency (critic C26) is unguarded by any test.
        val rows = listOf(
            app("com.a", "A", peekUntil = t0 + 60_000L),
            app("com.b", "B", peekUntil = t0 + 90_000L)
        )
        val face = derive(session(), rows, now = t0, foreground = "com.a")
        assertEquals(PillFace.Peek("com.b", "B", 90), face)
        // Arbitration is by lease END, not by row order.
        val reversed = derive(session(), rows.reversed(), now = t0, foreground = "com.a")
        assertEquals(PillFace.Peek("com.b", "B", 90), reversed)
    }

    @Test
    fun faceEqualsStoredEndsAtEverySampledInstant() {
        // The two-clocks disease, disproven by sampling: one fixed
        // set of stored ends, many instants — the peek's remaining
        // is the end minus the instant, every time, with no state
        // carried between samples.
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val s = session()
        // Wave-2 settling fix (F2): literal expected seconds per
        // sample (ceiling of (120_000 − offset) ms), computed by
        // hand. The previous oracle mirrored the implementation's
        // own ceiling expression, so a shared ceiling-vs-floor
        // misunderstanding passed both sides. The statelessness
        // claim — one fixed end, no drift across samples — stands
        // unchanged.
        val samples = listOf(
            0L to 120, 999L to 120, 1_000L to 119,
            45_500L to 75, 90_000L to 30, 119_999L to 1
        )
        for ((offset, expectedSeconds) in samples) {
            val face = derive(s, rows, now = t0 + offset, foreground = "com.peeked")
                as PillFace.Peek
            assertEquals("at +${offset}ms", expectedSeconds, face.remainingSeconds)
        }
    }
}
