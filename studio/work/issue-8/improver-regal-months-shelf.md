# IMPROVER — THE MONTHS SHELF, IMPROVED IN ROUNDS (issue #8)

**REGAL, Improver — the Drawer cell (lead: Lade), flat under Ren.**
**My one job:** the inline MONTHS shelf on the Drawer landing — the
presentation that replaced the killed INDEX. Nothing else in this
file. The filing zone, the search line, DETAIL and MONTH's internals
are other seats' finished work; I touch them only where the shelf
touches them, and I name the seam when I do.

**Fixed points, not re-opened:**
- INDEX is dead. The `ALL MONTHS` row and the INDEX view stay dead.
  Nothing here resurrects a corridor, a contents room, or a second
  list view. The shelf is a section of the landing's single scroll.
- The cabinet is evicted from the Drawer. This design works with
  the cabinet **simply absent** — no placeholder row, no "moved"
  notice, no forwarding sign, no cabinet-shaped hole, and nothing
  that chooses or implies its next home (that decision is parked,
  issue #21). The shelf never had a cabinet dependency and gains
  none.
- Lade's sentence is the law: *"The Drawer is where finished work
  rests — findable in seconds, readable without being judged."*
- No counts, totals, streaks, percentages or sums — in print or by
  arrangement. A month row is an address, never a measurement.
- No green or near-green anywhere. No Sora on this screen — the
  absence of Sora is acquitted (AUDIT-drawer §6, Aug): Anonymous
  Pro caps is this room's display voice. Atkinson Hyperlegible is
  the body face; Anonymous Pro is telemetry/display in this room.

**What I am improving FROM (existing output, read whole):**
1. `studio/plan/AUDIT-drawer.md` §2 — the ruling: LANDING becomes
   one scroll — current month, then a MONTHS shelf: one ruled row
   per earlier stored month (name incl. year, NO counts), each
   opening MONTH as today. Old-task depth 3 taps → 2.
2. `studio/work/issue-8/designer-drawer.md` §3 — the designed
   shelf: `MONTHS` heading given the full shelf break (18dp +
   hairline over the heading); rows Anonymous Pro caps 12sp, full
   ink, 48dp, hairline under each; press `#493EA4` at ~8% wash;
   no chevron; reverse-chronological; empty months absent; shelf
   absent when no earlier month qualifies; returning lands at the
   tapped shelf row.
3. `studio/work/issue-8/finisher-3-search-shelf.md` §4–§5 — the
   finished spec: the whole shelf rendered as ONE LazyColumn item
   (the landing's 12dp `spacedBy` must not land between shelf
   rows); word-only months STAY on the shelf (§4.5 correction);
   the Anchor Title rider (§5): the month's newest stored task
   title, verbatim, as a second line, uniform 60dp rows, one
   filing-month-true query, never N+1.
4. **The built shelf** in `DrawerScreen.kt` (LANDING branch,
   wave-1/2, commits f0e1461 + a3151e1) — what actually ships
   today, and the baseline every round below is measured against.

This file is a spec. No Kotlin was edited, nothing was compiled —
nobody compiles.

---

## 0 · THE GAP — BUILT SHELF vs DESIGNED SHELF

The wave-1 build killed INDEX and stood a shelf up in its place —
the structure is right and the kill holds. But the built shelf is
a **first draft of the designed shelf**, and at first glance it
fails the room's own hierarchy test (MASTER-PLAN K3: with colour
removed and at arm's length, shelf labels, slip titles and meta
lines must still separate by family, weight and tone alone):

