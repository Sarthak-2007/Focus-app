package com.example

import com.example.data.BlockedBrowserDomains
import com.example.service.matchBlockedDomain
import com.example.service.matchEmbeddedBlockedDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Issue #79 (Audit R2, angle url-blocker-bypasses) — the four
 * bypasses, proven as pure logic against the shipped matcher and
 * normaliser. Every test states the world AFTER its cure and would
 * have failed against the substring matcher / prefix-strip
 * normaliser it replaces.
 */
class UrlBlockerBypassesTest {

    private val defaults = BlockedBrowserDomains.defaultDomains

    // ---------------------------------------------------------------
    // Finding 4 — normalizeDomain stored dead rules verbatim:
    // userinfo, ports and trailing dots were kept, producing
    // accepted entries that could never match a real URL bar.
    // ---------------------------------------------------------------

    @Test
    fun normalize_stripsTrailingDot() {
        assertEquals("instagram.com", BlockedBrowserDomains.normalizeDomain("instagram.com."))
        assertEquals("instagram.com", BlockedBrowserDomains.normalizeDomain("https://instagram.com./"))
    }

    @Test
    fun normalize_stripsPort() {
        assertEquals("instagram.com", BlockedBrowserDomains.normalizeDomain("instagram.com:443"))
        assertEquals("instagram.com", BlockedBrowserDomains.normalizeDomain("https://instagram.com:8443"))
    }

    @Test
    fun normalize_stripsUserinfoPortAndIncidentalPathCanonicalises() {
        // The audit's exact insert: userinfo + port must not survive
        // into the stored rule. The path survives (paths are real
        // rules — see the Shorts case below), canonical in front.
        assertEquals(
            "instagram.com/login",
            BlockedBrowserDomains.normalizeDomain("https://user:pass@instagram.com:8443/login")
        )
        assertEquals(
            "instagram.com",
            BlockedBrowserDomains.normalizeDomain("user:pass@instagram.com")
        )
    }

    @Test
    fun normalize_keepsTheDocumentedShortsForm() {
        // Pre-existing documented behaviour, unchanged: a path rule
        // typed by hand stays a path rule.
        assertEquals(
            "youtube.com/shorts",
            BlockedBrowserDomains.normalizeDomain("https://www.YouTube.com/shorts?x=1")
        )
        assertEquals("youtube.com/shorts", BlockedBrowserDomains.normalizeDomain("youtube.com/shorts/"))
    }

    @Test
    fun normalize_stillRejectsUnusableInput() {
        assertNull(BlockedBrowserDomains.normalizeDomain(""))
        assertNull(BlockedBrowserDomains.normalizeDomain("https://"))
        assertNull(BlockedBrowserDomains.normalizeDomain("nodot"))
        assertNull(BlockedBrowserDomains.normalizeDomain("has space.com"))
        assertNull(BlockedBrowserDomains.normalizeDomain(":443"))
    }

    @Test
    fun matcher_legacyDeadRules_canonicalisedAtMatchTime() {
        // Rules stored by the OLD normaliser (already on phones)
        // must start matching without a data migration: the matcher
        // canonicalises stored rules through the same normaliser.
        assertEquals(
            "instagram.com",
            matchBlockedDomain("https://www.instagram.com/reel/abc/", listOf("instagram.com."))
        )
        assertEquals(
            "instagram.com",
            matchBlockedDomain("https://www.instagram.com/reel/abc/", listOf("instagram.com:443"))
        )
    }

    // ---------------------------------------------------------------
    // Finding 1 — proxy-host serving: a blocked site browsed in
    // full under a foreign host (Google Translate proxy) never
    // matched the substring matcher.
    // ---------------------------------------------------------------

    @Test
    fun proxy_translateGoogSuffix_unwrapsToOriginHost() {
        // The audit's exact simulation.
        assertEquals(
            "instagram.com",
            matchBlockedDomain(
                "https://instagram-com.translate.goog/p/abc/?_x_tr_sl=auto&_x_tr_tl=en",
                defaults
            )
        )
        assertEquals(
            "reddit.com",
            matchBlockedDomain("https://www-reddit-com.translate.goog/r/test/", defaults)
        )
    }

    @Test
    fun proxy_translateQueryParam_unwrapsTargetUrl() {
        assertEquals(
            "reddit.com",
            matchBlockedDomain(
                "https://translate.google.com/translate?sl=auto&tl=en&u=https://www.reddit.com/r/test",
                defaults
            )
        )
        assertEquals(
            "instagram.com",
            matchBlockedDomain(
                "https://translate.google.com/translate?u=https%3A%2F%2Fwww.instagram.com%2Freel%2Fabc%2F",
                defaults
            )
        )
    }

    @Test
    fun proxy_unblockedOrigin_stillPasses() {
        assertNull(
            matchBlockedDomain("https://en-wikipedia-org.translate.goog/wiki/Photosynthesis", defaults)
        )
        assertNull(
            matchBlockedDomain(
                "https://translate.google.com/translate?u=https://en.wikipedia.org/wiki/Cell",
                defaults
            )
        )
    }

