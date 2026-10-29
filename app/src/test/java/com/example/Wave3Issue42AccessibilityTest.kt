package com.example

import android.media.AudioManager
import com.example.service.extractUrlHost
import com.example.service.isCallSurfacePackage
import com.example.service.isTamperActionViewId
import com.example.service.matchBlockedDomain
import com.example.service.shouldBypassForCall
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Wave-3 — issue #42 (accessibility-service): the pure helpers
 * POSTEN extracts from MonolithAccessibilityService.
 *
 * Pure JUnit4 only: no Robolectric, no Android runtime calls.
 * The only Android touch is AudioManager's MODE_* constants,
 * which are compile-time constants inlined by the compiler.
 *
 * Each group guards one named failure mode of the old service:
 *
 * 1. GLOBAL-KILL-SWITCH — the old isCallActive() check ran BEFORE
 *    any block check and returned for EVERY package whenever the
 *    audio mode looked call-like. One incoming call therefore
 *    unblocked the whole phone, YouTube included. The cure is a
 *    per-package decision: [shouldBypassForCall] and
 *    [isCallSurfacePackage] only ever let a call surface through.
 *
 * 2. SUBSTRING-MATCH — the old matchBlockedDomain was
 *    `urlText.contains(domain)`, so "notyoutube.com",
 *    "youtube.com.evil.example" and a "youtube.com" sitting in a
 *    query string all blocked as if they were youtube.com. The
 *    cure is host-exact semantics on the parsed host:
 *    [extractUrlHost] and the new [matchBlockedDomain].
 *
 * 3. ENGLISH-TEXT-HEURISTIC — the old tamper guard scanned the
 *    whole Settings window's lowercased text for English words
 *    ("force stop", "uninstall", "clear data", ...) next to the
 *    word "monolith". It broke with locale, wording and OEM skins.
 *    The cure is a structural check on the view id of the actual
 *    tamper action: [isTamperActionViewId].
 */
class Wave3Issue42AccessibilityTest {

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part A — shouldBypassForCall:
    // a call-like audio mode bypasses ONLY on a call surface.
    // ---------------------------------------------------------------

