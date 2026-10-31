# FINISH — GEOMETRY & MEASURE: EVERY DP ON THE RECEPTION SHEET

Seat: **Zoll (finisher, seat 3)**, Pforte's cell, GitHub issue
#14, `Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Scope: gallery standard for **geometry and measure only** —
the full vertical measure of the sheet as it now stands, the
F-6 collapse geometry, the F-7 reserved key band, the key
census, and the PARK stub-row squeeze. Paper spec only: no
app code edited, nothing compiled, no tests run (build law).

Ground, read whole from the working tree:
`app/src/main/java/com/example/ui/components/ReceptionSheet.kt`
(784 lines — the CURRENT file, after the builder's wording /
S-12 edits), `ParkDrawer.kt`, `GuardLine.kt`, `WatchStore.kt`
(`COUNTED`, board names), `ui/theme/Type.kt`, and
`studio/plan/AUDIT-setup-sheet.md` §4 (K-2 as amended, F-6,
F-7, F-13). Fibel's `DESIGN-composition.md` §§12c/12d is the
F-6/F-7 form source — **note: that file is truncated on disk**
(it ends mid-code-block inside §12d); the F-6 block and the
F-7 envelope sentence survive whole and are what §2/§3 cite.

---

## §0 · METHOD — how every number below was got

- **True-font measurement, not chars-per-line folklore.**
  Every wrapped text was laid out greedily (word wrap on
  spaces) using the advance tables of the TTFs shipped in
  this tree (`res/font/atkinson_hyperlegible*.ttf`,
  `anonymous_pro_bold.ttf`, `sora_bold.ttf`), at the style's
  real size and letterSpacing. Text height = wrapped lines ×
  the style's `lineHeight`. The strings measured are the
  current code's strings, character for character.
- **Reference column: 320dp.** The sheet's column is the
  screen minus `PaddingValues(start = 20, end = 20)`; the
  canonical device is 360dp wide → **320dp of print width**,
  the same column the audit, Fibel, and Glatt all measured
  against. Viewport: **~760dp** tall, per the brief.
- **fontScale 1.0.** All `sp` heights below are at system
  fontScale 1.0; `dp` spacers never scale, `sp` text does.
- **The one unstyled line.** The door name/word row and the
  doors'-foot Guard line set no `lineHeight`: Atkinson's
  hhea metrics (ascender 950, descender −290, upm 1000)
  give a natural single line of **18.6dp at 15sp**. Both are
  measured at 18.6dp/line below. Every other text carries
  an explicit ladder lineHeight (`Type.kt`): PillBadge
  10/12 ls 2.0 · DrawerHeader 22/26 · Body 15/22 ·
  AppTitle 15/20 · PrimaryButton 15/20 ls 0.5 · the
  escort/gloss 12/17.
- **Knife edges (renderer risk, stated once).** Four lines
  sit within 3dp of the 320dp edge and can flip a wrap on a
  different renderer: D1 job L2 (318.0dp), D1 escort L1
  (318.7dp), D3 job L2 (317.7dp), D3 escort L1 (316.9dp).
  Each flip costs exactly one line (+22dp body / +17dp
  escort). **Every total below carries a ±22dp band** from
  D1's job line alone; treat totals as exact at this
  column/fontScale and ±~30dp in the wild.
- **Canonical printings.** Totals are for **first open**
  (`isFirstOpen = true`, stub note printed) **with a live
  routine standing** (the seeded rows guarantee one: the
  routine line prints 2 badge lines for every seed name —
  FASTED BLOCK / DAY BLOCKS / SLEEP all measure 2 lines).
  Deltas for the other printings are priced in §1.4.

---

## §1 · THE FULL VERTICAL MEASURE, IN ORDER

### 1.1 The fixed skeleton (state-independent, in sheet order)

