package com.example.data

import java.util.Locale

/**
 * Issue #107 (Audit R3, shield-setup-lived #1): the ONE tier rule a
 * SmartLoadout applies, as a pure function — shared by the commit
 * path (MonolithRepository.applySmartLoadout) and by the Shield
 * screen's pre-apply preview, so the numbers the preview prints are
 * computed by the exact code that will commit them. A preview that
 * re-implements the rule in the UI can drift from the commit; this
 * one cannot.
 */
object LoadoutRules {

    fun tierFor(
        loadout: SmartLoadout,
        packageName: String,
        appName: String,
        categoryName: String,
        currentTier: AppBlockTier? = null,
        // Issue #121: packages structurally classified time-critical
        // (alarm / calendar class — TimeCriticalApps, resolved by the
        // caller from the PackageManager). SLEEP only: a member of
        // the class stays OPEN whatever its branding. Callers that
        // cannot resolve the classification pass the default empty
        // set and get exactly the pre-#121 rule.
        timeCriticalPackages: Set<String> = emptySet()
    ): AppBlockTier {
        val lower = "$packageName $appName".lowercase(Locale.ROOT)
        val rawTier = when (loadout) {
            SmartLoadout.STUDY -> when {
                // Issue #133 (study-on-phone-itself, finding 1), as
                // reconciled with #129 F4 (Review ruling on #129,
                // filed on #166 at 6543839617): the class-carrying
                // browsers BY NAME — Chrome (the class host) and
                // YouTube (lectures) — are OPEN in the loadout built
                // for them; the old rule rationed them to 2m Peek,
                // unusable for a 50-minute lecture. #133's original
                // category-wide claim (every BROWSERS app OPEN) is
                // narrowed by the ruling: a browser that is not one
                // of the named class carriers falls through to the
                // F4 corridor below and lands on PEEK_2M. YouTube's
                // guessed category is SHORT_VIDEO, so this check
                // comes before the category locks; the Shorts/Reels
                // sniper (armed separately by the STUDY pick) stays
                // the guard on the short-form feed inside YouTube.
                lower.contains("youtube") || lower.contains("chrome") ->
                    AppBlockTier.OPEN
                categoryName == AppCategory.EDUCATION.name ->
                    AppBlockTier.OPEN
                categoryName == AppCategory.MESSAGING.name ->
                    AppBlockTier.PEEK_2M
                // Issue #129 F4 (ported from the earlier lane):
                // every browser NOT claimed by name above is a
                // corridor — it lands on PEEK_2M, so the one
                // honest search gets a knock and the open corridor
                // does not stand for the whole session. (This
                // branch was unreachable while the #133 branch
                // claimed the whole BROWSERS category ahead of it;
                // the reconciliation ruling restored it.)
                categoryName == AppCategory.BROWSERS.name ->
                    AppBlockTier.PEEK_2M
                categoryName == AppCategory.SHORT_VIDEO.name ||
                    categoryName == AppCategory.SOCIAL.name ||
                    categoryName == AppCategory.GAMES.name ->
                    AppBlockTier.LOCKED
                else -> AppBlockTier.OPEN
            }
            SmartLoadout.WORK_SPRINT -> when {
                categoryName == AppCategory.SHORT_VIDEO.name ||
                    categoryName == AppCategory.GAMES.name ->
                    AppBlockTier.LOCKED
                categoryName == AppCategory.SOCIAL.name ||
                    categoryName == AppCategory.BROWSERS.name ->
                    AppBlockTier.PEEK_2M
                else -> AppBlockTier.OPEN
            }
            SmartLoadout.SLEEP -> {
                // Issue #121: structurally time-critical packages
                // (alarm / calendar class by intent identity or exact
                // package identity — TimeCriticalApps) stay OPEN: an
                // alarm app branded without 'clock'/'alarm' and the
                // calendar were both LOCKED all night by the name
                // rules alone. This rides the same #129 no-loosen
                // clamp and sealed-session gate as every other OPEN
                // below — it is an exemption of the loadout's own
                // derivation, not a loosening back door.
                if (packageName in timeCriticalPackages) {
                    AppBlockTier.OPEN
                } else {
                    // Issue #119, finding 4: essentials by PACKAGE, not
                    // substring luck — a stock Camera (com.android.camera2)
                    // and Waze (com.waze) contain none of the old five
                    // substrings and were LOCKED all night. EssentialApps
                    // keeps the substring test only as a superset fallback.
                    EssentialApps.sleepLoadoutTierFor(packageName, appName)
                }
            }
        }
        // Issue #129 (unlisted-distraction, HIGH): a loadout NEVER
        // loosens LOCKED -> OPEN on a category guess. The guessed
        // category is exactly what mis-files an unlisted app;
        // before this law, STUDY/WORK_SPRINT re-derived
        // `else -> OPEN` from that same guess, so the one-tap fix
        // silently re-opened the very app the fail-closed birth
        // had shut, while the seal's held count excluded it. A
        // loadout may tighten or keep a tier; opening a shut app
        // is HIS hand on the catalogue sled, never a guess.
        val tier = if (rawTier == AppBlockTier.OPEN && currentTier == AppBlockTier.LOCKED) {
            AppBlockTier.LOCKED
        } else {
            rawTier
        }
        // Issue #119, finding 5: the dialler floor binds every
        // loadout — the dialler can never be classified LOCKED.
        return EssentialApps.floorTier(packageName, tier)
    }

