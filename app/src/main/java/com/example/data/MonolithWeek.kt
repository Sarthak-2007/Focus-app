package com.example.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/**
 * The week, defined once (TOKEN-SPEC §3). The token and any future
 * weekly surface consume this and only this: one fact, one definition.
 *
 * The week turns over Monday 00:00 local civil time in the device's
 * current timezone — the app's existing Monday-first convention.
 * Rollover needs no job, no reset, no alarm: a spend counts against
 * the current week iff the anchor stored on its row equals the anchor
 * computed at evaluation time, and at Monday 00:00 the equality simply
 * stops matching last week's rows.
 */
object MonolithWeek {

    /**
     * The instant of Monday 00:00:00.000 local time of the week
     * containing [nowMillis], in [zone]. `atStartOfDay` applies
     * java.time's DST gap/overlap rules (a nonexistent midnight shifts
     * forward to the first valid instant), so this is correct in any
     * zone the phone lands in.
     */
    fun weekAnchorMillis(nowMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
        val date = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.atStartOfDay(zone).toInstant().toEpochMilli()
    }
}

/**
 * The only remaining-derivation formula in the system (TOKEN-SPEC §2):
 * one break per week, spent rows subtract, never below zero — two
 * spends booked against one anchor (a data-corruption drill) read as
 * spent, never as a negative debt.
 */
internal fun emergencyBreaksRemainingFromSpendCount(spendCount: Int): Int =
    (1 - spendCount).coerceAtLeast(0)
