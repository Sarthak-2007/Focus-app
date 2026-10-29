# ARM AUDIT, PART 2 — THE COMMIT GESTURE ITSELF
## Moss — Critics, motion · Coordinator: Hale · Plan wave (no code)

Measured from current code: `ui/views/BlockitCoreViews.kt`
(`SlideToStartView`, `RotaryWheelView`), `ui/theme/MonolithCurves.kt`,
plus the wiring that decides what the gestures actually cost:
`ui/screens/TactileDeckScreen.kt`, `ui/MonolithViewModel.kt`,
`service/TactileSoundEngine.kt`, `data/MonolithRepository.kt`.

Geometry basis: 360 dp-wide phone (dp math is exact from code, so travels
scale linearly on wider phones). Wheel panel inner width ≈ 328 dp, wheel
height 240 dp → outerR = 108 dp, rimR = 92 dp. Slide track = screen − 24 dp
padding = 336 dp wide, 68 dp high, pinned below the scroll.

Method: code measurement + tired-thumb simulation at 03:30. His volume:
~10 arms/day, ~1,000+ over the drop year — every cost below is ×1,000.

---

## (a) THE SLIDE — `SlideToStartView`

- **Travel.** maxTravel = W − 2×6 dp inset − 76 dp thumb = **248 dp**
  of thumb travel on a 360 dp phone (300 dp on a 412 dp phone).
- **Commit threshold: dragFraction ≥ 0.82, evaluated ONLY on release**
  (ACTION_UP/CANCEL). Finger travel to the line: **0.82 × 248 ≈ 203 dp**.
  No dwell — zero ms of hold required past the line. No velocity term:
  a slow creep and a flick are judged identically.
- **Release before threshold:** total loss. `animateSpringBack(0f)` —
  280 ms, `MechanicalOvershoot` (1.25), fraction clamped to [0,1] so the
  overshoot tail is invisible dead time. A release at 0.81 buys nothing;
  the tired thumb re-drags the full ~200 dp. All-or-nothing is defensible
  for a commit gesture; at ×1,000 arms the undershoot tax is real.
- **Release after threshold:** dragFraction snaps to 1.0, callback fires
  synchronously, THEN the thumb springs back to 0 over the same 280 ms.
  The thumb does not stay at the sealed end — the sealed state is never
  drawn by this view; the screen swap does that work (and if the
  pre-flight modal intervenes, the thumb quietly returns to rest with
  nothing armed — see totals).
- **The gesture is secretly a button — worst structural finding.**
  ACTION_DOWN sets `isDraggingThumb = true` and calls `updateDrag(x)`
  with NO hit-test against the thumb. A single **tap anywhere at ≥82%
  of the track, then release, commits** — no drag, no travel, no
  deliberateness. Conversely the whole 336 dp track is the grab area,
  which is why it feels forgiving. The one centre gesture can be
  executed as an accidental far-right tap.
- **Haptics/sound during drag.** DOWN: fireFeel(4L) → `playMechanicalLatch`.
  Drag ticks: 8 buckets (bucket = frac×8) — up to ~6–7 fireFeel(1L) rotary
  detents at 380→695 Hz across a full drag. Crossing 0.82 in either
  direction: fireFeel(8L) → `playFirmClick` — the gate is FELT mid-drag
  (Round 0 fix, verified in code). Good.
- **Sound at commit: right engine call, fired twice.** The view now calls
  `eng.playHarmonicSeal(true)` directly — the old `fireFeel(10L)` →
  `playCompletionPulse` (session-complete sound spent on starting one)
  is gone; fallback only if engine == null. BUT `onSlideCommitted` →
  `requestArmWithPreFlightCheck()` → `armMonolithSession()` calls
  `playHarmonicSeal(arming = true)` **again**. Two full seal waveforms
  (24+38+16 ms @255) back-to-back on every clean arm. TELL THE TRUTH
  applies to sound: one seal, one clunk.
- **Label death before the decision point.** Label + badge alpha =
  (1 − frac×1.3) → fully invisible at frac 0.77, i.e. **before** the
  0.82 gate. At the exact moment of commitment the screen offers the
  thumb no text at all — only the orange trail (accent @ alpha 55, a
  second orange mass next to the thumb's COMMIT fill — flag for the
  accent-budget owner).

## (b) THE WHEEL — `RotaryWheelView`

