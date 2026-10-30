# Issue #7 — Shield: IMPROVER ROUNDS (FEILE)

**Seat:** FEILE, IMPROVER, dedicated-100 programme, in VOGT's issue #7 cell.
**One job:** improve the cell's EXISTING output in visible rounds — its
**state wording** and its **Standing Orders presentation** — nothing else.
Never from zero: every round below starts from a named, quoted piece of
existing output (issue #7's body, `studio/plan/AUDIT-shield.md`, the cell's
fourteen filings in `studio/cells/issue-7/`, MONTEUR's landed code in
`ShieldBlockingStatus.kt` / `ShieldMatrixScreen.kt`, VOGT's wave-2 report
`studio/plan/ISSUE-7-WAVE2-REPORT.md`) and changes something concrete,
saying what got better and why.
**Status:** DECLARED — spec only. No app code edited by me, nothing
compiled, no tests run. Patches are worded for MONTEUR/VOGT to apply.
**Type law where type is specified:** Sora (display) / Atkinson
Hyperlegible (words) / Anonymous Pro (numerals, telemetry). **Colour law:**
no green or near-green anywhere in this file's specs; the status line's
full-ink law (wave-2 cure) is never weakened — state is carried by verb +
glyph, never by hue.

## Truths protected in every round (unchanged, restated so no round can drift)

- Shield prints whether blocking is live: `BLOCKING NOW — …` /
  `NOT BLOCKING`. `BLOCKING NOW` is earned only by the two session-keyed
  cascade witnesses (shield `protecting` for this session id + a11y
  `armedSessionId` for this session id, beat fresh under the repository's
  10 s gap law) — never from the session row's `isActive` flag alone.
- ROUTINES header: `ACTIVE` → `ARMED` (`liveCount` derivation untouched).
- `daysMask = 0` + enabled: the cap prints `WON'T FIRE`, never `ON`,
  never silence; `NO DAYS SET` stays in the meta as the printed cause.

---

## ROUND 1 — The status sentence speaks one grammar in all its forms

**Existing output worked on:** `computeShieldBlockingStatus` in
`ShieldBlockingStatus.kt` (four forms: LIVE / NO_SESSION / UNPROVEN /
STALE), the screen fallback in `ShieldMatrixScreen.kt`
(`statusHeadline … ?: "ENFORCEMENT NOT PROVEN — NO WITNESS READ YET"`),
and the wave-2 glyph render (`●` if LIVE else `○`).

### 1.1 STALE gains the session label — SAUM V-1, ruled in wave-1, never landed

Today the two "session exists, proof failed" forms disagree about whether
the session exists in print:

- UNPROVEN: `NOT BLOCKING — SLEEP SESSION RUNNING, ENFORCEMENT NOT PROVEN`
- STALE: `NOT BLOCKING — SHIELD PROOF STALE` *(which session? the print
  does not say)*

**Change (exact):** the STALE branch headline becomes

> `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE`

— SAUM's P-2 verbatim, separator grammar per SAUM V-3 (`·` between
facts). The old label-less short form is **retired**, not kept as a
compact variant: one fact, one wording.
**What got better and why:** STALE is the form he meets when a wall he
armed has quietly stopped proving itself — the exact moment the label
matters most. A man with a SLEEP routine running and a hand session
earlier in the day can now tell *which* session's proof went stale
without opening anything else. It also closes the one internal
inconsistency SAUM convicted inside the derivation itself: two forms of
one fact family now carry the same detail, in the same order.

### 1.2 The no-witness fallback joins the family — SAUM V-2 lock applied to the screen

Today the screen's fallback is a third grammar spoken by no other form:
verb-less, dash-first — `ENFORCEMENT NOT PROVEN — NO WITNESS READ YET`.
It does not begin with the headline's verb phrase, so the one scan the
screen trains (`BLOCKING NOW` / `NOT BLOCKING`, first words, every time)
breaks precisely on cold start, the moment the scan is most needed.

**Change (exact):** the fallback is locked to the derivation's own
UNPROVEN form with the generic label, per SAUM V-2:

- session active (the screen's existing `isSessionActive` input chooses
  between these two forms only — it never earns `BLOCKING NOW`):
  `NOT BLOCKING — SESSION RUNNING, ENFORCEMENT NOT PROVEN`
- no session: `NOT BLOCKING — NO SESSION RUNNING` (SAUM table 1d/1g).

Character-identical to what the derivation prints for the same facts, so
a wording edit to one is an edit to both — the lock SAUM recorded and the
code never took.
**What got better and why:** cold start now speaks the same sentence the
settled screen speaks. There is no longer a special dialect for "the
ViewModel hasn't answered yet" that a tired reader must learn separately,
and no future editor can fix the UNPROVEN wording in the derivation and
silently leave the fallback saying the old thing.

### 1.3 The glyph stops conflating "proven dead" with "not proven"

Today (wave-2 render): `●` for LIVE, `○` for **everything else** —
NO_SESSION, UNPROVEN, STALE and the fallback all wear the hollow mark.
The words were cured of exactly this conflation (SALZ C-3: unknown must
not print as a proven negative); the pre-verbal channel still commits it.
A glance that catches only glyph + verb shape reads an unproven wall as
a proven-down wall.

**Change (exact):** three glyph states, one axis (PANZER's solidity
scale, glyph-only — the words stay full ink in every state):

| Proof | Glyph | Meaning |
|---|---|---|
| LIVE | `●` solid, full ink | the wall stands, proved |
| NO_SESSION | `○` hollow | proved: nothing is running |
| UNPROVEN / STALE / fallback | `●` at 55% ink | a session claims to run; proof has not landed / has lapsed |

**What got better and why:** the glyph now carries the same three-state
truth the sentence carries — present-and-solid, proved-absent, and
claimed-but-unproven are three different facts and finally look like
three different facts, in the channel he reads first. No colour is
introduced; the 55% step lives on the glyph alone (PANZER's recorded
move of FORM's 55% from text to glyph, because 55% *text* measured
≈3.4:1 by day — too weak for this line).

### 1.4 The session label is capped at the print layer — SAUM P-1

`shieldSessionLabel` is unbounded: `RoutineEntity.name` and profile names
carry no length cap anywhere in the tree, so one long rename
(fixture: `MORNING FASTED STUDY BLOCK`, 26 chars → a 35-char label)
silently breaks every budget the sentence's render law assumes — the
40 dp slot, the `ENDS` segment, the never-dropped Safe Break segment.

**Change (exact, SAUM P-1):** in `shieldSessionLabel`, cap the finished
label at 20 chars, cutting the *name segment only*: routine labels rebuild
as `name.take(11) + "… SESSION"`; other labels as `label.take(19) + "…"`.
The ` SESSION` suffix is never cut. Pure-function test case (added to the
existing truth-table suite's list): the 26-char fixture name → a 20-char
label ending `… SESSION`.
**What got better and why:** every other segment of the sentence is
fixed or short; the label was the one unbounded input, and it is now
bounded at the one place all its readers share — the derivation — instead
of at each render site separately, where the caps would drift.

### 1.5 A past ENDS is dropped, never printed — FORM §1.3

`shieldSessionEndsAtMillis` returns a routine session's frozen
`windowEndMillis` even when that instant is already past while the row
still reads active (the mid-transition emission FORM named). The headline
then prints `BLOCKING NOW — … · ENDS 03:30` at 03:31 — a small visible
falsehood standing next to the screen's most important true word.

**Change (exact):** in `computeShieldBlockingStatus`, if the derived
`endsAt` is `<= nowWallMillis`, omit the `· ENDS HH:MM` segment (the
stopwatch form: verb + label only). The next emission settles the line.
**What got better and why:** the sentence never states an end that has
already happened. Dropping a segment is honest; printing a past end
teaches him to discount the whole line — including `BLOCKING NOW`.

**Round 1 net:** five concrete changes, all inside the status sentence;
the witness conjunction, the 10 s gap law, the tone map and every
protected truth above are untouched. Forms after Round 1 (complete set):

| Proof | Headline (exact template) | Glyph |
|---|---|---|
| LIVE, timed | `BLOCKING NOW — <LABEL> · ENDS HH:MM` (+ ` · SAFE BREAK — PEEK APPS OPEN` in a break) | ● full |
| LIVE, stopwatch / past-end dropped | `BLOCKING NOW — <LABEL>` (+ break suffix as above) | ● full |
| NO_SESSION | `NOT BLOCKING — NO SESSION RUNNING` | ○ |
| UNPROVEN | `NOT BLOCKING — <LABEL> RUNNING, ENFORCEMENT NOT PROVEN` | ● 55% |
| STALE | `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE` | ● 55% |
| Fallback (no derivation supplied) | the UNPROVEN or NO_SESSION form above, per §1.2 | ● 55% / ○ |

— FEILE (IMPROVER), issue #7 cell, Oct 30 2026

---

## ROUND 2 — Standing Orders: the counts sit with the sentence that qualifies them, and their words look like the doors they are

**Existing output worked on:** the Standing Orders region of
`ShieldMatrixScreen.kt` as wave-2 left it — `RulesHeaderRow` printing
`YOUR RULES`, `StandingTallyLine` / `TallySegment`, the folded tally under
search (`tallyText(shut, peek, open = null)`), `CatalogueGroupHeader`,
and the LazyColumn's item order (status · banner · cap · routines ·
tally · prune · websites · search · catalogue).

