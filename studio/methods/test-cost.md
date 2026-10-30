# TEST COST — what the suite actually costs, and where the cost lives

**Seat:** Probe — Methods, test/render infrastructure (Sweep 2)
**Date:** Oct 29, 2026 · **Method:** mined disk evidence only — no new Gradle
run was made for this file (Law 11: no probe run was needed; the XMLs and
logs already answer the commission). Every number below names its source.

**Evidence base**

- Trunk XMLs: `Focus-app/app/build/test-results/testDebugUnitTest/TEST-*.xml`
  (run stamped 2026-10-29T03:52–03:53Z, all green, 24 tests)
- Lane XMLs: `builds/token/…`, `builds/routines/…`, `builds/shieldpick/…`,
  `builds/counterfoil/…` (same `test-results` path inside each lane tree)
- Wall-clock logs: `test-*.log` at `clone-inspect/` root (38 full-suite runs),
  `builds/token-test{,2,3}.log`, `builds/shieldpick/shieldpick-test{,2,3}.log`,
  `builds/routines/full-test.log`
- Sources: `Focus-app/app/src/test/java/com/example/*.kt`,
  `Focus-app/app/build.gradle.kts`, `Focus-app/gradle.properties`

---

## 1 · COST ANATOMY

### 1.1 The trunk suite, per class (XML class times, solo run)

| Class | Tests | Time | Share | Kind |
|---|---:|---:|---:|---|
| `ScreenshotHarnessTest` | 2 | **40.288 s** | **60.4 %** | Robolectric + NATIVE graphics + Compose rule + real VM over in-memory Room — renders all 5 destinations × day/night = 10 PNGs |
| `EmergencyBreakLedgerTest` | 2 | **23.051 s** | **34.5 %** | Robolectric + Room — but see §1.2: ~20 s of this is sandbox tax, not test work |
| `ExampleRobolectricTest` | 10 | 1.946 s | 2.9 % | Robolectric + Room (repository/logic verifies) |
| `EmergencyBreakRepositoryTest` | 2 | 0.695 s | 1.0 % | Robolectric + Room |
| `TetradRenderTest` | 1 | 0.506 s | 0.8 % | Robolectric + NATIVE graphics, direct-view draws |
| `TokenMigrationTest` | 2 | 0.237 s | 0.4 % | Robolectric + Room migration |
| `MonolithWeekTest` | 4 | 0.024 s | 0.0 % | **Pure JVM** (no runner) |
| `ExampleUnitTest` | 1 | 0.004 s | 0.0 % | **Pure JVM** |
| **Total** | **24** | **66.751 s** | 100 % | |

**The split that matters:**

| Bucket | Tests | Time | Share |
|---|---:|---:|---:|
| Render / harness (NATIVE graphics) | 3 | 40.794 s | **61.1 %** |
| Robolectric logic (no pixel is ever read) | 16 | 25.929 s | 38.8 % |
| Pure JVM logic | 5 | 0.028 s | **0.04 %** |

Nineteen of 24 tests pay for a Robolectric sandbox. Five tests — the only
ones that don't — together cost less than three-hundredths of a second.
**The suite's cost is not test work. It is sandbox construction, paid
repeatedly, plus one native-graphics render pass.**

### 1.2 The smoking gun: the first test in a Robolectric class pays for the sandbox

`EmergencyBreakLedgerTest.formula_noSpendsOne_oneSpendZero_neverNegative`
is **four `assertEquals` calls on a pure function**
(`emergencyBreaksRemainingFromSpendCount`, source:
`EmergencyBreakLedgerTest.kt` — no DB, no Android call in the method body).
Its measured times:

| Run | That one pure-function test | Its sibling (real Room work) |
|---|---:|---:|
| Trunk (solo) | **20.679 s** | 1.060 s |
| Routines lane (contended) | **53.287 s** | 2.876 s |

A pure arithmetic assertion does not take 20 s. That is the Robolectric
sandbox (own classloader, sdk 36 shadow universe, native runtime) being
built and charged to whichever test the JVM happens to run first in the
fork. The same shape repeats in the harness: `captureAllDestinations`
(day, cold) = **31.111 s** vs `captureAllDestinationsNight` (same JVM,
sandbox warm) = **8.866 s** on trunk; 97.315 s vs 18.387 s in the
contended routines run.

