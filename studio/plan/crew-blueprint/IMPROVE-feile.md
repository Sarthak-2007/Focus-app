# IMPROVE — FEILE (Blueprint crew, Improver, under Riss)

*Second pass over PRESENTATION-blueprint.md + the trunk it improves.
Every delta names: element · current behaviour (file:line) ·
improved behaviour in Blueprint grammar · price · builder partition
(Monteur = shell/theme/PARK · Ausbau = Deck · Einbau = Shield /
Tasks / Drawer / receipt / clock / door / pill). Ranked by
(his pain × frequency) ÷ price. No delta without a file and a
change made this list.*

*Inputs state, plainly: inbox `mail/feile/` was EMPTY at seating
(0 letters — nothing to answer, mail already at zero).
`plan/crew-blueprint/DESIGN-zirkel.md` has NOT landed — the folder
holds only FLOW-verlauf.md and REGISTRY-registratur.md. This pass
therefore improves the presentation spec + trunk directly; where
Zirkel's geometry later contradicts a figure here, the figure is
mine to re-argue, not silently right. Line numbers are trunk
(`Focus-app/app/src/main/java/com/example/ui/…`), read this run.*

*Checked, NO delta (acquittals, so nobody re-hunts them): tapping
the wheel's rim already sets the value — `RotaryWheelView`
ACTION_DOWN outside the hub calls `updateMinutesFromTouch`
(BlockitCoreViews.kt:1005ff), so rim numerals are already a
tap-to-set surface. The Drawer empty states are already honest
(`NOTHING STORED YET`, DrawerScreen.kt landing; `NOTHING STORED
UNDER "<query>"` in search). The app-row sled's live area is
already the full 54dp row height (ShieldMatrixScreen.kt:302),
not just its painted 34dp track — a good accident, kept.*

---

## RANKED DELTAS

### F-01 — Sled: the name-area tap that silently cycles the tier — DELETE IT
- **Current:** `AppRowSledToggleView.onTouchEvent`
  (views/BlockitSuitcaseAndSwitchesViews.kt:337–355): an ACTION_UP
  left of the sled does not miss — it CYCLES
  LOCKED→PEEK_2M→OPEN→LOCKED. A tap on the app's name, or a
  scroll that ends as a tap, weakens or strengthens the shield
  with no label, no figure, no trace except the quantity line
  changing somewhere above.
- **Improved (Blueprint):** nothing unlabelled (§0 law 5). Taps
  outside the sled's track do nothing. The sled is the only verb
  on the row; the row's left half is print (name + status pip).
- **Price:** ~8 lines deleted/changed, 1 file.
- **Partition:** Einbau.

### F-02 — Slide: the tap shorthand only lives on the cap — GIVE IT THE WHOLE PLATE
- **Current:** tap-to-commit hit-tests the cap only — drawn rect
  inflated 8dp in x, full height in y (BlockitCoreViews.kt:1911:
  `downOnCap = event.x >= capLeft - dp(8f) && … <= capRight +
  dp(8f)`; cap 84dp wide, SlideToStartView:1731). At Deck width
  (~366dp view) the cap rests at x≈8–92dp: for a right-handed
  one-hand grip at 03:30 that is the far, stretch corner of the
  thumb arc — the shorthand lives in the worst seat on the plate.
  The drag it replaces costs a ~245dp finger sweep (travel
  266dp × p=0.92 to reach displayed CATCH 0.84 through the gear,
  SlideGestureMath:1312–1345).
- **Improved:** Blueprint §1.10 already rules the commit is a
  full-width solid plate — so the plate IS the tap target. Any
  press ≥90ms with <16dp movement (F-03) anywhere on the track
  runs the same mesh to 0.92; a press that moves becomes the
  drag, exactly as now. The drawing does not grade where on the
  plate his thumb landed.
- **Price:** ~15 lines, BlockitCoreViews.kt (beginAttempt /
  isTap classification), 1 file. No machinery: mesh, seat and
  warrant reads unchanged.
- **Partition:** Ausbau.

### F-03 — Slide: tap slop 8dp + silent brush — LOOSEN, AND PRINT THE REFUSAL
- **Current:** a tap dies if the finger moves >8dp
  (BlockitCoreViews.kt, dragMove: `abs(event.x - downXPx) >
  dp(8f)` sets movedBeyondSlop; endDrag:2050 then refuses the
  tap branch) and a press <90ms is a brush (SlideGestureMath:
  1327, 1356) answered only by a 1dp sink + dull knock — at
  03:30, eyes half shut, both read as "nothing happened."
