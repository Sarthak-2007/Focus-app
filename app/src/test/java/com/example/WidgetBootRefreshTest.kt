package com.example

import android.content.Intent
import android.os.PowerManager
import com.example.service.bootGlanceRefreshDue
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #155 (F1) — the boot/update/clock glance-refresh cure
 * (BUILDER-3, WING-F).
 *
 * Before this cure the boot receiver reconciled shield + routines
 * on every boot, app update and clock change and never told the
 * home-screen widget: the launcher kept the pre-reconcile snapshot
 * (a SEALED line for a session the tick had just finalised, a
 * NEXT SEAL instant already in the past, HH:mm strings rendered
 * in a zone the user had left) until the platform's 30-minute
 * widget period happened to fire. Every test states the world
 * AFTER the cure: the pure rule did not exist before it, and the
 * receiver carried no widget reference at all.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WidgetBootRefreshTest {

    // ---------------------------------------------------------------
    // The pure rule: exactly the reconcile doors whose outcome can
    // move a printed widget field owe the glance a re-render.
    // ---------------------------------------------------------------

    @Test
    fun refreshDue_bootAndUpdateActions_allDue() {
        assertTrue(bootGlanceRefreshDue(Intent.ACTION_LOCKED_BOOT_COMPLETED))
        assertTrue(bootGlanceRefreshDue(Intent.ACTION_BOOT_COMPLETED))
        assertTrue(bootGlanceRefreshDue(Intent.ACTION_MY_PACKAGE_REPLACED))
        assertTrue(bootGlanceRefreshDue("android.intent.action.QUICKBOOT_POWERON"))
    }

    @Test
    fun refreshDue_clockActions_allDue() {
        // Every printed HH:mm is a wall-clock render: a moved clock
        // or zone makes the standing snapshot wrong even when no
        // session or routine state moved.
        assertTrue(bootGlanceRefreshDue(Intent.ACTION_TIME_CHANGED))
        assertTrue(bootGlanceRefreshDue(Intent.ACTION_TIMEZONE_CHANGED))
    }

    @Test
    fun refreshDue_doorsThatMoveNoPrintedField_notDue() {
        // Battery doors only re-read the shield card's battery line;
        // a package-component change reconciles the app catalogue.
        // Neither moves a session, a routine, or a printed time.
        assertFalse(bootGlanceRefreshDue(Intent.ACTION_BATTERY_LOW))
        assertFalse(bootGlanceRefreshDue(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
        assertFalse(bootGlanceRefreshDue(Intent.ACTION_PACKAGE_CHANGED))
        assertFalse(bootGlanceRefreshDue(null))
        assertFalse(bootGlanceRefreshDue("com.example.UNRELATED"))
    }

    // ---------------------------------------------------------------
    // The wiring, pinned at the source (the same pinning style as
    // BootDeviceAdminTruthTest): the refresh must ride AFTER the
    // reconcile it corrects, on both doors.
    // ---------------------------------------------------------------

    private fun bootReceiverSource(): String =
        locateMainSource("com/example/service/MonolithBootReceiver.kt").readText()

    @Test
    fun bootActions_refreshRunsAfterTheReconcile() {
        val source = bootReceiverSource()
        val body = source.substringAfter("private suspend fun runBootActions")
        // The refresh is the last group of runBootActions: it reads
        // the state the tick and the shield groups settled, never
        // the state they found.
        assertTrue(body.contains("refreshGlanceAfterReconcile(appContext, action)"))
        assertTrue(
            body.indexOf("shield-after-tick") <
                body.indexOf("refreshGlanceAfterReconcile")
        )
    }

    @Test
    fun timeChangedBranch_refreshFollowsTheTick() {
        val source = bootReceiverSource()
        val branch = source
            .substringAfter("Intent.ACTION_TIME_CHANGED,")
            .substringBefore("Intent.ACTION_PACKAGE_CHANGED")
        assertTrue(branch.contains("refreshGlanceAfterReconcile(appContext, action)"))
        assertTrue(
            branch.indexOf("time-changed-tick") <
                branch.indexOf("refreshGlanceAfterReconcile")
        )
    }

    @Test
    fun refreshFunnel_isTheWidgetProvidersOwnDoor() {
        val source = bootReceiverSource()
        // One funnel, the provider's own: a second render path here
        // could drift from the shield's refresh and lie differently.
        assertTrue(source.contains("MonolithWidgetProvider.requestUpdate(context)"))
        assertEquals(
            1,
            Regex("MonolithWidgetProvider\\.requestUpdate").findAll(source).count()
        )
    }

    @Test
    fun refresh_isGuardedLikeEveryOtherBootGroup() {
        val source = bootReceiverSource()
        val helper = source.substringAfter("internal suspend fun refreshGlanceAfterReconcile")
        // Issue #44's law: a failing group is contained and logged,
        // never a crash out of a boot door — the widget host being
        // gone must not fail the reconcile.
        assertTrue(helper.contains("runGuardedBootAction(\"widget-refresh\")"))
        assertTrue(helper.contains("bootGlanceRefreshDue(action)"))
    }

    companion object {
        fun locateMainSource(relative: String): File {
            val candidates = listOf(
                File("src/main/java/$relative"),
                File("app/src/main/java/$relative")
            )
            return candidates.firstOrNull { it.exists() }
                ?: error("could not locate $relative from ${File(".").absolutePath}")
        }
    }
}
