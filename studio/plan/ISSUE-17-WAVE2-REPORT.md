# ISSUE #17 — WAVE-2 REPORT · Preservation — snapshot #3

**Lead:** Konto (cluster H, flat under Ren). **Verdict: SNAPSHOT #3
NOT ATTEMPTED — stood down on a filed blocker (below). No thrash:
one health test, one audit of the runner, no run.**

## The blocker — exact, three-part (lead-verified, Oct 30 ~04:00 IST)

**B-1 · Write health: PROBES PASS, MEMORY GATE FAILS.**
Sustained-write test in `~/workspace/preservation/`: 60/60 small
writes with fsync + sha256 re-read OK over 13.4 s; one 20 MB
write + re-hash OK in 0.6 s. Writes are demonstrably healthy at
probe scale in this generation. BUT MemAvailable measured
648 MB → ~883 MB across the session — far under the cell's own
gate of ≥2,500–2,560 MB sustained (the 01:37 attempt died
repeatedly with MemAvailable 2,251→1,363 MB; the script's 350 MB
floor is, in the Feel-1 seat's word, theatre). The gate that
matters is the memory gate, and it fails.

**B-2 · The staged runner is STALE — it would mint a lying
snapshot.** `~/workspace/preservation/run_snapshot3.py` audited
line-by-line:
- §1a copies `design-refs/swarm/` — DELETED Oct 30 (~03:00, his
  cleanup order; records migrated to the repo, commit 8438f5a).
  `os.walk` on a missing root yields nothing and raises nothing:
  the snapshot would silently carry **0 studio files** while its
  SNAPSHOT.md still describes the live wall/mail tree.
- §1c lane detection (`builds/<lane>/Focus-app`) still finds the
  21 kept lane trees — but the payload MISSES `builds/cells/`
  entirely: all sixteen Wave-1 cell branches, i.e. the newest
  unmerged work, including five unpushed / two uncommitted trees.
- SNAPSHOT.md source-state text is hard-coded false: "batch-8
  truth-merge state (DB v11)… Truth Car 3 lanes landed
  UNCOMPILED… pre-truth3-merge" trigger. Today is DB v12,
  post-batch-9, post-GitHub-studio. The name it would mint
  (`…_manual-pre-truth3-merge`) lies about the moment it seals.
- Its two restore spot-checks point at swarm files that would not
  be in the set (false FAIL), and per the Finisher-4 seat the
  runner stamps that FAIL into both copies and still exits 0.
A snapshot whose manifest verifies a payload its own text
misdescribes fails TELL-THE-TRUTH even if every hash matches.
Running it as-is was refused on this ground alone.

**B-3 · What a correct attempt needs (the v4 runner, priced).**
Source-completeness assertion (live per-root counts asserted
BEFORE manifest, no root silently zero) · payload = repo `studio/`
(431 files, via D-1) + `builds/cells/` sans `.git` (~390 MB, D-2)
+ trunk + kept lanes + design-refs flat files (D-6) · new name
grammar `snap-003__<date>_<HHMM>IST__manual__db12-post-batch9-github-studio__<verdict>`
minted pre-run · memory floor 2,560 MB in CODE with mid-run
re-checks · FAIL = non-zero exit + `FAILED.md` in `failures/` in
both stores + debris kept · independent restore-from-B as a
separate event, verdict as the (name, manifest sha256, verdict)
triple in four append-only places · fresh-generation window only
(restart → gates → probe both destinations: 100 small + 100 MB,
re-read, zero EPERM → run). Price (seats' estimates): ~1 hour
runner amendments + one healthy window for the run itself.
**Interim truth:** until v4 runs, #1/#2 remain the only snapshots,
BOTH on the same spindle (`/dev/mapper/rv`) and now sole copies of
the deleted mail/wall — the off-box leg (D-8: release-asset
interim same-day; restic→R2+B2 as the durable form, seats'
measured 492 MB unique for #1+#2+#3, ₹0/mo at today's size) is
the real preservation gap, not the missing #3 ordinal.

## Finishers ×5

**Finisher-1 (surface)**
Do NOT run `run_snapshot3.py` as-is: its surface lies — a missing swarm root stages as 0 files without raising, so a "complete" #3 would silently omit the studio record. Attempt only after D-1/D-2 repointing to `studio/` (431 files) + `builds/cells/` sans `.git`, plus a live per-root count assertion before the manifest is built. Proof counts: staged→A→B sha256 census closing exactly on counts/bytes, no root at zero unless censused zero.
— Finisher-1 (surface)

**Finisher-2 (motion)**
The motion never converged on the wall night: the 0137 log is not "5 failures" but 12 STAGE-ATTEMPT deaths, MemAvailable bleeding 2251→1363 MB across 25s backoffs. Resumable staging is the right remedy only if each pass prints skipped/copied counts and actually shrinks the remainder — convergence visible, not asserted. Attempt only in motion from a fresh generation, quiescent lanes, no Gradle in flight. Proof: attempt/pass vocabulary separated in the log, `attempts=` + `elapsed=` in SUMMARY.
— Finisher-2 (motion)

**Finisher-3 (light)**
A PASS that cannot be re-lit later is not proof — #2 is exactly that: integrity PROVEN (5,999/5,999 re-hash) but its restore PASS is UNPROVABLE, no run log survives. #3 must be attempted only with the gate readings written to disk *first*, and an independent restore-from-B as a second, separate event from the runner's self-test. Proof counts: the verdict as a triple — (full name, manifest sha256, verdict) — identical in test log, both SNAPSHOT.md copies, and registry; any name/hash mismatch voids it on its face.
— Finisher-3 (light)

**Finisher-4 (sound/haptics)**
As staged, the runner is mute when it matters most: it would fail its two hardcoded swarm spot-checks, stamp `RESTORE-TEST FAIL` into both copies, delete its own evidence, print a normal SUMMARY, and exit 0. That is a silent false-FAIL enshrined forever. Amend A12/A15/A16 first — spot-checks repointed to files in this set, atomic verified stamp, FAIL exits non-zero and keeps debris. Proof counts only when success and failure *sound* different: exit code, stamp, and log all agreeing.
— Finisher-4 (sound/haptics)

**Finisher-5 (typography/print)**
Do not print the staged name: `2026-10-30_0137_manual-pre-truth3-merge` lies about the moment — this is DB12, post-batch9, post-GitHub-studio. Mint under the new grammar `snap-003__<date>_<HHMM>IST__manual__db12-post-batch9-github-studio__<verdict-at-sealing>` before the run, never rename after. Ordinals are commentary, never identifiers — #1's "truncated manifest" myth (disproven: 3,325 lines, hash matches) came from names swapping in print. Proof: crowned numbers only — files, manifest-sum bytes, manifest sha256, printed the same everywhere.
— Finisher-5 (typography/print)

## Feel specialists ×5

**Feel-1 (weight)**
The run feels heavy before it fails: deaths began at 2,251 MB, so the script's 350 MB floor is theatre. Gate at ≥2,500–2,560 MB *sustained*, `pgrep gradle` clean — the 02:38 daily died with 2 daemons resident, one a 1.5 GB RSS build, polling 505–1,527 MB for 20 minutes. Attempt only when the box feels light and stays light across the polling shape that refused before. Proof: the MemAvailable series in the gate record, not a single opening reading.
— Feel-1 (weight)

**Feel-2 (light)**
Failure today lives in prose and dies there — no FAILED artifact exists anywhere, and "debris cleaned" was false on disk (a 0-byte remnant still sits in `.staging-001/`). Every attempt, gate refusals included, must leave a ceremony-generated `FAILED.md` (name, minute, phase, measured cause, debris state) in `failures/` in *both* homes, and the debris line written only after `find` proves it. Proof counts: a stranger at dawn can see what failed, why, and what remains — without asking anyone.
— Feel-2 (light)

**Feel-3 (timing)**
Timing is the cure and the trap: fresh runtime generations write clean and decay in ~1 hour, so yesterday's probe pass proves nothing today. Attempt #3 in the minutes after a restart, gates in order — memory, daemons, fresh write-probe in *both* destinations (100 small + one 100 MB file, re-read, zero EPERM) — and if the window closes mid-run, stop; do not chase a decaying generation through 12 retries again. Proof: timestamps in IST on every log line, gate time to seal time inside the healthy window.
— Feel-3 (timing)

**Feel-4 (touch+sound)**
EPERM is the wall you feel, not a number you read: the decisive gate is tactile — actually writing and re-reading in A and B before committing 341–541 MB of payload. The long middle must not go dead-air either: per-section start lines, every-250-files counters in copy, every-500 in the five hash passes, wake times after backoffs. Proof counts: you can hear the run breathing the whole way, and a stall is audible within one counter interval — never discovered at FAIL-STOP.
— Feel-4 (touch+sound)

**Feel-5 (instant-read)**
Right now neither store root has an INDEX.md, and A+B sit on the same spindle (`/dev/mapper/rv`, one VM, one account) — the shelf looks calm while box death takes both, and #1/#2 are now sole copies of the deleted mail/wall. Attempt #3, and the same day ship the off-box leg (D-8 interim release asset; backfill #1/#2 in the same sitting) — do not wait. Proof at a glance: INDEX.md in both roots with triples + twin note, and Sarthak gets exactly one line — full name, manifest sha256, files, bytes, verdict.
— Feel-5 (instant-read)

## The 20 named types

**Crazy-ideas:** Snapshot is now a time-capsule problem, not a copy problem — seal #3 as a signed, content-addressed restic repo to R2+B2 (492 MB unique for #1+#2+#3 measured, ₹0/mo) and ship the manifest hash in GitHub release notes; a dead box then resurrects from hash alone. — *Crazy-ideas*
**Maximum-implementation-potential:** The staged `run_snapshot3.py` maximises false confidence: missing `design-refs/swarm` stages as 0 files via `os.walk`, passes `fail=0`, then fails its own hardcoded swarm spot-checks and exits 0. Do not patch around it — implement A3/A4/A12 + source-completeness assertion (live per-root counts asserted pre-manifest) as one rewrite. — *Maximum-implementation-potential*
**Brutal critic:** A/B is a lie — both copies on `/dev/mapper/rv`, one VM, one spindle. #1/#2 are now sole copies of the deleted mail/wall after Oct 30 cleanup, and #2's restore PASS is unprovable (no log survives). Calling this preservation without D-8 off-box backfill today is theatre. — *Brutal critic*
**Improver:** Fix the refusal invisibility first: gate record written before the gate, log opened before MemAvailable check, `FAILED.md` in `failures/` in both stores on every refusal/attempt. Spec: amend `run_daily.py` (350 MB code vs ~2,500 MB text) and the runner per §7 — price: ~1 hour, zero bytes copied. — *Improver*
**Designer:** The shelf lies by calm: no `INDEX.md` in either store root, `core-001` (16 files, no manifest) sits unruled beside real sets, daily refusal `2026-10-30_0238_daily` exists only as prose. D-10 storefront — sets listed by full-name triple (name, manifest sha256, verdict) — is preservation UX, not decoration. — *Designer*
**Consumer:** If I need to restore at 3 AM, I get a name that lies: staged `_manual-pre-truth3-merge` describes batch-8/DB v11 for a DB12 post-batch9 moment. Adopt D-7 grammar `snap-003__<date>_<HHMM>IST__manual__db12-post-batch9-github-studio__<verdict>` pre-run; renaming after sealing is forbidden for a reason. — *Consumer*
**Student:** Lesson from #1: the artifact was fine (3,325/3,325 re-hash OK, manifest `11ada2f4…e05fcf`), the records about it were wrong — "truncated manifest" copied brief-to-brief. Teach the rule: verdicts travel as triples, ordinals (#1/#2) are commentary, never identifiers. — *Student*
**User:** Since Oct 29 21:45 everything that matters is unpreserved — batch 9, merge tree + 5 uncommitted test fixes, cell findings unpushed. Payload must include today's reality: trunk `Focus-app`, `builds/cells/` without `.git` (~390 MB, D-2), repo `studio/` (431 files/12 MB, D-1) — not the deleted swarm tree. — *User*
**User-experience:** A 541 MB / 5,914-file copy with no counters is dead air that invites killing it mid-run. Feel pack (§6) is print-only, integrity-neutral: per-section start lines, every-250-files counters, IST timestamp every line, SUMMARY gains `attempts=` + `elapsed=` — convergence visible, not asserted. — *User-experience*
**Purpose-questioning:** Why snapshot lanes at all if they're on GitHub? Answer fails today: D-4 shows 193.9 MB (39%) is lane `.git`, but dirty/unpushed lanes exist — prune only after per-lane unpushed/stash census, exceptions named in SNAPSHOT.md. `wt-ci` is the opposite: pure checkout, exclude tree, keep only its `studio/` via D-1. — *Purpose-questioning*
**Thoughtful:** Attempt 0137's sole physical evidence is a 0-byte `MAXIMPL.md` in staging — and the runner's startup `rmtree` (A1) would silently destroy it on next launch. Preservation that erases its own failure history cannot be trusted about success; archive debris before any wipe. — *Thoughtful*
**Deep-thinker:** Circular verification is the core flaw (C-3): a snapshot missing whole roots still verifies against its own manifest. Independent restore (§5 step 5) breaks the circle — inputs frozen from run-log + registry (never the set's own SNAPSHOT.md), restore from B, full verify, APK byte-compare to live shipped file. — *Deep-thinker*
**Enthusiastic designer:** Make the PASS unmissable and the FAIL unhideable! Stamp triple in four append-only places — test log, both SNAPSHOT.md byte-identical (`restore-test:` / `restore-test-2:`), registry under full name — and a PASS whose name/hash mismatches its header is void on its face. — *Enthusiastic designer*
**Wow-level designer:** Interim wow is speed: each set as one zstd volume on a private archive repo release — ~1.71 GiB total compressed, under 2 GiB asset limit, $0, shipped same day as #3 including #1/#2 backfill. No waiting for perfect restic setup while sole copies sit on one disk. — *Wow-level designer*
**Designer-29-years-experience:** I've seen this failure for decades: exit 0 on FAIL, evidence deleted on FAIL (A16), stamp written non-atomically without fsync (C-8). Fix the grammar, not the script cosmetics: FAIL exits non-zero, FAIL keeps scratch, manifest+stamp fsync'd, per-attempt errors name file/path/phase. — *Designer-29-years-experience*
**Intentionality-focused designer:** Every prune must state intent: D-5 drops `merge-batch9/guh-test/` probe fixtures — side-effect is zero symlinks in payload, which simplifies restore census (symlinks otherwise scored once by target-string hash in §5b reconciliation). If a cut isn't named with its consequence, it doesn't ship. — *Intentionality-focused designer*
**Synchronous collaboration:** The daily line contradicts itself in three places at once — cron body points into deleted swarm tree and never mentions `run_daily.py`, code gate 350 MB vs text ~2,500 MB, spec claims a lockfile grep proves doesn't exist (C-5). D-9: one runner, wired, or retire the line honestly; no third state. — *Synchronous collaboration*
**Implementation-quality:** Gate quality is the run: 7 gates in order (§3), decisive one is #3 — write probe (100 small + 100 MB, re-read, both destinations, zero EPERM) valid only in its window. Right now is not a window: MemAvailable 1.3–1.7 GB falling, Gradle daemon resident, load ~10. Don't run. — *Implementation-quality* *(Lead note: Konto's own probe measured 648–883 MB available — worse than this seat's reading; the conclusion is the same and stronger.)*
**Technical execution specialist:** Numbers crowned by FINISHER-5: as-is 5,914 files/~541 MB, with D-4 4,710 files/~341 MB; disk 123 GB free (~57× worst peak) was never binding — RAM was (deaths logged at 2,251 MB). Set floor 2,560 MB in code (A2) with mid-run re-checks, verify A before copying B (A13). — *Technical execution specialist* *(Lead note: free disk re-measured tonight: 115 GB — still not binding.)*
**Interaction-detail specialist:** The smallest interaction is the report to Sarthak: one line, triple form only — full name, manifest sha256, files, bytes, verdict (§5 step 9). No tours, no logs in chat. And D-6 detail matters: capture the 33 design-refs flat files (3.1 MB) beside the root — two are studio work product the tree-walk would miss. — *Interaction-detail specialist*

## Lead verdict — Konto

Stood down, deliberately, and the stand-down is the deliverable:
the staged remedy's payload and metadata are stale in ways that
would have made snapshot #3 a verified lie, and the memory gate
fails the cell's own standard even though small writes pass.
Snapshot #3 remains UNTAKEN. Snapshots #1/#2 states are unchanged
(#1 PROVEN; #2 integrity PROVEN, restore event UNPROVABLE — the
cell's wave-1 findings stand).

**DECISION-FOR-SARTHAK (from this issue):** the off-box leg needs
accounts only he can open (Cloudflare R2 / Backblaze B2) if Ren
adopts the restic form; the interim release-asset form stays
inside his own GitHub and needs no decision. No other #17 item is
his to rule — the v4 runner and the healthy-window run are Ren's
to schedule.

**Next wants (Konto):** (a) write the v4 runner (~1 h, spec in
B-3) and run it in the first fresh-generation window with
MemAvailable ≥2,560 MB sustained; (b) ship the D-8 interim
off-box asset for #1/#2 the same sitting — sole copies on one
spindle is the actual emergency; (c) INDEX.md + failures/ grammar
in both stores so the next refusal is visible without archaeology.

## Issue-comment text (posted via connector if it goes through; this text is the fallback record)

> Wave 2 — Konto (cluster H). Snapshot #3 NOT attempted; stand-down filed with exact blocker in studio/plan/ISSUE-17-WAVE2-REPORT.md: (1) write probes pass (60/60 + 20 MB verified) but MemAvailable ~0.65–0.88 GB fails the ≥2.5 GB memory gate; (2) the staged run_snapshot3.py is stale — it would silently stage 0 files from the deleted design-refs/swarm, miss builds/cells/ entirely, and seal hard-coded batch-8/DB-v11 text over a DB-v12 tree; (3) v4-runner spec + price filed in the report (source-completeness assertion, cells+studio payload, new name grammar, non-zero FAIL + FAILED.md). #1/#2 unchanged. DECLARED, uncompiled. — Konto

— Konto, cluster-H lead, issue #17
