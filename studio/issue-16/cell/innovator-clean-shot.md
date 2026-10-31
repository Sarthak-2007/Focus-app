# Issue #16 — Pill lane: One Clean Shot for the 18 Verdicts
**Author:** Innovator, Pastille's cell (issue #16) — design only. No compiling, no test runs, no Gradle executed for this note.
**Inspected (read-only):** `builds/pill/test.sh`, `builds/pill/build.sh`, `builds/pill/test.log`, `builds/pill/assemble.log`, `Focus-app/gradle.properties`, `Focus-app/app/build.gradle.kts`, both pill test sources, `clone-inspect/env.sh` + `env21.sh`.
**Date:** 2026-10-30

---

## 0. Ground truth the design has to fit

1. **The 18 are exactly two classes, not the suite.**
   - `PillDerivationTest` — **15 tests**, plain JUnit, no runner, no Android, no DB. Pure function (`PillDerivation.derive`) over hand-built entities. Cheap, fast, deterministic.
   - `PillLeaseRepositoryTest` — **3 tests**, `@RunWith(RobolectricTestRunner) @Config(sdk=[36])`, real Room DB via `MonolithDatabase.getInstance`, `runBlocking`. Heavy JVM, shared sandbox DB per class (test packages are unique per test by design — see class doc).
   - 15 + 3 = 18. Every other class in the lane (33 files, **204 `@Test` total**) already has / does not need this verdict.
2. **`test.sh` as written cannot succeed today, for a non-test reason.** It calls
   `~/workspace/clone-inspect/design-refs/swarm/tools/queue.sh` — that tree was deleted 2026-10-30 (~03:00, Commons/Mailroom retirement). `bash <missing> acquire` → `ACQUIRE FAILED` / exit 3 before Gradle is ever reached. Any healthy-window run launched with the current script wastes the window on a known-dead path.
