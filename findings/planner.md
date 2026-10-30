# Issue #17 — Snapshot #3: Staged Run Plan (PLANNER, Anker cell)

Prepared: Fri 2026-10-30 ~03:10 IST. **Preparation only — the snapshot was NOT run.**
Executor: Ren, at a healthy window (preconditions in §5). Runner: `~/workspace/preservation/run_snapshot3.py` (v3, Anker) — **do not run it as-is; apply the §7 fixes first.**

---

## 1. What the staged script does (read line-by-line)

`run_snapshot3.py` is a resumable variant of the Snapshot #2 runner (`run_snapshot1.py` lineage):

1. **Name/staging:** `NAME = <YYYY-MM-DD_HHMM IST>_manual-pre-truth3-merge`; stages into `~/workspace/preservation/.staging-snapshot-3/<NAME>`.
2. **Memory floor:** aborts if `MemAvailable < 350 MB` at start.
3. **Wipes the whole staging parent** (`.staging-snapshot-3/`) before starting — cross-run resume is impossible; resumability is *in-run only*: `copy_file` skips a destination that is already size+sha256-identical, and a `PermissionError`/vanished source retries the whole staging pass after a 25 s backoff, up to 12 attempts.
4. **Stages the payload** (§2 below), file-by-file, streaming (nothing held in memory). Symlinks are preserved as links; a link's manifest hash is the sha256 of its target *string* (so dangling fixtures stay verifiable).
5. **Manifest:** builds `MANIFEST.sha256` over the staged tree (`<sha256>  <relpath>` lines, sorted; excludes itself and `SNAPSHOT.md`), prints file count / total bytes / manifest sha256.
6. **Stage-verify:** re-hashes every staged file against the manifest; any failure = fail-stop (exit 2).
7. **Writes `SNAPSHOT.md`** into staging (trigger, operator, source-state text, counts, manifest hash, restore-test: PENDING).
8. **Publishes to two locations**, each with the same 12-attempt backoff-retry `copytree` (symlinks preserved), A first, then B *from verified A*:
   - **Location A:** `~/workspace/preservation/monolith-snapshots/<NAME>`
   - **Location B:** `~/workspace/your_files/monolith-archive/snapshots/<NAME>`
9. **Verifies A and B** against the manifest + manifest-hash equality; any failure = fail-stop.
10. **Restore test (spec S5):** copies B into `~/workspace/preservation/restore-test/<NAME>`, re-verifies every file by sha256, spot-opens 3 files (see defect D2), then stamps the PASS/FAIL line into `SNAPSHOT.md` in **both** A and B, and deletes the scratch + staging dirs.

## 2. Established shape — Snapshots #1 and #2 (both verified on disk today)

| | #1 `2026-10-29_1148_manual-first` | #2 `2026-10-29_2104_manual-post-batch7-pre-art-merge` |
|---|---|---|
| Layout | `swarm/ trunk/ lanes/ records/ apks/` + `MANIFEST.sha256` + `SNAPSHOT.md` | identical |
| Files / bytes | 3,325 files, 568,973,290 B (551M on disk) | 5,999 files, 974,007,921 B (944M on disk) |
| Manifest sha256 | `11ada2f4…e05fcf` | `0de9adf2…f4e09b2000194397e5cd90e740a174866c` |
| A/B manifest equality | identical in A and B ✔ (re-checked today) | identical in A and B ✔ (re-checked today) |
| Restore test | PASS (from B, 0 checksum failures) | PASS (from B, 0 checksum failures) |

(An older `core-001/` mini-snapshot — 17 files, no manifest — predates this shape; ignore it.)
**#3 must keep this exact layout** so the three snapshots stay mutually comparable: same five payload dirs, same manifest format (sha256 + two spaces + relative path, sorted, excluding `MANIFEST.sha256` and `SNAPSHOT.md`), same `SNAPSHOT.md` fields, same A→B→restore-from-B ceremony.

## 3. Target list — exact source paths (as the script copies them, verified to exist today)

Source root `SRC = ~/workspace/clone-inspect`:

| # | Source | Destination in snapshot | Status today |
|---|---|---|---|
| 1a | `SRC/design-refs/swarm/` (whole tree) | `swarm/` | **MISSING — deleted in the Oct 30 ~03:00 cleanup (GitHub is now the studio home). Script silently stages 0 files. See D1.** |
| 1b | `SRC/Focus-app/app/src/` | `trunk/app/src/` | exists (2.3M) |
| 1b | `SRC/Focus-app/{app/build.gradle.kts, build.gradle.kts, settings.gradle.kts, gradle.properties, metadata.json, debug.keystore}` | `trunk/…` | all 6 exist |
| 1b | `SRC/Focus-app/gradle/`, `SRC/Focus-app/public/` | `trunk/gradle/`, `trunk/public/` | exist (12K / 9.0M) |
| 1c | every `SRC/builds/<lane>/` where `<lane>/Focus-app` is a dir — copied whole-lane, pruning dirs named `guh`, `build`, `.gradle`, `tmp` at any depth | `lanes/<lane>/` | **21 lanes qualify today** (list below), ~470M raw before pruning |
| 1d | `SRC/{BUILD-CHANGES.md, DEAD-CODE-AUDIT.md, REMOVAL-LOG.md, VERDICT.md, env.sh, env21.sh}` | `records/` | all 6 exist |
| 1d | globs at `SRC/`: `monolith-brief-*.md`, `monolith-correction-brief.md`, `build*.log`, `test-*.log`, `merge-*.log`, `help*.log`, `gate-build.log`, `gate-build.sh`, `hb2-fix-verify.log`, `hb2-fix-verify.sh` (deduped) | `records/` | only the 3 brief `.md` files still match; **all log globs are dead post-cleanup (harmless — empty glob ≠ error). See D5.** |
| 1e | `SRC/monolith-*.apk` | `apks/` | only 2 remain: `monolith-batch8-truth-fixed.apk`, `monolith-batch9.apk` (~22M total; batches 1–7 APKs already live in #1/#2) |

**Lanes qualifying under the script's rule today (21):** dawn, flow-clock, look-blueprint, look-dawn, look-noir, look-signal, look-temple, merge-batch9, nofire, obsidian, onboarding, pill, routine-edit, truth-3-breaks, truth-3-intercept, truth-3-proofbase, truth-3-shorts, truth-3-sound, truth-3-sweep, truth-3-times, warrant.

**Not captured by the script's rules (decision for Ren, §7/D4):**
- `SRC/builds/cells/` (515M — the Oct 30 GitHub-native issue cells, `issue-2`…`issue-15`, each a repo clone with its own `.git`) and `SRC/builds/wt-ci/` (25M — CI worktree, contains `studio/`). Neither has a `builds/<name>/Focus-app` shape at the level the rule scans. They are git clones whose branches live on GitHub (`agent/issue-<n>`), so GitHub is arguably their preservation — but nothing in the snapshot proves that.
- `SRC/design-refs/` flat files (3.1M — 20 reference images + `CRITIQUE-deck-shield.md`, `INDEX.md`, `PLAN-vault-guard.md`): the script only ever took `design-refs/swarm`, so with swarm gone the snapshot would contain **zero** design-refs. Recommend adding the flat tree as `design-refs/` (new top-level dir; note the layout addition in SNAPSHOT.md).

## 4. Exclusions — what to leave out, and why

Already excluded by the script (keep):
- **`guh`, `build`, `.gradle`, `tmp` dirs inside lanes** — Gradle user homes, build outputs, gradle state, scratch. All regenerable by a build; in #2's era these were the multi-GB bulk. ✔ Justified.
- **Trunk build outputs / caches** — trunk is copied as source-only (`app/src` + named build files + `gradle/` + `public/`), so `Focus-app/build`, `Focus-app/.gradle`, `Focus-app/.kotlin`, `Focus-app/.aistudio` never enter. ✔
- **`SRC/trunk-guh/` (2.0G) and `SRC/tools/` (3.3G)** — gradle home + toolchain; never in scope, regenerable/re-downloadable. ✔
- **`SRC/shots/` (14M), `fonts-dl/` (500K), `fremd-view/` (724K)** — screenshots and download scratch; never in #1/#2 either. ✔ (If Ren wants the batch-9 proof shots preserved, they belong in records — flag, don't silently add.)

Recommended additions to the exclusion set (apply in the §7 patch):
- **Lane `.git` dirs** (`builds/*/Focus-app/.git`, ~8.9M × 21 ≈ 187M): the script does **not** prune them, so as-is they will be copied. Every lane `.git` is a clone of `Sarthak-2007/Focus-app`, whose history is on GitHub; lane uniqueness lives in the working tree, not in a duplicated copy of shared history. Excluding cuts ~35–40% of payload and of copy time — copy time is exactly what the flickering write layer punishes. *Trade-off to state plainly:* an unpushed lane-local commit/stash would be lost. Ren should confirm lanes hold no unpushed commits (or accept the risk) before pruning `.git`. Same logic for `builds/cells/*/.git` if cells are added.
- **`merge-batch9/guh-test/`** — probe-fixture tree (62 symlinks live under `builds/`, most of them here); it escapes the prune because the dir is named `guh-test`, not `guh`. Tiny, harmless either way; prune for consistency or keep — but decide, don't inherit by accident.

## 5. Preconditions for a "healthy window" (all must hold immediately before launch)

Measured at plan time (Fri 2026-10-30 ~03:10 IST): disk **115G available of 150G (23% used)** on `/home/hatch` — ample. `MemAvailable` ~1.3–1.7G and *falling between probes*, 1 Gradle daemon resident, load average ~10 — **tonight, right now, is NOT a healthy window.** The Oct 29/30 lesson (AGENTS.md): write death was RAM exhaustion masquerading as a permission wall, and Snapshot #3's own log (`snapshot3-run.log`) shows all 12 attempts dying with `Errno 1` while MemAvailable read 2,251 MB → 1,363 MB. **Memory alone is not the gate; demonstrated writes are.**

1. **Disk headroom:** `df -h ~/workspace` shows ≥ 10G available (actual today: 115G ✔). Estimated #3 payload ≈ 330M (with `.git` pruned) to ≈ 520M (as-is); peak transient usage = staging + A + B + restore scratch ≈ 4× payload ≈ 1.3–2.1G; final footprint ≈ 2× payload in A+B.
2. **Sustained write probe (the real gate):** in `~/workspace/preservation/`, write and re-read-verify (a) 100 small files, (b) one 100 MB file, (c) one JVM-shaped burst if a build is planned nearby — all must succeed back-to-back with zero `EPERM`. A single successful write proves nothing; the failure mode is waves.
3. **Memory:** `MemAvailable` ≥ 2.5G at launch (Snapshot #2's successful window measured 2,551 MB) — the script's own 350 MB floor is far too low (see D6). No Gradle daemons resident (`ps aux | grep -i gradle` = 0), or the resident daemon is the known warm one from a fresh generation; gradle.properties regime is the fixed `-Xmx2g / workers.max=2`.
4. **Fresh generation preferred:** the write wall decays over ~an hour of runtime generation age; launch early in a fresh runtime generation, not at the tail of a long one.
5. **Quiescence:** no lane/cell actively writing into `clone-inspect/builds/` during the copy (cells are live under issue work — Ren should pause or accept copy-minute state; #1/#2's convention was "the lane set as it stood at the copy minute", stated in SNAPSHOT.md).
6. **Name check:** no existing `monolith-snapshots/<NAME>` or B twin (the script `rmtree`s a same-named destination without asking — minute-resolution names make collision unlikely, not impossible).

## 6. Execution sequence for Ren (after the §7 patch)

1. Re-check §5 gates in order: `df`, write probe, `MemAvailable`, gradle-daemon count. If any gate fails, stop — do not "try anyway"; five failed attempts are already on record.
2. Apply the §7 edits to `run_snapshot3.py` (name/trigger/source-state text, spot-check paths, `.git` decision, design-refs decision). Diff-review the file once before running.
3. Launch in the background with logging, mirroring the previous runs:
   `cd ~/workspace/preservation && python3 run_snapshot3.py > snapshot3-run-<HHMM>.log 2>&1`
4. Watch for the ceremony's own checkpoints in order: `STAGE-CONVERGED` → `COUNTS` → `FILES/BYTES/MANIFEST_SHA256` → `STAGE-VERIFY ok=<n> fail=0` → `WROTE <A>` → `WROTE <B>` → `VERIFY-A/B … manifest_matches=True` → `RESTORE ok=<n> fail=0` → `RESTORE-TEST PASS` → `SUMMARY … restore=PASS`.
5. Any `FAIL-STOP` (exit 2): leave the debris in place, register the attempt FAILED (name + minute + MemAvailable at failure, as in `snapshot3-run.log` practice), and do not retry inside the same window — the in-run 12× backoff already rode the flicker.
6. On PASS: confirm `SNAPSHOT.md` in **both** A and B carries the stamped restore line (not `PENDING`), and that `.staging-snapshot-3/` and `restore-test/<NAME>` were cleaned by the script.
7. Run the independent §8 restore test (don't rely solely on the runner's self-report).
8. Report to Sarthak per his presentation rule: finished result only — name, file count, bytes, manifest sha256, restore PASS — no board tour.

## 7. Defects in the staged script — fix before running

- **D1 (BLOCKER) Swarm source is gone.** `stage_payload` 1a copies `SRC/design-refs/swarm`, deleted Oct 30 ~03:00 (collaboration moved to GitHub repo `Sarthak-2007/Focus-app`; plan/methods records migrated into the repo at `studio/plan/` + `studio/methods/`, commit 8438f5a). `os.walk` on a missing dir yields nothing, so the script **silently stages an empty/absent `swarm/`** — no error. Either (a) repoint 1a at the repo's `studio/` tree (present in `builds/wt-ci/studio/` and on GitHub; notably absent from trunk `Focus-app/`), or (b) drop the swarm section and record in SNAPSHOT.md that the studio record now lives in git. Silence is the only unacceptable option.
- **D2 (BLOCKER) Restore spot-checks are guaranteed to FAIL.** `spot_candidates` hardcodes `swarm/plan/SNAPSHOT-SPEC.md` and `swarm/mail/ren/*.md` — both deleted with the swarm tree. `restore_pass` requires **all** spots `:True`, so even a perfect copy ends `RESTORE-TEST FAIL` and stamps FAIL into both SNAPSHOT.md copies. Repoint spots at living files, e.g. `records/VERDICT.md`, first `.kt` under `trunk/app/src` (this one still works), and a lane file such as `lanes/merge-batch9/…`.
- **D3 (BLOCKER) SNAPSHOT.md text is stale and would enshrine false history.** `TRIGGER` and the source-state paragraph claim: pre-truth3-merge, trunk at batch-8 / DB v11, Truth Car 3 lanes "landed UNCOMPILED / in flight", APKs "batches 1-8", studio tree "live (mail and wall)". Reality now: batch 9 built and delivered (DB v12, `monolith-batch9.apk` 21:02 Oct 29), Truth Car 3 merged, looks lanes + issue cells exist, swarm/mail/wall deleted, GitHub is the studio home. Also the manifest note claims "exactly 3 payload entries are Methods queue-sim ghost fixtures (dangling → /nonexistent-target)" — with swarm gone there are none from that path (current dangling-ish links are the `merge-batch9/guh-test` probes). Rewrite name suffix (`manual-pre-truth3-merge` no longer describes the moment — suggest `manual-post-batch9-github-studio`), trigger, source-state, and the symlink note to match the copy minute.
- **D4 (GAP) New work since the script was written is invisible to it.** `builds/cells/` (515M, 14 issue-cell clones) and `builds/wt-ci/` (25M, the only local tree containing `studio/`) match no rule. Decide explicitly: include (with `.git` pruned) or exclude on the stated ground "preserved as GitHub branches `agent/issue-<n>`" — and write the decision into SNAPSHOT.md either way.
- **D5 (MINOR) Dead record globs.** All `*.log`/`gate-build.*`/`hb2-fix-verify.*` patterns match nothing post-cleanup (root logs were removed). Harmless, but `records/` shrinks from #2's 117 files to ~9. If the build logs matter, they're gone from the root — say so in SNAPSHOT.md rather than letting the count drop unexplained.
- **D6 (RISK) 350 MB memory floor is miscalibrated.** The script's own failure log shows `Errno 1` deaths at 2,251 MB available. The floor guards against a failure mode that isn't the binding one. Raise the effective gate operationally (§5.3: ≥2.5G + write probe), or patch the constant — running under the script's floor alone invites a sixth failed attempt.
- **D7 (RISK) A and B are the same disk.** Both locations sit on `/dev/mapper/rv` (`/home/hatch`) — A/B protects against botched writes and partial copies, not against device/VM loss. True for #1/#2 as well (established shape), but state it: Snapshot #3 is a *working-state* preservation point, not an off-device backup. (The repo half of the studio is genuinely off-device on GitHub; the lane trees are not.)
- **D8 (MINOR) Verification asymmetry.** `verify()` checks that every manifest entry exists with the right hash, but never checks for *extra* files in A/B, and `build_manifest`'s exclusion of `MANIFEST.sha256`/`SNAPSHOT.md` only matches those names at payload root. Acceptable for shape-consistency with #1/#2; noted so nobody over-claims what "verified" means.
- **D9 (MINOR) Destructive edges.** Start-of-run `rmtree` of the entire `.staging-snapshot-3/` parent (a leftover partial staging from the 01:37 attempt — currently holding a single `swarm/MAXIMPL.md` — will be destroyed; it's debris of a registered FAILED attempt, but Ren should know), and `rmtree` of any same-named A/B destination. No dry-run mode exists; the §6 diff-review + name check is the safeguard.

## 8. Restore-test procedure (independent proof of #3, after the run)

The runner performs its own S5 restore test from B; this is the *independent* re-proof, mirroring how #1/#2 stand verified today:

1. **Manifest identity:** `sha256sum` the `MANIFEST.sha256` in A and in B — both must equal the `manifest_sha256` printed in the run's `SUMMARY` line and recorded in `SNAPSHOT.md`.
2. **Full hash verification of the restore copy:** copy **B** (not A) to a scratch dir outside both locations, e.g. `~/workspace/preservation/restore-test/manual-<NAME>/`, then inside it run the runner's convention: every manifest line's file must exist and hash-match. `sha256sum -c MANIFEST.sha256` works for all regular files; any preserved symlinks will report unreadable under `sha256sum -c` — verify those separately by comparing `readlink` output against the manifest's hash-of-target-string convention. Expect: **0 failures, file count == manifest line count**.
3. **File-count cross-check:** `find <restore> -type f -o -type l | wc -l` == manifest lines + 2 (`MANIFEST.sha256` + `SNAPSHOT.md`); total bytes == the `BYTES` figure in the run log.
4. **Spot-restore (open real content, not just hashes):** open and read the head of (a) a trunk source file, e.g. the first `.kt` under `trunk/app/src`; (b) `records/BUILD-CHANGES.md`; (c) one lane file unique to #3's era, e.g. under `lanes/merge-batch9/`; (d) byte-compare one APK against the source: `sha256sum` of restored `apks/monolith-batch9.apk` == `~/workspace/clone-inspect/monolith-batch9.apk`. All must be real, readable, non-trivial content.
5. **Stamp check:** `SNAPSHOT.md` in the restored copy reads `restore-test: PASS <date/time> from Location B, <n> files, 0 checksum failures`.
6. Clean the manual scratch dir; leave A and B untouched. Record the independent test outcome (name, counts, manifest hash, PASS) alongside the run log in `~/workspace/preservation/`.

---

*Planner's note: nothing was copied and the runner was not executed. All path/size/disk figures above were measured read-only on Fri 2026-10-30 ~03:05–03:15 IST. The three D1–D3 blockers mean the staged script, run as-is, would produce a snapshot that is simultaneously incomplete (no studio record), mislabelled (batch-8-era text), and self-stamped FAIL — patch first, run second.*
