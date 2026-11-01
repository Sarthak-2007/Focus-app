# FINISH — THE DRAWER'S STATES & EDGE CASES (issue #8)

Finisher 5 — States, in LADE's cell for issue #8. Source of law:
`studio/plan/AUDIT-drawer.md` (Lade's verdict; §6 acquittals bind,
nothing below re-opens one). Floor design:
`studio/work/issue-8/designer-drawer.md` — this file finishes only
what the floor leaves unfinished: the states nobody designs.
Evidence: `DrawerScreen.kt` read whole (line numbers below are
that file, current tree), plus `MainActivity.kt` (BackHandler,
DrawerScreen call site) and `MonolithViewModel.kt` (Drawer flows).
No Kotlin written or edited. Nobody compiled anything.

Lade's sentence governs every state, including the broken ones:

> "The Drawer is where finished work rests — findable in seconds,
> readable without being judged."

A state is finished when a man meeting it at 1 AM can tell, in
one glance, *where he is, what exists, and what the one available
gesture does* — and when nothing in it prints, implies, or
affords something false. Silence, refusal, and restoration are
print too. They get the same gallery standard as the shelves.

---

## 1 · EMPTY — SILENCE THAT READS AS CALM, NOT BROKEN

### 1.0 The law

An absence is never given furniture: no box, no icon, no
illustration, no coaching line, no call to action (a "finish a
task to see it here" line is a summons, and storage asks nothing —
§6). Absence is handled exactly two ways in this room, and the
Builder uses no third:

- **Print**, where an acquitted line exists for the whole room
  being empty: `NOTHING STORED YET` (§6, Plan, acquitted).
- **Silence**, where a band within a living room is empty: the
  band renders nothing — no heading, no line, no spacer beyond
  the standard section rhythm — and the living bands around it
  prove the room works.

