package com.example.ui.components
import com.example.R

import android.os.SystemClock
import com.example.data.ClockText
import com.example.data.SystemGuardStatus
import com.example.service.UsageAccess
import com.example.service.WatchStore
import com.example.session.ArmPhase
import com.example.session.provenCount
import com.example.service.Warrant
import com.example.service.WarrantState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Issue #54 F4: the pure print logic that used to live inside
// the 897-line MonolithCustomComponents god-file — warrant seam
// marks, end-time text, imprint tenant text, refusal and jam
// sentences. No Compose, no UI: unit-tested directly, and the
// components file now holds components.

/** The seam stamp's two marks for a warrant — Rosa's table and her
 *  grade definitions, which are binding on this print:
 *  - LIVE BIND: the enforcer is held by a live system bind in this
 *    process AND its pipeline root-stamped this residency's
 *    foreground AND the usage grant cross-check reads live. A claim
 *    about liveness and pipeline, explicitly NOT a claim that an
 *    intercept fired. (Rosa's filed definition also named a fourth
 *    leg — the accessibility grant string read live. SETTLED at
 *    the slide merge (Ren, Review F2): the definition is amended
 *    to the three legs actually read. The fourth is refused with
 *    reason: the system instantiates the accessibility service
 *    only when it is enabled and bound, so the instance's
 *    existence proves the grant more directly than re-reading
 *    the Settings string the substrate was built to retire.
 *    Settled alongside (Review F1): an unbound service maps to
 *    OFF by definition in liveWarrant — the UNPROVEN-unbound
 *    room is definitionally unreachable in production, not
 *    accidentally. Its seam branch STAYS: the function must
 *    remain total and truthful for any warrant handed to it —
 *    an earlier merge pass removed the branch and the lane's
 *    own table test caught the function printing BIND HELD
 *    for an unbound warrant. Reachability is liveWarrant's
 *    fact to state; print truth is this function's.)
 *  - BIND ONLY: liveness proven, pipeline (or a grant) unproven.
 *  - OFF CONFIRMED (issue #115; was "POSITIVE"): the OFF it
 *    accompanies rests on a directly observed dead signal, not on
 *    silence or timeout. "POSITIVE" read as proof that blocking
 *    WORKS next to "BIND NOT HELD" — the pair contradicted itself
 *    to any reader who does not hold this table; the new grade
 *    names what is actually proven: the OFF.
 *  - NOT SEEN (issue #115; was "UNSEEN" in the left mark): the bind
 *    is held, but no root-stamp was seen this residency — the left
 *    mark now says so in the same plain words the right mark's
 *    BIND ONLY grade already implies.
 *  - NONE: nothing has been proven yet either way.
 *  The grade is taken over the wire's legs only — the system's
 *  service list, a system-delivered root-stamp event, a live
 *  AppOps read. No leg is the service's self-assertion (the
 *  shield-protecting leg is excluded from the LIVE conjuncts for
 *  exactly this reason), so the per-link SELF-REPORT machinery
 *  the chair's F6 note imagined is not built (PLAN-AUDIT, Orig
 *  F6: unenforceable as written; Rosa's definition already
 *  solved it). `END-TO-END` stays forbidden.
 *  Two of Rosa's rows degrade honestly on this substrate, which
 *  records neither an ever-held bit nor a bind-loss instant:
 *  `BIND LOST HH:MM` and `BIND NEVER HELD` are NOT printed — the
 *  left mark states the present-tense fact the legs prove
 *  (`BIND NOT HELD`). Null warrant → null: before this process's
 *  first read, the stamp line is blank. */
internal fun seamMarksFor(warrant: Warrant?): Pair<String, String>? {
    if (warrant == null) return null
    return when (warrant.state) {
        WarrantState.LIVE -> "BIND HELD · THIS VISIT" to "PROOF: LIVE BIND"
        WarrantState.UNPROVEN -> when {
            // Room (ii): unconfirmed — nothing proven either way.
            // Production-unreachable (liveWarrant maps every
            // unbound read to OFF by definition — settled at the
            // slide merge, Review F1), but the function stays
            // total and truthful for any warrant it is handed:
            // an unbound warrant must never print BIND HELD.
            !warrant.a11yBound -> "BIND UNCONFIRMED" to "PROOF: NONE"
            // Held and stamped; a grant is what is unproven.
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT" to "PROOF: BIND ONLY"
            // Room (i): held, unstamped this visit.
            else -> "BIND HELD · NOT SEEN" to "PROOF: BIND ONLY"
        }
        WarrantState.OFF -> when {
            !warrant.a11yBound -> "BIND NOT HELD" to "PROOF: OFF CONFIRMED"
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT" to "PROOF: OFF CONFIRMED"
            else -> "BIND HELD · NOT SEEN" to "PROOF: OFF CONFIRMED"
        }
    }
}

