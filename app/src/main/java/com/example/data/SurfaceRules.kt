package com.example.data

import android.content.Context

/**
 * Issue #335 (Audit R5 supplement — Freedom F2/F3): Monolith's
 * in-app surface knowledge was compiled-in Kotlin constants — the
 * Shorts/Reels view-id list for exactly two packages and a closed
 * URL-bar suffix list — so a renamed view id or an unlisted browser
 * silently stopped enforcement until a developer shipped an app
 * update. Freedom's equivalent knowledge is runtime data.
 *
 * This store makes Monolith's surface knowledge DATA, not code:
 *  - [SurfaceRule] pairs a provider package with the view ids whose
 *    visible presence means a blockable surface is on screen.
 *  - The shipped defaults are exactly the rules that used to be the
 *    hard-coded constants (YouTube Shorts player, Instagram Reels
 *    viewer, the three URL-bar id suffixes) — expressed as data.
 *  - Learned rules (a surface id or URL-bar id discovered on-device
 *    for a package that had none) are persisted in SharedPreferences
 *    and merged over the defaults at read time, so enforcement can
 *    extend to a third app or a new browser WITHOUT a code change.
 *  - Everything stays on this phone: no rule ever leaves the device
 *    (the app's settled local-only design; see #335 F1/F4 strikes).
 */
data class SurfaceRule(
    val providerPackage: String,
    val viewIds: List<String>
)

/** The shipped surface rules — the former constants, as data. */
val DEFAULT_SURFACE_RULES: List<SurfaceRule> = listOf(
    SurfaceRule(
        "com.google.android.youtube",
        listOf(
            "com.google.android.youtube:id/reel_player_page_container",
            "com.google.android.youtube:id/shorts_player"
        )
    ),
    SurfaceRule(
        "com.instagram.android",
        listOf(
            "com.instagram.android:id/clips_viewer_view_pager",
            "com.instagram.android:id/reel_viewer_fragment"
        )
    )
)

/** URL-bar resource-id suffixes every Chromium/Firefox-family bar
 *  is matched by, before any per-browser learned suffix. */
val DEFAULT_URL_BAR_SUFFIXES: List<String> = listOf(
    ":id/url_bar",
    ":id/location_bar_edit_text",
    ":id/mozac_browser_toolbar_url_view"
)

private const val MAX_VIEW_IDS_PER_PACKAGE = 32
private const val MAX_RULE_PACKAGES = 64

/** Union of [defaults] and [learned], keyed by package: a learned
 *  rule for a known package EXTENDS its id set; a learned rule for
 *  a new package ADDS the package. Order-stable, de-duplicated,
 *  capped so a corrupt store can never grow enforcement unbounded. */
fun mergeSurfaceRules(
    defaults: List<SurfaceRule>,
    learned: List<SurfaceRule>
): List<SurfaceRule> {
    val byPackage = LinkedHashMap<String, LinkedHashSet<String>>()
    for (rule in defaults + learned) {
        val pkg = rule.providerPackage.trim().lowercase()
        if (pkg.isEmpty()) continue
        val ids = byPackage.getOrPut(pkg) { LinkedHashSet() }
        for (id in rule.viewIds) {
            val clean = id.trim().lowercase()
            if (clean.isNotEmpty() && ids.size < MAX_VIEW_IDS_PER_PACKAGE) ids.add(clean)
        }
    }
    return byPackage.entries.take(MAX_RULE_PACKAGES)
        .map { (pkg, ids) -> SurfaceRule(pkg, ids.toList()) }
}

/** [rules] plus one learned view id for [providerPackage]. */
fun withLearnedViewId(
    rules: List<SurfaceRule>,
    providerPackage: String,
    viewId: String
): List<SurfaceRule> =
    mergeSurfaceRules(rules, listOf(SurfaceRule(providerPackage, listOf(viewId))))

/** The effective view-id set for [providerPackage], or empty when
 *  no rule (shipped or learned) covers the package. */
