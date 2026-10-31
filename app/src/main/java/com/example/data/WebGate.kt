package com.example.data

import android.content.Context
import com.example.service.extractUrlHost
import com.example.service.matchBlockedDomain

/**
 * Issue #339 (Audit R5 supplement — Freedom, Second-pass F1):
 * Freedom sessions can block the ENTIRE web except named
 * exceptions; Monolith could only blacklist a hand-listed set of
 * domains — any unlisted or mirror domain browsed freely in a
 * 'locked' session.
 *
 * The Web Gate is the allowlist mode the audit's Fix names: when
 * armed, a session's web is closed by default and only the user's
 * allowed hosts (and their subdomains) load. The stored blocked
 * list keeps its force in both modes (a listed domain is blocked
 * even if it also appears in the allowlist — the blacklist is the
 * older, more specific instruction).
 */
enum class WebGateVerdict { ALLOW, BLOCK_LISTED, BLOCK_ALL }

/** The gate's pure decision for one committed URL-bar text. */
fun webGateVerdict(
    urlText: String,
    blockAll: Boolean,
    allowedHosts: List<String>,
    blockedDomains: List<String>,
    excludedHosts: List<String> = emptyList()
): WebGateVerdict {
    // Audit R6 #341 F52: an excluded host is carved out of the
    // blocklist BEFORE the blocklist speaks — the exclusion list
    // is checked first, exactly as Freedom's matcher does. (In
    // block-all mode the host still needs the allowlist below;
    // an exclusion exempts from the LIST, not from the gate.)
    if (matchBlockedDomain(urlText, blockedDomains) != null &&
        !hostExcluded(extractUrlHost(urlText), excludedHosts)
    ) {
        return WebGateVerdict.BLOCK_LISTED
    }
    if (!blockAll) return WebGateVerdict.ALLOW
    // Block-all is armed: the host must prove it is allowed. A bar
    // text with no parseable host proves nothing — in the mode the
    // user explicitly armed, unprovable is blocked.
    val host = extractUrlHost(urlText) ?: return WebGateVerdict.BLOCK_ALL
    val allowed = allowedHosts.asSequence()
        .map { it.trim().lowercase().removePrefix("*.").substringBefore('/') }
        .filter { it.isNotEmpty() }
        .any { allowedHost -> host == allowedHost || host.endsWith(".$allowedHost") }
    return if (allowed) WebGateVerdict.ALLOW else WebGateVerdict.BLOCK_ALL
}

/**
 * Audit R6 #341 F52: the exclusion test — is [host] carved out by
 * the user's exclusion list (exact host or a subdomain of an
 * excluded host)? Pure; shared by the gate and both site doors.
 */
fun hostExcluded(host: String?, excludedHosts: List<String>): Boolean {
    if (host.isNullOrEmpty()) return false
    val clean = host.trim().lowercase().removePrefix("www.")
    return excludedHosts.asSequence()
        .map { it.trim().lowercase().removePrefix("*.").substringBefore('/').removePrefix("www.") }
        .filter { it.isNotEmpty() }
        .any { excluded -> clean == excluded || clean.endsWith(".$excluded") }
}

/** Normalise a user-typed allowed host the way the matcher reads
 *  it: bare host, lowercase, no scheme, no path, no port. */
fun normaliseAllowedHost(raw: String): String? {
    val host = raw.trim().lowercase()
        .removePrefix("https://").removePrefix("http://")
        .removePrefix("*.").substringBefore('/').substringBefore(':')
        .removePrefix("www.")
    return host.takeIf { it.contains('.') && !it.contains(' ') && !it.contains('@') }
}

/**
 * Persisted Web Gate state (SharedPreferences — a Guard-grade
 * preference, deliberately outside the Room seal: toggling the
 * gate must never mutate a signed session row).
 */
class WebGateStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var cachedBlockAll: Boolean? = null

    @Volatile
    private var cachedAllowed: List<String>? = null

    fun blockAll(): Boolean {
        cachedBlockAll?.let { return it }
        val value = prefs.getBoolean(KEY_BLOCK_ALL, false)
        cachedBlockAll = value
        return value
    }

    fun setBlockAll(on: Boolean) {
        prefs.edit().putBoolean(KEY_BLOCK_ALL, on).apply()
        cachedBlockAll = on
    }

    fun allowedHosts(): List<String> {
        cachedAllowed?.let { return it }
        val value = (prefs.getStringSet(KEY_ALLOWED, emptySet()) ?: emptySet()).sorted()
        cachedAllowed = value
        return value
    }

    fun addAllowedHost(raw: String): Boolean {
        val host = normaliseAllowedHost(raw) ?: return false
        val next = (allowedHosts() + host).distinct().sorted()
        prefs.edit().putStringSet(KEY_ALLOWED, next.toSet()).apply()
        cachedAllowed = next
        return true
    }

    fun removeAllowedHost(host: String) {
        val next = allowedHosts() - host
        prefs.edit().putStringSet(KEY_ALLOWED, next.toSet()).apply()
        cachedAllowed = next
    }

    // Audit R6 #341 F52: the exclusion list — hosts carved out of
    // the ordinary blocklist matcher (see hostExcluded). Stored
    // beside the allowlist; the two lists answer different
    // questions and never merge.
    @Volatile
    private var cachedExcluded: List<String>? = null

    fun excludedHosts(): List<String> {
        cachedExcluded?.let { return it }
        val value = (prefs.getStringSet(KEY_EXCLUDED, emptySet()) ?: emptySet()).sorted()
        cachedExcluded = value
        return value
    }

    fun addExcludedHost(raw: String): Boolean {
        val host = normaliseAllowedHost(raw) ?: return false
        val next = (excludedHosts() + host).distinct().sorted()
        prefs.edit().putStringSet(KEY_EXCLUDED, next.toSet()).apply()
        cachedExcluded = next
        return true
    }

    fun removeExcludedHost(host: String) {
        val next = excludedHosts() - host
        prefs.edit().putStringSet(KEY_EXCLUDED, next.toSet()).apply()
        cachedExcluded = next
    }

    companion object {
        private const val PREFS_NAME = "web_gate"
        private const val KEY_BLOCK_ALL = "block_all"
        private const val KEY_ALLOWED = "allowed_hosts"
        private const val KEY_EXCLUDED = "excluded_hosts"
    }
}
