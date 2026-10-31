package com.example.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * The Drawer's one day rule: his day starts at 03:30 (he wakes at
 * 03:30), not at midnight. Every Drawer derivation of "which day /
 * which month" — the repository months index, the repository month
 * bounds, the screen's grouping keys — goes through here, and ONLY
 * here. A SQL twin of this rule used to compute the months index
 * by subtracting 210 elapsed minutes inside SQLite; the two were
 * different functions on DST days (Audit R1 #37, verified at the
 * 2026-11-01 America/New_York fall-back boundary), so the twin is
 * dead: the DAO returns raw instants and this object files them.
 * Without the rule, a task finished at 00:02 files under the next
 * day AND the next month, and a phantom month appears in the index.
 *
 * Row finish times are NEVER shifted: they print true clock. Only
 * the filing boundary moves.
 */
object DrawerDay {

    /** 03:30 = 210 minutes after midnight. */
    const val DAY_START_OFFSET_MINUTES: Long = 210L

    /** The same offset in seconds. Retained for callers that work
     *  in epoch seconds; the months index no longer consumes it
     *  (Audit R1 #37 killed the SQL twin — see class doc). */
    const val DAY_START_OFFSET_SECONDS: Long = 12_600L

    /** The filing date of an instant: the local date of (instant − 210 min). */
    fun filingDate(instantMillis: Long, zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(instantMillis).atZone(zone).toLocalDateTime()
            .minusMinutes(DAY_START_OFFSET_MINUTES).toLocalDate()

    fun filingYearMonth(instantMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Pair<Int, Int> {
        val date = filingDate(instantMillis, zone)
        return date.year to date.monthValue
    }

    /**
     * [first instant of the filing month, first instant of the next
     * filing month) in wall millis, both anchored at 03:30 local —
     * so the range selects exactly the instants whose filing date
     * falls inside (year, month).
     */
    fun monthBoundsMillis(year: Int, month: Int, zone: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
        val first = LocalDate.of(year, month, 1)
        val start = first.atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        val end = first.plusMonths(1).atTime(3, 30).atZone(zone).toInstant().toEpochMilli()
        return start to end
    }

    fun dateIso(date: LocalDate): String = date.toString()

    /**
     * The next instant at which [filingDate] changes for a clock
     * starting at [fromMillis] — the next local 03:30 strictly
     * after it. Truth Car 2 (DRW-05): the Drawer's "today" keys to
     * this boundary instead of freezing at first composition. By
     * construction the current filing day began at
     * filingDate(from) 03:30 local, an instant ≤ from, so the next
     * boundary is that day-start plus one day (atZone resolves any
     * DST shift inside the span).
     */
    fun nextBoundaryMillis(fromMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
        val boundaryTime = filingDate(fromMillis, zone).plusDays(1).atTime(3, 30)
        val resolved = boundaryTime.atZone(zone)
        if (resolved.toLocalDateTime() == boundaryTime) {
            return resolved.toInstant().toEpochMilli()
        }
        // Audit R6 #341 F69: 03:30 falls inside a spring-forward gap
        // (EU rule: 03:00 -> 04:00). atZone shifts the nonexistent
        // 03:30 forward by the gap, landing 30+ minutes AFTER the
        // filing rule itself has already flipped (filingDate flips
        // the instant the local clock first reads >= 03:30, which is
        // the transition instant itself). Wake at the transition.
        val transition = zone.rules.getTransition(boundaryTime)
        if (transition != null && transition.isGap) {
            return transition.instant.toEpochMilli()
        }
        return resolved.toInstant().toEpochMilli()
    }
}
