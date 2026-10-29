# PLAN — Rad (Improvers) — the rotary wheel, Deck

Element: `RotaryWheelView` in `ui/views/BlockitCoreViews.kt` (class starts line 317),
mounted by `TactileDeckScreen.kt` at `fillMaxWidth × 240dp`, idle only.
Coordinator: Ilya. Wave: PLAN — no code in this document's making.
Freedom note: written under Sarthak's citation-lifting order. Wave-1 and the
synthesis are my floor and evidence, not my law; every break is named in CONFLICTS.

Nominal geometry at shipped size: height 240dp → `outerR (R) = min(w,h) × 0.45 ≈ 108dp`.
All radii given as offsets from R so the spec survives any width.

---

## CURRENT — truthful, from the code in front of me (Oct 29, ~05:15 IST)

### What Round 0 actually fixed since wave-1 (verified line by line)

1. **The scale stopped lying.** Tick loop is now `for (m in 5..180 step 5)` — 36 ticks,
   one per real detent; major `m % 30 == 0`, mid `m % 15 == 0`. Wave-1's 61 ticks at
   2.92-min intervals with majors on 34.2/63.3/92.5 min are gone. This was wave-1's ★.
2. **The settle is wired.** `onDraw` derives sweep, arc and thumb from `wheelAngleDeg`;
   `minutesToAngle()` returns the one true frame (120°…420°); `animateWheelSettle()`
   animates that same field (280ms CurveB, 420ms if Δ > 60°) and now visibly moves.
   Wave-1's "animator animating nothing" is dead.
3. **A fling exists — as a projection, not a travel.** On UP the tracker is finally
   queried (`computeCurrentVelocity(1000)`), ω = `(dx·vy − dy·vx)/r²` in °/s; if
   |ω| > 40°/s the target becomes `wheelAngle + ω × 0.18s`, clamped 120…420, snapped,
   then the settle animates there. There is no per-frame fling motion of its own.
4. **Real fonts shipped.** Sora Medium/SemiBold/Bold, Atkinson Bold, Anonymous Pro
   Bold are true TTFs; the Canvas typefaces load the real Bold files. The synthesized
   smear wave-1 complained about is gone from this view's paints.
5. **Night staircase rebuilt around it.** Tokens in this file now: night ground/surface
   `#171412`, socket variant `#100E0C` (recess now genuinely *below* ground),
   hub bright `#362C27`, text primary `#D2C6BB`, secondary `#9C8E84`.

### What is still exactly as wave-1 found it (the honest remainder)

- **Drag is still absolute.** `ACTION_DOWN` anywhere outside the hub calls
  `updateMinutesFromTouch()`, which maps the finger's absolute `atan2` angle straight
  to a value. Tap opposite the thumb → the value teleports. No grab offset, no annulus
  gate: a touch on the blank field between hub and rim also sets the time.
- **Ticks and arc still share one band.** The re-indexed ticks stayed at their old
  radii (outer R−11; inner R−16 minor / R−18.5 mid / R−21 major) and the 4dp arc still
  sits at `rimR = R−16`, spanning R−18…R−14 — straight through the tick band. Truth
  arrived; separation did not. At render size the rim is still one grey smear
  (see `shots/view-deck8n.jpg`, `shots/night/n01_deck.png`).
- **Knurl is still the parity zebra.** 90 teeth, every 4°, 1.3dp stroke, R−4.5…R−1
  (3.5dp long), colour alternating `0x2E000000` / `0x30FFFFFF` by `tooth % 2` —
  identical at top and bottom. Printing, not light.
- **Night thumb/arc still cream (X1, locally unfixed).** Thumb fill and active arc use
  `tokPrimary()` = `#FFE4CF` at night; pip `tokOnPrimary()` = `#1B110D`. `tokControlFill()`
  (`#4A423C` night) exists in this same file and other views use it — the wheel doesn't.
  In the night render the cream thumb disc is the brightest object on the panel.
- **No rim numerals.** No 30/60/90/120/150/180 anywhere. The wheel cannot be read from
  its own scale; the 84sp readout above it does all the work.
- **Haptics still conflate three events.** Every 15-min crossing fires `fireFeel(4L)` →
  `playMechanicalLatch` (waveform `[0,11,26,9]` @ `[0,215,0,150]`, ~46ms, 760Hz/16ms,
  gain 0.54) *instead of* the detent — and the same latch fires on hub tap and on
  **every** settle, including releases that changed nothing. `fireFeel`'s own contract
  comment admits pitch is ignored outside the 1L branch, so the 15-min latch also
  throws away the pitch ramp. Minor detent remains the good idea: `playRotaryDetent`,
  `[0,7,5]` @ `[0,180,70]`, 6ms, gain 0.38, pitch 340→880Hz by value. **No rate limit**:
  a fast spin queues one click per 5-min crossing into a 64-deep queue → buzz.
