package com.example

import com.example.service.ForegroundSelection
import com.example.service.ShieldRestartPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #43, findings 2 + 4 — the pure laws behind the shield's
 * new foreground detection and restart policy:
 *
 *  - a real accessibility foreground witness beats the UsageStats
 *    guess, and the guess is not even consulted while a witness
 *    exists (a recency guess must never override a known
 *    foreground, or it intercepts apps the user already left);
 *  - UsageStats remains the fallback only when no witness exists;
 *  - a restart is scheduled on task removal only while the shield
 *    was protecting.
 */
class ForegroundSelectionTest {

    private val blocked = setOf("com.example.blocked", "com.example.otherblocked")

    @Test
    fun witnessPresentAndBlocked_returnsWitness_fallbackNotConsulted() {
        var fallbackCalls = 0
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = "com.example.blocked",
            blockedPackages = blocked,
            usageStatsFallback = { fallbackCalls++; "com.example.otherblocked" }
        )
        assertEquals("com.example.blocked", result)
        assertEquals(0, fallbackCalls)
    }

    @Test
    fun witnessPresentAndNotBlocked_returnsNull_fallbackNotConsulted() {
        // The known foreground is an allowed app. The UsageStats
        // guess naming a recently-used blocked app must NOT fire:
        // the user already left that app.
        var fallbackCalls = 0
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = "com.example.allowed",
            blockedPackages = blocked,
            usageStatsFallback = { fallbackCalls++; "com.example.blocked" }
        )
        assertNull(result)
        assertEquals(0, fallbackCalls)
    }

    @Test
    fun witnessPresentIsOwnPackage_returnsNull() {
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = "com.example",
            blockedPackages = blocked,
            usageStatsFallback = { "com.example.blocked" }
        )
        assertNull(result)
    }

    @Test
    fun noWitness_fallbackBlocked_returnsFallback() {
        var fallbackCalls = 0
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = null,
            blockedPackages = blocked,
            usageStatsFallback = { fallbackCalls++; "com.example.otherblocked" }
        )
        assertEquals("com.example.otherblocked", result)
        assertEquals(1, fallbackCalls)
    }

    @Test
    fun noWitness_fallbackNotBlocked_returnsNull() {
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = null,
            blockedPackages = blocked,
            usageStatsFallback = { "com.example.allowed" }
        )
        assertNull(result)
    }

    @Test
    fun noWitness_fallbackNull_returnsNull() {
        val result = ForegroundSelection.selectBlockedForeground(
            accessibilityForeground = null,
            blockedPackages = blocked,
            usageStatsFallback = { null }
        )
        assertNull(result)
    }

    @Test
    fun emptyBlockedSet_neverReturnsAnything() {
        assertNull(
            ForegroundSelection.selectBlockedForeground(
                accessibilityForeground = "com.example.blocked",
                blockedPackages = emptySet(),
                usageStatsFallback = { "com.example.blocked" }
            )
        )
        assertNull(
            ForegroundSelection.selectBlockedForeground(
                accessibilityForeground = null,
                blockedPackages = emptySet(),
                usageStatsFallback = { "com.example.blocked" }
            )
        )
    }

    // Issue #520 (5-D): the shield selection's call-owner
    // exemption — one call truth, both doors. The package the
    // a11y door locked as the live call's owner is never
    // selected; a null owner exempts nothing.
    // PIN-SCOPE: PLUMBING (pure selection helper).

    @Test
    fun callOwnerExempt_ownerPackage_isExempt() {
        assertTrue(
            ForegroundSelection.isCallOwnerExempt("com.whatsapp", "com.whatsapp")
        )
    }

    @Test
    fun callOwnerExempt_otherPackageDuringCall_notExempt() {
        assertFalse(
            ForegroundSelection.isCallOwnerExempt("com.example.blocked", "com.whatsapp")
        )
    }

    @Test
    fun callOwnerExempt_noOwner_notExempt() {
        assertFalse(
            ForegroundSelection.isCallOwnerExempt("com.whatsapp", null)
        )
    }

    @Test
    fun restartPolicy_onlyWhenProtecting_andNearImmediate() {
        assertTrue(ShieldRestartPolicy.shouldScheduleRestart(wasProtecting = true))
        assertFalse(ShieldRestartPolicy.shouldScheduleRestart(wasProtecting = false))
        // Near-immediate means seconds, not minutes.
        assertTrue(ShieldRestartPolicy.RESTART_DELAY_MS in 1L..5_000L)
    }
}
