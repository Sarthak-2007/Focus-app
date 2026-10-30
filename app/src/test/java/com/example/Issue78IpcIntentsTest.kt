package com.example

import android.content.Context
import android.net.Uri
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.service.InterceptConsumptionStore
import com.example.service.InterceptPath
import com.example.service.ShieldDispatch
import com.example.service.MonolithShieldService
import com.example.service.shieldDispatchFor
import com.example.ui.airlockIntentRevalidated
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #78 (Audit R2, ipc-intents) — one discriminating test per
 * finding: each test names the pre-cure behaviour it would have
 * failed on.
 *
 *  1. FIX-pane Settings navigation is pinned to com.android.settings.
 *  2. Intercept consumption is persisted + the showing revalidated.
 *  3. ShieldService dispatch is fail-closed.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue78IpcIntentsTest {

    // -----------------------------------------------------------------
    // Finding 1 — pinned Settings intents. Pre-cure: package was null
    // (unpinned implicit intent, resolvable by any installed app).
    // -----------------------------------------------------------------

    @Test
    fun settingsIntentsArePinnedToTheSystemSettingsApp() {
        val plain = pinnedSettingsIntent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        assertEquals(SYSTEM_SETTINGS_PACKAGE, plain.`package`)
        assertEquals("com.android.settings", plain.`package`)
        assertEquals(Settings.ACTION_ACCESSIBILITY_SETTINGS, plain.action)

        val usage = pinnedSettingsIntent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        assertEquals("com.android.settings", usage.`package`)

        val listener = pinnedSettingsIntent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        assertEquals("com.android.settings", listener.`package`)

        // The data-carrying panes keep their package: Uri AND their pin.
        val uri = Uri.parse("package:com.example")
        val overlay = pinnedSettingsIntent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        assertEquals("com.android.settings", overlay.`package`)
        assertEquals(uri, overlay.data)

        val battery = pinnedSettingsIntent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, uri)
        assertEquals("com.android.settings", battery.`package`)
        assertEquals(uri, battery.data)
    }

    // -----------------------------------------------------------------
    // Finding 2a — persisted consumption. Pre-cure: consumption lived
    // only in removeExtra() on the in-memory intent; nothing on disk
    // recognised a redelivered intent.
    // -----------------------------------------------------------------

    @Test
    fun interceptConsumptionIsPersistedAndRecognisedOnReRead() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val id = "issue78-test-intercept-id"
        assertFalse(InterceptConsumptionStore.isConsumed(context, id))
        assertTrue(InterceptConsumptionStore.markConsumed(context, id))
        // A fresh read from disk (what a recreated activity does
        // after process death) still recognises the firing.
        assertTrue(InterceptConsumptionStore.isConsumed(context, id))
        assertFalse(InterceptConsumptionStore.isConsumed(context, "some-other-firing"))
    }

    @Test
    fun legacyFingerprintIsStableForIdenticalExtras() {
        val a = InterceptConsumptionStore.fingerprint("com.app", "App", "Locked", "APP_TIER")
        val b = InterceptConsumptionStore.fingerprint("com.app", "App", "Locked", "APP_TIER")
        val c = InterceptConsumptionStore.fingerprint("com.app", "App", "Locked", "SHIELD_APP")
        assertEquals(a, b)
        assertTrue(a != c)
    }

    @Test
    fun consumptionEntriesPruneByAgeAndCap() {
        val now = 10_000_000_000L
        val fresh = listOf("a" to now, "b" to now - 1_000L)
        val stale = listOf("old" to now - 49L * 60L * 60L * 1000L)
        assertEquals(fresh, InterceptConsumptionStore.pruned(fresh + stale, now))
        val many = (1..100).map { "id$it" to now - it.toLong() }
        assertEquals(64, InterceptConsumptionStore.pruned(many, now).size)
        // Encode/parse round-trips.
        assertEquals(
            fresh,
            InterceptConsumptionStore.parseEntries(
                InterceptConsumptionStore.encodeEntries(fresh), now
            )
        )
    }

    // -----------------------------------------------------------------
    // Finding 2b — revalidation. Pre-cure: triggerAirlockIntercept
    // showed the Airlock unconditionally, session or no session.
    // -----------------------------------------------------------------

    @Test
    fun airlockRequiresALiveSessionOnEveryDoor() {
        for (path in InterceptPath.values()) {
            assertFalse(
                "no session must mean no Airlock on $path",
                airlockIntentRevalidated(path, AppBlockTier.LOCKED, false, false, false)
            )
        }
    }

    @Test
    fun appTierDoorsRevalidateAgainstTheLiveTier() {
        // Still locked: shows.
        assertTrue(airlockIntentRevalidated(InterceptPath.APP_TIER, AppBlockTier.LOCKED, true, false, false))
        assertTrue(airlockIntentRevalidated(InterceptPath.SHIELD_APP, AppBlockTier.LOCKED, true, false, false))
        // Tier opened since the firing (or row pruned): stale, dropped.
        assertFalse(airlockIntentRevalidated(InterceptPath.APP_TIER, AppBlockTier.OPEN, true, false, false))
        assertFalse(airlockIntentRevalidated(InterceptPath.SHIELD_APP, null, true, false, false))
        // PEEK app under a live lease is being honoured, not blocked.
        assertFalse(airlockIntentRevalidated(InterceptPath.APP_TIER, AppBlockTier.PEEK_2M, true, false, true))
        // PEEK app passes freely in a Safe Break: no Airlock.
        assertFalse(airlockIntentRevalidated(InterceptPath.SHIELD_APP, AppBlockTier.PEEK_2M, true, true, false))
        // PEEK app outside a break with no lease: still blocked, shows.
        assertTrue(airlockIntentRevalidated(InterceptPath.APP_TIER, AppBlockTier.PEEK_2M, true, false, false))
        // A LOCKED app stays blocked through a Safe Break.
        assertTrue(airlockIntentRevalidated(InterceptPath.APP_TIER, AppBlockTier.LOCKED, true, true, false))
    }

    @Test
    fun nonTierDoorsRevalidateOnTheSessionAlone() {
        // The site door's package is a browser (OPEN row / no peek
        // semantics) and the guard's is Settings (often no row):
        // with a live session their Airlocks still show, exactly as
        // enforcement fired them.
        assertTrue(airlockIntentRevalidated(InterceptPath.SITE, AppBlockTier.OPEN, true, false, false))
        assertTrue(airlockIntentRevalidated(InterceptPath.SNIPER, null, true, false, false))
        assertTrue(airlockIntentRevalidated(InterceptPath.UNINSTALL_GUARD, null, true, false, false))
        assertTrue(airlockIntentRevalidated(InterceptPath.UNKNOWN, null, true, false, false))
    }

    // -----------------------------------------------------------------
    // Finding 3 — fail-closed shield dispatch. Pre-cure: every
    // non-STOP action (unknown strings AND null sticky restarts)
    // fell into the full start path and reset the witness.
    // -----------------------------------------------------------------

    @Test
    fun shieldDispatchStartsOnlyOnTheStartAction() {
        assertEquals(
            ShieldDispatch.START_FULL,
            shieldDispatchFor(MonolithShieldService.ACTION_START_SHIELD, monitorRunning = false, hasLiveSession = false)
        )
        assertEquals(
            ShieldDispatch.STOP,
            shieldDispatchFor(MonolithShieldService.ACTION_STOP_SHIELD, monitorRunning = true, hasLiveSession = true)
        )
    }

    @Test
    fun shieldDispatchKeepsARunningLoopUntouchedOnDuplicatesAndUnknownActions() {
        // Duplicate START while protecting: no reset, no loop restart.
        assertEquals(
            ShieldDispatch.KEEP_RUNNING,
            shieldDispatchFor(MonolithShieldService.ACTION_START_SHIELD, monitorRunning = true, hasLiveSession = true)
        )
        // Unknown / typo'd action over a live loop: ignored, loop stands.
        assertEquals(
            ShieldDispatch.KEEP_RUNNING,
            shieldDispatchFor("com.example.action.START_SHEILD", monitorRunning = true, hasLiveSession = true)
        )
        // Unknown action with nothing running: fail closed, stop self.
        assertEquals(
            ShieldDispatch.STOP_SELF,
            shieldDispatchFor("com.example.action.START_SHEILD", monitorRunning = false, hasLiveSession = true)
        )
    }

    @Test
    fun shieldDispatchStickyRestartNeedsALiveSession() {
        // Null intent (START_STICKY restart) WITH a live session:
        // the legitimate restart resumes protection.
        assertEquals(
            ShieldDispatch.START_FULL,
            shieldDispatchFor(null, monitorRunning = false, hasLiveSession = true)
        )
        assertEquals(
            ShieldDispatch.KEEP_RUNNING,
            shieldDispatchFor(null, monitorRunning = true, hasLiveSession = true)
        )
        // Null intent with NO live session: never enters the
        // foreground, never resets a witness — it stops itself.
        assertEquals(
            ShieldDispatch.STOP_SELF,
            shieldDispatchFor(null, monitorRunning = false, hasLiveSession = false)
        )
        assertEquals(
            ShieldDispatch.STOP_SELF,
            shieldDispatchFor(null, monitorRunning = true, hasLiveSession = false)
        )
    }
}
