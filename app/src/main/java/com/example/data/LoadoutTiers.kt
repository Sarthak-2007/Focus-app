package com.example.data

/**
 * Issue #129 F4 — THE LOADOUT DERIVATION, pure.
 *
 * Before this file the one-tap fix re-derived tiers inline in
 * the repository from the same guessed category the birth used,
 * so a mis-guessed app stayed OPEN under the very loadout whose
 * subtitle promised it blocked — while the seal counted "held"
 * apps it was never among.
 *
 * Two cures live here, both inside what a loadout already
 * promises:
 * - BROWSERS are a corridor, not a tool: under STUDY and
 *   WORK_SPRINT a browser now lands on PEEK_2M (generalising the
 *   old string special-case that only caught Chrome/YouTube by
 *   name). A 2-minute knock serves the one honest search; the
 *   open corridor does not stand for the whole session.
 * - The derivation is pure and shared, so the repository, the
 *   seal's held count and any test read ONE derivation.
 *
 * Deliberately NOT cured here (DECISION-NEEDED, priced in the
 * #129 report): an app filed SHOPPING — the "Other Apps" bucket
 * every unrecognised app falls into, Docs and calculators
 * included — still lands OPEN under STUDY/WORK_SPRINT. Failing
 * that whole bucket closed is a policy flip on apps the man
 * deliberately keeps open, and it is his call, not a fix's.
 */
object LoadoutTiers {

    fun tierFor(
        loadout: SmartLoadout,
        packageName: String,
        appName: String,
        categoryName: String
    ): AppBlockTier {
        val lower = "$packageName $appName".lowercase()
        return when (loadout) {
            SmartLoadout.STUDY -> {
                when {
                    lower.contains("youtube") || lower.contains("chrome") ||
                        categoryName == AppCategory.MESSAGING.name ->
                        AppBlockTier.PEEK_2M
                    // #129 F4: every browser, not just Chrome by
                    // name — the corridor gets a knock, not a door.
                    categoryName == AppCategory.BROWSERS.name ->
                        AppBlockTier.PEEK_2M
                    categoryName == AppCategory.SHORT_VIDEO.name ||
                        categoryName == AppCategory.SOCIAL.name ||
                        categoryName == AppCategory.GAMES.name ->
                        AppBlockTier.LOCKED
                    else -> AppBlockTier.OPEN
                }
            }
            SmartLoadout.WORK_SPRINT -> {
                when {
                    categoryName == AppCategory.SHORT_VIDEO.name ||
                        categoryName == AppCategory.GAMES.name ->
                        AppBlockTier.LOCKED
                    categoryName == AppCategory.SOCIAL.name ||
                        categoryName == AppCategory.BROWSERS.name ->
                        AppBlockTier.PEEK_2M
                    else -> AppBlockTier.OPEN
                }
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
}
