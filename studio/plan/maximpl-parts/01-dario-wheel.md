# 01 — DARIO · THE WHEEL (Maximum-Implementable, PLAN WAVE)

**Seat:** Dario — wheel, Maximum-Implementable cohort (coordinator: Voss).
**Element:** `RotaryWheelView` in `ui/views/BlockitCoreViews.kt`, hosted by
`ui/screens/TactileDeckScreen.kt`.
**Wave:** PLAN — documents only, no code. Ren builds from this file without
follow-up questions; nothing below is left at TBD.
**Concept:** NONE. No narrative, no ceremony, no metaphor that costs code.
**Standards carried:** ARM ≤10s cold-open-to-sealed · HOLD absolute, zero
mid-session decisions · TELL THE TRUTH once, in human language.

**Round 0 state this spec starts from (verified in code, Oct 29):**
`wheelAngleDeg` IS wired to `onDraw`; `minutesToAngle()` returns the one
canonical frame 120°…420°; the tick ring IS the true 36 detents (5…180 step 5);
the VelocityTracker IS queried on release — but only as a one-shot projection
(`ω × 0.18s`) into `animateWheelSettle()`, never as travelling motion; drag is
still absolute `atan2` (tap opposite the thumb teleports the value); hub still
prints `BLOCKS / 2×`, fires on `ACTION_DOWN`, clamps 1…6 while the host cycles
1…4; thumb + progress arc still fill `tokPrimary()` (cream `#FFE4CF` at night);
the Deck readout still shows the BLOCK length as `MM:SS` at 84sp with the total
demoted to a 10.5sp caption. This spec is the delta from that state to target.

**Conflicts resolved up front (wave-1 / other seats vs settled law — law wins):**
1. Wave-1 hub numeral (`2×` on the cap) vs Synthesis 16 (hub = pips only) →
   **pips win.** The numeral leaves the cap in this spec.
2. Wave-1 rim numeral `5` at the start stop vs TRUTH → the start stop is FLOW
   (open flow), not 5 usable minutes. **The start stop prints `FLOW`, never `5`.**
3. Wave-1 §3.1 finger-centred "touch light" (a second light source) vs the
   one-source specular rig (§ Depth & light, law L1) → **touch light dies.**
   Drag aliveness is carried by `dragPulse` geometry, not a second lamp.
4. Rad's pitched-major / end-stop engine methods (`playWheelMajor`,
   `playWheelEndStop`) vs this cohort's charter (buildable with the engine
   as it exists) → **existing engine wins for Maximum-Implementable.** Majors
   ride the latch; the pitched major is a named engine extension, deferred,
   not silently assumed (§ Sound).
5. Rad's rejection of program arcs vs the brief + Synthesis 17 → **arcs ship**,
   on the fixed socket band, rendering wall-clock routine windows — a
   different fact from the wheel's duration scale, so the one-fact law holds.
6. Nabe's hub geometry (peak: visual 0.30R / touch 0.41R / rest annulus) vs
   wave-1's 43dp cap → **dispatch is keyed off a `hubTouchR` property, never a
   literal** (§ Mechanism M7), so either hub geometry ships without wheel
   rework. This spec ships the 43dp geometry; Nabe's peak drops in clean.

---

## Mechanism

### M0 — Host and canonical geometry

- **Host change (one):** `TactileDeckScreen.kt`, the `RotaryWheelView`
  `AndroidView` modifier `height(240.dp)` → **`height(272.dp)`**. Panel
  padding unchanged (16dp H / 18dp V). Wheel disc Ø goes 216 → 256dp.
- **Canonical angle:** `wheelAngleDeg` is display degrees, **120…420, always**
  (0° = east, clockwise positive, Canvas convention). Value arc spans 300°;
  the 60° dead sector sits at the bottom (60°…120° through south).
  `minutesToAngle(m) = 120 + (m − 5) / 175 × 300`. `faceRotation =
  wheelAngleDeg − 270` (0 when value sits at top-centre).
- **Scale constants:** 300° / 175 min = **1.7143° per minute**; 5-min detent =
  **8.571°**; 15-min = 25.71°; 30-min = 51.43°. **36 detents:** 5, 10, … 180.
  Detent *k* (0…35) sits at `120 + k × 8.571°`, value `5 + k × 5`.
- In `onDraw`: `outerR = min(w, h) × 0.47f` → **127.8 ≈ 128dp** at the 272dp
  reference height. All radii below are dp from centre at reference size;
  fractions of `outerR` are given so the spec survives other widths.

| Layer | Radius / size (reference) | Fraction / note |
|---|---|---|
| Socket outer R | 136dp | `outerR + 8dp` |
| Program band centreline | R 132.5dp, stroke 5dp (130→135) | fixed case, § M6 |
| Grip ring (rotates) | R 110 → 128dp, annulus 18dp | `0.859R → 1.0R` |
| Knurl teeth | R 123.5 → 128dp, 120 teeth @ 3° | on grip |
| Grip index line | R 112 → 124dp, width 2.2dp | on grip, at `wheelAngleDeg` |
| Scale plate (fixed) | R ≤ 110dp | `0.859R` |
| Tick outer end | R 108dp (all ticks) | lengths § M4 |
| Numerals | centred R 91.5dp | § M4 |
| Track centreline | R 78dp | slot 7dp, progress 5dp |
| Thumb | R 14dp at R 78dp | § M5 |
| Face field | R < 74.5dp | bare + micro-print |
| Hub cap (visual = touch) | **R 43dp** | `0.336 × outerR`, § M7 |
| Rest annulus (dead on DOWN) | R 43 → 55dp | § M1 |
| Fine/coarse zone boundary | **88dp** | `0.6875 × outerR`, § M1 |
| Drag outer hit-bound | dist ≤ 138dp | socket R + 2dp |

### M1 — Touch dispatch (the gate every gesture passes)

On `ACTION_DOWN`, compute `dist` from centre and dispatch exactly once;
the dispatch never changes mid-gesture (MOVE is never re-gated):

| `dist` | Dispatch |
|---|---|
| `< hubTouchR` (43dp) | **Hub press** begins (§ M7). No drag state is entered. |
| `hubTouchR … 55dp` | **Rest annulus — dead.** Return `true`, consume, do nothing. A palm landing beside the cap sets nothing and cycles nothing. |
| `55dp … 88dp` | **Fine drag** (§ M2). |
| `≥ 88dp … 138dp` | **Coarse drag** (§ M2). |
| `> 138dp` | Return `false`; the touch is not the wheel's. |
| Within 16dp of the thumb centre (any radius) | **Coarse drag** — grabbing the thumb itself is always 1:1; the thumb is the handle. This overrides the fine zone only. |

`isLockedSession == true` → every dispatch returns `false` before the table.

### M2 — Two-zone drag (delta accumulation, never absolute)

State (all primitives, preallocated, zero `onDraw` allocation):
`continuousDeg: Float`, `grabOffsetDeg: Float`, `lastTouchAngleDeg: Float`,
`dragGain: Float`, `zoneSnapMinutes: Int` (5 coarse / 1 fine),
`dragPulse: Float`, `lastDetentMinute: Int`, `lastDetentMs: Long`,
`omegaEmaDegPerSec: Float`, `endStopLatchedLow/High: Boolean`,
`wasFlow: Boolean`, `valueAtGestureStart: Int`.

- **DOWN (drag zones):** cancel `settleAnimator` + `flingAnimator`;
  `lastTouchAngleDeg = atan2` of the touch; `continuousDeg = wheelAngleDeg`
  (re-sync — grabbing mid-settle/fling brakes the wheel where it is);
  `dragGain = 1.0` (coarse) or **`0.45`** (fine); `valueAtGestureStart =
  minutesValue`; fire the grab beat (§ Haptics H1); `dragPulse` animates in
  (§ Motion).