| # | Designed / finished (Designer §3, Finisher-3 §4) | Built (DrawerScreen.kt, LANDING) | Defect |
|---|---|---|---|
| G1 | Row print: Anonymous Pro Bold 12sp caps, ls 1sp, full ink | Atkinson Hyperlegible Medium **14sp**, sentence case from `monthLabel` uppercased | A month row is **indistinguishable from a StoredRow title** — same face, same size, same weight family, same ink. `OCTOBER 2026` reads as a task he finished, not an address. The shelf and the work wear one costume. |
| G2 | Section break: hairline over the `MONTHS` heading, 18dp of break weight in total | Heading `MONTHS` 11sp with **6dp top padding, no hairline** | The shelf does not read as a new section of the scroll. After the last day group (or after EARLIER) the eye meets another small caps line at the same rhythm as a day heading — a shelf that looks like a day. |
| G3 | Whole shelf = **one** LazyColumn item containing a Column (Finisher-3 §4.1) | Heading is one item; **each row is its own item** in a LazyColumn with `Arrangement.spacedBy(12.dp)` | The 12dp column gap lands between every pair of shelf rows, **on top of** each row's own divider — the shelf's pitch stutters into separated blocks, exactly the re-pitching into cards Finisher-3 warned against. |
| G4 | Row height **48dp**, whole row the tap target | Row = text + `padding(vertical = 10.dp)` ≈ **40dp** felt height | Below the ~44–45dp tap floor this same file enforces for every BACK. The shelf's only verb is the hardest target on the landing to hit. |
| G5 | Press state: violet wash `#493EA4` 8% light / 22% night, manual from the press state (ripple is purged app-wide) | No press state at all — clickable with no indication, no wash | A press gives **no answer**. At 1 AM a tired thumb cannot tell a registered tap from a miss until the view changes — or doesn't. |
| G6 | Anchor Title rider (Finisher-3 §5, SHIP with conditions): verbatim newest-task title as second line, uniform 60dp | **Absent** — `StoredMonth(year, month)` carries no anchor; rows are name-only | A 24-row shelf is scanned by remembering which month held the work — the memory test the audit charged search with, rebuilt on the shelf. The recognition route does not exist. |
| G7 | Shelf = months **earlier** than the current filing month | Filter is `filterNot { it == currentYearMonth }` — anything not-current shelves, **including a future month** | See Round 4: filing tomorrow's word in next month (the wave-2 filing zone makes this a normal, nightly act) puts **next month on the "earlier months" shelf tonight**. A small false arrangement, live at every month seam. |
| G8 | Return from MONTH lands at the tapped shelf row (Designer §3.6) | Nothing — MONTH's BACK returns to LANDING at whatever scroll the list kept | Browsing two months in a row repays each return with a re-scroll past the whole current month. "Findable in seconds" dies on the second month. |

Rounds 1–4 close G1–G8 in that order of pain. Round 5 consolidates
the whole shelf into one buildable spec with acceptance checks.

---

## ROUND 1 · THE ROW GETS ITS OWN VOICE, THE SHELF GETS ITS BREAK
*Closes G1, G2, G3. What got better: a month can no longer be
mistaken for a task, the shelf reads as a section instead of a
day, and its pitch is exact instead of accidental.*

### 1.1 Row print — Anonymous Pro, not the task voice

- Print: the month name including the year, on every row, every
  time — `SEPTEMBER 2026` — composed by the shipped `monthLabel`
  (`MMMM yyyy`, uppercased), unchanged.
- Face: **Anonymous Pro Bold, 12sp, lineHeight 16sp,
  letterSpacing 1sp, full ink** (`onBackground`; palette ink
  `#34323B` at full strength).
- Why this is an improvement and not a restyle: the built row
  (Atkinson Medium 14sp) is character-for-character the StoredRow
  title's costume. On a landing where the current month's tasks
  sit directly above the shelf, the first shelf row continues the
  task list visually and only its wording betrays it. Anonymous
  caps is the room's address voice everywhere else it names a
  place in time — the MONTH view's own header prints the same
  `monthLabel` string in Anonymous Bold — so the shelf row and
  the room it opens now speak one voice, and the shelf separates
  from the work by **family alone**, colour removed, at arm's
  length (the K3 test, passed by construction).
- The year is never dimmed, dropped on "same-year" rows, or moved
  to a separator (Designer §3.1 stands): at a fast scroll each row
  must be self-contained; relative print is how JANUARY 2026 gets
  misread as JANUARY 2025 at 1 AM.

### 1.2 The section break — hairline, then air, then the heading

The shelf is a new section of the scroll, and it must cost
visibly more than a new day (the audit's day-heading logic,
one scale up). Exact stack, inside the shelf item (§1.3):

1. `HorizontalDivider` — 1dp, `outlineVariant` (palette ink
   `#34323B` at ~14%). The shelf always draws its own top
   hairline, whoever stands above it.
2. Spacer **11dp**.
3. Heading `MONTHS` — Anonymous Pro Bold, **11sp**, lineHeight
   14sp, letterSpacing 1sp, meta ink (`onSurfaceVariant`). Half a
   point above the day heading's 10.5sp because this heading
   sections the scroll rather than labelling a day (Finisher-3
   §4.1). Not tappable. No count ever joins it — `MONTHS`, alone.
4. Spacer **6dp** to the first row.