/** The seam at a named instant (Truth Car 3, UNA2-03): while the
 *  read is fresh this is exactly [seamMarksFor]. Once the read is
 *  older than the board's own staleness threshold, the residency
 *  facts on the left do not age — bound is bound, a stamp is a
 *  stamp — but the PROOF does: the right mark admits the read's age
 *  ("PROOF: STALE") instead of claiming a currency it no longer
 *  holds. The warrant carries its read instant now; the print
 *  finally can tell. */
internal fun seamMarksFor(warrant: Warrant?, nowMono: Long): Pair<String, String>? {
    if (warrant == null) return null
    if (!WatchStore.warrantStaleForPrint(warrant, nowMono)) return seamMarksFor(warrant)
    val left = when {
        !warrant.a11yBound -> "BIND NOT HELD"
        warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT"
        else -> "BIND HELD · NOT SEEN"
    }
    return left to "PROOF: STALE"
}

/** TIME's print (Uma): a pure function of a stored end-milli — the
 *  shipped ever-sliding finish line dies here; nothing about this
 *  text is remembered, and nothing here ticks. */
internal fun endsAtText(is24Hour: Boolean, endMillis: Long): String =
    "ENDS " + ClockText.timeOfDay(is24Hour, endMillis)


/** The imprint's tenant (Emil): the seal keeps its count in every
 *  tense. Idle prints the future ONLY on a LIVE wire — over a dead
 *  or unproven wire the box is EMPTY (space held): printing WILL
 *  SHUT there is the register's worst lie, committed in type.
 *  Sealed prints the press-frozen count, never a live recount.
 *
 *  Truth Car 3 (UNA2-02): the count arrives SPLIT — [shut] the
 *  LOCKED tier, [peek] the PEEK_2M tier — and the tenant prints the
 *  split, as the Shield's header does. A PEEK app does not shut and
 *  is not held: it meets the airlock and a 2-minute lease. The old
 *  single count (LOCKED+PEEK under WILL SHUT / HELD) named the sum
 *  with the stronger verb; the number was real, the noun was the
 *  lie. Either figure unknown → the box is empty, never a guess.
 *
 *  Truth Car 3 (UNA2-04, Hale W-24): sealed, when the live wire
 *  has fallen to OFF ([wireNow]), the tenant keeps the seal's
 *  words but marks their tense on its face — the suffix
 *  " · AS SEALED" — while the BLOCK cell follows the seam's live
 *  word. When live and frozen agree, no suffix: a standing
 *  qualifier teaches the eye to skip it. */
internal fun imprintTenantText(
    isSealed: Boolean,
    shut: Int?,
    peek: Int?,
    warrantState: WarrantState?,
    wireNow: WarrantState? = null
): String? {
    if (isSealed) {
        val s = shut ?: return null
        val p = peek ?: return null
        val split = splitTenantText(s, p, sealed = true) ?: return null
        return if (wireNow == WarrantState.OFF) "$split · AS SEALED" else split
    }
    if (warrantState != WarrantState.LIVE) return null
    val s = shut ?: return null
    val p = peek ?: return null
    return splitTenantText(s, p, sealed = false)
}

/** The split in Hale's W-23 grammar, mirroring the Shield's nouns:
 *  idle "{n} WILL SHUT · {m} PEEK", sealed "{n} HELD · {m} PEEK".
 *  Omission law: a zero segment never prints — n == 0 leaves
 *  "{m} PEEK" alone, and "0 WILL SHUT" is never a sentence. Both
 *  zero → null: the box stands empty rather than print a count
 *  of nothing under a verb. */
private fun splitTenantText(shut: Int, peek: Int, sealed: Boolean): String? {
    val shutPart = when {
        shut > 0 -> if (sealed) "$shut HELD" else "$shut WILL SHUT"
        else -> null
    }
    val peekPart = if (peek > 0) "$peek PEEK" else null
    val parts = listOfNotNull(shutPart, peekPart)
    if (parts.isEmpty()) return null
    val split = parts.joinToString(" · ")
    // R3b #160 (two-phones-tablet): the seal certifies THIS handset only.
    // The sealed imprint says so at the moment of certification — the
    // hold never reaches a second phone, a tablet or a laptop, and the
    // words now carry that boundary instead of implying the person.
    return if (sealed) "$split · THIS PHONE ONLY" else split
}






/** The wedge (Shield Rethink 2, Step 1): Shield edits are lawful
 *  only while NO session is in hold — with one carve-out, the
 *  session's own grace window (its first seconds, where aborting
 *  is free and the law may still be corrected by the hand that is
 *  about to be bound by it). Extracted from ShieldMatrixScreen so
 *  the gate itself is pinned by a table test: a future refactor
 *  that quietly re-opens an edit door mid-hold fails CI, not the
 *  user. The ViewModel's one-way ratchet and tierBypassGuard are
 *  the session machinery's own latches, not edit doors, and are
 *  not governed by this print-side gate. */
internal fun shieldControlsLocked(isSessionActive: Boolean, isGracePeriod: Boolean): Boolean =
    isSessionActive && !isGracePeriod

