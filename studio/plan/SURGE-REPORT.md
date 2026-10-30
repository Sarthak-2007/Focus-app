# SURGE REPORT — Issue #52 (Audit R1 · drawer-screen) — STOSS-52 under SCHUB, cluster C7 Tafel

Status: IN PROGRESS (updated live by STOSS-52; final line replaces this when CI lands)

## Seat roster (20 named seats, run as lens bundles by STOSS-52, all signed here)
Crazy-ideas · Maximum-implementation-potential · Brutal critic · Improver · Designer ·
Consumer · Student · User · User-experience · Purpose-questioning · Thoughtful ·
Deep-thinker · Enthusiastic designer · Wow-level designer · Designer-29-years ·
Intentionality-focused designer · Synchronous collaboration · Implementation-quality ·
Technical execution specialist · Interaction-detail specialist — all LEAD-COVERED by
STOSS-52 in one pass (single-issue surge seat; no seat silently dropped).
Extra types seated: Verifier (every finding re-checked against current code),
Diagnostician (F3 root cause: ViewModel exposed no load state), Builder (the fixes).

## State found (fresh check by STOSS-52, ~02:02Z Oct 30)
- Branch agent/issue-52 EXISTS on remote, tip 43c2887 = local cell HEAD
  (~/workspace/clone-inspect/builds/cells/issue-52). Prior wave-3 seat had already
  dispositioned all 5 findings in the issue body: 3 ticked, 2 PARTIAL.
- CI for 43c2887: run 40975963122, completed FAILURE — build job failed ONLY at the
  "Unit tests" step (assemble/compile green). Failures sit in the shared floor set.
- Overlap check vs agent/issue-8 (tip d04c7fb): its DrawerScreen carries NO loading
  state and NO 48dp/Role touch targets (grep: zero hits for drawerLoading/sizeIn/
  Role.Button) → F3/F5 remainders are NOT fixed-elsewhere. F1's rememberSaveable
  half was already credited to issue-8 in the issue body by the prior seat.
- Fresh-clone mechanics note: the task asked for a fresh clone at
  builds/surge/issue-52. git clone + cp both died on the box-wide EPERM write wave
  (loose-object and template writes refused; the same failure reproduces in the
  issue-51 cell, so it is the known generation write-wall, not this cell). Work
  proceeded in the pre-existing cell builds/cells/issue-52, which WAVE3-DISPATCH
  explicitly permits ("if a cell already exists, reuse it").

## Findings disposition (5 total)
1. [HIGH] Fake internal navigation stack — FIXED @43c2887 (prior seat, verified in
   code: rememberSaveable view/task/cabinet + BackHandler one-level pop,
   DrawerScreen.kt current lines ~195-215). Ticked in issue body.
2. [HIGH] Sealed search trap — FIXED @43c2887 (CLEAR SEARCH live while sealed,
   testTag drawer_search_clear). Ticked.
3. [HIGH] No loading/error state — was PARTIAL (empty-month line only).
   NOW FIXED-IN-CODE by STOSS-52 (commit pending push, see below): ViewModel gains
   drawerLoading/drawerError StateFlows; selectDrawerMonth sets loading at tap,
   clears on land, writes "THE DRAWER COULD NOT BE READ — TRY AGAIN" on failure
   (CancellationException rethrown); loadEarlierDrawer and refreshDrawer's index
   reads fail loud the same way. DrawerScreen takes drawerLoading/drawerError
   (defaulted params) and prints three distinct states on MONTH (drawer_error /
   drawer_loading / NOTHING FILED IN <month>) and guards LANDING against a
   premature "NOTHING STORED YET" during the first read. MainActivity collects
   and passes both flows.
4. [MEDIUM] buildDayGroups scope + ticker desync — row-scope FIXED @43c2887;
   ticker half VERIFIED-FALSE on current main (DRW-05 boundary-aligned ticker,
   DrawerScreen.kt "today keys to the day boundary" block). Ticked.
5. [MEDIUM] Tiny bare-Text clickables — was PARTIAL (DrawerNavRow + day-words done
   @43c2887). NOW COMPLETED by STOSS-52: every remaining clickable in
   DrawerScreen.kt carries Role.Button — detail BACK, cabinet BACK, month BACK
   (new testTag drawer_month_back), month-index rows, ALL MONTHS row, cabinet
   rows, StoredRow, EarlierRow, CLEAR SEARCH — and 48dp min height everywhere it
   was deficient (grep-verified: 11/11 clickable sites carry Role.Button, zero
   bare `.clickable {` remains).

## Files changed by STOSS-52 (3 files, +195/−41 vs 43c2887)
- app/src/main/java/com/example/ui/MonolithViewModel.kt — drawerLoading/drawerError.
- app/src/main/java/com/example/ui/screens/DrawerScreen.kt — states + touch targets.
- app/src/main/java/com/example/MainActivity.kt — collect + pass the two flows.

## CI benchmark (shared floor)
- Main's own unit-test floor: 38 failures (315 tests) — BreakMigrationTest,
  CascadeDeckRenderTest ×5, ChromeWaveUnitTest, DrawerRenderTest ×7,
  RoutinesFinaliseTest, ScreenshotHarnessTest ×13, ShortsSniperTruthTest,
  SoundAndSocialTruthTest ×4, TetradRenderTest, TruthCar3Sweep(Robo) ×3.
  All floor suites fail on main itself (Robolectric env + render harness +
  migration). Source: builds/surge/main-failures.txt.
- Verdict standard for this issue: assemble green + test failures ⊆ floor set.

## Pending (filled at close)
- STOSS-52 commit SHA / push / CI run URL + verdict / merge / issue closure.
