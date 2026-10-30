package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioManager
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.MainActivity
import com.example.data.AppBlockTier
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Enforces real-time platform-level protections during an active focus session:
 * 1. Active Phone & VoIP Call Bypass: Checks AudioManager.mode (MODE_RINGTONE, MODE_IN_CALL,
 *    MODE_IN_COMMUNICATION) BEFORE any block check so incoming WhatsApp/Telegram/cellular calls are never kicked.
 * 2. Settings & Package Installer Blocker: Blocks attempts to Force stop, Uninstall, Clear storage,
 *    or Deactivate Monolith in Android Settings or system package installers.
 * 3. YouTube Shorts & Instagram Reels Sniper: Checks BOTH internal View IDs AND selected bottom-navigation
 *    tabs (`node.isSelected` with text/description "Shorts" or "Reels"). Remains strictly active even during Safe Breaks.
 *    Gated by the persisted Guard switch `shortsReelsSniperEnabled` (Truth Car 3, UNA-01) — OFF leaves feeds alone.
 *    A peek lease does NOT silence it (#131): the lease buys a time-boxed glance at the app, never a
 *    Shorts/Reels binge — the sniper is the feed-specific door, the lease is an app-access door.
 *    Scope, stated honestly (#131): the sniper polices ONLY the vertical Shorts/Reels feed. It does
 *    not police long-form video, home feeds, Stories or DMs — those surfaces are governed solely by
 *    the app's tier below (an OPEN app is open there because the user tiered it OPEN).
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
 * The browser blocker's matching core, pure (Truth Car 3, UNA-06):
 * the FIRST stored domain the URL text contains, or null. The list
 * it receives IS the enforced list — callers pass the user's stored
 * sites unchanged, so an empty list matches nothing. There is no
 * factory fallback anywhere upstream of this function.
 */
internal fun matchBlockedDomain(urlText: String, domains: List<String>): String? =
    domains.firstOrNull { domain -> urlText.contains(domain) }

/**
 * #131: the player/viewer surfaces whose VISIBLE presence means a
 * Short or Reel is actually on screen. Deliberately absent:
 * `com.google.android.youtube:id/reel_recycler` — that is the
 * Shorts SHELF container, present on home/channel/search screens
 * while the user is merely near Shorts, not watching one. Presence
 * of a shelf is proximity; only a visible player is watching.
 */
internal val SHORTS_PLAYER_VIEW_IDS: List<String> = listOf(
    "com.google.android.youtube:id/reel_player_page_container",
    "com.google.android.youtube:id/shorts_player",
    "com.instagram.android:id/clips_viewer_view_pager",
    "com.instagram.android:id/reel_viewer_fragment"
)

/** #131: the selected-tab label match, extracted pure. A label
 *  alone is NOT evidence of watching — the caller must also place
 *  the tab in the bottom-navigation region ([isBottomNavBounds]),
 *  so a selected 'Shorts' channel tab or search chip never fires. */
internal fun isFeedTabLabel(textLower: String, descLower: String): Boolean =
    descLower == "shorts" || descLower == "reels" ||
        textLower == "shorts" || textLower == "reels" ||
        descLower.startsWith("shorts,") || descLower.startsWith("reels,")

/** #131: a node sits in the bottom-navigation region when its
 *  bottom edge reaches the lowest fifth of the window. Channel
 *  tab strips and search filter chips live far above this line. */
internal fun isBottomNavBounds(boundsBottomPx: Int, rootHeightPx: Int): Boolean =
    rootHeightPx > 0 && boundsBottomPx >= (rootHeightPx * 4) / 5

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
        // Watch Store bind (issue #48): restore the persisted LAST
        // PROVED into the fresh process's store.
        WatchStore.bind(applicationContext)
        // Watch Store BIND writer (chrome C3): the binding itself is
        // the measurement; the store stamps it.
        WatchStore.onAccessibilityBound(true)
        startSessionArmedCollector()
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

        // 1. Active Phone & VoIP Call Bypass (WhatsApp, Telegram, Cellular calls never blocked mid-ring)
        if (isCallActive()) return

        scope.launch {
            val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
            val activeSession = dao.getActiveSessionSnapshot() ?: return@launch
            if (!activeSession.isActive) return@launch

            val rootNode = try { rootInActiveWindow } catch (_: Exception) { null }

            // The persisted Guard row, read once per event and
            // honoured by both interceptors below (the browser
            // step read it alone before Truth Car 3; the sniper
            // read nothing at all — UNA-01).
            val guardSettings = dao.getGuardSettingsSnapshot()

            // 2. Settings & Package Installer Blocker
            if (isPackageInstallerOrTamperSettings(pkgName, rootNode)) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                launchInterceptScreen(
                    packageName = pkgName,
                    appName = "System Settings Guard",
                    reasonBadge = "Uninstall Guard",
                    path = InterceptPath.UNINSTALL_GUARD
                )
                return@launch
            }

            // UNA2-01, the lease agreement — AMENDED by #131 for
            // the sniper only: a live peek lease on THIS package
            // still silences the site blocker below (a browser peek
            // must not be re-intercepted by the door it was bought
            // through), but it no longer silences the sniper. The
            // old blanket rule turned the one sanctioned YouTube
            // entry — its factory tier is PEEK_2M — into a 2-minute
            // Shorts pass, and made the PARK sniper toggle a lived
            // no-op in the default configuration. A lease is an
            // app-access door: it buys a time-boxed, counted glance
            // at the app (branch 5 still honours it exactly as
            // before); it was never a feed licence. Inside a peeked
            // YouTube/Instagram the user may use the app — and the
            // moment a Short/Reel actually plays, the sniper fires.
            val leaseRow = dao.getAllAppsSnapshot().find { it.packageName == pkgName }
            val leaseLive = leaseRow != null &&
                leaseRow.resolvedTier == AppBlockTier.PEEK_2M &&
                SystemClock.elapsedRealtime() < leaseRow.activePeekUntilElapsedRealtime

            // 3. YouTube Shorts & Instagram Reels Sniper (Strictly active during both Work Blocks and Safe Breaks, during peek leases (#131) — and only while its persisted switch is ON: UNA-01)
            if (shortsSniperArmed(guardSettings, pkgName)) {
                if (detectShortsOrReelsFeed(rootNode)) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    launchInterceptScreen(
                        packageName = pkgName,
                        appName = if (pkgName.contains("youtube")) "YouTube Shorts" else "Instagram Reels",
                        reasonBadge = if (pkgName.contains("youtube")) "Shorts Blocked" else "Reels Blocked",
                        path = InterceptPath.SNIPER
                    )
                    return@launch
                }
            }

            // 4. In-Browser URL Bar Blocker (Strictly active during both Work Blocks and Safe Breaks; ignores focused typing)
            // Enforces the USER'S persisted site list + persisted Guard toggle (Ren).
            // UNA-06 (Truth Car 3): the stored list IS the enforced list.
            // There is NO factory fallback — an emptied list blocks no
            // websites, exactly as Shield's "0 SITES" print says. (The
            // factory defaults live in the seeded stored list itself,
            // his to edit or empty; they are never smuggled back in at
            // enforcement time.) The settings row is the hoisted read
            // above (Falk's A-4 placement, composed at merge): one
            // read per event, honoured by both interceptors.
            if (!leaseLive && (guardSettings?.browserBlockerEnabled ?: true) && isSupportedBrowser(pkgName) && rootNode != null) {
                val userDomains = dao.getAllSitesSnapshot().map { it.domain }
                val matchedDomain = detectBlockedBrowserUrl(
                    rootNode,
                    userDomains
                )
                if (matchedDomain != null) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    launchInterceptScreen(
                        packageName = pkgName,
                        appName = matchedDomain,
                        reasonBadge = "Site Blocked",
                        path = InterceptPath.SITE
                    )
                    return@launch
                }
            }

            // 5. 3-Tier Per-App & Safe Break Blocking
            val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkgName } ?: return@launch
            val tier = appRule.resolvedTier
            if (tier == AppBlockTier.OPEN) return@launch

            // Safe Break Rule: 2m Peek apps unlock freely during the 10-minute Safe Break, while Locked apps stay locked
            if (activeSession.isInSafeBreak && tier == AppBlockTier.PEEK_2M) {
                return@launch
            }

            // Active 2-Minute Peek Lease Rule: allow access while monotonic clock is within the 2m lease window
            val nowMono = SystemClock.elapsedRealtime()
            if (tier == AppBlockTier.PEEK_2M && nowMono < appRule.activePeekUntilElapsedRealtime) {
                return@launch
            }

            launchInterceptScreen(
                packageName = appRule.packageName,
                appName = appRule.appName,
                reasonBadge = tier.badge,
                path = InterceptPath.APP_TIER
            )
        }
    }

    private fun isCallActive(): Boolean {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            val mode = am.mode
            mode == AudioManager.MODE_RINGTONE ||
                mode == AudioManager.MODE_IN_CALL ||
                mode == AudioManager.MODE_IN_COMMUNICATION
        } catch (_: Exception) {
            false
        }
    }

    private fun isPackageInstallerOrTamperSettings(pkgName: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val installerPackages = setOf(
            "com.google.android.packageinstaller",
            "com.android.packageinstaller",
            "com.samsung.android.packageinstaller",
            "com.miui.packageinstaller"
        )
        if (installerPackages.contains(pkgName)) return true

        if (pkgName == "com.android.settings" && rootNode != null) {
            val dump = collectWindowTextLowercase(rootNode)
            val mentionsMonolith = dump.contains("monolith")
            val mentionsTamperAction = dump.contains("force stop") ||
                dump.contains("deactivate") ||
                dump.contains("uninstall") ||
                dump.contains("clear storage") ||
                dump.contains("clear data") ||
                dump.contains("device admin")
            return mentionsMonolith && mentionsTamperAction
        }
        return false
    }

    private fun detectShortsOrReelsFeed(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
        // Strategy A (#131, tightened): known player/viewer View
        // IDs — and only when a node is actually VISIBLE to the
        // user. Mere presence anywhere in the tree is proximity,
        // not watching: the Shorts shelf ID (reel_recycler) is
        // excluded entirely (see SHORTS_PLAYER_VIEW_IDS), and a
        // player surface instantiated off-screen no longer fires.
        for (id in SHORTS_PLAYER_VIEW_IDS) {
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(id) } catch (_: Exception) { null }
            if (nodes?.any { it.isVisibleToUser } == true) return true
        }
        // Strategy B (#131, tightened): a selected Shorts/Reels
        // tab counts only in the bottom-navigation region — the
        // main apps open their feed the moment that tab is
        // selected, so there selection IS watching. A selected
        // 'Shorts' channel tab or search chip sits far above the
        // nav bar and is mere browsing; it must not fire.
        val rootRect = Rect()
        try { rootNode.getBoundsInScreen(rootRect) } catch (_: Exception) { /* no bounds: B cannot prove its case */ }
        return isSelectedBottomTabShortsOrReels(rootNode, rootRect.height())
    }

    private fun isSelectedBottomTabShortsOrReels(node: AccessibilityNodeInfo?, rootHeightPx: Int): Boolean {
        if (node == null) return false
        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
        if (node.isSelected && isFeedTabLabel(text, desc)) {
            val rect = Rect()
            val hasBounds = try { node.getBoundsInScreen(rect); true } catch (_: Exception) { false }
            if (hasBounds && isBottomNavBounds(rect.bottom, rootHeightPx)) return true
        }
        for (i in 0 until node.childCount) {
            if (isSelectedBottomTabShortsOrReels(node.getChild(i), rootHeightPx)) return true
        }
        return false
    }

    private fun isSupportedBrowser(pkgName: String): Boolean {
        return pkgName in setOf(
            "com.android.chrome",
            "com.brave.browser",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.sec.android.app.sbrowser"
        )
    }

    /**
     * Reads the browser URL bar across Chrome, Brave, Firefox, Edge, and Samsung Internet.
     * Ignores the URL bar when `node.isFocused == true` so it never falsely triggers while the user is typing.
     */
    private fun detectBlockedBrowserUrl(rootNode: AccessibilityNodeInfo, domains: List<String>): String? {
        val urlBarViewIds = listOf(
            "com.android.chrome:id/url_bar",
            "com.brave.browser:id/url_bar",
            "org.mozilla.firefox:id/mozac_browser_toolbar_url_view",
            "com.microsoft.emmx:id/url_bar",
            "com.sec.android.app.sbrowser:id/location_bar_edit_text"
        )
        for (viewId in urlBarViewIds) {
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(viewId) } catch (_: Exception) { null }
            if (!nodes.isNullOrEmpty()) {
                for (node in nodes) {
                    // Do NOT trigger while the address bar is focused and the user is actively typing
                    if (node.isFocused) continue
                    val urlText = node.text?.toString()?.lowercase() ?: continue
                    val matched = matchBlockedDomain(urlText, domains)
                    if (matched != null) return matched
                }
            }
        }
        return null
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
