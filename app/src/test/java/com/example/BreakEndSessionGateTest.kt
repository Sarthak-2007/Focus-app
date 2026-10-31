package com.example

import com.example.service.BreakEndReceiver
import com.example.service.BreakEndScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #155 (WING-F lifecycle edges) — pins for the break-end
 * session gate.
 *
 * The defect: [BreakEndScheduler] armed an exact alarm at every
 * Safe Break entry and stamped the owning session's id into
 * EXTRA_SESSION_ID, but [BreakEndReceiver] never read that extra.
 * Break EXITS cancel the alarm, but a session ENDING mid-break —
 * user stop, emergency valve, routine finalise, supersede by a
 * new session — does not (disarmActiveSession /
 * completeEmergencyBreak / startSession's predecessor close write
 * no scheduler cancel). The alarm then fired at the original
 * break end and posted "Break's over / Back to your session" for
 * a session that no longer existed: a stale glance in news form.
 *
 * The gate's shape is deliberate: it must NOT require the row to
 * still be in a break (at a legitimate expiry the shield tick can
 * flip the break off before the alarm delivery runs) — only that
 * the break's own session is still the live one.
 */
class BreakEndSessionGateTest {

    @Test
    fun allows_whenTheBreaksOwnSessionIsStillLive() {
        assertTrue(BreakEndScheduler.sessionGateAllows(7, 7))
    }

    @Test
    fun suppresses_whenNoSessionIsLive() {
        // User stop / emergency valve / routine finalise mid-break:
        // the row is inactive, so no live session exists at all.
        assertFalse(BreakEndScheduler.sessionGateAllows(7, null))
    }

    @Test
    fun suppresses_whenANewSessionSupersededTheBreaksSession() {
        assertFalse(BreakEndScheduler.sessionGateAllows(7, 8))
    }

    @Test
    fun allows_legacyAlarmWithoutASessionExtra() {
        // An alarm armed before the extra existed (id 0) keeps the
        // pre-gate behaviour instead of being swallowed.
        assertTrue(BreakEndScheduler.sessionGateAllows(0, null))
        assertTrue(BreakEndScheduler.sessionGateAllows(0, 7))
    }

    @Test
    fun breakOverCardExpiresOnTheF3Horizon() {
        assertEquals(45 * 60 * 1000L, BreakEndReceiver.BREAK_OVER_TIMEOUT_MS)
    }

    private fun receiverSource(): String = listOf(
        File("src/main/java/com/example/service/BreakEndReceiver.kt"),
        File("app/src/main/java/com/example/service/BreakEndReceiver.kt"),
        File("../app/src/main/java/com/example/service/BreakEndReceiver.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("BreakEndReceiver.kt not found from ${File(".").absolutePath}")

    @Test
    fun receiverActuallyReadsTheSessionExtraAndGatesOnIt() {
        // The mechanism pin (same style as #540's source pins): the
        // gate function alone proves nothing if the receiver never
        // calls it — the extra must be read and the gate consulted
        // before the card posts.
        val source = receiverSource()
        assertTrue(source.contains("EXTRA_SESSION_ID"))
        assertTrue(source.contains("sessionGateAllows"))
        assertTrue(source.contains("setTimeoutAfter(BREAK_OVER_TIMEOUT_MS)"))
    }
}
