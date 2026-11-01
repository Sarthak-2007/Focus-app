# ISSUE #10 — WAVE-2 REPORT · THE RECEIPT (audit fixes)
## Lade, cluster-D lead, flat under Ren. Branch `agent/issue-10`, cell `builds/cells/issue-10`.
## State: DECLARED — code + tests written, NOTHING compiled (standing law; Ren compiles on Sarthak's word).

The receipt slip is a proven keeper — Sarthak loved its print character.
Everything below is TRUTH FIXES ONLY: no redesign, the mono ledger,
omission law, listed-never-summed deflections and stillness all stand.

Wave-1 (commit e39ae62) landed: A4 outcome filter, A1 `RAN {longSpan}`
total, persisted dismissal, W-41 HELD seam (line omitted until the seal
split columns exist). Wave-2's job was the rest of Group A.

## FIXES IMPLEMENTED (this wave — commit d126c07, pushed)

1. **A2 — outcome words** (audit item 7). At the source:
   `MonolithRepository.receiptOutcomeWord` now stamps `STOOD DOWN`
   for ROUTINE_SLEEP_STANDDOWN (was `SLEPT THROUGH` — plain English
   read it as the man's failure for the machine's own stand-down).
   Collision check: `STOOD DOWN` appears nowhere else in this tree.
   At the sheet, the outcome segment prints only when it adds a fact:
   plain `ENDED` (tautology) and `ENDED · SHIELD GAP` (double-print —
   THE SHIELD WAS OFF… carries the gap alone) are suppressed; a legacy
   `SLEPT THROUGH` on a rehydrated slip normalises to `STOOD DOWN`, so
   no old slip re-prints the old word. PARTIAL stays STATE-ONLY (its
   evidence cure is B5, parked). ATTENDED / FLOW ENDED unchanged.
2. **Item 10 — BREAK BANKED killed as printed.** Its one live path
   booked a completed valve break's 60-second hold PRICE as
   "BREAK BANKED 1 MIN" — a semantic falsehood, in a /60-floor dialect
   contradicting the file's own ceiling longSpan. The print block is
   deleted; `bankedBreakSeconds` stays on the state as record
   (STATE-ONLY). Re-earning path recorded in code: a lawful
   banked-break closing under a true name (`BREAK HELD {longSpan}`),
   priced then.
3. **A3 — footer, W-43 cured** (item 13). Exact ruled text:
   `THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT`
   (clause 1 was always true; clause 2's RECORD was false as
   exclusivity — what lives in YPT is hours). The footer is now its
   own ruled section; guard: under 320dp available width it splits at
   the clause boundary into two lines, never mid-clause, `maxLines = 1`
   on the one-line form; night ink follows the slip's ink law (body
   ink at 0.72 alpha at 9sp) instead of the variant grey that went
   near-illegible in frame n08. One-line measurement re-derived for
   the new 60-char sentence (~315dp at 0.25sp tracking) in the code
   comment — Ren verifies at first render.
4. **A5 — shape + anchor** (items 15/17). 24dp card + 1dp border →
   3dp radius, no border, lift by shadow alone (10dp, same 3dp shape);
   outer padding 12dp-all → top-only: the slip sits in CONTACT at
   bottom and sides and covers the nav band.
5. **A6 — rhythm, rules, machine voice** (item 16). Sections now
   exist: the RAN rule (wave-1), a rule + 8dp gap before the
   deflections, a rule before the footer; both machine statements
   (THE SHIELD WAS OFF…, SHIELD — STILL BLOCKED…) move from Atkinson
   12sp to Anonymous Pro Bold 13sp/1sp — the human voice belongs to
   his intention alone. Head (item 2): the № prints in body ink,
   never greyer.
6. **A7 — arrival feeds** (item 18). Crossfade → whole-document
   slide-feed from the bottom edge (`slideInVertically`, specC 280ms,
   no number animates, no re-strike); the dead exit fadeOut is deleted
   (host unmounts first); restored slips render settled
   (EnterTransition.None — contract Z6).
