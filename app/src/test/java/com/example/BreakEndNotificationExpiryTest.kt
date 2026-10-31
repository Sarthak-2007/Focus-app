package com.example

import com.example.service.BreakEndReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #155 mechanics census (SMALL-3, WING-F) — pin for the
 * break-end card's self-expiry.
 *
 * The census found BreakEndReceiver's "Break's over" card (id
 * 820000, channel "BreakEnded_v2") was the one moment-card in the
 * shield estate with no timeoutAfter and no cancel path anywhere
 * in the codebase: its siblings — the F3 completion card and the
 * SESSION COMPLETE summons — both expire at 45 minutes, while an
 * untapped break-end card could linger indefinitely. These pins
 * follow the source-pin pattern Issue155CompletionExpiryTest
 * established for the summons cure, so a future merge cannot
 * silently drop the expiry.
 */
class BreakEndNotificationExpiryTest {

    @Test
    fun breakEndTimeoutMatchesTheEstateCompletionTimeout() {
        assertEquals(45 * 60 * 1000L, BreakEndReceiver.BREAK_END_TIMEOUT_MS)
        assertEquals(
            com.example.service.MonolithShieldService.COMPLETION_TIMEOUT_MS,
            BreakEndReceiver.BREAK_END_TIMEOUT_MS
        )
    }

    private fun breakEndSource(): String = listOf(
        File("src/main/java/com/example/service/BreakEndReceiver.kt"),
        File("app/src/main/java/com/example/service/BreakEndReceiver.kt"),
        File("../app/src/main/java/com/example/service/BreakEndReceiver.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("BreakEndReceiver.kt not found from ${File(".").absolutePath}")

    @Test
    fun breakEndCardCarriesTheExpiry() {
        val source = breakEndSource()
        assertTrue(source.contains(".setTimeoutAfter(BREAK_END_TIMEOUT_MS)"))
        // The card stays a tap-to-dismiss moment card, not a
        // standing one: autoCancel present, never ongoing.
        assertTrue(source.contains(".setAutoCancel(true)"))
        assertTrue(!source.contains(".setOngoing(true)"))
    }
}
