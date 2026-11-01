# ISSUE #9 — FEEL 1 (TOUCH): THE TOUCH OF PARK, ABOVE ALL THE CHECK KEY

*Feel 1 (touch), issue-9 cell (for Hof). Ground: the cell tree's built
`ParkDrawer.kt` (CHECK key + CHECKING derivation, FIX key + its return
effect, grab strip, scroll column), `MonolithCurves.kt` (MonolithKin),
`TactileSoundEngine.kt` (the knock / click waveforms the finger
actually feels), `MainActivity.kt` (`openFixPane`, the parkOpen
sweep), `issue-9-plan.md`, `issue-9-designer.md`,
`issue-9-finisher-keys.md`, and `AUDIT-park.md` §7 (the FIX dip and
its 800ms live-store return read — acquitted; nothing below re-opens
either). Paper only — nobody compiles.*

*PARK is the quietest screen and this is its touch law: a press here
should feel like touching an instrument, not a phone. Every number
below is either read out of the built file or tagged PATCH-READY
with its exact value.*

---

## §0 · VERDICT IN ONE BREATH

CHECK and FIX are **one family in the hand and two verbs in the
hold** — and both halves of that sentence are load-bearing. Depth
(1dp), onset (DOWN), and voice (the seat knock, identical call) must
never diverge: the finger must not be able to tell which key it is
touching before it reads the word (Finisher 2's law, adopted). What
diverges — what *holds* the dip and what *ends* it — is the meaning
of each verb, and it must stay divergent: CHECK's dip is held by the
machine answering; FIX's dip is held by the man's absence. One
wound in the built file is touch's own and is patched in §6 (T-1):
**FIX commits on DOWN, before the gesture is known to be a press at
all** — a scroll that merely *starts* on FIX leaves the app for OS
Settings. CHECK may keep DOWN-commit (its act is harmless and
idempotent); FIX may not.

---

## §1 · THE TWO PRESSES, AS BUILT (exact values)

| | FIX | CHECK |
|---|---|---|
| Body | full content width × **48dp**, radius **8dp**, ink fill | identical |
| Detector | `detectTapGestures(onPress=…)` in `pointerInput(item)`, gated by `interactive` | `detectTapGestures(onPress=…)`, gated by `interactive && !checking` |
| Commit instant | **DOWN** — `onFixClick(item)` fires inside `onPress`, before `tryAwaitRelease()` | **DOWN** — `onCheckClick()` fires inside `onPress`, before `tryAwaitRelease()` |
| Commit's cost | `startActivity` to an OS Settings pane (`MainActivity.openFixPane`) — the man leaves the app | `runWatchSweep()` — six parallel grant queries in the store; nothing leaves the room |
| Knock on DOWN | `playSeatKnock(10ms, 0.10 day / 0.06 night, 300Hz)` | the identical call, character for character |
| Dip | `offset(y = if (fixPressed) 1.dp else 0.dp)` — **snaps, 0ms, no animator** | `offset(y = if (checking) 1.dp else 0.dp)` — **snaps, 0ms, no animator** |
| Dip held by | `fixPressed` — set on DOWN, held for the **whole absence** in Settings | `checking` — the derivation: held until every one of the 6 PERMISSIONS carries `measuredMono >= checkPressedMono` |
| Dip released by | the return read only: `parkReturnTick` → `delay(800L)` → live-store read of the one named item (acquitted, §7) | the stamp landing itself — no delay, no timer, no beat (Finisher 2 §5's fence, adopted whole) |
| Success voice | `playFirmClick()` iff that item reads GRANTED at the return read | `playFirmClick()` iff all counted four read GRANTED when the set lands |
| Failure voice | silence — dip still releases | silence — board re-prints, `NOT PROVED` line stands under the key |
| Second press | unreachable (he is in Settings) | refused — no knock, no dip change, no second sweep from the key |

The knock the finger feels (TactileSoundEngine): a waveform of
**10ms strike + 5ms tail at 35%**, strike amplitude **25/255 by day,
15/255 by night** (0.10 / 0.06 × 255), plus the 300Hz transient. The
firm click is a different animal in the same hand: **13ms at
225/255, gap, 8ms at 135/255** — roughly nine times the knock's
amplitude. Acknowledgement and verdict are distinguishable blind,
by strength alone. That gap is the vocabulary; never narrow it.

---

## §2 · ONE FAMILY? — YES IN DEPTH AND VOICE, NO IN HOLD

**Depth: 1dp, both, forever.** 1dp is ≈0.16mm — below what a
fingertip pad can feel as static depth, especially centred on a
48dp key where the finger itself hides the face. The dip is not
*felt* as travel; it is *seen* as motion at the key's edges against
the hairlines and spacers around it, and *felt* only through its
synchrony with the knock. Two consequences:

1. **Never deepen one key.** 2dp on CHECK would read as a different
   species of object and would break the acquitted FIX grammar by
   comparison. Depth is family property.
2. **The dip's *timing* is its perceptibility.** As built, both
   offsets snap in 0ms — a 1dp step in a single frame reads as a
   flicker, not a press. A 90ms travel is what turns 1dp into
   something the eye-hand loop registers as *the key went down*.
   This is why T-2 (§6) is a touch patch, not a motion garnish.

**Timing, shared:** knock + dip begin on DOWN, in the same
recomposition, ≤1 frame after contact. No press in this drawer may
wait for UP: a verb that answers on release feels like a form
submit; PARK's keys answer like machine keys — contact is the act.

**Timing, divergent (by design, protect it):**

- CHECK's hold is **the sweep's own duration, ~300ms class**,
  unbounded in law: a query that never returns leaves the key
  dipped at CHECKING for the drawer's life (no timeout patch —
  a clock inventing a release would be a lie in the hand).
- FIX's hold is **minutes-class**: the whole Settings trip plus
  exactly **800ms** after the return tick. The dip the man pressed
  is still down when he comes back; it releases only when the
  verdict is read. His finger learns the key *waited for him*.

---

## §3 · WHAT THE FINGER LEARNS FROM EACH

**CHECK teaches: "the key is down while the machine answers."**
- A normal tap lasts ~100ms; the sweep takes ~300ms class. So for
  an ordinary tap the finger is already gone before the dip
  releases — the dip is a *visual receipt*, the knock is the felt
  one. Only a deliberate hold (>~300ms) feels the dip release
  *under the finger*, and that moment teaches the drawer's core
  touch truth: **neither dip tracks the finger.** `tryAwaitRelease()`
  is awaited and its result discarded in both keys; lift, hold,
  and slide change nothing once DOWN has landed. The dip belongs
  to the act, not to the hand.
- Word swap CHECK → CHECKING in the same frame as the dip: the
  hand asks, the face answers, the rows land one by one behind a
  key that stays down. LAST PROVED advancing in the same instant
  the dip releases ties release = proof in one felt event.
- Silence after a set-with-lacks is learned fast precisely because
  the firm click is so much stronger when it does come: click =
  whole, no click = read the board. Never add a failure buzz; a
  third voice would blur the two the hand already knows.

**FIX teaches: "the key stays down until your errand is judged."**
- Knock + dip on DOWN, then the room changes (Settings). The dip
  surviving an app switch is the strongest touch fact in the
  drawer: on return, the still-dipped key says *verdict pending*
  for 800ms, then releases — with the click if repaired, in
  silence if not. That 800ms of held dip is the entire handover;
  shortening it (or releasing on return-frame) would teach him
  the return itself was the verdict. Acquitted, and felt-correct.
- FIX's face never changes while dipped — no CHECKING-style word.
  Right: he is not here to read it. Its state is for the return.

**The night case.** At night the knock's strike is 15/255 — near
the perception floor of many vibration motors. What carries the
acknowledgement at 1 AM is the dip's *motion* (hence T-2) and the
word swap. Do not "fix" the night knock upward on one key or
both: 0.06 is the quietest-screen law, and the dip exists to
cover exactly this gap.

---

## §4 · ACCIDENTAL-PRESS REGISTER (ranked by cost)

**R-1 · Scroll-start on FIX → exile to Settings. SEVERE, built-in.**
Both keys live inside the drawer's `verticalScroll` column. A
vertical scroll is a DOWN, a pause, a move. `detectTapGestures`
fires `onPress` on the DOWN — *before* touch slop is crossed,
before the scroll exists as a gesture. By the time the scroll
would claim the pointer (and `tryAwaitRelease()` returns false),
`onFixClick` has already run: the man who put his thumb on FIX to
*scroll past it* is standing in OS Settings. The discarded
`tryAwaitRelease()` result is the receipt: the code already learns
the press was cancelled and does nothing with the knowledge.
This is also the standard way he will reach a below-fold CHECK
(§5): the revealing stroke's most natural start point is the
board's lower half — FIX's neighbourhood.

**R-2 · Scroll-start on CHECK → an unintended sweep. BENIGN.**
Same mechanism, opposite cost: the sweep is idempotent, stamps
only freshen, the knock + a ~300ms dip are the whole consequence.
It may even read as a feature (touch the board, it re-proves).
Kept as DOWN-commit deliberately — see T-1's asymmetry rule.

**R-3 · No slide-off escape on FIX. HIGH (same root as R-1).**
Standard keys cancel when the finger slides off before release.
Here, DOWN is already exile; sliding off, however far, changes
nothing. The finger has no *undo posture* for FIX. T-1 restores
one: slide past slop / let the scroll take the pointer =
cancelled trip, dip releases, nothing launches.

**R-4 · Stacked-key mis-aim. MODERATE, consequence-asymmetric.**
With both keys present they stack: FIX 48dp, gap (14dp as built;
8dp under Finisher 2 §7), CHECK 48dp — centres **62dp apart as
built, 56dp apart** after Finisher 2's spacing. Two full-width
48dp targets at 56dp centres are comfortably discriminable for a
thumb *aiming* at either centre; the risk is the boundary band
(±4dp around the gap) where a low CHECK aim lands on FIX's bottom
edge — and the two errors cost oppositely (a sweep vs exile).
This asymmetry, not the geometry, is what T-1 exists to absorb.

**R-5 · Presses during the rise. LOW, correct as built.**
`interactive = visible && settled && !isSealed`, with
`settled = (fraction == 0f)`: during the 280ms SEAT rise the keys
are visible but dead — no knock, no dip, no queue. A fast thumb
tapping CHECK mid-rise gets silence and must press again at rest.
That is calm-correct (no verb should fire from a moving sheet),
but it must stay *all-or-nothing*: never queue a mid-rise press
to fire at settle — a delayed verb with no finger on it would
feel haunted. SPEC-ONLY: protect the gate exactly as written.

**R-6 · Double-press. COVERED, unevenly.**
CHECK while CHECKING: gated off (`!checking`) — second tap is
wholly dead, not even a knock. Correct: a knock with no act
behind it would devalue the knock everywhere. FIX: unreachable
after the first press by construction. The open-sweep race is the
one soft spot: PARK's open already fired a sweep (host,
`parkOpen` effect); a CHECK press in the first ~300ms dispatches
a *second* sweep where the Designer (§1.2) said a press would be
absorbed. In the hand this is invisible except that the knock
answers a question already being answered. No patch proposed:
the store exposes no honest in-flight derivation to gate on, and
inventing one is worse than a duplicated idempotent sweep.
Flagged for Hof, not buried.

