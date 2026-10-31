# ISSUE #106 — Wave-3B Report · Audit R3 · tasks-lived

Builder-verifier seat under PFAD. Verified every finding against
CURRENT `origin/main` (tip `0a00e95` at cell creation; audit line
numbers were from an older tree — current locations cited below).
All five findings HELD. All five fixed in code on
`agent/issue-106`. No local compile (per brief); CI is the verdict.

## Finding 1 — [CRITICAL] Seed-sync silently deletes a same-titled task — HELD, FIXED

- Was: `MonolithRepository.kt:400-406` (current tree) —
  `ensureSeededAndSyncPackages` keyed non-COMPLETED tasks by
  `"${title.lowercase()}|${status}"` and `dao.deleteTaskById`'d
  every repeat. No UndoDeleteStore copy, no banner. Runs at every
  process start and every PARK open.
- Fix: `MonolithRepository.kt:394-424` (now). The dedupe key is
  now the task's FULL identity — title, status, category,
  sub-steps/notes, estimate, seconds spent, queue order,
  completion stamps, started offset. Only an exact double-insert
  (the same insertion written twice by a race) collapses; two
  different user tasks that share a title are both kept, always.
  The COMPLETED permanence guard is unchanged. Where two exact
  rows did collide, the kept row is the earlier id — and since the
  rows are field-for-field identical, no history or progress
  exists on the dropped copy that the kept copy lacks.
