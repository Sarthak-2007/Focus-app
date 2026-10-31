package com.example.service

/**
 * Issue #80 (Audit R2, blocking-threat-model) — pure matching cores
 * for the doors that used to key on the single foreground package
 * only. Everything here is Android-free so it is unit-testable:
 * the services do the platform walking (windows, node trees,
 * notification shade) and these functions decide.
 */

/** Host surfaces that render OTHER apps' content under their own
 *  package name (finding 5): launchers (widgets, Discover) and
 *  voice assistants. Content on these surfaces must be judged by
 *  what it names, not by the host's package. */
enum class SurfaceHostKind { LAUNCHER, ASSISTANT, OTHER }

private val LAUNCHER_PACKAGES = setOf(
    "com.android.launcher3",
    "com.android.launcher",
    "com.google.android.apps.nexuslauncher",
    "com.sec.android.app.launcher",
    "com.miui.home",
    "com.huawei.android.launcher",
    "com.oneplus.launcher",
    "com.oppo.launcher",
    "com.asus.launcher",
    "com.vivo.launcher",
    "com.realme.launcher"
)

private val ASSISTANT_PACKAGES = setOf(
    "com.google.android.googlequicksearchbox",
    "com.google.android.apps.googleassistant",
    "com.samsung.android.bixby.agent",
    "com.samsung.android.visionintelligence"
)

internal fun surfaceHostKindFor(packageName: String): SurfaceHostKind = when {
    packageName in LAUNCHER_PACKAGES -> SurfaceHostKind.LAUNCHER
    packageName in ASSISTANT_PACKAGES -> SurfaceHostKind.ASSISTANT
    else -> SurfaceHostKind.OTHER
}

private val LOADED_URL_PATTERN =
    Regex("^(https?://|www\\.)?([a-z0-9-]+\\.)+[a-z]{2,}([/?#][^\\s]*)?$")

/**
 * Does this node text look like a LOADED URL (address bar, WebView
 * URL, link preview target) rather than prose that merely mentions
 * a domain? A chat message saying "see instagram.com lol" has
 * spaces and must never fire the site door; "instagram.com/reel/x"
 * committed in a WebView has none. Focused (being-typed) text is
 * filtered by the caller, exactly as the browser door always did.
 */
internal fun looksLikeLoadedUrl(text: String): Boolean {
    val t = text.trim().lowercase()
    if (t.isEmpty() || t.any { it.isWhitespace() }) return false
    return LOADED_URL_PATTERN.matches(t)
}

/** The blocked domain a loaded-URL text carries, or null. */
/**
 * Audit R6 #341 F52: the loaded-URL door with the exclusion
 * carve-out — a text whose host is excluded never matches, the
 * same law the URL-bar door follows.
 */
internal fun matchBlockedDomainInLoadedUrlExcluding(
    text: String,
    domains: List<String>,
    excludedHosts: List<String>
): String? {
    val host = extractUrlHost(text.trim().lowercase())
    if (com.example.data.hostExcluded(host, excludedHosts)) return null
    return matchBlockedDomainInLoadedUrl(text, domains)
}

internal fun matchBlockedDomainInLoadedUrl(text: String, domains: List<String>): String? {
    if (!looksLikeLoadedUrl(text)) return null
    return matchBlockedDomain(text.trim().lowercase(), domains)
}

/**
 * Finding 5's content test for launcher/Assistant surfaces: a
 * single node text that NAMES a non-OPEN app and reads like
 * CONTENT (an attribution line, a card, an Assistant answer).
 * Deliberately NOT a bare label: the home screen's icon grid is
 * made of nodes whose text is exactly the app name ("YouTube"),
 * and firing on those would intercept the launcher itself in a
 * loop — a cure worse than the finding. A node must be at least
 * [MIN_SURFACE_CONTENT_CHARS] long and contain the app's name;
 * short names (< 4 chars) are ignored as too collision-prone.
 * Residue is honest: widget/feed content that nowhere names its
 * source app carries no signal any door can attribute.
 */
internal const val MIN_SURFACE_CONTENT_CHARS = 20

internal fun surfaceNodeTextNamesBlockedApp(
    nodeTexts: List<String>,
    blockedAppNames: List<String>
): String? {
    for (text in nodeTexts) {
        val t = text.trim().lowercase()
        if (t.length < MIN_SURFACE_CONTENT_CHARS) continue
        for (name in blockedAppNames) {
            if (name.length >= 4 && t.contains(name.lowercase())) return name
        }
    }
    return null
}