| # | Element (code seat) | Measure | dp |
|---|---|---|---|
| 1 | Column top padding | `PaddingValues(top = 28)` | 28 |
| 2 | Nameplate `MONOLITH ML-01` | PillBadge, 1 line × 12 (104.4dp wide) | 12 |
| 3 | Spacer |  | 8 |
| 4 | Title `SETUP + INTRODUCTION` | DrawerHeader, 1 line × 26 (275.7dp wide — fits) | 26 |
| 5 | Spacer |  | 14 |
| 6 | `ReceptionHairline()` | 1 @ 0.18f | 1 |
| 7 | Spacer |  | 16 |
| 8 | Eyebrow `WHAT IT IS` | PillBadge, 1 × 12 | 12 |
| 9 | Spacer |  | 8 |
| 10 | Identity (S-7 words, Body) | **3 lines × 22** — L1 ends "…shuts the apps", L3 "backs you out of Shorts and Reels." | 66 |
| 11 | Spacer |  | 8 |
| 12 | Boundary line (PillBadge) | 2 lines × 12 ("…MONOLITH / KEEPS NO SCORE AND GRADES NOTHING.") | 24 |
| 13 | Spacer |  | 20 |
| 14 | Eyebrow `THE FOUR DOORS` | PillBadge, 1 × 12 | 12 |
| 15 | Spacer (before the card loop) |  | 4 |
| — | **Door cards ×4 — §1.2, state-dependent** |  | — |
| 16 | Spacer (after the loop) |  | 10 |
| 17 | Rank line (PillBadge) | 3 lines × 12 | 36 |
| 18 | Guard foot, when it prints | Spacer 6 + line(s) × 18.6 — §1.3 | 24.6 |
| 19 | Spacer |  | 20 |
| 20 | Eyebrow `HOW A SESSION RUNS` | PillBadge, 1 × 12 | 12 |
| 21 | Spacer |  | 8 |
| 22 | Beat ARM | label 12 + body 1 × 22 + spacer 8 | 42 |
| 23 | Beat SEAL | label 12 + body 2 × 22 + spacer 8 | 64 |
| 24 | Beat THE ROOM | label 12 + body 3 × 22 + spacer 8 | 86 |
| 25 | Beat PEEK | label 12 + body 3 × 22 + spacer 8 | 86 |
| 26 | Beat BREAK | label 12 + body 2 × 22 + spacer 8 | 64 |
| 27 | Beat FINISH | label 12 + body 2 × 22 + spacer 8 | 64 |
| 28 | First-routine line, when one stands | Spacer 4 + PillBadge 2 × 12 | 28 |
| 29 | Spacer |  | 14 |
| 30 | Rules line (S-14 words, Body) | **4 lines × 22** (all-caps prose is wide: L4 is the orphan "SESSIONS.") | 88 |
| 31 | Spacer |  | 10 |
| 32 | `OPEN THE LIST` key | Box, fixed | 48 |
| 33 | Spacer |  | 12 |
| 34 | Leave line (S-11 words, PillBadge) | 2 lines × 12 | 24 |
| 35 | Spacer |  | 20 |
| 36 | Total count `GUARD n/4`, when counted | AppTitle 1 × 20 + Spacer 4 | 24 |
| 37 | Total sentence (Body) | **state-dependent — §1.3** | — |
| 38 | Stub block, first open only | Spacer 10 + note 2 × 12 (Glatt's 24dp budget confirmed exactly) | 34 |
| 39 | Spacer |  | 12 |
| 40 | `ReceptionTearRule()` | 1 @ 0.35f | 1 |
| 41 | Spacer |  | 14 |
| 42 | DONE key `TO THE DECK →` | Box, fixed | 52 |
| 43 | Spacer |  | 10 |
| 44 | DONE act line (PillBadge) | 3 lines × 12 | 36 |
| 45 | Column bottom padding | `PaddingValues(bottom = 40)` | 40 |

**Skeleton subtotal** (everything except cards, the Guard
foot, the total count/sentence, and the stub block):
**1,160.0dp.** Beats block (#22–27) alone: 406dp. Movement 3
complete (#20–35, routine standing): 670dp + the total's
own lines.

### 1.2 One door card, dissected (as built, expanded form)

Card anatomy, top to bottom — every card, every state:

```
Spacer 10
name row        18.6   (badge 12 centred against the word's
                        natural 18.6 line; longest pairing
                        NOTIFICATION LISTENER + NOT CHECKED
                        = 266.1dp wide — fits, 53.9 slack)
Spacer 4
job (Body /22)  D1 88 (4 lines) · D2 44 (2) · D3 88 (4) · D4 88 (4)
[STALE only]    Spacer 4 + gloss 1 × 17            = 21
[MISSING only]  Spacer 4 + escort (12/17):
                D1 68 (4 lines) · D2 34 (2) · D3 34 (2) · D4 51 (3)
[key band]      Spacer 10 + Box 48                 = 58
                (MISSING → FIX, STALE/UNMEASURED → CHECK,
                 GRANTED → nothing, 0)
Spacer 10
hairline        1
```

The §5 stranger lines are what grew the jobs: D1, D3, D4
carry privacy / way-back tails and now run **4 lines**, not
the 2 the pre-cure jobs ran. D2 (no tail) stays 2.

| Door | SET (expanded) | MISSING | STALE | UNMEASURED |
|---|---|---|---|---|
| D1 ACCESSIBILITY | 131.6 | **261.6** | 210.6 | 189.6 |
| D2 USAGE ACCESS | 87.6 | **183.6** | 166.6 | 145.6 |
| D3 DEVICE ADMIN | 131.6 | **227.6** | 210.6 | 189.6 |
| D4 NOTIFICATION LISTENER | 131.6 | **244.6** | 210.6 | 189.6 |
| **Σ four cards** | **482.4** | **917.4** | **798.4** | **714.4** |

### 1.3 The state-dependent foot lines

- **Guard foot** (`guardLineFor`, Atkinson Bold 15 ls 0.5,
  full-width Text): `GUARD 4/4` 85.9dp wide → 1 line.
  First-lack forms: `… — ACCESSIBILITY` 232.1, `… — USAGE
  ACCESS` 224.6, `… — NOTIFICATION LISTENER` 308.1 (11.9dp
  slack), `… — ACCESSIBILITY STALE` 285.6 — all 1 line,
  block = 6 + 18.6 = **24.6**. **One exception:**
  `GUARD 3/4 — NOTIFICATION LISTENER STALE` measures
  **360.9dp — it wraps to 2 lines** ("…NOTIFICATION
  LISTENER / STALE"), block = 6 + 37.2 = **43.2 (+18.6)**.
  That state (D1–D3 granted, D4 stale) is the only printing
  where the sheet's Guard foot is a two-line print.
  UNMEASURED anywhere → the foot prints nothing (block 0).
- **Total sentence** (Body 15/22, per-lack composition):
  all-MISSING = the four absence sentences joined, **8
  lines = 176**; all-STALE = the four aged-proof sentences
  joined, **8 lines = 176**; all-SET = `ALL FOUR DOORS
  SET.`, **1 line = 22**; UNMEASURED / null snapshot =
  `THE DOORS ARE STILL BEING READ.`, **1 line = 22**, and
  the count line (#36) is absent entire (−24).

### 1.4 The three state totals (+ two reference states)

First open, routine standing, 320dp column, viewport 760dp:

| State | Cards | Guard | Count | Sentence | **TOTAL** | Viewports | Scroll past fold |
|---|---|---|---|---|---|---|---|
| **All-MISSING** | 917.4 | 24.6 | 24 | 176 | **2,336.0dp** | 3.07× | 1,576.0dp |
| **All-SET, as built (expanded)** | 482.4 | 24.6 | 24 | 22 | **1,747.0dp** | 2.30× | 987.0dp |
| **All-SET, F-6 landed (§2)** | 148.0 | 24.6 | 24 | 22 | **1,412.6dp** | 1.86× | 652.6dp |
| **Mixed (defined below)** | 753.4 | 24.6 | 24 | 88 | **2,084.0dp** | 2.74× | 1,324.0dp |
| Mixed, F-6 landed | 658.8 | 24.6 | 24 | 88 | **1,989.4dp** | 2.62× | 1,229.4dp |
| *Ref: all-STALE* | 798.4 | 24.6 | 24 | 176 | *2,217.0dp* | 2.92× | 1,457.0dp |
| *Ref: all-UNMEASURED* | 714.4 | 0 | 0 | 22 | *1,930.4dp* | 2.54× | 1,170.4dp |

**Mixed, defined:** board order SET · MISSING · MISSING ·
STALE (D1 granted, D2+D3 missing, D4 stale) — the mid-errand
printing that exercises every branch at once: one collapsed-
eligible card, two FIX bands, one gloss + CHECK band, Guard
foot `GUARD 1/4 — USAGE ACCESS` (1 line), total sentence =
the USAGE + ADMIN absence sentences joined (4 lines, 88dp;
the stale door is priced by precedence, not printed — the
edge table's C-3). Any other mix is the skeleton (1,160.0)
+ the §1.2 per-door column for each door's state + §1.3.

**Printing deltas:** day-forty reprint (not first open):
**−34.0** (stub block dies entire). No live routine:
**−28.0**. Stale-listener Guard foot: **+18.6** (§1.3).

### 1.5 Verdict on the audit's figures — one corrected

Audit §4 K-2 priced ~2,190dp all-MISSING, ~1,715dp all-SET,
~1,950dp untouched, against the **pre-merge 692-line tree**;
Fibel's own header prices those figures at ±5%.

- **All-MISSING ~2,190 → CORRECTED: 2,336.0dp (+146.0,
  +6.7% — outside the audit's own ±5% band).** The pre-merge
  baseline is not in this tree, so a closed line-by-line
  reconciliation is impossible; the identifiable drivers,
  each measured, all post-date or outrun the estimate:
  the per-lack total (Blank 6) takes the sentence from one
  lack's 66dp to **176dp (+110)**; the §5 job tails take
  three jobs from 2 lines to 4 (**+132** across the cards);
  the S-7 identity runs 3 lines, not 2 (**+22**); the DONE
  act line is new print (**+46** with its spacer) — partly
  offset by cures already in this tree that the estimate
  predates (F-1/F-3 kills, −74). **Use 2,336.0.**
- **All-SET ~1,715 → CONFIRMED as the expanded figure:**
  measured **1,747.0 (+32.0, +1.9%, inside the band)**. It
  is not the working figure once F-6 lands — §2.
- **Untouched ~1,950 → CONFIRMED:** the all-UNMEASURED
  printing measures **1,930.4 (−19.6, −1.0%)**.

---

## §2 · F-6 — THE GRANTED-DOOR COLLAPSE, EXACT GEOMETRY

### 2.1 The collapsed card, to the dp (Fibel §12c, verified)

A GRANTED door prints as one ruled line and nothing else:

```
Spacer                              8
Row, FORCED .height(20.dp), centred:
  name  (PillBadge 10/12, onSurfaceVariant)
  weight spacer
  word  SET (Atkinson Bold 15, ls 0.5, onSurfaceVariant)
                                     20
Spacer                              8
ReceptionHairline (1 @ 0.18f)        1
TOTAL                               37.0
```

- The forced **20dp row** is load-bearing: the word's
  natural line is 18.6dp (§0), the badge's is 12dp — the
  explicit 20 seats both centred with air (badge: 4dp
  above/below; word: 0.7dp) and makes the card exactly
  37.0 on every renderer, not 35.6 on this one.
- **What dies, by name:** the expanded card's top Spacer
  10 → 8 and bottom Spacer 10 → 8 (−4), the name-row's
  separate existence is kept, and the Spacer 4 + job
  (44–88) die entire. No escort, no gloss, no key band —
  a granted door never carried the last two, and the job
  is the collapse's whole saving.
- **Per-door saving vs §1.2 expanded SET:** D1 −94.6 ·
  D2 −50.6 · D3 −94.6 · D4 −94.6 → **−334.4 at 4/4.**
  (Fibel's "−52dp per door / ~89dp expanded" was priced on
  the pre-cure 2-line jobs; with the §5 tails the expanded
  card is 131.6 for three doors and the saving is larger.
  Her 37dp target itself is exact and stands.)
- **Return-chit exception (Fibel's interface ruling, kept):**
  on the chit's one printing the collapsed card grows the
  chit line under the row: 37 + 17 = **54.0**, hairline
  still last. One printing only; it never re-expands.

### 2.2 The all-SET total with F-6, and amended K-2

All-SET with every card collapsed: **1,412.6dp** (§1.4) —
1.86 viewports. Positions, from the document's top:
the fourth card's hairline lands at **407.0dp** (all four
doors + the doors' foot — rank and Guard line end at
497.6 — inside the first 760dp viewport); DONE's top at
**1,274.6**, its bottom at **1,326.6**; document end
1,412.6.

Amended K-2: *"the foot within one short scroll of the
last door card."* The audit never quantified "short
scroll" or the anchor, so both lawful readings are ruled:

- **Top-anchored (sheet just opened, all doors already
  granted — the day-forty shape): HOLDS, only with F-6.**
  Scroll travel to bring DONE's bottom into the viewport
  = 1,326.6 − 760 = **566.6dp ≤ 760** (one viewport-length
  scroll), 193.4dp to spare. As built (expanded) the same
  travel is **901.0dp — FAILS by 141.0.** F-6 is precisely
  the difference between holding and failing; this is the
  amendment's teeth, and they bite.
- **Last-card-anchored, literal (fourth hairline at the
  viewport's top, foot = DONE's bottom): MISSES by
  159.6dp.** The gap from the last card to DONE's bottom
  is **919.6dp** (Movement 3's 670 + the doors' foot 90.6
  + the foot block 159.0) and F-6 cannot shrink it — the
  collapse spends its saving *above* the last card. Even
  the total count's top sits 760.6dp past the last card —
  one viewport, to the dp. If Pforte wants this anchor,
  no card geometry satisfies it; only moving Movement 3
  (rejected at §4) would. **Recommendation: pin K-2's
  anchor to the sheet top in the ruling text** — under
  that anchor the amended law is true, numeric, and
  F-6-dependent, exactly as the audit intended.

In every non-granted state K-2 does not hold and is not
supposed to: all-MISSING needs 1,490.0dp of travel to
DONE's bottom (1.96 viewports) — the errand *is* the
scroll, and the sheet shortens toward its foot by exactly
the §2.1 savings as each door is granted (−94.6 / −50.6
per door, in board order of repair).

---

## §3 · F-7 — THE RESERVED KEY BAND, EXACT

### 3.1 The band, measured from the code

Both key branches of `ReceptionDoorCard` occupy the
identical envelope:

```
FIX   (MISSING):            Spacer 10 + Box 48 = 58
CHECK (STALE / UNMEASURED): Spacer 10 + Box 48 = 58
```

### 3.2 State of the tree: the reservation already stands

In the CURRENT file, S-12 has landed — UNMEASURED carries
the CHECK key in the band's seat. The band is therefore
never empty in any printing of this tree: **the reserved
height while UNMEASURED is exactly 58.0dp** (10 + 48),
occupied by the CHECK key itself, and the sweep's arrival
moves the band by **0dp in every landing**:

| Landing (from UNMEASURED) | Band Δ | Card Δ (whole) | What actually moves |
|---|---|---|---|
| → MISSING | 0 (CHECK → FIX, same 58) | +(4 + escort): D1 +72 · D2 +38 · D3 +38 · D4 +55 | the escort grows *above* the band |
| → STALE | 0 (CHECK stays) | +21 | the gloss grows above the band |
| → GRANTED, as built | −58 (band vacates) | −58 | the band itself leaves |
| → GRANTED, F-6 landed | −58 | D1 −152.6 · D2 −108.6 · D3 −152.6 · D4 −152.6 | band + job + spacers collapse to the 37 line |

That is F-7's demand — *the sweep's arrival changes words,
not layout* — satisfied for the band itself by
construction: words (FIX ↔ CHECK, the door word, the
gloss/escort voices) change; the 58dp envelope does not.
For the record, the pre-S-12 tree the audit read had
UNMEASURED at `else -> Unit` (band 0): a MISSING landing
then jumped the card by **+58 + 4 + escort (D1: +130)** —
the defect F-7 named, already dead in this tree.

### 3.3 The reservation spec (for any pre-live split)

If the builder ever splits a pre-sweep printing (snapshot
null / not-yet-live) from the live UNMEASURED printing,
the placeholder in the `when` seat is specified to the dp:

- **One `Spacer(Modifier.height(58.dp))`** — the whole
  envelope as a single block (not 10 + 48: there is no key
  to seat, and a two-part placeholder invites a 10/48
  drift). No Box, no rule, no text, no hairline inside it.
- It sits exactly where the band sits: after the job (and
  after the gloss/escort seat, which stays empty while
  UNMEASURED per S-12), before the card's closing
  Spacer 10 + hairline, which do not move.
- Resulting pre-live card = the §1.2 UNMEASURED card,
  dp-identical (101.6 + job: D1/D3/D4 189.6, D2 145.6),
  so the first sweep's arrival is a 0dp event for the
  band under §3.2's table, in both forms of the printing.

---

## §4 · KEY GEOMETRY CENSUS — FOUR KEYS, ONE GRAMMAR

| Key | Height | Dress (as built) | Label, longest lawful | Label width | Verdict |
|---|---|---|---|---|---|
| FIX (per MISSING door) | **48** | ink (F-4's dress cure is another seat's; geometry untouched by it) | `FIX — NOTIFICATION LISTENER` | 253.2 / 320 (66.8 slack) | **LAWFUL** |
| CHECK (per STALE/UNMEASURED door) | **48** | surfaceVariant | `CHECK — NOTIFICATION LISTENER` | 286.2 / 320 (33.8 slack) | **LAWFUL** |
| OPEN THE LIST | **48** | surfaceVariant | `OPEN THE LIST` | 120.6 / 320 | **LAWFUL** |
| DONE `TO THE DECK →` | **52** | ink — the sheet's one terminal key | `TO THE DECK →` | 120.7 / 320 | **LAWFUL** |

**The grammar they answer to:** 48dp is this house's row/
key module — PARK's board rows, theme row, stub row, and
PARK's own FIX key are all 48, and the sheet's three
working keys copy it exactly. 52 is not a fifth size
invented for DONE: it is the module **+4**, already in the
family (PARK's sound-sled row is 52, its one taller
furniture row), spent here on the sheet's single terminal
act — the only key that stamps, the only ink key once F-4
lands. A census of four heights {48, 48, 48, 52} with the
52 singleton at the foot is a grammar, not a scatter.
All four keys share radius 8 (`RoundedCornerShape(8.dp)`)
and full column width; no key carries a border.

**The FIX dip's 1dp, accounted:** FIX (sheet and PARK,
byte-identical pattern) carries
`.offset(y = if (fixPressed) 1.dp else 0.dp)`. Offset is
not measure: **the card's height changes by 0dp.** The
dip spends clearance, not layout — pressed, the key's
bottom edge sits 1dp deeper into the card's closing
Spacer 10 (**10 → 9dp** of air above the hairline) and
leaves **10 → 11dp** above itself. It is held for the
whole absence (a state, not a tap animation — there is no
tween on it in code) and releases when the return verdict
resolves. **Lawful:** 1dp of travel inside a 10dp gutter
can touch nothing; the hairline is never reached (9dp
remain). CHECK carries **no dip** — lawful asymmetry, per
audit §3 (F-4/F-5 micro-pass: CHECK takes FIX's knock,
*voice only, no dip*); DONE and OPEN THE LIST carry none
either — the dip belongs to departure keys alone, and
neither of those departs anywhere.

**Tightest print on the sheet (flagged for the render
check):** `CHECK — NOTIFICATION LISTENER` at 286.2dp is
the widest key label. Minimum lawful screen width for it
= 286.2 + 40 (the column's padding) = **326.2dp**. On a
320dp-wide screen (280dp column) the label must wrap
inside its 48dp box or clip — the merge render check
owns that device class; at the canonical 360dp it fits
with 16.9dp a side.

---

## §5 · PARK STUB ROW — THE SQUEEZE, MEASURED AND RULED

Row: `ParkDrawer.kt`, the (5b) reception stub — fixed
`.height(48.dp)`, inside PARK's 20dp horizontal padding →
**320dp of row** at the canonical width. Left plate
`SETUP + INTRODUCTION` (PillBadge) measures **149.2dp**.
Right word: `guardLineFor(...)?.text ?: "OPEN"` in
AppTitle (Atkinson Bold 15, ls 0, lineHeight 20).

### 5.1 The longest lawful text, and the true overflow

Per `GuardLine.kt` the right word is `GUARD 4/4`, or
`GUARD n/4 — {NAME}` with ` STALE` appended for a stale
first lack; the longest board name is NOTIFICATION
LISTENER. The audit's estimate ("~360dp in ~320dp") is
**corrected — the squeeze is worse**:

| Right word | Width | Plate + word | Overflow vs 320 |
|---|---|---|---|
| `GUARD 4/4` | 81.4 | 230.6 | fits (89.4 slack) |
| `OPEN` (null-state fallback, Glatt's G-2) | 40.0 | 189.2 | fits |
| `GUARD 0/4 — ACCESSIBILITY` (shortest lack) | 219.6 | 368.8 | **+48.8** |
| `GUARD 2/4 — NOTIFICATION LISTENER` | 290.6 | 439.8 | **+119.8** |
| `GUARD 2/4 — NOTIFICATION LISTENER STALE` (longest lawful) | **341.1** | **490.3** | **+170.3** |

The longest lawful text does not fit the row, does not
fit the *screen* (341.1 > 320 column), and only two of
five printings fit the row at all. **As built, the row
cannot print its worst case lawfully under either Compose
measurement reading:** if the right word is measured
against the remaining width (170.8 after the plate) it
wraps to **3 lines × 20 = 60dp inside a fixed 48dp row**
— vertical overflow; if it is measured unweighted-first
against the full 320 it wraps to 2 lines (STALE case) at
up to 290.6dp wide and is placed after the 149.2 plate —
horizontal overflow past the row's edge. Neither is a
clean second line. This is a defect in the current tree,
not a render-check hypothetical.

### 5.2 Ruling — the row grows; the word never shrinks

Per audit §4 F-13 (the shorten STRUCK; "the row grows a
second line; the word never shrinks") — **grow, specced
exactly:**

1. The right word takes `Modifier.weight(1f)` in place of
   the bare weight spacer (an 8dp fixed spacer keeps the
   plate/word gap: available width for the word =
   320 − 149.2 − 8 = **162.8dp**), with
   `textAlign = TextAlign.End` so wrapped lines stack as
   a right word, first line flush to the row's right edge.
2. Row `.height(48.dp)` → `.heightIn(min = 48.dp)`,
   contents centred as today. Nothing else in the row
   moves: plate, dress, tap, testTag unchanged; font stays
   AppTitle 15/20 — **no size change, no maxLines, no
   ellipsis, no truncation, ever.**
3. Resulting row heights, by printing (wrap at 162.8dp,
   measured): `GUARD 4/4` / `OPEN` → 1 line, row **48**.
   `… — ACCESSIBILITY` → 2 lines (`GUARD 0/4 —` /
   `ACCESSIBILITY`, 40dp of text), row **48** (the text
   fits inside the module — the second line is free).
   `… — NOTIFICATION LISTENER` and `… — NOTIFICATION
   LISTENER STALE` → 3 lines (`GUARD n/4 —` / `NOTIFICATION`
   / `LISTENER [STALE]`, 60dp of text), row **60 (+12)**.
   PARK's total height grows by exactly **12dp, in the
   listener-lack states only**; every other printing of
   the drawer is dp-identical to today.
4. Rejected alternative, priced: the full-width second
   line (word re-seated under the plate at full 320dp) —
   the STALE text still wraps there (341.1 > 320: 2 lines,
   40dp), giving a 12 + 6 + 40 = **58dp** row that breaks
   the row's right-word grammar to save 2dp against
   ruling 3's worst case. Rejected: the word stays a
   right word.

(The `?: "OPEN"` fallback itself is Glatt's charge G-2,
ruled by that seat's §4 — this section prices its width,
40.0dp, fits under the ruling unchanged, and takes no
position on its lawfulness.)

---

## §6 · MERGE RENDER CHECKS (geometry subset, for Ren's compile)

1. All-MISSING first open: document measures ~2,336dp;
   D1's job line holds 4 lines (its L2 has 2.0dp of slack
   — if the capture shows 5, the total is 2,358 and §0's
   knife-edge note, not the builder, is the explanation).
2. F-6 landed, all-SET: total ~1,413dp; fourth hairline
   at ~407dp; one viewport-scroll from the top shows DONE
   whole. Collapsed row reads exactly 20dp (37dp card).
3. UNMEASURED → sweep landing: key band does not move
   (58 before, 58 after); only escort/gloss growth above
   it registers.
4. PARK stub, worst case `GUARD 2/4 — NOTIFICATION LISTENER
   STALE`: under §5.2 the row stands 60dp, word whole in
   3 right-aligned lines at full size; no other PARK row
   moves.
5. `CHECK — NOTIFICATION LISTENER` on the narrowest
   device class in scope (§4 flag): no wrap inside the
   48dp box at ≥326.2dp screen width.

— Zoll (finisher), issue #14 cell
