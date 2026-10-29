# AUDIT — THE RECEIPT (the finish slip)
## Ruled by Zettel, coordinator. Crew: Una (critic) · Druck (designer, print) · Emil (inventor) · Muede (human, 1 AM) · Aug (finisher) · Neu (fresh-eyes). Filings: mail/zettel/0130__from-{una,druck,emil,muede,aug,neu}__receipt-audit.md. Ordered by Sarthak, Oct 30 01:15 IST.

## The verdict in one breath

The words are a receipt; several of its figures are not yet facts, and the
object carrying them is still a Material card. The slip's character — the
thing Sarthak ❤️'d — survives this audit intact and is protected below as
binding acquittals. What does not survive: the HELD line as printed (it
books PEEK apps as held and counts sites blind — W-41, KILLED as printed),
the footer's second clause as worded (W-43, stands on clause 2), a
deflection list that prints peek visits as if they were blocks (Neu
C-03, verified in assembly), and the slip's missing total — the lived
span is computed, carried in state, and printed nowhere (six seats,
unanimous). The ending also remains fragile: any stray tap swats it, a
dismissed slip is unrecoverable in-session, and it resurrects uninvited
on cold start. Cures are grouped at the end: what batch 10 can carry,
what needs Ren's ruling, what depends on other lanes.

## THE INVENTORY — every line the slip can print, in order, ruled

1. **Title (his intention)** — KEEP. Binding acquittal (Una, Neu, Muede).
   Faulted slips: see item 14.
2. **№ (zero-padded, row-latched claim, RC-04)** — KEEP as fact (Una,
   Aug). MOVE as print (Druck P-4): the head is an app bar; the № prints
   smaller and greyer than the title. A document's number is its
   head-mark — same ink, never greyer.
3. **ARMED HH:mm** — KEEP, with item 4. STATE-ONLY date (Neu C-02,
   Muede C-9): when the span crosses midnight the slip prints no date
   anywhere; the ARMED line carries its day, or the head carries the
   date once. Same-day slips unchanged.
