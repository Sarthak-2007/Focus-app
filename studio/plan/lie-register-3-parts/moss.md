# LIE REGISTER 3 — MOSS (Critics, seat 5: motion)

Ground read, merged trunk only: `FocusSequencerScreen.kt` (632 lines, whole file),
`MonolithViewModel.kt` `completeTaskAndAdvance` (L587–601) and what it touches —
`MonolithRepository.kt` `completeTaskAndAdvance` (L1437–1500), the VM's second
caller `toggleChecklistItem` (VM L521–523), and its live wiring
(`MainActivity.kt` L317, L319). Lane reports were not used as evidence.
Inbox checked: one letter (Latch, flow-key-trap) — already in `read/`, unrelated
to this audit, no bearing on any charge below.

The design as ruled, restated for the record: dashed perforation tops the leaf;
the tear is a TAP on "DONE — TEAR"; stub drops 10dp and fades over 180ms, dashes
halve over 260ms, and ONLY THEN the existing completion path runs; storage
untouched until completion; sealed = solid overprint, label absent, no gesture;
held blank = perforation + ruled blank, no tap target; the tear's pulse is the
completion's only sound — the ViewModel's old seal sound was removed by ruling.

---

## CHARGES

### MO-1 (S1) — There is no latch where it matters: the completion path is a TOGGLE, and a second fire resurrects the task

The leaf has a latch (acquitted at MO-A1). Nothing below the leaf does.

`MonolithViewModel.kt:587-601` — every call launches the write, unconditionally:

```kotlin
fun completeTaskAndAdvance(taskId: Int) {
    ...
    viewModelScope.launch {
        val nextActive = repository.completeTaskAndAdvance(taskId)
```

