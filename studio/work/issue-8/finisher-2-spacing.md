# FINISH — THE DRAWER, SPACING / HAIRLINES / RHYTHM (issue #8)

Finisher 2 — Spacing (issue #8 cell), for LADE.
Sources read whole: `studio/plan/AUDIT-drawer.md` (the verdict —
its §2 / §6 numbers are law here, not suggestions),
`studio/work/issue-8/designer-drawer.md` (the floor: inline MONTHS
shelf, cabinet evicted, filing zone under the search line), and
`DrawerScreen.kt` as built (every dp below is measured from that
file; line heights marked ≈ are render measures for Ren's pass —
padding, heights and gaps are exact).

My slice is only the geometry of the finished screen. No Kotlin
was edited, nothing was compiled, the cabinet's next home is not
proposed, hinted at, or implied anywhere below — issue #21 owns
it, parked. Where the cabinet's geometry appears at all, it
appears as "evicted, not cured."

---

## 1 · THE ONE LAW AND THE ONE SCALE

The audit kills one spacing system (§2, ROW SPACING): the divider
stays, the column gap inside day groups dies, rows breathe by
padding + hairline only. Measured against the code, that sentence
is bigger than it looks — **the 12dp column gap is the
LazyColumn's `verticalArrangement = spacedBy(12.dp)`, and it is
paid at every item boundary in all three Drawer columns**, on top
of whatever padding each item already owns. Almost every defect
in §3 is that one line double-owning a gap somebody else also
owns. So the finished screen runs on one law:

> **Every vertical gap has exactly one owner** — an item's own
> padding, a fixed height, a spacer, or a hairline's adjacency.
> Never two. The LazyColumn arrangement gap is 0dp everywhere in
> the Drawer, and every gap the 12dp used to fake is re-homed,
> by name, in the table in §2. A builder who only deletes the
> 12dp will collapse the masthead; a builder who only adds the
> ruled paddings on top of it will double every break. Both
> halves of the table land together or neither does.

The gallery rhythm scale (the only values the finished screen
may spend):

| Token | Value | Where it is spent |
|---|---|---|
| Page padding | 12dp horizontal | acquitted (§6) — unchanged |
| Top air | 8dp | container top only (see D-02) |
| Bottom air | 100dp | acquitted (§6) — made exact (see D-02) |
| Header band | 45dp | every view's header, BACK's home |
| Band gap | 12dp | between masthead bands only, owned once |
| Masthead lines | 44dp | search line, each filing line |
| Full-width tap rows | 48dp | shelf rows, EARLIER |
| Heading → its rows | 6dp | every heading, everywhere, one value |
| Day break | 18dp | above every day heading (ruled 6→18) |
| Section break | 24dp | above MONTHS only (see A-1) |
| Hairline | 1dp | the one divider weight, everywhere |

The masthead now rhymes: 45 header / 44 search / 44 + 44 filing.
Before the verdict those same bands were 18 / 48 / (words buried
in a heading ~200dp down the scroll). The finish is not taller —
it is re-owned (pitch accounting in §4).

---

## 2 · MASTER DELTA TABLE — CURRENT → GALLERY

### 2.1 The frame

| # | Element | Current (as built) | Gallery | Why |
|---|---|---|---|---|
| D-01 | LazyColumn `spacedBy` — list, DETAIL, (cabinet's travels out) | 12dp at every item boundary | **0dp** | The audit's killed column gap. It double-owns every gap below; killing it is what makes every other delta in this table true instead of approximate. |
| D-02 | Column outer padding | `padding(horizontal 12, vertical 8)` + `contentPadding(bottom 100)` → bottom air is really 108dp | horizontal 12 (keep), **top 8 only**, contentPadding bottom **100** | 100dp bottom is acquitted by name (§6) — so it should be 100, not 100 + the container's 8 leaking underneath it. Top air stays 8. |
| D-03 | Header band (LANDING / MONTH / SEARCH / DETAIL) | No band exists. LANDING's STORED is a bare text item ≈18dp; MONTH and DETAIL headers are bare text rows ≈17dp | **Fixed 45dp band** in every view; STORED / month label vertically centred in it | One header height across views, so the room does not jump vertically when he navigates. The band exists to give BACK a home (D-04); if Ren rules LANDING carries no BACK, the band stays 45 with STORED alone — geometry invariant either way. |

### 2.2 BACK — the worst target in the file

| # | Element | Current | Gallery | Why |
|---|---|---|---|---|
| D-04 | BACK tap target (MONTH header, DETAIL header; SEARCH gains one per §2 of the verdict) | Bare `Text` + `clickable`, zero padding: target = the glyphs, ≈17dp tall × ≈35dp wide | **45dp tall × ≥45dp wide, edge-flush**: the cell's outer edge sits at x=0 (left-side BACKs) or at the screen's right edge (DETAIL's right-side BACK) — i.e. the cell bleeds through the 12dp page padding (−12dp start offset); the **glyph keeps its 12dp inset**, so print alignment to the page line is unchanged while the target starts at the glass edge | The verdict rules ~45dp, edge-flush (Aug). Today BACK is the smallest target on a screen whose whole errand is 1 AM navigation. Glyph position must not move — only the invisible cell grows. |
| D-05 | INDEX's BACK (`DrawerNavRow`) | ≈28dp target (see D-14) | **Dies with INDEX** — not cured, not padded | A killed view's controls are not finish work. The shelf replaces the route; MONTH's BACK (D-04) is the surviving one and its parent is honestly LANDING. |

### 2.3 Search line

| # | Element | Current | Gallery | Why |
|---|---|---|---|---|
| D-06 | Search body | Capsule: 48dp tall, 12dp corner radius, fill + 1dp border box | **44dp tall, 0dp radius, no fill, no border box, one 1dp bottom rule** (the hairline system, ink at ~14%) | Ruled (§2: KILL the capsule body). 44 keeps the ≥44dp target floor while joining the masthead rhyme (44/44/44). |
| D-07 | Search text inset | Page 12dp + capsule inner `padding(horizontal 12)` = text starts **24dp** from the screen edge | Inner horizontal padding **12 → 0**; text starts at the page's own **12dp** | The capsule double-indented the only line he types on — every other line on the screen starts at 12. A horizontal double-system, same disease as D-01 in the other axis. The bottom rule spans the full content width (12 → width−12), like every hairline. |
| D-08 | Gap above search line | `spacedBy` 12 (ghost owner) | Search item owns **top padding 12** (the band gap, spent once) | Re-homing, per §1: the value survives, the second owner dies. |

### 2.4 Filing zone (new furniture — geometry specced from zero)

The zone holds today's and tomorrow's day-lines directly under
the search line (verdict §2, designer §4). It has no "current"
in the code — the words live today inside `DayHeading`, measured
in D-13 — so these are birth values, not deltas:

| # | Element | Gallery value | Why |
|---|---|---|---|
| D-09 | Each filing line | **44dp fixed height**, date left at the page line, words right; **no hairline between the two lines**; **one 1dp hairline under the zone**; gaps above/below inside the zone: **0** | The zone hangs from the search line's rule — the search rule is its top rule, so it needs no gap and no rule of its own at the top. A hairline between today and tomorrow would make each line read as a list row (the shelf-row disease, §2.6); one rule under the pair closes the zone as one object. Zone total: 44 + 44 + 1 = **89dp, constant**. |
| D-10 | Filing word targets (REST / ILL / FAMILY) | Each word is a cell **44dp tall** (the full line height) × **glyph width + 16dp** (8dp padding inside the cell on each side); cells abut — glyph-to-glyph gap stays 16dp, target-to-target gap is **0** | Designer: words separated enough that a 1 AM thumb cannot hit the neighbour. Splitting the old 16dp dead gap 8/8 into the two cells keeps the visual gap identical and makes every dp of it land on a word — a miss between words becomes impossible; only a genuinely wrong word can be hit. Last cell's right edge ends 4dp past the content line (12 page − 8 cell padding) — deliberate; edge-flush is BACK's law (D-04), not the words'. |
| D-11 | Declared filing line | **Identical 44dp geometry.** The declared word prints in full ink beside its date; the other two words disappear; the line does **not** collapse | A line that shrinks the moment he files (44 → ≈20dp of bare print) would reflow the entire scroll at the exact 1 AM moment the filing exists for. State changes must be geometry-invariant across this whole screen; this is the sharpest case. |

### 2.5 Day groups — headings and stored rows

| # | Element | Current | Gallery | Why |
|---|---|---|---|---|
| D-12 | Day heading break | `padding(top 6)` on the heading **plus** `spacedBy` 12 above the item = an accidental 18dp owned by two systems; heading type 11sp | Heading owns **top padding 18dp, alone** (D-01 has zeroed the ghost); type **11 → 10.5sp** bold caps (ruled demotion) | The verdict's 6→18 is the **total** break, not 18 on top of the column's 12 — a builder who reads it naively ships a 30dp break. After the fix the break is the same ≈18dp he already sees, but owned once, so it survives every future edit to the column. |
| D-13 | Day heading, declaration row | `Column(spacedBy 6)` + heading + (today, unsealed) a word row: bare-text targets ≈15dp tall, `spacedBy(16)` of dead air between them; heading block swells to ≈42dp on today, ≈21dp on other days | **The word row leaves the heading entirely** (it is the filing zone now, D-09/D-10). Heading is one line: top 18 + text ≈14 (10.5sp) + **bottom padding 6** = **block ≈38dp, identical on every day** | Today a heading's height depends on its state — today costs double what yesterday costs, so day boundaries read unevenly down the scroll. One line, one height, everywhere: the shelf break becomes a metronome. Bottom 6 is the single heading→rows value (see scale). |
| D-14 | `DrawerNavRow` (CURIOS door; INDEX's BACK) | Bare text + `padding(vertical 6)`: target ≈28dp tall, pitch ≈41dp with `spacedBy` | **Row killed** (cabinet evicted; INDEX killed) — the 28dp target dies with it | The joint-worst target in the file (§5) is furniture the verdict removes. Do not "finish" it with padding; do not leave the composable behind as a temptation. |
| D-15 | ALL MONTHS row | Custom row: `padding(vertical 6)` + 11sp label + chevron, ≈27dp + divider, pitch ≈40dp | **Killed** — replaced by the shelf (§2.6) | Its block (≈27dp) actually **out-cost the day heading's** (≈21dp): a corridor row stood taller than a shelf break, which is the "rows cost the same as headings" charge in its purest form. The chevron dies with it — the shelf rows carry none (designer §3.1). |
| D-16 | StoredRow | `padding(vertical 6)`, internal gap 3dp (title → meta), 1dp hairline under; pitch today ≈62dp of which the **live target is ≈49dp and 13dp is dead** (12 `spacedBy` + hairline adjacency) | Padding 6/6 **keep**, internal 3 **keep**, hairline 1 **keep**, pitch **≈50dp** (D-01 removes the 12); clickable region = the row's box, ending at the hairline — the hairline itself is the only non-live dp in the pitch | The row's construction is acquitted by name (Plan, Aug) — this delta touches nothing inside it. What changes is only that its pitch stops containing a 12dp dead band where taps land nowhere and the scroll feels drunk. **Standing watch, spacing form:** the meta line's 8dp horizontal gaps stay a left-flowing line — SPENT is never right-aligned or columnised as a "finish" (Dens's arrangement charge, §2). |
| D-17 | EARLIER row | `padding(vertical 10)` around an 11sp label: live target ≈35dp, pitch ≈47dp | **Fixed height 48dp** (padding 10/10 → 0; the height owns the row), label centred; item owns **top padding 12** (band gap) above it | A paging verb he taps at the bottom of a long scroll, half-asleep, was the second-smallest target in the file. At 48 it joins the shelf-row species — the screen's two full-width tap rows now share one height, so the hand learns one size. The 12 above it is re-homed from `spacedBy`, spent once. |
| D-18 | Search-result headings | Own geometry: `padding(top 6)`, 11sp, with-year, no bottom padding — a third heading costume | **The day-heading geometry, verbatim**: top 18, 10.5sp, bottom 6 (with-year label is content, not geometry) | One heading composable, one geometry, three call sites (LANDING / MONTH / SEARCH). A result list whose shelves break differently from the landing's teaches the eye two rhythms for one act. |
| D-19 | Empty prints (`NOTHING STORED YET`, `NOTHING STORED UNDER…` → re-scoped `NO STORED TASK UNDER…`) | `padding(vertical 24)` + a `spacedBy` 12 on each side that the padding never asked for | **24/24 keep — now the sole owner** of the empty state's air | The value was always right; it was just paying rent to two landlords. No number changes; the asymmetry (24 + ghost 12 above, 24 + ghost 12 below, different neighbours per view) dies. |

### 2.6 The MONTHS shelf (INDEX's replacement, on the landing)

| # | Element | Current (INDEX rows, the nearest ancestor) | Gallery | Why |
|---|---|---|---|---|
| D-20 | Shelf heading MONTHS | — (did not exist; INDEX was a view) | **Top break 24dp**, heading **12sp** bold caps, **bottom padding 6**, **no hairline over the heading** | See amendment A-1: 24 (not the designer's 18) so a *section* out-costs a *day* (18) — at 18 the shelf opening would cost exactly one day break and read as one more day group with a strange heading. 12sp puts MONTHS in the STORED family (13sp), above day headings (10.5sp): 13 / 12 / 10.5 is the screen's whole hierarchy, in type as in space. |
| D-21 | Shelf row height | Content-driven: `padding(vertical 10)` + 14sp line ≈ 41dp live, ≈53dp pitch | **Fixed 48dp**, vertical padding **10/10 → 0** (the height owns the row — padding + fixed height would be a double system in miniature) | Ruled 48dp (designer §3.1). Fixed height is what makes ≤60 rows (five years) scan as one ruled index instead of a list of near-rows. |
| D-22 | Shelf row species | Atkinson Medium 14sp sentence case — **the StoredRow's own costume**; a month row felt exactly like a task row | **Anonymous Pro Bold caps 12sp, full ink**, text at the page line (no indent), **no chevron, no trailing furniture** | A shelf row that feels like a list row gets read as a task and tapped with the wrong expectation. Face + case + fixed height make it a different species at a glance, before a word is read. No indent: shelf months are peers of the current month in time, not its subordinates. |
| D-23 | Shelf row ruling + press | Hairline under (INDEX had this too) + `spacedBy` 12 of dead air between rows | **1dp hairline under every row including the last**; rows stack hairline-to-row with **0dp gap** — pitch = 48 live + 1 hairline; press wash (`#493EA4` ~8%) fills the 48dp box and stops at the hairline; the hairline never washes | The last row keeps its rule: a shelf of ruled rows ends ruled, above the acquitted 100dp of air. Zero dead band between targets — on a 60-row shelf, INDEX's geometry would have hidden 12 × 60 = **720dp of dead scroll**; the gallery shelf hides 60dp of hairline. |
| D-24 | Shelf position gaps | — | Last day group (or EARLIER, D-17) → MONTHS heading: the heading's own **24** (D-20), nothing else. Shelf's last hairline → bottom air **100** (D-02) | The section break is owned by the heading alone, so it is 24 whether the shelf follows day groups, EARLIER, or an empty (silent) current month — predecessor-independent, like the day heading's 18 (D-12). Absent shelf (no earlier months): nothing renders, no gap is spent — furniture for an absence is the defect this pass keeps removing (designer §3.4). |

### 2.7 DETAIL — the same law, one room over

DETAIL shares the column, so D-01/D-02/D-03/D-04 already apply.
Its internal stack is the file's other triple-system:

| # | Element | Current | Gallery | Why |
|---|---|---|---|---|
| D-25 | Provenance block leading | Column `spacedBy(12)` (ghost) × block `spacedBy(8)` × `Spacer(4)` before SPENT — three systems set every gap in the block, all to similar values | Column gap 0 (D-01); block internal gap **8 → 4**; the block owns **top padding 12** under the header band | Title → category → date → ESTIMATE (moved up into this block by the verdict) are one record and should lead tight, like lines of one entry. 4dp is a leading, not a break — which is what makes the next delta legible. |
| D-26 | SPENT separation | One more 8dp line in the block, behind a 4dp spacer — ESTIMATE and SPENT sit in an even 8-rhythm, an invitation to subtract down the column | **Spacer 4 → 18dp** between the provenance block and SPENT; SPENT stands alone | Ruled (§2: SPENT stands alone after a spacer; the subtraction column breaks). Spacing form of the same cure: equal gaps invite column reading; an 18 — the day-break value — says *new fact*, not *next line*. |
| D-27 | Substeps | Rows under `spacedBy(6)`, **start padding 14** (the only 14 in the file), numbers flush-right via `SpaceBetween` — ragged left edge, titles at 26dp from the screen edge | Number moves to a **leading column 24dp wide** at the page line; **titles start at 24dp** (36dp from the edge); row gap **6 keep** as sole owner (no padding, no hairlines — one system, as in D-16's law) | Ruled (two-digit numbering to Aug's leading column; strike distinction dead — type, not spacing). 14 was a lone arbitrary indent aligned to nothing; 24 is a column every title shares, so the eye files steps by their text, not by a wandering number at the far edge. Substeps are read-only print — no target floor applies; 6dp leading is correct and stays. |
| D-28 | Substep section break | `spacedBy(12)` ghost above the substep item | Substeps item owns **top padding 18** | A new section inside DETAIL costs the day-break value, same as a new day outside it. One break vocabulary for the whole screen: 12 band / 18 section-in-view / 24 section-on-landing (D-20). |

### 2.8 Hairlines — the system that stays

One weight, **1dp**, everywhere: under StoredRows, under shelf
rows, under the filing zone, the search line's bottom rule.
No 2dp, no double rules, no border boxes. Colour is the
designer's line (ink at ~14%), not this slice — thickness and
placement are: a hairline is always the **last dp of the object
it closes**, drawn outside press/ripple bounds, so feedback never
washes the ruling (D-23 states it for the shelf; it holds for
StoredRow today and is ratified, not changed).

---

## 3 · DOUBLE-SYSTEM CENSUS — EVERY ONE FOUND, AND ITS SINGLE OWNER AFTER

1. **Column gap × row padding × hairline** in day groups (12 + 6 + 1) → padding + hairline; column gap 0. (D-01, D-16)
2. **Heading top 6 × column gap 12** — an 18 owned twice, one edit away from 6 or 30 → heading owns 18 alone. (D-12)
3. **Capsule inner inset 12 × page inset 12** (horizontal) → page line alone; inner 0. (D-07)
4. **Capsule box**: fill + border + radius stacking three boundaries on one line → one 1dp rule. (D-06)
5. **INDEX/shelf pitch 53 = live 41 + dead 12** → 48 fixed, dead 1 (the hairline). (D-21, D-23)
6. **EARLIER pitch 47 = live 35 + dead 12** → 48 fixed + owned top 12. (D-17)
7. **Detail triple-stack** (12 × 8 × 4) → 0 / 4 / 18, one owner per gap. (D-25, D-26)
8. **Bottom air 108 = acquitted 100 + leaked 8** → exactly 100. (D-02)
9. **The phantom 0dp CABINET item** in the list's `when()`: a zero-height item still costs its two `spacedBy` gaps — **24dp of air for nothing, if it ever composed**. It dies with the eviction (verdict §2) — and D-01 makes the pattern impossible anywhere else: in a 0-gap column, a 0-height item costs 0.
10. **MONTHS hairline-over × last row's hairline** (designer's draft geometry) → resolved by carrying no over-hairline; see A-1.

---

## 4 · PITCH ACCOUNTING — THE LANDING, BEFORE AND AFTER

Approximate (≈ = text-dependent), from container top to the
bottom of the first day heading — everything he crosses before
the first stored row:

**Today ≈ 222dp**, spent as: top 8 · STORED ≈18 · gap 12 ·
capsule 48 · gap 12 · ALL MONTHS ≈28 · gap 12 · CURIOS ≈30 ·
gap 12 · today's heading with its word row ≈42.

**Gallery ≈ 237dp**, spent as: top 8 · header band 45 · band
gap 12 · search line 44 + rule 1 · filing 44 · filing 44 · zone
hairline 1 · day heading block 38.

Fifteen dp taller — and the spend is unrecognisable: BACK's
45dp target, a second filing line (tomorrow, the audit's worst
finding made fileable) with 44dp word targets, and an 18dp day
break now exist inside roughly the height the capsule and two
nav rows used to occupy. Nothing was added to the screen's
height budget; the furniture's rent was reassigned to targets
and breaks.

The ratio the verdict actually ruled on — white space before a
new day vs before a new row, inside a group:

- Today: before a heading ≈ 12 (ghost) + 6 (top) = 18 of owned-
  looking air, but before a row the same ghost pays 12 + 6 = 18
  as well — **the breaks are identical, which is why a new shelf
  feels like a new row** (the audit's 25-vs-25 in its accounting).
- Gallery: before a heading's text = row bottom 6 + hairline 1 +
  heading top **18** = **25dp**; before a row's title = 6 + 1 + 6
  = **13dp**. A new day costs ≈ **1.9×** a new row in white
  alone — past the verdict's ruled ≈37-vs-25 intent (≈1.5×),
  delivered by subtraction (the ghost's 12) rather than by
  inflating the heading past its ruled 18.

---

## 5 · TAP-TARGET CENSUS — EVERY TAPPABLE ON THE FINISHED SCREEN

| Target | Live today | Pitch today | Live gallery | Pitch gallery |
|---|---|---|---|---|
| BACK (MONTH / DETAIL; SEARCH gains one) | ≈17dp tall | ≈29dp | **45dp**, edge-flush, ≥45 wide | 45 (the band) |
| Filing words REST / ILL / FAMILY | ≈15dp tall, glyph-wide | in-heading | **44dp** tall, glyph + 16 wide, 0 dead between cells | 44 (the line) |
| Search field | 48dp | ≈60dp | **44dp** | 44 + rule |
| StoredRow | ≈49dp | ≈62dp (13 dead) | ≈49dp | ≈50dp (1 dead: the hairline) |
| Shelf row (was INDEX row) | ≈41dp | ≈53dp (12 dead) | **48dp** | 49dp (1 dead) |
| EARLIER | ≈35dp | ≈47dp (12 dead) | **48dp** | 60 incl. its owned top 12 |
| ALL MONTHS / CURIOS / `DrawerNavRow` | ≈27–28dp | ≈40dp | — killed — | — |

No surviving target is under 44dp. No surviving pitch contains
more than 1dp of dead band, except EARLIER's owned 12 — which is
a break, not a miss zone: it sits above the row, reads as air,
and the row below it is the largest target on the screen.

---

## 6 · AMENDMENTS & FLAGS FOR LADE

- **A-1 — MONTHS break 24, no over-hairline** (amends designer
  §3.1's "18dp, plus a hairline over the heading"). Two reasons,
  both geometric: (a) at 18 the section opening costs exactly one
  day break — the heading-vs-row equality this pass exists to
  kill, one level up; (b) the shelf follows day groups whose last
  StoredRow already ends in a hairline, so a second hairline
  carried over the heading either coincides with it (a phantom
  2dp rule) or floats 18–24dp below it as a twin. The section
  reads as a section by break (24) + species (12sp caps heading,
  48dp ruled rows) instead. If the panel wants the over-hairline
  back, it should replace the last-row hairline in that position,
  not add to it — one owner per line, as per gap.
- **A-2 — This file prices the floor's 48dp shelf row only.** The
  innovator's anchor-title proposal (P3) would grow the row to
  ≈56dp; if it is ever ruled in, D-20–D-23 re-open and the shelf
  census in §5 must be re-struck. Filed so the two specs can
  never silently coexist.
- **A-3 — Sealed search, geometry note.** If seal doctrine rules
  the search line is not composed while sealed (designer §5's
  allowed branch), the masthead shrinks by 57dp at the seal
  toggle. That is acceptable — a deliberate state change, not a
  mid-errand reflow — and the alternative (reserving 57dp of
  blank) would be a painted door rendered in geometry. No
  reservation is specced. The filing lines, by contrast, never
  change height in any state (D-11).

## 7 · VERIFICATION — FOR REN'S RENDER PASS (nobody here compiles)

1. Screenshot-measure the landing stack against §4 (≈237dp to
   the first heading's foot, ± text metrics).
2. Confirm the day-break white measures 25dp vs the row's 13dp.
3. Touch-test BACK at the extreme screen edge, both sides
   (DETAIL's is right-flush), and each filing word at its cell
   corners — corners must answer.
4. Scroll a 12+-month shelf: no dead band between any two rows;
   last row ruled; 100dp of air below it, not 108.
5. File ILL at the filing zone and confirm zero scroll reflow
   (D-11) — same screenshot before/after, diffed.

---

— Finisher 2 — Spacing (issue #8 cell)
