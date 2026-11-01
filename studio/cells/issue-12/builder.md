# BUILDER — Issue #12 cell (F-1 + F-3 + F-6)

*Builder (issue #12 cell). DECLARED — code written, NOT compiled, NOT
tested by running. Nobody compiled, per standing rule; Ren compiles
when Sarthak says. Clone: `builds/cells/issue-12/`, branch
`agent/issue-12`, changes uncommitted in the working tree.*

## F-1 — routine-born sessions take the queue head as intention

### Files changed

**`app/src/main/java/com/example/data/RoutineDerive.kt`**
- `derive()` / `deriveOne()` gain ONE last parameter,
  `headTaskTitle: String?` — **no default** in production signatures
  (steer #2): every call site passes it by name, so a forgotten call
  site is a compile error, never a silent return to routine names.
- **The resolver (steer #1): `RoutineDerive.resolveHeadTitle(tasks:
  List<FocusTaskEntity>): String?`** — a pure function living in
  `RoutineDerive` (data layer, beside derive). Takes the caller's
  DAO-ordered snapshot list, returns the first ACTIVE/QUEUED row's
  title, skips blank/whitespace titles to the next eligible row,
  NEVER returns a DEFERRED (or COMPLETED) row, null when nothing is
  eligible. No DAO/Context/clock inside — derive() purity untouched;
  derive() only ever receives the resulting `String?`.
- The seal line in `deriveOne` is the bare glanceable form (steer #2):
  `intention = if (isSleep) routine.name else headTaskTitle ?: routine.name`
  — no helper, no isBlank() inside derive; title hygiene lives in the
  resolver. The fallback is the pre-F-1 behaviour: the change is a
  pure widening.
- **SLEEP exception — LEAD-RULED, not smuggled (steer #2 item 5):**
  SLEEP routines always seal `routine.name` even when a head exists
  ("the head names work, never sleep"), implemented via deriveOne's
  existing local `isSleep`. SLEEP's J2 non-finalisation untouched.
- Stale comment fixed: `DesiredSession.intention`'s `// = routine.name`
  now states the F-1 fact incl. the SLEEP exception (steer #2 item 3);
  the file header's purity note now records that the head title
  arrives as a parameter like every other input.

**`app/src/main/java/com/example/service/RoutineHeartbeat.kt`**
- `reconcileOnce` resolves the head in the same pass that derives and
  materialises: `RoutineDerive.resolveHeadTitle(dao.getAllTasksSnapshot())`,
  passed as `headTaskTitle = headTaskTitle`. The DAO snapshot's own
  ordering (status CASE ACTIVE/QUEUED/DEFERRED, queueOrder ASC, id
  ASC) is the input contract — never any existing next-up helper
  (steer #2 item 4: `completeTaskAndAdvance`'s advance falls back to
  DEFERRED and was explicitly not reused).

**`app/src/main/java/com/example/ui/MonolithViewModel.kt`**
- `evaluateTheme()`'s deriveOne call passes `headTaskTitle = null`
  by name (steer #2 item 6): it reads only `.state`, discards the
  DesiredSession, and no task list exists in its scope — none threaded.

**Seal-time note (precise):** the intention string is frozen into
`DesiredSession` at derive, inside the same reconcile pass whose
materialise → `proveAndSeal` seals the session seconds later.
`sealSession()` itself was NOT touched: by its own law it only
re-anchors + re-signs, and its Boolean is the Throw's guard. Queue
edits after the seal cannot rewrite the row (snapshot by
construction); sealing consumes/promotes/completes nothing; no
`taskId` is written (plan ruling 6 — struck, stays struck).

## F-3 — Horizon bar + HORIZON header kill

**`app/src/main/java/com/example/ui/screens/FocusSequencerScreen.kt`**
- HORIZON header + `TaskHorizonBarView` AndroidView section deleted;
  `computedTotalMins` deleted with it — no minute total survives
  anywhere on the screen (steer #3 item 3). Nothing replaces the bar.
- QUEUE header is now the screen's only `FullBleedSectionHeader`.
  Right side via the component's existing slots, no component change,
  no accent: mono digit = `displayQueue.size`, suffix =
  ` TASK` / ` TASKS`, sealed form appends ` · SEALED` (separator is
  U+00B7 `·`, not the dead header's `•`). Count source is
  `displayQueue.size` in EVERY state — ghost included, the same list
  #NN numbers (steer #3 items 1–2).
- The bar's duplicate "NOTHING QUEUED" printing dies with the bar;
  the 112dp blank leaf is untouched, exactly as built (item 4).

**`app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt`**
- `TaskHorizonBarView` class deleted whole (its fake proportions:
  `estimatedMinutes.coerceAtLeast(10)` per segment). `TowerStreakView`
  in the same file untouched. The now-orphaned `FocusTaskEntity`
  import removed; the pre-existing unused `DailyFocusStat` import was
  left as found.

## F-6 — SPLIT generator kill + HAND-SPLIT (steer #4)

**Retraction acknowledged in code:** the earlier "HAND-SPLIT already
landed" claim does NOT hold on this branch — verified live before
acting: generator at `MonolithModels.kt:168`, called at
`MonolithRepository.kt:2916`, wired at the leaf. All three are now
actually dead:

- `MonolithModels.kt` — `FocusTaskEntity.defaultSubStepsForCategory`
  DELETED (tombstone comment left). It never read its title argument
  and branched on category alone; the composer hard-codes "Study",
  which matched no branch, so every split got the same generic triple.
- `MonolithRepository.kt` — `autoSplitTaskIntoMicroSteps` DELETED
  (the generator's only caller).
- `MonolithViewModel.kt` — `autoSplitTaskIntoMicroSteps` DELETED.
- `MainActivity.kt` — `onAutoSplitTask` argument removed (1 line).
- `FocusSequencerScreen.kt` — `onAutoSplitTask` parameter removed
  from screen + leaf; SPLIT word re-wired as **HAND-SPLIT**: tapping
  SPLIT (same slot/visibility as the old verb — a leaf with no steps
  yet, hidden while the field is open) opens a ruled step field on
  the leaf (the task composer's recessed grammar, indented to the
  sub-step line) with ADD STEP / CLOSE words; each step is written
  by hand and lands through the previously-dead `onAddSubStep`
  (screen param → leaf param → `viewModel::addTaskSubStep` →
  `repository.addTaskSubStep`). The field stays open across adds;
  written steps appear in the numbered list above it. Ghost leaves
  never show the field or open it.

**The one tangle, stated precisely (not forced):** truly blank
*persisted* ruled lines are impossible in the current model —
`parseSubSteps` drops empty titles, `serializeSubSteps` round-trips
only titled steps, and `addTaskSubStep` (VM + repository) refuses
blanks. Making blank lines persist would mean changing the sub-step
model + its progress fraction + blank-row rendering on Deck/Drawer
surfaces — a model change beyond a clean F-6 wiring. Shipped form:
the blank lives as the leaf's ruled field; only written steps
persist. If Amt/Ren want persisted blanks, that's its own priced
change to `MonolithModels` parsing.

**Test adaptation (flagged loudly):**
`app/src/test/java/com/example/ExampleRobolectricTest.kt`, one
segment of the omnibus Solar Queue test. It called the deleted
`repository.autoSplitTaskIntoMicroSteps` and asserted 3 sub-steps —
a count that was only exact because the generator OVERWROTE the
notes. The segment now hand-adds steps via `addTaskSubStep`. The
task's edited notes ("Include p99 latency chart") already parse as
ONE step, so TWO hand-added steps keep the asserted total at exactly
3 (append, never overwrite), and the following toggle assertion
(step 0 → done) is unaffected. No other assertion in the file was
touched. If the merge prefers the test untouched, the price is
keeping the generator — there is no third state.

## Tests written (NOT run)

`app/src/test/java/com/example/RoutineDeriveTest.kt` — 19 pre-existing
cases keep their exact meaning: the test-local `derive()` wrapper is
the ONLY place a `headTaskTitle = null` default exists (steer #2).
15 new cases, fixtures = the real seeded morning (FASTED BLOCK
05:00–08:00 from `routineTargets()`; seeded queue titles verbatim —
ACTIVE "Finish Chapter 4 Problem Set (#1–6)", QUEUED "Synthesize
Lecture 9 Architecture Notes", QUEUED "Debug Dynamic Programming
Memo Table"):

- Resolver (7): seeded morning → ACTIVE head; DAO-order honoured
  (ACTIVE ahead of QUEUED regardless of queueOrder); DEFERRED-only →
  null; COMPLETED+DEFERRED → null; blank head → next eligible;
  all-blank → null; empty → null.
- Derive (8): head taken as intention (profileName/window/remainder
  unchanged); empty queue → routine name; DEFERRED-only → routine
  name; snapshot semantics (same inputs → equal result; a later head
  does not rewrite an earlier derivation); two routines one morning
  (FASTED BLOCK at 06:00 and DAY BLOCKS at 10:00 read the same head
  at their own seals; queue unconsumed after both); SLEEP with head
  available still seals "SLEEP" (+ J2 non-interference re-asserted);
  head does not resurrect a sub-5-minute remainder; head does not
  override a pause.

## Deliberately NOT touched

- **DB schema** — unchanged; the existing `intention: String` column
  carries F-1 whole. No migration, no provenance column (parked by
  the lead — Ren's merge call).
- **`estimatedMinutes` field** (steer #4 item 4) — survives
  everything; only the bar's proportions died. The field is still
  read by the leaf's #NN/START prints, receipt, dial seeding, undo.
- **`completeTaskAndAdvance` / `promoteTaskToActive` intention
  semantics** (steer #4 item 2) — the F-1 diff does not intersect
  either function; the Critic's ESR-02 charge against the advance
  path is Ren's ruling to make, not this cell's fork.
- F-7 tear respin, F-2/F-4/F-5/F-8/F-9 — not in this pass (batch-10
  per plan). Intention prints bare verbatim on every surface; no
  provenance is printed anywhere (Finisher-4).

## Awareness + risks for the merge

- **Session dedup consequence (steer #4 item 3 — stated, path NOT
  touched):** the seed-sync dedup in `MonolithRepository`
  (~:408-418) keys non-active sessions by
  `intention|targetDurationSeconds|completedTasksSummary`. Before
  F-1, two routine sessions from different routines differed by
  routine name; now two routine sessions sealing the SAME head with
  the same target duration and the same (typically empty) summary
  COLLIDE under that key, and a later seed-sync could delete one as
  a duplicate. Consequence is inherent to F-1's spec; fixing the
  key (e.g. adding routineId/window) is a merge/Ren decision.
- Uncompiled by rule: brace/paren balance and reference sweeps were
  checked statically (no dangling `TaskHorizonBarView`,
  `onAutoSplitTask`, `autoSplitTaskIntoMicroSteps`,
  `defaultSubStepsForCategory` references remain outside tombstone
  comments), but the first real compile is Ren's and may still
  surface something paper review cannot.
- QUEUE header now prints `0 TASKS` on an empty queue (mono "0" is
  always composed) — that is steer #3's specified form.
- During the 260ms tear ghost window the header count includes the
  ghost leaf (one above the live queue) — ruled in steer #3 item 2
  (one source: the paper in hand), stated here so it isn't later
  misread as an off-by-one.
- HAND-SPLIT parity note: SPLIT's word is visible only on leaves
  with no steps (as the generator's verb was); once a leaf has
  steps there is still no add-another-step affordance on this
  screen after CLOSE. Widening that is a design call, not made here.
- Amendment log vs my first draft (for the record): an
  `intentionFor()` helper + `= null` defaults were written first,
  then REMOVED under steer #2 (bare seal line, no production
  defaults); SPLIT was first removed outright, then re-wired as
  HAND-SPLIT under steer #4's retraction. Final state follows the
  steers, not the first draft.

— Builder (issue #12 cell)
