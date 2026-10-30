# Snapshot #3 — RUN PLAN (Issue #17 cell, for Ren's execution)

Prepared by ANKER (cell lead) with PLANNER, INNOVATOR, CRITIC, VERIFIER,
FINISHER-1…5, FEEL-1…5. Evidence: `findings/` on this branch (14 seats).
Nothing in this plan has been executed. Ren runs the snapshot; nobody in
this cell copies gigabytes on their own initiative.

## 0. Standing verdict

**Do not run `~/workspace/preservation/run_snapshot3.py` as-is.**
On a healthy window it would, in FINISHER-4's measured projection: copy
every byte perfectly, silently omit the studio record (the deleted swarm
tree stages as 0 files — `os.walk` on a missing directory does not raise),
verify restore fail=0, then **fail the verdict** on its two hardcoded
swarm spot-checks, stamp `RESTORE-TEST FAIL` permanently into both
archive copies' SNAPSHOT.md, delete the staging tree and restore scratch
(the only re-proof evidence), print a normal SUMMARY, and **exit 0**.
The run below is the amended ceremony, not the staged one.

## 1. The archive as measured (crowned numbers, FINISHER-5; proof-graded)

Verdicts travel as triples — (full name, manifest sha256, verdict) —
per FINISHER-3's rule. Ordinals are commentary, never identifiers.

| Set | Files | Bytes (manifest-sum) | Manifest sha256 | State |
|---|---|---|---|---|
| `2026-10-29_1148_manual-first` ("#1") | 3,325 | 568,973,290 | `11ada2f4…e05fcf` | Integrity PROVEN (full re-hash A+B, 3,325/3,325 OK); restore PASS PROVEN by contemporaneous `snapshot1-run.log` |
| `2026-10-29_2104_manual-post-batch7-pre-art-merge` ("#2") | 5,999 | 974,007,921 | `0de9adf2…4866c` | Integrity PROVEN (re-hash 5,999/5,999 OK both homes); restore PASS stamped but **UNPROVABLE as an event** — no run log survives; the set is its own only witness |
| `core-001` | 16 payload files (17 on disk) | 414,511 | none | PARTIAL decision-record bundle. Not a workspace snapshot; no manifest, no restore test. Needs a ruling, not a shrug (FEEL-3/F-3, FEEL-5/F-5) |
| Snapshot #3 | — | — | — | Does not exist. Five registered attempts: 2353, 0014, 0018, 0115, 0137. Only 0137 has a log; its staging remnant is exactly 1 file, 0 bytes |
| `2026-10-30_0238_daily` | 0 | 0 | none | Refused at the memory gate; record exists only as registry prose (see §7) |

**The "truncated #1 manifest" is disproven and is hereby retired.**
VERIFIER (full re-hash), CRITIC (C-13), FINISHER-5 and the lead's own
measurement agree: both surviving copies of #1's MANIFEST.sha256 hold
3,325 well-formed lines, end complete, and hash to the value recorded at
creation. The flaw lives in the *records about* the artifact (a claim
copied from brief to brief), not in the artifact. Registry repair: the
correction gets written under both full names — `monolith-snapshot-001-2026-10-29`
(the FAILED tar-era attempt, no archive) and `2026-10-29_1148_manual-first`
(the surviving set) — so the ordinal stops swapping their verdicts
(FEEL-2/F-2, FEEL-3/F-1, FINISHER-3 §10).

**Why #3 matters more than it did yesterday (CRITIC C-2, C-7):** since
Oct 29 21:45 everything is unpreserved — batch 9, the merge tree with its
5 uncommitted test fixes, and the issue cells' uncommitted findings —
and since the Oct 30 cleanup, **snapshots #1/#2 are the sole surviving
copies of the studio's deleted mail archive and wall**, on one disk.

## 2. Decisions Ren rules before the run (cell recommendations attached)

