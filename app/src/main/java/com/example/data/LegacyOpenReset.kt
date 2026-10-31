package com.example.data

/**
 * Ren ruling 00:34 IST Oct 31 (#7, via KLAR-BAU's final supplement):
 * the LEGACY OPEN reset — the pure half.
 *
 * The Shield inversion (PR #388, Shield Rethink 2 Step 2) makes new
 * catalogue rows born SHUT, but it cannot reach backwards: every row
 * already standing OPEN on his phone — born OPEN under the old
 * birth default, or opened by his own hand — is byte-identical
 * under v12, and shipped code treats all of them as his ruling.
 * The ruling therefore closes them ALL, once, at first run after
 * the inversion lands, and announces the count on the Sheet where
 * he can re-open any of them by hand. This object is only the
 * selection: which rows the one-time reset may touch. The commit,
 * the persisted done-flag and the announcement record live in
 * MonolithRepository; the strip itself lives on the Sheet.
 *
 * Never selected, whatever their stored tier:
 *  - this app itself (a blocker that locks its own row has made
 *    itself unmanageable, not safer);
 *  - safety-floor packages (SafetyFloor: dialler / telephony,
 *    keyboards, emergency surfaces — the doors never intercept
 *    them, and a LOCKED row for a keyboard would be a lie that
 *    can still bite through other readers);
 *  - anything the dialler floor would raise out of LOCKED
 *    (EssentialApps.floorTier — one definition, reused).
 * PEEK and LOCKED rows are not the reset's business: it closes
 * exactly the OPEN remainder, nothing else.
 */
object LegacyOpenReset {

    fun packagesToReset(
        apps: List<BlockedAppEntity>,
        ownPackage: String
    ): List<String> =
        apps.asSequence()
            .filter { it.resolvedTier == AppBlockTier.OPEN }
            .map { it.packageName }
            .filter { it != ownPackage }
            .filter { !SafetyFloor.isFloorPackage(it) }
            .filter {
                EssentialApps.floorTier(it, AppBlockTier.LOCKED) == AppBlockTier.LOCKED
            }
            .distinct()
            .toList()

    /** The Sheet strip's words, in the strip grammar the prune
     *  note and the review prompt already print in (MainActivity
     *  composes those the same way). The ruling's sentence, with
     *  the live count: "N apps were reset to SHUT — reopen any of
     *  them here." Singular handled: one app is not "apps". */
    fun announcementLine(count: Int): String =
        if (count == 1) {
            "1 APP WAS RESET TO SHUT — REOPEN IT HERE"
        } else {
            "$count APPS WERE RESET TO SHUT — REOPEN ANY OF THEM HERE"
        }
}
