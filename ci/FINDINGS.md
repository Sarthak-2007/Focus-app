# Issue #3 — CI: prove the automatic build circuit end to end

## Verdict: NOT PROVEN — blocked at debug signing

The pipeline now reaches the code and compiles it. It has never
finished a build. Every run from #4 onward dies at the same task —
`:app:validateSigningDebug` — because the debug keystore the build
demands is gitignored and has never been in the repo.

**The one cure that unblocks everything is drafted and waiting for
Ren to land:** generate the standard debug key in the workflow,
before Assemble (`ci/rescue-build.draft.yml`). Nothing else in this
file blocks the acceptance bar.

## The bar — still unmet

Issue #3 closes on ONE run with all three, in order:

- [ ] `assembleDebug` **succeeds**
- [ ] debug APK **artifact uploaded** (`monolith-debug`)
- [ ] **unit-test step executes** and reports its own verdict
      (pass/fail is irrelevant here — test health is issue #2's)

Status after 7 runs: **0 / 3.** No run has ever produced an artifact;
the unit-test step has never executed — it has only ever been
*skipped*. A skipped test step is not evidence about issue #2.

## Evidence — all 7 runs

Workflow `rescue-build` (`.github/workflows/rescue-build.yml`).
Every row verified against the Actions runs / jobs / artifacts /
step-log endpoints, not against comments.

| Run | Run ID | Head | Failing step | Verbatim log evidence | Artifacts |
|---|---|---|---|---|---|
| #1 | 40956477459 | `b792727` | Step 4 — `android-actions/setup-android@v3` | "Wrong version in preinstalled sdkmanager" — sdkmanager 16.0 exited 1; steps 5–8 skipped (job 123737779591) | 0 |
| #2 | 40956661031 | `b981ca1` | Step 4 — Install SDK platform 36.1 | `sdkmanager: command not found` · `Process completed with exit code 127.` (job 123738392030) | 0 |
| #3 | 40957010853 | `6d2eebe` | Step 5 — Assemble debug (step 4 SDK install **passed**) | `chmod: cannot access 'gradlew': No such file or directory`, exit 1 — died in <1s (job 123739594315) | 0 |
| #4 | 40957609929 | `817e8c7` | Step 5 — Assemble debug | `> Task :app:validateSigningDebug FAILED` · `Keystore file '/home/runner/work/Focus-app/Focus-app/debug.keystore' not found for signing config 'debugConfig'.` · `BUILD FAILED in 5m 23s`, 30 tasks executed (job 123741660656) | 0 |
| #5 | 40958472117 | `8438f5a` | Step 5 — Assemble debug | Identical `validateSigningDebug` / missing-keystore error · `BUILD FAILED in 4m 56s`; upload + Unit tests skipped | 0 |
| #6 | 40959707129 | `47c0398` (`agent/issue-15`) | Step 5 — Assemble debug | Identical `validateSigningDebug` / missing-keystore error · `BUILD FAILED in 4m 35s`; upload + Unit tests skipped | 0 |
| #7 | 40961380241 | `f3a5c3a` (`agent/issue-3`) | Step 5 — Assemble debug | Identical `validateSigningDebug` / missing-keystore error · `BUILD FAILED in 5m`; upload + Unit tests skipped | 0 |

Pattern in runs #4–#7 is step-for-step identical: checkout ✓ ·
setup-java ✓ · Install SDK platform 36.1 ✓ · Assemble ✗ ·
upload-artifact **skipped** · Unit tests **skipped**.

## What is already proven (runs #1–#3: plumbing, fixed and holding)

1. **setup-android action removed.** Run #1 failed inside it; the
   workflow now uses the runner's preinstalled SDK directly
   (commit `b981ca1`).
2. **sdkmanager by full path.** Run #2: bare `sdkmanager` is not on
   PATH (exit 127). Fix `6d2eebe` calls
   `$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager` — SDK install
   has passed in every run since (#3–#7).
3. **Gradle wrapper committed.** Run #3 exposed that the repo never
   contained `gradlew` (local builds used a standalone Gradle).
   Commit `817e8c7` added `gradlew` (+248), `gradlew.bat`,
   `gradle/wrapper/gradle-wrapper.jar`, and updated
   `gradle-wrapper.properties` (Contents API: `gradlew` 8,618 B,
   wrapper jar 46,175 B). Runs #4–#7 download Gradle 9.3.1 and run
   ~5 minutes of real tasks — the wrapper fix holds.

## Root cause — signing (repo evidence, not inference)

- `app/build.gradle.kts:35–36` — signing config `debugConfig`:
  `storeFile = file("${rootDir}/debug.keystore")`, with the standard
  Android debug credentials (storePassword `android`, keyAlias
  `androiddebugkey`, keyPassword `android`, lines 35–40).
- `app/build.gradle.kts:55` — the debug build type is forced onto
  `debugConfig` instead of AGP's auto-generated default key.
- `.gitignore:18` — `debug.keystore`.
- `git ls-files` — **no keystore tracked**; Git tree of `main`:
  zero keystore files in 716 entries.

Conclusion: the file only ever existed on local machines (generated
by AI Studio). Every clean checkout fails signing validation,
deterministically. Compilation is *not* the problem: in run #4 all
Kotlin compilation upstream of signing passed (warnings only).

**Noise in the same logs — explicitly not the cause** (build
continued past both, in runs #4 and #5): a KSP/AWT
`NullPointerException` logged as `##[error]Exception in thread
"AWT-EventQueue-0" …`, and the warning `File google-services.json
is missing. The Google Services Plugin cannot function without it.`

## The cure

### Recommended — minimal fix, drafted, awaiting Ren

`ci/rescue-build.draft.yml` (branch `agent/issue-3`) is the current
workflow plus ONE step before Assemble — no repo change, no binary
in history, `.gitignore` untouched:

```bash
keytool -genkeypair -v -keystore debug.keystore -storepass android \
  -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 \
  -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
```

`keytool` ships with the JDK that `setup-java` already installs.
This reproduces exactly the key `debugConfig` expects, at exactly
the path it expects (`${rootDir}/debug.keystore`).

### Alternatives — considered, not recommended as the first move

| Option | Why not first |
|---|---|
| Commit the real `debug.keystore` (force-add past `.gitignore:18`) | Works, and restores local/CI signature identity — but puts a binary in history against the project's own ignore rule. (Innovator's long-term preference; see Decisions.) |
| Repoint `debugConfig` at `~/.android/debug.keystore` (AGP default) | Changes the app's build file, so it can affect local builds too. Only Ren changes that file; this workflow does not. |

### After the cure lands — how this issue closes

The next push must be checked for all three, from the API:
assemble **success** + `monolith-debug` artifact **present** +
unit-test step **executed** (any verdict) — against the Closure
Evidence Spec (Finisher 4, on issue #3: 7 gates, incl. the rule
that a cure existing only on a draft branch does not count).
Post that run's verdict on issue #3 in the standing format
(Finisher 3's skeleton, Feel Specialist 1's headlines, Finisher 5's
plain-words lines). Until then, the bar above stays 0 / 3.

## Open decisions — Ren only (he lands all workflow changes)

1. **Keystore identity.** CI-generated throwaway key (draft above;
   zero repo change; CI APKs then cannot update-install over
   locally built ones) **vs** commit the real `debug.keystore`
   (Innovator's preference — one signature everywhere; the debug
   password is the public default, so this is not a secret leak.
   The *release* key, `my-upload-key.jks` via `KEYSTORE_PATH` in
   `app/build.gradle.kts`, is a secret and must stay out either way).
2. **Acceptance wording.** The Innovator's draft splits build/test
   into parallel jobs, turning "assemble → upload → test" into two
   independent verdicts. That changes this issue's acceptance line —
   it needs Ren's amendment, not a silent swap.
3. **Caching.** `gradle/actions/setup-gradle` (Innovator's draft)
   vs hand-rolled `actions/cache` — supply-chain weight vs
   maintenance.
4. **Naming** (Feel Specialist 2). Workflow display name →
   `build-and-test` now (file name stays `rescue-build.yml` until
   this issue closes, then renames with it); all steps named for
   the act, the keystore step spelled out in full; artifact either
   moves after the test step carrying the test outcome in its name,
   or is named `…-UNTESTED` while upload precedes tests; run
   citations as run #N · `sha7` · id `run-id`.

## Known risks the cure does NOT fix

From the Critic (comment 6508980068) and Innovator
(comment 6508990824); full drafts in
`ci/rescue-build.innovator.yml`. None of these block the bar; all
of them matter the day after it is met.

- **Untested APK looks shippable.** Current order is Assemble →
  upload-artifact → Unit tests: a red run will carry a
  downloadable APK indistinguishable from a green run's, with no
  test report uploaded. Fix proposed: tests first, or a distinct
  `…-UNTESTED` artifact name + test-report uploads.
- **"Step executes" proves plumbing, not health.** Closing #3 must
  state which was achieved; a green end-to-end run remains a
  separate, later fact.
- **Permanent red trains ignoring red.** Once assemble passes,
  every push goes red at the known-broken test step (issue #2)
  until #2 lands. Proposed: explicitly advisory test step with a
  loud summary until #2 closes, gating after.
- **Budget burn.** Unfiltered `on: push`, no cache, no
  concurrency: runs #1–#5 cost ≈15 billable minutes for 0 artifacts
  (Critic's estimate from job durations); runs #6–#7 added ~10
  more. Run #5 (`8438f5a`, a `studio/` docs migration) and run #6
  (`47c0398`, a plan doc) each paid for a full Android build.
  Against 2,000 free minutes/month. Proposed: `paths:` filter
  (`workflow_dispatch` stays unfiltered), Gradle caching, per-ref
  concurrency.
- **No timeout.** Job has no `timeout-minutes`; the 360-minute
  default applies — one hung daemon can eat 18% of the month.
  Proposed: 25 (build) / 20 (test); longest legitimate run so far
  is ~6 minutes.
- **CI cannot gate on this plan.** `GET /branches/main/protection`
  and `GET /rulesets` both return 403 on a free-plan private repo —
  no required status checks can exist. Until the plan changes, the
  verdict posted on this issue is the gate.
- **Name and rot.** The workflow is named `rescue-build` while
  serving as permanent CI; run #4's log already warns setup-java
  v4 is deprecated and Node 20 actions are being force-migrated;
  the SDK step swallows license output and failure
  (`> /dev/null 2>&1 || true`). Innovator's draft pins
  `ubuntu-24.04`, SHA-pins v5 actions, and adds step summaries.

## Provenance

- Cell: Fern (lead) — diagnosis, minimal draft, integration;
  Diagnostician (comment 6508963898); Verifier, independently
  against the API (comment 6508973214); Critic
  (comment 6508980068); Innovator — full draft
  `ci/rescue-build.innovator.yml` (comment 6508990824).
- Finisher pass: Finisher 1 (documents — this file's rewrite),
  Finisher 2 (workflow craft — the minimal draft's finish),
  Finisher 3 (verdict format), Finisher 4 (closure evidence spec),
  Finisher 5 (plain words audit + verdict vocabulary).
- Feel pass: Feel Specialist 1 (verdict wording), 2 (signal
  naming), 3 (how failure lands), 4 (interruption ladder),
  5 (the green landing — closing comment pre-written).
- Correction on the record: the Critic's Charge 1 said the run #4
  verdict never landed on this issue. It did — Fern's comment
  6508950234 predates the Critic's thread snapshot. The underlying
  demand (a verdict per run until closure) stands and is satisfied
  for runs #4–#7.
- Runs #6 and #7 and the Finisher/Feel passes were added Oct 30,
  ~03:35 IST, verified live against the Actions API.

— Fern, issue #3 cell lead
