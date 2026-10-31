# DESIGN — THE DRAWER AFTER ISSUE #8 (main-app pass)

Designer, issue #8 cell. Source of law: `studio/plan/AUDIT-drawer.md`
(Lade's verdict, §2 / §2A / §6). This file designs only what the
verdict leaves to design: **the inline MONTHS shelf** and **the
Drawer landing with the cabinet evicted**. It does not touch the
forge bug (Builder, code), and it does **not** choose, hint at, or
imply a new home for the cabinet — that is parked in issue #21.
Where this file says "the cabinet," it says only: it is not here.

Nobody compiled anything. Nothing in this file is a Kotlin change.

---

## 0 · WHAT THIS SCREEN IS FOR NOW

Lade's sentence is the spec, verbatim:

> "The Drawer is where finished work rests — findable in seconds,
> readable without being judged."

Monolith is a blocker, not a tracker. The Drawer is its quiet
drawer in the literal sense: finished tasks are **put away, kept
forever, and can be found again** — nothing more. Concretely, for
every element proposed below, three tests must all pass:

1. **No grading, ever.** No totals, no counts, no streaks, no
   percentages, no per-month tallies, no completion sets. Not in
   print, and not by arrangement (no columns laid out to be summed,
   no rosters of slots). This binds the shelf design in §3 hardest:
   a month row carries a name and nothing else.
2. **Findable in seconds.** Three routes, no corridors: scroll the
   current month (0 taps), the MONTHS shelf → MONTH (2 taps to a
   task in any earlier month), search (type, tap). Every route
   lands on the same StoredRow and the same DETAIL.
3. **Read-only rest.** The only verbs on this screen are BACK,
   EARLIER (grow at his tap), the filing words (today/tomorrow,
   until declared), and tapping a row to read it. No edit, no
   delete, no reopen, no select. DETAIL keeps exactly one verb:
   BACK (acquitted, §6).

If a future idea fails any test, it does not belong on this screen,
however pretty it is.

---

## 1 · TYPE & COLOUR — HOUSE STYLE, THIS ROOM'S VOICE

Fonts: **Sora** (display), **Atkinson Hyperlegible** (body),
**Anonymous Pro** (numbers / telemetry). One binding exception from
the audit (§6, Aug, acquitted): **the absence of Sora is acquitted
on this screen — Anonymous Pro caps is the Drawer's display
voice.** The Drawer is a records room, not a poster; Sora stays
out. Do not "fix" this in the name of consistency.

| Role | Face | Size / form |
|---|---|---|
| Room name (STORED), shelf heading (MONTHS), month rows | Anonymous Pro | caps, bold where the audit rules it |
| Day headings in groups | Anonymous Pro | 10.5sp, bold caps (demoted 11 → 10.5, §2) |
| Search placeholder **and typed text** (same costume, §2) | Anonymous Pro | 12sp |
| Filing dates, finish clocks, SPENT, all telemetry | Anonymous Pro | as built |
| Task titles, detail body, substep text | Atkinson Hyperlegible | as built |

Colour — palette only, and this room uses a **subset on purpose**:
a quiet room does not spend the whole palette.

- **Ink:** `#34323B` — all primary text, full strength.
- **Meta ink:** `#34323B` at ~60% — categories, clocks, secondary
  lines. Never a second grey hue; opacity of the ink only.
- **Hairlines:** `#34323B` at ~14% — the one divider system (§2:
  rows breathe by padding + hairline only; the 12dp column gap
  inside day groups is dead).
- **The one accent:** `#493EA4` (deep violet) — interactive
  affordance only: undeclared filing words, the search caret and
  focus rule, press states (accent at ~8% wash). The moment a
  thing stops being an action, it stops being violet (see §4,
  declared filing words print in full ink — they are records now).
- **Not used on this screen:** `#F04FCB`, `#C84481`, `#E2BC39`,
  `#E47E35`, `#377ED0`. They belong to other rooms. In particular
  the day words get **no semantic colour-coding** — ILL is not red,
  REST is not anything. All three words are treated identically;
  colour-grading a sick day is still grading.
- **Never** green or anything near green, anywhere, including
  press/highlight states (the violet wash covers this).

Page metrics (all acquitted or ruled in §2, restated so the Builder
has one place to read them): 12dp page padding; 100dp bottom
padding; tap targets ~45dp, edge-flush for BACK; day-heading shelf
break 18dp top padding (a new day must cost visibly more than a new
row: 37dp against a row's 25dp).

---

## 2 · THE LANDING — ONE SCROLL, CABINET GONE

The cabinet's eviction removes the CURIOS row, one divider, and the
screen's only trophy furniture. INDEX and its ALL MONTHS row die
with it as navigation. What remains is a **single scroll** with
five bands, in this order, and nothing else:

```
┌──────────────────────────────────────────────┐
│ ‹ BACK            S T O R E D                │  1 · header
│ ──────────────────────────────────────────── │  2 · search line (44dp, 1dp rule)
│ TODAY · FRI 30 OCT        REST  ILL  FAMILY  │  3 · filing zone
│ TOMORROW · SAT 31 OCT     REST  ILL  FAMILY  │
│ ──────────────────────────────────────────── │
│ THU 29 OCT                                   │  4 · current month,
│   Task title              21:04   SPENT 1:20 │     day groups
│   ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   │
│   Task title              18:47   SPENT 0:50 │
│ WED 28 OCT                                   │
│   Task title              22:15   SPENT 2:05 │
│                                  EARLIER     │     (pages in place)
│                                              │
│ MONTHS                                       │  5 · MONTHS shelf
│ SEPTEMBER 2026                               │     (earlier months
│                                              │      only — the
│                                              │      current month
│                                              │      is band 4)
│ ──────────────────────────────────────────── │
│ AUGUST 2026                                  │
│ ──────────────────────────────────────────── │
│        (100dp bottom padding)                │
└──────────────────────────────────────────────┘
```

*(Sketch is schematic; row internals are the acquitted StoredRow
as built — title, category, finish clock, SPENT stays on the row.)*

Band by band:

1. **Header.** Header-costume BACK (the one BACK costume, §2) +
   **STORED** in Anonymous caps. With the nav rows below unified
   and the cabinet row gone, STORED is again the heaviest print on
   the screen — Aug's hierarchy cure lands by subtraction: we did
   not enlarge the header, we removed what out-inked it.
2. **Search line.** The capsule is dead. An entry line: 0dp radius,
   no fill, no border box, one 1dp bottom rule, 44dp tall, text
   starting at the page's own 12dp, placeholder `TYPE TO FIND A
   TASK`. Typed text keeps the placeholder's Anonymous 12sp costume
   (the costume-change on typing is a finish defect, cured).
3. **Filing zone.** Compact, directly under the search line, before
   any stored work — see §4.
4. **Current month.** Day groups exactly as acquitted: heading
   (10.5sp bold caps, 18dp shelf break above), StoredRows separated
   by hairlines and padding only, EARLIER at the foot paging in
   place. No month heading is needed here — the landing *is* the
   current month; printing its name would be a signpost to the room
   you're standing in.
5. **MONTHS shelf.** §3. This band replaces INDEX entirely.

**What is gone, by name** (so nobody re-adds it as "polish"):
CURIOS row · the cabinet view and its dead branch in the list's
`when()` · the ALL MONTHS row · the INDEX view · the search capsule
body · today's forced empty heading · FORGED tags on list rows ·
the standing forge line under any list · the dead CABINET spacer
branch. After the eviction, no element on this screen refers to
forging, artifacts, or collecting — the Drawer makes no promise
the forge code path (Repository shuffling all six, §2A) would
silently break, and implies nowhere that each finish adds a
visible thing.

**Empty landing.** If nothing is stored at all: `NOTHING STORED
YET` (acquitted print) and nothing else below the filing zone — no
shelf heading over an empty shelf (see §3.4). If the current month
is empty but earlier months hold work: today's heading does not
print (§2, killed as a shelf for an absence), the current-month
band is simply silent, and the MONTHS shelf stands below with real
named months — the acquitted answer to Muede's "silence reads
broken": the shelf proves the archive exists.

---

## 3 · THE MONTHS SHELF — THE DESIGN CENTRE OF THIS PASS

INDEX dies because it was a corridor: a view that added zero
content, whose own child disowned it (MONTH's BACK bypassed it).
The shelf is not a smaller INDEX. It is the archive's table of
contents printed **on the landing itself**, in the same scroll as
the work — the difference between a contents page bound into the
book and a separate room containing only a list of rooms.

### 3.1 Form

- Shelf heading: `MONTHS`, Anonymous Pro caps, same voice and size
  family as a day heading but given the full shelf break above it
  (18dp, plus a hairline over the heading — the shelf is a new
  section of the scroll, and the break must read as a section
  break, not as another day).
- One **ruled row per earlier stored month**, nothing more:
  - Content: the month's name **including the year**, every row,
    every time — `SEPTEMBER 2026`. Anonymous Pro caps, 12sp, full
    ink. Year on every row is not redundancy: at a fast scroll,
    each row must be self-contained; dimming or dropping the year
    on "same-year" rows (considered, rejected) makes a row mean
    something only relative to its neighbour, and relative print
    is how JANUARY 2026 gets misread as JANUARY 2025 at 1 AM.
  - Row height **48dp**, hairline under each row, whole row is the
    tap target. Press state: `#493EA4` at ~8% wash.
  - **No trailing furniture.** No chevron, no arrow, no "›" — the
    whole landing's grammar already says rows open (StoredRow does
    the same job with no arrow). No counts, no task numbers, no
    "last active" dates, no SPENT sums. A count per month is a
    score per month; the audit's no-count restraint from INDEX
    (§6, acquitted) carries over in full — the kill was structural,
    never an excuse to start counting on the shelf.
- Tapping a month row opens **MONTH** exactly as it works today:
  that month's day groups, same StoredRow, EARLIER paging within
  the month only, standing under that month's own heading (which
  honestly scopes EARLIER — Fremd's bare-adverb charge is answered
  by this furniture, the word stays).

