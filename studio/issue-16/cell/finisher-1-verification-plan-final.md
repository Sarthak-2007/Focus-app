# Issue #16 — Pill Lane: Verification Plan (Final)

**Repo:** `Sarthak-2007/Focus-app` · **Lane:** `~/workspace/clone-inspect/builds/pill/`
**Status of this document:** finished for one-read sign-off. No compiling, no Gradle, no test run was performed by this cell — every statement below is `PASS` (named artefact), `INSPECTION` (file:line read), or `UNPROVED`, and is labelled as such.
**Sources finished from:** `designer-verification-design.md` (presentation + flags), `innovator-clean-shot.md` (run design), `critic-charges.md` C1–C28 (charges that change the plan are folded in where they land, cited as C*n*).

---

## 1. Sign-off sheet — sign here, nothing else to read first

```
#16 PILL — SIGN-OFF SHEET
Today:  NO_VERDICT 18/18  ·  assemble: GREEN  ·  merge-ready: NO  ·  runnable script: NO (see §3.0)

[1] TEST VERDICT     A Absence ?/5   B Time maths ?/6   C Peek face ?/4   D Lease writes ?/3
[2] FLAG A  start2MinutePeek composition    ☐ SIGN  ☐ SIGN WITH A-3 DECIDED (§6.4)  ☐ REFUSE
[3] FLAG B  EditableTimesProvider swap      ☐ SIGN B-peek  ☐ SIGN B-break (§7.3 mode)  ☐ DEFER B-break  ☐ REFUSE
[4] NOT PROVED BY ANY OF THE 18 (§5):  PillController window · Route B notification ·
    ST-08 launch · PillTheme · provider at non-default minutes — INSPECTION / UNPROVED only

Signed: __________   Date: ______   If unsigned, next action: __________
```

Rules that govern this sheet:

1. **Headline is `NO_VERDICT`, not failure.** Honest summary today, all three clauses mandatory: *Code compiles. 18 tests written. 0 tests have ever reported. 0 tests have ever failed.* (§2)
2. **Four counters, never one "18/18".** A–D map 1:1 to the run shards in §3.2 (`D1-absent`=A, `D2-time`=B, `D3-peek`=C, `L-lease`=D), so a partial result shows *which law* is unproved.
3. **Flags sign separately from tests.** None of the 18 calls `start2MinutePeek` or constructs the Frist provider (§5, C3), so a green suite does not sign either flag, and a refused flag does not block banking a test verdict.
4. **Block [4] is on page 1 by design.** "18/18 PASS" must never be readable as "the pill works."

---

## 2. Ground facts and attempt ledger

| Attempt | Scope | Outcome | Artefact |
|---|---|---|---|
| Assemble | `:app:assembleDebug` (JDK 17) | **GREEN** — `BUILD SUCCESSFUL in 17m 43s`; main + test sources compile | `builds/pill/assemble.log` |
| Test 1–2 | `:app:testDebugUnitTest`, full suite | Died to the environment; no test XML harvested; logs superseded (the overwrite defect §3.3 fixes) | — |
| Test 3 | `:app:testDebugUnitTest`, full suite | Reached `:app:testDebugUnitTest` after `:app:compileDebugUnitTestKotlin` completed, then *"Gradle build daemon disappeared unexpectedly"* (daemon pid 53253). Death after compilation, before any verdict | `builds/pill/test.log` |
| Script re-run | `test.sh` | No evidence at all | `builds/pill/test-run.log` (0 bytes) |

Tree facts (INSPECTION):

- Pill lane is **DB v11** — `MonolithDatabase.kt:25` (`version = 11`) in the pill tree. The batch-9 merge tree is already **DB v12** (`MonolithDatabase.kt:25`, merge tree) and contains **no** `Pill*.kt` and **no** `EditableTimes.kt` — the pill lands entirely additively there. The pill brings no migration of its own; Flag B's columns arrive with Frist's 11→12 work (§7.4).
- Package is `com.example` in both trees; every pill file lands at the identical path. No renames (§8).
- Environment physics, established and not re-litigated: 2 cores / 7,935 MB, no swap; `gradle.properties` already carries the fixed regime (`-Xmx2g`, `workers.max=2`, in-process Kotlin compiler, config cache on). Assemble took 17m 43s green — exposure time is the enemy, which is why §3 shards.

---

## 3. The one re-run — stated exactly, criteria stated before it happens

### 3.0 Blocker first: the issued script cannot run (C1)

Issue #16 names `builds/pill/test.sh` as the re-run route. As shipped it exits before Gradle:

- `test.sh:6` sets `Q=~/workspace/clone-inspect/design-refs/swarm/tools/queue.sh`; `test.sh:10` runs `bash "$Q" acquire pill … || exit 3`. That tree was deleted 2026-10-30 (Commons/Mailroom retirement); the path does not exist on disk today (verified). `build.sh` carries the identical dependency.
- `test.sh:16-17` also runs the **unfiltered full suite** (204 `@Test` across 33 files) in one daemon, one overwritten `test.log`, verdict = one stdout line lost if the box dies (C2). That is the exact shape that died three times.

**Therefore the one command below is the successor script, not `test.sh` as shipped.** Until `test2.sh` exists on disk and passes pre-flight, there is no runnable command and the healthy window must not be spent. Building `test2.sh` (≈150 lines, per `innovator-clean-shot.md` §1–§7) is cell work, paper-reviewed, no compile.

### 3.1 The one command (Ren only, healthy window only)

```bash
bash ~/workspace/clone-inspect/builds/pill/test2.sh
```

`test2.sh` is specified, in full, by §3.2–§3.4 of this plan. It takes no arguments. Its run directory is `builds/pill/runs/<yyyyMMdd-HHmmss>/` with a `runs/latest` symlink; it never overwrites `test.log` or any prior run.

### 3.2 Shards — fixed table, scoped with `--tests`, nothing else runs

Preceded by **C0-compile** (`:app:compileDebugUnitTestKotlin` as its own shard, so a compile death is never misread as a test death — the Test-3 death occurred at the test task *after* compilation, `test.log`):

| Shard | `--tests` filter (class `com.example.…`) | Tests |
|---|---|---|
| `D1-absent` | `PillDerivationTest.noSessionIsAbsent`, `inactiveSessionIsAbsent`, `ownPackageIsAbsent`, `telecomForegroundIsAbsent`, `keyguardIsAbsent` | 5 |
| `D2-time` | `PillDerivationTest.breakOutranksPeek`, `breakRemainingEqualsTheHouseDerivationAtEverySample`, `stopwatchShowsElapsedFromTheStoredPair`, `timedShowsNowFrameRemainingAndBlock`, `unstampedTickCreditsNothingBeyondTheStoredFact`, `remainingIncludesThePenalty` | 6 |
| `D3-peek` | `PillDerivationTest.peekFaceNamesTheRowAndDiesExactlyAtItsEnd`, `peekFaceFollowsHimWhereverHeWanders`, `twoLiveLeasesResolveToTheLaterEnd`, `faceEqualsStoredEndsAtEverySampledInstant` | 4 |
| `L-lease` | `PillLeaseRepositoryTest` (whole class, unsplit — Robolectric startup dominates; the class was authored for shared-sandbox, in-class execution, class doc `PillLeaseRepositoryTest.kt:27–29`) | 3 |

The other 186 tests are **not** run. They are not the missing verdict; running them multiplies wall time and RAM pressure for zero information. A full-suite confirmation, if wanted, is a separate post-merge decision.

### 3.3 Harvest, ledger, resume — after every shard, before the next

1. Copy that shard's `app/build/test-results/testDebugUnitTest/*.xml` into `runs/<run-id>/results/<shard>/` immediately — Gradle wipes that directory on the next run, so unharvested results do not survive.
2. Append one line per test to `runs/<run-id>/ledger.tsv` (test, shard, verdict, failures/errors/skipped, duration, XML SHA-256), written via temp file + `fsync` + append + `fsync`.
3. Create `runs/<run-id>/<shard>.done` by atomic rename **only after** 1–2 complete. Re-running the one command skips `.done` shards; only `NO_VERDICT` shards retry.
4. A 15 s heartbeat sidecar (`date`, free RAM, daemon-alive, disk-free) appends to `runs/<run-id>/heartbeat.log` for the driver's lifetime, so a fourth death leaves its own certificate.

### 3.4 Pre-flight gate — any FAIL = do not start, window not spent

Eight checks, each pass/fail with its own line in `runs/<run-id>/preflight.txt`, final line `PREFLIGHT PASS 8/8` or `PREFLIGHT FAIL check=<n>`:

1. Queue tool exists and is executable — or queue explicitly recorded as `QUEUE=none` for a solo window run. (Today's `test.sh` path **fails** this check; that is the point of the gate.)
2. JDK 21 + Gradle present after sourcing `env21.sh`; `java -version` recorded.
3. Sustained write probe in the lane scratch dir: 8 × 1 MB writes + 200 small create/delete, `sync`, read back.
4. RAM: `free -m` available ≥ 2,500 MB; resident Gradle daemons ≤ 1 foreign.
5. Disk: ≥ 5 GB free on the workspace filesystem.
6. Lane state clean: `guh/` writable, no stale lock held by a dead pid, prior `test.log` preserved into the run dir.
7. Filter sanity: every `--tests` name in §3.2 grep-matches a `fun <name>(` in the two pill test sources.
8. Daemon posture: `gradle --status` recorded; a live same-JDK, same-`guh` daemon is **reused — never `--stop`ped** (deleting `test.sh`'s unconditional `gradle --stop; sleep 4` is part of the successor spec; stop only on JVM-version mismatch, and log which branch was taken).

### 3.5 Verdict taxonomy and pass/fail criteria — fixed before the run

- **Per test**, from harvested JUnit XML only, never from Gradle's exit code alone: `PASS` (testcase, no failure/error) · `FAIL` (`<failure>`) · `ERROR` (`<error>`) · `NO_VERDICT` (no XML entry).
- **Per run**: `COMPLETE` iff all 18 carry PASS/FAIL/ERROR **and** the harvested names reconcile exactly against a checked-in `expected-18.txt` manifest (one fully-qualified method per line); otherwise `PARTIAL k/18` with the missing names listed in `VERDICT.md` / `verdict.json` in the run dir.
- **Infrastructure deaths name the shard, never the tests**: log grep `daemon disappeared` → `INFRA_DAEMON_GONE`; `OutOfMemoryError` / native-thread failure → `INFRA_RAM`; "No tests found" → `INFRA_FILTER`. Tests inside an infra-killed shard stay `NO_VERDICT (shard: INFRA_…)`.
- **Category consequences, pre-committed** (detail in §4): any FAIL in A, C, or D **blocks merge**. A FAIL in B blocks merge **for that face only**, read per-test, never from the counter alone. `NO_VERDICT` in any category blocks any claim that the category is proved; it does not by itself refuse either flag (§1 rule 3).

---

## 4. The 18 — what each verdict proves, and what a FAIL costs

### A — Absence gates (5, plain JUnit) — the pill must not appear

`PillDerivationTest.kt`; code `PillDerivation.derive`, `PillFace.kt:82–95`.

| Test | Line | PASS proves |
|---|---|---|
| `noSessionIsAbsent` | L71 | No session row → `Absent` |
| `inactiveSessionIsAbsent` | L76 | Inactive session → `Absent` |
| `ownPackageIsAbsent` | L81 | Foreground = Monolith itself → `Absent` |
| `telecomForegroundIsAbsent` | L87 | `com.android.incallui` / `com.google.android.dialer` foreground → `Absent` (2 of the 5 packages in `PillFace.kt:66–72` — C28; the other three ship untested unless the test is widened to iterate the set) |
| `keyguardIsAbsent` | L94 | Lockscreen up → `Absent` |

FAIL → **BLOCKS MERGE.** PASS proves the derivation's suppression gates only — not that `PillController` removes its window (`PillController.kt:139`), which is §5 INSPECTION.

### B — Time maths from stored ends (6, plain JUnit) — the face is recomputed, never counted

`PillDerivationTest.kt`; code `PillFace.kt:100–154`, `elapsedAt` L160–165.

| Test | Line | PASS proves |
|---|---|---|
| `breakOutranksPeek` | L100 | Break wins over a live lease; 600 s − 30 s = `Break(570)` |
| `breakRemainingEqualsTheHouseDerivationAtEverySample` | L114 | Break remaining ≡ `breakRemainingAfter(600, t0, t0+offset)` at 7 offsets incl. past-end. **Caveat (C24):** the oracle *is* the function `derive` delegates to (`PillFace.kt:100–108` → `WallAccrual.kt:40–51`) — PASS proves wiring, not arithmetic. Literal expected values are the settling fix. |
| `stopwatchShowsElapsedFromTheStoredPair` | L128 | Elapsed = stored elapsed + whole seconds since stored tick; sub-second credits nothing |
| `timedShowsNowFrameRemainingAndBlock` | L145 | Remaining = target − elapsed-at-instant (1190 s); block index/count carried (2/4) |
| `unstampedTickCreditsNothingBeyondTheStoredFact` | L160 | `lastTick = 0` credits 0 beyond stored elapsed |
| `remainingIncludesThePenalty` | L167 | Effective target = target + penalty (1500 + 900 − 300 = 2100) |

FAIL → **BLOCKS MERGE for that face only** (e.g. a penalty-only failure blocks the timed face's claim, not the break face's).

### C — Peek face (4, plain JUnit) — the fuse names its app and dies on time

`PillDerivationTest.kt`; lease scan `PillFace.kt:113–127`.

| Test | Line | PASS proves |
|---|---|---|
| `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` | L179 | `Peek(pkg, name, 120)`; ceiling rounding, last partial second reads `1`; at the end instant the face reverts to `Session`, never `0` |
| `peekFaceFollowsHimWhereverHeWanders` | L198 | Peek face shown whatever the foreground app; names the peeked app, not the foreground one |
| `twoLiveLeasesResolveToTheLaterEnd` | L207 | If storage ever held two live leases, `maxBy` end wins (a state a correct grant makes impossible — see D/C26 note) |
| `faceEqualsStoredEndsAtEverySampledInstant` | L219 | Fixed stored ends × 6 instants → remaining = `(end − now + 999)/1000` every time, no state carried. **Caveat (C25):** expected values replicate the implementation's ceiling formula (`PillDerivationTest.kt:219–237` vs `PillFace.kt:120–125`) — a shared ceiling-vs-floor misunderstanding passes both sides. |

FAIL → **BLOCKS MERGE.** A fuse that dies early, reads 0, or names the wrong app is worse than no pill.

### D — Lease writes (3, Robolectric + Room) — the write side keeps the laws C assumes

`PillLeaseRepositoryTest.kt` (`RobolectricTestRunner`, `@Config(sdk=[36])`, real `MonolithDatabase.getInstance`, unique packages per test against the shared sandbox DB — class doc L27–29). Code: `MonolithRepository.startPeekLease` L1180–1192, `recordPeekEnded` L1198–1205, `MonolithDao` L32/L41.

| Test | Line | PASS proves |
|---|---|---|
| `grantStampsEndAndPreArmsCooldown` | L55 | Grant end ≈ now + 120 s (asserted window 100–120 s — C27: a flat 100 s provider also passes); cooldown pre-armed to the lease's own end |
| `grantZeroesEveryOtherRowsLease` | L77 | Second grant zeroes the first row's lease, leaves its earned cooldown anchor standing, bystander untouched, exactly 1 live lease in storage |
| `handBackZeroesTheLeaseAndAnchorsCooldownToNow` | L120 | Hand-back (pill ✕ and Route B `END PEEK` share this verb) zeroes the lease, re-anchors cooldown to the hand-back instant, not the granted end. **Caveat (C27):** Robolectric's shadow clock does not advance inside `runBlocking`, so the before/after bracket can collapse to one frozen value — PASS is weaker than "anchored to now" until the test drives the shadow clock explicitly. |

FAIL → **BLOCKS MERGE.** PASS proves grant/exclusivity/hand-back **at today's default only** (120 s via `EditableTimesProvider` returning `PEEK_DURATION_MILLIS`, `EditableTimes.kt:38`). It proves nothing at any other `peekMinutes` — that is Frist-lane `EditableTimesTruthTest` FT-3/FT-4 territory, cross-referenced under Flag B (§7), never implied by D. Two standing risks the reviewer signs knowingly: the grant is two non-atomic DAO calls with no `@Transaction`/mutex, so concurrent grants can interleave into two live leases (C26; `MonolithRepository.kt:1185–1190`, `MonolithDao.kt:31–41`), and D's verdict is only interpretable on a fresh/cleaned sandbox (§9, R-3).

---

## 5. Coverage honesty map — what "verified" does not mean

| Surface | File | Evidence | What the plan states |
|---|---|---|---|
| `PillDerivation.derive` | `PillFace.kt:60–165` | TEST (A+B+C, 15) | Proved by §4 once run — with the C24/C25 oracle caveats printed there |
| Lease grant / hand-back writes | `MonolithRepository.kt:1180–1205`, `MonolithDao.kt:32–42` | TEST (D, 3, defaults only) | Proved at 120 s only |
| `PillController` — window create/remove, `canDrawOverlays` gate (L139), render-key short-circuit (L147), drag/snap, body tap → `MainActivity`, ✕ → `onHandBack` (L288–292) | `PillController.kt` (whole) | INSPECTION | No test constructs it. Read-through checklist (window dies on `Absent`, on grant loss, in `stop()` L106–119) + one on-device check post-merge: arm, peek, drag, hand back, revoke overlay grant mid-peek. Known paper findings carried, not hidden: stale-foreground safe-side decay (C18, `PillController.kt:80–85, 121`), ✕/ticker race (C19, L130–131 vs L289–291), tick failures swallowed with no counter/log (C21, L90–99) |
| Route B — shield notification peek face, chronometer, `END PEEK` → `ACTION_END_PEEK` → `recordPeekEnded` | `MonolithShieldService.kt:38–58, 160–191, 298–343` | INSPECTION | No test builds a notification. Checklist: subtitle format `Peeking at %s • %02d:%02d left`; action present only when `peekPackage != null`; handler touches nothing else (no loop restart, L38–57). **Drift warning (C15):** Route B never calls `PillDerivation.derive` — the only `derive(` call in the services is `PillController.kt:122`. The shield re-implements the priority and the `(+999)/1000` ceiling inline (`MonolithShieldService.kt:162–191`, face logic to L233) and adds a wall-clock system chronometer (L222–232, L317–324) — a third clock. `PillFace.kt:1–14`'s "one derivation, two mouths" is therefore a mirroring agreement today, not shared code (§9, R-1) |
| ST-08 — post-grant launch to the peeked app | `MonolithViewModel.kt:2179–2189` (pill lane) | INSPECTION | Safety is structural (grant-before-launch ordering, `runCatching`) — that is Flag A's checklist (§6.3), not a test |
| `PillTheme` / `PillPalettes.Default` (accent `#F47442`, never green) | `PillTheme.kt` | INSPECTION | Palette is data: 5 ARGB values read against trunk night tokens; Anonymous Pro via `R.font.anonymous_pro` with monospace fallback. Header over-promises: the figures typeface has no public writer (C20, `PillTheme.kt:50–62`), so a look lane cannot swap it through the holder today |
| `EditableTimesProvider.breakDurationSeconds()` | `EditableTimes.kt:44` | UNPROVED in this lane | **Zero consumers in the pill tree** (§7.3). Break editability must not be listed as pill-proved under any wording |
| Monitor-loop survival | `MonolithShieldService.kt:31, 121–146` | UNPROVED | SupervisorJob siblings: the pill ticker wraps ticks in `runCatching` (`PillController.kt:90–99`); the monitor loop has no error handling. One monitor exception leaves a live pill advancing a clock over a dead session (C17) — untested, no fault-injection verdict exists |

---

## 6. Merge Flag A — `start2MinutePeek` composition

### 6.1 Anatomy — neither tree's version is copied verbatim

| Element | Pill lane (`MonolithViewModel.kt:2155–2193`) | Batch-9 tree (`MonolithViewModel.kt:2384–2413`) |
|---|---|---|
| Pre-grant guard | **Absent.** Grants unconditionally; only guard is at intercept time (L2128–2131) | **Present (UNA2-01 second gate):** re-derives the offer via `peekOfferFor(state.interceptPath, entity, cooldownNow, …)` (`MonolithViewModel.kt:141–151`); refusal `return`s, airlock left standing |
| Blank package | `interceptedPackageName.ifBlank { "com.whatsapp" }` (L2157) — invents a lease, a launch, and a log for an app never intercepted (C7) | `val pkg = state.interceptedPackageName` — no default; guard's `entity == null` refuses first |
| Grant write | `repository.startPeekLease(pkg)`, first statement in the coroutine (L2168) | Same call, same position, after the guard |
| ST-08 post-grant launch | **Present** (L2172–2189): after grant, `runCatching { getLaunchIntentForPackage(pkg) + FLAG_ACTIVITY_NEW_TASK → startActivity }`, then `recordIntercept(…, "Used 2m Peek", …)` | **Absent.** Grant → `recordIntercept` only |
| Legacy pill state | Sets `_floatingPeekPill`, `secondsRemaining = PEEK_DURATION_SECONDS` (L2162–2167) | Same set, same constant — see §6.4, this line is a decision, not a copy |

### 6.2 Port surface — the flag as scoped understates the work (C4, BLOCKER as scoped)

Suki's guard reads inputs the pill lane does not have. The composition is a **port of four pieces**, named here so no paste silently defaults:

1. `InterceptPath` enum — batch-9: `MonolithAccessibilityService.kt:46–58` (incl. `canHonourPeekLease`, true only for `APP_TIER`/`SHIELD_APP`); zero hits for `interceptPath` anywhere in the pill lane's main source.
2. `AirlockInterceptState.interceptPath` field — batch-9 `MonolithViewModel.kt:153–166` vs pill `MonolithViewModel.kt:118–132` (no such field).
3. `triggerAirlockIntercept` gaining the path parameter from its real callers — pill current form `MonolithViewModel.kt:2120–2144`.
4. The firing service's intent plumbing carrying the path — the pill shield intercept intent today carries only package/name/badge (`MonolithShieldService.kt:296–303`).

**Default-path trap (C5):** if 1–2 land without 3–4, the field stays `InterceptPath.UNKNOWN`, `canHonourPeekLease` is false, and every grant is silently refused forever while the airlock still offers the door. No test in the 18 calls `start2MinutePeek`, so nothing automated catches it. Settled only by a per-path intercept → airlock → grant check asserting a lease row exists afterwards.

### 6.3 The composed verb the reviewer signs — numbered, in this order

1. Read airlock state. Re-derive the offer from current facts with `peekOfferFor(...)` — the **same** pure function at intercept time and grant time. (The pill lane's intercept-time formula counts `appEntity == null` as peekable, `MonolithViewModel.kt:2128–2132`, where `peekOfferFor` counts it as refusal, L147–151 — keeping both reintroduces offer-then-refuse one layer up, C8. One formula wins: `peekOfferFor`.)
2. **If refused: stop.** Bare `return` before any mutation — no sound, no airlock dismiss, no pill state, no lease, no launch, no intercept log; the airlock stays on screen. Placement is load-bearing (C6): the pill body performs synchronous visible mutations at L2159–2166 *before* its coroutine, so a guard pasted above `viewModelScope.launch` but below those lines leaves a phantom pill whose expiry calls `recordPeekEnded` (`MonolithViewModel.kt:852–863, 2194–2202`) and arms a 20-minute cooldown for a peek that never happened (`MonolithRepository.kt:1197–1204`).
3. If granted: latch sound, dismiss airlock, set `_floatingPeekPill` — subject to the §6.4 decision.
4. In `viewModelScope.launch`, in this order: ① `repository.startPeekLease(pkg)`; ② ST-08 `runCatching` launch to `pkg`; ③ `repository.recordIntercept(..., outcome = "Used 2m Peek")`.
5. `pkg` is `state.interceptedPackageName` with **no `ifBlank` default** — Suki's semantics delete pill-lane behaviour on this line, and the plan says so in words.
6. **Grant must be proven before ② and ③ (C9).** `startPeekLease` returns `Unit` and silently no-ops when the provider returns ≤ 0 (`MonolithRepository.kt:1180–1183`). Under Flag B, `peekMinutes = 0` is a legal setting: as composed today the flow would still hide the airlock, launch the app with no lease (the shield immediately re-intercepts it), and log "Used 2m Peek". The merged verb therefore requires `startPeekLease` to report whether it granted (or the guard to prove grantability first); launch and log fire only on a proven grant. The launch's own `runCatching` failure likewise must not be logged as "used".

Invariant checklist (all must hold in the merged code):

- ☐ Guard executes before **every** mutation, not merely before the lease.
- ☐ Lease-before-launch ordering preserved; launch inside the same coroutine, after the grant, `runCatching`, `FLAG_ACTIVITY_NEW_TASK`.
- ☐ Refusal path side-effect-free, airlock visible, no cooldown written.
- ☐ No `com.whatsapp` or any blank-package fallback anywhere in the merged function.
- ☐ `recordIntercept` fires exactly once, after the launch attempt, only on a proven grant, outcome `"Used 2m Peek"`.
- ☐ Port pieces 1–4 (§6.2) all present; no path defaults to `UNKNOWN` in production flow.

**Sign:** ☐ SIGN composition as §6.3 ☐ SIGN with A-3 decided the other way (§6.4) ☐ REFUSE — reason: ______

### 6.4 A-3 — the leftover second clock is a merge decision, not a follow-up footnote (C13/C14, HIGH)

`PillFace.kt:13–17` declares the counted-decrement state the disease the derivation replaced. It is still alive: `start2MinutePeek` seeds `_floatingPeekPill` from the constant `PEEK_DURATION_SECONDS` (120), bypassing the provider (`MonolithViewModel.kt:2161–2166`); a 1 Hz VM ticker decrements it (L851–863); at zero it calls `end2MinutePeekEarly()` → `recordPeekEnded()`, zeroing the real lease in storage (L2194–2202 → `MonolithRepository.kt:1197–1204`). Consequences: with Frist `peekMinutes` > 2 (up to 30), every peek is silently truncated at 120 s; and a storage hand-back (pill ✕, `PillController.kt:289–291`; Route B END PEEK, `MonolithShieldService.kt:44–55`) never reaches the VM state, whose later expiry re-anchors the cooldown late — the cooldown takes the later anchor (`MonolithRepository.kt:1157`), so the extension is real.

Options for the reviewer — **one must be ticked before Flag B is signed**: (i) delete the VM state/ticker so the storage derivation is the only clock (the header's own claim), or (ii) make the ticker derive from the stored lease end and clear on storage hand-back. Accept-and-document is **not** offered: at non-default minutes the state is actively wrong, not merely dead.

---

## 7. Merge Flag B — Frist's `EditableTimesProvider` seam swap

### 7.1 The swap table — if any row stops being true, this is a change, not a swap

Pastille's seam: `builds/pill/…/data/EditableTimes.kt` (45 lines; header L1–29 is the contract). Frist's fulfilment: `builds/truth-3-times/…/data/EditableTimes.kt` (header states the same contract).

| Contract point | Pastille (pill) | Frist (truth-3-times) | Changed? |
|---|---|---|---|
| Class + package | `com.example.data.EditableTimesProvider` | Identical | No |
| Constructor | `(dao: MonolithDao)`, dao `@Suppress("unused")` (L31–33) | `(dao: MonolithDao)`, dao now read (L34–36) | Signature no; body use yes — the point |
| `peekDurationMillis(): Long` | `= PEEK_DURATION_MILLIS` (120 000), L38 | `getGuardSettingsSnapshot() ?: GuardSettingsEntity()` → `peekMinutes.coerceIn(0, 30) * 60_000`, L41–45 | Body only |
| `breakDurationSeconds(): Int` | `= SAFE_BREAK_DURATION_SECONDS` (600), L44 | `breakMinutes.coerceIn(0, 60) * 60`, L51–55 | Body only |
| Bounds constants | Prose only (peek 0–30, break 0–60) | `companion object` min/max constants, L57–66 | Additive |
| Mid-flight law | Provider read at grant/creation only; no enforcer or derivation reads it | Identical law, restated L29–33 | No |

### 7.2 Consumer inventory — the "unchanged" claim, made checkable

| Consumer | Pill tree | After swap |
|---|---|---|
| Instantiation | `MonolithRepository.kt:95` — `private val editableTimes = EditableTimesProvider(dao)` | Byte-identical (Frist tree: same line at its L96). **Note (C3):** the provider is hard-wired — no constructor parameter or setter — so no pill-lane test can substitute a non-default provider. Frist's contract tests (`EditableTimesTruthTest`: 0-grants-nothing, mid-flight immovability, setter clamp) have no pill counterpart; D (§4) therefore never covers this seam |
| Peek grant | `startPeekLease` L1180–1192 reads `peekDurationMillis()`; `if (durationMillis <= 0L) return` — 0 = peeks-off already honoured by the consumer (but see §6.3 step 6: the caller cannot see the refusal) | Unchanged; a 0 setting now actually occurs |
| Break at session creation | **No consumer exists in the pill tree** — §7.3 | ← the split |
| Everything else (`PillDerivation`, shield, enforcers) | Never reads the provider | Unchanged — a live lease/break holds an absolute end/stamp; an edit prices the future only |

**Default-equivalence (one line):** at Frist defaults (2 / 10) the provider returns `2 × 60_000 = 120_000 ms` and `10 × 60 = 600 s` — bit-identical to the constants replaced; an absent settings row reads as entity defaults, so a never-seeded install behaves exactly as the pill lane does today. Category D's 120 s assertions remain valid after the swap at defaults — D needs re-running on the merged tree (§8 step 5), not re-authoring.

### 7.3 The split — B-peek is a swap; B-break is a swap plus missing wire (C10, BLOCKER for the one-signature framing)

- **B-peek — true drop-in.** Provider body changes; the one consumer already handles the full range including 0. Sign and done.
- **B-break — provider swaps, pill-side effect does not exist.** `breakDurationSeconds()` has **zero callers** in the pill tree. The break's 600 s is baked in four places: the block-finish write `MonolithRepository.kt:1897` (`safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS`), and the span derivations `MonolithModels.kt:391` (`(totalStudyBlocks - 1) * 600`), `:395` (`cycle = effectiveTargetSeconds + 600`), `:398` (`600 - safeBreakSecondsRemaining`). Pill `FocusSessionEntity` has no `breakSeconds` column (the word occurs only in the seam's own comment, `EditableTimes.kt:12`). Frist's tree wires it properly: row carries `breakSeconds: Int = 600` (`MonolithModels.kt:372`) stamped at creation from `editableTimes.breakDurationSeconds()` (`MonolithRepository.kt:1377`), and the span maths reads the stamp.

The reviewer ticks exactly one B-break mode:

- ☐ B-break as provider-only, break editability **explicitly deferred** (the swapped function is then knowingly dead code until wired).
- ☐ B-break **only together with** Frist's consumer side: `breakSeconds` stamp at creation + L1897 and the three span sites reading the stamp. **Recommended default, printed here in advance.**
- ☐ DEFER B-break entirely.

### 7.4 Dependencies — prerequisites of the swap, not discoveries at compile time (C11)

1. **GuardSettings columns** `peekMinutes: Int = 2`, `breakMinutes: Int = 10` — Frist `MonolithModels.kt:819–833`. Absent from batch-9 (its GuardSettings ends at `shortsReelsSniperEnabled`, `MonolithModels.kt:772–779`) and from the pill entity (`MonolithModels.kt:705–732`). Without them Frist's provider does not compile against either tree.
2. **Migration 11→12** for those columns (Frist `MonolithDatabase.kt:197–198`), landing on a tree already at v12 for other Truth Car 3 adds. Column order in the composed migration is already ruled in Frist's own GuardSettings doc comment: *peek/break append first among the car's guard adds; `shortsReelsSniperEnabled` follows.* Schema identity at v12 depends on this line.
3. **Repository setters** that coerce writes to the same bounds (Frist's tree carries them) merge with the provider — the provider's read clamp is the second wall, never the only one.
4. **Diff discipline on `MonolithRepository.kt` (C12):** Frist's own `startPeekLease` (`truth-3-times MonolithRepository.kt:1200–1210`) dropped the pill's one-live-lease zeroing. The merge is hunk-by-hunk; `dao.clearOtherPeekLeases(packageName)` (pill `MonolithRepository.kt:1185`) is explicitly accounted for and retained, and `grantZeroesEveryOtherRowsLease` stays in the verdict set.

**Sign:** ☐ SIGN B-peek ☐ SIGN B-break (mode per §7.3) ☐ REFUSE — reason: ______

---

## 8. Merge manifest and order

**New files into the batch-9 tree** (none exist there today): `service/PillFace.kt`, `service/PillController.kt`, `service/PillTheme.kt`, `data/EditableTimes.kt` (Pastille's version first; Frist's body replaces it under Flag B), `test/PillDerivationTest.kt`, `test/PillLeaseRepositoryTest.kt`.

**Modified files:** `data/MonolithRepository.kt` (provider field L95, `startPeekLease`/`recordPeekEnded`, `queryForegroundPackage`), `data/MonolithDao.kt` (L32, L41), `service/MonolithShieldService.kt` (pill ownership, Route B, `ACTION_END_PEEK`), `ui/MonolithViewModel.kt` (`start2MinutePeek` — **Flag A composition per §6.3, neither tree's version copied verbatim**; plus the Flag A port pieces §6.2 and the §6.4 decision).

**Order:** (1) Frist GuardSettings columns + 11→12 migration reconciliation → (2) pill files + modifications → (3) Flag A composition in the ViewModel → (4) Flag B provider body swap (+ B-break wiring if §7.3 mode 2 ticked) → (5) the §3 run on the **merged** tree. Steps 1 and 4 are one flag split around the pill landing — the provider is never swapped into a tree lacking its columns.

**Untouched, by name:** DB version stays 12 (no pill migration). `PillTheme.palette` ships as `PillPalettes.Default`; look lanes redress it later by writing the palette, per `PillTheme.kt`'s own contract — no look work is smuggled into #16.

---

## 9. Residual risks carried past merge — each with what closes it

- **R-1 — Route B drift (C15).** Route B mirrors the derivation inline in the shield (`MonolithShieldService.kt:162–191`) instead of calling it. They agree today, checklist-verified in §5; any future derivation change made once diverges the two mouths. Closes with: Route B consuming `PillDerivation.derive`'s result directly (chronometer end derived from that face), as a named follow-up — not a #16 blocker.
- **R-2 — Controller is inspection-only (C17–C19, C21, C23).** The overlay window, the monitor-loop-death shape, and the body-tap launch have no automated verdict in this lane and cannot get one from these 18. Closes with: the §5 on-device check after the merged build exists (arm, peek, drag, hand back, revoke overlay grant mid-peek, tap pill body, kill the service). Block [4] of the sign-off sheet stays visible until then.
- **R-3 — Category D sandbox hygiene.** D's tests share one sandbox DB by design (unique packages per test, class doc L27–29). A re-run in a dirty sandbox is a plausible false-failure source. Closes with: §3's run protocol stating the sandbox is fresh or cleaned before any D verdict is interpreted.
- **R-4 — END PEEK stale-tap (C16).** `recordPeekEnded` re-anchors the cooldown unconditionally (`MonolithRepository.kt:1197–1204`); a stale tap after natural expiry extends the cooldown, and its `PendingIntent.getService` (`MonolithShieldService.kt:325–345`) may be undeliverable to a dead service. Closes with: a no-op when no lease is live/past, plus a device test tapping END PEEK with the service killed. Not a #16 blocker; recorded so the signature is knowing.

---

— Finisher-1 (issue-16 cell)
