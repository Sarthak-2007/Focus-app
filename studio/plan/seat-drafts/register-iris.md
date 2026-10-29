# REGISTER AUDIT — COLOUR + LIGHT — Iris, Designers (colour)

*Auditing plan/REGISTER.md (Sana's draft) against: my §1.3 map
(plan/designers-parts/iris.md + DESIGNERS-PLAN §1.3, normative),
plan/beyond-deck-parts/mara.md, BEYOND-deck W2, MASTER-PLAN §§2–3
as filed, DESIGNERS-PLAN §§1.3/3/6, nacht.md §1.3, Dario's ruling
(mail 0715 + invention/maximpl-parts/01-dario.md).*

**Hale's rule applied throughout:** no colour claim below is filed
without its channel arithmetic. Master table in the Appendix; the
headline result: **every hex in the locked system is warm-ordered
R≥G≥B. The only G-dominant hex found anywhere in scope is the
shipped code's `#758F88` (R117 G143 B136, G−R = +26) — already
convicted by Cato-II, Critics invention wave. It is not in any plan
file; it is in the code a builder will touch. See Hunt H15.**

---

## A. Every colour/light value the register actually carries

The register carries **exactly five** colour/light entries — and
**zero hex values**. Its own preamble says a builder who finds a
number not in the register "does not build from it." Read literally,
no colour in the app may currently be built. That gap is §B.

### A1. GATE 0.68 — "No colour changes at the gate, ever (F-ORANGE)"
**CONFIRMED.** MASTER §2.6: "The gate at 0.68 is haptic-only — no
colour flips at the gate (F-ORANGE: the cap IS the accent object at
rest)." BEYOND W4 identical. DESIGNERS §1.3 accent budget agrees.

### A2. SPECULAR — swept form, 720-entry LUTs + prefix sums, sinc
attenuation |sin(πΔ/p)/(πΔ/p)|, p = 3°, "sinc replaces dual sub-draws"
**CONFIRMED as registered — and the register is the *newest* source,
not the deviant one.** Dario's ruling (mail to Mara, 0715; filed in
invention/maximpl-parts/01-dario.md, Amendment 2) kills the dual
sub-draw: its pitch response is |cos(πΔ/2p)| = 0.71 at Δ=4.5° and
**1.0 inverted at Δ=6°**, where the sinc is exactly 0 and Mara's
table claims 0.21 at 4.5°. Sinc as a frame-global multiplier delivers
every row of Mara's table; draws stay at 120. Δ over *drawn* frames
(Amendment 1) also confirmed from the same ruling.
**CONTESTED in the other direction:** MASTER §2.4 and BEYOND W2 both
still print "dual sub-draws at |Δ| ≥ 1.5°" alongside the sinc. Per
the register's own preamble those two files carry the superseded
interim — Sana reconciles by striking the dual-sub-draw phrase in
MASTER §2.4 / BEYOND W2, **not** by editing the register. I do not fork.
Two builder-grade precisions the register should absorb when it
amends: (a) the LUTs are **two** 720-entry tables (t³ broad, t⁸
machined, 0.5° steps) and the prefix-sum tables are **721** entries
(mara.md, Beyond 1); (b) Dario's correction — Mara's peak-envelope
column is t³; the **t⁸ machined peak attenuates to 0.968 at Δ=18°,
not 0.988**.

### A3. BROKEN FLARE — variance 0.84–1.16, compile-time only, teeth
17/83 hand-set 0.62/0.68, no runtime writer
**CONFIRMED, four-way.** Identical in mara.md (Beyond 3: `v[i] =
0.84 + 0.32 × h[i]`, splitmix32; alpha_i = 0.16 + (specularAlpha −
0.16) × v[i] — excess only), BEYOND W2, MASTER §2.4, and the
register. The cleanest entry in the document.

### A4. CLOSURE BAR — "160ms ink-settle **from 88%**"
**CONTESTED — the 88% has no Deck source.** BEYOND W2 and MASTER
§2.4 both file "160ms ink-settle" with no starting percentage. The
only "settle from 88%" in the plan corpus is Nadia's horizon
re-strike (plan/maximpl-parts/08-nadia-horizon.md:170, :217) — a
different surface (chrome horizon, not the hub closure bar). Either
Sana cites a Deck source for 88% or the register strikes it; as
filed it is an un-sourced constant of exactly the kind this register
exists to kill. The 160ms itself is CONFIRMED.

