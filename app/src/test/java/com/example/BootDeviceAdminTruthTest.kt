package com.example

import android.content.Intent
import com.example.service.RoutineHeartbeat
import com.example.service.bootHeartbeatSource
import com.example.service.bootStampDue
import com.example.service.deviceAdminDisableMessage
import com.example.service.runGuardedBootAction
import com.example.service.shouldRaiseBootShield
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Issue #44 (audit R1, boot-deviceadmin) — the five findings' tests.
 * Every test states the world AFTER its cure and would have failed
 * (or failed to compile) in the world before it: the disable message
 * and the boot helpers did not exist, limit-password was declared,
 * the fix pane used a hardcoded Settings component, and boot work
 * ran in a bare uncaught coroutine.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BootDeviceAdminTruthTest {

    // ---------------------------------------------------------------
    // Finding 1 — the disable-request message tells the truth and
    // is session-aware. Before the cure it always claimed an active
    // session was being "protected", session or not.
    // ---------------------------------------------------------------

    @Test
    fun disableMessage_activeSession_warnsAndStatesTheLimit() {
        val message = deviceAdminDisableMessage(sessionActive = true)
        assertTrue(message.contains("A Monolith focus session is active"))
        assertTrue(message.contains("Android still allows you to deactivate"))
        assertTrue(message.contains("does not prevent deactivation or uninstall"))
        assertTrue(message.contains("only adds friction and this warning"))
    }

    @Test
    fun disableMessage_noSession_saysSoAndStatesTheSameLimit() {
        val message = deviceAdminDisableMessage(sessionActive = false)
        assertTrue(message.contains("No active Monolith focus session"))
        assertTrue(message.contains("Android still allows you to deactivate"))
        assertTrue(message.contains("does not prevent deactivation or uninstall"))
        assertTrue(message.contains("only adds friction and this warning"))
    }

    @Test
    fun disableMessage_branchesDifferAndNeitherClaimsPrevention() {
        val active = deviceAdminDisableMessage(sessionActive = true)
        val idle = deviceAdminDisableMessage(sessionActive = false)
        assertNotEquals(active, idle)
        for (message in listOf(active, idle)) {
            assertFalse(message.contains("prevents"))
            assertFalse(message.contains("cannot deactivate"))
            assertFalse(message.contains("cannot uninstall"))
            assertFalse(message.contains("is protecting your active study session"))
        }
    }

    @Test
    fun disableMessage_receiverIsSessionAwareViaTheWitness() {
        val source = locateMainSource(
            "com/example/service/MonolithDeviceAdminReceiver.kt"
        ).readText()
        assertTrue(
            "onDisableRequested must read the accessibility witness",
            source.contains("MonolithAccessibilityService.armedSessionId")
        )
        assertTrue(source.contains("deviceAdminDisableMessage("))
        assertFalse(
            "the old 'prevent uninstalling' claim is back in the KDoc",
            source.contains("to prevent uninstalling Monolith")
        )
    }

    // ---------------------------------------------------------------
    // Finding 2 — the unused limit-password policy is gone, and the
    // device-admin XML is still a valid device-admin declaration.
    // ---------------------------------------------------------------

    @Test
    fun deviceAdminXml_declaresNoPolicies() {
        val xml = locateMainRes("xml/device_admin_receiver.xml").readText()
        assertFalse(
            "limit-password is back in device_admin_receiver.xml",
            xml.contains("limit-password")
        )
        assertFalse(xml.contains("<uses-policies"))
        assertTrue(xml.contains("<device-admin"))
    }

    @Test
    fun deviceAdminXml_noPolicyReturnsAnywhereInTheTree() {
        val manifest = locateMainFile("AndroidManifest.xml").readText()
        assertFalse(manifest.contains("limit-password"))
        assertFalse(manifest.contains("Monolith Uninstall Guard"))
    }

    // ---------------------------------------------------------------
    // Finding 3 — device admin is requested through the official
    // intent, and the receiver carries its own honest description.
    // ---------------------------------------------------------------

    @Test
    fun fixPane_requestsDeviceAdminThroughTheOfficialIntent() {
        val source = locateMainSource("com/example/MainActivity.kt").readText()
        assertTrue(source.contains("DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN"))
        assertTrue(source.contains("DevicePolicyManager.EXTRA_DEVICE_ADMIN"))
        assertTrue(source.contains("DevicePolicyManager.EXTRA_ADD_EXPLANATION"))
        assertTrue(source.contains("MonolithDeviceAdminReceiver::class.java"))
        assertFalse(
            "the hardcoded Settings component is back in the fix pane",
            source.contains("com.android.settings.DeviceAdminSettings")
        )
        // The official intent is fired first; Security Settings is
        // only the fallback after it.
        val official = source.indexOf("ACTION_ADD_DEVICE_ADMIN")
        val fallback = source.indexOf("ACTION_SECURITY_SETTINGS")
        assertTrue("official intent must precede the fallback", official in 0 until fallback)
    }

    @Test
    fun manifest_deviceAdminReceiverHasItsOwnDescription() {
        val manifest = locateMainFile("AndroidManifest.xml").readText()
        val receiverBlock = manifest.substringAfter("MonolithDeviceAdminReceiver")
        assertTrue(receiverBlock.contains("@string/device_admin_description"))
        assertTrue(receiverBlock.contains("@string/device_admin_label"))
        assertFalse(
            "the device admin must not reuse the accessibility description",
            manifest.contains(
                "android:description=\"@string/accessibility_service_description\""
            )
        )
    }

    // ---------------------------------------------------------------
    // Finding 4 — boot helpers: shield decision, stamp rule, source
    // rule, and the guarded-group contract.
    // ---------------------------------------------------------------

    @Test
    fun shouldRaiseBootShield_truthTable() {
        assertEquals(true, shouldRaiseBootShield(hasSessionSnapshot = true, sessionIsActive = true))
        assertEquals(false, shouldRaiseBootShield(hasSessionSnapshot = true, sessionIsActive = false))
        assertEquals(false, shouldRaiseBootShield(hasSessionSnapshot = false, sessionIsActive = true))
        assertEquals(false, shouldRaiseBootShield(hasSessionSnapshot = false, sessionIsActive = false))
    }

    @Test
    fun bootStampDue_onlyLockedBootAndQuickboot() {
        assertTrue(bootStampDue(Intent.ACTION_LOCKED_BOOT_COMPLETED))
        assertTrue(bootStampDue("android.intent.action.QUICKBOOT_POWERON"))
        assertFalse(bootStampDue(Intent.ACTION_BOOT_COMPLETED))
        assertFalse(bootStampDue(Intent.ACTION_MY_PACKAGE_REPLACED))
        assertFalse(bootStampDue(Intent.ACTION_TIME_CHANGED))
        assertFalse(bootStampDue(null))
    }

    @Test
    fun bootHeartbeatSource_packageReplacedIsItsOwnSource() {
        assertEquals(
            RoutineHeartbeat.Source.PACKAGE_REPLACED,
            bootHeartbeatSource(Intent.ACTION_MY_PACKAGE_REPLACED)
        )
        assertEquals(
            RoutineHeartbeat.Source.BOOT,
            bootHeartbeatSource(Intent.ACTION_BOOT_COMPLETED)
        )
        assertEquals(
            RoutineHeartbeat.Source.BOOT,
            bootHeartbeatSource(Intent.ACTION_LOCKED_BOOT_COMPLETED)
        )
        assertEquals(RoutineHeartbeat.Source.BOOT, bootHeartbeatSource(null))
    }

    @Test
    fun guardedBootAction_aFailureIsContainedAndLaterGroupsStillRun() = runBlocking {
        val ran = mutableListOf<String>()
        runGuardedBootAction { ran += "first" }
        runGuardedBootAction { throw IllegalStateException("boom") }
        runGuardedBootAction { ran += "after-failure" }
        assertEquals(listOf("first", "after-failure"), ran)
    }

    @Test
    fun guardedBootAction_aFailureIsReportedByName_neverSwallowed() = runBlocking {
        // PR CHECK (Tyler's review of the #44 landing): containment
        // must not be silence. The failing group is reported, by
        // name, exactly once; the healthy groups report nothing.
        val reports = mutableListOf<Pair<String, String>>()
        val reporter: (String, Exception) -> Unit = { name, failure ->
            reports += name to (failure.message ?: "")
        }
        runGuardedBootAction("first", reporter) { }
        runGuardedBootAction("boom-group", reporter) { throw IllegalStateException("boom") }
        runGuardedBootAction("after", reporter) { }
        assertEquals(listOf("boom-group" to "boom"), reports)
    }

    @Test
    fun guardedBootAction_cancellationIsRethrown() = runBlocking {
        try {
            runGuardedBootAction { throw CancellationException("cancelled") }
            fail("CancellationException must be rethrown, not contained")
        } catch (expected: CancellationException) {
            // Expected: cancellation is not a containable failure.
        }
    }

    @Test
    fun bootReceiver_shieldRunsBeforeTheTickAndAgainAfter() {
        val source = locateMainSource(
            "com/example/service/MonolithBootReceiver.kt"
        ).readText()
        assertTrue(source.contains("SupervisorJob"))
        assertTrue(source.contains("CoroutineExceptionHandler"))
        val firstShield = source.indexOf("MonolithShieldService.startShield")
        val tick = source.indexOf("RoutineHeartbeat.tick(appContext, bootHeartbeatSource(action)")
        val lastShield = source.lastIndexOf("MonolithShieldService.startShield")
        assertTrue("a shield raise must precede the tick", firstShield in 0 until tick)
        assertTrue("a shield raise must follow the tick", lastShield > tick)
        // Exactly one finish per goAsync branch, each in a finally.
        // The counts are branch-relative, not absolute (#166, K8's
        // seventh sitting): the receiver grew from two goAsync
        // branches to four (quickboot/boot group, reboot,
        // timezone, package-changed) and the hardcoded 2 went red
        // on a receiver that honours the law in every branch —
        // census #5: expected:<2> but was:<4>. The law is the
        // invariant: finishes == branches == finallies, so the
        // next honest branch arrives already guarded and a branch
        // that forgets its finish (or its finally) still fails.
        val branches = Regex("goAsync\\(\\)").findAll(source).count()
        assertTrue("the receiver must take goAsync branches", branches > 0)
        assertEquals(
            branches,
            Regex("pendingResult\\.finish\\(\\)").findAll(source).count()
        )
        assertEquals(branches, Regex("finally \\{").findAll(source).count())
    }

    companion object {
        /** Locate a main-source file from the test working directory,
         *  whether tests run from the module dir or the repo root —
         *  same pattern as TruthCar3SweepTest.locateMainSource. */
        fun locateMainSource(relative: String): File = locateMainFile("java/$relative")

        fun locateMainRes(relative: String): File = locateMainFile("res/$relative")

        fun locateMainFile(relative: String): File {
            val candidates = listOf(
                File("src/main/$relative"),
                File("app/src/main/$relative")
            )
            return candidates.firstOrNull { it.exists() }
                ?: error("could not locate $relative from ${File(".").absolutePath}")
        }
    }
}