No in-flight set, no already-completed check, no session/sealed check. And what
it calls is not an idempotent "complete" — `MonolithRepository.kt:1441-1450`:

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
```

So the feared double-fire does not produce two clean completions. It produces
this: first fire completes and appends to the session record
(`MonolithRepository.kt:1464-1475` — `completedTasksSummary` gains an entry,
`tasksCompletedCount + 1`); second fire silently un-completes the task back to
QUEUED/ACTIVE and clears `completedAtMillis` — and the un-complete branch never
touches the session, so the record keeps the entry and the count for a task that
is now unfinished. The leaf reappears in the queue with a fresh `tearing=false`;
the user tears it again; the repository appends the same title to the summary a
second time and increments the count again. Two Drawer/record entries and a
count of 2 for one task, reached by nothing more exotic than the completion
firing twice. Exactly-once is load-bearing for record integrity, and the only
thing carrying it is a `remember {}` in a recyclable list row (see MO-3, MO-6).

### MO-2 (S1) — Order lie: completion fires at 180ms; the ruled order requires it after 260ms

The ruled sequence is conjunctive: drop/fade over 180ms AND dashes halve over
260ms, "and ONLY THEN does the existing completion path run." The file's own
KDoc repeats it (`FocusSequencerScreen.kt:325-327`): "the torn edge's dashes
halve over 260ms curve B) and only then does the existing completion path run."

The code (`FocusSequencerScreen.kt:366-371`):

```kotlin
if (tearing) {
    LaunchedEffect(task.id) {
        delay(MonolithCurves.DURATION_180.toLong())
        onCompleteTask(task.id)
    }
}
```

The delay is 180ms. The dash settle it is supposed to wait for is the 260ms
animation at L361-365 (`specB<Float>(MonolithCurves.DURATION_260)`, target
`0.5f`). Completion therefore runs a full 80ms before the dashes have halved;
the task leaves `orderedQueue`, the leaf — perforation canvas included — leaves
composition, and the dash animation is disposed mid-settle. The dashes never
reach 1.5dp on any tear, ever. The last fifth of the ruled motion is dead code
in practice: drawn, animated, and amputated on every single completion.

### MO-3 (S1) — The tear is losable: the pending completion lives in composition, and composition is disposable

The only record that a tear is in flight is (`FocusSequencerScreen.kt:355`):

```kotlin
var tearing by remember(task.id) { mutableStateOf(false) }
```

`remember`, not `rememberSaveable`, not ViewModel state — and the completion
itself is a composition-scoped `LaunchedEffect` (L366-371, quoted at MO-2). If
the leaf leaves composition during the 180ms window, the effect is cancelled,
`delay` never returns, `onCompleteTask` never fires — and `tearing` dies with
the composition, so nothing anywhere knows a tear was attempted. The pulse,
however, already played at tap time (L377). Concrete exits from composition,
all reachable inside 180ms:

- **LazyColumn recycling / scroll-away:** the leaf lives in
  `itemsIndexed(orderedQueue, …)` (L300) in a `LazyColumn` (L124ff); fling the
  list and the tearing row is disposed.
- **The leaf's own CUE/START tap:** still clickable during the tear (MO-4) and
  calls `onNavigateToDeck()` (L515-518) — navigating away disposes this screen,
  and with it the pending completion.
- **Configuration change / tab switch:** same disposal, same silent loss.

The failure mode is the worst kind for a motion system: the user sees the stub
fall, hears the completion pulse, and the task is simply still there when they
scroll back — re-armed, `tearing=false`, ready to be torn (and pulsed) again.
Animation started, sound spent, act never happened, no trace.

### MO-4 (S2) — The tearing leaf's other controls stay live, and they write storage before completion runs

Only the tear label is gated during the fall
(`Modifier.clickable(enabled = !tearing) { tear() }`, L496). Every other
control on the same leaf keeps a plain, ungated `clickable` for the entire
180ms window:

- sub-step rows: `.clickable { onToggleSubStep(task.id, stepIdx) }` — L454
- `SPLIT`: `.clickable { onAutoSplitTask(task.id) }` — L506
- `START …m` / `CUE`: `.clickable { onCueTaskToFocus(task.id); onNavigateToDeck() }` — L515-518
- `DEL`: `.clickable { onDeleteTask(task.id) }` — L526

Consequences, in the design's own terms: (a) the ruled "storage is untouched
until completion" holds for the tear call itself (MO-A4) but not for the leaf —
a sub-step toggle or a split persists a write to the very task that is
mid-tear, before its completion runs; (b) `DEL` inside the window deletes the
task, after which the delayed `onCompleteTask(task.id)` still fires into the
ViewModel and repository (which returns null at
`MonolithRepository.kt:1439` — benign for storage, but the tear's animation
and pulse have testified to a completion of a task the user just deleted);
(c) `CUE` inside the window is the MO-3 kill path. A leaf that is detaching
at its perforation must be inert below the tear; this one is fully open for
business while it falls.

### MO-5 (S2) — Sealed state: the tear is truly gone (MO-A5), but the sealed leaf is not gestureless and not mute

While `isSessionActive` is true, the tear label is absent and the perforation
is overprinted solid — acquitted in full at MO-A5. But no other control on the
leaf consults `isSessionActive` at all. The clickables at L454 (sub-steps),
L506 (SPLIT), L515-518 (CUE/START) and L526 (DEL) are composed and enabled
identically in sealed and unsealed states: a queued task can be deleted, split,
cued/promoted, and its sub-steps toggled in the middle of a sealed session,
from the very leaf whose perforation has just been sealed shut to say the pad
is closed. Worse for the sound law: two of those paths reach seal-voiced
ViewModel functions — `MonolithViewModel.kt:721-722`
(`autoSplitTaskIntoMicroSteps` → `soundEngine.playHarmonicSeal(arming = true)`)
and `MonolithViewModel.kt:739-740` (`cueTaskAndNavigateToFocus` →
`soundEngine.playHarmonicSeal(arming = true)`). Those are not the completion
path (MO-A6 stands), but they mean the sealed leaf can still speak in the
seal's voice while its own perforation claims nothing can be torn or moved.
If "no gesture" in the ruling is read tear-only, this is a HOLD leak; read as
written — no gesture — it is a direct violation. Filed as a charge either way;
Hale to rule the reading.

### MO-6 (S2) — The completion path carries none of the ruled guards, and a second, tearless caller is wired live

Every protection the design rules — tear first, pulse as sole sound, sealed =
no completion, exactly-once latch — lives exclusively in `TaskLeafItem`.
`completeTaskAndAdvance` itself (`MonolithViewModel.kt:587-601`, quoted at
MO-1) checks nothing: not sealed, not already-completed, not in-flight. And it
has another caller inside the ViewModel (`MonolithViewModel.kt:521-523`):

```kotlin
fun toggleChecklistItem(id: Int) {
    completeTaskAndAdvance(id)
}
```

wired live to the Deck at `MainActivity.kt:317`
(`onToggleChecklistItem = viewModel::toggleChecklistItem`), alongside a direct
Deck binding of the raw function at `MainActivity.kt:319`
(`onCompleteActiveTask = viewModel::completeTaskAndAdvance`). Through those
doors a task completes with no perforation, no 180/260ms motion, no pulse, no
sealed check — and, per MO-1, a second tap on a completed item un-completes
it. The ruling governs one screen's label; the act it governs is reachable
without the label. (Whether the Deck's own surfaces gate these callbacks is
outside my assigned ground; the charge is that the shared path they land on
enforces nothing, so any gating is per-caller luck.)

### MO-7 (S3) — The pulse is counted per attempt, not per completion

`FocusSequencerScreen.kt:373-379`:

```kotlin
fun tear() {
    if (!tearing && !isSessionActive) {
        tearing = true
        // The tear's one sound, single owner (Counterfoil §2.4).
        soundEngine?.playCompletionPulse()
    }
}
```

Within one composition this is exactly once (MO-A6). But the pulse fires at
t=0, at the start of the fall — 180ms before the act it is named for — and
because the pending completion is losable (MO-3), a pulse can sound for a
completion that never happens; when the user re-tears the resurrected leaf,
the pulse sounds a second time for what is still, in storage, a single
completion. The sound law as ruled — "the completion sound is the tear's
pulse alone" — is honoured in ownership and violated in accounting: pulses
count tear attempts, completions count something else, and MO-1/MO-3 are the
two places they diverge.

---

## ACQUITTALS

### MO-A1 — Double-tap within one composition: TRIPLE-GATED, clean (Q1, in part)

State latch `FocusSequencerScreen.kt:355`
(`var tearing by remember(task.id) { mutableStateOf(false) }`); guard in the
act itself, L374 (`if (!tearing && !isSessionActive) { tearing = true; … }`);
and the input itself disabled, L496
(`Modifier.clickable(enabled = !tearing) { tear() }`). A second tap in the
fall window reaches a disabled clickable whose handler would refuse anyway.
Recomposition alone cannot re-fire completion: the effect at L367 is keyed
on `task.id` only, is entered once when `tearing` flips, and `tearing` never
flips back inside a composition. The lie is not here — it is that this latch
is the *only* latch (MO-1, MO-6).

### MO-A2 — Motion numbers as drawn: exact (the ruled values are really in the code)

Drop and fade, `FocusSequencerScreen.kt:392-395`:
`alpha = 1f - tearProgress`, `translationY = tearProgress * 10.dp.toPx()` —
10dp, full fade, driven by L356-360
(`animateFloatAsState(…, MonolithCurves.specA<Float>(MonolithCurves.DURATION_180))`).
Dash settle targets half, L361-365 (targetValue `0.5f`, `specB`, `DURATION_260`),
and is drawn as halved dashes with the gap untouched, L580-581
(`val dash = 3.dp.toPx() * dashScale`, `val gap = 3.dp.toPx()`) — 3dp → 1.5dp,
exactly "dashes halve." The perforation tops the leaf: `PerforationRow` is the
first element of the leaf's column (L399). Every number in the ruling exists
and is correct — MO-2 is an ordering lie, not a numbers lie.

### MO-A3 — Completion is keyed by identity, never by position (Q2, in part)

`FocusSequencerScreen.kt:300`: `itemsIndexed(orderedQueue, key = { _, t -> t.id })`.
The effect captures the identity, L367-369: `LaunchedEffect(task.id) { …;
onCompleteTask(task.id) }`. `index` is used only for print — the `#%02d` plate
(L430) and `leavesBehind` (L304) — never for the completion call. A reorder
during the fall cannot complete the wrong task, and the queue cannot show the
same task on two leaves: the queue builder de-duplicates the active task at
L105-110 (`addAll(queuedTasks.filter { it.id != activeTask?.id })`).