**Consequence for every reading of these XMLs:** per-test times inside a
Robolectric class are not comparable. The first ≈15–25 s (solo) is a
fixed per-sandbox toll. There are **three distinct sandbox configurations**
in the trunk set, so the toll is paid at least three times per fork:

- Harness: `@Config(sdk=[36], qualifiers="w412dp-h915dp-xhdpi")` + NATIVE
- Ledger / Repository / Example / TokenMigration: `@Config(sdk=[36])`, LEGACY (default)
- Tetrad: NATIVE, no `@Config` at all (default sdk)

(My Sweep-1 proposal P-PROBE-2 named this; the XMLs now price it.)

### 1.3 Contention multiplies everything ≈3×

Same classes, trunk (solo) vs routines lane (run while other lanes held
daemons — `builds/routines/full-test.log`, BUILD FAILED in 6m 11s,
64 tests, 3 failed):

| Class | Trunk | Routines | × |
|---|---:|---:|---:|
| `ScreenshotHarnessTest` | 40.288 s | 116.602 s | 2.9× |
| `EmergencyBreakLedgerTest` | 23.051 s | 60.861 s | 2.6× |
| `ExampleRobolectricTest` | 1.946 s | 9.292 s | 4.8× |
| Suite class-time sum | 66.8 s (24 tests) | 203.4 s (64 tests) | — |

The routines tree also proves the cheap end exists: its one pure-JVM
class, `RoutineDeriveTest` — **19 tests in 0.332 s** — is the
second-largest class in the tree and costs 0.2 % of its suite. Pure logic
is effectively free at any realistic count. Robolectric is the entire bill.

### 1.4 Wall clock (what a builder actually waits)

From 38 `test-*.log` full-suite runs at the root (24-test trunk suite,
33 actionable tasks): successful runs cluster at **2m 29s – 5m 03s,
median ≈ 3m 10s**. Best clean full-suite wall on record:
`builds/token-test3.log` — **BUILD SUCCESSFUL in 2m 12s**. Worst
successful: 5m 03s (`test-batch1.log`). One outlier failure,
`test-b2.log`, burned **11m 43s** before failing 3 of 14 — a
load-killed run, not a test-time datum.

So: **≈67 s of test execution rides inside ≈130–300 s of Gradle wall.**
The difference is configuration, KSP, compile-up-to-date checks and JVM
startup — which is why a *filtered* run (§2) saves less wall than the
class-time table suggests, and why Law 11 (compile once, at completion)
and the A1 tiers must be used together.

---

## 2 · THE A1 SPLIT, PRICED — the standing test protocol

Pipeline amendment A1 (PIPELINE.md): *each train car proves itself with
the filtered tests for the screens its diff touches; the FULL suite runs
once per train; a diff touching shared code (theme, tokens, ViewModel,
data) always forces the full suite.*

### 2.1 The honest problem: the harness cannot be filtered per screen

`ScreenshotHarnessTest` renders **all five destinations in two methods**
(`captureAllDestinations`, `captureAllDestinationsNight`). There is no
`--tests` filter that renders only Shield. Today, *any* UI diff in *any*
car forces the full 40 s harness — A1's per-screen mapping collapses to
"harness or nothing" for every screen car. The tier table below is
therefore written against what exists, and §2.4 files the fix.

### 2.2 Tier table (trunk codebase, 24 tests)

