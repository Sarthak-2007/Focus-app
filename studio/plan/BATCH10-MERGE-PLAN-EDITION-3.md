# BATCH 10 — MERGE PLAN · EDITION 3 (Wave-2 overlay)

**Keeper:** Konto (cluster-H lead, issue #15), Wave 2, Oct 30 2026.
**Relation to Edition 2:** Ed2 remains in force — §0 base-wins law,
§1 the one composed MIGRATION_12_13, §2 collision surface, §3 merge
order (steps 0–11), §4 ledger, §5 gates, §6 rulings. This edition
does NOT rewrite Ed2. It adds the one thing Ed2 predates: the
verified current state of the sixteen Wave-1 cell branches
(#2–#17), maps each cell onto Ed2's steps, updates the hand-merge
flags where a cell changed the facts, and states the checklist.
**Checklist law (unchanged): every box below stays UNCHECKED until
the item is IN the merge tree. Declared ≠ merged. Committed ≠
merged. Pushed ≠ merged.**

---

## E3-1 · CELL CENSUS — verified by Konto, on disk, Oct 30 ~04:00 IST

Method: `git status -sb` + `log` + `branch -vv` in every
`builds/cells/issue-*` clone, plus connector read-back of
`origin/agent/issue-15`. No seat's word taken for any row.

| Issue | Branch HEAD | State (verified) | Base |
|---|---|---|---|
| #2 test call-sites | 8a256a4 | committed, PUSHED, in sync with origin/agent/issue-2 | 8438f5a |
| #3 CI | bbfeb0b (2 cell commits) | committed, PUSHED, in sync; draft signing-wall fix in tree | 8438f5a |
| #4 Deck top strip (BOLT) | c5de8d2 | committed design-of-record + **UNCOMMITTED code**: 2 modified (MonolithCustomComponents.kt, TactileDeckScreen.kt) + 2 untracked (SealBar.kt, SealBarStateTest.kt) | **old main @1852181\*** |
| #5 Truth Car 3 verification | bb2393f | committed, **ahead-1 UNPUSHED**, no upstream branch | 8438f5a |
| #6 Deck audit | c5fd740 | committed, PUSHED, in sync — HEAD is already a Wave-2 commit (cluster B has run) | 8438f5a |
| #7 Shield | 8ec3e4a | committed, PUSHED, in sync | 8438f5a |
| #8 Drawer | f0e1461 | committed, PUSHED, in sync | 8438f5a |
| #9 Park | 6237468 (3 commits) | committed, PUSHED, in sync | 8438f5a |
| #10 Receipt | e39ae62 | committed, **NO upstream — never pushed** | **old main @1852181\*** |
| #11 Held Door | c956260 | committed, PUSHED, in sync | 8438f5a |
| #12 Tasks / F-1 | d9d7697 | committed, **ahead-1 UNPUSHED** (tracks origin/main; no origin/agent/issue-12) | 8438f5a |
| #13 Session Room | b2cdc3f (plan) | plan committed, no upstream + **UNCOMMITTED code**: 5 files modified (+928/−114: MainActivity, MonolithRepository, ReceiptState, MonolithViewModel, AirlockAndDisarmOverlays) + 7 untracked (ReceiptFiledStore.kt, DeskClockSettle.kt, DeskClockFlowTest.kt, cell plan files) | 8438f5a |
| #14 Setup/onboarding | 9858a0b | committed, no upstream, working tree CLEAN (the commission's "uncommitted" note is stale — the cell committed after it was written) | 8438f5a |
| #15 this epic | 2c745aa local | Ed2: local commit 2c745aa; **origin carries twin commit 3875a78** (same message, created via connector push_files after direct git push failed — proxy CONNECT aborted; tree verified identical, dcf6905). Local and remote have DIVERGED BY SHA, not by content. | 8438f5a |
| #16 Pill | 6c86ea5 (2 commits, nested repo at builds/cells/issue-16/repo) | committed, CLEAN, **NO upstream — never pushed**; the cell's seat files (feel/finisher/flags) sit loose in issue-16/ OUTSIDE the repo — not versioned | 8438f5a |
| #17 Snapshot #3 | 99e4722 | committed, PUSHED, in sync | 8438f5a |

**Strays — NOT merge inputs, named so nobody admits them by
accident:** `issue-9-builder/` (branch agent/issue-9-builder, old
base, 2 uncommitted files — scratch, excluded); `issue-12-work/`
(non-git scratch); `issue-14-paths.bin` and
`issue-14.muse-github-snapshot-sync.json` (tool artifacts).

\* **Ancestry warning (#4, #10):** their `main` sits at 1852181,
a commit carrying the Gradle-wrapper message that does NOT appear
in current main's ancestry (main carries the same change as
817e8c7). Before either cell's delta is admitted, run a merge-base
check against the batch-9 base; do not assume fast-forward kinship.

**Push-health fact for the whole programme:** direct `git push`
from this box is currently unreliable (proxy CONNECT aborted /
credential hang observed twice tonight). The connector
`push_files` route works and is how Ed2 reached origin. Any cell
marked UNPUSHED above is one connector push away — but until that
call lands, its work exists on ONE disk. That is a preservation
fact, and it is why #17 matters to this epic (see Ed3-4).

## E3-2 · CELL → STEP MAP (onto Ed2 §3 order; order itself unchanged)

- [ ] Step 0 — Baseline freeze: FAIL baselines on main @817e8c7 + tree-hash freeze of every cell branch AS CENSUSED above. #4/#13 uncommitted work must be committed by their cells or explicitly frozen as working-tree hashes — a hash of an uncommitted tree is the weakest freeze on record; committing is priced at minutes and preferred.
- [ ] Step 1 — Admission gate: #5's verdicts (bb2393f, push first) recorded per lane/cell; a REPAINTED ruling stops that input here.
- [ ] Step 2 — MIGRATION_12_13 composed FIRST (Ed2 §1 text, unchanged). Open sub-decision carried from Wave-2 seats: rows 7–8 (heldShutAtSeal/heldPeekAtSeal) exist in NO tree — Ren rules R1 (confirm −1 / set 0 / hold both columns and ship 6) before this step opens.
- [ ] Step 3 — warrant → nofire (lanes, as Ed2). #2's test call-site fixes (8a256a4, pushed) are the precondition that this step's test deaths (ArmProvingControllerTest fake-override) are reconcilable at Step 10.
- [ ] Step 4 — truth-3-times + EditableTimes provider FILE SELECTION (Ed2, unchanged) — same sitting as M2 sheet-duration cure.
- [ ] Step 5 — Pill: **source of truth changed by #16's cell.** The composed `start2MinutePeek` ALREADY EXISTS in issue-16/repo @352e5db (Suki pre-grant guard + Pastille ST-08 launch, composed by the cell). Ed2 Flag A is therefore no longer "compose at merge" — it is "adopt the cell's composed text as base, verify seam-survival (guard → latch → grant → gate → launch → record), add Frist's seed". #16's loose seat files must be committed into its repo before admission.
- [ ] Step 6 — routine-edit (Voss) hunk-by-hunk with hunk ledger (Ed2, unchanged) + **#12 F-1** (d9d7697, push first): routine sessions take the queue head as intention. F-1 composes AFTER Voss's routine deltas, onto the re-based text — never onto Voss's v10-era base.
- [ ] Step 7 — Session Room from the **issue-13 CELL TREE** (DeskClockSettle.kt, ReceiptFiledStore.kt, DeskClockFlowTest.kt + Klinge 19 / A-1…A-12 list copied verbatim). Entry precondition NEW in Ed3: the #13 cell COMMITS its working tree. Merging from an uncommitted tree is forbidden — there is nothing to hash-freeze.
- [ ] Step 8 — Onboarding LAST (Ed2): #14 @9858a0b committed and clean; S-1 disposition recorded by name (cured-with-test-flip or struck). #6 (Deck audit, pushed, incl. its Wave-2 commit) and **#4 BOLT** compose the Deck face in the same sitting as each other — #4's cell commits first, ancestry check (E3-1\*) passes first.
- [ ] Step 8b (NEW, ordering only) — remaining screen cells in Ed2 §4 ledger order: #7 Shield (composes onto BASE + F5 header from Step 3), #9 Park, #8 Drawer (cabinet eviction; its new home is DECISION-parked in #21 — the eviction lands, the re-homing does NOT), #10 Receipt (ancestry check first; ReceiptFiledStore interplay with #13 resolved here — one store, named owner), #11 Held Door.
- [ ] Step 9 — Ren's hand-work (Ed2, unchanged): W-08, W-41 full package, W-43/W-13, Receipt Group A, Frist prints. Wave-2 note (Max-impl seat): this step is the epic's true ceiling — none of it exists in any tree; price the epic as lanes + cells + this hand-work, or Batch 10 ships columns that sit at default forever.
- [ ] Step 10 — Test reconciliation table over EVERY test file (Ed2, unchanged), seeded by #2's fixed call-sites.
- [ ] Step 11 — The single build. Entry: Sarthak's explicit word, quoted + dated. Exit: FOUR lines (suite counts by class · migration verdict per column · cure FAIL→PASS register with hashes · artifact path+size+sha256+DB v13). **#3's CI circuit** is the alternative proof path — its signing-wall draft (bbfeb0b) needs Ren's adoption ruling before it can carry any of this.

## E3-3 · HAND-MERGE FLAGS — updated register

1. **start2MinutePeek** — three authors, now with a composed candidate in hand (#16 @352e5db). Non-negotiables: Frist's seed (`secondsRemaining = peekMinutes.value * 60`) inside or the metronome dies; ST-08 launch AFTER `repository.startPeekLease(pkg)`; no `ifBlank { "com.whatsapp" }` fallback anywhere; pill's `startPeekLease` lands wholesale WITH `clearOtherPeekLeases`; owed Repository-head guard (Ed2 §4B6) composed at the lease head.
2. **Voss routine-editing** — v10-era CODE base wearing a v12 Database. Deltas RE-BASED onto batch-9 text, hunk ledger mandatory; only `withinLabel` enters MIGRATION_12_13; dead voiced surface (selectArtifact / tapSerialPlateEasterEgg) NEVER transplants; Dao paging cannot express DRW-04 page+1 — do not pretend otherwise in the ledger.
3. **MIGRATION_12_13** — composed ONCE, Ed2 §1: entity append order = ADD COLUMN order per table; Frist + Voss append at the SAME `receiptNumber` anchor — composed-order table governs; negative grep: neither lane's private 11→12 text survives anywhere.
4. **Hot files** — MonolithViewModel.kt (differs in EVERY lane and most cells), MainActivity.kt (6 versions), WatchStore.kt (4), MonolithAccessibilityService.kt (3), MonolithShieldService.kt (pill/nofire/times hunks interleave :119–:408). Per-hot-file surface manifest (base hash → composed hash → named hunks only) at Step 0. NO wholesale file win, ever (base-wins law).
5. **Provider is a FILE SELECTION** — times' EditableTimes.kt wholesale; pill's constants version never lands (compile break with Frist's Repository, or worse: compiles and silently never reads the columns).
6. **deskClockBannerText duplication** — flow-clock lane vs batch-9: keep ONE copy; the room itself comes from #13's tree, not the lane.
7. **Sound** — every lane's TactileSoundEngine is the pre-fact literal version; BASE engine wins; exactly one `playSealThrow` site; R5 voicing rulings (pill hand-back, ADD TIME) recorded as decisions, not inherited silences.
8. **#13/#10 ReceiptFiledStore** — lands as a STORE at Step 7; if it becomes a column instead, that is a new §1 item and Ren's call, taken before Step 2 closes.

## E3-4 · WHAT THIS EPIC OWES #17

The merge's pre-state evidence (Step 0) and five unpushed /
partially-uncommitted cell branches (#4, #5, #10, #12, #13, #16)
currently exist on one disk. Snapshot #3 as staged would NOT
capture the cells (see ISSUE-17-WAVE2-REPORT). Until the v4 runner
exists, the cheapest true preservation for this epic is: push the
six branches above via the connector route. Priced: six push_files
calls, no code changes. Konto flags this as the epic's #1
pre-merge risk — above every conflict in §2.

## E3-5 · REMAINDERS / NOT IN THIS EDITION

- No merge performed. No compile (law). No cell branch altered by
  Konto except this plan file on agent/issue-15.
- Wave-2 seat verdicts for this issue: ISSUE-15-WAVE2-REPORT.md
  (same branch). Panel verdicts are the panel's file, not Konto's.

— Konto, cluster-H lead, issue #15 (DECLARED, uncompiled)
