# FUNKE — CRAZY IDEAS FOR THE DRAWER (issue #8)

**Seat:** Funke, crazy-ideas only. Not implementing, not auditing, not merging.
**Ground read before inventing:** issue #8 (forge bug, INDEX→MONTHS shelf,
cabinet evicted, home parked in #21), `studio/plan/AUDIT-drawer.md`,
`studio/plan/DRAWER-REVIEW.md`, all three existing issue comments.

**The laws I invent INSIDE (they are the fun, not the fence):**
Monolith is a blocker, not a tracker — no scores, no totals, no
day-grading, ever. Filing day starts 03:30. Day-words REST / ILL /
FAMILY are print, not judgement. The receipt slip is a proven-loved
surface (his ❤️, batch 7) — print is this app's native poetry.
Fonts: Sora / Atkinson Hyperlegible / Anonymous Pro. Elite palette
only, never green, never AI images — everything below is type,
hairlines, and deterministic geometry.

**My one-sentence thesis:** every seat so far designed the Drawer as
*furniture*. The Drawer is the only room in Monolith that faces
backwards — design it as a **place you visit on purpose, briefly,
ceremonially, and leave lighter** — and the anti-tracker law stops
being a restriction and becomes the whole aesthetic: if you may not
*count* the past, you must *stage* it.

---

## CHAPTER 1 — THE MONTHS SHELF

### 1. THE MONTH IS A SPINE
Each month on the shelf renders as a book spine: Sora caps for the
month name, and the spine's *thickness* is set by the single longest
day in that month — never by a count, never by a sum. A monstrous
14-hour day makes October fat; thirty thin days make November slim.
You read your year by silhouette, the way you recognise a shelf of
real books. Nobody can add spines up into a score; thickness is one
day's biography, not a total.
- **Mechanism:** one `max(spent)` per filing-month from the existing
  DAO, mapped to a 10–28dp spine width; shelf becomes a horizontal
  row of spines, tap = the MONTH view as today.
- **Cost:** S — one aggregate query + one composable; no schema change.

