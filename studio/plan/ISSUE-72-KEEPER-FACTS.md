# ISSUE #72 — KEEPER FACT SHEET (fresh census for PRISMA's RELEASE-ISSUE-MAP)

**Keeper seat:** KEEPER OF RECORD, Wave-3 cluster C10 · **Repo:** Sarthak-2007/Focus-app · **Issue:** #72 (release-issue-map)
**Census window:** 2026-10-29T23:57:24Z → 2026-10-30T00:42:38Z (**2026-10-30 05:27:24 → 06:12:38 IST**)
**Core snapshots:** git ls-remote #1 ≈00:00Z · issues/PRs/releases/main-runs JSON 00:16–00:19Z · git ls-remote #2 + fetch ≈00:31–00:36Z · run-job views 00:31–00:36Z
**Base clone (read-only):** `~/workspace/clone-inspect/builds/cells/w3c10-base` (origin = https://github.com/Sarthak-2007/Focus-app.git)
**Raw provenance files:** `~/workspace/clone-inspect/w3c10-reports/keeper-raw/` (01-lsremote.txt, 04-main-runs.json, 05-batch10-runs.json, 07-issues.json, 08-prs.json, 09-releases.txt, 10-batch9.json, 13-lsremote2.txt, main-run-*.json, batch10-run-*.json)
**Method:** `gh` CLI (logged in as Sarthak-2007) + `git ls-remote`/`git fetch` read-only. **No writes to GitHub.**

> ⚠️ **STALE WITHIN THE HOUR — read this first.** This programme moved *during* the census, repeatedly: main advanced e29acdf → 73aaeae (PR #165) → 066e59b (PR #167) → 2d2683b (PR #168) → 92e18e7 → 68a3713 → 0a00e95 inside the window, PR #170 merged at 00:10:34Z, PR #169 sat open, batch-10 advanced d08a3f3 → 9c18a37 → c61859bc, agent/issue-71 advanced 62cb972 → 99188d6, and **PR #171 had already merged by the time the fetched main log was read (~00:36Z) — its merge SHA was NOT measured.** Every number below carries its snapshot time; any number without one is not in this sheet. Republish, don't patch.

---

## 1. Main tip + last commits

**Main tip at final measurement (`git ls-remote origin main`, ls-remote #2, ≈00:31Z): `0a00e959c34390b5b3d37b6a242690356bfeffba`**
(First ls-remote ≈00:00Z had main at `2d2683b5ac086963cb0d5bf7152121d015639a0d`.)

Last commits on fetched `origin/main` @ `0a00e95` (`git log` after fetch, ≈00:36Z):

| SHA | Time | Subject |
|---|---|---|
| `0a00e959c34390b5b3d37b6a242690356bfeffba` | 2026-10-30T00:13:38Z (run created) | "SIGNING FIX (all lanes): replace wrong fresh debug.keystore (FE:08:0C…" — displayTitle of run 40978334853 |
| `68a3713c87289f650144075fb113696e3118b8d0` | 2026-10-30T00:10:37Z (run created) | Merge pull request #170 from Sarthak-2007/agent/issue-62 — displayTitle of run 40978007987 (full SHA corrected from the run list; an earlier draft of this table carried a garbled SHA for this commit) |
| `92e18e77f87c47246e67c80af78f2e5b47a8d1d3` | 2026-10-30T00:09:05Z (run created) | "Merge agent/issue-35 — Wave-3 #35 app-rules: total 4-bucket classific…" — displayTitle of run 40977839698 |
| `2d2683b5ac086963cb0d5bf7152121d015639a0d` | 2026-10-30T05:21:02+05:30 | Merge pull request #168 from Sarthak-2007/agent/issue-61 |
| `066e59b88499a7d1d80ce5dd47575a87e0713e76` | 2026-10-30T05:10:19+05:30 | Merge pull request #167 from Sarthak-2007/agent/design-revamp |
| `73aaeaef0e197187e0c0411bce42a6a8f0e43bec` | 2026-10-30T04:58:31+05:30 | Merge pull request #165 from Sarthak-2007/agent/issue-2 |

## 2. Main CI — last 6 runs (`gh run list --branch main --limit 6`, 00:17Z; jobs via `gh run view <id> --json jobs`)

| Run ID | Head | Created (Z) | Status | Assemble | Artifact upload | Unit tests |
|---|---|---|---|---|---|---|
| 40978334853 | `0a00e95` | 00:13:38 | **in_progress** at census | — not measured — | — | — |
| 40978007987 | `68a3713` | 00:10:37 | **in_progress** at census | — not measured — | — | — |
| 40977839698 | `92e18e7` | 00:09:05 | **in_progress** at census | — not measured — | — | — |
| 40975880219 | `2d2683b` | 23:51:05 | completed / **failure** | **success** | **success** | **failure (executed)** |
| 40974717269 | `066e59b` | 23:40:22 | completed / **failure** | **success** | **success** | **failure (executed)** |
| 40973388591 | `73aaeae` | 23:28:34 | completed / **failure** | **success** | **success** | **failure (executed)** |

- Concluded-run step detail (all three identical shape, from `main-run-<id>.json`): checkout ✓, setup-java ✓, Install SDK platform 36.1 ✓, Generate debug keystore ✓, Assemble debug ✓, upload-artifact ✓, **Unit tests ✗**, Verdict summary ✓.
- **Green main runs in this window: 0 of 3 concluded.** The 3 in-progress runs are reported as in-progress, never guessed; by file close (~00:42Z) at least the PR #171 merge implies further runs exist that were not measured.

## 3. Issue census (`gh issue list --state all --limit 250 --json number,state,closedAt,title`, 00:16Z; file 07-issues.json)

- **Repo-wide: 165 issues — 151 OPEN / 14 CLOSED.** Highest issue number: **166** (numbers 165/167–170 in this range are PRs, §4). ⚠️ STALE WITHIN THE HOUR: closures were landing during the window (#5 closed 23:47:10Z, after the bundle's census).
- **All closed issue numbers:** 2, 3, 5, 12, 13, 19, 20, 26, 35, 59, 61, 62, 66, 71.

**#1–#22 individually: 15 OPEN / 7 CLOSED**

| # | State | closedAt (Z) | # | State | closedAt (Z) |
|---|---|---|---|---|---|
| 1 | OPEN | — | 12 | CLOSED | 2026-10-29T23:17:16Z |
| 2 | CLOSED | 2026-10-29T23:23:40Z | 13 | CLOSED | 2026-10-29T23:17:33Z |
| 3 | CLOSED | 2026-10-29T22:56:22Z | 14 | OPEN | — |
| 4 | OPEN | — | 15 | OPEN | — |
| 5 | **CLOSED** | **2026-10-29T23:47:10Z** (new since bundle) | 16 | OPEN | — |
| 6 | OPEN | — | 17 | OPEN | — |
| 7 | OPEN | — | 18 | OPEN | — |
| 8 | OPEN | — | 19 | CLOSED | 2026-10-29T23:19:15Z |
| 9 | OPEN | — | 20 | CLOSED | 2026-10-29T23:19:23Z |
| 10 | OPEN | — | 21 | OPEN | — |
| 11 | OPEN | — | 22 | OPEN | — |

**#68–#72:** #68 OPEN · #69 OPEN · #70 OPEN · **#71 CLOSED 2026-10-29T23:34:14Z** · **#72 OPEN** (this issue).
**Audit issues #23–#164: 142 filed, 7 CLOSED** — closed: **26, 35, 59, 61, 62, 66, 71** (135 open). Supersedes the bundle's "exactly 1 closed (#26)".
**Also open:** #166 — "CI verdict follow-up: 37 real test failures now that the suite runs (258 tests, …)" — the runtime-failure queue now has its own issue.

## 4. PR census (`gh pr list --state all --limit 100`, 00:18Z; file 08-prs.json)

**5 PRs ever — 4 MERGED / 1 OPEN / 0 closed-unmerged.** (Bundle said 0 PRs ever; superseded.)

| PR | State | Head → base | Merged (Z) | Title (short) |
|---|---|---|---|---|
| #165 | MERGED | agent/issue-2 → main | 2026-10-29T23:28:31Z | Issue #2: test call-sites fixed, suite compiles and runs (258 tests) + CI on JDK 21 |
| #167 | MERGED | agent/design-revamp → main | 2026-10-29T23:40:20Z | Revamp slice 1: the launcher block surface (dial + ALLOWED/PEEK screens) |
| #168 | MERGED | agent/issue-61 → main | 2026-10-29T23:51:02Z | Issue #61: delete dead TowerStreakView, truthful TaskHorizonBarView |
| #169 | **OPEN** | agent/issue-66 → main | — | Issue #66 (audit R1 tests-2): honest test suite … |
| #170 | MERGED | agent/issue-62 → main | 2026-10-30T00:10:34Z | fix(rail-view): issue #62 — all 5 audit findings fixed (Wave-3 C6) |

⚠️ STALE WITHIN THE HOUR: **PR #171 merged after this snapshot** (seen in the fetched main log's merge train, ≈00:36Z) — its number is from that log line; its title/head were not measured. PR total at file close is therefore ≥6, not 5.

## 5. Releases (`gh release list` + `gh release view batch-9 --json …`, 00:16–00:18Z)

- **Exactly 1 release.** Tag `batch-9`, name "Batch 9 — Truth Car 3 (test build, Oct 30)", published 2026-10-29T21:17:50Z, marked **Latest**.
- **isPrerelease: false · isDraft: false** — the misflag from the audit is unchanged.
- Asset: `monolith-batch9.apk`, **10,995,124 B**, **downloadCount 0**, sha256 `793331dcee8c5be196b98eef576febd95ffc5aa97a8cec9eccb950ecf2695122`, state `uploaded` (asset id RA_kwDOVavfyc4pH6Dm).

## 6. Version + release path (on fetched `origin/main` @ 0a00e95, read ≈00:36Z)

- `app/build.gradle.kts:19-20` — **versionCode = 1, versionName = "1.0"** (unchanged; identical on batch-10 @ c61859bc lineage: lines 19-20, same values).
- Release signing block `app/build.gradle.kts:27-34`: `signingConfigs.create("release")` — storeFile = `$KEYSTORE_PATH` env else `${rootDir}/my-upload-key.jks`, storePassword = `$STORE_PASSWORD`, keyAlias `upload`, keyPassword = `$KEY_PASSWORD`; applied to the release build type at :43-51. (Bundle's line range 27-35 ≈ same block.)
- Workflow `.github/workflows/rescue-build.yml` on main: `java-version: '21'` (:21 — **the JDK 21 cure from PR #165 is now on main**, bundle-era main was JDK 17), Generate debug keystore step, **Assemble debug (:60-64, `./gradlew assembleDebug`)**, upload-artifact, **Unit tests (`testDebugUnitTest`, :74)**, verdict summary.
- **`assembleRelease` / `bundleRelease`: NO step anywhere in the workflow** (grep on main and on batch-10 both return only assembleDebug + testDebugUnitTest). No releasable build path exists yet — Finding 2's core still holds.

## 7. batch-10 (`git ls-remote` + fetch + `gh run list --branch batch-10`, snapshots 00:00Z → 00:33Z)

- **Branch tip at final ls-remote (≈00:31Z): `c61859bcfc7f64014dd1550002d47c849638c1ca`** — commit subject (from its CI run title): "SIGNING FIX: committed debug.keystore was a fresh wrong key (FE:08:0C…)". Earlier tips in-window: `d08a3f3` (bundle) → `9c18a3728fefc68a599d6b676675c263de1fecc8` (00:00Z, "Merge remote-tracking branch 'origin/main' into batch-10", 23:38:04Z) → `c61859bc`.
- Divergence at fetch time (@ batch-10 9c18a37 vs main 0a00e95-era): **88 commits on batch-10 not on main; 10 commits on main not on batch-10** (`git rev-list --count`). ⚠️ moving — recount at republish.
- **Latest CI run: 40977857349 @ `c61859bc`, created 2026-10-30T00:09:15Z — concluded FAILURE** (it was in_progress when listed at 00:19Z; job view at ~00:33Z shows completed/failure — both states timestamped, not guessed). Stages: Assemble debug **success**, upload-artifact **success**, **Unit tests failure (executed)**, verdict summary success. **This is a change from the bundle: batch-10 now assembles; it is tests-red, not assemble-red.**
- Previous concluded batch-10 run for contrast: 40974465694 @ `9c18a37`, failure — same stage shape (assemble ✓, artifact ✓, tests ✗). All 6 batch-10 runs in the list: 1 in-progress-at-listing→failure (40977857349) + 5 completed failure (40974465694, 40974188644 @8a47203, 40971463696 @d08a3f3, 40970834444 @461a5eb, 40970075949 @fb5213a). **batch-10 has never had a green run in this list.**
- **DB version:** batch-10 `MonolithDatabase.kt:25` → **version = 13**; main `:25` → **version = 12**.
- **MIGRATION_12_13:** batch-10 — **present in code**, `MonolithDatabase.kt:209` `val MIGRATION_12_13 = object : Migration(12, 13)`, registered in `addMigrations(…, MIGRATION_12_13)` (:267); also referenced in MonolithModels.kt / MonolithRepository.kt on that branch. Main — **zero hits** (`git grep -l MIGRATION_12_13 origin/main` = none). Migration claim: FALSE on batch-10, HOLDS on main — unchanged from the bundle.

## 8. Test status on main NOW (post-PR #165)

**YES — tests execute on main.** The latest concluded main run, **40975880219 @ `2d2683b`** (the PR #168 merge, itself after PR #165 and PR #167), ran the Unit tests step to completion with conclusion **failure** — the step executed; it did not skip and did not die at test compilation. The same holds for both other concluded post-#165 runs (40973388591 @ 73aaeae, the PR #165 merge itself, and 40974717269 @ 066e59b): assemble ✓ / artifact ✓ / tests executed-and-failed.
- What remains red on main is therefore the runtime-failure queue (the bundle's 258-run / 37-failed lineage from agent/issue-2 run 40970308776; now tracked as issue **#166**, OPEN). Exact current failure count on main was **not** measured in this census (no log pull) — do not republish "37" as a main number without a log.
- The three main runs in progress at census (40977839698 @ 92e18e7, 40978007987 @ 68a3713, 40978334853 @ 0a00e95) would each re-confirm this on conclusion; the first of them to conclude is the next proof point. Their conclusions were **not** observed — not guessed.

## 9. C10 cluster branches (`git ls-remote`, both snapshots)

| Branch | Snapshot 1 (≈00:00Z) | Snapshot 2 (≈00:31Z) |
|---|---|---|
| `agent/issue-71` | `62cb972a1621d92ff48122ef689b4ae88cf9dd75` ("merge main (73aaeae, PR #165) into agent/issue-71 before PR — PRISMA") | **`99188d67916eb0cb36a283ed1f3a52f2ecc2b17d`** (moved again in-window) |
| `agent/issue-72` | **does not exist on remote** (no ref returned) | **does not exist on remote** (no ref returned) |

PRISMA's map branch `agent/issue-72` has not been pushed yet at either measurement — the map will be its first commit.

---

## Headline numbers (for the map's §0 header)

1. Main tip: **0a00e95** (final ls-remote ≈00:31Z; PR #171 already merged after — ⚠️ stale)
2. Main CI (last 6): **3 concluded, all failure; 3 in_progress at census; 0 green**
3. Latest concluded main run **40975880219**: assemble ✓ · artifact ✓ · tests executed, failed
4. Issues: **165 total — 151 open / 14 closed**
5. #1–#22: **15 open / 7 closed** (new: #5)
6. Audit #23–#164 closed: **7** (26, 35, 59, 61, 62, 66, 71)
7. PRs ever: **5 at snapshot (4 merged / 1 open)** — ≥6 at file close (PR #171)
8. Releases: **1** (batch-9, prerelease=false, 0 downloads, 10,995,124 B) · version **1 / "1.0"** · batch-10 @ **c61859bc**, DB **v13**, latest run **40977857349 failure at tests (assemble ✓)**

*Keeper of Record, C10 — read-only census, no GitHub writes. Raw files: `keeper-raw/` alongside this sheet.*
