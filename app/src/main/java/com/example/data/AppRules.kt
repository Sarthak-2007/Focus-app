package com.example.data

/**
 * HIS DECISIONS, GROUPED (Ordner lane) — the derivation under the
 * Shield standing-orders slip: the apps he has set, split by ruling,
 * alphabetical inside each group (a ledger's order, identical to the
 * catalogue's, so a name lives at the same height in both). OPEN apps
 * carry no ruling and never appear here; the counts are the list
 * sizes themselves, so a printed count cannot disagree with the
 * lines under it.
 */
data class AppRules(
    val peek: List<BlockedAppEntity>,
    val shut: List<BlockedAppEntity>
) {
    val peekCount: Int get() = peek.size
    val shutCount: Int get() = shut.size
    val isEmpty: Boolean get() = peek.isEmpty() && shut.isEmpty()
}

fun List<BlockedAppEntity>.groupedByRule(): AppRules = AppRules(
    peek = filter { it.resolvedTier == AppBlockTier.PEEK_2M }
        .sortedBy { it.appName.lowercase() },
    shut = filter { it.resolvedTier == AppBlockTier.LOCKED }
        .sortedBy { it.appName.lowercase() }
)

/**
 * THE CATALOGUE'S GROUPS (Shield audit R-1; issue #7 builder pass).
 * The catalogue is the ONE enumeration of his rulings on Shield:
 * the Standing Orders slip's per-app lines are retired, and the
 * catalogue itself groups by ruling so a name appears exactly
 * once on the screen. Group order is the slip's order — PEEK
 * before SHUT (the holes are the urgent read), OPEN last — and
 * alphabetical inside each group, the ledger's order. A ruling
 * with no apps in this list contributes no group: its absence is
 * printed once, by the tally's zero above the catalogue, never by
 * an empty section here.
 */
fun List<BlockedAppEntity>.groupedForCatalogue():
    List<Pair<AppBlockTier, List<BlockedAppEntity>>> =
    listOf(AppBlockTier.PEEK_2M, AppBlockTier.LOCKED, AppBlockTier.OPEN)
        .map { tier ->
            tier to filter { it.resolvedTier == tier }
                .sortedBy { it.appName.lowercase() }
        }
        .filter { it.second.isNotEmpty() }
