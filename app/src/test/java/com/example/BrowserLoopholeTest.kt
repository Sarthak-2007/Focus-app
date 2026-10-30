package com.example

import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.data.BlockedBrowserDomains
import com.example.data.heldSitesCount
import com.example.service.InterceptPath
import com.example.service.SitePeekStore
import com.example.service.isSupportedBrowserPackage
import com.example.service.matchBlockedDomain
import com.example.service.sitePeekExempts
import com.example.ui.peekOfferFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * W3 #130 — Audit R3b, angle browser-as-loophole. Each test states
 * the world after its cure and fails on the audited behaviour:
 *  1. a browser app peek no longer silences the site blocker;
 *  2. youtube.com (home/watch, not only /shorts) is on the seeded
 *     site list and matches through the same matcher;
 *  3. second browsers are supported by the site blocker;
 *  4. blocked sites can be peeked, domain-scoped;
 *  5. the receipt's held-sites count follows the blocker toggle.
 */
class BrowserLoopholeTest {

    private fun app(pkg: String, tier: AppBlockTier) = BlockedAppEntity(
        packageName = pkg,
        appName = "App",
        category = "Web",
        isBlocked = tier != AppBlockTier.OPEN,
        blockTier = tier.name
    )

    // ---------------------------------------------------------------
    // Finding 1 + 4 — the exemption is (package, domain, lease end)
    // or it is nothing. An app peek grants no tag, so no tuple with
    // a null tag ever exempts: site rules stay live through it.
    // ---------------------------------------------------------------

    @Test
    fun sitePeekExemptsOnlyTheGrantedDomainInTheGrantedBrowser() {
        val now = 100_000L
        val leaseEnd = now + 120_000L
        assertTrue(
            sitePeekExempts("reddit.com", "com.android.chrome", "reddit.com", "com.android.chrome", leaseEnd, now)
        )
        // Same browser, another blocked site: still held.
        assertFalse(
            sitePeekExempts("reddit.com", "com.android.chrome", "instagram.com", "com.android.chrome", leaseEnd, now)
        )
        // Same domain, another browser: still held.
        assertFalse(
            sitePeekExempts("reddit.com", "com.android.chrome", "reddit.com", "com.brave.browser", leaseEnd, now)
        )
        // No tag (a plain app peek on the browser): nothing exempt.
        assertFalse(
            sitePeekExempts(null, null, "reddit.com", "com.android.chrome", leaseEnd, now)
        )
        // Lease spent or never granted: nothing exempt.
        assertFalse(
            sitePeekExempts("reddit.com", "com.android.chrome", "reddit.com", "com.android.chrome", leaseEnd, leaseEnd)
        )
        assertFalse(
            sitePeekExempts("reddit.com", "com.android.chrome", "reddit.com", "com.android.chrome", 0L, now)
        )
    }

    @Test
    fun sitePeekStoreGrantAndScopedClear() {
        SitePeekStore.resetForTest()
        SitePeekStore.grant("reddit.com", "com.android.chrome")
        assertEquals("reddit.com", SitePeekStore.domain)
        // A different package's lease ending does not clear the tag.
        SitePeekStore.clearIf("com.brave.browser")
        assertEquals("reddit.com", SitePeekStore.domain)
        SitePeekStore.clearIf("com.android.chrome")
        assertEquals(null, SitePeekStore.domain)
        assertEquals(null, SitePeekStore.packageName)
    }

    // ---------------------------------------------------------------
    // Finding 4 — the SITE door sells a peek regardless of the
    // browser's app tier, under the same cooldown/pill discipline.
    // ---------------------------------------------------------------

    @Test
    fun siteDoorOffersPeekForAnyBrowserTier() {
        // Factory Chrome is OPEN; STUDY Chrome is PEEK_2M. Both owe
        // a site peek — the old matrix owed none on any tier.
        assertTrue(peekOfferFor(InterceptPath.SITE, app("com.android.chrome", AppBlockTier.OPEN), 0, false))
        assertTrue(peekOfferFor(InterceptPath.SITE, app("com.android.chrome", AppBlockTier.PEEK_2M), 0, false))
        assertTrue(peekOfferFor(InterceptPath.SITE, app("com.android.chrome", AppBlockTier.LOCKED), 0, false))
        // Same discipline as the app doors: cooldown, pill, row.
        assertFalse(peekOfferFor(InterceptPath.SITE, app("com.android.chrome", AppBlockTier.OPEN), 1, false))
        assertFalse(peekOfferFor(InterceptPath.SITE, app("com.android.chrome", AppBlockTier.OPEN), 0, true))
        assertFalse(peekOfferFor(InterceptPath.SITE, null, 0, false))
        // The app doors keep their tier gate untouched.
        assertFalse(peekOfferFor(InterceptPath.APP_TIER, app("com.android.chrome", AppBlockTier.OPEN), 0, false))
    }

    // ---------------------------------------------------------------
    // Finding 2 — the YouTube feed is on the seeded list, enforced
    // by the same matcher as every other site.
    // ---------------------------------------------------------------

    @Test
    fun youtubeFeedIsBlockedByTheSeededList() {
        assertTrue(BlockedBrowserDomains.defaultDomains.contains("youtube.com"))
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/watch?v=abc", BlockedBrowserDomains.defaultDomains)
        )
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/", BlockedBrowserDomains.defaultDomains)
        )
        // Composition ruling (GESTALT, on the #130 landing): #79's
        // matcher and its host-print law WIN — a matched rule prints
        // its canonical host, so the Shorts URL matched through the
        // /shorts entry prints "youtube.com" (#79 canonical host
        // print supersedes #130's entry print, "youtube.com/shorts").
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/shorts/xyz", BlockedBrowserDomains.defaultDomains)
        )
    }

    // ---------------------------------------------------------------
    // Finding 3 — the site blocker polices second browsers too.
    // ---------------------------------------------------------------

    @Test
    fun secondBrowsersAreSupportedBrowsers() {
        for (pkg in listOf(
            "com.android.chrome", "com.brave.browser", "org.mozilla.firefox",
            "com.microsoft.emmx", "com.sec.android.app.sbrowser"
        )) {
            assertTrue("original five must stay supported: $pkg", isSupportedBrowserPackage(pkg))
        }
        for (pkg in listOf(
            "com.opera.browser", "com.opera.mini.native", "com.opera.gx",
            "com.duckduckgo.mobile.android", "com.kiwibrowser.browser",
            "mark.via.gp", "com.yandex.browser", "com.UCMobile.intl"
        )) {
            assertTrue("second browser must be policed: $pkg", isSupportedBrowserPackage(pkg))
        }
        assertFalse(isSupportedBrowserPackage("com.example.shopping"))
        assertFalse(isSupportedBrowserPackage("com.whatsapp"))
    }

    // ---------------------------------------------------------------
    // Finding 5 — held sites follow the enforcement toggle.
    // ---------------------------------------------------------------

    @Test
    fun heldSitesCountIsZeroWhenTheBlockerIsOff() {
        assertEquals(9, heldSitesCount(9, browserBlockerEnabled = true))
        assertEquals(0, heldSitesCount(9, browserBlockerEnabled = false))
        assertEquals(0, heldSitesCount(0, browserBlockerEnabled = true))
    }
}