- **D-1 Studio record:** repoint the dead swarm capture to the repo's
  `studio/` tree, captured as `studio/` (431 files, 12 MB). Not "drop" —
  it is the irreplaceable part. (FINISHER-2 P-1, rec C)
- **D-2 Cells:** capture `builds/cells/` **without** `.git` (~390 MB).
  The "cells are on GitHub" rationale is false today — findings-only and
  unpushed cell state exists (CRITIC C-7). (P-2, rec B)
- **D-3 wt-ci:** exclude as a tree (pure checkout, on GitHub); its only
  load-bearing content is `studio/`, covered by D-1. (P-3, rec A)
- **D-4 Lane `.git`:** prune (193.9 MB, 39% of lane payload, 1,204 files)
  **only after** a per-lane unpushed-commit/stash check; any dirty lane
  keeps its `.git` as a named exception in SNAPSHOT.md. (P-4)
- **D-5 Probe fixtures:** prune `merge-batch9/guh-test/` — side effect:
  payload then holds zero symlinks, simplifying the restore census. (P-5)
- **D-6 Design-refs flat files:** capture the 33 files (3.1 MB) beside
  the design-refs root; two are studio work product. (P-6)
- **D-7 Name:** mint under FEEL-3's grammar —
  `snap-003__<date>_<taken-HHMM>IST__manual__db12-post-batch9-github-studio__<verdict-at-sealing>`.
  The staged `_manual-pre-truth3-merge` name lies about the moment
  (batch-8/DB v11/pre-merge) and the spec forbids renaming after the
  fact — this is a pre-run fix, not an annotation. (FEEL-3/F-7)
- **D-8 Off-box leg (the structural decision):** A and B are the same
  spindle (`/dev/mapper/rv`, one VM, one account) — the ceremony survives
  fat-fingers, not box death (CRITIC C-1/C-2, FEEL-5/F-2). Cell's ranked
  answer (INNOVATOR): (1) standing fix = content-addressed restic to
  R2 + B2 with signed manifest index committed to this repo — ₹0/month
  at today's size, ~0.3–0.45 GB stored for all three sets (measured
  dedupe: #1+#2 sum 1,543 MB, only 492 MB unique), survives box death +
  account loss + corruption; honest cost: a lost restic password loses
  the archive — Vault + offline copy with Sarthak, procedural.
  (2) Interim, same-day: each set as one zstd volume on a **private
  archive repo's** releases (~1.71 GiB total compressed, under the 2 GiB
  asset limit, $0). **Backfill #1 and #2 off-box with #3, do not wait** —
  they are sole copies of deleted history. Archive *branch* is rejected
  on evidence (permanent bloat every clone pays). LFS is a trap
  (pointer files on restore day after quota).
- **D-9 The daily line:** one runner, wired, or none. Today the cron
  body, `run_daily.py`, and the spec contradict each other (gate 350 in
  code vs ~2,500 in text; a spec "lockfile" grep proves doesn't exist;
  the cron body points into the deleted swarm tree and never mentions
  run_daily.py) — CRITIC C-5. Recommendation: repair run_daily.py
  (gate number in code, log opened before the gate, FAILED stamp on
  refusal) and point the cron at it, or retire the daily line honestly.
- **D-10 Storefront:** write `INDEX.md` into both store roots (sets,
  triples, twin note, "backups — do not delete"), and rule on core-001
  under its mapped name. (FEEL-5/F-1/F-3)

## 3. The gate (FINISHER-1 — seven gates, checked in this order)

1. **Memory screen:** MemAvailable ≥ 2,500 MB — a screening floor,
   necessary not sufficient. The script's 350 MB floor is rejected
   (deaths are logged at 2,251 MB; FEEL-1/F-4, CRITIC C-9). Retiring the
   floor itself on falsification grounds is Ren's ruling, not the
   checklist's.
2. **No Gradle daemons resident** (`pgrep -af gradle` clean) and no
   build in flight.