### 2. THE SHELF TILTS
A month whose last day was declared ILL or FAMILY leans — the spine
rotates 4° and rests against its neighbour, like a book pulled half
out and left. REST months stand straight but get a bookmark ribbon
(a 6dp vertical strip in #E2BC39) hanging below the shelf line.
Declared words stop being database rows and become *posture*. You
can see a hard season from across the room without a single number
or a single judgement word.
- **Mechanism:** read the month's day-word set (already stored);
  rotation/ribbon are pure render state on the shelf row.
- **Cost:** S — no data change at all; one render branch.

### 3. THE EMPTY MONTH IS A GHOST SPINE
A month with nothing stored still gets a spine — hairline outline
only, no fill, month name in the palest ink. Today an absent month
is simply not there, and absence is the one fact the Drawer
currently *hides*. A ghost spine says "this month happened; nothing
was filed" — which is true, unjudged, and (at 1 AM, looking back)
sometimes the most honest object in the app.
- **Mechanism:** shelf iterates calendar months from first-stored to
  current, left-joining stored months; missing = ghost rendering.
- **Cost:** S — shelf-list construction only.

### 4. FIVE-YEAR SHELF = THE LONG WALL
Pull the shelf past the current year and it doesn't page — it
*recedes*: earlier years render one step smaller and one step paler
per year, a corridor of shelves in forced perspective, Anonymous Pro
year numerals at each landing. Feel-5's "five-year shelf" test made
visual. Search still lands you anywhere in one jump; the wall is
for *wandering*, and wandering is the ritual this room exists for.
- **Mechanism:** scale/alpha by year-distance in the shelf list;
  years beyond stored range are ghost shelves (idea 3).
- **Cost:** M — shelf becomes a lazy list with per-year styling;
  needs a scroll-restore anchor so return trips land where you left.

---

## CHAPTER 2 — THE CABINET EVICTION

*The cabinet is evicted; its home is parked (#21). Fine. But an
eviction with no ceremony is just a delete wearing a suit. These
ideas dispose of the cabinet the way Monolith disposes of everything:
with a receipt.*

### 5. EVICTION AS A FUNERAL PROCESSION
The six artifacts do not vanish on update day. On six consecutive
evenings (at the SLEEP routine's arm, the quietest moment the app
owns), one artifact at a time is shown on the block screen's rest
surface — full lore card, forged date, the true line "FORGED BY A
FINISHED TASK" — and then it leaves. After the sixth evening, the
cabinet is gone and has been *seen* going. Software deletes things
instantly; Monolith, the app of rituals, is the one app that could
afford to take a week to say goodbye.
- **Mechanism:** a 6-step eviction state (one int in prefs) advanced
  by the routine-arm event; each step renders one catalog entry from
  `CurioCatalog` + its forged row, then marks it departed.
- **Cost:** M — one state machine + one card surface; zero schema
  change (forged ids already stored; departed = prefs set).

### 6. THE DUST OUTLINE
Where the cabinet stood, the Drawer keeps ONE plate: "WHERE THE
CABINET STOOD" — six hairline silhouettes (deterministic SVG-ish
paths drawn in code, one per artifact's shape family: cube, rings,
wave, prism…) at 18% ink, forever unfilled, untappable. Museums do
this when a piece is deaccessioned: the outline stays on the wall.
It is also the truthful answer to DRW-02 — the toys never existed
as interactions; as *outlines of things once promised* they finally
tell the truth.
- **Mechanism:** static plate composable under the shelf; six
  hand-drawn vector paths in the elite palette's #34323B.
- **Cost:** S — one composable, six paths, no data.

### 7. THE EVICTION RECEIPT
The loved receipt grammar gets its darkest joke: when the last
artifact departs (idea 5) — or immediately, if the procession is
rejected — the Drawer files ONE receipt among the stored tasks,
dated that day, titled "THE CABINET", SPENT line reading the
cabinet's whole lifespan ("HELD FROM <first forge> TO <eviction>").
It is searchable. It pages with EARLIER. It is a stored task like
any other, because in this app the past is filed, not deleted.
Years from now he can search "cabinet" and find its death
certificate filed between two honest workdays.
- **Mechanism:** one synthetic stored row written at eviction
  completion (flagged type in the existing row structure), rendered
  by the normal StoredRow path — receipt typography free.
- **Cost:** S–M — one insert + a render check that synthetic rows
  skip forge/day-word logic.

### 8. ARTIFACT AMNESTY — ONE COMES BACK AS A DOORSTOP
One artifact — his pick, tapped during the procession — does not
leave. It is demoted: stripped of lore, requirements, and mystery,
it becomes the Drawer's literal paperweight, sitting at the foot of
the shelf, doing exactly one job: it is the long-press target that
opens search. From relic to tool. The demotion is the point — the
cabinet's sin was pretending to be treasure; a doorstop pretends
to be nothing and is used daily.
- **Mechanism:** prefs hold the spared artifact id; shelf footer
  renders its silhouette at full ink; long-press = search focus.
- **Cost:** S — one pref, one render, one gesture binding.

---

## CHAPTER 3 — HELD HISTORY

### 9. THE HELD SHELF
Sessions sealed but never finished — and the `partial_stamp` rows
the code already writes — currently have no honest home. Give them
a HELD shelf inside the Drawer, in a different ink (#377ED0, the
palette's cold blue): title, date, SPENT as far as it got. Not
failed (that's grading), not finished (that's lying) — *held*,
exactly as a library holds a book mid-return. One tap re-opens the
task as a fresh declaration if he wants it back; untouched, it
rests. This converts the app's guiltiest silent data into its most
merciful surface.
- **Mechanism:** query the existing partial rows into a Drawer
  section below the current month; reopen = existing task-create
  path prefilled with the held title.
- **Cost:** M — one query + one section + reuse of the create path;
  the critic's F4 note (filter `partial_stamp` from artifact logic)
  must ride along.

### 10. THE UNFINISHED LETTER
A held task can carry ONE line, written at the moment of holding —
auto-composed by the app from fact, never typed under pressure:
"STOPPED AT 22:41 · THE ROOM WAS QUIET" is too much; the factual
version: "HELD 22:41 · 31 MIN IN". Filed on the held row in
Anonymous Pro. It is the margin note a librarian pencils in — where
you stopped, so future-you resumes without archaeology. No advice,
no summary, no AI sentence: a timestamp and a duration, the two
facts that make a held thing resumable.
- **Mechanism:** stamp composed at hold time from session fields
  already recorded; printed on the held row and its detail.
- **Cost:** S — string composition on an existing write path.

### 11. HELD THINGS AGE IN INK, NOT IN VERDICTS
A held row's ink lightens by one fixed step per month it rests —
five steps, then it stays at the palest step forever. Nothing is
deleted, nothing expires, nothing nags; age is shown the way paper
shows it. The pale rows at the bottom of the HELD shelf are simply
old intentions, visible at a glance, carrying no sentence harsher
than light ink. (Hard line: lightening is
...[truncated 2512 chars]