# ISSUE #110 — WAVE 3 REPORT · Audit R3 journey: vault-history-lived

**Branch:** `agent/issue-110` · **Base:** `origin/main` 0a00e95 · **Cell:** issue-110 (REISE cluster)
**Commits:** `e4511ff` (repository truth fixes) · `d34d7bd` (Drawer prints) · `8e3b631` (tests) · this report
**Counts:** 5 findings → **2 FIXED · 1 FIXED (interim relabel) + STANDS-BLOCKED (priced) · 1 BY-DESIGN + DECISION-NEEDED · 1 BY-DESIGN/DECISION-NEEDED** — detail per finding below. Zero schema changes, zero new migrations, zero downgrades.

---

## Per-finding dispositions

### 1. [HIGH] Drawer stores task completions, not sessions — **BY-DESIGN + DECISION-NEEDED (flagged, not built)**

Verified against current code: `MonolithDao.getStoredPage` / `getStoredMonths` filter
`status='COMPLETED'` on `focus_tasks`; no Drawer path queries `focus_sessions`
(DrawerScreen.kt, MonolithDao.kt:206-212). The audit's facts hold — the conclusion
does not survive Sarthak's settled design law:

- The Drawer's own header (DrawerScreen.kt) states the ruling in code: *"Permanent
  storage for finished tasks… It is not a dashboard, report card or attendance book:
  no totals, no streaks, no percentages, no counts, no summons."*
- Settled law (his rulings): **Monolith is a BLOCKER, not a tracker.** The Drawer is a
  quiet drawer of FINISHED TASKS, stored forever, searchable, with no scores and no
  day-grading. Study tracking lives in YPT + GMC, never in Monolith.

Building session rows into the Drawer would turn it into the session tracker he
ruled out. **DECISION-NEEDED:** if Sarthak ever wants "did I actually focus this
week?" answered inside Monolith, that is a new ruling, not a fix — the session data
already exists (`focus_sessions`, and the Deck-side session counts in
MonolithViewModel remain live), so the day he asks, it is a display decision, not a
data problem. Nothing built.

### 2. [HIGH] Lifetime SPENT dumped on the completion day — **FIXED (truthful relabel, interim) + STANDS-BLOCKED (per-day split, priced)**

Investigation, as ordered — is accrual session-segmented in stored data? **No:**

- `FocusTaskEntity` carries ONE accrual scalar, `actualSecondsSpent`
  (MonolithRepository.accrueActiveTaskSeconds adds wall time into it across every
  session until completion). There is no segment table and no per-session split.
- `completedInSessionId` names only the COMPLETING session — it says nothing about
  earlier sessions where most accrual may have happened.
- `startedAtSessionElapsedSec` was the candidate segment witness; it is a landed
  column that **no writer in main ever writes** (MonolithModels.kt comment: *"this
  build lands the column only"*; grep finds zero writes outside migration + the
  undo-delete prefs mirror). It cannot reconstruct anything.
- Session rows carry no per-task accrual either (session `taskId` is likewise a
  column-only Arm-Ticket bond).

A truthful per-day split is therefore **not computable from existing data**.

**Interim cure built (honest relabel, commit d34d7bd):** every SPENT print now says
what it is — shelf rows print `TOTAL …`, the detail prints `SPENT IN TOTAL …` — and
the DrawerScreen header records the reason in code. The shelf no longer claims the
finish day contained time spread over earlier days.

**STANDS-BLOCKED, priced:** the real cure needs accrual-segment storage the schema
train must own: one segment row per (task, session) — e.g. a new
`task_accrual_segments(taskId, sessionId, dateIso, seconds)` table written by
`accrueActiveTaskSeconds` at session end / task switch — a **new Room migration
(DB v12 → v13)**, a DAO + repository writer change, a Drawer grouping change, and
backfill honesty (pre-migration rows stay lifetime-only, labelled TOTAL forever).
No schema change was made here, per the standing law.

### 3. [HIGH] No-session completions stored as SPENT 0 SEC; receipt inflates to "1m actual" — **the lie FIXED (e4511ff); the storing BY-DESIGN**

Verified: `completeTaskAndAdvance` (both layers) has no session guard — it stamps
`completedAtMillis = now`, `completedInSessionId = currentSession?.id ?: 0` and
writes the COMPLETED row with no session ever run. And
`completedTaskSummaryEntry` (MonolithRepository.kt:2602) did carry
`(actualSecondsSpent / 60).coerceAtLeast(1)`.

- **The inflation is fixed.** The entry now prints the real accrual: `0s` →
  `no session time`; a real sub-minute accrual → `<1m actual`; full minutes
  unchanged (`24m actual`). `reopenStoredTask`'s string-match repair uses the same
  single formatter, so write/strip can never drift apart.
