# RELEASE ↔ ISSUE MAP — Monolith (Focus-app)

## §0 Provenance and the law of this document

- **Written by:** PRISMA, Wave-3 cluster C10 lead, for issue #72 (Audit R1 · release-issue-map).
- **Census time:** 2026-10-30 ~02:40 UTC (~08:10 IST). **Main tip at census: `8c3bb32`** (`git ls-remote origin main`).
- **The law:** a number without a run id, a SHA, or a file:line is not a number. Every figure below carries its provenance. Where a live re-check was blocked by API degradation at census time, the figure is stamped with the earlier verified read it rests on — never silently refreshed.
- **STALE WITHIN THE HOUR:** this programme merges hourly. The census in §4 is a photograph, not a feed. Re-run the §4 commands before quoting any count.
- Verification record behind this map: `studio/plan/ISSUE-72-VERIFY.md` (20-seat bundle, two censuses) + the Keeper-of-Record fact sheet (2026-10-30T00:03:35Z). Two Keeper figures were caught wrong against direct reads (an `agent/issue-71` tip and the PR totals) and are corrected here from primary sources — which is why this map cites methods, not just numbers.

## §1 CI — the build machine

- One workflow: `.github/workflows/rescue-build.yml` — steps: keystore → **assemble debug** → **upload APK artifact** → **unit tests**. There is no release job (§2).
- The audit-era signing wall is **dead, killed twice**: a `debug.keystore` is committed on main (`cfe6ee2`, 2,666 B, tracked despite `.gitignore:18`), and the workflow generates one by keytool when absent (`8d2b077`).
- **Rehearsal proof:** run `40967732023` (main) — keystore ✓, assemble ✓, artifact uploaded ✓, unit tests **executed**. The pipeline *can* run end to end; it did, the same night the audit slices were being closed.
- **Current state at census — two walls, both named:**
  1. **Queue saturation.** Every run in the latest window — main, batch-10, and all three C10 branches — sits `queued` (e.g. main runs `40993269198`/`40993653062`/`40990423308`/`40993836541`; batch-10 `40988283195`/`40988666199`). Dozens of wave branches pushed within the hour; verdicts are hours behind pushes.
  2. **KSP test-step crash.** The two most recent *concluded* branch runs — `40982439537` (agent/issue-68) and `40985299268` (agent/issue-69) — both assembled green, then died in the unit-test step at test *compilation* with the identical annotation: `Cannot invoke "ksp.com.intellij.openapi.application.Application.getService(...)" because the return value of "...getApplication..." is null`. Unrelated code, identical signature = infrastructure, not code. Reruns of both are queued at census. Earlier the same night, tests executed fine (runs `40967732023`, `40970308776`), so the wall is intermittent — first suspect is the workflow's JDK/KSP combination on the current runner image (a `ubuntu-latest` → Ubuntu 26 migration notice rides the recent runs).
- **Green-main count:** at the Keeper census (00:03:35Z) 0 of the last 9 main runs were green — every concluded one failed at the test step. No concluded main run since has been read green; the four newest are still queued. The honest sentence: **main has not posted a green run in the observed window; its assemble step is proven, its test step is the wall.**

## §2 Release — there isn't one yet

- **Releases on the repo: exactly 1** — `batch-9` (verified by the bundle census and re-verified by the Keeper at 00:03:35Z; a live re-list was API-blocked at this census and is flagged for the next reader). It is a self-described **debug/test build wearing a full-release flag**: `prerelease=false`, asset 10,995,124 B, **0 downloads**.
- **Version is still the template's:** `versionCode 1` / `versionName "1.0"` — `app/build.gradle.kts:19-20`, on every branch read.
- **No release path exists in automation:** no `assembleRelease` / `bundleRelease` step in any workflow. The release signing config points at `my-upload-key.jks` + env secrets that are **absent by design** — the key is Sarthak's to create and hold; no agent can or should mint it.
- Consequence, plainly: the app has never had a release build. Everything ever shipped to a phone rode the debug artifact from §1.

## §3 Tests — the wall moved, twice