- **MOVE:** `delta = shortestAngleDelta(newAngle, lastTouchAngleDeg)`
  (wrap-safe, ±180°); `continuousDeg += delta × dragGain`;
  `clampedDeg = continuousDeg.coerceIn(120, 420)`;
  **rubber-band display:** `displayDeg = clampedDeg + (continuousDeg −
  clampedDeg) × 0.32`, with the visual over-travel hard-capped at **±12°**
  (beyond that the display stops following; `continuousDeg` itself is capped
  at ±37.5° past the end so a long outward drag doesn't store a long return);
  `wheelAngleDeg = displayDeg` while rubber-banded, else the magnetised
  angle (§ M3).
  Value comes from `clampedDeg` **only**:
  `rawMins = 5 + (clampedDeg − 120) / 300 × 175`; snapped to `zoneSnapMinutes`
  (coarse 5, fine 1; values < 10 snap to 5 in coarse — FLOW is detent 0;
  in fine, raw 5…9 are real 1-min steps *inside* FLOW mode, see § M8);
  on change → `minutesValue`, `onMinutesChanged`, detent beats (§ Haptics).
  Angular speed for the density law: `omegaEma = 0.5 × omegaEma + 0.5 ×
  |delta| / dtSeconds` per MOVE event.
- **Fine zone honesty:** while fine-dragging, draw a ghost marker — hairline
  circle R 5dp, `tokOutline()`, 1.2dp stroke — at the *finger's* angle on the
  R 78dp track. The finger leads, the thumb follows at 0.45×; the ghost shows
  the lead/lag instead of hiding it. Coarse zone: no ghost (finger = thumb).
- **Bottom dead sector:** the delta method crosses the 60° bottom gap without
  incident — deltas stay small, the value clamps at whichever end the finger
  approaches from. No special case exists, and none is needed; an absolute
  `atan2` mapping needed one, which is why it dies.

### M3 — Magnetic lean (display only)

While dragging (not flinging), when `|clampedDeg − nearestDetentDeg| ≤ 2.2°`:
`wheelAngleDeg = clampedDeg + (nearestDetentDeg − clampedDeg) × 0.45`.
The thumb visibly leans into the notch before the value commits. Commit —
callback, haptic, sound, readout — happens **only** on detent crossing,
never on lean. Outside 2.2°, display = `clampedDeg` exactly.

### M4 — Scale plate: ticks, numerals, terminals (fixed, never rotates)

Precompute in `init`: `FloatArray`/`IntArray` of 36 detent degrees + levels.

- **Ticks (36, three levels):** outer end R 108dp for all.
  Minor (every 5, not higher level): inner R 103dp (5dp long), width 1.0dp.
  Mid (every 15): inner R 100.5dp (7.5dp), width 1.4dp.
  Major (every 30 + both terminals): inner R 98dp (10dp), width 1.8dp.
  Colour: detent ≤ live drag value → ink tier by level (`tokOnSurfaceVariant()`
  α200 minor / `tokOnSurface()` mid / `tokTextPrimary()` major); detent > value
  → `tokHairline()` (minor/mid) / `tokOutline()` (major). The plate fills with
  ink as you turn — the scale is itself a progress rendering of block length,
  at its own radius, distinct from the § M5 track.
- **Numerals — majors only, radial, mono:** strings `30 60 90 120 150 180`,
  Anonymous Pro Bold (`monoTypefaceBold`), **9.5sp**, centred at R 91.5dp on
  the tick's ray, drawn `save → translate → rotate(deg + 90°) → draw centred →
  restore` (baseline tangent, glyph tops toward centre). Colour: ≤ value
  `tokTextPrimary()`, else `tokTextSecondary()`. 6 save/restore per frame.
- **Start stop prints `FLOW`:** at 120°, in place of a `5` numeral — Atkinson
  Hyperlegible Bold (`wordTypefaceBold`), **8sp**, letterSpacing 0.12,
  `tokTextSecondary()` normally, `tokTextPrimary()` when value == 5 (FLOW is
  the current value). The scale never claims 5 minutes it does not mean.
- **Terminals:** bars at 120° and 420°, R 96→110dp, width 3dp,
  `tokTextPrimary()`. Socket index notch: fixed bar at 120° on the case,
  R 137→142dp is outside the view — instead R 130→136dp band edge, 4dp wide,
  `tokOutline()`, drawn under the program band (§ M6) at the day-origin angle
  270° — see § M6 for why the case mark lives at top, not at 120°.
- **FLOW state (value == 5):** every tick above detent 0 drops to hairline,
  numerals dim to `tokTextSecondary()` — the scale visibly empties. That
  emptying *is* the FLOW affordance; no extra badge is drawn on the plate.

### M5 — Grip ring, track, thumb

- **Grip ring rotates** 1:1 with value: every grip element is drawn at
  `angle + faceRotation`. Carries: annulus fill `tokSurfaceVariant()`; knurl
  (§ Depth & light — 120 teeth, specular-lit, phase `faceRotation mod 3°` so
  slow rotation slides teeth under the fixed light); sheen wedge (50° arc of
  the annulus at grip-local 200°…250°, brightness from the rig at its screen
  centre); index line at `wheelAngleDeg`, R 112→124dp, 2.2dp,
  `tokTextPrimary()`, round cap — the grip's only mark, on the same ray as
  the thumb across the numeral band (a broken pointer the eye completes).
  Inner edge hairline R 110dp, outer edge hairline R 128dp, 1dp each.
- **Track (fixed, on the plate):** slot arc R 78dp, stroke 7dp,
  `tokSurfaceVariant()`, 120° sweep 300°, round caps; while `dragPulse > 0`
  the slot colour lerps toward `tokHairline()` by `dragPulse × 0.5` (the
  groove deepens under load). Progress arc R 78dp, stroke 5dp,
  **`tokTextPrimary()` in both modes** (never `tokPrimary()` — cream is a
  type tone at night, not a fill), sweep = value fraction × 300°, round cap.
  Top light line: arc R 74.5dp, stroke 1dp, `0x24FFFFFF` day / `0x14FFFFFF`
  night, full 300° — the milled edge catching the rig's light.
- **Thumb:** centre on R 78dp at `wheelAngleDeg`.
  Contact shadow: circle at +2.5dp Y (+4dp × `dragPulse`), R 14dp, α `0x42`
  day / `0x66` night, rising to `0x55` / `0x77` × `dragPulse`.
  Body: R `14dp × (1 + 0.12 × dragPulse)`, fill **`tokControlFill()`**
  (day `#252323`, night `#4A423C` — the night staircase cap, honoured).
  Marker line: radial through the thumb, R 69.5→86.5dp relative (17dp),
  width 1.8dp, pip colour, round cap — the thumb *points*.
  Pip: R 4.5dp, day `tokOnPrimary()` `#FBF1EC` / night `tokPrimary()`
  `#FFE4CF` (cream is correct at glyph size, wrong at fill size).
  **FLOW:** thumb body → `tokSurfaceBright()`, pip + marker → ink
  (`tokTextPrimary()`): an inverted dot = open flow; position still carries
  the state. Progress arc drops to hairline only.
- **Zero orange on the wheel in every state, both modes.** The Deck's one
  orange COMMIT fill lives on the slide thumb and nowhere else here.

### M6 — Program arcs: today's routines on the outer ring

The wheel's duration scale (300°, minutes) and the day scale (360°,
wall-clock) are different facts and live on different bands. The arcs live on
the **fixed socket case**, outside the rotating grip, so time never rotates
with value.

