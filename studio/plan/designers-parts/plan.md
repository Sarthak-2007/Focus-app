# PLAN — Designers, IA seat · SHIELD composition spec
Filed: 29 Oct 2026 · Coordinator: Sana · NO CODE — composition only
Locked laws honoured throughout: margin 20dp · spacing 4/8/12/20/32/48 only ·
radius 8/16 · type ramp heroes 44–112 / titles 18–26 / body 13–15 / labels 9–11 ·
night staircase ground #171412, cap #4A423C, text #D2C6BB / #9C8E84 / #776A60 ·
accent = COMMIT fill only → **Shield accent = ZERO, everywhere, no exceptions** ·
one fact, one rendering · ARM ≤10s · HOLD absolute · TRUTH once.

## THE ONE HERO — named
**The routines TIME COLUMN is Shield's hero.** Not a number: a column of start
times running down a locked axis, the day drawn as a schedule he can read in one
glance. Everything else on Shield is a list in service of that column. There is
no hero numeral on Shield and no second hero object; the Section strip is the
column's shadow, not its rival (same facts, drawn once as elevation — the strip
is registration, not restatement: it carries position-in-day, the rows carry
everything else; see §3 for the fact split).

## SCREEN MAP — top to bottom (single scroll column, margin 20dp both sides)
1. Screen title zone — 20dp top inset
2. **ROUTINES — the time column (hero)** — rows registered to §3's strip
3. Section strip — 24h elevation, directly under the rows, same axis
4. 32dp section break
5. **APPS & SITES — the list module** — sticky search, grouped rows
6. Verb-code legend — printed once, at the foot of the list module
7. 48dp break → RECORD section (specced in LEDGER-designers.md — not this file)
8. Bottom scroll inset 104dp (clears the band switch)

---

## §1 SCREEN TITLE ZONE
- Position: top, margin 20dp. Height: 32dp content + 20dp top inset.
- `SHIELD` — title tier, Sora 20sp, primary tone. Day: ink on day ground.
  Night: #D2C6BB on #171412.
- Right-aligned on the same baseline, tertiary label: `3 ROUTINES` — 10sp
  Anonymous Pro Bold, +2.0 tracking. Night #776A60. (A count of what is below,
  stated once, here — never repeated in chrome.)
- Spacing below title zone to first routine row: 20dp.

## §2 ROUTINES — THE TIME COLUMN (HERO)
### 2.1 The axis
- **Locked left axis: 56dp wide**, starting at the 20dp margin. Every start
  time in the column sits on this axis, right-aligned to its right edge.
  The axis never moves, never resizes, in any state (empty, live, conflict).
- Axis type: start time `05:00` — Anonymous Pro Bold 15sp (body tier, mono so
  the column aligns as figures, not words). Primary tone: day ink / night
  #D2C6BB. An OFF routine's time drops to tertiary (night #776A60) — the
  routine is asleep, its hour whispers.
