# RASTER — Grid seat, Designers cohort (coordinator: Sana)
## The spacing system, as applied — PLAN WAVE, no code

My wave-1 margin was 24dp. It lost. The margin is **20dp**, frozen by Sarthak's
law, and this plan does not mourn it — it makes 20dp do something 24 never did:
at 20, the arithmetic below closes *exactly* on every screen, with no remainder
hiding in a `SpaceBetween`. A grid is not the number you wanted. It is the number
that divides.

**The locked scale, and nothing else.** Gaps — padding, `Spacer`, `spacedBy`,
insets, offsets — may only be:

| Token | dp | One job |
|---|---|---|
| S4 | 4 | intra-line air: two lines of one thought, brick mortar, label-to-rule |
| S8 | 8 | intra-component air: icon-to-glyph, line-to-track inside one control |
| S12 | 12 | intra-group air: header-to-content, row padding, cells in one row |
| S20 | 20 | inter-component air: blocks inside one section, panel padding |
| S32 | 32 | section break — the smallest air that may precede a section header |
| S48 | 48 | the great break: hero-to-rest, sheet-to-sheet, pre-chrome air |

Wave-1's disease was 11 `spacedBy` values and ~35 dp values. The cure is not
fewer opinions, it is a closed list: **any gap literal outside {4, 8, 12, 20, 32,
48} is a bug, and any size literal outside §4 is a bug.** 16 and 24 survive only
as *sizes* (a line box, a radius) — never again as gaps.

---

## 1. The grid — axes every screen shares

Logical width 412dp (the harness width; the system is stated in edges, so it
holds at any width by re-solving §1.1 with the same tokens).

### 1.1 Horizontal

```
MARGIN     20dp both sides, every screen, every theme, no nesting exception
CONTENT    x = 20 … 392, width 372dp
COLUMNS    4 × 84dp, gutters S12 → spans: 1-col 84 · 2-col 180 · 3-col 276 · 4-col 372
           column boxes: [20–104] [116–200] [212–296] [308–392]
```

Column arithmetic: 4×84 + 3×12 = 372. Exact. Every block width in this plan is a
column span or a §4 size; a block that cannot state its span does not ship.

### 1.2 The axes (x-coordinates — the complete list)

| Axis | x | What locks to it |
|---|---|---|
| A | 20 | margin axis: bare text, headers, hairline left ends, panel outer edges |
| B | 40 | indent axis: A + S20 — sub-steps, subtitles, **all panel-interior text** |
| N | 60 | icon-name axis: A + 28 icon + S12 — names on icon rows (Shield apps) |
| C | 20–76 | time axis: Shield routine times, width 56, starting on A |
| T | 96 | time-row title axis: C's end (76) + S20. The wave-1 56dp time column survives untouched — at margin 20 it lands titles on the same x=96 it always wanted |
| O | 206 | centre axis: dial centre, centred numerals, loadout middle stop |
| sled | 296–392 | the one sled zone: width 96, right edge on D; 296 is column 3's right edge — the sled is a column-span object, not a leftover |
| D | 392 | right axis: right edges of controls, counts, state words — edges flush here, never centres |

**The panel rule that kills the nested-margin disease:** a panel spans the full
content width (edges on A and D) and pads its interior by exactly S20, so panel
text locks to **B**. There are exactly two text axes in the app — A for bare
lists, B inside panels — and wave-1's four drifting axes (12/24/26/28) are all
dead by construction.

Hairlines run A→D, always. Never edge-to-edge, never panel-to-panel.

### 1.3 The shared start beat — every screen begins at the same y

```
y 0–48    THE BEAT (chrome, identical on every screen):
          S8 top air · 32dp beat line · S8 bottom air · hairline on A→D at y=48
          Beat line content: MONOLITH wordmark left on A, the war line
          (days-to-NEET · today/target) right-flush on D — one line, chrome
          only, no counts, no pills, no screen name (the rail names the screen)
y 48–80   S32 — the beat's own section break, part of the beat, not negotiable
y 80–100  FIRST HEADER line box 20dp: the screen's first section header,
          left on A, its one right-aligned meta right-flush on D
y 100–112 S12
y 112     FIRST CONTENT — the same y on Deck, Shield, Tasks, Ledger, Guard,
          and the running session. No screen may start higher or lower.
```

The start complex totals **112dp** (48 + 32 + 20 + 12). A screen whose first
block is a hero (Deck, Ledger, session) puts its hero's *label line* in the
80–100 header slot and its hero body at y=112, so the beat is a rhythm, not a
decoration: chrome, break, name-of-the-thing, content — four beats, same tempo,
every screen, day and night identical.

