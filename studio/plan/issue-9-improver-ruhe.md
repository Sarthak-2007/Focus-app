# ISSUE #9 — IMPROVER PASS (RUHE): THE CHECK VERB + THE COLD-OPEN GUARD LINE, QUIETER

*Improver: RUHE, issue-9 cell (lead: Hof). Ground: `AUDIT-park.md`
(Hof), `issue-9-plan.md` (wave-1 close), `ISSUE-9-WAVE2-REPORT.md`
(Vogt), `issue-9-designer.md`, `issue-9-finisher-keys.md`,
`issue-9-finisher-guardline.md`, and the branch tree's own code:
`ParkDrawer.kt`, `GuardLine.kt`, `WatchStore.kt`, `Type.kt`,
`MainActivity.kt` — all as built on `agent/issue-9` @ 255ae52.
Paper only — nobody compiles; every item is DECLARED.*

*Scope, exactly: how Park PRESENTS its re-proof (the CHECK verb)
and the cold-open Guard line. Nothing else. No machinery changes,
no new surfaces, no new words beyond the prints specified here.*

*Improver's law, applied: Park is the quietest screen in the app.
Every round below LOWERS its voice — fewer full-ink states, fewer
duplicate statements, fewer sounds — and says what got better.
Nothing here raises a volume, adds a colour, or borrows the
parked Live-Fire Strip's language (issue #21 stays parked;
`FIRED AS SET` stays off the plate — unearned, both return
conditions unmet, verified again this pass). Fixed points held:
the cold-open Guard line prints `GUARD — NOT CHECKED`, never
blank; CHECKING remains a derivation from store stamps, never a
timer (Finisher 2's fence stands — nothing in this file sets,
clears, extends, or expires CHECKING by clock).*

*Type law: Sora for the headline and key words, Atkinson
Hyperlegible for board words, Anonymous Pro for telemetry/time —
off the existing ladder only (22 / 15 / 11 / 10). Never green or
near-green; no orange on any face in this pass.*

---

## ROUND 1 · CHECK'S DRESS JUDGES THE SAME WHOLE ITS VERDICT JUDGES

### Before (as built)

`ParkDrawer.kt`, the dress ladder (Designer §1.4, Finisher 2 §4.1):

```kotlin
val checkNeeded = if (snapshot == null) {
    true
} else {
    WatchItemId.PERMISSIONS.any { item ->
        val p = WatchStore.permPrintAt(snapshot, item, nowMono)
        p == PermPrint.STALE || p == PermPrint.UNMEASURED
    }
}
val checkInk = checking || checkNeeded
```

`PERMISSIONS` is all six permission-family items. The key's
verdict, one screen earlier in the same file, is computed on a
different whole (the `LaunchedEffect(checking)` landing block,
per Critic C-11, ruled deliberate and NOT re-opened here):

```kotlin
val firstLack = snap?.let { s ->
    WatchItemId.COUNTED.firstOrNull {
        WatchStore.permPrintAt(s, it, now) != PermPrint.GRANTED
    }
}
```

Consequence, reachable in ordinary use: the counted four sit
fresh and GRANTED while OVERLAY's measurement ages past the 45s
stale line — OVERLAY gates nothing (wave-2 seated it under the
eyebrow `NOT COUNTED — THE REST`) — and the CHECK key promotes
itself to full ink dress and stays there for the whole open
session. The drawer's loudest object is shouting for a door its
own proof does not judge. The face and the verdict speak
different wholes, and the louder one is the wrong one.

### After (PATCH-READY — one derivation, no other delta)

```kotlin
// Ruhe R1: the dress reads the COUNTED four — the whole the
// key's own verdict (firm click / NOT PROVED name) judges.
// OVERLAY and BATTERY print their states on their own rows
// (STALE half-ink, name-only unmeasured) but never promote
// the verb: they are NOT COUNTED, and CHECK does not prove
// them. A null snapshot needs no special case — nothing
// measured means the counted four are UNMEASURED.
val checkNeeded = WatchItemId.COUNTED.any { item ->
    snapshot == null ||
        WatchStore.permPrintAt(snapshot, item, nowMono).let {
            it == PermPrint.STALE || it == PermPrint.UNMEASURED
        }
}
val checkInk = checking || checkNeeded
```

(Builder: keep the null handling in whatever shape reads
cleanest; the ruling is the list — `COUNTED`, not `PERMISSIONS`.)

Unchanged, by name: MISSING never promotes the key (a lack's
answer is the FIX key above it); the dress swap stays instant at
the state boundary; the six-item whole still governs CHECKING's
*completion* and LAST PROVED (C-11's two wholes stand — this
round aligns the key's FACE with its verdict whole; it does not
touch the completion whole).

### What got better