- **Improved:** tap slop 8→16dp (still 6% of the 266dp travel —
  no drag can be mistaken for a tap). A brush on the cap leaves
  one annotation-red hairline tick in the reserved sentence
  slot for 600ms — the draughtsman marks the contact he refused,
  in the slot that already exists for corrections. No new words.
- **Price:** ~20 lines, BlockitCoreViews.kt + the sentence-slot
  host flag in TactileDeckScreen.kt. 2 files.
- **Partition:** Ausbau.

### F-04 — Filter words: 20dp-tall touch on a daily tool — 48dp
- **Current:** `FilterWord` (ShieldMatrixScreen.kt:548ff) is a
  bare clickable Column: 11sp text + 3dp gap + 2dp bar ≈ 20dp of
  live height for ALL / SHUT / PEEK / OPEN filtering.
- **Improved:** the word stays print; its cell becomes a ruled
  index tab 48dp tall, word centred, selected underline at full
  weight as spec §2.6 already draws it. Touch grows, ink doesn't.
- **Price:** ~6 lines, 1 file.
- **Partition:** Einbau.

### F-05 — NOW row DONE: a verb in a 25dp key — 48dp, and QUEUE gets a cell
- **Current:** DONE is a Box with `padding(horizontal = 10.dp,
  vertical = 5.dp)` around 11sp text (TactileDeckScreen.kt:
  764–783) ≈ 50×25dp; QUEUE n beside it is bare clickable Text,
  ~14dp tall (TactileDeckScreen.kt:784–795). DONE is pressed
  mid-session, one-handed, often without looking; QUEUE opens
  the sequencer. Both are fingertip work priced as print.
- **Improved:** per §1.7/§1.10's own ruling — DONE takes the
  outline key at row scale, now min 64×48dp, solid only while
  pressed; QUEUE n becomes a margin cell, min-height 48dp,
  figure right-aligned to its column. Row height grows ~23dp;
  the sub-step rows under it take the same 48dp row height
  (currently `padding(vertical = 3.dp)`, TactileDeckScreen.kt:
  ~800 — a 13sp line ≈ 22dp live).
- **Price:** ~25 lines, TactileDeckScreen.kt, 1 file.
- **Partition:** Ausbau.

### F-06 — Routine ON/OFF toggle: 48×32dp — BELOW THE FLOOR IT GUARDS
- **Current:** the routine toggle Box is `width(48.dp) /
  height(32.dp)` (ShieldMatrixScreen.kt:486–487). 32dp tall for
  the switch that decides whether SLEEP arms at 20:00.
- **Improved:** 56×48dp ruled margin cell, hatched when OFF
  (spec §2.5's revision toggle, at a thumb's size). ON/OFF
  figures stay Anonymous Pro inside the cell.
- **Price:** ~8 lines, 1 file.
- **Partition:** Einbau.

### F-07 — Guard line tap: the header's only door is 20dp of text in a 47dp band
- **Current:** `MonolithPullDownHeader`
  (components/MonolithCustomComponents.kt, header band): the
  Guard line's clickable sits on the Text itself — 15sp ≈ 20dp
  tall — inside a 47dp row that is otherwise dead.
- **Improved:** the margin note's tap cell extends to the full
  47dp band height and from the note's left edge to the plate
  edge. Print unchanged; the door is the margin, not the glyphs.
- **Price:** ~8 lines, 1 file.
- **Partition:** Monteur.

### F-08 — Held Door keys: RETURN / PEEK at chip height 42dp — 56 / 48
- **Current:** the door's two exits are BlockitActionChips,
  whose height is hardcoded 42dp (MonolithCustomComponents.kt:
  200); the door composes them full-width in
  AirlockAndDisarmOverlays.kt (RETURN TO FOCUS; EMERGENCY PEEK
  when the countdown ends). The door is the most-seen surface
  in the app and the one he operates mid-temptation, one hand.
- **Improved:** §4.5's two keys at door scale: BACK/RETURN
  outline key 56dp tall, PEEK figured key 48dp tall. Chip gains
  an optional height parameter (default 42dp — every other
  caller byte-identical).
- **Price:** ~15 lines, 2 files (chip param + door call sites).
- **Partition:** Einbau.