### 3.2 Ordering

**Reverse chronological, nearest first.** The shelf begins with
the month immediately before the current one and walks backwards:
the most likely errand (last month) is the first row; depth in the
list *is* age, which the scroll position already communicates — no
sorting controls, no ascending option, no "jump to year." Years
are never sub-headed or grouped: the year is in every row's name
(§3.1), and a year separator would be a second, redundant system
saying the same thing, plus a heading that owns no tap and holds
no work — corridor thinking at shelf scale. One list, one order,
forever.

### 3.3 Empty months — they do not exist on this screen

The shelf lists **only months that contain stored work**. A month
with nothing in it gets no row — not a greyed row, not a
`NOTHING STORED` line, not a gap in the sequence. Three grounds,
all already law somewhere in the audit:

- The forced empty heading for today was killed as "a shelf for an
  absence" (§2); an empty-month row is the same object at shelf
  scale.
- Empty-month silence is acquitted by name (§6).
- The shelf is an index of **where work rests**, not a calendar.
  A calendar must account for every month; an index must not
  invent entries. If he finished nothing in July, the shelf steps
  from AUGUST 2026 to JUNE 2026 without comment — the step is
  visible in the names, and that is all the honesty the gap needs.

Corollary: the current month never appears on the shelf (it is
band 4, live, above). A month joins the shelf the moment it stops
being current and holds at least one stored task; a month with no
stored work never joins at all.