- **The storing stands, BY-DESIGN:** he did finish the task, and the Drawer is the
  finished-task drawer — a completion outside a session is still a finished task,
  stored forever at its truthful 0 seconds (never deleted, never dressed up).
  Proven by test: `completionWithoutSession_isStillStored_withZeroSeconds`.

### 4. [MEDIUM] Day words only declarable today — **BY-DESIGN (verified deliberate) + DECISION-NEEDED; late-filing NOT built**

Verified: `MonolithViewModel.declareTodayWord` hardcodes
`DrawerDay.filingDate(System.currentTimeMillis())`, and `DayHeading` renders the
tappable words only when `isToday`. The deliberateness is documented at the write
site itself: *"only today's filing date is declarable from here — the Drawer files
words, it does not rewrite past days."* The underlying store is NOT the blocker —
`repository.declareDayWord(dateIso, word)` already accepts any date, and
`DayWordStore` is keyed by ISO date — so late-filing would be a small build
**if** Sarthak rules it. It is a design change (the Drawer rewriting a past day),
so per orders it is **DECISION-NEEDED**, flagged here, and deliberately not built
unilaterally.

### 5. [MEDIUM] Cabinet/Curios amnesiac; 'partial_stamp' matches no catalog row — **FIXED (e4511ff + d34d7bd)**

Verified: forge picks `CurioCatalog.artifacts.shuffled().first()`
(MonolithRepository, ending write), the cabinet read was
`SELECT DISTINCT forgedArtifactId` (MonolithDao.kt:74) collapsing every repeat, the
detail printed only `FORGED — IT STANDS IN THE CABINET` / `NOT YET FORGED`, and the
disarm doors stamp `forgedArtifactId = "partial_stamp"` — an id in no catalog row.

Fixed, all from data the app already stores (a repository derivation, **not** a
schema change):

- `MonolithRepository.forgeStats()` derives per-artifact **count + most recent
  forge instant** from session rows (`forgedArtifactId` + `endWallMillis`, falling
  back to `startWallTimeMillis` for pre-v11 rows). Wired repository → ViewModel
  (`forgeStats` StateFlow, re-read in `refreshDrawer`) → MainActivity → DrawerScreen.
- Cabinet list: repeat forges print `FORGED ×n`. Cabinet detail prints the record:
  `FORGED ×3 · LAST TUE 27 OCT 2026` (Anonymous Pro, record voice).
- `partial_stamp` is now the named constant `PARTIAL_STAMP_ID` (the disarm writer
  uses it) with a real catalog entry, `PARTIAL_STAMP_ARTIFACT` — *"Partial Stamp ·
  A Span Ended Early"* — whose lore says plainly it is **not a forged curio**: a
  sealed span that ended early with real minutes banked. It joins the cabinet list
  **only when a real session carries it**, is labelled `STAMPED` / `STAMPED ×n ·
  LAST …`, and never prints as forged. The six curios on offer stay six.
- What does NOT change: the forge still chooses at random (that is the cabinet's
  own printed rule, DRW-01), and routine finishes still forge nothing — both are
  standing design, not amnesia.

---

## Evidence

**Tests:** `app/src/test/java/com/example/VaultHistoryTruthTest.kt` — 9 tests, all
passing in CI (Gradle names every failing test in the run log; this class appears
zero times):

1. `receiptEntry_zeroAccruedTask_printsNoSessionTime_neverOneMinute`
2. `receiptEntry_subMinuteAccrual_printsLessThanOneMinute`
3. `receiptEntry_fullMinutes_printsAsBefore`
4. `completionWithoutSession_isStillStored_withZeroSeconds`
5. `forgeStats_countsForges_andKeepsMostRecentInstant`
6. `partialStamp_hasAnHonestCatalogEntry_andIsNotACurioOnOffer`
7. `disarmEarlyEnd_stampsTheConstant_andForgeStatsCountIt`
8. `drawerRowAndDetail_printSpentAsLifetimeTotal`
9. `cabinetDetail_printsForgeCountAndLastDate_stampIsHonest`

Neighbour surface in the same run also green: DrawerPermanenceTest,
DrawerDayTest, SeedAndDayWordDrainTest, ExampleRobolectricTest (which asserts the
`partial_stamp` value and the session summary) — none appear in the failure list.

