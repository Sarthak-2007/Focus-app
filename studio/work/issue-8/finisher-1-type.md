# FINISH — THE DRAWER, TYPOGRAPHY & INK HIERARCHY (issue #8)

Finisher 1 — Type (issue #8 cell), for LADE.
Sources read whole: `studio/plan/AUDIT-drawer.md` (the verdict —
§2 / §6 are law here), `studio/work/issue-8/designer-drawer.md`
(the floor: inline MONTHS shelf, cabinet evicted, filing zone),
`innovator-drawer.md`, and `DrawerScreen.kt` as built — every
"current" value below is measured from that file. Sibling specs
honoured, not re-ruled: Finisher 2 (spacing — geometry, heading
breaks, shelf-row height), Finisher 4 (filing — zone states, the
03:30 seam, retirement). Where they set type, I adopt their
numbers verbatim and say so; where they left type open, this
file closes it.

No partial `finisher-1-type.md` existed in the clone — this seat
starts fresh, nothing inherited. No Kotlin was edited, nobody
compiled, and the cabinet's next home is not proposed, hinted
at, or implied anywhere below — issue #21 owns it, parked.
Cabinet type is therefore **not finished here**: it is evicted,
not cured. Its lines appear below only where their death removes
a defect from *this* room.

---

## 1 · THE LAW OF THIS ROOM'S TYPE

**Anonymous Pro caps is the Drawer's display voice. Sora is
absent — acquitted (§6, Aug) — and stays absent.** One trap,
named for the Builder: `Type.kt` contains a style literally
named `DrawerHeaderTextStyle` (Sora Bold 22sp). `DrawerScreen.kt`
does not use it — STORED is hand-set Anonymous 13sp. Do not
"fix" STORED onto that style. The name is a lie left in the
theme file; the room's voice is the audit's ruling.

**Faces, by species — the whole system:**

| Species | Face | What belongs to it |
|---|---|---|
| Names, labels, verbs, telemetry | Anonymous Pro | STORED, MONTHS, headings, BACK, EARLIER, shelf rows, filing print, search text, provenance, facts, meta runs |
| His content | Atkinson Hyperlegible | Task titles (rows + DETAIL), substep text, the substep reason line |

That table is the caps law in disguise: **every all-caps print
in the finished room is Anonymous Pro.** As built, caps also
print in Atkinson (BACK, the day words, both empty lines, the
CURIOS row) — a body face shouting in caps with no tracking is
a costume, not a voice. Each case is cured in §3.

**The ladder — six sizes, each with exactly one job family:**

| Size | Job |
|---|---|
| 17sp | DETAIL task title — the room's only hero |
| 14sp | StoredRow title |
| 13sp | STORED — the room's name, and nothing else |
| 12sp | The working label size: MONTHS heading, shelf rows, MONTH heading, BACK, EARLIER, search text, filing words, DETAIL facts, substep text |
| 11sp | Provenance & filing dates: DETAIL category + date line, filing-zone dates |
| 10.5sp | The meta family: day headings (Bold), row meta runs, substep numbers (Regular) |

As built the file spends **eight** sizes (17 / 14 / 13 / 12.5 /
12 / 11 / 10.5, plus a 14sp chevron glyph) and 13sp alone does
four unrelated jobs. §4 censuses every in-between size and where
it goes. After this finish, a size *is* a job: he can tell what
kind of thing a line is from its size before he reads it.

**Weights — only real weights exist.** `Type.kt` states it in
its own comments: Atkinson ships Regular + Bold only, Anonymous
ships Regular + Bold only; a `FontWeight.Medium` request
resolves to the nearest real weight. As built, the Drawer asks
for Medium in four places (StoredRow title, substep titles,
INDEX month rows, cabinet names) — **ink that grades by a weight
that does not exist**, rendering as Regular while the code
claims a step between Regular and Bold. Gallery law: Anonymous
**Bold** = names, headings, verbs, shelf (anything that labels
or acts); Anonymous **Regular** = data runs, facts, prompts,
provenance. Atkinson **Bold** = titles; Atkinson **Regular** =
body and steps. No Medium anywhere in the room.

**Tracking — one law.** Anonymous caps labels carry
`letterSpacing 1sp`, with exactly one exception: STORED carries
2sp, the signature of the room's name. As built, tracking is
scattered (2sp on STORED *and* EARLIER, 1sp on most labels, 0 on
BACK, the day words, the DETAIL date line, and the row meta
run). Cures in §3 make 2sp STORED's alone. Numeric data runs
(row meta, substep numbers) stay untracked — tracking is for
labels, not for data.

**Ink — three steps of one ink, one accent.** `#34323B` at
**100%** (full: names, content, verbs, records), **~60%** (meta:
dates, provenance, telemetry, prompts, hairlines are the ~14%
step — Finisher 2 owns hairline placement; the step is stated
here so type and ruling share one scale). **Never a second grey
hue** — every "grey" in this room is the ink at an opacity
(Designer §1). The single accent `#493EA4` appears in type only
where a thing is *still an action*: undeclared filing words,
the search caret and focus rule. The moment a thing stops being
an action it stops being violet (Designer §1, Finisher 4 §2).
**Never green**, in any state, including press states (the
violet wash covers those — Feel 1). Deltas below name ink steps,
not raw hex: the Builder maps steps to the theme's ink tokens
so the dark room inverts by the same steps.

---

## 2 · HOW TO READ THE TABLES

Each delta: **element · current (as built) → gallery standard ·
why.** "Current" values are `face / weight / size / tracking /
ink` in that order. Deltas marked **[audit]** are ruled in the
verdict itself; **[floor]** are the Designer's; unmarked are
this seat's finish calls for LADE to merge.

---

## 3 · MASTER DELTA TABLE — ELEMENT BY ELEMENT

### 3.1 STORED header (LANDING + DETAIL — one print, two call sites)

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-01 | STORED ink | Anonymous / Bold / 13sp / 2sp / **meta (~60%)** → same face, weight, size, tracking at **full ink (100%)** | The room's name is the faintest structural print on the screen: as built it is out-inked by ALL MONTHS, CURIOS, EARLIER, BACK, and every row title — all at full ink. Aug's hierarchy cure (§2) unifies the nav rows; the eviction kills two of the offenders; this delta completes the cure at the header itself. Feel 5 (Moment 5) states the finished state plainly: "STORED at full weight… the header is again the heaviest print on the screen." A name printed in meta ink reads as a caption *about* the room, not the room's name. |
| T-02 | STORED in DETAIL | Identical spec to LANDING (it already matches in code) → **keep identical; T-01's ink cure applies to both call sites** | One room, one name, one voice, wherever he stands in it. Filed so a Builder curing the landing header does not leave the DETAIL header in meta ink — the two call sites are separate `Text`s in the file. |

### 3.2 BACK — the header's other word

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-03 | BACK (MONTH, DETAIL; SEARCH gains one **[audit]**) | Atkinson / Bold / 12sp / **no tracking** / full ink → **Anonymous / Bold / 12sp / 1sp / full ink** | BACK is the only verb that prints in the body face, in caps, untracked — sitting in a mono header beside STORED (LANDING/DETAIL) or the mono month label (MONTH), it is visibly a different species from everything around it. The audit rules one BACK *costume* (§2); this is that costume, finished: the room's label voice. Size and ink were already right; face and tracking were the defect. `DrawerNavRow`'s Atkinson BACK (INDEX) dies with INDEX and is not cured (Finisher 2, D-05/D-14). |

### 3.3 The search line

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-04 | Typed search text **[audit]** | Atkinson / Bold / 13sp / no tracking / full ink → **Anonymous / Regular / 12sp / 1sp / full ink** — the placeholder's exact costume | The audit names this a finish defect in so many words: "typed text keeping the placeholder's Anonymous 12sp costume (today the field changes costume when typed in)." As built, the first keystroke changes face, weight, size, tracking, *and* ink — the line he is typing in becomes a different object from the line he tapped. After the cure, placeholder and typed text differ in ink only (prompt = meta, his words = full), which is the one honest distinction: state, not costume. Mono at identical tracking also means zero character-width reflow as he types. |
| T-05 | Placeholder `TYPE TO FIND A TASK` | Anonymous / Regular / 12sp / 1sp / meta → **keep, unchanged** | It is the costume T-04 adopts. Acquitted by adoption: the audit's cure is defined *by* this print. |
| T-06 | Search caret | `SolidColor(onSurfaceVariant)` — meta ink → **`#493EA4`, full strength** | Designer §1 names the caret as one of the accent's two homes in this room ("the search caret and focus rule"). A meta-ink caret is the theme's leftover, not a decision; the caret is the one moving thing on the line and it marks where his action is — accent is the affordance ink. (The focus rule taking the same accent is print-adjacent geometry; Feel 1 §4.1 already specifies its behaviour.) |
| T-07 | Search-result day headings | Anonymous / Bold / **11sp** / 1sp / meta, own geometry → **Anonymous / Bold / 10.5sp / 1sp / meta** — the day-heading voice of T-11, verbatim (with-year label is content, not a costume) | Three heading costumes exist as built (landing/month groups, search groups, MONTH title). Search headings are day headings; the demotion in T-11 applies at every call site or the eye learns two rhythms for one act (Finisher 2, D-18, makes the same point in geometry). Word-print on search headings: Finisher 4 §4.3 rules them wordless (locators over results) — adopted here; the Critic's M9 dissent (same day, two prints) is flagged for LADE in §5, not silently overridden. |

### 3.4 The filing zone (presentation per Designer §4 / Finisher 4 §3 — type adopted and completed)

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-08 | Filing dates (`TODAY · FRI 30 OCT`) | *(new furniture; the as-built ancestor is the DayHeading line: Anonymous / Bold / 11sp / 1sp / meta)* → **Anonymous / Bold / 11sp / 1sp / meta ink** **[floor — Finisher 4 §3, adopted]** | 11sp is the provenance size in the finished ladder (§1): a filing date is a locator, spoken in the same voice as DETAIL's provenance block (T-16), one step above the day headings it will become. Bold because it labels a line; meta because it is not the action on the line. |
| T-09 | Undeclared words REST / ILL / FAMILY | *(as built, on today's heading)* Atkinson / Bold / **11sp** / no tracking / **variant-or-full by active state** → **Anonymous / Bold / 12sp / 1sp / `#493EA4`, all three identical** **[floor — Finisher 4 §3, adopted]** | Three cures in one delta. Face/caps law: the words are caps labels, so they join the room's caps voice. Ink grading dies: as built the *active* word prints full and the others meta — the screen grades the three words against each other before he has chosen; in the zone all three carry the one accent at the one strength, because all three are equally actions and no word is coloured by meaning (ILL is not red, REST is not anything — Designer §1). Size 12: the words are the line's payload and sit one step above their date. |
| T-10 | Retired (declared) line | *(as built: the word rides its heading in the heading's own meta ink, toggleable)* → date segment unchanged (T-08); the declared word prints **beside its date** as a second span: **Anonymous / Bold / 12sp / 1sp / full ink**; the other two words do not print **[floor — Finisher 4 §2/§3, adopted]** | The retirement must be *readable in the type*: violet → full ink is the moment a word stops being an action and becomes a record (Designer §1). Two spans, never one colour — as built, heading and word are a single `Text` in a single meta ink, so his authored word prints at locator strength. At full ink and 12sp the word outranks its own date by exactly the steps a record should outrank its locator — and by no more. |

### 3.5 Day headings in groups (LANDING / MONTH)

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-11 | Day heading type **[audit]** | Anonymous / Bold / **11sp** / 1sp / meta → Anonymous / Bold / **10.5sp** / 1sp / meta | The audit's ruled demotion, verbatim: "heading type demotes 11→10.5sp into the meta family, bold caps kept." 10.5 is the meta family's size (row meta, substep numbers); a heading at 11 sat *between* the label size (12) and the meta size (10.5) — a shelf label priced like neither, which is part of why a new day read as a new row. Bold stays: at the meta size, weight is what separates a shelf label from the data runs beneath it. (The 6→18dp shelf break that completes this cure is Finisher 2, D-12 — geometry, theirs.) |
| T-12 | Declared word on a group heading | Single `Text`: `"${heading} · ${word}"`, all Anonymous / Bold / 11sp / 1sp / **meta** → **two spans**: date in the T-11 voice (10.5sp Bold meta), ` · ` meta, **word in Anonymous / Bold / 10.5sp / 1sp / full ink** | Same cure as T-10 at heading scale: his authored word is a record, the date is its locator, and one `Text` in one ink cannot say both. Full ink at the *heading's own size* — the word must not grow a second, larger costume on headings than it has in the filing zone's retirement (T-10, 12sp beside an 11sp date — the same one-step relationship, preserved at both scales). Transfer rule (which surface shows the word when) is Finisher 4 §4.3 — presentation of the word, theirs; its type, this delta. |

### 3.6 Stored rows

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-13 | Row title | Atkinson / **Medium** / 14sp / full ink → Atkinson / **Bold** / 14sp / full ink | Medium does not exist in this family (§1) — as built the title renders Regular while the code claims a middle step. The real choice is only Regular or Bold, and Bold is right: the house's row-title voice is Atkinson Bold (`AppTitleTextStyle`, Type.kt), and hierarchy needs the title to outrank its meta line by weight *and* size — Regular 14 over Regular 10.5 is size-only separation, the weakest step in the room. Bold title / Regular body also gives the room one Atkinson law: **titles Bold, body Regular** (DETAIL title, T-15, already obeys it). The row's construction is acquitted (Plan, Aug) and untouched — this delta is inside the title's weight only. |
| T-14 | Row meta run (category · clock · SPENT value) | Anonymous / Regular / 10.5sp / no tracking / meta; **category prints in its stored case** (`Study`, `Writing`, `Code`) → same face, weight, size, tracking, ink; **category uppercased at print** (`STUDY`) — display transform only, stored data unchanged | The meta run is a mono telemetry line, and every other mono label in the room is caps — a sentence-case word in tracked mono company is a label that forgot its costume. The code already proves the pattern one room over: cabinet serials are uppercased at print (`serialCode.uppercase()`); categories never got the same line. Uppercasing at print keeps one law — *mono prints caps* — without touching a single stored record. The run otherwise stays exactly as acquitted: one voice for category, clock, and duration; SPENT is never right-aligned or columnised (Dens's standing watch, §2 of the verdict — a type-arrangement charge this delta explicitly does not create). |
| T-15 | Substep reason line (search hits) **[audit]** | *(does not exist; the audit rules the line into existence — §2: a substep hit prints the matched step as a second line)* → **Atkinson / Regular / 12sp / meta ink**, under the row title | A ruled line with no type spec anywhere in the cell — this delta closes it. Atkinson because the step is his content, quoted; Regular 12 because it is the step's own voice in DETAIL (T-18) at one ink step down: the row is *citing* the step, not re-printing it at full strength. Meta ink keeps the citation subordinate to the title it explains — a reason line at full ink would compete with the result it justifies. (Ghost Search, if ever adopted, inherits this voice for its lit rows — Innovator P2, Feel 3 §6.) |

### 3.7 The MONTHS shelf (INDEX's replacement — Designer §3 / Finisher 2 D-20–D-23)

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-16a | Shelf heading `MONTHS` | *(did not exist)* → **Anonymous / Bold / 12sp / 1sp / full ink** **[floor — Finisher 2 D-20 sets 12sp Bold caps; ink and tracking completed here]** | 12sp puts MONTHS in the STORED family — 13 / 12 / 10.5 is the screen's whole heading hierarchy (Finisher 2). Full ink because MONTHS is a *name* (of a section), and names print full (STORED, T-01); dates print meta (T-11). That single distinction — names full, dates meta — is the ink law a reader can actually learn. |
| T-16b | Shelf row (`SEPTEMBER 2026`) | *(ancestor: INDEX rows — Atkinson / **Medium** / 14sp / sentence case / full ink, no tracking)* → **Anonymous / Bold / 12sp / 1sp / full ink**, year on every row, no chevron, no trailing furniture **[floor — Designer §3.1, Finisher 2 D-22]** | The INDEX row wore the StoredRow's own costume (Atkinson 14) — a month felt exactly like a task and was tapped with the wrong expectation. Anonymous Bold caps at the label size makes the shelf a different species at a glance, before a word is read. The 14sp Atkinson chevron `›` that rode the old ALL MONTHS row dies with it: a punctuation glyph at row-title size was the loudest single character on the landing. Word-only months (Critic S2; Feel 5, Moment 4 — **required**) take this row in this exact voice with no distinguishing mark: a mark would print *why* the month is thin, which is judgement by arrangement. |
| T-16c | MONTH view heading (the tapped month's name) | Anonymous / Bold / **11sp** / 1sp / **meta** → **Anonymous / Bold / 12sp / 1sp / full ink** — the shelf row's voice, verbatim | The same words, `SEPTEMBER 2026`, print at 12sp full ink on the shelf and then *shrink and fade* to 11sp meta when he arrives in the month. A name that demotes itself on arrival tells him he has gone somewhere lesser. Arrival must confirm, not demote: the heading he lands under is the row he tapped, in the same voice. (BACK beside it is T-03; MONTH's parent is LANDING — the audit's truth cure, not type.) |
| T-17 | EARLIER | Anonymous / Bold / 11sp / **2sp** / full ink → Anonymous / Bold / **12sp / 1sp** / full ink | EARLIER is the only print besides STORED carrying 2sp tracking — a paging verb wearing the room name's signature. Tracking law (§1) makes 2sp STORED's alone. Size 11→12 joins EARLIER to the working label size it shares with the shelf rows (Finisher 2, D-17, makes them share a 48dp height too — the screen's two full-width tap rows become one species in type as in geometry). Ink was already right: a verb prints full. |

### 3.8 Empty lines

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-18a | `NOTHING STORED YET` | Atkinson / Regular / **13sp** / no tracking / meta → **Anonymous / Regular / 12sp / 1sp / meta** | An all-caps standing line in the body face at the orphan 13sp (see §4). It is not a sentence to read — it is a label for a state, and labels speak Anonymous caps in this room. Meta ink stays: the line reports an absence and must not shout it (empty-state silence is acquitted, §6 — this delta changes its costume, not its volume). |
| T-18b | Search empty line **[audit]** | Atkinson / Regular / 13sp / meta, text `NOTHING STORED UNDER “X”` → **Anonymous / Regular / 12sp / 1sp / meta**, text **`NO STORED TASK UNDER “X”`** | Wording is the audit's re-scope (§2: after the eviction, search's territory and its promise finally match; the old line was false for every curio name). Type follows T-18a — the two empty lines are one species and print in one voice, including the quoted query, which takes no special span: his word inside the line is part of the label, not a highlight. |

### 3.9 DETAIL — the one task, read (amendments per verdict §2)

| # | Element | Current → Gallery | Why |
|---|---|---|---|
| T-19 | DETAIL title | Atkinson / Bold / 17sp / full ink → **keep, unchanged** | The room's only hero and the top of the ladder. Acquitted construction; nothing in the hunts touched it. |
| T-20 | Provenance lines (category, date line) | Category: Anonymous / Regular / 11sp / **1sp** / meta, **stored case**. Date line: Anonymous / Regular / **12sp** / **no tracking** / meta → **one voice for both lines: Anonymous / Regular / 11sp / 1sp / meta**, category uppercased at print (T-14's transform) | Two adjacent lines doing one job — saying where this record sits — in two sizes and two trackings. The date line's 12sp untracked is a third provenance costume hiding in the block. Unified at 11sp/1sp they join the ladder's provenance size, the same voice as the filing dates (T-08): everywhere the room prints *locator* facts, it prints them identically. |
| T-21 | ESTIMATE / SPENT facts | Atkinson / Regular / **13sp** / full ink → **Anonymous / Regular / 12sp / 1sp / full ink** (ESTIMATE in the provenance block, SPENT alone after the spacer — placement is the verdict's / Finisher 2 D-25–D-26; voice is this delta) | These are the only *facts* in the room printed in the body face. A fact here is a word plus a measured quantity — telemetry grammar, the same species as the date line directly above them — and Designer §1 already files SPENT under Anonymous telemetry ("as built" describes the *row's* SPENT, which is Anonymous; the DETAIL's Atkinson SPENT is the anomaly). 13sp Atkinson was also an orphan size doing no other job (see §4). At Anonymous 12 the block reads as one mono record under an Atkinson title — title in his content voice, record in the room's record voice. Full ink stays: facts are content, not locators. This delta amends the shorthand of Designer §1's table ("detail body — Atkinson") for these two lines only; lore and substeps remain Atkinson. Flagged for LADE in §5. |
| T-22 | Substep titles **[audit]** | Atkinson / **Medium** / **12.5sp** / **full ink + no strike if undone, meta + strike-through if done** → **Atkinson / Regular / 12sp / full ink, no strike, for every step** | The audit kills the strike distinction outright ("all substeps print in one voice") — this delta names that voice. 12.5sp is the file's purest in-between size, belonging to no family (§4); Medium is the phantom weight (§1); the ink split (done steps faded *and* struck) grades finished work twice for being finished, in the room whose law is that it doesn't. One voice = the undone steps' current voice, extended to all: Regular, full ink, 12sp. |
| T-23 | Substep numbers | Anonymous / Regular / 10.5sp / meta, flush-right → **same type, unchanged** (position moves to the leading column — Finisher 2 D-27, geometry) | Two-digit numbering is acquitted (DRW-08, §6) and 10.5 meta Regular is exactly the meta family's data voice. Filed so the number's *type* is not "fixed" alongside its position: only its chair moves. |
| — | FORGED in DETAIL | — | **No delta, and a guard:** nothing in this finish prints FORGED anywhere in the Drawer. The audit's "state belongs in the detail" (§2A) and §6's "FORGED printed at full ink in the detail" are about the **cabinet's** detail, which travels with the eviction. The Critic (M5) proves Designer §5's sentence can be misread into the *task* DETAIL, where no forged datum exists. Builder instruction from this seat: do not print FORGED in the task DETAIL under any delta in this file. |

### 3.10 What dies and takes its type with it (no cure, by name)

`ALL MONTHS` (Anonymous Bold 11 full ink) and its `›` (Atkinson
14 meta) — killed **[audit]**; the shelf (T-16) is not their
cure but their replacement. `CURIOS` / `DrawerNavRow` (Atkinson
Bold 12) — killed with the cabinet's eviction **[audit §2A]**.
The search capsule's type consequences die with the capsule
body (the line that replaces it is T-04/T-05). The dead CABINET
branch's 0dp Spacer prints nothing and takes nothing. Nothing
in this list is "finished" — finishing killed furniture would
be how it creeps back.

---

## 4 · THE HUNTS — CENSUSES (what was found, and where it went)

**Between-scale sizes.** Every size in the built file that sits
between two ladder families, with its disposition:
- **12.5sp** (substep titles) — between label 12 and hero-side
  13/14; belonged to nothing → 12 (T-22).
- **13sp Atkinson** (typed search, DETAIL facts, both empty
  lines) — one size doing four unrelated jobs, sitting between
  the row title (14) and the label size (12) → all four usages
  leave; 13sp survives only as STORED's Anonymous display size
  (T-04, T-18, T-21).
- **11sp scatter** (ALL MONTHS, MONTH title, day words, day
  headings, category) — five jobs, no family → after the kills
  and cures, 11sp keeps exactly one job: provenance & filing
  dates (T-08, T-20).
- **14sp chevron** — a glyph at title size → dies (T-16b).

**Phantom weights.** `FontWeight.Medium` × 4 call sites in
scope (row title, substeps, INDEX rows, cabinet names) against
a family that ships two weights. Dispositions: Bold (T-13),
Regular (T-22), replaced species (T-16b), evicted (cabinet).
After this finish the word Medium does not appear in the
Drawer's type.

**Caps & tracking census.** Caps in Atkinson as built: BACK,
day words, CURIOS, both empty lines — five prints, all cured
or killed above (T-03, T-09, T-18, §3.10). Caps missing where
the law requires them: category, both call sites (T-14, T-20).
Tracking as built: 2sp × 2 (STORED, EARLIER), 1sp × most
labels, 0 × 5 (BACK, day words, date line, row meta, typed
search) → finished: 2sp × 1 (STORED), 1sp on every caps label,
0 only on numeric data runs (row meta, substep numbers).

**Ink grading by accident.** Every place ink — not position,
not print — ranked things the room refuses to rank:
STORED in meta under full-ink furniture (T-01); the tapped
month's name shrinking *and* fading on arrival (T-16c); the
active day word darker than its unchosen neighbours (T-09);
done substeps faded *and* struck (T-22); a declared word
printed at its date's meta strength (T-10, T-12); the search
caret in meta while the line's whole job is action (T-06).
Each is cured at its delta; the pattern is recorded here
because it is the hunt's real catch: **in this file, ink was
doing the grading the print had renounced** — Dens's charge
against SPENT's arrangement, in the colour channel.

---

## 5 · FLAGS FOR LADE

- **F-1 — Filing-line height conflict between siblings.**
  Finisher 2 (D-09) sets each filing line at 44dp; Finisher 4
  (§1) sets 48dp. Geometry, not this slice — every type delta
  in §3.4 is height-independent — but the two specs cannot both
  build. One ruling needed before the Builder reaches the zone.
- **F-2 — T-21 amends a Designer shorthand.** Designer §1's
  table files "detail body" under Atkinson; T-21 moves only
  the ESTIMATE/SPENT fact lines to Anonymous, on Designer §1's
  own telemetry row. If LADE prefers the facts stay Atkinson,
  the fallback that preserves the ladder is Atkinson Regular
  **12sp** (killing the orphan 13) — the size cure is
  load-bearing; the face cure is the arguable half.
- **F-3 — Search headings and day words (Critic M9 vs
  Finisher 4 §4.3).** Adopted: wordless search headings
  (Finisher 4). Recorded dissent: the same declared day prints
  with its word on the landing and without it in search
  results. A type-consistency seat cannot call that finished —
  it is *ruled*, which is different. LADE's merge should carry
  it as a known, owned inconsistency, not a discovered one.

## 6 · VERIFICATION — FOR REN'S RENDER PASS (nobody here compiles)

1. Screenshot the landing: STORED is the heaviest print on the
   screen; MONTHS reads one step below it; no Atkinson caps
   anywhere in the frame; no size on screen outside
   {13, 12, 10.5} except row titles at 14.
2. Type one character in search: the line's face, size, and
   width-per-character do not change; only ink deepens and the
   caret is violet.
3. Open a MONTH from the shelf: the heading is pixel-equal in
   voice to the row tapped (12sp Bold caps, full ink).
4. DETAIL: provenance block is one voice (11sp/1sp/meta, two
   lines); facts are mono; substeps show no strike and no faded
   step; no FORGED anywhere.
5. A declared day, three ways (filing zone, landing heading,
   MONTH heading): the word is full ink in all three, its date
   meta in all three.
6. Grep the finished `DrawerScreen.kt`: `FontWeight.Medium`,
   `12.5.sp`, and Atkinson caps strings (`"BACK"`, `"NOTHING`)
   return nothing in Drawer scope.

---

— Finisher 1 — Type (issue #8 cell)
