# Pia — Wild, watches

Lens: timekeeping mechanisms, stripped of story. A watch earns stillness,
a jump, and a finish — nothing else moves. Every proposal below survives
only because it works, tells the truth, and is beautiful. No narrative
wrapper, no ceremony on the arm path, no second rendering of any fact.

Assigned surfaces: (1) Deck in its running state, (2) the always-on
desk clock as a study display. They are one mechanism seen at two
distances — arm's length in the hand, and across a dark room from the
desk — so they share the same beat, the same numeral behaviour, and the
same finish. If they ever disagree, both are wrong.

---

## Deck — Running Session Display

### Chosen form — Deadbeat Jumping-Minute, Still Between Beats

**The idea in one line:** the running Deck is motionless for 59 seconds
of every minute; on the minute it does exactly three things at once —
the remaining numeral jumps, the progress arc steps, the hub escape
marker advances one tooth — then it is motionless again. In the last
60 seconds the gear changes: the display steps every second.

#### Mechanism and state / data source
- Sole data source: the live session entity — `elapsedSeconds`,
  `remainingSeconds`, `targetDurationSeconds`, `currentBlockIndex`,
  `totalStudyBlocks`, the cued task title if one exists, and the wall
  finish time derived from start time + target. No derived "readiness",
  no estimated progress, no banked or ghost minutes. If the entity does
  not have it, the screen does not show it.
- Hero fact, one home: **REMAINING**, centre of the readout well.
  Format by proximity, and a unit is only ever shown at the cadence it
  is updated:
  - Remaining ≥ 10 min: minutes only — `1:42` (H:MM) or `42` with a
    micro-label `MIN LEFT` under the left edge. Updates once per minute.
  - Remaining < 10 min: still minutes only in the hero; the precise
    `MM:SS` lives once, in the micro-block (see below), updated per
    minute tick as a snapshot is *not* used — see finale rule.
  - Remaining < 60 s (finale): hero becomes `00:47` MM:SS, updates
    every second by hard cut. This is the only per-second state.
- Secondary facts, each once, each demoted, all in the same well on one
  baseline axis beside / below the hero — never a second hero:
  - `ELAPSED 47:12` — Anonymous Pro 13sp, label at 45% ink, value at 80%.
  - `ENDS 5:42 AM` — same style, same line group. Derived from the same
    entity, recomputed only on block change, never ticking.
  - Cued task title, if present: one line, Atkinson Hyperlegible 12sp
    SemiBold, uppercase, letterpressed, single line with ellipsis.
    Absent if no task is cued — no placeholder, no "NO TASK" theatre.
  - Block state as a word only when it is not `1 OF 1`:
    `BLOCK 2 OF 4` in the footnote position. One block needs no label.
- Progress fact, one home: a thin stepped arc on the wheel's outer
  ring, drawn in the ring's own ink. Arc fraction =
  `elapsedSeconds / targetDurationSeconds` of the *current block*.
  It advances only on the minute jump (and per second in the finale),
  in one step per advance. No continuous sweep, no separate bar,
  no percentage numeral anywhere — position is the percentage.
  Block boundaries are notched into the arc as 1dp ground-coloured
  cuts through the fill, so a multi-block session's scale is printed
  on the progress itself. At 0% the arc is honestly empty; no floor,
  no minimum fill.
- Beat mechanism (the watches core): an escape marker — a single
  short radial tooth, 2dp wide, at the arc's head — advances with the
  arc. Between beats there is no balance wheel spinning, no pulsing
  colon, no breathing glow. The colon is a drawn glyph at 45% alpha,
  always. Stillness is not an idle state; it *is* the display.
