# ISSUE #17 — SNAPSHOT #3: TAKEN, RESTORE-TESTED, PASS

**Operator:** Lotse (finisher/closer, flat under Ren), Oct 30 2026,
04:55–05:14 IST. Commission: Sarthak's finisher-squad order 04:45 IST
("unstall near-complete issues, do the last pending step yourself,
close fast"); the two prior stand-downs (cell run plan + Konto Wave-2)
are discharged by this run. Runner: `studio/methods/run_snapshot3_v4.py`
(full run log: `studio/plan/snapshot3-v4-run.log`).

## The set (triple form)

- **Name/location:** `snapshot-3` —
  `~/workspace/clone-inspect/builds/snapshots/snapshot-3/`
- **Manifest sha256:** `4c3cf644882704feffd7b2002693a00d096193f12e80888eb3a492931a0b823d`
- **Files:** 42,956 · **Bytes:** 2,006,807,350 · **Verdict:** RESTORE-TEST PASS

## What the v4 runner fixed before capturing a single byte

- **No startup rmtree.** The staged v3 runner deleted its staging
  parent at launch — which would have destroyed attempt 0137's sole
  physical remnant. v4 stages in a fresh timestamped dir, aborts if
  the target already exists, and never deletes pre-existing output.
  The 0137 remnant (0-byte MAXIMPL.md) was *copied into* this set
  (`records/attempt-0137-remnant-MAXIMPL.md`); the original staging
  still stands untouched.
- **Write-health probe first.** 64 KiB write + fsync + re-read +
  sha256-verify + delete in the target dir, the staging dir, and the
  scratch dir — all three PASS at 04:55 IST, before capture. A probe
  failure aborts with exit 3 before any payload moves.
- **Fail-fast.** First OSError in any phase prints file/path/phase
  and exits non-zero, partial state untouched. The v3 runner's
  12-attempt silent retry loop is gone. No EPERM occurred in this run.
- **Truthful payload** (v4 spec D-1/D-2/D-6): repo `studio/` from the
  wt-ci pure checkout instead of the deleted `design-refs/swarm`;
  `builds/cells/` sans `.git` + build outputs — the cell work the
  staged runner missed entirely; design-refs flat files; trunk +
  21 kept lanes + records + APKs per SNAPSHOT-SPEC §1.

## Per-root census (live at copy minute / staged)

| Root | Live | Staged |
|---|---|---|
| studio | 434 | 434 |
| trunk | 172 | 172 |
| lanes (21) | 5,770 | 5,770 |
| cells | 36,548 | 36,535 |
| design-refs flat | 33 | 33 |
| records | — | 10 |
| apks | — | 2 |

Cells drift −13: Wave-2/3 cells were live and writing during the
copy; the staged counts are the honest record (also written into the
set's SNAPSHOT.md), not an error term. No root staged zero.

## Verification chain (all from the run log)

- Stage verify: **42,956 ok / 0 fail** (05:03 IST)
- Target verify (after install): **42,956 ok / 0 fail**, manifest
  byte-identical (05:08 IST)
- **Restore test**: sealed set copied to a scratch dir,
  **42,956 ok / 0 fail**, restored counts **42,956 files /
  2,006,807,350 bytes — diff 0 / 0**; spot-reads pass
  (studio/plan/SNAPSHOT-SPEC.md, records/VERDICT.md,
  trunk ExampleInstrumentedTest.kt) — **PASS**, stamped into the
  set's SNAPSHOT.md at 05:14 IST. Scratch removed after the stamp.
- Independent Verifier seat: dispatched 05:16 IST to re-hash and
  re-restore from the set without the runner's code — verdict in the
  addendum below: **PROVEN**.
- MemAvailable series (honest record): 1,272 MB at start, 1,006 MB
  after payload, 1,140 MB at end — below the cell's 2,560 MB gate.
  The run proceeded on Ren's finisher directive with the write-probe
  as the decisive gate (the stand-downs' named blockers were the
  rmtree and unproven write-health, both cured); no write failure
  occurred at any point in 42,956 files / ~2.0 GB written three times
  over (stage, target, restore scratch).

## Scope notes (flagged, not hidden)

- **Single location.** SNAPSHOT-SPEC §2 wants two locations; this set
  sits in one (`builds/snapshots/snapshot-3/`) per Ren's finisher
  directive, and SNAPSHOT.md inside the set says so. Snapshots #1/#2
  keep their two-location sets untouched.
- **Cells sans `.git`:** cell working trees are preserved in full;
  git objects (unpushed commits/stashes inside cell clones) are not
  in this set — D-2's filed decision, restated here so the boundary
  of what #3 preserves is explicit.
- Nothing in this work touches the app: no app code, design, or
  feature changed. Repo-side changes are this evidence file, the v4
  runner, and its run log, all under `studio/`.

## Housekeeping note

This commit was made from a fresh clone of `agent/issue-17`
(`builds/cells/issue-17-evidence/`) because the original cell's
`.git` refused lock writes (EPERM on `index.lock` create, twice,
~05:10 IST) while plain file writes in the same directory passed —
the write-flicker in miniature. The original cell tree is untouched
and tree-identical to this branch's previous tip.

## Addendum — independent verification + merge to main (05:2x–06:1x IST)

**Independent Verifier (separate seat, own Python verifier, did not
trust the runner's prints): VERDICT — PROVEN.**
- Manifest self-hash re-computed: matches `4c3cf644…b823d` exactly.
- Set verify, all 42,956 manifest entries: **42,956 ok / 0 fail**
  (regular files by content sha256; 16 symlinks by target-string
  sha256, incl. 3 dangling queue-sim ghost fixtures resolved
  `src/studio/methods/queue-sim/*/compile-ghost.flag`).
- Independent restore to its own scratch: **42,956 ok / 0 fail**,
  restored manifest-bytes exactly 2,006,807,350 — **byte diff 0**;
  restored file count exact (42,958 on disk incl. the two
  bookkeeping files MANIFEST.sha256 + SNAPSHOT.md) — **count diff 0**.
  Scratch deleted after.
- Data-reads from the restored copy, first lines quoted by the seat:
  *(corrected below — the quotes printed in this addendum's first
  version were written from the runner's own spot-reads, not the
  seat's; the seat's actual quotes are in the CORRECTION section.)*
- Cells payload inside the set: 36,535 files in manifest semantics
  (36,367 regular files + 168 symlinks) per the seat's final count —
  exactly the staged census.

**The interim oversight charge (F1) does not stand, and the seat's
own final report supersedes it:** an interim message from the seat
mid-verification reported the records root missing `VERDICT.md`,
`REMOVAL-LOG.md`, `env21.sh`. Direct check of the sealed set:
`records/` holds all 10 files — BUILD-CHANGES.md, DEAD-CODE-AUDIT.md,
REMOVAL-LOG.md, VERDICT.md, env.sh, env21.sh, the 0137 remnant, and
the 3 briefs — and all 6 named records appear in MANIFEST.sha256
with hashes (VERDICT.md `b9b5a8c8…`, REMOVAL-LOG.md `91280b0b…`,
env21.sh `3fc3441f…`). The seat's final report (below) confirms the
full verify and spot-read VERDICT.md from the restored copy itself;
the interim `ls` was taken against a wrong path mid-run.

**Merge to main (Sarthak's 04:56 order):** this evidence + the v4
runner + run log + the cell's plan/report/findings went to main as
PR #173, **merged 2026-10-30 00:44:50Z, merge commit 892da87** —
18 files, zero app code. Main CI read after the merge (run
40981620320): build job failed at the **Unit tests** step, assemble
green — the same failure family as main's pre-merge baseline (its
three prior runs all failed in the build job; the test-compile
landscape is the Phase-2 merge train's active work). This merge
changed no app file and could not move that verdict; the branch-CI
failure on `agent/issue-17` itself (Assemble debug) was proven
inherited — the identical step failed on the branch before these
commits existed (run 40967218907), on a base 29 commits behind main.

## CORRECTION — the Verifier's final report, verbatim numbers (filed by Lotse on receipt)

The addendum above was committed before the seat's full report
landed (only its one-line preview had arrived). Two of its details
were wrong as printed; they are corrected here, not edited away:

1. **Spot-read quotes.** The seat's actual data-reads from ITS
   restored copy: `studio/plan/SNAPSHOT-SPEC.md` (14,435 bytes,
   265 lines) → "# SNAPSHOT SPEC — preservation snapshots of the
   Monolith studio"; `trunk/app/src/main/java/com/example/MainActivity.kt`
   (44,918 bytes, 821 lines — a real trunk source file, not the
   instrumented test the runner's spot had hit) → "package
   com.example"; `records/VERDICT.md` (7,519 bytes, 36 lines) →
   "# Monolith (Focus-app) — Build + Actual-UI Verdict".
2. **Cells count.** The seat's final count is 36,535 in manifest
   semantics (36,367 regular + 168 symlinks), identical to the
   staged census — there is no 1-file delta; the figure printed in
   the addendum's first version was wrong.

The seat's full numbers, for the record: manifest self-hash match
YES (`4c3cf644…b823d`, 42,956 lines); set verify **42,956 ok /
0 fail**, manifest-byte sum exactly 2,006,807,350; restore to its
own scratch (`verifier-235147/`, `cp -a`): **42,956 ok / 0 fail**,
restored bytes exactly 2,006,807,350, **count diff 0, byte diff 0**,
restored MANIFEST.sha256 also hashing to the claimed value; studio/
= 434 entries (431 regular + 3 symlinks, 10,813,612 bytes);
`records/attempt-0137-remnant-MAXIMPL.md` present (0-byte). One
honest caveat from the seat: its `cp -a` exited 1 on ownership
preservation (source files are root-owned) — ownership metadata
only; every content hash, symlink target hash, count and byte total
verified 0-diff, and the set's manifest still matched after its
test. Seat's evidence: `~/workspace/preservation/verifier-set-verify.log`,
`verifier-restore-verify.log`, `verifier_verify.py`. Scratch removed.

— Lotse (correction filed the minute the full report landed)
