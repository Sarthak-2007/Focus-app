# ISSUE #14 — WAVE-2 REPORT: Setup Sheet + onboarding merge (Empfang + Pforte)

**Lead:** Pforte, cluster G, flat under Ren. **Branch:** `agent/issue-14`.
**State at Wave-2 open (lead-verified):** the brief's "uncommitted changes"
were already committed by Wave-1 as `9858a0b` (MainActivity +268,
ParkDrawer +117, ReceptionSheet.kt 1279 lines, SetupStore.kt,
ReceiptSheet.kt, ScreenshotHarnessTest +47, ReceptionSheetTest,
SetupMechanismsTest). Working tree was clean. Wave-2 therefore AUDITED
the committed graft and fixed what the audit caught.
**Everything DECLARED, uncompiled. Nobody compiled; nobody ran gradle.**

## LEAD VERDICT

- **S-1 is cured in code, lead-verified.** `receptionTotal`
  (ReceptionSheet.kt:215) prices EVERY lack per-kind in COUNTED order;
  the STALE sentence (kt:213) asserts proof age only, never "IS SET";
  count = fresh-GRANTED only. Forbidden sentences survive only inside
  comments recording their striking (kt:137, :194, :307) — no printed
  string carries FIRES AS SET / MID-SESSION / UNBREAKABLE.
- **Wave-2 caught a live S-1-class defect in the TESTS (fixed, below).**

## FIXES APPLIED BY LEAD (this wave, DECLARED)

1. **`totalMissingBranchWinsOverStale` corrected + renamed**
   `totalPricesStaleAndMissingEachInItsOwnKindsWords`
   (ReceptionSheetTest.kt). The old test codified the pre-C-10
   branch-wins rule — any MISSING erased every STALE door — contradicting
   C-10, the implementation, and the sibling mixed-lack test at :275.
   As written it would have FAILED on first run and, worse, taught the
   old lie. It now expects both lacks priced in COUNTED order
   (aged-proof sentence first, absence sentence second), count 2.
   Independently found by three seats (Brutal critic, Deep-thinker,
   Synchronous collaboration) and by the lead.
2. **Buried-contradiction guard added** (same file):
   `totalNeverContradictsItselfAcrossAllDoorStateCombinations` sweeps
   all 4^4 = 256 counted-door print combinations and pins: null count
   ⟺ still-being-read sentence; printed count = fresh-GRANTED count.
   Lead-verified against `WatchStore.permPrintAt` semantics.
3. **S-8 marked in code** (ReceptionSheet.kt, THE ROOM beat): an
   `INTERIM FALSEHOOD — DO NOT SHIP SILENTLY` comment now sits on the
   beat itself, so a code-only review cannot miss what MERGE-PLAN item 6
   already flagged. The line itself is NOT re-worded here — that act
   belongs to the #13+#14 composition.

## REMAINDERS (not fixed, with reasons)

- **S-8 ROOM beat** — interim falsehood until #13's settle machinery
  composes (no 20 s settle, desk clock prints one figure not three).
  Composition gate, named above and in MERGE-PLAN item 6.
- **S-3 value slots** — PEEK/BREAK beats print flat "two minutes" /
  "Ten minutes" (kt:684/689), TRUE today against hard constants
  (120 s / 600 s); slots land with MIGRATION_12_13 in batch-10.
- **S-12 rider** (enabled-but-unbound words) — the WatchStore sweep
  never calls the distinguishing query (MonolithRepository:682);
  printing its words would assert an unmeasured state. Repository work.
- **Ledger consumer (a)** — Deck refusal slot's "· LEFT AT SETUP." is
  unwired; the slot is issue #6's surface, wiring collides at composition.
- **Consumer trap (new, Wave-2, B-cluster Consumer seat):** an ESCORT
  door's REPAIRED chit can never print — ESCORT verdict travels via
  PARK board/H-2 wayback, REPAIRED via card chit, and GRANTED doors
  collapse (F-6) hiding the chit row. Needs a Fibel ruling at
  composition: verdict line inside the collapsed row, or ESCORT-class
  chits fall back to the PARK board line.
