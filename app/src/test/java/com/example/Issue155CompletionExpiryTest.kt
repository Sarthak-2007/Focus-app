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

    private fun buildNotificationCard(
        title: String,
        subtitle: String,
        ongoing: Boolean
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
            service, title, subtitle, ongoing, false, 0L, null, null, null
        ) as Notification
    }

    private fun publicTextOf(n: Notification): String =
        n.publicVersion?.extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: ""

    @Test
    fun lockScreenPublicVersionNeverClaimsProtectionWithoutTheWitness() {
        // LEAD-4 A7 (WING-F stack): the public version is gated on
        // the protecting witness itself, not the subtitle alone.
        // In this harness the witness is down (no shield loop has
        // flipped it), so even a standing-form card must carry the
        // non-claim line on the lock screen — before the gate it
        // read "Focus session protected" verbatim.
        MonolithShieldService.protecting = false
        val standing = buildNotificationCard(
            "Monolith \u2022 Deep Work", "42:00 left in session \u2022 3 reaches blocked",
            ongoing = true
        )
        assertEquals("Monolith session in progress", publicTextOf(standing))
        val arming = buildNotificationCard("Monolith", "Arming shield\u2026", ongoing = true)
        assertEquals("Monolith session in progress", publicTextOf(arming))
        val complete = buildNotificationCard(
            "Session Complete", "Your receipt is waiting in the Ledger.", ongoing = false
        )
        assertEquals("Session complete", publicTextOf(complete))
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
        // Read-back accessor: Notification has no `timeoutAfterMillis`
        // property — Builder.setTimeoutAfter(long) is read back via
        // Notification.getTimeoutAfter() (API 26+), i.e. Kotlin
        // `timeoutAfter`. If the production setTimeoutAfter call is
        // removed, this returns 0 and the pin fails.
        assertEquals(
            "completion card must self-expire at the F3 timeout",
            MonolithShieldService.COMPLETION_TIMEOUT_MS,
            n.timeoutAfter
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
        assertEquals(0L, n.timeoutAfter)
    }

    private fun shieldSource(): String = listOf(
        File("src/main/java/com/example/service/MonolithShieldService.kt"),
        File("app/src/main/java/com/example/service/MonolithShieldService.kt"),
        File("../app/src/main/java/com/example/service/MonolithShieldService.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("MonolithShieldService.kt not found from ${File(".").absolutePath}")

    private fun widgetSource(): String = listOf(
        File("src/main/java/com/example/widget/MonolithWidgetProvider.kt"),
        File("app/src/main/java/com/example/widget/MonolithWidgetProvider.kt"),
        File("../app/src/main/java/com/example/widget/MonolithWidgetProvider.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("MonolithWidgetProvider.kt not found from ${File(".").absolutePath}")

    private fun widgetTruthSource(): String = listOf(
        File("src/main/java/com/example/widget/WidgetSealTruth.kt"),
        File("app/src/main/java/com/example/widget/WidgetSealTruth.kt"),
        File("../app/src/main/java/com/example/widget/WidgetSealTruth.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("WidgetSealTruth.kt not found from ${File(".").absolutePath}")

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

    @Test
    fun revokedAfterFlipUnflipsTheWitness() {
        // Race 1b (IQ-4) + IQ-1 unified latch (WING-F): the latch
        // must also flip DOWN — an iteration that measures EITHER
        // door closed (usage access revoked OR the accessibility
        // door no longer standing for this session) resets the
        // witness before the flip/gate below it, in the same
        // iteration, and fires the falling-edge widget refresh.
        // Without this block the gate reads a stale-true latch
        // forever after a mid-session revocation.
        val source = shieldSource()
        val resetIdx = source.indexOf("if (protecting && (!usageGranted || !accessibilityStanding)) {")
        assertTrue("revoked-after-flip reset block missing", resetIdx >= 0)
        val window = source.substring(resetIdx, minOf(resetIdx + 700, source.length))
        assertTrue(window.contains("protecting = false"))
        assertTrue(window.contains("protectingSessionId = -1"))
        assertTrue(
            "demote must fire the falling-edge widget refresh",
            window.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
        val flipIdx = source.indexOf(
            "if (!protecting && usageGranted && accessibilityStanding) {", resetIdx
        )
        assertTrue("reset must run before the flip in the iteration", flipIdx > resetIdx)
    }

    @Test
    fun unifiedLatchMeasuresBothDoorsAtTheFlipSite() {
        // IQ-1: the flip requires BOTH doors, and the
        // accessibility door's standing is the armed-for-THIS-
        // session measure (bound + armedSessionId == session id),
        // taken per iteration in the shield loop — not the
        // widget reading the accessibility service (the rejected
        // fork) and not a bound-only check.
        val source = shieldSource()
        assertTrue(
            source.contains(
                "MonolithAccessibilityService.armedSessionId == updated.id"
            )
        )
        assertTrue(
            source.contains(
                "if (!protecting && usageGranted && accessibilityStanding) {"
            )
        )
        assertTrue(
            "the latch must not re-home the door as a published second witness",
            !source.contains("protectingAccessibilityStanding")
        )
    }

    @Test
    fun widgetNeverReadsTheAccessibilityServiceNorADoorParameter() {
        // IQ-1's fork pin, final form (WING-F): the provider fork
        // is REMOVED, not re-homed — the widget reads no
        // accessibility service, and WidgetSealTruth takes no
        // accessibility parameter. With the unified latch the
        // protecting witness implies the door; if either file
        // grows a second derivation again, this pin fails.
        val widget = widgetSource()
        assertTrue(!widget.contains("MonolithAccessibilityService"))
        assertTrue(!widget.contains("accessibilityBound"))
        val truth = widgetTruthSource()
        assertTrue(!truth.contains("accessibilityBound"))
    }

    @Test
    fun breakOverSummonsExpiresAfterTenMinutes() {
        // IQ-2 VC-4: BREAK OVER carries its own 10-minute term,
        // not the completion cards' 45 minutes.
        assertEquals(600_000L, MonolithShieldService.BREAK_OVER_TIMEOUT_MS)
        val source = shieldSource()
        assertTrue(source.contains("timeoutAfterMs = BREAK_OVER_TIMEOUT_MS"))
    }

    @Test
    fun batteryRepostIsLatchGated() {
        // Gate completeness: a battery re-post must never revive
        // the stored full card while the latch is down.
        val source = shieldSource()
        val fnIdx = source.indexOf("fun refreshBatteryNotification()")
        assertTrue(fnIdx >= 0)
        val window = source.substring(fnIdx, minOf(fnIdx + 900, source.length))
        assertTrue(window.contains("if (!protecting) return"))
    }

    @Test
    fun exceptionPathReplacesTheStaleFullCard() {
        // Gate completeness: the loop's exception demote posts
        // the arming card over the last full card, in the catch.
        val source = shieldSource()
        val catchIdx = source.indexOf("monitor iteration \$iteration failed")
        assertTrue(catchIdx >= 0)
        val window = source.substring(catchIdx, minOf(catchIdx + 1200, source.length))
        assertTrue(
            window.contains(
                "updateNotification(\"Monolith\", \"Arming shield\\u2026\")"
            )
        )
    }

    @Test
    fun localStopRefreshesTheWidget() {
        // The local-stop fallback is a shield-down moment: the
        // widget refresh fires there exactly as on STOP dispatch.
        val source = shieldSource()
        val fnIdx = source.indexOf("fun requestLocalStop()")
        assertTrue(fnIdx >= 0)
        val window = source.substring(fnIdx, minOf(fnIdx + 800, source.length))
        assertTrue(
            window.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
    }

    @Test
    fun onDestroyRefreshesTheWidget() {
        // A destroyed shield is a shield-down moment: the widget
        // refresh fires in onDestroy exactly as on STOP dispatch,
        // so a stale SEALED cannot outlive the service.
        val source = shieldSource()
        val fnIdx = source.indexOf("override fun onDestroy()")
        assertTrue(fnIdx >= 0)
        val window = source.substring(fnIdx, minOf(fnIdx + 900, source.length))
        assertTrue(
            window.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
    }

    @Test
    fun missingGrantWarningAppendsAndNamesTheCost() {
        // IQ-2 M-8: the warning appends to the normal bigText,
        // the usage wording names the real cost, and the counter
        // is "urges stopped" in the subtitle scope too.
        val source = shieldSource()
        assertTrue(source.contains("append(bigText ?: subtitle)"))
        assertTrue(!source.contains("detector degraded"))
        assertTrue(
            source.contains(
                "\"Usage access (off \u2014 Monolith may miss app launches)\""
            )
        )
    }

    @Test
    fun completionSummonsUsesTheLedgerLine() {
        // IQ-5 F-1: both completion cards tell one story — the
        // summons carries the shield card's Ledger line, not the
        // Deck line main's #164 cure already corrected.
        val source = shieldSource()
        assertTrue(
            source.contains(
                "\"Your session has ended. Your receipt is waiting in the Ledger.\""
            )
        )
        assertTrue(!source.contains("receipt is on the Deck"))
    }

    @Test
    fun lockScreenPublicVersionMatchesTheCardState() {
        // IQ-2 M-1: the lock-screen public version is built from
        // the card's own state — not ongoing with the same
        // timeout on the completion card, the non-claim line
        // while arming, the protection claim only on the
        // standing card.
        val source = shieldSource()
        assertTrue(source.contains("!ongoing -> \"Session complete\""))
        assertTrue(source.contains("\"Monolith session in progress\""))
        assertTrue(source.contains(".setOngoing(ongoing)"))
        assertTrue(
            source.contains("if (timeoutAfterMs > 0) setTimeoutAfter(timeoutAfterMs)")
        )
    }
}