**R-7 · Long-press and hold. NO-OP, by construction.**
Holding either key does nothing beyond the DOWN act: no repeat
sweep, no escalation, no context act. Holding CHECK and watching
the dip release under the finger (§3) is the only long-press
"behaviour", and it teaches the right lesson. Never add a
long-press verb to either key — the drawer's whole touch register
is *one contact, one act*.

---

## §5 · THE SHEET'S OWN GESTURES — WHAT THE KEYS MUST NEVER FIGHT

The sheet has exactly three gesture territories, and the keys'
safety comes from their separation. Keep the borders where they
are:

1. **The grab strip owns sheet-drag.** 48dp touch height at the
   sheet's top (pill 24dp, growing to **28dp** while gripped,
   4dp tall), `detectVerticalDragGestures`: drag carries the
   sheet 1:1 (`fraction.snapTo(fraction + dragAmount/height)`),
   release seats by the **40% law** (>0.4 dismisses, else re-seat
   at 280 SEAT). It sits **≥521dp above FIX's top edge** at rest
   scroll — no finger travelling to a key ever crosses it, and no
   grip-drag ever starts on a key. **Law: no key may ever grow a
   drag detector, and the grab strip's drag may never extend
   into the scroll column.** A whole-sheet drag would put every
   scroll, every key, and dismissal in one arena.