### 3.4 Absent shelf

If there are no earlier stored months (new archive, or everything
finished sits in the current month), **the shelf does not render**:
no MONTHS heading, no placeholder line, no empty state. The
landing ends after the current month's EARLIER / last day group,
into the 100dp bottom padding. Furniture for an absence is the
exact defect this pass keeps removing.

### 3.5 Scale — kept forever means the shelf must age well

The archive is forever, so the shelf is designed for the long
case, not the first month:

- One row per stored month is the cheapest possible unit — five
  years of archive is ≤60 rows of 48dp. The shelf stays **one
  unpaginated list in the landing scroll**; no EARLIER, no "show
  more," no collapsing by year. Paginating a list of names would
  re-corridor the shelf.
- As the shelf lengthens, **search becomes the primary route and
  the shelf the browsing route** — both already exist; nothing new
  is needed at year five that isn't needed at month two. That is
  the test the design passes: no scale furniture is pre-built, and
  none will be needed.

### 3.6 Returning — scroll restoration

MONTH's BACK is the header costume, its parent is **LANDING**
(honestly, now that the shelf has replaced INDEX), and going back
stops silently resetting the VM month (§2). Designer addition
within that ruling: returning from a MONTH lands the scroll **at
the shelf row he tapped**, not at the top of the landing. Browsing
two or three months in a row — the natural way to hunt a
half-remembered task — must not repay each return with a full
re-scroll past the current month. Same rule for DETAIL → MONTH and
DETAIL → LANDING: you return to where you were standing. This is
the shelf-scale expression of "findable in seconds."

