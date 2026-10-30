package com.example.ui.components

import com.example.service.PermPrint
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore

/**
 * THE GUARD LINE (chrome C2, spec §2.3) — the header band's right
 * side: one line of print over the Watch Store's counted four.
 *
 * Four states, no fifth:
 *  - WHOLE:      `GUARD 4/4`
 *  - LACK:       `GUARD {n}/4 — {NAME}` — n counts the counted four
 *                currently warranted GRANTED (GRANTED and not
 *                stale); NAME is the FIRST non-whole item in board
 *                order, printed BARE when missing and with
 *                ` STALE` appended when stale. One lack is named;
 *                a second rides unnamed in the count.
 *  - UNMEASURED: `GUARD — NOT CHECKED`. The line used to print
 *                NOTHING in this state — no default, no assumption,
 *                so the seeded all-false guard status could never
 *                print a day-one 0/4. Hof's PARK audit (P-6) struck
 *                the silence: on a cold open, blank reads as FINE
 *                to a tired man, and a blank is a claim too. The
 *                line now names its own ignorance — it still
 *                assumes nothing, and it no longer hides.
 *
 * Pure: a function of (snapshot, now) and nothing else — no clock
 * reads beyond the passed instant, no Android, no state. The same
 * snapshot at the same instant prints the same line, always, which
 * is what lets the seal freeze it honestly: at the seal frame the
 * host captures this print and the band holds it for the session's
 * whole life.
 */
data class GuardLinePrint(val text: String)

fun guardLineFor(snapshot: WatchSnapshot?, nowMono: Long): GuardLinePrint {
    if (snapshot == null) return GuardLinePrint("GUARD — NOT CHECKED")
    val prints = WatchItemId.COUNTED.map { it to WatchStore.permPrintAt(snapshot, it, nowMono) }
    if (prints.any { it.second == PermPrint.UNMEASURED }) {
        return GuardLinePrint("GUARD — NOT CHECKED")
    }
    val warranted = prints.count { it.second == PermPrint.GRANTED }
    if (warranted == WatchItemId.COUNTED.size) return GuardLinePrint("GUARD 4/4")
    val firstLack = prints.first { it.second != PermPrint.GRANTED }
    val name = when (firstLack.second) {
        PermPrint.STALE -> "${firstLack.first.boardName} STALE"
        // UNSET is its own word (issue #9, Zahn Z-7): the usage
        // door's "no record" answer is named, never dressed as a
        // measured denial.
        PermPrint.UNSET -> "${firstLack.first.boardName} UNSET"
        else -> firstLack.first.boardName
    }
    return GuardLinePrint("GUARD $warranted/4 — $name")
}

/**
 * PARK's headline (issue #9, Zahn Z-2 / Ruhe R3a): the SAME law
 * as the header line, in the drawer's dress — a count only off a
 * fully measured counted set, `GUARD — NOT CHECKED` in every
 * unmeasured state (null snapshot, EMPTY snapshot, partial set).
 * Before this helper the headline read countedWarranted raw and
 * printed a fabricated `GUARD 0/4` on cold open beside the
 * header's honest NOT CHECKED.
 */
fun parkGuardHeadline(snapshot: WatchSnapshot?, nowMono: Long): String {
    val counted = snapshot?.let { WatchStore.countedWarranted(it, nowMono) }
    return if (counted != null) "GUARD $counted/4" else "GUARD — NOT CHECKED"
}

/**
 * PARK's foot signature count segment (issue #9, Zahn Z-2):
 * `{n}/4` off a fully measured counted set, `NOT CHECKED`
 * otherwise — the foot may never sign a fabricated count either.
 */
fun parkGuardCountSegment(snapshot: WatchSnapshot?, nowMono: Long): String {
    val counted = snapshot?.let { WatchStore.countedWarranted(it, nowMono) }
    return if (counted != null) "$counted/4" else "NOT CHECKED"
}

/**
 * The seal freeze's Guard line, judged (issue #9, Zahn Z-4): a
 * freeze captured at a MEASURED print holds for the session's
 * whole life, as the freeze law says. A freeze captured at the
 * NOT CHECKED floor holds nothing — it is ignorance, not a seal
 * fact — so it falls through to the live derivation until a
 * measured capture replaces it (the ViewModel re-captures it on
 * the first measured emission). Before this, a session restored
 * after process death could hold a frozen NOT CHECKED header for
 * its whole life on a fully measured machine.
 */
fun frozenGuardLineOrLive(
    freeze: GuardLinePrint?,
    sessionActive: Boolean,
    snapshot: WatchSnapshot?,
    nowMono: Long
): GuardLinePrint {
    if (sessionActive && freeze != null &&
        freeze.text != "GUARD — NOT CHECKED"
    ) {
        return freeze
    }
    return guardLineFor(snapshot, nowMono)
}
