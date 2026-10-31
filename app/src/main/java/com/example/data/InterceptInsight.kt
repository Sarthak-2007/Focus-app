package com.example.data

import java.util.Calendar
import java.util.TimeZone

/**
 * The intercept log, READ BACK (Audit R5 coverage — issues #251,
 * #253, #254, #255, DURCH-E lane).
 *
 * Until this file, every urge Monolith ever intercepted — its app,
 * its outcome, its trigger, its timestamp — was recorded forever
 * and then aggregated into exactly ONE number: the lifetime
 * counter behind `totalInterceptions` in the view model. The
 * audits named the same hole four times, from four angles:
 *  - #251: "a timestamped urge time-series … aggregated into
 *    exactly one number — no temptation pattern, danger hour,
 *    or predictive guard exists";
 *  - #253: "Intercept log is a write-only goldmine … reduced to
 *    a single lifetime counter, with no breakdown anywhere" and
 *    "dominant trigger, vulnerable hours and per-app resist
 *    rates are collected and never computed";
 *  - #254: the per-app attempt mirror rivals show at the
 *    decision point needs per-app counts that did not exist;
 *  - #255 (Move 3): "Monolith logs every deflection but never
 *    tells the user which apps, hours or urges are beating
 *    them."
 *
 * This is the missing computation, and ONLY the computation:
 * a pure derivation over the rows that already exist. No schema
 * change, no new storage, no surface — the redesign (issue #18)
 * decides where, or whether, these figures are printed; what it
 * can no longer say is that they were never computed.
 *
 * Truth rules, inherited from the slip's own law:
 *  - "Resisted" means the outcome is in
 *    [InterceptOutcome.BLOCKED_OUTCOMES] — the same allowlist
 *    [isBlockedInterceptOutcome] backs for the receipt. A peek
 *    grant is counted as a peek, never as a resist; an outcome
 *    no writer classifies is counted as OTHER, never silently
 *    folded into either side.
 *  - The reserved exposure rows ([MonolithRepository
 *    .EXPOSURE_LOG_PACKAGE]) are bookkeeping for the receipt,
 *    not urges — the receipt assembly already excludes them by
 *    the same test, and so does this derivation.
 *  - An empty log derives to zeros and nulls. No danger hour,
 *    no dominant trigger and no rate is ever invented from
 *    nothing.
 */
data class InterceptAppInsight(
    val packageName: String,
    val appName: String,
    val total: Int,
    val resisted: Int,
    val peeked: Int,
    val other: Int
) {
    /** Resisted share of this app's classified attempts
     *  (resisted + peeked); null when neither side was ever
     *  recorded, so an unclassifiable history prints no rate. */
    val resistRate: Float?
        get() {
            val classified = resisted + peeked
            return if (classified == 0) null else resisted.toFloat() / classified
        }
}

/** One trigger's tally: how often it fired, and how often the
 *  user held against it. */
data class InterceptTriggerInsight(
    val triggerReason: String,
    val total: Int,
    val resisted: Int
)

data class InterceptInsight(
    val total: Int,
    val resisted: Int,
    val peeked: Int,
    val other: Int,
    /** Per-app tallies, most-attempted first; ties break by app
     *  name so the order is stable and testable. */
    val perApp: List<InterceptAppInsight>,
    /** Per-trigger tallies, most-frequent first; ties break by
     *  trigger name. */
    val perTrigger: List<InterceptTriggerInsight>,
    /** Attempts per local hour of day, index 0..23. */
    val perHour: List<Int>,
    /** The local hour with the most attempts — the danger
     *  hour. Ties resolve to the EARLIEST hour. Null on an
     *  empty log. */
    val dangerHour: Int?,
    /** The most frequent trigger. Ties resolve alphabetically.
     *  Null on an empty log. */
    val dominantTrigger: String?
) {
    /** Resisted share of all classified attempts; null when
     *  nothing classifiable was ever recorded. */
    val resistRate: Float?
        get() {
            val classified = resisted + peeked
            return if (classified == 0) null else resisted.toFloat() / classified
        }

    companion object {
        val EMPTY = InterceptInsight(
            total = 0, resisted = 0, peeked = 0, other = 0,
            perApp = emptyList(), perTrigger = emptyList(),
            perHour = List(24) { 0 }, dangerHour = null,
            dominantTrigger = null
        )

        /** Pure: derive the whole breakdown from stored rows.
         *  [zone] decides what "hour of day" means; production
         *  passes the device zone by default. */
        fun derive(
            logs: List<InterceptLogEntity>,
            zone: TimeZone = TimeZone.getDefault()
        ): InterceptInsight {
            val urges = logs.filter {
                it.packageName != MonolithRepository.EXPOSURE_LOG_PACKAGE
            }
            if (urges.isEmpty()) return EMPTY

            var resisted = 0
            var peeked = 0
            var other = 0
            val perHour = IntArray(24)
            val cal = Calendar.getInstance(zone)

            // Per-app accumulation; the app name served is the
            // one on that package's MOST RECENT row, so a renamed
            // app is never labelled with its stale name.
            val apps = mutableMapOf<String, AppAcc>()
            val triggers = mutableMapOf<String, Pair<Int, Int>>()

            for (log in urges) {
                val wasResisted = isBlockedInterceptOutcome(log.outcome)
                val wasPeek = InterceptOutcome.USED_PEEK.equals(
                    log.outcome.trim(), ignoreCase = true
                )
                when {
                    wasResisted -> resisted++
                    wasPeek -> peeked++
                    else -> other++
                }

                cal.timeInMillis = log.timestampMillis
                perHour[cal.get(Calendar.HOUR_OF_DAY)]++

                val acc = apps.getOrPut(log.packageName) {
                    AppAcc(log.appName, log.timestampMillis)
                }
                if (log.timestampMillis >= acc.latestMillis) {
                    acc.appName = log.appName
                    acc.latestMillis = log.timestampMillis
                }
                acc.total++
                when {
                    wasResisted -> acc.resisted++
                    wasPeek -> acc.peeked++
                    else -> acc.other++
                }

                val key = log.triggerReason.ifBlank { "Habit" }
                val (t, r) = triggers[key] ?: (0 to 0)
                triggers[key] = (t + 1) to (r + if (wasResisted) 1 else 0)
            }

            val perApp = apps.entries
                .map { (pkg, a) ->
                    InterceptAppInsight(pkg, a.appName, a.total, a.resisted, a.peeked, a.other)
                }
                .sortedWith(compareByDescending<InterceptAppInsight> { it.total }
                    .thenBy { it.appName })
            val perTrigger = triggers.entries
                .map { (k, v) -> InterceptTriggerInsight(k, v.first, v.second) }
                .sortedWith(compareByDescending<InterceptTriggerInsight> { it.total }
                    .thenBy { it.triggerReason })

            var dangerHour: Int? = null
            var dangerCount = 0
            for (hour in 0..23) {
                if (perHour[hour] > dangerCount) {
                    dangerCount = perHour[hour]
                    dangerHour = hour
                }
            }

            return InterceptInsight(
                total = urges.size,
                resisted = resisted,
                peeked = peeked,
                other = other,
                perApp = perApp,
                perTrigger = perTrigger,
                perHour = perHour.toList(),
                dangerHour = dangerHour,
                dominantTrigger = perTrigger.firstOrNull()?.triggerReason
            )
        }
    }
}

/** Mutable per-app accumulation for [InterceptInsight.derive]. */
private class AppAcc(
    var appName: String,
    var latestMillis: Long,
    var total: Int = 0,
    var resisted: Int = 0,
    var peeked: Int = 0,
    var other: Int = 0
)