    @Test
    fun shouldBypassForCall_inCall_dialer_bypasses() {
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_IN_CALL,
                "com.google.android.dialer"
            )
        )
    }

    @Test
    fun shouldBypassForCall_inCall_youtube_doesNotBypass() {
        // THE regression case: a call in progress must not unblock
        // other apps. The old global check returned here.
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_CALL,
                "com.google.android.youtube"
            )
        )
    }

    @Test
    fun shouldBypassForCall_ringtone_inCallUi_bypasses() {
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_RINGTONE,
                "com.android.incallui"
            )
        )
    }

    @Test
    fun shouldBypassForCall_inCommunication_suffixDialer_bypasses() {
        // OEM / third-party dialers are recognised by the suffix
        // rule, not only by an exact package list.
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.example.some.dialer"
            )
        )
    }

    @Test
    fun shouldBypassForCall_normalMode_dialer_doesNotBypass() {
        // A call surface alone is not enough: with no call-like
        // audio mode there is nothing to bypass for.
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_NORMAL,
                "com.google.android.dialer"
            )
        )
    }

    @Test
    fun shouldBypassForCall_inCall_telecom_bypasses() {
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_IN_CALL,
                "com.android.server.telecom"
            )
        )
    }

    @Test
    fun shouldBypassForCall_ringtone_youtube_doesNotBypass() {
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_RINGTONE,
                "com.google.android.youtube"
            )
        )
    }

    @Test
    fun shouldBypassForCall_inCommunication_youtube_doesNotBypass() {
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.google.android.youtube"
            )
        )
    }

    @Test
    fun shouldBypassForCall_normalMode_inCallUi_doesNotBypass() {
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_NORMAL,
                "com.android.incallui"
            )
        )
    }

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part B — isCallSurfacePackage:
    // the exact set plus the dialer / incallui suffix rule.
    // ---------------------------------------------------------------

    @Test
    fun isCallSurfacePackage_googleDialer_isCallSurface() {
        assertTrue(isCallSurfacePackage("com.google.android.dialer"))
    }

    @Test
    fun isCallSurfacePackage_androidInCallUi_isCallSurface() {
        assertTrue(isCallSurfacePackage("com.android.incallui"))
    }

    @Test
    fun isCallSurfacePackage_serverTelecom_isCallSurface() {
        assertTrue(isCallSurfacePackage("com.android.server.telecom"))
    }

    @Test
    fun isCallSurfacePackage_suffixDialer_isCallSurface() {
        assertTrue(isCallSurfacePackage("com.example.some.dialer"))
    }

    @Test
    fun isCallSurfacePackage_suffixInCallUi_isCallSurface() {
        assertTrue(isCallSurfacePackage("org.example.incallui"))
    }

    @Test
    fun isCallSurfacePackage_youtube_isNotCallSurface() {
        assertFalse(isCallSurfacePackage("com.google.android.youtube"))
    }

    @Test
    fun isCallSurfacePackage_settings_isNotCallSurface() {
        assertFalse(isCallSurfacePackage("com.android.settings"))
    }

    // ---------------------------------------------------------------
    // 2. SUBSTRING-MATCH, part A — extractUrlHost: the host alone,
    // with scheme, credentials, port and trailing dot stripped.
    // ---------------------------------------------------------------

    @Test
    fun extractUrlHost_httpsUrlWithQuery_returnsHost() {
        assertEquals(
            "www.youtube.com",
            extractUrlHost("https://www.youtube.com/watch?v=abc")
        )
    }

    @Test
    fun extractUrlHost_bareHostWithPath_returnsHost() {
        assertEquals("youtube.com", extractUrlHost("youtube.com/feed"))
    }

    @Test
    fun extractUrlHost_credentialsAndPort_returnsHostOnly() {
        assertEquals(
            "sub.example.com",
            extractUrlHost("https://user:pass@sub.example.com:8443/a?b=c")
        )
    }

    @Test
    fun extractUrlHost_empty_returnsNull() {
        assertNull(extractUrlHost(""))
    }

    @Test
    fun extractUrlHost_plainText_returnsNull() {
        assertNull(extractUrlHost("hello world"))
    }

    @Test
    fun extractUrlHost_trailingDot_isStripped() {
        assertEquals("example.com", extractUrlHost("https://example.com."))
    }

    // ---------------------------------------------------------------
    // 2. SUBSTRING-MATCH, part B — matchBlockedDomain: host-exact
    // semantics. A listed domain matches the host itself or any
    // subdomain of it — never a lookalike, a parent, or a mention
    // elsewhere in the URL text.
    // ---------------------------------------------------------------

    @Test
    fun matchBlockedDomain_wwwSubdomain_matchesListedDomain() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain(
                "https://www.youtube.com/watch?v=abc",
                listOf("youtube.com")
            )
        )
    }

    @Test
    fun matchBlockedDomain_lookalikeHost_doesNotMatch() {
        assertNull(
            matchBlockedDomain(
                "https://notyoutube.com/",
                listOf("youtube.com")
            )
        )
    }

    @Test
    fun matchBlockedDomain_listedDomainAsSubdomainOfOtherHost_doesNotMatch() {
        assertNull(
            matchBlockedDomain(
                "https://youtube.com.evil.example/",
                listOf("youtube.com")
            )
        )
    }

    @Test
    fun matchBlockedDomain_domainInQuery_doesNotMatch() {
        assertNull(
            matchBlockedDomain(
                "https://example.com/?next=youtube.com",
                listOf("youtube.com")
            )
        )
    }

    @Test
    fun matchBlockedDomain_mobileSubdomain_matchesListedDomain() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain(
                "https://m.youtube.com/",
                listOf("youtube.com")
            )
        )
    }

    @Test
    fun matchBlockedDomain_emptyList_matchesNothing() {
        assertNull(
            matchBlockedDomain(
                "https://www.youtube.com/watch?v=abc",
                emptyList()
            )
        )
    }

    @Test
    fun matchBlockedDomain_wildcardEntry_matchesSubdomainAndReturnsBareDomain() {
        assertEquals(
            "youtube.com",
            matchBlockedDomain(
                "https://www.youtube.com/",
                listOf("*.youtube.com")
            )
        )
    }

    // ---------------------------------------------------------------
    // 3. ENGLISH-TEXT-HEURISTIC — isTamperActionViewId: only the
    // view ids of the actual tamper actions count, whatever
    // language the Settings window happens to be rendered in.
    // ---------------------------------------------------------------

    @Test
    fun isTamperActionViewId_uninstallButton_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/uninstall_button")
        )
    }

    @Test
    fun isTamperActionViewId_forceStopButton_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/force_stop_button")
        )
    }

    @Test
    fun isTamperActionViewId_clearDataButton_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/clear_data_button")
        )
    }

    @Test
    fun isTamperActionViewId_entityHeaderTitle_isNotTamperAction() {
        assertFalse(
            isTamperActionViewId("com.android.settings:id/entity_header_title")
        )
    }

    @Test
    fun isTamperActionViewId_null_isNotTamperAction() {
        assertFalse(isTamperActionViewId(null))
    }

    @Test
    fun isTamperActionViewId_empty_isNotTamperAction() {
        assertFalse(isTamperActionViewId(""))
    }
}
