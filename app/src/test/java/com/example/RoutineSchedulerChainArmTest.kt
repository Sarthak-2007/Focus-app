package com.example

import com.example.service.RoutineScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #46 (routine-scheduler audit): the chain alarm must fire
 * EXACTLY at user-asked routine boundaries.
 *
 * Plain-JUnit half: the pure arm-mode decision truth table and the
 * pure result mapping — no Android runtime needed, mirroring
 * RoutineDeriveTest's style. Source-tripwire half: the exact-arm
 * machinery must actually be present in the shipped sources (a pure
 * helper nobody calls would pass the truth table and cure nothing).
 */
class RoutineSchedulerChainArmTest {

    // --- Arm-mode decision truth table ----------------------------------

    @Test
    fun armMode_preS_needsNoPermission_alwaysExact() {
        // Before API 31 exact alarms required no permission, so a
        // "cannot schedule exact" answer must never degrade them.
        assertEquals(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT,
            RoutineScheduler.armMode(sdkInt = 24, canScheduleExactAlarms = false)
        )
        assertEquals(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT,
            RoutineScheduler.armMode(sdkInt = 30, canScheduleExactAlarms = false)
        )
        assertEquals(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT,
            RoutineScheduler.armMode(sdkInt = 30, canScheduleExactAlarms = true)
        )
    }

    @Test
    fun armMode_fromS_exactOnlyWhenPlatformAllows() {
        assertEquals(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT,
            RoutineScheduler.armMode(sdkInt = 31, canScheduleExactAlarms = true)
        )
        assertEquals(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT,
            RoutineScheduler.armMode(sdkInt = 36, canScheduleExactAlarms = true)
        )
    }

    @Test
    fun armMode_fromS_deniedExact_degradesToInexact_neverExact() {
        assertEquals(
            RoutineScheduler.ArmMode.ALLOW_WHILE_IDLE_INEXACT,
            RoutineScheduler.armMode(sdkInt = 31, canScheduleExactAlarms = false)
        )
        assertEquals(
            RoutineScheduler.ArmMode.ALLOW_WHILE_IDLE_INEXACT,
            RoutineScheduler.armMode(sdkInt = 36, canScheduleExactAlarms = false)
        )
    }

    // --- Result mapping ---------------------------------------------------

    @Test
    fun chainArmResult_exactMode_isArmedExactAtBoundary() {
        val result = RoutineScheduler.chainArmResult(
            RoutineScheduler.ArmMode.ALARM_CLOCK_EXACT, boundaryMillis = 1_234_567L
        )
        assertEquals(RoutineScheduler.ChainArmResult.ArmedExact(1_234_567L), result)
    }

    @Test
    fun chainArmResult_inexactMode_isArmedInexact_andSaysDegraded() {
        val result = RoutineScheduler.chainArmResult(
            RoutineScheduler.ArmMode.ALLOW_WHILE_IDLE_INEXACT, boundaryMillis = 1_234_567L
        )
        assertTrue(result is RoutineScheduler.ChainArmResult.ArmedInexact)
        result as RoutineScheduler.ChainArmResult.ArmedInexact
        assertEquals(1_234_567L, result.boundaryMillis)
        assertTrue(
            "a degraded arm must name itself DEGRADED, was: ${result.reason}",
            result.reason.contains("DEGRADED")
        )
    }

    @Test
    fun chainArmResult_inexactMode_preservesCallersReason() {
        val result = RoutineScheduler.chainArmResult(
            RoutineScheduler.ArmMode.ALLOW_WHILE_IDLE_INEXACT,
            boundaryMillis = 42L,
            inexactReason = "DEGRADED: setAlarmClock denied in test"
        )
        assertEquals(
            RoutineScheduler.ChainArmResult.ArmedInexact(42L, "DEGRADED: setAlarmClock denied in test"),
            result
        )
    }
}

/**
 * Source tripwires for issue #46 — the cures must exist in the shipped
 * sources, in the shipped order (heartbeat rearm FIRST).
 */
class RoutineSchedulerSourceTripwireTest {

    private fun locate(relativeFromMain: String): File {
        val candidates = listOf(
            File("src/main/$relativeFromMain"),
            File("app/src/main/$relativeFromMain")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relativeFromMain from ${File(".").absolutePath}")
    }

    private fun schedulerSource(): String =
        locate("java/com/example/service/RoutineScheduler.kt").readText()

    private fun heartbeatSource(): String =
        locate("java/com/example/service/RoutineHeartbeat.kt").readText()

    private fun receiverSource(): String =
        locate("java/com/example/service/RoutineAlarmReceiver.kt").readText()

    @Test
    fun scheduler_armsWithSetAlarmClock_andChecksExactCapability() {
        val source = schedulerSource()
        assertTrue("chain must be armed via setAlarmClock", source.contains("setAlarmClock"))
        assertTrue("alarm clock info must carry the boundary", source.contains("AlarmClockInfo"))
        assertTrue(
            "exact capability must be gated via canScheduleExactAlarms",
            source.contains("canScheduleExactAlarms")
        )
    }

    @Test
    fun manifest_declaresScheduleExactAlarm() {
        val manifest = locate("AndroidManifest.xml").readText()
        assertTrue(
            "manifest must declare SCHEDULE_EXACT_ALARM",
            manifest.contains("android.permission.SCHEDULE_EXACT_ALARM")
        )
    }

    @Test
    fun scheduler_neverFailsSilently_failuresReturnFailedAndLog() {
        val source = schedulerSource()
        assertTrue("failed arms must be a ChainArmResult.Failed", source.contains("ChainArmResult.Failed"))
        assertTrue("failed arms must be logged at error level", source.contains("Log.e"))
        assertTrue(
            "the inexact fallback must still exist for denied exact alarms",
            source.contains("setAndAllowWhileIdle")
        )
    }

    @Test
    fun heartbeat_rearmsChainBeforeFallibleReconcileWork() {
        val source = heartbeatSource()
        val rearmFirst = source.indexOf("RoutineDerive.nextBoundaryMillis(routines, now)")
        val derive = source.indexOf("RoutineDerive.derive(")
        assertTrue("heartbeat must compute the early boundary", rearmFirst >= 0)
        assertTrue("heartbeat must derive", derive >= 0)
        assertTrue(
            "the chain rearm must happen BEFORE derive()/session reconcile work",
            rearmFirst < derive
        )
    }

    @Test
    fun heartbeat_recordsArmedBoundaryOnlyOnArmedResults() {
        val source = heartbeatSource()
        assertTrue(
            "heartbeat must handle the exact armed result",
            source.contains("ChainArmResult.ArmedExact")
        )
        assertTrue(
            "heartbeat must handle degraded arms distinctly",
            source.contains("ChainArmResult.ArmedInexact")
        )
        assertTrue(
            "heartbeat must handle failed arms without believing them",
            source.contains("ChainArmResult.Failed")
        )
    }

    @Test
    fun receiver_isCrashSafe_supervisorHandlerAndRethrownCancellation() {
        val source = receiverSource()
        assertTrue("receiver scope must be supervised", source.contains("SupervisorJob"))
        assertTrue(
            "receiver scope must carry a CoroutineExceptionHandler",
            source.contains("CoroutineExceptionHandler")
        )
        assertTrue(
            "CancellationException must be rethrown, never swallowed",
            source.contains("CancellationException")
        )
    }
}