### 2.1 The tally moves to directly under the status sentence

Today the tally — the counts the status sentence qualifies — sits in
section 3.5, *below the LOADOUT cap and the whole ROUTINES block*. The
first screen therefore reads: whether it is blocking, then an act (the
cap), then another system (routines), and only then how much is shut.
FORM §2.1 fixed the meeting point ("R-5 fixes the sentence above the
tally; R-1 fixes the tally at the top; this is where they meet") and
GLANZ's finish spec ruled the same: "promoted to its own item directly
under the status sentence/banner". Wave-2 kept the old order.

**Change (exact):** LazyColumn order becomes — status sentence ·
ratchet banner · **Standing Orders tally (header + line)** · LOADOUT cap
(+ source print) · ROUTINES · prune note · BLOCKED WEBSITES · search +
filters · grouped catalogue. Nothing else moves; R-2 (routines stay
above the catalogue) and R-3 (websites above ALL APPS) are untouched.
`catalogueGroupIndex` is re-derived by counting emitted items in the
new order at the call site (GLANZ's law: indices counted at emission,
never hardcoded) — and its header comment is updated to the new order,
so the next hand does not "fix" it back from a stale comment.
**What got better and why:** the audit's 1 AM test — *whether blocking,
how many, which* — now passes in one viewport in that order: the
sentence, then the counts it qualifies, then the acts. Facts before
controls is also the screen's own stated logic everywhere else
(sentence item 0 "above every ruling it qualifies"); the tally was the
one fact still queueing behind two controls.

### 2.2 The surface gets its real name: `STANDING ORDERS`

Wave-2's `RulesHeaderRow` prints `YOUR RULES` — the retired slip's
header, resurrected over the tally. The cell's filings call this surface
by three names (the slip, your rules, the tally), while the audit, the
issue body and the programme call it one: **Standing Orders**. GLANZ's
spec for this surface was explicit: no `YOUR RULES` header resurrected.

**Change (exact):** the header word becomes `STANDING ORDERS`, dress
unchanged (Atkinson Hyperlegible Bold 11.5 sp, letterSpacing 0.6,
full ink — this section keeps its one full-volume voice). The folded
tally under search rides the same header (§2.4). No other header on the
screen changes.
**What got better and why:** a surface whose printed name matches the
name in the issue, the audit and every ruling about it can be talked
about — by Sarthak, by Ren, by the next audit — without a translation
step. `YOUR RULES` also misdescribes what stands there now: not a list
of his rules (that was the slip, retired) but the standing summary of
them, which is what "standing orders" means.

### 2.3 Tally words wear the door grammar — and a zero is not a door

Wave-2 made the tally's words jumps (R-1 completed) but rendered them
as bare clickable `Text`: at rest nothing distinguishes a jump-word
from the folded tally's identical, non-tappable print under search —
Fremd C5's defect in its second form (a switch that nowhere says it can
be tapped), now wearing the cure's clothes. Worse, a segment whose
count is 0 is still clickable: its group does not exist in
`groupedForCatalogue()` output, `catalogueGroupIndex` falls through to
the end index, and the tap silently scrolls to the bottom of the list —
a confident misdirection (the SALZ C-11.2 family).

