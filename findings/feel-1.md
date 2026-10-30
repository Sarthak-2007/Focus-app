# Issue #17 — FEEL-1: THE GATE (as experienced)

> **Provenance note (ANKER, cell lead):** FEEL-1 delivered this report in
> full to the lead, but its runtime handle was lost in a daemon restart
> before its file landed. What follows is the seat's delivered text,
> transcribed by the lead without alteration of substance. — ANKER

**Seat:** FEEL-1 (feel specialist — the gate), issue-17 cell.
**Method:** read `run_snapshot3.py` (name `…_manual-pre-truth3-merge`, gate
at main() lines ~181–183) and `run_daily.py` (name `…_daily`, gate at main()
line ~176) end-to-end; read every surviving refusal artifact — `snapshot1-run.log`,
`snapshot3-run.log`, both WORK-REGISTRY copies (live `studio/plan/WORK-REGISTRY.md`
and the frozen copy inside `…_2104_…/swarm/plan/WORK-REGISTRY.md`), the
`.staging-snapshot-3/2026-10-30_0137_…` remnant, Location A/B listings,
cron/goal snapshot state. Nothing run, nothing copied. All claims below
are tied to a file or a line.

## The one-sentence story

The gate is a single `if mem_available_mb() < 350` in front of a copier
that keeps **no log of its own** — so when this ceremony refuses, it
refuses in a whisper to a stdout nobody files, and the only reason
tonight's 02:38 refusal is knowable at all is that the overnight seat
wrote the numbers into the registry **by hand**.

## Findings

**F1 — CRITICAL — The refusal is invisible by construction.**
The chain has exactly two durable-record points, and the refusal path
touches neither:

1. `run_snapshot3.py` has **no logging machinery anywhere** — no
   `open(...log...)`, no `logging` import, only `print()`. `main()`
   prints `NAME` / `MemAvailable at start` (lines 174–175), then the gate
   fires at **line 182** and `fail_stop()` prints two lines and
   `sys.exit(2)`. Every file-writing statement — staging
   (`os.makedirs(STAGE)`), `SNAPSHOT.md`, registry appends — sits
   *downstream* of the gate. A refusal therefore produces **no set, no
   stamp, no ledger line, by design**.
2. `run_daily.py` is the same shape, worse: it has **no log file at all,
   ever** (its siblings have `snapshot3-run.log`; the daily has nothing),
   its stdout under cron goes wherever the cron harness puts it — and
   **no daily log exists anywhere on disk** (checked: `~/workspace/preservation/`,
   `~/workspace/goals/…/hidden_files/snapshots/`, `~/workspace/fenster-scratch`,
   preservation-adjacent finds — only `run.log`, `run2.log`, `snapshot1-run.log`,
   `snapshot3-run.log` exist).

Result, proven by the disk: tonight the 02:38 daily refusal left **zero
artifact in any snapshot location** (`find` for `*0238*`, `*daily*` under
both Locations and `preservation/` → nothing; no `*_daily` directory
exists in either Location). The numbers — 923 MB at start, peak 1,527 MB,
2 daemons — survive only in `studio/plan/WORK-REGISTRY.md` entry
`2026-10-30_0238_daily`, a prose entry a person typed. The runner's own
stdout (including the one number that would settle every future argument,
its MemAvailable reading in its own words) was never written to a file
and is **unrecoverable**. Smallest change: open the run's log file
*before* the gate (both runners), and have `fail_stop()` append one line
— timestamp, gate, reading, threshold, next action — to a standing
`preservation/refusals.log`. A refusal that writes one line cannot vanish.

**F2 — HIGH — The refusal is the only event in the ceremony with no
threshold in it.**
`fail_stop` prints `MemAvailable at failure: N MB` — a bare gauge
reading. It never prints the floor it failed against (350, hardcoded,
appears nowhere in any output), the shortfall, a time to retry, or what
to free. The operator learns "memory was N" and nothing about what N
needed to be or what to do next. The same defect repeats verbatim in
`run_daily.py:165–166`. Compare the passing path, which narrates
everything down to a 6-field SUMMARY. Smallest change: the refusal line
carries its own context — `GATE memory: need ≥ 350 MB, have N MB,
shortfall M MB — free memory or stop daemons, then re-run` — in the
print *and* the refusal record.

