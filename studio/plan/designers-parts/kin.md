# KIN — Designers, Motion seat
## THE MOTION GRAMMAR TABLE — Monolith, whole app
### Plan wave · no code · every entry buildable without asking a question

Coordinator: Sana (Designers). Seat: Kin, motion.
Ground law, not relitigated here: **Concept = NONE · ARM ≤10 s · HOLD absolute ·
TRUTH once.** Citation requirement lifted by Sarthak's direct order — this file
cites nothing as a quota; numbers carried over from wave 1 are carried because
they survived contact with the settled law, and every number I supersede is
named in §6 with its reason.

---

## 0. The grammar in five sentences

1. **Everything answers on DOWN, in-frame.** First visible change ≤16 ms after
   `ACTION_DOWN` / pointer-down — one frame. A control whose first response
   waits for `ACTION_UP` is broken, not styled.
2. **One mover per input.** Per gesture or event, exactly one element moves
   (plus its own contact shadow). A second moving element is slop and is cut.
3. **State words flip ON ARRIVAL, never mid-flight.** *Arrival* is defined
   mechanically in §1.3 — the word, the seat haptic, and the shadow-slap all
   land on that same frame. During travel, position alone carries state;
   the old word stays, at full ink.
4. **Refusals are physical stillness.** A refused slide returns and *stops*.
   A spent hold bar does nothing. No shake, no bounce-off, no error
   animation, no red flash, anywhere, ever.
5. **Ceremony only on state change, never on the daily path, never ambient.**
   Nothing loops. Nothing breathes. When nothing is touched, Monolith is as
   still as an instrument on a bench.

---

## 1. Registries — the only curves, springs, and definitions a builder may use

### 1.1 Named curves (Canvas `Interpolator` / Compose `Easing`)

| Name | Value | Job |
|---|---|---|
| **THROW** | `cubic-bezier(0.20, 0.00, 0.00, 1.00)` | The house curve. Every powered travel: sleds, caps, panel travel, wheel lock/unlock, overlay exits. Hard attack, friction stop. (Wave-1 CurveA, kept and renamed by function.) |
| **SEAT** | `cubic-bezier(0.22, 0.90, 0.28, 1.00)` | **CurveC's replacement** — see §1.2. Entrances that must arrive and be *done*: overlays, drawer, slide commit snap. |
| **PRESS** | `cubic-bezier(0.20, 0.00, 0.30, 1.00)` | The down-dip only (≤90 ms). Never used for travel. |
| **LINEAR-MEASURED** | `linear` | Anything that *measures* time: hold fill, hold drain, countdown arc step is exempt (it steps, §3 M-14). An eased timer is a lying timer. |

No other bezier exists in this app. **CurveB is retired** (its front-loaded
shape is what real springs now do honestly). Any new curve proposed later
must pass the arrival test in §1.2 or it does not ship.

### 1.2 CurveC — conviction and replacement, stated plainly

CurveC `cubic-bezier(0.30, 0.00, 0.80, 0.15)` ends almost horizontal: at 85 % of
its duration it has delivered only ~78 % of its travel, so every overlay and
drawer built on it *stalls in fog before it arrives* — mush read by the eye as
hesitation, the one thing a sealing machine may never show.

**Replacement: SEAT — `cubic-bezier(0.22, 0.90, 0.28, 1.00)`.** At 50 % of
duration it has delivered ≥88 % of travel; at 85 % of duration, ≥98 %. It
arrives early and spends its remaining milliseconds *settling*, not travelling.
**The arrival test (app-wide, permanent):** any easing used for an entrance
must reach ≥97 % of progress by 85 % of its duration. CurveC fails it
(≈78 %); SEAT passes it; THROW passes it. That test, not taste, is the law.

### 1.3 Springs (Compose `SpringSpec` / equivalent underdamped animator on Canvas)

| Name | `stiffness` | `dampingRatio` | Job |
|---|---|---|---|
| **SNAP-WHEEL** | 280 | 0.78 | Wheel snap-to-detent, wheel programmatic change. |
| **THROW-SPRING** | 520 | 0.90 | Sled, rocker, loadout cap, band cap — one law for every thrown cap. |
| **RETURN-CRITICAL** | 300 | 1.00 | Slide refused-return. Critically damped: zero overshoot *by construction*, not by clamp. |
| **PRESS-BACK** | 800 | 0.65 | Key/hub spring-back after the dip. |
| **BANK-RETURN** | 110 | 0.85 | The weekly bank sled returning to full on Shield (§3 M-19). The heaviest, slowest spring in the app — machinery resetting, once a week. |

`MechanicalOvershoot` and `MechanicalAnticipateOvershoot` are **deleted**,
not repaired: the first is neutralised by its own `coerceIn(0f,1f)` clamp
(a spring that cannot overshoot is a lie in the code), the second has no
call site. Springs above are real springs or they are nothing.

### 1.4 Definitions a builder can code against

- **ARRIVAL** (the frame words flip): for a spring, the first frame where
  `|position − target| ≤ max(1 % of travel, 0.5 dp)`; for a bezier, the
  animator's final frame. Seat haptic + state word + shadow restore all fire
  on this frame and no other.
- **DOWN-RESPONSE**: the first frame after pointer-down in which the pressed
  part's translate/shadow/scale has changed by any visible amount. Budget:
  ≤16 ms. Measured on device at 120 fps, not assumed.
- **TRAVEL**: the distance between the part's two resting positions (or
  adjacent stops). All overshoot figures below are % of travel *and* dp —
  whichever limit is hit first wins.
