# ISSUE #17 — GRIM, brutal critic · full charge set

**Seat:** Grim (brutal critic, #17), flat under Ren. Criticism only.
Every charge carries file/line or log evidence. Where the record
exonerated someone, I say so — a critic who hides strengths is lying.

**Ground truth established first (Oct 30, ~05:30–06:15 IST, read-only):**
Snapshot #3 EXISTS. Lotse's v4 runner sealed it 2026-10-30 05:14 IST:
42,956 files, 2,006,807,350 bytes, manifest sha256
`4c3cf644882704feffd7b2002693a00d096193f12e80888eb3a492931a0b823d`,
restore PASS (`~/workspace/preservation/snapshot3-v4-run.log`), and an
independent verifier re-hashed both the sealed set and its restore
scratch: 42,956 OK / 0 FAIL each (`verifier-set-verify.log`,
`verifier-restore-verify.log`; mtimes 23:51 UTC = 05:21 IST, after the
seal — the verification is temporally sound). The five registered
attempts (2353, 0014, 0018, 0115, 0137) all FAILED
(`studio/plan/WORK-REGISTRY.md` snapshot block). Everything below is
about what the programme claims versus what it can prove.

---

## G-1 · Snapshot #3's success is registered NOWHERE — by the spec's own law, it does not count

Spec §7: a snapshot is entered in the registry with status COUNTED only
after restore PASS; the registry "is the record"
(`studio/plan/SNAPSHOT-SPEC.md` §7 and its log section).
`grep -r 4c3cf644` across the wt-ci `studio/` tree, the #17 cell's
`studio/`, and `~/workspace/preservation/*.md` returns **zero hits**.
The registry's last word on #3 is the 0137 entry's conclusion
("Snapshot #3 cannot be completed from this exec session") and the
spec log's tail: "NOT COUNTED — snapshot not taken as of Oct 30
~02:00 IST". No comment on issue #17 records the v4 run either (the
comment thread ends at Fabel's seating). The programme's one success
is, on paper, a non-event; a reader of the record would re-attempt a
snapshot that already exists — or worse, "clean up" an unregistered
2.0 GB directory inside `builds/`.

## G-2 · The only copy of #3 lives inside the tree it preserves, on the same spindle as everything else — WORST CHARGE

Spec §2, verbatim: the two copies must live in different top-level
trees, "neither inside `clone-inspect/` (the tree being preserved
cannot preserve itself)". The sealed set sits at
`~/workspace/clone-inspect/builds/snapshots/snapshot-3/` (2.0 GB,
`du`). There is no Location A copy, no Location B copy.
`df` on `preservation/`, `your_files/`, and `clone-inspect/` returns
one filesystem: `/dev/mapper/rv` mounted on `/home/hatch` — A, B, and
#3 all die in the same box death. Worse: the Oct 30 cleanup deleted
roughly 24 GB *inside clone-inspect* (tree shrank ~30 GB → 5.9 GB per
the studio record) hours before #3 was placed inside it. #3 survived
the last deletion pass by timing, not by structure. The v4 runner's
docstring admits the S2 ceremony was "superseded… by the
commissioning order" — a finisher directive traded away the spec's
core structural rule, and nobody has priced what replaced it: nothing.

## G-3 · The 02:38 daily was refused by a gate that v4 disproved by running under it

Registry, 0238 entry: the daily failed closed after ~20 min of polls,
MemAvailable peak **1,527 MB** against the ~2,500 MB gate; "the copy
was NOT forced into a starved box". Three hours later the v4 run
**started at 1,272 MB and dipped to 1,006 MB** after the payload copy
(`snapshot3-v4-run.log`, 04:55:59 and 04:59:05 IST) — below the
daily's *peak* — and passed stage-verify, target-verify, and restore
with zero failures. The gate programme had already been "closed as
falsified" by the 0115/0137 seat ("MemAvailable polling is hereby
retired as this snapshot's gate: it certified nothing in four
attempts" — registry 0115 entry) *before* 02:38, and the daily seat
obeyed the corpse anyway (its own entry admits the tension and defers
to Ren). Was failing closed right? As obedience to a written order,
yes; as engineering, no — the falsification was already on file, and
the counterexample is now sealed with a PASS stamp. A gate that
refuses at 1,527 and is beaten at 1,006 is not a safety margin; it is
a superstition with a threshold.

