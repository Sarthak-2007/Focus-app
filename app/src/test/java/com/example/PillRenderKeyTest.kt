package com.example

import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.service.PillDerivation
import com.example.service.PillFace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * The renderKey contract — census D11 on #16 (comment 6536820385).
 *
 * `PillController.render` touches the window ONLY when the face's
 * renderKey moves ("A string that changes exactly when the
 * printed face changes" — PillFace.kt). That contract had zero
 * tests: a key that changed too often would repaint every second
 * for nothing; a key that changed too rarely would freeze a live
 * fuse on the glass. These tests pin both directions, face by
 * face, plus the cross-face distinctness the gate relies on.
 *
 * Plain JUnit, hand-computed literals, same harness as
 * [PillDerivationTest]. Written against PR #231's train API;
 * parked on `agent/issue-16-test-matrix` for post-train landing.
 * DECLARED, uncompiled.
 *
 * — T24 Builder seat 4, #16 wave
 */
class PillRenderKeyTest {

    private val own = "com.example"
    private val t0 = 1_000_000L

    private fun session(
        isStopwatch: Boolean = false,
        isInSafeBreak: Boolean = false,
        elapsedSeconds: Int = 0,
        safeBreakSecondsRemaining: Int = 0,
        lastTickElapsedRealtimeMillis: Long = 0L
    ) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = 1500,
        elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = 1_000_000L,
        startElapsedRealtimeMillis = 0L,
        isActive = true,
        completedSuccessfully = false,
        isStopwatchMode = isStopwatch,
        isInSafeBreak = isInSafeBreak,
        safeBreakSecondsRemaining = safeBreakSecondsRemaining,
        lastTickElapsedRealtimeMillis = lastTickElapsedRealtimeMillis
    )

    private fun app(pkg: String, name: String, peekUntil: Long = 0L) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = "MESSAGING",
        isBlocked = true,
        activePeekUntilElapsedRealtime = peekUntil
    )

    // (f) The Absent key is the constant "absent".
    @Test
    fun absentRenderKeyIsTheConstantAbsent() {
        assertEquals("absent", PillFace.Absent.renderKey)
    }

    // (a) Identical faces carry identical keys — the gate's
    // no-repaint direction.
    @Test
    fun identicalFacesCarryIdenticalRenderKeys() {
        assertEquals(
            PillFace.Session("Deep Study", false, 1190, 310, 2, 4).renderKey,
            PillFace.Session("Deep Study", false, 1190, 310, 2, 4).renderKey
        )
        assertEquals(
            PillFace.Break(570).renderKey,
            PillFace.Break(570).renderKey
        )
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 42).renderKey,
            PillFace.Peek("com.peeked", "Peeked", 42).renderKey
        )
    }

    // (b) A Session differing only in remaining seconds carries a
    // different key — otherwise the timed face would freeze.
    @Test
    fun sessionRenderKeyMovesWithRemainingSeconds() {
        assertNotEquals(
            PillFace.Session("Deep Study", false, 1190, 310, 2, 4).renderKey,
            PillFace.Session("Deep Study", false, 1189, 311, 2, 4).renderKey
        )
    }

    // (c) Stopwatch vs timed at the same numbers carry different
    // keys — the print differs ("+MM:SS" vs "MM:SS"), so the key
    // must too.
    @Test
    fun stopwatchAndTimedSessionsAtTheSameNumbersCarryDifferentKeys() {
        assertNotEquals(
            PillFace.Session("Deep Study", true, 0, 300, 1, 1).renderKey,
            PillFace.Session("Deep Study", false, 0, 300, 1, 1).renderKey
        )
    }

    // (d) A Peek differing only in package carries a different
    // key — a re-grant to another app must repaint the name.
    @Test
    fun peekRenderKeyMovesWithThePackage() {
        assertNotEquals(
            PillFace.Peek("com.a", "A", 42).renderKey,
            PillFace.Peek("com.b", "A", 42).renderKey
        )
    }

    // (e) Break(n) and a Session with remaining (n) are different
    // prints and must carry different keys.
    @Test
    fun breakAndSessionAtTheSameSecondsCarryDifferentKeys() {
        assertNotEquals(
            PillFace.Break(570).renderKey,
            PillFace.Session("Deep Study", false, 570, 930, 1, 1).renderKey
        )
    }

    // The known omissions, pinned honestly (T16 item 5 on #16):
    // Session's key omits `intention` and Peek's omits `appName`.
    // Today neither omission can freeze a wrong print — the
    // controller never renders the intention, and a peek's app
    // name is a function of its package (which IS in the key),
    // with the remaining seconds changing every second anyway.
    // If a renderer ever prints the intention, or names can
    // change under a stable package, THIS test is the tripwire
    // that must be revisited in the same change.
    @Test
    fun renderKeysOmitIntentionAndAppNameAsPinned() {
        assertEquals(
            PillFace.Session("Deep Study", false, 1190, 310, 2, 4).renderKey,
            PillFace.Session("Morning Pages", false, 1190, 310, 2, 4).renderKey
        )
        assertEquals(
            PillFace.Peek("com.peeked", "Peeked", 42).renderKey,
            PillFace.Peek("com.peeked", "Renamed", 42).renderKey
        )
    }

    // (g) Across a sampled lease, the derived Peek key changes
    // every whole second — the controller's 1 Hz repaint cadence
    // for a live fuse depends on exactly this.
    @Test
    fun peekRenderKeyChangesEverySecondAcrossASampledLease() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val s = session()
        val keys = (0..5).map { second ->
            (PillDerivation.derive(
                s, rows, t0 + second * 1_000L, "com.peeked", own
            ) as PillFace.Peek).renderKey
        }
        assertEquals(
            "six samples one second apart must give six distinct keys",
            6,
            keys.toSet().size
        )
        // …and within one whole second the key is stable, so the
        // gate does not repaint mid-second for nothing.
        val firstSecondKeys = listOf(0L, 1L, 500L, 999L).map { offset ->
            (PillDerivation.derive(
                s, rows, t0 + offset, "com.peeked", own
            ) as PillFace.Peek).renderKey
        }
        assertEquals(1, firstSecondKeys.toSet().size)
    }
}