- **Data source (exact):** `MonolithViewModel.routines:
  StateFlow<List<RoutineEntity>>` (`repository.allRoutinesFlow`, DAO
  `ORDER BY startMinutes ASC`). `TactileDeckScreen` does **not** receive
  routines today — the host gains two parameters, threaded from
  `MainActivity.kt`'s existing `routines` collection:
  `programWindows: List<ProgramWindow>` and `nowMinutes: Int`, passed to the
  view as properties `programWindows` / `nowMinutesOfDay` (plain fields +
  `invalidate()`, matching the view's existing property pattern).
  `ProgramWindow` is a host-side value type: `(startMin: Int, endMin: Int)`
  in today's frame, `endMin` may exceed 1440 for overnight windows.
- **Host filtering (rested-user data, rendered dumb):** include a routine iff
  `enabled == true` AND its `daysMask` has today's bit (bit =
  `(Calendar.DAY_OF_WEEK + 5) % 7`, per `RoutineSchedule.bitFor`). Overnight
  windows (`endMinutes ≤ startMinutes`, e.g. SLEEP 20:00–03:30) produce
  **two** segments in today's frame: today's own start→end+1440, and — if
  yesterday's bit is also set — yesterday's spill `0 → endMinutes`.
  Duration math reuses `RoutineSchedule.durationMinutes` (15…720 clamp);
  the view does no calendar work of its own. Paused/disabled routines are
  simply not passed — the wheel never greys a routine, because enabled-ness
  is rendered once, on Shield, where it is decided.
- **Angle map (true duration scale):** day origin = **03:30 (his wake) at
  top**: `θ(m) = 270° + (((m − 210) mod 1440) / 1440) × 360°`. **15° per
  hour, 1° per 4 minutes, everywhere, no exceptions** — a 3-hour fasted
  block (05:00–08:00) spans exactly 45°, SLEEP (20:00–03:30, 7.5h) spans
  112.5° wrapping through the origin. There are **no hour numerals** on this
  band: position against the now-line and against the day-origin notch is
  the claim; printing a second clock face would be a second rendering.
- **Case mark:** at 270° (top), a fixed notch R 130→136dp, 4dp wide,
  `tokOutline()` — the 03:30 origin. It is the band's only fixed furniture.
- **Rendering:** per segment, arc on centreline R 132.5dp, stroke **5dp**,
  butt caps, shrunk **0.5° at each end** (≈2 min) so adjacent segments keep a
  legible seam. Alpha: STUDY loadout **α 200**, SLEEP loadout **α 120**,
  any other loadout α 160 — loadout is the segment's only distinction; the
  wheel does not print routine names (names live on Shield, once).
  Colour: `tokOnSurface()` both modes. Draw order: ascending `startMin`.
  **Overlap:** segments draw at α 170 instead of their loadout alpha when
  the host flags any overlap in the day (host computes the flag while
  expanding windows); overlapped minutes composite darker — two claims on
  the same minutes *look* doubled, because they are. No second lane, no
  hiding: overlap is drawn, not resolved — resolving it is his, on Shield.
- **Now-line:** radial line R 129.5→135.5dp, width 1.5dp, `tokTextPrimary()`,
  at `θ(nowMinutes)`, with a 3dp dot at R 132.5dp on the same ray. If `now`
  falls inside a segment, that segment draws at **α 255** (the only alpha
  change state is allowed to cause here — it is emphasis of a fact already
  drawn, not a new rendering). Host refreshes `nowMinutesOfDay` every
  **30s** while the Deck is composed and idle (`LaunchedEffect` +
  `delay(30_000)` in `TactileDeckScreen`); worst-case now-line error is
  0.125°, invisible at this radius. The view owns no clock.
- **Empty day** (no enabled routine today): the band draws only its track —
  full 360° hairline circle R 132.5dp, stroke 5dp, `tokHairline()` at α 60 —
  plus the now-line and the origin notch. Absence is the rendering. No text,
  no placeholder arc, no "no routines" on the wheel (that sentence, if ever
  needed, belongs to Shield).
- **Interaction:** none. Touches landing on the band (dist 130…138dp) fall in
  the coarse drag zone and turn the wheel; arcs never consume a touch, never
  edit a routine, never fire a callback. The wheel *shows* his program; it
  never *writes* it (rested user decides — on Shield; tired user executes —
  here).

### M7 — Hub: pips only, press-and-release

- **Geometry:** cap visual R **43dp** (`0.336 × outerR`), touch R **identical**
  (43dp — the `min × 0.46 × 0.52 × 0.85` recompute in today's `onTouchEvent`
  dies; drawn radius = touch radius). All hub dispatch reads a single
  property `hubTouchR: Float` so Nabe's peak geometry (visual 0.30R in a
  0.34R recess, touch 0.41R, rest annulus to match) can replace the constants
  without touching dispatch, drag, or draw code outside the hub pass.
- **What the cap renders — and nothing else:** contact shadow (cy +2.5dp,
  α `0x26` day / `0x50` night, thinned by press, § Motion); body fill
  `tokSurfaceBright()` (day `#FAF6F0` / night `#362C27`); outer hairline
  R 43dp 1dp; step ring R 40dp 0.8dp α 150; light arc R 42.4dp, 1.2dp,
  200°→340°, alpha **derived from the rig** (§ Depth & light, D4) — day
  `0x23`, night `0x15`; three screws on orbit R 32dp at `faceRotation +
  {90°, 210°, 330°}` (body R 3.2dp `tokSurfaceVariant()`, slot 4.4dp × 1.1dp
  `tokTextSecondary()` at `faceRotation × 1.7 + i × 47°` — screws rotate with
  the grip axle and prove rotation at the centre); **four pips** in a row at
  cy +17dp, R 2dp, pitch 7dp, centred — filled = `studyBlocksCount` in
  `tokTextPrimary()`, empty = `tokHairline()`. **No numeral, no `×`, no
  `BLOCKS` word on the cap.** Count is position/quantity, readable in
  peripheral vision; the factorisation in words lives once, in the Deck
  caption (§ M8). The cap's centre screw (in the screw orbit's middle field)
  is the thumb's anchor.
- **Range:** `studyBlocksCount` clamps **1…4** in the view (today's 1…6 dies;
  the host already cycles 1…4 — view and host now agree, and the four pips
  *are* the range made visible).
- **Press semantics (fire-on-release, cancellable):**
  DOWN inside `hubTouchR` → press begins: `hubPressT` 0→1 (§ Motion), cap
  scales `1 − 0.04 × hubPressT`, shadow thins `(1 − 0.5 × hubPressT)`.
  **No callback, no haptic, no sound on DOWN.**
  UP inside `hubTouchR`, press duration ≤ 600ms, total touch travel ≤ 10dp →
  **commit:** `onCycleBlocksCount` fires, commit beat lands (§ Haptics H9).
  UP outside, travel > 10dp, duration > 600ms, or `ACTION_CANCEL` → press
  releases with **no commit and no feel** — a cancelled press is a non-event
  and the machine stays silent about non-events.
  Wrap 4→1 is the identical press with the identical beat; the hero readout
  dropping is the whole announcement.
- **FLOW (value == 5):** pips draw all-empty at α 100 (the count is parked,
  not deleted — it returns when minutes rise); press animates visually but
  commit does nothing — no callback, no beat, no hidden state change.
  Cycling an invisible count was a silent lie; it dies here.
