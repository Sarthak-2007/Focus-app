package com.example.data

/**
 * SLICE E — THE RECEIPT CONTRACT (Zettel, reconciled §10-Z1..Z6;
 * Nocke's discriminator + detention arithmetic; Reihe's issuance
 * rules). This file is the shared contract three joints build
 * against: the repository's claim transaction mints it, the VM's
 * publishReceipt facade is its only entry point (Schlitten C1),
 * and the Deck's slip renders it.
 *
 * THE STORE IS guard_settings (Z1): № = receiptSequence, slot
 * pointer = lastReceiptSessionId, outcome word =
 * lastReceiptOutcome, finish latch = lastCompositeSessionId. No
 * receipt_store table exists. What persists is exactly a number,
 * a pointer, an outcome word. The slip itself is re-derived from
 * the session row at issuance and at rehydration — never stored.
 *
 * Truth Car 2 (10→11): the session row additionally persists two
 * of its own facts for the slip — endWallMillis (RC-01, the true
 * end stamped at the ending write) and receiptNumber (RC-04,
 * the № this session claimed, stamped inside the claim
 * transaction; the claim latches on the row).
 *
 * Receipt audit (issue #10): two more persisted/carried facts.
 * The DISMISSED № lives OUTSIDE Room, in the repository's
 * `monolith_receipt` SharedPreferences (the audit's interim law,
 * item 20: a dismissed slip stays dismissed across process
 * death) — deliberately no guard_settings column, so this
 * contract implies NO database version change. And the HELD
 * line's future figures ride as [ReceiptHeldFigures]: today
 * nothing fills them (see that type), so the line does not
 * print at all.
 */

/** Nocke §2.3's outcome vocabulary — the discriminator stamped
 *  once, at the ending write, into focus_sessions.outcome. The
 *  empty string is the not-ended (or routine-row) value. */
object SessionOutcome {
    /** A manual session whose span elapsed clean. */
    const val ELAPSED_CLEAN = "ELAPSED_CLEAN"

    /** A manual session whose span elapsed while the shield was
     *  gone for part of it — the books are BROKEN (Nocke §5). */
    const val ELAPSED_SHIELD_GAP = "ELAPSED_SHIELD_GAP"

    /** Issue #129 F3: a manual session whose span elapsed with
     *  the shield beating, but where an unguarded distraction
     *  app (OPEN tier in a distraction category, an unreviewed
     *  auto-OPEN app, or an app with no catalogue row) was used
     *  for 30s+ inside the window. The session elapsed; it was
     *  NOT clean, and the record now says so. The exposed apps'
     *  names are booked at the ending write (see
     *  MonolithRepository.EXPOSURE_LOG_PACKAGE). */
    const val ELAPSED_OPEN_EXPOSURE = "ELAPSED_OPEN_EXPOSURE"

    /** FLOW ended by its lawful door past the floor. */
    const val STOPWATCH_ENDED = "STOPWATCH_ENDED"

    /** Issue #290 F1: FLOW stopped by hand UNDER its 20-minute
     *  floor — the withered record (Forest books a sub-minimum
     *  stopwatch stop as a withered tree). Distinct from
     *  ENDED_EARLY so the record names what actually happened;
     *  never a finish (FLOW_FINISH requires STOPWATCH_ENDED). */
    const val STOPWATCH_WITHERED = "STOPWATCH_WITHERED"

    /** The 60s valve was held to completion: the token was spent
     *  and the session BROKE. No receipt, no №, ever. */
    const val VALVE_BROKE = "VALVE_BROKE"

    /** The free grace cancel inside the opening window. */
    const val GRACE_CANCEL = "GRACE_CANCEL"

    /** Audit R6 #341 F30: a session that was still active when its
     *  successor was born — closed at the birth transaction with its
     *  true end stamp, never left unclassified. */
    const val SUPERSEDED = "SUPERSEDED"

    /** Ended early by his hand (the disarm challenge). */
    const val ENDED_EARLY = "ENDED_EARLY"

    /** A window's own end closed the session (routine doors). */
    const val ROUTINE_ENDED = "ROUTINE_ENDED"
}

/** The finish kinds the E-clock and the receipt recognise. A row
 *  is a FINISH iff it is inactive AND carries one of these
 *  stamps (Nocke §3, as reconciled — the outcome column, not the
 *  legacy completedSuccessfully bit, is the gate: a PARTIAL
 *  routine receipt publishes with completedSuccessfully = false). */