**Change (exact):**
- A **live** segment (count > 0, group present) wears the screen's one
  door grammar (GLANZ / `FilterWord` geometry): the segment's word in
  full ink with the 18 × 2 dp ink bar beneath it, inside the tally
  line's existing fixed slot so bars never change the line's height.
- A **zero** segment keeps its numeral and word in full ink (counts are
  facts; an absence prints as a fact) but wears **no bar and takes no
  tap** — `onJump` is not wired for it. A door is never painted on a
  wall.
- Semantics, per FORM §3.1: live — `"<n> shut. Tap to jump to the shut
  apps."` (likewise peek/open); zero — `"<n> shut."` and no action.
  Today's description (`"9 SHUT"`, action attached at zero too) is
  replaced whole.

**What got better and why:** the tally now advertises its own use at
rest, in the same visual language the filter words already taught him,
and the one tap that could previously lie to him — jumping to a ruling
that has no apps — does nothing instead of somewhere wrong. The strike
vocabulary stays intact: whole-line 260 ms Curve-A re-strike, 250 ms
coalesce, digits never roll; when a segment's count hits zero its bar
leaves in the same strike as its numeral changes (GLANZ's extension).

### 2.4 The folded tally under search still sums to the phone

Under search the header's right side prints `tallyText(shut, peek,
open = null)` → `7 SHUT · 2 PEEK`. The OPEN count silently vanishes, so
the one count line visible while he searches no longer sums to the
phone — contradicting this same file's own doctrine ("three numbers
that sum to the phone, so the book provably covers every app on it")
at the exact moment he is hunting one app among them.

**Change (exact):** the folded form prints all three counts in the same
mixed grammar as the open tally: `7 SHUT · 2 PEEK · 61 OPEN`
(Anonymous Pro Bold numerals, Atkinson Hyperlegible Bold words,
11.5 sp — at ≈24 glyphs it fits the header's right side at 320 dp;
the title ellipsises first if the two ever compete, per the screen's
header law). The `open = null` variant of `tallyText` is deleted, so
no future call site can reintroduce a partial count by passing null.
**What got better and why:** there is now exactly one tally grammar on
the screen, open or folded, and it always balances. Search remains a
finding tool that never filters the counts (Liv D6, acquitted) — and
now it visibly doesn't.

### 2.5 The glyph legend lands, and the group headers gain their marks

R-10's answer to name-is-not-a-door was a one-line legend at the
catalogue head (FORM §3.1), and GLANZ specced a 10 dp solidity glyph on
each group header. Neither landed: wave-2's `CatalogueGroupHeader` is
the bare word, and no legend exists anywhere — while every catalogue
row's ruling is carried by a sled position and a Canvas mark a
first-time reader has no key for.

**Change (exact):**
- `CatalogueGroupHeader` gains the 10 dp solidity glyph on the rows'
  margin axis, PANZER's scale (the same scale as Round 1 §1.3):
  **solid** = SHUT · **55% ink** = PEEK · **1.4 dp frame** = OPEN.
  The word and its dress do not change; the count still does not print
  here (R-13 / SALZ C-11.1 — the tally is the counts' one home).
- One legend line, printed once, directly above the first group
  header: it draws the same three glyph marks the headers draw —
  rendered by the same composable, never a typed character that could
  drift from them — each followed by its word in Anonymous Pro 11 sp,
  variant ink: `<solid> SHUT · <55%> PEEK · <frame> OPEN`
  (FORM's law: legend and marks are one fact; if the marks are ever
  re-machined, the legend changes in the same edit because it *is* the
  marks).
- The PEEK contract line stays exactly where R-6 put it — in the PEEK
  group header, under the word — and sticks with that header.

**What got better and why:** the catalogue becomes self-teaching. A
reader who has never seen a sled can decode every row's mark from the
header above it and the legend above that, instead of inferring the
code from sled positions. And because the legend renders the headers'
own marks, the key can never silently stop matching the lock.

**Round 2 net:** five concrete changes, all inside Standing Orders
presentation. No count derivation, no group order (PEEK → SHUT → OPEN),
no sled row and no commit path changes; the tally's arithmetic and
re-strike manners (acquitted) are untouched.

— FEILE (IMPROVER), issue #7 cell, Oct 30 2026

---

## ROUND 3 — State words at the edge: nothing true gets shrunk, clipped or eaten, and the whole vocabulary lands in one table

**Existing output worked on:** `RoutineRow` and the status line's render
in `ShieldMatrixScreen.kt` as wave-2 left them, `CatalogueGroupHeader`'s
PEEK contract construction, and the cap source print (`loadout_cap_source`).

### 3.1 `WON'T FIRE` prints at the same size as every other cap word

