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
 * host captures this print as the session's birth record.
 *
 * THE FREEZE'S LIMIT (Audit R3 #113, finding 2): the captured print
 * is a PAST-tense fact. The band prints it only while no live
 * measurement exists — the moment the Watch Store holds a current
 * print, the live one is the header's truth, in both directions:
 * a counted permission revoked mid-session drops the band to
 * `GUARD 3/4 — …` at once (the freeze used to hold `GUARD 4/4`
 * over a dead permission for the session's whole life), and a
 * permission restored mid-session lifts it back. See
 * [resolveGuardLinePrint].
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

/**
 * The header band's print, resolved (Audit R3 #113, finding 2).
 *
 * - No session: the live print, whole and simple.
 * - Session running, live print MEASURED (non-null): the LIVE
 *   print. A measured present outranks a captured past — the band
 *   is a present-tense claim over the phone's guard, and printing
 *   the seal frame's `GUARD 4/4` while a counted permission stands
 *   revoked is the lie this resolution exists to kill.
 * - Session running, live print UNMEASURED (null): the seal
 *   freeze stands — it is the last whole measurement the machine
 *   actually took, and the UNMEASURED law forbids inventing a
 *   fresher one.
 *
 * Pure, like [guardLineFor]: same inputs, same print, always.
 */
fun resolveGuardLinePrint(
    sessionActive: Boolean,
    frozen: GuardLinePrint?,
    live: GuardLinePrint?
): GuardLinePrint? {
    if (!sessionActive) return live
    return live ?: frozen
}
