# REGISTER AUDIT — GEOMETRY + ROOM (Raster, Designers — spacing/geometry)

*Audit of plan/REGISTER.md (Sana's draft) against DESIGNERS-PLAN §1.2 +
§§3–8 (raster.md / uhr.md / razor.md normative parts), MASTER-PLAN §0 +
§§2–3, and BEYOND-deck (D5, W1, W2, W4 room debt, W5). §0 law checked:
margin 20dp frozen, radius 8dp keys / 16dp panels frozen — the register
carries both correctly. Verdicts below: CONFIRMED = one value, traceable
to a governing source; CONTESTED = two live values in governing sources,
rival named — I do not pick; Sana reconciles.*

---

## A. CONFIRMED / CONTESTED TABLE — every geometry value in the register

### A1. The slide (geometry-bearing lines only)

| Register value | Verdict | Source / rival |
|---|---|---|
| GATE 0.68 · CATCH/COMMIT 0.84 · DAMPED 0.68–0.84 | CONFIRMED | MASTER §2.6 (Fen: FREE 0–0.68 · DAMPED 0.68–0.84 · TAKEN 0.84–1.00; commit = displayed crossing 0.84 under contact); BEYOND W4 same. Supersedes DESIGNERS §3.3 + §11-R5 (warn 0.68 · catch 0.80 · commit 0.82) — register's superseded list is accurate. |
| OFF-STOP 0.65 (gate −0.03, refusal only) | CONFIRMED | MASTER §2.6 interlock (BLOCK returns OFF → cap stops at gate −0.03 = 0.65, pre-latch). Not the dead 0.65-as-commit (DESIGNERS §11-R5 loser, Vau) — register keeps the two apart correctly. |
| TRAVEL 180dp | CONFIRMED | MASTER §2.6 ("over 180dp of travel ≈28.6mm"). Not Raster's wave-1 derived travel 308 (raster.md §3.1, slide 64 high / thumb 48) — that whole slide geometry is superseded; see missed list M6. |
| BRUSH FLOOR 90ms · TAP MESH 140ms→0.92 + 110ms assist · SEAT SPRING (400/16) 110ms · LATCH KNOCK 14ms @ 0.85 at 0.97 | CONFIRMED | MASTER §2.6 tap mechanism + drag (Cato's spring (400/16), 110ms; knock 14ms @ 0.85 at 0.97 of travel; brush <90ms commits nothing; mesh 140ms bezier to 0.92). |

### A2. The wheel and hub — **the register's wheel line is the contested core of this audit**

| Register value | Verdict | Source / rival |
|---|---|---|
| WHEEL Ø288dp (R144) | CONFIRMED | DESIGNERS §3.2 + §11-R3 (Vau's 312 dead); MASTER §2.4; BEYOND F-ROOM (Ø272 starves the stack, Ø304 starves the reserve). |
| Radial stack hub 0–64 · gap 64–72 · face 72–114 · tick band 114–130 · clearance 130–136 · knurl 136–144 (cited in my brief as §2.4-as-filed) | CONFIRMED — but NOT IN THE REGISTER (see M2) | MASTER §2.4 and DESIGNERS §3.2, identical, sums exactly to R144. The register omits the stack it should carry. |
| SCALE 9° = 5 min | **CONTESTED** | Register's value: BEYOND W2 ("numeric scale untouched (9° = 5 min)") + MASTER §2.4 Strait paragraph. **Rival: 10.000° steps** — DESIGNERS §3.2 ("Uhr's geometry is normative": 36 detents, 10.000° steps), uhr.md §§ (step angle exactly 10.000°, λ(180) = −350°), and MASTER §2.4's own wheel paragraph ("10.000° steps, clockwise 0→+350°"). MASTER §2.4 carries both scales in adjacent paragraphs. A builder cannot build both. |
| STOPS: FLOW + 10–180 in 5-min steps | **CONTESTED** (first numeric stop) | Register/BEYOND W2/MASTER Strait: FLOW↔**10** (the Strait is FLOW↔10 widened to 18°). **Rival: values 5–180** — DESIGNERS §3.2 + MASTER §2.4 wheel paragraph + uhr.md (λ(5) = 0°; end-stop ticks at 5/180 with 4×4 feet). If 5 exists, the Strait's landing step is 5, not 10, and W2's geometry re-opens. |
| STRAIT 18° | CONFIRMED (as a width) | BEYOND W2 + MASTER §2.4 (FLOW↔10 widened to 18°, landing step + phantom step, no tick/tooth/numeral inside). Its endpoints inherit the 10-vs-5 contest above. |
| DEAD SECTOR 36° | **CONTESTED** (naming collision) | Register's value: BEYOND W2 + MASTER §2.4 Strait ("dead sector 45°→36°"). **Rival object: the 10° dead wedge** — DESIGNERS §3.2 + MASTER §2.4 wheel paragraph + uhr.md §9 (sector between the 180 and 5 ticks, one anvil hairline). Are "dead sector 36°" and "dead wedge 10°" two objects or one object with two values? No source says. If one: 36 vs 10 is a straight fight. If two: the register is missing the wedge (M3). Either way it cannot stand as filed. |
| TRAVEL FLOW→180: 324° | **CONTESTED** (follows the scale) | 324° is arithmetically exact under 9° (18° strait + 34 × 9° = 324°). **Rival: 350°** under Uhr's 10° scale (DESIGNERS §3.2 "clockwise travel 0→+350°"; uhr.md total travel 0°→+350°). One of {9°, 324°} / {10°, 350°} dies in reconciliation; the pair must die together. |
| STRAIT GATE: <180°/s falls back · ≥180°/s crosses paying Δω = −90°/s · held finger 1:1 | CONFIRMED | BEYOND W2 + MASTER §2.4, verbatim agreement. |
| FLOW WELL ±6° capture, seat ≤520ms | CONFIRMED | BEYOND W2 + MASTER §2.4. |
| LOADED WHEEL I_rel(n) = 1 + 0.14·(n−1) · τ 0.34→0.48s · two overshoots · fling ≤1,400ms · seat knock 16ms @ 0.85 → 24ms @ 1.0 · FLOW load zero | CONFIRMED | BEYOND W2 + MASTER §2.4, verbatim agreement. (τ endpoints are the n=1/n=8 evaluations of τ(n) = 0.34·√I_rel — register's formula line is consistent.) |
| HUB Ø128 | CONFIRMED | DESIGNERS §3.2 (Ø128, raised, recess well) + MASTER §2.4 + BEYOND W2 ("Hub Ø128 kept"). Radius 64 = the stack's hub band 0–64 — internally consistent. |
| WELLS: 8, Ø10dp, on R40 | CONFIRMED-BY-PARTS (composite — flagged, not contested) | Count 8 + R40 ring: DESIGNERS §3.2 (8 pip positions Ø10 on R40) and BEYOND W2/MASTER §2.4 (tetrad wells, closure bar R40). Ø10 diameter: only DESIGNERS §3.2 states it, and it states it for the *pips* the tetrad form killed. No single source states "tetrad wells Ø10". Probably right; Sana should get Vega/Uhr to say it in one place rather than let the register weld two sources silently. |
| COUNT CAP 8 | CONFIRMED | BEYOND W2 + MASTER §2.4 (tetrad arithmetic: a third tetrad seals 590–710 min; engine coercion 1..4 → 8 is filed build debt in both). |
| CLOSURE BAR: R40, 10dp wide, 160ms ink-settle from 88% | CONFIRMED (bar) / PARTIAL (the "from 88%") | Bar R40 · 10dp wide · 160ms ink-settle: BEYOND W2 + MASTER §2.4 verbatim. The "from 88%" onset appears in neither — register carries a detail its cited sources don't. Sana: source it or strike it. |
| SPECULAR: swept form, 720-entry LUTs + prefix sums, sinc p = 3° | CONFIRMED (core) | BEYOND W2 + MASTER §2.4 (`specularSwept(θ₀,θ₁)`, 720-entry LUTs, prefix sums, \|sin(πΔ/p)/(πΔ/p)\|, p = 3°). |
| SPECULAR: "sinc replaces dual sub-draws" (Dario's amendments) | **CONTESTED** | Register kills the dual sub-draws. **Rival: MASTER §2.4 retains them** — "dual sub-draws at \|Δ\| ≥ 1.5°, tooth contrast attenuated by sinc" (BEYOND W2 same). The register's Dario amendment is not in MASTER §§2–3 or BEYOND as filed. Geometry-adjacent (it decides what is drawn per frame), so I flag it rather than pass it. |
| BROKEN FLARE: variance 0.84–1.16, compile-time only, teeth 17/83 at 0.62/0.68, no runtime writer | CONFIRMED | BEYOND W2 + MASTER §2.4, verbatim agreement. |

### A3. The valve

| Register value | Verdict | Source / rival |
|---|---|---|
| BAR: full-width × 112dp | CONFIRMED | DESIGNERS §7.2 (Floor's spec wins whole: full-width 112dp touch field; the 68dp bar and every sub-88dp ancestor dead) + §10 floors ("the emergency hold target is 112dp"). MASTER §0.1-8 (Barriere verbatim) governs the numbers around it. |
| Touch floor for the valve ≥88dp (bar exceeds it) | CONFIRMED | MASTER §2.6 tail (Critics touch floors: valve ≥88dp) + §0/§2 passim. |
| BANKS 15/30/45 · FULL HOLD 60.0s · DRAIN 800ms → 1.0× 3.0s → 2.0× | Not geometry — noted only: DESIGNERS §7.2 as filed still carries the flat 2:1 drain the register supersedes; register itself names that supersession. No geometry conflict. | — |

### A4. Chrome and room — **second contested core**

| Register value | Verdict | Source / rival |
|---|---|---|
| BAND: DECK · SHIELD · TASKS · LEDGER + PARK (4 stops + utility end-detent) | CONFIRMED (composition) | MASTER §0, his verdicts (Ledger KEEP THE TAB 05:41; Guard DISSOLVED 07:39: "the band is DECK · SHIELD · TASKS · LEDGER + PARK"). Supersedes DESIGNERS §8.3's operative line (V-DEMOTE 3 stops), which predates the keep verdict. |
| PARK: 44dp zone + 4dp notch · flat 48dp cost under every variant | CONFIRMED | DESIGNERS §8.3 PARK paragraph + razor.md §3.1 (reserved 44dp + 4dp notch; usable track 342 − 44 − 4 = 294dp) + razor §4 ("PARK costs a flat 48dp of track regardless of variant"). |
| PARK: arrival IS invocation · two rest heights | CONFIRMED | DESIGNERS §8.3 (arrival IS drawer invocation, Mikro; "the drawer has exactly two rest heights"). |
| **Operative geometry = V-TAB figures: segment 85.5dp / cap 77.5dp, "priced for exactly this branch"** | **CONTESTED — the register's sentence is wrong as written** | 85.5/77.5 is Razor's V-TAB geometry **without PARK** (razor.md §1.1: "Geometry (no PARK): segment = 342 ÷ 4 = 85.5dp; cap 77.5dp"; §4 matrix, V-TAB × G-KEPT cell). The register's own band is V-TAB **with** PARK (G-DISSOLVED), and for that cell every source that prices it gives **segment 73.5dp / cap 65.5dp**: razor.md §1.1 ("with PARK, G-DISSOLVED: segment 73.5dp"), §3.1 (294 ÷ 4 = 73.5, cap 65.5), §4 matrix (V-TAB × G-DISSOLVED: "segment 73.5dp, cap 65.5dp, end-to-end 220.5dp + 48dp to PARK"). DESIGNERS §8.3's variant line repeats 85.5/77.5 for V-TAB without applying its own flat-48 PARK cost two paragraphs earlier — the register inherited the slip. Sana: either the operative pair becomes 73.5/65.5, or the register must state that PARK's 48dp comes out of something other than the stops track — no source states that. |
| GUARD LINE `GUARD 4/4` in chrome | CONFIRMED (not geometry) | MASTER §0 Guard verdict + DESIGNERS §8.5 G-DISSOLVED (pip + `GUARD n/4`, header right end). |
| ROOM: fixed sum 796dp · reserve = H − 796 · floor 20dp · under H=820 return to coordinators · wheel never shrinks, strip never folds | CONFIRMED (headline) — with an arithmetic caveat, next row | BEYOND W4 room debt + MASTER §2.2, verbatim agreement (48dp at H=844, 100dp at H=896). |
| ROOM: the stack that sums to 796 | **CONTESTED (decomposition)** | No filed stack sums to 796 without choosing sides in two other fights: (i) MASTER §2.2 charges the room debt as readout→wheel S32→**S20** (−12), but W1's coordinates (readout y112–200 · NEXT y208–228 · wheel y260–548, centre (206, 404)) require the **S32** gap — centre y = 228 + gap + 144 = 404 only at gap 32; at S20 the centre falls to (206, 392) and §2.2's own stated centre contradicts its own charge. (ii) The tail sums to 796 with rail **52** (DESIGNERS §8.3) + S32, or with rail **64** (raster.md §3.6) + S20 — not both, and the register registers neither rail height (M9). The headline 796 stands in two sources; its decomposition does not. Sana/Kato: publish the one stack that sums to it. (A later invention-wave challenge — Spatz/Hale: repeal the "pays down to 20" clause, fence frozen 844 — is a proposal, not law; the register correctly ignores it, but it is aimed at exactly this clause and should be answered, not discovered by a builder.) |
| RADIUS 8dp keys / 16dp panels (frozen) | CONFIRMED | §0 Ren's craft rulings ("Radius frozen 8dp keys / 16dp panels") + DESIGNERS §11-R23. Register should note R23's one named exception, which MASTER §3.7 spends: the sled track radius is slot geometry (DESIGNERS §4.4 track radius 17 slot; MASTER §3.7 Yuki track radius 12) — the freeze as registered reads absolute and will false-positive the sled. |
| MARGIN 20dp (frozen) | CONFIRMED | §0 + DESIGNERS §1.2 + raster.md §1.1 (20 both sides, no nesting exception; content 372 = 4 × 84 on S12). |
| TOUCH FLOORS: 48dp everywhere; valve ≥88dp | CONFIRMED | DESIGNERS §10 (nothing touchable under 48 in its TOUCH area) + MASTER §2.6 (chip floor ≥48; valve ≥88). Note DESIGNERS §8.3/R17: the nav cap is 44dp *visual* under a segment × rail touch target — the register's "48 everywhere" is a touch-area law, and Sana should say so in the register or the cap reads as a violation. |

### A5. AOD (geometry-bearing lines)

| Register value | Verdict | Source / rival |
|---|---|---|
| AOD REMAINING `H:MM` at 104sp | CONFIRMED | DESIGNERS §6.2 (Sora 700 104sp, hero always; §11-R1: 104 is AOD-only) + §11-R21 (H:MM on AOD, M:SS on running, by surface). |
| Patrol 4×3 anchors | CONFIRMED (grid) — patrol *distances* missing from register (M11) | DESIGNERS §6.2 (4×3 anchor grid, ±36dp × −64/0/+64dp). |
| Dimming 0.215 → 0.174 → 0.130, ceiling 0.25 | CONFIRMED | DESIGNERS §6.2, verbatim (Stages 0/1/2 at 0–20 / 20–50 / 50+ min). |

---

## B. MISSED CONSTANTS — load-bearing geometry in the sources, absent from the register

Each with value + governing source. Grouped by surface.

**The grid itself (§1.2 system — the register registers the margin and
nothing else of it):**
- M1. **Content width 372dp · columns 4 × 84dp on S12 gutters** (spans
  84/180/276/372; column boxes [20–104][116–200][212–296][308–392]) —
  DESIGNERS §1.2; raster.md §1.1. Every width in the app derives from
  this; it is more load-bearing than half the register.
- M2. **Axes: A=20 · B=40 (all panel interiors) · N=60 · time axis
  C=20–76 (56dp) · title axis T=96 · centre O=206 · sled zone 296–392 ·
  D=392** — DESIGNERS §1.2; raster.md §1.2.
- M3. **Start beat: header 48 (hairline at y48) + S32 + header L20 +
  S12 → first content y=112 on every screen** (start complex 112dp) —
  DESIGNERS §1.2; raster.md §1.3. MASTER §2.2/W1 assume it (readout
  y112–200) without the register carrying it.
- M4. **Row pitches P64 (S12·Z40·S12) and P48 · group header H32 ·
  section cadence S32+L20+S12 = 64** — DESIGNERS §1.2; raster.md §2.
- M5. **Gap tokens {4, 8, 12, 20, 32, 48} + the closed list of 27
  permitted sizes; any other dp literal is a build error** — DESIGNERS
  §1.2; raster.md §§0/4 (the grep acceptance test lives there).

**Deck / wheel / slide:**
- M6. **Slide track: full content width × 72dp, radius 16** —
  DESIGNERS §3.3; MASTER §2.2 stack (slide 72). (Raster's wave-1 slide
  64 high / thumb 48×48 / travel 308 is dead — superseded by the 72dp
  track + 180dp travel; the register should bury it by name, because
  raster.md still prints it.)
- M7. **Slide cap: 84 × 56dp, radius 8, inset 8dp** — DESIGNERS §3.3;
  MASTER §2.6 ("The cap (84×56, radius 8, the accent) is unchanged").
  Load-bearing: cap-travel = track width − 16dp insets − 84dp cap is
  the denominator of every 0.68/0.84 coordinate the register *does*
  carry (MASTER §2.6, D4 notch placement). Registering the fractions
  without the cap that defines them is registering a ratio without its
  ruler.
- M8. **D4 notches: 8dp long × 2dp deep, top + bottom inner walls,
  leading edge at track-left + 0.84 × cap-travel** — MASTER §2.6.
  **Marker line 3×28dp, thickening 3 → 4 (grip) → 5dp (at the
  catch)** — DESIGNERS §3.3. **Track recess skirt 2dp** (D8) — MASTER
  §2.6. **Refusal slot: 22dp high, 8dp below the track, reserved
  always** — MASTER §2.6.
- M9. **The standing strip: 56dp, 3 cells, 8dp above the slide track —
  64dp total room cost** — MASTER §2.6; BEYOND W4 (the room debt
  itself: "the standing strip costs 64dp"). The register carries the
  debt's *sum* (796) but not the *item* (64) that caused it.
- M10. **Wheel print geometry: rim numerals at R100 (majors only);
  reader dot Ø4 at R133; ticks minor 8×1.0 / medium 12×1.5 / major
  16×2.0, outer ends on R130; end-stop feet 4×4dp crossing R114;
  over-rotation cap +4°; settle tolerance ±0.5°; rim width 30dp** —
  DESIGNERS §3.2; MASTER §2.4. Plus the **10° dead wedge + anvil
  hairline** (uhr.md §9) if it survives the A2 naming fight.
- M11. **Knurl: 120 teeth, 3.000° pitch, teeth 8.0 × 2.4dp** —
  DESIGNERS §3.2. (The register carries the knurl's light law but not
  its tooth geometry; p = 3° in the specular line *is* this pitch —
  say so.)
- M12. **Hub press geometry: DOWN = +1 tonal step + 1dp depress;
  specular core at 315° (core ±9°, cos² to ±34°, peak lift 10% day /
  8% night), translating 2dp toward 135° on press; tetrad printed gap
  12°; disc chamfer 2dp arc at R133, 250°→350°** — MASTER §2.4;
  BEYOND D2/D6. The 1dp/2dp pair is the hub's only motion geometry.

**Shield:**
- M13. **Yuki sled (operative, MASTER §3.7): track 168 × 36dp, radius
  12, pad 3, slot 54, cap 54 × 30 r8, row 52dp; touch rect = track
  +12dp left × full row height; margin readout word zone fixed 44dp**
  — MASTER §3.7, which explicitly overrules DESIGNERS §4.4 (track
  34 / cap 36×30 / travel 72) *and* raster.md §4's standard sled
  96×32 / mini-sled 56×32. Three sled geometries live in the sources;
  the register carries none, so a builder grepping the register finds
  no sled at all and will find a wrong one in the filed plan.
- M14. **Routine mini-sled 56 × 32** (Raster §3.2 Shield map, in
  P64 Z40) vs MASTER §3.4's Sten mini-sled under Yuki's throw contract
  (no dimensions stated in §3.4) — register neither; at minimum the
  56×32 needs a home or a death certificate.
- M15. **Section strip: 28dp total (20dp bar field + 8dp ticks),
  now-line 1dp × bar height + 4dp tick** — DESIGNERS §4.3; MASTER
  §3.5. **Loadout band: height 80dp** (S12 · L16 · S8 · 32 track ·
  S12; cap middle stop on O) — raster.md §3.2. **Shield app row 52dp,
  icon 28dp, group headers 28dp** — DESIGNERS §4.4 (note: 52dp row vs
  Raster's P64 app rows in raster.md §3.2 — a live pitch fight the
  register could have caught: MASTER §3.7 settles row = 52dp).

**Chrome:**
- M16. **Rail height + travelling cap height — MISSED AND CONTESTED.**
  Rail **52dp / cap 44dp** (DESIGNERS §8.3; §11-R17 "segment × 52dp
  rail is the target") vs rail **64dp / cap zone 48dp** (raster.md
  §3.6; DESIGNERS §8.4 "the rail's 64dp block swaps content"; Kato's
  merged room: rail y780–844 at H=844). The register registers no rail
  height at all, and the room decomposition (A4) cannot close until
  this is settled. Also: **cap travel 180ms + 60ms per stop crossed**
  (DESIGNERS §8.3) and **PARK resistance in the last 12dp; cap drags
  48dp past the last stop; 1dp score line** (DESIGNERS §8.3; razor.md
  §3.1).
- M17. **Operative pre-verdict figures a builder will still meet:
  3-stop segment 98dp / cap 90dp (V-DEMOTE × G-DISSOLVED, razor §3.1 +
  §4b addendum; DESIGNERS §8.3 operative line)** — dead by his keep
  verdict, but they are printed as OPERATIVE in a governing file; the
  register's superseded list should bury them beside ≈2.5s and 0.82,
  or the next reader re-derives the wrong rail.

**Running / AOD:**
- M18. **Stepped arc: Ø280dp, 270° sweep, 10dp stroke, bottom 90° gap**
  — DESIGNERS §6.1. (Kato's invention wave re-bases it to Ø288 on the
  datum (206, 404) — proposal, not law; the register should carry
  Ø280 and name the proposal as pending, exactly as it does not carry
  Spatz's room challenge.)
- M19. **Valve rest slot: recessed 200 × 48dp** nested in the arc's
  gap — DESIGNERS §6.1 (distinct from the 112dp overlay bar the
  register does carry; both are load-bearing, on different surfaces).
  Plus the overlay bar's **12dp drift tolerance** — DESIGNERS §7.2.
- M20. **AOD patrol distances: ±36dp × −64/0/+64dp; ≤1 grid-step per
  minute; all 12 anchors before any repeat; ≥24dp from edges** —
  DESIGNERS §6.2 (anchors x ∈ {170, 206, 242}, y ∈ {340, 404, 468}
  per Kato's datum survey). The register's "patrol 4×3 anchors"
  without the distances is a grid with no size. Also missed: **AOD
  progress bar 120 × 3dp · armed dot Ø6dp · ELAPSED at 50sp** —
  DESIGNERS §6.2.

---

## C. Summary for reconciliation

Confirmed clean: slide coordinates (0.68 / 0.84 / 0.65 / 180dp), hub
Ø128 + cap 8 + closure bar, valve 112 / ≥88, margin 20, radius 8/16,
room headline 796 / H−796 / floor 20 / under-820, PARK 44+4 = flat 48,
AOD 104sp + dimming ladder.

Contested, in the order a builder hits them:
1. **Wheel scale pair** — 9°/324°/first-stop-10/dead-sector-36°
   (BEYOND W2, register) vs 10°/350°/first-stop-5/dead-wedge-10°
   (Uhr normative, DESIGNERS §3.2, MASTER §2.4's own wheel paragraph).
2. **V-TAB operative pair** — 85.5/77.5 (no PARK) filed as the
   with-PARK branch's geometry; rival 73.5/65.5 (razor, thrice).
3. **Room decomposition** — 796 reachable only via (S20 + rail 64) or
   (S32 + rail 52); MASTER §2.2 asserts S20 *and* centre (206, 404),
   which cannot both hold.
4. **Rail height** — 52 vs 64 (feeds contest 3).
5. **Specular dual sub-draws** — killed by the register's Dario
   amendment, retained by MASTER §2.4/BEYOND W2.
6. **Closure-bar "from 88%"** — registered, unsourced in §§2–3/BEYOND.

Missed: 20 constants/groups (M1–M20), of which M1–M5 (the grid
itself), M7 (the cap that defines the slide fractions), M9 (the strip
that caused the room debt) and M13 (the operative sled) are the ones
that will bite first.

*— Raster, Designers (spacing/geometry). Inbox was empty on seating
(no unread letters; prior mail already in read/). No code touched.*
