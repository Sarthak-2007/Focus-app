# FEDER — INTERACTION DETAIL, ISSUE #11 (THE HELD DOOR)

Seat: Feder, interaction detail (dedicated-100). Scope: every press,
hold, fill, drain and voice on this face. Upgrades wave-2's open
remainders (RETURN press craft, P-A haptic ramp) and rules the two
fragments filed unruled to the bench (Schwelle's knock at HOLD 00,
Schliff's per-second detent). DECLARED — nobody compiled.
Law held: fonts Sora / Atkinson Hyperlegible / Anonymous Pro ·
never green · RETURN stationary, centre 136dp above the card's
bottom edge, in every frame · nothing under a travelling thumb moves.

## 1. RETURN — the press (cures wave-2 remainder "bare .clickable")

RETURN is the face's one free act; its press must feel like a latch
opening, not a rectangle registering a tap.

- **Travel:** on press-down the slab's inner face translates 2dp
  toward the screen (scale 0.985 on Y only, anchored at the slab's
  bottom edge — it dips, it does not shrink sideways) over 60ms,
  EasingA (CubicBezier 0.2, 0, 0, 1). A 1dp contact shadow appears
  under the bottom edge at alpha 0.35 for exactly the pressed
  duration. Outer bounds NEVER change: the 56dp box, its border and
  its address are invariant — travel is drawn inside the slab
  (top inner padding absorbs the 2dp), so the stationarity tests
  and the travelling-thumb law are untouched.
- **Release:** return travel over 180ms, EasingA. If the finger
  slides off (press cancelled), travel returns over 120ms and
  NOTHING fires — no sound, no haptic, no booking.
- **Commit point:** the act fires on press-DOWN + 40ms dwell, not
  on release. A man slapping RETURN in panic must not wait for his
  finger to leave; 40ms rejects palm-brush accidents only.
- **Voice (unchanged recipe, now bound to the press):**
  playDoorReturn fires at the commit point — 300Hz, 28ms, gain
  0.48, chordRatio 0.9; waveform [0, 9, 22, 10]ms /
  [0, 165, 0, 195]. Press-down itself is silent and haptic-free:
  the voice belongs to the act, not the touch.
- **Pressed ink:** while pressed, the fill deepens one step
  (NightColorPrimaryContainer → its 0.88-alpha self over the night
  ground) and the label #471602 does not move, fade or re-weight.

## 2. PEEK CHIP — P-A adopted, specced (the latch lifts in your hand)

The 600ms hold-to-fill (LinearEasing, acquitted) gains the haptic
ramp P-A proposed. One continuous waveform, fired once at
press-down, cancelled on early release:

- `createWaveform(timings=[0, 200, 200, 200], amplitudes=[0, 60, 130, 210], repeat=-1)`
  — three 200ms segments tracking the fill's thirds: a hand
  tightening on a latch. Peak 210 < the night-law 255 ceiling.
- **Completion detent** at fill = 1.0, before onTake's re-derivation
  verdict: one 12ms segment at amplitude 190 appended by firing
  `createOneShot(12, 190)` at the fill's end — the latch seats.
  Then playDoorPeek (300Hz, 32ms, 0.50, ratio 1.5) fires ONLY past
  the grant's re-derivation, as today. A refused grant kills the
  ramp mid-segment (vibrator.cancel()) and stays silent — refusal
  is felt as the ramp dying, never as a buzz of protest.
- **Early release:** ramp cancelled within one segment boundary
  (≤200ms); the fill drains in 120ms as today. No detent, no voice.
- **Sound-off:** haptic ramp unchanged. **Haptics-off:** fill gains
  a 2dp leading-edge bar in full ink so progress is visible, not
  only felt. TalkBack "Take the peek" action unchanged.

## 3. THE GATE — drain feel (wall counting → offer)

- Redraw at 50ms stands (wave-2 cure). The 2dp rule drains with
  EasingA applied to the FRACTION (not linear): fast first breath,
  slow last centimetre — the wall visibly runs out.
- **Schliff's per-second detent: REJECTED on the wall.** Ton's score
  already rules silence on the wall except the arm; a ticking wall
  is a countdown nagging a craving man. Recorded as ruled, not lost.
- **The arm (playDoorArm, unchanged):** 300Hz, 44ms, gain 0.30,
  waveform [0, 7, 5] / [0, 150, 60], once at the state transition.
  Feder adds the visual twin: at the arm instant the gate line's
  text crossfades to the chip inside Z1's fixed 48dp (180ms,
  specA) — already the skeleton's law; the sound and the picture
  must land in the same frame (±16ms), or the arm feels dubbed.

