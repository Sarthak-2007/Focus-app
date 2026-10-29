package com.example.ui.components

import com.example.service.PermPrint
import com.example.service.WatchItemId
import com.example.service.WatchSnapshot
import com.example.service.WatchStore

/**
 * THE GUARD LINE (chrome C2, spec §2.3) — the header band's right
 * side: one line of print over the Watch Store's counted four.
 *
 * Three states, no fourth:
 *  - WHOLE:      `GUARD 4/4`
 *  - LACK:       `GUARD {n}/4 — {NAME}` — n counts the counted four
 *                currently warranted GRANTED (GRANTED and not
 *                stale); NAME is the FIRST non-whole item in board
 *                order, printed BARE when missing and with
 *                ` STALE` appended when stale. One lack is named;
 *                a second rides unnamed in the count.
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
    val prints = WatchItemId.COUNTED.map { it to WatchStore.permPrintAt(snapshot, it, nowMono) }
    if (prints.any { it.second == PermPrint.UNMEASURED }) return null
    val warranted = prints.count { it.second == PermPrint.GRANTED }
    if (warranted == WatchItemId.COUNTED.size) return GuardLinePrint("GUARD 4/4")
    val firstLack = prints.first { it.second != PermPrint.GRANTED }
    val name = when (firstLack.second) {
        PermPrint.STALE -> "${firstLack.first.boardName} STALE"
        else -> firstLack.first.boardName
    }
    return GuardLinePrint("GUARD $warranted/4 — $name")
}
