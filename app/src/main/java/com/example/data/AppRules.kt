package com.example.data

/**
 * HIS DECISIONS, GROUPED (Ordner lane; Wave-3 issue #35 cure) — the
 * derivation under the Shield standing-orders slip: the apps he has
 * set, split by ruling.
 *
 * ORDER (the real one): alphabetical by display name
 * (case-insensitive) inside each group, ties broken by packageName.
 * Package names are unique after de-duplication, so this is a TOTAL
 * order: the same input always prints in the same order, whatever
 * order the catalogue feed (MonolithDao: intercept-count order)
 * arrived in. It is NOT claimed to be the catalogue's order — the
 * catalogue sorts by intercept count, this ledger sorts by name.
 *
 * CLASSIFICATION IS TOTAL: every de-duplicated input app lands in
 * exactly one named bucket — [peek], [shut], [open] or [corrupt] —
 * so peekCount + shutCount + openCount + corruptCount always equals
 * the number of distinct packages in the input. Nothing is dropped
 * silently. OPEN apps carry no ruling and the slip does not print
 * them as lines, but they are counted here, never vanished.
 *
 * CORRUPT TIERS ARE NOT DECISIONS: a row whose stored blockTier
 * string does not parse as an [AppBlockTier] lands in [corrupt],
 * never in peek/shut via resolvedTier's isBlocked fallback — a
 * fallback is a guess about damaged data, not a ruling he made.
 * (resolvedTier itself, in MonolithModels.kt, still launders
 * corrupt strings for its other consumers; that cure belongs to
 * issue #32 — see the Wave-3 #35 report's cross-issue evidence.)
 *
 * DE-DUPLICATION: inputs are de-duplicated by packageName before
 * classification. If stale copies of one package disagree, the
 * STRICTEST valid ruling wins (LOCKED > PEEK_2M > OPEN; a corrupt
 * copy loses to any valid copy) — a blocker fails closed, and the
 * package still appears in exactly one bucket.
 *
 * COUNTS: the counts are the bucket list sizes themselves, and the
 * one production consumer (ShieldMatrixScreen) prints THESE counts,
 * so a printed count cannot disagree with the lines under it.
 */
data class AppRules(
    val peek: List<BlockedAppEntity>,
    val shut: List<BlockedAppEntity>,
    val open: List<BlockedAppEntity> = emptyList(),
    val corrupt: List<BlockedAppEntity> = emptyList()
) {
    init {
        // Disjoint by construction: no package may stand in two
        // buckets at once, whoever builds this value.
        val seen = HashSet<String>()
        for (app in peek + shut + open + corrupt) {
            require(seen.add(app.packageName)) {
                "AppRules buckets must be disjoint by package: ${app.packageName}"
            }
        }
    }

    val peekCount: Int get() = peek.size
    val shutCount: Int get() = shut.size
    val openCount: Int get() = open.size
    val corruptCount: Int get() = corrupt.size

    /** Every de-duplicated input app, accounted for exactly once. */
    val accountedCount: Int get() = peekCount + shutCount + openCount + corruptCount

    /** No rulings to print and nothing corrupt awaiting attention.
     *  OPEN-only input is empty in this sense: he made no decision. */
    val isEmpty: Boolean get() = peek.isEmpty() && shut.isEmpty() && corrupt.isEmpty()
}

/** The ledger's total order: name (case-insensitive), then the
 *  unique package name — no tie can inherit feed order. */
private val ledgerOrder: Comparator<BlockedAppEntity> =
    compareBy<BlockedAppEntity> { it.appName.lowercase() }.thenBy { it.packageName }

/** Strictness rank for de-duplication: higher wins. A corrupt
 *  (unparseable) tier ranks below every real ruling. */
private fun strictness(tier: AppBlockTier?): Int = when (tier) {
    AppBlockTier.LOCKED -> 3
    AppBlockTier.PEEK_2M -> 2
    AppBlockTier.OPEN -> 1
    null -> 0
}

/** The stored tier exactly as written, or null when the stored
 *  string is corrupt — deliberately NOT resolvedTier, whose
 *  fallback would launder corruption into a fake decision. */
private fun BlockedAppEntity.storedTierOrNull(): AppBlockTier? =
    runCatching { AppBlockTier.valueOf(blockTier) }.getOrNull()

fun List<BlockedAppEntity>.groupedByRule(): AppRules {
    // De-dupe by package: among stale copies the strictest valid
    // ruling wins; ties keep the first copy in input order.
    val deduped = groupBy { it.packageName }.values.map { copies ->
        copies.maxByOrNull { strictness(it.storedTierOrNull()) } ?: copies.first()
    }
    val peek = mutableListOf<BlockedAppEntity>()
    val shut = mutableListOf<BlockedAppEntity>()
    val open = mutableListOf<BlockedAppEntity>()
    val corrupt = mutableListOf<BlockedAppEntity>()
    for (app in deduped) {
        when (app.storedTierOrNull()) {
            AppBlockTier.PEEK_2M -> peek += app
            AppBlockTier.LOCKED -> shut += app
            AppBlockTier.OPEN -> open += app
            null -> corrupt += app
        }
    }
    return AppRules(
        peek = peek.sortedWith(ledgerOrder),
        shut = shut.sortedWith(ledgerOrder),
        open = open.sortedWith(ledgerOrder),
        corrupt = corrupt.sortedWith(ledgerOrder)
    )
}
