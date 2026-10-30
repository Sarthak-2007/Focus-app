package com.example.ui.components

import android.os.SystemClock
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
 *  service list, a system-answered root acquisition, a live
 *  AppOps read. The stamp leg is that system answer:
 *  rootInActiveWindow is the system's own reply about the
 *  active window's root, whether it is read on the event path
 *  (a system-delivered event whose root is then acquired) or
 *  by the service's active self-probe. Amended in the open
 *  (FIX-2, answering CRIT-1 C-1): this definition once named
 *  only the event path, which forbade the probe the cold-start
 *  cure runs on — the doctrine is the distinction below, not
 *  the path the answer arrives by. What stays excluded,
 *  exactly as before, is the service's own internal say-so:
 *  no leg reads a flag the service merely sets about itself
 *  (the shield-protecting leg is excluded from the LIVE
 *  conjuncts for exactly this reason), so the per-link
 *  SELF-REPORT machinery the chair's F6 note imagined is not
 *  built (PLAN-AUDIT, Orig F6: unenforceable as written;
 *  Rosa's definition already solved it). `END-TO-END` stays
 *  forbidden.
 *  The known limit, named rather than hidden: event DISPATCH
 *  can wedge while the root API still answers, and a probe
 *  stamp would then certify a pipeline that delivers nothing.
 *  That residual is bounded by law, not trusted away — stamp
 *  validity is freshness (45 s, [WatchStore.STAMP_FRESH_MS],
 *  read via WatchStore.pipelineProvenFresh) and the bound
 *  service re-probes every 15 s, so a wedged pipeline's proof
 *  dies inside the freshness window instead of standing for
 *  the residency's whole life.
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

/** The grace age at [nowMono] — the one anchor math every grace
 *  judgement in this file shares (the seam below and the sentence
 *  table). The anchor is the warrant's grace anchor when it
 *  carries one, else its residency start (bind-epoch law,
 *  FIX-1 Addendum 3 / DAY-1 D-2): derivation stamps
 *  [Warrant.graceAnchorMono] with the bind epoch's first
 *  unproven instant when a proof debt is inherited across
 *  residencies, so Deck re-entry cannot mint fresh silence —
 *  the debt's age rides through the residency change. A
 *  hand-built warrant carrying anchor 0 falls back to its
 *  residency start, exactly as before the anchor existed. Null
 *  when neither is carried (both 0): age unknown, and age
 *  unknown is never inside a grace. */
private fun graceAgeMs(warrant: Warrant, nowMono: Long): Long? {
    val anchor =
        if (warrant.graceAnchorMono > 0L) warrant.graceAnchorMono
        else warrant.residencyStartMono
    return if (anchor > 0L) (nowMono - anchor).coerceAtLeast(0L) else null
}

/** Inside the cold-start grace: a carried anchor whose grace age
 *  is at most [WatchStore.RESIDENCY_GRACE_MS]. */
private fun inResidencyGrace(warrant: Warrant, nowMono: Long): Boolean {
    val ageMs = graceAgeMs(warrant, nowMono) ?: return false
    return ageMs <= WatchStore.RESIDENCY_GRACE_MS
}

/** The seam at a named instant (Truth Car 3, UNA2-03): while the
 *  read is fresh this is exactly [seamMarksFor]. Once the read is
 *  older than the board's own staleness threshold, the residency
 *  facts on the left do not age — bound is bound, a stamp is a
 *  stamp — but the PROOF does: the right mark admits the read's age
 *  ("PROOF: STALE") instead of claiming a currency it no longer
 *  holds. The warrant carries its read instant now; the print
 *  finally can tell.
 *
 *  One verdict per screen (FIX-2, answering CRIT-1 C-3): the
 *  timed seam also ages the grace anchor. An UNPROVEN warrant, bound
 *  but unstamped, INSIDE the grace still prints "PROOF: BIND
 *  ONLY" — the sentence slot is silent then too, so the screen
 *  speaks once. PAST the grace the sentence hardens to BLOCKING
 *  IS OFF, and the seam must not keep certifying a bind proof
 *  beside it: the left mark keeps the bound fact ("BIND HELD ·
 *  UNSEEN") and the right mark dies to "PROOF: NONE" — nothing
 *  has been proven, which is the whole verdict. The stale-read
 *  STALE mark keeps its precedence over both: a dead read admits
 *  its age before any grace arithmetic runs. */
internal fun seamMarksFor(warrant: Warrant?, nowMono: Long): Pair<String, String>? {
    if (warrant == null) return null
    if (WatchStore.warrantStaleForPrint(warrant, nowMono)) {
        val left = when {
            !warrant.a11yBound -> "BIND NOT HELD"
            warrant.a11yStampedThisResidency -> "BIND HELD · THIS VISIT"
            else -> "BIND HELD · UNSEEN"
        }
        return left to "PROOF: STALE"
    }
    if (warrant.state == WarrantState.UNPROVEN &&
        warrant.a11yBound &&
        !warrant.a11yStampedThisResidency &&
        !inResidencyGrace(warrant, nowMono)
    ) {
        return "BIND HELD · UNSEEN" to "PROOF: NONE"
    }
    return seamMarksFor(warrant)
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
 *  that owns the lack. There is no re-read action: the wire
 *  re-reads itself — probe, poke, collector, the Deck's ticker —
 *  faster than a tap ever did, so the fog's retry verb is
 *  deleted outright (FIX-2, answering FEEL-1 F-5), not left
 *  standing as a door no wall holds. The cascade's sentences
 *  (step 4) carry a null action: a jam offers no verb — retry
 *  is the machine's, never his — and an annulment is a fact to
 *  read, not a door to tap. */
enum class RefusalAction { FIX_ACCESSIBILITY, FIX_USAGE }

data class RefusalSentence(val text: String, val action: RefusalAction?)

/**
 * Suki's cause table, cured for the cold start (issue #7) — ONE
 * sentence, causal priority. Row order as restructured by FIX-2
 * (answering CRIT-1 C-5 / DAY-1 D-1 — the unbound grace): the
 * usage rows claim FIRST, above even the unbound row, because a
 * known lack with a known owner must never be masked — not by a
 * transient state, and not by another leg's death either. Below
 * them sits the UNPROVEN grace row, and it is bound-blind: an
 * UNPROVEN warrant inside its residency grace is silent whether
 * it is bound or not, because derivation now hands the table
 * BOTH cold-open races in that one state (the stamp race, and
 * the bind race — an enabled-but-unbound service inside grace
 * derives UNPROVEN, per WatchStore.deriveWarrant's unbound
 * grace). The two standing sentences are Ren's chair ruling #4
 * verbatim; the usage row is Suki's as filed. Healthy (LIVE) →
 * null: the slot renders its reserved emptiness.
 *
 * The cured table, in the order the rows claim:
 *  - stale read → null (the guard above the table: a fix may
 *    only be demanded off a current read; the seam carries the
 *    STALE mark instead).
 *  - usage DENIED or UNSET → USAGE ACCESS IS OFF (issue #6
 *    C3/F9: UNSET is a known lack with a known owner — the
 *    sentence names the door the hand opens). The old table
 *    fired its fog row first for an unstamped DENIED warrant,
 *    hiding a fixable lack behind a retry that could never
 *    fix it; these rows have stood above the transient rows
 *    ever since, and now above the unbound row too.
 *  - UNPROVEN, grace age ≤ grace → null (bound or not): the
 *    two cold-open races, below. Grace age is measured from
 *    the warrant's grace anchor — the bind epoch's first
 *    unproven instant when a proof debt is inherited across
 *    residencies, else the residency start (bind-epoch law).
 *  - not bound → BLOCKING IS OFF (the guard is dead; observed,
 *    not inferred). An unbound warrant reaches this row only
 *    past its grace or with no grant enabled — a grant-absent
 *    unbound derives OFF and lands here at residency age 0.
 *  - UNPROVEN past the grace (or with no residency carried) →
 *    BLOCKING IS OFF: bound but blind, or bound-never-came —
 *    off in fact either way.
 *  - OFF → BLOCKING IS OFF (kept for totality: this function
 *    stays truthful for any warrant handed to it).
 *  - LIVE → null.
 *
 * The races (the cold-start bug this cure closes): on a cold
 * open the Deck is showing before the accessibility service has
 * bound, and a quiet Deck generates no a11y events — so the
 * residency stamp that proves the pipeline can rest UNPROVEN,
 * unstamped, forever, and the old table printed its fog sentence
 * ("can't check") at rest, as if checking were a thing the hand
 * must retry. It is not: the guard now self-probes at residency
 * start, again at +1500 ms, and on rebind, so a bound guard proves
 * itself within moments with no hand involved; and an
 * enabled-but-unbound service is a bind still landing, not a
 * death, for the same window. Both transients are therefore
 * SILENCE — the slot's reserved emptiness, the house pattern for
 * machine-working states — for the grace window
 * ([WatchStore.RESIDENCY_GRACE_MS] from the residency start the
 * warrant itself carries). Past the grace, or with no residency
 * carried at all (residencyStartMono 0: age unknown, never fresh),
 * the verdict hardens and prints BLOCKING IS OFF; its FIX door
 * rebinds the service, which is the one act that can change the
 * fact. The fog sentence is deleted, not reworded: there is no
 * state left that it truthfully names.
 *
 * This no-clock overload delegates to the timed one with the
 * process clock, so every caller walks the same cured table and
 * no path can resurrect the fog.
 */
internal fun refusalSentenceFor(warrant: Warrant?): RefusalSentence? =
    refusalSentenceFor(warrant, SystemClock.elapsedRealtime())

/** The sentence slot at a named instant (Truth Car 3, UNA2-03): a
 *  sentence that orders a fix must ride a CURRENT read. On a stale
 *  read the slot keeps its reserved emptiness — the seam carries
 *  the STALE mark, and the Deck's ticker re-reads the wire within a
 *  tick of the threshold, so the silence is bounded. Demanding a fix
 *  off a minutes-old warrant (or withholding one) would be the
 *  print lying about the wire's present tense. This overload holds
 *  the cured cause table itself (see the no-clock overload's doc);
 *  its [nowMono] also ages the warrant's residency for the grace. */
internal fun refusalSentenceFor(warrant: Warrant?, nowMono: Long): RefusalSentence? {
    if (warrant == null) return null
    if (WatchStore.warrantStaleForPrint(warrant, nowMono)) return null
    return when {
        // The usage rows claim first (FIX-2 row order): a known
        // lack with a known owner is never masked — not by the
        // stamp race, not by the bind race, not by an unbound
        // service either.
        warrant.usage == UsageAccess.DENIED ->
            RefusalSentence("USAGE ACCESS IS OFF — TAP TO FIX", RefusalAction.FIX_USAGE)
        warrant.usage == UsageAccess.UNSET ->
            RefusalSentence("USAGE ACCESS IS OFF — TAP TO FIX", RefusalAction.FIX_USAGE)
        // The grace row, bound-blind (FIX-2, answering CRIT-1
        // C-5 / DAY-1 D-1): UNPROVEN inside the residency grace
        // is silent whether the warrant is bound or not. Usage
        // is ALLOWED here — DENIED and UNSET claimed their rows
        // above. Derivation hands this row both cold-open races
        // in the one state: bound-but-unstamped (the self-probe
        // is still landing its proof) and enabled-but-unbound
        // (the bind is still landing). Inside the grace, silence
        // is the machine working; the verdict hardens below.
        warrant.state == WarrantState.UNPROVEN && inResidencyGrace(warrant, nowMono) ->
            null
        !warrant.a11yBound ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        // UNPROVEN past the grace — or with no residency carried
        // at all (age unknown, never fresh): bound but blind, or
        // a bind that never came. Off in fact.
        warrant.state == WarrantState.UNPROVEN ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        warrant.state == WarrantState.OFF ->
            RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
        else -> null
    }
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
internal fun jamSentenceFor(phase: ArmPhase): RefusalSentence? {
    if (phase !is ArmPhase.Jammed) return null
    val first = phase.jamSet.minByOrNull { it.ordinal } ?: return null
    return RefusalSentence(
        text = "${first.displayName} DIDN'T ANSWER — STILL PROVING.",
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
