# ROUTINES REBUILD — Sten's parts (MaxImpl, routines seat)

*For the merged routines-rebuild brief. Two sections, both build-ready:
§1 the `derive()` function spec, §2 alarm-hint wiring + the reseed.
Design source: Derive-Don't-Fire (`invention/maximpl-parts/05-sten.md`),
which supersedes the firing machinery of `plan/maximpl-parts/05-sten-routines.md`
wherever they disagree; that plan's row/editor/Section/theme sections stand
untouched. Verified against the shipped tree, Oct 29 (file:line citations are
to the shipped code).*

**Migration context (binding arbitration, not re-argued here):**
7→8 is Voss's token build alone (`emergency_break_spends`, fallback deleted).
This build is **8→9**: my 13 ADD COLUMNs below + `startedAtSessionElapsedSec`
(`focus_tasks`, Nadia's ride) + `taskId` (`focus_sessions`, Rurik's ride) —
those two columns are co-tenants of Migration(8,9), specced by their owners,
listed here only so the DDL lands in one migration object. 9→10 is Rurik's
step tables + Otis's readiness log. `autoPausedAtMillis` is formally NOT
created (auto-pause struck; a 3-miss streak prints as a fact, his hand pauses).

```sql
-- routines (2)
ALTER TABLE routines ADD COLUMN pausedUntilMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE routines ADD COLUMN scheduleEditedAtMillis INTEGER NOT NULL DEFAULT 0;
-- focus_sessions (7)
ALTER TABLE focus_sessions ADD COLUMN routineId INTEGER;
ALTER TABLE focus_sessions ADD COLUMN windowStartMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN windowEndMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN routineOutcome TEXT;
ALTER TABLE focus_sessions ADD COLUMN evidenceMask INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN firstEvidenceMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN lastEvidenceMillis INTEGER NOT NULL DEFAULT 0;
-- guard_settings (4)
ALTER TABLE guard_settings ADD COLUMN routineReseedV1Done INTEGER NOT NULL DEFAULT 0;
ALTER TABLE guard_settings ADD COLUMN routineReseedNoteDismissed INTEGER NOT NULL DEFAULT 0;
ALTER TABLE guard_settings ADD COLUMN lastBootAtMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE guard_settings ADD COLUMN testDateEpochDay INTEGER NOT NULL DEFAULT -1;
-- NO new table. routine_attendance is never created.
```
Registered cumulatively: `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)`,
no fallback line (the token build already deleted it at
`MonolithDatabase.kt:49`), no wholesale `getInstance` rewrite. Pre-existing
session rows get `routineId = NULL` = manual — true, and never backfilled.

---

## 1. `derive()` — routine window state as a pure function

### 1.1 Home, signature, purity contract

One new file, `data/RoutineDerive.kt`. One object. No other file in the app
may compute "is this window open / what should exist" — surfaces, the
reconciler, the scheduler, and the theme rule all call this or the
`RoutineSchedule` functions beneath it.

```kotlin
object RoutineDerive {

    fun derive(
        routines: List<RoutineEntity>,          // schedule rows, as stored
        nowMillis: Long,                        // wall clock, supplied by caller
        activeSession: FocusSessionEntity?,     // the one active row, or null
        sessions: List<FocusSessionEntity>,     // recent session rows (see 1.2)
        settings: GuardSettingsEntity           // testDateEpochDay, lastBootAtMillis
    ): DeriveResult

    // Single-routine overload for hot paths (shield tick, §1.7 site 2):
    // identical semantics, one routine, same inputs minus the list.
    fun deriveOne(
        routine: RoutineEntity, nowMillis: Long,
        activeSession: FocusSessionEntity?,
        sessions: List<FocusSessionEntity>,
        settings: GuardSettingsEntity
    ): RoutineDerivation
}
```

**Purity, absolute:** no `System.currentTimeMillis()`, no
`SystemClock`, no DAO, no Context, no randomness inside `derive`.
Every input arrives as a parameter; the same inputs return an equal
result, always. Callers read the clock and the DB, then call. This is
what makes the function unit-testable with a fake clock and what makes
a stale or hallucinated caller harmless (§2): a wrong *trigger* can
only ever produce a correct *evaluation of now*.

### 1.2 Inputs, exact

- `routines` — all rows from `dao.getAllRoutinesSnapshot()` (already
  `ORDER BY startMinutes ASC`; order is not load-bearing).
- `nowMillis` — wall clock at the call site. Windows are civil-time
  facts (05:00 means 05:00 on the clock on the wall), so wall clock is
  the correct input; monotonic time never enters window math. Session
  *elapsed* stays the repository's verified-elapsed business
  (`computeVerifiedSessionElapsed`) — `derive` judges windows, not timers.
