# ISSUE #91 — Wave-3 Report (Audit R2: test-flakiness)

Seat-bundle carrier report to FLUT, Wave-3 cluster lead.
Repo: Sarthak-2007/Focus-app · Worktree: builds/cells/issue-91
Branch: agent/issue-91 · Base: origin/main @ 0a00e95 · Tip: 4b58afc
Push: PUSHED (origin/agent/issue-91 @ 4b58afc, first attempt, ~60 s)
Scope law honoured: ALL changes are in app/src/test only.
`git diff origin/main --stat` = 6 test files, +109/−18, ZERO production files.

Counts: 5 findings — 5 VERIFIED + FIXED-IN-CODE, 0 VERIFIED-FALSE,
0 STANDS-BLOCKED. No assertion was weakened anywhere; two helpers
were made STRICTER (they now throw), one fixture's claimed shape was
re-stated truthfully (see F5).

---

## F1 [HIGH] TruthCar3CaptureBodyTest leaks into the production singleton DB — VERIFIED → FIXED
Commit: cf9e835 · File: app/src/test/java/com/example/TruthCar3CaptureBodyTest.kt

Verified at HEAD: test obtained `MonolithDatabase.getInstance(context)`
(production file DB), inserted a FocusSessionEntity with isActive=true
and a BlockedAppEntity `com.example.chat` (LOCKED), and the file contained
no @After / delete / finally — only a `clearAllCapturedNotifications()`
at the START. Cross-class damage in-repo confirmed (EmergencyBreakRepositoryTest
asserts no active session).

Fix: the inserted session's id is captured (`sessionId = dao.insertSession(...)`)
and the whole body is wrapped in try/finally whose finally runs on EVERY
exit path (assertion failure included):
- `dao.deleteSessionById(sessionId)` — the inserted row, by its own id
- `dao.deleteAppsByPackage(listOf("com.example.chat"))` — the inserted app, by package
- `dao.clearAllCapturedNotifications()` — the capture row the listener wrote
No path through the test leaves rows behind: insert failures before the
try keep sessionId null and write nothing else (the app insert is inside
the try); everything after the session insert is inside the try.
Chose the finally-cleanup route over in-memory migration because the
listener under test resolves the singleton DB internally — the body
needs the production DB by construction. Test code only.

## F2 [HIGH] Real-clock fixtures vs production re-reading the clock — VERIFIED → FIXED
Commit: b44ea3d · Files: RoutinesFinaliseTest.kt, EmergencyBreakRepositoryTest.kt

Verified at HEAD:
- RoutinesFinaliseTest.history_excludesUnfinalisedRoutineRowsAndLegacyRows
  built fixtures at now−1h/now−20m/now−2h and asserted
  compute7DayHistory().last(); production (MonolithRepository.kt:2377-2387)
  re-reads Calendar at call time and buckets by calendar day → deterministic
  failure ~00:00–01:00.
- EmergencyBreakRepositoryTest.spendIsAtomicDerivedAndRefusedAtZero captured
  anchorNow once while completeEmergencyBreak() re-reads the clock and stamps
  the spend with the completion instant's week anchor → getSpendsForWeek(anchorNow)
  returns 0 across a Monday 00:00 straddle where 1 is expected.

Fix (tests only; production clock untouched, not mocked):
- History test: dayStart computed exactly as production derives it
  (Calendar, H/M/S/ms zeroed); every fixture placed at fixed offsets inside
  TODAY — finalised window 10:00–10:30 with 4 intercepts 5 min apart
  (books the same 1200 s), unfinalised 08:00–09:00, legacy startWall 12:00,
  manual startWall 12:20 with the same 600 s — asserted total unchanged
  ((1200+600)/60). Bucket membership no longer depends on run time.
- Week test: anchorNow is now a var, re-derived immediately after the real
  completion from the spend row's OWN stamped instant
  (`weekAnchorMillis(getAllSpendsSnapshot().single{ sessionId }.spentAtWallMillis)`)
  — the single instant production itself used. All later assertions
  (spend count 1, refusal at zero, grace-cancel) read the week the spend
  actually booked, Monday straddle or not. The pre-completion forced-failure
  assertions still read the capture-time anchor and still expect 0 rows,
  which holds in either week because the failed writes write nothing.

## F3 [HIGH] Timeout-swallowing wait helpers — VERIFIED → FIXED
Commit: 77675a7 · Files: DrawerRenderTest.kt, SoundAndSocialTruthTest.kt

Verified at HEAD: DrawerRenderTest.waitForSeed (~112-119) polled 45 s then
fell out of the loop silently (class grep: 7 @Test, 0 assertions — a seed
timeout passed green on an empty drawer); SoundAndSocialTruthTest.settlePark
(~391-402) returned void after 5 s of failed waitForIdle.

Fix: both helpers now throw AssertionError on timeout — waitForSeed with the
observed apps/profiles counts, settlePark with a settle message — the in-repo
idiom (WatchStoreRobolectricTest awaitSnapshot; SoundAndSocialTruthTest's own
awaitTrue already threw). No other behaviour changed; success paths return
exactly as before. This is a strictness INCREASE, not a pass-forcing change.

## F4 [MEDIUM] DrawerPermanenceTest SharedPreferences side-channel — VERIFIED → FIXED
Commit: b7586f6 · File: DrawerPermanenceTest.kt

