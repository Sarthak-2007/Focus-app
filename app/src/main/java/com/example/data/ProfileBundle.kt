package com.example.data

import java.util.Locale

/**
 * Issue #253, lines 8 + 9 (Audit R5 — main — Unrealised
 * potential): the full-context profile bundle, as pure functions.
 *
 * A profile used to be a category stencil and nothing else: no
 * per-app tiers, limits or exceptions (line 8), and no websites,
 * sound or strictness — those stayed global whatever profile was
 * picked (line 9). The bundle columns on [FocusProfileEntity]
 * carry the rest of the context, encoded as compact CSV text so
 * the profile row stays one row:
 *
 *  - appTiersCsv:  "pkg=TIER,pkg=TIER" — the per-app snapshot.
 *    An entry here is an explicit ruling: it beats the category
 *    stencil at apply time, and an OPEN entry is the per-app
 *    exception the stencil could never express.
 *  - appLimitsCsv: "pkg=minutes,pkg=minutes" — per-app daily
 *    limits riding the same snapshot.
 *  - sitesCsv:     "domain,domain" — the blocked-site set the
 *    profile carries.
 *
 * Every parse is total: junk entries are skipped, never thrown
 * on — these strings are machine-written, but a legacy or
 * hand-edited row must not be able to crash an apply. Formats
 * sort by package/domain so a capture → apply → capture cycle
 * is byte-stable.
 */
object ProfileBundle {

    fun parseAppTiers(csv: String): Map<String, AppBlockTier> {
        if (csv.isBlank()) return emptyMap()
        val out = LinkedHashMap<String, AppBlockTier>()
        for (entry in csv.split(",")) {
            val parts = entry.trim().split("=", limit = 2)
            if (parts.size != 2) continue
            val pkg = parts[0].trim()
            val tier = runCatching { AppBlockTier.valueOf(parts[1].trim()) }.getOrNull()
            if (pkg.isNotEmpty() && tier != null) out[pkg] = tier
        }
        return out
    }

    fun formatAppTiers(tiers: Map<String, AppBlockTier>): String =
        tiers.entries
            .sortedBy { it.key }
            .joinToString(",") { "${it.key}=${it.value.name}" }

    fun parseAppLimits(csv: String): Map<String, Int> {
        if (csv.isBlank()) return emptyMap()
        val out = LinkedHashMap<String, Int>()
        for (entry in csv.split(",")) {
            val parts = entry.trim().split("=", limit = 2)
            if (parts.size != 2) continue
            val pkg = parts[0].trim()
            val minutes = parts[1].trim().toIntOrNull()
            if (pkg.isNotEmpty() && minutes != null && minutes >= 0) out[pkg] = minutes
        }
        return out
    }

    fun formatAppLimits(limits: Map<String, Int>): String =
        limits.entries
            .sortedBy { it.key }
            .joinToString(",") { "${it.key}=${it.value}" }

    fun parseSites(csv: String): List<String> {
        if (csv.isBlank()) return emptyList()
        return csv.split(",")
            .map { it.trim().lowercase(Locale.ROOT) }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    fun formatSites(sites: Collection<String>): String =
        sites.map { it.trim().lowercase(Locale.ROOT) }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
            .joinToString(",")
}