3. **Write probe — the decisive gate:** 100 small files + one 100 MB
   file written and re-read in *both* destinations, zero EPERM. A pass
   is valid only for the window in which it was taken; yesterday's pass
   proves nothing today.
4. **Disk:** ≥ 10 GB free for #3 (the daily line's own floor is 15 GB —
   different rule, D-9). Measured headroom today: 123 GB, ~57× the
   worst-case 4×-payload peak. Disk has never been the binding gate.
5. **Fresh runtime generation** (cure-hunters' law): prefer the minutes
   after a runtime restart.
6. **Lanes quiescent:** no cell mid-write; `git status` census of the
   payload trees taken and kept with the run log.
7. **Gate record first:** before anything else happens, the gate's own
   readings (MemAvailable series, daemon census, probe result, disk)
   are appended to the run's log file on disk. **A refusal is recorded
   the same way** — a dated refusal file in `preservation/` — because in
   the current design a refusal leaves no artifact at all (FEEL-1/F-1,
   FEEL-2/F-1). If the gate says no, the run stops; the record remains.

Right now is **not** a window (PLANNER, measured during preparation):
MemAvailable ~1.3–1.7 GB and falling, a Gradle daemon resident, load ~10.

## 4. Amendments to the runner — before any run (FINISHER-4: A1–A16)

Ordered, each checkable by grep/read before launch. The three blockers:

- **A3+A4 (was D1):** a missing payload root must raise, never stage
  silently as 0; swarm capture repointed per D-1.
- **A12 (was D2):** spot-checks repointed to files that exist
  (`records/VERDICT.md`, a lane `.kt` from this set's own manifest) —
  with the strict-UTF-8 trap on binary content handled.
- **A9+A10+A11 (was D3):** name, TRIGGER, and source-state rewritten to
  the true moment (D-7); the dead "exactly 3 ghost fixtures" note and
  stale registry path replaced by census-truth.

The rest, in FINISHER-4's numbering: A1 staging-wipe guard (the startup
`rmtree` currently destroys the 0137 remnant — the 0-byte MAXIMPL.md,
sole physical evidence of attempt 0137 — without a word; FEEL-2/F-6);
A2 memory floor 350 → 2,560 MB in code, with mid-run re-checks; A5/A6/A7
the D-6/D-2/D-3+D-4 payload decisions made mechanical; A13 verify A
*before* copying B (the code currently contradicts its own ceremony);
A14 same-name A/B destruction guard; A15 fail-stop wrapping + atomic
verified stamp; A16 restore-FAIL exit code and cleanup discipline
(a FAIL must exit non-zero and must not delete its evidence).
Plus three from CRITIC beyond the planner's list: a **source-completeness
assertion** — expected per-root file counts computed from the live tree
and *asserted* before the manifest is built, ending the circular
verification in which a snapshot missing whole roots still passes (C-3);
**fsync** on manifest and stamp writes (C-8); and **per-attempt error
lines that name the file, path, and phase** (C-8, FEEL-2/F-3).
Plus the feel pack (§6) — print-only, integrity-neutral.

## 5. Execution sequence (Ren, at the healthy window)

0. Rule D-1…D-10 (§2). Apply §4 amendments; grep-verify each is in.
1. Run the gate (§3). Record readings first. On refusal: refusal file
   written, stop, report the refusal as a result — not as silence.
2. Fresh census: lanes list and counts from today's `builds/` (21 lanes
   at preparation: 497.1 MB, 5,711 files), per-lane unpushed check for
   D-4, symlink census for the manifest note (count decided by census,
   never copied from script text — FINISHER-3 §5c).
3. Run the amended runner. Payload estimate: 5,914 files / ~541 MB
   as-is; 4,710 files / ~341 MB with D-4 applied (FINISHER-5).
4. Runner self-test completes (stage → A → B → verify → restore-from-B),
   stamps in the triple grammar (§5 step 6 wording), exit code honest.
