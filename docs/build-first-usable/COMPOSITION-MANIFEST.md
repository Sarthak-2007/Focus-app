# MERGE-ORDER MANIFEST — `batch/build-first-usable` (Slice 1 composition)

*Filed by BUILD-SR-8 (T24, P9), 2026-11-02 IST. Canonical home: this file. Travelling copy: `docs/build-first-usable/COMPOSITION-MANIFEST.md` on `agent/build-sr-integration` (identical text; if the two ever differ, this file governs and the difference is named upward, AL-0056).*

## The route, stated once

`batch/build-first-usable` is the composition branch for Slice 1. It starts at `origin/main` (`03c830fb8a616d4ed5d3c2f143e614e885fbc3e6`). Lane work reaches it **only** by the steps below, **in this order**, **at the coordinator's call** — a step executes when its preconditions hold, never on a clock. Each step is one merge of one lane PR's head into the batch, followed by the batch's full venue (a push to `batch/**` fires `build` + `proof` + `screenshots` + `compile`; `targeted` is replaced by the full suite on batch heads, per the venue file and VERIFY-SR-1's milestone gate).

Dependency order (plan §2, as the brief fixes it): **P0 → P1 → P3 → P4 → P2 → P5 → P6/P7/P8**, with the P9 harness landing after P1 (step 3) so the walk can run against the composed model for the rest of the climb.

## Preconditions — every step, no exceptions

