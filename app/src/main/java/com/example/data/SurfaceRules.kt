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
