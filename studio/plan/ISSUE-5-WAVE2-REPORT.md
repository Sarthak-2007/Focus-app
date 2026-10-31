# ISSUE #5 — WAVE-2 REPORT (Verification: Truth Car 3 on the merged batch-9 tree)

**Lead:** Tor, cluster E, flat under Ren. **Cell:** builds/cells/issue-5, branch `agent/issue-5`.
**Wave-1 commit:** bb2393f (joint report + four session verdicts, studio/verification/).
**Wave-2 state:** DECLARED, uncompiled. Verification here is code-evidence per charge:
stands / cured / regressed, file evidence. No agent compiled or ran a test.
**Method:** 30 seats in 3 bundles + lead's independent re-derivation of the headline charges.

## HEADLINE — VERIFICATION COMPLETE ON PAPER

The four owed sessions all stand delivered and are now cross-checked by three
independent wave-2 bundles plus the lead: **Pruefer proof session** (proof-shape
audit, no PROOF BROKEN), **Una pass 2** (per-charge cure verdicts), **Hale lapse
check** (Lapses 0–7), **Register spot-check** (Small Things register). Two wave-2
bundles re-derived charges from the code itself, not from the wave-1 files, and
agree with wave-1 on every charge they touched. **REGRESSED: 0, in every session
and every wave-2 pass.**

Final tally (joint report §3, adopted): cure axis over 26 charge rows —
**20 CURED · 4 PARTIALLY CURED · 2 STANDS · 0 REGRESSED** — plus W-41 and W-43
outside the 26 rows, both **STAND**. Proof axis: **NO PROOF EXISTS for eight
units** (counting note in Gaps, item 3).

## LEAD'S INDEPENDENT RE-DERIVATION (Tor, this wave)

- `MIGRATION_11_12` (MonolithDatabase.kt:190–195): exactly two ALTERs —
  `shortsReelsSniperEnabled` (:192), `breakTakenSeconds` (:193). No peekMinutes /
  breakMinutes / breakSeconds / extension columns. **Times lane CURE ABSENT — confirmed.**
- `grep peekMinutes|breakSeconds|EditableTimes` over merged main: zero (only the
  unread `StudyModePreset.breakMinutes`, MonolithModels.kt:64, and two comments
  admitting the stamp is future). `startPeekLease` still reads the constant.
- **W-43 STANDS:** ReceiptSheet.kt:176 still prints "YOUR STUDY RECORD LIVES IN
  YPT"; the plate's W-13 companion is cured (ParkDrawer.kt:122 "STUDY HOURS: IN YPT").
- **W-41 STANDS:** `countHeldApps` (MonolithRepository.kt:1318–1319) is still
  `count { resolvedTier != OPEN }` — SHUT+PEEK flattened at birth capture (:1384).
- **UNA2-06 STANDS (both halves):** `floatingPeekPill` occurs only inside
  MonolithViewModel.kt (state, ticker, grant) — zero collectors/renders in any
  UI file. A taken peek shows no countdown anywhere and offers no early-end.
- Deck hero neighbour (Hale Cat. 2 observation): `primaryReadout`
  (TactileDeckScreen.kt:319) still per-block — 00:00 beside "BREAK · n MIN"
  during a Safe Break. Unruled; carried below.

## THE FOUR SESSIONS — WAVE-2 SECOND LOOK

- **PRUEFER (proof):** Delivered as a proof-*shape* audit: for each cure, a
  discriminating assertion that fails on proofbase (f1f24ea, DB v11) and is
  textually satisfied on merged. PROOF HOLDS: UNA-01/02/03/04/06(+guard)/07/08/09,
  UNA2-02, UNA2-03 main, APP-1, APP-2, ST-02, ST-03, breakTakenSeconds honesty.
  HALF-PROVEN: UNA-05, UNA2-06 (derivation only). NO PROOF BROKEN anywhere.
  Countersign for the times lane correctly refused — no composed migration
  exists to countersign. Caveat preserved: as committed, the merged test tree
  did not compile (issue #2 carries the repair); verdicts judged on the working
  copy incl. uncommitted fixes (5 files, +20/−8).
- **UNA (pass 2):** As filed 21 CURED · 3 PARTIALLY · 2 STANDS · 0 REGRESSED;
  FUGE's UNA2-04 narrowing moves the adopted tally to 20 · 4 · 2 · 0 (tenant
  half CURED/PROOF HOLDS; deck running cell PARTIALLY CURED, OFF-only re-key).
  NACHSICHT's leniency battery downgraded exactly two of Pruefer's grades
  (UNA2-01, SIL-3 → NO PROOF EXISTS at charge level); both adopted.
