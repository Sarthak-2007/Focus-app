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