---

## 4 · THE FILING ZONE (placement is ruled; presentation here)

Ruled composition (§2): a compact filing zone directly under the
search line, holding **today's and tomorrow's day-lines**, before
the stored work — the ask moves here, tomorrow becomes fileable,
and the words never stand on any other day's heading as an ask.
Presentation:

- Each line: the date (Anonymous Pro caps, meta ink — `TODAY · FRI
  30 OCT`, `TOMORROW · SAT 31 OCT`) left, the three words
  **REST · ILL · FAMILY** right, in `#493EA4`, each word its own
  tap target (≥44dp tall line; words separated enough that a 1 AM
  thumb cannot hit the neighbour — Muede's errand is the design
  case: Drawer, ILL, sleep, two taps).
- A word tapped files that day. The line then **retires to pure
  print**: the declared word sits beside its date in full ink
  `#34323B`, the other two words disappear, the line is read-only
  forever — his authored word, never a number (§6, acquitted).
  Violet leaves the line at the same moment the action does (§1).
- Undeclared, both lines simply stand — they are two compact lines,
  not a banner, not a card, nothing that grows if ignored. A line
  for a day already past is never shown; an unfiled past day stays
  blank by his choice, per the ruling.
- Tomorrow's line shows tomorrow's **filing date** under the 03:30
  rule (the day that begins at 03:30), consistent with DRW-05's
  live ticker — a tap at 01:00 files the day he is still in /
  about to sleep into, never the wrong day unprinted (Muede's
  charge, answered by the ruling; presentation just keeps the
  printed date honest to the same clock as everything else here).

**Sealed state.** Declared lines print as print (above). For an
undeclared line under seal, this design lays down one rule and
leaves the doctrine to Ren/LADE: **never a painted door** — the
same law the audit applied to sealed search. Either the words are
tappable under seal, or under seal the undeclared line renders as
plain date print with no word affordance at all. What it may never
do is show violet tappable-looking words that refuse.

---

## 5 · FINDING A FINISHED ITEM AGAIN — THE THREE ROUTES, SPECIFIED

1. **It's this month — scroll.** 0 taps. The row itself answers
   "how long" (SPENT stays on the row, acquitted) without opening
   DETAIL. Best case from the audit's 1 AM test, preserved.
2. **It's an earlier month — shelf.** Tap the month row on the
   landing, tap the task row in MONTH: **2 taps** (was 4 via the
   corridor). MONTH's BACK tells the truth (parent: LANDING) and
   the landing restores his scroll position at that shelf row
   (§3.6), so a wrong month costs one tap back and the next row is
   under his thumb.
