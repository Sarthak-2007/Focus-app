# Issue #7 — Shield (audit fixes) — Finisher Polish List: POLITUR

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield (audit fixes)”
**Seat:** POLITUR, FINISHER 5, in VOGT's issue #7 cell
**Branch / tree:** `agent/issue-7`, shared clone `builds/cells/issue-7/`
**Status:** DECLARED — polish specification only. No file under `app/src/` edited
(MONTEUR is sole code editor; every item below carries the exact patch to apply),
nothing compiled, no tests run, no git operations.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-12 items 1–2 are this
slice's charter; R-5/R-6/R-7 as they touch dress); `designer-form.md` (FORM) and
`innovator-funke.md` (FUNKE) from this cell; the working-tree Shield as MONTEUR's
pass stands — `ui/screens/ShieldMatrixScreen.kt`, `ui/ShieldBlockingStatus.kt`,
`ui/components/MonolithCustomComponents.kt` (`FullBleedSectionHeader`,
`rememberMixedTypographyText`, `inkControlFill`), `ui/theme/Color.kt`,
`ui/theme/Theme.kt`, `ui/theme/Type.kt`.
**Scope, exactly:** micro-finishing of everything the fix touches — (a) one header
family, (b) the padding/gap law around the new status line and the tally,
(c) ellipsis behaviour on right-side header words, (d) light/dark parity of every
new word and marker, (e) removal of visual double-meaning. Everything else
(cap machining, sled truths, wheels, semantics) is another seat's slice; where
the fix's own words are already specced by FORM, this list enforces her values
against the code as it stands, and says so.

Contrast figures below are computed from the token hexes in `Color.kt` /
`Theme.kt` (WCAG relative-luminance formula), not estimated from screenshots.

---

## The five finished-state laws (the list below is these, applied)

- **L1 — One header family.** Every surviving section header on Shield is
  `FullBleedSectionHeader`: Atkinson Hyperlegible Bold 11.5 sp, letterSpacing
  0.6 sp, internal 16 dp top spacer, title left / mixed-grammar right side,
  right edge flush with the 12 dp page axis. Two emphasis levels only: grey
  title (`onSurfaceVariant`) for ordinary sections, ink title (`onBackground`)
  for his decisions (YOUR RULES + the catalogue group headers). The status
  line is **not** a family member and is never “unified” down to 11.5 sp.
- **L2 — One gap law.** Section separation is always **12 + 16 = 28 dp**
  (the LazyColumn's `spacedBy(12.dp)` + the header's internal 16 dp spacer —
  never a third term). Content under a header sits **8 dp** below its label
  when header and content share one LazyColumn item; **12 dp** (the column's
  own spacing, nothing added) when they are separate items. A hidden element
  costs 0 dp and 0 gaps.
- **L3 — Ellipsis law.** In any header row the **title ellipsises first**;
  a right-side word never wraps, never pushes the title off-axis, and
  ellipsises only when the title has nothing left to give. Construction that
  guarantees it: right side unweighted (measured first), title
  `Modifier.weight(1f)`, both `maxLines = 1, overflow = Ellipsis`.
- **L4 — Parity law.** Every new word/marker takes its colour from a scheme
  token or from `onBackground` at a named alpha — identical construction in
  both schemes, never a hardcoded hex, never a token whose meaning changes
  between schemes. A state that is 3.5:1 in one scheme and 6.5:1 in the
  other is not the same print.
- **L5 — One state, one dialect.** Each fact gets exactly one visual carrier:
  the wall's standing = status verb + solidity glyph; the counts = the tally;
  a routine's firing truth = its cap word; “running” = one bold ink suffix.
  Colour never re-says what a word already says, and no element borrows
  another fact's dress (alarm orange, ON's solid fill) to say it louder.

---

## The numbered polish list — current state → finished state

### Headers (R-12 item 1)