A heading exists over a day **iff** that day holds ≥1 stored row
**or** a declared word. A declared word is a stored fact, so its
heading is a record, not an absence shelf; an undeclared, rowless
day prints nothing. (Today's forced heading dies per §2 of the
audit in every state below, sealed or not — its ask now lives in
the filing zone, Designer's §4.)

### 1.1 E0 — the whole archive is empty

Trigger (post-fix definition, see the defect in 1.5): no stored
months, no stored rows in any month. Composition, top to bottom,
and nothing else:

1. Header: BACK costume + `STORED`.
2. Search line (44dp entry line, Designer's §2) — live, unsealed;
   sealed handling per §2 of this file. An empty archive is still
   searchable furniture; the line is not hidden "because there is
   nothing to find" — hiding and re-adding it later would make
   the room change shape the day the first task lands.
3. Filing zone: today + tomorrow lines, exactly as Designer's §4
   (undeclared words in `#493EA4`, or a declared word retired to
   full-ink print). **The filing zone stands even here** — a sick
   day in the first week, before anything was ever finished, is
   exactly when a word needs declaring; the current code already
   honours this inside the empty branch (today's `DayHeading`
   under the empty line) and the finish keeps the behaviour in
   the new furniture.
4. `NOTHING STORED YET` — Atkinson Hyperlegible 13sp, meta ink
   (`#34323B` ~60%), 24dp vertical padding, starting at the page's
   12dp. Left-aligned with everything else; never centred,
   never enlarged. It is a shelf label for an empty room, not a
   hero message.
5. Then the 100dp bottom padding. No MONTHS heading (Designer's
   §3.4: no shelf heading over an empty shelf), no EARLIER, no
   CURIOS row (evicted, §5 below — it stands in today's empty
   branch too, line 737, and dies there as well).

Calm test: the screen's rhythm — header, line, two filing lines,
one quiet line, air — is the same rhythm as the populated room
with its shelves removed, not a different "empty screen." When
the first task is stored, the only change is that day groups
appear where the empty line stood; no furniture moves, appears,
or changes costume.

### 1.2 E1 — nothing stored, but a day word is declared

This is a **live defect in the current code**, verified:
`globallyEmpty = storedMonths.isEmpty() && dayWords.isEmpty()`
(line 185). Declare REST on day one and `globallyEmpty` flips
false with the archive still empty — the landing falls into the
"populated" branch and prints an `ALL MONTHS` row over an empty
index and a CURIOS row, over zero stored work.

Finish: emptiness is a fact about **stored work only**. The
empty test is `storedMonths.isEmpty()` (equivalently: no stored
rows exist); `dayWords` never enters it. E1 then renders exactly
as E0, with one difference: the declared filing line stands in
the filing zone as print (and, if the declared day is an earlier
day of the current month, its heading prints per 1.0 — a record
with no rows under it, word beside date, nothing else). The
`NOTHING STORED YET` line still prints: a declared word is not
stored work, and the line remains true.

### 1.3 E2 — current month empty, earlier months hold work

The audit's acquitted case (§4: Dens + Plan over Muede's "silence
reads broken"; the shelf is the answer). Composition:

1. Header, search line, filing zone — as E0.
2. **Current-month band: total silence.** No heading for today
   (killed), no heading for the month, no absence line, no
   collapsed placeholder, no extra void. The band contributes
   zero height beyond the standard gap between the filing zone's
   hairline and the shelf's section break.
3. MONTHS shelf (Designer's §3): `MONTHS` heading with its full
   section break (hairline over it, 18dp shelf break), then one
   ruled row per earlier stored month, nearest first.

Why this reads calm and not broken, stated for the Builder so he
does not "help": the named months are real work, in his own
titles' room, one scroll below the silence. The eye reads
"nothing this month — and here is everything" in one pass. Any
print inside the silent band ("NOTHING THIS MONTH") would be a
second, smaller empty-state competing with the shelf and would
re-open an acquitted ruling. The gap's size is the discipline:
it must be the **same** section rhythm as a populated landing
(filing hairline → 18dp break → next heading), never a stretched
void that makes the shelf feel like it fell down the screen.

Sub-case, same finish, no special pleading: the month has just
turned (the 1st, or the 03:30 seam) and the current month is
empty *because it is new*. Nothing prints about newness. The
shelf's first row is last month; the room does not explain the
calendar.

### 1.4 E3 — a month view with nothing in it

Post-shelf, MONTH is only reachable from a shelf row, so its
month holds ≥1 row by construction. It stays specified because
two doors can still land here: process-death restore of a month
whose rows fail to load (§3.5), and a stale VM month. Finish:
MONTH renders its honest header (month name + header BACK) and
silence below — no absence line, no EARLIER, no auto-bounce to
LANDING. A man who was sent back unasked has been moved by the
app; a man standing in a quiet month with a working BACK has
not. If the rows arrive (async load), they fill under the header
without the header moving.

Search-empty is **not** in this section: it is a search state,
finished at §4.4 / Designer's §5 — re-scoped line
`NO STORED TASK UNDER "X"`, query left live and editable, no
"try another word" coaching, BACK present. It borrows this
section's law (one true line, no furniture for an absence) and
nothing else.

### 1.5 Builder checklist — empty

- [ ] Empty test reads stored work only; `dayWords` removed from
      it (line 185 defect).
- [ ] Today's forced heading gone in every branch; a day heading
      prints iff rows ≥1 or a word is declared.
- [ ] E0/E1: filing zone composed and live above the
      `NOTHING STORED YET` line; no MONTHS heading, no EARLIER.
- [ ] E2: current-month band contributes no composable at all;
      shelf section break identical to the populated case.
- [ ] No empty-state illustration, icon, CTA, or coaching text
      anywhere in this section's states.

---

## 2 · SEALED — READING IS FREE; NEVER A PAINTED DOOR

### 2.0 What the seal does today (verified inventory)

| Surface | Today under seal |
|---|---|
| Stored rows / DETAIL | Read fully; rows tappable, DETAIL opens, BACK works — **acquitted** (Muede, Vega, §6) |
| Search field | **Composed but `enabled = !isSealed` (line 556)** inside the capsule, placeholder `TYPE TO FIND A TASK` (line 546) still printing — the painted door itself |
| Filing words | Declaration row suppressed (`isToday && !isSealed`, `DayHeading`); a declared word still prints beside its date as print; an undeclared day shows nothing — refusal by disappearance |
| Today's group | `includeTodayGroup = !isSealed` (lines 685, 789): an undeclared, rowless today vanishes from the landing entirely under seal |
| EARLIER | Live — paging is reading; correct, keep in both variants |
| Cabinet rows/detail | Read fully (travels out with the eviction, §5; noted so its new home inherits "reading is free") |

And the file's own header comment still states the old doctrine
("search input and every edit are inert") — whichever variant Ren
rules, that comment is rewritten to match; a comment that states
the losing doctrine is a false print in the source.

**The one law both variants obey:** nothing on a sealed Drawer
may *look* operable and refuse. No disabled-but-visible input, no
violet word that does not answer, no greyed imitation of a live
control. A thing under seal is either **live** or **print** or
**absent**. There is no fourth costume. (This is Designer's §4
rule for filing words, generalised to the whole room.)

### 2.1 VARIANT A — "THE EYES AND THE EPHEMERAL ARE FREE"
*(the audit's resolved position, §2: search enables under seal)*

Doctrine in one line: **the seal freezes stored records, not
looking.** Anything that mutates nothing stored is live; anything
that writes a record is print or absent.

- **Search: composed and fully live.** The entry line exactly as
  unsealed (44dp, 1dp bottom rule, Anonymous 12sp for placeholder
  *and* typed text, caret and focus rule in `#493EA4`). Typing
  runs the same VM query; results group, page with EARLIER, and
  open DETAIL identically; a substep hit prints its reason line;
  the empty line is `NO STORED TASK UNDER "X"`. The query is
  ephemeral VM state — it is never written to any store — and
  that fact, not a permission, is why it is free. SEARCH's BACK
  clears the query (§4.3). Keyboard, focus, and IME behaviour are
  indistinguishable from unsealed: a sealed search that behaves
  *almost* like search is a painted door with better manners.
- **Filing words: print only (finish F-PRINT).** In the filing
  zone, a declared line prints exactly as its retired unsealed
  form — date + word in full ink `#34323B`, no violet, no tap
  target (the whole line's touch area removed, not disabled). An
  undeclared line prints its **date only**, full meta ink, no
  words standing beside it. The three words are not composed —
  not greyed, not struck, not hinted. A man who has never seen
  the unsealed room cannot tell anything is missing, and a man
  who has sees print where print belongs: the zone reads as a
  record of the two days, which under seal is what it is.
- **Reading surfaces:** rows, DETAIL, EARLIER, shelf, MONTH —
  all live, all identical to unsealed. DETAIL keeps BACK as its
  only verb. Scroll positions behave normally.
- **Empty combos:** sealed E0 prints `NOTHING STORED YET` with
  the filing zone in F-PRINT form; sealed E2 keeps its silent
  band and shelf. Seal changes no emptiness ruling.
- **Cost of this variant:** delete `enabled = !isSealed` from the
  search field; gate the filing words' composition on seal
  instead of their clickability. The smallest change of any
  variant — it mostly *removes* seal branches.

### 2.2 VARIANT B — "NO INPUT UNDER SEAL"
*(the audit's fallback: if Ren's seal doctrine forbids all input,
the field is not composed while sealed at all)*

Doctrine in one line: **a sealed room contains no input surface
of any kind.** Nothing that accepts a keystroke or files a record
exists on the screen; what remains is pure reading furniture.

- **Search: not composed.** No entry line, no placeholder, no
  disabled ghost, no "search is unavailable" line, no substitute
  furniture of any kind. The landing's first band after the
  header is the filing zone (in F-PRINT form, below). The 44dp
  the line occupied is **not** held open — the room closes up
  cleanly, because a held gap is the painted door drawn in
  negative space. Consequence, stated plainly so the call is
  priced: while sealed, finding is by scroll and shelf only;
  "findable in seconds" (§2's charge) is knowingly traded away
  for doctrine purity, and search returns the moment the seal
  lifts, with the query it had (§3.4).
- **Filing words: print only (F-PRINT), identical to Variant A.**
  Declared = date + word in full ink; undeclared = date only.
  The filing treatment deliberately does **not** differ between
  variants: filing writes a stored record, so it is frozen under
  either doctrine, and Ren's call stays a one-sentence call
  about search alone.
- **Reading surfaces, empty combos:** identical to Variant A in
  every particular. The *only* delta between A and B, anywhere
  on the screen, is whether the search line is composed.
- **Cost of this variant:** one composition gate on the search
  item (`if (!isSealed)`), plus the scroll-anchor care in §3.4
  (a list whose top band appears/disappears at seal lift must not
  jump under his thumb — anchor the list by item key, not by
  pixel, across the transition).

### 2.3 The filing swap — F-LIVE, spec'd so it costs one line to adopt

If Ren rules the day words **exempt** from the seal — the honest
argument exists: a word files the *day*, not a task; Muede's
errand (Drawer → ILL → sleep, two taps, at 01:00, quite plausibly
while a session seal stands) is the audit's own test — then the
finish is: filing zone under seal is **byte-identical to
unsealed** (words in `#493EA4`, each its own ≥44dp target,
declared line retiring to full-ink print; tomorrow fileable the
same night). It pairs with either variant above without touching
search. Default recommendation carried with this spec: **A +
F-PRINT** is the consistent pairing (the audit freed search
*because* it mutates nothing stored; a word mutates a stored
record). F-LIVE is finished and priced if Ren's doctrine exempts
the day's diary from the task store — but the exemption must be
his sentence, not the Builder's drift.

### 2.4 Builder checklist — sealed

- [ ] No `enabled = false` input remains anywhere on the screen;
      a control is composed-live or not composed.
- [ ] Variant A: search path contains zero `isSealed` checks.
      Variant B: search item gated at composition; no height held.
- [ ] Filing zone: sealed form removes tap targets entirely
      (F-PRINT) or is identical to unsealed (F-LIVE if ruled);
      never violet-but-inert words.
- [ ] `includeTodayGroup` no longer keyed to `isSealed` (lines
      685, 789): today's group follows the §1.0 heading law in
      both seal states; the filing zone, not the group, carries
      the ask.
- [ ] Header comment rewritten to the ruled doctrine.
- [ ] Rows, DETAIL, EARLIER, shelf, MONTH navigation verified
      live under seal — reading is free is an acquittal, not a
      hope.

---

## 3 · PROCESS DEATH — WHAT RESTORES, WHERE, EXACTLY

### 3.0 The current exposure (verified)

Everything that says *where he is* in the Drawer is bare
`remember`: `view` (line 164), `selectedTask` — a whole
`FocusTaskEntity` object (line 165), `cabinetArtifact` (line
166). The three `LazyColumn`s declare **no `LazyListState` at
all**, so even scroll position is unowned. The VM's Drawer flows
(`drawerMonth`, `drawerRows`, `drawerQuery`, `drawerResults`,
offsets) are plain `MutableStateFlow`s — no `SavedStateHandle`
exists in `MonolithViewModel` — so process death also loses the
query and the open month at the VM layer, and `refreshDrawer()`
then resets the month to the current filing month on the next
VAULT open. Net effect, the audit's charge: a man three shelves
deep at 1 AM wakes up on the landing, query gone, place gone.
The *records* are never at risk — stored tasks live in the
database forever (DrawerPermanence) — only his place is.

### 3.1 The formal state type (one type, IDs only)

The Builder introduces exactly one navigation type for this
screen, replacing `view` + `selectedTask` + `cabinetArtifact`:

```kotlin
sealed interface DrawerNav {
    data object Landing : DrawerNav
    data class Month(val year: Int, val month: Int) : DrawerNav
    data class Detail(val taskId: Long, val origin: DrawerNav) : DrawerNav
}
```

Rules of the type:

- **DETAIL is a named state of this type** — never again a
  shadow early-return keyed off a nullable object. `origin` is
  the state BACK returns to and the state restored *beneath* the
  detail; origin is never itself a `Detail` (one level only —
  DETAIL's only verb is BACK, so no detail-on-detail can occur).
- **IDs, never objects.** The detail carries the task's `id`
  (`Long`) and is re-resolved against the repository on every
  composition/restore. No `FocusTaskEntity`, no `KineticArtifact`,
  no row object is ever placed in saveable state (they are not
  saveable, and a stale object is a stale fact).
- **SEARCH is not a member of the type.** Searching stays what
  the code already derives it as — `drawerQuery.isNotBlank()` —
  but the query becomes saveable (§3.3), and the state *beneath*
  the search is the current `DrawerNav` (`Landing` or `Month`),
  which search overlays and BACK restores. This keeps exactly one
  source of truth for "where": the type; and one for "what is he
  finding": the query.
- **INDEX and CABINET are not members.** They are dead (§5); a
  saved state naming either, met after an upgrade, falls back per
  §3.5 rather than resurrecting a killed view.

The type is held in `rememberSaveable` with an explicit saver
(the house already has the pattern: `rememberSaveable(saver =
LazyListState.Saver)` in `ShieldMatrixScreen`). One
`LazyListState` per list surface (landing, MONTH, search results,
DETAIL), each `rememberSaveable`-held the same way.

### 3.2 What is saved — the complete bundle, nothing more

| Saved | Form | Why |
|---|---|---|
| `DrawerNav` | type above | where he is |
| Search query | `String` | what he was finding |
| Per-surface scroll | `LazyListState` (first visible item **key** + offset) | where his eye was |
| Loaded depth per paged surface | row count loaded via EARLIER (landing / month / search) | EARLIER pages exist only in memory; without depth, scroll restore points at items that are not loaded |
| Shelf anchor on landing | the month-row key he last tapped into, if any | Designer's §3.6: return lands at the shelf row he tapped |

**Explicitly not saved:** `isSealed` (derived live from session
state — the seal at restore is whatever is true *now*, and §2's
variant applies to the current seal, not the one at death);
`today` (re-derived by the DRW-05 03:30 ticker); `dayWords`,
rows, results (re-read from the repository — the database is the
record; saving copies of records is how stale facts are born);
keyboard-open / text-field focus (restore never force-opens the
IME or steals focus); press states; the cabinet anything (§5).

### 3.3 Restore, state by state — where each piece lands

- **LANDING.** Nav restores as `Landing`. The VM re-reads the
  current filing month's first page, then re-pages EARLIER until
  the saved loaded-depth is reached (bounded by `hasEarlier`;
  if the archive shrank — it cannot, permanence — depth simply
  stops at the end). Scroll restores by item key: the saved
  first-visible key (a day-group date key or a shelf month key)
  is scrolled to with its saved offset; if the key is somehow
  absent after full re-paging, scroll falls to the top — a plain
  landing, never a crash, never a blank list.
- **MONTH(y, m).** Nav restores as `Month(y, m)`. Restore must
  **win over `refreshDrawer()`'s reset**: the VM applies the
  saved month after (or instead of) the refresh's
  current-month reset — the Builder sequences this so the reset
  never overwrites a pending restore, because a restore that
  lands on the current month is LANDING with extra steps.
  Rows re-page to saved depth; scroll restores by key as above.
  If `(y, m)` no longer holds any stored row (unreachable via
  the shelf; reachable via a stale save), §1.4 applies: honest
  header, silence, working BACK.
- **SEARCH (query over Landing or Month).** The query restores
  into the VM (`setDrawerQuery(saved)`) and the search re-runs —
  results are re-derived, never resurrected from a saved list —
  re-paged to saved depth, scroll by key. The `DrawerNav`
  beneath restores first, so SEARCH's BACK (§4.3) and a
  DETAIL-over-search both have their true floor. Sealed note:
  if the app died unsealed mid-search and restores sealed,
  Variant A shows the restored query live in the field;
  Variant B does not compose the field but **keeps the query
  saved** — lifting the seal later in the visit returns his
  search intact rather than punishing him for the seal's timing.
- **DETAIL(taskId, origin).** Origin restores fully (above),
  then the task is re-resolved **by ID** from the repository and
  the detail composes over its origin. If the ID resolves to
  nothing — impossible under permanence, specified anyway — the
  detail does not compose: he lands on the restored origin at
  its restored scroll, with no error print (the Drawer does not
  announce machinery). No empty detail, no placeholder skeleton:
  while the ID resolves, the origin stands; the detail replaces
  it when the record arrives. At 1 AM this is a blink, not a
  loading screen — no spinner is composed anywhere in restore.
- **Filing zone / seal / today** re-derive live on the restored
  screen; nothing about them is restored because nothing about
  them is navigation.

### 3.4 Death's neighbours

Configuration change is the cheap case and must not regress:
the VM survives it, `rememberSaveable` covers the rest — same
bundle, same restore path, no second mechanism. Death *while
sealed*, death *mid-typing* (query saved as of the last VM
update; a half-typed word restores whole — the query saves on
change, not on submit), death *on the DETAIL of a search hit*
(origin = the nav beneath the search, query saved alongside —
BACK from the restored detail returns to the restored results,
query intact, per §4.3).

### 3.5 Builder checklist — process death

- [ ] `DrawerNav` sealed type introduced; `view`,
      `selectedTask`, `cabinetArtifact` bare `remember`s deleted
      (lines 164–166); DETAIL a named state carrying `taskId`.
- [ ] Query + nav + depths + scroll states in `rememberSaveable`
      / saved-state; zero entity objects in any saver.
- [ ] Restore sequencing beats `refreshDrawer()`'s month reset.
- [ ] Unknown/legacy saved values (INDEX, CABINET) decode to
      `Landing` — never crash, never resurrect.
- [ ] Kill-and-restore test per state: LANDING (deep, post-
      EARLIER, shelf anchor), MONTH, SEARCH-over-MONTH,
      DETAIL-from-search — each returns to view, month, task,
      query, and scroll key.

---

## 4 · BACK — ONE COSTUME, TRUE PARENTS, ONE STEP PER PRESS

### 4.0 The current census (verified; the audit's charge, priced)

Four BACK controls in two costumes, three destination rules, and
**no system-back handling inside the Drawer at all**:

| # | Where | Costume today | Goes to |
|---|---|---|---|
| 1 | DETAIL (line 238) | header row, right side, Atkinson bold 12sp text, bare `clickable` — no padding, a ~12sp-tall target | `selectedTask = null` — the view underneath, whichever it was |
| 2 | CABINET header | same header costume | list → LANDING; detail → two-step pop (artifact, then list) — *travels out with the cabinet, §5* |
| 3 | INDEX (line 623) | `DrawerNavRow` — full-width left-aligned text row, 6dp vertical padding, sits as a list item, not in a header | LANDING |
| 4 | MONTH (line ~668) | header costume | LANDING **plus** `onSelectMonth(currentYearMonth)` (line 675) — a silent VM-month reset riding on a BACK |
| — | SEARCH | **none** | exit only by erasing the query — the trap |
| — | System back | `MainActivity`: any non-DECK destination → DECK in one press | a man in DETAIL over MONTH over LANDING is ejected to the DECK by a single press |

### 4.1 The one costume

Every BACK on this screen — and every BACK the Builder adds —
is the **header costume**, specified once:

- Position: the header row of the surface it closes, opposite
  the surface's title (title left, BACK right), in the same row
  height family as the `STORED` header.
- Print: the word `BACK`, Atkinson Hyperlegible, bold, 12sp,
  full ink `#34323B`. No arrow, no `‹`, no icon, no label naming
  the destination (the parent is stated by the truth table, 4.2,
  not printed per-screen — a BACK that names a parent it does
  not go to is the current MONTH lie in a new suit).
- Target: **~45dp tall, edge-flush** to the 12dp page padding
  (Aug, ruled §2) — the clickable area extends to the screen
  edge and the full header height, though the word stays its
  size. Press state: `#493EA4` at ~8% wash over the target.
- The `DrawerNavRow` costume (full-width list-item BACK) is
  **dead** — its only Drawer uses are INDEX's BACK and the
  CURIOS rows, both evicted (§5); the composable itself is
  removed with them (§5.2).

### 4.2 The truth table — every state, its one parent

| State | On-screen BACK | System back | Parent rule |
|---|---|---|---|
| LANDING | none composed (it is the root of this destination) | **not consumed** — `MainActivity`'s handler ejects to DECK | LANDING's parent is the destination switch itself |
| MONTH(y, m) | header BACK | one press = the on-screen BACK | → LANDING, scroll restored at the shelf row he tapped (§3.2 anchor). **No VM-month side effect**: BACK must not call `onSelectMonth` for the current month — see 4.5 |
| SEARCH (over LANDING or MONTH) | header BACK, same costume, in the search surface's header | one press = the on-screen BACK | **clears the query** (`onQueryChange("")`, which resets VM results + offsets) and returns to the nav beneath, scroll restored. Never clears anything else |
| DETAIL(taskId, origin) | header BACK — its only verb (acquitted) | one press = the on-screen BACK | → origin exactly as left: MONTH keeps its month and scroll; SEARCH keeps its **query intact** and its results scroll — leaving a detail is not leaving the search |

The DETAIL-over-SEARCH vs SEARCH's-own-BACK asymmetry is
deliberate and is the whole point: *BACK always undoes the last
thing he did.* He typed (BACK erases the typing), then he opened
(BACK closes the opening, typing preserved).

### 4.3 SEARCH gains its BACK — the trap dies

SEARCH is composed today by replacing the list whenever the
query is non-blank; it has no header of its own. Finish: the
search surface gets the standard header row — `STORED` title
left, header BACK right — above the entry line, present whenever
the query is non-blank. BACK clears the query through the VM
(same call as erasing by hand, so results/offsets reset by the
existing path) and the nav beneath reappears **as he left it** —
which §3's saveable scroll makes true even without death. Erasing
the query by hand remains a second, equivalent exit (same
result, same restore); neither path may strand him at a scrolled
position that was the search's, not the landing's.

### 4.4 System back — consumed inside the Drawer, one step per press

`DrawerScreen` installs its own `BackHandler`, enabled exactly
when the Drawer is not at rest (`nav != Landing`, or the query
is non-blank). Each press performs **the same function** as the
on-screen BACK of the current top state — one step, never two,
never a skip to LANDING from DETAIL-over-MONTH, never an eject.
At rest (LANDING, blank query) the Drawer's handler is disabled
and `MainActivity`'s existing handler (non-DECK → DECK) fires —
the ejection is correct *there*, and only there. The Builder
verifies precedence by behaviour, not by hope: with the Drawer
composed and a detail open, one press closes the detail and the
destination is still VAULT; the DECK ejection must not win.
(Cabinet's two-step pop is correct logic and travels with the
cabinet to its new home — it is not replicated, approximated, or
left behind here.)

### 4.5 The month-slot conflation BACK was hiding

MONTH's BACK resets the VM month (line 675) because the landing
and the open month currently share one VM slot: `drawerMonth` /
`drawerRows` hold whichever month was selected last, and the
landing's groups are filtered out of that same page. The reset
is load-bearing today and a lie by design. The finish removes
the need, not just the call: **the landing's current-month page
and the open month are separate facts** — `Month(y, m)` in
`DrawerNav` names the open month locally; the landing always
renders the current filing month's own loaded page (its depth
and scroll saved per §3). Returning from MONTH therefore needs
no reset to tell the truth: the landing he returns to is the
landing he left. How the VM splits the slots is the Builder's
machinery; the behaviour — BACK carries no side effects beyond
the step it names — is the spec.

### 4.6 Builder checklist — BACK

- [ ] One costume everywhere; `DrawerNavRow`-costume BACKs gone.
- [ ] ~45dp edge-flush targets on every BACK, DETAIL's included
      (today's bare text at line 238 is the worst offender).
- [ ] SEARCH header + BACK composed whenever query non-blank;
      BACK routes through `onQueryChange("")`.
- [ ] `DrawerScreen`-local `BackHandler`, enabled iff not at
      rest; one step per press; DECK ejection only from rest.
- [ ] MONTH BACK free of `onSelectMonth` (line 675 call deleted
      with the conflation, 4.5).
- [ ] Press-walk test: LANDING → MONTH → DETAIL → back → back →
      LANDING → back → DECK; and LANDING → type → DETAIL → back
      (query intact) → back (query cleared) → LANDING.

---

## 5 · THE CABINET-SHAPED HOLE — EVICTION, VERIFIED IN THE CODE

Issue #21 parks the cabinet's new home; this section owns only
the hole: after eviction the Drawer must contain **no dead
cabinet branch, no orphan state, no dangling reference** — and
equally, nothing the cabinet needs to live elsewhere may be
deleted from the codebase by this pass. Every item below was
verified by reading the paths, not by searching for a word and
assuming.

### 5.1 What the Builder removes from `DrawerScreen.kt`

1. **The CABINET early-return block** (line 330 to the `return`
   before the list views): its header + BACK, the artifact list
   (rows, `FORGED` list tags), the standing forge-rule line
   (`cabinet_forge_rule` — killed as a standing line, §2A), and
   the artifact detail (serial plate, lore, `FORGED — IT STANDS
   IN THE CABINET` / `NOT YET FORGED` print). The block is a
   whole second screen squatting in this file; it leaves whole.
2. **The dead branch in the list's `when()`** (lines 617–619):
   `DrawerView.CABINET -> item { Spacer(Modifier.height(0.dp)) }`
   — a 0dp spacer behind a comment admitting it can never run.
   This is the literal cabinet-shaped hole the audit named; it
   dies with the eviction regardless of any other choice.
3. **`DrawerView.CABINET`** from the enum (line 72) — with §3's
   type change the enum is superseded by `DrawerNav` entirely;
   either way no CABINET value survives in this screen's state.
4. **`cabinetArtifact` state** (line 166) — orphan the moment the
   block goes; no other line reads it.
5. **Both CURIOS rows**: the globally-empty branch's row (line
   ~737) and the populated branch's row (line ~775), each with
   its divider and the `drawer_curios_row` testTag. The row is
   killed, not annotated — no "moved" notice, no forwarding
   sign, no placeholder (Designer's §6).
6. **The screen's cabinet parameters**: `artifacts:
   List<KineticArtifact>` and `forgedArtifactIds: Set<String>`
   in the `DrawerScreen` signature, **and** their arguments at
   the `MainActivity` call site (VAULT branch) — a parameter
   nobody passes is as dangling as a call nobody reads.
7. **Cabinet-only imports** in this file: `CurioCatalog`,
   `KineticArtifact`. (Sweep the rest of the import block after
   removal — e.g. `Spacer` survives via DETAIL; the Builder
   confirms each import still has a call site rather than
   trusting this list.)
8. **The chrome-C10 comment block** riding on the parameters
   ("the curios' new home… the cabinet is its furniture") and
   every comment in the file that describes the cabinet as part
   of this screen, including the CABINET section banner. A
   comment describing an evicted room is a dangling reference
   in prose.

### 5.2 Orphans created by the wider verdict (same sweep)

- **`DrawerView.INDEX` + the INDEX branch + the `ALL MONTHS`
  row** (Designer's §2–3 kill): once INDEX and both CURIOS rows
  are gone, **`DrawerNavRow` has zero call sites** (its only
  uses are INDEX's BACK, line 623, and the two CURIOS rows) —
  the private composable is removed, not left as a helper
  awaiting a job.
- **`globallyEmpty`'s CURIOS-in-empty-branch** asymmetry dies
  with item 5; the empty branch keeps only §1.1's furniture.

### 5.3 Dangling references outside the screen (flag, don't strand)

- **Tests:** `ScreenshotHarnessTest` clicks `drawer_curios_row`
  and `cabinet_row_artifact_lissajous` and captures
  `07_cabinet_detail.png` (two call sites). These references
  dangle the moment the rows go. Disposition is the Builder's /
  Ren's call with issue #21 (the cabinet shots presumably move
  to the cabinet's new home's harness), but they must be
  **named in the eviction commit, not discovered by a red
  build**. `DrawerRenderTest`'s fixtures (populated / empty
  Drawer) carry no cabinet dependency and stand.
- **The word "cabinet" elsewhere in this file's KDoc** (the
  header's storage framing) is swept per 5.1.8; occurrences in
  *other* files are other rooms' business.

### 5.4 What must NOT be removed (travels with the cabinet)

Verified as living outside this screen and owned by issue #21 /
the forge truth fix, **not** by this eviction: `CurioCatalog`
and `KineticArtifact` (data), the Repository forge path and the
Dao's distinct-set read, the VM's `_forgedArtifactIds` flow, and
the cabinet's two-step pop *logic* (artifact-then-list) as a
behaviour. The Drawer stops **hosting** all of it; this pass
deletes none of it from the codebase. If the Builder finds the
VM flow unread after the MainActivity call-site change, it
stays anyway — its reader is the cabinet's next home, and an
unread-for-now flow feeding a parked destination is inventory,
not an orphan.

### 5.5 The hole test (acceptance)

After the sweep, in `DrawerScreen.kt`: no `CABINET`, no
`cabinet`, no `CURIOS`, no `Curio`, no `artifact`, no `forged`
in code, comments, testTags, or keys — the grep comes back
empty, and the screen still compiles its remaining states
(LANDING / MONTH / SEARCH / DETAIL) with §3's type. A hole
shaped exactly like the cabinet, outlined in dead symbols, is
the failure this section exists to prevent; no hole at all is
the pass.

---

## 6 · THE FINISH LIST, STATE BY STATE (for LADE)

1. **EMPTY archive (E0/E1)** — filing zone + one acquitted line
   + air; emptiness test fixed to read stored work only
   (`dayWords` defect, line 185, killed). **Empty current month
   beside the shelf (E2)** — the band composes nothing; the
   shelf's named months carry the calm; section rhythm unbroken.
   Headings exist iff rows or a declared word exist.
2. **SEALED** — reading free everywhere (acquitted, verified in
   code). Search finished **both ways**: Variant A (composed,
   fully live — audit-resolved) and Variant B (not composed at
   all, no height held) — the only delta between them is one
   composition gate. Filing words finished both ways too:
   F-PRINT (declared = print, undeclared = date only, no tap
   targets) as the consistent default; F-LIVE (byte-identical
   to unsealed) spec'd as the one-line exemption. Never, in any
   variant, a composed thing that refuses.
3. **PROCESS DEATH** — one formal type, `DrawerNav` (LANDING /
   MONTH(y, m) / DETAIL(taskId, origin)); DETAIL a named state;
   IDs only, never entities. Saved bundle enumerated (nav,
   query, per-surface scroll-by-key, EARLIER depth, shelf
   anchor); restore specified per state, including restore
   beating `refreshDrawer()`'s month reset, sealed-restore
   keeping a Variant-B-hidden query, and silent fallback to
   origin if an ID ever resolves null. Seal and today are
   re-derived, never restored.
4. **BACK** — one header costume (~45dp, edge-flush, `BACK`,
   no destination naming); truth table for LANDING / MONTH /
   SEARCH / DETAIL; MONTH's parent is LANDING with **no
   VM-reset side effect** (the landing/open-month slot
   conflation named as the root defect); SEARCH's BACK clears
   the query, DETAIL's BACK from search preserves it; system
   back consumed inside the Drawer one step per press, DECK
   ejection only from rest.
5. **EVICTION HOLE** — removal inventory verified line by line:
   CABINET block, the 0dp dead `when()` branch, enum value,
   `cabinetArtifact`, both CURIOS rows, both screen parameters
   + MainActivity arguments, cabinet-only imports, C10 prose;
   `DrawerNavRow` orphaned by the combined kills and removed;
   `ScreenshotHarnessTest`'s cabinet tags flagged as dangling
   by name. Named keep-list (catalog, model, forge path, Dao
   read, VM flow, pop logic) travels to issue #21 untouched.

Open for Ren, deliberately not decided here: the seal doctrine
call itself (Variant A vs B; F-PRINT vs F-LIVE) — both finishes
above are complete, so the call costs one sentence; and the
`ScreenshotHarnessTest` cabinet shots' disposition, which
belongs with the cabinet's new home in issue #21.

— Finisher 5 — States (issue #8 cell)
