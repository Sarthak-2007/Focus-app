package com.example.service

import android.util.Log
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioManager
import android.os.SystemClock
import android.telecom.TelecomManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.example.MainActivity
import com.example.data.AppBlockTier
import com.example.data.BlockedBrowserDomains
import com.example.data.GuardSettingsEntity
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.Locale

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
 *    A peek lease does NOT silence it (#131): the lease buys a time-boxed glance at the app, never a
 *    Shorts/Reels binge — the sniper is the feed-specific door, the lease is an app-access door.
 *    Scope, stated honestly (#131): the sniper polices ONLY the vertical Shorts/Reels feed. It does
 *    not police long-form video, home feeds, Stories or DMs — those surfaces are governed solely by
 *    the app's tier below (an OPEN app is open there because the user tiered it OPEN).
 * 4. In-Browser URL Bar Blocker: Ignores the address bar while the user is actively typing (`node.isFocused == true`),
 *    and blocks distracting domains once committed/loaded—even during Safe Breaks.
 * 6. Least-privilege exercise + honest degradation (Wave-3 #42,
 *    Scout-2 Finds 11/18): the config grant stays content-capable
 *    because the sniper, site and tamper doors genuinely need node
 *    content — splitting it away would trade HOLD strength, which
 *    is never traded. What is least-privilege is the EXERCISE: no
 *    content is read with no session armed, and root traversal
 *    happens only for packages whose door needs node evidence
 *    (captureEventFacts). isAccessibilityTool is pinned false in
 *    the config — Monolith is not an accessibility tool, and under
 *    Android 17 Advanced Protection this service may be revoked;
 *    blocking then honestly degrades to the UsageStats/shield
 *    layer instead of claiming a status it does not hold.
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
 *
 * Issue #79 (Audit R2, url-blocker-bypasses #1/#2/#4): the matcher
 * no longer does a raw substring contains() over the URL-bar text.
 * Both sides are parsed and canonicalised:
 *  - the visited URL's host is extracted (userinfo/port/trailing
 *    dot stripped, subdomains of a blocked host match, lookalike
 *    hosts and query-string mentions do NOT — the substring matcher
 *    blocked both, and missed everything below);
 *  - stored rules are canonicalised through
 *    [BlockedBrowserDomains.normalizeDomain] at match time, so
 *    rules stored dead by the old normaliser ("instagram.com.",
 *    "instagram.com:443") start matching with no data migration;
 *  - known proxy wrappers are unwrapped to their origin host:
 *    Google Translate's `<origin-with-hyphens>.translate.goog`
 *    serving form and `translate.google.com/translate?u=<target>`;
 *  - the Shorts rule ("youtube.com/shorts") matches the same Short
 *    in its other URL spellings — youtube.com/watch?v= and
 *    youtu.be/<id> — without widening into all of YouTube: the
 *    home/channel pages stay open under it.
 * Returns the matched rule's canonical host — the identity the
 * intercept screen prints.
 */
internal fun matchBlockedDomain(urlText: String, domains: List<String>): String? {
    if (domains.isEmpty()) return null
    val candidates = urlCandidates(urlText)
    if (candidates.isEmpty()) return null
    for (rawRule in domains) {
        val rule = parseSiteRule(rawRule) ?: continue
        if (candidates.any { ruleMatches(rule, it) }) return rule.host
    }
    return null
}

/**
 * Issue #79, finding 3 (detectable residue): an embedded player's
 * URL, where the accessibility tree exposes it as page text, names
 * the blocked host inside an `/embed` URL token
 * ("youtube.com/embed/…", "tiktok.com/embed/v2/…",
 * "instagram.com/p/…/embed/"). A plain textual mention of a blocked
 * domain is NOT an embed and never matches — allowed pages that
 * talk about a blocked service stay open. Path rules (e.g.
 * youtube.com/shorts) do not fire here: an embed URL cannot prove
 * the embedded video is a Short, so that part of the finding is
 * honestly undetectable from the tree and stays documented as
 * residue in the issue report, not papered over.
 */
internal fun matchEmbeddedBlockedDomain(contentText: String, domains: List<String>): String? {
    if (domains.isEmpty()) return null
    val tokens = contentText.lowercase(Locale.ROOT).split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return null
    for (rawRule in domains) {
        val rule = parseSiteRule(rawRule) ?: continue
        if (rule.path != null) continue
        // STURM3-02 hardening (adversarial pass on the WELLE fix): the
        // token's host is PARSED and compared exactly / as a subdomain,
        // never substring-matched — the original token.contains(host)
        // fired on lookalike hosts ("notinstagram.com/embed/…") and on
        // attacker parents ("instagram.com.evil.com/embed/…"), the same
        // false-block class finding 1's matcher rewrite eliminated.
        val hit = tokens.any { token ->
            val candidate = parseUrlCandidate(token) ?: return@any false
            val hostMatches = candidate.host == rule.host ||
                candidate.host.endsWith("." + rule.host)
            hostMatches && candidate.path.contains("/embed")
        }
        if (hit) return rule.host
    }
    return null
}

/** A canonicalised stored rule: host plus optional path prefix. */
private data class SiteRule(val host: String, val path: String?)

private fun parseSiteRule(raw: String): SiteRule? {
    val canonical = BlockedBrowserDomains.normalizeDomain(raw) ?: return null
    val host = canonical.substringBefore('/').removePrefix("*.")
    val path = if (canonical.contains('/')) "/" + canonical.substringAfter('/') else null
    return SiteRule(host, path)
}

/** One parsed candidate URL the visited page may actually be. */
private data class UrlCandidate(
    val host: String,
    val path: String,
    val query: Map<String, String>,
    /** True when the URL was spelled as a youtu.be short link. */
    val isShortLink: Boolean
)

private fun parseUrlCandidate(text: String): UrlCandidate? {
    var d = text.trim().lowercase(Locale.ROOT)
    if (d.isEmpty()) return null
    d = d.substringBefore('#')
    val queryString = d.substringAfter('?', "")
    d = d.substringBefore('?')
    d = d.removePrefix("https://").removePrefix("http://").removePrefix("//")
    if (d.isEmpty()) return null
    val slash = d.indexOf('/')
    val authority = if (slash >= 0) d.substring(0, slash) else d
    val path = if (slash >= 0) d.substring(slash) else ""
    var host = authority.substringAfterLast('@').substringBefore(':')
    host = host.trim().trimEnd('.').removePrefix("www.").trimEnd('.')
    if (host.isEmpty() || !host.contains('.')) return null
    val query = mutableMapOf<String, String>()
    if (queryString.isNotEmpty()) {
        for (pair in queryString.split('&')) {
            if (pair.isEmpty()) continue
            val key = pair.substringBefore('=').trim()
            val value = try {
                java.net.URLDecoder.decode(pair.substringAfter('=', ""), "UTF-8")
            } catch (_: Exception) {
                pair.substringAfter('=', "")
            }
            if (key.isNotEmpty()) query[key] = value
        }
    }
    return UrlCandidate(
        host = host,
        path = path,
        query = query,
        isShortLink = host == "youtu.be" || host.endsWith(".youtu.be")
    )
}

/**
 * The candidate set for one URL-bar text: the URL as visited, plus
 * its unwrapped origin when a known proxy wrapper serves it.
 */
private fun urlCandidates(urlText: String): List<UrlCandidate> {
    val base = parseUrlCandidate(urlText) ?: return emptyList()
    val out = mutableListOf(base)
    // Google Translate proxy, host-suffix form: the entire blocked
    // site is served under <origin>.translate.goog with the origin
    // host's dots rewritten to hyphens (and literal hyphens doubled).
    if (base.host.endsWith(".translate.goog")) {
        val encoded = base.host.removeSuffix(".translate.goog")
        val originHost = encoded
            .replace("--", "\u0001")
            .replace('-', '.')
            .replace("\u0001", "-")
            .removePrefix("www.")
        if (originHost.contains('.')) {
            out.add(base.copy(host = originHost, isShortLink = false))
        }
    }
    // Google Translate proxy, query form: translate.google.com/
    // translate?u=<target URL> (also the googleusercontent host).
    if (base.host == "translate.google.com" ||
        base.host == "translate.googleusercontent.com"
    ) {
        val target = base.query["u"]
        if (!target.isNullOrEmpty()) {
            parseUrlCandidate(target)?.let { out.add(it) }
        }
    }
    return out
}

private fun ruleMatches(rule: SiteRule, candidate: UrlCandidate): Boolean {
    // youtu.be/<id> IS youtube.com/watch?v=<id> in another spelling.
    val effectiveHost = if (candidate.isShortLink) "youtube.com" else candidate.host
    val hostMatches = effectiveHost == rule.host || effectiveHost.endsWith("." + rule.host)
    if (!hostMatches) return false
    val rulePath = rule.path ?: return true
    if (candidate.path == rulePath || candidate.path.startsWith("$rulePath/")) return true
    // Shorts, alternate spellings (finding 2): a Short opened as a
    // watch page or a short link is the same class as /shorts/<id>.
    // Deliberately NOT wider: the YouTube home page, channel pages
    // and search stay open under the Shorts rule.
    if (rule.host == "youtube.com" && rulePath == "/shorts") {
        if (candidate.isShortLink) return true
        if (candidate.path == "/watch" && candidate.query.containsKey("v")) return true
    }
    return false
}

/**
 * Merge resolution (STURM3-02, #79 onto Wave-3 #42 main): main's
 * own host-exact matchBlockedDomain is SUPERSEDED by the #79
 * matcher above — same host-exact semantics (lookalikes, query
 * mentions and evil parents never match), plus path rules,
 * proxy unwrapping and Shorts spellings. Main's extractUrlHost
 * is kept verbatim below: Wave3Issue42AccessibilityTest imports
 * it and other callers use it. One compatibility fix in the #79
 * matcher: parseSiteRule strips a leading "*." from a stored
 * rule's host, so wildcard entries ("*.youtube.com") match and
 * report the bare domain, exactly as #42's matcher did.
 */

/** The host of a URL-bar text, or null when the text carries no
 *  plausible host. Tolerates a missing scheme (users see bare
 *  hosts in some bars), userinfo, ports, paths, queries, fragments. */
internal fun extractUrlHost(urlText: String): String? {
    var rest = urlText.trim().lowercase(Locale.ROOT)
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
 * Call exemption (Wave-3 #42, Verifier-hardened): the ONLY packages
 * a live call may exempt. Before the first cure, `isCallActive()`
 * was a global kill-switch — AudioManager in a call mode suspended
 * EVERY block for EVERY app, so an incoming call opened a hole the
 * size of the whole session. The first cure's name-matching was
 * then broken by the C5 Verifier: a VoIP call's surface is the
 * app's OWN package (WhatsApp, Telegram, Signal name themselves
 * nothing like a dialer), so name rules alone would have blocked
 * the very calls the feature promises to protect.
 *
 * The exemption therefore has three independent legs, and a live
 * call exempts a package only if one of them names it:
 *  1. SURFACE — the system dialer / in-call UI packages (set +
 *     dialer/incallui name rules): the cellular call UI.
 *  2. OWNER — the package that was in the foreground when this
 *     call became active, tracked from window-state events. For a
 *     VoIP call that is the VoIP app itself, whatever its name.
 *  3. DEFAULT DIALER — the package holding Telecom's default
 *     dialer role (covers replacement dialers like Truecaller
 *     that no name rule can enumerate).
 * A blocked app that is none of the three stays blocked mid-call;
 * a call app that IS blocked is exempt on its own surface only —
 * the shipped promise — and its call unblocks nothing else.
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

/** Pure decision: does a live call exempt THIS event's package?
 *  [callOwnerPackage] is the foreground package captured when the
 *  current call became active (null when unknown); [defaultDialerPackage]
 *  is Telecom's current default dialer (null when unread). */
internal fun shouldBypassForCall(
    audioMode: Int,
    packageName: String,
    callOwnerPackage: String? = null,
    defaultDialerPackage: String? = null
): Boolean {
    val callMode = audioMode == AudioManager.MODE_RINGTONE ||
        audioMode == AudioManager.MODE_IN_CALL ||
        audioMode == AudioManager.MODE_IN_COMMUNICATION
    if (!callMode) return false
    return isCallSurfacePackage(packageName) ||
        packageName == callOwnerPackage ||
        packageName == defaultDialerPackage
}

/** Installer surfaces that can force-stop / uninstall Monolith
 *  (Wave-3 #42): the system package installers across OEMs. An
 *  installer window only exists during an install/uninstall flow —
 *  but R3b #162 F1 narrowed the trigger: presence alone fired on
 *  ANY app's flow, so the guard now also requires the window to
 *  name Monolith (see isPackageInstallerOrTamperSettings). */
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
    val id = viewIdResourceName?.lowercase(Locale.ROOT) ?: return false
    return id.endsWith(":id/uninstall_button") ||
        id.endsWith(":id/force_stop_button") ||
        id.endsWith(":id/disable_button") ||
        id.endsWith(":id/clear_data_button") ||
        id.endsWith(":id/clear_storage_button") ||
        id.endsWith(":id/button_uninstall") ||
        id.endsWith(":id/button_force_stop") ||
        id.endsWith(":id/button_disable")
}

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

        /** Truth (issue #76): detection queries that THREW, counted
         *  and logged instead of silently masquerading as "evidence
         *  absent". The fail directions themselves are unchanged
         *  (enforcement fails open, the call bypass fails closed) —
         *  what changed is that the failure is no longer invisible:
         *  a rising count on a healthy phone is itself the signal. */
        @Volatile
        var detectionFailures: Int = 0
            internal set

        internal fun noteDetectionFailure(where: String, e: Exception) {
            detectionFailures++
            Log.w("MonolithA11y", "detection query failed at $where (total $detectionFailures)", e)
        }

        /** Monotonic stamp of the last event whose root window was
         *  successfully acquired. Pipeline proof (the service is
         *  bound and processing this foreground), not session proof. */
        @Volatile
        var lastRootMono: Long = 0L
            internal set

        /** Binding generation (issue #7, TECH-1 B-Major-1): bumped
         *  when a binding begins (onServiceConnected) and every time
         *  one ends (onUnbind/onDestroy). A pipeline probe captures
         *  the generation before its root query and re-checks it at
         *  stamp time: the query can block for up to the platform's
         *  interaction timeout, the binding can die while it is in
         *  flight, and unbind zeroes [lastRootMono] precisely so a
         *  rebound service starts unproven — an in-flight probe from
         *  the dead binding must never stamp after that zeroing and
         *  resurrect proof its successor never earned. */
        @Volatile
        var bindingGeneration: Long = 0L
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

        /** Foreground witness (issue #43, finding 2): the package
         *  of the most recent TYPE_WINDOW_STATE_CHANGED event —
         *  the package string ONLY, never window content — and the
         *  monotonic instant it was stamped. This is real
         *  foreground detection: the shield prefers this witness
         *  over the UsageStats lastTimeUsed guess whenever this
         *  service is bound. Null / 0 when no window event has
         *  been seen under this binding; the stamp dies with the
         *  binding (onUnbind / onDestroy), like lastRootMono. */
        @Volatile
        var foregroundPackage: String? = null
            internal set

        @Volatile
        var foregroundMono: Long = 0L
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
        // A new binding is a new generation: probes still in flight
        // from any previous binding fail their stamp-time re-check.
        bindingGeneration++
        instance = this
        // Cold-start cure (issue #7): a rebind mid-residency
        // re-proves itself. The stamp died with the old binding
        // (onUnbind zeroes lastRootMono), so a residency that is
        // already in force would rest UNPROVEN until some event
        // happened to fire — probe actively instead of waiting.
        if (WatchStore.residencyActive()) {
            requestPipelineProbe()
        }
        // Watch Store bind (issue #48): restore the persisted LAST
        // PROVED into the fresh process's store.
        WatchStore.bind(applicationContext)
        // Watch Store BIND writer (chrome C3): the binding itself is
        // the measurement; the store stamps it.
        WatchStore.onAccessibilityBound(true)
        startSessionArmedCollector()
        startEventWorker()
    }

    /**
     * Active self-probe (issue #7, cold-start cure): the SAME proof
     * the event path performs in [onAccessibilityEvent], done on
     * demand instead of waiting for an event. The stamp races
     * residency start on a cold open — the Deck can appear before
     * this service binds, and a quiet Deck generates no events — so
     * residency start (and a rebind mid-residency) probe the
     * pipeline actively. A live root stamps [lastRootMono] and pokes
     * the store, exactly as an event would; a null root or a thrown
     * query stamps NOTHING and returns false — the wire reads dead,
     * which is the truth. The stamp is also generation-guarded: a
     * probe whose binding died while its query was in flight stamps
     * nothing, so a dead binding can never mint its successor's
     * proof (TECH-1 B-Major-1).
     */
    fun probePipelineNow(): Boolean {
        // Capture the binding this probe serves BEFORE the query:
        // the query can block for up to the platform interaction
        // timeout, and the binding can die while it is in flight
        // (TECH-1 B-Major-1).
        val generationAtLaunch = bindingGeneration
        return try {
            if (rootInActiveWindow != null) {
                // Stamp only if the binding that launched this probe
                // is still the live one: same generation (no bind,
                // unbind or destroy since launch) and this instance
                // still the registered service. A probe that outlived
                // its binding proves nothing for its successor.
                if (bindingGeneration != generationAtLaunch || instance !== this) {
                    false
                } else {
                    lastRootMono = SystemClock.elapsedRealtime()
                    WatchStore.poke()
                    true
                }
            } else {
                false
            }
        } catch (e: Exception) {
            // No stamp — and (issue #76) the failure is counted, not silent.
            noteDetectionFailure("pipelineProbe", e)
            false
        }
    }

    /** Fire-and-forget probe request: runs [probePipelineNow] on
     *  this service's existing scope, so a caller on any thread —
     *  including the UI thread at residency start — never blocks
     *  waiting for the root window. */
    fun requestPipelineProbe() {
        scope.launch {
            probePipelineNow()
        }
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
        val browserUrlTexts: List<String> = emptyList(),
        /** Issue #79 finding 3: the browser window's text, captured
         *  for the embed scan at match time in the worker. Empty
         *  while a URL bar is focused (never fire mid-typing). */
        val browserContentText: String = ""
    )

    private data class EventSnapshot(
        val packageName: String,
        val facts: EventFacts,
        /** The session this event was captured under (the armed
         *  witness at capture time). The worker drops a snapshot
         *  whose session is no longer the active one — an event
         *  from a dead session must never fire inside its
         *  successor (Verifier, Wave-3 #42). */
        val sessionId: Int = -1
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
                        val wasDisarmed = armedSessionId == -1
                        // Issue #77: stamp the mono BEFORE publishing
                        // the id. The old order let a sampler pair the
                        // NEW session's id with the PREVIOUS session's
                        // still-standing mono — link L3 'confirmed' for
                        // a session this gate never observed. With
                        // mono-first, a mid-write sampler sees either
                        // the old id with a fresh mono (harmless: the
                        // old session is gone) or the new id with its
                        // own stamp — never the torn pair.
                        armedMono = SystemClock.elapsedRealtime()
                        armedSessionId = session.id
                        if (wasDisarmed) {
                            // Catch-up scan (Verifier, Wave-3
                            // #42): events dropped while the gate
                            // was shut are gone, and a static
                            // window (Settings, a paused feed)
                            // may never re-fire — so the instant
                            // the gate opens, judge the window
                            // that is already on screen.
                            enqueueCurrentWindowSnapshot()
                        }
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
            // End this binding's generation FIRST: any probe still
            // in flight under it fails its stamp-time re-check and
            // cannot resurrect the stamp zeroed below (B-Major-1).
            bindingGeneration++
            instance = null
            // The stamp dies with the binding (Register 3 Z-1): a
            // rebound service must never inherit its predecessor's
            // proof of life.
            lastRootMono = 0L
            armedSessionId = -1
            armedMono = 0L
            foregroundPackage = null
            foregroundMono = 0L
            WatchStore.onAccessibilityBound(false)
        }
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Foreground witness (issue #43, finding 2), stamped BEFORE
        // any early return below — including the own-package and
        // systemui returns, so the witness always names the app
        // actually in front (a stale witness naming the previous
        // app would let the shield intercept an app the user has
        // already left). Package string only; no window content
        // is read or stored for this witness.
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val fgPkg = event.packageName?.toString()?.takeIf { it.isNotBlank() }
            if (fgPkg != null) {
                foregroundPackage = fgPkg
                foregroundMono = SystemClock.elapsedRealtime()
            }
        }
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
        } catch (e: Exception) {
            // No stamp — and (issue #76) the failure is counted, not silent.
            noteDetectionFailure("rootStamp", e)
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

        // Call tracking + bypass — PER CALL (Wave-3 #42): refresh
        // the foreground tracker and the call owner from this
        // event first, then ask the pure decision. A live call
        // exempts its own surface only; it no longer suspends
        // blocking for every other app on the phone.
        refreshCallTracking(pkgName, event.eventType)
        if (isCallActiveForPackage(pkgName)) return

        // Synchronous fact capture: every node-derived verdict is
        // taken HERE, while rootInActiveWindow still describes
        // this event's window — never after an async hop.
        val facts = captureEventFacts(pkgName)
        val snapshot = EventSnapshot(pkgName, facts, armedSessionId)
        if (facts.tamperSurface) {
            // The tamper door does NOT ride the conflated channel
            // (Verifier, Wave-3 #42): conflation is per-channel,
            // not per-window, so a later harmless event could
            // silently evict a tamper snapshot for a STATIC
            // Settings/installer page that will never re-fire.
            // Tamper events are rare by nature; they take the
            // direct path and are processed in arrival order.
            scope.launch {
                try {
                    processEvent(snapshot)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Same law as the worker: one bad event must
                    // not kill enforcement.
                }
            }
        } else {
            eventChannel.trySend(snapshot)
        }

        // Interactive-window sweep (#92): the checks above police
        // only the ACTIVE window. A blocked app in the non-active
        // split-screen pane, or a feed playing in a picture-in-
        // picture window, was never scanned at all — the config
        // now grants flagRetrieveInteractiveWindows, so every
        // other application window is judged too, through the
        // same snapshot pipeline and the same doors.
        sweepOtherWindows(pkgName)
    }

    /** #92: judge every OTHER application window on this event.
     *  Facts are captured synchronously from each window's own
     *  root, with captureEventFacts' needs-root discipline, and
     *  dispatched exactly like the active window's snapshot —
     *  tamper direct (never evicted by conflation), the rest via
     *  the conflated channel. processEvent applies the same
     *  session binding, call exemption, lease rules (#131: the
     *  sniper is not lease-silenced; the site door is) and tier
     *  door to a swept package as to the foreground one. */
    private fun sweepOtherWindows(eventPkg: String) {
        for (winRoot in interactiveWindowRoots()) {
            val winPkg = winRoot.packageName?.toString() ?: continue
            if (winPkg == eventPkg) continue
            if (winPkg == applicationContext.packageName ||
                winPkg.startsWith("com.android.systemui")
            ) {
                continue
            }
            val winFacts = captureEventFacts(winPkg, winRoot)
            val winSnapshot = EventSnapshot(winPkg, winFacts, armedSessionId)
            if (winFacts.tamperSurface) {
                scope.launch {
                    try {
                        processEvent(winSnapshot)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        // Same law as the worker: one bad event
                        // must not kill enforcement.
                    }
                }
            } else {
                eventChannel.trySend(winSnapshot)
            }
        }
    }

    /**
     * Roots of every TYPE_APPLICATION window the system exposes
     * to this service — requires flagRetrieveInteractiveWindows
     * in the service config (#92). Split-screen panes and
     * picture-in-picture windows are application windows, so
     * they arrive here even when they are not the active window.
     * Any failure reads as an empty list: the sweep simply does
     * not run, never a crash on the event path.
     */
    private fun interactiveWindowRoots(): List<AccessibilityNodeInfo> {
        return try {
            windows?.mapNotNull { window ->
                if (window.type != AccessibilityWindowInfo.TYPE_APPLICATION) null
                else try { window.root } catch (_: Exception) { null }
            } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    // ------------------------------------------------------------------
    // Call tracking (Wave-3 #42): the foreground package is tracked
    // from window-state events (package name only — no content), so
    // the moment a call becomes active the service can name its
    // OWNER: the app that was in front when the call began. The
    // owner locks on first observation and clears when the call
    // ends; it never follows the user into other apps mid-call.
    // ------------------------------------------------------------------

    @Volatile
    private var lastForegroundPackage: String? = null

    @Volatile
    private var callOwnerPackage: String? = null

    private fun refreshCallTracking(pkgName: String, eventType: Int) {
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            lastForegroundPackage = pkgName
        }
        val mode = try {
            (getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.mode
        } catch (_: Exception) {
            null
        } ?: return
        val callMode = mode == AudioManager.MODE_RINGTONE ||
            mode == AudioManager.MODE_IN_CALL ||
            mode == AudioManager.MODE_IN_COMMUNICATION
        if (callMode) {
            if (callOwnerPackage == null) {
                callOwnerPackage = if (
                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                ) {
                    pkgName
                } else {
                    lastForegroundPackage
                }
            }
        } else if (callOwnerPackage != null) {
            callOwnerPackage = null
        }
    }

    /** Reads the root window ONCE for the packages whose doors
     *  need node evidence (installer/settings surfaces, the two
     *  sniper apps, supported browsers) and distils it into plain
     *  facts. All other packages need no content at all — the tier
     *  door works from the package name alone. */
    private fun captureEventFacts(pkgName: String): EventFacts {
        val rootNode = try { rootInActiveWindow } catch (e: Exception) { noteDetectionFailure("rootInActiveWindow", e); null }
            ?: return EventFacts()
        return captureEventFacts(pkgName, rootNode)
    }

    /** The fact capture itself, against a GIVEN window's root —
     *  the active window for the event's own snapshot, another
     *  application window for the #92 sweep's snapshots. The same
     *  needs-root discipline applies either way: packages whose
     *  doors work from the name alone never have content read. */
    private fun captureEventFacts(pkgName: String, rootNode: AccessibilityNodeInfo): EventFacts {
        val needsRoot = pkgName in INSTALLER_PACKAGES ||
            pkgName in MARKET_PACKAGES ||
            pkgName in SETTINGS_PACKAGES ||
            pkgName == "com.google.android.youtube" ||
            pkgName == "com.instagram.android" ||
            isSupportedBrowser(pkgName)
        if (!needsRoot) return EventFacts()
        return EventFacts(
            tamperSurface = isPackageInstallerOrTamperSettings(pkgName, rootNode),
            sniperFeedVisible = detectShortsOrReelsFeed(rootNode),
            browserUrlTexts = if (isSupportedBrowser(pkgName)) {
                extractBrowserUrlTexts(rootNode)
            } else {
                emptyList()
            },
            browserContentText = if (isSupportedBrowser(pkgName) && !hasFocusedUrlBar(rootNode)) {
                collectWindowTextLowercase(rootNode)
            } else {
                ""
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
        // Session binding: a snapshot captured under a dead session
        // never fires inside its successor.
        if (snapshot.sessionId != -1 && snapshot.sessionId != activeSession.id) return
        // Call re-check at processing time: a call that began after
        // this event was captured (owner not yet locked at capture)
        // still exempts its own surface — the intercept must never
        // win the race against the ring.
        if (isCallActiveForPackage(pkgName)) return

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

        // UNA2-01, the lease agreement — AMENDED by #131 for
        // the sniper only (ported into this pipeline at the
        // #42 merge): a live peek lease on THIS package still
        // silences the site blocker below (a browser peek must
        // not be re-intercepted by the door it was bought
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
        val appsSnapshot = dao.getAllAppsSnapshot()
        val leaseRow = appsSnapshot.find { it.packageName == pkgName }
        val leaseLive = leaseRow != null &&
            leaseRow.resolvedTier == AppBlockTier.PEEK_2M &&
            SystemClock.elapsedRealtime() < leaseRow.activePeekUntilElapsedRealtime

        // 3. YouTube Shorts & Instagram Reels Sniper (Strictly active during both Work Blocks and Safe Breaks, during peek leases (#131) — and only while its persisted switch is ON: UNA-01)
        if (shortsSniperArmed(guardSettings, pkgName) && snapshot.facts.sniperFeedVisible) {
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
            } ?: snapshot.facts.browserContentText.takeIf { it.isNotEmpty() }?.let {
                // Issue #79 finding 3 (detectable part): the URL bar
                // is clean, but a blocked service's embed may be
                // playing inside this allowed page — where the tree
                // exposes the embed's address as page text.
                matchEmbeddedBlockedDomain(it, userDomains)
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
        // Issue #140: the daily limit binds every tier. An OPEN
        // app whose today's usage reached its limit is
        // intercepted like a blocked one; the peek exemptions
        // below still protect a lawful peek already running.
        val overLimit = appRule.dailyLimitMinutes > 0 &&
            appRule.todayUsageMinutes >= appRule.dailyLimitMinutes
        if (tier == AppBlockTier.OPEN && !overLimit) return
        // Issue #332 F5: the safety floor holds in this door too —
        // a floor package (dialer / telecom / keyboard / emergency)
        // is never intercepted, whatever tier a legacy row holds.
        // (Writer-side, EssentialApps.floorTier already stops new
        // LOCKED dialler rows; the door floor covers every class,
        // including the IME and emergency surfaces writers miss.)
        if (com.example.data.SafetyFloor.isFloorPackage(pkgName)) return

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
            reasonBadge = if (overLimit && tier == AppBlockTier.OPEN) "Daily Limit" else tier.badge,
            path = InterceptPath.APP_TIER
        )
    }

    private fun isCallActiveForPackage(pkgName: String): Boolean {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            val defaultDialer = try {
                (getSystemService(Context.TELECOM_SERVICE) as? TelecomManager)
                    ?.defaultDialerPackage
            } catch (_: Exception) {
                null
            }
            shouldBypassForCall(am.mode, pkgName, callOwnerPackage, defaultDialer)
        } catch (e: Exception) {
            // Issue #76: a thrown AudioManager query is not "no call"
            // — the bypass is still lost (fail-closed stands), but
            // the loss is counted and logged, never silent.
            noteDetectionFailure("isCallActive", e)
            false
        }
    }

    /**
     * R3b #162 F3: words that appear ONLY inside a destructive
     * CONFIRM dialog — never on the static App Info page. The old
     * Settings test used the loose words printed permanently on
     * that page (uninstall / force stop / deactivate / clear data /
     * device admin), so merely OPENING App Info fired the guard:
     * viewing was indistinguishable from tampering. These phrases
     * are the dialogs' own sentences — the uninstall confirm asks
     * its question, the force-stop confirm warns the app "may
     * misbehave", the clear-data confirm promises data will "be
     * deleted", the admin screen names the act itself.
     */
    private val destructiveDialogPhrases = listOf(
        "do you want to uninstall",
        "may misbehave",
        "be deleted",
        "deactivate this device admin"
    )

    /** R3b #162 F3, locale-independent leg: a confirm dialog is an
     *  AlertDialog, and AlertDialogs carry framework button IDs
     *  (android:id/button1 positive, button2 negative, button3
     *  neutral) that do not translate and never appear on the
     *  static App Info page. Dialog presence + the Monolith anchor
     *  is dialog evidence in every locale Android ships. */
    private fun hasAlertDialogButtons(node: AccessibilityNodeInfo?, depth: Int = 0): Boolean {
        if (node == null || depth > 14) return false
        val id = node.viewIdResourceName?.lowercase(Locale.ROOT) ?: ""
        if (id.endsWith("android:id/button1") || id.endsWith("android:id/button2") ||
            id.endsWith("android:id/button3")
        ) return true
        for (i in 0 until node.childCount) {
            if (hasAlertDialogButtons(node.getChild(i), depth + 1)) return true
        }
        return false
    }

    private fun isPackageInstallerOrTamperSettings(pkgName: String, rootNode: AccessibilityNodeInfo?): Boolean {
        if (pkgName in INSTALLER_PACKAGES) {
            // R3b #162 F1: the installer package ALONE used to be
            // enough — installing, updating or uninstalling ANY app
            // mid-session fired the Uninstall Guard, even deleting
            // the distracting app itself in a moment of resolve.
            // The guard's charter is Monolith's own uninstall
            // dialog, which always names Monolith; require the name
            // on screen, and never fire blind when no window text
            // can be read.
            if (rootNode == null) return false
            return collectWindowTextLowercase(rootNode).contains("monolith")
        }
        if (rootNode == null) return false

        if (pkgName in MARKET_PACKAGES) {
            // A market window is a tamper surface only when BOTH
            // hold (Verifier, Wave-3 #42 — the bare substring rule
            // fired on a store SEARCH for "monolith"): the window
            // is about Monolith itself AND an uninstall affordance
            // is actually present on it. Search results and list
            // cards carry no uninstall action; the app page does.
            if (!collectWindowTextLowercase(rootNode).contains("monolith")) return false
            return hasTamperActionButton(rootNode) || hasUninstallAffordance(rootNode)
        }

        if (pkgName in SETTINGS_PACKAGES) {
            val dump = collectWindowTextLowercase(rootNode)
            val mentionsMonolith = dump.contains("monolith")
            if (!mentionsMonolith) return false
            // R3b #162 F3 supersedes the #42 static-page test for
            // Settings: the app-info action buttons and their labels
            // sit permanently on Monolith's App Info page, so the old
            // rule fired on VIEWING alone — the owner could not even
            // read Monolith's storage or permissions mid-session.
            // The guard now fires only on a destructive CONFIRM: the
            // dialog's own phrases (English leg), or framework
            // AlertDialog buttons beside the Monolith anchor
            // (locale-independent leg). Every destructive act still
            // passes through its confirm dialog, so HOLD is kept —
            // the bounce lands at the moment of the act, not at the
            // moment of looking.
            return destructiveDialogPhrases.any { dump.contains(it) } ||
                hasAlertDialogButtons(rootNode)
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

    /** Market-side uninstall evidence: a node whose resource ID
     *  names an uninstall action, or a button whose label does.
     *  The label leg is English-only by necessity (market UIs do
     *  not expose stable IDs for it) — it is a second leg behind
     *  the Monolith anchor, never the only door. */
    private fun hasUninstallAffordance(node: AccessibilityNodeInfo?, depth: Int = 0): Boolean {
        if (node == null || depth > 14) return false
        val viewId = node.viewIdResourceName?.lowercase(Locale.ROOT) ?: ""
        if (viewId.contains("uninstall")) return true
        val className = node.className?.toString() ?: ""
        if (className.endsWith("Button")) {
            val label = (node.text?.toString() ?: node.contentDescription?.toString() ?: "")
                .trim().lowercase(Locale.ROOT)
            if (label == "uninstall") return true
        }
        for (i in 0 until node.childCount) {
            if (hasUninstallAffordance(node.getChild(i), depth + 1)) return true
        }
        return false
    }

    /** One synthetic snapshot of the window already on screen, for
     *  the collector's catch-up scan when the session gate opens. */
    private fun enqueueCurrentWindowSnapshot() {
        val rootNode = try { rootInActiveWindow } catch (e: Exception) { noteDetectionFailure("rootInActiveWindow", e); null } ?: return
        val pkgName = rootNode.packageName?.toString() ?: return
        if (pkgName == applicationContext.packageName ||
            pkgName.startsWith("com.android.systemui")
        ) {
            return
        }
        val snapshot = EventSnapshot(pkgName, captureEventFacts(pkgName), armedSessionId)
        if (snapshot.facts.tamperSurface) {
            scope.launch {
                try {
                    processEvent(snapshot)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                }
            }
        } else {
            eventChannel.trySend(snapshot)
        }
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
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(id) } catch (e: Exception) { noteDetectionFailure("findByViewId", e); null }
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
        val desc = node.contentDescription?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""
        val text = node.text?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (node.isSelected && isFeedTabLabel(text, desc)) {
            val rect = Rect()
            val hasBounds = try { node.getBoundsInScreen(rect); true } catch (_: Exception) { false }
            if (hasBounds && isBottomNavBounds(rect.bottom, rootHeightPx)) return true
        }
        // Structural fallback (Wave-3 #42): a SELECTED node whose
        // resource ID names the feed tab. Resource IDs are code
        // identifiers — they survive locales that translate the
        // tab's spoken label, which the text match above cannot.
        // It takes the same bottom-nav bounds gate as the label
        // leg (#131): a selected channel tab or search chip whose
        // ID happens to name the feed is browsing, not watching.
        val viewId = node.viewIdResourceName?.lowercase(Locale.ROOT) ?: ""
        if (node.isSelected && (
                viewId.contains("shorts") || viewId.contains("reels") ||
                    viewId.contains("clips_tab") || viewId.contains("clips_entry")
            )
        ) {
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

    /** Issue #79: is a URL-bar node currently focused (the user is
     *  typing an address)? Mirrors [collectUrlBarTexts]' ID-suffix
     *  logic. While true, the embed content scan stays silent —
     *  the same don't-fire-mid-typing rule the URL door keeps. */
    private fun hasFocusedUrlBar(node: AccessibilityNodeInfo?, depth: Int = 0): Boolean {
        if (node == null || depth > 32) return false
        val viewId = node.viewIdResourceName?.lowercase(Locale.ROOT) ?: ""
        if (node.isFocused && (
                viewId.endsWith(":id/url_bar") ||
                    viewId.endsWith(":id/location_bar_edit_text") ||
                    viewId.endsWith(":id/mozac_browser_toolbar_url_view")
                )
        ) return true
        for (i in 0 until node.childCount) {
            if (hasFocusedUrlBar(node.getChild(i), depth + 1)) return true
        }
        return false
    }

    private fun collectUrlBarTexts(
        node: AccessibilityNodeInfo?,
        depth: Int,
        out: MutableList<String>
    ) {
        if (node == null || depth > 32 || out.size >= 6) return
        val viewId = node.viewIdResourceName?.lowercase(Locale.ROOT) ?: ""
        if (!node.isFocused && (
                viewId.endsWith(":id/url_bar") ||
                    viewId.endsWith(":id/location_bar_edit_text") ||
                    viewId.endsWith(":id/mozac_browser_toolbar_url_view")
                )
        ) {
            node.text?.toString()?.lowercase(Locale.ROOT)?.takeIf { it.isNotBlank() }?.let { out.add(it) }
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
        return sb.toString().lowercase(Locale.ROOT)
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
            // Issue #78: unique firing id — MainActivity persists
            // its consumption before showing the Airlock, so a
            // process-death redelivery of this intent is dropped.
            putExtra(
                MonolithShieldService.EXTRA_INTERCEPT_ID,
                java.util.UUID.randomUUID().toString()
            )
            // Issue #68 F5, Tier 2: single-use provenance nonce —
            // MainActivity honours no intercept extra without one.
            putExtra(
                MonolithShieldService.EXTRA_INTERCEPT_NONCE,
                InterceptNonce.mint(applicationContext)
            )
        }
        // Issue #94 (F2): never a bare background launch — a dropped
        // launch stamps itself and falls back to a tappable
        // notification (InterceptLaunch).
        InterceptLaunch.launch(applicationContext, interceptIntent, appName)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        if (instance === this) {
            // Same generation close as onUnbind: a dying binding's
            // in-flight probes stamp nothing after this point.
            bindingGeneration++
            instance = null
            lastRootMono = 0L
            armedSessionId = -1
            armedMono = 0L
            foregroundPackage = null
            foregroundMono = 0L
            WatchStore.onAccessibilityBound(false)
        }
        scope.cancel()
        super.onDestroy()
    }
}