## G-4 · The daily retry plan is theatre — there is no mechanism, only a sentence

The 0238 entry's standing remedy: "the daily runner stands staged…
a re-seat when the box cools runs it directly". Registry line 879 +
OPEN-LOOPS L38: the daily cron "is Ren's to create" — i.e., it does
not exist. No cron definition file under `~/workspace/cron.d/`
mentions a snapshot (grep, Oct 30). Result on disk: **zero `*_daily`
snapshots have ever existed** in either store (both store roots list
only `2026-10-29_1148…`, `2026-10-29_2104…`, `core-001`). Spec §6
owes one daily per day plus one per merge; batch 9 and the Truth
Car 3 merge both landed with no `merge-*` snapshot in either store.
A cadence whose retry depends on someone remembering a registry
sentence is not a cadence.

## G-5 · Every runner's verification is circular — a PASS proves the copy matches itself, never the source

In all three runners the manifest is built from the *staged copy*:
`build_manifest(STAGE)` (`run_snapshot3.py` ~line 176;
`run_snapshot3_v4.py`, `build_manifest(STAGE)`; `run_daily.py`
identical). Every subsequent verify compares a copy against hashes
of that same copy. If `shutil.copy2` sealed a torn, stale, or
half-written file, the manifest faithfully hashes the damage and
every verify passes forever. No runner ever hashes the *source* tree
and compares. The ceremony's strongest word — "verified" — means
"internally consistent", and no SNAPSHOT.md anywhere discloses that
distinction. (The Wave-2 report's Deep-thinker seat named this C-3;
it remains unfixed in v4.)

## G-6 · Thirteen files vanished inside snapshot #3's PASS, and none of them is named

`snapshot3-v4-run.log`, 04:59:04 IST: `ROOT cells live=36548
staged=36535`. The SNAPSHOT.md drift line repeats it. Thirteen files
that existed at census were gone (or moved) at copy — recorded as a
bare number, never as a list. Which cell, which branch, whose
uncommitted work? The set cannot say. Combined with G-5, the deeper
hole is unnamed *content* drift: cells were "LIVE and writing during
the copy" (v4 docstring), `copy_file` reads whatever bytes are on
disk mid-write, and the manifest then blesses the tear. Count drift
was disclosed; content drift is not even measurable by this design.

## G-7 · v4's write-health probe tests the one operation that was never broken

`run_snapshot3_v4.py`, `write_probe()`: writes 64 KB of
`os.urandom` per directory, re-hashes, deletes. But the 0137 probe
matrix (registry, 01:52 IST diagnosis) proved the night's signature
precisely: "pure WRITES of non-workspace data pass at any size…
READ-then-WRITE of workspace data is REFUSED". A fresh-random-bytes
probe is the exact operation already proven to pass while real
copies die. The probe that gates #3 certifies nothing about the
failure mode that killed five attempts. It passed, the run worked —
in a fresh runtime generation, per the Methods ruling (RAM/generation
decay) — but the gate is a rain dance that happened to precede rain.

## G-8 · v4 succeeded by ignoring its own commissioning spec, and nobody reconciled the two

Wave-2's B-3 (`studio/plan/ISSUE-17-WAVE2-REPORT.md`) priced the
correct attempt: memory floor 2,560 MB **in code** with mid-run
re-checks · source-completeness assertion · new name grammar ·
FAILED.md in `failures/` in both stores · independent restore-from-B
as a separate event. The delivered v4 has: **no memory gate at all**
(`mem_available_mb()` is called for logging at start/after-copy/
failure/end and never compared to any threshold) · one location, not
two · the name `snapshot-3`, not the grammar · restore-from-itself in
the same run, not from B as a separate event · no `failures/`
directory exists in either store to this hour. v4 is good work
(G-credit below) — but the record now holds two incompatible
definitions of "snapshot #3 done", and the one that was met is the
one nobody approved as the standard.