### A5. AOD — dimming luminance 0.215 → 0.174 → 0.130, ceiling 0.25;
REMAINING `H:MM` at 104sp; patrol 4×3 anchors
**Values CONFIRMED; provenance in the brief/register chain is
wrong.** Source is DESIGNERS-PLAN §6.2 + nacht.md §1.3 — MASTER-PLAN
as filed **ends at §3.10 and contains no AOD section at all**, so
any citation of these numbers to "MASTER §§2–3" is a phantom
reference (the same disease CRITICS-PHASE3 charged against §0's
citations of §§5.1–§9). Sana: register the AOD entry's source as
DESIGNERS §6.2 / nacht.md §1.3 until a MASTER §5 actually lands.
Stage windows (0–20 / 20–50 / 50+ min), the 2s crossfade on a minute
boundary, and the concrete per-stage hexes are all missing from the
register — Hunt H12.

---

## B. REGISTER-HUNT — load-bearing colour/light constants the
sources carry and the register lacks

Each item: value + source. All hexes channel-checked, Appendix.

**H1 — The locked night staircase (DESIGNERS §1.3; iris.md §0).**
ground `#171412` (R23 G20 B18) · recess `#100E0C` (16/14/12) ·
raised `#362C27` (54/44/39) · cap `#4A423C` (74/66/60) · text
`#D2C6BB` (210/198/187) · text-2 `#9C8E84` (156/142/132) · muted
`#776A60` (119/106/96) · glyph `#EDE3D8` (237/227/216) · cream
`#FFE4CF` (255/228/207) **text-only, never a fill**. None registered.

**H2 — The day staircase (DESIGNERS §1.3, Stoff; iris.md §0).**
recess `#D8D1CB` (216/209/203) · ground `#E4DFDA` (228/223/218) ·
raised `#FAF6F0` (250/246/240) · ink `#252323` (37/35/35) · text
`#2C2A29` (44/42/41) · muted `#5F5856` (95/88/86). None registered.

**H3 — The accents + the budget (DESIGNERS §1.3; iris.md §3).**
ACCENT day `#F55314` (R245 G83 B20) · night `#F47442` (244/116/66) ·
ON-ACCENT `#471602` (71/22/2) both modes. Budget per screen: Deck 1
(slide cap) · Tasks 1 (START key) · Shield 0 · running 0 · AOD 0 ·
overlays 0 · chrome 0. Orange = COMMIT fill only; no orange type,
numeral, glyph, hairline or badge anywhere. The register states the
law only negatively, at the gate (A1) — the budget itself, the only
colour law a builder is graded on, is unregistered. Add Ren's §0
ruling as its dead-entry companion: **armed dot — no orange in
chrome, no exceptions; R13 refused** (MASTER §0).

**H4 — `#FF8A5C` BREAK SPENT exception — register is silent; it
must carry it as DEAD, not omit it.** DESIGNERS §1.3 + iris.md §4.3
file it live (night-only, word-only, 2 placements, ≤2% area, never
AOD); DESIGNERS C4 says it "stands or falls with Ren's R13 review."
Ren's review is in: R13 refused, law stands (MASTER §0), and my
brief records the HOLD SPENT exception as **DENIED by Ren and
staying denied**. Arithmetic for the AOD placement, from nacht.md
§1.7: `#FF8A5C` (255/138/92) sits at lum **0.402**, breaching the
0.25 AOD ceiling outright. The register names other dead constants
(0.80, 0.82) precisely so they are not resurrected — a builder
reading §1.3 today would resurrect this one in good faith. Sana:
add it to the register's superseded list, denied, with the 0.402
figure. (I do not re-argue it. Denied is denied.)

**H5 — Mara's filed specular form (mara.md, filed form; wild-seats/
10-mara.md §0.2).** `specularAlpha = 0.16 + 0.66 × t(θ)³` (face/
broad); machined rim split `base + peak × t(θ)⁸`, **+15% peak**;
light vector `L = normalize(−0.42, −0.91)`, θ_L ≈ 245.2°. The
register registers the *swept wrapper* but not the function it
integrates — a builder cannot compute one alpha from the register
alone.

**H6 — Mara's caps and clamps (mara.md).** Specular peak **14% day /
8% night over cap fills**; composited non-accent fills **never
lighter than `#4A423C`** at night; specular **never over text**;
colour = desaturated warm cream/white, **≤35% saturation at peak**
(mara.md Beyond 3). Swept averaging only lowers peaks, so the night
ceiling is inherited — but it must be registered to be inherited.

**H7 — The OTHER day peak: 10%, and the rig's territory
(DESIGNERS §1.4; MASTER §2.4; BEYOND D6).** Hub-cap specular core:
azimuth **315°**, core **±9°**, cos² falloff to zero at **±34°**,
peak lift **10% day / 8% night**. This is a *different* constant
from H6's 14% day tooth peak over cap fills — two day peaks, two
surfaces, and a builder given only one number will spend it on both.
Territory list is load-bearing too: knob cap, sled caps, wheel
crown/hub, pressed key caps ONLY; tracks, ground, rows, text,
sheets, nav pill forbidden.