4. **PLANNED {longSpan}** — MOVE (Una; Neu C-12). Assembly recomputes it
   from the final row's spanTargetSeconds, so a mid-session tamper
   penalty prints inside the plan. The figure must source from the
   seal-time target (Frist's sealedSpanTargetSeconds, batch 10) — the
   same source the Deck takes, or the two surfaces disagree.
5. **THE MISSING TOTAL — `sealedElapsedSeconds`** — CURE, unanimous
   (Una R1, Muede P-1, Druck P-2, Emil P1, Neu C-01, Aug). The field is
   assembled, carried through PendingReceipt into ReceiptState, and
   read zero times by the sheet. The slip gains its total:
   `RAN {longSpan(sealedElapsedSeconds)}`, set as the total line under
   ENDED over a rule (Druck's sectioning, item 16). On FLOW slips —
   where PLANNED is lawfully absent (RC-02) — it is the headline
   figure; flow's whole point is the duration and it is the one figure
   the slip withholds. Price: ~4 lines. Field and formatter exist.
6. **ENDED HH:mm** — KEEP. The slip's best line (Una): endWallMillis
   stamped at the ending write (RC-01) prints the witnessed end on
   every stamped path. Edge (Neu C-09): unstamped rows still print a
   discovery instant or derivation in the same typeface — accepted as
   filed; the stamp has covered every new row since v11.
7. **Outcome word** — split ruling (Muede; Neu cold-concurring):
   - plain `ENDED` — KILL. Tautology: the line above already says
     ENDED HH:mm. The outcome slot prints only when it adds a fact.
   - `ENDED · SHIELD GAP` — KILL. The shield-gap fact then prints
     twice on one slip in two dialects (here + item 12). Item 12
     carries it alone.
   - `SLEPT THROUGH` — KILL. Reads in plain English as failure (he
     slept through the alarm) for the machine's own stand-down.
     Cure word: `STOOD DOWN` — the machine's act, and true.
   - `PARTIAL` — STATE-ONLY. A grade with its evidence redacted:
     attended seconds are computed at judgement and discarded, and the
     word spans 16 attended minutes to window-minus-16. It prints only
     with its evidence (`PARTIAL · {attended} OF {window}`, long form);
     the figure must be carried to the slip (small state addition) or
     the word waits. (Muede P-1b; Neu C-11.)
   - `ATTENDED`, `FLOW ENDED` — KEEP (`FLOW ENDED` is thin but true;
     item 5's total is its cure, not a new word).
8. **WITHIN line** — MOVE (Una; Druck S-1; RC-03). The routine-row
   branch is frozen at insert; the hand-armed branch is re-derived
   LIVE at assembly and again at rehydration, so a restored slip can
   disagree with its first printing. Cure already exists: the RC-03
   freeze (Voss's lane stamps the label at issuance). Batch 10 must
   not close this item without that merge.
9. **HELD n APPS · m SITES** — KILL AS PRINTED (W-41; Una, verified
   wider than filed; Neu C-04). Apps: `countHeldApps`
   (MonolithRepository.kt:1255) counts `resolvedTier != OPEN`, so
   PEEK-tier apps — which open on a lease and freely in every Safe
   Break — are booked HELD. Sites: `heldSites = getAllSitesSnapshot()
   .size` (Repository, startSession) is a blind list-size — with the
   browser toggle OFF the slip still books every site; with the list
   emptied it prints `0 SITES` while the enforcer silently holds the
   8 factory domains (AccessibilityService). Wrong in both directions.
   Ruling: the line becomes STATE-ONLY — it prints only when both
   figures derive from enforcement truth (SHUT/PEEK split for apps,
   enforcer-derived for sites). Until then it is OMITTED, under the
   slip's own omission law: an absent line over a false one. The split
   columns Suki offered in Car 3 (heldShutAtSeal/heldPeekAtSeal) are
   the priced cure — Ren's batch-10 call (group C).
10. **BREAK BANKED n MIN** — KILL as named and as arithmeticked (Una
    latent-dialect charge; Neu C-05). Today it cannot print on a
    manual/FLOW receipt: spends are written only by a completed valve
    break (heldSeconds = 60, Repository:2047ff), whose session
    publishes nothing. Its one live path (routine valve-break →
    finalise) prints the 60-second hold PRICE as "BREAK BANKED 1
    MIN" — a semantic falsehood. And `/60` floor contradicts the same
    file's `longSpan` ceiling dialect. If a lawful banked-break
    closing ever lands, the line is re-earned under a true name
    (`BREAK HELD {longSpan}`), priced then.
11. **Deflection lines (HH:mm · app name, dim)** — KEEP the form
    (binding: listed, never summed — Una, Druck, Aug, Muede), with
    three cures:
    - TRUTH (Neu C-03, verified): assembly filters on intention,
      package, break windows, 30s dedupe — never on `log.outcome`,
      though InterceptLogEntity carries one (MonolithModels.kt:434).
      Rows where he was LET IN on a peek print identically to blocks.
      Cure: filter to blocked outcomes only; the outcome vocabulary
      must be enumerated in the cure, not assumed.
    - WINDOW (Una): for routine rows the window extends to
      windowEndMillis, booking post-end intercepts guarded only by a
      free-text intention match. Bound it or stamp its basis.
    - PRINT (Druck P-6 over Aug's dim acquittal — ruled for Druck):
      one ink. The dim second ink dies; separation is by the section
      rule and a hanging indent. Silence spans ≥20 min print as span
      rules inside the list (Emil P5, S–M). Site entries print
      distinguishably from app entries (Una/Neu edge).
12. **THE SHIELD WAS OFF FOR PART OF THAT SESSION** — KEEP, binding
    (all six seats; the plainest line on the slip). Voice cure in
    item 16 applies.
13. **Footer** — KEEP the law, KILL the noun (W-43; Una stood it
    precisely). Clause 1, "THE MACHINE KEEPS ITS OWN LOG", is TRUE
    and acquitted — with the precision that the log excludes the slip
    itself (only № + pointer + outcome persist, ReceiptState.kt:
    10–18). Clause 2, "YOUR STUDY RECORD LIVES IN YPT", is false as
    exclusivity: Monolith itself is a study record (session history,
    the Drawer of finished tasks), and "YPT" occurs in the whole
    Kotlin tree only in these two prints — no integration exists.
    What lives in YPT is hours. Cure (concurs Hale's RECORD→HOURS):
    `THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT`.
    Muede's acquittal of the footer's function (blocker-not-tracker,
    on paper) stands — the cure keeps the law, kills the false noun.
    Guards (Druck P-7; Aug): the one-line fit was measured at ~412dp
    widths; at 360dp the sentence wraps silently, and night ink in
    frame n08 is near-illegible. Add maxLines discipline: if it
    cannot fit one line at his fontScale, the footer splits at its
    own clause boundary into two lines — never wraps mid-clause —
    and its night ink follows the slip's ink law.
14. **The faulted sheet** — the audit's thickest knot (Muede Walk 4,
    Neu Q5, Emil, Aug, Una):
    - No title reads to a stranger as a rendering crash (Neu). F-W1's
      law was written to keep ENDED-FAULT off the slip, not to erase
      his intention for the machine's fault. CHARGE ON THE RULING:
      faulted slips print `FOR: <intention>` in the mono body (Emil).
      Ren ratifies or F-W1 stands whole — group B.
    - The empty title Box leaves № floating over a title-height void
      (Aug, faulted frame). Collapse the row on the faulted branch.
    - `(SERVICE DIDN'T STOP)` is engineer-speak at midnight (Muede,
      Neu). Plain wording, and print the retry fact: a retry is
      already scheduled at faultWall+30 000 and the slip never says
      so (+1 state field, priced by Muede P-3).
    - `STILL BLOCKED` never ages: on a restored slip the present tense
      re-asserts a stale fact (Una). Tie to Emil P6 (group B): a
      `RELEASE CONFIRMED LATER` corrected-duplicate stamp, which
      needs the fault flag to be clearable (+DB v12). Interim: the
      restored fault line carries the fault's wall time.
    - The completion pulse fires at ~700 ms, before the fault verdict
      exists (deadline 1520 ms) (Muede). A faulted finish must not
      sound the clean-finish pulse first. VM-side ordering cure.

## THE OBJECT — print and finish rulings

15. **Shape** — KILL the card (Druck P-1; Aug). 24dp rounded corners +
    1dp outlineVariant border were inherited whole from the digest
    sheet; thermal paper is cut square. Cure: 3dp radius, no border,
    lift by shadow. The torn bottom edge (Druck's GenericShape teeth;
    Emil's tear) is proposal-tier — group B.
16. **Rhythm and voice** — MOVE (Druck P-3; Aug). Uniform 8dp spacing
    and zero rules make a document with no sections: group the rhythm
    (8 within, 16 between), rule the sections (head / body / total /
    foot). The two bold statements leave the title's Atkinson for
    Anonymous Pro Bold 13sp/1sp — the typewriter's double-strike:
    the human voice belongs to his intention alone; the machine's
    interruptions speak machine, and at a size that interrupts.
17. **Scrim and anchor** — scrim KEEP at 0.12 (binding acquittals:
    Muede, Druck — the room stays visible; continuity is the point).
    Aug's pixel charge stands on its own fact and is cured without
    darkening: the nav pill peeks under the slip's foot in both day
    frames because the sheet floats (12dp padding all sides). MOVE:
    anchor means contact — 0 at bottom and sides; the slip covers
    the nav band.
18. **Arrival** — KILL the crossfade (Aug; Emil; Druck P-9). A fading
    receipt is a UI card appearing; the detention (E1480–E1800) has
    already paid for the arrival. The document FEEDS (Druck's
    slide-feed, short, whole-document — no number animates). The exit
    fadeOut is dead code (host unmounts first) — delete it; the real
    exit law is item 19. Restored slips never re-perform the arrival
    (Aug): they render settled, per the contract's own Z6.

## THE ENDING — dismissal and keeping

19. **Dismissal** — MOVE (Muede C-2; Emil sin 2; Aug). Today any tap
    anywhere swats the ending, the tap's own action never fires, the
    fade-in lands under a moving finger, and `clickable` fires on UP
    where the host law promises DOWN. Merge-ready cures: dismissal
    lands on a real DOWN gesture; a 2-second dwell from the existing
    `receiptPublishedMonoMs` stamp during which taps pass to their
    own targets (the in-flight tap cannot kill an unread slip); the
    faulted slip alone dismisses only on an explicit act (Muede P-3).
20. **Keeping** — the asymmetry is charged (Emil sin 3; Muede C-2):
    dismissal is unpersisted `remember`, so the dismissed slip
    re-ambushes at cold start, while yesterday's slip can never be
    summoned on purpose. THE SLIP BOOK (Emil P3, M–L): a Drawer
    section where every № is an address — free in principle because
    RC-04 already stamps each № on its row — plus a Deck re-read
    strip for the standing slip (Muede P-2). Proposal-tier (group B):
    it adds a surface, and surfaces are Ren's call. Emil's TEAR
    (P2: only a perforated tear closes the slip, and tearing is what
    files it) is the audit's strongest invention and rhymes with the
    reception's ruled tear — also group B, priced M / M+ with the
    v12 torn-stamp tier. Interim law until Ren rules: a dismissed
    slip stays dismissed across process death (persist the dismissed
    № — small, no new surface), so the ambush dies even before the
    book exists.
21. **Silences** — GRACE_CANCEL silence: ACQUITTED, binding (Muede).
    VALVE_BROKE: no slip is mercy and is acquitted; total erasure is
    not — a Drawer stub rides with the Slip Book proposal (rows and
    stamps already persist). MISSED routines receive less truth than
    PARTIAL — charged here, cure belongs to the routine judgement /
    Drawer surfaces; forwarded as an outgoing note to Lade's audit.
    Clean SLEEP stand-down slips ambushing the waking man: Muede P-4
    (mint them drawer-only; faulted stand-downs exempt and loud) —
    group B, Ren's call, one host-gate condition if ruled in.

## THE CURES — grouped for the merge

**Group A — batch-10 mergeable now (render / wording / assembly class):**
A1. The total line `RAN {longSpan}` (item 5) — ~4 lines.
A2. Outcome-word kills: plain ENDED, ENDED · SHIELD GAP, SLEPT
    THROUGH → STOOD DOWN (item 7).
A3. Footer noun cure RECORD → HOURS + one-line guard + night ink
    (items 13; W-43's cure, Ren's merge custody as already booked).
A4. Deflection outcome filter — blocked outcomes only (item 11).
A5. Shape: 3dp radius, no border, anchor to contact (items 15, 17).
A6. Rhythm + section rules; statements to Anonymous Pro Bold 13sp
    (item 16).
A7. Arrival: feed not fade; dead exit fade deleted; restored slips
    settled (item 18).
A8. Dismissal: DOWN gesture + 2s dwell from the existing mono stamp;
    faulted slip dismisses explicitly (item 19).
A9. Faulted row collapse; dismissed-№ persistence (items 14, 20).
A10. Deflection print: one ink, rules + hanging indent, site entries
    distinguished (item 11 print half).
A11. Date on cross-midnight slips (item 3).

**Group B — Ren's ruling required (surfaces, laws, schema):**
B1. THE TEAR as the closing gesture + torn edge (Emil P2; Druck) —
    M / M+ with DB v12 torn stamp.
B2. THE SLIP BOOK in the Drawer + Deck re-read strip (Emil P3;
    Muede P-2/P-6) — M–L.
B3. F-W1 amendment: `FOR: <intention>` on faulted slips (Emil).
B4. Fault line: plain wording + printed retry time (+1 field) and
    the corrected-duplicate stamp `RELEASE CONFIRMED LATER` (Emil
    P6, +DB v12); pulse-ordering cure rides with it.
B5. PARTIAL with its evidence (attended seconds carried to the slip).
B6. Clean SLEEP stand-down slips drawer-only (Muede P-4).
B7. THE SENTENCE — one locked-grammar prose testimony line (Emil
    P4, M). Narrates, never sums; banned praise vocabulary as specced.

**Group C — dependencies on other lanes/decisions:**
C1. W-41's full cure: heldShutAtSeal/heldPeekAtSeal seal columns
    (Suki's Car-3 offer) + enforcer-derived sites figure. Until they
    land, item 9's omission holds — the HELD line does not print.
C2. RC-03 freeze for the WITHIN line — built in Voss's lane; the
    batch-10 merge carries it or item 8 stays open.
C3. PLANNED's seal-time source — Frist's sealedSpanTargetSeconds;
    the slip and the Deck must take the same source in the same merge.

**Standing dispositions:** W-41 — STANDS, wider than filed; line
KILLED as printed, STATE-ONLY until C1. W-43 — STANDS on clause 2
only; clause 1 acquitted; cure = A3 (as booked, Ren's merge custody).

## ACQUITTALS — binding, written to protect the loved character

- The printed-slip character itself: mono ledger (Anonymous Pro 11sp
  at 1sp tracking), long-form durations, zero buttons or CTA on the
  paper, the slip the brightest object in every frame (Aug).
- The omission law: HELD, PLANNED, and the title vanish rather than
  print a figure the machine cannot back (−1 never prints) (all).
- Deflections listed and never summed; 30s dedupe and system-row
  exclusion verified as contracted (Una).
- The detention E1200–E1800: the slip arrives only when the machine
  has proven it stopped; death-booked and PARTIAL finishes never
  pulse (Emil, Muede).
- Stillness after arrival: no re-strike, no animated numbers, no
  auto-dismiss timer (Muede, Aug).
- ENDED from the stamped endWallMillis — the slip's best line (Una).
- THE SHIELD WAS OFF sentence — plainest line on the slip (all six).
- GRACE_CANCEL's silence (Muede). FLOW's absent PLANNED (RC-02).

## SEAT INDEX — every filing answered

Una: W-41 stood wider (item 9) · W-43 stood on clause 2 (item 13) ·
PLANNED recompute (item 4) · WITHIN live re-derivation (item 8) ·
BREAK BANKED latent dialect → killed as named (item 10) · STILL
BLOCKED ageing (item 14) · RAN proposal adopted as A1 · deflection
window + site-labelling edges (item 11) · acquittals carried.
Druck: P-1 corners KILLED (item 15) · P-2 total adopted (A1) · P-3
rhythm/rules adopted (item 16) · P-4 head MOVE (item 2) · P-5
interruptions (item 16) · P-6 one ink — ruled FOR Druck over Aug's
dim acquittal; the form Aug acquitted stands (item 11) · P-7 footer
guard (item 13) · P-8 below-the-fold — cured structurally by Emil
P7's pinned head/foot, adopted inside items 13/16's sectioning;
Druck's bottom-fade is the fallback if pinning fights the host ·
P-9 feed adopted (item 18) · torn edge → B1 · S-1 = item 8.
Emil: sin 1 = A1 · sin 2 = item 19 · sin 3 = item 20 · P1 = A1 ·
P2 TEAR → B1 · P3 SLIP BOOK → B2 · P4 SENTENCE → B7 · P5 quiet
chapters adopted (item 11) · P6 corrected duplicate → B4 · P7 pin
the stock adopted (see Druck P-8) · F-W1 challenge → B3 · his two
self-killed drafts noted with respect — the never-sum law held.
Muede: C-1 = A1 · C-2 dismissal/keeping = items 19–20 · C-3 faulted
= item 14 · C-4 PARTIAL = item 7/B5 · C-5 sleep slips → B6 ·
tautology + double-print kills adopted (item 7) · BREAK BANKED
(item 10) · deflection list MOVE-to-Drawer NOT adopted — the form is
acquitted by three seats and the list is the session's only
narrative; its truth and print cures ride at item 11, and the Drawer
half of his ask is answered by B2 · P-5 (print a duration for the
shield gap) dies by his own condition — gap stamps can't prove a
duration today; recorded, not lost · acquittals carried binding.
Aug: fade KILLED, exit-fade dead code, DOWN dismissal (items 18–19)
· scrim 0.28 NOT adopted — 0.12 acquitted by two seats; his pixel
fact cured by contact anchor (item 17) · edge/float/rhythm/voices
adopted (items 15–17) · faulted dead air (item 14) · footer guard
(item 13) · restored-slip arrival + elapsed STATE-ONLY notes
adopted (items 18, 5) · re-shoot ask carried into my letter to Ren.
Neu: C-01 = A1 · C-02 date (A11) · C-03 outcome filter (A4) ·
C-04 = item 9 · C-05 = item 10 · undecodables (WITHIN, SLEPT
THROUGH, PARTIAL, YPT, №-to-nowhere) answered at items 7, 8, 13,
and B2 (the № gains its address if the book is ruled in) · C-09
ENDED edge noted (item 6) · C-12 = item 4 · C-13 = item 21 · Q5
faulted-as-crash = item 14/B3 · acquittals carried.

— Zettel, coordinator. Paper only; nothing compiled (his law).
