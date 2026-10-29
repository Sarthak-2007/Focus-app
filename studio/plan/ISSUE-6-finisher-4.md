# ISSUE #6 — FINISHER-4 SPEC: F19 · F20 · F21 (gallery standard)

FINISHER-4, Rost's cell, issue #6 (Deck audit fixes), branch
`agent/issue-6`. Paper-verified against the tree as read this shift:
`TactileDeckScreen.kt` (1,148 lines), `MainActivity.kt` (821),
`ReceiptSheet.kt`, `MonolithRailView.kt`,
`MonolithCustomComponents.kt` (strip + `endsAtText`),
`MonolithCurves.kt` / `MonolithKin`. Source rulings:
`studio/plan/AUDIT-deck.md` §5 (charges 5 + 8), §9 (F19/F20/F21);
cross-checked against `studio/plan/AUDIT-receipt.md` items 17–19
(Zettel's cell) — one tension found, flagged in §4, not silently
resolved. Nothing compiled, nothing committed — DECLARED only.

**Merge-order note (read first).** F19–F21 are independent of the
restructure and land on the current room (audit §9 sequencing).
Every replacement text below is true of the tree AS IT STANDS.
Where another F-item rewrites the same surface later (F2 kills the
FLOW cap, F6 the chips, F10 the catch ghost, F15 the vertigo, F16
the standing strip), that item's builder owns the final wording of
its own block; F21's texts are the floor those lanes start from,
never a text they must preserve past their own change.

---

## §1 · F19 — RECEIPT DISMISSAL: SLIP-SWIPE OR PARK/RAIL ONLY (S)

### 1.1 · The state of the code, charged

- `ReceiptSheet.kt`: the scrim `Box` carries
  `.clickable(onClick = onDismiss)` over the full frame — ANY tap
  that reaches the scrim dismisses, on UP. The slip `Column`
  carries `.clickable(enabled = false) {}` — a disabled clickable
  installs no gesture detector, so taps on the paper fall through
  to the scrim behind it. Net behaviour, exactly as Zettel's audit
  (item 19) charged: any tap anywhere swats the ending, and the
  tapped thing's own action never fires.
- Host (`MainActivity.kt`): dismissal state is
  `dismissedReceiptSessionId` (line ~263). The lawful doors that
  already exist and MUST keep working: the `LaunchedEffect(
  currentDestination, parkOpen)` beside it dismisses on any
  navigation away from the Deck and on PARK's invocation; the
  rail's `onTabSelected` and PARK's invocation run through that
  same effect. No timer dismisses. System Back has no handler on
  the Deck while the slip stands.
- The slip's content scrolls (`verticalScroll` on the slip
  `Column`) — the dismissal swipe has to coexist with that scroll;
  §1.3 rules how.

### 1.2 · The exact gesture set — exhaustive

