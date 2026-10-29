# LEDGER EXAMINATION — Fader (Improvers, loadout band / tonal structures)

**Elements assigned:** the streak tower · the 7-day columns
**Question, in Sarthak's words:** *"run ledger through agents, im not sure if we should keep or not."*
**Scope:** plan wave — examination and peak specs only, no code.
**Sources read first-hand:** `CurioVaultScreen.kt`, `BlockitTowerFadersAndCatalogViews.kt`
(`TowerStreakView`, `DayFadersView`), `MonolithRepository.compute7DayHistory()`,
renders `shots/04_vault.png` (day) and `shots/night/n04_vault.png` (night).

**Verdicts up front:**

| Element | Verdict |
|---|---|
| Streak tower | **DEAD — at any altitude.** The element itself, not its execution. Its underlying fact survives as one line of type. |
| 7-day columns | **KEEP-AT-PEAK — home: the rebuilt Ledger tab only**, rebuilt as a printed-scale column chart of *sealed* minutes, absence rendered as absence. |

---

## 0. The binding fact, stated exactly: what Monolith knows that YPT doesn't

YPT tracks **study**: a timer he runs, wherever he studies, however he studies.
Monolith can only ever track **what happened under its own seal**: sessions this app
armed and held, minutes elapsed inside them, deflections absorbed during them,
routines kept or missed. Those are different facts that often describe the same hours.

Applied to my two elements:

- **The columns duplicate** the *shape* of YPT's week — seven days, minutes per day.
  On a day he studies entirely under seal, the column and YPT's day are the same
  number wearing two coats. **What the columns uniquely add:** the minutes are
  *sealed minutes only* — study that happened with the phone held, under a loadout,
  with deflections counted. They answer "how much of my week did the machine
  actually hold?" — a question YPT cannot ask, because YPT never held anything.
- **The tower duplicates** a streak concept that also lives in YPT's world — and, as
  shipped, it doesn't even count that. See §1.1: its bricks are not days at all.

That distinction runs both verdicts. The columns have a unique fact and a broken
rendering. The tower has a borrowed fact and a rendering whose metaphor forbids
the truth.

