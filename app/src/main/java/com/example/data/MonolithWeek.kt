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
 * the current week iff [spendCountsAgainstWeek] says so, and at
 * Monday 00:00 last week's rows simply stop counting.
 *
 * Issue #39 F1 (the timezone hole in the old rule): matching used to
 * be exact anchor EQUALITY, but the stored anchor was minted under
 * the zone the phone was in AT SPEND TIME while the evaluation anchor
 * is recomputed under the AMBIENT zone — a timezone change moved the
 * recomputed anchor off the stored one, the stored spend stopped
 * matching, and the weekly cap silently reset (an extra emergency
 * break: a HOLD leak). Stored rows are correct instants; only the
 * matching was broken. The cure is the two-arm predicate below,
 * implemented in SQL by the week's two Dao queries: a spend counts
 * iff its stored anchor lies within the ruled maximum zone delta
 * (26h) of the current anchor — the same Monday seen from another
 * zone — OR the spend instant itself falls inside the current local
 * week. Both arms fail closed, and adjacent weeks sit ~7 days away
 * on both arms, so neither arm can pull a neighbouring week's spend
 * in. No schema change: every stored row keeps its meaning.
 */
object MonolithWeek {

    /**
     * The ruled maximum Monday-to-Monday delta between two zones
     * (TOKEN-SPEC §3): the widest real offset pair (+14 / −12) moves
     * the same local Monday by at most 26 hours as an instant. The
     * Dao queries carry this number as a literal (Room annotations
     * are compile-time constants); the two must never drift.
     */
    const val WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS: Long = 93_600_000L

    /**
     * Whether [spend] counts against the week whose anchor is
     * [currentAnchorMillis] (next week's anchor
     * [nextWeekAnchorMillis], calendar-derived). Two arms, either is
     * sufficient, both fail closed:
     *
     * (a) the spend's STORED anchor is within
     * [WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS] of the current anchor —
     * the same Monday minted under a different zone; or
     * (b) the spend INSTANT lies inside the current local week,
     * [currentAnchorMillis, nextWeekAnchorMillis) — the traveller
     * whose local week genuinely contains the moment they spent,
     * even when the stored anchor belongs to a week-line the new
     * zone drew almost a week away from its own.
     */
    fun spendCountsAgainstWeek(
        spend: EmergencyBreakSpendEntity,
        currentAnchorMillis: Long,
        nextWeekAnchorMillis: Long
    ): Boolean = spendCountsAgainstWeek(
        spendAnchorMillis = spend.weekAnchorMillis,
        spentAtWallMillis = spend.spentAtWallMillis,
        currentAnchorMillis = currentAnchorMillis,
        nextWeekAnchorMillis = nextWeekAnchorMillis
    )

    /** The primitive form of [spendCountsAgainstWeek] — the exact
     *  condition the Dao queries express in SQL. */
    fun spendCountsAgainstWeek(
        spendAnchorMillis: Long,
        spentAtWallMillis: Long,
        currentAnchorMillis: Long,
        nextWeekAnchorMillis: Long
    ): Boolean =
        (spendAnchorMillis >= currentAnchorMillis - WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS &&
            spendAnchorMillis <= currentAnchorMillis + WEEK_ANCHOR_ZONE_TOLERANCE_MILLIS) ||
            (spentAtWallMillis >= currentAnchorMillis && spentAtWallMillis < nextWeekAnchorMillis)

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

    /**
     * The anchor of the NEXT week: Monday 00:00 local, seven days
     * after this week's anchor (issue #119, finding 1). This is the
     * instant a spent emergency-break token returns — the SPENT face
     * of the valve prints it, so the state the user is in and when
     * it ends are both stated, never hidden.
     */
    fun nextWeekAnchorMillis(nowMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
        val date = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val nextMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(1)
        return nextMonday.atStartOfDay(zone).toInstant().toEpochMilli()
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
