# FINISH — SEARCH LINE & SHELF ROWS TO GALLERY STANDARD (issue #8)

Finisher 3 — Search & Shelf (issue #8 cell). Sources of law, in
order: `studio/plan/AUDIT-drawer.md` (Lade's verdict, §2 / §6
binding), `designer-drawer.md` (the floor), `innovator-drawer.md`
(P2 Ghost Search, P3 Anchor Title — on the table, judged here).
Ground truth checked against the shipped code:
`DrawerScreen.kt` (capsule at the list head, INDEX month rows),
`MonolithDao.kt` (`getStoredMonths`, `searchStoredTasks`),
`MonolithRepository.kt` (`storedMonths`, `searchStoredTasks`),
`MonolithViewModel.kt` (`setDrawerQuery`), `Theme.kt` (ripple is
purged app-wide; indications draw nothing).

This file is a spec only. No Kotlin was edited, nothing compiled.
My surfaces: **(a) the search entry line**, including substep-hit
print and the empty line; **(b) the MONTHS shelf rows**, including
the press state. Everything else in the Drawer belongs to other
finishers; where my surfaces touch theirs, I name the seam and
stop.

---

## 0 · VERDICTS, UP FRONT

| Proposal | Verdict |
|---|---|
| Ruled entry line replacing the capsule (Audit §2, Designer §2) | **SHIP** — finished to buildable spec in §1–§2, with six finish deltas the floor did not state (§6) |
| Ghost Search (Innovator P2) as a replacement for the SEARCH view | **FAILS at gallery standard — KILL row-ghosting.** Five independent proofs in §3. Nothing of P2 ships in this pass; its one honest idea already exists in the ruled design |
| MONTHS shelf rows (Designer §3) | **SHIP** — finished in §4, with one correction to the floor (word-only months, §4.5) flagged for Lade to ratify |
| Anchor Title rider (Innovator P3) | **SHIP WITH CONDITIONS** — the recognition gain is real and every risk closes; seven binding conditions in §5. If any condition cannot be met, the rider dies and the Designer's name-only row ships untouched |

Gallery standard, operationalised for these two surfaces: every
dimension sums, every state is named, every printed character is
a stored fact, a 1 AM thumb and a screen reader both survive it,
and nothing on the surface needs a second surface to explain it.

---

## 1 · THE SEARCH ENTRY LINE — BUILDABLE SPEC

### 1.1 What dies, exactly (current code, for the Builder's diff)

The shipped field is a `Box`: 48dp, `RoundedCornerShape(12.dp)`,
`surfaceVariant` fill, 1dp `outlineVariant` border box, 12dp
inner horizontal padding (so typed text actually starts 24dp
from the screen edge — page padding 12 + box padding 12), with
the placeholder overlaid as a sibling `Text`. Placeholder:
Anonymous Pro 12sp / letterspacing 1sp / `onSurfaceVariant`.
Typed text: **Atkinson Bold 13sp / `onBackground`** — family,
weight, size and ink all change the moment he types. That
costume change is the finish defect the audit named; the capsule
body around it is the heaviest object on the screen. Both die.

### 1.2 Geometry

- Container: `Column`, full content width (the LazyColumn's 12dp
  page padding is the only horizontal inset — **text starts at
  the page's own 12dp, flush with STORED, the filing dates, and
  every row title below it**). No inner horizontal padding.
- Field box: **44dp** tall, content vertically centred.
- Under it: one `HorizontalDivider`, **1dp**, full width. Total
  footprint 45dp. The divider is the line; there is no other
  border, no fill, no clip, no shape. `RoundedCornerShape`,
  `.background`, `.border`, `.clip` imports leave this composable.
- The line composes identically on LANDING and in SEARCH (and,
  as today, above MONTH — the room never changes costume between
  views; zero delta there).

### 1.3 Type — one costume, empty or typed

One `TextStyle`, used for the placeholder **and** the input:

- Anonymous Pro, **Regular** (the placeholder's shipped weight —
  the field does not get bolder when it fills), **12sp**,
  lineHeight **16sp**, letterSpacing **1sp**,
  `includeFontPadding = false` so placeholder and typed baselines
  are the same baseline, not two near-misses.
- Placeholder ink: meta — `scheme.onSurfaceVariant` (Designer:
  ink `#34323B` at ~60%). Placeholder print, verbatim:
  `TYPE TO FIND A TASK`.
- Typed ink: full — `scheme.onBackground`. Ink strength is the
  only difference between empty and typed; family, weight, size,
  spacing never move. That is what "keeping the costume" means,
  made testable.
- Construction: `BasicTextField` with a `decorationBox` that
  prints the placeholder when the value is empty. The shipped
  overlay-a-`Text`-behind-the-field construction is killed: two
  independently laid-out texts can drift by a pixel and will.
  One box, one baseline.
- No leading icon, no magnifier glyph, no trailing clear `×`.
  A glyph is stock furniture this room refused everywhere else,
  and a clear button is a new verb nobody ruled — **BACK clears
  the query** (§1.5). Nothing stands between the page edge and
  his first letter.

### 1.4 Colour & the violet

- Resting rule: the room's hairline — `scheme.outlineVariant`,
  the same token every divider on this screen already uses
  (acquitted system, §6). Do **not** introduce the Designer's
  literal 14%-ink alpha as a second hairline value in this
  slice; one hairline system per screen outranks a local match
  to a prose percentage.
- The one accent, per Designer §1: deep violet `#493EA4` — this
  room's ruled affordance colour, carried here as a named local
  value (the app scheme's orange accent does not enter the
  Drawer; the Designer ruled the subset and I hold it).
  - **Caret:** `SolidColor(#493EA4)`.
  - **Focused rule:** the 1dp rule turns `#493EA4`, full
    strength. Thickness never changes — a rule that grows to
    2dp on focus shifts every row below it by 1dp on every tap
    in and out; colour carries focus, geometry never moves.
  - **Text selection:** handle `#493EA4`, selection background
    `#493EA4` at 20% (the default selection colours borrow the
    scheme primary — near-black handles on a records line read
    as ink blots).
- Night / sealed scheme (the scheme flips under seal): `#493EA4`
  on the night ground is too dark to carry focus alone.
  Buildable rule, no new hue invented: in the dark scheme the
  caret and focused rule use
  `lerp(#493EA4, scheme.onBackground, 0.55f)` — the same violet
  stepped toward the room's own cream ink, exactly as the night
  scheme already warms its own accent one step. The resting rule
  and all inks stay scheme tokens, so they adapt untouched.

### 1.5 Behaviour & states

| State | Print & behaviour |
|---|---|
| Empty, unfocused | Placeholder at meta ink, hairline rule |
| Empty, focused | Placeholder stays (it is the field's name, not a hint that vanishes), caret violet at position 0, rule violet |
| Typing | Live search as today (`setDrawerQuery` cancels the prior job). **Finish delta:** on every query *change*, the previous query's result rows clear immediately — see below |
| Results landed | SEARCH view as ruled: day groups with year headings (acquitted), own EARLIER, substep second lines per §2 |
| No results | The empty line, §2.2 — printed **only** on a landed empty page |
| Sealed | Search **enabled** (Audit §2, resolved: a query mutates nothing stored). The shipped `enabled = !isSealed` dies. If seal doctrine ever forbids all input, the line is not composed while sealed — never the inert painted door that prints `TYPE TO FIND A TASK` and refuses it |

- **Stale-results defect, closed here because it is a false
  print in my surface:** today, typing a second character leaves
  the first query's hits standing under the new text until the
  new page lands — rows on screen that do not match the words in
  the field. Gallery rule: when the query text changes, results
  clear to *nothing* (no rows, no empty line) until the matching
  page lands; the VM already refuses stale pages
  (`_drawerQuery.value == query` guard) — the display must stop
  showing them too. Builder delta: clear `_drawerResults` /
  `_drawerSearchHasEarlier` in `setDrawerQuery` for a changed
  non-blank query. Local search lands in milliseconds; a brief
  blank is honest, a stale hit is not.
- **BACK:** SEARCH carries the header BACK in the one costume
  (other finishers own the header band; my requirement on it):
  tapping it **clears the query, clears focus** (keyboard drops —
  a cleared search that leaves the keyboard standing over the
  landing is half an exit), and returns to the landing *as he
  left it* (Designer §5 — landing scroll state is saveable and
  is not reset by a search round-trip). System back while
  searching does the same, one press, consumed inside the
  Drawer before any global ejection (Audit §2).
- **Keyboard:** `singleLine`, `ImeAction.Search` (no action
  fires — search is live — but the key must not promise a
  newline), no auto-capitalisation change to what he typed:
  the query prints back verbatim in the empty line (§2.2), so
  the field may not quietly restyle it.
- **Semantics:** the field carries
  `contentDescription = "Search stored tasks"`; the visible
  placeholder is print, not a label, and a screen reader meets
  a bare text field without one today.

### 1.6 A matching truth the Builder must not ship past

`searchStoredTasks` matches with SQL `LIKE '%' || :q || '%'`
over title / category / `notesOrSubsteps`. Unescaped, the
characters `%` and `_` in a query are wildcards: a query of `%`
returns the whole archive — rows that do not contain the
character he typed, i.e. results that lie about why they
matched. Finish delta (Dao/Repository layer, no UI change):
escape `\`, `%`, `_` in the trimmed query and add
`ESCAPE '\'` to the three LIKE clauses. A query is a literal
substring, always. This also keeps §2's reason-print honest:
any landed hit genuinely contains the query somewhere the row
can point to.

---

## 2 · INSIDE SEARCH — THE TWO RULED PRINTS, FINISHED

### 2.1 Substep hits print their reason (Audit §2, Fremd)

- For each result row, the Builder derives the matched step in
  Kotlin from the parsed model — `task.subSteps` in stored
  order — never by re-reading the raw column: the first step
  whose title contains the trimmed query, case-insensitive.
- Print the second line **only** when title and category do
  *not* themselves contain the query. If the reason is already
  visible on the row, a second line is noise; if the match
  lives only in a step, the step prints; if no parsed step
  matches (a match spanning serialisation artefacts, e.g. a
  query containing `[x]`), **no line prints** — never invent a
  reason (with §1.6 shipped, this residue is near-empty and
  always honest).
- The line: the step's title **verbatim** — no prefix, no
  `STEP:` label, no quotes, no case transform. Atkinson
  Hyperlegible Regular **12.5sp** (the detail screen's substep
  size — the same words at the same size in both rooms),
  meta ink (`onSurfaceVariant`), single line, ellipsis,
  seated between the title and the meta row, aligned to the
  title's start edge. Row grows by that one line only for rows
  that carry it.
- Scope: SEARCH result rows only. Landing and MONTH rows never
  gain this line — they were not found by anything.
- Test tag for the harness: `drawer_search_step_<taskId>`.

### 2.2 The empty line (Audit §2, Plan)

- Print, character-exact: `NO STORED TASK UNDER “<query>”` —
  curly quotes as the screen already prints them, the query
  inserted **verbatim as typed** (case preserved — this line
  quotes him; it may not shout his own words back in caps, so
  the string is composed, never uppercased by transform).
- Voice: Anonymous Pro 12sp, letterSpacing 1sp, meta ink,
  24dp vertical padding — the shipped empty line's placement,
  re-voiced from Atkinson 13sp into the room's display voice
  now that it is a records statement, not body copy.
- Post-eviction the promise and the territory match: nothing
  this screen stores sits outside search (the cabinet is gone),
  so the line is finally true for every query, curios' names
  included.

---

## 3 · GHOST SEARCH (INNOVATOR P2) — VERDICT: FAILS, KILLED

The proposal, fairly stated: typing narrows the room in place —
matches at full ink, non-matches receding to ~14% ghosts with
category / clock / SPENT stripped, pure-ghost day headings
receding with their rows, shelf rows lighting or receding by a
per-month contains-hit boolean, the SEARCH state dying, BACK or
clearing restoring full ink with scroll untouched.

It is the most seductive idea in the Innovator's file and it
fails at gallery standard on five independent grounds. Any one
would be enough to send it back; together they are a kill, not
a revision — because every fix below dissolves the mechanism
that was proposed.

1. **It re-opens a binding acquittal.** §6 acquits, by name,
   "search result grouping with year headings," and §2 rules
   SEARCH as a view that *gains* a BACK which clears the query.
   P2 kills that view and its grouping. A finisher cannot
   finish a proposal over a binding acquittal; that re-ruling
   belongs to Lade/Ren and was not sought. Shipping P2 as
   specced would make this cell the author of an unruled
   reversal.
2. **Its map lies by omission — the truth defect.** The landing
   holds only the current month, paged 50 at a time
   (`DRAWER_PAGE_SIZE`, EARLIER-gated). Ghosting can only judge
   *loaded* rows. A hit sitting in an unloaded EARLIER page of
   the current month renders its day all-ghost — the screen
   prints, in effect, "not here," when it means "not loaded."
   The Innovator's mitigation (the shelf boolean covers
   unloaded months) covers other months; it does nothing for
   the unloaded pages of the month he is standing in, which is
   the first place he looks. A finding surface whose negative
   is unreliable is worse than the database form it replaces:
   the form's result list is at least complete for its query
   (paged, with EARLIER honestly signalling more).
3. **14% is not a reading tier.** Title ink `#34323B` at 14%
   over the ground blends to roughly **1.3:1** contrast —
   below every legibility floor, large-text included. The
   Innovator intends ghosts as *seen-through slips*, not text;
   but a title is content, and at gallery standard content is
   either readable or absent. The middle state collides with
   two live meanings in this very app: *disabled* (the sealed
   search box he just escaped) and *deleted*. At 1 AM, a room
   full of apparently-disabled, apparently-deleted copies of
   his finished work is the opposite of the audit's sentence.
   Raising the ghost to a readable ~45–50% "fixes" it into a
   two-tier result list with worse ordering than the ruled
   one — the mechanism does not survive its own cure.
4. **Ghosts would dim records that are not search results.**
   Day headings on this screen carry declared day words — his
   authored filing, print that §6 acquits as a record. A
   pure-ghost day's heading "recedes with its rows" under P2:
   a search for a physics task would dim the day he filed
   ILL. Search may rank its own results; it may not de-ink
   the archive's filing. The same objection, smaller: stripping
   SPENT/clock from ghost rows deletes the per-row receipt the
   audit kept by name, from rows that remain tappable — or, if
   ghosts are *not* tappable, search silently changes what a
   row does without changing what it is called. P2 does not
   answer which; both answers fail.
5. **Semantics and the shelf seam are unbuildable as drawn.**
   Ghost rows remain in the semantics tree (a screen reader
   reads the full archive on every keystroke, ghosts included,
   unless they are cleared — and clearing them makes the
   *reader's* room differ from the sighted room), and the
   filing zone's behaviour under a query is unspecified — does
   REST / ILL / FAMILY ghost too? Finally, the shelf-light half
   of P2 describes furniture that is never on screen with the
   search field's results: in both the shipped code and the
   floor, an active query replaces the landing's bands — the
   shelf is not composed while searching. The "map of where a
   word lives in time" is a real desire, and the ruled design
   already answers it honestly: SEARCH results group under
   day headings **with years**, paged completely. Presence in
   time is printed by the headings of actual hits — records,
   not a boolean glow over months he has not opened.

**What ships instead:** the ruled SEARCH view, finished in
§1–§2 — complete, paged, reason-printing, BACK-exitable. The
speed P2 wanted is already in it: one field, live results, no
corridor.

**Reopen conditions** (all four, before anyone re-files P2):
Lade re-rules the §6 grouping acquittal; the ghost set is the
*complete* archive for the query, not the loaded page (a data
change with its own price); ghost ink is a readable tier with
declared-word headings exempt; semantics behaviour is specced
for a screen reader first, not last. Until then: **killed,
recorded, not to be rebuilt from this file.**

---

## 4 · THE MONTHS SHELF ROWS — BUILDABLE SPEC

### 4.1 Section geometry

- The shelf is a section of the landing's single scroll
  (Designer §3), never a view: heading `MONTHS`, then one ruled
  row per earlier stored month, reverse-chronological, nearest
  first, no year sub-headings, no pagination, no EARLIER — a
  list of names is never re-corridored (Designer §3.5 stands:
  ≤60 rows at five years).
- Heading: Anonymous Pro Bold, **11sp**, letterSpacing 1sp,
  meta ink — the day-heading family (10.5sp) stepped half a
  point up, because this heading sections the scroll rather
  than labelling a day. Above it, the section break: a hairline
  (`outlineVariant`), then 12dp, then the heading, then 6dp to
  the first row; the heading's top padding carries the audit's
  18dp shelf-break weight in total. Nothing about the heading
  is tappable.
- **Composition note for the Builder:** render the whole shelf
  — heading + rows — as **one LazyColumn item** containing a
  `Column`. The landing column's 12dp arrangement spacing
  otherwise lands between every pair of shelf rows and quietly
  re-pitches the shelf (see §4.2) into separated cards. Sixty
  two-line rows are a trivial composition cost; exact pitch is
  worth more than lazy-per-row shelf items. Scroll restoration
  (Designer §3.6 — return lands at the tapped row) is then
  `scrollToItem(shelfItemIndex, offset)` with the offset
  computed from the tapped row's position inside the shelf
  item: heading height + rowIndex × row pitch, in px. The
  requirement stands regardless of mechanism: a wrong month
  costs one BACK and the next row is under his thumb.

### 4.2 The row — floor form (name only, Designer §3.1)

- Height **48dp**; hairline (1dp, `outlineVariant`) under every
  row; the whole row is the tap target; text starts at the
  page's 12dp (no row-local horizontal padding).
- Print: the month name **including the year, on every row,
  every time** — `SEPTEMBER 2026` — Anonymous Pro Bold, 12sp,
  letterSpacing 1sp, full ink (`onBackground`). The string is
  composed already-caps from the shipped `monthLabel`
  (`MMMM yyyy`, uppercased); the year is never dimmed, dropped
  on "same-year" rows, or moved to a separator.
- **No trailing furniture, exhaustively:** no chevron, no `›`
  (the shipped INDEX row's grammar and the ALL MONTHS row's
  arrow both die with the corridor), no counts, no task
  numbers, no last-active dates, no SPENT sums, no "last
  opened." A count per month is a score per month (§6's
  no-count restraint from INDEX carries over in full — its
  kill was structural, never a licence to count on the shelf).
- Press state: **violet wash** — the row's background fills
  `#493EA4` at **8%** in the light scheme for exactly as long
  as the press lasts. Implementation is manual state, not
  indication: ripple is purged app-wide (`Theme.kt`), so the
  Builder drives the wash from the row's `InteractionSource`
  pressed state; wash on at press-down in the same frame, a
  120ms fade on release so the shelf answers like a physical
  surface rather than a light switch. In the night scheme the
  same violet at 8% is invisible against the dark ground:
  alpha steps to **22%** — same hue, perceptual parity, no new
  colour. The wash covers the row's full 48dp (60dp with the
  anchor, §5) and nothing else; the hairline under the row is
  drawn after the wash and never tints.
