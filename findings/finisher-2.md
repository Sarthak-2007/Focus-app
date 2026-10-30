# Issue #17 — Snapshot #3: THE PAYLOAD — Manifest of Intent (FINISHER-2, Anker cell)

Verified read-only: Fri 2026-10-30, ~03:18–03:25 IST. Source script: `~/workspace/preservation/run_snapshot3.py`. Source root for everything below: `SRC = ~/workspace/clone-inspect`.
**Preparation only — nothing was copied, nothing was run.** Every count and size in this document was measured on disk today; where it disagrees with the planner's figures (~03:05–03:15 IST), today wins and the drift is flagged.

---

## ⚠ READ THIS FIRST — the five traps a tired operator will hit

1. **The script can succeed while capturing nothing for §1.** `design-refs/swarm/` is **deleted**. The script walks a missing directory, gets zero files, prints no error. A green run ≠ a complete snapshot. See Decision **P-1**.
2. **`builds/` has 23 entries. The script captures 21.** The other two — `cells/` and `wt-ci/` — are skipped *by rule shape*, not by error, and no log line says so. See Decisions **P-2 / P-3**.
3. **`dawn` and `look-dawn` are two different lanes.** Both exist, both qualify, both are captured. Do not deduplicate them.
4. **Today the lane prune list saves nothing.** The script prunes directories named `guh`, `build`, `.gradle`, `tmp` inside lanes — verified: **zero** directories with those names exist in any qualifying lane today. The prune is a safeguard, not a saving. The real size lever is the lane `.git` decision (**P-4**): 193.9 MB.
5. **If nothing is added or pruned, the snapshot contains exactly 20 symlinks** — all of them probe fixtures in one place: `lanes/merge-batch9/guh-test/grund-probe/sl_0 … sl_19`. There are no other symlinks in the captured payload. See Decision **P-5**.

---

## 1. CAPTURED — every root the script takes, and what it is for