fun surfaceViewIdsFor(rules: List<SurfaceRule>, providerPackage: String): List<String> =
    rules.firstOrNull { it.providerPackage == providerPackage.trim().lowercase() }
        ?.viewIds ?: emptyList()

/** Line encoding for persistence: `package<TAB>id1|id2|…` per line.
 *  Resource ids never contain tab, pipe or newline, so the format
 *  is unambiguous without a JSON dependency. */
fun encodeSurfaceRules(rules: List<SurfaceRule>): String =
    rules.joinToString("\n") { rule ->
        rule.providerPackage + "\t" + rule.viewIds.joinToString("|")
    }

/** Tolerant decode: malformed lines are skipped, never fatal — a
 *  corrupt learned store degrades to the shipped defaults. */
fun decodeSurfaceRules(text: String?): List<SurfaceRule> {
    if (text.isNullOrBlank()) return emptyList()
    return text.lines().mapNotNull { line ->
        val parts = line.split("\t")
        if (parts.size != 2) return@mapNotNull null
        val pkg = parts[0].trim().lowercase()
        val ids = parts[1].split("|").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        if (pkg.isEmpty() || ids.isEmpty()) null else SurfaceRule(pkg, ids)
    }
}

/**
 * Persisted, on-device surface-rule store. Reads merge the shipped
 * defaults with whatever has been learned on this phone; learning
 * writes only the learned delta. In-memory cached after first read
 * (the accessibility event path consults it per event).
 */
class SurfaceRuleStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var cachedRules: List<SurfaceRule>? = null

    @Volatile
    private var cachedUrlBarRules: List<SurfaceRule>? = null

    /** Effective surface rules: defaults + learned, merged. */
    fun rules(): List<SurfaceRule> {
        cachedRules?.let { return it }
        val merged = mergeSurfaceRules(
            DEFAULT_SURFACE_RULES,
            decodeSurfaceRules(prefs.getString(KEY_LEARNED_SURFACES, null))
        )
        cachedRules = merged
        return merged
    }

    /** Effective URL-bar suffixes for [browserPackage]: the shipped
     *  suffixes plus any suffix learned for this browser on-device. */
    fun urlBarSuffixesFor(browserPackage: String): List<String> {
        val learned = urlBarRules()
        val extra = surfaceViewIdsFor(learned, browserPackage)
        return (DEFAULT_URL_BAR_SUFFIXES + extra).distinct()
    }

    /** True when a URL-bar rule was learned for [browserPackage] —
     *  i.e. the diagnostic walk found a working address bar in a
     *  browser the shipped list never named (#335 F2). */
    fun hasLearnedBrowser(browserPackage: String): Boolean =
        surfaceViewIdsFor(urlBarRules(), browserPackage).isNotEmpty()

    /** Learn, on-device, that [viewId] in [providerPackage] is a
     *  blockable surface. Persists the delta and refreshes the
     *  cache; enforcement picks it up with no app update. */
    @Synchronized
    fun learnSurfaceViewId(providerPackage: String, viewId: String) {
        val learned = decodeSurfaceRules(prefs.getString(KEY_LEARNED_SURFACES, null))
        val next = withLearnedViewId(learned, providerPackage, viewId)
        prefs.edit().putString(KEY_LEARNED_SURFACES, encodeSurfaceRules(next)).apply()
        cachedRules = mergeSurfaceRules(DEFAULT_SURFACE_RULES, next)
    }

    /** Learn, on-device, the URL-bar id suffix that works in
     *  [browserPackage] (the diagnostic-walk result for an unknown
     *  browser, #335 F2). The suffix is stored package-qualified by
     *  the caller when known; suffix form is what matching uses. */
    @Synchronized
    fun learnBrowserUrlBarSuffix(browserPackage: String, suffix: String) {
        val learned = decodeSurfaceRules(prefs.getString(KEY_LEARNED_URL_BARS, null))
        val next = withLearnedViewId(learned, browserPackage, suffix)
        prefs.edit().putString(KEY_LEARNED_URL_BARS, encodeSurfaceRules(next)).apply()
        cachedUrlBarRules = next
    }

    private fun urlBarRules(): List<SurfaceRule> {
        cachedUrlBarRules?.let { return it }
        val decoded = decodeSurfaceRules(prefs.getString(KEY_LEARNED_URL_BARS, null))
        cachedUrlBarRules = decoded
        return decoded
    }

    companion object {
        private const val PREFS_NAME = "surface_rules"
        private const val KEY_LEARNED_SURFACES = "learned_surface_rules"
        private const val KEY_LEARNED_URL_BARS = "learned_url_bar_rules"
    }
}

