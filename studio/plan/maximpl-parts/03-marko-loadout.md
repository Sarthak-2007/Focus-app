# 03 — MARKO (Maximum-Implementable, coordinator Voss) — THE LOADOUT BAND + FLOW KEY
## PLAN WAVE — plan only, no code

**Element:** `RombiKnobView` — the STUDY / DEEP / SLEEP selector band — plus the FLOW key
on the Deck, whose skirt/sink is the band's sibling latch and is specced here at §4.
**Hosts today:** Shield only (`ShieldMatrixScreen.kt` SECTION 1, `AndroidView`, 84dp).
This plan mounts it on the Deck too, under one shared state (§6).
**Wave-1 base:** `maximpl/03-loadout-cap.md` — carried forward except where a section
below says CHANGED, with the reason. Three wave-1 numbers are dead on arrival in the
current tree and are re-derived here: the night plate/floor tokens (Round 1a moved
`tokSurfaceVariant` night to `#100E0C`, *below* ground), the marker token (Round 0
landed it as `tokOnControlFill()`, which drifts at night — §5), and the seat bar's
colour (wave-1 spent `tokAccent()`; the settled budget is Shield ZERO orange — §5).

His day, priced into this element: the band is set by the rested man in the evening.
At 03:30, fasted, arming the 05:00 block, the cap is already seated where he left it —
the tired man never pays travel time on the arm path. SLEEP is chosen in the dark
(~19:30–20:00), so the night marker is not an edge case; it is the stop's home case.
Saturday tests change nothing here: STUDY is STUDY.

---

## Mechanism

**The band is a milled plate with a fader in it. Three stops, one cap, no continuous
value.** `stopFracs = {0.05, 0.50, 0.95}` — kept. The loadouts are three discrete
machines; the control never reports a fraction as a value.

**Geometry (host 104dp, up from 84dp — wave-1 §1, kept):**

| Part | Spec |
|---|---|
| Host height | **104dp**, full width, both hosts (§6) |
| Mechanical axis | `trackY = h × 0.47` |
| Track span | `trackLeft = 30dp`, `trackRight = w − 30dp` |
| Numeral row | baseline `trackY − 26dp`, Anonymous Pro |
| Slot zone | slot `trackY ± 5dp`; cap centred on `trackY` |
| Name row | baseline `trackY + 34dp`, Atkinson Hyperlegible |
| Seat bar | `trackY + 40dp`, 18 × 2.5dp, under the selected name only (§5) |
| Cap | **56 × 32dp, radius 8dp** — CHANGED from wave-1's 58×36 r10: 8dp is the frozen key radius, and 56×32 puts the cap in the same family as the FLOW cap (56×32) and Yuki's sled caps (54×30). Ratio 1.75:1 — wider than tall, obviously a handle. At stop 0 on a 312dp view the cap's left edge lands ≈12dp inside the plate: no clipping, slot ends stay visible past the cap at both extremes — the range is always readable. |

**The plate.** Fill `tokSurfaceVariant()` + `tokHairline()` 1dp stroke, radius
`radiusSmall` (12dp). Recess pair (the recipe `SledSwitchView` in the same file
already uses on its tracks): top inner shade `0x2E000000`, 1.4dp, inset 12dp per
side at `top + 1.4dp`; bottom inner light catch `0x2EFFFFFF`, 1.4dp at
`bottom − 1.4dp`.

**Slot bed** — a shallow well the slot sits in: `trackLeft − 14dp … trackRight + 14dp`,
`trackY ± 11dp`, radius 10dp. Tonal steps, numbers not adjectives:

| Layer | Day | Night |
|---|---|---|
| Screen ground | `#E4DFDA` | `#171412` (staircase base) |
| Plate (`tokSurfaceVariant`) | `#D8D1CB` — one step below ground | `#100E0C` — **1.50:1 below ground** (recess *below* ground, per the rebuilt staircase; the old `#272320` plate sat *above* ground and is dead) |
| Slot bed | black `0x14` over plate → effective ≈ `#BFB7B0` | black `0x48` (72/255) over plate → effective ≈ `#0B0A08` — **≈1.55:1 below plate** |
| Slot floor | `tokBlockBar()` `#2E2A26` (solid) | black `0xB8` (184/255) over bed → effective ≈ `#050404` — the darkest thing in the band |
| Travelled floor | white `0x14` over floor, stop-0 centre → marker x | same `0x14` |
| Detent notch | black `0x59` over floor | black `0x59` over floor |

**CHANGED — night floor is not `tokBlockBar()`.** Wave-1 §3 specced night floor =
`tokBlockBar()` `#161311` over a `#272320` plate: a hole darker than its plate.
With the Round 1a plate at `#100E0C`, `#161311` is *lighter* than the plate — the
hole would invert and read as a raised bar. The floor is therefore a black-alpha
fill composited over the bed (one fill, no new token, deterministic result
`#050404`). Day keeps the solid token because day's plate is light and `#2E2A26`
is genuinely below it. Every adjacent night step above is ≥1.35:1, as locked.

Bed closed by its own light logic: 1dp `0x2E000000` on its top inner edge, 1dp
`0x2EFFFFFF` on its bottom inner edge (both modes; at night these read on the bed,
not the floor).

