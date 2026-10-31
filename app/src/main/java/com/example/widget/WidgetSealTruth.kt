package com.example.widget

/**
 * Issue #155, verifier residue (2): the widget's SEALED truth rule.
 *
 * The widget's first landing printed SEALED from the session row
 * alone (`isActive`) — F2's lie reborn on the home screen: a dead
 * or blind shield (usage access revoked, loop gone) beside a live
 * row still read SEALED. This rule is the shield's own truth,
 * shared verbatim with the widget's print: a seal is PROVEN only
 * when the shield's protecting witness stands for THIS session
 * (the flip that happens only after a full monitor iteration with
 * usage access) AND the shield's persisted beat is fresh (the
 * loop stamps it every iteration). Anything else is unproven and
 * the widget must print the honest check line instead.
 *
 * Pure on purpose — no Android, no stored state — so the rule is
 * pinned by plain unit tests (WidgetSealTruthTest).
 */
object WidgetSealTruth {

    /**
     * How old the shield's persisted beat may be before the seal
     * counts as unproven. The monitor loop stamps the beat every
     * iteration (~1s cadence); 30s is generous headroom for doze
     * throttling while still catching a dead shield fast.
     */
    const val BEAT_FRESH_MILLIS: Long = 30_000L

    fun sealedProven(
        isActive: Boolean,
        protecting: Boolean,
        protectingSessionId: Int,
        sessionId: Int,
        beatWallMillis: Long,
        nowWallMillis: Long,
        accessibilityBound: Boolean
    ): Boolean {
        if (!isActive) return false
        if (!protecting) return false
        // Issue #155 (IQ-5 F-3(ii)): the accessibility door is a
        // truth input, not a detail — when it dies mid-session
        // blocking is OFF, whatever the usage witness says, and
        // the shield card already warns so. The widget must not
        // print SEALED over a dead door.
        if (!accessibilityBound) return false
        if (protectingSessionId != sessionId) return false
        if (beatWallMillis <= 0L) return false
        val beatAgeMillis = nowWallMillis - beatWallMillis
        return beatAgeMillis in 0..BEAT_FRESH_MILLIS
    }
}