- **Ends are hard clamps.** `coerceIn(120,420)` in drag, projection and settle. No
  resistance, no overshoot, no end event. 5 and 180 feel like everywhere else, only
  quieter (the finger just stops producing changes).
- **During drag the thumb disagrees with the value.** `updateMinutesFromTouch` sets
  `wheelAngleDeg = clampedDeg` (continuous finger angle) while `minutesValue` is the
  snapped detent — so mid-drag the thumb sits *between* detents the scale claims are
  the only stops, then settles. Half-honest: position is continuous, stops are not.
- **No drag state, no accessibility.** `isDragging` is never read by `onDraw` (thumb
  never grows, nothing acknowledges the grab). No `contentDescription`, no step
  semantics. `isLockedSession` still only returns `false` on touch and draws
  identically; the Deck never mounts the wheel while running (it swaps in a 6dp
  progress track and always passes `false`) — the locked branch remains dead code.
- **Hub unchanged and oversized.** 0.52R (≈56dp radius), flat offset shadows retained
  on hub (`0x26000000` @ +2dp) and thumb (`0x42000000` @ +2.5dp); label BLOCKS/FLOW
  9.5sp Atkinson Bold, numeral 24sp Anonymous Pro Bold coloured `tokPrimary()` (cream
  at night — legitimate as *type*, noted, not a defect). Two small lies of plumbing:
  the view clamps blocks 1…6 while the Deck cycles 1…4; the touch hub radius uses
  `min × 0.46 × 0.52` against onDraw's `min × 0.45 × 0.52` — a 0.46 that matches nothing.
- **Dead annulus unchanged.** ~31dp of blank disc between hub edge and tick inner ends,
  crossed only by the arc.

**Headline:** Round 0 made the wheel *honest and animated*. It did not make it
*held* (absolute drag), *readable* (no numerals, fused bands), or *quiet at night*
(cream thumb). Those three are the ladder.

---

## LADDER — 5 rungs, each independently shippable

Shared vocabulary for every rung: curves are `MonolithCurves` (A = 0.2,0,0,1 ·
B = 0.05,0.7,0.1,1 · C = 0.3,0,0.8,0.15 · MechanicalOvershoot = OvershootInterpolator
1.25); durations only from {180, 260, 280, 420, 520}ms unless a rung says otherwise.
Sound is the existing synth (4 partials {0.6, 0.28, 0.37, 2.2}, dual decays) — no files.
"Engine: existing" means no new `TactileSoundEngine` method is needed.

### Rung 1 — THE HAND OWNS THE THUMB (relative drag + honest feel)

*Mechanism — touch only; no geometry changes.*
- On `ACTION_DOWN`: compute touch radius. Hub (`< drawHubR × 0.85`, using onDraw's own
  0.45 factor — the stray 0.46 dies here) → block-cycle tap, unchanged behaviour.
  Annulus `R−64dp … R+7dp` (knurl, tick/arc band, field edge, socket lip) → begin drag
  with `grabOffset = touchAngle − wheelAngleDeg`. Everything inside that annulus →
  dead: a resting palm sets nothing. The inner edge is set at 0.41R (≈ R−64dp at
  nominal) to coincide with the hub's touch radius in Nabe's plan — the band between
  the hub cap and the drag annulus is a REST ZONE (see CONFLICTS): `ACTION_DOWN`
  there arms nothing, cycles nothing, starts no drag; a drag already in progress
  passes over it unaffected, because only DOWN dispatch is gated, never MOVE.
- On `MOVE`: `rawAngle = touchAngle − grabOffset`, clamped as today; the thumb can
  never jump, because the value is a *delta* from where the finger landed.
- `wheelAngleDeg` during drag = the **snapped detent angle**, not the raw angle
  (kills the mid-drag position/value disagreement in CURRENT). The raw angle is kept
  in a field for Rung 3's magnetism; at this rung it is not drawn.
