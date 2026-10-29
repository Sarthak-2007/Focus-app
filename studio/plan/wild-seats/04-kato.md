# Kato — Wild, architecture

## Shield — the routine section as a 24-hour elevation / section

**Diagnosis, from the current Shield renders.** Routines today are two text rows
floating under the loadout band — a name, a span, an ON pill — with the old seeds
(09:00–13:00, 22:30–07:00) still printed on them. Nothing about the rows shows
*how much of the day* a routine owns, where the free time is, or that the sleep
routine wraps past midnight. The information is all in the data
(`startMinutes`, `endMinutes`, `daysMask`, `loadoutName`, enabled) and none of
it is spatial. My wave-1 answer — The Section, a masonry elevation strip
registered to the rows — was adopted in synthesis in its ink form. This file is
that idea rebuilt under settled law, with its three wave-1 errors corrected:

1. **The orange now-line is dead.** Wave-1 spent Shield's accent on the current
   hour. Settled law: orange is a COMMIT fill only, on the Deck slide and Tasks
   START, zero orange in chrome. The now-line below is ink. No exceptions.
2. **The seeds are his real day now:** FASTED BLOCK 05:00–08:00 DAILY · STUDY;
   DAY BLOCKS 09:00–19:30 DAILY · STUDY; SLEEP 20:00–03:30 DAILY · SLEEP.
   Every worked number below uses these.
3. **A routine window is not a session.** The elevation may say a window is
   open. It may never say he is studying — a routine must never record or
   imply a session he was not in. The Deck's running state is that fact's
   only home.

### Chosen form — THE REGISTERED SECTION

One drawing, two readings of it, in a single scroll unit: a true-scale
24-hour elevation strip on the left, and the routine rows on the right,
each row anchored to its block in the strip. Block and row are one object
seen twice — the block carries duration, position, and void; the row carries
name, words, and controls.

**Geometry — the coordinate system (one scale, never broken)**

- The routines section is ONE item in Shield's scroll column (loadout band
  above it, app list below it). Strip and rows scroll together; there is no
  independently scrolling timeline.
- Axis: 00:00 at top → 24:00 at bottom. Scale: **22dp per hour, uniform,
  528dp total.** No compression, no broken axis, no "fit to content"
  rescaling — if the scale ever changed to fit, the drawing would be lying
  about duration. A scale note is letterpressed at the strip's foot:
  `24H · ONE SCALE` (Anonymous Pro 9sp, muted).
- Left column, 64dp wide, in two parts: a 28dp margin carrying hour labels,
  and a 36dp block lane where the masonry lives. Rows occupy the remaining
  width to the 20dp screen margin.
- Hour structure: a 6dp tick inside the block lane every hour (1dp, muted);
  a label every 3 hours in the margin (`00 03 06 … 21`, Anonymous Pro 9sp,
  letterpress — label tone is the surface darkened, never a chip); the 3-hour
  hairline extends at 50% strength across the full row field, so rows sit on
  a visible grid and registration can be checked by eye at any scroll depth.
- Row module: fixed pitch **76dp minimum** — name (Sora Bold 16sp, one line,
  ellipsis), meta (Anonymous Pro 11.5sp: `DAYS · LOADOUT · DURATION`, e.g.
  `DAILY · STUDY · 3H 00M`), and the ON/OFF control as a miniature sled at
  the right (position = state; word `ON`/`OFF` printed in the sled's empty
  half as confirmation only). Duration is always printed — nobody does
  20:00→03:30 arithmetic in their head at 03:30.

**Registration — rows anchored to blocks, displacement disclosed**

- A row's top edge sits at its routine's start position on the axis:
  FASTED at 05:00 → 110dp; DAY BLOCKS at 09:00 → 198dp; SLEEP at 20:00 →
  440dp. With his seeds, start-gaps (88dp, 242dp) exceed the 76dp pitch, so
  every row sits exactly on its start. This is the normal case and it must
  feel like a list that happens to be a drawing.
- **Collision rule:** if a routine starts less than 76dp after the previous
  row's start, its row is pushed down to previous-row-bottom + 8dp. The
  block never moves. The displacement is drawn, not hidden: a registration
  line (1dp ink at 60%) leaves the block's top-right corner, runs right 8dp,
  turns 90° down the lane's right edge, and turns into the row's top edge —
  an elbow leader, drafting style, square bends only. A 4dp open circle
  marks the true start point on the lane edge. A displaced row is visibly a
  callout; an undisplaced row's leader is zero-length and invisible.