Today the cap's text size is conditional: `11 sp`, shrinking to `10 sp`
when the word is longer than 3 glyphs. Only one word on the row ever
trips that condition — `WON'T FIRE` (10 glyphs). The single most
consequential word a routine row can print is therefore the smallest
print on the row: hierarchy inverted at exactly the wrong word.
POLITUR ruled this in wave-1's finish filings (11 sp for every cap word);
the conditional survived into wave-2's code.

**Change (exact):** delete the length conditional — every cap word
(`ON` / `OFF` / `WON'T FIRE`) renders Anonymous Pro Bold **11 sp**; the
cap's horizontal padding rises 6 dp → 10 dp (SAUM P-4), so the word is
carried by the payload-sized cap (`widthIn(min = 48 dp)`, ≈86 dp for
`WON'T FIRE` at 11 sp) instead of by shrinking the word. Fill/ink law
unchanged: `WON'T FIRE` keeps its middle density (variant fill +
outline) with the word in full ink.
**What got better and why:** a warning that has to be squinted at is a
warning designed to be missed at 1 AM. The cap grows; the word never
shrinks — the same law Round 1 §1.4 applied to the session label,
applied here to the cap.

### 3.2 `RUNNING NOW` becomes its own segment, and the meta shortens by law, not by clipping

Today the meta line is one concatenated `Text` —
`"<DAYS> · <LOADOUT>" + routineRunningNowSuffix(...)` — in variant ink,
`maxLines = 1`, no overflow behaviour specified. Its worst case is real:
`MON TUE WED THU FRI SAT SUN · STUDY · RUNNING NOW` = 49 chars ≈ 353 dp,
which fits nowhere; what the reader gets is whatever the clip leaves,
possibly half of `RUNNING NOW` — the one segment that proves a routine
is actually running, eaten invisibly (PANZER and SAUM P-5 both ruled
this separation; neither landed).

**Change (exact, SAUM P-5/P-6):**
- The meta renders as a `Row` of two segments: the days/loadout segment
  (`weight(1f, fill = false)`, Ellipsis as backstop only) and
  ` · RUNNING NOW` as its own `Text` — Anonymous Pro Bold 12 sp, **full
  ink**, `maxLines = 1`, never ellipsised.
- Shortening is derivation-side and whole-segment only, in a pure helper
  (`routineMetaLabel`, 30-char budget): **(1)** `RUNNING NOW` never
  drops and never shortens; **(2)** the loadout segment drops first,
  whole, with its `·` (it has a second home: the expanded row's loadout
  chip); **(3)** if days + suffix still exceed budget, days compress to
  `<n> DAYS` (the exact days live in the expanded day chips). Worked
  worst case: the 49-char line above → drop loadout → 43, still over →
  `7 DAYS · RUNNING NOW` (20 ✓). A `WON'T FIRE` row can never carry the
  suffix (no session is ever materialised for `daysMask = 0`), so rows
  3.1 and 3.2 never fight.
