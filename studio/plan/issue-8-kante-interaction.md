# ISSUE #8 — KANTE: INTERACTION DETAIL FOR THE DRAWER

*Kante, interaction-detail specialist, dedicated-100 programme.
Ground: this cell's built `DrawerScreen.kt` (wave-2 state), the
wave-2 report's 30 seats, AUDIT-drawer, DRAWER-REVIEW. Paper only —
nobody compiles. Fonts/palette law untouched; no new colour, no
new hue, nothing green.*

*What the existing seats settled and I do NOT re-open: the Drawer
is motionless (Finisher Motion), view switches are instant cuts,
no sound is invented (Finisher Sound/Haptics), filing words retire
to print on declaration. What nobody specced — because nobody was
seated for it — is the physics of the touches and the scroll that
DO exist: six `clickable`s with stock Material ripple, and a
`LazyColumn` running stock fling with no state hoist. A shelf you
scroll and rows you press are interactions; stock defaults are
not a spec. This file is that spec.*

---

## K8-1 · RIPPLE DIES EVERYWHERE IN THIS ROOM (PATCH-READY)

Every pressable in `DrawerScreen.kt` — filing words, StoredRow,
shelf month rows, EarlierRow, the three BACKs — uses bare
`Modifier.clickable { }`, which carries the Material ripple:
a radial ink wash spreading from the touch point over ~300ms.
That is a phone-UI effect on a paper ledger, and it is the one
piece of visible "animation" in a room whose motion law is zero.

Cure, uniform: every clickable in this file takes
`interactionSource = remember { MutableInteractionSource() }`,
`indication = null`, and the press is answered by the ink-step
in K8-2 instead. One shared `remember` per pressable, no other
change. Cost: ~15 min for the file. If any ripple survives in
this screen, it is a defect on sight.

## K8-2 · THE PRESS IS AN INK STEP — 0ms IN, 0ms OUT (PATCH-READY)

The room has no tweens, so the press cannot tween either. The
press feedback is a step in ink density, driven by
`interactionSource.collectIsPressedAsState()`:

| Pressable | At rest | While pressed (DOWN→UP/cancel) |
|---|---|---|
| StoredRow title | Atkinson Medium 14sp, onBackground | same glyph, **onSurfaceVariant** |
| Shelf month row | Atkinson Medium 14sp, onBackground | **onSurfaceVariant** |
| EarlierRow | Anonymous Bold 11sp, onBackground | **onSurfaceVariant** |
| BACK texts | Atkinson Bold 12sp, onBackground | **onSurfaceVariant** |
| Filing words | Atkinson Bold 11sp, onSurfaceVariant | **onBackground** (inverse — see below) |

Rules:
- The step lands in the same frame as DOWN and restores in the
  frame of UP/cancel. No duration, no easing — a step. The eye
  reads "the print darkened under my finger," which is what
  pressing a stamp onto paper does.
- Filing words step UP in ink (variant → full), everything else
  steps DOWN (full → variant). Reason, mechanical: the words are
  the room's one live ask; pressing one is an act of writing, so
  the ink gets *stronger*. Rows/BACK/EARLIER are navigation —
  the print yields under the finger. Two directions, one rule:
  writing darkens, travelling lightens.
- Only the pressed element steps. The row's meta line, hairlines,
  and neighbours do not move, dim, or scale. Press travel in dp:
  **0dp, forever** — nothing in this room translates.
- This also closes the pre-cut press problem: shelf rows and
  BACKs change view on the click (UP), so without K8-2 the press
  is never seen at all — the cut simply happens. With the step,
  DOWN paints the ink step, UP cuts the view. The finger gets
  its receipt before the room changes.

## K8-3 · SHELF SCROLL WEIGHT — THE LEDGER IS HEAVY (PATCH-READY)

As built, both `LazyColumn`s (list :332, detail :209) pass **no
state and no fling behaviour**: stock decay, stock overscroll
glow/stretch, and scroll position destroyed on every view switch
(the wave-2 saveable cure saved view name + selected ID, not
scroll). Spec:

1. **State:** one `rememberLazyListState()` per view — LANDING,
   MONTH, SEARCH, DETAIL — hoisted beside the existing
   `rememberSaveable` view state. Returning from DETAIL restores
   the list's exact scroll offset; entering MONTH starts at 0
   (a shelf you open starts at its top — that one is a door, not
   a return).
