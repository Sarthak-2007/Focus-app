# Issue #7 — Shield pass: LEAD SYNTHESIS & RULINGS (VOGT)

Cell: VOGT (lead) · FORM (designer) · FUNKE (innovator) · SALZ (critic) ·
MONTEUR (builder) · finishers PANZER / GLANZ / SAUM / FASSUNG / POLITUR ·
feel HAUT / KLANG / TAST / SCHWUNG / LUFT. Branch `agent/issue-7` from
main `8438f5a`. **DECLARED throughout: nobody compiled, no tests run.**

## What landed (MONTEUR, verified by VOGT against the tree)

1. **The status sentence** — new pure derivation
   `ui/ShieldBlockingStatus.kt`, fed by a `shieldBlockingStatus`
   StateFlow (1s ticker + Watch/session emissions), printed as item 0
   of Shield (`testTag shield_blocking_status`). Plus 17 truth-table
   tests, written, not run.
2. **R-1 de-duplication** — Standing Orders slip lines, their tap-flip
   path and all RestingFlip machinery deleted (~200 lines); tally is
   counts-only; the catalogue is the one enumeration, grouped by
   ruling via `groupedForCatalogue()` (PEEK → SHUT → OPEN).
3. **ACTIVE → ARMED** in the ROUTINES header (liveCount unchanged).
4. **WON'T FIRE** — pure `routineCapWord(enabled, daysMask)`; the cap
   itself prints it, middle density, payload-widened. RUNNING NOW on
   routine rows derives from the active session's `routineId`.
Diff: 6 code files, +227/−317, plus 2 new files and this cell's
14 records. The ±30-minute chips are untouched — **delegated to
Voss's batch-10 time wheels (R-8)**, unanimous across the cell.

## Lead rulings on the cell's conflicts

- **Proof source (the headline ruling).** R-5 as filed in my own
  audit was underpowered — FORM, FUNKE and SALZ (C-1…C-4) convicted
  it independently: `isSessionActive` is a persisted row flag, an
  input to enforcement, never evidence of it. The landed derivation
  is adopted: BLOCKING NOW requires **both cascade witnesses for
  this session id** (shield `protecting` + a11y `armedSessionId`),
  with beat staleness under the repository's own 10s gap law.
  Residual, recorded not hidden: the shield's polling door also
  needs usage access (SALZ C-2) — a third conjunct is a named
  follow-up for Ren's merge call; FUNKE's root-stamp link is the
  named strengthening (his Mechanism A in full).
- **Freshness law.** The 10s gap law governs this sentence.
  WatchStore's 45s is the permission-board threshold; it does not
  apply here. FUNKE's ≤10s proposal and the landed law agree.
- **Vocabulary.** MONTEUR's forms stand (`NOT BLOCKING — …
  ENFORCEMENT NOT PROVEN` / `… SHIELD PROOF STALE` /
  `ENFORCEMENT NOT PROVEN — NO WITNESS READ YET`). FORM's
  `CAN'T CHECK BLOCKING` stays Deck's warrant sentence — one
  surface, one question, one word. SAUM's copy table governs any
  future wording edit.
- **RUNNING NOW.** Session-keyed (landed) beats window math —
  SALZ C-5 answered, FUNKE's OPEN_RUNNING point converges.
  FUNKE's `RoutinePrintState` is the named refactor if a PAUSED
  print is ever added.
- **ARMED collision (SALZ C-9).** Dissent recorded; the audit's
  R-6 ruling stands for this pass. Ren may revisit at a wording pass.
- **Sticky sentence (SALZ C-7).** Not in this pass. LUFT's answer
  is adopted as the standing alternative: the session-night ground
  is the travelling proof; if the phone proves otherwise, GLANZ's
  sticky-header construction is already specced.

## The finish/feel layer — one named follow-up

Not applied to code in this pass. The five finishers' patch lists
and the five feel specs converge on one job: **the Shield finish
pass** (≈½–1 builder-day, Ren's call: on this branch or at the
batch-10 merge). Its item #1, convicted by four seats with
measurements: the landed status line paints every not-blocking
state in `scheme.error` (3.49:1 by day) — the fix is full ink in
all states, state carried by verb + 10dp solidity glyph (PANZER /
SAUM / POLITUR / LUFT, one voice). Also in the follow-up: fixed
40dp status slot + 260ms Curve-A whole-line re-strike + 250ms
anti-flap coalesce (PANZER / SCHWUNG), payload cap grammar and
Sora eviction on routine rows (PANZER), GLANZ P-1…P-9 (tally
jumps, sticky group headers, PEEK contract line), FASSUNG's
semantics pack (P-3 sled self-description is its core), and
LUFT A-1/A-3 (session-night keyed on proven LIVE; stepped night
for sleds; symmetric exit step).

**Refusal sound — ruled for the follow-up:** no sound changes in
this pass. TAST's print-only rule governs Shield row refusals
(R-7's letter); KLANG's quiet seat-knock dialect is recorded for
wheel-clamp / rejected-add cases only, pending Ren. KLANG F-1
(`applySmartLoadout` firing the Throw's body for a non-seal) and
TAST T-1 (refused sled throw voicing a fake commit) are filed as
defects for that same follow-up, not this branch.

## Acceptance state against issue #7

- [x] Shield prints whether blocking is live — with proof, not a flag
- [x] Standing Orders de-duplicated (R-1 core: one enumeration)
- [x] ACTIVE → ARMED
- [x] daysMask=0 → WON'T FIRE
- [x] ±30 chips — DELEGATED to Voss's time wheels, marked, not hacked

Unproven until Ren's compile and the phone: everything above is
paper-verified (full re-reads, grep checks, brace balance). The 17
new tests have never run.

— VOGT (lead), issue #7 cell, Oct 30 2026