- **Editing rule:** tapping a row opens its editor beneath it (time steppers
  with the value inside the control, day strip, 3-stop loadout strip,
  duplicate / rename / delete / pause — the full settled ownership set, in
  the service-panel grammar the routine-row seats already specced). The
  editor pushes every row below it down; their blocks do not move; their
  leaders lengthen by exactly the editor's height. The drawing admits it is
  being edited. Editor motion: height 420ms, `CubicBezier(0.3, 0, 0.8, 0.15)`;
  leaders are redrawn from the same progress value each frame, never
  separately animated. Collapse: 260ms, same curve family reversed in feel —
  faster, no stagger.
- Only one editor open at a time (accordion). Opening one closes another.

**Blocks — masonry, state, overlap, wrap**

- ON block: flat rect, square corners, fill by the routine's own loadout
  (see coupling below), 1dp darker outline. No rounded corners, no chart
  look — this is a section drawing, not a widget.
- OFF / paused block: **dashed outline only** (6dp dash / 4dp gap) at 50%
  ink, no fill — a demolished wall still shown in the drawing. Row type
  dims in step (name to muted tier) so the list triages by tone before any
  word is read. One ghost style only; paused and off are the same fact here
  (not enabled) and get one rendering.
- **Overlaps:** if two ON routines overlap in time, the 36dp lane splits
  into two 17dp lanes with a 2dp ground gap for the overlapping span only;
  each block keeps its true top and bottom. A third simultaneous overlap
  shares the second lane, separated by a 1dp ground line. Blocks never
  stack, merge, or hide each other — an overlap in his schedule is a real
  collision and the drawing shows it as one, in space, without a warning
  dialog or a red badge.
- **Overnight wrap — SLEEP 20:00–03:30, the load-bearing case:** the block
  runs 20:00 → the lane's bottom edge (24:00, 88dp of masonry), and resumes
  00:00 → 03:30 at the top (77dp). Both segments carry the same fill. Each
  cut end gets a continuation serif: a 6dp horizontal tick crossing the
  segment's full width at the edge, 1dp ink — the drafting convention for
  "this continues past the cut." There is exactly **one row**, at 20:00
  (start-day semantics), meta reading `DAILY · SLEEP · 7H 30M`, end printed
  in the editor as `03:30 +1D`. The wrap is never drawn as two routines,
  never given a second name, and the top segment is not tappable separately —
  both segments hit-test to the same row and scroll it into view. The visual
  payoff is the truth of his day: sleep is the one structure that touches
  both ends of the drawing; the day is held inside it.

**Gaps — courtyards, measured**

- Between ON blocks, the lane is empty: ground tone, hour ticks only. That
  void is the design's second subject — unbuilt time reads at a glance
  because the built time has mass next to it.
- A gap of ≥60 minutes between one ON block's end and the next ON block's
  start gets one label, set in the row-field void at the gap's vertical
  centre (not in the lane, so it can never collide with a dashed ghost
  block sitting in the same span): `1H 00M OPEN` — Anonymous Pro 9sp, muted,
  flanked by two 12dp hairlines. His day reads: 03:30–05:00 `1H 30M OPEN`
  (meditation / morning — unbuilt by design), 08:00–09:00 `1H 00M OPEN`
  (gym — unbuilt in this app, and the label says OPEN, not EMPTY, because
  the app only knows it holds no routine there), 19:30–20:00 unlabelled
  (30m, below threshold).
- Gap labels are derived arithmetic on enabled routines only, recomputed on
  any edit. They are not objects: not tappable, not editable, no state.
  They hide while any editor is open (they describe the collapsed drawing).
- A gap label never claims what the gap is *for*. It measures void; it does
  not interpret it.

**The now-line — ink, once a minute, modest claims**

- A 1dp ink line crosses the block lane at the current time, with the
  current hour label in the margin set at full text tier (all other hour
  labels muted). It extends 8dp into the row field and stops — it marks the
  axis, it does not underline a row.
- The row whose window contains now gains a 2dp ink spine on its block's
  left edge and one meta suffix: `· WINDOW OPEN — ENDS 03:30`. That wording
  is the truth ceiling. Not LIVE, not RUNNING, not IN SESSION: the schedule
  is open; whether he is in a session is the Deck's fact, rendered there.
- Updates on a 60-second tick while Shield is visible, and on Shield entry.
  No animation, no sound, no haptic — a clock hand does not applaud itself.
  At 03:30 the line sits inside sleep's top segment, which is correct and
  quietly the best summary of his life this screen can give.

**Loadout coupling — per-routine, not a global sweep**