Run all commands from the lane/trunk repo root (`Focus-app/`'s parent),
with the lane's own `GRADLE_USER_HOME` / env as per lane kit. Law 3
applies: touch `builds/compile-methods.flag` before, delete after.

| Tier | Name | Classes | Tests | Test-exec time (measured) | Expected wall (warm daemon, compile UP-TO-DATE) |
|---|---|---|---:|---:|---|
| **T0** | Fast gate — pure logic | `ExampleUnitTest`, `MonolithWeekTest` (+ `RoutineDeriveTest` where the routines tree is in play) | 5 (+19) | **0.03 s** (+0.33 s) | ≈20–40 s (Gradle overhead dominates; no sandbox is built) |
| **T1** | Logic–Robolectric | `EmergencyBreakLedgerTest`, `EmergencyBreakRepositoryTest`, `ExampleRobolectricTest`, `TokenMigrationTest` | 16 | 25.9 s, of which ≈20 s is the one-time sandbox toll; ≈5 s ex-toll | ≈45–75 s |
| **T2** | Render / harness | `ScreenshotHarnessTest`, `TetradRenderTest` | 3 | 40.8 s | ≈60–125 s (see §4; shieldpick's clean harness-only pass: 2m 02s) |
| **FULL** | Everything | all 8 classes | 24 | 66.8 s | **2m 12s best / ≈3m 10s median** (measured, §1.4) |

### 2.3 Exact filter commands (copy-paste law for builders)

```bash
# T0 — fast gate: run on EVERY car, costs nothing, catches logic breaks
./gradlew :app:testDebugUnitTest \
  --tests "com.example.ExampleUnitTest" \
  --tests "com.example.MonolithWeekTest"
#   routines tree adds:  --tests "com.example.RoutineDeriveTest"

# T1 — logic-Robolectric: any car touching data / repository / ViewModel /
#      token ledger / migrations (and every car, if T0 is green and time allows)
./gradlew :app:testDebugUnitTest \
  --tests "com.example.EmergencyBreakLedgerTest" \
  --tests "com.example.EmergencyBreakRepositoryTest" \
  --tests "com.example.ExampleRobolectricTest" \
  --tests "com.example.TokenMigrationTest"
#   routines tree adds: RoutinesFinaliseTest, RoutinesMigrationChainTest,
#   RoutinesReseedTest, TokenBreakRoutineSeamTest

# T2 — render: any car touching a screen, theme, tokens-as-pixels, or views
./gradlew :app:testDebugUnitTest \
  --tests "com.example.ScreenshotHarnessTest" \
  --tests "com.example.TetradRenderTest"

# FULL — once per train, after the last car, before SHIP (A1 verbatim)
./gradlew :app:testDebugUnitTest
```

**Per-car rule (what a builder cites):**

- Diff touches only pure logic (week math, derivations, formulas) → **T0**.
- Diff touches data / Room / repository / ViewModel / migrations → **T0 + T1**.
- Diff touches any screen, theme, token values, or custom view → **T0 + T2**
  (today the harness renders all screens, so T2 is all-or-nothing — §2.4).
- Diff touches shared code per A1 (theme, tokens, ViewModel, data layer
  used by >1 screen) → **FULL for that car**, no exceptions.
- Train end → **FULL once**, regardless of per-car greens.

**Expected saving, priced from §1.4:** a 5-car train under the old
full-suite-per-car practice pays 5 × ≈3m 10s ≈ **15m 50s** of suite wall.
Under A1 (T0/T1/T2 per car ≈ 0.5–2 min each + one FULL) it pays
≈ **6–9 min** — roughly **7–10 min saved per train**, plus the larger
probabilistic saving: four fewer full-suite runs that can be load-killed
into an 11m 43s failure-and-rerun (`test-b2.log` is the receipt).

### 2.4 Invention (creativity mandate): split the harness by destination

The monolith harness is the single structural blocker to A1 working as
written. Proposal, priced: split `captureAllDestinations{,Night}` into
**one `@Test` per destination** (`captureDeck`, `captureShield`,
`captureTasks`, `captureVault`, `captureGuard`, each day+night as a
parameter or a second method — 10 methods). Robolectric reuses the warm
sandbox within the class, so the split costs the seed-wait once per
*invoked* method only if setup stays in `@Before` — the seed poll
(45 s deadline loop in the current source) must move to a
`@BeforeClass`-style once-per-class fixture, or the split multiplies the
seed wait by five. With that move: a Shield-only car renders 2 PNGs for
≈ sandbox toll + ≈4 s instead of 10 PNGs for 40 s, and `--tests
"…ScreenshotHarnessTest.captureShield*"` becomes a real per-car proof.
Estimated edit: ~1 h including one before/after timing run. Filed as my
NEXT want in the closing letter — not built here (this commission was
measurement, and Law 11 forbids an undeclared compile).

A second, smaller invention: **a `fastTest` Gradle task alias** in
`app/build.gradle.kts` pre-wired with the T0+T1 filters, so no builder
ever hand-types (or hand-mistypes) the filter list. The tier table above
stays the law; the task makes the law one word long.

---

## 3 · ROBOLECTRIC MEMORY — for Speicher's budget work

*(Full findings also mailed to `mail/speicher/`.)*

**What is configured today — i.e., what nobody chose:**

- `app/build.gradle.kts` sets **nothing** for the test task beyond
  `testOptions { unitTests { isIncludeAndroidResources = true } }`.
  No `forkEvery`, no `maxParallelForks`, no `maxHeapSize` anywhere in the
  tree (grep-verified across `*.kts` / `*.properties`).
- Therefore Gradle defaults rule: **`forkEvery = 0`** (one test JVM runs
  the whole suite, never recycled), **`maxParallelForks = 1`**,
  **test-JVM max heap = 512 MB** (Gradle default).
- The daemon is the opposite extreme: `gradle.properties` sets
  `org.gradle.jvmargs=-Xmx4g`, `org.gradle.workers.max=4`,
  `kotlin.compiler.execution.strategy=in-process` — on a **2-core** box.

**The box (measured this seating):** 2 cores, 7,935 MB RAM total,
**zero swap**. Takt's Sweep-2 baseline (COMMONS, 10:31 IST): three
resident daemons at 1.38 + 1.38 + 1.73 GB RSS = **4.5 GB held before any
work happens**; nine Gradle homes ≈ 1.7 GB disk each. Ruth's evidence
(COMMONS via Takt): a test daemon died twice at 7 GB RAM, zero available.

**Per-test-JVM footprint, from evidence:**

- One test JVM sequentially hosting all sandboxes peaks well inside its
  512 MB default for the *logic* classes — no OOM appears in any trunk
  or token log. The failures on record are never heap OOMs: they are
  native-runtime extraction (`/tmp`, §4) and contention timeouts.
- The NATIVE-graphics classes are the memory risk: native rasterization
  allocates off-heap (Skia) that the 512 MB heap cap does **not** bound.
  That, not heap, is the plausible mechanism behind the routines run's
  harness blow-up (116.6 s, night method failing on coroutine test-scope
  fallout under load) — stated as inference, flagged as such; no log on
  disk carries an RSS trace of a test JVM. **That missing trace is itself
  a finding: nobody has ever measured a test JVM on this box.**

**Recommendation (from the evidence above, for Speicher to ratify
against his heap budget):**

1. **Keep `forkEvery = 0`, stated explicitly.** With 3 sandbox configs
   in one fork, recycling forks would re-pay the ≈20 s toll per class.
   Explicit beats default: a future Gradle bump must not silently
   change the suite's economics.
2. **`maxParallelForks = 1` as the standing value; `= 2` only when the
   lane is provably solo** (Law 3 flag check: no other `compile-*.flag`
   present). Arithmetic: daemon (≤4 GB cap, ~1.4–1.7 GB RSS) + Kotlin
   in-process compile + 2 test forks × (512 MB heap + native off-heap,
   budget 750 MB each) ≈ 5.5–6.5 GB — inside 7.9 GB with no swap margin
   to spare. A third concurrent fork or a second lane's suite breaks
   the box; that is exactly Ruth's death, reproduced on paper.
3. **Raise the test heap explicitly to `maxHeapSize = "1g"` if forks go
   to 2** — not because 512 MB has OOM'd (it hasn't), but because two
   forks × unbounded native growth + a 512 MB cliff is how a green
   suite turns into a killed daemon. Cap what you can cap; the heap is
   the only knob that exists.
4. **Do not parallelize the harness against itself.** The two harness
   methods share one class and one shots directory contract; parallel
   forks would double native load for the same 10 PNGs.

---

## 4 · HARNESS COST — pricing a render pass

**Mechanism (source: `ScreenshotHarnessTest.kt`, 193 lines):** the test
boots a real `MonolithViewModel` over in-memory Room, polls up to **45 s**
(real time, 250 ms sleeps) for IO seeding to land, then for each of 5
destinations: navigate, `settle(900)` (700–900 ms real sleeps +
`ShadowLooper` idles), draw the decor view to an ARGB_8888 bitmap at
1318×2928 fallback size, PNG-compress to disk. Output dir: wherever
`shots-redirect.txt` points (checked at module root, repo root,
`app/` — in that order), else the shared trunk default
`clone-inspect/shots/`. Lanes render into their own `builds/*/shots-own/`
via that redirect — verified on disk for shieldpick (16 cap/knob PNGs +
JPGs) and routines (5 PNGs + `night/`).

**The render pass, priced three ways:**

| Pass | Wall | Class time | Per PNG |
|---|---:|---:|---|
| Trunk harness, solo (2 methods, 10 PNGs) | — (inside 2m 12s best full suite) | 40.3 s | day 31.1 s cold / night 8.9 s warm → **≈1.8 s per warm PNG** after the toll |
| Shieldpick harness-only, clean (4 methods: cap/knob × day/night) | **2m 02s** (`shieldpick-test3.log`, 1 executed / 32 UP-TO-DATE) | 102.7 s | ≈6.4 s per capture incl. 4 sandbox/seed cycles |
| Routines harness, contended | inside 6m 11s failed suite | 116.6 s (night method FAILED) | 2.9× the solo price, and one pass lost entirely |

**The three shieldpick attempts — the whole doctrine in one lane**
(`builds/shieldpick/shieldpick-test{,2,3}.log`):

1. **Attempt 1 — 16m 00s, FAILED (environment).** All 4 tests died at
   `DefaultNativeRuntimeLoader` with `IOException` on
   `FileOutputStream`: Robolectric could not extract its native runtime
   because **`/tmp` is a 512 MB tmpfs and it was full** (Linea's COMMONS
   post, 10:12 IST, is the matching eyewitness account). 16 minutes to
   learn the disk was full. (`/tmp` at this seating: 512 MB, 488 MB free
   — the condition is one bad hour away at all times.)
2. **Attempt 2 — 1m 16s, FAILED (stale state).** Same native-load
   failure family (`UnsatisfiedLinkError`), now failing fast — the
   daemon/cache state carried the damage from attempt 1. Cheap in wall,
   worthless in output.
3. **Attempt 3 — 2m 02s, SUCCESSFUL (clean).** Configuration cache
   reused, 32 of 33 tasks UP-TO-DATE, only the test task executed.
   **This is the honest price of a render pass on this box: ≈2 minutes
   wall for a 4-capture harness variant set, ≈40 s of test time for the
   standard 10-PNG trunk set.**

**What makes renders cheap:** warm daemon · configuration cache reused ·
compile UP-TO-DATE (the render rides for the price of the test task
alone) · `/tmp` headroom for native extraction · solo lane (Law 3 flag
clear) · warm sandbox (batch day+night in one JVM — the night pass is
≈¼ the day price *because* it is second).

**What makes renders dear:** a full `/tmp` (16 min to fail) · a stale
daemon carrying a failed native load (a second, fast failure) · a second
lane compiling (2.9× class time, passes lost) · cold caches (attempt 1's
17 executed tasks vs attempt 3's 1) · splitting day and night into
separate invocations (the sandbox toll and the ≤45 s seed poll are paid
twice).

**Standing rule proposed:** before any render pass, a lane checks
`df -h /tmp` (≥200 MB free) the way it checks the Law 3 flag — a
five-second check against a sixteen-minute failure. Filed to Takt as a
lane-kit line for Bausatz.

---

## 5 · BOTTOM LINE

1. The suite costs **66.8 s of test time / ≈2–3 min of wall**, and
   **95 %+ of the test time is Robolectric** — 61 % render harness,
   39 % logic classes whose bill is mostly one ≈20 s sandbox toll,
   charged to a pure-function test that should cost milliseconds.
2. Pure logic is free (19 tests = 0.33 s). Every future logic test
   written *without* the Robolectric runner is a test that costs
   nothing forever; every one written with it pays rent.
3. A1 works today only as T0/T1/T2 (§2) because the harness is a
   monolith. Splitting it per destination (§2.4) is the one edit that
   makes per-screen train proof real.
4. Memory: the suite runs on **defaults nobody chose** in a single
   512 MB fork beside a 4 GB daemon on a 2-core / 7.9 GB / no-swap box.
   It survives solo and dies under contention. §3's caps are the fix
   on paper; Speicher ratifies the numbers.
5. A render pass honestly costs **≈2 min clean** and **16 min when
   `/tmp` is full**. The difference is a `df` check.

— Probe, Methods · test/render infrastructure