## 4. THE VALVE — 60-second emergency hold (Disarm face, door-originated)

Existing physics acquitted and kept: 32ms tick loop; banked at
15/30/45 (bankInterval = total/4); drain after 800ms grace, 1× for
3,000ms then 2×, floor at bankedMs; banked drawn solid (alpha 255),
in-hand chamber translucent (alpha 180); seams at crossed quarters.

Feder's detail layer on top:

- **Per-second detent — RULED IN here (Schwelle's knock, placed):**
  the valve already fires fireFeel(4ms) per second, 8ms at 5s
  marks. Upgrade to waveforms so the hand reads time without eyes:
  per-second `createOneShot(6, 90)`; every 5th second
  `createOneShot(10, 140)`; at 10s remaining and 5s remaining, a
  double-knock [0, 8, 30, 8] / [0, 120, 0, 120] — HOLD 00 gets its
  knock BEFORE zero, at the 45s bank (below), so completion never
  surprises.
- **Bank moments (15/30/45):** keep pitches 560/470/390Hz
  (descending — each bank heavier, lower, more settled) and add
  the waveform [0, 14, 40, 20] / [0, 200, 0, 160]: a bolt sliding
  home. At the bank instant the chamber's solid edge must reach
  the seam in the same frame as the knock (±32ms, the tick's own
  period) — sound, haptic and the solid fill are one event.
- **Grip pressure tell:** from 45s→60s the in-hand chamber's alpha
  rises 180→220 linearly — the last quarter feels fuller in the
  hand and the eye. No colour change; density only.
- **Slip and drain:** on release, 800ms grace is SILENT and the
  fill holds frozen (a fumble is not yet a loss). Drain start
  fires one descending voice: 240Hz, 36ms, gain 0.36, ratio 0.9 —
  the door-exit pitch family, quieter. During drain the per-second
  detents stop (the hand is off; ticking at nobody is noise).
  Drain landing exactly on bankedMs fires the 8ms seat (existing)
  plus prints nothing new — the label already says BANKED Ns.
- **Completion (60.0s):** the seal's own grammar, inverted: the
  SEAL_THROW waveform [0, 24, 38, 16] / [0, 255, 0, 175] at half
  amplitude ([0, 128, 0, 88]) + the existing 10ms completion feel
  dies (replaced, not stacked). Sound: 240Hz, 64ms, gain 0.54,
  chordRatio 0.9 — the release interval at door-exit pitch, the
  longest voice on this face, because this is the costliest act
  in the app. Then the overlay exits at 260ms specA. No second
  voice anywhere on the handoff.
- **KEEP FOCUSING (the valve's RETURN):** same press craft as §1 —
  2dp dip, 60ms down / 180ms up, commit at press-down + 40ms,
  and SILENCE on commit (existing law: its peel-back stays silent).
  Its haptic is one 8ms one-shot at amplitude 120 — a hand taken
  off the valve, nothing more.

## 5. HONEST EXIT row ("END THE SESSION INSTEAD", 48dp)

- Press: no travel (it is a route, not a slab). Ink goes 0.92 →
  1.00 alpha at press-down, back on release; underline hairline
  1dp fades in under the text over 120ms while pressed.
- Commit on RELEASE (deliberate contrast with RETURN: the exit
  asks for a completed touch, RETURN asks for none). Voice at the
  confirmed handoff only: playDoorExit (240Hz, 40ms, 0.42, 0.9)
  as specced in code. Accidental brush = nothing.

## 6. ARMED vs BLOCKING — how the door differs in the hand

The door only exists while BLOCKING, but its two sub-states must
read differently blind:
- **Offer armed (peek available):** the face carries §2's ramp in
  reserve and §3's arm voice has sounded — the door feels LOADED:
  one object on it can be spent.
- **Withheld / never-owed:** no ramp exists anywhere on the face;
  the only haptics are RETURN's latch and the exit's step down.
  A man exploring the face with his thumb learns "nothing here
  spends" from the absence itself. Never simulate the chip's feel
  on a withheld door.

## Price
§1 ~35 LOC (pressed-state drawing inside RETURN's existing Box) ·
§2 ~25 LOC net (P-A's 50–70, minus the fill-bar fallback shared
with §1's drawing) · §3 0 LOC (rule only) · §4 ~45 LOC (waveform
swaps + chamber alpha ramp in the existing tick loop) · §5 ~15
LOC. 0 DB, 0 VM, 0 new prints. One on-device feel check per
section, batched into the batch-10 phone proof Tor already owes.

— Feder, interaction detail