- **Mandatory pre-gesture? No — and yes.** Nothing forces a wheel touch:
  with the resting value accepted, the slide alone arms. BUT the resting
  value is not stable or his: ViewModel inits dial = 50 while the init
  preset is POMODORO_25_5 (25 min — the two disagree on cold open), and
  the `activeTask` collector **overwrites dialMinutes with the active
  task's estimatedMinutes** whenever the task changes. "Duration mustn't
  change" is a state the app keeps taking away from him.
- **Scale, now truthful.** 5–180 min over a 300° sweep (120°→420°).
  36 real stops (5, then every 5 min): 1 detent = 8.57° = **13.8 dp of
  rim arc**. Drawn ticks exist ONLY at real stops; majors every 30,
  mids every 15 (Round 0 fix, verified). The scale no longer lies.
- **Drag feel: stepped, not continuous.** `updateMinutesFromTouch` sets
  `wheelAngleDeg = clampedDeg` ONLY when the snapped minute value
  changes. Between detents the drawn dot freezes, then jumps 8.57°.
  At 03:30 that reads as stiction — the wheel feels broken in the hand
  even though the number is honest.
- **Top cliff.** The 60° gap at the top is not a dead zone: finger angle
  <90° is shifted +360 and clamped to 420 (max, 180 min); ≥90° clamps to
  120 (min, 5 min). Crossing top-centre mid-drag flips the value
  180 → 5 discontinuously.
- **Fling + settle: wired, and drives the drawn angle.** VelocityTracker
  is finally queried: ω = (dx·vy − dy·vx)/r², threshold |ω| > 40°/s
  (low — ≈193 px/s tangential at the rim; most releases qualify),
  projection = ω × 0.18 s (a 500°/s flick adds ~90° ≈ 52 min).
  Settle: ValueAnimator on `wheelAngleDeg` itself, **280 ms if
  Δ ≤ 60°, else 420 ms**, CurveB. onDraw derives sweep/dot from
  `wheelAngleDeg` — settle, drag and drawing share one frame (verified).
  The value is final BEFORE the settle animates, so settle is perceived
  dead time (~0.3–0.4 s) rather than blocking time — unless he waits
  to trust the number, which a tired user does.
- **Every detent sounds twice.** Wheel crossing: fireFeel(1L, pitched
  340–880 Hz; 4L latch at %15 stops) → engine `playRotaryDetent`.
  Callback → `ViewModel.setDialMinutes` → `soundEngine.playRotaryDetent`
  AGAIN at a second pitch. Two strikes + two haptic waveforms per
  5-minute step. A 25→50 change = 5 detents = 10 detent sounds.

## (c) THE BLOCKS HUB — centre of the wheel

- **Hit area lies smaller than the drawing.** Drawn hub radius =
  0.52 × outerR ≈ 56 dp. Touch cycles only if dist < innerHubR × 0.85,
  computed off a 0.46 base ≈ **49 dp radius** — the outer ~7 dp ring of
  the drawn hub starts a wheel drag instead of cycling blocks. A tired
  thumb aiming at the hub's edge silently changes the duration.
- **Cycling is one-directional wrap, 1→2→3→4→1** (Deck lambda; VM clamps
  1..4). Resting value is 2, so reaching **1 costs 3 taps**, reaching 4
  costs 2, 4→1 costs 1. No reverse. ~0.2 s per deliberate tap → up to
  ~0.6 s + three chances to miss into a wheel drag.
- **Zero press feedback.** No scale, no colour shift, no animator on the
  hub — the only confirmation is the numeral changing after a ViewModel
  round-trip, plus sound. And the sound is doubled/mismatched: view
  fires fireFeel(4L) → `playMechanicalLatch`, VM `setStudyBlocksCount`
  fires `playRotaryDetent(640)` on top.
- Stale range in the view: `studyBlocksCount` coerces 1..6; VM and
  repository both say 1..4. Dead inconsistency, harmless today.

## (d) THE FLOW KEY — Deck, under the wheel

One tap, three silent state changes (two VM calls in the click handler):

1. `selectStudyPreset(STOPWATCH_FLOW)` → **dialMinutes := 5**,
   **studyBlocksCount := preset.defaultBlocks = 1**, latch sound.
2. `onDialMinutesChanged(5)` → no-op (already 5).

- **It destroys, it does not toggle.** His blocks choice (e.g. 2) is
  reset to 1 with no record and no way back except re-cycling the hub.
  His dial value is gone. The wheel thumb teleports to 5 (external
  sets snap `wheelAngleDeg` instantly when not dragging — no animation).
