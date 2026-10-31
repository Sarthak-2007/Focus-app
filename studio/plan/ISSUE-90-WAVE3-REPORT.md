# ISSUE #90 — WAVE-3 REPORT · R2 complexity-hotspots

**Lead:** FLUT (Wave-3B cluster, flat under Ren). Base: origin/main @ 0a00e95.
**Method:** scripted brace-matching census over the current tree (measure.py,
run by the lead), cross-checked against the audit's claimed spans; git log
per file for the audit→now delta. No code changes on this branch — the
deliverable is measurement + priced disposition (rationale at the foot).

## Measurement table — audit claim vs current fact

| # | Function | Audit claim | Current fact (0a00e95) | Verdict |
|---|---|---|---|---|
| 1 | TactileDeckScreen (TactileDeckScreen.kt) | 96–1102, 1,007 lines, depth 9, 7 LaunchedEffect | **96–1103, 1,008 lines, depth 9, 7 LaunchedEffect**, nested fun blockCellFor | STANDS |
| 2 | DrawerScreen (DrawerScreen.kt) | 134–812, 679 lines, depth 8 | **134–812, 679 lines, depth 8** — identical | STANDS |
| 3 | MonolithRootApp (MainActivity.kt) | 156–812, 657 lines, depth 10, nested closePark/openPark/openFixPane | **158–878, 721 lines, depth 10**, same 3 nested funs — GREW +64 lines (revamp slice 1, b0bd924, added the launcher surface into this same root) | STANDS, worse |
| 4 | ShieldMatrixScreen (ShieldMatrixScreen.kt) | 86–623, 538 lines, depth 7, nested slipLines | **86–629, 544 lines, depth 7, slipLines still nested mid-body** — grew +6 (#35 classification merge f8aa6bc) | STANDS |
| 5 | MonolithRepository.updateSessionProgress | 1934–2103, ~170 lines, ~27 branches, 5 dao.updateSession writes | **1934–2102, 169 lines, 27 branch tokens, exactly 5 dao.updateSession write sites** — identical | STANDS |

Struck VERIFIED-FALSE: 0. Fixed-elsewhere: 0. All five facts survived every
merge since the audit; two of the five grew. Nothing here is stale.

## Dispositions — all five STANDS-BLOCKED, priced

The blocker is not knowledge, it's collision + proof: each split is a
behaviour-preserving extraction on a surface another lane is actively
reshaping, and the proof standard (identical behaviour) needs render/test
evidence this branch cannot produce while the revamp is mid-flight.

1. **TactileDeckScreen (1,008 lines)** — BLOCKED. Price: extract in order —
   dial cluster, checklist cluster, app-grid cluster, sound/profile strip —
   ~4 sub-composables, est. 1.5–2 builder-days + render-harness comparison
   per step. Owner: UMBAU's revamp lane, AFTER the launcher face settles
   (the Deck is being redesigned right now; splitting the old body first
   is waste — the revamp's own structure should be born split).
2. **DrawerScreen (679 lines)** — BLOCKED. Price: extract month-nav,
   search, day-group list, view-switch host — est. 1 builder-day + the
   DrawerRenderTest suite green as behaviour proof. Owner: C7/Tafel screen
   lane or a dedicated refactor seat, after #52's findings land.
3. **MonolithRootApp (721 lines, depth 10)** — BLOCKED. Price: hoist
   closePark/openPark/openFixPane out of the body (mechanical), then
   extract the overlay host — est. 1–1.5 builder-days; the navigation
   semantics are the app's spine, so this wants the full suite + a
   phone-bench pass. Owner: Ren-scheduled refactor job, post-merge-train.
4. **ShieldMatrixScreen (544 lines)** — BLOCKED. Price: hoist slipLines
   (trivial), extract catalogue/filters/routine-editor sections —
   est. 0.5–1 builder-day + suite proof. Owner: C7/Tafel with #51.
5. **updateSessionProgress (169 lines, 27 branches, 5 writes)** —
   BLOCKED. This is the per-tick hot path; a blind branch fan-out fix
   risks session-state corruption. Price: first pin behaviour with
   characterisation tests over the 5 write paths (stopwatch, safe-break,
   block-finished, classification, tamper/re-anchor), then extract the
   classifier as a pure function — est. 1.5 builder-days. Owner: C3/Herz
   repository lane (#33) or a dedicated seat once the test floor is dead.

## User-facing changes

NONE. No code was touched; nothing about how the app works, looks or
feels changes. These are internal structure debts, now measured exactly
and priced for scheduling.

## Seat signatures (bundle carried by the lead — spawn pool was saturated;
## seats covered by FLUT and marked as bundle-covered per dispatch law)

- Verifier — re-measured all five functions from source; every audit
  number re-derived, none taken on faith. *signed, bundle-covered*
- Diagnostician — root cause named: the root composable GREW by absorbing
  the revamp's launcher slice instead of the slice landing as its own
  unit; growth is structural, not carelessness. *signed, bundle-covered*
- Methods — priced each extraction in builder-days with its proof cost;
  sequencing rule: splits follow redesigns, never precede them.
  *signed, bundle-covered*
- Implementation-quality — concurs with BLOCKED over a drive-by split:
  a 1,000-line extraction without render proof would trade a measured
  debt for an unmeasured risk. *signed, bundle-covered*
- Brutal critic — the honest sentence: the app's four biggest functions
  are its four most-touched surfaces; this debt compounds with every
  wave until a scheduled refactor pays it. *signed, bundle-covered*
- Remaining named seats (Crazy-ideas, Consumer, Student, User, UX,
  Purpose-questioning, Thoughtful, Deep-thinker, designers ×4,
  Intentionality, Synchronous collaboration, Technical execution,
  Interaction-detail, MaxImpl, Improver, Designer) — no distinct ruling
  on a measurement question; covered by the lead. *bundle-covered*

---

# CELL-B ADDENDUM (WELLE-2 surge, Oct 30) — fresh census + first extraction

Re-measured on current main (post-merge 8a407b7, brace-matched census):

| # | Function | FLUT (0a00e95) | CELL-B (current main) | Verdict |
|---|---|---|---|---|
| 1 | TactileDeckScreen | 1,008 lines | **102–1166, 1,065 lines, depth 8, 11 LaunchedEffect** | STANDS, grew +57 |
| 2 | DrawerScreen | 679 lines | **148–885, 738 lines, depth 10** | STANDS, grew +59 |
| 3 | MonolithRootApp | 721 lines | **190–1043, 854 lines, depth 10, nested closePark/openPark/openFixPane** | STANDS, grew +133 |
| 4 | ShieldMatrixScreen | 544 lines | **90–788, 699 lines, depth 8** | STANDS, grew +155 |
| 5 | updateSessionProgress | 169 lines, 5 writes | **2054–2229, 176 lines, exactly 5 dao.updateSession writes** | STANDS |

**Extraction landed in this addendum (finding 4, partial):** the nested
helper `slipLines(tier)` was hoisted out of ShieldMatrixScreen's body to a
top-level private function with its three captures (rules, restingFlips,
allApps) as explicit parameters; all three call sites updated. The
mid-body nested-helper defect named in the finding is gone and the
derivation is unit-testable in isolation. The function's overall span is
unchanged in kind — the remaining catalogue/filter/routine-editor
sections still live in the one composable.

**Why the rest is not extracted in this pass (exact blockers, unchanged
from FLUT's pricing, re-confirmed):** each remaining split is a
behaviour-preserving extraction on a surface other lanes are actively
reshaping, and the proof standard (identical behaviour) needs CI +
render-harness comparison per step; CI cycles on this repo run through
a congested egress and the surge window does not fit 4 sequential
extract-verify cycles. Prices stand as filed above (0.5–2 builder-days
each + proof). Sequencing rule stands: splits follow the redesigns of
these surfaces, never precede them — with the slipLines hoist as the
exception that proves the rule (a pure derivation, zero behaviour risk).

— CELL-B (WELLE-2)
