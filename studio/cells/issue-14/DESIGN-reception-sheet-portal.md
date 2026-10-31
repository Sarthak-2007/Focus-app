# DESIGN — THE RECEPTION SHEET SURFACES (cards · total · DONE tear)

Seat: **PORTAL (designer)**, dedicated-100 programme, cell #14
under **Pforte** (lead), GitHub issue #14,
`Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Scope, entire: the first-open Reception Sheet surfaces —
**the door cards, the total line, the DONE tear.** Nothing
else. Movements' wording, the chit mechanism, the ledger and
the handover are other seats' work; where my surfaces touch
them I spec the seat and the interface, never their insides.

Sources read whole before drawing: `AUDIT-setup-sheet.md`
(Pforte's audit, §1–§9), the cell's current
`ReceptionSheet.kt` (1,296 lines, post-§6 merge),
`GuardLine.kt`, `ReceptionSheetTest.kt` (incl. the
256-combination guard), Fibel's `DESIGN-composition.md`,
Letzt's `FINISH-tear-exit.md`, `Type.kt` (the ladder), and
Empfang's original lane sheet
(`builds/onboarding/…/ReceptionSheet.kt`, 692 lines) — the
version in which S-1 was born.

Paper work only. Nothing compiled, no gradle run, no code
edited by this seat. Every value below is decided; where a
value is inherited from a ruling I name the ruling, and
where it is mine it is marked **INV-n** with its price.

Design law held throughout: hand-built only, no AI images,
no green or near-green anywhere; type only from the house
ladder (Sora / Atkinson Hyperlegible / Anonymous Pro);
live state words only — **SET / NOT SET / UNPROVED /
NOT CHECKED** — no synonyms, no softening, ever.

---

## §0 · THE THESIS

The audit's headline charge, S-1, was cured in the cell's
code the way a sentence is cured: the total's *words* now
branch on print kind, and a 256-combination test guards the
result. That cure is real — and it is a discipline, not a
structure. Read the current sheet's composition cold:

- the door cards render from a `prints` list precomputed in
  the sheet body;
- the total calls `receptionTotal(snapshot, nowMono)`, which
  **re-derives every print a second time** from the raw
  snapshot;
- the Guard line at the doors' foot calls
  `guardLineFor(snapshot, nowMono)`, which **re-derives them
  a third time**.

Three derivations, one function (`permPrintAt`), one
instant — they agree today because nobody has edited one of
them yet. The first future edit that touches a staleness
bound, a counted set, or a branch in exactly one place
re-opens S-1 silently, on a surface whose whole job is to
be believed. The test would catch some of it. The drawing
should make it un-drawable.

**This design's one move: the sheet takes ONE READING, and
every surface below the root is a view of it — by type, not
by convention.** A surface that never receives the snapshot
cannot re-derive anything. A total whose only input is the
same list instance the cards rendered cannot contradict
them, in the same way a sum written under a column of
figures cannot disagree with the column if it is *counted
off the column* and never off the ledger behind it.

Everything else in this file — card anatomy, the total
block, the tear — is built to serve or to show that move.

---

## §1 · THE IMPOSSIBILITY CONSTRUCTION (S-1, structural cure)

### 1.1 The Reading — one object, made once, at the root

At the sheet's root, per (snapshot, nowMono), exactly one
value is built:

```
SheetReading(
    doors: List<DoorPrint>,        // COUNTED order, size 4, always
)
DoorPrint(
    item:  WatchItemId,
    print: PermPrint,              // GRANTED / MISSING / STALE / UNMEASURED
    word:  String,                 // SET / NOT SET / UNPROVED / NOT CHECKED
    key:   KeyKind,                // FIX / CHECK / NONE
    gloss: String?,                // the STALE gloss, else null
    price: String?,                // this door's total sentence, else null
)
```

Every field of `DoorPrint` is decided in **one** `when
(print)` at the reading's construction — the only place in
the sheet where a print is interpreted. The word mapping is
the locked K-1 grammar (SET ≡ GRANTED, NOT SET ≡ MISSING,
UNPROVED ≡ STALE, NOT CHECKED ≡ UNMEASURED). The price is
the audit's acquitted per-kind sentence: the absence
sentence for a MISSING door (A-2 verbatim, S-6's admin
cure), the aged-proof sentence for a STALE door (S-1/S-12
words), null for GRANTED (nothing to price) and for
UNMEASURED (an unread door prices nothing — the reading
sentence covers it, §1.3).

`WatchStore.permPrintAt` is called from the reading's
construction and **nowhere else in the sheet**. That is a
grep-able law, stated here so a reviewer can enforce it in
one search: in `ReceptionSheet.kt`, `permPrintAt` appears
at the reading site only. (The PARK board keeps its own
reading of the same store — board vs sheet agreement rides
the store, as today; this law governs the sheet's insides,
where S-1 lived.)

### 1.2 Surfaces receive the Reading, never the snapshot

Signature law, binding on the builder:

- `ReceptionDoorCard(door: DoorPrint, …)` — no snapshot,
  no clock, no store parameter exists on the card.
- `ReceptionTotalBlock(reading: SheetReading)` — the total
  is a pure render of the list. There is **no**
  `receptionTotal(snapshot, nowMono)` overload left to call
  by accident; the existing pure function survives only as
  the reading's internal builder, renamed so its product is
  the reading, not a parallel total.
- The in-sheet Guard line renders `reading.guardText`,
  computed inside the reading from the same list (the
  header band outside the sheet keeps `guardLineFor` — its
  own surface, its own seat; inside the sheet there is one
  derivation, not two).

A contradiction between card and total now requires one of
two things, both visible in a diff: a surface growing a
snapshot parameter (a signature change — review catches
signatures), or a second `permPrintAt` call site (the grep
law). It cannot arrive as a quiet branch edit inside a
total function, because there is no total function with
anything to branch on.

### 1.3 The count is a tally, and the reading sentence

- The numeral is `reading.doors.count { it.print ==
  GRANTED }` — counted off the rendered list, never off a
  parallel derivation. Its type is `Int?`: **null while any
  door is UNMEASURED**, exactly today's law (a total is
  never printed over an unread door). Null numeral → no
  `GUARD n/4` line at all; the block prints the reading
  sentence alone (§3.3).
- The total sentence is `reading.doors.mapNotNull {
  it.price }.joinToString(" ")` — concatenation of the
  cards' own price fields, in COUNTED order. Composition,
  never precedence (C-10): a MISSING door cannot erase a
  STALE door's price because neither is selected by a
  branch; both are *in the list*. All four GRANTED → the
  price list is empty and the sentence is S-9's full stop:
  `ALL FOUR DOORS SET.` Any UNMEASURED → the sentence is
  `THE DOORS ARE STILL BEING READ.` and every price is
  suppressed with the count — an unread sheet prices
  nothing it has not read.
- **Atomic frame rule.** The 5-second staleness beat writes
  ONE new `SheetReading` into ONE state holder. Cards,
  Guard line and total recompose in the same frame from the
  same instance. No surface reads `SystemClock` or the
  snapshot itself, so no surface can be half a beat ahead of
  another: a door going UNPROVED at second 46 changes its
  card, its proof-table cell (§3.2), the numeral and the
  sentence in one frame, or in none. The old failure —
  ~46 s of standing reading collapsing the total over cards
  that still said SET — is un-drawable twice over: the
  words are per-kind (§1.1) and the update is atomic.

### 1.4 The one component both surfaces share

The state word is drawn by **one composable**,
`DoorWordText(door: DoorPrint, chitRepaired: Boolean)` —
Atkinson Hyperlegible Bold 15sp, letterSpacing 0.5sp;
colour `onSurfaceVariant` for a standing SET (the dim SET
is acquitted, Letzt A-3), `onBackground` for every lack,
and ink-bold for the chit's one repaired printing (Funke
§1.6's span, as built). The card header calls it. The
proof table (§3.2) calls it. There is no second piece of
code in the sheet that turns a print into a state word, so
there is no second place for the words to drift apart.

Same law for the hairline (`ReceptionHairline`, 1dp @
0.18f — PARK's ruling, byte-identical) and the tear rule
(§4.3): one definition, every seat.

### 1.5 How each old road to contradiction is closed

| Road (how S-1 or a sibling is reached) | Closed by |
|---|---|
| Total branches on `!= GRANTED`, pricing STALE as absence (S-1 as built by Empfang) | Price is a field of the print kind, fixed at the reading (§1.1). The total has no branch. |
| A MISSING door erases a STALE door's price (the pre-C-10 reading) | Sentence = join over the list (§1.3). Nothing is selected, so nothing is erased. |
| Card gloss and total sentence drift apart over edits | One string per door: the gloss on an UNPROVED card and its price in the total are the same field's family, written once (§2.3). |
| Count computed from a different set than the cards show | Count = tally over the rendered list (§1.3). |
| Half-updated frame during the staleness beat | Atomic reading swap (§1.3). |
| Chit line outliving the word it testifies about | Inherited law, kept verbatim: `chitPrintsAgainst` — a judged chit prints only while the door's current print still agrees with its verdict. My cards consume the chit only through that gate (§2.4). |
| A future surface (a new key, a badge) re-deriving state | Signature law (§1.2) + the one-`permPrintAt`-site grep law (§1.1). |

### 1.6 What this costs the existing tests

Nothing is thrown away. The 256-combination guard moves
one level down, from `receptionTotal(snapshot, …)` to the
reading builder, and gains the assertion S-1 actually
needs: for every combination, **the card word list, the
proof-table cells, the numeral and the sentence are all
read off the one returned reading and must agree** — word
list ≡ cells by construction, numeral ≡ count of SET
words in that list, sentence ≡ join of that list's prices.
The existing per-kind sentence tests (stale-as-aged-proof,
mixed lacks, every-missing-priced) keep their expected
strings byte-for-byte; they now assert against
`reading.sentence`. Price: SMALL — one builder rename,
signature changes on two composables, test re-pointing.
No new machinery, no store change, no DB touch.

---

## §2 · THE DOOR CARDS

### 2.1 Anatomy — one form, four fillings

The card family is the sheet's spine: a form being filled,
the same ruled row four times (audit §1.9, KEEP the form).
Every card, expanded form, top to bottom:

```
 10dp
 ACCESSIBILITY                                    SET ← word (DoorWordText)
 ────────────────────────────────────────────────  ← (SET only: card ends here)
 THE INTERCEPTOR. Shuts the apps you named, …      ← job (BodyTextStyle)
 WENT OUT NOT SET · CAME BACK SET. · NEXT — …      ← chit line, when judged
 THE LAST PROOF AGED OUT — CHECK RE-PROVES IT      ← gloss (UNPROVED only)
 YOU'LL SEE A LIST OF APPS. FIND MONOLITH, …       ← escort (NOT SET only)
 [           FIX — ACCESSIBILITY                 ] ← key band, 58dp envelope
 10dp
 ────────────────────────────────────────────────  ← ReceptionHairline