Wave-1 coupled the whole elevation to the loadout cap: move the cap and
every block re-toned in a staggered sweep. That was theatre, and it was
false — it implied every routine belongs to whichever loadout is currently
selected. Corrected coupling:

- Each block's fill encodes **its own routine's loadout**, permanently:
  STUDY = one tone step below ground; DEEP = two steps below ground;
  SLEEP = half a step below ground with its outline at full strength (rest
  reads palest, but bounded). Night staircase equivalents: fills resolve
  upward from `#171412` in the locked steps, capped at `#4A423C`; SLEEP at
  night is the step just above ground with a cream-tier outline at 35%.
- A legend is printed once, at the strip's foot, on the same baseline as
  the scale note: three 8×8dp swatches with `STUDY / DEEP / SLEEP` in
  Anonymous Pro 9sp. One legend, one home — rows repeat the loadout as a
  word in meta because a row must be readable without the drawing; the
  legend exists so the *fill* is never a code he has to guess.
- The Shield loadout cap (the manual, right-now loadout) does **not**
  re-tone the elevation. Moving it changes what a manual session will use;
  the schedule drawing is not that fact and does not perform it.
- Changing a routine's loadout **in its editor** re-tones its block:
  crossfade 180ms, `CubicBezier(0.2, 0, 0, 1)`, landing with the editor's
  own firm-click haptic (13ms @ 0.9). Cause and effect in the same gesture,
  nowhere else.

**Create / duplicate — building in the drawing**

- Section foot, under the legend: `+ NEW ROUTINE` as a word (no filled
  button). It opens a new row in edit state at its default position
  (09:00–10:00, DAILY, STUDY) with its block shown as dashed outline plus a
  dotted inner line — the third line style, used only here, meaning
  *unsaved*. The editor's footer word is `NOT SAVED — SET THE WINDOW`. On
  first save the block wipes to solid (build wipe, below) and the dotted
  line dies forever. An unsaved block can never be mistaken for a scheduled
  one, and abandoning the editor deletes it with no trace.
- Duplicate (in the editor) copies the routine, opens the copy in edit
  state, and displaces via the normal collision rule until he moves it.

**Motion, haptics, sound — full spec**

- Toggle ON (build): block fill wipes **upward from its base**, 260ms,
  `CubicBezier(0.3, 0, 0.2, 1)` — a wall rising from its footing. Wrapped
  routines wipe both segments simultaneously from their shared logical
  base (the 20:00 segment from its base; the 00:00 segment from the lane
  top downward — it is the same wall, continuing). Haptic on completion:
  14ms @ 0.8, +50ms, 8ms @ 0.5 (footing, then course). Row sled throws in
  the same 260ms window on the app's standard sled curve; the ViewModel's
  latch sound is the toggle's only sound.
- Toggle OFF (demolish): fill wipes downward, 220ms, same curve — gravity
  helps. Haptic: three ticks, 4ms @ 0.35, spaced 18ms (a short crumble).
  The dashed outline is present from the first frame of the wipe; the wall
  is being demolished *in* its own ghost.
- Editor expand/collapse: as above (420 / 260ms); haptic, one firm click
  on open (13ms @ 0.9), one rotary detent on close (7ms @ 0.5, 5ms tail).
  Time steppers: detent per accepted step, 5ms @ 0.4; a step the span
  clamp refuses is silent and still — refusal is absence of feedback.
- Scroll: nothing. No haptics, no sound, no parallax. A drawing does not
  perform while you walk past it.
- Sound, total: the elevation itself is **silent by design**. The only
  sounds are the existing engine patterns fired by the controls it hosts
  (latch on toggle, firm click on loadout, detents on steps) — one call
  site per action, none doubled, none ambient.

**Depth / material**

- Tone steps and hairlines only; zero drop shadows, zero gradients, square
  corners everywhere in the lane. Blocks sit *on* the ground, not in cards;
  the lane itself has no container fill — the masonry is drawn on Shield's
  ground, separated from the row field by one 1dp vertical hairline running
  the section's full height (the section cut line). Recess is used exactly
  once: the editor panel floor sits one tone step down, so opening a row
  reads as opening the wall the block belongs to.
- Type: Sora Bold 16sp names; Anonymous Pro for every numeral, label, and
  meta string; Atkinson Hyperlegible nowhere in this section (no prose
  exists here). Text tiers at night ≈11 / 5.6 / 3.4:1 against `#171412`.

**Guard fork — his call, both placements stated**

The elevation contains no Guard fact under either outcome — no permission
counts, no website counts (those facts keep their existing homes; duplicating
them here would break one-fact-one-rendering).

