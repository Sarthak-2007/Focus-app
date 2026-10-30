package com.example.data

/**
 * THE CATALOG RECONCILE (Ordner lane — Sarthak's complaint, Oct 29
 * 22:40 IST: "the list of apps is too long, it contains apps that I
 * haven't installed").
 *
 * Pure reconciliation between three facts: the rows the table holds,
 * the packages the phone actually has (launchable, installed — the
 * PackageManager launcher query, whose visibility the manifest's
 * <queries> declaration grants), and today's real usage. The table
 * ends the sync holding the installed set — but a row is a man's
 * RULING, not a cache entry, so the sync may only take a row away
 * on corroborated evidence, never on one query's say-so (Wave 3,
 * issue #34 — the honest-catalog repairs):
 *
 * - PRUNE NEEDS TWO CONSECUTIVE ABSENCES. A package absent from
 *   one sync is only NOTED ([AppCatalogPlan.pendingPrune], which
 *   the caller persists); it is pruned by a later sync only if it
 *   is absent again. An empty installed list prunes nothing and
 *   notes nothing — it can only be a failed query. A truncated or
 *   visibility-filtered result therefore cannot wipe the catalog:
 *   it would have to lie identically on two separate syncs, and
 *   even then the tombstone below keeps the loss reversible.
 * - TWO STRIKES MEANS TWO GENUINELY SEPARATE LOOKS (fresh-eyes
 *   L3). The ViewModel init, its own guard refresh, and PARK all
 *   call the sync seconds apart against identical PackageManager
 *   state, so without a guard ONE transiently-filtered query at
 *   cold start satisfied both strikes in a single launch. Repeat
 *   syncs inside [SYNC_COLLAPSE_WINDOW_MILLIS] of a completed sync
 *   are collapsed by the caller (see [isCollapseRepeat]): they
 *   return without touching the strike set at all. A second look
 *   only counts once the window has passed.
 * - A PRUNED RULE IS REMEMBERED. The caller files the pruned rows'
 *   tiers/limits as remembered rules (prefs, no schema change) and
 *   passes them back as [rememberedRules]; a rediscovered package
 *   is reborn with its REMEMBERED ruling ahead of any factory
 *   preset or category default — a user's LOCKED never silently
 *   reopens because an app was transiently unlaunchable (disabled,
 *   launcher-less, in another profile).
 * - a stored row whose package is corroborated-gone is PRUNED (so
 *   no reader of this table can count a ghost forever: not the
 *   Shield list, not the Deck's held count, not PARK's census);
 * - an installed package with no row is DISCOVERED, uncapped, born
 *   with its remembered rule if it has one, else its factory
 *   preset, else its category default;
 * - an installed package with a row keeps its RULING untouched —
 *   the man's own tier and limit are never re-priced by a sync —
 *   but its FACTS follow the phone: the label and category are
 *   refreshed from the installed record, and its usage figure
 *   follows the real one — including a real zero. A package the
 *   usage map does not mention keeps its figure: the map is a
 *   partial witness (no permission reads as an empty map), and an
 *   unmeasured app must not be zeroed on a guess.
 *
 * The old blind factory seed (twelve rows inserted whether or not
 * the apps existed, with invented usage and intercept figures) is
 * gone. The presets below keep only what a preset may honestly
 * carry: a birth tier and a daily limit. Usage starts real (from the
 * usage query) and intercepts start at zero, because both are
 * history, and history is never seeded.
 */
data class InstalledPackage(
    val packageName: String,
    val label: String,
    /** True when this entry is the package's launcher/main activity
     *  (the caller resolves it against the package's launch intent).
     *  One package can surface several launcher activities with
     *  different labels; the main activity's label is the app's
     *  name, the others are shortcuts wearing it. */
    val isMainActivity: Boolean = false
)

/** A birth rule for a package: the tier and daily limit it is
 *  discovered with IF it is actually installed. */
data class AppRulePreset(
    val tier: AppBlockTier,
    val dailyLimitMinutes: Int
)

/** What one sync must do to make the table equal the phone. */
data class AppCatalogPlan(
    val toInsert: List<BlockedAppEntity>,
    /** Rows to write back: a usage figure that followed the real
     *  one and/or a label/category refreshed from the phone. The
     *  ruling fields (tier, limit) on these copies are always the
     *  stored ones — a sync never re-prices a ruling. */
    val usageUpdates: List<BlockedAppEntity>,
    val toPrune: List<String>,
    /** Packages absent from THIS sync but not yet prunable (first
     *  absence). The caller persists this set and hands it back as
     *  `previouslyAbsent` on the next sync; only a package absent
     *  twice in a row is pruned. */
    val pendingPrune: List<String> = emptyList()
)

object AppCatalogReconcile {