```

Page seat: full-bleed ruled page, 20dp side margins (the
sheet is PARK's family — a page of the book, not a slip;
no card chrome, no border, no scrim, per the audit's
family correction). Vertical rhythm is the built one:
10dp head spacer, 4dp between rows, hairline last.

Type and colour per element, all from the ladder:

| Element | Style | Colour |
|---|---|---|
| Door name | `PillBadgeTextStyle` — Anonymous Pro Bold 10/12, tracking 2sp | `onSurfaceVariant` |
| State word | `DoorWordText` — Atkinson Bold 15, tracking 0.5sp | §1.4 (dim SET / ink lack / ink-bold chit SET) |
| Job line | `BodyTextStyle` — Atkinson 400 15/22 | `onBackground` |
| Chit line, gloss, escort | `DoorJobTextStyle` — Atkinson 400 15/22 | `onSurfaceVariant` |
| Key label | `PrimaryButtonTextStyle` — Sora Bold 15/20, tracking 0.5sp | `onBackground` |

The state is carried by the **word**, never by colour
alone: SET and NOT SET differ as text before they differ
as tone, so the card survives colour-blindness, night
light and a greyscale capture intact. Colour only ranks
what the words already said — a finished door recedes, an
open one stands in ink.

### 2.2 The four fillings — one card per print kind

**SET (GRANTED) — the collapsed line, 37dp.** 8dp + a 20dp
row (name left, SET right, dim) + 8dp + hairline. No job,
no escort, no key (F-6, as built). The sheet visibly
completes as he works: each repair returns 52dp to the
page and pulls the foot toward him. The collapse is the
sheet's progress bar — the only one it is allowed, because
it is made of the doors themselves, not of a percentage
that could lie about them.

**NOT SET (MISSING) — the full card.** Job line (with its
door's stranger lines riding it, as cured: Door 1's
network-permission sentence in its C-16 form, Door 3's
way-back, Door 4's held-on-this-phone), escort beneath —
full form on the first NOT SET card in COUNTED order,
short list form on later ones, each door's Android warning
whole in both forms (F-5) — and the FIX key: 48dp,
radius 8dp, `surfaceVariant` dress (F-4: ink is DONE's
alone), 1dp dip on DOWN held for the whole absence, seat
knock (10ms, 0.1 day / 0.06 night, 300Hz), act on
release-true only, departure chit written before the
intent fires. Label: `FIX — <BOARD NAME>`.

**UNPROVED (STALE) — the aged card.** Job line, then the
gloss in full — `THE LAST PROOF AGED OUT — CHECK
RE-PROVES IT` — then the CHECK key in the same 58dp
envelope: `surfaceVariant`, no dip ever, FIX's knock
values as its voice. No escort: he has been to this door;
what he needs is not directions but a re-proof. The word
UNPROVED is never apologised for and never hidden — it is
the sheet admitting the limit of what it knows, in the
same size as every other word on the card.

**NOT CHECKED (UNMEASURED) — the unread card.** Job line,
**no escort** (S-12: an unread door carries no promises
about a trip it has not measured), and the key band
**reserved at the full 58dp** from first composition
(F-7): the CHECK key stands in it from the start — a sweep
is the only honest verb for an unread door — so the first
measurement changes words, never layout. Nothing on this
card moves when the sweep lands; only the print changes.
A form's empty field, already ruled, waiting for ink.

### 2.3 One string per door

The gloss printed on an UNPROVED card and the aged-proof
price printed in the total are **the same sentence** —
`THE LAST PROOF OF <NAME> AGED OUT — CHECK RE-PROVES IT.`
in the total's composed form, the gloss its card form —
both generated from the door's single `DoorPrint`, never
typed twice in two files. If the words are ever re-ruled,
they change in the reading's one `when`, and card and
total change together in the same commit, the same frame,
the same build. (This is §1.5's third row, drawn.)

### 2.4 The chit's seat on the card

The Return Chit (Funke's mechanism, other seat's insides)
prints **one line, in the visited card only**, directly
under the job line, `DoorJobTextStyle`, gated by
`chitPrintsAgainst` (§1.5). On a SET card it breaks the
collapse for exactly one printing: the ruled line grows
the chit line beneath it (37 → 54dp), its SET standing
ink-bold for that printing only — the testimony's one
emphasis. On any later printing the card is a plain ruled
line again. The chit never prints on the total, never in
the proof table: it is a card's memory of its own errand,
and it lives where the errand happened.

### 2.5 Key choreography (inherited, stated once)

Keys never appear or vanish in one frame: PARK's
visibility pair on both key blocks — 160 Seat in / 160
Throw out, the 10dp spacer riding inside the animated
58dp envelope. Transitions: NOT SET ⇄ UNPROVED swaps
CHECK and FIX concurrently at constant envelope height;
any → SET throws the key out over 160 while the hairline
rises to meet the collapsing card. First composition
starts entered — a standing key is printed, not animated
in. Bottom padding of the sheet: **24dp** (the audit §3
micro-pass value; the cell code still carries 40dp —
flagged for Pforte as the one unlanded line of that pass).

---

## §3 · THE TOTAL LINE

### 3.1 The block, in order

The total is the foot of the form, above the tear, in this
exact stack (routine line rides S-2/F-10 above it):

```
 NEXT ROUTINE — MORNING · 09:00 · SET ON SHIELD   ← PillBadge, variant
 10dp
 GUARD 3/4                                        ← Anonymous Pro Bold 15
 ┌─────────────────────┬─────────────────────┐
 │ ACCESSIBILITY       │ USAGE ACCESS        │   ← proof table (§3.2)
 │ SET                 │ SET                 │
 ├─────────────────────┼─────────────────────┤
 │ DEVICE ADMIN        │ NOTIFICATION        │
 │ NOT SET             │ LISTENER  UNPROVED  │
 └─────────────────────┴─────────────────────┘
 WITHOUT DEVICE ADMIN, MONOLITH CAN BE           ← price sentences,
 UNINSTALLED. THE LAST PROOF OF NOTIFICATION      BodyTextStyle, joined
 LISTENER AGED OUT — CHECK RE-PROVES IT.          off the reading (§1.3)
 10dp
 THIS SHEET WAITS IN PARK — SETUP + INTRODUCTION. ← stub note (above the
 PARK OPENS BY TAKING THE RAIL CAP TO THE …         tear, F-12, latched §4.4)
 12dp
 - - - - - - - - - - - - - - - - - - - - - - -    ← tear rule (§4.3)
