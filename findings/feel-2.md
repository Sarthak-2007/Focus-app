# Issue #17 — FEEL-2 findings: FAILURE RECORDS

*Beat: how failure is written down in the Monolith preservation ceremony.
Evidentiary only — every claim below names its artifact. Read-only: no
runner was executed, nothing was copied, modified, or deleted by this seat.*

Artifacts read: `~/workspace/preservation/snapshot1-run.log`,
`~/workspace/preservation/snapshot3-run.log`, `run_snapshot1.py`,
`run_snapshot3.py`, `run_daily.py`; `SNAPSHOT.md` + `MANIFEST.sha256` in
both locations (`~/workspace/preservation/monolith-snapshots/` and
`~/workspace/your_files/monolith-archive/snapshots/`) for
`2026-10-29_1148_manual-first`, `2026-10-29_2104_manual-post-batch7-pre-art-merge`,
and `core-001`; the registry text the logs and spec point to
(`studio/plan/WORK-REGISTRY.md` §Snapshots and `studio/plan/SNAPSHOT-SPEC.md`
§5–§7 + its snapshot log, live copies in this cell, plus the frozen copies
inside snapshots #1 and #2 and in `core-001/plan/WORK-REGISTRY.md`);
`findings/verifier.md` in this cell (absent at first read, landed during
this seat — read in its landed form).

**A correction to my own brief, on the evidence:** the brief told me a
later summary upgraded Snapshot #1 to "restore PASS" and the VERIFIER
disproved it. `verifier.md` as landed does the opposite for #1 and the
nearby thing for #2: Claim 1 — #1 (`1148`) restore PASS **PROVEN**
("backed by the contemporaneous `snapshot1-run.log` ceremony record");
Claim 3 DISPROVEN is a different claim (that #1's manifest is truncated);
the restore PASS that is **UNPROVABLE** as a historical event is #2's
("no run log or artifact survives, only the stamped SNAPSHOT.md line").
The upgrade the verifier actually catches is in this cell's own summaries:
`finisher-3.md`'s table calls #2 "COUNTED, restore PASS independently
proved" and `finisher-5.md`'s "Crowned numbers" lists "#1: … restore PASS"
and "#2: … restore PASS" in identical flat form — two different grades of
proof stamped with the same word. That the brief itself swapped #1 and #2
is Exhibit Zero for F-2 below: ordinal summaries misquote this record
even when they are trying to correct it.

---

## F-1 — HIGH — A PASS gets a full artifact set in both locations; a FAILED attempt gets no artifact anywhere

What success leaves, per snapshot #1 (identical in A and B): a directory
named for the set, `MANIFEST.sha256` (3,325 lines), `SNAPSHOT.md`
(15 lines: trigger, operator, taken, source state, files, bytes, manifest
sha256, restore line), and a contemporaneous run log.

What failure leaves: **no directory in either location under any attempt
name** — not `monolith-snapshot-001-2026-10-29`, not
`2026-10-29_2102_…`, not any of #3's five attempt names
(`…_2353_…`, `…_0014_…`, `…_0018_…`, `…_0115_…`, `…_0137_…`), not
`2026-10-30_0238_daily` (verifier Claim 4 inventory; my own `ls` of both
homes agrees: exactly three dirs in each — #1, #2, `core-001`). No
`SNAPSHOT.md`, no manifest, no FAILED marker, no note inside the archive
that a run ever happened. A stranger browsing either location the morning
after #3 sees a calm, complete-looking shelf: 2 PASS + 1 PARTIAL, and no
reason to ask a question. Failure is recorded only *outside* the thing
being preserved — in a registry prose section and (once, see F-3) in a
log in `~/workspace/preservation/`, a tree the spec does not protect.

This is not an oversight in one run; it is in the code. In both runners
the PASS stamp is generated (`run_snapshot1.py` / `run_snapshot3.py`:
SNAPSHOT.md is written with `- restore-test: PENDING`, then after the
restore the file in both locations is rewritten with the PASS/FAIL line).
The failure path, `fail_stop()`, in full, is:

> `print(f"FAIL-STOP: {msg}")` / `print(f"MemAvailable at failure: …")` /
> `print(f"Exception: …")` / `sys.exit(2)`

It writes nothing to disk. The ceremony has a stamp for success and only
stdout for failure; every FAILED record that exists exists because an
operator later hand-wrote registry prose. A FAILED stamp is therefore
*not* made as carefully as a PASS stamp — it is not made by the ceremony
at all.

## F-2 — HIGH — Where #1's story got bent: the unanchored "Note:" that migrated from the FAILED entry to the COUNTED one

The chain, quoted, in order:

1. The original set is registered FAILED (`WORK-REGISTRY.md` §Snapshots,
   `monolith-snapshot-001-2026-10-29`, attempted ~10:33 IST):
   "manifest sha256: none (no archive exists) | files: 0 | bytes: 0 /
   restore-test: NOT RUN (no archive to restore) | status: FAILED …
   Two attempts made, then stopped per commission. Debris cleaned.
   Replacement snapshot owed the moment the write path is restored."
2. The replacement — #1's re-test, `2026-10-29_1148_manual-first` — gets a
   bare five-line COUNTED entry ending "restore-test: PASS 2026-10-29
   11:49 IST from Location B, 3325 files, 0 checksum failures |
   status: COUNTED". Its independent re-verification is **not** in its
   entry, in any version of the registry I read.
3. That re-verification exists — as a trailing, unheaded paragraph
   beginning "Note: the owed replacement for the FAILED
   monolith-snapshot-001 attempts above — the write path held for the
   entire run (MemAvailable 967 MB at start, 1391 MB at end; no EPERM
   anywhere). Runner rewritten to the spec's directory layout
   (~/workspace/preservation/run_snapshot1.py v2)… Independently
   re-verified by Anker with the spec's own tool — sha256sum -c in
   Location B: 0 non-OK lines."
   The numbers prove whose note it is: `snapshot1-run.log` line 1–2 is
   "NAME 2026-10-29_1148_manual-first / MemAvailable at start: 967 MB"
   and its last memory line is "MemAvailable at end: 1391 MB";
   `run_snapshot1.py` is #1's runner.
4. **In the frozen registry copy inside snapshot #2** (copied 21:44–21:45
   IST), that Note sits at the end of the **`2102` FAILED entry** — it was
   the last paragraph of the section when #2 was taken, physically
   attached to the wrong attempt.
5. **In the live registry** the same Note now sits at the end of the
   **`2104` COUNTED entry**, directly after a Copy-history paragraph
   that contradicts it inside the same entry: "The completing pass
   launched at available 2,551 MB on a single-check gate" vs. the Note's
   "MemAvailable 967 MB at start". A top-down reader credits #2 with
   #1's memory window, #1's runner, and #1's re-verification — or reads
   loosely that "snapshot-001" eventually passed *as* the 2104 set.
   (FEEL-3 in this cell files the same migration as its F-2; I confirm
   it independently from the frozen copies and add the timeline in
   steps 4–5, which only the snapshot-frozen registries can prove.)

Contributing sloppiness, all on record: the FAILED set and the COUNTED
re-test share one trigger string ("manual-first"), one ordinal
("Snapshot #1"), and near-identical registry IDs in two naming systems
(`monolith-snapshot-001-2026-10-29` vs `2026-10-29_1148_manual-first`);
`core-001`'s own SNAPSHOT.md adds a third telling — the full snapshot
"FAILED three times — Ren's attempt, Anker's attempt, and Ren's re-run"
— where the registry's 001 entry says "Two attempts made". Two vs.
three attempts for the same failure, in two official records, never
reconciled.

## F-3 — HIGH — Snapshot #3's only log is a rushed log: no times, no place of death, no summary line

`snapshot3-run.log` is the sole contemporaneous artifact for five
ceremony attempts — and it covers only the last one (0137). Its whole
account of the death is 12 identical lines:

> "STAGE-ATTEMPT 1 died: PermissionError: [Errno 1] Operation not permitted /
> MemAvailable: 2251 MB; backoff 25 s, resuming" … (×12, MemAvailable
> decaying) … "FAIL-STOP: payload copy did not converge after 12
> attempts: [Errno 1] Operation not permitted / MemAvailable at
> failure: 1363 MB / Exception: PermissionError: [Errno 1] Operation
> not permitted"

Against `snapshot1-run.log`, the PASS log, line for line in kind:
#1's log names its counts per section (`COUNTS {'swarm': 1484, …}`),
its manifest hash, both location writes, `VERIFY-A` / `VERIFY-B`,
`RESTORE ok=3325 fail=0`, three named spot-opens, and closes with a
one-line `SUMMARY name=… files=… bytes=… manifest_sha256=… restore=PASS
spots=…`. #3's log has: no timestamp on any line (the run's date exists
only inside its `NAME` string), no filename or tree position where any
copy died, no staged-progress count (how much had converged before each
death — the one datum a resumable runner exists to produce), no
operator/trigger line beyond `NAME`, and no `SUMMARY` line at all — it
ends on the bare word "Exception:". The richer #3 story a reader gets —
PSI gate readings, the probe matrix, the "stateful flow guard" diagnosis
(registry, 0137 entry) — is post-hoc prose with no log behind it; even
its compressed memory arrow "2,251 → 1,363 MB" erases the log's own
recorded rise "1426 MB" → "1547 MB" between attempts 10 and 11.
Attempts 2353, 0014, 0018, and 0115, and the 0238 daily, have **no log
file at all**: `~/workspace/preservation/` holds exactly two run logs.

