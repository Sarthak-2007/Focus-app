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

    /** FLOW ended by its lawful door past the floor. */
    const val STOPWATCH_ENDED = "STOPWATCH_ENDED"

    /** The 60s valve was held to completion: the token was spent
     *  and the session BROKE. No receipt, no №, ever. */
    const val VALVE_BROKE = "VALVE_BROKE"

    /** The free grace cancel inside the opening window. */
    const val GRACE_CANCEL = "GRACE_CANCEL"

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
        SessionOutcome.ELAPSED_CLEAN, SessionOutcome.ELAPSED_SHIELD_GAP -> FinishKind.MANUAL_FINISH
        SessionOutcome.STOPWATCH_ENDED ->
            if (row.completedSuccessfully) FinishKind.FLOW_FINISH else null
        else -> null
    }
}

/** Whether this finish kind sounds the E700 completion pulse.
 *  Death-booked finishes (ELAPSED_SHIELD_GAP) never pulse (B-12);
 *  PARTIAL never pulses; the rest of the publishable kinds do. */
fun finishPulses(kind: String, row: FocusSessionEntity): Boolean = when (kind) {
    FinishKind.MANUAL_FINISH -> row.outcome != SessionOutcome.ELAPSED_SHIELD_GAP
    FinishKind.FLOW_FINISH -> true
    FinishKind.ROUTINE_ATTENDED -> true
    FinishKind.ROUTINE_SLEEP_STANDDOWN -> true
    else -> false
}

/** One deflection on the slip: a blocked attempt the machine
 *  turned away during the session, reduced at assembly (window
 *  ∩ intention match ∩ 30s-per-package dedupe, first kept).
 *  There is deliberately NO total/count field anywhere in this
 *  contract (Zettel): the slip lists; it never sums. */
data class ReceiptDeflection(
    val appName: String,
    val packageName: String,
    val atWallMillis: Long
)

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
    // Audit R3b #145: the curio this finish forged, by name — the
    // completion moment must not be silent about its own reward.
    // Null when the span forged nothing (routine-born rows, valve
    // breaks publish nothing at all); the sheet omits the line.
    val forgedArtifactName: String? = null
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
    // Audit R3b #145: see PendingReceipt.forgedArtifactName.
    val forgedArtifactName: String? = null
) {
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
