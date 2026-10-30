package com.example

import com.example.data.AppCatalogReconcile
import com.example.data.ProfiledInstalledPackage
import com.example.service.SurfaceHostKind
import com.example.service.looksLikeLoadedUrl
import com.example.service.matchBlockedDomainInLoadedUrl
import com.example.service.surfaceHostKindFor
import com.example.service.surfaceNodeTextNamesBlockedApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #80 (Audit R2, blocking-threat-model) — the pure cores of
 * the new doors. Each test names the bypass it closes; the Android
 * walking (windows, shade sweep, LauncherApps) lives in the
 * services and is exercised on-device / in CI.
 */
class ThreatModelGuardsTest {

    private val domains = listOf("instagram.com", "youtube.com", "reddit.com")

    // --- #80-3: URL check no longer host-package-gated ---------

    @Test
    fun loadedUrlInAnyHostMatches() {
        // As it appears in a Custom Tab / in-app WebView node.
        assertEquals(
            "instagram.com",
            matchBlockedDomainInLoadedUrl("https://www.instagram.com/reel/x", domains)
        )
        assertEquals(
            "reddit.com",
            matchBlockedDomainInLoadedUrl("reddit.com/r/neet", domains)
        )
        assertNull(matchBlockedDomainInLoadedUrl("https://example.org/page", domains))
    }

    @Test
    fun proseMentioningADomainNeverMatches() {
        // A chat message / caption mentioning a domain is not a
        // loaded site — the old gate's cure must not become a
        // false-positive door inside messaging apps.
        assertFalse(looksLikeLoadedUrl("check instagram.com lol"))
        assertNull(
            matchBlockedDomainInLoadedUrl("check instagram.com lol", domains)
        )
        assertFalse(looksLikeLoadedUrl("instagram is down again"))
        assertTrue(looksLikeLoadedUrl("www.youtube.com/shorts/abc"))
        assertTrue(looksLikeLoadedUrl("https://x.com/home"))
    }

    // --- #80-5: launcher / Assistant surfaces ------------------

    @Test
    fun surfaceHostsAreRecognised() {
        assertEquals(
            SurfaceHostKind.LAUNCHER,
            surfaceHostKindFor("com.google.android.apps.nexuslauncher")
        )
        assertEquals(
            SurfaceHostKind.LAUNCHER, surfaceHostKindFor("com.miui.home")
        )
        assertEquals(
            SurfaceHostKind.ASSISTANT,
            surfaceHostKindFor("com.google.android.googlequicksearchbox")
        )
        assertEquals(
            SurfaceHostKind.OTHER, surfaceHostKindFor("com.whatsapp")
        )
    }

    @Test
    fun surfaceTextNamingBlockedAppFires() {
        // A content node: an attribution / card line naming the app.
        assertEquals(
            "YouTube",
            surfaceNodeTextNamesBlockedApp(
                listOf("YouTube \u2022 1.2M views \u2022 trending now"),
                listOf("YouTube", "Instagram")
            )
        )
        // A bare icon label must NEVER fire: the home-screen icon
        // grid is exactly that, and firing there would intercept
        // the launcher itself in a loop.
        assertNull(
            surfaceNodeTextNamesBlockedApp(
                listOf("YouTube", "Instagram", "Weather"),
                listOf("YouTube", "Instagram")
            )
        )
        assertNull(
            surfaceNodeTextNamesBlockedApp(
                listOf("weather calendar clock for today"),
                listOf("YouTube", "Instagram")
            )
        )
        // Short names are too collision-prone to police on.
        assertNull(
            surfaceNodeTextNamesBlockedApp(
                listOf("x marks the spot, twenty chars long"),
                listOf("X")
            )
        )
    }

    // --- #80-1: profiled discovery -----------------------------

    @Test
    fun profiledInstallsMergeByPackageAndExposeConflation() {
        val profiled = listOf(
            ProfiledInstalledPackage("com.instagram.android", "Instagram", "UserHandle{0}"),
            ProfiledInstalledPackage("com.instagram.android", "Instagram", "UserHandle{10}"),
            ProfiledInstalledPackage("com.whatsapp", "WhatsApp", "UserHandle{0}")
        )
        // Catalog still holds one row per package (its PK) ...
        assertEquals(2, AppCatalogReconcile.mergeProfiledInstalls(profiled).size)
        // ... but the two installs behind the Instagram row are
        // now visible as a fact instead of silently conflated.
        assertEquals(
            listOf("UserHandle{0}", "UserHandle{10}"),
            AppCatalogReconcile.profilesByPackage(profiled)["com.instagram.android"]
        )
        assertEquals(
            listOf("UserHandle{0}"),
            AppCatalogReconcile.profilesByPackage(profiled)["com.whatsapp"]
        )
    }
}
