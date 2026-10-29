# TEMPLE — DESIGN PACK (build-ready)

*Meissel (chisel), designer, Stein's Temple crew. Oct 29, 2026.*
*Sources, in force order: plan/DEEPEN-temple.md (governs where it
kills) → plan/PRESENTATION-temple.md → the lane's actual tokens in
builds/temple/Focus-app/app/src/main/java/com/example/ui/theme/
Color.kt (values below are copied from that file, not re-derived).
Where this pack fixes a number the two specs left open, the number
is marked ◆PACK — it is a design decision of this pack, builders
build it as written, critics may attack it on the wall.*

## 0. Bindings (never re-litigate at the bench)

- **Blocker, not tracker.** No scores, totals, grades, or day-marks
  on any surface in this pack. The Drawer keeps; it never judges.
- **No DB changes, no machinery changes.** Everything here is view,
  composition, colour, and words, over machinery that already
  exists — except the four named-machinery items in §9, which are
  Ren's to rule and are NOT built from this pack.
- **No render, no cure.** Every claim in this pack is a spec, not a
  proof. The renders owed are listed in §9; nobody says "fixed"
  until the picture exists.
- **Never green.** Every colour below satisfies G < max(R,B)
  (verified against Color.kt). Do not introduce a colour that
  doesn't.
- **Fonts, only three:** Sora (display), Atkinson Hyperlegible
  (body), Anonymous Pro (telemetry/numbers). No other family, no
  synthesized weights: Atkinson and Anonymous Pro ship 400/700
  only — never request 500/600 in them.
- **Fremd test:** every printed string in §8 must be decodable by
  a tired man in 5 seconds. If a string is not in §8, it is not
  printed. Builders do not invent copy.
- **Reference frame:** 390dp wide × 844dp tall. Page margin 12dp,
  content width 366dp, universal gap 12dp (the lane's existing
  grid). Text column inset inside full-width courses is 16dp, so
  course text and stele text share one column at 28dp from the
  screen edge. On other widths, everything marked "full width"
  stretches; everything with a fixed dp size does not.
- **Corner radii (chiselled, from Theme.kt MonolithShapes):**
  2dp (slip, niches) · 3dp (small tablets) · 4dp (tablets, stones,
  rows) · 8dp (channels, pill) · 10dp (stele only). No other
  radius exists in Temple. The receipt slip's inherited 24dp and
  the AOD bar's 12dp are skeleton radii and DIE under this pack.

## 1. THE TOKEN TABLE — every Temple colour

38 token slots (19 day + 19 night) = 34 distinct colours, counting
the two brass-alpha variants and true black (§1.2). Slot-for-slot
with Color.kt; builders reference tokens by their code names,
never by raw hex, except for the §1.2 derived tokens which get
new constants as named there.

| # | Token (day / night code name) | Day | Night | Role — lawful seats and fences |
|---|---|---|---|---|
| 1 | Background / NightColorBackground | #E8DCC0 | #17100A | Window ground on every screen. Day: sunlit sandstone. Night: bronze-black room. |
| 2 | Surface / NightColorSurface | #E8DCC0 | #17100A | Default surface; identical to ground by law — Temple is flat until relief is spent (§2). |
| 3 | SurfaceSheet / NightColorSurfaceSheet | #EFE5CB | #4E3A20 | Top step of the staircase. Day: receipt slip body, Tasks tablets. Night: topmost bronze — receipt slip, keystone seat. Never a text host for the secondary tier (night 3.49:1 — §1.3 fence). |
| 4 | SurfaceVariant / NightColorSurfaceVariant (recess) | #D8C69C | #0E0906 | Carved-below: seat pits, niches, composer well, toggle niches, groove behind loadout stones, knurl ring of the dial, Held Door ground (night value, both modes — §7.7). |
| 5 | SurfaceBright / NightColorSurfaceBright (slab / raised) | #E3C184 | #392A16 | The one raised stone. Day: stele face (steps 1.26 off ground + R7 shadow). Night: stele face, seated stones, **pill body in BOTH modes** (§7.8). |
| 6 | Primary / NightColorPrimary | #2E2118 | #F3DFB2 | BRONZE, the choosing metal (§3). Day: solid bronze tablets (DONE, SHUT, toggles ON, keystone, nav-adjacent solids), control ink. Night: flips to parchment — it is the *light* on primary surfaces, not a fill colour; night bronze fills come from slot 10. |
| 7 | OnPrimary / NightColorOnPrimary | #F7ECD2 | #241A08 | Content on slot 6. Day: words on bronze tablets. Night: ink on parchment. |
| 8 | PrimaryContainer / NightColorPrimaryContainer | #BC8F26 | #DFAF3F | **GOLD LEAF — the commit metal (§3). The only accent.** Day #BC8F26, night struck brighter #DFAF3F. Seats: bolt grip, START tablet, frieze NOW marker, lamp rake. Nowhere else. Never carries words on ground (day 2.17:1). |
| 9 | OnPrimaryContainer / NightColorOnPrimaryContainer | #2A1C04 | #2E2003 | Deep umber — the only words gold ever carries (on the gold itself, full contrast). |
| 10 | SecondaryContainer / NightColorSecondaryContainer | #43301B | #4E3A20 | Dark bronze cap: colonnade (bottom rail) selected cap both modes; night keystone fill; night PEEK tablet on the Held Door. |
| 11 | OnSecondaryContainer / NightColorOnSecondaryContainer | #F2E4C2 | #F3DFB2 | Words on slot 10 caps. |
| 12 | OnSurface / NightColorOnSurface (glyph tier) | #4A3A24 | #EFE0BE | Primary icons/glyphs; the quiet tier that MAY carry words on sheets (day on sheet ≈ high, night 8.24:1 on sheet). Receipt quiet line, frieze NOW label at night, pill small line. |
| 13 | OnSurfaceVariant / NightColorOnSurfaceVariant (muted) | #6B573C | #9A7E57 | Muted glyphs/decoration. Night: 4.93:1 on ground (may label), 3.62:1 on raised — DECORATIVE ONLY on raised, never words. |
| 14 | Outline / NightColorOutline (brass, solid) | #8A6D3B | #A37F35 | BRASS the line metal (§3): strong hairlines, double rules, frieze rule, receipt head rule (2dp), desk-clock stand, seal-adjacent rules, tablet borders where specified. Line duty only — never text (day 3.56:1 on ground). |
| 15 | OutlineVariant / NightColorOutlineVariant | #8A6D3B @ 0x75 | #A37F35 @ 0x75 | Brass hairline at 46% alpha (0x75 = 117/255). The default 1dp hairline everywhere a rule is drawn at hairline weight. |
| 16 | BlockBarSurface / NightBlockBarSurface | #241A10 | #0E0906 | Black-bronze: the bolt channel, the block/session bar. The darkest warm solid; also the day shadow colour's base (§2.3). |
| 17 | TextColorPrimary / NightTextColorPrimary | #2B2013 | #E7D5AB | THE ink. All body text, hero inscription, fact rows, course values. Day on ground 11.70:1 family; night on ground 13.01:1. |
| 18 | TextColorSecondary / NightTextColorSecondary | #6E5A3A | #A98F63 | Secondary words. Lawful seats: ground (night 6.10:1, day 4.84:1) and recess (night 6.41:1). FENCED OFF sheets in both modes (§1.3) and off night raised for words. |
| 19 | Alert / NightAlert (vermilion) | #D63E06 | #FF8A5C | The temple pigment. **Day value #D63E06 is THE vermilion in both modes** for every seal stroke and filled wound in this pack (§3.3). NightAlert #FF8A5C is a token definition ONLY: nothing renders it (the lane's night scheme renders error via slot 8, per Theme.kt) — builders must not introduce it. |