7. **A8 — dismissal** (item 19). Scrim dismissal now lands on a DOWN
   gesture (`detectTapGestures` onPress — the old clickable fired on
   UP), gated by a 2-second dwell from `receiptPublishedMonoMs`, so
   the tap already travelling when the slip arrives cannot kill an
   unread slip. A FAULTED slip is not dismissible at the sheet at
   all — its exits are the host's own (navigation, PARK, which
   already dismiss it in MainActivity). NOTE, recorded honestly: the
   dwell's live-tap cost (a deliberate tap inside 2s reads as
   nothing) is the audit's own trade, accepted by all feel seats.
8. **A9 — faulted header collapses** (item 14, sheet half). The
   faulted branch no longer composes a title-height void with the №
   floating over it: the header row collapses to the № alone,
   end-aligned. F-W1 stands whole (no title, no `FOR:` — B3 is parked).
9. **A10 — deflections, print half** (item 11). One ink (the dim
   second ink dies), ruled section, 12dp hanging indent. The
   silence-span rules (Emil P5) and site-vs-app distinction are NOT
   built — see remainders.
10. **A11 — cross-midnight date** (item 3). When arm and end fall on
    different local dates, the ARMED line carries its day
    (`ARMED TUE 29 OCT 23:50 · PLANNED …`); same-day slips unchanged.

Tests (declared, unrun): `ReceiptWave2Test.kt` — legacy SLEPT THROUGH
normalisation, BREAK BANKED absence + footer HOURS, shield-gap single
print, cross-midnight ARMED date. No existing test pinned the old
prints (verified by grep across the suite).

## ROSTER — FINISHERS ×5 + FEEL ×5 (signed)

### Finisher — Surface
FIX — A5 is finite and shippable now: ReceiptSheet.kt:100 is RoundedCornerShape(24.dp) with a 1.dp outlineVariant border at :102; thermal paper is cut square, so close it to 3dp radius, no border, lift by shadow only — and A17's anchor in the same edit: the host Box pads 12.dp on all sides (:94), which is why the nav pill peeks under the slip's foot; go to 0 at bottom and sides (keep the top inset if the host needs it). Pair with Druck P-8's structural cure already adopted in the audit: pin head/foot, scroll the middle. One caution the sheet must respect: surfaceBright at :101 is the brightest object in every frame — that is an acquittal (Aug), so A5 changes the cut, never the stock. Signed — Finisher (Surface), #10 Receipt.

### Finisher — Motion
FIX — A7, and treat A18 as one item, not two: the detention (E1480–E1800, Detention in ReceiptState.kt:290-330) has already paid for the arrival, so the current fadeIn(specC) at ReceiptSheet.kt:84 is a card appearing, not a document arriving. Spec: delete the exit fadeOut outright (it is dead code — the host unmounts first), replace enter with Druck's short whole-document slide-feed (translateY from +100% to 0 over the feed duration in MonolithCurves, no number animates, nothing re-strikes), and gate on receipt.restored: a rehydrated slip renders settled with EnterTransition.None, per the contract's own Z6 (ReceiptState.kt:256-258). Acceptance is testable in the existing harness style: restored=true → first frame is the final frame. Signed — Finisher (Motion), #10 Receipt.

### Finisher — Light
FIX — A3's ink half, split from its wording half. Footer at ReceiptSheet.kt:201-207 is Anonymous Pro 9sp in onSurfaceVariant; Aug's charge (audit item 13) is that night ink in frame n08 is near-illegible, so the footer must follow the slip's own ink law — body ink, quiet by size and tracking, not by a greyer colour. Implement with W-43's noun cure in the same pass (RECORD → HOURS) and add the fit discipline: if the line cannot fit one line at his fontScale it splits at the clause boundary into two lines, never wraps mid-clause. Scrim stays 0.12 (binding acquittal, item 17) — do not let A5's anchor work darken it by accident. Signed — Finisher (Light), #10 Receipt.

