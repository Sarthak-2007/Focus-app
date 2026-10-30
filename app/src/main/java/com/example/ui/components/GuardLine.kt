package com.example.ui.components

import com.example.service.DetectorHealthSnapshot
import com.example.service.PermPrint
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore

/** The Guard line's confession of ignorance (issue #9, P-6):
 *  the counted set has not been measured, so no count exists.
 *  One spelling, everywhere — header, freeze resolution and
 *  tests all read this constant's value. */
const val GUARD_NOT_CHECKED_TEXT: String = "GUARD — NOT CHECKED"

/**
 * THE GUARD LINE (chrome C2, spec §2.3) — the header band's right
 * side: one line of print over the Watch Store's permission family.
 *
 * Issue #109, F4: the denominator is SIX — every permission line
 * the PARK board prints. The line used to count only the four
 * COUNTED items and could print `GUARD 4/4` (whole) over a board
 * showing OVERLAY / BATTERY MISSING with a FIX key offered: the
 * header claimed a wholeness the board it opens contradicted.
 *
 * Three states, no fourth:
 *  - WHOLE:      `GUARD 6/6`
 *  - LACK:       `GUARD {n}/6 — {NAME}` — n counts the six
 *                currently warranted GRANTED (GRANTED and not
 *                stale); NAME is the FIRST non-whole item in board
 *                order, printed BARE when missing and with
 *                ` STALE` appended when stale — a stale DENIAL
 *                included (#58 F1: MISSING_STALE prints with the
 *                suffix exactly as a stale grant does). One lack
 *                is named; a second rides unnamed in the count.
 *  - UNMEASURED: `GUARD — NOT CHECKED` (issue #9, P-6). The line
 *                used to print NOTHING in this state — no default
 *                and no assumption, so a seeded all-false status
 *                could never print a day-one 0/4. Hof's PARK audit
 *                struck the silence: on a cold open, blank reads
 *                as FINE to a tired man, and a blank is a claim
 *                too. The line now names its own ignorance — it
 *                still assumes nothing, and it no longer hides.
 *                A fabricated count is equally forbidden: no
 *                `/6` number exists until every one of the six
 *                is measured (Zahn Z-2; WatchStore.countedWarranted
 *                is gated on the same law).
 *
 * Pure: a function of (snapshot, now) and nothing else — no clock
 * reads beyond the passed instant, no Android, no state. The same
 * snapshot at the same instant prints the same line, always, which
 * is what lets the seal freeze it honestly: at the seal frame the
 * host captures this print as the session's birth record.
 *
 * THE FREEZE'S LIMIT (Audit R3 #113, finding 2, reconciled with
 * issue #9, Zahn Z-4): the captured print is a PAST-tense fact.
 * A MEASURED live print outranks the freeze in both directions:
 * a permission revoked mid-session drops the band off its frozen
 * whole print the moment the store measures the loss, and a
 * permission restored mid-session lifts it back. A freeze
 * captured at the NOT CHECKED floor holds NOTHING — it is
 * ignorance, not a seal fact — so it falls through to the live
 * derivation; a session restored after process death can never
 * be held at NOT CHECKED for its whole life by a birth capture
 * that raced the first sweep. See [resolveGuardLinePrint].
 */
data class GuardLinePrint(val text: String)

