# Issue #17 — Snapshot #3: Failure Behaviour & the Script Amendment List (FINISHER-4, Anker cell)

Prepared: Fri 2026-10-30 ~03:20 IST. **Preparation only — nothing was amended, nothing was run, nothing large was copied.**
Sources read line-by-line: `~/workspace/preservation/run_snapshot3.py` (358 lines; all line numbers below are that file),
`findings/planner.md` (defects D1–D9), `~/workspace/preservation/snapshot3-run.log` (the 01:37 attempt),
`~/workspace/preservation/snapshot1-run.log` (the healthy ceremony, Snapshot #1). Disk state re-checked read-only today:
`design-refs/swarm/` is absent, `builds/wt-ci/studio/{plan,methods}` exists, the 01:37 staging leftover is still on disk.

**Verdict in one line:** do not run `run_snapshot3.py` as-is. Run unamended, its most likely "successful" outcome is a
snapshot that is silently incomplete, falsely labelled, and permanently stamped `RESTORE-TEST FAIL` — while exiting 0.
Apply the **16 amendments in §1, in order**, diff-review once, then run under the planner's §5 healthy-window gates.

---

## 1. The amendment list — exact, ordered, checkable

Apply in this order (it follows the script top-to-bottom, so the diff reads cleanly). Each item names the defect it
cures (planner D-number, or FG = failure-grammar finding in §2) and the line/section it touches. The check is how Ren
proves the amendment is in before launch. Items marked **[DECISION]** need Ren's explicit choice first — the choice,
whichever way it goes, must end up written in the script and in `SNAPSHOT.md`; inheriting the current behaviour by
accident is the one unacceptable outcome.

### Guard rails first

- **A1 — Stop the silent startup wipe. Cures D9 / FG-F1.**
  *Touches:* `main()` prologue, lines **195–198** (`stage_parent … shutil.rmtree(stage_parent)`).
  *Change:* before any `rmtree`, print the full contents being destroyed (`find`-equivalent listing + byte total) to
  stdout, so the run log names what died. Absent an explicit `--clear-staging` style acknowledgement in the code path
  Ren reviews, abort with `fail_stop` if `.staging-snapshot-3/` is non-empty. The known current content is exactly
  one file — `.staging-snapshot-3/2026-10-30_0137_manual-pre-truth3-merge/swarm/MAXIMPL.md` — the sole on-disk debris
  of the registered FAILED 01:37 attempt; it must be named in the log or moved aside, never vapourised silently.
  *Check:* `grep -n "rmtree" run_snapshot3.py` — the staging-parent call is preceded by a listing/abort; a dry read of
  `main()` shows no unconditional wipe remains.

- **A2 — Replace the 350 MB memory floor with a gate that matches the observed failure. Cures D6 / FG-F0.**
  *Touches:* line **193** (`if mem_available_mb() < 350`) and the helper at lines **40–45**.
  *Change:* raise the floor to **2560 MB** (planner §5.3: Snapshot #2's successful window measured 2,551 MB;
  the 01:37 log shows death at 2,251 MB, so 350 MB guards nothing), and re-check memory immediately before the
  Location A write and before the restore copy — the 01:37 decay was ~900 MB *during* the run, so a start-only check
  certifies a window that may no longer exist. The operational sustained-write probe (planner §5.2) stays a launch
  precondition outside the script.
  *Check:* the constant `350` no longer exists in the file; `MemAvailable` is consulted at ≥3 points in `main()`.

### Make a missing source loud, then fix the sources

- **A3 — `copy_tree` must fail on a missing source root instead of staging nothing. Cures the mechanism under D1 / FG-F2.**
  *Touches:* `copy_tree`, lines **78–90**, used by `stage_payload` §1a–§1e.
  *Change:* at entry, `if not os.path.isdir(src_root): raise FileNotFoundError(src_root)`. Today `os.walk()` on a
  nonexistent directory yields zero iterations and raises nothing (no `onerror` is set), so *any* vanished tree —
  swarm today, any trunk subtree tomorrow — stages as a silent 0 and passes stage-verify vacuously, because the
  manifest is built from whatever staging contains. Note the consequence once A3 lands: a missing tree enters the
  staging retry loop as an `OSError` and burns all 12 attempts (~5 min) before `FAIL-STOP` — acceptable, because it
  fails; silence is not.
  *Check:* read `copy_tree` — the guard is the first statement; the raised type is an `OSError` subclass so the
  existing retry/`fail_stop` path handles it.

- **A4 — Resolve the swarm source: repoint or record the absence. Never leave §1a as-is. Cures D1 (BLOCKER).**
  *Touches:* `stage_payload` §1a, lines **95–98** (`SRC/design-refs/swarm` → `STAGE/swarm`).
  *Change — Ren picks one:* **(a)** repoint §1a at the migrated studio record, `SRC/builds/wt-ci/studio`
  (verified on disk today: `studio/plan/` + `studio/methods/`, the tree migrated to GitHub repo
  `Sarthak-2007/Focus-app` at commit `8438f5a`), staged under a truthful destination name (e.g. `studio/`), with the
  layout addition noted in `SNAPSHOT.md`; **or (b)** delete the §1a copy entirely and write into the `SNAPSHOT.md`
  template (A10) that the swarm tree was deleted Oct 30 ~03:00, that the studio record now lives in git, and that
  this snapshot intentionally contains no `swarm/`. Keeping the present code is not an option: it stages an
  absent/empty `swarm/` with `counts["swarm"] == 0` and no error, breaking comparability with #1/#2 (1484 swarm
  files in #1) while looking like success.
  *Check:* either §1a's source path exists on disk at launch (`test -d`), or §1a is gone and the template text in
  A10 carries the deletion note. `COUNTS` in the run log must never again show a silent `swarm: 0`.

- **A5 — [DECISION] Capture the flat `design-refs/` files or exclude them on the record. Cures the gap in planner §3 / D4.**
  *Touches:* new block in `stage_payload` immediately after §1a (after line **98**).
  *Change:* with swarm gone, the script as-is captures **zero** design-refs. Recommended: copy
  `SRC/design-refs/` flat files (verified today: 20 reference images plus `CRITIQUE-deck-shield.md`, `INDEX.md`,
  `PLAN-vault-guard.md` and the `view-*/viewcrit-*` derivatives) to `STAGE/design-refs/`, noting the new top-level
  dir in `SNAPSHOT.md`. If Ren excludes them, the exclusion and its reason go into the template text instead.
  *Check:* a `counts["design-refs"]` entry exists, or the template carries the exclusion sentence — one of the two
  is greppable in the amended file.

- **A6 — [DECISION] Decide `builds/cells/` and `builds/wt-ci/` explicitly. Cures D4.**
  *Touches:* the lane rule, lines **114–124** (`builds/<lane>` holding a `Focus-app` dir).
  *Change:* neither tree matches the rule — `builds/cells/` (~515 MB of issue-cell repo clones; `issue-2` …
  `issue-17` and variants on disk today) and `builds/wt-ci/` (~25 MB, the only local tree holding `studio/`) are
  invisible to the snapshot. Either add a copy block for them (with `.git` handled per A7), or leave them out **and**
  write the ground into the template: "preserved as GitHub branches `agent/issue-<n>`; not in this snapshot."
  Silence would let a future reader believe #3 covers the cell era when it does not.
  *Check:* the decision sentence is present in the `SNAPSHOT.md` template (A10), whichever way it goes.

- **A7 — [DECISION] Prune lane `.git` dirs; settle `guh-test`. Cures the payload risk in planner §4.**
  *Touches:* the lane `copy_tree` call, lines **121–123**, `prune_names=("guh", "build", ".gradle", "tmp")`.
  *Change:* add `".git"` to `prune_names` — as-is, ~21 lane clones × ~8.9 MB ≈ **187 MB** of duplicated shared
  history (every lane `.git` clones the same GitHub repo) is copied, inflating payload and copy time by roughly a
  third, and copy time is exactly what the flickering write layer punishes. **Precondition Ren must confirm first:**
  no lane holds an unpushed commit or stash that exists only in its `.git` — if one does, that lane is exempted and
  the exemption is named in `SNAPSHOT.md`. Same line: decide `merge-batch9/guh-test/` (probe-fixture tree that
  escapes the prune only because it is named `guh-test`, not `guh`) — prune it or keep it, deliberately.
  *Check:* the amended `prune_names` tuple is diff-visible; Ren's no-unpushed-commits confirmation is recorded
  alongside the run log before launch.

- **A8 — [HOUSEKEEPING] Acknowledge the dead record globs in the text. Cures D5.**
  *Touches:* the glob list, lines **133–136**, and the template note via A10/A11.
  *Change:* no code change is required — empty globs are harmless — but `records/` drops from 117 files (#2) to ~9
  because every `build*.log` / `test-*.log` / `merge-*.log` / `help*.log` / `gate-build.*` / `hb2-fix-verify.*`
  pattern now matches nothing post-cleanup. The template must say the root logs are gone, so the count drop is
  explained history, not suspected data loss.
  *Check:* the sentence is in the template; the glob list itself is unchanged.

### Fix the words the snapshot will be believed by

- **A9 — Rename the snapshot. Cures D3.**
  *Touches:* line **33** (`NAME = … + "_manual-pre-truth3-merge"`) and the module docstring, lines **2–5**.
  *Change:* the suffix becomes **`_manual-post-batch9-github-studio`** (planner's suggestion) or an equivalent
  Ren-approved label that is true at the copy minute. Nothing about this run is "pre-truth3-merge": Truth Car 3 is
  merged and batch 9 is delivered.
  *Check:* `grep -n "pre-truth3" run_snapshot3.py` returns nothing outside an explicitly historical copy-history
  mention (A11).

- **A10 — Rewrite `TRIGGER` and the source-state paragraph to the copy minute's reality. Cures D3 (BLOCKER).**
  *Touches:* line **36** (`TRIGGER = …`) and the f-string template, lines **237–246**.
  *Change:* delete every stale claim — "pre-merge preservation point", "Truth Car 3 lanes landed uncompiled /
  in flight", "trunk at batch-8 truth-merge state (DB v11)", "batch 8-fixed is the build on Sarthak's phone",
  "shipped APKs batches 1-8", "studio tree live at copy time (mail and wall…)". Write what is true now: trunk at
  **batch-9, DB v12**, Truth Car 3 **merged**, `monolith-batch9.apk` built (Oct 29 21:02, 10,995,124 B) and
  delivered; APKs at the source root are exactly `monolith-batch8-truth-fixed.apk` + `monolith-batch9.apk`; the
  looks lanes and the GitHub issue cells exist; the swarm/mail/wall tree was deleted Oct 30 ~03:00 and the studio
  lives in the GitHub repo. Plus the A4/A5/A6 decision sentences and the A16 disclosures land here.
  *Check:* `grep -n "batch-8\|DB v11\|UNCOMPILED\|in flight" run_snapshot3.py` returns nothing in `TRIGGER` or the
  template; the words `batch-9`, `DB v12`, and `GitHub` each appear in the template.

- **A11 — Rewrite the manifest symlink note and the copy-history line. Cures D3 (symlink half) + D5.**
  *Touches:* template lines **249–264**.
  *Change:* delete "exactly 3 payload entries are Methods queue-sim ghost fixtures
  (`swarm/methods/queue-sim/*/compile-ghost.flag`, dangling by design -> /nonexistent-target)" — with swarm gone
  there are **zero** such entries; the remaining dangling-link population is the `merge-batch9/guh-test` probe set,
  whose real count Ren states from the amended payload (or omits a count rather than inventing one). Update the
  copy history: attempts 2353/0014/0018 were registered in `plan/WORK-REGISTRY.md` *inside the deleted swarm
  tree* — cite where that registry lives now (repo `studio/plan/`) — and add the **01:37 attempt** to the history:
  12 staging attempts, all `PermissionError [Errno 1]`, MemAvailable 2,251 MB → 1,363 MB, registered FAILED.
  Add the D5 records-shrink sentence (A8) here if it is not in A10's paragraph.
  *Check:* `grep -n "queue-sim\|/nonexistent-target" run_snapshot3.py` returns nothing (or only inside a sentence
  that says these fixtures no longer exist); `0137` appears in the template's history.

### Fix the verdict machinery

- **A12 — Repoint the restore spot-checks at files that exist. Cures D2 (BLOCKER).**
  *Touches:* `spot_candidates`, lines **311–315**, and the mail-letter filler, lines **323–327**.
  *Change:* spot 1 `swarm/plan/SNAPSHOT-SPEC.md` → **`records/VERDICT.md`** (staged from `SRC/VERDICT.md`, verified
  on disk). Spot 3 `swarm/mail/ren/*.md` → a deterministic **text** file from the new era, filled with the same
  walk pattern the trunk-kt spot already uses (lines 316–322): first `.kt` under
  `lanes/merge-batch9/Focus-app/app/src`, tag renamed `lane-kt`. Spot 2 (trunk-kt) is correct — leave it. Two
  constraints the replacement must respect: (i) the spot reader at lines **328–334** opens with
  `encoding="utf-8", errors="strict"`, so the target must be a text file — never an APK or any binary, which would
  raise `UnicodeDecodeError` and crash the run *after* a perfect restore (see FG-F10); (ii) `restore_pass` at lines
  **336–337** requires **all** spots to end `:True`, so every repointed path must exist in the *staged* layout
  (i.e. under `SCRATCH/records/…`, `SCRATCH/lanes/merge-batch9/…`), not merely at the source.
  *Check:* `grep -n "swarm/" run_snapshot3.py` returns nothing in the restore section; each new spot path's staged
  equivalent corresponds to a file that exists at its source today.

- **A13 — Verify A before B is copied from it. Cures FG-F7/F8 (the code contradicts its own ceremony).**
  *Touches:* restructure lines **272–298**: the write loop (272–290) currently writes A **and** B, and only then
  does the verify loop (292–298) check either.
  *Change:* interleave — write A → run the existing `verify()` + manifest-hash check for A → only on pass, write B
  from A → verify B. Today B is copied from an *unverified* A, so a corrupt A is faithfully duplicated before
  anyone looks; and if A later fails verification, the run has already spent a full B copy. (The planner's §1
  description already assumes "B *from verified A*" — make the code match its own spec.)
  *Check:* in the amended `main()`, the string `VERIFY-A` (or its verify call) textually precedes the B write.

- **A14 — Guard same-name destruction at A and B. Cures D9 / FG-F6.**
  *Touches:* lines **274–275** (pre-write `rmtree(dst)`) and **280–281** (per-retry `rmtree(dst)`).
  *Change:* before the first delete of `LOC_A/<NAME>` or `LOC_B/<NAME>`, if that directory already contains a
  `MANIFEST.sha256`, treat it as a pre-existing snapshot, not debris: `fail_stop` and let Ren rename/move it
  deliberately. The per-retry delete may keep firing only on a destination this same run created (track a flag).
  `NAME` has minute resolution, so a collision needs only a same-minute relaunch — unlikely, not impossible — and
  today the code would destroy a good snapshot *before* its replacement exists, then possibly fail the write and
  leave neither.
  *Check:* read the write loop — a manifest-presence guard precedes the first `rmtree` for each location.

- **A15 — Every ceremony write fails as `FAIL-STOP`, and the final stamp is verified. Cures FG-F3/F5/F11.**
  *Touches:* manifest write lines **222–223**; staging `SNAPSHOT.md` write lines **267–268**; the `verify()` calls
  at lines **227, 294, 307** (whose internal `sha256_of` can raise `OSError` uncaught); stamp loop lines **343–348**.
  *Change:* wrap each in `try/except OSError → fail_stop(...)` so every death in the ceremony exits 2 with the
  `FAIL-STOP:` + MemAvailable lines, instead of an unhandled traceback and exit 1. In the stamp loop specifically:
  write to a temp file in the same directory and rename over `SNAPSHOT.md` (the current truncate-then-write at
  line 347 can leave an archive's `SNAPSHOT.md` empty if the write dies mid-stamp, with A already stamped and B
  not — the two copies silently divergent), then re-read and assert the PENDING line was actually replaced —
  `str.replace` at line 348 silently no-ops if the needle ever differs, leaving both copies `PENDING` under a
  `SUMMARY` that claims a verdict.
  *Check:* no bare `open(..., "w")` remains in `main()` outside a `try`; the stamp block contains a post-write
  assertion/re-read.

- **A16 — A restore FAIL must behave like a failure, and the template must disclose what "verified" does not mean. Cures FG-F11 + D7 + D8.**
  *Touches:* verdict/cleanup block, lines **336–355**, and the template (via A10).
  *Change:* (i) If `restore_pass` is False, after stamping, exit non-zero (`fail_stop`-style, exit 2) and **do not**
  delete `SCRATCH` or the staging parent — today lines **350–351** delete both with `ignore_errors=True` and the
  script exits **0** on FAIL exactly as on PASS (lines 353–355 print a `SUMMARY` either way), so any watcher keying
  on the exit code records a failed snapshot as a success, and the evidence needed to re-test is gone. (ii) Add two
  disclosure sentences to `SNAPSHOT.md`: A and B both live on the same device (`/dev/mapper/rv`, `/home/hatch`) —
  #3 is a working-state preservation point, **not** an off-device backup (D7); and verification checks that every
  manifest entry exists with a matching hash — it does **not** detect extra files in A/B (D8), so "verified" must
  never be over-claimed.
  *Check:* the FAIL branch of the amended block contains a non-zero exit and no `rmtree` of scratch/staging; both
  disclosure sentences are greppable in the template.

**Count: 16 amendments (A1–A16). Blockers among them: A3+A4 (D1), A12 (D2), A9+A10+A11 (D3).**
No amendment changes the established #1/#2 shape — same five payload dirs (plus only the A4/A5 additions, noted in
`SNAPSHOT.md`), same manifest format, same A→B→restore-from-B ceremony — except A13, which makes the code finally
do the ceremony its own spec already describes.

---

## 2. The failure grammar — what happens at every point the run can die

How to read this: for each phase, the **world state** the death leaves behind, **what the operator sees** (exact
output shapes from the code), **what is safe to retry**, and what is **not**. Two cross-cutting facts first:

- **Exit-code vocabulary is broken in three places.** The script has three ways to die: `fail_stop` → exit **2** with
  `FAIL-STOP:` + `MemAvailable at failure:` lines (lines 182–187); an uncaught exception → traceback, exit **1**, no
  `FAIL-STOP` line; and a restore **FAIL verdict → exit 0**, indistinguishable from PASS except by reading the
  `SUMMARY` text. A15/A16 exist because of this.
- **Retries are asymmetric.** Staging, the A write, and the B write each get 12 attempts with 25 s backoff
  (~5 minutes of backoff alone per phase). The restore copy from B — the phase everything else exists to prove —
  gets **one** attempt, no retry (lines 301–306). A single write-flicker there kills a fully verified snapshot.
- **The night's own evidence.** Snapshot #1's log is the healthy grammar end-to-end: `STAGE-VERIFY ok=3325 fail=0` →
  `WROTE` A → `WROTE` B → `VERIFY-A/B … manifest_matches=True` → `RESTORE ok=3325 fail=0` → all three spots `:True` →
  `RESTORE-TEST PASS`, starting at just **967 MB** MemAvailable. Snapshot #3's 01:37 log is the sick grammar:
  12 × `STAGE-ATTEMPT n died: PermissionError: [Errno 1] Operation not permitted`, MemAvailable decaying
  **2,251 → 1,363 MB** across the attempts, then `FAIL-STOP`. Conclusion the grammar must encode: low memory alone
  did not kill #1, and ample memory did not save 01:37 — a passing memory gate certifies almost nothing (hence A2's
  probe precondition in planner §5).

### F0 — Memory gate (lines 191–194)

- **World state:** untouched. The gate runs before the staging wipe (line 195), so this is the only death that
  destroys nothing, stages nothing, and leaves no debris.
- **Operator sees:** `NAME …`, `MemAvailable at start: … MB`, then `FAIL-STOP: MemAvailable below 350 MB floor; not starting.` + exit 2.
- **Safe to retry:** yes, freely, once a genuinely healthy window exists (planner §5: ≥2.5 GB *and* a sustained
  write probe *and* no resident Gradle daemon). Retrying into the same window just re-fails.
- **Never:** treat a *pass* at this gate as evidence the run will survive — 01:37 passed a 350 MB floor six times over.

### F1 — Startup staging wipe (lines 195–198) — *a destructive step that is not a checkpoint*

- **World state:** on success, the entire `.staging-snapshot-3/` parent is gone, including the 01:37 leftover
  (`…_0137_manual-pre-truth3-merge/swarm/MAXIMPL.md`). If the `rmtree` itself hits the write wall mid-delete, the
  parent is left half-deleted in an unrecorded state.
- **Operator sees:** *nothing.* No line names what was destroyed, how much, or that anything existed. A mid-delete
  death surfaces as a raw traceback (exit 1) — this call is not wrapped.
- **Safe to retry:** the wipe is idempotent, but what it destroyed is not recoverable; "retry" is meaningless for
  the lost debris.
- **Never:** launch the script as a "harmless test" — its second action is an unconditional, unlogged deletion
  (hence A1). This is also why the script's in-run resumability (skip-if-identical, lines 71–74) is *in-run only*:
  cross-run resume is impossible by design, and any converged-but-unpublished staging from an interrupted run is
  destroyed by the next launch without a word.

### F2 — Staging attempts (lines 202–214)

- **World state:** sources are only ever read — `SRC` is never modified by staging. Staging holds a partial payload
  that grows attempt by attempt (identical files are skipped, lines 71–74). `LANES […]` prints on every attempt, so
  the log repeats it up to 12 times.
- **Operator sees:** per attempt, `STAGE-ATTEMPT n died: PermissionError: [Errno 1] Operation not permitted` +
  `MemAvailable: … MB; backoff 25 s, resuming`; on convergence, `STAGE-CONVERGED attempt=n`. On exhaustion:
  `FAIL-STOP: payload copy did not converge after 12 attempts: [Errno 1] …`, `MemAvailable at failure: 1363 MB`,
  `Exception: PermissionError: …`, exit 2 — exactly the tail of `snapshot3-run.log`.
- **Safe to retry:** in a *new* healthy window only; the in-run loop has already ridden the flicker 12 times, and
  the planner's rule stands — register the attempt FAILED (name + minute + MemAvailable at failure) and do not
  retry in the same window. Beware one misdiagnosis: a deterministically missing source file
  (`FileNotFoundError` from the fixed records list, lines 128–131, or — after A3 — a missing tree) is also an
  `OSError`, so the loop will burn all 12 attempts over ~5 minutes and report it in the same language as the
  flicker. Read the exception text, not just the attempt count.
- **Never:** assume the partial staging left behind will survive to help the next run — F1 deletes it at launch.

### F3 — Manifest write (lines 220–225) — *unwrapped*

- **World state:** staging is complete but uncertified: no `MANIFEST.sha256` (or a truncated one), `SNAPSHOT.md`
  not yet written, A and B untouched. The staged payload itself is intact.
- **Operator sees:** an unhandled traceback (exit 1), no `FAIL-STOP`, no MemAvailable line. Easily misread as a
  coding crash rather than the same write wall.
- **Safe to retry:** only via a full relaunch — which first destroys the good staging (F1) and re-copies everything.
  There is no resume-from-manifest entry point. That asymmetry (a one-line file's death costs a full re-copy) is
  what A15 mitigates by at least naming the death correctly.

### F4 — Stage-verify (lines 227–230)

- **World state:** staging left fully intact on disk — the best diagnostic moment in the run. A and B untouched.
- **Operator sees:** `STAGE-VERIFY ok=<n> fail=<k>`, then `FAIL-STOP: staging verification failed on [<first 5 paths only>]`, exit 2. Only five failing paths are ever printed; a systemic failure looks identical to a five-file one.
- **Safe to retry:** diagnose from the staging tree *first* — its hashes against the sources tell flicker apart
  from source mutation during the copy (live cells writing into `builds/` mid-copy produce a Frankenstein payload
  the manifest will happily certify, because the manifest is built *from staging*, lines 220–221 — it proves the
  copy is self-consistent, never that the source stood still; hence planner §5.5 quiescence).
- **Never:** relaunch before inspecting — the relaunch's first act (F1) destroys the evidence.

### F5 — Staging `SNAPSHOT.md` write (lines 267–268) — *unwrapped*

- Identical grammar to F3: traceback, exit 1, staging + manifest intact, A/B untouched, full re-copy on retry.
  Whatever text bug survived amendment review (A9–A11) is now frozen into the snapshot-to-be — this write is why
  the §1 text amendments are blockers, not cosmetics.

### F6 — Location A write (lines 272–290, first iteration)

- **World state:** each attempt that dies leaves a *partial* A at `monolith-snapshots/<NAME>`; the next attempt
  deletes it and recopies from STAGE, which remains intact and is the source of truth for the whole phase. If all
  12 attempts die, the final partial A is left in place (no cleanup after the last failure). B has not been touched.
- **Operator sees:** `WRITE <dst> attempt n died: …; backoff 25 s` per attempt, `WROTE <dst>` on success, or
  `FAIL-STOP: location write never converged: <dst>`, exit 2.
- **Safe to retry:** yes — relaunch in a healthy window, or copy from the still-intact staging by hand. Nothing
  published exists yet, so nothing good can be lost *by the retry itself*…
- **Never:** …except the F6 trap A14 guards: if `<NAME>` already held a real snapshot, lines 274–275 destroyed it
  *before the first attempt*, so a subsequently failing write leaves neither the old snapshot nor the new one.

### F7 — Location B write (lines 272–290, second iteration)

- **World state — the dangerous half-published state:** A is complete on disk but **unverified** (verification is
  F8, after both writes) and its `SNAPSHOT.md` still reads `restore-test: PENDING`. If B's 12 attempts die, the
  world holds one full-looking, never-verified, PENDING-stamped copy in Location A and partial debris in B.
- **Operator sees:** same `WRITE … died` / `WROTE` shapes, then `FAIL-STOP: location write never converged: <B path>`, exit 2.
- **Safe to retry:** a relaunch recopies everything (and, per F1/F6, wipes staging and any same-name A first).
  A hand-copy of A → B is also sound *if* A is verified by hand first.
- **Never:** let anyone treat the surviving A as a finished snapshot — until F8 and the stamp (F11) complete, it is
  a claim, not a record. And note F7's quiet flaw (A13): B's source is A *as written*, not A *as verified*, so a
  silently corrupt A would be duplicated faithfully and only caught at F8, after the copy cost is spent twice.

### F8 — Verify A, then B (lines 292–298)

- **World state:** if A fails, B is never checked (the loop `fail_stop`s on the first bad location) — but B was
  already fully written in F7. If B fails, A remains in place, verified-good but PENDING-stamped. Staging is still
  intact — the last moment the full evidence chain (source → staging → A → B) coexists.
- **Operator sees:** `VERIFY-A ok=<n> fail=<k> manifest_matches=True/False` (likewise B), then possibly
  `FAIL-STOP: location <tag> verification failed: [<first 5 paths>]`, exit 2.
- **Safe to retry:** diagnose here before anything else; all four trees exist to compare.
- **Never:** over-claim a pass — `verify()` (lines 169–179) iterates only manifest entries: a file can be *extra*
  in A/B and no check will ever see it (D8, disclosed by A16). "Verified" means "nothing listed is missing or
  altered", full stop.

### F9 — Restore copy from B (lines 300–306) — *single attempt, no retry*

- **World state:** the pre-delete at lines 302–303 silently destroys any previous `restore-test/<NAME>` scratch.
  A failed `copytree` leaves partial scratch in `restore-test/<NAME>`, and A+B — complete, verified in F8 — are
  left permanently stamped `restore-test: PENDING`, because the stamp step (F11) is never reached. A good snapshot
  is left *looking untested forever*.
- **Operator sees:** `FAIL-STOP: restore-test copy from B died: …` + exception line, exit 2. No `RESTORE` line at all.
- **Safe to retry:** there is no restore-only path in the script — recovery is a full relaunch, or the planner's
  §8 independent restore procedure against B by hand (which is the better answer: it also double-checks the runner).
- **Never:** "fix" the PENDING stamp by hand-editing A/B's `SNAPSHOT.md` — an edited stamp destroys the very
  provenance the snapshot exists to provide.

### F10 — Restore verify + spot-open (lines 307–338)

- **World state:** scratch holds a full copy of B. A hash failure here is real evidence about B; a *spot* failure
  may be evidence only about the spot list. Exceptions inside `verify(SCRATCH, …)` or the spot reader (strict
  UTF-8, line 330) are unwrapped: a `UnicodeDecodeError` on a binary spot target crashes with a traceback, exit 1,
  stamps never written, scratch left behind.
- **Operator sees (healthy, from #1's log):** `RESTORE ok=3325 fail=0 files=3325 bytes=568973290`,
  `SPOT-OPEN plan-md:SNAPSHOT-SPEC.md:True trunk-kt:…:True mail-letter:…:True`, `RESTORE-TEST PASS`.
- **Operator sees (the script as it stands today, guaranteed):** `RESTORE ok=<n> fail=0` — the data perfect — then
  `SPOT-OPEN plan-md:MISSING trunk-kt:…:True mail-letter:MISSING`, then `RESTORE-TEST FAIL`, because
  `restore_pass` (lines 336–337) requires *every* spot `:True` and two of the three hardcoded spots point into the
  deleted swarm tree (D2). This is a verdict failure, not a data failure, and nothing in the output distinguishes
  the two kinds except reading the spot line itself.
- **Safe to retry:** the independent §8 procedure can prove the data independently — but it cannot repair the
  stamp the script is about to write (F11).

### F11 — Stamp, cleanup, exit (lines 340–355) — *the irreversible phase, and the worst failure mode in the script*

- **World state:** the verdict line is written into `SNAPSHOT.md` in **both** A and B by truncate-then-write
  (lines 347–348): no temp file, no re-read, no error handling. If the A stamp succeeds and the B stamp dies
  mid-write, B's `SNAPSHOT.md` can be left truncated or empty, A carries the verdict, and the two archive copies
  have silently diverged — with a traceback (exit 1) as the only signal. If the replace needle
  (`"- restore-test: PENDING\n"`) ever fails to match, the stamp silently no-ops: both copies stay `PENDING`
  while the log claims a verdict.
- **On completion:** scratch and the staging parent are deleted with `ignore_errors=True` (lines 350–351) — even
  a failed deletion is silent — `SUMMARY name=… restore=PASS|FAIL spots=…` prints, and the process **exits 0 on
  FAIL exactly as on PASS**.
- **Operator sees:** nothing alarming. A FAIL run's log tail looks like a PASS run's log tail unless someone reads
  the `restore=` field.
- **Safe to retry:** nothing about this phase is retryable from the script — the staging evidence is deleted, so
  re-testing means a full re-run from F1.
- **Never:** pipeline this script's exit code into any "snapshot succeeded" bookkeeping. As written, it lies by
  omission on exactly the outcome it exists to certify.

### The never-silently-overwrite register

| # | What | Where in code | What is lost / risked |
|---|---|---|---|
| N1 | `.staging-snapshot-3/` parent, incl. the 01:37 leftover `swarm/MAXIMPL.md` | lines 196–197, at startup | The sole debris of a registered FAILED attempt; destroyed with zero log lines (A1) |
| N2 | Pre-existing `monolith-snapshots/<NAME>` (A) and its B twin | lines 274–275, before any copy; re-deleted per retry, lines 280–281 | A good snapshot destroyed *before* its replacement exists; a failed write then leaves neither (A14) |
| N3 | `restore-test/<NAME>` scratch | lines 302–303 | A previous scratch/evidence tree, deleted unnamed |
| N4 | `SNAPSHOT.md` inside A and B at stamp time | lines 347–348, truncate-in-place | Truncation or A/B divergence if the write dies mid-stamp; silent no-op if the PENDING needle mismatches (A15) |
| N5 | Partial A/B destinations on retry | lines 280–281 | Intended for this run's own debris — but the code cannot distinguish its own partial from N2's pre-existing snapshot; A14 draws that line |

### Retry-safety summary

| Death point | Exit | Debris left | Retry posture |
|---|---|---|---|
| F0 memory gate | 2 | none | Retry freely in a healthy window |
| F1 staging wipe | 1 (if it dies) | half-wiped staging parent | The destruction is the event; nothing to retry |
| F2 staging ×12 | 2 | partial staging (doomed at next launch) | Register FAILED; new window only |
| F3/F5 manifest / staging SNAPSHOT.md write | 1 | complete staging, uncertified | Full relaunch only — staging is wiped first |
| F4 stage-verify | 2 | full staging — best evidence | Diagnose *before* any relaunch |
| F6 A write ×12 | 2 | partial A; staging intact | Relaunch in healthy window; beware N2 |
| F7 B write ×12 | 2 | unverified A (PENDING) + partial B | Verify A by hand before trusting or copying it |
| F8 verify A/B | 2 | all trees intact — last full-evidence moment | Diagnose here first |
| F9 restore copy | 2 | A+B verified, stamped PENDING; partial scratch | Planner §8 hand procedure, or full relaunch |
| F10 spot/verdict | 0 or 1 | scratch intact until F11 | Data provable independently; stamp is not repairable by hand |
| F11 stamp/cleanup | 0 even on FAIL | staging + scratch deleted | Nothing retryable; the verdict is permanent |

---

## 3. The worst failure mode found

**The false-FAIL enshrinement at F10+F11, compounded by D1.** Run unamended on a healthy window, the script would
in all likelihood copy every byte perfectly — and then: the manifest would silently contain no studio record at all
(D1, §1a staging an absent tree as 0 files); the restore verify would report `fail=0`; the hardcoded spot-checks
would report two `MISSING`s for files deleted with the swarm tree; `restore_pass` would compute **False**; and the
script would stamp `RESTORE-TEST FAIL` into the `SNAPSHOT.md` of **both** permanent archive copies, delete the
staging tree and the restore scratch — the only evidence that could re-prove the data — print a normal-looking
`SUMMARY`, and **exit 0**. The archive would thereafter state, in its own hand, that a perfect snapshot failed its
restore test, that it was taken before a merge that has already happened, at a database version two behind reality —
and no exit code, watcher, or future reader skimming stamps would ever know any of it was the script lying, not
the data failing. A snapshot system whose worst failure is *manufacturing false history and then deleting the
evidence* is worse than one that merely fails to copy: F2's honest `FAIL-STOP` costs an evening; F11's quiet
verdict costs the archive's credibility permanently. A12 + A15 + A16 together dismantle it; none of the three is
optional.

Runner-up, for the record: **N1/F1** — the unconditional, unlogged `rmtree` of the staging parent at startup, which
will destroy the 01:37 attempt's leftover (`swarm/MAXIMPL.md`) the moment anyone launches the script for any reason,
including a mistaken one. A1 cures it, and it is the cheapest amendment on the list.

---

*Nothing amended, nothing run, nothing large copied. All line numbers verified against the on-disk
`run_snapshot3.py` (358 lines) and all disk-state claims re-checked read-only on Fri 2026-10-30 ~03:20 IST.*

— FINISHER-4
