package com.example

import com.example.data.ForegroundEventDerivation
import com.example.data.ForegroundEventSample
import com.example.data.ForegroundTransition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #295, finding F2 — the pure law behind the repository's
 * new UsageEvents foreground derivation:
 *
 *  - the foreground is the latest RESUMED package that no PAUSED
 *    or STOPPED for the same package has since closed;
 *  - a stream-named foreground (blocked or not) is final — the
 *    lastTimeUsed recency guess is never consulted while the
 *    stream knows the foreground, so an app the user already left
 *    can no longer be intercepted on recency;
 *  - the recency guess survives only as the fallback for a stream
 *    that names no foreground (empty window, or everything
 *    resumed has since paused away).
 */
class ForegroundEventDerivationTest {

    private val blocked = setOf("com.example.blocked", "com.example.otherblocked")

    private fun resumed(pkg: String, t: Long) =
        ForegroundEventSample(pkg, ForegroundTransition.RESUMED, t)

    private fun paused(pkg: String, t: Long) =
        ForegroundEventSample(pkg, ForegroundTransition.PAUSED, t)

    private fun stopped(pkg: String, t: Long) =
        ForegroundEventSample(pkg, ForegroundTransition.STOPPED, t)

    @Test
    fun emptyStream_noForeground_fallbackConsulted() {
        var fallbackCalls = 0
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = emptyList(),
            blockedPackages = blocked,
            lastTimeUsedFallback = { fallbackCalls++; "com.example.blocked" }
        )
        assertEquals("com.example.blocked", result)
        assertEquals(1, fallbackCalls)
    }

    @Test
    fun resumedBlocked_isForeground_fallbackNotConsulted() {
        var fallbackCalls = 0
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = listOf(resumed("com.example.blocked", 100L)),
            blockedPackages = blocked,
            lastTimeUsedFallback = { fallbackCalls++; "com.example.otherblocked" }
        )
        assertEquals("com.example.blocked", result)
        assertEquals(0, fallbackCalls)
    }

    @Test
    fun resumedThenPaused_noForeground_fallbackConsulted() {
        var fallbackCalls = 0
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = listOf(
                resumed("com.example.blocked", 100L),
                paused("com.example.blocked", 200L)
            ),
            blockedPackages = blocked,
            lastTimeUsedFallback = { fallbackCalls++; null }
        )
        assertNull(result)
        assertEquals(1, fallbackCalls)
    }

    @Test
    fun resumedBlockedThenResumedAllowed_knownForegroundNotBlocked_nullAndNoFallback() {
        // The F2 false intercept, killed: the user opened the
        // blocked app, then left for an allowed one. The stream
        // knows the foreground is the allowed app, so the answer
        // is null — the recency guess naming the blocked app the
        // user already left must NOT fire.
        var fallbackCalls = 0
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = listOf(
                resumed("com.example.blocked", 100L),
                resumed("com.example.allowed", 200L),
                paused("com.example.blocked", 210L)
            ),
            blockedPackages = blocked,
            lastTimeUsedFallback = { fallbackCalls++; "com.example.blocked" }
        )
        assertNull(result)
        assertEquals(0, fallbackCalls)
    }

    @Test
    fun resumedAllowedThenResumedBlocked_blocked() {
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = listOf(
                resumed("com.example.allowed", 100L),
                resumed("com.example.blocked", 200L),
                paused("com.example.allowed", 210L)
            ),
            blockedPackages = blocked,
            lastTimeUsedFallback = { null }
        )
        assertEquals("com.example.blocked", result)
    }

    @Test
    fun pausedOfOtherPackage_doesNotClearForeground() {
        // The system pauses the old activity after resuming the
        // new one; that late PAUSED must not erase the foreground.
        assertEquals(
            "com.example.blocked",
            ForegroundEventDerivation.deriveForegroundPackage(
                listOf(
                    resumed("com.example.allowed", 100L),
                    resumed("com.example.blocked", 200L),
                    paused("com.example.allowed", 300L)
                )
            )
        )
    }

    @Test
    fun resumedThenStopped_noForeground() {
        assertNull(
            ForegroundEventDerivation.deriveForegroundPackage(
                listOf(
                    resumed("com.example.blocked", 100L),
                    stopped("com.example.blocked", 200L)
                )
            )
        )
    }

    @Test
    fun outOfOrderEvents_replayedInTimeOrder() {
        // queryEvents is chronological, but the law must not depend
        // on the caller's ordering: replay is by timestamp.
        assertEquals(
            "com.example.blocked",
            ForegroundEventDerivation.deriveForegroundPackage(
                listOf(
                    resumed("com.example.blocked", 200L),
                    resumed("com.example.allowed", 100L)
                )
            )
        )
    }

    @Test
    fun fallbackAnswer_stillFilteredThroughBlockedSet() {
        val result = ForegroundEventDerivation.selectBlockedForeground(
            events = emptyList(),
            blockedPackages = blocked,
            lastTimeUsedFallback = { "com.example.allowed" }
        )
        assertNull(result)
    }

    @Test
    fun eventWindow_isMinutesNotSeconds() {
        // The window must outlast a stay-put stretch, or the
        // derivation goes silent and the guess returns.
        assertTrue(ForegroundEventDerivation.EVENT_WINDOW_MS in 60_000L..900_000L)
    }
}
