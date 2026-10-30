# ISSUE #72 VERIFY — Audit R1 · release-issue-map (20-seat bundle, for PRISMA / Wave-3 cluster C10)

**Repo:** Sarthak-2007/Focus-app · **Issue:** #72 (OPEN at every snapshot taken)
**Audit ground:** main @ `8438f5a`, ~02:52 IST Oct 30 (`studio/audits/AUDIT-R1.txt`, slice `release-issue-map`, ~line 2558)
**Verification ground:** read-only base `~/workspace/clone-inspect/builds/cells/w3c10-base` = origin/main @ `e29acdf`, plus live `gh` reads (no writes, no commits, no issue edits).
**Verification window:** Fri 2026-10-30 ~04:46 IST (23:16Z) → ~05:00 IST. **The programme moved DURING the window** — five issues in #1–#22 closed live between the first and last census (timestamps below). Both snapshots are reported; neither is silently preferred.

**Verdict counts: HOLDS 1 · PARTIAL 3 · FALSE 1 · OWNED-BY overlay on 1 (Finding 3 is HOLDS-on-main and OWNED-BY #2).** No finding is fully dead: the map's *mechanisms* have been cured in three places, its *headline outcome* — no green, signed, releasable build on main — still holds everywhere.

---

## (a) Per-finding verdict table

| # | Finding (audit, @8438f5a) | Verdict NOW | One-line truth |
|---|---|---|---|
| 1 | [CRITICAL] CI has never built anything: 5/5 main runs failed, keystore missing | **PARTIAL** (mechanism FALSE, green-run outcome HOLDS) | CI now assembles + uploads an APK on main; it still has never gone green (9/9 failure) because unit tests are red. |
| 2 | [CRITICAL] Sole 'release' is a manually-built debug APK mislabelled as a full release; no releasable build | **HOLDS** | Unchanged in every particular: 1 release, `prerelease=false`, debug/test body, 0 downloads, versionCode 1, no `assembleRelease` anywhere. |
| 3 | [HIGH] Issue #2 real and still broken on main; fix unmerged on side branches | **HOLDS on main · OWNED-BY #2** | Main's CI log reproduces all six cited errors verbatim today; the cure is proven on `agent/issue-2` (tests execute: 258 run, 37 failed) and #2 was closed live during verification — but main itself is still broken. |
| 4 | [HIGH] Issues #1–#22 are process theatre: 0 closed | **FALSE** (headline) / PARTIAL (comment-volume sub-claim) | 0 closed → 1 closed at window start (#3) → **6 of #1–#22 closed** by window end, closures landing in real time from the merge train. |
| 5 | [HIGH] Batch-10 (#15) + passes #6–#16 unshippable epics; MIGRATION_12_13 does not exist | **PARTIAL** (migration claim FALSE on `batch-10`, HOLDS on main; unshippable HOLDS) | A composed `MIGRATION_12_13` + DB v13 now exists in code on branch `batch-10` — whose latest CI run fails at *assemble*, and nothing has merged to main. |

### Finding 1 — CI / keystore — PARTIAL

Audit evidence (AUDIT-R1.txt, slice): 5/5 main runs failed (`b792727, b981ca1, 6d2eebe, 817e8c7, 8438f5a` run `40958472117`); run #5 steps: Install SDK ✓, Assemble debug ✗, upload skipped, tests skipped; root cause `:app:validateSigningDebug: Keystore …/debug.keystore not found`; `app/build.gradle.kts:36` demanded `${rootDir}/debug.keystore`; `.gitignore:18` listed it; file absent.

Live re-measure:

- **Keystore blocker is cured twice over on main.** (i) Commit `cfe6ee2` *"CI unblock: commit the standard debug keystore…"* — `git ls-tree -r HEAD` lists `debug.keystore` (2,666 B, tracked: `git ls-files -v` → `H debug.keystore`; `.gitignore:18` still *lists* the name but a tracked file overrides the ignore — worth one line in Prisma's map so nobody re-diagnoses it). (ii) Commit `8d2b077` landed the issue-#3 workflow on main: `.github/workflows/rescue-build.yml` now carries a *"Generate debug keystore (issue #3 fix)"* step (guarded `keytool -genkeypair … -alias androiddebugkey`, workflow file ~lines 44–62), 25-min timeout, and a truthful verdict-summary step. Either cure alone suffices; both are present.
- **The rehearsal run proves the chain.** Run **40967732023** (`agent/issue-3` @ `1111cbb`, https://github.com/Sarthak-2007/Focus-app/actions/runs/40967732023): steps = Generate debug keystore **success**, Assemble debug **success**, upload-artifact **success**, Unit tests **failure**, Verdict summary success. Artifact `monolith-debug` 10,570,765 B. It failed exactly where the brief said: tests *executed*, then failed on the stale call-sites (Finding 3) — the signing wall is gone.
- **Main now builds.** Main run history is now **9 runs, 9 × `failure`** (40956477459 `b792727` … 40970102642 `e29acdf`), but the failure moved: the last four main runs (40968747267 @`cfe6ee2`, 40968930553 @`8d2b077`, 40969279638 @`8ccdce5`, **40970102642 @`e29acdf`**) all show **Assemble debug: success, upload-artifact: success, Unit tests: failure**. Run 40970102642's artifact: `monolith-debug`, 10,570,760 B, not expired.
- **What still holds, exactly:** no main run has ever concluded `success` (0/9 green); the uploaded APK is explicitly flagged UNTESTED by the workflow's own verdict summary while tests are red. "CI has never built anything" is **false**; "CI has never *proven* anything" is still true.

### Finding 2 — The 'release' — HOLDS

- `gh release list`: exactly **1** release. `gh release view batch-9`: tag `batch-9`, name *"Batch 9 — Truth Car 3 (test build, Oct 30)"*, `isPrerelease: false`, `isDraft: false`, published 2026-10-29T21:17:50Z. Asset `monolith-batch9.apk`, 10,995,124 B, **downloadCount 0**, sha256 `793331dcee8c5be196b98eef576febd95ffc5aa97a8cec9eccb950ecf2695122`. Its own body still says *"Debug/test build"* and *"The unit tests do not compile yet"* — the mislabel the audit charged is verbatim intact: a self-described test build carrying the full-release flag.
- **No version movement:** `app/build.gradle.kts:19-20` on main — `versionCode = 1`, `versionName = "1.0"`. Identical on `batch-10`. Never bumped in any tree checked.
- **No releasable build path exists:** release signing (`app/build.gradle.kts:27-35`) requires `${rootDir}/my-upload-key.jks` (or `KEYSTORE_PATH` env) + `STORE_PASSWORD`/`KEY_PASSWORD`; `git ls-tree` shows **no** `.jks` and no `keystore.properties` on main (correctly excluded by the new `.gitignore:21-23` `*.jks` rules from `cfe6ee2` — so the key is absent *by design*, and no CI secret evidence was found in any checked surface). The workflow on main **and** on `batch-10` runs only `./gradlew assembleDebug` + `testDebugUnitTest` — `grep assemble|bundle` returns no `assembleRelease`/`bundleRelease` step anywhere.
- Verdict **HOLDS, 100%**. The only cosmetic softening since the audit: the release *title* honestly contains "(test build, Oct 30)". The flag, the version, the signing gap and the missing release step are all exactly as audited.

### Finding 3 — Issue #2 / main's tests — HOLDS on main · OWNED-BY #2

Production signature (main): `ParkDrawer` at `app/src/main/java/com/example/ui/components/ParkDrawer.kt:127` requires, among others, `shortsReelsSniperEnabled: Boolean` (:133), `socialSitesEnabled: Boolean` (:141), `onToggleSocialSites: () -> Unit` (:142), `onToggleShortsReelsSniper`. The production side is complete; the tests lag it. Main's own latest CI log (run **40970102642**, `--log-failed`) reproduces the audit census *verbatim, same files, same lines*:

- `ScreenshotHarnessTest.kt:350` — No value passed for `socialSitesEnabled` / `onToggleSocialSites` (call at :333, missing pair confirmed by direct read :333-351)
- `ShortsToggleRenderTest.kt:66` — same missing pair (call at :59-71)
- `SoundAndSocialTruthTest.kt:382` — No value passed for `shortsReelsSniperEnabled` / `onToggleShortsReelsSniper` (its `setParkContent` *does* pass the social pair at :388-389 — half-cured file, exactly as #2's body implies)
- `TruthCar3SweepTest.kt:314` — all four parameters missing; **plus a second broken site at :346 the audit did not cite** (same four) — the census is, if anything, understated
- `TruthCar3SweepTest.kt:6-7` — Unresolved reference `assertDoesNotExist` / `assertExists` (imports still present at :6-7 by direct read)
- `TruthCar3CaptureBodyTest.kt:70` — "None of the following candidates is applicable" (the 9-arg `StatusBarNotification` call at :70-74 vs SDK 36's 10-arg constructor)

Result on main: `:app:compileDebugUnitTestKotlin FAILED`, 17 `e:` compiler errors across 5 files / 6 call-sites. **No test has ever run on main.**

The cure, and its ownership:

- `tyler/safe-fixes` (the audit's `e5eec1a`): **does not exist on the remote** — `git ls-remote` returns no `tyler/*` ref. That branch was local-only and is superseded; Prisma's map must not cite it as a live location.
- The live cure is `agent/issue-2`, tip **`f789cef`** ("ci: run CI on JDK 21 — Robolectric 4.16 refuses SDK 36 on JDK 17…"), on top of `8a256a4` (the call-site fixes) + merges of main's keystore/workflow cures. Diff vs main: the same 5 test files (+ the JDK 17→21 workflow line). Its latest run **40970308776**: Assemble **success**, artifact uploaded, **Unit tests executed — 258 tests completed, 37 failed**. So on that lineage the compile wall is *proven gone*; what remains is 37 runtime failures (incl. `ScreenshotHarnessTest`, `DrawerRenderTest`, `SoundAndSocialTruthTest`, `TruthCar3Sweep*` cases) that are real test/behaviour verdicts, not call-site noise — they need triage, and they are the true current blocker under #2's lineage. The same fixes were adopted verbatim by `agent/issue-12` (`bbadf5b`) and `agent/issue-13` (`5984712`), whose addendum runs were in progress at snapshot time (40971845382, 40971860651).
- **State flip during verification:** `gh issue view 2` at window start: **OPEN**. Final census: **CLOSED, closedAt 2026-10-29T23:23:40Z**. The closure rests on branch/batch-10 proof, *not* on a main merge — main @`e29acdf` still fails test compilation today. Prisma's map must say this plainly: #2 is closed as an issue, its cure is **not on main**.

### Finding 4 — #1–#22 "process theatre, 0 closed" — FALSE (headline)

- Audit ground: 22 open / 0 closed, all created in a ~5-min burst 2026-10-29T21:17-21:22Z.
- Window-start census (first `gh issue list`, ~23:13Z): #1–#22 = 21 OPEN / 1 CLOSED — **#3 CLOSED** (closedAt 2026-10-29T22:56:22Z), the keystore/workflow issue, closed on the run-40967732023 evidence above.
- Window-end census (final `gh issue list --state all`, 165 issues): #1–#22 = **16 OPEN / 6 CLOSED** — #2 (23:23:40Z), #3, #12 (23:17:16Z), #13 (23:17:33Z), #19 (23:19:15Z), #20 (23:19:23Z). Repo-wide: **158 OPEN / 7 CLOSED** (the 7th is audit issue #26, manifest-permissions). Closures were landing *while this bundle measured* — that is the opposite of theatre; it is the Konto Phase-2 merge train running.
- What still holds of the sub-claims: comment volume is real (GraphQL comment totals: #3 = 21, #12 = 21, #7 = 17, #5 = 9, #2 = 8–9 by API variance, #15 = 7, #22 = 7; audit-era #3 = 19 → now 21). And four of #1–#22 remain non-work **by design** and still open: #1 charter (1 comment), #18 frozen/record-only (1), #21 parked-until-ruled (0), #22 master index (7). Those four are records, not stalled work — Prisma's map should classify them as such instead of counting them as unclosed labour.
- **0 PRs repo-wide** (`gh pr list --state all` = empty at both snapshots): all integration is happening by direct branch pushes and the `batch-10` merge branch, not pull requests. Any map that waits for PRs will misread the train.

### Finding 5 — Batch-10 / MIGRATION_12_13 — PARTIAL

- **On main: HOLDS.** `MonolithDatabase.kt:25` → `version = 12`; migrations present: `MIGRATION_7_8, 8_9, 9_10, 10_11, 11_12` only (`addMigrations` :242); `grep MIGRATION_12_13` on main = **zero hits in code**.
- **On `agent/issue-15`: plan only.** Tip `e216481` (Edition-3 errata). Still DB v12, no migration code. It carries `studio/plan/BATCH10-MERGE-PLAN.md` (Edition 2) + `BATCH10-MERGE-PLAN-EDITION-3.md`: §1 composes the migration as *text* (8 columns), §3 step 2 is still an unchecked "- [ ] Step 2 — MIGRATION_12_13 composed FIRST", with the rows 7–8 sub-decision (heldShut/heldPeek defaults) explicitly carried as open in the plan. A plan is not a migration.
- **On `batch-10`: the migration EXISTS in code — the audit sentence is now false repo-wide.** Branch `batch-10` did not exist at audit time; tip **`d08a3f3`**, 89 commits not on main (lane merges incl. #2, #3, #5–#17 lineages). `MonolithDatabase.kt` there: `version = 13` (:25), `val MIGRATION_12_13 = object : Migration(12, 13)` (:209) with exactly the plan's 8 `ADD COLUMN`s — `guard_settings.peekMinutes DEFAULT 2`, `breakMinutes DEFAULT 10`, `focus_sessions.breakSeconds DEFAULT 600`, `extensionSecondsAdded DEFAULT 0`, `extensionLog DEFAULT ''`, `withinLabel` (nullable, no default), `heldShutAtSeal DEFAULT -1`, `heldPeekAtSeal DEFAULT -1` — registered in `addMigrations(…, MIGRATION_12_13)`; plus the W-41 birth writer (commit `060c9b2`), pill lane files, EditableTimes rewire, W-08 dynamic PARK plate.
- **But "unshippable" HOLDS today.** `batch-10` CI: 4 runs, all failure; latest **40971463696 @`d08a3f3`**: **Assemble debug: failure**, upload skipped, tests skipped — the composed tree does not currently assemble on CI. Issue #15: **OPEN** (7 comments). Passes: #6, #7, #8, #9, #10, #11, #14, #16, #17 still OPEN (only #12/#13 of the #6–#16 set have closed, and not via a main merge). Nothing from `batch-10` is on main; main's HEAD adds no app code since `fff9edd` (batch 9) — its last five commits are CI + studio/audit records.
- Net: the epic is no longer "stacked on a migration that does not exist"; it is stacked on a migration that exists on an **unmerged, assemble-red** branch. Different disease, same prognosis — until `batch-10` goes assemble-green and lands.

---

## (b) Content spec — Prisma's updated release-issue map document

**Proposed home (Prisma's call, on `agent/issue-72`):** `studio/plan/RELEASE-ISSUE-MAP.md`, header-dated and SHA-stamped, regenerated — never hand-edited — at each milestone. Every number below was produced by a check actually run in this verification (command cited). Prices are effort prices, not money.

### §0 — Provenance header
Repo, main SHA (`e29acdf` at verification), map timestamp, supersedes-line pointing at AUDIT-R1 slice `release-issue-map` (@`8438f5a`). Rule printed at top: *a number without a run id, SHA, or file:line is not a number.*

### §1 — CI status (true numbers)
- Workflow: `.github/workflows/rescue-build.yml` (main) — checkout → JDK **17** (note: `agent/issue-2` already carries the JDK **21** fix `f789cef` for Robolectric 4.16/SDK 36; main does not) → SDK 36.1 install → keystore generate-if-missing → `assembleDebug` → upload `monolith-debug` (3-day retention) → `testDebugUnitTest` → verdict summary. Timeout 25 min.
- Main: **9 runs, 0 green, 9 failure** (`gh run list --branch main`). Failure *stage* table per run: runs 1–5 (@≤`8438f5a`) died at/before assemble (signing wall on #4/#5 lineage); runs 6–9 (@`cfe6ee2`→`e29acdf`) assemble ✓ + artifact ✓ + tests ✗.
- Keystore: committed on main (`cfe6ee2`, 2,666 B, tracked despite `.gitignore:18`) **and** generated by workflow (`8d2b077`). Signing wall: **closed** (owned by #3, CLOSED 22:56:22Z; proof run 40967732023).
- Latest artifact: run 40970102642, `monolith-debug`, 10,570,760 B, labelled UNTESTED by the workflow itself.
- Branch CI snapshot: `agent/issue-2` 40970308776 assemble ✓ / tests executed; `batch-10` 40971463696 assemble ✗; `agent/issue-12`/`agent/issue-13` addendum runs in flight at census time.

### §2 — Release status (true numbers)
- Releases: **1**. `batch-9`: full-release flag on a self-described debug/test build; asset 10,995,124 B; **0 downloads**; body discloses uncompiled tests.
- Version: `versionCode 1` / `versionName "1.0"` (`app/build.gradle.kts:19-20`) — on main and on `batch-10`.
- Release build path: **none**. No `assembleRelease`/`bundleRelease` step in any workflow checked; release signing config points at an absent-by-design `my-upload-key.jks` + env secrets; `*.jks` gitignored (`cfe6ee2`).
- Section verdict line: *There is currently no build a stranger could be handed as "the release." There is a debug APK in CI artifacts (untested) and a debug APK in Releases (misflagged).*

### §3 — Test status (true numbers)
- On main: **does not compile** — 17 compiler errors, 5 files, 6 sites (full table from Finding 3, sourced from run 40970102642's log). Zero tests have ever executed on main.
- On `agent/issue-2` lineage: **compiles and executes — 258 tests, 221 passed, 37 failed** (run 40970308776). The 37 are the next real work queue; the map lists them by class (ScreenshotHarness, DrawerRender, CascadeDeckRender, SoundAndSocialTruth, TruthCar3Sweep, BreakMigration, ShortsSniperTruth, ChromeWave, Tetrad — from the run log) and marks them *untriaged: behaviour vs stale-expectation not yet separated* (contrast `agent/issue-13`, where 5 DeskClockFlowTest failures were proven stale expectations and cured in `3c7b092`).
- Note the JDK split: main workflow JDK 17 vs issue-2's JDK 21 cure — on main the compile wall hides whether JDK 17 would also kill Robolectric classes at runtime; the map flags this as a masked second wall behind Finding 3.

### §4 — Issue census (true numbers, timestamped)
- Repo-wide at window end: **165 issues — 158 OPEN / 7 CLOSED** (`gh issue list --state all --limit 200`; GraphQL totals agree: open 158, closed 7).
- #1–#22: **16 OPEN / 6 CLOSED** (#2, #3, #12, #13, #19, #20 — with closedAt timestamps from Finding 4). Of the 16 open, 4 are records-by-design (#1, #18, #21, #22), leaving **12 live work issues** in the original set.
- Audit issues #23–#164: 142 filed (R1 #23–#72, R2 #73–#96, R3 #97–#114, R3b #115–#164); exactly **1 closed** at census (#26). #72 itself: OPEN, 0 comments at first view.
- PRs: **0, ever** (`gh pr list --state all` empty). Integration route = branch pushes + `batch-10` merge branch.
- Census rule for the map: republish with a timestamp every time; this census was stale within 10 minutes of being taken (5 closures) — an undated census is a lie by lunchtime.

### §5 — Merge-train status
- Train engine: branch `batch-10` @`d08a3f3`, 89 commits not on main, containing merges of the #2, #3, #5–#17 cell branches + hand-work (W-08, W-41, pill, EditableTimes). DB **v13** there vs **v12** on main.
- Train health: **assemble-red at tip** (40971463696). Earlier train runs (40969511651, 40970075949, 40970834444) also failure — the train has never had a green run.
- `agent/issue-15` = plan-only branch (Editions 2–3 of the merge plan); `agent/issue-2` = test-cure lineage (JDK 21); `tyler/safe-fixes` = **does not exist on remote — strike from all maps**.
- Main's own last app-code commit is still `fff9edd` (batch 9); everything since is CI/studio/audit records (`817e8c7`…`e29acdf`).

### §6 — What blocks a real release, in order, with prices
1. **Land the test cure on main** (merge `agent/issue-2`'s 5 test files + JDK 21 workflow line, or land `batch-10` whole — pick one; the map recommends the small merge first so main's CI can see tests at all). *Price: one merge + one ~5-min CI cycle; risk: low — the diff is 5 test files + 1 workflow line, already CI-proven to compile.*
2. **Triage the 37 runtime failures** on the issue-2 lineage into *stale expectation* vs *real behaviour* (the issue-13 DeskClock precedent: 5/5 were stale). *Price: one verification bundle per failing class (~9 classes); blocks any "green CI" claim, not the APK.*
3. **Make `batch-10` assemble-green, then land it** (DB v12→v13, MIGRATION_12_13, pill, W-08/W-41). *Price: unknown until the 40971463696 assemble log is read — first action is diagnosis, not code; then one merge + migration test evidence (fresh-install ≡ upgraded) per the batch-10 plan's own Step-11 receipt rule.*
4. **Build the release path**: add `assembleRelease` (or `bundleRelease`) to the workflow; provision the upload key as a CI secret (never in-repo — `.gitignore:21-23` already guards it); decide the version bump (1/"1.0" → the first real release version is Sarthak's call, not the map's). *Price: one workflow edit + one secret-provisioning action by the key holder + one decision.*
5. **Fix the flag**: mark `batch-9` as prerelease (one API/UI toggle) or supersede it with the §6.4 build and delete the mislabel by replacement. *Price: minutes; do it with the real release, not before, so the Releases page is never empty.*
6. **Close the loop on #72 itself**: once 1–5 land, #72's five boxes are struck with run ids — not before. *Price: this map, republished.*

### §7 — Finding-by-finding strike table
A verbatim copy of §(a)'s table above, formatted as #72's checkboxes: each line either FIXED (evidence link), STRUCK (evidence link), or OPEN-OWNED-BY #n. Current state: F1 struck-in-part (signing/assemble), F2 open, F3 open-on-main / owned #2, F4 struck, F5 open / owned #15.

---

## (c) Seat signatures (20 seats, each signing its own contribution)

1. **Crazy-ideas** — Proposed the map's §0 rule ("a number without a run id is not a number") and §6.1's small-merge-first route: land the 5-file test cure on main *today* instead of waiting for the whole train. — *Crazy-ideas*
2. **Maximum-implementation-potential** — The highest-leverage single act in this issue is merging `agent/issue-2`'s cure to main: it converts main's CI from a compile-error printer into a test-executing instrument in one ~5-minute cycle. — *Maximum-implementation-potential*
3. **Brutal critic** — Half the "progress" here is stage-dressing until it touches main: #2 was *closed* while main still fails to compile its tests, and `batch-10` has never had a green run; I refused to let either be scored as shipped. — *Brutal critic*
4. **Improver** — Improved the audit census itself: main's CI log shows a *second*, uncited broken site (`TruthCar3SweepTest.kt:346`, same four parameters) — Finding 3's census is understated, and the map now carries 6 sites, not 5. — *Improver*
5. **Designer** — §(b)'s map is structured as a release *instrument panel* (§1 CI → §2 Release → §3 Tests → §4 Census → §5 Train → §6 Blockers), each section ending in a one-line verdict a non-engineer can read in 10 seconds. — *Designer*
6. **Consumer** — From the outside there is exactly one downloadable thing, it calls itself a test build, its flag says full release, and 0 people have downloaded it; §2 says that in those words. — *Consumer*
7. **Student** — Verified the beginner's question — "has CI ever worked?" — has a two-part answer (builds: yes, since `cfe6ee2`; green: never, 0/9), and made §1 teach that distinction instead of collapsing it. — *Student*
8. **User** — As the person who would install this: the CI artifact is honestly labelled UNTESTED by the workflow itself, which is the one piece of labelling in this whole map that is *not* lying. — *User*
9. **User-experience** — Flagged the Releases-page experience as the worst surface in the programme: a stranger's first click lands on a misflagged debug APK; §6.5 sequences the fix so the page is never left empty. — *User-experience*
10. **Purpose-questioning** — Asked why #72 exists at all if it merely repeats the audit: its purpose is the *delta* — and the delta is real (signing cured, migration written, 6 closures) but smaller than the motion suggests (nothing on main). The map is organised around that gap. — *Purpose-questioning*
11. **Thoughtful** — Noted the closures landing *during* measurement (23:17–23:23Z) and insisted both censuses be printed with timestamps rather than quietly updating the numbers — a map that hides its own measurement window will lie next time too. — *Thoughtful*
12. **Deep-thinker** — The deep pattern: every cure in this programme lands on a branch first and main last, so *every* main-based audit is stale on arrival; §4's "republish with timestamp" rule is the systemic fix, not a formatting preference. — *Deep-thinker*
13. **Enthusiastic designer** — The batch-10 tree genuinely excites me: DB v13, the composed 8-column migration, W-41's seal-split writer, the pill — the next release is *already written*; it just hasn't survived its own assemble. — *Enthusiastic designer*
14. **Wow-level designer** — §6 is written to wow by honesty, not adjectives: six blockers, in dependency order, each with a price and a first action — a release map a stranger could execute from without asking anyone anything. — *Wow-level designer*
15. **Designer with 29 years of experience** — In 29 years the failure mode never changes: the flag, the version and the title are the release, whatever the code does; `batch-9` fails all three (misflagged, v1/"1.0", test build) and §2/§6.4–6.5 treat them as release work, not metadata. — *Designer with 29 years of experience*
16. **Intentionality-focused designer** — Checked every "by design" claim: the absent upload key is intentional (gitignore guard, `cfe6ee2`), the four record-issues (#1/#18/#21/#22) are intentional, the UNTESTED artifact label is intentional — the map now separates *intended* states from *accidental* ones instead of counting both as debt. — *Intentionality-focused designer*
17. **Synchronous collaboration** — Coordinated the live-census problem across seats: while sections were being drafted, #2/#12/#13/#19/#20 closed; the bundle re-ran the census rather than shipping a split-brain report, and §(a) carries both snapshots. — *Synchronous collaboration*
18. **Implementation-quality** — Verified quality, not just existence, of the migration: `batch-10`'s `MIGRATION_12_13` matches the plan's 8 columns *in order and default* (incl. nullable `withinLabel`, -1 seal pair) and is registered in `addMigrations` — then checked its branch's CI and reported the assemble failure alongside, unsoftened. — *Implementation-quality*
19. **Technical execution specialist** — Ran the primary evidence: `ls-tree`/file:line reads on the read-only base, run/step/artifact APIs for 40967732023, 40970102642, 40970308776, 40971463696, branch fetches for `agent/issue-2`/`agent/issue-15`/`batch-10`, and the `tyler/*` ls-remote null result. — *Technical execution specialist*
20. **Interaction-detail specialist** — Sweated the small interactions: `.gitignore:18` still *names* `debug.keystore` while the tracked file overrides it (a future diagnostician's trap, now documented in §1), and the workflow's verdict-summary is the one user-facing surface that already tells the truth — the map adopts its wording. — *Interaction-detail specialist*

*No seat was dropped; no seat required LEAD-COVERED substitution — all 20 contributed a distinct, evidence-tied line above.*

---

## Method & limits

- All repo reads from the read-only base @`e29acdf` plus `git fetch` of `agent/issue-2`, `agent/issue-15`, `agent/issue-3`, `batch-10` into remote refs (working tree untouched, no commits). All GitHub reads via `gh` CLI/API (run lists, run jobs/steps, `--log-failed`, artifacts, releases, issues incl. GraphQL comment/closure data). **No writes of any kind.**
- Limits: (i) CI log extracts were grep-scoped, not full-log reads — the 37-failure class list in §3 is from the failed-log lines returned, counts (258/37) are Gradle's own summary line. (ii) Comment counts vary slightly by API surface (REST vs GraphQL counted #2 as 9 vs 8 across snapshots taken minutes apart while comments/closures were landing); ranges are shown where they differed. (iii) Two in-flight runs at census time (40971845382 `agent/issue-12`, 40971860651 `agent/issue-13`) had no conclusion yet and are reported as in-flight, not failed. (iv) No device/phone verification: "shippable" here means CI-assembled + merged, nothing about on-phone behaviour.


---

## ADDENDUM — post-verification movement (appended after the main report was filed)

The merge train kept moving after the census window above closed. A late-returning read of issue #2's closing comments (all by `Sarthak-2007`, the repo account) establishes:

- **PR #165 — the repo's first pull request — MERGED 2026-10-29T23:26:22Z**: *"Issue #2: test call-sites fixed, suite compiles and runs (258 tests) + CI on JDK 21"*, head `agent/issue-2`. (At every census inside the report window, `gh pr list --state all` was empty; §4's "0 PRs" was true then and is **superseded now**.)
- Ren's closing verdict on #2 (2026-10-29T23:28:47Z) states the call-site fixes *"are merged to main via PR #165"* and the keystore cure is on main. **Finding 3's caveat — "the cure is not on main" — is therefore superseded**: the cure landed on main minutes after the verification window. Finding 3's verdict as measured (HOLDS on main @`e29acdf`) stands for its stated ground; on current main the call-site half of #2 is now fixed, and what remains open from that lineage is the 37 runtime failures.
- The 37 failures' ownership is confirmed by Riegel's finisher note (2026-10-29T23:25:03Z) on #2: *"runtime/render failures in other scopes — BreakMigrationTest (batch-10 migration composition), CascadeDeckRenderTest, ChromeWaveUnitTest, DrawerRenderTest (#8), ScreenshotHarnessTest (screen issues) — owned by the batch-10 composed tree and their issues, not by the call-site fix."* This matches §(b) §3's triage framing exactly.
- Main has advanced past `e29acdf`: a main run **40973388591 @`73aaeae`** was `in_progress` at the time of that read, alongside a wave of new branch runs (`agent/issue-47`, `agent/issue-50`, `agent/issue-56`, `agent/issue-61`, `agent/issue-67`, `agent/design-revamp`). No conclusion for 40973388591 is claimed here — it had not finished when read.
- Net effect on #72 if Prisma republishes the map now: Finding 3 flips from "HOLDS on main" to **struck on main (via PR #165), remainder OWNED-BY the failing classes' issues**; Finding 4's PR line updates to "1 PR, merged"; Findings 1, 2, 5 are unchanged by this addendum (no green main run had yet concluded, no release change, `batch-10` still unmerged at last check).
