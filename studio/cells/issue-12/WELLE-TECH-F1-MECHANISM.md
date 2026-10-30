# WELLE — Technical execution spec: F-1 mechanism (#12)

*Welle, technical execution. Synchronous review of the mechanism under
the data layer: how a routine-born session gets its intention, where
that value is frozen, and what can still corrupt it. Evidence is from
trunk v12 (`Focus-app` main), `agent/issue-12` (F-1 as built), and
`origin/batch-10` (F-1 as composed). Nobody compiled or merged —
Ren does both, on Sarthak's word.*

## 1. The bug, mechanically

Before F-1 the title of a routine session was decided in exactly one
place and never revisited:

- `RoutineDerive.deriveOne` built `DesiredSession(intention = routine.name)`
  (trunk `RoutineDerive.kt:169`; the data class documented the field
  as `// = routine.name`).
- `RoutineHeartbeat.reconcileOnce` → `MonolithRepository.materialiseRoutineSession`
  forwarded `desired.intention` into `startSession` (`MonolithRepository.kt:1091`),
  which inserted the row with that string.
- `sealSession()` (the seal proper) only re-anchors wall/mono and
  re-signs the HMAC — it never touches `intention`. So "sealing with
  intention = routine.name" is precise: the name was baked in at
  materialisation, seconds before the seal, and the seal ratified it.

The hand path never had this bug: `MonolithViewModel.activeTask` is a
projection (first ACTIVE, else first QUEUED, over the DAO-ordered
flow) and the arm path titles the session from it. F-1 makes the
routine path read the same projection.

## 2. The mechanism as built (agent/issue-12, composed into batch-10)

No schema change. The existing `focus_sessions.intention TEXT NOT NULL`
column carries F-1 whole. No migration, no provenance column.

1. **Resolver — `RoutineDerive.resolveHeadTitle(tasks: List<FocusTaskEntity>): String?`**
   Pure function in the data layer. Input contract is the DAO's own
   ordering (`MonolithDao.getAllTasksSnapshot`: status CASE
   ACTIVE→QUEUED→DEFERRED→else, `queueOrder ASC, id ASC`). Returns the
   first row whose `resolvedStatus` is ACTIVE or QUEUED and whose
   title is non-blank (blank titles skip to the next eligible row).
   DEFERRED never headlines; COMPLETED never headlines. `null` when
   nothing is eligible. No DAO/Context/clock inside — `derive()`'s
   purity law is untouched because the title arrives as a parameter.
2. **Derive — `derive()`/`deriveOne()` gain a final parameter
   `headTaskTitle: String?`, no default in production signatures.**
   A forgotten call site is a compile error, not a silent return to
   routine names. The seal line is one glanceable expression:
   `intention = if (isSleep) routine.name else headTaskTitle?.takeIf { it.isNotBlank() } ?: routine.name`.
3. **SLEEP exception (lead-ruled):** a SLEEP routine always seals
   `routine.name`, head or no head — the head names work, never sleep.
   SLEEP's J2 non-finalisation is untouched.
4. **Call sites:** `RoutineHeartbeat.reconcileOnce` resolves the head
   from `dao.getAllTasksSnapshot()` in the same pass that derives;
   `MonolithViewModel.evaluateTheme()`'s `deriveOne` call passes
   `headTaskTitle = null` by name (it reads only `.state` and discards
   the DesiredSession — no task list is threaded into it).
5. **H1 — seal-time re-resolve (wave-2 fix, present in batch-10):**
   derive-time resolution is stale by construction — the finalise
   loop between derive and materialise does real IO, and a queue edit
   landing inside it would otherwise seal a stale head for the whole
   session. So at materialise, the heartbeat re-resolves:
   `freshHead = resolveHeadTitle(dao.getAllTasksSnapshot())`,
   `desired.copy(intention = freshHead ?: routine.name)` (non-SLEEP
   only). The intention is then frozen into the session row inside
   `startSession`'s birth transaction. After that instant nothing in
   the seal path can change it: `proveAndSeal` → `sealSession()`
   re-anchors + re-signs only.