---

## 2. Vertical rhythm

### 2.1 Line boxes (the only text heights spacing recognises)

**L16** micro-labels/meta · **L20** titles, body, headers · **L48** display
lines (queue total, permission count) · **L64** hero numerals (Deck total,
Ledger hero) · **L96** session remaining — the largest box in the app, used
once, on the one screen where the number is the whole screen.

### 2.2 Row modules — two pitches run the entire app

**P64 — the primary row** (routines, apps, Guard switches, Guard sites):
`S12 top pad · Z40 content zone · S12 bottom pad`, hairline at the pitch
boundary. Z40 holds either one L20 line centred, or two lines stacked
L20 + S4 + L16 = 40 exactly, or one 40dp-or-shorter control centred.
The row itself is the 64dp touch target; nothing inside needs its own.

**P48 — the single-line row** (queue tasks, sub-steps, receipts, search):
`S8 · Z32 · S8`, hairline at the boundary. Z32 holds one L20 line centred or
one 32dp control (the standard sled is 96×32 and lives here as happily as in
Z40). 48 is the accessibility floor — a pitch may never go below it for
anything touchable, and nothing passive may waste P64.

**H32 — the group header** inside a section (state groups on Shield, day
groups on Ledger): `S8 · L16 · S8`. It is a row, not a section — it gets no
S32, ever. That distinction is the whole cadence.

### 2.3 Section cadence — one pattern, repeated

```
[ S32 break ] [ header L20 ] [ S12 ] [ content … ]
```

- First section: its S32 is the start beat's own (§1.3) — no double break.
- Between two sections: exactly S32. Not 28 by accumulation, not 32+12 by a
  header composable smuggling its own spacer. **Section headers carry zero
  built-in spacing**; the arrangement supplies the cadence, nobody else.
- Inside a section, blocks separate by S20; rows inside a list separate by
  nothing — pitch and hairline do the work (list `spacedBy` = 0, always).
- The pre-chrome break at the foot of a scroll surface is S48, then the
  scroll inset of §3.6 — content never slides under the rail and never ends
  flush against it.

Section-start module total: 32 + 20 + 12 = **64 = P64**. The section rhythm
and the row rhythm are the same rhythm. That is not a coincidence I found;
it is the design.

---

## 3. Per-screen spacing maps

Every gap below is a token. Every structural height is a §4 size. Y-coordinates
are from content top (below the system status bar).

### 3.1 Deck (non-scrolling, top-anchored stack + one named reserve)

| y | Element | Height / gaps |
|---|---|---|
| 0–48 | The Beat | §1.3 |
| 80–100 | Header slot: duration label on A, finishes-meta on D | L20 |
| 112–200 | Readout block: L16 label · S8 · L64 hero numeral (the total, its only home) | 88 |
| +S20 | — | |
| 220–508 | The dial: Ø288, centred on O (x 62–350, derived from O − 144) | 288 |
| +S20 | Block-count / cap row: keys 48 high, cells 84 wide on the column grid, S12 between | 48 |
| +S20 | NOW row: task line on A/B, right meta on D | 48 (P48 module) |
| flex | **The Reserve** — the Deck's lower field, bounded above by the NOW row and below by the slide's S20. It is a named flex region (builder: weight, no dp literal), defended empty; it is the only non-numeric vertical space in the app, and it is non-numeric *on purpose* | — |
| −S20 above rail | Slide-to-arm: full content width, height 64, thumb 48×48 at S8 inset, travel 308 (derived: 372 − S8 − 48 − S8) | 64 |

### 3.2 Shield (scrolling)

Sections in cadence §2.3. First header (ROUTINES) sits in the beat's header
slot at y80; first content y112.

- **Routine rows — P64.** Horizontal lock: times in C (20–76, two L16 lines +
  S8 inside Z40), titles on T (x96: L20 + S4 + L16 meta in Z40), mini-sled
  **56×32** right-flush on D, centred in Z40. One time axis, one title axis —
  a routine row can be laid out by a builder who has never seen the screen.
- **Loadout band — panel, height 80:** S12 top · L16 label (on B) · S8 ·
  32dp track zone (cap stops at the thirds of the content width; middle stop
  on O) · S12 bottom. Panel edges on A/D, interior on B.
- **Apps section:** search row P48 (field full width, Z32). Group headers
  H32 by state. **App rows — P64:** icon 28×28 on A centred in Z40, name on
  N (x60), sled 96×32 in the sled zone (296–392). Group-to-group inside the
  section: S20 + H32 — never a section break; states are groups, not sections.