- `activeSession` — `dao.getActiveSessionSnapshot()`.
- `sessions` — the rows the natural-key and judgement checks need:
  every session with `routineId != null` whose `windowEndMillis` is
  within the last 48h, plus every unfinalised routine session
  (`routineId != null && routineOutcome == null`) regardless of age.
  One DAO query supplies both:
  `getRoutineSessionsForDerive(sinceMillis: Long)` =
  `SELECT * FROM focus_sessions WHERE routineId IS NOT NULL AND (windowEndMillis >= :sinceMillis OR (isActive = 0 AND routineOutcome IS NULL))`.
  Manual rows (`routineId == null`) enter only via `activeSession`
  (the HELD check) — the query never loads history for its own sake.
- `settings` — the single `guard_settings` row; only
  `testDateEpochDay` and `lastBootAtMillis` are read.

### 1.3 Outputs, exact

```kotlin
data class DeriveResult(
    val derivations: List<RoutineDerivation>,  // one per input routine, input order
    val nextBoundaryMillis: Long?              // §2: the chain alarm's instant
)

data class RoutineDerivation(
    val routineId: Int,
    val state: WindowState,
    val window: WindowInstance?,        // the window containing now, if any
    val desiredSession: DesiredSession?,// non-null ONLY in state OPEN_IDLE (§1.5)
    val finaliseDue: List<Int>,         // session ids the reconciler must finalise now
    val judgement: WindowJudgement?     // for the latest CLOSED window, if provable (§1.6)
)

data class WindowInstance(
    val startMillis: Long,              // SCHEDULED start (frozen truth, not delivery time)
    val endMillis: Long,                // SCHEDULED end = start + duration
    val remainderMinutes: Int,          // (endMillis - now) / 60_000, floor, >= 0
    val driftMinutes: Int               // (now - start) / 60_000 when now > start, else 0
)

data class DesiredSession(
    val routineId: Int,
    val windowStartMillis: Long,
    val windowEndMillis: Long,
    val durationMinutes: Int,           // = remainderMinutes, NEVER the full window
    val loadoutName: String,            // routine.loadoutName, applied at materialise
    val intention: String,              // = routine.name
    val profileName: String             // = "Routine: <name>" — legacy rendering only;
                                        // identity is routineId, never this string
)

enum class WindowState {
    DISABLED,        // enabled == false
    PAUSED,          // pausedUntilMillis > window start of the candidate window
    SUPPRESSED,      // test-date predicate (§1.4) kills the candidate window
    OUTSIDE,         // enabled, no window contains now, none due to judge
    OPEN_IDLE,       // window contains now, no session has ever claimed it
    OPEN_RUNNING,    // window contains now, active session carries this routineId
    OPEN_HELD,       // window contains now, a different/no-routine session occupies it
    CLOSED_DUE       // no window contains now, but work is owed: an unfinalised
                     // routine session exists, or the latest closed window is unjudged
}
```

State precedence, evaluated in this exact order per routine (first match wins):
DISABLED → (window containing now exists? → SUPPRESSED if its start is
suppressed → PAUSED if its start < `pausedUntilMillis` → OPEN_RUNNING /
OPEN_HELD / OPEN_IDLE by session occupancy) → (no containing window →
CLOSED_DUE if `finaliseDue` non-empty or an unjudged provable closed
window exists → else PAUSED if the *next* window's start <
`pausedUntilMillis` → else SUPPRESSED if the next window's start is
suppressed (so the row can print the test-day line ahead of the date) →
else OUTSIDE).

Judgement enum (the query form of the plan's §5.4 outcomes; stamps on
session rows use the same names as strings in `routineOutcome`):

```kotlin
enum class WindowJudgement { ATTENDED, PARTIAL, MISSED, HELD, LATE_FIRE, NOT_DELIVERED, UNKNOWN }
```
UNKNOWN is a first-class result, not an error: it prints nothing,
books nothing, and breaks a streak walk. `derive` returns it whenever
the record cannot prove a judgement (§1.6, rule J4).

### 1.4 The Saturday (test-date) suppression predicate

Suppression is a **date** exception, not a weekday rule. A predicate
that tested `dayOfWeek == SATURDAY` would suppress every Saturday
forever, including Saturdays with no test — that predicate is wrong
and is not what ships. The shipped predicate, verbatim semantics:

```kotlin
fun isSuppressed(routine: RoutineEntity, windowStartMillis: Long, testEpochDay: Long): Boolean =
    testEpochDay >= 0 &&
    routine.loadoutName != SmartLoadout.SLEEP.name &&
    epochDayOf(windowStartMillis) == testEpochDay
```
where `epochDayOf` converts via `java.time` in the device's current
zone, and `testEpochDay` is `guard_settings.testDateEpochDay`
(−1 = none; set once, rested, from the test-date field; a past value
is inert — it suppresses nothing and is never cleaned up).

Rules:
- **Start-day keys it.** A window *starting* on the test date is
  suppressed. A window starting the day before and spanning into the
  test date is not. (In his seeds the only such window is SLEEP —
  exempt anyway.)