- **It is a one-way trap in the UI.** The FLOW key is the ONLY caller of
  `selectStudyPreset` in the entire app — no surface can ever select
  another preset again. Worse, Deck computes
  `isStopwatchMode = (preset == FLOW) || dial < 10`: after one FLOW tap,
  dragging the wheel back to 50 leaves the Deck **lying** — caption
  "NO LIMIT", finish line "OPEN FLOW • 20M FLOOR", hub shows ∞ —
  while `Repository.startSession` (which ignores preset, uses dial<10)
  arms a normal timed 50-min session. Screen says open flow; the seal
  is timed. A direct TELL THE TRUTH violation manufactured by state
  nothing can clear short of app-data reset.

---

## TOTALS — full manual arm, cold Deck, thumb only

Assumptions: accessibility already granted (pre-flight off the path —
see breach note), deliberate tired-thumb drag ≈ 0.7 s for the slide,
wheel drag 0.5–0.9 s, hub tap 0.2 s.

| Scenario | Gestures | Finger travel | Detent/step events | Wall-clock to seal initiated |
|---|---|---|---|---|
| **Duration needn't change** (resting 50, blocks 2 accepted) | 1 (slide) | ≈203 dp to gate (≈248 dp full) | ~6–7 drag ticks + 1 gate click + **seal ×2** | **≈0.7–1.0 s** |
| **Duration must change** 25→50, blocks OK | 2 (wheel drag + slide) | ≈69 dp rim arc + 203 dp | 5 detents × **2 sounds each** + settle tick + slide set | **≈1.5–2.2 s** (incl. 280 ms settle perceived) |
| **Worst honest case** FLOW left dial at 5 → 180, blocks 1→4 via hub wrap = 3 taps… (blocks were reset to 1; want 2 = 1 tap) — priced as dial 5→180 (26 detents, 223°, ≈358 dp rim arc, likely 2 grips) + 1 hub tap + slide | 4–5 | ≈560 dp | 26 detents × 2 sounds + 420 ms settle + slide set | **≈3.5–4.5 s** |

**Against the 10 s budget: the gesture path PASSES in every priced
scenario — with two unbounded exceptions the stopwatch can't see:**

1. **Pre-flight modal.** If accessibility is off, the committed slide
   ends in `ShieldPermissionPrimerModal`, not a seal: +1 confirm tap
   minimum (+2–5 s of 03:30 reading), or a trip to system Settings
   (unbounded). ARM ≤10 s is then false on exactly the mornings the
   permission has silently died.
2. **FLOW-trap confusion.** No seconds priced — the cost is a man
   re-checking a screen that lies to him about what he just armed.

×1,000 multiplier check: double detent sounds = ~10,000 extra strikes
a year; double seal = 1,000 doubled clunks; all-or-nothing slide
undershoots at even 5% = 50 full re-drags.

---

## VERDICT TABLE — Element | Measured cost | Verdict | Fix the plan must carry

