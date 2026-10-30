package com.example.ui.components

import com.example.service.PermPrint
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore

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
 *  - UNMEASURED: the line prints NOTHING. There is no default and
 *                no assumption — the seeded all-false guard status
 *                that used to print a day-one 0/4 is exactly the
 *                claim this state exists to prevent.
 *
 * Pure: a function of (snapshot, now) and nothing else — no clock
 * reads beyond the passed instant, no Android, no state. The same
 * snapshot at the same instant prints the same line, always, which
 * is what lets the seal freeze it honestly: at the seal frame the
 * host captures this print and the band holds it for the session's
 * whole life.
 */
data class GuardLinePrint(val text: String)

fun guardLineFor(snapshot: WatchSnapshot?, nowMono: Long): GuardLinePrint? {
    if (snapshot == null) return null
    val prints = WatchItemId.PERMISSIONS.map { it to WatchStore.permPrintAt(snapshot, it, nowMono) }
    if (prints.any { it.second == PermPrint.UNMEASURED }) return null
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
