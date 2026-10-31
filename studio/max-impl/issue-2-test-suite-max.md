# ISSUE #2 — MAXIMUM IMPLEMENTATION: A TEST SUITE THAT CAN NEVER SILENTLY ROT

**Seat:** MIP-01 (Maximum-Implementation-Potential, issue #2) — dedicated-100 programme
**Repo / branch:** `Sarthak-2007/Focus-app` · `agent/issue-2`
**State: DECLARED, uncompiled.** Nobody in this seat compiles or runs Gradle.
Ren compiles only on Sarthak's explicit word; CI is the independent witness.
**Job of this file:** take the idea inside issue #2 — *fix the batch-9 test
call-sites* — and work out the **fullest buildable version** of what that idea
is really about: a test suite that can never silently rot against the app
again. Mechanism, cost, and demands on the app/CI, per layer, so Ren can
adopt it whole, in phases, or slice by slice.

---

## 0 · THE MAXIMISED IDEA, IN ONE PARAGRAPH

Batch 9 did not fail because a test was wrong; it failed because
**production moved in two lanes and the test source set froze**, and the
first anyone knew was a wall of `No value passed for parameter` at the next
compile gate — weeks of latency between cause and detection, in a studio
that compiles rarely on purpose. The maximised version therefore does not
"fix the call-sites better"; it **removes every interval in which drift can
exist undetected**. Drift is caught at four moments, in escalating order of
cost: (1) **at write time**, by a sub-5-second static census wired into the
commit itself; (2) **at signature-change time**, because tests stop calling
raw composables and instead call *generated, type-safe fixtures* that are
regenerated from the real production declarations — a signature change
breaks exactly one generated file, visibly, in the same PR that changed the
signature, instead of N stale test files at the next merge; (3) **at PR
time**, by a dedicated test-*compile-only* CI job plus a committed,
diff-reviewed signature manifest, so a stale call-site is a red check on
the PR that caused it, never a surprise at a merge gate; and (4) **at
render time**, by promoting the existing screenshot harness into a golden
render contract over every screen × state × theme, with semantics-tree
snapshots, so the suite rots neither in *what it calls* nor in *what it
sees*. A nightly **drift drill** then proves the detectors themselves still
work, by seeding a fake signature change and requiring every layer to go
red. The suite stops being a snapshot of the app as it was, and becomes a
machine-checked contract against the app as it is.

---

## 1 · GROUND (checked in this clone, Oct 30, 2026 — not from summaries)

| Fact | Value | Source |
|---|---|---|
| The rot, censused | 5 stale `ParkDrawer` call-sites (classes A×2, B×1, C×2) across 4 files; 1 SDK-36 `StatusBarNotification` arity site (D); 2 disputed imports (E) | `studio/plan/ISSUE-2-test-call-sites.md` §2 |
| Production signature that moved | `ParkDrawer`: 15 required params + defaulted `modifier`, `ui/components/ParkDrawer.kt` ~:128–147 | read in clone |
| Test source set | 43 files, 258 `@Test` functions, `app/src/test/java/com/example/` | grep in clone |
| Production surface to guard | 49 main `.kt` files, 45 `@Composable`s, ~650 `fun` declarations | grep in clone |
| Stack | Kotlin 2.2.10, Compose BOM 2024.09.00, Robolectric 4.16.1, Room 2.7.0, compileSdk 36.1 / targetSdk 36 / minSdk 24, JDK 21 in CI | `gradle/libs.versions.toml`, `app/build.gradle.kts`, `.github/workflows/rescue-build.yml` |
| Package / DB | `com.example`; `MonolithDatabase` (`monolith_tactile_db`), **version 12, 11 entities, `exportSchema = false`** | `data/MonolithDatabase.kt` |
| Existing render harness | `ScreenshotHarnessTest`: Robolectric NATIVE graphics, `createAndroidComposeRule<ComponentActivity>`, real VM + in-memory Room, 5 destinations (rail's four stops + PARK via direct `ParkDrawer`) × day/night = 10 PNGs, plus `zzAccentCensus` pixel-law pass | read in clone |
| Existing anti-drift script | `studio/tools/check_test_callsites.py` (wave-2, Fuge): stdlib Python, <5 s, named-arg census vs production signatures + SBN arity + banned-import check; proven on seeded regressions; stated limits: not a compiler, heuristic parsing, positional calls unchecked | read in clone |
| Suite cost anatomy | Trunk suite 66.8 s class-time: render/harness 61%, Robolectric logic 39%, pure JVM 0.04%. Full-suite wall 2m29s–5m03s, median ≈3m10s. **Cost is sandbox construction, not test work** | `studio/methods/test-cost.md` |
| CI today | `rescue-build.yml` on every push: `assembleDebug` then `testDebugUnitTest`, 25-min timeout, JDK 21, keystore generated in-job, truthful verdict summary; APK uploaded *before* tests and labelled UNTESTED if tests fail | read in clone |
| testTags | 72 `testTag` usages already in main sources — a tag vocabulary exists but is stringly-typed at call sites | grep in clone |

**Scope note.** Issue #2 proper is *tests only, no production change, no
defaults added to `ParkDrawer`*. That discipline is correct for the fix and
this spec keeps it for Layer 0. The maximised version deliberately goes
further — Layers 2, 5 and 6 ask small, priced things of production code and
CI. Each such demand is listed explicitly in §9 so nothing smuggles itself
in under the fix's authority.

---

## 2 · DESIGN PRINCIPLE — FOUR MOMENTS, NO SILENT INTERVAL

Rot is a *latency* disease: the signature changed at T₀, the suite found
out at T₀ + one-merge-gate. Every layer below exists to collapse one
interval:

| Moment | Detector | Latency after drift | Layer |
|---|---|---|---|
| While writing / committing | Census v2 in a pre-commit hook + watch mode | seconds | L1 |
| When a signature changes | Generated fixtures + committed signature manifest — the change itself regenerates/breaks the contract in the same diff | same PR | L2 |
| On every PR | Test-compile-only CI job + manifest-diff gate | one CI run (~minutes) | L3 |
| When behaviour/appearance changes | Golden render matrix + semantics snapshots | one harness run | L4, L5 |
| Always | Drift drill + suite-health budgets prove the detectors are alive | nightly | L6 |
| Underneath all of it | Room schema contract (the DB rots the same way signatures do) | same PR | L7 |

No layer trusts another: the census is heuristic, so the compile job
exists; the compile job is sampled by path filters, so the manifest gate
exists; the manifest is human-acknowledged, so the drill exists.

---

## 3 · LAYER 0 — THE FIX ITSELF (issue #2 as filed)

Already specified in `studio/plan/ISSUE-2-test-call-sites.md` and executed
DECLARED by Fuge's cell (wave-1 commit `8a256a4`, wave-2 comments + census
script `afcdd38`): append the missing named pairs neutrally at the 5 sites,
insert the 10th `StatusBarNotification` argument, keep the E-1/E-2 import
deletion flagged UNPROVEN until a compile settles it. This spec changes
nothing there. L0 is the *seed*: every layer below is what stops L0 from
ever being needed again.

**Cost:** already paid (≈ the cell's wave-1 + wave-2). **Remaining:** the
§5 gate of the plan file — Ren's compile on Sarthak's word + Actions
witness.

---

## 4 · LAYER 1 — CENSUS v2: THE WRITE-TIME NET

Upgrade `studio/tools/check_test_callsites.py` from a wave-2 report tool
into the studio's always-on pre-gate.

### 4.1 Mechanism

1. **Positional arity for everything, not just `StatusBarNotification`.**
   v1 checks named-arg calls and one hard-coded constructor. v2 extracts
   *every* production declaration's parameter count + required count and
   checks positional and mixed call-sites in tests for arity, and named
   call-sites for missing-required / unknown-arg (v1 behaviour, kept).
2. **Framework-constructor table, generated not hard-coded.** The D-class
   bug was an SDK arity change frozen into a test. Replace the single
   `SBN_SDK36_ARITY = 10` constant with `studio/contracts/sdk-arity.json`,
   generated by a small JVM/Python tool that reads the *actual*
   `android.jar` for the project's compileSdk (`platforms/android-36.1/`)
   and dumps public constructor arities for every framework class the test
   source set instantiates (discovered by grep, so the table only ever
   contains classes actually used — today: a handful). An SDK bump
   regenerates the table; the diff is the early warning.
3. **Symbol-existence census.** Every production top-level symbol a test
   imports or references must still exist (catches renamed/deleted
   functions — the import class of rot, generalising the E-class banned-
   import check from 2 hard-coded lines to the whole import graph).
4. **Room surface census.** DAO method names + entity field names
   referenced from tests checked against `data/` declarations (the suite's
   Room tests rot against DAO renames exactly as render tests rot against
   composables).
5. **Wiring — three invocation points, same script, same exit code:**
   - **Pre-commit hook** (`studio/tools/hooks/pre-commit`, installed by
     `studio/tools/install-hooks.sh`, copies not symlinks so lanes get a
     frozen copy): runs the census *only when staged files touch
     `app/src/main` signatures or `app/src/test`*; <5 s budget enforced —
     if a future census exceeds it, it must shard, not slow the commit.
   - **Watch mode** (`--watch`, stdlib polling, 1 s): the literal
     write-time catch — an agent or Ren editing a signature sees the red
     line in the terminal before the file is even committed. This is the
     mechanism that moves detection from merge-time to write-time; the
     hook is its enforcement, the watcher is its ergonomics.
   - **CI step** (Layer 3, Job A) — the hook can be skipped (`--no-verify`
     exists); CI cannot.
6. **Self-tests stay mandatory.** v1 earned trust by catching two seeded
   regressions and three of its own parser bugs. v2 ships a
   `studio/tools/fixtures/census/` mini-tree (one clean case + one case
   per rot class A–E) and a `--selftest` mode that must pass before the
   hook/CI will run the real census. A detector that cannot prove it
   detects is decoration.

### 4.2 Cost & risk

- **Files:** 1 script rewritten (~450 → ~700 lines Python, stdlib only),
  1 hook, 1 installer, 1 arity-generator tool (~120 lines), 1 JSON table,
  ~6 fixture files. **No Gradle, no new dependency, no production change.**
- **Time:** ≈ 1 builder-day including fixtures and negative tests.
- **Risk: LOW.** Known limit, stated in v1 and inherited honestly: regex/
  paren parsing is heuristic (lambdas/strings in defaults can mis-split).
  Mitigation is architectural, not hopeful — L1 is the *fast* net; L2/L3
  are the *true* nets. A census false-positive costs a human read; a
  false-negative is caught one layer down.

---

## 5 · LAYER 2 — GENERATED SIGNATURE CONTRACTS & TYPE-SAFE FIXTURES (the core)

This is the layer that changes the *shape* of the problem. Today every
test file hand-writes production signatures at its call-sites, so one
signature change has N stale copies. The maximised version has exactly
**one** hand-maintained copy of each signature — production itself — and
every test reaches production through generated code that is regenerated
from it.

### 5.1 The signature manifest (committed, diff-reviewed)

- A Gradle task `generateSignatureManifest` in a small included build /
  buildSrc plugin (Kotlin, using **kotlin-compiler-embeddable's PSI** —
  the real parser, not regex; this retires L1's heuristic limit for the
  manifest's own data) walks `app/src/main`, and emits
  `studio/contracts/signature-manifest.json`: for every public
  declaration — name, file, kind (composable / function / constructor /
  DAO method), parameters in order with name, type string, has-default,
  and a per-declaration SHA over that tuple.
- The manifest is **committed**. CI regenerates it and fails the PR if the
  committed copy differs — *unless* the PR also updates the manifest, in
  which case the manifest diff **is** the signature-change review surface:
  a reviewer (or Ren) sees "+2 required params on ParkDrawer" as a
  6-line JSON diff, not as a Kotlin error wall three weeks later.
- A one-line `manifest.sha` roll-up goes in the manifest header; the
  nightly drill (§8) and the suite-health report quote it, so "which
  contract generation is this suite written against?" is always answerable
  in one grep. (Today it is answerable only by compiling.)

### 5.2 Generated test fixtures (the call-site killer)

- A second task, `generateTestFixtures`, consumes the same PSI walk and
  emits — into `app/build/generated/source/contracts/test/`, wired as a
  test source dir and a dependency of `compileDebugUnitTestKotlin` — a
  `Fixtures.kt` containing, for each production declaration that tests
  actually call (allowlist derived from the census: today ~30
  declarations, headed by `ParkDrawer`), a **builder-style fixture**:
  ```kotlin
  // GENERATED — do not edit. Regenerate: ./gradlew generateTestFixtures
  class ParkDrawerFixture {
      var visible: Boolean = true
      var snapshot: WatchSnapshot? = null
      // … every parameter, pre-filled with the neutral value declared
      // in the fixture spec (see 5.3) …
      var socialSitesEnabled: Boolean = true   // ← appears automatically
      var onToggleSocialSites: () -> Unit = {} //   the day production adds it
      @Composable fun Compose() = ParkDrawer(visible, snapshot, …)
  }
  ```
- Tests migrate to `ParkDrawerFixture().apply { snapshot = … }.Compose()`
  (or a top-level `parkDrawer(overrides)` convenience emitted alongside).
  From then on, **a production signature change cannot stale a test
  call-site**: the fixture is regenerated in the same build, gains the new
  parameter with its declared neutral default, and every test compiles
  unchanged. The *only* breakage a signature change can still cause is the
  deliberate one: if the new parameter has **no neutral value declared**,
  generation fails with a named error — `ParkDrawer.socialSitesEnabled:
  no fixture default` — which is precisely the human decision issue #2's
  fixers had to make by hand at 5 sites, now demanded once, at the moment
  of the change, from the person making it.
- Non-composable functions get the same treatment where tests call them
  positionally (plain fixture data classes / argument holders), and
  framework constructors get **factory fixtures**: e.g.
  `StatusBarNotificationFactory.create(...)` generated against the
  *installed SDK's* android.jar signatures (Layer 1's arity table, raised
  to full parameter lists) — the D-class bug becomes ungeneratable.

### 5.3 The fixture-defaults spec (the one human file)

`studio/contracts/fixture-defaults.yaml` — per declaration, per parameter:
the neutral value expression, or `REQUIRED` (generation fails without a
test-supplied value). Seeded from issue #2's own fix rationale: toggles
not under test get the persisted default + no-op lambda (`true` / `{}`);
subjects under test are marked `REQUIRED` so no test silently renders
with scaffolding where it meant to assert. ≈40 lines today. This file is
the studio's institutional memory of "what is scaffolding and what is
subject" — the judgement that wave-1 had to re-derive per file.

### 5.4 The no-silent-defaults guardrail (production-facing, priced in §9)

The entire rot began because adding *required* parameters is loud but
adding *defaulted* ones is silent — a defaulted new parameter lets stale
tests compile while rendering a different app than production shows.
Guardrail: a detekt-style custom rule (or a manifest-diff CI check —
cheaper: the manifest records has-default, so CI fails any PR where a
parameter on a fixture-covered declaration *gains a default* without a
`fixture-defaults.yaml` entry acknowledging it). No new tooling beyond
the manifest diff; the rule is a 20-line check over two JSON files.

### 5.5 Cost & risk

- **Files:** 1 buildSrc/included-build plugin (2 tasks, ~600 lines
  Kotlin), `signature-manifest.json` (generated, ~45 composables + called
  functions), `fixture-defaults.yaml` (~40 lines), build wiring in
  `app/build.gradle.kts` (~10 lines), migration of the ~30 test
  call-sites that use covered declarations (the 5 ParkDrawer sites first;
  the rest opportunistically per file as lanes touch them — **not** a
  43-file flag day).
- **Time:** ≈ 3–4 builder-days (plugin + PSI walk + generator + first
  fixtures), + ≈ ½ day migrating the ParkDrawer/render-test cluster.
- **CI cost:** manifest + fixture generation are PSI parses of 49 files —
  seconds-to-a-minute, cached by Gradle; negligible against a 3-minute
  suite.
- **Risk: MEDIUM.** (a) PSI parsing of compose-specific syntax (trailing
  lambdas, default expressions containing lambdas) is the fiddly part —
  contained by generating fixtures only for the allowlist, not all 650
  declarations. (b) Generated-code churn in reviews — contained: fixtures
  are *not* committed (build output), only the manifest is. (c) A wrong
  neutral default silently changing what a test asserts — the Risk-3 scar
  from the issue #2 plan; contained by `REQUIRED` marking for subjects
  and by Layer 4/5 render/semantics goldens, which see the rendered
  result, not the call.

---

## 6 · LAYER 3 — CI: DRIFT BECOMES A RED CHECK ON THE PR THAT CAUSES IT

Today's `rescue-build.yml` runs everything, in sequence, on every push —
so test-compile health is only ever learned at full-suite cost, and the
APK-untested labelling already in the workflow shows the studio knows a
late verdict is a degraded one. Split into staged jobs (same workflow
file, jobs not steps, so failures are fast and parallel):

| Job | Runs | Trigger | Budget |
|---|---|---|---|
| **A · Contract** | census v2 (`--selftest` then census), manifest regen + diff gate, fixture-defaults guardrail (5.4), sdk-arity regen + diff | every PR/push | < 2 min |
| **B · Test-compile** | `./gradlew compileDebugUnitTestKotlin` **only** — no test execution, no APK | every PR/push | ≈ 2–4 min (compile-dominated; cf. test-cost.md §1.4) |
| **C · Suite** | `assembleDebug` + `testDebugUnitTest` (today's flow, verdict summary kept verbatim) | push to `agent/*` and `main`; PRs labelled `full-suite` or touching `app/src/main` | 25-min timeout (existing) |
| **D · Render matrix** | Layer 4 harness, full matrix | nightly + PRs touching `ui/` paths | nightly |

- **Path-filter honesty:** Job C's trigger filter is the dangerous line
  in this design, so it is written conservatively — *any* change under
  `app/src/main` or `app/src/test` or `gradle/` triggers C; only
  docs/studio-only changes skip it. When in doubt, run. The saving comes
  from B being the universal gate (a stale call-site is *compile* rot,
  and B catches all of it) while C's expensive Robolect
...[truncated 5201 chars]