Destination layout inside the snapshot (unchanged from #1/#2): `swarm/  trunk/  lanes/  records/  apks/` + `MANIFEST.sha256` + `SNAPSHOT.md`.

### 1a — `swarm/` — the studio record (mail, wall, plans, methods)

| Source | Destination | Purpose (one line) |
|---|---|---|
| `SRC/design-refs/swarm/` (whole tree) | `swarm/` | The studio's own record — how every lane's work was briefed, argued, and ruled before GitHub. |

**Status today: SOURCE DELETED** (Oct 30 ~03:00 cleanup; collaboration moved to the GitHub repo `Sarthak-2007/Focus-app`, records migrated into the repo at `studio/plan/` + `studio/methods/`, commit `8438f5a`). As written, this section stages **0 files, silently**. → **Decision P-1.**

### 1b — `trunk/` — the main app source (allowlist copy: source only, never the whole folder)

| Source | Destination | Purpose (one line) |
|---|---|---|
| `SRC/Focus-app/app/src/` (2.3M) | `trunk/app/src/` | The app's actual source code — the thing every lane branched from. |
| `SRC/Focus-app/app/build.gradle.kts` | `trunk/app/build.gradle.kts` | App-module build definition — required to rebuild the trunk. |
| `SRC/Focus-app/build.gradle.kts` | `trunk/build.gradle.kts` | Root build definition — required to rebuild the trunk. |
| `SRC/Focus-app/settings.gradle.kts` | `trunk/settings.gradle.kts` | Project/module map — required to rebuild the trunk. |
| `SRC/Focus-app/gradle.properties` | `trunk/gradle.properties` | Build tuning (incl. the fixed `-Xmx2g / workers.max=2` memory regime). |
| `SRC/Focus-app/metadata.json` | `trunk/metadata.json` | Project metadata carried by the AI Studio origin of the tree. |
| `SRC/Focus-app/debug.keystore` | `trunk/debug.keystore` | Debug signing key — without it, restored debug builds do not match the installed app's signature. |
| `SRC/Focus-app/gradle/` (12K) | `trunk/gradle/` | Gradle wrapper configuration — pins the build toolchain version. |
| `SRC/Focus-app/public/` (9.0M) | `trunk/public/` | Static/public assets shipped with the app. |

All 9 sources verified present today. Anything else inside `Focus-app/` is **not** captured — see §2, rows E-3/E-4.

### 1c — `lanes/` — every lane tree, copied whole (minus the §2 prunes)

**Capture rule, exactly as coded:** a directory `SRC/builds/<name>/` is captured if and only if `SRC/builds/<name>/Focus-app` is a directory. Copied whole-lane to `lanes/<name>/`.

**Verified today: `builds/` = 23 entries → 21 qualify, 2 do not (`cells`, `wt-ci` — §3).**
**21 qualifying lanes, measured today: 497.1 MB raw total (incl. 193.9 MB of `.git`, §P-4), 5,711 regular files + 20 symlinks.**

| # | Lane | Size (raw) | `.git` inside | Purpose (one line) |
|---|---|---|---|---|
| 1 | `dawn` | 26M | 8.9M | Dawn look, flat re-seat (the version seated after the coordinator seating was refused). |
| 2 | `flow-clock` | 23M | 8.9M | Flow Team's clock work — the flow/timer surface lane. |
| 3 | `look-blueprint` | 23M | 8.9M | Blueprint look — one of the seven parallel look designs. |
| 4 | `look-dawn` | 28M | 8.9M | Dawn look, coordinator-crew version — distinct from lane 1, capture both. |
| 5 | `look-noir` | 24M | 8.9M | Noir look — one of the seven parallel look designs. |
| 6 | `look-signal` | 23M | 8.9M | Signal look — one of the seven parallel look designs. |
| 7 | `look-temple` | 23M | 8.9M | Temple look — one of the seven parallel look designs. |
| 8 | `merge-batch9` | 31M | 17M | The composed batch-9 merge tree (trunk + Truth Car 3, DB v12) — source of the delivered batch-9 build; also holds the `guh-test` probe tree (P-5) and the worktree registry `wt-ci` points into. |
| 9 | `nofire` | 23M | 8.9M | No-fire fix lane (F1–F5) — the "can't check blocking" class of fixes. |
| 10 | `obsidian` | 28M | 8.9M | Obsidian look, flat re-seat (coordinator seating was refused). |
| 11 | `onboarding` | 23M | 8.9M | Onboarding / first-run setup lane. |
| 12 | `pill` | 23M | 8.9M | The pill — the Pastille lane's piece. |
| 13 | `routine-edit` | 26M | 8.9M | Routine-editing lane — persisted, editable routine windows. |
| 14 | `truth-3-breaks` | 23M | 8.9M | Truth Car 3, component: breaks. |
| 15 | `truth-3-intercept` | 23M | 8.9M | Truth Car 3, component: intercept. |
| 16 | `truth-3-proofbase` | 23M | 8.9M | Truth Car 3, component: proof base. |
| 17 | `truth-3-shorts` | 23M | 8.9M | Truth Car 3, component: Shorts & Reels handling. |
| 18 | `truth-3-sound` | 23M | 8.9M | Truth Car 3, component: sound. |
| 19 | `truth-3-sweep` | 23M | 8.9M | Truth Car 3, component: sweep. |
| 20 | `truth-3-times` | 23M | 8.9M | Truth Car 3, component: times. |
| 21 | `warrant` | 23M | 8.9M | Warrant-stamp lane — the residency/a11y stamp whose absence causes "CAN'T CHECK BLOCKING — TAP TO RETRY". |

Lane purposes are stated at the level the lane names and the planner's record support; no lane's internal diff was audited for this manifest.

### 1d — `records/` — the paper trail at the clone-inspect root

Fixed files (all 6 verified present today):

| Source | Purpose (one line) |
|---|---|
| `SRC/BUILD-CHANGES.md` (82,190 B) | What was changed in each build, in order. |
| `SRC/DEAD-CODE-AUDIT.md` (7,500 B) | The audit of dead code and what was ruled on it. |
| `SRC/REMOVAL-LOG.md` (11,834 B) | Name-by-name record of what was removed and where copies live. |
| `SRC/VERDICT.md` (7,519 B) | Standing verdicts the studio works under. |
| `SRC/env.sh` (578 B) | Shell environment for the build lanes. |
| `SRC/env21.sh` (297 B) | Shell environment variant (JDK 21 line). |

Glob files (deduped): patterns `monolith-brief-*.md`, `monolith-correction-brief.md`, `build*.log`, `test-*.log`, `merge-*.log`, `help*.log`, `gate-build.log`, `gate-build.sh`, `hb2-fix-verify.log`, `hb2-fix-verify.sh`.

**Verified today: only 3 glob files still exist** — `monolith-brief-v2-one-shot.md`, `monolith-brief-v3-not-a-clone.md`, `monolith-correction-brief.md` (the commissioning briefs for the app). **Every `*.log` / `gate-build.*` / `hb2-fix-verify.*` pattern matches nothing** — the root logs were removed in the cleanup. An empty glob is not an error.
**So `records/` in Snapshot #3 = 9 files total** (6 fixed + 3 briefs), down from 117 in #2. That drop must be explained in `SNAPSHOT.md`, or a future reader will read it as data loss during the copy. → noted as part of Decision **P-6** (wording, no payload choice).

### 1e — `apks/` — the shipped binaries

| Source | Purpose (one line) |
|---|---|
| `SRC/monolith-batch8-truth-fixed.apk` (11M, Oct 29 16:39) | Batch-8 truth-fixed build — the pre-batch-9 shipped binary. |
| `SRC/monolith-batch9.apk` (11M, Oct 29 21:02) | Batch-9 build — the current delivered binary (DB v12). |

Verified: exactly these 2 match `SRC/monolith-*.apk` today (~22M total). Batches 1–7 APKs are not missing — they already live in Snapshots #1/#2.

**Payload size if run exactly as written:** ≈ 530 MB (lanes 497.1M + trunk ≈ 11.3M + records ≈ 0.11M + apks ≈ 22M + swarm 0).

---

## 2. EXCLUDED — everything deliberately left out, and why

Reason tags: **[REGENERABLE]** a build/toolchain recreates it · **[ON GITHUB]** the same content lives in the `Sarthak-2007/Focus-app` repo · **[PROBE FIXTURE]** test scaffolding, not work product · **[SCRATCH]** working scratch, never a deliverable · **[MACHINE-LOCAL]** true only on this box.

| # | Excluded item | Size today | Why it stays out |
|---|---|---|---|
| E-1 | Lane-internal dirs named `guh`, `build`, `.gradle`, `tmp` (script prune, any depth inside a lane) | **0 dirs exist today** | [REGENERABLE] Gradle user homes, build outputs, Gradle state, scratch. The prune is currently a no-op safeguard — do not expect a saving from it. |
| E-2 | `SRC/trunk-guh/` | 2.0G | [REGENERABLE] The trunk's Gradle user home; a build recreates it. Never in #1/#2 scope. |
| E-3 | Trunk build/tool state inside `SRC/Focus-app/`: `build/` (11M), `.gradle/` (8.6M), `.kotlin/` (0), `.aistudio/` (2.6M) | ≈22M | [REGENERABLE] Never enters because §1b is an allowlist (source + named files only), not a folder copy. |
| E-4 | Trunk non-allowlisted files: `.git/` (10M) [ON GITHUB], `.github/workflows/rescue-build.yml` [ON GITHUB], `.gitignore`, `.env.example`, `shots-redirect.txt` [ON GITHUB / in every clone], `local.properties` (contains `sdk.dir=/home/hatch/workspace/android-sdk`) [MACHINE-LOCAL] | ≈10M | Excluded *implicitly* by the §1b allowlist — correct in every case, but implicit: nobody reading the script's output would know these were considered. This row is the record that they were. |
| E-5 | `SRC/tools/` | 3.3G | [REGENERABLE] The local toolchain (Gradle/Android tooling); re-downloadable, identical for everyone. Never in scope. |
| E-6 | `SRC/shots/` | 14M | [SCRATCH] Proof/QA screenshots from build sessions; never in #1/#2. (If the batch-9 proof shots specifically are wanted, that is a `records/` addition — Decision **P-6**, do not silently add.) |
| E-7 | `SRC/fonts-dl/` (500K), `SRC/fremd-view/` (724K) | ≈1.2M | [SCRATCH] Font-download scratch and a foreign-view inspection copy; never in #1/#2. |
| E-8 | Snapshot infrastructure itself: everything under `~/workspace/preservation/` (runners, staging, prior snapshots) and Location B under `~/workspace/your_files/monolith-archive/` | — | Out of scope by definition: the snapshot preserves `clone-inspect`, not its own machinery. Prior snapshots are already preserved by being snapshots. |

**Not in this table, on purpose:** lane `.git` dirs, `merge-batch9/guh-test/`, `builds/cells/`, `builds/wt-ci/`, and the `design-refs/` flat files are **not** settled exclusions — the script's current behaviour on each is an accident of shape, not a decision. They are §3.

---

## 3. OPEN DECISIONS — not decided here; each needs Ren's explicit ruling before the run

> FINISHER-2 recommends. FINISHER-2 does not decide. Until a ruling lands, the operator must treat the script's current behaviour (right-hand column of each header) as **unratified**, and `SNAPSHOT.md` must state whichever choice was taken — silence is the only unacceptable outcome (planner D1/D4).

### P-1 — The deleted swarm tree: where does the studio record come from now?

- **The situation:** §1a's source, `SRC/design-refs/swarm/`, was deleted Oct 30 ~03:00. The studio's records now live in the GitHub repo's `studio/` folder (`studio/plan/` + `studio/methods/`, migrated at commit `8438f5a`). Locally, that tree exists **only inside repo clones**: verified in `SRC/builds/wt-ci/studio/` (12M, **431 files**: `plan/` 362 files recursively, `methods/` 69) and inside most `builds/cells/issue-*/studio/` copies. It does **not** exist in trunk `SRC/Focus-app/`. Run as-is, the snapshot's first payload section is empty and nothing complains.
- **Choice A:** Repoint §1a at `SRC/builds/wt-ci/studio/` → destination `swarm/` (keep the #1/#2 layout name). Cost: couples the snapshot to the CI worktree surviving until run minute; `wt-ci`'s copy is one clone's checkout, taken as it stands.
- **Choice B:** Drop the swarm section entirely; state in `SNAPSHOT.md`: "studio record lives in git at `studio/`, commit …" and record the commit hash at copy minute. Layout diverges from #1/#2 (four payload dirs, not five).
- **Choice C:** Repoint §1a at `SRC/builds/wt-ci/studio/` → destination `studio/` (name it what it now is), and note the rename in `SNAPSHOT.md`.
- **Script today:** captures nothing, says nothing.
- **★ Recommendation: Choice C, falling back to A if layout-comparability with #1/#2 is judged more important than naming honesty.** The studio record is the single most irreplaceable thing in the payload — code can be rebuilt, the reasoning cannot — so "exclude it because git has it" (B) leaves Snapshot #3 as the first snapshot that cannot explain the work it preserves. C keeps the content and tells the truth about what it is now called. Whichever of A/C: the `studio/` copy carries **3 dangling ghost fixtures** (`studio/methods/queue-sim/*/compile-ghost.flag` → `/nonexistent-target`, by design) — the manifest's hash-of-target-string convention covers them, and the `SNAPSHOT.md` symlink note must say **3**, from this path (see also P-2's multiplication warning).

### P-2 — `builds/cells/` — the GitHub-native issue cells (matches no capture rule)

- **The situation, verified today (drifted since the planner wrote):** `SRC/builds/cells/` = **604M** (planner said 515M — it grew), **19 entries: 18 directories + 1 loose file**.
  Directories: `issue-2, issue-3, issue-4, issue-5, issue-6, issue-7, issue-8, issue-9, issue-9-builder, issue-10, issue-11, issue-12, issue-12-work, issue-13, issue-14, issue-15, issue-16, issue-17`. Loose file: `issue-9-builder-implementation.md` (28K).
  It matches no rule because its children are repo-root-shaped (`app/` at top level), not `Focus-app/`-shaped. Three shape anomalies an operator must not "tidy up" by assumption: `issue-12-work/` holds **findings markdown only, no repo**; `issue-16/` holds its repo **nested at `issue-16/repo/`** plus loose markdown beside it; `issue-17/` is **this cell** — if cells are captured, Snapshot #3 contains its own planning documents, which is correct, not a bug.
  Inside: **17 `.git` directories totalling 211.4 MB**, and **42 symlinks** — every one a `studio/methods/queue-sim/*/compile-ghost.flag` dangling fixture (3 per studio copy × 14 copies).
- **Choice A:** Exclude cells entirely; state in `SNAPSHOT.md`: "issue cells preserved as GitHub branches `agent/issue-<n>`; local cell trees not captured."
- **Choice B:** Capture `builds/cells/` whole → `lanes/` sibling destination `cells/`, with `.git` pruned per P-4's ruling (604M → ≈390M). Captures unpushed/local-only state (e.g. `issue-12-work` findings, `issue-9-builder-implementation.md`) that GitHub does **not** hold.
- **Choice C:** Capture cells whole **with** `.git` (full 604M).
- **Script today:** captures nothing, says nothing.
- **★ Recommendation: Choice B.** The GitHub argument (A) is true for pushed branches and false for exactly the parts most worth preserving: findings folders, the nested `issue-16` work, and any cell state not yet pushed at copy minute — the planner's own caveat ("nothing in the snapshot proves that" the branches hold it) stands. B costs ≈390M of a 115G-free disk. If B is taken, the manifest symlink note changes: **+42 ghost fixtures**, and the restore spot-checks must not be pointed inside `cells/` studio copies expecting unique content — they are duplicates of P-1's tree.

### P-3 — `builds/wt-ci/` — the CI worktree (matches no capture rule)

- **The situation, verified today:** `SRC/builds/wt-ci/` = **25M**, repo-root-shaped (hence no rule match). It is the clean CI checkout — the tree that proved the repo builds on GitHub's terms — and, per P-1, currently the most convenient local holder of `studio/`.
  **Operator warning:** `wt-ci/.git` is **not a directory**. It is a 95-byte text **pointer file**: `gitdir: …/builds/merge-batch9/Focus-app/.git/worktrees/wt-ci`. Capturing `wt-ci` while pruning lane `.git` (P-4) leaves that pointer dangling — harmless as a preserved file, meaningless as a repo. Do not let a restorer mistake a captured `wt-ci` for a working clone.
- **Choice A:** Exclude `wt-ci` as a tree; if P-1 takes Choice A/C, its `studio/` sub-tree is still captured under that ruling. State the exclusion in `SNAPSHOT.md`: "CI worktree excluded — [ON GITHUB] a clean checkout of the repo; its only unique content, `studio/`, is captured under P-1."
- **Choice B:** Capture `wt-ci` whole → `wt-ci/` (25M), pointer file and all.
- **Script today:** captures nothing, says nothing.
- **★ Recommendation: Choice A.** Unlike the cells, `wt-ci` holds nothing local-only by design — it is a checkout, and checkouts are [REGENERABLE]/[ON GITHUB]. Its one load-bearing sub-tree is already the subject of P-1. Capturing it whole (B) mostly duplicates a cells/trunk-shaped tree and imports the dangling-pointer confusion for 25M of no new information.

### P-4 — Lane `.git` directories — the script does NOT prune them

- **The situation, verified today:** every qualifying lane holds `Focus-app/.git`: **21 directories, 193.9 MB total** (20 lanes × 8.9M; `merge-batch9` × 17M). That is **39% of the entire lane payload**. The script as written copies all of it. Each lane `.git` is a clone of the same repo, `Sarthak-2007/Focus-app` — the shared history is [ON GITHUB]; a lane's uniqueness lives in its working tree. The same question applies to cells' `.git` (211.4 MB) if P-2 takes B/C, and it is already answered for trunk (E-4: trunk `.git` never enters, by allowlist).
- **Choice A:** Prune `.git` inside lanes (add `.git` to the script's prune set). Payload ≈ 530M → ≈ 336M; copy time — the resource the flickering write layer punishes — falls by the same proportion.
- **Choice B:** Keep lane `.git` as written. Preserves any lane-local commit, stash, or branch state exactly as it stands.
- **Script today:** Choice B, by omission.
- **★ Recommendation: Choice A — conditional, and the condition is a check, not a hope.** Before the run, Ren confirms per lane that no lane holds an **unpushed commit or stash** (e.g. `git -C <lane>/Focus-app status` / `log @{u}..HEAD` / `stash list` across the 21 lanes — read-only, minutes of work). If every lane is clean: prune, and write "lane `.git` excluded — duplicated on GitHub, lanes verified clean of unpushed state at <time>" into `SNAPSHOT.md`. **If even one lane has unpushed state: keep `.git` for that lane (or push first), and name the exception.** An unconditional prune is the one version of this decision that can silently destroy the only copy of real work.

### P-5 — `merge-batch9/guh-test/` — the probe-fixture tree that escapes the prune

- **The situation, verified today:** `SRC/builds/merge-batch9/guh-test/` = **80K**, a Gradle-probe fixture tree (`native/…/libnative-platform.so`, `grund-probe/*.bin`). It escapes E-1's prune only because it is named `guh-test`, not `guh`. It holds **all 20 of the payload's symlinks** (`grund-probe/sl_0 … sl_19`). It is [PROBE FIXTURE] — scaffolding from diagnosing the write wall, not app work.
- **Choice A:** Prune it (add `guh-test` to the prune set, or prune the `merge-batch9/guh-test` path explicitly). Side effect, stated plainly: the Snapshot #3 payload then contains **zero symlinks**, and the `SNAPSHOT.md` manifest note about link hashing becomes a convention statement with no instances from the lanes.
- **Choice B:** Keep it. 80K, harmless, and it preserves the probe evidence exactly as the diagnosis left it.
- **Script today:** Choice B, by naming accident.
- **★ Recommendation: Choice A.** A fixture that survives only because of a one-word naming miss is an accident, not a preservation policy; the write-wall diagnosis it served is recorded in the studio record (P-1) and in `AGENTS.md`. If Ren prefers the evidence kept, B is defensible — but it must be *chosen*, and the `SNAPSHOT.md` symlink note must then say "20 symlinks, all probe fixtures in `lanes/merge-batch9/guh-test/`", replacing the script's current stale claim about swarm ghost fixtures.

### P-6 — Two smaller items that need a word, not silence (no payload risk either way)

- **`design-refs/` flat files — verified 33 files, 3.1M** (correcting the planner's "20 images + 3 md" undercount): 20 numbered reference images (`01-…`–`20-…jpg`), 3 `view-*.jpg`, 7 `viewcrit-*.jpg`, and 3 documents (`INDEX.md`, `CRITIQUE-deck-shield.md`, `PLAN-vault-guard.md`). The script has never captured them (it only ever took `design-refs/swarm`), so if P-1 is resolved without them, Snapshot #3 contains **zero** design-refs while #1/#2's era is unrecoverable on this point. **Choice:** add as a new top-level `design-refs/` (and note the layout addition in `SNAPSHOT.md`) — or exclude explicitly as "reference imagery, re-findable; critique/planning documents live in git-era records". **★ Recommendation: add them.** 3.1M is nothing; `CRITIQUE-deck-shield.md` and `PLAN-vault-guard.md` are studio work product, not re-findable imagery, and a snapshot whose design rationale is missing fails the same test as P-1.
- **`records/` shrinkage + `shots/`:** `SNAPSHOT.md` must carry one sentence explaining `records/` = 9 files (root build logs were deleted in the Oct 30 cleanup — §1d), and one sentence stating `shots/` was excluded per E-6 — or, if Ren wants the batch-9 proof shots kept, they are added to `records/` by explicit ruling. **★ Recommendation: explain, don't add** — the shots are session QA, already superseded by the delivered APK; the explanation sentence is what a future reader actually needs.

---

## 4. Decision ledger — fill this in at run time, copy it into `SNAPSHOT.md`

| Decision | Question in one line | Script today | FINISHER-2 recommends | Ren ruled |
|---|---|---|---|---|
| P-1 | Studio record: repoint to `wt-ci/studio/`, drop with a git note, or repoint as `studio/`? | Captures nothing, silently | **C** (repoint as `studio/`), fallback A | ☐ |
| P-2 | `builds/cells/` (604M, 18 dirs + 1 file): exclude-on-GitHub, capture without `.git`, or capture whole? | Captures nothing, silently | **B** (capture, `.git` per P-4) | ☐ |
| P-3 | `builds/wt-ci/` (25M): exclude (studio via P-1) or capture whole? | Captures nothing, silently | **A** (exclude; pointer-file warning stands) | ☐ |
| P-4 | Lane `.git` (193.9M, 39% of lanes): prune or keep? | Keeps, by omission | **A, conditional** on a per-lane unpushed-state check | ☐ |
| P-5 | `merge-batch9/guh-test/` (80K, all 20 payload symlinks): prune or keep? | Keeps, by naming accident | **A** (prune) | ☐ |
| P-6 | `design-refs/` flat files (33 files, 3.1M): add or explicitly exclude? + `records/`/`shots/` wording | Never captured; wording absent | **Add** the files; **explain** records/shots | ☐ |

**Payload under the recommended rulings (C + B + A + A + A + add):** ≈ 530M − 194M (lane `.git`) − 0.08M (`guh-test`) + ≈ 390M (cells without `.git`) + 12M (`studio/`, inside the cells/wt-ci figures if counted once — operator: do not double-count; `studio/` under P-1 and the studio copies inside cells are duplicates of the same tree) + 3.1M (design-refs) ≈ **730M, of which a large share is deliberate duplication of `studio/` across cell trees**. If that duplication is judged waste, P-2 can carve out `cells/*/studio/` — FINISHER-2 flags the option and does **not** recommend it: partial-tree carve-outs are exactly how silent gaps are born.

---

*Signed — **FINISHER-2** (Anker cell, Issue #17). Preparation only: no snapshot was run, no payload was copied; all figures are from read-only directory listings, `du`, and `find` counts taken Fri 2026-10-30 ~03:18–03:25 IST. The payload above names what exists today, not what existed when the script was written.*