- The routine **name** `Text` gains the explicit law it currently lacks:
  `overflow = Ellipsis` (not the default clip) and the full untruncated
  name as its `contentDescription` (SAUM P-10).

**What got better and why:** truncation on this row is now a decision
made in a testable derivation, in whole true segments, in a stated
order — never an accident of pixel width. And the row's proof-word
graduates from grey string-tail to its own full-ink segment: schedule
fact and running proof no longer share one fading ink.

### 3.3 The status line gets its render law — house type, segment overflow, a slot that can grow

Wave-2 cured the status line's colour and gave it a fixed 40 dp slot,
but the render is still one bare `Text`: all-Anonymous Pro 13 sp, no
`maxLines`, no overflow law — inside a slot that cannot grow. FORM
§2.2's dress (Atkinson Hyperlegible Bold 14 sp words, Anonymous Pro
numerals — the tally's mixed grammar) was never landed, and Round 1's
longest form (label capped at 20 → UNPROVEN at 67 chars) improvises its
wrapping at 320 dp / fontScale 1.3, mid-sentence, pushing or clipping
against the fixed slot.

**Change (exact):**
- **Type:** the headline builds as annotated segments with the tally's
  existing mixed grammar (`tallyText`'s construction): words in
  Atkinson Hyperlegible Bold 14 sp, the `ENDS HH:MM` numerals in
  Anonymous Pro Bold 14 sp. Sora appears nowhere on this line (it
  appears nowhere in Shield's working print; display type stays the
  app's display surfaces' business).
- **Segments:** split the headline on ` · ` — segment 0 (verb + label,
  plus the UNPROVEN/STALE tail) takes `weight(1f)`, `maxLines = 1`,
  Ellipsis; the fixed segments (`ENDS …`, the Safe Break qualifier)
  render whole, never ellipsised. Priority is Round 1's table made
  mechanical: the verb phrase never truncates, the label absorbs all
  pressure, fixed segments move whole or not at all.
- **Stacking:** at fontScale ≤ 1.15 the segments sit in one `Row`; above
  1.15, or whenever the Safe Break suffix is present, they stack in a
  `Column` (verb + label on L1, fixed segments in order on L2 — SAUM
  rows 1a/1c). The slot becomes **min-height 40 dp**, not fixed height:
  it holds one line at 40 dp on ordinary days and grows for the stacked
  form instead of clipping it. The whole-line 260 ms Curve-A re-strike,
  the `shield_blocking_status` testTag and the Row's semantics (the full
  untruncated headline as `contentDescription`) are kept exactly.

**What got better and why:** the screen's most important sentence is no
longer its least-specified render. It finally wears the house grammar
every neighbouring fact already wears, and its behaviour at the narrow,
large-font edge — the edge SAUM measured and the current code ignores —
is a stated law with a worked form, not an improvisation.

### 3.4 The PEEK contract: singular law and a break only at its middle dot

The contract is built in `CatalogueGroupHeader` as
`PEEK — $peekMin MINUTES, THEN SHUT AGAIN · $coolMin MIN BETWEEN PEEKS`.
Two edge defects: **(a)** no singular law — if `PEEK_DURATION_SECONDS`
ever becomes 60, the line prints `1 MINUTES` (the constants are the
source of truth precisely so an edit there reprints the truth; today
it would reprint a grammar error); **(b)** at 56 chars ≈ 353 dp in
Anonymous Pro 11 sp the line exceeds even a 360 dp screen, with no wrap
law — it breaks wherever the pixels run out, mid-phrase.

