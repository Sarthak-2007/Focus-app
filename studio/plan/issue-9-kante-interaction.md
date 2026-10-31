# ISSUE #9 — KANTE: INTERACTION DETAIL FOR PARK / THE CHECK KEY

*Kante, interaction-detail specialist, dedicated-100 programme.
Ground: this cell's built `ParkDrawer.kt`, Feel 1 (touch), Feel 2
(sound), Feel 3 (motion), Finisher 2 (keys), the wave-2 report.
Paper only — nobody compiles. Fonts/palette law untouched.*

*The cell has specced CHECK's dip (1dp, 90ms SEAT in / 160ms
THROW out), its voices (seat knock on DOWN, firm click on a
counted-whole landing, silence otherwise), and its fences
(no per-row sound, no timers) to a standard I am not here to
redo. What is missing is the key as a PHYSICAL object: as built
it is a flat 48dp ink rectangle whose only depth is a 1dp
translation. A machine key has a face, a foot, and a travel that
the foot visibly pays for. That, plus three timing joints nobody
owned (haptic onset vs dip onset, click vs release start, the
grab pill's growth clock), is this file.*

---

## K9-1 · THE KEY HAS A FOOT — 3D TREATMENT (PATCH-READY)

Both keys (FIX and CHECK — one family, Finisher 2's law, so
every number below applies to both, identically):

- **Face:** the existing ink fill, radius 8dp, 48dp total height
  — unchanged.
- **Foot:** a **2dp band** along the key's bottom edge, drawn in
  the same ink at the scheme's next density step down (the ink
  mixed toward the ground: implement as the fill colour darkened
  by compositing `Color.Black @ 0.18` over it — no new token, no
  new hue). The foot is the part of the key below the face's
  travel line: it reads as the key's thickness, the reason a
  1dp dip can be believed.
- **The dip pays the foot, not the layout.** Drive the foot's
  height from the SAME animator value as the dip (Finisher 2's
  `animateDpAsState`, 90 SEAT / 160 THROW): `foot = 2.dp - dip`.
  At rest: face + 2dp foot = 48dp. Fully dipped: face translated
  1dp down, foot compressed to 1dp, total footprint still 48dp —
  the key sinks INTO its thickness instead of sliding over the
  paper, and nothing below it in the column moves by a pixel.
  This replaces the bare `.offset(y = dip)`: the offset applies
  to the face only; the foot is a height, not a translation.
- **No shadow, no elevation, no highlight line.** PARK is
  one-tone (§8.10); the foot is a density step, not a lighting
  effect. A drop shadow under the key would be the drawer's only
  cast shadow and would read as a card, not a key. The top edge
  gets NO 1dp light line — machined keys are not bevelled
  buttons; the foot alone carries the depth.
- **Radius discipline:** the foot's bottom corners carry the
  face's 8dp radius; its top corners are square (it is behind
  the face). When dipped, no corner of the foot may protrude
  past the face's silhouette.

## K9-2 · PRESS PHYSICS — THE THREE CLOCKS AGREE (SPEC + ONE PATCH)

A press is three events that must read as one: sound/haptic,
travel, commit. Exact budget, per press of either key:

| Event | Deadline |
|---|---|
| DOWN dispatched | t = 0 |
| Seat knock call (Feel 2's frozen call) | same frame as DOWN, ≤ 1 frame (≤16ms @60Hz) |
| Dip animator starts (face begins 1dp travel) | same recomposition as the knock |
| Dip seats (1dp reached) | t + 90ms (PRESS_MS / EasingSeat) |
| Commit | CHECK: on DOWN (as built). FIX: on confirmed release (Feel 1 T-1) |

- **Haptic waveform, restated as the felt spec** (engine values,
  frozen by Feel 2 §6 — repeated here because this is the
  waveform the finger judges): knock = `[0, 10, 5]ms @
  [0, 25, 9]` day / `[0, 15, 5]` night amplitudes (of 255);
  verdict click = `[0, 13, 20, 8]ms @ [0, 225, 0, 135]`. The
  knock's strike (10ms) completes before the dip seats (90ms):
  the hand hears/feels contact first, then watches the key
  finish travelling — the order a real key makes (contact
  noise precedes full travel). Do not delay the knock to meet
  the dip; do not shorten the dip to meet the knock.
- **PATCH-READY — the verdict click fires at release START.**
  Where the landing effect plays `playFirmClick()`, it must
  fire in the completion frame — the same frame `checking`
  flips false and the dip animator's target returns to 0dp —
  NOT at the end of the 160ms THROW return. The click is the
  sound of the key *beginning* to come back because it is
  done, the way a latch clicks as it releases. A click landing
  160ms after the last row swapped would trail its own fact.
  (Feel 3 §3.3 already puts the word re-print in the completion
  frame; this puts the voice in the same frame. One frame,
  three witnesses: word, voice, travel-start.)
- **Hold behaviour:** a finger still down at completion changes
  nothing — the dip releases under the resting finger (Feel 1
  §3). Spec addition: the foot decompresses on the same 160ms
  THROW under that finger; there is no second knock on the
  man's own later UP. UP is not an event on these keys.

## K9-3 · THE GRAB PILL'S GROWTH GETS A CLOCK (PATCH-READY)

As built, the grip grows the pill 24 → 28dp wide while gripped
(Feel 1 §5) with no duration anywhere in the papers — an
unanimated size step on the one object whose whole job is to be
held. Spec:

- Growth: **120ms, EasingSeat** (`animateDpAsState`, label
  `park_grip_width`), starting the frame the drag detector
  reports first movement ≥ 2dp — NOT on DOWN alone (a tap on
  the strip grows nothing; Feel 1 noted the tap-grows-pill
  false affordance; growth on movement makes the pill answer
  the grip, not the touch).
- Release: **160ms, EasingThrow** back to 24dp, on drag end —
  the sheet's own departure clock, so pill and sheet settle in
  one tempo family.
- Height stays 4dp throughout; the growth is width only, and
  the strip's 48dp touch height never changes — the hand's
  target does not breathe, only the visible pill does.
- No haptic on grip, growth, seat, or dismiss — Feel 2 fence 2
  stands; this is a visual clock only.

## K9-4 · WHAT THE CHECK KEY NEVER GETS (FENCES, KANTE'S SEAT)

1. No press-and-hold escalation, no repeat, no long-press —
   Feel 1 R-7 stands; I add: no *visual* hold state either
   (no deepening dip at 500ms/1000ms thresholds). A held CHECK
   at 2s is pixel-identical to CHECK at 100ms plus the board's
   own landings.
2. No success dress change on the key itself: after a whole
   landing the key returns to the identical face it had before
   the press. The verdict lives in the click + board + LAST
   PROVED, never in the key glowing, checking itself, or
   keeping a "proved" tint. A key that remembers being right
   becomes a badge; badges are dashboard furniture.
3. No ripple, no indication: both keys keep `indication = null`
   discipline — the dip + foot IS the indication, and it is
   mechanical, not optical.
4. Dress-ladder swaps (print → ink, Finisher 2 §4) stay 0ms
   steps (Feel 3 §3.4) — the foot does not crossfade either;
   its colour derives from the fill each frame, so a dress
   swap steps the foot in the same frame for free. A builder
   who caches the foot colour breaks this — derive, don't cache.

## ACCEPTANCE, BY FINGER AND EYE

1. Press CHECK slowly, watching the key's bottom edge: the
   face sinks 1dp over 90ms while the dark foot thins 2 → 1dp;
   the NOT PROVED joint / sleds below do not move at all.
2. Whole landing: click, word re-print, and the first frame of
   the dip's return share one frame; the foot re-thickens over
   the same 160ms; at rest the key is indistinguishable from
   before the press.
3. FIX round trip: the returned-to key is still dipped with a
   1dp foot for the 800ms verdict hold, then releases whole —
   foot, face, and (if repaired) click together.
4. Grip the strip and drag 3dp: the pill widens 24 → 28dp over
   ~120ms; tap the strip without dragging: the pill does not
   grow. Release a parried drag: pill and sheet settle in the
   same THROW tempo.
5. Hold CHECK for 3s on a hung sweep: nothing changes after
   the dip seats — no growth, no pulse, no second voice.

— Kante, interaction detail
