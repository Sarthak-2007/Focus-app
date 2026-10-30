package com.example.ui.components

import com.example.service.DetectorHealthSnapshot
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

/**
 * Issue #339 (Second-pass F4): the permission-criticality
 * taxonomy. Freedom's engine publishes availability as a
 * first-class state — 'active', 'disabled' (ONLY when
 * Accessibility is missing), 'restricted' (anything else
 * missing) — while Monolith's Guard count conflated critical and
 * cosmetic grants into one number, so the board could not say
 * which single grant kills blocking.
 *
 * The taxonomy, over the Watch Store's prints:
 *  - DISABLED: Accessibility is KNOWN missing (MISSING /
 *    MISSING_STALE) — blocking's fatal grant is gone.
 *  - RESTRICTED: Accessibility stands granted and at least one
 *    other permission is KNOWN missing — blocking lives, degraded.
 *  - ACTIVE: Accessibility granted, no known lack anywhere.
 *  - null (no claim): Accessibility's own state is unmeasured or
 *    merely stale — the board prints nothing rather than guess,
 *    the guard line's UNMEASURED rule. STALE and UNPROVED prints
 *    on other items are not known lacks and never force RESTRICTED.
 */
enum class BlockingAvailability { ACTIVE, RESTRICTED, DISABLED }

fun blockingAvailabilityFor(snapshot: WatchSnapshot?, nowMono: Long): BlockingAvailability? {
    if (snapshot == null) return null
    val a11y = WatchStore.permPrintAt(snapshot, WatchItemId.ACCESSIBILITY, nowMono)
    if (a11y == PermPrint.UNMEASURED || a11y == PermPrint.STALE) return null
    if (a11y == PermPrint.MISSING || a11y == PermPrint.MISSING_STALE) {
        return BlockingAvailability.DISABLED
    }
    val knownLack = WatchItemId.PERMISSIONS.any { item ->
        item != WatchItemId.ACCESSIBILITY &&
            WatchStore.permPrintAt(snapshot, item, nowMono).let {
                it == PermPrint.MISSING || it == PermPrint.MISSING_STALE
            }
    }
    return if (knownLack) BlockingAvailability.RESTRICTED else BlockingAvailability.ACTIVE
}

/** The taxonomy as board print (PARK), one honest line. */
fun availabilityLineFor(snapshot: WatchSnapshot?, nowMono: Long): GuardLinePrint? =
    when (blockingAvailabilityFor(snapshot, nowMono)) {
        null -> null
        BlockingAvailability.ACTIVE -> GuardLinePrint("BLOCKING — ACTIVE")
        BlockingAvailability.RESTRICTED -> GuardLinePrint("BLOCKING — RESTRICTED")
        BlockingAvailability.DISABLED -> GuardLinePrint("BLOCKING — DISABLED · ACCESSIBILITY MISSING")
    }