Standards applied throughout: **TRUTH once** (one fact, one rendering, human
language) · **ARM ≤10s** (neither element sits on the arm path; both are taxed
instead on the seconds they steal from screens that do) · **HOLD absolute**
(neither may demand a decision mid-session; neither may mint minutes for time
the seal didn't hold).

---

## 1. THE STREAK TOWER

### 1.1 CURRENT form, truthfully

A 196dp card. Bricks laid 7 per floor, bottom floor first, left to right. Laid
bricks are solid ink; the newest ("crown") brick is the orange accent; unlaid
slots are ghost outlines. Floor labels `F01…F04` down the left edge, a header
reading `STREAK TOWER • 7 BRICKS / FLOOR`, and a readout `FL 03 • 16 BRICKS`.
It is used twice: in the Vault, and again in an airlock/disarm overlay.

Three truths about it that the picture hides:

1. **It does not count streak days. It does not count days at all.**
   `CurioVaultScreen` computes
   `totalBricks = (totalFocusedMinutes / 25) + completedCount`, then forces
   `.coerceAtLeast(16)`. A brand-new user with zero sessions opens the Ledger
   and is shown a tower of **sixteen bricks he never laid**. The "streak tower"
   is a re-rendering of the minutes counter in brick shapes — the same fact the
   tile directly above it already prints as `TOWER BRICKS 016`, which is itself
   derived from the `FOCUSED MIN 150` tile beside that. One fact, three
   renderings, stacked within one screenful.
2. **It saturates.** The view clamps bricks to 70. Past ten floors the monument
   stops growing while the streak continues — the monument quietly resigns at
   exactly the altitude where a monument would start to mean something.
3. **It has no past and no break.** There is no date on any brick, no mapping
   from brick to day, no state for a missed day. Tap a brick: a haptic fires and
   a callback receives the total count. The bricks are not addressable because
   there is nothing in them to address.

### 1.2 The beauty judgement, honestly — his axis

**It is not beautiful. It is a chart wearing a monument's clothes.**

Look at the day render without the label: seven wide, uniformly rounded
rectangles per row, four rows, one cell lit orange. The eye reads a calendar
grid, a chocolate bar, a spreadsheet selection — anything except a tower.
A tower is *vertical, narrow, and heavy at the base*; this object is wider
than it is tall, every "brick" is the same weightless rounded slab, there is
no mortar, no offset bond, no shadow, no base, no sky. The `F01–F04` labels
down the side complete the spreadsheet reading — those are row headers.
The orange crown brick doesn't read as a crown; it reads as the *selected
cell*. At night it's worse: cream slabs floating on a brown card, the unlaid
floor rendered as black voids that look like missing data, not unbuilt future.

Nothing in the geometry earns the word "tower." Rename it `GRID` and not one
pixel would need to change. That is the definition of a chart in costume.

### 1.3 The TRUTH test it cannot pass

The brief's question, answered directly: **on the day the streak breaks, this
element does nothing — and every honest thing it could do destroys it.**

- **Ignore the break** (current behaviour — forced, since bricks derive from
  cumulative minutes, which never decrease): the monument lies by standing.
  A streak record that cannot register a break is not a record.
- **Demolish / reset to zero:** now the element *accuses*. One sick day erases
  the visible evidence of a hundred held days. The record says the hundred
  days are gone; they aren't — he lived them, YPT holds their hours. A
  rendering that deletes true history to dramatise a gap fails TRUTH once
  from the other side.
- **Crack the top floor:** legible for exactly one break. Break twice and the
  visual grammar needs cracks on cracks; by month six the tower is a damage
  report, and reading "how am I doing" requires archaeology.

This is not an execution flaw. **Accumulation-only is the metaphor.** A tower
is the wrong shape for a fact that can go backwards, in the same way a
thermometer is the wrong shape for a fact that can't. No altitude fixes a
metaphor.

Add HOLD: ghost slots pre-draw the future as empty sockets waiting to be
filled — every unlaid brick is a small standing demand. And add the overlay
reuse: the tower also appears in the airlock path, where a monument to
accumulated virtue has no business standing between a man and a decision
about *this* session.

### 1.4 PEAK — the highest form this element can take

Specced in good faith, because a verdict against a straw peak is worthless:

- **Redefine the unit honestly:** one brick = one *sealed day* (≥1 completed
  sealed session that day). Nothing else mints a brick. No floor of 16 free
  bricks; day one shows one brick on a foundation line.
- **Geometry:** abandon the wide grid. A true vertical section, 76dp wide,
  full Ledger height, left-anchored: running bond (alternate floors offset
  half a brick), brick 34×13dp, 2dp mortar gaps in the ground tone, a 4dp
  foundation plinth in the darkest ink, floors separated every 7 bricks by a
  hairline course line with the week's start date set in Anonymous Pro 8.5sp
  in the margin.
- **Tones:** bricks in a three-step ink staircase cycled deterministically
  by course (day: `#252323 / #3A3633 / #4E4948` on ground `#E4DFDA`; night:
  `#FFE4CF / #D2C6BB / #B8A99C` on ground `#171412`) — tone carries *course*,
  never value; the newest brick is solid accent `#F55314` (day) / `#F47442`
  (night) for its first day only, then settles into the staircase.
- **Break behaviour:** the unfinished course stays unfinished — permanently.
  The next sealed day does **not** fill the gap and does not restart at
  ground level; it starts a **new course above a 6dp shadow gap** — a visible
  strata break, with the restart date printed in the margin. The tower keeps
  its scars in section, the way geology does. Nothing falls, nothing is erased.
- **Home:** Ledger tab only. Removed from the airlock overlay at any altitude.

**Why the peak still dies — three structural failures no spec removes:**

1. **Scale.** At one brick per day, day 100 is 15 courses ≈ 240dp of tower;
   day 186 (NEET) is ~27 courses. The peak must then either compress bricks
   (the printed scale lies — the exact sin the wave-1 spec executed the
   faders for), scroll (a monument you scroll is a list wearing the costume
   again), or cap (saturation — the shipped sin, kept). Every exit is a lie
   or a costume change.
2. **The strata-break is truthful and unreadable.** After three breaks the
   section needs a legend to parse. An element that needs a legend to say
   "you stopped and started again" has lost to the sentence that says it.
3. **Its fact fits in one line of type, completely.** `SEALED DAYS IN A ROW — 12`
   (Anonymous Pro, Ledger header) loses *nothing* the tower knows: not the
   count, not the break (the number simply restarts, and yesterday's receipts
   still show the run that ended), not the history (receipts hold it). What
   the tower adds over that line is only *size* — and size purchased with
   196dp, a saturating scale, and a metaphor that must lie, accuse, or
   compress. Beauty is retention is a workshop law I cite as evidence: this
   object, honestly looked at, is not the beautiful thing on its screen —
   the receipts below it, plain rows of kept sessions, are closer.

### 1.5 VERDICT — TOWER: **DEAD, at any altitude**

Kill the view, both homes (Ledger + airlock overlay). The streak fact survives
as one mono line in the rebuilt Ledger's header block, beside the hero stat —
one fact, one rendering. The 196dp it vacates goes to the columns' printed
scale and to receipts that no longer need truncating at eight.

### 1.6 THE STRONGEST CASE AGAINST MY VERDICT — written to win

*The tower is the only element in this app that thinks in months, and you are
killing it for telling the truth badly instead of letting it tell the truth
well. Everything else Monolith owns is amnesiac: columns forget last week,
receipts forget last month, the wheel forgets yesterday. A NEET drop year is
186 days; the single most valuable thing an object can do for a man on day
100 of 186 is make days 1–99 physically present in the room — stacked,
weighty, undeniable — on the morning his motivation is gone. A line of type
cannot do that; nobody ever screenshotted a line of type, nobody ever felt
a number in their stomach. Your own peak spec proves the break problem is
solved: the strata gap records a break without erasing or accusing — it is
exactly how real structures carry damage, and your "unreadable after three
breaks" is a designer's guess about a user who will read his own tower the
way he reads his own handwriting: fluently, because it's his. The scale
problem is equally overstated — a tower that outgrows its card and becomes
a rising silhouette behind the Ledger, cropped by the screen's edge, is not
lying about scale; it's being a tower, which is the one object in the world
nobody demands fit in a frame. And the retention evidence cuts against you:
streak monuments are the most proven retention mechanism in consumer
software precisely because loss is visible — you call that accusation, but
he has asked, repeatedly and in writing, to be held with urgency and no
comfort. You are protecting him from a standard he set. DEAD is the coward's
verdict here: the honest one is KEEP-AT-PEAK and let the scarred tower stand.*

I have read that at full strength. It fails on one point it cannot answer:
the peak it defends must crop, compress, or scroll by day ~60 — before the
day-100 morning it exists for — and a monument whose scale rule changes
mid-life is three different objects pretending to be one. The line of type
never changes its rule. The tower dies on arithmetic, not on taste.

---

## 2. THE 7-DAY COLUMNS

### 2.1 CURRENT form, truthfully

`DayFadersView`: seven vertical tubes, one per day, MON–SUN style labels,
minute readout in mono above each tube (`50m`), a recessed track, an inner
fill rising from the bottom, a horizontal notch near the fill's top edge —
the fader-cap gesture — and the selected day's fill turned orange. Tapping
a column selects it; selection changes the header counter (`DAY 07/07`) and
nothing else anywhere on the screen.

Truth audit — the rendering lies in three places, all in the code:

1. **A zero day draws a fill.** `frac` is clamped `.coerceIn(0.12f, 1f)` —
   a day with 0 sealed minutes renders a 12%-height stub bar. The day render
   proves it: four consecutive `0m` days, four black stubs sitting in their
   tubes. Absence is rendered as a small presence. This is the single worst
   lie in the Ledger, because it lies in the *kind* direction — it softens —
   and a record that softens cannot be trusted when it flatters either.
2. **There is no scale.** Heights are normalised to *this week's maximum*
   (floored at 60m). The tallest column is always ~full height whether it
   holds 75 minutes or 7 hours. The same column height means a different
   number every week; comparing this week to last week by eye — the one
   thing a column chart exists for — is impossible.
3. **The fader metaphor is borrowed and unearned.** A fader is a control:
   it slides, and sliding it *does* something. These don't slide and do
   nothing; the notch on the fill is a picture of a cap on a picture of a
   fader. Worse, selection-by-orange rewrites the selected day's *magnitude
   colour*, so the act of inspecting a value changes its visual weight
   against its neighbours — the chart corrupts itself when touched.
   And if `sevenDayStats` ever arrives short, the view silently draws a
   **hard-coded fictional week** (45/75/60/90/50/110/85m) rather than nothing.
   A fallback that invents a good week is disqualifying until removed.

What the current form gets right — and why this element lives where the
tower dies: the *fact* is real (`compute7DayHistory` sums actual session
elapsed time per calendar day), the window is human (a week is the unit he
plans in), the reading time is ~2 seconds, and a column, unlike a tower,
has a native grammar for going down: shorter is just shorter. The metaphor
can tell the truth. The execution currently doesn't.

**Does the drained column accuse or record?** As shipped: neither — it
*flatters*, via the 12% stub. The accusation risk lives in framing devices
(target lines, red, down-arrows, "0m" rendered as a verdict), not in the
height itself. A short column in the same ink, on the same scale, with no
goal line drawn across it, records. That is specifiable. See §2.2.

### 2.2 PEAK — the column chart this element can be

**Name and header (the truth label, non-negotiable):**
`7 DAYS — SEALED ONLY` (Atkinson Hyperlegible Bold, 11sp, tracked), right
side: the selected day's date in Anonymous Pro. The columns never claim to
be his study week; YPT owns that record. These are the minutes Monolith held.

**Geometry:**
- Plot height **120dp**, seven columns **32dp wide**, **12dp gaps** (the
  app's universal gap — no new spacing values), a shared **baseline** at the
  foot: 1.5dp solid ink rule, the chart's foundation. Columns sit *on* the
  baseline; nothing floats.
- **Printed scale, fixed forever:** 0–12H, gridlines at 0 / 3H / 6H / 9H /
  12H — hairlines (`#615B5A` at 45% day / outline-variant night) spanning
  the plot, numerals in Anonymous Pro 9sp in a 30dp right rail (`12H`,
  `9H`, `6H`, `3H`, `0`). The scale never rescales, never auto-zooms, has
  **no target line**: 12H is the top of the ruler, not a demand drawn
  across his week. A 50-minute day is a short column. That is what 50
  minutes looks like against a day. It is a measurement, not a grade.
- Column radius 6dp top corners only, square at the baseline — a column is
  built from its foot, not a pill suspended in a tube. **The tube dies**:
  no recessed track, no fader notch, no cap line. Nothing of the fader
  survives except the lesson of my own loadout band: a level is read from
  where a solid thing ends against a printed scale — not from a tube.
...[truncated 7943 chars]
- **Zero days draw nothing.** A day with no sealed minutes gets the
  baseline and its day letter — no column, no stub, no placeholder. The
  gap in the row *is* the record of that day. (This is lie № 1 of §2.1,
  answered in the form itself: absence renders as absence.)
- **A short feed draws a short chart.** If `sevenDayStats` holds three
  days, three columns stand. The hard-coded fictional week is removed,
  not replaced — a chart that invents one good week can never be
  believed about a bad one.
- **Selection is by form, never by colour.** Tapping a column selects
  the day: its label takes full ink weight and the header's right side
  prints the date with the exact figure in Anonymous Pro —
  `TUE 28 OCT · 4H 10M SEALED`. The column itself does not change
  colour, weight, or height: inspecting a value must never alter its
  visual magnitude against its neighbours (lie № 3, answered). The tap
  indexes into that day's receipts — the same session rows the chart
  sums, per the binding condition with Linea: one pipeline, or the
  index can contradict its own list.
- **Sound:** one detent on selection. Nothing else in this element
  sounds, ever — a record does not applaud or sigh.
- **Night:** columns in outline-variant ink on the identical scale;
  selection still reads by label weight alone. Same ruler, same heights,
  no second grammar after dark.
- **Reading time:** the week in ~2 seconds — the shape first (one tall
  day, one gap), the number only when a column is touched. A chart he
  must study is a chart that has failed; this one is read the way a
  level is read, at a glance, against a scale that never moves.

### 2.3 VERDICT — the columns: KEEP-AT-PEAK

**Formal verdict: option (a) for this element — the 7-day columns
survive at the §2.2 peak, and their only lawful home is the rebuilt
Ledger tab.**

The tower (§1) I killed with my own arithmetic, and I do not resurrect
it. The columns are the opposite case: every lie in their current form
is an execution lie, not a fact lie — the minutes are real, the window
is the unit he plans in, and a column has a native grammar for going
down. Fixed scale, true zeros, selection by form, no invented weeks:
the element at peak is a measurement instrument, and it is the one
element in this examination whose peak genuinely requires the tab.
Demote it to Shield or chrome and it does not shrink — it becomes a
different, worse element: a number without its scale, a week without
its shape.

This verdict is filed as the dissent against my own cohort's option
(b). I state it at full strength because the choice it defines is the
only real choice in this examination: if the sealed week, honestly
scaled, is worth a destination to him, the tab is rebuilt around this
chart and nothing else returns. If it is not, (b) is complete and my
columns die with the tab — by his word, not by my arithmetic.

### 2.4 THE STRONGEST CASE AGAINST MY OWN VERDICT

Written to win, because a verdict that cannot state its opposite has
not earned its signature:

**He may never open it.** The Humans' evidence is brutal on this
point: a destination-record is opened ~once, ever, and in motion —
across every seam of his actual weeks — zero times. My chart's entire
defence assumes a Sunday-review altitude where a man sits with his
week. If his real behaviour is glance-only, I have specced a museum
with honest labels: true scale, true zeros, and no visitor. A museum
that tells the truth is still a museum.

**The week already has an owner.** YPT holds his study record; this
chart holds the seal's minutes — different numbers, same rhythm, same
seven-day shape on a screen. Two weekly silhouettes in two apps invite
exactly the comparison the truth standard forbids each of them to make
alone, and on the mornings they disagree (a sealed hour that was not
study; a study hour never sealed) the chart I built to be believed
becomes the second witness at an argument it cannot settle.

**One line carries the fact at the altitude he lives.** Band's weekly
line on Shield prints the seal's account where his eyes already are,
inside the surface he opens daily, at zero navigation cost. My chart
answers *how was the week shaped*; the line answers *what did the seal
hold* — and the second question is asked a hundred times for every
once the first is. If the record's job is to be present at the moment
of decision, demotion is not the chart's death. It is the chart's
promotion to where decisions happen.

If he reads the record whole, keep my columns and rebuild the tab
around them. If he glances — and the evidence says he glances — (b)
wins and this section is my own signature on the warrant.

---

*— Fader (loadout band), Ledger examination: tower §1, columns §2.*

---

**INTEGRITY NOTE — Phase 3 repair (Ilya, coordinator).** As filed in
Phase 1, this document ended at line 281 with a literal truncation
marker (`...[truncated 7943 chars]`) written into the file as its last
line; everything after §2.2's "tube dies" bullet was lost in the
original write despite the seat's report describing it as delivered.
The marker is retained above as the scar — this file does not pretend
the loss did not happen. The restored tail (rest of §2.2, §2.3 formal
verdict, §2.4 strongest case against) was re-appended in Phase 3 in
small append-only chunks, reconstructed strictly from Fader's verdict
table and report letter as merged in plan/LEDGER-improvers.md (§§5–6
and the cohort option) — no position above is new; each was already on
the merged record. Integrity confirmed: the file now carries both of
Fader's examinations complete (tower §1, columns §2), his verdict, and
his anti-case. Postscript the seat could not have written in Phase 1:
Sarthak's ruling — KEEP THE TAB, REBUILT — adopted the branch this
file dissented toward. The dissent is now the operative spec, and
§2.4 stands unedited as the honest price of it.

*— Ilya, Improvers coordinator, Phase 3.*
