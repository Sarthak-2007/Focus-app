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
 * ends the sync holding EXACTLY the installed set:
 *
 * - a stored row whose package is gone is PRUNED (the one rule for
 *   stored rows — never kept dormant, so no reader of this table can
 *   ever count a ghost: not the Shield list, not the Deck's held
 *   count, not PARK's census);
 * - an installed package with no row is DISCOVERED, uncapped, born
 *   with its factory preset if it has one, else its category default;
 * - an installed package with a row keeps its row untouched — the
 *   man's own tier is never re-priced by a sync — except its usage
 *   figure, which follows the real one.
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
    val label: String
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
    val usageUpdates: List<BlockedAppEntity>,
    val toPrune: List<String>
)

/**
 * The one package classifier (Issue #132, music-during-focus).
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
            lower.contains("tiktok") || lower.contains("youtube") || lower.contains("instagram") || lower.contains("reel") ->
                AppCategory.SHORT_VIDEO
            lower.contains("twitter") || lower.contains("reddit") || lower.contains("snapchat") || lower.contains("facebook") ->
                AppCategory.SOCIAL
            lower.contains("game") || lower.contains("clash") || lower.contains("roblox") || lower.contains("play") ->
                AppCategory.GAMES
            lower.contains("whatsapp") || lower.contains("discord") || lower.contains("telegram") || lower.contains("message") ->
                AppCategory.MESSAGING
            lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") || lower.contains("brave") ->
                AppCategory.BROWSERS
            else -> AppCategory.SHOPPING
        }
    }
}

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

    fun reconcile(
        existing: List<BlockedAppEntity>,
        installed: List<InstalledPackage>,
        usageMinutes: Map<String, Int>,
        categorize: (String, String) -> AppCategory
    ): AppCatalogPlan {
        // A package with several launcher activities is still one app.
        val installedByPackage = installed.associateBy { it.packageName }
        val existingByPackage = existing.associateBy { it.packageName }

        val toPrune = existing
            .filter { it.packageName !in installedByPackage }
            .map { it.packageName }

        val toInsert = mutableListOf<BlockedAppEntity>()
        val usageUpdates = mutableListOf<BlockedAppEntity>()
        for ((pkg, info) in installedByPackage) {
            val current = existingByPackage[pkg]
            if (current == null) {
                val category = categorize(pkg, info.label)
                val preset = FACTORY_PRESETS[pkg]
                val tier = preset?.tier ?: when (category) {
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
                    dailyLimitMinutes = preset?.dailyLimitMinutes ?: 30,
                    todayUsageMinutes = usageMinutes[pkg] ?: 0,
                    interceptCount = 0,
                    isSystemDetected = true
                )
            } else {
                val realMinutes = usageMinutes[pkg]
                if (realMinutes != null && realMinutes > 0 &&
                    realMinutes != current.todayUsageMinutes
                ) {
                    usageUpdates += current.copy(todayUsageMinutes = realMinutes)
                }
            }
        }
        return AppCatalogPlan(toInsert, usageUpdates, toPrune)
    }
}
