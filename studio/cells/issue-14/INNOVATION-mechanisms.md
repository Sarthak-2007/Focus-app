# INNOVATION — THE FOUR RULED MECHANISMS AS BUILDABLE SPECS
## Issue #14 cell · Funke (innovator seat) · for Pforte's merge

Source of law: `studio/plan/AUDIT-setup-sheet.md` §3 + §6 items
7–10. Code read whole before writing: `ReceptionSheet.kt`,
`data/SetupStore.kt`, `service/WatchStore.kt`, `MainActivity.kt`
(`MonolithRootApp`), `ui/components/ParkDrawer.kt`,
`ui/components/GuardLine.kt`, `MonolithCustomComponents.kt`
(`refusalSentenceFor` / `RefusalSentenceSlot`),
`screens/TactileDeckScreen.kt` (the slot's host),
`ui/MonolithViewModel.kt` (`navigateTo`, `guardLinePrint`).

Paper only. Nobody compiles. Every state the builder needs is
named below — if a case is not in a table here, it does not get
invented at the keyboard; it comes back to this cell.

Conventions used throughout:

- All four mechanisms live in the EXISTING prefs file
  `monolith_setup` owned by `SetupStore`. No Room change, no DB
  version move — same argument as the stamp itself (chrome, not
  session data).
- Every `SetupStore` write is ONE `edit()` transaction ending in
  `commit()` (G-02 standard: these are facts the app has already
  acted on; `apply()`'s async window is forbidden).
- Door bit indices, everywhere a mask appears, are the index in
  `WatchItemId.COUNTED`: 0=ACCESSIBILITY, 1=USAGE_ACCESS,
  2=DEVICE_ADMIN, 3=NOTIFICATION_LISTENER. OVERLAY and BATTERY
  are never in any mask — the ledger is a counted-four fact.
- Clocks: mono = `SystemClock.elapsedRealtime()`, wall =
  `System.currentTimeMillis()`. `SetupStore` reads both clocks
  ITSELF inside each write, so a (mono, wall) pair can never be
  assembled from two different instants by a caller.

---

## §1 · THE RETURN CHIT (§6.7) — Proposal 1, SHIPS

### 1.1 What dies

`ReceptionSheet.kt`'s standing verdict: the `remember` pair
(`fixPressed` / `fixPressedItem`) judged by a
`LaunchedEffect(returnTick)` that sleeps a fixed 800 ms, reads
whatever snapshot exists, pays only in audio, and dies with the
process (audit F-1/F-3). The 800 ms delay, the snapshot-guess,
and the sound-only payment are deleted. The `fixPressed` DIP
(the key's 1 dp press visual) stays — it is dress, not verdict —
but it is released by the chit leaving DEPARTED (§1.5), never by
a timer.

### 1.2 SetupStore fields (all in `monolith_setup`)

| Key | Type | Default | Meaning |
|---|---|---|---|
| `chit_state` | String (enum name) | absent = no chit | `ReturnChitState` name |
| `chit_door` | String | — | `WatchItemId` name of the visited door |
| `chit_departure_print` | String | — | `PermPrint` name at the FIX tap |
| `chit_departure_mono` | Long | 0 | mono at departure |
| `chit_departure_wall` | Long | 0 | wall at departure |
| `chit_verdict_mono` | Long | 0 | mono of the judging measurement |
| `chit_verdict_wall` | Long | 0 | wall at resolution |
| `chit_next_door` | String | `""` = none | next MISSING door at resolution |

Types in `SetupStore.kt`:

```kotlin
enum class ReturnChitState {
    DEPARTED, RETURNED_REPAIRED, RETURNED_UNCHANGED,
    RETURNED_UNREAD, RETURNED_DARK, SPENT
}

data class ReturnChit(
    val door: WatchItemId,
    val departurePrint: PermPrint,
    val departureMono: Long,
    val departureWall: Long,
    val state: ReturnChitState,
    val verdictMono: Long = 0L,
    val verdictWall: Long = 0L,
    val nextDoor: WatchItemId? = null
)
```

### 1.3 SetupStore functions (exact signatures)

```kotlin
/** At the FIX tap. One edit, commit(). Overwrites any prior
 *  chit whole — one excursion at a time; the latest wins. */
fun writeDepartureChit(door: WatchItemId, departurePrint: PermPrint)

/** Null when chit_state is absent or unparseable. */
fun chit(): ReturnChit?

/** DEPARTED -> a RETURNED_* state. One edit, commit().
 *  No-op unless the stored state is DEPARTED (a late second
 *  resolver can never rewrite a printed verdict). */
fun resolveChit(state: ReturnChitState, nextDoor: WatchItemId?)

/** RETURNED_* -> SPENT. One edit, commit(). No-op from
 *  DEPARTED (an unjudged excursion is never silently spent)
 *  and from SPENT. */
fun spendChit()

/** Clears all eight chit keys. One edit, commit(). */
fun clearChit()
```

`resolveChit` stamps `chit_verdict_mono/wall` from its own
clock reads at the call.


### 1.4 The verdict — pure function, the only judge

In `SetupStore.kt` (pure, unit-testable, no Android beyond the
passed values):

```kotlin
fun chitVerdictFor(
    chit: ReturnChit,
    measurement: WatchMeasurement?,
    verdictMono: Long
): ReturnChitState?
```

Return contract — read top to bottom, first match wins:

| # | Condition | Returns |
|---|---|---|
| 1 | `verdictMono < chit.departureMono` (mono went backwards: the phone rebooted in Settings; departure and return are on different boot clocks and no comparison is lawful) | `RETURNED_DARK` |
| 2 | `measurement == null`, or `measurement.measuredMono <= chit.departureMono` (nothing measured AFTER departure exists yet — a pre-departure stamp, however fresh it looks, judges nothing) | `null` = keep waiting |
| 3 | `measurement.perm == null` | `RETURNED_UNREAD` |
| 4 | print at verdict instant is GRANTED (`permPrintAt` semantics: GRANTED and age ≤ `STALE_AFTER_MS`) | `RETURNED_REPAIRED` |
| 5 | print is MISSING | `RETURNED_UNCHANGED` |
| 6 | print is STALE (a GRANTED landed after departure but its proof already aged out before the verdict ran) | `RETURNED_UNREAD` |

DARK vs UNREAD, decided once so the builder never re-decides:
UNREAD = we looked and the proof is absent or aged. DARK = we
cannot lawfully look (boot-clock discontinuity, row 1). A
deadline expiry (§1.5) resolves to UNREAD, or to DARK when row
1's condition holds at expiry.

Note, so nobody "fixes" it later: in this merge the FIX key
exists only on MISSING cards, so `departurePrint` is MISSING
in every reachable departure. The field stays total over
`PermPrint` — the function, the line composer, and the tests
handle all four — because a CHECK-departure or a STALE-card
FIX in a later lane must not need a schema change.

### 1.5 Who calls what, where

**Departure — `ReceptionSheet.kt`, `ReceptionDoorCard`'s FIX
key.** The card already holds the live `print`. New sheet
parameter `onDeparture: (WatchItemId, PermPrint) -> Unit`,
wired in the card's `onFix` BEFORE `onFixClick(item)`:

```kotlin
onFix = {
    fixPressed = true
    fixPressedItem = item
    onDeparture(item, print)   // chit first, pane second
    onFixClick(item)
}
```

`MainActivity.MonolithRootApp` implements it as
`onDeparture = { door, print -> setupStore.writeDepartureChit(door, print) }`.
Order matters: the pane intent fires second, so the chit is
committed before Android can take the process.

CHECK writes NO chit. CHECK never leaves the app; there is no
departure to judge.

**Resolution — host-side, `MainActivity.kt`.** The sheet never
judges again. The host already owns the return machinery
(ON_RESUME observer → `runWatchSweep()` + `parkReturnTick++`).
Add, beside the reception host state:

```kotlin
var chitUi by remember { mutableStateOf(setupStore.chit()) }

LaunchedEffect(parkReturnTick, watchSnapshot) {
    val c = setupStore.chit() ?: return@LaunchedEffect
    if (c.state != ReturnChitState.DEPARTED) { chitUi = c; return@LaunchedEffect }
    val verdict = chitVerdictFor(
        c, watchSnapshot?.measurement(c.door),
        SystemClock.elapsedRealtime()
    )
    if (verdict != null) {
        val next = watchSnapshot?.let { snap ->
            WatchItemId.COUNTED.firstOrNull { other ->
                other != c.door &&
                WatchStore.permPrintAt(snap, other, SystemClock.elapsedRealtime()) == PermPrint.MISSING
            }
        }
        setupStore.resolveChit(verdict, next)
        if (verdict == ReturnChitState.RETURNED_REPAIRED) {
            soundEngine.playFirmClick()  // Letzt A-5: click is
            // the second channel, REPAIRED only, never a voice
        }
        chitUi = setupStore.chit()
    }
}
```

Deadline: a second effect gives the sweep a bounded window —
the fixed 800 ms race becomes a named deadline, not a guess:

```kotlin
LaunchedEffect(parkReturnTick) {
    if (parkReturnTick <= 0) return@LaunchedEffect
    val c = setupStore.chit() ?: return@LaunchedEffect
    if (c.state != ReturnChitState.DEPARTED) return@LaunchedEffect
    delay(3_000L)
    val still = setupStore.chit() ?: return@LaunchedEffect
    if (still.state == ReturnChitState.DEPARTED) {
        val nowMono = SystemClock.elapsedRealtime()
        setupStore.resolveChit(
            if (nowMono < still.departureMono) ReturnChitState.RETURNED_DARK
            else ReturnChitState.RETURNED_UNREAD, null)
        chitUi = setupStore.chit()
    }
}
```

Process death in Settings is the chit's whole reason to
exist: on cold start `chitUi` initialises from
`setupStore.chit()`, the state is still DEPARTED, the first
Deck-entry sweep lands a post-departure measurement, and the
effect above resolves it exactly as if no death occurred. If
the phone rebooted, row 1 of the table resolves DARK.

**Spending.** The line prints for ONE printing of the sheet.
Host: `LaunchedEffect(receptionOpen) { if (!receptionOpen) { setupStore.spendChit(); chitUi = setupStore.chit() } }`.
DONE spends it the same way (DONE closes the sheet).
A new `writeDepartureChit` overwrites a SPENT chit whole.
PARK reprints of the sheet therefore never re-print an old
verdict — SPENT is the tombstone that makes that true.

**Dip release.** In the sheet, replace the deleted 800 ms
effect with: `LaunchedEffect(chit?.state) { if (chit != null && chit.state != DEPARTED && chit.door == fixPressedItem) { fixPressed = false; fixPressedItem = null } }`, plus the existing
reset when `visible` goes false. New sheet parameter:
`chit: ReturnChit?`.


### 1.6 The printed line — one line, in the visited card only

Pure composer in `ReceptionSheet.kt`:

```kotlin
fun chitLineFor(chit: ReturnChit): String?
// null for DEPARTED and SPENT — nothing prints until judged,
// and nothing re-prints after spending.
```

Word for a print is `receptionDoorWord(print)` (SET / NOT SET /
UNPROVED / NOT CHECKED). Lines, verbatim:

| State | Line |
|---|---|
| RETURNED_REPAIRED | `WENT OUT <depWord> · CAME BACK SET.` + next suffix |
| RETURNED_UNCHANGED | `WENT OUT <depWord> · CAME BACK NOT SET.` + next suffix |
| RETURNED_UNREAD | `WENT OUT <depWord> · CAME BACK NOT CHECKED.` (no suffix) |
| RETURNED_DARK | `WENT OUT <depWord> · RETURN NOT READ.` (no suffix) |

Next suffix (REPAIRED and UNCHANGED only — the two states
where a next step is a known fact):
`nextDoor != null` → ` · NEXT — <nextDoor.boardName>`;
`nextDoor == null` → ` · ALL FOUR SET.` — printed only when
the verdict snapshot in fact showed no other MISSING door
(the resolver above guarantees this by construction).

Placement: inside `ReceptionDoorCard`, under the job line,
above the prepare line, only when `chit?.door == item` and
`chitLineFor(chit) != null`. Style: `PillBadgeTextStyle` at
`onSurfaceVariant`, EXCEPT the return SET, which is rendered
as an annotated span — `FontWeight.Bold`, colour
`scheme.onBackground` (ink) — for that printing. The same
printing exception applies to the card header's own door
word: while an unspent RETURNED_REPAIRED chit names this
door, the header word SET takes Bold + ink instead of its
standing dim treatment. Both revert the moment the chit is
SPENT. No other card changes in any state. Sound: none from
the line itself (the host's firm click at resolution is the
only audio, §1.5).

Auto-scroll: the sheet's scroll state becomes named —
`val scrollState = rememberScrollState()` passed to the
existing `verticalScroll`. Each door card carries a
`BringIntoViewRequester` keyed by item
(`remember { WatchItemId.COUNTED.associateWith { BringIntoViewRequester() } }`
in the sheet, the matching one handed to its card and
attached via `Modifier.bringIntoViewRequester(...)`).
Effect in the sheet:

```kotlin
LaunchedEffect(chit?.state, settled) {
    val c = chit ?: return@LaunchedEffect
    if (c.state in RETURNED_STATES && settled) {
        requesters[c.door]?.bringIntoView()
    }
}
```

(`RETURNED_STATES` = the four RETURNED_* values.) If the card
is already in view this is a no-op — correct: the scroll is
for the man who left from a card now off-screen, not a
flourish on every return.

### 1.7 Tests the builder writes (paper list)

- `chitVerdictFor` table: rows 1–6 each, incl. the
  `measuredMono == departureMono` boundary (must be row 2,
  not a verdict) and GRANTED-but-stale (row 6).
- `chitLineFor`: all four RETURNED_* lines at
  departurePrint = MISSING, with and without `nextDoor`;
  DEPARTED/SPENT → null.
- Store round-trip under Robolectric prefs: write → read →
  resolve → spend; resolve from SPENT is a no-op; spend from
  DEPARTED is a no-op.

---

## §2 · THE SETUP LEDGER (§6.8) — Proposal 2 core, SHIPS

The doors-at-stamp fact is unrecoverable once lost, so these
fields land IN THE SAME `edit()` as the chit's first merge —
in fact in the same transaction as the DONE stamp itself.

### 2.1 Fields + functions (`SetupStore`)

| Key | Type | Default | Meaning |
|---|---|---|---|
| `setup_stamped_wall` | Long | 0 | wall at DONE; 0 = never stamped |
| `setup_doors_at_stamp_mask` | Int | 0 | COUNTED bits GRANTED at the stamp instant |
| `setup_ever_set_mask` | Int | 0 | COUNTED bits seen GRANTED at any sweep since the stamp |
| `setup_called_in_mask` | Int | 0 | COUNTED bits whose call-in (§2.3a) has fired |

```kotlin
/** The DONE write. Sets setup_done, setup_stamped_wall and
 *  setup_doors_at_stamp_mask in ONE edit, commit() — but the
 *  two ledger fields are WRITE-ONCE: if stamped_wall != 0
 *  they are not touched (a day-40 reprint's DONE must never
 *  rewrite day one's fact). setup_done is idempotent. */
fun stampSetup(doorsAtStampMask: Int)

/** Null while unstamped (stamped_wall == 0). */
fun stampedWall(): Long?
fun doorsAtStampMask(): Int?

fun everSetMask(): Int
/** ORs bits in. Caller guarantees a real change; still one
 *  edit, commit(). */
fun noteEverSet(bits: Int)

fun calledInMask(): Int
fun markCalledIn(door: WatchItemId)  // one edit, commit()
```

Mask derivation is a pure helper in `ReceptionSheet.kt`
beside `receptionTotal` (it shares their derivation law):

```kotlin
fun doorsMaskAt(snapshot: WatchSnapshot?, nowMono: Long): Int
// bit i set iff permPrintAt(snapshot, COUNTED[i], nowMono)
//   == GRANTED. STALE is NOT set — aged proof was not a set
//   door at the stamp, and the ledger never upgrades it.
// snapshot == null -> 0.
```

**Write point — host, `MainActivity.stampSetupAndEnter()`:**
compute the mask from the CURRENT `watchSnapshot` at the DONE
instant, then stamp, in this order:

```kotlin
fun stampSetupAndEnter() {
    val mask = doorsMaskAt(watchSnapshot, SystemClock.elapsedRealtime())
    setupStore.stampSetup(mask)
    setupStamped = true
    ... // existing parkIntro discharge, close, navigate — unchanged
}
```

`SetupStore.markCompleted()` is deleted; this is its only
call site. (`isCompleted()` stays as the boolean read.)

**Ever-set write point — host:** 
`LaunchedEffect(watchSnapshot, setupStamped)`: when stamped,
compute `doorsMaskAt(watchSnapshot, nowMono)`; if
`(mask and everSetMask().inv()) != 0`, call
`noteEverSet(mask)`. Writes happen only on a bit's first
grant, ever — a handful of commits across the app's life.


### 2.2 Consumer (b) — PARK: "WAS SET AT SETUP — SINCE LOST"

Exact placement: `ParkDrawer.kt`, the board. Host passes one
new parameter, `setupStampMask: Int?` (`null` while
unstamped; in `MonolithRootApp`,
`if (setupStamped) setupStore.doorsAtStampMask() else null` —
`setupStamped` state already recomposes the host at the
stamp, so the value is fresh from that frame on).

`ParkBoardRow` gains `subLine: String? = null`, printed under
the row's hairline in `PillBadgeTextStyle` /
`onSurfaceVariant`, test tag `park_board_sub_<boardName>`.
For the six PERMISSIONS rows, PARK computes it only for the
counted four:

```kotlin
subLine = if (
    item in WatchItemId.COUNTED &&
    setupStampMask != null &&
    setupStampMask.bit(COUNTED.indexOf(item)) &&
    WatchStore.permPrintAt(snapshot, item, nowMono) == PermPrint.MISSING
) "WAS SET AT SETUP — SINCE LOST" else null
```

MISSING only, decided: STALE is aged proof, not loss — the
board already prints STALE as the word, and "SINCE LOST"
over an aged proof would be a new lie of exactly the species
S-1 charged. UNMEASURED prints nothing (no measurement, no
claim). The line is standing print, not one-shot: it prints
on every PARK opening while the regression stands, and
disappears the sweep the door reads GRANTED again. It is
capability memory, never a grade — no date, no count rides
it (the audit's kills: no nostalgia dates, no visit counts).

### 2.3 Consumer (a) — the deferred door, called in ONCE at
its first bite, in the refusal's own slot

The slot is the Deck's `RefusalSentenceSlot`
(`TactileDeckScreen.kt`, fed by `refusalSentenceFor`). A
door is DEFERRED when all three hold: its stamp bit is 0
(it was lacking at setup), its ever-set bit is 0 (it has
never been granted since — without this bit, a door he fixed
on day three and lost on day nine would be falsely called
deferred; the ever-set mask exists to keep this sentence
true), and its called-in bit is 0.

Pure helper, `MonolithCustomComponents.kt` beside
`refusalSentenceFor`:

```kotlin
fun refusalDoor(sentence: RefusalSentence, warrant: Warrant?): WatchItemId?
// FIX_ACCESSIBILITY -> ACCESSIBILITY
// FIX_USAGE -> USAGE_ACCESS
// RETRY_CHECK -> USAGE_ACCESS when warrant.usage == UNSET,
//   else ACCESSIBILITY (the unstamped-residency leg)
// null action (jam/annulment sentences) -> null: no call-in
//   ever rides a sentence that is not a door's refusal.

fun callInApplies(door: WatchItemId, stampMask: Int?,
                  everSetMask: Int, calledInMask: Int): Boolean
```

Wiring: `TactileDeckScreen` gains parameters
`setupStampMask: Int?`, `setupEverSetMask: Int`,
`setupCalledInMask: Int`, `onLedgerCallIn: (WatchItemId) -> Unit`,
all supplied by `MonolithRootApp` from `setupStore` (held in
host `remember` state refreshed wherever the masks change —
the stamp, `noteEverSet`, and the call-in itself).
Where the slot's `sentence` is computed (the existing `when`
over armPhase / armSentence / `refusalSentenceFor`), the
standing sentence is post-processed once:

```kotlin
val door = sentence?.let { refusalDoor(it, deckWarrant) }
val shown = if (sentence != null && door != null &&
    callInApplies(door, setupStampMask, setupEverSetMask, setupCalledInMask)
) sentence.copy(text = sentence.text + " · LEFT AT SETUP.") else sentence
```

Printed form, examples verbatim:
`BLOCKING IS OFF — TAP TO FIX · LEFT AT SETUP.` /
`USAGE ACCESS IS OFF — TAP TO FIX · LEFT AT SETUP.`
The tap action is the standing sentence's own — the suffix
adds no verb and changes no routing.

Consumption: `LaunchedEffect(shown?.text, door) { if (suffixWasApplied) door?.let(onLedgerCallIn) }`
in the Deck — the effect runs after the augmented sentence
has composed at least once, then the host marks the bit,
state refreshes, and the suffix never prints again for that
door, in any later refusal, ever. `markCalledIn` is the
consumption; there is no timer and no second channel.

### 2.4 Tests (paper list)

- `doorsMaskAt`: GRANTED sets, STALE/MISSING/UNMEASURED
  clear, null snapshot = 0.
- `stampSetup` write-once: second call with a different mask
  leaves wall + mask untouched, `setup_done` still true.
- `callInApplies` truth table over the three masks; PARK
  sub-line condition incl. STALE → null.

---

## §3 · THE ARMED HANDOVER (§6.9) — Proposal 3, SHIPS

### 3.1 Mechanism — host state only, unpersisted by law

In `MonolithRootApp` (`MainActivity.kt`), beside the
reception state:

```kotlin
var armedHandover by remember { mutableStateOf<GuardLinePrint?>(null) }
```

`remember`, NOT `rememberSaveable`, NOT `SetupStore`, NOT
the ViewModel: the handover is a fact about THIS sitting's
DONE press. Process death kills it, lawfully — a relaunched
app prints the standing Guard line, which is true, merely
less pointed. Auto-seal is killed by name (audit §3): the
handover points at the slide; it never performs it.

Setter — inside `stampSetupAndEnter()`, AFTER the ledger
stamp (§2.1), from the same DONE-instant snapshot:

```kotlin
val nowMono = SystemClock.elapsedRealtime()
armedHandover = if (receptionTotal(watchSnapshot, nowMono).count == 4) {
    GuardLinePrint("GUARD 4/4 — ONE ACT LEFT: SLIDE.")
} else {
    guardLineFor(watchSnapshot, nowMono)  // the standing
    // sentence, frozen at the DONE instant; may be null
    // while unmeasured, which simply means no override.
}
```

`receptionTotal(...).count == 4` is the sheet's own total —
the handover can never claim wholeness the sheet did not
print (and with §6.1's S-1 cure, count 4 means four GRANTED
prints, not four non-absences).

### 3.2 Where it prints — the Guard line's own slot

The header band is the slot: in the `Scaffold` header call,
`MonolithPullDownHeader(guardLine = armedHandover ?: guardLinePrint, ...)`.
One line, one tenant: while the handover stands, the live
`guardLinePrint` flow is masked, not merged. PARK's stub row
keeps reading the live line (it has its own derivation);
the handover is the Deck header's alone in this merge —
see proposal N-2 below for the priced extension.

### 3.3 Consumption — one-shot, four spenders, first wins

Any ONE of these spends it (`armedHandover = null`):

1. **The slide is attempted.** Host wraps the Deck's arm
   callback: the `TactileDeckScreen(onArmSession = ...)`
   call site becomes `{ armedHandover = null; viewModel.armMonolithSession() }`.
   Attempt, not success — a refused slide has still spent
   the "one act", and the refusal sentence then owns the
   slot's story.
2. **A session stands.** `LaunchedEffect(activeSession?.isActive) { if (activeSession?.isActive == true) armedHandover = null }`
   — catches routine-armed seals, which never pass through
   the Deck callback.
3. **He leaves the Deck.** `LaunchedEffect(currentDestination) { if (currentDestination != MonolithDestination.DECK) armedHandover = null }`.
4. **PARK opens.** `LaunchedEffect(parkOpen) { if (parkOpen) armedHandover = null }`
   — the Guard-line tap that would have shown him the line
   instead opens PARK; the moment is spent either way.

No expiry timer: an untouched handover over a standing
4/4 remains true indefinitely, and the first sweep that
drops a door makes `guardLineFor` diverge — at which point
spender 3/4 or the slide is the only honest end. (A fifth
spender, "handover dies when the warranted count drops",
was considered and rejected: recomputing truth under a
frozen print is the seal-freeze problem again; the four
spenders above are acts of HIS, which is what one-shot
means here.)


---

## §4 · H-2 SHIELD EXCURSION LAW (§6.10) — the trapdoor cured

### 4.1 The defect, in host terms

Today `onOpenShield = { receptionOpen = false; viewModel.navigateTo(SHIELD) }`.
`receptionOpen` is a `remember` initialised once from the
stamp; nothing re-raises it this sitting, and the BACK chain
lands him on the Deck sheetless. The sheet forgot him.

Note the asymmetry the law rests on: a FIX excursion needs
no flag — `receptionOpen` stays true under the Settings
pane, and return is an ON_RESUME into the same composition.
Only the Shield excursion sets `receptionOpen = false`, so
only it needs a departure record.

### 4.2 Exact host change (`MainActivity.kt`)

New state beside the reception state:

```kotlin
var shieldExcursion by remember { mutableStateOf(false) }
```

`onOpenShield` becomes:

```kotlin
onOpenShield = {
    if (!setupStamped) shieldExcursion = true
    receptionOpen = false
    viewModel.navigateTo(MonolithDestination.SHIELD)
}
```

Re-raise — one effect, keyed on the destination so EVERY
return path obeys it (rail tap, the non-Deck `BackHandler`,
a PARK route — all funnel through `currentDestination`):

```kotlin
LaunchedEffect(currentDestination, setupStamped, sealed) {
    if (shieldExcursion &&
        currentDestination == MonolithDestination.DECK &&
        !setupStamped && !sealed
    ) {
        shieldExcursion = false
        receptionOpen = true
    }
}
```

Endings other than return, both clearing the flag so it can
never fire stale: DONE cannot be pressed from Shield (the
sheet is not composed there), but the stamp can arrive via
PARK's reprint in a later sitting — the `!setupStamped`
condition already disarms the effect permanently in that
case; and if a routine seals the phone while he is on
Shield, `sealed` blocks the raise for that pass and the
flag is cleared by
`LaunchedEffect(sealed) { if (sealed) shieldExcursion = false }`
(the excursion ended by seal; an unstamped setup still
re-offers at the next cold start — the standing law, not a
loss). Process death mid-excursion: the flag is
`remember`-only and dies, and the cold start's initial
`receptionOpen = receptionInitiallyOpen(...)` opens the
sheet anyway. Every death lands on a lawful state.

Scope, stated plainly: the law is "while unstamped". A
stamped sheet re-opened from PARK that takes the Shield
door does NOT re-raise — PARK is its home and its return;
`shieldExcursion` is never set in that case because
`onOpenShield`'s guard reads the stamp, and a PARK-opened
sheet carries `setupStamped == true`.

### 4.3 The button's line — prints the way back

Until the host law lands the button owes a printed return;
with the law landed (§4.2, same merge) the line stays as
the excursion's standing truth, printed in
`ReceptionSheet.kt` directly under the `OPEN THE LIST` box,
above the existing LEAVE line, `PillBadgeTextStyle` /
`onSurfaceVariant`, test tag `reception_open_list_wayback`:

`THE LIST LIVES ON SHIELD — COME BACK TO THE DECK AND THIS SHEET RISES AGAIN.`

Printed only while `isFirstOpen` (the unstamped printing —
the only one the law governs). The LEAVE line beneath it is
unchanged. If §4.2 were ever reverted without this line
being re-ruled, the line becomes false and must die in the
same commit — the two are one fact.

### 4.4 Tests (paper list)

- Host-law truth table, by hand over the effect's three
  conditions: excursion + DECK + unstamped + unsealed →
  raises once, flag cleared; stamped → never; sealed →
  never, flag cleared; destination SHIELD/TASKS/VAULT → no
  raise, flag stands.

---

## §5 · BEYOND THE RULED LIST — new proposals, NOT build items

Nothing in this section is specced for this merge. Each is
a proposal to Pforte/Ren with its price; the builder builds
none of it on this document's authority. Proposal 4 of the
audit (the door that fell out) stays DEFERRED and is
deliberately not specced here — its admission price (the
regression census) is still owed.

**N-1 · The chit's overnight law.** A DEPARTED chit whose
`departureWall` is more than 24 h old at the next sheet
showing resolves to RETURNED_DARK before any sweep is
consulted — a verdict about Tuesday's excursion must never
print on Friday, however lawful its measurement. *Why:* the
chit proves a return he remembers; past a day it proves
only bookkeeping. *Price:* FREE–SMALL — one wall-age check
in the host resolution effect + one test row. No new state.

**N-2 · Handover echo on the PARK stub.** While
`armedHandover` stands, PARK's stub row prints it in place
of the live Guard line — one fact, one print, extended to
the stub the way F-13 extended the tear's rule. *Why:* he
can reach PARK before the Deck in the same sitting (Guard
tap spends the handover under §3.3 spender 4 without his
ever reading it); the stub is where that spent moment
should still speak once. *Price:* FREE — host passes the
handover into `ParkDrawer` as one nullable parameter; the
row's word precedence becomes handover → live line → OPEN.

**N-3 · The OUT breadcrumb in the Guard line.** While a
chit stands DEPARTED and the app has resumed (he is back
inside Monolith but the sheet is behind PARK or a seal
gate), the header prints the standing Guard line plus
` · OUT: <DOOR>`. *Why:* the excursion is the one state the
house currently prints nowhere — the sheet is hidden, the
chit is silent, and a man who detoured through PARK mid-
errand has no print telling him an errand is open.
*Price:* SMALL — one pure suffix function over
`guardLineFor` + chit state, one host state read; spends
itself when the chit resolves.

---

## §6 · BUILD ORDER NOTE FOR THE MERGE

§2's `stampSetup` and §1's chit fields land in the same
`SetupStore` rewrite (one file, one review). §3 and §4 are
both `MainActivity`-only host changes and share the
reception state block — write them in one pass so the four
effects (resolution, deadline, ever-set, re-raise) are read
together. §1's sheet changes (departure callback, chit
line, scroll) are independent and can land first or last.
Nothing here touches WatchStore, the warrant, or Room.

— Funke (innovator), issue #14 cell