/**
 * Audit R6 #341 — the BROWSER COVERAGE TABLE (F10 / F16 / F27 /
 * F38). Browser coverage used to be a hardcoded package set plus
 * one global URL-bar id list tried against every browser. It is
 * now a shipped DATA TABLE in the SurfaceRules tradition (data,
 * not code — the #335 settlement): each row names a browser, the
 * extraction ENGINE its address bar answers to, the engine's
 * extra URL-bar id suffixes, and the private-mode BADGES its
 * windows print. Adding a browser is adding a row.
 *
 * Engines (F16 — the per-browser extraction split):
 * - CHROMIUM: the shared Chromium bar ids (the defaults).
 * - FIREFOX: the mozac toolbar id (in the defaults) — its own
 *   engine row so a Firefox id change touches one row.
 * - GENERIC: no known bar id — extraction rides the generic
 *   address-bar leg (an unfocused EditText whose whole text is a
 *   URL), which the service already runs for suffix-less bars.
 *
 * Stock OEM browsers (F27): Mi Browser, Vivo Browser, HeyTap
 * Browser (Oppo/Realme), Huawei Browser and the AOSP stock
 * browser are rows here — before this table they were in NO set,
 * so the stock browser on Xiaomi/Vivo/Oppo phones could never be
 * site-blocked while the board printed SOCIAL SITES: RUNNING.
 */
enum class BrowserEngine { CHROMIUM, FIREFOX, GENERIC }

data class BrowserProfile(
    val packageName: String,
    val engine: BrowserEngine,
    /** Extra URL-bar id suffixes for this browser's engine,
     *  beyond [DEFAULT_URL_BAR_SUFFIXES]. */
    val urlBarSuffixes: List<String> = emptyList(),
    /** Lowercase window-text phrases that mean THIS browser is
     *  showing a private/incognito window (F38). Empty = this
     *  browser prints no detectable badge; its private state is
     *  UNKNOWN, never silently "normal". */
    val incognitoBadges: List<String> = emptyList(),
    /** True for the browser that ships on the phone (F27). */
    val stockOem: Boolean = false
)

