package com.example

import com.example.service.ShieldWatchdog
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #323 (Audit R5 — YPT head-to-head, F3): the shield
 * watchdog's decision matrix. Before the cure, a shield service
 * killed mid-session (OEM battery killer, OOM, an unhonoured
 * START_STICKY) stayed dead until the user next opened the app,
 * while the session row still claimed protection. The watchdog
 * law is one line: restart only a shield that died under a live
 * session — every other state is already correct and must not
 * be disturbed.
 */
class ShieldWatchdogTest {

    @Test
    fun `live session with a dead shield restarts it`() {
        assertTrue(ShieldWatchdog.shouldRestartShield(hasActiveSession = true, shieldPresent = false))
    }

    @Test
    fun `live session with a standing shield is left alone`() {
        assertFalse(ShieldWatchdog.shouldRestartShield(hasActiveSession = true, shieldPresent = true))
    }

    @Test
    fun `no session means a stopped shield is the correct state`() {
        assertFalse(ShieldWatchdog.shouldRestartShield(hasActiveSession = false, shieldPresent = false))
        assertFalse(ShieldWatchdog.shouldRestartShield(hasActiveSession = false, shieldPresent = true))
    }
}