**H8 — Mara's linear form, bounded (mara.md, as corrected by
Dario).** `bandOffset = clamp(−0.35 × localTravelPx, −8dp, +8dp)`,
measured from the nearest seat, **zero at every seat**. The
cumulative filed form is dead by Mara's own concession. Unregistered.

**H9 — D1 / D2 / D3 (BEYOND-deck D1–D3; MASTER §2.3–2.4).**
- D1: at night the hero numeral — only the numeral — steps to glyph
  `#EDE3D8`; label and unit stay plate.
- D2 chamfer: 2dp arc at R133, 250°→350° through 315°, day
  `#FAF6F0` **at 80%** / night `#4A423C` **at 60%**; if the knurl
  gate fails, the arc moves to **R140** as the rim's only light.
- D3 deboss ghosts (behind the removal trial): shadow ghost y−1dp
  day `#D8D1CB` at 60% / night `#000000` at 50%; catch ghost y+1dp
  day `#FAF6F0` at 70% / night `#4A423C` at 35%. MASTER §2.3 files
  D3 with **none** of these numbers — they live only in BEYOND.

**H10 — D9 wheel print voices (MASTER §2.4, "as filed").** Named
staircase tokens at **full opacity, never percentages**: major ticks
day `#252323` / night `#9C8E84`; medium day `#5F5856` / night
`#776A60`; minor one step above the disc (day `#E4DFDA` / night
`#171412`); rim numerals day `#5F5856` / night `#9C8E84`; reader dot
day ink / night `#EDE3D8`. Register: none. Note the superseded
rival a builder will meet first: DESIGNERS §3.4's "ticks & numerals
ink/text at **35/85/55%**" — D9 kills the percentage form; the
register should name it dead alongside 0.80/0.82.

**H11 — Knurl A/B tooth alternation (DESIGNERS §3.2; mara.md).**
Day `#FAF6F0` (250/246/240) / `#CFC7C0` (207/199/192); night
`#4A423C` (74/66/60) / `#241E1A` (36/30/26). 120 teeth, 3.000°
pitch. Unregistered — and it is the substrate the Broken Flare (A3)
multiplies, so A3 is unbuildable without it.

**H12 — AOD concrete tones (nacht.md §1.3; DESIGNERS §6.2).** The
register's luminance stages (A5) are *ratios*; Nacht files the
actual hexes a builder sets:
- Stage 0 (0–20m): REMAINING `#8A7E71` (138/126/113, lum 0.215,
  5.3:1 on black) · ELAPSED `#6E6359` (110/99/89, 0.130, 3.6:1) ·
  labels `#4E463F` (78/70/63, 0.064)
- Stage 1 (20–50m): REMAINING `#7D7268` (125/114/104, 0.174) ·
  ELAPSED `#5C5348` (92/83/72, 0.089) · labels gone
- Stage 2 (50m+): REMAINING `#6E6359` (0.130) · ELAPSED `#554C43`
  (85/76/67, 0.075) · labels gone · bar-track alpha halves
- Colon at **45%** of the numeral tone, never blinks; progress-bar
  track = label tone at **35% alpha** over black, fill = elapsed tone
- Ember dot Ø6 `#7A3410` (122/52/16, lum 0.066) — never dims, ever
- Finish (exactly one 60s step): all tones up to Stage 0, dot to
  `#C74814` (199/72/20, lum 0.168 — under the 0.25 ceiling)
- AOD ground true `#000000`; wake = 2.5s brightness ramp from the
  AOD floor, no day-flash (DESIGNERS §6.2)
Cross-check that passes: ELAPSED Stage 0 `#6E6359` = REMAINING
Stage 2 `#6E6359` — the staircase reuses its own steps, as filed.

**H13 — Slide + Deck print alphas (MASTER §2.5–2.6; BEYOND D4/D8;
DESIGNERS §3.3–3.4).**
- D4 notches: fill `#000000` **at 35%** over the track tone, both modes
- D8 recess skirt: `#000000` **at 25% day / 35% night**
- `SLIDE TO ARM` track word: **55% tone** (Sora 700 15sp, +0.5)
- NEXT line: `#9C8E84` (Anonymous Pro 700 15sp) — the night text-2
  tier, named in MASTER §2.5, unregistered
- Dead permission: slide cap → `#4A423C`, inert (MASTER §2.6, W1)
- Primed frame: hub tetrad wells filled `#D2C6BB` (MASTER §2.6, W1)
- Hero strike alphas (MASTER §2.3): RE-SET outgoing dims to the
  **55% register** for 50ms; strike density **86→100%** over 120ms;
  squeeze pass **12% alpha**, dead by 90ms