- *If Guard dissolves:* its websites module lands in Shield **below the app
  list**, and permissions move to chrome/drawer per that plan. The routine
  section is untouched and stays directly under the loadout band — routines
  remain Shield's hero, nothing Guard-shaped ever enters the drawing.
- *If Guard stays a destination:* equally untouched. The elevation neither
  gains nor loses a neighbour inside Shield.
- Lens note (architecture, labelled his call): a section drawing should
  contain one system — the day's structure. Guard is a different system
  (the perimeter). Keeping it out of the elevation is right under both
  outcomes; where the perimeter's own screen lives is not this element's
  business.

**Standards check**

- **ARM ≤10s:** zero cost. This surface is not on the arm path and adds no
  gesture, prompt, or wait to it. Editing a routine is rested-user work,
  done in Shield, never required before a session can start.
- **HOLD absolute:** zero mid-session decisions introduced. The elevation
  is viewable but its edits change the *schedule*, never a running session;
  nothing here fires, prompts, or negotiates while a session runs, and the
  now-line's `WINDOW OPEN` wording cannot be mistaken for a session control.
- **TRUTH once:** uniform 22dp/h scale with no compression anywhere;
  displacement drawn as leaders, never absorbed silently; wrapped sleep is
  one routine, one row, one name; gap labels are arithmetic on enabled
  routines, labelled OPEN, never interpreted; window-open never rendered
  as attendance; loadout fills encode each routine's own loadout, with the
  global cap explicitly decoupled; no history, no totals, no record —
  the Ledger is dead and this drawing keeps no past.

### Runner-up — THE ROTATED SECTION (axis origin at his wake, 03:30)

Same strip, same rows, same registration and collision rules — one change:
the axis runs 03:30 → 03:30. SLEEP (20:00–03:30) becomes a single unbroken
block at the foot of the drawing, ending exactly on the axis end;
continuation serifs and the whole wrap apparatus disappear for his seeds,
and the drawing's bottom edge *is* his day boundary — the foundation course
the next day stands on. Hour labels run `03 06 09 … 00 03` down the margin.
Now-line, gaps, coupling, motion, and haptics identical to the chosen form.

**Why it lost.** Three reasons, in order of weight. (1) *Scanning:* every
human reads a day axis expecting 00 at the top; burying midnight two-thirds
down makes every time lookup start with a translation, forever, to save
one wrap convention. (2) *The wrap isn't solved, it's moved:* any routine
crossing 03:30 — a 02:00–04:00 test-eve block is exactly the kind he will
someday add — wraps across the seam instead, and it wraps across his wake
and meditation, the worst possible place. (3) *Stability:* the origin is a
habit, not a constant. The day his wake moves to 04:30, either the drawing
re-bases and every block he knows jumps position, or the origin stays and
quietly becomes a lie about when his day starts. The chosen form's 00:00
origin never moves, and its wrap — drawn honestly, serifs and all — turns
out to be a feature: sleep visibly holds both ends of the day.

## Too good to lose

1. **Build in the courtyard.** Press-and-hold (350ms, one 10ms @ 0.6
   press-knock) on a labelled gap in the row field opens a new routine in
   edit state pre-filled with exactly that gap's span and the surrounding
   routines' days — creating a routine *inside the void it will fill*, its
   unsaved dotted block already standing in the lane at true scale. It
   lost the main spec only because creation already has a front door
   (`+ NEW ROUTINE`); as a second door it must never ship before the first
   is proven. If it ships, it replaces nothing — the foot word stays.
2. **The elapsed axis.** The margin segment *above* the now-line (time
   already past today) drops one tone step; below it, full tone. The axis
   itself ages through the day — at 03:30 the drawing is nearly all future,
   at 19:30 nearly all past. It shades **only the margin and hour labels,
   never the blocks**: elapsed clock time is a fact; a block turning "done"
   would claim attendance the schedule cannot know. One tone step, updated
   on the same 60s tick as the now-line, silent. Cut from the chosen form
   to keep the now-line the section's single temporal gesture — it is the
   first thing to restore if the section ever feels static.

## Mail / wall note

Inbox empty at start. Two letters arrived during the work — from Orson and
from Mara, both relaying Sarthak's order lifting the citation requirement
(the same order Marlowe passed down directly). Read; both moved to `read/`.
No reply needed — the order was already in force in this file, which cites
nothing except where a technique genuinely earns it. One letter sent: to
Sten (Maximum-Implementable, routines) — the registration / displacement
rule and the window-open wording ceiling, since his seat will have to
build against both. One wall post appended to COMMONS.md: the orange
now-line correction and the rotated-axis rejection, for any seat still
carrying wave-1's version.