### F-09 — Loadout cap: 36dp track, 36dp touch — TOUCH TO 48, DRAWING UNCHANGED
- **Current:** ShieldCapSelector track is 36dp, cap 30dp, and
  the tap Row fills only the track's 36dp (ShieldCapSelector.kt:
  100, 134, tap Row ~160ff) — inside an 84dp slot whose lower
  half (ticks) is dead to touch.
- **Improved:** the throw's drawn geometry does not move (his
  picked object, pixel-identical); the tap zones extend
  vertically to a 48dp band centred on the track, overdrawing
  nothing — the ticks row beneath stays print. A dimensioning
  hand should not have to aim at a 36dp line at 05:00.
- **Price:** ~12 lines, ShieldCapSelector.kt, 1 file.
- **Partition:** Einbau.

### F-10 — FLOW active: the scale should sink, not just the hub
- **Current:** spec §1.5 rules that while FLOW is active the
  wheel's ticks sink to hairline (the scale drawn out of use).
  Trunk dims only the hub assembly to 40% (BlockitCoreViews.kt:
  928ff, guide ring/wells); the detent tick loop (~750ff) draws
  at full voice in FLOW.
- **Improved:** tick paint takes the same 0.4 factor when
  `minutesValue < 10` — one conditional in the tick loop,
  majors/mids/minors all sinking together, restoring at the
  10-shore landing.
- **Price:** ~6 lines, BlockitCoreViews.kt, 1 file.
- **Partition:** Ausbau.

### F-11 — Deck with no active task: the NOW row vanishes — PRINT THE EMPTY ITEM
- **Current:** `if (activeTask != null)` guards the whole NOW
  section (TactileDeckScreen.kt:725) — with no task cued, the
  Deck simply has no line where item 01 should be; absence is
  indistinguishable from a broken Deck.
- **Improved:** §4.1's empty-state honesty, applied to the Deck:
  a hairline-ruled row `NOW — NOTHING QUEUED`, figure muted,
  tapping it opens Tasks. One line, present exactly when the
  fact is.
- **Price:** ~12 lines, TactileDeckScreen.kt, 1 file.
- **Partition:** Ausbau.

### F-12 — Sled middle stop labelled "2m": a duration wearing a tier's seat
- **Current:** stops print `SHUT / 2m / OPEN`
  (BlockitSuitcaseAndSwitchesViews.kt:296ff, with a special
  mixed-type draw for the "2"). Everywhere else the tier's name
  is PEEK (spec §2.2's quantity line, §4.5's door); "2m" is the
  lease's length, a different fact, printed in the tier's cell.
- **Improved:** stops print SHUT / PEEK / OPEN in the sled's
  word voice (the mixed-mono branch dies). The length gets its
  one lawful seat: a column head figured once above the first
  app row on Shield — `PEEK = 2 MIN` in the parts list's margin,
  Anonymous Pro, muted. One fact, one rendering, per tier and
  per length.
- **Price:** ~15 lines across BlockitSuitcaseAndSwitchesViews.kt
  + ShieldMatrixScreen.kt. 2 files.
- **Partition:** Einbau.

### F-13 — Slide words fade out exactly at the catch — KEEP THE PLATE'S WORDS LEGIBLE
- **Current:** during a hand drag the cap words fade as
  `1 − 1.3·frac` (BlockitCoreViews.kt onDraw, ~1785): fully
  gone by displayed 0.77 — before the catch at 0.84, i.e. the
  instruction disappears at the one moment the hand is
  deciding whether to finish the throw.
- **Improved:** on the Commit Plate the words never fade; they
  are printed in the un-travelled segment of the track,
  right-aligned to the plate's right margin, plate-white on
  indigo — as the cap advances the words' seat shortens but
  their ink does not. (Mesh state already holds fade at 1;
  this extends the same law to drags.)
- **Price:** ~15 lines, BlockitCoreViews.kt onDraw, 1 file.
- **Partition:** Ausbau.

### F-14 — Title block index cells: the spec names no size — SET THE FLOOR BEFORE IT IS BUILT
- **Current:** spec §1.13 replaces the rail with title-block
  index cells but prices no dimensions. The trunk rail it
  replaces runs CAP_HEIGHT 44dp on a 52dp band, segments
  73.5dp wide at 390dp, PARK zone 48dp (MonolithRailView.kt:
  26–45) — a builder copying the nearest numbers ships 44dp
  cells, under the 48dp thumb floor, on the control he touches
  most (every plate change, all day).
