# Issue #7 — Shield: the Standing Orders surface, finished

**Seat:** GLANZ, FINISHER 2, in VOGT's cell for issue #7, "Main-app pass — Shield (audit fixes)."
**Slice (only this):** the Standing Orders de-duplication surface under audit **R-1** — the
state-only tally headline and the single grouped catalogue that replaces the slip + flat
catalogue pair. Group headers, tally dress and manners, spacing rhythm, first-glance
hierarchy, and calm at 0 / 7 / 74 apps. Everything else on Shield — status sentence
(FORM), cap, routines, websites, sled internals (Panzer's bench), refusal placement — is
another seat's; I touch it only where my surface touches it, and I say so where I do.
**Status:** DECLARED — specification only. No file under `app/src/` edited by me, nothing
compiled, no tests run. MONTEUR is sole code editor; §7 is his patch list.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13, merge list, acquittals);
`studio/cells/issue-7/designer-form.md` (§2.1 order, §3.1 copy) and
`innovator-funke.md` (§5a RuleBook) — this spec finishes their surface, it does not
re-litigate it; `ShieldMatrixScreen.kt` in **both** states: as merged at `8438f5a` and as
the working tree stands at writing (MONTEUR in flight — see §1); `data/AppRules.kt`
(working tree), `ui/components/MonolithCustomComponents.kt` (`FullBleedSectionHeader`,
:123), `ui/theme/Type.kt`, `ui/theme/MonolithCurves.kt`;
`MonolithRepository` peek constants (`PEEK_DURATION_SECONDS = 120`,
`PEEK_COOLDOWN_MILLIS = 20 min`).

---

## 1. Where the tree actually stands (so MONTEUR and I never talk past each other)

Already landed in the working tree, **not** part of my patch list — do not redo:

- `AppRules.groupedForCatalogue()` — groups in PEEK → LOCKED → OPEN order, alphabetical
  inside each group, **empty groups contribute no group** (its own doc: a ruling's absence
  "is printed once, by the tally's zero above the catalogue, never by an empty section").
- `ShieldMatrixScreen` already computes `catalogueGroups = filteredApps.groupedForCatalogue()`
  and carries the de-duplication header comment — but at writing, §3.5 still emits the full
  slip (`SlipHeaderRow`, `slipLines`, `SlipLineRow`, `RestingFlip` machinery) and §5 still
  emits the flat `items(filteredApps)`. The surface is mid-amputation: the new model exists,
  the old body is still attached. My patches (§7) complete exactly that cut.

One doctrine from the landed code that governs everything below, because it is already
the tree's stated law and it is right: **a count is a list size, an absence is a zero in
the tally, and no fact gets a second home.** Where FORM §3.1 and the landed code disagree
(the in-catalogue `NONE` line), I follow the landed code + R-1; the disagreement is
registered in §8 for VOGT, not silently papered over.

---

## 2. The finished surface at a glance

Top-to-bottom, the parts of Shield this spec owns (neighbours named for position only):

```
[status sentence — FORM's, item 0]
[ratchet banner — existing item, Deck-origin refusals only after R-7]
TALLY HEADLINE          ← §3 — one line, three segments, words are jumps
[LOADOUT cap — R-4's]
[ROUTINES — R-2: stays]
[BLOCKED WEBSITES — R-3: above ALL APPS when that merge lands; my rhythm
                     does not depend on which side of the catalogue it sits]
ALL APPS head block     ← §5.3 — header + search + filter words + glyph legend
GROUP HEADER · PEEK     ← §4 — sticky, carries the PEEK contract line
  sled rows (54dp, acquitted — untouched)
GROUP HEADER · SHUT     ← §4 — sticky
  sled rows
GROUP HEADER · OPEN     ← §4 — sticky
  sled rows
```

**What the eye lands on first, in order, and why:**

1. **The status sentence** — the screen's largest fact (FORM §2.2). Not mine; my surface
   is built so nothing in it out-shouts it: the tally is one step smaller, the group
   headers one step smaller than that.
2. **The tally's numerals.** Inside my surface, the first landing is the SHUT numeral:
   Anonymous Pro Bold 15sp is the largest mono in the upper screen, ink at full density,
   and the tally's grammar is numeral-first (the existing `tallyText` order — number,
   then word). A tired eye reads `9 · 4 · 61` before it reads any word, and that *is*
   the one-glance proof: three numbers that sum to the phone.
