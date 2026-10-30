package com.example.data

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
        categoryName: String
    ): AppBlockTier {
        val lower = "$packageName $appName".lowercase()
        val rawTier = when (loadout) {
            SmartLoadout.STUDY -> when {
                // Issue #133 (study-on-phone-itself, finding 1): the
                // study surfaces themselves — browsers (Chrome-hosted
                // classes, PDFs, Classroom) and YouTube lectures —
                // are OPEN in the loadout built for them; the old
                // rule rationed them to 2m Peek, unusable for a
                // 50-minute lecture. YouTube's guessed category is
                // SHORT_VIDEO, so this check comes before the
                // category locks; the Shorts/Reels sniper (armed
                // separately by the STUDY pick) stays the guard on
                // the short-form feed inside YouTube.
                categoryName == AppCategory.BROWSERS.name ||
                    lower.contains("youtube") || lower.contains("chrome") ->
                    AppBlockTier.OPEN
                categoryName == AppCategory.EDUCATION.name ->
                    AppBlockTier.OPEN
                categoryName == AppCategory.MESSAGING.name ->
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
                categoryName == AppCategory.SOCIAL.name ->
                    AppBlockTier.PEEK_2M
                else -> AppBlockTier.OPEN
            }
            SmartLoadout.SLEEP -> {
                // Issue #119, finding 4: essentials by PACKAGE, not
                // substring luck — a stock Camera (com.android.camera2)
                // and Waze (com.waze) contain none of the old five
                // substrings and were LOCKED all night. EssentialApps
                // keeps the substring test only as a superset fallback.
                EssentialApps.sleepLoadoutTierFor(packageName, appName)
            }
        }
        // Issue #119, finding 5: the dialler floor binds every
        // loadout — the dialler can never be classified LOCKED.
        return EssentialApps.floorTier(packageName, rawTier)
    }

    /** Issue #304 F1: would applying [loadout] loosen ANY app's
     *  current tier? The hand loadout tap uses this to surface the
     *  one-way ratchet honestly instead of silently clamping. */
    fun anyLoosening(loadout: SmartLoadout, apps: List<BlockedAppEntity>): Boolean =
        apps.any { app ->
            SessionTierGate.loosens(
                app.resolvedTier,
                tierFor(loadout, app.packageName, app.appName, app.category)
            )
        }

    /** How many of [apps] would change tier if [loadout] applied,
     *  and the SHUT / PEEK / OPEN counts the phone would land on. */
    data class Preview(val changed: Int, val shut: Int, val peek: Int, val open: Int)

    fun preview(loadout: SmartLoadout, apps: List<BlockedAppEntity>): Preview {
        var changed = 0
        var shut = 0
        var peek = 0
        var open = 0
        for (app in apps) {
            val tier = tierFor(loadout, app.packageName, app.appName, app.category)
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
