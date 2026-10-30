# CRITIC — Issue #17 / ANKER cell: charges against the snapshot design, its claims, and this cell's plan

Critic seat, ANKER cell. Scope unrestricted per brief. Every charge carries evidence
(a file, a count, or a command output) gathered read-only on Fri 2026-10-30, ~03:05–03:35 IST.
Nothing was modified; nothing was copied. Where the record turned out to be *better*
than the brief claimed, that is filed too (C-13) — a critic who only convicts is a fan.

Ground truth used: `~/workspace/preservation/` (runners, logs, snapshots),
SNAPSHOT-SPEC.md + WORK-REGISTRY.md as preserved inside snapshot #2
(`swarm/plan/`), `cron.list` / `cron.view` / `cron.status` for `monolith-daily-snapshot`,
and this cell's `findings/planner.md` (filed ~03:10 IST, read before writing).

---

## C-1 — CRITICAL — "Two physically separate locations" is one disk, one subvolume, one VM

The spec (SNAPSHOT-SPEC §2) sells A/B as "two physically separate locations" and the
whole preservation claim rests on it. Reality, measured today:

- `df -h ~/workspace`: both homes on `/dev/mapper/rv`, 150G, mounted at `/home/hatch`.
- `os.stat` on snapshot #1's MANIFEST in A and in B: `st_dev=88` for **both**.
- A = `~/workspace/preservation/monolith-snapshots/`, B = `~/workspace/your_files/monolith-archive/snapshots/`
  — different directory *trees*, same btrfs subvolume (`subvol=/home/hatch`), same VM, same account.

A/B separation protects against exactly one failure: a single directory tree being
deleted while siblings survive (the Oct 4 / Oct 10 pattern the spec cites). It protects
against **nothing** else on the brief's list: disk failure, VM loss, account loss, a
workspace-wide cleanup, or a bad `rmtree` rooted at `~/workspace` all take the original,
A, B, the staging dirs and the restore-test dir in the same event. There is no off-box
leg anywhere in the snapshot system: no remote copy, no export, no archive outside
`/home/hatch` was found (`find` over `~/workspace` returns only the two homes above).
The planner (D7) concedes "same disk" and then schedules #3 into the same two homes
anyway, renaming the flaw ("a working-state preservation point") instead of fixing it.

## C-2 — CRITICAL — The snapshots are now the *only* copy of the studio's mind, and they are on the box they protect

Snapshot #2's SNAPSHOT.md and the spec §1a treat `swarm/` (plan, mail, COMMONS wall)
as one component among five. Since the Oct 30 ~03:00 cleanup, the live original is gone:

- `ls ~/workspace/clone-inspect/design-refs/swarm` → *No such file or directory*.
- MEMORY (Oct 30 cleanup entry): the Commons/Mailroom were **deleted wholesale**;
  only `plan/` + `methods/` (431 files) were migrated to GitHub (commit 8438f5a)
  before deletion. The mail (115 inboxes) and COMMONS.md were not migrated.

So snapshots #1/#2 are the sole surviving copies of the mail archive and the wall —
and per C-1 they exist only on this one disk. The preservation system's most
irreplaceable payload is protected by the weakest version of its own redundancy.

## C-3 — CRITICAL — Verification is circular: the runner can PASS a snapshot of nothing

`run_snapshot3.py` (and `run_daily.py`, identical structure) builds the manifest
**from the staged tree** (`build_manifest(STAGE)`), then verifies staged/A/B/restore
against that manifest. Nothing ever compares the staged tree against the *source*.
Consequences, all live today:

- `stage_payload` 1a copies `SRC/design-refs/swarm` — deleted (C-2). `os.walk` on a
  missing directory yields nothing and raises nothing: swarm stages as **0 files,
  no error**. (Planner D1 caught this instance.)
- The general hole is bigger than D1: `copy_tree` uses `os.walk` with no `onerror`
  handler, so unreadable/vanished directories are silently skipped mid-copy as well.
  `counts` is printed but **never asserted** against any expectation anywhere in
  either runner. A snapshot missing an entire lane, the records set, or the swarm
  still produces `STAGE-VERIFY ok=<n> fail=0`, `RESTORE-TEST PASS`, status COUNTED.
- Spec §1 says "a snapshot missing any component is INCOMPLETE and does not count."
  No code path in either runner can detect a missing component. The spec's central
  integrity rule is unenforced by the only tooling that exists.

## C-4 — HIGH — "Restore PASS" proves bytes, not resurrection: a restored tree cannot be built

The S5 restore test (spec and both runners) is: copy B to scratch, `sha256` every
file, open 3 files. It never attempts to use the restored tree. Measured gaps:

- Snapshot #2's `trunk/gradle/wrapper/` contains **only `gradle-wrapper.properties`** —
  no `gradle-wrapper.jar`, no `gradlew` (the live trunk has neither either; local
  builds used a standalone Gradle). A restored trunk cannot bootstrap a build.
- `local.properties` (SDK path) is excluded by design; the Android SDK
  (`~/workspace/android-sdk/`) is outside snapshot scope entirely.
- The toolchain that makes the source buildable — `~/workspace/clone-inspect/tools/`,
  **3.3G** (JDK 21, gradle-9.3.1, SDK zips) — is excluded by spec §1d as
  "re-downloadable". On a dead box with no network guarantee, "re-downloadable"
  is an assumption, not a restore.
- Neither the spec, the runners, nor the planner's §8 "independent proof" ever
  compiles, installs, or even `git`-checks a restored tree. Every "restore PASS"
  on record (#1: 3,325 files; #2: 5,999 files) certifies checksums only.
  The planner's §8 repeats the same hash-and-spot-open ceremony and calls it
  "independent proof" — it is independent of the *runner*, not of the *method*.

## C-5 — HIGH — The daily half-policy is already broken: first cron run FAILED, its instructions point at a deleted file

Two overlapping half-policies (brief's suspicion) is confirmed, and worse than stated:

- `cron.list`: `monolith-daily-snapshot`, enabled, daily 02:38 IST.
  `cron.status`: **last run Fri 2026-10-30 02:59 IST — `last_status: failed`,
  "Failed without a recorded reason", `consecutive_failures: 1`, `last_success_at: null`.**
  No `*_daily` directory exists in either snapshot home (`ls` + grep: no match).
  The daily policy has never once produced a snapshot.
- The cron body (step: "read the cadence spec first") directs the agent to
  *Anker's letter at `~/workspace/clone-inspect/design-refs/swarm/mail/ren/2148__from-anker__*.md`*
  — inside the swarm tree deleted Oct 30 ~03:00 (C-2). The standing instruction's
  first reference is a dangling pointer.
- The cron body contradicts the spec it cites: step 6 restores "the spec's test
  subset", but spec §5 mandates copying the **whole** snapshot from B and verifying
  100% of it. Step 3 says "use the spec's lockfile" — `grep -i lock` over
  SNAPSHOT-SPEC.md and over both runners returns **nothing** (rc=1): there is no
  lockfile in the spec and no locking in any runner. Two concurrent runs (daily
  02:38 + a manual) would `rmtree` each other's staging/destinations unimpeded.
- A deterministic daily runner exists — `~/workspace/preservation/run_daily.py`
  (written Oct 29 21:15) — and the cron body **never mentions it**. The cron instead
  re-derives the ceremony from prose every night. Script and schedule are two
  separate daily policies, neither wired to the other.
- The planner's plan for #3 never mentions the cron at all. Even if Ren hand-runs
  #3 perfectly, the nightly job keeps failing against a deleted letter.


## C-6 — HIGH — Cadence on paper vs cadence in fact: the merge-snapshot rule has never once been obeyed

Spec §6: a snapshot is owed **on every merge**, plus daily, plus on demand. On disk:

- `ls` of both homes: exactly two counted snapshots, both `manual-*`
  (1148_manual-first, 2104_manual-post-batch7-pre-art-merge), plus PARTIAL `core-001`.
  **Zero `merge-*` snapshots exist**, despite the batch 5–9 merges, the Truth Car
  merges, and the art merges that fill BUILD-CHANGES.md for Oct 29.
- Snapshot #2's own trigger text admits the default: it is "the two owed snapshots
  discharged as one current-state snapshot" — the post-slide snapshot and the
  Oct 29 daily, folded into a manual run. Owed snapshots being "discharged" in bulk
  by a differently-named one is the registry forgiving the cadence retroactively.
- The naming is also unreliable as a timeline: #2 is *named* `2104` but its
  SNAPSHOT.md says "taken: 2026-10-29 21:45 IST" — names sort by attempt start,
  not by the state they preserve.

## C-7 — HIGH — Everything since Oct 29 21:45 is unpreserved, including work the plan calls "on GitHub"

The newest counted snapshot predates: batch 9's delivery, the CI fix chain, the
GitHub migration, the swarm deletion, and all 16 issue cells. Specifics the
staged runner + planner between them still leave exposed:

- `git -C ~/workspace/clone-inspect/builds/merge-batch9/Focus-app status --porcelain`
  → **5 modified files** (the issue-#2 test call-site fixes, uncommitted). They ride
  the merge-batch9 lane copy *if* #3 runs — the one piece of luck in this design.
- The cells: `git status --porcelain` across `builds/cells/issue-*` shows
  uncommitted/untracked residue in **10 of 17** cell trees (issue-2: 5, issue-14: 6,
  issue-6: 3, issue-4: 2, and 1 each in issue-3, 8, 9, 11, 12, 17 — including this
  cell's own `findings/`, untracked). Untracked files are not on GitHub by
  definition. The planner's D4 offers "exclude cells: preserved as GitHub branches"
  as a respectable option without quantifying this residue — the exclusion rationale
  is false for exactly the files (findings, plans) that exist nowhere else.
- Root records moved on: live `BUILD-CHANGES.md` is **82,190 B** vs **79,694 B** in
  snapshot #2 (and 56,081 B in core-001). The ledger of what was built exists in its
  current state in exactly one place: the live tree.

## C-8 — HIGH — run_snapshot3.py's "resumable" claim was never demonstrated; its failure log is diagnostically blind

- The docstring promises staging "RESUMABLE in-run… rides the flicker". The evidence
  of the 01:37 attempt (`snapshot3-run.log`): 12 attempts, 12 ×
  `PermissionError: [Errno 1] Operation not permitted`, FAIL-STOP. The staging
  leftover on disk today — `find .staging-snapshot-3` — contains exactly
  **1 payload file** (`swarm/MAXIMPL.md`). Twelve "resumed" passes never got past
  the first file of the first component; convergence was asserted, never observed.
- All 12 log lines are identical and carry **no filename, no path, no phase** —
  after five failed attempts that night, the record cannot say *which file* kills
  the copy. A preservation runner whose failures are anonymous cannot be debugged
  from its own logs.
- Cross-run resume is impossible by construction: `main()` opens with
  `shutil.rmtree(stage_parent)` — the *entire* `.staging-snapshot-3/` parent —
  before staging (planner §1.3/D9 notes this). And `NAME` is minute-resolution
  clock time, so a later run is a different name under the same wiped parent.
- Same destructive edge at publish: if a destination `monolith-snapshots/<NAME>`
  exists it is `rmtree`d *before* the replacement copy is attempted; if all 12
  write attempts then fail, the run leaves **nothing** at that name — the runner
  can convert an existing counted snapshot into a registered failure.
- No `fsync` appears anywhere in either runner; on a box whose write layer was
  the failure mode of the night, "copied" is asserted at `close()`, never at disk.

## C-9 — MEDIUM — The memory gate is theatre: three different floors, none of them the binding one

- `run_snapshot3.py` / `run_daily.py`: abort only if `MemAvailable < 350 MB`,
  checked **once**, at start.
- `snapshot3-run.log`: the run that died 12 times started at **2,251 MB** and fell
  to 1,363 MB while retrying — the 350 MB floor guarded nothing.
- WORK-REGISTRY (in #2) records the 2104 replacement as "window-gated on the
  studio's own criterion (available ≥ 3,000 MB sustained)"; the cron body says
  ~2,500 MB; `run_daily.py`'s own SNAPSHOT.md template claims the copy was
  "memory-gated per Anker's letter (single check >= ~2,500 MB)" while its code
  checks 350 MB once. Four documents, three numbers, one template that misstates
  its own code. The planner's §5 write-probe is the first honest gate proposed —
  but it lives in a plan, not in any runner or cron body that will execute.

## C-10 — MEDIUM — v3's symlink convention silently breaks the spec's verification definition, and no counted snapshot exercises it

- Spec §4: "Verification of a copy = re-run `sha256sum -c MANIFEST.sha256`."
- `find -type l` over snapshots #1 and #2: **0 symlinks in both**. The counted
  snapshots are 100% regular files (and `sha256sum -c` on B today: 0 non-OK for both).
- v3 invents a different rule for links (hash of the target *string*) and admits in
  its own SNAPSHOT.md template that `sha256sum -c` "reports those 3 lines
  unreadable" — i.e. a #3 taken by this runner **cannot pass the spec's own
  verification command**, and its PASS is only reproducible by the runner's private
  `verify()`. A preservation format whose checker ships with the artifact is not
  independently verifiable. (The planner's §8.2 hand-waves this as "verify those
  separately" — that is the concession, not the cure.)

## C-11 — MEDIUM — SNAPSHOT.md is edited inside both "immutable" copies after the restore test

Spec §3: "no snapshot directory is ever renamed or edited after its restore test
passes." Both runners stamp the restore result by rewriting `SNAPSHOT.md` **in A
and in B** after the scratch copy was verified. The stamped A/B are therefore not
byte-identical to the copy that was restore-tested (the difference is confined to
SNAPSHOT.md, which the manifest excludes — by design, but then the sentence
"restore-test: PASS … from Location B" describes a B that no longer exists in its
tested form, and the final state of A/B is itself never re-verified after the edit).
On the flickering write layer of Oct 29–30, that final unreverified write is
precisely the class of write that produced 0-byte shells in the 2102 attempt
(registry: "records and apks arrived as 0-byte shells").

## C-12 — MEDIUM — The plan itself: a corrupted checksum, a dismissal that breaks retention, and the flaw it names but keeps

Charges against `findings/planner.md` specifically:

- §2's table prints snapshot #2's manifest sha256 as
  `0de9adf2…f4e09b2000194397e5cd90e740a174866c`. The real value (SNAPSHOT.md in
  both homes; recomputed today, matches) is
  `0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c`.
  The plan's string is a corrupted splice (`f4e09b…` occurs nowhere in the true
  hash). In a preservation cell, the manifest hash is the *one* string that must
  survive transcription exactly; the plan corrupts it in its summary table while
  §8.1 makes that same hash the anchor of the independent test.
- §2 dismisses `core-001` in one line ("ignore it"). core-001 is a registered
  PARTIAL whose SNAPSHOT.md honestly lists what it lacks; spec §8 requires
  FAILED/UNVERIFIED material be *kept until a COUNTED replacement exists* and its
  registry entry struck through, never silently dropped. "Ignore it" is not a
  retention decision Ren ever made; the plan makes it by parenthesis.
- D7 names the same-disk flaw correctly — then the plan's execution sequence (§6)
  still writes #3 to the same two homes on the same disk, with no off-box step
  added, although a working off-box channel demonstrably exists (the GitHub repo
  received 431 migrated files and lane branches the same night). Naming a
  CRITICAL flaw and scheduling around it is how #1 and #2 got here.

## C-13 — LOW / EXONERATION — The brief's "known flaw" (snapshot #1 manifest truncated mid-line) is not in the surviving artifact

Filed because a charge without proof does not file — and neither does a flaw:

- Parsed both surviving copies of #1's MANIFEST.sha256 (A and B) with a strict
  `<64-hex>␣␣<path>` regex: **3,325 lines, 0 malformed**, file ends with `\n`,
  last line complete (`…  trunk/settings.gradle.kts`).
- `sha256(MANIFEST.sha256)` in A and B both equal the `11ada2f4…e05fcf` recorded in
  SNAPSHOT.md; same for #2 (`0de9adf2…4866c` in both homes).
- Re-ran the spec's own test today on Location B: `sha256sum -c` → **0 non-OK
  lines for #1 and for #2**. The lanes' `BASE-MANIFEST.sha256` files inside #1
  contain `#` comment headers (9 non-hash lines each) — not truncation.
- So: either the truncation belonged to a superseded attempt (the FAILED
  `monolith-snapshot-001` / tar-era runner, whose debris was cleaned), or the flaw
  record is simply wrong. It cannot be pointed at in anything that survives.
  A preservation system whose flaw-register carries an unlocatable flaw has a
  registry-integrity problem of its own (the registry narrative IS inside the
  snapshots it describes — see WORK-REGISTRY in #2, which is also where the 2102
  failure and the "0-byte shells" detail live, and nowhere else).

## C-14 — LOW — Runner scope drift nobody re-ruled

- `run_snapshot3.py` prunes lane dirs named (`guh`, `build`, `.gradle`, `tmp`);
  `run_daily.py` prunes (`guh`, `guh2`, `build`, `.gradle`, `tmp`, `tmpdir`);
  the spec §1c names (`guh`, `build`, `.gradle`, `tmp`). Three documents, three
  prune sets — two snapshots taken by the two runners are not strictly comparable,
  and neither runner prunes lane `.git` (in #2, every lane's `.git` was copied —
  `find` shows `.git` under lanes/blueprint, caps, cascade, chrome, counterfoil,
  dawn, depcull, drawer, …), so a large slice of every snapshot is duplicated
  shared git history, not lane state.
- The docstring of `run_snapshot3.py` claims the records globs include
  `gate-build.*`; the code lists only `gate-build.log` / `gate-build.sh`. All of
  these globs are dead today anyway (planner D5) — the drift is noted because the
  *next* widening will be trusted the same way this one was.

---

## Verdict

The counted snapshots #1 and #2 are, as byte archives, *sound* — re-verified today
end-to-end (C-13). Everything around them is not: the redundancy is one disk (C-1)
holding the only copies of a deleted studio (C-2); the verifier cannot detect a
missing component (C-3); "restore" has never meant "rebuildable" (C-4); the daily
policy failed its first run against a deleted instruction file (C-5); the merge
cadence was never once kept (C-6); and the newest work — cells, findings, the
batch-9 ledger — sits outside the system entirely (C-7). Snapshot #3 as staged,
even after the planner's D1–D3 patches, preserves a smaller, older-shaped world
onto the same single disk. The fix is not a better run of this runner; it is an
off-box leg, a source-completeness assertion, and a restore test that builds.

— Critic, ANKER cell (issue #17). Charges filed with evidence; nothing modified.