**Ordering / transaction guarantees, stated honestly:** the five DAO
reads in a reconcile pass are NOT one transactional snapshot (the
code says so itself). The guarantee F-1 actually provides is weaker
and sufficient: the title is whatever the queue head was at the last
read before the birth transaction, and from the birth transaction
onward it is a row fact, immune to later queue edits. Sealing
consumes, promotes, and completes nothing; no `taskId` is written
(the Arm Ticket bond column stays NULL for routine births).

## 3. Edge cases that would corrupt truth

| Case | Behaviour | Verdict |
|---|---|---|
| Empty queue | resolver → `null` → fallback `routine.name` (pre-F-1 behaviour, pure widening) | Safe |
| DEFERRED-only queue | `null` → routine name. `completeTaskAndAdvance`'s DEFERRED fallback was explicitly NOT reused | Safe |
| All titles blank / head blank | blank skips to next eligible; all-blank → `null` → routine name | Safe |
| Queue edited between derive and materialise | H1 re-resolve reads the queue as it stands at materialise | Safe |
| Queue edited after materialise / after seal | Row is a snapshot; no rewrite on this path | Safe |
| Two routines, one morning | Both read the same head at their own seals; queue unconsumed after both | Safe by design |
| Remainder < 5 min (`MIN_REMAINDER_MINUTES`) | No DesiredSession is emitted; a head never resurrects a dying window | Safe |
| Paused window | Head does not override PAUSED | Safe |
| Annulled proving (tombstone) | Heartbeat checks `isTombstoned(routineId, windowStartMillis)` BEFORE materialising; tombstones are per-process (in-memory set in `SessionSealTransition`), so a process death clears them — but the window's claim row and the derive claim-check (`mine.firstOrNull { windowStartMillis == … }`) still prevent a second mint for a claimed window | Safe, with the per-process caveat stated |
| SLEEP window | Always `routine.name`; stands down unstamped (J2) | Safe |

## 4. Two surviving truth risks (priced, NOT fixed by F-1)

1. **Unguarded sealed-intention rewrite in `completeTaskAndAdvance`.**
   Still present in batch-10 (`MonolithRepository.kt:2865`):
   on promotion it does `currentSession.copy(intention = promoted.title)`
   with no seal guard, while `promoteTaskToActive` (same file, :2978)
   refuses the same write when `sealedSessionIdFromMarker() == activeSession.id`
   (ESR-02). Consequence: completing a task mid-session silently
   re-titles a SEALED routine session, and the receipt's title source
   diverges from what was sealed. The #12 panel verdict was PASS
   conditional on Ren's ESR-02 ruling at merge — that ruling is still
   the open item. **Price if ruled "guard it":** ~5 lines (mirror the
   `promoteTaskToActive` guard) + 1 test. **Risk of the fix:** low —
   the promotion itself proceeds; only the session-row write is refused.
2. **Seed-sync dedup-key collision, made likelier by F-1.**
   Seed-sync dedups non-active sessions on
   `intention|targetDurationSeconds|completedTasksSummary`
   (batch-10 `MonolithRepository.kt:441`) and DELETES later rows with
   a duplicate key. Before F-1, sessions from different routines
   differed by routine name. Now two routine sessions that sealed the
   same head title, with equal target duration and equal (typically
   empty) summary, collide — a later seed-sync can delete a real
   session record. **Price if ruled "fix the key":** add
   `routineId|windowStartMillis` to the key — ~2 lines + 1 regression
   test; risk low, but it changes dedup behaviour for genuinely
   duplicated seed rows, so it wants one seed-fixture test proving
   true duplicates still collapse. This is a merge/Ren decision; F-1's
   builder stated it and did not touch the path. I concur: do not let
   it ride silently into the phone build.

## 5. Price of F-1 as a whole

- Effort already spent: 3 production files (`RoutineDerive.kt`,
  `RoutineHeartbeat.kt`, `MonolithViewModel.kt`), 15 new derive tests
  (7 resolver + 8 derive) in `RoutineDeriveTest.kt`; CI on
  agent/issue-12 reported assemble green + F-1 tests passing
  (commit 2993fbc).
- Data risk to existing users: **zero.** No schema change, no
  migration, no backfill. Sessions already sealed keep the routine
  name they were born with — F-1 never rewrites history, it only
  titles future births truthfully.
- Residual risk: the two items in §4, both merge rulings, both cheap.

— Welle, technical execution
