# Issue #32 — models (Wave 3, cluster C3 / HERZ)

Branch: `agent/issue-32` · Base: origin/main @ e29acdf · Code commit: beaecac
CI: run 40973772649 (rescue-build, push) — status recorded in the lead's
report to HERZ; verdict read by HERZ before closure. Issue body NOT edited,
issue NOT closed (HERZ dispositions).

Wave-2 check first: `git diff origin/main...origin/agent/issue-7|11|12 --
MonolithModels.kt` — issue-7 touches only RoutineSchedule comments,
issue-11 deletes DistractionTriggerReason, issue-12 deletes
defaultSubStepsForCategory. None cures any #32 finding → no
FIXED-ELSEWHERE claims.

## Per-finding dispositions

### F1 [CRITICAL] estimatedMinutes default 25 contradicts its invariant — FIXED-IN-CODE (beaecac)
Verified at MonolithModels.kt:111 (`val estimatedMinutes: Int = 25` vs the
comment at 106–110: stored 0 = no estimate; surfaces print only when > 0).
Cure: default → 0 in the entity, and in the two funnel defaults that
fabricated the same value for omitting callers —
MonolithRepository.addTaskToQueue (was `Int = 25`) and
MonolithViewModel.addTaskToQueue (was `Int = 25`). Explicit callers are
untouched (seeds pass 25/45 explicitly as stored history; MainActivity
passes the user-typed mins; ViewModel:1632 already passed 0).
Test: `task_defaultEstimate_isZero_noEstimateGiven`.

### F2 [HIGH] Zero validation — FIXED-IN-CODE (beaecac), trap-aware boundary stated
Verified: no init{}/require/check in the file; FocusProfileEntity
(252 ff.), RoutineEntity (550 ff.), FocusSessionEntity (268 ff.) accepted
hour=99, daysMask=-1, blocks=0, currentBlockIndex=999.
BOUNDARY CHOSEN, and why: validation at construction/write funnels +
coercion at read/derive points — explicitly NOT init{require()}. Room
loads legacy rows through these same constructors, so a throwing init
crashes on existing DB rows and bricks the app on upgrade; coercion
cannot throw and never rewrites stored data by itself.
- Write funnels: `FocusProfileEntity.sanitized()` applied in
  Repository.saveCustomProfile; `FocusSessionEntity.sanitized()` in
  Repository.startSession; `RoutineEntity.sanitized()` in
  Repository.updateRoutine (the funnel ViewModel.persistRoutine routes
  through). Ranges: profile hours 0..23, minutes 0..59, endHour 0..24
  (24 is the lawful midnight-end sentinel of the existing
  updateProfileTimeWindow writer, coerceIn(1,24) — clamping to 23 would
  have broken it), defaultMinutes 1..1440; session durations/counts
  floored ≥0, blocks 1..64; routine minutes wrapped mod-1440, daysMask
  masked to 7 bits.
- Derive points: RoutineSchedule (durationMinutes, nextStartMillis,
  nextEndMillis, windowContaining, latestClosedWindow, isLive,
  isActiveNow) reads `routine.sanitized()`; FocusSessionEntity spans
  read safeTotalBlocks / safeCurrentBlockIndex and floor elapsed /
  breakTaken / target / penalty. Sentinels deliberately untouched
  (heldAppsAtSeal -1 = uncaptured, evidenceMask bits).
Tests: garbageRows_constructWithoutThrowing (the anti-brick pin),
profile_sanitized…, routine_sanitized…, routineSchedule_corruptRow…,
session_garbageBlockCounts….