object FinishKind {
    const val MANUAL_FINISH = "MANUAL_FINISH"
    const val FLOW_FINISH = "FLOW_FINISH"
    const val ROUTINE_ATTENDED = "ROUTINE_ATTENDED"
    const val ROUTINE_PARTIAL = "ROUTINE_PARTIAL"
    const val ROUTINE_SLEEP_STANDDOWN = "ROUTINE_SLEEP_STANDDOWN"
}

/**
 * The terminal predicate (build-ready §7 reconciliation of Nocke
 * §3 + Reihe §3): evaluated on the ROW, pure, on every history
 * emission and at VM-init scan. Returns the finish kind or null.
 *
 * - Manual FINISH: inactive ∧ outcome ∈ {ELAPSED_CLEAN,
 *   ELAPSED_SHIELD_GAP}.
 * - FLOW finish: inactive ∧ outcome == STOPWATCH_ENDED ∧
 *   completedSuccessfully (the past-floor door is the only
 *   stopwatch ending that books a finish; its other doors stamp
 *   ENDED_EARLY / GRACE_CANCEL / VALVE_BROKE and never reach here).
 * - Routine ATTENDED: inactive ∧ routineOutcome == ATTENDED.
 * - Routine PARTIAL: inactive ∧ routineOutcome == PARTIAL
 *   (receipt, NO pulse).
 * - SLEEP stand-down: inactive ∧ routineOutcome == null ∧ the
 *   row is a SLEEP-loadout routine row ∧ now ≥ windowEndMillis ∧
 *   outcome column empty (a valve-broken SLEEP row carries
 *   VALVE_BROKE and is excluded by the last clause). The loadout
 *   test is the caller's (it needs the routine row); this pure
 *   form takes it as [isSleepLoadout].
 * - MISSED / LATE_FIRE are never an E0.
 */
fun finishKindOf(row: FocusSessionEntity, isSleepLoadout: Boolean, nowWallMillis: Long): String? {
    if (row.isActive) return null
    if (row.routineId != null) {
        // Audit R1 #40: VALVE_BROKE's law is "No receipt, no №,
        // ever" — and it binds routine rows too. Before this
        // guard the routine branch read routineOutcome alone: a
        // valve-broken row that the finalise funnel later stamped
        // ATTENDED/PARTIAL would mint exactly the receipt its
        // outcome forbids. (Only the SLEEP sub-branch below ever
        // looked at row.outcome.)
        if (row.outcome == SessionOutcome.VALVE_BROKE) return null
        return when {
            row.routineOutcome == WindowJudgement.ATTENDED.name -> FinishKind.ROUTINE_ATTENDED
            row.routineOutcome == WindowJudgement.PARTIAL.name -> FinishKind.ROUTINE_PARTIAL
            row.routineOutcome == null && isSleepLoadout &&
                row.windowEndMillis in 1..nowWallMillis && row.outcome.isEmpty() ->
                FinishKind.ROUTINE_SLEEP_STANDDOWN
            else -> null
        }
    }
    return when (row.outcome) {
        SessionOutcome.ELAPSED_CLEAN, SessionOutcome.ELAPSED_SHIELD_GAP,
        SessionOutcome.ELAPSED_OPEN_EXPOSURE -> FinishKind.MANUAL_FINISH
        SessionOutcome.STOPWATCH_ENDED ->
            if (row.completedSuccessfully) FinishKind.FLOW_FINISH else null
        else -> null
    }
}

/** Whether this finish kind sounds the E700 completion pulse.
 *  Death-booked finishes (ELAPSED_SHIELD_GAP) never pulse (B-12);
 *  PARTIAL never pulses; the rest of the publishable kinds do.
 *
 *  Issue #13, A-10(i) — SILENCE FOR THE KIND: the stand-down
 *  returns false. The machine does not applaud sleep. The gate
 *  sits on the finish ceremony's CALLS in the VM (the E0 knock
 *  and the E880 pair die with the pulse), so the haptic half —
 *  triggerWaveform fires unconditionally inside the sound
 *  engine, NOT gated by the sound toggle — dies with the audio.
 *  The voices themselves are untouched for every witnessed
 *  finish. */