### 1.1 The staircase (the only depth order)

- **Day (luminance):** slab #E3C184 (0.56) → recess #D8C69C (0.58)
  → ground #E8DCC0 (0.72) → sheet #EFE5CB (0.79). Note the slab
  sits *below* ground in luminance by design: a bronze-stone
  tablet on sand, separated by its cast shadow, not by lightness.
- **Night (luminance):** recess #0E0906 → ground #17100A → raised
  #392A16 → sheet #4E3A20. Clean climb: ground→raised 1.36:1,
  raised→sheet 1.29:1.

### 1.2 Derived tokens (new constants; add to Color.kt as named)

| Constant | Value | Use |
|---|---|---|
| `TempleTrueBlack` | #000000 | AOD desk-clock ground only (OLED off). Also the night joint-shadow base. |
| `TempleChiselDarkDay` | #2B2013 (= slot 17) at tier alpha (§2.2) | Dark edge of every day incision. |
| `TempleChiselDarkNight` | #0E0906 (= night recess) @ 100%; on a recess host, `TempleTrueBlack` | Dark edge of every night incision. |
| `TempleChiselLight` | per host — table in §2.2 | Light edge of every incision. |
| `TempleJointShadowDay` | #241A10 @ 40% | Joint shadow lines + R2 shadow colour, day. |
| `TempleJointShadowNight` | #000000 @ 60% | Joint shadow lines + R2 shadow colour, night. |
| `TempleSteleShadowDay` | #241A10 @ 45% (spot+ambient) | R7 stele shadow colour, day. |
| `TempleSteleShadowNight` | #000000 @ 70% (spot+ambient) | R7 stele shadow colour, night. |
| `TempleAodInk` | #E7D5AB @ 55% alpha, composited over `TempleTrueBlack` | Desk-clock numerals, lines, stand hour. Measured 0.201 relative luminance vs the 0.25 ceiling. Never raise the alpha. |
| `TempleLampRake` | #DFAF3F, gradient alpha 0% → 18% → 0% | The seal-moment lamp rake (§6). Night value in both modes — a lamp is a lamp. |

### 1.3 Contrast fences (from PRESENTATION §10 — builders obey, no re-measuring at the bench)

- Night recess→ground is 1.05:1: recesses are read by their brass
  hairline (5.06:1 on ground), never by the tone step. Every
  recess in §7 is closed by a hairline or a chisel pair. No
  exceptions.
- Night secondary text on sheet = 3.49:1 = UNLAWFUL for words.
  Sheets (receipt, night Tasks tablets use raised, not sheet —
  §7.3) carry slot 17 and slot 12 words only.
- Night muted (slot 13) on raised = 3.62:1: decoration only.
- Day gold on ground = 2.17:1: gold never carries words except
  umber-on-gold (slot 9 on slot 8).
- Brass (slot 14) is line duty in both modes, never text.


## 2. THE CHISEL PAIR + THE THREE RELIEF DEPTHS

Relief is Temple's legibility system. Tone difference is never
asked to do shape's job again (that was Ruth's condition 2).

### 2.1 The chisel pair — exact drawing rule

An **incision** is any mark cut INTO a surface: ring ticks, the
ring annulus edges, seat pits, course joints, frieze ticks and
notches, the composer well's border, niche inner edges, toggle
niches, the pill's ✕ square. Every incision is drawn in TWO
passes, in this order:

1. **Light pass.** The mark's path, translated by the light
   offset, stroked at exactly **1dp**, colour `TempleChiselLight`
   (host table, §2.2) at 100%.
2. **Dark pass.** The mark's path at its nominal position,
   stroked at the mark's own tier width (§2.2), colour
   `TempleChiselDark*` at the tier alpha.

Light offsets (light falls from directly above; the lower/right
inner wall of a cut catches it):

- Horizontal mark: light pass translated **(0, +1dp)**.
- Vertical mark: light pass translated **(+1dp, 0)**.
- Circular mark (ring annulus): light pass = same circle
  translated **(0, +1dp)**.
- Radial tick (dial): light pass = the tick's two endpoints each
  shifted **1dp tangentially, clockwise**, where the tangential
  unit at angle a (canvas coords, point = centre + (cos a,
  sin a)·r) is **(−sin a, cos a)**. Endpoints:
  `p_light = p + (−sin a, cos a) · 1dp`.

Total footprint of any paired mark = tier width + 1dp. Layout
reserves the footprint; the nominal position is the DARK pass's
position, so pairing never moves existing layout by more than
the +1dp light spill, which is always inside the host's padding
as specced in §7.

**Not incisions** (never paired): brass hairlines and rules
(metal inlay, single pass, slot 14/15), gold elements, text,
filled shapes, the vermilion seal. If it is metal laid ON the
stone, it is one pass; if it is a cut INTO the stone, it is two.

### 2.2 Tier widths, dark-edge alphas, light-edge hosts

Dark-pass stroke widths (from the lane's dial geometry, now law
for all incisions of that class):

| Mark class | Dark width | Day dark alpha | Night dark alpha |
|---|---|---|---|
| Major tick / notch edge | 1.8dp | 100% | 100% |
| Course joint dark pass | 1.0dp (per §2.3 R1 — joints are their own class) | 100% | 100% |
| Mid tick | 1.3dp | 100% | 100% |
| Minor tick | 1.0dp | 85% | 100% |
| Seat pit / unfilled well edge | 1.0dp | 45% | 100% |
| Frieze hour tick | 1.0dp | 85% | 100% |

(Day alphas are PRESENTATION §1.4(a)'s cured depths: mids took
full ink, minors 85%, unfilled wells 45%. Night is uniform: the
lamplight rake reads the pair, not the alpha.)

Light edge by HOST surface (the next luminous step above the
host; this table, not a formula, is the law):

| Host | Day light edge | Night light edge |
|---|---|---|
| Recess (slot 4) | Ground #E8DCC0 | Ground #17100A |
| Ground (slot 1/2) | Sheet #EFE5CB | Raised #392A16 |
| Slab / raised (slot 5) | Sheet #EFE5CB | Sheet #4E3A20 |
| Sheet (slot 3) | **none — dark pass only** | **none — dark pass only** |

Sheet is the top step: nothing lighter exists in the world, so
sheet incisions are dark-pass only AND always accompanied by an
R1 joint shadow (§2.3) on their lower edge. This is the one
lawful single-pass incision.

### 2.3 The three relief depths — R7 / R2 / R1, and no others

Depth is rationed like gold: exactly three sizes exist.

- **R7 — THE STELE CASTS (7dp).** `Modifier.shadow(7.dp, steleShape)`
  with shadow colours `TempleSteleShadowDay/Night` (§1.2), shape =
  RoundedCornerShape(10dp). Lawful hosts: the Deck stele (§7.1),
  a routine plaque WHILE it is being edited (§7.2), nothing else.
  A standing stone casts; furniture does not.