- **Improved (rule, written into §1.13 before build):** index
  cells min 48dp tall × 64dp wide; PARK cell min 56dp wide
  (a verb + a tired thumb). At plate widths where
  4×64 + PARK 56 + wordmark cannot fit one row (≤360dp wide:
  rail segments already shrink to 66dp, and at 320dp to 58dp),
  the title block takes TWO rows — wordmark + ISSUED row above,
  index row below — never narrower cells. The drawing's cell
  loses to the thumb, by rule, in advance.
- **Price:** ~30 lines at build time (RailGeometry constants +
  host band height), MonolithRailView.kt + Deck host. Priced
  here so Monteur builds it once, correctly.
- **Partition:** Monteur.

### F-15 — Wheel: no figured step at the wheel's own seat — ±5 KEYS ON THE FLOW ROW
- **Current:** the wheel is the only session-length control
  (288dp tall, TactileDeckScreen.kt:646); changing 50→65 means
  orbiting the rim — 27° of arc per 15 min at rim radius ~130dp
  — or flinging and catching. The FLOW row beneath it
  (TactileDeckScreen.kt ~660–695) holds one key and empty space.
- **Improved:** two figured keys flank FLOW on that row:
  `−5` and `+5`, Anonymous Pro, outline keys 48dp tall, wired
  to the existing `onDialMinutesChanged` with the pitch ladder
  firing per step (the ear confirms, §5). They are dimension
  adjusters, not a second wheel: the figure (§1.3) and the
  wheel stay the one rendering; the keys only step the hand's
  measurement when the hand cannot orbit — 03:30, one hand,
  phone just picked up.
- **Price:** ~30 lines, TactileDeckScreen.kt, 1 file.
- **Partition:** Ausbau.

### F-16 — Tasks leaf verbs: DONE — TEAR at 20dp, DEL 20dp from CUE — A 48dp ACTION ROW
- **Current:** TaskLeafItem's action row (FocusSequencerScreen.kt
  actions Row, ~640ff): DONE — TEAR is bare clickable 15sp text
  (~20dp live), SPLIT/CUE/DEL bare 12sp text (~16dp live),
  spaced 20dp apart — CUE and DEL are ~30dp-wide neighbours,
  and a tired mis-tap deletes where it meant to cue (UNDO
  exists; the apology should not be the design).
- **Improved:** the leaf's action row becomes a ruled row
  48dp tall; each verb's cell min 48dp wide; DEL leaves the
  cluster and takes the row's right end as a margin verb in
  annotation red (a removal corrects the schedule — §2.5's
  delete rule, applied to the leaf). DONE — TEAR keeps its
  seat and its size of voice; only its cell grows.
- **Price:** ~25 lines, FocusSequencerScreen.kt, 1 file.
- **Partition:** Einbau.

### F-17 — Editor day chips: M T W T F S S — AMBIGUOUS LETTERS ON A FIRING DECISION
- **Current:** the expanded editor's day row prints single
  letters `M, T, W, T, F, S, S` (ShieldMatrixScreen.kt:531) —
  two T's, two S's, distinguished only by position. Spec §2.5
  already kills bare letters for the collapsed label
  (routineDaysLabel, :403ff, prints MON–FRI / named days); the
  editor — the surface where the mask is actually SET — kept
  the ambiguous form. A wrong tap here fires a routine on the
  wrong day, silently, for weeks.
- **Improved:** chips print MON TUE WED THU FRI SAT SUN in
  miniature (Anonymous Pro 10sp), cells min 44dp wide × 48dp
  tall, wrapping to two ruled rows (4+3) inside the editor
  sheet. Position stops being the only disambiguator.
- **Price:** ~12 lines + row wrap, ShieldMatrixScreen.kt, 1 file.
- **Partition:** Einbau.

### F-18 — Editor steppers overflow their row, and ±30 is the only step that exists
- **Current (two facts, one element):** (a) the expanded row
  packs START + −30 + +30 + END + −30 + +30 + loadout chip in
  one Row (ShieldMatrixScreen.kt:504–528): at 42dp chip height
  (MonolithCustomComponents.kt:200) the contents measure
  ~430dp against ~366dp available — the row compresses its
  chips on real phones. (b) ±30 is the ONLY delta offered,
  while `adjustRoutineTime(routine, isStart, deltaMinutes)`
  (MonolithViewModel.kt:1344) accepts any Int — so 05:25, or
  correcting 20:00→20:15, is unbuildable by hand.
