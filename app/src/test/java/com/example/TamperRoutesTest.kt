package com.example

import com.example.data.ClearDataWitness
import com.example.data.ComponentGuard
import com.example.data.RebootDrift
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #81 (Audit R2 · tamper-routes): the pure verdicts behind
 * findings 1, 3 and 5. Android-facing halves (MediaStore witness,
 * PackageManager reads, shield-loop wiring) are thin adapters over
 * these — the decisions themselves are pinned here.
 */
class TamperRoutesTest {

    // ---------- Finding 3: RebootDrift (Safe Mode) ----------

    @Test
    fun plainRebootStaysClean() {
        // 30 min of session, ~2 min of boot time unaccounted for.
        assertFalse(RebootDrift.isTamper(1800 + 120, 1800, hmacValid = true))
    }

    @Test
    fun safeModeHoursRegisterAsTamper() {
        // The audit's shape: the monotonic rebuild credits only the
        // pre-reboot 30 min + seconds since the return boot, while
        // 3 wall hours passed — most of them in Safe Mode, where the
        // blocked system apps run and Monolith cannot.
        assertTrue(RebootDrift.isTamper(1800 + 3 * 3600, 1800 + 60, hmacValid = true))
    }

    @Test
    fun poweredOffOvernightRegistersAsTamper() {
        // Unexplained wall time is unexplained, whatever the cause:
        // the session was sealed, and a night of wall time passed
        // with no monotonic witness.
        assertTrue(RebootDrift.isTamper(1800 + 8 * 3600, 1800, hmacValid = true))
    }

    @Test
    fun driftExactlyAtToleranceStaysClean() {
        assertFalse(
            RebootDrift.isTamper(
                1800 + RebootDrift.TOLERANCE_SECONDS, 1800, hmacValid = true
            )
        )
    }

    @Test
    fun brokenHmacIsTamperRegardlessOfClocks() {
        assertTrue(RebootDrift.isTamper(1800, 1800, hmacValid = false))
    }

    // ---------- Finding 5: ComponentGuard (pm disable) ----------

    @Test
    fun allDefaultOrEnabledComponentsPass() {
        val states = mapOf(
            "MonolithShieldService" to 0,      // DEFAULT
            "MonolithAccessibilityService" to 1 // ENABLED
        )
        assertTrue(ComponentGuard.allEnabled(states))
        assertEquals(emptyList<String>(), ComponentGuard.disabledCritical(states))
    }

    @Test
    fun disabledStatesAreNamed() {
        val states = mapOf(
            "MonolithShieldService" to 2,        // DISABLED
            "MonolithBootReceiver" to 3,         // DISABLED_USER
            "MonolithNotificationListener" to 4, // DISABLED_UNTIL_USED
            "MonolithAccessibilityService" to 1
        )
        assertEquals(
            listOf(
                "MonolithBootReceiver",
                "MonolithNotificationListener",
                "MonolithShieldService"
            ),
            ComponentGuard.disabledCritical(states)
        )
        assertFalse(ComponentGuard.allEnabled(states))
    }

    // ---------- Finding 1: ClearDataWitness ----------

    @Test
    fun witnessPayloadRoundTrips() {
        val payload = ClearDataWitness.encode(42, 1_700_000_000_000L)
        assertEquals(42, ClearDataWitness.parseSessionId(payload))
    }

    @Test
    fun witnessPayloadRejectsGarbage() {
        assertNull(ClearDataWitness.parseSessionId(null))
        assertNull(ClearDataWitness.parseSessionId(""))
        assertNull(ClearDataWitness.parseSessionId("MNL1|abc|123"))
        assertNull(ClearDataWitness.parseSessionId("MNL1|0|123"))
        assertNull(ClearDataWitness.parseSessionId("MNL1|42|0"))
        assertNull(ClearDataWitness.parseSessionId("OTHER|42|123"))
        assertNull(ClearDataWitness.parseSessionId("MNL1|42"))
    }

    @Test
    fun resurrectionNeedsWitnessAndEmptySessionTable() {
        assertTrue(ClearDataWitness.isResurrection(42, sessionRowCount = 0))
        assertFalse(ClearDataWitness.isResurrection(42, sessionRowCount = 3))
        assertFalse(ClearDataWitness.isResurrection(null, sessionRowCount = 0))
    }
}