- *Curve + ms:* none new — settle stays 280ms CurveB / 420ms CurveB (Δ > 60°).
- *Haptics:* minor detent unchanged (`playRotaryDetent`, `[0,7,5]` @ `[0,180,70]`,
  pitch 340→880Hz) **but rate-limited to one feel per 35ms** — crossings inside the
  window are drawn, not felt or queued. 15-min crossings revert to minor detents
  (the latch leaves the scale entirely at this rung). Settle fires its feel **only if
  the value changed during that gesture**; a no-change release is silent. Hub tap
  keeps the latch — it is now the *only* latch on this view, so it finally means one
  thing: "you tapped the hub."
- *Sound:* detent transient unchanged (6ms, gain 0.38, pitch by value); under the
  rate limit, skipped crossings emit nothing (no queue build-up, no buzz).
- *Depth/tokens:* untouched. Night cream thumb survives this rung — named, not hidden;
  it dies in Rung 2.
- *States:* idle/dragging draw identically (growth is Rung 3); locked still unmounted
  by the Deck, as today.

### Rung 2 — THE RIM READS (separated bands, numerals, night discipline)

*Mechanism — one onDraw rebuild of the rim; touch inherits Rung 1.*
- Radial stack, outside in: socket R+7dp (unchanged tones/arcs) → knurl band
  R−5.5…R−1 (unchanged zebra at this rung) → groove: 1dp dark circle at R−7.5dp,
  black @ 16% day / white @ 8% night → **tick band moved inboard, clear of the arc**:
  outer end R−13dp for all ticks; minor (5-min) inner R−18dp, mid (15-min) R−20.5dp,
  major (30-min) R−23dp; widths 1.0 / 1.3 / 1.8dp as today → **arc channel** at
  `rimR = R−30dp`, stroke 4dp, round cap, track hairline, active as colour below.
  First time knurl, scale and arc occupy three bands with air between them.
- **Numerals:** `30 60 90 120 150 180` printed upright (never rotated), centred on
  their detent angles at R−42dp, Anonymous Pro Bold 9.5sp. At the start stop, print
  the word **FLOW** (Atkinson Hyperlegible Bold 8sp, letterSpacing 0.12) instead of
  "5" — because at 5 the wheel does not mean 5 minutes, it means open flow (the
  readout itself says NO LIMIT there); printing "5" would be the scale lying again,
  politely. The numeral nearest the current value (within half a detent of a major)
  draws at full `tokTextPrimary()`; the rest at `tokOnSurface()` day `#4E4948` /
  night `#EDE3D8` @ 85%.
- Thumb: stays a 14dp cap, now centred on the new channel (R−30dp). **Fill →
  `tokControlFill()`** (day `#252323` — no visible change; night `#4A423C`), pip
  inverts honestly: day `#FBF1EC`, night `#FFE4CF`. This closes X1 on this element
  with a token that already exists.