5. **Independent restore test** (FINISHER-3's procedure, a different
   event from the runner's self-test, different scratch path): inputs
   frozen from two independent records (run-log SUMMARY + registry —
   never the set's own SNAPSHOT.md alone); Gate 1 A/B manifest identity
   (four readings); Gate 2 strict manifest parse; restore from B;
   Gate 4 full verify under runner hash semantics with the §5b
   `sha256sum -c` reconciliation (symlinks scored once, in 5a, by
   target-string hash; neither failures nor silent skips); Gate 5
   counts/bytes/extras close exactly; Gate 6 content spots chosen from
   *this* set (trunk .kt, records/BUILD-CHANGES.md, a merge-batch9-era
   lane file, the set's own SNAPSHOT.md still reading PENDING, and the
   APK byte-compare against the live shipped file, all three hashes
   printed).
6. Stamp the verdict as a **triple** — (full name, manifest sha256,
   verdict) — in four places, append-only: test log first, then both
   SNAPSHOT.md copies byte-identically (`restore-test:` for the
   runner's self-test, `restore-test-2:` for the independent test),
   then the registry entry under the full name. A PASS line whose name
   ≠ the file's header name, or whose hash ≠ the manifest field above
   it, is void on its face (FINISHER-3 §9b validity rule).
7. Off-box leg per D-8 the same day (interim release asset on the
   private archive repo; backfill #1/#2 in the same sitting).
8. INDEX.md in both store roots updated (§2 D-10).
9. Report to Sarthak: one line, triple form — name, manifest sha256,
   files, bytes, verdict. Nothing else about the run reaches his chat.

## 6. Failure grammar (FEEL-2 + FEEL-4 + FINISHER-4 F0–F11)

- Every failed attempt — gate refusal included — produces a
  ceremony-generated `FAILED.md` (name, minute, phase, measured cause,
  debris state) deposited in a `failures/` folder in **both** locations.
  Today no FAILED artifact exists anywhere; failure lives in prose and
  dies there (FEEL-2/F-1).
- One timestamped log per ceremony attempt, deposited inside the
  attempt's record — not one shared log overwritten five times
  (FEEL-2/F-3). Vocabulary fixed: **attempt** = one ceremony run;
  **pass** = one internal staging retry. (Defensible counts of the
  wall-night failure currently read 5, 12, or ≥27 depending on which
  word a summariser grabs — FEEL-2/F-4.)
- Debris is a checked line, never prose: "debris cleaned" was false on
  disk for attempt 001 (a 0-byte remnant survives in `.staging-001/`)
  — the line is written only after `find` proves it (FEEL-2/F-6).
- Output feel pack (FEEL-4, print-only): per-section start lines +
  every-250-files counters in the copy; attempt-start lines; skipped/
  copied counts per pass (convergence visible, not asserted); wake
  times after backoffs; pass-start lines + every-500 counters in the
  five hash passes; IST timestamps on every line; SUMMARY gains
  `attempts=` and `elapsed=` behind a rule line.
- Registry notes are anchored and append-only ("Note re: <full name>"),
  never nested under another set's entry (FEEL-2/F-2, FEEL-3/F-2), and
  never hand-edited in place inside a sealed set (FEEL-2/F-7).

## 7. The daily retry — prepared (the cell's first concrete job)

**What happened at 02:38** (registry entry `2026-10-30_0238_daily`,
quoted by FINISHER-1; the runner itself filed nothing): the run died at
Step 1, MEMORY CHECK. The ~2,500 MB gate never cleared across ~20
minutes of polls, 02:38–02:58 IST — readings 923 MB at start, then
1475 / 1092 / 843 / 662 / 505 / 1527 / 1145 / 1098 / 1000 (peak 1,527).
The storm was concrete: free RAM 143–262 MB, **2 Gradle daemons
resident, one a 1.5 GB RSS blockit-mod build in flight from 02:28 IST**.
Two gates passed and are *not* partial successes: DISK (111 GB free vs
the daily floor of 15 GB) and the write-health probe in both
destinations. Copy never started; no debris; nothing to clean.

**Retry preconditions (all fresh — nothing carries over):**
1. Prove the named storm picture is gone: daemon census clean, no
   build in flight, and the 02:28 build's tree quiescent.
2. MemAvailable ≥ ~2,500 MB sustained across the same polling shape
   that refused (not a single reading).
3. Re-run the write probe in both destinations — the 02:38 probe pass
   explicitly does **not** carry over.
4. Disk ≥ 15 GB (the daily cadence's own floor — FINISHER-1's
   correction; it is not #3's 10 GB).

**Amendments run_daily.py needs before the retry means anything**
(otherwise a second refusal is as invisible as the first):
- the gate it actually enforces written in code (today: 350 MB in code,
  ~2,500 MB claimed in its own SNAPSHOT.md template — CRITIC C-9);
- a log file opened *before* the gate, so a refusal leaves an artifact
  (FEEL-1/F-2);
- a FAILED stamp + registry line on refusal, in the §6 grammar;
- the staging-wedge cured: every failure path currently exits before
  any verdict record and `.staging-daily` is only ever deleted by a
  success — one bad night must not silently stale-block the line
  (CRITIC C-3);
- D-9's wiring ruling: which script the cron fires must be resolvable
  from disk, not from memory.

## 8. What this cell did not do

No snapshot was run, no restore test was executed, no gigabytes were
copied, no file in either snapshot location was modified. All figures
are read-only measurements taken Fri 2026-10-30, ~03:05–03:45 IST.
The execution — amendments applied, gate run, snapshot taken, restore
tested, off-box leg shipped — is Ren's, at the healthy window, under
the rulings in §2.

## 9. Evidence index (on this branch, `findings/`)

- `planner.md` — the staged run plan; script anatomy; defects D1–D9.
- `verifier.md` — independent verdicts: #1 PROVEN, #2 integrity PROVEN /
  restore event UNPROVABLE, truncation DISPROVEN, #3 absence PROVEN.
- `critic.md` — 14 charges (C-1…C-14): same-spindle A/B, sole-copy mail/
  wall, circular verification, unbuildable restore, daily half-policy,
  merge-snapshot debt, unpreserved present, non-convergent resumability,
  gate theatre, symlink-convention proof gap, post-stamp edits, plan
  errata, the exoneration, prune drift.
- `innovator.md` — off-box design + pricing: restic/R2+B2 ranked first
  (₹0/mo at today's size), release-asset interim, LFS trap, branch
  rejected; measured dedupe 1,543 MB → 492 MB unique.
- `finisher-1.md` — the seven-gate checklist; the 02:38 record, quoted.
- `finisher-2.md` — payload manifest of intent; decisions P-1…P-6.
- `finisher-3.md` — the restore-test procedure: triple rule, seven
  gates, verdict grammar, stamp wording + validity rule, #1 repair path
  and its §12 addendum absorbing the verifier reversal.
- `finisher-4.md` — amendments A1–A16; failure grammar F0–F11;
  never-overwrite register N1–N5; the false-FAIL enshrinement.
- `finisher-5.md` — the numbers page: crowned figures, method deltas.
- `feel-1.md` — the gate as experienced: refusal invisibility
  (delivered in report; file lands with the seat's close).
- `feel-2.md` — failure records: no FAILED artifact anywhere; where
  #1's story bent, quoted; smallest fixes.
- `feel-3.md` — names and identity: the grammar, retroactive mapping.
- `feel-4.md` — the long middle: nine dead-air findings, print-only fixes.
- `feel-5.md` — the two locations and the morning after: no index,
  unmarked clutter in the user's files, false-calm shelf.

— ANKER, for the issue-17 cell