fun guardLineFor(snapshot: WatchSnapshot?, nowMono: Long): GuardLinePrint {
    if (snapshot == null) return GuardLinePrint(GUARD_NOT_CHECKED_TEXT)
    val prints = WatchItemId.PERMISSIONS.map { it to WatchStore.permPrintAt(snapshot, it, nowMono) }
    if (prints.any { it.second == PermPrint.UNMEASURED }) {
        return GuardLinePrint(GUARD_NOT_CHECKED_TEXT)
    }
    val warranted = prints.count { it.second == PermPrint.GRANTED }
    // Issue #109 F4 (merge resolution): the denominator is the
    // board's full permission set — PERMISSIONS.size (6) — never
    // COUNTED.size. COUNTED survives as the warrant's original
    // four and is, in WatchStore's own words, no longer the
    // header's denominator: a counted denominator prints
    // GUARD 4/4 wholeness over a board showing OVERLAY/BATTERY
    // MISSING (the exact F4 lie) and throws when all six stand
    // GRANTED. Issue #58's counted-denominator form collided
    // with this branch's six-count here; the six-count is the
    // audited truth, pinned by tests (GUARD 6/6; GUARD 5/6 —
    // OVERLAY; never 4/4 over a lack).
    val total = WatchItemId.PERMISSIONS.size
    if (warranted == total) return GuardLinePrint("GUARD $total/$total")
    val firstLack = prints.first { it.second != PermPrint.GRANTED }
    val name = when (firstLack.second) {
        // A stale lack names its age whatever its value was: a
        // stale GRANTED (STALE) and a stale denial (MISSING_STALE,
        // issue #58 F1) both print with the STALE suffix — neither
        // is presented as current fact.
        PermPrint.STALE, PermPrint.MISSING_STALE -> "${firstLack.first.boardName} STALE"
        // Issue #94: an UNPROVED line names its doubt on the Guard
        // line too — never a bare name that reads as an ordinary lack.
        PermPrint.UNPROVED -> "${firstLack.first.boardName} UNPROVED"
        else -> firstLack.first.boardName
    }
    return GuardLinePrint("GUARD $warranted/$total — $name")
}

/**
 * The header band's print, resolved (Audit R3 #113, finding 2,
 * reconciled with issue #9, Zahn Z-4).
 *
 * - No session: the live print, whole and simple.
 * - Session running, live print MEASURED (not the NOT CHECKED
 *   floor): the LIVE print. A measured present outranks a
 *   captured past — the band is a present-tense claim over the
 *   phone's guard, and printing the seal frame's whole print
 *   while a permission stands revoked is the lie this resolution
 *   exists to kill.
 * - Session running, live print at the NOT CHECKED floor, freeze
 *   MEASURED: the seal freeze stands — it is the last whole
 *   measurement the machine actually took, and the UNMEASURED
 *   law forbids inventing a fresher one.
 * - Session running, live at the floor and the freeze absent or
 *   also at the floor: the live print — `GUARD — NOT CHECKED`
 *   itself. A NOT CHECKED freeze holds nothing (Z-4).
 *
 * Pure, like [guardLineFor]: same inputs, same print, always.
 */
fun resolveGuardLinePrint(
    sessionActive: Boolean,
    frozen: GuardLinePrint?,
    live: GuardLinePrint
): GuardLinePrint {
    if (!sessionActive) return live
    if (live.text != GUARD_NOT_CHECKED_TEXT) return live
    val frozenMeasured = frozen?.takeIf { it.text != GUARD_NOT_CHECKED_TEXT }
    return frozenMeasured ?: live
}

/**
 * Issue #335 F5: the DETECTOR line — the detector-health surface
 * the audit found missing. Pure, like [guardLineFor]: a function
 * of (health snapshot, now) only. Three honest states:
 *  - never proved: the detector has not seen a single node yet —
 *    printed as such, never dressed as live.
 *  - stale: nodes were seen, then silence past the stale line —
 *    the silent-detector state, named instead of impersonating a
 *    working one.
 *  - live: nodes flowing; a slow last scan (>100 ms, Freedom's
 *    health line) is appended, not hidden.
 */
fun detectorLineFor(health: DetectorHealthSnapshot, nowMono: Long): GuardLinePrint {
    if (!health.hasProved) return GuardLinePrint("DETECTOR — NEVER PROVED")
    if (health.isStale(nowMono, DetectorHealthSnapshot.STALE_AFTER_MS)) {
        val silentSec = (nowMono - health.lastNodeMono) / 1000
        return GuardLinePrint("DETECTOR — STALE ${silentSec}S")
    }
    return if (health.lastScanWasSlow) {
        GuardLinePrint("DETECTOR — LIVE · LAST SCAN ${health.lastScanDurationMs}MS SLOW")
    } else {
        GuardLinePrint("DETECTOR — LIVE")
    }
}