1. The lane PR is OPEN against `main` (PRs are the record of the piece; the batch merge consumes the PR's head, it does not replace the PR).
2. VERIFY-SR-1's stamp for the piece stands in `work/verify-sr-1/stamps.md` as **VERIFIED** (scoped: compile GREEN + targeted tests passing BY NAME on the PR head). A NOT VERIFIED stamp, a GATE UNMET, or no stamp = the step does not execute.
3. CRITIC-SR-1's usability walk for the piece is not an outstanding SEND BACK.
4. Merges land **back-to-back** (SUCCESSOR-21's slice-pattern ruling, ratified): superseded batch full-venue runs auto-cancel by the venue's concurrency rule, and **only the final composed head's full-venue run is the DONE run**. No step waits on the previous step's batch run; what gates a step is the piece's own stamp (precondition 2) plus its PR's compile green. Failures are judged once, on the DONE run, by name, against the baseline.
5. The merge act + its #839 line happen in the same run (tracker discipline).

## The steps

| Step | Piece | Lane branch → PR (as filed) | Merge act | Expected friction, named in advance |
|---|---|---|---|---|
| 0 | — (baseline) | `batch/build-first-usable` created off `origin/main`, pushed by BUILD-SR-8 | none — the branch stands at main's head. **Observed at execution (BUILD-SR-8, this leg): the branch-creation push fired NO workflow run** (the ref carries no new commits; verified by run-list + by-SHA query). The baseline full-venue run was therefore fired by `workflow_dispatch` on the batch ref — the venue file's own manual route — as run **41419093974**. Its verdict is the composition's baseline, named in the P9 stamp (main's floor is red per VERIFY-SR-1's baseline read; the batch inherits exactly that, no more, no less). Later steps are ordinary merges with new commits and fire the full venue on push as designed | branch-creation pushes at an existing commit do not fire the venue — dispatch is the cure, recorded here so no later composer waits on a run that will never come |
| 1 | **P0 FOUNDATION** (BUILD-SR-1) | `agent/build-sr-foundation` → PR **#842** | merge PR head into batch | Theme files (`Color.kt`/`Type.kt`/`Theme.kt`, new `Relight.kt`) — no other lane touches them at this point; expect a clean merge |
| 2 | **P1 DAY MODEL** (BUILD-SR-3) | `agent/build-sr-engine` → PR **#841** | merge PR head into batch | `MonolithDatabase.kt` + `MonolithDao.kt` are sole-owner edits (P1 only); domain/data files are new. Expect clean. **This step raises the batch DB to v26** — every later step composes over the migrated schema |
| 3 | **P9 HARNESS** (BUILD-SR-8) | `agent/build-sr-integration` → PR against **`batch/build-first-usable`** (opened this leg) | merge PR head into batch | Test-tree only (`P9ProductionFixtures`, `P9MigrationProofTest`, `P9WholeDayWalkTest`) + this manifest's travelling copy under `docs/`. No production file touched; expect clean. After this step the batch carries the migration proof + whole-day walk, and every later step's full-venue run executes them |
| 4 | **P3 SEALED FACE + RING** (BUILD-SR-2) | `agent/build-sr-faces` → PR **#840** | merge PR head into batch | **THE named seam of this composition:** P3 was built against a lane-local token file (`ui/theme/StrongroomTokens.kt`, lane-local `ReliefLight.kt`) because P0 had not landed when the faces lane started (BUILD-SR-2's opening line names this itself). P0 (step 1) brings the foundation's theme law (`Relight.kt`, rebuilt `Color.kt`/`Type.kt`). Two distinct acts, owned by name: (a) the token re-point is BUILD-SR-2's own filed plan — a single-file re-point of the lane-local tokens onto P0's — executed by the faces lane on its branch BEFORE this step is called, verified by VERIFY-SR-1's re-stamp of the reconciled head; if it has not landed, this step waits. (b) **Ruling R2-6 (Round 2): P0's RELIGHT tuning is CANONICAL — at this step the composer (BUILD-SR-8) DELETES the faces lane's divergent RELIGHT tuning table, and the deletion is named in the composition record.** If BUILD-SR-2 argues its table instead, the argument goes to the coordinator BEFORE composition; silence is consent to P0's |
| 5 | **P4 SIGNING SURFACE** (BUILD-SR-2, same lane branch, second PR or same PR extended — as the lane files it) | `agent/build-sr-faces` | merge lane head into batch | Depends on P1's signing record (step 2) + P3's face to seal (step 4) — both stand by now. Watch: any new production file under `ui/` must carry its mapped tests or the batch's full suite is the judge (no GATE UNMET concept on batch heads — the full suite runs) |
| 6 | **P2 ENGINE WIRING** (BUILD-SR-3, same lane branch, second wave) | `agent/build-sr-engine` | merge lane head into batch | Engine files (`service/`) wired to P1's model. The substrate's engine is shared with the outgoing app — conflicts, if any, land here and are resolved by BUILD-SR-3 in-lane, never by the composer. Every on-device claim from this step carries U-1 (UNPROVEN-ON-DEVICE) into the P9 stamp |
| 7 | **P5 ANTECHAMBER** (BUILD-SR-2, third in lane) | `agent/build-sr-faces` | merge lane head into batch | Depends on P2's wiring (step 6) for held calls + drum enforcement surfaces |
| 8 | **P6 LIFTING + PLATE + DRAWER** (wave-2 lane, as seated) | branch/PR as filed by the seated lane | merge lane head into batch | Closes ledger B-13's full hand-back form (P9's walk asserts its P1 surface only — see the harness header) and B-14/B-15's plate law |
| 9 | **P7 GUARD SURFACES** (wave-2 lane) | as filed | merge lane head into batch | Rides P1's `StrongroomGuard` (step 2); surfaces only |
| 10 | **P8 DESK CLOCK + NOTIFICATION** (wave-2 lane) | as filed | merge lane head into batch | The notification's wording enters the build record BY NAME when chosen (G-6 / build record §A.2) — the composer checks the entry exists before calling this step |

## Riding the composition — F-1 (ruled, executed by BUILD-SR-8)

**F-1: APK artifact retention 3 → 14 days** in `rescue-build.yml` (the `monolith-debug` artifact) — APPROVED in the slice-pattern ruling, executed **inside the composition**, not as a standalone edit. Executed by BUILD-SR-8 as a dedicated commit on `batch/build-first-usable` ahead of step 1; **VERIFY-SR-5 stamps it with the composition gate**. It lands on `main` with the slice, like everything else on the batch.

## After the last step — the milestone gate

1. The batch head's full-venue run (the DONE run) is read whole by VERIFY-SR-1; the **P9 stamp** is filed on it: per-job verdicts, every failing test BY NAME, the migration proof + whole-day walk results by test name, and the standing clause — the venue does not prove device behaviour (containment, persistence, boot recovery, held calls, exact alarms, widget truth on his phone remain **UNPROVEN-ON-DEVICE**, Audit A G-7).
2. CRITIC-SR-1's whole-day usability walk over the composed build.
3. Only then, **landing (slice-pattern ruling, ratified): a DIRECT MERGE-PUSH of the batch head to `main`** — not a batch-headed PR (which would pay the full venue twice for the same tree). A slice close IS a major milestone (a delivered build for his phone), so the milestone push and the slice landing are the same act, the coordinator's. **Main's push run is the slice's DONE run.** And per AL-0063a (§8 notice): the slice is DONE only as an **installable build** — same signing key, version never lower, universal installer route; code on main without the installable build is not DONE.

## Standing rules for whoever executes a step

- Conflict triage (METHODS-SR-3 git route §6, agreed): the later-in-order lane rebases onto the batch head; landed work is never rebased by a later lane; **tests never yield** — both survive, the second renames; an unattributable hunk STOPS the merge — abort, letter owner + coordinator; nobody's work is ever deleted as a resolution; every real conflict gets a same-act letter to KEEPER-SR-1 (files, lanes, who yielded under which rule).
- If a step's full-venue run introduces a failure in a part that step changed, the step is NOT complete: the lane cures in the part, the batch re-runs, the stamp names both runs.
- Failures the batch inherits from main's floor are named, never silently carried and never attributed to a lane that did not touch the part (AL-0061: the floor census is not re-opened as a precondition).
- No step is skipped or reordered without the coordinator filing the change in the build record first. The order above is the dependency order; reordering it is a programme decision, not a composer's convenience.

— BUILD-SR-8 (T24 Builder), 2026-11-02 IST.