- **R2 — A SEATED STONE CASTS SMALL (2dp).**
  `Modifier.shadow(2.dp, RoundedCornerShape(4dp))`, colours
  `TempleJointShadowDay/Night`. Lawful hosts: seated set stones,
  the chosen loadout stone, Tasks tablets, the receipt slip, the
  pill tablet, ON toggle tablets get NO shadow (seated flush —
  shadow's absence is the ON read; see §7.2).
  **The seat construction (used by set stones, loadout stones,
  toggle tablets):** pit = host rect filled with recess (slot 4);
  chisel pair drawn on the pit's top and left inner edges; the
  stone is the pit rect **inset 2dp on all four sides** — the 2dp
  recess ring showing around the stone IS the shadow gap, and the
  stone additionally casts its R2. An unseated seat is the pit
  alone: recess fill + pair, no stone, no shadow.
- **R1 — A JOINT IS A SHADOW LINE (1dp).** A 1dp line, colour
  `TempleJointShadowDay/Night`, drawn directly beneath a course
  band's bottom edge, full course width, PLUS the chisel pair
  applied to the joint itself (dark 1dp at the band's bottom
  edge, light 1dp below the shadow line — total joint footprint
  3dp ◆PACK, reserved inside the course spacing in §7; courses
  are specced joint-to-joint so the footprint never eats text).
  Every course boundary, step edge, and plaque separator in §7
  is an R1 joint. There are no other dividers in Temple: no
  Material dividers, no alpha-grey lines.

**Steps (the threshold, §7.1):** each step is a band whose lower
edge carries an R1 joint; step fills descend the staircase so
the eye reads three treads, not three stripes.

## 3. THE METAL LAW — applied per element

Three metals, three jobs. **Brass lines. Bronze chooses. Gold
commits.** Vermilion is not a metal; it is the pigment (§3.3).

### 3.1 Brass (slot 14 solid / slot 15 hairline) — every line

Lintel hairline · stele double rule (1dp + 2dp gap + 1dp, slot
15) · the frieze RULE itself (the ticks that cross it are
incisions, §2 — the rule is inlay, the ticks are cuts) ·
receipt head rule (2dp, slot 14 solid — the one 2dp brass) ·
desk-clock stand · Held Door double rules · pill edge (1dp,
night brass #A37F35, both modes). Brass never fills an area
and never forms a letter. (Colonnade stop joints and the
keystone groove's edges are NOT brass: they are R1 joints and
chisel pairs respectively — shadow and cut, not inlay.)

### 3.2 Bronze (slots 6/10 solids) — every choice and state

| Element | Bronze form |
|---|---|
| Gate bolt body | Slot 16 channel; the bolt BAR is slot 6 day / slot 10 night, drawn inside the channel behind the gold grip |
| Keystone (Shield loadout) | Slot 6 day / slot 10 night fill, slot 11 words |
| Loadout stones (unchosen resting, chosen seated) | Stone, not metal — fills per §7.2; the CHOOSING is done by the keystone above them |
| DONE — TEAR tablet (Deck + Tasks) | Slot 6 fill, slot 7 words |
| SHUT tablet (Shield app gates) | Slot 6 fill, slot 7 words |
| PEEK chip (Shield) | Slot 6 outline 1.5dp, slot 17 words — a bronze frame, not a fill |
| Toggle tablet ON | Slot 6 fill, slot 7 words, seated flush |
| Toggle tablet OFF | Slot 5 fill, slot 17 words, resting loose (+2dp y, R1 only) |
| "+ MIN" extension tablet | Slot 6 outline 1.5dp, slot 17 words |
| END BREAK tablet | Slot 6 fill, slot 7 words |
| BREAK tablet (Deck base strip) | Slot 6 outline 1.5dp, slot 17 words |
| ADD ROUTINE / FIX tablets | Slot 6 fill, slot 7 words |
| Colonnade selected cap | Slot 10 fill, slot 11 words (the rail's existing law) |
| Held Door PEEK tablet | Slot 10 fill + 1dp night-brass edge, slot 17-night words |
| Composer ADD tablet | Slot 6 fill, slot 7 words |

### 3.3 Gold (slot 8) — commits only, the complete census

Exactly four seats. A fifth gold pixel is a bug:

1. **The bolt grip** (Deck threshold): the cap, 84×58dp (§7.1),
   slot 9 umber words/marks on it.
2. **START** tablet (Tasks, when a task is picked up).
3. **The frieze NOW marker** (Deck, Shield, Held Door miniature):
   a solid bar 3dp wide × 14dp high. Its LABEL is always ink —
   gold marks, it never speaks.
4. **The lamp rake** at the seal moment (§6): light, not paint —
   gradient over the hero's strokes, once, 450ms.

The slide instruction words ("SLIDE OR TAP TO START") print on
the channel in slot 7 — they are the channel's inscription, not
the cap's, and take no gold.

### 3.4 Vermilion (#D63E06, both modes) — outline = seal, filled = wounds

**Outline (stroke only, never fill, never text):** the seal.
Stroke 1.5dp on small seals (14×14dp), 2dp on the Held Door seal
(72×72dp). Complete census of lawful seats:

- Witness course, Deck — beside HELD, only when proof is
  positive (§7.1).
- Shield course heads — one per course, at the head's left (§7.2).
- Receipt № — beside the number (§7.6). A reconstructed finish's
  seal is drawn in BRASS outline instead (unstruck); a broken
  finish's seal is the vermilion outline STRUCK by one 1dp brass
  hairline across its centre.
- Held Door — the great seal across the app name (§7.7).

**Filled (solid #D63E06):** wounds only. Complete census:

- Confession square on the Deck: 10×10dp filled square before
  the broken-day inscription (§7.1, state D).
- Broken niche marker in the Drawer: 8×8dp filled square (§7.4).
- True machinery alerts (error states owned by the machinery's
  own surfaces — this pack designs none).

Filled vermilion appears NOWHERE else: not on the blocking-off
sentence (a lack is a fact), not on the peek cooldown (a fact),
not on MISSING guard rows (a fact — they take a bronze FIX
tablet), not as a badge, dot, or underline anywhere.

## 4. TYPE — the complete style table

Existing styles are from Type.kt; ◆PACK styles are new and are
added to Type.kt with exactly these values.

| Style | Font / weight | Size / line | Tracking | Used for |
|---|---|---|---|---|
| DeskClockTextStyle (exists) | Sora 700 | 104 / 100sp | −3.0sp | AOD numerals only |
| DialClockTextStyle (exists) | Sora 700 | 72 / 72sp | −2.0sp | Hero total, Deck only |
| DrawerHeaderTextStyle (exists) | Sora 700 | 22 / 26sp | +0.4sp | Screen titles (SHIELD / TASKS / DRAWER) |
| TaskActiveTitleTextStyle (exists) | Atkinson 700 | 22 / 27sp | 0 | (existing seats only) |
| PrimaryButtonTextStyle (exists) | Sora 700 | 15 / 20sp | +0.5sp | Tablet words on bronze/gold (DONE, SHUT, START, END BREAK, RETURN, PEEK tablet) |
| AppTitleTextStyle (exists) | Atkinson 700 | 15 / 20sp | 0 | Row titles: app names, task names, plaque names, niche names |
| BodyTextStyle (exists) | Atkinson 400 | 15 / 22sp | 0 | Body copy, dedication task line |
| PillBadgeTextStyle (exists) | Anonymous 700 | 10 / 12sp | +2.4sp | Eyebrows, badges, course labels, state words |
| ◆PACK LintelWord | Sora 700 | 15 / 18sp | +0.5sp | "MONOLITH" on the lintel |
| ◆PACK Mono15 | Anonymous 400 | 15 / 20sp | 0 | Lintel hour, course values, plaque spans, pill SESSION/BREAK times at word size |
| ◆PACK Mono15Bold | Anonymous 700 | 15 / 20sp | 0 | Receipt fact values, Held Door fact lines |
| ◆PACK PillBig | Anonymous 700 | 22 / 26sp | 0 | Pill big line ("41 MIN", "1:47") |
| ◆PACK SealName | Anonymous 700 | 22 / 26sp | +2.4sp | Held Door app name |
| ◆PACK QuietLine | Anonymous 400 | 9 / 12sp | +0.25sp | Receipt quiet line ONLY (measured fit: the 62-char sentence ≈326dp at this setting; slip inner width is 302dp ◆PACK-checked §7.6 — set at 9sp it wraps to two lines max, centred, and that is lawful) |

No other sizes exist. In particular: no 12sp, no 13sp, no 11sp —
the lane's inherited off-ladder sizes in ReceiptSheet die.


## 5. SHARED COMPONENTS — built once, used at every home

### 5.1 The lintel (every screen)

Height **44dp** ◆PACK, full width, ground shows through (no fill).
Row 1 (18dp): left "MONOLITH" (LintelWord, slot 17); right the
living hour "16:04" (Mono15, slot 12). Row 2 (14dp): the screen's
standing sentence in PillBadge style, slot 18 — on the Deck it is
the Guard sentence (§8 #1); on Shield/Tasks/Drawer it is the
screen's own count line (§7). Closed beneath by a 1dp brass
hairline (slot 15), then a 12dp gap. The lintel carries no seal,
no gold, no bronze fill.

### 5.2 The frieze (Deck, Shield, Held Door miniature)

One component, three sizes. Construction (full size):

- Band height **44dp** with the NEXT line, **36dp** without.
- The rule: 1dp brass (slot 14), horizontal, at y = 17dp from
  band top, spanning the text column (x = 28dp → 362dp at
  reference; i.e. course inset, not screen edge).
- Scale: 03:00 → 21:00 linear. x(t) = x0 + (t − 03:00)/18h × W.
- Hour ticks: at each hour, a 4dp vertical tick centred on the
  rule, chisel pair (frieze tick tier, §2.2). End labels "03" and
  "21" in PillBadge style, slot 18, vertically centred on the
  rule, OUTSIDE the span ends in the margin ("03" right-aligned
  ending 4dp before the rule's start; "21" starting 4dp after
  its end) — never below the rule, where the NEXT line lives.
  Miniature (§7.7): the labels move below the rule's ends
  (y 22–34 of its 36dp), the only difference.
- Routine spans: filled notches — rects **6dp high** centred on
  the rule, from span start to span end; fill slot 6 day /
  slot 3 night; top and bottom edges get the chisel pair's dark
  pass only (they are bars ON the rule, read by their edges).
- NOW marker: gold bar 3 × 14dp centred on the rule at x(now),
  §3.3 seat 3. Label "NOW 16:04" (PillBadge, slot 17 day /
  slot 12 night) centred on the marker, ABOVE the rule (baseline
  at y = 13dp), horizontally clamped inside the band.
- NEXT line (Deck only, idle only): y = 30–42dp, left-aligned at
  the text column: "NEXT — SLEEP 20:00" (PillBadge, slot 18).
  The line's space is RESERVED in all states (blank when a block
  runs) — the band never changes height.
- Miniature (Held Door): width 280dp, height 36dp, same
  construction scaled to its width, no NEXT line, NOW label
  included. Colours: night set in both modes (§7.7).

### 5.3 The seal (one drawing, two sizes, four homes)

A square outline, centred stroke: small 14×14dp @ 1.5dp;
door 72×72dp @ 2dp. Corners are square (0dp radius) — a seal
stone is square; chiselled radii do not apply to it. Drawn with
`drawRect` stroke, colour #D63E06 (or brass slot 14 for the
unstruck/reconstructed variant, §3.4). No inner glyph, ever.

### 5.4 The bronze tablet (buttons in this world)

Height 32dp unless §7 says otherwise; radius 3dp; fill/border
per §3.2; the word style is named at each seat in §7 (default
PrimaryButtonTextStyle 15sp; PillBadge 10sp where §7 names it —
DONE — TEAR and the chips speak in the badge voice). Minimum hit area
48×48dp regardless of drawn size (pad the touch target, never
the drawing). Press: dip ≤1dp travel, answer on DOWN within
90ms (machinery law, restated so no builder "improves" it).

## 6. FEEL — the numbers builders set

(Adopted from PRESENTATION §9 / DEEPEN §9; only buildable values
are listed. The seal's VOICE is bit-identical across worlds —
Temple sets no sound parameter at the seal instant, ever.)

- **Curves:** default Curve C (0.3, 0, 0.8, 0.15) at 520ms —
  the slowest house. Tab transitions and the colonnade use the
  existing MonolithKin values unchanged.
- **Wheel:** the shipped integrator and ζ=0.38 coast stand;
  detents thinned by class at call sites — majors sound, fine
  tier is finger-only.
- **Lamp rake (seal moment, t=0):** one gradient sweep,
  `TempleLampRake`, left→right across the stele's hero strokes
  and the gold grip, masked to those strokes, **450ms, once**,
  then absolute stillness. View only, on the existing t=0 hook.
- **Knocks (call-site parameters, existing voice family):**
  seat/arm knock: pitch 160–220Hz, 24–32ms, full amplitude.
  Stone seat (hub tap lays a stone): the seat knock, once per
  stone. Break boundaries: one low knock per flip. Routine
  boundary: one low knock 60s BEFORE the boundary. Peek at 30s
  left: one low knock from the pill. Session end with the app
  backgrounded: one low knock as the pill dies. Held Door
  arrival: one low knock at its floor. **03:30 SLEEP stand-down:
  NO sound, by design** — the desk clock's face changing is the
  signal.
- **Receipt arrival:** translateY from −8dp to 0, Curve C,
  520ms, once. No overshoot, no flourish.
- **Idle:** silence, stillness. No ambient anything.

## 7. PER-SURFACE COMPOSITIONS

### 7.1 THE DECK — the stele front

Vertical column, page margin 12dp, `verticalScroll` (on the
390×844 reference the whole front fits without scrolling —
budget below; smaller viewports scroll, the colonnade stays
pinned at the foot by the shell, as today). Top → bottom:

**Budget (reference):** lintel 44 + gap 12 + stele 360 +
courses 4×32 = 128 + dedication 40 + frieze 44 + threshold 96
+ gaps 3×12 = 36 → **760dp** ≤ 768dp available above the
colonnade. The three 12dp gaps sit exactly: under the lintel,
between dedication and frieze, between frieze and threshold.
Everything else is adjacent — courses hang off the stele,
dedication hangs off the courses (its R1 joint is the seam).
If a state's content exceeds its course, the course
grows and the front scrolls; courses never shrink text.

1. **LINTEL** — §5.1. Row 2 = Guard sentence (§8 #1).
2. **THE STELE** — full content width (366dp), height **360dp**,
   radius 10dp, fill slot 5, R7 shadow. Inside: padding 16dp H,
   16dp top; the parts sum to the 360dp exactly (16 pad + 12
   eyebrow + 4 + 72 hero + 8 + 4 rule + 12 + 204 ring + 28 base
   strip):
   - Eyebrow row (12dp): "TOTAL" — or "FLOW" in stopwatch mode —
     PillBadge, slot 12, left-aligned.
   - Gap 4dp. **Hero total**: DialClockTextStyle 72sp, slot 17.
     Protected: size, weight, and position do not change.
   - Gap 8dp. **Double rule**: slot 15, 1dp + 2dp + 1dp, full
     inner width.
   - Gap 12dp. **THE RING**: 204×204dp ◆PACK, centred. The
     shipped wheel instrument in an incised annulus: outer
     annulus edges are circular incisions (§2.1); the 120-tooth
     knurl takes the recess tone (slot 4) — the stone's cut
     edge. Ticks (from lane geometry, now law): major length
     10dp @ 1.8dp wide, mid 7.5dp @ 1.3dp, minor 5dp @ 1.0dp,
     all chisel-paired (§2.2 tiers), each drawn between
     r = outerR − 11dp and r = outerR − 21dp (major) /
     − 18.5dp (mid) / − 16dp (minor). Numerals: Anonymous 400 15sp,
     slot 17, at the mid radius. Hub: Ø76dp centred (the
     machinery's hub; press cycles block count, 90ms answer);
     slider dot r14dp in slot 6 with its 6dp dimple (existing
     geometry stands).
   - **BASE STRIP** (the stele's foot, inside the stele, bottom):
     height 28dp, full inner width, closed above by an R1 joint.
     Left: **THE COURSE OF SET STONES** — 8 seat pits in one
     row, two tetrads of four: pit 22×22dp ◆PACK, tetrad-internal
     gap 4dp, inter-tetrad gap 12dp (row width = 8×22 + 6×4 +
     12 = **212dp**, left-aligned at the inner padding).
     Seated stone: 18×18dp, radius 4dp, fill ground
     (slot 1) day / sheet (slot 3) night, R2, inset 2dp in its
     pit (§2.3). Unseated: pit alone (recess + pair @ 45% day).
     Right: **BREAK tablet** (§3.2), height 24dp, width **112dp**
     ◆PACK, right-aligned — 212 + 10 gap + 112 = 334 = the inner
     width, exactly. PillBadge words "BREAK {n} MIN" — at the
     setting's maximum ("BREAK 60 MIN", 12 glyphs ≈ 101dp) the
     words still clear the tablet; the value is the setting
     (§8 #9); tap opens typed minute entry.
3. **THE COURSES** — directly beneath the stele, no gap (they
   are its courses). Each: height 32dp, full width, fill =
   ground (slot 1/2) — the wall continues at ground tone;
   separated by R1 joints (§2.3, footprint inside the 32dp:
   text zone 29dp + joint 3dp). Layout per course: label
   PillBadge slot 18 at the text column (x=28dp); value
   Mono15 slot 17, right-aligned at x=362dp.
   - C1: label "BLOCK" · value: "RUNNING" / "OFF" / "BREAK".
   - C2: label "APPS" · value "{s} SHUT · {p} PEEK" (the honest
     split; OPEN apps are not counted here).
   - C3: label "ENDS" · value "16:27" while running; "—" idle.
   - C4 **THE WITNESS COURSE**: label "BIND" · value "HELD" /
     "NOT HELD"; when proof is positive, the small seal (§5.3)
     is struck at the right, 8dp left of the value, and the
     value reads "HELD" — seal + word, never seal instead of
     word.
   - **State D — THE CONFESSION** (replaces C1–C4's content on a
     broken day; the courses keep their heights): C1 prints the
     filled vermilion square 10×10dp at the text column,
     followed 8dp later by the fact in Mono15Bold slot 17:
     "DAY BROKEN — SLEEP DID NOT START · 20:00" (§8 #12). The
     stele's double rule is drawn with a 6dp gap in its INNER
     line, centred above C1. Nothing else on the Deck changes.
     No vermilion anywhere else in this state.
4. **THE DEDICATION LINE** — height 40dp, full width, ground
   fill, R1 joint above it. Left at text column: "NOW" PillBadge
   slot 18, then the current task name (BodyTextStyle slot 17,
   one line, ellipsis), then "· QUEUE {n}" (BodyTextStyle,
   slot 18) when n > 0. Right: **DONE — TEAR** bronze tablet
   104×28dp ◆PACK, PillBadge words (§8 #5). While a task runs,
   a **"+ MIN"** bronze-outline tablet 56×28dp sits 8dp left of
   DONE (§8 #10). In a break, this course prints instead:
   "BREAK" PillBadge + "· {n} MIN" Mono15 (§8 #11) and carries
   no tablets.
5. **THE FRIEZE COURSE** — §5.2 full size with NEXT line.
6. **THE THRESHOLD** — three steps + the bolt course:
   - Step bands: heights 8dp each; widths: step 1 full 366dp,
     step 2 inset 12dp each side (342dp), step 3 inset 24dp
     each side (318dp), centred. Fills descend the staircase:
     day slot 5 → slot 1 → slot 4; night slot 3 → slot 5 →
     slot 4. Each step's lower edge carries an R1 joint.
   - **Bolt course**: height 72dp, width = step 3's width
     (318dp), centred, sitting on step 3. The channel: height
     58dp, vertically centred, radius 8dp, fill slot 16, inner
     chisel pair on its top edge (host = ground: light edge per
     §2.2). The bolt bar (slot 6 day / slot 10 night) is drawn
     inside the channel from its left inset 8dp to the grip.
     **The grip (cap): 84×58dp**, fill slot 8 gold, radius 8dp,
     slot 9 umber 1dp marker line at its leading edge inset
     11dp (existing geometry). Channel inscription "SLIDE OR
     TAP TO START" (PillBadge, slot 7) centred in the channel's
     remaining width. Bolt travel at reference = 318 − 16 −
     84 = **218dp**. Slide or tap commits (his verdict; the
     proving cascade is machinery and untouched).
   - **Break state:** the bolt stands drawn back (bar at rest
     at the channel's left, grip at its rest position) and the
     channel's remaining width carries the bronze **END BREAK**
     tablet 152×48dp, centred in the register (§8 #13).
   - **Blocking-off state:** no bolt is drawn. The channel
     prints "BLOCKING IS OFF — TAP TO FIX" (AppTitleTextStyle,
     slot 17, centred) and the whole bolt course is the tap
     target. Ink only — no vermilion (§3.4).
7. **THE COLONNADE** (bottom rail, shell-owned): 52dp, existing
   RailGeometry unchanged (margins 20dp, cap 65.5×44dp at
   reference, PARK zone 48dp printing nothing). Temple
   dressing: recess fill slot 4; the room he is in reads as an
   OPEN doorway — its cap is slot 10 with slot 11 words and the
   cap's seat is a recess pit (§2.3 construction, no stone);
   closed doorways print their words in slot 18 on the recess;
   1dp vertical R1 joints at each stop boundary. No destination,
   order, or geometry changes.

### 7.2 THE SHIELD — the wall of inscriptions

Scroll column, margin 12dp. Top → bottom:

1. **LINTEL** — §5.1; row 2 = "{s} SHUT · {p} PEEK · {o} OPEN"
   (PillBadge, slot 18) — the wall's own count line.
2. **TITLE COURSE** — height 36dp: "SHIELD" DrawerHeader
   slot 17, left at text column. R1 joint beneath.
3. **FRIEZE ENTABLATURE** — §5.2, 36dp size (no NEXT line),
   directly under the title: the whole guarded day before any
   single routine is read.
4. **COURSE HEADS** (one per register, before its body): height
   36dp; at text column: the small seal (§5.3, vermilion
   outline) then 10dp gap then the register name
   (PrimaryButtonTextStyle, slot 17): "LOADOUT" / "ROUTINES" /
   "ALL APPS" / "GUARD". Right-aligned course fact (Mono15,
   slot 18): LOADOUT → chosen loadout's name; ROUTINES →
   "{n} ROUTINES" (Fremd: spell it whole, never "{n} CUT");
   ALL APPS → the PEEK inscription (§8 #8, tappable, opens
   typed minute entry); GUARD → "{k} OF 4 SET". R1 joint
   beneath each head.
5. **LOADOUT BODY** — height 84dp, ground fill. Vertical
   geometry, measured from the body's top edge (builders: these
   y-values are the spec, do not re-derive): the **groove band**
   (recess slot 4, radius 4dp, chisel pair on its top inner
   edge, spanning the text-column width) runs **y 8 → 44**
   (36dp). The **keystone channel** is the band's top zone,
   y 8 → 22; **THE KEYSTONE** — 34×14dp ◆PACK, radius 3dp,
   bronze (§3.2) — rides at **y 10 → 24** (2dp clearance above,
   its base crossing the channel's floor line: a capstone sits
   INTO its groove, not on it). A 1dp R1 joint line crosses the
   band at y 26. The **three stones** stand at **y 26 → 78**
   (52dp high, 6dp clear of the body's bottom edge), overlapping
   the band's lower 18dp and standing on the ground below it.
   Each stone: width = (334 − 16)/3 = **106dp** at reference,
   radius 4dp, gaps 8dp. Unchosen stone: ground fill (slot 1
   day / slot 2 night), slot 18 name. Chosen stone: seated
   (§2.3) — a seat pit (recess rect, stone + 4dp) is drawn on
   the ground course beneath it and the stone sits inset 2dp
   in that pit; fill slot 5, R2, slot 17 name (AppTitleTextStyle)
   + beneath it "{n} APPS" (PillBadge, slot 18). The keystone
   centres over the chosen stone and travels with MonolithKin
   NAV timing (180ms + 60ms per stop). Gold never enters this
   wall.
6. **ROUTINES BODY** — plaques: each height 54dp, ground fill,
   R1 joints between. Left at text column: name
   (AppTitleTextStyle, slot 17); beneath it days line
   (PillBadge, slot 18 — "MON — FRI" / "EVERY DAY"). Right:
   span (Mono15, slot 17): "09:00 – 13:00". While a plaque is
   being edited it LIFTS: fill slot 5 + R7 shadow (§2.3) and
   its fields become typed entries (exact minutes — known
   values are entered, never stepped). Final row: height 48dp
   with a bronze **ADD ROUTINE** tablet (128×32dp) at the text
   column.
7. **ALL APPS BODY** —
   - Search well: height 40dp, recess fill (slot 4), radius
     4dp, chisel pair on all inner edges (drawn as: dark border
     1dp + light border 1dp inset by 1dp), placeholder
     "SEARCH APPS" (PillBadge, slot 18), typed text
     BodyTextStyle slot 17, inset 12dp.
   - Category tabs: height 32dp; words BodyTextStyle; selected:
     slot 17 words + 2dp slot 6 underline beneath the word;
     unselected: slot 18 words. A mark, not a glow.
   - **App gate rows**: height 56dp, R1 joints between. Icon
     32×32dp radius 4dp at text column. Name AppTitleTextStyle
     slot 17; beneath it the state word (PillBadge, slot 18):
     "SHUT" / "PEEK {n} MIN" / "OPEN". Control at right
     (x ends 362dp), hit height 48dp: SHUT → bronze tablet
     72×32dp "SHUT"; PEEK → bronze-outline chip 104×32dp
     "PEEK {n} MIN" (PillBadge; sized for the longest lawful
     string, "PEEK 60 MIN" ≈ 92dp of tracked glyphs); OPEN →
     no box, the word
     "OPEN" (PrimaryButtonTextStyle, slot 18) is the control.
     Tapping the control cycles the gate (machinery as-is).
8. **GUARD BODY** — rows height 54dp, R1 joints: item name
   (AppTitleTextStyle, slot 17); right: "SET" (Mono15, slot 18)
   or, when missing, "MISSING" (Mono15Bold, slot 17) + bronze
   **FIX** tablet 64×32dp 8dp to its right.
9. **TOGGLES** (wherever a setting row carries one): the niche
   construction — niche 64×36dp recess (slot 4) radius 4dp,
   chisel pair inner; tablet 56×28dp radius 3dp centred in the
   niche: ON = slot 6 fill, slot 7 "ON", seated flush (no
   shadow, an R1 line directly beneath the tablet); OFF =
   slot 5 fill, slot 17 "OFF", offset +2dp down, R1 shadow
   only. The tablet's position and shadow ARE the state read.


### 7.3 TASKS — the tablet wall

Scroll column, margin 12dp.

1. **LINTEL** — §5.1; row 2 = "{n} WAITING" (PillBadge, slot 18).
2. **TITLE COURSE** — height 36dp: "TASKS" DrawerHeader slot 17
   at text column; R1 joint beneath.
3. **TABLETS** — each height **64dp**, radius 4dp, R2 shadow,
   gap 12dp between tablets. Fill: day sheet (slot 3); night
   raised (slot 5) — night sheets are fenced (§1.3) and the
   receipt owns the night sheet. Inside:
   - The **tear perforation**: a vertical dashed line at
     x = width − 116dp ◆PACK, from 8dp below the top edge to
     8dp above the bottom: dashes 4dp on / 3dp off, 1dp. It is
     a scored groove, drawn as the chisel pair's dark pass
     only (§2.2 sheet rule by day; night host is raised, dark
     edge per §2.2 with its light pass): day colour slot 17
     @ 45%, night colour #0E0906 @ 100%.
   - Task name: AppTitleTextStyle, slot 17, inset left 16dp,
     right bound = perforation − 12dp, max 2 lines, ellipsis.
   - **The stub** (right of the perforation, 116dp wide):
     **DONE — TEAR** bronze tablet 96×32dp centred (§5.4
     words; the tracked badge text measures ≈84dp, leaving
     6dp clear each side — do not shrink the tablet). The tear books at the tap (machinery law — no
     sound or stub animation may precede the booking).
   - **Started tablet state:** the screen's one gold spend —
     when a task is picked up, its tablet gains a 3dp gold
     (slot 8) left edge bar (full height, radius side only) and
     a gold **START** tablet replaces the stub's bronze one
     only at the moment of pickup offer, per the lane's existing
     states. Gold appears on at most one tablet at a time.
4. **THE COMPOSER** — a blank tablet at the wall's foot, height
   56dp, recess fill (slot 4), radius 4dp, chisel pair on all
   inner edges (as §7.2 search well). Placeholder "NAME THE
   TASK" (PillBadge, slot 18); typed text BodyTextStyle slot 17;
   right inside: bronze **ADD** tablet 64×36dp. **No duration
   field exists anywhere on this surface** — minutes are dead.
5. **Empty state:** one centred line, PillBadge slot 18:
   "NO TASKS WAITING". Nothing else on the wall.

### 7.4 THE DRAWER — the niche wall

Scroll column, margin 12dp.

1. **LINTEL** — §5.1; row 2 = "KEPT" (PillBadge, slot 18) —
   the drawer states what it is, never a count of worth.
2. **TITLE COURSE** — height 36dp: "DRAWER" DrawerHeader
   slot 17; R1 joint beneath.
3. **NICHE GRID** — 2 columns, gap 12dp; niche width 177dp at
   reference, height **96dp**, radius 2dp, fill recess (slot 4),
   chisel pair on the top and left inner edges. Inside:
   - **Niche lintel strip** (top 20dp): the finish's date-line,
     Anonymous 700 10sp (PillBadge tracking), slot 17:
     "29 OCT" — with the hour when the niche is opened:
     the closed niche prints date only.
   - Task name: AppTitleTextStyle slot 17, max 2 lines.
   - Bottom line (PillBadge, slot 18): "FINISHED 16:27" — or,
     for a broken finish: the filled vermilion square 8×8dp
     (§3.4) + "BROKEN" in slot 17. Those are the only two
     bottom lines a niche may carry. No durations-as-scores,
     no totals, no streaks — nothing is summed anywhere on
     this wall.
4. **TREASURY ROW** (curios): horizontal scroll row, height
   88dp: curio niches 64×64dp (same construction, radius 2dp),
   gap 12dp; beneath each, inside the row's remaining 24dp,
   its keeping line (PillBadge, slot 18): "KEPT FROM 12 OCT"
   — the session it was kept from. **No price, no requirement
   line, ever** (the fake "REQUIRES 300 MIN" is dead).
5. **PIGEONHOLES** (held messages): rows height 72dp, recess
   fill, radius 2dp, gap 12dp: sender AppTitleTextStyle
   slot 17; preview BodyTextStyle slot 18, one line, ellipsis;
   hour Mono15 slot 12 at right. Tap opens the letter whole
   (full peek, his ruling). Letters are read whole or not
   opened; no summary line is generated.

### 7.5 THE DESK CLOCK — the lamp niche (AOD)

- Ground: `TempleTrueBlack`, full bleed, both modes (this
  surface has no day face).
- Layout: centred column, padding 24dp (existing overlay
  geometry); the burn-in shift machinery moves the whole
  column as today — Temple changes no offset values.
- **Numerals**: DeskClockTextStyle 104sp, colour `TempleAodInk`
  (§1.2 — #E7D5AB @ 55%). Idle: the DEVICE'S time "16:04"
  (never the composer's dial setting — that print was a lie
  and is killed by name). Running: the span's elapsed.
- **Second line** (18dp below numerals, centred): Mono15 in
  `TempleAodInk`: running — "REMAINING 0:35:50"; stopwatch —
  "ELAPSED 1:24:10" is the numerals themselves, so the second
  line prints the wall hour "NOW 16:04" ◆PACK (one fact per
  line, never the same fact twice). Idle: no second line.
- **THE STAND**: one brass line, width = numerals' width +
  48dp ◆PACK, centred, 18dp below the second line (or below
  the numerals when idle). It carries the progress: the
  elapsed fraction is drawn 2dp slot 14 solid; the remainder
  1dp slot 14 @ 30%. The inherited 16dp pill progress bar is
  DELETED — the stand is the bar. At its right end, above the
  line, the current hour in PillBadge `TempleAodInk`:
  "16:04" — omitted when idle and on the stopwatch face
  (there the second line already carries the hour; no fact
  prints twice on this face).
- Nothing else is on this face. No date, no task name, no
  seal. A lamp in a niche.

### 7.6 THE RECEIPT — the slip that leaves the building

The one paper object; its print character is the proven keeper
and is NOT redesigned — this section materials it.

- **Slip**: width **342dp** ◆PACK centred on the sheet scrim,
  fill slot 3 (day sheet / night sheet-bronze), radius **2dp**
  (the inherited 24dp dies, §0), R2 shadow. Inner padding 20dp
  → inner width 302dp.
- **Head rule**: 2dp slot 14 brass, full slip width, at the
  slip's top edge — the brass rule the slip is pinned under.
- **Header row** (height 28dp): left "MONOLITH" PillBadge
  slot 17; right: the № — Mono15Bold slot 17, format "№ 0042"
  — with the small seal (§5.3) struck immediately 8dp to its
  right. Seal variants per §3.4: vermilion outline = witnessed;
  brass outline = reconstructed; vermilion outline + 1dp brass
  strike = broken.
- **Fact rows**: each height 28dp, full inner width: label
  PillBadge slot 17 left; value Mono15Bold slot 17 right.
  Rows in order: "PLANNED" (omitted entirely on FLOW slips),
  "LIVED", "ENDED", "EXTENDED +{n} MIN" (only when the session
  row carries an extension — printed loud or the slip lies),
  and the state row: "STATE" / "FINISHED" · "BROKEN".
  **Every word on the slip is slot 17 or slot 12.** No
  secondary tier touches the slip (§1.3).
- **Quiet line**: the WITHIN window line, QuietLine style
  (§4), slot 12, centred, max 2 lines.
- **Tear edge**: at the slip's bottom, a dashed joint line
  (as §7.3 perforation: 4 on / 3 off, 1dp, joint-shadow
  colour) 8dp above the bottom edge. Below it, nothing.
- Arrival motion §6. The slip is presented over the Drawer or
  the Deck per existing machinery; its ground scrim is slot 16
  @ 60% ◆PACK in both modes.

### 7.7 THE HELD DOOR — the sealed gate

Full-bleed, both modes in the night set (a gate is shut at any
hour): ground = night recess #0E0906. All colours below are
night tokens in both modes. Centred column, margin 24dp;
the name's centre sits at **38% of screen height** ◆PACK.

1. **Double rule** (top): width 240dp centred, slot 14:
   1dp + 2dp + 1dp, its bottom 28dp above the name's top.
2. **THE NAME**: the reached-for app's name, SealName style
   (§4), colour night glyph #EFE0BE, centred, set in CAPITALS
   (the door engraves — with the pill's PEEK face, one of the
   two lawful re-casings in §8). The blocked app itself is never
   shown, blurred, or mocked.
3. **THE SEAL**: the door seal (§5.3, 72×72dp @ 2dp, vermilion
   outline) centred exactly on the name's centre point,
   drawn OVER the name.
4. **Double rule** (bottom): mirrored, 28dp below the name.
5. **Fact lines** (28dp below the bottom rule, centred,
   Mono15Bold #E7D5AB): line 1 "THE BLOCK ENDS 16:27";
   line 2 "41 MIN LEFT". Gap 4dp.
6. **Frieze miniature** — §5.2 miniature (280×36dp), 20dp
   below the fact lines.
7. **Task block** (16dp below the frieze; omitted whole when
   no task runs): "IN THE MIDDLE OF" PillBadge #A98F63
   centred; 4dp below, the task name AppTitleTextStyle
   #E7D5AB centred, max 2 lines.
8. **The doors row** (pinned: bottom padding 32dp, full width,
   margin 24dp): **RETURN** left — a plain text tablet,
   96×48dp hit area, PrimaryButtonTextStyle #E7D5AB, no fill,
   no border. **PEEK** right — bronze tablet 152×48dp,
   radius 4dp, §3.2 fill, words "PEEK {n} MIN"
   (PrimaryButtonTextStyle, #E7D5AB). Exactly two affordances.
   - **Cooldown state:** the PEEK tablet is drawn SEATED AND
     BARRED: fill night ground #17100A, plus two 2dp brass
     (slot 14) horizontal bars across its face at ⅓ and ⅔ of
     its height, and it takes no tap. Above it (12dp):
     "THE LAST PEEK IS STILL COOLING" (BodyTextStyle,
     #E7D5AB, centred). Below it (8dp): "PEEK RETURNS IN
     {n} MIN" (PillBadge, #EFE0BE, centred). No vermilion.
   - **No-door state:** where the intercept cannot honour a
     lease, the PEEK tablet is NOT DRAWN — no ghost, no
     disabled state. RETURN centres itself in the row.
9. **Feel:** one low knock on arrival (§6); dismissal is
   instant, unanimated, silent. The door does not argue.

### 7.8 THE PILL — the hanging bronze tablet

An overlay over OTHER apps only (derivation is Freya's
machinery — PILL-AND-TIMES §2 — and binds exactly: never over
Monolith's own surfaces, never on the lock screen, never
through a call; it dies with the service and with the session).

- **The object, all faces:** fill night raised #392A16 in BOTH
  modes (a bronze plaque does not change with the room it
  hangs in); edge 1dp night brass #A37F35; radius 8dp; R2
  shadow (night colour). Height **52dp**. It hangs docked to
  the nearest screen edge with a **4dp gap** ◆PACK from the
  edge; dragged, it re-hangs (machinery). No dismiss control
  on SESSION or BREAK faces.
- **Face widths** ◆PACK: SESSION **136dp** · BREAK **152dp** ·
  PEEK **184dp**. Inner padding 12dp H.
- **SESSION face:** line 1 (PillBig #E7D5AB): "41 MIN" — a
  stopwatch session prints its elapsed "1:24:10" (7 glyphs at
  22sp mono ≈ 94dp ≤ 112dp inner ✓). Line 2 (PillBadge,
  #EFE0BE): "BLOCK 2 OF 3". Body tap: none (it is a plaque,
  not a button).
- **BREAK face:** line 1 (PillBadge #EFE0BE): "BREAK".
  Line 2: one row — "6 MIN" (PillBig) + " LEFT" (Mono15Bold),
  both #E7D5AB, baseline-aligned. Body tap returns him to the
  Deck at the END BREAK tablet.
- **PEEK face:** line 1 (PillBadge #E7D5AB, one line,
  ellipsis): the peeked app's name in capitals. Line 2: a row
  — the remaining **in seconds** "1:47" (PillBig #E7D5AB),
  left; at the row's right end the **✕ tablet**: 32×32dp,
  radius 3dp, incised square (chisel pair on the raised host:
  dark #0E0906, light sheet #4E3A20, §2.2), ✕ glyph
  Anonymous 700 15sp #E7D5AB centred; hit area padded to
  40×40dp. The ✕ calls the existing early-end verb — the way
  back hangs in front of him, counting.
- **Warnings:** at 30 seconds of peek remaining, one low
  knock (§6). When the derivation first reads the session
  ended, one low knock and the tablet is gone — its last act
  is the announcement, then it dies with the service state.
- Contrast claim, filed as a claim: parchment on the raised
  bronze ≈ 9.6:1 (DEEPEN §8, from tokens). To be re-measured
  on the render; if it fails, the face's fill — not the
  words — moves one step up the staircase, and the change is
  filed as a new pack revision, not a bench improvisation.

## 8. THE WORD TABLE — every string this world prints

Templates; {n}/{s}/{p}/{o}/{k} are machinery values, printed as
plain numerals. No string exists outside this table + the
screens' fixed titles (MONOLITH, SHIELD, TASKS, DRAWER, TOTAL,
FLOW) and machinery-supplied names (apps, tasks, routines,
loadouts — printed as supplied, never re-cased except where a
row above says CAPS).

| # | String | Seat |
|---|---|---|
| 1 | "GUARD — {k} OF 4 SET · {MISSING ITEM} MISSING" / when complete: "GUARD — 4 OF 4 SET" | Deck lintel row 2 |
| 2 | "BLOCKING IS OFF — TAP TO FIX" | Deck threshold, blocking-off state |
| 3 | "SLIDE OR TAP TO START" | Bolt channel inscription |
| 4 | "NOW {task}" line furniture: "NOW" + name + "· QUEUE {n}" | Deck dedication |
| 5 | "DONE — TEAR" | DONE tablets, Deck + Tasks stub |
| 6 | "NEXT — SLEEP 20:00" (routine name + its hour) | Deck frieze, idle |
| 7 | "NOW 16:04" (the living hour at the marker) | Every frieze |
| 8 | "PEEK — {n} MIN" (tappable; opens typed entry) | Shield ALL APPS course head |
| 9 | "BREAK {n} MIN" (tappable; opens typed entry) | Deck base strip tablet |
| 10 | "+ MIN" | Extension tablet, Deck dedication while running |
| 11 | "BREAK · {n} MIN" | Deck dedication, during a break |
| 12 | "DAY BROKEN — {WHAT FAILED} DID NOT START · {hh:mm}" | Deck confession course |
| 13 | "END BREAK" | Threshold tablet during a break (bolt drawn back; bronze tablet 152×48dp centred in the channel's remaining width, same 72dp register ◆PACK) |
| 14 | "PEEK {n} MIN" | Shield chip; Held Door tablet |
| 15 | "THE LAST PEEK IS STILL COOLING" + "PEEK RETURNS IN {n} MIN" | Held Door, cooldown state |
| 16 | "THE BLOCK ENDS {hh:mm}" / "{n} MIN LEFT" | Held Door fact lines |
| 17 | "IN THE MIDDLE OF" + task name | Held Door task block |
| 18 | "RETURN" | Held Door |
| 19 | "BLOCK {i} OF {n}" / "BREAK" / "6 MIN LEFT" furniture | Pill faces |
| 20 | "ELAPSED {h:mm:ss}" / "REMAINING {h:mm:ss}" / "NOW {hh:mm}" | Desk clock second line |
| 21 | "NAME THE TASK" / "ADD" / "NO TASKS WAITING" | Tasks composer + empty state |
| 22 | "SEARCH APPS" / "ADD ROUTINE" / "FIX" / "SET" / "MISSING" / "SHUT" / "OPEN" | Shield furniture |
| 23 | "FINISHED {hh:mm}" / "BROKEN" / "KEPT FROM {date}" | Drawer niches |
| 24 | Receipt furniture: "№ {n}", "PLANNED", "LIVED", "ENDED", "EXTENDED +{n} MIN", "STATE", "FINISHED", "BROKEN" | Receipt slip |

Durations are NEVER hardcoded: every {n} above reads its
setting (peekMinutes / breakMinutes per PILL-AND-TIMES) at
print time. A literal "2 MIN" anywhere in Temple code is a bug
against this pack.

## 9. BUILD ORDER, NAMED MACHINERY, DEBTS

**Builders (Maurer, Setzer) build in this order:** §1 tokens
(+ §1.2 constants) → §2 chisel/relief helpers (one Canvas
utility + the seat construction) → §5 shared components →
§7.1 Deck → §7.2 Shield → §7.3/§7.4 → §7.5–§7.8. Each surface
compiles ONCE when its piece is finished (Pipeline Law 11);
Meissel does not compile and does not render.

**Named machinery — NOT in this pack, Ren rules:** the END
BREAK verb (small) · the two duration settings' storage and
readers (already commissioned as the times machinery) · the
add-time writer (small) · the warning events' reach (heartbeat
/ pill-service call sites) · the idle desk clock printing the
device's time ([MERGE] print over an existing fact). Surfaces
above are designed so each lands without recomposition.

**Debts, stated before judging (no render, no cure):**
1. The phase-2 §1.4 day-cure render — code-applied,
   Noir-verified, UNPROVEN in pixels. Still owed.
2. First renders owed from this pack: Deck day + night (stele
   front whole), Shield wall, Tasks wall, Drawer wall, desk
   clock (idle + running), receipt in-world (witnessed +
   reconstructed + broken seals), Held Door in all three
   side-door states, pill in all three faces. Until they
   exist, every number in this pack marked ◆PACK is a claim,
   and the pill's 9.6:1 is an estimate.
3. The accent census in the harness still pins the scrapped
   orange law (inherited disclosure) — the merger updates it
   to §3.3's census: one gold, four seats.

*— Meissel, Temple crew, Oct 29, 2026. Companion to
PRESENTATION-temple.md and DEEPEN-temple.md; where they left a
number open, this pack's ◆PACK number governs the build.*