    val FACTORY_PRESETS: Map<String, AppRulePreset> = mapOf(
        "com.zhiliaoapp.musically" to AppRulePreset(AppBlockTier.LOCKED, 15),
        "com.instagram.android" to AppRulePreset(AppBlockTier.LOCKED, 20),
        "com.google.android.youtube" to AppRulePreset(AppBlockTier.PEEK_2M, 30),
        "com.twitter.android" to AppRulePreset(AppBlockTier.LOCKED, 15),
        "com.reddit.frontpage" to AppRulePreset(AppBlockTier.LOCKED, 20),
        "com.snapchat.android" to AppRulePreset(AppBlockTier.LOCKED, 20),
        "com.whatsapp" to AppRulePreset(AppBlockTier.PEEK_2M, 45),
        "com.discord" to AppRulePreset(AppBlockTier.PEEK_2M, 45),
        "com.supercell.clashroyale" to AppRulePreset(AppBlockTier.LOCKED, 15),
        "com.roblox.client" to AppRulePreset(AppBlockTier.LOCKED, 20),
        "com.android.chrome" to AppRulePreset(AppBlockTier.OPEN, 60),
        "com.google.android.apps.docs" to AppRulePreset(AppBlockTier.OPEN, 120)
    )

    /** THE DEDUPE RULE (issue #34): one package is one app, however
     *  many launcher activities it surfaces. The surviving record
     *  is, in order: (1) an entry flagged as the main/launcher
     *  activity; (2) otherwise the lexicographically smallest label
     *  (case-insensitive, then exact) — a stated, deterministic rule
     *  that never depends on the query's return order. */
    fun dedupeInstalled(installed: List<InstalledPackage>): Map<String, InstalledPackage> =
        installed.groupBy { it.packageName }.mapValues { (_, entries) ->
            entries.firstOrNull { it.isMainActivity }
                ?: entries.minWithOrNull(
                    compareBy({ it.label.lowercase() }, { it.label })
                )!!
        }

    /** How long after a completed catalog sync a repeat sync is
     *  the SAME look, not a second one (fresh-eyes L3). The
     *  same-launch callers fire seconds apart — even a slow cold
     *  start is a minute, not ten — while genuinely separate
     *  looks (the next launch, a later guard refresh) sit far
     *  outside it, so a truly-gone app is still pruned promptly
     *  and a transiently-filtered query can never spend both
     *  strikes at once. Ten minutes also sits under the
     *  platform's own 15-minute periodic-work floor: nothing
     *  legitimate is delayed longer than Android itself would
     *  delay it. */
    const val SYNC_COLLAPSE_WINDOW_MILLIS: Long = 10L * 60L * 1000L

    /** True when a sync arriving at [nowMillis] is a repeat of
     *  the look that completed at [lastSuccessfulSyncMillis] —
     *  inside the collapse window — and must not touch the
     *  strike set. No recorded sync (0) is never a repeat, and a
     *  clock that moved backwards is never a repeat either: a
     *  fresh look is the safe answer to a lying clock. */
    fun isCollapseRepeat(
        lastSuccessfulSyncMillis: Long,
        nowMillis: Long,
        windowMillis: Long = SYNC_COLLAPSE_WINDOW_MILLIS
    ): Boolean {
        if (lastSuccessfulSyncMillis <= 0L) return false
        val elapsed = nowMillis - lastSuccessfulSyncMillis
        return elapsed >= 0L && elapsed < windowMillis
    }

    fun reconcile(
        existing: List<BlockedAppEntity>,
        installed: List<InstalledPackage>,
        usageMinutes: Map<String, Int>,
        categorize: (String, String) -> AppCategory,
        previouslyAbsent: Set<String> = emptySet(),
        rememberedRules: Map<String, AppRulePreset> = emptyMap()
    ): AppCatalogPlan {
        val installedByPackage = dedupeInstalled(installed)
        val existingByPackage = existing.associateBy { it.packageName }

        // An empty query is a failed query, never evidence: prune
        // nothing, note nothing.
        if (installedByPackage.isEmpty()) {
            return AppCatalogPlan(emptyList(), emptyList(), emptyList(), emptyList())
        }

        // Two consecutive absences or the row stays. The first
        // absence is only noted (pendingPrune) for the caller to
        // persist; a package present again clears its own note by
        // simply not being absent.
        val absent = existing
            .filter { it.packageName !in installedByPackage }
            .map { it.packageName }
        val toPrune = absent.filter { it in previouslyAbsent }
        val pendingPrune = absent.filter { it !in previouslyAbsent }

        val toInsert = mutableListOf<BlockedAppEntity>()
        val usageUpdates = mutableListOf<BlockedAppEntity>()
        for ((pkg, info) in installedByPackage) {
            val current = existingByPackage[pkg]
            if (current == null) {
                val category = categorize(pkg, info.label)
                // His own remembered ruling outranks the factory:
                // a rule survives its app's transient absence.
                val remembered = rememberedRules[pkg]
                val preset = FACTORY_PRESETS[pkg]
                val tier = remembered?.tier ?: preset?.tier ?: when (category) {
                    AppCategory.SHORT_VIDEO, AppCategory.GAMES -> AppBlockTier.LOCKED
                    AppCategory.MESSAGING -> AppBlockTier.PEEK_2M
                    else -> AppBlockTier.OPEN
                }
                toInsert += BlockedAppEntity(
                    packageName = pkg,
                    appName = info.label,
                    category = category.name,
                    isBlocked = tier != AppBlockTier.OPEN,
                    blockTier = tier.name,
                    dailyLimitMinutes = remembered?.dailyLimitMinutes
                        ?: preset?.dailyLimitMinutes ?: 30,
                    todayUsageMinutes = usageMinutes[pkg] ?: 0,
                    interceptCount = 0,
                    isSystemDetected = true
                )
            } else {
                // Facts follow the phone (label, category, usage —
                // a real 0 included); the ruling never moves.
                val category = categorize(pkg, info.label)
                val realMinutes = usageMinutes[pkg]
                val refreshed = current.copy(
                    appName = info.label,
                    category = category.name,
                    todayUsageMinutes = realMinutes ?: current.todayUsageMinutes
                )
                if (refreshed != current) {
                    usageUpdates += refreshed
                }
            }
        }
        return AppCatalogPlan(toInsert, usageUpdates, toPrune, pendingPrune)
    }

