# NACHHALL — INTERACTION DETAIL SPEC — Issue #13, Session Room
## Seat: NACHHALL, interaction-detail specialist (dedicated-100)
## Scope: veil, landing, the raise, stillness, SLEEP-as-night, the seal as felt from the room
## Status: DECLARED — spec only, uncompiled, no code written

Grounding (read before filing): Vela's `DESIGN-face-spec.md`
(alphas, seats, A-4 cause table — this spec does not re-open any
of it), Salz's `CHARGES.md` (C-15, C-16, C-22, C-26), Hauch's
FEEL-hauch (H-4/H-5/H-6), Daemmer's silence census, the shipped
engine (`TactileSoundEngine.kt`: SEAL_THROW timings
`[0,24,38,16]` / amps `[0,255,0,175]`; body 360 Hz, partials
360/1080/1080/1692, weights {0.6, 0.28, 0.37, 2.2}, comb tail
−46 dB at 900 ms, wet send −12 dB) and `MonolithCurves`
(A = ⎡0.2,0,0,1⎦, B = ⎡0.05,0.7,0.1,1⎦, C = ⎡0.3,0,0.8,0.15⎦,
Throw = ⎡0.5,0,1,0.4⎦, durations strictly from
{110,140,180,260,280,420,520,1050}).

Rule of this file: nothing on this face is a flat rectangle
with a label, and nothing moves, sounds, or buzzes without a
number below.

---

## ND-13.0 · THE PRESS PHYSICS (applies to every touch this face takes)

- **No ripple, anywhere, ever.** Every touchable on the room,
  the veil and the Landing takes `indication = null`. A ripple
  is a light event nobody specced, on the face whose whole law
  is light discipline (H-5, generalised from the veil to the
  room).
- **The only press the room itself takes is the dismissal tap.**
  Its press state, on pointer DOWN: the whole print column
  dips — print alpha × 0.92 and column translateY +2 dp, over
  60 ms Curve A. No scale (a clock that shrinks under a thumb
  looks frightened; a clock that settles 2 dp looks pressed).
  On UP inside: EXIT, DISMISSAL runs as Vela §5 (180 ms
  EasingThrow, ground + print together). On drag-off > 24 dp:
  dismissal still books — it is the face's only verb, a thumb
  that changed its mind has nowhere else to go — but the dip
  restores first over 110 ms Curve A, then the exit runs.
- **No tick on press-down.** Feedback reports bookings, never
  contact (the Tasks cell's Feel-1 law, adopted whole). The
  dismissal books no haptic and no sound at all: A-9's silence
  covers the exit as it covers the raise.

## ND-13.1 · THE RAISE — CHOREOGRAPHY IN THE HAND AND ON THE TABLE

Vela §5's cause table stands (ground/print splits, totals
660 / 600 / 440 / 0 ms). Three upgrades:

1. **Parallel, not series (C-15's cure, made a feel law).**
   Ground and print animations launch in child coroutines
   from the same t=0; the print's `delayMillis` is measured
   from t=0, never from the ground's completion. Acceptance
   is frame arithmetic, not vibes: WATCHED print must START
   at 180 ms ± 1 frame and complete at 600 ms ± 1 frame;
   HAND at 440 ms ± 1 frame; UNWATCHED at ≈ 660 ms.
2. **The raise is silent in the hand.** No haptic and no
   sound fires on ANY raise cause — ENTRY UNWATCHED,
   WATCHED, HAND, SEEDED, or the battery-yield wake (§ND-13.4
   is the single exception, and it is a wake, not a raise).
   A settle raise happens because the phone was put DOWN;
   a buzz through a nightstand at that second is the room
   announcing itself to a man already leaving it. If the
   phone is in the hand, V1 says HELD and there is no raise
   to feel — suppression stays silent (Samt: no print, no
   sound, no partial raise).
3. **Set-down micro-settle.** When a V1 REST verdict fires
   the raise from a set-down, the print's 520 Curve C entry
   carries a 1 dp downward settle in its last 110 ms (the
   column lands the way the phone landed). WATCHED/HAND
   entries: no settle — the eyes were already there.

## ND-13.2 · STILLNESS — THE ROOM'S IDLE LAW

- **Zero idle animation.** No breathing, no pulsing, no
  shimmer, no caret. Between events the face is a photograph.
  The complete event list that may move a pixel: the hero's
  count, the burn-in glide (1 step / minute, column offset
  only, as built), the descent (driven by f, §A-2), the dawn
  ramp, a cause-keyed arrival/exit, the surrender fade.
- **The count itself:** in `MM:SS`, the seconds change is a
  HARD CUT — mechanical clocks do not crossfade their
  seconds, and a fade at 1 Hz reads as a pulse, which is an
  idle animation wearing a digit's clothes. At the
  `H:MM` boundary and on minute rolls in `H:MM`, ONLY the
  changing glyph crossfades, 180 ms Curve A; unchanged
  glyphs never move or re-fade.
- **Descent is continuous, not stepped:** alphas are a pure
  function of f evaluated per frame, never quantised to
  steps the eye could catch at 03:00.

## ND-13.3 · SLEEP SPANS — NIGHT, NOT A DIMMER SWITCH

A dimmer switch lowers one number. Night is a different
physics. For a routine-born SLEEP span, in order:

1. **Light budget, stated as a ceiling:** after the descent
   (f → 1), no pixel on the face exceeds composite
   L = 0.110 (the exit line's descended value) except during
   the dawn ramp. Standing, no pixel ever exceeds L = 0.242.
   These are Salz's composites of the one ink `#FFE4CF` over
   `#000000`; the hue NEVER shifts — night here is less
   light, not bluer light.
2. **The tear rule goes soft, the gaps stay void:** rule
   alpha rides the hero (0.55 → 0.36, A-2); dashes are drawn
   on the one rigid grid at the column offset only (A-7), so
   at night the unspent span reads as faint perforation in
   black, never as a bar. Notches stay 3 dp of true black.
3. **Sound/haptic void, total:** inside a SLEEP span the
   room emits nothing — no minute tick, no descent cue, no
   break line exists to announce (Moench MA-1…5). The
   stand-down at 03:30 inherits C-26's surgery: the gate
   sits on the CALLS, so the haptic half dies with the
   audio; ceremony = silence, in both channels, by
   construction.
4. **The leaving:** stand-down print leaves on the
   surrender curve — 520 ms Curve C to α 0.00 over held
   black — never a cut, never over a lit Deck (A-4's light
   pin). The veil that follows is α 0.80 (E-1), and per
   C-22 it fires ONLY for the stand-down / unwitnessed
   night return, never for a witnessed noon finish.
5. **Battery yield is a going-to-sleep, not a failure:**
   520 Curve C fade to 0.00 ending 520 ms before the flag
   clears (Vela §9), and C-16's cure is a feel requirement,
   not just a stamp fix: the unplug-at-03:00 case must fade,
   at unplug + 10 min, in the room's own curve — a face
   that vanishes mid-print has failed the night even if
   every unit test passes.

## ND-13.4 · THE WAKE TAP (battery-yield dark face) — THE ONE EXCEPTION

- First tap on the dark face = WAKE ONLY. It is not a
  dismissal, never writes the dismissal bit (Vela §1.8).
  Print returns SEEDED: frame one at full lawful alpha,
  no fade, descent suspended 60 s (A-2(iii)).
- The wake carries the room's only permitted haptic:
  one-shot 12 ms at amplitude 90 / 255 — a knuckle tap
  felt through a fingertip in the dark, saying *still
  here*. No sound. A second tap ≥ 400 ms later is the
  dismissal and follows ND-13.0.

## ND-13.5 · THE VEIL AND THE LANDING — PRESS PHYSICS

- **Unveil on DOWN, not on click (H-4's cure, with its
  feel numbers):** the scrim takes a raw down-handler;
  the phase flips to LANDING at the first pointer DOWN —
  tap, drag-start, palm, all of them. Scrim α 0.80 → 0.45
  over 260 ms Curve A, starting under the thumb; the same
  press's release lands on the already-unveiling Deck.
  `indication = null` (H-5): the unveil draws no light.
- **Bloom on the first TRUE VERB only (H-6's cure):** the
  Landing (Deck at α 0.55 over true black) holds through
  brushes, palms, volume keys and the unveil tap's own
  gesture train. The bloom — 520 ms Curve C to full
  light — fires only when a verb books (a control's
  action commits), never on `onUserInteraction`.
- **Presses at Landing light are half-depth:** a verb
  pressed while the Deck is still at the Landing's α 0.55
  dips at half the normal throw — scale 0.985 (not 0.97),
  translateY +1 dp, edge-highlight collapse halved — so
  pressing in the dark never produces a full-light flash
  of press feedback brighter than the room it sits in.
  Dip 60 ms Curve A, return 160 ms
  cubic-bezier(0.22, 0.90, 0.28, 1) (HubSeatReturn), same
  as everywhere; only the depth changes with the light.

## ND-13.6 · THE SEAL MOMENT, AS FELT FROM THE ROOM SIDE

The seal voice is the engine's and it is already right;
this seat pins how the room receives it:

- **One voice, at t=0 proof only:** the Throw — haptic
  timings `[0, 24, 38, 16]` ms, amplitudes `[0, 255, 0,
  175]` (the zeroed middle segment is the breath: the
  shipped seal thuds through its middle, the Throw
  breathes through it — do not "fix" the gap), audio body
  360 Hz with the frozen partial set, comb tail reaching
  −46 dB at 900 ms. Hand seal and routine seal are the
  same voice; two seals cannot differ.
- **Haptic queue purge at latch:** `primeForSeal` already
  purges the AUDIO queue behind a barrier. Extend the
  same law to haptics: at latch, any pending waveform
  (a detent, a tear pulse, a completion pulse from the
  Deck) is cancelled, so nothing buzzes inside the seal
  shot's 1050 ms except the Throw itself, fired as the
  seal act's last statement.
- **The shot:** 1050 ms total (MonolithCurves
  DURATION_1050), transform on
  cubic-bezier(0.5, 0, 0.85, 0.35), blur re-easing on
  cubic-bezier(0.32, 0, 0.18, 1) — a blur tracking the
  transform reads as smear, not recession. Input is
  locked for the shot's full span; a tap inside it books
  nothing and ticks nothing.
- **After the shot:** if the settle has already fired,
  the room's first frame is SEEDED — full lawful alpha,
  no fade (Vela §5). The seal's vertigo and the room's
  fade never stack: two arrivals in a row read as one
  long accident.

## ACCEPTANCE (frames or it didn't happen)

- [ ] WATCHED raise: print starts 180 ± 16 ms, completes
      600 ± 16 ms, from process-truth timestamps (C-15).
- [ ] Set-down raise and HAND raise: zero haptic events,
      zero audio events, logged from the engine.
- [ ] SLEEP span at f = 1: brightest pixel on a captured
      frame composites to L ≤ 0.110; hue of every lit
      pixel is the one ink's.
- [ ] Unplug at T+5 h with face standing: no yield before
      unplug + 10 min; fade runs inside its 520 ms window
      (C-16's scenario, photographed at unplug + 9 min).
- [ ] Veil: a dragging first contact unveils on DOWN;
      a volume-key press on the Landing blooms nothing;
      first verb blooms 520 Curve C.
- [ ] Seal: engine log shows exactly one waveform inside
      the 1050 ms shot — the Throw's.
- [ ] Dark-face wake tap: face returns, dismissal bit
      unset, exactly one 12 ms / amp-90 haptic.

(a) WHAT I WANT NEXT: a haptic-capture pass on a real
device for the Throw vs the shipped seal — the 38 ms
zeroed middle is a paper claim until a fingertip confirms
the breath reads as a breath.
(b) WHAT I COULD DO UNASSIGNED: the same press-physics
audit (ND-13.0 / ND-13.5) run over the Shield and the
Drawer, whose verbs currently inherit default indication.

— Nachhall, interaction detail