**1. Three header dialects → one family.**
Current: three composables print section headers on/around this screen —
`FullBleedSectionHeader` (grey title, `MonolithCustomComponents.kt:123`),
`SlipHeaderRow` (ink title, own 16 dp spacer, own right-side builder —
renamed `RulesHeaderRow` at its two call sites in MONTEUR's pass but still
*defined* as `SlipHeaderRow`, `ShieldMatrixScreen.kt` ~L845), and
`SlipSectionHeader` (ink, `padding(top = 4.dp)`, no right side — the slip's
relic, and the obvious template MONTEUR would reach for for the not-yet-written
`CatalogueGroupHeader`).
Finished: `SlipHeaderRow` and `SlipSectionHeader` are **deleted**.
`FullBleedSectionHeader` gains one parameter, `titleInk: Boolean = false`
(title colour `onBackground` when true, `onSurfaceVariant` when false).
YOUR RULES calls it with `titleInk = true`; `CatalogueGroupHeader` is built
from it with `titleInk = true` plus the right-side count (item 11) and, for
PEEK only, the contract line as a second row inside the same item
(Atkinson Bold 11.5 sp words / Anonymous Pro Bold numerals, `onSurfaceVariant`,
`padding(top = 2.dp)` — inside the header's item, so it costs no extra gap
under L2). Exact patch (MONTEUR): add the parameter at
`MonolithCustomComponents.kt:123–130`, switch the title `color =` at :147,
replace both `RulesHeaderRow(...)` call sites (Shield ~L342, ~L349) with
`FullBleedSectionHeader(titleWord = "YOUR RULES", titleInk = true, …)`,
delete `SlipHeaderRow` / `SlipSectionHeader` bodies.

