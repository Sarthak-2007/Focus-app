# DESIGN — BLUEPRINT (Zirkel, Designer seat)
## The buildable design contract for the Blueprint look crew
### Under Riss (Blueprint coordinator), who answers to Ren — Oct 29, 2026

*Sources, all read before a single value was set: plan/PRESENTATION-blueprint.md
(the constitution — where this contract and the constitution disagree, the
constitution wins and the disagreement is an OPEN item, §8);
mail/blueprint/2141__from-ren__k5-supplement-owed.md;
mail/blueprint/2202__from-salz__gap-verdict.md + plan/REFINE-salz-gap-blueprint.md;
plan/REFINE-ruth.md, "AMENDMENT — BLUEPRINT, RENDER VERDICT" (21:44 IST);
plan/REFINE-zunder.md B-3; plan/SMALL-THINGS-SPECS-PASS.md Blueprint addendum
(K-5/K-8/K-9); plan/TYPE-RAMP-TABLE.md; the lane's own code —
builds/blueprint/Focus-app/…/theme/Color.kt, Type.kt, Theme.kt,
components/BlueprintPlate.kt. Builders: Monteur, Ausbau, Einbau.
No compiling in this contract's making (stand-down + crew law).*

**Leitsatz, held:** Blueprint is a technical drawing of a day that will be
built — deep indigo line-work on a white plate by day, inverted at night;
one ink; annotation red only where the drawing corrects; Anonymous Pro
figures everywhere; Sora exactly once per plate (the title-block wordmark);
Atkinson Hyperlegible for names and sentences he writes. Never green.
Blocker, not tracker — no scores, streaks, or grades anywhere below.

**How to read this contract:** every value is exact and buildable as
written. A value marked **[CURE]** changes the shipped lane. A value
marked **[OPEN-n]** is not settled — it is routed to Riss in my closing
letter, and no builder invents past it.

---

## 1. TOKENS — the full table, day + night, night cures applied

Ported from `builds/blueprint/Focus-app/app/src/main/java/com/example/ui/theme/Color.kt`
(19 semantic tokens × 2 states). Hexes stand as shipped except where a
[CURE] says otherwise. RGB given for every token — the comment text in
§1.4 is the RGB, so a comment can never again lie about its hex.

### 1.1 DAY — the white plate

| # | Token (Kotlin name) | Hex | RGB | Seat |
|---|---|---|---|---|
| D1 | TokenColorBackground | #F4F6FC | 244·246·252 | The plate / ground |
| D2 | TokenColorSurface | #F4F6FC | 244·246·252 | Default surface — same as plate, flat |
| D3 | TokenColorSurfaceSheet | #E9EDF9 | 233·237·249 | Sheet wash — one step down (day sheets, bottom sheets) |
| D4 | TokenColorSurfaceVariant | #D9E0F2 | 217·224·242 | Recess / inset well |
| D5 | TokenColorSurfaceBright | #FFFFFF | 255·255·255 | The ONE raised sheet — pure white, loose sheets only (constitution §0 law 4) |
| D6 | TokenColorPrimary | #1D2B6B | 29·43·107 | The line ink; commit fill |
| D7 | TokenColorOnPrimary | #F4F6FC | 244·246·252 | Content on ink |
| D8 | TokenColorPrimaryContainer | #1D2B6B | 29·43·107 | THE commit fill — a solid indigo plate, nothing else |
| D9 | TokenColorOnPrimaryContainer | #FFFFFF | 255·255·255 | Content on the commit plate |
| D10 | TokenColorSecondaryContainer | #3C4D9C | 60·77·156 | Selected-pill / index-cell fill |
| D11 | TokenColorOnSecondaryContainer | #EDF0FA | 237·240·250 | Content on D10 |
| D12 | TokenColorOnSurface | #2A3768 | 42·55·104 | Primary glyph tone |
| D13 | TokenColorOnSurfaceVariant | #5B6AA6 | 91·106·166 | Secondary text / glyphs |
| D14 | TokenColorOutline | #33428C | 51·66·140 | FULL-WEIGHT outline — frames, sheet edges (§2) |
| D15 | TokenColorOutlineVariant | #7533428C | 51·66·140 @ 0x75 | Hairlines ONLY — separators, tick rule; never a frame (§2) |
| D16 | TokenBlockBarSurface | #131D47 | 19·29·71 | Session / block bar |
| D17 | TokenTextColorPrimary | #1A2450 | 26·36·80 | Body text (13.76:1 on plate, Salz-verified) |
| D18 | TokenTextColorSecondary | #5B6AA6 | 91·106·166 | Secondary text |
| D19 | TokenAlert | #C8102E | 200·16·46 | Annotation red, day (5.44:1) — corrections only |

### 1.2 NIGHT — the deep plate, WITH THE CURES

