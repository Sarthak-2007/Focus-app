package com.example

import com.example.ui.components.SealBarParams
import com.example.ui.components.SealBarState
import com.example.ui.components.endsAtNumeralsFor
import com.example.ui.components.sealBarStateFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #4 — the seal bar's derivation table. The inputs are the
 * retired proof strip's own vocabulary: blockCell's word ("ON" /
 * "OFF" / "UNKNOWN", already freshness-checked at idle and already
 * carrying the live-OFF override while running) and the session's
 * spanRemainingSeconds — the same span the strip's TIME cell
 * priced. These tests pin the re-encoding into position + light;
 * the rendering itself is a device-class check.
 */
class SealBarStateTest {

    @Test
    fun idleOnIsBlockingIdleOffIsOpenIdleUnknownIsHalfSet() {
        assertEquals(
            SealBarState.BLOCKING,
            sealBarStateFor(isSessionRunning = false, blockWord = "ON", remainingSeconds = null)
        )
        assertEquals(
            SealBarState.OFF,
            sealBarStateFor(isSessionRunning = false, blockWord = "OFF", remainingSeconds = null)
        )
        assertEquals(
            SealBarState.UNPROVEN,
            sealBarStateFor(isSessionRunning = false, blockWord = "UNKNOWN", remainingSeconds = null)
        )
    }

    @Test
    fun runningOnIsRunningWithNumeralsState() {
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = 3600)
        )
        // Stopwatch: no remaining span exists, and the state must
        // not fall into ENDING SOON over a null.
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = null)
        )
    }

    @Test
    fun endingSoonStartsExactlyAtTheThreshold() {
        val t = SealBarParams.endingSoonThresholdSeconds
        assertEquals(
            SealBarState.ENDING_SOON,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = t)
        )
        assertEquals(
            SealBarState.RUNNING,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = t + 1)
        )
        // The last second of the span is still ending soon, never
        // already off — the session owns its end.
        assertEquals(
            SealBarState.ENDING_SOON,
            sealBarStateFor(isSessionRunning = true, blockWord = "ON", remainingSeconds = 0)
        )
    }

    @Test
    fun runningLiveOffOpensTheSealWhateverTheFreezeSaid() {
        // UNA2-04 carried into the encoding: while running, the
        // blockCell word already follows the live wire's OFF over
        // the frozen verdict — the seal opens with it.
        assertEquals(
            SealBarState.OFF,
            sealBarStateFor(isSessionRunning = true, blockWord = "OFF", remainingSeconds = 3600)
        )
    }

    @Test
    fun runningUnknownKeepsThePlateShutAndTheLampDark() {
        assertEquals(
            SealBarState.RUNNING_UNPROVEN,
            sealBarStateFor(isSessionRunning = true, blockWord = "UNKNOWN", remainingSeconds = 3600)
        )
        // An unproven bind never earns ENDING SOON's lit alert —
        // the threshold applies to the proven branch only.
        assertEquals(
            SealBarState.RUNNING_UNPROVEN,
            sealBarStateFor(isSessionRunning = true, blockWord = "UNKNOWN", remainingSeconds = 10)
        )
    }

    @Test
    fun caughtIsOneThirdNeverHalf() {
        // THE BOLT §3: ½ reads as a resting state; ⅓ is the caught pose.
        assertEquals(1f / 3f, SealBarParams.plateUnprovenFraction, 0.0001f)
    }

    @Test
    fun jewelStaysInsideTheGlanceBudgetCap() {
        // THE BOLT §6: jewel ≤9dp is the enforceable proxy for the
        // ≤41.9 luminous budget; the budget ships as a named constant.
        assertTrue(SealBarParams.lampDiameter.value <= 9f)
        assertEquals(41.9f, SealBarParams.jewelLuminousBudget, 0.0001f)
    }

    @Test
    fun warningOrangeIsTheOneModeInvariantHue() {
        // THE BOLT §5: #D63E06 in both modes; #FF8A5C never returns.
        assertEquals(0xFFD63E06, SealBarParams.warningOrange.value.toLong())
    }

    @Test
    fun numeralsComeOnlyFromAnEndsPrint() {
        assertEquals("14:30", endsAtNumeralsFor("ENDS 14:30"))
        assertNull(endsAtNumeralsFor("NO END"))
        assertNull(endsAtNumeralsFor("INVALID"))
        assertNull(endsAtNumeralsFor(""))
    }
}