- **Era 1 (audit):** main's tests did not compile — 17 compiler errors, 5 files, 6 stale call sites (ScreenshotHarnessTest.kt:350, ShortsToggleRenderTest.kt:66, SoundAndSocialTruthTest.kt:382, TruthCar3SweepTest.kt:6-7/314 + :346, TruthCar3CaptureBodyTest.kt:70), all owned by issue #2's tree.
- **Cured:** PR **#165** merged `agent/issue-2` to main at 2026-10-29T23:26:22Z (issue #2 closed 23:23:40Z) — the call-site cure is on main.
- **Era 2 (now):** tests compile and execute; what remains is **37 runtime failures** — run `40970308776`: 258 run / 37 failed — owned by the batch-10 composed tree and the screen issues (Riegel's note on #2), *not* by main's own diff.
- **Era 3 (this hour):** the KSP crash of §1 intermittently prevents test compilation at all. Until a rerun lands green-of-crash, "tests executing" cannot be evidenced on any branch — including branches whose code is unrelated to the crash.

## §4 Census (2026-10-30 ~02:40 UTC — re-run before quoting)

- Issues repo-wide: **120 OPEN / 45 CLOSED** (`gh issue list --state all --limit 300`).
- PRs repo-wide: **61 total / 29 merged / 32 open** (GitHub search API `type:pr` totals — the REST list undercounts under load; method named so the next reader can reproduce).
- **#1–#22: 11 CLOSED / 11 OPEN.** Closed: #2, #3, #5, #6, #12, #13, #14, #16, #17, #19, #20. Open: #1, #4, #7, #8, #9, #10, #11, #15, #18, #21, #22.
  - **Records-by-design (open ≠ stalled):** #1 (charter), #18 (frozen looks programme), #21 (parked decisions), #22 (master index). These close by Sarthak's ruling, not by work. Any "issues closed" metric that counts them as backlog is lying.
- **C10 cluster:** #68 OPEN (branch pushed, CI queued), #69 OPEN (same), #70 OPEN (same), **#71 CLOSED + merged** (PR #171, merge commit `d5b7a23`; verifier post-pass 8 PASS / 0 FAIL), #72 = this map.
- **Releases:** 1 (`batch-9`, §2). **Main tip:** `8c3bb32`. **batch-10 tip:** `44b6d3b`.

## §5 The merge train

**Landed on main (this wave):** PR #165 (`agent/issue-2` — test call-site cure), PR #167 (`agent/design-revamp`), PR #168 (`agent/issue-2` again), PR #171 (`agent/issue-71` — docs truth, C10). Three further merges landed after #171 (`9c96fc5`, `6818844`, `8c3bb32`); their PR numbers were not read at census — the train is moving faster than the census.

**In flight at census:**
- **`batch-10` @ `44b6d3b`** — Konto's Phase-2 composition train: DB v13 with `MIGRATION_12_13` (MonolithDatabase.kt:209 on that branch; the plan's exact 8 ADD COLUMNs), W-41 birth writer (`060c9b2`), ~89 commits ahead at the Keeper census. **Composition-sensitive (DB migration, W-41/43): it lands as one train, by Konto's hand — nobody force-merges pieces of it.**
- **`agent/issue-68` @ `6de1d25`** (C10) — intercept-extra provenance fix; CI queued (rerun of `40982439537`).
- **`agent/issue-69` @ `e69c458`** (C10) — battery cures; CI queued (rerun of `40985299268`).
- **`agent/issue-70` @ `08eb602`** (C10) — accessibility/usability cures; CI queued (run `40992150071`).
- **`agent/issue-9`** — Park permission-recovery cures (verdict print, escort, count) that #70's F4 rests on; issue #9 still OPEN at census. (#14, whose Reception #70's F2/F3 rest on, is CLOSED at census — its owner's pass landed it.)

## §6 Blockers, in dependency order, priced

1. **CI throughput + the KSP wall (fleet-wide, blocks every verdict).** Queue saturation is a concurrency fact (dozens of simultaneous branch pushes); the KSP crash needs one workflow investigation (JDK/KSP pin vs runner image) with a single proving rerun. *Price: one focused infra pass; until then every cluster's "tests executing" evidence is hostage.*
2. **37 runtime test failures** (run `40970308776`). Triage against the batch-10 composed tree + screen issues; each failure already has an owning issue by Riegel's mapping on #2. *Price: distributed across owners; blocks any "green main" claim, blocks nothing else.*
3. **batch-10 green + land** (Konto's train, §5). Assemble was red at its last concluded read (run `40971463696`); two newer runs queued at census. *Price: the train's own work; lands the DB v13 migration + W-41/43 as one composition.*
4. **The release path** (after 1–3): (a) **version decision — Sarthak's call** (what number follows `1.0`); (b) **upload key + store secrets — Sarthak's hands only**; (c) add an `assembleRelease`/`bundleRelease` job + signed-release artifact to `rescue-build.yml` (*price: one workflow block, an afternoon's care, rides only after (a)+(b) exist*).
5. **The `batch-9` flag.** A debug build wearing `prerelease=false` mislabels the shelf. *Price: one API call to flip it to prerelease — or it is superseded the day blocker 4 produces a real release. Do not flip it silently; it is the only "release" a visitor can download, and the map is its correction until then.*
6. **Records classification** (§4): #1/#18/#21/#22 close by ruling. *Price: one line from Sarthak each, whenever he chooses; until then they are furniture, not backlog.*

## §7 Issue #72 — the strike table

| # | Finding (as filed) | Disposition |
|---|---|---|
| F1 | Signing/CI wall: no green main runs | **PARTIAL** — the signing half is FALSE now (killed twice: `cfe6ee2`, `8d2b077`; rehearsal `40967732023` proved the full pipeline incl. tests executing). The "no green main" half still holds in the observed window, its current form being §1's two walls (queue + KSP), not signing. |
| F2 | The only release is a mislabelled debug build; version 1/"1.0"; no release path | **HOLDS** — §2 verbatim. **STANDS-BLOCKED** on blocker 4: the version number and the upload key are Sarthak's decisions, named with prices. Owned jointly by this map and the release slices #24/#25. |
| F3 | Test wall from #2's stale call sites | **PARTIAL, superseded** — cured on main by PR #165 (2026-10-29T23:26:22Z). What remains is §3 Era 2 (37 runtime failures, owned by the batch-10 tree + screen issues) and Era 3 (the KSP wall, blocker 1). `tyler/safe-fixes` does not exist on the remote and must never be cited as the cure branch. |
| F4 | #1–#22 all open / stalled | **FALSE as filed** — 0 closed at audit time; **11 of 22 closed** at this census (§4), including #2 and #3 on CI evidence. The comment-volume sub-claim partially held at first census (#3 = 21 comments). The four records-by-design are classified in §4 so the count is never misread again. |
| F5 | DB migration W-41/43 missing | **PARTIAL** — on main: holds (DB v12, no `MIGRATION_12_13` in code). On `batch-10`: the migration EXISTS in code (DB v13, MonolithDatabase.kt:209, exact 8 ADD COLUMNs, W-41 writer `060c9b2`) but the branch was assemble-red at last concluded read and is unmerged. **Composition-sensitive: Konto's train (§5/§6.3); #15 owns the issue-side claim and stays OPEN until the train lands.** |

*Map written once, stamped once. Its successor is not an edit — it is the next census, taken when blockers 1–3 move.*