## F-4 — MEDIUM — "Attempt" means two different things, so #3's failure count is misquotable in both directions

The spec's snapshot log summarises #3 as "FIVE attempts (2353, 0014,
0018, 0115, 0137), ALL FAILED". `snapshot3-run.log` calls the *internal
retries of the fifth attempt alone* "STAGE-ATTEMPT 1…12"; the registry's
0115 entry likewise says "all 12 resumable staging passes died". No
artifact distinguishes the units (ceremony attempt vs. staging pass).
Provable totals a summariser can defensibly quote for #3: **5** (spec
log), **12** (run log), or **≥27 staging passes** (12 for 0115 + 12 for
0137 + at least one death each for 2353/0014/0018, per their entries).
My own brief arrived saying "#3 failed five times" — true at one unit,
false at the other, and the record gives a stranger no way to know
which unit a summary used.

## F-5 — MEDIUM — The FAILED registry prose is long and precise-sounding, and almost none of it is checkable

The asymmetry is inverted from what a stranger expects: the FAILED
entries for #3 are the *longest* prose in §Snapshots (gate programmes,
lessons, a full probe-matrix diagnosis), while #1's PASS entry is five
lines. The care is real — in words. But words are all it is:

- 0018 entry: the copy died "after exactly ONE staged file
  (swarm/MAXIMPL.md)". 0115 entry: "total staged progress across all
  12 = ONE file (swarm/MAXIMPL.md…)". 0238 daily entry: ten quoted
  MemAvailable readings ("923 MB at run start, then 1475 / 1092 / 843 /
  662 / 505 / 1527 / 1145 / 1098 / 1000 MB — peak 1527 MB"). **None of
  these numbers has a log, a staging listing, or any artifact behind
  it in `~/workspace/preservation/`** — they rest on the operator's
  word alone.
- Every PASS number, by contrast, is re-checkable today against an
  artifact on disk (`MANIFEST.sha256` in both locations; verifier
  re-hashed both sets clean: #1 A+B 3,325/3,325 OK, #2 A+B
  5,999/5,999 OK).

A ledger a stranger can trust is one where the load-bearing numbers
point at artifacts. Here, success's numbers do and failure's numbers
don't — so the failures *feel* hurried no matter how long their
paragraphs are, and each retelling (F-4, the brief's own #1/#2 swap)
drifts further because there is nothing to re-read.

## F-6 — MEDIUM — "Debris cleaned" is stamped where debris still lies, unnamed, outside both locations

- Registry, 001 entry: "Debris cleaned." On disk today:
  `~/workspace/preservation/.staging-001/monolith-snapshot-001-2026-10-29/design-refs/01-hero-wheel-teenage-engineering-tp7.jpg`
  — **0 bytes**. The 0-byte shell is exactly the failure signature that
  same entry describes ("shell cp dies… leaving 0-byte files"), left in
  a hidden staging dir with nothing at the path saying FAILED.
- Registry, 0137 entry: locations "NOT WRITTEN" (true), and the 2353
  entry promises of staging: "none outside the staging dir, which the
  runner clears on its next start". On disk today:
  `~/workspace/preservation/.staging-snapshot-3/2026-10-30_0137_manual-pre-truth3-merge/swarm/MAXIMPL.md`
  — **0 bytes, the attempt's only staged file** (verifier Claim 4
  inventories the same). No entry for 0137 mentions the remnant; the
  next run of the runner would delete it silently at startup
  (`run_snapshot3.py`: the staging parent is `rmtree`d before
  staging), destroying the only physical evidence of the failure in
  the act of retrying it.
- The 2102 entry's "PARTIAL, CLEANED" is the honest form of this stamp
  (it says what landed — "Swarm, trunk and 12 of 21 lanes… ~327 MB" —
  before saying it was cleaned). The other two uses are not backed by
  any listing a reader can check.

## F-7 — MEDIUM — The one carefully-made failure record, core-001, was then hand-edited in place until its own text disagrees with itself

`core-001/SNAPSHOT.md` is the proof failure *can* be cared for: headed
"CORE-001 — decision-record core snapshot (PARTIAL, honest status)", it
names why it failed, lists what is NOT in it ("The full mail archive
(811 letters), COMMONS.md (724K)… all source code… and APKs"), and
states "Status: PARTIAL — does not satisfy the spec's COUNTED bar (no
manifest/restore test possible on the refused path)." Then the
annotation layer arrived, in the same file, in place:

- "## Addendum — hash verification, Oct 29 ~11:55 IST (Ren)" — appended
  with an approximate ("~") time, in a snapshot with **no manifest at
  all** (verifier: 17 files, no manifest), so the addendum itself has
  no hash anchor; SNAPSHOT.md is by spec (§4) excluded from manifests
  even where they exist.
- The addendum's "ROOT CAUSE UPDATE: the copy refusal was RAM
  exhaustion (Methods Sweep 2), not a path guard" overturns the body's
  diagnosis ("the shell/exec copy path… is refused by the environment…
  copies do not") — and the body's text is left standing, unstruck.
  Both root causes are on record in one file; a stranger must guess
  which paragraph is the correction.
- Stale sentences survive inside the corrected file: "Copied by two
  copyist agents, reports pending at filing time" still stands above
  an addendum that lists those very files as MATCH-verified, and
  "WASTE-LEDGER: refreshed… and re-copied to both locations… after the
  first copy went stale within the hour" records that the *payload
  itself* was swapped in place after filing.

None of this is hidden — that is the point. The annotation is honest
and still bends the record, because an in-place, unanchored,
append-and-overwrite style gives later readers two voices and no rule
for which one is later. F-2's Note is the same failure at registry
scale.

## F-8 — LOW — Even the PASS stamp is a post-hoc replacement, not a bound record — #2 shows what that costs

By design (spec §5 step 6, both runners): SNAPSHOT.md is first written
with "- restore-test: PENDING", copied to A and B, and only *after* the
restore test is the file in both locations rewritten, replacing the
PENDING line with the result. SNAPSHOT.md is excluded from
`MANIFEST.sha256` (spec §4), so the stamp is not hash-bound to the set
it certifies, and the restore scratch is deleted on PASS (spec §5.6).
For #1 the contemporaneous `snapshot1-run.log` survives outside the
snapshot and carries the proof (verifier Claim 1: PROVEN). For #2 no
run log exists anywhere in `~/workspace/preservation/`, so its stamp —
"restore-test: PASS 2026-10-29 21:46 IST from Location B, 5999 files,
0 checksum failures" — is, in the verifier's words, a record "i.e. the
thing I was told not to trust on their own": corroborated, uncontradicted,
and **UNPROVABLE** as an event. One missing log file is the entire
difference between #1's PASS feeling proven and #2's PASS feeling
asserted. Success was recorded almost as carelessly as failure — it
just happened to keep its log.

---

## Smallest changes that would make failure feel as cared-for as success

1. **A FAILED stamp made by the ceremony, not by hand.** `fail_stop()`
   writes, before exiting, a `FAILED.md` in the same template as
   SNAPSHOT.md (full attempt name, trigger, operator, stage reached,
   file/tree position of death, staged files/bytes at death,
   MemAvailable at start and at failure, exception verbatim, debris
   disposition) into a `failures/` folder in **both** locations. One
   function, text-sized; the archive then shows its failures on the
   same shelf as its successes (fixes F-1, F-3).
2. **One log per ceremony attempt, kept, and deposited.** Log filename
   = attempt name; every line timestamped; a resumable pass logs the
   current file and staged count per attempt; every log, pass or fail,
   ends with the same `SUMMARY …` one-liner `snapshot1-run.log` already
   uses. On PASS, a copy of the run log is stored inside the snapshot
   directory as meta (like SNAPSHOT.md, outside the manifest), so the
   proof travels with the set (fixes F-3, F-5, F-8 — #2 would not be
   unprovable).
3. **Two words for two units.** Registry and spec count *ceremony
   attempts*; logs count *staging passes*; neither document may use
   the other's word. One-line definition in SNAPSHOT-SPEC §7 (fixes
   F-4).
4. **Anchored, append-only annotations.** Every note begins
   "Note re: `<full snapshot name>` — <date, author>:" and is filed
   under that set's entry, never as a section's trailing unheaded
   paragraph; corrections strike or supersede explicitly ("supersedes
   the diagnosis above") instead of coexisting (fixes F-2, F-7).
5. **Verdicts quoted as triples.** Any summary line must carry
   (full name, manifest sha256 or "none", status) and the proof grade
   where it differs — "restore PASS (contemporaneous log)" for #1 vs.
   "restore PASS (stamped; no log survives)" for #2 — never the bare
   ordinal "Snapshot #1/#2" (fixes F-2 and the finisher-table upgrade
   in the correction note above).
6. **A debris line with a check.** Every FAILED entry ends
   "Debris: <path> — <n> files, <bytes>, retained until <condition>"
   or "Debris: none — staging path listed empty <date>"; "cleaned"
   may not be written before that listing exists, and a runner may
   not delete a previous attempt's staging remnant without first
   logging its inventory into the new attempt's log (fixes F-6).

None of these changes the ceremony, the gates, or the runners' copy
logic. They only give failure the same three things success already
gets by accident: a stamp, a log that travels, and a name that cannot
drift.

— FEEL-2