## G-9 · The staged remedy is a loaded gun, and the registry still points at it — LIVE HAZARD

The 0137 registry entry's REMEDY, still the standing instruction:
"run the staged runner from a FRESH exec context —
`python3 ~/workspace/preservation/run_snapshot3.py`". That script,
read line by line today:

- §1a sources `design-refs/swarm/` — **deleted Oct 30 ~03:00**.
  `os.walk` on a missing root yields nothing and raises nothing:
  swarm would stage **0 files, silently**, while the hard-coded
  SNAPSHOT.md text still describes "studio tree live at copy time
  (mail and wall as of the copy minute)".
- §1c's lane rule (`builds/<lane>/Focus-app`) **misses
  `builds/cells/` entirely** — all sixteen cell trees, the newest
  work on the box.
- Its SNAPSHOT.md source-state is hard-coded false for today:
  "trunk at batch-8 truth-merge state (DB v11)… Truth Car 3 lanes
  landed UNCOMPILED… pre-truth3-merge" — the tree is DB v12,
  post-batch-9, post-GitHub-studio.
- `main()` begins with `shutil.rmtree(stage_parent)` on
  `.staging-snapshot-3/` — which holds attempt-0137's sole physical
  remnant, the 0-byte `swarm/MAXIMPL.md`. The next launch of the
  staged remedy destroys the only evidence of the failure it is
  meant to remedy. (v4 preserved the remnant; v3 still deletes it.)
- On restore-test FAIL it stamps the FAIL into both locations,
  deletes the scratch (`shutil.rmtree(SCRATCH, ignore_errors=True)`
  runs unconditionally), prints a normal SUMMARY, and **exits 0** —
  there is no fail-stop after the restore test.

`run_daily.py` carries every one of these defects (it is a v3
derivative and says so). If anyone obeys the registry tomorrow, the
programme mints exactly the verified lie Konto's cell refused to
mint — and files it as COUNTED.

## G-10 · The daily gate exists only in prose; the code would run at 351 MB

`run_daily.py` `main()`: the only memory check is
`if mem_available_mb() < 350: fail_stop(...)`. Its own SNAPSHOT.md
template asserts the run was "memory-gated per Anker's letter
(single check >= ~2,500 MB available)". The 02:38 refusal was a
*seat* obeying a *letter*; the program enforces neither 2,500 nor
2,560. Governance that lives in a letter dies with the letter's
reader — the next seat to run the file directly inherits a 350 MB
floor and a metadata template that swears it was 2,500.

## G-11 · Snapshot #3's purpose expired before it succeeded

Commission (registry 2353 entry; Sarthak's order Oct 29 23:16 IST):
"the pre-merge preservation point for Truth Car 3". All five
attempts failed; v4 sealed Oct 30 04:55 with its own SNAPSHOT.md
recording "trunk at batch-9 state (DB v12)". The pre-merge state #3
exists to preserve was never preserved — it advanced through the
merge while the attempts died. v4's SNAPSHOT.md is honest about
what it sealed; the issue's framing ("snapshot #3", the pre-merge
point) is not. The ordinal now commemorates a moment that was
missed, and any future restorer who trusts the name over the
SNAPSHOT.md will reconstruct the wrong past.

## G-12 · Snapshot #2's restore PASS is unprovable — no run log survives

`~/workspace/preservation/` holds `snapshot1-run.log`,
`snapshot3-run.log`, `snapshot3-v4-run.log`, and the verifier logs.
There is **no run log for the 2104 set**. Its COUNTED status rests
on its SNAPSHOT.md line and registry prose — self-attestation, the
evidence class this programme accepts nowhere else. (Its integrity
was re-hashed by the Wave-1 cell per the Wave-2 report — integrity
PROVEN, restore event UNPROVABLE; the distinction matters because
§5 counts the *event*, not the bytes.) #1, by contrast, has its log
and a 60-second seal-to-PASS window (taken 11:48, PASS stamped
11:49) that nobody has ever explained for a 569 MB × 4-pass
ceremony — plausible on this disk, unexplained in the record.

