# VAU — Designers, Wow seat · PLAN WAVE · The Deck composition spec

Coordinator: Sana. Scope: the Deck only, composition only — no code, no new
features, no concept. Concept is NONE and this spec does not smuggle one back:
nothing on this screen narrates, celebrates, or fires itself. It states facts
once, in type and position, and it waits to be slid.

Binding law applied throughout (not re-argued): margin 20dp · spacing tokens
4/8/12/20/32/48 only · radius 8dp keys / 16dp panels · accent = the slide cap's
orange fill, the only orange on the Deck, zero orange on the wheel · state
grammar (position = power, ink = selection, margin words = readouts, answers
on DOWN, no ripple, depth = one tonal step + 1dp hairline) · ARM ≤10s ·
HOLD absolute · TRUTH once. The Deck idles at the last session, armed-ready:
on a normal day the wheel is touched zero times and the slide is the one act.

---

## 1. The ONE hero — and its defence

**The hero is the TOTAL readout. Not the wheel.**

The wheel is the largest object on the screen and it is deliberately *not*
the hero. Three reasons, in order of weight:

1. **The hero must be the thing that is true every day.** On a normal day he
   never touches the wheel — the Deck idles at the last session. An untouched
   control cannot be the hero; at rest it is furniture. The total is the one
   fact he checks at 03:30 with half-open eyes: *how long am I about to seal
   myself in for?* That number is the promise the slide then keeps.
2. **The hero must survive a photograph.** In a screenshot the wheel reads as
   a dial — beautiful, generic. The total at 104sp reads as a *decision* —
   his number, his morning. People are shown numbers they chose, not controls
   they didn't touch.
3. **Hierarchy is honesty.** If the wheel out-shouted the readout, the screen
   would be advertising the rare act (changing the block) over the daily act
   (arming the total). Beauty here is the ranking told truthfully: fact first,
   instrument second, verb last.

The wheel is the screen's *instrument* — machined, real, and quiet. The slide
is its *verb* — the only orange, the only motion that matters. Neither
competes with the number. One hero, one instrument, one verb: that is the
whole composition.

---

## 2. Canvas and anchoring

- Reference canvas: **390 × 844dp**. All widths below are formulas first,
  reference values second, so the spec survives other widths.
- Content column: screen width − 40dp (20dp margin each side) = **350dp** at
  reference. Every element below lives inside this column unless stated.
- The Deck does **not scroll**. It is two anchored stacks and one reserve:
  - **Top stack** (nameplate → readout → NEXT → wheel) is top-anchored.
  - **Bottom stack** (task line → slide → band nav) is bottom-anchored, so
    the slide's thumb zone never moves between devices or font scales.
  - **The reserve** (§5) sits between the wheel and the task line and absorbs
    all height variance. Extra height goes to the reserve and *above* the
    wheel (between NEXT and wheel), never below the slide.
- Vertical rhythm uses spacing tokens only: 4 / 8 / 12 / 20 / 32 / 48.

---

## 3. The stack — every element, top to bottom

Type proposals are flagged **[→ Set]** throughout: Set owns the final type
table; sizes here are proposals that obey the settled ramp (heroes 44–112 /
titles 18–26 / body 13–15 / labels 9–11, no two tiers within 1.4×). The Deck
deliberately uses only **three sizes — 10, 15, 104** — ratios 1.5× and 6.9×.
Differentiation inside a tier is by family, weight and tracking, never by a
fourth size.

### 3.1 Nameplate line — the chrome, reduced to a printed line
- **Position:** first element, top of content column. Height **32dp**.
- **Contents:** left — ink dot Ø8dp + wordmark `MONOLITH`, Sora Bold 15sp,
  tracking +1.0 **[→ Set]**. Right — *nothing*.
- **Killed here:** the `HELD 02` and `GUARD 1/4` pills and the DAY/NIGHT pill.
  Their facts have homes elsewhere; chrome states nothing twice.
- **Rule:** a 1dp hairline under the line, full content width — ink at 12%
  day / text at 12% night. This hairline is the screen's print register;
  everything below it is the instrument.
- **Tokens:** dot + wordmark ink `#252323` day / text `#D2C6BB` night. No fill,
  no panel, no radius.

### 3.2 Gap — 32dp (section break token)

### 3.3 The readout block — THE HERO
- **Label:** `TOTAL`, Sora SemiBold 10sp, tracking +2.0, left-aligned on the
  margin, line height 14dp. Ink at 55% day / text at 55% night. The word
  TOTAL is load-bearing: it is what makes the number below unambiguous
  against the wheel's block scale.
