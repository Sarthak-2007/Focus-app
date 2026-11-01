# FINISH — THE FILING ZONE & DAY STRUCTURE (issue #8)

Finisher 4 — Filing (issue #8 cell). My surface only: the filing
zone (today + tomorrow day-lines directly under the search line),
today's forced empty heading, day headings in groups, and the
03:30 filing-day rule at the seam. Sources of law:
`studio/plan/AUDIT-drawer.md` §2 (day-word ruling, forced-heading
kill, day-heading cures), §3 (the two-dates charge, not taken —
the 03:30 rule itself acquitted), §4 (Muede's 1 AM test),
§6 (acquittals: day words as print, day grouping as filing not
grading, DRW-05 ticker), and `designer-drawer.md` §1 (type &
colour subset), §2 (landing bands), §4 (filing-zone presentation).
Nothing below re-opens an acquittal, touches the shelf, search,
DETAIL, or the cabinet. No Kotlin was edited; nobody compiled.

---

## 0 · THE AS-BUILT GAP, IN CODE (why this finish exists)

Measured against the ruling, the shipped `DrawerScreen.kt` /
`MonolithViewModel.kt` fail the filing surface in six exact ways:

| # | As built | Ruled finish |
|---|---|---|
| 1 | The ask lives **on today's group heading** (`DayHeading`, `isToday && !isSealed` branch) — a question standing on a shelf label, repeated wherever today's group renders, including the MONTH view. | The ask **moves** to a dedicated filing zone under the search line (audit §2). Headings become print-only everywhere. |
| 2 | **Tomorrow is unfileable at any tap count** — `declareTodayWord(word)` takes no date; the VM stamps `filingDate(System.currentTimeMillis())` at write time (`MonolithViewModel.kt:1955`). The audit's worst single finding. | Two lines, today + tomorrow, each carrying its own explicit filing date (§6 below). The repository layer already supports this: `MonolithRepository.declareDayWord(dateIso, word)` and `DayWordStore.put(dateIso, word)` are fully general — only the VM/screen plumbing is today-locked. |
| 3 | Words are ~11sp Atkinson text with a bare `clickable` — **no minimum target**; a 1 AM thumb can hit the neighbour, and the active word **toggles clear** on a second tap (changeable all day). | Separate ≥44dp targets with dead gaps between them (§3); once declared, the line **retires to pure print, read-only forever** — no toggle, no clear, no re-ask (§2). |
| 4 | Words print in `onSurfaceVariant` / `onBackground` — theme greys, indistinguishable from print. Nothing marks them as the screen's only live action. | All three words in the **single accent `#493EA4`**, identically. The accent is present exactly while the action is, and leaves with it (§2, §3). |
| 5 | `buildDayGroups(includeTodayGroup = true)` **forces today's heading into existence** on the landing, in MONTH, and as a special item under `NOTHING STORED YET` on the globally-empty landing — a shelf for an absence. | **Killed** (§4). A group exists only if it holds rows or a declared word. |
| 6 | Heading type is 11sp with a 6dp top pad: a new day costs exactly what a new row costs (25dp), so shelves read as rows. The declared word prints in the same meta colour as its date. | Heading demoted to 10.5sp, shelf break 6→18dp (a day costs 37dp against a row's 25dp); the declared word prints at **full ink** beside its meta-ink date (§4). |

`DrawerDay.kt` and `DayWordStore.kt` need **no logic change** —
the 03:30 arithmetic and DRW-05's boundary ticker are acquitted
and already correct. Every delta below is presentation + one VM
signature.

---

## 1 · THE FILING ZONE — EXACT COMPOSITION

**Placement.** One `LazyColumn` item (a single `Column`, so the
list's item spacing can never insert gaps inside the zone),
composed **only** when `view == LANDING && !searching`, directly
under the search line and before any stored work — band 3 of the
designer's landing. It composes in **every** landing state:
normal, globally empty (above `NOTHING STORED YET`), current
month empty, and sealed (§2, sealed rows). It never composes in
SEARCH, MONTH, or DETAIL — filing is a landing act; the MONTH
view's headings are print (§4.3).

**Anatomy — two lines, nothing else:**

```
TODAY · FRI 30 OCT                REST   ILL   FAMILY
TOMORROW · SAT 31 OCT             REST   ILL   FAMILY
──────────────────────────────────────────────────────  ← zone hairline
```

- Each line: `Row`, full width, **48dp tall**, contents vertically
  centred, **no internal vertical padding** (the 48dp *is* the tap
  height; padding must not shrink the target inside it).
- Date segment, left, `weight(1f)`: `TODAY · <label>` /
  `TOMORROW · <label>`, where `<label>` is the line's **filing
  date** formatted exactly as `headingLabel` formats group
  headings (`EEE d MMM`, `Locale.US`, uppercase), with ` yyyy`
  appended **iff the filing date's year differs from the calendar
  year of `now`** — the one case where omitting the year can lie
  (see §5.4). The `·` is U+00B7 with one space each side.
- Words segment, right: `REST`, `ILL`, `FAMILY` in
  `DayWordStore.WORDS` order — fixed forever, never reordered by
  recency, frequency, or a previous declaration on the other line.
- **No hairline between the two lines.** The zone closes with one
  hairline under the second line: 1dp, ink `#34323B` at ~14% (the
  room's one divider system). Nothing else terminates or frames
  the zone — no card, no fill, no heading word like "FILE A DAY".
  The zone is two lines of print, not a banner; ignored, it does
  not grow, glow, or repeat itself.
- Zone total height: 48 + 48 + 1 = **97dp**, constant in every
  state (declared lines keep the 48dp frame, §2) — declaring a
  day must never shift the stored work below it by a single dp.

**Test tags (builder, exact):** zone `drawer_filing_zone`; lines
`filing_line_today`, `filing_line_tomorrow`; word targets
`filing_word_<rest|ill|family>_<today|tomorrow>`; retired lines
`filing_declared_today`, `filing_declared_tomorrow`.

---

## 2 · LINE STATES — THE FULL TABLE, NO OTHER STATES EXIST

Each line independently is in exactly one of four states, derived
**only** from `dayWords[iso]` and `isSealed` — never from a local
`remember`. Process death, recomposition, or the drain moving a
word from `DayWordStore` to `day_notes` (the `allDayWords()` union
already covers both) must render the identical state.

| State | Date segment | Words segment | Interactive |
|---|---|---|---|
| **Undeclared** | meta ink | `REST ILL FAMILY`, all in `#493EA4` | Each word a target (§3) |
| **Pressed** (transient) | meta ink | pressed word's target filled with `#493EA4` at ~8% wash, 0dp radius; glyph colour unchanged | — |
| **Declared → retired** | meta ink, then ` · ` (meta), then the word at **full ink** | the other two words **gone**; nothing on the right side | **None.** No `clickable`, no toggle-clear, no long-press, no semantics action. Read-only forever. |
| **Sealed, undeclared** | meta ink | **words not composed at all** — the line is plain date print | None |
| **Sealed, declared** | as Declared | as Declared | None |

Rules that bind the table:

- **Retirement is total and instant.** On the tap's release the
  write commits synchronously (the store already `commit()`s —
  G-02's standard) and the state flow re-reads; the same
  recomposition that removes `REST`/`FAMILY` removes the accent.
  **The accent leaves when the action does** — a retired line
  containing any `#493EA4` pixel is a defect.
- **No confirmation, no undo, no toast, no dialog, no haptic
  flourish, no sound.** The design case is two taps and sleep
  (§6); a confirm step is a third tap taxing exactly the man the
  ruling exists for, and an undo affordance would re-open a line
  the ruling retired. The protection against a mis-tap is
  geometric (§3), not procedural.
- **The two lines never interact.** Declaring today leaves
  tomorrow's line untouched in place, colour, and targets — and
  vice versa. Declaring both is two independent retirements.
- **Sealed follows the designer's one rule: never a painted
  door.** Filing mutates the record, so under seal the undeclared
  words are not composed — not greyed, not disabled-looking, not
  violet. (This is the designer's §4 second branch, chosen over
  "tappable under seal" because §2's search resolution turns on
  the opposite fact: a query mutates nothing, a filing does.)
  Declared lines print under seal exactly as unsealed — print is
  print.
- **No word ever appears anywhere else as an action.** Not on a
  group heading (§4), not in MONTH, not in SEARCH results, not on
  an empty-month line. The zone's two lines are the only filing
  surface in the app.

---

## 3 · TYPE, COLOUR, TARGETS, ACCESSIBILITY

**Type & colour (both lines, both themes — the values are the
ruling's, not the theme's):**

| Element | Face | Form | Colour |
|---|---|---|---|
| `TODAY ·` / `TOMORROW ·` + date label | Anonymous Pro | bold caps, 11sp, letterSpacing 1sp | meta ink — `#34323B` at ~60% (the room's meta token) |
| Undeclared words | Anonymous Pro | bold caps, 12sp, letterSpacing 1sp | `#493EA4`, all three identical |
| Retired word (beside its date) | Anonymous Pro | bold caps, 12sp, letterSpacing 1sp | full ink `#34323B` at 100% |
| Zone hairline | — | 1dp | `#34323B` at ~14% |

- **No semantic colour-coding, ever.** `ILL` is not red, not
  darker, not larger; `REST` carries no hue of its own; `FAMILY`
  none. All three words share face, size, weight, spacing, and
  the one accent. Colour-grading a sick day is still grading
  (designer §1) — a red ILL in a no-grading room is a verdict
  wearing a colour.
- The accent `#493EA4` appears in the zone **only** as undeclared
  word glyphs and the 8% press wash. Never on the date, never on
  the hairline, never as a background behind a whole line.
- **Never green** or near-green anywhere in the zone, press
  states included (the violet wash covers press feedback).

**Tap targets — the 1 AM geometry:**

- Each word is its **own** target: height = the full line height,
  **48dp**; width = `max(48dp, glyph width + 24dp)` (12dp of
  target padding each side of the glyphs).
- Between adjacent targets: an **8dp inert spacer** — composed,
  visible as empty line, carrying no `clickable`. A thumb landing
  on a boundary falls in the gap and files **nothing**, instead
  of filing the neighbour. On a surface where a declaration is
  permanent, a dead gap is the honest failure mode; a nearest-
  word guess is a lie filed forever.
- The `FAMILY` target's right edge is flush with the line's right
  edge (page padding 12dp outside it); the words segment as a
  whole is end-aligned. No target extends past the page padding.
- Press feedback is the 8% accent wash on that target only,
  square corners (0dp radius — this room has no rounded
  furniture left once the search capsule dies).

**Accessibility:**

- Each undeclared word target: `Role.Button`, content description
  naming the **full filing date and the word** — e.g. *"File
  Saturday 31 October as ILL"* — built from the line's `LocalDate`,
  never from the short label alone. At the seam (§5) the short
  label and the calendar disagree; the spoken date must be the
  filing date.
- Focus/traversal order per line: date text, then `REST`, `ILL`,
  `FAMILY`, left to right. A retired line exposes **text only** —
  no button role, no action in its semantics tree. A sealed
  undeclared line likewise exposes text only.

---

## 4 · THE KILLED HEADING & DAY HEADINGS IN GROUPS

### 4.1 Today's forced empty heading — KILLED, exactly here

- **Delete the `includeTodayGroup` parameter** from
  `buildDayGroups` and all three call sites (LANDING, MONTH,
  SEARCH — SEARCH already passes `false` and is otherwise
  unchanged). The date set becomes: filing dates of the rows in
  scope **∪** dates holding a declared word in scope. Nothing
  else mints a group.
- **Delete the special `DayHeading` item** in the `globallyEmpty`
  branch. The globally-empty landing is: header, search line,
  **filing zone**, `NOTHING STORED YET` — in that order, nothing
  else (designer §2). The zone fully replaces the old capture
  hack: a sick day in the first week is filed from the zone, not
  from a heading bolted under the empty line.
- Consequences, stated so nobody "fixes" them back:
  - Today with **no rows and no word** → **no group heading
    anywhere**. The filing zone line stands (it is a live action,
    not a shelf for an absence); the groups below are silent
    about today. This is the audit's silence law applied to
    today exactly as it already applied to an empty month (§6
    acquittal).
  - Today with **a word and no rows** → a group heading **does**
    render in the groups (§4.2). A declared word is stored
    content, not an absence; the word-only group is how that day
    stays filed once it leaves the zone.
  - Same rule in MONTH: a month view no longer force-renders
    today's group when the current month is opened.

### 4.2 Group headings — finish values

- Type: Anonymous Pro, **10.5sp** (demoted from 11sp), bold caps,
  letterSpacing 1sp — into the meta family, per Aug's cure.
- Shelf break: heading block padding **top 18dp** (from 6dp),
  bottom 6dp. A new day thereby costs **37dp** against a row's
  25dp — a shelf must cost visibly more than a row, or the groups
  read as one list. This applies to the first heading under the
  filing zone too (zone hairline + 18dp = the section break).
- The heading is **two print spans, never one colour**: the date
  label in meta ink (`#34323B` ~60%), then — if a word is
  declared — ` · ` in meta ink and the **word at full ink**
  `#34323B`. The word is his authored print riding beside its
  date (§6 acquittal); at full ink it reads as record, the date
  as locator. No accent on a heading, ever.
- The heading composable becomes **print-only**: delete the
  `isToday` ask branch, the words row, and the
  `onDeclareTodayWord` parameter from `DayHeading`. Its only
  inputs are the group's date and word (subject to §4.3).

### 4.3 One word, one surface at a time — the transfer rule

A declared word for a date currently inside the filing zone
(today/tomorrow) would otherwise print twice on the landing:
once on the retired zone line, once on that date's group heading.
Gallery standard is one occurrence per composition:

- **On the LANDING**, group headings for the two zone dates
  suppress the word span (`word = null` passed for those dates
  only) — the date heading still renders if the group holds rows.
  The word lives on the zone line, directly under the search
  line, where he filed it.
- **In MONTH** (a separate composition with no zone), the same
  date's heading prints its word in full — suppression is scoped
  to the landing render, never to the data.
- **When a date ages out of the zone** (the 03:30 roll, §5.3),
  its heading inherits the word at the same moment the zone line
  disappears. Transfer, never copy, never loss: at every instant
  the word is visible exactly once on the landing.
- SEARCH group headings stay wordless, as built — search headings
  are locators over result rows; no change.

---

## 5 · THE 03:30 SEAM — BEHAVIOUR, SPECIFIED TO THE MINUTE

Definitions (all from `DrawerDay`, acquitted — do not re-derive
elsewhere): the **filing day** `F = filingDate(now)` = the local
date of `now − 210 min`. The zone's two dates are:

- **today line → `F`**
- **tomorrow line → `F + 1 day`**

Both derive from the **same `nowMillis`** state the DRW-05 ticker
already maintains, so both lines always re-derive in the same
recomposition. Neither line ever reads `LocalDate.now()`, a
calendar date, or a second clock.

### 5.1 The seam table (Asia/Kolkata; calendar date ≠ filing date before 03:30)

| Wall clock | Filing day `F` | TODAY line prints | TOMORROW line prints |
|---|---|---|---|
| Fri 30 Oct, 14:00 | Fri 30 Oct | `TODAY · FRI 30 OCT` | `TOMORROW · SAT 31 OCT` |
| Sat 31 Oct, **01:00** | **Fri 30 Oct** | `TODAY · FRI 30 OCT` | `TOMORROW · SAT 31 OCT` |
| Sat 31 Oct, 03:29 | Fri 30 Oct | `TODAY · FRI 30 OCT` | `TOMORROW · SAT 31 OCT` |
| Sat 31 Oct, **03:30** | **Sat 31 Oct** | `TODAY · SAT 31 OCT` | `TOMORROW · SUN 1 NOV` |
| Sat 31 Oct, 14:00 | Sat 31 Oct | `TODAY · SAT 31 OCT` | `TOMORROW · SUN 1 NOV` |

The 01:00 row is the whole point: the calendar has rolled, the
filing day has not. A man awake at 01:00 is still inside the day
that began at 03:30 yesterday; the day he will sleep into and
wake up sick in begins at 03:30 today, and it is printed —
honestly, by its filing date — on the TOMORROW line. **The print
never lies about which day got filed** because the date is
printed *on the line he taps*, derived from the filing clock,
and carried by the tap itself (§5.2). A calendar-naive build at
01:00 would print `TODAY · SAT 31 OCT` / `TOMORROW · SUN 1 NOV`
and file his tap under Sunday — the audit's wrong-day charge,
killed here by construction.

### 5.2 The write carries its date — the race rule

- The callback signature changes from `(String?) -> Unit` (today,
  re-derived at write time — the current defect) to
  **`(LocalDate, String) -> Unit`**: each zone line passes **its
  own composed `LocalDate`**. The VM's replacement function
  (`declareDayWord(date, word)`) converts that exact date with
  `DrawerDay.dateIso(date)` and writes it. It must **not**
  re-derive any date from `System.currentTimeMillis()`, and must
  **not** window-check the date against a freshly computed
  today/tomorrow — a check at write time would reintroduce the
  seam bug at the one moment it matters:
  - Tap lands at **03:29:59** on the TODAY line (filing `F` =
    yesterday's date) and the commit executes at 03:30:00.x —
    the word files under `F`, the day printed on the line he
    touched. Truth of print wins over freshness of clock.
  - Tap lands at 03:29:59 on the TOMORROW line (`F + 1`) — files
    under `F + 1`, which a second later becomes the new today.
    Still exactly the date he read.
- The `null` (clear) path is **not wired** into the zone at all:
  no zone interaction passes `null`. `Repository.declareDayWord`'s
  null handling stays in the data layer, unused by this surface.

### 5.3 The boundary roll, with a declaration in flight

The existing DRW-05 ticker (delay to `nextBoundaryMillis`, then
re-read `nowMillis`) drives the zone unchanged. At the roll:

- Both lines advance by one day **atomically** — there is no
  frame in which today has rolled and tomorrow has not.
- A word declared yesterday on the TOMORROW line **migrates**:
  its date is now `F`, so it renders as the TODAY line in the
  retired state — same full-ink print, no ask re-appears for that
  date, no duplicate on its group heading (the transfer rule
  §4.3 now suppresses it there, since the date is a zone date).
- The date that was TODAY leaves the zone; if it holds a word,
  the word continues on its group heading (§4.3) — the zone
  forgetting a day never unmakes its filing.
- A Drawer left open across 03:30 in Muede's hands (§6) shows him
  the roll if he ever looks again; a Drawer closed at 01:05
  simply opens next morning already correct. Neither path needs
  a refresh tap — `refreshDrawer()` also re-reads day words on
  entry, as built.

### 5.4 Month and year crossings

- **Month:** `F` = Fri 30 Oct ⇒ tomorrow = Sat 31 Oct; `F` = Sat
  31 Oct ⇒ tomorrow prints `SUN 1 NOV`. The declaration writes
  ISO `2026-11-01` through the existing general store path; the
  pre-existing `storedMonths()` union (task-months ∪ word-months)
  keeps November addressable in MONTH after the date ages out of
  the zone, so a word-only month is never filed nowhere. Rely on
  this; do not change it from this slice.
- **Year:** `F` = Thu 31 Dec 2026 ⇒ tomorrow prints
  `TOMORROW · FRI 1 JAN 2027` — the §1 year rule fires because
  the filing year (2027) differs from the calendar year of `now`
  (2026). Symmetrically, at 01:00 on Fri 1 Jan 2027 the TODAY
  line prints `TODAY · THU 31 DEC 2026`. In the common case no
  year prints, matching the designer's examples.

---

## 6 · MUEDE'S 1 AM ERRAND — TAP BY TAP (the acceptance walk)

Preconditions: **Saturday 31 October 2026, 01:00 IST.** Calendar
says Saturday; the filing day is still **Friday 30 October**
(it began 03:30 Friday; Saturday's filing day begins 03:30
tonight). He is sick, in bed, phone in one hand. The archive
holds stored work, so the landing is populated. His errand: file
**tomorrow** — the day he will wake into — as ILL, and sleep.

**Tap 1 — open the Drawer.**
He taps the Drawer's destination. The landing composes in one
scroll: `STORED` header; the search entry line (44dp, 1dp rule);
then the filing zone, before a single stored task:

```
TODAY · FRI 30 OCT                REST   ILL   FAMILY
TOMORROW · SAT 31 OCT             REST   ILL   FAMILY
```

He reads two dates, both filing dates. The day he is still in:
Friday. The day he needs: **Saturday 31 October**, printed on the
line itself, in meta ink, impossible to mistake for Sunday —
which is not on this screen at all. The three words on that line
stand in the one violet; nothing else on the screen is violet,
nothing asks him anything else. No scroll — the zone is above
the stored work by construction. No typing.

**Tap 2 — `ILL`, on the TOMORROW line.**
His thumb lands on `ILL`: a 48dp-tall target, 8dp of dead line
on either side, so the half-asleep miss left or right files
nothing rather than filing `REST` forever. Under his thumb the
target washes `#493EA4` at 8% — the only feedback, silent.

On release, the write commits synchronously under the ISO date
the line carried, `2026-10-31`. The same recomposition retires
the line:

```
TODAY · FRI 30 OCT                REST   ILL   FAMILY
TOMORROW · SAT 31 OCT · ILL
```

`ILL` now sits beside its date at full ink; `REST` and `FAMILY`
are gone from that line; the violet is gone from that line; the
line is 48dp tall, exactly as before, so nothing below it moved.
The TODAY line is untouched — Friday is still fileable, and it
is not his errand. No dialog, no toast, no count, no "saved"
flourish. There is nothing left to do and nothing left to tap.

**He locks the phone and sleeps. Total: 2 taps, 0 scrolls,
0 keystrokes.**

**The morning after (no taps — verification, not a step).**
At 03:30 the filing day rolls. If the Drawer were open, both
lines would advance together and Saturday's `ILL` would migrate
to the TODAY line, still retired, still full ink. When he opens
the Drawer at, say, 09:00, the top of the landing reads:

```
TODAY · SAT 31 OCT · ILL
TOMORROW · SUN 1 NOV              REST   ILL   FAMILY
```

His filing is on the day he meant, in print, on the first
screen — and it will stay there, word beside date, after the
zone lets the day go: Saturday's group heading in MONTH carries
`SAT 31 OCT · ILL` at full ink for as long as the archive keeps
it, which is forever. The counterfactual is the audit's finding:
before this finish, the same errand was **impossible at any tap
count**, and the nearest available tap — ILL on the night-time
"today" heading — would have filed Friday and printed nothing
to warn him.

*Seam variant, for the record:* the identical errand at
**03:29** files the same ISO date by the same two taps (§5.2's
race rule covers a commit landing after the boundary); at
**03:31** the ILL line has already migrated to TODAY, and the
errand is again two taps — Drawer, `ILL` on the TODAY line. At
no minute of the night does the correct word sit on the wrong
date.

---

## 7 · BUILDER DELTAS — EXACT LIST (my slice only)

1. **`DrawerScreen.kt` — new composables.** `FilingZone(today:
   LocalDate, dayWords, isSealed, onDeclareDayWord)` rendering
   two `FilingDayLine`s per §1–§3, composed as one item in the
   LANDING branch immediately after the search item, gated on
   `!searching`. `tomorrow = today.plusDays(1)` — derived from
   the existing ticker-backed `today`, never from a second clock.
2. **`DrawerScreen.kt` — signature.** `onDeclareTodayWord:
   (String?) -> Unit` is deleted everywhere; replaced by
   `onDeclareDayWord: (LocalDate, String) -> Unit`. Only
   `FilingDayLine` calls it, always with its own date.
3. **`DrawerScreen.kt` — `DayHeading`.** Print-only per §4.2:
   10.5sp, top padding 18dp / bottom 6dp, two-span colour (date
   meta, word full ink). Ask branch, words row, `isToday`,
   `isSealed`, and `onDeclareTodayWord` params deleted.
4. **`DrawerScreen.kt` — `buildDayGroups`.** `includeTodayGroup`
   param and the `dates.add(today)` line deleted; date set =
   row dates ∪ in-scope word dates. LANDING passes word
   suppression for the two zone dates per §4.3; MONTH does not.
   The `globallyEmpty` branch loses its `DayHeading` item (§4.1).
5. **`MonolithViewModel.kt`.** `declareTodayWord(word)` →
   `declareDayWord(date: LocalDate, word: String)` per §5.2:
   `repository.declareDayWord(DrawerDay.dateIso(date), word)`,
   then re-read `_dayWords` (and `_storedMonths`, as the current
   function does). No clock read inside.
6. **`MainActivity.kt`.** Wiring follows the renamed callback
   (`dayWords` state flow unchanged).
7. **Untouched, by name:** `DrawerDay.kt`, `DayWordStore.kt`,
   `MonolithRepository.declareDayWord` / `allDayWords` /
   `storedMonths`, the DRW-05 ticker block. If a delta seems to
   need one of these, it is the wrong delta — stop and re-read §5.

**Acceptance checks for the builder's render/unit pass** (written
here, run by whoever Ren seats — I do not compile):

- Seam: with the clock at Sat 31 Oct 2026 01:00 IST, zone lines
  print `FRI 30 OCT` / `SAT 31 OCT`; at 03:30 both advance in one
  recomposition; `DrawerDayTest` stays green untouched.
- Tap routing: tapping `ILL` on the tomorrow line writes ISO =
  tomorrow's filing date and leaves today's entry absent from
  the map; tapping at a simulated 03:29:59 for a commit after
  03:30 still writes the tapped line's date.
- Retirement: after declaration, the line exposes no clickable
  node, no `filing_word_*` tag for that line, zero `#493EA4`
  pixels; the other line's three targets remain.
- Geometry: every `filing_word_*` target measures ≥48dp tall
  and ≥48dp wide; the inter-target gaps carry no clickable.
- Heading kill: empty today + no word ⇒ no group heading item
  for today on LANDING or MONTH, and none under
  `NOTHING STORED YET`; word-only today ⇒ heading present in
  groups, word suppressed on LANDING while the date is a zone
  date, present in MONTH.
- Colour audit: `ILL`/`REST`/`FAMILY` glyph colours identical;
  no red, no green anywhere in the zone in either theme.
- Sealed: undeclared zone lines compose no word nodes; declared
  lines print identically to unsealed.

## 8 · WHAT THIS FINISH DELIBERATELY DOES NOT DO

- No filing for any day beyond tomorrow, and none retroactively
  for a past day — the ruling gives a past unfiled day to his
  choice to leave blank; the zone's window is exactly two days.
- No editing or clearing of a declared word from any surface —
  retirement is forever (audit §2); the data layer's null path
  is not surfaced.
- No "days begin at 03:30" print in the zone or anywhere on this
  screen — that line belongs to the introduction sheet (audit §3,
  escalated); the zone *behaves* by the rule and prints its
  results, it does not lecture about it.
- No counts, no streak of filed days, no calendar strip, no
  month grid — two lines are the whole surface, by design.

— Finisher 4 — Filing (issue #8 cell)