| Element | Measured cost (travel / threshold / ms / haptics) | Verdict | Fix the plan must carry |
|---|---|---|---|
| Slide travel + threshold | 248 dp travel; gate at 0.82 ≈ 203 dp; no dwell; release-only judgement | PASS vs 10 s; heavy vs ×1,000 — the longest single travel on the Deck, on purpose, but 0.81 = total loss | Keep 0.82 + release-judgement (deliberateness is the point); plan must price ONE undershoot re-drag in the arm budget and state it |
| Slide tap-to-commit hole | ACTION_DOWN has no thumb hit-test: tap at ≥0.82 + release commits with 0 dp of drag | **FAIL — breaks the centre gesture.** The ONE deliberate gesture is executable by accident | Plan must require: commit only if the touch STARTS on/near the thumb (or drag distance ≥ some floor from touch-down x); tap-anywhere may move the thumb, never arm |
| Slide haptics in drag | DOWN latch (4L); ~6–7 pitched ticks (1L, 380–695 Hz); gate crossing firm click (8L) both directions | PASS — the gate is felt before it is judged; best feel in the slice | Carry unchanged; protect the mid-drag gate click in any slide rework |
| Slide settle | Commit: snap to 1.0 then 280 ms overshoot spring to 0 (post-callback, non-blocking) | PASS on time; WEAK on truth — thumb never rests at sealed end | Plan: on commit the thumb stays at the end until the screen swaps; spring-back reserved for failed drags only |
| Slide commit sound | `playHarmonicSeal(true)` in view + again in `armMonolithSession()` = seal ×2 per arm | **FAIL — TELL THE TRUTH in sound: one seal, two clunks** | Plan: exactly one seal call site per arm — view owns the gesture sound, VM arm path must not re-fire it (and vice-versa; pick one) |
| Slide label fade | Alpha (1 − 1.3·frac) → invisible at 0.77, gate at 0.82 | WEAK — no text survives to the decision point | Plan: label may fade, but the gate itself gets a drawn mark (tick/notch at 0.82) the thumb crosses |
| Wheel necessity | Not mandatory per arm; resting value unstable — cold-open dial 50 vs preset 25 disagree; active task overwrites dial on task change | **FAIL vs the standard's spirit** — a pre-gesture he didn't choose keeps being inserted before the slide | Plan: one resting value, owned by him; task changes and cold open must not silently re-price the next arm; preset/dial disagreement killed |
| Wheel detents | 36 true stops, 8.57° / 13.8 dp rim arc each; ticks only at stops; majors 30 / mids 15 | PASS — scale tells the truth now | Carry unchanged |
| Wheel drag rendering | Drawn angle updates ONLY on detent change — dot freezes then jumps 8.57° | WEAK — reads as stiction at 03:30 | Plan: drawn angle follows the finger continuously; only the VALUE snaps |
| Wheel top gap | 60° gap clamps to extremes either side of 90°: crossing top-centre flips 180 ↔ 5 | FAIL (minor) — a discontinuity inside one drag | Plan: crossing the gap mid-drag holds the nearest end instead of flipping; reversal requires re-entry from the sweep side |
| Wheel fling + settle | ω threshold 40°/s; projection ×0.18 s; settle 280 ms (Δ≤60°) / 420 ms, CurveB, drives drawn angle | PASS — real, wired, honest durations | Carry; note fling threshold is low enough that slow releases project too — plan should state intended threshold feel |
| Wheel detent sound | View fireFeel(1L/4L) + VM `setDialMinutes` detent = 2 strikes per step | **FAIL — doubled cost on every price change, ×26 steps worst case** | Plan: one detent sound per step, owned by ONE layer (view feel or VM, not both) — same single-call-site rule as the seal |
| Blocks hub hit area | Drawn r ≈ 56 dp; touch r ≈ 49 dp (0.85 × 0.46-base) — edge taps start wheel drags | FAIL (minor) — the hub's rim betrays the tap into a duration change | Plan: touch radius ≥ drawn radius; hub gets first claim on any DOWN inside its drawn circle |
| Blocks hub cycling | One-way wrap 1→2→3→4→1; from resting 2: to 1 = 3 taps, to 4 = 2 taps; ~0.2 s/tap; NO press feedback (no scale/colour/animator), doubled sound (latch + detent 640 Hz) | WEAK — cheap in seconds, expensive in certainty; ×1,000 arms the wrap-to-1 tax repeats | Plan: visible press state on the hub (depth/colour shift on DOWN) + single sound; state the wrap cost or give 1 a cheaper path |
| FLOW key | 1 tap → dial := 5, blocks := 1 (silent, unrecorded), preset := FLOW forever; thumb teleports; NO exit surface exists; Deck then lies ("NO LIMIT / OPEN FLOW") over timed sessions | **FAIL — worst state bug in the slice: a one-way, silent, lying reset sitting one tap from the centre gesture** | Plan must carry ALL of: FLOW is reversible from the same surface (second tap or wheel ≥10 clears preset); blocks count is NOT reset by FLOW; Deck mode derives from ONE source (the dial, as the repository already does) |
| **Full arm (duration OK)** | 1 gesture, ≈203 dp, ≈0.7–1.0 s, seal ×2 | PASS vs 10 s | Fix sound duplication; budget holds |
| **Full arm (duration changed)** | 2–5 gestures, ≈1.5–2.2 s typical, ≈3.5–4.5 s worst | PASS vs 10 s on the clock — FAIL on friction quality (doubled sounds, stepped dot, wrap taps) | Fixes above are the plan's motion price for this scenario |
| **Full arm (pre-flight intervenes)** | Slide + modal + confirm tap (or Settings trip) | **FAIL vs 10 s — unbounded** | Plan: permission state is checked and surfaced BEFORE the slide (on the Deck, at rest), never after the commit gesture |

---

**Worst finding, one sentence:** The FLOW key is a one-way trap — one
accidental tap silently resets the dial to 5 and the blocks to 1, no
surface in the app can ever undo its preset, and from then on the Deck
tells him "OPEN FLOW / NO LIMIT" while the repository arms timed
sessions — a lying screen one tap away from the one gesture that must
never lie.

— Moss, Critics (motion)