fun finishPulses(kind: String, row: FocusSessionEntity): Boolean = when (kind) {
    // An exposed finish is death-booked in the same sense as a
    // shield gap (#129 F3): the record stands, the pulse does not.
    FinishKind.MANUAL_FINISH -> row.outcome != SessionOutcome.ELAPSED_SHIELD_GAP &&
        row.outcome != SessionOutcome.ELAPSED_OPEN_EXPOSURE
    FinishKind.FLOW_FINISH -> true
    FinishKind.ROUTINE_ATTENDED -> true
    FinishKind.ROUTINE_SLEEP_STANDDOWN -> false
    else -> false
}

/** Issue #13, A-10(ii): routine-born kinds. A receipt of one of
 *  these kinds is BORN FILED — it never stands on the Deck; it
 *  stays reachable as a record. One persisted filed-marker
 *  (ReceiptFiledStore, beside the standing pointer's world)
 *  carries the fact; rehydration and the standing composition
 *  both refuse a filed slip. */
fun isRoutineBornKind(kind: String): Boolean = when (kind) {
    FinishKind.ROUTINE_ATTENDED,
    FinishKind.ROUTINE_PARTIAL,
    FinishKind.ROUTINE_SLEEP_STANDDOWN -> true
    else -> false
}

/**
 * The intercept log's outcome vocabulary, enumerated from EVERY
 * writer in the tree (receipt audit item 11 / issue #10 A4,
 * ported by WELLE-6 from e39ae62 — the one e39ae62 code cure
 * current main had not absorbed by another route). The column
 * is free text at the writers; these are all the values that
 * exist:
 *
 * - [BLOCKED] — the harness/test spelling of a plain block
 *   ("Blocked" / "BLOCKED" in the test suites; matched
 *   case-insensitively).
 * - [STAYED_FOCUSED] — MonolithViewModel.transmuteImpulseAndReturn:
 *   the airlock turned him away and he returned to the work.
 * - [SPLIT_INTO_MICRO_STEP] — MonolithViewModel
 *   .splitActiveTaskAndReturn: intercepted, never let in; he
 *   split the task instead.
 * - [USED_PEEK] — MonolithViewModel's peek grant: he was LET IN
 *   on a 2-minute lease. NOT a block, and the one value this
 *   enumeration exists to keep off the slip.
 *
 * (The repository's disarm path also writes intercept rows with
 * the disarm reason as outcome, but always under the system
 * package — assembly's system-row exclusion already keeps them
 * off the slip. A newer writer, "Left Airlock Without
 * Choosing", is deliberately NOT in the allowlist: no choice
 * was recorded, so the slip cannot back it as a block — it
 * stays off until a ruling classifies it.)
 */
object InterceptOutcome {
    const val BLOCKED = "Blocked"
    const val STAYED_FOCUSED = "Stayed Focused"
    const val SPLIT_INTO_MICRO_STEP = "Split Into 10m Micro-Step"
    const val USED_PEEK = "Used 2m Peek"

    /** The outcomes that were truly turned away. Anything else —
     *  a peek grant, or a value no writer above produces — is
     *  NOT a deflection: the slip prints only what it can back,
     *  so the filter is an allowlist, never a denylist. */
    val BLOCKED_OUTCOMES: Set<String> = setOf(BLOCKED, STAYED_FOCUSED, SPLIT_INTO_MICRO_STEP)
}

/** True iff this intercept row's outcome was a block (see
 *  [InterceptOutcome]). Case-insensitive: the writers' spelling
 *  and the test suites' spelling differ in case only. */
fun isBlockedInterceptOutcome(outcome: String): Boolean =
    InterceptOutcome.BLOCKED_OUTCOMES.any { it.equals(outcome.trim(), ignoreCase = true) }

/** Issue #13, A-10(iv): kinds whose end is a STORED-END fact —
 *  a routine row's end is its frozen windowEndMillis, so filing
 *  at any age is truthful and branch U's 10-minute cliff (which
 *  guards against fabricating a № for an unwitnessed finish)
 *  does not apply. Witnessed manual finishes keep the cliff. */
fun receiptCliffExempt(kind: String): Boolean = isRoutineBornKind(kind)

/** One row of the Vault's receipts index (issue #157): the facts
 *  a claimed session's own row proves, nothing more — its №, its
 *  session id, the instant it ended, its outcome word, and the
 *  two warning facts the row itself carries (the release-fault
 *  stamp and the shield-gap outcome). The slip is NOT stored; a
 *  tap on this row re-derives it via the repository. */
