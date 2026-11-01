package com.example

import com.example.service.WatchItemId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #286 (Audit R5 supplement — Forest, platform execution):
 * the verified platform footprint, pinned so the audit record
 * and the code cannot drift apart again.
 *
 *  F1: Monolith DOES run a specialUse foreground service, its
 *     own boot receiver and a device admin — pinned here as
 *     fact. The finding's implied cure (Forest needs none of
 *     these) does not survive Forest's own evidence: Forest's
 *     default plant enforces NOTHING without Deep Focus (issue
 *     #289 W1) — its no-FGS footprint is the footprint of an
 *     honour-system timer. Monolith's FGS is the enforcement
 *     itself (the 2-second shield loop), and its boot receiver
 *     is what re-arms protection before unlock. Removing them
 *     removes the block, which is the product.
 *  F2: the grant surface is exactly the six WatchStore
 *     permission items — pinned at six so a seventh Settings
 *     trip can never sneak in, and the count the audit cites
 *     stays checkable.
 *  F3: the device-admin component exists but its policy file
 *     has declared ZERO policies since the issue #44 truth fix
 *     (no <uses-policies> at all) — it is a disable-warning
 *     friction surface, not an admin power. And like Forest,
 *     Monolith declares no VPN service anywhere.
 *  Timing: routine timing declares the exact-alarm
 *     permission (SCHEDULE_EXACT_ALARM, issues #280 + #319 F2
 *     + #309 F2 — asked only when the user first enables a
 *     routine, never a first-run gate), but the law is
 *     unchanged: alarms are HINTS, never the mechanism. The
 *     one ExactAlarm law probes the grant (useExact) and
 *     fires exact only when it is held, with the inexact
 *     setAndAllowWhileIdle arm as the standing fallback, and
 *     the heartbeat reconcile remains the mechanism either
 *     way. The stronger USE_EXACT_ALARM permission is still
 *     never taken.
 */
class ForestPlatformFootprintTest {

    private fun locateMainFile(relative: String): File {
        val candidates = listOf(
            File("src/main/$relative"),
            File("app/src/main/$relative")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("could not locate $relative from ${File(".").absolutePath}")
    }

    private fun manifest(): String = locateMainFile("AndroidManifest.xml").readText()

    @Test
    fun f1_shieldServiceIsSpecialUseForeground() {
        val m = manifest()
        assertTrue(m.contains("MonolithShieldService"))
        assertTrue(m.contains("android:foregroundServiceType=\"specialUse\""))
        assertTrue(m.contains("FOREGROUND_SERVICE_SPECIAL_USE"))
    }

    @Test
    fun f1_bootReceiverCoversLockedAndPlainBoot() {
        val m = manifest()
        assertTrue(m.contains("MonolithBootReceiver"))
        assertTrue(m.contains("android.intent.action.LOCKED_BOOT_COMPLETED"))
        assertTrue(m.contains("android.intent.action.BOOT_COMPLETED"))
    }

    @Test
    fun f2_grantSurfaceIsExactlyTheSixWatchPermissions() {
        assertEquals(6, WatchItemId.PERMISSIONS.size)
    }

    @Test
    fun f3_deviceAdminDeclaresNoPolicies() {
        val xml = locateMainFile("res/xml/device_admin_receiver.xml").readText()
        assertTrue(xml.contains("<device-admin"))
        assertFalse("a device-admin policy returned", xml.contains("<uses-policies"))
        assertFalse(xml.contains("limit-password"))
        assertFalse(xml.contains("force-lock"))
        assertFalse(xml.contains("wipe-data"))
    }

    @Test
    fun f3_noVpnServiceAnywhere() {
        val m = manifest()
        assertFalse(m.contains("VpnService"))
        assertFalse(m.contains("BIND_VPN_SERVICE"))
    }

    @Test
    fun timing_alarmsAreHintsNeverTheMechanism() {
        val m = manifest()
        // Issue #280 + #319 F2 + #309 F2 (deliberate, on the record
        // in the manifest's own comment at the declaration): the
        // exact-alarm permission IS declared — asked only when the
        // user first enables a routine, never a first-run gate.
        // This assertion flipped from assertFalse on that drift
        // evidence (posted on #166 before this edit, W2 BUILDER-B2);
        // every guard rail below is what keeps the flip honest.
        assertTrue(m.contains("SCHEDULE_EXACT_ALARM"))
        // The stronger, never-ask permission is still not taken —
        // this half of the original pin survives verbatim.
        assertFalse(m.contains("USE_EXACT_ALARM"))
        val scheduler = locateMainFile(
            "java/com/example/service/RoutineScheduler.kt"
        ).readText()
        assertTrue(scheduler.contains("Alarms are HINTS, never the mechanism"))
        assertTrue(scheduler.contains("setAndAllowWhileIdle"))
        // The chain arms through the one ExactAlarm law (#319 F2):
        // exactness is probed, never assumed, and the inexact arm
        // remains the fallback — an exact alarm sharpens the hint,
        // it never becomes the truth.
        assertTrue(scheduler.contains("ExactAlarm.setWakeup"))
        val exactAlarm = locateMainFile(
            "java/com/example/service/ExactAlarm.kt"
        ).readText()
        assertTrue(exactAlarm.contains("canScheduleExactAlarms"))
        assertTrue(exactAlarm.contains("setExactAndAllowWhileIdle"))
        assertTrue(exactAlarm.contains("setAndAllowWhileIdle"))
    }
}