- **HALE (lapse check):** Joint report carries every lapse faithfully — in
  Lapses 5/6 it is *stronger* than Hale (adds cure/proof axis grades). Lapse 0
  (trunk-path contamination: `~/workspace/clone-inspect/Focus-app` overwritten
  by batch-9 lineage, HEAD 3ea05bd; true baseline proofbase) and Lapse 4
  (the car's own §5.1 verification gate never ran before shipment; this cell's
  pass is the first, after delivery) are the deepest items — process, not code.
  One unflagged correction: Hale's "6 of 30 items" vs the report's "7"
  (FT-1…FT-6 + ST-04) — the report's arithmetic is right; errata noted here.
- **REGISTER (spot-check):** Checked the **Small Things (ST) register only** —
  ST-02/ST-03 HONOURED (cured); ST-04 filed OUT OF SCOPE on patch-log evidence
  with his own warning that the registry reading re-files it SILENTLY DROPPED
  (joint report quotes him in full, adopts the registry reading: CURE ABSENT /
  STANDS); ST-01/05/06/07/08 out of batch-9 scope, batch-10 lanes named.
  **The wording register was never REGISTER's brief** — see W-29 below.

## FT-5 — INDIVIDUALLY EVIDENCED (lead; no wave-1 session ever named it)

FT-5 is the pill derivation table (TRUTH3-PROOF-STANDARD §4.4.5; merge map:
"FT-5, the pill derivation table, is the pill lane's — proven only on the
phone bench"). Verdict: **STANDS in its specified form; derivation-from-constant
present.** The derivation itself is in the merged tree and proof-holds
(`peekSecondsRemaining` from the stored lease end, VM; instrument
TruthCar3InterceptTest.kt:249 — UNA2-06's derivation half). But FT-5's table
prices the pill from the *setting*; no setting exists (no peekMinutes column,
§ Lead re-derivation), and the pill lane's own files are absent from the merged
tree. Same seam of loss as FT-1…FT-6: the MIGRATION_11_12 composition.

## GAPS IN THE VERIFICATION RECORD (wave-2 completeness audit)

1. **Checker-seat files absent:** SATZ/BELEG/FUGE/KANTE/SCHLUSS and
   DRUCK/NACHSICHT/EILE/BEQUEM/ZWEIFEL exist only as summaries in joint
   §§4/7 — their greps and FUGE's resolutions (which move the headline tally)
   cannot be independently verified from studio/verification/. Recorded, not
   fatal: three wave-2 bundles re-derived the load-bearing charges directly.
2. **UNA2-04 tenant proof string quoted nowhere** in any session verdict.
3. **"Eight units" NO-PROOF count** is 8 only if UNA2-07 + its guard count as
   one (UNA2-01, SIL-3, UNA2-05, UNA2-07+guard, APP-3, APP-4, UNA2-03 rider,
   UNA2-04 deck half); counted separately it is 9. Ambiguity recorded here.
4. **Pruefer main-source cite drift** (joint §4 errata stands): migration
   :192/:193 not :193/:194; sound `.commit()` at SoundEffectsSetting.kt:58;
   PARK sled ParkDrawer.kt:534–601; ParkSocialSitesRow defined :699–772;
   UNA-09 derivation RoutineDerive.kt:280. Every test-file cite exact (EILE).
5. **Two denominators, no bridge:** 26 charge rows vs the 30-item registry
   coexist in the joint report by its own admission. No verdict changes under
   either; the arithmetic gap is on record.
6. **Setup-sheet "IT FIRES AS SET" reprint** (AUDIT-setup-sheet S-2) was
   checked by no #5 session — a different surface from the cured plate line.
   Unchecked, carried below.

## WHAT #5 STILL OWES (the batch-10 carry list, verified)

1. **Times lane as 12→13** (v12 shipped): all five columns (peekMinutes,
   breakMinutes, breakSeconds, extensionSecondsAdded, extensionLog) composed
   against the merged files + S-3 provider rewire + plate PEEK-line re-point
   (Lapse 7) in the same change; then UNA re-audit + Pruefer countersign with
   EditableTimesTruthTest transplanted; SIL-2 and FT-5 re-judged.
2. **Receipt false prints:** W-43 footer (RECORD → HOURS, one word) and W-41
   HELD split (split stored at birth, rides 12→13; or Ren's ruling for the
   "UNDER THE SEAL" fallback). Both STAND / NO PROOF EXISTS today, on the
   one surface Sarthak loved and the only permanent one.
3. **UNA2-06 surfaces:** in-app render (written by nobody — cheapest wow in
   the backlog: the state machine waits in the VM) + pill overlay merged or
   formally struck; render proof owed with it.
4. **No-fire closure (UNA-05/W-11):** Wache F1–F5 in the same build AND a
   live fire re-verified on the phone before any "FIRED AS SET" restoration.
   Plus the unchecked setup-sheet reprint (Gaps 6).
5. **Proof obligations for cures already in tree:** UNA2-01 end-state test
   (PeekLeaseForUnlistedPackageArmsNothing), SIL-3 derivation/render
   assertions, UNA2-04 strip-scenario test, UNA2-05, UNA2-07 (+guard),
   APP-3, APP-4, UNA2-03 ENDS rider — plus #11's cooldown-at-0 frame
   (ISSUE-11-WAVE2-REPORT).
6. **Trunk hygiene (Lapse 0 residue):** ruling on which path is trunk now,
   name + date for the overwrite in the log, briefs re-pointed to
   truth-3-proofbase by name, proofbase frozen/tagged before the same
   accident can recur.
7. **Issue #2's committed test-tree repair** and the `una06` fixture
   correction (expectation → the shorts URL it evidently meant).
8. **REGISTER re-run** against the batch-10 composed tree, ST-04 first;
   ST-07 needs a builder seated by name (its cure exists only as look-spec).

## GENUINELY CLOSED ON PAPER

Cure + proof both hold: UNA-01, UNA-02, UNA-03, UNA-04, UNA-06 (+guard),
UNA-07, UNA-08/W-13, UNA-09, UNA2-02, UNA2-03 (main), APP-1, APP-2, ST-02,
ST-03 (call-level), breakTakenSeconds honesty. Cure closed, proof open:
UNA2-05, UNA2-07, APP-3, APP-4, SIL-3, UNA2-03 rider. Words closed, machinery
open: UNA-05. Process: Lapse 0 diagnosed with baseline re-established;
Lapse 4's missing verification supplied by this cell, after delivery, stated
plainly. Ren's five merge-time fixes: no lapse on any (Hale Cat. 2).
**No PROOF BROKEN proof anywhere — batch-9's failure mode is absence
(a lane, two renders, eight proofs), not repainted evidence.**

## DECISION-FOR-SARTHAK

- **W-29 ratification (the register's one unchecked collision):** Hale's
  wording book KEPT "EMERGENCY PEEK" (cure = badge only); Tor's #11 audit
  overturned the KEEP (chip = "PEEK" + derived badge, price re-timed) and
  the #11 branch implements the overturn. No verification seat ever checked
  the collision; no ratification is on record in this cell. **Ren to ratify
  or reject the overturn** — recorded here so Sarthak hears it as a decision
  item, not a silent default. Until ratified, batch-9 ships the KEEP and
  batch-10 (carrying #11) would ship the overturn, undecided.
- **Deck hero during a Safe Break:** primaryReadout shows 00:00 beside
  "BREAK · n MIN" — pre-existing, arguably scoped out by the span law,
  never ruled. Wants a ruling, not silence (Hale + Interaction-detail).
- **Honest label for batch-9** (Purpose-questioning): "Truth Car 3 — truth
  fixes landed; pill, editable times, and ST-04 still open." The unqualified
  "Truth Car 3 delivered" repeats, in miniature, the sin the car exists to kill.
- **`startPeekLease` ungated public writer:** UNA2-01's guard lives entirely
  in the VM; any future direct caller reopens the S1 shape. Hardening is a
  ruling for the merge, not a verification act.

## SEAT SECTIONS (30)

### Finishers ×5 (verification finishers; wave-1 checker names, work re-done in wave-2 bundle)
- **Finisher-1 — SATZ — inventory complete.** §A–C charges compiled from UNA +
  Pruefer tables, cross-checked against joint §3. No SATZ file in folder — thin
  on primary evidence, aggregation only. — SATZ
- **Finisher-2 — BELEG — times absence re-derived** from 4 independent session
  greps/migration cites; contamination baseline confirmed in all 4 files. No
  BELEG file in folder — thin. — BELEG
- **Finisher-3 — FUGE — flagged.** His §3 resolutions move the tally 21→20;
  his own file is absent, so the shift rests on joint §3's assertion. — FUGE
- **Finisher-4 — KANTE — receipt charges inventoried.** W-41/W-43 are
  single-session (Hale) and outside the 26 rows — flagged, and lead-corroborated
  this wave by direct grep (see Lead re-derivation). — KANTE
- **Finisher-5 — SCHLUSS — counts reconciled.** UNA 21·3·2·0 vs adopted
  20·4·2·0; 0 REGRESSED in both. — SCHLUSS

### Feel specialists ×5
- **Feel-1 — DRUCK — PASS.** 0 laundering tells, 5 cleared; nits = cite drift +
  implicit entity-order check. Summary-only in folder — thin. — DRUCK
- **Feel-2 — NACHSICHT — PASS.** Leniency downgrades (UNA2-01, SIL-3) are
  load-bearing and adopted; NO-PROOF group checked for softened regressions — none. — NACHSICHT
- **Feel-3 — EILE — PASS.** Errata list is his; no cite reverses a verdict;
  every test-file cite exact. — EILE
- **Feel-4 — BEQUEM — PASS with narrowings.** 8 LINK HOLDS, 2 PRESENCE ONLY
  (UNA-01/02 board word rides VM-fed WatchStore mirrors, not Room; UNA2-04
  re-key OFF-only) — both adopted in joint §3/§7. — BEQUEM
- **Feel-5 — ZWEIFEL — PASS.** Falsification attacks withstood on UNA-01,
  UNA-03, FT absence; UNA2-04 in part. Surfaced the 260ms displayedShorts lag
  and the caller-less SessionSealTransitions.peek() trap. — ZWEIFEL

### The 20 named types
- **Crazy-ideas — proposal filed.** Render the pill's first half inside the
  Airlock overlay (the one surface guaranteed on screen at peek start); reuse
  peekOfferFor's pure pattern for any future extend offer. — Crazy-ideas
- **Maximum-implementation-potential — PASS on diagnosis.** Editable times is
  the highest value per line: every seam already cut (breakLengthSeconds is
  the single reader; PEEK_OFF already arms for 0). The columns never landed. — MIP
- **Brutal critic — the car arrived with its headline freight missing.** Six
  cures real; pill + times + ST-04 absent. Do not let extendActiveTaskOvertime
  be cited as ST-04 — it extends a to-do item, not a block. — Brutal critic
- **Improver — next slice ordered.** (1) Collect floatingPeekPill in-app;
  (2) breakSeconds column + re-point the one reader; (3) ST-02 test asserting
  the taken-seconds booking. — Improver
- **Designer — debt is absence, not ugliness.** Cured surfaces print truthfully;
  the peek pill — the element the ritual is named around — was never drawn. — Designer
- **Consumer — 6 of 9 promises in the box.** Sound/shorts/backup honest now;
  no editable lengths, no extend, no pill timer. Verdict matches the table,
  not the car's name. — Consumer
- **Student — the felt fixes are ST-02 and UNA-01;** the missing ST-04 hurts
  most in his pattern — a flowing block still cannot be extended. — Student
- **User — verbs checked by hand:** END BREAK, sound, shorts, per-door offer
  all truthful; extend-block and pill not there. — User
- **User-experience — the peek journey ends in a void at the grant;** the
  task-estimate extension is a UX trap a user in flow will misread as
  extending his block. — User-experience
- **Purpose-questioning — delivery claim partially false.** Honest label
  proposed (DECISION-FOR-SARTHAK above). — Purpose-questioning
- **Thoughtful — Hale carried faithfully,** hardened in Lapses 5/6; the §1
  headline under-counts shipping false prints if read alone (disclosed in §1). — Thoughtful
- **Deep-thinker — Lapses 0 and 4 are the deepest:** institutional, not
  technical; Lapse 0 is diagnosed, not closed, until trunk hygiene (owed 6) lands. — Deep-thinker
- **Enthusiastic designer — UNA2-06 is the saddest item in the car:** good
  derivation spent on nothing visible; cheapest wow in the backlog. — Enthusiastic designer
- **Wow-level designer — the loved surface is the one still lying:** the
  receipt is permanent and kept; two false prints on it outweigh blandness
  anywhere else. — Wow-level designer
- **Designer, 29 years — W-29 verdict: OPEN.** Two authorities decided
  oppositely; nobody re-checked; ratification owed (DECISION above). — Designer-29y
- **Intentionality-focused designer — accident is not a cure:** Lapse 7 prints
  true only because times failed to land; W-11 plate sound, setup-sheet
  reprint unchecked. — Intentionality
- **Synchronous collaboration — agreement unusually strong** (4 sessions, 4
  methods, one dissent quoted and ruled, not buried); W-29 fell *between*
  the sessions — a collaboration failure, not an evidence failure. — Synchronous collaboration
- **Implementation-quality — Pruefer delivered what was statically
  deliverable,** including refusing the times countersign; the uncompiled
  test-tree caveat preserved. — Implementation-quality
- **Technical execution specialist — six items are structurally undeliverable
  without compiling/phone:** any green run, the +20/−8 compile question,
  UNA-05's live fire, ST-03 audibility, UNA2-06 overlay half, the times
  Room chain. Everything else owed was delivered. — Technical execution
- **Interaction-detail specialist — break interactions are the car's
  cleanest;** peek interactions truthful at the door, void after the grant;
  seconds Hale on the Deck-hero ruling. — Interaction-detail

## GITHUB COMMENT (attempted via connector; exact text if blocked)

"Wave-2 — Tor, cluster E. Verification COMPLETE on paper: the four owed
sessions (Pruefer proof, Una pass 2, Hale lapse check, Register spot-check)
cross-checked by 30 wave-2 seats + lead re-derivation. 0 REGRESSED. Cure
tally 20 CURED · 4 PARTIALLY · 2 STANDS (times family + ST-04) over 26 rows,
plus W-41/W-43 STAND on the receipt. FT-5 individually evidenced this wave
(stands in specified form). W-29 ratification owed — Hale's KEEP vs Tor's
#11 overturn, unchecked by any session until now; filed in
studio/plan/ISSUE-5-WAVE2-REPORT.md with the full batch-10 carry list.
DECLARED, uncompiled. — Tor"

## NEXT (Law 10)

(a) Tor wants: Konto's batch-10 plan to carry the §Owed list by name —
12→13 times composition, W-41/W-43, UNA2-06 render, and the proof
obligations — and Ren's W-29 ratification before the merge composes #11.
(b) Tor could do, unassigned: freeze/tag proofbase and write the trunk
overwrite's name-and-date entry for the log (Lapse 0 residue), and draft
the PeekLeaseForUnlistedPackageArmsNothing end-state test for UNA2-01.

— Tor, cluster-E lead, issue #5 (wave 2)

## ADDENDUM — delivery state under the gh/git route (Oct 30, 04:10 IST)

Course correction (Ren, Sarthak's order): the GitHub connector is retired —
gh CLI / git only. Reconciliation: remote bb2393f (wave-1) + 5a82872 (this
report, landed via the connector *before* the correction) vs local fa38861 —
tree diff after `git fetch`: **empty — PUSHED-EQUIVALENT**. Local branch
reset to the remote tip (no content lost) and this addendum is the first
commit pushed by plain `git push`. The signed issue comment was posted
before the correction (comment 6510087901, correction 6510150454); NOT
re-posted via gh, to avoid a duplicate — exact text stands above. — Tor