3. **He remembers a word — search.** Type in the entry line.
   Results keep the acquitted grouping (day groups, year headings)
   with their own EARLIER. Two ruled honesty fixes are part of the
   design, not options:
   - **Substep matches show their reason:** when the hit is in a
     substep, the row prints that step as a second line (Atkinson,
     meta ink) under the title. A result that cannot show why it
     matched is a small false print.
   - **Empty result line is re-scoped:** `NO STORED TASK UNDER
     "X"` — after the eviction, search's territory and its promise
     finally match (nothing this screen stores is outside search).
   - SEARCH has the header BACK like every other view; BACK clears
     the query and returns to the landing as he left it. Leaving a
     mode only by erasing your own typing is a trap (§2).
   - Search is **enabled under seal** (§2, resolved): a query
     mutates nothing stored. If seal doctrine ever forbids all
     input, the line is not composed while sealed at all — never
     the inert box that invites typing it refuses.

DETAIL, reached from any route, is one named, saveable state (not
a shadow early-return), read-only, BACK its only verb. Inside it,
the ruled amendments: ESTIMATE moves up into the provenance block
(with category); SPENT stands alone after a spacer (both facts
print; the subtraction column breaks); substeps print in **one
voice** — the strike distinction is dead — with two-digit
numbering in Aug's leading column; FORGED, where it belongs,
prints at full ink **here in the detail only** (state belongs in
the detail, §2A), never on list rows, and no standing forge line
stands anywhere in the Drawer.

**State survival (design requirement on §2's state-only ruling):**
view / selected task / open month are saveable, identified by ID.
Process death mid-browse returns him to the view, month, and task
he was reading — a man who was three shelves deep at 1 AM does not
wake up on the landing. System back is consumed inside the Drawer,
one step per press, before any global ejection may fire.

---

## 6 · WHAT THIS DESIGN DELIBERATELY DOES NOT DO

- **No cabinet, no cabinet-shaped hole.** No placeholder row, no
  "moved" notice, no forwarding sign. The CURIOS row is killed,
  not annotated (§2A). Nothing here chooses or implies the
  cabinet's next home (issue #21 owns that, parked).
- **No counts anywhere** — not per month, not per day, not in the
  header, not in search results. The shelf's restraint is the
  screen's thesis.
- **No new gestures, no Trawl.** Vega's Trawl is a priced proposal
  for the looks programme, explicitly not ruled here; this pass
  adds no gesture layer to a storage screen.
- **No "days begin at 03:30" furniture.** The rule is printed
  nowhere on this screen; its one-line statement is escalated for
  the introduction sheet (§3 of the audit), where house clocks are
  learned once.
- **No Sora, no extra palette colours, no imagery at all** — hand-
  built type and hairlines only; there is nothing on this screen
  an image could say.

---

## 7 · HANDOVER NOTES FOR THE BUILDER (design side only)

- Shelf data need: the set of distinct earlier months holding ≥1
  stored task, reverse-chronological — names only, **no counts
  may be fetched into the row model** (if the query returns a
  count, drop it before the UI; a count in the model is a count
  waiting to be printed).
- The shelf is a section of the landing's single scroll, not a
  view, not a state. MONTH / SEARCH / DETAIL remain named states
  with saveable IDs (§5).
- The dead CABINET branch in the list's `when()` dies with the
  eviction (§2); the cabinet's two-step pop logic travels with the
  cabinet, wherever issue #21 sends it — it is not deleted from
  the codebase by this pass, only from this screen.
- Forge bug (Repository shuffles all six vs the cabinet's distinct
  set) is the Builder's code fix under issue #8 item 1; the design
  side of that truth is already discharged above: nothing in the
  Drawer prints, tags, or arranges anything about forging.

— Designer (issue #8 cell)