**CI:** run **40988626930** — https://github.com/Sarthak-2007/Focus-app/actions/runs/40988626930
(tip 8e3b631; queued ~29 min in tonight's repo-wide queue jam before starting).

- **Assemble debug: SUCCESS** (APK artifact `monolith-debug` uploaded).
- **Unit tests: FAILURE — the shared CI test floor, named precisely:** 315 tests
  completed, **37 failed, zero of them in #110's surface.** Failure census by cause,
  read from the run log:
  - **26 × `java.io.FileNotFoundException`** in render-capture tests —
    ScreenshotHarnessTest.kt:150 (13), DrawerRenderTest.kt:136 (7),
    CascadeDeckRenderTest.kt:163 (5), TetradRenderTest.kt:46 (1): their `capture()`
    writes PNGs to hardcoded local-machine paths (`/home/hatch/workspace/…`) that do
    not exist on a GitHub runner. Structural, pre-existing, cannot pass on clean CI.
  - **2 × `SQLiteException`** in v11→12 migration tests
    (BreakMigrationTest.kt:91, ShortsSniperTruthTest.kt:134) — untouched by this
    branch (zero migration/database files in the diff).
  - The remaining 9, all in subsystems this issue does not touch:
    3 × coroutine `UncaughtExceptionsBeforeTest` aborts, 2 × AssertionError at
    SoundAndSocialTruthTest.kt:422, 3 × TruthCar3Sweep failures (2 IllegalState +
    1 AssertionError), 1 × RuntimeException at ChromeWaveUnitTest.kt:52
    (warrant derivation).
  
  This is the shared test floor the wave brief flagged as being killed centrally;
  it is the run's **only** red. No #110 test, and no test of any class this branch
  modified the behaviour of (repository receipt/forge paths, Drawer), failed.

---

## PLAIN-WORDS user-facing changes

- A task you tick done before any focus time reaches it no longer claims
  "1 minute" on the session receipt. It says it plainly: **no session time**.
  A task you worked on for under a minute says **<1m**, not a rounded-up minute.
- In the Drawer, the time on a finished task now says **TOTAL** — it is all the
  time that task ever took, filed under the day you finished it, and it no longer
  pretends that day held all of it.
- The Curios cabinet now remembers: each piece shows **how many times it was
  forged and the date of the last forge**. Repeat forges count instead of
  vanishing.
- The **Partial Stamp** — the mark left when a sealed session ends early with
  real minutes banked — now has its own honest row in the cabinet, labelled
  STAMPED, saying what it is. Before, it was an invisible code no screen could
  name.
- Nothing else changed on screen: the Drawer still stores finished tasks only
  (by Sarthak's ruling), and day words are still declared for today only
  (flagged for his decision, not changed behind his back).

## Seat sign-offs (bundle worn by this cell; rest LEAD-COVERED by REISE)

- **Human / Day-fit + User (lead):** walked the journey as he'd ask it tomorrow —
  "did I actually focus this week?" The Drawer still can't answer it, and under
  his own ruling it mustn't try: that's YPT/GMC's job. What the Drawer owes him is
  that everything it DOES print is true — after this wave, it is. — signed
- **Verifier:** every finding re-checked line-by-line against the current tree
  (post Wave-2 #8 Drawer merge): F1 facts hold, disposition overruled by design
  law; F2's segmentation hunt executed (scalar accrual, unwritten offset column,
  completing-session-only id); F3 both halves verified at both layers; F4's
  deliberate-intent comment verified at the write site; F5 verified at forge,
  DAO, disarm and cabinet. — signed
- **Diagnostician:** the root pattern across F2/F3/F5 is one disease — stored
  aggregates printed as if they were segments (lifetime as a day, zero as a
  minute, DISTINCT as a record). Cures were chosen per data reality: relabel
  where no segments exist, compute where rows already hold the facts, refuse to
  fabricate where neither holds. — signed
- **Brutal critic:** F1's audit framing was tracker-think and deserved the
  BY-DESIGN strike; F4's "fix" would have been a design change smuggled in as a
  bugfix — flagged, not built. The ×count in the cabinet is a record of forges,
  not a score — it grades nothing and ranks nothing. The relabel in F2 is an
  interim honesty patch, and the report says so instead of dressing it as the
  split. — signed
- **Builder:** 3 surgical commits, 5 main-source files + 1 test file, no schema,
  no migration, no downgrade; receipt formatter stays single-sourced so
  complete/reopen can never drift. — signed
- **Keepers of Record:** this report + the DrawerScreen header note + the
  PARTIAL_STAMP_ARTIFACT lore keep the record: what was fixed, what was refused
  and why, what is blocked and what it costs. — signed
- **Thoughtful:** the Partial Stamp's lore was written to honour the minutes
  without honouring the quit — "the minutes were real, the finish was not."
  — signed
- **All other seats: LEAD-COVERED by REISE** (cluster lead), per the cell brief.

## Flagged upward (for REISE / Sarthak)

1. **DECISION-NEEDED (F1):** session history inside Monolith — only on his new
   ruling; data already exists.
2. **DECISION-NEEDED (F4):** late-filing of day words — small build on his
   ruling; store already supports any date.
3. **STANDS-BLOCKED priced (F2):** per-day SPENT split — needs the accrual-segment
   table + DB v12→v13 migration, owned by the schema train.
4. **CI floor:** run 40988626930's 37 failures are the shared floor (26 hardcoded-
   path capture FileNotFound + migration/sound/truth-car classes) — central kill
   already in motion per the wave brief; named here so this branch's red is never
   misread as #110's.