- A 1dp vertical hairline runs the full height of the column at axis-right
  + 12dp, tertiary hairline tone (night: #4A423C at full presence — it is a
  drawn rail, the schedule's spine). Rows hang off this rail.
### 2.2 The routine row — grid
Row height: **72dp** per routine. Row internal grid (left → right):
- [0–56dp] start-time axis (above).
- [axis+12dp hairline] then content block begins at 80dp from screen left
  (20 margin + 56 axis + 4 gap accounted in the rail offset; content start is
  a fixed 80dp, never recomputed).
- Content block, three lines, baselines on the spacing tokens:
  - **Line 1 — NAME (the title):** Sora SemiBold 18sp (title tier floor),
    primary tone. e.g. `FASTED BLOCK`.
  - **Line 2 — META (one line, one rendering):** Atkinson 13sp, secondary
    tone (night #9C8E84). Format, fixed order, middot-separated:
    `DAILY · STUDY · 3H 00M` — days · loadout · **DURATION printed**.
    Duration is stated here and nowhere else on Shield (not in the strip,
    not in NEXT). Loadout word is the fixed verb-code noun (STUDY / SLEEP).
  - **Line 3 — NEXT / LIVE line:** Anonymous Pro 11sp, +1.0 tracking,
    secondary tone. Exactly one of:
    - `NEXT — TODAY 05:00` / `NEXT — TOMORROW 09:00`
    - `WINDOW OPEN — ENDS 08:00` (the live state; the word LIVE is never
      used — a routine window is not a session and the copy does not pretend
      otherwise)
    - `OFF` is NOT printed here — off-ness is the sled's job (§2.4); this
      line for an off routine prints `— PAUSED` only if auto-paused by
      attendance, else the line is absent (absence, not a zero-state word).
  - **Conflict line (replaces Line 3 when present, never adds a 4th line):**
    same type slot, tertiary→secondary weight shift, plain statement on the
    row itself: `HELD — MANUAL SESSION RUNNING` · `OVERLAPS DAY BLOCKS
    09:00–09:30` · `MISSED ×3 — AUTO-PAUSED`. A conflict is never a badge,
    never orange, never in chrome. It lives on the row it accuses.
- Row separators: 1dp hairline, tertiary, inset to the content block start
  (80dp) — the axis column is never cut by separators; time is continuous.
- Row tap target: the full 72dp row (minus the sled, §2.4) opens the routine
  editor. 48dp minimum honoured with 24dp to spare.
### 2.3 The reseeds — his real day (exact, migrating only rows that match the old seeds exactly; user-edited rows untouched)
| Start (axis) | Name (18sp) | Meta (13sp) |
|---|---|---|
| `05:00` | FASTED BLOCK | `DAILY · STUDY · 3H 00M` |
| `09:00` | DAY BLOCKS | `DAILY · STUDY · 10H 30M` |
| `20:00` | SLEEP | `DAILY · SLEEP · 7H 30M` |
- SLEEP's wrap past midnight is one routine, one row: its NEXT line reads
  `WINDOW OPEN — ENDS 03:30` in-window; the end time lives only in that line,
  the meta duration (7H 30M) already carries the span — end time is never
  printed a second time as a range in the row body.
- Row order: strictly chronological by start time. A new routine inserts by
  its hour, never appended at the foot like a settings item.
### 2.4 The on/off — miniature sled (position + margin word)
- Position: right end of the row, vertically centred on Line 1–2 block.
  Size: **36dp wide × 20dp high** track, radius 8dp (locked radius; no pill
  invention beyond the sled's own drawn form — the sled is a machine part,
  not a chip). Cap: 16dp × 16dp, inset 2dp.
- State grammar (locked): **position = power.** Cap right + track filled one
  staircase step up (night: cap #4A423C track→ one step above ground, cap in
  primary text tone #D2C6BB) = ON. Cap left + track at ground step = OFF.
- **Margin word:** the state word sits OUTSIDE the sled, at its right margin
  — `ON` / `OFF`, Anonymous Pro Bold 10sp, +2.0 tracking, secondary tone,
  flips **on arrival** of the cap, never mid-flight. Position and word are
  the two channels; colour is never one of them. Zero accent in either state.
- Throw: 240ms spring (cohort law), answers on DOWN, haptic = over-centre
  family (ON sharper / OFF duller).
### 2.5 Create / duplicate — the front door
- **Create:** a full-width text row directly under the last routine row,
  same 80dp content inset, 52dp high: `+ NEW ROUTINE` — 13sp Atkinson,
  secondary tone, the `+` in primary. Not a floating button, not orange,
  not a card. It is a line in the schedule, because adding a routine is
  editing the day.
- **Duplicate:** in the routine editor (row tap), as a text action beside
  DELETE: `DUPLICATE`. The duplicate lands in the column at its (edited)
  hour with name suffixed `— COPY` until renamed. No long-press hidden
  gestures for either path — the flagship feature's door is printed.

## §3 THE SECTION STRIP — 24h elevation, registered to the rows
- Position: directly under the last routine row, 20dp below it. Full content
  width (margin 20dp both sides). Height: **28dp** total — 20dp bar field +
  8dp hour ticks below.
- Registration: the strip's horizontal axis is **time of day 00:00–24:00**;
  its blocks are the same routines as §2's rows, in the same left-to-right
  chronological truth as the rows' top-to-bottom order. A block's width =
  duration at true scale (content width ÷ 24h per hour). SLEEP's block wraps:
  drawn as two segments (20:00→edge, edge→03:30) joined by continuation
  marks — one routine, visibly one object.
- Rendering — **ink only, zero accent:** ON routine = filled block one
  staircase step above ground with primary-tone presence; OFF = hairline
  outline only; auto-paused/ghost-prone = dashed outline (dashed = off-family,
  per the settled reading: blocks = routines, dashed = off). Night fills:
  block #4A423C-family step, outline #776A60.
- **Now-line:** 1dp vertical line at the current hour, **ink** (primary text
  tone, night #D2C6BB), full 20dp bar height + 4dp tick below. It is a line,
  not a lamp. It names nothing; the row it touches already says WINDOW OPEN.
- Fact split (one fact, one rendering): the strip carries ONLY
  position-and-span-in-day. Names, days, durations, states live in the rows
  and are NOT reprinted in or under the strip — no labels inside blocks.
- Hour ticks: `00 06 12 18 24` only, 9sp Anonymous Pro (label floor),
  tertiary tone, under their true positions.

## §4 APPS & SITES — the list module (below the strip, 32dp break)
### 4.1 Section head + search
- Head: `APPS & SITES` — 10sp label tier, tertiary, + a right-aligned count
  line in the same register: `12 SHUT · 3 PEEK` (OPEN count omitted — OPEN is
  the default of the world, not a Monolith fact worth ink; SHUT/PEEK are the
  decisions). Counts stated once, here.
- **Search: sticky, 36dp high**, pinned under the title zone when the list
  scrolls. Field: ground-step fill, radius 8dp, `SEARCH` placeholder 13sp
  tertiary Atkinson, no icon drawn (the word is the affordance), clear = the
  typed text selects-all on long press — no × chip furniture.
### 4.2 Grouping — by state, in enforcement order
- Groups in fixed order: **SHUT → PEEK → OPEN**. Group header: 10sp label
  tier, tertiary, +2.0 tracking, 28dp high, sticky within the list scroll
  under the search.
- **OPEN group is COLLAPSED by default** — header row only, with its count
  (`OPEN — 41`). OPEN apps are the un-decided mass; they earn one line, not
  forty rows. Expanding is one tap on the header; collapse state persists.
- SHUT and PEEK groups are expanded always (they are the decisions — few,
  audited often).
### 4.3 The app row — 52dp
- Height **52dp**, hairline separator inset to text start. Left → right:
  - **Real app icon, 28dp**, radius 8dp (the app's own artwork, unmodified —
    he scans icons, nobody scans an alphabetical document).
  - **Name:** Atkinson 15sp (body tier top), primary tone, one line, 12dp
    from icon.
  - **State sled:** same miniature sled as §2.4 but **three-position**
    (OPEN / PEEK / SHUT — cap position across the 36dp track IS the state),
    right-aligned, with the **margin word** at its right in the same 10sp
    register: `SHUT` / `PEEK` / `OPEN`. Word flips on arrival only.
  - Verb-code fills (the legend in §5 is the key): SHUT = full ink fill of
    the cap zone; PEEK = mid-grey `#615B5A` register (day token; night:
    one staircase step below SHUT's presence, same hue family — tone, never
    a second colour); OPEN = porcelain/ground (night: ground step, hairline
    outline). No green anywhere (his standing colour ban), no orange.
- Row tap (outside the sled): no-op by design — the sled is the row's only
  verb; a row that also opens something teaches two grammars.
### 4.4 Blocked websites — same list module, same rows
- Websites are rows in the SAME groups, interleaved by state, not a separate
  screen, section, or app: a site row = 52dp, favicon-or-globe glyph plate
  28dp (drawn plate, radius 8dp, tertiary glyph — sites have no real icon to
  honour), domain as the name line (`x.com`), and beneath it in 11sp tertiary
  the single canonical domain only — **duplicate domain spellings are one
  row** (`twitter.com`/`x.com` collapse to one row, aliases listed in the
  editor, never as two rows).
- Add-site: one text line at the foot of the list module, same grammar as
  §2.5: `+ ADD SITE` 13sp secondary. Editor carries REMOVE as a text action.
- A site's sled is two-position only (SHUT / OPEN — there is no PEEK for a
  domain); the track greys its middle detent. Same part, honest range.
### 4.5 Furniture budget
- **Furniture ≤ 35% of the first viewport.** First viewport contains: title
  zone + all three routine rows + the Section strip + list head/search —
  the apps themselves begin at or below ~65% of the first screenful. If a
  build's first viewport is more than a third chrome/heads/search, it fails
  this spec regardless of how it looks in a still.

## §5 VERB-CODE LEGEND — printed once, at the foot of the list module
- Position: under the last list row (or the collapsed OPEN header), 20dp
  below, above the 48dp RECORD break. **Printed exactly once in the app** —
  Shield's foot is its only home; it is never repeated on Deck, Tasks, or in
  any editor.
- Form: one line-set, 10sp Anonymous Pro, +1.5 tracking, tertiary tone,
  swatches drawn as 12dp sled-cap squares (radius 8dp system — caps, not
  pills):
  `■ INK = SHUT · ■ MID = PEEK · □ OPEN · — ORANGE = COMMIT (DECK · TASKS ONLY)`
- The last clause is printed because its absence on this screen needs a
  reason a tired eye can find: orange is not missing from Shield by
  oversight; it is not this screen's verb.

## §6 DAY / NIGHT TOKEN MAP (summary, all elements above)
| Element | Day | Night (locked staircase) |
|---|---|---|
| Ground | day ground | #171412 |
| Cap / sled ON fill step | one step up, day ladder | #4A423C |
| Primary text (names, axis, hero column) | day ink | #D2C6BB |
| Secondary text (meta, NEXT, words) | day secondary | #9C8E84 |
| Tertiary (heads, ticks, conflicts' base, legend) | day tertiary | #776A60 |
| Hairlines / rail | day hairline | #4A423C register |
| Accent | none | none — zero, both modes |
Type is mode-invariant: Sora (names/titles) · Atkinson (body/meta) ·
Anonymous Pro Bold (axis, labels, words, legend). Nothing on Shield uses a
heroic size — the column's rhythm is the hero, not a digit.

— Plan, Designers (IA seat), for Sana
