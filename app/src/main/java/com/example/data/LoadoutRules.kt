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
        categoryName: String,
        currentTier: AppBlockTier? = null
    ): AppBlockTier {
        val lower = "$packageName $appName".lowercase()
        val guessed = when (loadout) {
            SmartLoadout.STUDY -> when {
                lower.contains("youtube") || lower.contains("chrome") ||
                    categoryName == AppCategory.MESSAGING.name ->
                    AppBlockTier.PEEK_2M
                // Issue #129 F4 (ported from the earlier lane):
                // EVERY browser is a corridor, not just Chrome by
                // name — a browser lands on PEEK_2M, so the one
                // honest search gets a knock and the open corridor
                // does not stand for the whole session.
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
                if (lower.contains("dialer") || lower.contains("phone") ||
                    lower.contains("clock") || lower.contains("alarm") ||
                    lower.contains("maps")
                ) {
                    AppBlockTier.OPEN
                } else {
                    AppBlockTier.LOCKED
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
        return if (guessed == AppBlockTier.OPEN && currentTier == AppBlockTier.LOCKED) {
            AppBlockTier.LOCKED
        } else {
            guessed
        }
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
            val tier = tierFor(loadout, app.packageName, app.appName, app.category, app.resolvedTier)
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