| # | Token (Kotlin name) | Hex | RGB | Seat after cures |
|---|---|---|---|---|
| N1 | NightColorBackground | #0C1533 | 12·21·51 | Night ground — **and now the plate itself [CURE-A]** |
| N2 | NightColorSurface | #0C1533 | 12·21·51 | Default surface — same as ground |
| N3 | NightColorSurfaceSheet | #1D2A58 | 29·42·88 | The ONE loose-sheet surface — stands directly on ground [CURE-A] |
| N4 | NightColorSurfaceVariant | #060C22 | 6·12·34 | Recess / inset — below ground (1.08:1; read by its closing hairline, outline-on-ground 3.17:1) |
| N5 | ~~NightColorSurfaceBright~~ | ~~#17234C~~ | 23·35·76 | **KILLED as a surface tier [CURE-A]** — see ruling below |
| N6 | NightColorPrimary | #E9EEFF | 233·238·255 | Inverted ink — line-work and commit-travel fill |
| N7 | NightColorOnPrimary | #111B42 | 17·27·66 | Content on inverted ink |
| N8 | NightColorPrimaryContainer | #E9EEFF | 233·238·255 | Commit fill while travelling/committing at night [CURE-B]; never at rest |
| N9 | NightColorOnPrimaryContainer | #16224C | 22·34·76 | Content on N8 |
| N10 | NightColorSecondaryContainer | #26356E | 38·53·110 | Selected-pill / index-cell fill (text on it 9.13:1) |
| N11 | NightColorOnSecondaryContainer | #DCE4FF | 220·228·255 | Content on N10 |
| N12 | NightColorOnSurface | #E9EEFF | 233·238·255 | Primary glyph tone (15.49:1 ink on ground) |
| N13 | NightColorOnSurfaceVariant | #8B9BD8 | 139·155·216 | Muted tier (6.64:1 off ground — no whisper tier below 5:1, constitution §8 N5) |
| N14 | NightColorOutline | #55649F | 85·100·159 | FULL-WEIGHT outline — frames, sheet edges (2.69:1 over the old raised; 3.17:1 on ground) |
| N15 | NightColorOutlineVariant | #7555649F | 85·100·159 @ 0x75 | Hairlines ONLY (1.55:1 over the old raised — a frame in this reopens Salz's N3 charge, §2) |
| N16 | NightBlockBarSurface | #05091C | 5·9·28 | Block bar (1.10:1 step; its words carry 13.81:1) |
| N17 | NightTextColorPrimary | #D9E1FF | 217·225·255 | Body text (13.81:1 on ground) |
| N18 | NightTextColorSecondary | #93A3D9 | 147·163·217 | Secondary text (5.56:1 on sheet) |
| N19 | NightAlert | #FF6B7A | 255·107·122 | Annotation red, night (6.52:1 on ground) — corrections only |
| N20 | **NightDeskClockNumeral [NEW]** | **#7D85A3** | **125·133·163** | **Desk-clock numerals ONLY [CURE-C]** — §1.3 |

#### CURE-A — the night plate step collapses (Ruth's charge, answered)

Ruth's render verdict measured the shipped night plate at #17234C,
relative luminance 0.139, over a ground that samples its filed #0C1533 —
"a glowing blue rectangle" at 03:30 and again at 20:00. Cure direction
was hers and is adopted whole: *collapse the night plate step to the
ground and let the line-work carry the sheet.*

