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

/**
 * The one package classifier (Issue #132, music-during-focus;
 * merged with Issue #107's shield-setup-lived #3 cures).
 * Pure and total: every package+label lands in exactly one
 * [AppCategory]. Extracted from MonolithRepository so the
 * classification is unit-testable and the repository delegates.
 *
 * MUSIC IS CHECKED FIRST, BY TOKEN, not substring: a media
 * player's identity must beat the traps below it —
 * "youtube.music" would otherwise file YouTube Music as
 * SHORT_VIDEO and any "…Player" label as GAMES via "play".
 * Token matching (split on non-alphanumerics) is what keeps
 * the check honest: "musically" (TikTok's package) contains
 * the substring "music" but is not the token "music".
 *
 * Issue #107: the GAMES branch no longer carries the bare
 * substring "play" — it filed the Play Store itself (and any
 * label containing "display") under GAMES. Game tokens are
 * specific now, "musically" files SHORT_VIDEO, and the guess
 * is printed on every catalogue row and correctable there, so
 * a wrong guess is a fact the user can see and fix, not a
 * secret that prices his apps.
 */
object AppCategorizer {

    private val MUSIC_TOKENS = setOf(
        "music", "podcast", "podcasts", "audio", "audiobook", "audiobooks",
        "radio", "spotify", "soundcloud", "deezer", "tidal", "gaana",
        "wynk", "shazam", "bandcamp", "audius", "audible", "tunein",
        "castbox", "audiomack", "pocketcasts"
    )

    fun categorize(pkg: String, label: String): AppCategory {
        val lower = "$pkg $label".lowercase()
        val tokens = lower.split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }
        if (tokens.any { it in MUSIC_TOKENS }) return AppCategory.MUSIC
        return when {
            lower.contains("tiktok") || lower.contains("musically") || lower.contains("youtube") || lower.contains("instagram") || lower.contains("reel") ->
                AppCategory.SHORT_VIDEO
            lower.contains("twitter") || lower.contains("reddit") || lower.contains("snapchat") || lower.contains("facebook") ->
                AppCategory.SOCIAL
            lower.contains("game") || lower.contains("clash") || lower.contains("roblox") || lower.contains("minecraft") || lower.contains("pubg") || lower.contains("candycrush") ->
                AppCategory.GAMES
            lower.contains("whatsapp") || lower.contains("discord") || lower.contains("telegram") || lower.contains("message") ->
                AppCategory.MESSAGING
            lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") || lower.contains("brave") ->
                AppCategory.BROWSERS
            else -> AppCategory.SHOPPING
        }
    }
}

/**
 * Issue #107 test seam: the classifier under its audit name.
 * Delegates to the ONE classifier ([AppCategorizer]) — there is
 * no second implementation to drift from it.
 */
fun categorizePackage(pkg: String, label: String): AppCategory =
    AppCategorizer.categorize(pkg, label)

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
                    // Issue #132: MUSIC is born PEEK_2M, like
                    // MESSAGING — the existing peek lease (and the
                    // Safe Break's PEEK pass) is the lawful way to
                    // open the player mid-session and press play;
                    // background playback was never blocked, so a
                    // 2-minute start window is all a player needs.
                    // Born LOCKED, a player got no peek at all.
                    AppCategory.MESSAGING, AppCategory.MUSIC -> AppBlockTier.PEEK_2M
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
}