2. **The scroll column owns content travel.** The board, keys,
   sleds and plate scroll as one column (`verticalScroll`, 20dp
   side padding, 100dp bottom padding as built). A vertical
   stroke starting anywhere in it is a scroll first — which is
   precisely why R-1/R-3 are about *commit timing*, never about
   adding drag handling to the keys. **Law: keys consume taps
   only; they must never consume a vertical drag's DOWN in a way
   that blocks the scroll's claim** — with T-1, FIX's launch
   waits for the release, so a claimed scroll simply cancels it.
3. **Scrim, rail, back own dismissal.** Scrim tap (the scrim
   carries `clickable`, `indication = null`), rail stop press /
   cap drag, PARK re-tap, Back. A downward stroke on a key is
   *not* a dismissal and must never become one — at scroll-top it
   reads as an attempted scroll and ends there. The grip's second
   detector (`detectTapGestures` on the strip) only grows the
   pill; a tap on the grip does nothing else. Minor false
   affordance (growth without an act), accepted: the strip was
   acquitted whole (audit §1.1), and a tap-to-collapse would add
   a fourth dismissal dialect for no need.

Sleds are neighbours, not kin: their horizontal throws live in
the same scroll column, but no touch rule crosses between a
sled's throw and a key's press. Keys stay tap-only, sleds stay
throw-only.