Hairline + spacers put **18dp of break weight above the heading**
(the audit's shelf-break weight, Finisher-3's arithmetic), and
because the break is *inside* the shelf item it survives every
landing state: after a heavy current month, after EARLIER, and —
critically — when the current month is silent and the shelf is
the first work on the screen (Round 4, edge E2). The built 6dp
pad survived none of these distinctly.

### 1.3 One item, one pitch — the shelf composes as a single item

- The whole shelf — top hairline, heading, every row — renders as
  **one LazyColumn item** (`key = "months-shelf"`) containing a
  `Column`. The landing LazyColumn's `Arrangement.spacedBy(12.dp)`
  therefore falls *around* the shelf, never *inside* it.
- Inside the Column there is no arrangement spacing at all: rows
  are fixed-height (§2.1 / §3.1) and each is closed by its own
  1dp `outlineVariant` hairline drawn as the row's last element.
  Pitch = row height, exactly, sixty rows deep. No double
  hairlines: a row's closing hairline and the next row's body are
  adjacent by construction, and the shelf's top hairline (§1.2)
  belongs to the heading block, not to a row.
- Cost, stated honestly: one item composes all shelf rows at
  once. At the design bound (60 rows at five years, Round 4) that
  is sixty small text rows — a trivial composition. The re-check
  trigger is named in §4.3; until it fires, exact pitch is worth
  more than lazy-per-row items.

**R1 acceptance (shelf slice):** (a) desaturate a render of the
landing — the shelf still separates from the task rows by type
family alone; (b) measure consecutive shelf rows in the render —
pitch is uniform to the dp; (c) the `MONTHS` heading is preceded
by a hairline and visibly more air than any day heading gets.

---

## ROUND 2 · THE PRESS ANSWERS, THE TARGET FITS THE THUMB, THE READER IS TOLD TRUE
*Closes G4, G5, and the shelf's semantics debt. What got better:
the shelf's only verb now feels like a physical surface, is as
easy to hit as every BACK in the file, and announces itself
honestly to a screen reader.*

### 2.1 Geometry — a real 48dp row (name-only form)

- Row height is **set, not accumulated**: the row container is
  `height(48.dp)` with its content vertically centred — replacing
  the built form (text + 10dp vertical padding ≈ 40dp). 48dp
  clears the house tap floor (~44–45dp) with the same margin the
  heading break uses; the whole row, edge to edge of the content
  width, is the tap target. Text starts at the page's own 12dp —
  no row-local horizontal padding (the LazyColumn's page padding
  is the only inset, as everywhere on this screen).
