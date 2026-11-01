package com.example

import com.example.service.SessionMirrorFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #274: the QS tile's remaining-time label — pure logic.
 * The tile must never print a lie: no session, a stopwatch session
 * (no end wall) or an already-expired mirror all yield null, and
 * the tile falls back to "Session live" instead of a fake countdown.
 */
class SessionMirrorFormatTest {

    @Test
    fun `countdown formats minutes and zero-padded seconds`() {
        val now = 1_000_000L
        assertEquals("42:17 left", SessionMirrorFormat.remainingLabel(now + 2_537_000L, now))
        assertEquals("1:05 left", SessionMirrorFormat.remainingLabel(now + 65_000L, now))
    }

    @Test
    fun `partial second rounds up so the tile never shows zero early`() {
        val now = 5_000L
        assertEquals("0:01 left", SessionMirrorFormat.remainingLabel(now + 1L, now))
    }

    @Test
    fun `no end wall or expired mirror says nothing`() {
        val now = 5_000L
        assertNull(SessionMirrorFormat.remainingLabel(0L, now))
        assertNull(SessionMirrorFormat.remainingLabel(now - 1L, now))
        assertNull(SessionMirrorFormat.remainingLabel(now, now))
    }
}