### 3.3 Tasks (scrolling list + sticky composer foot)

- Header slot (y80): QUEUE header; its content at y112 is the queue-total
  block: L48 numeral + S4 + L16 label = 68, a block height, not a pitch.
- **Queue rows — P48**, title on A, duration/status meta right-flush on D,
  S12 minimum clearance between title end and meta (truncation eats the
  title, never the gap).
- **ACTIVE block** (the one expanded task): header built as P64 (title L20 +
  S4 + meta L16 in Z40, pads S12), sub-step rows P48 each indented to **B**,
  action row P48 (action words start on A, S20 between words, never boxed).
  Blocks in the queue separate by hairline only; ACTIVE is a tonal step,
  not a card — depth law: one tonal step + hairline, no shadow, no radius
  needed because nothing is boxed.
- **Composer — sticky footer block, height 72 collapsed** (S12 · 48 field
  row · S12), sitting directly above the rail with no gap: it *is* chrome
  while it is stuck. Field row: field + ADD key 84 wide (one column) with
  S12 between. Focused state: the key bank appears inside the same block —
  + S12 + one 48 key row = **132 expanded** — and the list's bottom inset
  grows by the same 60 (a derived number, stated once, here). Duration
  cells in the bank: 84 wide, S12 apart — the column grid, again.

### 3.4 Running session (the chrome-invariant screen)

- The Beat is unchanged (y0–48) — a session does not get a different
  beginning; its header slot (y80) carries REMAINING's label, content at 112:
  L96 remaining numeral centred on O.
- +S20 · current-task line, P48 module, title on B. +S20 · progress zone,
  height 32: hairline track 4dp thick centred in the zone, elapsed meta
  below-left on A — spacing only; what the track *means* is another seat's.
- +S32 (a true section break — controls are a new section) · FINISH key row:
  one key, full content width, 48 high.
- **Bottom chrome: the rail's 64dp block stays, its content swaps.** In
  session the rail block holds the emergency-hold bar: S8 · 48dp bar
  (full content width, radius 8) · S8. Chrome height is invariant across
  tab, session, sheet and AOD-return states — the bottom of the phone never
  jumps, so the hand's memory of "down there" is never wrong.

### 3.5 Overlays — sheets, dialogs, airlock

- **Bottom sheet:** full width (412), radius 16 top corners only, interior
  margin still 20 — sheet text locks to the same A/D as every screen; a
  sheet is a screen that arrived from below, not a new country. Top beat
  of the sheet: grab zone height 20 (S8 · 4dp grabber, 48 wide, centred on
  O · S8), then the sheet's own header in the standard slot pattern:
  S20 · L20 header · S12 · hairline. Rows inside reuse P64/P48 unchanged.
  Sticky action foot, when present: height 72 (S12 · 48 · S12) — the
  composer foot's module, reused verbatim.
- **Dialog:** width 308 (= 412 − 2×(20 margin + S32 inset) — the dialog
  respects the margin and then steps one great token further in). Interior
  padding S20 on all sides (text on its own local B-equivalent), gaps S12
  between lines, S20 before the button row, buttons 48 high.
- **Airlock / peek overlay:** sheet geometry, no choices to space: L96
  countdown zone, S20, one L20 line, S48 of air before the sheet's foot —
  the emptiness is the message and it is budgeted, not leftover.

### 3.6 Chrome — top beat, bottom rail, and the Ledger proof

- **Top:** the Beat, §1.3, 48dp, identical everywhere. Nothing else lives
  above y48 on any screen.
- **Bottom rail: height 64** (S8 · 48 cap zone · S8), flush and recessed —
  no floating pill, no horizontal inset; the rail is a full-width block
  whose *stops* obey the margin grid: **stop width 64, inter-stop gap S12,
  first stop's left edge on A.**
- **The Ledger variants — all three, one geometry.** Destinations are laid
  out start-anchored, never space-between, so the count may change without
  the rhythm changing:

  | Ledger fate | Stops | Arithmetic | Trailing reserve (to D) |
  |---|---|---|---|
  | **Keep** (tab) | 5: Deck · Shield · Tasks · Ledger · + 5th destination | 5×64 + 4×S12 = 368, + S4 to D = 372 | S4 — the rail is full, exactly |
  | **Demote** (sheet) | 4 stops | 4×64 + 3×S12 = 292 | 80dp zone right-flush on D holding the Ledger word-button (80×48, in the cap zone) — the record lives where the 5th stop would have breathed |
  | **Kill** | 4 stops | identical to Demote | the same 80dp zone holds only the armed lamp (8dp, centred in the cap zone, right-flush zone) — the rail does not re-space for an absence |

  In all three: stop width 64, gap S12, cap height 48, rail height 64,
  first stop on A. A builder implements the rail once; the Ledger decision
  changes one trailing zone's *content*, never its spacing. (The same
  start-anchored law absorbs the Guard question if it is ever dissolved:
  4 stops, reserve zone, done.)

