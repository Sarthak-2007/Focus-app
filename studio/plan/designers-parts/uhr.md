# UHR — The Wheel as a Drawn Object (Plan Wave, Designers)

Seat: Uhr, Watchmaker, Designers cohort (coordinator: Sana). Scope: the Deck
wheel ONLY, as a static drawn object — geometry, print, knurl, light, tones.
Motion curves belong to Kin, haptics to Ton; where this file touches them it
gives the drawn tolerance they must land inside, nothing more.

Settled law obeyed, not re-argued: 36 real detents at 5-min steps · three tick
weights (5 / 15 / 30) · rim numerals at majors only · hub = pips only, no
numerals, no words · TOTAL readout lives outside the wheel and is Deck's only
big number · zero orange on the wheel at any alpha, in any state · cheap knurl
kept (alternating-tone teeth under the travelling specular) · radius 8/16 ·
depth = one tonal step + hairline, no cast shadow on the wheel, ever.

Angles in this file: degrees clockwise from 12 o'clock, screen space, unless
marked LOCAL (painted on the wheel, rotating with it). Radii from wheel centre.
All sizes dp unless marked sp.

---

## 1. Diameter — 288dp. Vau is right; his scale was wrong, his size was not.

**Wheel outer diameter: 288dp (R = 144dp). I keep Vau's 288 and defend it.**

- **Fit.** Screen margin is frozen at 20dp. On the narrowest handset in scope
  (360dp wide) the content column is 320dp; a 288dp wheel leaves a 16dp gutter
  each side inside the column — the wheel dominates without touching anything.
  At 304dp the gutter falls to 8dp and the wheel crowds the loadout band
  below it; at 272dp the gutter grows to 24dp and the Deck's lower field goes
  dead again — the exact void this diameter exists to kill.
- **Legibility scales with R.** Adjacent detents are 10° apart. At the tick
  baseline (R = 130dp, §3) that is 22.7dp of arc between tick centrelines —
  a thumb landing ±11dp off-centre still unambiguously owns one detent. At
  240dp (my wave-1 dial) the same pitch is 19.2dp and the minor tier crowds;
  at 288dp every tier in §3 fits with air around it (§3 clearance column).
- **The hand.** 288dp puts the rim's working arc (lower 180°, where the thumb
  actually drags) at a radius the thumb pad (Ø ~28dp contact) can roll along
  without the hub entering the gesture. Smaller wheels turn the drag into a
  hub-collision exercise.
- **What I take from Vau and what I strike.** His static rebuild put the dial
  at 288dp and printed `5/10/…/60` numerals on a 60-tick, 1-minute scale at
  R = 112dp. The diameter survives; the scale does not — settled law replaced
  it with 36 true detents, and §4 moves the numeral ring to R = 100dp because
  on the true scale his R = 112dp sits 2dp inside the major ticks' inner ends
  (R = 114dp) and collides. Mailed to Vau with this section attached.

