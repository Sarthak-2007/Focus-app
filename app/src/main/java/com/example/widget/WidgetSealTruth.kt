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
 * IQ-1 unified latch (WING-F): there is NO accessibility input
 * here any more. The shield's flip requires BOTH doors (usage
 * access AND the accessibility door armed for this session) and
 * its demote fires on EITHER door closing, so the protecting
 * witness itself implies the accessibility door — a second
 * derivation in a second reader (the provider fork, and the
 * F-3(ii) parameter that fed it) is removed, not re-homed.
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

    /**
     * Issue #155 (IQ-4 expiry): the wall instant at which a SEALED
     * render stops being provable and must re-render — the beat's
     * freshness boundary plus a small delivery slack. A SEALED
     * snapshot arms its own expiry wakeup for this instant (the
     * provider's one-shot alarm), so a claim can never outlive its
     * truth on screen just because no other trigger fired.
     */
    fun expiryWakeMillis(beatWallMillis: Long): Long =
        beatWallMillis + BEAT_FRESH_MILLIS + 1_000L

    fun sealedProven(
        isActive: Boolean,
        protecting: Boolean,
        protectingSessionId: Int,
        sessionId: Int,
        beatWallMillis: Long,
        nowWallMillis: Long,
        shieldGapSeen: Boolean = false
    ): Boolean {
        if (!isActive) return false
        if (!protecting) return false
        // Issue #155 (CRITIC-3 Charge C / LEAD-4 A8): the row's own
        // blindness witness is a truth input. Once the gap latch
        // has seen the shield blind (usage revoked, a critical
        // component disabled), the session can never be certified
        // clean — and the widget must not print SEALED over a
        // latched gap, however fresh the beat looks. The beat was
        // stamped every iteration regardless of the usage check,
        // so beat freshness alone never disproved blindness.
        if (shieldGapSeen) return false
        if (protectingSessionId != sessionId) return false
        if (beatWallMillis <= 0L) return false
        val beatAgeMillis = nowWallMillis - beatWallMillis
        return beatAgeMillis in 0..BEAT_FRESH_MILLIS
    }
}