- **Scroll inset (every scroll surface):** bottom inset = rail 64 + S48
  pre-chrome break = **112**, a derived constant stated once. Top inset 0 —
  the Beat is in-flow, not an overlay.

### 3.7 The two remaining screens, in brief (same modules, no new numbers)

- **Ledger (if kept):** header slot = hero label; hero block at y112:
  L16 + S8 + L64 = 88 (the Deck readout block, reused). Stats as one P48
  mono line row. Chart zone height 144: S12 · 96 bar field (bars 32 wide on
  a 56 pitch — 6×56 + 32 = 368 + S4 trailing, the rail's arithmetic in
  miniature) · S12 · L16 day labels · S8. Receipt rows P48. Telemetry, if
  any survives, as two 180×80 tiles (2-col span × band height) with S12
  between — the loadout band's box, reused.
- **Guard (as destination):** permission hero as a panel (edges A/D, text
  B, padding S20; count line L48). Switch rows P64, sled zone or state
  word — both right-flush on D inside the reserved 96-wide zone, so the
  spacing is identical whichever rendering wins. Site rows P64. Add-site
  row, height 48: field 276 (3-col) · S12 · ADD 84 (1-col) = 372.

---

## 4. The complete permitted SIZE list (heights & widths)

Gaps are §0's six tokens. **Sizes** — every height, width, diameter and
thickness a builder may type — are only these, each with its job:

| dp | Job(s) |
|---|---|
| 1 | hairline thickness |
| 4 | track / grabber thickness |
| 8 | armed lamp diameter · radius of keys (8dp, per freeze) |
| 12 | small glyph box (state dots, check boxes' inner mark zone) |
| 16 | L16 line box · radius of panels (16dp, per freeze) |
| 20 | L20 line box · sheet grab zone · screen margin (the frozen 20) |
| 28 | app icon image (Shield rows) |
| 32 | Z32 zone · standard control height (sleds 96×32, mini-sled 56×32) · chart bar width |
| 40 | Z40 content zone |
| 48 | touch floor · key height · stop cap height · Beat height · thumb 48×48 · grabber width · L48 line box |
| 56 | time column width · mini-sled width · chart pitch |
| 64 | P64 pitch · rail height · slide height · slide thumb width · nav stop width · L64 hero line box · section-start module |
| 68 | queue-total block (L48 + S4 + L16 — derived, named here once) |
| 72 | sticky foot block (composer / sheet actions: S12 + 48 + S12) |
| 80 | loadout band & telemetry tile height · rail trailing reserve width |
| 84 | one column |
| 88 | hero readout block (L16 + S8 + L64 — derived, named here once) |
| 96 | sled width · L96 session line box |
| 132 | composer foot, focused (72 + S12 + 48 — derived, named here once) |
| 144 | Ledger chart zone |
| 180 | two-column span |
| 276 | three-column span |
| 288 | dial diameter |
| 308 | dialog width · slide travel (two objects, one number, both derived above) |
| 372 | content width |
| 412 | full width (sheets, rail block) |

Coordinates (§1.2 axes, column edges, and the start-beat y-values 48 / 80 /
100 / 112) are the third and final legal family. **Anything else — any other
literal after `.dp` anywhere in UI code — is a defect.** The wave-1 offenders
are named so they can be hunted: gaps 1, 2, 3, 5, 6, 10, 14, 16, 18, 22, 24;
sizes 42, 44, 54, 58, 60, 74 — all dead. (Radii obey the freeze: 8 keys /
16 panels / full circles only; depth is one tonal step + hairline — spacing
adds no shadow elevation, ever, because there is no dp value for one.)

**Acceptance, no opinion needed:** grep every `.dp` literal in the UI tree.
Each must be (a) a gap token {4, 8, 12, 20, 32, 48}, (b) a §4 size, or (c) a
§1.2/§1.3 coordinate. Then measure two renders, day and night: content edges
at x=20.0 and x=392.0 in every band; first content top at y=112.0 on all
screens; Shield routine titles pixel-identical at x=96; rail stops at 64/S12
in whichever Ledger variant shipped.

---

*Raster — Grid, Designers. The margin I lost turned out to be the margin
that divides. 20 · 84 · 12 · 96 · 112: five numbers, and every screen in
this app is one machine keeping one tempo.*