---

## §6 · ONE HAND AT THE REST HEIGHTS

**Rest states.** KIN-DRAWER has exactly two: fraction 1 (shut)
and fraction 0 (seated open). Keys exist to the hand only at
fraction 0 — mid-travel they are dead (R-5), under the seal they
are absent. Everything below is the seated sheet at scroll 0.

**Where the keys sit at rest** (cumulative from the sheet's top
edge, built spacers): grab strip 48 + header block ≈66 → board
top ≈114dp; the board is 9 ruled lines at the acquitted 49dp
pitch = **441dp** → board bottom ≈555dp; FIX (when present)
**569–617dp**; CHECK **631–679dp** with FIX present,
**569–617dp** alone.

**Against real viewports** (the host is the screen minus header
band and rail):

| Viewport height | FIX alone zone | CHECK with FIX present | CHECK alone |
|---|---|---|---|
| 732dp | fully visible | fully visible (bottom margin 53dp) | fully visible |
| 640dp | fully visible, 23dp to spare | **bottom 39dp below the fold** — a ≤39dp scroll reveals it | fully visible, 23dp to spare |
| 600dp | bottom 17dp clipped | **79dp below the fold** | bottom 17dp clipped |

Read: at rest, when visible, both keys sit in the **bottom
fifth of the sheet — prime thumb arc** for either hand, and
being full-width (sheet minus 40dp of padding) they ask nothing
horizontally: any thumb, any grip, lands. The failure is not
reach but *presence*: on a ≤640dp viewport with a lack on the
board, CHECK is born below the fold, and the stroke that reveals
it starts on the board — over R-1's territory. T-1 defuses the
stroke; the fold itself is structural (a 441dp acquitted board
above the keys) and no touch patch may shrink a board row to
rescue it. **Acceptance, exact:** on a 640dp viewport, CHECK
alone must rest fully visible and CHECK below FIX must need no
more than a **40dp** revealing scroll; on 600dp, no more than
**80dp**. If a future spacing change pushes past those numbers,
it escalates to Hof — it does not quietly borrow row height.

The 24dp seam fade above the rail takes no touch and prints
nothing; a key resting with ≤23dp clearance above the viewport's
bottom edge sits clear of it. Do not add bottom padding beneath
the keys to "protect" them — the keys are mid-column objects;
what follows them (sleds, plate) is supposed to be scrolled to.

---

## §7 · PATCH-READY ADJUSTMENTS (exact values, application order)

**T-1 · PATCH-READY — FIX commits on confirmed release; its
acknowledgement stays on DOWN.** *(The one behavioural change in
this document. It touches neither the dip, the knock, nor the
acquitted 800ms return read — only the instant `onFixClick`
fires, moving it from DOWN to the finger's own confirmed UP,
typically 80–150ms later, still inside the same contact.)*
In the FIX key's `pointerInput(item)` block, replace:

```kotlin
detectTapGestures(
    onPress = {
        fixPressed = true
        fixPressedItem = item
        soundEngine?.playSeatKnock(
            durationMs = 10,
            amplitude01 = if (isNight) 0.06f else 0.1f,
            pitchHz = 300f
        )
        onFixClick(item)
        tryAwaitRelease()
    }
)
```

with:

```kotlin
detectTapGestures(
    onPress = {
        fixPressed = true
        fixPressedItem = item
        soundEngine?.playSeatKnock(
            durationMs = 10,
            amplitude01 = if (isNight) 0.06f else 0.1f,
            pitchHz = 300f
        )
        // Touch (Feel 1, T-1): the knock and dip answer on
        // DOWN, but the trip to Settings commits only on a
        // confirmed release. A press the scroll claims, or
        // a finger that slides off, returns false here —
        // the dip releases and nothing launches. CHECK
        // keeps DOWN-commit: a sweep costs nothing.
        if (tryAwaitRelease()) {
            onFixClick(item)
        } else {
            fixPressed = false
            fixPressedItem = null
        }
    }
)
```