val BROWSER_PROFILES: List<BrowserProfile> = listOf(
    BrowserProfile("com.android.chrome", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("gone incognito", "incognito tab")),
    BrowserProfile("com.brave.browser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("private tab", "you are in a private tab")),
    BrowserProfile("org.mozilla.firefox", BrowserEngine.FIREFOX,
        incognitoBadges = listOf("private browsing", "you're in private browsing", "you are in private browsing")),
    BrowserProfile("com.microsoft.emmx", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("inprivate")),
    BrowserProfile("com.sec.android.app.sbrowser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("secret mode")),
    BrowserProfile("com.opera.browser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("private mode")),
    BrowserProfile("com.opera.mini.native", BrowserEngine.GENERIC,
        incognitoBadges = listOf("private mode")),
    BrowserProfile("com.opera.gx", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("private mode")),
    BrowserProfile("com.duckduckgo.mobile.android", BrowserEngine.GENERIC),
    BrowserProfile("com.kiwibrowser.browser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("gone incognito", "incognito tab")),
    BrowserProfile("mark.via.gp", BrowserEngine.GENERIC),
    BrowserProfile("com.yandex.browser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("incognito mode")),
    BrowserProfile("com.UCMobile.intl", BrowserEngine.GENERIC),
    BrowserProfile("com.vivaldi.browser", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("private tab")),
    BrowserProfile("com.opera.browser.beta", BrowserEngine.CHROMIUM,
        incognitoBadges = listOf("private mode")),
    // F27: the stock OEM browsers — policed from this build on.
    BrowserProfile("com.mi.globalbrowser", BrowserEngine.GENERIC, stockOem = true,
        incognitoBadges = listOf("incognito mode")),
    BrowserProfile("com.vivo.browser", BrowserEngine.GENERIC, stockOem = true,
        incognitoBadges = listOf("private mode", "secret mode")),
    BrowserProfile("com.heytap.browser", BrowserEngine.GENERIC, stockOem = true,
        incognitoBadges = listOf("private mode")),
    BrowserProfile("com.huawei.browser", BrowserEngine.GENERIC, stockOem = true,
        incognitoBadges = listOf("private mode")),
    BrowserProfile("com.android.browser", BrowserEngine.GENERIC, stockOem = true,
        incognitoBadges = listOf("incognito tab"))
)

/** The coverage table's row for [packageName], or null — a
 *  package with no row is an unsupported browser, never silently
 *  supported (F10). Learned browsers (#335) extend coverage at
 *  runtime through SurfaceRuleStore, not through this table. */
fun browserProfileFor(packageName: String): BrowserProfile? =
    BROWSER_PROFILES.firstOrNull { it.packageName == packageName }

/** F16: the URL-bar id suffixes an ENGINE's toolbars use, beyond
 *  the shared defaults — Firefox's mozac toolbar ids are not
 *  Chromium's bar ids, and extraction keyed to one global list
 *  missed every browser whose bar the list didn't name. */
fun engineUrlBarSuffixes(engine: BrowserEngine): List<String> = when (engine) {
    BrowserEngine.CHROMIUM -> listOf(":id/url_bar", ":id/omnibox_url_text")
    BrowserEngine.FIREFOX -> listOf(
        ":id/mozac_browser_toolbar_url_view",
        ":id/mozac_browser_toolbar_edit_view"
    )
    BrowserEngine.GENERIC -> listOf(":id/url_bar", ":id/address_bar")
}

/** The URL-bar suffixes extraction must try for [packageName]:
 *  the shipped defaults + the browser's own suffixes (its
 *  profile's explicit list, else its ENGINE's list) + any suffix
 *  learned for this browser on-device (F16). */
fun urlBarSuffixesForBrowser(
    packageName: String,
    learnedSuffixes: List<String>
): List<String> {
    val profile = browserProfileFor(packageName)
    val engineSuffixes = when {
        profile == null -> emptyList()
        profile.urlBarSuffixes.isNotEmpty() -> profile.urlBarSuffixes
        else -> engineUrlBarSuffixes(profile.engine)
    }
    return (DEFAULT_URL_BAR_SUFFIXES + engineSuffixes + learnedSuffixes).distinct()
}

/** F38: what can honestly be said about a browser window's
 *  private state. UNKNOWN is a real answer: a browser whose
 *  profile carries no badge table cannot be pronounced normal. */
enum class IncognitoState { PRIVATE, NORMAL, UNKNOWN }

/**
 * F38 detection: [windowTextLowercase] is the browser window's
 * collected text (the service's browserContentText). A badge hit
 * means PRIVATE; no hit means NORMAL only when this browser HAS
 * a badge table to miss; no table means UNKNOWN.
 */
fun incognitoStateFor(packageName: String, windowTextLowercase: String): IncognitoState {
    val profile = browserProfileFor(packageName) ?: return IncognitoState.UNKNOWN
    if (profile.incognitoBadges.isEmpty()) return IncognitoState.UNKNOWN
    val text = windowTextLowercase.lowercase()
    return if (profile.incognitoBadges.any { text.contains(it) }) {
        IncognitoState.PRIVATE
    } else {
        IncognitoState.NORMAL
    }
}
