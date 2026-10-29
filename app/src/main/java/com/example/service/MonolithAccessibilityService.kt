package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.MainActivity
import com.example.data.AppBlockTier
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Enforces real-time platform-level protections during an active focus session:
 * 1. Active Phone & VoIP Call Bypass: Checks AudioManager.mode (MODE_RINGTONE, MODE_IN_CALL,
 *    MODE_IN_COMMUNICATION) and exempts ONLY the call's own surface (dialer / in-call UI packages) —
 *    incoming WhatsApp/Telegram/cellular calls are never kicked on their own screen, and a call
 *    never suspends blocking for other apps (Wave-3 #42; the bypass used to be global).
 * 2. Settings & Package Installer Blocker: Blocks attempts to Force stop, Uninstall, Clear storage,
 *    or Deactivate Monolith in Android Settings, OEM package installers, and app markets showing
 *    Monolith's page. Detection keys on locale-independent app-info button resource IDs first,
 *    with the English text match kept only as a fallback (Wave-3 #42).
 * 3. YouTube Shorts & Instagram Reels Sniper: Checks BOTH internal View IDs AND selected bottom-navigation
 *    tabs (`node.isSelected` with text/description "Shorts" or "Reels"). Remains strictly active even during Safe Breaks.
 *    Gated by the persisted Guard switch `shortsReelsSniperEnabled` (Truth Car 3, UNA-01) — OFF leaves feeds alone.
 * 4. In-Browser URL Bar Blocker: Ignores the address bar while the user is actively typing (`node.isFocused == true`),
 *    and blocks distracting domains once committed/loaded—even during Safe Breaks.
 * 5. 3-Tier Per-App & Safe Break Enforcement:
 *    - During a 10-minute Safe Break (`activeSession.isInSafeBreak`), `2m Peek` apps (e.g., WhatsApp) unlock freely
 *      while `Locked` apps, Shorts, Reels, and blocked browser domains stay strictly locked.
 *    - During study blocks, `2m Peek` apps are allowed only while `SystemClock.elapsedRealtime() < appRule.activePeekUntilElapsedRealtime`.
 */
/** Which door fired an intercept (Truth Car 3, UNA2-01). The airlock's
 *  peek offer must be computed from the PATH as well as the tier:
 *  only the two app-tier paths ever consult the peek lease — the
 *  accessibility tier path reads the row's lease before it lets an
 *  app through, and the shield monitor's blocked set excludes a
 *  leased PEEK app — so only on those doors can a lease actually be
 *  honoured. The guard, sniper and site doors intercept
 *  unconditionally; an offer made there protects nothing. UNKNOWN is
 *  the conservative default for any intent that arrives without a
 *  path: unproven honour means no offer. */
enum class InterceptPath {
    APP_TIER,
    SHIELD_APP,
    UNINSTALL_GUARD,
    SNIPER,
    SITE,
    UNKNOWN;

    /** A lease started from this door will actually be read by the
     *  enforcing code that fired it. */
    val canHonourPeekLease: Boolean
        get() = this == APP_TIER || this == SHIELD_APP
}

/**
 * The browser blocker's matching core, pure (Truth Car 3, UNA-06;
 * Wave-3 #42): the FIRST stored domain that is the URL's HOST or a
 * parent of it, or null. Host-exact matching replaced raw substring
 * matching: before the cure, a stored domain appearing anywhere in
 * the URL text — a query parameter, a path segment, another site's
 * subdomain label (`notyoutube.com`, `youtube.com.evil.example`) —
 * fired the blocker, and the false positives were indistinguishable
 * from real hits. The list it receives IS the enforced list —
 * callers pass the user's stored sites unchanged, so an empty list
 * matches nothing. There is no factory fallback anywhere upstream
 * of this function.
 */
internal fun matchBlockedDomain(urlText: String, domains: List<String>): String? {
    val host = extractUrlHost(urlText) ?: return null
    // Returns the NORMALISED stored domain (a "*.youtube.com" entry
    // reports as "youtube.com") — the value names the blocked site
    // on the intercept screen, so it must be the site, not the
    // pattern syntax the user happened to type.
    return domains.asSequence()
        .map { raw ->
            raw.trim().lowercase()
                .removePrefix("*.").substringBefore('/').substringBefore(':')
        }
        .firstOrNull { domain ->
            domain.isNotEmpty() && (host == domain || host.endsWith(".$domain"))
        }
}

/** The host of a URL-bar text, or null when the text carries no
 *  plausible host. Tolerates a missing scheme (users see bare
 *  hosts in some bars), userinfo, ports, paths, queries, fragments. */
internal fun extractUrlHost(urlText: String): String? {
    var rest = urlText.trim().lowercase()
    if (rest.isEmpty() || rest.contains(' ')) return null
    val schemeEnd = rest.indexOf("://")
    if (schemeEnd >= 0) rest = rest.substring(schemeEnd + 3)
    rest = rest.substringBefore('/').substringBefore('?').substringBefore('#')
    if (rest.contains('@')) rest = rest.substringAfterLast('@')
    val host = if (rest.startsWith("[")) {
        rest.substringAfter('[').substringBefore(']')
    } else {
        rest.substringBefore(':')
    }.trimEnd('.')
    return host.ifEmpty { null }
}

/**
 * Call-surface packages (Wave-3 #42): the ONLY packages a live call
 * may exempt. Before the cure, `isCallActive()` was a global
 * kill-switch — AudioManager in a call mode suspended EVERY block
 * for EVERY app, so an incoming call opened a hole the size of the
 * whole session: switch to any blocked app mid-ring and nothing
 * fired. The bypass now belongs to the call's own surface: the
 * system dialer / in-call UI packages, plus any package whose name
 * marks it as a dialer or in-call UI (OEM and VoIP dialers name
 * themselves so). A call app that is itself blocked (e.g. WhatsApp
 * in a call) is still exempt on its OWN surface — that is the
 * shipped promise — but its call no longer unblocks anything else.
 */
internal val CALL_SURFACE_PACKAGES = setOf(
    "com.google.android.dialer",
    "com.android.dialer",
    "com.android.incallui",
    "com.google.android.incallui",
    "com.samsung.android.dialer",
    "com.samsung.android.incallui",
    "com.android.server.telecom",
    "com.android.phone"
)

internal fun isCallSurfacePackage(packageName: String): Boolean =
    packageName in CALL_SURFACE_PACKAGES ||
        packageName.endsWith(".dialer") ||
        packageName.endsWith(".incallui") ||
        packageName.contains(".incallui.")

/** Pure decision: does a live call exempt THIS event's package? */
internal fun shouldBypassForCall(audioMode: Int, packageName: String): Boolean {
    val callMode = audioMode == AudioManager.MODE_RINGTONE ||
        audioMode == AudioManager.MODE_IN_CALL ||
        audioMode == AudioManager.MODE_IN_COMMUNICATION
    return callMode && isCallSurfacePackage(packageName)
}

/** Installer surfaces that can force-stop / uninstall Monolith
 *  (Wave-3 #42): the system package installers across OEMs. An
 *  installer window only exists during an install/uninstall flow,
 *  so presence alone is the signal (the pre-cure behaviour for the
 *  original four, kept). */
internal val INSTALLER_PACKAGES = setOf(
    "com.google.android.packageinstaller",
    "com.android.packageinstaller",
    "com.samsung.android.packageinstaller",
    "com.miui.packageinstaller",
    "com.coloros.packageinstaller",
    "com.oplus.packageinstaller",
    "com.bbk.packageinstaller",
    "com.huawei.packageinstaller"
)

/** App markets that host an uninstall action on an app's page.
 *  Unlike installers they also host ordinary browsing, so the
 *  guard fires only when the window is about Monolith itself. */
internal val MARKET_PACKAGES = setOf(
    "com.android.vending",
    "com.sec.android.app.samsungapps",
    "com.xiaomi.mipicks",
    "com.heytap.market",
    "com.oppo.market",
    "com.bbk.appstore",
    "com.huawei.appmarket"
)

/** Settings surfaces whose app-info page can force-stop, disable,
 *  clear, or uninstall Monolith. */
internal val SETTINGS_PACKAGES = setOf(
    "com.android.settings",
    "com.miui.securitycenter"
)

/**
 * Locale-independent tamper evidence (Wave-3 #42): the resource
 * IDs of the app-info action buttons are code identifiers — they
 * do not translate. Before the cure the guard matched English
 * button TEXT ("force stop", "uninstall", …), so a phone in any
 * other language walked straight past it. A Settings window that
 * carries one of these buttons is an app-info screen in every
 * locale Android ships.
 */
internal fun isTamperActionViewId(viewIdResourceName: String?): Boolean {
    val id = viewIdResourceName?.lowercase() ?: return false
    return id.endsWith(":id/uninstall_button") ||
        id.endsWith(":id/force_stop_button") ||
        id.endsWith(":id/disable_button") ||
        id.endsWith(":id/clear_data_button") ||
        id.endsWith(":id/clear_storage_button") ||
        id.endsWith(":id/button_uninstall") ||
        id.endsWith(":id/button_force_stop") ||
        id.endsWith(":id/button_disable")
}

class MonolithAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        // Live Wire registry (Ines): the service proves its own
        // liveness by EXISTING. In-memory only - proof is never
        // persisted; persisted proof is stale proof.
        @Volatile
        var instance: MonolithAccessibilityService? = null
            internal set

        /** Monotonic stamp of the last event whose root window was
         *  successfully acquired. Pipeline proof (the service is
         *  bound and processing this foreground), not session proof. */
        @Volatile
        var lastRootMono: Long = 0L
            internal set

        /** Cascade witness (Slice D step 4, Opal L3): the session
         *  this service's own session gate has observed as active,
         *  and the monotonic instant it was observed. The stamp is
         *  written by the session collector started in
         *  onServiceConnected - the service's event path early-
         *  returns when no active session exists, so this Flow
         *  observation is the moment its gate opens for the row.
         *  -1 / 0 when no session is armed. The stamp dies with
         *  the binding (onUnbind), like lastRootMono. */
        @Volatile
        var armedSessionId: Int = -1
            internal set

        @Volatile
        var armedMono: Long = 0L
            internal set

        /** Truth Car 3 (UNA-01): the sniper's gate. The sniper
         *  fires only where its persisted switch says so — OFF
         *  means Shorts and Reels are left alone. A missing
         *  settings row reads ON: that was the shipped behaviour
         *  before the switch existed, and the absence of a store
         *  must never silently disarm an interceptor. */
        fun shortsSniperArmed(settings: GuardSettingsEntity?, packageName: String): Boolean =
            (settings?.shortsReelsSniperEnabled ?: true) &&
                (packageName == "com.google.android.youtube" ||
                    packageName == "com.instagram.android")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        // Watch Store BIND writer (chrome C3): the binding itself is
        // the measurement; the store stamps it.
        WatchStore.onAccessibilityBound(true)
        startSessionArmedCollector()
        startEventWorker()
    }

    // ------------------------------------------------------------------
    // Event pipeline (Wave-3 #42): before the cure, EVERY relevant
    // event launched its own unbounded IO coroutine, and each one
    // re-read rootInActiveWindow AFTER async dispatch — the root it
    // inspected could belong to a different, later window than the
    // event it claimed to police, while a burst of window-content
    // events fanned out into dozens of racing DB reads (two full
    // getAllAppsSnapshot() calls per event among them). The pipeline
    // now has ONE worker and a CONFLATED channel:
    //  - onAccessibilityEvent captures every node-derived fact
    //    SYNCHRONOUSLY, on the event thread, while the root still
    //    describes this event's window;
    //  - the worker processes snapshots strictly sequentially;
    //  - a burst conflates to the latest snapshot — which is the
    //    only window that can still be on screen anyway;
    //  - the app table is read ONCE per processed event and shared
    //    by the lease check and the tier door.
    // ------------------------------------------------------------------

    /** Node-derived facts about one event's window, captured
     *  synchronously before any async hop. */
    private data class EventFacts(
        val tamperSurface: Boolean = false,
        val sniperFeedVisible: Boolean = false,
        val browserUrlTexts: List<String> = emptyList()
    )

    private data class EventSnapshot(
        val packageName: String,
        val facts: EventFacts
    )

    private val eventChannel = Channel<EventSnapshot>(Channel.CONFLATED)

    private fun startEventWorker() {
        scope.launch {
            for (snapshot in eventChannel) {
                try {
                    processEvent(snapshot)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // One bad event must not kill the pipeline: the
                    // next snapshot still deserves its verdict.
                }
            }
        }
    }

    /** L3's collector: watches the active-session Flow for the
     *  life of this binding and stamps the armed witness. Runs on
     *  the service's existing scope (cancelled in onDestroy -
     *  Opal R-2: a standing collector must not leak across
     *  rebinds; the scope is per-instance, so a rebind creates a
     *  new service instance and a new scope with it). */
    private fun startSessionArmedCollector() {
        val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
        scope.launch {
            dao.getActiveSessionFlow().collect { session ->
                if (session != null && session.isActive) {
                    if (armedSessionId != session.id) {
                        armedSessionId = session.id
                        armedMono = SystemClock.elapsedRealtime()
                    }
                } else if (armedSessionId != -1) {
                    armedSessionId = -1
                    armedMono = 0L
                }
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (instance === this) {
            instance = null
            // The stamp dies with the binding (Register 3 Z-1): a
            // rebound service must never inherit its predecessor's
            // proof of life.
            lastRootMono = 0L
            armedSessionId = -1
            armedMono = 0L
            WatchStore.onAccessibilityBound(false)
        }
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Live Wire stamp, taken BEFORE any early return below
        // (Rosa's stamp-before-return ordering): the stamp proves
        // the event pipeline processed this foreground, not that a
        // session exists - so it is taken on every event, including
        // the ones the blocking logic will ignore. A failed root
        // acquisition stamps nothing: the wire reads dead, which
        // is the truth.
        try {
            if (rootInActiveWindow != null) {
                lastRootMono = SystemClock.elapsedRealtime()
                WatchStore.poke()
            }
        } catch (_: Exception) {
            // No stamp.
        }

        val pkgName = event?.packageName?.toString() ?: return
        if (pkgName == applicationContext.packageName || pkgName.startsWith("com.android.systemui")) return

        // Opportunistic routine reconcile (throttled to 30 s inside
        // the heartbeat): window events are proof the device is awake
        // and in use, so a missed boundary gets caught here too.
        RoutineHeartbeat.tickOpportunistic(applicationContext)

        // Session gate BEFORE any window content is read (Wave-3
        // #42): with no session armed, this service reads nothing,
        // traverses nothing, and launches no DB work. The pre-cure
        // path acquired the root window and fanned out DB reads for
        // every event on the phone, session or not — maximum
        // accessibility power exercised around the clock, under a
        // one-line disclosure that mentioned none of it. (The
        // liveness stamp above is untouched by this gate: it checks
        // only that a root EXISTS, never its content.)
        if (armedSessionId == -1) return

        // 1. Active Phone & VoIP Call Bypass — PER PACKAGE (Wave-3
        // #42): a live call exempts the call's own surface only.
        // WhatsApp/Telegram/cellular calls are never blocked on
        // their own screen; the call no longer suspends blocking
        // for every other app on the phone.
        if (isCallActiveForPackage(pkgName)) return

        // Synchronous fact capture: every node-derived verdict is
        // taken HERE, while rootInActiveWindow still describes
        // this event's window — never after an async hop.
        val facts = captureEventFacts(pkgName)
        eventChannel.trySend(EventSnapshot(pkgName, facts))
    }

    /** Reads the root window ONCE for the packages whose doors
     *  need node evidence (installer/settings surfaces, the two
     *  sniper apps, supported browsers) and distils it into plain
     *  facts. All other packages need no content at all — the tier
     *  door works from the package name alone. */
    private fun captureEventFacts(pkgName: String): EventFacts {
        val needsRoot = pkgName in INSTALLER_PACKAGES ||
            pkgName in MARKET_PACKAGES ||
            pkgName in SETTINGS_PACKAGES ||
            pkgName == "com.google.android.youtube" ||
            pkgName == "com.instagram.android" ||
            isSupportedBrowser(pkgName)
        if (!needsRoot) return EventFacts()
        val rootNode = try { rootInActiveWindow } catch (_: Exception) { null }
            ?: return EventFacts()
        return EventFacts(
            tamperSurface = isPackageInstallerOrTamperSettings(pkgName, rootNode),
            sniperFeedVisible = detectShortsOrReelsFeed(rootNode),
            browserUrlTexts = if (isSupportedBrowser(pkgName)) {
                extractBrowserUrlTexts(rootNode)
            } else {
                emptyList()
            }
        )
    }

    /** The ONE worker's per-event body: sequential, single-flight,
     *  fed only by synchronously captured facts. The app table is
     *  read once and shared by the lease check and the tier door
     *  (the pre-cure fan-out read it twice per event per coroutine). */
    private suspend fun processEvent(snapshot: EventSnapshot) {
        val pkgName = snapshot.packageName
        val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
        val activeSession = dao.getActiveSessionSnapshot() ?: return
        if (!activeSession.isActive) return

        // The persisted Guard row, read once per event and
        // honoured by both interceptors below (the browser
        // step read it alone before Truth Car 3; the sniper
        // read nothing at all — UNA-01).
        val guardSettings = dao.getGuardSettingsSnapshot()

        // 2. Settings & Package Installer Blocker
        if (snapshot.facts.tamperSurface) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            launchInterceptScreen(
                packageName = pkgName,
                appName = "System Settings Guard",
                reasonBadge = "Uninstall Guard",
                path = InterceptPath.UNINSTALL_GUARD
            )
            return
        }

        // UNA2-01, the lease agreement: a live peek lease on
        // THIS package silences the sniper and the site blocker
        // too — a lease is honoured by every door or it is
        // honoured by none. The per-app door (branch 5) and the
        // shield service's set filter already read the lease;
        // these two doors fired blind, so a peek bought on the
        // per-app door could be re-intercepted mid-lease by the
        // sniper — the lease protecting nothing again.
        val appsSnapshot = dao.getAllAppsSnapshot()
        val leaseRow = appsSnapshot.find { it.packageName == pkgName }
        val leaseLive = leaseRow != null &&
            leaseRow.resolvedTier == AppBlockTier.PEEK_2M &&
            SystemClock.elapsedRealtime() < leaseRow.activePeekUntilElapsedRealtime

        // 3. YouTube Shorts & Instagram Reels Sniper (Strictly active during both Work Blocks and Safe Breaks — and only while its persisted switch is ON: UNA-01)
        if (!leaseLive && shortsSniperArmed(guardSettings, pkgName) && snapshot.facts.sniperFeedVisible) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            launchInterceptScreen(
                packageName = pkgName,
                appName = if (pkgName.contains("youtube")) "YouTube Shorts" else "Instagram Reels",
                reasonBadge = if (pkgName.contains("youtube")) "Shorts Blocked" else "Reels Blocked",
                path = InterceptPath.SNIPER
            )
            return
        }

        // 4. In-Browser URL Bar Blocker (Strictly active during both Work Blocks and Safe Breaks; ignores focused typing)
        // Enforces the USER'S persisted site list + persisted Guard toggle (Ren).
        // UNA-06 (Truth Car 3): the stored list IS the enforced list.
        // There is NO factory fallback — an emptied list blocks no
        // websites, exactly as Shield's "0 SITES" print says. (The
        // factory defaults live in the seeded stored list itself,
        // his to edit or empty; they are never smuggled back in at
        // enforcement time.) The URL texts were captured
        // synchronously with the event; the domain match itself is
        // host-exact (Wave-3 #42).
        if (!leaseLive && (guardSettings?.browserBlockerEnabled ?: true) && isSupportedBrowser(pkgName)) {
            val userDomains = dao.getAllSitesSnapshot().map { it.domain }
            val matchedDomain = snapshot.facts.browserUrlTexts.firstNotNullOfOrNull { urlText ->
                matchBlockedDomain(urlText, userDomains)
            }
            if (matchedDomain != null) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                launchInterceptScreen(
                    packageName = pkgName,
                    appName = matchedDomain,
                    reasonBadge = "Site Blocked",
                    path = InterceptPath.SITE
                )
                return
            }
        }

        // 5. 3-Tier Per-App & Safe Break Blocking
        val appRule = appsSnapshot.find { it.packageName == pkgName } ?: return
        val tier = appRule.resolvedTier
        if (tier == AppBlockTier.OPEN) return

        // Safe Break Rule: 2m Peek apps unlock freely during the 10-minute Safe Break, while Locked apps stay locked
        if (activeSession.isInSafeBreak && tier == AppBlockTier.PEEK_2M) {
            return
        }

        // Active 2-Minute Peek Lease Rule: allow access while monotonic clock is within the 2m lease window
        val nowMono = SystemClock.elapsedRealtime()
        if (tier == AppBlockTier.PEEK_2M && nowMono < appRule.activePeekUntilElapsedRealtime) {
            return
        }

        launchInterceptScreen(
            packageName = appRule.packageName,
            appName = appRule.appName,
            reasonBadge = tier.badge,
            path = InterceptPath.APP_TIER
        )
    }

    private fun isCallActiveForPackage(pkgName: String): Boolean {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            shouldBypassForCall(am.mode, pkgName)
        } catch (_: Exception) {
            false
        }
    }

    private fun isPackageInstallerOrTamperSettings(pkgName: String, rootNode: AccessibilityNodeInfo?): Boolean {
        if (pkgName in INSTALLER_PACKAGES) return true
        if (rootNode == null) return false

        if (pkgName in MARKET_PACKAGES) {
            // A market window is only a tamper surface when it is
            // showing Monolith's own page (where its uninstall
            // action lives); ordinary market browsing is untouched.
            return collectWindowTextLowercase(rootNode).contains("monolith")
        }

        if (pkgName in SETTINGS_PACKAGES) {
            val dump = collectWindowTextLowercase(rootNode)
            val mentionsMonolith = dump.contains("monolith")
            if (!mentionsMonolith) return false
            // Primary evidence is locale-independent: the app-info
            // action buttons' resource IDs. The English text match
            // survives only as a fallback for OEM skins that rename
            // the IDs — it is no longer the only door.
            val mentionsTamperAction = dump.contains("force stop") ||
                dump.contains("deactivate") ||
                dump.contains("uninstall") ||
                dump.contains("clear storage") ||
                dump.contains("clear data") ||
                dump.contains("device admin")
            return hasTamperActionButton(rootNode) || mentionsTamperAction
        }
        return false
    }

    /** Recursive scan for an app-info action button by resource ID. */
    private fun hasTamperActionButton(node: AccessibilityNodeInfo?, depth: Int = 0): Boolean {
        if (node == null || depth > 14) return false
        if (isTamperActionViewId(node.viewIdResourceName)) return true
        for (i in 0 until node.childCount) {
            if (hasTamperActionButton(node.getChild(i), depth + 1)) return true
        }
        return false
    }

    private fun detectShortsOrReelsFeed(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
        // Strategy A: Known internal View IDs
        val shortsViewIds = listOf(
            "com.google.android.youtube:id/reel_recycler",
            "com.google.android.youtube:id/reel_player_page_container",
            "com.google.android.youtube:id/shorts_player",
            "com.instagram.android:id/clips_viewer_view_pager",
            "com.instagram.android:id/reel_viewer_fragment"
        )
        for (id in shortsViewIds) {
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(id) } catch (_: Exception) { null }
            if (!nodes.isNullOrEmpty()) return true
        }
        // Strategy B: Resilient selected bottom-navigation tab inspection (`node.isSelected`)
        return isSelectedBottomTabShortsOrReels(rootNode)
    }

    private fun isSelectedBottomTabShortsOrReels(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
        if (node.isSelected && (
                desc == "shorts" || desc == "reels" ||
                    text == "shorts" || text == "reels" ||
                    desc.startsWith("shorts,") || desc.startsWith("reels,")
            )
        ) {
            return true
        }
        // Structural fallback (Wave-3 #42): a SELECTED node whose
        // resource ID names the feed tab. Resource IDs are code
        // identifiers — they survive locales that translate the
        // tab's spoken label, which the text match above cannot.
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        if (node.isSelected && (
                viewId.contains("shorts") || viewId.contains("reels") ||
                    viewId.contains("clips_tab") || viewId.contains("clips_entry")
            )
        ) {
            return true
        }
        for (i in 0 until node.childCount) {
            if (isSelectedBottomTabShortsOrReels(node.getChild(i))) return true
        }
        return false
    }

    private fun isSupportedBrowser(pkgName: String): Boolean {
        return pkgName in setOf(
            "com.android.chrome",
            "com.brave.browser",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.sec.android.app.sbrowser",
            "com.duckduckgo.mobile.android",
            "com.vivaldi.browser",
            "com.opera.browser",
            "com.opera.browser.beta"
        )
    }

    /**
     * Captures the browser URL bar's text across Chrome, Brave,
     * Firefox, Edge, Samsung Internet, DuckDuckGo, Vivaldi and
     * Opera (Wave-3 #42). Bars are found by resource-ID SUFFIX, not
     * by a full package-qualified ID list: Chromium forks and OEM
     * skins reuse the same bar IDs under their own package names,
     * and the old closed list silently read nothing on any browser
     * it did not name. The focused bar is skipped — never trigger
     * while the user is actively typing. Runs synchronously on the
     * event thread; the domain match happens later, in the worker,
     * against these captured texts.
     */
    private fun extractBrowserUrlTexts(rootNode: AccessibilityNodeInfo): List<String> {
        val texts = mutableListOf<String>()
        collectUrlBarTexts(rootNode, 0, texts)
        return texts
    }

    private fun collectUrlBarTexts(
        node: AccessibilityNodeInfo?,
        depth: Int,
        out: MutableList<String>
    ) {
        if (node == null || depth > 16 || out.size >= 4) return
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        if (!node.isFocused && (
                viewId.endsWith(":id/url_bar") ||
                    viewId.endsWith(":id/location_bar_edit_text") ||
                    viewId.endsWith(":id/mozac_browser_toolbar_url_view")
                )
        ) {
            node.text?.toString()?.lowercase()?.takeIf { it.isNotBlank() }?.let { out.add(it) }
        }
        for (i in 0 until node.childCount) {
            collectUrlBarTexts(node.getChild(i), depth + 1, out)
        }
    }

    private fun collectWindowTextLowercase(node: AccessibilityNodeInfo?, depth: Int = 0): String {
        if (node == null || depth > 14) return ""
        val sb = StringBuilder()
        node.text?.let { sb.append(it).append(' ') }
        node.contentDescription?.let { sb.append(it).append(' ') }
        for (i in 0 until node.childCount) {
            sb.append(collectWindowTextLowercase(node.getChild(i), depth + 1))
        }
        return sb.toString().lowercase()
    }

    private fun launchInterceptScreen(
        packageName: String,
        appName: String,
        reasonBadge: String,
        path: InterceptPath
    ) {
        val interceptIntent = Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE, packageName)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, appName)
            putExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE, reasonBadge)
            putExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH, path.name)
        }
        startActivity(interceptIntent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        if (instance === this) {
            instance = null
            lastRootMono = 0L
            armedSessionId = -1
            armedMono = 0L
            WatchStore.onAccessibilityBound(false)
        }
        scope.cancel()
        super.onDestroy()
    }
}