- **Global budget (Saft's, adopted whole)**: contact ≤16 ms · overshoot
  ≤3 % of travel **or** 2 dp, whichever is smaller · max 1 overshoot cycle ·
  scale floor ≥0.94 (nothing presses smaller, ever) · per-row numbers below
  are stricter than this budget wherever they say so, and the stricter
  number governs.

### 1.5 Night pairing law — what the EYE does when Ton turns the sound down

Ton owns the audio side: night = sound ×0.6, haptic ×0.85. Mine is the eye's
half of that pairing, and it is a refusal: **the eye never gets louder because
the ear got quieter.**

- Geometry, distance, duration, overshoot: **identical day and night**, for
  every row in §3. Muscle memory built at noon must work at 03:30.
- The only night change to a moving part is its *fill register*: the orange
  commit fill steps to the night lamp register while it travels, exactly as
  it sits at rest — motion introduces no second night behaviour.
- No compensating pulse, glow, halo, scale-up, or added flash is ever paired
  with a quietened sound. A seat that lands softly at night lands *visually
  identically* — position and the word flipping on arrival are the receipt.
- AOD is exempt from this section; it has its own law (M-16).

---

## 2. How to read the table

Columns: **Trigger** (the exact event that starts it) · **What moves** (the
single mover) · **Curve / spring** (from §1 only) · **Duration** (target
settle, not animator nominal) · **Overshoot** · **Never moves during it**
(the freeze list for that motion — a violation of this column is a bug).
State-word behaviour is stated inside the row wherever a word exists.

Durations are settle targets verified on device: springs are tuned until
10–90 % travel and settle-to-±1 % land inside the stated window.

---

## 3. THE MOTION GRAMMAR TABLE

### A — The wheel (Deck)

#### M-01 · Wheel drag
- **Trigger:** pointer-down on the rim, then move.
- **What moves:** the rim angle — continuous, 1:1 with the touch angle,
  zero easing, zero lag filter beyond a 1-frame velocity smooth. Knurl
  teeth and rim markings rotate *with* the angle because they are drawn
  from it. (Plumbing precondition: the renderer must draw from the same
  continuous angle the gesture writes. The wave-1 settle animated a
  variable `onDraw` never read — that fraud is fixed before this row
  means anything.)
- **Curve / spring:** none — direct drive.
- **Duration:** the gesture's own.
- **Overshoot:** 0°. At the 5 min / 180 min end stops the angle **clamps
  dead** — no rubber-banding, no elastic overdrag. Rubber is a phone
  material; this is a machined end stop. One end-stop knock per drag on
  first contact with a stop; pushing further moves nothing.
- **Readout law (supersedes wave-1 Motion D1 — reason in §6):** the hero
  numeral **steps, never rolls**. It changes by hard cut on the frame the
  angle crosses a 5-min detent (8.57°), same frame as the detent tick —
  new glyphs land at 88 % ink and settle to 100 % over 120 ms (letterpress,
  not a morph). Values the wheel cannot set (63, 71, 84 min…) are never
  drawn, not even for one frame. `FINISHES` and every **computed** number
  (total = block × count) **re-strike in place, instantly, 0 ms**, at the
  detent frame, and sit at ~55 % ink while the wheel is in motion —
  provisional while the hand is on the instrument, full ink on the seat
  frame. **Chosen** numbers step; **measured** numbers step; **computed**
  numbers re-strike. No number in this app ever rolls, flips, or tweens
  through intermediates.
- **Never moves during it:** the hub (no spin, no pulse, no scale), the
  tick ring independently of the rim (it *is* the rim), the slide bar,
  the header, the nav.

#### M-02 · Wheel fling
- **Trigger:** pointer-up on the rim with |angular velocity| ≥60°/s
  (VelocityTracker — plumbed in wave 1 and never queried; query it).
  Below 60°/s there is no fling: go straight to M-03.
- **What moves:** the rim angle, under inertia.
- **Curve / spring:** exponential decay, ω(t) = ω₀·e^(−2.4t) — **decay
  2.4 /s**, ω₀ clamped to ±540°/s. Detent ticks keep firing on crossings
  during the fling, thinned per Ton's velocity law (≥28 ms apart; every
  2nd detent above ~6 detents/s, every 3rd above ~12/s, amplitude −25 %).
  A fling may never carry the wheel more than 60° past the release
  position hands-off — if a test fling crosses more, halve the velocity
  clamp until it can't. Inertia is not a slot machine.
- **Duration:** until |ω| < 60°/s, then M-03 takes over. Fling + snap
  together settle ≤380 ms from release, worst case.
- **Overshoot:** the fling itself has none (it decays, it does not pass
  its own stopping point and come back); the snap's overshoot is M-03's.
- **Never moves during it:** everything in M-01's freeze list; no detent
  tick fires while the wheel is not moving.

#### M-03 · Wheel snap-to-detent
- **Trigger:** fling decay reaches |ω| < 60°/s, or release below 60°/s,
  or a programmatic value change (preset, task cue, routine pre-set).
- **What moves:** the rim angle, from current angle to the nearest
  5-min detent.
- **Curve / spring:** **SNAP-WHEEL — SpringSpec(stiffness 280,
  dampingRatio 0.78)**. Canvas fallback approximation: 300 ms,
  `cubic-bezier(0.25, 0.90, 0.25, 1.00)`, replaced by the true spring if
  the view is ever ported.
- **Duration:** **280 ms nominal, settle ≤380 ms** from release
  including any preceding fling.
- **Overshoot:** **≤2°, one pass only** (≈¼ detent), zero second
  oscillation. A detent is a notch the pawl falls into; it does not ring.
  The final detent waveform fires at the seat frame (ARRIVAL), not at
  release.
- **Never moves during it:** hub, slide, header, nav; the readout steps
  only if the snap crosses a detent line, per M-01.

#### M-04 · Hub press (BLOCKS count, Deck centre)
- **Trigger:** pointer-down on the hub.
- **What moves:** the hub only — it presses *into* the wheel: translate
  toward the panel +1 dp and scale 1 → **0.94** (the budget floor; the
  hub is the one part allowed to touch it), contact shadow collapses
  to 0 dp.
- **Curve / spring:** down — **PRESS**, **45 ms** to the bottom of the
  dip. Back — **PRESS-BACK (800, 0.65)**.
- **Duration:** dip 45 ms; **the count changes at the bottom of the dip
  (t = 45 ms)**, synchronised with the firm click; spring-back settled
  **≤190 ms** total.
- **Overshoot:** spring-back scale overshoot **≤1.03** (i.e. peak 1.03),
  one cycle — the key-cap law of §3D applied to the hub.
- **Never moves during it:** the rim does **not** rotate, rock, or
  counter-shift when the hub is pressed; the readout numeral for the
  count re-strikes instantly at t = 45 ms (computed number, M-01 law);
  nothing else on Deck.

### B — The slide-to-arm (Deck) — the centre gesture

Geometry anchor: travel = the slot's full drawable length; all fractions
below are of travel. **Two thresholds, and the distance between them is
the design:** the **gate at 0.68** (the hand feels the point of no
return) and **commit at 0.82 displayed** (the machine accepts). Wave 1
scattered this across 0.65–0.82 in four files; the range was never a
disagreement — 0.65–0.68 is where the gate *tick* lives in every version,
0.82 is where commit lives in every version. Now they are two named
events in one gesture.