3. **The group headers while scrolling** — because they are sticky (§4.3), one of them is
   always the topmost catalogue element; the eye always knows which ruling it is standing
   in. Inside a group, the eye lands on nothing — rows are uniform by design (acquitted
   54dp row), and uniformity is the calm.

**A retained asymmetry, ruled deliberately:** the tally prints `SHUT · PEEK · OPEN`
(ledger order — the headline count, the wall first), the catalogue groups print
`PEEK → SHUT → OPEN` (urgency order — the holes first; R-1: "PEEK before SHUT ordering
survives into the group order"). These are two different reads of the same book — a
balance line and a work list — and each keeps the order its read needs. Flagged in §8;
if VOGT wants one order everywhere, the tally is the one that changes (one argument in
`tallyText`), never the groups.

---

## 3. The tally headline — exact finish

### 3.1 Placement and slot

- The tally is its own `LazyColumn` item, emitted **directly after the ratchet-banner
  item and before the LOADOUT item** (FORM §2.1's order: status, banner, tally, cap).
  It leaves §3.5 entirely; §3.5 ceases to exist.
- **No section header above it.** `YOUR RULES` / `SlipHeaderRow` die with the slip and are
  not resurrected in any synonym — the tally *is* the headline; a label above a headline
  is a second headline. The status sentence above it is its context.
- Fixed slot: **height 32dp**, content vertically centred, so a re-strike, a segment
  gaining/losing its jump bar (§3.3), or a numeral going 1→2→3 digits never moves the
  LOADOUT cap below it by a single dp. Left edge on the screen's margin axis (the
  LazyColumn's existing 12dp horizontal padding — unchanged).
- testTag `standing_tally` stays, now on the Row (§3.2).

### 3.2 Typography — one print, three segments

The tally stops being one `Text` and becomes a `Row` of three segment composables —
because the words are jumps (R-1), and a jump needs its own touch target and its own
door furniture. The **print** does not change its grammar:

- Segment print = the existing `tallyText` mixed grammar, per segment: numeral in
  **Anonymous Pro Bold**, word in **Atkinson Hyperlegible Bold**, built with the same
  two `SpanStyle`s `tallyText` already uses. One change of size, ruled here: numerals
  **15sp** (they are the first landing, §2; 15sp is the screen's existing row-number
  size — the routine time column — so no new rung is invented), words stay **11.5sp**,
  letterSpacing 0.6sp. Numeral and word share the segment's baseline; the segment is
  `maxLines = 1`, never wraps, never shrinks.
- Separators: a literal `·` between segments, Anonymous Pro Bold 15sp,
  `onSurfaceVariant`, non-clickable, `Arrangement.spacedBy(10.dp)` in the Row plus the
  separator's own width. The separator is furniture, not a segment.
- Colour: full ink (`onBackground`) for numeral and word in all three segments, in both
  themes, at all counts — including zero (§3.3 changes a zero segment's *affordance*,
  never its ink: `0 OPEN` is a fact at full volume, not a greyed apology).
- The counts themselves are **derived once, from the unfiltered book**: replace the two
  separate `count {}` passes with the fold FUNKE §5a specifies and the landed code makes
  trivial — `val rules = remember(allApps) { allApps.groupedByRule() }`;
  `shut = rules.shutCount`, `peek = rules.peekCount`, `open = allApps.size − shut − peek`.
  Under search or a category filter the tally **does not change** — it counts his
  decisions, not the current view (Liv D6, carried by FORM §3.1 and FUNKE §5a). The
  catalogue head's `SHOWING n` is the view's number; the tally is the book's. Two
  numbers, two homes, no third.

### 3.3 The words are jumps — and say so, exactly when they can jump

R-1: tap SHUT, the saveable `listState` scrolls to the SHUT group. Fremd C5 (via FORM
§3.1): a receipt that is secretly a switch is a truth defect — the segments must read as
doors at rest. The door furniture is the screen's existing one, reused exactly:

- Each **live** segment is a `Column` (the `FilterWord` construction, same file):
  the segment print, then `Spacer(3.dp)`, then the bar — **18dp × 2dp, full ink** —
  centred under the **word** (not the numeral: the word is the destination's name).
  Same geometry as the filter words below, so the screen has exactly one door grammar:
  ink word + short bar = this word takes you somewhere.
- A segment is **live** iff its group exists in the *currently displayed* catalogue —
  i.e. count > 0 and no filter is hiding it. Two dead cases, both printed honestly:
  - **Count = 0:** no bar, no click, no ripple. A zero has nowhere to take him; a door
    painted on a wall is the C5 lie in reverse. Semantics (§6) still read the fact.
  - **A search/category filter is active:** the segment keeps its bar but its tap does
    two things in order — clears the search query and category, sets `pendingJump =
    tier`; a `LaunchedEffect(catalogueGroups, pendingJump)` performs the scroll once
    the unfiltered groups land and the target group exists, then clears `pendingJump`.
    Never scroll to an index computed against the filtered list.
- Touch target: the segment `Column` gets `Modifier.height(32.dp)` (fills the slot) and
  a minimum width of 48dp via `widthIn(min = 48.dp)`, `clickable` with the house
  behaviour (no custom indication invented; the bar is the affordance, the scroll is
  the feedback). The jump itself: `animateScrollToItem(index, scrollOffset = 0)` in a
  `rememberCoroutineScope` — animated, on the tally's own manners (a jump is a
  re-location, not a teleport; ~the house 420dp/s-class motion — MONTEUR uses
  `animateScrollToItem`'s default spec, no new curve token), landing the group header
  at the top of the viewport, where it sticks (§4.3) and confirms arrival with its own
  count (§4.2).
- **Index computation — the one fragile point, specced shut.** `LazyListState` scrolls
  by index, not key, and the item count above the catalogue varies with conditional
  items (prune note today; R-3's websites move tomorrow). Hardcoded indices are
  therefore forbidden. MONTEUR computes them by counting at emission, in DSL order:
  a `var nextItemIndex = 0` local before the `LazyColumn` content lambda, incremented
  by 1 after every `item {}` / `stickyHeader {}` call and by `group.size` after every
  `items(group)` call, in source order, inside the same conditionals that emit them.
  When a group header is emitted, `groupHeaderIndex[tier] = nextItemIndex` is recorded
  *before* its increment. A comment at the counter states the law: **every emission
  between the tally and the last group must increment, or jumps land wrong** — and the
  on-device landing check in §9 is the proof this was maintained. This survives the
  R-3 reorder and the prune note's presence/absence with no code change, which is why
  it is specified over any formula naming today's sections.

### 3.4 Re-strike manners — the governed law, kept whole, extended by one clause

The tally's manners are acquitted craft (Gust/Mikro M4, audit standing charges) and this
finish changes none of them — it states them as the contract MONTEUR must not break
while rebuilding the line as segments:

- **Coalesce:** 250ms. `tallyShown` updates only after `tallyNow` has been stable
  250ms (the existing `LaunchedEffect(tallyNow) { delay(250); … }`, unchanged). A burst
  of sled flips re-strikes **once**, to the latest figures. The ≤250ms print lag is
  governed and accepted (audit, standing charges) — it is a print delay, not a state lie.
- **Whole line, one strike:** the `AnimatedContent` (targetState = `tallyShown`,
  260ms fade in/out, `MonolithCurves.EasingA` — the existing `StandingTallyLine` spec,
  unchanged) wraps **the entire Row**, separators and bars included. Segments never
  animate individually; a segment whose count changed does not flash, pulse, or roll
  while its neighbours sit still. Old print lifts, new print lands, once.
- **Never:** digit rolls, count-ups, odometer anything, scale pops, colour flashes.
  A loved ledger does not perform arithmetic.
- **One extension (new, mine):** the jump bars are part of the struck print. If a
  re-strike takes a segment 1→0 (last PEEK app flipped to SHUT from its sled), the bar
  leaves **in the same strike** as the numeral change — the line never shows, for one
  frame, a live door under a printed zero. This falls out of §3.2's construction
  (bars render from `tallyShown`'s counts, inside the `AnimatedContent`) — MONTEUR
  must not hoist bar state outside it.
- First frame: the tally prints `tallyNow` immediately (`remember { mutableStateOf(
  tallyNow) }`, as today) — no entrance animation, no strike on arrival. Facts do not
  fade in.

---

## 4. The group headers — exact finish

### 4.1 Order and existence

- Order is the landed `groupedForCatalogue()` order: **PEEK, then SHUT, then OPEN** —
  PEEK before SHUT survives from the slip (R-1), OPEN last (it is the inert tail; at
  74 apps it is also the long tail, and the urgent reads must not wait behind it).
- A group with no members emits **no header and no placeholder** (§1's doctrine). There
  is no `NONE` line in the catalogue — the tally's zero, 32dp slot at the top of the
  screen, is the absence's one print. Three empty headers at 0 would cost ~100dp to
  say what `0 · 0 · 0` already said, and an empty section reads as a bug where a zero
  reads as a fact (the slip's own `SlipNoneLine` grammar is acquitted *for the slip*;
  the slip is dead, and its grammar does not get a second home).
- Under a filter, the same rule applies to the *filtered* groups (a category with no
  PEEK apps shows no PEEK header), while the tally keeps printing the unfiltered book
  (§3.2). If the filtered catalogue is entirely empty, §5.3's single line answers —
  never three empty headers.

### 4.2 Dress — the slip's ink, the header family's geometry

Panzer R-12 item 9's ruling is my construction order: the slip's furniture craft
*transfers to the group headers*. Concretely, the header is a new composable,
`CatalogueGroupHeader`, built as follows — it is **not** `FullBleedSectionHeader`
(that family's grey is for sections; these headers are his rulings, and the slip's one
deliberate exception — *his decisions speak at full volume*, `SlipHeaderRow`'s dress —
is inherited on purpose):

- **Row**, height 20dp, vertically centred, full width, on the margin axis:
  - **Margin glyph, 10dp square** (the finish pass's margin-glyph size, R-12 items
    4–6), Panzer's solidity scale, one material at three densities: SHUT = solid ink
    fill; PEEK = ink at **55%** alpha fill; OPEN = **1.4dp ink frame**, transparent
    fill. This is the same mark the catalogue rows carry in their margin, at the same
    size, on the same axis — header and rows are one construction, and the glyph
    legend (§5.3) describes both. In Compose: a 10dp `Box`, `background(scheme
    .onBackground)` / `.copy(alpha = 0.55f)` / `border(1.4.dp, scheme.onBackground)`.
  - `Spacer(8.dp)`.
  - **Word:** `PEEK` / `SHUT` / `OPEN` — Atkinson Hyperlegible Bold, **11.5sp**,
    letterSpacing 0.6sp, `onBackground`. (The `SlipSectionHeader` dress, promoted to
    the header family's geometry. Sora does not guest here.)
  - **Count, right-aligned:** the group's size as Anonymous Pro Bold 11.5sp,
    `onBackground`, in the `FullBleedSectionHeader` right-side position — but with
    **end padding 0** (R-12 item 1–2: the 3dp off-axis pad dies; my header is born
    without it, whatever the older family still carries). The count is not a second
    home for the tally's number: it is the section's own label and the jump's landing
    confirmation — you tap `4 PEEK` at the top, you land on a header that says
    `PEEK · 4`, and the two prints agreeing *is* the arrival proof. It is the list's
    size (`group.size`), never a recomputed count — it cannot disagree.
- **The PEEK contract line — PEEK header only, nowhere else on this screen** (R-6):
  a second line inside the PEEK header item, under the row: Anonymous Pro **11sp**,
  `onSurfaceVariant`, letterSpacing 0.4sp, `maxLines = 1`, `TextOverflow.Ellipsis`
  (R-12's ellipsis law), padding top 2dp. Text **derived at render, never hardcoded**:
  `"${PEEK_DURATION_SECONDS / 60} MINUTES, THEN SHUT AGAIN · " +
  "${PEEK_COOLDOWN_MILLIS / 60_000} MIN BETWEEN PEEKS"` → prints
  `2 MINUTES, THEN SHUT AGAIN · 20 MIN BETWEEN PEEKS`. If the constants change, the
  print follows — a hardcoded copy would become the next audit's C2.
- No hairline, no rule, no background panel, no accent anywhere in the header.
  Sections separate by gap + label (the screen's S7 law).

### 4.3 Behaviour — sticky, and silent

- Each group header is emitted with **`stickyHeader(key = "group_header_<tier>")`**,
  `Modifier.background(scheme.background)` over its full item height (rows slide
  *under* a stuck header, never through it — an unfilled sticky header is a
  transparency bug, and at 61 OPEN rows it would smear the whole scroll). This is
  also how merge item 3's "sticky catalogue header" intent lands at the level that
  matters: not the search block pinned, but the *ruling you're inside* pinned.
- A stuck header is pixel-identical to its in-flow self — same glyph, word, count,
  contract line. No shadow, no elevation, no condensed form. Stickiness is a
  position, not a costume change.
- The header is **not clickable**. It names a place; it does nothing. (The tally
  segments are the doors; doors and nameplates are different furniture — R-10's
  name-is-not-a-door ruling, applied to my own surface.)
- The PEEK contract line sticks *with* its header (it is inside the same
  `stickyHeader` item) — while he scrolls the PEEK rows, the terms of PEEK stay
  printed at the top. That is the contract's one home doing its job for the whole
  length of its section, not just at its gate.

---

## 5. Spacing rhythm — the whole surface on one law

Screen constants I do **not** touch: LazyColumn horizontal padding 12dp,
`verticalArrangement = spacedBy(12.dp)`, bottom content padding 100dp, catalogue row
height 54dp. My rhythm is built from the 12dp item gap plus internal padding, and
nothing else — no new gap values enter the screen from this surface.

| Junction | Distance | Construction |
|---|---|---|
| Status sentence → tally | 12dp | item gap (banner item collapses between them when empty — existing behaviour, not mine to fix) |
| Tally slot | 32dp fixed | §3.1; segment columns fill it |
| Tally → LOADOUT header | 12dp + header's own 16dp spacer | the section gap law, unchanged |
| ALL APPS header row → search field | 8dp | existing spacer in the head block, unchanged |
| Filter words → glyph legend | 8dp | spacer inside the head block (§5.3) |
| Head block → first group header text | 12dp (gap) + 8dp (header top pad) = **20dp** | §5.1 |
| Group header row → first row of its group | 12dp | item gap (PEEK: + 2dp + contract line 14dp first) |
| Row → row inside a group | 12dp | item gap over the 54dp row — the acquitted cadence |
| Last row of a group → next group header text | 12dp (gap) + 8dp (header top pad) = **20dp** | the group breath: exactly the head-block junction, so every group boundary in the catalogue has the same weight |
| Last catalogue row → next section | 12dp + that section's own spacer | unchanged |

### 5.1 The header's internal spacing

`CatalogueGroupHeader` item = `Column(Modifier.background(bg).padding(top = 8.dp))`:
glyph/word/count Row (20dp), PEEK-only contract line (+2dp pad, 14dp line). In-flow
item height: **28dp** (SHUT/OPEN), **44dp** (PEEK). When stuck, the 8dp top pad is
the header's breathing room against the viewport edge — it is part of the item, so
the stuck and flowing forms have identical geometry (§4.3).

### 5.2 Why 20dp at group boundaries and 12dp everywhere else

One distinction, expressed once: 12dp separates *items*, 20dp separates *rulings*.
His eye learns exactly one new interval on this surface, and it always means the
same thing — the ruling changed. Anything more (rules, panels, bigger gaps per
group) would be three more intervals to learn for the same fact the glyph already
carries.

### 5.3 The catalogue head block — three riding fixes, all inside my surface

The ALL APPS head block (existing single item: header + search + filter words) gains:

- **Glyph legend, once**, as the block's last element: `Spacer(8.dp)` + one line,
  Anonymous Pro 11sp, `onSurfaceVariant`: `■ SHUT · □ PEEK · NO MARK — OPEN`,
  testTag `catalogue_glyph_legend`. (Marks are Panzer's solidity glyphs per R-10/R-12;
  if his machining changes the marks, the legend's characters change in the same
  build — legend and marks are one fact, FORM §3.1.) It prints above the first group
  header and nowhere else; under it, the rows' margin glyphs need no further
  explanation anywhere on the screen.
- **`SHOWING n` only while filtered** (merge item 3): when `searchQuery.isBlank() &&
  selectedCategory == null`, the ALL APPS header's right side prints **nothing**
  (pass empty prefix/number to `FullBleedSectionHeader` — its right side already
  suppresses when all three are empty). Unfiltered, the header's count would be a
  third copy of the book's total, which the tally already prints as a sum.
- **Placeholder:** `Filter apps…` → **`SEARCH APPS`** (R-6, Muede T6 — it is a search;
  the word sits in my head block, so the patch rides here, flagged for the wording
  pass to cross-check, not duplicated there).
- **The empty-filter answer:** if `filteredApps.isEmpty()`, exactly one line item
  follows the head block — search active: the existing `NO APP BY THAT NAME ON THIS
  PHONE` (existing testTag `shield_no_match_line`, unchanged); category-only filter:
  `NONE IN THIS FILTER`, same dress (Anonymous Pro 11sp, `onSurfaceVariant`). No
  group headers, no other void. An absence prints as a fact, once.

---

## 6. Semantics (R-11, my surface's share)

Zero `contentDescription`s is disqualifying at the audit's bar; my surface's share:

- Tally segment (live): `"9 shut. Jump to the shut apps."` — per segment, count from
  `tallyShown`, exposed as a button with a click action. Tally segment (zero/inert):
  `"0 open."` — a stat, no action, not focusable as a control.
- Group header: `"Peek, 4 apps."` / PEEK adds `"Peek: 2 minutes, then shut again.
  20 minutes between peeks."` Headers are headings for TalkBack navigation, not
  controls.
- The tally Row as a whole carries no separate description — its segments are the
  semantics; a container description would double-read the numbers.

---

## 7. Exact patches for MONTEUR (ShieldMatrixScreen.kt unless named)

In source order. Line anchors are to the working tree at writing; the slip block's
own comments name each region if lines have moved.

- **P-1 · Counts (§3.2).** Delete the two `count {}` derivations of `shutCount` /
  `peekCount`; derive from `remember(allApps) { allApps.groupedByRule() }`
  (`rules.shutCount`, `rules.peekCount`, `openCount = allApps.size − …`). Re-add the
  `groupedByRule` import (the in-flight import switch to `groupedForCatalogue`
  keeps both). `tallyNow` / `tallyShown` / the 250ms `LaunchedEffect` stay verbatim.
- **P-2 · Tally moves (§3.1).** Delete the whole §3.5 block (`if (searchQuery
  .isBlank()) { … } else { … }`). Insert one `item { TallyHeadline(...) }` directly
  after the ratchet-banner item, before the LOADOUT item. No header above it.
- **P-3 · `StandingTallyLine` → `TallyHeadline` (§3.2–3.4).** Rebuild as: fixed
  32dp slot; one `AnimatedContent(tallyShown, 260ms EasingA fade)` wrapping a `Row`
  (testTag `standing_tally`) of three `TallySegment`s + two `·` separators.
  `TallySegment(count, word, live, onJump)`: per-segment `tallyText`-grammar
  `AnnotatedString` (numeral Anonymous Bold **15sp**, word Atkinson Bold 11.5sp /
  0.6sp), `Column(height 32dp, widthIn(min 48dp))`, bar `18×2dp` ink + 3dp spacer
  **only when live**, semantics per §6, testTags `tally_segment_shut` / `_peek` /
  `_open`. `tallyText(shut, peek, open)`'s full form is reused per segment; its
  folded (`open = null`) form has no remaining caller — delete that branch.
- **P-4 · Jump plumbing (§3.3).** `groupHeaderIndex: MutableMap<AppBlockTier, Int>`
  + `nextItemIndex` counter maintained at every emission from the first item to the
  last group (law comment at the counter); `pendingJump` state; segment tap =
  jump-now (unfiltered) or clear-filters + `pendingJump` (filtered);
  `LaunchedEffect(catalogueGroups, pendingJump)` scrolls via the tally's
  `rememberCoroutineScope` + `listState.animateScrollToItem(index, 0)`.
- **P-5 · Slip machinery deletion (R-1's ~200 lines).** Delete: `restingFlips`,
  its sweeper `LaunchedEffect`, the search-fold `LaunchedEffect`, `slipTap`,
  `slipLines`, `RestingFlip`, `SlipLine`, `SlipLineRow`, `SlipNoneLine`,
  `SlipSectionHeader`, `SlipHeaderRow`, and `tierRank` if it has no other caller
  (check first — if the ViewModel-facing code uses it, it stays; the slip's copy
  goes). Imports orphaned by the deletion go in the same pass. **The resting-flip
  choreography is NOT hoisted onto catalogue rows in this merge** (my ruling,
  §8): the sled's 1:1 drag is already feedback at the control touched (R-7's home);
  a flipped row re-sorts into its new group on recomposition, instantly and calmly.
- **P-6 · Grouped catalogue (§4, §5).** Replace §5's flat
  `items(filteredApps, …)` with, per `(tier, group)` in `catalogueGroups`:
  `stickyHeader(key = "group_header_<tier>") { CatalogueGroupHeader(...) }` then
  `items(group, key = { it.packageName }) { CatalogueAppRow(...) }` — row call
  verbatim. The no-match line (§5.3) stays an item after the groups.
- **P-7 · `CatalogueGroupHeader` (§4.2, §5.1).** New private composable per spec:
  glyph `Box` (solid / 55% / 1.4dp frame by tier), word, right count =
  `group.size`, `padding(top = 8.dp)`, `background(scheme.background)`; PEEK-only
  contract line built from `MonolithRepository.PEEK_DURATION_SECONDS` /
  `PEEK_COOLDOWN_MILLIS` (companion constants — import, never literal-copy the
  numbers). testTags `group_header_peek` / `_shut` / `_open`.
- **P-8 · Head block (§5.3).** In the ALL APPS item: conditional `SHOWING`
  (empty right side when unfiltered), placeholder → `SEARCH APPS`, legend line
  appended (testTag `catalogue_glyph_legend`), category-empty line variant.
- **P-9 · Nothing else.** `CatalogueAppRow`, the sled, row height, list padding,
  bottom clearance, `listState` saveable behaviour: untouched. If a patch above
  seems to require touching them, stop and bring it to VOGT — it means this spec
  was misread, not that the row needs work.

Estimated shape (for VOGT's merge accounting, not a promise): P-5 deletes ~230
lines of slip machinery; P-2/P-3/P-4/P-6/P-7/P-8 add ~190 lines of headline,
header, and plumbing. The surface *shrinks* while gaining the jumps — R-13's
budget rule is satisfied by construction: R-1's new surface is paid for by the
slip, in full, in the same screen.

---

## 8. Calm at scale — the three loads, walked

**0 apps** (`allApps` empty — fresh phone, or the catalogue not yet landed):
tally prints `0 SHUT · 0 PEEK · 0 OPEN`, all three segments inert, slot still
32dp — the screen's upper geometry is identical to its loaded self, so nothing
jumps when the first emission lands. Catalogue: no headers, the §5.3 line (or
silence above an empty filter result). Nothing on the surface performs emptiness;
zero is printed as a fact and the screen moves on.

**7 apps** (say 2 PEEK · 5 SHUT · 0 OPEN): two headers, seven rows — the whole
catalogue is ~560dp and sits on the first screen under cap + routines with room
to spare. No folding, no SHOW ALL, no "view more" at any size: a fold would be a
second state for him to verify, bought to save scroll the grouped order already
saved. The OPEN segment in the tally is inert (`0`), its header absent — the
book still balances: 2 + 5 + 0 = 7, printed.

**74 apps** (the audit's own example: 9 SHUT · 4 PEEK · 61 OPEN): pre-catalogue
scroll attributable to my surface is now **O(1)** — the tally is one 32dp line
whether the book holds 7 rulings or 700; the slip's +52dp-per-ruling growth law
is dead with the slip. In the catalogue: PEEK's 4 rows (~264dp) are the first
catalogue viewport entire — the urgent read never waits; SHUT's 9 follow
(~594dp); OPEN's 61 (~4,000dp) are the inert tail, alphabetical, with the OPEN
header stuck at the top the whole way down so position is never a guess, and
the tally's OPEN jump landing him directly at its head. Total surface cost of
the de-duplication at this size: −(13 slip lines × 40dp + 2 slip headers +
fold machinery) ≈ **−560dp and one entire commit path**, against +3 headers
(~100dp) — and every app's name now prints exactly once, on the row that can
change it. At 740 apps the arithmetic scales the same way; the only print that
widens is a numeral (3-digit segments still fit a 360dp line: ≈65dp per
segment, ≈220dp the row — §3.2's no-wrap law is safe to four digits).

**Calm, defined for this surface:** nothing moves except the tally's governed
re-strike and the sled under his own thumb; nothing is counted twice; nothing
empty performs; the longest list in the app is also its most uniform. If it
feels busy, the bug is in this spec, not in the count of his apps.

---

## 9. Refinements & conflicts — flagged for VOGT, decided as follows unless struck

1. **In-catalogue `NONE` (FORM §3.1) vs landed `groupedForCatalogue` doc + R-1:**
   I follow the landed code — no empty groups, no `NONE` in the catalogue; the
   tally's zero is the absence's one home. FORM's `NONE` would give the absence
   two prints and cost ~28dp per empty group forever.
2. **Jump bars (FORM §3.1 "door grammar permanently"):** I make the bar
   conditional on the segment being a real door right now (count > 0). A bar
   under a zero is an affordance that lies. The *grammar* is permanent; its
   presence is truthful.
3. **Tally order vs group order (§2):** retained asymmetry, ruled — ledger
   order in the tally, urgency order in the groups.
4. **Resting-flip choreography:** retired with the slip, not hoisted (P-5).
   FUNKE §5a offered the hoist as an option; the sled's own feedback + R-7's
   at-the-control refusal cover the moment. If the phone proves the instant
   re-sort jarring, the hoist is the named follow-up — same state machine,
   catalogue rows — not a slip resurrection.
5. **Sticky group headers** go slightly beyond R-1's letter (which specifies
   grouping + jumps, not stickiness) and land merge item 3's sticky intent at
   group level. Priced inside P-6/P-7; if scope is cut, non-sticky headers
   still satisfy every other line of this spec — the jumps and counts are
   unchanged. This is the first thing to cut and the last thing I would.

**Verification (paper claims end here — these are Ren's/device checks, priced
into the merge, not performed by me):** tally sums to `allApps.size` at 0 / 7 /
74; each live segment's jump lands its header at viewport top (this is the
§3.3 counter's proof); a sled flip re-strikes the tally once, ≤250ms later,
whole-line; flipping the last PEEK app removes its bar in the same strike and
its header on recomposition; PEEK contract line matches the repository
constants; stuck OPEN header stays legible over scrolling rows in both themes.

---

## Draft GitHub comment (for VOGT to post)

— GLANZ (FINISHER), for VOGT's issue #7 cell.

Finish spec delivered: `studio/cells/issue-7/finisher-glanz.md` (DECLARED — spec only, no code edited, nothing compiled). My slice is R-1's surface taken to gallery standard: the state-only tally headline and the single grouped catalogue.

Decisions MONTEUR builds from (§7 is the exact patch list, P-1…P-9): **Tally** — promoted to its own item directly under the status sentence/banner, no `YOUR RULES` header resurrected; fixed 32dp slot so re-strikes never move the cap; print grammar unchanged (Anonymous Pro Bold numerals / Atkinson Bold words) with numerals raised to 15sp as the surface's first landing; counts folded from the unfiltered `groupedByRule()` so search/filter can never bend them. Re-strike manners kept whole — 250ms coalesce, one 260ms Curve-A whole-line strike, never a roll — plus one extension: jump bars render inside the strike, so a segment hitting zero loses its door in the same frame its numeral changes. **Jumps** — live segments wear the screen's one door grammar (ink word + 18×2dp bar, `FilterWord` geometry); a zero segment wears no bar and does nothing, because a door painted on a wall is the C5 lie in reverse; a tap under an active filter clears the filter first, then scrolls via `pendingJump`; scroll indices are counted at emission (DSL-order counter), never hardcoded, so the prune note and R-3's websites move can't break the landings. **Group headers** — new `CatalogueGroupHeader`: PEEK → SHUT → OPEN, sticky with a filled background, 10dp solidity glyph on the rows' margin axis (solid / 55% / 1.4dp frame), word in the slip's full-ink dress, right count = the group's own list size with the 3dp off-axis pad never born; PEEK's header alone carries the contract line, derived from `PEEK_DURATION_SECONDS` / `PEEK_COOLDOWN_MILLIS` at render, and it sticks with its header. Empty groups print nothing — the tally's zero is the absence's one home. **Rhythm** — one new interval only: 12dp between items, 20dp between rulings. **Calm** — pre-catalogue cost of this surface is now O(1) in the number of rulings (the slip's +52dp-per-ruling law is dead); at the audit's 74 apps, PEEK's 4 rows are the first catalogue viewport entire and every name prints exactly once, on the sled row that can change it. Net surface change ≈ −40 lines: the slip pays for everything (R-13 satisfied by construction).

Flagged for VOGT in §9, decided unless struck: no in-catalogue `NONE` (against FORM §3.1, with the landed `groupedForCatalogue` doc); jump bars conditional on a real destination; tally keeps ledger order (SHUT first) while groups keep urgency order (PEEK first); resting-flip choreography retired with the slip, not hoisted; sticky headers are my one step beyond R-1's letter — first cut if scope demands, everything else survives without them.

— GLANZ

---

*GLANZ, finisher, VOGT's issue #7 cell — Oct 30, 2026. DECLARED: specification
only; no app code edited, nothing compiled, no tests run, nothing committed or
pushed. GitHub coordination is VOGT's.*