**Ruling on #17234C (NightColorSurfaceBright): KILLED as a tier,
not demoted.** Demotion ("true-sheet use only") was the other option
and it is refused for a draughtsman's reason: a tier that still exists
will be reached for. The loose sheet already has its own token (N3,
#1D2A58); #17234C has no lawful seat left. Builders:

- Every plate, card, strip, header-zone and rail-successor surface
  that read `NightColorSurfaceBright` now reads `NightColorSurface`
  (ground, #0C1533). The plate IS the ground at night; the frame (§2)
  is what makes it a sheet.
- True loose sheets (routine editor sheet, receipt sheet, bottom
  sheets) read `NightColorSurfaceSheet` (#1D2A58) — unchanged hex.
- Theme.kt night scheme remaps: `surfaceBright = NightColorSurface`;
  `surfaceContainer = NightColorSurface`; `surfaceContainerHigh =
  NightColorSurfaceSheet` (sheets only — any non-sheet reading it is
  a bug under this contract); `tertiaryContainer` and
  `inverseSurface`-family reads of the killed tier remap to
  `NightColorSurface` / `NightColorPrimary` as they already pair.
- The token definition itself is deleted at the winner-merge with
  the fossil alias block (constitution §6.2); until deletion, no new
  call site may reference it.

**Consequence for Salz's N3:** the filed pair (sheet #1D2A58 over
raised #17234C, 1.10:1) no longer exists. The sheet now stands on
ground: **#1D2A58 over #0C1533 = 1.30:1** (Zirkel-computed from the
sRGB/WCAG formula, this seating — Salz's desk to re-verify, [OPEN-1]).
The step is no longer the filed field's shallowest, and the sheet is
still identified by its full-weight frame (§2), not by lift alone.

#### CURE-B — the slide cap rests dark at night

Ruth: on the night face the cap "rests pale" — sampled #E9EEFF,
rel-lum 0.934, "a large near-white block glowing at the bottom of the
night face." Named tokens (new semantic aliases in Color.kt, pointing
at the table above — no new hexes):

- **Rest, night: `CommitCapRestNight = NightColorBackground` (#0C1533)**
  — the cap is ground-dark, framed 1.5dp in N14 full-weight outline,
  its 1dp leading-edge marker in N7 (#111B42 is wrong on dark — the
  marker takes N17 #D9E1FF at night-rest; see §5).
- **Travel, night: `CommitCapTravelNight = NightColorPrimaryContainer`
  (#E9EEFF)** — the pale fill exists ONLY while the cap is travelling
  (drag displayed > 0), in the tap-mesh/committing states, and during
  the seal's travel home is NOT travel (a returning cap prints no
  travel — trunk law, kept). Words on the travelling cap: N9 #16224C.
- Day needs no cure and gets no new token: rest and travel are both
  D8 #1D2B6B (Ruth acquitted the day arm in full).

Full state table in §5.

#### CURE-C — desk-clock numerals at the 55% composite

**`NightDeskClockNumeral = #7D85A3`** — the exact 55% sRGB composite
of #D9E1FF over the night ground #0C1533 (Salz: channel arithmetic
checks to the rounding digit). Relative luminance **0.238**, under
Nacht's 0.25 AOD ceiling by 0.012 — the tightest landing of any filed
cure, and the conservative computation (over true black it lands
lower). This token colours desk-clock numerals and nothing else;
the clock's ground is N1; its dimension line and labels take N13 at
hairline weight, never the numeral token (one dim tier, one job).
Defect stands until the dimming is in code and captured — Nacht's
03:30 test and Linea's L05 captures remain the court (§8, [OPEN-2]).

### 1.3 Zero-green check — every value, run this seating

Rule: G < max(R,B) for every chromatic token.

- **Day: 17 of 19 pass strictly.** D5 #FFFFFF and D9 #FFFFFF are
  neutral white (255·255·255): G *equals* max(R,B) — zero green
  excess, no green cast is possible in a neutral. They are filed
  here as the neutral exception, in the open, not rounded into a
  pass. Every other day token passes strictly (highest-risk checked:
  D13/D18 #5B6AA6, 106 < 166 ✓).
- **Night: 20 of 20 pass strictly**, including N20 #7D85A3
  (133 < 163 ✓) and N19 #FF6B7A (107 < 255 ✓).
- No token in either table has G as its maximum channel except the
  two neutral whites, where all three channels tie.

### 1.4 The stale night comments — rewritten to true RGB [CURE-D]

Constitution §6.1: comments that lie about hexes are this world's
cardinal sin. The shipped `Color.kt:35–53` comments describe another
palette ("R:23, G:20, B:18", "Flips to cream", "the same orange
hue"). Builders replace the night block's comments with exactly
these (hex — true RGB — seat; no other words):

```
NightColorBackground        // #0C1533 — R:12,  G:21,  B:51  — night ground and plate (CURE-A)
NightColorSurface           // #0C1533 — R:12,  G:21,  B:51  — default surface, same as ground
NightColorSurfaceSheet      // #1D2A58 — R:29,  G:42,  B:88  — the one loose sheet, on ground
NightColorSurfaceVariant    // #060C22 — R:6,   G:12,  B:34  — recess, below ground
NightColorPrimary           // #E9EEFF — R:233, G:238, B:255 — inverted ink
NightColorOnPrimary         // #111B42 — R:17,  G:27,  B:66  — content on inverted ink
NightColorPrimaryContainer  // #E9EEFF — R:233, G:238, B:255 — commit fill, travel only (CURE-B)
NightColorOnPrimaryContainer// #16224C — R:22,  G:34,  B:76  — content on the travel fill
NightColorSecondaryContainer// #26356E — R:38,  G:53,  B:110 — selected pill / index cell
NightColorOnSecondaryContainer // #DCE4FF — R:220, G:228, B:255 — content on the pill
NightColorOnSurface         // #E9EEFF — R:233, G:238, B:255 — primary glyph tone
NightColorOnSurfaceVariant  // #8B9BD8 — R:139, G:155, B:216 — muted tier
NightColorOutline           // #55649F — R:85,  G:100, B:159 — full-weight outline
NightColorOutlineVariant    // #55649F @0x75 — R:85, G:100, B:159 — hairlines only, never a frame
NightBlockBarSurface        // #05091C — R:5,   G:9,   B:28  — block bar
NightTextColorPrimary       // #D9E1FF — R:217, G:225, B:255 — body text
NightTextColorSecondary     // #93A3D9 — R:147, G:163, B:217 — secondary text
NightAlert                  // #FF6B7A — R:255, G:107, B:122 — annotation red, night
NightDeskClockNumeral       // #7D85A3 — R:125, G:133, B:163 — desk-clock numerals only (CURE-C)
```

Day comments stand as shipped (they tell the truth) except D5/D9,
which gain the words "neutral white — the zero-green neutral
exception, §1.3 of the crew contract."

---

## 2. THE FRAME — double frame at full weight

Every plate (Deck, Shield, Tasks, Ledger, Held Door) sits in the same
frame. Geometry ports `BlueprintPlate.kt` and corrects its alphas.

| Element | Value |
|---|---|
| Outer frame inset | **7dp** from the plate edge, all four sides |
| Outer frame stroke | **1.5dp**, full-weight outline token — day D14 #33428C, night N14 #55649F, **alpha 1.00, always** |
| Inner frame inset | **11dp** (4dp clear of the outer line) |
| Inner frame stroke | **0.75dp**, **same full-weight outline token, alpha 1.00** — the twin differs from the outer line by *weight only, never by alpha* [CURE: shipped code draws it at alpha 0.55 — that dies] |
| Content safe inset | **19dp** from plate edge (inner frame + 8dp clear) |
| Corner radius | **0dp** everywhere (Theme.kt flattened scale stands; sheets 2dp max) |
| Registration marks | Centre at **27dp × 27dp** from each corner (on the frame's diagonal field); crosshair arms **9dp** each way, stroke **1dp**; ring radius **4.5dp**, stroke **1dp**; all in the full-weight outline token, alpha 1.00 [CURE: shipped code draws them in onBackground ink — they take the outline token so the whole furniture family is one tier] |
| Bottom tick rule | Along the inner frame's bottom line, inside it: first tick at inner-inset + **18dp**, spacing **18dp**; minor tick length **5dp**, every 5th tick **9dp**; stroke **0.75dp**, full-weight outline token [CURE: shipped alpha 0.60 dies — weight and length carry the tier, not transparency] |
| Title-block zone | Bottom margin, inside the frame — §3. The tick rule stops **8dp** clear of the title block's left edge; it never runs under the block. |

**Salz's condition, written as a build law:** the frame stroke —
outer, inner, sheet edges, registration marks — uses the full-weight
outline token (2.69:1 over the old raised tier; 3.17:1 on ground).
The 0x75-alpha hairline variant (D15/N15, 1.55:1 over raised) is
lawful ONLY for separators between rows and the tick rule's stroke
weight class — and even there this contract sets it at full alpha;
the variant token survives for row separators inside lists, where a
sheet's existence never depends on it. **Any frame drawn in the
variant reopens charge N3 and fails crew review on sight.** The same
law binds the editor sheet's edge and the receipt sheet's edge:
a loose sheet is identified by its full-weight edge (§1 CURE-A),
in both modes, no exceptions.

The phase-1 title line (`DWG NO. … · SCALE 1:1 · REV C` riding the
top-centre at 8sp muted) **dies** — Ruth's residue charge (1): it was
the least legible thing on its own sheet and collided with the
eyebrow and the corner mark. Its facts move into the title block
(§3), set at plate tier with room to breathe.

---

## 3. THE TITLE BLOCK — geometry, cells, and the death of band + rail

The header band **dies**. The floating rail **dies**. Neither is
replaced by a successor band: the title block at the bottom of the
plate carries the wordmark, the plate's identity, the issue stamp,
the seal's landing cell, and all navigation.

### 3.1 Geometry

- Seat: bottom of the plate, **inside the frame**, spanning the full
  content width (plate width − 2 × 19dp safe inset is NOT used — the
  block spans frame-to-frame: plate width − 2 × 11dp, its outer
  rules continuous with the inner frame's sides).
- Height: **64dp** total — Tier B (title row) **32dp** over Tier A
  (index row) **32dp**, divided by a **1dp** working rule.
- Rules: top rule **1.5dp** full-weight outline; cell dividers
  **1dp** full-weight outline; bottom and sides are the inner frame
  itself (the block is drawn *into* the frame, not pasted over it).
- Plate turns: 180ms lateral, no bounce (constitution §5).

### 3.2 Tier B — the title row (left → right)

| Cell | Width | Content | Type |
|---|---|---|---|
| Wordmark | **104dp** | `MONOLITH` | **Sora Bold, 15sp, letterSpacing +1.0sp** — Sora's one seat on the plate; it appears nowhere else on the plate, in any state |
| Plate identity | flex (remaining after fixed cells) | `PLATE 01 / DECK` | Anonymous Pro Bold, 10sp, ls +2.0sp (plate tier, §6) |
| Issue | **104dp** | `ISSUED 05:00` — **one cell, one Text, label and figure inseparable (K-8)**: the word ISSUED is never abbreviated, never demoted below the figure, never split by a divider or a line-break | Anonymous Pro Bold, 10sp, ls +1.0sp |
| Revision / seal | **120dp** | At rest: `REV C`. At the seal: **`SEALED HH:MM`** (§3.4) | Anonymous Pro Bold, 10sp, ls +1.0sp |

Plate identities: **01 DECK · 02 SHIELD · 03 TASKS · 04 LEDGER**
(the Drawer's plate name in this block is LEDGER, per the crew
brief; the constitution's register language stands inside the
plate). Scale is not printed in the block — the phase-1 `SCALE 1:1`
was furniture; a block cell prints a fact or stays structural.

### 3.3 Tier A — the plate index (navigation)

Five equal-flex cells, left → right: `01 DECK` · `02 SHIELD` ·
`03 TASKS` · `04 LEDGER` · **`PARK`** (last cell, always; PARK is a
verb in a cell, where a draughtsman parks a sheet).

- Cell type: Anonymous Pro Bold, 10sp, ls +2.0sp, centred.
- **Current plate's cell is solid-inked:** fill D10 #3C4D9C (day) /
  N10 #26356E (night); text D11 #EDF0FA / N11 #DCE4FF (9.13:1 at
  night, Salz-verified). All other cells: plate fill, text in
  glyph tone (D12 / N12). This solid is index ink at cell scale —
  it is not, and must never grow into, the Commit Plate's form
  (full-width, verb-bearing, §5). The distinction is the
  constitution's own (§1.13) and is stated here so no builder
  "unifies" them.
- PARK cell: never solid-inked as a current-plate state (PARK is an
  act, not a plate); while the park drawer is open its cell takes
  the 1.5dp full-weight inner outline, fill unchanged.
- Hit area: the full cell, 32dp tall minimum — plate tier type,
  furniture-sized target; the cell, not the word, is the button.

### 3.4 The seal stamp

At the seal, the revision cell's `REV C` is replaced in the same
cell by `SEALED HH:MM` (the witnessed seal instant, trunk fact) and
the cell's inner rule steps to **1.5dp** full-weight outline — the
stamp is a heavier rule in the same cell, not a colour: text stays
ink (D17 / N17), **never red** (red corrects; a seal witnesses),
never filled. The stamp and the Commit Plate's sealed register
(§5) print the same fact in two seats because they are two
different documents — the sheet's revision cell and the plate's
register line; a builder who "deduplicates" them has deleted the
stamp. On a new plate issue (next session / next day) the cell
returns to `REV C`.

### 3.5 The Guard line — a margin note inside the frame

The header band's second fact moves to where a draughtsman puts a
warning: **top-left inside the frame**, in the margin, above the
eyebrow's zone, left edge on the 19dp content inset.

- Form: the note's text between two **0.75dp** full-weight outline
  rules (above and below the text line), rule length = text length
  + **8dp** each side; text Anonymous Pro, 15sp, lineHeight 20sp.
- **Measured, no lack:** `GUARD 4/4` — Regular weight, glyph tone
  (D12 / N13 muted at night is NOT used; margin facts print at
  N12/N17 tier — a specification does not whisper its figures).
- **Lack:** the note steps to full ink — Bold, text-1 (D17 / N17) —
  and the lack's name takes annotation red (D19 / N19):
  `GUARD 3/4 — <LACK NAME IN RED>`. The count stays ink; only the
  name is corrected, because only the name is the correction.
- **Unmeasured:** the note prints **nothing** — no rules, no cell,
  no placeholder. Silence is the honest third state, kept exactly
  (constitution §1.1). The margin stays empty; nothing reflows
  (the eyebrow's seat is fixed, §4.1).

---

## 4. THE DIMENSION FIGURE — the Deck hero

### 4.1 Anatomy (idle / composing)

Top to bottom, all seats fixed:

1. **Eyebrow (the figure's caption):** `TOTAL` / `SESSION RUNNING` /
   `BREAK · N MIN` — Anonymous Pro Bold, 10sp, ls +2.0sp, muted tier
   (D13 / N13), centred over the figure, **6dp leader gap** between
   caption baseline and figure top. It never floats as a section
   header; it is tied to its figure.
2. **The figure:** the true span (blocks × length + breaks, priced
   in) — **Anonymous Pro Bold, 72sp, lineHeight 72sp,
   letterSpacing −1.0sp** (the lane's DialClock tier, kept; the
   trunk ramp's Sora hero does NOT apply in this world —
   constitution law 3: a drawing figures its largest number in its
   one figure alphabet). Colour: text-1 (D17 / N17).
3. **Extension lines:** two verticals, **1.5dp** full-weight outline
   token, at x = figure centre ± **144dp** (the wheel's radius —
   they drop to the wheel's diameter, 288dp). Top: they begin **4dp
   above** the figure's cap line. Bottom: they end **6dp past** the
   dimension line (a draughtsman's overshoot — the line reaches
   past what it measures, by a stated amount, never by accident).
4. **The dimension line:** horizontal, on the wheel's top tangent
   line, spanning exactly extension-line to extension-line. Stroke
   **1dp** working weight, full-weight outline token. **Arrowheads:**
   closed, filled in the outline token, **8dp** long, **3dp**
   half-height, tips touching the extension lines from inside —
   the line states, geometrically, "this figure measures that
   circle." (Ports `BlueprintDimensionRule` and promotes it: its
   0.75dp open arrowheads become 1dp with filled heads.)
5. **The wheel** below, 288dp, as the constitution's §1.4 drafted
   circle. The selected minute prints **only** in the figure —
   one fact, one rendering; the leader lines make the single
   rendering checkable against the hand.

FLOW active: the wheel's ticks sink to hairline (0.75dp, outline
variant lawful here — a scale drawn out of use), the figure and
its lines unchanged. The invisible dial-5 never prints (§6 audit
of the constitution, carried).

### 4.2 The running form — same seat, same lines

While sealed, the figure in the **same seat at the same size**
becomes the trunk's existing elapsed/remaining derivation. Nothing
moves; a running drawing is the same sheet with its figure
advancing. Progress is read off the dimension line, never
re-figured: the line's filled segment — **1.5dp** in primary ink
(D6 / N6), from the left arrowhead's tip to the elapsed fraction
point; the unfilled remainder stays 1dp outline. **The % twin is
deleted** (constitution §1.6): no percentage prints anywhere on
the plate, in any state.

### 4.3 B-3 — the broken dimension (Safe Break)

Adopted whole from Zunder (REFINE-zunder B-3), settled to values:

- While a Safe Break runs, the dimension line **breaks at its
  centre** with the conventional zigzag: break zone **24dp** wide,
  centred on the line; polyline through offsets (−12dp, 0) →
  (−6dp, −5dp) → (0, +5dp) → (+6dp, −5dp) → (+12dp, 0); stroke
  **1.5dp**, primary ink. The arrowheads and extension lines do
  not move.
- **Elapsed chain left, remaining chain right:** the filled
  progress segment (§4.2) ends at the break's left edge; the
  remaining segment resumes at the break's right edge at 1dp
  outline. The span across the break is the break — omitted from
  the main line on purpose, and the zigzag is the statement of
  that purpose.
- **Break minutes figured beneath, in [brackets]:** centred under
  the break zone, **8dp** below the dimension line:
  `[N MIN]` — Anonymous Pro Bold, **15sp**, lineHeight 20sp,
  text-1. The bracket is this world's mark for a span held off the
  main line — the same honesty grammar as the receipt's bracketed
  reconstructed figures (constitution §4.3). No other text joins
  it; the eyebrow (§4.1.1) stays the break's caption.
- At the break's end the line redraws **whole, unbroken, no
  trace** — no scar, no marker, no history on the sheet.
- The desk clock refuses the break (Nacht's law, constitution
  §4.4): B-3 exists on the Deck sheet and the Pill only, never on
  the night plate.
- Machinery: one draw branch (~25 lines) in the dimension-line
  composable, keyed to the existing Safe Break state and its
  remaining span (WallAccrual). Zero new state — Zunder's price
  stands.

---

## 5. THE COMMIT PLATE — the one solid plate

**Uniqueness law (constitution §1.10, held against my own world):**
the Commit Plate is the only full-width solid-ink control in the
app. Everything else that might wear its dress is demoted in §5.3.
The plate is the trunk slide assembly (machinery untouched: cap
84dp wide, inset 5dp, travel = width − 2×inset − cap) drawn as a
plate:

- Plate body: full content width, height **64dp**, corners **0dp**,
  frame **1.5dp** full-weight outline token. Track face = plate
  surface (D2 / N2).
- Words on the track face, centred at 54% width (trunk seat):
  `SLIDE OR TAP TO START` (rest, frozen through every drag state);
  `TAP AGAIN TO UNDO` (tap-mesh window only) — Anonymous Pro Bold,
  10sp, ls +2.0sp (plate tier). Word colour by state, table below —
  **the trunk's hardcoded LIGHT_ON_PRIMARY for these words dies**;
  words take the token of the surface they sit on [OPEN-3].

### 5.1 States — day

| State | Cap fill | Cap marker | Track words | Notes |
|---|---|---|---|---|
| Rest | **D8 #1D2B6B** (solid indigo) | D7 #F4F6FC, 1dp, leading edge | D17 ink on plate face | Day rest is already honest — Ruth acquitted it |
| Travel (drag > 0, mesh, committing) | **D8 #1D2B6B** (unchanged) | D7 | fade per trunk R5 (hand-drags only) | Trail behind cap at alpha 55, D8 — suppressed while returning |
| Sealed | — (cap home) | — | track empties to the register, §5.2 | Title block stamps simultaneously (§3.4) |

### 5.2 States — night [CURE-B applied]

| State | Cap fill | Cap marker | Track words | Notes |
|---|---|---|---|---|
| **Rest** | **`CommitCapRestNight` = N1 #0C1533 (dark)**, cap framed 1.5dp N14 | **N17 #D9E1FF**, 1dp | N17 on track face | The cap does not glow at rest. This is the cure Ruth named: "rest the slide cap dark, filling only on travel" |
| **Travel** (drag displayed > 0 · tap-mesh · committing) | **`CommitCapTravelNight` = N8 #E9EEFF (pale)** | N7 #111B42, 1dp | N17; words under the travelling cap are occluded by it, never recoloured mid-drag | Pale exists only while the hand is spending it. Trail alpha 55 in N8, suppressed while returning |
| Return home | N1 (dark) from the moment return begins | N17 | — | A returning cap prints no travel (trunk law) and no pale |
| **Sealed** | — (cap home, dark) | — | register, below | — |

**Sealed register (both modes):** the plate empties — track fill =
plate surface, frame drops to **1dp** working-weight full outline
token (a register line's rank, not a sheet's edge — §2's frame law
is not engaged), and centred in the plate: `SEALED HH:MM` —
Anonymous Pro Bold, 10sp, ls +2.0sp, text-1. A fact, witnessed, or
nothing: if no seal instant exists, the register prints nothing
and the plate returns to rest state whole.

Grace ABORT and the emergency valve keep the trunk's address
(Δy = 0), drawn as the plate's negative: hairline frame (0.75dp),
the valve's words in annotation red (D19 / N19) — the draughtsman
correcting the drawing before it is built. The refusal sentence
keeps its reserved slot under the plate, in annotation red, and
the slot is empty in the healthy state (constitution §1.11).
**Ruth residue (3) is discharged here by contract:** the Deck's
refusal line wears red in this build — shipped pixels wore ink;
that was the skin's debt, and builders do not carry it forward.

### 5.3 DONE — demoted to an outline key

Every DONE seat (NOW row, Tasks rows) is an **outline key**:
**1dp** full-weight outline border, fill = plate surface (no
fill), label in primary ink (D6 / N6), Anonymous Pro Bold 15sp
(§6). **Solid only while pressed:** during the press itself the
key fills D6 / N6 and its label takes D7 / N7; on release it
returns to outline before the tear/bookkeeping completes. DONE
never rests solid — the Commit Plate's uniqueness survives
because no other verb wears a filled body at rest. (FLOW-active
is hatched outline, constitution §1.5 — also never a solid
plate; the index cell's solid, §3.3, is cell-scale index ink,
fenced in its own section.)

---

## 6. TYPE RAMP — exact values

Six sizes only — **104 / 72 / 50 / 22 / 15 / 10** (the studio ramp;
no 8sp, no 11/12/13sp anything, anywhere — the phase-1 title line's
8sp died with it in §2). Families by law: **Anonymous Pro** for
every figure, label, title and button word in this world (weights
400/700 only — the family ships nothing else); **Atkinson
Hyperlegible** for names and sentences he writes (400/700 only);
**Sora** exactly once per plate (§3.2 wordmark). Where the trunk
ramp assigns Sora (heroes, titles, button words), Blueprint's
grammar overrides and this table governs — the divergence is the
world's sharpest claim (constitution law 3) and is deliberate.

| Role | Family | Weight | Size / LineHeight | LetterSpacing |
|---|---|---|---|---|
| Desk-clock numerals (H1, AOD only) | Anonymous Pro | Bold 700 | 104sp / 100sp | −3.0sp |
| Dimension figure (H2) | Anonymous Pro | Bold 700 | 72sp / 72sp | −1.0sp |
| Second figures (H3): Held Door remaining, overlay countdowns, queue totals | Anonymous Pro | Bold 700 | 50sp / 52sp | −1.5sp |
| Section titles (T1) | Anonymous Pro | Bold 700 | 22sp / 26sp | −0.3sp |
| Active task title (his words, promoted) | Atkinson Hyperlegible | Bold 700 | 22sp / 27sp | 0 |
| Row labels: app names, task rows (queued), routine names | Atkinson Hyperlegible | Bold 700 | 15sp / 20sp | 0 |
| Body / sentences he writes / explanatory copy | Atkinson Hyperlegible | Regular 400 | 15sp / 22sp | 0 |
| Figured meta: routine spans `05:00–08:00`, splits `7 SHUT · 3 PEEK`, counts, receipt figures | Anonymous Pro | Regular 400 (spans/meta) · Bold 700 (counts, splits) | 15sp / 20sp | 0 |
| Button words: DONE, ADD, SAVE, BACK, PEEK `N MIN` | Anonymous Pro | Bold 700 | 15sp / 20sp | +0.5sp |
| Margin notes: GUARD (§3.5), `LAST ROUTINE CHECK HH:MM`, `NEXT — …` (§7.1) | Anonymous Pro | Regular 400 base · Bold 700 when the note carries a fact he must act on (lack, stale check, next boundary) | 15sp / 20sp | +0.5sp |
| B-3 bracket figure `[N MIN]` | Anonymous Pro | Bold 700 | 15sp / 20sp | 0 |
| Receipt lines | Atkinson 400 (words) / Anonymous Pro 400 (figures) | — | 15sp / 21sp | 0 |
| **Plate tier (L1) — ALL furniture:** eyebrows/captions, title-block cells, index cells, slide words, sealed register, badge text | Anonymous Pro | Bold 700 | 10sp / 12sp | +2.0sp (ISSUED/SEALED cells +1.0sp per §3.2 — the figure must fit its cell without truncating; the label is never the part that shrinks) |
| **Wordmark (the one Sora seat)** | **Sora** | **Bold 700** | **15sp / 20sp** | **+1.0sp** |

Type.kt build notes for the lane: the shipped lane Type.kt already
carries the figure tiers in Anonymous Pro (DialClock 72/−1.0,
DeskClock 104/−3.0, PillBadge 10/+2.0 — kept as filed). Its style
*comments* lie in the same way Color.kt's did ("App names — Atkinson
700" over an Anonymous Pro style; "Button words — Sora 700" over an
Anonymous Pro style): builders rewrite the comments to name the
family the style actually sets, per this table. A comment that
names the wrong family is the same cardinal sin in a smaller font.

---

## 7. THE OWED SUPPLEMENT — Ren's three debts, answered in grammar

(Ren's letter, mail/blueprint/2141: K-5, before-end warnings, K-9;
K-8 already discharged in §3.2.)

### 7.1 K-9 — `NEXT` on the idle Deck

The day-scale cured the fact's homelessness on Shield; its seat is
the Deck. **On the idle (unarmed, not running) Deck, a margin note
in the Guard note's seat family — directly beneath the Guard note,
same rules, same inset — prints: `NEXT — SLEEP 20:00`** (the next
routine boundary, name + figured start, from the stored routine
facts). Margin-note type (§6); Bold when the boundary is within
60 minutes, Regular beyond it — weight is the promotion, never
colour. While a session runs the note does not print (the running
Deck is a gauge; its forward fact is the figure). If no routine is
enabled, nothing prints. One line, one seat, no duplicate on
Shield — Shield's scale shows the whole day; the Deck's note is
the glance.

### 7.2 K-5 — peek and break lengths as figured, settable dimensions

A drawing does not hide its tolerances in a settings list; it
figures them in the **tolerance block**. Blueprint's Shield proof
strip is already the tolerance block (constitution §1.9); the two
lengths take seats as figured fields in the Shield parts list,
directly under the LOADOUT quantity line, as two parts-list rows:

- `PEEK — N MIN` and `BREAK — N MIN` — item-numbered rows, the
  figure set in a **Figured Field** (the §2.5 editor instrument:
  tap the figure, step 1/5/15 or keypad; pitch = f(value), §6 of
  the constitution's feel law applies — the ear confirms the
  length as it confirms every dimension).
- Where the lengths are *spent*, they print as figured facts, from
  the same stored values — never as hardcoded constants: the Held
  Door's peek key reads `PEEK N MIN` with N = the set length; the
  Pill's three states figure remaining against the set length;
  the B-3 bracket figures the break's remaining from the set
  length. If the setting changes, every seat re-figures from the
  one stored fact — one fact, one rendering, updated in its seats.
- **Machinery honesty:** the storage and the honoured lease/break
  lengths are Truth Car 3 / Freya's §3 commission, not mine. This
  section settles the presentation home only. If the stored-length
  machinery has not landed when the crew builds, the rows print
  the machinery's current constants as *figured but not settable*
  (no step affordance, no keypad) — a figured constant is honest;
  a settable-looking field that stores nothing is a lie with a
  handle, and this world has already refused one of those (§2.9
  of the constitution). Flagged [OPEN-4].

### 7.3 Warnings before ends — the figure steps up before it steps off

Blueprint warns the way a drawing corrects attention: **by weight,
in the seat where the fact already lives** — no new surfaces, no
chime (the rejected chime stays rejected).

- **Peek, T-60s:** the Pill's figure steps Regular → Bold and the
  Pill gains its 1.5dp full-weight top rule (the tag is re-ruled
  for its last minute). **T-10s:** one seat knock (the Held Door
  stamp's sound, constitution §5) through the Pill's own channel.
  At the end: the existing last frame `COOLDOWN N MIN`, then
  dismissal (constitution §4.6, unchanged).
- **Break, T-60s:** the same promotion in both its seats — the
  Deck eyebrow's break figure steps to Bold full ink, the B-3
  bracket figure gains the 1.5dp rule above it, and the Pill's
  break state promotes identically. **T-10s:** one seat knock if
  the Pill is present; the Deck alone does not knock for a break
  (he may be away from the phone — a knock nobody hears is not a
  warning, and the Pill is the seat built to reach him).
- **Block / routine end:** no before-warning is designed, argued
  by name: a block's end is *witnessed* by machinery he set (the
  seal, detention, the receipt) — it is not an expiry he can be
  caught by mid-act in another app the way a peek or a break
  expires under him. The routine boundary's before-fact already
  exists in this world: §7.1's NEXT note steps to Bold inside
  60 minutes. That is the routine's warning, in grammar.
- Machinery: the promotions key off remaining-span facts that
  already exist (peek remaining, break remaining — Held Door
  baseline). The T-10 knock's channel on a notification-based
  Pill is the Pill spec's problem, named there, not hidden here
  [OPEN-5].

---

## 8. OPEN — routed to Riss, not invented past

- **[OPEN-1]** §1 CURE-A's new sheet step (1.30:1) is Zirkel-
  computed. Salz's desk re-verifies before builders treat it as
  acquitted; if it fails, the priced fallback stands (deepen the
  sheet one step, re-file the register).
- **[OPEN-2]** CURE-C is token math (0.238 < 0.25). The defect
  stands until Linea's L05 captures + Nacht's 03:30 test acquit
  the dark room. Builders implement the token; nobody claims the
  cure in a render caption until the captures exist.
- **[OPEN-3]** §5's slide words: the trunk view hardcodes its word
  colour. Re-voicing words per state (dark cap vs pale cap vs
  track face) needs the builders to confirm the view can take a
  token per state without touching slide machinery. If it cannot,
  the fallback is words on the track face only, token N17/D17,
  in every state — never a hardcoded cream.
- **[OPEN-4]** §7.2's settable lengths depend on Truth Car 3 /
  Freya §3 storage landing. Presentation home is settled either
  way; settability ships only with the stored fact.
- **[OPEN-5]** §7.3's T-10 knock presumes the Pill can sound.
  If the Pill lands notification-only, the knock becomes the
  notification's own sound and the T-60 promotion carries the
  warning alone — Riss to confirm with Pastille's seat.
- **[OPEN-6]** §3.2's ISSUED stamp samples at composition
  (constitution A6). Contract rule: sampled **once per plate
  entry** (navigation into the plate), never per recomposition —
  builders confirm an entry hook exists; if the only hook is
  composition, the stamp samples on first composition after entry
  and is keyed to the plate identity so a recomposition cannot
  silently re-issue the sheet. K-8's inseparability holds either
  way.
- **[OPEN-7]** Ruth residue (2) — the dim mid-tier index (wheel
  dimension numerals, filter tabs, muted sub-lines) was "acquitted
  as day-fit, not forgiven as craft." This contract leaves those
  tiers at their filed values; a legibility pass over the index
  tier is a finisher's job, not settled here, and not silently
  promoted — promoting index to instruction weight would break
  the hierarchy the acquittal rested on.

*Settled and signed — Zirkel, Designer seat, Blueprint crew,
Oct 29, 2026. Inbox: 0 unread at start, 0 at close (mail/zirkel/
was empty at seating — stated plainly, as ordered).*