**Slot body.** `trackY ± 5dp`, radius 5dp (full-round ends are the travel limits).
Inner top shadow 1.6dp `0x66000000` at `trackY − 3.6dp`, inset 5dp from the ends;
floor light catch 1dp at `trackY + 4dp` — `0x1AFFFFFF` day (the day floor blows out
at full alpha), `0x2EFFFFFF` night.

**Travelled floor.** From stop 0's centre to the cap's marker x, the floor is
overlaid `0x14FFFFFF`. This is not a second rendering of progress: it is the cap's
own wake in the slot, a pure function of marker position, drawn in the same pass.
It carries no number and no state the position doesn't already carry.

**Detent notches.** At each stop: rect `sx − 1.5dp … sx + 1.5dp`, `trackY ± 5dp`,
`0x59000000` over the floor. Three holes waiting; the seated cap covers its own
notch — the catch is felt (§Haptics) at the exact frame the hole disappears.

**Printed scale.** Majors: through-slot tick at each stop, `trackY ± 7dp`, 1.4dp;
selected stop's tick in `tokTextPrimary()`, others `tokTextSecondary()`. Minors:
3 per inter-stop span (¼, ½, ¾), *below* the slot on the bed, `trackY + 8dp …
+ 11dp`, 1dp wide, decorative tier (day `tokTextSecondary()`; night the 3.5:1 tier
`#776A60`). No numerals on minors — hierarchy by length and tone only.

**Drag engagement (the guardrails are the mechanism):**
- DOWN anywhere on the plate is the control's hit area. Drag engages only after
  **8dp of horizontal-dominant movement** (|Δx| > 8dp and |Δx| > 1.5 × |Δy|);
  before that threshold the gesture belongs to the host's vertical scroll.
- On engagement: `parent?.requestDisallowInterceptTouchEvent(true)` — the pattern
  `RotaryWheelView` and `Emergency60sValveView` already use — released on UP/CANCEL.
- The cap is **grabbed, not teleported**: DOWN records `grabOffset = event.x − capCx`
  and the cap never jumps to the finger. DOWN inside the cap rect ±6dp slop = grabbed
  (grab feel, §Haptics); DOWN elsewhere on the plate = armed tap / potential drag
  with the offset preserved from the cap's current centre.
- Second pointer: ignored entirely. The first pointer owns the gesture to UP/CANCEL.

---

## Motion (curve + ms)

All durations from `MonolithCurves` {180, 260, 280, 420, 520}; all interpolators
from the same file. No new durations, no new curves.

**Drag (finger owns the cap):**
- Follow is **1:1**: `rawFrac = ((event.x − grabOffset) − trackLeft) / trackW`,
  drawn every MOVE, no animator, no easing — the hand is the curve.
- **Magnetic detents:** capture radius **0.045 frac** (≈12–13dp on a full-width
  track). Inside it, `drawn = stop + (raw − stop) × 0.55` — the cap visibly
  hesitates in the groove without the finger stopping.