- User-facing: two tasks both called "Revision" (or "Read
  Chapter 4" for two subjects) both survive every relaunch.

## Finding 2 — [HIGH] Deck advance rewrites the sealed session's receipt title — HELD, FIXED

- Was: `MonolithRepository.kt:2575-2586` (current tree) —
  `completeTaskAndAdvance` set `currentSession.copy(intention =
  promoted.title)` unconditionally. The receipt is built from
  that field (`assemblePendingReceipt`, title = row.intention,
  deflections filtered by `log.sessionIntention == row.intention`,
  `MonolithRepository.kt:1552ff`), so the sealed slip was
  retitled and its intercepts dropped. `promoteTaskToActive`
  (`:2667ff`, seal refusal at its ESR-02 block) refused the same
  write — two doors, contradictory records.
- Fix: `MonolithRepository.kt:2615-2631` (now) —
  `completeTaskAndAdvance` applies the IDENTICAL seal refusal:
  the promotion to ACTIVE proceeds, but while
  `sealedSessionIdFromMarker() == currentSession.id` the
  intention write is skipped. Unsealed advance still moves the
  intention, exactly as `promoteTaskToActive` does. Queue-side
  only, per the brief — the receipt side (#105, finding 2) is
  untouched.
- User-facing: finishing a task from the Deck during a sealed
  session no longer renames the session's own receipt or loses
  its logged intercepts.

## Finding 3 — [HIGH] UNDO banner born off-screen — HELD, FIXED

- Was: `FocusSequencerScreen.kt:263-300` (current tree) — the
  banner is a LazyColumn item near the TOP of the list (index 2:
  Horizon, composer, banner); nothing scrolled it into view.
  Life: `UndoDeleteStore.WINDOW_MILLIS = 5_000L`
  (`UndoDeleteStore.kt:99`), dismiss delay in
  `MonolithViewModel.kt` delete path. A DEL tapped on leaf #8
  birthed the banner above the viewport and it expired unseen.
- Fix: `FocusSequencerScreen.kt:136-149, 176-179` (now) — the
  list carries a `rememberLazyListState()`; a
  `LaunchedEffect(deletedTaskForUndo)` scrolls to item 2 the
  moment a delete lands, so the banner is born visible wherever
  the user is scrolled, with its full 5-second window usable.
  The banner itself, its slot and the 5-second window are
  unchanged (no downgrade; the cure is visibility, per brief).
- User-facing: delete a task at the bottom of the queue and the
  UNDO banner appears on screen immediately.

## Finding 4 — [MEDIUM] Adding the only task force-promotes it mid-session — HELD, FIXED

- Was: `MonolithRepository.kt:2458-2461` (current tree) —
  `shouldBeActive = promoteToActive || !hasActive`, and the
  composer passed `promoteToActive = orderedQueue.isEmpty()`
  (`FocusSequencerScreen.kt:141`). Verified end-to-end: crowned
  ACTIVE → ViewModel `activeTask` collector pushes the title
  into the intention → `accrueActiveTaskSeconds` bills it.
  Worse, the accrual itself had a second crown: its QUEUED
  fallback (`:2425-2444`) promoted the queue head to ACTIVE on
  the next tick, so keeping the jot QUEUED at add-time alone
  would not have held.
- Fix, three layers, all now:
  1. `MonolithRepository.kt:2480-2496` — `addTaskToQueue`: the
     no-active force-promotion applies only while NO session is
     running (`dao.getActiveSessionSnapshot()?.isActive`).
     Explicit `promoteToActive = true` (the Airlock micro-step
     verb, whose act is promotion) is honoured in both worlds.
  2. `FocusSequencerScreen.kt:156-161` — the composer passes
     `orderedQueue.isEmpty() && !isSessionActive`.
  3. `MonolithRepository.kt:2440-2463` — `accrueActiveTaskSeconds`
     credits ONLY an ACTIVE task; the QUEUED fallback is gone.
     No ACTIVE task = nothing billed (WallAccrual's null-id
     anchor advance, the same mechanism as Safe Break).
- Innocent case kept and tested: the first task added to an
  empty queue while NO session runs still becomes ACTIVE.
- User-facing: jotting a task down mid-session leaves it queued
  for later; NOW does not move and no time is billed to it.

## Finding 5 — [MEDIUM] SPLIT stamps the same problem-set steps on every task — HELD, FIXED

- Was: `MonolithModels.kt:168-195` (current tree) —
  `defaultSubStepsForCategory(title, category)` never read
  `title`; every composer task is category "Study", which fell
  to the else branch (the problem-set triple). Design intent
  read before curing: the studio's own Tasks audit
  (`studio/plan/AUDIT-tasks.md` item 11, ESR-20 in
  `EVERY-SCREEN-REVIEW-BUILD1.md`) convicts the same generator —
  the verb promised derivation and delivered a template; the
  category triples for Code/Writing/Exam are genuine method
  templates for those kinds of work, and the problem-set triple
  is one only for problem work.
- Fix: `MonolithModels.kt:168-252` (now) — the generator reads
  the task's own words, in order: (1) a title that lists its
  parts on `;` / `•` / `|` is split on its own separators,
  verbatim; (2) title/category signals route to the matching
  method — code, writing, exam, problem set (problem words
  only), language drill, reading/revision recall; (3) anything
  else gets three phases named with the task's own title
  (Begin / Continue and complete / Review …: <title>). SPLIT
  the verb is untouched — no downgrade, no removal; the cure
  is derivation plus the template surviving only where it is
  true.
- User-facing: SPLIT on "Revise History dates" gives
  reading/recall steps, on "Learn French verbs" a language
  drill, on a problem set the problem-set steps — and a title
  written as a list splits into that list.

## Tests added (CI is the verdict — not compiled locally)

- `app/src/test/java/com/example/TasksLivedFixesTest.kt` —
  11 tests: same-title pair survives seed-sync ×2 runs; exact
  double-insert collapses to one; sealed advance leaves the
  session intention untouched; unsealed advance still moves it;
  mid-session jot stays QUEUED and a session tick neither
  crowns nor bills it; first task outside a session becomes
  ACTIVE; SPLIT routing for history / French verbs / problem
  set / self-listed title / unmatched title; auto-split of a
  Writing task still yields 3 steps (the standing
  ExampleRobolectricTest expectation is preserved by design:
  a Writing title routes to the writing template).
- `app/src/test/java/com/example/TasksUndoBannerRenderTest.kt` —
  compose render test: scroll to the foot of a 12-task queue,
  land a delete, assert the UNDO button is displayed.

## Files changed

- `app/src/main/java/com/example/data/MonolithRepository.kt` (F1, F2, F4)
- `app/src/main/java/com/example/data/MonolithModels.kt` (F5)
- `app/src/main/java/com/example/ui/screens/FocusSequencerScreen.kt` (F3, F4)
- `app/src/test/java/com/example/TasksLivedFixesTest.kt` (new)
- `app/src/test/java/com/example/TasksUndoBannerRenderTest.kt` (new)
- `studio/plan/ISSUE-106-WAVE3-REPORT.md` (this file)

---

## Closure addendum — DURCH-C rebase (Oct 30, 2026)

The Wave-3B branch predated issue #124 (typing-intention). On rebase
onto current main, the closure seat verified each finding against
CURRENT main code:

- Findings 1, 3, 4, 5 were still live in main in their original form;
  the Wave-3B cures are ported here (dedupe full-identity key with
  Locale.ROOT; banner scrollToItem(2); composer gate + session guard
  + ACTIVE-only accrual keeping main's persistAccrualAnchor; SPLIT
  derivation with Locale.ROOT).
- Finding 2 was ALREADY CURED IN MAIN, in a STRONGER form: #124 froze
  a session's intention at arm time for its whole life — no advance,
  sealed or not, rewrites it. The Wave-3B sealed-only hunk would have
  REGRESSED that law (it re-introduced intention rewriting for
  unsealed sessions), so it was DROPPED on rebase, and the test
  `unsealedAdvance_stillMovesSessionIntention` was rewritten as
  `unsealedAdvance_alsoDoesNotRewriteSessionIntention` pinning the
  standing #124 law. Divergence from the original disposition is
  deliberate and recorded here.