The asymmetry is the rule, stated for every future key in this
drawer: **DOWN-commit is earned by costing nothing.** Any key
whose act leaves the room, spends money, or cannot be undone
commits on confirmed release; its knock and dip still answer on
DOWN, so the hand loses no immediacy — only the accident loses
its route.

**T-2 · PATCH-READY — give both dips the same travel.**
Adopt Finisher 2 §2 verbatim — `animateDpAsState` per key,
**in: 90ms `MonolithKin.PRESS_MS`, `EasingSeat`; out: 160ms
`MonolithKin.DRAWER_CLOSE_MS`, `EasingThrow`**, targets 1dp/0dp
driven by `fixPressed` / `checking` respectively. Touch's reasons
for cosigning: 90ms is inside the hand's cause-effect window
(contact and travel read as one event), a snapped 1dp reads as
flicker, and at night (§3) this motion *is* the acknowledgement.
Named divergence for Hof: `MonolithKin.PRESS_BACK_MS` (150ms)
exists and is *not* used — the keys release on the drawer's own
160ms THROW, per Designer §1.6, so a key's return and the sheet's
departure share one clock-feel. If Hof prefers 150, change both
keys together; a split is the only wrong answer.

**T-3 · SPEC-ONLY — protect the commit/gate geometry.**
48dp height exactly (the target is the key; no invisible
extension — an extended FIX target overlapping the NOT PROVED
row or the 8dp inter-key gap would import R-4 into print),
20dp side padding as the only inset, `interactive` gate
unchanged, CHECK's `!checking` gate unchanged, no ripple /
indication on either key, ever.

**T-4 · SPEC-ONLY — the CHECKING fence, felt edition.**
No timer, beat, or frame count may release CHECK's dip, add a
second press path, or buzz on timeout. The key staying dipped
at CHECKING past any expectation is the honest touch state for
a question still unanswered (Finisher 2 §5). Likewise FIX's
800ms beat never crosses to CHECK.

**T-5 · SPEC-ONLY — dismissal borders.**
Grab strip stays the only sheet-drag zone (48dp, 40% law,
24→28dp grip growth); keys never dismiss; scrim/rail/back
dismissal unchanged. No new gesture is introduced by any patch
in this document.

---

## §8 · ACCEPTANCE, BY FINGER (no instruments)

1. Scroll test, FIX: put a thumb on FIX and scroll the sheet.
   After T-1: knock + dip on contact, dip releases when the
   scroll claims the gesture, **Settings never opens**, no firm
   click. Before T-1 this same gesture exiles him — the patch's
   whole proof is this one stroke.
2. Scroll test, CHECK: same stroke on CHECK fires one sweep —
   knock, dip ≈ the sweep's duration, rows re-print. Accepted
   as benign (R-2); it must never fire *two* sweeps for one
   contact.
3. Tap CHECK on a fresh board: knock + 90ms dip travel on DOWN,
   CHECKING under the finger or just after it lifts, release at
   the stamp landing over 160ms THROW, one firm click if whole,
   silence if not.
4. Hold CHECK for 2s: exactly one sweep; the dip releases under
   the still-resting finger when the set lands; lifting the
   finger afterwards does nothing at all.
5. FIX round trip: press (knock, dip, release-confirmed launch),
   repair in Settings, return — the key is *still dipped* on
   re-entry, holds ≈800ms, then releases with the firm click.
   Return unrepaired: same hold, release in silence.
6. Rise test: tap either key during the 280ms rise — nothing:
   no knock, no dip, no queued verb at settle. Press again at
   rest: normal act.
7. Reach test, one-handed (each thumb): at 640dp viewport,
   CHECK alone is tappable with no scroll; with FIX present, one
   ≤40dp stroke reveals CHECK and that stroke — started anywhere
   on the board, FIX included — cannot launch Settings (T-1).
8. Grip test: drag from the grab strip — pill grows 24→28dp,
   sheet follows 1:1, release under 40% re-seats, past it
   dismisses. No drag begun on any key moves the sheet, and no
   drag begun on the strip touches a key.

— Feel 1 (touch), issue-9 cell (for Hof)