- **Face micro-print (fixed, the face's only other furniture):**
  `5–180 · MIN`, Anonymous Pro regular, 7sp, letterSpacing 0.18,
  `tokTextSecondary()` α 170, centred at cy +62dp. It states the scale's
  range once, in print, the way instruments do.

### M8 — The one-number law (readout = TOTAL)

Three facts, three homes, no fourth rendering anywhere:

| Fact | Only home | Set by |
|---|---|---|
| Block length (5…180 min, or FLOW) | The wheel's printed scale — position against ticks/numerals | The wheel only (drag, fling, a11y step, FLOW key) |
| Block count (1…4) | Hub pips | Hub release-commit only |
| **Total = block × count** | **The Deck hero readout** | Nothing — derived, never set directly |

- **Host readout (`TactileDeckScreen.primaryReadout`) becomes the TOTAL,**
  live: `total = dialMinutes × studyBlocksCount`, recomputed on every
  `onMinutesChanged` and every block commit, in the **same frame** as the
  detent/press that caused it. Human format, never `MM:SS` (there are no
  seconds before a session exists; fake precision is a lie):
  total < 60 → `50 MIN`; total ≥ 60 → `1H 40` (hours, space, zero-padded
  minutes; ceiling 180 × 4 = 720 → `12H 00`). Type: Anonymous Pro Bold;
  84sp while the string is ≤ 5 glyphs, 68sp at 6 glyphs (`12H 00`).
  FLOW → the readout prints the word `FLOW` at hero size.
- **Stepped, never rolled:** the readout changes only at detent crossings and
  hub commits — values between detents do not exist on a stepped instrument
  and are never printed, not even for one frame of a fling. A change lands as
  a hard cut with one physical trace: glyphs at **88% ink settling to 100%
  over 180ms, CurveA** (host-side alpha; 180ms is the vocabulary's smallest
  duration — Vega/Aldo's 120ms ink-settle adapted to the locked vocabulary).
- **Caption = the factorisation, in words:** `2 BLOCKS OF 25 MIN`
  (Anonymous Pro, 10.5sp, letterSpacing 1sp, `onSurfaceVariant`) — the
  factors' only textual home. FLOW caption: `OPEN FLOW — NO BLOCKS`.
  The string `MIN TOTAL` dies (the total needs no label; it is the hero).
- **FINISHES line:** computed from the total at seat/commit frames only.
  While the wheel is in motion (drag, fling, settle) it draws at **55% ink**
  — provisional while the hand is on the instrument — and returns to full
  ink, recomputed from the seat-time clock, on the seat frame. FLOW:
  FINISHES is replaced by `NO LIMIT`.
- **Slide badge:** timed mode → `subMonoBadge = ""` (blank). Repeating the
  total — or the block length — on the commit control is a second rendering
  at the exact moment of commitment; it dies. FLOW mode → badge stays
  `FLOW` (a mode, not a number).
- **FLOW key exception, named:** the FLOW key under the wheel sets block
  length to 5 via the same state path as the wheel. It is a block-length
  setter, not a total setter, so the law holds — but it is the wheel's one
  external writer, and any future preset surface must meet the same test:
  set a factor, never the product.

---

## Motion (curve + ms)

All animators are `ValueAnimator` on the view (Deck host is Compose, the
wheel is a View — curves come from `MonolithCurves` PathInterpolators;
durations only from **{180, 260, 280, 420, 520}**). No new curve is
introduced. New animators beside the existing `settleAnimator`:
`flingAnimator`, `pulseAnimator` (drives `dragPulse` + `hubPressT`).

| Motion | Curve | Ms | From → To / trigger |
|---|---|---|---|
| Drag pulse in | CurveC (0.3, 0, 0.8, 0.15) | 180 | `dragPulse` 0→1 on drag DOWN — its flat tail is a press settling in |
| Drag pulse out | CurveA (0.2, 0, 0, 1) | 260 | `dragPulse` 1→0 on UP/CANCEL |
| Hub press in | CurveA | 180 | `hubPressT` 0→1 on hub DOWN; cap scale 1→0.96 |
| Hub press out | CurveB (0.05, 0.7, 0.1, 1) | 260 | `hubPressT` 1→0 on UP/CANCEL, commit or not |
| Fling | CurveB | **420 fixed** | § below |
| Seat, ≤ 1 detent (≤ 8.57°) | MechanicalOvershoot (1.25) | 180 | overshoot ≤ 1.8°, one pawl-drop |
| Seat, 2–4 detents (8.57–34.3°) | CurveA | 260 | direct arrival, no overshoot theatre |
| Seat, > 4 detents, and every post-fling seat | CurveB | 280 | the long tail lands the throw |
| Rubber-band return | MechanicalOvershoot (1.25) | 280 | display → end detent, overshoot ≈ 1.5° past the stop, one bounce |
| Readout ink-settle (host) | CurveA / EasingA | 180 | glyph alpha 88% → 100% on total change |

**Fling (replaces today's projection outright):**
1. On UP (dragging, not rubber-banded): `velocityTracker.computeCurrentVelocity(1000, 4000f)`
   — the 4000 px/s cap is new; today's uncapped call lets a tracking glitch
   mint an infinite throw. Tangential angular velocity at the last touch
   point: `ω = (vx × −sin(a) + vy × cos(a)) / radiusPx × (180/π)` in °/s,
   `a` = last touch angle, `radiusPx = max(downDistPx, 24dp in px)`.
2. `|ω| ≤ 120°/s` → no fling; go straight to seat. Below 120°/s a release is
   a placement, not a throw (today's 40°/s threshold flung placements).
3. `|ω| > 120°/s` → `flingAnimator`, **420ms, CurveB**, from current
   `wheelAngleDeg` to `rest = wheelAngleDeg + sign(ω) × min(|ω| × 0.22s,
   105°)`, clamped 120…420. **0.22s is the projection factor** (CurveB's
   effective travel integral); **105° cap = 61 min max throw** — a hard spin
   crosses half the dial, never wraps it. Each frame converts angle →
   snapped value and fires detents through the § Haptics density law, so the
   fling *sounds* like a ratchet decelerating because CurveB stretches the
   intervals — no extra code produces that.
4. **End hit during fling:** the frame the animator's value reaches 120° or
   420°, cancel the fling that frame, fire the end-stop beat, and seat at
   the end detent (280ms CurveB tier collapses to 0 distance — seat is a
   no-op; the end-stop beat *is* the seat beat for that arrival).
5. After fling, or directly from step 2: seat per the table above.
   `continuousDeg` re-syncs to the final detent angle at every seat end.
6. **Rubber return pre-empts fling:** if `continuousDeg` is outside
   120…420 at release, there is no velocity read at all — rubber return
   (280ms MechanicalOvershoot) runs, end-stop beat fires **on animation
   end** (when the wheel thunks home, not when the finger leaves).

**Grabbing mid-motion:** any drag DOWN cancels both animators and re-syncs
`continuousDeg = wheelAngleDeg` — the hand is a brake with τ = 0. There is
no catch-up animation; the wheel is where the hand found it.

**Invalidation:** setters, touch, and running animators invalidate as today;
`dragPulse` / `hubPressT` animators invalidate per frame while running.
The specular rig (§ Depth & light) owns no animator and drives no
invalidation — it is computed inside draws the host already makes.

---

## Haptics (per beat)

Carrier for every beat: `TactileSoundEngine.triggerWaveform(timings,
amplitudes)` via `BlockitCanvasView.fireFeel(hapticMs, freqHz)` — except
where "engine direct" is named. Waveforms below are the engine's built
waveforms, quoted exactly; **no engine change is required by any beat.**
`fireFeel` routing law (Round 0): pitch is honoured **only** by the 1L
branch; 4L/8L/10L calls never carry a pitch argument — passing one is a lie
the codebase already stripped once.

| # | Beat | Call | Waveform (ms) | Amplitudes | When |
|---|---|---|---|---|---|
| H1 | Grab | `fireFeel(1L, freqAtValue)` | [0, 7, 5] | [0, 180, 70] | Drag DOWN — picking the wheel up ticks once, confirming grip |
| H2 | Minute crossing (fine zone) | `fireFeel(1L, freqAtRawMinute)` | [0, 7, 5] | [0, 180, 70] | Each 1-min step in fine drag, subject to throttle + density law |
| H3 | 5-min detent | `fireFeel(1L, freqAtValue)` | [0, 7, 5] | [0, 180, 70] | Each detent crossing, drag or fling, subject to throttle + density law |
| H4 | 15-min major | `fireFeel(4L)` → latch | [0, 11, 26, 9] | [0, 215, 0, 150] | Crossing a value with `value % 15 == 0` — **replaces** H3 for that crossing, never stacks |
| H5 | End-stop knock | `fireFeel(8L)` → firm click | [0, 13, 20, 8] | [0, 225, 0, 135] | Value arrives at 5 or 180 from inside; fling end-hit; rubber-return seat (on anim end) |
| H6 | Seat, travel ≥ 10° | `fireFeel(4L)` → latch | [0, 11, 26, 9] | [0, 215, 0, 150] | `onAnimationEnd` of seat/fling — the pawl drops when the wheel *arrives*, not when it leaves |
| H7 | Seat, travel < 10° | `fireFeel(1L, freqAtLandedValue)` | [0, 7, 5] | [0, 180, 70] | `onAnimationEnd`, small arrivals |
| H8 | FLOW crossing | `fireFeel(10L)` → completion pulse | [0, 10, 24, 10, 24, 14] | [0, 175, 0, 125, 0, 80] | `value == 5` flips either direction — **replaces** H3/H4 for that crossing |
| H9 | Hub commit | `fireFeel(4L)` → latch | [0, 11, 26, 9] | [0, 215, 0, 150] | Hub UP-commit only (§ M7). DOWN is silent; cancel is silent; wrap 4→1 is this same beat |

**Silence list (deliberate, load-bearing):** no-change release (value at
UP == `valueAtGestureStart`, no fling) → no seat beat. Hub DOWN, hub cancel,
FLOW-mode hub commit attempt, locked-view touch attempts, density-suppressed
crossings → nothing. A control that clicks for non-events trains the hand to
ignore its clicks.

**Throttle (detent class — H1/H2/H3/H7 only):** never fire < **28ms** after
the previous detent-class beat; crossings inside the window are drawn, not
felt, and **dropped, never queued** (the engine queue is 64 deep; queueing
a spin's crossings is how the old mush happened). H4/H5/H8 **bypass** the
throttle but replace their crossing's detent beat. End-stop latch: H5 fires
once per arrival; it re-arms only after the value leaves the stop by ≥ 1
detent (coarse) / ≥ 2 min (fine) — holding the wheel against the wall is
silent after the first knock.

**Speed-density law** (speed = `omegaEmaDegPerSec`, § M2; during fling,
speed = animator's instantaneous |dAngle/dt|):

| Speed | Fine zone | Coarse zone / fling |
|---|---|---|
| < 50°/s (≈ < 29 min/s) | every 1-min crossing (H2) | every detent (H3) |
| 50–120°/s | detents only (H3); minute ticks suppressed | every detent (H3) |
| > 120°/s, and all fling | detents only (H3), 28ms throttle still applies | detents only (H3), 28ms throttle still applies |

At a hardest flick the wheel crosses ≈ 35 detents/s; the 12ms detent
waveform cannot re-arm meaningfully faster than ~28ms, so per-detent feel
at full speed would collapse into continuous buzz. The law thins *events*;
CurveB's tail stretches their spacing, and the deceleration is heard/felt
as a ratchet winding down. That is the honest limit of this vibrator, and
the spec stays inside it instead of pretending otherwise.

**FLOW-crossing detail:** `wasFlow` is tracked across drag, fling, seat,
and external sets. H8 fires exactly once per flip, on the crossing frame —
entering FLOW exhales; leaving FLOW (5 → 10 coarse, 5 → 6 fine) fires the
same pulse: the gate swings both ways with one sound.

---

## Sound (primitive + pitch)

Synth voice for every transient (engine as built): 44.1kHz float-PCM, 4
partials weighted {0.6, 0.28, 0.37, 2.2}, body decay exp(−0.0114)/sample,
tail exp(−0.0313)/sample. Pitch is carried **only** by
`playRotaryDetent` — every pitched beat below routes through `fireFeel(1L)`
or an engine-direct `playRotaryDetent` call; no other primitive receives a
pitch it would ignore.

| Beat | Primitive | Pitch | Dur | Gain | Chord |
|---|---|---|---|---|---|
| H1/H2/H3/H7 detent family | `playRotaryDetent(freq)` via 1L | **340Hz @ 5 min → 880Hz @ 180 min**, `freq = 340 + ((v − 5) / 175) × 540` (engine clamps 280–1480) | 6ms | 0.38 | 1.0 |
| H4 major / H6 seat / H9 hub | `playMechanicalLatch(true)` via 4L | 760Hz fixed (engine-owned; no arg passed) | 16ms | 0.54 | 1.2 |
| H5 end-stop / rubber seat | `playFirmClick()` via 8L | 620Hz fixed | 12ms | 0.50 | 1.1 |
| H8 FLOW crossing | `playCompletionPulse()` via 10L bucket | 185Hz fixed | 64ms | 0.68 | 1.33 |

- **Pitch = value** is the wheel's only pitch law: commitment sounds tighter
  as it grows. Fine-zone minute ticks use the *raw* (unsnapped-to-5) minute
  in the same formula, so pitch glides smoothly through the fine range
  instead of stepping at detents.
- **Majors are identified by waveform, not pitch** — the latch's
  strike-gap-seat shape against the detent's single strike. Both end stops
  share the firm click's double-strike shape; the ends are told apart by
  position and by the detent pitch ramp that led there (340Hz end vs 880Hz
  end). A *pitched* major and a sub-ramp end-stop voice (Rad's
  `playWheelMajor` / `playWheelEndStop` at 210Hz) are better instruments
  and are **deferred engine extensions, explicitly not assumed anywhere in
  this spec** — if the engine grows them later, H4/H5 swap primitives and
  nothing else in this file changes.
- **No gain rides:** detent gain is engine-fixed at 0.38; this spec does
  not fake a velocity gain curve it cannot produce. Speed is expressed by
  event density (§ Haptics), never by stacking or queueing transients.
- Sound-off / haptics-off (engine flags) change nothing in the motion or
  draw spec — every beat degrades to silence independently, and the wheel
  remains fully legible, because no fact is carried by sound alone.

---

## Depth & light

### D0 — Night staircase (locked foundation, applied)

Night ground `#171412`; socket recess goes **below** ground
(`tokSurfaceVariant()` night `#100E0C`); surface steps ≥ 1.35:1; text tiers
≈ 11 / 5.8 / 3.5:1 (`tokTextPrimary` / `tokTextSecondary` / hairline class);
control fills cap at `#4A423C` (`tokControlFill()` night) — the thumb obeys
it; AOD ground is true `#000000` (the wheel is never drawn on AOD; the rule
is recorded so no reuse of this view drifts it). Day staircase mirrors with
the light token set. Depth is tone steps + hairlines + recess — **no drop
shadows, no blur, no glow anywhere on this element**; the two contact
shadows (thumb, hub) are flat offset circles in black alpha, the app's
existing idiom, and they are the only shadow-class marks allowed.

Draw order (replaces today's `onDraw` sequence; existing preallocated
paints only — `paintFill/Stroke/Accent/Word/WordSub/Mono/MonoSub`,
`boundsRect`, `vectorPath` — zero allocation):

| Pass | Content |
|---|---|
| L0 | Socket recess: circle R 136 `tokSurfaceVariant()`; top inner arc stroke 3dp `0x2A000000` day / `0x14000000` night; bottom arc 2.5dp `0x24FFFFFF` day / `0x2EFFFFFF` night (at night the *light* edge carries the recess) |
| L1 | Program band: track (empty-day case), segments, origin notch, now-line (§ M6) |
| L2 | Grip annulus + sheen wedge (rig-lit, § D3) |
| L3 | Knurl: 120 teeth, rig-lit (§ D3) |
| L4 | Grip index line (§ M5) |
| L5 | Scale plate: circle R 110 `tokSurface()`; hairline R 110 1dp |
| L6 | Ticks (36, § M4) |
| L7 | Numerals + FLOW word (§ M4) |
| L8 | Terminal bars (§ M4) |
| L9 | Track: slot → progress → top light line (§ M5) |
| L10 | Ghost marker (fine drag only, § M2) |
| L11 | Thumb: shadow → body → marker line → pip (§ M5) |
| L12 | Face micro-print (§ M7) |
| L13 | Hub: shadow → body (press-scaled) → step rings → light arc → screws → pips (§ M7) |
| L14 | Locked overlay (only if `isLockedSession`): progress arc + thumb body redrawn in `tokHairline()` — reuse insurance; the Deck unmounts the wheel while running, so this pass is normally dead |

### D1 — The Travelling Specular rig (CANONICAL — Dario owns this spec)

**Marko (loadout caps), Yuki (sleds), Otis (nav) reference this section.
Do not fork it; extend it by mailing Dario.**

- **L1 — One source, fixed in screen space, forever.** Direction
  `L = normalize(−0.42, −0.91)` — from the upper left, ≈ 25° off vertical.
  In the wheel's display-angle convention (0° east, clockwise +, y down)
  the source sits at **LIGHT_ANGLE = 245.2°**. No element, state, loadout,
  hour, theme, or session ever moves this vector. Only the *position of a
  moving part* changes what the light does to it.
- **L2 — Light carries no data.** Specular never encodes duration,
  readiness, progress, elapsed time, armed-ness, selection, or count.
  Those facts each have exactly one rendering (position, ink, words,
  numerals, pips). Light is a material property of metal under a lamp.
  Any use that asks light to *mean* something is out of spec by definition.
- **L3 — Light is not accent.** Specular colour is warm white `#FFF6EC` at
  capped alpha (peaks in D3/D5). It is never orange, never counts against
  the accent budget, and is never drawn over text, numerals, or pips.

### D2 — The two rig functions (the entire API)

```
specularBroad(absAngleDeg)   = t³,  t = max(0, cos(absAngleDeg − 245.2°))
specularMachined(absAngleDeg)= t⁸,  same t
bandOffset(localTravelPx)    = clamp(−0.35 × localTravelPx, −8dp, +8dp)
```

- **`bandOffset` is bounded and seat-relative (amendment, Marko's clamp,
  accepted by the rig owner):** `localTravelPx` is the signed distance from
  the part's **nearest seat / rest position**, never cumulative travel from
  an origin. The offset is **zero at every seat** and saturates at ±8dp.
  Two reasons, both binding: (1) cumulative offset over long travel
  physically exits the part — the loadout band travels ≈ 270dp, where
  −0.35× would park the highlight ~95dp away and leave the cap unlit at
  exactly the far stop; (2) a per-stop *rest* offset would encode position
  — light carrying data, forbidden by L2. At rest, the light is identical
  at every stop; only *departure* from a seat rakes it.

- **Implementation a Canvas can actually do:** both specular functions are
  **precomputed lookup tables**, built once in `init`:
  `FloatArray(720)` per exponent at 0.5° resolution (index =
  `((angle mod 360) × 2).roundToInt() mod 720`). Two tables (broad +
  machined) = 1,440 floats ≈ 5.6KB, built once, never rebuilt. **Zero trig
  per frame, zero allocation per frame.** No `Shader`, no per-frame
  `LinearGradient`/`RadialGradient` construction anywhere in the rig.
- The linear form's gradient is precomputed **once per consuming view in
  `onSizeChanged`** (D5) and reused under translation — never rebuilt per
  frame.

### D3 — Rotary form: what the wheel consumes

- **Knurl (machined exponent):** 120 teeth, step 3°, stroke 1.3dp,
  R 123.5→128dp. Tooth *i* screen angle `a = i × 3° + (faceRotation mod 3°)`
  — teeth are placed by screen angle each frame from a precomputed
  unit-vector table (`FloatArray(240)` cos/sin per tooth, built in `init`),
  so rotation slides teeth *through* the fixed light instead of strobing a
  pattern in place. Per tooth: `s = specularMachined(a)`.
  Even teeth (light stroke, white): α = day `0x0A + 0x26 × s` (peak `0x30`) /
  night `0x06 + 0x20 × s` (peak `0x26`).
  Odd teeth (dark stroke, black): α = day `0x2E − 0x10 × s` / night
  `0x38 − 0x16 × s` (dark teeth *fade* where the light teeth flare — the rim
  reads as a lit cylinder, not a zebra). Parity is retained at low contrast
  so rotation phase still reads at the bottom of the wheel where `s ≈ 0`.
- **Grip sheen (broad exponent):** the 50° annulus wedge (§ M5) fills white
  at α = (day `0x0A` / night `0x06`) × `(0.4 + 0.6 × specularBroad(wedge
  screen-centre angle))`. The wedge rotates with the grip; its brightness
  is a function of where it currently sits in the room. A fling visibly
  throws light around the rim — because the rim moved, not because the
  light performed.
- **Hub light arc (broad exponent, static sample):** the arc spans
  200°→340°, centre 270°; `specularBroad(270°) = cos(24.8°)³ ≈ 0.749`, so
  its alpha is **derived, not chosen**: day `0x30 × 0.749 ≈ 0x23`, night
  `0x1C × 0.749 ≈ 0x15`, stroke 1.2dp at R 42.4dp. If LIGHT_ANGLE ever
  changes, this number is recomputed from the formula, never re-tuned by eye.
- **Explicit non-consumers (printed matter does not shine):** ticks,
  numerals, the FLOW word, terminal bars, program arcs + now-line, track
  progress, pips, micro-print. Their alphas are fixed by § M4/M6/M7. (A
  glinting tick was considered and rejected: moving emphasis on the scale
  competes with the value ink that is the scale's job.)

### D4 — Day / night behaviour (wheel surfaces)

| Element | Day | Night |
|---|---|---|
| Socket / grip fill | variant `#D8D1CB` | variant `#100E0C` (below ground) |
| Knurl light tooth α | ≤ `0x30` | ≤ `0x26` |
| Knurl dark tooth α | `0x2E → 0x1E` | `0x38 → 0x22` |
| Sheen wedge α | ≤ `0x0A` | ≤ `0x06` |
| Index line / majors ≤ value / progress | textPrimary `#2C2A29` | textPrimary `#D2C6BB` |
| Thumb body | controlFill `#252323` | controlFill `#4A423C` (cap honoured) |
| Thumb pip + marker | onPrimary `#FBF1EC` | primary `#FFE4CF` (glyph-size cream only) |
| Thumb shadow α | `0x42 → 0x55` | `0x66 → 0x77` |
| Hub body | surfaceBright `#FAF6F0` | surfaceBright `#362C27` |
| Hub shadow α | `0x26` | `0x50` |
| Hub light arc α | `0x23` (rig-derived) | `0x15` (rig-derived) |
| Program segments | onSurface α 200/160/120 | same alphas (tone carries, not brightness) |
| Accent on wheel | **none** | **none** |

### D5 — Linear form: caps, sleds, band cap (for Marko / Yuki / Otis)

Every linear sliding part — Deck slide thumb, loadout cap, app-row sleds,
routine sleds, nav travelling cap — draws **the same band** inside its own
clip:

- Band width **18dp**; gradient (precomputed per view in `onSizeChanged`,
  one `LinearGradient` object, reused): stops at 0 / 0.35 / 0.5 / 0.65 / 1
  with alphas 0 / 40% peak / **peak** / 40% peak / 0 of `#FFF6EC`.
  Peak alpha: **day 14%, night 8%** — except over the Deck slide's orange
  COMMIT fill, where peak is capped at **8% in both modes** and the effect
  is carried by the band's top 1px catch edge, so the orange stays
  unmistakably orange (the fill is the accent; the cream is weather on it).
- Band centre = `capCentre + bandOffset(localTravelPx)` (§ D2, bounded,
  seat-relative): the highlight counter-slides 0.35dp per 1dp of departure
  from the nearest seat, saturating at 8dp — the perceived lag *is* the
  physics; no temporal lag filter is added (a trailing highlight reads as
  input latency). For seatless continuous travel (the Deck slide mid-track)
  the seats are the track ends; mid-track the band rides saturated at ∓8dp,
  inside any cap ≥ 24dp wide.
- The band lives **under** the cap's marker line/label, never over it.
  On night `tokControlFill` (`#4A423C`) sleds, the band body peak drops to
  **4%** and the top catch edge (1px, 12%) carries the effect — the
  staircase rule caps *fills*; hairline-class edges are the sanctioned
  carrier at night, and no fill is lifted past its step.
- A cap reads identically in greyscale with the band deleted. That is the
  acceptance test for every consumer: delete the band, lose nothing but
  material.

### D6 — Cost per frame (the rig's budget, binding)

Wheel: 120 table lookups + 120 line draws + 1 wedge path + 1 broad sample —
**< 0.15ms** on a mid-range device, inside a draw the wheel already makes.
Each linear consumer: 1 gradient rect draw inside its existing clip.
Rig-wide: **zero allocation after init/`onSizeChanged`, zero animators,
zero clocks, zero extra invalidations.** When nothing moves, nothing is
repainted for light's sake.

---

## State machine

States (mutually exclusive) + orthogonal flags. `FLOW` is **not** a state —
it is the value mode `minutesValue == 5`, tracked by the `wasFlow` flag in
every state below.

| State | Entered by | Exits to |
|---|---|---|
| IDLE | init; seat/rubber-return end; hub release | DRAG_COARSE / DRAG_FINE (DOWN in zone), HUB_PRESSED (DOWN in hub), SETTLE (external `minutesValue` set while not dragging — animates 260ms CurveA to the new detent, no beats except H6/H7 on end if value changed) |
| DRAG_COARSE | DOWN ≥ 88dp or on thumb (§ M1) | FLING (UP, \|ω\| > 120, not rubber), SETTLE (UP otherwise), RUBBER_RETURN (UP while rubber-banded), IDLE (CANCEL → SETTLE path with no beats) |
| DRAG_FINE | DOWN 55…88dp | same as DRAG_COARSE |
| FLING | UP with velocity | SETTLE (animator end), IDLE (end-hit: beat + snap state vars, no SETTLE needed), DRAG_* (DOWN grab — brake) |
| SETTLE | release without fling; post-fling; external set | IDLE (anim end → H6/H7 if value changed during the gesture), DRAG_* (DOWN grab) |
| RUBBER_RETURN | UP while `continuousDeg` outside 120…420 | IDLE (anim end → H5), DRAG_* (DOWN grab) |
| HUB_PRESSED | DOWN inside `hubTouchR` | IDLE (UP → commit per § M7, or silent cancel) |
| (LOCKED) | `isLockedSession == true` | Not a machine state: touch dispatch short-circuits (§ M1), draw adds L14 only. The Deck unmounts the wheel while a session runs, so LOCKED is reuse insurance, never the primary presentation. HOLD is enforced by the host unmounting the wheel — this view offers no mid-session edit path of its own. |

Flags: `wasFlow` (H8 edge detection), `endStopLatchedLow/High` (H5
once-per-arrival, § Haptics re-arm rule), `valueAtGestureStart` (silence
rule for no-change releases), `overlapDay` (host-set, § M6 alpha rule).

Invariants: `wheelAngleDeg` leaves 120…420 **only** as rubber-band display
(§ M2) or settle overshoot (≤ 1.8°); `minutesValue` is always a snapped
value (1-min steps exist only as fine-zone values 6…9 inside FLOW mode —
see Edge cases E4); at every animator end, `continuousDeg ==
minutesToAngle(minutesValue)` exactly.

---

## Edge cases

- **E1 — Tap without drag on the rim/face:** DOWN+UP at the same angle.
  Delta accumulation means the value does not jump to the finger (today's
  absolute mapping teleports it — that behaviour is the bug this spec
  exists to kill). A rim tap changes nothing and, per the silence list,
  beats nothing except the H1 grab tick on DOWN.
- **E2 — Multi-touch / pointer swap:** only the first pointer is tracked
  (`activePointerId`); additional DOWNs are ignored, and if the tracked
  pointer lifts while another remains, the gesture ends as CANCEL (settle,
  no beats). The wheel never averages two fingers into a phantom angle.
- **E3 — ACTION_CANCEL (system steal, notification shade):** identical to
  a no-change UP — animators settle, `dragPulse` releases, no beats, no
  fling (velocity from a stolen gesture is not intent).
- **E4 — Fine-zone values 6…9:** FLOW mode is `value < 10` in the host
  (`isStopwatchMode`). Fine drag can land 6, 7, 8, 9 — all still FLOW to
  the host, all still detent 0's neighbourhood on the scale. The wheel
  reports them honestly via `onMinutesChanged`; the readout prints `FLOW`
  for every value < 10 (host rule, § M8 FLOW caption applies); snapping
  back to exactly 5 happens only via coarse detent or the FLOW key. No
  special wheel case exists — inventing one would make the fine zone lie
  about its own resolution.
- **E5 — External set during drag** (host writes `minutesValue` from the
  FLOW key mid-gesture): the setter already guards with `!isDragging` for
  the angle; the value write is accepted but the drag's `continuousDeg`
  continues from the finger — on release the drag's value wins. A rested
  surface and a live hand never fight; the hand is present, the surface
  can be pressed again.
- **E6 — Process death / detach mid-fling:** animators are view fields;
  `onDetachedFromWindow` cancels both, recycles the tracker, and snaps
  `wheelAngleDeg = minutesToAngle(minutesValue)` — the persisted value
  (host state) is the truth, the angle is derived. No orphaned callbacks.
- **E7 — SLEEP window across the day origin:** 20:00–03:30 draws as one
  continuous arc wrapping through 270° (Canvas arc sweep handles the wrap
  once the host supplies `endMin > 1440` for today's instance, § M6);
  yesterday's spill segment (00:00–03:30) is a second segment ending at the
  same origin — the two meet at the notch with the standard 0.5° seams.
  At exactly 03:30 the now-line sits on the origin notch: wake time is the
  seam of the drawing, by construction.
- **E8 — Saturday (test day):** his routines carry day masks; a Saturday
  with only a test routine enabled draws exactly that one segment. The
  wheel does not know what a test is and must never learn — it draws the
  windows it is given.
- **E9 — All routines disabled / deleted:** empty-day rendering (§ M6).
  The band never disappears (its track + now-line remain), so the case
  doesn't change size between "busy day" and "empty day" — no layout shift
  is ever caused by his program.
- **E10 — Hub press during FLING/SETTLE:** hub DOWN cancels nothing; the
  press and the wheel's arrival proceed independently (different dispatch,
  § M1 — the DOWN that began the press was never a drag). Commit during a
  settle changes count only; the settle's target (block length) is
  unaffected. Rotation and count never share a gesture.
- **E11 — Accessibility / switch control:** the view exposes discrete step
  semantics — `contentDescription = "Focus duration, N minutes"` (or
  "Focus duration, open flow" at 5), updated on every detent commit;
  accessibility increment/decrement actions step ∓/± one detent (5 min)
  through the same snap + beat path as a coarse crossing. One scale, every
  hand — including the half-asleep one at 03:30, which is the real
  accessibility case this instrument is built for: the pitch ramp
  (340→880Hz), the latch majors, and the firm-click ends make the full
  range countable eyes-free from FLOW upward.
- **E12 — Haptics/sound disabled, or no vibrator:** every beat no-ops
  inside the engine; motion, draw, and readout are bit-identical. Feel is
  a rendering of events, never a carrier of facts.
- **E13 — Narrow host (width < height pathologies, split-screen):**
  `outerR` derives from `min(w, h)`; all zone radii are fractions of
  `outerR` except the dp-anchored thumb/track, which scale down
  proportionally below the reference only via the `0.47` factor — at
  `outerR < 100dp` the fine zone (55…88dp at reference) is recomputed as
  `0.43R…0.6875R` so the zones never invert. Reference-size numbers are
  the contract; fractions are the fallback, in that order.
- **E14 — Guard / Ledger (open fates, no silent decision):** the wheel
  reads **nothing** from Guard (no permission state, no app counts, no
  readiness) and its spec is identical under both Guard outcomes. The
  wheel writes **nothing** to any record: if the Ledger is kept, its
  session history must source from `FocusSessionEntity`, never from wheel
  state — flagged here as a dependency so no later Ledger spec quietly
  couples itself to this view.

---

## Code: helps / fights (file anchors)

### Helps — already true, build on it

| Asset | Anchor |
|---|---|
| Canonical angle frame; `onDraw` reads `wheelAngleDeg` | `BlockitCoreViews.kt` → `RotaryWheelView.minutesToAngle()` (120…420) + `onDraw` sweep derivation |
| True 36-detent tick loop with 5/15/30 levels | `RotaryWheelView.onDraw` tick pass (`for (m in 5..180 step 5)`) — radii/colour rules change per § M4, the loop stands |
| Honest `fireFeel` contract (pitch only via 1L) | `BlockitCanvasView.fireFeel()` + its Round 0 contract comment |
| All four sound primitives + waveforms as specced | `service/TactileSoundEngine.kt` → `playRotaryDetent / playMechanicalLatch / playFirmClick / playCompletionPulse` — **no change** |
| Curve + duration vocabulary complete for every motion here | `ui/theme/MonolithCurves.kt` — CurveA/B/C, MechanicalOvershoot(1.25), {180, 260, 280, 420, 520} — **no change** |
| Night control-fill token exists and is correct | `BlockitCanvasView.tokControlFill()` (night `#4A423C`) — the wheel just has to use it (§ M5) |
| Real font weights shipped (Anonymous Pro Bold, Atkinson Bold true files) | `BlockitCanvasView` typeface loaders — numerals/pips spec assumes them |
| Routine data + window math exist end-to-end | `MonolithViewModel.routines` (StateFlow), `data/MonolithModels.kt` → `RoutineEntity` + `RoutineSchedule` (`durationMinutes`, `nextStartMillis`, bit convention), `MonolithDao.getAllRoutinesFlow()` |
| Host already collects routines | `MainActivity.kt` → `val routines by viewModel.routines.collectAsStateWithLifecycle()` — threading them into `TactileDeckScreen` is plumbing, not new data |

### Fights — must change, exactly here

| Fight | Anchor + change |
|---|---|
| Absolute `atan2` drag teleports value | `RotaryWheelView.updateMinutesFromTouch()` — replaced by § M1/M2 dispatch + delta accumulation (keep the method name as the MOVE entry if convenient; its body is new) |
| Projection fling (`ω × 0.18`, threshold 40°/s, uncapped velocity) + settle that fires latch at *start* | `RotaryWheelView.onTouchEvent()` ACTION_UP branch + `animateWheelSettle()` — replaced by § Motion fling/seat; seat beats move to `onAnimationEnd`; `computeCurrentVelocity(1000)` gains the 4000f cap |
| Hub fires on DOWN, prints numeral, clamps 1…6 | `RotaryWheelView.onTouchEvent()` hub branch + `onDraw` hub pass + `studyBlocksCount` setter — replaced by § M7 (release-commit, pips, clamp 1…4) |
| Stray touch-radius constants (`min × 0.46 × 0.52 × 0.85`) | `RotaryWheelView.onTouchEvent()` — dies; single `hubTouchR` (§ M7) |
| Thumb + progress in `tokPrimary()` (night cream fill) | `RotaryWheelView.onDraw` arc + thumb passes → `tokTextPrimary()` (progress) / `tokControlFill()` (thumb body), § M5 |
| Readout shows block length as `MM:SS`; total in caption; slide badge repeats block | `TactileDeckScreen.kt` → `primaryReadout`, the `MIN TOTAL` caption, `SlideToStartView.subMonoBadge` assignment — inverted per § M8 |
| Deck receives no routine data | `TactileDeckScreen.kt` signature + `MainActivity.kt` call site — add `programWindows` / `nowMinutes` (§ M6); view gains the two plain properties |
| No accessibility semantics | `RotaryWheelView.minutesValue` setter — add `contentDescription` + step actions (§ E11) |
| `TactileSoundEngine` — pitched major / sub-ramp end voice do not exist | **Not changed in this spec.** Deferred extension, named in § Sound; H4/H5 are specced on today's primitives so the build never waits on the engine |

---

## Law check

- **ARM ≤ 10s:** the wheel adds zero steps, zero confirmations, zero
  wait-out animations to the arm path. Relative-feeling delta drag removes
  the re-aim a teleport costs; a full-range change (25 → 120) is one fling
  (420ms, running *under* intent) instead of repeated strokes. Cold-open
  cost of this element: one drag or one throw, first try. **Pass,
  improved.**
- **HOLD absolute:** the Deck unmounts the wheel while a session runs; the
  view itself offers no mid-session verb, peek, or adjust — hub included.
  Program arcs are display-only and consume no touch. **Pass.**
- **TELL THE TRUTH once, in human language:** block length lives only on
  the printed scale (which prints `FLOW`, not `5`, at the stop that means
  flow); count lives only as pips; the total lives only as the hero readout
  in human format (`1H 40`, never fake-precision `MM:SS`), stepped at
  detent commits, never rolled through values the instrument cannot set;
  routine windows are drawn at true duration scale on their own band with
  overlaps shown as overlaps. The slide badge's number dies so commitment
  has no second rendering. **Pass.**
- **One fact, one rendering:** scale = block, pips = count, readout =
  total, arcs = wall-clock windows, light = material only (rig law L2).
  No fact appears twice in two meanings anywhere in this spec. **Pass.**
- **Beauty is retention:** the wheel's beauty budget is spent where the
  hand learns — detent feel in the motion, a rim that turns under a fixed
  lamp, a cap that seats when pressed — not on ceremony. Nothing here
  performs when untouched. **Pass.**
- **Rested decides, tired executes:** routines are created/edited on
  Shield by the rested user and merely *shown* here; block + count are set
  in ≤ 2 gestures by the tired user; the emergency hold, the slide, and
  every consequence stay exactly where his decisions put them. **Pass.**
- **His decisions:** emergency hold BANKED — untouched by this element.
  Routines fully his — the wheel renders his windows and writes nothing.
  Guard fate OPEN — this spec depends on nothing in Guard and presumes
  neither outcome (§ E14). Ledger fate OPEN — no screen specced here; the
  one dependency is flagged (§ E14): Ledger must never source from the
  wheel. **Pass — nothing silently decided.**
- **Locked foundations:** real font weights named per element (Anonymous
  Pro Bold numerals, Atkinson Bold words); night staircase per § D0 with
  control fills capped at `#4A423C`; accent budget — orange is a COMMIT
  fill only, **zero orange on the wheel in any state**; **never green** —
  no new colour literals exist in this spec beyond black/white alphas and
  the rig's `#FFF6EC` specular tint. **Pass.**

**Highest-impact decision in this spec:** killing the absolute drag and the
projection fling together — delta-accumulated two-zone drag (rim 1:1, face
0.45×) into a travelling 420ms CurveB fling that ratchets through real
detents and seats with its beat on arrival. Every other element here —
true scale, specular rim, pips, program arcs — is read as craft only once
the flagship control is *held* instead of poked, and *travels* instead of
teleporting. Hand before eye; motion before paint.

— Dario, Maximum-Implementable, wheel