data class ReceiptIndexEntry(
    val sessionId: Int,
    val number: Int,
    val endedAtWallMs: Long,
    val outcomeWord: String,
    val releaseFaultShield: Boolean,
    val shieldWasOff: Boolean
) {
    /** The printed №, the slip's own voice: zero-padded to a
     *  minimum of 3, never truncated. */
    val numberPrint: String get() = "№ " + number.toString().padStart(3, '0')
}

/** One deflection on the slip: a blocked attempt the machine
 *  turned away during the session, reduced at assembly (window
 *  ∩ intention match ∩ BLOCKED outcome ∩ 30s-per-package
 *  dedupe, first kept).
 *  There is deliberately NO total/count field anywhere in this
 *  contract (Zettel): the slip lists; it never sums.
 *
 *  [isSite] (Nachdruck R4, Finisher 3 §4): true when the reach
 *  was a SITE — the row's appName is the matched domain, stamped
 *  by the enforcer, not an app label. Derived at assembly by
 *  exact match against the blocked-site list (no DB change), so
 *  rehydrated slips — which re-run assembly — print identically
 *  to fresh ones. The default keeps every existing constructor
 *  (test fixtures, harness frames) valid. Named edge (Finisher
 *  3 §4.1, accepted there): if a domain is removed from the site
 *  list between the session and a later rehydration, that row's
 *  match fails and it prints as an app entry; the cure (a
 *  persisted kind stamp on the intercept row) is a DB change
 *  and stays Zettel/Ren's call. */
data class ReceiptDeflection(
    val appName: String,
    val packageName: String,
    val atWallMillis: Long,
    val isSite: Boolean = false,
    // Issue #105 (F3, remainder after the A4 allowlist): the
    // log's own outcome rides. Assembly admits only proven
    // blocks (A4 — a peek never reaches the slip at all), but
    // the three blocked outcomes are different events — a
    // turned-away attempt, a plain block, and a task split
    // into a micro-step — and the old print could not tell
    // them apart. Default "" keeps hand-built slips valid:
    // no outcome recorded, no word printed, none invented.
    val outcome: String = ""
) {
    /** The outcome in the slip's dialect. "Stayed Focused"
     *  gets its true name; any other logged blocked outcome
     *  prints verbatim (upper-cased); a deflection carrying
     *  no outcome prints no word. */
    val outcomePrint: String
        get() = when {
            outcome.trim().equals("Stayed Focused", ignoreCase = true) -> "TURNED AWAY"
            outcome.trim().equals("Used 2m Peek", ignoreCase = true) -> "PEEKED 2 MIN"
            else -> outcome.trim().uppercase(java.util.Locale.US)
        }
}

/**
 * The HELD line's figures, in the only form the slip may print
 * (receipt audit item 9 / W-41): apps split by what enforcement
 * actually did — [shutApps] held shut, [peekApps] peek-tier,
 * [heldSites] derived from the enforcer, never a list size.
 *
 * W-41 PREPARATION ONLY (issue #10): the persisted split columns
 * (heldShutAtSeal / heldPeekAtSeal + an enforcer-derived sites
 * figure) do NOT exist yet — they are Ren's batch-10 data work.
 * Nothing in this codebase fills this type today: assembly
 * leaves it null and the sheet prints no HELD line at all,
 * under the slip's own omission law (an absent line over a
 * false one). When the columns land, assembly fills this type
 * from them at the one marked fill point in
 * MonolithRepository.assemblePendingReceipt, and the line
 * prints with no sheet change. The legacy summed columns
 * (heldAppsAtSeal / heldSitesAtSeal) stay on the state as
 * record, STATE-ONLY — the sheet never reads them again.
 */
data class ReceiptHeldFigures(
    val shutApps: Int,
    val peekApps: Int,
    val heldSites: Int
) {
    /** The printed line, in the Shield header's split dialect
     *  (MonolithCustomComponents' heldSplitText): zero parts are
     *  omitted; null when nothing was held at all. */
    fun printLine(): String? {
        val parts = mutableListOf<String>()
        if (shutApps > 0) parts += "$shutApps HELD"
        if (peekApps > 0) parts += "$peekApps PEEK"
        if (heldSites > 0) parts += "$heldSites SITES"
        return if (parts.isEmpty()) null else parts.joinToString(" · ")
    }
}

/** The facts gathered at E0 for a publish, before the claim
 *  transaction mints the №. Pure reads only (assembly, Reihe §3):
 *  the ended row's own stored fields, the frozen routine stamps,
 *  the spend ledger, the intercept window. endedAtWallMs is the
 *  E0 wall sample, carried — never re-sampled at render. */