- Tapping opens MONTH exactly as today (Designer §3.1): that
  month's day groups under its own heading, EARLIER scoped
  within the month, BACK in the header costume whose parent is
  honestly LANDING.
- Semantics: one clickable node per row, role Button,
  content description = the month name as printed.

### 4.3 Which months stand on the shelf

- Earlier months only: the current filing month (03:30 rule,
  `DrawerDay.filingYearMonth`) never appears — it is band 4,
  live above. Builder filters `storedMonths()` against the
  current filing year-month.
- Absent shelf: if no earlier month qualifies, the section —
  heading included — does not render at all (Designer §3.4).
  No placeholder, no empty line. Furniture for an absence is
  the defect this whole pass keeps removing.

### 4.4 (Reserved — ordering and scale are the Designer's §3.2 /
§3.5 and stand unamended; nothing in finishing changed them.)

### 4.5 Finisher's correction to the floor — word-only months

Designer §3.3 rules the shelf lists "only months that contain
stored work." The data disagrees, deliberately:
`MonolithRepository.storedMonths()` unions task months with
**declared-word months** — a month holding only a filed word
(REST / ILL / FAMILY) and no finished task is still an address
in the index, *or the word would be filed nowhere* (the
repository's own stated reason). Killing such a row from the
shelf would orphan a record the audit acquitted as print.

**Correction:** word-only months **stay on the shelf**, in
their chronological place, in the single-line floor form
(§4.2) even when the Anchor Title ships (§5.3) — they have no
newest stored task, and no line is invented for them. This
deviates from Designer §3.3's letter to preserve §3.3's intent
(the shelf invents no entries — a declared word *is* an entry)
and the audit's filing law. **Flagged for Lade to ratify**; if
Lade instead rules word-only months off the shelf, he must
also name where their words remain reachable, or the ruling
deletes filed records by furniture.

---

## 5 · THE ANCHOR TITLE (INNOVATOR P3) — SHIP, WITH CONDITIONS

The mechanism: under each shelf month's name, one second line —
**the title of that month's newest stored task, verbatim** —
so a forty-row shelf is scanned by recognising his own work
instead of remembering its month. Judged against the three
tests (Designer §0): it prints no count, total, or grade — a
title is a record, not a measurement; it is pull-only, inside
a visit he chose, with no marker or summons; the screen's verbs
do not change. The failure modes are all in the finishing, and
all of them close:

### 5.1 Final row form (replaces §4.2 geometry where it differs)

- Uniform row height **60dp for every shelf row**, anchored or
  not. Variable heights would make the name column stutter
  down the shelf; a uniform pitch is what makes forty rows
  scannable, and the word-only row (§4.5) simply leaves its
  second line unprinted inside the same 60dp.
- Layout inside the 60dp: top padding 11dp; name line
  (Anonymous Bold 12sp, lineHeight 16sp, ls 1sp, full ink);
  3dp gap; anchor line (Atkinson Hyperlegible **Regular 13sp**,
  lineHeight 18sp, meta ink); bottom padding 12dp.
  11 + 16 + 3 + 18 + 12 = 60. In a word-only row the name sits
  at the identical top position — the scan line never moves.
- Anchor print rules, binding: the stored title **verbatim —
  never uppercased, never summarised, never generated, never
  rotated** through the month's titles (the newest only, so
  what surfaces is always the same predictable line). Single
  line, ellipsis at the row's width. "Verbatim" means
  *unedited until the ellipsis*: truncation by width is a
  layout fact, stated here so nobody later calls the ellipsis
  a broken promise — and so nobody "helpfully" shortens a
  title in the query instead.