- One whole governs the key: dress, firm click, and the
  `NOT PROVED` name all speak the counted four.
- The commonest false shout in the drawer dies: a stale or
  unmeasured non-counted row can no longer hold the key at full
  ink for an entire session. The fact still prints — on its own
  row, at its own dress. Nothing is hidden; the verb is simply
  no longer promoted by a door it does not prove.
- Volume audit: full-ink CHECK states shrink to exactly three —
  a press in flight, a counted row stale, a counted row
  unmeasured. Each one is a state where pressing CHECK is
  actually the answer.

### Acceptance (DECLARED tests, for the builder)

1. Counted four fresh GRANTED + OVERLAY past 45s (STALE):
   key in print dress; OVERLAY row still prints `STALE` at
   half-ink.
2. Counted four fresh + BATTERY never measured: key in print
   dress; BATTERY row name-only.
3. Any counted row STALE or UNMEASURED (incl. null snapshot):
   key in ink dress — the pre-round behaviour, preserved.
4. Any counted row MISSING, rest fresh: key in print dress
   (FIX carries the lack) — preserved.

---

## ROUND 2 · NOT PROVED LEAVES THE BOARD'S GRAMMAR; IT BECOMES A DATED RECEIPT LINE

### Before (as built)

The unwhole landing prints (ParkDrawer.kt, under the CHECK key):

```kotlin
val lackName = lastCheckLackName
if (lackName != null && !checking) {
    Spacer(modifier = Modifier.height(16.dp))
    ParkBoardRow(
        name = "NOT PROVED",
        word = lackName,
        wordInk = ParkWordInk.FULL
    )
}
```

A `ParkBoardRow` is the board's heaviest print object: a 48dp
row plus hairline, its word in Atkinson Hyperlegible Bold 15sp.
FULL is the loudest ink tier (F-B2: "a measured lack"). The
verdict therefore spends the surface's maximum volume on a fact
the board already printed a few lines above — the named door's
own row says `MISSING` at FULL or `STALE` at HALF. One lack, two
statements, the duplicate at full volume, on the screen whose
whole character is quiet.

Second defect: the row is undated. It persists (Designer §1.5's
persistence law — it must: a reader at 1 AM must not miss it by
looking up late), but it persists in the present tense while the
board around it ages. Minutes later it still reads as a standing
verdict on the current board, when it is in fact the record of
one completed act.

### After (PATCH-READY — replace the block above)

The verdict leaves the board-row grammar and takes the drawer's
quietest existing print register — the footline's dress
(`park_guard_footline`: Anonymous Pro 11sp, `onSurfaceVariant`):

- One `Text`, 8dp under the key (the spacing module; the two-key
  stack already uses 8dp between keys), `testTag =
  "park_check_verdict"`, text composed as:

  `NOT PROVED — {NAME} · {HH:mm}`

  `{NAME}` is the same first unproved counted door the current
  code computes; `{HH:mm}` is the wall time of the landing that
  produced the verdict, in the foot line's own format
  (`SimpleDateFormat("HH:mm", Locale.US)`).
- State: `lastCheckLackName` gains a companion —
  `lastCheckLackWall: Long?`, set in the same landing block
  (`System.currentTimeMillis()` at the instant the verdict is
  set) and cleared everywhere the name is cleared (next press,
  close, board-reads-whole). This is a print stamp of the same
  species as LAST PROVED's wall time: it is recorded, never
  read back into any derivation. It does not set, clear, or
  expire anything; Finisher 2's fence (no timer may declare
  CHECKING or a verdict) is untouched — the verdict's lifetime
  is exactly what it is today.
- Sound: unchanged — an unwhole landing is silent; the firm
  click still sounds only on a whole counted four.
- The 16dp spacer + row are deleted with the block; nothing
  else moves. (Net: the drawer gets ~40dp shorter in the
  verdict state and the key zone keeps the {8,16,24} module.)

### What got better

- Volume: 15sp bold at full ink → 11sp at quiet ink. The
  verdict is now visibly what it is — the act's footnote —
  and the board's own row is once again the single full-ink
  statement of the lack.
