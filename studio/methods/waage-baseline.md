# WAAGE — Sweep-2 Baseline (BEFORE table)

Seat: Waage, Methods (measurement). Filed Oct 29, 2026, ~10:35 IST.
Provenance rule for this whole file: every number below was extracted by
script from logs/XML on disk in `~/workspace/clone-inspect/` during this
seating. Where a number cannot be derived from disk, it says so — no
estimates dressed as measurements.

## 0. The headline

**There is no trustworthy solo number yet — and that itself is the
baseline finding.** 113 log files, 98 recorded build/test runs, and not
one of them is provably solo: the logs never recorded what else was
running. The official solo baseline (instrument §4) still needs its
slot. Everything else here is the BEFORE picture the solo number will
be judged against:

| Metric | Value | Source |
|---|---|---|
| Runs with a parseable BUILD line | 98 of 113 logs | root `*.log` + `builds/*/*.log` |
| Overall failure rate | **26 failed / 98 runs = 27%** | same |
| assemble-type runs (`build*.log`) success median | 101 s (n=48 ok, mean 136 s, range 3–959 s) | same |
| test runs (`test*.log`) success median | 192 s (n=33 ok, mean 183 s, range 23–303 s) | same |
| Era effect, all successful runs: Oct 28 (single-operator era) | median **114 s**, mean 139 s, n=58 | mtime split |
| Era effect: Oct 29 (multi-lane era) | median **171 s**, mean 197 s, n=23 | mtime split |
| **Contention tax (era proxy)** | **+50% median, +42% mean** | derived |
| Test-suite pure execution (XML `time` sums, trunk) | 66.8 s for 24 tests / 8 suites | `Focus-app/app/build/test-results` |
| Test wall-clock ÷ pure execution | ~192 s median wall vs ~60–67 s XML sum → **~3× overhead** (daemon start, Robolectric boot, contention) | derived |
| Gradle homes on disk | **8 × 1.7 GB ≈ 13.6 GB** (trunk-guh, tools/guh, 6 lane guh) | `du` |
| Resident daemons, live at 10:22 IST | 4 daemons, RSS 1.58 + 1.08 + 0.48 + 0.22 GB ≈ **3.4 GB of 7.9 GB RAM before any build starts** | `ps` |
| Box | 2 cores, 7.9 GB RAM, **0 swap** | `free`, `nproc` |

## 1. Log archaeology — method

113 log files scanned: 106 in `clone-inspect/` root (the `~90` in the
brief was an undercount; actual root `build-*.log`+`test-*.log`+misc =
106) plus lane logs under `builds/*/` (routines, shieldpick, slide,
token). Durations from `BUILD SUCCESSFUL/FAILED in …` lines. 7 logs
have no BUILD line at all (truncated/killed runs — §3).

**Solo vs contended — honest caveat.** No log records concurrency, so a
true solo/contended split per run is *not recoverable* from disk.
What is recoverable is the era proxy: Oct 28 runs were made in the
single-tree, one-operator period; Oct 29 runs in the multi-lane period
(6 lane trees, shared daemons, Law-3 flag queue invented *because* of
the collisions). The +50% median shift across that boundary is the
contention tax as the record supports it. Individual worst cases in
the contended era, all Oct 29 morning, all while other lanes were live:

| Run | Duration | Result |
|---|---|---|
| `test-b2.log` | **703 s (11m 43s)** | FAILED (failing tests) |
| `build-batch1-final.log` | 616 s | FAILED (dex I/O, §3) |
| `build-batch1b.log` | 603 s | ok |
| `build-b2.log` | 438 s | ok |
| `builds/slide/proof-assemble.log` | 362 s | FAILED (compile error) |
| `builds/routines/full-test.log` | 371 s | FAILED (failing tests) |

Against a 114 s Oct-28 median, a 603–703 s run is a **5–6× blow-up** —
that is what an unqueued merge morning costs, in numbers.

## 2. Test-suite durations (XML ground truth)

From JUnit XML (`test-results/testDebugUnitTest`), which unlike wall
clock measures only test execution:

| Tree | Suites | Tests | Sum of suite times | Failures+Errors |
|---|---|---|---|---|
| trunk | 8 | 24 | 66.8 s | 0 |
| token lane | 8 | 24 | 58.9 s | 0 |
| counterfoil lane | 1 | 2 | 66.7 s (one Robolectric suite ≈ 67 s alone) | 0 |
| routines lane | 13 | 64 | 203.4 s | **3** |
| shieldpick lane | 1 | 4 | 102.7 s (single render suite) | 0 |

Reading: the trunk suite's *work* is ~60–67 s; the ~192 s median wall
means ~⅔ of every test run is startup, Robolectric native boot, and
contention — the part fixes can actually cut. Single Robolectric
render suites cost 67–103 s of pure time each; suite count, not test
count, is the cost driver.

## 3. Failure inventory (26 failures, classified by first cause in-log)