- **SLEEP is never suppressed** (loadout decides, never the name):
  the night after a test still locks down on schedule.
- The predicate is consulted in exactly four places, all inside the
  schedule layer: `nextStartMillis`, `nextEndMillis`'s containing-
  window search, `isActiveNow`, and `derive`'s closed-window
  enumerator. Every consumer therefore inherits it: the chain never
  arms a suppressed start (§2), `derive` never returns OPEN_IDLE for
  it, the ribbon never announces it, and the enumerator never creates
  a judgement for it — **no window exists, so no miss can be booked.
  That is HPA-09 by construction, not by bookkeeping.**
- Scope boundary, stated so nobody discovers it by building: one
  stored date only. Two outstanding test dates on *different* days
  exceed one Long; the bridge is plan §3.5 pause-until on the affected
  routines for the second date. Not built, not needed by his calendar.

### 1.5 The materialisation rule (what `desiredSession` means)

`desiredSession != null` ⟺ state == OPEN_IDLE **and** all of:
- no session row in `sessions` (active or not, finalised or not) has
  `(routineId, windowStartMillis) == (routine.id, window.startMillis)`
  — the natural key. One window, at most one session, ever. A held
  window fails this check forever via the occupying session's overlap
  rule in §1.6/J3, and a window whose session already ended fails it
  via its own row: **no belated takeover, no second mint, ever**;
- `window.remainderMinutes >= 5`. Below 5, sealing him because Doze
  napped is the machine punishing him for its own lateness:
  `desiredSession = null`, and the window judges LATE_FIRE at close
  (§1.6/J5).

The reconciler (§1.7) is the only applier: it calls
`repository.materialiseRoutineSession(desiredSession)`, which applies
the loadout and inserts the session **with the snapshot columns
written at insert** (`routineId`, `windowStartMillis`,
`windowEndMillis`) — the stored window is that session's law from
then on (rule R5 below). `startSession` gains those three as optional
parameters with manual defaults (`routineId = null`, windows = 0);
every existing manual caller is unchanged.

**R5 — the stored window outranks the live row.** Once a session
exists, its stand-down and judgement read its *stored*
`windowEndMillis`, never the routine row's current fields. Rename,
retime, pause, delete, and the §2 reseed may all rewrite the row
mid-window; the live session does not notice. This one rule is why
the rename fence, the reseed postponement, and pending-delete from
the old plan are all dead (§1.8).

### 1.6 Judgement rules for closed windows (J1–J6)

Applied by the reconciler/finaliser, computed by `derive` (for the
latest closed window) and by the attendance query at row-render time
(walk of at most 4 windows — line 4 needs latest + a streak that only
distinguishes 0/1/2/≥3). Thresholds are the plan §5.4 table, unchanged:
A = attendedMinutes (evidence-derived, §1.6.1), W = scheduled window
minutes, R = actual remainder when fired late.

- **J1 — a routine session row exists for the window:** if
  `routineOutcome != null`, that stamp IS the judgement (stamps are
  immutable; `derive` never re-judges a stamped row). If unstamped,
  finalise it (§1.6.2): ATTENDED if A ≥ W−15 · PARTIAL if 15 ≤ A <
  W−15 · MISSED if A < 15.
- **J2 — SLEEP exemption:** a routine whose loadout is SLEEP never
  receives a judgement, a stamp, evidence, or a streak — `derive`
  returns `judgement = null` for every SLEEP window, closed or not.
  Its sessions still materialise and stand down by stored window end.
- **J3 — no routine session, a manual/other-routine session overlapped
  the window:** HELD. Attended = the overlap minutes, printed only,
  never summed anywhere. Resets a miss streak.
- **J4 — no session at all:** NOT_DELIVERED only if *provable* — the
  window contains `settings.lastBootAtMillis` AND it is the most
  recent closed window. Else MISSED if `window.startMillis >
  routine.scheduleEditedAtMillis` (the window provably postdates the
  last edit, so the current row proves it was scheduled). Else
  UNKNOWN: before the edit horizon the row cannot prove the window
  was ever scheduled (it may have been paused, disabled, or timed
  differently), and the record does not invent misses.
- **J5 — LATE_FIRE:** if the session was materialised with
  `driftMinutes > 2`, or no session exists but the window's first
  proof of life is a wake with remainder < the full window, outcomes
  judge A against **R**, not W — except MISSED is impossible: A < 15
  books PARTIAL-at-A (a 0 books a zeroed session row, entry stands).
  Streak-neutral: neither resets nor increments.
- **J6 — suppressed / paused / disabled windows do not exist for
  judgement.** The enumerator skips any window whose start is
  suppressed (§1.4), whose start < `pausedUntilMillis`, or which the
  edit horizon (J4) makes unprovable. Absence in the record means
  "not scheduled," never "missed." A day he paused in advance cannot
  be a miss; a test date cannot break a streak.