**2. The 3 dp off-axis pad dies — in both places it lives.**
Current: the header's right-side `Text` carries `Modifier.padding(end = 3.dp)`
(`MonolithCustomComponents.kt:159`), and `SlipHeaderRow`'s right side repeats
it. Every right word on the screen therefore sits 3 dp inside the axis that
the cap, the sleds, the website REMOVE chips and the status line all end on —
the eye reads the header's right column as *almost* aligned, which is worse
than unaligned.
Finished: `padding(end = 3.dp)` deleted, no replacement. Right edge of every
header word lands exactly on the LazyColumn's 12 dp horizontal padding axis.
(This is R-12 item 2's pad, named in the audit; it currently exists twice.)

**3. Header row construction — the title gets the weight, the right side gets measured first.**
Current: `FullBleedSectionHeader`'s Row is `SpaceBetween` with two unweighted
`Text`s, neither with `maxLines` or `overflow` (MonolithCustomComponents
:136–161). A long title or right word wraps to a second line and the header
quietly becomes two rows tall, breaking L2's arithmetic for everything below.
Finished: title `Text` gains `Modifier.weight(1f)`, `maxLines = 1`,
`overflow = TextOverflow.Ellipsis`; right `Text` gains `maxLines = 1`,
`overflow = TextOverflow.Ellipsis`, `Modifier.padding(start = 12.dp)`
(the 12 dp is the minimum title↔right gutter, replacing the dead 3 dp end
pad). Because the unweighted right side is measured first, L3 holds by
construction, not by hope.

**4. Ellipsis behaviour, right-side words — the inventory, word by word.**
Current: no right-side header word on Shield has any truncation behaviour.
The cases, longest first: the folded tally under search (`7 SHUT · 2 PEEK`,
~15 glyphs of mixed 11.5 sp ≈ 105 dp), `BLOCKED WEBSITES` + `12 SITES`,
`ALL APPS` + `SHOWING 148`, `ROUTINES` + `2 ARMED`.
Finished: all four ride item 3's construction. The law per word: right side
**never wraps** (a two-line “SHOWING / 148” is the current failure mode);
the title ellipsises first (`BLOCKED WEBSITES` → `BLOCKED WEBSIT…` before
`12 SITES` loses a glyph); the right side ellipsises only in the pathological
case, from its tail, never its number — so if a right word ever must clip,
MONTEUR clips the *suffix word*, not the mono numeral (the numeral is the
fact; “ARMED” is its label). No font-size reduction anywhere as a fitting
device — see item 13 for the same law on the routine cap.

### The status line (R-5, as dressed by FORM §2.2)

**5. Status type — one dialect, not a third.**
Current (MONTEUR's pass, Shield ~L215): the whole headline is
Anonymous Pro Bold 13 sp, no letterSpacing, no lineHeight — a third type
dialect that is neither the tally's mixed grammar nor FORM's spec, and at
13 sp mono the longest headline (`NOT BLOCKING — SESSION RUNNING,
ENFORCEMENT NOT PROVEN`, 54 glyphs ≈ 420 dp at ~7.8 dp/glyph) cannot fit
the 336 dp measure and wraps.
Finished (FORM §2.2, enforced): Atkinson Hyperlegible Bold **14 sp**,
letterSpacing **0.4 sp** for the words; the `ENDS HH:MM` numerals in
Anonymous Pro Bold via the tally's existing AnnotatedString pattern
(`tallyText`'s mono/word SpanStyle construction, Shield ~L820) — one mixed
grammar for the whole top of the screen. Exact patch: status `Text` at
Shield ~L215 becomes `text = statusAnnotated` built by a small builder
beside `tallyText` (word spans Atkinson Bold, the `HH:MM` span Anonymous
Bold), `fontSize = 14.sp`, `letterSpacing = 0.4.sp`.

**6. Status slot — fixed height, one line, segments yield in FORM's order.**
Current: bare `Text`, no height, no `maxLines` — a state change that wraps
moves the cap, the routines and the tally by a full line (~17 dp) under
his eyes; the screen's first fact is its least stable object.
Finished: the status item is a fixed **28 dp** slot (`Modifier.height(28.dp)`,
content vertically centred), `maxLines = 1`. Truncation follows FORM §2.3
and is a *derivation* duty, not a rendering accident: session label
ellipsises first, `ENDS …` drops second, the Safe Break segment never
drops, the verb never truncates. `TextOverflow.Ellipsis` on the `Text` is
the backstop only — if it ever fires, the derivation (FUNKE/MONTEUR's
`ShieldBlockingStatus`) has a string it shouldn't have produced.
testTag `shield_blocking_status` stays, moved to the slot.

**7. Status colour — the parity failure, killed. Ink says it; colour doesn't.**
Current: `color = if (isBlocking) scheme.onBackground else scheme.error`
(Shield ~L222). Measured consequence: NOT BLOCKING prints at **3.49:1 in
the light scheme** (`error` = TokenAlert `#D63E06` on `#E4DFDA`) and
**6.48:1 in the dark scheme** (`error` is re-mapped to the night accent
`#F47442` on `#171412`, Theme.kt:73) — the same words at nearly half the
contrast, in a *different hue family*, purely because the scheme changed.
It is also a double-meaning (L5): orange is this screen's alarm/accent
dialect (the ratchet banner's `primaryContainer` fill) — a standing fact
is borrowing an alarm's voice.
Finished (FORM §2.2, enforced): **no `error` token on this line, ever.**
`BLOCKING NOW` and `NOT BLOCKING` both print in full ink `onBackground`
(**10.79:1 light / 10.95:1 dark** — the only pair on the screen that is
already parity-true). The unproven state (FUNKE's UNPROVEN / STALE /
no-proof-supplied fallback) prints at `onBackground.copy(alpha = 0.55f)`
— Panzer's middle density, one construction in both schemes. State is
carried by verb + glyph (item 8), never by colour alone. Exact patch:
replace the colour branch at Shield ~L222 with a `when (blockingStatus?.proof)`
mapping to those two values; the no-status fallback counts as unproven
(55%), never as error.

**8. Status glyph — exactly one marker, in the solidity scale.**
Current: the line has no glyph at all; state rides on words + the broken
colour of item 7.
Finished (FORM §2.2 + R-12's solidity scale): one **10 dp square** at the
slot's left, text starting 8 dp after it: **solid ink** = BLOCKING NOW ·
**hollow 1.4 dp frame** (`border(1.4.dp, onBackground)`, no fill) = NOT
BLOCKING · **55% ink** = unproven. Square, not circle — the app's geometry
is caps and squares, and the mark rhymes with the catalogue's margin
glyphs (R-12 item 6: 8→10 dp, same scale) so one material reads at both
sizes: solidity = enforcement strength, at the wall and at the app.
Double-meaning guard: this glyph is the **only** marker the line may
carry — no coloured dot beside it, no second icon; and it never appears
on any other line of the top block (the tally stays glyph-less; its
numbers are its carrier).

### The gap law around status and tally (R-12 item 2)

**9. The hidden banner currently costs 40 dp; the law says a hidden thing costs 0.**
Current arithmetic: status item → banner item → LOADOUT item =
12 (column gap) + 0 (collapsed `AnimatedVisibility`) + 12 (column gap) +
16 (header spacer) = **40 dp** between the status text and the LOADOUT
label when no refusal exists — versus 28 dp everywhere else L2 holds.
The most common state of the screen is the one that breaks the law.
Finished: the banner leaves its own LazyColumn item and becomes the
second child of the status item's `Column` (`Arrangement.spacedBy(12.dp)`,
rendered only when the message is non-blank — the `AnimatedVisibility`
is dropped with the move; a refusal banner that needs an entrance can
re-strike like the tally, it may not tax every quiet visit 12 dp).
Result in both states: status → next header label = **28 dp**; status →
banner (when present) = **12 dp**; banner → next header label = **28 dp**.
Exact patch: merge Shield's items at ~L209 and ~L236 into one item;
delete the standalone banner item.

**10. Under-header gaps — 4 and 6 become 8; the tally's 8 is the law's witness.**
Current: content under a header label sits at 8 dp (LOADOUT cap, Shield
~L283; ALL APPS search box, ~L386; tally under YOUR RULES, ~L343) but
**4 dp** under ROUTINES (Spacer at ~L318) and the reset note hangs at
`padding(top = 6.dp)` (~L306) — three values for one relationship, and
the routines block is the tightest on the screen for no reason.
Finished: every under-header gap on Shield is **8 dp**: ROUTINES Spacer
4→8 dp; reset-note top padding 6→8 dp (the note, when present, *is*
content under the header and obeys the same 8; the Spacer after the
header stays 8 whether the note prints or not, so rows shift only by
the note's own height). The one codified exception: catalogue group
headers are separate LazyColumn items, so group-label → first row is
the column's **12 dp** with nothing added — uniform across all three
groups, and the group header item must therefore contain **no** bottom
padding and no trailing spacer. Wherever the tally finally lands
(FORM §2.1 puts it directly under the status block; MONTEUR's current
pass still prints it below ROUTINES), items 9–10 hold unchanged — the
law is order-agnostic by construction.

### Parity of the fix's other new words and markers

**11. Tally and header right sides — two builders of one grammar become one builder.**
Current: the header right side is built by `rememberMixedTypographyText`
(MonolithCustomComponents :77 — Atkinson Bold words in `onSurfaceVariant`,
Anonymous Bold numeral in `onBackground`) while the tally is built by
`tallyText` (Shield ~L820 — same families/weights, both spans in
`onBackground`). Two code paths print what the eye must read as one
grammar; they already differ in word colour, so the tally's SHUT and a
header's ARMED are cousins, not siblings.
Finished: `tallyText` is re-based on the one builder — a single
`mixedLine(...)` used by the header, the tally, the folded tally, and
the group-header counts, with the colour split as its only parameter:
header right sides keep grey words + ink numeral; the tally and group
counts print ink words + ink numeral (his decisions at full volume —
the one deliberate emphasis L1 allows). Group headers print their count
through this same segment (e.g. right side = the tally segment for that
tier), so “9” in the tally and “9” in the SHUT group header are the
same glyphs in the same dress — a restatement, not a second dialect.

**12. RUNNING NOW — out of the grey string, into its own carrier.**
Current (MONTEUR's pass, RoutineRow ~L654): the marker is concatenated
into the meta `Text` — Anonymous Pro **12 sp, regular weight,
`onSurfaceVariant`** — indistinguishable from the days/loadout words it
rides on, and at the dark scheme's muted tier (`#776A60`, **3.51:1**)
the screen's only true “now” on a routine is its faintest print. The
meta line also has no `maxLines`: `MON TUE WED THU FRI SAT · SLEEP ·
RUNNING NOW` wraps, and collapsed rows stop sharing a height.
Finished (FORM §3.2's word, finished): the meta line becomes a `Row` —
days · loadout as one `Text` (`weight(1f)`, `maxLines = 1`,
`overflow = Ellipsis`, unchanged 12 sp `onSurfaceVariant`), and
`RUNNING NOW`, when present, as its own non-shrinking `Text`:
Anonymous Pro **Bold**, **12 sp** (the host line's size — one optical
baseline), colour **`onBackground`** (10.79:1 / 10.95:1, parity-true),
`padding(start = 6.dp)`. The schedule words ellipsise; the marker never
does. Dress separation (L5): the marker stays small, bold and ink —
it must never borrow the headline's 14 sp or its glyph, or the screen
grows two “NOW”s in one dialect and neither is believed.
Exact patch: replace the concatenated `text =` at RoutineRow ~L654
with the Row above; `routineRunningNowSuffix` keeps returning the
marker string, the Row decides whether it prints.

**13. WON'T FIRE cap — the word keeps its size; the dress stops saying ON.**
Current (RoutineRow ~L673): two defects in one cap. (a) The font shrinks
to fit — `fontSize = if (capWord.length > 3) 10.sp else 11.sp` — so the
screen's most important new word is also its smallest cap word, in a
size no other cap uses. (b) The fill keys off `routine.enabled` alone,
so a WON'T FIRE cap wears solid `inkControlFill()` + `onInkControlFill()`
— ON's exact dress — while its word says the opposite: two dialects,
one object, contradicting each other.
Finished (FORM §3.3, enforced): **11 sp for all three cap words, always**;
the cap sizes to its payload — `widthIn(min = 48.dp)` +
`padding(horizontal = 6.dp)` already in MONTEUR's pass gives WON'T FIRE
(10 glyphs ≈ 66 dp at 11 sp Anonymous Bold + 12 dp padding) a finished
width of **~78 dp**, height unchanged at 32 dp. Dress by word, not by
the raw boolean: `ON` = solid ink fill / on-ink text (unchanged);
`OFF` = `surfaceVariant` fill / variant text (unchanged); **`WON'T FIRE`
= `surfaceVariant` fill + 1 dp `outlineVariant` border + text in
`onBackground.copy(alpha = 0.55f)`** — the middle density again, the
same 55% the unproven status wears (item 7): one alpha for “present
but inert” everywhere on this screen, both schemes, no hardcoded grey.
No strikethrough (that strike is R-7's refusal grammar; borrowing it
here blurs both). Exact patch: in RoutineRow, derive
`when (capWord) { "ON" -> …, "WON'T FIRE" -> …, else -> … }` fill /
border / text-colour triples beside the existing `capWord` val, and
delete the length-conditional `fontSize`.

**14. Ratchet banner text — the last hardcoded hex on a touched surface.**
Current: banner text is `Color(0xFFFBF1EC)` on `scheme.primaryContainer`
(Shield ~L248) — a light-theme fossil that rides into the dark scheme
unchanged. Measured: **3.08:1** light (on `#F55314`), **2.55:1** dark
(on `#F47442`) — the refusal he most needs to read at 1 AM is the
least legible print the fix touches, and it gets *worse* in the dark.
Finished (R-7's tokenisation, with the number that settles it):
`color = scheme.onPrimaryContainer` — `#471602` in **both** schemes —
**4.43:1 light / 5.37:1 dark**, and the `Color(0xFFFBF1EC)` literal
leaves the file. (Same literal inside `BlockitActionChip`'s accent
branch, MonolithCustomComponents :193, is PARK/Deck furniture — out
of this slice, flagged for VOGT so it isn't “fixed” twice.)

**15. BLOCKED WEBSITES rows — the moved section obeys the ellipsis law too.**
Current (Shield ~L521): the domain `Text` (Anonymous Pro 14 sp) has no
weight, no `maxLines`, no overflow; a long domain wraps to two lines
or shoves the REMOVE chip off-axis — in the section R-3 just moved
~5,800 dp up the screen to be reachable.
Finished: domain `Text` gains `Modifier.weight(1f)`, `maxLines = 1`,
`overflow = TextOverflow.Ellipsis`; the REMOVE chip stays unweighted
and flush on the 12 dp axis (L3, same construction as item 3). The add
field below it already single-lines; no change.

**16. Dead slip furniture — the retired dialect leaves the file.**
Current: MONTEUR's pass retired the slip's lines but the file still
carries the slip's whole printing press — `SlipLineRow` (with the dot
leaders Dens F7 killed), `SlipLine`, `RestingFlip`, `SlipNoneLine`,
`tierRank`, and (until item 1 lands) `SlipSectionHeader`. Dead code in
a shared file is a dialect waiting to be reused by the next hand —
and `CatalogueGroupHeader` is being written in this same pass, one
`SlipSectionHeader` away from resurrecting the wrong family.
Finished: once items 1 and 11 land and a grep confirms zero call
sites, MONTEUR deletes the retired slip composables/data from
`ShieldMatrixScreen.kt` in the same commit-range as the fix (the
tally's builder and `StandingTallyLine` stay — they are live).
Finisher's note, not a ruling: if any of these prove still-referenced
at patch time, that reference is a finding for VOGT, not a reason to
keep the furniture.

### Double-meaning sweep — the finished state, one line per fact

| Fact | Its ONE carrier after this list | Killed second dialect |
|---|---|---|
| Wall standing now | Status verb + solidity glyph (items 5–8) | `error`-orange colour branch; palette mood |
| Counts of his rulings | Tally, one builder (item 11) | Header-builder vs tally-builder split; slip lines (already retired by R-1) |
| A group's local count | Same mixed segment as the tally (item 11) | A group header in `SlipSectionHeader` dress |
| Routine will fire | Cap word + its own dress (item 13) | ON's solid fill under a WON'T FIRE word; the header's old ACTIVE (already → ARMED by R-6) |
| Routine running now | Bold ink `RUNNING NOW` suffix (item 12) | Grey regular type identical to schedule words |
| Refusal | Banner words on a tokenised ground (item 14) | A light-scheme hex pretending to work at night |
| Section identity | One header family, two emphasis levels (item 1) | Ink-by-accident (slip) vs grey-by-default (sections) |

Out of slice, noted once so nobody re-files them as finishing: the
screen-wide night flip on session (`effectiveNight`) is standing app
law, not a second dialect of the status line; the glyph legend line
(R-10 / FORM §3.1) keeps `onSurfaceVariant` in both schemes — the dark
scheme's 3.51:1 is that token's designed mute tier, and L4 forbids
inventing a per-theme hex to “fix” it.

---

## Draft GitHub comment (for VOGT to post)

— POLITUR (FINISHER), for VOGT's issue #7 cell.

Finishing pass delivered: `studio/cells/issue-7/finisher-politur.md` (DECLARED — specification only; no `app/src/` edits, MONTEUR holds the code; nothing compiled or tested).

Sixteen numbered items, current state → finished state with values, each carrying the exact patch. The headlines:

1. **One header family** (R-12 item 1): `SlipHeaderRow`/`SlipSectionHeader` die; `FullBleedSectionHeader` gains a `titleInk` level for YOUR RULES + the catalogue group headers. The **3 dp off-axis pad dies in both places it lives** — every right word lands flush on the 12 dp page axis.
2. **Gap law**: section separation is 12 + 16 = **28 dp**, under-header content is **8 dp** (routines' 4 dp and the reset note's 6 dp rise to it). The hidden ratchet banner currently taxes the top block **40 dp** — it moves inside the status item, so hidden costs 0.
3. **Ellipsis law**: right-side header words never wrap; the title (weight 1f) ellipsises first; a right word, if ever clipped, clips its suffix word, never its numeral. Same construction applied to the websites rows.
4. **Parity, measured**: the status line's `scheme.error` branch prints NOT BLOCKING at **3.49:1 light / 6.48:1 dark** in two different hues — killed; both verdicts print in full ink (**10.79:1 / 10.95:1**), the unproven state at one 55% alpha in both schemes, state carried by verb + a 10 dp solidity glyph. The banner's hardcoded `0xFFFBF1EC` (**3.08:1 / 2.55:1**) becomes `onPrimaryContainer` (**4.43:1 / 5.37:1**).
5. **Double-meaning removed**: `WON'T FIRE` stops wearing ON's solid fill (middle density: variant fill, outline, 55% word) and stops shrinking to 10 sp — 11 sp for every cap word, cap ~78 dp wide. `RUNNING NOW` leaves the grey meta string for its own bold ink carrier that never ellipsises. Tally and header right sides merge onto one mixed-grammar builder. The retired slip's printing press (dot leaders included) leaves the file so the next hand can't resurrect the wrong dialect.

— POLITUR

---

*POLITUR, finisher 5, VOGT's issue #7 cell — Oct 30, 2026. DECLARED: polish
specification only; no code edited, nothing compiled, no tests run, nothing
committed or pushed.*
