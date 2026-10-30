package com.example.data

import com.example.service.InterceptPath

/**
 * Issue #324, Finding 3 — BUILD half (the design half, per-reason
 * Airlock layouts, is #55's): the intercept log's typed taxonomy.
 *
 * At closure the finding stood exactly as charged: [InterceptLogEntity]
 * stored `outcome` and `triggerReason` as free-text strings, so no
 * statistic or receipt could aggregate what the user DECIDED at the
 * wall without string-matching prose, and nothing recorded WHY the
 * wall rose in a form code could switch on. This file is the typed
 * layer the cure adds (schema v16, MIGRATION_15_16):
 *
 *  - [InterceptOutcomeType] — what happened at the wall (the
 *    decision), replacing free-text outcomes as the aggregation
 *    source. The legacy `outcome` string stays on the row as the
 *    printed word (the slip prints it); the type is what stats and
 *    receipts now count.
 *  - [InterceptStopReason] — why the wall rose, stamped from facts
 *    the firing door actually holds (its [InterceptPath] and the
 *    session's routine birth). Values no writer can truthfully
 *    distinguish today (a Zario-style daily-opens LIMIT_HIT, a
 *    PEEK_EXPIRY re-intercept) are NOT in the enum: inventing them
 *    would fabricate history. They arrive with #55's design pass,
 *    when a surface exists that can tell them apart.
 *  - The stated trigger reason was already a real enum at the
 *    writers ([DistractionTriggerReason]); v16 persists its NAME
 *    alongside the short tag the door prints.
 *
 * Everywhere, UNKNOWN is the truthful value for "never recorded" —
 * the same doctrine as heldAppsAtSeal's -1: a pre-v16 row's door is
 * gone, and the migration backfills only what the legacy row itself
 * proves (see MIGRATION_15_16).
 */
enum class InterceptOutcomeType(
    /** The canonical legacy spelling the writers stamp into the
     *  free-text `outcome` column for this type, or null where the
     *  type has no single spelling (UNKNOWN). */
    val legacyOutcome: String?
) {
    /** The arrival itself, booked at trigger (issue #87): the wall
     *  rose; no decision has been made yet. */
    INTERCEPTED("Intercepted"),

    /** A plain block — the harness/test spelling. */
    BLOCKED("Blocked"),

    /** He turned away and returned to the work. A true deflection. */
    STAYED_FOCUSED("Stayed Focused"),

    /** Intercepted, never let in; he split the task instead
     *  (vocabulary of record in [InterceptOutcome]; no live writer
     *  remains on main, the type keeps the historical rows
     *  aggregable). A true deflection. */
    SPLIT_INTO_MICRO_STEP("Split Into 10m Micro-Step"),

    /** He was LET IN on the 2-minute peek lease. Never a block. */
    USED_PEEK("Used 2m Peek"),

    /** The airlock closed with no choice recorded — deliberately
     *  NOT a deflection (the slip cannot back a block nobody
     *  chose), exactly as [InterceptOutcome]'s allowlist ruled. */
    LEFT_WITHOUT_CHOOSING("Left Airlock Without Choosing"),

    /** He left the intercept through the launcher doors. */
    OPENED_LAUNCHER("Opened Launcher"),

    /** An uninstall attempt at the guard door that turned back. */
    UNINSTALL_ATTEMPT_RETURNED("Uninstall Attempt — Returned"),

    /** The ending write's exposure booking (#129 F3): an unguarded
     *  app used inside the window, filed under the reserved
     *  exposure package. Not a deflection. */
    OPEN_EXPOSURE("Open exposure"),

    /** The system rows (reserved system package): a disarm door
     *  ended the session; the row's legacy `outcome` carries the
     *  disarm reason word ("Ended Early", "Emergency Break", …),
     *  which is a session outcome, not an intercept decision. */
    ENDED_BY_DISARM(null),

    /** No writer's spelling matched — never classified. */
    UNKNOWN(null);

    /** True iff this outcome was a genuine turn-away — the typed
     *  form of [InterceptOutcome.BLOCKED_OUTCOMES]' allowlist.
     *  An allowlist, never a denylist: anything unproven stays off
     *  the slip. */
    val isBlocked: Boolean
        get() = this == BLOCKED || this == STAYED_FOCUSED || this == SPLIT_INTO_MICRO_STEP

    companion object {
        /** The stored column value for this type (its NAME). */
        fun storedOf(type: InterceptOutcomeType): String = type.name

        /** Read a stored v16 column value back. Anything
         *  unrecognised is UNKNOWN — a future type's rows degrade
         *  to unclassified, never to a guessed decision. */
        fun fromStored(stored: String?): InterceptOutcomeType =
            entries.firstOrNull { it.name == stored?.trim() } ?: UNKNOWN

        /**
         * Classify a legacy free-text outcome. Trimmed,
         * case-insensitive — the writers' spelling and the test
         * suites' spelling differ in case only (the same tolerance
         * [isBlockedInterceptOutcome] has always applied).
         * Unmatched prose is UNKNOWN, never a guess.
         */
        fun fromLegacy(raw: String?): InterceptOutcomeType {
            val text = raw?.trim().orEmpty()
            if (text.isEmpty()) return UNKNOWN
            if (text.startsWith("Uninstall Attempt", ignoreCase = true)) {
                return UNINSTALL_ATTEMPT_RETURNED
            }
            return entries.firstOrNull { type ->
                type.legacyOutcome?.equals(text, ignoreCase = true) == true
            } ?: UNKNOWN
        }
    }
}