| Input | Result |
|---|---|
| Downward swipe ON THE SLIP, released past threshold (§1.3) | **DISMISSES** — the slip's own door |
| Rail stop tap or drag-seat (any of the 4 stops) | **DISMISSES** — host door, via navigation effect |
| PARK invoked (rail zone, or Guard-line tap opening PARK) | **DISMISSES** — host door, via `parkOpen` effect |
| Tap on the scrim (DOWN+UP within touch slop) | **SHAKE** (§1.4) — never dismisses, at any count, at any age of the slip |
| Tap on the slip | Nothing. The paper is read, not pressed. No shake, no dismissal |
| Drag on the scrim (any direction) | Nothing — no shake, no dismissal |
| Long-press anywhere (scrim or slip) | Nothing |
| Upward / horizontal swipe on the slip | Nothing — content scrolls (upward) as today; the slip does not translate |
| System Back | NOT a door. Do not add a `BackHandler` for the slip; host default stands (the slip's cold-start persistence is AUDIT-receipt item 20, group B — a neighbour, not this item) |

No dwell, no grace period, no faulted-slip exception: the two
lawful doors work from the slip's first frame, restored slips
included (a restored slip is the same object under the same law).
The swipe IS the explicit act for the faulted slip — see §4.1 for
how this answers AUDIT-receipt A8.

### 1.3 · The slip-swipe, specced

- **Direction:** downward only — the slip leaves through the foot
  of the screen, the reverse of its feed.
- **Tracking:** a downward drag that begins on the slip while the
  slip's own scroll state is at its top (`scrollState.value == 0`)
  translates the whole slip 1:1 with the finger. The expected
  mechanism is a `NestedScrollConnection` on the slip that claims
  post-scroll downward deltas at the top (the scroll keeps first
  claim on every other delta — content scrolling is untouched).
  A downward drag while the content is scrolled translates
  nothing; it scrolls back toward the top as today, and only the
  overshoot past the top becomes slip travel.
- **Threshold:** release at ≥ **56dp** of slip travel dismisses.
  Release below 56dp springs the slip home: 260ms,
  `MonolithCurves.specA()` (DURATION_260 · EasingA — a seat).
  A downward fling released with ≥ 24dp of travel also dismisses
  (velocity judged by the nested-scroll fling hand-off, not a
  second tracker).
- **Exit:** from the release offset the slip translates down its
  full height and the scrim fades 0.12 → 0, both over **180ms on
  `MonolithKin.EasingThrow`** (DURATION_180 — THROW is the house's
  named dismissal motion: "a dismissal accelerates away").
  `onDismiss()` fires exactly once, at the animation's end; the
  host unmounts as today. From the frame the exit starts, all
  input on scrim and slip is dead — no double-fire, no shake
  during the exit.
- **The slip consumes its own surface:** taps and non-dismissal
  drags on the slip are consumed by the slip's pointer handling.
  Nothing falls through to the scrim, nothing behind the sheet is
  reachable — same containment as today, now by the slip's own
  hand rather than by fall-through accident.

### 1.4 · The shake, specced

A stray scrim tap is a wrong-door knock; the answer is the slip
shaking its head — the paper moves, the room does not.

- **Target:** the slip only. The scrim never moves, never flashes.
- **Form:** horizontal keyframe shake on the slip's
  `translationX`, one pass, **280ms** (`MonolithCurves`
  DURATION_280), keyframes (time → offset):
  0ms → 0dp · 55ms → −6dp · 115ms → +5dp · 175ms → −3dp ·
  230ms → +1.5dp · 280ms → 0dp. Decaying, asymmetric, ends at
  exact rest — no overshoot past 0, no second pass.
- **Retrigger:** a tap mid-shake restarts the envelope from 0
  (snap, then replay). Taps never queue, never stack amplitude.
- **Feel:** one `HapticFeedbackType.TextHandleMove` tick at shake
  start, via `LocalHapticFeedback` — no new host seam, no sound
  (the receipt room is silent print; TactileSoundEngine is not
  wired here and F19 does not wire it).
- **Scrim ripple dies:** the scrim's `clickable` takes
  `indication = null`. A ripple on a defocus scrim is a button
  costume on a non-button.
- **Reduced motion:** if the system animator duration scale is 0,
  the shake collapses to a single 2dp nudge over 90ms — the
  refusal still reads, the motion does not perform.
- **Semantics:** the shake changes no state a screen reader can
  see; the slip's dismissal action name for F22's pass is the
  swipe ("Dismiss receipt" as a custom action on the slip) —
  noted here so F22 does not invent a scrim-tap action.

### 1.5 · Comment corrections that ride with F19

These comments become false the moment F19 lands; they are part
of this item, not F21 scope creep.

**(a) `ReceiptSheet.kt` header KDoc** — current: "…the slip
settled and still: it never re-strikes, never animates its
numbers, and dismisses on the next DOWN anywhere or on
navigation." Replacement paragraph:

```
 * receipt branch. Viewport-hosted (F-B1: it composes over the
 * Deck at the root, never inside the rail's band), defocus scrim
 * at 0.12 (Linea's frame law — the digest's 0.65 is a different
 * room's law), the slip settled and still: it never re-strikes,
 * never animates its numbers. Dismissal (F19): a downward swipe
 * on the slip itself, or the host's PARK/rail doors — a tap on
 * the scrim shakes the slip and dismisses nothing.
```

**(b) `MainActivity.kt` lines 263–268** — current: "the slip
stands until the next DOWN anywhere, a navigation, or PARK's
invocation". Replacement:

```
    // The receipt sheet's dismissal is host state (F-B1): the
    // slip stands until it is swiped away on its own surface, a
    // navigation, or PARK's invocation — a scrim tap only shakes
    // it (F19). It is never dismissed by a timer. (The nav/PARK
    // effect itself sits below, beside the PARK state it reads.)
```

**(c) `MainActivity.kt` receipt hosting block, lines ~748–754** —
current: "dismissed by the next DOWN (its scrim), by navigation,
or by PARK". Replacement:

```
            // THE RECEIPT SLIP (Slice E, F-L1/F-B1): viewport-
            // hosted over the Deck's room, dismissed by a swipe
            // on the slip itself, by navigation, or by PARK
            // (F19) — the two sheets (this and the digest) never
            // stack: PARK and the digest live in their own
            // invocations, and opening either dismisses this one
            // first.
```

Untouched, verified true under F19: the rail comment in
`MainActivity.kt` ("A stop press while PARK is open is dismissal
path (a) — … the session receipt sheet goes with it (F-B1)").

### 1.6 · Acceptance (paper/render-harness assertions)

1. Scrim tap ×5 → slip still composed; `dismissedReceiptSessionId`
   unchanged; shake observed after each tap.
2. Slip tap → slip composed, no shake, no dismissal.
3. Slip drag down 40dp, release → slip returns to 0 offset;
   no dismissal.
4. Slip drag down 80dp, release → exit runs; `onDismiss` fired
   exactly once; host state carries the session id.
5. Slip content scrolled (value > 0): downward drag scrolls
   content first; no slip translation until the top is reached.
6. Rail stop tap with slip standing → dismissed. PARK invoked
   with slip standing → dismissed. (Host doors regression.)
7. Faulted slip: identical set — swipe dismisses, scrim shakes.
8. Scrim shows no ripple on tap.

---

## §2 · F20 — PRINT PARK ON THE RAIL'S UNNAMED ZONE (S)

File: `MonolithRailView.kt` → `onDraw` (PARK-zone furniture block,
~line 226) + the class KDoc. Host chrome, flagged already by the
builder map as outside the Deck file but inside this issue.

### 2.1 · The print spec

- **Word:** `PARK`. Nothing else — no count, no badge, no icon.
- **Voice:** the stops' own print voice, no new style: the same
  `paintMono` in its word-pass state — Anonymous Pro, 10sp,
  `letterSpacing = 0.2f`, `Align.CENTER`.
- **Seat:** centred in the zone — centre x =
  (parkStartX + trackRightX) / 2, baseline = the stops'
  `wordBaseline` (centreY + 10sp × 0.36). Same line, same height
  as DECK · SHIELD · TASKS · LEDGER: five words on one baseline,
  the fifth behind its score line.
- **Fit, by arithmetic:** the zone is a constant 48dp
  (`RailGeometry.PARK_ZONE_DP`, width-independent). PARK at 10sp
  Anonymous Pro (0.6em advance): 4 × 6.0dp = 24dp, plus 3 letter
  gaps × 0.2em × 10sp = 6dp → ≈30dp of print in 48dp of zone,
  ≈9dp clear on each side of the word before the score line and
  the end wall. It fits without touching the geometry. The
  builder confirms against `paintMono.measureText("PARK")` on the
  first render; if a future typeface swap breaks the fit, the
  word shrinks — the zone never grows.
- **Tone law** (mirrors the stop words' three tones, same tokens):
  | State | Tone |
  |---|---|
  | At rest, cap elsewhere | `tokTextSecondary()` — an unselected stop's ink |
  | Pressed (`pressedStop == PARK_INDEX`, not dragging) | `tokTextPrimary()` — the one-frame strike, same as a pressed stop |
  | Cap in the zone (PARK open, or cap settled/dragged to nearest stop 4) | `tokOnControlFill()` — the word under the cap takes the glyph tone, same as a selected stop |
  While PARK is open the word keeps the glyph tone for the
  sheet's whole life: PARK is where he is, and the band says so.
  (The stops' "no word is selected while PARK is open" rule is
  unchanged — PARK's word is not a stop's selection, it is the
  zone's name lighting under its cap.)

### 2.2 · Furniture: the notch grip dies

The two notch lines at the zone's centre (`notchCx ± 2dp`,
ink 0.35 + 0.35 × parkFraction) occupy the exact centre the word
now takes — word and grip cannot share the seat. **The notch is
deleted, with the `parkFraction` inking that fed it.** What
stays, unchanged: the score line at the zone's inner edge
(ink 0.18, 6dp insets) and the end-wall line at the track's end
(ink 0.35) — they become the word's frame, the pair of rules a
named end-station sits between.

### 2.3 · Traps, named for the builder

- Do NOT add `"PARK"` to `RailGeometry.WORDS`. That list is the
  four stops; the draw loop, `stopForX`, and travel math index it
  as stops 0..3. A fifth entry would print a phantom segment word
  and invite stop-math drift. PARK prints from its own draw call
  in the zone block.
- PARK gains no rest position, no selection state, no travel
  change: `selectedTabIndex` stays 0..3, `PARK_INDEX = 4` stays
  travel-math only, the cap's resistance stretch into the zone
  (0.55 / 0.85 factors) is untouched.
- Geometry is untouched: `PARK_ZONE_DP` stays 48, margins,
  insets, segment math all stand. This item is print only.
- F22 note: the word is print. The rail's accessibility semantics
  (the zone's action name) remain F22's bar debt, unchanged by
  this item.

### 2.4 · Comment correction that rides with F20

`MonolithRailView.kt` class KDoc — current: "…and, at the track's
end, the PARK zone — a flat 48dp stretch that prints NO word
ever, because PARK is where the machine's own state lives, not a
place he goes." Replacement:

```
 * A 52dp recessed band flush at the foot of the screen: four stops
 * printed ON the recess (DECK · SHIELD · TASKS · LEDGER) and, at the
 * track's end, the PARK zone — a flat 48dp stretch that prints PARK
 * in the stops' own voice (F20): it is a door he uses, and a door is
 * named. The zone is still not a stop — no rest position, no
 * selection; the cap enters it, the word names it, the sheet is the
 * machine's own state. One cap travels the stops under KIN-NAV.
```

(`RailGeometry`'s KDoc — "There is no fifth rest position: PARK
is a zone the cap enters, not a stop it selects" — is true today
and stays true under F20. Untouched.)

### 2.5 · Acceptance

1. Render at rest: PARK visible in the zone, secondary ink, on
   the stops' baseline; notch lines absent; score + end-wall
   lines present.
2. PARK open: PARK in on-control ink; the four stop words
   unselected, as today.
3. Press-and-hold the zone: PARK strikes to primary ink within
   the frame, same as a stop press.
4. Cap travel, resistance, seat behaviour pixel-identical to
   pre-F20 renders at every other position.
5. Zone width 48dp at 360 / 390 / 412dp screen widths; word
   never touches either framing line.

---