- **H-2 pin is flag-only** (SetupMechanismsTest has no H-2 test;
  `shieldExcursion` has zero test callers) — host-composition test
  rides the merge, per Wave-1 plan.
- **Tear/DONE numbers unpinned by any test** (180/260/420, dip,
  specA/specB fields) — regression-invisible until Ren's run;
  Robolectric timing assertions are the wrong tool; device/wall-clock
  check at the merge is the honest instrument.
- **Whole-case handover test asserts the PARK echo verdict-only** —
  the Guard-slot half of the whole case is not composition-tested.

## DECISION-FOR-SARTHAK

- None inside #14. (The ROOM beat's final wording is a #13-composition
  act under Ren's creative control, not a Sarthak decision.)

## SEAT SECTIONS (30 — 5 finishers, 5 feel, 20 named types)

Condensed by the lead to verdict + load-bearing evidence; full seat
texts were delivered to the lead and their findings are carried above
or below. No seat compiled or ran anything.

### Finishers
- **Finisher-surface — FIX-FIRST.** S-1 PASS in code (kt:215, :213,
  test :242). S-8 is the one printed falsehood (kt:675-682). S-3 flat
  minutes TRUE today. F-6 collapse is a `when`-order, not a fade.
- **Finisher-motion — FIX-FIRST.** No settle state exists in this tree
  (`settled = fraction.value == 0f`, kt:427 — sheet geometry only);
  S-8's 20 s and the chit's 3 s deadline are different clocks, neither
  is the Room's. Tear total 420 = 180+260 in comments only.
- **Finisher-light — FIX-FIRST (S-8 only).** FIX takes surfaceVariant,
  ink on DONE alone (F-4 closed). No light state anywhere keys on
  STALE — the aged door and the fresh door are lit identically.
- **Finisher-sound/haptics — PASS with flags.** DONE click is judged
  (stamp→pulse, reprint→click, kt sound law closed in Wave-1); CHECK
  knocks with FIX's call, no dip. No haptic channel exists in the sheet.
- **Finisher-typography/print — FIX-FIRST (record only).** Beats print
  as a ruled table (84dp label column); total count + Guard foot +
  PARK stub in Anonymous Pro Bold 15. (Seat's "17sp role" claim:
  LEAD-UNVERIFIED — Type.kt roles read 15sp at :108/:118/:126.)

### Feel specialists
- **Feel-weight — LEAD-COVERED summary of cluster returns:** the sheet's
  weight is in DONE's dip-that-does-not-return on a stamping commit;
  a drag-off stamps nothing (release law) — the heaviest honest feel
  in the merge.
- **Feel-light — LEAD-COVERED:** granted doors collapse to one ruled
  37dp line; the chit grows it for one printing only.
- **Feel-timing — LEAD-COVERED:** rise 280 SEAT, tear exit 420, other
  leaves 160 THROW; chit deadline named 3 s (the 800 ms race is dead).
- **Feel-touch+sound — LEAD-COVERED:** feel on DOWN, act on
  release-true, on every key; OPEN THE LIST gate knock 14ms @0.50/0.30.
- **Feel-instant-read — LEAD-COVERED:** header keeps naming the first
  thing missing (S-11); total can no longer contradict the cards (S-1).

### The 20 named types
- **Crazy-ideas — PASS.** Proposals (spec+price in seat text): proof-age
  printed as a number ("PROVED 46 S AGO") instead of the STALE word;
  a physical tear-off chit the user keeps. Neither landed — proposals.
- **Maximum-implementation-potential — FIX-FIRST.** Highest-leverage
  remainder is the S-12 rider: one sweep call to the existing
  distinguishing query unlocks truthful stuck-state words.