**Change (exact, SAUM P-9 + row 7b):** template becomes
`PEEK — ${mins} MINUTE${"S" unless mins == 1}, THEN SHUT AGAIN · ${cd} MIN BETWEEN PEEKS`,
both numbers still derived from `PEEK_DURATION_SECONDS` /
`PEEK_COOLDOWN_MILLIS` at render, never hardcoded. The line renders as
two segments split at its ` · ` in a wrapping row: the only permitted
break is at that dot (segment 1, 33 chars ≈ 218 dp, fits 296 dp at
fontScale 1.0 and 1.3 alike; on 320 dp at 1.3 it takes L1 alone and the
cooldown segment takes L2 whole). It still prints **once** on this
screen, in the PEEK group header only; PARK's plate stays the settings
home.
**What got better and why:** the one place PEEK's terms are stated can
now survive both a constants edit and a narrow phone without printing
something false or broken — small words, but they are a contract, and
contracts are read literally at 1 AM.

### 3.5 The cap source print learns to say one thing in two situations

Today's two forms are not parallel: `THIS CAP SHOWS YOUR LAST HAND PICK`
states the cap's source positively; the routine form — `A ROUTINE
SESSION IS RUNNING — ITS LOADOUT GOVERNS, NOT THIS CAP` (65 chars,
wraps the 11 sp line) — never says what the cap *does* show. A reader
who only ever sees the second form never learns the cap is his hand
pick; he learns only what it isn't.

**Change (exact):** one prefix grammar — the cap's content first, the
qualifier second:
- no routine session live: `THIS CAP SHOWS YOUR LAST HAND PICK` *(unchanged)*
- routine session live: `THIS CAP SHOWS YOUR LAST HAND PICK — NOT THE RUNNING ROUTINE'S LOADOUT`

Same testTag (`loadout_cap_source`), same dress (Anonymous Pro 11 sp,
variant ink). The routine's existence remains proved by the status
sentence and the row's `RUNNING NOW`; this line's one job — what the
cap means — is now stated identically in both situations.
**What got better and why:** R-4's charge was a selector displaying
history while wearing a state's clothes. The cure print should not
need two readings to teach one fact; now the fact is the constant part
and the situation is the suffix, in the sentence grammar the rest of
the screen already uses.

---

## THE FINAL COPY TABLE — every state word on Shield after Rounds 1–3

One table, exact templates. This is the deliverable's canonical end
state; where it conflicts with an earlier filing, the conflict is named
in the notes under the table, never smoothed over.

| # | Surface | Exact print | Law |
|---|---|---|---|
| 1 | Status, LIVE timed | `BLOCKING NOW — <LABEL> · ENDS HH:MM` | label ≤20 chars (R1 §1.4); past end → segment dropped (R1 §1.5); glyph ● full |
| 2 | Status, LIVE stopwatch | `BLOCKING NOW — <LABEL>` | no end is ever invented; glyph ● full |
| 3 | Status, LIVE in Safe Break | rows 1–2 + ` · SAFE BREAK — PEEK APPS OPEN` | suffix never dropped, never ellipsised; stacked render (R3 §3.3) |
| 4 | Status, NO_SESSION | `NOT BLOCKING — NO SESSION RUNNING` | glyph ○ |
| 5 | Status, UNPROVEN | `NOT BLOCKING — <LABEL> RUNNING, ENFORCEMENT NOT PROVEN` | glyph ● 55% |
| 6 | Status, STALE | `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE` | glyph ● 55% (R1 §1.1) |
| 7 | Status, fallback | row 5 with label `SESSION`, or row 4 | character-identical lock (R1 §1.2) |
| 8 | Standing Orders header | `STANDING ORDERS` | Atkinson Bold 11.5 sp, full ink (R2 §2.2) |
| 9 | Tally, open | `<n> SHUT · <n> PEEK · <n> OPEN` | words are jumps with door bars when live; zero = no bar, no tap (R2 §2.3) |
| 10 | Tally, folded (search) | `<n> SHUT · <n> PEEK · <n> OPEN` | same grammar, all three counts (R2 §2.4) |
| 11 | Group headers | `SHUT` / `PEEK` / `OPEN` + solidity glyph | no counts here — tally is their one home; legend above first header (R2 §2.5) |
| 12 | PEEK contract | `PEEK — <m> MINUTE(S), THEN SHUT AGAIN · <c> MIN BETWEEN PEEKS` | derived, singular law, break only at `·` (R3 §3.4) |
| 13 | ROUTINES header | `<n> ARMED` | protected truth, unchanged |
| 14 | Routine cap | `ON` / `OFF` / `WON'T FIRE` | precedence OFF → WON'T FIRE → ON; all at 11 sp (R3 §3.1) |
| 15 | Routine meta | `<DAYS> · <LOADOUT>` + own-segment ` · RUNNING NOW` | whole-segment shortening law; suffix full ink, never dropped (R3 §3.2) |
| 16 | Routine meta, mask 0 | `NO DAYS SET · <LOADOUT>` | the cause; row 14's cap is the consequence — both print, once each |
| 17 | Cap source | `THIS CAP SHOWS YOUR LAST HAND PICK` (+ ` — NOT THE RUNNING ROUTINE'S LOADOUT` when a routine session is live) | one prefix grammar (R3 §3.5) |
| 18 | Reset strip | `YOUR DAY, SEEDED — TAP A ROW TO OPEN IT · TIMES YOU SET ARE NEVER CHANGED` | wave-2 form, kept; never truncated, wraps whole |
| 19 | Search placeholder | `SEARCH APPS` | wave-2 form, kept |
| 20 | Site ADD verdicts | `NOT A SITE — CHECK THE ADDRESS AND TRY AGAIN` / `ALREADY ON THE LIST` | wave-2 forms, kept; field keeps the typed text |

