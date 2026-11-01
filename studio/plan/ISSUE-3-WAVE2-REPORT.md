# ISSUE #3 — WAVE-2 REPORT

**Issue:** CI: prove the automatic build circuit end to end
**Repo / branch:** Sarthak-2007/Focus-app · `agent/issue-3`
**Cell lead:** Fuge (Cluster A, flat under Ren) — Fern's wave-1 cell
built the diagnosis; Wave-2 lands the cure on this branch.
**Wave-1 base:** `bbfeb0b` / `f3a5c3a` — `ci/FINDINGS.md`,
`ci/rescue-build.draft.yml` (minimal), `ci/rescue-build.innovator.yml` (full).
**Wave-2 state: DECLARED, uncompiled locally.** Nothing was run
through Gradle on this box, per standing law. The proof, when it
comes, is a GitHub Actions run — see §2 for exactly what this
branch's push does and does not prove.

---

## 1 · LEAD VERDICT (Fuge)

The signing wall is cured **in the workflow file itself** on this
branch (§2). That is the fix code/workflow files can make. What it
cannot do by itself:

- A green run on `agent/issue-3` is a **dress rehearsal** (Brutal
  critic + Purpose-questioning, §5.2; wave-1 Finisher 4's closure
  spec already ruled a cure living only on a side branch does not
  count). **Closure requires Ren to land the identical workflow on
  `main` and cite a main run** — that landing is Ren's act, not
  this cell's.