### MO-A4 — Storage on the tear path itself: untouched until the delay expires (Q3, narrowly)

`tear()` (L373-379) writes only local composition state and plays the pulse —
no DAO, repository, or ViewModel call. The first storage touch on this path is
inside `MonolithViewModel.kt:592-593`, reachable only after the screen's delay
via `onCompleteTask`; the ViewModel's own state mutations
(`_intentionText.value`, `_dialMinutes.value`, L594-598) happen after the
repository returns. Nothing on the tear path persists early. (The leaf's
*other* controls writing during the window are charged separately at MO-4;
this acquittal covers the tear's own chain only.)

### MO-A5 — Sealed tear path: no gesture, no ghost semantics (Q4, tear path)

The label is not disabled — it is not composed. `FocusSequencerScreen.kt:488`:
`if (!isSessionActive) { Text(text = "DONE — TEAR", …, Modifier.clickable(…)) }`.
A sealed leaf contains no tear semantics node for a screen reader or keyboard
to land on; there is no `enabled = false` remnant anywhere on this path. The
act double-checks the seal even if invoked, L374 (`!isSessionActive`). The
mark matches the claim: `PerforationRow(sealed = isSessionActive)` (L399)
overprints the dashes with a solid rule when sealed, L592-599
(`if (sealed) { drawLine(…, start = Offset(0f, y), end = Offset(size.width, y), …) }`).

### MO-A6 — The ViewModel's seal sound on the completion path: REMOVED, as ruled (Q5)

`completeTaskAndAdvance` (`MonolithViewModel.kt:587-601`) contains no sound
call of any kind — no `playHarmonicSeal`, no `playMechanicalLatch`, no
`playCompletionPulse` — and carries the ruling in its own comment, L588-591:
"No sound here (merge ruling on Linea's flag): the seal is the SESSION sound
and was misspent on a task act… The tear's completion pulse in the view is the
sole owner of this act." A tree-wide grep for `playCompletionPulse` returns
exactly one call site on this act, `FocusSequencerScreen.kt:377` (inside the
latched `tear()`, MO-A1). For the record, the grep's other ViewModel hit,
`MonolithViewModel.kt:159`, sits in `triggerRatchetWarning` — a ratchet
banner, not the completion path — and is expressly NOT charged.

### MO-A7 — Held blank: no tap target exists (Q6, in part)

The empty-queue branch, `FocusSequencerScreen.kt:273-298`, composes a
`Column` (modifiers: `fillMaxWidth`, `padding`, `testTag("task_blank_leaf")`
only, L276-279) holding `PerforationRow(sealed = isSessionActive)` (L282) and
a 56dp `Box` (L283-287) whose only content is inert `Text`. No `clickable`,
no chip, no button anywhere in the branch; the blank cannot be tapped,
focused as an action, or torn. Perforation present, address preserved, hands
off — as ruled.

### MO-A8 — The label voice: character-exact (Q6, in part)

`FocusSequencerScreen.kt:490`: `text = "DONE — TEAR"` — the ruled words, the
em dash, the spacing, at the ruled 15sp plate face (L491-495:
`AnonymousProFamily`, `FontWeight.Bold`, `15.sp`). The blank's printed line is
likewise plain and honest (L290: `"NOTHING QUEUED"`). No charge on the words.

---

## Worst finding, one sentence

The completion the tear calls is secretly a toggle with no latch of its own, so if it ever fires twice — and the only guard is a `remember {}` inside a recyclable list row — the second fire resurrects the torn task while the session record keeps its entry and its count, corrupting the very record the tear was built to honour.

— Moss, Critics · seat 5 (motion)