2. **Fling decay — heavier than stock.** Custom `FlingBehavior`
   over `exponentialDecay<Float>(frictionMultiplier = 1.45f,
   absVelocityThreshold = 120.dp/s in px)`. Measured intent: a
   full-velocity fling (~4,000 px/s) travels ≈ **1.6 viewport
   heights** and is under 100 px/s within ≈ **650ms**, vs stock
   ≈2.5–3 heights / ~1s. A ledger page is thumbed, not bowled:
   one hard flick = one shelf-and-a-half, never the whole archive
   blurring past. Drag itself stays 1:1, unmodified — weight
   lives in the decay only.
3. **Overscroll: none.** No glow, no stretch, no bounce at
   either end (`overscrollEffect = null` on the columns /
   `LocalOverscrollConfiguration provides null` on the versions
   that still read it). The shelf ends the way a paper ledger
   ends: the last row arrives and the page stops. A rubber-band
   on an archive is a claim that there is give in the record.
   There is none.
4. **No snap, no scrollbar, no scroll sound, no scroll haptic.**
   Rows rest wherever the decay leaves them; the 1dp hairlines
   are the only grid.

## K8-4 · FILING-WORD TARGET GEOMETRY — CLOSE THE MIS-FILE GAP (PATCH-READY)

The wave-2 Feel Touch+Sound seat ruled the words' row gap
16 → 24dp and vertical padding 6dp; the built `FilingDayLine`
still ships `spacedBy(16.dp)` + `padding(vertical = 6.dp)` and
**no horizontal padding** — text-edge to text-edge, ILL and REST
sit ~16dp apart with glyph-box targets. Final geometry:

- Each word: `padding(horizontal = 8.dp, vertical = 10.dp)`
  inside the clickable → felt target ≈ **(word width + 16dp) ×
  ~35dp** (11sp Atkinson Bold line ≈ 15dp + 20dp padding).
- Row `spacedBy` drops to **8dp**: with 8dp padding per side,
  adjacent target edges touch exactly and glyph edges stand
  **24dp** apart — the ruled separation, achieved by target
  padding instead of dead space, so no point on the row is
  untappable air between two filings.
- Mis-file physics: at 24dp glyph separation and 35dp target
  height, a thumb centred on ILL cannot overlap REST's target.
  This is the room's only write; its aim geometry is priced
  like one.

## K8-5 · SEARCH RULE — THE FOCUS STEP CLOSES THE REMAINDER (PATCH-READY)

The wave-2 remainder ("rule focus-brightening, ~20 min at
merge") is specced here so it cannot drift: track focus with
`onFocusChanged` on the entry line (one Boolean). While focused:

- the 1dp bottom rule steps `outlineVariant → onSurfaceVariant`
  (0ms step, per K8-2's no-tween law);
- the cursor stays `SolidColor(onSurfaceVariant)` as built —
  cursor and rule now agree at focus, which is the whole point;
- on blur both return in one frame. No other element reacts.
  The placeholder does not move, shrink, or float — this is a
  ledger line, not a Material field.

## K8-6 · ONE PRICED OVERTURN, FLAGGED — A FILING TICK (FOR REN/LADE TO RATIFY)

Finisher Sound/Haptics ruled: no new haptic, the Drawer stays
silent. I do not smuggle past that ruling; I price its exception
and put it to the lead, because filing is the room's only
**write** and every other write in this app is felt (the seal,
the keys in PARK). If ratified, exactly this and nothing more:

- One haptic tick at the declaration commit only (the frame the
  word retires to print): `VibrationEffect.createOneShot(9,
  36)` — 9ms at amplitude 36/255, i.e. *below* PARK's night
  knock (15ms @ 15 strike is longer; this is shorter and
  softer than any voice in the app). No acoustic half, ever —
  a sound would break the ruling outright; a felt tick does not
  make the room click, it makes the *hand* know the record took.
- Clearing a word (tap the active word) gets the identical tick
  — an edit is a write in both directions.
- Nothing else in the Drawer vibrates: not rows, not EARLIER,
  not BACK, not scroll ends. If the tick is refused, K8-1..K8-5
  stand complete without it — the room's press physics is the
  ink step, with or without the tick.

## ACCEPTANCE, BY FINGER AND EYE

1. Press any row, shelf month, BACK, EARLIER: no ripple ring
   anywhere; the pressed print steps ink in the DOWN frame and
   back in the UP frame; the view cut (where one follows) comes
   after the step is visible.
2. Fling the landing hard: the list travels ≈1.6 screens and
   dies inside ~650ms; at both ends it stops dead — no glow,
   no stretch, no bounce.
3. Open a stored task from mid-list, go BACK: the list is at
   the same offset, the row you opened under your thumb.
4. Filing zone: thumbnail ILL — REST's target never overlaps;
   the word steps to full ink while pressed; on release the
   line is print.
5. Focus the search line: rule steps up one ink tier in one
   frame, back on blur; nothing else on the page changes.

— Kante, interaction detail