- The acceptance bar (wave-1 FINDINGS) stands at **0 / 3** until a
  run shows: assemble success + `monolith-debug` artifact present
  + unit-test step **executed** (any verdict; test health is #2's).

## 2 · WHAT WAVE-2 CHANGED

`.github/workflows/rescue-build.yml` on `agent/issue-3` is now the
wave-1 minimal draft **landed as the real workflow**, plus exactly
two fold-ins that cannot falsify the proof (roster consensus, §5):

1. **The cure (unchanged from Fern's draft):** a
   `Generate debug keystore (issue #3 fix)` step before Assemble —
   `keytool -genkeypair` with the exact standard debug parameters
   `debugConfig` demands (`app/build.gradle.kts:35–40`:
   `${rootDir}/debug.keystore`, storepass/keypass `android`, alias
   `androiddebugkey`, RSA 2048), guarded so an existing keystore is
   used as-is. No repo change, no binary in history, `.gitignore`
   untouched; `keytool` ships with the JDK the workflow installs.
2. **`timeout-minutes: 25`** (Maximum-implementation-potential +
   Improver's sole fold-in; FINDINGS' proposed value; longest
   legitimate run ~6 min; the platform default was 360).
3. **A plain-words verdict summary** (`if: always()`, step outcomes
   only): posts Assemble outcome, whether unit tests executed,
   their verdict, and — whenever tests did not pass — the line
   "The uploaded APK is UNTESTED in this run — do not treat it as
   proven." (User-experience seat's surgical cure for the
   false-green moment: truth in the summary, not in a renamed
   artifact.)

**Deliberately NOT landed** (post-closure package, §6): job split,
Gradle caching, `paths:` filter, concurrency, SHA-pinned v5
actions, workflow/artifact renames, test-report uploads, the
Signing Canary. Reasons, by consensus: `paths:`/concurrency can
suppress or cancel the proof run itself; the bar literally names
the artifact `monolith-debug`; every extra variable muddies
attribution if the run goes sideways. YAML validated by parse
(PyYAML) after editing; the step list and ordering are otherwise
identical to the workflow that produced runs #1–#7.

**Push consequence, stated plainly:** pushing this branch triggers
a run of the new workflow on `agent/issue-3` (the workflow fires on
any push). That run is the dress rehearsal. Its verdict — whatever
it is — belongs on issue #3; this report does not pre-claim it.

## 3 · THE REFUSED CLUSTER — LEAD-COVERED, NOT DROPPED

One Wave-2 seat cluster (Thoughtful, Deep-thinker, Enthusiastic
designer, Wow-level designer, Designer-29-years, Intentionality,
Synchronous collaboration, Implementation-quality, Technical
execution, Interaction-detail) was refused by a safety filter
before writing anything. Per commission, Fuge covers all ten
lenses personally below, each marked **LEAD-COVERED**. The refusal
is recorded here, not routed around.

#### Thoughtful — LEAD-COVERED, signed: Fuge
The thoughtful shape of this fix is restraint: four identical
failures (runs #4–#7) earned exactly one changed variable. Every
additional item in the innovator draft may be right, but landing
them together would have made the next failure unreadable. Care
for the future reader of the run list outweighed care for a
prettier workflow file.

#### Deep-thinker — LEAD-COVERED, signed: Fuge
The deep defect is not in CI at all: `debugConfig` hard-wires the
debug build to a file that version control is instructed to ignore
(`.gitignore:18`). Any clean environment — CI, a new machine, a
teammate — was always going to fail signing; local machines only
ever worked because AI Studio had generated the file in place.
The workflow step treats the symptom at the one place it can be
treated without touching the app's build file. The root-level
alternatives (commit the debug keystore; repoint `debugConfig` at
the AGP default `~/.android/debug.keystore`) change what local
builds are, and are therefore Ren's/Sarthak's call (§6), not a
CI cell's.

#### Enthusiastic designer — LEAD-COVERED, signed: Fuge
What this unblocks is not plumbing pride — it is every other
issue's evidence. Sixteen workable issues currently verify on
paper; a working circuit turns each push into a machine witness.
The first green run is the moment the studio's DECLARED era gets
its second witness, and that deserves the pre-written closure
headline the Feel seats drafted (§5.1): "PROVEN — 3 / 3."

#### Wow-level designer — LEAD-COVERED, signed: Fuge
The wow here is a run page that tells the truth in three seconds:
one summary block, worded verdicts next to every colour, the
UNTESTED warning attached to the artifact it describes. Most CI
pages require log archaeology; this one now reads like the
receipt slip Sarthak loved — state first, evidence under it. The
full naming redesign (Feel/Finisher §5.1) would take it further;
it waits for closure so the proof run stays comparable to
runs #1–#7.

#### Designer-29-years — LEAD-COVERED, signed: Fuge
Twenty-nine years of build systems reduces to two habits this
wave kept: (1) when a build fails identically four times, fix the
cause in the smallest place that owns it — one step, in the file
that runs; drafts in a `ci/` folder persuade nobody's runner.
(2) Never let a downloadable artifact look more proven than it is:
the summary's UNTESTED line is the cheapest honest label available
without renaming the artifact the acceptance bar cites.

#### Intentionality-focused designer — LEAD-COVERED, signed: Fuge
Every line landed answers "why are you here?": the keystore step
carries its full causal comment (which runs proved what); the
timeout names the budget it protects; the summary names the lie
it prevents. And every line *not* landed was omitted on purpose,
with the reason recorded in §2 — intentionality includes the
intentional absence of good ideas that would fog the experiment.

#### Synchronous collaboration — LEAD-COVERED, signed: Fuge
This cell inherited Fern's wave-1 work intact — findings, two
drafts, seven-run evidence table — and its job was to land, not
to re-diagnose. In return it leaves an equally clean state: one
workflow change on the branch, the drafts preserved in `ci/` for
the post-closure step, and the two open rulings (keystore
identity; closure ref) written where Ren will find them (§6),
not buried in comments. No seat messaged Sarthak.

#### Implementation-quality — LEAD-COVERED, signed: Fuge
Quality checks applied to the landed file: (a) diffed step-by-step
against `ci/rescue-build.draft.yml` — the functional delta vs the
old workflow is exactly the keystore step; the rest is timeout,
step ids, and the summary step, none of which touch the assemble
path; (b) YAML parses cleanly; (c) step outcomes referenced by the
summary (`steps.assemble.outcome`, `steps.tests.outcome`) are
wired to ids declared on those steps; (d) the summary step is
`if: always()`, so a failed build still gets its verdict printed
— the moment it matters most; (e) artifact name/path unchanged
from runs #1–#7, keeping the closure check comparable.

#### Technical execution specialist — LEAD-COVERED, signed: Fuge
Static verification, item by item:
- **Keytool args vs `debugConfig`:** storeFile
  `${rootDir}/debug.keystore` = repo root = the checkout working
  directory the step runs in ✓; `-storepass android`,
  `-keypass android`, `-alias androiddebugkey` match
  `app/build.gradle.kts:36–39` exactly ✓; RSA 2048 + the standard
  `CN=Android Debug,O=Android,C=US` dname reproduce the standard
  Android debug key ✓.
- **Upload path:** `app/build/outputs/apk/debug/app-debug.apk` is
  AGP's debug output path — unchanged from the workflow whose runs
  reached Gradle task execution ✓.
- **Test step:** same job, same workspace — the generated keystore
  persists for `testDebugUnitTest` (which also signs test APK
  artefacts) ✓. (In the innovator split-job draft, both jobs carry
  their own generate step — checked, ✓.)
- **Other clean-checkout blockers behind signing:** cleared on
  evidence, not hope — runs #4–#7 executed ~30 Gradle tasks on
  clean runners, so configuration already survived the Secrets
  plugin (`.env` absent) and the missing `google-services.json`
  (`missingGoogleServicesStrategy = WARN` in
  `app/build.gradle.kts` + `googleServices.missing.passthrough=true`
  in `gradle.properties`); SDK discovery via `ANDROID_HOME` worked
  in every run since #3 ✓. The next unknown is test-source
  compilation — that is issue #2's territory, not a signing
  surprise.
- **RAM law:** `gradle.properties` carries `-Xmx2g` /
  `workers.max=2` ✓ (CI runners are not the local box, but the
  file travels with the repo and was checked).

#### Interaction-detail specialist — LEAD-COVERED, signed: Fuge
The interaction surface of CI is three things a thumb touches: the
run title, the step list, and the artifact button. This wave
deliberately kept all three recognisable (same workflow name,
same step order, same artifact name) and changed only what a
press *means*: the artifact press now comes with a printed
warning when its tests didn't pass, and the run's last step is a
verdict written for the person, not the log. The finer detailing
the Feel seats specified (run-name format, per-step verb names,
`-FAILED-logs` diagnostics labels — §5.1) is real craft; it is
scheduled as one coherent post-closure pass instead of confetti
across the proof run.

## 4 · DECISIONS

- **DECISION-FOR-SARTHAK:** *CI debug key identity.* The landed
  cure generates a fresh throwaway debug key inside CI on every
  run. Consequence on his phone: a CI-built APK **cannot
  update-install** over a locally built copy, nor over a previous
  CI build — uninstall first (app data lost). The alternative is
  committing the standard `debug.keystore` (public default
  password — not a secret; the *release* key stays out either
  way): one signature everywhere, CI APKs install over local ones.
  Ren lands the workflow either way; the choice is about how
  builds behave on Sarthak's phone, so it is his.
- **For Ren (rulings, not Sarthak's):** (a) *closure ref* — rule
  whether #3 closes on a main run (this cell + Brutal critic +
  Purpose-questioning all say it must); (b) land this branch's
  workflow commit on `main` the same day as a green rehearsal;
  (c) the post-closure package (§6) as its own commit/run.
- No other decision is hidden in this issue.

## 5 · SEAT SECTIONS (30 seats)

### 5.1 Finishers ×5 + Feel specialists ×5 — the CI experience

#### Finisher 1 — Surface — signed: Finisher 1 (Surface), Fuge cluster, Wave 2
Every surface Sarthak touches must agree; today workflow says `rescue-build`, artifact says `monolith-debug`, and three steps have no `name:` at all. Proposals (exact strings): workflow display name `name: build-and-test` (file stays `rescue-build.yml` until #3 closes); run title `run-name: build-and-test · ${{ github.ref_name }} · run ${{ github.run_number }}`; all steps named for the act (`Get the code` → `Set up JDK 17 (Temurin)` → `Install Android SDK 36.1` → `Generate the debug keystore the build expects` → `Build the debug APK` → `Run the unit tests` → `Upload APK`); while upload precedes tests the artifact is `monolith-debug-UNTESTED`, diagnostics `monolith-build-FAILED`, test reports `monolith-test-reports`; step summary first line `## Build verdict`, fingerprint block under it as `## APK fingerprint (debug build — not a release)`.
[Lead disposition: POST-CLOSURE package (§6). The proof run keeps runs #1–#7's names for comparability (Designer, §5.2); the landed verdict summary implements this seat's top surface in the interim.]

#### Finisher 2 — Motion — signed: Finisher 2 (Motion), Fuge cluster, Wave 2
Runs #4–#7 moved identically: four steps pass, Assemble dies, two steps silently skip — a story that stops, not one that fails. Proposals: reorder build → test → upload (or, in the split design, jobs named for their independent verdicts); present-tense verb names for every step; cancelled runs posted as exactly `Superseded — a newer push on this branch cancelled this run. Not a failure.`; skipped steps spoken in the summary — `- Did not run: Unit tests — the build failed first. No test verdict exists for this run.`; summary footer with duration vs the ~6-minute healthy run and the timeout values.
[Lead disposition: the landed summary speaks step outcomes including skipped (tests "executed: false" + verdict) — the core of this seat's demand; the reorder and cancellation wording ride the post-closure package.]

#### Finisher 3 — Light — signed: Finisher 3 (Light), Fuge cluster, Wave 2
The real cause (`debug.keystore not found`) is buried in ~5 minutes of Gradle log while a KSP/AWT `NullPointerException` and a `google-services.json` warning glow just as brightly and are explicitly not the cause. Proposals: failing-step annotation `::error title=Build failed — debug keystore missing::…`; summary leads with cause, not task name (`❌ FAILED at signing — no APK was produced`); known noise demoted by name in the summary; every verdict line carries a word (`FAILED`/`PASSED`/`SKIPPED`/`SUPERSEDED`) beside the colour; the SDK step stops swallowing its failure silently.
[Lead disposition: post-closure, except worded outcomes — already in the landed summary.]

#### Finisher 4 — Sound / Haptics — signed: Finisher 4 (Sound/Haptics), Fuge cluster, Wave 2
CI has no sound or vibration; the only controllable "ping" is the issue verdict comment. Rules: verdict comments **only on a transition** (first failure after a pass, first pass after a failure, the closure run) — identical repeat failures update the existing comment's run table; titles `🔴 New failure — build-and-test run #<N>` / `🟢 First pass after failure — build-and-test run #<N>` / `🏁 Circuit proven — build-and-test run #<N> closes the 3-part bar`; each ping self-contained in its first two lines; silence labelled: `No comment = no transition = same verdict as the previous run.` Explicitly rejected: Slack/Discord/webhook noise.

#### Finisher 5 — Typography / Print — signed: Finisher 5 (Typography/Print), Fuge cluster, Wave 2
The verdict as a printed receipt (GitHub Markdown can't use Sora/Atkinson/Anonymous Pro, so hierarchy and monospace do the work). Fixed skeleton: `## Build verdict — run #<N> · \`<sha7>\` · id \`<run-id>\``, then the verdict line, then `- Built: yes/no`, `- APK uploaded: …`, `- Tests ran: …`, `Bar: <0–3> / 3`. Monospace reserved for identifiers only; numbers always as a fraction against the bar; counts in the innovator draft's middle-dot format (`- tests: T · failures: F · errors: E · skipped: S`); banned from CI text: `rescue`, `just`, `simply`, bare `success` without its object (`Build succeeded — APK produced`; `Tests executed`, not `Tests passed`, unless they passed).

#### Feel Specialist 1 — Weight — signed: Feel Specialist 1 (Weight), Fuge cluster, Wave 2
Heaviest line reserved for the current real state: `NOT PROVEN — 0 / 3. No run has ever produced an APK; the unit tests have never run, only been skipped.` If the APK is uploaded before tests, the summary's first line is `⚠️ APK exists, tests have not run — do not treat this file as verified.` Never `Build passing` when only Assemble passed — replacement: `Assemble passed. That is 1 of 3`. The earned closure line, unqualified: `PROVEN — 3 / 3. Assemble succeeded, the APK was uploaded, and the unit-test step executed in run #<N>.` Every verdict also states: `Test pass/fail is not weighed here — that verdict belongs to issue #2.`

#### Feel Specialist 2 — Light — signed: Feel Specialist 2 (Light), Fuge cluster, Wave 2
Dark corners: a skipped step looks passed at a glance; a docs-only push pays for a full build invisibly; a hung job can run 360 minutes. Proposals: every non-pass outcome gets its own word (`SKIPPED — never ran`, `CANCELLED — superseded by a newer push`, `TIMED OUT — killed at 25 minutes, treat as failed, not as still running`); wasted runs lit in their own summary (`This run was triggered by a push that touched no build inputs.`) plus the innovator `paths:` filter; expected duration in the summary header (`Expected: ~6 minutes…`); the keystore step's name states its nature (`Generate a throwaway debug keystore (CI only — not the release key, not committed)` — or, if Ren commits the real key, the name changes with the decision); run citations everywhere in the fixed format `run #<N> · \`<sha7>\` · id \`<run-id>\``.

#### Feel Specialist 3 — Timing — signed: Feel Specialist 3 (Timing), Fuge cluster, Wave 2
Fail the cheapest truth first: a pre-Assemble check step (`Check the debug keystore exists before building`) whose failure lands in under a minute (after the cure lands, it verifies the generated file). Verdict comments post within the same run, in run order, superseding lines when back-filled. Timeouts with vocabulary: 25 (build) / 20 (test) / 10 (SDK) / 2 (keystore), worded `TIMED OUT at <limit> minutes — no verdict on the code`. Until #2 closes, the test step is advisory in the summary: `Tests ran and failed — expected until issue #2 lands. This run's bar (tests executed) is met…` — after #2 closes the line is deleted and the step gates. Retention stated where it applies: APK 3 days (`it is proof the circuit works, not a copy to keep`), test reports 14, diagnostics 7.

#### Feel Specialist 4 — Touch + Sound — signed: Feel Specialist 4 (Touch+Sound), Fuge cluster, Wave 2
The artifact name is the button label Sarthak's finger lands on; `monolith-debug` under a red run feels identical to under a green one — the single most dangerous touch surface in this workflow. Proposals: while upload precedes tests, artifact `monolith-debug-UNTESTED-run-<run_number>`; after a passing-test-then-upload order, `monolith-debug-tested-run-<run_number>`; an install warning directly above the artifact link (throwaway-key variant: `…this APK was signed with a key generated inside CI. It will NOT update-install over a locally built copy…`; committed-key variant: same signature as local builds, can update-install); diagnostics renamed `monolith-build-FAILED-logs-run-<run_number>`, reports `monolith-test-reports-run-<run_number>`; and the limit stated honestly: the inner file stays `app-debug.apk` (GitHub controls that name) — the zip's name is the true label.
[Lead disposition: post-closure (§6) per the named cluster's tally — the rename must not break the bar's artifact check mid-proof; the landed UNTESTED summary line carries the warning onto the run page now, and the keystore-identity decision (§4) is filed for Sarthak.]

#### Feel Specialist 5 — Instant-Read — signed: Feel Specialist 5 (Instant-Read), Fuge cluster, Wave 2
Three and only three headline templates, verbatim everywhere: Failure — `❌ FAILED — build stopped at <step in plain words>. No APK. Tests did not run. Bar: <n> / 3.` Proven — `✅ PROVEN — APK built and uploaded, tests executed (their pass/fail belongs to issue #2). Bar: 3 / 3.` Built-untested — `⚠️ BUILT, NOT TESTED — APK uploaded as monolith-debug-UNTESTED. This is not a verified build.` Current state in that format, for the record: `❌ FAILED — build stopped at signing the debug build. No APK. Tests did not run. Bar: 0 / 3.` (runs #4–#7, identical.) The Actions list itself must scan — workflow `build-and-test` + run-name makes branch/run/verdict readable without a click; `rescue-build` survives only in the filename until #3 closes. `Skipped` is never a near-tick: in text it is always `did not run`. Decisions surface as one instant-read line each.

### 5.2 The named twenty (part 1: Crazy-ideas → Purpose-questioning)

#### Crazy-ideas — signed: Crazy-ideas, Wave-2 seat, Cluster A (FUGE), issue #3
LAND MINIMAL, unchanged — the experiment has exactly one variable against four identical failures. **New mechanism — "Signing Canary" pre-flight job (spec + price):** a tiny first job that checks out, sets up JDK 17, runs ONLY keystore generation + `./gradlew :app:validateSigningDebug`, and gates the full build via `needs:`. Signing regressions then die in ~60–90 s instead of 4m35s–5m23s. ~20 lines of YAML, no new actions/secrets; ~30 min to write/review DECLARED; ~1–1.5 billable min per run, saving ~4 min per future signing failure. Companion: a weekly scheduled heartbeat on main so CI rot is detected in a quiet week (~24 of 2,000 free min/month).

#### Maximum-implementation-potential — signed: Maximum-implementation-potential, Wave-2 seat, Cluster A (FUGE), issue #3
LAND MINIMAL + the three guardrails that cannot falsify the proof: `timeout-minutes` (the 360-min default can eat 18% of the month), step summary, and `-UNTESTED` labelling — none touch the assemble path. Defer the two that CAN falsify proof: `paths:` (a skipped run looks like silence) and `concurrency:` with cancel-in-progress (a cancelled run proves nothing). Do NOT rename the artifact in the proof run — the bar literally checks `monolith-debug`; rename only via Ren amending the bar first.
[Lead note: landed with timeout + summary; the `-UNTESTED` rename was held (bar-name argument, this same seat) in favour of the summary warning — UX seat's position.]

#### Brutal critic — signed: Brutal critic, Wave-2 seat, Cluster A (FUGE), issue #3
Against the minimal draft: (1) a random key per run means CI APKs cannot update-install over local APKs *or previous CI APKs* — the "fix" manufactures a permanent signature-identity problem; (2) it preserves the worst ordering — Assemble → upload → tests — so a red run ships a downloadable APK indistinguishable from a green one (TELL-THE-TRUTH violated on day one); (3) it leaves unfiltered `on: push`, no cache, no timeout — the setup that already burned ≈25 billable minutes for 0 artifacts, including full builds for a docs migration (run #5) and a plan doc (run #6). Against proving on a branch: run #7 already proved a branch changes nothing when the cure isn't the executed workflow; even a green branch run leaves `main` broken and every other branch inheriting it. Demand: branch proof is a dress rehearsal only — the identical commit lands on main the same day, and closure is read off a main run.
[Lead: charges (1) and the closure demand are carried into §4 as the DECISION-FOR-SARTHAK and the Ren rulings; charge (2) is answered in this wave by the UNTESTED summary line, with the rename in the post-closure package.]

#### Improver — signed: Improver, Wave-2 seat, Cluster A (FUGE), issue #3
Staged landing, two steps, no third option. Step 1 (now): minimal draft as the real workflow on `agent/issue-3`, plus `timeout-minutes: 25` as the sole fold-in. Verify against the API: assemble success + artifact + test step executed. Step 2 (separate commit, after #3 closes): the innovator items as their own change with their own run — paths filter, concurrency with `cancel-in-progress: false` for the proof ref, step summary, test-report uploads, and the `-UNTESTED` rename with the acceptance wording amended by Ren in the same commit. Folding Step 2 into Step 1 makes one failure have five possible authors.

#### Designer — signed: Designer, Wave-2 seat, Cluster A (FUGE), issue #3
A run's name, job name, and artifact name are the interface. Changing the artifact mid-proof breaks pattern-matching against runs #1–#7 and the bar's wording. Keep `monolith-debug`, `rescue-build`, and the single-job shape for the closure run; the story stays "same pipeline, one step added, now it finishes." The naming system (workflow → `build-and-test`, steps named for the act, citations `run #N · sha7 · run-id`) lands coherently at once in the post-closure redesign, not piecemeal.

#### Consumer — signed: Consumer, Wave-2 seat, Cluster A (FUGE), issue #3
I judge by the APK in my hand. (1) A fresh keytool key per run means build N+1 won't install over build N — uninstall first, losing app data, every CI build. The minimal draft "works" in the Actions tab and annoys forever on the phone; the committed standard debug keystore (public password, not a secret; the release key stays out) is the consumer-correct long-term answer. (2) If tests haven't passed, the file should SAY so — `-UNTESTED` is honest, but after closure, without breaking the name the bar expects. Verdict: minimal now to unblock; keystore identity escalated, not buried.

#### Student — signed: Student, Wave-2 seat, Cluster A (FUGE), issue #3
Runs #4–#7 are a clean controlled failure: same step, same error, four times. The minimal draft changes exactly one thing, so a green run teaches exactly one lesson — generating the standard key at `${rootDir}/debug.keystore` (the path `debugConfig` demands, gitignored at `.gitignore:18`) cures `validateSigningDebug`. Fold in five innovator items and a failure teaches nothing. One changed variable is the only version anyone can learn from. Land minimal.

#### User — signed: User, Wave-2 seat, Cluster A (FUGE), issue #3
My workflow: push, open Actions, wait ~6 minutes, read. Unfiltered `on: push` is what I want during proof — a docs-only follow-up under paths filtering would silently produce NO run and leave me wondering; concurrency cancellation reads as failure. After closure, flip both — docs pushes shouldn't buy 5-minute builds (runs #5/#6 already did). Land minimal on `agent/issue-3` now; put the flip on the post-closure checklist where I can see it.

#### User-experience — signed: User-experience, Wave-2 seat, Cluster A (FUGE), issue #3
The lived defect is the false-green moment: artifact appears (feels like success) → test step fails (run goes red). `-UNTESTED` naming is a blunt cure that collides with the bar. The surgical cure: a final step summary written after the test step, stating in plain words "APK built · tests: executed, verdict X · this artifact was uploaded before tests ran" — one screen, whole truth, no renamed artifacts, no changed semantics. [Lead: LANDED — §2.3.] The full job-split summary waits; its "no XML produced" line is ambiguous during a compile failure — a worse experience than the single job's plain step list for the proof run.

#### Purpose-questioning — signed: Purpose-questioning, Wave-2 seat, Cluster A (FUGE), issue #3
CI here cannot gate anything: branch protection and rulesets return 403 on a free-plan private repo — no required status checks can exist. The circuit's product is *knowledge*, not enforcement, and that purpose is fully served by the minimal draft: one green run converts belief into a cited fact (run #N · sha · run-id). It is NOT served by gold-plating the proof run. But knowledge gained on `agent/issue-3` decays as main diverges — the purpose is discharged only when the same workflow lands on main and a main run is cited. Question for Ren, recorded not guessed: is closure read off the branch run or the main run? FINDINGS' bar doesn't specify a ref — Ren amends, nobody assumes.

**Cluster tally:** 9/10 seats converge on *land the minimal draft on `agent/issue-3` now*; the only widely-accepted fold-in is `timeout-minutes: 25`. Two items flagged upward, not decided: keystore identity, and the closure ref.

### 5.3 The named twenty (part 2: Thoughtful → Interaction-detail)

All ten seats in this half are **LEAD-COVERED by Fuge** — see §3
(the original cluster was refused before writing; the lenses were
covered by the lead personally, from direct static verification,
and are signed as such there).

## 6 · POST-CLOSURE PACKAGE (recorded, not landed)

For Ren, as ONE separate commit + run after #3 closes (Improver
Step 2): paths filter (`workflow_dispatch` stays unfiltered);
concurrency with `cancel-in-progress: false` for the proof ref;
Gradle caching (setup-gradle vs hand-rolled cache — Ren's call);
job split build/test with JUnit summaries + test-report uploads;
the naming system landing coherently (workflow → `build-and-test`,
file rename with it, per-step verb names, `-UNTESTED` /
`-FAILED-logs` / `-reports` artifact labels with the acceptance
wording amended in the same commit); Signing Canary pre-flight
job + weekly heartbeat (Crazy-ideas, priced in §5.2); SDK-step
failure no longer swallowed; run-citation format fixed as
`run #N · \`sha7\` · id \`run-id\`` everywhere.

## 7 · GITHUB COMMENT — EXACT TEXT (posting status in the lead's final report to Ren)

```text
Wave-2 on #3 — Fuge (Cluster A lead), reporting to Ren.

The cure is LANDED on this branch. .github/workflows/rescue-build.yml
on agent/issue-3 is now Fern's minimal draft as the real workflow:
one added step — "Generate debug keystore (issue #3 fix)" — keytool
generating the standard Android debug key at ${rootDir}/debug.keystore
before Assemble, exactly what debugConfig demands
(app/build.gradle.kts:35-40) and .gitignore:18 has kept out of every
clean checkout. Runs #4-#7 all died at :app:validateSigningDebug for
exactly this; nothing else in the file blocks the bar.

Two fold-ins, both proof-neutral (roster consensus): timeout-minutes
25 (was the 360 platform default), and a plain-words verdict summary
on every run — including, while tests are red, "The uploaded APK is
UNTESTED in this run." Artifact name deliberately unchanged
(monolith-debug): the bar cites it, and a rename mid-proof breaks
comparison with runs #1-#7. Paths filter, concurrency, caching,
job split, renames and the Signing Canary are filed as a
post-closure package in the report — they can suppress or cancel
a proof run, so they do not ride this one.

The push of this commit runs the new workflow on agent/issue-3:
that run is the DRESS REHEARSAL. Bar today: 0/3. Closure still
needs Ren to land the identical workflow on main and cite a main
run — a branch-only cure does not count (wave-1 Finisher 4's
closure spec; Wave-2 Brutal critic + Purpose-questioning concur).

DECISION-FOR-SARTHAK (detail in the report): the CI key is
throwaway per run, so CI APKs cannot update-install over local
builds or each other (uninstall first, data lost). Alternative:
commit the standard debug.keystore — public default password, not
a secret; the release key stays out either way — for one signature
everywhere.

Report: studio/plan/ISSUE-3-WAVE2-REPORT.md (30 seats; ten of them
LEAD-COVERED after a seat cluster was refused — recorded there,
not hidden).

— Fuge
```

## 8 · WHAT FUGE WANTS TO DO NEXT (Cluster A, issue #3 side)

- Read the rehearsal run's verdict against the Closure Evidence
  Spec the moment it lands and post it on this issue in the
  standing format (§5.1 skeleton) — transition-only, per
  Finisher 4's rule.
- Draft the post-closure package (§6) as a ready-to-land commit on
  this branch (DECLARED) so Ren's Step-2 landing is one review,
  not another wave.
- Build the Signing Canary as that package's first item if Ren
  adopts it — it is the cheapest standing guard against this exact
  wall returning.

— Fuge, Cluster A lead, Oct 30 2026 (Wave-2)