**Notes on named conflicts, for VOGT/Ren (not silently resolved here):**
- Rows 5–7 keep VOGT's wave-1 vocabulary ruling (MONTEUR's forms stand;
  FORM's `CAN'T CHECK BLOCKING — TAP TO RETRY` stays the Deck's
  sentence). Round 1 changed only the forms' *consistency* — STALE's
  missing label, the fallback's foreign grammar — never the vocabulary
  itself.
- `ARMED` (row 13) keeps the audit's R-6 word; SALZ C-9's collision
  dissent (receipt `ARMED hh:mm`, Deck's ARMED section) stays on record
  as a wording-pass item for Ren, exactly where VOGT left it. An
  improver does not overturn a lead ruling inside a copy table.
- The ±30 chips print nothing new in this file: DELEGATED to Voss's
  batch-10 time wheels (R-8), as every seat before me ruled.
- Ghost Preview (R-4a), the third witness conjunct (SALZ C-2 residual),
  in-place sled refusal (R-7's second half): priced remainders in
  VOGT's wave-2 report, outside state wording and Standing Orders
  presentation — untouched, not re-priced here.

## Acceptance mapping (issue #7 body)

- [x] Shield must say whether blocking is live — *wording completed*:
  one grammar across all six printed forms + fallback (Round 1), a
  three-state glyph that matches it (§1.3), and a render law that
  keeps it whole at the measured edges (Round 3 §3.3). The derivation
  itself was already landed by MONTEUR and is not altered.
- [x] Standing Orders de-duplication — *presentation completed*: tally
  at the top under the sentence, real name, door grammar, folded form
  balancing, legend landed (Round 2). The de-duplication itself was
  already landed (R-1 core, wave-1; jumps, wave-2) and is not altered.
- [x] ACTIVE → ARMED — landed wave-1, protected here (table row 13).
- [x] daysMask = 0 → WON'T FIRE — landed wave-1/2, completed at the
  edge here: same size as every cap word, meta law beside it
  (Round 3 §§3.1–3.2).
- [ ] ±30 chips → 20:00→20:40 — DELEGATED to Voss's time wheels,
  restated, not worked (R-8).

Everything above is paper spec against the current tree. Unproven
until MONTEUR applies it, Ren compiles on Sarthak's word, and the
phone exercises it — the on-device checks SAUM filed (his §5) cover
rows 1–6, 14–15 of the table as written.

— FEILE (IMPROVER), issue #7 cell, Oct 30 2026. DECLARED: no app code
edited, nothing compiled, no tests run.
