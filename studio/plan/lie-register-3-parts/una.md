# LIE REGISTER 3 — UNA (Critics seat 15, consistency)

Ground: merged trunk only. Primary file:
`app/src/main/java/com/example/ui/MonolithViewModel.kt` (1123 lines, read whole).
Second witness wherever the VM touches token / tasks / emergency break:
`app/src/main/java/com/example/data/MonolithRepository.kt`.
Trace-chain witnesses (composition + the second ticker) are named as such where used:
`MainActivity.kt`, `ui/components/AirlockAndDisarmOverlays.kt`,
`ui/screens/TactileDeckScreen.kt`, `ui/screens/CurioVaultScreen.kt`,
`service/MonolithShieldService.kt`, `data/MonolithWeek.kt`, `data/MonolithDatabase.kt`.
Lane reports were not used as evidence. No code changed — charges only.

---

## Q1 — Token state: any surviving VM-local token state?

### CHARGE U-1 — The ledger flow is re-seeded with a hardcoded `1` inside the VM; on a spent week the trunk renders "1 remaining" until the ledger's first emission lands

`MonolithViewModel.kt:150-152`:

```kotlin
val weeklyEmergencyTokensRemaining: StateFlow<Int> =
    repository.observeEmergencyBreaksRemaining()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
```

The comment directly above it (`MonolithViewModel.kt:142-149`) claims the old
in-memory counter is dead — *"The old bare in-memory MutableStateFlow(1) is deleted…
The repository is the law now; this flow is only its rendering"* — but the deleted
counter's exact value survives as the `stateIn` initial value. A `StateFlow` holds
its initial value from construction until the upstream flow's first emission.
The upstream is not instant: `MonolithRepository.kt:1202-1209` derives through
`weekAnchorTickFlow()` → `flatMapLatest` → a Room query
(`dao.observeSpendsForWeek(anchor)`), mapped by
`MonolithWeek.kt:41-42` — `(1 - spendCount).coerceAtLeast(0)`.

So on every fresh ViewModel (process start, VM recreation) in a week whose spend
is already booked, the true ledger value is `0` and every consumer of this flow
renders `1` for the intervening frames:

- Deck badge — `MainActivity.kt:195` collects the flow, `MainActivity.kt:344`
  passes it to the Deck, `TactileDeckScreen.kt:522` prints it:
  `monoBadge = "$weeklyEmergencyTokensRemaining/1 WEEK"` — a spent week opens
  reading **1/1 WEEK**.
- Emergency overlay — `MainActivity.kt:562-567` passes the same value as
  `tokensRemaining` into `DisarmChallengeOverlay`; see U-1's consequence at Q4.

The question asked "even for one frame." This is exactly that frame — and it is
the worst possible frame: the first one, on the surface whose whole job is the
count. The repository side is clean (see Acquittal A-U1); the lie is introduced
by the VM's seed value, in the VM's own file, directly under a comment denying it.

### CHARGE U-2 — A second hardcoded `1` sits at the Deck composable's parameter default, in the same trace chain

`TactileDeckScreen.kt:115`:

```kotlin
weeklyEmergencyTokensRemaining: Int = 1,
```

`MainActivity.kt:344` always passes the VM value today, so this default is
dormant — but it is a second place in the repository→overlay trace where "1"
is asserted without consulting the ledger. Any future caller (preview, test,
a new surface) that omits the argument silently renders a full token on a
spent week. One fact, one home: this default has no home in the ledger.

### ACQUITTAL A-U1 — No VM-local *mutable* token state survives; no decrement; the challenge state holds no count; the repository derivation is the single formula

- The only token-typed state in the VM is the flow at `MonolithViewModel.kt:150-152`
  above. A full grep of the VM for `token` returns only that flow and its comments
  (`:70-74`, `:144-149`, `:952-954`, `:966-973`). There is no
  `MutableStateFlow<Int>` token, no cached count field, no `_token.value--` anywhere.
- `DisarmChallengeState` (`MonolithViewModel.kt:67-75`) carries only
  `isVisible` and `requiredHoldSeconds = 60` — no snapshot count, exactly as its
  own comment requires.