- **Improved:** split into two ruled rows, START over END,
  each: figured time (the row's existing figure, enlarged to
  a tappable cell — F-20's field), then step cells
  `−15 −5 +5 +15` at 48dp (the ±30 pair dies; 15+15 reaches
  every 30 it could, and 5 reaches what it never could).
  Steppers fire the pitch ladder per step (§5's law, editor
  included — spec §3's table already claims it).
- **Price:** ~35 lines, ShieldMatrixScreen.kt, 1 file; 0 VM
  lines (the delta parameter is already general).
- **Partition:** Einbau.

### F-19 — Day-scale: a 25-minute routine is a 6dp sliver with a 73dp label — THE SLIVER RULE
- **Current:** trunk has no day-scale — routines are rows only
  (ShieldMatrixScreen.kt:185–210) — and spec §2.4 builds the
  scale (03:30→03:30 across the Shield's width) with NO rule
  for short segments. Arithmetic the builder will meet: usable
  width ~342dp ÷ 1440 min = 0.237dp/min. FASTED 3h = 43dp —
  fine. A 25-min routine = 5.9dp of segment carrying a label
  `05:00–05:25` ≈ 73dp wide at 11sp. Centred labels collide;
  inside labels are impossible; the drawing lies by omission.
- **Improved (the rule §2.4 ships with):** segment widths stay
  TRUE — a drawing never fattens a measurement. Labels route
  by width: ≥60dp segment → label above, centred, as spec'd.
  12–60dp → leader line from the segment's midpoint to a
  staggered second label row (labels alternate row 1 / row 2
  in time order, extension ticks at both boundaries). <12dp →
  no on-scale label at all: the segment carries its item
  number only, figured on the scale, and the number is the
  parts-list row's number below (01, 02…) — the scale points
  at the row, the row carries the words. Disabled (hatched)
  segments follow the same routing. This must land WITH the
  scale's first build, not after: retrofitting label routing
  re-draws every Shield render.
- **Price:** ~70 lines, one new DayScale composable +
  ShieldMatrixScreen.kt seat. 2 files.
- **Partition:** Einbau. (Blocks the §2.4 build — flagged for
  Riss: Einbau should not start the scale without it.)

### F-20 — Editor figured field + keypad: the spec's end state, priced honestly
- **Current:** spec §2.5 promises direct time-setting to any
  minute (tap the figure, keypad or 1/5/15 steps); trunk
  offers only the ±30 chips of F-18. F-18's steppers are the
  interim cure, not this one.
- **Improved:** tapping the START or END figure on the editor
  sheet opens the figure for entry: a numeric keypad sheet
  (the one raised white sheet, §0 law 4) with HH and MM figured
  fields, the day-scale (§2.4, with F-19's routing) redrawing
  live above it, collisions annotated red at the touching
  boundaries as spec'd. SAVE is an outline key, never the Plate.
- **Price:** ~140 lines new sheet composable + ~15 lines VM
  (an absolute `setRoutineTime` beside adjustRoutineTime:
  1344) + wiring in ShieldMatrixScreen.kt. 3 files. Ranked
  last by the formula — highest pain, lowest frequency,
  highest price — but named so it is never mistaken for done
  when F-18 lands.
- **Partition:** Einbau.

### F-21 — Horizon bar: name-only tasks draw equal workload — A FALSE FIGURE
- **Current:** TaskHorizonBarView weights every segment by
  `estimatedMinutes.coerceAtLeast(10)` (BlockitTowerFadersAnd
  CatalogViews.kt:194–198). Since MINUTES DIE, a queue of
  name-only tasks all carry 0 → all coerce to 10 → the bar
  draws N equal segments: a proportional claim about workload
  that no fact supports, on the Tasks plate's header.
- **Improved:** when no task in the queue carries an estimate,
  the bar stops being a proportion: it draws one tick per
  item on a ruled line, figured with the item count — a count
  strip, not a workload strip (spec §4.1: order is the
  information). The proportional form returns only for queues
  where estimates actually exist (pre-order history rows).
- **Price:** ~35 lines, BlockitTowerFadersAndCatalogViews.kt,
  1 file.
- **Partition:** Einbau.

### F-22 — Desk clock idle: the dial setting dressed as a running clock
- **Current:** with no session, the desk clock prints
  `dialMinutes × 60` as its 96sp figure with progress 0
  (AirlockAndDisarmOverlays.kt: AlwaysOnDeskClockOverlay,
  displaySecs else-branch, ~120ff) — the nightstand shows a
  time that is not elapsed, not remaining, and not the hour:
  a setting wearing a clock's face (the Small Things register
  found the same fact).
- **Improved:** §4.4's night plate states the span or nothing:
  idle, the plate prints the dial figure AS a setting —
  `SET 50 MIN` at muted tier, no dimension line, no progress
  bar — and ELAPSED / REMAINING seats stay empty until a
  session gives them facts. A labelled static figure, like
  ISSUED (§1.12): honest because it is labelled.
- **Price:** ~20 lines, AirlockAndDisarmOverlays.kt, 1 file.
- **Partition:** Einbau.

### F-23 — Shield empty lists: filtered-to-zero prints nothing at all
- **Current:** when `filteredApps` is empty the apps section
  renders zero rows and no line (ShieldMatrixScreen.kt items
  block, ~285ff) — search-miss, category-miss and no-apps are
  indistinguishable. Same disease, smaller dose: an empty
  routines list prints the header's `0 ACTIVE` and silence.
- **Improved:** one ruled empty row in each seat, Anonymous
  Pro muted: `NO APPS MATCH — "<query>"` (query figured as
  written) or `NO APPS LISTED` when the source list itself is
  empty; `NO ROUTINES SET` under the day-scale. A drawing with
  no items is honest white space WITH its caption (§4.1's
  rule, extended to Shield).
- **Price:** ~20 lines, ShieldMatrixScreen.kt, 1 file.
- **Partition:** Einbau.

### F-24 — Drawer text verbs: BACK / EARLIER / day-words at 15–30dp live height
- **Current:** DrawerNavRow is 12sp text + 6dp vertical
  padding ≈ 30dp (DrawerScreen.kt DrawerNavRow); EARLIER's
  row pads 10dp around 11sp ≈ 35dp; the day-word declarations
  (REST / ILL / FAMILY) are bare 11sp clickable Text with no
  padding at all ≈ 15dp live (DrawerScreen.kt DayHeading) —
  for a declaration he may make ill, in bed, one-handed.
- **Improved:** every Drawer verb cell min 48dp tall: BACK and
  EARLIER rows padded to it, day-words as three margin cells
  48dp tall with the active word's cell ruled at working
  weight. Print sizes unchanged.
- **Price:** ~20 lines, DrawerScreen.kt, 1 file.
- **Partition:** Einbau.

### F-25 — PARK sound sled: 76×34dp live bounds for a 34dp drawing
- **Current:** PARK hosts the binary sled at exactly
  `width(76.dp) / height(34.dp)` (ParkDrawer.kt sound row,
  ~440ff) and SledSwitchView's own track is 76×34 with only
  ~32dp of travel (BlockitSuitcaseAndSwitchesViews.kt:114–137:
  trackW 76, sledW ≈ 37.8, maxTravel ≈ 32.2dp). The throw is
  short and the target is short; the row around it is 52dp
  tall and dead.
- **Improved:** the sled's DRAWING stays 76×34 (chrome grammar);
  its touch cell in PARK grows to the row's full 52dp height ×
  92dp width (transparent hit extension in the host Box, the
  overlay pattern PARK already uses for its sealed state).
  Travel stays 32dp — a binary throw is a detent, not a
  dimension; the cure is aim, not distance.
- **Price:** ~10 lines, ParkDrawer.kt, 1 file.
- **Partition:** Monteur.

---

## WHAT I WOULD DO NEXT (for Riss)
1. F-01, F-02, F-05, F-06 ship first — four small diffs that
   remove the only silent tier-change, the worst-seated
   shorthand, and the two most-pressed undersized verbs.
2. F-19 gates Einbau's day-scale build; F-14 gates Monteur's
   title block. Both are rules that cost ~nothing now and a
   re-render later.
3. F-18 before F-20, and F-20 not forgotten when F-18 lands.

*— Feile, Improver, Blueprint crew — Oct 29, 2026.*

---

## SUPPLEMENT — DESIGN-zirkel.md landed mid-run (read whole, same run)

*Correction to this file's header: DESIGN-zirkel.md landed at
~22:32 IST while this pass was being written (it was absent at
seating and at first check — the header's statement was true
when written and is stale now). I read the contract whole
before closing. Where it settles what I priced, I say so;
where we disagree, the disagreement is Riss's to rule, stated
here, not smoothed over.*

- **F-10 (FLOW ticks sink): SETTLED BY CONTRACT.** Zirkel §4.1
  adopts it verbatim (ticks to hairline under FLOW). Delta
  stands, now contract-backed; Ausbau builds it once.
- **F-05 (DONE): FORM SETTLED, SIZE STILL OWED.** Zirkel §5.3
  settles DONE's dress exactly as F-05 proposed (outline key,
  solid only while pressed, 15sp). It sets no key size — a
  15/20sp label in an unpadded outline key is a ~22dp target.
  F-05's 64×48dp cell stands as the size amendment to §5.3.
- **F-14 (title block): AMENDED — Zirkel's geometry beats my
  two-row invention; my floor survives as a HIT-AREA rule.**
  The contract settles the block at 64dp: Tier B 32dp over
  Tier A (index) 32dp, five equal-flex cells, "hit area: the
  full cell, 32dp tall minimum" (§3.3). Widths acquit
  themselves (at a 390dp plate the block spans ~368dp → cells
  ~73dp ≥ my 64dp floor). Height does not: 32dp is called
  "furniture-sized" in the contract, but this app's own
  precedent prices furniture touch at 48dp — PARK's grip strip
  is 32dp visual / 48dp touch (ParkDrawer.kt, grab strip) and
  its FIX key is 48dp. Amended delta: drawn geometry stands
  exactly as Zirkel sets it; Tier A's HIT rect grows to 48dp —
  8dp up into Tier B's lower margin (Tier B is print, no
  control lives there) and 8dp down across the block's bottom
  rule into the frame margin (dead space to the plate edge).
  No render changes; ~10 lines in the title-block host,
  Monteur. Sub-rule for narrow plates: where a cell falls
  below 64dp wide (plate ≤ ~342dp), the index cells drop
  their `0n ` prefix — the number already lives in Tier B's
  identity cell; DECK / SHIELD / TASKS / LEDGER / PARK stand
  alone rather than shrinking the type.
- **F-13 (slide words fade): DISAGREEMENT, routed to Riss.**
  Zirkel §5.1 keeps the trunk fade for hand-drags ("fade per
  trunk R5"). My delta stands against it, one fact: the fade
  reaches zero at displayed 0.77, the catch is at 0.84 — the
  plate's words are gone before the hand reaches the decision
  point. If Riss rules for the contract, F-13 dies by that
  ruling and I record it; until then it is not withdrawn.
- **F-02 / F-03 (slide tap): UNTOUCHED by the contract** —
  §5 prices the plate's dress and states (64dp plate, cap
  84dp) and keeps trunk machinery; the tap's hit-test seat is
  machinery-adjacent presentation the contract does not
  re-argue. Both deltas stand as filed. (One note for Ausbau:
  the contract's plate is 64dp where the trunk host reserves
  72dp, TactileDeckScreen.kt:892 — the whole-plate tap rect
  follows the plate, not the zone.)
- **F-19 (day-scale sliver rule): UNTOUCHED — the contract is
  silent on the scale's segment geometry.** Zirkel settles
  tokens, frame, title block, figure, commit, ramp, and the
  §7 supplement (NEXT note, figured PEEK/BREAK fields — which
  corroborate F-18/F-20's instrument for lengths). The Shield
  day-scale itself carries no geometry in the contract, so
  the sliver rule stands exactly as filed and still gates
  Einbau's first scale build.
- **F-07 (Guard tap): MADE CONDITIONAL.** Zirkel §3.5 draws
  the Guard margin note as print between rules and states no
  tap; the trunk's tap-to-PARK may die with the header band by
  design. F-07 applies only if Riss rules the note keeps a
  tap; if the note is print-only, F-07 withdraws and the
  index's PARK cell is the only door, as §3.3 sets it.
- **F-18 / F-20 (editor): CORROBORATED in instrument.** §7.2's
  Figured Field (tap the figure, step 1/5/15 or keypad, pitch
  = f(value)) is the editor instrument F-20 priced for routine
  times; F-18's step set matches the contract's. Both stand.

*— Feile, supplement, Oct 29, 2026 (post-Zirkel).*
