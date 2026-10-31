# GLANZ — INTERACTION DETAIL · Issue #16 · The Pill

**Seat:** Glanz, interaction detail (dedicated-100). **Scope:** the pill as a touched,
glanced-at object — its three verbs, its press/drag physics, its birth and death, its
light over foreign apps, its haptic score. **Posture:** DECLARED, spec only, nothing
compiled. This file consolidates the cell's feel seats (Weight / Light / Timing /
Touch & Sound / Instant Read), Persona Student 2's hour-nine charges, and the buildable
cores of Glut's and Zunder's crazy files into **one numbered spec a builder can ship
without choosing between us**. Conflicts are resolved here, on the record, §7.
The derivation stays pure and stays the only clock (house law): everything below lives
in `PillController` / presentation, keyed off the derivation's render boundaries —
flip-book stills, never a free-running animator (Glut G-0, adopted as law).

Fonts: figures **Anonymous Pro**, labels **Atkinson Hyperlegible**, state word **Sora**.
Palette: face `#34323B`, accent `#F47442` (the lane's own), figures cream.
**Never green. The pill never makes a sound — its voice is haptics only (§5).**

---

## 0. The charge this spec answers

Feel-5 graded the shipped pill **FAIL, 2 of 12** on instant read; Student 2 confirmed it
from hour nine: a 9 sp label is no print, a colon clock forces conversion a fried brain
can't do, and the three touches (body-tap / drag / ✕) are **unsignalled gambles** — an
irreversible verb disguised as a harmless one is the most expensive pixel in the app.
So: **§1 makes the verbs predictable, §2 makes them physical, §3 makes the face readable
in 0.5 s without digits.** Numbers first, in that order.

## 1. The three verbs — three physics, none disguised

| Verb | Act | Signal on the face | Physics |
|---|---|---|---|
| **Body tap** | READ — expand the full reading | none needed; it can do no harm | §1a |
| **Drag** | MOVE — re-perch the pill | pill lifts (§2) | §2 |
| **✕ squeeze** | END the peek early | ✕ in its own bezel cell (§1b) | §1c |

### 1a. Body tap = read, never a verb
Tap (lift within 250 ms, movement ≤ 6 dp Manhattan — the house slop) **expands** the
pill for **900 ms**: height **+6 dp** (180 ms decelerate out, 220 ms back), revealing the
second line — Session face: `BLOCK k OF n` + span remainder in the room's own grammar
(H:MM at ≥ 1 h — Student 2's A-11/A-12 clause: one number, one meaning, never the
per-block remainder in a second grammar); Peek face: the peeked app's name in Atkinson
11 sp. Then it contracts. A tap can never end, grant, or record anything. This kills the
gamble: the exploratory touch is *safe by construction*, so he will use it.

### 1b. The ✕ cell — the dangerous verb wears its own door
- The ✕ sits in a **trailing cell, 40 × 40 dp** (the pill's full height), separated from
  the figures by a **1 dp hairline** at cream 24%. Glyph at **60% ink** at rest —
  visibly a control, visibly asleep — rising to **100%** the frame a press lands in the
  cell. No other part of the pill highlights on an ✕ press.
- The cell is printed on **every face that can be ended** (Peek; Break's early-return if
  the lane rules one) and **absent, not dimmed**, on faces that cannot (Session) — an
  absent door is honest, a dimmed one is a tease.

### 1c. ✕ = squeeze-to-surrender (Zunder #5, adopted as spec)
A tap on ✕ does **nothing** but wake the cell (glyph 60% → 100% for 600 ms). Ending a
peek costs a held squeeze:
- Press-and-hold in the cell: the whole pill **compresses — width −18% over 600 ms**,
  ease-in, figures dimming to 70% across the same span.
- Progress ticks in the hand: at **200 ms** one-shot **6 ms / 90**; at **400 ms**
  one-shot **8 ms / 120** — two steps, felt, so the squeeze's length is never a surprise.
- At **600 ms**: `recordPeekEnded` fires; completion detent one-shot **12 ms / 190**;
  the pill performs the RETURN death (§4).
- Release before 600 ms, or slide-off: pill springs back over **180 ms** (spring §2),
  glyph relaxes, **nothing recorded, no haptic on the abort** — a released squeeze is a
  non-event, exactly like a cancelled press everywhere else in this app.
- Disambiguation: squeeze starts only on a **≥ 250 ms stationary** press in the cell;
  movement > 6 dp cancels into nothing (the cell never starts a drag — drags begin on
  the body only).

## 2. Press & drag physics (the pill has mass, and mass is time)

- **Pickup:** on drag start, squash toward the drag direction — scale **1.03 / 0.97**
  for **90 ms**; the pill's shadow grows 6 dp → **9 dp blur** and lifts 2 dp (it is
  *held* now). Haptic: one-shot **6 ms / 60** — pickup is acknowledged, not celebrated.
- **Drag:** 1:1 with the finger, except Peek at ≤ 10 s remaining: **8% lag** (offset
  smoothing, Zunder #1) — the deadline lands in the thumb before it lands in the eye.
  Session face lag 0%; Break face lag 0% (a break should feel light).
- **Release spring** (replaces the fixed 140 ms tween): `SpringAnimation`-class
  integrator in the controller, fling velocity inherited from a `VelocityTracker`
  (the data Feel-1 showed is currently thrown away — stop throwing it away).
  - Fresh Peek (> 30 s) / Break: **stiffness 380, damping 26** — settle ≈ 300 ms,
    overshoot ≤ 4 dp.
  - Peek ≤ 10 s: **stiffness 220, damping 32** — slow, **overshoot 0**. Heavy things
    don't bounce.
  - Session: stiffness 300, damping 28.
- **Landing:** squash scaleX **1.06** / scaleY **0.94** for **90 ms**, elastic return
  over **180 ms**. Landing tick one-shot **8 ms / 100**, **only** when the pill changes
  edge or perch band — a nudge that lands where it started is silent.
- **Perches:** three remembered Y-positions, one per face class (Session / Break / Peek),
  persisted as three ints in the existing settings store (Zunder #7). A face change
  glides the pill to that face's perch over **200 ms** — the change is *travel*, felt
  and seen, not a teleport re-read.

## 3. The face — read in 0.5 s, digits as confirmation

- **Geometry floors:** height **40 dp**; figures **Anonymous Pro 15 sp** minimum; label
  Atkinson **11 sp** caps; total width = figures + ✕ cell + 12 dp padding each end.
  Nothing on the face is smaller than these numbers, on any face, ever (the 9 sp label
  is the FAIL being cured).
- **The fuse-line (Glut G-8, adopted):** a **2 dp** hairline across the pill's belly,
  burning from the leading edge: lit length = `fractionRemaining × inner width`,
  recomputed only at render boundaries (G-0). Colour: cream 55% above 30 s remaining;
  **accent `#F47442`** at ≤ 30 s. The 0.5 s glance reads *how much line is left*;
  the digits confirm it. Route B (notification) prints the same fraction as **8 block
  glyphs** `▮▮▮▮▮▯▯▯` — same fact, two mouths, each native (Zunder #2).
- **The melt (Glut G-2, adopted, bounded):** Peek face only, as flip-book stills at
  render boundaries — corner radius **20 dp → 26 dp** and height **40 → 42 dp** as
  `fractionRemaining` falls 1 → 0. Shape is the peripheral urgency channel; it never
  moves the figures' baseline, never touches height on Session/Break faces.
- **Vocabulary (Glut G-10, adopted):** the label is **≤ 1 word** (PEEK / BREAK /
  SESSION's intention, §3 last bullet); at **≤ 30 s the label disappears entirely** —
  less to read as there's less time to read it. At ≤ 10 s the border swaps to accent
  `#F47442` at 60% (Zunder #2's perceptual acceleration is achieved by the melt +
  border together; the fuse-line does **not** ease — a lying fuse is worse than none).
- **Session face hierarchy:** intention in full cream figures (it is *why* the block
  exists), span remainder beneath in the muted tier — never truncated mid-word:
  shrink to the 11 sp floor, then wrap and grow height (the pill is an object; it may
  be taller when it has more to say). Zunder #10's locket flip is **not** adopted
  (§7) — a block change re-strikes the face in one frame, house law.

## 4. Birth and death are events

| Event | Choreography | Haptic |
|---|---|---|
| **Birth** (any face appears) | slide in from its docked edge over **220 ms** decelerate + landing squash (§2) | one-shot **10 ms / 120** at arrival |
| **Expiry** (peek fuse dies) | **THE RECALL** (Glut G-6): shrink to **62% width over 120 ms**, then fly to the grant edge over **180 ms** accelerating, fade only in the last **40 ms** — repossession, not disappearance | §5 final pulse, at shrink start |
| **Early hand-back** (squeeze completes) | **THE RETURN**: same flight path at **320 ms** decelerate — slower, because he chose it — then the **ghost**: an empty hairline capsule outline at the perch, figures gone, fading over **900 ms** | completion detent (§1c) |
| **Absent-gate death** (lockscreen, call, own package) | **instant cut. No choreography, ever.** A pill animating out over the lockscreen breaks the law that suppresses it | none |
| **Face change** (session ↔ peek ↔ break) | perch glide **200 ms** (§2) + face re-strike in the arrival frame | none |

## 5. The haptic score — the pill's only voice

**Sound: none. On any face, at any time, for any event.** The pill floats over other
people's apps and quiet rooms; it never chirps (Zunder #9's law, held).

| Moment | Pattern |
|---|---|
| Drag pickup | one-shot 6 ms / 60 |
| Landing on a new edge/perch | one-shot 8 ms / 100 |
| Birth | one-shot 10 ms / 120 |
| Peek ≤ 10 s, per second, **only while the pill is rendered and the phone is awake** | one-shot **8 ms / 70** |
| Peek expiry (at 0) | one-shot **25 ms / 180**, once, with the Recall's shrink |
| Squeeze progress | 6 ms / 90 @ 200 ms · 8 ms / 120 @ 400 ms |
| Squeeze completion | one-shot 12 ms / 190 |
| Session block boundary (blockIndex change) | one-shot 10 ms / 100 |
| Everything else — face changes, cooldown, absent gates, no-op taps | **silence** |

All haptics gate on the **same Absent conditions as rendering** — the pill can never
tick over a lockscreen or a call.

## 6. Light over foreign apps

The pill floats over apps it does not control, in any lighting. Its legibility is
guaranteed by construction, not by hoping the background is dark:
- Face `#34323B` at **92% alpha** (day) / **96%** (night).
- Light from above: **1 dp top bezel**, cream at **24%**; bottom edge ink deepened 18%.
- Shadow: **6 dp blur, black 35%**, offset +2 dp Y (9 dp blur while held, §2).
- Contrast floor: figures vs face ≥ **7 : 1**; the bezel + shadow pair — not the face
  colour alone — is what separates the pill from an arbitrary background. Both are
  mandatory render elements; a look that removes them fails this spec.
- Look worlds may re-body the pill (spring constants, squash cap, death choreography
  selector — Zunder #12's `PillMotion` data object, adopted): palette *and* motion are
  data in `PillTheme`'s holder. The floors in §3 and the score in §5 are **not**
  look-negotiable.

## 7. Resolutions & flags (on the record)

- **Zunder #4's DROP death vs Glut G-6's RECALL:** RECALL wins for expiry (a dropped
  pill reads as *discarded*; a recalled one reads as *the lease ended* — the app's
  grammar is repossession). The drop survives nowhere.
- **Zunder #10's locket flip:** not adopted. Motion at a block boundary would replay
  at the exact moment the room's own face changes; one re-strike, house law.
- **Glut G-16 (hollow cooldown pill):** not in this spec — it needs a derivation input
  that doesn't exist; stays priced and flagged for Ren on #16, not smuggled here.
- **C13 ghost ticker (cell's blocker):** every timing above keys off the derivation's
  render boundaries and stored ends. If the ViewModel's constant-seeded ghost survives
  the merge, the pill's *feel* will lie at exactly the moment its figures start to.
  This spec assumes the storage-derived face; the ruling is the cell's/Ren's, the
  dependency is named here so nobody ships the choreography on top of the ghost.

## Acceptance (flash + verb tests, Student 2's terms)

- **Flash test:** 0.5 s exposure, bright and dark foreign backgrounds — remaining time
  read to **± 10 s** from the fuse-line alone, digits covered; urgency band
  (comfortable / ≤ 30 s / ≤ 10 s) identified from shape + border with digits covered.
- **Verb-prediction test:** first-touch users, face only — predict each touch's outcome
  before touching; **≥ 90%** correct on all three verbs; the ✕ must be predicted as
  "ends it, but only if I hold" — squeeze, not tap.
- **Timing:** birth 220 ms, Recall 120 + 180 ms, Return 320 ms, ghost 900 ms —
  each ± 1 frame @ 60 Hz; spring settle within §2's bands with 0 overshoot at ≤ 10 s.
- **Silence audit:** zero sound events from the pill across a full peek lifecycle;
  zero haptics while any Absent gate holds.

— Glanz, interaction detail