    /** THE BORN CATEGORY (fresh-eyes L2). A newly-seen app's
     *  category decides its born tier (GAMES / SHORT_VIDEO are
     *  born LOCKED, MESSAGING born PEEK_2M), so a sloppy match is
     *  a sloppy ruling. The old matcher ran raw substrings over
     *  "$pkg $label": "play" made the Google Play Store a GAME,
     *  "Display…" a game too, "reel" fired inside "Freelance",
     *  "message" inside any longer word. Matching is now on
     *  WHOLE TOKENS: package and label are split (dots, spaces,
     *  punctuation, camel humps) and a key must equal a token —
     *  or, for a deliberate multi-word key like "play games",
     *  a consecutive token sequence. Plurals that substring
     *  matching used to catch for free ("games", "reels",
     *  "messages") are stated as their own keys. The bare key
     *  "play" is GONE: as a lone word it names stores and media
     *  players far more often than games; it survives only
     *  inside "play games", where it means what it says.
     *  Category order is the old order — first match wins. */
    fun categorize(pkg: String, label: String): AppCategory {
        val pkgTokens = tokenize(pkg)
        val labelTokens = tokenize(label)
        fun matches(vararg keys: String): Boolean = keys.any { key ->
            val keyTokens = key.split(' ')
            containsTokenSequence(pkgTokens, keyTokens) ||
                containsTokenSequence(labelTokens, keyTokens)
        }
        return when {
            matches("tiktok", "youtube", "instagram", "reel", "reels") ->
                AppCategory.SHORT_VIDEO
            matches("twitter", "reddit", "snapchat", "facebook") ->
                AppCategory.SOCIAL
            matches("game", "games", "clash", "roblox", "play games") ->
                AppCategory.GAMES
            matches(
                "whatsapp", "discord", "telegram", "message",
                "messages", "messaging", "messenger"
            ) -> AppCategory.MESSAGING
            matches("chrome", "browser", "browsers", "firefox", "brave") ->
                AppCategory.BROWSERS
            else -> AppCategory.SHOPPING
        }
    }

    /** SUMMING usage aggregation (Fix 4 — the finding-5 bug
     *  class, one map over). The repository's usage query built
     *  its map with `associate{}`, which silently keeps the LAST
     *  bucket per package; INTERVAL_DAILY over a trailing-12h
     *  window can return several buckets for one package, so a
     *  multi-bucket app's "today usage" under-reported — the
     *  stored figure then failed to follow the real one
     *  (finding 3's territory). Buckets for a package SUM. A
     *  zero bucket still yields a present 0 entry: presence in
     *  the map is the "measured" witness reconcile trusts. */
    fun aggregateUsageMinutes(buckets: List<Pair<String, Int>>): Map<String, Int> =
        buckets.groupingBy { it.first }.fold(0) { acc, (_, minutes) -> acc + minutes }

    /** Whole tokens of a package name or label: split on
     *  non-alphanumerics and camel humps, lowercased. The raw
     *  chunk is kept alongside its camel parts, so "WhatsApp"
     *  yields whatsapp (and whats, app) and "YouTube" yields
     *  youtube (and you, tube) — the brand keys match the raw
     *  chunk, the parts only add evidence, never remove it. */
    private fun tokenize(text: String): List<String> {
        val tokens = mutableListOf<String>()
        for (chunk in text.split(Regex("[^A-Za-z0-9]+"))) {
            if (chunk.isEmpty()) continue
            tokens += chunk.lowercase()
            val parts = chunk.split(
                Regex(
                    "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])" +
                        "|(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])"
                )
            )
            if (parts.size > 1) {
                parts.forEach { tokens += it.lowercase() }
            }
        }
        return tokens
    }

    /** True when [key] appears as a consecutive run in [tokens]
     *  — whole-token equality; a single-word key is a run of one. */
    private fun containsTokenSequence(tokens: List<String>, key: List<String>): Boolean {
        if (key.isEmpty() || key.size > tokens.size) return false
        for (start in 0..(tokens.size - key.size)) {
            if (tokens.subList(start, start + key.size) == key) return true
        }
        return false
    }
}