Verified at HEAD: @Before built a fresh in-memory DB, @After only db.close();
neither cleared prefs, while wordOnlyMonth_appearsInIndex calls
repo.declareDayWord("2026-08-14","ILL") which routes to DayWordStore
(SharedPreferences `monolith_day_words`) — words leaked in both directions
across tests/classes.

Fix: clear `monolith_day_words` SharedPreferences in BOTH @Before and @After
(`getSharedPreferences("monolith_day_words", MODE_PRIVATE).edit().clear().commit()`),
copying the sibling pattern exactly (SeedAndDayWordDrainTest clears in
setup+teardown via the DAY_WORDS_PREFS_NAME constant for the same file;
DrawerRenderTest clears in @Before with the same literal call).

## F5 [MEDIUM] DrawerRenderTest date-dependent populated fixture — VERIFIED → FIXED
Commit: 4b58afc · File: DrawerRenderTest.kt

Verified at HEAD: seedPopulated anchored to LocalDate.now() /
System.currentTimeMillis() — now−30m, now−2h, today.withDayOfMonth(5),
today.minusMonths(1).withDayOfMonth(12), word for calendar today.

Fix: every fixture instant is now a deterministic function of the CURRENT
FILING DAY, `DrawerDay.filingDate(System.currentTimeMillis())` (the app's
own 03:30 rule), each at a wall time after 03:30 so filing date == named date:
- Electrostatics Problem Set 12 — filingToday 14:30
- Botany NCERT Re-Read — filingToday 11:15
- Physics Mock — Full Syllabus — filingToday−1d 21:14
- Organic Chemistry — GOC Worksheet — filingToday−10d 09:15
- Human Physiology MCQ Set — 12th of the month before filingToday's month, 18:40
- Day word declared for DrawerDay.dateIso(filingToday) (the key the
  ViewModel itself uses for "today"), not the calendar date.

Stability proof (by construction, any run date/time): the four filing dates
are always distinct (filingToday−10d falls on day 19–31 when it lands in the
previous month, never the 12th; otherwise it is in the current month while
the physiology fixture is in the previous), and the months present are
always exactly filingToday's month and the one before it. Claim re-stated
truthfully in the fixture comment: five completions across FOUR filing
groups and two months (the old "three groups" claim was itself part of the
drift — its group count changed at month boundaries). The Electrostatics
click target in drawerPopulated_day is a filingToday fixture, always in the
landing scope. sampleCaptures() (digest sheet) was left untouched: it makes
no grouping/month claims and is outside the finding.

---

## User-facing changes
NONE. Every commit touches app/src/test only; production behaviour is
unchanged by construction (verified: `git diff origin/main --name-only`
lists 6 files, all under app/src/test/java/com/example/).

## Proof / CI
Branch CI run (rescue-build, push trigger): run 40985785870, attempt 1.
- Queue: ~39 min queued (fleet-wide contention — every Wave-3 branch
  pushing at once), job executed 17m38s.
- Assemble debug: PASS ✓ (production + debug compile green; APK uploaded).
- Unit tests step: FAIL ✗ — SHARED FLOOR, evidenced, not hidden:
  the identical failure signature appears on the unrelated sibling
  branch agent/issue-133 (run 40991146683): Assemble ✓, Unit tests ✗,
  same annotation — `Cannot invoke "ksp.com.intellij.openapi.application
  .Application.getService(...)" because ... ApplicationManager
  .getApplication() is null`. That is the repo-wide Robolectric/KSP
  environment failure the dedicated squad owns (per the brief's shared
  floor note); it is not specific to this diff, which touches no
  production code and no build files.
- Full job logs could not be downloaded from this box (gh log routes
  returned empty / stream-cancelled repeatedly through the contended
  egress proxy), so a per-test breakdown of the floor failure is NOT
  claimed. Desk-verification of the six edited files instead: every
  call used exists in the current tree (MonolithDao.deleteSessionById
  :87, deleteAppsByPackage :38, getAllSpendsSnapshot :137,
  DrawerDay.filingDate, MonolithWeek.weekAnchorMillis), no new imports
  were needed anywhere, and brace/paren balance checks pass on all
  six files.
- A re-run of the failed job was queued (run 40985785870, attempt 2,
  queued 02:41 UTC) for FLUT to read when the floor squad's fix lands;
  its verdict was still pending (queued) at report time.
- This report file itself: present in the worktree at this path; its
  commit was blocked by the box write-wall (git loose-object writes
  EPERM in waves, plain file writes fine — the Oct 29-30 documented
  pattern) after 4 retries over ~6 min. All five CODE commits are
  already pushed (tip 4b58afc); only this report's commit is
  REPORT-COMMIT-PENDING for FLUT/a later generation to land.

## Seat signatures (bundle-carried, 20 named seats + roster lenses)
Verified end-to-end against HEAD before any edit; each finding re-read
in its production counterpart (MonolithRepository compute7DayHistory /
completeEmergencyBreak, MonolithWeek, DrawerDay, MonolithDao delete paths).
Lenses applied: Verifier (file+line re-check of all 5 — all stood),
Diagnostician (root causes: singleton-DB construction, calendar bucketing,
Monday anchor staleness, prefs side-channel, filing-day drift),
Implementation-quality (no assertion weakened; helpers made stricter;
fixture claim re-stated rather than silently kept),
Technical execution (5 logical commits, push first-attempt, CI read).
Thin lenses bundle-covered per the brief. Signed: seat-bundle carrier,
issue-91 cell, for FLUT (Wave-3).