3. **`test.sh` runs the wrong scope in the wrong shape:** one invocation, `gradle :app:testDebugUnitTest` — all 204 tests, one daemon, one `test.log` opened with `>` (destroys the previous attempt's evidence), verdict = a single stdout line `PILL_TEST_DONE test=$T` that is **lost if the shell/box dies**, and an exit code that cannot distinguish *test failed* from *daemon vanished*.
4. **The last death is on record** (`test.log`): compilation completed, `:app:testDebugUnitTest` started, then `Gradle build daemon disappeared unexpectedly` (daemon pid 53253). Zero test XML was harvested. That is the failure mode to design against — death **after** the expensive work, **before** any verdict exists.
5. **Environment physics (already established in this studio, not re-litigated here):** 2 cores / 7,935 MB, no swap, `gradle.properties` already carries the fixed regime (`-Xmx2g`, `workers.max=2`, in-process Kotlin compiler, config cache on). `assembleDebug` took **17m 43s** when it went green — exposure time is the enemy. A warm daemon is an asset; `test.sh` unconditionally does `gradle --stop; sleep 4`, discarding it and paying a cold daemon + config-cache re-entry for nothing.
6. **Gradle deletes/regenerates `app/build/test-results/testDebugUnitTest/` on each test run.** Results not copied out *between* runs do not survive the next run. This single fact dictates the shard-and-harvest shape below.

**Design principle:** the re-run must be *resumable, partial-credit, and self-evidencing* — any prefix of it that completes is a banked verdict, and any death leaves behind enough to name its own cause. Nothing below changes a test, a source file, or `gradle.properties`.

---

## Proposal 1 — SHARD-4: split the 18 into 4 independently-runnable shards, scoped with `--tests` [TOP]

**Spec.** Replace the single full-suite invocation with a driver loop over a fixed shard table, each shard a separate Gradle invocation using JUnit method-level `--tests` filters (supported by the existing `testDebugUnitTest` task; no build-file change):

| Shard | Contents | Tests | Character |
|---|---|---|---|
| `D1-absent` | `PillDerivationTest.noSessionIsAbsent`, `inactiveSessionIsAbsent`, `ownPackageIsAbsent`, `telecomForegroundIsAbsent`, `keyguardIsAbsent` | 5 | Pure JVM |
| `D2-time` | `breakOutranksPeek`, `breakRemainingEqualsTheHouseDerivationAtEverySample`, `stopwatchShowsElapsedFromTheStoredPair`, `timedShowsNowFrameRemainingAndBlock`, `unstampedTickCreditsNothingBeyondTheStoredFact`, `remainingIncludesThePenalty` | 6 | Pure JVM |
| `D3-peek` | `peekFaceNamesTheRowAndDiesExactlyAtItsEnd`, `peekFaceFollowsHimWhereverHeWanders`, `twoLiveLeasesResolveToTheLaterEnd`, `faceEqualsStoredEndsAtEverySampledInstant` | 4 | Pure JVM |
| `L-lease` | `PillLeaseRepositoryTest` (whole class, deliberately unsplit) | 3 | Robolectric + Room |

Why `L-lease` stays whole: Robolectric startup (sandbox, SDK 36 shadow classpath) dominates its cost — splitting 3 tests into 3 invocations pays that startup 3× and triples daemon-death exposure for the heaviest shard. The class was also written against a shared sandbox DB with per-test unique packages; keeping Gradle's normal in-class execution preserves exactly the conditions the tests were authored for. Derivation shards are grouped by *subject* (absence / time maths / peek face), not arbitrarily, so a shard failure is already a localised diagnosis.

Also scope-cut the run: **do not run the other 186 tests.** They are not the missing verdict, and running them multiplies wall-time and RAM pressure roughly an order of magnitude for zero information. If Ren wants a full-suite confirmation later, that is a separate, post-merge decision — not a rider on this window.

Driver sketch (draft, for Ren to adopt/adapt — not executed here):

```bash
# shards.txt: shard_id <TAB> --tests args...
# run-shard loop (inside test2.sh, Proposal 5):
while IFS=$'\t' read -r shard filters; do
  [ -f "$RUN_DIR/$shard.done" ] && continue          # resume: banked shards skipped
  gradle :app:testDebugUnitTest --console=plain $filters \
      > "$RUN_DIR/$shard.log" 2>&1; echo $? > "$RUN_DIR/$shard.exit"
  harvest "$shard"                                    # Proposal 2 — immediately, before next shard
done < shards.txt
```

**Cost:** ~35 lines (shard table + loop). ~20 min to write/review. Run overhead: 3 extra Gradle invocations — each pays daemon/task-graph/config-cache re-entry, est. +1–3 min total on a warm daemon, against a 17-minute-class monolith. **Risk: low.** Method filters are exact-name; a typo'd filter yields Gradle's "No tests found" failure, which Proposal 4 classifies as `NO_RESULTS`, not a test failure — and the pre-flight (Proposal 3) includes a filter-vs-source cross-check that catches typos before the window.

---

## Proposal 2 — Harvest-and-ledger: verdict capture that survives a mid-run death [TOP]

**Spec.** Two artefacts, both written **after every shard, before the next shard starts**:

1. **XML harvest.** Copy `app/build/test-results/testDebugUnitTest/*.xml` for that shard into `$RUN_DIR/results/$shard/` (run dir = `builds/pill/runs/<yyyyMMdd-HHmmss>/`). Necessary because the next Gradle run wipes that directory (ground truth §6). Also copy the shard's `.log` tail and `.exit` alongside.
2. **Append-only verdict ledger** `$RUN_DIR/ledger.tsv`, one line per test, appended + `sync`ed per shard:

```tsv
# run_id	shard	test_class	test_method	verdict	failures	errors	skipped	duration_s	xml_sha256
20261030-041500	D1-absent	com.example.PillDerivationTest	noSessionIsAbsent	PASS	0	0	0	0.012	9f2c…
```

   Write pattern per shard: write `ledger.tmp`, `fsync`, append to `ledger.tsv`, `fsync` the file. A `.done` marker per shard is created by atomic rename (`$shard.done.tmp` → `$shard.done`) **only after** its XML is harvested and its ledger lines are synced. `.done` existence is therefore a trustworthy resume predicate (Proposal 1's loop) and a trustworthy progress readout for anyone watching: `ls runs/latest/*.done | wc -l` = shards banked, out of 4.

Verdict is read **from the XML** (`tests/failures/errors/skipped` attributes + per-`<testcase>` entries), never inferred from Gradle's exit code alone. Exit code is recorded as corroboration, not as the verdict.

**Cost:** ~45 lines (harvest function + a ~25-line Python-stdlib `xml.etree` summariser; Python is already on the box). ~25 min. Run overhead: <5 s per shard. **Risk: low.** Only new failure mode is disk-full on copy — which the pre-flight disk check (Proposal 3) screens, and which would be caught as a missing `.done`, i.e. fail-closed toward `NO_VERDICT`, never toward a false PASS.

---

## Proposal 3 — Pre-flight gate: refuse to burn the healthy window on a dead environment [TOP]

**Spec.** A standalone `preflight.sh` (draft below in outline; run by Ren *before* the re-run, and re-run cheaply at the head of `test2.sh`). Every check is pass/fail with a distinct exit code; **any fail = do not start**, because starting into a sick environment is how the previous 3 attempts died. Checks, in order:

| # | Check | How | Catches |
|---|---|---|---|
| 1 | Queue tool exists & is executable | `test -x "$Q"` — **currently FAILS** (deleted swarm tree) | Today's certain exit-3 waste |
| 2 | JDK21 + Gradle present | `test -x $JDK21/bin/java`, `command -v gradle` after sourcing `env21.sh`; print `java -version` into the run dir | Wrong-JDK daemon mismatch |
| 3 | Sustained write probe | Write 8 × 1 MB files + a JVM-shaped write (create/delete 200 small files) in `$LANE/.probe/`, `sync`, read back, delete | The write-wall class: single writes passing while real builds die |
| 4 | RAM headroom | `available` from `free -m` ≥ 2,500 MB; count resident Gradle daemons (`pgrep -f GradleDaemon`) ≤ 1 foreign daemon | OOM daemon disappearance (the recorded death) |
| 5 | Disk headroom | `df` free on workspace ≥ 5 GB | Harvest/XML copy failure mid-run |
| 6 | Lane state clean | `guh/` writable; no stale `*.lock` held by a dead pid in `guh/`; previous `test.log` preserved to run dir (never overwritten) | Stale-lock hangs; evidence loss |
| 7 | Filter sanity | Every `--tests` name in `shards.txt` grep-matches a `fun <name>(` in the two pill test sources | Typos → "No tests found" mid-window |
| 8 | Daemon posture | `gradle --status` recorded; if a live JDK21 daemon for this `guh` exists, **reuse it — do not `--stop`** (see Proposal 5) | Self-inflicted cold start |

Output: one line per check (`PREFLIGHT 3/8 RAM available=3120MB PASS`) into `$RUN_DIR/preflight.txt`, final line `PREFLIGHT PASS 8/8` or `PREFLIGHT FAIL check=<n>`. This file alone lets a post-mortem separate "should never have started" from "died mid-run".

**Cost:** ~70 lines bash. ~30 min to write/review. Run cost: ~10–20 s. **Risk: very low** — it only reads and probes its own scratch dir; the one judgement call is the 2,500 MB RAM threshold (tunable by Ren; derivation shards would pass on far less, the Robolectric shard is the one that needs the headroom).

---

## Proposal 4 — Verdict taxonomy: make the outcome unambiguous

**Spec.** Ban the bare `TEST_EXIT=$T`. The run ends with exactly one machine-readable `VERDICT.md` + `verdict.json` in the run dir, generated by the Proposal 2 summariser, using this classification per test and per run:

- Per test: `PASS` (XML testcase, no failure/error) · `FAIL` (XML `<failure>`) · `ERROR` (XML `<error>`) · `NO_VERDICT` (no XML entry — shard never ran, died, or was filtered out). **`NO_VERDICT` is a first-class, printable outcome, never silently merged into FAIL.**
- Per run: `COMPLETE` iff all 18 have PASS/FAIL/ERROR from XML **and** the XML totals reconcile (`tests=18` across harvested shards, names match the expected-18 manifest exactly — a checked-in `expected-18.txt`, one FQ method per line) · otherwise `PARTIAL <k>/18` with the missing names listed.
- Infrastructure deaths are named, not numbered: the driver greps each shard log for `daemon disappeared` → `INFRA_DAEMON_GONE`, `OutOfMemoryError` / `unable to create new native thread` → `INFRA_RAM`, "No tests found" → `INFRA_FILTER`, and stamps the shard (not the tests) with it. Tests in an infra-killed shard stay `NO_VERDICT (shard: INFRA_…)`.

`VERDICT.md` renders the 18 as a table (name, shard, verdict, duration) plus the run line — the exact artefact Pastille pastes into issue #16. Reviewer never has to read a Gradle log to know what was proved.

**Cost:** folded into Proposal 2's summariser (+~20 lines) + `expected-18.txt` (18 lines, generated once from the sources by grep). ~15 min. **Risk: none** to execution; it only constrains interpretation — deliberately, so a fourth environment death reads as "PARTIAL 11/18, INFRA_DAEMON_GONE in L-lease" instead of another total loss.

---

## Proposal 5 — `test2.sh`: fix the two self-inflicted wounds in `test.sh`

**Spec.** A successor script (keep `test.sh` untouched as evidence) that:
1. Takes the queue path as a parameter with an existence check, defaulting to whatever queue tool Ren designates post-swarm-retirement — or, if the lane is run solo in the healthy window, makes queue acquisition *optional-but-logged* (`QUEUE=none` recorded in preflight) rather than a hard death on a deleted path. Ren's call; the inventor's point is only that a deleted coordination tool must not be a single point of failure for a solo verification run.
2. **Deletes the unconditional `gradle --stop; sleep 4`.** Replaced by Proposal 3 check 8: reuse a healthy same-JDK, same-`guh` daemon; stop only a daemon whose JVM version mismatches `env21.sh`, and log which branch was taken. (The JDK-switch stop discipline in the original header comment was for `build.sh`'s 17→21 sequence; `test.sh` is 21-only, so the stop buys nothing.)
3. Never overwrites: all outputs under `runs/<run-id>/`, with a `runs/latest` symlink. `test.log` at lane root is left as the historical record of the daemon death.

**Cost:** ~20 lines changed vs `test.sh`. ~15 min. **Risk: low** — retaining a daemon is the studio's own standing recipe ("keep a born daemon warm"); the mismatch-stop branch preserves the original safety intent.

---

## Proposal 6 — Heartbeat sidecar: a death certificate, written while dying is still possible

**Spec.** `test2.sh` backgrounds a 15-second loop for its own lifetime: `date, free-available-MB, daemon-pid-alive?, df-free` appended to `$RUN_DIR/heartbeat.log` with a `sync` every 4th beat; killed by a trap on driver exit. If the box dies, the last beats show the RAM slope into the death; if only the daemon dies, heartbeat continues and timestamps it against the shard log. This converts the next failure (if any) from archaeology into reading the last page.

**Cost:** ~12 lines. ~10 min. Run overhead: negligible (<0.1% CPU, ~50 KB log). **Risk: negligible** — read-only probes, own log file; trap cleanup prevents orphan loops (plus a self-limit: exit after 90 min regardless).

---

## Proposal 7 — Compile as shard 0, so compile death ≠ test death

**Spec.** First loop entry `C0-compile`: `gradle :app:compileDebugUnitTestKotlin` (plus its harvest = exit code + log + a `.done`). The recorded death occurred *at* the test task after compilation had already succeeded inside the same invocation — under the monolith that distinction was invisible. As its own shard: if C0 banks, every later death is provably a *runtime* death, and resumed runs skip straight to tests on the warm cache. If C0 itself dies, no test shard is attempted and the run is honestly `PARTIAL 0/18 (compile shard: INFRA_…)`.

**Cost:** 1 line in `shards.txt` + the loop's existing machinery. ~5 min. Run overhead: negative on resume (skips recompilation checks per shard are already incremental). **Risk: none.**

---

## Recommended package for Ren's healthy window

Adopt **1 + 2 + 3 + 4** as one piece (`test2.sh` ≈ 150 lines total incl. preflight and summariser, ~1.5 h to write and paper-review in-cell, zero compile/test by the cell), with **5** folded into it, **7** as a one-line addition, and **6** if the extra 10 minutes is available. Sequence in the window:

```
preflight.sh            → PREFLIGHT PASS 8/8   (else: stop, window not spent)
test2.sh                → C0 → D1 → D2 → D3 → L, harvest+ledger after each
summarise (automatic)   → VERDICT.md: COMPLETE 18/18  |  PARTIAL k/18 + named infra cause
resume (if PARTIAL)     → re-run test2.sh; .done shards skipped, only NO_VERDICT shards retry
```

Worst case under this design is strictly better than the current best case: a fourth environment death still leaves banked shard verdicts, a named cause, and a one-command resume — instead of a fourth total loss.

### Cost summary

| Proposal | Lines | Build time | Run overhead | Risk |
|---|---|---|---|---|
| 1 SHARD-4 split + scope cut | ~35 | ~20 min | +1–3 min (warm daemon) | Low |
| 2 Harvest + ledger | ~45 | ~25 min | <20 s total | Low |
| 3 Pre-flight gate | ~70 | ~30 min | 10–20 s | Very low |
| 4 Verdict taxonomy + manifest | ~20 + 18-line manifest | ~15 min | — | None |
| 5 test2.sh daemon/queue fixes | ~20 Δ | ~15 min | −(cold-start saved) | Low |
| 6 Heartbeat sidecar | ~12 | ~10 min | Negligible | Negligible |
| 7 Compile shard C0 | ~1 | ~5 min | Negative on resume | None |