    @Test
    fun matcher_hostBased_noSubstringFalsePositives() {
        // The old substring matcher blocked any page whose URL text
        // merely CONTAINED a blocked string — a lookalike host or a
        // query parameter. Host matching is exact-or-subdomain.
        assertNull(matchBlockedDomain("https://example.com/?next=instagram.com", defaults))
        assertNull(matchBlockedDomain("https://instagram.com.evil.example/reel/abc/", defaults))
        assertNull(matchBlockedDomain("https://notinstagram.com/reel/abc/", defaults))
        // …while real subdomains of a blocked host still match.
        assertEquals(
            "instagram.com",
            matchBlockedDomain("https://m.instagram.com/reel/abc/", defaults)
        )
        // …and a visited host carrying a trailing dot still matches
        // (the audit verified this did NOT bypass; it must not
        // start bypassing under host parsing).
        assertEquals(
            "instagram.com",
            matchBlockedDomain("https://www.instagram.com./reel/abc/", defaults)
        )
    }

    // ---------------------------------------------------------------
    // Finding 2 — the youtube.com/shorts rule was evaded by the
    // same Short in /watch?v= form or via a youtu.be link.
    // ---------------------------------------------------------------

    @Test
    fun shorts_canonicalForm_matches() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/shorts/SHORTID", defaults)
        )
    }

    @Test
    fun shorts_watchForm_matches() {
        // The audit's exact evasion, and the case the pre-existing
        // TruthCar3SweepTest.una06 test already demands of defaults.
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/watch?v=SHORTID", defaults)
        )
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://www.youtube.com/watch?v=SHORTID", listOf("youtube.com/shorts"))
        )
    }

    @Test
    fun shorts_shortLinkForm_matches() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://youtu.be/SHORTID", defaults)
        )
        assertEquals(
            "youtube.com",
            matchBlockedDomain("https://youtu.be/SHORTID?t=3", listOf("youtube.com/shorts"))
        )
    }

    @Test
    fun shorts_ruleIsNotWidenedToAllOfYouTube() {
        // The rule's intent is Shorts, not the whole service: the
        // YouTube home page / a channel page stay open under it.
        assertNull(matchBlockedDomain("https://www.youtube.com/", listOf("youtube.com/shorts")))
        assertNull(matchBlockedDomain("https://www.youtube.com/@somechannel", listOf("youtube.com/shorts")))
        // A different service's path rule gains no alternate forms.
        assertNull(matchBlockedDomain("https://www.reddit.com/", listOf("reddit.com/r/popular")))
        assertEquals(
            "reddit.com",
            matchBlockedDomain("https://www.reddit.com/r/popular/top", listOf("reddit.com/r/popular"))
        )
    }

    // ---------------------------------------------------------------
    // Finding 3 — embedded players: the detectable residue. The
    // accessibility tree exposes embed URLs as page text on some
    // pages; those must match. A plain textual MENTION of a blocked
    // domain must not (no false-positive page blocks).
    // ---------------------------------------------------------------

    @Test
    fun embed_embedUrlInPageContent_matches() {
        assertEquals(
            "youtube.com",
            matchEmbeddedBlockedDomain("now playing youtube.com/embed/abc123 hd", listOf("youtube.com"))
        )
        assertEquals(
            "tiktok.com",
            matchEmbeddedBlockedDomain("video tiktok.com/embed/v2/7293847 player", defaults)
        )
        assertEquals(
            "instagram.com",
            matchEmbeddedBlockedDomain("frame instagram.com/p/abc123/embed/ loaded", defaults)
        )
    }

    @Test
    fun embed_plainMention_doesNotMatch() {
        assertNull(matchEmbeddedBlockedDomain("follow us on instagram.com for more", defaults))
        assertNull(matchEmbeddedBlockedDomain("a story about youtube.com and fame", defaults))
        assertNull(matchEmbeddedBlockedDomain("no services here at all", defaults))
        assertNull(matchEmbeddedBlockedDomain("youtube.com/embed/abc123", emptyList()))
    }

    @Test
    fun embed_lookalikeAndEvilParentHosts_doNotMatch() {
        // STURM3-02 hardening: substring host matching false-fired on
        // lookalikes and attacker-parent hosts; the parsed host must
        // equal the rule host or be a true subdomain of it.
        assertNull(matchEmbeddedBlockedDomain("player notinstagram.com/embed/x loaded", defaults))
        assertNull(matchEmbeddedBlockedDomain("player instagram.com.evil.com/embed/x loaded", defaults))
        assertEquals(
            "youtube.com",
            matchEmbeddedBlockedDomain("player m.youtube.com/embed/abc123 loaded", listOf("youtube.com"))
        )
    }

    @Test
    fun embed_pathRules_doNotFireOnEmbeds() {
        // An embedded YouTube video cannot be proven a Short from
        // its embed URL, so the Shorts path rule stays out of the
        // embed scan — that residue is documented, not papered over.
        assertNull(matchEmbeddedBlockedDomain("youtube.com/embed/abc123", defaults))
    }
}
