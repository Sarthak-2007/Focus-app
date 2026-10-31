package com.example

import android.media.AudioManager
import com.example.service.extractUrlHost
import com.example.service.isCallControlLabel
import com.example.service.isCallControlViewId
import com.example.service.isCallSurfaceClassName
import com.example.service.isCallSurfacePackage
import com.example.service.isTamperActionViewId
import com.example.service.matchBlockedDomain
import com.example.service.shouldBypassForCall
import com.example.service.shouldLockCallOwner
import com.example.service.windowCarriesCallControls
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

    @Test
    fun isTamperActionViewId_disableButton_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/disable_button")
        )
    }

    @Test
    fun isTamperActionViewId_clearStorageButton_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/clear_storage_button")
        )
    }

    @Test
    fun isTamperActionViewId_buttonUninstallVariant_isTamperAction() {
        assertTrue(
            isTamperActionViewId("com.android.settings:id/button_uninstall")
        )
    }

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part C — the OWNER and DEFAULT-DIALER
    // legs (Verifier round): a VoIP call's surface is the app's own
    // package, which no name rule can enumerate. The owner leg is
    // what keeps the shipped promise — a blocked WhatsApp in a call
    // is exempt on its own screen — without reopening the hole:
    // the same WhatsApp, NOT owning the call, stays blocked.
    //
    // Issue #520 (5-C) narrowed this leg: the owner is exempt ONLY
    // while its call surface is in front (ownerSurfaceVisible).
    // The two bypass tests below therefore pass the surface fact
    // explicitly — the promise they guard ("exempt on its own
    // screen") is unchanged; what died is the old reading under
    // which owning a call exempted the whole app, surface or not.
    // That dead reading is pinned as a denial further down.
    // PIN-SCOPE: PLUMBING (pure decision functions; parts C–F).
    // ---------------------------------------------------------------

    @Test
    fun shouldBypassForCall_voipAppOwningTheCall_bypasses() {
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.whatsapp",
                callOwnerPackage = "com.whatsapp",
                ownerSurfaceVisible = true
            )
        )
    }

    @Test
    fun shouldBypassForCall_voipAppNotOwningTheCall_staysBlocked() {
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.whatsapp",
                callOwnerPackage = "org.telegram.messenger"
            )
        )
    }

    @Test
    fun shouldBypassForCall_ringingVoipApp_bypasses() {
        // The ringing screen IS the call surface — the surface
        // fact stands while it is in front (issue #520).
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_RINGTONE,
                "org.telegram.messenger",
                callOwnerPackage = "org.telegram.messenger",
                ownerSurfaceVisible = true
            )
        )
    }

    @Test
    fun shouldBypassForCall_defaultDialer_bypasses() {
        // Replacement dialers (Truecaller et al.) are covered by
        // the Telecom role, not by any name rule.
        assertTrue(
            shouldBypassForCall(
                AudioManager.MODE_IN_CALL,
                "com.truecaller",
                defaultDialerPackage = "com.truecaller"
            )
        )
    }

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part D — issue #520 (5-C): the owner
    // leg exempts the call SURFACE, not the app. The same owner,
    // mid-call, on any other of its own windows (chats, Status,
    // feeds) stays blocked.
    // ---------------------------------------------------------------

    @Test
    fun shouldBypassForCall_voipOwnerOffItsCallSurface_staysBlocked() {
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.whatsapp",
                callOwnerPackage = "com.whatsapp",
                ownerSurfaceVisible = false
            )
        )
    }

    @Test
    fun shouldBypassForCall_surfaceFactAloneWithoutOwnership_staysBlocked() {
        // A call surface standing in SOME window does not exempt
        // a package that does not own the call.
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_COMMUNICATION,
                "com.whatsapp",
                callOwnerPackage = "org.telegram.messenger",
                ownerSurfaceVisible = true
            )
        )
    }

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part E — issue #520 (C-11): the LOCK.
    // Communication mode alone never mints an owner; one proof
    // (surface package, dialer role, Telecom call, or a window
    // carrying call controls) must stand.
    // ---------------------------------------------------------------

    @Test
    fun shouldLockCallOwner_modeFlipAlone_noLock() {
        // The exploit as filed: a game foreground flips
        // MODE_IN_COMMUNICATION (voice chat). No proof stands.
        assertFalse(
            shouldLockCallOwner(
                AudioManager.MODE_IN_COMMUNICATION,
                candidatePackage = "com.example.voicegame"
            )
        )
    }

    @Test
    fun shouldLockCallOwner_surfaceProvenVoip_locks() {
        assertTrue(
            shouldLockCallOwner(
                AudioManager.MODE_IN_COMMUNICATION,
                candidatePackage = "com.whatsapp",
                candidateSurfaceVisible = true
            )
        )
    }

    @Test
    fun shouldLockCallOwner_telecomCallActive_locks() {
        assertTrue(
            shouldLockCallOwner(
                AudioManager.MODE_IN_CALL,
                candidatePackage = "com.example.selfmanagedvoip",
                telecomCallActive = true
            )
        )
    }

    @Test
    fun shouldLockCallOwner_surfacePackageCandidate_locks() {
        assertTrue(
            shouldLockCallOwner(
                AudioManager.MODE_RINGTONE,
                candidatePackage = "com.android.incallui"
            )
        )
    }

    @Test
    fun shouldLockCallOwner_dialerRoleCandidate_locks() {
        assertTrue(
            shouldLockCallOwner(
                AudioManager.MODE_IN_CALL,
                candidatePackage = "com.truecaller",
                defaultDialerPackage = "com.truecaller"
            )
        )
    }

    @Test
    fun shouldLockCallOwner_normalMode_neverLocks() {
        // Every proof offered, no call mode: no lock.
        assertFalse(
            shouldLockCallOwner(
                AudioManager.MODE_NORMAL,
                candidatePackage = "com.whatsapp",
                defaultDialerPackage = "com.whatsapp",
                telecomCallActive = true,
                candidateSurfaceVisible = true
            )
        )
    }

    @Test
    fun shouldLockCallOwner_noCandidate_noLock() {
        assertFalse(
            shouldLockCallOwner(
                AudioManager.MODE_IN_COMMUNICATION,
                candidatePackage = null,
                telecomCallActive = true,
                candidateSurfaceVisible = true
            )
        )
    }

    // ---------------------------------------------------------------
    // 1. GLOBAL-KILL-SWITCH, part F — issue #520 (5-C): the
    // visible-surface test itself. A window is a call surface by
    // what it CARRIES: a call-activity class name, a call-control
    // view id, or a call-control label. A chat window that merely
    // TALKS about a call ("Call ended") carries none of them.
    // ---------------------------------------------------------------

    @Test
    fun windowCarriesCallControls_whatsappVoiceCallClass_isSurface() {
        assertTrue(
            windowCarriesCallControls(
                "com.whatsapp.voicecalling.VoiceCallActivity",
                emptyList(), emptyList()
            )
        )
    }

    @Test
    fun windowCarriesCallControls_telegramVoipClass_isSurface() {
        assertTrue(isCallSurfaceClassName("org.telegram.ui.VoIPActivity"))
        assertTrue(
            windowCarriesCallControls("org.telegram.ui.VoIPActivity", emptyList(), emptyList())
        )
    }

    @Test
    fun windowCarriesCallControls_endCallViewId_isSurface() {
        assertTrue(isCallControlViewId("com.whatsapp:id/end_call_button"))
        assertTrue(
            windowCarriesCallControls(
                "com.whatsapp.MainActivity",
                listOf("com.whatsapp:id/conversation_text", "com.whatsapp:id/end_call_button"),
                listOf("Type a message")
            )
        )
    }

    @Test
    fun windowCarriesCallControls_endCallLabel_isSurface() {
        assertTrue(isCallControlLabel("End call"))
        assertTrue(isCallControlLabel("Hang up"))
        assertTrue(
            windowCarriesCallControls(
                "com.example.customvoip.CallHostActivity",
                emptyList(),
                listOf("Mute", "End call", "Speaker")
            )
        )
    }

    @Test
    fun windowCarriesCallControls_chatWindowMentioningACall_isNotSurface() {
        // The chat RECORD of a call is not a call surface: a
        // "Call ended" status line and ordinary composer ids mint
        // nothing.
        assertFalse(isCallControlLabel("Call ended"))
        assertFalse(
            windowCarriesCallControls(
                "com.whatsapp.Conversation",
                listOf("com.whatsapp:id/conversation_text", "com.whatsapp:id/input"),
                listOf("Call ended", "Type a message")
            )
        )
    }

    @Test
    fun windowCarriesCallControls_voipInPackageNameAlone_isNotSurface() {
        // The class leg judges the simple class name: an app
        // whose PACKAGE mentions voip does not carry the surface
        // on its ordinary windows.
        assertFalse(isCallSurfaceClassName("com.voipdiscount.app.HomeActivity"))
        assertFalse(
            windowCarriesCallControls(
                "com.voipdiscount.app.HomeActivity",
                listOf("com.voipdiscount.app:id/home_list"),
                listOf("Cheap international calls")
            )
        )
    }

    @Test
    fun windowCarriesCallControls_voiceGameWindow_isNotSurface() {
        // The C-11 shape at the window level: a voice-chat game
        // window carries no call controls.
        assertFalse(
            windowCarriesCallControls(
                "com.example.voicegame.GameActivity",
                listOf("com.example.voicegame:id/voice_chat_toggle"),
                listOf("Voice chat on", "Push to talk")
            )
        )
    }

    // ---------------------------------------------------------------
    // 4. ADVANCED-PROTECTION HONESTY (Scout-2 Find 11, surge round):
    // Android 17 Advanced Protection revokes Accessibility from
    // apps not declared as genuine accessibility tools. Monolith
    // is a blocker, NOT an accessibility tool — the config must
    // never claim isAccessibilityTool="true" to dodge revocation.
    // The pin is explicit false; absence would also default false,
    // but explicit is checkable, and this test is the check.
    // ---------------------------------------------------------------

    @Test
    fun accessibilityConfig_neverClaimsToBeAnAccessibilityTool() {
        val candidates = listOf(
            java.io.File("src/main/res/xml/accessibility_service_config.xml"),
            java.io.File("app/src/main/res/xml/accessibility_service_config.xml")
        )
        val config = candidates.firstOrNull { it.isFile }
        org.junit.Assume.assumeTrue(
            "accessibility_service_config.xml not found from test working dir",
            config != null
        )
        val text = config!!.readText()
        assertTrue(
            "config must pin isAccessibilityTool explicitly",
            text.contains("android:isAccessibilityTool=\"false\"")
        )
        assertFalse(
            "config must never falsely declare isAccessibilityTool=true",
            text.contains("isAccessibilityTool=\"true\"")
        )
    }

    @Test
    fun shouldBypassForCall_unrelatedDialerNamedApp_staysBlocked() {
        // A blocked third-party dialer that neither owns the call
        // nor holds the dialer role gets no exemption from its
        // name alone when a real owner exists.
        assertFalse(
            shouldBypassForCall(
                AudioManager.MODE_IN_CALL,
                "com.truecaller",
                callOwnerPackage = "com.google.android.dialer",
                defaultDialerPackage = "com.google.android.dialer"
            )
        )
    }
}