- **Brutal critic — FIX-FIRST.** Headline charge was the contradictory
  test (FIXED by lead, fix #1). Also: BUILD-log §6's privacy lines are
  a stale record — LEAD-VERIFIED the current code prints only the
  verifiable halves ("MONOLITH HOLDS NO NETWORK PERMISSION.",
  "HELD ON THIS PHONE, IN THE DIGEST.", kt:143-157); the seat's
  "overclaim still printed" charge is SUPERSEDED BY CODE.
- **Improver — FIX-FIRST.** Add the 256-combination guard (DONE, fix
  #2); pin `doorsMaskAt` STALE-bit behaviour by name in tests.
- **Designer — PASS.** Composition closed in Wave-1 (F-4/F-5/F-6/F-7);
  the sheet reads as one instrument, not four forms.
- **Consumer — FIX-FIRST.** Found the ESCORT/F-6 chit trap (carried in
  Remainders). First-open at 04:00: the leave line (S-11) is the
  sentence that makes the sheet survivable.
- **Student — PASS.** Door jobs lead with rank words (interceptor /
  witness / anchor / mail room) — learnable in one read.
- **User — PASS.** DONE is live at any door state; no trap, no nag.
- **User-experience — FIX-FIRST.** The ROOM beat (S-8) is the only
  place the sheet teaches a future the tree doesn't have.
- **Purpose-questioning — PASS.** Every sentence on the sheet answers
  "what does this door do in his life" (M-3); spec-speak is dead.
- **Thoughtful — FIX-FIRST.** Chit expiry (24h DARK, C-15 print-against
  law) is the most considered mechanism in the merge; its ESCORT hole
  (Consumer) is the unconsidered corner.
- **Deep-thinker — FIX-FIRST.** Independently derived the test
  contradiction from C-10's text vs the test's comment.
- **Enthusiastic designer — PASS.** The tear + stamp is the sheet's
  signature; keep it loud.
- **Wow-level designer — FIX-FIRST.** S-8 again: a wow surface cannot
  narrate a room that doesn't exist yet.
- **Designer-29-years — FIX-FIRST.** In 29 years the pattern is:
  the total that contradicts its cards is the bug users photograph.
  S-1's cure is correct; the test that codified the lie was the
  remaining liability (fixed).
- **Intentionality-focused designer — PASS.** Every deviation in
  Wave-1 was recorded, none silent (tear release law, F-6 sequencing).
- **Synchronous collaboration — FIX-FIRST.** Proposed the
  256-combination guard (applied, fix #2) and named the cross-issue
  composition gates (S-8→#13, S-3→batch-10, consumer (a)→#6).
- **Implementation-quality — FIX-FIRST.** `doorsMaskAt` is
  fresh-GRANTED-only by construction (C-14 evidence standard met by
  derivation); stamp is write-once in SetupStore.
- **Technical execution specialist — FIX-FIRST.** Test count: 17
  ReceptionSheet + 11 SetupMechanisms + harness; the harness's
  fabricated routine line was cured in Wave-1 (C-03). Unrun, all.
- **Interaction-detail specialist — FIX-FIRST.** Dip rides the chit
  state (Haut §6 condition 2 — same state the line prints from);
  FIX/CHECK share the 160/160 visibility pair on PARK's stub.

## NEXT WANTS (lead, unassigned)

- At #13+#14 composition: re-word or strike the ROOM beat; rule the
  ESCORT chit trap; wire ledger consumer (a) on #6's surface.
- At batch-10: S-3 value slots with MIGRATION_12_13; S-12 rider via
  the repository query.
- On Ren's word only: first compile + run of this branch's tests.

— Pforte, cluster-G lead, Wave-2, issue #14. DECLARED, uncompiled.

## GITHUB COMMENT (fallback text — route note as in the #16 report)

> **Wave-2 — issue #14 (Pforte, cluster G)**
> S-1 verified cured in the committed graft (9858a0b): receptionTotal
> prices every lack per-kind; the aged-proof sentence asserts age
> only. Wave-2 caught the same lie living in a test —
> `totalMissingBranchWinsOverStale` codified the pre-C-10 erasure —
> corrected and renamed, plus a 256-combination contradiction guard
> added, and the S-8 ROOM beat marked INTERIM FALSEHOOD in code
> (composition gate with #13, unchanged). All DECLARED, uncompiled.
> Full report: studio/plan/ISSUE-14-WAVE2-REPORT.md on this branch.
> — Pforte
