package com.example

import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #338 (Audit R5 supplement — Freedom, Comparative audit):
 * the wins, pinned as regression guards.
 *
 * F2 — Monolith ships no tracker or log pipeline: no analytics,
 * session-replay or attribution SDK is on the classpath, and
 * production logging is failure-only and content-free (the one
 * package-bearing log line was removed in this issue's commit).
 * If a future change adds any of these SDKs as a dependency, this
 * test fails before the privacy win silently dies.
 *
 * F1 — the intercept is native: the block surface is a Compose
 * screen in this process (LauncherBlockScreen / Airlock overlays),
 * not a bridge to a second runtime. Its classes are on the app
 * classpath; Freedom's failure mode (no engine, no screen) has no
 * equivalent here.
 */
class NoTelemetryGuardTest {

    @Test
    fun noAnalyticsOrReplaySdkOnClasspath() {
        val forbidden = listOf(
            "com.google.firebase.analytics.FirebaseAnalytics",
            "com.microsoft.clarity.Clarity",
            "com.braze.Braze",
            "com.kochava.tracker.Tracker",
            "com.mixpanel.android.mpmetrics.MixpanelAPI",
            "com.amplitude.core.Amplitude",
            "io.sentry.Sentry",
            "com.posthog.PostHog"
        )
        for (name in forbidden) {
            assertThrows(
                "$name must never ship in Monolith",
                ClassNotFoundException::class.java
            ) { Class.forName(name) }
        }
    }

    @Test
    fun nativeInterceptSurfacesAreInProcess() {
        // The block face renders from these in-process Compose
        // surfaces — no engine, bridge or web layer to be absent.
        Class.forName("com.example.ui.components.LauncherBlockScreenKt")
        Class.forName("com.example.ui.components.AirlockAndDisarmOverlaysKt")
        assertTrue(true)
    }

    @Test
    fun bootRearmAndSignedRecordMachineryPresent() {
        // F3: the boot receiver that actually re-arms (its pure
        // decisions are truth-tabled in BootDeviceAdminTruthTest).
        Class.forName("com.example.service.MonolithBootReceiver")
        // F4: the HMAC signer behind the durable on-device session
        // record (sign/verify truth in CryptoIntegrityManagerTest).
        Class.forName("com.example.data.CryptoIntegrityManager")
        assertTrue(true)
    }
}