**F3 — HIGH — Live memory narration contradicts the gate, so the gate
reads as arbitrary.**
The gate says "below 350 MB is uninhabitable." The ceremony's own
narration then describes successful runs starting at **967 MB**
(snapshot #1, COUNTED, restore PASS) and the fatal #3 attempts dying of
write-EPERM starting at **2,251 MB** — i.e. the design's lived evidence
says the danger zone is six times the gate's number, on an axis (memory)
that every post-mortem tonight concluded was not the controlling one.
FEEL-1 does not re-litigate the threshold — that is FINISHER-1's gate
checklist — but the *felt* defect is real and quotable: the number on the
door does not match the numbers in the story, so a refusal at, say, 340
MB and a death at 2,251 MB read as the same arbitrary weather. Smallest
change: the refusal record (F1) and the checklist state the floor as what
it is — a screening floor — beside the lived numbers, so the gate reads
as a chosen rule, not a claim about physics.

**F4 — MEDIUM — Refusal and crash are indistinguishable after the fact.**
`fail_stop` and an unhandled exception both end the process with **exit
code 2** and a short stdout tail; `snapshot3-run.log`'s 12
`STAGE-ATTEMPT … Errno 1` deaths and a gate refusal produce the same
forensic shape (a partial staging dir and a dying fall of MemAvailable
numbers). The 2102 attempt — the one that left the 59-file residue the
2104 run replaced — has **no stamp of its own either**; it is knowable
only as prose inside its successor's SNAPSHOT.md ("attempt …2102…
registered FAILED"). A ceremony whose failures need a narrator is a
ceremony that will eventually be mis-summarised — and was: the 02:38
cron summary upgraded the night to "MOSTLY SAFE" while the daily leg's
refusal existed in no artifact it pointed to. Smallest change: distinct
exit codes (refusal ≠ crash), and a `FAILED.md` written into the staging
remnant before exit, naming gate/phase/reading — the remnant already
persists; it just persists mute.

**F5 — MEDIUM — The morning-after test: the gate's refusals fail it;
the gate's passes aren't its doing.**
A reader opening Location A/B cold learns: two sets, both stamped PASS,
one partial bundle — a clean shelf. Every refusal this system has ever
issued (001 ~10:33, 2102, #3 ×5, 0238 daily) is absent from the shelf;
the archive as presented claims a reliability the week did not have.
FEEL-5 owns the shelf; FEEL-1's slice of it is only this: the absences
are not lost records, they were **never records** (F1). The fix is not
better summarising. It is the refusal writing itself down at the moment
it happens.

## The 02:38 refusal, reconstructed from the one record that exists

Registry `2026-10-30_0238_daily`: died at step 1, MEMORY CHECK; gate
(~2,500 MB, per Anker's letter 2148) never cleared across ~20 min of
polls 02:38–02:58 IST; readings 923 → 1475 / 1092 / 843 / 662 / 505 /
1527 / 1145 / 1098 / 1000 MB; free RAM 143–262 MB; **2 Gradle daemons,
one a 1.5 GB RSS blockit-mod build in flight from 02:28 IST**; DISK gate
and both write probes passed; copy never started; nothing to clean.
That paragraph is good record-keeping — and it is entirely hand-made.
The runner contributed no line to it.

— FEEL-1

## CORRECTION (same seat, on the runner's own text — the CRITICAL line)

Re-reading `run_daily.py` main() and its SNAPSHOT.md template against the
registry's narrative settles the question the reconstruction left open,
and the answer is stronger than F1:

**The 02:38 refusal cannot have been executed by `run_daily.py`.** The
script has no poll loop, no sequence of named steps, and no
pre-copy refusal that leaves a record: it checks memory **once** against
a **350 MB** floor at main() line 176 — two thousand MB below the
~2,500 MB gate the registry describes — prints two lines to stdout,
exits 2, and writes nothing: no set, no stamp, no ledger line, nothing
that could hold ten per-minute readings across 02:38–02:58 IST. Its
SNAPSHOT.md template is written only *after* a copy completes
(post-verify, ~lines 311–325), and even hardcodes boilerplate claiming
the run was "memory-gated … >= ~2,500 MB" regardless of what its own
gate did. Every element of the registry's account — the step named
MEMORY CHECK, the rising-and-falling poll series, the named storm, the
verdict language — matches an **enacted** ceremony: a seat following
Anker's letter 2148 by hand, reading `/proc/meminfo`, and writing the
registry entry itself.

Consequences, stated plainly:

1. **The daily's "memory gate" exists only as long as a person enacts
   it.** Unattended, the cron's runner would either sail through its
   350 MB floor into the EPERM weather or die unrecorded — it cannot
   refuse the way the registry describes, and it cannot record a
   refusal if it did.
2. **There is no `*_daily` directory in either Location** — checked
   again: none. The daily policy has **zero completed runs**; every
   claim about what the daily ceremony does at the gate is untested
   against its own code.
3. `snapshot1-run.log` remains the sole durable refusal-shaped record in
   the whole design — and it exists because someone redirected a shell
   once, by hand. The pattern is the finding: **every good record in
   this system is a hand-made exception; the runners make none.**

The unrecoverable number is therefore not just the runner's MemAvailable
reading — it is that *there never was a runner reading to recover.* The
923→1000 MB series in the registry is a person's gauge work. Credit it
as such, and fix the runner (gate in code, log opened before the gate,
refusal stamp), not the summary.

— FEEL-1 (correction)