Vertical placement (for Raster's grid, stated once): wheel centre sits on the
Deck's vertical axis; wheel top edge 24dp below the TOTAL readout's baseline.
The wheel is the Deck's centre of mass — nothing else on Deck exceeds 144dp
in any dimension.

---

## 2. Radial stack — the whole wheel in one table (pin this above the code)

| Zone | R (dp) | Width | What lives there |
|---|---|---|---|
| Hub disc | 0 – 64 | Ø 128 | Pips only (§6) |
| Hub edge hairline | 64 | 0.75 stroke | Closes the hub (§6) |
| Hub breathing gap | 64 – 72 | 8 | Bare face, nothing printed |
| Face field | 72 – 114 | 42 | Numeral ring centred R = 100 (§4); otherwise empty |
| Tick band | 114 – 130 | 16 | The one true scale (§3) |
| Clearance annulus | 130 – 136 | 6 | The reader dot and nothing else (§5) |
| Knurl band | 136 – 144 | 8 | Cheap knurl, grip only (§7) |
| Wheel edge hairline | 144 | 0.75 stroke | Outer parting line |

**Rim width, defined: 30dp** — R = 114 to R = 144, i.e. tick band (16) +
clearance (6) + knurl (8). Everything inside R = 114 is face; everything in
the rim is measurement or grip. No element may straddle the R = 114 line
except the end-stop feet (§9), which do so deliberately and only twice.

Wheel body: disc fill = `raised` (§10), seated on the Deck ground by exactly
one tonal step + the R = 144 hairline at ink 18% (day) / glyph 14% (night).
No shadow, no bevel ring, no second concentric line anywhere on the face —
the complete set of circles on this wheel is: hub edge, tick baseline
(implied, never drawn), knurl inner edge (implied by the teeth), outer edge.

---

## 3. The scale — 36 detents, three weights, one baseline

**Detent law.** Values v = 5, 10, 15, …, 180 minutes — 36 values, 5-min
steps, step angle exactly 10.000°. Painted LOCAL angles (the wheel carries
the scale; turning the wheel clockwise increases the value, like a volume
crown):

    λ(v) = −(v − 5) × 2°   (local, counter-clockwise painting)

so λ(5) = 0°, λ(10) = −10°, …, λ(180) = −350°. Wheel rotation at rest for
value v is R(v) = +(v − 5) × 2° clockwise; total travel 0° → +350°. The
remaining 10° of the circle is the dead wedge (§9) — the scale does not
close, and that gap is how the eye knows the instrument has ends.

Ticks are computed from integer detent index i = (v − 5)/5 ∈ [0, 35],
angle = i × 10°, never accumulated float rotation. Angular error per tick
≤ ±0.25° (±0.57dp of arc at R = 130dp).

**Three weights, cumulative** (a 30-min tick is not also drawn as 15 or 5):

| Weight | Detents | Count | Length | Width | Outer R | Inner R |
|---|---|---|---|---|---|---|
| Minor (5-min) | every detent not below | 24 | 8dp | 1.0dp | 130 | 122 |
| Medium (15-min) | v ∈ {15,45,75,105,135,165} + 30-multiples excluded below* | 6 | 12dp | 1.5dp | 130 | 118 |
| Major (30-min) | v ∈ {30,60,90,120,150,180} | 6 | 16dp | 2.0dp | 130 | 114 |

*Count check: 36 detents = 24 minor + 6 medium-only (15-multiples that are
not 30-multiples) + 6 major. Every 15-min position carries at least the
medium geometry; every 30-min position carries major geometry instead.

- **One baseline:** all 36 outer endpoints lie on R = 130dp exactly. No tier
  starts at a different radius; a ragged outer edge is a reject.
- **Caps are butt (square), never round.** A round cap adds half its width
  at both ends — 1.0dp of overshoot on a major — and breaks the baseline.
- **Tone:** ticks are print, flat, one pass, no specular, no shadow. Day:
  minor ink @ 55%, medium ink @ 75%, major ink @ 100%. Night: minor glyph
  @ 50%, medium glyph @ 72%, major glyph @ 100%. Alpha buys the hierarchy;
  geometry buys the truth — never trade one for the other below these floors.
- The 5-min end tick (i = 0) and the 180-min end tick (i = 35) use major
  geometry (16 × 2.0dp) regardless of the table — ends must read as ends —
  plus the stop foot of §9. They carry no numerals (§4).

---

## 4. Rim numerals — six, at majors only

- **Set: `30 60 90 120 150 180`** — exactly the major detents. No `5` (it is
  an end, §9, not a major), no intermediate values; the medium ticks do
  their job unnumbered.
- **Font/size:** Anonymous Pro Bold, **13sp**, ink (day) / glyph (night),
  full opacity, flat print — no letterpress second pass on the wheel.
- **Placement:** each numeral centred on its detent's LOCAL angle at
  **R = 100dp**, centre tolerance ±0.5dp in both axes. Numerals are set
  **upright** (baseline horizontal, screen-aligned when the detent is at the
  reader) — never rotated radially; rotated numerals are a toy-watch tell.
  Because the scale rotates with the wheel, a numeral is upright in screen
  space only when its detent is selected; at all other positions it rides
  the wheel's rotation — that is honest: print on a moving part moves.
- **Clearance:** a 13sp Anonymous Pro numeral is ≈ 9.2dp tall; centred at
  R = 100dp its outer edge reaches ≈ R = 104.6dp — **9.4dp of air** to the
  major tick inner end (R = 114dp). Its inner edge reaches ≈ R = 95.4dp —
  23.4dp of air to the breathing gap. Nothing else is printed in the face
  field: no branding, no minute marks, no second ring.

---

## 5. The reader — one fixed dot reads the scale

The wheel carries the scale; the frame carries the reader. The reader never
rotates, never takes orange, and is the only fixed mark on the Deck's dial
area.

- **Reader dot:** filled circle, **Ø 4.0dp**, centre at screen angle 0°
  (12 o'clock) at **R = 133dp** from wheel centre. It lives in the clearance
  annulus (130–136dp): radially it spans R = 131–135dp, leaving exactly
  **1.0dp** to the tick outer endpoints (130dp) and **1.0dp** to the knurl
  inner edge (136dp). It touches nothing, at any rotation.
- **Reading rule:** the selected detent is the tick whose centreline passes
  within **±0.5°** of the dot's centreline (±1.16dp of arc at R = 133dp).
  At rest, the settled tick must sit dead under the dot — if a settled
  screenshot shows the dot splitting two ticks, the detent maths is wrong,
  not the drawing.
- **Form:** day: ink @ 100%. Night: glyph @ 100%. No ring, no tail, no
  triangle, no second index at any other angle. When the wheel is dragged
  past an end into the rubber-band zone (§9), the dot sits over the blank
  dead wedge — blank under the reader is the drawn statement "past the
  end," and it needs no icon to say it.

---

## 6. Hub — pips only, count as position

Hub: Ø **128dp** (R = 64dp), centre coincident with wheel centre within
0.5dp on both axes. Fill = one tonal step from the face (§10), closed by
the 0.75dp hairline at R = 64dp (ink @ 22% day / glyph @ 18% night). No
shadow. No numerals, no words, no `×`, no screws — wave-1's screwed plate
is superseded by settled law: the hub's entire vocabulary is pips.

**Pips — outer ring (counts 1–12):**
- 12 pip positions on a circle at **R = 44dp**, at every 30° starting at
  0° (top). Position k (1-indexed) sits at screen-local angle (k − 1) × 30°
  clockwise from top, painted on the hub (the hub does not rotate with the
  wheel — it is a separate, non-rotating control seated in the wheel's
  centre; pips never ride the scale's rotation).
- Pip size: **Ø 7.0dp**. Centre-to-centre chord at R = 44dp, 30° apart:
  22.8dp; edge-to-edge gap 15.8dp — pips can never visually merge.
- Filled pip (counted): solid disc, ink (day) / glyph (night), 100%.
- Empty pip (uncounted): ring only — 1.0dp stroke circle at ink @ 28%
  (day) / glyph @ 24% (night), interior = hub fill. An empty position is
  drawn, not omitted: the ring of 12 positions is the scale of the count,
  and count is read as *the last filled position*, never as a numeral.
- Count = number of filled pips, filled clockwise consecutively from the
  top position. A gap in the fill sequence is a build error, not a state.

**Pattern change at 13 — inner ring (counts 13–24):**
- Counts 1–12 use the outer ring only. At count **13** the pattern changes:
  the outer ring stays full (12/12) and an **inner ring** appears — 12
  positions at **R = 27dp**, every 30° from top, pip **Ø 5.5dp** (chord
  14.0dp, gap 8.5dp), same fill/empty language, filled clockwise from top.
  Count = 12 + inner filled. Inner-ring empty positions are drawn only
  once the inner ring exists (count ≥ 13); below 13 there is no inner
  ring at all — no ghost positions for a count he does not have.
- Hard cap: **24**. At 24 both rings are full; the hub refuses further
  increments by stillness (no shake, no buzz in the drawing — refusal is
  the absence of the +1dp travel, §6 press). 24 × 180 min is beyond any
  real day; the cap exists so the pattern never needs a third ring.
- Count 0 (no blocks): outer ring drawn all-empty, no inner ring. The hub
  reads as an unloaded instrument, which is the truth.

**Press state (+1dp, fire-on-release):** the hub is a button — tap adds
one block. On DOWN the whole hub disc translates **+1.0dp** toward the
face: drawn as (a) hub fill stepping one tonal step toward the face fill
(§10 press column), and (b) the R = 64dp hairline deepening to ink @ 38%
(day) / glyph @ 32% (night). Pips do not move, scale, or re-tone. On
release the increment fires and the hub returns. A press that slides off
the hub's Ø 128dp bounds cancels — no fire, hub returns. Travel is the
only press language: no ripple, no glow, no colour change beyond the one
tonal step.

---

## 7. Knurl — the cheap version, specced to look expensive

Ren's craft call stands: alternating-tone teeth lit by the travelling
specular. No per-tooth bevels, no per-tooth gradients, no light table.
Each tooth is **one flat fill**. The expense is in the numbers, not the
rendering.

- **Count: 120 teeth**, full 360°, in the knurl band R = 136–144dp.
  Pitch: **3.000° per tooth**; circumferential pitch at mid-band
  (R = 140dp) = **7.33dp**.
- **Tooth geometry:** each tooth is a radial wedge bar, radial length
  **8.0dp** (R = 136 → 144, the full band), tangential width **2.4dp**
  measured at R = 140dp (angular width 0.982°), centred on its pitch
  angle, sides radial, ends butt. Tooth-to-tooth gap at mid-band:
  **4.93dp** of band ground showing between teeth. Teeth are drawn in the
  wheel's rotating layer — they turn with the wheel; the light does not (§8).
- **Tone pair (the alternation):** even-index teeth Tone A, odd-index
  Tone B, band ground beneath = face fill showing in the gaps.
  - Day: A = `#FAF6F0` (`raised`), B = `#CFC7C0` (`knurlShade` — ground
    stepped down once; the file's one requested new day token).
  - Night: A = `#4A423C` (`controlFill`, the night cap tone), B =
    `#241E1A` (`knurlShadeNight` — raised stepped halfway toward ground).
  The pair straddles the band ground in both modes: one tooth catches,
  one falls away, and at arm's length the band reads as cut metal, not
  print — because the alternation period (7.33dp) is below the distance
  at which the eye resolves individual teeth as *marks*.
- **What is banned on the knurl:** per-tooth highlight/shade line pairs,
  gradients on teeth, anti-aliasing tricks beyond Canvas default, teeth
  in the dead wedge being omitted (knurl is grip, not scale — it runs the
  full 360° including behind the dead wedge, §9), and any tooth count
  change between modes. 120 in day, 120 in night, same angles.
- **Cost, stated:** 120 flat wedges in a `drawWithCache` layer keyed on
  wheel size; rebuilt on size change only. The specular (§8) re-tones
  teeth per frame by fill lookup, never by re-drawing geometry.

---

## 8. The travelling specular — one rig, app-wide

One light, fixed in space, for the wheel, every cap, and every sled.
Parts pass through it; it never follows a part. Mara's law holds: the
rig carries no data — a tooth flares because it is *there*, never because
the wheel means something by it.

- **Azimuth: 315°** — the highlight core is centred at screen angle 315°
  (upper-left, the 10:30 position), fixed in screen space for the life of
  the app, both modes. This is the same 315° as the house light; there
  are not two lights.
- **Angular width:** full-strength core **±9°** (18° wide). Falloff from
  core edge to zero over the next **25°** each side — total lit footprint
  **68°** (±34°).
- **Falloff law:** for a point at angular distance Δ from 315°,
  `k(Δ) = 1` for |Δ| ≤ 9°; `k(Δ) = cos²( (π/2) × (|Δ| − 9°) / 25° )` for
  9° < |Δ| < 34°; `k(Δ) = 0` beyond. Smooth at both joins, no hard edge
  anywhere — a hard edge would read as a drawn wedge, and a drawn wedge
  is a lie about a surface.
- **Peak strength:** the rig lifts a surface toward the specular tint by
  at most **k × 10%** (day: tint `#FFFFFF`; night: tint `#EDE3D8` at
  **k × 8%**). Nothing under this rig ever exceeds 10% added white —
  past that the app is chrome, and chrome is banned.
- **On the wheel:** the rig is sampled per knurl tooth at the tooth's
  current screen angle (tooth centre): rendered fill =
  `mix(toothTone, specularTint, k × peak)`. The hub's hairline catch arc
  and the face take the same sample as a clipped overlay ring/band —
  drawn in screen space AFTER the rotating layer, so the lit zone stays
  at 315° while teeth stream through it. The tick print and numerals
  take **no** specular: print is flat, or the scale's contrast becomes a
  function of rotation and the instrument lies by position.
- **On caps and sleds (same rig, linear sampling):** a translating part
  samples the rig as a fixed vertical band in its parent panel's space:
  band centre **112dp** from the panel's left inner edge, core width
  **20dp**, falloff **28dp** each side (total 76dp footprint), same cos²
  law, same peaks. A cap sliding through 112dp flares and settles exactly
  as a tooth rotating through 315° does — one light, two samplings,
  no per-screen invention.

---

## 9. End-stops at 5 and 180 — the scale has ends, drawn as ends

- **The dead wedge:** LOCAL angles from λ = 0° clockwise 10° back to
  λ = −350° (the sector between the 180 tick and the 5 tick, passing
  local 12 o'clock when the wheel is at rest at either end's neighbour).
  No ticks, no numerals in the wedge. Knurl runs through it (§7). Face
  fill only.
- **Stop feet:** the 5-min and 180-min ticks each carry one foot: a
  square block **4.0dp wide (tangential) × 4.0dp tall (radial)**, centred
  on the tick's angle, spanning **R = 110–114dp** — the only two marks
  allowed to cross the R = 114 rim line into the face field. Ink/glyph
  at 100%, butt joins to the tick's inner end. A foot says "the scale
  terminates here" in geometry; no word, arrow, or `MIN`/`MAX` print
  exists on this wheel.
- **The anvil line:** one radial hairline in the dead wedge's centre
  (LOCAL λ = −355°, i.e. 5° from each end tick): **1.0dp wide, R = 118–130dp**,
  ink @ 30% (day) / glyph @ 26% (night). It is the wedge's centre mark —
  when over-rotation brings it under the reader dot, the hand is exactly
  halfway into nowhere, and the drawing says so without a symbol.
- **Over-rotation (drawn limit):** the wheel may be dragged at most
  **+4°** past either end (rubber-band zone — resistance curve is Kin's;
  the drawn limit is mine). At full over-rotation the reader dot still
  sits over blank wedge or the anvil line, never over a false tick:
  there is no angle at which this wheel can display a value it cannot set.

---

## 10. Tonal assignments — by token, both modes

Depth law on the wheel: exactly one tonal step between adjacent zones +
hairlines. No zone is two steps from its neighbour.

| Element | Day token | Day hex | Night token | Night hex |
|---|---|---|---|---|
| Deck ground (behind wheel) | `ground` | `#E4DFDA` | `ground` | `#171412` |
| Wheel face / band ground | `raised` | `#FAF6F0` | `raised` | `#362C27` |
| Hub fill (rest) | `ground` | `#E4DFDA` | `recess` | `#100E0C` |
| Hub fill (pressed, +1dp) | `recessDay`* | `#D8D1CB` | `ground` | `#171412` |
| Hub hairline | `ink` @ 22% | `#252323` | `glyph` @ 18% | `#EDE3D8` |
| Ticks minor / med / major | `ink` @ 55/75/100% | `#252323` | `glyph` @ 50/72/100% | `#EDE3D8` |
| Numerals | `ink` @ 100% | `#252323` | `glyph` @ 100% | `#EDE3D8` |
| Reader dot | `ink` @ 100% | `#252323` | `glyph` @ 100% | `#EDE3D8` |
| Pip filled | `ink` @ 100% | `#252323` | `glyph` @ 100% | `#EDE3D8` |
| Pip empty ring | `ink` @ 28% | `#252323` | `glyph` @ 24% | `#EDE3D8` |
| Knurl tooth A | `raised` | `#FAF6F0` | `controlFill` | `#4A423C` |
| Knurl tooth B | `knurlShade`* | `#CFC7C0` | `knurlShadeNight`* | `#241E1A` |
| Stop feet | `ink` @ 100% | `#252323` | `glyph` @ 100% | `#EDE3D8` |
| Anvil line | `ink` @ 30% | `#252323` | `glyph` @ 26% | `#EDE3D8` |
| Specular tint | white @ ≤10% | `#FFFFFF` | `glyph` @ ≤8% | `#EDE3D8` |
| Outer edge hairline | `ink` @ 18% | `#252323` | `glyph` @ 14% | `#EDE3D8` |

*Three derived tones are requested as tokens (`recessDay`, `knurlShade`,
`knurlShadeNight`) because the settled day palette ships no recess and the
knurl pair needs a named shade in each mode. Each is exactly one step from
its parent above; no other new colour enters the wheel. Night hub logic:
hub = `recess` (below ground) at rest — the hub is a well the pips sit in —
and pressing steps it UP to `ground`: the +1dp travel reads as the well
floor rising to meet the face. Day hub mirrors it one step the other way
(ground at rest on a raised face, recess-step when pressed). Orange:
**none — no row, no state, no alpha.**

The `text` token (`#D2C6BB` night) has no job on the wheel: the wheel
contains no text. Numerals are glyphs, and they take `glyph`.

---

## 11. Loupe QA gate — acceptance, all measurable

Export Deck at 200%, crop the wheel. Pass requires ALL of:

1. At rest on any value, the reader dot's centreline splits exactly one
   tick; measured error ≤ ±0.5°.
2. All 36 tick outer endpoints touch the R = 130dp overlay circle; none
   crosses or falls short by > 0.75dp.
3. Exactly 36 ticks, 6 numerals, 12 (+12) pip positions, 120 knurl teeth
   — counted, not eyeballed. No duplicate/fainter second tick system.
4. Knurl teeth strictly alternate A/B with zero consecutive repeats
   around the full 360°, dead wedge included.
5. Hub shows a hairline edge and no shadow at any zoom; pips show no
   numeral or word at any zoom.
6. A screenshot at 315° ± 34° shows knurl flare; the same screenshot with
   the wheel rotated 180° shows the flare STILL at 315° screen angle —
   if the light moved with the wheel, the rig is wrong.
7. Grep the wheel's draw code for the orange tokens: zero hits.

— Uhr, Watchmaker, Designers. Every value above is a number a builder can
type. Where I kept a predecessor's number (Vau's 288), I said so and said
why; everything else here supersedes my wave-1 dial (240dp, 60 ticks,
screwed hub) in full.
