# FEEL — THE DRAWER'S AIR (issue #8)

Feel 4 — Atmosphere (issue #8 cell). Light and material only.
Source of law: `studio/plan/AUDIT-drawer.md` (§5 above all: after
the fixes, "the Drawer is the calmest screen in the app AND the
most honest one"), built on the Designer's floor in
`designer-drawer.md` in this folder — its type, layout, shelf and
filing rulings stand untouched. This file answers the one
question the floor leaves open: **what room is he standing in?**

Nobody compiled anything. Nothing here is a Kotlin change.

---

## 0 · THE ROOM IN ONE PARAGRAPH

He is standing in a small records room lit like paper, not like
a screen. The ground is a warm sheet; the print is ink; the only
colour that ever moves is one violet, and it moves only where a
verb lives. There is no furniture in the room except the paper
stack itself — the cabinet took the last trophy furniture out
with it, and the room is better for the air (§6). At night the
same room is read by lamplight turned low: the sheet does not
change, the light on it does. Old months are not darker,
yellower, or lesser — they are simply further down the same
stack, under the same light, in the same ink (§5).

---

## 1 · THE GROUND — PAPER, NOT SLATE, NOT PERMANENT DUSK

**Ruled: the ground is paper.** One sheet colour per light
state (§4), flat across the whole scroll — landing, shelf,
MONTH, SEARCH and DETAIL all stand on the same sheet. No band,
section or view gets its own ground tint (the one exception is
the depth step in §5, and it is a *view* step, never a per-row
or per-month tint).

| State | Ground | What it is |
|---|---|---|
| Room light (day) | `#F7F3ED` | Warm paper — off-white, never `#FFFFFF` |
| Dusk (§4) | `#23212B` | The ink `#34323B` deepened — paper after lights-out, not a black void |

Why paper wins, and the other two lose:

- **Slate loses.** Slate is a work surface — the board you chalk
  sums on. It is blue-grey at every hour, it invites writing
  *on* the archive instead of reading it, and its cool cast
  fights the one violet all day. A grading room's material in
  the no-grading room.
- **Permanent dusk loses.** Dusk is a time, not a material. As
  an all-day ground it makes noon feel sealed, spends the
  night's calm before night arrives, and leaves the 1 AM visit
  nowhere quieter to go.
- **Paper wins** because it is what the room already is: a
  records room whose display voice is Anonymous Pro caps (§6 of
  the audit, acquitted). Paper is matte, warm, silent, and it
  ages by being *kept*, not by changing colour — which is
  exactly the Drawer's promise.

Ground discipline:

- **Flat, always.** No gradient, no vignette, no top-light
  falloff, no texture, grain, noise or watermark. A gradient is
  a lamp the design is pretending not to own; if light changes,
  it changes as a state (§4), never as decoration.
- **No raised sheet.** There is no card colour in this room.
  DETAIL is not a card laid on the ground — it is the same
  sheet, read closer. The app's one raised surface
  (`#FAF6F0`-family) stays in the rooms that own it.
- **No pure white, no pure black, at any hour.** White is the
  fridge light (§4); black is a void, and paper in a dark room
  is not a void — it is dark paper. Both extremes are banned
  from every Drawer surface, including press states and the
  search field.

Zero-green check (house law): every value in this file
satisfies green < max(red, blue). Verified per value in §7.

---

## 2 · ONE MATERIAL METAPHOR — PAPER, ONLY PAPER

**Named and binding: the material of this room is PAPER.**
Not wood, not metal, not a drawer. The eviction makes this
more binding, not less: the last object that could have
justified cabinet-making (the cabinet) is gone, so no
cabinet-making language survives anywhere in the room.

Obeyed, concretely:

- **Ruled, not slotted.** Hairlines are the paper's ruling —
  the faint lines a ledger sheet is printed with (§3). Rows
  sit *on lines*, never *in slots*. No inset wells, no recessed
  trays, no bezel around anything.
- **Stacked, not drawer-slid.** Months are sheets lower in one
  stack. Scrolling is leafing: vertical, continuous, unhurried.
  No horizontal pull, no drawer-slide transition, no handle —
  the chevron was already killed by the Designer; this section
  kills its whole family (pulls, grips, latches, rails).
- **Flat, not thick.** Paper has no elevation in this app.
  No shadows, no edge highlights, no "sheet lift" on press.
  A press is a fingertip darkening paper: the Designer's
  `#493EA4` at ~8% wash — ink soaking in, not a button sinking.
- **Matte, not glossy.** No sheen, no gloss sweep, no specular
  anything, no glass. Nothing in the room reflects.
- **No second material, ever.** No wood warmth (no brown
  furniture tones beyond the paper's own warmth), no brushed
  metal, no leather, no stone. The moment a surface needs a
  material adjective that is not *paper*, it does not ship.
- **Silence is part of the material.** Paper makes no sound
  being read. This room adds no page-turn effect, no slide
  noise, no ambient bed — feel law holds: events only, and
  this room has almost no events.

How the shelf feels like furniture, then, if it may not look
like any: **by weight and permanence, not by imitation.**
Furniture is the thing in a room that does not move, resize,
or rearrange itself between visits. The MONTHS shelf earns
that feeling the honest way — 48dp rows, the same every year;
full-width hairlines that run edge to edge like a shelf's
front edge runs wall to wall; names that never reflow. It
feels built-in because it *behaves* built-in. The 100dp of
empty paper below the last row (§6) is the room's floor
space: you can feel a room's size by the emptiness it can
afford.

---

## 3 · HAIRLINE TEMPERATURE — INK, DILUTED, NEVER A SECOND PIGMENT

**Ruled: a hairline in this room is never a colour. It is the
ink, diluted.** `#34323B` at ~14% over the day ground (the
Designer's system, kept whole); the paper-ink at ~12% over the
dusk ground. The search line's 1dp rule is the same ink at the
same dilution — its extra presence comes from its weight
(1dp) and its job, not from a darker pigment.

Temperature follows from that, and it is the point:

- **By day the lines read warm** — not because the line is
  warm, but because warm paper shows through a translucent
  cool ink. The warmth belongs to the sheet; the line borrows
  it. Any separately-chosen "warm grey" line token would drift
  against the paper within a release or two; a dilution cannot
  drift — it *is* the ink and the ground, mixed.
- **At dusk the lines read cooler** for the same reason in
  reverse: pale ink diluted over deepened ink. Same family,
  same recipe, lower light. The room's temperature change is
  one change (the light, §4), felt everywhere at once —
  never a set of individually re-tinted parts.
- **Banned:** blue-grey or slate hairlines (a second hue
  entering by the thinnest door is still a second hue);
  black lines at any alpha that reads as black; hairlines
  that darken per section to "organise" the scroll — a darker
  rule over old months would be a border drawn around his
  history, and borders grade.

---

## 4 · LIGHT BEHAVIOUR — THE ROOM DIMS WITH THE HOUSE, NEVER FLASHES

The test that governs this whole section is Muede's, from the
audit's §4: the Drawer opened at 1 AM, by a sick man, in the
dark, who needs two taps and then sleep. A drawer that opens
like a fridge — bright cavity, white blast, eyes adjusting
while he squints at ILL — fails the room's only night job.

### 4.1 Two light states, house-ruled triggers

The Drawer owns **no clock of its own**. It has exactly two
states, ROOM LIGHT and DUSK, and DUSK is entered when **any**
house state says the house is dark:

1. the app is in its night scheme, **or**
2. a sealed / focus session is active (the app's existing
   night-forcing state), **or**
3. a SLEEP routine span is active.

Any one is sufficient; none is overridden locally. Two
consequences are binding:

- **Light never follows the filing day.** The 03:30 rule is a
  filing rule (acquitted), not a lamp. At 01:00 the filing day
  is still "yesterday" and the room must be at full dusk; at
  04:00 the filing day has flipped and the room must *still*
  be dusk if the SLEEP span or night scheme says so. An
  implementation that keys light off `DrawerDay` is a bug
  under this spec, by name.
- **A SLEEP span forces dusk even in a light system theme.**
  A drawer opened inside a sleep block is opened in the
  dark, whatever the phone's daytime setting says. If
  routine state is unavailable, fall back to (1) and (2)
  only — never guess from the wall clock.

### 4.2 The first frame is already lit correctly

- Entering the Drawer in dusk composes dusk **on the first
  frame**. No day frame, no white intermediate, no fade-in
  from bright. He must never see the bright version of this
  room at 1 AM — not for 100ms, not once.
- When the state changes *while he is standing in the room*
  (a SLEEP span begins, a session seals), ground and ink
  cross together over the house's long settle — 420ms, the
  existing `DURATION_420` vocabulary — as one light change,
  never element-by-element, never with a brightness pop.
- The Drawer never touches window/screen brightness, never
  raises it for search focus, never holds the screen awake
  brighter than the house set it. It borrows the phone's
  light; it does not make its own.

### 4.3 Dusk values — dimmed light, undimmed reading

Calm at night comes from the *ground and the glare budget*,
never from making print harder to read — sealed reading is
acquitted as full reading, and a sick man does not get a
dimmer ILL as a kindness.

| Role | Dusk value | Notes |
|---|---|---|
| Ground (landing) | `#23212B` | Ink deepened (§1) |
| Ground (MONTH) | `#1C1A23` | The depth step, §5 |
| Primary ink | `#E8E4EE` | Soft paper-white — never `#FFFFFF`; ~12:1 on dusk ground, the house night legibility tier |
| Meta ink | `#E8E4EE` at ~60% | Same dilution recipe as day |
| Hairline | `#E8E4EE` at ~12% | §3 |
| Accent (text / caret / filing words) | `#A79FE6` | The same violet, lifted for dark ground — one hue, its night value, ~6:1 on dusk ground |
| Accent press wash | `#A79FE6` at ~10% | Ink soaking into dark paper |

- The day accent `#493EA4` is **not** used as text on the
  dusk ground: at ~1.6:1 it would make the filing words —
  the one night-critical verb — the least readable print in
  the room. `#A79FE6` is not a second accent; it is
  `#493EA4` under this room's light law, exactly as the
  house night scheme already lifts its own accent one step
  for dark ground. Hue family unchanged; nothing else in
  the room may borrow the lifted value.
- Glare budget: in dusk, the brightest object on screen is
  body text at `#E8E4EE`. Nothing exceeds it — no white
  field behind the search text, no bright caret block (the
  caret is the lifted violet), no filled chip anywhere.

### 4.4 What night never does

No red-shift or "night mode" amber wash (a filter laid over
his history is still a grading of it, and amber is a second
temperature fighting the paper's own). No gradual sunset
animation tied to the clock. No per-visit dimming that
deepens the longer he scrolls. The light is a state with a
trigger and a value — stated, testable, and boring.

---

## 5 · CURRENT MONTH vs DEEP SHELF — WARM BY NEARNESS, COOL BY DEPTH, NEVER GRADED

The air difference is real and must be felt: the current
month is the sheet on top of the stack, handled today, still
warm from the desk. A month from two years ago is a sheet
deep in the stack — the air down there is stiller and
cooler. **The entire difference is carried by light-depth
and stillness. None of it is carried by colour-grading,
fading, or tinting the old work itself.**

### 5.1 The one depth step

- **Landing (current month):** full room ground —
  `#F7F3ED` by day, `#23212B` at dusk.
- **MONTH view (any earlier month):** the same paper one
  shade deeper in the stack — `#F0EBE3` by day, `#1C1A23`
  at dusk. Paper in slight shadow, because it lies *under*
  the current sheet. Ink, meta, hairline and accent values
  are **identical** in both views; contrast is unchanged
  (ink on the deeper day ground still measures ~11:1).

This is a step, not a scale, and the distinction is the law
of this section:

- The step is **binary**: current, or not current. Every
  earlier month gets the *same* deeper ground — SEPTEMBER
  2026 and a month from 2024 stand in identical air. Depth
  beyond "not current" is not measured, so it cannot rank.
- The shelf rows **on the landing** all sit on the landing
  ground. Tinting shelf rows progressively — each older row
  a breath darker — is a gradient wearing furniture's
  clothes, and it is banned by name. The step happens when
  he *opens* a month (he has walked deeper into the room),
  never while he reads the contents page.
- DETAIL inherits the ground of the view he came from:
  a task read from a deep month is read in deep-month air;
  the same task found by search from the landing is read
  in landing air. The task's own print never changes —
  only the room he is standing in when he reads it.

### 5.2 Stillness — the other half of "cooler"

Deep months feel cooler mostly because **nothing down there
asks him anything**:

- No filing zone stands in MONTH — the ask lives on the
  landing only (Designer's ruling); a deep month contains
  no live verb except BACK and EARLIER. Air with no ask in
  it is still air.
- No caret, no focus state, no press wash at rest — the
  violet, the room's only warm-blooded colour, is
  effectively absent from a deep month until he touches a
  row. The current month, by contrast, always holds two
  undeclared filing lines in violet: it is *attended*,
  and attended rooms feel warmer. That warmth is presence,
  not a tint.
- Day groups in a deep month keep the same 18dp shelf
  break and the same hairlines — the coolness is never
  bought by compressing, shrinking, or crowding old work.
  Old paper gets the same margins as new paper.

### 5.3 The anti-grading bans (explicit, because this is where they will be tried)

- No ink fade by age — a 2024 title prints at the same
  `#34323B` as today's. Faded ink says *lesser*; his
  finished work does not fade.
- No sepia, no yellowing, no "archive" tint on old months.
  Paper in this room does not yellow; yellowing is decay,
  and decay is a judgement.
- No saturation or contrast ramp across the shelf, in
  either direction.
- No ground darker than the one §5.1 step, for any month,
  however old. Five years down the stack, the air is the
  same as one month down. **Forever-archive means the deep
  end must feel like the shallow end, or the archive is a
  ranking with a long axis.**

---

## 6 · THE BREATH — WHAT THE CABINET'S ABSENCE DOES TO THE AIR, AND HOW IT IS KEPT

Removing the cabinet is not only a row deleted. A trophy
shelf was the one object in the room that *glinted* —
serials, lore, a standing promise about future finishes.
Trophies change a room's air out of proportion to their
size: they make it a place you visit to *check on things*,
and they pull the eye upward, toward display, in a room
whose whole job is rest at eye level. With it gone, three
kinds of air come back:

1. **Vertical air.** The CURIOS row and its divider are
   gone; STORED is again the heaviest print on the screen
   (the Designer's hierarchy cure lands by subtraction);
   the search line and the filing zone sit closer to the
   work, and the first stored task arrives sooner in the
   scroll. The room feels *nearer* — less corridor between
   the door and the paper.
2. **Chromatic air.** The cabinet was the only reason the
   room held lore-voice, artifact names, and a forward
   promise. Without it, the violet budget drops to verbs
   only, and long stretches of the scroll are pure ink on
   paper. That monochrome stretch *is* the calm §5 of the
   audit promises — it exists only because nothing is
   competing with the work for temperature.
3. **Temporal air.** One less decision per visit. Nothing
   in the room now reports, teases, or awaits completion.
   A room where nothing is pending breathes slower.

**How the design keeps the breath instead of refilling
the room** — the standing temptation after any eviction is
to promote something into the freed space. Banned, by name:

- **The MONTHS shelf does not move up to fill anything.**
  It stays at the foot of the scroll, after the current
  month, exactly where the Designer put it. The freed
  space is not "where the shelf goes now"; the freed
  space is *freed*.
- **Rows do not stretch.** On a sparse archive the landing
  simply ends earlier. Row heights, shelf breaks and
  margins are never inflated to fill a viewport — a room
  that pads itself to look full is lying about its size.
- **The 100dp bottom padding is kept at full value** even
  when the archive is three rows tall. That empty paper
  below the last row is the breath made visible: the desk
  the sheet does not cover. Cutting it on short archives
  to "tighten" the screen spends the exact air this
  section exists to protect.
- **No forwarding sign, no placeholder, no memorial.**
  (The Designer's §6 rules the same; the feel reason is
  this: a "the cabinet moved" notice is a plaque about an
  absence, and a room that plaques its absences has not
  actually let the furniture go.) The wall where the
  trophy shelf hung stays blank paper. Blank is the
  finished state, not a gap awaiting a decision.
- **Nothing new may glint.** Any future proposal that
  wants colour, motion, or display-voice in this room
  must first answer: *are you the cabinet coming back
  smaller?* If its honest answer is yes, it belongs in
  issue #21's room, not this one.

---

## 7 · BUILDER TOKEN TABLE (values only — no code in this file)

| Token (suggested name) | Room light | Dusk |
|---|---|---|
| Drawer ground — landing | `#F7F3ED` | `#23212B` |
| Drawer ground — MONTH depth step | `#F0EBE3` | `#1C1A23` |
| Primary ink | `#34323B` @ 100% | `#E8E4EE` @ 100% |
| Meta ink | `#34323B` @ ~60% | `#E8E4EE` @ ~60% |
| Hairline / search rule | `#34323B` @ ~14% | `#E8E4EE` @ ~12% |
| Accent (affordance only) | `#493EA4` | `#A79FE6` (same hue, night value) |
| Accent press wash | `#493EA4` @ ~8% | `#A79FE6` @ ~10% |
| Raised / card surface | — none — | — none — |
| Banned outright | `#FFFFFF`, slate/blue-grey lines, any green or near-green | `#000000`, `#FFFFFF`, day accent as text |

Zero-green verification: `#F7F3ED` (G243 < R247),
`#F0EBE3` (G235 < R240), `#23212B` (G33 < B43),
`#1C1A23` (G26 < B35), `#E8E4EE` (G228 < B238),
`#A79FE6` (G159 < B230), `#34323B` (G50 < B59),
`#493EA4` (G62 < B164). All pass.

Contrast floors (acceptance, not aspiration): primary ink
≥ 10:1 on both grounds in both states; meta ink ≥ 4:1;
accent as text ≥ 5:1 in dusk (the lifted value exists to
meet this); no floor is met by brightening the ground.

Implementation cost: SMALL. One ground token swapped by
view (landing vs MONTH) and one scheme pair (room light /
dusk) selected by the house states in §4.1; no new
components, no schema, no per-row or per-month data. If the
MONTH depth step proves noisy on-device, it is the one
expendable line in this file — §5.2's stillness carries
the current/deep difference on its own. Everything else
is load-bearing.

---

## 8 · ACCEPTANCE WALKS (feel QA — no compile implied)

1. **The 1 AM walk.** Dark room, SLEEP span active, phone
   brightness low. Open the Drawer. First frame is dusk
   ground; ILL is filed in two taps; at no point does any
   frame read brighter than body text on `#23212B`.
2. **The noon walk.** Room light, same day. Ground is warm
   paper; the only violet on screen is the filing words,
   the search caret, and press washes. Nothing glints.
3. **The five-year walk.** Open the oldest stored month,
   then last month. Air identical except the single §5.1
   step both share; titles in the oldest month print at
   full ink, full size, full margins. If the old month
   feels *lesser* in any measurable way — alpha, size,
   tint, spacing — this spec has failed, not the month.
4. **The empty-room walk.** Fresh archive, nothing stored.
   The landing ends early over 100dp of blank paper and
   reads as a clean desk, not a broken screen. Nothing
   on it mourns the cabinet.

---

## 9 · WHAT THIS SPEC DELIBERATELY DOES NOT DO

- **No wider palette.** `#F04FCB`, `#E2BC39`, `#C84481`,
  `#E47E35`, `#377ED0` were considered for warmth accents
  and shelf furniture, and the room does not earn any of
  them: every job they could do here is either a verb
  (violet's job) or a grade (nobody's job). They stay in
  their own rooms.
- **No Sora.** The absence of Sora is acquitted; Anonymous
  Pro caps is this room's display voice, and paper is why:
  ledgers are not set in poster type.
- **No imagery, no texture, no material rendering** —
  hand-built type and hairlines only, per house law.
- **No opinion on the cabinet's next home.** Issue #21
  owns it. This file says only what its absence does to
  *this* room's air, and how that air is kept.

— Feel 4 — Atmosphere (issue #8 cell)