#### 1.6.1 Attended minutes — computed once, at finalise, from timestamps

No sampler. Evidence instants = { session start } ∪ { intercept-log
timestamps belonging to this session } ∪ { task completions with
`completedInSessionId == session.id` } ∪ { `firstEvidenceMillis`,
`lastEvidenceMillis` stamps (E2 foreground return, E4 hold start —
§2.4) }. `attendedSeconds` = measure of ⋃[t, t+5min] over those
instants, intersected with [windowStart, windowEnd], capped at W.
Evidence kinds and never-counts are plan §5.3's E1–E4 / N1–N6,
unchanged. Missing instants can only shrink coverage — *book less*
is what the arithmetic does, not a policy a sampler must remember.

#### 1.6.2 Finalise — the one funnel

`finaliseRoutineSession(session, nowMillis)` (repository, one
transaction): computes A (§1.6.1), judges (J1/J5), then books the
session row — ATTENDED: verified elapsed stands,
`completedSuccessfully = true` · PARTIAL: `elapsedSeconds` rewritten
to `attendedSeconds`, `completedSuccessfully = false`, no artifact ·
MISSED: `elapsedSeconds = 0`, `completedSuccessfully = false`,
`forgedArtifactId = null` — writes `routineOutcome`, sets
`isActive = false`. No intercept-log line is written for any of these
(a miss is not an interception).

Two fences make it the *only* exit for a routine session:
- `disarmActiveSession` gains a head guard: if
  `active.routineId != null`, the generic path refuses and the call
  routes to `finaliseRoutineSession`. (TOKEN-SPEC §4.1's step-4
  dispatch is this funnel arriving from the token side: at valve
  completion inside a routine session, the token transaction calls
  this finaliser with the accrued A — PARTIAL arm ↔
  `disarmRoutineSessionPartial(attendedSeconds)`, MISSED arm ↔ the
  zeroing arm, ATTENDED arm ↔ stands-as-booked, in token-spec's
  vocabulary; SLEEP sessions skip finalise per J2 and disarm by the
  generic path, exactly as §4.1 rules. My build owns the inside of
  that dispatch; the token's transaction shape, spend write, and
  `BreakResult` are untouched.)
- `updateSessionProgress`'s completion branch is fenced for
  `routineId != null`: at target it sets `isActive = false` WITHOUT
  `completedSuccessfully = true` and WITHOUT forging a Curio artifact.
  The row then sits in the distinguishable unfinalised state
  (`routineId != null && routineOutcome == null`), which
  `compute7DayHistory` excludes outright, and the next reconcile or
  read finalises it (§1.7). A routine session can therefore never
  mint minutes by merely reaching its target unattended.

### 1.7 Idempotence + the four failure clocks

**Idempotence.** `derive` is pure — trivially idempotent. Applying it
(the reconciler) is idempotent by construction:
- materialise is an upsert against the natural key
  `(routineId, windowStartMillis)` (§1.5) — a second pass inside the
  same window finds the row and returns OPEN_RUNNING, minting nothing;
- finalise writes only rows with `routineOutcome == null`; a stamped
  row is never re-judged (J1);
- rearm (§2) cancels before it sets, so N passes leave exactly one
  chain alarm;
- the reseed (§2.5) re-matches by exact fields — already-transformed
  rows no longer match the old seeds.
Two reconciles in the same millisecond, a duplicate hint, a hint plus
a resume in the same second: all converge to the same rows.

**Late fire.** A wake landing inside a window derives OPEN_IDLE with
`remainderMinutes = (end − now)/60` and materialises the remainder
only — never the full window (the shipped receiver's
`durationMinutes(routine)` full-window start at
`RoutineAlarmReceiver.kt:41` is the bug this replaces). Remainder < 5:
no seal, LATE_FIRE judgement at close (J5). Drift ≤ 2min is beneath
notice and prints nothing.

**Missed fire (no wake at all inside the window).** Nothing is minted
retroactively — a window whose end has passed can never gain a
session. The next wake of any kind derives CLOSED_DUE; the window
judges per J3/J4 (HELD if occupied, NOT_DELIVERED if a boot inside it
is provable, MISSED if provably scheduled, UNKNOWN otherwise).

**Reboot.** Alarms die at reboot; sessions and rows do not. The boot
receiver (§2.3) stamps `lastBootAtMillis`, restarts the shield if an
active session exists (shipped behaviour, kept), then runs reconcile
+ rearm. Inside an open window with no session, reconcile
materialises the remainder exactly as a late fire — the session's
stored window still ends on schedule. A window the phone slept
through entirely judges NOT_DELIVERED (J4) if it is the latest closed
window. Session elapsed across the reboot stays
`computeVerifiedSessionElapsed`'s existing boot-count machinery —
untouched by this build.

**Clock change.** Window math is wall-clock by design (§1.2), so:
- Clock forward past a window: it becomes a closed window at the next
  wake and judges per J3/J4. It is never materialised after its end.