## G-13 · The APK archive quietly collapsed from 23 to 2

Snapshot #2 sealed **23 APKs** including `monolith-final-debug.apk`
(the accepted build at spec time; spec §1e/§8 give APKs of record
special retention). The Oct 30 cleanup cut the live clone-inspect
root to exactly 2 APKs (`monolith-batch8-truth-fixed.apk`,
`monolith-batch9.apk`), and #3 seals those same 2. Batches 1–7 and
final-debug now exist **only** inside snapshots #1/#2. No
post-cleanup snapshot re-secures the full set, and §1e defines the
APK component off a root that no longer holds them — the spec's
source set rotted underneath it and nobody amended the spec.

## G-14 · core-001 squats on the shelf, indistinguishable from real sets

`core-001/` sits in **both** store roots: 17 files, **no
MANIFEST.sha256 at all**, self-declared "PARTIAL… does not satisfy
the spec's COUNTED bar". Spec §1: a set missing a component "is
INCOMPLETE and does not count". Neither store root has an INDEX.md;
nothing at shelf level distinguishes the partial core from the two
COUNTED sets beside it. At 3 AM, mid-disaster, the shelf lies by
omission. (Feel-5 and the Wave-2 Designer seat raised the missing
INDEX; it is still missing.)

## G-15 · Snapshot #3's "studio record" is an unrefreshed third-party checkout

v4 copies `studio/` from `builds/wt-ci/studio` — the CI lane's
checkout (HEAD `f19c2d1`, "scouts: first sweep"). The runner
contains **no git operation** — no fetch, no pull, no rev-parse —
and SNAPSHOT.md records no commit. Sixteen cells push from their
own clones; whatever wt-ci happened to hold at 04:56 IST is sealed
as "the studio record", its freshness unproven and, from inside
the set, unprovable. A cache was snapshotted and labelled a source.

## G-16 · CORRECTION: the "#1 truncated manifest" is folklore — #1 is proven, and the myth is the charge

I checked all five manifests on disk (both locations of #1 and #2,
plus #3): **every line is a well-formed 64-hex hash** (awk length
check, 0 bad lines), line counts match their SNAPSHOT.md claims
exactly (3,325 / 5,999 / 42,956), A and B manifests of #1 are
byte-identical (sha256 `11ada2f4…e05fcf`, matching the registry),
and a `comm` diff of manifest paths vs files on disk is clean in
both directions. The Wave-2 report reached the same verdict
(Finisher-5, Student). Whoever briefed "snapshot #1's manifest is
truncated mid-line" repeated a defect that does not exist. The
programme audits bytes rigorously and its own briefs not at all —
the false charge travelled brief-to-brief while G-1 through G-15
sat unfiled. Ordinals are commentary; print triples or shut up.

---

## Credit where due (not charges)

- v4 fixed real defects: no startup rmtree, fail-fast with phase
  names, non-zero exits (2/3/4/5), fsync on manifest and stamp,
  target-exists refusal, drift printed in SNAPSHOT.md instead of
  smoothed over, remnant preserved as evidence.
- The independent verifier (`verifier_verify.py`, separate seat)
  re-hashed set + restore scratch with a parallel implementation —
  the one genuinely non-circular check in the whole programme,
  because it at least re-reads the sealed bytes independently.
- The 02:38 seat obeyed its written orders and filed the failure
  the same night with full readings. The failure is the gate's
  falsified basis and the missing retry mechanism (G-3, G-4), not
  the seat's conduct.
- Snapshot #1 is proven (G-16). Snapshot #2's bytes are proven;
  only its restore *event* is unprovable (G-12).

## The one-line version

The programme can copy, hash, and restore — what it cannot do is
prove a snapshot matches its source (G-5), keep its only #3 out of
the blast radius (G-2), remember that #3 happened (G-1), or stop
pointing at a remedy that now manufactures lies (G-9).

— Grim (brutal critic, #17)