- Repository derivation is one formula in one place:
  `MonolithRepository.kt:1202-1209` (`observeEmergencyBreaksRemaining`) and
  `:1176-1179` (the suspend twin) both reduce to
  `emergencyBreaksRemainingFromSpendCount(spends.size)` =
  `(1 - spendCount).coerceAtLeast(0)` (`MonolithWeek.kt:41-42`), re-anchored on
  week rollover while collected (`MonolithRepository.kt:1188-1198`).

Verdict on Q1: the *architecture* is acquitted; the *seed value* is charged (U-1, U-2).

---

## Q2 — `completeTaskAndAdvance`: sound, Drawer write, and where "actual seconds" come from

### ACQUITTAL A-U2 — The VM's `completeTaskAndAdvance` plays NO harmonic seal; the merge ruling was obeyed in the VM

`MonolithViewModel.kt:587-601`, whole function:

```kotlin
fun completeTaskAndAdvance(taskId: Int) {
    // No sound here (merge ruling on Linea's flag): the seal is
    // the SESSION sound and was misspent on a task act - and it
    // fired before the completion even ran. The tear's
    // completion pulse in the view is the sole owner of this act.
    viewModelScope.launch {
        val nextActive = repository.completeTaskAndAdvance(taskId)
        if (nextActive != null) {
            _intentionText.value = nextActive.title
            if (activeSession.value?.isActive != true) {
                _dialMinutes.value = nextActive.estimatedMinutes
            }
        }
    }
}
```

No `soundEngine` call of any kind in the body. The only other VM entry to the
same repository act, `toggleChecklistItem` (`MonolithViewModel.kt`, delegating
straight to `completeTaskAndAdvance(id)`), is likewise silent. The repository
function itself (`MonolithRepository.kt:1437-1500`) contains no sound call —
it has no sound engine. Clean on this point.

### ACQUITTAL A-U3 (partial) — The stored completed-task row has the right *shape*

`MonolithRepository.kt:1453-1461`:

```kotlin
var currentSession = dao.getActiveSessionSnapshot()
val nowWall = System.currentTimeMillis()
val actualMins = (target.actualSecondsSpent / 60).coerceAtLeast(1)
val completedTask = target.copy(
    status = TaskQueueStatus.COMPLETED.name,
    completedAtMillis = nowWall,
    completedInSessionId = currentSession?.id ?: 0
)
dao.updateTask(completedTask)
```

The row is persisted as COMPLETED with a wall-clock completion instant, its
session id, and its `actualSecondsSpent` carried through untouched on the entity
(`MonolithModels.kt:99` — `val actualSecondsSpent: Int = 0`). What the Drawer
law needs structurally is written. What those seconds *are* is charged next —
the shape acquits, the value does not.

### CHARGE U-3 — Task "actual seconds" are TICK-COUNT, not wall-clock: `+1` per `updateSessionProgress` call, and TWO trunk drivers call it (~90 booked per 60 real seconds). Register 2's LR2-S2-24 disease is alive in the merged trunk

The accrual, quoted — `MonolithRepository.kt:1347-1359`:

```kotlin
private suspend fun incrementActiveTaskSecond() {
    val tasks = dao.getAllTasksSnapshot()
    val activeTask = tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        ?: tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.QUEUED }
    if (activeTask != null) {
        dao.updateTask(
            activeTask.copy(
                status = TaskQueueStatus.ACTIVE.name,
                actualSecondsSpent = activeTask.actualSecondsSpent + 1
            )
        )
    }
}
```

It is invoked unconditionally (outside safe break) on *every* progress call —
`MonolithRepository.kt:1032-1034`:

```kotlin
if (!session.isInSafeBreak) {
    incrementActiveTaskSecond()
}
```

No elapsed-delta is ever consulted. Contrast the session's own clock in the
same function, which *is* wall-derived — `MonolithRepository.kt:1010`:
`val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(session)`
(monotonic `elapsedRealtime` difference). One function: session seconds are
measured, task seconds are counted in calls.

The call rate in the trunk is two drivers, not one:

1. The VM ticker — `MonolithViewModel.kt:330-342,364`: `startTickerLoop()`
   loops `repository.updateSessionProgress(current)` then `delay(1000L)`
   (≈1 call / wall second).