- Press wash (§4.2) covers the full 60dp including the anchor
  line; the anchor's ink does not turn violet under press —
  only the ground washes.
- Semantics: the row's content description becomes
  `"<MONTH NAME>, newest: <anchor title>"` when an anchor
  exists; the visible print stays exactly two lines.

### 5.2 Data spec (the part P3 priced loosely — tightened here)

- `StoredMonth(year, month)` gains `anchorTitle: String?`.
- **One query, never N+1.** P3 allowed "one small query per
  month at shelf-build time"; at sixty months that is sixty
  round-trips to build a list of names. Killed. The months
  query returns, per filing month, the title from a correlated
  pick: the task row with the greatest
  `(completedAtMillis, id)` — the same total order every paged
  read on this screen already uses — within that month.
- **Filing month, not calendar month.** The grouping must use
  the identical 03:30 shift `getStoredMonths` already applies
  in SQLite (`(completedAtMillis / 1000) - 12600`), or a task
  finished at 01:00 anchors to the wrong shelf row while its
  own row files a month earlier. The anchor and the archive
  must never disagree about which month a task rests in.
- Only `status = 'COMPLETED'` rows anchor (the same filter as
  every Drawer read). Word-only months get `null` from the
  task side of the union — handled in §4.5/§5.1, never by a
  substitute string.