data class PendingReceipt(
    val sessionId: Int,
    val kind: String,
    val outcomeWord: String,
    val title: String,
    val armWallMs: Long,
    val endedAtWallMs: Long,
    // Null = the session had no plan (Truth Car 2, RC-02: FLOW
    // finishes — the stopwatch's stored target is a floor, not a
    // plan). The sheet omits the PLANNED segment whole, the same
    // omission law the HELD line obeys when its fact is absent.
    val plannedSeconds: Int?,
    val sealedElapsedSeconds: Int,
    val heldAppsAtSeal: Int,
    val heldSitesAtSeal: Int,
    val routineLabelWithin: String?,
    val bankedBreakSeconds: Int,
    val shieldWasOff: Boolean,
    val deflections: List<ReceiptDeflection>,
    // Audit R3 #113, finding 4: the tamper penalty the row carries,
    // threaded so the slip can NAME it — before this the penalty
    // silently stretched the plan (it is inside spanTargetSeconds)
    // and no surface ever mentioned it.
    val penaltySecondsAdded: Int = 0,
    /** Issue #129 F3: the unguarded apps actually used inside
     *  this session's window (labels, longest-use first), read
     *  back from the ending write's exposure booking. Empty for
     *  every session not stamped ELAPSED_OPEN_EXPOSURE. */
    val openExposureApps: List<String> = emptyList(),
    // Audit R3b #145: the curio this finish forged, by name — the
    // completion moment must not be silent about its own reward.
    // Null when the span forged nothing (routine-born rows, valve
    // breaks publish nothing at all); the sheet omits the line.
    val forgedArtifactName: String? = null,
    // W-41: the seal's held split, read off the row (see
    // FocusSessionEntity.heldShutAtSeal). -1 on a pre-v14 row:
    // the sheet falls back to the stored total.
    val heldShutAtSeal: Int = -1,
    val heldPeekAtSeal: Int = -1,
    // Issue #105 (F1): the tasks this session's own row booked
    // as finished (completedTasksSummary, split on its " • "
    // join), in booking order — the slip prints the work it
    // attests. (F4): the Safe Break seconds actually lived in
    // this session (the row's breakTakenSeconds), NOT the
    // valve ledger [bankedBreakSeconds] reads.
    val completedTasks: List<String> = emptyList(),
    val safeBreakSeconds: Int = 0,
    // W-41 seam (see ReceiptHeldFigures): null until the
    // persisted SHUT/PEEK split columns land; assembly is the
    // only fill point.
    val heldFigures: ReceiptHeldFigures? = null
)

/**
 * The issued receipt — the slip's whole content (Zettel §1, 18
 * fields). [number] is the № (prints `№ 048`, zero-padded to 3,
 * never truncated). [restored] marks a rehydrated slip (Z6 case
 * b): it renders settled, never re-Strikes, and never arms E6.
 * [receiptPublishedMonoMs] is sampled by the VM inside
 * publishReceipt (Schlitten C2) and lives in VM memory only —
 * never persisted, never restored (C3); a restored slip carries
 * 0 and no clock reads it.
 */
