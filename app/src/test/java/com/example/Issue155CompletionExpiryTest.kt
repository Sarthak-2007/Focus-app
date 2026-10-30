package com.example

import android.app.Notification
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Issue #155 — pins for the completion-expiry mechanism (F3) and
 * its verifier residues (3a card truth, 3b summons ghost).
 *
 * Before these cures: the "Session Complete" shield card was
 * DETACHed, ongoing and never timed out (F3); the loop's full
 * session card posted before the protecting flip and never
 * reverted when the flip failed (3a); and #146's summons
 * "SESSION COMPLETE" card (id 4097) carried no timeout at all,
 * re-creating the F3 ghost on the HIGH channel (3b). F1 already
 * broke once through an unpinned merge train — these tests are
 * the pins so the mechanisms cannot silently regress.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue155CompletionExpiryTest {

    private fun buildNotification(
        ongoing: Boolean,
        autoCancel: Boolean,
        timeoutAfterMs: Long
    ): Notification {
        val service = Robolectric.buildService(MonolithShieldService::class.java)
            .create().get()
        val method = MonolithShieldService::class.java.getDeclaredMethod(
            "buildNotification",
            String::class.java,
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            String::class.java,
            Long::class.javaObjectType,
            Int::class.javaObjectType
        )
        method.isAccessible = true
        return method.invoke(
            service, "Session Complete", "Your receipt is waiting in the Ledger.",
            ongoing, autoCancel, timeoutAfterMs, null, null, null
        ) as Notification
    }

    @Test
    fun completionTimeoutIsFortyFiveMinutes() {
        assertEquals(45 * 60 * 1000L, MonolithShieldService.COMPLETION_TIMEOUT_MS)
    }

    @Test
    fun completionCardIsNotOngoingAutoCancelsAndExpires() {
        // The exact parameter triple the completion path posts.
        val n = buildNotification(
            ongoing = false,
            autoCancel = true,
            timeoutAfterMs = MonolithShieldService.COMPLETION_TIMEOUT_MS
        )
        assertTrue(
            "completion card must not be ongoing",
            n.flags and Notification.FLAG_ONGOING_EVENT == 0
        )
        assertTrue(
            "completion card must dismiss on tap",
            n.flags and Notification.FLAG_AUTO_CANCEL != 0
        )
        assertEquals(
            "completion card must self-expire at the F3 timeout",
            MonolithShieldService.COMPLETION_TIMEOUT_MS,
            n.timeoutAfterMillis
        )
    }

    @Test
    fun standingCardStaysOngoingWithoutExpiry() {
        // Contrast pin: the live-session card's defaults are the
        // opposite — ongoing, no auto-cancel, no timeout. If the
        // builder's parameter plumbing regresses, one of the two
        // tests fails instead of both cards silently swapping.
        val n = buildNotification(ongoing = true, autoCancel = false, timeoutAfterMs = 0L)
        assertTrue(n.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertTrue(n.flags and Notification.FLAG_AUTO_CANCEL == 0)
        assertEquals(0L, n.timeoutAfterMillis)
    }

    private fun shieldSource(): String = listOf(
        File("src/main/java/com/example/service/MonolithShieldService.kt"),
        File("app/src/main/java/com/example/service/MonolithShieldService.kt"),
        File("../app/src/main/java/com/example/service/MonolithShieldService.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("MonolithShieldService.kt not found from ${File(".").absolutePath}")

    @Test
    fun summonsCompletionCardCarriesTheSameExpiry() {
        // Residue (3b): the SESSION COMPLETE summons call must pass
        // the F3 timeout, and postSummons must apply it — otherwise
        // the ghost simply moves to the HIGH channel (id 4097).
        val source = shieldSource()
        assertTrue(source.contains("timeoutAfterMs = COMPLETION_TIMEOUT_MS"))
        assertTrue(source.contains("if (timeoutAfterMs > 0) setTimeoutAfter(timeoutAfterMs)"))
    }

    @Test
    fun loopCardFollowsTheProtectingFlip() {
        // Residue (3a): the loop must gate the full session card on
        // the protecting witness and post the honest arming card
        // when the flip did not happen — the gate and the arming
        // post are the mechanism; this pins their presence in the
        // loop so a future merge cannot quietly un-gate the card.
        val source = shieldSource()
        assertTrue(source.contains("if (!protecting) {"))
        assertTrue(
            source.contains(
                "updateNotification(\"Monolith\", \"Arming shield\\u2026\")"
            )
        )
    }
}
