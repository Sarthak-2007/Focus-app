package com.example.data

import java.util.Locale

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

/**
 * Issue #80 finding 1 (partial): one install of a package in one
 * user profile. [profileKey] is the profile's UserHandle string —
 * the only profile identity a normal app may read; Android exposes
 * no public numeric user id, and no door (AccessibilityEvent,
 * UsageStats, notifications) carries a UserHandle at all, which is
 * why per-profile RULES stay blocked (see ISSUE-80 report) while
 * per-profile DISCOVERY is possible and implemented.
 */
data class ProfiledInstalledPackage(
    val packageName: String,
    val label: String,
    val profileKey: String
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

    /** Deterministic homes (issue #133): a filing that is a fact
     *  about the package, not a guess about its name — checked
     *  before every token rule below. */
    private val DETERMINISTIC_HOMES: Map<String, AppCategory> = mapOf(
        // Anki — the spaced-repetition app (audit R3b, angle
        // study-on-phone-itself, names this exact package).
        "com.ichi2.anki" to AppCategory.EDUCATION,
        // Finance (issue #120): the payment/authenticator packages
        // whose identity is a fact, filed deterministically like
        // Anki above — Google Pay (India), PhonePe, Paytm, BHIM,
        // and the three authenticators.
        "com.google.android.apps.nbu.paisa.user" to AppCategory.FINANCE,
        "com.phonepe.app" to AppCategory.FINANCE,
        "net.one97.paytm" to AppCategory.FINANCE,
        "in.org.npci.upiapp" to AppCategory.FINANCE,
        "com.google.android.apps.authenticator2" to AppCategory.FINANCE,
        "com.microsoft.authenticator" to AppCategory.FINANCE,
        "com.authy.authy" to AppCategory.FINANCE
    )

    private val MUSIC_TOKENS = setOf(
        "music", "podcast", "podcasts", "audio", "audiobook", "audiobooks",
        "radio", "spotify", "soundcloud", "deezer", "tidal", "gaana",
        "wynk", "shazam", "bandcamp", "audius", "audible", "tunein",
        "castbox", "audiomack", "pocketcasts"
    )

    fun categorize(pkg: String, label: String): AppCategory {
        DETERMINISTIC_HOMES[pkg]?.let { return it }
        val pkgTokens = tokenize(pkg)
        val labelTokens = tokenize(label)
        fun matches(vararg keys: String): Boolean = keys.any { key ->
            val keyTokens = key.split(' ')
            containsTokenSequence(pkgTokens, keyTokens) ||
                containsTokenSequence(labelTokens, keyTokens)
        }
        // MUSIC FIRST, by token (the header above): a media
        // player's identity beats every trap below it.
        if ((pkgTokens + labelTokens).any { it in MUSIC_TOKENS }) {
            return AppCategory.MUSIC
        }
        // The remaining branches carry fresh-eyes L2's whole-token
        // discipline (merged in by STOSS-34 at the Herz/surge
        // reconciliation): the bare substring "play" is GONE — it
        // filed the Play Store and "Display…" as GAMES — and the
        // bare "reel" no longer fires inside "Freelance". Plurals
        // are stated as their own keys; "play" survives only in
        // "play games", where it means what it says.
        // Study signals (issue #133) sit before the entertainment
        // branches, in the same whole-token discipline: a study
        // app must never be filed as entertainment by name luck —
        // Google Play Books ("play books") is a study app, while
        // Play Store and Play Games keep their rulings below.
        if (matches(
                "anki", "flashcard", "flashcards", "classroom",
                "play books", "textbook", "textbooks", "ebook", "ebooks",
                "pdf reader", "lecture", "lectures", "dictionary",
                "education", "study"
            )
        ) return AppCategory.EDUCATION
        // Finance (issue #120), in the same whole-token discipline,
        // filed before the entertainment branches so a bank's name
        // can never file it as a game or a shop. Bare "pay" is NOT
        // a key — it fires inside too many unrelated names; the
        // payment brands are stated whole.
        if (matches(
                "bank", "banking", "upi", "authenticator", "authy",
                "gpay", "phonepe", "paytm", "bhim", "yono", "paypal",
                "wallet", "google pay", "mobile banking"
            )
        ) return AppCategory.FINANCE
        return when {
            matches("tiktok", "musically", "youtube", "instagram", "reel", "reels") ->
                AppCategory.SHORT_VIDEO
            matches("twitter", "reddit", "snapchat", "facebook") ->
                AppCategory.SOCIAL
            matches(
                "game", "games", "clash", "roblox", "minecraft",
                "pubg", "candycrush", "play games"
            ) -> AppCategory.GAMES
            matches(
                "whatsapp", "discord", "telegram", "message",
                "messages", "messaging", "messenger"
            ) -> AppCategory.MESSAGING
            // W3 #130 (re-expressed in this classifier's token
            // discipline on the current-main composition): Edge
            // (emmx), Opera, DuckDuckGo, Kiwi, Via ("mark via" —
            // the mark.via.gp package tokenised) and Yandex join
            // the recognised browsers — an unrecognised browser
            // fell through to SHOPPING, invisible as a web door in
            // the Shield list. (#130's original cure edited the
            // repository's inline copy of this `when`; that copy is
            // gone — the repository delegates here, so here is
            // where the recognition lives.)
            matches(
                "chrome", "browser", "browsers", "firefox", "brave",
                "opera", "duckduckgo", "kiwibrowser", "mark via",
                "yandex", "emmx"
            ) ->
                AppCategory.BROWSERS
            else -> AppCategory.SHOPPING
        }
    }

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
            tokens += chunk.lowercase(Locale.ROOT)
            val parts = chunk.split(
                Regex(
                    "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])" +
                        "|(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])"
                )
            )
            if (parts.size > 1) {
                parts.forEach { tokens += it.lowercase(Locale.ROOT) }
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
                    compareBy({ it.label.lowercase(Locale.ROOT) }, { it.label })
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

    /** Catalog view of profiled installs: one row per PACKAGE
     *  (the table's primary key is still the bare package name),
     *  first label seen wins. A package present in ANY profile is
     *  installed for catalog purposes — it must not be pruned just
     *  because the calling profile cannot launch it. */
    fun mergeProfiledInstalls(
        profiled: List<ProfiledInstalledPackage>
    ): List<InstalledPackage> =
        profiled.distinctBy { it.packageName }
            .map { InstalledPackage(it.packageName, it.label) }

    /** Which profiles each package is present in — the conflation
     *  made visible: more than one key means one rule row is
     *  standing for several installs. */
    fun profilesByPackage(
        profiled: List<ProfiledInstalledPackage>
    ): Map<String, List<String>> =
        profiled.groupBy({ it.packageName }, { it.profileKey })
            .mapValues { it.value.distinct().sorted() }

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
                    // Issue #120: FINANCE is born PEEK_2M too —
                    // a bank or authenticator born LOCKED has no
                    // lawful mid-session door at all, and the peek
                    // (5s wall, 2-minute lease) is exactly the
                    // ceremony an OTP or UPI approval needs.
                    AppCategory.MESSAGING, AppCategory.MUSIC, AppCategory.FINANCE -> AppBlockTier.PEEK_2M
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
     *  a sloppy ruling. L2's whole-token discipline — the bare
     *  "play" that made the Play Store a GAME is gone, "reel"
     *  no longer fires inside "Freelance" — now lives in the ONE
     *  classifier, [AppCategorizer] (issue #132's extraction),
     *  which also carries the MUSIC-first token branch. This
     *  seam delegates: there is no second implementation to
     *  drift (STOSS-34, Herz/surge merge reconciliation). */
    fun categorize(pkg: String, label: String): AppCategory =
        AppCategorizer.categorize(pkg, label)

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
}