2. The shield service — `MonolithShieldService.kt:81`:
   `val updated = repository.updateSessionProgress(activeSession)`, in a loop
   ending `MonolithShieldService.kt:148`: `delay(2000L)` (≈0.5 calls / wall second).

Whenever a session is sealed — precisely when task time accrues — both loops
run. ≈1.5 calls/sec × 60 = **≈90 increments per 60 wall seconds**, each worth
a full booked second. A 25-minute task completed in real time stores roughly
37 minutes of "actual." Every downstream rendering of actual time — the Drawer
row's stored seconds and the session summary's minutes (U-5) — inherits the
1.5× inflation. This is not an approximation of the register-2 charge; it is
the same mechanism, still merged.

### CHARGE U-4 — The same tick-count disease drains the Safe Break: `-1` per call in the same function, so a 600-second break ends in ~400 wall seconds

`MonolithRepository.kt:1052-1053`:

```kotlin
if (session.isInSafeBreak) {
    val nextBreakRemaining = (session.safeBreakSecondsRemaining - 1).coerceAtLeast(0)
```

Same two callers as U-3 (≈1.5 calls/sec), same absence of any wall-delta.
The break's remaining seconds are a stored counter decremented per call, so
the 10-minute Safe Break (`SAFE_BREAK_DURATION_SECONDS = 600`,
`MonolithRepository.kt:1087` area) drains at ~1.5× real time. The VM drives
half of this rate itself (`MonolithViewModel.kt:342`). Filed here because it
is the identical lie in the identical function the VM calls every second:
a duration in this app is denominated in *calls*, not seconds.

### CHARGE U-5 — The session summary's "actual" is floored to 1 minute: a task completed with zero accrued seconds is written into the permanent record as "1m actual"

`MonolithRepository.kt:1455`:

```kotlin
val actualMins = (target.actualSecondsSpent / 60).coerceAtLeast(1)
```

consumed at `MonolithRepository.kt:1465`:

```kotlin
val entry = "${target.title} (${target.estimatedMinutes}m est / ${actualMins}m actual)"
```

`incrementActiveTaskSecond` only runs while a session is active (U-3), so a
task completed from the queue/checklist with no session running — or seconds
after arming — has `actualSecondsSpent = 0`. The record nonetheless states
1 minute of actual work. A fabricated minimum, written into
`completedTasksSummary` where it persists after the task row itself can be
cleared (see U-6/U-11). Zero is a possible truth; this code refuses to say it.

### CHARGE U-6 — Un-completing a task never retracts its session summary entry or count; re-completing writes it twice. The session record and the task rows diverge permanently

Un-complete branch, `MonolithRepository.kt:1441-1450`:

```kotlin
if (target.resolvedStatus == TaskQueueStatus.COMPLETED) {
    // Un-complete back to queue (or active if none active)
    val hasActive = allTasks.any { it.resolvedStatus == TaskQueueStatus.ACTIVE && it.id != taskId }
    val restoredStatus = if (hasActive) TaskQueueStatus.QUEUED else TaskQueueStatus.ACTIVE
    val restored = target.copy(
        status = restoredStatus.name,
        completedAtMillis = 0L
    )
    dao.updateTask(restored)
    return@withContext …
}
```

It touches only the task row — and not even all of it (`completedInSessionId`
is left stale). It never touches the session. But the completing branch had
already written, `MonolithRepository.kt:1463-1476`:

```kotlin
// Append to active session's receipt summary
if (currentSession != null) {
    val entry = "${target.title} (${target.estimatedMinutes}m est / ${actualMins}m actual)"
    val updatedSummary = if (currentSession.completedTasksSummary.isBlank()) {
        entry
    } else {
        "${currentSession.completedTasksSummary} • $entry"
    }
    currentSession = currentSession.copy(
        completedTasksSummary = updatedSummary,
        tasksCompletedCount = currentSession.tasksCompletedCount + 1
    )
    dao.updateSession(currentSession)
}
```