- Truth over time: dated, the persistent line reads as
  history — *at 02:47 this door was not proved* — instead of
  a present-tense charge that quietly goes stale. Persistence
  (the Designer's law) and honesty stop trading against each
  other.
- Register coherence: Park now has exactly two dated receipt
  prints, both in Anonymous Pro — the foot signature
  (`GUARD ···· {n}/4 · {HH:mm}`) and this verdict. The board
  rows accuse; the small print records. Each in its own voice.

### Acceptance (DECLARED, for the builder)

1. Force an unwhole landing (one counted door MISSING): under
   the key, one 11sp quiet line `NOT PROVED — {DOOR} · {HH:mm}`;
   no board row, no hairline, no 15sp text in the verdict.
2. The named door's own board row still prints its state at
   its own ink (MISSING FULL / STALE HALF) — the only full-ink
   statement of that lack on the surface.
3. Verdict persists across scroll and board ageing; clears on
   next CHECK press, on drawer close, and when the counted
   four read whole by any road — as today.
4. Whole landing: no verdict line, one firm click — as today.

---

## ROUND 3 · THE COLD OPEN, END TO END

### 3a · The headline adopts the Guard line's trigger

Before (as built, ParkDrawer.kt header block):

```kotlin
val countedNow = snapshot?.let { WatchStore.countedWarranted(it, nowMono) }
Text(
    text = if (countedNow != null) "GUARD $countedNow/4" else "GUARD",
    style = DrawerHeaderTextStyle,
    ...
)
```

Two defects, both on the cold open's own ground:

1. **Null snapshot → bare `GUARD`.** A title, not a state. At
   that same instant the header band behind the sheet prints
   `GUARD — NOT CHECKED` (GuardLine.kt, total since F-G4) and
   the warrant row inside this same drawer prints
   `NOT CHECKED`. The drawer's headline is the one surface
   still allowed to say nothing about its ignorance — P-6's
   blank, wearing a headline's dress.
2. **Partial counted set → a count the header refuses to
   print.** `countedWarranted` counts GRANTED among the counted
   four regardless of whether the rest are measured. One counted
   door UNMEASURED, two GRANTED, one MISSING prints headline
   `GUARD 2/4` while `guardLineFor` — whose law is *no count
   off a partial set* (Designer §2.1, the day-one-lie guard) —
   prints `GUARD — NOT CHECKED` for the same snapshot at the
   same instant. Two faces of the Guard, one store, contradictory
   prints. The lie class P-6 killed in the band survived in the
   drawer.

After (PATCH-READY — the headline derives by the Guard line's
own rule; dress, tag, and slot unchanged):

```kotlin
val countedPrints = snapshot?.let { snap ->
    WatchItemId.COUNTED.map { WatchStore.permPrintAt(snap, it, nowMono) }
}
Text(
    text = if (countedPrints == null ||
        countedPrints.any { it == PermPrint.UNMEASURED }
    ) {
        "GUARD — NOT CHECKED"
    } else {
        "GUARD ${countedPrints.count { it == PermPrint.GRANTED }}/4"
    },
    style = DrawerHeaderTextStyle, // Sora Bold 22sp, as today
    modifier = Modifier.testTag("park_guard_headline")
)
```

Fit: 19 glyphs in Sora Bold 22sp at −0.3 tracking ≈ 230–245dp
against a 280dp content width on the narrowest (320dp) handset
class — one line, with air. No `maxLines` games, no ellipsis,
no shrink-to-fit: an elided NOT CHECKED would be a small P-6 of
its own (F-G3's ban, extended to this surface by name).

What got better: one trigger now governs every Guard summary
in the app — band and headline print the same sentence in their
own dresses, and no surface ever prints a count off a partial
set. NOT CHECKED is also the *quieter* print: it accuses no
door and names none, which is the correct volume for "not yet
measured" inside the drawer whose job is the machine's state.

### 3b · The open-sweep shows on the key — silently; the knock narrows

Before (as built): the host fires the Watch sweep on every
Park open (`MainActivity`, the `parkOpen` effect:
`viewModel.runWatchSweep()`), but the key's state knows only
manual presses:

```kotlin
var checkPressGen by remember { mutableStateOf(-1) }
val checking = checkPressGen >= 0 &&
    (snapshot?.lastCompletedSweepGen ?: 0) < checkPressGen
```

During the open-sweep the key therefore sits in ink dress
(Round 1's ladder promotes it — the board is stale/unmeasured,
which is why the sweep fired) reading `CHECK`: an invitation
to press a verb already running. A press re-dispatches a second
sweep and plays the seat knock for an act already in flight.
Designer §1.2 ruled the opposite — *opening PARK fires the
sweep, and the key simply shows that act* — and her §5
checklist item 2 expects `CHECKING` dipped on open. The build
quietly dropped that half.

After (PATCH-READY — display widens, verdict does not):

```kotlin
// Ruhe R3b: the DISPLAY state also shows a sweep the host
// fired (PARK open) — the same generation pair C-1 seats:
// a sweep is in flight while the store's dispatched
// generation is ahead of the last completed one.
val sweepInFlight = (snapshot?.lastCompletedSweepGen ?: 0) <
    WatchStore.currentSweepGen()
val checkingDisplay = checking || sweepInFlight
```

- The key's word and dip read `checkingDisplay` (was:
  `checking`). Dress ladder unchanged (Round 1's `checkInk`
  already includes the manual state; an in-flight open-sweep
  on a fresh board keeps print dress — the word alone carries
  it, at print volume).
- The verdict block (`LaunchedEffect(checking)`: firm click /
  `NOT PROVED` name) stays keyed to the MANUAL `checking`
  exactly as built. An open-sweep landing — whole or unwhole —
  plays no click and sets no verdict line. Those belong to his
  deliberate press; the drawer's automatic housekeeping does
  not get to announce itself.
- The knock narrows: in the key's `onPress`, `playSeatKnock`
  sounds only when the press takes the key from not-displayed-
  checking to checking (`!checkingDisplay` at the press
  instant). A re-press while CHECKING already shows keeps its
  C-2 function — re-dispatch, re-base the generation — in
  silence. The escape hatch was never meant to knock twice.
- Seal behaviour untouched: the key is absent under the seal
  (wave-1's ruling over the Designer's inert seat, C-4 — not
  re-opened), so this derivation only ever renders where the
  key lawfully exists.

Derivation honesty: both numbers are store facts read at
render, of the same species as the existing C-1 derivation;
landings emit snapshots throughout a flight, so the word
tracks the act. No timer, no delay, no new state is introduced
— Finisher 2's fence stands.

What got better: the cold open tells one story — the key
admits a proof is already running instead of inviting a
redundant press; the redundant press, when it still happens,
is silent; and the only sounds left in the entire verb are the
idle-press seat knock and the deliberate whole-landing firm
click. Fewer presses invited, fewer knocks played, and the
Designer's filed spec and the built drawer agree again.

### Acceptance (DECLARED, for the builder)

1. Cold start, open Park before any sweep lands: headline
   `GUARD — NOT CHECKED`; header band (behind the sheet) the
   same sentence; warrant row `NOT CHECKED`. No bare `GUARD`
   anywhere.
2. Snapshot with one counted door UNMEASURED (any granted /
   missing mix): headline `GUARD — NOT CHECKED` — never a
   count. Fully measured set: headline `GUARD {n}/4` — as
   today.
3. Open Park on a stale board: key reads `CHECKING`, dipped,
   with no press and no sound, until the open-sweep's set
   completes; no click, no verdict line on its landing.
4. Press CHECK from idle: one seat knock, `CHECKING`, and on
   landing the Round-2 verdict behaviour (click iff counted
   whole; dated quiet line iff not).
5. Press CHECK while `CHECKING` shows: sweep re-dispatches,
   generation re-bases, NO knock.

---

## WHAT THIS PASS DID NOT TOUCH (by scope and by ruling)

- The header Guard line's own prints — all four were already
  right (wave-1 + F-G4 totality). Round 3a cures its drawer
  twin, not the line itself.
- CHECKING's completion whole (six) and the C-11 two-wholes
  ruling; the seal's read-only drawer and CHECK's absence
  under it (C-4); the FIX key, staircase, and verdict (Vogt's
  wave-2, another lane); tiers, age column, warrant line,
  plate (cut by wave-2; `FIRED AS SET` verified still struck
  in `PLATE_BODY` and its UNA-05 comment this pass — unearned,
  stays off); the Live-Fire Strip — parked in #21 by Sarthak's
  decision, not designed, not hinted, not pre-sold anywhere in
  this file. CHECK's copy register gains no word from it.
- No machinery, no DB, no new permission, no new surface.
  Every delta above is a derivation or a print in files the
  cell already owns (`ParkDrawer.kt`; tests beside the
  existing Park print tests).

## VOLUME LEDGER (the improver's receipt)

| Surface | Before | After |
|---|---|---|
| CHECK dress | full ink on any of 6 stale/unmeasured | full ink on counted 4 only (R1) |
| NOT PROVED | 15sp Bold, FULL ink, 48dp board row + hairline | 11sp Anonymous Pro, quiet ink, one dated line (R2) |
| Headline, unmeasured | bare `GUARD`, or a count off a partial set | `GUARD — NOT CHECKED`, the band's own law (R3a) |
| Open-sweep | invisible; key invites a knocking re-press | shown as CHECKING, no sound, no verdict (R3b) |
| Re-press knock | knocks every press | knocks only from idle (R3b) |

Net: five loud states removed or demoted, zero added. Every
fact that printed before still prints — on its own row, in its
own dress, at its own volume.

## Next wants (unassigned capacity)

- The same one-trigger audit for the Deck's warrant prints
  vs. Park's warrant row (wave-2 seated them on one derivation;
  nobody has yet checked their *unmeasured* faces agree the way
  R3a now makes the Guard faces agree). Another cell's scope —
  filed here for Hof/Ren to route, not taken.

— RUHE, improver, issue #9 (lead: Hof) · Oct 30, 2026