- Clock forward *inside* a live session: the session's own verified-
  elapsed/tamper machinery already owns that fight (drift > 90s =
  tamper); `derive` does not second-guess it. The session stands down
  by its stored `windowEndMillis` at the next evaluation.
- Clock backward into a window that already has a session row
  (finalised or not): the natural key (§1.5) returns OPEN_RUNNING or
  the stamped judgement — no second session, no re-judgement.
- Clock backward before a window: OUTSIDE; the chain rearms to the
  (now earlier) true boundary.
- Timezone change: `RoutineSchedule`'s Calendar math runs in the
  device's current zone, so future windows move with the zone —
  correct for civil-time schedules. A live session is immune (R5,
  stored window). This mirrors TOKEN-SPEC §3's anchor ruling: one
  definition, instants compared, no zone-of-origin bookkeeping.

### 1.8 Exact call sites that consume `derive()`

`derive` (pure evaluation) may be called anywhere. `reconcile`
(apply: materialise + finalise + rearm) has exactly these entry
points — no others may apply:

| # | Site | What runs | Cadence / trigger |
|---|---|---|---|
| 1 | `RoutineAlarmReceiver.onReceive` (hint shell, §2.2) | full reconcile | chain alarm delivery |
| 2 | `MonolithShieldService` monitor loop | `deriveOne` for the active session's routine ONLY: if its stored `windowEndMillis <= now` → `finaliseRoutineSession`; the service never materialises (it exists only while a session runs, and it stops itself when none does — shipped `MonolithShieldService.kt:80-85`) | every existing 2s tick; no new loop, no new cadence |
| 3 | App open: `MonolithViewModel` init after `ensureSeededAndSyncPackages()` returns (today `MonolithViewModel.kt:294-295`), and `MainActivity` ON_RESUME (`MainActivity.kt:191`, the existing lifecycle hook — it also stamps E2 evidence) | full reconcile, via `viewModel.requestRoutineReconcile()` | once per init / per resume |
| 4 | `MonolithBootReceiver` (LOCKED_BOOT_COMPLETED / BOOT_COMPLETED / MY_PACKAGE_REPLACED) | stamp `lastBootAtMillis`, restart shield if active (kept), full reconcile | per boot event |
| 5 | Every routine CRUD write, through the existing single `persistRoutine` funnel + create/duplicate/delete/undo | full reconcile (which rearms) | per write |
| 6 | Any session start / disarm (manual included), at the end of `startSession` / `disarmActiveSession` | full reconcile | per session transition |