#### M-05 · Slide drag
- **Trigger:** pointer-down **on the sled thumb only** — the bar does
  not teleport the sled to a touch elsewhere on the track — then move.
- **What moves:** the sled, 1:1 with the finger for authority. In the
  gate zone only (0.68–1.00), the *drawn* sled may lag the finger by
  **≤6 dp** through a stiff follow (SNAP-WHEEL spring chasing the
  finger position), recovering to 0 dp lag at rest. The finger is never
  outrun by more than 6 dp and never resisted in fact — weight is
  rendered, not enforced (see §6, item 3: the heavy resistance map is
  superseded). The orange trail fills behind the sled, drawn from the
  sled's drawn position, never the finger's.
- **Curve / spring:** direct drive outside the gate zone; SNAP-WHEEL
  follow inside it.
- **Duration:** the gesture's own. A fast arm and a slow arm both pass;
  there is **no pace gate, no velocity condition, no minimum or maximum
  drag time**, ever. Deviation costs nothing.
- **Overshoot:** none — the sled clamps at both ends of the slot, dead.
- **During the drag:** the centre label fades out by 70 % travel
  (existing 1.3× fade rate, kept); travel ticks fire on the 16-tooth
  ratchet (Ton, D2b), backwards crossings tick too, one tooth quieter.
  At **0.68 the gate tick** fires — one heavy tick, and the sled's marker
  line thickens by 1 dp on that frame (a drawn change, not a motion).
  From 0.68 the drawn lag begins.
- **Never moves during it:** the wheel, the hub, the header, the nav,
  the bar's track and radius; the label does not change its *words*
  during the drag — it only fades.

#### M-06 · Slide commit — powered snap, seat, staged seal
- **Trigger:** pointer-up with displayed fraction ≥**0.82**.
- **What moves, in sequence — three beats, never simultaneous:**
  1. **t 0–120 ms — powered snap.** The sled completes its remaining
     travel itself: **120 ms, SEAT curve**. It arrives flush at the
     slot's end. No teleport (wave 1 teleported to `dragFraction = 1`
     and instantly sprang back — the user never saw the sled arrive;
     that is fixed here by the dwell below).
  2. **t 120 ms — the seat.** Sled holds dead at the end. The harmonic
     seal fires on this frame (Ton 2c voicing). The slot's end badge
     inverts on this frame.
  3. **t 120–510 ms — the Deck reconfigures, staged, one mover at a
     time:** (a) the bar's label crossfades to the armed word — 120 ms
     fade, text swaps at the fade midpoint, this is the *only* permitted
     mid-animation word swap in the app because the fade's midpoint is
     its arrival; (b) the wheel locks — its active arc and dot drain
     from accent to ink over **240 ms, THROW**, rim input dies on the
     seat frame (the drain is the *picture* of a lock that already
     happened); (c) the NOW task line and FINISHES settle to running
     values — **instant text change at t 270 ms, 0 ms animation**.
- **Duration:** **≤510 ms total**, hard ceiling.
- **Overshoot:** 0 dp on the snap (SEAT arrives and settles; it does not
  pass the end of the slot — there is nowhere past it to go).
- **Dwell law:** the armed state must read as armed for the full staged
  sequence before anything resets or leaves this screen. The sled does
  **not** spring home inside the sequence; its reset is hidden inside
  the *next* disarm or the next Deck entry, never performed as part of
  committing.
- **Never moves during it:** nav (does not celebrate, dim, or shift),
  header pills (their values change instantly if state changes them —
  instantly, not animated), the hub.


#### M-07 · Slide refused return
- **Trigger:** pointer-up with displayed fraction <0.82.
- **What moves:** the sled only, home to rest.
- **Curve / spring:** **RETURN-CRITICAL — SpringSpec(300, 1.00)**,
  critically damped.
- **Duration:** **≈260 ms** from half travel; scale the spring's start
  velocity with release distance — from 0.80 travel, ≤340 ms.
- **Overshoot:** **zero, by construction.** A sled that didn't reach
  the latch is pulled home and *stops*. It does not bounce off the rest
  position, it does not shake, it does not flash. Silence during the
  return; one quiet landing tick at rest (Ton 2d). A refused slide is a
  **non-event in the data**: no record, no count, no residue.