- Hub: 0.52R → **0.46R** (≈50dp), widening the field the numerals now live in.
  Label/numeral type unchanged; hub numeral colour → `tokTextPrimary()` both modes
  (it is type, and type has its own token — `tokPrimary()` was a fill doing a
  type's job).
- *Curve + ms:* none (static redraw). Numeral emphasis switches discretely on detent
  commit — no fade, no animation on a scale.
- *Haptics/sound:* inherited from Rung 1, verbatim.
- *Depth/tokens:* staircase panel → socket (−1) → disc (0) → groove (line) → printed
  scale (0, flat) → arc channel (0) → hub (+1). Flat offset shadows still present at
  this rung (they die in Rung 4). One new local value only: night active arc/ticks
  `#D8C3B0` (cream pulled to ~72% value — a 300° sweep becomes a reading, not a
  lamp); day active stays `#252323`. Accent: **zero on this element, both modes** —
  the slide owns the Deck's orange; the wheel's state is position.

### Rung 3 — PHYSICS (fling that travels, magnetic detents, ends that push back)

*Mechanism — motion system replaces the projection hack; needs Rungs 1–2.*
- **Fling:** on UP, ω from the tracker as today, but the threshold rises 40 → 120°/s
  (below that, a release is a placement, not a throw). Above it: a real animator
  drives `wheelAngleDeg` per frame, **420ms cap, CurveC**, travel `ω × 0.22s`,
  integrated so an end hit stops the fling *that frame* and fires the end event —
  no tunnelling through 180, no target computed in the dark.
- **Magnetic detents:** while dragging, when the raw angle is within 2.2° of a detent,
  `display = raw + (detent − raw) × 0.45`. The thumb visibly leans into the notch
  before the value commits; commit (callback + feel + sound) still happens only on
  detent crossing.
- **Rubber-banded ends:** raw angle beyond 120°/420° displays at
  `end + (raw − end) × 0.32`, capped at ±7° overshoot. Release inside overshoot:
  spring to the end detent, **240ms MechanicalOvershoot (1.25)** — the one duration
  outside the standard set, justified: it is a spring-back, and 180ms reads as a
  snap while 280ms reads as a settle; 240 is the spring's own time.
- **Settle, tiered:** ≤ half a detent → **180ms CurveA**; larger → **280ms CurveB**;
  post-fling arrival → 280ms CurveB. (The 420ms settle tier dies — long settles were
  the projection's apology.)
- *Haptics (new named engine methods, built on the existing `triggerWaveform` +
  `enqueueTransient` machinery — additive only, no existing method changes):*
  - Minor detent: existing `playRotaryDetent`, unchanged.
  - **Major detent (30-min), replaces nothing — it is new:** `playWheelMajor(pitch)` —
    waveform `[0,7,5]` @ `[0,195,75]`, fallback 4ms; sound = pitch-by-value at the
    major, 8ms, gain 0.46, chordRatio 1.1. Heavier than a minor, lighter than any
    latch, and *pitched*, so majors join the ramp instead of breaking it.
  - **End stop:** `playWheelEndStop()` — waveform `[0,12,16,9]` @ `[0,230,0,165]`,
    fallback 8ms; sound 210Hz, 18ms, gain 0.50, chordRatio 0.9 — pitched *below* the
    whole 340–880 ramp, so both ends are identifiable blind. Fires once on overshoot
    entry; re-arms after the raw angle returns 2° inside the range.
  - **FLOW entry** (value becomes 5 by drag, fling or settle): the end-stop's softer
    sibling — waveform `[0,9,20,7]` @ `[0,180,0,120]`; sound 260Hz, 10ms, gain 0.44,
    chordRatio 0.9. Entering flow feels like a gate, not a wall — the two ends must
    not feel the same, because they do not mean the same.
  - Settle seat: minor detent at the landed pitch, only if the value changed
    (Rung 1's rule, now with a name). Grab: `[0,3]` @ `[0,90]`, silent. Release with
    no change: nothing — a control that clicks for a non-event trains the user to
    ignore its clicks.
- *Sound under speed:* while crossings arrive faster than 1 per 35ms, detent gain
  drops 0.38 → 0.22 — a fling sounds like a ratchet winding down, not a machine gun.
  Majors and end stops always fire at full gain.
- *Depth/tokens:* inherited from Rung 2. *States:* dragging — cap grows 14 → 16dp
  radius over **120ms CurveA** (and back on release); ticks within ±10° of the thumb
  draw at full active colour while dragging. Idle and locked as Rung 2.

### Rung 4 — LIGHT (the knurl, honestly priced — a break with the synthesis)

*Mechanism — onDraw rim rebuild; needs Rung 2's bands.*
- **Knurl rebuilt as light, not stripes:** 120 teeth every 3°, R−5.5…R−1, stroke
  1.1dp. Light fixed at top (−90°). A **precomputed 120-entry colour table** (built
  once at construction, zero per-frame allocation): for tooth angle θ,
  `t = (1 + cos(θ + 90°))/2` (1 at top, 0 at bottom); each tooth draws a shadow line
  (black @ 10% + 20%·t, day) plus a 0.7dp catch line offset +0.8dp tangentially
  (white @ 8% + 26%·t, day). Night: same table, alphas × 0.8. The rim brightens
  toward the top of the panel in both modes — one light source, obeyed.
- **Shadows die.** The hub and thumb flat offset circles are deleted, not softened.
  In their place: hub edge-light arcs (top 200°…340° white @ 14%, 1.5dp, inset 1dp;
  bottom 20°…160° black @ 17%, 1.5dp, inset 1dp — the socket's own recipe, one
  family), and two turning lines on the field (hairline circles at hubGroove +3dp
  and +7dp, black @ 6% day / white @ 5% night) so the dead annulus reads as machined
  face, not blank.
- *Curve + ms:* none. The light does not move; the wheel turns under it only in the
  sense that the teeth are fixed to the panel, not the value — the knurl is grip,
  not indication, and it must NOT rotate with the value (a rotating knurl would lie
  about what is turning: nothing physical turns here except the thumb's position).
- *Haptics/sound:* inherited from Rung 3, verbatim.
- *Depth/tokens:* final staircase — panel → socket (−1, arcs narrowed to 2dp/1.5dp)
  → disc (0, seam hairline) → groove (line) → printed scale (0) → arc channel (0)
  → field (turning lines) → hub (+1, edge light). No shadow paints anywhere on
  the element. No new tokens; the light table is the view's only new state.

### Rung 5 — THE BLIND WHEEL (peak rung; settable eyes-free, honest when locked)

*Mechanism — semantics + state rendering; needs Rungs 1–3 (4 optional).*
- **Accessibility as instrument, not compliance:** `contentDescription` =
  "Focus duration, {value} minutes" on every detent commit; at 5, "Focus duration,
  open flow". The view exposes discrete step semantics (+/− 5 min) so switch/keyboard
  control moves in the same detents the finger feels — one scale, every hand.
- **Eyes-free setting, completed:** Rung 3's pitched ramp + pitched majors + the two
  distinct end voices already let a half-asleep user count from FLOW: sweep until the
  low gate (FLOW), then count majors (30/60/90…) by their heavier pitched click.
  Rung 5 adds the confirmation that closes the loop: on release, if the gesture
  travelled ≥ 6 detents, one seat feel at the landed pitch after the settle arrives
  (180/280ms) — the wheel *answers* with where it landed. No speech, no extra sound
  family; the existing ramp is the voice.
- **Locked, drawn honestly (if ever mounted locked):** disc overlaid with surface
  tone @ 45%; active arc redrawn as elapsed progress in `tokSecondaryContainer()`
  (day `#615B5A` / night `#4A423C`); thumb replaced by a 9dp seat dot in control fill
  at the set angle; hub label reads "LOCKED" (Atkinson Bold 8.5sp, numeral removed);
  numerals at 40% tone. Touch: no drag, no cycle; one dull attempt feel
  `[0,8]` @ `[0,110]`, **no sound**, throttled to one per 500ms. (The Deck's current
  behaviour — unmounting the wheel for the progress track while running — stays the
  primary locked presentation; this is the view keeping its own promise if reused.)
- *Curve + ms:* locked overlay is static (no transition — a lock that fades in is a
  lock performing). *Haptics/sound:* per above; nothing else new.
- *Depth/tokens:* no new colours; overlay uses existing surface token at alpha.

---

## ★ — single highest-value rung, priced

**★ Rung 1 — relative drag + honest feel.**

- **Build effort:** one touch-handling rewrite inside `RotaryWheelView` (~120 lines:
  grab offset, annulus gate, detent-angle drawing during drag, 35ms feel throttle,
  conditional settle feel) + deleting the 15-min latch branch and the stray 0.46.
  No new tokens, no new engine methods, no Deck changes. ≈ **half a day including
  on-device feel check**. Risk: lowest on the ladder — `onDraw` is untouched.
- **Felt gain:** every single interaction. Today the first touch of every session
  can teleport the value to wherever the finger happened to land — the user aims
  twice for a control he uses at 3:30 AM. After Rung 1 the wheel is grabbed, not
  poked; the latch stops meaning three things; a no-change release stops applauding
  itself. This is also the precondition for Rungs 3 and 5 — magnetism and blind
  setting are nonsense on top of an absolute drag.
- Why not Rung 2 as ★: the rim's readability matters, but a scale you read correctly
  and then set wrong because the thumb jumped is worse than a plain scale your hand
  owns. Hand before eye.

---

## PEAK (≤8 lines)

The peak wheel is a blind instrument: grabbed anywhere on its working ring it stays
under the finger (relative drag, magnetic lean), thrown it travels and stops dead
at its ends, and its whole range is speakable in pitch — FLOW's low gate, minors
climbing 340→880Hz, majors heavier on the same ramp, 180's thock below everything.
Its rim prints only what is true (36 detents, FLOW at the start stop, numerals on
round numbers), its knurl obeys one light, and at night nothing on it is cream
except type. It adds zero seconds and zero decisions to arming: set, slide, sealed.
Nothing on it is decoration that a scale, a light, or a detent didn't earn.

---

## STANDARDS CHECK

- **ARM ≤ 10s (cold open to sealed): PASS, improved.** The wheel is on the arm path
  exactly once. Rung 1 removes the re-aim a teleport costs (one gesture, first try);
  Rung 3's fling makes large changes (25 → 120) a single throw instead of a long
  drag; Rung 5 makes the whole setting eyes-free. No rung adds a step, a mode, a
  confirmation, or an animation the user must wait out — the longest motion on the
  ladder (fling, 420ms cap) runs *under* the finger's intent, not in front of it.
- **HOLD absolute (zero mid-session decisions): PASS, untouched.** The Deck still
  unmounts the wheel while a session runs; no rung introduces a mid-session edit
  path, a peek, or an adjust. Rung 5's locked rendering only makes an already-locked
  state honest if the view is ever reused mounted.
- **TELL THE TRUTH once, in human language: PASS, extended.** The scale's 36 ticks
  are already the 36 stops (Round 0). Rung 2 completes it: numerals only on values
  that exist, and FLOW printed at the start stop because that stop *is* flow, not
  5 minutes — the one place wave-1's own peak still printed a polite lie ("5").
  One fact, one rendering: the rim scale states block length; the readout above
  states the session total; the hub states block count. No number appears twice
  in two meanings.
- **His settled decisions:** Ledger tab dead — nothing here touches or revives it
  (the nav's LEDGER label is Band's element, not this plan's). Emergency hold —
  banked, and not this element; untouched. Routines fully his — **no routine arcs,
  NEXT markings or program information are added to the wheel**; the synthesis
  floated rim program arcs and I am deliberately not laddering them, because what
  fires and when is his call, not the dial's decoration. Guard — his open pick;
  this plan presumes nothing about it and depends on nothing in it.
- **His standing design picks:** fonts Sora / Atkinson Hyperlegible / Anonymous Pro
  at their real shipped weights — every type spec above names those files. Warm
  palette family throughout; the only new colour is the night active value
  `#D8C3B0`, a desaturated step of the existing cream. **Never green: no green
  anywhere in this plan.** Zero orange on the wheel in all rungs and states.

---

## CONFLICTS

- **Synthesis craft call — cheap knurl: BROKEN WITH, deliberately.** The synthesis
  kept parity teeth + a shared specular rig and sent the money to haptics. Rung 4
  re-proposes the light-modelled knurl — *priced honestly:* it is a 120-entry
  precomputed table and ~40 lines of `onDraw`, ≈ half a day including night tuning,
  **not** the per-frame specular opus wave-1 was accused of; the expensive version
  was a moving light rig, and this is not that. It sits at Rung 4, after physics —
  if the build stops at Rung 3, the synthesis's priority (haptics first) still won.
  Why break at all: the parity zebra is the last printed-not-machined surface on the
  element, and at table prices the "opus" objection no longer holds.
- **Synthesis accent budget (Deck = 1, the slide): KEPT, not broken.** Zero orange
  on the wheel in every rung. The budget was right; the knurl ruling was a pricing
  error, not a taste error.
- **Nabe (hub, Improvers) — SETTLED BY MAIL during this wave.** Nabe's letter
  (thread `hub-centre-geometry`) takes the cap the other way and further: visual
  0.30R in a 0.34R recess, touch 0.41R, with the annulus around it as a REST ZONE.
  My answer, sent to `mail/nabe/`: **accepted, in full.** It does not break the
  1:1 drag or the detent work — Rung 1 gates only `ACTION_DOWN` dispatch, and MOVE
  tracking is untouched once a drag has begun — and mis-tap immunity around the
  hub is the same principle as my dead inner field, extended by the seat who owns
  the centre. Consequences for this plan: (a) Rung 1's drag annulus inner edge is
  0.41R, aligned to his touch radius (spec updated above); (b) my Rung 2 hub
  shrink (0.52R → 0.46R) is the *floor* — if Nabe's peak ships, his 0.30R/0.34R
  geometry supersedes it, and my rim specs (numerals at R−42dp, arc at R−30dp)
  are unaffected because they live far outside his recess. Hub content
  (BLOCKS/FLOW + count, cycle-on-tap) remains entirely Nabe's.
- **Dario (wheel, MaxImpl):** my PEAK (Rung 5, blind wheel + Rung 3 physics beneath
  it) changes the wheel spec he implements from — **mail sent** to `mail/dario/`
  with the top step before finishing this plan (see mailroom).
- **TactileSoundEngine is shared** (Ines/slide and others call it): Rungs 3/5 only
  *add* named methods (`playWheelMajor`, `playWheelEndStop`, FLOW-entry variant);
  no existing waveform, gain or queue behaviour changes, so no other element's feel
  moves.
- **Deck readout (not an element seat; Ren's composition):** rim numerals do not
  duplicate the 84sp readout — scale vs value, per STANDARDS CHECK. If the
  one-fact-one-rendering law is read stricter, the numerals are the rendering that
  survives, because they sit on the control that sets them.

— Rad, Improvers, wheel
