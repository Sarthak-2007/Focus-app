package com.example

import com.example.data.AppBlockTier
import com.example.data.SessionTierGate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #304 F1 (Audit R5, AppBlock head-to-head): the one-way
 * ratchet's direction gate, proven as pure logic — strictness is a
 * total order, loosening is exactly a move down it, and the sealed
 * clamp never lets a write land looser than the current tier while
 * never blocking a tightening or an unsealed write.
 */
class SessionTierGateTest {

    @Test
    fun strictnessIsTotalOrder() {
        assertTrue(SessionTierGate.strictness(AppBlockTier.LOCKED) >
            SessionTierGate.strictness(AppBlockTier.PEEK_2M))
        assertTrue(SessionTierGate.strictness(AppBlockTier.PEEK_2M) >
            SessionTierGate.strictness(AppBlockTier.OPEN))
    }

    @Test
    fun loosensOnlyDownward() {
        assertTrue(SessionTierGate.loosens(AppBlockTier.LOCKED, AppBlockTier.PEEK_2M))
        assertTrue(SessionTierGate.loosens(AppBlockTier.LOCKED, AppBlockTier.OPEN))
        assertTrue(SessionTierGate.loosens(AppBlockTier.PEEK_2M, AppBlockTier.OPEN))
        assertFalse(SessionTierGate.loosens(AppBlockTier.OPEN, AppBlockTier.PEEK_2M))
        assertFalse(SessionTierGate.loosens(AppBlockTier.PEEK_2M, AppBlockTier.LOCKED))
        assertFalse(SessionTierGate.loosens(AppBlockTier.OPEN, AppBlockTier.LOCKED))
        for (tier in AppBlockTier.entries) {
            assertFalse("same tier never loosens", SessionTierGate.loosens(tier, tier))
        }
    }

    @Test
    fun sealedClampRefusesEveryLoosening() {
        for (from in AppBlockTier.entries) {
            for (to in AppBlockTier.entries) {
                val landed = SessionTierGate.clampWhileSealed(from, to, sealed = true)
                assertTrue(
                    "sealed write $from -> $to landed looser at $landed",
                    SessionTierGate.strictness(landed) >= SessionTierGate.strictness(from)
                )
                if (!SessionTierGate.loosens(from, to)) {
                    assertEquals("tightening/equal writes land whole", to, landed)
                } else {
                    assertEquals("loosening clamps to current", from, landed)
                }
            }
        }
    }

    @Test
    fun unsealedWritesAlwaysLand() {
        for (from in AppBlockTier.entries) {
            for (to in AppBlockTier.entries) {
                assertEquals(to, SessionTierGate.clampWhileSealed(from, to, sealed = false))
            }
        }
    }
}