- **CONTESTED — the cap marker line's colour.** Three rivals, no
  register entry, and my own §1.3 map is silent on it (my gap,
  owned): vau.md (Designers §3.3 source) — day `#FAF6F0` at 90% /
  night **ink `#252323` at 90%**; mara.md Beyond 2 — cream `#FFE4CF`
  at 90%, "nothing else"; joss.md — glyph tone (`#EDE3D8` night).
  On the night cap `#F47442` (244/116/66) these are three different
  contrasts. Sana reconciles; I flag, I do not pick in the register's
  name. (My map's logic — ink is day-only, cream is text/glyphs —
  favours cream or glyph; that is an argument for the reconciliation
  letter, not a ruling.)

**H14 — Shield / sled / chrome colour constants (MASTER §3;
DESIGNERS §1.3 gaps).**
- Night sled fill staircase, continuous RGB blend on animated
  position: SHUT `#4A423C` → PEEK `#362C27` → OPEN hollow recess
  (MASTER §3.7); PEEK cap + hairline night `#362C27`; OPEN night
  hollow recess + `#9C8E84` edge (MASTER §3.7)
- Loadout marker = literal `#FBF1EC` (251/241/236) **both modes** —
  "the token drifts; the constant doesn't" (MASTER §3.6)
- Search field line pair: **0x2E alpha** black/white = 46/255 ≈
  **18.0%**, inset 12dp (MASTER §3.7)
- BLOCKBAR day `#2E2A26` (46/42/38) / night `#161311` (22/19/17)
  (iris.md §0); Nacht N2: night block bar is lawful only seated on
  raised (1.36:1) or sheet (1.88:1) (DESIGNERS §1.3)
- Nacht's gap arithmetic (DESIGNERS §1.3): N1 night recess↔ground
  **1.05:1** — every recess wears the catch hairline or is invisible;
  N5 **muted banned on all night raised surfaces (2.60:1)**; night
  text ratios 10.95:1 / 5.78:1 / 3.51:1; day muted `#5F5856`
  verified 5.26 ground / 4.61 recess / 6.47 raised
- Hairline token (iris.md §0): `#615B5A` day / `#8F8888` night at
  alpha byte **0x75** — **notation trap:** 0x75 = 117, 117/255 =
  **45.9%**, not 75%. My own map writes "@75%" (meaning the byte);
  a builder reading percent ships a hairline 1.6× too strong. The
  register must file it as "α byte 0x75 ≈ 46%" or not at all.
- Scrim borrow (iris.md G1): overlays use GROUND at **~80% alpha**,
  both modes — no scrim token exists. Unregistered anywhere.

**H15 — The shipped green (warning, not a plan constant).**
Shipped code carries `#758F88` as night outlineVariant: R117
**G143** B136 — the only G-dominant value in scope, G−R = +26,
G−B = +7 (Cato-II, Critics invention wave, channel arithmetic
theirs and re-verified here). Replacement nominated there:
`#8F8375` (143/131/117, R≥G≥B, warm-ordered). If the register is
the single home of load-bearing constants, the kill + replacement
belong in it — otherwise the register governs the plan while the
code keeps the one hex the whole system exists to exclude.

---

## Appendix — channel arithmetic (Hale's rule)

Every hex cited above, R/G/B decimal. All locked-system values are
R≥G≥B (warm-ordered); none is G-dominant; none is green.

Night: #171412 23/20/18 · #100E0C 16/14/12 · #362C27 54/44/39 ·
#4A423C 74/66/60 · #D2C6BB 210/198/187 · #9C8E84 156/142/132 ·
#776A60 119/106/96 · #EDE3D8 237/227/216 · #FFE4CF 255/228/207 ·
#F47442 244/116/66 · #161311 22/19/17 · #241E1A 36/30/26
Day: #E4DFDA 228/223/218 · #D8D1CB 216/209/203 · #FAF6F0
250/246/240 · #CFC7C0 207/199/192 · #252323 37/35/35 · #2C2A29
44/42/41 · #5F5856 95/88/86 · #615B5A 97/91/90 · #F55314 245/83/20 ·
#2E2A26 46/42/38
Shared: #471602 71/22/2 · #FBF1EC 251/241/236 · #000000 0/0/0
AOD: #8A7E71 138/126/113 · #7D7268 125/114/104 · #6E6359 110/99/89 ·
#5C5348 92/83/72 · #554C43 85/76/67 · #4E463F 78/70/63 · #7A3410
122/52/16 · #C74814 199/72/20
Denied: #FF8A5C 255/138/92 (lum 0.402 > 0.25 AOD ceiling)
Shipped-code violation: #758F88 117/**143**/136 — G-dominant, the
sole exception in scope; nominated replacement #8F8375 143/131/117.

*— Iris, Designers, colour. No code touched. Sana reconciles the
contested items (A2's stale rivals, A4's 88%, A5's provenance,
H13's marker line) — this file forks nothing.*