Consequence, by construction: complete → un-complete → re-complete one task
in one session and the session's stored summary lists it **twice** with
`tasksCompletedCount = 2`, while exactly one completed task row exists.
Un-complete alone leaves the session claiming a completion the task queue
denies. The VM offers this path directly (`toggleChecklistItem` →
`completeTaskAndAdvance`, the toggle semantics are the repository's own).
One fact — "this task was completed in this session" — two stored renderings,
and they disagree forever once the toggle is used.

### CHARGE U-7 — A ticker tick silently promotes the first QUEUED task to ACTIVE

Same accrual body, `MonolithRepository.kt:1349-1355` (quoted in U-3): the
fallback target is the first QUEUED task, and the write is
`activeTask.copy(status = TaskQueueStatus.ACTIVE.name, …)` — the tick itself
performs the promotion, with no user act, no intention update in the
repository, and no queue-order repair. The VM then ratifies the accident:
its `activeTask` flow (`MonolithViewModel.kt:187-190`) accepts the first
ACTIVE row, and its collector (`MonolithViewModel.kt:317-325`) overwrites
`_intentionText.value = task.title` from it. A timing side-effect of the
seconds-counter is functioning as a queue-policy decision.

---

## Q3 — Emergency break from the VM side: single transaction, or direct disarms?

### ACQUITTAL A-U4 — The valve's own path is lawful: the VM calls the repository's single `completeEmergencyBreak`, sounds only *after* the transaction commits, and stays silent on refusal

`MonolithViewModel.kt:975-991`:

```kotlin
fun completeEmergencyBreakFromValve() {
    viewModelScope.launch {
        when (repository.completeEmergencyBreak()) {
            BreakResult.Completed -> {
                soundEngine.playHarmonicSeal(arming = false)
                _isAlwaysOnDeskClockVisible.value = false
                _disarmChallenge.value = DisarmChallengeState(isVisible = false)
                MonolithShieldService.stopShield(appContext)
                _sevenDayStats.value = repository.compute7DayHistory()
            }
            BreakResult.RefusedNoToken, BreakResult.NoActiveSession -> {
                // Session stays sealed; the overlay remains open and
                // re-renders from the repository flow. Nothing sounds.
            }
        }
    }
}
```

The repository side is the single transaction the spec demands —
`MonolithRepository.kt:1224-1248`: spend-check, spend-row insert, and disarm
inside one `database.withTransaction { … }`, refusing at
`:1231-1233` (`if (dao.getSpendsForWeek(anchor).size >= 1) return@withTransaction BreakResult.RefusedNoToken`)
before any write. The overlay's wiring reaches only this and dismiss —
`MainActivity.kt:562-567`: `onCompleteDisarm = { viewModel.completeEmergencyBreakFromValve() }`,
`onDismissKeepFocusing = viewModel::dismissDisarmChallenge`, and
`dismissDisarmChallenge` (`MonolithViewModel.kt:947-949`) only flips
`isVisible` — it disarms nothing. From the overlay itself, there is no
back door. Acquitted.

### ACQUITTAL A-U5 (scoped) — `requestDisarmChallenge`'s direct disarm is a *completion*, not a break, and never touches the token

`MonolithViewModel.kt:930-938`: when a stopwatch session has passed the
20-minute floor, the function disarms via
`repository.disarmActiveSession(completed = true, outcomeReason = "Stopwatch Completed")`
instead of opening the overlay. That session *earned* its end; no spend is
owed and none is written. Not a token bypass. (Minor dead code in the same
function, noted for the record: `MonolithViewModel.kt:927` computes
`val remainingFloor = …` and nothing ever reads it.)

### CHARGE U-8 — `confirmDisarmSession` is an unauthenticated free disarm living in the VM: default reason "Ended Early", no grace check of its own — the only thing making it a *grace* cancel is a Deck rendering condition and a substring sniff in the repository

`MonolithViewModel.kt:951-963`:

```kotlin
fun confirmDisarmSession(reason: String = "Ended Early") {
    // TOKEN-SPEC §5: this path never touches the token again. The
    // grace cancel (§4.4) is free and unrecorded by the ledger;
    // a real break goes through completeEmergencyBreakFromValve().
    soundEngine.playHarmonicSeal(arming = false)
    _isAlwaysOnDeskClockVisible.value = false
    _disarmChallenge.value = DisarmChallengeState(isVisible = false)
    viewModelScope.launch {
        repository.disarmActiveSession(completed = false, outcomeReason = reason)
        MonolithShieldService.stopShield(appContext)
        _sevenDayStats.value = repository.compute7DayHistory()
    }
}
```

Facts, in order:

1. The function itself checks **nothing** — no elapsed time, no grace window,
   no session age, no reason validation. Called at minute 40 with its default
   argument, it ends the session early, spends no token, and never passes
   through `completeEmergencyBreak`'s transaction.
2. Its only wired caller in the trunk today is the grace chip —
   `MainActivity.kt:332-334` (`onCancelWithinGraceWindow = { viewModel.confirmDisarmSession("Accidental Start Grace Cancel") }`) —
   and the grace window itself exists only as a *rendering* condition in the
   Deck composable, `TactileDeckScreen.kt:507-508`:
   `val withinGraceWindow = activeSession != null && activeSession.elapsedSeconds <= 10`.
   Enforcement lives in whether a button is drawn, not in the VM or the
   repository. Any other present or future caller — or a stale composition —
   inherits a general-purpose early disarm.
3. The repository decides "grace" by substring, `MonolithRepository.kt:1277`:
   `val isGraceCancel = outcomeReason.contains("Grace Cancel", ignoreCase = true)` —
   and grace means the harshest erasure in the disarm path,
   `MonolithRepository.kt:1278`: `val bankedElapsed = if (isGraceCancel) 0 else max(verifiedElapsed, active.elapsedSeconds)`,
   plus no intercept log (`:1293` — `if (!completed && !isGraceCancel)`).
   Any reason string containing "grace cancel" zeroes a session's elapsed time
   and leaves no trace; any reason *not* containing it (e.g. the default
   "Ended Early") books a Partial Stamp + intercept log
   (`MonolithRepository.kt:1279-1305`) — again, token-free.

The comment at `:952-954` describes the *intended* use as if it were the
*enforced* behaviour. In the trunk, intent is all that enforces it.

---

## Q4 — The SPENT face: at zero tokens, is a long-pressable valve still composed?

### ACQUITTAL A-U6 — Structurally clean: at zero the valve is not composed at all; refusal does not depend on a gesture behaving

`AirlockAndDisarmOverlays.kt:627-637` (body text switches on the count) and
the composition gate at `AirlockAndDisarmOverlays.kt:637`:

```kotlin
if (tokensRemaining > 0) {
    AndroidView(
        factory = { ctx ->
            Emergency60sValveView(ctx).apply { … }
```

Below the gate there is no `else` branch composing any valve, pad, or
hold-target — at `tokensRemaining == 0` the overlay renders the SPENT text
(*"This week's break is already spent. Sessions end when their time does."*,
`:627-631`) and the KEEP FOCUSING chip only. There is no gesture present to
refuse. And if the callback were somehow invoked anyway, the repository
refuses a second time (A-U4, `MonolithRepository.kt:1231-1233`). Two layers,
both structural. Acquitted.

**The one hole in this acquittal is not in this file — it is U-1.** The gate
reads the VM's flow, whose initial value is `1`. On a spent week, from VM
creation until the ledger's first emission arrives, `tokensRemaining`
*is* `> 0` and the valve *is* composed — live, holdable, and (per A-U4) its
completion would be refused by the repository transaction, but the SPENT
face the law promises at zero is not what renders first. The composition
condition is honest; the number fed into it is not, for exactly the frames
U-1 charges.

---

## Q5 — Consistency between surfaces: one fact, two renderings

### CHARGE U-9 — `totalFocusedMinutes` is a VM-invented derivation with a fabricated floor (35) and a fabricated initial (75); the repository's own derivation of the same fact has neither

VM — `MonolithViewModel.kt:292-299`:

```kotlin
val totalFocusedMinutes: StateFlow<Int> = combine(
    sessionHistory,
    activeSession
) { history, active ->
    val completedSecs = history.filter { !it.isActive }.sumOf { it.elapsedSeconds }
    val activeSecs = active?.elapsedSeconds ?: 0
    ((completedSecs + activeSecs) / 60).coerceAtLeast(35)
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 75)
```

Repository — `MonolithRepository.kt:1330-1332` (inside `compute7DayHistory`,
the source of the VM's own `sevenDayStats`):

```kotlin
val totalSecs = daySessions.sumOf { it.elapsedSeconds }
val totalMins = totalSecs / 60
```

No floor. So the same phone, same database, same moment can show: **75**
focused minutes before the first emission, **35** on a genuinely zero-focus
history, and **0** in the 7-day derivation of the identical seconds. The VM
value is fanned out to at least four surfaces from `MainActivity.kt`
(`:272-273` drawer, `:305-306` Deck, `:414-415` Vault, `:535-537` streak
monument). A number that cannot fall below 35 is not a rendering of the
ledger; it is a claim the ledger never made.

### CHARGE U-10 — `totalInterceptions` takes the *maximum* of two different counters and seeds it at 6; the repository derives interceptions a third way

VM — `MonolithViewModel.kt:301-304`:

```kotlin
val totalInterceptions: StateFlow<Int> = interceptLogs.combine(apps) { logs, appList ->
    val fromApps = appList.sumOf { it.interceptCount }
    maxOf(logs.size, fromApps)
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)
```

Three numbers for one fact coexist in the trunk:

1. `logs.size` — intercept *log rows*;
2. `sumOf { it.interceptCount }` — per-app counters (incremented alongside the
   log in `recordIntercept`, `MonolithRepository.kt:1119-1130` area —
   `dao.incrementInterceptCount(packageName)` + `dao.insertInterceptLog(…)` —
   but also seeded with nonzero values and written by session disarms, so the
   two drift independently);
3. the repository's 7-day derivation, `MonolithRepository.kt:1333`:
   `val intercepts = daySessions.sumOf { it.phantomReachesBlocked }` — session
   phantom counts, a third store entirely.

The VM publishes `maxOf(1, 2)` — a value that is *neither* source, guaranteed
to disagree with one of them whenever they differ, pre-seeded at **6** before
any data arrives. It is rendered by the drawer, Deck, and Vault from the same
`MainActivity.kt` fan-out as U-9 (`:273`, `:306`, `:415`, `:537`).

### CHARGE U-11 — Completed-task counts have two homes that provably diverge, and `completedTasksSummary` — the repository's own per-session record — is never exposed by the VM at all

- VM home: `MonolithViewModel.kt:182-184` —
  `val completedTasks … list.filter { it.resolvedStatus == TaskQueueStatus.COMPLETED }`
  — rendered as `completedTasks.size` on the Deck (`MainActivity.kt:301`,
  `completedTasksCount = completedTasks.size`) and the Sequencer header
  ("DONE n", from the same list).
- Session home: `FocusSessionEntity.tasksCompletedCount`
  (`MonolithModels.kt:273`), rendered by the Vault per session —
  `CurioVaultScreen.kt:257`: `monoNumber = session.tasksCompletedCount.toString()`.

They are incremented by different rules. The session count moves only inside
`completeTaskAndAdvance` *and only when a session is active*
(`MonolithRepository.kt:1464-1475`, quoted in U-6); the VM list moves on
every completion, session or not. They diverge on at least four trunk paths:

1. **No-session completion** — task row COMPLETED (`completedInSessionId = 0`,
   `MonolithRepository.kt:1459`), VM count +1, no session count moves.
2. **Un-complete** — VM count −1; session count and summary unchanged (U-6).
3. **Clear completed** — `MonolithViewModel.kt` `clearCompletedTasks()`
   delegates to `repository.clearCompletedTasks()`, which deletes the task
   rows; VM count → 0, every session's stored count and summary keep
   claiming their completions.
4. **Re-complete after un-complete** — session count +1 *again* for the same
   task (U-6); VM count +1 once.

And the summary string itself: a grep of the VM for `completedTasksSummary`
returns **0 hits**. The repository maintains it as the session's receipt
record (`MonolithRepository.kt:1471-1472`), the VM surfaces neither it nor
any derivation of it — so the one per-session rendering that exists (the
Vault's bare count) can never be checked against the string it supposedly
counts, from anything the VM exposes. One fact, two counters, one invisible
ledger string. This is the Q5 question's exact case, and it fails.

### CHARGE U-12 — `totalQueueMinutes` floors the queue at the *dial* value, read as a stale snapshot inside a `tasks` map

`MonolithViewModel.kt:191-195`:

```kotlin
val totalQueueMinutes: StateFlow<Int> = tasks.map { list ->
    list.filter {
        it.resolvedStatus == TaskQueueStatus.ACTIVE || it.resolvedStatus == TaskQueueStatus.QUEUED
    }.sumOf { it.estimatedMinutes }.coerceAtLeast(_dialMinutes.value)
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50)
```

Two faults in four lines. (a) An empty queue renders as the dial setting —
50 minutes of queued work that does not exist (initial value also 50).
(b) `_dialMinutes.value` is read *inside* a `tasks.map`: the map re-runs only
when the task list emits, so turning the dial does not re-derive the total —
the floor is whatever the dial happened to be at the last task change. A
computed number computed from a photograph of another flow.

### CHARGE U-13 — The checklist invents three tasks whenever the real queue is empty

`MonolithViewModel.kt:197-206` (map branch) and the identical `stateIn`
initial at `:214-222`:

```kotlin
if (list.isEmpty()) {
    listOf(
        StudyChecklistItem(1, "Finish Chapter 4 Problem Set (#1–6)", false),
        StudyChecklistItem(2, "Synthesize Lecture 9 Architecture Notes", false),
        StudyChecklistItem(3, "Debug Dynamic Programming Memo Table", false)
    )
}
```

An empty task table — the truth — renders as three named, checkable tasks.
Toggling one routes into `completeTaskAndAdvance(id)` with ids 1–3 that may
belong to other real tasks or none. Fabricated content in the VM's own
derivation layer, of the same family as U-9/U-10's fabricated numbers.

---

## Q6 — Anything still assuming DB v7 (counter-world) semantics?

### ACQUITTAL A-U7 — Textually clean: no v7 reference, no migration-era TODO, no fallback, no "counter" language anywhere in the VM

A grep of all 1123 lines of `MonolithViewModel.kt` for `TODO`, `FIXME`,
`fallback`, `v7`, `version 7`, `counter` returns nothing. The trunk's database
is v8 with the ledger migration (`MonolithDatabase.kt:24` — `version = 8`;
`:41-52` — `MIGRATION_7_8` creating `emergency_break_spends`), and the VM's
comments describe the ledger world — correctly — as the current one
(`MonolithViewModel.kt:142-149`, `:952-954`, `:965-974`).

**But the counter world survives functionally, in initial values** — the v7
app's habit of *asserting* numbers before measuring them, carried into v8
derivations:

- the token's initial `1` (U-1) is the deleted `MutableStateFlow(1)`'s value,
  verbatim, in its replacement's constructor;
- `totalFocusedMinutes` initial `75` + floor `35` (U-9);
- `totalInterceptions` initial `6` (U-10);
- `totalQueueMinutes` initial `50` + dial floor (U-12);
- the checklist's three invented tasks (U-13).

No comment admits any of them. The migration moved the storage; the VM kept
the epistemology — *print a plausible number first, let truth catch up* —
which is precisely the assumption DB v7 ran on.

---

## Summary for the merge

Charges: U-1 (token seed 1), U-2 (Deck default 1), U-3 (tick-count task
accrual, ~90/60, dual driver), U-4 (tick-count Safe Break drain), U-5 (1-minute
actual floor), U-6 (un-complete orphans summary + count), U-7 (tick promotes
QUEUED→ACTIVE), U-8 (unauthenticated `confirmDisarmSession`), U-9 (focused
minutes floor/initial), U-10 (interceptions maxOf + seed 6), U-11 (two
completed-count homes; summary unexposed), U-12 (queue-minutes dial floor),
U-13 (invented checklist tasks).
Acquittals: A-U1 (no mutable VM token state; single repo derivation),
A-U2 (no seal sound on task completion), A-U3 (completed-row shape, partial),
A-U4 (valve path = single transaction, sound after commit), A-U5 (stopwatch
direct disarm is a completion, scoped), A-U6 (SPENT face composes no valve),
A-U7 (no v7 text/TODO/fallback in VM; DB is v8).

**Worst finding, one plain sentence:** The app still books task time by counting function calls instead of measuring time — two tickers each add a second per call, so sixty real seconds of work are stored as roughly ninety, and every "actual" the Drawer will ever show is inflated by half again from the moment it is written.

— Una, Critics seat 15 (consistency)