- **Never moves during it:** the trail fill retreats *with* the sled
  (it is the sled's shadow, not a second mover); wheel, words, header,
  nav all still. The centre label fades back in over the final 120 ms
  of the return — the label returning is part of the sled arriving
  home, not a separate animation.

#### M-08 · Disarm (session end by user action, and emergency break completion)
- **Trigger:** disarm action, or the banked hold completing (M-18).
- **What moves:** one thing at a time, reverse of M-06, lighter: the
  wheel's arc re-accents over **240 ms, THROW**; the bar's label
  restores (120 ms fade, swap at midpoint). Seal sound is the disarm
  voicing (Ton 2c).
- **Duration:** ≤360 ms total. **Overshoot:** 0.
- **Never moves during it:** no receipt, tally, or record animates on
  Deck at disarm — the receipt line appears instantly where it lives
  (M-15 law: records are written, not performed).

### C — Thrown caps: sled, rocker, loadout cap (one law, three bodies)

Shared preamble for M-09/M-10/M-11 — all three are **the same physical
event**: a cap thrown along a slot until it hits a stop.
- **DOWN first:** on pointer-down anywhere on the control, the cap
  depresses within one frame — translate +1 dp toward the panel,
  contact shadow 2 → 0.5 dp, top lit edge dims. State has **not**
  changed; the machine has only acknowledged the hand. (Wave 1 fired
  all three on `ACTION_UP` only — the finger got nothing for the
  80–150 ms it was on the glass. That is the deficit this section
  exists to close.)
- **Draggable:** all three drag after **4 dp touch slop**, 1:1,
  clamped to the track with **≤4 dp overdrag at 0.25×** past the ends.
  A part drawn with a track and a thumb drags; a part that can be
  pulled off its slot, wiggled vertically, or flung past a stop is a
  fidget and is cut.
- **Tap = throw:** a tap (down+up <200 ms, movement <4 dp) throws the
  cap with THROW-SPRING to the tapped stop.
- **Word law:** the state word (`ARM`/`OFF`, `SHUT`/`PEEK`/`OPEN`,
  `STUDY`/`DEEP`/`SLEEP`) flips **on ARRIVAL** (§1.4) — never on down,
  never mid-flight. Mid-flight the old word stays at full ink; position
  is the in-flight readout. If word and position can disagree for one
  frame, the control is lying.

#### M-09 · Sled throw (Guard ARM/OFF, routine ON/OFF miniature sleds)
- **Trigger:** tap on the sled/row, or drag release nearer the target
  stop than the origin stop.
- **What moves:** the cap in its track.
- **Curve / spring:** **THROW-SPRING — SpringSpec(520, 0.90)**;
  Canvas equivalent **240 ms, THROW** bezier.
- **Duration:** adjacent/full throw (this is a 2-stop part, ~32 dp
  travel) settles **≤240 ms**.
- **Overshoot:** **zero.** A lever hits its stop dead. The seat is
  communicated by the latch haptic + shadow-slap (shadow snaps
  0.5 → 2 dp in 30 ms at ARRIVAL), never by visual bounce.
- **Direction is the meaning (visual half):** ON/SHUT arrival lands
  with the shadow-slap at full weight; OFF/OPEN arrival lands with the
  shadow-slap at 60 % alpha — the eye gets the same asymmetry Ton's
  waveforms carry, so the throw is readable eyes-half-open.
- **Never moves during it:** the track, the printed stops, the row's
  label and hairlines, the row background (no flash, no tint, no
  ripple — ever, on anything).

#### M-10 · Rocker throw (app-row SHUT / PEEK / OPEN, 3 stops)
- **Trigger:** tap on a stop zone, or drag release.
- **What moves:** the rocker cap.
- **Curve / spring:** **THROW-SPRING (520, 0.90)**, distance-scaled:
  adjacent stop (54 dp) settles **≤240 ms**; full traverse
  SHUT→OPEN (108 dp) — stiffness drops to **380**, dampingRatio stays
  0.90, settles **≤320 ms**. Formula for any future stop count:
  `duration ≈ 180 + 90 × stopsCrossed` ms, capped at 340 ms — velocity
  is the constant, not time. Constant-duration throws over different
  distances are why bands feel weightless.
- **Overshoot:** **zero** (heavy part, M-09 law).
- **Mid-stop event:** crossing the middle stop during a full traverse
  fires the detent tick at the crossing frame — position first, tick
  confirms. The word does **not** flip to the middle stop's word
  mid-traverse; it flips once, on ARRIVAL at the final stop.
- **PEEK (mid-stop) seat:** lands with shadow-slap at 80 % and **no**
  second-strike visual accent — a half-commit must look like a
  half-commit.
- **Never moves during it:** app icon (28 dp, fixed), app name, the
  neighbouring rows, the group header. The row does not highlight,
  expand, or indent while its cap travels.


#### M-11 · Loadout cap travel (band: STUDY / DEEP / SLEEP)
- **Trigger:** tap on a stop, or drag release (draggable per §C
  preamble).
- **What moves:** the cap on the band.
- **Curve / spring:** **THROW-SPRING (520, 0.90)** adjacent;
  full traverse stiffness 380, settle **≤320 ms** (M-10 scaling).
- **Duration:** adjacent **≤240 ms**, full **≤320 ms**.
- **Overshoot:** light cap exception — **≤1.5 % of travel or 1 dp,
  whichever is smaller**, one pass. In practice: invisible as bounce in
  a screen recording, felt as the cap kissing the stop. If it reads as
  a bounce, it is too much.
- **Fill/label law:** the cap's fill and its word key to the **animated
  position**, arriving with it — never to the committed value ahead of
  the cap, never lagging behind it. The marker line on the cap is
  constant cream, day and night.
- **Never moves during it:** the band's printed stops and labels, the
  Deck readout (the new loadout's numbers re-strike instantly on
  ARRIVAL if they change — computed law, M-01), the wheel.

### D — Keys

#### M-12 · Key dip (every cap, chip, and key: duration caps, FLOW,
#### ADD / REMOVE / DONE / DEL / START, filter words, composer keys)
- **Trigger:** pointer-down on the key.
- **What moves:** the key only — translate **+1.5 dp** toward the panel
  (legal range 1–2 dp; 1.5 is the house value, no per-key variance),
  contact shadow collapses to 0 dp, top lit edge inverts. **No scale
  change on keys** — scale is the hub's and the nav group's vocabulary
  (M-04, M-13); keys sink, they don't shrink. (Where wave-1 Saft D4
  gave caps scale 0.96: superseded — two press vocabularies for one
  class of part is how grammars rot. The dip + shadow is the whole
  vocabulary; if a key needs more to feel pressable, its *drawing* is
  wrong — recess it — not its animation.)
- **Curve / spring:** down — **PRESS curve, 80 ms** (legal range
  70–90 ms; 80 is the house value). First visible change ≤16 ms.
- **Duration:** dip 80 ms; **hold** — a held key sits depressed
  indefinitely, nothing pulses, nothing charges (the hold bar, M-17,
  is the only charging control in the app). **Release:** spring-back
  **PRESS-BACK (800, 0.65)**, settled **≤150 ms**; the key's action
  commits when the returning key crosses 99 % of its return — ≈60–80 ms
  after release — so cause and effect stay welded without waiting for
  full settle.
- **Overshoot:** spring-back **≤1.03** (peak translate −0.05 dp past
  rest, i.e. the key kisses 0.05 dp proud of its seat), one cycle.
- **Selected state:** shown by the key *staying* at its seated tone —
  never by a bounce on arrival. Pressing an already-selected key dips
  and returns and does **nothing else**: no re-fire, no animation
  beyond the dip.
- **Never moves during it:** the key's label (no shift, no weight
  change), neighbouring keys, the row or field the key belongs to.
  Composer keys appearing/disappearing with field focus is **not** a
  key motion — the key bank is either present or absent, swapped
  instantly with the focus change, 0 ms. A rising keyboard-bank
  animation is a phone gesture, not a machine one.

### E — Navigation & destinations

#### M-13 · Band-switch cap travel (bottom nav)
- **Trigger:** tap on a destination stop on the band.
- **What moves:** the travelling cap on the flush recessed rail. The
  tapped destination's icon+label group dips — scale **0.94**, 45 ms,
  PRESS — inside the cap's first 45 ms; the cap is still the one mover,
  the dip is its contact acknowledgement.