### Finisher — Sound/Haptics
KEEP — with one sequencing charge carried, not closed. The sheet itself is silent and must stay so: no dismissal sound, no tear sound in Group A (the tear is B1, parked in #21). Wave-1's standing disposition holds — the E880 knock pair is P-gated with the completion pulse, part of the finish voice, not the paper's. What remains open is audit item 14's pulse-ordering defect: the completion pulse fires at ~700ms, before the fault verdict exists (deadline 1520ms), so a faulted finish can sound clean first. That cure is VM-side ordering, not a sheet edit — I record it as a remainder for Ren's batch-10 hand-work and explicitly do NOT sign it closed in this issue. Signed — Finisher (Sound/Haptics), #10 Receipt.

### Finisher — Typography/Print
FIX — A6 plus two print items, all in ReceiptSheet.kt. (1) Rhythm: the Column at :108 runs uniform Arrangement.spacedBy(8.dp) with zero rules; regroup to 8 within / 16 between, with section rules at head / body / total / foot — RAN already has its rule (:139); deflections and footer need theirs. (2) Voice: shieldWasOff (:172) and the faulted line (:185) are Atkinson Bold 12sp — move both to Anonymous Pro Bold 13sp/1sp; the human voice belongs to his intention alone (title, :116). (3) Head (audit item 2 / Druck P-4): numberPrint at :128 prints in onSurfaceVariant, smaller-gyer than the title — a document's number is its head-mark: same ink, never greyer. (4) A10's print half: deflections at :165-170 pass dim=true into ReceiptMonoLine — one ink; separation by section rule and hanging indent, not a second colour. Signed — Finisher (Typography/Print), #10 Receipt.

### Feel — Weight
FIX — A8 is the heaviest-feeling defect on the sheet. ReceiptSheet.kt:92 makes the entire scrim clickable(onClick = onDismiss) and :105 swallows the slip's own taps with clickable(enabled=false){} — any stray tap anywhere swats the ending, and Compose clickable fires on UP where the host law promises DOWN. Spec from the audit (item 19): dismissal lands on a real DOWN gesture (pointerInput + detectTapGestures), plus a 2-second dwell from receiptPublishedMonoMs (ReceiptState.kt:268 — sampled by the VM in publishReceipt, C2) during which taps pass through to their own targets. The faulted slip leaves this rule entirely: explicit act only. Nothing else on the slip should gain weight — zero buttons or CTA on the paper is an acquittal. Signed — Feel (Weight), #10 Receipt.

### Feel — Light (second seat)
KEEP — the light contract of this sheet is already right and my seat's job is to stop Group A breaking it. The slip is the brightest object in every frame (surfaceBright, :101), the room stays visible behind a 0.12 scrim (:91), and no figure animates after arrival. A3's night-ink cure (footer follows body ink) strengthens the one weak point; A5 must not import the digest sheet's heavier treatment — the 0.65 scrim there is a different room's law (sheet header, :42-44). If A6's section rules land, they print in outlineVariant like RAN's existing rule (:139) — rules are paper creases, not ink. Signed — Feel (Light), #10 Receipt.

### Feel — Timing
FIX — two timing truths. (1) A8's dwell is not a delay for its own sake: the slip's arrival is detained (E1200–E1800) precisely so the machine has proven it stopped; a dismissal that can land inside the first 2 seconds lets the in-flight tap — the one already travelling when the slip appeared — kill a document nobody has read. The dwell constant hangs off the existing mono stamp, no new clock. (2) A9's interim half: the fault line STILL BLOCKED never ages (audit item 14); until B4's corrected-duplicate stamp exists, a restored faulted slip must carry the fault's wall time — that is a state field the sheet cannot invent, so I sign the sheet-side half (faulted row collapse, below) FIX and the ageing half REMAINDER, Ren-routed. Signed — Feel (Timing), #10 Receipt.

### Feel — Touch+Sound
FIX — A9's touch half plus A8's faulted exception. Faulted slips today dismiss like clean ones — a single stray tap — yet they are the one slip a man must act on; Muede P-3 rules they dismiss only on an explicit act. On the clean slip, the DOWN-gesture cure must not introduce a ripple or press state: the paper does not depress, the scrim does not flash; the only feedback is the document leaving. Touch target honesty: the whole scrim is the target, so there is no small-target problem to solve — do not add a close affordance to fix a problem the sheet does not have (zero buttons is binding). Signed — Feel (Touch+Sound), #10 Receipt.

### Feel — Instant-Read
FIX — A11 and A2, the two one-glance lies. (1) ReceiptSheet.kt:125 prints ARMED HH:mm with no date anywhere on the slip (audit item 3, Neu C-02): when the span crosses midnight, the ARMED line carries its day (or the head carries the date once); same-day slips unchanged — a man reading yesterday's slip at 1 AM must not have to compute which night it was. (2) Outcome words at :134 (receiptOutcomeWord, MonolithRepository.kt:1736-1742): plain ENDED repeats the line above it, ENDED · SHIELD GAP prints the gap twice in two dialects, SLEPT THROUGH reads as his failure for the machine's stand-down — print STOOD DOWN, and let the outcome slot exist only when it adds a fact. The title (his intention) stays the first read; RAN (wave-1) is the total; nothing new competes with either. Signed — Feel (Instant-Read), #10 Receipt.

## ROSTER — THE 20 NAMED TYPES (signed)

### Crazy-ideas
Verdict: FIX A2/A3/BREAK BANKED kill first — they are one-line truth repairs; defer every invention. One concrete point: my crazy candidate — print the slip's № as a perforated stub edge — is B1-adjacent and parked in #21; do NOT smuggle it into A5's 3dp shape change. Spec for what ships: ReceiptSheet.kt:163 delete outright. Price: 0.5h including grep for "BANKED" across tests. — Crazy-ideas

### Maximum-implementation-potential
Verdict: A2 is bigger than the audit admits: `receiptOutcomeWord` (MonolithRepository.kt:1739) is also the value persisted via claimReceiptNumber → lastReceiptOutcome, so changing "SLEPT THROUGH" → "STOOD DOWN" changes the stored dialect, not just print. That's correct — fix at source, not in the sheet — but rehydrated old slips will carry the legacy string; sheet should normalize legacy "SLEPT THROUGH" on display or accept mixed dialect for one generation. Price the normalization: +3 lines, worth it. Rest of Group-A is sheet-local and low-risk. — Maximum-implementation-potential

### Brutal critic
Verdict: The wave-1 cell closed A1/A4/A9-dismissal and called the HELD seam done, but left the slip still printing two outright falsehoods — "BREAK BANKED" (:163, a hold *price* booked as banked minutes) and "YOUR STUDY RECORD LIVES IN YPT" (:203, W-43, false as exclusivity — Monolith itself is a study record). A receipt whose footer lies is worse than no footer. These two kills outrank A5/A6/A7 aesthetics; any wave-2 that ships shape before truth has its priorities inverted. Also: dismissal persistence was fixed in prefs while the sheet still dismisses on any tap (:91 clickable) — the ending is still fragile. — Brutal critic

### Improver
Verdict: FIX, with one improvement to A3's guard: instead of maxLines discipline alone, measure once — the cured sentence is 60 chars vs the old 62, at 0.25sp tracking/9sp it re-measures ~315dp against the same inner width, so the one-line fit the wave-1 comment feared losing is actually *safer* after the noun cure. Update the comment block at :190-199 with the new measurement rather than leaving a stale warning that will scare the next editor into reverting. Price: included in A3. — Improver

### Designer
Verdict: A5 + A6 as a pair, exact spec: RoundedCornerShape(3.dp), border deleted, shadow elevation for lift; Box padding 12.dp all sides → 0 bottom/sides (anchor = contact, covers nav band). Rhythm: replace spacedBy(8.dp) uniform (:108) with 8dp within sections / 16dp between, HorizontalDivider (outlineVariant, as at :139) ruling head/body/total/foot. Fonts untouched — Sora never appears here, Atkinson title + Anonymous Pro body is the acquitted voice. Never green, no images: satisfied trivially. — Designer

### Consumer
Verdict: I am the man at midnight. "SLEPT THROUGH" tells me I failed when the machine stood down — that word alone justifies A2. "BREAK BANKED 1 MIN" tells me I earned something I didn't. Kill both before any visual work; I will never notice 3dp corners, I will absolutely notice being lied to on the one slip I actually read. STOOD DOWN is plain English and true. — Consumer

### Student
Verdict: A3 matters more than it looks: the footer is the only place the app states the blocker-not-tracker law. "STUDY RECORD" contradicts the Drawer of finished tasks I can open in this same app — a student who keeps records here *and* in YPT reads that footer as the app denying its own Drawer. "YOUR STUDY HOURS LIVE IN YPT" is the exact, checkable claim. Ship A3 verbatim as ruled, no paraphrase. — Student

### User
Verdict: A8 is my whole experience of the ending: today any tap — including the one already in flight when the slip fades in — swats it (ReceiptSheet.kt:91-92), on UP, not DOWN. DOWN-gesture + 2s dwell from receiptPublishedMonoMs (already stamped, ReceiptState.kt:1329 in VM) is implementable without new state. Faulted slip: explicit dismissal only. I don't care about the feed animation if the slip can still vanish before I read RAN. — User

### User-experience
Verdict: A7 + A8 are one UX item: arrival and dismissal are the same gesture surface. Feed-not-fade only works if the dwell prevents the feed being interrupted by the tap that summoned the underlying screen. Sequence them: (1) A8 dwell/DOWN, (2) A7 feed with restored slips settled (restored=true → no enter transition, per Z6 at ReceiptState.kt:257), (3) delete dead exit fadeOut (:85) in the same edit or it will be "fixed" back in later. — User-experience

### Purpose-questioning
Verdict: Ask why each line exists. BREAK BANKED: no lawful banked-break closing exists today (audit item 10 — spends are written only by a completed valve break whose session publishes nothing); a line with no lawful referent is deleted, not renamed. Footer clause 2: its purpose is the blocker-not-tracker law — HOURS states it, RECORD overstates it. Outcome slot: purpose is adding a fact; plain "ENDED" adds none, so the slot itself is conditional. Every Group-A kill passes this test; every Group-A keep survives it. — Purpose-questioning

### Thoughtful
Verdict: A9 deserves more care than "collapse the row": the faulted sheet's empty title Box (:124) leaves № floating over a title-height void, which a tired reader parses as a rendering crash (Neu). Collapse is right, but keep the № in its exact head position and size — the number is the slip's address (RC-04) and must not move between clean and faulted slips, or the eye learns two documents. Also record: B3's `FOR: <intention>` would cure the stranger-read properly, but it's Ren's ruling in #21 — do not pre-build it in A9. — Thoughtful (LEAD NOTE: implemented as ruled — № keeps 13sp Bold; it sits end-aligned on the collapsed row, its head position on faulted slips. — Lade)

### Deep-thinker
Verdict: The deep issue under A2: outcomeWord is a *persisted* dialect (claim → lastReceiptOutcome → rehydrate), so the slip has a memory across process death that the audit's print-level reading misses. Any word change is a migration of meaning, not copy-editing. STOOD DOWN is safe because stand-down rows are classified by kind (ROUTINE_SLEEP_STANDDOWN), not by matching the word — verified: finishKindOf keys on routineOutcome/loadout, never on outcomeWord text. Proceed, and add a test pinning receiptOutcomeWord for every FinishKind so the dialect can't drift silently again. (Covered in ReceiptWave2Test at sheet level + the repository change is single-site. — Lade) — Deep-thinker

### Enthusiastic designer
Verdict: The feed arrival (A7) is the one place this sheet is allowed to be delightful — a receipt that *feeds* out is the loved character made physical. Spec it with restraint: whole-document slide, short (Druck's feed, not a bounce), no staggered lines, no number counting. If it animates anything beyond the paper's translation, cut it. Enthusiasm spent on A2/A3 truth first, delight second — in that order this wave earns both. — Enthusiastic designer

### Wow-level designer
Verdict: Nothing in Group A is wow, and that's correct — wow here would be a redesign, which is forbidden (the character is a proven keeper). The wow-level move available is A10's print half: one ink for deflections (kill dim=true at :167), section rule above the list, hanging indent — a list that reads as print, not UI rows. It costs nothing and it's the difference between a screenshot of an app and a photograph of a receipt. Do A10 print half; its silence-span rules (Emil P5) are S–M and can ride batch-10, not wave-2. (Silence spans recorded as remainder. — Lade) — Wow-level designer

### Designer with 29 years of experience
Verdict: Twenty-nine years of print: a receipt's credibility is its footer and its totals. This slip got its total in wave-1 (RAN) and still signs itself with a false footer — in print work that slip would be pulped. A3 verbatim, A5's square cut (thermal paper has no 24dp corners; the radius was inherited from a Material card, i.e., from software pretending to be paper), statements in the machine's own typewriter voice (Anonymous Pro Bold 13sp, audit item 16) — the human voice belongs to his intention alone. Ship in that order. — Designer, 29 years

### Intentionality-focused designer
Verdict: Audit every surviving default: 24dp radius (default card), 1dp outlineVariant border (default card), uniform 8dp spacing (default rhythm), fadeIn (default transition), dim second ink (default hierarchy trick). Group A is precisely the list of defaults this sheet inherited without choosing. Intentionality test for wave-2's diff: every remaining value in ReceiptSheet.kt should be traceable to an audit item or an acquittal. The scrim 0.12 passes (chosen, Linea). surfaceBright passes (chosen, Aug). The rest is A5/A6/A7's work. — Intentionality-focused designer

### Synchronous collaboration
Verdict: Coordinate A2 with whoever owns rehydration tests: ReceiptClaimRepositoryTest and ScreenshotHarnessTest:624 fabricate outcomeWord = "ENDED" — unaffected — but any harness frame fabricating a stand-down slip with "SLEPT THROUGH" will now mismatch production dialect. Grep before merge: "SLEPT THROUGH" should survive only in the audit doc and (if kept) a legacy-normalization branch. Also A3's footer text is asserted anywhere it appears — grep "STUDY RECORD LIVES" repo-wide; ParkDrawer already prints the HOURS dialect (:122 in ParkDrawer.kt), so the receipt is the laggard, not the pioneer. (Both greps run — clean. — Lade) — Synchronous collaboration

### Implementation-quality
Verdict: Implementation order with prices: (1) BREAK BANKED delete + footer string swap — 15 min, zero structural risk. (2) A2 repository word + sheet normalization — 30 min. (3) A5/A6 sheet restructure — half day, highest regression surface (scroll/pinned sections). (4) A7/A8 gesture+animation — half day, needs harness frames to verify restored-settled. (5) A11 date helper — 20 min, but put the date logic beside hhmm (:226) as a pure function of (armWallMs, endedAtWallMs) so it's unit-testable without compose. Total ≈ 1.5 days declared, uncompiled, consistent with the cell's existing test style. — Implementation-quality

### Technical execution specialist
Verdict: Three execution traps. (1) A8's dwell reads receiptPublishedMonoMs, which is 0 on restored slips (C3: never persisted) — gate must be `restored || mono == 0L || elapsed >= 2000`, or restored slips become undismissable for 2s after every cold start… actually worse, elapsedRealtime - 0 is huge, so it passes — but state the gate explicitly anyway. (2) detectTapGestures onPress fires on DOWN but awaits release — use onPress { tryAwaitRelease() } semantics carefully or dismissal triggers on press-and-hold scroll attempts. (3) A11: SimpleDateFormat hhmm (:227) uses default zone; the cross-midnight check must use the same zone or it will disagree with the printed times at DST/zone edges — use ZoneId.systemDefault() consistently. (Gate implemented exactly as (1); onPress dismisses at press without awaiting release, as the audit's DOWN law requires — a scroll attempt starting on the scrim was already a dismiss under the old clickable too; zone consistency done via LocalDate.ofInstant(systemDefault). — Lade) — Technical execution specialist

### Interaction-detail specialist
Verdict: Details that decide whether A7/A8 feel perfect: feed duration should match MonolithCurves.specC (280ms) — the house's own motion constant, not a new one; travel distance = slip's own height (initialOffsetY = { it }), ease on the printer's side (fast out, settle without bounce — a bounce is a toy). Dwell: during the 2s, taps must pass to their targets (audit item 19) — that means the scrim's pointerInput must *not consume* during dwell, which detectTapGestures alone won't give you; implement as a conditional pointerInput keyed on dwell expiry, or accept taps-eaten-but-not-dismissing as the honest interim and say so in the report. Vibration: none — no haptic on dismissal, the paper leaving is the feedback. 3D: the A5 shadow is the only depth cue; keep it flat otherwise. (Interim stated honestly in the fixes list: during dwell a scrim tap is eaten without dismissing rather than passed through — pass-through needs host cooperation and is a batch-10 remainder. — Lade) — Interaction-detail specialist

## REMAINDERS (not built, with reasons)
- **W-41 full cure** — needs persisted `heldShutAtSeal`/`heldPeekAtSeal`
  stamped at seal from enforcement state + an enforcer-derived sites
  figure (Ren's batch-10 data work, #15). The HELD line stays omitted;
  the fill point in `assemblePendingReceipt` is ready.
- **A10 silence-span rules** (Emil P5, S–M) and **site-vs-app print
  distinction**: intercept logs carry no site marker the assembly can
  trust (site rows share the browser's package; N6) — needs a marker
  at the intercept writers, cross-lane.
- **A8 dwell pass-through**: during the dwell a scrim tap is eaten,
  not passed to its own target (pointer-input consumption) — host
  cooperation, batch-10.
- **Fault pulse-ordering** (item 14: pulse at ~700ms precedes the
  1520ms fault verdict) + fault-line ageing / retry print: VM-side
  and B4-adjacent — Ren's batch-10 hand-work.
- **C2/C3 dependencies**: WITHIN freeze (RC-03, Voss's lane) and
  PLANNED's seal-time source (Frist's sealedSpanTargetSeconds) ride
  the batch-10 merge; item 8/4 stay open until then.
- **Outgoing charges from wave-1 stand**: N1 (peek counts as
  attendance, #12/#13), N2 (peek increments "urges stopped", #7),
  N3 (deflection list = resolutions, not attempts).

## DECISION-FOR-SARTHAK (all parked in #21 — flagged, never guessed)
- B1 THE TEAR as closing gesture + torn edge (M / M+ with DB stamp).
- B2 THE SLIP BOOK in the Drawer + Deck re-read strip (M–L). (Note:
  the Drawer is #8's room — a Slip Book section would land there;
  it arrives only by his ruling.)
- B3 F-W1 amendment: `FOR: <intention>` on faulted slips.
- B4 fault line plain wording + printed retry time + RELEASE
  CONFIRMED LATER corrected-duplicate stamp (+DB).
- B5 PARTIAL with its evidence (attended seconds carried to the slip).
- B6 clean SLEEP stand-down slips drawer-only.
- B7 THE SENTENCE (one locked-grammar prose testimony line).
- From #8's side: whether a forged artifact (and a repeat forge)
  prints on the finish receipt — the forge's announcement question.

## GITHUB — PUSHED + COMMENT POSTED (gh route)
- Cell origin was a LOCAL path (`builds/merge-batch9/Focus-app`) —
  fixed to `https://github.com/Sarthak-2007/Focus-app.git` per Ren's
  note; push: `agent/issue-10` created at d126c07, upstream set.
  The connector was never used (Sarthak's 04:06 route).
- Signed comment posted via `gh issue comment 10`:
  https://github.com/Sarthak-2007/Focus-app/issues/10#issuecomment-6510185545

## VERIFICATION OWED (Ren's process)
- First compile + ReceiptWave2Test + the receipt suites on this branch.
- R3's `"Blocked"` literal grep across production writers (wave-1
  remainder, still owed at first compile).
- Footer one-line measurement at his fontScale on a real frame;
  feed/dwell feel on his phone.

## NEXT WANTS
- Batch-10 (#15) to carry: the W-41 seal columns (then the ~3-line
  fill-point adapter flips and HELD returns in split grammar),
  RC-03 + sealedSpanTargetSeconds, the pulse-ordering cure, and the
  dwell pass-through.
- A night frame (n08 successor) after first compile, to prove the
  footer ink cure where it was charged.
