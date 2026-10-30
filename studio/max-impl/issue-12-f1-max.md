# F-1 MAX — Routine sessions consume the task queue

*Seat: MIP-06 (Maximum-Implementation-Potential, issue #12). Source: the Tasks
audit (`studio/plan/AUDIT-tasks.md`), fix F-1 — "the audit's headline …
Nothing else in this list matters more." Grounded against trunk code,
2026-10-30. Nobody compiles under this spec; prices are builder-days,
paper only.*

## The maximised idea, in one paragraph

A routine window no longer seals with a label — it seals with **work**. At
the instant a routine session is born, the app resolves the head of the
task queue through **one shared resolver** (the same function the manual
arm, the Tasks screen, and the routine engine all read), **bonds** that
task to the session on the session row's already-existing `taskId`
column, freezes its title as the session's `intention`, stamps **where
that intention came from** so the finish receipt can print its provenance
forever, and promotes the bonded task to ACTIVE so every surface agrees
on what is being worked. An empty queue degrades honestly to today's
behaviour (`intention = routine.name`, no bond, no fabrication); SLEEP
never bonds; a task torn mid-session advances the queue live while the
sealed intention stays frozen as the receipt's truth; and each routine
can eventually declare *how* it eats from the queue (global head, a
pinned task, or nothing) — turning the 1 AM errand ("queue it tonight,
it starts at 05:00") from a promise kept by one arm path and broken by
the other into a property of the machine itself.

---

## 0. Ground truth (verified in trunk, not assumed)

| Fact | Where |
|---|---|
| Routine intention is the routine's name; the queue is never consulted | `RoutineDerive.kt` — `DesiredSession.intention // = routine.name`, built at line ~169 as `intention = routine.name` |
| `derive()` is **pure, absolutely**: no clock, no DAO, no Context; every input is a parameter | `RoutineDerive.kt` header. **Consequence: F-1 cannot "just query the DAO" inside derive — the queue must become a derive input.** |
| `RoutineHeartbeat.reconcileOnce()` reads routines / active / sessions (48 h lookback) / settings, calls `derive()`, materialises at most one `desiredSession` | `service/RoutineHeartbeat.kt`. It never reads tasks today — one snapshot read is the entire new plumbing there. |
| `materialiseRoutineSession(desired)` → `startSession(intention, profileName, durationMinutes, routineId, window…)`; `startSession` writes the row inside one transaction with the held-apps/sites seal snapshot | `data/MonolithRepository.kt`. `startSession` has **no `taskId` parameter today**. |
| The session row **already carries the bond column**: `taskId: Int?` (routines rebuild 8→9, Rurik's Arm Ticket bond — "this build lands the column only", no writer) | `data/MonolithModels.kt`, `FocusSessionEntity`. **Core F-1 needs no migration: DB stays v12.** |
| The manual path already honours the head, by a *different* definition: VM `activeTask` = first `ACTIVE`, else first `QUEUED` (unsorted fallthrough); a collector copies its title into `_intentionText`; `armMonolithSession()` arms with that text. The manual arm also writes **no** `taskId`. | `ui/MonolithViewModel.kt` (`activeTask`, `queuedTasks`, init collector, `armMonolithSession`); `ui/ArmProvingController.kt` (`ArmSessionPort.startSession` has no task param). Three head-definitions exist in embryo (VM flow, repository promotion, audit prose) — F-1 MAX unifies them (§2). |
| Promotion/advance machinery exists and is queueOrder-based: `promoteTaskToActive` (demotes old ACTIVE to QUEUED at idx+1, sets new ACTIVE queueOrder 0), `completeTaskAndAdvance` (COMPLETED latch; promotes next QUEUED `minBy queueOrder`, DEFERRED as fallback; books `completedTasksSummary` + count on the session) | `MonolithRepository.kt`. |
| Sealed-intention guard exists on **one** path only: `promoteTaskToActive` refuses to rewrite a sealed session's intention (ESR-02 seal marker), while `completeTaskAndAdvance` rewrites `session.intention = promoted.title` **unconditionally** | `MonolithRepository.kt`. This asymmetry is a live receipt-truth hole F-1 MAX closes (§5). |
| Reseeded routines: `FASTED BLOCK 05:00–08:00 (300–480)`, `DAY BLOCKS 09:00–19:30 (540–1170)`, `SLEEP 20:00–03:30 (1200–210)`, all `ALL_DAYS`; SLEEP's loadout is `SmartLoadout.SLEEP` and SLEEP rows are never finalised/judged | `MonolithRepository.routineTargets()`, `RoutineDerive` (`isSleep`). |
| A window that was claimed is **never reminted**; remainder < 5 min never seals (`MIN_REMAINDER_MINUTES`); a window held by another session yields `OPEN_HELD`, no desired session | `RoutineDerive.deriveOne`. The bond inherits all of this for free — see §6. |
| DB is **v12** (`MIGRATION_11_12` is the last); `fallbackToDestructiveMigration` is gone permanently — any new column is an explicit migration or it is a loud failure | `data/MonolithDatabase.kt`. |
| Horizon bar proportions: `totalMins = Σ estimatedMinutes.coerceAtLeast(10)`, `weight = coerceAtLeast(10)/totalMins`; every composer-made task stores `estimatedMinutes = 0` (MINUTES DIE), so every segment is floored to a phantom equal 10 | `ui/views/BlockitTowerFadersAndCatalogViews.kt:194,198`. Interaction priced in §8 only. |
| SPLIT fabricates: `defaultSubStepsForCategory(title, category)` ignores the title, branches on category alone, and the composer hard-codes `"Study"` (no branch) → every split gets the physics triple | `MonolithModels.kt`, audit item 11. Interaction priced in §8 only. |

**Hard boundary (not maximised past):** Monolith is a blocker, never a
tracker. F-1 MAX moves *which named task a blocking session carries*. It
adds no hours, no syllabus state, no scoring, no streaks — study hours
stay in YPT, syllabus in GMC. `tasksCompletedCount` /
`completedTasksSummary` already exist on the receipt; F-1 changes
nothing about them except making their session's title truthful.

---

## 1. The tiers

F-1 is specified in three tiers. Tier 1 is the audit fix, maximised to
correctness. Tiers 2–3 are how far the idea honestly extends. Each tier
ships alone; no tier silently requires the next.

- **Tier 1 — CORE BOND.** Shared head resolver; queue becomes a derive
  input; `DesiredSession` carries the bond; materialise re-resolves and
  bonds transactionally; SLEEP excluded; empty queue = today's behaviour.
- **Tier 2 — PROVENANCE & THE FROZEN RECEIPT.** One new column
  (`intentionSource`, DB v13) so the receipt prints where its title came
  from, forever, even after the task is deleted; sealed intention frozen
  on *all* advance paths; manual arm writes the same bond.
- **Tier 3 — PER-ROUTINE QUEUE BINDING + THE TONIGHT PREVIEW.** Each
  routine declares `GLOBAL` / `PINNED` / `NONE` (tag binding specified
  but gated, §7.3); the Tasks screen prints the next window's resolved
  head before it fires.

---

## 2. The one resolver (the mechanism everything hangs on)

New pure object in the data layer, e.g. `QueueHead.kt`:

```kotlin
object QueueHead {
    /** The head, or null. Pure; no DAO, no clock. */
    fun resolve(tasks: List<FocusTaskEntity>): FocusTaskEntity?
    fun resolveFor(tasks: List<FocusTaskEntity>, binding: RoutineBinding): FocusTaskEntity? // Tier 3
}
```

**Definition of head (one definition, app-wide):**

1. Candidates: `resolvedStatus ∈ {ACTIVE, QUEUED}`. `COMPLETED` is
   history; **`DEFERRED` is never head** — deferral is the man's explicit
   "saved for later", and a 05:00 routine silently resurrecting a
   deferred leaf would be the machine overruling him. (This *diverges*
   from `completeTaskAndAdvance`'s DEFERRED fallback; §5 aligns that
   fallback to this rule or explicitly re-rules it — it must not remain
   an accidental second definition.)
2. Any `ACTIVE` beats any `QUEUED`. Multiple ACTIVE (a corrupt/legacy
   state the promotion funnel should prevent but the resolver must
   survive): lowest `(queueOrder, id)` wins, deterministically.
3. Otherwise the `QUEUED` task with lowest `(queueOrder, id)`.
   `queueOrder` ties are broken by `id` — the same tiebreak
   `MonolithViewModel.queuedTasks` already uses (`compareBy queueOrder,
   thenBy id`).
4. **`estimatedMinutes` plays no part** — not in ordering, not in
   eligibility, not as a tiebreak. A permutation test (§9) shuffles
   estimates and proves the head never moves. This is the Horizon
   interaction made into law (§8).

**Consumers, all re-pointed at this one function:**

- `MonolithViewModel.activeTask` / `queuedTasks` — the VM's head becomes
  `QueueHead.resolve(tasks)`; the screen can then never show a different
  "next" from the one a routine will bond.
- `RoutineDerive.derive(...)` — new parameter `tasks: List<FocusTaskEntity>`
  (snapshot supplied by the heartbeat, §3).
- `MonolithRepository.materialiseRoutineSession` — authoritative
  re-resolution on a fresh snapshot (§4).
- `completeTaskAndAdvance` / `promoteTaskToActive` — their "next up"
  selection delegates to the resolver instead of open-coding
  `minByOrNull { queueOrder }` (which today ignores the `id` tiebreak
  and the ACTIVE preference — a latent disagreement with the VM).

Cost of the unification itself: the resolver is ~40 lines; the
re-pointing is mechanical. The payoff is the audit's whole complaint
class — one arm honouring a promise another breaks — becoming
structurally impossible rather than patched.

---

## 3. Tier 1 — the core bond

### 3.1 Derive (pure, as the house demands)

`DesiredSession` gains two fields:

```kotlin
data class DesiredSession(
    … existing …,
    val intention: String,        // head?.title ?: routine.name
    val bondedTaskId: Int?,       // head?.id, null on fallback / SLEEP
)
```

`derive()` / `deriveOne()` gain the `tasks` parameter. Inside
`deriveOne`, the desired-session construction (today:
`intention = routine.name`) becomes:

```kotlin
val head = if (isSleep) null else QueueHead.resolve(tasks)
DesiredSession(
    …,
    intention = head?.title ?: routine.name,
    bondedTaskId = head?.id,
    profileName = "Routine: ${routine.name}"   // UNCHANGED — routine
                                               // identity stays in
                                               // profileName/routineId,
                                               // never in intention
)
```

Laws this preserves, deliberately:

- **Purity absolute.** Derive still reads no DAO and no clock; a stale
  heartbeat can only produce a correct evaluation of the snapshot it
  was handed, and §4 makes materialise — not derive — authoritative.
- **No bond, no promotion, no side effect at derive time.** Derive runs
  on passes that seal nothing (remainder < 5, `OPEN_HELD`, `PAUSED`,
  `DISABLED`). The queue is mutated only when a session is actually
  born (§4). Resolving ≠ consuming.
- **SLEEP never resolves.** `isSleep` (loadout `SLEEP`) short-circuits
  to `head = null`: the 20:00 session seals titled `SLEEP`, promotes
  nothing, and cannot wake the phone into a study leaf. (Any future
  routine given the SLEEP loadout inherits this; a routine *named*
  anything with a non-SLEEP loadout bonds normally — the loadout, not
  the name string, is the predicate, matching `deriveOne`'s existing
  `isSleep` test.)
- **One desired session per pass, as today.** The heartbeat already
  materialises `firstNotNullOfOrNull { desiredSession }`; overlapping
  windows therefore bond in routine-list order and the loser evaluates
  `OPEN_HELD` on the next pass. Deterministic; documented in §6.

### 3.2 Heartbeat (the only new plumbing)

In `RoutineHeartbeat.reconcileOnce()`, beside the existing snapshots:

```kotlin
val tasks = dao.getAllTasksSnapshot()
val result = RoutineDerive.derive(routines, now, active, sessions, settings, tasks)
```

One read per pass. Passes are mutex-coalesced and already throttled
(opportunistic ≥ 30 s); the tasks table is the man's own pad (tens of
rows). No new alarm, no new trigger, no change to the chain.

### 3.3 Fallback (empty queue)

Queue empty — or holding only `COMPLETED` / `DEFERRED` rows — resolves
to `null`, and the desired session is **byte-for-byte today's**:
`intention = routine.name`, `bondedTaskId = null`, remainder duration,
same loadout, same window snapshot. F-1 must never delay, refuse, or
condition a seal on the queue having content: the blocker blocks first;
the queue is an intention source, not a gate. Nor may the fallback
reach for `startSession`'s blank-intention default
(`"Deep Uninterrupted Study"`): the routine name is the truthful label
of an unbonded routine session and stays printable as such (Tier 2
prints it as exactly that).

---

## 4. Tier 1 — materialise: the authoritative, transactional bond

Derive's snapshot can be stale by the time the pass materialises
(queue edited from the UI mid-pass; the bonded task torn or deleted in
between). The maximised mechanism therefore resolves **twice**:

1. **Derive-time resolution** (§3.1) — decides *whether* this window's
   session is a queue session and what it provisionally carries; keeps
   derive's tests able to prove the whole behaviour purely.
2. **Materialise-time re-resolution (authoritative)** — in
   `materialiseRoutineSession(desired)`:

```kotlin
suspend fun materialiseRoutineSession(desired: DesiredSession): FocusSessionEntity {
    val fresh = dao.getAllTasksSnapshot()
    val head = if (desired is sleep) null else QueueHead.resolve(fresh)
    // head may differ from desired.bondedTaskId — fresh wins, always.
    return startSession(
        intention = head?.title ?: desired.intention, // desired.intention
                                                      // is routine.name
                                                      // on fallback
        …,
        taskId = head?.id                             // NEW param (§4.1)
    ) // + promotion, inside/around the same unit of work (§4.2)
}
```

Race rulings (each is a one-line consequence of "fresh wins"):

- Head **completed/deleted** between derive and materialise → the fresh
  resolver returns the *next* head, or `null` → fallback. A session
  never bonds a completed or non-existent task.
- Head **renamed** in between → the session carries the new title (the
  seal hasn't happened; nothing has been promised yet).
- A **new task added at the head** (composer FIRST key, audit F-2) in
  between → it wins. Correct: it is the head at birth.
- Queue emptied in between → fallback to `routine.name`, no bond.

### 4.1 `startSession` learns the bond — and nothing else changes

Add `taskId: Int? = null` to `startSession(...)` and set it on the
inserted `FocusSessionEntity`. The column exists (8→9); the insert
already runs in `withTransaction` with the held-count snapshot, so the
bond is written **once, at birth, by the one writer**, in the same
transaction as the seal snapshot — exactly the writer law the
held-count columns already obey ("exactly one writer; no DAO update
for these columns will ever exist"). No DAO update method for
`focus_sessions.taskId` is created. Default `null` keeps every manual
call site compiling and behaving as today until Tier 2 wires it.

### 4.2 Promotion at birth

Bonding without promoting would leave two "current" tasks: the
session's bonded leaf and a stale `ACTIVE` from yesterday. So
materialise, in the same unit of work as the insert (or immediately
after it, before `startShield` — order specified, not left to taste):

- If the bonded task is `QUEUED` → promote via the existing funnel's
  semantics: it becomes `ACTIVE`, `queueOrder = 0`, any prior `ACTIVE`
  is demoted to `QUEUED` (existing idx+1 behaviour), and
  `startedAtSessionElapsedSec = 0` is stamped (the column exists for
  exactly this Nadia offset; 0 is the truthful offset at a session's
  birth).
- If it is already `ACTIVE` → no write. (The common FASTED→DAY
  continuity case, §6.)
- Promotion is **not** consumption: if the seal's proving annuls
  (Slice E tombstone path), the task simply remains `ACTIVE` — which
  is where an unstarted head belongs anyway, and the next arm (manual
  or routine) re-resolves to it. No rollback machinery is built,
  because none is needed: ACTIVE is a queue position, not a spend.

The tombstone check in the heartbeat (annulled window stays refused)
runs *before* materialise today and stays before it: a tombstoned
window never reaches materialise, so it never promotes either.

---

## 5. Tier 2 — provenance, the frozen receipt, and the manual bond

### 5.1 Why Tier 1 alone leaves one lie standing

`FocusSessionEntity.taskId` is a bare `Int?`. Task ids are Room
auto-generated; a deleted task's id can be reissued to a later row.
Months later, the Drawer could join a session's dangling `taskId` to a
*different* task and print a false lineage. Tier 1's `intention` is
already a frozen title string (safe), but *provenance* — "this title
came from the queue" vs "this is just the routine's name" — is only
inferable while the task row lives. A receipt that can only sometimes
prove its title is the exact class of lie this app keeps killing.

### 5.2 The one column (DB v12 → v13)

```sql
ALTER TABLE focus_sessions ADD COLUMN intentionSource TEXT NOT NULL DEFAULT ''
```

Vocabulary (plain strings, like `routineOutcome` / `outcome`):

| Value | Meaning | Set when |
|---|---|---|
| `''` | Pre-v13 row — provenance never recorded, prints nothing | default; **never backfilled** |
| `QUEUE_HEAD` | `intention` is the frozen title of `taskId`, resolved at birth | Tier 1 materialise with a head |
| `ROUTINE_NAME` | Queue had no head; `intention = routine.name` | Tier 1 materialise, fallback |
| `ROUTINE_SLEEP` | SLEEP-loadout window; bonding excluded by rule | SLEEP materialise |
| `MANUAL_TASK` | Hand arm; `intention` from the head at latch | §5.4 |
| `MANUAL_TEXT` | Hand arm; free-typed intention, no task | §5.4 |

One migration, schema-only, zero data writes — the house pattern for
9→10, 10→11, 11→12. `intention` itself remains the frozen title
snapshot, so the receipt needs no join and no task row to print the
truth. **Tier 3's routine columns ride this same v13 bump** (§7) so the
app pays one migration, not two.

### 5.3 What prints

- **Finish receipt** (its printed-slip character is a proven keeper —
  it does not get redesigned here, only one honest line): under the
  intention, a quiet provenance line —
  `FROM QUEUE · #01` for `QUEUE_HEAD` (the `#NN` is the leaf's number
  at seal, from queueOrder at birth, not a live re-read);
  `ROUTINE · QUEUE EMPTY` for `ROUTINE_NAME`;
  nothing for `''`, nothing extra for SLEEP.
  The `#NN` figure must be captured at birth if printed — either
  derive it from the bonded promotion (bonded task is `queueOrder 0` /
  `#01` by construction in Tier 1 — true today by the promotion rule,
  so `#01` is printable without a column) — do **not** add a column for
  it. If Tier 3 PINNED bonding ships, a pinned task need not be #01;
  then the receipt prints `FROM QUEUE · PINNED` instead. No column
  either way.
- **Deck while sealed:** unchanged source — the seal freeze / session
  row. The bonded task's sub-steps print via the existing task line;
  see the SPLIT gate in §8.
- **Drawer / session detail:** the stored row shows intention +
  provenance word; the bonded task title is the intention string
  itself, so a deleted task leaves a fully truthful record.

### 5.4 Freeze the sealed intention on *every* path (closes the ESR-02 asymmetry)

With routine sessions now carrying queue titles, the
`completeTaskAndAdvance` hole (§0) becomes load-bearing: tearing the
bonded task mid-session currently rewrites the live session's
`intention` to the next task — silently retitling a sealed receipt.

Maximised ruling:

- `session.intention` is **written once, at birth, by `startSession`,
  for every session kind.** No advance, promotion, reopen, or defer
  path ever rewrites it. The existing sealed-marker guard in
  `promoteTaskToActive` is deleted and replaced by the stronger,
  simpler rule: the write itself no longer exists on those paths;
  `completeTaskAndAdvance`'s `dao.updateSession(copy(intention = …))`
  is removed (its summary/count booking stays untouched).
- The **live** "what am I on now" fact already has an honest surface:
  the VM's `_intentionText` follows `activeTask`, and the Deck's task
  line reads the task, not the receipt title. Nothing visible is lost;
  only the receipt stops lying.
- Scope note, priced honestly: this also changes *manual* sessions
  (their receipt keeps the arm-time title instead of the last promoted
  title). That is the truthful behaviour and this spec recommends it,
  but it is the one place Tier 2 touches a surface beyond F-1's letter.
  Conservative variant, if Ren rules narrow: apply the freeze only
  when `session.routineId != null`, keep manual rewriting. The variant
  costs the same lines; it buys a permanent two-rule system. Flagged
  as **Decision D2** (§10), not smuggled.

### 5.5 The manual bond (same tier, small)

`ArmSessionPort.startSession` / `RepositoryArmSessionPort` gain the
`taskId` + source: the VM's latch passes `activeTask.value?.id` when
`_intentionText` came from the head collector, `null` + `MANUAL_TEXT`
when the man typed over it. The cascade's proving/seal machinery is
untouched (the bond rides the existing insert). Result: every session
row in the app states what it was armed on — one provenance system,
not a routine-only patch.

---

## 6. Edge cases — the full set, each ruled

1. **Empty queue at window start.** Fallback (§3.3). Session seals on
   time, titled with the routine name. Nothing is promoted, nothing is
   fabricated, the receipt (Tier 2) says `ROUTINE · QUEUE EMPTY`.
2. **Queue holds only DEFERRED / COMPLETED.** Same as empty (§2 rule 1).
   In particular the 05:00 session must **not** bond yesterday's
   deferred leaf.
3. **Queue edited before the window.** Wins trivially — resolution
   happens at birth, not at scheduling. The 1 AM errand (audit's
   Muede walk) is this case: queue at 01:00, FASTED seals at 05:00
   with that leaf. **This is the acceptance case (§9, test 1).**
4. **Queue edited mid-window, before materialise.** Back-to-back and
   held windows materialise late (below); any edit landing before the
   materialising pass is honoured by the fresh re-resolution (§4).
5. **Queue edited mid-window, after materialise.** The live session is
   **not** retitled, not re-bonded (sealed intention, §5.4). Edits
   apply to the *next* session. Reordering the queue under a running
   session is safe and meaningless to it — except tearing (case 7).
6. **Window held by a manual session (`OPEN_HELD`).** No desired
   session, no bond, no promotion. If the manual session ends
   mid-window, the next reconcile pass re-derives: unclaimed window,
   remainder ≥ 5 → materialises *then*, bonding the head **as it
   stands at that moment**. A mid-window edit made during the manual
   session is therefore honoured — the freshest correct behaviour,
   and it falls out of the existing state machine for free.
7. **Bonded task finished early (torn during its routine session).**
   `completeTaskAndAdvance` books it (summary + count, as today),
   promotes the next head as the live ACTIVE task; the session's
   frozen intention/provenance do not move (§5.4); the session runs to
   its window end regardless — **a routine session's duration is the
   window remainder, never the task.** Finishing the queue does not
   end the block; emptying the queue mid-session changes nothing
   about the seal. (Blocker, not tracker: the session owes the
   window, not a task count.)
8. **Bonded task deleted mid-session** (DEL + undo window passed).
   The session row stands: frozen title + `taskId` (now dangling) +
   provenance. With Tier 2 the record stays truthful; the delete
   path's existing next-active advance keeps the queue coherent. No
   session write occurs.
9. **Bonded task renamed mid-session.** Live task surfaces show the
   new name (they read the task row); the session's frozen intention
   keeps the seal-time name (it is a receipt of what was sealed).
   Both are true; they are about different instants. Documented here
   so no future seat "fixes" either side.
10. **Multiple routines, one queue (the real day).** FASTED 05:00
    bonds head A. A torn → B becomes ACTIVE live. DAY BLOCKS 09:00
    materialises against the then-current queue: head is B (or A,
    unfinished and still ACTIVE → re-bonded, no write, §4.2). The
    queue is consumed *across* the day by successive windows exactly
    as the man queued it — no per-window reset, no double-bond.
    SLEEP 20:00 bonds nothing. Next morning: head is whatever the
    queue's truth is then.
11. **Two routines whose windows overlap / share a start.** Derive
    emits both as desired only if both are unclaimed and unheld; the
    heartbeat materialises one per pass in routine-list order; the
    second sees the active session and goes `OPEN_HELD`. One bond,
    one session. Deterministic, if arbitrary — overlapping routines
    are already a schedule smell the app tolerates elsewhere the
    same way.
12. **Remainder < 5 min / late fire (Doze, boot inside window).**
    No seal → no bond → **no promotion** (promotion lives in
    materialise only, §3.1). A window the machine missed does not
    consume the queue's head as a side effect. Judgement
    (MISSED / NOT_DELIVERED / LATE_FIRE) is untouched — F-1 changes
    no outcome stamps, and a bonded-but-late session is judged by
    the existing E1–E4 evidence exactly as an unbonded one.
13. **Paused / disabled routine.** Derive returns before the desired
    construction; the resolver is never invoked for it. Pausing
    FASTED does not disturb the queue at all.
14. **Routine renamed / retimed.** Fallback intention reads the
    *current* row at derive time (existing behaviour); claimed
    windows keep their frozen rows (R5 stored-snapshot rule) —
    bonded rows likewise. `routineId`, never the name string, is
    identity (existing law; F-1 adds no name-matching anywhere).
15. **Routine deleted mid-window.** Orphan finalise reads the stored
    session row; `taskId` / provenance ride along and finalise
    normally. No task write is owed by an orphan.
16. **Reboot / process death mid-window.** The claim check
    (`windowStartMillis` match) prevents reminting; the bond is on
    the surviving row. On the window's *next* day, normal flow.
17. **Corrupt queue shapes.** Multiple ACTIVE → resolver rule 2.
    `queueOrder` duplicates → `(queueOrder, id)` total order.
    ACTIVE task also being the bonded task of the *live* session
    (manual session running, routine held) → case 6. A task whose
    `status` string is unparseable → `resolvedStatus` already
    defaults to QUEUED; the resolver inherits that and treats it as
    a QUEUED candidate. No new failure mode is introduced.

---

## 7. Tier 3 — how far the idea extends

### 7.1 Per-routine queue binding

One routine, one declared appetite. `RoutineEntity` gains (same v13
bump as §5.2 — one migration carries both):

```sql
ALTER TABLE routines ADD COLUMN queueBinding TEXT NOT NULL DEFAULT 'GLOBAL';
ALTER TABLE routines ADD COLUMN pinnedTaskId INTEGER;  -- nullable, no default = none
```

| Binding | Resolver behaviour at that routine's materialise |
|---|---|
| `GLOBAL` (default; every existing row) | `QueueHead.resolve(tasks)` — Tier 1 behaviour. |
| `PINNED` | If `pinnedTaskId` names a task in {ACTIVE, QUEUED} → bond it (promote if QUEUED). If it is COMPLETED / DEFERRED / deleted → **fall back to the global head**, and if none, to the routine name. A pin is a preference, never a seal-blocker and never a resurrection of a finished leaf. Provenance prints `FROM QUEUE · PINNED`. |
| `NONE` | Never resolve; `intention = routine.name`, provenance `ROUTINE_NAME`. For a window the man wants as a pure block (e.g. a DAY BLOCKS stretch reserved for whatever the day brings). SLEEP is *forced* NONE by loadout regardless of this column — the column can narrow bonding, the loadout rule cannot be widened by it. |

Editor cost: the routine editor gains one segmented control
(GLOBAL / PINNED / NONE) + a task picker shown only under PINNED.
No other surface changes. Reseed: untouched — new columns default
every reseeded row to today's maximised behaviour (`GLOBAL`).

### 7.2 The tonight preview (the promise, printed before it fires)

The Tasks screen's QUEUE header (which the audit already makes the
screen's one header, F-3) gains a quiet second line when a next
routine window exists and no session is running:

```
NEXT · 05:00 FASTED BLOCK → <resolved head title | NOTHING QUEUED>
```

Computed from `RoutineSchedule.nextWindow` over live routines +
`QueueHead.resolveFor(nextRoutine.binding)` — a pure projection of
the same two functions materialise will use, so the preview **cannot
promise a different head from the one that will bond** (any edit
before 05:00 updates both together; the line is a live print, not a
commitment — the seal-time re-resolution in §4 remains the truth).
This is the cheapest surface in the whole spec and arguably the one
the man feels: at 01:00 he *sees* the errand land.

### 7.3 Tag binding — specified, deliberately gated

The obvious fourth binding — "FASTED takes the first `Physics`-tagged
leaf" — is **not buildable honestly today**: `FocusTaskEntity.categoryTag`
exists, but the composer hard-codes `"Study"` for every task it creates
(the same fact that makes SPLIT fabricate, §8), so a tag binding would
match either everything or nothing. Tag binding ships only after a
composer that lets the man tag a leaf exists; the resolver's
`resolveFor` signature already leaves the slot
(`binding = TAG(tag)` → first {ACTIVE, QUEUED} with
`categoryTag == tag`, else global head, else name). Recorded here so
no builder invents it early against a dead field.

### 7.4 Explicitly NOT extended (the maximisation stops here)

- **No queue-per-sub-window splitting** (a routine window does not
  pre-assign tasks to hours inside it; the queue advances by tears,
  live — case 7).
- **No auto-queueing** from GMC syllabus, YPT, or anywhere else. The
  queue is the man's handwriting; F-1 gives routines eyes for it,
  not a second author.
- **No completion requirement** — a window attended with zero tears
  is judged by existing evidence rules; the bond never converts
  attendance into task performance (blocker, not tracker).

---

## 8. Secondary audit items — priced *only* as they interact with F-1

### 8.1 Horizon bar + SPLIT proportions (audit items 1, F-3)

The bar is killed by F-3 on its own verdict (~113 lines deleted, zero
added — Dens's price; not re-priced here). Its interaction with F-1
is exactly two lines long:

1. **F-1 makes queue order load-bearing machinery.** Today the bar's
   equal phantom segments merely *misdescribe* a queue nothing depends
   on. After F-1, the queue's head is what a 3-hour FASTED session
   will be titled and bonded to — printing that queue as proportional
   workload shares (of a quantity, `estimatedMinutes`, that the
   composer no longer even asks) would put a fabricated chart directly
   over a now-functional mechanism. Keeping the bar post-F-1 is
   strictly worse than keeping it today. **Sequencing: F-3 lands
   before or with Tier 1; it needs nothing from F-1.**
2. **The resolver's minutes-independence (§2 rule 4) is the legal
   form of the kill.** Any future surface or mechanism that orders,
   sizes, or selects by `estimatedMinutes.coerceAtLeast(10)` reopens
   the lie. The permutation test (§9, test 8) is priced inside Tier
   1's test cost — no separate builder time.

No other Horizon cost touches F-1: `totalQueueMinutes` (VM) is not
read by the resolver, the heartbeat, or materialise.

### 8.2 SPLIT (audit item 11, F-6 hand-split)

Interaction, likewise bounded:

1. A bonded task's sub-steps print on the session's task line. If the
   head leaf was ever SPLIT under the current fabricator, the routine
   session opens displaying steps the man never wrote ("Check units
   & boundary conditions" on a biology leaf) — now at session scale,
   not just leaf scale.
2. **Guard (priced inside Tier 1, ~3 lines):** bonding never calls
   `autoSplitTaskIntoMicroSteps` / `defaultSubStepsForCategory`; a
   bond carries the task's sub-steps *as stored* or none. F-1 adds no
   generation of any kind.
3. **Sequencing:** F-6 (hand-split: blank ruled lines via the dead
   `onAddSubStep`) remains its own SMALL–MEDIUM item. Tier 1 does not
   depend on it — it depends only on *not adding* fabrication. If F-6
   lands first, bonded leaves simply carry hand-written steps, which
   is the maximised end state for the Deck's task line.

---

## 9. Acceptance tests (paper spec; nobody compiles)

Extends `RoutineDeriveTest` (pure, JUnit — derive's purity is what
makes Tier 1 provable without Android) + repository tests in the
existing style:

1. **The 1 AM errand.** Queue = [A QUEUED q0, B QUEUED q1] at 01:00;
   derive FASTED at 05:00 → `OPEN_IDLE`, desired `intention == A.title`,
   `bondedTaskId == A.id`, `durationMinutes == 180` (full remainder).
2. **Empty queue.** Derive DAY BLOCKS at 10:00, tasks = [] →
   `intention == "DAY BLOCKS"`, `bondedTaskId == null`. (And the same
   with only COMPLETED / only DEFERRED rows.)
3. **ACTIVE preference + tiebreaks.** Tasks [QUEUED q0, ACTIVE q5] →
   head is the ACTIVE one. Two ACTIVE → lowest `(queueOrder, id)`.
   QUEUED order tie → lower id.
4. **SLEEP.** Queue non-empty; derive SLEEP at 21:00 →
   `intention == "SLEEP"`, `bondedTaskId == null`.
5. **No-seal, no-consume.** Same queue at remainder 4 min → desired is
   null (existing rule) — and at `OPEN_HELD` / `PAUSED` / `DISABLED`
   → desired null. (Promotion's absence is a materialise-side
   assertion: repository test asserts task rows unchanged after a
   derive-only pass.)
6. **Materialise freshness (repository).** Derive with head A; complete
   A; materialise → bonds B (or fallback if none). Delete A variant →
   same. Rename A variant → new title.
7. **Promotion (repository).** Materialise with head QUEUED + a stale
   ACTIVE Z → session row `taskId == head.id`; head now ACTIVE q0 with
   `startedAtSessionElapsedSec == 0`; Z demoted to QUEUED. Head already
   ACTIVE → no task writes.
8. **Minutes independence (pure).** Any permutation of
   `estimatedMinutes` over a fixed queue → identical head from
   `QueueHead.resolve`, and identical desired sessions from derive.
9. **Frozen intention (Tier 2, repository).** Bond A; complete A
   mid-session → session `intention` still `A.title`,
   `completedTasksSummary` booked, B is ACTIVE, session row's
   `intentionSource == QUEUE_HEAD` throughout.
10. **Continuity across windows (repository/derive).** FASTED bonds A,
    A unfinished; DAY BLOCKS derive at 09:00 with A ACTIVE → desired
    bonds A again. A torn in FASTED → DAY bonds B.
11. **Migration (Tier 2/3, if taken).** v12→v13: existing rows read
    `intentionSource == ''`, `queueBinding == 'GLOBAL'`,
    `pinnedTaskId == null`; PRAGMA scan still finds no
    `testDateEpochDay` (the standing struck-column assertion, carried
    forward as at v9–v12).
12. **Preview consistency (Tier 3).** For a fixture queue + routine
    set, the preview's resolved head equals the head derive produces
    at that window's start with an unchanged queue.

---

## 10. Costs, demands, decisions

### Cost headline

| Tier | Contents | Price |
|---|---|---|
| **1 — Core bond** | `QueueHead` (~40 lines) + 3 consumer re-points; derive signature + `tasks`; heartbeat +1 snapshot read; `DesiredSession` +2 fields; `startSession(taskId)`; materialise re-resolve + promote (~30 lines); SPLIT no-generate guard (~3 lines); tests 1–8, 10 | **½–1 builder-day** (the audit's SMALL–MEDIUM, confirmed against code — the bond column and promotion funnel already existing is what keeps it there) |
| **2 — Provenance & freeze** | DB v13 migration (1 column of Tier 2's; shares the bump with Tier 3); `intentionSource` writes at both materialise and manual latch; receipt provenance line; sealed-intention freeze (deleting two writes, adding none); manual bond via `ArmSessionPort`; tests 9, 11 | **+½ builder-day** |
| **3 — Binding & preview** | `routines` +2 columns (same v13); `resolveFor`; routine-editor segmented control + pinned picker; QUEUE-header preview line; tests 11, 12 | **+1–1½ builder-days** (tag binding excluded — gated, §7.3) |
| **Maximum, all tiers** | | **≈ 2–3 builder-days**, phased 1 → 2 → 3, each independently shippable |

Interaction items (not in the totals above, by instruction): F-3
Horizon kill ~113 lines deleted / zero added (Dens) lands before or
with Tier 1; F-6 hand-split stays its own SMALL–MEDIUM and is *not* a
Tier 1 dependency — only the 3-line no-generate guard is, and it is
already counted in Tier 1.

### Demands on the app

- **Schema:** Tier 1 — none (DB stays v12; `focus_sessions.taskId`
  already exists). Tiers 2+3 — exactly one bump to v13, three columns
  total across two tables, schema-only, no backfill, explicit
  migration (destructive fallback is gone; a missing migration is a
  loud failure, by standing law).
- **Machinery:** derive's signature changes — every `derive()` caller
  and every `RoutineDeriveTest` call site gains the `tasks` argument
  (mechanical; counted in Tier 1). No new alarms, services, tables,
  or triggers. Heartbeat gains one snapshot read per pass.
- **Surfaces:** Tier 1 changes no pixels — the FASTED session simply
  opens titled with the task, and the Deck's existing task line shows
  the bonded leaf. Tier 2 adds one quiet line to the receipt. Tier 3
  adds one editor control + one QUEUE-header line.
- **Invariants new code must hold:** derive stays pure; `taskId` and
  `intentionSource` are written once at birth by `startSession` and
  never updated; promotion happens only inside materialise; SLEEP
  loadout never resolves a head; the resolver never reads
  `estimatedMinutes`.

### Decisions flagged (Ren's, not this seat's)

- **D2 (§5.4):** freeze sealed intention for manual sessions too
  (recommended) vs routine-only freeze (narrower, two rules forever).
- **D3 (§7.1):** Tier 3 in the same pass as Tier 2 (one v13, editor
  work bundled) or deferred until Tier 1 has lived on the phone.
- **D4 (§2 rule 1):** `completeTaskAndAdvance`'s DEFERRED fallback —
  align it to "DEFERRED is never head" (recommended; one definition)
  or explicitly re-rule it as a completion-only exception and document
  the divergence in `QueueHead`.

— MIP-06 (Maximum-Implementation-Potential, issue #12)