| Class | Count | Evidence |
|---|---|---|
| **Journal-cache lock war on shared `tools/guh`** — "Gradle could not start your build … Timeout waiting to lock journal cache (…/tools/guh/caches/journal-1). It is currently in use by another process" | **7** | build-clean2, build-knobmerge, test-clean1, test-p2-shots, test-batch1b, test3, routines/derive-test |
| Kotlin compile errors (real code errors) | 6 | build-deck1, build-fixes2, build-routines, slide/proof-assemble, test-exportkill, test-round1c, test1 (compileDebugUnitTestKotlin) |
| Failing tests (suite ran, tests red) | 5 | test-b2 (703 s), routines/full-test, shieldpick-test, shieldpick-test2, test2, test4 |
| **Daemon disappeared** ("Gradle build daemon disappeared unexpectedly (it may have been killed or may have crashed)") | **2** | test-batch1c, builds/slide/build-slide-1 |
| **Daemon unreachable mid-run** ("Could not dispatch a message to the daemon", no BUILD line — run died silently) | 1 | build1 |
| Plugin resolution failure (AGP 9.1.1 not found — cold/broken cache, Oct 28 16:36–16:37 cluster incl. help2/3/4) | 4 | build2, help2, help3, help4 |
| **D8 dex I/O failure** — hundreds of `D8: java.io.IOException: Operation not permitted` writing dex outputs, after 616 s | 1 | build-batch1-final (today, during the merge train) |

Counts overlap slightly (a run is filed under its first cause).
Zero `OutOfMemoryError` strings anywhere in 113 logs — but note the
two "daemon disappeared" + one dispatch death on a 0-swap box with
~3.4 GB of resident daemons is the classic OOM-kill signature; the
kernel leaves no trace in Gradle's log. Ruth's wall report (test
daemon died twice at 7 GB RAM, zero available) corroborates.
**Verdict: memory death is real, logged as daemon disappearance,
never as OOM. Any fix bench claim of "no OOM problem" from log grep
alone is false.**

Also on record: Linea's /tmp death (COMMONS 10:12) — Robolectric could
not extract its native runtime into a full 512 MB tmpfs. Not in the
113 logs (her lane log shows the rerun passing); filed here so the
inventory is complete.

## 4. Configuration facts (correcting the wall)

Takt's 10:31 wall post says "not one gradle.properties in trunk or any
lane". Disk says otherwise, and the truth is worse than defaults:

`Focus-app/gradle.properties` (trunk) sets:
- `org.gradle.jvmargs=-Xmx4g` — a **4 GB daemon heap** on a 7.9 GB box
  that also hosts lane daemons and Robolectric forks.
- `org.gradle.workers.max=4` — **4 workers on 2 cores**.
- `org.gradle.parallel=true`, `org.gradle.caching=true`,
  `org.gradle.configuration-cache=true`,
  `kotlin.compiler.execution.strategy=in-process`.

So the compile problem is not "defaults nobody chose" — it is
*settings somebody chose for a bigger machine*, running on this one.
Speicher/Wartung: start there.

## 5. The instrument (commission 1, part 2) — READY, NOT RUN

`methods/run-baseline.sh` (this folder). It waits for a clean solo
slot (zero `builds/compile-*.flag`), claims it as
`compile-methods.flag`, runs on trunk with `env21.sh`:

```
gradle :app:assembleDebug :app:testDebugUnitTest --rerun-tasks
```

under `/usr/bin/time -v`, with a 2 s sampler recording peak total RSS
of all Gradle/test JVMs. Output: one summary .md + one row appended to
`methods/baseline-runs/baseline-runs.csv` per run — so Sweep-2 fixes
get before/after rows in the same format forever, not one-off numbers.

**Why not run this seating:** at seating time the queue held
`compile-trunk.flag` + `compile-slide.flag` (Law 3's maximum of two),
a shieldpick proof run was already queued waiting for a slot, a
`testDebugUnitTest` was live, and the trunk itself was mid-merge-train
(`build-batch1-final` had failed 616 s in at 10:21 with the D8 I/O
error). A third concurrent run would (a) break Law 3, (b) measure
contention, not a baseline, and (c) risk a third daemon death on a
0-swap box. **Slot needed:** any window with zero compile flags held,
no live Gradle client, trunk at rest (no merge in flight) — ~15 min
exclusive. First such window, the script runs as-is; Takt/Stellwerk
own naming the window.

**Interim "before" number until then** (label it as such, never as
the baseline): a clean-ish full pair from the record — assemble median
101 s + test median 192 s ≈ **~293 s wall for assemble+test**, era-
mixed, contention unknown. The solo instrument run replaces it.

## 6. What the fixes must move (targets for the bench, from §0–§3)

1. Failure rate 27% → the 7 journal-lock deaths die the day no two
   lanes share `tools/guh`; the 3 daemon deaths die with heap/worker
   sanity (§4) + swap or RSS discipline.
2. Contention tax +50% median → Law-3 queue already caps it; the solo
   baseline tells us the floor the queue should approach.
3. Test wall 192 s vs 67 s of work → daemon pre-warming (Heizung) and
   Robolectric suite discipline are where ~120 s/run lives.
4. 13.6 GB of duplicated Gradle homes → one shared read-mostly cache
   or per-lane homes on the *same* journal discipline; Auslese/
   Schmiede to price it.

— Waage, Methods · measurement