Read-only consumers (call `derive`, never apply): Shield row line 3/4
+ Section spine (on the plan's hoisted 30s section ticker), the theme
rule (plan §7 — it already evaluates `isActiveNow`; it now reads the
same derivation), the Deck NEXT ribbon (Ines's contract, §2.6), and
the attendance LAST/streak walk. Rendering never materialises: a
screen that could mint a session by being looked at would be a firing
in disguise.

### 1.9 Existing functions — die / shrink / stay

**DIE:**
- `RoutineAlarmReceiver`'s BOUNDARY_START / BOUNDARY_END branches
  (shipped `RoutineAlarmReceiver.kt:35-60`) — the full-window start,
  the `applySmartLoadout` + `startSession` direct call, and the
  name-matched END (`active.profileName == "Routine: ${routine.name}"`,
  Lie #13). The receiver file shrinks 74 → ~28 lines (§2.2).
- `RoutineScheduler.scheduleRoutine`, `cancelRoutine`,
  `cancelBoundary`, and the `routineId * 2 (+1)` request-code
  arithmetic — replaced by `rearmChain` / `cancelChain` (§2.1).
  `EXTRA_BOUNDARY` dies as a meaningful value.
- `RoutineSchedule.nextEndMillis` as a caller-facing function — its
  "end of containing *or next* window" ambiguity is unusable for
  stand-down. Its overnight-containing-window logic folds into a new
  private `windowContaining(routine, now): WindowInstance?` inside
  `RoutineSchedule`, which `derive` uses. No caller outside the
  schedule layer may ask for a window end again.
- The old plan's machinery (never shipped, now formally dead — do not
  build): the `routine_attendance` table + sampler + sweep, the
  rename fence, pending-delete (`deleteAfterWindowEnd` is NOT
  created), reseed postponement, `MISSED_AUTOPAUSED`.

**SHRINK / CHANGE BEHAVIOUR:**
- `MonolithRepository.disarmActiveSession` — gains the routine head
  guard (§1.6.2); manual behaviour byte-identical.
- `MonolithRepository.updateSessionProgress` — completion branch
  fenced for routine sessions (no completed flag, no artifact).
- `MonolithRepository.compute7DayHistory` — one filter: unfinalised
  routine rows excluded. Nothing else changes; history becomes
  truthful because booking is fixed at the source.
- `RoutineSchedule.durationMinutes` — coercion `15..720` →
  `30..1080`, unifying with `adjustRoutineTime`'s clamp (also
  `30..1080`): printed duration ≡ raw span for every storable
  routine, and DAY BLOCKS' 630 minutes sits mid-range.
- `RoutineSchedule.nextStartMillis` / `isActiveNow` — gain the
  §1.4 suppression check and (for nextStart) the pause-aware origin
  `max(now, pausedUntilMillis)` via one helper
  `effectiveNextStartMillis(routine, now)` in `RoutineSchedule`, so
  the scheduler and the ribbon can never disagree.
- `MonolithBootReceiver` — `scheduleAll` call replaced by
  boot-stamp + reconcile (§1.8 site 4). Its direct-boot posture and
  shield restart stay.
- `MonolithViewModel` init sequence (`:294-295`) —
  `RoutineScheduler.scheduleAll(...)` call replaced by reconcile
  (which rearms the chain itself).

**STAY, untouched:**
- `RoutineSchedule` bit math (`bitFor`, `atMinutes`), `nextStartMillis`'
  strictly-after contract, `durationMinutes`' wrap arithmetic.
- `MonolithRepository.startSession` signature for manual callers
  (optional snapshot params added, defaults = manual), `applySmartLoadout`,
  `computeVerifiedSessionElapsed`, peek/intercept/notification machinery.
- `MonolithShieldService.startShield` / `stopShield`, its 2s loop
  shape, its notification and intercept work.
- The token build's `completeEmergencyBreak` transaction and
  `emergency_break_spends` — this build only supplies the inside of
  its §4.1 routine dispatch (§1.6.2).

---

## 2. Alarm-hint wiring — alarms demoted to hints

### 2.1 What still gets scheduled: exactly one chain alarm

There is **one** routine alarm in the whole app after this build.

```kotlin
object RoutineScheduler {
    const val CHAIN_REQUEST_CODE = 0x5201
    fun rearmChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long)
    fun cancelChain(context: Context)
}
```

`rearmChain` computes `DeriveResult.nextBoundaryMillis` — the minimum
over all routines that are enabled, `daysMask != 0`, unsuppressed,
and pause-aware, of { next start strictly after now, end of the
window containing now } — then:

```kotlin
alarmManager.cancel(chainPendingIntent(context))            // always first
nextBoundaryMillis?.let {
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, it, chainPendingIntent(context))
}
```

- The PendingIntent: `PendingIntent.getBroadcast(context,
  CHAIN_REQUEST_CODE, Intent(context, RoutineAlarmReceiver::class.java),
  FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)`. **No extras that mean
  anything.** (An optional `hint_source="chain"` string extra is
  permitted for log readability; no code may branch on it.)
- `setAndAllowWhileIdle` is retained deliberately. Exact alarms
  (`setExactAndAllowWhileIdle`) were declined: SCHEDULE_EXACT_ALARM
  costs a user grant + policy friction, and under Derive-Don't-Fire
  lateness is already priced honestly (remainder materialisation,
  LATE_FIRE, drift honesty — §1.7). The chain buys *timeliness of
  evaluation*, never correctness.
- No boundary exists (no enabled routines) → cancel, schedule
  nothing. Armed-ness is one probeable boolean:
  `PendingIntent.getBroadcast(..., FLAG_NO_CREATE) != null`.
- **Legacy cleanup, once:** the first seed-sync pass after the 8→9
  upgrade cancels both legacy PendingIntents for every known routine
  id (request codes `id*2`, `id*2+1`) before the first `rearmChain`.
  ~6 lines, runs inside the §2.5 reseed pass's lock. Without it, a
  stale per-routine alarm would keep waking the receiver — harmless
  (a hint is a hint) but noisy, and the cleanup is cheap.

### 2.2 What a hint may and may not do

The receiver becomes a shell (~28 lines): `goAsync()` → stamp nothing
→ `RoutineReconciler.reconcile(appContext, System.currentTimeMillis())`
→ `pendingResult.finish()`. It reads no extras, loads no routine,
branches on nothing.

**A hint MAY:**
- trigger one `derive()` evaluation of now (via reconcile);
- and, *through reconcile's derivation only*, cause a remainder
  session to materialise, a due session to finalise, attendance to be
  judged, and the chain to rearm (§2.3).

**A hint MAY NEVER:**
- start, stop, book, or disarm a session *directly* — it carries no
  authority to name a routine, a boundary, or an action;
- be believed: its delivery time is not the window start, its
  existence is not presence, its absence is not a miss. A hint
  delivered 3 hours late derives OUTSIDE and does nothing. A
  hallucinated/spurious broadcast costs one pass over ≤ a dozen rows;
- apply a loadout, write an evidence stamp, spend or read the
  emergency token, or write any row other than through reconcile's
  materialise/finalise funnels;
- count as attendance evidence (plan §5.3 N1: the machine's acts are
  not his attendance — the alarm firing is the machine's act).

This is symmetric for every other reconcile trigger (§1.8 sites 3–6):
boot, resume, CRUD, and session transitions are all just hints with
better provenance. No trigger asserts anything except "now exists —
re-derive."

### 2.3 Rearm points (exhaustive)

The chain is rearmed at the end of **every** reconcile pass, and
reconcile runs at §1.8's six sites. Therefore: chain delivery ✓ ·
boot / package-replaced ✓ · app init ✓ · every resume ✓ · every
routine CRUD/toggle/pause/resume write ✓ · every session start/disarm
✓ · reseed (§2.5) ✓ · shield-service start (it launches only with a
session, whose start already reconciled) — covered transitively.
There is no per-action registration list to keep in sync any more;
the old plan §3.6's exhaustive re-registration table collapses to
this paragraph. A lost chain is repaired by the next unrelated wake —
he opens the app daily, the phone boots, sessions start — and the
failure is self-revealing at that wake because reconcile re-derives
the present, not just the future.

### 2.4 The E4 interaction (TOKEN-SPEC §4.3 B4) — evidence ≠ token input

Two systems touch at one point and must not leak into each other:

- **The stamp.** When the 60-second hold *starts* during a routine
  session, `recordAttendanceEvidence(sessionId, E4)` writes bit 3 of
  the session row's `evidenceMask` and updates
  `firstEvidenceMillis` / `lastEvidenceMillis`. This is **presence
  evidence**: a man who reached for his one weekly break was *in* the
  window. It feeds §1.6.1's attended-minutes computation at finalise.
  Bank crossings (15/30/45), slips, drains, and abandons stamp
  nothing further; a later hold start in the same session updates
  `lastEvidenceMillis` only.
- **The token silence.** TOKEN-SPEC §4.3's write-nothing rule governs
  `emergency_break_spends` and token accounting *only*. The E4 stamp
  is not an attempt record and lives in a different table under a
  different spec (plan §5.3); no builder may suppress it under §4.3.
- **Never an input the other way:** `emergencyBreaksRemaining()`
  never reads `evidenceMask`, attendance, or any routine state; the
  attendance derivation never reads `emergency_break_spends`. If the
  hold *completes*, the token transaction (§4.1) writes the spend and
  calls §1.6.2's finaliser inside the same transaction — the accrued
  attendedSeconds (which E4 helped evidence) decide
  ATTENDED/PARTIAL/MISSED. Evidence prices the *booking*; the token
  prices the *break*. Each is the other's neighbour, neither is the
  other's input.

### 2.5 The reseed — data transform in seed-sync, NOT a migration

Per TOKEN-SPEC §6.4 (my A1, adopted): **the reseed is not a Room
migration callback.** Migration(8,9) is schema only (the 13 columns
above). The data transform runs inside the existing
`MonolithRepository.ensureSeededAndSyncPackages()`, under the existing
`seedMutex` (`MonolithRepository.kt:65`), guarded by
`guard_settings.routineReseedV1Done`, and is version-number-blind:
it executes identically whichever number the routines schema
migration carries.

**Placement inside the function (exact):** after the
`guard_settings` row is ensured (shipped order already creates it
before the routines block) and in the same `withLock` pass as the
routines seed-on-empty check — extended, not replaced. It completes
before `MonolithViewModel` init's reconcile (§1.8 site 3) reads the
snapshot, so the first post-update evaluation already sees his real
day. **No postponement:** unlike the old plan §4, a live
"Routine: Morning Deep Work" session does not delay the transform —
R5 (§1.5) means the live session obeys its stored window and the
transformed row governs the next window. The postponement state
machine is dead; do not resurrect it.

**Targets (his real day, exact):**

| Name | Window | startMin | endMin | Days | Loadout |
|---|---|---|---|---|---|
| FASTED BLOCK | 05:00–08:00 | 300 | 480 | DAILY (127) | STUDY |
| DAY BLOCKS | 09:00–19:30 | 540 | 1170 | DAILY (127) | STUDY |
| SLEEP | 20:00–03:30 (+1) | 1200 | 210 | DAILY (127) | SLEEP |

DAY BLOCKS is 630 minutes — legal only because §1.9 unifies the span
clamp at 30..1080. Build order consequence: the clamp fix lands with
or before this transform, never after.

**Match rule — EXACT on all four fields, or the row is not a seed**
(`enabled` is deliberately not matched; it is *carried over* — toggling
is use, not editing, and a switched-off seed migrates still-off):

| Old seed | name | start | end | daysMask | loadoutName |
|---|---|---|---|---|---|
| S1 | `Morning Deep Work` | 540 | 780 | 31 (MON–FRI) | `STUDY` |
| S2 | `Night Wind-Down` | 1350 | 420 | 127 (DAILY) | `SLEEP` |

(Shipped seed values, `MonolithRepository.kt:85-101`, verified.)

**Transform steps, in order, all inside the lock:**
1. S1 row (if an exact match exists) → update in place, **id
   preserved**: name `DAY BLOCKS`, 540–1170, mask 127, `STUDY`,
   `enabled` carried over, `scheduleEditedAtMillis = now`.
2. S2 row → update in place, id preserved: name `SLEEP`, 1200–210,
   mask 127, `SLEEP`, `enabled` carried over,
   `scheduleEditedAtMillis = now`.
3. FASTED BLOCK has no ancestor — fall through to step 4.
4. For each of the three targets: if no row now matches its exact
   (name + start + end + daysMask + loadoutName), insert it fresh
   (enabled = true) — **unless** a row already carries that exact
   *name* (he pre-created it by hand; name collision = hands off).
   An *edited* old seed (any of the four match fields differs) is
   untouched by steps 1–2, and its successor still inserts here.
   Overlap between his edited row and an inserted successor is legal
   and drawn honestly by the Section; the transform never edits a
   row he edited. Full stop.
5. Legacy-alarm cleanup (§2.1), then `cancelChain` + `rearmChain`
   against the post-transform snapshot.
6. Set `routineReseedV1Done = true` — only at the end, only if steps
   1–5 completed. A crash mid-pass is safe by construction: re-entry
   re-matches (transformed rows no longer match S1/S2), inserts are
   guarded by the exact-match/name checks, and the flag is still
   false. The pass is idempotent; there is no PARTIAL state to
   persist.

**Fresh installs:** the seed-on-empty block now writes the three
target routines directly (replacing the two old seed literals at
`MonolithRepository.kt:85-101`), and the same pass sets
`routineReseedV1Done = true` — a fresh install never "migrates" and
never sees the reset note. **First-run trace (old plan §4, kept):**
no modal, no Deck interruption, no ceremony; one strip under the
ROUTINES header — `SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO
CHANGE IT` — shown until any routine editor opens (sets
`routineReseedNoteDismissed`) or 14 days pass. Told once, in human
language, then gone.

### 2.6 Downstream contracts this section rewrites

- **Ines (firing-data-contract):** item 1's per-routine "alarm was
  armed" truth rule is replaced by the §2.1 chain probe — there is no
  per-routine alarm to interrogate, and the ribbon's lawful claim is
  the scheduled time as plan, printed only while the chain probe says
  armed. Item 2: mise en place keeps its `(routineId, startMillis)`
  key, but the duration it lays out is the **remainder at
  derive-equivalent evaluation**, and on a HELD morning it must expect
  no session. Item 3 (demotion) stands.
- **Token build (Voss):** §1.6.2 supplies the inside of TOKEN-SPEC
  §4.1 step 4. Nothing in this build calls a break-class disarm
  outside `completeEmergencyBreak`, and nothing here reads or writes
  `emergency_break_spends`.

### 2.7 Price + acceptance (this build's routines half)

| Item | Δ lines (approx.) |
|---|---|
| Migration(8,9) — my 13 columns' DDL + entities | +30 |
| `RoutineDerive.kt` (§1) | +~150 |
| `RoutineWindow` helpers folded into `RoutineSchedule` (windowContaining, suppression, effectiveNextStart) + clamp unify | +~45 / −15 |
| `RoutineReconciler` + repository materialise/finalise/evidence (§§1.5–1.6) | +~170 |
| Repository fences (disarm guard, progress fence, history filter) | +~25 / −5 |
| Receiver → hint shell | −46 net |
| Scheduler → one chain + legacy cleanup | −25 net |
| Triggers (boot stamp, VM init/resume, CRUD funnel) | +~20 |
| Reseed transform + fresh-install seeds (§2.5) | +~60 |
| Tests (below) | +~180 |
| **Total** | **≈ +680 / −90, ~2 focused build days incl. tests** |

Acceptance, beyond TOKEN-SPEC §8's chain tests (A2) and routine-break
test (B3), which stand: **stale hint** (deliver a hint 3h after window
end → no session, no judgement change, chain armed at the next true
boundary) · **duplicate wake** (reconcile twice in one window →
exactly one session row for the natural key) · **rename mid-window**
(stand-down + outcome proceed by id; the shipped code fails this
today) · **delete mid-window** (session finalises at its stored end;
routine gone immediately) · **ghost** (untouched phone through FASTED
→ row finalised MISSED, `elapsedSeconds = 0`, no artifact, 7-day total
unchanged) · **Saturday** (test date set → chain boundary skips the
date's STUDY windows, SLEEP arms normally, enumerator judges no
window that day, streak walk passes over it unbroken) · **eligibility**
(pause Mon–Fri, unpause, view next week → no MISSEDs minted for the
paused span) · **reseed** (v8-shaped DB with exact old seeds → three
targets, ids preserved, edited seed untouched + successor inserted,
flag set, second run a no-op).

— Sten, Maximum-Implementable · routines seat · for the merged routines-rebuild brief