- Session Cloister, absolute: while running, bottom navigation is
  inert, the wheel does not accept drags, loadout / routines / app
  lists are not editable from this surface, and no prompt, suggestion,
  or optional branch appears. Reachable, and only reachable:
  (a) glance, (b) open the desk clock, (c) the banked emergency break
  (60s total for the week, progress banks at 15 / 30 / 45s, a slip
  drains banked progress rather than zeroing it — its own spec, not
  re-invented here, and it never borrows this display's beat).

#### Motion — curve and milliseconds
- Minute jump: **140ms total.** The incoming minute glyph strip
  translates one glyph height in the first 90ms on
  `CubicBezier(0.2, 0.0, 0.0, 1.0)` with a 2dp overshoot past its seat,
  then settles 2dp → 0 in the final 50ms with one capped oscillation.
  At completion the base value swaps in the same frame. A sliver of
  the neighbouring glyph is visible at the well's edge only during
  those 140ms — that sliver is the depth cue, earned by the move.
- Arc step: synchronised to the same frame as the jump, **120ms**
  `CubicBezier(0.2, 0.0, 0.0, 1.0)` micro-tween of the head marker
  only; the filled arc behind it does not tween — it *was* there.
- Finale per-second change: **hard cut, 0ms.** No jump, no roll, no
  fade. A morphing digit is unreadable for its whole transition and
  the last minute exists to be read.
- Everything else on this surface: **still, by specification.**
  No idle animation is added to fill the quiet. If a frame is
  identical to the previous frame for 59 seconds, that is correct.

#### Haptics
- Minute jump, foreground only, screen on, app in hand:
  one detent click **8ms @ 0.55**. That is the entire per-minute
  budget. Backgrounded, pocketed, face-down, or desk-clock-open:
  **explicit silence** — no haptic at all.
- Finale: no per-second haptics. At 10s remaining, one **6ms @ 0.4**
  tick, once. Stacking ticks under a studying man is a tax, not a beat.
- Finish: see Finish transition below.

#### Sound
- **Explicit silence** for the whole running state: no tick, no room
  bed, no per-minute chime in the chosen form. A synthesised room was
  considered and cut — it is a second, invented fact ("the machine is
  present") rendered in audio, it costs battery for hours, and its
  absence at finish is only moving if you first paid for it all
  session. The one sound belongs to the finish, below.

#### Depth / material
- Readout well: one tonal step recessed below the Deck ground, 1px
  caught-light line on its top lip, 1px darker line on its bottom
  lip. The numeral sits *behind* a cut in the plate, not on it.
- Night: ground `#171412` staircase as locked — surfaces step at
  ≥1.35:1, text tiers ≈11 / 5.6 / 3.4:1, no fill above `#4A423C`.
  Day: same geometry, day tokens. The mechanism does not change
  with the light; only the values do.
- **Zero orange.** Orange was spent as the COMMIT fill on the slide
  that armed this session. The running display spends none — no live
  dot, no arc in orange, no finish numeral in orange. Live-ness is
  carried by the beat and by position, never by colour.
- No drop shadows, anywhere. Depth is the recess, the hairlines,
  and the 140ms glyph sliver, and nothing else.

#### Finish transition — the escapement runs down, then one receipt
- At zero the hero holds `00:00` (or `0` in the minutes band) and the
  arc sits full with its head marker at the terminal notch, for a
  full **1000ms of stillness**. Nothing fires in that second.
- Then, in order, never simultaneously:
  1. **400ms of scheduled silence** continues (no sound has played
     all session, so this is a held breath, not a contrast trick).
  2. One completion pulse — the existing seal/pulse voice, unmodified —
     and one haptic knock **20ms @ 1.0** on the same frame.
  3. The escape marker makes three final advances at widening
     intervals — **1000 / 1300 / 1700ms** — each 120ms, amplitude of
     the arc step shrinking to zero. The mechanism visibly exhausts
     the energy the session gave it; total run-down ≈ **2.4s** after
     the pulse. It is skippable: any tap during run-down completes it
     instantly and lands on the same end state. Skipping costs nothing
     and records nothing different.
  4. Deck unlocks: the arc drains from accent-ink back to resting ink
     over **240ms** on the house throw curve, the slide returns as the
     COMMIT control, and **one truthful receipt** is issued — when,
     how long, which loadout / task if cued — as a single line-group
     on the Deck, dismissible, not archived to any Ledger/Vault screen
     (that tab is dead). No tower, no collection, no second copy of
     the total anywhere else in the app.
- An aborted / emergency-broken session gets **none** of this:
  no run-down, no pulse, no receipt for unfinished time presented as
  finished. The display returns to idle instantly. Truth first.

#### Guard fork
- This surface needs exactly two rested-state preferences: desk-clock
  auto-brightness behaviour and whether the finish pulse may sound
  during a SLEEP routine. Neither is adjustable mid-session under
  either outcome.
  - **If Guard is dissolved (his call):** both live with the other
    session preferences in the Shield-side drawer / band PARK
    position, set when rested.
  - **If Guard survives as a destination (his call):** both live on
    the Guard plate, set when rested.
  - The running display itself is identical either way and contains
    no Guard entry point while sealed.

#### Standards check
- **Arm-path cost: 0.** This surface is reached only after sealing;
  it adds no gesture or wait to cold-open → sealed (≤10s unchanged).
  The finish run-down is post-session and tap-skippable, so it never
  taxes the *next* arm either.
- **Mid-session decisions introduced: 0.** No settings, prompts,
  branches, or toggles. Opening the desk clock and invoking the
  already-banked emergency break are the only acts, and neither is
  a decision *offered by* the display.
- **Truth risk: low, and named.** Risks are (a) showing seconds at a
  slower cadence than per-second — prevented by the format-by-proximity
  rule above; (b) a minimum-fill arc lying at 0% — removed; (c) a
  receipt implying an aborted session finished — prevented by the
  abort rule. Remaining + elapsed + ends are three distinct facts,
  each rendered once, on this surface.

### Runner-up — Continuous Tourbillon Sweep

**Compact spec:** hub becomes a three-spoke cage rotating a full
360° per block (0.24°/s at 25 min), `BLOCKS` counter-rotated upright
inside it; outer arc sweeps continuously, 1:1 with elapsed time,
linear, no steps; hero digits crossfade per second; cage fades/scales
in on arm over 300ms `FastOutSlowIn`. Haptics: none per second, one
cage-lock knock 22ms @ 1.0 on arm + 10ms @ 0.3 echo at +90ms. Sound:
explicit silence. Depth: cage ring 1.5px stroke inside the existing
hub recess, one tonal step below the rim.

**Why it lost:** it is the most watch-like object in the file and it
fails the room it has to live in. Continuous rotation for 50 minutes
is, in peripheral vision, a loading spinner — it signals *waiting*,
not *keeping time* — and it spends attention every second to convey
a fact the minute jump conveys once a minute. It also costs a
continuous animation loop for hours (battery, and on the AOD a
constantly-moving lit perimeter is the worst burn-in shape available),
and its crossfading digits break the hard-cut readability rule in the
finale. Beautiful on a wrist, wrong on a study desk. The cage survives
only as a fragment in *Too good to lose* if a future idle (non-running)
surface ever wants a proof of life.

---

## Always-On Desk Clock — Study Display

### Chosen form — The Rigid Minute Plate That Tours True Black

**The idea in one line:** one rigid plate — remaining hero, elapsed and
ends whispered beside it, a seam of progress at its feet — sits on true
black, changes once a minute in the same jump as the Deck, and the whole
plate, as one object, slowly tours a figure-8 field so no pixel is ever
asked to burn for a whole night.

#### Mechanism and state / data source
- Same sole source as the Deck running state: the live session entity
  plus wall time. The AOD shows **no fact the Deck does not already
  own**, and no fact twice within itself. It is the Deck's running
  truth, recomposed for distance — not a second clock.
- Ground: **true black `#000000`**, full screen, no container, no
  panel, no chrome, no navigation, no task banner card. On OLED,
  black pixels are off: zero glow, zero draw, and the plate floats
  on nothing. Any non-black ground lights the whole room all night
  to frame a number.
- Composition — one rigid `Column` plate, internal geometry frozen
  for the life of the session:
  - Whisper header, one line: cued task title if present (Atkinson
    Hyperlegible 12sp SemiBold, uppercase, 55% cream, single line),
    otherwise nothing. No "DESK CLOCK", no instructions, no live dot
    in any colour. Presence of the plate *is* the live signal.
  - Hero row: **REMAINING** left, Anonymous Pro Bold, mono so the
    plate never changes width (`1:11` → `1:12` is the same object).
    Size by glyph count, pre-built styles, no runtime measuring:
    ≤4 glyphs **132sp**, 5 glyphs (finale) **112sp**, line height
    ≈ size − 4sp, letter-spacing −2sp. Ink: warm cream at **88%** —
    full cream at 132sp in a dark room is a lamp, not an instrument.
    Colon at 45% alpha inside the same string. Format and cadence
    *identical to the Deck*: minutes only ≥10 min remaining, hero
    becomes per-second `MM:SS` only in the last 60 seconds.
  - Micro-block, standing beside the hero at its upper third, top-
    aligned, 16dp gap: two lines, Anonymous Pro 13sp —
    `ELAPSED 47:12` and `ENDS 5:42 AM`, labels at 45%, values at 80%.
    Beside, never centred against, never below as an afterthought.
  - Seam: 24dp below the hero baseline, one hairline across the plate
    width, 2dp. Track at 14% cream; fill = **elapsed fraction**,
    growing left → right at 85% cream (accumulation, not draining —
    time banked, not time lost). Block-boundary ticks cut through
    the fill in ground black, 1dp × 7dp, at each block fraction.
    Head marker: one vertical bar 2dp × 11dp at 100% cream at the
    fill's leading edge — position is the percentage; no `%` text
    exists on this surface.
  - Footnote row, 16dp below the seam, Anonymous Pro 10.5sp at 42%:
    left `BLOCK 2 OF 4` only when blocks > 1 (or the stopwatch floor
    word, or `SAFE BREAK` during a safe break — all from fields the
    entity already carries); right, the time of day
    `THU 29 OCT · 3:47 AM`. A desk clock that cannot tell the time
    of day has forgotten its job; this is its second, quieter job.
  - Exit hint: `TAP TO EXIT` as a ghost only — 30% alpha for the
    first 4 seconds after opening, fades over the house spec and is
    removed. After four seconds the surface carries zero instructions.
  - Plate sits at optical centre with a −24dp vertical bias (the
    seam + footnote weight below the hero pulls mass down), minimum
    32dp clear of every screen edge — that margin *is* the drift
    field's fence (below).
- Modes, same plate, no new layouts: countdown (above); stopwatch
  (hero = elapsed, micro-block = `STARTED 3:02 AM`, seam = floor
  gauge to the 20-minute floor with one tick at 100%, footnote flips
  to `FLOOR CLEARED` past it — the word flip is the whole celebration);
  safe break (hero dims to 60%, shows break remaining, seam frozen,
  footnote `SAFE BREAK` — a break that looks like work is a lie);
  no session (reachable only if opened programmatically: hero = dial
  value at 70%, micro-block `NOT RUNNING`, seam empty, no header).
- Keep-screen-on + brightness, the premise made real: on entry the
  surface sets keep-screen-on and caps window brightness at **0.35**;
  on exit both are restored exactly, in the same dispose block, with
  no leak into the rest of the app. Without this the "always-on"
  clock is a screen the system may kill — the feature's first lie,
  removed before any paint.

#### Motion and burn-in behaviour — one wakeup does both jobs
- **Cadence:** the plate recomposes **once per minute** for ~98% of
  a session (per second only in the finale's last 60s, when he is
  watching anyway). The minute change, the numeral jump, and the
  burn-in move are **the same wakeup** — protection costs zero extra
  power, and screenshot-diffing two captures 5s apart in cruise must
  yield identical pixels.
- **The jump:** inherited exactly from the Deck — 140ms, same curve,
  same 2dp overshoot/settle, colon still at 45%. Consistency across
  the two surfaces is what makes it a mechanism, not an effect.
- **The tour (burn-in):** the whole rigid plate — hero, seam,
  footnote, header together, internal positions never changing —
  translates once per minute to a new offset sampled by the golden
  angle on a Lissajous figure-8:
  `θ = step × 137.508°; dx = 26dp × sin θ; dy = 17dp × sin 2θ`.
  Field **52 × 34dp** centred on optical centre; at maximum excursion
  the plate still keeps ≥6dp clearance inside its 32dp margins
  (field scales ×0.75 under 340dp width). Successive positions never
  repeat a neighbourhood, so over an hour the lit pixels spread
  across roughly an order of magnitude more area than the static
  layout — versus a ±2dp raster nudge, under which a ~5dp digit
  stroke sits on the same pixel columns all night and the protection
  is theatre. Move duration: **520ms** on the house heavy-settle
  curve (`CubicBezier(0.05, 0.7, 0.1, 1.0)`) — if he happens to look,
  the clock settles a few millimetres like an instrument set down;
  in peripheral vision it is subliminal. Still for the other ~59.5s.
- **Self-protection of the static parts:** the hero digits refresh
  by changing; the genuinely static, at-risk elements — colon,
  labels, seam track, ticks — all ride the plate, so no per-element
  trick (alpha cycling, per-part jitter) is needed or wanted.
- **Staged quieting:** after 20 continuous minutes on this surface,
  hero ink steps 88% → 76% and micro/footnote step down proportionally,
  once, on a minute boundary, and holds. The clock gets quieter the
  longer the session runs; it never gets brighter on its own, and
  no notification ever wakes or brightens it.

#### Haptics
- **Explicit silence, total.** The AOD never buzzes the desk — not on
  the minute, not on the tour, not on entry. Entry also plays no
  latch sound. A click at 03:30 into a silent room is a bug wearing
  a feature's clothes.

#### Sound
- **Explicit silence**, with one inherited exception: if the session
  finishes while this surface is open, the Deck's finish sequence
  governs (below) — and if that finish falls inside a SLEEP routine,
  even the pulse is withheld and the finish is visual only. The
  rested user decided that, in the Guard fork below; the AOD does
  not improvise at 03:30.

#### Depth / material
- None, deliberately. No recess, no inlay, no container — on true
  black a container is a lit rectangle burning battery to announce
  itself as a card. Depth here is *distance in the room*: one bright
  fact, two whispered facts, one hairline, on nothing. Warm cream
  (`#FFE4CF` family at the alphas above) is kept over cold white —
  candlelight, not headlight. **Zero orange, zero green,** no other
  colour exists on this surface.

#### Glanceability — the test it must pass
- From 2–3 metres, half-asleep, peripheral vision only, three
  questions answered without reading a word: *how long is left*
  (hero size + value), *am I most of the way there* (head marker's
  position on the seam), *what time is it actually* (footnote right,
  if he chooses to focus). If any of the three needs a label read
  at distance, the plate has failed and the label moves, not the man.

#### Finish transition (AOD form)
- Final frame in cruise is honest: if the last minute is on screen,
  the hero's last cruise frame shows its true value, seam filling.
- At zero: hero holds `00:00`, seam full, head marker at the right
  edge, for **1000ms**. Then the Deck finish sequence's pulse +
  knock govern if sound is permitted (above); the plate itself
  makes **no** brightness breach, no colour change, no alert flash.
  Session end is noticeable because a plate that has toured quietly
  for an hour *stops and then leaves* — the overlay dismisses on the
  existing fade, keep-screen-on and brightness restore, and the Deck
  receipt (Deck section, step 4) is the single record. If he was
  asleep at his desk, nothing on this surface punishes him for it
  when he wakes: no stale `00:00` burns in — dismissal is automatic
  on completion, as the session engine already does.

#### Guard fork
- Preferences this surface needs, rested-state only, never offered
  on the AOD itself: brightness cap (default 0.35), staged-quieting
  on/off, finish-pulse permission during SLEEP routines.
  - **If Guard is dissolved (his call):** they live in the Shield-side
    drawer / band PARK position with the other session preferences.
  - **If Guard survives as a destination (his call):** they live on
    the Guard plate.
  - Entry point, composition, and behaviour of the AOD are identical
    under both. No Guard entry point exists on the AOD under either.

#### Standards check
- **Arm-path cost: 0.** The AOD is opened from inside an already-
  sealed session (one tap on the running Deck) and never sits on
  the cold-open → sealed path.
- **Mid-session decisions introduced: 0.** Tap-anywhere-to-exit is
  the only interaction, and it returns to a Deck that is itself
  cloistered. No buttons exist here because a button is a target,
  and a target is an invitation to leave the session.
- **Truth risk: low, and named.** (a) A hero showing seconds it is
  not updating — prevented by the shared format-by-proximity rule;
  (b) a shrinking remaining-fraction bar reading as loss and lying
  at 0% with a minimum fill — both removed, fill is elapsed and
  honestly empty at start; (c) time-of-day drifting from the minute
  cadence — it is formatted at the same minute wakeup that moves
  the plate, so it is minute-accurate by construction.

### Runner-up — Rising Light / Moon Aperture

**Compact spec:** elapsed time rendered as light itself — five hard
horizontal bands behind the numerals climb a warm ash → sand → bone
ramp as the session progresses (elapsed = height of the warm band,
remaining = dark above it), with a 34dp circular aperture in the
corner across which a moon dot traverses the night's target
(`nightProgress = elapsed focus tonight ÷ tonight's target`).
Numerals stay Anonymous Pro on top; updates per minute; no haptics,
no sound; bands and aperture tour with the plate for burn-in, as in
the chosen form.

**Why it lost:** it is genuinely beautiful and it breaks the first
law twice. Elapsed progress would then live in **two renderings on
one surface** — the seam *and* the light — so one of them has to die,
and the seam is the one that also carries block ticks and a precise
head position; the light carries neither. The moon's mapping is
invented: it needs a printed teaching label ("MOON = TONIGHT'S
TARGET") for its first nights, and a mechanism that needs a label
has not finished designing itself. Worst, practically: a rising
field of lit bands is the largest possible lit area on an OLED held
for hours — maximum battery, maximum room glow, maximum burn-in
exposure, spent on the *least* precise rendering on the surface.
Poetry that costs the instrument its precision and its battery is
decoration. It loses whole; no part of it is smuggled into the chosen
form.

---

## Too good to lose

**1. Retrograde Block Flyback (multi-block turnover).** When one block
ends and the next begins inside a sealed multi-block session — no
decision is offered, the next block simply starts — the seam's head
marker / Deck arc head does horology's most kinetic move: it *flies
back* to the next segment's start instead of sliding home. Spec:
260ms — first 180ms accelerating `CubicBezier(0.55, 0.0, 1.0, 0.4)`,
final 80ms a hard arrival with 1dp overshoot and instant stop; during
flight only, the head draws 3 ghost positions at alpha 0.25 / 0.12 /
0.05. Haptics, foreground only: breakaway tick 6ms @ 0.4 at t=0,
arrival knock 20ms @ 1.0 at t≈260ms. Sound: explicit silence. Why it
matters: block turnover is currently a non-event inside a long
session, so a 4-block morning feels like one undifferentiated slab;
one flyback per block gives the session joints you can feel without
ever asking the man anything. It was not chosen as the main mechanism
because it fires only at block boundaries — it is a joint, not a beat —
but as the turnover gesture on both surfaces above it should ship
with them.

**2. Guilloché Counter-Light on the Running Hub Recess.** The hub
recess (Deck, running state only) carries engine-turned geometry:
~120 concentric hairline arcs centred 18% off hub centre, stroke 1px,
alternating alpha 0.05 / 0.11 in the recess's own ink, plus a single
soft specular band (~70dp) whose angle is `−0.3 ×` the arc head's
travel — as each minute step advances the head, the light rakes
*against* it, so the still hub shimmers exactly once per minute, in
step with the beat, and never between beats. No motion of its own,
no haptics, no sound; night alphas halve. Why it matters: it gives
the running hub a worked, machined surface — life put back with zero
new elements, zero colour, and zero cost between beats, because the
light is a pure function of a position the mechanism already owns.
It lost the hub to the escape marker on truth grounds (one hub,
one job) but it is the strongest idle-surface treatment in this seat
and should be first in line if the hub recess is ever reworked for
the *idle* Deck.

---

## Mail / wall note

Inbox: one letter was read and moved to `pia/read/` per protocol.
A second letter arrived in the inbox after that read; it was left
unread and unacted-on in place, pending verification through the
parent, because a flagged content notice attached to the first read.
No reply was sent from this seat, and no Commons wall post was made
by this seat in this run — the deliverable above is the seat's
complete contribution.
