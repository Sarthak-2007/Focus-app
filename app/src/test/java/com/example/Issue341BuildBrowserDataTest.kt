package com.example

import com.example.data.BROWSER_PROFILES
import com.example.data.BrowserEngine
import com.example.data.IncognitoState
import com.example.data.WebGateVerdict
import com.example.data.browserProfileFor
import com.example.data.hostExcluded
import com.example.data.incognitoStateFor
import com.example.data.urlBarSuffixesForBrowser
import com.example.data.webGateVerdict
import com.example.service.matchBlockedDomainInLoadedUrlExcluding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Audit R6 #341 BUILD — F10 / F16 / F27 / F38 / F52: the browser
 * coverage table, the per-browser extraction engines, the
 * incognito door's inputs, and the Web Gate exclusion list
 * (com.example.data.WebGate — the mapper's covering reference
 * for that changed production file).
 *
 * Scope note (named, not hidden): verification wave 3
 * (6534024259) corrected the counts against current main — the
 * coverage table on main carries 20 profiles (15 + 5 stock OEM),
 * and these cases pin THAT table.
 * PIN scope: SOURCE.
 */
class Issue341BuildBrowserDataTest {

    // ---- F10 / F27: the coverage table ----

    @Test
    fun coverageTable_coversEveryShippedBrowserPackage_includingOemStock() {
        val expected = listOf(
            "com.android.chrome", "com.brave.browser", "org.mozilla.firefox",
            "com.microsoft.emmx", "com.sec.android.app.sbrowser",
            "com.opera.browser", "com.opera.mini.native", "com.opera.gx",
            "com.duckduckgo.mobile.android", "com.kiwibrowser.browser",
            "mark.via.gp", "com.yandex.browser", "com.UCMobile.intl",
            "com.vivaldi.browser", "com.opera.browser.beta",
            // The stock OEM browsers (F27's cure half).
            "com.mi.globalbrowser", "com.vivo.browser", "com.heytap.browser",
            "com.huawei.browser", "com.android.browser"
        )
        assertEquals(expected.toSet(), BROWSER_PROFILES.map { it.packageName }.toSet())
        for (pkg in expected) {
            assertTrue("$pkg must resolve", browserProfileFor(pkg) != null)
        }
        // The OEM five are marked as stock rows.
        assertTrue(browserProfileFor("com.mi.globalbrowser")!!.stockOem)
        assertTrue(browserProfileFor("com.heytap.browser")!!.stockOem)
    }

    @Test
    fun coverageTable_unknownPackage_isUnsupportedNotCrash() {
        assertNull(browserProfileFor("com.example.notabrowser"))
        assertNull(browserProfileFor(""))
    }

    @Test
    fun stockOemBrowser_blockedSite_isSiteBlocked() {
        // The loaded-URL matcher is package-blind; the F27 cure is
        // that these packages are now IN the policed set at all —
        // pinned here via the coverage table + the matcher itself.
        for (pkg in listOf("com.mi.globalbrowser", "com.vivo.browser", "com.heytap.browser")) {
            assertTrue(browserProfileFor(pkg) != null)
        }
        assertEquals(
            "youtube.com",
            matchBlockedDomainInLoadedUrlExcluding(
                "https://www.youtube.com/watch?v=1", listOf("youtube.com"), emptyList()
            )
        )
    }

    // ---- F16: per-browser extraction engines ----