- **Rubber-banded end walls:** beyond `[0.05, 0.95]`, excess × **0.25**, clamped
  to **±0.04 frac** (≈11dp) past the end stop. (Harder than the wheel's ×0.32:
  a fader's wall is a wall, not a rim.)
- Drawn frac is clamped `[0.01, 0.99]` in `onDraw` regardless of source — the
  invariant is structural, not hopeful.

**Release / tap / programmatic travel:**

| Case | Duration | Curve | Notes |
|---|---|---|---|
| Drag-release snap, distance ≤0.12 frac | 180ms | CurveA (0.2,0,0,1) | Already in the groove's mouth; a click-shut |
| Drag-release snap, >0.12 frac, landing on an end stop | 260ms | CurveA | Firm take-back; walls never overshoot |
| Drag-release snap, >0.12 frac, landing on DEEP | 280ms | MechanicalOvershoot (1.25) | Excursion ≤0.045 frac past DEEP, pulled back into the notch |
| Tap, adjacent stop, landing on an end stop | 280ms | CurveB (0.05,0.7,0.1,1) | Quick throw, long settle |
| Tap, adjacent stop, landing on DEEP | 280ms | MechanicalOvershoot (1.25) | Same distance, groove landing |
| Tap, two-stop jump (STUDY↔SLEEP) | 420ms | CurveB | Distance earns duration; passes DEEP with a tick (§Sound), never overshoots — the destination is a wall |
| Programmatic set (host/routine-driven, idle only) | as tap, by distance | as tap | Same table; the view does not care who asked |
| Cancel return | 260ms | CurveA | No overshoot, no seat feel on arrival |
| Retarget mid-travel | re-derived from remaining distance | per new case | Animator cancels, restarts from live `capFrac` |

**Overshoot ruling — wave-1 CONFIRMED with one exception.** Wave-1 allowed
`MechanicalOvershoot` only into the middle stop. Confirmed: end stops are walls,
and a wall hit must *stop* (CurveB's hard attack + the seat click is the wall).
Exception added: a drag-release landing on DEEP from ≤0.12 frac gets **no**
overshoot (180ms CurveA). Reason: overshoot honours momentum the hand actually
gave the cap; inside 0.12 frac there is no travel momentum to honour, and an
overshoot there would push the cap *out* of a groove it is already entering —
motion lying about physics. Tap arrivals keep overshoot at any distance because
a tap *is* a throw: the 280/420ms travel is the momentum.

**Pass-through:** in the animator update listener only (drag has its own scrub
ticks): crossing `stopFracs[1]` with neither endpoint at DEEP fires the DEEP
detent once per animation (`passedDetentFired`, reset in `animateCapTo`).
While `|capFrac − 0.50| < 0.03` during travel, DEEP's major tick draws in
`tokTextPrimary()` — a one-frame lamp confirming what the ear just heard.

**State sequencing (the wave-1 lie, killed):** `selectedLevel`, the typography
flip, and `onLevelChanged` all commit **at the seat** — animator end, or
snap end — never at DOWN, never at UP. During travel the departing stop stays
typographically selected. Position and type never contradict each other for a
single frame.

**Reduced motion:** system animator scale 0 → `capFrac` set directly, seat
feel + apply fire immediately. The sequencing (seat → apply) survives; only
the glide is cut.

### §4 FLOW key (Deck) — skirt travel, sunken ON

The synthesis craft call stands: FLOW keeps, with skirt travel + sunken ON state.
This section fixes the numbers; it adopts Latch's (Improvers) ladder rungs 2–3
geometry and resolves the voicing to the existing engine (§Sound).

| Property | OFF (timed) | ON (FLOW latched) |
|---|---|---|
| Touch box | 72 × 48dp | same |
| Well | 62 × 40dp, r11, one tonal step below panel, 1dp shadow line top-inner / 1dp light-catch bottom-inner, no outer border | same |
| Cap | 56 × 32dp, r8, `offsetY = −1dp` (proud of the well rim), skirt **3dp** | 56 × 32dp, r8, `offsetY = +2dp`, skirt **1dp** |
| Cap face | crown: two-stop vertical gradient of existing tokens (day `#FAF6F0 → #E4DFDA`; night `#362C27 → #100E0C`) | **flat** `inkControlFill()` (day `#2C2A29`, night `#4A423C`) — a sunk key catches no crown light; gradient removed, not dimmed |
| Legend | Anonymous Pro Bold 12sp, letterSpacing 1.5sp, panel ink | same size, `onInkControlFill()` cream |
| Marker line | absent (layout slot reserved — legend never jumps) | **12 × 2dp**, inset 4dp below cap top, cream, revealed as the cap sinks |

- **Skirt travel: exactly 3dp of cap travel** (−1dp → +2dp) with the skirt
  collapsing **3dp → 1dp** (a 2dp silhouette change). Depth delta ON vs OFF:
  the face drops 3dp *and* goes from one step above the panel (crown-lit) to
  the well floor in ink — position, silhouette, and value all say "latched";
  any one survives a desaturated 25%-scale glance.
- **Motion:** press-in **80ms CurveA/EasingA**; latch settle (skirt finish +
  marker reveal, marker delayed 40ms into it) **140ms CurveA**; unlatch rise
  **200ms MechanicalOvershoot (1.25)**, overshoot capped at **+0.5dp** past
  rest; fill/gradient crossfade **120ms CurveA**, synced to travel — the key
  darkens *as* it sinks, never before or after. No scale, ever.
- **Toggle is real:** tapping ON restores the {preset, dial, blocks} captured
  at engagement (Latch rung 1 — a single `toggleFlow()` entry point). A sunken
  key that cannot rise is a trap with better paint; the sink does not ship
  without the unlatch. The current two-setter click in `TactileDeckScreen.kt`
  (preset + `onDialMinutesChanged(5)`) also double-voices the tap through the
  ViewModel (latch + phantom wheel detent) — the single entry point kills both
  the trap and the double voice. One tap, one owner, one sound.

---

## Haptics (per beat: ms + amplitude)

Engine waveforms are quoted verbatim from `TactileSoundEngine.kt`. All band
beats route through `fireFeel(ms, freq)` in `BlockitCanvasView`; FLOW routes
through the engine directly (reason in §Code). No-engine fallback for every
beat: the existing `fireFeel` one-shot at that beat's ms, `DEFAULT_AMPLITUDE`,
unchanged.

| Beat | Call | Waveform timings (ms) | Amplitudes (0–255) |
|---|---|---|---|
| Grab cap (DOWN inside cap ±6dp) | `fireFeel(4L)` → `playMechanicalLatch(true)` | [0, 11, 26, 9] | [0, 215, 0, 150] |
| Scrub crosses a stop (nearest-stop changes in MOVE) | `fireFeel(1L, 500 + idx×90)` → `playRotaryDetent` | [0, 7, 5] | [0, 180, 70] |
| Animated pass-through of DEEP | `fireFeel(1L, 590)` | [0, 7, 5] | [0, 180, 70] |
| End-wall entry in drag (once per entry; re-arms on exit) | `fireFeel(4L)` → `playMechanicalLatch(true)` | [0, 11, 26, 9] | [0, 215, 0, 150] |
| **Seat on commit** (animator/snap end at a new stop) | `fireFeel(8L)` → `playFirmClick()` | [0, 13, 20, 8] | [0, 225, 0, 135] |
| Tap on the already-selected stop / drag released back home | `fireFeel(1L, stop pitch)` | [0, 7, 5] | [0, 180, 70] |
| Cancel return arrival | — | none | none (the machine took its cap back; silence is the receipt) |
| FLOW engage | `playMechanicalLatch(true)` | [0, 11, 26, 9] | [0, 215, 0, 150] |
| FLOW release | `playMechanicalLatch(false)` | [0, 9, 22, 10] | [0, 165, 0, 195] |

Rules: never seat + detent for the same arrival (the firm click's second segment
*is* the seat); MOVE fires only on a nearest-stop bucket change (the
`RotaryWheelView` bucket pattern — at 0.45-frac spacing no time throttle and no
hysteresis are needed); a scrub STUDY→SLEEP produces exactly two detent ticks
(DEEP crossing, SLEEP approach) plus one seat.

---

## Sound (primitive + pitch)

| Beat | Primitive | Pitch / shape |
|---|---|---|
| Grab | `playMechanicalLatch(true)` transient | 760Hz, 16ms, gain 0.54, chord 1.2 |
| Scrub detent | `playRotaryDetent(freq)` — **the only pitched path** (`fireFeel` 1L branch) | 500 / 590 / 680Hz by stop idx, 6ms, gain 0.38 |
| Pass-through DEEP | `playRotaryDetent(590)` | DEEP's own pitch, heard mid-flight |
| End-wall entry | `playMechanicalLatch(true)` transient | 760Hz, 16ms, gain 0.54 — a dull mechanical stop, same family as the grab: metal stopping the hand |
| Seat | `playFirmClick()` | 620Hz, 12ms, gain 0.5, chord 1.1 |
| Same-stop tap / return home | `playRotaryDetent(stop pitch)` | 500 / 590 / 680Hz, 6ms, gain 0.38 |
| FLOW engage | `playMechanicalLatch(true)` | 760Hz, 16ms, gain 0.54, chord 1.2 — sharp snap in |
| FLOW release | `playMechanicalLatch(false)` | 520Hz, 16ms, gain 0.54, chord 0.9 — lower, heavier seat out |

**FLOW voicing — conflict resolved here, against Latch's rungs 2–3.** Latch
proposed a dedicated haptic pair ([5,25,9] @ [150,0,210] / [9,20,5] @
[170,0,130]) and a 190Hz/330Hz synth pair. Not adopted. The existing latch
primitive already encodes direction twice — pitch (760 engage / 520 release)
and seat amplitude (150 engage / 195 release) — and this plan wave mints no new
engine primitives for any element. If device tuning later proves the pair
indistinguishable, that is a tuning finding against the engine, filed then —
not a reason to fork the vocabulary now. Pitch is never passed to the 4L/8L/10L
branches of `fireFeel`: it is honoured only on 1L, and every call site above
respects that (the current band code's `fireFeel(8L)` with no pitch is already
honest; wave-1's dead pitch ladder on the seat stays dead).

No other sounds. No press sound on the plate outside the cap, no apply fanfare,
no error sound — there is no error state.

---

## Depth & light

**Draw order (band), back to front:** plate + recess pair → slot bed + its edge
lines → travelled floor → slot floor + inner top shadow + floor light catch →
notches → major/minor ticks → numerals + names + seat bar → shadow pool → cap
body layers → specular band (inside cap clip) → grip grooves → marker line.
Zero allocation: pre-allocated paints/RectFs only; any `paint.alpha` use is
restored to 255 immediately after.

**Shadow pool — two layers, lagging (wave-1 §5.1, kept with numbers):**
`poolCx = capCx − (capFrac − prevCapFrac) × trackW × 0.6`, updated per frame
before `invalidate()`; at rest the term is 0.
- Soft pool: rect `poolCx ± (capW/2 + 2dp)`, `trackY − capH/2 + 4dp … + capH/2 + 4dp`,
  radius 9dp, `0x2E000000`.
- Contact core: rect `poolCx ± capW/2`, offset **+2.5dp** y, radius 8dp, `0x38000000`.
- While dragging: both y-offsets grow **+1.5dp**, core alpha drops to `0x2E000000`
  — the cap lifts off the plate in the hand. Nothing else on the cap changes in
  motion; a real fader cap doesn't morph, its shadow betrays its speed.

**Cap construction (on `tokControlFill()` — day `#252323`, night `#4A423C`, the
locked fill cap; material comes from layering on it, never from changing it):**
1. Body: flat `tokControlFill()`, radius 8dp.
2. Specular band (Dario's Travelling Specular rig, consumed — see below).
3. Bottom shade: bottom 5dp strip overlaid `0x18000000`.
4. Lit top edge: `0x59FFFFFF`, 1.1dp, at `top + 1.1dp`, inset **10dp** per side.
5. Side bevels: 1dp verticals, middle 60% of height, inset 1dp — left
   `0x30FFFFFF`, right `0x33000000`. One light source, obeyed on all four edges.
6. Grip grooves: two pairs flanking the marker at `capCx ± 11dp` and `± 15dp`,
   `trackY − 8dp … + 8dp`, 1.2dp wide — dark groove `0x40000000` with a 1dp
   `0x26FFFFFF` twin offset +1dp in x (cut + catch). They say "push sideways"
   in peripheral vision and keep the marker's field singular.
7. Marker line on top (§below).

**CHANGED — the static crown sheen is deleted.** Wave-1 §5.2 overlaid the top
40% of the body with `0x14FFFFFF` *and* this plan adds Dario's travelling band:
two light systems on one cap is two light sources, and the rig's whole claim is
one source, fixed in space. The static crown goes; the lit top edge line (a
machined edge, not a sheen) stays.

**The specular rig, as this cap consumes it** (Mara's rig, Dario's build):
18dp-wide linear band inside the cap's clip, drawn over the body and bottom
shade, **under** the grooves and marker; peak alpha **14% day / 8% night**
cream-white, result clamped so the night cap never lifts past `#4A423C`.
**Bounded form — the one disagreement with the rig as specced, mailed to Dario:**
the rig's cumulative offset (`−0.35 × travelFromRest`) is priced for sled travel
(~50dp). The band's travel is 0.90 frac ≈ 250–290dp; cumulative offset at SLEEP
would be −88…−100dp — the band would sit entirely outside a 56dp cap, leaving
the cap unlit at exactly the stop chosen in the dark. This cap therefore
consumes the rig in neighbourhood form:

> `bandLocalOffset = clamp(−0.35 × (capCx − nearestSeatCx), −8dp, +8dp)`, zero at every seat.

During travel the highlight leans and lags exactly as the rig intends; at rest
it is identical at all three stops — which the rig's own law requires: light
must never carry data, and a per-stop rest offset *is* position rendered a
second time. The clamp lives at this cap's consumption point; the wheel and
sled rig is untouched. If Dario's build disagrees, his counter goes in his
file and the clamp number — not the principle — is what moves.

**Marker line — constant cream `#FBF1EC`, both modes.** 3dp, `Paint.Cap.ROUND`,
`trackY − 11dp … + 11dp` (22dp in a 32dp cap, 5dp clear each end). Spec target
is the **literal constant**, not a token: `tokOnControlFill()` (what the current
code calls) returns `#FBF1EC` by day but `#FFE4CF` at night — close, warm, and
not the constant the synthesis legislated (item 7). `tokOnPrimary()` is worse:
night `#1B110D`, a ~2:1 dark slit on the `#4A423C` cap — wave-1's groove reading,
dead. One line, one cream, day and night: `#FBF1EC` on `#252323` ≈ 15:1, on
`#4A423C` ≈ 8.6:1. It is the brightest mark in the band in both modes, which is
the marker's whole job.

**Typography hierarchy (real paint pairs only — the fonts now ship true weights):**

| | Selected | Unselected |
|---|---|---|
| Name | `paintWord` (Atkinson **Bold 700**), **13sp**, `tokTextPrimary()` | `paintWordSub` (Atkinson Regular 400), **11sp**, `tokTextSecondary()` |
| Numeral | `paintMono` (Anonymous Pro **Bold 700**), **11sp**, `tokTextPrimary()` | `paintMonoSub` (Anonymous Pro Regular 400), **9sp**, `tokTextSecondary()` |

- Night tiers are the locked staircase tiers: primary `#D2C6BB` ≈ **11:1**,
  secondary `#9C8E84` ≈ **5.8:1**, decorative (minors only) `#776A60` ≈ **3.5:1**.
  No alpha-dimming of night text anywhere in this spec — Fader's alpha-140
  night trick was priced against the old `#F1E8E6` secondary and is obsolete
  against `#9C8E84`.
- Day: primary `#2C2A29`, secondary `#615B5A`.
- `letterSpacing = 0.06` on both word paints for the name row only, reset after
  the loop (paints are shared within the view; this view draws nothing else
  with them). Printed-plate caps, not UI text.
- `isFakeBoldText` is **never** set in this view. The current code sets it on
  `paintMono` for the selected numeral — with the real Bold file shipped, that
  is a smear laid over a true weight. It goes.

**Selected name's indicator — the accent conflict, resolved.** Wave-1 §4 drew
the seat bar in `tokAccent()` and called it "the band's single accent instance."
That was under the old budget. The settled budget (synthesis item 12) is
**Shield ZERO orange**, and the brief for this wave repeats it: the selected
state is ink/position, never accent. Resolution: **the seat bar survives, the
orange dies.** The bar (18 × 2.5dp under the selected name at `trackY + 40dp`)
is drawn in **`tokTextPrimary()`** — day `#2C2A29`, night `#D2C6BB` — the same
ink the selected name itself is printed in. It is the name's own underline, in
the grammar `FilterWord` already uses lower on Shield, not a lamp and not a
second fact: position says which stop, the name row says it in words at the
margin, the bar binds the word to the position. Nothing else in the band — cap,
marker, numerals, ticks, FLOW key — touches accent in either host. On the Deck,
the Deck's one orange stays where it already lives: the slide. Zero added.

---

## State machine

States of `RombiKnobView` (one instance per host, one shared state source — §6):

| State | Entered by | Cap | Type / callback | Exit |
|---|---|---|---|---|
| **SEATED** | start; seat completed | at `stopFracs[selectedLevel]`, pool centred | selected stop bold/primary; bar under it | DOWN |
| **PRESSED** | DOWN on plate | at seat; pool offsets +0 (no lift yet) | unchanged | MOVE ≥8dp horizontal-dominant → DRAGGING; UP → tap resolution; CANCEL → SEATED |
| **DRAGGING** | engagement threshold crossed | 1:1 finger, magnetised, rubber-banded at walls | departing stop stays selected; **no callback, nothing applied** | UP → SNAPPING; CANCEL → RETURNING; vertical exit >24dp beyond plate → RETURNING |
| **TRAVELLING** | tap on another stop; programmatic set while idle | animator per Motion table | departing stop stays selected until seat | animator end → APPLYING; DOWN → cancel + PRESSED (retarget) |
| **SNAPPING** | UP from DRAGGING | release animator per Motion table | unchanged until end | end at new stop → APPLYING; end at home stop → SEATED (return tick only) |
| **RETURNING** | CANCEL, or vertical-exit abort | 260ms CurveA home | unchanged throughout | end → SEATED, no feel |
| **APPLYING** | animator/snap end on a new stop | at seat | **one frame:** seat feel fires, typography flips, `selectedLevel` commits, `onLevelChanged` fires, `contentDescription` updates | → SEATED |
| **LOCKED** | session ARMING (slide gate crossed) or RUNNING, either host | frozen at the sealed loadout | frozen; touch swallowed, returns true, **no feel, no travel, no callback** | session end → SEATED (queued programmatic change, if any, now travels) |

Rules that bind every transition:
- `selectedLevel` changes in exactly one place: APPLYING. The host's `update`
  block re-setting the same value is a no-op (the current setter already
  guards on equality — kept).
- The touch path commits through a private `commitLevel()` that animates only
  the *remainder* — the setter's programmatic animation must never re-travel
  distance the hand already covered.
- Passing DEEP selects nothing. Only a seat selects.
- Accessibility: on APPLYING, `contentDescription = "Loadout, <NAME>, <n> of 3"`
  and `performClick()` from the seat path. TalkBack hears the word at the same
  frame the eye sees the seat — never per passing stop.

---

## Edge cases

- **Mid-arming loadout change (HOLD law):** the slide's payload is captured at
  its gate crossing. From that frame the band is LOCKED in both hosts: a drag
  or tap does nothing — no travel, no feel, no queued change. A gesture that
  started *before* the gate crossing is cancelled into RETURNING with no
  callback. The sealed session runs the loadout it sealed with. No exceptions,
  no "apply at next block" — mid-session there are no decisions, including
  this one.
- **Routine firing while a session runs:** routines are fully his and fire on
  their own schedule; a routine whose loadout differs from the sealed one does
  **not** retarget the running session. Its loadout is queued at the view level
  and travels (programmatic row of the Motion table) when the session ends and
  the band returns to SEATED. If the man changes the band by hand while idle,
  the queue is discarded — the hand outranks the schedule he wrote, and both
  are his.
- **Routine firing while idle:** programmatic travel, same motion/feel table,
  APPLYING fires the callback — the host cannot distinguish a routine's change
  from a tap's, and must not need to.
- **Change while cap is travelling:** DOWN cancels the animator and retargets
  from live `capFrac`; duration re-derived from the remaining distance. A
  programmatic set during TRAVELLING does the same. No queue of pending stops
  ever forms — latest instruction wins, because there is only ever one cap.
- **Drag released outside the plate vertically (>24dp):** RETURNING, return
  tick suppressed (that's the cancel path — no feel), no callback.
- **Host scroll stealing the gesture before engagement:** before the 8dp
  threshold the list owns the gesture and the view may receive CANCEL — handled
  as RETURNING from wherever the cap is (at seat: a no-op frame).
- **Same-stop tap:** detent tick at that stop's pitch, no animator (already
  seated), no callback. An acknowledgement, not a fake change.
- **Narrow widths:** at 312dp view width the track is 252dp; stop spacing
  113dp; cap 56dp never reaches either plate edge (≥12dp clear). Below 280dp
  view width the minor ticks drop out first (decorative tier yields); slot,
  cap, majors, and type never scale down.
- **Font scaling:** type is sp-based and the host is a fixed 104dp; at 1.3×
  font scale the name row (13sp bold ≈ 17dp cap height) still clears the seat
  bar at +40dp. Numerals and names never truncate — three fixed words, sized
  for the largest of them (SLEEP) at spec size.
- **`soundEngine == null`** (previews, host not yet wired): every beat degrades
  to the `fireFeel` one-shot fallback at its ms value. No beat is ever dropped
  silently by the view itself.
- **Night AOD:** the band is not an AOD surface and draws nothing there. The
  desk clock owns that ground (`#000000`); no loadout state is rendered on it —
  the sealed loadout is a fact the session already carries.
- **The DEEP naming debt (flagged, not fixed here):** the band prints DEEP; the
  Shield host maps level 1 → `SmartLoadout.WORK_SPRINT`; routine rows print the
  same loadout as WORK. One loadout, two names, both in his app. This spec
  prints **DEEP in both band hosts** (the band is one control with one scale —
  it cannot print DEEP on Shield and WORK on Deck), and flags the host/routine
  naming for Ren's call. Inventing a rename here would touch routines, which
  are his.

---

## Code: helps / fights

**Helps — build on these:**
- `BlockitCoreViews.kt:199` — `fireFeel`'s contract comment is honest: pitch
  honoured only on 1L. Every pitched call in this spec routes through 1L.
- `BlockitCoreViews.kt:147–172` — real typefaces, pre-allocated: `paintWord`
  (Atkinson Bold), `paintWordSub` (Regular), `paintMono` (Anonymous Pro Bold),
  `paintMonoSub` (Regular). The typography table above is four paint swaps.
- `BlockitCoreViews.kt:128` — `tokControlFill()`: day `LIGHT_PRIMARY #252323`,
  night `0xFF4A423C` — the locked cap fill, already correct in the current cap.
- `TactileSoundEngine.kt:258–311` — every primitive this spec voices already
  exists with the exact waveforms quoted: `playRotaryDetent`, `playFirmClick`,
  `playMechanicalLatch(engaged)` (both directions — FLOW release is free).
- `MonolithCurves.kt:16–30` — all five durations and all four interpolators
  used above already exist. This element needs nothing new from the file.
- `BlockitSuitcaseAndSwitchesViews.kt` in-file neighbours — `SledSwitchView`
  (CurveA 260ms throw + track recess pair), `Emergency60sValveView`
  (disallow-intercept pattern), `RotaryWheelView` in `BlockitCoreViews.kt`
  (DOWN/MOVE/UP/CANCEL + bucket-throttled detents): the drag state machine is
  assembled from patterns the codebase already trusts.
- `ShieldMatrixScreen.kt:140–164` — the host `update` block already maps
  levels → `SmartLoadout` and sets `selectedLevel`; APPLYING reuses it as-is.
- `MonolithCustomComponents.kt:439–448` — `inkControlFill()` /
  `onInkControlFill()` give FLOW its ON face/legend in Compose, matching the
  Canvas tokens exactly (day ink `#2C2A29`; night cap `#4A423C`).

**Fights — named, with the delta each one forces:**
- `BlockitSuitcaseAndSwitchesViews.kt:276–297` — `onTouchEvent` handles
  `ACTION_UP` only: no DOWN, no MOVE, no CANCEL, no disallow-intercept, and it
  returns `true` for everything, swallowing gestures it never uses. The whole
  §Mechanism drag machine replaces this handler.
- Same file, `animateCapTo` (~194) — fixed 280ms / CurveB, no parameters, no
  end listener. It gains (duration, interpolator) parameters and the seat
  listener APPLYING hangs on. Defaults keep today's single call site valid.
- Same file, `selectedLevel` setter + the UP handler — selection, callback,
  and feel all fire at tap time, up to 420ms before the cap arrives: the band
  contradicts itself for the whole travel. Fixed by the APPLYING state, not
  by moving one call.
- Same file, `onDraw` (~252) — `paintMono.isFakeBoldText = isSel` still set.
  Real Bold shipped in Round 0; the flag now smears a true weight. Deleted.
- Same file, marker draw (~269) — `tokOnControlFill()`: correct instinct
  (Round 0 killed the `tokOnPrimary` groove), wrong constant at night
  (`#FFE4CF` ≠ legislated `#FBF1EC`). Spec target is the literal constant;
  if a token is wanted, it is a new `tokMarkerCream()` returning
  `LIGHT_ON_PRIMARY` in both modes — a name delta, flagged for Ren.
- `BlockitCoreViews.kt:124, 142` — `tokSurfaceVariant()` night is now
  `#100E0C` and `tokBlockBar()` night `#161311` sits *above* it. Any wave-1
  night bed/floor code written against the old plate inverts. §Mechanism's
  black-alpha floor exists because of this pair.
- `BlockitCoreViews.kt:199` — `fireFeel(4L)` hardcodes
  `playMechanicalLatch(true)`. FLOW's release therefore cannot route through
  `fireFeel`; the Deck (Compose) calls the engine's
  `playMechanicalLatch(false)` directly. The band itself only ever needs the
  engaged shape, so the band is unaffected — but the next seat that needs a
  pitched or directional 4L will hit the same wall.
- `ShieldMatrixScreen.kt:94, 142` — the band exists **only** on Shield, and
  `selectedLoadoutIndex` is a `remember` *inside* that screen. §6's Deck
  instance requires the state hoisted above both screens; two per-screen
  remembers would be two facts and they would diverge the first time he
  changes loadout on one screen and arms on the other.
- `TactileDeckScreen.kt:259–282` — FLOW today: one flat Box, ≈42×26dp target,
  no well/skirt/offset, one-way click calling two setters. §4 replaces the
  Box's internals and its handler; placement (footer-left, opposite its
  consequence text) and idle-only mounting are kept exactly.
- Class name `RombiKnobView` is a fossil (the rotary diamond it names is
  gone) and the testTag `rombi_knob_loadout_view` depends on it. Both stay.
  Renaming is a separate commit's vanity, not this element's budget.

### §6 Hosts — one band, two homes, one contract

The band mounts in **Shield SECTION 1** (existing, height 84dp → 104dp) and on
the **Deck, idle branch only**, directly beneath the instrument panel and above
the commit slide, height 104dp. On the Deck it is unmounted while a session
runs (the running branch already replaces the panel's controls); on Shield it
stays mounted and LOCKED (§State machine).

Identical behaviour contract in both hosts — no per-host tuning:
1. Same view, same geometry, same stops, same labels (DEEP on both), same
   level → `SmartLoadout` mapping (STUDY / WORK_SPRINT / SLEEP).
2. Same state: one hoisted `selectedLoadoutIndex` above both screens. The two
   instances are two windows on one fact; if they ever disagree, that is a
   bug in the hoist, never a "per-screen loadout."
3. Same gestures, same Motion table, same Haptics/Sound tables, same
   APPLYING-at-seat sequencing.
4. Same LOCKED behaviour from the slide's gate crossing to session end.
5. Same zero-orange rule: the Deck instance spends none of the Deck's accent;
   the slide keeps the screen's one orange alone.

**Dependencies flagged (not decided here):** Guard's fate is OPEN — this spec
is indifferent: nothing in the band moves, resizes, or re-voices if Guard
dissolves into Shield or survives as a destination. Ledger's fate is OPEN —
the band writes no record and reads none; if the Ledger is kept, the session's
sealed loadout reaches it from the session, never from the band. The banked
hold is not this element: the band never touches the valve, and FLOW's exit
economics (clean completion past the 20-min floor, hold-valve before it) are
the session's existing rules, untouched.

---

## Law check

- **ARM ≤ 10s — PASS.** The band adds zero steps to the arm path: the cap is
  seated by the rested man before the morning exists. A deliberate change is
  one tap (≤420ms travel, apply at seat) or one drag (~0.5s) — and it happens
  on Shield or the Deck's idle branch, before the slide is touched. FLOW is a
  parallel shortcut, not a step: one tap (~0.3s) replaces a dial drag, and a
  fumbled FLOW tap now costs one tap to undo instead of a re-dial.
- **HOLD absolute — PASS, structurally.** From the slide's gate crossing to
  session end the band is LOCKED in both hosts: no travel, no callback, no
  queued mid-session change, no decision offered. A routine firing mid-session
  cannot retarget the sealed loadout. The loadout a session sealed with is
  the loadout it finishes with.
- **TELL THE TRUTH once, in human language — PASS.** One fact (which loadout)
  rendered once as position on a printed scale; the name row is that same
  scale's margin words, flipping only at the seat so type and position never
  disagree; the travelled floor is the cap's own wake, not a progress bar;
  light carries no data (rig band identical at rest at all three stops);
  sounds and haptics fire at the event they describe — the seat click lands
  when the cap lands, not when the finger lifts. The one dishonesty found and
  left flagged, not hidden: the DEEP/WORK naming split (§Edge cases) — the
  band is consistent with itself in both hosts; the rename is Ren's call.
- **One fact, one rendering — PASS** (see above; the seat bar is the selected
  name's own ink underline, in Shield's existing FilterWord grammar — not a
  second display of the fact, and explicitly not a lamp).
- **Beauty is retention — the spend is here:** real drag with magnetic
  grooves, a lagging shadow pool, a slot with a floor you can see, a latch key
  that sinks. The band stops being three buttons wearing a fader's body.
- **Rested decides, tired executes — PASS.** Every loadout decision surface in
  this spec lives in idle/rested territory (Shield, Deck idle); every tired
  surface (arming, running, AOD) is frozen or absent.
- **His locks — PASS.** Real font weights only (Bold/Regular paint pairs, fake
  bold deleted); night staircase honoured with numbers (ground `#171412`,
  plate 1.50:1 below, bed ≈1.55:1 below plate, text 11 / 5.8 / 3.5 tiers,
  control fills capped at `#4A423C`); accent budget: **Shield zero orange —
  the wave-1 tokAccent seat bar is explicitly revoked and redrawn in ink** —
  Deck band instance also zero; marker constant cream `#FBF1EC`; no green
  anywhere in any hex or alpha above; concept NONE — no narrative, no
  ceremony, no name for the gesture beyond what the hand learns.

**Highest-impact decision in this plan:** selection commits at the seat, not
the tap — and drag exists to make that seat *earned*. Everything else (slot,
pool, specular, type) is in service of one change: the cap follows the finger,
hesitates in DEEP's groove, and the loadout applies at the frame the cap
arrives, with the click that says so. Wave-1 specced the drag; this plan kills
the apply-before-arrival lie that would have made the drag a preview of a
decision already taken.

— Marko, Maximum-Implementable (loadout)

---

## Addendum — mail received before close (05:26–05:31 IST, answered 05:35)

- **Otis (nav, thread: band-travel-math):** the nav band switch reuses this
  band's mechanism and prices travel by distance (280 / 420ms) to keep its
  passing ticks legible. Convergence confirmed both ways: this spec's Motion
  table already prices by distance for the same reason (the DEEP pass-through
  tick). Cap divergence is deliberate on both sides and logged — nav cap 30dp
  tall in a 72dp strip; this band's cap 56×32dp. Neither is to be harmonised.
- **Fader (Improvers, thread: loadout-peak-changes-your-spec):** full
  convergence on DEEP-over-WORK (his Rung 2 renames the Shield translator and
  routine editor *renderings* to DEEP — endorsed here; enum/persistence
  untouched), on the literal `#FBF1EC` marker (independently derived, same
  `#FFE4CF` drift named), on drag, the 500/590/680 ladder, and
  distance-proportional travel. **Two disagreements stand, logged for Ren:**
  (a) the seat bar — Fader rejects it at every rung as a second rendering;
  this spec keeps it in ink as part of the selected name's print (§Depth &
  light); if Ren rules it a second rendering it dies in one line and nothing
  else moves. (b) host height — Fader 88dp vs this spec's 104dp; the gap is
  downstream of the bar row and the 11sp selected numeral, and if the bar
  dies this spec re-prices to 96dp, not 88.
