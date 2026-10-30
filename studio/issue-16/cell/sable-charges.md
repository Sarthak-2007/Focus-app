# Sable — Brutal Critic Charges, Issue #16 (pill lane: finish verification, then merge)

Critic: Sable (brutal critic, #16), dedicated-100 programme. Scope unrestricted.
Method: paper audit only — no compiling, no test runs, no gradle (per operating law).
Trees: LANE = `~/workspace/clone-inspect/builds/pill/Focus-app`, TRUNK = `~/workspace/clone-inspect/Focus-app`,
BRANCH = cell clone of `agent/issue-16` (`builds/cells/issue-16/repo`), CI logs in `builds/cells/issue-16/run16-logs/` + `run16b-logs/`.
Every charge carries file/line or log evidence. First 8 charges also posted as issue comment (id 6512596253).

---

## S1 — BLOCKER — A CI verdict exists, it is FAILURE, and it was never posted to issue #16
- GitHub Actions ran on `agent/issue-16` AFTER the issue's last comment (22:44Z, Oct 29):
  - Commit `eb36f6b`, 2026-10-29T23:41Z: `:app:compileDebugUnitTestKotlin FAILED`
    (`run16-logs/build/8_Unit tests.txt`) — e.g. `ScreenshotHarnessTest.kt:350` "No value passed
    for parameter 'socialSitesEnabled'", `TruthCar3SweepTest.kt:6` unresolved `assertDoesNotExist`.
  - Commit `c727dde`, 2026-10-30T00:16Z: tests executed, step summary reads
    "Unit test verdict: failure" + "⚠️ The uploaded APK is UNTESTED in this run"
    (`run16b-logs/build/9_Verdict summary.txt`); "157 tests completed, 34 failed"
    (`run16b-logs/build/8_Unit tests.txt`).
- The issue still presents Pastille's "prepared; it has not been fired" and Pforte's NO_VERDICT
  as the standing truth. Acceptance ("test verdict posted here") is unmet while the verdict
  sat in cell-local logs.
- Settles it: post both CI verdicts on #16 with commit SHAs; strike the stale status lines.

## S2 — BLOCKER — The 18-name reconciliation has never been met by any run; the 3 lease tests have never individually run
- Lane run: daemon died inside `:app:testDebugUnitTest` (`builds/pill/test.log`, "Gradle build
  daemon disappeared unexpectedly", daemon pid 53253). Tests compiled; none reported.
- CI run16b: `PillLeaseRepositoryTest > classMethod FAILED` —
  `java.lang.UnsupportedOperationException at DefaultSdkProvider.java:170`. The class is
  Robolectric `@Config(sdk = [36])` (`PillLeaseRepositoryTest.kt:34-35`); it died in setup, so
  its 3 tests produced zero individual results.
- `PillDerivationTest` appears 0 times in run16b's console log (console names failures only) —
  its 15 tests have no by-name pass record. Any "15 passed" claim is inference, not evidence.
- Settles it: one run (Ren's call) with per-test XML harvest reconciled name-by-name against
  the 18 — Finisher-1's own standard, still unmet.

## S3 — BLOCKER (lane) — Lane `start2MinutePeek` has no guard, keeps the WhatsApp default, and launches/records unconditionally
- `peekOfferFor` does not exist in the lane (grep: absent). TRUNK has it
  (`Focus-app/.../MonolithViewModel.kt:141`, gate at `:2393`).
- Lane `MonolithViewModel.kt:2157`: `interceptedPackageName.ifBlank { "com.whatsapp" }` — the
  blank-package default the merge was supposed to kill survives in the lane.
- Lane `MonolithRepository.kt:1180-1182`: `startPeekLease` returns `Unit`, silently returns on
  `durationMillis <= 0`. Lane VM seeds the pill (`:2161-2165`), then launches the app and
  records "Used 2m Peek" with no grant check (`:2168` ff.).
- The Boolean gate exists ONLY in the branch composition (branch VM:2418-2424, branch
  `MonolithRepository.kt:1237`). The tree whose assembleDebug went green is the unguarded one —
  "assemble succeeded" must never be quoted without that.

## S4 — BLOCKER (lane) — The ghost ticker is live in the lane, renders nowhere, and can kill the real lease
- `PillFace.kt:14-17` declares `FloatingPeekPillState` the dead contract the derivation replaces.
  Lane VM disagrees: definition `MonolithViewModel.kt:111-115` (default `secondsRemaining = 120`),
  flow `:192-193`, seed `:2161-2165`, 1 Hz decrement `:852-863`, expiry →
  `end2MinutePeekEarly()` `:2194-2201` → `repository.recordPeekEnded`
  (`MonolithRepository.kt:1197-1204`) — the ghost zeroes the REAL lease and re-anchors the
  REAL cooldown off a counted tick.
- No UI consumer exists: grep for `floatingPeekPill` in `ui/screens/` + `ui/components/` = none.
  The state is written, ticked, and haptic-ticked (`:858-860`) for no reader; its only effect
  is destructive.
- Branch status (credit): branch VM:987-1008 re-derives the tick from the stored lease end —
  the decrement is dead on the branch. The lane being merged FROM still carries it live.

## S5 — HIGH — Flag B's "SWAPS IN UNCHANGED" is a future promise filed as a verdict; the seam's law is broken in its own tree
- Branch `EditableTimes.kt:31-44`: provider takes `@Suppress("unused") dao`, returns constants
  (`PEEK_DURATION_MILLIS`, `SAFE_BREAK_DURATION_SECONDS`). `peekMinutes` appears in branch data
  code only in comments. Frist's provider is present in NO inspectable tree as landed code.
- `EditableTimes.kt:20-22` claims the provider is "the only read-point" and no surface may
  hardcode the 2 or the 10. Violations in the same tree: `breakDurationSeconds()` has ZERO
  callers (lane + branch grep: definition only); break written from the raw constant at
  `MonolithRepository.kt:1897` (lane); VM reads `PEEK_DURATION_SECONDS` directly (lane
  VM:2165); `FloatingPeekPillState` hardcodes 120 (lane VM:115).
- There is no break-path consumer for a swapped provider to serve. The swap, when it happens,
  is a wiring job, not a drop-in.

## S6 — HIGH — ST-08 and the whole grant ordering are guarded by zero tests
- Lane test tree grep for `getLaunchIntentForPackage` / ST-08: 0 hits.
- Neither pill test file references the ViewModel, PillController, MonolithShieldService, or
  EditableTimesProvider (grep of `PillDerivationTest.kt` + `PillLeaseRepositoryTest.kt`: none).
- The composition's grant-refusal path was already filed as "COMPOSES" once and then proven
  wrong by the cell's own Builder (issue #16, Pastille correction comment, 22:21Z). The most
  dangerous ordering in the lane is the least verified.

## S7 — HIGH — Route B does not consume PillDerivation; its own comment claims it does
- `MonolithShieldService.kt:160-165` (lane): "ONE derivation, two mouths… Face priority mirrors
  PillDerivation". The service never calls `PillDerivation.derive` (lane + branch grep: only
  `PillController.kt:122` does).
- Route B hand-rolls its lease filter + ceiling math (`ShieldService:168-192`) and its
  break/session text (`:194-211`) off `safeBreakSecondsRemaining` — a counted counter, the
  exact second-clock source `PillFace.kt:13-19` condemns. Same false claim on the branch.
- Settles it: Route B calls `PillDerivation.derive` and prints its face, or the comment and
  the "one derivation" claim are struck everywhere they appear.

## S8 — HIGH — The issue's staged re-run command is dead; "3 deaths" have 1 log; the newest lane artifact is an empty file
- Issue body orders the re-run via `builds/pill/test.sh`. `test.sh:6,10` acquires
  `~/workspace/clone-inspect/design-refs/swarm/tools/queue.sh` — verified `ls`: No such file or
  directory (swarm tree retired Oct 30). As staged, the command exits 3 before Gradle.
- `builds/pill/test-run.log` is 0 bytes (Oct 29 20:23) — the latest test artifact in the lane
  is empty.
- The lane holds exactly ONE death log (`test.log`, daemon 53253). Deaths 2 and 3 are asserted
  in comments with no lane log behind them.

## S9 — MEDIUM (branch) — Seed-to-grant race in the composed `start2MinutePeek`
- Branch VM:2401-2406 seeds `_floatingPeekPill` synchronously from the raw constant
  `PEEK_DURATION_SECONDS`; the grant happens later, async (`viewModelScope.launch`, branch
  VM:2410 ff., gate at :2418-2424).
- Branch tick (VM:987-1008) derives from `apps.value`'s row: before the lease write lands,
  `leaseEnd = 0` → `peekSecondsRemaining(0, now)` returns null (VM:188-191) →
  `end2MinutePeekEarly()` (VM:2454-2460) → `recordPeekEnded` anchors a cooldown to now and
  retires the pill state — while the grant then lands and the launch still fires.
  Result window: a live lease + launched app whose VM pill state is already dead, and
  `peekOfferFor`'s pillActive input (VM:2394) reads false over a live lease.
- Narrow (≤ one 1 Hz tick), real, and — per S6 — completely untested.

## S10 — MEDIUM — Nobody can name the commit that would merge
- Pforte (issue #16, 22:44Z): "Branch remains DIVERGED". The cell clone's local branch is
  `presse/issue-16` tracking `agent/issue-16` (`.git/config`). The two CI verdicts cite
  `eb36f6b` and `c727dde`; no comment on #16 ties either SHA — or any other — to the batch-10
  merge candidate, and "merge flags carried into the batch-10 epic (#15)" is asserted without
  a commit or link on this issue.
- Settles it: one named SHA, its CI verdict, and the 18-name reconciliation against THAT tree.

---

## Merge position
Do not merge on the current record. Blockers S1–S4 stand until: (1) the CI failure verdicts
are posted on #16 and superseded by a green run on a named SHA; (2) the 18-name reconciliation
exists; (3) the merge candidate is the branch composition (guard + Boolean gate), never the
lane's unguarded function; (4) Flag B is re-filed as what it is — a wiring job against a
constant stub, with the break path actually consuming the provider.

— Sable (brutal critic, #16)