/** Why the wall rose — see the file header for the honesty rule on
 *  which values exist. Stamped at write time from the firing door
 *  and the session's birth; UNKNOWN for every row whose door is no
 *  longer knowable. */
enum class InterceptStopReason {
    /** A hand-armed (manual) session's tier door stopped him. */
    SESSION_LOCK,

    /** A routine-born session's window stopped him. */
    ROUTINE_WINDOW,

    /** The uninstall guard door (settings / app-info reach). */
    UNINSTALL_GUARD,

    /** The Shorts/Reels sniper door. */
    SNIPER,

    /** The browser site door. */
    SITE,

    /** The ending write's exposure booking (not a wall at all —
     *  the row exists so the receipt can name the exposed apps). */
    OPEN_EXPOSURE,

    /** The system disarm row: the "stop" was the session ending. */
    SESSION_DISARM,

    /** Never recorded, or a door this build does not know. */
    UNKNOWN;

    companion object {
        fun fromStored(stored: String?): InterceptStopReason =
            entries.firstOrNull { it.name == stored?.trim() } ?: UNKNOWN
    }
}

/**
 * The stop reason a firing door can truthfully stamp: the guard,
 * sniper and site doors name themselves; the tier doors
 * (APP_TIER / SHIELD_APP) are told apart by the session's birth —
 * a routine-born session's stop is a ROUTINE_WINDOW stop, a
 * hand-armed session's is a SESSION_LOCK stop. An unprovenanced
 * door (UNKNOWN path) stamps UNKNOWN rather than guessing.
 */
fun interceptStopReasonFor(path: InterceptPath, sessionIsRoutine: Boolean): InterceptStopReason =
    when (path) {
        InterceptPath.UNINSTALL_GUARD -> InterceptStopReason.UNINSTALL_GUARD
        InterceptPath.SNIPER -> InterceptStopReason.SNIPER
        InterceptPath.SITE -> InterceptStopReason.SITE
        InterceptPath.APP_TIER, InterceptPath.SHIELD_APP ->
            if (sessionIsRoutine) InterceptStopReason.ROUTINE_WINDOW
            else InterceptStopReason.SESSION_LOCK
        InterceptPath.UNKNOWN -> InterceptStopReason.UNKNOWN
    }

/**
 * The stop reason a stored row proves on its own, for rows whose
 * writer stamped none: the two reserved packages and the uninstall
 * outcome are hard evidence; everything else is UNKNOWN — a
 * historical row's door cannot be reconstructed from prose.
 * Mirrors the MIGRATION_15_16 backfill exactly.
 */
fun interceptStopReasonFromLegacy(packageName: String, outcome: String?): InterceptStopReason =
    when {
        packageName == MonolithRepository.SYSTEM_LOG_PACKAGE ->
            InterceptStopReason.SESSION_DISARM
        packageName == MonolithRepository.EXPOSURE_LOG_PACKAGE ->
            InterceptStopReason.OPEN_EXPOSURE
        InterceptOutcomeType.fromLegacy(outcome) == InterceptOutcomeType.UNINSTALL_ATTEMPT_RETURNED ->
            InterceptStopReason.UNINSTALL_GUARD
        else -> InterceptStopReason.UNKNOWN
    }

/** The stored trigger-type value for a stated reason (its enum
 *  NAME), or UNKNOWN's name where no reason was stated. */
fun storedTriggerReasonTypeOf(reason: DistractionTriggerReason?): String =
    reason?.name ?: InterceptTriggerReasonUnknown

/** The stored value meaning "no stated trigger reason". */
const val InterceptTriggerReasonUnknown: String = "UNKNOWN"

/**
 * Read a row's stated trigger reason: the v16 column first; a row
 * the column leaves UNKNOWN falls back to the legacy short tag the
 * writers always stamped (Habit / Stuck / Tired / Message), so
 * pre-v16 rows keep the reason they actually recorded. The two
 * reserved packages return null — the exposure rows ride a package
 * name in the legacy column and the system rows never took a
 * reason; neither stated one.
 */
fun triggerReasonFromRow(
    storedType: String?,
    legacyTag: String?,
    packageName: String
): DistractionTriggerReason? {
    if (packageName == MonolithRepository.SYSTEM_LOG_PACKAGE ||
        packageName == MonolithRepository.EXPOSURE_LOG_PACKAGE
    ) {
        return null
    }
    DistractionTriggerReason.entries.firstOrNull { it.name == storedType?.trim() }
        ?.let { return it }
    return DistractionTriggerReason.entries.firstOrNull {
        it.shortTag.equals(legacyTag?.trim(), ignoreCase = true)
    }
}

/** Classify a legacy short tag (recordIntercept's derivation when
 *  the caller passes no explicit type). */
fun triggerReasonFromLegacyTag(tag: String?): DistractionTriggerReason? =
    DistractionTriggerReason.entries.firstOrNull {
        it.shortTag.equals(tag?.trim(), ignoreCase = true)
    }

/**
 * The aggregation the typed log exists for: counts per decision
 * type over any row set (a session window, a day, the whole
 * ledger). Pure — receipts and stats read the same numbers from
 * the same rows, with no string matching anywhere downstream.
 */
fun Iterable<InterceptLogEntity>.countByOutcomeType(): Map<InterceptOutcomeType, Int> =
    groupingBy { it.outcomeKind }.eachCount()

/** Counts per stop reason over any row set — the typed input
 *  #55's per-reason Airlock pass will aggregate against. */
fun Iterable<InterceptLogEntity>.countByStopReason(): Map<InterceptStopReason, Int> =
    groupingBy { it.stopReasonKind }.eachCount()