data class ReceiptState(
    val sessionId: Int,
    val number: Int,
    val kind: String,
    val outcomeWord: String,
    val title: String,
    val armWallMs: Long,
    val endedAtWallMs: Long,
    // Null on FLOW slips (RC-02): no PLANNED segment prints.
    val plannedSeconds: Int?,
    val sealedElapsedSeconds: Int,
    val heldAppsAtSeal: Int,
    val heldSitesAtSeal: Int,
    val routineLabelWithin: String?,
    val bankedBreakSeconds: Int,
    val shieldWasOff: Boolean,
    val faulted: Boolean,
    val deflections: List<ReceiptDeflection>,
    val restored: Boolean,
    val receiptPublishedMonoMs: Long,
    // Audit R3 #113, finding 4: see PendingReceipt — the slip
    // prints the penalty its plan silently included.
    val penaltySecondsAdded: Int = 0,
    /** Issue #129 F3: see [PendingReceipt.openExposureApps]. */
    val openExposureApps: List<String> = emptyList(),
    // Audit R3b #145: see PendingReceipt.forgedArtifactName.
    val forgedArtifactName: String? = null,
    // W-41: see PendingReceipt.heldShutAtSeal.
    val heldShutAtSeal: Int = -1,
    val heldPeekAtSeal: Int = -1,
    // Issue #152: the IANA zone the session was ARMED in, captured
    // at the birth funnel (ArmZoneStore). Null when unknown (a
    // session armed before the capture existed). The slip renders
    // ARMED in this zone — with its offset printed — whenever it
    // differs from the zone in force at render; a receipt's times
    // must say where they live.
    val armZoneId: String? = null,
    // Issue #105 (F1/F4): see PendingReceipt.completedTasks /
    // safeBreakSeconds — carried through publish + rehydrate.
    val completedTasks: List<String> = emptyList(),
    val safeBreakSeconds: Int = 0,
    // Issue #152 residue (reopened): the IANA zone the session
    // ENDED in, captured at the session-ending write into the
    // same store's end slot (ArmZoneStore, keyed by the end
    // instant). Null when unknown (a session that ended before
    // the capture existed). The slip renders ENDED in this zone
    // — with its offset printed — whenever it differs from the
    // zone in force at render, exactly as ARMED renders in its
    // own witness. Before this field existed ENDED was the one
    // line on the slip still derived at print time.
    val endZoneId: String? = null,
    // W-41 seam (see ReceiptHeldFigures): null until the
    // persisted SHUT/PEEK split columns land — while null, the
    // sheet prints NO HELD line (the old summed line booked
    // PEEK apps as held and counted sites blind; killed as
    // printed, audit item 9).
    val heldFigures: ReceiptHeldFigures? = null
) {
    /** The HELD line this slip may print, or null when the line
     *  is omitted whole (figures uncaptured, or nothing held). */
    val heldLine: String? get() = heldFigures?.printLine()
    /** The printed №: zero-padded to a minimum of 3, never
     *  truncated (№ 048 … № 1048). */
    val numberPrint: String get() = "№ " + number.toString().padStart(3, '0')
}

/**
 * Nocke §4's detention arithmetic — pure, table-tested (T3):
 *   R0 (rack start)  = max(E1200, P)
 *   receipt t=0      = min(max(E1480, R0 + 280), E1800)
 *   faulted          ⇔ no P by E1520, or the chain halted
 * All values are offsets from E0 in milliseconds; P is the
 * pawl-lift offset (the frame the one commanded link — the
 * shield's stop — confirmed open), or null while unconfirmed.
 */
object Detention {
    const val HELD_FRAME_MS = 1200L
    const val RACK_AFTER_PAWL_MS = 280L
    const val RECEIPT_FLOOR_MS = 1480L
    const val RECEIPT_CEILING_MS = 1800L
    const val FAULT_DEADLINE_MS = 1520L

    fun rackStartMs(pawlLiftMs: Long?): Long =
        // Audit R1 #40: an unlifted pawl deems the rack started at
        // the ceiling — NOT at Long.MAX_VALUE/4. The old sentinel
        // leaked into the ViewModel's detention tail
        // (rackStartMs(null) + RACK_AFTER_PAWL_MS fed straight to
        // delay()), parking the finish state for ~2^61 ms: on the
        // faulted branch the finish NEVER retired in that process.
        // receiptAtMs(null) is unchanged (1800 either way — the
        // clamp saw to that); only the tail is cured.
        maxOf(HELD_FRAME_MS, pawlLiftMs ?: RECEIPT_CEILING_MS)

    fun receiptAtMs(pawlLiftMs: Long?): Long {
        val r0 = rackStartMs(pawlLiftMs)
        return minOf(maxOf(RECEIPT_FLOOR_MS, r0 + RACK_AFTER_PAWL_MS), RECEIPT_CEILING_MS)
    }

    /** At [nowOffsetMs] into the detention, is the finish faulted
     *  beyond recovery? True once the fault deadline passes with
     *  no pawl-lift, or immediately when the chain halts. */
    fun isFaulted(pawlLiftMs: Long?, halted: Boolean, nowOffsetMs: Long): Boolean =
        halted || (pawlLiftMs == null && nowOffsetMs >= FAULT_DEADLINE_MS)

    /** The receipt issues at E1800 exactly on the faulted branch. */
    fun issueOffsetMs(pawlLiftMs: Long?, halted: Boolean, nowOffsetMs: Long): Long? {
        if (isFaulted(pawlLiftMs, halted, nowOffsetMs)) return RECEIPT_CEILING_MS
        val p = pawlLiftMs ?: return null
        return receiptAtMs(p)
    }
}