- **Gap:** 4dp.
- **The numeral:** total session minutes as a plain integer — `100`, `240`,
  never `1:40`, never `1H 40M`. One spelling of duration on this screen, and
  the colon form is banned because a colon promises a clock time, which is a
  different fact (and the FINISHES fact is killed outright, §7).
  Anonymous Pro Bold **104sp**, tracking −2, tabular figures, line height
  112dp, left-aligned on the margin **[→ Set]**. Ink `#252323` day /
  text `#D2C6BB` night. Never orange, never cream-filled, never on a panel —
  the numeral sits directly on the ground. The ground is its panel.
- **Unit:** the word `MIN` follows the numeral on its baseline, Anonymous Pro
  Bold 10sp, tracking +2.0, ink at 55%. A unit at label size, not a second
  number at any size.
- **Behaviour:** the numeral is **live while dragging** — it re-renders the
  instant a detent lands (block × pips) and the instant the hub releases a
  new count. It never animates: no count-up, no scale pulse, no crossfade.
  It is set, instantly, like a counter wheel clicking over. Motion lives in
  the hand, not in the number.

### 3.4 Gap — 8dp

### 3.5 NEXT line — see §6 (one margin-word line, 14dp high)

### 3.6 Gap — 32dp (grows first when the reserve has surplus, §2)

### 3.7 The wheel — the instrument
- **Diameter: 312dp** (the void verdict, §5). Centred in the content column.
  On viewports narrower than 392dp the diameter is `content width` and the
  margin yields to the wheel, never the reverse.
- **Body:** a flat disc at the **recess** tone — day recess derived one step
  below ground `#E4DFDA` → proposed `#DAD3CC` **[flag: day recess hex is not
  in the settled law; this is a one-step derivation for Rat to ratify, not a
  new colour]** / night recess `#100E0C`. Edge: 1dp hairline, ink 18% day /
  text 16% night. No shadow, no bevel drawn in type, no knurl texture — the
  teeth are the tick ring and the light rig, nothing else.
- **Pointer:** the wheel turns under a **fixed pointer** at 12 o'clock: a
  2 × 16dp ink line printed on the ground *outside* the disc, its tip 4dp
  from the disc edge, centred on the wheel's vertical axis. Full ink, both
  modes (night: text `#D2C6BB` at 100%). Position is the readout of block
  length; the pointer never moves.
- **Tick ring (the block scale):** printed on the disc, outer radius 148dp.
  Range 5–120 minutes, **36 true detents at 5-minute steps** around the
  working arc.
  - 5-min tick: 8dp long, 1dp wide.
  - 15-min major: 14dp long, 2dp wide.
  - 30-min major: 18dp long, 2dp wide.
  - Colour: ink at 35% (minor) / 85% (major) day; text at 35% / 85% night.
    The tick currently under the pointer renders at 100% and one weight
    heavier — selection by ink density only. **Zero orange on the wheel,
    in every state, including drag.**
- **Rim numerals:** at the 15-min majors only — `15 30 45 60 75 90 105 120` —
  radius 118dp, Anonymous Pro Regular 10sp, ink at 55% day / text at 55%
  night **[→ Set]**. These are scale print, not a readout: they say what the
  wheel *can* say, the hero says what it *does* say. (Flagged for Floor:
  scale print at 10sp is non-functional labelling on a control whose value
  is simultaneously rendered at 104sp — the floor is met by the hero, not
  by enlarging the scale.)
- **Hub:** Ø120dp, centred, **raised** tone — day `#FAF6F0` / night
  `#362C27` — with a 1dp hairline (ink 22% / text 18%). The hub states the
  block **count as pips only, no numerals, ever**:
  - 8 pip positions on a ring at radius 36dp from hub centre, pip Ø10dp.
  - Counted pips: filled ink day / filled text night. Uncounted: 1dp
    hairline ring, empty inside.
  - Count range 1–8. The hub is a key: it answers on **DOWN** (depresses
    one tonal step to recess + translates 1dp), and the count increments
    on **release** — tap = +1 block, wrapping 8 → 1. No long-press
    language, no second gesture to learn at 03:30.
- **Physics (settled, restated as composition requirements):** 1:1 drag on
  the disc · velocity fling · spring snap into the true 5-min detents ·
  detent feel in the motion (5-min tick, 15-min major, knock at both
  end-stops) · rubber-banded resistance past 5 and past 120 · hub, pointer
  and readout never move with the disc. Light is fixed in space; the teeth
  pass through it. Light carries no data.

