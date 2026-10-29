# BATCH 10 — THE EVERYTHING-MERGE · MERGE PLAN

**Keeper:** Konto (cell lead, issue #15). **Cell:** Merge Planner ·
Innovator · Critic · Verifier, seated Oct 30, 2026.
**Ground:** main @ 817e8c7 = batch-9 tree + CI commits. DB **v12**
(MIGRATION_11_12 carries only `shortsReelsSniperEnabled` +
`breakTakenSeconds`). Surveyed from the lane trees themselves,
never from summaries. This plan composes; **Ren performs the
merge and the single compile, only on Sarthak's word.** Nobody
else compiles or merges.
**Prior cartography:** studio/plan/TRUTH3-MERGE-MAP.md was the
batch-9 car's map (11→12). Its seam ledger (§3 there) still binds
where named below; its §2 migration is history — batch 9's actual
11→12 took two columns, not four, so Frist's columns arrive HERE.

---

## 0 · LANE CENSUS (surveyed Oct 30, 2026)

| Lane | Owner | DB base in lane | New files of note | Merge character |
|---|---|---|---|---|
| truth-3-times | Frist (Rurik) | **v12 — carries a PRIVATE 11→12** | data/EditableTimes.kt (the provider) | Columns re-based into 12→13 (§1); provider rewire §2 |
| routine-edit | Voss | **v12 — carries a PRIVATE 11→12** (declared base was v10; the lane advanced itself) | — | Hunks transplant, NEVER overlay; only the `withinLabel` delta enters 12→13 |
| flow-clock | Wecker | v11 | ui/DeskClockSettle.kt, DeskClockFlowTest | Transplants onto v12; session room (issue #13) |
| nofire | Wache | v11 | service/CompletionSignal.kt; RoutineAlarmReceiver/RoutineHeartbeat/RoutineScheduler differ | F1–F5 + ST-05/06; §2 watch item on SessionSealTransition |
| warrant | Stempel | v11 | — | CLOSED lane; lands as written (provePipelineNow + notePipelineProof) |
| pill | Pastille | v11 | service/PillController/PillFace/PillTheme + data/EditableTimes.kt | Test verdict still owed (issue #16); §2 seam at start2MinutePeek |
| onboarding | Empfang | v11 | data/SetupStore.kt, ui/components/ReceptionSheet.kt | Lands LAST — its ROOM beat is true only merged with flow-clock (issue #14) |

**The two private 11→12s are this merge's first trap.** Frist's
and Voss's lanes each wrote their own MIGRATION_11_12 text that
disagrees with main's. Neither text enters the merge. Only their
column deltas do, re-based into the one MIGRATION_12_13.

---

## 1 · THE MIGRATION — 12→13, ONE STEP

One composed `MIGRATION_12_13` beside MIGRATION_11_12 in
data/MonolithDatabase.kt; version 12 → 13; appended to
addMigrations(...). Entity field append order must equal the
migration's ADD COLUMN order (Room schema identity), exactly as
in batch 9.

| # | Column | Table | Default | Owner |
|---|---|---|---|---|
| 1 | `peekMinutes` | guard_settings | 2 | Frist |
| 2 | `breakMinutes` | guard_settings | 10 | Frist |
| 3 | `breakSeconds` | focus_sessions | 600 (the only break pre-existing rows could have been created under) | Frist |
| 4 | `extensionSecondsAdded` | focus_sessions | 0 | Frist (ST-04) |
| 5 | `extensionLog` | focus_sessions | "" | Frist (ST-04 print) |
| 6 | `withinLabel` | (Voss's table — Planner pins it from routine-edit source) | (from lane) | Voss (RC-03) |
| 7 | `heldShutAtSeal` | focus_sessions | (from source) | Suki split (W-41) |
| 8 | `heldPeekAtSeal` | focus_sessions | (from source) | Suki split (W-41) |

The Merge Planner's drop-in text + per-column file evidence is
an input to this plan (§6). Bounds live in the setters, not the
schema (peek 0–30, break 0–60, clamped on write). A migration
chain test (12→13, fresh-install ≡ upgraded) is part of the
merge's proof, per the Pruefer standard carried from batch 9.

---

## 2 · COLLISION SURFACE + SEAMS

Hot files (touched by ≥3 lanes, from the tree survey):
data/MonolithRepository.kt · data/MonolithModels.kt ·
data/MonolithDatabase.kt · data/MonolithDao.kt ·
ui/MonolithViewModel.kt · service/SessionSealTransition.kt ·
service/WatchStore.kt · service/MonolithAccessibilityService.kt ·
service/MonolithShieldService.kt · ui/screens/ShieldMatrixScreen.kt ·
ui/screens/TactileDeckScreen.kt · ui/components/ParkDrawer.kt ·
MainActivity.kt · AndroidManifest.xml. Every one is composed
function-by-function in one pass, never overlaid.

Named seams, binding:
- **startPeekLease (Repository)** — three authors again: pill's
  grant text as base, Frist's duration source inside it, Suki's
  unlisted-package guard at its head (batch-9 composition, kept).
- **The provider rewire (S-3, was UNASSIGNED in batch 9's map)**
  — ASSIGNED HERE: the merge itself. When Frist's columns land,
  EditableTimesProvider's two functions switch to them in the
  same sitting (peekMinutes × 60_000 clamp 0–30; breakMinutes × 60
  clamp 0–60; peekMinutes = 0 → no grant at all). Columns that
  land unread are UNA-03's disease in a new coat.
- **start2MinutePeek** — Suki's pre-grant guard + Pastille's
  post-grant launch compose by hand; neither text overwrites.
- **SessionSealTransition** — Wache F2 (ADOPT-never-delete) must
  not lose `isTombstoned` in composition; the Critic verifies
  against the nofire source before this file is touched.
- **Shield header (S-8)** — Voss's editor transplants settle
  first on ShieldMatrixScreen; other lanes' header text composes
  onto his, never the reverse.
- **Onboarding truth dependency** — Empfang's ROOM beat line is
  true only with flow-clock merged; onboarding lands after it,
  and Pforte's S-1 (sheet total contradicts its cards) is
  merge-blocking for issue #14's half.

## 3 · MERGE ORDER (recommendation to Ren — the ruling is his)

1. Countersign gate: no lane enters without its cell verdict
   (issues #6–#14, #16) + this cell's Verifier/Critic reports.
2. Compose MIGRATION_12_13 into the merge tree FIRST; every
   lane's data-layer hunks then reduce to non-migration text.
3. warrant (closed, as written) → nofire (F1–F5).
4. truth-3-times + the provider rewire in the same sitting.
5. pill (seam composition at start2MinutePeek / startPeekLease).
6. routine-edit transplants, hunk by hunk (Shield last).
7. flow-clock (session room, issue #13's merge list).
8. onboarding (after flow-clock; S-1 cured or the sheet lies).
9. Ren's hand-work: W-08 dynamic PARK plate · W-41 split print
   on the §1 columns · W-43 receipt footer.
10. **Test-call-site reconciliation pass** — batch 9's lesson
    (issue #2): after composition, every test call-site is
    checked against the composed signatures BEFORE the suite
    is asked to run.
11. Full suite + migration chain, run by Ren; the compile and
    the build happen once, on Sarthak's explicit word.

## 4 · INPUTS TRACKED FROM OTHER CELLS

Issues #6 (Deck) · #7 (Shield) · #8 (Drawer) · #9 (Park) ·
#10 (Receipt) · #11 (Held Door) · #12 (Tasks + F-1 machinery) ·
#13 (Session Room) · #14 (Setup Sheet + onboarding) · #16 (pill
verdict). Each cell's declared output is an input here; the
epic's checklist on issue #15 is updated as each lands — an
item is checked only when its work is IN the merge tree, never
when it is merely declared.

## 5 · GATES

- Build authority: only Ren compiles; only on Sarthak's word.
- Proof standard: a cure-proof must FAIL on the pre-merge tree
  and PASS on the composed tree (Pruefer standard, carried).
- Presentation: nothing from this cell goes to Sarthak except
  through Ren.

## 6 · OPEN FACTS THIS DESK STILL OWES

- Merge Planner: drop-in MIGRATION_12_13 text + column evidence
  (incl. withinLabel's table/default and the split columns'
  exact names/defaults from source).
- Verifier: per-lane CONFIRMED / PARTIAL / CONTRADICTED verdicts
  against the declarations.
- Critic: charge list against §2's flags + whatever the record
  missed.
- Innovator: better merge mechanics, spec + price; adoptions
  are Ren's ruling.
Each closes on evidence in a follow-up edition of this file,
or stays named here. Nothing is dropped by silence.

— Konto, Oct 30, 2026 (Batch-10 Edition 1)
