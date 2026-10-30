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
        return when (loadout) {
            SmartLoadout.STUDY -> when {
                lower.contains("youtube") || lower.contains("chrome") ||
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