- **Curve / spring:** **THROW-SPRING (520, 0.90)**.
- **Duration:** one stop **≤240 ms**; three stops **≤340 ms**
  (distance-scaled per M-10's formula).
- **Overshoot:** **≤2 dp**, one pass (light cap law).
- **Word/icon law:** the destination's selected rendering (ink weight,
  the 3 dp armed lamp at FOCUS) applies **on ARRIVAL** of the cap, not
  on tap. Re-tapping the current destination: the group dips and
  **nothing travels** — no cap motion, no screen motion, no sound
  above the down tick.
- **Never moves during it:** the rail, the printed stops, the screen
  above the band (the destination transition, M-14, is *sequenced
  after* the cap's arrival or concurrent-but-independent — builder's
  choice of exactly one: **concurrent**, because the cap arriving on a
  screen that hasn't changed yet reads as lag; the two motions share
  one 240–340 ms window and neither waits for the other), badge counts
  (there are none — killed), the header.


#### M-14 · Destination transitions — THE PANEL TRAVEL LAW (one law, all destinations)
*This was undefined in wave 1. It is now owned, and there is exactly one
law — no per-screen variants, no exceptions list.*
- **Trigger:** destination change via the band switch (Deck 0 ·
  Shield 1 · Tasks 2 · Ledger 3 — Guard is not a destination; it is an
  overlay/drawer surface and obeys M-15/M-16's family, §F).
- **The law:** outgoing screen fades to 0 in **100 ms, linear**, while
  translating **−6 dp** in the direction of travel. Incoming screen
  starts offset **+14 dp** in the direction of travel, fades in and
  settles to 0 over **240 ms, THROW**. Total window **≤240 ms**;
  overlap is only the 100 ms out-fade. No scale, no vertical drift, no
  full-width slide (a screen-width slide is a phone gesture; a panel
  on a rail moves a short, definite distance and stops).
- **Direction logic:** destinations have a fixed order (above). Moving
  to a **higher** index: incoming enters from the **right** (+14 dp →
  0), outgoing exits **left** (0 → −6 dp). Moving to a **lower** index:
  mirrored exactly. Same-destination re-tap: **nothing moves** (M-13).
  The direction is a function of index difference's *sign only* —
  distance (1 stop vs 3) changes the band cap's duration, never the
  panel's travel distance.
- **Overshoot:** 0 dp — the panel settles at 0, it does not pass it.
- **Curve replacement note:** the wave-1 tab transition (fadeIn 420 ms
  on CurveC-family + fadeOut 260 ms, two screens ghosting through each
  other for ~260 ms) is **deleted in whole**. Panels on a machine do
  not interpenetrate.
- **Never moves during it:** the header/nameplate and the band rail
  (the machine's frame does not move when a panel changes), **list
  contents** — rows are already where they belong when the panel
  arrives: no per-row entrance, no stagger, no cascade, on any screen,
  ever — and **scroll position**: returning to a destination restores
  its scroll instantly, 0 ms, no scroll-to animation.
- **Theme change (day/night) is not a transition:** instant swap,
  **0 ms**. A lamp changing is not a fade; a 420 ms tonal crossfade
  reads as the app blushing.

### F — Drawer & overlays

#### M-15 · Drawer open / close (status drawer, Guard drawer off the band's PARK detent)
- **Trigger:** open action / dismiss action (tap-out, back, or the
  drawer's own handle).
- **What moves:** the drawer slab only — height expansion (or edge
  translation, per its mounting — the timing law is identical either
  way).
- **Curve / spring:** open — **280 ms, SEAT** (this replaces the
  wave-1 `specC(420)` expand built on convicted CurveC — same object,
  140 ms shorter, and it *arrives*). Content is clipped by the slab's
  edge; **inner rows are visible from the first frame** — no staggered
  row entrances; a drawer is one object.
- **Duration:** open **280 ms**; close **160 ms, THROW** — leaving is
  always faster than arriving, app-wide.
- **Overshoot:** 0 — the slab stops at its printed height; the content
  behind it does not dim-bounce or shift.
- **Never moves during it:** the screen beneath (no push, no scale,
  no parallax), the header, the band. The scrim, if any, is a fade
  locked to the slab's own curve and duration — it is part of the
  slab's arrival, not a second mover.

#### M-16 · Overlay entrances — speed is meaning (three classes, no more)
- **Class 1 — INTERCEPT (Airlock: a blocked app was just caught).**
  The fastest visual event in the app, because it *is* an
  interruption: full opacity in **120 ms, THROW**, with a **6 dp
  downward drop** (it falls in front of him). Overshoot 0. Anything
  softer lies about what happened.
- **Class 2 — DECISION (disarm / grace / peek overlays).**
  **240 ms, SEAT**, fade + **8 dp rise**. Overshoot 0.
- **Class 3 — RECORD (receipt, DAY CLOSED, session-end line).**
  **No entrance. 0 ms.** The line is simply *there* when he looks.
  Records are written, not performed.
- **Exits, all classes:** **160 ms, THROW**, fade, no travel.
- **Never moves during any overlay:** the screen beneath does not
  scale down, blur-animate, or drift (a static scrim is permitted; an
  *animating* backdrop is a second mover and is cut); no element
  inside the overlay staggers in; the overlay's own buttons obey M-12
  and nothing grander.


### G — The running room, the clock, and the banked hold

#### M-17 · Running-session clock (Deck, running) & routine firing
- **Countdown digits:** hard cut at the second boundary — no roll,
  flip, fade, or scale on any numeral, ever. A morphing digit is
  unreadable for its whole transition, and this screen exists to be
  read at a glance.
- **Progress dot/arc:** advances in **1-second steps**, each step a
  **120 ms, THROW** micro-tween of the arc/dot only. Continuous sweep
  is banned — sweep makes the Deck feel anxious; steps make it feel
  kept. Flow/elapsed mode: same law, inverted direction.
- **Routine firing, app backgrounded (the normal case):** **zero
  animation anywhere.** The session simply *is* running when he next
  opens the app — Deck already locked, wheel already drained, one
  line present instantly (`ROUTINE · STARTED 09:00`, human words).
  The arm sequence is **never replayed** for a session the user
  didn't arm; a replayed ceremony for an absent audience is confetti.
- **Routine firing, app in foreground:** M-06's reconfiguration in
  compressed form — wheel drains 240 ms THROW, bar word swaps at its
  fade midpoint, total **≤360 ms**, and **no modal, no toast, no
  banner**. The seal haptic in his hand is the notification.
- **Routine END:** state restores instantly; the only motion is the
  completion unlock (arc re-accents, 240 ms THROW) after the readout
  holds `00:00` for a full second.
- **Never moves while a session runs:** everything else on Deck.
  A running Monolith is a still room with one stepping hand.

#### M-18 · AOD desk-clock patrol shift
- **Trigger:** the minute boundary (the display update and the
  anti-burn-in shift are the *same* wakeup — protection costs zero
  extra power).
- **What moves:** the whole AOD composition, as one block.
- **Curve / duration:** **0 ms. A discrete jump, never a glide.** A
  gliding clock face reads as a screensaver and defeats the shift's
  purpose.
- **The shift:** translate by (dx, dy), dx ∈ {−16, −8, 0, +8, +16} dp,
  dy ∈ {−10, 0, +10} dp, chosen with **no immediate repeat** of the
  previous offset. Every **30 minutes**, additionally swap the anchor
  block (upper-third ↔ centre) and swap which reading is large
  (elapsed ↔ remaining) — also 0 ms, also on a minute boundary.
- **Digits:** update once per minute, aligned to the boundary, hard
  cut (M-17 law). **No seconds, ever.** The colon never blinks (render
  it at 45 % of numeral ink — a blinking colon is a burn-in machine
  toggling the same pixels at maximum duty cycle).
- **Staged dimming:** after 20 continuous minutes, all readings drop
  one luminance step and hold. The clock gets *quieter* the longer
  the session runs; it never gets brighter on its own, and
  notifications never wake it. The single permitted breach: at
  REMAINING = 0, the armed dot and elapsed reading step up for exactly
  60 seconds — an instant step, not an animation — then return.
- **Overshoot:** not applicable — nothing travels; positions change
  by fiat at the boundary.
- **Never moves:** nothing *within* the composition relative to
  itself; no element drifts, pulses, or breathes independently. The
  composition is one block or the patrol is a lie.

#### M-19 · THE BANKED EMERGENCY HOLD — build, bank, slip, drain, complete
*The control: full-width hold bar on the running Deck (entry strip
56 dp tall at rest; active surface full width × 112 dp). 60 held
seconds total. Checkpoints at 15 / 30 / 45 bank progress in segments.
One use per week; banking alone never consumes it — only completion
does. Held time is measured on a monotonic clock.*
- **Entry (contact):** pointer-down anywhere on the entry strip —
  any contact holds it: thumb, palm edge, two fingers; the contact
  centroid is ignored, multi-touch counts as contact, there is no
  sub-target to aim at or miss. The strip expands upward to the
  112 dp active surface over **160 ms, SEAT**. **Hold time accrues
  from the first contact frame, not from the expansion's end** — the
  animation never eats his seconds. This expansion is the entry's
  only motion.
- **Build (holding):** **there is no animation.** Fill travels the bar
  left-to-right **direct-drive, LINEAR-MEASURED, 1:1 with measured
  held time**, updated per frame with no easing anywhere. The hero
  numeral above the bar (`HELD 0:37`, Anonymous Pro) counts **up**,
  stepping once per second (measured number, M-01 law). Fill is
  position, not a second rendering of the total; the numeral is the
  total's one home. Bar compression: the surface sits 1 dp deeper
  than the entry strip for the whole hold — set at entry, static
  thereafter. Nothing strains, squeezes, or escalates visually;
  escalation lives in Ton's tick ladder, not in the geometry.
- **Banking (crossing 15 / 30 / 45):** on the crossing frame, the
  earned segment's fill **locks solid — instant tone change, 0 ms**
  (banked segments render solid cream; the unbanked tail renders in
  the capped tone — density is the difference, readable at a glance),
  the notch word beside that checkpoint flips to `BANKED` **on that
  frame** (this is an arrival: the segment *has* arrived, permanently),
  and the bar gives one visible **2 dp step-down over 50 ms, SEAT,
  returning over 120 ms, THROW** — a pedal passing a detent. One
  mover: the bar. Nothing else on screen acknowledges the bank.

- **Slip → grace → drain (the heart of this row):** contact lost for
  any reason (lift, slide-off, tremor break):
  1. **800 ms grace — no drain at all.** The displayed value freezes.
     Re-grip inside the grace resumes accrual from the exact displayed
     value, on that frame. A bounce or a knuckle shifting costs
     nothing — tremor is not intent, and this control is operated by
     frightened hands.
  2. **Then the tail drains at 2 banked-seconds per real second**
     (rate **2:1**), **LINEAR-MEASURED**, fill visibly retreating
     toward the floor, the hero numeral counting **down** in step
     with the displayed value (it is still a measured number — it
     steps, 4 steps/sec at this rate: render the numeral at 250 ms
     steps during drain, value always exact at each step). A draining
     tick every 250 ms (Ton's side) lets him *feel* the loss without
     looking. Worst case: a full unbanked tail is <15 s of progress,
     so a drain lasts **<7.5 s** — long enough to be watchable and
     re-grippable, never a punishment film.
  3. **The floor never drains.** Drain stops dead at `banked`
     (15 / 30 / 45). Banked floors are solid (see Banking) and do not
     retreat, flicker, or dim while the tail drains past them — the
     eye must see exactly what is safe and what is leaving.
  4. **Re-grip at any moment** halts the drain **on that frame** and
     resumes accrual from wherever the value has fallen to. No
     re-entry animation repeats; the surface is already expanded.
  5. **Close:** if the tail reaches the floor and no re-grip comes
     within **10 s**, the attempt closes: the bar collapses to the
     entry strip over **160 ms, THROW**, and the strip now truthfully
     reads `BANKED 30 — PRESS AND HOLD TO CONTINUE` for the rest of
     that session. Banked progress persists **for that session only**;
     it resets at session end and is rendered nowhere else.
- **Completion (60.0 held seconds):** on the completion frame —
  blocking lifts, the weekly use is written, the disarm seal fires.
  The running Deck's composition rises **2 dp over 420 ms,
  THROW-family easing `cubic-bezier(0.25, 0.60, 0.30, 1.00)`** — the
  one duration in the app allowed past 360 ms outside M-19's own
  drain, because this is the machine *opening*, the heaviest state
  change it owns. (This bezier is a named exception, not a new class:
  it exists only here, and any reuse elsewhere is a bug.) Then M-08's
  disarm runs. No celebration follows. Ever.
- **Spent state:** if the week's break is already used, the entry
  strip reads `BREAK USED — BACK [reset day]` and contact on it does
  **nothing** — no expansion, no dip, no response beyond the strip's
  existing word. A spent control that still invites a 60-second hold
  would be a trap and a lie.
- **Overshoot:** none anywhere in this row. Fill never passes measured
  time; drain never passes the floor; the completion rise settles at
  2 dp exactly.
- **Never moves during the hold:** the Deck behind the bar (the
  countdown steps per M-17 and nothing else), the numeral's *position*,
  the notch marks (printed scale), the header, the nav. Zero orange
  anywhere on this control — orange is COMMIT, and colouring the
  emergency exit like a commit button breaks the ration and
  advertises the exit all session.

#### M-20 · Weekly bank return (Shield, first open of a new week)
- **Trigger:** first app open where the week has rolled over **and**
  the bank sled was spent or partially banked.
- **What moves:** the Shield bank sled only, to full.
- **Curve / spring:** **BANK-RETURN — SpringSpec(110, 0.85)**.
- **Duration:** **650 ms**. Ticks fire as it passes each printed
  checkpoint (15 / 30 / 45); one arrival knock at the full stop.
- **Overshoot:** ≤1 % of travel, one pass.
- **If the break was never used: nothing animates and nothing
  announces.** The sled was already full; a new week over an untouched
  bank is a non-event, and the app says so by silence. No notification,
  no badge, no banner, ever.
- **Never moves during it:** everything else on Shield; the return
  never blocks or precedes an arm (it is never on Deck).

### H — Ledger records (one row, because records barely move — deliberately)

#### M-21 · Ledger on load & on completion
- **On load:** hero stat, 7-day columns, receipts, tower — **all set
  instantly, 0 ms.** No counting-up numbers, no rising faders, no
  brick-by-brick build. These are readings of the past; a gauge that
  animates its history is lying about having measured it.
- **The single exception:** if the Ledger is *on screen at the moment
  a session completes*, today's column may rise **once, 360 ms,
  THROW**, and the new receipt row appears instantly at the top of
  its list (0 ms — the row does not slide in). That change happened
  *now*, in front of him; everything older is set in print.
- **Never animates here:** accession numbers, totals, historical rows,
  absence (a zero day renders as a gap/word, instantly — never an
  animated empty tube).

---

## 4. THE NEVER-ANIMATE LIST — app-wide, permanent

If a future change animates anything on this list, it is wrong
regardless of how good it looks. (N = never.)

1. **N-01 Ripple** — Material ripple, state-layer tints, expanding
   rings, glow-pulses on press. Purged app-wide. Water has no place
   on a machined panel.
2. **N-02 Printed things** — hairlines, tick rings/scales as objects
   (the rim's ticks move only *as* the rim), printed stops, notch
   marks, legends, the nameplate/model plate.
3. **N-03 The frame during transitions** — header and band rail never
   move, dim, or celebrate when a panel changes or a session arms.
4. **N-04 Counts & pills as motion** — badge counts, header pill
   values, shut counts: instant change, 0 ms, always.
5. **N-05 Numerals rolling/flipping/morphing** — no rolling counters,
   no flip-clock morphs, no tweened numbers, no count-ups. Chosen
   numbers step, measured numbers step, computed numbers re-strike
   (§3 M-01).
6. **N-06 The orange breathing** — the accent never pulses, breathes,
   glows, blinks, or sweeps. Including the "one ambient halo"
   proposals: **no ambient motion exists in this app, full stop.**
   The armed dot on AOD is lit or unlit; it does not blink.
7. **N-07 List entrances** — no per-row entrance, stagger, cascade,
   or fade-up, on any list, on any screen, including first load.
8. **N-08 Scroll, typing, keyboard** — system behaviour, untouched.
   No scroll-to animations, no field-focus zooms, no key-bank rise
   (M-12).
9. **N-09 History performing itself** — Ledger counters/faders/tower
   on load (M-21 is the whole law).
10. **N-10 The hub spinning; the tick ring rotating independently of
    the rim; the wheel spinning on its own** (no self-winding
    *animation* on Deck — a routine's pre-set value arrives by M-03's
    spring if he is watching, or is simply *there* if he isn't).
11. **N-11 Seal/arm ceremony replays** — re-entering Deck never
    replays the seal sequence; a routine that fired in the background
    never performs its arming (M-17).
12. **N-12 Error theatre** — no shake, no bounce-off-walls, no red
    flash, no descending animation for any refusal, failure, or
    denied action. Refusal = stillness + the truthful word.
13. **N-13 Skeleton shimmer / loading pulses** — loading renders as
    the printed frame with values absent, statically. Nothing
    shimmers on a bench instrument.
14. **N-14 Idle motion of any kind** — no breathing logos, no drifting
    backgrounds, no animated illustrations, no looping anything.
    The only repeating visual events in the app are the 1-second
    clock step (M-17) and the AOD minute jump (M-18) — both are
    *measurements*, not ambience.
15. **N-15 Scale below 0.94; squash-and-stretch; rotation wobble;
    skew** — on anything, for any reason.
16. **N-16 The AOD colon blinking; AOD glide transitions** (M-18).
17. **N-17 Eased timers** — hold fill, hold drain, any progress that
    claims to *be* time (LINEAR-MEASURED only, §1.1).
18. **N-18 The spent/disabled control responding** — a spent hold bar,
    a locked wheel, a dead sled: no dip, no tease-motion, nothing.
    Stillness is the answer (M-19).
19. **N-19 Celebration** — no checkmark draws, no confetti, no brick
    ceremonies, no streak flames igniting, on any completion. The
    machine winds down; it does not celebrate (M-17/M-21).
20. **N-20 Mid-flight word flips** — banned as a class (§0.3), listed
    here so a future "small optimisation" can't smuggle one in.
21. **N-21 Second movers** — any animation whose existence requires
    another element to move in sympathy (backdrop scale/blur-anim,
    neighbour-row parting, header reactions) is cut as a class.
22. **N-22 Theme crossfades** — day/night is an instant swap (M-14).

---

## 5. Build notes (so nobody has to ask)

- **Animate only what is drawn.** Every spring/animator must drive the
  exact value the renderer reads. An invisible settle is worse than
  none — it still charges its duration in haptic expectation.
- **One spring implementation.** Compose `spring()` and one equivalent
  underdamped animator for Canvas views, parameterised by §1.3's
  (stiffness, dampingRatio) pairs. No per-view hand-rolled physics.
- **Haptics belong to motion events** (crossing, arrival, seat),
  never to raw touch events. If a feel call sits directly in an
  `ACTION_DOWN`/`ACTION_UP` branch instead of a crossing/arrival
  callback, it is early or late by up to 200 ms and it moves.
- **Verify on device at 120 fps:** DOWN-response ≤16 ms; word-flip
  frame == seat-haptic frame; each spring's 10–90 % and settle inside
  its row's window; CurveC's arrival test (§1.2) re-run against any
  easing anyone proposes, forever.
- **Duration vocabulary, final:** 0 (instant) · 45–90 (press) · 120
  (snap/intercept/step) · 160 (exits/drawer close) · 240 (throw) ·
  280 (snap/drawer open) · 320–340 (full traverse) · 360 (foreground
  routine fire) · 510 ceiling (slide seal staging) · 650 (weekly bank
  return) · 420 exception (hold completion rise, M-19 only). Wave-1's
  {180, 260, 280, 420, 520} vocabulary is dissolved into this table;
  180 and 520 had zero call sites and are not mourned.

---

## 6. Superseded wave-1 numbers — each with its reason

| # | Superseded | Replaced by | Reason |
|---|---|---|---|
| 1 | Wheel readout updates continuously from angle (Motion D1) | Readout **steps** on detent crossings; computed numbers re-strike (M-01) | A continuous readout prints values the wheel cannot set for ~600 ms of every spin — motion printing lies. Vega (physics) and Aldo (typography) reached this independently in the plan wave; TRUTH once outranks smoothness. |
| 2 | Sled word flips at travel midpoint >0.5 (Motion D2) | Word flips **on ARRIVAL** (M-09/10/11) | Settled state grammar in the brief, verbatim: *state words flip ON ARRIVAL, never mid-flight.* Mid-flight flip means word and position can disagree; position is the in-flight readout. |
| 3 | Slide resistance map d = r − 0.22r³, commit requiring raw 0.97 (Saft D2) | 1:1 authority; ≤6 dp *rendered* lag in the gate zone only (M-05) | The Synthesis already applied the humans' ceiling: *no strain wall, gentle resistance only.* A map where the thumb outruns the sled by 22 % taxes exactly the tired, shaky hand the slide exists for. Weight is rendered (lag + gate tick), never enforced. |
| 4 | Valve drain proportional, ≤1200 ms, on CurveC (Saft D5) | Banked law: 800 ms grace → tail drains **2:1**, floors at 15/30/45 never drain (M-19) | The BANKED hold is settled law (Sarthak's D-C): slip drains, never zeroes, and banked segments are *floors*, not a slower route to zero. CurveC is convicted besides. Rosa's plan-wave debounce (800 ms) adopted — tremor is not intent. |
| 5 | Key caps press by scale 0.96 (Saft D4) | Keys **sink** (+1.5 dp, shadow collapse), no scale (M-12) | One press vocabulary per class of part. Scale belongs to the hub and nav group; dip belongs to keys. Two vocabularies for keys is how grammars rot. Scale floor 0.94 still governs where scale *is* used (M-04, M-13). |
| 6 | Wheel end-stops rubber-banded, 0.32×, ≤10° overdrag (Saft D1) | **Dead clamp**, 0° overdrag (M-01) | A machined end stop does not flex. Rubber-banding is the phone-UI material this app's whole surface language refuses; the knock communicates the wall. |
| 7 | Tab cross-dissolve 420/260 ms (Motion D4's target, wave-1 code) | **Panel Travel Law**: 100 ms out / 240 ms in, 14 dp, index-sign direction (M-14) | Was undefined as a *law* in wave 1; the brief assigns it to this seat. One law, named curve, direction logic — now closed. |
| 8 | Latch/seat haptic at 92 % crossing (Saft D3) | Seat haptic **on the ARRIVAL frame** (§1.4 definition) | One frame definition for word + haptic + shadow; "92 %" and "arrival" disagreeing by a frame or two is exactly the class of lie T6 bans. If hardware recordings prove the spring's audible seat precedes visual arrival, the *definition* gets revisited with evidence — flagged to Saft by mail. |
| 9 | Slide commit snap ~90 ms + crossfade-to-running 260 ms (Motion D3 / Saft D2 variants) | **120 ms SEAT snap** + staged reconfiguration ≤510 ms (M-06) | The brief's settled number is ~120 ms powered; the staged 510 ms ceiling is the brief's own dwell law. The 260 ms whole-Deck crossfade dissolved the staging into one blur — beats must be readable one at a time (one mover rule). |

Numbers carried over *unchanged* because they survived: fling decay
2.4/s · wheel snap 280 ms / 0.78 / ≤2° · THROW-SPRING 520·0.90 at
240 ms, distance-scaled · key dip 1–2 dp over 70–90 ms, spring-back
≤1.03 · gate 0.68 inside the brief's 0.65–0.82 band, commit 0.82 ·
seal dwell ≤510 ms staged · Saft's global budget (§1.4) · night
×0.6 / ×0.85 on Ton's side, eye paired per §1.5.

---

## 7. Coordination & process

- **Inbox at start:** empty (only the `read/` subfolder). Checked
  again before finishing: still empty.
- **Wall:** read in full before writing. Mara's light law (light never
  carries data) is compatible with everything here — no motion in this
  table asks light to mean anything; the specular rig, if built, is a
  pure function of position and adds no mover. Rosa's banked-valve
  spec is the direct ancestor of M-19 (her grace + drain rate + floors
  adopted whole; her 420 ms completion rise kept as the named
  exception). Fen's gate-at-0.68 with no pace condition is M-05's
  spine.
- **Mail sent:** to **Saft** (budget adopted whole; two disagreements
  flagged honestly — 92 % latch vs arrival frame, and the cut
  resistance map) and to **Ton** (night eye-pairing law + the
  arrival-frame synchronisation his seat waveforms must land on).
  Neither fight blocks this file: both are one-frame/one-curve
  questions with an evidence route named.
- **Ledger question** (Ren's 05:15 wall post): motion's one sentence —
  the Ledger's *tab* status changes nothing in this grammar; M-14's
  index order gains or loses a stop and M-21 already governs its
  content either way. The grammar is indifferent; the record's home
  is not a motion question.

— Kin, Designers (motion). Filed 05:19 IST, Oct 29 2026.
