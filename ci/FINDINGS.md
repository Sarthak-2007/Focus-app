# Issue #3 — cell findings (Fern lead)

## Run #4 (40957609929, head 817e8c7) — diagnosed from its logs

Per-step: checkout ✓ · setup-java ✓ · Install SDK platform 36.1 ✓ ·
Assemble debug ✗ · upload-artifact — skipped · Unit tests — skipped ·
artifacts: none. Run conclusion: failure.

Assemble ran 5m 23s, 30 actionable tasks executed, all Kotlin
compilation passed. Single failure:

```
> Task :app:validateSigningDebug FAILED
> Keystore file '/home/runner/work/Focus-app/Focus-app/debug.keystore'
  not found for signing config 'debugConfig'.
```

A KSP/AWT NullPointerException appears mid-log; the build continued
past it. Noise, not the cause.

## Root cause (repo evidence)

- `app/build.gradle.kts`: debug build type uses `debugConfig`,
  `storeFile = file("${rootDir}/debug.keystore")`, storePassword
  "android", keyAlias "androiddebugkey", keyPassword "android" —
  the standard Android debug key, relocated to the repo root.
- `.gitignore` line 18: `debug.keystore`. `git ls-files` shows no
  keystore tracked. The file only ever existed locally (AI Studio).
- Therefore every clean checkout fails signing, deterministically.

## Cure options

1. **Workflow generates the key (RECOMMENDED, drafted in
   `ci/rescue-build.draft.yml`)** — one `keytool -genkeypair` step
   before Assemble reproducing the standard debug key at the repo
   root. No repo change, no binary in history, .gitignore untouched.
2. Commit debug.keystore (force-add past the ignore rule) — works,
   but puts a binary in history against the project's own ignore.
3. Repoint debugConfig at `~/.android/debug.keystore` (AGP's
   auto-generated default) — touches app build config; wider blast
   radius; owned by Ren/trunk, not by this workflow.

## Chain status against the issue's acceptance bar

Bar: one run with assemble ✓ + artifact uploaded + unit-test step
executed (any test verdict — test-compile breakage is issue #2's).
Not yet met. Runs #1–#3 died in plumbing before code; run #4 died at
signing after full compilation. The keystore step is the only known
blocker between the current workflow and the bar.

— Fern, issue #3 cell lead

## Addendum — full cell results (Oct 30, ~03:10 IST)

**Run #5 (40958472117, head 8438f5a) replicated run #4 exactly:**
Assemble failed at `:app:validateSigningDebug`, same keystore error,
BUILD FAILED in 4m 56s, upload + tests skipped, 0 artifacts. Two
independent runs, one wall. The Verifier independently confirmed
every historical claim (runs #1–#5, the 817e8c7 wrapper commit)
against the API; the Diagnostician's reading matches this file.

**Critic's charges (full text on issue #3, comment 6508980068):**
artifact uploads before tests run — a red run can carry a
shippable-looking APK (fix: tests first, or an UNTESTED artifact
name, plus test-report uploads); "executes, whatever the verdict"
proves plumbing, not health — split the acceptance; permanent red
trains people to ignore red (advisory test step until issue #2
closes); unfiltered push trigger + no cache + no concurrency burns
the 2,000 free minutes (runs #1–#5 ≈ 15 billable minutes, 0
artifacts); no timeout-minutes (360 default); branch protection /
required checks are impossible on a free-plan private repo (both
endpoints 403) — the issue thread is the interim gate; the name
"rescue-build" no longer describes the general CI.
**Correction to the Critic's charge 1:** it states the run #4
verdict never landed on the issue. It did — Fern's comment
6508950234, posted before the Critic's thread snapshot was taken
(the Critic read the thread when it held only Ren's comment). The
charge's underlying demand — a verdict per run until closure —
stands and is now satisfied for runs #4 and #5.

**Innovator's draft** (`ci/rescue-build.innovator.yml`): split
build/test jobs, setup-gradle caching, push path filters (run #5
was a docs commit that burned a full build), timeouts 25/20,
per-ref concurrency (main exempt), SHA-pinned v5 actions,
ubuntu-24.04 pin, step summaries + diagnostics/test-report
artifacts. Priced per change in the Innovator's comment 6508990824.

**Open decisions for Ren (he lands all workflow changes):**
1. Keystore: commit the real `debug.keystore` (Innovator's
   preference — restores local/CI signature identity; the debug
   key's password is the public default) vs the drafted CI-generated
   throwaway key (zero repo change; CI APKs then can't update-install
   over locally built ones on the phone).
2. Acceptance wording: the Innovator's parallel jobs change
   "assemble → upload → test" into two independent verdicts — the
   issue's acceptance line needs Ren's amendment, not a silent swap.
3. Caching: `gradle/actions/setup-gradle` vs hand-rolled
   `actions/cache` (supply-chain weight vs maintenance).

— Fern, issue #3 cell lead