- The anchor is derived **in the Dao/Repository months read**
  (which `refreshDrawer` already triggers), never from the
  paged `drawerRows` — those hold one month, partially loaded,
  and an anchor computed from them would change as he pages.
  The anchor is a fact of the archive, stable for the visit.

### 5.3 Finish risks, each closed or carried

1. **Misdirection** — one title stands for a month of unlike
   work and he scans past the right month. *Carried, honestly:*
   true of every finding aid; the newest slip is the one most
   likely still warm, and search (§1–§2) is the route when
   anchors fail. The anchor advises; the name above it decides.
2. **A painful or private title surfaces without a tap.**
   *Carried per P3's own reasoning:* his finished work, on his
   locked app, one level shallower than it already sits — and
   the newest-only rule means it is never a surprise exhumed
   from deep in the month.
3. **The second line drifts into summary over time** ("a busy
   month of physics"). *Closed by §5.1:* stored title verbatim
   or nothing; any generated text in this slot kills the rider
   on the truth standard without a new hearing.
4. **Counting by the back door.** The anchor prints no number
   and implies none beyond "at least this exists" — the same
   implication the month row itself already makes. *Closed.*
5. **Shelf rhythm at scale.** *Closed by uniform 60dp* (§5.1)
   and single-item composition (§4.1).
6. **The seam at month boundaries under the 03:30 rule.**
   *Closed by §5.2's identical-shift requirement,* with the
   acceptance test in §7.
7. **Cost creep.** One query change + one nullable field + one
   text line: inside P3's half-day. If the correlated query
   cannot be expressed in Room as specced, the rider waits —
   the N+1 form is not an acceptable fallback (§5.2).

**If any of conditions §5.1–§5.2 cannot be met as written, the
Anchor Title does not ship in a weakened form.** The floor's
name-only 48dp row (§4.2) is already gallery-grade; a rider
that ships half-true is worse than no rider.

---

## 6 · DELTAS SUMMARY — FOR LADE / THE BUILDER

Against the shipped code and the floor, my slice changes:

1. **Search box → entry line** (`DrawerScreen.kt`): capsule
   `Box` (48dp, 12dp radius, fill, border, 24dp effective text
   inset, overlaid placeholder) replaced by the §1.2–§1.4
   construction: 44dp field + 1dp rule, decorationBox
   placeholder, one Anonymous 12sp costume for placeholder and
   typed text, violet caret / focused rule / selection.
2. **Sealed search enabled**: `enabled = !isSealed` removed
   (§1.5). Never the painted door.
3. **BACK in SEARCH clears query + focus** and restores the
   landing scroll position (§1.5). System back identical.
4. **Stale results cleared on query change** (ViewModel,
   §1.5) and **empty line gated on a landed empty page**.
5. **LIKE escaping** in search matching (§1.6) — `%` / `_` /
   `\` literal, `ESCAPE '\'`.
6. **Substep reason line** on search hits (§2.1) and the
   **re-scoped empty line** `NO STORED TASK UNDER “X”` in the
   room voice (§2.2).
7. **INDEX + ALL MONTHS die; MONTHS shelf is built** as one
   section of the landing scroll (§4.1–§4.3): name + year on
   every row, 48dp, hairline, violet-wash press (8% light /
   22% night), no chevron / counts / dates, current month
   excluded, absent when empty.
8. **Word-only months stay on the shelf** (§4.5) — deviation
   from Designer §3.3, flagged for Lade's ratification.
9. **Anchor Title ships under §5's conditions**: uniform 60dp
   rows, verbatim newest-task title, `StoredMonth.anchorTitle`,
   one filing-month-true query, no N+1.
10. **Ghost Search (P2) does not ship** (§3): no in-place
    ghosting, no shelf boolean, SEARCH view and its year-headed
    grouping stand as ruled.

Explicitly *not* in my deltas: no cabinet home (issue #21),
no Kotlin written, no counts anywhere, no new verb on the
screen, no gesture layer (P5 / the Trawl remain the looks
programme's hearing, untouched by this file).

---

## 7 · ACCEPTANCE — HOW LADE CHECKS THE FINISH

1. Type one character: the field's costume does not change —
   same face, size, weight as the placeholder; only the ink
   deepens. Caret and rule are violet; nothing on the screen
   moves by a pixel on focus in or out.
2. Type a query, delete a character, type another: at no frame
   do rows stand under a query they do not match; the empty
   line appears only when a completed search found nothing,
   and reads `NO STORED TASK UNDER “<exactly what he typed>”`.
3. Search a word that lives only in a substep: the hit prints
   that step, verbatim, as its second line. Search `%`: the
   archive does not come back — only titles containing a
   literal `%` (in practice, the honest empty line).
4. BACK from search — header and system, both: query cleared,
   keyboard down, landing restored to the shelf row he left.
5. Sealed: the search line takes typing and answers it. There
   is no state of this screen where the line invites and
   refuses.
6. Shelf at a glance: every row carries its year; no row
   carries anything else but (with the rider) one stored
   title. Press a row: violet wash, that row only, gone
   cleanly on release — in light and in the night scheme.
7. The 03:30 seam: a task finished at 01:00 files in the
   previous filing month everywhere — its day group, its
   shelf row, and its anchor candidacy agree.
8. A word-only month (declare ILL in a month with no finished
   task, nothing else): its row stands on the shelf, one line,
   same pitch, and opens a MONTH where the word is filed.
9. A month whose newest title is longer than the row: one
   line, ellipsis, row still 60dp, the month below unmoved.
10. Screen reader: the field announces as search; each shelf
    row announces as its month (plus its anchor, §5.1); no
    ghost state exists to be misread.

— Finisher 3 — Search & Shelf (issue #8 cell)
