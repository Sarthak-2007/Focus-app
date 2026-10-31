package com.example

import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Issue #155 (IQ-3 reseat consult 6537600954, re-close bench) —
 * pins for the shield card's lock-screen line during a Safe Break.
 *
 * Before this cure: buildNotification's public-version chain had
 * no break branch. A standing card mid-break — protecting witness
 * up, no grant missing, ongoing — fell through to the else and
 * printed "Focus session protected" on the lock screen while PEEK
 * apps were lawfully open: a whole claim for a partial seal (the
 * T5 residue routed to the re-close bench).
 *
 * The decision now lives in MonolithShieldService.shieldPublicText,
 * pure, and this test pins its whole state table. The wiring —
 * the loop's break branch and the battery re-post threading the
 * explicit inSafeBreak into the builder — is pinned by source
 * shape in Issue155Iq4LifecycleTest (the Robolectric harness
 * cannot stand the grant probe's witnesses up, so the break
 * branch is not reachable there; the table itself is pinned
 * here, where it can be proven exhaustively).
 */
class Issue155PublicTextTest {

    private fun text(
        ongoing: Boolean = true,
        arming: Boolean = false,
        protecting: Boolean = true,
        missingCount: Int = 0,
        inSafeBreak: Boolean = false
    ): String = MonolithShieldService.shieldPublicText(
        ongoing, arming, protecting, missingCount, inSafeBreak
    )

    @Test
    fun standingProvenCardEarnsTheProtectionClaim() {
        assertEquals("Focus session protected", text())
    }

    @Test
    fun safeBreakPrintsTheBreakLineNeverTheProtectionClaim() {
        val line = text(inSafeBreak = true)
        assertEquals("Monolith \u2014 on a break \u00b7 peek apps open", line)
        assertFalse(
            "a break is a partial seal; its lock-screen line must not claim protection",
            line.contains("protected")
        )
    }

    @Test
    fun breakLineYieldsToEveryNonClaimGate() {
        // Arming, unproven, or grant-missing states lead with the
        // non-claim line even mid-break: the break branch sits
        // behind every truth gate, directly before the claim.
        assertEquals(
            "Monolith session in progress",
            text(arming = true, inSafeBreak = true)
        )
        assertEquals(
            "Monolith session in progress",
            text(protecting = false, inSafeBreak = true)
        )
        assertEquals(
            "Monolith session in progress",
            text(missingCount = 1, inSafeBreak = true)
        )
    }

    @Test
    fun completionLineWinsOverTheBreak() {
        // A detached completion card is not ongoing; the break it
        // may have ended inside does not rename its public face.
        assertEquals(
            "Session complete",
            text(ongoing = false, inSafeBreak = true)
        )
    }
}