    /** Issue #304 F1: would applying [loadout] loosen ANY app's
     *  current tier? The hand loadout tap uses this to surface the
     *  one-way ratchet honestly instead of silently clamping. */
    fun anyLoosening(
        loadout: SmartLoadout,
        apps: List<BlockedAppEntity>,
        timeCriticalPackages: Set<String> = emptySet()
    ): Boolean =
        apps.any { app ->
            SessionTierGate.loosens(
                app.resolvedTier,
                tierFor(loadout, app.packageName, app.appName, app.category, timeCriticalPackages = timeCriticalPackages)
            )
        }

    /** How many of [apps] would change tier if [loadout] applied,
     *  and the SHUT / PEEK / OPEN counts the phone would land on. */
    data class Preview(val changed: Int, val shut: Int, val peek: Int, val open: Int)

    fun preview(
        loadout: SmartLoadout,
        apps: List<BlockedAppEntity>,
        timeCriticalPackages: Set<String> = emptySet()
    ): Preview {
        var changed = 0
        var shut = 0
        var peek = 0
        var open = 0
        for (app in apps) {
            val tier = tierFor(loadout, app.packageName, app.appName, app.category, app.resolvedTier, timeCriticalPackages)
            if (tier != app.resolvedTier) changed++
            when (tier) {
                AppBlockTier.LOCKED -> shut++
                AppBlockTier.PEEK_2M -> peek++
                AppBlockTier.OPEN -> open++
            }
        }
        return Preview(changed, shut, peek, open)
    }
}

/**
 * Issue #304 F1 (Audit R5, AppBlock head-to-head): the one-way
 * ratchet as a pure, repository-level direction gate.
 *
 * AppBlock's Strict Mode makes edits tightening-only while it is
 * active ("Changes can only tighten the rules"). Monolith already
 * ratcheted the hand tier taps and site removals in the ViewModel
 * (cycleAppBlockTier / setAppBlockTier / removeBlockedSite there),
 * but the rule-WRITING layer itself knew no direction: a mid-session
 * Smart Loadout tap rewrote every app's tier from the preset —
 * loosening LOCKED apps to OPEN in one tap — and any future caller
 * of the repository verbs would have bypassed the ViewModel's
 * ratchet entirely. The invariant now lives at the write boundary:
 * while a session is sealed, NO repository tier/site write may move
 * protection toward OPEN. Tightening is always lawful.
 */
object SessionTierGate {

    /** Strictness rank: LOCKED seals hardest, OPEN not at all. */
    fun strictness(tier: AppBlockTier): Int = when (tier) {
        AppBlockTier.OPEN -> 0
        AppBlockTier.PEEK_2M -> 1
        AppBlockTier.LOCKED -> 2
    }

    /** True when moving [from] -> [to] weakens protection. */
    fun loosens(from: AppBlockTier, to: AppBlockTier): Boolean =
        strictness(to) < strictness(from)

    /**
     * The tier a write may land on: while [sealed], a loosening
     * desire is clamped to the current tier (the write becomes a
     * tightening-only no-op for that app); unsealed, [desired]
     * always lands.
     */
    fun clampWhileSealed(
        current: AppBlockTier,
        desired: AppBlockTier,
        sealed: Boolean
    ): AppBlockTier = if (sealed && loosens(current, desired)) current else desired
}