    @Test
    fun extractionEngine_isChosenPerBrowser_notOneGlobalIdList() {
        assertEquals(
            BrowserEngine.FIREFOX,
            browserProfileFor("org.mozilla.firefox")!!.engine
        )
        assertEquals(
            BrowserEngine.CHROMIUM,
            browserProfileFor("com.android.chrome")!!.engine
        )
        val firefoxSuffixes = urlBarSuffixesForBrowser("org.mozilla.firefox", emptyList())
        val chromeSuffixes = urlBarSuffixesForBrowser("com.android.chrome", emptyList())
        assertTrue(firefoxSuffixes.isNotEmpty())
        assertTrue(chromeSuffixes.isNotEmpty())
        // The engines' id sets are NOT one shared list.
        assertNotEquals(firefoxSuffixes.toSet(), chromeSuffixes.toSet())
        // A learned id widens its own browser's set only.
        val widened = urlBarSuffixesForBrowser(
            "com.android.chrome", listOf("com.android.chrome:id/custom_bar")
        )
        assertTrue(widened.contains("com.android.chrome:id/custom_bar"))
        assertFalse(firefoxSuffixes.contains("com.android.chrome:id/custom_bar"))
    }

    // ---- F38: the incognito door's inputs ----

    @Test
    fun incognitoBadge_browserInPrivateMode_appliesPrivateModePolicy() {
        assertEquals(
            IncognitoState.PRIVATE,
            incognitoStateFor("com.android.chrome", "you have gone incognito")
        )
        assertEquals(
            IncognitoState.PRIVATE,
            incognitoStateFor("org.mozilla.firefox", "you're in private browsing")
        )
    }

    @Test
    fun incognitoBadge_normalWindow_unchangedMatching() {
        assertEquals(
            IncognitoState.NORMAL,
            incognitoStateFor("com.android.chrome", "youtube.com — watch videos")
        )
    }

    @Test
    fun incognitoBadge_unknownBadge_neverTreatedAsNormalSilently() {
        // DuckDuckGo ships no badge table in the profile: the state
        // is UNKNOWN, a distinct value — never the NORMAL the old
        // code silently assumed for every browser.
        assertEquals(
            IncognitoState.UNKNOWN,
            incognitoStateFor("com.duckduckgo.mobile.android", "anything at all")
        )
        assertEquals(
            IncognitoState.UNKNOWN,
            incognitoStateFor("com.example.notabrowser", "anything at all")
        )
    }

    // ---- F52: the exclusion list ----

    @Test
    fun exclusionList_carvedHostInsideBlockedDomain_isAllowed() {
        val verdict = webGateVerdict(
            urlText = "https://music.youtube.com/",
            blockAll = false,
            allowedHosts = emptyList(),
            blockedDomains = listOf("youtube.com"),
            excludedHosts = listOf("music.youtube.com")
        )
        assertNotEquals(WebGateVerdict.BLOCK_LISTED, verdict)
        assertTrue(hostExcluded("music.youtube.com", listOf("music.youtube.com")))
        assertEquals(
            null,
            matchBlockedDomainInLoadedUrlExcluding(
                "https://music.youtube.com/",
                listOf("youtube.com"),
                listOf("music.youtube.com")
            )
        )
    }

    @Test
    fun exclusionList_nonCarvedHostOnSameDomain_stillBlocked() {
        val verdict = webGateVerdict(
            urlText = "https://www.youtube.com/",
            blockAll = false,
            allowedHosts = emptyList(),
            blockedDomains = listOf("youtube.com"),
            excludedHosts = listOf("music.youtube.com")
        )
        assertEquals(WebGateVerdict.BLOCK_LISTED, verdict)
        assertFalse(hostExcluded("www.youtube.com", listOf("music.youtube.com")))
        assertEquals(
            "youtube.com",
            matchBlockedDomainInLoadedUrlExcluding(
                "https://www.youtube.com/",
                listOf("youtube.com"),
                listOf("music.youtube.com")
            )
        )
    }

    @Test
    fun exclusionList_neverOverridesBlockAllMode() {
        // In block-all mode an exclusion is NOT an allowlist entry:
        // the host still needs the allowlist to pass.
        val verdict = webGateVerdict(
            urlText = "https://music.youtube.com/",
            blockAll = true,
            allowedHosts = emptyList(),
            blockedDomains = listOf("youtube.com"),
            excludedHosts = listOf("music.youtube.com")
        )
        assertEquals(WebGateVerdict.BLOCK_ALL, verdict)
    }
}
