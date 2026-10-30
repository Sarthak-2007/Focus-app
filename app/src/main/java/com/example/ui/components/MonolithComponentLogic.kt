package com.example.ui.components

import com.example.data.SystemGuardStatus
import com.example.service.UsageAccess
import com.example.service.WatchStore
import com.example.ui.ArmPhase
import com.example.ui.provenCount
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
 *  - POSITIVE: the OFF it accompanies rests on a directly observed
 *    dead signal, not on silence or timeout.
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
            else -> "BIND HELD · UNSEEN" to "PROOF: BIND ONLY"
        }
        WarrantState.OFF -> when {
            !warrant.a11yBound -> "BIND NOT HELD" to "PROOF: POSITIVE"
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT" to "PROOF: POSITIVE"
            else -> "BIND HELD · UNSEEN" to "PROOF: POSITIVE"
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
        else -> "BIND HELD · UNSEEN"
    }
    return left to "PROOF: STALE"
}

/** TIME's print (Uma): a pure function of a stored end-milli — the
 *  shipped ever-sliding finish line dies here; nothing about this
 *  text is remembered, and nothing here ticks. */
internal fun endsAtText(endMillis: Long): String =
    "ENDS " + SimpleDateFormat("HH:mm", Locale.US).format(Date(endMillis))

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

/** What the sentence slot's tap does (Suki): FIX goes to the pane
 *  that owns the lack; RETRY re-reads the wire. The cascade's
 *  sentences (step 4) carry a null action: a jam offers no verb —
 *  retry is the machine's, never his — and an annulment is a
 *  fact to read, not a door to tap. */
enum class RefusalAction { FIX_ACCESSIBILITY, FIX_USAGE, RETRY_CHECK }

data class RefusalSentence(val text: String, val action: RefusalAction?)

/**
 * Suki's cause table, ONE sentence, causal priority — the a11y row
 * outranks the usage row because enforcement dies at the first leg.
 * The two standing sentences are Ren's chair ruling #4 verbatim;
 * the usage row is Suki's as filed. Healthy (LIVE) → null: the slot
 * renders its reserved emptiness.
 */
internal fun refusalSentenceFor(warrant: Warrant?): RefusalSentence? {
    if (warrant == null) return null
    return when {
        !warrant.a11yBound ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        warrant.state == WarrantState.UNPROVEN && !warrant.a11yStampedThisResidency &&
            warrant.usage != UsageAccess.UNSET ->
            // The UNSET exclusion is the C3 lie's other door (finisher
            // pass, fresh-eyes catch): with usage UNSET this row's
            // "TAP TO RETRY" printed while the Deck's RETRY handler
            // rerouted the tap to the Usage pane. UNSET falls through
            // to its own row below and names the pane it opens.
            RefusalSentence("CAN'T CHECK BLOCKING — TAP TO RETRY", RefusalAction.RETRY_CHECK)
        warrant.usage == UsageAccess.DENIED ->
            RefusalSentence("USAGE ACCESS IS OFF — TAP TO FIX", RefusalAction.FIX_USAGE)
        warrant.usage == UsageAccess.UNSET ->
            // Issue #6 C3/F9: UNSET is a known lack with a known
            // owner — the sentence names the door the hand opens.
            // "TAP TO RETRY" was the live lie: the Deck's tap on
            // this row already routed to the Usage pane.
            RefusalSentence("USAGE ACCESS IS OFF — TAP TO FIX", RefusalAction.FIX_USAGE)
        warrant.state == WarrantState.OFF ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        else -> null
    }
}

/** The sentence slot at a named instant (Truth Car 3, UNA2-03): a
 *  sentence that orders a fix must ride a CURRENT read. On a stale
 *  read the slot keeps its reserved emptiness — the seam carries
 *  the STALE mark, and the Deck's ticker re-reads the wire within a
 *  tick of the threshold, so the silence is bounded. Demanding a fix
 *  off a minutes-old warrant (or withholding one) would be the
 *  print lying about the wire's present tense. */
internal fun refusalSentenceFor(warrant: Warrant?, nowMono: Long): RefusalSentence? {
    if (warrant == null) return null
    if (WatchStore.warrantStaleForPrint(warrant, nowMono)) return null
    return refusalSentenceFor(warrant)
}

/**
 * The cascade's jam sentence (step 4, Opal §F4): while an attempt
 * is JAMMED, the slot names the first unanswering link — names,
 * never a count — in the present tense of a machine still
 * working. It carries no action: the retry fires on the
 * machine's own clock and offers him no verb. Non-jammed phases
 * print nothing here (PROVING speaks through provingSentenceFor
 * below since issue #98; the annulment's past-tense sentence is
 * the controller's own armSentence, not this function's).
 */
internal fun jamSentenceFor(phase: ArmPhase): RefusalSentence? =
    jamSentenceFor(phase, nowMono = null)

/**
 * Issue #126 F4 (ported onto the MonolithComponentLogic extraction
 * at the main merge): the jam sentence judges the LIVE pending set,
 * carries the machine's verb, and counts down to the annulment. The
 * frozen form read phase.jamSet — the set sampled at the L+500
 * deadline — so a link that answered during the retry window kept
 * being accused for the attempt's remaining life. The phase's links
 * map is updated by every mark (the controller's applyMark), so the
 * first STILL-pending link is the truthful name; jamSet survives
 * only as the fallback for a phase whose links carry no pending
 * mark. With [nowMono] the sentence also prints the retry verb and
 * the seconds left before the L+1500 annulment
 * (ArmProvingController.ANNUL_MS) — the deadline the old sentence
 * hid until it fired. The no-clock form keeps the original wording
 * for pure callers and tests.
 */
internal fun jamSentenceFor(phase: ArmPhase, nowMono: Long?): RefusalSentence? {
    if (phase !is ArmPhase.Jammed) return null
    val pending = phase.links.filterValues { it is com.example.ui.LinkMark.Pending }.keys
    val first = (pending.ifEmpty { phase.jamSet }).minByOrNull { it.ordinal } ?: return null
    if (nowMono == null) {
        return RefusalSentence(
            text = "${first.displayName} DIDN'T ANSWER — STILL PROVING.",
            action = null
        )
    }
    val remainingMs = (phase.latchMono + com.example.ui.ArmProvingController.ANNUL_MS - nowMono)
        .coerceAtLeast(0L)
    val secs = ((remainingMs + 999L) / 1000L).coerceAtLeast(1L)
    return RefusalSentence(
        text = "${first.displayName} STILL HASN'T ANSWERED — RETRYING · ANNULS IN ${secs}S.",
        action = null
    )
}

/**
 * The cascade's proving sentence (issue #98, R3 arming journey):
 * while an attempt is PROVING, the slot names the work and counts
 * it — "CHECKING THE SEAL — 1 OF 3 PROVED" — derived from the
 * controller's own link marks via provenCount(), so the print can
 * never disagree with the machine it describes. Before this the
 * slot was explicitly null for Proving and the Deck rendered its
 * idle form for up to the 1500ms annulment deadline while real
 * blocking already ran: a silent black box indistinguishable from
 * a hang. It carries no action, on the jam's own law: proving
 * resolves by seal or annulment on the machine's clock (≤1500ms),
 * and the human's cancel door for this span is the slide's own
 * pre-latch undo (BlockitCoreViews) before the latch, and the
 * grace abort after the seal.
 */
internal fun provingSentenceFor(phase: ArmPhase): RefusalSentence? {
    if (phase !is ArmPhase.Proving) return null
    return RefusalSentence(
        text = "CHECKING THE SEAL — ${phase.provenCount()} OF 3 PROVED",
        action = null
    )
}