### 3.8 The reserve — see §5. Minimum 48dp, pure ground, contains nothing.

### 3.9 Task line — the lower field's only tenant
- **Position:** first element of the bottom stack, sitting directly on the
  reserve's lower edge. Height **20dp**, single line, no panel, no fill.
- **Contents:** left — label `TASK`, Sora SemiBold 10sp, tracking +2.0, ink
  at 55%; 12dp gap; the cued task's name, Atkinson Hyperlegible Regular
  15sp, one line, ellipsised at the margin. No estimate, no duration, no
  step chips, no DONE/QUEUE furniture — that card is dead; the task's
  duration is Tasks' fact, rendered there.
- **Absent state:** if no task is cued, the line is **absent** — not
  replaced by placeholder copy. The reserve simply grows by 40dp. Absence
  renders as absence.
- **Tokens:** name in ink / text at 100%; label as above. No orange.

### 3.10 Gap — 20dp

### 3.11 The slide — see §4. Height 72dp, full content width.

### 3.12 Gap — 20dp

### 3.13 Band nav — chrome, noted for anchoring only
Height 56dp, flush recessed rail (its own spec owns it). The Deck's only
obligation: the slide sits exactly 20dp above it on every device. No orange
passes between the slide and the rail except the rail's own armed lamp,
which is the rail's fact, not the Deck's.

---

## 4. The slide — full anatomy

The slide is a drawn bolt and the screen's only verb. It is the **only
orange object on the Deck** and the only element allowed to be a panel.

