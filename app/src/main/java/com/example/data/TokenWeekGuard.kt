package com.example.data

import java.time.ZoneId

/**
 * The token week's corroboration guard (issue #523, finding 6-2).
 *
 * [ClockHonesty] cannot see across a reboot: the first read of a
 * boot takes the wall clock at face value, so a date change
 * performed while the phone was OFF moves the honest now into a
 * later week and a spent emergency token reads fresh. This guard is
 * the spend ledger's own memory of where honest time had actually
 * been corroborated to — the repository feeds it the honest now and
 * persists the state it hands back; every token read and the one
 * token write evaluate against the instant this guard returns.
 *
 * The rule, in one breath: the token never evaluates in a week that
 * SKIPS PAST the last corroborated instant (the marker) while a
 * spend stands against the marker's week. A skip is claimed the
 * first time the honest now's week anchor passes the marker's; from
 * that read the evaluation stays floored at the marker — the token
 * reads exactly as it did at the last corroborated instant — until
 * the skip is corroborated or the clock heals:
 *
 * - Corroboration is monotonic, the only kind that survives the
 *   attack: within the SAME boot that claimed the skip, once the
 *   monotonic clock has honestly lived the whole claimed interval
 *   (marker → claim), the week is believed. An attacker who sets
 *   the date ten days forward must keep the phone awake and running
 *   for ten days to mint the token — the forged span costs the real
 *   span. A reboot inside the span breaks corroboration (the
 *   off-time inside it is unaccountable) and the claim re-arms at
 *   the new boot's first read.
 * - Healing: if the clock comes back so the claimed week is no
 *   longer ahead of the marker's week, the skip dissolves and the
 *   evaluation follows the honest now again.
 * - No spend against the marker's week → nothing to protect: the
 *   marker simply follows the honest now. Travelling with an
 *   unspent token mints nothing; the token was always there.
 *
 * The marker never rewinds (a rollback is [ClockHonesty]'s floor,
 * upstream of this guard) and, once seeded from an existing spend
 * ledger, starts at the latest spend's instant — a ledger written
 * by an older build is protected from its first guarded read.
 */
object TokenWeekGuard {

    /** The persisted guard state. [markerWallMillis] is the last
     *  corroborated token instant (0 = never seeded).
     *  [pendingClaimWallMillis] is the honest instant whose week
     *  first skipped past the marker (0 = no skip pending), claimed
     *  in boot [pendingBootCount] at monotonic reading
     *  [pendingGateElapsedRealtimeMillis]. */
    data class State(
        val markerWallMillis: Long = 0L,
        val pendingClaimWallMillis: Long = 0L,
        val pendingBootCount: Int = -1,
        val pendingGateElapsedRealtimeMillis: Long = 0L
    )

    /** [effectiveNowMillis] is the instant the token machinery must
     *  evaluate at; [state] is the guard state to persist;
     *  [skipRefused] names that this read was floored at the marker
     *  (for the caller's record, never for a user surface). */
    data class Result(
        val effectiveNowMillis: Long,
        val state: State,
        val skipRefused: Boolean
    )

    fun evaluate(
        state: State,
        nowMillis: Long,
        nowBootCount: Int,
        nowElapsedRealtimeMillis: Long,
        markerWeekHasSpend: Boolean,
        latestSpendWallMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): Result {
        var marker = state.markerWallMillis
        // Seed: a ledger that already holds spends starts the marker
        // at the latest spend — that instant was corroborated by the
        // act of spending itself. No spends: the marker starts here.
        if (marker <= 0L) {
            marker = if (latestSpendWallMillis > 0L) latestSpendWallMillis else nowMillis
            if (marker == nowMillis) {
                return Result(nowMillis, State(markerWallMillis = marker), skipRefused = false)
            }
        }
        val markerWeek = MonolithWeek.weekAnchorMillis(marker, zone)

        // A skip is already pending: it governs until corroborated
        // or healed.
        if (state.pendingClaimWallMillis > 0L) {
            if (nowBootCount != state.pendingBootCount) {
                // A reboot inside the span: the off-time is
                // unaccountable, corroboration restarts. If the
                // clock healed meanwhile (its week no longer ahead
                // of the marker's), the skip simply dissolves.
                return if (MonolithWeek.weekAnchorMillis(nowMillis, zone) <= markerWeek) {
                    Result(nowMillis, State(markerWallMillis = nowMillis), skipRefused = false)
                } else {
                    Result(
                        marker,
                        State(marker, nowMillis, nowBootCount, nowElapsedRealtimeMillis),
                        skipRefused = true
                    )
                }
            }
            val livedMillis =
                nowElapsedRealtimeMillis - state.pendingGateElapsedRealtimeMillis
            val corroborated = livedMillis >= state.pendingClaimWallMillis - marker &&
                MonolithWeek.weekAnchorMillis(nowMillis, zone) ==
                MonolithWeek.weekAnchorMillis(state.pendingClaimWallMillis, zone)
            return if (corroborated) {
                Result(nowMillis, State(markerWallMillis = nowMillis), skipRefused = false)
            } else {
                Result(
                    marker,
                    state.copy(markerWallMillis = marker),
                    skipRefused = true
                )
            }
        }

        // No skip pending: a forward week jump past the marker is a
        // skip only when a spend stands against the marker's week.
        if (MonolithWeek.weekAnchorMillis(nowMillis, zone) > markerWeek) {
            return if (markerWeekHasSpend) {
                Result(
                    marker,
                    State(marker, nowMillis, nowBootCount, nowElapsedRealtimeMillis),
                    skipRefused = true
                )
            } else {
                Result(nowMillis, State(markerWallMillis = nowMillis), skipRefused = false)
            }
        }
        // Same week (or ClockHonesty's floored rollback): the marker
        // follows the honest now forward and never rewinds.
        return Result(
            nowMillis,
            State(markerWallMillis = maxOf(marker, nowMillis)),
            skipRefused = false
        )
    }
}
