# REVAMP-INTERACTION — the launcher's press, haptic and dial spec (4 seats)

**For:** UMBAU (lead builder, launcher revamp, branch `agent/design-revamp`)
**Scope:** the new block-screen launcher ONLY — full-screen night surface,
hero dial, two door plates (ALLOWED APPS / PEEK APPS) opening app grids,
OPEN tap = launch, PEEK tap = raises the existing Held Door.
**Machinery untouched, by order:** the Held Door's 5 s breath + peek-offer
path, the Emergency Break valve (60 s hold, once a week), the Guard-line
entry, the Airlock plates, session end. This file specifies *feel* —
press physics, haptics, dial rendering — never new blocking behaviour.
**Sources:** `WAVE2-SCOUTS-1.md` finds #1 (Composition vocabulary), #2
(≤3 events/flow, Clear > Rich > Buzzy, same-frame rule), #9 (densifying
hold — governs the valve, NOT the launcher), #12 (spring + shape-morph),
#15 (four-state depth grammar). Code grounding: `MonolithCurves.kt`
(duration vocabulary 110/140/180/250/260/280/420/520, EasingA),
`MonolithKin` (PRESS 90 ms, TAB_IN 240 ms / 14 dp), night tokens in
`Color.kt` (#171412 ground, #362C27 raised, #4A423C sheet/cap, #100E0C
recessed, #F47442 accent, #FFE4CF cream), `minSdk = 24`, `targetSdk = 36`.

**How to build from this file:** §0 is normative — one set of numbers,
build exactly those. §§1–4 are the four seats' signed records behind §0:
each seat states the same build-critical numbers (no seat contradicts
§0) plus the detail only that seat owns. Where a seat dissented during
drafting, the dissent and its resolution are printed in its section.

**The three laws every number below obeys**

1. **Same frame (find #2).** Haptic, spring and (where present) sound
   start in the same frame. A haptic dispatched even ~50 ms after its
   visual reads as broken. Implementation consequence: haptics fire
   from the press-state change itself, never from a delayed effect.
2. **Budget (find #2).** ≤3 meaningful haptic events per flow. Silence
   is a scored event-class: DENIED = 0, the dial = 0, the Held Door's
   breath = 0 launcher events (its own machinery, untouched).
3. **One progress float (finds #12 + #15).** Scale, corner radius,
   elevation, translation and highlight alpha for a control are ALL
   lerped from a single `pressProgress` (0 = rest, 1 = fully pressed)
   driven by one spring. They can never desync, because there is only
   one animated value per control.

---

## §0 — CANONICAL SPEC (normative)

### 0.1 Press choreography — door plates (ALLOWED APPS / PEEK APPS)

Plates are the heaviest controls in the app: full-width doors, not
buttons. HEAVY spring, no bounce worth the name.

| Property | Rest (RAISED) | Pressed (TRAVEL) | Δ / note |
|---|---|---|---|
| Spring | `SpringSpec(dampingRatio = 0.86f, stiffness = 260f, visibilityThreshold = 0.001f)` | same spring drives in AND out | one spring, both directions |
| Time to 90 % travel | — | ≈ 85 ms | matches `MonolithKin.PRESS_MS = 90` |
| Settle (within 1 % of target) | — | ≈ 240 ms in, ≈ 260 ms out | release overshoot peaks at scale 1.004, never more |
| Scale X | 1.000 | 0.970 | −3.0 % |
| Scale Y | 1.000 | 0.945 | −5.5 % — non-uniform squish sells mass |
| Corner radius | 20 dp | 27 dp | +7 dp morph, from `pressProgress` |
| Elevation (shadow) | 6 dp | 1 dp | −5 dp collapse |
| Shadow alpha (key shadow) | 0.35 | 0.10 | ambient shadow scales with the same ratio |
| Translation Y | 0 dp | +3.0 dp | the plate travels INTO the panel |
| Top highlight (1 dp inner top stroke) | alpha 0.16 | alpha 0.03 | light flattens as the surface seats |
| Bottom bevel | base | +8 % darker | the lower edge loads |
| Size / touch | height 76 dp, full width − 16 dp margins each side, 12 dp gap between plates | unchanged | touch target never shrinks with the visual |

Depth grammar (find #15), plate lifecycle: **RAISED** at rest →
**TRAVEL** under the finger → **SEATED/inset** for the 120 ms commit
beat (elevation 0 dp, radius held at 27 dp) → grid transition runs.
DISABLED state (a plate with zero apps — should not occur; if it does):
flat, elevation 0 dp, no highlight, no spring, no haptic.

Grid-open transition (after SEATED beat): grid content translates
14 dp → 0 and fades in over 240 ms on `MonolithCurves.EasingA`
(identical to `MonolithKin` TAB_IN: 240 ms / 14 dp). The plate's own
screen does the reverse on close: 160 ms, accelerating out (THROW).

Answer on **DOWN**, commit on **UP-inside**: the spring starts on
`awaitFirstDown` (the finger must feel the plate move within one
frame); the action commits on release only if the release point is
inside the plate bounds expanded by 8 dp slop. Release outside =
cancel: spring returns, flow ends, no second haptic (see §0.3, the
down-CLICK already spent is the flow's only event).

### 0.2 Press choreography — app tiles (OPEN grid and PEEK grid)

Tiles are small, frequent, and must feel like keys, not doors.
SNAPPY spring, visible overshoot on release — the opposite pole
from the plates, deliberately.

| Property | Rest (RAISED) | Pressed (TRAVEL) | Δ / note |
|---|---|---|---|
| Spring | `SpringSpec(dampingRatio = 0.58f, stiffness = 850f, visibilityThreshold = 0.001f)` | same spring both directions | |
| Time to 90 % travel | — | ≈ 55 ms | |
| Settle | — | ≈ 130 ms in, ≈ 210 ms out | release overshoot peaks at scale 1.030 |
| Scale X / Y | 1.000 | 0.920 / 0.920 | uniform — a key travels straight down |
| Corner radius | 16 dp | 23 dp | +7 dp morph, same `pressProgress` rule |
| Elevation (shadow) | 4 dp | 0 dp | full collapse — the key bottoms out |
| Shadow alpha | 0.30 | 0.06 | |
| Translation Y | 0 dp | +2.0 dp | |
| Top highlight | alpha 0.12 | alpha 0.02 | |
| Cell | 84 dp tall incl. label, 4-column grid, icon 40 dp, label Atkinson Hyperlegible 12 sp beneath the well | touch target ≥ 64 × 64 dp | icon well is the visual; the cell is the target |

Commit difference (the ONLY difference between the two grids' tiles —
physics are identical):

- **OPEN tile, on commit:** a 1 dp accent edge (#F47442) lights around
  the well for 120 ms, then the launch hand-off fires. The tile never
  goes inset — it is leaving, not seating.
- **PEEK tile, on commit:** the tile goes **SEATED/inset** (elevation
  0 dp, well fill drops to recessed #100E0C, radius held 23 dp) and
  STAYS inset while the Held Door raises over 320 ms. When the door
  seats, the launcher is gone; the inset tile is the last thing seen.
  This is find #15's SEATED state used as a promise: *this one is
  being held for you.*

Guard-line entry tap (not a plate — a line has no body): no scale.
The line thickens 2 dp → 3 dp and its alpha pulses 0.75 → 1.0 over
110 ms (`DURATION_110`), spring-free, tween on EasingA. That is all.

### 0.3 Haptic score — the whole launcher

Vocabulary: `VibrationEffect.Composition` primitives only (find #1).
`CLICK` = `PRIMITIVE_CLICK`, `THUD` = `PRIMITIVE_THUD`,
`TICK` = `PRIMITIVE_TICK`, `LOW_TICK` = `PRIMITIVE_LOW_TICK`,
`SLOW_RISE` = `PRIMITIVE_SLOW_RISE`, `QUICK_FALL` =
`PRIMITIVE_QUICK_FALL`. Scale is the primitive's float scale
(0.0–1.0). Every event is dispatched in the same frame as its visual
commit (Law 1). Events are counted per flow (Law 2).

| Flow | # | Event | Primitive | Scale | Fires when (frame-exact) |
|---|---|---|---|---|---|
| F-ENTER — Guard-line tap (sealed) or Airlock plate → launcher opens | 1 | 1 event | CLICK | 0.70 | the frame the launcher surface becomes visible |
| F-PLATE — door plate press → grid opens | 1 | down-commit | CLICK | 1.00 | the frame `pressProgress` crosses 0.35 on the way down (≈ scale 0.989) — NOT on raw touch-down, NOT on release |
| | 2 | grid land | THUD | 0.60 | the frame the grid transition settles (240 ms mark) |
| F-OPEN — tile tap → app launches | 1 | down-commit | CLICK | 1.00 | `pressProgress` crosses 0.35 (tile) |
| | 2 | hand-off | QUICK_FALL | 0.80 | the frame the launch intent is dispatched |
| F-PEEK — tile tap → Held Door raises | 1 | down-commit | CLICK | 1.00 | `pressProgress` crosses 0.35 (tile) |
| | 2+3 | door raise + seat (ONE composition, 2 primitives) | SLOW_RISE @ 0 ms, then THUD @ +300 ms | 0.70 / 0.80 | composition dispatched the frame the door raise starts; THUD lands as the door seats at ≈ 320 ms |
| F-BACK — grid closes → launcher face | 1 | 1 event | LOW_TICK (fallback TICK, see ladder) | 0.70 (TICK: 0.50) | the frame the close transition starts |
| F-END — session ends while launcher is up | 1+2 | ONE composition | QUICK_FALL @ 0 ms, then THUD @ +90 ms | 0.90 / 0.70 | the frame the session-end state lands |
| F-DENIED — tap on a withheld / empty target | — | **0 events, by law** | — | — | visual only: the existing withheld reason prints; no buzz, ever (find #2: buzzy-or-nothing → nothing) |
| Hero dial (all flows) | — | **0 events** | — | — | no per-second, per-minute or rollover ticks on the face; the dial speaks in F-END only |
| Emergency Break valve | — | **out of budget** | — | — | the valve's existing densifying schedule (find #9) governs; the launcher adds exactly 0 events to it |
| Cancelled press (release outside slop) | — | no further event | — | — | the down-commit CLICK already fired is the whole spend |

Worst flow in the launcher is F-PEEK at exactly 3 events. Every other
flow is ≤2. There is no fourth event available to any future edit
without breaking Law 2 — that is the point of printing the budget.

**Composition shapes (Kotlin, normative):**

```kotlin
// Single-primitive event (F-ENTER, F-PLATE ×2, F-OPEN ×2, F-PEEK #1, F-BACK)
VibrationEffect.startComposition()
    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 0)
    .compose()

// F-PEEK door raise — one call, two primitives, 300 ms internal delay
VibrationEffect.startComposition()
    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.70f, 0)
    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.80f, 300)
    .compose()

// F-END — one call
VibrationEffect.startComposition()
    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.90f, 0)
    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.70f, 90)
    .compose()
```

**The fallback ladder — named `LauncherHaptics` Three-Rung Ladder.**
`minSdk = 24`; Composition exists only on API 30+. Every score entry
resolves through the ladder at call time; the score above is written
against Rung A and degrades, never disappears silently:

| Rung | Condition | Mechanism | Per-primitive mapping |
|---|---|---|---|
| **A — COMP-30** | API ≥ 30 AND `Vibrator.arePrimitivesSupported(primitive)` true for every primitive in the event | `VibrationEffect.Composition` as scored | as scored |
| **B — PREDEF-29** | API = 29, or any single primitive unsupported on API ≥ 30 | `VibrationEffect.createPredefined(...)` for the WHOLE event (never mix rungs inside one event) | CLICK → `EFFECT_CLICK`; THUD → `EFFECT_HEAVY_CLICK`; LOW_TICK/TICK → `EFFECT_TICK`; SLOW_RISE → `EFFECT_DOUBLE_CLICK`; QUICK_FALL → `EFFECT_HEAVY_CLICK`. The F-PEEK and F-END two-beat shapes survive as `EFFECT_DOUBLE_CLICK` (PEEK) / `EFFECT_HEAVY_CLICK` (END) — one predefined call, timing internal to the effect |
| **C — WAVE-24** | API 24–28, or Rung B unavailable | API 26–28: `VibrationEffect.createWaveform(timings, amplitudes, -1)`; API 24–25: `Vibrator.vibrate(longArrayOf(...), -1)` (amplitudes ignored by the platform) | CLICK = 12 ms @ amp 200 · THUD = 28 ms @ 255 + 12 ms @ 90 tail · TICK = 6 ms @ 120 · LOW_TICK = 6 ms @ 80 · SLOW_RISE = 30 ms @ 90 → 50 ms @ 150 → 70 ms @ 230 (150 ms total) · QUICK_FALL = 22 ms @ 230 → 10 ms @ 80. Two-beat events concatenate with the scored delay (300 ms / 90 ms) as a 0-amplitude segment |

Capability is probed ONCE per process (cache the rung per primitive
set), never per tap. System haptic setting is respected: if the
device's touch-haptics are off at the OS level, the launcher is
silent — no workaround, no escalation (find #2).

Names for UMBAU: one object, `LauncherHaptics`, exposing
`enter()`, `plateDown()`, `plateLand()`, `tileDown()`, `openHandoff()`,
`peekRaise()`, `back()`, `sessionEnd()` — call sites never name a
primitive. Tests run against a fake-vibrator seam recording
(rung, primitive/effect, scale, delay) per call, in the pattern of
find #1's `SealHaptics` proposal: if the seam records a 4th event in
any flow, the test fails.

### 0.4 What §0 deliberately does NOT do

- No haptic on the dial's sweep, ever — a ticking clock you can feel
  is a pager, not a machine (find #2, buzzy class).
- No sound specified here. Sound is a separate open ruling (Q6/#19
  territory); nothing in this file rules on it either way.
- No change to Held Door timing, peek duration, cooldown, or the
  valve. Feel only.

---

## §1 — CRAZY-IDEAS seat

*Remit: the mechanism nobody else would propose, priced honestly.
Press and haptics first (my share of the bundle), then the one wild
steal.*

### 1.1 My press numbers

I argued for heavier plates in drafting — stiffness 200, damping
0.92, scale Y 0.930 — and lost on evidence: at 0.92 the return takes
~340 ms to settle, which reads as *lag* on a door you open a dozen
times a session, and lag is the one feel this revamp cannot afford.
I sign §0.1/§0.2 as written (plates 260 / 0.86, tiles 850 / 0.58).
My surviving contributions to the canonical numbers, which I own:

- **The +7 dp radius morph on BOTH controls.** Drafting started at
  +4 dp. +4 is visible only if you stare; +7 is felt in peripheral
  vision while looking at the label — which is where the finger's
  owner is actually looking. Radius is the cheapest mass cue we have;
  spend it.
- **Non-uniform plate squish (X 0.970 / Y 0.945).** A uniform 0.97
  reads as *zoom*. Squishing Y harder than X reads as *weight* —
  the plate compresses vertically like something with a gasket.
- **PEEK tile stays inset (§0.2).** My proposal, adopted: the last
  frame before the Held Door is a tile visibly *seated into* the
  panel. It turns a transition into a handover.

### 1.2 My haptic lines

I sign §0.3 whole. My additions, both adopted into the score:

- **F-DENIED = silence was my fight.** The obvious design buzzes an
  error at a withheld peek. Find #2's law — buzzy or nothing, choose
  nothing — plus one observation: a denied buzz *trains* the tap
  (it is still feedback, still a response). Silence + the printed
  reason is the only denial that teaches nothing except the reason.
- **F-PEEK is the only 3-event flow, on purpose.** If any flow earns
  the full budget it is the one that ends at the Held Door — the
  launcher's most consequential door. Everything else stays at ≤2
  so F-PEEK's third event (the seating THUD) still means something.

### 1.3 THE WILD MECHANISM — Gravity Light (one, priced, follow-up only)

**Steal: one light source for the whole launcher, pointed by gravity.**

Every machined object in the real world is lit by ONE light. Our
plates, tiles and dial are currently lit by *convention* — a fixed
135° highlight baked into each control separately. Gravity Light
replaces the convention with physics: a single virtual light whose
direction comes from the device's rotation-vector sensor, low-pass
filtered, clamped to ±18° of tilt. Tilt the phone and the specular
edge on the dial's bezel, the plates' top highlights and the tiles'
wells ALL slide together, by the same vector, in the same frame.
Nothing moves except the light. The launcher stops being three
drawings and becomes one object on a table, in a hand.

Why it fits this app specifically: Monolith's whole claim is
*machine, not screen*. Machines are recognised by how light moves
across them when they move. This is the single cheapest effect that
produces that recognition, and it costs zero blocking behaviour —
pure surface, fully removable.

**Price, honest:**

- **Size: M — ≈ 1 to 1.5 builder-days, one follow-up commit, ~180
  lines.** One `TiltLightSource` (SensorManager
  `TYPE_ROTATION_VECTOR`, `SENSOR_DELAY_UI` throttled to 30 Hz,
  low-pass α = 0.12, axis remapped for display rotation) exposing a
  `StateFlow<Offset>` light vector; three consumers (dial specular,
  plate highlight offset, tile highlight offset) reading it in their
  draw phase only — no recomposition per sensor event.
- **What it touches:** draw code only. No DB, no session state, no
  machinery, no permissions (rotation vector needs none).
- **Failure surface, stated plainly:** (a) sensor absent (some
  tablets/emulators) → vector locks at the static 135° default and
  the launcher is EXACTLY as §0 ships — the fallback is the current
  design, not a degraded one; (b) foldables and rotated displays
  need the axis-remap tested on-device, budget half the estimate
  for that alone; (c) it MUST unregister when the launcher is not
  visible (DisposableEffect lifecycle) or it is a battery leak with
  good PR; measured draw cost is nil, sensor cost at 30 Hz is
  negligible, but an unregistered listener is forever.
- **Accessibility gate, non-negotiable:** system reduce-motion /
  animator-off → Gravity Light is OFF, static light, no setting of
  ours re-enables it. Light that moves when the user has asked the
  OS for stillness is a defect, not a feature.
- **What it is NOT:** not a gesture, not an unlock, not information.
  It carries no state. If it ever starts meaning something, it has
  gone wrong.

UMBAU does not build this in the revamp commit. The launcher ships
correct under a static light; Gravity Light lands after, as its own
commit, and can be reverted as one commit if it reads as a gimmick
on Sarthak's phone. That reversibility is part of the price.

— *Crazy-ideas seat* ✍

---

## §2 — MAXIMUM-IMPLEMENTATION-POTENTIAL seat

*Remit: the fastest honest path from spec to shipped feel — what to
build first, what to cache, what never to allocate. My press and
haptic numbers are §0's; this section is about WHY they are cheap
and how the dial stops looking drawn.*

### 2.1 My press numbers — and their implementation shape

I sign §0.1/§0.2. The numbers were chosen to be implementable as
ONE mechanism, not three:

```kotlin
// Per control: one Animatable, one spring, five lerps.
val press = remember { Animatable(0f) }
LaunchedEffect(pressed) {
    press.animateTo(if (pressed) 1f else 0f,
        if (isPlate) LauncherMotion.HeavySpring else LauncherMotion.SnappySpring)
}
val p = press.value
// scaleX = lerp(1f, 0.970f, p)  scaleY = lerp(1f, 0.945f, p)   (plate)
// radius = lerp(20.dp, 27.dp, p); elevation = lerp(6.dp, 1.dp, p)
// translationY = lerp(0.dp, 3.dp, p); highlightAlpha = lerp(0.16f, 0.03f, p)
```

- `LauncherMotion.HeavySpring = SpringSpec<Float>(0.86f, 260f)`,
  `LauncherMotion.SnappySpring = SpringSpec<Float>(0.58f, 850f)` —
  add both to `MonolithCurves.kt` beside the existing vocabulary;
  do NOT reuse `specA/B/C` tweens for press (tweens cannot retarget
  mid-flight without a velocity jump; springs can, and a press IS
  a mid-flight retarget every time a finger lifts early).
- Haptic trigger without a side-channel: fire the down-commit event
  from a `snapshotFlow { press.value }` collector that fires once
  when the value crosses 0.35 upward while pressed. One collector
  per control type, disposed with the control. This guarantees
  Law 1's same-frame property structurally: the haptic is a
  function of the same animated value the draw phase is reading.
- Zero allocations in the draw path: all `Brush`es, the dial `Path`
  (§2.3) and shadow paints are `remember`ed on (size, palette).
  Springs allocate nothing per frame. If a profiler shows press
  jank, it will be shadow rendering — the fix is `shadowElevation`
  on a `graphicsLayer`, never a hand-rolled blur.

### 2.2 My haptic lines

I sign §0.3, including the ladder. Implementation notes I own:

- Probe once: `LauncherHaptics` resolves its rung in `init` per
  primitive-set and caches it; a tap must never call
  `arePrimitivesSupported` on the UI thread path per event.
- Rung discipline: an event resolves to exactly ONE rung. Mixing
  (CLICK from Composition + THUD from waveform inside F-PEEK)
  produces two different motor voices in one gesture — worse than
  either rung alone.
- The fake-vibrator seam is the deliverable's test: record
  (method, rung, scale, delayMs) tuples; assert per-flow counts
  (ENTER 1, PLATE 2, OPEN 2, PEEK 3, BACK 1, END 2, DENIED 0) and
  assert no event fires on dial recomposition. These are pure-JVM
  tests against the seam — no device needed to prove the budget.

### 2.3 THE DIAL — machined, not drawn (fastest path)

The hero dial fails as "drawn" for three concrete reasons: flat
strokes, a highlight that rotates WITH the progress (painted-on
light), and per-frame recomposition of the numeral layer. The fix
is a 3-layer structure in ONE composable, ≈ 150 lines, no new
dependencies, no bitmap assets:

**Layer 1 — static face, drawn once per (size, palette) change.**
One `Canvas` via `Modifier.drawWithCache`: inside the cache block,
build and retain (a) `tickPath` — all 60 minute ticks as ONE `Path`
(60 ticks: every 5th is 10 dp long / 2.5 dp wide, others 5 dp /
1.5 dp), each tick drawn TWICE into the path's companion strokes:
the dark engraved line, plus a 0.75 dp highlight line offset toward
the fixed light — engraving is a dark cut with a lit edge, and that
second stroke is the entire difference between a tick and a notch;
(b) the bezel: two concentric strokes (outer 2 dp #4A423C, inner
1 dp black @ alpha 0.55); (c) the groove: a full 12 dp stroke arc
in recessed #100E0C that the progress arc will sit IN; (d) the
fixed specular: a `Brush.linearGradient` (white @ 0.10 → transparent
at 55 %) clipped to the dial circle at a FIXED 135°. It does not
rotate, does not follow progress — light belongs to the room, not
the ring. Per frame, Layer 1 costs one `drawPath` + three
`drawArc`/`drawCircle` calls with cached brushes. No allocations.

**Layer 2 — progress, draw-phase state only.** The sweep is
computed from the deadline, never accumulated:
`fraction = (endsAtMillis − now) / sessionDurationMillis`, written
by a `withFrameMillis` loop into a `MutableFloatState` that is read
ONLY inside the draw block. Reading snapshot state in draw gives
draw-phase invalidation — the arc redraws at display rate with
ZERO recomposition. Draw order per frame: progress arc (7 dp stroke,
`StrokeCap.Round` OFF — machined ends are butt-cut: `StrokeCap.Butt`,
centred in the 12 dp groove), then its specular edge (the same arc,
1.5 dp stroke, offset −2.5 dp in Y, white @ 0.18, clipped to the
progress sweep only). Geometry: start angle −90° (12 o'clock),
sweep = 360° × fraction, depleting clockwise-end-first as fraction
falls. Two draw calls per frame. That is the whole dynamic cost.

**Layer 3 — numeral + intention, recomposed at 1 Hz.** The
remaining-time numeral (Anonymous Pro, 64 sp, tabular figures) and
the intention line live in a separate composable driven by a
seconds-ticking state. The arc sweeps at 60 fps; the numeral steps
once per second; neither invalidates the other. A dial whose
numeral recomposes the ring is how you get jank that looks like
cheapness.

**Escape hatch, priced and NOT taken now:** if profiling on a real
device shows Layer 1 above ~1 ms/frame, promote the static face to
a cached `ImageBitmap` rendered once into an offscreen canvas in
the same cache block (per-frame cost becomes one `drawImage`).
Half a day. Take it only on a measured number, never on a guess.

Acceptance for "machined": tilt the phone (mentally — Gravity Light
is §1.3's follow-up, not this commit) — no: the test is simpler.
Screenshot the dial at 90 % and at 10 % remaining. If the light,
the ticks and the bezel are pixel-identical and only the arc +
numeral changed, it is machined. If anything else moved, it is
drawn. Fix until the screenshots pass.

— *Maximum-implementation-potential seat* ✍

---

## §3 — USER-EXPERIENCE seat

*Remit: whether a tired person at hour nine of a session can work
this launcher without thinking. My numbers are §0's; I own the
conditions around them.*

### 3.1 My press numbers — the conditions that make them usable

I sign §0.1/§0.2, with these binding conditions:

- **Plates live in the lower two-thirds.** The dial owns the top;
  the doors sit where the thumb already is. A 76 dp plate whose
  centre is below screen mid-height, every time, no exceptions for
  "balance". Balance is a poster value; reach is a use value.
- **Plate labels carry counts, set in Sora SemiBold 15 sp caps:**
  `ALLOWED APPS · 6` / `PEEK APPS · 3`. A door that doesn't say
  what's behind it makes the user open it to find out — an extra
  flow, an extra temptation surface, for zero information gain.
  Counts update from the same session state as the grids; a stale
  count is a lie and fails review like any other.
- **Tiles are recognisable at arm's length:** 40 dp icon, app name
  in Atkinson Hyperlegible 12 sp (never truncated to "…": shorten
  the grid to fewer columns before truncating a name — a user
  hunting a truncated name is a user reading every tile).
- **The dial is NOT a tap target.** It displays: progress ring,
  Anonymous Pro remaining numeral, the session's intention line
  beneath it (Atkinson 13 sp, one line, set at arm time). Taps on
  the dial do nothing, visibly and haptically (F-DENIED silence
  applies). A display that sometimes acts is a trap.
- **Exits stay exactly where the machinery put them:** Emergency
  Break valve and session end, nowhere else, no new affordance
  invented by this revamp. The launcher adds doors to apps; it
  adds NO doors out of the session.

### 3.2 My haptic lines

I sign §0.3. The UX reading of the score, for the record:

- The score is learnable in one session because it is a *grammar*,
  not a list: **CLICK = you touched a control and it registered;
  THUD = something heavy landed (a grid, a door, the session's
  end); TICK-class = small navigation (back); QUICK_FALL = you
  left (launch, session end); SLOW_RISE = a door is being raised
  for you.** Five meanings, never reused across meanings. A user
  who can predict the THUD at the Held Door's seat has understood
  the machine.
- **Accessibility, binding:** (a) TalkBack focus order is dial
  (as one labelled display: "2 hours 14 minutes remaining,
  intention: …") → ALLOWED plate → PEEK plate → tiles in reading
  order; every plate and tile carries a real content description
  ("Allowed apps, 6 apps", "Peek PhonePe" — never "button");
  (b) reduce-motion (OS animator scale 0) collapses both springs
  to a 120 ms fade with scale locked at 1.000 and radius static —
  the haptic score is UNCHANGED (haptics are not motion);
  (c) no control sits inside the system-gesture insets; plate
  bottom margin ≥ 24 dp above the gesture bar zone;
  (d) haptic intensity is never the only channel: every haptic
  event in §0.3 has a simultaneous visual event by construction
  (Law 1 cuts both ways).
- **The 9 pm test:** every flow above must be completable eyes-
  half-closed: ENTER (1 tap) → PLATE (1 tap) → tile (1 tap) =
  3 taps, 5 haptic events total across the whole journey, zero
  decisions. If a builder's change adds a tap or a confirmation
  anywhere in that path, it comes back to this seat before it
  ships.

— *User-experience seat* ✍

---

## §4 — INTERACTION-DETAIL SPECIALIST seat

*Remit: buttons, 3D treatment, vibration, peak perfection — the
last 5 % that separates machined from merely good. I hold the
pen on §0's numbers; this section is the fine print UMBAU will
be held to.*

### 4.1 Press choreography — the fine print

Canonical numbers: §0.1 (plates HEAVY 260 / 0.86; squish X 0.970 /
Y 0.945; radius 20 → 27 dp; elevation 6 → 1 dp; travel +3.0 dp) and
§0.2 (tiles SNAPPY 850 / 0.58; scale 0.920; radius 16 → 23 dp;
elevation 4 → 0 dp; travel +2.0 dp). The detail underneath them:

- **Four-state depth grammar (find #15), tokenised — build these
  as ONE token set, `LauncherDepth`, never per-screen recipes:**
  RAISED = key shadow (offset Y +3 dp, blur 6 dp, black @ 0.35) +
  ambient (offset Y +1 dp, blur 2 dp, black @ 0.18) + top highlight
  stroke 1 dp @ 0.16. TRAVEL = both shadows scale by (1 − p) toward
  the §0 pressed values on the SAME `pressProgress`; highlight
  fades to 0.03. SEATED = elevation 0, well fill = recessed token,
  INNER shadow instead (offset Y −1 dp, black @ 0.25, drawn inside
  the clip) — a seated control is lit from its rim, not its face.
  FLAT (disabled) = no shadow, no highlight, fill = ground.
  Light direction is 135° (from upper-left) for every shadow and
  highlight in the launcher, including the dial (§2.3). Two light
  directions in one room reads as two rooms.
- **The press answers on DOWN or it is broken.** First visible
  movement within 1 frame (≤ 16 ms at 60 Hz, ≤ 8 ms at 120 Hz) of
  touch-down. If a build answers on UP, reject it without discussion
  — that single decision is the difference between a key and a link.
- **Interruption is the normal case.** A second finger, a palm, a
  lift at 40 % travel: the spring retargets from current value AND
  current velocity (Compose `SpringSpec` does this; a tween does
  not — this is why §2.1 forbids tweens here). Test it: press,
  release at half-travel, press again mid-return. Any visible
  velocity step = fail.
- **120 Hz honesty:** the springs are frame-rate independent
  (physics, not per-frame increments). Do not "optimise" them into
  duration-based animations for high-refresh devices.
- **Peak detail — the seat beat:** on commit, before the grid
  transition starts, the plate holds SEATED for exactly 120 ms
  (§0.1). Shorter and the THUD has nothing to land on; longer and
  it reads as delay. 120 ms is the difference between *the door
  seated, then opened* and *the button was slow*. Guard this
  number in review; it will be "optimised" by someone. Don't let it.
- **Radius morph direction:** radius INCREASES under pressure on
  both controls (+7 dp). A pressed thing spreads; a spreading
  thing's corners round. The reverse (squarer under load) reads
  as a UI glitch, not physics. (Find #12's morph, direction ruled.)

### 4.2 Haptic score — the fine print

Canonical score: §0.3, ladder included. My held pen notes:

- **Scales are ceilings, not targets.** CLICK at 1.00 is reserved
  for genuine commits (plate down, tile down). F-ENTER's 0.70 and
  F-BACK's LOW_TICK 0.70 exist so the two 1.00s keep their meaning.
  If everything is 1.00, nothing is. Any future edit raising a
  scale must name which existing 1.00 it is demoting.
- **THUD discipline:** THUD appears exactly three times in the
  whole launcher (plate land 0.60, door seat 0.80, session end
  0.70) — always mass arriving, never departure, never error.
  QUICK_FALL owns departures. The moment a THUD means two things,
  the vocabulary (find #1) is dead and we are back to buzzes.
- **Motor variance is real:** THUD on a weak motor reads as a long
  buzz. The ladder's Rung A gate (`arePrimitivesSupported`) is
  therefore not optional polish — on devices returning false, the
  PREDEF/WAVE rungs' shorter, crisper shapes are the *better*
  rendering, not a degraded one. Do not force Composition where
  the hardware says no.
- **No haptic without a witness:** every event in the score fires
  alongside a visual change in the same frame (Law 1). An orphan
  vibration — motor fires, screen didn't move — is the single most
  "cheap Android app" sensation there is. The fake-vibrator seam
  tests (§2.2) plus one on-device pass (press everything, watch
  AND feel simultaneously) close this. The on-device pass is not
  optional: haptics cannot be verified from code alone, and this
  seat will not sign a build it has only read.
- **The valve stays sacred and separate:** the Emergency Break's
  densifying schedule (find #9) is the launcher's neighbouring
  instrument, not part of this score. Zero launcher events are
  added to it, and its schedule is never borrowed for launcher
  flows — a user must never feel the valve's language anywhere
  except the valve.

### 4.3 Sign-off conditions (all four seats concur)

1. §0 built as printed; deviations return to the seat that owns
   the number (detail: §4, dial: §2, wild follow-up: §1, flows: §3).
2. Fake-vibrator budget tests green (counts per §2.2).
3. Dial screenshot test green (§2.3: 90 % vs 10 %, only arc +
   numeral differ).
4. On-device feel pass: same-frame check on every §0.3 event,
   interruption test (§4.1), reduce-motion pass (§3.2).
5. Gravity Light (§1.3) NOT in the revamp commit.

— *Interaction-detail specialist seat* ✍

---

*Bundle delivered to UMBAU via the repo. No app code in this file's
commit — spec only, per brief.*