- **Track:** full content width (350dp at reference) × **72dp**, radius
  **16dp** (panel radius — this is the Deck's one panel-scale object).
  Fill: **recess** tone (day `#DAD3CC` proposed as §3.7 / night `#100E0C`).
  Edge: 1dp hairline, ink 18% day / text 16% night. The track is a slot cut
  into the ground, not a card laid on it.
- **Track word:** `SLIDE TO ARM`, Sora SemiBold 15sp, tracking +1.5,
  centred on the full track, ink at 55% day / text at 55% night **[→ Set]**.
  It is printed *on the track*: the cap occludes it as it passes, letter by
  letter. When the cap is home (seated right), the word is fully covered —
  a committed slide has nothing left to say.
- **Cap (the sled):** **84 × 56dp**, radius **8dp** (key radius — the cap is
  the screen's one key), inset **8dp** from the track's edges on all sides
  at rest. Fill: **orange — day `#F55314` / night `#F47442`** — the accent,
  whole and unshared. No text on the cap, ever.
- **Marker line:** on the cap, a vertical line **3 × 28dp**, centred
  vertically, 12dp from the cap's leading (right) edge. Colour: day
  `#FAF6F0` at 90% / night ink `#252323` at 90% — whichever sits on that
  mode's orange. It is the grip print: the eye and the thumb agree on where
  the cap is held. At the gate it thickens to **5dp** (§below) and stays
  5dp until the cap seats.
- **Seat print:** at the track's right inset, the cap's destination drawn
  as a 84 × 56dp hairline outline (ink 25% / text 25%, radius 8dp) — the
  empty seat the cap is travelling to. Position states the goal before the
  hand moves; when the cap arrives, cap and outline coincide and the
  outline disappears under it.
- **Travel:** travel distance = track width − 84 − 16 (both insets) =
  **250dp** at reference. Thumb-only: the cap moves only under the thumb —
  tapping the track teleports nothing.
- **The gate:** commit threshold at **0.65 of travel** — gate x = 8 +
  0.65 × 250 = **170.5dp from track left** at reference (recompute from the
  formula, never hard-code the reference number). The gate is printed as
  two 6dp notches on the track's top and bottom hairline edges at gate x,
  ink at 25%. Crossing it: **one gate tick** (haptic + the marker line
  thickening, above — waveform is Ton's to name) and the drag's resistance
  character changes: before the gate, gentle progressive resistance;
  after it, the cap is *taken* — release anywhere past the gate and the cap
  completes under power.
- **Commit:** on release past the gate (or on crossing 0.85 of travel while
  still dragging — a full throw commits without waiting for release), the
  cap animates the remaining travel in **≤120ms**, seats into its outline,
  and the **seal** fires (the one sound + thock this screen owns). Then a
  **dwell of ≤120ms** in the fully seated state — the bolt visibly *home*
  — before the session state takes the screen. Total gate-to-session:
  ≤240ms. There is **no strain wall**: resistance never exceeds "gentle";
  a tired thumb at 03:30 crosses 0.65 as easily as a fresh one. Friction
  theatre on the daily path is banned by the spectacle law and by ARM ≤10s.
- **Refusal:** release before the gate → the cap returns critically damped,
  silent, zero overshoot, marker back to 3dp. Stillness and silence are the
  refusal. No buzz, no shake, no copy.
- **Badge policy — DECIDED: the badge dies.** There is no `25M`, no total,
  no count, nothing printed on or beside the slide except its verb. The
  total already has a home at 104sp twenty centimetres of eye-travel above;
  repeating it at label size on the cap's track would be a second rendering
  of the screen's most important fact, at 1/10th the authority, in the one
  place the eye should be watching the hand. The slide is a verb. Verbs
  don't carry readouts.

---

## 5. The void fight — SETTLED

The wave-1 fight, named plainly: TE called the Deck's empty lower field a
*designed reserve* (bound it, mark it, defend it); IA called it *dead
space* (kill it, give the room to the dial); my own wave-1 seat split it —
a 288dp dial, and ceremony to fill the rest.

**The decision: IA wins the diagnosis, TE loses the remedy, and my wave-1
split loses its second half.**

1. **The dial grows: 288 → 312dp.** IA was right that the old void was
   theft — roughly a third of the screen held nothing while the instrument
   sat undersized. The dial takes back 24dp of it. 312dp is the largest
   diameter the 20dp margin grid honestly holds at reference width, and a
   larger wheel is not vanity: bigger radius = finer 1:1 drag = the detents
   land where the thumb meant them.
2. **Ceremony does not fill anything.** My wave-1 answer is dead and I am
   the one killing it: under the spectacle law, ceremony belongs to state
   changes, never to the daily arm path — and a fill that only exists
   during a ceremony is a void 23 hours a day with a party trick. The
   slide's own commit (§4) is all the event this screen gets, and it lasts
   240ms.
3. **What remains is a gap, not a field — and it is fenced by tokens, not
   by marks.** TE's registration crosses die: printing marks on emptiness
   to prove it was intended is an apology in ink, and an apologised-for
   void reads worse than an honest one. Instead the lower field is
   dismantled as a *field*: the preset key row (FLOW / 15m / 25m / 45m /
   90m) is deleted — the presets were a second control restating the
   wheel's scale, one fact with two renderings — and the cued-task card
   collapses to the single task line (§3.9). What is left between wheel
   and slide is exactly: **reserve (min 48dp, pure ground, no element may
   enter it) + task line (20dp) + 20dp gap.** The reserve's job is stated
   in one sentence: *it is the thumb's runway* — the slide is reached
   without crossing a single tappable thing, and the eye, having read the
   total and the wheel, finds nothing competing with the one orange verb.
   Emptiness with a fence and a job is a reserve. Emptiness between two
   cards was dead space. Both seats were describing the same hole; the
   fix was to remove the cards, not to decorate or deny the hole.

---

## 6. The NEXT fact — where it lives, in what form, in how many words

- **Where:** §3.5 — one line directly under the hero readout, left-aligned
  on the 20dp margin, 8dp below the numeral's line box. It lives with the
  readout because it is a readout's fact (when the *next* session is), and
  it lives nowhere else on the Deck.
- **Form:** a margin word, never a ribbon, never a panel, never filled.
  Anonymous Pro Bold **10sp**, tracking +1.5, ink at 70% day / text at 70%
  night **[→ Set]**. No icon, no dot, no orange.
- **Words: exactly three tokens.** Format: `NEXT 05:00 STUDY` — the word
  NEXT, the start time, the loadout's name. No verb. No `AT`, no `FIRES`,
  no `ITSELF`, no `·`-separated metadata tail. Three tokens is the whole
  fact; a fourth word would be narration, and narration is what died with
  the concept.
- **Absent state:** no routine ahead → the line is absent and the readout
  block closes up by 22dp (8 gap + 14 line). The Deck never prints
  `NEXT — NONE`; a missing fact renders as missing.
- **Truth rule:** the line renders the routine engine's actual next start
  or it renders nothing. It is recomputed when routines change, not
  narrated from a cached string.

---

## 7. Kill list (this screen, applied by this spec)

- `25:00` colon-format duration and the `FINISHES 5:32 PM` line — derived
  clock facts; the total in plain minutes is the only duration rendering.
- Hub `50 MIN TOTAL`, hub `2× BLOCKS`, dial-centre `25 min` — all numerals
  in or on the wheel. The hub keeps pips; the total keeps the hero.
- The slide's duration badge (§4) and every orange numeral anywhere.
- The preset key row (FLOW / 15m / 25m / 45m / 90m) — second control, second
  rendering of the wheel's scale. (The FLOW *concept* as a key elsewhere is
  other seats' business; on the Deck it was a preset among presets and it
  dies with them.)
- The cued-task card with its chips, DONE and QUEUE buttons → one task
  line (§3.9).
- `SHUT 07 APPS` and all loadout counts on the Deck — Shield's fact,
  rendered on Shield.
- Nameplate pills (`HELD 02`, `GUARD 1/4`, DAY/NIGHT) → one printed line.
- The ignition sweep and every other wave-1 Deck ceremony on the arm path,
  including my own. The commit's 240ms is the whole show (§4, §5.2).

---

## 8. Day / night tokens — per element

| Element | Day | Night |
|---|---|---|
| Ground | `#E4DFDA` | `#171412` |
| Nameplate wordmark + dot | ink `#252323` | text `#D2C6BB` |
| Hairlines | ink at 12–22% (per §) | text at 12–18% (per §) |
| Readout label / unit | ink at 55% | text at 55% |
| Hero numeral | ink `#252323` | text `#D2C6BB` |
| NEXT line | ink at 70% | text at 70% |
| Wheel disc | recess `#DAD3CC` *(proposed, §3.7)* | recess `#100E0C` |
| Ticks / rim numerals | ink at 35 / 85 / 55% | text at 35 / 85 / 55% |
| Pointer | ink `#252323` | text `#D2C6BB` |
| Hub | raised `#FAF6F0` | raised `#362C27` |
| Pips (counted / empty) | ink fill / ink hairline | text fill / text hairline |
| Task line name / label | ink 100% / 55% | text 100% / 55% |
| Slide track | recess `#DAD3CC` *(proposed)* | recess `#100E0C` |
| Slide track word | ink at 55% | text at 55% |
| Slide seat print | ink at 25% | text at 25% |
| **Slide cap (the only orange)** | **`#F55314`** | **`#F47442`** |
| Cap marker line | `#FAF6F0` at 90% | ink `#252323` at 90% |

Cream `#FFE4CF` appears on the Deck **nowhere as a fill** — at night it is a
text tone in the staircase and this screen's text already runs on `#D2C6BB`;
cream is not spent here. One screen, one text voice.

---

## 9. The beauty paragraph

This is the screen he shows people because it looks like a decision already
made by a person with taste: a warm grey field with almost nothing on it —
one enormous number in honest mono, stating his morning in a single glance;
below it a machined disc that turns under a fixed pointer like the gauge of
something expensive, its centre saying nothing but eight quiet pips; and at
the thumb, one slot of saturated orange waiting in a recessed track, the
only colour for sale on the whole screen. Nothing glows, nothing pulses,
nothing explains itself. It photographs like a Braun instrument and behaves
like a bolt, and the beauty is precisely the restraint — every other focus
app begs for attention with rings and streaks and confetti, while Monolith's
Deck just sits there, armed, certain, and silent, the way a well-made tool
sits on a bench: you can tell it will work before you touch it, and the one
touch it asks for is the satisfying one.

---

## 10. Flags for other seats (proposals, not rulings)

- **[→ Set]** All type in §3 is proposed: three sizes (10 / 15 / 104),
  Sora SemiBold labels, Anonymous Pro Bold for numeral + NEXT, Atkinson
  Regular for the task name. If Set's final table moves a size, the ratios
  (≥1.4× between tiers) and the three-size discipline are the parts I am
  defending — the exact points are negotiable.
- **[→ Rat / Grid]** Day recess `#DAD3CC` is derived, not settled law
  (§3.7) — one tonal step below day ground `#E4DFDA`. Ratify or replace
  with the measured step; the *position* of the recess in the staircase is
  not negotiable, its hex is.
- **[→ Ton]** The gate tick (§4) needs a named waveform: one tick at gate
  crossing, distinct from the seal, silent on refusal. Spec assumes both
  exist as separate feelings.
- **[→ Kin]** Commit completion ≤120ms and dwell ≤120ms are ceilings in
  §4 — the curve inside them is Motion's, provided the seated state is
  *held visibly* for the dwell and never skipped on fast devices.
- **[→ Floor]** Rim scale print at 10sp flagged in §3.7 with its defence;
  if Floor rules the scale itself functional, the fallback is deleting rim
  numerals entirely — never enlarging them into a second readout.

— Vau, Designers (Wow seat)