- The closing hairline sits *inside* the 48dp (drawn last, at the
  row's foot) so pitch arithmetic in §1.3 stays exact: row =
  48dp, of which the final 1dp is its rule.
- The anchored form's 60dp geometry is Round 3's; whichever form
  ships, **every row in one shelf shares one height** — the shelf
  never mixes 48dp and 60dp rows (§3.1).

### 2.2 Press — the violet wash, driven by hand

Ripple is purged app-wide (`Theme.kt`); the built shelf's silent
clickable is what remains when nobody replaces it. The shelf
replaces it, exactly as Finisher-3 §4.2 ruled, now with the
mechanics stated so a builder cannot substitute a ripple back:

- The row holds a `MutableInteractionSource`; its pressed state
  drives a background wash of the house accent **`#493EA4`** —
  **8% alpha in the light scheme, 22% in the night scheme**
  (same hue, perceptual parity on a dark ground; no new colour).
  `DrawerScreen` already receives `isDarkTheme` — the built shelf
  ignores it; the improved shelf is its first consumer on this
  screen. Use it, do not re-derive the scheme.
- Wash on **at press-down, in the same frame**; on release, a
  **120ms fade** to nothing. The shelf answers like a physical
  surface, not a light switch (Finisher-3's words, kept).
- The wash covers the row's full height and **nothing else**; the
  closing hairline is drawn after the wash and never tints; the
  row's print does not change colour under press — only the
  ground washes. Adjacent rows never wash.
- No other motion exists on the shelf. Rows do not slide, fade
  in, or animate on arrival (the Drawer is motionless by ruling —
  Finisher, Motion, wave-2 report). The wash is a press answer,
  not an entrance.

### 2.3 Semantics — one node, one true announcement

- One clickable node per row, **role Button**, content
  description = the month name exactly as printed
  (`SEPTEMBER 2026`). In the anchored form the description gains
  the anchor (§3.4); the visible print never carries the word
  "newest".
- Test tag preserved from the built shelf, unchanged:
  `drawer_month_<year>_<month>` — existing harness references
  keep working; retrofitting tags is how harness tests rot.
- **Sealed state — the shelf stays live.** Opening a month is
  reading, and the sealed Drawer is read-fully furniture (audit
  §2; sealed rows and detail already read). The built code
  correctly puts no `isSealed` gate on the shelf rows; this round
  makes that a stated law with a test (§5, A7), because it is
  exactly the kind of thing a future "disable navigation under
  seal" pass would silently break — and a sealed Drawer that
  cannot open its own shelf is not findable in seconds.

**R2 acceptance (shelf slice):** (a) press a shelf row in light
and night renders — wash visible in both, one row only, gone
cleanly on release; (b) every shelf row measures 48dp (or 60dp
anchored) including its hairline; (c) a screen reader announces
each row as a button named by its printed month; (d) sealed —
a shelf tap still opens its month.

---

## ROUND 3 · THE ANCHOR TITLE — ADOPTED, TIGHTENED, MADE BUILD-SAFE
*Closes G6. What got better: a long shelf is scanned by
recognising his own work instead of remembering its month — and
the rider can no longer ship in a weakened, half-true form.*

Finisher-3 §5 judged Innovator P3's Anchor Title SHIP WITH
CONDITIONS. The built shelf never shipped it. This round adopts
it whole and tightens four screws the finisher left to chance.

### 3.1 The form — one shelf, one height, one choice

- **The shelf ships in exactly one of two lawful forms, chosen
  once for the whole shelf, never per row:**
  - **Name-only form:** every row 48dp (Round 1–2 spec).
  - **Anchored form:** every row **60dp**, two lines (§3.2).
- A row never collapses or grows because of its own contents.
  A word-only month (§4.4) and — defensively — a task month whose
  anchor (§3.3) somehow returns null both stand at the anchored
  height with the second line simply unprinted, the name at the
  identical top position. The scan line never moves; the pitch
  never stutters. Mixed heights in one shelf are a defect, full
  stop.
- **The choice is gated on the data, not on taste:** the anchored
  form ships only if §3.3's single-query data spec is met as
  written. If it cannot be met, the shelf ships name-only —
  which is already gallery-grade — and the anchor waits. There
  is no third, weakened form (no per-month queries, no anchor
  computed from paged rows, no "first title we happened to load").

### 3.2 Row layout — anchored form (sums to 60dp)

- Top padding **11dp**; name line (Anonymous Pro Bold 12sp,
  lineHeight **16sp**, ls 1sp, full ink); gap **3dp**; anchor
  line (Atkinson Hyperlegible **Regular 13sp**, lineHeight
  **18sp**, meta ink `onSurfaceVariant`); bottom padding **12dp**
  — of which the row's closing 1dp hairline is the final dp, as
  in §2.1. 11 + 16 + 3 + 18 + 12 = **60**.
- The anchor is the month's newest stored task title,
  **verbatim** — never uppercased (the name line is caps; the
  anchor keeps its stored case — the case contrast *is* the scan
  cue), never summarised, never generated, never rotated through
  the month's titles, never shortened in the query. Single line,
  ellipsis at the row's width: truncation by width is a layout
  fact, stated here so nobody later calls the ellipsis a broken
  promise — and so nobody "helpfully" pre-shortens a title.
- The anchor line prints **no label, no date, no count**: no
  "NEWEST", no finish clock, no second task. It is a title he
  wrote, standing under an address. The moment it carries
  furniture it becomes a summary, and a generated or decorated
  line in this slot kills the rider on the truth standard
  without a new hearing (Finisher-3 §5.3.3, kept verbatim in
  force).
- Press wash (Round 2) covers the full 60dp including the anchor
  line; the anchor's ink does not turn violet under press — only
  the ground washes.

### 3.3 Data — one query, filing-month true, never N+1

- `StoredMonth(year, month)` gains **`anchorTitle: String?`** —
  the only model change in this entire file. Nothing else in the
  model, the DAO's other reads, or the schema changes; DB version
  untouched.
- **One query.** `getStoredMonths` returns, per filing month, the
  title of the task row with the greatest
  `(completedAtMillis, id)` in that month — the same total order
  every paged read on this screen already uses — picked
  correlatively inside the months query. One small query per
  month at shelf-build time is **killed**: at sixty months that
  is sixty round-trips to build a list of names.
- **Filing month, not calendar month.** The pick applies the
  identical 03:30 shift the months query already applies in
  SQLite (`(completedAtMillis / 1000) - 12600`), so a task
  finished at 01:00 anchors to the same shelf row its own day
  group files under. Anchor and archive must never disagree
  about which month a task rests in (§5, A9 tests the seam).
- Only `status = 'COMPLETED'` rows anchor — the same filter as
  every Drawer read. Word-only months get `null` from the task
  side of the union (§4.4), never a substitute string.
- The anchor is derived in the Dao/Repository months read (which
  `refreshDrawer` already triggers), **never from `drawerRows`** —
  those hold one month, partially paged, and an anchor computed
  from them would change as he pages EARLIER. The anchor is a
  fact of the archive, stable for the visit.
- Repository union unchanged in shape: task months ∪ declared-
  word months, reverse-chronological. The anchor rides on the
  task side only; the union logic does not learn about titles.

### 3.4 Semantics — the one place "newest" is spoken

- Anchored row content description:
  `"<MONTH NAME>, newest: <anchor title>"` when an anchor
  exists; the plain month name when it does not. The word
  "newest" exists **only** in the reader's ear, never in print —
  in print it would be a label, and labels become furniture.
- Carried honestly (Finisher-3 §5.3.1–2, not hidden): one title
  standing for a month of unlike work can misdirect a scan, and
  a painful title surfaces one level shallower than it sat. Both
  are true of every finding aid; the newest slip is the one most
  likely still warm, the name above the anchor always decides,
  and search remains the route when anchors fail. The anchor
  advises; it never decides.

**R3 acceptance (shelf slice):** (a) a ≥12-month render scans by
anchor recognition — a known task's month is findable without
reading every month name; (b) every row measures 60dp including
word-only rows; (c) a title longer than the row ellipsises in
place and moves nothing below it; (d) the months read is one
query — no per-month fetch exists in the diff.

---

## ROUND 4 · SCALE, RETURN, AND THE EDGES — INCLUDING THE MONTH THAT ISN'T EARLIER
*Closes G7, G8, and the edge states the built shelf leaves to
luck. What got better: the shelf stays true at five years, a
wrong month costs one tap, and next month can no longer sneak
onto a shelf of earlier months.*

### 4.1 The future-month catch (G7) — the round's headline

The built filter is `filterNot { it == currentYearMonth }`:
everything that is not the current month shelves. Since wave-2's
filing zone, filing **tomorrow's** word is a normal nightly act —
and when tomorrow falls in next month (31 Oct → 1 Nov), the
repository's union (task months ∪ declared-word months, acquitted
and correct) gains next month **tonight**. The built shelf would
print it at the top of the "earlier months" shelf, above last
month — a future month presented as the nearest past. Tapping it
even "works," which makes it worse: a false arrangement that
functions is a lie with good plumbing.

- **Ruled:** the shelf lists months **strictly before** the
  current filing month — `(year, month)` lexicographically less
  than `filingYearMonth(now)` under the 03:30 rule. Not
  "not-current": *earlier*.
- Tomorrow's word loses nothing: tonight it stands in the filing
  zone where he filed it; when its month becomes current it
  stands in the current-month band; when that month ends it joins
  the shelf in its true place. No record is orphaned at any
  point — the word is reachable every day of its life, in the
  band that is honest that day.
- The change is one filter expression in the LANDING branch. The
  repository union is **not** touched — it must keep returning
  the future word-month, or the filing zone's own month logic
  loses it. The shelf was the wrong consumer, so the shelf is
  what changes. (§5, A8 tests the seam night.)

### 4.2 Ordering and scale — one list, forever

- **Reverse chronological, nearest first**, exactly as the
  repository already sorts. No year sub-headings, no grouping,
  no "jump to year," no sorting control: the year is in every
  row's name (§1.1), and a year separator would be a second
  system saying the same thing, plus a heading that owns no tap
  and holds no work — corridor thinking at shelf scale
  (Designer §3.2 stands, unamended).
- **One unpaginated list.** No EARLIER on the shelf, no "show
  more," no collapse. Paginating a list of names re-corridors
  it. Design bound: five years ≤ 60 rows (2,880dp name-only /
  3,600dp anchored) inside the landing's single scroll; as the
  shelf lengthens, search becomes the primary route and the
  shelf the browsing route — both already exist, and nothing
  needed at year five is absent at month two.
- The current month never appears on the shelf (it is the
  landing's body, above). An empty month never appears (§4.4).

### 4.3 Composition re-check trigger (the honest bound on §1.3)

The single-item composition (§1.3) is right while shelf rows are
text and hairlines. **Re-litigate it if and only if** a row ever
gains non-text content (imagery, controls, live data) or the
archive's realistic bound passes ~15 years of months. Until
then, splitting the shelf back into lazy per-row items to "save
composition" buys nothing and re-opens G3.

### 4.4 Empty, word-only, and absent — the shelf's silence law

- **E1 · No earlier months:** the shelf does not render at all —
  no heading, no top hairline, no placeholder, no empty line.
  The landing ends after the current month into the 100dp bottom
  padding. Furniture for an absence is the defect this whole
  pass keeps removing.
- **E2 · Current month empty, earlier months exist:** the
  current-month band is silent (no absence line — acquitted),
  and the shelf stands below the filing zone as the first work
  on the screen, **with its full section break intact** (§1.2 —
  the break lives inside the shelf item precisely so this case
  cannot flatten it). Named months of real work standing visible
  are the acquitted answer to "silence reads broken": the shelf
  proves the archive exists.
- **E3 · Word-only month:** a month holding only a declared word
  (REST / ILL / FAMILY) and no finished task **stays on the
  shelf**, in its chronological place (Finisher-3 §4.5
  correction to Designer §3.3, adopted and now load-bearing:
  `storedMonths()` unions word months deliberately, or the word
  would be filed nowhere). It opens a MONTH where the word is
  filed. In the anchored form it stands at 60dp with its second
  line unprinted (§3.1); in the name-only form it is
  indistinguishable in shape from any other row — which is the
  point: a filed word is an entry, not an apology.
- **E4 · A month with nothing:** no row, no greyed row, no gap
  comment. The shelf steps from AUGUST 2026 to JUNE 2026 without
  a word; the step is visible in the names, and that is all the
  honesty the gap needs. The shelf is an index of where work
  rests, not a calendar — a calendar must account for every
  month; an index must not invent entries.

### 4.5 Returning — the tapped row is under his thumb (G8)

- Returning from MONTH lands the landing scroll **at the shelf
  row he tapped**, not at the landing's top: browsing two or
  three months in a row — the natural hunt for a half-remembered
  task — must not repay each return with a full re-scroll past
  the current month. Same rule through DETAIL → MONTH → LANDING:
  he returns to where he was standing.
- Mechanism, for the builder (the requirement binds, the
  arithmetic is the cheap part): with the shelf as one item
  (§1.3), the host's landing `LazyListState` restores via
  `scrollToItem(shelfItemIndex, offset)` where
  `offset = −(top-hairline + heading block + rowIndex × row
  pitch)` in px, so the tapped row sits at the viewport top.
  Row pitch is the form's fixed height (48 or 60dp) — one more
  reason §3.1 forbids mixed heights: the return arithmetic must
  never guess.
- This is a landing-scroll state requirement, not a new store:
  it rides the saveable-state work wave-2 already landed for
  view/selected task. If the host cannot restore the offset, it
  restores the shelf item's top as the floor — never the
  landing's top.

**R4 acceptance (shelf slice):** (a) file ILL for a tomorrow in
next month at 23:00 — no future month stands on the shelf
tonight, and the word is on the shelf in its true place after
that month ends; (b) open the third shelf month, BACK — the
tapped row is at the viewport top; (c) five-year synthetic shelf
(60 rows) scrolls as one list with uniform pitch and no paging
furniture; (d) E1–E4 each render as specified above.

---

## ROUND 5 · THE SHELF, CONSOLIDATED — ONE BUILDABLE SPEC
*What got better: Rounds 1–4 collapse into a single spec a
builder can diff against in one sitting, with every number in
one table and every claim tied to a check. This section governs
where it differs from Designer §3 / Finisher-3 §4–§5; everything
in those files that this section does not mention stands.*

### 5.1 The spec in one table

| Element | Spec |
|---|---|
| Place | A section of the LANDING single scroll, after the current-month band and its EARLIER. Never a view, never a state, never composed while searching. |
| Presence | Rendered **iff** ≥1 month qualifies (§5.2). Otherwise absent entirely — no heading, no hairline, no placeholder. |
| Composition | ONE LazyColumn item, `key = "months-shelf"`, containing a Column: heading block, then all rows. Landing `spacedBy(12.dp)` never lands inside it. |
| Heading block | Hairline 1dp `outlineVariant` → spacer 11dp → `MONTHS` (Anonymous Pro Bold 11sp, lh 14sp, ls 1sp, meta ink) → spacer 6dp. 18dp of break weight above the heading. Not tappable. No count, ever. |
| Row membership | Months **strictly before** the current filing month (03:30 rule), holding ≥1 stored task **or** ≥1 declared word, reverse-chronological as the repository returns them. Current month: never. Future month (tomorrow's word): never — §4.1. Empty month: never. |
| Row form — one per shelf | **Name-only:** 48dp. **Anchored:** 60dp, uniform, gated on §3.3's data spec. Never mixed in one shelf. |
| Row print — name line | `monthLabel` verbatim: month name + year, every row, every time. Anonymous Pro Bold 12sp, lh 16sp, ls 1sp, full ink (`onBackground`). |
| Row print — anchor line (anchored form only) | Newest stored task title of that filing month, **verbatim, stored case**, single line, ellipsis. Atkinson Hyperlegible Regular 13sp, lh 18sp, meta ink. No label, date or count. Null (word-only / defensive) → line unprinted, height unchanged. |
| Row geometry (anchored) | Pad top 11dp + name 16 + gap 3 + anchor 18 + pad bottom 12 = 60dp. Closing hairline = final 1dp inside the height. |
| Row geometry (name-only) | Container `height(48.dp)`, content centred, closing hairline = final 1dp inside the height. |
| Tap | Whole row. Opens MONTH for that year-month exactly as today (`onSelectMonth` + view change), standing under that month's own heading, EARLIER scoped within the month. Works identically **under seal** (reading). |
| Press | Violet wash `#493EA4`, 8% light / 22% night (via the existing `isDarkTheme` param), from `MutableInteractionSource` pressed state — no ripple. On in the press frame, 120ms fade on release. Row only; hairline drawn after, never tinted; print never recolours. |
| Motion | None besides the press wash. No entrance, no slide, no fade-in of rows. |
| Semantics | One node/row, role Button. Description: month name as printed; anchored: `"<NAME>, newest: <anchor>"`. Test tag `drawer_month_<year>_<month>`, unchanged from built. |
| Return | BACK from MONTH (and through DETAIL) restores the landing scroll at the tapped shelf row (offset arithmetic §4.5); floor: the shelf item's top. Never the landing's top by default. |
| Colour | Ink `#34323B` full / meta at ~60% (theme `onBackground` / `onSurfaceVariant`); hairlines `outlineVariant`; accent `#493EA4` for the press wash only. No other palette colour. **Never green or near-green**, including press/highlight states. |
| Type | Anonymous Pro (heading + name lines) and Atkinson Hyperlegible (anchor line) only. **No Sora** on this screen — acquitted absence, not an omission. |

### 5.2 Data the shelf may consume — the whole list

- `storedMonths()` as the repository already returns it: task
  months ∪ declared-word months, reverse-chronological — plus,
  in the anchored form, `anchorTitle` per §3.3.
- The current filing year-month, from the same live 03:30 ticker
  the landing already runs (DRW-05) — never a second frozen date.
- **Nothing else.** If a query returns a count, it is dropped
  before the UI: a count in the model is a count waiting to be
  printed. No last-opened dates, no SPENT sums, no task numbers
  enter the shelf's row model.

### 5.3 Builder diff checklist (shelf only — small on purpose)

1. `DrawerScreen.kt`, LANDING branch: replace the heading item +
   per-row `items(earlierMonths)` with the single
   `item(key = "months-shelf")` Column of §5.1; change the
   membership filter to strictly-before-current (§4.1); re-voice
   the row per §5.1 (Anonymous Pro, fixed height, in-row
   hairline, press wash via `InteractionSource`, `isDarkTheme`
   alpha step); keep `onSelectMonth` / view-change wiring and
   the test tags byte-identical.
2. Anchored form only: `MonolithModels.kt` — `StoredMonth`
   gains `anchorTitle: String? = null` (default keeps every
   existing constructor honest); `MonolithDao.kt` —
   `getStoredMonths` picks the anchor correlatively in the same
   query with the same −12600s filing shift; `MonolithRepository
   .kt` — the union passes the anchor through on task months and
   `null` on word-only months. **No schema change, no migration,
   DB version untouched.** If the correlated pick cannot be
   expressed in Room as specced, ship the name-only form and
   leave the anchor for a later pass — do not improvise an N+1.
3. Landing scroll restoration (§4.5) rides the host's landing
   `LazyListState`; no new store, no new ViewModel state.
4. Tests to add beside `DrawerWave2Test`: shelf membership
   (future word-month excluded, word-only month included,
   current month excluded), uniform row height in the anchored
   form incl. a word-only row, sealed shelf tap opens MONTH,
   return-offset lands on the tapped row. Declared, unrun —
   nobody compiles.

### 5.4 Kill list — what must NOT appear on the shelf

Any one of these in a render or a diff kills the pass, no
hearing: a count, total, SPENT sum or task number on any row or
on the heading · a chevron, arrow or `›` · a year sub-heading or
any grouping · a calendar grid · pagination, EARLIER or "show
more" on the shelf · a greyed or placeholder row for an empty
month · a future or current month row · per-row heights mixed
in one shelf · a ripple · a card, border loop, chip or raised
container around the shelf or any row · any reference to the
cabinet, curios, forging or artifacts (no row, no notice, no
hole) · any green or near-green, in any state · Sora.

### 5.5 Acceptance — the shelf, end to end (A1–A10)

1. **A1 · First glance.** Desaturated landing render: shelf
   labels, slip titles and meta lines separate by family,
   weight and tone alone. A month row cannot be read as a task.
2. **A2 · Pitch.** 12+ shelf rows measure at one uniform height
   (48 or 60dp) to the dp, hairlines inside the measure.
3. **A3 · Two taps.** From the landing, any earlier month's task
   is reached in 2 taps (shelf row, task row) — the corridor's
   3–4 stay dead.
4. **A4 · Press.** Light and night: press washes one row violet
   (8% / 22%), on in the press frame, 120ms fade out, hairline
   untinted, neighbouring rows untouched.
5. **A5 · Year on every row.** A fast scroll past a year seam
   (DECEMBER → JANUARY across years) is unambiguous at every
   single row, mid-scroll, without stopping.
6. **A6 · Anchor truth.** The anchor under a month is that
   filing month's newest COMPLETED task, verbatim — checked
   against the store for a month whose newest task finished at
   01:00 (filing month ≠ calendar month; anchor and day group
   agree).
7. **A7 · Sealed.** Under seal: shelf rows press, wash and open
   their months; reading is uninterrupted.
8. **A8 · Seam night.** At 23:00 on the last day of a month,
   file ILL for tomorrow: no future month appears on the shelf;
   the filing zone shows the word; after that month ends, its
   row shelves in true chronological place.
9. **A9 · Word-only.** A month with only a declared word shelves
   at full uniform height and opens a MONTH where the word
   prints. A month with nothing shelves not at all.
10. **A10 · Return.** Open the 3rd shelf month, BACK: the tapped
    row is at the viewport top. Repeat via a task's DETAIL: same
    landing place.

### 5.6 What this pass deliberately did not do

- **No cabinet home, no cabinet trace.** The shelf was designed
  and improved as if the cabinet had never been a Drawer
  resident — because for the man opening this screen, it no
  longer is. Its future is issue #21's parked decision; nothing
  here spends it.
- **No INDEX resurrection in any costume** — no contents view, no
  "all months" screen behind a tap, no paginated shelf. The
  corridor stays dead; the shelf is the landing's own furniture.
- **No new verb.** The shelf's only action remains opening a
  month. No sort, no jump, no search field of its own (the
  room's search line already serves it), no gesture layer (the
  Trawl stays a priced proposal for the looks programme).
- **No Kotlin written, nothing compiled.** This is the improved
  spec; the builder's diff follows §5.3.

### 5.7 For Lade to ratify (small, named)

1. Finisher-3 §4.5's word-only correction — adopted here as
   load-bearing (§4.4 E3). The built repository already unions
   word months; this pass asks only that the *presentation* law
   catch up with the data law.
2. The anchored form's gate (§3.1): anchor ships only on the
   single-query data spec, else name-only ships. If Lade prefers
   the name-only form unconditionally, §3 falls away cleanly and
   Rounds 1, 2, 4, 5 stand untouched — the rounds were built to
   survive losing the rider.
3. The strictly-before-current membership rule (§4.1) — a
   one-expression change with a nightly-visible consequence at
   month seams; flagged rather than buried in the diff.

— REGAL, Improver (#8 shelf). Rounds 1–5 complete: G1–G8 each
closed by a named round, the consolidated spec at §5, acceptance
at §5.5. The shelf that replaces INDEX is now, on paper, what the
audit ordered and the build only sketched: an address list in the
room's own voice, at an exact pitch, that answers a press, keeps
its silence about absences, never lies about which months are
earlier — and says nothing, anywhere, about a cabinet.
