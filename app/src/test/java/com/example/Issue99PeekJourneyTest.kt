package com.example

import com.example.data.BlockedAppEntity
import com.example.data.expiredPeekPackages
import com.example.ui.components.peekPillCountdownText
import com.example.ui.components.returnToFocusLive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Issue #99 — Journey OPENING-BLOCKED-APP pure-logic proofs. */
class Issue99PeekJourneyTest {

    private fun app(pkg: String, leaseEnd: Long) = BlockedAppEntity(
        packageName = pkg, appName = pkg, category = "SOCIAL", isBlocked = true,
        activePeekUntilElapsedRealtime = leaseEnd
    )

    @Test fun expiredPeekIsDetectedInsideWindow() {
        val now = 1_000_000L
        assertEquals(setOf("a"), expiredPeekPackages(listOf(app("a", now - 5_000L)), now))
    }

    @Test fun liveLeaseAndNeverLeasedAreNotExpired() {
        val now = 1_000_000L
        assertTrue(expiredPeekPackages(listOf(app("a", now + 5_000L)), now).isEmpty())
        assertTrue(expiredPeekPackages(listOf(app("a", 0L)), now).isEmpty())
    }

    @Test fun ancientLeaseIsHistoryNotEviction() {
        val now = 10_000_000L
        assertTrue(expiredPeekPackages(listOf(app("a", now - 3_600_000L)), now).isEmpty())
    }

    @Test fun pillCountdownFormats() {
        assertEquals("2:00", peekPillCountdownText(120))
        assertEquals("0:07", peekPillCountdownText(7))
        assertEquals("0:00", peekPillCountdownText(-3))
    }

    @Test fun lockedTierReturnWaitsForWall() {
        assertFalse(returnToFocusLive(isLockedTier = true, countdownSecs = 5))
        assertFalse(returnToFocusLive(isLockedTier = true, countdownSecs = 1))
        assertTrue(returnToFocusLive(isLockedTier = true, countdownSecs = 0))
    }

    @Test fun peekTierReturnStaysLive() {
        assertTrue(returnToFocusLive(isLockedTier = false, countdownSecs = 5))
        assertTrue(returnToFocusLive(isLockedTier = false, countdownSecs = 0))
    }
}
