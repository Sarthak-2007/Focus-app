# MERGE-ORDER MANIFEST — `batch/build-first-usable` (Slice 1 composition)

*Filed by BUILD-SR-8 (T24, P9), 2026-11-02 IST. Canonical home: this file. Travelling copy: `docs/build-first-usable/COMPOSITION-MANIFEST.md` on `agent/build-sr-integration` (identical text; if the two ever differ, this file governs and the difference is named upward, AL-0056).*

## The route, stated once

`batch/build-first-usable` is the composition branch for Slice 1. It starts at `origin/main` (`03c830fb8a616d4ed5d3c2f143e614e885fbc3e6`). Lane work reaches it **only** by the steps below, **in this order**, **at the coordinator's call** — a step executes when its preconditions hold, never on a clock. Each step is one merge of one lane PR's head into the batch, followed by the batch's full venue (a push to `batch/**` fires `build` + `proof` + `screenshots` + `compile`; `targeted` is replaced by the full suite on batch heads, per the venue file and VERIFY-SR-1's milestone gate).

Dependency order (plan §2, as the brief fixes it): **P0 → P1 → P3 → P4 → P2 → P5 → P6/P7/P8**, with the P9 harness landing after P1 (step 3) so the walk can run against the composed model for the rest of the climb.

## Preconditions — every step, no exceptions

1. The lane PR is OPEN against `main` (PRs are the record of the piece; the batch merge consumes the PR's head, it does not replace the PR).
2. VERIFY-SR-1's stamp for the piece stands in `work/verify-sr-1/stamps.md` as **VERIFIED** (scoped: compile GREEN + targeted tests passing BY NAME on the PR head). A NOT VERIFIED stamp, a GATE UNMET, or no stamp = the step does not execute.
3. CRITIC-SR-1's usability walk for the piece is not an outstanding SEND BACK.
4. The batch head is green-or-named: the previous step's full-venue run has been read; its failures, if any, are named on #839 and in the P9 stamp — a step never merges onto an unread batch head.
5. The merge act + its #839 line happen in the same run (tracker discipline).

## The steps

| Step | Piece | Lane branch → PR (as filed) | Merge act | Expected friction, named in advance |
|---|---|---|---|---|
| 0 | — (baseline) | `batch/build-first-usable` created off `origin/main`, pushed by BUILD-SR-8 | none — the push itself fires the full venue; its run is the composition's baseline and is named in the P9 stamp (main's floor is red per VERIFY-SR-1's baseline read; the batch inherits exactly that, no more, no less) | none |
| 1 | **P0 FOUNDATION** (BUILD-SR-1) | `agent/build-sr-foundation` → PR **#842** | merge PR head into batch | Theme files (`Color.kt`/`Type.kt`/`Theme.kt`, new `Relight.kt`) — no other lane touches them at this point; expect a clean merge |
| 2 | **P1 DAY MODEL** (BUILD-SR-3) | `agent/build-sr-engine` → PR **#841** | merge PR head into batch | `MonolithDatabase.kt` + `MonolithDao.kt` are sole-owner edits (P1 only); domain/data files are new. Expect clean. **This step raises the batch DB to v26** — every later step composes over the migrated schema |
| 3 | **P9 HARNESS** (BUILD-SR-8) | `agent/build-sr-integration` → PR against **`batch/build-first-usable`** (opened this leg) | merge PR head into batch | Test-tree only (`P9ProductionFixtures`, `P9MigrationProofTest`, `P9WholeDayWalkTest`) + this manifest's travelling copy under `docs/`. No production file touched; expect clean. After this step the batch carries the migration proof + whole-day walk, and every later step's full-venue run executes them |
| 4 | **P3 SEALED FACE + RING** (BUILD-SR-2) | `agent/build-sr-faces` → PR **#840** | merge PR head into batch | **THE named seam of this composition:** P3 was built against a lane-local token file (`ui/theme/StrongroomTokens.kt`, lane-local `ReliefLight.kt`) because P0 had not landed when the faces lane started (BUILD-SR-2's opening line names this itself). P0 (step 1) brings the foundation's theme law (`Relight.kt`, rebuilt `Color.kt`/`Type.kt`). Reconciliation is BUILD-SR-2's own filed plan — a single-file re-point of the lane-local tokens onto P0's — executed by the faces lane on its branch BEFORE this step is called, verified by VERIFY-SR-1's re-stamp of the reconciled head. If the re-point has not landed, this step waits; nobody reconciles inside the merge |
| 5 | **P4 SIGNING SURFACE** (BUILD-SR-2, same lane branch, second PR or same PR extended — as the lane files it) | `agent/build-sr-faces` | merge lane head into batch | Depends on P1's signing record (step 2) + P3's face to seal (step 4) — both stand by now. Watch: any new production file under `ui/` must carry its mapped tests or the batch's full suite is the judge (no GATE UNMET concept on batch heads — the full suite runs) |
| 6 | **P2 ENGINE WIRING** (BUILD-SR-3, same lane branch, second wave) | `agent/build-sr-engine` | merge lane head into batch | Engine files (`service/`) wired to P1's model. The substrate's engine is shared with the outgoing app — conflicts, if any, land here and are resolved by BUILD-SR-3 in-lane, never by the composer. Every on-device claim from this step carries U-1 (UNPROVEN-ON-DEVICE) into the P9 stamp |
| 7 | **P5 ANTECHAMBER** (BUILD-SR-2, third in lane) | `agent/build-sr-faces` | merge lane head into batch | Depends on P2's wiring (step 6) for held calls + drum enforcement surfaces |
| 8 | **P6 LIFTING + PLATE + DRAWER** (wave-2 lane, as seated) | branch/PR as filed by the seated lane | merge lane head into batch | Closes ledger B-13's full hand-back form (P9's walk asserts its P1 surface only — see the harness header) and B-14/B-15's plate law |
| 9 | **P7 GUARD SURFACES** (wave-2 lane) | as filed | merge lane head into batch | Rides P1's `StrongroomGuard` (step 2); surfaces only |
| 10 | **P8 DESK CLOCK + NOTIFICATION** (wave-2 lane) | as filed | merge lane head into batch | The notification's wording enters the build record BY NAME when chosen (G-6 / build record §A.2) — the composer checks the entry exists before calling this step |

## After the last step — the milestone gate

1. The batch head's full-venue run is read whole by VERIFY-SR-1; the **P9 stamp** is filed on it: per-job verdicts, every failing test BY NAME, the migration proof + whole-day walk results by test name, and the standing clause — the venue does not prove device behaviour (containment, persistence, boot recovery, held calls, exact alarms, widget truth on his phone remain **UNPROVEN-ON-DEVICE**, Audit A G-7).
2. CRITIC-SR-1's whole-day usability walk over the composed build.
3. Only then: the milestone commit + push to `main` (the coordinator's act, per plan §3 and the GitHub-saves rule — main receives nothing before this gate).

## Standing rules for whoever executes a step

- A merge conflict is the OWNING LANE's to resolve on its branch (re-merge main/batch as its brief directs), never resolved by hand inside the batch except for trivially textual collisions, which are named on #839 when they happen.
- If a step's full-venue run introduces a failure in a part that step changed, the step is NOT complete: the lane cures in the part, the batch re-runs, the stamp names both runs.
- Failures the batch inherits from main's floor are named, never silently carried and never attributed to a lane that did not touch the part (AL-0061: the floor census is not re-opened as a precondition).
- No step is skipped or reordered without the coordinator filing the change in the build record first. The order above is the dependency order; reordering it is a programme decision, not a composer's convenience.

— BUILD-SR-8 (T24 Builder), 2026-11-02 IST.