```

### 3.2 **INV-1 — the proof table: the total shows its working**

Between the numeral and the sentences, a 2×2 ruled table:
the four doors again, in COUNTED order (row-major:
ACCESSIBILITY, USAGE ACCESS / DEVICE ADMIN, NOTIFICATION
LISTENER), each cell printing the door's board name
(`PillBadgeTextStyle`, variant) over its state word —
drawn by `DoorWordText` (§1.4), the same component, fed
the same `DoorPrint` instance the card above rendered.

This is the S-1 cure made *visible to the reader*, not
only to the code: at the decision moment, one short scroll
above DONE, he can recount the total against the table,
and the table against the cards, and find the same four
words in the same order three times. A printed form that
shows its working does not ask to be trusted.

Geometry: two columns split at the page's midpoint, cells
separated by the sheet's hairline (1dp @ 0.18f) — vertical
rule between columns, horizontal rule between rows and
under the table; cell padding 8dp vertical, name-to-word
gap 4dp. Cell height ≈ 36dp; table ≈ 73dp + rules.
NOTIFICATION LISTENER wraps to two badge lines in its
cell; the row grows, the word never shrinks (F-13's law,
applied here first).

**Price:** +~85dp at the foot in every state. Paid for, in
the states that matter, by F-6: every SET door returns
52dp above, so a finished or half-finished sheet is
*shorter* with the table than today's sheet is without it.
Only the untouched sheet (no door set, nothing collapsed)
pays full price — and that is the sheet whose reader has
the most checking to do. The K-2 amendment still holds:
the numeral — the datum the foot exists for — sits exactly
where it sits today, one short scroll past the last card;
the table grows *below* the numeral, toward the tear.

### 3.3 The numeral and the sentence — every state

| Reading | Numeral line | Sentence |
|---|---|---|
| Any door NOT CHECKED | *(no numeral line)* | `THE DOORS ARE STILL BEING READ.` — proof table still prints, its unread cells reading NOT CHECKED; no prices. |
| All SET | `GUARD 4/4` | `ALL FOUR DOORS SET.` Full stop (S-9). No "blocking stands whole" — the warrant's third conjunct is not this sheet's to claim. |
| Lacks, any mix | `GUARD n/4`, n = tally of SET cells | The join of the lacking doors' prices, COUNTED order, each in its own kind's words (§1.3). The one-lack case prints byte-identical to today's acquitted branch. |
| All four UNPROVED | `GUARD 0/4` | Four aged-proof sentences. The numeral says *fresh proofs: none*; the sentences say why — 0/4 over four granted-but-aged doors can no longer be misread as 0/4 over four absent ones, because the proof table above it reads UNPROVED ×4, not NOT SET ×4. This row is S-1's original crime scene, and it is the row this design exists for. |

The numeral's dress is F-11's: Anonymous Pro Bold 15sp,
tracking 0.5sp, `onBackground` — the telemetry family
carries the datum; the sentence stays prose
(`BodyTextStyle`). The in-sheet Guard line at the doors'
foot prints the identical datum from the identical reading
(§1.2) — one fact, one print, two seats, no fork.

### 3.4 What the total never does

- Never counts a door it has not read (§3.3, row 1).
- Never prices a door in another door's kind of words.
- Never claims a consequence beyond the doors: no seal,
  no blocking-whole, no firing (S-2/S-9 family).
- Never names a "first" lack as the only lack: every
  lacking door is priced (Blank §5.6), and the proof table
  names all four, always.

---

## §4 · DONE AND THE TEAR — a physical act, not a picture of one

The brief's law for this surface: **DONE is a tear.** Not
a button that dismisses a sheet while a dashed line
decorates it — the sheet *separates*, the part below the
rule leaves in his hand, and the stub stays. Funke's
ruling, ratified by the audit and kept here whole: **the
machine tears; the finger doesn't.** The slide on the Deck
already owns drag-to-commit; a second drag ritual on the
exit would gate the sheet's only exit behind the gesture a
04:00 hand performs worst. His finger presses. The paper
does the tearing.

### 4.1 The DONE key

- The sheet's **one ink key**: full width, 52dp, radius
  8dp, ink fill, label `TO THE DECK →` in
  `PrimaryButtonTextStyle` (Sora Bold 15). Every other key
  on the sheet wears `surfaceVariant` (§2.2) — ink is
  spent exactly once, on the act that ends the document.
- Press life: 1dp dip on DOWN, **in silence** (a stamp's
  sound is its strike; nothing plays on the way down).
  The act commits on **release-true only**: a drag-off or
  a scroll that starts on the key stamps nothing, tears
  nothing, and the dip lifts.
- Live at **any** door state — all SET, all NOT SET, mid-
  reading. DONE is never gated, greyed or priced. The act
  line beneath it says so, standing (§4.2), so the
  permission is printed before it is needed.
- On a PARK reprint (setup already stamped) the same key
  prints, untorn: firm click, immediate 160 THROW exit
  (§4.5). Same key, same words — the difference is what
  the press *does*, and §4.2's line is worded to stay true
  in both printings.

### 4.2 The act line — what the key does, printed under it

10dp under the key, `PillBadgeTextStyle`, variant colour:

> THIS FINISHES THE SETUP. IT STARTS NOTHING, SEALS
> NOTHING, AND MAY BE PRESSED WITH DOORS UNSET.

One line of act (Blank (a)3, as built). It is part of the
torn block (§4.4): it describes the leaf it rides on, and
it leaves with it. On a reprint it prints again, unchanged
— a reprint is the same document.

### 4.3 The tear rule

The line the sheet separates on, drawn on PerforationRow's
geometry (Letzt F-2, as built): dash 3dp / gap 3dp **in
dp**, centred in its 1dp height, tiled from the left edge,
ink at 0.35 alpha. It is the only dashed rule on the sheet
— everything else is hairline-ruled — so the eye learns it
on first sight: *this is where paper separates.* Its
geometry is shared with the counterfoil's perforation and
with the dashed rule over PARK's stub row (§4.6) — one
spec, three seats, never a lookalike drawn twice.

### 4.4 The tear — timeline of a stamping press

From the release-true commit (the dip already travelled
on DOWN and does not return — the torn block continues
from it):

| t | Event |
|---|---|
| 0 | **The stamp books first.** `onStamp()` — SetupStore commits; the setup is stamped before any sound or motion. A death anywhere after this line leaves a stamped setup. The stub note above the tear is latched at this instant, so the booking cannot un-print it mid-tear. |
| 0 | `playTearPulse()` — the tear's own voice replaces the firm click. One act, one voice; the click is not layered under it. |
| 0 | `tearing = true`: every key and the scroll lock for the tear's whole duration. No second press exists inside the window — there is no re-entry path, no second stamp, no second sound. |
| 0–180 | **The leaf separates:** the block below the rule — DONE key + act line, one wrapper, nothing else — drops 10dp and fades to nothing, `specA(180)`. |
| 0–260 | **The rule gives way:** dashes halve, 3dp → 1.5dp, gap constant, `specB(260)`. The pitch tightens as the paper parts — the rule does not fade, it *thins*, the way a perforation looks mid-tear. |
| 0–260 | **Above the rule: stillness.** No tear-driven transform, alpha or motion on the stub — nameplate, cards, total, stub note stand exactly as printed. That stillness is the stub staying in the hand. (Live re-prints — a staleness beat landing mid-tear — may still change words above the rule; that is the sheet's standing liveness, not tear motion, and §1.3 keeps it consistent.) |
| 260 | The driver hands the exit to the host: `onDone()`. |
| 260–420 | The whole sheet throws, 160 THROW, exactly the standing exit. Torn values hold through it — nothing pops back. |
| 420 | Uncomposed. He is on the Deck. The Armed Handover (Funke's mechanism) is the Deck's seat, not this surface's. |

Total **420ms**, composed entirely of house values (180
specA + 260 specB + 160 THROW) — no new duration, curve or
sound is introduced by this design.

**Reduced motion:** if the system's animator scale is 0,
the tear collapses to its facts — stamp, pulse, instant
separation, THROW. The act is never gated behind its own
animation; a man who has killed motion still finishes his
setup in one press.

**Semantics:** the tear is announced as the act it is —
the DONE key's accessibility label carries the stamp
(`TO THE DECK` + the act line as its description), and the
exit is announced once, at the handover, not narrated
through the animation.

### 4.5 The presses that do not tear

- **Reprint DONE** (already stamped): firm click, exit at
  once, 160 total. No stamp call, no tear state touched.
  A reprint never inherits a spent tear — every showing
  starts untorn.
- **BACK** at any moment: unstamped, untorn, free. The
  sheet re-offers at the next cold start and waits in
  PARK. During the 260ms tear window BACK may truncate
  the motion; it cannot truncate the fact — the stamp
  booked at t=0 (§4.4), which is why that order is law.

### 4.6 **INV-2 — the stub is the tear's other half**

When he next meets this sheet's remains, it is PARK's stub
row — and the dashed rule that stands above that row
(F-13's second half, shipped in the audit) is **this**
rule: same PerforationRow geometry, same 0.35 ink, drawn
from the same spec. The design states the continuity
out loud so no future seat "tidies" one of them into a
hairline: the stub row in PARK is the counterfoil of the
first-open tear — the part of the document that stayed,
still wearing the edge it tore along. Price: zero. It is
already built in both seats; this paragraph only forbids
diverging them.

---

## §5 · THE SHEET AROUND THE THREE SURFACES (context, fixed)

So the three surfaces sit in one document, the frame they
share, decided and not reopened here: nameplate `MONOLITH
ML-01` (S-13: no `· FIRST OPEN` — the plate names the
stamp, not the opening) → title `SETUP + INTRODUCTION`
(Sora Bold 22, `DrawerHeaderTextStyle`) → hairline →
Movement 1 (`WHAT IT IS`; the identity at AppTitle weight;
the YPT / no-score boundary in the register's cured words)
→ Movement 2 (`THE FOUR DOORS`; §2's cards; the rank line;
the Guard line at the foot) → Movement 3 (`HOW A SESSION
RUNS`; the six beats as a ruled table, Anonymous Pro label
column; the Shield door with its printed way back) → the
total (§3) → the tear (§4). Rise 280 SEAT on open. The
sheet is full-bleed, background = scheme background; night
changes sound amplitudes only, never print.

---

## §6 · STATE MATRIX — the whole sheet, checkable

One row per reading; every surface's print in that reading.
A render reviewer checks captures against this table, row
by row — any cell that differs is a defect by definition.

| Reading | Cards | Guard line (in-sheet) | Total block | DONE |
|---|---|---|---|---|
| Untouched (no snapshot yet) | 4 × NOT CHECKED, CHECK keys standing in reserved bands, no escorts | prints nothing | No numeral; `THE DOORS ARE STILL BEING READ.`; proof table NOT CHECKED ×4 | Live |
| All SET | 4 × collapsed ruled lines | `GUARD 4/4` | `GUARD 4/4`; table SET ×4; `ALL FOUR DOORS SET.` | Live; tears on press (first open) |
| All NOT SET | 4 × full cards, FIX keys, first escort full | `GUARD 0/4 — ACCESSIBILITY` | `GUARD 0/4`; table NOT SET ×4; four absence prices in COUNTED order | Live |
| All UNPROVED (granted, aged) | 4 × aged cards, gloss + CHECK | `GUARD 0/4 — ACCESSIBILITY STALE` † | `GUARD 0/4`; table UNPROVED ×4; four aged-proof prices; **no WITHOUT anywhere** | Live |
| Mixed (1 NOT SET, 1 UNPROVED, 2 SET) | 2 collapsed, 1 full, 1 aged | `GUARD 2/4 — <first lack>` | `GUARD 2/4`; table mirrors cards cell-for-cell; absence price + aged-proof price, COUNTED order | Live |
| Return from a FIX trip, repaired | Visited card SET + chit line, ink-bold SET, one printing | per current reading | per current reading — the chit never reaches the total | Live |
| PARK reprint, day forty | Identical grammar, live prints of that day | per current reading | per current reading | Live; firm click, no tear |

† The Guard line's `STALE` suffix is the header family's
own word on its own surface (GuardLine spec, chrome C2) —
it is **not** a fifth state word on this sheet: inside the
sheet's surfaces the door's word is UNPROVED, and the proof
table is where a reader inside this document checks it.
Flagged, not redesigned: if Pforte wants the suffix
re-ruled, it is the Guard line's seat, and it must change
on the header and in the sheet together or not at all.

---

## §7 · ACCEPTANCE — paper checks for the merge render

Closes, for these surfaces, the audit §9 render debt
(no render exists; every look claim is code-judged):

1. Captures `11_reception` / `n11_reception` in at least
   three readings — untouched, mixed (§6 row 5), all
   UNPROVED (§6 row 4) — checked cell-by-cell against §6.
2. The recount, by eye, on the mixed capture: cards →
   proof table → numeral → sentences, four words in
   COUNTED order, identical in all three seats.
3. The tear, on a real motor at 04:00 light: stamp-first
   order verified by killing the app mid-tear (relaunch
   must land stamped, on the Deck's terms); stub stillness
   above the rule; 420ms total, no pop-back in the THROW.
4. The grep law (§1.1): `permPrintAt` at one site in the
   sheet; no snapshot parameter on card or total.
5. The 256-combination guard, re-pointed at the reading
   (§1.6), green — with its new agreement assertions
   (numeral ≡ SET-word tally; sentence ≡ price join).

No compile is run by this seat; these checks run at the
merge, under Ren, when Sarthak says.

---

## §8 · INTERFACES AND FLAGS FOR PFORTE

- **Builder interface:** §1.2's signatures and §1.1's
  reading are the whole ask. Cards (§2), total (§3) and
  tear (§4) consume them as drawn; Fibel's composition
  spec and Letzt's tear spec remain the builders' texts
  for everything this file inherits rather than re-decides.
- **Flag 1 (unlanded micro-pass):** sheet bottom padding
  40dp in code vs the audit's ruled 24dp (§2.5).
- **Flag 2 (Guard `STALE` suffix):** §6 † — the header
  family's word, referred, not touched.
- **Flag 3 (S-8):** unchanged by this design — the ROOM
  beat's truth still gates on the flow-clock composition
  at merge time; no surface in this file depends on it.
- **Not mine, not touched:** the chit's states and store,
  the Setup Ledger, the Armed Handover, the Shield
  excursion's departure law, PARK's insides beyond the
  stub-row continuity named in §4.6.

— PORTAL (designer), dedicated-100, cell #14 · Oct 30, 2026