### F3 [HIGH] Stringly-typed state + silent fallbacks — FIXED-IN-CODE (beaecac) for the evidenced resolvers; remainder evidenced below
Verified: resolvedStatus (was MonolithModels 125–126) laundered garbage
→ QUEUED; resolvedTier (245–249) garbage → isBlocked fallback, which
for isBlocked=false meant OPEN.
Cure: resolvedStatus garbage → DEFERRED + `statusIsCorrupt` marker.
QUEUED is promotable (a garbled COMPLETED row resurrected into the live
queue); DEFERRED is inert, visible in Saved-for-Later, and never
auto-promoted ahead of a real QUEUED task — callers all branch on the
four real statuses, so DEFERRED is tolerated everywhere QUEUED was.
resolvedTier: garbage + isBlocked → LOCKED (fail-CLOSED; a corrupt tier
can no longer silently become OPEN and defeat the block); garbage +
isBlocked=false → OPEN but marked by `tierIsCorrupt` (inventing a block
on a typo would lock a user's app). Corruption is marked, not laundered.
Remainder of the audit's string list, verified, no resolver launders
them into an enforcement decision: DayNoteEntity.word is closed by
construction at its only writer (DayWordStore.kt:54 WORDS =
REST/ILL/FAMILY, drain refuses anything else); routineOutcome/outcome
are write-once stamps written from enum `.name` at the finalise funnel
(MonolithRepository:1043) and read as opaque stamps, never resolved
back into an enforcement enum in this file. Changing those stored types
would require a schema change — FROZEN at v12, out of scope by law.
Tests: task_corruptStatus…, app_garbageTier… (×2).

### F4 [HIGH] BlockedAppEntity dual truth isBlocked vs blockTier — FIXED-IN-CODE (beaecac)
Grep evidence (repo-wide, main sources): every reader already reads
resolvedTier (AppRules, MonolithRepository:266/1106/1319, accessibility
/ shield / notification services, ViewModel, ShieldMatrixScreen) — the
only direct isBlocked reader is the DAO ordering
(MonolithDao:13 `ORDER BY isBlocked DESC`). Every writer already wrote
both fields from ONE tier value (Repository cycle/setAppBlockTier via
dao.setAppTier, addCustomApp, AppCatalogReconcile:97–98,
applySmartLoadout, applyProfile) — the divergence hole was copy():
nothing tied the fields after construction.
Cure (schema frozen, no column removed): blockTier is the declared
truth, isBlocked its derived cache. `BlockedAppEntity.normalized()` is
the single derivation point — it rewrites both fields from resolvedTier
(and floors counts). applySmartLoadout and applyProfile now set the
tier ONLY and normalize (for applyProfile, shouldBlock ≡ tier != OPEN
in every branch, MESSAGING→PEEK included, so behaviour is identical).
Any future copy()-diverged row is detectable via tierIsCorrupt and
repairable via normalized(). Tests: app_divergentCopy… (the audit's
exact copy(blockTier=OPEN) case), app_peekTier_normalized….

### F5 [MEDIUM] Span math: magic 600 vs named constant — FIXED-IN-CODE (beaecac); wider audit claim VERIFIED-FALSE in part
Verified current code (Truth Car 3 already landed): spanElapsedSeconds
no longer prices completed breaks at 600 at all — it books
breakTakenSeconds (actual seconds, written at break exit); only the
IN-BREAK term reads SAFE_BREAK_DURATION_SECONDS, and spanTargetSeconds
(MonolithModels ~399) still hardcoded `* 600`.
Cure: spanTargetSeconds now prices breaks at
MonolithRepository.SAFE_BREAK_DURATION_SECONDS — the same constant the
elapsed side reads; change it once and both move.
VERIFIED-FALSE part: the audit's claim that the span should follow
StudyModePreset breakMinutes (5/10/15) does not hold against current
code — Safe Break is a fixed 10-minute mechanism everywhere it is
armed (MonolithRepository:110, :1844, :2027); presets select study
block length/blocks and never arm a Safe Break of their own length.
Test: spanTarget_pricesBreaksAtTheNamedConstant; existing BreakSpanTest
expectations unchanged numerically (constant = 600 today).

## Tests
New: app/src/test/java/com/example/ModelsInvariantsWave3Test.kt —
13 pure-logic JUnit tests (existing plain-JUnit style, cf.
RoutineDeriveTest). No local compile (law: CI is the court).

## The 20 seats (internal lens passes, signed)
- Crazy-ideas: "Fail-closed tier + DEFERRED-for-corrupt-status came from asking what a garbage row should DO, not what it should be called." — Crazy-ideas
- Maximum-implementation-potential: "Every funnel and every derivation got the cure in one pass — no follow-up lane needed for F1–F5." — Maximum-implementation-potential
- Brutal critic: "sanitized() only helps if funnels call it; I grepped the writers — updateRoutine/startSession/saveCustomProfile are the funnels, reseed writes known-good constants direct. Acceptable, named in the report." — Brutal critic
- Improver: "tierIsCorrupt/statusIsCorrupt turn silent laundering into a surfaceable signal — the next lane can print them." — Improver
- Designer: LEAD-COVERED — "No visual surface changes; DEFERRED corrupt rows land in Saved-for-Later where they're seen, not hidden." — Lead for Designer
- Consumer: "A corrupt row no longer locks or unlocks my apps silently wrong — LOCKED on doubt is the behaviour I'd choose." — Consumer
- Student: LEAD-COVERED — "A garbled task no longer jumps back into the live queue mid-session; it waits in Saved-for-Later." — Lead for Student
- User: "My stored data can't brick the app on upgrade — that was the whole trap, and it's pinned by a test." — User
- User-experience: LEAD-COVERED — "Estimates I never gave stop appearing on my tasks (default 0); nothing I did give changes." — Lead for User-experience
- Purpose-questioning: "Asked why isBlocked exists at all: it's a DAO sort key and a stored cache. Declared derived, kept, tied — honest answer, no schema crime." — Purpose-questioning
- Thoughtful: "endHour=24 sentinel respected — clamping blindly to 23 would have 'fixed' a lawful value into a bug." — Thoughtful
- Deep-thinker: "The invariant lives at boundaries because the constructor is a load path, not a birth path. That's the whole design in one line." — Deep-thinker
- Enthusiastic designer: LEAD-COVERED — "Span target and elapsed now share one constant — one knob, no silent divergence. Clean." — Lead for Enthusiastic designer
- Wow-level designer: LEAD-COVERED — "No wow surface here; correctness is the wow in a models file." — Lead for Wow-level designer
- Designer-29-years: "Thirty years of data classes say: never validate in the constructor of a persisted entity. The brief's trap was the test; we passed it." — Designer-29-years
- Intentionality designer: "Every default now means something stated: 0 = no estimate, DEFERRED = corrupt-and-parked, LOCKED = corrupt-and-closed." — Intentionality designer
- Synchronous collaboration: "Wave-2 branches checked before writing a line — no duplicated cures, no rival fixes." — Synchronous collaboration
- Implementation-quality: "13 focused tests, each naming the finding it pins; garbage-construction test guards the anti-brick property forever." — Implementation-quality
- Technical execution: "Diff is surgical: Models + two funnel files + one test file. No schema, no migration, no entity field changes." — Technical execution
- Interaction-detail: LEAD-COVERED — "Peek tier keeps isBlocked=true under normalized() — the 2-minute knock still counts as held everywhere it did." — Lead for Interaction-detail
